/** 
 *@file : usersController 
 * 
 *@usersController :Load user module functionality for application 
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
 * 02       17-08-2016  Nagaraju SVP Goli           Added usersListController
 * 03       22-08-2016  Nagaraju SVP Goli           Added newUserController
 * 04       22-08-2016  Nagaraju SVP Goli           Added newUserController
 * 05       29-08-2016  Nagaraju SVP Goli           Added usersFactory
 * 06       17-10-2016  Nagarjuna Eerla	    	    Implementing customers filter in users list
 * 06       02-11-2016  Nagarjuna Eerla	    	    Added the user module permissions
 */
app.controller("usersListController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories) {
        /**
         *Is the binding part between the HTML (view) and the JavaScript
         *@param $scope
         *
         *will be shared among all the components of an app
         *@param $rootScope
         *
         *facilitates API's for application
         *@param ApiFactory
         *
         *facilitates messages for application
         *@param messageFactory
         *
         *facilitates url redirection for the application
         *@param $state
         *
         *facilitates sleep functionality for the application
         *@param $timeout
         *
         *facilitates the centralized messages
         *@param toastr
         *
         *It is our custom factory provides custom messages
         *@messageFactory
         *
         *Provides reusable functions for users module
         *@usersFactory
         *
         */
        $('#modelDialog').modal("hide");

        $scope.getFilteredLength = function () {
            $timeout(function () {
                $scope.filteredLength = $($element).find(".userlist").find("li").length;
            }, 100)
        };
        $scope.openPopup = function () {
            $state.go("users.usersList.import");
        }
        $scope.alphabets = ['#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
        $scope.alphabetHeight = Math.floor(($(".alphabets-badge-width").height() - 6) / 27);
        $rootScope.changePwdOpenedCount = 0;
        $rootScope.changePwdOpened = false;
        $rootScope.changeEmailOpenedCount = 0;
        $rootScope.changeEmailOpened = false;
        $scope.isDataAvailable = true;
        $scope.userActivityFilter = function (obj) {
            /**
             *providers the filtered results, based on user inputs
             *@param :obj
             */
            switch ($scope.filterBy && $scope.filterBy.toLowerCase()) {
                case "status":
                    return obj.isActive === Number($scope.userActivity);
                case "roles":
                    return obj.roleId === Number($scope.roleId);

                case "customers":
                    if (obj.customers && obj.customers.split(",").indexOf($scope.customerId) != -1) {
                        return true;
                    }
                    return false;
                case "groups":

                    if (obj.groupId && obj.groupId.split(",").indexOf($scope.groupId) != -1) {
                        return true;
                    }
                    return false;
                case "sites":
                    if (obj.locationId && obj.locationId.split(",").indexOf($scope.siteId) != -1) {
                        return true;
                    }
                    return false;
            }
            $scope.search = function (item) {
                /**
                 *Filtering first name and last name from the object
                 *@param item
                 */
                var fullName = item.firstName + " " + item.lastName;
                if (!$scope.filterUsers || (item.firstName.toLowerCase().indexOf($scope.filterUsers) != -1) || (item.lastName.toLowerCase().indexOf($scope.filterUsers.toLowerCase()) != -1) || (fullName.toLowerCase().indexOf($scope.filterUsers.toLowerCase()) != -1)) {
                    searchLength();
                    return true;
                }
                return false;
            };
            $scope.filterByChange = function () {
                /**
                 *Reseting the default state while user switching other than roles / customers
                 */

                switch ($scope.filterBy.toLowerCase()) {
                    case "roles":
                        $scope.roleId = $scope.roles[0].value.toString();
                        break;
                    case "customers":
                        $scope.customerId = $scope.customers[0].value.toString();
                        break;
                    case "groups":
                        $scope.groupId = $scope.groups[0].value.toString();
                        break;
                    case "sites":
                        $scope.siteId = $scope.sites[0].siteId.toString();
                        break;
                }
                $scope.getFilteredLength();
            };
            return true;
        };

        if ($state.params.roleId) {
            /**
             *User trying to remove user associated roles, user can able to see those users list associated with that role
             *
             *Filtering role based results
             *
             *@param roleId
             *
             */
            $scope.$on('rolesLoaded', function (event, args) {
                $scope.filterBy = "Roles";
                $scope.roleId = $state.params.roleId.toString();
            });
        }
        $scope.moveRelatedGroup = function (alphabet) {
                $($element).find(".userlist").animate({
                    scrollTop: $($element).find("[group='" + alphabet + "']").position().top + $($element).find(".userlist").scrollTop()
                });
            }
            /**
             *This is the reusable method for getting the customers
             *@param data
             */
        usersFactory.getCustomers(function (data) {
            if (data && data.length > 0) {
                $scope.customers = data;
            }
        });
        /**
         *This is the reusable method for getting the customers
         *@param data
         */
        usersFactory.getGroupSites(function (data) {
            if (data) {
                $scope.groups = data.groupsList;
                var tempGroupsList = [];
                var tempNullGroupsList = [];
                $.each($scope.groups, function (key, value) {
                    if (value.lable) {
                        tempGroupsList.push({
                            value: value.value,
                            lable: value.lable
                        });
                    } else {
                        tempNullGroupsList.push({
                            value: value.value,
                            lable: value.lable
                        });
                    }
                })
                tempGroupsList = angular.copy(tempGroupsList).sort(function (a, b) {
                    return (a.lable.toLowerCase() > b.lable.toLowerCase()) ? 1 : ((b.lable.toLowerCase() > a.lable.toLowerCase()) ? -1 : 0);
                });
                $scope.groups = $.merge($.merge([], tempNullGroupsList), tempGroupsList);
                $scope.sites = data.siteList;
            }
        });
        $scope.viewInfo = function (user) {
            $scope.user = null;
            $rootScope.user = null;
            if ($state.current.name.indexOf("users.usersList") != -1) {
                var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + user.userId;
                if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Customer Management')) {
                    $rootScope.selectedUser = user;
                    $state.go("users.usersList.userProfile.customers", $rootScope.mergeObject($state.params, {
                        userId: user.userId,
                        sourcePage: sourcePage
                    }));
                } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
                    $state.go("users.usersList.userProfile.groups", $rootScope.mergeObject($state.params, {
                        userId: user.userId,
                        sourcePage: sourcePage
                    }));

                } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                    $state.go("users.usersList.userProfile.sites", $rootScope.mergeObject($state.params, {
                        userId: user.userId,
                        sourcePage: sourcePage
                    }));

                } else {
                    $rootScope.selectedUser = user;
                    $state.go("users.usersList.userProfile.activityLog", $rootScope.mergeObject($state.params, {
                        userId: user.userId,
                        sourcePage: sourcePage
                    }));
                }
                return;
            }
            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Customer Management')) {
                $rootScope.selectedUser = user;
                $state.go("users.usersList.userProfile.customers", $rootScope.mergeObject($state.params, {
                    userId: user.userId
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
                $state.go("users.usersList.userProfile.groups", $rootScope.mergeObject($state.params, {
                    userId: user.userId
                }));

            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                $state.go("users.usersList.userProfile.sites", $rootScope.mergeObject($state.params, {
                    userId: user.userId
                }));

            } else {
                $rootScope.selectedUser = user;
                $state.go("users.usersList.userProfile.activityLog", $rootScope.mergeObject($state.params, {
                    userId: user.userId
                }));
            }
        };
        /**
         * using this method to append customer id 
         */
        $scope.openNewUser = function () {
            $state.go("users.addNew", {
                customerId: $state.params ? $state.params.customerId : null,
                isFromCustomerProfile: $state.params ? $state.params.isFromCustomerProfile : null,
                isFromGroupSite: $state.params ? $state.params.isFromGroupSite : null
            });
        }
        $rootScope.accept = function () {
            /**
             *Assign event handler for when user click on ok in the model window
             */
            var btn = $("#modelDialog").find('.confirm')[0].targetElement;
            var status = $(btn.me).prop("checked") ? 0 : 1;
            ApiFactory.getApiData({
                serviceName: "userAcitivity",
                data: {
                    "status": status,
                    "userId": btn.obj.userId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        if ($(btn.me).prop("checked")) {
                            $(btn.me).prop("checked", false);
                            $(btn.me).closest("li").addClass("disabled");
                            $.each($scope.users, function (key, value) {
                                value.filter(function (element, index) {
                                    $.each(element, function (key, value) {
                                        if (key === 'userId' && value === btn.obj.userId) {
                                            element.isActive = 0;
                                        }
                                    });
                                });
                            });

                            toastr.success(messageFactory.getMessage(data.code).split("||")[0]);
                        } else {
                            $(btn.me).prop("checked", true);
                            $(btn.me).closest("li").removeClass("disabled");
                            $.each($scope.users, function (key, value) {
                                value.filter(function (element, index) {
                                    $.each(element, function (key, value) {
                                        if (key === 'userId' && value === btn.obj.userId) {
                                            element.isActive = 1;
                                        }
                                    });
                                });
                            });
                            toastr.success(messageFactory.getMessage(data.code).split("||")[1]);
                        }
                        $("#modelDialog").modal("hide");
                    } else {
                        $("#modelDialog").modal("hide");
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function (data) {}
            });
        };

        $scope.changeStatus = function (user, event) {
            /**
             *This method is for activation and deactivation of an user
             *
             *@param user
             *
             *@user Requested user object
             */
            if ($(event.target).prop("checked")) {
                $(event.target).prop("checked", false);
            } else {
                $(event.target).prop("checked", true);
            }
            var me = event.target;
            $('#modelDialog').on('show.bs.modal', function (event) {
                var modal = $(this);
                if ($(me).prop("checked")) {
                    modal.find('.modal-title').text('Deactivate User');
                    modal.find('.model-content').text('Are you sure you want to deactivate the user?');
                    return;
                }
                modal.find('.modal-title').text('Activate User');
                modal.find('.model-content').text('Are you sure you want to activate the user?');
            });
            $("#modelDialog").find('.confirm')[0].targetElement = {
                me: me,
                obj: user,
            };
            $("#modelDialog").modal("show");

        };

        $scope.listRoles = {};
        ApiFactory.getApiData({
            /**
             *Requesting API for All user information
             */
            serviceName: "getAllUsersDetails",
            data: {
                type: $state.current.activeTab,
                value: ($state.params && $state.params.siteId) ? $state.params.siteId + '$0' : ($state.params && $state.params.groupId) ? $state.params.groupId + '$0' : ($state.params && $state.params.customerId) ? $state.params.customerId : 0
            },
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {

                    if (data.data && data.data.length === 0) {
                        $scope.filteredLength = 0;
                        $scope.isDataAvailable = false;
                        return;
                    }



                    angular.forEach(data.data, function (val) {
                        $scope.listRoles[val.roleId] = val.roleName;
                    });
                    var tempRolesList = [];
                    var tempNullRolesList = [];
                    $.each($scope.listRoles, function (key, value) {
                        if (value) {
                            tempRolesList.push({
                                value: key,
                                label: value
                            });
                        } else {
                            tempNullRolesList.push({
                                value: key,
                                label: value
                            });
                        }
                    })
                    tempRolesList = angular.copy(tempRolesList).sort(function (a, b) {
                        return (a.label.toLowerCase() > b.label.toLowerCase()) ? 1 : ((b.label.toLowerCase() > a.label.toLowerCase()) ? -1 : 0);
                    });
                    tempRolesList = $.merge($.merge([], tempNullRolesList), tempRolesList);
                    $scope.roles = angular.copy(tempRolesList);
                    $scope.roleId = $scope.roles[0].value;
                    $scope.users = commonFactories.makeAlphabetsGroup(data.data, "firstName");
                    $scope.getFilteredLength();
                    $scope.users = commonFactories.makeAlphabetsGroup(data.data, "firstName");
                    $scope.getFilteredLength();
                    setTimeout(function () {
                        $("#nodata").css({
                            opacity: 1
                        });
                    }, 500);

                    $rootScope.$broadcast("rolesLoaded");
                }
            },
            onFailure: function (data) {}
        });
        $scope.showPlus = false;
        $scope.checkAll = function () {
            $scope.showMain = $scope.showMain == true ? false : true;
            $scope.showPlus = $scope.showPlus == true ? false : true;
            $scope.users = angular.copy($scope.users);
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

                $scope.getFilteredLength();

            }
        }
    })
    .controller("newUserController", function ($scope, $rootScope, cfpLoadingBar, ApiFactory, $state, messageFactory, $timeout, toastr, $element, usersFactory, commonFactories) {
        /**
         *Is the binding part between the HTML (view) and the JavaScript
         *@param $scope
         *
         *will be shared among all the components of an app
         *@param $rootScope
         *
         *facilitates API's for application
         *@param ApiFactory
         *
         *facilitates messages for application
         *@param messageFactory
         *
         *facilitates url redirection for the application
         *@param $state
         *
         *facilitates sleep functionality for the application
         *@param $timeout
         *
         *facilitates the centralized messages
         *@param toastr
         *
         *It is our custom factory provides custom messages
         *@messageFactory
         *
         *Provides reusable functions for users module
         *@usersFactory
         *
         */

        $scope.userInfoPage = function () {

            $state.go("users.usersList.userProfile.customers", $state.params);
        }

        $scope.statusOptions = [
            {
                label: "Active",
                id: 1
    	                        }, {
                label: "Inactive",
                id: 0
    	                        }
    	                       ];


        /*$scope.customersDataArry = [{
            id: "All",
            label: "All Customers"
        }];*/
        $scope.selectedCustomers = [];

        $scope.multiSelectCustomerSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 3,
            scrollable: true,
            showCheckAll: true,
            enableSearch: true,
            showUncheckAll: true,
            dynamicTitle: true

        };


        $scope.customersDropdownEvents = {
            onItemSelect: customerChangeselect,
            onItemDeselect: customerChangeDeselect,
            onSelectAll: customersSelectAll,
            onDeselectAll: customersUnSelectAll

        };


        /*  
          $scope.groupsDataArry = [{
              id: "All",
              label: "All Groups"
          }];*/
        $scope.selectedGroups = [];

        $scope.multiSelectGroupsSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 3,
            scrollable: true,
            showCheckAll: true,
            enableSearch: true,
            showUncheckAll: true,
            dynamicTitle: true,
            displayProp: "groupName",
            idProp: "groupId"

        };


        $scope.groupsDropdownEvents = {
            onItemSelect: groupsChangeselect,
            onItemDeselect: groupsChangeDeselect,
            onSelectAll: groupsSelectAll,
            onDeselectAll: groupsUnSelectAll

        };


        /*$scope.sitesDataArry = [{
            id: "All",
            label: "All Sites"
        }];*/
        $scope.selectedSites = [];

        $scope.multiSelectSitesSettings = {
            scrollableHeight: '200px',
            smartButtonMaxItems: 3,
            scrollable: true,
            showCheckAll: true,
            enableSearch: true,
            showUncheckAll: true,
            dynamicTitle: true,
            displayProp: "siteName",
            idProp: "siteId"

        };


        $scope.sitesDropdownEvents = {
            onItemSelect: sitesChangeselect,
            onItemDeselect: sitesChangeDeselect

        };


        var cstrSelectAll = false;

        function customerChangeselect(callback) {



            if (cstrSelectAll) {
                return;
            }

            var selectedCustomers = [];

            $.each($scope.selectedCustomers, function (key, value) {

                selectedCustomers.push(value.id);

            })

            usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {

                $scope.groupsDataArry = data.groups;
                if ($scope.selectedGroups.length == 0) {
                    $scope.sitesDataArry = data.sites;


                    var selectedSts = [];

                    $.each($scope.selectedSites, function (key, value) {

                        $.each($scope.sitesDataArry, function (key1, value1) {

                            if (value1.siteId == value.id) {

                                var sit = {};

                                sit.id = value.id;

                                selectedSts.push(sit);
                            }

                        })

                    })

                    $scope.selectedSites = selectedSts;




                }

                if (callback && typeof (callback) == "function") {
                    callback(true);
                }
            })

        }

        function customerChangeDeselect() {
            var selectedCustomers = [];
            $.each($scope.selectedCustomers, function (key, value) {
                selectedCustomers.push(value.id);
            })
            if ($scope.selectedCustomers.length == 0) {
                $scope.groupsDataArry = [];
                $scope.sitesDataArry = [];
                $scope.selectedGroups = [];
                $scope.selectedSites = [];
                return;
            };
            usersFactory.getGroupsSites(selectedCustomers.toString(), function (data) {
                $scope.groupsDataArry = data.groups;
                $scope.sitesDataArry = data.sites;
            	var selectedGups = [];
            	var selectedSts = [];

                $.each($scope.selectedGroups, function (key, value) {
                    $.each(data.groups, function (key1, value1) {

                        if (value1.groupId == value.id) {
                            var gup = {};
                            gup.id = Number(value.id);
                            selectedGups.push(gup);
                        }
                    })
                })
                $scope.selectedGroups = selectedGups;

                if ($scope.selectedGroups.length == 0) {
                    $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");
                }
                $.each($scope.selectedSites, function (key, value) {
                    $.each(data.sites, function (key1, value1) {
                        if (value1.siteId == value.id) {
                            var sit = {};
                            sit.id = Number(value.id);
                            selectedSts.push(sit);
                        }
                    })
                })
                $scope.selectedSites = selectedSts;
            })
        }

        function customersSelectAll() {
            cstrSelectAll = true;
            setTimeout(function () {
                cstrSelectAll = false;
                customerChangeselect();
            }, 500)
        }

        function customersUnSelectAll() {
            $scope.groupsDataArry = [];
            $scope.sitesDataArry = [];
            $scope.selectedGroups = [];
            $scope.selectedSites = [];
            $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");
        }

        var grpSelectAll = false;
        function groupsChangeselect() {
            if (grpSelectAll) {
                return;
            }

            usersFactory.getSites($scope.selectedGroups, function (data) {
                var sitesData = [];
                $.each(data, function (key, value) {
                    var site = {};
                    site.siteId = value.id;
                    site.siteName = value.name;
                    sitesData.push(site);

                });

                $scope.sitesDataArry = sitesData;


                var selectedSites = [];

                $.each($scope.sitesDataArry, function (key, value) {
                    var site = {};
                    site.id = value.siteId;
                    selectedSites.push(site);
                });




                $scope.selectedSites = selectedSites;


                $($element).find(".sites-dropdown").find(".dropdown-menu").addClass("readonly-actions");


            });

        }

        function groupsChangeDeselect() {



            if ($scope.selectedGroups.length == 0) {

                $scope.sitesDataArry = [];
                $scope.selectedSites = [];

                customerChangeselect();

                $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");

                return;
            }

            usersFactory.getSites($scope.selectedGroups, function (data) {
                var sitesData = [];
                $.each(data, function (key, value) {
                    var site = {};
                    site.siteId = value.id;
                    site.siteName = value.name;
                    sitesData.push(site);

                });

                $scope.sitesDataArry = sitesData;


                var selectedSites = [];

                $.each($scope.sitesDataArry, function (key, value) {
                    var site = {};
                    site.id = value.siteId;
                    selectedSites.push(site);
                })




                $scope.selectedSites = selectedSites;

            });

        }

        function groupsSelectAll() {

            grpSelectAll = true;

            setTimeout(function () {

                grpSelectAll = false;

                groupsChangeselect();

            }, 500)
        }

        function groupsUnSelectAll() {

            $scope.sitesDataArry = [];
            $scope.selectedSites = [];

            customerChangeselect();

            $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");

        }



        function sitesChangeselect() {

        }

        function sitesChangeDeselect() {


        }

        /*

                commonFactories.getCustomers(function (data) {
                    if (data && data.length > 0) {
                        $scope.customersDataArry = data;
                    }
                });
        */

        /*
    
    
            $scope.editUser = function () {
                *
                 *Redirecting to edit state
                 
                $state.go("users.usersList.userProfile.edit");
            };*/

        if ($state.params.customerId && !$state.params.isFromGroupSite) {
            $scope.selectedCustomers = [{
                id: Number($state.params.customerId)
            }];
            customerChangeselect();
            $scope.hideCustomers = true;
        }

        $scope.createUser = function (form) {
            /**
             *Posting the valid user information to server
             *
             *@param form
             *
             *@form is a collection of controls for the purpose of grouping related controls together
             *
             */
            if (form.$valid) {
                if ($scope.reportPreference != 0 && $scope.reportLevel == 0) {
                    return;
                }
                var customersList = [];
                $scope.selectedCustomers = angular.copy(usersFactory.getUniqueArray($scope.selectedCustomers, "id"))
                $.each($scope.selectedCustomers, function (key, value) {
                    if (value.id != "All") {
                        customersList.push(value.id);
                    }
                });

                var groupsList = [];
                $scope.selectedGroups = angular.copy(usersFactory.getUniqueArray($scope.selectedGroups, "id"))
                $.each($scope.selectedGroups, function (key, value) {
                    if (value.id != "All") {
                        groupsList.push(value.id);
                    }
                });

                var locationsList = [];

                $scope.selectedSites = angular.copy(usersFactory.getUniqueArray($scope.selectedSites, "id"));

                $.each($scope.selectedSites, function (key, value) {
                    if (value.id != "All") {
                        locationsList.push(value.id);
                    }
                });


                if ($state.current.actionType === "new" || $state.current.actionType === "edit") {

                    if (customersList.length > 0 && groupsList.length == 0 && locationsList.length == 0) {
                        $scope.isUserOptedForAllGroups = 1;

                        var groupsList = [];
                        $scope.groupsDataArry = angular.copy(usersFactory.getUniqueArray($scope.groupsDataArry, "groupId"))
                        $.each($scope.groupsDataArry, function (key, value) {
                            if (value.groupId != "All") {
                                groupsList.push(value.groupId);
                            }
                        });

                        var locationsList = [];

                        $scope.sitesDataArry = angular.copy(usersFactory.getUniqueArray($scope.sitesDataArry, "siteId"));

                        $.each($scope.sitesDataArry, function (key, value) {
                            if (value.siteId != "All") {
                                locationsList.push(value.siteId);
                            }
                        });

                    }
                }
                var alertPreference = ($scope.preferenceEmail ? 1 + "," : "") + ($scope.preferenceText ? 2 : "");

                if (alertPreference.slice(-1) == ",") {
                    alertPreference = alertPreference.slice(0, -1);
                }

                alertPreference = alertPreference ? eval(alertPreference.replace(",", "+")) : 0;

                var data = {
                    "firstName": $scope.firstName ? $scope.firstName : "",
                    "lastName": $scope.lastName ? $scope.lastName : "",
                    "isActive": 1,
                    "phoneNumber": $scope.phoneNumber ? $scope.phoneNumber : "",
                    "emailId": $scope.emailId ? $scope.emailId : "",
                    "title": $scope.title ? $scope.title : "",
                    "roleId": $scope.roleId ? Number($scope.roleId) : $scope.roleId,
                    "customers": customersList.toString() ? (customersList.toString().endChar(",") ? customersList.toString().slice(0, -1):customersList.toString()) : "",
                    "groupId": groupsList.toString() ? (groupsList.toString().endChar(",") ? groupsList.toString().slice(0, -1):groupsList.toString()) : "",
                    "locationId": locationsList.toString() ? (locationsList.toString().endChar(",") ? locationsList.toString().slice(0, -1):locationsList.toString()) : "",
                    "alertPreference": alertPreference,
                    "reportPreference": $scope.reportPreference ? $scope.reportPreference : "",
                    "isOptedToSendMail": $scope.sendEmail ? 1 : 0,
                    "reportLevel": $scope.reportLevel ? $scope.reportLevel : "",
                    "isUserOptedForAllGroups": $scope.isUserOptedForAllGroups ? 1 : 0

                };

                if ($state.params.customerId) {
                    data.customers = $state.params.customerId.toString();
                }

                if ($state.current.actionType === "edit" && $rootScope.selectedUser) {
                    data.isActive = $rootScope.selectedUser.isActive;
                    if ($scope.isCSO) {
                        data.isOptedToSendMail = 0;
                    }

                    if ($scope.locked) {
                        data.isActive = 2;
                    } else if (data.isActive != 0) {
                        data.isActive = 1;
                    }
                }


                ApiFactory.getApiData({
                    /**
                     *Request to post the information to server
                     */
                    serviceName: ($state.current.actionType === "edit") ? "updateUser" : "saveUser",
                    /**
                    Here we used common controller for both create and update while injecting actionType in the state
                    */
                    data: ($state.current.actionType == "edit") ? $.extend(data, {
                        userId: $rootScope.selectedUser.userId
                    }) : data,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {


                            toastr.success(messageFactory.getMessage(data.code));
                            if ($state.current.actionType === "new") {
                                // $($element).find("form").trigger("reset");
                                /*  $scope.selectedCustomers = [];
                                  $scope.selectedGroups = [];
                                  $scope.selectedLocations = [];
                                  $scope.roleId = "";
                                  $timeout(function () {
                                      $scope.preferenceEmail = false;
                                      $scope.preferenceText = false;
                                  }, 500);*/
                                skipChecking = true;
                                if ($rootScope.addedUsersData) {
                                    $rootScope.addedUsersData.push(data.data);
                                } else {
                                    $rootScope.addedUsersData = [];
                                    $rootScope.addedUsersData.push(data.data);
                                }


                                history.back();

                                /* if ($state.params && $state.params.customerId && $state.params.isFromEditCustomer) {
                                     $state.go("customers.customersList.getCustomer.editCustomer", $state.params);
                                     return;
                                 }
                                 if ($state.params && $state.params.customerId && $state.params.isFromCustomerProfile) {
                                     $state.go("customers.customersList.getCustomer.users", $state.params);
                                     return;
                                 }
                                 if ($state.params && $state.params.customerId && $state.params.isFromGroupSite && $state.params.groupId) {
                                     $state.go("customers.customersList.getCustomer.groups.groupInfo.users", $state.params);
                                     return;
                                 }
                                 if ($state.params && $state.params.customerId && $state.params.isFromGroupSite && $state.params.siteId) {
                                     $state.go("customers.customersList.getCustomer.sites.viewSite.users", $state.params);
                                     return;
                                 }


                                 $state.go("users.usersList");*/
                                return;
                            } else if ($state.current.actionType === "edit") {
                                if (data.data.userId === $rootScope.userDetails.userId) {
                                    $rootScope.userDetails = data.data;
                                }
                            }

                            if ($rootScope.changePwdOpened || $rootScope.changeEmailOpened) {
                                // Sending back to the previous one 
                                // history.go(-(($rootScope.changePwdOpenedCount*2)+1));
                                $state.go("users.usersList.userProfile.customers");
                            } else {
                                history.back();
                            }

                            // Handling the change password model by taking flag and count
                            $rootScope.changePwdOpened = false;
                            $rootScope.changePwdOpenedCount = 0;
                            $rootScope.changeEmailOpenedCount = 0;
                            $rootScope.changeEmailOpened = false;

                            skipChecking = true;
                            //$state.go("users.usersList.userProfile.customers");
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }

                    },
                    onFailure: function (data) {}
                });
            }
        };
        var prevRole;
        $scope.onRoleChange = function (callback) {

            if ($scope.roleId == 2) {
                $scope.sendEmail = false;
                $scope.preferenceEmail = false;
            }

            $scope.isCSO = false;

            if ($state.params.customerId) {
                return;
            }

            $.each($scope.roles, function (key, value) {

                if ($scope.roleId == value.value) {
                    prevRole = value;

                    if (value.superAdmin == 1) {
                        $scope.customersDataArry = [{
                            id: "All",
                            label: "All Customers"
        }]
                        $scope.selectedCustomers = [{
                            id: "All"
                }];
                        $scope.groupsDataArry = [{
                            groupId: "All",
                            groupName: "All Groups"
          }]
                        $scope.selectedGroups = [{
                            id: "All"
                }];
                        $scope.sitesDataArry = [{
                            siteId: "All",
                            siteName: "All Sites"
        }];
                        $scope.selectedSites = [{
                            id: "All"
                }];

                        $scope.allModules = true;
                        if (callback && typeof (callback) == "function") {
                            callback(false);
                        }

                    } else {

                        if (value.superAdmin == prevRole.superAdmin && ($scope.customersDataArry && $scope.customersDataArry.length > 0 && $scope.customersDataArry[0].id != "All")) {
                            return;
                        }

                        commonFactories.getCustomers(function (data) {
                            if (data && data.length > 0) {

                                $scope.customersDataArry = [];
                                $scope.selectedCustomers = [];
                                $scope.groupsDataArry = [];
                                $scope.selectedGroups = [];
                                $scope.sitesDataArry = [];
                                $scope.selectedSites = [];
                                $($element).find(".sites-dropdown").find(".dropdown-menu").removeClass("readonly-actions");
                                $scope.customersDataArry = data;

                                if (callback && typeof (callback) == "function") {
                                    callback(true);
                                }
                            }
                        });

                        $scope.allModules = false;
                    }
                }

                if ($scope.roleId == value.value && Number(value.isCSO) === 1) {
                    $scope.isCSO = true;
                    return;
                }
            });

        };

        $scope.onReportPreferenceChange = function (preference) {
            if ($state.current.actionType === "new") {
                if (preference > 0) {
                    $scope.reportLevel = "3";
                } else if (preference === "0") {
                    $scope.reportLevel = "0";
                }
            } else {

                if (preference === "0") {
                    $scope.reportLevel = "0";
                } else if ((preference > 0 && $scope.reportLevel !== "1") || (preference > 0 && $scope.reportLevel === "2")) {
                    $scope.reportLevel = "3";
                }
            }
        }

        if ($state.current.actionType === "new") {
            usersFactory.getRoles(function (data) {
                if (data && data.length > 0) {
                    $scope.roles = data;

                }
            });
            /*usersFactory.getCustomers(function (data) {
                if (data && data.length > 0) {
                    $scope.customers = data;
                }
            });*/
        }

        if ($state.current.actionType === "view" || $state.current.actionType == "edit" || $state.current.actionType == "change") {
        	ApiFactory.getApiData({
                serviceName: "getUserDetails",
                data: {
                    userId: $state.params.userId
                },
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        /**
                         *We are injecting user information to $rootScope because when ever user switching to user information screen without refresing the page we are transforing user informationt to other controller
                         */
                        $rootScope.selectedUser = data.data;
                        checkRoles();
                    } else {}
                },
                onFailure: function (data) {}
            });

            function checkRoles() {
                if ($scope.roles) {
                    $scope.roleId = $rootScope.selectedUser.roleId;
                    $scope.onRoleChange(checkCustomers);
                } else {
                    usersFactory.getRoles(function (data) {
                        if (data && data.length > 0) {
                            $scope.roles = data;
                            $scope.roleId = $rootScope.selectedUser.roleId;

                            $scope.onRoleChange(checkCustomers);
                        }
                    });
                }
            }

            function checkCustomers(proceed) {


                if (proceed && $rootScope.selectedUser.customers) {

                    var customers = [];
                    $.each($rootScope.selectedUser.customers.split(","), function (key, value) {

                        var cst = {};
                        cst.id = Number(value);

                        customers.push(cst);

                    });
                    $scope.selectedCustomers = customers;

                    customerChangeselect(checkGroups);

                } else {

                    showFormWithData();
                }


            }

            function checkGroups(proceed) {


                if (proceed && $rootScope.selectedUser.groupId) {


                    var groups = [];
                    $.each($rootScope.selectedUser.groupId.split(","), function (key, value) {

                        var gup = {};
                        gup.id = Number(value);

                        groups.push(gup);

                    });
                    $scope.selectedGroups = groups;

                    groupsChangeselect();

                    showFormWithData();

                } else if ($rootScope.selectedUser.locationId) {

                    var sites = [];
                    $.each($rootScope.selectedUser.locationId.split(","), function (key, value) {

                        var sit = {};
                        sit.id = Number(value);

                        sites.push(sit);

                    });
                    $scope.selectedSites = sites;

                    showFormWithData();

                } else {
                    showFormWithData();
                }

            }

            // $rootScope.fillUsersFormData = showFormWithData;
            $scope.$on('discarted', function (event, args) {
                $scope.createUserForm.customersList.$invalid = false;
                $scope.createUserForm.groupList.$invalid = false;
                $scope.createUserForm.locationsList.$invalid = false;
                showFormWithData();
            });

            $scope.statusChange = function () {
                if ($scope.locked) {
                    $scope.isActive = "Active & locked";
                } else {
                    if ($rootScope.selectedUser.isActive === 1) {
                        $scope.isActive = "Active";
                    } else if ($rootScope.selectedUser.isActive === 0) {
                        $scope.isActive = "Inactive";
                    } else if ($rootScope.selectedUser.isActive === 2) {
                        $scope.isActive = "Active";
                    }
                }
            }
        }
            function showFormWithData() {
                /**
                 *This function excutes when user select an user with all data getting captured
                 */
                var data = $rootScope.selectedUser;
                $scope.firstName = data.firstName;
                $scope.lastName = data.lastName;
                $scope.phoneNumber = data.phoneNumber;
                $scope.emailId = data.emailId;
                //$scope.roleId = data.roleId && data.roleId.toString();
                $scope.title = data.title;
                $scope.lastLoginInfo = data.lastLoginInfo;
                $scope.isEmailSent = data.isEmailSent;
                if (data.isActive === 1) {
                    $scope.isActive = "Active";
                } else if (data.isActive === 0) {
                    $scope.isHideLock = 1;
                    $scope.isActive = "Inactive";
                } else if (data.isActive === 2) {
                    $scope.isActive = "Active & locked";
                }

                //$scope.selectedCustomers = [];
                $scope.isOptedToSendMail = (data.isOptedToSendMail === 1) ? true : false;
                $scope.locked = (data.isActive == 2) ? true : false;

                $scope.reportPreference = data.reportPreference.toString();
                $scope.isCSO = Number(data.isCSO);
                $scope.isFirstTimeUser = Number(data.isFirstTimeUser);
                $scope.reportLevel = data.reportLevel.toString();
                $scope.preferenceEmail = (data.alertPreference && (data.alertPreference === 1 || data.alertPreference === 3)) ? true : false;
                $scope.preferenceText = (data.alertPreference && (data.alertPreference === 2 || data.alertPreference === 3)) ? true : false;
                $scope.isLoggedInSelectedUserSame = false;
                if (data.userId === $rootScope.userDetails.userId) {
                    $scope.isLoggedInSelectedUserSame = true;
                } else {
                    $scope.isLoggedInSelectedUserSame = false;
                }
                $rootScope.accept = function () {
                	
                	if($("#modelDialog").find('.confirm')[0].actionType == "change_email"){
                		skipChecking = true;
                        $state.go("users.usersList.userProfile.edit.changeEmail");
                    	return;
                	}

                	ApiFactory.getApiData({
                        serviceName: "deleteUser",
                        data: {
                            userId: $rootScope.selectedUser.userId
                        },
                        onSuccess: function (data) {
                            if (data.status.toLowerCase() === "success") {
                                $("#modelDialog").modal("hide");
                                skipChecking = true;
                                $state.go("users.usersList", {
                                    sourcePage: "users"
                                });
                                toastr.success(messageFactory.getMessage(data.code));
                            } else {
                                toastr.error(messageFactory.getMessage(data.code));
                                $("#modelDialog").modal("hide");
                            }
                        },
                        onFailure: function (data) {
                            $("#modelDialog").modal("hide");
                        }
                    });
                };
                $scope.removeUser = function () {
                    $('#modelDialog').on('show.bs.modal', function (event) {
                        var button = $(event.relatedTarget); //Button that triggered the modal;
                        var recipient = button.data('whatever'); // Extract info from data-* attributes ;
                        /* If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
                        Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.*/
                        $(this).find(".confirm")[0].actionType = "delete";
                        var modal = $(this);
                        modal.find('.modal-title').text('Delete user');
                        modal.find('.model-content').text('Are you sure you want to delete this user?');
                    });
                };
            }
        


        $scope.changePassword = function () {
            skipChecking = true;
            $state.go("users.usersList.userProfile.edit.changePassword");
        };

        $scope.changeEmail = function (event) {
        	
        	$('#modelDialog').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget); //Button that triggered the modal;
                var recipient = button.data('whatever'); // Extract info from data-* attributes ;
                var modelDialogBox = "The current email account will be deactivated, are you sure you wish to continue?";
            	if($rootScope.selectedUser.userId === $rootScope.userDetails.userId ){
            		modelDialogBox = "The current email account will be deactivated and you will need to create a new password for your new email account. The system will log you out, are you sure you wish to continue?"
            	}
                $(this).find(".confirm")[0].actionType = "change_email";
        		var modal = $(this);
                modal.find('.modal-title').text('Change Email');
                modal.find('.model-content').text(modelDialogBox);
                
        	});
        	$("#modelDialog").modal("show");
        };
        $rootScope.$on("newEmailId", function (event,args) {
          	$scope.emailId = args.emailId;
          })
    }).controller("changeEmailController", function ($scope, $state,$element, $compile, ApiFactory, toastr, messageFactory, $rootScope, $timeout) {

    	$rootScope.changeEmailOpened = true;

        if ($rootScope.changeEmailOpened) {

            $rootScope.changeEmailOpenedCount = $rootScope.changeEmailOpenedCount ? $rootScope.changeEmailOpenedCount + 1 : 1;
        }
    	  $('#modelDialog').on('show.bs.modal', function (event) {
              var modal = $(this);
              modal.find(".modal-title").text("Change Email");
              $(this).off('shown.bs.modal');
          })
          $("#modelDialog").modal("show");

          $('#modelDialog').on('hidden.bs.modal', function () {
              if ($state.$current.self.name == "users.usersList.userProfile.edit.changeEmail") {
                  $timeout(function () {
                      skipChecking = true;
                      $state.go("users.usersList.userProfile.edit");
                      skipChecking = false;
                  },500);
              }
              $(this).off('hidden.bs.modal');

          });
          $scope.changeEmailUser = {};
          $.each($($element).find("form").find("input,select,textarea"), function () {
              if ($(this).attr("name")) {
                  $scope.changeEmailUser[$(this).attr("name")] = "";
              }
          });
          if ($rootScope.selectedUser) {
              $scope.changeEmailUser.userId = $rootScope.selectedUser.userId;
              $scope.changeEmailUser.firstName = $rootScope.selectedUser.firstName; 
              $scope.changeEmailUser.lastName = $rootScope.selectedUser.lastName;
              $scope.changeEmailUser.fromEmailId =  $rootScope.selectedUser.emailId;
          }
         
          $scope.changeEmailSubmit = function (form) {
          	var isLoggedInUserChangedEmailId = false;
              if (!form.$valid) {
                  angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                  return false;
              }
              if (form.$valid) {
                  ApiFactory.getApiData({
                      serviceName: "updateEmail",
                      data: $scope.changeEmailUser,
                      onSuccess: function (data) {
                          if (data.status.toLowerCase() === "success") {
                              $("#modelDialog").modal("hide");
                              skipChecking = true;
                              $rootScope.$broadcast('newEmailId', {emailId: $scope.changeEmailUser.toChangeEmailId});
                              toastr.success(messageFactory.getMessage(data.code));
                              if ($scope.changeEmailUser.userId === $rootScope.userDetails.userId) {
                            	  isLoggedInUserChangedEmailId = true;
                              }
                          } else {
                              toastr.error(messageFactory.getMessage(data.code));
                          }
                      },
                      onFailure: function () {}
                  });
                  
                  if(isLoggedInUserChangedEmailId){
                	  window.location.href = $rootScope.appContext + '/logout';
                  }
              }
          };
    })
    .controller("changePasswordController", function ($scope, $state, $compile, ApiFactory, toastr, messageFactory, $rootScope, $timeout) {


        $rootScope.changePwdOpened = true;

        if ($rootScope.changePwdOpened) {

            $rootScope.changePwdOpenedCount = $rootScope.changePwdOpenedCount ? $rootScope.changePwdOpenedCount + 1 : 1;
        }


        $scope.changePasswordFormData = {};


        $('#modelDialog').on('show.bs.modal', function (event) {
            var modal = $(this);
            modal.find(".modal-title").text("Change password");
            $(this).off('shown.bs.modal');

        })

        $("#modelDialog").modal("show");

        $('#modelDialog').on('hidden.bs.modal', function () {
            if ($state.$current.self.name == "users.usersList.userProfile.edit.changePassword") {
                $timeout(function () {
                    skipChecking = true;
                    $state.go("users.usersList.userProfile.edit");
                    skipChecking = false;
                },500);
            }

            $(this).off('hidden.bs.modal');

        });

        $scope.changePasswordSubmit = function (form) {
            if (form.$valid) {
                ApiFactory.getApiData({
                    serviceName: "changePassword",
                    data: $.extend({
                        userId: $state.params.userId
                    }, $scope.changePasswordFormData),
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $("#modelDialog").modal("hide");

                            skipChecking = true;

                            // history.back();

                            toastr.success(messageFactory.getMessage(data.code));
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function (data) {}
                });
            }
        }
    })

