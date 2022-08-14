app.controller("configurationController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state) {
    $scope.filtered = {};

    ApiFactory.getApiData({
        serviceName: "customersList",
        data: $state.params,
        onSuccess: function (data) {
            $scope.customerList = data.data;
            $scope.customer = data.data[0].customerId;

            if ($state.params.customerId) {
                $scope.customer = $state.params.customerId;
            }

            $scope.loadAlerts();
        },
        onFailure: function () {}
    });


    $scope.loadAlerts = function () {
        $state.params.customerId = $scope.customer;

        ApiFactory.getApiData({
            serviceName: "customeralertconfig",
            data: {
                customerId: $state.params.customerId ? $state.params.customerId : $scope.customer
            },
            onSuccess: function (data) {
                $scope.alerts = data.data;
                $state.go(".", {
                    customerId: $scope.customer
                }, {
                    notify: false
                });

            },
            onFailure: function () {}
        });
    }
    $scope.editConfiguration = function (data) {
        $state.go("alerts.configuration.edit", {
            customerId: $scope.customer,
            alertId: data.alertId
        });
    }
}).controller("editConfigurationController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {
    $scope.alerts = {};
    $scope.alerts.userAction = [];
    ApiFactory.getApiData({
        serviceName: "getConfig",
        data: $state.params,
        onSuccess: function (data) {
            $scope.alerts = $.extend($scope.alerts, data.data.resultAry[0]);
            $scope.actionList = data.data.actionList;
            $scope.priorityOptions = data.data.priorityList;
            
            // keeping the old data
            $scope.oldAlerts = angular.copy($.extend($scope.alerts, data.data.resultAry[0]));
            $scope.oldactionList = angular.copy($scope.actionList);
            $scope.oldpriorityOptions = angular.copy($scope.priorityOptions);
            if ($scope.alerts.alertId != 1) {
            	$scope.alerts.parameterId = Number($scope.alerts.parameterId);
            }
            if ($scope.alerts.alertId == 2 || $scope.alerts.alertId == 3) {
            	$scope.alerts.parameterIdMin = $scope.alerts.parameterId
            } else if ($scope.alerts.alertId == 6 || $scope.alerts.alertId == 7) {
            	$scope.alerts.parameterIdTemp = $scope.alerts.parameterId
            } else if ($scope.alerts.alertId == 5) {
            	$scope.alerts.parameterIdHour = $scope.alerts.parameterId; 
            }
            $timeout(function () {
                $($element).find(".user-actions").trigger('input');
            }, 500)
        },
        onFailure: function () {}
    });
    $scope.addRow = function () {
        $scope.actionList.push({
            "userAction": ""
        });
    }
    $scope.removeRow = function (index) {
        $scope.actionList.splice(index, 1);
    }

    $scope.restoreConfiguration = function () {
        $('#modelDialog').on('show.bs.modal', function () {
            var modal = $(this);
            modal.find('.modal-title').text('Resetting configurations to default');
            modal.find('.model-content').text('Are you sure to reset alerts to default configuration?');
            return;
        });
        $("#modelDialog").modal("show");


        $rootScope.accept = function () {
            ApiFactory.getApiData({
                serviceName: "deletecustomeralertconfig",
                data: $scope.alerts,
                onSuccess: function (data) {
                    if (data.status.toUpperCase() == "SUCCESS") {
                        toastr.success(messageFactory.getMessage(data.code));
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }

                    $state.go(".", $state.params, {
                        reload: true
                    });
                },
                onFailure: function () {}
            });
        };

    }

    $scope.saveConfig = function (form) {
        var checkValues = $('input[name=actionItems]').map(function () {
            return $(this).val().replace(/,/g, '$');
        }).get();

        if (!form.$valid) {
            return false;
        }

        $scope.alerts.userAction = checkValues;
        if ($scope.alerts.alertId == 2 || $scope.alerts.alertId == 3) {
        	$scope.alerts.parameterId = $scope.alerts.parameterIdMin + ""; 
        } else if ($scope.alerts.alertId == 6 || $scope.alerts.alertId == 7) {
        	$scope.alerts.parameterId = $scope.alerts.parameterIdTemp + ""; 
        } else if ($scope.alerts.alertId == 5) {
        	$scope.alerts.parameterId = $scope.alerts.parameterIdHour + ""; 
        }
        ApiFactory.getApiData({
            serviceName: "saveConfig",
            data: $scope.alerts,
            onSuccess: function (data) {
                if (data.status.toUpperCase() == "SUCCESS") {
                    toastr.success(messageFactory.getMessage(data.code));
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
                $state.go("alerts.configuration", {
                    customerId: $state.params.customerId
                });
            },
            onFailure: function () {}
        });
    }
    $scope.editConfiguration = function (data) {
        $state.go("alerts.configuration", {
            customerId: $scope.customer,
            alertId: data.alertId
        });
    }
}).controller("customerAlertsController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, $timeout, $element, commonFactories) {

    $scope.alertsInfo = [];
    $scope.recordsDisplayCount = 0;
    $rootScope.totalAlertCount = 0;
    $scope.alertsFormData = [];
    $scope.checkAll = false;
    $scope.alertStatus = $state.params.dashboardAlertStatus ? $state.params.dashboardAlertStatus : 'open';
    $scope.alertsFormData.alertStatus =$state.params.dashboardAlertStatus ? $state.params.dashboardAlertStatus : 'open';
    $scope.alertListStatus = $state.params.dashboardAlertStatus ? $state.params.dashboardAlertStatus : 'open';
    $scope.filterBy = 'Customers';
    $scope.LevelTwoFilter = 'AlarmType';

    $state.go('.', {
        dashboardAlertStatus: $scope.alertStatus
    }, {
        notify: false
    });

    $scope.gotoDeviceProfile = function (deviceId, customerId) {

        $rootScope.deviceProfileData = null;
        $rootScope.deviceProfileData1 = null;
        $rootScope.deviceProfileParentData = null;

        $state.go("devices.devicesList.profile.settings", $rootScope.mergeObject($state.params, {
            deviceId: deviceId,
            customerId: customerId
        }));

    }

    if ($state.params.fromSource !== undefined && $state.params.fromSource === 'dashboard') {
        if ($state.params.priority === '1') {
            $scope.LevelTwoFilter = "Priority";
        } else if ($state.params.priority === 'resolve') {
            $scope.LevelTwoFilter = "AlarmType";
            $scope.alertListStatus = 'resolve';
        } else if ($state.params.priority === 'new') {
            $scope.LevelTwoFilter = "AlarmType";
            $scope.alertListStatus = 'new';
        }
        callDashboardAlerts($state.params.dashboardAlertStatus,
            $state.params.dashboardFilter,
            $state.params.dashboardAlertId,
            $state.params.dashboardSpecifcId,
            $state.params.dashboardTimeInDays,
            $state.params.priority);
    } else {
        callAlertsInfo($scope.alertStatus);
    }

    $scope.checkAtleatOne = false;


    $scope.uncheckCheckAll = function () {
        $scope.checkAll = false;
        $('input[name=subChk]').prop("checked", false);
    }

    $scope.checkAllValues = function () {
        if ($scope.checkAll === true) {
            $('input[name=subChk]').prop("checked", true);
        } else {
            $scope.checkAll = false;
            $scope.alertsFormData = [];
            $('input[name=subChk]').prop("checked", false);
        }
    }
    $scope.updateAlertStatus = function (changeStatus) {
        if ($state.params.fromSource !== undefined && $state.params.fromSource === 'dashboard' && $state.params.priority) {
            $state.go('.', {
                fromSource: $state.params.fromSource,
                dashboardAlertStatus: $state.params.dashboardAlertStatus,
                dashboardFilter: $state.params.dashboardFilter,
                dashboardSpecifcId: $state.params.dashboardSpecifcId,
                dashboardAlertId: $state.params.dashboardAlertId,
                dashboardTimeInDays: $state.params.dashboardTimeInDays,
                priority: ""
            }, {
                notify: false
            });
        }
        var alertCheckedIds = $('input[name=subChk]:checked').map(function () {
            return $(this).val();
        }).get();

        if (alertCheckedIds.length === 0) {
            $scope.checkAtleatOne = true;
        } else {

            var currentAlertStatus = 'open';

            if ($scope.LevelTwoFilter == 'AlarmType') {
                currentAlertStatus = $scope.alertListStatus;
            } else {
                currentAlertStatus = 'open';
            }

            $scope.checkAtleatOne = false;
            ApiFactory.getApiData({
                serviceName: "updateAlertStatus",
                data: {
                    alertIds: alertCheckedIds,
                    alertStatus: changeStatus
                },
                onSuccess: function () {
                	 $scope.updatAlertStatus = '';
                	 $state.go($state.$current.self.name , {}, {
                         reload: true
                     });
                	 loadAlertCount();
                     return;
                  //  callAlertsInfo(currentAlertStatus);
                    //$scope.updatAlertStatus = '';
                    //loadAlertCount();
                },
                onFailure: function () {}
            });
        }
    }

    function callAlertsInfo(alertStatus) {

        ApiFactory.getApiData({
            serviceName: "getCustomerAlerts",
            data: commonFactories.fromPageAlerts($state.current.name, $state.params, alertStatus),
            onSuccess: function (data) {
                $scope.checkAtleatOne = false;
                $("#nodata").css({
                    opacity: 1
                });
                $scope.alertsFormData = [];
                $scope.recordsDisplayCount = data.data.alertsInfo.length;
                if (data.data.alertsInfo.length > 0) {
                    $scope.customerList = {};
                    $scope.groupList = {};
                    $scope.siteList = {};
                    $scope.deviceList = {};
                    $scope.priorityList = {};
                    $.each(data.data.alertsInfo, function (key, value) {
                        $scope.customerList[value.customerId.toString().replace(/-/g, "").trim()] = value.customerName.trim();
                        if (value.groupName.indexOf(",") != -1) {
                            var groupNameAry = value.groupName.split(',');
                            var groupNameId = value.groupId.split(',');
                            $.each(groupNameAry, function (groupIndex, groupValue) {
                                $scope.groupList[groupNameId[groupIndex].replace(/-/g, "").trim()] = groupValue.trim();
                            });
                        } else {
                            if (value.groupName) {
                                $scope.groupList[value.groupId.toString().replace(/-/g, "").trim()] = value.groupName.trim();
                            }
                        }
                        if (value.siteName) {
                            $scope.siteList[value.siteId.toString().replace(/-/g, "").trim()] = value.siteName.trim();
                        }

                        $scope.deviceList[value.deviceId.toString().replace(/-/g, "").trim()] = value.deviceName.trim();
                        $scope.priorityList[value.alertProrityId.toString().replace(/-/g, "").trim()] = value.alertProrityName.trim();

                    });
                    /*if($scope.customerList[$scope.customerData]!==undefined && $scope.customerData.indexOf('~~') == -1){
                    	$scope.customerData=$scope.customerList[$scope.customerData]+'~~'+$scope.customerData;
                    }*/
                }
                /*else{
                	        		
                	        		if($scope.customerList[$scope.customerData]!==undefined && $scope.customerData.indexOf('~~') == -1){
                                    	$scope.customerData=$scope.customerList[$scope.customerData]+'~~'+$scope.customerData;
                                    }
                	        	}*/
                if ($scope.customerList[$scope.customerData] !== undefined && $scope.customerData.indexOf('~~') == -1) {
                    $scope.customerData = $scope.customerList[$scope.customerData] + '~~' + $scope.customerData;
                }
                if ($scope.groupList[$scope.group] !== undefined && $scope.group.indexOf('~~') == -1) {
                    $scope.group = $scope.groupList[$scope.group] + '~~' + $scope.group;
                }
                if ($scope.siteList[$scope.site] !== undefined && $scope.site.indexOf('~~') == -1) {
                    $scope.site = $scope.siteList[$scope.site] + '~~' + $scope.site;
                }
                if ($scope.deviceList[$scope.device] !== undefined && $scope.device.indexOf('~~') == -1) {
                    $scope.device = $scope.deviceList[$scope.device] + '~~' + $scope.device;
                }
                $scope.alertsInfo = data.data.alertsInfo;
                if (alertStatus.toLowerCase() === 'open') {
                    $rootScope.totalAlertCount = data.data.alertsInfo.length;
                }
                $scope.checkAll = false;
                $('input[name=subChk]').prop("checked", false);

                // Sorting maps to arrange in alphabetical order
                $scope.customerList = sortHashMap($scope.customerList);
                $scope.groupList = sortHashMap($scope.groupList);
                $scope.siteList = sortHashMap($scope.siteList);
                $scope.deviceList = sortHashMap($scope.deviceList);
                $scope.priorityList = sortHashMap($scope.priorityList);
            },
            onFailure: function () {}
        });
    }

    // Sort map with alphabets as values
    function sortHashMap(valMap) {
        tempArry = [];
        tempMap = {};
        $.each(valMap, function (index, value) {
            tempArry.push({
                key: index,
                val: value
            });
        });

        tempArry.sort(function (val1, val2) {
            return (val1.val.toUpperCase() < val2.val.toUpperCase()) ? -1 : (val1.val.toUpperCase() > val2.val.toUpperCase()) ? 1 : 0;
        });

        $.each(tempArry, function (a, b) {

            if ((tempArry[a].key).indexOf("~~") > 0) {
                tempMap[tempArry[a].key] = valMap[tempArry[a].key];
            } else {
                tempMap[valMap[tempArry[a].key] + "~~" + tempArry[a].key] = valMap[tempArry[a].key];
            }

        })
        return valMap = tempMap;
    }

    function callDashboardAlerts(alertStatus, fromCurrentPage, alertId, specificId, timeInDays, priority) {
        $scope.customerList = {};
        $scope.groupList = {};
        $scope.siteList = {};
        $scope.deviceList = {};
        $scope.priorityList = {};
        ApiFactory.getApiData({
            serviceName: "getdashboardalerts",
            data: {
                alertStatus: alertStatus,
                fromCurrentPage: fromCurrentPage,
                alertId: alertId,
                specificId: specificId,
                timePeriodInDays: timeInDays
            },
            onSuccess: function (data) {
                $scope.checkAtleatOne = false;
                $("#nodata").css({
                    opacity: 1
                });
                $scope.alertsFormData = [];
                $scope.recordsDisplayCount = data.data.alertsInfo.length;
                if (data.data.alertsInfo.length > 0) {
                    $.each(data.data.alertsInfo, function (key, value) {
                        $scope.customerList[value.customerId] = value.customerName;
                        if (value.groupName)
                            $scope.groupList[value.groupId] = value.groupName;
                        if (value.siteName)
                            $scope.siteList[value.siteId] = value.siteName;
                        $scope.deviceList[value.deviceId] = value.deviceName;
                        $scope.priorityList[value.alertProrityId] = value.alertProrityName;
                    });
                }
                $scope.alertsInfo = data.data.alertsInfo;
                $rootScope.totalAlertCount = data.data.alertsInfo.length;
                $scope.checkAll = false;
                $('input[name=subChk]').prop("checked", false);
                if (priority === '1') {
                    $scope.priority = priority;
                }
                if (fromCurrentPage === "customers" || fromCurrentPage === "sites") {
                    //$scope.customerData=specificId;
                    $("#alertCustomer option[value='all']").hide();
                    $("#alertPriority option[value='all']").hide();

                    $.each($scope.customerList, function (key, value) {
                        $scope.customerData = key;
                    })
                    $.each($scope.priorityList, function (key, value) {
                        $scope.priority = key;
                    })
                }
            },
            onFailure: function () {}
        });
    }

    $scope.fetchAlertData = function () {
    	$state.go('.', {
            dashboardAlertStatus: $scope.alertListStatus,
        }, {
            notify: false
        });
        if ($state.params.fromSource !== undefined && $state.params.fromSource === 'dashboard' && $state.params.priority) {
            $state.go('.', {
                fromSource: $state.params.fromSource,
                dashboardAlertStatus: $state.params.dashboardAlertStatus,
                dashboardFilter: $state.params.dashboardFilter,
                dashboardSpecifcId: $state.params.dashboardSpecifcId,
                dashboardAlertId: $state.params.dashboardAlertId,
                dashboardTimeInDays: $state.params.dashboardTimeInDays,
                priority: ""
            }, {
                notify: false
            });
        }
        callAlertsInfo($scope.alertListStatus);
        $("#alertCustomer option[value='all']").show();
        $("#alertPriority option[value='all']").show();
        $scope.priority = 'all';
        $scope.checkAll = false;
    }

    function loadAlertCount() {
        ApiFactory.getApiData({
            serviceName: "getnewalertcount",
            onSuccess: function (data) {
                $rootScope.newAlertCount = data.data;
                $state.reload();
            },
            onFailure: function () {}
        });
    }
    $scope.splitValues = function (value, delim) {
        return value.split(delim);
    }
    $scope.checkAllFlag = function () {
        var checkValues = $('input[name=subChk]:checked').map(function () {
            return $(this).val();
        }).get();
        if ($scope.alertsInfo.length === checkValues.length) {
            $scope.checkAll = true;
        } else {
            $scope.checkAll = false;
        }
    }
    $scope.alertsFilter = function (obj) {

        switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
            case "customers":
                if ($scope.customerData !== 'all') {
                    if ($scope.customerData.indexOf("~~") < 0) {
                        return obj.customerId == $scope.customerData
                    } else {
                        return obj.customerId == $scope.customerData.substring($scope.customerData.indexOf("~~") + 2, $scope.customerData.length)
                    }
                } else {
                    return true;
                }
            case "groups":
                if ($scope.group !== 'all') {
                    if ($scope.group.indexOf("~~") < 0) {
                        return obj.groupId == $scope.group
                    } else {
                        obj.groupId = obj.groupId.replace(/-/g, "");
                        return obj.groupId == $scope.group.substring($scope.group.indexOf("~~") + 2, $scope.group.length)
                    }
                } else {
                    return true;
                }
            case "sites":
                if ($scope.site !== 'all') {
                    if ($scope.site.indexOf("~~") < 0) {
                        return obj.siteId == $scope.site
                    } else {
                        return obj.siteId == $scope.site.substring($scope.site.indexOf("~~") + 2, $scope.site.length)
                    }
                } else {
                    return true;
                }
            case "devices":
                if ($scope.device !== 'all') {
                    if ($scope.device.indexOf("~~") < 0) {
                        return obj.deviceId == $scope.device
                    } else {
                        return obj.deviceId == $scope.device.substring($scope.device.indexOf("~~") + 2, $scope.device.length)
                    }
                } else {
                    return true;
                }
            case "priority":
                if ($scope.priority !== 'all') {
                    if ($scope.priority.indexOf("~~") < 0) {
                        return obj.alertProrityId == $scope.priority
                    } else {
                        return obj.alertProrityId == $scope.priority.substring($scope.priority.indexOf("~~") + 2, $scope.priority.length)
                    }
                } else {
                    return true;
                }
        }
        $scope.filterByChange = function () {
            if ($state.params.fromSource !== undefined && $state.params.fromSource === 'dashboard' && $state.params.priority) {
                $state.go('.', {
                    fromSource: $state.params.fromSource,
                    dashboardAlertStatus: $state.params.dashboardAlertStatus,
                    dashboardFilter: $state.params.dashboardFilter,
                    dashboardSpecifcId: $state.params.dashboardSpecifcId,
                    dashboardAlertId: $state.params.dashboardAlertId,
                    dashboardTimeInDays: $state.params.dashboardTimeInDays,
                    priority: ""
                }, {
                    notify: false
                });
            }
            switch ($scope.filterBy.toLowerCase()) {
                case "alarmtype":
                    callAlertsInfo($scope.alertListStatus);
                    break;
                case "groups":
                    callAlertsInfo('all');
                    break;
                case "sites":
                    callAlertsInfo('all');
                    break;
                case "devices":
                    callAlertsInfo('all');
                    break;
                case "customers":
                    callAlertsInfo('all');
                    break;
                case "priority":
                    callAlertsInfo('all');
                    break;
                default:
                    callAlertsInfo($scope.alertListStatus);
                    break;
            }
        };
        return true;
    };
    $scope.alertsFilterLevelTwo = function (obj) {

        switch ($scope.LevelTwoFilter && $scope.LevelTwoFilter.toLowerCase()) {
            case "priority":

                if ($scope.priority !== 'all') {
                    if ($scope.priority.indexOf("~~") < 0) {
                        return obj.alertProrityId == $scope.priority
                    } else {
                        return obj.alertProrityId == $scope.priority.substring($scope.priority.indexOf("~~") + 2, $scope.priority.length)
                    }
                } else {
                    return true;
                }
        }

        return true;
    };
}).controller("actionItemsController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, $timeout, $element, toastr) {

    $scope.siteList = {};
    $scope.deviceList = {};
    callActionItems('open')

    function callActionItems(status) {
        ApiFactory.getApiData({
            serviceName: "getAlertsByActionItems",
            data: {
                alertStatus: status
            },
            onSuccess: function (data) {
                $scope.alertsActionItems = data.data;
                $("#nodata").css({
                    opacity: 1
                });
                if (data.data.length > 0) {
                    $.each(data.data, function (key, value) {
                        $scope.siteList[value.siteId] = value.siteName;
                        $scope.deviceList[value.deviceId] = value.deviceName;
                    });
                }
            },
            onFailure: function () {}
        });
    }
    $scope.fetchAlertData = function () {
        callActionItems($scope.alertListStatus);
    }
    $scope.saveActionItem = function (object, status) {
        $scope.actionsData = angular.copy(object);
        $scope.actionsData.actionStatus = status;
        ApiFactory.getApiData({
            serviceName: "updateUserActionItems",
            data: $scope.actionsData,
            onSuccess: function (data) {
                $scope.alertsActionItems = data.data;
                if (data.status.toLowerCase() === "success") {
                    toastr.success(messageFactory.getMessage(data.code));
                    $state.go("alerts.actionitems");
                    callActionItems('open');
                    return;
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        });
    }
    $scope.selectMainItem = function (object, index) {
        for (var j in $scope.filteredActionItems[index].actionItems) {
            $scope.filteredActionItems[index].actionItems[j].itemStatus = $scope.filteredActionItems[index].mainChk === 0 ? 0 : 1;
        }
    }

    $scope.selectSubItem = function (object, mainIndex, index, data) {
        $scope.alertsActionItems[mainIndex].actionItems[index].itemStatus = data === 1 ? 1 : 0;
    }
    $scope.alertsFilter = function (obj) {
        switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
            case "sites":
                if ($scope.site !== 'all') {
                    return obj.siteId == $scope.site;
                } else {
                    return true;
                }
            case "devices":
                if ($scope.device !== 'all') {
                    return obj.deviceId == $scope.device;
                } else {
                    return true;
                }
        }
        $scope.filterByChange = function () {
            switch ($scope.filterBy.toLowerCase()) {
                case "alarmtype":
                    callActionItems($scope.alertListStatus);
                    break;
                case "sites":
                    callActionItems('all');
                    break;
                case "groups":
                    callActionItems('all');
                    break;
                default:
                    callActionItems('open');
                    break;
            }
        };
        return true;
    };
}).controller("alertsController", function () {

});