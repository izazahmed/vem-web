app.controller("deviceProfileController", function ($scope, $rootScope, $state, $session, deviceFactory, ApiFactory, $timeout, cfpLoadingBar) {
        $scope.editDevice = function (data) {
            $state.params = $.extend($state.params);
            $state.go("devices.devicesList.profile.edit", $state.params);
        };
        $scope.gotoSettings = function () {
            $state.go("devices.devicesList.profile.settings", $state.params);
        };
        $scope.gotoForecasts = function () {
            $state.go("devices.devicesList.profile.forecasts", $state.params);
        };
        $scope.gotoAlerts = function () {
            $state.go("devices.devicesList.profile.alerts", $state.params);
        };
        $scope.gotoActivityLog = function () {
            $state.go("devices.devicesList.profile.activityLog", $state.params);
        };
        $scope.locations = deviceFactory.getLocations;
        $scope.getLocation = deviceFactory.getlocationLabel;
        $scope.getThermoStat = deviceFactory.getThermostatLabel;
        $scope.actionView = {};
        $scope.reloadState = function () {
            // $state.reload();
            $scope.$broadcast('reloadDevice');
        };

        $scope.gotoCustomerProfile = function (customerId) {
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
        }
        
        $scope.gotoGroupProfile = function(customerId,groupId){
            
            
            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                $state.go("groups.groupList.groupInfo.sites", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                $state.go("groups.groupList.groupInfo.devices", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    customerId: customerId
                }));
            } else {
                $state.go("groups.groupList.groupInfo.alerts", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    customerId: customerId
                }));
            }
            
        }
        
        $scope.gotoSiteProfile = function(customerId,siteId){
            
            
            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                        $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            customerId:customerId
                        }));
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                        $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            customerId: customerId
                        }));
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                        $state.go("sites.sitesList.viewSite.users", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            customerId: customerId
                        }));
                    } else {
                        $state.go("sites.sitesList.viewSite.activityLog", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            customerId: customerId
                        }));
                    }
            
            
        }

        if ($state.current.name != "devices.devicesList.profile.settings") {
            //cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "getDeviceDataByDeviceId",
                data: $state.params,
                onSuccess: function (data) {
                    $rootScope.deviceData = data;
                    if (data.status.toLowerCase() === "success") {
                        $timeout(function () {
                            $("#nodata").css({
                                opacity: 1
                            });
                        }, 2000);
                        var data = angular.copy(data.data);
                        if (data.thingState && data.thingState.tstat_msg == 'null') {
                            data.thingState.tstat_msg = null;
                        }
                        $scope.sourceDeviceData = angular.copy(data);
                        $rootScope.deviceProfileData = angular.copy(data);
                        $scope.locations = deviceFactory.getLocations;
                        $scope.getLocation = deviceFactory.getlocationLabel;
                        $scope.getWeekLabel = deviceFactory.getWeekDay;
                        if ($rootScope.deviceProfileData.thingState) {
                            var d = new Date();
                            d.setHours($rootScope.deviceProfileData.thingState.tstat_clock.current_time.split(":")[0]);
                            d.setMinutes($rootScope.deviceProfileData.thingState.tstat_clock.current_time.split(":")[1]);
                            $scope.timeData = d;
                            $scope.hstep = 1;
                            $scope.mstep = 15;
                        }
                        $rootScope.deviceProfileParentData = angular.copy($rootScope.deviceProfileData);

                    } else {}
                    //cfpLoadingBar.complete();
                },
                onFailure: function () {}
            });
        }
    }).controller("settingsProfileController", function ($scope, $state, $rootScope, ApiFactory, toastr, messageFactory, $session, deviceFactory, $element, $timeout, $interval, cfpLoadingBar) {
        $scope.sourceDeviceData = null;
        $rootScope.deviceProfileData = null;


        $scope.$on('reloadDevice', function (e) {
            cfpLoadingBar.start();
            loadDeviceSettingsData(true);
        });


        function naturalCompare(a, b) {
            var ax = [],
                bx = [];

            a.replace(/(\d+)|(\D+)/g, function (_, $1, $2) {
                ax.push([$1 || Infinity, $2 || ""])
            });
            b.replace(/(\d+)|(\D+)/g, function (_, $1, $2) {
                bx.push([$1 || Infinity, $2 || ""])
            });

            while (ax.length && bx.length) {
                var an = ax.shift();
                var bn = bx.shift();
                var nn = (an[0] - bn[0]) || an[1].localeCompare(bn[1]);
                if (nn) return nn;
            }

            return ax.length - bx.length;
        }

        cfpLoadingBar.start();
        loadDeviceSettingsData(true);

        function loadDeviceSettingsData(flag) {
            $scope.cool_setError = false;
            $scope.heat_setError = false;
            $scope.validateCalibration = false;

            ApiFactory.getApiData({
                serviceName: "getDeviceDataByDeviceId",
                data: $state.params,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $("#nodata").css({
                            opacity: 1
                        });
                        var data = angular.copy(data.data);


                        if (data && data.thingState && data.scheduleStatus == 3) {
                            $scope.scheduleMatched = false;
                        } else {
                            $scope.scheduleMatched = true;
                        }




                        if (data && data.thingState && data.thingState.relay_state) {

                            var sortable = [];
                            for (var relay in data.thingState.relay_state) {
                                sortable.push(relay);
                            }

                            var obj = {};

                            $.each(sortable.sort(naturalCompare), function (key, value) {

                                obj[value] = data.thingState.relay_state[value];

                            });

                            data.thingState.relay_state = angular.copy(obj);


                        }

                        if (data.awsCompatible && data.thingState) {
                            data.keyBLockout = data.thingState.keyBLockout;
                        }

                        if (data.awsCompatible && data.thingState) {
                            data.calibration = data.thingState.calibration;
                        }

                        if (data.awsCompatible && data.thingState) {
                            data.configChanges = data.thingState.configChanges;
                        }

                        var config = {};
                        var configChanges = data.configChanges;
                        $.each(configChanges, function (key, value) {
                            config[value.configName] = value.configValue;
                        });
                        data.configChanges = angular.copy(config);

                        if (data.thingState && (data.thingState.tstat_msg && data.thingState.tstat_msg != "") && data.thingState.tstat_msg == 'null') {
                            data.thingState.tstat_msg = null;
                        }
                        if (data.configChanges && (data.configChanges.tstat_msg || data.configChanges.tstat_msg != "") && data.configChanges.tstat_msg == 'null') {
                            data.configChanges.tstat_msg = null;
                        }



                        $scope.devicescreenData = angular.copy(data);


                        if (data.thingState && data.thingState.heat_set) {
                            data.thingState.heat_set = Math.round(data.thingState.heat_set);
                        }
                        if (data.thingState && data.thingState.cool_set) {
                            data.thingState.cool_set = Math.round(data.thingState.cool_set);
                        }
                        if (data.configChanges && data.configChanges.cool_set && data.thingState) {
                            data.thingState.cool_set = data.configChanges.cool_set;
                        }
                        if (data.configChanges && data.configChanges.heat_set && data.thingState) {
                            data.thingState.heat_set = data.configChanges.heat_set;
                        }
                        if (data.configChanges && data.configChanges.temp_hold && data.thingState) {
                            data.thingState.temp_hold = data.configChanges.temp_hold;
                        }
                        if (data.configChanges && data.configChanges.tstat_mode && data.thingState) {
                            data.thingState.tstat_mode = data.configChanges.tstat_mode;
                        }
                        if (data.configChanges && (data.configChanges.tstat_msg || data.configChanges.tstat_msg == "") && data.thingState) {
                            data.thingState.tstat_msg = data.configChanges.tstat_msg;
                        }
                        if (data.configChanges && data.configChanges.fan_state && data.thingState) {
                            data.thingState.fan_state = data.configChanges.fan_state;
                        }
                        if (data.configChanges && data.configChanges.current_time && data.thingState && data.thingState.tstat_clock) {
                            data.thingState.tstat_clock.current_time = data.configChanges.current_time;
                        }
                        if (data.configChanges && data.configChanges.current_time && data.thingState && data.thingState.tstat_clock) {
                            data.thingState.tstat_clock.current_day = data.configChanges.current_day;
                        }
                        if (data.configChanges && (data.configChanges.calibration || data.configChanges.calibration == "") && data.thingState) {
                            data.calibration = data.configChanges.calibration;
                        }
                        if (data.configChanges && data.configChanges.keyBLockout && data.thingState) {
                            data.keyBLockout = data.configChanges.keyBLockout;
                        }
                        
                        if (data.stagesOfHeat==0) {
                        	data.stagesOfHeat = 2;
                        }
                        if (data.stagesOfCool==0) {
                        	data.stagesOfCool = 2;
                        }
                        $scope.sourceDeviceData = angular.copy(data);
                        $rootScope.deviceProfileData = angular.copy(data);
                        $rootScope.deviceProfileData1 = angular.copy(data);
                        if (document && document.activeElement) {
                            document.activeElement.blur();
                        }

                        /*if (!$scope.events || $rootScope.deviceProfileData.scheduleStatus == 3) {
                            $scope.events = angular.copy({});
                        }*/
                       
                        	if ($rootScope.deviceProfileData.dbSchedule && $rootScope.deviceProfileData.scheduleStatus != 1) {
                        		 if (flag) {
                        		loadSchedule();
                        		 }
	                        } else {
	                        	if (flag) {
	                            $scope.events = angular.copy({});
	                        	}
	                            if (!data.scheduleName) {
	                                $scope.sourceDeviceData.scheduleName = "Custom Schedule";
	                                $rootScope.deviceProfileData.scheduleName = "Custom Schedule";
	                                $rootScope.deviceProfileData1.scheduleName = "Custom Schedule";
	                                
	                                if(!data.scheduleStatus){
	                                	
	                                	$scope.sourceDeviceData.scheduleStatus = 2;
	                                    $rootScope.deviceProfileData.scheduleStatus = 2;
	                                    $rootScope.deviceProfileData1.scheduleStatus = 2;
	                                	
	                                }
	                                if (flag) {
	                                $scope.readDeviceSchedule();
	                                }
	                            }
	                        }
                        
                        $scope.locations = deviceFactory.getLocations;
                        $scope.getLocation = deviceFactory.getlocationLabel;
                        $scope.getWeekLabel = deviceFactory.getWeekDay;
                        if ($rootScope.deviceProfileData.thingState) {
                            var d = new Date();
                            d.setHours($rootScope.deviceProfileData.thingState.tstat_clock.current_time.split(":")[0]);
                            d.setMinutes($rootScope.deviceProfileData.thingState.tstat_clock.current_time.split(":")[1]);
                            $scope.timeData = d;
                            $scope.hstep = 1;
                            $scope.mstep = 15;
                        }
                        if (flag) {
                        	loadAnayticsParams();                        	
                        }
                        $timeout(function () {
                            if ($state.current.name == "devices.devicesList.profile.settings") {
                            	loadDeviceSettingsData(false);
                            }
                        }, 60000);
                        if (flag) {
                        $scope.readRefresh = false;
                        }

                        $rootScope.deviceProfileParentData = angular.copy($rootScope.deviceProfileData);

                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }

                },
                onFailure: function () {}
            });
        }
        
      

        $scope.relayData = {
            relay1: "Active in Heat (B)",
            relay2: "Active in Cool (O)",
            relay3: "Fan (G)",
            relay4: "1st Stage Heat (W)",
            relay5: "2nd Stage Heat (W2)",
            relay6: "1st Stage Cool (Y)",
            relay7: "2nd Stage Cool (Y2)",
        };
    
    $scope.getHvacStatus = function(){
        return (($rootScope.deviceProfileData.thingState.op_state == "OFF" && $rootScope.deviceProfileData.thingState.tstat_mode == "OFF") ? "OFF" : ($rootScope.deviceProfileData.thingState.op_state == "OFF") ? "NONE" : $rootScope.deviceProfileData.thingState.op_state);
    }
        $scope.goScheduleProfile = function (scheduleId) {

            $state.go("scheduler.schedulerList.getSchedule.scheduler", $rootScope.mergeObject($state.params, {
                scheduleId: scheduleId
            }))
        };

        function loadSchedule() {
            var myEvents = {};
            if ($rootScope.deviceProfileData.dbSchedule && Object.keys($rootScope.deviceProfileData.dbSchedule).length) {
                $.each($rootScope.deviceProfileData.dbSchedule, function (key, value) {
                    $.each(value, function (key1, value1) {
                        var temp = {};
                        temp.time = moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                        temp.am = (moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                        temp.clpoint = value1.cool_temp_set;
                        temp.htpoint = value1.heat_temp_set;
                        temp.from = "db";
                        if (!myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())]) {
                            myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())] = [];
                        }
                        myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())].push(temp);
                    });
                });
            }
            $scope.events = angular.copy(myEvents);
            $scope.occHours = angular.copy($rootScope.deviceProfileData.occHours);

            if (!$scope.events) {
                $scope.events = angular.copy({});
            }


        }

        $scope.changeClock = function (time, day) {
            cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "setClock",
                data: {
                    "currentTime": moment(time).format("HH:mm"),
                    "currentDay": day,
                    //"xcspecDevId": $rootScope.deviceProfileData.xcspecDeviceId
                    "deviceId": $rootScope.deviceProfileData.deviceId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        loadDeviceSettingsData(false);
                    } else {
                        if (data.code === "Must login or provide credentials.") {
                            toastr.error("Unauthorized while setting the Tstat with XCSPEC");
                        } else {
                            toastr.error(data.code);
                        }
                        $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                    }
                    cfpLoadingBar.complete();

                },
                onFailure: function () {
                    cfpLoadingBar.complete();
                }
            });
        };

        $scope.changeSetPoint = function (type, data) {
        	
            $scope.validateCalibration = true;
            if (!data && type == "3" && $("[name=calibration]").val()) {
                // $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                return;
            }
            if ((type == "3" && data == $scope.sourceDeviceData.calibration)) {
                return;
            }
            if ((type == "6" && data == $scope.sourceDeviceData.thingState.tstat_msg)) {
                return;
            }

            if ((type == "7" && data == $scope.sourceDeviceData.eTInterval)) {
                return;
            }
            cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "setTstatData",
                data: {
                    "data": data ? data.toString() : "",
                    "type": type.toString(),
                    //"xcspecDevId": $rootScope.deviceProfileData.xcspecDeviceId

                    "deviceId": $rootScope.deviceProfileData.deviceId

                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        // document.activeElement.blur();
                        toastr.success(messageFactory.getMessage(data.code));
                        loadDeviceSettingsData(false);
                    } else {
                        if (data.code === "Must login or provide credentials.") {
                            toastr.error("Unauthorized while setting the Tstat with XCSPEC");
                        } else {
                            toastr.error(data.code);
                        }
                        $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                    }
                    cfpLoadingBar.complete();

                },
                onFailure: function () {
                    cfpLoadingBar.complete();
                }
            });
        };
        $scope.setTemp = function (mode, temp) {
            $scope.cool_setError = false;
            $scope.heat_setError = false;

            if (mode.toLowerCase().trim() === "cool") {
                $scope.cool_setError = true;
            }
            if (mode.toLowerCase().trim() === "heat") {
                $scope.heat_setError = true;
            }


            if (mode.toLowerCase().trim() === "heat") {

                if ($rootScope.deviceProfileData.minSP > temp || $rootScope.deviceProfileData.maxSP < temp) {
                    return;
                }
                /*if (!($rootScope.deviceProfileData.minSP > temp || $rootScope.deviceProfileData.maxSP < temp) && temp >= $rootScope.deviceProfileData.thingState.cool_set) {
                    return;
                }*/
            }
            if (mode.toLowerCase().trim() === "cool") {

                if ($rootScope.deviceProfileData.minSP > temp || $rootScope.deviceProfileData.maxSP < temp) {
                    return;
                }
                /*if (!($rootScope.deviceProfileData.minSP > temp || $rootScope.deviceProfileData.maxSP < temp) && $rootScope.deviceProfileData.thingState.heat_set >= temp) {
                    return;
                }*/
            }
            if (!temp || temp === "null") {
                $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                return;
            }
            if (mode.toLowerCase().trim() === "cool" && temp == $scope.sourceDeviceData.thingState.cool_set) {
                return;
            }
            if (mode.toLowerCase().trim() === "heat" && temp == $scope.sourceDeviceData.thingState.heat_set) {
                return;
            }
            cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "setTemperature",
                data: {
                    "mode": mode,
                    "temp": temp,
                    // "xcspecDevId": $rootScope.deviceProfileData.xcspecDeviceId,
                    "deviceId": $rootScope.deviceProfileData.deviceId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        // document.activeElement.blur();
                        toastr.success(messageFactory.getMessage(data.code));
                        loadDeviceSettingsData(false);
                    } else {
                        if (data.code === "Must login or provide credentials.") {
                            toastr.error("Unauthorized while setting the temparature with XCSPEC");
                        } else {
                            toastr.error(data.code);
                        }
                        $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                    }

                    cfpLoadingBar.complete();
                },
                onFailure: function () {
                    cfpLoadingBar.complete();
                }
            });
        };
        $scope.createCustomSchedule = function (schedule) {
            $state.go("devices.devicesList.profile.settings.addCustomSchedule", {
                scheduleId: schedule.scheduleId
            });
        };

        $scope.updateHeatPump = function (type, data) {

            
            if(type == 2){
                
               $rootScope.deviceProfileData.stagesOfCool =  (($rootScope.deviceProfileData.stagesOfHeat == 1 || $rootScope.deviceProfileData.stagesOfHeat == 2) ? $rootScope.deviceProfileData.stagesOfHeat : $rootScope.deviceProfileData.stagesOfCool);
                
                 updateHeatPumpDetails(2,data);
                 if(data==1 || data==2)
                 updateHeatPumpDetails(3,data);
            }
            if(type == 3){
                
               $rootScope.deviceProfileData.stagesOfHeat = $rootScope.deviceProfileData.stagesOfCool;
                updateHeatPumpDetails(2,data);
                updateHeatPumpDetails(3,data);
            }
            

        };
        
