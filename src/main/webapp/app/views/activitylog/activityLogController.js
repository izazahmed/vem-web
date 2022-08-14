/**
 *@file : activityLogController
 *
 *@activityLogController :Load Activity Log module functionality for application
 *
 *@author :(Bhoomika Rabadiya - brabadiya@ctepl.com)
 *
 *@Contact :(Umang - ugupta@ctepl.com)
 *
 *@Contact : (Chenna - yreddy@ctepl.com)
 *
 *@version     VEM2-1.0
 *@date        11-11-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       22-08-2016  Bhoomika Rabadiya           File Created
 * 02       22-08-2016  Bhoomika Rabadiya           Added activityLogController
 * 03       23-08-2016  Bhoomika Rabadiya           Added newactivityLogController
 *
 */
app.controller("activityLogListController", function ($scope, ApiFactory, $rootScope, $state, $element, usersFactory, DTOptionsBuilder, DTColumnBuilder, $timeout) {
    $scope.open1 = function () {
        $scope.popup1.opened = true;
    };
    $scope.dataLoaded = false;
    $scope.open2 = function () {
        $scope.popup2.opened = true;
    };

    $scope.isDataAvailable = true;

    $scope.popup1 = {
        opened: false
    };

    $scope.popup2 = {
        opened: false
    };

    $scope.activityObj = {};
    $scope.activityData = {};
    $scope.activityObj.specificId = 0;
    $scope.activityObj.serviceId = 0;
    $scope.startDate = "";
    $scope.endDate = "";
    $scope.activityObj.startDate = "";
    $scope.activityObj.endDate = "";
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.activityObj[$(this).attr("name")] = "";
        }
    });
    $scope.activityObj.currentPage = 1;
    $scope.activityObj.currentPageList = 1;
    $scope.activityObj.recordsPerPage = 10;
    

    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];

    $scope.dtInstance = null;

    var customers = {};
    var groups = {};
    var sites = {};
    var devices = {};
    $scope.pageIndexValue = 1;
    $scope.totalRecords = 0;
    
    $scope.options = {
    		  maxDate: new Date(), // restrict maximum date to today
    		  showWeeks: true
    		};
    
    $scope.filteredTotalRecords = 0;
    

    $scope.dtColumns = [
                         DTColumnBuilder.newColumn(null).withTitle('Id').renderWith(function (data, type, full) {
            if($scope.activityObj.filterBy && (($scope.activityObj.filterBy.toLowerCase() === 'module')||($scope.activityObj.filterBy.toLowerCase() === 'action')))
        	{
            	return $scope.filteredTotalRecords - ($scope.activityObj.currentPage + data.alNumber) + 1 + " . " + data.userName;
        	}
            else
            {
            	return $scope.totalRecords - ($scope.activityObj.currentPage + data.alNumber) + 1 + " . " + data.userName;
            }
                        	 
                        	 
                        	 
        }),
                         /*DTColumnBuilder.newColumn(null).withTitle('Id').renderWith(function (data, type, full) {
                        	 return $scope.totalRecords - ($scope.activityObj.currentPage + data.alNumber) + 1+".";
                        	 
                         }),
                         DTColumnBuilder.newColumn("userName", "userName").withOption('name', 'userName'),*/
	                     DTColumnBuilder.newColumn("alAction", "alAction").withOption('name', 'alAction'),
	                     DTColumnBuilder.newColumn("alSpecificName", "alSpecificName").withOption('name', 'alSpecificName'),
	                     DTColumnBuilder.newColumn("alWhere", "alWhere").withOption('name', 'alWhere'),
	                     DTColumnBuilder.newColumn("aldescription", "aldescription").withOption('name', 'aldescription'),
	                     DTColumnBuilder.newColumn("alCreatedOn", "alCreatedOn").withOption('name', 'alCreatedOn')
	                    ]
    $scope.serverData = function (sSource, aoData, fnCallback) {
    	
        $scope.activityObj.currentPage = null;
        $scope.activityObj.recordsPerPage = null;
        $scope.activityObj.currentPage = aoData[3].value;
        $scope.activityObj.recordsPerPage = 150;

        if ($scope.activityObj.serviceId == 0) {
            ApiFactory.getApiData({
                serviceName: "filterbyselect",
                onSuccess: function (data) {
                    $scope.filterDetails = data.data.records;

                    $scope.customers = [];
                    $scope.groups = [];
                    $scope.sites = [];
                    $scope.devices = [];
                    
                    $.each($scope.filterDetails, function (key, value) {

                        if (value.filterType.toLowerCase() == 'customer') {
                            $scope.customers.push(value);
                        } else if (value.filterType.toLowerCase() == 'group') {
                            $scope.groups.push(value);
                        } else if (value.filterType.toLowerCase() == 'site') {
                            $scope.sites.push(value);
                        } else if (value.filterType.toLowerCase() == 'device') {
                            $scope.devices.push(value);
                        }
                    });
                    
                    $scope.customers = $rootScope.aphaNumSort($scope.customers,'name');
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'name');
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'name');
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'name');
                },
                onFailure: function (data) {
                    toastr.error(messageFactory.getMessage(data.code));
                }
            });
        }

        ApiFactory.getApiData({
            serviceName: "getActivityLogPagination",
            data: $scope.activityObj,
            onSuccess: function (data) {

                $("#nodata").css({
                    opacity: 1
                });
                $scope.dataLoaded = true;

                if (data.data && data.data.length === 0) {
                    $scope.isDataAvailable = false;
                    return;
                }

                $scope.totalRecords = data.data.totalRecords;
                $scope.logs = data.data.records;
                $scope.filteredTotalRecords = data.data.totalRecords;

                var records = {
                    'recordsTotal': $scope.totalRecords,
                    'data': $scope.logs,
                    'recordsFiltered': $scope.totalRecords
                };
                fnCallback(records);

  //                  $(".dataTables_length").addClass("line-height-40").css({opacity:1}).find("select").removeClass();
  //                  $(".dataTables_length").addClass("pull-left").insertAfter($("#activity-log-dates"));

            }
        })
    }

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withFnServerData($scope.serverData)
        .withDataProp('data')
        .withOption('serverSide', true)
        .withOption('paging', true)
        .withOption('bFilter', false)
        .withOption('info', false)
        .withOption('autoWidth',false)
        .withOption('lengthMenu',[[150], [150]])
        .withDOM('lrtip')
        .withOption('bLengthChange', false)
        .withPaginationType('simple_numbers')
        .withBootstrap()
        .withOption('fnRowCallback', function(nRow, aData, iDisplayIndex, iDisplayIndexFull) { 
        	$('td:eq(4)', nRow).html(HtmlEncode(aData.aldescription)); //only applied for 5th column and replacing the aldescription with html entities chars 
        });
    
    function HtmlEncode(s) {
        var el = document.createElement("div");
        el.innerText = el.textContent = s;
        s = el.innerHTML;
        return s;
    }

    $scope.searchLog = function (e) {
        if (e.which == 13) {
        	$scope.activityObj.filterByAction = undefined;
            $scope.activityObj.filterByModule = undefined;
        	$scope.activityObj.description = $scope.filterActivity;
        	$scope.dtInstance.rerender($scope.serverData);
        }
    }

    $scope.filterByChange = function () {
        if ($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy === '') {
            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 0;
            
            $scope.activityObj.filterByAction = undefined;
            $scope.activityObj.filterByModule = undefined;
            $scope.activityObj.description = undefined;
            
            $scope.dtInstance.rerender($scope.serverData);
            
        } else if ($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'customer') {
            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 1;

            if ($scope.activityObj.customerId) {
            	
            	$scope.activityObj.filterByAction = undefined;
                $scope.activityObj.filterByModule = undefined;
                $scope.activityObj.description = undefined;
            	
                $scope.activityObj.specificId = parseInt($scope.activityObj.customerId);
                $scope.dtInstance.rerender($scope.serverData);
            }
        } else if ($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'group') {
            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 2;

            if ($scope.activityObj.groupId) {
            	
            	$scope.activityObj.filterByAction = undefined;
                $scope.activityObj.filterByModule = undefined;
                $scope.activityObj.description = undefined;
            	
                $scope.activityObj.specificId = parseInt($scope.activityObj.groupId);
                $scope.dtInstance.rerender($scope.serverData);
            }
        } else if ($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'site') {
            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 3;

            if ($scope.activityObj.siteId) {
            	
            	$scope.activityObj.filterByAction = undefined;
                $scope.activityObj.filterByModule = undefined;
                $scope.activityObj.description = undefined;
            	
                $scope.activityObj.specificId = parseInt($scope.activityObj.siteId);
                $scope.dtInstance.rerender($scope.serverData);
            }
        } else if ($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'device') {
            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 4;

            if ($scope.activityObj.deviceId) {
            	
            	$scope.activityObj.filterByAction = undefined;
                $scope.activityObj.filterByModule = undefined;
                $scope.activityObj.description = undefined;
                
                $scope.activityObj.specificId = parseInt($scope.activityObj.deviceId);
                $scope.dtInstance.rerender($scope.serverData);
            }
        }
        else if($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'module'){

            $scope.activityObj.specificId = 0;
            $scope.activityObj.serviceId = 0;
            
            if($scope.activityObj.filterByModule && $scope.activityObj.filterByModule !=="")
            {
            	$scope.activityObj.filterByAction = undefined;
            	$scope.activityObj.description = undefined;
            	
            	$scope.dtInstance.rerender($scope.serverData);
            }
            else if($scope.activityObj.filterByModule === "")
            {
            	$scope.activityObj.filterByAction = undefined;
            	$scope.activityObj.filterByModule = undefined;
            	$scope.activityObj.description = undefined;
            	
            	$scope.dtInstance.rerender($scope.serverData);
            }
        
        	}
        else if($scope.activityObj.filterBy!=undefined && $scope.activityObj.filterBy.toLowerCase() === 'action'){
        		 $scope.activityObj.specificId = 0;
        		 $scope.activityObj.serviceId = 0;
             
             if($scope.activityObj.filterByAction && $scope.activityObj.filterByAction!=="")
             {
                 $scope.activityObj.filterByModule = undefined;
                 $scope.activityObj.description = undefined;
            	 
             	$scope.dtInstance.rerender($scope.serverData);
             }
             else if($scope.activityObj.filterByAction == "")
             {
            	 $scope.activityObj.filterByAction = undefined;
                 $scope.activityObj.filterByModule = undefined;
                 $scope.activityObj.description = undefined;
            	 
             	$scope.dtInstance.rerender($scope.serverData);
             }
        }
        else{
        	$scope.dtInstance.rerender($scope.serverData);
        }
        	
    }


    $scope.dateFilterData = function () {
        $scope.activityObj.specificId = 0;
        $scope.activityObj.serviceId = 0;
        if ($scope.startDate && $scope.endDate) {
            if ($scope.startDate) {
                $scope.activityObj.startDate = moment($scope.startDate).format("MM/DD/YYYY");
            }
            if ($scope.endDate) {
                $scope.activityObj.endDate = moment($scope.endDate).format("MM/DD/YYYY");
            }
            
            $scope.filterByChange();
            
//            $scope.dtInstance.rerender($scope.serverData);
        } else if (!$scope.startDate && !$scope.endDate) {
            $scope.activityObj.startDate = "";
            $scope.activityObj.endDate = "";
            $scope.dtInstance.rerender($scope.serverData);
        }
    }
    $scope.logActivityFilter = function (obj) {
        $scope.searchActivity = function (item) {
            var input = item.aldescription;
            if (!$scope.filterActivity || (input.toLowerCase().indexOf($scope.filterActivity.toLowerCase()) !== -1)) {
                return true;
            }
            return false;
        };
        return true;
    };
    $scope.addActivity = function () {
        $state.go("activitylog.activityLogList.addNew");
    };
}).controller("newactivityLogController", function ($scope, $element, ApiFactory, messageFactory, $rootScope, $state, toastr, $timeout, usersFactory, commonFactories) {
    $scope.open1 = function () {
        $scope.popup1.opened = true;
    };
    $scope.popup1 = {
        opened: false
    };
    $scope.hstep = 1;
    $scope.mstep = 15;
    $scope.getMeridian = true;
    $scope.activity = {};
    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];
    $scope.disableCustomer = false;
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.activity[$(this).attr("name")] = "";
        }
    });
    $scope.options = {
  		  maxDate: new Date(), // restrict maximum date to today
  		  showWeeks: true
  		};
    commonFactories.getCustomers(function (data) {
        if (data && data.length > 0) {
            $scope.customers = data;
            $scope.customers = $rootScope.aphaNumSort($scope.customers,'customerLabel');
            if (data.length === 1) {
                $scope.customerId = $scope.customers[0].id;
                $scope.disableCustomer = true;
            } else {
                $scope.disableCustomer = false;
            }
            usersFactory.getGroupsSites($scope.customerId, function (groupSiteData) {
                $scope.groups = groupSiteData.groups;
                $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                $scope.sites = groupSiteData.sites;
                $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                $scope.devices = groupSiteData.devices;
                $scope.devices = $rootScope.aphaNumSort($scope.devices,'device');
            });
        }
    });
    $scope.getGroups = function () {
        $scope.groups = [];
        if ($scope.customerId) {
            loadSitesBySelectedCustomers($scope.customerId);
        } else {
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
        }
    };
    $scope.getSites = function () {
        if ($scope.groupId) {
            $scope.sites = [];
            $scope.devices = [];
            loadSitesByCustomers($scope.groupId);
        } else {
            $scope.siteId = "";
            $scope.deviceId = "";
            $scope.groups = [];
            $scope.sites = [];
            $scope.devices = [];
            usersFactory.getGroupsSites($scope.customerId, function (groupSiteData) {
                $scope.groups = groupSiteData.groups;
                $scope.sites = groupSiteData.sites;
                $scope.devices = groupSiteData.devices;
            });
        }
    };
    $scope.getDevices = function () {
        if ($scope.siteId) {
            loadDevicesBySites($scope.siteId);
        } else {
            if ($scope.groupId) {
                $scope.sites = [];
                $scope.devices = [];
                loadSitesByCustomers($scope.groupId);
            } else {
                $scope.deviceId = "";
                $scope.groups = [];
                $scope.sites = [];
                $scope.devices = [];
                usersFactory.getGroupsSites($scope.customerId, function (groupSiteData) {
                    $scope.groups = groupSiteData.groups;
                    $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    $scope.sites = groupSiteData.sites;
                    $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    $scope.devices = groupSiteData.devices;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'deviceLable');
                });
            }
        }
    };

    function loadSitesBySelectedCustomers(customers) {
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
                        $scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
                    }
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'deviceLable');
                    }
                }
            },
            onFailure: function () {}
        });
    }

    function loadSitesByCustomers(groupsByCustomers) {
        $scope.sites = [];
        $scope.devices = [];
        ApiFactory.getApiData({
            serviceName: "getsitesbygroups",
            data: groupsByCustomers.toString() ? groupsByCustomers.toString() : "",
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    if (data.data.siteList.length > 0) {
                        $scope.sites = data.data.siteList;
                        $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                    }
                    if (data.data.deviceList.length > 0) {
                        $scope.devices = data.data.deviceList;
                        $scope.devices = $rootScope.aphaNumSort($scope.devices,'device');
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
                if (data.status.toLowerCase() === "success" && data.data.things.length > 0) {
                    $scope.devices = data.data.things;
                    $scope.devices = $rootScope.aphaNumSort($scope.devices,'device');
                }
            },
            onFailure: function () {}
        });
    }
    $scope.createActivity = function (form) {
        $scope.activity.customerId = $scope.customerId ? $scope.customerId : 0;
        $scope.activity.groupId = $scope.groupId ? $scope.groupId : 0;
        $scope.activity.siteId = $scope.siteId ? $scope.siteId : 0;
        $scope.activity.deviceId = $scope.deviceId ? $scope.deviceId : 0;
        $scope.activity.timeStamp = moment($scope.date).format("MM/DD/YYYY") + " " + moment($scope.hhmm).format("hh:mm A");
        $scope.activityData = angular.copy($scope.activity);
        delete $scope.activityData.date;
        delete $scope.activityData.hhmm;
        delete $scope.activityData.customersList;
        delete $scope.activityData.devicesList;
        delete $scope.activityData.groupList;
        delete $scope.activityData.locationsList;
        $scope.activityData.contactNumber = $rootScope.customPhonePattern($scope.activityData.contactNumber);
        
       if (!form.$valid) {
            angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
            return false;
        }
        if (form.$valid) {
        	
            ApiFactory.getApiData({
                serviceName: "addActivity",
                data: $scope.activityData,
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        toastr.success(messageFactory.getMessage(data.code));
                        skipChecking = true;
                        $state.go("activitylog.activityLogList");
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function () {}
            });
        }
    }
})
