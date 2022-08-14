/** 
 *@file : commonController 
 *
 *@messageFactory :Load header and Left menu bar for application
 *
 *@author :(Nagaraju SVP Goli - ngoli@ctepl.com) 
 *
 *@Contact :(Umang - ugupta@ctepl.com)
 * 
 *@Contact : (Chenna - yreddy@ctepl.com)
 *
 *@version     VEM2-1.0
 *@date        17-08-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       17-08-2016  Nagaraju SVP Goli           File Created
 * 02       22-08-2016  Nagaraju SVP Goli           Added messages Object
 * 03       02-11-2016  Nagarjuna Eerla   	        Added user module permissions for left menu
 *
 */
app.controller("header", function ($scope, $rootScope) {
    $rootScope.accept = $scope.accept; // Attaching click event to $rootScope
    $rootScope.cancel = $scope.cancel; // Attaching click event to $rootScope
}).controller("leftMenu", function ($scope, $rootScope, ApiFactory, $interval, $element, $timeout) {
    if ($rootScope.activeELement) {
        $scope.activeELement = $rootScope.activeELement;
    }
    
    /*
    	ApiFactory.getApiData({
            serviceName: "storeTimeZone",
            data:$rootScope.userDetails.timeZone,
            onSuccess: function (data) {
            	
            },
            onFailure: function () {}
        });
    
    */
    
    
    var prevCount = $($element).find(".new-alert-count").text();
    function loadAlertCount(){
    	
    	ApiFactory.getApiData({
            serviceName: "getnewalertcount",
            onSuccess: function (data) {
            	if(prevCount !== data.data){
            		$rootScope.newAlertCount = data.data;
            	}
            },
            onFailure: function () {}
        });
    }
    $timeout(loadAlertCount, 8000)
    $interval(loadAlertCount, 300000);
})