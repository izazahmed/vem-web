app.controller("schedulerListController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {

    /**
     *This method is for fetching customers list
     *
     *@param customer state
     *
     */

    $scope.alphabets = ['#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
    $scope.alphabetHeight = Math.floor(($(".alphabets-badge-width").height() - 6) / 27);
    $scope.isDataAvailable = true;
    $scope.changeScheduleActivity = function () {
        $timeout(function () {
            $scope.filteredLength = $($element).find(".schedules-list").find("li").length;
        }, 100)
    }
    $scope.checkAll = function () {
        $scope.showMain = $scope.showMain == true ? false : true;
        $scope.showPlus = $scope.showPlus == true ? false : true;
        $scope.schedules = angular.copy($scope.schedules);
    };
    $rootScope.loadSchedulerList = function () {
	 
        ApiFactory.getApiData({
            serviceName: "schedulerList",
            data: {
                customerId: $state.params.customerId
            },
            onSuccess: function (data) {

                $timeout(function () {
                    $("#nodata").css({
                        opacity: 1
                    });
                }, 2000);

                if (data.data && data.data.length === 0) {
                    $scope.totalschedules = 0;
                    $scope.filteredLength = 0;
                    $scope.isDataAvailable = false;
                    return;
                }

                $scope.totalschedules = data.data.length
                $scope.schedules = commonFactories.makeAlphabetsGroup(data.data, "scheduleName");

                $scope.changeScheduleActivity();
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    }
    $scope.newSchedule = function () {
        var obj = {};
        switch (commonFactories.getScheduleSource($state)) {
            case "groups":
                obj.groupId = $state.params.groupId;
                break;
            case "sites":
                obj.siteId = $state.params.siteId;
                break;
            case "customers":
                obj.customerId = $state.params.customerId;
                break;
        }
        $state.go("scheduler.addSchedule", obj);
    }
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
            $scope.changeScheduleActivity();
        }
    }
    $scope.moveRelatedGroup = function (alphabet) {
        $($element).find(".schedules-list").animate({
            scrollTop: $($element).find("[group='" + alphabet + "']").position().top + $($element).find(".schedules-list").scrollTop()
        });
    }
    if ($rootScope.userDetails.isSuper) {
        $scope.customerdphide = true;
        $scope.groupdphide = true;
        $scope.sitedphide = true;
    }
    if ($rootScope.userDetails.isSuper || $rootScope.userDetails.rolePermissions['Schedule Management'] == 2) {
        $scope.applybuttonhide = true;
        $scope.copybuttonhide = true;
        $scope.addbuttonhide = true;
    }
    $scope.statusFilter = function (obj) {
        if ($scope.filterBy == "Status" && $scope.filterSearch != "All") {
            return obj.status_id == $scope.filterSearch;
        }
        return true;
    };
    $scope.search = function (item) {
        var input = item.scheduleName;
        if (!$scope.filterSchedules || (input.toLowerCase().indexOf($scope.filterSchedules.toLowerCase()) !== -1)) {
            searchLength();
            return true;
        }
        return false;
    };
    $scope.filterByChange = function () {
        $scope.filterdata = "";
        $scope.tabinnerId = "";
        $scope.localfillter = "";
        if ($state.current.name == "customers.customersList.getCustomer.schedule") {
            $scope.tabinnerId = $state.params.customerId
            if ($scope.filterBy == "Groups") {
                $scope.localfillter = "customerGropus"
            } else if ($scope.filterBy == "Sites") {
                $scope.localfillter = "customerSite"
            } else if ($scope.filterBy == "Devices") {
                $scope.localfillter = "customerDevice"
            }
        } else {
            $scope.localfillter = $scope.filterBy
        }
        var schedulefilterData = {
            "filterBy": $scope.localfillter,
            "tabFiledId": $scope.tabinnerId

        }
        if ($scope.filterBy == "Status") {
            $scope.filterdata = [{
                "key": "1",
                "value": "New"
            }, {
                "key": "2",
                "value": "Downloading"
            }, {
                "key": "3",
                "value": "Downloading Successful"
            }, {
                "key": "4",
                "value": "Downloading Failed"
            }];

            $rootScope.loadDataByTab();
            $scope.filterSearch = "All";
            $scope.filterBy = "Status";
        } else if ($scope.filterBy == "All") {
            $rootScope.loadDataByTab();
        } else {
            ApiFactory.getApiData({
                serviceName: "scheduledynaselect",
                data: schedulefilterData,
                onSuccess: function (data) {
                    $scope.filterdata = data.data;
                    $scope.filterSearch = "All";
                    $scope.filterBySearch();
                },
                onFailure: function (data) {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            });
        }
        $scope.changeScheduleActivity();
    };
    $scope.filterBySearch = function (fromTab) {
        if ($scope.filterBy == "Status") {
            $scope.changeScheduleActivity();
            return
        }
        if ($state.current.name == "customers.customersList.getCustomer.schedule") {
            $scope.tabinnerId = $state.params.customerId
            if ($scope.filterBy == "Groups") {
                $scope.filterBy = "customerGropus"
            } else if ($scope.filterBy == "Sites") {
                $scope.filterBy = "customerSite"
            } else if ($scope.filterBy == "Devices") {
                $scope.filterBy = "customerDevice"
            }
        }
        var schedulefilterData = {
            "filterBy": $scope.filterBy,
            "searchValue": $scope.filterSearch,
            "tabFiledId": $scope.tabinnerId
        }
        if ($scope.filterBy == "customerGropus") {
            $scope.filterBy = "Groups"
        }
        if ($scope.filterBy == "customerSite") {
            $scope.filterBy = "Sites"
        }
        if ($scope.filterBy == "customerDevice") {
            $scope.filterBy = "Devices"
        }
        if ($scope.filterBy == "fromcustomer") {
            $scope.filterBy = "All"
        }
        ApiFactory.getApiData({
            serviceName: "schedulesearch",
            data: schedulefilterData,
            onSuccess: function (data) {
                if (fromTab != null && (fromTab == "fromcustomer" || fromTab == "fromgroup" || fromTab == "fromSite")) {
                    $scope.totalschedules = data.data.length
                }
                if (data.data && data.data.length > 0) {
                    $scope.schedules = commonFactories.makeAlphabetsGroup(data.data, "scheduleName");
                } else {
                    $scope.schedules = "";
                }
                $("#nodata").css({
                    opacity: 1
                });
                $scope.changeScheduleActivity();
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
                $scope.schedules = "";
            }
        });
    };
    $scope.applySchedule = function (schedule) {
        if (
            /*$state.current.name == "customers.customersList.getCustomer.schedule"||$state.current.name == "groups.groupList.groupInfo.schedules"
                  	  ||$state.current.name == "sites.sitesList.viewSite.schedules"||*/
            $state.current.name == "sites.sitesList.viewSite.Test") {
            $scope.applydeviceSchedule(schedule)
        } else {
            $rootScope.selectedSchedule = schedule
            if ($state.current.name == "scheduler.schedulerList") {
                $state.go("scheduler.schedulerList.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "customers.customersList.getCustomer.schedule") {
                $state.go("customers.customersList.getCustomer.schedule.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "groups.groupList.groupInfo.schedules") {
                $state.go("groups.groupList.groupInfo.schedules.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "sites.sitesList.viewSite.schedules") {
                $state.go("sites.sitesList.viewSite.schedules.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
        }
    };
    $scope.getSourcePageId = function (id) {
        if ($state.current.name.indexOf("scheduler.schedulerList") != -1 && $state.params.sourcePage) {
            return $state.params.sourcePage.split("-")[0] + "-" + id;
        } else {
            return $state.params.sourcePage;
        }
    }
    $scope.getSchedule = function (id) {
        $rootScope.scheduleId = id;
        if ($state.current.name.indexOf("scheduler.schedulerList") != -1) {
            var scheduleId = id;
            var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + scheduleId;
            $state.go("scheduler.schedulerList.getSchedule.scheduler", $rootScope.mergeObject($state.params, {
                scheduleId: id,
                sourcePage: sourcePage
            }));
            return;
        }
        $state.go("scheduler.schedulerList.getSchedule.scheduler", $rootScope.mergeObject($state.params, {
            scheduleId: id
        }));
    };
    $scope.copySchedule = function (id) {
        $rootScope.scheduleId = id;
        $state.go("scheduler.schedulerList.copySchedule", {
            scheduleId: id
        });
    };

    $rootScope.loadDataByTab = function () {
        if ($state.current.name == "customers.customersList.getCustomer.schedule" || $state.current.name == "customers.customersList.getCustomer.schedule.applyView") {

            $scope.filterBy = "fromcustomer";
            $scope.filterSearch = "All"
            $scope.filterBySearch("fromcustomer");
            if ($scope.filterBy == "fromcustomer") {
                $scope.filterBy = "All";
            }
            $scope.filterHide = true;
            $scope.filterdropHide = true;
            // $scope.applybuttonhide = false;
            $scope.customerdphide = false;
        } else if ($state.current.name == "groups.groupList.groupInfo.schedules" || $state.current.name == "groups.groupList.groupInfo.schedules.applyView") {
            $scope.filterBy = "customerGroupSchedule";
            $scope.filterSearch = $state.params.groupId
            $scope.filterBySearch("fromgroup");
            $scope.groupdphide = false;
            $scope.filterHide = true;
            $scope.filterdropHide = true;
            // $scope.applybuttonhide = false;
        } else if ($state.current.name == "sites.sitesList.viewSite.schedules" || $state.current.name == "sites.sitesList.viewSite.schedules.applyView") {
            $scope.filterBy = "customerSiteSchedule";
            $scope.filterSearch = $state.params.siteId
            $scope.filterHide = true;
            $scope.filterdropHide = true;
            $scope.sitedphide = false;

            // $scope.applybuttonhide = false;
            $scope.filterBySearch("fromSite");
        } else {
            $rootScope.loadSchedulerList();
        }
    }
    $rootScope.loadDataByTab();
    $scope.applydeviceSchedule = function (schedule) {
        var scheduleApplysave = {
            "scheduleId": schedule.scheduleId,
            "fromSubTab": "applyOnly"
        }
        ApiFactory.getApiData({
            serviceName: "scheduleapplysave",
            data: scheduleApplysave,
            onSuccess: function (data) {
                $rootScope.loadDataByTab();
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    };
})

.controller("applyViewController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {
   
    $("#modelDialog").modal("show");
    $scope.groupFileterIds = [];
    $scope.siteFileterIds = [];
    $scope.deviceFileterIds = [];
    $scope.groupGroupIds = [];
    $scope.groupdataArry = [{
        id: "All",
        label: "All"
    }];
    $scope.sitedataArry = [{
        id: "All",
        label: "All"
    }];
    $scope.devicedataArry = [{
        id: "All",
        label: "All"
    }];
    $scope.siteIds = [];
    $scope.deviceIds = [];
    $scope.groupSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 3,
        scrollable: true,
        showCheckAll: false,
        showUncheckAll: false,
        dynamicTitle: true
    };

    $scope.groupFileterIds = [{
        id: "All",
        label: "All"
    }]
    $scope.siteFileterIds = [{
        id: "All",
        label: "All"
    }]
    $scope.deviceFileterIds = [{
        id: "All",
        label: "All"
    }]
    $('#modelDialog').on('hidden.bs.modal', function () {
        if ($state.$current.self.name === "scheduler.schedulerList.applyView") {
            skipChecking = true;
            $state.go("scheduler.schedulerList");
            skipChecking = false;
        }
        if ($state.$current.self.name === "customers.customersList.getCustomer.schedule.applyView") {
            skipChecking = true;
            $state.go("customers.customersList.getCustomer.schedule");
            skipChecking = false;
        }
        $(this).off('hidden.bs.modal');
    });
    var customer = {
        "filterBy": "customer",
        "scheduleId": $state.params.seheduleId
    };
    $scope.apllyViewCustomer = function () {
        ApiFactory.getApiData({
            serviceName: "scheduleapplyview",
            data: customer,
            onSuccess: function (data) {
                $scope.applyView = data.data;
                if (data.data[0].sl_customer_id) {
                    $scope.customerList = data.data[0].sl_customer_id;
                    $scope.customerdis = true;
                    $scope.filterCustomerChange()
                }
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    }
    $scope.filterCustomerChange = function () {
        $scope.groupdataArry = [{
            id: "All",
            label: "All"
        }];
        $scope.sitedataArry = [{
            id: "All",
            label: "All"
        }];
        $scope.devicedataArry = [{
            id: "All",
            label: "All"
        }];
        if ($scope.customerList != "All") {
            $scope.groupFileterIds = [];
            $scope.groupGroupIds = [];
            $scope.siteFileterIds = [];
            $scope.siteIds = [];
            $scope.deviceFileterIds = [];
            $scope.deviceIds = [];
        } else {
            $scope.groupFileterIds = [{
                id: "All",
                label: "All"
            }]
            $scope.siteFileterIds = [{
                id: "All",
                label: "All"
            }]
            $scope.deviceFileterIds = [{
                id: "All",
                label: "All"
            }]
        }
        var group = {
            "filterBy": "groups",
            "searchId": $scope.customerList,
            "scheduleId": $state.params.seheduleId
        };
        ApiFactory.getApiData({
            serviceName: "scheduleapplyview",
            data: group,
            onSuccess: function (data) {
                if (data.data && data.data.length > 0) {
                    angular.forEach(data.data, function (value, index) {
                        $scope.groupdataArry.push({
                            id: value.id,
                            label: value.label
                        });
                    });
                    if (data.data[0].sl_group_id) {
                        groupids = data.data[0].sl_group_id.split(",");
                        angular.forEach(groupids, function (value, index) {
                            $scope.groupFileterIds.push({
                                id: value
                            });
                        });
                        $scope.groupdis = true;
                        filtergroupChangeselect(event);
                    }

                } else {
                    if (!$rootScope.userDetails.isSuper) {
                        $scope.groupdataArry = "";
                    }
                }
                if (data.data[0] == null || !data.data[0].sl_group_id) {
                    filterdefualtSites(function () {
                        if ($rootScope.userDetails.isSuper) {
                            filterdefualtdevice();
                        }
                    });
                }
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    };

    if ($rootScope.userDetails.isSuper == 1) {

        if ($state.current.name == "customers.customersList.getCustomer.schedule.applyView") {

            $scope.customerList = $state.params.customerId
                // $scope.filterCustomerChange();
            $scope.apllyViewCustomer();
            $scope.customerShow = true;
        } else {
            $scope.apllyViewCustomer();
            $scope.customerShow = true;
        }

        $scope.groupDisabled = true
    } else {
        if ($state.current.name == "customers.customersList.getCustomer.schedule.applyView") {
            $scope.filterCustomerChange();
        } else {
            $scope.filterCustomerChange();
        }
        $scope.customerShow = false;
        $scope.groupDisabled = true
    }
    $scope.groupDropdownEvents = {
        onItemSelect: filtergroupChangeselect,
        onItemDeselect: filtergroupChangeDeselect
    };
    $scope.siteDropdownEvents = {
        onItemSelect: filterSiteChangeselect,
        onItemDeselect: filterSiteChangeDeselect
    };
    $scope.deviceDropdownEvents = {
        onItemSelect: filterDeviceChangeselect,
        onItemDeselect: filterDeviceChangeDeselect
    };

    function filtergroupChangeDeselect(event) {
        if (event.id === "All") {
            $scope.groupFileterIds = [];
            $scope.groupGroupIds = [];
        }
        if ($scope.groupFileterIds.length == 0) {
            $scope.siteFileterIds = [];
            $scope.siteIds = [];
            filterGroupChange(event);
            filterdefualtSites();
        } else {
            filterGroupChange(event, function () {
                filterSiteChangeselect(event, 'Load', function () {
                    filterDeviceChangeselect("", 'Load');
                });
            });
        }
        if ($scope.devicedataArry.length <= 1) {
            $scope.deviceFileterIds = [];
            $scope.deviceIds = [];
        }
    }

    function filtergroupChangeselect(event, load, callback) {

        if (event.id === "All" || load == 'Load') {
            angular.forEach($scope.groupdataArry, function (value, index) {
                $scope.groupFileterIds.push({
                    id: value.id,
                    label: value.label
                });
            });
        }
        filterGroupChange(event, function () {
            filterSiteChangeselect(event, 'Load', function () {
                filterDeviceChangeselect("", 'Load');
            });
        });
    }

    function filterGroupChange(event, callback) {
        $scope.devicedataArry = [{
            id: "All",
            label: "All"
        }];
        $scope.sitedataArry = [{
            id: "All",
            label: "All"
        }];
        $scope.groupGroupIds = [];
        angular.forEach($scope.groupFileterIds, function (value, index) {
            $scope.groupGroupIds.push(value.id);
        });
        var site = {
            "filterBy": "sites",
            "searchId": $scope.groupGroupIds,
            "scheduleId": $state.params.seheduleId
        };
        if ($scope.groupFileterIds.length != 0) {
            ApiFactory.getApiData({
                serviceName: "scheduleapplyview",
                data: site,
                onSuccess: function (data) {
                    angular.forEach(data.data, function (value, index) {
                        $scope.sitedataArry.push({
                            id: value.id,
                            label: value.label
                        });

                    });
                    if (data.data[0].sl_site_id) {
                        siteids = data.data[0].sl_site_id.split(",");

                        angular.forEach(siteids, function (value, index) {
                            $scope.siteFileterIds.push({
                                id: value
                            });
                        });
                        $($element).find(".sits-dropdown").find(".dropdown-menu").addClass("readonly-actions");
                        filterSiteChange();
                    }
                    $scope.applySiteView = data.data;
                    if (!$rootScope.userDetails.isSuper) {
                        if (data.data && data.data.length == 0) {
                            $scope.sitedataArry = "";
                            $scope.devicedataArry = "";
                        }
                    }
                    callback(true)
                },
                onFailure: function (data) {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            });
        }
    };

    function filterSiteChangeDeselect(event) {
        if (event.id === "All") {
            $scope.siteFileterIds = [];
            $scope.siteIds = [];
        }
        filterSiteChange(event);
        if ($scope.devicedataArry.length <= 1) {
            $scope.deviceFileterIds = [];
            $scope.deviceIds = [];
        }
    }

    function filterSiteChangeselect(event, load, callback) {
        if (event.id === "All" || load == 'Load') {
            $scope.siteFileterIds = [];
            angular.forEach($scope.sitedataArry, function (value, index) {
                $scope.siteFileterIds.push({
                    id: value.id,
                    label: value.label
                });
            });

        }
        if (load != 'Load') {
            //  $($element).find(".group-dropdown").find(".dropdown-menu").addClass("readonly-actions");
        }
        filterSiteChange(event, callback);

    }

    function filterSiteChange(event, callback) {

        $scope.devicedataArry = [{
            id: "All",
            label: "All"
        }];
        $scope.siteIds = [];
        angular.forEach($scope.siteFileterIds, function (value, index) {
            $scope.siteIds.push(value.id);
        });

        var device = {
            "filterBy": "device",
            "searchId": $scope.siteIds
        };
        ApiFactory.getApiData({
            serviceName: "scheduleapplyview",
            data: device,
            onSuccess: function (data) {
                angular.forEach(data.data, function (value, index) {
                    $scope.devicedataArry.push({
                        id: value.id,
                        label: value.label
                    });
                });
                $scope.applyDeviceView = data.data;
                if (!$rootScope.userDetails.isSuper) {
                    if (data.data && data.data.length == 0) {
                        $scope.devicedataArry = "";
                    }
                }
                if (callback && typeof (callback) == "function") {
                    callback(true);
                }

            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    };

    function filterDeviceChangeDeselect(event) {
        if (event.id === "All") {
            $scope.deviceFileterIds = [];
            $scope.deviceIds = [];
        }
    }

    function filterDeviceChangeselect(event, load) {
        $scope.isErrorhide = false;
        if (event.id === "All" || load == 'Load') {
            if ($scope.devicedataArry.length > 1) {
                $scope.deviceFileterIds = []
                angular.forEach($scope.devicedataArry, function (value, index) {
                    $scope.deviceFileterIds.push({
                        id: value.id,
                        label: value.label
                    });
                });
            }
        }
        if (load == 'Load') {
            // $($element).find(".device-dropdown").find(".dropdown-menu").addClass("readonly-actions");
        }
    }

    function filterdefualtSites(callback) {
        if ($state.current.name == "customers.customersList.getCustomer.schedule.applyView") {
            $scope.customerList = $state.params.customerId
        }
        var site = {
            "filterBy": "sitesdefualt",
            "searchId": $scope.customerList,
            "scheduleId": $state.params.seheduleId
        };
        ApiFactory.getApiData({
            serviceName: "scheduleapplyview",
            data: site,
            onSuccess: function (data) {
                angular.forEach(data.data, function (value, index) {
                    $scope.sitedataArry.push({
                        id: value.id,
                        label: value.label
                    });
                });
                if (data.data[0].sl_site_id) {
                    siteids = data.data[0].sl_site_id.split(",");

                    angular.forEach(siteids, function (value, index) {
                        $scope.siteFileterIds.push({
                            id: value
                        });
                    });
                    $scope.groupdis = true;
                    $scope.sitedis = true;
                    filterSiteChangeselect(event);
                }
                if (!$rootScope.userDetails.isSuper) {
                    if (data.data && data.data.length == 0) {
                        $scope.sitedataArry = "";
                        $scope.devicedataArry = "";
                    }
                }
                if (data.data[0].sl_site_id) {
                    //callback(false)
                } else {
                    callback(true)
                }
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
                callback(true)
            }
        });

    };

    function filterdefualtdevice() {
        if ($state.current.name == "customers.customersList.getCustomer.schedule.applyView") {
            $scope.customerList = $state.params.customerId
        }
        var defultdevice = {
            "filterBy": "devicedefualt",
            "searchId": $scope.customerList
        };
        ApiFactory.getApiData({
            serviceName: "scheduleapplyview",
            data: defultdevice,
            onSuccess: function (data) {
                angular.forEach(data.data, function (value, index) {
                    $scope.devicedataArry.push({
                        id: value.id,
                        label: value.label
                    });
                });
                $scope.applyDeviceView = data.data;

            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });
    };
    $scope.applySchedule = function () {

        if (!$rootScope.userDetails.isSuper) {
            $scope.customerList = "";
        }
        var validateData = "";
        var scheduleName = "";
        $scope.deviceIds = [];
        angular.forEach($scope.deviceFileterIds, function (value, index) {
            $scope.deviceIds.push(value.id);
        });
        $scope.groupGroupIds = [];
        angular.forEach($scope.groupFileterIds, function (value, index) {
            $scope.groupGroupIds.push(value.id);
        });
        $scope.siteIds = [];
        angular.forEach($scope.siteFileterIds, function (value, index) {
            $scope.siteIds.push(value.id);

        });
        /* if(!$rootScope.userDetails.isSuper){
	       if ($scope.deviceIds.length == 0) {
	            $scope.isErrorhide = true;
	            return
	        }
        }*/
        var scheduleApplysave = {
            "customerId": $scope.customerList,
            "groupIds": $scope.groupGroupIds,
            "siteIds": $scope.siteIds,
            "deviceIds": $scope.deviceIds,
            "scheduleId": $state.params.seheduleId
                // "validateFlag": "true"

        }
        if (!$rootScope.userDetails.isSuper) {
            $scope.isvalidateError = false;
            if ($scope.groupGroupIds == "" && $scope.siteIds == "" && $scope.deviceIds == "") {
                $scope.validateError = "Please select at least one option"
                $scope.isvalidateError = true;
                return
            }
        }

        ApiFactory.getApiData({
            serviceName: "scheduleapplysave",
            data: scheduleApplysave,
            onSuccess: function (data) {
                var count = 0;
                var loopcount = 1;
                $scope.applyDeviceView = data.data;
                if (data.data.Flag == "validateFail") {
                    /* angular.forEach(data.data.groupSitesList, function (value, index) {
                         if (value.name.split("@@")[1] == null) {
                             if (loopcount < data.data.groupSitesList.length && data.data.groupSitesList.length > 1) {
                                 validateData += value.name + ",";
                             } else {
                                 validateData += value.name
                             }
                         } else {
                             count++;
                             if (loopcount < data.data.groupSitesList.length && data.data.groupSitesList.length > 1) {
                                 validateData += value.name.split("@@")[0] + ",";
                             } else {
                                 validateData += value.name.split("@@")[0]
                             }
                             scheduleName = value.name.split("@@")[1]
                         }
                         loopcount++;
                     });*/
                    if (count == 0) {
                        $scope.validateError = "There are no devices to download schedule to.";
                    } else {
                        $scope.validateError = "Unable to download because " + validateData + " is in the process of downloading " + scheduleName;
                    }
                    $scope.isvalidateError = true;
                } else {
                    //$scope.applyScheduleDevice();
                    toastr.success(messageFactory.getMessage(data.code));
                    if ($state.current.name == "customers.customersList.getCustomer.schedule.applyView") {
                        $state.go("customers.customersList.getCustomer.schedule", {}, {
                            reload: true
                        });
                        $rootScope.loadDataByTab();

                    } else if ($state.current.name == "groups.groupList.groupInfo.schedules.applyView") {
                        $state.go("groups.groupList.groupInfo.schedules", {}, {
                            reload: true
                        });
                        $rootScope.loadDataByTab();

                    } else if ($state.current.name == "sites.sitesList.viewSite.schedules.applyView") {
                        $state.go("sites.sitesList.viewSite.schedules", {}, {
                            reload: true
                        });
                        $rootScope.loadDataByTab();

                    } else if ($state.current.name == "scheduler.schedulerList.getSchedule.scheduler.applyView") {
                        $state.go("scheduler.schedulerList.getSchedule.scheduler", {}, {
                            reload: true
                        });
                        $rootScope.loadDataByTab();

                    } else {
                       // $state.go("scheduler.schedulerList");
                        $state.go("scheduler.schedulerList", {}, {
                            reload: true
                        });
                        $rootScope.loadSchedulerList();
                    }

                }
            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });

    };

}).controller("addScheduleController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {
    $scope.daysOfWeek = [{
        "label": "Monday",
        "value": 1
	}, {
        "label": "Tuesday",
        "value": 2
	}, {
        "label": "Wednesday",
        "value": 3
	}, {
        "label": "Thursday",
        "value": 4
	}, {
        "label": "Friday",
        "value": 5
	}, {
        "label": "Saturday",
        "value": 6
	}, {
        "label": "Sunday",
        "value": 7
	}];

    $scope.selectedCustomers = [];
    $scope.multiSelectCustomerSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        enableSearch: true,
        showCheckAll: false,
        showUncheckAll: false,
        closeOnSelect: true
    };
    $scope.customersDropdownEvents = {
        onItemSelect: customerChangeselect,
        onItemDeselect: customerChangeDeselect,
        onSelectAll: customersSelectAll,
        onDeselectAll: customersUnSelectAll
    };
    $scope.selectedGroups = [];
    $scope.multiSelectGroupsSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 3,
        scrollable: true,
        showCheckAll: true,
        showUncheckAll: true,
        enableSearch: true,
        dynamicTitle: true,
        displayProp: "groupName",
        idProp: "groupId"

    };
    $scope.groupsDropdownEvents = {
        onItemSelect: groupsChangeSelect,
        onItemDeselect: groupsChangeDeselect,
        onSelectAll: groupsSelectAll,
        onDeselectAll: groupsUnSelectAll

    };
    $scope.selectedSites = [];
    $scope.multiSelectSitesSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 3,
        scrollable: true,
        showCheckAll: true,
        enableSearch: true,
        showUncheckAll: true,
        dynamicTitle: true,
        displayProp: "siteName",
        idProp: "siteId"

    };
    $scope.sitesDropdownEvents = {
        onItemSelect: sitesChangeselect,
        onItemDeselect: sitesChangeDeselect
    };

    var cstrSelectAll = false;

    function customerChangeselect(callback) {

        if (callback && typeof (callback) == "object") {
            $scope.selectedCustomers = [callback];
        }

        if (cstrSelectAll) {
            return;
        }

        var selectedCustomers = [];
        $.each($scope.selectedCustomers, function (key, value) {
            selectedCustomers.push(value.id);
        })

        usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {

            $scope.groupsDataArry = data.groups;


            var selectedGups = [];
            $.each($scope.selectedGroups, function (key, value) {
                $.each($scope.groupsDataArry, function (key1, value1) {
                    if (value1.groupId == value.id) {
                        var gup = {};
                        gup.id = value.id;
                        selectedGups.push(gup);
                    }
                })
            })
            $scope.selectedGroups = selectedGups;


            if ($scope.selectedGroups.length == 0) {
                $scope.sitesDataArry = data.sites;
                var selectedSts = [];
                $.each($scope.selectedSites, function (key, value) {

                    $.each($scope.sitesDataArry, function (key1, value1) {
                        if (value1.siteId == value.id) {
                            var sit = {};
                            sit.id = value.id;
                            selectedSts.push(sit);
                        }
                    })
                })
                $scope.selectedSites = selectedSts;
            }




            if (callback && typeof (callback) == "function") {
               
                callback(true);
            }
        })

    }

    function customerChangeDeselect() {
        var selectedCustomers = [];
        $.each($scope.selectedCustomers, function (key, value) {
            selectedCustomers.push(value.id);
        })
        if ($scope.selectedCustomers.length == 0) {
            $scope.groupsDataArry = [];
            $scope.sitesDataArry = [];
            $scope.selectedGroups = [];
            $scope.selectedSites = [];
            return;
        }
        usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {

            $scope.groupsDataArry = data.groups;
            
              var selectedGups = [];
            $.each($scope.selectedGroups, function (key, value) {
                $.each($scope.groupsDataArry, function (key1, value1) {
                    if (value1.groupId == value.id) {
                        var gup = {};
                        gup.id = value.id;
                        selectedGups.push(gup);
                    }
                })
            })
            $scope.selectedGroups = selectedGups;
            
            if ($scope.selectedGroups.length == 0) {
                $scope.sitesDataArry = data.sites;
                var selectedSts = [];
                $.each($scope.selectedSites, function (key, value) {
                    $.each($scope.sitesDataArry, function (key1, value1) {
                        if (value1.siteId == value.id) {
                            var sit = {};
                            sit.id = value.id;
                            selectedSts.push(sit);
                        }
                    })
                })
                $scope.selectedSites = selectedSts;
            }
          
        })
    }

    function customersSelectAll() {
        cstrSelectAll = true;
        setTimeout(function () {
            cstrSelectAll = false;
            customerChangeselect();
        }, 500)
    }

    function customersUnSelectAll() {
        $scope.groupsDataArry = [];
        $scope.sitesDataArry = [];
        $scope.selectedGroups = [];
        $scope.selectedSites = [];
    }
    var grpSelectAll = false;

    function groupsChangeSelect() {
        if (grpSelectAll) {
            return;
        }
        usersFactory.getSites($scope.selectedGroups, function (data) {
            var sitesData = [];
            $.each(data, function (key, value) {
                var site = {};
                site.siteId = value.id;
                site.siteName = value.name;
                sitesData.push(site);
            });
            $scope.sitesDataArry = sitesData;
            var selectedSts = [];
            $.each($scope.selectedSites, function (key, value) {

                $.each($scope.sitesDataArry, function (key1, value1) {
                    if (value1.siteId == value.id) {
                        var sit = {};
                        sit.id = value.id.toString();
                        selectedSts.push(sit);
                    }
                })
            });

            $scope.selectedSites = selectedSts;
        });
    }

    function groupsChangeDeselect() {
        if ($scope.selectedGroups.length == 0) {
            $scope.sitesDataArry = [];
            $scope.selectedSites = [];
            customerChangeselect();
            return;
        }
        usersFactory.getSites($scope.selectedGroups, function (data) {
            var sitesData = [];
            $.each(data, function (key, value) {
                var site = {};
                site.siteId = value.id;
                site.siteName = value.name;
                sitesData.push(site);
            });
            $scope.sitesDataArry = sitesData;
            var selectedSts = [];
            $.each($scope.selectedSites, function (key, value) {

                $.each($scope.sitesDataArry, function (key1, value1) {
                    if (value1.siteId == value.id) {
                        var sit = {};
                        sit.id = value.id.toString();
                        selectedSts.push(sit);
                    }
                })
            });

            $scope.selectedSites = selectedSts;
        });
    }

    function groupsSelectAll() {
        grpSelectAll = true;
        setTimeout(function () {
            grpSelectAll = false;
            groupsChangeSelect();
        }, 500)
    }

    function groupsUnSelectAll() {
        $scope.sitesDataArry = [];
        $scope.selectedSites = [];
        customerChangeselect();
    }

    function sitesChangeselect() {}

    function sitesChangeDeselect() {}
    if ($state.params && $state.params.sourcePage && $state.params.sourcePage == 'schedules') {
        commonFactories.getCustomers(function (data) {
            if (data && data.length > 0) {
                $scope.customersDataArry = [];
                $scope.selectedCustomers = [];
                $scope.groupsDataArry = [];
                $scope.selectedGroups = [];
                $scope.sitesDataArry = [];
                $scope.selectedSites = [];
                $scope.customersDataArry = data;
            }
        });
    }

    $scope.noofrows = ['1', '2', '3', '4'];
    $scope.errorMsg = [];
    $.each($scope.noofrows, function (key) {
        $scope.errorMsg.push(false);
    });
    $scope.schedule = {};
    $scope.schedule.timePoints = [{}];
    $scope.schedule.dayandheatingpnts = [{}];
    var finalMap = new Object();
    finalMap = null;
    $scope.checkAllDays = function () {
        if ($scope.checkAll) {
            $scope.schedule.daysOfWeek = {
                "1": 1,
                "2": 2,
                "3": 3,
                "4": 4,
                "5": 5,
                "6": 6,
                "7": 7
            };
            compareAll();
            return;
        }
        $scope.schedule.daysOfWeek = null;
        $scope.checkAll = false;
        $.each($scope.schedule.timePoints, function (key) {
            $scope.schedule.timePoints[key].time = '';
            $scope.schedule.timePoints[key].htpoint = '';
            $scope.schedule.timePoints[key].clpoint = '';
            $scope.schedule.timePoints[key].am = '1';
        });
    }

    function compareAll() {
        if (!finalMap) {
            return;
        } else {
            $scope.schedule.timepointsmap = angular.copy(finalMap);
        }
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        var days = [];
        $.each(daysArr, function (key, value) {
            var k = angular.copy($scope.schedule.timepointsmap[value]);
            var k1 = angular.copy($scope.schedule.timepointsmap[value]);
            if (k) {
                for (var i = 0; i < k.length; i++) {
                    delete k1[i].dow_id
                }
                days.push(JSON.stringify(k1));
            }
        })
        var compare = true;
        var daysArray = Object.keys(days);
        for (var i = 0; i < daysArray.length; i++) {
            if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                compare = false;
            }
        }
        if (Object.keys(days).length != daysArr.length) {
            compare = false;
        }
        if (compare) {
            var data = [];
            $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                var temp = angular.copy(value);
                var time = new Date();
                var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                time.setHours(hrs);
                time.setMinutes(mns);
                temp.time = time;
                data.push(temp);
            })
            var data = data.sort(function (a, b) {
                // Turn your strings into dates, and then subtract them
                // to get a value that is either negative, positive, or zero.
                return new Date(b.time) - new Date(a.time);
            }).reverse();
            $scope.schedule.timePoints = angular.copy(data);
            return;
        }
    }
    
    
    $scope.schedule.show = false;
    $scope.populateTimePoints = function (index, $event) {
        if (!finalMap) {
            return;
        } else {
            $scope.schedule.timepointsmap = angular.copy(finalMap);
        }
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        if (daysArr.length == 1) {
            if ($scope.schedule.timepointsmap[daysArr[0]]) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            }
        } else {
            if ($($event.target).prop("checked")) {
                if (!$scope.schedule.timepointsmap[index]) {
                    $.each($scope.schedule.timePoints, function (key) {
                        $scope.schedule.timePoints[key].time = '';
                        $scope.schedule.timePoints[key].htpoint = '';
                        $scope.schedule.timePoints[key].clpoint = '';
                        $scope.schedule.timePoints[key].am = '1';
                    });
                    return;
                }
            }
            var days = [];
            $.each(daysArr, function (key, value) {
                var k = angular.copy($scope.schedule.timepointsmap[value]);
                var k1 = angular.copy($scope.schedule.timepointsmap[value]);
                if (k) {
                    for (var i = 0; i < k.length; i++) {
                        delete k1[i].dow_id
                    }
                    days.push(JSON.stringify(k1));
                }
            })
            var compare = true;
            var daysArray = Object.keys(days);
            for (var i = 0; i < daysArray.length; i++) {
                if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                    compare = false;
                }
            }
            if (Object.keys(days).length != daysArr.length) {
                compare = false;
            }
            if (compare) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            } else {
                $.each($scope.schedule.timePoints, function (key) {
                    $scope.schedule.timePoints[key].time = '';
                    $scope.schedule.timePoints[key].htpoint = '';
                    $scope.schedule.timePoints[key].clpoint = '';
                    $scope.schedule.timePoints[key].am = '1';
                });
            }
        }
    };
    $scope.chooseWeekVal = function () {
        var daysOfWeek = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysOfWeek.push(value);
            }
        });
        if (daysOfWeek.length > 0) {
            return false;
        }
        return true;
    }

    function storeDependencies() {
        $scope.schedule.customersList = [];
        $scope.selectedCustomers = angular.copy(usersFactory.getUniqueArray($scope.selectedCustomers, "id"))
        $.each($scope.selectedCustomers, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.customersList.push(value.id);
            }
        });
        $scope.schedule.groupsList = [];
        $scope.selectedGroups = angular.copy(usersFactory.getUniqueArray($scope.selectedGroups, "id"))
        $.each($scope.selectedGroups, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.groupsList.push(value.id);
            }
        });
        $scope.schedule.locationsList = [];
        $scope.selectedSites = angular.copy(usersFactory.getUniqueArray($scope.selectedSites, "id"));
        $.each($scope.selectedSites, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.locationsList.push(value.id);
            }
        });
    }
    
   /* $scope.validateTimePoints = function(){
	var times = [];
        $.each($scope.schedule.timePoints, function (key, value) {
            times.push(value.time);
        })
        var sameTimepoints = false;
        for (var i = 0; i < times.length; i++) {
            for (var j = i + 1; j < times.length; j++) {
                if (times[j]) {
                    var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                    if (Math.abs(diff) < 30) {
                        $scope.errorMsg[j] = true;
                      
                    }
                }
            }
        }
    }*/
    $scope.$watch("schedule.timePoints", function(){

	 for (var j = 0; j < $scope.errorMsg.length; j++) {
	            $scope.errorMsg[j] = false;
	        }
	var times = [];
        $.each($scope.schedule.timePoints, function (key, value) {
            times.push(value.time);
        })
 
        for (var i = 0; i < times.length; i++) {
            for (var j = i + 1; j < times.length; j++) {
                if (times[j]) {
                    var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                    if (Math.abs(diff) < 30) {
                        $scope.errorMsg[j] = true;
                      
                    }
                }
            }
        }
	}, true);

    $scope.addNewShedule = function (form) {
        for (var j = 0; j < $scope.errorMsg.length; j++) {
            $scope.errorMsg[j] = false;
        }
        // store depedencies
        storeDependencies();

        var times = [];
        $.each($scope.schedule.timePoints, function (key, value) {
            times.push(value.time);
        })
        var sameTimepoints = false;
        for (var i = 0; i < times.length; i++) {
            for (var j = i + 1; j < times.length; j++) {
                if (times[j]) {
                    var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                    if (Math.abs(diff) < 30) {
                        $scope.errorMsg[j] = true;
                        sameTimepoints = true;
                    }
                }
            }
        }
        if (sameTimepoints) {
            // toastr.error("Please maintain 30 minutes time gap between each time period");
            return false;
        }
        var daysOfWeek = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysOfWeek.push(value);
            }
        });
        $scope.chooseWeekVal();
        if (form.$valid && daysOfWeek.length) {
            var data = [];
            $.each($scope.schedule.timePoints, function (key, value) {
                var temp = angular.copy(value);
                temp.time = moment(value.time).format("hh:mm A").split(" ")[0];
                temp.am = moment(value.time).format("hh:mm A").split(" ")[1].toLowerCase() == "am" ? 1 : 2;
                data.push(temp);
            });
            var sameTimepoints = false;
            for (var i = 0; i < data.length; i++) {
                for (var j = i + 1; j < data.length; j++) {
                    if (data[j]) {
                        if (data[i].time === data[j].time &&
                            data[i].am === data[j].am) {
                            sameTimepoints = true;
                            $scope.errorMsg[j] = true;
                        }
                    }
                }
            }
            if (sameTimepoints) {
                //toastr.error("Please enter data for different time points");
                return false;
            }
            form.$submitted = false;
            $scope.schedule.dayandheatingpnts = [{}];
            var map = new Object();

            $.each($scope.schedule.daysOfWeek, function (key, value) {
                if (value && value != -1) {
                    map[key] = angular.copy(data);
                }
            });
            finalMap = $.extend(finalMap, map);
            $scope.events = angular.copy(finalMap);
            emptyFields(form);
        }
    };

    function emptyFields(form) {
        form.$submitted = false;
        $timeout(function () {
            $.each($scope.schedule.timePoints, function (key) {
                $scope.schedule.timePoints[key].time = '';
                $scope.schedule.timePoints[key].htpoint = '';
                $scope.schedule.timePoints[key].clpoint = '';
                $scope.schedule.timePoints[key].am = '1';
            });

            $scope.schedule.daysOfWeek = null;
            $scope.checkAll = false;

        }, 500)
    }
    $scope.cancelSchedule = function (form) {
        form.$submitted = false;
        $.each($scope.schedule.timePoints, function (key) {
            $scope.schedule.timePoints[key].time = null;
            $scope.schedule.timePoints[key].htpoint = null;
            $scope.schedule.timePoints[key].clpoint = null;
            $scope.schedule.timePoints[key].am = '1';
        });
        $scope.schedule.daysOfWeek = null;
        $scope.checkAll = false;
    }

    $scope.saveSchedule = function (form) {
        form.$submitted = false;
        if (finalMap == null) {
            toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_ALL_DATA_8002"));
            return;
        }
        $scope.schedule.timepointsmap = finalMap;
        if (Object.keys($scope.schedule.timepointsmap).length < 7) {
            toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_ALL_DATA_8002"));
            return;
        }
        if (Object.keys($scope.schedule.timepointsmap).length < 7) {
            var emptyWeeks = [' Monday', ' Tuesday', ' Wednesday', ' Thursday', ' Friday', ' Saturday', ' Sunday', ];

            $.each($scope.schedule.timepointsmap, function (key, value) {
                delete emptyWeeks[key - 1];
            })
            emptyWeeks = emptyWeeks.filter(function (element) {
                return element != null;
            })
            var weeks;
            if (emptyWeeks.length > 2) {
                var day = emptyWeeks.toString();
                var sub = day.substr(0, day.lastIndexOf(","));
                weeks = sub + " and " + emptyWeeks[emptyWeeks.length - 1];

            } else {
                weeks = emptyWeeks.toString().replace(",", " and");
            }

            $('#modelDialog').on('show.bs.modal', function (event) {
                var modal = $(this)
                modal.find('.modal-title').text('There is no schedule for ' + weeks);
                modal.find('.model-content').text('Are you sure you want to continue')
                $(this).off('show.bs.modal');
            });
            $('#modelDialog').modal("show");
            return;
        }
        var obj = {};
        if ($state.params.customerId) {
            obj.customerId = $state.params.customerId
        }
        if ($state.params.siteId) {
            obj.siteId = $state.params.siteId
        }
        if ($state.params.groupId) {
            obj.groupId = $state.params.groupId
        }

        storeDependencies();
        var data = $.extend(angular.copy($scope.schedule), obj);
        if ($state.params && $state.params.sourcePage && $state.params.sourcePage == 'schedules') {
            data = angular.copy(data);
            data.customerId = angular.copy(data.customersList).toString();
            data.groupId = angular.copy(data.groupsList).toString();
            data.siteId = angular.copy(data.locationsList).toString();

        }
        delete data.customersList;
        delete data.groupsList;
        delete data.locationsList;
        ApiFactory.getApiData({
            serviceName: "addschedule",
            data: data,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    toastr.success(messageFactory.getMessage(data.code));

                    history.back();

                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        })
    };
    $rootScope.accept = function () {
        $('#modelDialog').modal("hide");
        ApiFactory.getApiData({
            serviceName: "addschedule",
            data: $scope.schedule,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    toastr.success(messageFactory.getMessage(data.code));

                    $state.go("scheduler.schedulerList");

                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        })
    }
}).controller("scheduleInfoController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state) {

    $scope.editSchedule = function () {
        $state.go("scheduler.schedulerList.getSchedule.editSchedule", {
            scheduleId: $state.params.scheduleId
        });
    };

    $scope.getAssignedNames = function (name) {
        return name && name.split(",");
    }

    $scope.gotoSchedules = function () {
	$state.go("scheduler.schedulerList.getSchedule.scheduler", $state.params);
    }
    $scope.gotoGroups = function () {

        $state.go("scheduler.schedulerList.getSchedule.groups", $state.params);
    }
    $scope.gotoSites = function () {

        $state.go("scheduler.schedulerList.getSchedule.sites", $state.params);
    }
    $scope.gotoDevices = function () {

        $state.go("scheduler.schedulerList.getSchedule.devices", $state.params);
    }

    if ($rootScope.userDetails.isSuper || $rootScope.userDetails.rolePermissions['Schedule Management'] == 2) {
        $scope.applybuttonhide = true;
        $scope.copybuttonhide = true;
        $scope.addbuttonhide = true;
    }
    if ($state.current.name != "scheduler.schedulerList.getSchedule.scheduler") {
        $rootScope.schedule = {};
        ApiFactory.getApiData({
            serviceName: "getSchedule",
            data: $state.params,
            onSuccess: function (data) {
                $rootScope.schedule = data.data;
            },
            onFailure: function () {}
        })
    }


}).controller("viewScheduleController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state) {
    if($rootScope.$previousState.name != 'scheduler.schedulerList.getSchedule.devices'){
	    $rootScope.schedule ={};
    }
    
    ApiFactory.getApiData({
        serviceName: "getSchedule",
        data: $state.params,
        onSuccess: function (data) {
            $rootScope.schedule = {};
            $rootScope.schedule = data.data;
        },
        onFailure: function () {}
    });
    $scope.applySchedule = function (schedule) {
        if (
            /*$state.current.name == "customers.customersList.getCustomer.schedule"||$state.current.name == "groups.groupList.groupInfo.schedules"
                     	  ||$state.current.name == "sites.sitesList.viewSite.schedules"||*/
            $state.current.name == "sites.sitesList.viewSite.Test") {
            $scope.applydeviceSchedule(schedule)
        } else {
            $rootScope.selectedSchedule = schedule
            if ($state.current.name == "scheduler.schedulerList") {
                $state.go("scheduler.schedulerList.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "customers.customersList.getCustomer.schedule") {
                $state.go("customers.customersList.getCustomer.schedule.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "groups.groupList.groupInfo.schedules") {

                $state.go("groups.groupList.groupInfo.schedules.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "sites.sitesList.viewSite.schedules") {

                $state.go("sites.sitesList.viewSite.schedules.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
            if ($state.current.name == "scheduler.schedulerList.getSchedule.scheduler") {

                $state.go("scheduler.schedulerList.getSchedule.scheduler.applyView", {
                    seheduleId: schedule.scheduleId
                })
            }
        }
    };
    $scope.editSchedule = function () {
        $state.go("scheduler.schedulerList.getSchedule.editSchedule", {
            scheduleId: $state.params.scheduleId
        });
    };

    if ($rootScope.userDetails.isSuper || $rootScope.userDetails.rolePermissions['Schedule Management'] == 2) {
        $scope.applybuttonhide = true;
        $scope.copybuttonhide = true;
        $scope.addbuttonhide = true;
    }

}).controller("editScheduleController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {
    
    
    
    $scope.daysOfWeek = [{
        "label": "Monday",
        "value": 1
	}, {
        "label": "Tuesday",
        "value": 2
	}, {
        "label": "Wednesday",
        "value": 3
	}, {
        "label": "Thursday",
        "value": 4
	}, {
        "label": "Friday",
        "value": 5
	}, {
        "label": "Saturday",
        "value": 6
	}, {
        "label": "Sunday",
        "value": 7
	}];

    $scope.getAssignedNames = function (name) {
        return name && name.split(",");
    }
    $scope.noofrows = ['1', '2', '3', '4'];
    $scope.errorMsg = [];
    $.each($scope.noofrows, function (key) {
        $scope.errorMsg.push(false);
    });
    $scope.schedule = {};
    $scope.schedule.timePoints = [{}];
    var finalMap = new Object();
    $scope.schedule.show = false;
    $scope.checkAllDays = function () {
        if ($scope.checkAll) {
            $scope.schedule.daysOfWeek = {
                "1": 1,
                "2": 2,
                "3": 3,
                "4": 4,
                "5": 5,
                "6": 6,
                "7": 7
            };

            compareAll();
            return;
        }
        $scope.schedule.daysOfWeek = null;
        $scope.checkAll = false;
        $.each($scope.schedule.timePoints, function (key) {
            $scope.schedule.timePoints[key].time = '';
            $scope.schedule.timePoints[key].htpoint = '';
            $scope.schedule.timePoints[key].clpoint = '';
            $scope.schedule.timePoints[key].am = '1';
        });
    }

    function compareAll() {
        if (!finalMap) {
            return;
        } else {
            $scope.schedule.timepointsmap = angular.copy(finalMap);
        }
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        var days = [];
        $.each(daysArr, function (key, value) {
            var k = angular.copy($scope.schedule.timepointsmap[value]);
            var k1 = angular.copy($scope.schedule.timepointsmap[value]);
            if (k) {
                for (var i = 0; i < k.length; i++) {
                    delete k1[i].dow_id
                }
                days.push(JSON.stringify(k1));
            }
        })
        var compare = true;
        var daysArray = Object.keys(days);
        for (var i = 0; i < daysArray.length; i++) {
            if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                compare = false;
            }
        }
        if (Object.keys(days).length != daysArr.length) {
            compare = false;
        }
        if (compare) {
            var data = [];
            $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                var temp = angular.copy(value);
                var time = new Date();
                var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                time.setHours(hrs);
                time.setMinutes(mns);
                temp.time = time;
                data.push(temp);
            })
            var data = data.sort(function (a, b) {
                // Turn your strings into dates, and then subtract them
                // to get a value that is either negative, positive, or zero.
                return new Date(b.time) - new Date(a.time);
            }).reverse();
            $scope.schedule.timePoints = angular.copy(data);
            return;
        }
    }
    $scope.$watch('schedule.timePoints', function (data) {
        if (data) {
            $.each($scope.schedule.timePoints, function (key, value) {
                if (value.time) {
                    if (key < 3) {
                        // $scope.validateMinPoints[key + 1] = new Date(moment(value.time).add(0.5, 'hours'));
                        //$scope.validateMaxPoints[key - 1] = new Date(moment(value.time).subtract(0.5, 'hours'));
                    }
                    // var start = new Date();
                    // start.setHours(0, 0, 0, 0);
                    // var end = new Date();
                    // end.setHours(23, 59, 59, 999);
                    // $scope.validateMinPoints[0] = start;
                    //$scope.validateMaxPoints[0] = end;
                    //$scope.validateMaxPoints[3] = end;
                }
            });
        }
    }, true);
    
   

    $scope.populateTimePoints = function (index, $event) {
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        if (daysArr.length == 0) {
            $.each($scope.schedule.timePoints, function (key) {
                $scope.schedule.timePoints[key].time = '';
                $scope.schedule.timePoints[key].htpoint = '';
                $scope.schedule.timePoints[key].clpoint = '';
                $scope.schedule.timePoints[key].am = '1';
            });
            return;
        }
        if (daysArr.length == 1) {
            if ($scope.schedule.timepointsmap[daysArr[0]]) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            }
        } else {
            if ($($event.target).prop("checked")) {
                if (!$scope.schedule.timepointsmap[index]) {
                    $.each($scope.schedule.timePoints, function (key) {
                        $scope.schedule.timePoints[key].time = '';
                        $scope.schedule.timePoints[key].htpoint = '';
                        $scope.schedule.timePoints[key].clpoint = '';
                        $scope.schedule.timePoints[key].am = '1';
                    });
                    return;
                }
            }
            var days = [];
            $.each(daysArr, function (key, value) {
                var k = angular.copy($scope.schedule.timepointsmap[value]);
                var k1 = angular.copy($scope.schedule.timepointsmap[value]);
                if (k) {
                    for (var i = 0; i < k.length; i++) {
                        delete k1[i].dow_id
                    }
                    days.push(JSON.stringify(k1));
                }
            })
            var compare = true;
            var daysArray = Object.keys(days);
            for (var i = 0; i < daysArray.length; i++) {
                if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                    compare = false;
                }
            }
            if (Object.keys(days).length != daysArr.length) {
                compare = false;
            }
            if (compare) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            } else {
                $.each($scope.schedule.timePoints, function (key) {
                    $scope.schedule.timePoints[key].time = '';
                    $scope.schedule.timePoints[key].htpoint = '';
                    $scope.schedule.timePoints[key].clpoint = '';
                    $scope.schedule.timePoints[key].am = '1';
                });
            }
        }
    };
    if (angular.isUndefined($rootScope.schedule)) {
        ApiFactory.getApiData({
            serviceName: "getSchedule",
            data: $state.params,
            onSuccess: function (data) {
                $rootScope.schedule = data.data;
                getSchedule();
            },
            onFailure: function () {}
        })
    } else {
        getSchedule()
    }

    function getSchedule() {
        $scope.schedule = $.extend($scope.schedule, $rootScope.schedule);
        finalMap = $rootScope.schedule.timepointsmap;
        $scope.events = angular.copy(finalMap);
        $scope.editShedule = function (form) {
            for (var j = 0; j < $scope.errorMsg.length; j++) {
                $scope.errorMsg[j] = false;
            }
            var times = [];
            $.each($scope.schedule.timePoints, function (key, value) {
                times.push(value.time);
            })
            var sameTimepoints = false;
            for (var i = 0; i < times.length; i++) {
                for (var j = i + 1; j < times.length; j++) {
                    if (times[j]) {
                        var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                        if (Math.abs(diff) < 30) {
                            sameTimepoints = true;
                            $scope.errorMsg[j] = true;
                        }
                    }
                }
            }
            if (sameTimepoints) {
                //toastr.error("Please maintain 30 minutes time gap between each time period");
                return false;
            }

            if (form.$valid && $scope.schedule.daysOfWeek) {
                form.$submitted = false;
                $scope.schedule.dayandheatingpnts = [{}];
                var map = new Object();
                var data = [];
                $.each($scope.schedule.timePoints, function (key, value) {
                    var temp = angular.copy(value);
                    temp.time = moment(value.time).format("hh:mm A").split(" ")[0];
                    temp.am = moment(value.time).format("hh:mm A").split(" ")[1].toLowerCase() == "am" ? 1 : 2;
                    data.push(temp);
                });
                var sameTimepoints = false;
                for (var i = 0; i < data.length; i++) {
                    for (var j = i + 1; j < data.length; j++) {
                        if (data[j]) {
                            if (data[i].time === data[j].time &&
                                data[i].am === data[j].am) {
                                $scope.errorMsg[j] = true;
                                sameTimepoints = true;
                            }
                        }
                    }
                }
                if (sameTimepoints) {
                    // toastr.error("Please enter data for different time points");
                    return false;
                }
                $.each($scope.schedule.daysOfWeek, function (key, value) {
                    if (value && value != -1) {
                        map[key] = angular.copy(data);
                    }
                });
                finalMap = $.extend(finalMap, map);
                $scope.eventsUpdate = true;
                $scope.events = angular.copy(finalMap);
                emptyFields(form);
            }
        };

        function emptyFields(form) {
            form.$submitted = false;
            $timeout(function () {
                $.each($scope.schedule.timePoints, function (key) {
                    $scope.schedule.timePoints[key].time = '';
                    $scope.schedule.timePoints[key].htpoint = '';
                    $scope.schedule.timePoints[key].clpoint = '';
                    $scope.schedule.timePoints[key].am = '1';
                });
                $scope.schedule.daysOfWeek = null;
                $scope.checkAll = false;
            }, 500)
        }
        $scope.cancelSchedule = function (form) {
            form.$submitted = false;
            $.each($scope.schedule.timePoints, function (key) {
                $scope.schedule.timePoints[key].time = null;
                $scope.schedule.timePoints[key].htpoint = null;
                $scope.schedule.timePoints[key].clpoint = null;
                $scope.schedule.timePoints[key].am = '1';
            });
            $scope.schedule.daysOfWeek = null;
            $scope.checkAll = false;
            finalMap = null;
        }
        $scope.updateSchedule = function (form) {
            form.$submitted = false;
            if (Object.keys(finalMap).length <= 0) {
                toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_DATA_8002"));
                return false;
            }
            $scope.schedule.timepointsmap = finalMap;
            $scope.schedule.oldName = $rootScope.schedule.scheduleName;
            $('#modelDialog').on('show.bs.modal', function (event) {
                var modal = $(this)
                modal.find('.modal-title').text('Update schedule');
                modal.find('.model-content').text('This action will apply updated schedule to all assigned devices. Are you sure you want to update this schedule?')
                $(this).off('show.bs.modal');
                $(this).find(".confirm")[0].action = 'update';
                $(this).find(".cancel")[0].cancelValue = true;
            });
            $('#modelDialog').modal("show");
            return;
        };
        $scope.deleteSchedule = function () {
            $('#modelDialog').on('show.bs.modal', function (event) {
                var modal = $(this)
                modal.find('.modal-title').text('Delete schedule');
                modal.find('.model-content').text('Are you sure you want to delete this schedule?')
                $(this).off('show.bs.modal');
                $(this).find(".confirm")[0].action = 'delete';
                $(this).find(".cancel")[0].cancelValue = null;
            });
            // $('#modelDialog').modal("show");
        }
        $rootScope.cancel = function () {
            if (event.target.cancelValue) {
                skipChecking = true;
                $state.go('scheduler.schedulerList.getSchedule.scheduler', $state.params);
            }
        }
        $rootScope.accept = function (event) {

            switch (event.target.action) {
                case 'update':
                    if (Object.keys($scope.schedule.timepointsmap).length < 7) {
                        var emptyWeeks = [' Monday', ' Tuesday', ' Wednesday', ' Thursday', ' Friday', ' Saturday', ' Sunday'];
                        $.each($scope.schedule.timepointsmap, function (key, value) {
                            delete emptyWeeks[key - 1];
                        })
                        emptyWeeks = emptyWeeks.filter(function (element) {
                            return element != null;
                        })
                        var weeks;
                        if (emptyWeeks.length > 2) {
                            var day = emptyWeeks.toString();
                            var sub = day.substr(0, day.lastIndexOf(","));
                            weeks = sub + " and " + emptyWeeks[emptyWeeks.length - 1];
                        } else {
                            weeks = emptyWeeks.toString().replace(",", " and");
                        }
                        $('#modelDialog').on('show.bs.modal', function (event) {
                            var modal = $(this)
                            modal.find('.modal-title').text('There is no schedule for ' + weeks);
                            modal.find('.model-content').text('Are you sure you want to continue')
                            $(this).off('show.bs.modal');
                            $(this).find(".confirm")[0].action = 'updateNotify';
                            $(this).find(".cancel")[0].cancelValue = null;
                        });
                        $('#modelDialog').modal("show");
                        return;
                    }
                    continueUpdate();
                    break;
                case 'updateNotify':
                    continueUpdate();
                    break;
                case 'delete':
                    ApiFactory.getApiData({
                        serviceName: "deleteSchedule",
                        data: {
                            scheduleId: $state.params.scheduleId
                        },
                        onSuccess: function (data) {
                            if (data.status.toLowerCase() === "success") {
                                $("#modelDialog").modal("hide");
                                skipChecking = true;
                                toastr.success(messageFactory.getMessage(data.code));
                                $state.go("scheduler.schedulerList");
                            } else {
                                toastr.error(messageFactory.getMessage(data.code));
                            }
                        },
                        onFailure: function (data) {
                            $("#modelDialog").modal("hide");
                        }
                    });
                    break;
            }

            function continueUpdate() {
                $('#modelDialog').modal("hide");
                ApiFactory.getApiData({
                    serviceName: "updateschedule",
                    data: $scope.schedule,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            toastr.success(messageFactory.getMessage(data.code));
                            skipChecking = true;
                            history.back();
                            //  $state.go("scheduler.schedulerList");
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                })
            }
        }
    }
    $scope.$watch("schedule.timePoints", function(){
	 for (var j = 0; j < $scope.errorMsg.length; j++) {
	            $scope.errorMsg[j] = false;
	        }
	var times = [];
     $.each($scope.schedule.timePoints, function (key, value) {
         times.push(value.time);
     })

     for (var i = 0; i < times.length; i++) {
         for (var j = i + 1; j < times.length; j++) {
             if (times[j]) {
                 var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                 if (Math.abs(diff) < 30) {
                     $scope.errorMsg[j] = true;
                     
                 }
             }
         }
     }
}, true);
}).controller("copyScheduleController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {
    
    $scope.daysOfWeek = [{
        "label": "Monday",
        "value": 1
	}, {
        "label": "Tuesday",
        "value": 2
	}, {
        "label": "Wednesday",
        "value": 3
	}, {
        "label": "Thursday",
        "value": 4
	}, {
        "label": "Friday",
        "value": 5
	}, {
        "label": "Saturday",
        "value": 6
	}, {
        "label": "Sunday",
        "value": 7
	}];
    $scope.selectedCustomers = [];
    $scope.multiSelectCustomerSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        enableSearch: true,
        showCheckAll: false,
        showUncheckAll: false,
        closeOnSelect: true
    };
    $scope.customersDropdownEvents = {
        onItemSelect: customerChangeselect,
        onItemDeselect: customerChangeDeselect,
        onSelectAll: customersSelectAll,
        onDeselectAll: customersUnSelectAll
    };
    $scope.selectedGroups = [];
    $scope.multiSelectGroupsSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 3,
        scrollable: true,
        showCheckAll: true,
        showUncheckAll: true,
        enableSearch: true,
        dynamicTitle: true,
        displayProp: "groupName",
        idProp: "groupId"
    };
    $scope.groupsDropdownEvents = {
        onItemSelect: groupsChangeSelect,
        onItemDeselect: groupsChangeDeselect,
        onSelectAll: groupsSelectAll,
        onDeselectAll: groupsUnSelectAll
    };
    $scope.selectedSites = [];
    $scope.multiSelectSitesSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 3,
        scrollable: true,
        showCheckAll: true,
        enableSearch: true,
        showUncheckAll: true,
        dynamicTitle: true,
        displayProp: "siteName",
        idProp: "siteId"
    };
    $scope.sitesDropdownEvents = {
        onItemSelect: sitesChangeselect,
        onItemDeselect: sitesChangeDeselect
    };
    var cstrSelectAll = false;

    function customerChangeselect(callback) {
        if (callback && typeof (callback) == "object") {
            $scope.selectedCustomers = [callback];
        }
        if (cstrSelectAll) {
            return;
        }
        var selectedCustomers = [];
        $.each($scope.selectedCustomers, function (key, value) {
            selectedCustomers.push(value.id);
        })
        usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {
            $scope.groupsDataArry = data.groups;

            var selectedGups = [];
            $.each($scope.selectedGroups, function (key, value) {
                $.each($scope.groupsDataArry, function (key1, value1) {
                    if (value1.groupId == value.id) {
                        var gup = {};
                        gup.id = value.id;
                        selectedGups.push(gup);
                    }
                })
            })
            $scope.selectedGroups = selectedGups;

            if ($scope.selectedGroups.length == 0) {
          
                $scope.sitesDataArry = data.sites;
                var selectedSts = [];
                $.each($scope.selectedSites, function (key, value) {
                    $.each($scope.sitesDataArry, function (key1, value1) {
                        if (value1.siteId == value.id) {
                            var sit = {};
                            sit.id = value.id;
                            selectedSts.push(sit);
                        }
                    })
                })
                $scope.selectedSites = selectedSts;
            }



            if (callback && typeof (callback) == "function") {
                callback(true);
            }
        })
    }

    function customerChangeDeselect() {
        var selectedCustomers = [];
        $.each($scope.selectedCustomers, function (key, value) {
            selectedCustomers.push(value.id);
        })
        if ($scope.selectedCustomers.length == 0) {
            $scope.groupsDataArry = [];
            $scope.sitesDataArry = [];
            $scope.selectedGroups = [];
            $scope.selectedSites = [];
            return;
        }
        usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {
            $scope.groupsDataArry = data.groups;
            var selectedGups = [];
            $.each($scope.selectedGroups, function (key, value) {
                $.each($scope.groupsDataArry, function (key1, value1) {
                    if (value1.groupId == value.id) {
                        var gup = {};
                        gup.id = value.id;
                        selectedGups.push(gup);
                    }
                })
            })
            $scope.selectedGroups = selectedGups;
            if ($scope.selectedGroups.length == 0) {
                $scope.sitesDataArry = data.sites;
                var selectedSts = [];
                $.each($scope.selectedSites, function (key, value) {
                    $.each($scope.sitesDataArry, function (key1, value1) {
                        if (value1.siteId == value.id) {
                            var sit = {};
                            sit.id = value.id;
                            selectedSts.push(sit);
                        }
                    })
                })
                $scope.selectedSites = selectedSts;
            }

        })
    }

    function customersSelectAll() {
        cstrSelectAll = true;
        setTimeout(function () {
            cstrSelectAll = false;
            customerChangeselect();
        }, 500)
    }

    function customersUnSelectAll() {
        $scope.groupsDataArry = [];
        $scope.sitesDataArry = [];
        $scope.selectedGroups = [];
        $scope.selectedSites = [];
        $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");
    }
    var grpSelectAll = false;

    function groupsChangeSelect() {
        if (grpSelectAll) {
            return;
        }
        usersFactory.getSites($scope.selectedGroups, function (data) {
            var sitesData = [];
            $.each(data, function (key, value) {
                var site = {};
                site.siteId = value.id;
                site.siteName = value.name;
                sitesData.push(site);

            });
            $scope.sitesDataArry = sitesData;
            var selectedSts = [];
            $.each($scope.selectedSites, function (key, value) {

                $.each($scope.sitesDataArry, function (key1, value1) {
                    if (value1.siteId == value.id) {
                        var sit = {};
                        sit.id = value.id.toString();
                        selectedSts.push(sit);
                    }
                })
            });

            $scope.selectedSites = selectedSts;


        });
    }

    function groupsChangeDeselect() {
        if ($scope.selectedGroups.length == 0) {
            $scope.sitesDataArry = [];
            $scope.selectedSites = [];
            customerChangeselect();
            return;
        }
        usersFactory.getSites($scope.selectedGroups, function (data) {
            var sitesData = [];
            $.each(data, function (key, value) {
                var site = {};
                site.siteId = value.id;
                site.siteName = value.name;
                sitesData.push(site);

            });
            $scope.sitesDataArry = sitesData;
            var selectedSts = [];
            $.each($scope.selectedSites, function (key, value) {

                $.each($scope.sitesDataArry, function (key1, value1) {
                    if (value1.siteId == value.id) {
                        var sit = {};
                        sit.id = value.id.toString();
                        selectedSts.push(sit);
                    }
                })
            });

            $scope.selectedSites = selectedSts;
        });
    }

    function groupsSelectAll() {
        grpSelectAll = true;
        setTimeout(function () {
            grpSelectAll = false;
            groupsChangeSelect();
        }, 500)
    }

    function groupsUnSelectAll() {
        $scope.sitesDataArry = [];
        $scope.selectedSites = [];
        customerChangeselect();
        $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");
    }

    function sitesChangeselect() {}

    function sitesChangeDeselect() {}

    function loadCustomersDropdown(callback) {
        if ($state.params.sourcePage && !$rootScope.schedule.parentCustId) {
            commonFactories.getCustomers(function (data) {
                if (data && data.length > 0) {
                    $scope.customersDataArry = [];
                    $scope.selectedCustomers = [];
                    $scope.groupsDataArry = [];
                    $scope.selectedGroups = [];
                    $scope.sitesDataArry = [];
                    $scope.selectedSites = [];
                    $scope.customersDataArry = data;
                    if ($rootScope.schedule.customerIds) {
                        $scope.selectedCustomers = [{
                            id: Number($rootScope.schedule.customerIds)
                    }];
                        customerChangeselect(checkGroups);
                    }
                }
            });
            return;
        }
        if ($rootScope.schedule.parentCustId) {
            commonFactories.getCustomers(function (data) {
                if (data && data.length > 0) {
                    $scope.customersDataArry = [];
                    $scope.selectedCustomers = [];
                    $scope.groupsDataArry = [];
                    $scope.selectedGroups = [];
                    $scope.sitesDataArry = [];
                    $scope.selectedSites = [];
                    $scope.customersDataArry = data;
                    $scope.selectedCustomers = [{
                        id: Number($rootScope.schedule.parentCustId)
                    }];
                    customerChangeselect(checkGroups);
                    callback(false);
                }
            });
        } else {
            callback(true);
        }
    }

    function checkGroups() {
        if ($rootScope.schedule.groupIds) {
            var groupIds = $rootScope.schedule.groupIds;
            $.each(groupIds.split(","), function (key, value) {
                $scope.selectedGroups.push({
                    id: Number(value)
                });
            })
            groupsChangeSelect(checkSites);
        } else {
            checkSites();
        }
    }

    function checkSites() {
        if ($rootScope.schedule.siteIds) {
            var siteIds = $rootScope.schedule.siteIds;
            $.each(siteIds.split(","), function (key, value) {
                $scope.selectedSites.push({
                    id: Number(value)
                });
            })
        }
    }

    function loadSelectedModules() {
        if ($rootScope.schedule.customerIds) {
            var customerId = $rootScope.schedule.customerIds;
            var customer = $rootScope.schedule.customers;
            $scope.customersDataArry = [{
                id: Number(customerId),
                label: customer
            }];
            $scope.selectedCustomers = [{
                id: Number(customerId)
            }];
            customerChangeselect();
        }
        if ($rootScope.schedule.groupIds) {
            var groupId = $rootScope.schedule.groupIds;
            var group = $rootScope.schedule.groups;
            $scope.groupsDataArry = [{
                groupId: groupId,
                groupName: group
            }];
            $scope.selectedGroups = [{
                id: groupId
            }];
            groupsChangeSelect();
        }
        if ($rootScope.schedule.siteIds) {
            var siteId = $rootScope.schedule.siteIds;
            var site = $rootScope.schedule.sites;
            $scope.sitesDataArry = [{
                siteId: siteId,
                siteName: site
            }];
            $scope.selectedSites = [{
                id: siteId
            }];
        }
    }
    $scope.noofrows = ['1', '2', '3', '4'];
    $scope.errorMsg = [];
    $.each($scope.noofrows, function (key) {
        $scope.errorMsg.push(false);
    });
    $scope.schedule = {};
    $scope.schedule.timePoints = [{}];
    var finalMap = new Object();
    $scope.schedule.show = false;
    $scope.checkAllDays = function () {
        if ($scope.checkAll) {
            $scope.schedule.daysOfWeek = {
                "1": 1,
                "2": 2,
                "3": 3,
                "4": 4,
                "5": 5,
                "6": 6,
                "7": 7
            };
            compareAll();
            return;
        }
        $scope.schedule.daysOfWeek = null;
        $scope.checkAll = false;
        $.each($scope.schedule.timePoints, function (key) {
            $scope.schedule.timePoints[key].time = '';
            $scope.schedule.timePoints[key].htpoint = '';
            $scope.schedule.timePoints[key].clpoint = '';
            $scope.schedule.timePoints[key].am = '1';
        });
    }

    function compareAll() {
        if (!finalMap) {
            return;
        } else {
            $scope.schedule.timepointsmap = angular.copy(finalMap);
        }
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {

            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        var days = [];
        $.each(daysArr, function (key, value) {
            var k = angular.copy($scope.schedule.timepointsmap[value]);
            var k1 = angular.copy($scope.schedule.timepointsmap[value]);
            if (k) {
                for (var i = 0; i < k.length; i++) {
                    delete k1[i].dow_id
                }
                days.push(JSON.stringify(k1));
            }
        })
        var compare = true;
        var daysArray = Object.keys(days);
        for (var i = 0; i < daysArray.length; i++) {
            if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                compare = false;
            }
        }
        if (Object.keys(days).length != daysArr.length) {
            compare = false;
        }
        if (compare) {
            var data = [];
            $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                var temp = angular.copy(value);
                var time = new Date();
                var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                time.setHours(hrs);
                time.setMinutes(mns);
                temp.time = time;
                data.push(temp);
            })
            var data = data.sort(function (a, b) {
                // Turn your strings into dates, and then subtract them
                // to get a value that is either negative, positive, or zero.
                return new Date(b.time) - new Date(a.time);
            }).reverse();
            $scope.schedule.timePoints = angular.copy(data);
            return;
        }
    }
    if (angular.isUndefined($rootScope.scheduleId)) {
        $rootScope.scheduleId = $state.params.scheduleId;
    }
    ApiFactory.getApiData({
        serviceName: "getSchedule",
        data: {
            scheduleId: $rootScope.scheduleId
        },
        onSuccess: function (data) {
            $rootScope.schedule = data.data;
            getSchedule();

            loadCustomersDropdown(function (proceed) {

                if (proceed) {
                    loadSelectedModules();
                }

            });
        },
        onFailure: function () {}
    })
    $scope.populateTimePoints = function (index, $event) {
        var daysArr = [];
        $.each($scope.schedule.daysOfWeek, function (key, value) {
            if (value && value != -1) {
                daysArr.push(key);
            }
        });
        if (daysArr.length == 1) {
            if ($scope.schedule.timepointsmap[daysArr[0]]) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            }
        } else {

            if ($($event.target).prop("checked")) {
                if (!$scope.schedule.timepointsmap[index]) {
                    $.each($scope.schedule.timePoints, function (key) {
                        $scope.schedule.timePoints[key].time = '';
                        $scope.schedule.timePoints[key].htpoint = '';
                        $scope.schedule.timePoints[key].clpoint = '';
                        $scope.schedule.timePoints[key].am = '1';
                    });
                    return;
                }
            }
            var days = [];
            $.each(daysArr, function (key, value) {
                var k = angular.copy($scope.schedule.timepointsmap[value]);
                var k1 = angular.copy($scope.schedule.timepointsmap[value]);
                if (k) {
                    for (var i = 0; i < k.length; i++) {
                        delete k1[i].dow_id
                    }
                    days.push(JSON.stringify(k1));
                }
            })
            var compare = true;
            var daysArray = Object.keys(days);
            for (var i = 0; i < daysArray.length; i++) {
                if (days[daysArray[i + 1]] && (days[daysArray[i]] != days[daysArray[i + 1]])) {
                    compare = false;
                }
            }
            if (Object.keys(days).length != daysArr.length) {
                compare = false;

            }
            if (compare) {
                var data = [];
                $.each($scope.schedule.timepointsmap[daysArr[0]], function (key, value) {
                    var temp = angular.copy(value);
                    var time = new Date();
                    var hrs = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("HH");
                    var mns = moment(temp.time + " " + (value.am == 1 ? "AM" : "PM"), ["h:mm A"]).format("mm");
                    time.setHours(hrs);
                    time.setMinutes(mns);
                    temp.time = time;
                    data.push(temp);
                })
                var data = data.sort(function (a, b) {
                    // Turn your strings into dates, and then subtract them
                    // to get a value that is either negative, positive, or zero.
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
            } else {
                $.each($scope.schedule.timePoints, function (key) {
                    $scope.schedule.timePoints[key].time = '';
                    $scope.schedule.timePoints[key].htpoint = '';
                    $scope.schedule.timePoints[key].clpoint = '';
                    $scope.schedule.timePoints[key].am = '1';
                });
            }
        }
    };

    function storeDependencies() {

        $scope.schedule.customersList = [];
        $scope.selectedCustomers = angular.copy(usersFactory.getUniqueArray($scope.selectedCustomers, "id"))
        $.each($scope.selectedCustomers, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.customersList.push(value.id);
            }
        });
        $scope.schedule.groupsList = [];
        $scope.selectedGroups = angular.copy(usersFactory.getUniqueArray($scope.selectedGroups, "id"))
        $.each($scope.selectedGroups, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.groupsList.push(value.id);
            }
        });
        $scope.schedule.locationsList = [];
        $scope.selectedSites = angular.copy(usersFactory.getUniqueArray($scope.selectedSites, "id"));
        $.each($scope.selectedSites, function (key, value) {
            if (value.id != "All") {
                $scope.schedule.locationsList.push(value.id);
            }
        });
    }

    function getSchedule() {
        $scope.schedule = $.extend($scope.schedule, $rootScope.schedule);
        finalMap = $rootScope.schedule.timepointsmap;
        $scope.events = angular.copy(finalMap);
        $scope.editShedule = function (form) {
            for (var j = 0; j < $scope.errorMsg.length; j++) {
                $scope.errorMsg[j] = false;
            }
            storeDependencies();
            var times = [];
            $.each($scope.schedule.timePoints, function (key, value) {

                times.push(value.time);

            })
            var sameTimepoints = false;
            for (var i = 0; i < times.length; i++) {
                for (var j = i + 1; j < times.length; j++) {
                    if (times[j]) {
                        var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                        if (Math.abs(diff) < 30) {
                            sameTimepoints = true;
                            $scope.errorMsg[j] = true;
                        }
                    }
                }
            }
            if (sameTimepoints) {
                //toastr.error("Please maintain 30 minutes time gap between each time period");
                return false;
            }
            if (form.$valid && $scope.schedule.daysOfWeek) {
                form.$submitted = false;
                $scope.schedule.dayandheatingpnts = [{}];
                var map = new Object();
                var data = [];
                $.each($scope.schedule.timePoints, function (key, value) {
                    var temp = angular.copy(value);
                    temp.time = moment(value.time).format("hh:mm A").split(" ")[0];
                    temp.am = moment(value.time).format("hh:mm A").split(" ")[1].toLowerCase() == "am" ? 1 : 2;
                    data.push(temp);
                });
                var sameTimepoints = false;
                for (var i = 0; i < data.length; i++) {
                    for (var j = i + 1; j < data.length; j++) {
                        if (data[j]) {
                            if (data[i].time === data[j].time &&
                                data[i].am === data[j].am) {
                                sameTimepoints = true;
                            }
                        }
                    }
                }
                if (sameTimepoints) {
                    toastr.error("Please enter data for different time points");
                    return false;
                }
                $.each($scope.schedule.daysOfWeek, function (key, value) {
                    if (value && value != -1) {
                        map[key] = angular.copy(data);
                    }
                });
                finalMap = $.extend(finalMap, map);
                $scope.eventsUpdate = true;
                $scope.events = angular.copy(finalMap);
                emptyFields(form);
            }
        };

        function emptyFields(form) {
            form.$submitted = false;
            $timeout(function () {
                $.each($scope.schedule.timePoints, function (key) {
                    $scope.schedule.timePoints[key].time = '';
                    $scope.schedule.timePoints[key].htpoint = '';
                    $scope.schedule.timePoints[key].clpoint = '';
                    $scope.schedule.timePoints[key].am = '1';
                });
                $scope.schedule.daysOfWeek = null;
                $scope.checkAll = false;
            }, 500)
        }
        $scope.cancelSchedule = function (form) {
            form.$submitted = false;
            $.each($scope.schedule.timePoints, function (key) {
                $scope.schedule.timePoints[key].time = null;
                $scope.schedule.timePoints[key].htpoint = null;
                $scope.schedule.timePoints[key].clpoint = null;
                $scope.schedule.timePoints[key].am = '1';
            });

            $scope.schedule.daysOfWeek = null;
            $scope.checkAll = false;
            finalMap = null;
        }
        $scope.updateSchedule = function (form) {
            form.$submitted = false;
            if (finalMap == null) {
                toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_ALL_DATA_8002"));
                return;
            }
            $scope.schedule.timepointsmap = finalMap;
            if (Object.keys($scope.schedule.timepointsmap).length < 7) {
                toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_ALL_DATA_8002"));
                return;
            }
            $scope.schedule.oldName = $rootScope.schedule.scheduleName;
            if (Object.keys($scope.schedule.timepointsmap).length < 7) {
                $('#modelDialog').on('show.bs.modal', function (event) {
                    var modal = $(this)
                    modal.find('.modal-title').text('Delete schedule');
                    modal.find('.model-content').text('Are you sure you want to delete this schedule?')
                    $(this).off('show.bs.modal');
                });
                $('#modelDialog').modal("show");
                var emptyWeeks = [' Monday', ' Tuesday', ' Wednesday', ' Thursday', ' Friday', ' Saturday', ' Sunday'];
                $.each($scope.schedule.timepointsmap, function (key, value) {
                    delete emptyWeeks[key - 1];
                })
                emptyWeeks = emptyWeeks.filter(function (element) {
                    return element != null;
                })
                var weeks;
                if (emptyWeeks.length > 2) {
                    var day = emptyWeeks.toString();
                    var sub = day.substr(0, day.lastIndexOf(","));
                    weeks = sub + " and " + emptyWeeks[emptyWeeks.length - 1];
                } else {
                    weeks = emptyWeeks.toString().replace(",", " and");
                }
                $('#modelDialog').on('show.bs.modal', function (event) {
                    var modal = $(this)
                    modal.find('.modal-title').text('There is no schedule for ' + weeks);
                    modal.find('.model-content').text('Are you sure you want to continue')
                    $(this).off('show.bs.modal');
                });
                $('#modelDialog').modal("show");
                return;
            }

            storeDependencies();

            var data = angular.copy($scope.schedule);
            data.customerId = (data.customersList === undefined) ? '' : angular.copy(data.customersList).toString();
            data.groupId = (data.groupsList === undefined) ? '' : angular.copy(data.groupsList).toString();
            data.siteId = (data.locationsList === undefined) ? '' : angular.copy(data.locationsList).toString();
            delete data.customersList;
            delete data.groupsList;
            delete data.locationsList;
            ApiFactory.getApiData({
                serviceName: "addschedule",
                data: data,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        skipChecking = true;
                        history.back();
                        // $state.go("scheduler.schedulerList");
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            })
        };
    }
    $rootScope.accept = function () {
        $('#modelDialog').modal("hide");
        var data = angular.copy($scope.schedule);
        data.customerId = angular.copy(data.customersList).toString();
        data.groupId = angular.copy(data.groupsList).toString();
        data.siteId = angular.copy(data.locationsList).toString();
        delete data.customersList;
        delete data.groupsList;
        delete data.locationsList;

        ApiFactory.getApiData({
            serviceName: "addschedule",
            data: data,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    toastr.success(messageFactory.getMessage(data.code));
                    skipChecking = true;
                    history.back();
                    //$state.go("scheduler.schedulerList");
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        })
    }
    
    $scope.$watch("schedule.timePoints", function(){
	 for (var j = 0; j < $scope.errorMsg.length; j++) {
	            $scope.errorMsg[j] = false;
	        }
    	var times = [];
        $.each($scope.schedule.timePoints, function (key, value) {
            times.push(value.time);
        })

        for (var i = 0; i < times.length; i++) {
            for (var j = i + 1; j < times.length; j++) {
                if (times[j]) {
                    var diff = moment(times[i], "DD/MM/YYYY HH:mm:ss").diff(moment(times[j], "DD/MM/YYYY HH:mm:ss"), 'minutes');
                    if (Math.abs(diff) < 30) {
                        $scope.errorMsg[j] = true;
                        
                    }
                }
            }
        }
    }, true);
    
})