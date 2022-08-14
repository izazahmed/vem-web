app.controller("fileUpload", function (commonFactories,$scope, $rootScope, ApiFactory,messageFactory, $state, toastr, $timeout, usersFactory, $element) {

	 $scope.uploadSubmitFormData = {};
	 $scope.imageUpload = function (event) {



	        var data = commonFactories.getImageData(event, function (data) {
	            var extenstion = data.name.substr((data.name.lastIndexOf(".") + 1), data.name.length);

	            if (extenstion && extenstion.toLowerCase() != "png") {

	                toastr.error(messageFactory.getMessage("INFO_FILE_PNG"));
	                 $(event.target).replaceWith($scope.input.clone(true));

	                return;
	            }
	            if ((data.size / (1024)).toFixed(1) < 500) {
	                $($element).find(".company-logo").attr("src", data.src);
	                $($element).find(".company-logo").attr("width", 50);

	            } else {

	                $(event.target).replaceWith($scope.input.clone(true));
	                toastr.error("Please use only below 500KB for upload");
	            }
	        });


	    }
	 $scope.uploadSubmitFormData.files = $($element).find("[type='file']");
	 
	 $scope.uploadFormData = function (form) {
        	ApiFactory.getApiData({
	        serviceName: "fileUpload",
	        data:$scope.uploadSubmitFormData,
	        onSuccess: function (data) {
	        	$scope.upload = data.data[0];
	        },
	        onFailure: function (data) {
	        	toastr.error(messageFactory.getMessage(data.code));
	        }
	    });
	 }

})