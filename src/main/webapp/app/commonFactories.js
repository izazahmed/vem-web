app.factory("commonFactories", function (ApiFactory) {

    return {
        getImageData: function (evt, callback) {
            var files = evt.target.files; //FileList object

            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var reader = new FileReader();
                reader.onload = function (e) {
                    file.src = e.target.result
                    callback(file);
                };
                reader.readAsDataURL(file);
            }

        },
        getCustomers: function (callback) {
            ApiFactory.getApiData({
                serviceName: "getCustomersByUser",
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        var customers = [];
                        $.each(data.data, function (key, value) {
                            customers.push({
                                id: Number(value.customerId),
                                label: value.companyName,
                                companyLogo: value.companyLogo
                            });
                        })
                        callback(customers);
                    }
                },
                onFailure: function () {}
            });
        },
        makeAlphabetsGroup: function (obj, sortKey) {

            function isAlphaOrParen(str) {
                return /^[a-zA-Z()]+$/.test(str);
            }
            sortKey = sortKey.toString();

            var temp = {};

            $.each(obj, function (key, value) {
                if (!temp[value[sortKey].charAt(0).toUpperCase()]) {

                    if (isAlphaOrParen(value[sortKey].charAt(0).toUpperCase())) {
                        temp[value[sortKey].charAt(0).toUpperCase()] = [];
                    }
                }

                if (!temp["#"]) {
                    if (!isAlphaOrParen(value[sortKey].charAt(0).toUpperCase())) {
                        temp["#"] = [];
                    }
                }

                if (isAlphaOrParen(value[sortKey].charAt(0))) {
                    temp[value[sortKey].charAt(0).toUpperCase()].push(value);
                } else {
                    temp["#"].push(value);
                }
            });

            var sortableArray = Object.keys(temp).sort();
            var sortableObject = {};
            /*if (sortableArray[0] == "#") {
                sortableArray.push(sortableArray.shift());
            }*/
            sortableArray.filter(function (element) {
                sortableObject[element] = temp[element];
            });

            return sortableObject;
        },
        fromPageSites: function (name, params) {
            var listData = {};
            if (name.indexOf("sites.sitesList") >= 0) {
                listData.fromPage = "sites";
                listData.id = params.customerId;
                listData.siteId = params.siteId;
            } else if (name.indexOf("customers.customersList.getCustomer.sites") >= 0) {
                listData.fromPage = "customers";
                listData.id = params.customerId;
                listData.siteId = params.siteId;
            }
            /*else if (name.indexOf("customers.customersList.getCustomer.groups.groupInfo.sites")) {
               			listData.fromPage = "groups";
               			listData.id = params.groupId;
               			listData.siteId = params.siteId;
               		}*/
            else if (name.indexOf("groups.groupList.groupInfo.sites") >= 0 || name.indexOf("users.usersList.userProfile.groups.sites") >= 0 || name.indexOf("customers.customersList.getCustomer.groups.groupInfo.sites") >= 0) {
                listData.fromPage = "groups";
                listData.id = params.groupId;
                listData.siteId = params.siteId;
            } else if (name.indexOf("scheduler.schedulerList.getSchedule.groups.groupInfo.sites") >= 0) {
                listData.fromPage = "groups";
                listData.id = params.groupId;
                listData.siteId = params.siteId;
                listData.scheduleId = params.scheduleId;
            } else if (name.indexOf("scheduler.schedulerList.getSchedule.sites") >= 0) {
                listData.fromPage = "schedules";
                listData.id = params.scheduleId;
                listData.siteId = params.siteId;
            } else if (name.indexOf("users.usersList.userProfile.sites") >= 0) {
                listData.fromPage = "users";
                listData.id = params.userId;
                listData.siteId = params.siteId;
            }
            /*else if (name.indexOf("users.usersList.userProfile.groups.sites")) {
              			listData.fromPage = "groups";
              			listData.id = params.groupId;
              			listData.siteId = params.siteId;
              		}*/
            return listData;
        },
        fromPageGroups: function (name, params) {
            var listData = {};
            if (name.indexOf("customers.customersList.getCustomer.groups") >= 0) {
                listData.moduleName = "customers";
                //listData.customerId = params.customerId;
                listData.moduleId = params.customerId;
            } else if (name.indexOf("groups.groupList") >= 0) {
                listData.moduleName = "groups";
                //listData.customerId = params.customerId;
                listData.moduleId = params.customerId;
            } else if (name.indexOf("scheduler.schedulerList.getSchedule.groups") >= 0) {
                listData.moduleName = "schedules";
                listData.moduleId = params.scheduleId;
            } else if (name.indexOf("users.usersList.userProfile.groups") >= 0) {
                listData.moduleName = "users";
                listData.moduleId = params.userId;
            }
            return listData;
        },

        fromPageAlerts: function (name, params, alertStatus) {

            alertStatus = (alertStatus == '' || alertStatus == undefined) ? 'open' : alertStatus;

            var listData = {};
            if (name.indexOf("devices.devicesList.profile.alerts") >= 0) {
                listData = {
                    alertStatus: alertStatus,
                    fromCurrentPage: 'devices',
                    specificId: params.deviceId
                };
            } else if (name.indexOf("customers.customersList.getCustomer.alerts") >= 0) {
                listData = {
                    alertStatus: alertStatus,
                    fromCurrentPage: 'customers',
                    specificId: params.customerId
                };
            } else if (name.indexOf("sites.sitesList.viewSite.alerts") >= 0) {
                listData = {
                    alertStatus: alertStatus,
                    fromCurrentPage: 'sites',
                    specificId: params.siteId
                };
            } else if (name.indexOf("groups.groupList.groupInfo.alerts") >= 0) {
                listData = {
                    alertStatus: alertStatus,
                    fromCurrentPage: 'groups',
                    specificId: params.groupId
                };
            } else if (name.indexOf("alerts.customerAlerts") >= 0) {
                listData = {
                    alertStatus: alertStatus,
                    fromCurrentPage: 'alerts',
                    specificId: '-'
                };
            }
            return listData;

        },
        getFinalCall: function (callback) {

            var rtime;
            var timeout = false;
            var delta = 200;
            searchLength();

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

                    callback(true)

                }
            }

        },
        getScheduleSource: function (state) {


            if (state.current.name == "groups.groupList.groupInfo.schedules") {

                return "groups";
            }


            if (state.current.name == "sites.sitesList.viewSite.schedules") {

                return "sites";
            }

            if (state.current.name == "customers.customersList.getCustomer.schedule") {

                return "customers";
            }

        },
        getUniqueArray: function (arr, prop) {
            var new_arr = [];
            var lookup = {};
            var arr = angular.copy(arr);
            for (var i in arr) {
            	if(arr[i][prop] !== 0) // Adding this condition to remove zero id's example scheduleId = 0
                lookup[arr[i][prop]] = arr[i];
            }
            for (i in lookup) {
                new_arr.push(lookup[i]);
            }
            return new_arr;
        },
        getSortedArray: function (arr, prop) {


            return arr.sort(function (a, b) {

                if (a[prop] === null) {
                    return 1;
                } else if (b[prop] === null) {
                    return -1;
                } else if (a[prop] === b[prop]) {
                    return 0;
                }

                return (a[prop].toLowerCase() > b[prop].toLowerCase()) ? 1 : ((b[prop].toLowerCase() > a[prop].toLowerCase()) ? -1 : 0);
            })
        }

    };
})

.factory("$session", function ($crypto) {
    return {
        params: function (obj) {
            if (obj) {
                var encrypted = $crypto.encrypt(angular.toJson(obj), 'abc');
                window.sessionStorage.setItem(document.cookie, encrypted);
            } else {
                if (window.sessionStorage.getItem(document.cookie)) {
                    return angular.fromJson($crypto.decrypt(window.sessionStorage.getItem(document.cookie), 'abc'));
                } else {
                    return null;
                }
            }
        }
    }
})