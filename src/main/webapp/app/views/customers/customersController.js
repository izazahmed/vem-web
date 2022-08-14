app.controller("customersListController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr) {

    /**
     * This method is for fetching customers list
     * 
     * @param customer
     *            state
     * 
     */
	
	$scope.isDataAvailable = true;
	
    ApiFactory.getApiData({
        serviceName: "customersList",
        onSuccess: function (data) {
        	
        	if (data.data && data.data.length === 0) {
                $scope.isDataAvailable = false;
                return;
            }
        	
            $scope.customers = data.data;
            $("#nodata").css({
                opacity: 1
            });
        },
        onFailure: function () {}
    });
    
    // To sort customers
    $scope.customerSorter = function (a){
    	   return normalizeMixedDataValue(a.companyName);
    	}
    function normalizeMixedDataValue( value ) {
        var padding = "000000000000000";
        value = value.replace(
            /(\d+)((\.\d+)+)?/g,
            function( $0, integer, decimal, $3 ) {
                if ( decimal !== $3 ) {
                    return(
                        padding.slice( integer.length ) +
                        integer +
                        decimal
                    );
                }
                decimal = ( decimal || ".0" );
                return(
                    padding.slice( integer.length ) +
                    integer +
                    decimal +
                    padding.slice( decimal.length )
                );
            }
        );
        return( value );
    }
    
    
    
    $scope.changeCustomerStatus = function (customer, event) {
        /**
         * This method is for activation and deactivation of an customer
         * 
         * @param customerId,customer
         *            event object
         * 
         */
        if ($(event.target).prop("checked")) {
            $(event.target).prop("checked", false);
        } else {
            $(event.target).prop("checked", true);
        }
        var me = event.target;
        $('#modelDialog').on('show.bs.modal', function (event) {
            var modal = $(this);
            if ($(me).prop("checked")) {
                modal.find('.modal-title').text('Deactivate customer');
                modal.find('.model-content').text('Are you sure you want to deactivate the customer?');
                return;
            } else {
                modal.find('.modal-title').text('Activate customer');
                modal.find('.model-content').text('Are you sure you want to activate the customer?');
                return;
            }
        });
        $("#modelDialog").find('.confirm')[0].targetElement = {
            me: me,
            obj: customer,
        };
        $("#modelDialog").modal("show");
    }
    $rootScope.accept = function () {
        /**
         * Assign event handler for when user click on ok in the model window
         */
        var btn = $("#modelDialog").find('.confirm')[0].targetElement;
        var status = $(btn.me).prop("checked") ? 0 : 1;

        ApiFactory.getApiData({
            serviceName: "customerstatus",
            data: {
                "status": status,
                "customerId": btn.obj.customerId
            },
            onSuccess: function (data) {
                $(btn.me).prop("checked", status);
                $("#modelDialog").modal("hide");
                if ($(btn.me).prop("checked") == true) {
                    $(btn.me).closest("li").removeClass("disabled");
                } else {
                    $(btn.me).closest("li").addClass("disabled");
                }
                toastr.success(messageFactory.getMessage(data.code));
            },
            onFailure: function () {}
        });
    };
    $scope.getSourcePageId = function (customerId) {

        if ($state.current.name.indexOf("customers.customersList") != -1 && $state.params.sourcePage) {

            return $state.params.sourcePage.split("-")[0] + "-" + customerId;

        } else {
            return $state.params.sourcePage;
        }
    }

    $scope.getCustomerProfile = function (customerId) {
        var customerId = customerId;
        var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + customerId;


        if ($state.current.name.indexOf("customers.customersList") != -1) {

            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
                $state.go("customers.customersList.getCustomer.groups", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                $state.go("customers.customersList.getCustomer.sites", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                $state.go("customers.customersList.getCustomer.devices", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("customers.customersList.getCustomer.schedule", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                $state.go("customers.customersList.getCustomer.users", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else {
                $state.go("customers.customersList.getCustomer.activityLog", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            }

            return;

        }




        if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
            $state.go("customers.customersList.getCustomer.groups", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
            $state.go("customers.customersList.getCustomer.sites", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
            $state.go("customers.customersList.getCustomer.devices", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
            $state.go("customers.customersList.getCustomer.schedule", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
            $state.go("customers.customersList.getCustomer.users", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else {
            $state.go("customers.customersList.getCustomer.activityLog", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        }

    };
}).controller("customerProfile", function ($scope, $rootScope, ApiFactory, messageFactory, $state) {
	$rootScope.customeTabCounts = {};
    $rootScope.addedUsersData = [];
    $rootScope.addedSiteData = [];
    $scope.prefrencesOptionsArray = [];

    
    
    $scope.gotoGroups = function(){
        
        $state.go("customers.customersList.getCustomer.groups",$state.params);
    }

    $scope.gotoSites = function(){
        
        $state.go("customers.customersList.getCustomer.sites",$state.params);
    }
    
    $scope.gotoDevices = function(){
        
        $state.go("customers.customersList.getCustomer.devices",$state.params);
    }
    
    $scope.gotoSchedules = function(){
        
        $state.go("customers.customersList.getCustomer.schedule",$state.params);
    }
    
    $scope.gotoUsers = function(){
        
        $state.go("customers.customersList.getCustomer.users",$state.params);
    }
    
    $scope.gotoAlerts = function(){
        
        $state.go("customers.customersList.getCustomer.alerts",$state.params);
    }
    
    $scope.gotoActivityLog = function(){
        
        $state.go("customers.customersList.getCustomer.activityLog",$state.params);
    }
    
    
    
    
    ApiFactory.getApiData({
        serviceName: "customerProfile",
        data: {
            customerId: $state.params.customerId
        },
        onSuccess: function (data) {
            $scope.customer = data.data[0];
            $rootScope.customeTabCounts = {
                groupCount: $scope.customer.groups,
                siteCount: $scope.customer.locations,
                deviceCount: $scope.customer.devices,
                scheduleCount: $scope.customer.schedule,
                alertsCount: $scope.customer.alerts,
                userCount: $scope.customer.users,
                activityLogCount: $scope.customer.activityLog
            }

            if ($scope.customer.fanOn === 'Yes') {
                $scope.prefrencesOptionsArray.push("Fan On");
            }
            if ($scope.customer.fanAuto === 'Yes') {
                $scope.prefrencesOptionsArray.push("Fan Auto");
            }
            if ($scope.customer.resetHold === 'Yes') {
                $scope.prefrencesOptionsArray.push("Reset Hold to Off");
            }
            if ($scope.customer.havcAuto === 'Yes') {
                $scope.prefrencesOptionsArray.push("Reset HVAC to Auto");
            }
            if ($scope.customer.nightSchedule === 'Yes') {
                $scope.prefrencesOptionsArray.push("Nightly Schedule Download");
            }
        },
        onFailure: function () {}
    });

    // function to checking permissions of customer module
    $scope.customerModuleReadWriteCheck = function () {
        if (!eaiDetails.isSuper)
            if (eaiDetails.rolePermissions.hasOwnProperty("Customer Management"))
                if (eaiDetails.rolePermissions["Customer Management"] == 2) // 2 means read - write 
                    return true;
                else return false;
        else return false;
        else return true;
    };
}).controller("newCustomerController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {
    $('#modelDialog').on('hidden.bs.modal', function () {
        skipChecking = true;
        var modal = $(this);
        if (modal.find('.modal-title').text().toLowerCase().indexOf("discard") != -1) {
            skipChecking = false;
            return;
        }
        $state.go("customers.customersList");
        skipChecking = false;

        $(this).off('hidden.bs.modal');
    });
    $scope.$on("locationModelHiding", function () {
        skipChecking = true;
        var modal = $(this);

        if (modal.find('.modal-title').text().toLowerCase().indexOf("discard") != -1) {
            skipChecking = false;
            return;
        }
        $state.go("customers.customersList");
        skipChecking = false;
        $(this).off('hidden.bs.modal');
    })
    if ($state.current.name === "customers.addNew.addChoose") {
        $("#modelDialog").modal("show");
    }

    $scope.addLocations = function () {
        $rootScope.latestCustomerFormData = $scope.newCustomerFormData;
        skipChecking = true;
        $state.go("customers.addNew.addLocations");
    }
    $scope.addUsers = function () {
        $rootScope.latestCustomerFormData = $scope.newCustomerFormData;
        skipChecking = true;
        $state.go("customers.addNew.addUsers", $state.params);
    };
    $scope.$on('userAdded', function (event, args) {
        ApiFactory.getApiData({
            /**		
             * Request to post the information to server		
             */
            serviceName: "createcustomerusersandsites",
            data: {
                userId: $rootScope.addedUsersData.userId,
                customerId: $scope.customerId
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    skipChecking = true;
                    $state.go("customers.customersList.getCustomer.editCustomer");
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        });
    });
    $scope.$on('siteAdded', function (event, args) {
        skipChecking = true;
        $state.go("customers.customersList.getCustomer.editCustomer");
        skipChecking = false;
    });

    $scope.newCustomerFormData = {};
    $scope.getCustomerIdTab = function ($event) {
        if ($event.keyCode == 9) {
            $scope.getCustomerId();
        }
    }
    $scope.removeCustomerId = function ($event) {
        if (!$scope.newCustomerFormData.companyName) {
            $scope.newCustomerFormData.customerCode = "";
        }
    }
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.newCustomerFormData[$(this).attr("name")] = "";
        }
    });

    $scope.newCustomerFormData.usersList = $rootScope.addedUsersData;
    $scope.newCustomerFormData.locationsList = $rootScope.addedSiteData;
    ApiFactory.getApiData({
        /**
         * Request to post the information to server
         */
        serviceName: "loadStates",
        onSuccess: function (data) {
            $scope.newCustomerFormData.getStateOptions = data.data.states;
        },
        onFailure: function () {}
    });
    $scope.changeState = function (state) {
        if (state) {
            ApiFactory.getApiData({
                /**
                 * Request to post the information to server
                 */
                serviceName: "getCities",
                data: state,
                onSuccess: function (data) {
                    $scope.newCustomerFormData.getCityOptions = data.data;
                    $scope.newCustomerFormData.city = undefined;
                    $scope.$broadcast("citiesLoaded");
                },
                onFailure: function () {}
            });
        } else {
            $scope.newCustomerFormData.getCityOptions = [];
            $scope.newCustomerFormData.city = undefined;
        }
    };
    $scope.changeCity = function (city) {
        if (city) {
        	$scope.validateZipCode();
        }
    };

    $scope.uploadSubmitFormData = {};
    $scope.imageUpload = function (event) {
        var $target = event.target
        var tag = "." + $target.getAttribute('tagname');
        var classname = $target.getAttribute('classname');
        $scope.input = $($element).find("." + classname);
        commonFactories.getImageData(event, function (data) {
            var extenstion = data.name.substr((data.name.lastIndexOf(".") + 1), data.name.length);
            if (extenstion && (extenstion.toLowerCase() != "png" && extenstion.toLowerCase() != "jpg" && extenstion.toLowerCase() != "jpeg")) {
                toastr.error(messageFactory.getMessage("INFO_FILE_PNG"));
                $(event.target).replaceWith($scope.input.clone(true));
                return;
            }
            if ((data.size / (1024)).toFixed(1) < 500) {
                $($element).find(tag).attr("src", data.src);
                $($element).find(tag).attr("width", 50);
            } else {
                $(event.target).replaceWith($scope.input.clone(true));
                toastr.error("Please use only below 500KB for upload");

                return;
            }
            var element = $($target);
            element.attr("name", "file");
            if (data.src) {
                $scope.uploadSubmitFormData.files = $($target);
                ApiFactory.getApiData({
                    serviceName: "fileUpload",
                    data: $scope.uploadSubmitFormData,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $scope.newCustomerFormData.companyLogo = data.data.fileName;
                          } else {
                            $($target).replaceWith($scope.input.clone(true));
                            toastr.error(messageFactory.getMessage(data.code));
                        }

                    },
                    onFailure: function () {}
                });
            }
        });
    }
    /**
     * This method is for delete company logo of customer.
     */
    $scope.deleteImage = function () {
        $scope.newCustomerFormData.companyLogo=null;
    }
    
    
    var companyName = "";
    $scope.getCustomerId = function () {
        if ($scope.newCustomerFormData.companyName && companyName != $scope.newCustomerFormData.companyName) {
            companyName = $scope.newCustomerFormData.companyName;
            ApiFactory.getApiData({
                /**
                 * Request to post the information to server
                 */
                serviceName: "customercode",
                data: $scope.newCustomerFormData.companyName.split("/").join(""),
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.newCustomerFormData.customerCode = data.data.customerCode;
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        }
    }
    $scope.errors = {};
    $scope.errors.zipcode = false;
    $scope.validateZipCode = function () {
    	if (!angular.isUndefined($scope.newCustomerFormData.postalCode) && $scope.newCustomerFormData.postalCode.trim() !== "" && Math.round($scope.newCustomerFormData.postalCode) === 0) {
        	$scope.zipcodeError = "Zip code should not be zero.";
        	$scope.errors.zipcode = true;
        	return;
        } else {
         $scope.zipcodeError = "";
         $scope.errors.zipcode = false;
         if ($scope.newCustomerFormData.postalCode && $scope.newCustomerFormData.state && $scope.newCustomerFormData.city) {
             ApiFactory.getApiData({
                 serviceName: "checkzipcode",
                 data: {
                     "zipCode": $scope.newCustomerFormData.postalCode,
                     "stateId": $scope.newCustomerFormData.state.stateId.toString(),
                     "city": $scope.newCustomerFormData.city.cityName
                 },
                 onSuccess: function (data) {
                     if (data.status.toLowerCase() === "success") {
                         $scope.errors.zipcode = false;
                     } else {
                         $scope.zipcodeError = messageFactory.getMessage(data.code);
                         $scope.errors.zipcode = true;
                     }
                 },
                 onFailure: function () {}
             });
         } else {
        	 return;
         }
        }
    }
    
    
    //New customer controller
    $scope.getStateCityValidate = function () {

                if (!$scope.newCustomerFormData.state) {
                    $(".customer-state-name")[0].focus();
                    return;
                }
                if (!$scope.newCustomerFormData.city) {
                    $(".customer-city-name")[0].focus();
                }

            }
    
    $scope.getStatesAndCities = function (searchText) {
            $scope.makeDisable = false;

            if (!$scope.newCustomerFormData.state) {
                $scope.customerStateNameRequired = true;

            }
            if (!$scope.newCustomerFormData.city) {
                $scope.customerCityNameRequired = true;
            }
            if (!$scope.newCustomerFormData.state || !$scope.newCustomerFormData.city) {
                return;
            }

            if ($scope.newCustomerFormData.postalCode.length == 5) {
                getValidateZipCode();
            }
        };
    
    
        function getValidateZipCode(callback) {

            ApiFactory.getApiData({
                serviceName: "loadStatesfromZip",
                data: $scope.newCustomerFormData.postalCode,
                onSuccess: function (dataObj) {
                    if (dataObj.status.toLowerCase() === "success") {
                        if (dataObj.status == 'FAILURE') {
                            toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                        } else {

                            var matched;
                            var cityMatch;
                            var stateMatch;

                            $.each(dataObj.data, function (key, value) {

                                if ((value.state_name.toString().toLowerCase() == $scope.newCustomerFormData.state.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.newCustomerFormData.state.toString().toLowerCase()) && value.city_name.toString().toLowerCase() != $scope.newCustomerFormData.city.toString().toLowerCase()) {

                                    stateMatch = true;
                                    return;

                                }

                                if ((value.state_name.toString().toLowerCase() != $scope.newCustomerFormData.state.toString().toLowerCase() && value.state_short_code.toString().toLowerCase() != $scope.newCustomerFormData.state.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.newCustomerFormData.city.toString().toLowerCase()) {

                                    cityMatch = true;
                                    return;

                                }



                                if ((value.state_name.toString().toLowerCase() == $scope.newCustomerFormData.state.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.newCustomerFormData.state.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.newCustomerFormData.city.toString().toLowerCase()) {

                                    $scope.newCustomerFormData.cityId = value.city_id;
                                    $scope.newCustomerFormData.stateId = value.state_id;
                                    matched = true;

                                    if (callback) {
                                        callback(true);
                                    }

                                    return;

                                }



                            })

                            if (dataObj.data.length == 0) {

                                toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));

                                return;
                            }
                            if (!matched) {
                                if (stateMatch && !cityMatch) {
                                    var cities = [];

                                    dataObj.data.filter(function (obj) {
                                        cities.push(obj.city_name);
                                    })
                                    toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                                    return;
                                }

                                if (!stateMatch && cityMatch) {
                                    toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                                    return;
                                }

                                if (!stateMatch && !cityMatch) {
                                    var cities = [];

                                    dataObj.data.filter(function (obj) {
                                        cities.push(obj.city_name);
                                    })
                                    toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                                    return;
                                }
                            }
                        }
                    } else {

                        toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                    }

                },
                onFailure: function () {}
            });


        }




    if ($state.current.actionType !== "edit") {
        $scope.newCustomerFormData.thermostatePreferenceFanAuto = true;
        $scope.newCustomerFormData.thermostatePreferenceHvacAuto = true;
        $scope.newCustomerFormData.thermostatePreferenceResetHold = true;
        $scope.newCustomerFormData.nightlyScheduleDownload = true;
    }
    $scope.newCustomerForm = function (form) {
        if (form.$valid) {
            
            getValidateZipCode(function(){
                saveCustomer();
            })
            
        }
    }
    
    function saveCustomer(){
        
        $scope.newCustomerFormData1 = JSON.parse(JSON.stringify($scope.newCustomerFormData));
            delete $scope.newCustomerFormData1.getCityOptions;
            delete $scope.newCustomerFormData1.getStateOptions;
            //$scope.newCustomerFormData1.cityId = $scope.newCustomerFormData1.city.cityId;
            delete $scope.newCustomerFormData1.city;
            //$scope.newCustomerFormData1.stateId = $scope.newCustomerFormData1.state.stateId;
            delete $scope.newCustomerFormData1.state;

            $scope.newCustomerFormData1.city = $scope.newCustomerFormData1.cityId;
            $scope.newCustomerFormData1.state = $scope.newCustomerFormData1.stateId;

            if ($scope.newCustomerFormData1.thermostatePreferenceFanOn === '1' || $scope.newCustomerFormData1.thermostatePreferenceFanOn) {
                $scope.newCustomerFormData1.thermostatePreferenceFanOn = 1;
                $scope.newCustomerFormData1.thermostatePreferenceFanAuto = 0;
            } else if ($scope.newCustomerFormData1.thermostatePreferenceFanAuto === '1' || $scope.newCustomerFormData1.thermostatePreferenceFanAuto) {
                $scope.newCustomerFormData1.thermostatePreferenceFanOn = 0;
                $scope.newCustomerFormData1.thermostatePreferenceFanAuto = 1;
            } else {
                $scope.newCustomerFormData1.thermostatePreferenceFanOn = 0;
                $scope.newCustomerFormData1.thermostatePreferenceFanAuto = 0;
            }
            $scope.newCustomerFormData1.thermostatePreferenceHvacAuto = $scope.newCustomerFormData1.thermostatePreferenceHvacAuto === true ? 1 : 0;
            $scope.newCustomerFormData1.thermostatePreferenceResetHold = $scope.newCustomerFormData1.thermostatePreferenceResetHold === true ? 1 : 0;
            $scope.newCustomerFormData1.nightlyScheduleDownload = $scope.newCustomerFormData1.nightlyScheduleDownload === true ? 1 : 0;

            ApiFactory.getApiData({
                /**
                 * Request to post the information to server
                 */
                // data: $scope.newCustomerFormData1,
                serviceName: ($state.current.actionType === "edit") ? "updatecustomer" : "savecustomer",
                data: $scope.newCustomerFormData1,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        $scope.customerId = data.data.customerID;
                        $rootScope.customerId = data.data.customerID;
                        $rootScope.userDetails = data.data.getUserResponse;
                        skipChecking = true;
                        if ($state.current.actionType === "new") {
                        	 skipChecking = true;
                             history.back();
                            
                            return;
                        }
                        $rootScope.addedUsersData = null;
                        $rootScope.addedSiteData = null;
                        skipChecking = true;
                        history.back();
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        
    }
    $rootScope.accept = function () {
        if ($("#modelDialog").find(".confirm").attr("type") === "createCustomer") {
            skipChecking = true;
            $state.go("customers.addNew.addChoose");
            return;
        }
        ApiFactory.getApiData({
            serviceName: "deletecustomer",
            data: {
                customerId: $state.params.customerId
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                	$rootScope.userDetails = data.data.getUserResponse;
                    $("#modelDialog").modal("hide");
                    skipChecking = true;
                    $state.go("customers.customersList");
                    toastr.success(messageFactory.getMessage(data.code));
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                    $("#modelDialog").modal("hide");
                }
            },
            onFailure: function () {
                $("#modelDialog").modal("hide");
            }
        });
    };
    $scope.removeCustomer = function () {
        $('#modelDialog').on('show.bs.modal', function (event) {
            var button = $(event.relatedTarget); // Button that triggered the
            var recipient = button.data('whatever'); // Extract info from
           
            /*
             * If necessary, you could initiate an AJAX request here (and then
             * do the updating in a callback). Update the modal's content. We'll
             * use jQuery here, but you could use a data binding library or
             * other methods instead.
             */

            var modal = $(this);
            modal.find('.modal-title').text('Delete customer');
            modal.find('.model-content').text('Are you sure you want to delete customer ' + $scope.newCustomerFormData.companyName + ' including all of its sites, users, devices and schedules?');
            modal.find(".confirm").removeAttr("type");
        });
    };
    $scope.getThermostatePreferenceFan = function (model) {
        if (model === "newCustomerFormData.thermostatePreferenceFanAuto") {
            $scope.newCustomerFormData.thermostatePreferenceFanAuto = true;
            $scope.newCustomerFormData.thermostatePreferenceFanOn = false;
        }
        if (model === "newCustomerFormData.thermostatePreferenceFanOn") {
            $scope.newCustomerFormData.thermostatePreferenceFanAuto = false;
            $scope.newCustomerFormData.thermostatePreferenceFanOn = true;
        }
    }
    if ($state.current.actionType == "edit") {

        ApiFactory.getApiData({
            serviceName: "customerprofile",
            data: $state.params,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    /**
                     * We are injecting user information to $rootScope
                     * because when ever user switching to user information
                     * screen without refresing the page we are transforing
                     * user informationt to other controller
                     */
                    $scope.newCustomerFormData = $.extend($scope.newCustomerFormData, data.data[0]);
                    $scope.newCustomerFormData.thermostatePreferenceFanAuto = ($scope.newCustomerFormData, data.data[0].fanAuto === "No") ? false : true;
                    $scope.newCustomerFormData.thermostatePreferenceFanOn = ($scope.newCustomerFormData, data.data[0].fanOn === "No") ? false : true;
                    $scope.newCustomerFormData.thermostatePreferenceHvacAuto = ($scope.newCustomerFormData, data.data[0].havcAuto === "No") ? false : true;
                    $scope.newCustomerFormData.thermostatePreferenceResetHold = ($scope.newCustomerFormData, data.data[0].resetHold === "No") ? false : true;
                    $scope.newCustomerFormData.nightlyScheduleDownload = ($scope.newCustomerFormData, data.data[0].nightSchedule === "No") ? false : true;
                    $scope.newCustomerFormData.postalCode = $scope.newCustomerFormData.zipCode;
                    $scope.newCustomerFormData.degreePrefereces = $scope.newCustomerFormData.degreePreferenceId;
                    $scope.newCustomerFormData.customerStatus = $scope.newCustomerFormData.statusId;
                    $scope.newCustomerFormData.thermostateMaxSetPoint = $scope.newCustomerFormData.thermostateMaxSetPoint ? Number($scope.newCustomerFormData.thermostateMaxSetPoint) : null;
                    $scope.newCustomerFormData.thermostateMinSetPoint = $scope.newCustomerFormData.thermostateMinSetPoint ? Number($scope.newCustomerFormData.thermostateMinSetPoint) : null;
                    if ($scope.newCustomerFormData.stateId) {
                        ApiFactory.getApiData({
                            serviceName: "getCities",
                            data: {
                                "stateId": $scope.newCustomerFormData.stateId
                            },
                            onSuccess: function (data) {
                                $scope.newCustomerFormData.getCityOptions = data.data;
                            },
                            onFailure: function () {}
                        });
                    }
                    $scope.newCustomerFormData.state = $scope.newCustomerFormData.stateName;
                    $scope.newCustomerFormData.city = $scope.newCustomerFormData.cityName;
                    
                    if (angular.isUndefined($rootScope.latestCustomerFormData)) {

                    } else {
                        $scope.newCustomerFormData = $.extend($scope.newCustomerFormData, $rootScope.latestCustomerFormData);
                        delete $rootScope.latestCustomerFormData;
                    }
                }
            },
            onFailure: function () {}
        });
        $scope.$on('citiesLoaded', function () {
            $.each($scope.newCustomerFormData.getCityOptions, function (key, value) {
                if (value.cityName == $scope.newCustomerFormData.cityName) {
                    $scope.newCustomerFormData.city = value;
                    return;
                }
            })
        });
    }
}).controller("getCustomerController", function () {

}).controller("addUsersController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state) {
    $('#modelDialog').on('show.bs.modal', function () {
        var modal = $(this);
        modal.find(".modal-title").text("Add User");
    });
    $('#modelDialog').on('hidden.bs.modal', function () {
        if ($state.$current.self.name === "customers.addNew.addUsers") {
            skipChecking = true;
            $state.go("customers.customersList");
            skipChecking = false;
        }
        $(this).off('hidden.bs.modal');
    });

    $("#modelDialog").modal("show");
    $scope.importManually = function () {
        $state.params.isFromEditCustomer = true;
        $state.go("users.addNew", $state.params);
    }
}).controller("activityLogCustomerController", function ($scope, ApiFactory, $rootScope, $state, $element) {
    $scope.open1 = function () {
        $scope.popup1.opened = true;
    };

    $scope.open2 = function () {
        $scope.popup2.opened = true;
    };

    $scope.popup1 = {
        opened: false
    };

    $scope.popup2 = {
        opened: false
    };

    $scope.activityObj = {};
    $scope.activityData = {};
    $scope.activityObj.specificId = $state.params.customerId;
    $scope.activityObj.serviceId = 1;
    $scope.startDate = "";
    $scope.endDate = "";
    $scope.activityObj.startDate = "";
    $scope.activityObj.endDate = "";
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.activityObj[$(this).attr("name")] = "";
        }
    });
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];
    
    var groups = {};
    var sites = {};
    var devices = {};

    function listActivity() {
        ApiFactory.getApiData({
            serviceName: "getActivityLog",
            data: $scope.activityObj,
            onSuccess: function (data) {
                $("#nodata").css({
                    opacity: 1
                });
                $scope.logs = data.data;
                $.each($scope.logs, function (key, value) {
	        		if (value.alServiceName.toLowerCase().trim() === "group") {
	        			if (value.alSpecificName) {
	        				groups[value.alSpecificName.trim()] = value.alSpecificName.trim();
	        			}
	        		}
	        		if (value.alServiceName.toLowerCase().trim() === "site") {
	        			if (value.alSpecificName) {
	        				sites[value.alSpecificName.trim()] = value.alSpecificName.trim();
	        			}
	        		}
	        		if (value.alServiceName.toLowerCase().trim() === "device") {
	        			if (value.alSpecificName) {
	        				devices[value.alSpecificName.trim()] = value.alSpecificName.trim();
	        			}
	        		}
                });
                var tempGroupsList = [];
                $.each(groups,function(key, value){
            		 tempGroupsList.push({
                         value: key,
                         lable: value
                     });
                });
            	$scope.groups = $rootScope.aphaNumSort(tempGroupsList,'lable');
                
                var tempSitesList = [];
                $.each(sites,function(key, value){
            		 tempSitesList.push({
                         value: key,
                         lable: value
                     });
                });
            	$scope.sites = $rootScope.aphaNumSort(tempSitesList,'lable');

            	var tempDevicesList = [];
                $.each(devices,function(key, value){
            		 tempDevicesList.push({
                         value: key,
                         lable: value
                     });
                });
            	$scope.devices = $rootScope.aphaNumSort(tempDevicesList,'lable');
            },
            onFailure: function () {}
        });
    }
    listActivity();

    $scope.dateFilterData = function () {
        $scope.activityObj.specificId = $state.params.customerId;
        $scope.activityObj.serviceId = 1;
        if ($scope.startDate && $scope.endDate) {
            if ($scope.startDate) {
                $scope.activityObj.startDate = moment($scope.startDate).format("MM/DD/YYYY");
            }
            if ($scope.endDate) {
                $scope.activityObj.endDate = moment($scope.endDate).format("MM/DD/YYYY");
            }
            listActivity();
        } else if (!$scope.startDate && !$scope.endDate) {
    		$scope.activityObj.startDate = "";
    		$scope.activityObj.endDate = "";
    		listActivity();
    	}
    }
    $scope.logActivityFilter = function (obj) {
    	switch ($scope.activityObj.filterBy && $scope.activityObj.filterBy.toLowerCase()) {
	        case "group":
	        	if ($scope.activityObj.groupId) {
	        		return obj.alSpecificName === $scope.activityObj.groupId;
	        	} else {
	        		return true;
	        	}
	        case "site":
	        	if ($scope.activityObj.siteId) {
	        		return obj.alSpecificName === $scope.activityObj.siteId;
	        	} else {
	        		return true;
	        	}
	        case "device":
	        	if ($scope.activityObj.deviceId) {
	        		return obj.alSpecificName === $scope.activityObj.deviceId;
	        	} else {
	        		return true;
	        	}
	        case "module":
	        	if ($scope.activityObj.filterByModule) {
	        		return obj.alServiceName === $scope.activityObj.filterByModule;
	        	} else {
	        		return true;
	        	}
	        case "action":
	        	if ($scope.activityObj.filterByAction) {
	        			return obj.alAction.toLowerCase() === $scope.activityObj.filterByAction.toLowerCase();
	        	} else {
	        		return true;
	        	}
	        	/*if ($scope.activityObj.filterByAction) {
	        		if($scope.activityObj.filterByAction.toLowerCase() === 'called'){
	        			return (obj.alAction.toLowerCase() === 'called' 
	        				|| obj.alAction.toLowerCase() === 'emailed'
	        			    || obj.alAction.toLowerCase() === 'texted')
	        		}else{
	        			return obj.alAction.toLowerCase() === $scope.activityObj.filterByAction.toLowerCase();
	        		}
	        	} else {
	        		return true;
	        	}*/
			}
    	
	    	$scope.searchActivity = function (item) {
	             var input = item.aldescription;
	             if (!$scope.filterActivity || (input.toLowerCase().indexOf($scope.filterActivity.toLowerCase()) !== -1)) {
	                 return true;
	             }
	             return false;
	        };
	    return true;
	};
});