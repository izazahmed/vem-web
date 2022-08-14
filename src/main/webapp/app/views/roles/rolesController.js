/** 
 *@file : rolesController 
 * 
 *@rolesController :Load role module functionality for application 
 * 
 *@author :(Bhoomika Rabadiya - brabadiya@ctepl.com) 
 * 
 *@Contact :(Umang - ugupta@ctepl.com) 
 * 
 *@Contact : (Chenna - yreddy@ctepl.com) 
 *
 *@version     VEM2-1.0
 *@date        22-08-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       22-08-2016  Bhoomika Rabadiya           File Created
 * 02       22-08-2016  Bhoomika Rabadiya           Added rolesListController
 * 03       23-08-2016  Bhoomika Rabadiya           Added newRoleController
 * 04       23-08-2016  Bhoomika Rabadiya           Added viewRoleController
 * 05       29-08-2016  Bhoomika Rabadiya           Added editRoleController
 * 06		02-11-2016	Nagarjuna Eerla				Added the getUserDetails api call after updation of user details to update session details
 *
 */
app.controller("rolesListController", function ($scope, ApiFactory, $rootScope, $state, $element, $timeout) {
	function predicatBy(prop) {
	   return function(a,b) {
	      if ( a[prop] > b[prop]) {
	          return 1;
	      } else if ( a[prop] < b[prop] ) {
	          return -1;
	      }
	      return 0;
	   }
	}
	$scope.permissions = [{}];
	var rtime;
    var timeout = false;
    var delta = 200;
    $scope.isDataAvailable = true;

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
            $scope.changeRoleActivity();
        }
    }
    $scope.changeRoleActivity = function () {
        $timeout(function () {
        	$scope.filteredLength = $($element).find(".rolelist").find("li").length;
        }, 100)
    }
    ApiFactory.getApiData({
        serviceName: "listroles",
        onSuccess: function (data) {
            $.each(data.data.roleList, function (key, value) {
            	value.permissions.sort( predicatBy("permissionCode") );
                var map = {};
                $.each(value.permissions, function (key, value) {
                	map[value.permissionLevelName] = map[value.permissionLevelName] ? map[value.permissionLevelName] + ", " + value.permissionCode : value.permissionCode;
                })
                value.permissionList = [];
        	    value.permissionList = map;
            });
            $("#nodata").css({ opacity: 1 });
            if (data.data && data.data.length === 0) {
                $scope.filteredLength = 0;
                $scope.isDataAvailable = false;
                return;
            }
            $scope.roles = data.data.roleList;
        	$scope.permissions = data.data.permissionsData.permissions;
        	$scope.changeRoleActivity();
        },
        onFailure: function () {}
    });
    $scope.getRole = function (role) {
    	$rootScope.role = role;
    	$rootScope.permissions = $scope.permissions;
        $state.go("roles.rolesList.getRole", { roleId: role.roleId});
    };
    $scope.search = function (item) {
        var input = item.roleName;
        if (!$scope.filterRoles || (input.toLowerCase().indexOf($scope.filterRoles.toLowerCase()) !== -1)) {
            searchLength();
            return true;
        }
        return false;
    };
}).controller("newRoleController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state, toastr) {

	$scope.role = {};
	$scope.role.createActivityLog = 0;
	$scope.checkPermission = false;
	$scope.permissions = [{}];
	$scope.role.permissions = [{}];
	var result = [];
	$.each($($element).find("form").find("input,select,textarea"), function() {	
		if ($(this).attr("name")) {		
			$scope.role[$(this).attr("name")] = "";		
		}		
	});
	$scope.roleOptions = [{ "label": "Admin", "value": 1 }, { "label": "User", "value": 0 }];
	$scope.statusOptions = [{ "label": "Active", "value": 1 }, { "label": "Inactive", "value": 0 }];
	$scope.role.isActive = $scope.statusOptions[0].value; 
	ApiFactory.getApiData({
        serviceName: "loadPermissions",
        onSuccess: function (data) {
        	$scope.permissions =  data.data.permissions;
        	viewRole();
        },
        onFailure: function () {}
    });   
   
	function viewRole() {
	    $scope.role.roleType = 1;
	    for (var j in $scope.permissions) {
	    	if ($scope.permissions[j].permissionName === "Dashboard Management") {
	    		result[j] = {permission : 0, permissionLevel: 3 };
	    	} else {
	    		result[j] = {permission : 0, permissionLevel: 1 };
	    	}
		};
		$scope.role.permissions = result;
	    $scope.createRole = function (form) {
	    	if (!form.$valid) {

	    		var showvalid = false;
    			$.each($scope.role.permissions, function(key, value) {
			    	if (value.permission !== 0) {
			    		showvalid = true;
			    		return;
			    	}
			    });
	    		if (showvalid) {
	    			$scope.checkPermission = false;
	    		} else {
	    			$scope.checkPermission = true;
	    		}
	    	
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
	    	if (form.$valid) {

	    		var show = false;
    			$.each($scope.role.permissions, function(key, value) {
			    	if (value.permission !== 0) {
			    		show = true;
			    		return;
			    	}
			    });
	    		if (show) {
	    			$scope.checkPermission = false;
	    		} else {
	    			$scope.checkPermission = true;
	    		}
	    	
	    		if ($scope.checkPermission) {
	    			return;
	    		}
	    		
	    		$scope.roleData = angular.copy($scope.role);
		    	$scope.roleData.permissions = $scope.roleData.permissions.filter(function (value) {
	    		    return (value.permission !== 0);
	    		});
		    	 
		    	ApiFactory.getApiData({
	                serviceName: "addrole",
	                data: $scope.roleData,
	                onSuccess: function (data) {
	                    if (data.status.toLowerCase() === "success") {
	                        toastr.success(messageFactory.getMessage(data.code));
	                        skipChecking = true;
	                        $state.go("roles.rolesList");
	                    } else {
	                        toastr.error(messageFactory.getMessage(data.code));
	                    }
	                },
	                onFailure: function () {}
	            });
	    	}
	    }
	}
}).controller("viewRoleController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state) {
	$scope.role = {};
	$scope.permissions = [{}];
	$scope.role.permissions = [{}];
	var result = [];
	$scope.roleOptions = [{ "label": "Admin", "value": 1 }, { "label": "User", "value": 0 }];
	$scope.statusOptions = [{ "label": "Active", "value": 1 }, { "label": "Inactive", "value": 0 }];
	ApiFactory.getApiData({
        serviceName: "getrole",
        data: $state.params,
        onSuccess: function (data) {        	
        	$rootScope.permissions = data.data.permissionsData.permissions;
        	$rootScope.role = data.data.roleData;
        	viewRole();
        },
        onFailure: function () {}
    });
	function viewRole() {
		$scope.permissions = $rootScope.permissions;
		$scope.role = $.extend($scope.role, $rootScope.role);
		var resultTemp = {};
		for (var j in $scope.permissions) {
			result[j] = {};
			resultTemp[$scope.permissions[j].permissionId] = j;
			if ($scope.permissions[j].permissionName === "Dashboard Management") {
				result[j] = { permission : 0, permissionLevel: 3 };
			} else {
				result[j] = { permission : 0, permissionLevel: 1 };
			}
		};
		for (var p in $scope.role.permissions) {
			result[resultTemp[$scope.role.permissions[p].permission]] = $scope.role.permissions[p];
		};
		$scope.role.permissions = result;
	}
	 $scope.editRole = function () {
		 $rootScope.role = $scope.role;
         $state.go("roles.rolesList.getRole.editRole");
     };
}).controller("editRoleController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state, toastr, $timeout) {
	$scope.role = {};
	$scope.permissions = [{}];
	$scope.role.permissions = [{}];
	var result = [];
	$scope.roleOptions = [{ "label": "Admin", "value": 1 }, {"label": "User", "value": 0 }];
	$scope.statusOptions = [{ "label": "Active", "value": 1 }, { "label": "Inactive", "value": 0 }];
	if (angular.isUndefined($rootScope.role)) {
		ApiFactory.getApiData({
	        serviceName: "getrole",
	        data: $state.params,
	        onSuccess: function (data) {        	
	        	$rootScope.permissions = data.data.permissionsData.permissions;
	        	$rootScope.role = data.data.roleData;
	        	viewRole();
	        },
	        onFailure: function () {}
	    });
	} else {
		viewRole();
	}   
   
	function viewRole() {
		$scope.permissions = $rootScope.permissions;
		$scope.role = $.extend($scope.role, $rootScope.role);
		var resultTemp = {};
		for (var j in $scope.permissions) {
			result[j] = {};
			resultTemp[$scope.permissions[j].permissionId] = j;
			if ($scope.permissions[j].permissionName === "Dashboard Management") {
				result[j] = { permission : 0, permissionLevel: 3 };
			} else {
				result[j] = { permission : 0, permissionLevel: 1 };
			}
		};
		for (var p in $scope.role.permissions) {
			result[resultTemp[$scope.role.permissions[p].permission]] = $scope.role.permissions[p];
		};
		
		$scope.role.permissions = result;
	
	    $scope.updateRole = function (form) {
	    	$scope.isDirty = $scope.editRoleForm.$dirty;
	    	if (!form.$valid) {

	    		var showvalid = false;
    			$.each($scope.role.permissions, function(key, value) {
			    	if (value.permission !== 0) {
			    		showvalid = true;
			    		return;
			    	}
			    });
	    		if (showvalid) {
	    			$scope.checkPermission = false;
	    		} else {
	    			$scope.checkPermission = true;
	    		}
	    	
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
	    	if (form.$valid) {
	    		var show = false;
    			$.each($scope.role.permissions, function(key, value) {
			    	if (value.permission !== 0) {
			    		show = true;
			    		return;
			    	}
			    });
	    		if (show) {
	    			$scope.checkPermission = false;
	    		} else {
	    			$scope.checkPermission = true;
	    		}
	    	
	    		if ($scope.checkPermission) {
	    			return;
	    		}
	    		if ($scope.isDirty && $scope.role.usersCount > 1) {
	    			$scope.changeRole();
	    		} else {
	    			editRoleDetais();
	    		}
	    	}
	    }
	    function editRoleDetais() {
	    	$scope.roleData = angular.copy($scope.role);
	    	$scope.roleData.permissions = $scope.roleData.permissions.filter(function (value) {
    		    return (value.permission !== 0);
    		});
	    	ApiFactory.getApiData({
                serviceName: "updateRole",
                data: $scope.roleData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        skipChecking = true;
                        $state.go("roles.rolesList");
                        
                        // Updating session data on success of update roles
                        ApiFactory.getApiData({
                            serviceName: "getUserDetails",
                            data: {
                            	userId : $rootScope.userDetails.userId
                            },
                            onSuccess: function (data) {
                                if (data.status.toLowerCase() === "success") {
                                    // Updating userDetails session details
                                	$rootScope.userDetails = data.data;
                                	eaiDetails = data.data;
                                }
                            },
                            onFailure: function () {}
                        });
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
	    }
	    $scope.changeRole = function () {
            $('#modelDialog').on('show.bs.modal', function () {
                var modal = $(this)
                modal.find('.modal-title').text('Edit role');
                modal.find('.model-content').html("Are you sure you would like to edit this role, as there are other users with the same assigned roles? <a href='#users/usersList?sourcePage=users&roleId="+$rootScope.role.roleId+"' style='color:#1a6394;text-decoration: underline;'>Click here</a> to get a list of all the other users.");
            });
            $("#modelDialog").find('.confirm')[0].targetElement = {
                actionType: "edit"
            };
	    	$('#modelDialog').modal("show");
        }
	    $scope.removeRole = function () {
            $('#modelDialog').on('show.bs.modal', function () {
                var modal = $(this)
                modal.find('.modal-title').text('Delete role');
                modal.find('.model-content').text('Are you sure you want to delete this role?');
            });
            $("#modelDialog").find('.confirm')[0].targetElement = {
                actionType: "delete"
            };
        }
        $rootScope.accept = function () {
        	switch ($("#modelDialog").find('.confirm')[0].targetElement.actionType) {
                case "delete":
		           ApiFactory.getApiData({
		                serviceName: "deleteRole",
		                data: {roleId: $rootScope.role.roleId},
		                onSuccess: function (data) {
		                    if (data.status.toLowerCase() === "success") {
		                        $("#modelDialog").modal("hide");
		                        skipChecking = true;
		                        $state.go("roles.rolesList");
		                        toastr.success(messageFactory.getMessage(data.code));
		                    } else {
		                        var x = toastr.error(messageFactory.getMessage(data.code) + " <button class='relatedLinks btn btn-xs btn-default'>Click here</button> to get a list of all the associated users.", {
		                            timeOut: 0,
		                            extendedTimeOut: 0
		                        });
		                        $(x.el).on("click", function (event) {
		                            if ($(event.target).hasClass("relatedLinks")) {
		                                skipChecking = true;
		                                $state.go("users.usersList", {
		                                    roleId: $state.params.roleId,sourcePage:"users"
		                                });
		                            }
		                        });
		                        $("#modelDialog").modal("hide");
		                    }
		                },
		                onFailure: function () {
		                    $("#modelDialog").modal("hide");
		                }
		            });
		        break;
		        
                case "edit":
                	editRoleDetais();
                	$("#modelDialog").modal("hide");
                break;
            }
        }
	}
});