$scope.updateHeatPumpRadio = function (type, data) {

	updateHeatPumpDetails(type,data);
            

        };

    
    function updateHeatPumpDetails(type,data){
        
        
        
            cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "updateHeatpumpField",
                data: {
                    "deviceId": Number($state.params.deviceId),
                    "heatPumpUpdateType": type,
                    "heatPumpUpdateTypeValue": data
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                    } else {
                        if (data.code === "Must login or provide credentials.") {
                            toastr.error("Unauthorized while setting the Tstat with XCSPEC");
                        } else {
                            toastr.error(data.code);
                        }
                        $rootScope.deviceProfileData = angular.copy($scope.sourceDeviceData);
                    }
                    cfpLoadingBar.complete();
                    $scope.$broadcast('reloadDevice');
                },
                onFailure: function () {
                    cfpLoadingBar.complete();
                }
            });
        
    }

        $scope.showDetailsAnalytics = function () {
            $scope.showDetailsAnalyticsCont = !$scope.showDetailsAnalyticsCont;
            if (!$scope.commonChartData && $scope.showDetailsAnalyticsCont) {
                loadAnalyticsData();
            }
        };


        /* Starts Analytic reports*/
        $scope.report = {};
        $scope.dataType = [];
        $scope.parameterSelected = [];
        $scope.farenHeatParams = [];
        $scope.onOffParams = [];
        $scope.modeParams = [];
        $scope.daysParams = [];
        $scope.parameterOptions = [];
        $scope.daysDatatype = [];
        $scope.statusDataDatatype = [];
        $scope.trendingDataDatatype = [];
        $scope.isDegreeVsDaysGraph = 0;

        function loadAnayticsParams() {
            $scope.dataType = [];
            $scope.daysDatatype = [];
            $scope.statusDataDatatype = [];
            $scope.trendingDataDatatype = [];
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
                        $scope.report.datatype = 1;
                        loadAnalyticsData();
                    }
                },
                onFailure: function () {}
            });
        }

        function loadAnalyticsData() {

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
            $scope.loadAnalytics = function (scroll) {
            	cfpLoadingBar.start();
                $scope.labelDisplay = {};
                $scope.getDates();
                $scope.report.mainTitle = "";
                $scope.report.subTitle = "";
                if ($rootScope.deviceProfileData.name) {
                    $scope.report.subTitle += $rootScope.deviceProfileData.name + "<br>";
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

                if ($scope.report.datatype === 7) {
                    $scope.isDegreeVsDaysGraph = 1;
                } else {
                    $scope.isDegreeVsDaysGraph = 0;
                }

                var apiData = {
                    customerId: $state.params.customerId,
                    groupIds: 0,
                    siteIds: 0,
                    deviceIds: $state.params.deviceId,
                    fromDate: $scope.report.fromDate ? moment($scope.report.fromDate).format("YYYY-MM-DD HH:mm") : '',
                    toDate: $scope.report.toDate ? moment($scope.report.toDate).format("YYYY-MM-DD HH:mm") : '',
                    dataType: $scope.report.datatype ? $scope.report.datatype : 0,
                    analyticParams: $scope.parameterSelected.join(","),
                    farenHeatParams: $scope.farenHeatParams.join(","),
                    onOffParams: $scope.onOffParams.join(","),
                    modeParams: $scope.modeParams.join(","),
                    daysParams: $scope.daysParams.join(","),
                    degreeDaysGraph: $scope.isDegreeVsDaysGraph,
                    type: 'DEVICE'
                };
                $scope.serviceName = "getTrendingAnalytics";
                $scope.commonChartData = [];
                ApiFactory.getApiData({
                    serviceName: $scope.serviceName,
                    data: apiData,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $scope.commonChartData = data.data;

                            // Here adding additional parameters which are required for Dual Graph
                            if ($scope.isDegreeVsDaysGraph == 1) {
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

                            if (scroll) {
                                $timeout(function () {
                                    var container = $($element).closest(".scroll"),
                                        scrollTo = $($element).find("#datatype");
                                    container.animate({
                                        scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
                                    });
                                }, 500);
                            }
                        }

                        cfpLoadingBar.complete();
                    }
                });
            }
            $scope.getAnalyics = function (scroll) {
                $scope.parameterSelected = [];
                $scope.farenHeatParams = [];
                $scope.onOffParams = [];
                $scope.modeParams = [];
                $scope.daysParams = [];
                if ($scope.report.datatype === 7) {
                    $scope.parameterOptions = $scope.daysDatatype;
                } else if ($scope.report.datatype === 8) {
                    $scope.parameterOptions = $scope.statusDataDatatype;
                } else if ($scope.report.datatype === 1) {
                    $scope.parameterOptions = $scope.trendingDataDatatype;
                } else {
                    $scope.parameterOptions = [];
                    $scope.parameterSelected = [];
                    $scope.commonChartData = [];
                }
                if ($scope.parameterOptions.length > 0) {
                    $.each($scope.parameterOptions, function (key, value) {
                        var unit = $scope.isExists('params', $scope.parameterOptions, value.value).labelUnit;
                        if (unit === 'F') {
                            $scope.farenHeatParams.push(value.value);
                        } else if (unit === 'M') {
                            $scope.modeParams.push(value.value);
                        } else if (unit === 'D') {
                            $scope.daysParams.push(value.value);
                        } else if (unit === 'B') {
                            $scope.onOffParams.push(value.value);
                        } else {
                            $scope.parameterSelected.push(value.value);
                        }
                    });
                }
                $scope.loadAnalytics(scroll);
            }
            $scope.getAnalyics();
        }
        $scope.readRefresh = false;


        $scope.readDeviceSchedule = function () {

            ApiFactory.getApiData({
                serviceName: "readSchedule",
                data: $state.params.deviceId,
                onSuccess: function (data) {
                    if (data.data.dbSchedule) {
                        data.data.dbSchedule = null;
                    }
                    $scope.readSchedule = data.data;
                    loadDeviceSchedule();
                },
                onFailure: function () {}
            });
        };

        function loadDeviceSchedule() {

            var myEvents = {};
            if ($scope.readSchedule.devSchedule && Object.keys($scope.readSchedule.devSchedule).length) {
                var coolData = JSON.parse($scope.readSchedule.devSchedule.scheduleCool)[1].schedule;
                var heatData = JSON.parse($scope.readSchedule.devSchedule.scheduleHeat)[1].schedule;
                $.each(coolData, function (coolKey, coolValue) {
                    myEvents[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())] = [];
                    $.each(heatData, function (heatKey, heatValue) {
                        if (coolKey == heatKey) {
                            for (var i = 0; i < coolValue.length; i++) {
                                var temp = {};
                                for (var j = 0; j < heatValue.length; j++) {
                                    if (coolValue[i].period_xy == heatValue[j].period_xy) {
                                        temp.time = moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                                        temp.am = (moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                                        temp.clpoint = coolValue[i].temp_set;
                                        temp.htpoint = heatValue[j].temp_set;
                                        temp.period_xy = heatValue[j].period_xy;
                                        temp.from = "db";
                                    }
                                }
                                myEvents[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())].push(temp);
                            }
                        }
                    });
                });
            }
            $scope.events = angular.copy(myEvents);

        }


        $scope.readCurrentSchedule = function () {
        	
            if ($scope.readRefresh) {
            	 cfpLoadingBar.start();
                loadDeviceSettingsData(true);
                $scope.scheduleMatched = true;
                $scope.readRefresh = !$scope.readRefresh;
                return;

            }

            cfpLoadingBar.start();
            ApiFactory.getApiData({
                serviceName: "readSchedule",
                data: $state.params.deviceId,
                onSuccess: function (data) {
                    $scope.readSchedule = data.data;
                    loadReadSchedule();
                    compareSchedules();
                    $scope.readRefresh = !$scope.readRefresh;
                    cfpLoadingBar.complete();
                },
                onFailure: function () {}
            });
        };


        function loadReadSchedule() {

            var myEvents = {};
            if ($scope.readSchedule.devSchedule && Object.keys($scope.readSchedule.devSchedule).length) {
                var coolData = JSON.parse($scope.readSchedule.devSchedule.scheduleCool)[1].schedule;
                var heatData = JSON.parse($scope.readSchedule.devSchedule.scheduleHeat)[1].schedule;
                $.each(coolData, function (coolKey, coolValue) {
                    myEvents[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())] = [];
                    $.each(heatData, function (heatKey, heatValue) {
                        if (coolKey == heatKey) {
                            for (var i = 0; i < coolValue.length; i++) {
                                var temp = {};
                                for (var j = 0; j < heatValue.length; j++) {
                                    if (coolValue[i].period_xy == heatValue[j].period_xy) {
                                        temp.time = moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                                        temp.am = (moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                                        temp.clpoint = coolValue[i].temp_set;
                                        temp.htpoint = heatValue[j].temp_set;
                                        temp.period_xy = heatValue[j].period_xy;
                                        temp.from = "device";
                                    }
                                }
                                myEvents[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())].push(temp);
                            }
                        }
                    });
                });
            }
            if ($scope.readSchedule.dbSchedule && Object.keys($scope.readSchedule.dbSchedule).length) {
                $.each($scope.readSchedule.dbSchedule, function (key, value) {
                    $.each(value, function (key1, value1) {
                        var temp = {};
                        temp.time = moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                        temp.am = (moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                        temp.clpoint = value1.cool_temp_set;
                        temp.htpoint = value1.heat_temp_set;
                        temp.from = "db";
                        if (!myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())]) {
                            myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())] = [];
                        }
                        myEvents[deviceFactory.getWeekDayNumber(key.toLowerCase())].push(temp);
                    });
                });
            }
            $scope.events = angular.copy(myEvents);

        }

        function compareSchedules() {


            var devSchedule = {};
            if ($scope.readSchedule.devSchedule && Object.keys($scope.readSchedule.devSchedule).length) {
                var coolData = JSON.parse($scope.readSchedule.devSchedule.scheduleCool)[1].schedule;
                var heatData = JSON.parse($scope.readSchedule.devSchedule.scheduleHeat)[1].schedule;
                $.each(coolData, function (coolKey, coolValue) {
                    devSchedule[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())] = [];
                    $.each(heatData, function (heatKey, heatValue) {
                        if (coolKey == heatKey) {
                            for (var i = 0; i < coolValue.length; i++) {
                                var temp = {};
                                for (var j = 0; j < heatValue.length; j++) {
                                    if (coolValue[i].period_xy == heatValue[j].period_xy) {
                                        temp.time = moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                                        temp.am = (moment(coolValue[i].start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                                        temp.clpoint = Number(coolValue[i].temp_set);
                                        temp.htpoint = Number(heatValue[j].temp_set);
                                        //temp.period_xy = heatValue[j].period_xy;

                                    }
                                }
                                devSchedule[deviceFactory.getWeekDayNumber(coolKey.toLowerCase())].push(temp);
                            }
                        }
                    });
                });
            }
            var dbSchedule = {};
            if ($scope.readSchedule.dbSchedule && Object.keys($scope.readSchedule.dbSchedule).length) {
                $.each($scope.readSchedule.dbSchedule, function (key, value) {
                    $.each(value, function (key1, value1) {
                        var temp = {};
                        temp.time = moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[0];
                        temp.am = (moment(value1.start_time, ["HH:mm"]).format("h:mm A").split(" ")[1] == "AM") ? 1 : 2;
                        temp.clpoint = value1.cool_temp_set;
                        temp.htpoint = value1.heat_temp_set;

                        if (!dbSchedule[deviceFactory.getWeekDayNumber(key.toLowerCase())]) {
                            dbSchedule[deviceFactory.getWeekDayNumber(key.toLowerCase())] = [];
                        }
                        dbSchedule[deviceFactory.getWeekDayNumber(key.toLowerCase())].push(temp);
                    });
                });
            }
            if (JSON.stringify(devSchedule) != JSON.stringify(dbSchedule)) {
                $scope.scheduleMatched = false;
            }

        }

        $scope.runSchedule = function () {

        	
        	/*if($rootScope.deviceProfileData.thingState && $rootScope.deviceProfileData.thingState.temp_hold == 'ENABLE' && $rootScope.deviceProfileData.scheduleStatus == 2){
        		$rootScope.deviceProfileData.thingState.temp_hold = "DISABLE";
        		$scope.changeSetPoint('1',$rootScope.deviceProfileData.thingState.temp_hold);
        		return;
        	}
        	
        	if($scope.events && $rootScope.deviceProfileData.scheduleStatus != 3 && $rootScope.deviceProfileData.scheduleStatus != 1 && $rootScope.deviceProfileData.thingState.temp_hold != 'ENABLE' && !$rootScope.getSetAtLabel($rootScope.deviceProfileData)){
        		$rootScope.deviceProfileData.thingState.temp_hold = "ENABLE";
        		$scope.changeSetPoint('1',$rootScope.deviceProfileData.thingState.temp_hold);
        		setTimeout(function(){
        		$rootScope.deviceProfileData.thingState.temp_hold = "DISABLE";
        		$scope.changeSetPoint('1',$rootScope.deviceProfileData.thingState.temp_hold);
        		},30000);
        		return;
        	}*/
        	
            ApiFactory.getApiData({
                serviceName: "runSchedule",
                data: {
                    deviceId: $state.params.deviceId,
                    scheduleId: $rootScope.deviceProfileData.scheduleId,
                    xcspec: $rootScope.deviceProfileData.xcspecDeviceId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        if (data.status.toLowerCase() === "success") {
                            toastr.success(messageFactory.getMessage(data.code));
                            loadDeviceSettingsData(true);
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    }
                },
                onFailure: function () {}
            });


        }

        /* Ends Analytic reports*/
    }).controller("addCustomScheduleController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout) {
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
        $scope.noofrows = ['1', '2', '3', '4'];
        $scope.errorMsg = [];
        $.each($scope.noofrows, function (key) {
            $scope.errorMsg.push(false);
        });

        $scope.schedule = {};
        $scope.schedule.timePoints = [{}];
        var finalMap = new Object();
        $scope.schedule.show = false;
        $scope.schedule.scheduleName = "Custom Schedule";
        $scope.schedule.deviceId = $state.params.deviceId;
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
        };

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
                        delete k1[i].dow_id;
                    }
                    days.push(JSON.stringify(k1));
                }
            });
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
                });
                var data = data.sort(function (a, b) { /*Turn your strings into dates, and then subtract them*/ /*to get a value that is either negative, positive, or zero.*/
                    return new Date(b.time) - new Date(a.time);
                }).reverse();
                $scope.schedule.timePoints = angular.copy(data);
                return;
            }
        }
        if (angular.isUndefined($rootScope.scheduleId)) {
            $rootScope.scheduleId = $state.params.scheduleId;
        }


        $scope.checkSelectedDays = function () {
            var checkDays = [];

            $.each($scope.schedule.daysOfWeek, function (key, value) {
                if (value && value != -1) {
                    checkDays.push(key);
                }
            });


            return checkDays;
        }


        ApiFactory.getApiData({
            serviceName: "getSchedule",
            data: {
                scheduleId: $state.params.scheduleId
            },
            onSuccess: function (data) {
                $rootScope.schedule = data.data;
                getSchedule();
                $scope.schedule.scheduleName = "Custom Schedule";
            },
            onFailure: function () {}
        });
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
                    });
                    var data = data.sort(function (a, b) { /*Turn your strings into dates, and then subtract them*/ /*to get a value that is either negative, positive, or zero.*/
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
                            delete k1[i].dow_id;
                        }
                        days.push(JSON.stringify(k1));
                    }
                });
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
                    });
                    var data = data.sort(function (a, b) { /* Turn your strings into dates, and then subtract them*/ /*to get a value that is either negative, positive, or zero.*/
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
                });
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
                                if (data[i].time === data[j].time && data[i].am === data[j].am) {
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
                }, 500);
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

            };
            $scope.updateSchedule = function (form) {
                form.$submitted = false;
                /*if (Object.keys(finalMap).length <= 0) {
                    toastr.error(messageFactory.getMessage("ERROR_SCHEDULE_NO_DATA_8002"));
                    return false;
                }*/

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
                        var modal = $(this);
                        modal.find('.modal-title').text('Delete schedule');
                        modal.find('.model-content').text('Are you sure you want to delete this schedule?');
                        $(this).off('show.bs.modal');
                    });
                    $('#modelDialog').modal("show");
                    var emptyWeeks = [' Monday', ' Tuesday', ' Wednesday', ' Thursday', ' Friday', ' Saturday', ' Sunday'];
                    $.each($scope.schedule.timepointsmap, function (key, value) {
                        delete emptyWeeks[key - 1];
                    });
                    emptyWeeks = emptyWeeks.filter(function (element) {
                        return element != null;
                    });
                    var weeks;
                    if (emptyWeeks.length > 2) {
                        var day = emptyWeeks.toString();
                        var sub = day.substr(0, day.lastIndexOf(","));
                        weeks = sub + " and " + emptyWeeks[emptyWeeks.length - 1];
                    } else {
                        weeks = emptyWeeks.toString().replace(",", " and");
                    }
                    $('#modelDialog').on('show.bs.modal', function (event) {
                        var modal = $(this);
                        modal.find('.modal-title').text('There is no schedule for ' + weeks);
                        modal.find('.model-content').text('Are you sure you want to continue');
                        $(this).off('show.bs.modal');
                    });
                    $('#modelDialog').modal("show");
                    return;
                }
                var data = angular.copy($scope.schedule);
                var reqParams = ["deviceId", "dayandheatingpnts", "daysOfWeek", "scheduleName", "show", "timePoints", "timepointsmap"];
                var data1 = angular.copy(data);
                $.each(data, function (key, value) {
                    if (reqParams.indexOf(key) == -1) {
                        delete data1[key];
                    }
                });
                ApiFactory.getApiData({
                    serviceName: "adddeviceschedule",
                    data: data1,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            toastr.success(messageFactory.getMessage(data.code));
                            skipChecking = true;
                            history.back();
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            };
        }
        $rootScope.accept = function () {
            $('#modelDialog').modal("hide");
            var data = angular.copy($scope.schedule);
            var reqParams = ["deviceId", "dayandheatingpnts", "daysOfWeek", "scheduleName", "show", "timePoints", "timepointsmap"];
            var data1 = angular.copy(data);
            $.each(data, function (key, value) {
                if (reqParams.indexOf(key) == -1) {
                    delete data1[key];
                }
            });
            ApiFactory.getApiData({
                serviceName: "adddeviceschedule",
                data: data1,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        skipChecking = true;
                        history.back();
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        };
        $scope.$watch("schedule.timePoints", function () {
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
    }).controller("activityLogProfileController", function ($scope, $state, $rootScope, ApiFactory, deviceFactory, $element) {
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
        $scope.activityObj.specificId = $state.params.deviceId;
        $scope.activityObj.serviceId = 4;
        $scope.startDate = "";
        $scope.endDate = "";
        $scope.activityObj.startDate = "";
        $scope.activityObj.endDate = "";
        $.each($($element).find("form").find("input,select,textarea"), function () {
            if ($(this).attr("name")) {
                $scope.activityObj[$(this).attr("name")] = "";
            }
        });

        function listActivity() {
            ApiFactory.getApiData({
                serviceName: "getActivityLog",
                data: $scope.activityObj,
                onSuccess: function (data) {
                    $("#nodata").css({
                        opacity: 1
                    });
                    $scope.logs = data.data;
                },
                onFailure: function () {}
            });
        }
        listActivity();
        $scope.dateFilterData = function () {
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
        };
        $scope.logActivityFilter = function (obj) {
            if ($scope.activityObj.filterBy) {
                if ($scope.activityObj.filterBy.toLowerCase() === 'called') {
                    return (obj.alAction.toLowerCase() === 'called' || obj.alAction.toLowerCase() === 'emailed' || obj.alAction.toLowerCase() === 'texted')
                } else {
                    return obj.alAction.toLowerCase() === $scope.activityObj.filterBy.toLowerCase();
                }
            }
            $scope.searchActivity = function (item) {
                var input = item.aldescription;
                if (!$scope.filterActivity || (input.toLowerCase().indexOf($scope.filterActivity.toLowerCase()) !== -1)) {
                    return true;
                }
                return false;
            };
            $scope.searchActivity = function (item) {
                var input = item.aldescription;
                if (!$scope.filterActivity || (input.toLowerCase().indexOf($scope.filterActivity.toLowerCase()) !== -1)) {
                    return true;
                }
                return false;
            };
            return true;
        };

    }).controller("forecastsProfileController", function ($scope, $rootScope, $timeout, toastr, messageFactory, ApiFactory, $state, $element) {


        $scope.$watch(function () {
            return $('[aria-live=assertive]').find(".ng-binding").text();
        }, function (val) {
            if ($('[aria-live=assertive]').find(".dummy-label")[0]) {
                $('[aria-live=assertive]').find('.dummy-label').text(val.trim().split(" ")[0]);

            } else {
                $('[aria-live=assertive]').prepend("<strong class='dummy-label'>" + val.trim().split(" ")[0] + "</strong>");

            }

            $("[aria-live=assertive][ng-disabled='datepickerMode === maxMode'][disabled]").closest(".thead").hide();


            $('[aria-live=assertive]').closest("th").next("th").removeClass("disableTab");

            $('[aria-live=assertive]').closest("th").prev("th").removeClass("disableTab");


            if (val.trim().split(" ")[0].toLowerCase() == 'december') {
                $('[aria-live=assertive]').closest("th").next("th").addClass("disableTab");
            }


            if (val.trim().split(" ")[0].toLowerCase() == 'january') {
                $('[aria-live=assertive]').closest("th").prev("th").addClass("disableTab");
            }

            //TODO: write code here, slit wrists, etc. etc.
        });

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



        $scope.applyToDevice = function () {

            $state.go("sites.sitesList.viewSite.forecasts.applyView", $state.params);
        }
        $scope.changedFromDate = function () {
            if ($scope.forecast.fromDate) {
                $scope.forecast.fromDate = new Date(moment($scope.forecast.fromDate)).setFullYear(new Date().getFullYear());
            }
            if ($scope.forecast.toDate) {
                var date = new Date(moment($scope.forecast.fromDate));
                if (moment($scope.forecast.fromDate).isSameOrAfter($scope.forecast.toDate)) {
                    date = angular.copy(date.setDate(date.getDate() + 1));
                    $scope.forecast.toDate = date;
                }
            }
        };
        $scope.changedToDate = function () {
            if ($scope.forecast.toDate) {
                $scope.forecast.toDate = new Date(moment($scope.forecast.toDate)).setFullYear(new Date().getFullYear());
            }
        };

        $scope.minDate = new Date(new Date().getFullYear(), 0, 1);
        $scope.maxDate = new Date(new Date().getFullYear(), 11, 31);
        $scope.edit = false;
        $scope.editedForecast = null;
        $scope.forecast = {};
        $scope.schedule = {};
        $scope.showForm = true;
        $scope.showDaily = true;
        $scope.data = [];

    
        function processForecast(data) {
            var abc = [];
            $.each(data.data, function (key, value) {
                var temp = {};
                temp.forecastName = value.forecastName;
                temp.forecastId = value.forecastId;

                //temp.fromDate = new Date(moment(value.fromDate).format("YYYY-MM-DD") + " 00:00:00");
                //temp.toDate = new Date(moment(value.toDate).format("YYYY-MM-DD") + " 00:00:00");

                temp.fromDate = moment(value.fromDate);
                temp.toDate = moment(value.toDate);

                if (value.forecastTempList && value.forecastTempList.length) {

                    temp.min = value.forecastTempList[1] && value.forecastTempList[1].minTemp;
                    temp.max = value.forecastTempList[1] && value.forecastTempList[1].maxTemp;
                    temp.minForecastTempId = value.forecastTempList[0] && value.forecastTempList[0].forecastTempId;
                    temp.middleForecastTempId = value.forecastTempList[1] && value.forecastTempList[1].forecastTempId;
                    temp.maxForecastTempId = value.forecastTempList[2] && value.forecastTempList[2].forecastTempId;

                    temp.mode = value.mode;
                    temp.minSchedule = value.forecastTempList[0] && value.forecastTempList[0].scheduleId;
                    temp.middleSchedule = value.forecastTempList[1] && value.forecastTempList[1].scheduleId;
                    temp.maxSchedule = value.forecastTempList[2] && value.forecastTempList[2].scheduleId;


                }
                abc.push(temp);
            });
            
            
            $scope.data = angular.copy(abc);

            if ($scope.data.length > 3) {
                $scope.showForm = false;
            }
            if ($scope.schedule.daily) {
                $scope.showForm = false;
            }

        }
    

        $scope.cancelData = function (form) {
            if (form) {
                form.$submitted = false;
            }
            $timeout(function () {
                $scope.forecast = {};
                $scope.forecast.mode = 0;
                if ($scope.forecastList.length) {
                    $scope.forecast.mode = $scope.forecastList[0].mode;
                }

            }, 100);
            if ($scope.data.length > 3) {
                $scope.showForm = false;
            }
            if ($scope.schedule.daily) {
                $scope.showForm = false;
            }
            if (!$scope.showForm) {
                $($element).closest(".scroll").animate({
                    scrollTop: 0
                });
            }
            $scope.edit = false;
        };
    

        var data = $state.current.name.indexOf('sites.sitesList.viewSite.forecasts') != -1 ? {
            type: 0,
            typeId: $state.params.siteId
        } : {
            type: 1,
            typeId: $state.params.deviceId
        };

        $scope.forecastList = [];

        function loadForecastList() {

            ApiFactory.getApiData({
                serviceName: "getForecastList",
                data: data,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() == "success") {

                        $scope.forecastList = angular.copy(data.data);
                        if (data.data.length) {

                            $scope.forecast.mode = data.data[0].mode;
                        }
                        processForecast(data);

                        if (data.data.length == 1) {

                            var from = new Date(new Date().getFullYear(), 0, 1);
                            var to = new Date(new Date().getFullYear(), 11, 31);

                            if (moment(from).format("MM/DD/YYYY") == moment(data.data[0].fromDate).format("MM/DD/YYYY") && moment(to).format("MM/DD/YYYY") == moment(data.data[0].toDate).format("MM/DD/YYYY")) {

                                $scope.schedule.daily = true;
                                $scope.showForm = false;
                            }

                        }



                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        }
        loadForecastList();
        var requestData = "";

        if ($state.current.name.indexOf('sites.sitesList.viewSite.forecasts') != -1) {

            requestData = {
                "sortBy": "siteId",
                value: $state.params.siteId
            };

        } else {

            requestData = {
                "sortBy": "deviceId",
                value: $state.params.deviceId
            };

        }

        ApiFactory.getApiData({
            serviceName: "getScheduleList",
            data: requestData,
            onSuccess: function (data) {
                if (data.status.toLowerCase() == "success") {
                    $scope.schedules = data.data;
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        });
        $scope.getScheduleLabel = function (id) {
            var selected;
            $.each($scope.schedules, function (key, value) {
                if (value.id == id) {
                    selected = value.label;
                }
            });
            return selected;
        };
        $scope.checkedDaily = function () {
            if ($scope.schedule.daily) {
                $scope.forecast.fromDate = new Date(new Date().getFullYear(), 0, 1);
                $scope.forecast.toDate = new Date(new Date().getFullYear(), 11, 31);
            } else {
                $scope.forecast.fromDate = "";
                $scope.forecast.toDate = "";
            }
        };



        $scope.addForecast = function (form) {
            if (moment($scope.forecast.fromDate).diff(moment($scope.forecast.toDate)) > 0) {
                toastr.error(messageFactory.getMessage("CLIENT_SIDE_DEVICE_FORECAST_ERROR_INVALID_DATES"));
                return;
            }
            if (form.$valid) {
                $scope.forecastForm = form;
                if ($scope.data.length > 0) {
                    var data;
                    if ($scope.edit) {
                        data = angular.copy($scope.data);
                        data[$scope.editedForecast].forecastName = "";
                        data[$scope.editedForecast].fromDate = "";
                        data[$scope.editedForecast].toDate = "";
                    } else {
                        data = angular.copy($scope.data);
                    }
                    var sameName = false;
                    var valid = true;
                    var dates = [$scope.forecast.fromDate, $scope.forecast.toDate];
                    $.each(data, function (key, value) {
                        if (value.forecastName.toString().toLowerCase() == $scope.forecast.forecastName.toString().toLowerCase()) {
                            sameName = true;
                        }
                        $.each(dates, function (key1, value1) {

                            if ($scope.edit && !value1) {
                                return;
                            }
                            var dateFrom = moment(value.fromDate).format("MM/DD/YYYY");
                            var dateTo = moment(value.toDate).format("MM/DD/YYYY");
                            var dateCheck = moment(value1).format("MM/DD/YYYY");
                            if (dateCheckDiff(dateFrom, dateTo, dateCheck)) {
                                valid = false;
                            }
                        });
                    });
                    if (sameName) {
                        toastr.error(messageFactory.getMessage("CLIENT_SIDE_DEVICE_FORECAST_ERROR_SAME_NAME"));
                        return;
                    }
                    if (!valid) {
                        toastr.error(messageFactory.getMessage("CLIENT_SIDE_DEVICE_FORECAST_ERROR_SAME_DATE"));
                        return;
                    }
                }

                if ($scope.edit) {
                    var data = [angular.copy($scope.forecast)];
                    $scope.sendForecast(data);

                } else {
                    var data = [angular.copy($scope.forecast)];
                    $scope.sendForecast(data);
                }
                if ($scope.data.length > 3) {
                    $scope.showForm = false;
                }
                if ($scope.schedule.daily) {
                    $scope.showForm = false;
                }
            }
        };



        function dateCheckDiff(from, to, check) {

            var fDate, lDate, cDate;
            fDate = Date.parse(from);
            lDate = Date.parse(to);
            cDate = Date.parse(check);

            if ((cDate <= lDate && cDate >= fDate)) {
                return true;
            }
            return false;
        }



        $scope.deleteForecast = function (index) {



            $('#modelDialog').on('show.bs.modal', function (event) {
                $(this).off('show.bs.modal');
                var modal = $(this);

                modal.find('.modal-title').text('Delete Forecast');
                modal.find('.model-content').text('Are you sure you want to delete this Forecast?');
            });

            $("#modelDialog").find('.confirm')[0].targetElement = {
                actionType: "delete",
                index: index,
                id: $scope.data[index].forecastId

            };

            $("#modelDialog").modal("show");


        };

        $rootScope.accept = function () {

            switch ($("#modelDialog").find('.confirm')[0].targetElement.actionType) {


                case "delete":


                    ApiFactory.getApiData({
                        serviceName: "deleteForecast",
                        data: $("#modelDialog").find('.confirm')[0].targetElement.id,
                        onSuccess: function (data) {
                            if (data.status.toLowerCase() === "success") {
                                $scope.data.splice($("#modelDialog").find('.confirm')[0].targetElement.index, 1);
                                $scope.forecastList.splice($("#modelDialog").find('.confirm')[0].targetElement.index, 1);
                                if ($scope.data.length == 0) {
                                    $scope.schedule.daily = false;
                                    $scope.forecast.fromDate = "";
                                    $scope.forecast.toDate = "";
                                }

                                if ($scope.data.length > 3) {
                                    $scope.showForm = false;
                                } else {
                                    $scope.showForm = true;
                                }
                                if ($scope.schedule.daily) {
                                    $scope.showForm = false;
                                }

                                $scope.cancelData();
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
        $scope.editForecast = function (index) {
            $scope.showForm = true;
            $scope.data[index].fromDate = new Date($scope.data[index].fromDate);
            $scope.data[index].toDate = new Date($scope.data[index].toDate);
            $scope.forecast = angular.copy($scope.data[index]);
            if (!$scope.showDaily) {
                $scope.showDaily = false;
            }
            $scope.edit = true;
            $scope.editedForecast = index;

            $($element).closest(".scroll").animate({
                scrollTop: 200
            });

        };
        $scope.sendForecast = function (data) {
            if (data.length > 0) {
                var data = angular.copy(data);
                $.each(data, function (key, value) {
                    value.fromDate = moment(value.fromDate).format("YYYY-MM-DD") + " 00:00:00";
                    value.toDate = moment(value.toDate).format("YYYY-MM-DD") + " 00:00:00";
                    value.type = ($state.current.name === 'sites.sitesList.viewSite.forecasts') ? 0 : 1;
                    value.typeId = ($state.current.name === 'sites.sitesList.viewSite.forecasts') ? Number($state.params.siteId) : Number($state.params.deviceId);
                    value.forecastTempList = [{
                        minTemp: 0,
                        maxTemp: (value.min - 1),
                        scheduleId: value.minSchedule
                }, {
                        minTemp: value.min,
                        maxTemp: value.max,
                        scheduleId: value.middleSchedule
                }, {
                        minTemp: (value.max + 1),
                        maxTemp: (value.max + 1) < 200 ? 200 : ((value.max + 1) + 1),
                        scheduleId: value.maxSchedule
                }];

                    if (value.forecastId) {

                        value.forecastTempList[0].forecastTempId = value.minForecastTempId;
                        value.forecastTempList[0].forecastId = value.forecastId;
                        value.forecastTempList[1].forecastTempId = value.middleForecastTempId;
                        value.forecastTempList[1].forecastId = value.forecastId;
                        value.forecastTempList[2].forecastTempId = value.maxForecastTempId;
                        value.forecastTempList[2].forecastId = value.forecastId;



                    }

                    delete value.min;
                    delete value.max;
                    delete value.minSchedule;
                    delete value.middleSchedule;
                    delete value.maxSchedule;
                    delete value.minForecastTempId;
                    delete value.middleForecastTempId;
                    delete value.maxForecastTempId;
                });

                var selected = data;
                if (!data[0].forecastId) {
                    data[0].addType = "add";
                }



                var from = new Date(new Date(moment(data[0].fromDate)).getFullYear(), 0, 1);
                var to = new Date(new Date(moment(data[0].toDate)).getFullYear(), 11, 31);

                if (moment(from).format("MM/DD/YYYY") == moment(data[0].fromDate).format("MM/DD/YYYY") && moment(to).format("MM/DD/YYYY") == moment(data[0].toDate).format("MM/DD/YYYY")) {

                    $scope.schedule.daily = true;
                    $scope.showForm = false;
                } else {
                    $scope.schedule.daily = false;
                    $scope.showForm = true;
                }



                ApiFactory.getApiData({
                    serviceName: data[0].forecastId ? "updateForecast" : "addForecast",
                    data: {
                        "forecastConfig": data
                    },
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() == "success") {
                            toastr.success(messageFactory.getMessage(data.code));

                            if ($scope.edit) {
                                $scope.edit = false;
                            }

                            if ($scope.data.length > 3) {
                                $scope.showForm = false;
                            }
                            if ($scope.schedule.daily) {
                                $scope.showForm = false;
                            }

                            if (selected[0].forecastId) {

                                $scope.forecastList[$scope.editedForecast] = data.data[0];

                            } else {

                                $scope.forecastList.push(data.data[0]);

                            }

                            data.data = angular.copy($scope.forecastList);

                            processForecast(data);
                            $scope.forecastForm.$submitted = false;
                            $timeout(function () {
                                $scope.forecast = {};
                                $scope.forecast.mode = 0;
                                if ($scope.forecastList.length) {
                                    $scope.forecast.mode = $scope.forecastList[0].mode;
                                }
                            }, 100);
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            } else {
                toastr.error(messageFactory.getMessage("CLIENT_SIDE_DEVICE_FORECAST_ERROR_NO_FORECAST"));
            }
        };
        $scope.startStopForecast = function (forecastMode) {
            var deviceId = ($state.current.name === 'sites.sitesList.viewSite.forecasts') ? Number($state.params.siteId) : Number($state.params.deviceId);
            var forcastType = ($state.current.name === 'sites.sitesList.viewSite.forecasts') ? 0 : 1;
            ApiFactory.getApiData({
                serviceName: "updateForecastMode",
                data: {
                    "deviceId": deviceId,
                    "forecastMode": forecastMode,
                    "forcastType": forcastType
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success" && data.data === 2 && forecastMode == 1) {
                        $scope.forecast.mode = 0;
                        toastr.error("Forecasts are not available.");
                    } else if (data.status.toLowerCase() === "success" && data.data === 1) {
                        toastr.success('Forecast mode updated successfully.');
                        loadForecastList();
                    }
                },
                onFailure: function () {}
            });
        };
    })
    .controller("applyForecastsProfileController", function ($scope, $rootScope, ApiFactory, toastr, $state, messageFactory) {


        $("#modelDialog").modal("show");

        $scope.getDeviceSchedule = {};
        $scope.selectedDevices = [];

        $scope.multiSelectDeviceSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 3,
            scrollable: true,
            showCheckAll: false,
            enableSearch: true,
            showUncheckAll: false,
            dynamicTitle: true,
            displayProp: "deviceName",
            idProp: "deviceId"

        };

        $scope.devicesDropdownEvents = {
            onItemSelect: devicesChangeselect,
            onItemDeselect: devicesChangeDeselect,
            onSelectAll: devicesSelectAll,
            onDeselectAll: devicesUnSelectAll

        };

        function devicesChangeselect(e) {

            if ($scope.getDeviceSchedule[e.id]) {
                $(".container-fluid").css({
                    pointerEvents: "none"
                });

                var x = toastr.warning(messageFactory.getMessage("APPLY_TO_ALREADY_EXIST") + " <br/><button class='yes btn btn-xs btn-default'>Ok</button> <button class='no btn btn-xs btn-default'>Cancel</button>", {
                    timeOut: 0,
                    extendedTimeOut: 0
                });
                $(x.el).on("click", function (event) {
                    if ($(event.target).hasClass("yes")) {


                    }
                    if ($(event.target).hasClass("no")) {

                        $scope.selectedDevices.splice($scope.selectedDevices.indexOf(e.id), 1);

                    }
                    $(".container-fluid").css({
                        pointerEvents: "all"
                    });
                });
            }
        }

        function devicesChangeDeselect() {

        }

        function devicesSelectAll() {

        }

        function devicesUnSelectAll() {

        }


        ApiFactory.getApiData({
            serviceName: "listDevForecast",
            data: {
                siteId: $state.params.siteId
            },
            onSuccess: function (data) {

                $.each(data.data, function (key, value) {

                    $scope.getDeviceSchedule[value.deviceId] = value.forecastFlag;
                });


                $scope.devicesDataArry = data.data;

            },
            onFailure: function (data) {
                toastr.error(messageFactory.getMessage(data.code));
            }
        });


        var data = $state.current.name.indexOf('sites.sitesList.viewSite.forecasts') != -1 ? {
            type: 0,
            typeId: $state.params.siteId
        } : {
            type: 1,
            typeId: $state.params.deviceId
        };

        ApiFactory.getApiData({
            serviceName: "getForecastList",
            data: data,
            onSuccess: function (data) {
                if (data.status.toLowerCase() == "success") {
                    $scope.forecastList = angular.copy(data.data);


                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            },
            onFailure: function () {}
        });


        $scope.applyForecast = function (form) {

            if ($scope.selectedDevices == 0) {
                return;
            }

            var data = angular.copy($scope.forecastList);

            var newData = [];

            $.each(data, function (key, value) {

                var temp = value;
                delete temp.forecastId;
                delete temp.createdOn;
                delete temp.updatedBy;
                delete temp.updatedOn;

                $.each(angular.copy(temp.forecastTempList), function (key1, value1) {

                    delete temp.forecastTempList[key1].forecastId;
                    delete temp.forecastTempList[key1].forecastTempId;

                });

                newData.push(temp);

            });

            var finalData = [];

            $.each($scope.selectedDevices, function (key, value) {

                $.each(angular.copy(newData), function (key1, value1) {

                    var temp = value1;

                    value1.type = 1;
                    value1.typeId = value.id;
                    value1.addType = "apply";
                    finalData.push(temp);
                });

            });

            if (data.length > 0) {
                $(".container-fluid").removeAttr("style");

                ApiFactory.getApiData({
                    serviceName: "addForecast",
                    data: {
                        "forecastConfig": finalData
                    },
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() == "success") {
                            toastr.success(messageFactory.getMessage(data.code));

                            $("#modelDialog").modal("hide");
                            $state.go("sites.sitesList.viewSite.forecasts", $state.params);

                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
            } else {
                toastr.error(messageFactory.getMessage("CLIENT_SIDE_DEVICE_FORECAST_ERROR_NO_FORECAST"));
            }


        };

        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name === "sites.sitesList.viewSite.forecasts.applyView") {
                skipChecking = true;
                $state.go("sites.sitesList.viewSite.forecasts");
                skipChecking = false;
            }

            $(this).off('hidden.bs.modal');
        });

    })
