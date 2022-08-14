/** 
 *@file : dashboardController 
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
 *@date        01-11-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       01-11-2016  Nagarjuna Eerla           File Created
 */
app.controller("dashboardController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {

	if ($rootScope.pdfFlag === 'analytics') {
		$rootScope.pdfFlag = '';
		$state.go("report.reports", {
            reportType: 1,
            customer: $rootScope.pdfValue.split("-")[0],
            group: $rootScope.pdfValue.split("-")[1],
            site: $rootScope.pdfValue.split("-")[2],
            device: $rootScope.pdfValue.split("-")[3],
            time: $rootScope.pdfValue.split("-")[4],
            oldType: 1
        });
		return;
	} else if ($rootScope.pdfFlag === 'alert-critical') {
		$rootScope.pdfFlag = '';
		var dashboardFilter = "";
		var dashboardSpecifcId = 0;
		if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "site") {	
			dashboardFilter = "sites";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[2];
		} else if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "group") {
			dashboardFilter = "groups";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[1];
		} else if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "customer") {
			dashboardFilter = "customers";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[0];
		}
		$state.go("alerts.customerAlerts", $rootScope.mergeObject($state.params, {
			fromSource: 'dashboard',
			dashboardTimeInDays: 7,
			dashboardAlertStatus: 'open',
			dashboardFilter: dashboardFilter,
			priority: "1",
			dashboardSpecifcId: dashboardSpecifcId,
			dashboardAlertId: 0}));
		 return;
	} else if ($rootScope.pdfFlag === 'alert-resolved') {
		$rootScope.pdfFlag = '';
		var dashboardFilter = "";
		var dashboardSpecifcId = 0;
		if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "site") {	
			dashboardFilter = "sites";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[2];
		} else if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "group") {
			dashboardFilter = "groups";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[1];
		} else if ($rootScope.pdfValue.split("-")[3].toLowerCase() == "customer") {
			dashboardFilter = "customers";
			dashboardSpecifcId = $rootScope.pdfValue.split("-")[0];
		}
		$state.go("alerts.customerAlerts", $rootScope.mergeObject($state.params, {
			fromSource: 'dashboard',
			dashboardTimeInDays: 7,
			dashboardAlertStatus: 'resolve',
			priority: "resolve",
			dashboardFilter: dashboardFilter, 
			dashboardSpecifcId: dashboardSpecifcId,
			dashboardAlertId: 0}));
		 return;
	}  else if ($rootScope.pdfFlag === 'alert-new') {
		$rootScope.pdfFlag = '';		
		$state.go("alerts.customerAlerts", $rootScope.mergeObject($state.params, {
			fromSource: 'dashboard',
			dashboardTimeInDays: 7,
			dashboardAlertStatus: 'new',
			priority: "new",
			dashboardFilter: 'sites',
			dashboardSpecifcId: $rootScope.pdfValue.split("-")[1],
			dashboardAlertId: 0}));
		 return;
	} else if ($rootScope.pdfFlag === 'user') {
		$rootScope.pdfFlag = '';
		$state.go("users.settings.profile.edit", {userId: $rootScope.pdfValue});
		return;
	} else if ($rootScope.pdfFlag === 'dashboard') {
		$rootScope.pdfFlag = '';
		$state.go("eaidashboard", {
			customer: $rootScope.pdfValue.split("-")[0],
            group: $rootScope.pdfValue.split("-")[1],
            site: $rootScope.pdfValue.split("-")[2],
            time: $rootScope.pdfValue.split("-")[3]
        });
		return;
	} else if ($rootScope.pdfFlag === 'activity') {
		$rootScope.pdfFlag = '';
		$state.go("activitylog.activityLogList");
		return;
	}
	$scope.user = eaiDetails;
	if (!$rootScope.userDetails.rolePermissions.hasOwnProperty('Dashboard Management')) {
		$state.go("alerts.customerAlerts");
        return;
    } else if ($rootScope.userDetails.rolePermissions['Dashboard Management'] == 4) {
    	 var customerId = $rootScope.userDetails.custSiteIds.split("~")[0] ? $rootScope.userDetails.custSiteIds.split("~")[0] : 0;
    	 var siteId = $rootScope.userDetails.custSiteIds.split("~")[1] ? $rootScope.userDetails.custSiteIds.split("~")[1] : 0;
    	 if (Number($rootScope.userDetails.siteCount) > 1 || Number($rootScope.userDetails.siteCount) === 0) {
	    	 $state.go("sites.sitesList", $rootScope.mergeObject($state.params, { customerId: -1, sourcePage: 'dashboard'}));
    	 } else {
    		 if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
            	 $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, 
            			 {siteId: siteId, customerId: customerId, sourcePage: 'dashboard', singlePage: 'true'}));
             } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                 $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, 
            			 {siteId: siteId, customerId: customerId, sourcePage: 'dashboard', singlePage: 'true'}));
             } else if (!$rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                 $state.go("sites.sitesList.viewSite.forecasts", $rootScope.mergeObject($state.params, 
            			 {siteId: siteId, customerId: customerId, sourcePage: 'dashboard', singlePage: 'true'}));
             }
    	 }
        
        return;
    }
	
	$scope.getFromDate = function(inDays) {
	    var d = new Date();
	    return d.setDate(d.getDate() - inDays);
	}
	
    $scope.report = {};
	$scope.customers = [];
	$scope.groups = [];
	$scope.sites = [];
	$scope.reportOptions = [];
	$scope.mapOptions = [];
	$scope.timeOptions = [{
        name: "Last 24 Hrs",
        id : 1
    }, {
        name: "Last 7 Days",
        id : 7
    }, {
        name: "Last 28 Days",
        id : 28
    }];
    $scope.report.time = $scope.timeOptions[1].id;
    
	$.each($($element).find("form").find("input,select,textarea"), function() {		
		if ($(this).attr("name")) {		
			$scope.report[$(this).attr("name")] = "";		
		}		
	});
	commonFactories.getCustomers(function (data) {
        if (data && data.length > 0) {
        	$scope.customers = data;
        	$scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
            $scope.companyLogo = $state.params.customer > 0 ? $scope.isExists(Number($state.params.customer)).companyLogo : $scope.customers[0].companyLogo;
            $state.go('.', {
                customer: $scope.report.customer ? $scope.report.customer : 0
            }, {
                notify: false
            });
            usersFactory.getGroupsSites($scope.report.customer,function (groupSiteData) {
            	$scope.groups = groupSiteData.groups;
            	$scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
            	$scope.sites = groupSiteData.sites;
            	$scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
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
                                loadDashBoardCharts();
                            }
                        },
                        onFailure: function () {}
                    });
                } else {
                	loadDashBoardCharts();
                }
            });
        }
    });
	$scope.getGroups = function () {
		$state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
		if ($scope.report.customer) {
			$scope.companyLogo = $scope.isExists($scope.report.customer).companyLogo;
            loadGroupsSites($scope.report.customer);
		} else {
			$scope.groups = [];
			$scope.sites = [];
		}
	};
	$scope.isExists = function(id) {
		for (var i = 0; i < $scope.customers.length; i++) {
			if (Number(id) === Number($scope.customers[i].id)) {
				return $scope.customers[i]
			}
		}
	}
	$scope.getSites = function () {
		$state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
		if ($scope.report.group) {
			loadSites($scope.report.group);
		} else {
			$scope.report.site = "";
			$state.go('.', {
	            customer: $scope.report.customer ? $scope.report.customer : 0,
	            group: $scope.report.group ? $scope.report.group : 0,
	            site: $scope.report.site ? $scope.report.site : 0,
	            time: $scope.report.time
	        }, {
	            notify: false
	        });
			$scope.groups = [];
			$scope.sites = [];
			usersFactory.getGroupsSites($scope.report.customer, function (groupSiteData) {
            	$scope.groups = groupSiteData.groups;
            	$scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
            	$scope.sites = groupSiteData.sites;
            	$scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
            	loadDashBoardCharts();
            });
		}
	};
	function loadGroupsSites(customers) {
		$scope.groups = [];
		$scope.sites = [];
        ApiFactory.getApiData({
            serviceName: "getcustomergroups",
            data: {customerIds: customers.toString() ? customers.toString() : ""},
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
                    loadDashBoardCharts();
                }
            },
            onFailure: function () {}
        });
    }
	function loadSites(groupsByCustomers) {
		$scope.sites = [];
         ApiFactory.getApiData({
             serviceName: "getsitesbygroups",
             data: groupsByCustomers.toString() ? groupsByCustomers.toString() : "",
             onSuccess: function (data) {
                 if (data.status.toLowerCase() === "success") {
                	 if (data.data.siteList.length > 0) {
                		 $scope.sites = data.data.siteList;
                		 $scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
                	 }
                	 loadDashBoardCharts();
                 }
             },
             onFailure: function () {}
         });
     }
	// This function is to load data when user changes the site or date means it by site
	$scope.loadChart = function(){
		$state.go('.', {
            customer: $scope.report.customer ? $scope.report.customer : 0,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            time: $scope.report.time
        }, {
            notify: false
        });
		 $timeout(function () {
			 loadDashBoardCharts();
	     }, 100);
	}
	function loadDashBoardCharts () {
		if ($scope.customers.length > 0) {
            $scope.report.customer = $state.params.customer > 0 ? Number($state.params.customer) : $scope.customers[0].id;
        }
        if ($scope.reportOptions.length > 0) {
            $scope.report.reportType = $state.params.reportType > 0 ? Number($state.params.reportType) : Number($scope.reportOptions[0].id);
        }
        if ($scope.groups.length > 0) {
            $scope.report.group = Number($state.params.group) ? Number($state.params.group) : Number($scope.report.group);
        }
        if ($scope.sites.length > 0) {
            $scope.report.site = Number($state.params.site) ? Number($state.params.site) : Number($scope.report.site);
        }
    	$scope.noIssuesFlag = false;
		$scope.reportOptions = [];
		$scope.mapOptions = [];
		$scope.alertsdata = [];
		var type = "customer"
		if (!$scope.report.group && !$scope.report.site){	
			type = "customer";
		} else if ($scope.report.group && !$scope.report.site) {
			type = "group";
		} else if ((!$scope.report.group && $scope.report.site) || ($scope.report.site && !$scope.report.group) || ($scope.report.site && $scope.report.group)) {
			type = "site";
		}
		var apiData = {
            type: type.toUpperCase(),
            customerIds: $scope.report.customer,
            groupIds: $scope.report.group,
            siteIds: $scope.report.site,
            inDays: $scope.report.time,
            fromDate:moment($scope.getFromDate($scope.report.time)).format("YYYY-MM-DD HH:mm"),
            toDate:moment(new Date()).format("YYYY-MM-DD HH:mm")
        };
        ApiFactory.getApiData({
            /**
             *Requesting API for All dashboard information
             */
            serviceName: "getdashboarddata",
            data: apiData,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                	$.each(data.data.reports, function (key, value) {
                		if (value.id === 1) {
                			value["title"] = "Percent of HVAC units which are unable to reach setpoint";
                        } else if (value.id === 2) {
							value["title"] = "Percent of HVAC units which are running within setpoint (comfortable)";
						} else if (value.id === 3) {
							value["title"] = "Total number of hours in which the HVAC units are cooling or heating";
						} else if (value.id === 4) {
							value["title"] = "Percent of thermostats in which setpoint adjustments were made";
						}
                    });
                	$scope.reportOptions = data.data.reports;
                	$scope.mapOptions = data.data.mapData;
                	$scope.alertsdata = data.data.alerts;
                	if ($scope.alertsdata[0].data[0].y === 0 && $scope.alertsdata[1].data[0].y === 0 && $scope.alertsdata[2].data[0].y === 0) {
                		$scope.noIssuesFlag = true;
                	}
                	loadMap();
                }
            },
            onFailure: function () {}
        });
	}
	$scope.criticalFn = function(data) {
		var dashboardFilter;
		var dashboardSpecifcId;
		if ($scope.report.site){	
			dashboardFilter = "sites";
			dashboardSpecifcId = $scope.report.site; 
		} else if ($scope.report.group) {
			dashboardFilter = "groups";
			dashboardSpecifcId = $scope.report.group;
		} else if ($scope.report.customer) {
			dashboardFilter = "customers";
			dashboardSpecifcId = $scope.report.customer;
		}
		 $state.go("alerts.customerAlerts", $rootScope.mergeObject($state.params, {
			fromSource: 'dashboard',
			dashboardTimeInDays: 0,
			dashboardAlertStatus: 'open',
			dashboardFilter: dashboardFilter, 
			dashboardSpecifcId: dashboardSpecifcId,
			dashboardAlertId: data.id}));
	}
    $scope.ctrlFn = function (apiReport, solidChartData) {
    	if(((solidChartData.reportId === 1|| solidChartData.reportId === 2 || solidChartData.reportId === 4) && (solidChartData.y === 0))
    			|| ((solidChartData.reportId === 3) && (solidChartData.y === 0 && solidChartData.extraValue === 0))){
    		return;
    	}
    	
        $state.go("report.reports", {
            reportType: apiReport.id,
            customer: $scope.report.customer,
            group: $scope.report.group ? $scope.report.group : 0,
            site: $scope.report.site ? $scope.report.site : 0,
            device: $scope.report.device ? $scope.report.device : 0,
            time: $scope.report.time,
            oldType: apiReport.id
        }, {
        	 reload: true
        });
        return;
    }
    function loadMap() {
    	Highcharts.data({
	       googleSpreadsheetKey: '0AoIaUO7wH1HwdDFXSlpjN2J4aGg5MkVHWVhsYmtyVWc',

	        // custom handler for columns
	        parsed: function (columns) {
	            function pointClick() {
	            	if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
	            		$state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {siteId: this.siteId, customerId: this.customerId, sourcePage: 'sites-'+this.siteId}));
	            	}
	            }
	               var options = {
	                    chart: {
	                        renderTo: 'container',
	                        type: 'map',
	                        marginRight: 50
	                    },
	                    exporting : {
		            	        enabled: false,
		            	        buttons: {
		            	            exportButton: {
		            	                enabled: false
		            	            },
		            	            printButton: {
		            	                enabled: false
		            	            }
		            	        }
	                    },
	                    title: {
	                        text: ""
	                    },
	                    mapNavigation: {
	                        enabled: true,
	                        buttonOptions: {
	                            enabled: false
	                        }
	                    },
	                    legend: {
	                        align: 'right',
	   		             	verticalAlign: 'bottom',
	   		             	layout: 'vertical'
	                    },
	                    credits: {
	                        enabled: false
	                    },
	                    tooltip: {
	                        headerFormat: '',
	                        pointFormat: '<b>{point.address}</b>'
	                    },
	                    series: [{
	                        // Use the gb-all map with no data as a basemap
	                        mapData: Highcharts.maps['countries/us/us-all'],
	                        name: 'Basemap',
	                        borderColor: '#A0A0A0',
	                        nullColor: '#2A67A5',
	                        showInLegend: false
	                    }, {
	                        name: 'Separators',
	                        type: 'mapline',
	                        data: Highcharts.geojson(Highcharts.maps['countries/us/us-all'], 'mapline'),
	                        color: 'silver',
	                        showInLegend: false,
	                        enableMouseTracking: false,
	                        cursor: 'pointer'
	                    }, /*{
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[0].label,
	                        color: '#C4BEBE',
	                        marker: {
	                            symbol: "diamond"
	                        },
	                        data: $scope.mapOptions[0].value,
	                         point: {
		                            events: {
		                                click: pointClick
		                            }
		                        }
	                    },*/ {
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[1].label,
	                        color: "#F90602",
	                        marker: {
	                            symbol: "diamond"
	                        },
	                        data: $scope.mapOptions[1].value,
	                        point: {
	                            events: {
	                                click: pointClick
	                            }
	                        },
	                        cursor: 'pointer'
	                    }, {
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[2].label,
	                        color: "#08C965",
	                        marker: {
	                            symbol: "diamond"
	                        },
	                        data: $scope.mapOptions[2].value,
	            	        point: {
	                            events: {
	                                click: pointClick
	                            }
	                        },
	                        cursor: 'pointer'
	                    }, {
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[3].label,
	                        color: "#C4BEBE",
	                        marker: {
	                            symbol: "circle"
	                        },
	                        data: $scope.mapOptions[3].value,
            	        	point: {
	                            events: {
	                                click: pointClick
	                            }
	                        },
	                        cursor: 'pointer'
	                    }, {
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[4].label,
	                        color: "#F90602",
	                        marker: {
	                            symbol: "circle"
	                        },
	                        data: $scope.mapOptions[4].value,
	            	        point: {
	                            events: {
	                                click: pointClick
	                            }
	                        },
	                        cursor: 'pointer'
	                    }, {
	                        // Specify points using lat/lon
	                        type: 'mappoint',
	                        name: $scope.mapOptions[5].label,
	                        color: "#08C965",
	                        marker: {
	                            symbol: "circle"
	                        },
	                        data: $scope.mapOptions[5].value,
	                        point: {
	                            events: {
	                                click: pointClick
	                            }
	                        },
	                        cursor: 'pointer'
	                    }]
	                };
	            window.chart = new Highcharts.Map(options);
	        },
	        error: function () {}
	    });
    }
})