.controller("userProfileController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {

    $rootScope.changePwdOpenedCount = 0;
    $rootScope.changePwdOpened = false;

    $scope.gotoCustomers = function () {

        $state.go("users.usersList.userProfile.customers", $state.params);
    }
    $scope.gotoGroups = function () {

        $state.go("users.usersList.userProfile.groups", $state.params);
    }
    $scope.gotoSites = function () {

        $state.go("users.usersList.userProfile.sites", $state.params);
    }
    $scope.gotoActivityLog = function () {

        $state.go("users.usersList.userProfile.activityLog", $state.params);
    }

    $scope.getReportPreference = function (reportPreference) {
        if (reportPreference === 0) {
            return "None";
        }
        if (reportPreference === 1) {
            return "Weekly";
        }
        if (reportPreference === 2) {
            return "Monthly";
        }
        if (reportPreference === 3) {
            return "Quarterly";
        }
        if (reportPreference === 4) {
            return "Yearly";
        }
    }

    $scope.getReportLevel = function (reportLevel) {
        if (reportLevel === 0) {
            return "None";
        }
        if (reportLevel === 1) {
            return "Customer";
        }
        if (reportLevel === 2) {
            return "Group";
        }
        if (reportLevel === 3) {
            return "Site";
        }
    }

    ApiFactory.getApiData({

        serviceName: "getUserProfileInfo",
        data: {
            userId: $state.params.userId
        },
        onSuccess: function (data) {

            $("#nodata").css({
                opacity: 1
            });
            /*
	            var filteredCustomers = [];
	            var loggedInUserCustomers = eaiDetails.customers;
	            var customersArray = loggedInUserCustomers.split(',');
	
	            if (!$rootScope.userDetails.isSuper)
	                $.each(data.data.customersList, function (key, value) {
	                    // Pushing only active customers
	                    if (value.isActive == 1)
	                        if (customersArray.indexOf(value.customerId) !== -1)
	                            filteredCustomers.push(value);
	                    data.data.customersList = filteredCustomers;
	                });
             */
            $scope.user = data.data;
            $rootScope.user = data.data;

            /*    // if(!$scope.user.isSuper)
                ApiFactory.getApiData({
                    serviceName: "getSitesGroupsByCustomers",
                    data: {
                        "userId": $scope.user.userId,
                        "customerIds": $scope.user.customers
                    },
                    onSuccess: function (data) {

                        $rootScope.groupSitesByCustomer = data.data;
                    },
                    onFailure: function (data) {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                });*/

            /*  $scope.profileSiteFilter = function (obj) {

                  obj.siteId = obj.siteId + '';

                  if ($scope.user.locationId !== null)
                      if ($scope.user.locationId.split(",").indexOf(obj.siteId) != -1) {
                          return true;
                      }

                  return false;
              };*/
        },
        onFailure: function (data) {
            toastr.error(messageFactory.getMessage(data.code));
        }
    });

    $scope.getCustomerProfile = function (customerId) {
        var customerId = customerId;
        var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + customerId;


        if ($state.current.name.indexOf("customers.customersList") != -1) {

            if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
                $state.go("customers.customersList.getCustomer.groups", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                $state.go("customers.customersList.getCustomer.sites", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                $state.go("customers.customersList.getCustomer.devices", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                $state.go("customers.customersList.getCustomer.schedule", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                $state.go("customers.customersList.getCustomer.users", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            } else {
                $state.go("customers.customersList.getCustomer.activityLog", $rootScope.mergeObject($state.params, {
                    customerId: customerId,
                    sourcePage: sourcePage
                }));
            }

            return;

        }




        if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
            $state.go("customers.customersList.getCustomer.groups", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
            $state.go("customers.customersList.getCustomer.sites", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
            $state.go("customers.customersList.getCustomer.devices", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
            $state.go("customers.customersList.getCustomer.schedule", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
            $state.go("customers.customersList.getCustomer.users", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        } else {
            $state.go("customers.customersList.getCustomer.activityLog", $rootScope.mergeObject($state.params, {
                customerId: customerId
            }));
        }

    };
})

.controller("userSettingsController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element, commonFactories, cfpLoadingBar) {

        $scope.disableAction = true;
        $scope.user = $rootScope.userDetails;
        $scope.firstName = $scope.user.firstName;
        $scope.lastName = $scope.user.lastName;
        $scope.emailId = $scope.user.emailId;
        $scope.phoneNumber = $scope.user.phoneNumber;
        $scope.roleName = $scope.user.roleName;
        $scope.reportPreference = $scope.user.reportPreference;
        $scope.reportLevel = $scope.user.reportLevel;
        
        if ($scope.user.isActive === 1) {
            $scope.isActive = "Active";
        } else if ($scope.user.isActive === 0) {
            $scope.isHideLock = 1;
            $scope.isActive = "Inactive";
        } else if ($scope.user.isActive === 2) {
            $scope.isActive = "Active & locked";
        }

        $scope.onReportPreferenceChange = function (preference) {
            if (preference === 0 || preference === undefined ) {
                $scope.reportLevel = 0;
            } else if ((preference > 0 && $scope.reportLevel !== 1) || (preference > 0 && $scope.reportLevel === 2)) {
                $scope.reportLevel = 3;
            }
        }
        
        $scope.reportPreferenceOptions = [{
            name: "Weekly",
            id: 1
        }, {
            name: "Monthly",
            id: 2
        }, {
            name: "Quarterly",
            id: 3
        }, {
            name: "Yearly",
            id: 4
        }, {
            name: "None",
            id: 0
        }];
        
        $scope.reportLevelOptions = [{
            name: "Customer",
            id: 1
        }, {
            name: "Group",
            id: 2
        }, {
            name: "Site",
            id: 3
        }, {
            name: "None",
            id: 0
        }];
        
        $scope.preferenceEmail = ($scope.user.alertPreference && ($scope.user.alertPreference === 1 || $scope.user.alertPreference === 3)) ? true : false;
        $scope.preferenceText = ($scope.user.alertPreference && ($scope.user.alertPreference === 2 || $scope.user.alertPreference === 3)) ? true : false;

        $scope.changePassword = function () {
            skipChecking = true;
            $state.go("users.settings.profile.edit.changePassword");
        };

        $scope.newTime = new Date().getTime();

        $scope.updateUserSettings = function (form) {
            /**
             *Posting the valid user information to server
             *
             *@param form
             *
             *@form is a collection of controls for the purpose of grouping related controls together
             *
             */
            if (form.$valid) {

                var alertPreference = ($scope.preferenceEmail ? 1 + "," : "") + ($scope.preferenceText ? 2 : "");

                if (alertPreference.slice(-1) == ",") {
                    alertPreference = alertPreference.slice(0, -1);
                }

                alertPreference = alertPreference ? eval(alertPreference.replace(",", "+")) : 0;
                
                var data = {
                    "userId": $rootScope.userDetails.userId,
                    "firstName": $scope.firstName ? $scope.firstName : "",
                    "lastName": $scope.lastName ? $scope.lastName : "",
                    "phoneNumber": $scope.phoneNumber ? $scope.phoneNumber : "",
                    "emailId": $scope.emailId ? $scope.emailId : "",
                    "alertPreference": alertPreference,
                    "reportPreference": $scope.reportPreference ? $scope.reportPreference : "",
                    "reportLevel": $scope.reportLevel ? $scope.reportLevel : ""
                };

                ApiFactory.getApiData({
                    /**
                     *Request to post the information to server
                     */
                    serviceName: "updateUserSettingsProfile",
                    data: data,

                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            toastr.success(messageFactory.getMessage(data.code));
                            skipChecking = true;
                            // Updating session details 
                            $rootScope.userDetails = data.data;

                            // Redirecting to profile screen
                            $state.go("users.settings.profile");
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }

                    },
                    onFailure: function (data) {}
                });
            }
        };

        $scope.refreshState = function () {

            $state.go($state.current, {}, {
                reload: true
            });
        }
        $scope.newSettingsFormData = {};
        $scope.imageUpload = function (event) {

            var $target = event.target;

            if ($target.value == "") {
                $scope.refreshState();
            }
            $scope.input = $($element).find("[type=file]");
            commonFactories.getImageData(event, function (data) {

                var extenstion = data.name.substr((data.name.lastIndexOf(".") + 1), data.name.length);
                if (extenstion && extenstion.toLowerCase() != "png") {
                    toastr.error(messageFactory.getMessage("INFO_APP_FILE_PNG"));
                    $(event.target).replaceWith($scope.input.clone(true));
                    return;
                }
                if ((data.size / (1024)).toFixed(1) < 500) {
                    cfpLoadingBar.start();
                    $("body").css("pointer-events", "none");

                    $scope.disableAction = false;
                    $($element).find(".thumb").attr("src", data.src);

                    setTimeout(function () {

                        cfpLoadingBar.complete();
                        $("body").css("pointer-events", "all");
                    }, 500)

                } else {
                    $(event.target).replaceWith($scope.input.clone(true));
                    toastr.error("Please use only below 500KB for upload");
                }
                /*var element = $(event.target);
		       element.attr("name","file");
		       if(data.src) {
		        $scope.newSettingsFormData.files =  $(event.target);
		            ApiFactory.getApiData({
		                serviceName: "uploadSettingsLogo",
		                data: $scope.newSettingsFormData,
		                onSuccess: function (data) {
		              
		                },
		               onFailure: function (data) {}
		           });
		       }*/
            });
        }

        $scope.uploadCompanyLogo = function (form) {
            $scope.noimage = false;
            if ($($element).find("[type=file]").val() == "") {
                $scope.noimage = true;
                return;
            }
            if (form.$valid) {

                var element = $($element).find("[type=file]");
                element.attr("name", "file");

                $scope.newSettingsFormData.files = $($element).find("[type=file]");
                ApiFactory.getApiData({
                    serviceName: "uploadSettingsLogo",
                    data: $scope.newSettingsFormData,
                    onSuccess: function (data) {
                        toastr.success(messageFactory.getMessage(data.code));
                        $scope.disableAction = true;
                        $scope.applicationImage = data.data;

                        $(".header-logo").attr("src", $(".header-logo").attr("src") + "&" + new Date().getTime())

                    },
                    onFailure: function (data) {}
                });
            }
        }


        $scope.changeEmail = function (event) {
            
        	$('#modelDialog').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget); //Button that triggered the modal;
                var recipient = button.data('whatever'); // Extract info from data-* attributes ;
                $(this).find(".confirm")[0].actionType = "change_my_email";
        		var modal = $(this);
                modal.find('.modal-title').text('Change Email');
                modal.find('.model-content').text('The current email account will be deactivated and you will need to create a new password for your new email account. The system will log you out, are you sure you wish to continue?');
                
        	});
        	$("#modelDialog").modal("show");
        	
        };
        
        $rootScope.accept = function(){

        	if($("#modelDialog").find('.confirm')[0].actionType == "change_my_email"){
        		skipChecking = true;
                $state.go("users.settings.profile.edit.changeEmail");
            	return;
        	}
        }
        
        if ($state.$current.self.name == "users.settings.profile.edit.changeEmail") {
        	  $('#modelDialog').on('show.bs.modal', function (event) {
                  var modal = $(this);
                  modal.find(".modal-title").text("Change Email");
                  $(this).off('shown.bs.modal');
              })
              $("#modelDialog").modal("show");

              $('#modelDialog').on('hidden.bs.modal', function () {
                  if ($state.$current.self.name == "users.settings.profile.edit.changeEmail") {
                      $timeout(function () {
                          skipChecking = true;
                          $state.go("users.settings.profile.edit");
                          skipChecking = false;
                      },500);
                  }
                  $(this).off('hidden.bs.modal');

              });
              $scope.changeEmailUser = {};
              $.each($($element).find("form").find("input,select,textarea"), function () {
                  if ($(this).attr("name")) {
                      $scope.changeEmailUser[$(this).attr("name")] = "";
                  }
              });
              $scope.changeEmailUser.userId = $rootScope.userDetails.userId;
              $scope.changeEmailUser.firstName = $rootScope.userDetails.firstName; 
              $scope.changeEmailUser.lastName = $rootScope.userDetails.lastName;
              $scope.changeEmailUser.fromEmailId =  $rootScope.userDetails.emailId;
        }
        $scope.changeEmailSubmit = function (form) {
            if (!form.$valid) {
                angular.element("[name='" + form.$name + "']").find('.ng-invalid:visible:first').focus();
                return false;
            }
            if (form.$valid) {
            	var isMailChanged = false;
                ApiFactory.getApiData({
                    serviceName: "updateEmail",
                    data: $scope.changeEmailUser,
                    onSuccess: function (data) {
                        if (data.status.toLowerCase() === "success") {
                            $("#modelDialog").modal("hide");
                            skipChecking = true;
                            isMailChanged = true;
                            toastr.success(messageFactory.getMessage(data.code));
                        } else {
                            toastr.error(messageFactory.getMessage(data.code));
                        }
                    },
                    onFailure: function () {}
                });
                if(isMailChanged){
                	window.location.href = $rootScope.appContext + '/logout';
                }
            }
        };
        
        
    })
    .controller("userSettingsProfileController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {
        $scope.user = $rootScope.userDetails;

        $scope.getReportPreference = function (reportPreference) {
            if (reportPreference === 0) {
                return "None";
            }
            if (reportPreference === 1) {
                return "Weekly";
            }
            if (reportPreference === 2) {
                return "Monthly";
            }
            if (reportPreference === 3) {
                return "Quarterly";
            }
            if (reportPreference === 4) {
                return "Yearly";
            }
        }

        $scope.getReportLevel = function (reportLevel) {
            if (reportLevel === 0) {
                return "None";
            }
            if (reportLevel === 1) {
                return "Customer";
            }
            if (reportLevel === 2) {
                return "Group";
            }
            if (reportLevel === 3) {
                return "Site";
            }
        }
    })

