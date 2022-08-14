app.controller("groupListController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, usersFactory, commonFactories) {

	$scope.isDataAvailable = true;
	
    if ($state.current.name == "groups.groupList") {
        if ($state.params && $state.params.customerId != -1) {
            $state.go($state.current, $rootScope.mergeObject($state.params, {
                customerId: -1,
                sourcePage: "groups"
            }), {
                reload: true
            });
            return;
        }
    }
    $scope.getSourcePageId = function (groupId) {

        if ($state.current.name.indexOf("groups.groupList") != -1 && $state.params.sourcePage) {
            return $state.params.sourcePage.split("-")[0] + "-" + groupId;
        } else {
            return $state.params.sourcePage;
        }
    }
    $scope.alphabets = ['#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
    $scope.alphabetHeight = Math.floor(($(".alphabets-badge-width").height() - 6) / 27);
    $scope.moveRelatedGroup = function (alphabet) {
        $($element).find(".groupslist").animate({
            scrollTop: $($element).find("[group='" + alphabet + "']").position().top + $($element).find(".groupslist").scrollTop()
        });
    };

    if ($state.current.name.indexOf('groups.groupList') != -1 && $rootScope.userDetails.rolePermissions['Group Management'] == 2) {
        commonFactories.getCustomers(function (data) {
            $scope.customerList = data;
        });
    }
    $scope.filtered = {};
    $rootScope.getListGroup = function () {
        listGroup();
    }
    $rootScope.getListGroup();
    $scope.customerTempOptions = {};
    $scope.sitesTempOptions = {};
    $scope.scheduleTempOptions = {};
    $scope.sitesOptions = [];
    $scope.scheduleOptions = [];
    $scope.customerOptions = [];
    $scope.changeGroupActivity = function(){
  	   $timeout(function(){
  		   $scope.filteredLength = $($element).find(".groupslist").find("li").length;
  	   }, 100)
     }
    
    // To sort groups
    $scope.sorter = function (a){
    	   return parseInt(a.groupName.replace( /^\D+/g, '')); // gets number from a string
    	}
    
    function listGroup() {
        $scope.listData = {};
        $scope.listData = commonFactories.fromPageGroups($state.$current.self.name, $state.params);
        ApiFactory.getApiData({
            serviceName: "groupList",
            data: $scope.listData,
            onSuccess: function (data) {
            	$timeout( function(){
            		$("#nodata").css({opacity: 1})
            	}, 5000);
            	
                $rootScope.customeTabCounts.groupCount = data.data.length;
                if (data.data && data.data.length === 0) {
                    $scope.filteredLength = 0;
                    $scope.isDataAvailable = false;
                    return;
                }
                $.each(data.data, function (key, value) {
                    if (value.scheduleId) {
                        $scope.scheduleTempOptions[value.scheduleId] = value.scheduleName;
                    }
                    if (value.customerId) {
                        $scope.customerTempOptions[Number(value.customerId)] = value.customerInfo;
                    }
                    $.each(value.siteInfo, function (key1, value1) {
                        $scope.sitesTempOptions[value1.siteKey] = value1.siteValue;
                    });
                });
                $.map($scope.sitesTempOptions, function (value, key) {
                    $scope.sitesOptions.push({
                        label: value,
                        value: key
                    })
                });
                $.map($scope.scheduleTempOptions, function (value, key) {
                    $scope.scheduleOptions.push({
                        label: value,
                        value: key
                    })
                });
                $.map($scope.customerTempOptions, function (value, key) {
                    $scope.customerOptions.push({
                        label: value,
                        value: key
                    })
                });
                if ($scope.sitesOptions.length > 0) {
                	$scope.sitesOptions = $rootScope.aphaNumSort($scope.sitesOptions,'sitelabel');
                }
                if ($scope.scheduleOptions.length > 0) {
                	$scope.scheduleOptions = $rootScope.aphaNumSort($scope.scheduleOptions,'schedulelabel');
                }
                if ($scope.customerOptions.length > 0) {
                	$scope.customerOptions = $rootScope.aphaNumSort($scope.customerOptions,'customerLabel');
                }
                $scope.groups = commonFactories.makeAlphabetsGroup(data.data, "groupName");
                $scope.changeGroupActivity();
            },
            onFailure: function () {}
        });
    }
    $scope.showPlus = false;
	$scope.checkAll = function() {
		$scope.showMain = $scope.showMain == true ? false : true;	
		$scope.showPlus =  $scope.showPlus == true ? false : true;			 			
		$scope.groups = angular.copy($scope.groups);
	};
	
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
            $scope.changeGroupActivity();
        }
    }
    $scope.groupActivityFilter = function (obj) {
        switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
            case "status":
                return Number(obj.groupStatusCode) == Number($scope.groupActivity);
            case "customer":
                return Number(obj.customerId) === Number($scope.customer);
            case "sites":
                return $scope.isExists(obj.siteInfo)
            case "schedule":
                return Number(obj.scheduleId) === Number($scope.schedule);
        }
        $scope.isExists = function (data) {
            var there = false;
            for (var i = 0; i < data.length; i++) {
                if (Number(data[i].siteKey) === Number($scope.site)) {
                    there = true;
                }
            }
            return there;
        }
        $scope.search = function (item) {
            var input = item.groupName;
            if (!$scope.filterGroups || (input.toLowerCase().indexOf($scope.filterGroups.toLowerCase()) !== -1)) {
            	searchLength();
                return true;
            }
            return false;
        };
        $scope.filterByChange = function () {
            switch ($scope.filterBy.toLowerCase()) {
            	case "customer":
                	$scope.customer = $scope.customerOptions.length > 0 ? $scope.customerOptions[0].value.toString() : "";
                break;
                case "sites":
                    $scope.site = $scope.sitesOptions.length > 0 ? $scope.sitesOptions[0].value.toString() : "";
                    break;
                case "schedule":
                    $scope.schedule = $scope.scheduleOptions.length > 0 ? $scope.scheduleOptions[0].value.toString() : "";
                    break;
            }
            $scope.changeGroupActivity();
        };
        return true;
    };
    $scope.getGroupsData = function (group) {

        var groupId = group.groupId;
        if ($state.current.name.indexOf("groups.groupList") != -1) {
            var customerId = group.customerId;
            var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + group.groupId;
            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                $state.go("groups.groupList.groupInfo.sites", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    sourcePage: sourcePage,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                $state.go("groups.groupList.groupInfo.devices", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    sourcePage: sourcePage,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    sourcePage: sourcePage,
                    customerId: customerId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    sourcePage: sourcePage,
                    customerId: customerId
                }));
            } else {
                $state.go("groups.groupList.groupInfo.alerts", $rootScope.mergeObject($state.params, {
                    groupId: groupId,
                    sourcePage: sourcePage,
                    customerId: customerId
                }));
            }
            return;
        }

        var customerId = group.customerId;

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
    $scope.addGroup = function (customerId) {
    	
    	
    	if(!$rootScope.userDetails.isSuper){
    		
    		 if ($state.$current.self.name === "customers.customersList.getCustomer.groups") {
    	            $state.go("customers.customersList.getCustomer.groups.editManaully");
    	        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups") {
    	            $state.go("scheduler.schedulerList.getSchedule.groups.newGroup.manually");
    	        } else if ($state.$current.self.name === "groups.groupList") {
    	            $state.go("groups.groupList.newGroup.manually", $rootScope.mergeObject($state.params, {
    	                customerId: customerId
    	            }));
    	        } else if ($state.$current.self.name === "users.usersList.userProfile.groups") {
    	            $state.go("users.usersList.userProfile.groups.newGroup.manually");
    	        }
    		return;
    		
    	}
    	
        if ($state.$current.self.name === "customers.customersList.getCustomer.groups") {
            $state.go("customers.customersList.getCustomer.groups.newGroup");
        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups") {
            $state.go("scheduler.schedulerList.getSchedule.groups.newGroup");
        } else if ($state.$current.self.name === "groups.groupList") {
            $state.go("groups.groupList.newGroup", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }), {
                reload: false
            });
        } else if ($state.$current.self.name === "users.usersList.userProfile.groups") {
            $state.go("users.usersList.userProfile.groups.newGroup");
        }
    }
    $('#modelDialog').on('hidden.bs.modal', function () {
        if ($state.current.name === "customers.customersList.getCustomer.groups.newGroup" || $state.current.name === "customers.customersList.getCustomer.groups.editManaully") {
            skipChecking = true;
            $state.go("customers.customersList.getCustomer.groups");
            skipChecking = false;
        }
        if ($state.current.name === "groups.groupList.newGroup" || $state.current.name === "groups.groupList.newGroup.manually") {
            skipChecking = true;
            $state.go("groups.groupList");
            skipChecking = false;
        }
        if ($state.current.name === "scheduler.schedulerList.getSchedule.groups.newGroup" || $state.current.name === "scheduler.schedulerList.getSchedule.groups.newGroup.manually") {
            skipChecking = true;
            $state.go("scheduler.schedulerList.getSchedule.groups");
            skipChecking = false;
        }
        if ($state.current.name === "users.usersList.userProfile.groups.newGroup" || $state.current.name === "users.usersList.userProfile.groups.newGroup.manually") {
            skipChecking = true;
            $state.go("users.usersList.userProfile.groups");
            skipChecking = false;
        }
    });
}).controller("addGroupController", function ($scope, $rootScope, ApiFactory, messageFactory, $state) {
    $('#modelDialog').on('show.bs.modal', function () {
        var modal = $(this);
        modal.find(".modal-title").text("Add New Group");
    });
    $("#modelDialog").modal("show");
    $scope.importManually = function () {
        if ($state.$current.self.name === "customers.customersList.getCustomer.groups.newGroup") {
            $state.go("customers.customersList.getCustomer.groups.editManaully");
        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.newGroup") {
            $state.go("scheduler.schedulerList.getSchedule.groups.newGroup.manually");
        } else if ($state.$current.self.name === "groups.groupList.newGroup") {
            $state.go("groups.groupList.newGroup.manually");
        } else if ($state.$current.self.name === "users.usersList.userProfile.groups.newGroup") {
            $state.go("users.usersList.userProfile.groups.newGroup.manually");
        }
    }
}).controller("groupActivityController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, $element) {
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
    $scope.activityObj.specificId = $state.params.groupId;
    $scope.activityObj.serviceId = 2;
    $scope.startDate = "";
    $scope.endDate = "";
    $scope.activityObj.startDate = "";
    $scope.activityObj.endDate = "";
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.activityObj[$(this).attr("name")] = "";
        }
    });
    $scope.sites = [];
    $scope.devices = [];
    
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
        $scope.activityObj.specificId = $state.params.groupId;
        $scope.activityObj.serviceId = 2;
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

}).controller("newGroupController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory) {

    $scope.sitesFormData = {};
    $scope.selectedSiteGroup = [];
    $scope.uniqueGroupNameError = false;

    $('#modelDialog').on('show.bs.modal', function () {
        var modal = $(this);
        modal.find(".modal-title").text("Add New Group");
    })
    $("#modelDialog").modal("show");

    getGroup();

    function getGroup() {
        ApiFactory.getApiData({
            serviceName: "getSites",
            data: $state.params,
            onSuccess: function (data) {
                $scope.sites = data.data;
            },
            onFailure: function () {}
        });
        $scope.addSiteGroupList = function ($event) {
            if ($scope.sitesFormData.siteList && $scope.sitesFormData.siteList.siteId !== "") {
                $scope.addSiteGroupForm.siteList.$invalid = false;
                $scope.selectedSiteGroup.push($scope.sitesFormData.siteList);
                $scope.selectedSiteGroup = usersFactory.getUniqueArray($scope.selectedSiteGroup, 'siteId');
                
                if ($scope.selectedSiteGroup.indexOf($scope.sitesFormData.siteList) === -1) {
                    $scope.sites.splice($scope.sites.indexOf($scope.sitesFormData.siteList), 1);
                }
            } else {
                $scope.addSiteGroupForm.siteList.$invalid = true;
            }
        };
        $scope.removeSeletedSite = function (selectedSite, $event) {
            $scope.selectedSiteGroup.splice($scope.selectedSiteGroup.indexOf(selectedSite), 1);
            if ($scope.selectedSiteGroup.indexOf($scope.sitesFormData.siteList) === -1) {
                $scope.sites.push(selectedSite);
            }
        }
        $scope.checkGroupName = function () {
            if ($scope.sitesFormData.groupName === "undefined" || $scope.sitesFormData.groupName == null) {
                $scope.uniqueGroupNameError = false;
                $rootScope.$broadcast('gotInternal');
            } else {
                if ($scope.sitesFormData.groupName !== "") {
                    ApiFactory.getApiData({
                        serviceName: "checkGroupDuplicate",
                        data: {
                            groupName: $scope.sitesFormData.groupName,
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
                            $rootScope.$broadcast('gotInternalId');
                        },
                        onFailure: function () {}
                    });
                }
            }
        }
        $scope.$on("gotInternalId", function () {
            if ($scope.uniqueGroupNameError) {
                return;
            }
            	$scope.sitesTempArray = [];
	            $.each($scope.selectedSiteGroup, function (key, value) {
	                if (value !== null && !angular.isUndefined(value.siteName) && value.siteName !== "") {
	                	$scope.sitesTempArray.push(value)
	                }
	            });
                $scope.sitesFormData.selectedLocations = $scope.sitesTempArray;
                $scope.sitesFormData.customerId = $state.params.customerId;

                if ($state.current.name == "users.usersList.userProfile.groups.newGroup.manually") {

                    $scope.sitesFormData = $rootScope.mergeObject($scope.sitesFormData, {
                        userId: $state.params.userId
                    });

                } else {
                    $scope.sitesFormData = $rootScope.mergeObject($scope.sitesFormData, {
                        userId: $rootScope.userDetails.userId
                    });
                }

                ApiFactory.getApiData({
                    serviceName: "addGroup",
                    data: $scope.sitesFormData,
                    onSuccess: function (data) {
                	
                	
                	 
                        if(data.data === 2){
                        	toastr.error(messageFactory.getMessage(data.code));
                        	return false;
                        }else{
                            toastr.success(messageFactory.getMessage(data.code));
                        }
                        
                        if ($state.$current.self.name === "groups.groupList.newGroup.manually") {
                           
                            $state.go("groups.groupList", {}, {
                                reload: true
                            });
                            return;
                        }
                        
                        $timeout(function(){
                            $("#modelDialog").modal("hide");
                        if ($state.$current.self.name === "customers.customersList.getCustomer.groups.editManaully") {
                            $state.go("customers.customersList.getCustomer.groups", {
                                customerId: $scope.sitesFormData.customerId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.editManaully") {
                            $state.go("scheduler.schedulerList.getSchedule.groups", {
                                scheduleId: $state.params.scheduleId
                            }, {
                                reload: true
                            });
                            return;
                        } else if ($state.$current.self.name === "users.usersList.userProfile.groups.editManaully") {
                            $state.go("users.usersList.userProfile.groups", {
                                userId: $state.params.userId
                            }, {
                                reload: true
                            });
                            return;
                        }
                        
                            },500);
                    },
                    onFailure: function () {}
                });
        });
        $scope.addSiteGroup = function (form) {
            if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
            if (form.$valid) {
                $scope.checkGroupName();
            }
        }
    }
}).controller("groupInfoController", function ($scope, $rootScope, ApiFactory, messageFactory, $state) {
    $scope.groupInfo = {};
    
   $scope.gotoSchedules = function(){
        
        $state.go("groups.groupList.groupInfo.schedules",$state.params);
    }
   
   $scope.gotoDevices = function(){
        
        $state.go("groups.groupList.groupInfo.devices",$state.params);
    }
   $scope.gotoDevice = function(){
        
        $state.go("groups.groupList.groupInfo.devices",$state.params);
    }
   $scope.gotoSites = function(){
        
        $state.go("groups.groupList.groupInfo.sites",$state.params);
    }
   $scope.gotoAlerts = function(){
        
        $state.go("groups.groupList.groupInfo.alerts",$state.params);
    }
   $scope.gotoActivityLog = function(){
        
        $state.go("groups.groupList.groupInfo.activitylog",$state.params);
    }
   $scope.gotoUsers = function(){
        
        $state.go("groups.groupList.groupInfo.users",$state.params);
    }
    getGroup();
    function getGroup() {
        ApiFactory.getApiData({
            serviceName: "getGroupInfo",
            data: {
                customerId: $state.params.customerId,
                groupId: $state.params.groupId
            },
            onSuccess: function (data) {
                $scope.groupInfo = data.data;
            },
            onFailure: function () {}
        });
    }
    $scope.editInfo = function (groupId) {
        if ($state.$current.self.name.indexOf("customers.customersList.getCustomer.groups.groupInfo") !== -1) {
            $state.go("customers.customersList.getCustomer.groups.groupInfo.editGroup", {
                groupId: groupId
            });
        } else if ($state.$current.self.name.indexOf("scheduler.schedulerList.getSchedule.groups.groupInfo") !== -1) {
            $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.editGroup", {
                groupId: groupId
            });
        } else if ($state.$current.self.name.indexOf("groups.groupList.groupInfo") !== -1) {
            $state.go("groups.groupList.groupInfo.editGroup", {
                groupId: groupId
            } !== -1);
        } else if ($state.$current.self.name.indexOf("users.usersList.userProfile.groups.groupInfo") !== -1) {
            $state.go("users.usersList.userProfile.groups.groupInfo.editGroup", {
                groupId: groupId
            });
        }
    }
    $scope.getSites = function (groupId) {
        if ($state.$current.self.name.startsWith("customers.customersList.getCustomer.groups.groupInfo") === true) {
            $state.go("customers.customersList.getCustomer.groups.groupInfo.sites", {
                groupId: groupId
            });
        }
        if ($state.$current.self.name.startsWith("scheduler.schedulerList.getSchedule.groups.groupInfo") === true) {
            $state.go("scheduler.schedulerList.getSchedule.groups.groupInfo.sites", {
                groupId: groupId
            });
        } else if ($state.$current.self.name.startsWith("groups.groupList.groupInfo")) {
            $state.go("groups.groupList.groupInfo.sites", {
                groupId: groupId
            });
        } else if ($state.$current.self.name.startsWith("users.usersList.userProfile.groups.groupInfo")) {
            $state.go("users.usersList.userProfile.groups.groupInfo.sites", {
                groupId: groupId
            });
        }
    }

}).controller("editGroupController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory) {
    $scope.unchangedGroupName = "";
    $scope.sitesFormData = {};
    $scope.selectedSitesList=[];
    $scope.selectedSites = [];
    $scope.uniqueGroupNameError = false;
    getGroup();
    function getGroup() {

        ApiFactory.getApiData({
            serviceName: "getGroupSites",
            data: $state.params,
            onSuccess: function (data) {
                $scope.sites = data.data.groupSitesList;
                $scope.selectedSites = data.data.selectedGroupSites;
                $scope.groupStatusList = data.data.groupStatusList;
                $scope.selectedSitesList = angular.copy($scope.selectedSites);
                $scope.unchangedGroupName = data.data.groupInfo.groupName;
                $scope.sitesFormData = data.data.groupInfo;
                $scope.sitesFormData.groupStatusCode = data.data.groupInfo.groupStatusCode;
            },
            onFailure: function () {}
        });

        $scope.addSiteGroupList = function ($event) {
            if ($scope.sitesFormData.siteList && $scope.sitesFormData.siteList.siteId !== "") {
                $scope.selectedSites.push($scope.sitesFormData.siteList);
                $scope.selectedSites = usersFactory.getUniqueArray($scope.selectedSites, 'siteId');
                if ($scope.selectedSites.indexOf($scope.sitesFormData.siteList) === -1) {
                    $scope.sites.splice($scope.sites.indexOf($scope.sitesFormData.siteList), 1);
                }
            } else {
                $scope.addSiteGroupForm.siteList.$invalid = true;
            }
        };
        $scope.removeSeletedSite = function (selectedSite, $event) {
            $scope.selectedSites.splice($scope.selectedSites.indexOf(selectedSite), 1);
            if ($scope.selectedSites.indexOf($scope.sitesFormData.siteList) === -1) {
                $scope.sites.push(selectedSite);
            }
        }
        $scope.checkGroupName = function () {

            if ($scope.sitesFormData.groupName === "undefined" || $scope.sitesFormData.groupName == null) {
                $scope.uniqueGroupNameError = false;
                $rootScope.$broadcast('gotInternal');
            } else {

                if ($scope.sitesFormData.groupName !== "" && angular.lowercase($scope.unchangedGroupName) !== angular.lowercase($scope.sitesFormData.groupName)) {
                    ApiFactory.getApiData({
                        serviceName: "checkGroupDuplicate",
                        data: {
                            groupName: $scope.sitesFormData.groupName,
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
                            $rootScope.$broadcast('gotInternal');
                        },
                        onFailure: function () {}
                    });
                } else if (angular.lowercase($scope.unchangedGroupName) === angular.lowercase($scope.sitesFormData.groupName)) {
                    $scope.uniqueGroupNameError = false;
                    $rootScope.$broadcast('gotInternal');
                }
            }
        }
        $scope.$on("gotInternal", function () {
            if ($scope.uniqueGroupNameError) {
                return;
            }
            $scope.sitesTempArray = [];
            $.each($scope.selectedSites, function (key, value) {
                if (value !== null && !angular.isUndefined(value.siteName) && value.siteName !== "") {
                	$scope.sitesTempArray.push(value)
                }
            });
                $scope.sitesFormData.customerId = $state.params.customerId;
                $scope.sitesFormData.selectedLocations = $scope.sitesTempArray;
                ApiFactory.getApiData({
                    serviceName: "updateGroup",
                    data: $scope.sitesFormData,
                    onSuccess: function (data) {
                    	if(data.data === 2)
                        	toastr.error(messageFactory.getMessage(data.code));
                        else
                        	toastr.success(messageFactory.getMessage(data.code));
                    	 history.back();
                    },
                    onFailure: function () {}
                });
        });
        $scope.addSiteGroup = function (form) {
        	if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
            if (form.$valid) {
                $scope.checkGroupName();
            }
        }
        $scope.cancelGroup = function (groupId) {
            if ($state.$current.self.name === "customers.customersList.getCustomer.groups.groupInfo.editGroup") {
                $state.go("customers.customersList.getCustomer.groups.groupInfo.sites", {
                    groupId: groupId
                });
            } else if ($state.$current.self.name === "groups.groupList.groupInfo.editGroup") {
                $state.go("groups.groupList.groupInfo.sites", {
                    groupId: groupId
                });
            } else if ($state.$current.self.name === "scheduler.schedulerList.getSchedule.groups.groupInfo.editGroup") {
                $state.go("scheduler.schedulerList.getSchedule.groups", {
                    groupId: groupId,
                    scheduleId: $state.params.scheduleId
                });
            } else if ($state.$current.self.name === "users.usersList.userProfile.groups.groupInfo.editGroup") {
                $state.go("users.usersList.userProfile.groups", {
                    groupId: groupId,
                    userId: $state.params.userId
                });
            }
        }
        $scope.deleteSiteGroup = function () {
        	
        	
            $('#modelDialog').on('show.bs.modal', function () {
                var modal = $(this);
                modal.find('.modal-title').text('Delete group');
                modal.find('.model-content').text('Are you sure you want to delete this group?');
                return;
            });
        }

        $rootScope.accept = function () {
            $scope.sitesFormData.customerId = $state.params.customerId;
            $scope.sitesFormData.selectedLocations = $scope.selectedSites;
            
            ApiFactory.getApiData({
                serviceName: "deleteGroup",
                data: $scope.sitesFormData,
                onSuccess: function (data) {
                	if(data.data === 2)
                    	toastr.error(messageFactory.getMessage(data.code));
                    else
                    	toastr.success(messageFactory.getMessage(data.code));
                    $state.go("groups.groupList");
                },
                onFailure: function () {}
            });
        };
    }
})