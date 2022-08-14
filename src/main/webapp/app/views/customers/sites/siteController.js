/** 
 *@file : siteController 
 * 
 *@siteController :Load site module functionality for application 
 * 
 *@author :(Bhoomika Rabadiya - brabadiya@ctepl.com) 
 * 
 *@Contact :(Umang - ugupta@ctepl.com) 
 * 
 *@Contact : (Chenna - yreddy@ctepl.com) 
 *
 *@version     VEM2-1.0
 *@date        08-09-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       08-09-2016   Bhoomika Rabadiya           File Created
 * 02       08-09-2016   Bhoomika Rabadiya           Added siteListController
 * 03       08-09-2016   Bhoomika Rabadiya           Added viewSiteController
 * 04       20-09-2016   Bhoomika Rabadiya           Added editSiteController
 * 05       21-09-2016   Bhoomika Rabadiya           Added addLocationsController
 * 06       21-09-2016   Bhoomika Rabadiya           Added newLocationsController
 * 07       21-09-2016   Bhoomika Rabadiya           Added newSiteController
 *
 */
app.controller("siteListController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, $element, commonFactories) {

        if ($state.current.name == "sites.sitesList") {
            if ($state.params && $state.params.customerId != -1) {
                $state.go($state.current, $rootScope.mergeObject($state.params, {
                    customerId: -1,
                    sourcePage: "sites"
                }), {
                    reload: true
                });
                return;
            }
        }
        $scope.alphabets = ['#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
        $scope.alphabetHeight = Math.floor(($(".alphabets-badge-width").height() - 6) / 27);
        $scope.moveRelatedGroup = function (alphabet) {
            $($element).find(".sitelist").animate({
                scrollTop: $($element).find("[group='" + alphabet + "']").position().top + $($element).find(".sitelist").scrollTop()
            });
        }
        $scope.siteCityOptions = [];
        $scope.siteGroupOptions = [];
        $scope.siteStateOptions = [];
        $scope.siteScheduleOptions = [];
        $scope.siteCustomerOptions = [];

        $scope.isDataAvailable = true;

        $rootScope.getListSite = function () {
            $("#modelDialog").modal("hide");
            if (!$state.params.customerId) {
                $state.params.customerId = $rootScope.userDetails.customers.split(",")[0] ? $rootScope.userDetails.customers.split(",")[0] : 0;
                if ($state.params.customerId >= 0) {
                    listSite();
                }
            } else {
                listSite();
            }
        };
        if ($state.current.name.indexOf('sites.sitesList') != -1 && $rootScope.userDetails.rolePermissions['Site Management'] == 2) {
            commonFactories.getCustomers(function (data) {
                $scope.customerList = data;
            })
        }

        $rootScope.getListSite();

        function predicatBy(prop) {
            return function (a, b) {
                if (a[prop] > b[prop]) {
                    return 1;
                } else if (a[prop] < b[prop]) {
                    return -1;
                }
                return 0;
            }
        }

        function predicatByLower(prop) {
            return function (a, b) {

                if (a[prop] === "null" || a[prop] === null)
                    a[prop] = "";

                if (b[prop] === "null" || b[prop] === null)
                    b[prop] = "";

                if (a[prop].toLowerCase() > b[prop].toLowerCase()) {
                    return 1;
                } else if (a[prop].toLowerCase() < b[prop].toLowerCase()) {
                    return -1;
                }
                return 0;
            }
        }

        $scope.changeSiteActivity = function () {
            $timeout(function () {
                $scope.filteredLength = $($element).find(".sitelist").find("li").length;
            }, 100);
        }

        function listSite() {
            $scope.listData = {};
            $scope.listData = commonFactories.fromPageSites($state.$current.self.name, $state.params);

            ApiFactory.getApiData({
                serviceName: "listSite",
                data: $scope.listData,
                onSuccess: function (data) {
                    $timeout(function () {
                        $("#nodata").css({
                            opacity: 1
                        });
                    }, 2000);
                    if (data.data && data.data.siteList.length === 0) {
                        $scope.filteredLength = 0;
                        $scope.isDataAvailable = false;
                        return;
                    }
                    $rootScope.customeTabCounts.siteCount = data.data.siteList.length;

                    $.each(data.data.siteList, function (key, value) {
                        value.deviceLocationTemp.sort(predicatByLower("location"));
                    });

                    $scope.sites = commonFactories.makeAlphabetsGroup(data.data.siteList, "siteName");

                    if (data.data && data.data.citiesList.length > 0) {
                        $scope.siteCityOptions = $rootScope.aphaNumSort(data.data.citiesList,'city');
                    }
                    if ($scope.siteCityOptions.length > 0) {
                        $scope.city = $scope.siteCityOptions[0].cityId.toString();
                    }
                    if (data.data && data.data.statesList.length > 0) {
                        $scope.siteStateOptions = $rootScope.aphaNumSort(data.data.statesList,'state');
                    }
                    if ($scope.siteStateOptions.length > 0) {
                        $scope.state = $scope.siteStateOptions[0].stateId.toString();
                    }
                    if (data.data && data.data.groupsList.length > 0) {
                    	$scope.siteGroupOptions = $rootScope.aphaNumSort(data.data.groupsList,'groupLabel');
                    }
                    if ($scope.siteGroupOptions.length > 0) {
                        $scope.group = $scope.siteGroupOptions[0].value.toString();
                    }
                    if (data.data && data.data.scheduleList.length > 0) {
                    	$scope.siteScheduleOptions = $rootScope.aphaNumSort(data.data.scheduleList,'schedule');
                    }
                    if ($scope.siteScheduleOptions.length > 0) {
                        $scope.schedule = $scope.siteScheduleOptions[0].scheduleId.toString();
                    }
                    if (data.data && data.data.customerList.length > 0) {
                        $scope.siteCustomerOptions = $rootScope.aphaNumSort(data.data.customerList,'customername');
                    }
                    if ($scope.siteCustomerOptions.length > 0) {
                        $scope.customer = $scope.siteCustomerOptions[0].customerId.toString();
                    }
                    $scope.changeSiteActivity();
                },
                onFailure: function () {}
            });
            $scope.showPlus = false;
            $scope.checkAll = function () {
                $scope.showMain = $scope.showMain == true ? false : true;
                $scope.showPlus = $scope.showPlus == true ? false : true;
                $scope.sites = angular.copy($scope.sites);
            };
            $('.unchk').prop('checked', false);
            $scope.mailChk = false;
            $rootScope.checkboxCountValue = true;
            $scope.checkboxCount = function () {
                var checkValues = $('input[name=subChk]:checked').map(function () {
                    return $(this).val();
                }).get();
                if (checkValues.length <= 0) {
                    $rootScope.checkboxCountValue = true;
                } else {
                    $rootScope.checkboxCountValue = false;
                }
            }

            $scope.viewDeviceProfile = function (deviceLocation, siteID, customerId) {

                if ($state.current.name.indexOf("sites.sitesList") != -1) {
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + siteID;

                }
                $rootScope.deviceProfileData = null;
                $rootScope.deviceProfileData1 = null;
                $rootScope.deviceProfileParentData = null;

                $state.go("devices.devicesList.profile.settings", $rootScope.mergeObject($state.params, {
                    deviceId: deviceLocation.deviceId,
                    customerId: customerId,
                    sourcePage: sourcePage ? sourcePage : $state.params.sourcePage
                }));
            }
            $scope.checkboxCount();
            var rtime;
            var timeout = false;
            var delta = 200;

            function searchLength() {
                rtime = new Date();
                if (timeout === false) {
                    timeout = true;
                    setTimeout(searchEnd, delta);
                }
            };

            function searchEnd() {
                if (new Date() - rtime < delta) {
                    setTimeout(searchEnd, delta);
                } else {
                    timeout = false;
                    $scope.changeSiteActivity();
                }
            }
            $scope.getZonteTypeCSSClass = function (temp) {

                if (temp) {

                    if (temp.op_state == 'OFF' && temp.tstat_mode == 'OFF') {

                        return;
                    }

                    if (temp.op_state == 'OFF') {
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
            $scope.siteActivityFilter = function (obj) {
                switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
                    case "status":
                        return $scope.isActiveCheck(obj);
                    case "city":
                        return obj.siteCity.cityId === Number($scope.city)
                    case "state":
                        return obj.siteState.stateId === Number($scope.state)
                    case "schedule":
                        return obj.schedule.scheduleId === Number($scope.schedule)
                    case "customer":
                        return obj.customerId === Number($scope.customer)
                    case "group":
                        return $scope.isExists(obj.siteGroups)
                    case "ungroup":
                        return $scope.isNotExists(obj.siteGroups)
                }

                $scope.isNotExists = function (data) {
                    var there = false;
                    //for (var i = 0; i < data.length; i++) {
                    if (data.length == 0) {
                        there = true;
                    }
                    //}
                    return there;
                }

                $scope.isActiveCheck = function (data) {
                    if (Number(data.isActive.toString().trim()) === Number($scope.siteActivity.toString().trim())) {
                        return true;
                    }
                    return false;
                }
                $scope.isExists = function (data) {
                    var there = false;
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].value === Number($scope.group)) {
                            there = true;
                        }
                    }
                    return there;
                }
                $scope.search = function (item) {
                    var input = item.siteName + " # " + item.siteCode;
                    if (!$scope.filterSites || (input.toLowerCase().indexOf($scope.filterSites.toLowerCase()) !== -1)) {
                        searchLength();
                        return true;
                    }
                    return false;
                };
                $scope.filterByChange = function () {
                    switch ($scope.filterBy.toLowerCase()) {
                        case "city":
                            $scope.city = $scope.siteCityOptions.length > 0 ? $scope.siteCityOptions[0].cityId.toString() : "";
                            break;
                        case "state":
                            $scope.state = $scope.siteStateOptions.length > 0 ? $scope.siteStateOptions[0].stateId.toString() : "";
                            break;
                        case "group":
                            $scope.group = $scope.siteGroupOptions.length > 0 ? $scope.siteGroupOptions[0].value.toString() : "";
                            break;
                        case "customer":
                            $scope.customer = $scope.siteCustomerOptions.length > 0 ? $scope.siteCustomerOptions[0].customerId.toString() : "";
                            break;
                        case "schedule":
                            $scope.schedule = $scope.siteScheduleOptions.length > 0 ? $scope.siteScheduleOptions[0].scheduleId.toString() : "";
                            break;
                        case "status":
                            break;
                    }
                    $scope.changeSiteActivity();
                };
                return true;
            };
            $scope.groupMerge = function (siteGroups) {
                var gArray = [];
                $.each(siteGroups, function (key, value) {
                    gArray.push(value.lable)
                });
                return gArray.join(", ");
            }
            $scope.addLocations = function (customerId) {

                if (!$rootScope.userDetails.isSuper) {

                    if ($state.$current.self.name === "customers.customersList.getCustomer.sites") {
                        $state.go("customers.customersList.getCustomer.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "sites.sitesList") {
                        $state.go("sites.sitesList.addLocations.manually",
                            $rootScope.mergeObject($state.params, {
                                customerId: customerId
                            }));
                    } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites") {
                        $state.go("customers.customersList.getCustomer.groups.groupInfo.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites") {
                        $state.go("groups.groupList.groupInfo.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites") {
                        $state.go("scheduler.schedulerList.getSchedule.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites") {
                        $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "users.usersList.userProfile.sites") {
                        $state.go("users.usersList.userProfile.sites.addLocations.manually");
                    } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites") {
                        $state.go("users.usersList.userProfile.groups.groupInfo.sites.addLocations.manually");
                    }

                    return;
                }


                if ($state.$current.self.name === "customers.customersList.getCustomer.sites") {
                    $state.go("customers.customersList.getCustomer.sites.addLocations");
                } else if ($state.$current.self.name === "sites.sitesList") {
                    $state.go("sites.sitesList.addLocations", $rootScope.mergeObject($state.params, {
                        customerId: customerId
                    }), {
                        reload: false
                    });
                } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites") {
                    $state.go("customers.customersList.getCustomer.groups.groupInfo.sites.addLocations");
                } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites") {
                    $state.go("groups.groupList.groupInfo.sites.addLocations");
                } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites") {
                    $state.go("scheduler.schedulerList.getSchedule.sites.addLocations");
                } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites") {
                    $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations");
                } else if ($state.$current.self.name === "users.usersList.userProfile.sites") {
                    $state.go("users.usersList.userProfile.sites.addLocations");
                } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites") {
                    $state.go("users.usersList.userProfile.groups.groupInfo.sites.addLocations");
                }
            }
            $scope.changeStatus = function (site, event) {
                if ($(event.target).prop("checked")) {
                    $(event.target).prop("checked", false);
                } else {
                    $(event.target).prop("checked", true);
                }
                var me = event.target;
                $('#modelDialog').on('show.bs.modal', function () {
                    var modal = $(this);
                    if ($(me).prop("checked")) {
                        modal.find('.modal-title').text('Deactivate Site');
                        modal.find('.model-content').text('Are you sure you want to deactivate the site?');
                        return;
                    }
                    modal.find('.modal-title').text('Activate Site');
                    modal.find('.model-content').text('Are you sure you want to activate the site?');
                });
                $("#modelDialog").find('.confirm')[0].targetElement = {
                    me: me,
                    obj: site,
                };
                $("#modelDialog").modal("show");
            };
            $rootScope.accept = function () {
                var btn = $("#modelDialog").find('.confirm')[0].targetElement;
                var status = $(btn.me).prop("checked") ? 0 : 1;
                ApiFactory.getApiData({
                    serviceName: "siteAcitivity",
                    data: {
                        "siteStatus": status,
                        "siteId": btn.obj.siteId
                    },
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            if ($(btn.me).prop("checked")) {
                                $(btn.me).prop("checked", false);
                                $(btn.me).closest("li").addClass("disabled");
                                $.each($scope.sites, function (key, value) {
                                    value.filter(function (element, index) {
                                        $.each(element, function (key, value) {
                                            if (key === 'siteId' && value === btn.obj.siteId) {
                                                element.isActive = "0";
                                            }
                                        });
                                    });
                                });
                                toastr.success(messageFactory.getMessage(data.code));
                            } else {
                                $(btn.me).prop("checked", true);
                                $(btn.me).closest("li").removeClass("disabled");
                                $.each($scope.sites, function (key, value) {
                                    value.filter(function (element, index) {
                                        $.each(element, function (key, value) {
                                            if (key === 'siteId' && value === btn.obj.siteId) {
                                                element.isActive = "1";
                                            }
                                        });
                                    });
                                });
                                toastr.success(messageFactory.getMessage(data.code));
                            }
                            $("#modelDialog").modal("hide");
                        } else {
                            $("#modelDialog").modal("hide");
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            };
            $scope.getSourcePageId = function (siteId) {

                if ($state.current.name.indexOf("sites.sitesList") != -1 && $state.params.sourcePage) {
                    return $state.params.sourcePage.split("-")[0] + "-" + siteId;
                } else {
                    return $state.params.sourcePage;
                }
            }
            $scope.viewSite = function (site) {
                if ($state.current.name.indexOf("sites.sitesList") != -1) {
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + site.siteId;
                    if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                        $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                            siteId: site.siteId,
                            sourcePage: sourcePage,
                            customerId: site.customerId
                        }));
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                        $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                            siteId: site.siteId,
                            sourcePage: sourcePage,
                            customerId: site.customerId
                        }));
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                        $state.go("sites.sitesList.viewSite.users", $rootScope.mergeObject($state.params, {
                            siteId: site.siteId,
                            sourcePage: sourcePage,
                            customerId: site.customerId
                        }));
                    } else {
                        $state.go("sites.sitesList.viewSite.activityLog", $rootScope.mergeObject($state.params, {
                            siteId: site.siteId,
                            sourcePage: sourcePage,
                            customerId: site.customerId
                        }));
                    }
                    return;
                }
                if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                    $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                        siteId: site.siteId,
                        customerId: site.customerId
                    }));
                } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                    $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                        siteId: site.siteId,
                        customerId: site.customerId
                    }));
                } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                    $state.go("sites.sitesList.viewSite.users", $rootScope.mergeObject($state.params, {
                        siteId: site.siteId,
                        customerId: site.customerId
                    }));
                } else {
                    $state.go("sites.sitesList.viewSite.activityLog", $rootScope.mergeObject($state.params, {
                        siteId: site.siteId,
                        customerId: site.customerId
                    }));
                }
            };
            $scope.groupingSites = function () {
                var checkValues = $('input[name=subChk]:checked').map(function () {
                    return $(this).val();
                }).get();
                var checkLabels = $('input[name=subChk]:checked').map(function () {
                    return $(this).attr("label");
                }).get();
                $rootScope.checkValues = checkValues;
                $rootScope.checkLabels = checkLabels;
                if ($state.$current.self.name === "customers.customersList.getCustomer.sites") {
                    $state.go("customers.customersList.getCustomer.sites.groupingSites");
                } else if ($state.$current.self.name === "sites.sitesList") {
                    $state.go("sites.sitesList.groupingSites");
                } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites") {
                    $state.go("scheduler.schedulerList.getSchedule.sites.groupingSites");
                } else if ($state.$current.self.name === "users.usersList.userProfile.sites") {
                    $state.go("users.usersList.userProfile.sites.groupingSites");
                }
            }
        }
    }).controller("groupSiteController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout) {


        $('#modelDialog').on('show.bs.modal', function () {
            var modal = $(this);
            modal.find(".modal-title").text("Grouping Site");
            $(this).off('shown.bs.modal');
        });
        $('#modelDialog').on('hidden.bs.modal', function () {
            $timeout(function () {
                if ($state.$current.self.name === "customers.customersList.getCustomer.sites.groupingSites") {
                    skipChecking = true;
                    $state.go("customers.customersList.getCustomer.sites");
                    skipChecking = false;
                } else if ($state.$current.self.name === "sites.sitesList.groupingSites") {
                    skipChecking = true;
                    $state.go("sites.sitesList");
                    skipChecking = false;
                } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.groupingSites") {
                    skipChecking = true;
                    $state.go("scheduler.schedulerList.getSchedule.sites");
                    skipChecking = false;
                } else if ($state.$current.self.name === "users.usersList.userProfile.sites.groupingSites") {
                    skipChecking = true;
                    $state.go("users.usersList.userProfile.sites");
                    skipChecking = false;
                }
            }, 500)
            $(this).off('hidden.bs.modal');
        });

        $("#modelDialog").modal("show");
        $scope.sitesFormData = {};
        $scope.addgroup = true;
        $scope.groups = [{}];
        $scope.selectedSiteGroupIds = $rootScope.checkValues ? $rootScope.checkValues : [];
        $scope.selectedSiteGroup = $rootScope.checkLabels ? $rootScope.checkLabels : [];
        $scope.selectedLocations = [];

        $.each($scope.selectedSiteGroupIds, function (key, value) {
            $scope.selectedLocations.push({
                siteId: value,
                siteName: $scope.selectedSiteGroup[key]
            });
        });
        $scope.addLocationShow = false;
        $scope.uniqueGroupNameError = false;
        $rootScope.getListGroup = function () {
            if (!$state.params.customerId) {
                $state.params.customerId = $rootScope.userDetails.customers.split(",")[0] ? $rootScope.userDetails.customers.split(",")[0] : 0;
                if ($state.params.customerId >= 0) {
                    listGroup();
                }
            } else {
                listGroup();
            }
        }
        $rootScope.getListGroup();

        function listGroup() {
            var moduleName;
            if ($state.$current.self.name === "customers.customersList.getCustomer.sites.groupingSites") {
                moduleName = "customers";
            } else if ($state.$current.self.name === "sites.sitesList.groupingSites") {
                moduleName = "groups"
            }
            ApiFactory.getApiData({
                serviceName: "groupList",
                data: {
                    moduleId: $state.params.customerId,
                    moduleName: moduleName
                },
                onSuccess: function (data) {
                    $scope.groups = data.data;
                },
                onFailure: function () {}
            });
        }
        $scope.checkGroupName = function () {
            if ($scope.sitesFormData.group === "undefined" || $scope.sitesFormData.group == null) {
                $scope.uniqueGroupNameError = false;
                $rootScope.$broadcast('checkId');
            } else {
                if ($scope.sitesFormData.group !== "") {
                    ApiFactory.getApiData({
                        serviceName: "checkGroupDuplicate",
                        data: {
                            groupName: $scope.sitesFormData.group,
                            customerId: $state.params.customerId
                        },
                        onSuccess: function (data) {
                            if (data.data === "true") {
                                $scope.uniqueGroupNameError = true;
                            } else if (data.data === "error") {
                                toastr.error(messageFactory.getMessage(data.code));
                            } else if (data.data === "false") {
                                $scope.uniqueGroupNameError = false;
                            }
                            $rootScope.$broadcast('checkId');
                        },
                        onFailure: function () {}
                    });
                }
            }
        }
        $scope.$on("checkId", function () {
            if ($scope.uniqueGroupNameError) {
                return;
            }
            if ($scope.selectedSiteGroup && $scope.selectedSiteGroup.length > 0) {
                $scope.addLocationShow = false;
                var serviceName = "updateGroup";
                if ($scope.addgroup) {

                    $scope.sitesFormData.groupName = $scope.sitesFormData.group.groupName;
                    $scope.sitesFormData.groupId = $scope.sitesFormData.group.groupId;
                    $scope.sitesFormData.groupStatusCode = $scope.sitesFormData.group.groupStatusCode;
                    $scope.sitesFormData.updateMode = 3;
                    serviceName = "updateGroup";
                } else {
                    $scope.sitesFormData.groupName = $scope.sitesFormData.group;
                    serviceName = "addGroup";
                    $scope.sitesFormData.groupStatusCode = 1;
                }
                $scope.sitesFormData.selectedLocations = $scope.selectedLocations;
                $scope.sitesFormData.customerId = $state.params.customerId;
                for (var m = 0; m < $scope.sitesFormData.length; m++) {
                    $scope.sitesFormData[$scope.sitesFormData[m]] = $scope.sitesFormData[m];
                }
                $scope.sitesGroupFormData = angular.copy($scope.sitesFormData);

                delete $scope.sitesGroupFormData.group;

                ApiFactory.getApiData({
                    serviceName: serviceName,
                    data: $scope.sitesGroupFormData,
                    onSuccess: function (data) {

                        if (data.data === 2) {
                            toastr.error(messageFactory.getMessage(data.code));
                            return false;
                        } else
                            toastr.success(messageFactory.getMessage(data.code));

                        $('.unchk').prop('checked', false);
                        $scope.mailChk = false;
                        $rootScope.checkboxCountValue = true;
                        $("#modelDialog").modal("hide");
                        $timeout(function () {
                            if ($state.$current.self.name === "customers.customersList.getCustomer.sites") {
                                $state.go("customers.customersList.getCustomer.sites");
                                $rootScope.getListSite();
                            } else if ($state.$current.self.name === "sites.sitesList.groupingSites") {
                                $state.go("sites.sitesList");
                                $rootScope.getListSite();
                            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.groupingSites") {
                                $state.go("scheduler.schedulerList.getSchedule.sites");
                                $rootScope.getListSite();
                            } else if ($state.$current.self.name === "users.usersList.userProfile.sites.groupingSites") {
                                $state.go("users.usersList.userProfile.sites");
                                $rootScope.getListSite();
                            }
                        }, 500);
                    },
                    onFailure: function () {}
                });
            } else if ($scope.selectedSiteGroup.length <= 0) {
                $scope.addLocationShow = true;
                return;
            }
        });
        $scope.addSiteGroup = function (form) {
            if ($scope.selectedSiteGroup && $scope.selectedSiteGroup.length <= 0) {
                $scope.addLocationShow = true;
            } else {
                $scope.addLocationShow = false;
            }
            if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
            if ($scope.addLocationShow) {
                return;
            }
            if (form.$valid && $scope.addgroup) {
                if (!$scope.sitesFormData.group.groupId) {
                    $scope.addSiteGroupForm.group.$error = true;
                    return;
                }
                $rootScope.$broadcast('checkId');
            } else {
                $scope.checkGroupName();
            }
        }
        $scope.addnewgroup = function () {
            $scope.addgroup = false;
            $scope.sitesFormData.group = "";
        }
    }).controller("viewSiteController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, commonFactories, $timeout, cfpLoadingBar) {
        $scope.gotoDevices = function () {
            $state.go("sites.sitesList.viewSite.devices", $state.params);
        }
        $scope.gotoSchedules = function () {
            $state.go("sites.sitesList.viewSite.schedules", $state.params);
        }
        $scope.gotoForecasts = function () {
            $state.go("sites.sitesList.viewSite.forecasts", $state.params);
        }
        $scope.gotoAlerts = function () {
            $state.go("sites.sitesList.viewSite.alerts", $state.params);
        }
        $scope.gotoActivityLog = function () {
            $state.go("sites.sitesList.viewSite.activityLog", $state.params);
        }
        $scope.gotoUsers = function () {
            $state.go("sites.sitesList.viewSite.users", $state.params);
        }
        $scope.weather = {};
        $scope.tabCounts = {};
        $scope.site = {};
        $scope.site.siteHours = [{}];
        $scope.siteCityOptions = [{}];
        $scope.siteGroupOptions = [{}];
        $scope.siteTypeOptions = [{}];
        $scope.siteStateOptions = [];
        $scope.prefrencesOptionsArray = [];
        $scope.otherOptionsArray = [];
        var result = [];
        var occupyHoursresult = [];
        var hVAC = {};
        $scope.hVACUnitOptions = {};
        var hVACUnitOptionsArray = [];
        $scope.hVACUnitOptionsValues = [];
        $scope.data = [];
        $scope.hstep = 1;
        $scope.mstep = 15;

        $scope.changeTimeFormat = function (showMeridian) {
            $scope.getMeridian = showMeridian;
        }
        $scope.changeTimeFormatOccupy = function (showMeridian) {
            $scope.getMeridianOccupy = showMeridian;
        }
        $scope.degreePreferenceOptions = [{
            "label": "Fahrenheit",
            "value": 0
    }, {
            "label": "Celsius",
            "value": 1
	}];
        $scope.buildingTypeOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Standalone",
            "value": 1
    }, {
            "label": "Campus complex",
            "value": 2
    }, {
            "label": "Multi-story",
            "value": 3
    }, {
            "label": "Mall/Strip mall",
            "value": 4
	}];
        $scope.locationOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Roof Top",
            "value": 1
    }, {
            "label": "Closet",
            "value": 2
    }, {
            "label": "Back of the building",
            "value": 3
	}];
        $scope.modemTypeOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "PepWave",
            "value": 1
    }, {
            "label": "Other",
            "value": 2
    }];
        $scope.thermostatListOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Banquet Room",
            "value": 1
    }, {
            "label": "Call Center",
            "value": 2
    }, {
            "label": "Counter",
            "value": 3
    }, {
            "label": "Dining Room",
            "value": 4
    }, {
            "label": "Dining Room Left",
            "value": 5
    }, {
            "label": "Dining Room Right",
            "value": 6
    }, {
            "label": "Dining Room 1",
            "value": 7
    }, {
            "label": "Dining Room 2",
            "value": 8
    }, {
            "label": "Kitchen",
            "value": 9
    }, {
            "label": "Kitchen Front",
            "value": 10
    }, {
            "label": "Kitchen Back",
            "value": 11
    }, {
            "label": "Lobby",
            "value": 12
    }, {
            "label": "Office",
            "value": 13
    }, {
            "label": "Storage Area",
            "value": 14
    }, {

            "label": "Backroom",
            "value": 15
    }, {

            "label": "Front",
            "value": 16
    }, {

            "label": "Back",
            "value": 17
    }, {

            "label": "Right",
            "value": 18
    }, {

            "label": "Left",
            "value": 19
    }, {

            "label": "Restroom",
            "value": 20
    }, {

            "label": "Entry",
            "value": 21
    }, {

            "label": "Main",
            "value": 22
    }, {

            "label": "Other",
            "value": 23
    }];
    $scope.thermostatListOptions = $rootScope.aphaNumSort($scope.thermostatListOptions,'label');
        $rootScope.getSite = function () {
            if (!$state.params.customerId) {
                $state.params.customerId = $rootScope.userDetails.customers.split(",")[0] ? $rootScope.userDetails.customers.split(",")[0] : 0;
                if ($state.params.customerId >= 0) {
                    viewSite();
                }
            } else {
                viewSite();
            }
        };

        $rootScope.getSite();

        function viewSite() {
            $scope.listData = {};
            $scope.listData = commonFactories.fromPageSites($state.$current.self.name, $state.params);

            ApiFactory.getApiData({
                serviceName: "getSite",
                data: $scope.listData,
                onSuccess: function (data) {
                    cfpLoadingBar.start();
                    $scope.site = data.data.siteData;
                    if (!$scope.site.siteId) {
                        $state.go("sites.sitesList");
                    }
                    $scope.weather = data.data.weather;
                    $scope.tabCounts = data.data.tabCounts;
                    $scope.siteTypeOptions = data.data.prepopulateData.siteType;
                    $scope.siteTypeOptions.push({
                        value: 0,
                        lable: "Others"
                    });
                    $scope.siteStateOptions = data.data.prepopulateData.states;
                    $scope.siteGroupOptions = data.data.prepopulateData.siteGroup;
                    $scope.siteHrs = data.data.prepopulateData.siteHrs;
                    cfpLoadingBar.complete();
                    viewSiteInfo();
                },
                onFailure: function () {

                }
            });
        }

        function viewSiteInfo() {
            if ($scope.site.fanOn !== 0) {
                $scope.prefrencesOptionsArray.push("Fan On");
            }
            if ($scope.site.fanAuto !== 0) {
                $scope.prefrencesOptionsArray.push("Fan Auto");
            }
            if ($scope.site.resetHoldMode !== 0) {
                $scope.prefrencesOptionsArray.push("Reset Hold to Off");
            }
            if ($scope.site.isHVACModeToAuto !== 0) {
                $scope.prefrencesOptionsArray.push("Reset HVAC to Auto");
            }
            if ($scope.site.lock === 1) {
                $scope.prefrencesOptionsArray.push("Partial");
            } else if ($scope.site.lock === 0) {
            	$scope.prefrencesOptionsArray.push("Unlock");
            } else if ($scope.site.lock === 2) {
            	$scope.prefrencesOptionsArray.push("Full");
            }
            if ($scope.site.nightlyScheduleDownload !== 0) {
                $scope.prefrencesOptionsArray.push("Nightly Schedule Download");
            }
            if ($scope.site.minSP) {
                $scope.prefrencesOptionsArray.push("Min SP: " + $scope.site.minSP);
            }
            if ($scope.site.maxSP) {
                $scope.prefrencesOptionsArray.push("Max SP: " + $scope.site.maxSP);
            }
            if ($scope.site.siteDistrict) {
                $scope.otherOptionsArray.push($scope.site.siteDistrict);
            }
            if ($scope.site.siteRegion) {
                $scope.otherOptionsArray.push($scope.site.siteRegion);
            }
            if ($scope.site.siteArea) {
                $scope.otherOptionsArray.push($scope.site.siteArea);
            }
            var gArray = [];
            $.each($scope.site.siteGroups, function (key, value) {
                gArray.push(value.lable)
            })
            $scope.gArray = gArray;
            $timeout(function () {
                var el;
                var elOccupy;
                if ($scope.site.siteHrsFormate === 1) {
                    el = document.getElementById('siteHrsFormate12');
                } else {
                    el = document.getElementById('siteHrsFormate24');
                }
                if ($scope.site.siteOccupyHrsFormate === 1) {
                    elOccupy = document.getElementById('occupyHrsFormate12');
                } else {
                    elOccupy = document.getElementById('occupyHrsFormate24');
                }
                angular.element(el).triggerHandler('click');
                angular.element(elOccupy).triggerHandler('click');
            }, 0)
            $.each($scope.site.siteHours, function (key, value) {
                if (value.dayName.toLowerCase() === moment(new Date()).format("dddd").toLowerCase()) {
                    $scope.todayHours = value.openHrs + " to " + value.closeHrs;
                    return;
                }
            });
            var todayOccupyHoursTemp;
            $.each($scope.site.occupyHours, function (key, value) {
                if (value.dayName.toLowerCase() === moment(new Date()).format("dddd").toLowerCase()) {
                	todayOccupyHoursTemp = value.openHrs + " to " + value.closeHrs;
                    return;
                }
            });
            if (todayOccupyHoursTemp !== $scope.todayHours) {
            	$scope.todayOccupyHours = todayOccupyHoursTemp;
            }
            for (var i in $scope.site.siteHours) {
                $scope.site.siteHours[i].dayOfWeekChecked = true;
                if ($scope.site.siteHrsFormate === 1) {
                    $scope.site.siteHours[i].openHrs = split12($scope.site.siteHours[i].openHrs);
                    $scope.site.siteHours[i].closeHrs = split12($scope.site.siteHours[i].closeHrs);
                } else {
                    $scope.site.siteHours[i].openHrs = split24($scope.site.siteHours[i].openHrs);
                    $scope.site.siteHours[i].closeHrs = split24($scope.site.siteHours[i].closeHrs);
                }
                result[$scope.site.siteHours[i].dayOfWeek] = $scope.site.siteHours[i];
            };
            $scope.site.siteHours = result;

            for (var k in $scope.site.occupyHours) {
                $scope.site.occupyHours[k].dayOfWeekCheckedSame = true;
                if ($scope.site.siteOccupyHrsFormate === 1) {
                    $scope.site.occupyHours[k].openHrs = split12($scope.site.occupyHours[k].openHrs);
                    $scope.site.occupyHours[k].closeHrs = split12($scope.site.occupyHours[k].closeHrs);
                } else {
                    $scope.site.occupyHours[k].openHrs = split24($scope.site.occupyHours[k].openHrs);
                    $scope.site.occupyHours[k].closeHrs = split24($scope.site.occupyHours[k].closeHrs);
                }
                occupyHoursresult[$scope.site.occupyHours[k].dayOfWeek] = $scope.site.occupyHours[k];
            };
            $scope.site.occupyHours = occupyHoursresult;

            if ($scope.site.rTUList.length > 0) {
                $.each($scope.site.rTUList, function (key, value) {
                    if (value.unit) {
                        hVAC[key] = value.unit.trim();
                        hVACUnitOptionsArray.push(value.unit.trim())
                    }
                });
                $scope.hVACUnitOptionsValues = hVACUnitOptionsArray;
                $scope.hVACUnitOptions = hVAC;
            }
            if ($scope.site.thermostatList.length > 0) {
                $.each($scope.site.thermostatList, function (key, value) {
                    $scope.data.push(value.hVACUnit.trim())
                });
            }

            function split12(hrs) {
                return moment(hrs, ["hh:mm A"])
            }

            function split24(hrs) {
                return moment(hrs, ["HH:mm"])
            }
        }
        $scope.editLocations = function () {
            if ($state.$current.self.name.indexOf("customers.customersList.getCustomer.sites.viewSite") != -1) {
                $state.go("customers.customersList.getCustomer.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("sites.sitesList.viewSite") != -1) {
                $state.go("sites.sitesList.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("customers.customersList.getCustomer.groups.groupInfo.sites.viewSite") != -1) {
                $state.go("customers.customersList.getCustomer.groups.groupInfo.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("groups.groupList.groupInfo.sites.viewSite") != -1) {
                $state.go("groups.groupList.groupInfo.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("scheduler.schedulerList.getSchedule.sites.viewSite") != -1) {
                $state.go("scheduler.schedulerList.getSchedule.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("scheduler.schedulerList.getSchedule.groups.groupInfo.sites.viewSite") != -1) {
                $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("users.usersList.userProfile.sites.viewSite") != -1) {
                $state.go("users.usersList.userProfile.sites.viewSite.editLocations")
            } else if ($state.$current.self.name.indexOf("users.usersList.userProfile.groups.groupInfo.sites.viewSite") != -1) {
                $state.go("users.usersList.userProfile.groups.groupInfo.sites.viewSite.editLocations")
            };
        };
    }).controller("editSiteController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state, toastr, commonFactories, $filter, $timeout) {
        $scope.site = {};
        $scope.site.siteHours = [{}];
        $scope.opt = {};
        $scope.siteCityOptions = [{}];
        $scope.siteGroupOptions = [{}];
        $scope.siteTypeOptions = [{}];
        $scope.siteStateOptions = [];

        $scope.statusOptions = [
            {
                label: "New",
                id: 2
        }, {
                label: "Survey",
                id: 3
        }, {
                label: "Install",
                id: 4

        }, {
                label: "Waiting",
                id: 5
        }, {

                label: "New ownership",
                id: 6

        }, {
                label: "Closed",
                id: 7
        }, {
                label: "Active",
                id: 1
        }, {
                label: "Inactive",
                id: 0
        }];

        $scope.errors = {};
        $scope.errors.internalId = false;
        $scope.errors.zipcode = false;

        var result = [];
        var occupyHoursresult = [];
        var hVAC = {};
        $scope.hVACUnitOptions = {};
        var hVACUnitOptionsArray = [];
        $scope.hVACUnitOptionsValues = [];
        $scope.data = [];
        $scope.hstep = 1;
        $scope.mstep = 15;
        var siteGroupTemp = [];
        $scope.changeTimeFormat = function (showMeridian) {
            $scope.getMeridian = showMeridian;
        }
        $scope.changeTimeFormatOccupy = function (showMeridian) {
            $scope.getMeridianOccupy = showMeridian;
        }
        $scope.degreePreferenceOptions = [{
            "label": "Fahrenheit",
            "value": 0
    }, {
            "label": "Celsius",
            "value": 1
	}];
        $scope.buildingTypeOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Standalone",
            "value": 1
    }, {
            "label": "Campus complex",
            "value": 2
    }, {
            "label": "Multi-story",
            "value": 3
    }, {
            "label": "Mall/Strip mall",
            "value": 4
	}];
        $scope.locationOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Roof Top",
            "value": 1
    }, {
            "label": "Closet",
            "value": 2
    }, {
            "label": "Back of the building",
            "value": 3
	}];
        $scope.modemTypeOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "PepWave",
            "value": 1
    }, {
            "label": "Other",
            "value": 2
    }];
        $scope.thermostatListOptions = [{
            "label": "-- choose an option --",
            "value": 0
	}, {
            "label": "Banquet Room",
            "value": 1
    }, {
            "label": "Call Center",
            "value": 2
    }, {
            "label": "Counter",
            "value": 3
    }, {
            "label": "Dining Room",
            "value": 4
    }, {
            "label": "Dining Room Left",
            "value": 5
    }, {
            "label": "Dining Room Right",
            "value": 6
    }, {
            "label": "Dining Room 1",
            "value": 7
    }, {
            "label": "Dining Room 2",
            "value": 8
    }, {
            "label": "Kitchen",
            "value": 9
    }, {
            "label": "Kitchen Front",
            "value": 10
    }, {
            "label": "Kitchen Back",
            "value": 11
    }, {
            "label": "Lobby",
            "value": 12
    }, {
            "label": "Office",
            "value": 13
    }, {
            "label": "Storage Area",
            "value": 14
    }, {

            "label": "Backroom",
            "value": 15
    }, {

            "label": "Front",
            "value": 16
    }, {

            "label": "Back",
            "value": 17
    }, {

            "label": "Right",
            "value": 18
    }, {

            "label": "Left",
            "value": 19
    }, {

            "label": "Restroom",
            "value": 20
    }, {

            "label": "Entry",
            "value": 21
    }, {

            "label": "Main",
            "value": 22
    }, {

            "label": "Other",
            "value": 23
    }];
        $scope.thermostatListOptions = $rootScope.aphaNumSort($scope.thermostatListOptions,'label');
        $.each($($element).find("form").find("input,select,textarea"), function () {
            if ($(this).attr("name")) {
                $scope.site[$(this).attr("name")] = "";
            }
        });
        $rootScope.editSite = function () {
            if (!$state.params.customerId) {
                $state.params.customerId = $rootScope.userDetails.customers.split(",")[0] ? $rootScope.userDetails.customers.split(",")[0] : 0;
                if ($state.params.customerId >= 0) {
                    viewSite();
                }
            } else {
                viewSite();
            }
        };
        $rootScope.editSite();

        function viewSite() {
            $scope.listData = {};
            $scope.listData = commonFactories.fromPageSites($state.$current.self.name, $state.params);

            ApiFactory.getApiData({
                serviceName: "getSite",
                data: $scope.listData,
                onSuccess: function (data) {
                    $rootScope.site = data.data.siteData;
                    if (!$rootScope.site.siteId) {
                        $state.go("sites.sitesList");
                    }
                    $rootScope.siteTypeOptions = data.data.prepopulateData.siteType;
                    $rootScope.siteTypeOptions.push({
                        value: 0,
                        lable: "Others"
                    });
                    $rootScope.siteStateOptions = data.data.prepopulateData.states;
                    $rootScope.siteGroupOptions = data.data.prepopulateData.siteGroup;
                    $rootScope.siteHrs = data.data.prepopulateData.siteHrs;
                    viewSiteInfo();
                },
                onFailure: function (data) {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            });
        }

        function viewSiteInfo() {
            $scope.site = $.extend($scope.site, $rootScope.site);
            $scope.siteTypeOptions = $rootScope.siteTypeOptions;
            $scope.siteStateOptions = $rootScope.siteStateOptions;
            $scope.siteGroupOptions = $rootScope.siteGroupOptions;
            $scope.siteHrs = $rootScope.siteHrs;
            $scope.site.nightlyScheduleDownload = $scope.site.nightlyScheduleDownload === 1 ? true : false;
            $scope.site.isHVACModeToAuto = $scope.site.isHVACModeToAuto === 1 ? true : false;
            $scope.site.resetHoldMode = $scope.site.resetHoldMode === 1 ? true : false;
            
            if ($scope.site.fanOn === 1) {
                $scope.site.fanOn = 1;
            }
            if ($scope.site.fanAuto === 1) {
                $scope.site.fanOn = 0;
            }

            if ($scope.site.sameAsStore && $scope.site.siteHours.length > 0) {
                $scope.sameAsStore = true;
            } else {
                $scope.sameAsStore = false;
            }
            $scope.open1 = function () {
                $scope.popup1.opened = true;
            };

            $scope.popup1 = {
                opened: false
            };
            if ($scope.site.surveyDate) {
                $scope.site.surveyDate = new Date($scope.site.surveyDate.split("/")[2], $scope.site.surveyDate.split("/")[0] - 1, $scope.site.surveyDate.split("/")[1]);
            } else {
                $scope.site.surveyDate = undefined;
            }
            /* if ($scope.site.siteState.stateId) {
                 ApiFactory.getApiData({
                     serviceName: "getCities",
                     data: {
                         "stateId": $scope.site.siteState.stateId
                     },
                     onSuccess: function (data) {
                         $scope.siteCityOptions = data.data;
                     },
                     onFailure: function () {}
                 });
             }*/
            $scope.opt = {
                city: {
                    "cityId": $scope.site.siteCity.cityId,
                    "cityName": $scope.site.siteCity.cityName
                },
                state: {
                    "stateId": $scope.site.siteState.stateId,
                    "stateName": $scope.site.siteState.stateName
                },
                group: $scope.site.siteGroups
            };

            $scope.site.siteStateName = $scope.site.siteState.stateName;
            $scope.site.siteCityName = $scope.site.siteCity.cityName;
            $scope.site.siteState = $scope.site.siteState.stateId;
            $scope.site.siteCity = $scope.site.siteCity.cityId;

            if ($scope.site.siteType.value !== -1) {
                $scope.opt.selected = {
                    lable: $scope.site.siteType.lable.toString(),
                    value: Number($scope.site.siteType.value)
                };
            } else {
                $scope.opt.selected = null;
            }

            $.each($scope.site.siteGroups, function (key, value) {
                siteGroupTemp.push(value.value)
            });
            $timeout(function () {
                var el;
                var elOccupy;
                if ($scope.site.siteHrsFormate === 1) {
                    el = document.getElementById('siteHrsFormate12');
                } else {
                    el = document.getElementById('siteHrsFormate24');
                }
                if ($scope.site.siteOccupyHrsFormate === 1) {
                    elOccupy = document.getElementById('occupyHrsFormate12');
                } else {
                    elOccupy = document.getElementById('occupyHrsFormate24');
                }
                angular.element(el).triggerHandler('click');
                angular.element(elOccupy).triggerHandler('click');
            }, 0)
            for (var i in $scope.site.siteHours) {
                $scope.site.siteHours[i].dayOfWeekChecked = true;
                if ($scope.site.siteHrsFormate === 1) {
                    $scope.site.siteHours[i].openHrs = split12($scope.site.siteHours[i].openHrs);
                    $scope.site.siteHours[i].closeHrs = split12($scope.site.siteHours[i].closeHrs);
                } else {
                    $scope.site.siteHours[i].openHrs = split24($scope.site.siteHours[i].openHrs);
                    $scope.site.siteHours[i].closeHrs = split24($scope.site.siteHours[i].closeHrs);
                }
                result[$scope.site.siteHours[i].dayOfWeek] = $scope.site.siteHours[i];
            };
            $scope.site.siteHours = result;
            for (var k in $scope.site.occupyHours) {
                $scope.site.occupyHours[k].dayOfWeekCheckedSame = true;
                if ($scope.site.siteOccupyHrsFormate === 1) {
                    $scope.site.occupyHours[k].openHrs = split12($scope.site.occupyHours[k].openHrs);
                    $scope.site.occupyHours[k].closeHrs = split12($scope.site.occupyHours[k].closeHrs);
                } else {
                    $scope.site.occupyHours[k].openHrs = split24($scope.site.occupyHours[k].openHrs);
                    $scope.site.occupyHours[k].closeHrs = split24($scope.site.occupyHours[k].closeHrs);
                }
                occupyHoursresult[$scope.site.occupyHours[k].dayOfWeek] = $scope.site.occupyHours[k];
            };
            $scope.site.occupyHours = occupyHoursresult;

            if ($scope.site.rTUList.length > 0) {
                $.each($scope.site.rTUList, function (key, value) {
                    if (value.unit) {
                        hVAC[key] = value.unit.trim();
                        hVACUnitOptionsArray.push(value.unit.trim())
                    }
                });
                $scope.hVACUnitOptionsValues = hVACUnitOptionsArray;
                $scope.hVACUnitOptions = hVAC;
            }
            if ($scope.site.thermostatList.length > 0) {
                $.each($scope.site.thermostatList, function (key, value) {
                    $scope.data.push(value.hVACUnit.trim())
                });
            }
            $scope.clearStoreDate = function (index, value) {
                if (('' + index === '0' || index) && '' + value === '0') {
                    delete $scope.site.siteHours[index]
                }
            };
            $scope.clearOccupyDate = function (index, value) {
                if (('' + index === '0' || index) && '' + value === '0') {
                    delete $scope.site.occupyHours[index]
                }
            };
            var countStores = 0;
            var countStoresOcc = 0;
            var lengthStore = $scope.site.siteHours.length;
            var lengthOccupy = $scope.site.occupyHours.length;
            $scope.copyStoreHoursEdit = function () {
                if (lengthStore === 0) {
                    var resultAdds = [];
                    var closehours;
                    var openhours;
                    if (!angular.isUndefined($scope.site.siteHours)) {
                        for (var n in $scope.site.siteHours) {
                            if ($scope.site.siteHours[n].dayOfWeek !== 0 && $scope.site.siteHours[n].openHrs && $scope.site.siteHours[n].closeHrs) {
                                countStores++;
                                openhours = $scope.site.siteHours[n].openHrs;
                                closehours = $scope.site.siteHours[n].closeHrs;
                            }
                        }
                    }
                    if (countStores === 1) {
                        for (var j = 1; j < $scope.siteHrs.length + 1; j++) {
                            resultAdds[j] = {};
                        };
                        for (var p = 1; p < $scope.site.siteHours.length; p++) {
                            resultAdds[Number($scope.site.siteHours[p].dayOfWeek)] = $scope.site.siteHours[p];
                        };
                        for (var g = 1; g < resultAdds.length; g++) {
                            if (typeof resultAdds[g].dayOfWeek === "undefined") {
                                resultAdds.splice(g, 1, {
                                    dayOfWeek: g,
                                    closeHrs: closehours,
                                    openHrs: openhours,
                                    dayOfWeekChecked: true
                                });
                            }
                        }
                        $scope.site.siteHours = resultAdds;
                    }
                }
            };
            $scope.copySOccupyHoursEdit = function () {
                if (lengthOccupy === 0) {
                    var resultAdds = [];
                    var closehours;
                    var openhours;
                    if (!angular.isUndefined($scope.site.occupyHours)) {
                        for (var n in $scope.site.occupyHours) {
                            if ($scope.site.occupyHours[n].dayOfWeek !== 0 && $scope.site.occupyHours[n].openHrs && $scope.site.occupyHours[n].closeHrs) {
                                countStoresOcc++;
                                openhours = $scope.site.occupyHours[n].openHrs;
                                closehours = $scope.site.occupyHours[n].closeHrs;
                            }
                        }
                    }
                    if (countStoresOcc === 1) {
                        for (var j = 1; j < $scope.siteHrs.length + 1; j++) {
                            resultAdds[j] = {};
                        };
                        for (var p = 1; p < $scope.site.occupyHours.length; p++) {
                            resultAdds[Number($scope.site.occupyHours[p].dayOfWeek)] = $scope.site.occupyHours[p];
                        };
                        for (var g = 1; g < resultAdds.length; g++) {
                            if (typeof resultAdds[g].dayOfWeek === "undefined") {
                                resultAdds.splice(g, 1, {
                                    dayOfWeek: g,
                                    closeHrs: closehours,
                                    openHrs: openhours,
                                    dayOfWeekCheckedSame: true
                                });
                            }
                        }
                        $scope.site.occupyHours = resultAdds;
                    }
                }
            };

            function split12(hrs) {
                return new Date(moment(hrs, ["hh:mm A"]))
            }

            function split24(hrs) {
                return new Date(moment(hrs, ["HH:mm"]))
            }
            $scope.changeState = function (selectedState) {
                if (selectedState) {
                    ApiFactory.getApiData({
                        serviceName: "getCities",
                        data: selectedState,
                        onSuccess: function (data) {
                            $scope.opt.city = undefined;
                            $scope.siteCityOptions = data.data;
                        },
                        onFailure: function () {}
                    });
                } else {
                    $scope.siteCityOptions = [];
                    $scope.opt.city = undefined;
                }
            };
            $scope.changeCity = function (selectedCity) {
                if (selectedCity) {
                    $scope.checkzipcode();
                }
                for (var i = 0; i < $scope.siteStateOptions.length; i++) {
                    if (selectedCity.stateId == $scope.siteStateOptions[i].stateId) {
                        $scope.opt.state = {};
                        $scope.opt.state = $scope.siteStateOptions[i];
                    }
                }
            };
            $scope.sameAsStoreHour = function () {
                var resultTemp = [];
                if ($scope.sameAsStore) {
                    $scope.site.siteOccupyHrsFormate = $scope.site.siteHrsFormate;
                    $timeout(function () {
                        var elOccupy;
                        if ($scope.site.siteHrsFormate === 1) {
                            elOccupy = document.getElementById('occupyHrsFormate12');
                        } else {
                            elOccupy = document.getElementById('occupyHrsFormate24');
                        }
                        angular.element(elOccupy).triggerHandler('click');
                    }, 0)
                    for (var i in $scope.site.siteHours) {
                        resultTemp[$scope.site.siteHours[i].dayOfWeek] = angular.copy($scope.site.siteHours[i])
                        resultTemp[$scope.site.siteHours[i].dayOfWeek].dayOfWeekCheckedSame = true;
                        delete resultTemp[$scope.site.siteHours[i].dayOfWeek].dayOfWeekChecked;
                    }
                    $scope.site.occupyHours = resultTemp;
                } else {
                    $scope.site.occupyHours = [];
                }
            }
            if ($scope.site.siteHours.length > 0 && $scope.sameAsStore) {
                $scope.sameAsStore = true;
                $scope.sameAsStoreHour();
            }
            $scope.changeOccupyHorsData = function () {
                var count = 0;
                var storeCount = 0;
                if ($scope.site.siteHours.length > 0 && $scope.site.occupyHours.length > 0 && ($scope.site.siteHours.length == $scope.site.occupyHours.length)) {
                	for (var j in $scope.site.siteHours) {
                        if ($scope.site.siteHours[j].openHrs && $scope.site.siteHours[j].closeHrs) {
                        	storeCount++;
                        }
                    };
                    for (var k in $scope.site.occupyHours) {
                        if ($scope.site.siteOccupyHrsFormate === 1) {
                            var openOccupy = split12($scope.site.occupyHours[k].openHrs);
                            var closeOccupy = split12($scope.site.occupyHours[k].closeHrs);
                            var openStore = split12($scope.site.siteHours[k].openHrs);
                            var closeStore = split12($scope.site.siteHours[k].closeHrs);
                            if ((moment(openOccupy).isSame(openStore)) && moment(closeOccupy).isSame(closeStore)) {
                                count++;
                            }
                        } else {
                            var openOccupys = split24($scope.site.occupyHours[k].openHrs);
                            var closeOccupys = split24($scope.site.occupyHours[k].closeHrs);
                            var openStores = split24($scope.site.siteHours[k].openHrs);
                            var closeStores = split24($scope.site.siteHours[k].closeHrs);
                            if (moment(openOccupys).isSame(openStores) && moment(closeOccupys).isSame(closeStores)) {
                                count++;
                            }
                        }
                    };
                    if (storeCount == count) {
                        $scope.sameAsStore = true;
                    } else {
                        $scope.sameAsStore = false;
                    }
                }
            }
            $scope.changeOStoreHorsData = function () {
                var resultTemp = [];
                if ($scope.site.siteHours.length > 0 && $scope.sameAsStore) {
                    $scope.site.siteOccupyHrsFormate = $scope.site.siteHrsFormate;
                    $timeout(function () {
                        var elOccupy;
                        if ($scope.site.siteHrsFormate === 1) {
                            elOccupy = document.getElementById('occupyHrsFormate12');
                        } else {
                            elOccupy = document.getElementById('occupyHrsFormate24');
                        }
                        angular.element(elOccupy).triggerHandler('click');
                    }, 0)
                    for (var i in $scope.site.siteHours) {
                        resultTemp[$scope.site.siteHours[i].dayOfWeek] = angular.copy($scope.site.siteHours[i])
                        resultTemp[$scope.site.siteHours[i].dayOfWeek].dayOfWeekCheckedSame = true;
                        delete resultTemp[$scope.site.siteHours[i].dayOfWeek].dayOfWeekChecked;
                    }
                    $scope.site.occupyHours = resultTemp;
                }
            }
            $scope.addRTU = function () {
                $scope.site.rTUList.push({
                    unit: ($scope.site.rTUList.length + 1) + "",
                    hVACUnit: "",
                    location: 0,
                    heating: "",
                    cooling: ""
                });
                hVAC = {};
                $.each($scope.site.rTUList, function (key, value) {
                	if (value.unit) {
                		hVAC[key] = value.unit.trim();
                	}
                });
                hVACUnitOptionsArray = [];
                $.each(hVAC, function (key, value) {
                    hVACUnitOptionsArray.push(value.trim())
                });
                $scope.hVACUnitOptions = hVAC;
                $scope.hVACUnitOptionsValues = hVACUnitOptionsArray;
            }
            $scope.addThermostat = function () {
                $scope.site.thermostatList.push({
                    hVACUnit: "",
                    unit: ($scope.site.thermostatList.length + 1) + "",
                    locationImage: "",
                    locationType: 0,
                    otherLocation:"",
                    spaceEnough: 0,
                    make: "",
                    model: "",
                    wiringConfigThermostat: "",
                    wiringThermostatImage: "",
                    rCAndCPower: 0,
                    cWireAttached: 0,
                    noCWireAttached: 0,
                    automatedSchedule: 0,
                    automatedScheduleNote: "",
                    locationOfRemoteSensor: "",
                    validateSensor: "",
                    wiringConfigSensor: "",
                    wiringSensorImage: ""
                });
            }
            $scope.checkSiteInternalId = function () {
                if ($scope.site.siteInternalId) {
                    ApiFactory.getApiData({
                        serviceName: "checkSiteInternalId",
                        async: true,
                        data: {
                            "siteId": $scope.site.siteId,
                            "siteInternalId": $scope.site.siteInternalId,
                            "customerId": $state.params.customerId
                        },
                        onSuccess: function (data) {
                            if (Number(data.data) > 0) {
                                $scope.siteInternalIdError = messageFactory.getMessage(data.code);
                                $scope.errors.internalId = true;
                            } else {
                                $scope.errors.internalId = false;
                            }
                            $rootScope.$broadcast('gotEditInternalId');
                        },
                        onFailure: function () {}
                    });
                } else {
                    $scope.errors.internalId = false;
                    $rootScope.$broadcast('gotEditInternalId');
                }
            };
            $scope.checkzipcode = function () {
                $scope.makeDisable = false;
                if (!angular.isUndefined($scope.site.siteZipCode) && $scope.site.siteZipCode.trim() !== "" && Math.round($scope.site.siteZipCode) === 0) {
                    $scope.siteZipcodeError = "Zip code should not be zero.";
                    $scope.errors.zipcode = true;
                } else {
                    $scope.siteZipcodeError = "";
                    $scope.errors.zipcode = false;
                    if (!angular.isUndefined($scope.site.siteZipCode) && $scope.site.siteZipCode.trim() !== "" && $scope.opt.state && $scope.opt.city) {
                        ApiFactory.getApiData({
                            serviceName: "checkZipcode",
                            async: true,
                            data: {
                                "zipCode": $scope.site.siteZipCode,
                                "stateId": $scope.opt.state.stateId.toString(),
                                "city": $scope.opt.city.cityName.toString()
                            },
                            onSuccess: function (data) {
                                if (data.status.toLowerCase() === "success") {
                                    $scope.errors.zipcode = false;
                                    $scope.site.siteTimeZone = data.data.timeZone;
                                } else {
                                    $scope.siteZipcodeError = messageFactory.getMessage(data.code);
                                    $scope.errors.zipcode = true;
                                    if ($scope.errors.zipcode) {
                                        $($element).find(".scroll").animate({
                                            scrollTop: 0
                                        });
                                        return;
                                    }
                                }
                            },
                            onFailure: function () {}
                        });
                    } else {
                        $scope.site.siteTimeZone = "";
                    }
                }
            };
            $scope.$on("getEditTimeZoneValue", function () {
                ApiFactory.getApiData({
                    serviceName: "getTimeZoneSite",
                    async: true,
                    data: {
                        "zipCode": $scope.site.siteZipCode
                    },
                    onSuccess: function (data) {
                        $scope.site.siteTimeZone = data.data.timeZone;
                    },
                    onFailure: function () {}
                });
            });
            $scope.$on("gotEditInternalId", function () {
                if ($scope.errors.internalId) {
                    $($element).find(".scroll").animate({
                        scrollTop: 0
                    });
                    return;
                }
                if ($scope.errors.zipcode) {
                    $($element).find(".scroll").animate({
                        scrollTop: 0
                    });
                    return;
                }
                var arr = [];
                $.each($scope.site.rTUList, function (key, value) {
                    if (value.unit.trim()) {
                        arr.push(value.unit.trim());
                    }
                });
                if (arr.length > 0) {
                    var sorted_arr = arr.slice().sort();

                    var results = [];
                    for (var i = 0; i < arr.length - 1; i++) {
                        if (sorted_arr[i + 1] === sorted_arr[i]) {
                            results.push(sorted_arr[i]);
                        }
                    }
                    if (results.length > 0) {
                        return false;
                    }
                }
                var groupArray = [];
                var difference = [];
                $.each($scope.opt.group, function (key, value) {
                    groupArray.push(value.value);
                });
                jQuery.grep(siteGroupTemp, function (el) {
                    if (jQuery.inArray(el, groupArray) === -1) difference.push(el);
                });
                if ($scope.opt.selected !== null && !angular.isUndefined($scope.opt.selected) && $scope.opt.selected.value !== -1) {
                    $scope.site.siteType = $scope.opt.selected.value.toString();
                } else {
                    $scope.site.siteType = "-1";
                }
                /*$scope.site.siteCity = $scope.opt.city.cityId.toString();
                $scope.site.siteState = $scope.opt.state.stateId.toString();
                $scope.site.siteStateName = $scope.opt.state.stateName.toString();
                $scope.site.siteCityName = $scope.opt.city.cityName.toString();*/

                $scope.site.siteGroups = groupArray;
                $scope.site.deleteSiteGroups = difference;
                $scope.siteData = angular.copy($scope.site);
                
                $scope.siteData.surveyDate = $scope.siteData.surveyDate ? moment($scope.siteData.surveyDate).format("MM/DD/YYYY") : '';
                
                if ($scope.siteData.fanOn == 1) {
	            	$scope.siteData.fanOn = 1;
	                $scope.siteData.fanAuto = 0;
	            } else {
	            	$scope.siteData.fanOn = 0;
	                $scope.siteData.fanAuto = 1;
	            }

                if ($scope.sameAsStore) {
                    $scope.siteData.sameAsStore = 1;
                } else {
                    $scope.siteData.sameAsStore = 0;
                }
                $scope.siteData.isHVACModeToAuto = $scope.siteData.isHVACModeToAuto === true ? 1 : 0;
                $scope.siteData.resetHoldMode = $scope.siteData.resetHoldMode === true ? 1 : 0;
                $scope.siteData.nightlyScheduleDownload = $scope.siteData.nightlyScheduleDownload === true ? 1 : 0;

                if (!angular.isUndefined($scope.site.siteHours)) {
                    var jsonObj = [];
                    var obj;
                    for (var n in $scope.site.siteHours) {
                        if (!angular.isUndefined($scope.site.siteHours[n].dayOfWeek) && $scope.site.siteHours[n].dayOfWeek !== 0 && $scope.site.siteHours[n].openHrs && $scope.site.siteHours[n].closeHrs) {
                            obj = {};
                            obj["dayOfWeek"] = $scope.site.siteHours[n].dayOfWeek;
                            obj["openHrs"] = $scope.site.siteHrsFormate === 0 ? moment($scope.site.siteHours[n].openHrs).format("HH:mm") : moment($scope.site.siteHours[n].openHrs).format("hh:mm A");
                            obj["closeHrs"] = $scope.site.siteHrsFormate === 0 ? moment($scope.site.siteHours[n].closeHrs).format("HH:mm") : moment($scope.site.siteHours[n].closeHrs).format("hh:mm A");
                            jsonObj.push(obj);
                        }
                    }
                    $scope.siteData.siteHours = jsonObj;
                }
                if (!angular.isUndefined($scope.site.occupyHours)) {
                    var jsonoccupyHours = [];
                    var objoccupyHours;
                    for (var j in $scope.site.occupyHours) {
                        if (!angular.isUndefined($scope.site.occupyHours[j].dayOfWeek) && $scope.site.occupyHours[j].dayOfWeek !== 0 && $scope.site.occupyHours[j].openHrs && $scope.site.occupyHours[j].closeHrs) {
                            objoccupyHours = {};
                            objoccupyHours["dayOfWeek"] = $scope.site.occupyHours[j].dayOfWeek;
                            objoccupyHours["openHrs"] = $scope.site.siteOccupyHrsFormate === 0 ? moment($scope.site.occupyHours[j].openHrs).format("HH:mm") : moment($scope.site.occupyHours[j].openHrs).format("hh:mm A");
                            objoccupyHours["closeHrs"] = $scope.site.siteOccupyHrsFormate === 0 ? moment($scope.site.occupyHours[j].closeHrs).format("HH:mm") : moment($scope.site.occupyHours[j].closeHrs).format("hh:mm A");
                            jsonoccupyHours.push(objoccupyHours);
                        }
                    }
                    $scope.siteData.occupyHours = jsonoccupyHours;
                }
                for (var g = 0; g < $scope.siteData.thermostatList.length; g++) {
                    $scope.siteData.thermostatList[g].hVACUnit = $scope.data[g] ? $scope.data[g] : "";
                }
                for (var m = 0; m < $scope.siteData.length; m++) {
                    $scope.siteData[$scope.siteData[m]] = $scope.siteData[m];
                }
                ApiFactory.getApiData({
                    serviceName: "updateSite",
                    data: $scope.siteData,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            toastr.success(messageFactory.getMessage(data.code));

                            var siteId = $state.params.siteId;
                            var sourcePage = $state.params.sourcePage;


                            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {

                                $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                                    siteId: siteId,
                                    sourcePage: sourcePage
                                }));

                            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {

                                $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                                    siteId: siteId,
                                    sourcePage: sourcePage
                                }));

                            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                                $state.go("sites.sitesList.viewSite.users", $rootScope.mergeObject($state.params, {
                                    siteId: siteId,
                                    sourcePage: sourcePage
                                }));
                            } else {
                                $state.go("sites.sitesList.viewSite.activityLog", $rootScope.mergeObject($state.params, {
                                    siteId: siteId,
                                    sourcePage: sourcePage
                                }));
                            }





                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            });

            $scope.getStateCityValidate = function () {

                if (!$scope.site.siteStateName) {
                    $(".site-state-name")[0].focus();
                    return;
                }
                if (!$scope.site.siteCityName) {
                    $(".site-city-name")[0].focus();
                }

            }

            $scope.createLocation = function (form) {
                if (!form.$valid) {

                    if (!form.squareFootage.$valid) {
                        $scope.showMoreDetails = true;
                        $scope.showSiteInfo = true;
                        angular.element("[name='" + form.squareFootage.$name + "']").find('.ng-invalid:visible:first').focus();
                    }

                    if (!form.localContactEmail.$valid) {
                        $scope.showMoreDetails = true;
                        $scope.showSiteContact = true;
                        angular.element("[name='" + form.localContactEmail.$name + "']").find('.ng-invalid:visible:first').focus();
                    }

                    if (form.thermostatForm && !form.thermostatForm.$valid) {
                        $scope.showMoreDetails = true;
                        $scope.showThermostat = true;
                        angular.element("[name='" + form.thermostatForm.$name + "']").find('.ng-invalid:visible:first').focus();
                    }
                    angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();

                    return false;
                }
                if (form.$valid) {
                    getValidateZipCode(function () {
                        $scope.checkSiteInternalId();
                    })
                }
            };
            $scope.removeRTUBlock = function (event) {
                var $target = event.target;
                var index = $target.getAttribute('index');
                if ($scope.site.rTUList.length > 0) {
                    $scope.site.rTUList.splice(index, 1)
                    removeKeyStartsWith(hVAC, index)
                    hVACUnitOptionsArray = [];
                    $.each(hVAC, function (key, value) {
                        hVACUnitOptionsArray.push(value.trim())
                    });
                    $scope.hVACUnitOptions = hVAC;
                    $scope.hVACUnitOptionsValues = hVACUnitOptionsArray;
                }
            }
            $scope.removeThermostatBlock = function (event) {
                var $target = event.target;
                var index = $target.getAttribute('index');
                if ($scope.site.thermostatList.length > 0) {
                    $scope.site.thermostatList.splice(index, 1)
                    $scope.data.splice(index, 1);
                }
            }

            function ref(obj, str) {
                str = str.split(".");
                for (var i = 0; i < str.length; i++) {
                    o = obj[str[i]];
                }
                return o;
            }

            $scope.uploadSubmitFormData = {};
            $scope.imageUpload = function (event) {
                var $target = event.target;
                var tag = "." + $target.getAttribute('tagname');
                var classname = $target.getAttribute('classname');
                $scope.input = $($element).find("." + classname);
                commonFactories.getImageData(event, function (data) {
                    var extenstion = data.name.substr((data.name.lastIndexOf(".") + 1), data.name.length);
                    if (extenstion && (extenstion.toLowerCase() !== "png" && extenstion.toLowerCase() !== "jpg" && extenstion.toLowerCase() !== "jpeg")) {
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
                    var element = $(event.target);
                    element.attr("name", "file");
                    if (data.src) {
                        $scope.uploadSubmitFormData.files = $(event.target);
                        ApiFactory.getApiData({
                            serviceName: "fileUpload",
                            data: $scope.uploadSubmitFormData,
                            onSuccess: function (data) {
                                var input = element.next("input");

                                if ($target.getAttribute('index')) {
                                    $scope.site.thermostatList[$target.getAttribute('index')][$(input).attr("ng-model").split(".")[2] + ""] = data.data.fileName;
                                } else {
                                    $scope.site[$(input).attr("ng-model").split(".")[1]] = data.data.fileName
                                }
                            },
                            onFailure: function () {}
                        });
                    }
                });
            }
            $scope.checkUniqueUnit = function ($event) {
                var flagCheck = false;
                var indexVal;
                $.each(hVAC, function (key, value) {
                    if (value.trim().toLowerCase() === $event.target.value.trim().toLowerCase()) {
                        indexVal = key;
                        flagCheck = true;
                        return;
                    }
                });
                if ($event.target.value.trim()) {
                    if (!flagCheck) {
                        hVAC[$($event.target).attr('index')] = $event.target.value.trim();
                        $($event.target).next("div").hide();
                    } else {
                        if (indexVal !== $($event.target).attr('index')) {
                            $($event.target).next("div").show();
                        } else {
                            $($event.target).next("div").hide();
                        }
                    }
                } else {
                    removeKeyStartsWith(hVAC, $($event.target).attr('index'))
                }
                hVACUnitOptionsArray = [];
                $.each(hVAC, function (key, value) {
                    hVACUnitOptionsArray.push(value.trim())
                });
                $scope.hVACUnitOptions = hVAC;
                $scope.hVACUnitOptionsValues = hVACUnitOptionsArray;
            }

            function removeKeyStartsWith(obj, letter) {
                Object.keys(obj).forEach(function (key) {
                    if (key.match('^' + letter))
                        delete obj[key];
                });
            }
        }
        $scope.dateSurveyFlag = false;
        $scope.dateSurvey = function (dateSurveyFlag) {
            if (dateSurveyFlag) {
                $scope.dateSurveyFlag = true;
            }
        }
        $scope.deleteSite = function () {
            $('#modelDialog').on('show.bs.modal', function (event) {
                var modal = $(this)
                modal.find('.modal-title').text('Delete site');
                modal.find('.model-content').text('Are you sure you want to delete this site?')
            });
        }
        $rootScope.accept = function () {
                ApiFactory.getApiData({
                    serviceName: "deleteSite",
                    data: {
                        "siteId": $rootScope.site.siteId
                    },
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $("#modelDialog").modal("hide");
                            toastr.success(messageFactory.getMessage(data.code));
                            skipChecking = true;
                            $state.go("sites.sitesList");
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                            $("#modelDialog").modal("hide");
                        }
                    },
                    onFailure: function () {
                        $("#modelDialog").modal("hide");
                    }
                });
            }
            /*
             * This function is used to get cities after four characters entered;
             */
        $scope.refreshCities = function (searchText) {

            if (searchText.length > 3) {
                ApiFactory.getApiData({
                    serviceName: "getCitiesSearch",
                    data: {
                        "citySearchText": searchText
                    },
                    onSuccess: function (data) {
                        $scope.siteCityOptions = data.data;
                    },
                    onFailure: function () {}
                });
            }
        };

        /* editSiteController
         * This function is used to get states and cities; -Starts here
         */

        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name === "sites.sitesList.viewSite.editLocations.editStates") {
                skipChecking = true;
                $state.go("sites.sitesList.viewSite.editLocations");
                skipChecking = false;
            }
            $(this).off('hidden.bs.modal');
        });

        if ($state.$current.self.name === "sites.sitesList.viewSite.editLocations.editStates") {
            skipChecking = true;
            $state.go("sites.sitesList.viewSite.editLocations");
            skipChecking = false;
            $(this).off('hidden.bs.modal');
        }

        function getValidateZipCode(callback) {

            ApiFactory.getApiData({
                serviceName: "loadStatesfromZip",
                data: $scope.site.siteZipCode,
                onSuccess: function (dataObj) {
                    if (dataObj.status.toLowerCase() === "success") {
                        if (dataObj.status == 'FAILURE') {
                            toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                        } else {


                            var matched;
                            var cityMatch;
                            var stateMatch;

                            $.each(dataObj.data, function (key, value) {

                                if ((value.state_name.toString().toLowerCase() == $scope.site.siteStateName.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.site.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() != $scope.site.siteCityName.toString().toLowerCase()) {

                                    stateMatch = true;
                                    return;

                                }

                                if ((value.state_name.toString().toLowerCase() != $scope.site.siteStateName.toString().toLowerCase() && value.state_short_code.toString().toLowerCase() != $scope.site.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.site.siteCityName.toString().toLowerCase()) {

                                    cityMatch = true;
                                    return;

                                }



                                if ((value.state_name.toString().toLowerCase() == $scope.site.siteStateName.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.site.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.site.siteCityName.toString().toLowerCase()) {

                                    $scope.site.siteTimeZone = value.timezone;
                                    $scope.site.siteCity = value.city_id;
                                    $scope.site.siteState = value.state_id;
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


        $scope.getStatesAndCities = function (searchText) {
            $scope.makeDisable = false;

            if (!$scope.site.siteStateName) {
                $scope.siteStateNameRequired = true;

            }
            if (!$scope.site.siteCityName) {
                $scope.siteCityNameRequired = true;
            }
            if (!$scope.site.siteStateName || !$scope.site.siteCityName) {
                return;
            }
            $rootScope.storeObjects = {};
            $rootScope.siteObjModified = {};
            $rootScope.radioValue = [];
            if ($scope.site.siteZipCode.length == 5) {
                getValidateZipCode();
            }
        };

        $rootScope.$on("zipcodeChoosen", function (event, args) {
            $scope.site.siteStateName = args.obj.state_name;
            $scope.site.siteCityName = args.obj.city_name;
            $scope.site.siteState = args.obj.state_id;
            $scope.site.siteCity = args.obj.city_id;
            $scope.site.siteTimeZone = args.obj.timezone;
            $scope.site.siteZipCode = args.obj.zip_code;
        });

        $rootScope.$on("selectedFromGetStatesFromEdit", function (event, data) {
            $rootScope.editSiteLocationObj = data;

            $rootScope.$broadcast('zipcodeChoosen', {
                obj: data
            });

            if ($state.$current.self.name === "sites.sitesList.viewSite.editLocations.editStates") {
                skipChecking = true;
                $state.go("sites.sitesList.viewSite.editLocations");
                skipChecking = false;
            }
            $(this).off('hidden.bs.modal');
        });

        /* editSiteController
         * This function is used to get states and cities; -Ends here
         */


    })
    .filter('arrayDiff', function () {
        return function (array, diff) {
            var i, item, newArray = [],
                exception = Array.prototype.slice.call(arguments, 2);
            for (i = 0; i < array.length; i++) {
                item = array[i];
                if (diff.indexOf(item) < 0 || exception.indexOf(item) >= 0) {
                    newArray.push(item);
                }
            }
            return newArray;
        };

    })
    .controller("addLocationsController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state) {
        $('#modelDialog').on('show.bs.modal', function () {
            var modal = $(this);
            modal.find(".modal-title").text("Add Site");
        });
        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name === "customers.addNew.addLocations") {
                skipChecking = true;
                $state.go("customers.customersList.getCustomer.editCustomer");
                skipChecking = false;
            }
            $(this).off('hidden.bs.modal');
        });
        $("#modelDialog").modal("show");
        $scope.importManually = function () {
            $state.go("customers.addNew.addLocations.manually");
        }
        $scope.editLocations = function (id) {
            $state.go("customers.customersList.getCustomer.sites.viewSite.editLocations", {
                siteId: id
            });
        }
    }).controller("newSiteController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, $element, toastr) {
        $rootScope.storeObjects = null;

        $('#modelDialog').on('show.bs.modal', function () {
            var modal = $(this);
            modal.find(".modal-title").text("Add Site");
            $(this).off('shown.bs.modal');
        });


        $scope.bulkFileEmpty = true;

        $scope.fileBulkUpload = function (event) {

            $scope.fileEmpty = true;
            var $target = event.target;
            if ($target.files[0]) {
                $scope.bulkFileEmpty = false;
            }

        }


        $scope.uploadSubmitFormData = {};

        $scope.downloadLink = "app/views/uploadtemplates/Sites_Bulk_Import_Template.xlsx";

        $scope.importFile = function () {


            if ($($element).find("[type='file']").val() == "") {

                toastr.error(messageFactory.getMessage("EMPTY_FILE"));

                return;
            }


            $scope.uploadSubmitFormData.files = $($element).find("[type='file']");
            $scope.uploadSubmitFormData.import = "Site"
            $scope.uploadSubmitFormData.customerId = $state.params.customerId;

            $rootScope.toastrOpend = true;

            ApiFactory.getApiData({
                serviceName: "sitesUpload",
                data: $scope.uploadSubmitFormData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {


                        /*var x = toastr.success(messageFactory.getMessage(data.code) + '<br/>total records= ' + data.data.totalCount + '<br/>failed records= ' + data.data.failedCount + '<br/>inserted records= ' + data.data.sucessCount + " <br/> <button class='relatedLinks btn btn-xs btn-default'>Click here to see output</button>", {
                            timeOut: 0,
                            extendedTimeOut: 0
                        });
                        
                        */


                        toastr.success("Site bulk import starting, please go to 'Sites Bulk Import Status' page for status of import");


                        if ($state.$current.self.name === "customers.customersList.getCustomer.sites.addLocations") {
                            skipChecking = true;
                            $state.go("customers.customersList.getCustomer.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "sites.sitesList.addLocations") {
                            skipChecking = true;
                            $state.go("sites.sitesList");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites.addLocations") {
                            skipChecking = true;
                            $state.go("customers.customersList.getCustomer.groups.groupInfo.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites.addLocations") {
                            skipChecking = true;
                            $state.go("groups.groupList.groupInfo.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.addLocations") {
                            skipChecking = true;
                            $state.go("scheduler.schedulerList.getSchedule.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations") {
                            skipChecking = true;
                            $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "users.usersList.userProfile.sites.addLocations") {
                            skipChecking = true;
                            $state.go("users.usersList.userProfile.sites");
                            skipChecking = false;
                        } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites.addLocations") {
                            skipChecking = true;
                            $state.go("users.usersList.userProfile.groups.groupInfo.sites");
                            skipChecking = false;
                        }
                        setTimeout(function () {
                            $state.reload();
                        }, 500)
                        $("#modelDialog").modal("hide");

                    } else {
                        if (data.code == "ERR_SITE_UPLOAD_FORMAT_2000") {
                            toastr.error(messageFactory.getMessage(data.code));
                            return;
                        }
                        if (data.code == "ERR_SITE_UPLOAD_2000") {
                            toastr.error(messageFactory.getMessage(data.code));
                            return;
                        }
                        setTimeout(function () {
                            $state.reload();
                        }, 500)
                        toastr.error("Sites Bulk Upload Template Failed");

                    }
                },
                onFailure: function () {}
            });
        }

        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name === "customers.customersList.getCustomer.sites.addLocations") {
                skipChecking = true;
                $state.go("customers.customersList.getCustomer.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "sites.sitesList.addLocations") {
                skipChecking = true;
                $state.go("sites.sitesList");
                skipChecking = false;
            } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites.addLocations") {
                skipChecking = true;
                $state.go("customers.customersList.getCustomer.groups.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites.addLocations") {
                skipChecking = true;
                $state.go("groups.groupList.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.addLocations") {
                skipChecking = true;
                $state.go("scheduler.schedulerList.getSchedule.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations") {
                skipChecking = true;
                $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "users.usersList.userProfile.sites.addLocations") {
                skipChecking = true;
                $state.go("users.usersList.userProfile.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites.addLocations") {
                skipChecking = true;
                $state.go("users.usersList.userProfile.groups.groupInfo.sites");
                skipChecking = false;
            }
            $(this).off('hidden.bs.modal');
        });
        $("#modelDialog").modal("show");
        $scope.importManually = function () {
            if ($state.$current.self.name === "customers.customersList.getCustomer.sites.addLocations") {
                $state.go("customers.customersList.getCustomer.sites.addLocations.manually");
            } else if ($state.$current.self.name === "sites.sitesList.addLocations") {
                $state.go("sites.sitesList.addLocations.manually");
            } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites.addLocations") {
                $state.go("customers.customersList.getCustomer.groups.groupInfo.sites.addLocations.manually");
            } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites.addLocations") {
                $state.go("groups.groupList.groupInfo.sites.addLocations.manually");
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.addLocations") {
                $state.go("scheduler.schedulerList.getSchedule.sites.addLocations.manually");
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations") {
                $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations.manually");
            } else if ($state.$current.self.name === "users.usersList.userProfile.sites.addLocations") {
                $state.go("users.usersList.userProfile.sites.addLocations.manually");
            } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites.addLocations") {
                $state.go("users.usersList.userProfile.groups.groupInfo.sites.addLocations.manually");
            }
        }
    }).controller("newLocationsController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state, toastr, $filter, $timeout, commonFactories) {
        $('#modelDialog').on('show.bs.modal', function () {
            var modal = $(this);
            modal.find(".modal-title").text("Add Site");
        });
        $('#modelDialog').on('hidden.bs.modal', function () {
            $rootScope.$broadcast("locationModelHiding");
            if ($state.$current.self.name === "customers.customersList.getCustomer.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("customers.customersList.getCustomer.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "sites.sitesList.addLocations.manually") {
                skipChecking = true;
                $state.go("sites.sitesList");
                skipChecking = false;
            } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("customers.customersList.getCustomer.groups.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("groups.groupList.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("scheduler.schedulerList.getSchedule.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "users.usersList.userProfile.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("users.usersList.userProfile.sites");
                skipChecking = false;
            } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites.addLocations.manually") {
                skipChecking = true;
                $state.go("users.usersList.userProfile.groups.groupInfo.sites");
                skipChecking = false;
            }
            $(this).off('hidden.bs.modal');
        });

        $scope.hstep = 1;
        $scope.mstep = 15;
        $scope.changeTimeFormat = function (showMeridian) {
            $scope.getMeridian = showMeridian;
        }

        $("#modelDialog").modal("show");
        $scope.location = {};
        $scope.opt = {};
        $scope.location.siteHours = [{}];
        $scope.siteTypeOptions = [{}];
        $scope.siteGroupOptions = [{}];
        $scope.siteStateOptions = [];
        $scope.siteCityOptions = [];

        /*if ($rootScope.storeObjects != null) {
            $scope.location = $rootScope.storeObjects;
            $('#modelDialog').on('show.bs.modal', function () {
                var modal = $(this);
                modal.find(".modal-title").text("Select States");
            });
        } else {*/
            $.each($($element).find("form").find("input,select,textarea"), function () {
                if ($(this).attr("name")) {
                    $scope.location[$(this).attr("name")] = "";
                }
            });
        //}
        $scope.errors = {};
        $scope.errors.internalId = false;
        $scope.errors.zipcode = false;

        $scope.degreePreferenceOptions = [{
            "label": "Fahrenheit",
            "value": 0
        }, {
            "label": "Celsius",
            "value": 1
		}];
        $scope.getMeridian = true;
        if (!$state.params.customerId) {
            $state.params.customerId = $rootScope.userDetails.customers.split(",")[0] ? $rootScope.userDetails.customers.split(",")[0] : 0;
            if ($state.params.customerId >= 0) {
                addSite();
            }
        } else {
            addSite();
        }
        $scope.isExists = function (flag, data, id) {
            var idData;
            for (var i = 0; i < data.length; i++) {
                if (flag === 'group') {
                    idData = Number(data[i].value);
                }
                if (idData === Number(id)) {
                    return data[i]
                }
            }
        }

        function addSite() {
            $scope.listData = {};
            $scope.listData = commonFactories.fromPageSites($state.$current.self.name, $state.params);

            ApiFactory.getApiData({
                serviceName: "loadaddsite",
                data: $scope.listData,
                onSuccess: function (data) {
                    $scope.siteTypeOptions = data.data.siteType;
                    $scope.siteStateOptions = data.data.states;
                    $scope.siteGroupOptions = data.data.siteGroup;
                    $scope.siteHrs = data.data.siteHrs;
                    $scope.siteTypeOptions.push({
                        value: 0,
                        lable: "Others"
                    });
                    $scope.location.degreePreference = data.data.customerPreferences.degreePreference || 0;
                    $scope.location.nightlyScheduleDownload = data.data.customerPreferences.nightlyScheduleDownload === 1 ? true : false;
                    $scope.location.isHVACModeToAuto = data.data.customerPreferences.isHVACModeToAuto === 1 ? true : false;
                    $scope.location.resetHoldMode = data.data.customerPreferences.resetHoldMode === 1 ? true : false;
                    if (data.data.customerPreferences.fanOn === 1) {
                        $scope.location.fanOn = 1;
                    }
                    if (data.data.customerPreferences.fanAuto === 1) {
                    	$scope.location.fanOn = 0;
                    }
                    $scope.location.lock = data.data.customerPreferences.lock;
                  
                    $scope.location.siteHrsFormate = '1';
                    if (data.data.customerPreferences.thermostateMaxSetPoint) {
                        $scope.location.maxSP = Number(data.data.customerPreferences.thermostateMaxSetPoint);
                    }
                    if (data.data.customerPreferences.thermostateMinSetPoint) {
                        $scope.location.minSP = Number(data.data.customerPreferences.thermostateMinSetPoint);
                    }
                    $scope.location.siteGroups = [];
                    if ($scope.listData.fromPage === "groups") {
                        $scope.location.siteGroups.push({
                            lable: $scope.isExists('group', $scope.siteGroupOptions, Number($state.params.groupId)).lable,
                            value: Number($state.params.groupId)
                        })
                        $scope.opt.group = $scope.location.siteGroups;
                    }

                },
                onFailure: function () {}
            });
        }
        $scope.changeState = function (selectedState) {
            if (selectedState) {
                ApiFactory.getApiData({
                    serviceName: "getCities",
                    data: selectedState,
                    onSuccess: function (data) {
                        $scope.opt.city = undefined;
                        $scope.siteCityOptions = data.data;
                    },
                    onFailure: function () {}
                });
            } else {
                $scope.siteCityOptions = [];
                $scope.opt.city = undefined;
            }
        };
        $scope.changeCity = function (selectedCity) {

            if (selectedCity) {
                $scope.checkzipcode();
            }
            for (var i = 0; i < $scope.siteStateOptions.length; i++) {
                if (selectedCity.stateId == $scope.siteStateOptions[i].stateId) {
                    $scope.opt.state = {};
                    $scope.opt.state = $scope.siteStateOptions[i];
                }
            }
        };
        $scope.clearStoreDate = function (index, value) {
            if (('' + index === '0' || index) && '' + value === '0') {
                delete $scope.location.siteHours[index]
            }
        };
        var countStores = 0;
        $scope.copyStoreHours = function () {
            var resultAdd = [];
            var closehours;
            var openhours;
            if (!angular.isUndefined($scope.location.siteHours)) {
                for (var n in $scope.location.siteHours) {
                    if ($scope.location.siteHours[n].dayOfWeek !== 0 && $scope.location.siteHours[n].openHrs && $scope.location.siteHours[n].closeHrs) {
                        countStores++;
                        openhours = $scope.location.siteHours[n].openHrs;
                        closehours = $scope.location.siteHours[n].closeHrs;
                    }
                }
            }
            if (countStores === 1) {
                for (var j in $scope.siteHrs) {
                    resultAdd[j] = {};
                };
                for (var p in $scope.location.siteHours) {
                    resultAdd[Number($scope.location.siteHours[p].dayOfWeek) - 1] = $scope.location.siteHours[p];
                };
                for (var g = 0; g < resultAdd.length; g++) {
                    if (typeof resultAdd[g].dayOfWeek === "undefined") {
                        resultAdd.splice(g, 1, {
                            dayOfWeek: (g + 1),
                            closeHrs: closehours,
                            openHrs: openhours,
                            dayOfWeekChecked: true
                        });
                    }
                }
                $scope.location.siteHours = resultAdd;
            }
        };
        $scope.checkSiteInternalId = function () {
            if ($scope.location.siteInternalId) {
                ApiFactory.getApiData({
                    serviceName: "checkSiteInternalId",
                    async: true,
                    data: {
                        "siteId": "0",
                        "siteInternalId": $scope.location.siteInternalId,
                        "customerId": $state.params.customerId
                    },
                    onSuccess: function (data) {
                        if (Number(data.data) > 0) {
                            $scope.siteInternalIdError = messageFactory.getMessage(data.code);
                            $scope.errors.internalId = true;
                        } else {
                            $scope.errors.internalId = false;
                        }
                        $rootScope.$broadcast('gotInternalId');
                    },
                    onFailure: function () {}
                });
            } else {
                $scope.errors.internalId = false;
                $rootScope.$broadcast('gotInternalId');
            }
        };
        $scope.checkzipcode = function () {
            $scope.makeDisable = false;
            if (!angular.isUndefined($scope.location.siteZipCode) && $scope.location.siteZipCode.trim() !== "" && Math.round($scope.location.siteZipCode) === 0) {
                $scope.siteZipcodeError = "Zip code should not be zero.";
                $scope.errors.zipcode = true;
            } else {
                $scope.siteZipcodeError = "";
                $scope.errors.zipcode = false;
                if (!angular.isUndefined($scope.location.siteZipCode) && $scope.location.siteZipCode.trim() !== "" && $scope.opt.state && $scope.opt.city) {
                    ApiFactory.getApiData({
                        serviceName: "checkZipcode",
                        async: true,
                        data: {
                            "zipCode": $scope.location.siteZipCode,
                            "stateId": $scope.opt.state.stateId.toString(),
                            "city": $scope.opt.city.cityName.toString()
                        },
                        onSuccess: function (data) {
                            if (data.status.toLowerCase() === "success") {
                                $scope.errors.zipcode = false;
                                //$rootScope.$broadcast('getTimeZoneValue');
                                $scope.location.siteTimeZone = data.data.timeZone;
                            } else {
                                $scope.siteZipcodeError = messageFactory.getMessage(data.code);
                                $scope.errors.zipcode = true;
                                $(".modal-open .modal").animate({
                                    scrollTop: 0
                                });
                            }
                        },
                        onFailure: function () {}
                    });
                } else {
                    $scope.location.siteTimeZone = "";
                }
            }
        };
        $scope.$on("getTimeZoneValue", function () {
            ApiFactory.getApiData({
                serviceName: "getTimeZoneSite",
                async: true,
                data: {
                    "zipCode": $scope.location.siteZipCode
                },
                onSuccess: function (data) {
                    $scope.location.siteTimeZone = data.data.timeZone;
                },
                onFailure: function () {}
            });
        })
        $scope.$on("gotInternalId", function () {
            if ($scope.errors.internalId) {
                $(".modal-open .modal").animate({
                    scrollTop: 0
                });
                return;
            }
            if ($scope.errors.zipcode) {
                $(".modal-open .modal").animate({
                    scrollTop: 0
                });
                return;
            }
            var groupArray = [];
            $.each($scope.opt.group, function (key, value) {
                groupArray.push(value.value);
            });
            if (!angular.isUndefined($scope.opt.selected)) {
                $scope.location.siteType = $scope.opt.selected.value.toString();
            } else {
                $scope.location.siteType = "-1";
            }

            $scope.location.customerId = $state.params.customerId;
            $scope.location.siteGroups = groupArray;
            $scope.siteData = angular.copy($scope.location);

            if ($scope.siteData.fanOn == 1) {
            	$scope.siteData.fanOn = 1;
                $scope.siteData.fanAuto = 0;
            } else {
            	$scope.siteData.fanOn = 0;
                $scope.siteData.fanAuto = 1;
            }
            $scope.siteData.sameAsStore = 1;
            $scope.siteData.isHVACModeToAuto = $scope.siteData.isHVACModeToAuto === true ? 1 : 0;
            $scope.siteData.resetHoldMode = $scope.siteData.resetHoldMode === true ? 1 : 0;
            $scope.siteData.nightlyScheduleDownload = $scope.siteData.nightlyScheduleDownload === true ? 1 : 0;

            if (!angular.isUndefined($scope.location.siteHours)) {
                var jsonObj = [];
                var obj;
                for (var n in $scope.location.siteHours) {
                    if ($scope.location.siteHours[n].dayOfWeek !== 0 && $scope.location.siteHours[n].openHrs && $scope.location.siteHours[n].closeHrs) {
                        obj = {};
                        obj["dayOfWeek"] = $scope.location.siteHours[n].dayOfWeek;
                        obj["openHrs"] = $scope.location.siteHrsFormate === 0 ? moment($scope.location.siteHours[n].openHrs).format("HH:mm") : moment($scope.location.siteHours[n].openHrs).format("hh:mm A");
                        obj["closeHrs"] = $scope.location.siteHrsFormate === 0 ? moment($scope.location.siteHours[n].closeHrs).format("HH:mm") : moment($scope.location.siteHours[n].closeHrs).format("hh:mm A");
                        jsonObj.push(obj);
                    }
                }
                $scope.siteData.siteHours = jsonObj;
            }

            if ($state.current.name == "users.usersList.userProfile.sites.addLocations.manually") {

                $scope.siteData = $rootScope.mergeObject($scope.siteData, {
                    userId: $state.params.userId
                });

            } else {
                $scope.siteData = $rootScope.mergeObject($scope.siteData, {
                    userId: $rootScope.userDetails.userId
                });
            }

            ApiFactory.getApiData({
                serviceName: "addsite",
                data: $scope.siteData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        if ($rootScope.addedSiteData) {
                            $rootScope.addedSiteData.push(data.data);
                        } else {
                            $rootScope.addedSiteData = [];
                            $rootScope.addedSiteData.push(data.data);
                        }
                        if ($state.$current.self.name === "sites.sitesList.addLocations.manually") {
                            $('.unchk').prop('checked', false);
                            $state.go("sites.sitesList", {}, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "customers.customersList.getCustomer.sites.addLocations.manually") {
                            $('.unchk').prop('checked', false);
                            $state.go("customers.customersList.getCustomer.sites", {
                                customerId: $scope.siteData.customerId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.sites.addLocations.manually") {
                            $state.go("customers.customersList.getCustomer.groups.groupInfo.sites", {
                                customerId: $scope.siteData.customerId,
                                groupId: $state.params.groupId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "groups.groupList.groupInfo.sites.addLocations.manually") {
                            $state.go("groups.groupList.groupInfo.sites", {
                                customerId: $scope.siteData.customerId,
                                groupId: $state.params.groupId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.sites.addLocations.manually") {
                            $state.go("scheduler.schedulerList.getSchedule.sites", {
                                customerId: $scope.siteData.customerId,
                                scheduleId: $state.params.scheduleId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations.manually") {
                            $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites", {
                                customerId: $scope.siteData.customerId,
                                scheduleId: $state.params.scheduleId,
                                groupId: $state.params.groupId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "users.usersList.userProfile.sites.addLocations.manually") {
                            $state.go("users.usersList.userProfile.sites", {
                                customerId: $scope.siteData.customerId,
                                userId: $state.params.userId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.sites.addLocations.manually") {
                            $state.go("users.usersList.userProfile.groups.groupInfo.sites", {
                                customerId: $scope.siteData.customerId,
                                userId: $state.params.userId,
                                groupId: $state.params.groupId
                            }, {
                                reload: true
                            });
                            return;
                        } else {
                            $rootScope.$broadcast('siteAdded');
                        }
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        });
        $scope.createLocation = function (form) {
            if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
            if (form.$valid) {
            	$scope.makeDisable = true;
                getValidateZipCode(function () {
                	$scope.makeDisable = false;
                    $scope.checkSiteInternalId();
                })
            }
        };
        /*
         * This function is used to get cities after four characters entered;
         */
        $scope.refreshCities = function (searchText) {

            if (searchText.length > 3) {
                ApiFactory.getApiData({
                    serviceName: "getCitiesSearch",
                    data: {
                        "citySearchText": searchText
                    },
                    onSuccess: function (data) {
                        $scope.siteCityOptions = data.data;
                    },
                    onFailure: function () {}
                });
            }
        };


        $scope.getStateCityValidate = function () {

            if (!$scope.location.siteStateName) {
                $(".site-state-name")[0].focus();
                return;
            }
            if (!$scope.location.siteCityName) {
                $(".site-city-name")[0].focus();
            }

        }

        /* newLocationsController
         * This function is used to get states and cities; -Starts here
         */

        function getValidateZipCode(callback) {

            ApiFactory.getApiData({
                serviceName: "loadStatesfromZip",
                data: $scope.location.siteZipCode,
                onSuccess: function (dataObj) {
                	$scope.makeDisable = false;
                    if (dataObj.status.toLowerCase() === "success") {
                        if (dataObj.status == 'FAILURE') {
                            toastr.error(messageFactory.getMessage("FAIL_GEOCODEDATA_DETAILS_ANY_400"));
                        } else {

                            var matched;
                            var cityMatch;
                            var stateMatch;

                            $.each(dataObj.data, function (key, value) {

                                if ((value.state_name.toString().toLowerCase() == $scope.location.siteStateName.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.location.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() != $scope.location.siteCityName.toString().toLowerCase()) {

                                    stateMatch = true;
                                    return;

                                }

                                if ((value.state_name.toString().toLowerCase() != $scope.location.siteStateName.toString().toLowerCase() && value.state_short_code.toString().toLowerCase() != $scope.location.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.location.siteCityName.toString().toLowerCase()) {

                                    cityMatch = true;
                                    return;

                                }



                                if ((value.state_name.toString().toLowerCase() == $scope.location.siteStateName.toString().toLowerCase() || value.state_short_code.toString().toLowerCase() == $scope.location.siteStateName.toString().toLowerCase()) && value.city_name.toString().toLowerCase() == $scope.location.siteCityName.toString().toLowerCase()) {

                                    $scope.location.siteTimeZone = value.timezone;
                                    $scope.location.siteCity = value.city_id;
                                    $scope.location.siteState = value.state_id;
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



        $scope.getStatesAndCities = function (searchText) {
            $scope.makeDisable = false;


            if (!$scope.location.siteStateName) {
                $scope.siteStateNameRequired = true;

            }

            if (!$scope.location.siteCityName) {
                $scope.siteCityNameRequired = true;
            }

            if (!$scope.location.siteStateName || !$scope.location.siteCityName) {
                return;
            }


            $rootScope.storeObjects = {};
            $rootScope.siteObjModified = {};
            $rootScope.radioValue = [];
            $rootScope.selLocationData = $scope.location;

            if ($scope.location.siteZipCode && $scope.location.siteZipCode.length == 5) {
                getValidateZipCode();
            }


        };

        $rootScope.$on("selectedFromGetStates", function (event, data) {
            $state.go('sites.sitesList.addLocations.manually', $state.params);
            $scope.location = $rootScope.selLocationData;
            $scope.location.siteStateName = data.state_name;
            $scope.location.siteCityName = data.city_name;
            $scope.location.siteTimeZone = data.timezone;
            $scope.location.siteZipCode = data.zip_code;
            $scope.location.siteCity = data.city_id;
            $scope.location.siteState = data.state_id;
            $rootScope.siteObjModified = null;
            $rootScope.storeObjects = $scope.location;


        });

        /* newLocationsController
         * This function is used to get states and cities; -Ends here
         */

    })
    .controller("loadStatesCitiesController", function ($scope, ApiFactory, $rootScope, $state, $element, $timeout) {


        if (!$rootScope.statesAndCitiesArray) {

            if ($state.$current.self.name === 'sites.sitesList.addLocations.manually.getStates') {
                skipChecking = true;
                $state.go("sites.sitesList.addLocations.manually");
                skipChecking = false;
            }
            if ($state.$current.self.name === 'sites.sitesList.viewSite.editLocations.editStates') {
                skipChecking = true;
                $state.go("sites.sitesList.viewSite.editLocations");
                skipChecking = false;
            }

            return;

        }


        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name === 'sites.sitesList.addLocations.manually.getStates') {
                skipChecking = true;
                $state.go("sites.sitesList.addLocations.manually");
                skipChecking = false;
            }
            if ($state.$current.self.name === 'sites.sitesList.viewSite.editLocations.editStates') {
                skipChecking = true;
                $state.go("sites.sitesList.viewSite.editLocations");
                skipChecking = false;
            }

            $(this).off('hidden.bs.modal');
        });

        $("#modelDialog").modal("show");


        $scope.getSelectedObj = function (selectedObj) {

            $timeout(function () {
                $rootScope.$broadcast("selectedFromGetStates", selectedObj);
            }, 50);
        };
        $scope.getSelectedObjFromEdit = function (selectedObj) {
            $timeout(function () {
                $rootScope.$broadcast("selectedFromGetStatesFromEdit", selectedObj);
            }, 50);

        };


    }).controller("activityLogSiteController", function ($scope, ApiFactory, $rootScope, $state, $element) {
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
        $scope.activityObj.specificId = $state.params.siteId;
        $scope.activityObj.serviceId = 3;
        $scope.startDate = "";
        $scope.endDate = "";
        $scope.activityObj.startDate = "";
        $scope.activityObj.endDate = "";
        $.each($($element).find("form").find("input,select,textarea"), function () {
            if ($(this).attr("name")) {
                $scope.activityObj[$(this).attr("name")] = "";
            }
        });

        $scope.devices = [];
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
                        if (value.alServiceName.toLowerCase().trim() === "device") {
                            if (value.alSpecificName) {
                                devices[value.alSpecificName.trim()] = value.alSpecificName.trim();
                            }
                        }
                    });

                    var tempDevicesList = [];
                    $.each(devices, function (key, value) {
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
            $scope.activityObj.specificId = $state.params.siteId;
            $scope.activityObj.serviceId = 3;
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
    }).filter('reverse', function () {
        return function (items) {
            return items.slice().reverse();
        };
    });