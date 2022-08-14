/** 
 *@file : reportController 
 * 
 *@dashboardController :Load dashboard module 
 * 
 *@author :(Nagarjuna Eerla - neerla@ctepl.com) 
 * 
 *@Contact :(Umang - ugupta@ctepl.com) 
 * 
 *@Contact : (Chenna - yreddy@ctepl.com) 
 *
 *@version     VEM2-1.0
 *@date        30-11-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       30-11-2016  Nagarjuna Eerla           File Created
 */
app.controller("reportController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {
   
	if ($rootScope.userDetails.rolePermissions['Dashboard Management'] == 4) {
	     $state.go("report.devices");
	     return;
	}
	
	$scope.report = {};
    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];
    $scope.commonChartData = [];
    $scope.parameterSelected = [];
    $scope.parameterOptions = [];
    $scope.errors = {};
    $scope.errors.devicesLength = false;
    $scope.errors.devicesLengthMessage = [];
    $scope.timeOptions = [{
        name: "Select Time",
        id: 0
    }, {
        name: "Last 24 Hrs",
        id: 1
    }, {
        name: "Last 7 Days",
        id: 7
    }, {
        name: "Last 28 Days",
        id: 28
    }];
    $scope.reportOptions = [{
        name: "Degraded HVAC Units",
        id: 1
    }, {
        name: "Within Setpoints",
        id: 2
    }, {
        name: "HVAC Usage",
        id: 3
    }, {
        name: "Manual Changes",
        id: 4
    }, {
        name: "Communication Failure",
        id: 5
    }, {
        name: "Temperature vs Setpoint",
        id: 6
    }];
    $scope.paramterscustomTexts = {
        buttonDefaultText: 'Select Parameters'
    };
    $scope.parameterSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "label",
        idProp: "value"
    };
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.report[$(this).attr("name")] = "";
        }
    });
    $scope.open1 = function () {
        $scope.popup1.opened = true;
       

        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });

    };
    $scope.open2 = function () {
        $scope.popup2.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };

    // To sort groups
    $scope.groupNameSorter = function (a){
	   return parseInt(a.groupName.replace( /^\D+/g, '')); // gets number from a string
	}
    
    $scope.select = function () {
        $scope.report.time = 0;
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.popup2 = {
        opened: false
    };
    ApiFactory.getApiData({
        serviceName: "getAnalyticParams",
        onSuccess: function (data) {
            if (data.status.toLowerCase() === "success") {
                $scope.parameterOptions = data.data[0].analyticParams;
            }
        },
        onFailure: function () {}
    });
    $scope.isExists = function (flag, data, id) {
        var idData;
        for (var i = 0; i < data.length; i++) {
            if (flag === 'customer' || flag === 'time' || flag === 'reportType') {
                idData = Number(data[i].id);
            } else if (flag === 'group') {
                idData = Number(data[i].groupId);
            } else if (flag === 'site') {
                idData = Number(data[i].siteId);
            } else if (flag === 'device') {
                idData = Number(data[i].deviceId);
            } else if (flag === 'params') {
                idData = Number(data[i].value);
            }
            if (idData === Number(id)) {
                return data[i]
            }
        }
    }
    if ($scope.reportOptions.length > 0) {
        $scope.report.reportType = $state.params.reportType > 0 ? Number($state.params.reportType) : Number($scope.reportOptions[0].id);
    }
    commonFactories.getCustomers(function (data) {
        $scope.customers = [];
        if (data && data.length > 0) {
            $scope.customers = data;
            if ($scope.customers.length > 0) {
                $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
                $scope.$parent.companyLogo = $state.params.customer > 0 ? $scope.isExists('customer', $scope.customers, Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
                $scope.report.site = $state.params.site > 0 ? Number($state.params.site) : 0;
                $scope.report.group = $state.params.group > 0 ? Number($state.params.group) : 0;
                $state.go('.', {
                	reportType: $scope.report.reportType,
                    customer: $scope.report.customer ? $scope.report.customer : 0,
                    group: $scope.report.group ? $scope.report.group : 0,
                    site: $scope.report.site ? $scope.report.site : 0
                }, {
                    notify: false
                });
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    $scope.devices = groupSiteData.devices;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    if (Number($state.params.group) > 0) {
                    	$scope.sites = [];
                        $scope.devices = [];
                        ApiFactory.getApiData({
                            serviceName: "getsitesbygroupsinreports",
                            data: {
                            	customerId: $scope.report.customer,
                            	groupIds: $state.params.group.toString() ? $state.params.group.toString() : ""
                            },
                            onSuccess: function (data) {
                                if (data.status.toLowerCase() === "success") {
                                    if (data.data.siteList.length > 0) {
                                        $scope.sites = data.data.siteList;
                                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                                    }
                                    if (data.data.deviceList.length > 0) {
                                        $scope.devices = data.data.deviceList;
                                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                                    }
                                    
                                    if (Number($state.params.site) > 0) {
                                        $scope.devices = [];
                                        ApiFactory.getApiData({
                                            serviceName: "getThingListSort",
                                            data: {
                                                id: $state.params.site,
                                                sortId: "siteId"
                                            },
                                            onSuccess: function (data) {
                                                if (data.status.toLowerCase() === "success") {
                                                    if (data.data.things.length > 0) {
                                                        $scope.devices = data.data.things;
                                                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                                                    }
                                                    reportLoad();
                                                }
                                            },
                                            onFailure: function () {}
                                        });
                                    } else {
                                        reportLoad();
                                    }
                                }
                            },
                            onFailure: function () {}
                        });
                    } else if (Number($state.params.group) <= 0 && Number($state.params.site) > 0) {
                    	 if (Number($state.params.site) > 0) {
                             $scope.devices = [];
                             ApiFactory.getApiData({
                                 serviceName: "getThingListSort",
                                 data: {
                                     id: $state.params.site,
                                     sortId: "siteId"
                                 },
                                 onSuccess: function (data) {
                                     if (data.status.toLowerCase() === "success") {
                                         if (data.data.things.length > 0) {
                                             $scope.devices = data.data.things;
                                             $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                                         }
                                         reportLoad();
                                     }
                                 },
                                 onFailure: function () {}
                             });
                         } else {
                             reportLoad();
                         }
                    } else {
                        reportLoad();
                    }
                    var noneGroup = {
            			"groupName": "Ungroup",
            			"groupId": -1
            		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                });
            }
           
        }
        
        if (Number($state.params.reportType) === 6) {
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        }
        if ($scope.timeOptions.length > 0) {
            $scope.report.time = $state.params.time > 0 ? Number($state.params.time) : Number($scope.timeOptions[0].id);
        }
        $scope.getDates();
    });
    function reportLoad() {
        labelLoad();
        if ($scope.customers.length > 0) {
            $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
        }
        if ($scope.reportOptions.length > 0) {
            $scope.report.reportType = $state.params.reportType > 0 ? Number($state.params.reportType) : Number($scope.reportOptions[0].id);
        }
        if ($scope.groups.length > 0) {
            $scope.report.group = Number($state.params.group);
        }
        if ($scope.sites.length > 0) {
            $scope.report.site = Number($state.params.site);
        }
        if ($scope.devices.length > 0) {
            $scope.report.device = Number($state.params.device);
        }
        $scope.report.nameType = "customer"
        if (!$scope.report.group && !$scope.report.site && $scope.report.reportType !== 6) {
            $scope.report.nameType = "customer";
        } else if ($scope.report.group && !$scope.report.site && $scope.report.reportType !== 6) {
            $scope.report.nameType = "group";
        } else if ((!$scope.report.group && $scope.report.site && $scope.report.reportType !== 6) ||
            ($scope.report.site && !$scope.report.group && $scope.report.reportType !== 6) ||
            ($scope.report.site && $scope.report.group && $scope.report.reportType !== 6)) {
            $scope.report.nameType = "site";
        } else if ($scope.report.reportType === 6) {
            $scope.report.nameType = "device";
        }
        var analyticParams = [];
        var farenHeatParams = [];
        var onOffParams = [];

        if ($scope.report.reportType === 6) {
            $.each($scope.parameterSelected, function (key, value) {
                var unit = $scope.isExists('params', $scope.parameterOptions, value.id).labelUnit;
                if (unit === 'F') {
                    farenHeatParams.push(value.id);
                } else if (unit === 'B') {
                    onOffParams.push(value.id);
                } else {
                    analyticParams.push(value.id);
                }
            });
        }
        var apiData = {
            type: $scope.report.nameType.toUpperCase(),
            reportType: $scope.report.reportType,
            customerIds: $scope.report.customer ? $scope.report.customer : 0,
            groupIds: $scope.report.group ? $scope.report.group : 0,
            siteIds: $scope.report.site ? $scope.report.site : 0,
            deviceIds: $scope.report.device ? $scope.report.device : 0,
            inDays: $scope.report.time,
            fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
            toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
            params: analyticParams.join(","),
            farenHeatParams: farenHeatParams.join(","),
            onOffParams: onOffParams.join(",")
        };
        loadReport(apiData);
    }

    function labelLoad() {
        $scope.labelDisplay = {};
        if ($scope.customers.length > 0 && $scope.report.customer > 0) {
            $scope.labelDisplay.customer = $scope.report.customer ? $scope.isExists('customer', $scope.customers, $scope.report.customer).label : '';
        }
        if ($scope.reportOptions.length > 0 && $scope.report.reportType > 0) {
            $scope.labelDisplay.reportType = $scope.isExists('reportType', $scope.reportOptions, $scope.report.reportType).name;
        }
        if ($scope.timeOptions.length > 0 && $scope.report.time > 0) {
            $scope.getDates();
            $scope.labelDisplay.time = $scope.isExists('time', $scope.timeOptions, $scope.report.time).name;
        }
        if ($scope.groups.length > 0 && $scope.report.group > 0) {
            $scope.labelDisplay.group = $scope.report.group ? ($scope.isExists('group', $scope.groups, $scope.report.group) ? $scope.isExists('group', $scope.groups, $scope.report.group).groupName : '') : '';
        } else {
        	if ($scope.report.reportType === 6 && $scope.report.device > 0) {
        		$scope.labelDisplay.group = $scope.report.device ? ($scope.isExists('device', $scope.devices, $scope.report.device) ? $scope.isExists('device', $scope.devices, $scope.report.device).groupName : '') : '';
        	}
        }
        if ($scope.sites.length > 0 && $scope.report.site > 0) {
            $scope.labelDisplay.site = $scope.report.site ? ($scope.isExists('site', $scope.sites, $scope.report.site) ? $scope.isExists('site', $scope.sites, $scope.report.site).siteName : '') : '';
            var siteInternalId = $scope.isExists('site', $scope.sites, $scope.report.site).siteInternalId ? "#" + $scope.isExists('site', $scope.sites, $scope.report.site).siteInternalId : '';
            $scope.labelDisplay.site = $scope.report.site ? $scope.isExists('site', $scope.sites, $scope.report.site).siteName + " " + siteInternalId : '';
        } else {
        	if ($scope.report.device > 0) {
        		var siteInternalId1 = $scope.isExists('device', $scope.devices, $scope.report.device).siteInternalId ? "#" + $scope.isExists('device', $scope.devices, $scope.report.device).siteInternalId : '';
                $scope.labelDisplay.site = $scope.isExists('device', $scope.devices, $scope.report.device).siteName + " " + siteInternalId1;
        	}
        }
        if ($scope.devices.length > 0 && $scope.report.device > 0) {
            $scope.labelDisplay.device = $scope.report.device ? ($scope.isExists('device', $scope.devices, $scope.report.device) ? $scope.isExists('device', $scope.devices, $scope.report.device).name : '') : '';
        }
        $scope.labelDisplay.from = $scope.report.fromDate ? moment($scope.report.fromDate).format("MMM DD YYYY") : '';
        $scope.labelDisplay.to = $scope.report.toDate ? moment($scope.report.toDate).format("MMM DD YYYY") : '';

        $scope.report.mainTitle = "<b>" + $scope.labelDisplay.customer;
        if ($scope.labelDisplay.group) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.group;
        }
        if ($scope.labelDisplay.site) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.site;
        }
        $scope.report.mainTitle += "</b>";
        $scope.report.subTitle = "";
        if ($scope.labelDisplay.device !== '' && $scope.labelDisplay.device) {
            $scope.report.subTitle += $scope.labelDisplay.device + "<br>";
        }
        if ($scope.labelDisplay.reportType !== '' && $scope.labelDisplay.reportType) {
            $scope.report.subTitle += $scope.labelDisplay.reportType + "<br>";
        }
        if ($scope.labelDisplay.time !== '' && $scope.labelDisplay.time) {
            $scope.report.subTitle += $scope.labelDisplay.time + "<br>";
        }
        if ($scope.labelDisplay.from !== '' && $scope.labelDisplay.from) {
            $scope.report.subTitle += $scope.labelDisplay.from;
        }
        if ($scope.labelDisplay.to !== '' && $scope.labelDisplay.to) {
            $scope.report.subTitle += " - " + $scope.labelDisplay.to;
        }
    }
    $scope.deviceRedirect = function () {
    	 $state.go("devices.devicesList.profile.settings", {
             deviceId: $scope.report.device,
             customerId: $scope.report.customer,
             sourcePage: "devices-" + $scope.report.device
         });
    	 return;
    }
    $scope.getReportData = function (form) {
        if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }

        if (form.$valid) {
            var flag = 0;
            if ($scope.parameterSelected.length <= 0 && $scope.report.reportType === 6) {
                flag++;
            }
            if (!$scope.report.device && $scope.report.reportType === 6) {
                flag++;
            }
            if (flag > 0) {
                return;
            }
            reportLoad();
        }
    }
    $scope.getDates = function () {
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.time > 0) {
            var d = new Date();
            $scope.report.toDate = new Date()
            d.setDate(d.getDate() - Number($scope.report.time));
            $scope.report.fromDate = d;
        } else {
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
        }
    }

    function loadReport(apiData) {
        $scope.commonChartData = [];
        if ($scope.report.reportType === 3) {
            $scope.serviceName = "getHVACUsageReport";
        } else if ($scope.report.reportType === 6) {
            $scope.serviceName = "getTempSetpointReport";
        } else {
            $scope.serviceName = "getReportData";
        }
        $scope.reportTypeStatus = $scope.report.reportType;
        if ($scope.serviceName) {
            ApiFactory.getApiData({
                serviceName: $scope.serviceName,
                data: apiData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.commonChartData = data.data;
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        if ($scope.serviceName === "getTempSetpointReport") {
                            if ($scope.commonChartData.graphsValidation.isFarenHeatParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Temperature section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.commonChartData.graphsValidation.isOnOffParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("On/Off section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                        }
                    } else {
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                }
            });
        }
    }
    $scope.ctrlFn = function (id, name) {
        if (name === "customer") {
            $scope.report.group = Number(id);
            $state.go('.', {
                reportType: $scope.report.reportType,
                customer: $scope.report.customer ? $scope.report.customer : 0,
                group: Number(id) ? Number(id) : 0,
                site: $scope.report.site ? $scope.report.site : 0,
                device: $scope.report.device ? $scope.report.device : 0,
                time: $scope.report.time,
                oldType: $scope.report.reportType
            }, {
                notify: false
            });
            loadSites($scope.report.group);
        } else if (name === "group") {
            $scope.report.site = Number(id);
            $state.go('.', {
                reportType: $scope.report.reportType,
                customer: $scope.report.customer ? $scope.report.customer : 0,
                group: $scope.report.group ? $scope.report.group : 0,
                site: Number(id) ? Number(id) : 0,
                device: $scope.report.device ? $scope.report.device : 0,
                time: $scope.report.time,
                oldType: $scope.report.reportType
            }, {
                notify: false
            });
            loadDevicesBySites($scope.report.site);
        } else if (name === "site") {
            $scope.parameterSelected = [];
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
            $scope.report.device = Number(id);
            $scope.report.reportType = 6;
            $state.go('.', {
                reportType: $scope.report.reportType,
                customer: $scope.report.customer ? $scope.report.customer : 0,
                group: $scope.report.group ? $scope.report.group : 0,
                site: $scope.report.site ? $scope.report.site : 0,
                device: Number(id) ? Number(id) : 0,
                time: $scope.report.time,
                oldType: $scope.report.oldType ? $scope.report.oldType : $state.params.oldType
            }, {
                notify: false
            });
        } else if (name === "device") {
            $scope.report.device = Number(id);
            $state.go('.', {
                reportType: $scope.report.reportType,
                customer: $scope.report.customer ? $scope.report.customer : 0,
                group: $scope.report.group ? $scope.report.group : 0,
                site: $scope.report.site ? $scope.report.site : 0,
                device: Number(id) ? Number(id) : 0,
                time: $scope.report.time
            }, {
                notify: false
            });
        }
        if (name === "site" && $scope.report.site && $scope.report.device && $scope.devices.length <= 0) {
            loaddevices($scope.report.site);
        } else {
            finalCall();
        }
    }

    function finalCall() {
        $timeout(function () {
            var elOccupy = "";
            if (name === "customer") {
                elOccupy = document.getElementById('group');
            } else if (name === "group") {
                elOccupy = document.getElementById('site');
            } else if (name === "site") {
                elOccupy = document.getElementById('device');
            }
            angular.element(elOccupy).triggerHandler('change');
            reportLoad();
        }, 100);
    }

    function loaddevices(siteId) {
        $scope.devices = [];
        ApiFactory.getApiData({
            serviceName: "getThingListSort",
            data: {
                id: siteId,
                sortId: "siteId"
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.things.length > 0) {
                        $scope.devices = data.data.things;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                    finalCall();
                }
            },
            onFailure: function () {}
        });
    }

    $scope.backChart = function (name) {
        if (name === "group") {
            $scope.report.oldType = $scope.report.reportType;
            $scope.report.group = "";
        } else if (name === "site") {
            $scope.report.oldType = $scope.report.reportType;
            $scope.report.site = "";
            loadSites($scope.report.group);
        } else if (name === "device") {
            $scope.report.device = "";
        }
        if ($scope.report.reportType === 6) {
            $scope.report.reportType = $scope.report.oldType ? $scope.report.oldType : $state.params.oldType;
        }
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time,
            oldType: $scope.report.oldType
        }, {
            notify: false
        });
        $timeout(function () {
            var elOccupy = "";
            if (name === "customer") {
                elOccupy = document.getElementById('group');
            } else if (name === "group") {
                elOccupy = document.getElementById('site');
            } else if (name === "site") {
                elOccupy = document.getElementById('device');
            }
            angular.element(elOccupy).triggerHandler('change');
            reportLoad();
        }, 100);
    };
    $scope.changeReportType = function () {
        if ($scope.report.reportType === 6) {
            $scope.report.nameType = 'device';
            if ($scope.report.reportType === 6) {
                $.each($scope.parameterOptions, function (key, value) {
                    $scope.parameterSelected.push({
                        id: value.value
                    })
                });
            }
        } else {
            $scope.report.oldType = $scope.report.reportType;
            $scope.report.nameType = '';
            $scope.report.device = '';
            $scope.parameterSelected = [];
        }
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time,
            oldType: $scope.report.oldType
        }, {
            notify: false
        });
    };
    $scope.getGroups = function () {
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.customer) {
            $scope.$parent.companyLogo = $scope.isExists('customer', $scope.customers, $scope.report.customer).companyLogo;
            loadGroupsSites($scope.report.customer);
        } else {
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
        }
    };
    $scope.getSites = function () {
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.group) {
            loadSites($scope.report.group);
        } else {
            $scope.report.site = "";
            $scope.report.device = "";
            $state.go('.', {
                reportType: $scope.report.reportType,
                customer: $scope.report.customer ? $scope.report.customer : 0,
                group: $scope.report.group ? $scope.report.group : 0,
                site: $scope.report.site ? $scope.report.site : 0,
                device: $scope.report.device ? $scope.report.device : 0,
                time: $scope.report.time
            }, {
                notify: false
            });
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
            usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                $scope.groups = groupSiteData.groups;
                var noneGroup = {
            			"groupName": "Ungroup",
            			"groupId": -1
            		}
            	$scope.groups.push(noneGroup);
                $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                $scope.sites = groupSiteData.sites;
                $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                $scope.devices = groupSiteData.devices;
                $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
            });
        }
    };
    $scope.getDevices = function () {
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.site) {
            loadDevicesBySites($scope.report.site);
        } else {
            if ($scope.report.group) {
                loadSites($scope.report.group);
            } else {
                $scope.report.device = "";
                $scope.groups = [];
                $scope.sites = [];
                $scope.devices = [];
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    $scope.devices = groupSiteData.devices;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                });
            }
        }
    };
    $scope.changeDevices = function () {
        $state.go('.', {
            reportType: $scope.report.reportType,
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
    };

    function loadGroupsSites(customers) {
        $scope.groups = [];
        $scope.sites = [];
        $scope.devices = [];
        ApiFactory.getApiData({
            serviceName: "getcustomergroups",
            data: {
                customerIds: customers.toString() ? customers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.groupList.length > 0) {
                        $scope.groups = data.data.groupList;
                    }
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                    
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                }
            },
            onFailure: function () {}
        });
    }

    function loadSites(groupsByCustomers) {
        $scope.sites = [];
        $scope.devices = [];
        ApiFactory.getApiData({
            serviceName: "getsitesbygroupsinreports",
            data: {
            	customerId: $scope.report.customer,
            	groupIds: groupsByCustomers.toString() ? groupsByCustomers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                }
            },
            onFailure: function () {}
        });
    }

    function loadDevicesBySites(siteId) {
        $scope.devices = [];
        ApiFactory.getApiData({
            serviceName: "getThingListSort",
            data: {
                id: siteId,
                sortId: "siteId"
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.things.length > 0) {
                        $scope.devices = data.data.things;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                }
            },
            onFailure: function () {}
        });
    }
}).controller("customerReportsController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {
    $scope.report = {};
    $scope.customers = [];
    $scope.parameterSelected = [];
    $scope.parameterOptions = [{}];
    $scope.dataType = [];
    $scope.siteSurveyDatatype = [];
    $scope.customerDataDatatype = [];
    $scope.chartOptions = [];
    $scope.customerChartdata = [];
    $scope.analyticsMessage = {
        buttonDefaultText: 'Select Analytics Data'
    };
    $scope.parameterSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "label",
        idProp: "value"
    };
    $scope.isExists = function (flag, data, id) {
        var idData;
        for (var i = 0; i < data.length; i++) {
            if (flag === 'customer' || flag === 'report') {
                idData = Number(data[i].id);
            }
            if (idData === Number(id)) {
                return data[i]
            }
        }
    }
    commonFactories.getCustomers(function (data) {
        $scope.customers = [];
        if (data && data.length > 0) {
            $scope.customers = data;
            if ($scope.customers.length > 0) {
                $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
                $scope.$parent.companyLogo = $state.params.customer > 0 ? $scope.isExists('customer', $scope.customers, Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
                $state.go('.', {
                    customer: $scope.report.customer ? $scope.report.customer : 0
                }, {
                    notify: false
                });
                analyticsLoad();
            }
        }
    });

    function analyticsLoad() {
        $scope.dataType = [];
        $scope.parameterOptions = [];
        $scope.siteSurveyDatatype = [];
        $scope.customerDataDatatype = [];
        ApiFactory.getApiData({
            serviceName: "getAnalyticParams",
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    $scope.dataType.push({
                        id: data.data[1].id,
                        name: data.data[1].label
                    });
                    $scope.dataType.push({
                        id: data.data[2].id,
                        name: data.data[2].label
                    });
                    $scope.siteSurveyDatatype = data.data[1].analyticParams;
                    $scope.customerDataDatatype = data.data[2].analyticParams;
                }
            },
            onFailure: function () {}
        });
    }

    function labelLoad() {
        $scope.labelDisplay = {};
        if ($scope.customers.length > 0 && $scope.report.customer > 0) {
            $scope.labelDisplay.customer = $scope.report.customer ? $scope.isExists('customer', $scope.customers, $scope.report.customer).label : '';
        }
        if ($scope.dataType.length > 0 && $scope.report.datatype > 0) {
            $scope.labelDisplay.reportType = $scope.report.datatype ? $scope.isExists('report', $scope.dataType, $scope.report.datatype).name : '';
        }
        $scope.report.mainTitle = "<b>" + $scope.labelDisplay.customer + "</b>";
        $scope.report.subTitle = $scope.labelDisplay.reportType;
    }
    $scope.getGroups = function () {
        if ($scope.report.customer) {
            $scope.$parent.companyLogo = $scope.isExists('customer', $scope.customers, $scope.report.customer).companyLogo;
        }
    };

    function loadChart() {
        labelLoad();
        var analyticParams = [];
        $.each($scope.parameterSelected, function (key, value) {
            analyticParams.push(value.id)
        });
        var apiData = {
            dataType: $scope.report.datatype ? $scope.report.datatype : 0,
            customerId: $scope.report.customer ? $scope.report.customer : 0,
            groupIds: $scope.report.group ? $scope.report.group : 0,
            siteIds: $scope.report.site ? $scope.report.site : 0,
            deviceIds: $scope.report.device ? $scope.report.device : 0,
            analyticParams: analyticParams.join(","),
            fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
            toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : ''
        };
        chartAPIcall(apiData);
    }

    function chartAPIcall(apiData) {
        $scope.customerChartdata = [];
        ApiFactory.getApiData({
            serviceName: "getCustomerAnalyticsData",
            data: apiData,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    $scope.customerChartdata = data.data;
                }
            },
            onFailure: function () {}
        });
    }
    $scope.getReportData = function (form) {
        if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }
        if (form.$valid) {
            if ($scope.parameterSelected.length <= 0) {
                return;
            }
            loadChart()
        }
    }
    $scope.getAnalyics = function () {
        if ($scope.report.datatype === 2) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.siteSurveyDatatype;
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else if ($scope.report.datatype === 3) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.customerDataDatatype;
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else {
            $scope.parameterOptions = [];
            $scope.parameterSelected = [];
        }
    }

}).controller("groupReportsController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {
    $scope.report = {};
    $scope.groupChartdata = [];
    $scope.customers = [];
    $scope.groups = [];
    $scope.groupsSelected = [];
    $scope.parameterSelected = [];
    $scope.parameterOptions = [{}];
    $scope.dataType = [];
    $scope.siteSurveyDatatype = [];
    $scope.groupDataDatatype = [];
    $scope.trendingDataDatatype = [];
    $scope.analyticsMessage = {
        buttonDefaultText: 'Select Analytics Data'
    };
    $scope.errors = {};
    $scope.errors.devicesLength = false;
    $scope.errors.devicesLengthMessage = [];
    $scope.parameterSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "label",
        idProp: "value"
    };

    $scope.groupsMessage = {
        buttonDefaultText: 'Select Groups'
    };
    $scope.groupsSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "groupName",
        idProp: "groupId"
    };
    $scope.timeOptions = [{
        name: "Select Time",
        id: 0
    }, {
        name: "Last 24 Hrs",
        id: 1
    }, {
        name: "Last 7 Days",
        id: 7
    }, {
        name: "Last 28 Days",
        id: 28
    }];
    $scope.report.time = $scope.timeOptions[0].id
    $scope.open1 = function () {
        $scope.popup1.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.open2 = function () {
        $scope.popup2.opened = true;

        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.popup2 = {
        opened: false
    };
    $scope.isExists = function (flag, data, id) {
        var idData;
        for (var i = 0; i < data.length; i++) {
            if (flag === 'customer' || flag === 'report') {
                idData = Number(data[i].id);
            } else if (flag === 'group') {
                idData = Number(data[i].groupId);
            } else if (flag === 'params') {
                idData = Number(data[i].value);
            }
            if (idData === Number(id)) {
                return data[i]
            }
        }
    }
    $scope.getDates = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.time > 0) {
            var d = new Date();
            $scope.report.toDate = new Date()
            d.setDate(d.getDate() - Number($scope.report.time));
            $scope.report.fromDate = d;
        } else {
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
        }
    }
    commonFactories.getCustomers(function (data) {
        $scope.customers = [];
        if (data && data.length > 0) {
            $scope.customers = data;
            if ($scope.customers.length > 0) {
                $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
                $scope.$parent.companyLogo = $state.params.customer > 0 ? $scope.isExists('customer', $scope.customers, Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
                $state.go('.', {
                    customer: $scope.report.customer ? $scope.report.customer : 0
                }, {
                    notify: false
                });
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    analyticsLoad();
                });
            }
        }
    });

    function analyticsLoad() {
        $scope.dataType = [];
        $scope.parameterOptions = [];
        $scope.siteSurveyDatatype = [];
        $scope.groupDataDatatype = [];
        $scope.trendingDataDatatype = [];
        ApiFactory.getApiData({
            serviceName: "getAnalyticParams",
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    $scope.dataType.push({
                        id: data.data[1].id,
                        name: data.data[1].label
                    });
                    $scope.dataType.push({
                        id: data.data[3].id,
                        name: data.data[3].label
                    });
                    /*$scope.dataType.push({
                        id: data.data[5].id,
                        name: data.data[5].label
                    });*/
                    $scope.siteSurveyDatatype = data.data[1].analyticParams;
                    $scope.groupDataDatatype = data.data[3].analyticParams;
                    // $scope.trendingDataDatatype = data.data[5].analyticParams;
                }
            },
            onFailure: function () {}
        });
    }

    function labelLoad() {
        $scope.labelDisplay = {};
        if ($scope.customers.length > 0 && $scope.report.customer > 0) {
            $scope.labelDisplay.customer = $scope.report.customer ? $scope.isExists('customer', $scope.customers, $scope.report.customer).label : '';
        }
        if ($scope.timeOptions.length > 0) {
            $scope.report.time = $state.params.time > 0 ? Number($state.params.time) : Number($scope.timeOptions[0].id);
        }
        $scope.labelDisplay.group = [];
        $.each($scope.groupsSelected, function (key, value) {
            $scope.labelDisplay.group.push($scope.isExists('group', $scope.groups, value.id).groupName);
        });
        $scope.labelDisplay.from = $scope.report.fromDate ? moment($scope.report.fromDate).format("MMM DD YYYY") : '';
        $scope.labelDisplay.to = $scope.report.toDate ? moment($scope.report.toDate).format("MMM DD YYYY") : '';
        $scope.report.mainTitle = "<b>" + $scope.labelDisplay.customer;
        if ($scope.labelDisplay.group.length > 0) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.group.join(", ");
        }
        $scope.report.mainTitle += "</b>";

        if ($scope.dataType.length > 0 && $scope.report.datatype > 0) {
            $scope.labelDisplay.reportType = $scope.report.datatype ? $scope.isExists('report', $scope.dataType, $scope.report.datatype).name : '';
        }
        $scope.report.subTitle = "";
        $scope.report.subTitle += $scope.labelDisplay.reportType + "<br>";
        if ($scope.labelDisplay.from !== '' && $scope.labelDisplay.from) {
            $scope.report.subTitle += $scope.labelDisplay.from;
        }
        if ($scope.labelDisplay.to !== '' && $scope.labelDisplay.to) {
            $scope.report.subTitle += " - " + $scope.labelDisplay.to;
        }
    }

    function loadChart() {
        labelLoad();
        var analyticParams = [];
        var farenHeatParams = [];
        var onOffParams = [];
        var modeParams = [];
        var daysParams = [];

        $.each($scope.parameterSelected, function (key, value) {
            if ($scope.report.datatype === 6) {
                var unit = $scope.isExists('params', $scope.trendingDataDatatype, value.id).labelUnit;
                if (unit === 'F') {
                    farenHeatParams.push(value.id);
                } else if (unit === 'M') {
                    modeParams.push(value.id);
                } else if (unit === 'D') {
                    daysParams.push(value.id);
                } else if (unit === 'B') {
                    onOffParams.push(value.id);
                }
            } else {
                analyticParams.push(value.id);
            }
        });
        var groupsIds = [];
        $.each($scope.groupsSelected, function (key, value) {
            groupsIds.push(value.id)
        });
        var apiData = {
            customerId: $scope.report.customer ? $scope.report.customer : 0,
            groupIds: groupsIds.join(","),
            siteIds: $scope.report.site ? $scope.report.site : 0,
            deviceIds: $scope.report.device ? $scope.report.device : 0,
            fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
            toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
            dataType: $scope.report.datatype ? $scope.report.datatype : 0,
            analyticParams: analyticParams.join(","),
            farenHeatParams: farenHeatParams.join(","),
            onOffParams: onOffParams.join(","),
            modeParams: modeParams.join(","),
            daysParams: daysParams.join(","),
            type: 'GROUP'
        };
        chartAPIcall(apiData);
    }

    function chartAPIcall(apiData) {
        $scope.groupChartdata = [];
        if ($scope.report.datatype === 6) {
            $scope.reportTypeStatus = 6;
            $scope.serviceName = "getTrendingAnalytics";
        } else {
            $scope.reportTypeStatus = 0;
            $scope.serviceName = "getGroupAnalyticsData";
        }
        if ($scope.serviceName) {
            ApiFactory.getApiData({
                serviceName: $scope.serviceName,
                data: apiData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.errors.devicesLength = false;
                        $scope.groupChartdata = data.data;
                        $scope.errors.devicesLengthMessage = [];
                        if ($scope.serviceName === "getTrendingAnalytics") {
                            if ($scope.groupChartdata.graphsValidation.isDaysParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Days section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.groupChartdata.graphsValidation.isFarenHeatParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Temperature section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.groupChartdata.graphsValidation.isModeParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Modes section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.groupChartdata.graphsValidation.isOnOffParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("On/Off section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                        }
                    } else {
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                }
            });
        }
    }
    $scope.select = function () {
        $scope.report.time = 0;
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
    };
    $scope.getReportData = function (form) {
        if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }
        if (form.$valid) {
            var flag = 0;
            if ($scope.parameterSelected.length <= 0) {
                flag++;
            }
            if ($scope.groupsSelected.length <= 0) {
                flag++;
            }
            if (!$scope.report.fromDate && $scope.report.datatype === 6) {
                flag++;
            }
            if (!$scope.report.toDate && $scope.report.datatype === 6) {
                flag++;
            }
            if (flag > 0) {
                return;
            }
            loadChart()
        }
    }
    $scope.getAnalyics = function () {
        if ($scope.report.datatype === 2) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.siteSurveyDatatype;
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
            $scope.select();
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else if ($scope.report.datatype === 4) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.groupDataDatatype;
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
            $scope.select();
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else if ($scope.report.datatype === 6) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.trendingDataDatatype;
        } else {
            $scope.parameterOptions = [];
            $scope.parameterSelected = [];
        }
    }

    $scope.getGroups = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.customer) {
            $scope.$parent.companyLogo = $scope.isExists('customer', $scope.customers, $scope.report.customer).companyLogo;
            loadGroupsSites($scope.report.customer);
        } else {
            $scope.groups = [];
            $scope.groupsSelected = [];
        }
    };

    function loadGroupsSites(customers) {
        $scope.groups = [];
        $scope.groupsSelected = [];
        ApiFactory.getApiData({
            serviceName: "getcustomergroups",
            data: {
                customerIds: customers.toString() ? customers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.groupList.length > 0) {
                        $scope.groups = data.data.groupList;
                    }
                }
            },
            onFailure: function () {}
        });
    };
}).controller("siteReportsController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {
    $scope.report = {};
    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.sitesSelected = [];
    $scope.parameterSelected = [];
    $scope.parameterOptions = [{}];
    $scope.dataType = [];
    $scope.siteSurveyDatatype = [];
    $scope.siteDataDatatype = [];
    $scope.trendingDataDatatype = [];
    $scope.siteChartdata = [];
    $scope.analyticsMessage = {
        buttonDefaultText: 'Select Analytics Data'
    };
    $scope.parameterSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "label",
        idProp: "value"
    };
    $scope.errors = {};
    $scope.errors.devicesLength = false;
    $scope.errors.devicesLengthMessage = [];
    $scope.sitesMessage = {
        buttonDefaultText: 'Select Sites'
    };
    $scope.sitesSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "siteName",
        idProp: "siteId"
    };

    $scope.open1 = function () {
        $scope.popup1.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.open2 = function () {
        $scope.popup2.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.popup2 = {
        opened: false
    };
    $scope.timeOptions = [{
        name: "Select Time",
        id: 0
    }, {
        name: "Last 24 Hrs",
        id: 1
    }, {
        name: "Last 7 Days",
        id: 7
    }, {
        name: "Last 28 Days",
        id: 28
    }];
    $scope.report.time = $scope.timeOptions[0].id
    $scope.isExists = function (flag, data, id) {
        var idData;
        for (var i = 0; i < data.length; i++) {
            if (flag === 'customer' || flag === 'report') {
                idData = Number(data[i].id);
            } else if (flag === 'group') {
                idData = Number(data[i].groupId);
            } else if (flag === 'site') {
                idData = Number(data[i].siteId);
            } else if (flag === 'params') {
                idData = Number(data[i].value);
            }
            if (idData === Number(id)) {
                return data[i]
            }
        }
    }
    $scope.getDates = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.time > 0) {
            var d = new Date();
            $scope.report.toDate = new Date()
            d.setDate(d.getDate() - Number($scope.report.time));
            $scope.report.fromDate = d;
        } else {
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
        }
    }
    commonFactories.getCustomers(function (data) {
        $scope.customers = [];
        if (data && data.length > 0) {
            $scope.customers = data;
            if ($scope.customers.length > 0) {
                $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
                $scope.$parent.companyLogo = $state.params.customer > 0 ? $scope.isExists('customer', $scope.customers, Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
                $state.go('.', {
                    customer: $scope.report.customer ? $scope.report.customer : 0
                }, {
                    notify: false
                });
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    analyticsLoad();
                });
            }
        }
    });

    function analyticsLoad() {
        $scope.dataType = [];
        $scope.parameterOptions = [];
        $scope.siteSurveyDatatype = [];
        $scope.siteDataDatatype = [];
        $scope.trendingDataDatatype = [];
        ApiFactory.getApiData({
            serviceName: "getAnalyticParams",
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    $scope.dataType.push({
                        id: data.data[1].id,
                        name: data.data[1].label
                    });
                    $scope.dataType.push({
                        id: data.data[4].id,
                        name: data.data[4].label
                    });
                    /*$scope.dataType.push({
                        id: data.data[5].id,
                        name: data.data[5].label
                    });*/
                    $scope.siteSurveyDatatype = data.data[1].analyticParams;
                    $scope.siteDataDatatype = data.data[4].analyticParams;
                    // $scope.trendingDataDatatype = data.data[5].analyticParams;
                }
            },
            onFailure: function () {}
        });
    }

    function labelLoad() {
        $scope.labelDisplay = {};
        if ($scope.customers.length > 0 && $scope.report.customer > 0) {
            $scope.labelDisplay.customer = $scope.report.customer ? $scope.isExists('customer', $scope.customers, $scope.report.customer).label : '';
        }
        if ($scope.groups.length > 0 && $scope.report.group > 0) {
            $scope.labelDisplay.group = $scope.isExists('group', $scope.groups, $scope.report.group).groupName;
        }
        if ($scope.timeOptions.length > 0) {
            $scope.report.time = $state.params.time > 0 ? Number($state.params.time) : Number($scope.timeOptions[0].id);
        }
        $scope.labelDisplay.site = [];
        $.each($scope.sitesSelected, function (key, value) {
            var siteInternalId = $scope.isExists('site', $scope.sites, value.id).siteInternalId ? "#" + $scope.isExists('site', $scope.sites, value.id).siteInternalId : '';
            var siteName = $scope.isExists('site', $scope.sites, value.id).siteName ? $scope.isExists('site', $scope.sites, value.id).siteName + " " + siteInternalId : '';
            $scope.labelDisplay.site.push(siteName);
        });
        $scope.labelDisplay.from = $scope.report.fromDate ? moment($scope.report.fromDate).format("MMM DD YYYY") : '';
        $scope.labelDisplay.to = $scope.report.toDate ? moment($scope.report.toDate).format("MMM DD YYYY") : '';

        $scope.report.mainTitle = "<b>" + $scope.labelDisplay.customer;
        if ($scope.labelDisplay.group) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.group;
        }
        if ($scope.labelDisplay.site.length > 0) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.site.join(", ");
        }
        $scope.report.mainTitle += "</b>";
        if ($scope.dataType.length > 0 && $scope.report.datatype > 0) {
            $scope.labelDisplay.reportType = $scope.report.datatype ? $scope.isExists('report', $scope.dataType, $scope.report.datatype).name : '';
        }
        $scope.report.subTitle = "";
        $scope.report.subTitle += $scope.labelDisplay.reportType + "<br>";
        if ($scope.labelDisplay.from !== '' && $scope.labelDisplay.from) {
            $scope.report.subTitle += $scope.labelDisplay.from;
        }
        if ($scope.labelDisplay.to !== '' && $scope.labelDisplay.to) {
            $scope.report.subTitle += " - " + $scope.labelDisplay.to;
        }
    }

    function loadChart() {
        labelLoad();
        var analyticParams = [];
        var farenHeatParams = [];
        var onOffParams = [];
        var modeParams = [];
        var daysParams = [];
        $.each($scope.parameterSelected, function (key, value) {
            if ($scope.report.datatype === 6) {
                var unit = $scope.isExists('params', $scope.trendingDataDatatype, value.id).labelUnit;
                if (unit === 'F') {
                    farenHeatParams.push(value.id);
                } else if (unit === 'M') {
                    modeParams.push(value.id);
                } else if (unit === 'D') {
                    daysParams.push(value.id);
                } else if (unit === 'B') {
                    onOffParams.push(value.id);
                }
            } else {
                analyticParams.push(value.id);
            }
        });
        var sitesIds = [];
        $.each($scope.sitesSelected, function (key, value) {
            sitesIds.push(value.id)
        });
        var apiData = {
            customerId: $scope.report.customer ? $scope.report.customer : 0,
            groupIds: $scope.report.group ? $scope.report.group : 0,
            siteIds: sitesIds.join(","),
            deviceIds: $scope.report.device ? $scope.report.device : 0,
            fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
            toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
            dataType: $scope.report.datatype ? $scope.report.datatype : 0,
            analyticParams: analyticParams.join(","),
            farenHeatParams: farenHeatParams.join(","),
            onOffParams: onOffParams.join(","),
            modeParams: modeParams.join(","),
            daysParams: daysParams.join(","),
            type: 'SITE'
        };
        chartAPIcall(apiData);
    }

    function chartAPIcall(apiData) {
        $scope.siteChartdata = [];
        if ($scope.report.datatype === 6) {
            $scope.reportTypeStatus = 6;
            $scope.serviceName = "getTrendingAnalytics";
        } else {
            $scope.reportTypeStatus = 0;
            $scope.serviceName = "getSiteAnalyticsData";
        }
        if ($scope.serviceName) {
            ApiFactory.getApiData({
                serviceName: $scope.serviceName,
                data: apiData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        $scope.siteChartdata = data.data;
                        if ($scope.serviceName === "getTrendingAnalytics") {
                            if ($scope.siteChartdata.graphsValidation.isDaysParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Days section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.siteChartdata.graphsValidation.isFarenHeatParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Temperature section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.siteChartdata.graphsValidation.isModeParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("Modes section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                            if ($scope.siteChartdata.graphsValidation.isOnOffParamsValid === false) {
                                $scope.errors.devicesLengthMessage.push("On/Off section graph - A maximum of 8 parameters can be selected at a time");
                                $scope.errors.devicesLength = true;
                            }
                        }
                    } else {
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                }
            });
        }
    }
    $scope.select = function () {
        $scope.report.time = 0;
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
    };
    $scope.getReportData = function (form) {
        if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }
        if (form.$valid) {
            var flag = 0;
            if ($scope.parameterSelected.length <= 0) {
                flag++;
            }
            if ($scope.sitesSelected.length <= 0) {
                flag++;
            }
            if (!$scope.report.fromDate && $scope.report.datatype === 6) {
                flag++;
            }
            if (!$scope.report.toDate && $scope.report.datatype === 6) {
                flag++;
            }
            if (flag > 0) {
                return;
            }
            loadChart()
        }
    }
    $scope.getAnalyics = function () {
        if ($scope.report.datatype === 2) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.siteSurveyDatatype;
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
            $scope.select();
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else if ($scope.report.datatype === 5) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.siteDataDatatype;
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
            $scope.select();
            $.each($scope.parameterOptions, function (key, value) {
                $scope.parameterSelected.push({
                    id: value.value
                })
            });
        } else if ($scope.report.datatype === 6) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.trendingDataDatatype;
        } else {
            $scope.parameterOptions = [];
            $scope.parameterSelected = [];
        }
    }

    $scope.getGroups = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.customer) {
            $scope.$parent.companyLogo = $scope.isExists('customer', $scope.customers, $scope.report.customer).companyLogo;
            loadGroupsSites($scope.report.customer);
        } else {
            $scope.groups = [];
            $scope.sites = [];
            $scope.sitesSelected = [];
        }
    };

    $scope.getSites = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.group) {
            loadSites($scope.report.group);
        } else {
            $scope.groups = [];
            $scope.sites = [];
            $scope.sitesSelected = [];
            usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                $scope.groups = groupSiteData.groups;
                var noneGroup = {
            			"groupName": "Ungroup",
            			"groupId": -1
            		}
            	$scope.groups.push(noneGroup);
                $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                $scope.sites = groupSiteData.sites;
                $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
            });
        }
    };

    function loadGroupsSites(customers) {
        $scope.groups = [];
        $scope.sites = [];
        $scope.sitesSelected = [];
        ApiFactory.getApiData({
            serviceName: "getcustomergroups",
            data: {
                customerIds: customers.toString() ? customers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.groupList.length > 0) {
                        $scope.groups = data.data.groupList;
                    }
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                }
            },
            onFailure: function () {}
        });
    };

    function loadSites(groupsByCustomers) {
        $scope.sites = [];
        $scope.sitesSelected = [];
        ApiFactory.getApiData({
            serviceName: "getsitesbygroupsinreports",
            data: {
            	customerId: $scope.report.customer,
            	groupIds: groupsByCustomers.toString() ? groupsByCustomers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                }
            },
            onFailure: function () {}
        });
    }
}).controller("deviceReportsController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {

    $scope.report = {};
    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];
    $scope.deviceSelected = [];
    $scope.parameterSelected = [];
    $scope.parameterOptions = [{}];
    $scope.dataType = [];
    $scope.daysDatatype = [];
    $scope.statusDataDatatype = [];
    $scope.trendingDataDatatype = [];
    $scope.deviceChartdata = [];
    $scope.isDegreeVsDaysGraph = 0;
    
    $scope.analyticsMessage = {
        buttonDefaultText: 'Select Analytics Data'
    };
    $scope.deviceSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "name",
        idProp: "deviceId"
    };
    $scope.parameterSettings = {
        scrollableHeight: '200px',
        smartButtonMaxItems: 1,
        scrollable: true,
        dynamicTitle: true,
        displayProp: "label",
        idProp: "value"
    };
    $scope.errors = {};
    $scope.errors.devicesLength = false;
    $scope.errors.devicesLengthMessage = [];

    $scope.deviceMessage = {
        buttonDefaultText: 'Select Devices'
    };
    $scope.open1 = function () {
        $scope.popup1.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.open2 = function () {
        $scope.popup2.opened = true;
        var container = $($element).closest(".scroll"),
            scrollTo = $($element).find("[for=inputto]");
        container.animate({
            scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
        });
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.popup2 = {
        opened: false
    };
    $scope.timeOptions = [{
        name: "Select Time",
        id: 0
    }, {
        name: "Last 24 Hrs",
        id: 1
    }, {
        name: "Last 7 Days",
        id: 7
    }, {
        name: "Last 28 Days",
        id: 28
    }];
    $scope.report.time = $scope.timeOptions[0].id
    $scope.isExists = function (flag, data, id) {
        var idData;
        for (var i = 0; i < data.length; i++) {
            if (flag === 'customer' || flag === 'report') {
                idData = Number(data[i].id);
            } else if (flag === 'group') {
                idData = Number(data[i].groupId);
            } else if (flag === 'site') {
                idData = Number(data[i].siteId);
            } else if (flag === 'device') {
                idData = Number(data[i].deviceId);
            } else if (flag === 'params') {
                idData = Number(data[i].value);
            }
            if (idData === Number(id)) {
                return data[i]
            }
        }
    }
    $scope.getDates = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.time > 0) {
            var d = new Date();
            $scope.report.toDate = new Date()
            d.setDate(d.getDate() - Number($scope.report.time));
            $scope.report.fromDate = d;
        } else {
            $scope.report.toDate = "";
            $scope.report.fromDate = "";
        }
    }
    commonFactories.getCustomers(function (data) {
        $scope.customers = [];
        if (data && data.length > 0) {
            $scope.customers = data;
            if ($scope.customers.length > 0) {
                $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
                $scope.$parent.companyLogo = $state.params.customer > 0 ? $scope.isExists('customer', $scope.customers, Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
                $state.go('.', {
                    customer: $scope.report.customer ? $scope.report.customer : 0
                }, {
                    notify: false
                });
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    $scope.devices = groupSiteData.devices;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    analyticsLoad();
                });
            }
        }
    });

    function analyticsLoad() {
        $scope.dataType = [];
        $scope.parameterOptions = [];
        $scope.daysDatatype = [];
        $scope.statusDataDatatype = [];
        $scope.trendingDataDatatype = [];
        ApiFactory.getApiData({
            serviceName: "getAnalyticParams",
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    $scope.dataType.push({
                        id: data.data[6].id,
                        name: data.data[6].label
                    });
                    $scope.dataType.push({
                        id: data.data[7].id,
                        name: data.data[7].label
                    });
                    $scope.dataType.push({
                        id: data.data[5].id,
                        name: data.data[5].label
                    });
                    $scope.daysDatatype = data.data[6].analyticParams;
                    $scope.statusDataDatatype = data.data[7].analyticParams;
                    $scope.trendingDataDatatype = data.data[5].analyticParams;
                }
            },
            onFailure: function () {}
        });
        if ($scope.timeOptions.length > 0) {
            $scope.report.time = $state.params.time > 0 ? Number($state.params.time) : Number($scope.timeOptions[0].id);
        }
        $scope.getDates();
    }

    function labelLoad() {
        $scope.labelDisplay = {};
        $scope.tempLabelDevice = {};
        $scope.tempLabelDeviceArray = [];
        if ($scope.customers.length > 0 && $scope.report.customer > 0) {
            $scope.labelDisplay.customer = $scope.report.customer ? $scope.isExists('customer', $scope.customers, $scope.report.customer).label : '';
        }
        $scope.labelDisplay.device = [];
        $.each($scope.deviceSelected, function (key, value) {
            $scope.labelDisplay.device.push($scope.isExists('device', $scope.devices, value.id).name);
            $scope.tempLabelDevice[value.id] = $scope.isExists('device', $scope.devices, value.id).name;
        });
        $.each($scope.tempLabelDevice, function(key, value){
    		$scope.tempLabelDeviceArray.push({
    			id: key,
    			name: value
             });
    	});
        if ($scope.groups.length > 0 && $scope.report.group > 0) {
            $scope.labelDisplay.group = $scope.report.group ? $scope.isExists('group', $scope.groups, $scope.report.group).groupName : '';
        } else {
        	if ($scope.labelDisplay.device.length > 0) {
        		var tempGroups = [];
        		$.each($scope.deviceSelected, function (key, value) {
        			var temp = $scope.deviceSelected.length > 0 ? ($scope.isExists('device', $scope.devices, value.id) ? $scope.isExists('device', $scope.devices, value.id).groupName : '') : '';
        			if (temp) {
        				tempGroups.push(temp);
        			}
        		});
        		if (tempGroups.length > 0 ) {
        			var names = tempGroups.join(", ").split(",");
        			var uniqueNames = [];
        			$.each(names, function(i, el){
        			    if($.inArray(el.trim(), uniqueNames) === -1) uniqueNames.push(el.trim());
        			});
        			$scope.labelDisplay.group = uniqueNames.join(", ");
        		}
        	}
        }
        if ($scope.sites.length > 0 && $scope.report.site > 0) {
            var siteInternalId = $scope.isExists('site', $scope.sites, $scope.report.site).siteInternalId ? "#" + $scope.isExists('site', $scope.sites, $scope.report.site).siteInternalId : '';
            $scope.labelDisplay.site = $scope.report.site ? $scope.isExists('site', $scope.sites, $scope.report.site).siteName + " " + siteInternalId : '';
        } else {
        	if ($scope.labelDisplay.device.length > 0) {
        		var tempSites = {};
        		$.each($scope.deviceSelected, function (key, value) {
        			var siteInternalId = $scope.isExists('device', $scope.devices, value.id).siteInternalId ? "#" + $scope.isExists('device', $scope.devices, value.id).siteInternalId : '';
        			var temp = $scope.isExists('device', $scope.devices, value.id).siteName + " " + siteInternalId;
        			if (temp) {
        				tempSites[temp] = temp;
        			}
        		});
        		if (tempSites) {
        			var uniqueNamesSite = [];
        			$.each(tempSites, function(key, value){
        				uniqueNamesSite.push(value);
        			});
        			$scope.labelDisplay.site = uniqueNamesSite.join(", ")
        		}
        	}
        }
        $scope.labelDisplay.from = $scope.report.fromDate ? moment($scope.report.fromDate).format("MMM DD YYYY") : '';
        $scope.labelDisplay.to = $scope.report.toDate ? moment($scope.report.toDate).format("MMM DD YYYY") : '';

        $scope.report.mainTitle = "<b>" + $scope.labelDisplay.customer;
        if ($scope.labelDisplay.group) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.group;
        }
        if ($scope.labelDisplay.site) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.site;
        }
        if ($scope.labelDisplay.device.length > 0) {
            $scope.report.mainTitle += " - " + $scope.labelDisplay.device.join(", ");
        }
        $scope.report.mainTitle += "</b>";
        if ($scope.dataType.length > 0 && $scope.report.datatype > 0) {
            $scope.labelDisplay.reportType = $scope.report.datatype ? $scope.isExists('report', $scope.dataType, $scope.report.datatype).name : '';
        }
        $scope.report.subTitle = "";
        $scope.report.subTitle += $scope.labelDisplay.reportType + "<br>";
        if ($scope.labelDisplay.from !== '' && $scope.labelDisplay.from) {
            $scope.report.subTitle += $scope.labelDisplay.from;
        }
        if ($scope.labelDisplay.to !== '' && $scope.labelDisplay.to) {
            $scope.report.subTitle += " - " + $scope.labelDisplay.to;
        }
    }
    $scope.deviceRedirect = function (deviceId) {
   	 $state.go("devices.devicesList.profile.settings", {
            deviceId: deviceId,
            customerId: $scope.report.customer,
            sourcePage: "devices-" + deviceId
        });
   	 return;
   }
    function loadChart() {
        labelLoad();
        var analyticParams = [];
        var farenHeatParams = [];
        var onOffParams = [];
        var modeParams = [];
        var daysParams = [];
        $.each($scope.parameterSelected, function (key, value) {
            var unit;
            if ($scope.report.datatype === 6) {
            	$scope.isDegreeVsDaysGraph = 0;
                unit = $scope.isExists('params', $scope.trendingDataDatatype, value.id).labelUnit;
            } else if ($scope.report.datatype === 7) {
            	$scope.isDegreeVsDaysGraph = 1;
                unit = $scope.isExists('params', $scope.daysDatatype, value.id).labelUnit;
            } else if ($scope.report.datatype === 8) {
            	$scope.isDegreeVsDaysGraph = 0;
                unit = $scope.isExists('params', $scope.statusDataDatatype, value.id).labelUnit;
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
        var deviceIds = [];
        $.each($scope.deviceSelected, function (key, value) {
            deviceIds.push(value.id)
        });
        var apiData = {
            customerId: $scope.report.customer ? $scope.report.customer : 0,
            groupIds: $scope.report.group ? $scope.report.group : 0,
            siteIds: $scope.report.site ? $scope.report.site : 0,
            deviceIds: deviceIds.join(","),
            fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
            toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
            dataType: $scope.report.datatype ? $scope.report.datatype : 0,
            analyticParams: analyticParams.join(","),
            farenHeatParams: farenHeatParams.join(","),
            onOffParams: onOffParams.join(","),
            modeParams: modeParams.join(","),
            daysParams: daysParams.join(","),
            degreeDaysGraph: $scope.isDegreeVsDaysGraph,
            type: 'DEVICE'
        };
        chartAPIcall(apiData);
    }

    function chartAPIcall(apiData) {
        $scope.deviceChartdata = [];
        $scope.serviceName = "getTrendingAnalytics";
        if ($scope.serviceName) {
            ApiFactory.getApiData({
                serviceName: $scope.serviceName,
                data: apiData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $scope.deviceChartdata = data.data;
                        
                        // Here adding additional parameters which are required for Dual Graph
                        if($scope.isDegreeVsDaysGraph == 1){
                        	var degreeDaysList = [];
                        	for (var i = 0; i < data.data.degreeDaysList.length; i++) {
                        		if(data.data.degreeDaysList[i].paramId == 59 || data.data.degreeDaysList[i].paramId == 61 || data.data.degreeDaysList[i].paramId == 63 || data.data.degreeDaysList[i].paramId == 64){
                        			data.data.degreeDaysList[i]["type"] = "spline";
                        			data.data.degreeDaysList[i]["yAxis"] = 1;
                        			data.data.degreeDaysList[i]["lineWidth"] = 20;
                        			data.data.degreeDaysList[i]["linecap"] = "square";
                        			degreeDaysList.push(data.data.degreeDaysList[i]);
                        		}else if(data.data.degreeDaysList[i].paramId == 62 || data.data.degreeDaysList[i].paramId == 65){
                        			data.data.degreeDaysList[i]["type"] = "spline";
                        			data.data.degreeDaysList[i]["lineWidth"] = 2;
                        			degreeDaysList.push(data.data.degreeDaysList[i]);
                        		}
                        	}
                        	data.data.degreeDaysList = degreeDaysList;
                        	$scope.deviceChartdata = data.data;
                        }
                        
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        if ($scope.deviceChartdata.graphsValidation.isDaysParamsValid === false) {
                            $scope.errors.devicesLengthMessage.push("Days section graph - A maximum of 8 parameters can be selected at a time");
                            $scope.errors.devicesLength = true;
                        }
                        if ($scope.deviceChartdata.graphsValidation.isFarenHeatParamsValid === false) {
                            $scope.errors.devicesLengthMessage.push("Temperature section graph - A maximum of 8 parameters can be selected at a time");
                            $scope.errors.devicesLength = true;
                        }
                        if ($scope.deviceChartdata.graphsValidation.isModeParamsValid === false) {
                            $scope.errors.devicesLengthMessage.push("Modes section graph - A maximum of 8 parameters can be selected at a time");
                            $scope.errors.devicesLength = true;
                        }
                        if ($scope.deviceChartdata.graphsValidation.isOnOffParamsValid === false) {
                            $scope.errors.devicesLengthMessage.push("On/Off section graph - A maximum of 8 parameters can be selected at a time");
                            $scope.errors.devicesLength = true;
                        }
                    } else {
                        $scope.errors.devicesLength = false;
                        $scope.errors.devicesLengthMessage = [];
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                }
            });
        }
    }
    $scope.getReportData = function (form) {
        if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }
        if (form.$valid) {
            var flag = 0;
            if ($scope.parameterSelected.length <= 0) {
                flag++;
            }
            if ($scope.deviceSelected.length <= 0) {
                flag++;
            }
            if (!$scope.report.fromDate && $scope.report.datatype) {
                flag++;
            }
            if (!$scope.report.toDate && $scope.report.datatype) {
                flag++;
            }
            if (flag > 0) {
                return;
            }
            loadChart()
        }
    }
    $scope.select = function () {
        $scope.report.time = 0;
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            time: $scope.report.time
        }, {
            notify: false
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
        } else if ($scope.report.datatype === 6) {
            $scope.parameterSelected = [];
            $scope.parameterOptions = [];
            $scope.parameterOptions = $scope.trendingDataDatatype;
        } else {
            $scope.parameterOptions = [];
            $scope.parameterSelected = [];
        }
    }
    $scope.getGroups = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.customer) {
            $scope.$parent.companyLogo = $scope.isExists('customer', $scope.customers, $scope.report.customer).companyLogo;
            loadGroupsSites($scope.report.customer);
        } else {
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
            $scope.deviceSelected = [];
        }
    };
    $scope.getSites = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.group) {
            loadSites($scope.report.group);
        } else {
            $scope.report.site = "";
            $scope.report.device = "";
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
            $scope.deviceSelected = [];
            usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                $scope.groups = groupSiteData.groups;
                var noneGroup = {
            			"groupName": "Ungroup",
            			"groupId": -1
            		}
            	$scope.groups.push(noneGroup);
                $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                $scope.sites = groupSiteData.sites;
                $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                $scope.devices = groupSiteData.devices;
                $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
            });
        }
    };
    $scope.getDevices = function () {
        $state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
        if ($scope.report.site) {
            loadDevicesBySites($scope.report.site);
        } else {
            if ($scope.report.group) {
                loadSites($scope.report.group);
            } else {
                $scope.report.device = "";
                $scope.groups = [];
                $scope.sites = [];
                $scope.devices = [];
                $scope.deviceSelected = [];
                usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    $scope.devices = groupSiteData.devices;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                });
            }
        }
    };

    function loadGroupsSites(customers) {
        $scope.groups = [];
        $scope.sites = [];
        $scope.devices = [];
        $scope.deviceSelected = [];
        ApiFactory.getApiData({
            serviceName: "getcustomergroups",
            data: {
                customerIds: customers.toString() ? customers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.groupList.length > 0) {
                        $scope.groups = data.data.groupList;
                    }
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                    var noneGroup = {
                			"groupName": "Ungroup",
                			"groupId": -1
                		}
                	$scope.groups.push(noneGroup);
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                }
            },
            onFailure: function () {}
        });
    };

    function loadSites(groupsByCustomers) {
        $scope.sites = [];
        $scope.devices = [];
        $scope.deviceSelected = [];
        ApiFactory.getApiData({
            serviceName: "getsitesbygroupsinreports",
            data: {
            	customerId: $scope.report.customer,
            	groupIds: groupsByCustomers.toString() ? groupsByCustomers.toString() : ""
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                }
            },
            onFailure: function () {}
        });
    }

    function loadDevicesBySites(siteId) {
        $scope.devices = [];
        $scope.deviceSelected = [];
        ApiFactory.getApiData({
            serviceName: "getThingListSort",
            data: {
                id: siteId,
                sortId: "siteId"
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.things.length > 0) {
                        $scope.devices = data.data.things;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'devicename');
                    }
                }
            },
            onFailure: function () {}
        });
    }
}).controller("reportMainController", function () {
	
});