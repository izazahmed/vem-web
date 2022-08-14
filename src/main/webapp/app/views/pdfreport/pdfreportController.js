/** 
 *@file : pdfreportController 
 * 
 *@pdfreportController :Load pdf report module 
 * 
 *@author :(Bhoomika Rabadiya - brabadiya@ctepl.com) 
 * 
 *@Contact :(Umang - ugupta@ctepl.com) 
 * 
 *@Contact : (Chenna - yreddy@ctepl.com) 
 *
 *@version     VEM2-1.0
 *@date        10-02-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       10-02-2016  Bhoomika Rabadiya           File Created
 */
app.controller("reportListController", function ($scope, $rootScope, ApiFactory, messageFactory, $element, $state, toastr, $timeout, commonFactories, usersFactory) {
    $scope.report = {};
    $scope.customers = [];
	$scope.groups = [];
	$scope.sites = [];
	$scope.reportList = [];
	$scope.reportObject = {};
	$scope.reportObject.emails = [];
	$scope.isDataAvailable = true;
	
	$scope.open1 = function() {
		$scope.popup1.opened = true;
	};	
	$scope.open2 = function() {
		$scope.popup2.opened = true;
	};

	$scope.popup1 = {
			opened: false
	};

	$scope.popup2 = {
			opened: false
	};
	pdfReportList();
	function pdfReportList() {
		$scope.showMain = false;
		ApiFactory.getApiData({
	        serviceName: "getreportlist",
	        onSuccess: function (data) {
	            if (data.status.toLowerCase() === "success") {
	            	
	            	if (data.data && data.data.length === 0) {
	                    $scope.isDataAvailable = false;
	                    return;
	                }
	            	$scope.customers = [];
	            	$scope.groups = [];
	            	$scope.sites = [];
	            	$scope.reportList = [];
	            	$scope.reportList = data.data.reportList;

	        		var map = {};
	        		var mapList = [];
	        		$.each($scope.reportList,function(key, value){
	        			var newString = value.mailFromDate.replace(/[^A-Z0-9]+/ig, "");
	        			
	            		var keyMap = value.reportLevel + "-" + newString + "-" + value.specificIds;
	            		if (map[keyMap]) {
	            			var mapListTemp1 = map[keyMap];
	            			mapListTemp1.push(value)
	            			map[keyMap] = mapListTemp1; 
	            		} else {
	            			var mapListTemp2 = [];
	            			mapListTemp2.push(value)
	            			map[keyMap] = mapListTemp2;
	            		}
	            		
	            	});
	        		$.each(map,function(key, value) {
	        			var fullName = [];
	        			var userEmail = [];
	        			var userIds = [];
	        			var mainIds = [];
	        			var fileName, specificIds, reportDuration, reportName, specificNamesArray, reportLevel;
	        			var comfortOpt, reportLevelText, id, reportPDFName, reportPreferenceText, reportPreferenceText;
	        			var specificNames, mailFromDate, mailToDate, companyLogo, toDate, fromDate, reportStatus, resendHeader, specificNamesMail;
	        			var tempId;
	        			$.each(value,function(key, value) {
	        				fullName.push(value.userFName + " " + value.userLName);
	        				userEmail.push(value.userEmail);
	        				userIds.push(value.userId);
	        				mainIds.push(value.id);
	        				tempId = value.id;
	        				fileName = value.fileName;
	        				specificIds = value.specificIds;
	        				reportDuration = value.reportDuration;
	        				reportName = value.reportName;
	        				specificNamesArray = value.specificNamesArray;
	        				reportLevel = value.reportLevel;
	        				comfortOpt = value.comfortOpt;
	        				reportLevelText = value.reportLevelText;
	        				id = mainIds;
	        				reportPDFName = value.reportPDFName;
	        				reportPreferenceText = value.reportPreferenceText;
	        				specificNames = value.specificNames;
	        				mailFromDate = value.mailFromDate;
	        				mailToDate = value.mailToDate;
	        				companyLogo = value.companyLogo;
	        				toDate = value.toDate;
	        				fromDate = value.fromDate;
	        				reportStatus = value.reportStatus;
	        				resendHeader = value.resendHeader;
	        				specificNamesMail = value.specificNamesMail;
	        	    	});
	        			mapList.push({
	        				"fileName": fileName,
	        				"specificIds": specificIds,
	        				"reportDuration": reportDuration,
	        				"reportName": reportName,
	        				"specificNamesArray": specificNamesArray,
	        				"reportLevel": reportLevel,
	        				"fullName": fullName.unique().join(", "),
	        				"comfortOpt": comfortOpt,
	        				"reportLevelText": reportLevelText,
	        				"userEmail": userEmail.unique().join(", "),
	        				"id": mainIds.unique().join(", "),
	        				"reportPDFName": reportPDFName,
	        				"reportPreferenceText": reportPreferenceText,
	        				"specificNames": specificNames,
	        				"mailFromDate": mailFromDate,
	        				"mailToDate": mailToDate,
	        				"companyLogo": companyLogo,
	        				"toDate": toDate,
	        				"reportStatus": reportStatus,
	        				"userId": userIds.unique().join(", "),
	        				"fromDate": fromDate,
	        				"resendHeader": resendHeader,
	        				"specificNamesMail": specificNamesMail,
	        				"tempId":tempId
	        			})
	            	});
	        	
	        		$scope.reportList = mapList;
	        	
	        		
	            	$scope.mainReportList = angular.copy(data.data.reportList);
	            	$.each(data.data.filters.customerList.unique(),function(key, value){
	            		$scope.customers.push({
	            			customerName: value,
	            			customerId: value
                         });
	            	});
	            	$scope.customers = $rootScope.aphaNumSort($scope.customers,'customername');
	            	$.each(data.data.filters.groupList.unique(),function(key, value){
	            		$scope.groups.push({
	            			 groupName: value,
                			 groupId: value
                         });
	            	});
	            	$scope.groups = $rootScope.aphaNumSort($scope.groups,'group');
	            	$.each(data.data.filters.siteList.unique(),function(key, value){
	            		$scope.sites.push({
                			 siteName: value,
                			 siteId: value
                         });
	            	});
	            	$scope.sites = $rootScope.aphaNumSort($scope.sites,'site');
	            }
	        },
	        onFailure: function () {}
	    });
		
		Array.prototype.contains = function(v) {
		    for(var i = 0; i < this.length; i++) {
		        if(this[i] === v) return true;
		    }
		    return false;
		};

		Array.prototype.unique = function() {
		    var arr = [];
		    for(var i = 0; i < this.length; i++) {
		        if(!arr.contains(this[i])) {
		            arr.push(this[i]);
		        }
		    }
		    return arr; 
		}
		$scope.showPlus = false;
		$scope.checkAll = function () {
			$scope.showMain = $scope.showMain == true ? false : true;
			$scope.showPlus = $scope.showPlus == true ? false : true;
			$scope.reportList = angular.copy($scope.reportList);
		};
	}
	$scope.reportFilter = function (obj) {
    	switch ($scope.report.filterBy && $scope.report.filterBy.toLowerCase()) {
    		case "reportlevel":
    			return obj.reportLevel === Number($scope.report.reportLevel)    			
    		/*case "date":
    			return $scope.filterDate(obj.fromDate, obj.toDate);*/
	        case "customer":
	        	return $scope.isExists(obj.specificNamesArray, $scope.report.customer) && obj.reportLevel === 1
	        case "group":
	        	return $scope.isExists(obj.specificNamesArray, $scope.report.group) && obj.reportLevel === 2
	        case "site":
	        	return $scope.isExists(obj.specificNamesArray, $scope.report.site) && obj.reportLevel === 3
		}
    	$scope.isExists = function (data, text) {
             var there = false;
             for (var i = 0; i < data.length; i++) {
                 if (data[i].trim() === text.trim()) {
                     there = true;
                 }
             }
             return there;
        }
    	
    	$scope.filterDate = function () {
    		$scope.reportList = angular.copy($scope.mainReportList);
    		$scope.reportList = $scope.reportList.filter(function (value) {
    			var start = value.fromDate;
        		var end = value.toDate;
        		var there = false;
        		if ($scope.startDate && $scope.endDate) {
        			var sytemStartDate = moment(start).format("YYYY-MM-DD");
        		    var enterStartDate = moment($scope.startDate).format("YYYY-MM-DD");
        		    
        			var sytemToDate = moment(end).format("YYYY-MM-DD");
        		    var enterToDate = moment($scope.endDate).format("YYYY-MM-DD");
        		    there = moment(sytemStartDate).isSameOrAfter(enterStartDate) && moment(sytemToDate).isSameOrBefore(enterToDate);
        		} else {
        			$scope.reportList = angular.copy($scope.mainReportList);
        			there = true;
        		}
                return there;
    		});
       };
    	$scope.searchReport = function (item) {
             var input = item.reportName;
             if (!$scope.reportActivity || (input.toLowerCase().indexOf($scope.reportActivity.toLowerCase()) !== -1)) {
                 return true;
             }
             return false;
        };
        $scope.filterByChange = function () {
            switch ($scope.report.filterBy.toLowerCase()) {
                case "customer":
                    $scope.report.customer = $scope.customers.length > 0 ? $scope.customers[0].customerName.toString() : "";
                    break;
                case "group":
                    $scope.report.group = $scope.groups.length > 0 ? $scope.groups[0].groupName.toString() : "";
                    break;
                case "site":
                    $scope.report.site = $scope.sites.length > 0 ? $scope.sites[0].siteName.toString() : "";
                    break;
            }
        };
	    return true;
	};
	$scope.downloadPDF = function (report) {
		window.location.href = "fileUpload/getDownloadPDF?fileName="+ report.fileName + "&reportName=" + encodeURIComponent(report.reportPDFName);
	};
	$scope.hideDiv = function (index) {
		if (document.getElementById("div"+index).style.display == 'none') { 
	        $("#email"+index).val("");
	        document.getElementById("div"+index).style.display = 'block';
		} else {
			$("#div"+index).hide();
		}
	}
	$scope.resendPDF = function (form, index, reportData) {
		if (form.$valid) {
			ApiFactory.getApiData({
		        serviceName: "resendPDF",
		        data: {reportId: reportData.tempId, email: encodeURI($scope.reportObject.emails[index].email)} ,
	            onSuccess: function (data) {
		            if (data.status.toLowerCase() === "success") {
		            	$("#email"+index).val("");
		            	document.getElementById("div"+index).style.display = 'none';
		            	toastr.success(messageFactory.getMessage(data.code));
		            } else {
		            	toastr.error(messageFactory.getMessage(data.code));
		            }
		        },
		        onFailure: function () {}
		    });
		} else {
			return false;
		}
	};
	
	$scope.reportRelatedData = {};
	
	$rootScope.accept = function (event) {
		switch(event.target.action){
			case 'delete': 
				ApiFactory.getApiData({
			        serviceName: "deletePDF",
			        data: {reportId: reportRelatedData.id} ,
		            onSuccess: function (data) {
			            if (data.status.toLowerCase() === "success") {
			            	$("#modelDialog").modal("hide");
                            skipChecking = true;
			            	toastr.success(messageFactory.getMessage(data.code));
			            	pdfReportList();
			            } else {
			            	toastr.error(messageFactory.getMessage(data.code));
			            }
			        },
			        onFailure: function () {}
			    });
			
				break;
		}
	}
	
	$scope.deletePDF = function (reportData) {
		
		reportRelatedData = reportData;
		
		$('#modelDialog').on('show.bs.modal', function (event) {
            var modal = $(this)
            modal.find('.modal-title').text('Delete report');
            modal.find('.model-content').text('Are you sure you want to delete this report?')
            $(this).off('show.bs.modal');
            $(this).find(".confirm")[0].action = 'delete';
            $(this).find(".cancel")[0].cancelValue = null;
        });
		
		/*var result = confirm("Are you sure you want to delete the '"+reportData.reportName+"' report?");
		
		if(result)
		{}*/
	};
});