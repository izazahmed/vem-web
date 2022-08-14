angular.module('EAI').controller("bulkImportMainController",function($scope){
    
    
}).controller("bulkImportSiteController",function($scope,toastr,ApiFactory,messageFactory,$timeout,$state,cfpLoadingBar,$rootScope){

	$scope.makeParseInt = function(value){
		return parseInt(value)
	}
	
	function loadData(){
		if ($state.current.name === 'bulkImport.sites') {
			 ApiFactory.getApiData({
		         serviceName: "getUploadStatus",
		         onSuccess: function (data) {
		             if (data.data.length > 0) {
		            	 $scope.records = data.data;
		            	 cfpLoadingBar.complete();
		             }
		             $scope.records = data.data;
		             $timeout(function(){
		            	 loadData();
		             },1000);
		         },
		         onFailure: function () {
		        	 $scope.records = [];
		        	 $timeout(function(){
		            	 loadData();
		             },1000);
		         }
		     });
		}	
	}
	
	$scope.deleteSiteUpload = function (record) {
        $('#modelDialog').on('show.bs.modal', function (event) {
            var modal = $(this)
            $(this).find(".confirm")[0].data = record
            modal.find('.modal-title').text('Delete Bulk Import Result File');
            modal.find('.model-content').text('Are you sure you want to delete this file?')
        });
    }
	
    $rootScope.accept = function ($event) {
        ApiFactory.getApiData({
            serviceName: "deleteSiteUpload",
            data: {
                "bulkUploadProgressId": $event.currentTarget.data.bulkUploadProgressId,
                "fileName": $event.currentTarget.data.bulkUploadSheet
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    toastr.success(messageFactory.getMessage("INFO_DELETE_SITE_UPLOAD_SUCCESS_2001"));
                    $("#modelDialog").modal("hide");
                } else {
                    toastr.error(messageFactory.getMessage(data.code));
                    $("#modelDialog").modal("hide");
                }
            },
            onFailure: function () {
            	toastr.error("Failed");
            }
        });
    }
    
	$scope.getFileName = function(fileName){		
		return fileName.substr(0,fileName.lastIndexOf("."));
	}
	
	cfpLoadingBar.start();
	loadData();
    
})