.controller("settingsChangePasswordController", function ($scope, $state, $compile, ApiFactory, toastr, messageFactory, $rootScope) {

    $scope.changePasswordFormData = {};

    $('#modelDialog').on('show.bs.modal', function (event) {
        var modal = $(this);
        modal.find(".modal-title").text("Change password");
        $(this).off('shown.bs.modal');
    })

    $("#modelDialog").modal("show");

    $('#modelDialog').on('hidden.bs.modal', function () {
        if ($state.$current.self.name == "users.settings.profile.edit.changePassword") {
            skipChecking = true;
            $state.go("users.settings.profile.edit");
            skipChecking = false;
        }

        $(this).off('hidden.bs.modal');

    });

    $scope.changePasswordSubmit = function (form) {
        if (form.$valid) {
            ApiFactory.getApiData({
                serviceName: "changePassword",
                data: $.extend({
                    userId: $rootScope.userDetails.userId
                }, $scope.changePasswordFormData),
                onSuccess: function (data) {
                    if (data.status.toLowerCase() === "success") {
                        $("#modelDialog").modal("hide");
                        skipChecking = true;
                        history.back();
                        toastr.success(messageFactory.getMessage(data.code));
                    } else {
                        toastr.error(messageFactory.getMessage(data.code));
                    }
                },
                onFailure: function (data) {}
            });
        }
    }
}).controller("imporUsersController", function ($scope, $rootScope, ApiFactory, messageFactory, $state, toastr, $timeout, usersFactory, $element) {
    $('#modelDialog').on('show.bs.modal', function (event) {
        var modal = $(this);
        modal.find(".modal-title").text("Import Users");
        $(this).off('shown.bs.modal');
    });
    $("#modelDialog").modal("show");
    $scope.uploadSubmitFormData = {};
    $scope.importData = function () {
        $scope.uploadSubmitFormData.files = $($element).find("[type='file']");
        $scope.uploadSubmitFormData.import = "User"
        ApiFactory.getApiData({
            serviceName: "usersUpload",
            data: $scope.uploadSubmitFormData,
            onSuccess: function (data) {
                if (data.status.toLowerCase() === "success") {
                    var blob = new Blob([data], {
                        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    });
                    var objectUrl = URL.createObjectURL(blob);
                    var x = toastr.success(messageFactory.getMessage(data.code) + '<br/>total records= ' + data.data.totalCount + '<br/>failed records= ' + data.data.failedCount + '<br/>inserted records= ' + data.data.sucessCount + " <br/> <button class='relatedLinks btn btn-xs btn-default'>Click here to see output</button>", {
                        //var x = toastr.success(messageFactory.getMessage(data.code)+"<br/> <button class='relatedLinks btn btn-xs btn-default'>Click here to see output</button>",{
                        timeOut: 0,
                        extendedTimeOut: 0
                    });
                    $(x.el).on("click", function (event) {
                        if ($(event.target).hasClass("relatedLinks")) {
                            $("#modelDialog").modal("hide");
                            window.location.href = "fileUpload/loadExcel?excelName=" + data.data.fileName;
                        }
                    });
                } else {
                    var blob = new Blob([data], {
                        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    });
                    var objectUrl = URL.createObjectURL(blob);
                    //var x = toastr.error(messageFactory.getMessage(data.code)+"<br/> <button class='relatedLinks btn btn-xs btn-default'>Click here to see output</button>",{

                    if (data.code == "ERR_USER_UPLOAD_FORMAT_2000") {
                        toastr.error(messageFactory.getMessage(data.code));
                        return;
                    }
                    var x = toastr.error(messageFactory.getMessage(data.code) + '<br/>total records= ' + data.data.totalCount + '<br/>failed records= ' + data.data.failedCount + '<br/>inserted records= ' + data.data.sucessCount + " <br/> <button class='relatedLinks btn btn-xs btn-default'>Click here to see output</button>", {
                        timeOut: 0,
                        extendedTimeOut: 0
                    });
                    $(x.el).on("click", function (event) {
                        if ($(event.target).hasClass("relatedLinks")) {
                            $("#modelDialog").modal("hide");
                            window.location.href = "fileUpload/loadExcel?excelName=" + data.data.fileName;
                        }
                    });
                }
            },
            onFailure: function (data) {}
        });
    }
}).controller("activityLogUserController", function ($scope, ApiFactory, $rootScope, $state, $element, usersFactory) {
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
    $scope.activityObj.specificId = $state.params.userId;
    $scope.activityObj.serviceId = 6;
    $scope.startDate = "";
    $scope.endDate = "";
    $scope.activityObj.startDate = "";
    $scope.activityObj.endDate = "";
    $.each($($element).find("form").find("input,select,textarea"), function () {
        if ($(this).attr("name")) {
            $scope.activityObj[$(this).attr("name")] = "";
        }
    });
    $scope.customers = [];
    $scope.groups = [];
    $scope.sites = [];
    $scope.devices = [];

    var customers = {};
    var groups = {};
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
                    if (value.alServiceName.toLowerCase().trim() === "customer") {
                        if (value.alSpecificName) {
                            customers[value.alSpecificName.trim()] = value.alSpecificName.trim();
                        }
                    }
                    if (value.alServiceName.toLowerCase().trim() === "group") {
                        if (value.alSpecificName) {
                            groups[value.alSpecificName.trim()] = value.alSpecificName.trim();
                        }
                    }
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
                var tempCustomersList = [];
                $.each(customers, function (key, value) {
                    tempCustomersList.push({
                        value: key,
                        lable: value
                    });
                });
                $scope.customers = $rootScope.aphaNumSort(tempCustomersList,'lable');

                var tempGroupsList = [];
                $.each(groups, function (key, value) {
                    tempGroupsList.push({
                        value: key,
                        lable: value
                    });
                });
                $scope.groups = $rootScope.aphaNumSort(tempGroupsList,'lable');

                var tempSitesList = [];
                $.each(sites, function (key, value) {
                    tempSitesList.push({
                        value: key,
                        lable: value
                    });
                });
                $scope.sites = $rootScope.aphaNumSort(tempSitesList,'lable');

                var tempDevicesList = [];
                $.each(devices, function (key, value) {
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
        $scope.activityObj.specificId = $state.params.userId;
        $scope.activityObj.serviceId = 6;
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
            case "customer":
                if ($scope.activityObj.customerId) {
                    return obj.alSpecificName === $scope.activityObj.customerId;
                } else {
                    return true;
                }
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
                    if ($scope.activityObj.filterByAction.toLowerCase() === 'called') {
                        return (obj.alAction.toLowerCase() === 'called' || obj.alAction.toLowerCase() === 'emailed' || obj.alAction.toLowerCase() === 'texted')
                    } else {
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
});