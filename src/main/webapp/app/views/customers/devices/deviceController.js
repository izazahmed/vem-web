app.controller("deviceListController", function ($scope, $rootScope, $state, ApiFactory, deviceFactory, commonFactories, messageFactory, toastr, $timeout, $element) {

        $scope.locations = deviceFactory.getLocations();
        $scope.getLocation = deviceFactory.getlocationLabel;
        $scope.location = $scope.locations[0].value.toString();
        $scope.getWeekLabel = deviceFactory.getWeekDay;
        $scope.isDataAvailable = true;

        $scope.filterChange = function () {
            $scope.siteId = $scope.sitesDropdownList[0] && $scope.sitesDropdownList[0].siteId.toString();
            $scope.location = $scope.locations[0] && $scope.locations[0].value.toString();
            $scope.customerId = $scope.customerDropdownList[0] && $scope.customerDropdownList[0].customerId.toString();
            $scope.groupId = $scope.groupDropdownList[0] && $scope.groupDropdownList[0].groupId.toString();
            $scope.scheduleId = $scope.scheduleDropdownList[0] && $scope.scheduleDropdownList[0].scheduleId.toString();
        }
        if ($state.current.name.indexOf('devices.devicesList') != -1 && $rootScope.userDetails.rolePermissions['Device Management'] == 2) {
            commonFactories.getCustomers(function (data) {

                $scope.customerList = data;

            })
        }
        var data = {};


        switch ($state.current.name) {

            case "scheduler.schedulerList.getSchedule.devices":

                data.id = $state.params.scheduleId;
                data.module = 'scheduleId';

                break;

            case "sites.sitesList.viewSite.devices":

                data.id = $state.params.siteId;
                data.module = 'siteId';
                break;

            case "groups.groupList.groupInfo.devices":

                data.id = $state.params.groupId;
                data.module = 'groupId';
                break;

            case "groups.groupList.groupInfo.devices":

                data.id = $state.params.groupId;
                data.module = 'groupId';
                break;

            case "devices.devicesList":

                data.id = $rootScope.userDetails.userId;
                data.module = 'userId';
                break;

            default:

                data.id = $state.params.customerId;
                data.module = 'customerId';
                break;
        }
        $scope.deviceIds = [];
        ApiFactory.getApiData({
            serviceName: "getDevicesList",
            data: data,
            onSuccess: function (data) {

                if (data.status.toLowerCase() === "success") {
                	
                	$timeout(function(){
                	 $("#nodata").css({
                         opacity: 1
                     });

                	},1000)
                	if (data.data && data.data.things.length === 0) {
                		$scope.devicesList = [];
                        $scope.isDataAvailable = false;
                        return;
                    }
                	

                    $.each(data.data.things, function (key, value) {
                        if (value.thingState && value.thingState.tstat_msg == 'null') {
                            value.thingState.tstat_msg = null;
                        }

                        if (value.configChanges && value.configChanges.tstat_msg == 'null') {
                            value.configChanges.tstat_msg = null;
                        }
                        $scope.deviceIds.push({
                            id: value.deviceId,
                            name: value.name
                        });
                    });
                    
                    if ($scope.deviceIds.length > 0) {
                    	$scope.deviceIds = $rootScope.aphaNumSort($scope.deviceIds,'deviceLable');
                    }

                    $scope.permission = data.data.permission;
                    $scope.devicesList = data.data.things;
                    $rootScope.getDevicesListLength = $scope.devicesList.length;
                    $scope.getCurrentStatus = true;
                   
                    loadDropdownList();
                    loadLocationList();

                } else {

                    // toastr.error(messageFactory.getMessage("serverError"));
                }
                if ($state.current.name == 'sites.sitesList.viewSite.devices') {
                    loadAnayticsParams();
                }
            },
            onFailure: function () {}
        });

        function loadDropdownList() {

            var sites = [];

            $.each(angular.copy($scope.devicesList), function (key, value) {
                if (value.siteId) {
                    var temp = angular.copy(value);
                    sites.push(temp);
                }

            });

            $scope.sitesDropdownList = commonFactories.getSortedArray(commonFactories.getUniqueArray(sites, "siteId"), "siteName");

            var customers = [];

            $.each(angular.copy($scope.devicesList), function (key, value) {
                if (value.customerId) {
                    var temp = angular.copy(value);
                    customers.push(temp);
                }

            });

            $scope.customerDropdownList = commonFactories.getSortedArray(commonFactories.getUniqueArray(customers, "customerId"), "customerName");

            var devices = [];

            $.each(angular.copy($scope.devicesList), function (key, value) {
                if (value.groupId) {
                    $.each(value.groupId.split(","), function (key1, value1) {

                        var temp = angular.copy(value);
                        temp.groupId = value1;
                        temp.groupName = value.groupName.split(",")[key1];

                        devices.push(temp);

                    });
                }

            });


            $scope.groupDropdownList = commonFactories.getSortedArray(commonFactories.getUniqueArray(devices, "groupId"), "groupName");

            var schedules = [];

            $.each(angular.copy($scope.devicesList), function (key, value) {
                if (value.scheduleId) {
                    var temp = angular.copy(value);
                    schedules.push(temp);
                }

            });

            $scope.scheduleDropdownList = commonFactories.getSortedArray(commonFactories.getUniqueArray($scope.devicesList, "scheduleId"), "scheduleName");

        }

        function loadLocationList() {

            var uniqueLocations = commonFactories.getUniqueArray($scope.devicesList, "location");

            var locations = [];

            $.each(uniqueLocations, function (key, value) {

                if (value.location) {
                    var temp = {};
                    temp.value = value.location;
                    temp.label = deviceFactory.getlocationLabel(value.location);

                    locations.push(temp);
                }
            })

            $scope.locations = commonFactories.getSortedArray(locations, "label");

        }

        $scope.showPlus = false;
        $scope.checkAll = function () {
            $scope.showMain = $scope.showMain == true ? false : true;
            $scope.showPlus = $scope.showPlus == true ? false : true;
            $scope.devicesList = angular.copy($scope.devicesList);
        };

        $scope.deviceActivityFilter = function (obj) { /** *providers the filtered results, based on user inputs *@param :obj */
            switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
                case "status":
                    return obj.isActive === Number($scope.deviceActivity);
                case "site":
                    return Number(obj.siteId) === Number($scope.siteId);
                case "customer":
                    return Number(obj.customerId) === Number($scope.customerId);
                case "group":
                    return obj.groupId && obj.groupId.toString().indexOf($scope.groupId.toString()) != -1;
                case "schedule":
                    return Number(obj.scheduleId) === Number($scope.scheduleId);
                case "location":
                    return Number(obj.location) === Number($scope.location);
            }
            return true;
        };
        /*    if (!$scope.sites && $state.params.customerId) {
                deviceFactory.getSites($state.params, function (data) {
                    $scope.sites = data;
                    $scope.siteId = data[0].siteId.toString();
                    if ($state.current.actionType == "edit") {
                        loadThermoStart();
                    }
                });
            }*/
        $scope.getCSSClass = function (current, heat, cool) {
            if (!Number(current).between(Number(heat), Number(cool))) {
                return "text-danger";
            }
        };

        $scope.getZonteTypeCSSClass = function (temp) {
             if (temp) {

            	 
				 if(temp.op_state == 'OFF' && temp.tstat_mode == 'OFF'){
					 
					 return;
				 }
				
					if(temp.op_state == 'OFF'){
					return;
				}
				
				
				if (temp.op_state.toLowerCase().trim() === "cool") {
					return "text-blue";
				}
				if (temp.op_state.toLowerCase().trim() === "heat") {
					return "text-orange";
				}
				return;
               

                }
            
        };
        $scope.search = function (item) { /** *Filtering first name and last name from the object *@param item */
            var input = item.name + " " + item.siteName + " # " + item.siteCode;
            if (!$scope.filterDevices || (item.name.toLowerCase().indexOf($scope.filterDevices) != -1) || (input.toLowerCase().indexOf($scope.filterDevices.toLowerCase()) != -1)) {
                return true;
            }
            return false;
        };
        $scope.showDeviceProfile = function (device) {
            $rootScope.deviceProfileData = null;
            $rootScope.deviceProfileData1 = null;
            $rootScope.deviceProfileParentData = null;

            if ($state.current.name.indexOf("devices.devicesList") != -1) {
                var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + device.deviceId;

                $state.go("devices.devicesList.profile.settings", $rootScope.mergeObject($state.params, {
                    deviceId: device.deviceId,
                    customerId: device.customerId,
                    sourcePage: sourcePage
                }));
                return;
            }

            $state.go("devices.devicesList.profile.settings", $rootScope.mergeObject($state.params, {
                deviceId: device.deviceId,
                customerId: device.customerId
            }));
        };
        $scope.showDetailsAnalytics = function () {
            $scope.showDetailsAnalyticsCont = !$scope.showDetailsAnalyticsCont;
            if (!$scope.commonChartData && $scope.showChartDetails) {
                loadAnalyticsData();
            }
        }

        /* Starts Analytic reports*/
        $scope.report = {};
        $scope.deviceSelected = [];
        $scope.parameterSelected = [];
        $scope.parameterOptions = [{}];
        $scope.dataType = [];
        $scope.daysDatatype = [];
        $scope.statusDataDatatype = [];
        $scope.trendingDataDatatype = [];
        $scope.analyticsMessage = {
            buttonDefaultText: 'Select Analytics Data'
        };
        $scope.deviceSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 1,
            scrollable: true,
            dynamicTitle: true,
            displayProp: "name",
            idProp: "id"
        };
        $scope.parameterSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 1,
            scrollable: true,
            dynamicTitle: true,
            displayProp: "label",
            idProp: "value"
        };
        $scope.deviceMessage = {
            buttonDefaultText: 'Select Devices'
        };
        $scope.errors = {};
        $scope.errors.devicesLength = false;
        $scope.errors.devicesLengthMessage = [];
        $scope.timeOptions = [{
            name: "Last 24 Hrs",
            id: 1
	    }, {
            name: "Last 7 Days",
            id: 7
	    }, {
            name: "Last 28 Days",
            id: 28
	    }];
        $scope.report.time = $scope.timeOptions[0].id;

        function loadAnayticsParams() {
            ApiFactory.getApiData({
                serviceName: "getAnalyticParams",
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.dataType.push({
                            id: data.data[0].id,
                            name: "Temperature vs Setpoint"
                        });
                        $scope.dataType.push({
                            id: data.data[7].id,
                            name: data.data[7].label
                        });
                        $scope.dataType.push({
                            id: data.data[6].id,
                            name: data.data[6].label
                        });
                        $scope.daysDatatype = data.data[6].analyticParams;
                        $scope.statusDataDatatype = data.data[7].analyticParams;
                        $scope.trendingDataDatatype = data.data[0].analyticParams;
                    }
                },
                onFailure: function () {}
            });
        };
        $scope.getAnalyics = function () {
            if ($scope.report.datatype === 7) {
                $scope.parameterSelected = [];
                $scope.parameterOptions = [];
                $scope.parameterOptions = $scope.daysDatatype;
                $.each($scope.parameterOptions, function (key, value) {
                    $scope.parameterSelected.push({
                        id: value.value
                    })
                });
            } else if ($scope.report.datatype === 8) {
                $scope.parameterSelected = [];
                $scope.parameterOptions = [];
                $scope.parameterOptions = $scope.statusDataDatatype;
                $.each($scope.parameterOptions, function (key, value) {
                    $scope.parameterSelected.push({
                        id: value.value
                    })
                });
            } else if ($scope.report.datatype === 1) {
                $scope.parameterSelected = [];
                $scope.parameterOptions = [];
                $scope.parameterOptions = $scope.trendingDataDatatype;
            } else {
                $scope.parameterOptions = [];
                $scope.parameterSelected = [];
            }
        }

        $scope.getDates = function () {
            if ($scope.report.time > 0) {
                var d = new Date();
                $scope.report.toDate = new Date();
                d.setDate(d.getDate() - Number($scope.report.time));
                $scope.report.fromDate = d;
            }
        };

        $scope.isExists = function (flag, data, id) {
            var idData;
            for (var i = 0; i < data.length; i++) {
                if (flag === 'other') {
                    idData = Number(data[i].id);
                } else if (flag === 'params') {
                    idData = Number(data[i].value);
                }
                if (idData === Number(id)) {
                    return data[i]
                }
            }
        }

        function loadAnalyticsData() {
            $scope.getDates();
            $scope.report.mainTitle = "";
            $scope.report.subTitle = "";
            $scope.labelDisplay = {};
            var analyticParams = [];
            var farenHeatParams = [];
            var onOffParams = [];
            var modeParams = [];
            var daysParams = [];
            $scope.deviceFinal = [];
            $scope.labelDisplay.device = [];
            $.each($scope.parameterSelected, function (key, value) {
                var unit;
                if ($scope.report.datatype === 1) {
                    unit = $scope.isExists('params', $scope.parameterOptions, value.id).labelUnit;
                } else if ($scope.report.datatype === 7) {
                    unit = $scope.isExists('params', $scope.parameterOptions, value.id).labelUnit;
                } else if ($scope.report.datatype === 8) {
                    unit = $scope.isExists('params', $scope.parameterOptions, value.id).labelUnit;
                }
                if (unit === 'F') {
                    farenHeatParams.push(value.id);
                } else if (unit === 'M') {
                    modeParams.push(value.id);
                } else if (unit === 'D') {
                    daysParams.push(value.id);
                } else if (unit === 'B') {
                    onOffParams.push(value.id);
                } else {
                    analyticParams.push(value.id);
                }
            });
            $.each($scope.deviceSelected, function (key, value) {
                $scope.labelDisplay.device.push($scope.isExists('other', $scope.deviceIds, value.id).name);
                $scope.deviceFinal.push(value.id);
            });
            if ($scope.labelDisplay.device.length > 0) {
                $scope.report.mainTitle += $scope.labelDisplay.device.join(", ");
            }
            if ($scope.report.time) {
                $scope.report.subTitle += $scope.isExists('other', $scope.timeOptions, $scope.report.time).name + "<br>";
            }
            if ($scope.dataType.length > 0 && $scope.report.datatype > 0) {
                $scope.labelDisplay.reportType = $scope.report.datatype ? $scope.isExists('other', $scope.dataType, $scope.report.datatype).name : '';
            }
            $scope.report.subTitle = "";
            $scope.report.subTitle += $scope.labelDisplay.reportType + "<br>";
            if ($scope.report.from) {
                $scope.report.subTitle += $scope.report.from;
            }
            if ($scope.report.to) {
                $scope.report.subTitle += " - " + $scope.report.to;
            }
            var apiData = {
                customerId: $state.params.customerId,
                groupIds: 0,
                siteIds: 0,
                deviceIds: $scope.deviceFinal.join(","),
                fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
                toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
                dataType: $scope.report.datatype ? $scope.report.datatype : 0,
                analyticParams: analyticParams.join(","),
                farenHeatParams: farenHeatParams.join(","),
                onOffParams: onOffParams.join(","),
                modeParams: modeParams.join(","),
                daysParams: daysParams.join(","),                
                degreeDaysGraph: ($scope.report.datatype === 7) ? 1 : 0,
                type: 'DEVICE'
            };
            $scope.serviceName = "getTrendingAnalytics";
            $scope.commonChartData = [];
            if ($scope.deviceFinal.length > 0) {
                ApiFactory.getApiData({
                    serviceName: $scope.serviceName,
                    data: apiData,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $scope.commonChartData = data.data;
                            $scope.errors.devicesLength = false;
                            $scope.errors.devicesLengthMessage = [];
                            if ($scope.commonChartData.graphsValidation.isDaysParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Days section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.commonChartData.graphsValidation.isFarenHeatParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Temperature section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.commonChartData.graphsValidation.isModeParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Modes section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.commonChartData.graphsValidation.isOnOffParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("On/Off section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            
                            if ($scope.report.datatype === 7) {
                                var degreeDaysList = [];
                                for (var i = 0; i < data.data.degreeDaysList.length; i++) {
                                    if (data.data.degreeDaysList[i].paramId == 59 || data.data.degreeDaysList[i].paramId == 61 || data.data.degreeDaysList[i].paramId == 63 || data.data.degreeDaysList[i].paramId == 64) {
                                        data.data.degreeDaysList[i]["type"] = "spline";
                                        data.data.degreeDaysList[i]["yAxis"] = 1;
                                        data.data.degreeDaysList[i]["lineWidth"] = 20;
                                        data.data.degreeDaysList[i]["linecap"] = "square";
                                        degreeDaysList.push(data.data.degreeDaysList[i]);
                                    } else if (data.data.degreeDaysList[i].paramId == 62 || data.data.degreeDaysList[i].paramId == 65) {
                                        data.data.degreeDaysList[i]["type"] = "spline";
                                        data.data.degreeDaysList[i]["lineWidth"] = 2;
                                        degreeDaysList.push(data.data.degreeDaysList[i]);
                                    }
                                }
                                data.data.degreeDaysList = degreeDaysList;
                                $scope.commonChartData = data.data;
                            }
                            
                            /*$timeout(function () {
                                $($element).closest(".scroll").animate({
                                    scrollTop: $($element).closest(".scroll").prop("scrollHeight")
                                });
                            }, 500);*/

                            $timeout(function () {
                                var container = $($element).closest(".scroll"),
                                    scrollTo = $($element).find("[type=submit]");
                                container.animate({
                                    scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
                                });
                            }, 500);


                        } else {
                            $scope.errors.devicesLength = false;
                            $scope.errors.devicesLengthMessage = [];
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    }
                });
            }
        }
        $scope.loadAnalytics = function (form) {
            if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            } else {
                if ($scope.parameterSelected.length <= 0) {
                    return;
                }
            }
            loadAnalyticsData();
        }
    })
    .controller("newDeviceController", function ($scope, $rootScope, $state, ApiFactory, toastr, $session, messageFactory, deviceFactory, $element) {

        $scope.statusOptions = [
            {
                label: "Active",
                id: 1
    	                        }, {
                label: "Inactive",
                id: 0
    	                        }
    	                       ];

        $scope.locations = deviceFactory.getLocations();

        $scope.location = $scope.locations[0].value.toString();

        $scope.device = {};
        $scope.changeSite = function () {
            if ($scope.device.siteId) {
                ApiFactory.getApiData({
                    serviceName: "listThermostatUnit",
                    data: $scope.device.siteId,
                    onSuccess: function (data) {
                        $scope.units = [];
                        if (data.data.length > 0) {
                            $scope.units = data.data;
                        }
                    },
                    onFailure: function () {}
                });
                ApiFactory.getApiData({
                    serviceName: "getTstatPref",
                    data: $scope.device.siteId,
                    onSuccess: function (data) {
                        if (data.data) {
                            $scope.device.hvacToAuto = data.data.hvacToAuto;
                            $scope.device.fanPref = data.data.fanPref;
                            $scope.device.holdToAuto = data.data.holdToAuto;
                            $scope.device.nightSchedule = data.data.nightSchedule;
                            $scope.device.maxSP = data.data.maxSP;
                            $scope.device.minSP = data.data.minSP;
                            $scope.device.lockPref = data.data.lock;
                        }
                    },
                    onFailure: function () {}
                });
            }
        };

        $scope.getUnit = function () {

            if (!$scope.device.unit) {
                $scope.device.location = null;
                return;
            }
            $.each($scope.units, function (key, value) {
                if (value.tstatUnit === $scope.device.unit) {
                    $scope.device.location = Number(value.locationType);
                }
                if (value.locationType == 23 && value.tstatUnit === $scope.device.unit) {
                	$scope.device.otherLocation = value.otherLocation;
                }
            })

        }
        $.each($($element).find("form").find("input,select,textarea"), function () {
            if ($(this).attr("name")) {
                $scope.device[$(this).attr("name")] = "";
            }
        });
        $scope.addDevice = function (form) {

            if (form.$valid && !$scope.preventSpaces) {
                $scope.deviceData = angular.copy($scope.device);
                $scope.deviceData.deviceType = Number($scope.deviceData.deviceType);
                $scope.deviceData.location = Number($scope.deviceData.location);
                $scope.deviceData.registerType = Number($scope.deviceData.registerType);
                $scope.deviceData.siteId = Number($scope.deviceData.siteId);
                $scope.deviceData.customerId = Number($state.params.customerId);
                $scope.deviceData.hvacToAuto = $scope.deviceData.hvacToAuto ? $scope.deviceData.hvacToAuto : 0;
                $scope.deviceData.holdToAuto = $scope.deviceData.holdToAuto ? $scope.deviceData.holdToAuto : 0;
                $scope.deviceData.nightSchedule = $scope.deviceData.nightSchedule ? $scope.deviceData.nightSchedule : 0;
                $scope.deviceData.maxSP = $scope.deviceData.maxSP ? $scope.deviceData.maxSP : 0;
                $scope.deviceData.minSP = $scope.deviceData.minSP ? $scope.deviceData.minSP : 0;

                var reqParams = ["deviceId", "registerType", "name", "model", "macId", "version", "siteId", "location", "deviceType", "customerId", "deviceTag", "sf22HwVersion", "wifiKey", "unit", "fanPref", "hvacToAuto", "holdToAuto", "minSP", "maxSP", "lockPref", "nightSchedule","otherLocation"];

                $scope.deviceData1 = angular.copy($scope.deviceData);

                $.each($scope.deviceData, function (key, value) {

                    if (reqParams.indexOf(key) == -1) {
                        delete $scope.deviceData1[key];

                    }

                });

                if ($state.current.actionType === "edit") {

                    $scope.deviceData1.isActive = $scope.deviceData.isActive;
                }

                $scope.deviceData = angular.copy($scope.deviceData1);
                
                if($scope.deviceData.model == 'Pro1 T855i2'){
                	$scope.deviceData.awsCompatible = 1;
                }else{
                	$scope.deviceData.awsCompatible = 0;
                }
                

                ApiFactory.getApiData({
                    serviceName: ($state.current.actionType === "edit") ? "updateDevice" : "registerDevice",
                    data: $scope.deviceData,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            skipChecking = true;
                            history.back();
                            toastr.success(messageFactory.getMessage(data.code));
                        } else {
                            if (data.code === "ERROR_SQL_SAVE_DEVICE_5003") {
                                if (data.data && data.data.createdByUserName) {
                                    toastr.error("Mac ID is already registered to thermostat " + data.data.name);
                                } else {

                                    toastr.error("This mac-id is already registered in VEM 2.0");

                                }
                                return;
                            }
                            if (data.code === "ERROR_XCSPEC_ALREADY_REG_5002") {
                                if (data.data && data.data.createdByUserName) {
                                    toastr.error("Mac ID is already registered to thermostat " + data.data.name);

                                } else {
                                    toastr.error("This mac-id is already registered in XCSPEC");
                                }
                                return;
                            }

                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            }
        };

        if (!$scope.sites && $state.params.groupId) {
            deviceFactory.getSites({
                sortBy: 'groupId',
                value: $state.params.groupId
            }, function (data) {
                $scope.sites = data;

                if ($state.params.siteId) {

                    $scope.device.siteId = Number($state.params.siteId);

                    $scope.changeSite();

                }
            });
        } else if (!$scope.sites && $state.params.customerId) {
            deviceFactory.getSites({
                sortBy: 'customerId',
                value: $state.params.customerId
            }, function (data) {
                $scope.sites = data;

                if ($state.params.siteId) {

                    $scope.device.siteId = Number($state.params.siteId);

                    $scope.changeSite();

                }
            });


        }
        $scope.preventSpacesField = function ($event) {
            if (/\s/g.test($event.target.value.trim())) {
                $scope.preventSpaces = true;
            } else {
                $scope.preventSpaces = false;
            }
        }




        if ($state.current.actionType == "edit") {




            loadDeviceProfile();


        }

        function loadDeviceProfile() {


            ApiFactory.getApiData({
                serviceName: "getDeviceDataByDeviceId",
                data: $state.params,
                onSuccess: function (data) {

                    if (data.status.toLowerCase() === "success") {

                        $scope.sourceDevice = angular.copy(data.data);

                        $scope.device = data.data;
                        $scope.device.deviceType = $scope.device.deviceType.toString();

                        $scope.disableArea = $scope.device.registerType;

                        loadThermoStart();

                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }

                },
                onFailure: function () {}
            });
        }

        function loadThermoStart() {


            if ($state.current.actionType == "edit") {

                if (!$scope.device.siteId) {
                    return;
                }
                ApiFactory.getApiData({
                    serviceName: "listThermostatUnit",
                    data: $scope.device.siteId,
                    onSuccess: function (data) {
                        
                         if($scope.device.unit){
                            $scope.units = [{locationType:$scope.device.location,tstatUnit:$scope.device.unit}];
                            return;
                        }
                        
                        if (data.data.length > 0) {
                            $scope.units = data.data;
                        }
                    },
                    onFailure: function () {}
                });

            }
        }


        $scope.removeDevice = function () {

            $('#modelDialog').on('show.bs.modal', function (event) {
                $(this).off('show.bs.modal');
                var modal = $(this);

                modal.find('.modal-title').text('Delete Device');
                modal.find('.model-content').text('Are you sure you want to delete this device?');
            });

            $("#modelDialog").find('.confirm')[0].targetElement = {
                actionType: "delete"
            };

            $("#modelDialog").modal("show");

        }

        $scope.disconnectDevice = function () {

            var actionFrom = $scope.device.registerType;

            $('#modelDialog').on('show.bs.modal', function (event) {
                $(this).off('show.bs.modal');
                var modal = $(this);

                modal.find('.modal-title').text('Disconnect Device');
                modal.find('.model-content').text('Are you sure you want to disconnect the device?');
                return;
            });
            $("#modelDialog").find('.confirm')[0].targetElement = {
                actionType: "connect"
            };
            if (!actionFrom && $scope.sourceDevice.registerType) {
                $scope.device.registerType = ($scope.device.registerType === 1) ? 0 : 1;
                $("#modelDialog").modal("show");

            }

        }

        $rootScope.accept = function () {
            switch ($("#modelDialog").find('.confirm')[0].targetElement.actionType) {


                case "connect":

                    $("#modelDialog").modal("hide");

                    $scope.device.registerType = 0;
                    break;

                case "delete":

                    ApiFactory.getApiData({
                        serviceName: "deleteDevice",
                        data: {
                            "deviceId": $scope.device.deviceId,
                        },
                        onSuccess: function (data) {
                            if (data.status.toLowerCase() === "success") {
                                skipChecking = true;

                                $state.go("devices.devicesList");
                                toastr.success(messageFactory.getMessage(data.code));
                            } else {

                                toastr.success(messageFactory.getMessage(data.code));

                            }
                            $("#modelDialog").modal("hide");
                        },
                        onFailure: function () {}
                    });

                    break;

            }
        }

    })