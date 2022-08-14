/** 
 *@file : usersFactory 
 *
 *@usersFactory :Load reusable methods for user module
 *
 *@author :(Nagaraju SVP Goli - ngoli@ctepl.com) 
 *
 *@Contact :(Umang - ugupta@ctepl.com)
 * 
 *@Contact : (Chenna - yreddy@ctepl.com)
 *
 *@version     VEM2-1.0
 *@date        29-08-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       29-08-2016  Nagaraju SVP Goli           File Created
 * 02       29-08-2016  Nagaraju SVP Goli           Added getRoles method
 * 03       29-08-2016  Nagaraju SVP Goli           Added getUniqueArray method
 *
 */
app.factory('usersFactory', function (ApiFactory) {
    return {
        getRoles: function (callback) {
            ApiFactory.getApiData({
                serviceName: "listroles",
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        var roles = [];
                        if (data.data.roleList && data.data.roleList.length > 0) {
                            $.each(data.data.roleList, function (key, value) {
                                roles.push({
                                    value: value.roleId,
                                    label: value.roleName,
                                    isCSO: value.customerSupport,
                                    superAdmin:value.superAdmin,
                                    isActive:value.isActive
                                    
                                });
                            });
                        }
                        callback(roles);
                    }
                },
                onFailure: function () {}
            });
        },
        getCustomers: function (callback) {
            ApiFactory.getApiData({
                serviceName: "customersList",
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        var customers = [];
                        if (data.data && data.data.length > 0) {
                        	var loggedInUserCustomers = eaiDetails.customers;
                    		var customersArray = loggedInUserCustomers.split(',');
                            $.each(data.data, function (key, value) {
                            	if(eaiDetails.isSuper == 1){
                            		// Pushing only active customers
                                	if(value.isActive == 1)
                                	customers.push({
                                        value: value.customerId,
                                        label: value.companyName
                                    });
                            	}else{
                            		// Pushing only active customers
                                	if(value.isActive == 1)
                                		if(customersArray.indexOf(value.customerId) !== -1)
                                		customers.push({
                                            value: value.customerId,
                                            label: value.companyName
                                        });
                            	}
                            });
                        }
                        callback(customers);
                    }
                },
                onFailure: function () {}
            });
        },
        getGroupSites: function (callback) {
            ApiFactory.getApiData({
                serviceName: "listsite",
                data: {
                	fromPage:"sites",
                	id:"-1"
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                    	var groupSites = "";
                        if (data.data) {
                        	groupSites = data.data
                        }
                        callback(groupSites);
                    }
                },
                onFailure: function () {}
            });
        },
        getUniqueArray: function (arr, prop) {
            var new_arr = [];
            var lookup = {};
            var arr = angular.copy(arr);
            for (var i in arr) {
                lookup[arr[i][prop]] = arr[i];
            }
            for (i in lookup) {
                new_arr.push(lookup[i]);
            }
            return new_arr;
        },
        getGroupsSites: function (customerId, callback){
        	// API call to get groups by selected customers
            ApiFactory.getApiData({
                serviceName: "getcustomergroups",
                data: {
                    customerIds: customerId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        var groupSites = {
                            groups: data.data.groupList,
                            sites: data.data.siteList,
                            devices: data.data.deviceList
                        };
                        callback(groupSites);
                    }
                },
                onFailure: function () {}
            });
                            
        },
        getSites: function (groupIds, callback){
        	var selectedGroups = [];
        	var sites = [];
             $.each(groupIds, function (key, value) {
            	 selectedGroups.push(value.id);
             });
            // API call to get groups by selected customers
            ApiFactory.getApiData({
                serviceName: "getsitesbygroups",
                data: selectedGroups.toString() ? selectedGroups.toString() : "",
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        // pushing values to groups
                        if (data.data.siteList.length > 0) {
                        	
                        	var map = {};
                        	
                        	$.each(data.data.siteList, function (key, value) {
                                map[value.siteId] =  value.siteName;
                                  
                                
                            });
                        	
                        	
                        	$.each(map, function (key, value) {
                                sites.push({
                                    id: key,
                                    name: value
                                });
                                
                            });
                        }
                        callback(sites);
                    }
                },
                onFailure: function (data) {}
            });
        }
    }
})