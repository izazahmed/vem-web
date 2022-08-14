/** 
 *@file : states 
 *
 *@states :Load states for application
 *
 *@'right@' : is the view name
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
 * 02       17-08-2016  Nagaraju SVP Goli           Added users state
 * 03       17-08-2016  Nagaraju SVP Goli           Added users.addNew state
 * 04       22-08-2016  Nagaraju SVP Goli           Added users.usersList state
 * 05       23-08-2016  Nagaraju SVP Goli           Added users.usersList.userInfo state
 * 06       25-08-2016  Nagaraju SVP Goli           Added users.usersList.userInfo.edit state
 * 07		30-08-2016  Arun Singh					Added customers.customerslist
 * 08       25-08-2016  Nagaraju SVP Goli           Added users.usersList.userInfo.edit state  
 * 09       22-08-2016  Bhoomika Rabadiya           Added roles state
 * 10       23-08-2016  Bhoomika Rabadiya           Added roles.addNew state
 * 11       23-08-2016  Bhoomika Rabadiya           Added roles.rolesList state
 * 12       23-08-2016  Bhoomika Rabadiya           Added roles.rolesList.getRole state
 * 13       24-08-2016  Bhoomika Rabadiya           Added roles.rolesList.getRole.editRole state
 * 14       02-09-2016  Bhoomika Rabadiya           Added customers.customersList.getCustomer state
 * 15       02-09-2016  Bhoomika Rabadiya           Added customers.addNew.addLocations state
 * 16       06-09-2016  Bhoomika Rabadiya           Added customers.addNew.addLocations.manually state
 * 17       08-09-2016  Bhoomika Rabadiya          	Added customers.customersList.getCustomer.sites state
 * 18       08-09-2016  Bhoomika Rabadiya          	Added customers.customersList.getCustomer.sites.addLocations state
 * 19       08-09-2016  Bhoomika Rabadiya          	Added customers.customersList.getCustomer.sites.addLocations.manually state
 * 20       08-09-2016  Bhoomika Rabadiya          	Added customers.customersList.getCustomer.sites.viewSite state
 * 21       08-09-2016  Bhoomika Rabadiya           Added customers.customersList.getCustomer.sites.viewSite.editLocations state
 * 22       14-10-2016  Bhoomika Rabadiya           Added sites state
 * 23       14-10-2016  Bhoomika Rabadiya           Added sites.sitesList state
 * 24       14-10-2016  Bhoomika Rabadiya           Added sites.sitesList.addLocations state
 * 25       14-10-2016  Bhoomika Rabadiya           Added sites.sitesList.addLocations.manually state
 * 26       14-10-2016  Bhoomika Rabadiya           Added sites.sitesList.viewSite state
 * 27       14-10-2016  Bhoomika Rabadiya           Added sites.sitesList.viewSite.editLocations state
 * 28       25-10-2016  Bhoomika Rabadiya           Added groups state
 * 29       25-10-2016  Bhoomika Rabadiya           Added groups.groupList state
 * 30       25-10-2016  Bhoomika Rabadiya           Added groups.groupList.newGroup state
 * 31       25-10-2016  Bhoomika Rabadiya           Added groups.groupList.newGroup.manually state
 * 32       25-10-2016  Bhoomika Rabadiya           Added groups.groupList.groupInfo state
 * 33       25-10-2016  Bhoomika Rabadiya           Added groups.groupList.groupInfo.editGroup state
 * 34		02-11-2016	Nagarjuna Eerla				Added the eaidashboard state
 * 35		02-11-2016	Nagarjuna Eerla				Added customers.customersList.getCustomer.groups.groupInfo.users state
 * 36		02-11-2016	Nagarjuna Eerla				Added customers.customersList.getCustomer.sites.viewSite.users state
 * 37       11-11-2016  Bhoomika Rabadiya           Added activitylog state
 * 38       11-11-2016  Bhoomika Rabadiya           Added activitylog.activityLogList state
 * 39       11-11-2016  Bhoomika Rabadiya           Added activitylog.activityLogList.addNew state
 */
app.config(function ($stateProvider, $urlRouterProvider) {

    /**
     *providers for routing
     *@param :$stateProvider
     *@param :$urlRouterProvider
     */


    $stateProvider.state('users', {
            url: "/users",
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            defaultSubstate: 'users.usersList',
            abstract: true,
            data: {
                breadcrumbProxy: 'users.usersList'
            }
        }).state('users.settings', {
            url: "/userSettings?userId",
            views: {
                'right@': {
                    templateUrl: "app/views/profile/settings.html",
                    controller: "userSettingsController"
                }
            },
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            data: {
                displayName: 'settings'
            }
        }).state('users.settings.profile', {
            url: "/myProfile",
            views: {
                'right@': {
                    templateUrl: "app/views/profile/profile.html",
                    controller: "userSettingsProfileController"
                }
            },
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            data: {
                displayName: 'My Profile'
            }
        }).state('users.settings.profile.edit', {
            url: "/editProfile",
            views: {
                'right@': {
                    templateUrl: "app/views/profile/editProfile.html",
                    controller: "userSettingsController"
                }
            },
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            actionType: 'edit',
            usersActionType: "edit",
            data: {
                displayName: 'Edit Profile'
            }
        }).state('users.settings.profile.edit.changePassword', {
            url: "/changePassword",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/profile/changePassword.html",
                    controller: "settingsChangePasswordController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'change',
            usersActionType: "edit",
            data: {
                displayName: "Change password"
            }
        }).state('users.settings.profile.edit.changeEmail', {
            url: "/changeEmail",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/profile/changeEmail.html",
                    controller: "userSettingsController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'change',
            usersActionType: "edit",
            data: {
                displayName: "Change Email"
            }
        }).state('users.settings.help', {
            url: "/help",
            views: {
                'right@': {
                    templateUrl: "app/views/profile/help.html"
                }
            },
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            data: {
                displayName: 'help'
            }
        }).state('users.usersList', {
            url: "/usersList?sourcePage&roleId",
            views: {
                'right@': {
                    templateUrl: "app/views/users/usersList.html",
                    controller: "usersListController"
                }
            },
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            data: {
                displayName: 'Users'
            }
        }).state('users.usersList.import', {
            url: "/import",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/importData.html",
                    controller: "imporUsersController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-users',
            activeTab: 'users',
            data: {
                displayName: 'Users'
            }
        }).state('users.addNew', {
            url: "/addNew?customerId",
            views: {
                'right@': {
                    templateUrl: "app/views/users/newUser.html",
                    controller: "newUserController"
                }
            },
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'new',
            data: {
                displayName: 'New User'
            }
        }).state('users.usersList.userInfo', {
            url: "/userInfo?userId",
            views: {
                'right@': {
                    templateUrl: "app/views/users/userInfo.html",
                    controller: "newUserController"
                }
            },
            inherit: true,
            activeTab: 'users',
            usersActionType: "view",
            activeTabIcon: 'fa fa-users',
            actionType: 'view',
            data: {
                displayName: "User info"
            }
        }).state('users.usersList.userProfile.edit', {
            url: "/edit",
            activeTab: 'users',
            views: {
                'right@': {
                    templateUrl: "app/views/users/userInfo.html",
                    controller: "newUserController"
                }
            },
            activeTabIcon: 'fa fa-users',
            actionType: 'edit',
            usersActionType: "edit",
            data: {
                displayName: "Edit User"
            }
        }).state('users.usersList.userProfile', {
            url: "/userProfile?userId",
            views: {
                'right@': {
                    templateUrl: "app/views/users/userProfile.html",
                    controller: "userProfileController"
                }
            },
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'view',
            usersActionType: "view",
            data: {
                displayName: "User Profile"
            }
        }).state('users.usersList.userProfile.customers', {
            url: "/customers",
            views: {
                'userProfile-info': {
                    templateUrl: "app/views/users/userTabList.html",
                    controller: "userProfileController"
                }
            },
            activeSubTab: "customers",
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "customers"
            }
        }).state('users.usersList.userProfile.activityLog', {
            url: "/activityLog",
            views: {
                'userProfile-info': {
                    templateUrl: "app/views/users/activityLogList.html",
                    controller: "activityLogUserController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            activeSubTab: "activityLog",
            actionType: 'view',
            data: {
                displayName: "Users"
            }
        }).state('users.usersList.userProfile.edit.changePassword', {
            url: "/changePassword",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/profile/changePassword.html",
                    controller: "changePasswordController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'change',
            usersActionType: "edit",
            data: {
                displayName: "Change password"
            }
        }).state('users.usersList.userProfile.edit.changeEmail', {
            url: "/changeEmail",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/profile/changeEmail.html",
                    controller: "changeEmailController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeTabIcon: 'fa fa-users',
            actionType: 'change',
            usersActionType: "edit",
            data: {
                displayName: "Change Email"
            }
        }).state('roles', {
            url: "/roles",
            activeTab: 'roles',
            abstract: true,
            activeTabIcon: "fa fa-user",
            defaultSubstate: 'roles.rolesList'
        }).state('roles.rolesList', {
            url: "/rolesList",
            views: {
                'right@': {
                    templateUrl: "app/views/roles/roles.html",
                    controller: "rolesListController"
                }
            },
            activeTabIcon: 'fa fa-user',
            activeTab: 'roles',
            data: {
                displayName: "Roles"
            }
        }).state('roles.addNew', {
            url: "/addNew",
            views: {
                'right@': {
                    templateUrl: "app/views/roles/newRole.html",
                    controller: "newRoleController"
                }
            },
            activeTab: 'roles',
            activeTabIcon: "fa fa-user",
            actionType: 'new',
            data: {
                displayName: "New Role"
            }
        }).state('roles.rolesList.getRole.editRole', {
            url: "/editRole",
            views: {
                'right@': {
                    templateUrl: "app/views/roles/editRole.html",
                    controller: "editRoleController"
                }
            },
            activeTab: 'roles',
            activeTabIcon: "fa fa-user",
            actionType: 'edit',
            data: {
                displayName: "Edit Role"
            }
        }).state('roles.rolesList.getRole', {
            url: "/getRole?roleId",
            views: {
                'right@': {
                    templateUrl: "app/views/roles/viewRole.html",
                    controller: "viewRoleController"
                }
            },
            activeTab: 'roles',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Role Info"
            }
        }).state('customers', {
            url: "/customers?customerId&groupId",
            activeTab: 'customers',
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'customers.customersList'
        }).state('customers.customersList', {
            url: "/customerslist?sourcePage",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/customers.html",
                    controller: "customersListController"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'customers',
            data: {
                displayName: "Customers"
            }
        }).state('customers.customersList.getCustomer', {
            url: "/getCustomer",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/customerInfo.html",
                    controller: "customerProfile"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Customers Info"
            }
        }).state('customers.customersList.getCustomer.editCustomer', {
            url: "/editCustomer",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/editCustomer.html",
                    controller: "newCustomerController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'edit',
            flagToRedirect: 'customersEdit',
            data: {
                displayName: "Edit Customer"
            }
        }).state('devices', {
            url: "/devices",
            activeTab: 'devices',
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'devices.devicesList'
        }).state('devices.devicesList', {
            url: "/devicesList?sourcePage&customerId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/devicesList.html",
                    controller: "deviceListController"
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Devices"
            }
        }).state('devices.newDevice', {
            url: "/addNew?customerId&siteId&groupId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/newDevice.html",
                    controller: "newDeviceController"
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New device"
            }
        }).state('devices.devicesList.profile', {
            url: "/profile?deviceId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/deviceProfile.html",
                    controller: "deviceProfileController"
                }
            },
            abstract: true,
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            activeSubTab: "devices",
            actionType: 'view',
            data: {
                displayName: "Profile"
            }
        }).state('devices.devicesList.profile.edit', {
            url: "/edit",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/editDevice.html",
                    controller: "newDeviceController"
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            actionType: 'edit',
            data: {
                displayName: "Edit"
            }
        })
        .state('devices.devicesList.profile.settings', {
            url: "/settings",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/settings.html",
                    controller: "settingsProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['ui.bootstrap', 'assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css']);
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            activeSubTab: "current",
            actionType: 'view',
            data: {
                displayName: "settings"
            }
        }).state('devices.devicesList.profile.forecasts', {
            url: "/forecasts",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/forecasts.html",
                    controller: "forecastsProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['ui.bootstrap']);
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            activeSubTab: "forecasts",
            actionType: 'view',
            data: {
                displayName: "Forecasts"
            }
        }).state('devices.devicesList.profile.alerts', {
            url: "/alerts?dashboardAlertStatus",
            views: {
                'device-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            activeSubTab: "alerts",
            actionType: 'view',
            data: {
                displayName: "Alerts"
            }
        }).state('devices.devicesList.profile.settings.addCustomSchedule', {
            url: "/addCustomSchedule?scheduleId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/deviceProfile/addCustomSchedule.html",
                    controller: "addCustomScheduleController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            data: {
                displayName: "Schedules"
            }
        }).state('devices.devicesList.profile.activityLog', {
            url: "/activityLog",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/activityLog.html",
                    controller: "activityLogProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'devices',
            activeTabIcon: "fa fa-building",
            activeSubTab: "activityLog",
            actionType: 'view',
            data: {
                displayName: "activityLog"
            }
        }).state('customers.customersList.getCustomer.devices', {
            url: "/devices",
            views: {
                'customer-info': {
                    templateUrl: "app/views/customers/devices/devicesList.html",
                    controller: "deviceListController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "devices",
            actionType: 'view',
            data: {
                displayName: "Devices"
            }
        }).state('customers.customersList.getCustomer.users', {
            url: "/users",
            views: {
                'customer-info': {
                    templateUrl: "app/views/users/usersList.html",
                    controller: "usersListController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "users",
            actionType: 'view',
            data: {
                displayName: "Users"
            }
        }).state('customers.customersList.getCustomer.activityLog', {
            url: "/activityLog",
            views: {
                'customer-info': {
                    templateUrl: "app/views/customers/activityLogList.html",
                    controller: "activityLogCustomerController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "activityLog",
            actionType: 'view',
            data: {
                displayName: "Activity Log"
            }
        }).state('customers.customersList.getCustomer.devices.newDevice', {
            url: "/addNew",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/newDevice.html",
                    controller: "newDeviceController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New device"
            }
        }).state('customers.customersList.getCustomer.devices.profile', {
            url: "/profile?deviceId&siteId&scheduleId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/deviceProfile.html",
                    controller: "deviceProfileController"
                }
            },
            abstract: true,
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "devices",
            actionType: 'view',
            data: {
                displayName: "Profile"
            }
        })

    .state('customers.customersList.getCustomer.devices.edit', {
            url: "/edit?deviceId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/editDevice.html",
                    controller: "newDeviceController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'edit',
            data: {
                displayName: "Edit"
            }
        })
        .state('customers.customersList.getCustomer.devices.profile.settings', {
            url: "/settings",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/settings.html",
                    controller: "settingsProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['ui.bootstrap', 'assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css']);
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "current",
            actionType: 'view',
            data: {
                displayName: "settings"
            }
        }).state('customers.customersList.getCustomer.devices.profile.forecasts', {
            url: "/forecasts",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/forecasts.html",
                    controller: "forecastsProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['ui.bootstrap']);
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "forecasts",
            actionType: 'view',
            data: {
                displayName: "Forecasts"
            }
        }).state('customers.customersList.getCustomer.devices.profile.alerts', {
            url: "/alerts",
            views: {
                'device-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "alerts",
            actionType: 'view',
            data: {
                displayName: "Alerts"
            }
        }).state('customers.customersList.getCustomer.devices.profile.settings.addCustomSchedule', {
            url: "/addCustomSchedule",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/devices/deviceProfile/addCustomSchedule.html",
                    controller: "addCustomScheduleController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            data: {
                displayName: "Schedules"
            }
        }).state('customers.customersList.getCustomer.devices.profile.activityLog', {
            url: "/activityLog",
            views: {
                'device-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/activityLog.html",
                    controller: "activityLogProfileController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            activeSubTab: "activityLog",
            actionType: 'view',
            data: {
                displayName: "activityLog"
            }
        })

    .state('customers.customersList.getCustomer.sites', {
        url: "/sites",
        views: {
            'site-info': {
                templateUrl: "app/views/customers/sites/siteList.html",
                controller: "siteListController"
            }
        },
        activeSubTab: "sites",
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'view',
        data: {
            displayName: "Sites"
        }
    }).state('customers.addNew.addChoose', {
        url: "/addChoose",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/customerSiteUsers.html",
                controller: "newCustomerController"
            }
        },
        modal: true,
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'view',
        data: {
            displayName: "Add Users And Sites"
        }
    }).state('customers.customersList.getCustomer.sites.addLocations', {
        url: "/addLocations",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/import/import.html",
                controller: "newSiteController"
            }
        },
        modal: true,
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'new',
        data: {
            displayName: "New Site"
        }
    }).state('customers.customersList.getCustomer.sites.groupingSites', {
        url: "/groupingSites",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/sites/groupImport.html",
                controller: "groupSiteController"
            }
        },
        modal: true,
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'new',
        data: {
            displayName: "Grouping Site"
        }
    }).state('customers.addNew.addUsers', {
        url: "/addUsers",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/import/importUsers.html",
                controller: "addUsersController"
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'newManually'
    }).state('customers.customersList.getCustomer.sites.addLocations.manually', {
        url: "/manually",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/sites/addSite.html",
                controller: "newLocationsController"
            }
        },
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'newManually',
        data: {
            displayName: "New Site"
        }
    }).state('customers.customersList.getCustomer.sites.viewSite', {
        url: "/viewSite?siteId",
        views: {
            'right@': {
                templateUrl: "app/views/customers/sites/siteInfo.html",
                controller: "viewSiteController"
            }
        },
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'view',
        data: {
            displayName: "Site Info"
        }
    }).state('customers.customersList.getCustomer.sites.viewSite.activityLog', {
        url: "/activityLog",
        views: {
            'sites-info': {
                templateUrl: "app/views/customers/sites/activityLogList.html",
                controller: "activityLogSiteController"
            }
        },
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'sites',
        activeTabIcon: "fa fa-building",
        activeSubTab: "activityLog",
        actionType: 'view',
        data: {
            displayName: "Activity Log"
        }
    }).state('customers.customersList.getCustomer.sites.viewSite.devices', {
        url: "/devices",
        views: {
            'sites-info': {
                templateUrl: "app/views/customers/devices/devicesList.html",
                controller: "deviceListController"
            }
        },
        activeTab: 'sites',
        activeTabIcon: "fa fa-building",
        activeSubTab: "devices",
        actionType: 'view',
        data: {
            displayName: "Devices"
        }
    }).state('customers.customersList.getCustomer.sites.viewSite.users', {
        url: "/users",
        views: {
            'sites-info': {
                templateUrl: "app/views/users/usersList.html",
                controller: "usersListController"
            }
        },
        activeTab: 'sites',
        activeTabIcon: "fa fa-building",
        activeSubTab: "users",
        actionType: 'view',
        data: {
            displayName: "Users"
        }
    }).state('customers.customersList.getCustomer.sites.viewSite.editLocations', {
        url: "/editLocations",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/sites/editSite.html",
                controller: "editSiteController"
            }
        },
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'change',
        data: {
            displayName: "Edit Site"
        }
    }).state('customers.addNew', {
        url: "/addNew",
        views: {
            'right@': {
                templateUrl: "app/views/customers/newCustomer.html",
                controller: "newCustomerController"
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-user",
        actionType: 'new',
        flagToRedirect: 'customersEdit',
        data: {
            displayName: "New Customer"
        }
    }).state('customers.addNew.addLocations', {
        url: "/addLocations",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/import/import.html",
                controller: "addLocationsController"
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'newManually'
    }).state('customers.addNew.addLocations.manually', {
        url: "/manually",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/sites/addSite.html",
                controller: "newLocationsController"
            }
        },
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'newManually',
        data: {
            displayName: "New Location"
        }
    })
.state('sites.sitesList.addLocations.manually.getStates', {
        url: "/getStates",
        views: {
            'dialog-template@': {
                templateUrl: "app/views/customers/sites/getStates.html",
                controller: "loadStatesCitiesController"
            }
        },
        modal: true,
        resolve: {
            deps: function ($ocLazyLoad) {
                return $ocLazyLoad.load('ui.bootstrap');
            }
        },
        activeTab: 'customers',
        activeTabIcon: "fa fa-building",
        actionType: 'change',
        data: {
            displayName: "New Location"
        }
    })

    /**
     *Routing for Groups module
     *
     */
    .state('customers.customersList.getCustomer.groups', {
            url: "/groups",
            views: {
                'customer-info': {
                    templateUrl: "app/views/customers/groups/groupList.html",
                    controller: "groupListController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('customers.customersList.getCustomer.alerts', {
            url: "/alerts?dashboardAlertStatus",
            views: {
                'customer-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeSubTab: "alerts",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "alerts"
            }
        })
        .state('customers.customersList.getCustomer.groups.newGroup', {
            url: "/newGroup",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "addGroupController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        })
        .state('customers.customersList.getCustomer.groups.editManaully', {
            url: "/editManaully",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/groups/newGroup.html",
                    controller: "newGroupController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "Groups"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo', {
            url: "/groupInfo",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/groupInfo.html",
                    controller: "groupInfoController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'groupInfo',
            data: {
                displayName: "Group Info"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.sites', {
            url: "/sites",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeSubTab: "sites",
            activeTab: 'customers',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        }).state('customers.customersList.getCustomer.groups.groupInfo.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.sites.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.users', {
            url: "/users?isFromGroupSite",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/users/usersList.html",
                    controller: "usersListController"
                }
            },
            activeSubTab: "users",
            activeTab: 'customers',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Users"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.editGroup', {
            url: "/editGroup",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/editGroup.html",
                    controller: "editGroupController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'editGroupDetails',
            data: {
                displayName: "Edit Group"
            }
        }).state('customers.customersList.getCustomer.groups.groupInfo.activitylog', {
            url: "/activitylog",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/groups/activityLogList.html",
                    controller: "groupActivityController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "groups",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        })
        /*end of group routing*/
        .state('fileupload', {
            url: "/fileupload",
            views: {
                'right@': {
                    templateUrl: "app/views/upload/FileUpload.html",
                    controller: "fileUpload"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'groups',
            data: {
                displayName: "Groups"
            }
        }).state('alerts', { // ALERT CODE STARTS HERE................
            url: "/alerts?customerId&fromSource&dashboardAlertStatus&dashboardFilter&dashboardSpecifcId&dashboardAlertId&dashboardTimeInDays&priority",
            activeTab: 'alerts',
            activeTabIcon: 'fa fa-alert',
            defaultSubstate: 'alerts.alertList',
            views: {
                'right@': {
                    templateUrl: "app/views/alert/alertList.html",
                    controller: "alertsController"
                }
            },
            abstract: true,
            data: {
                breadcrumbProxy: 'alerts.alertList'
            }
        }).state('alerts.configuration', {
            url: "/configuration",
            views: {
                'alerts-info': {
                    templateUrl: "app/views/alert/alertConfiguration.html",
                    controller: "configurationController"
                }
            },
            activeTabIcon: 'fa fa-alert',
            activeTab: 'alerts',
            activeSubTab: 'configuration',
            data: {
                displayName: 'configuration'
            }
        }).state('alerts.actionitems', {
            url: "/actionitems",
            views: {
                'alerts-info': {
                    templateUrl: "app/views/alert/actionItems.html",
                    controller: "actionItemsController"
                }
            },
            activeTabIcon: 'fa fa-alert',
            activeTab: 'alerts',
            activeSubTab: 'actionItems',
            data: {
                displayName: 'Action Items'
            }
        }).state('alerts.customerAlerts', {
            url: "/customerAlerts",
            views: {
                'alerts-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeTabIcon: 'fa fa-alert',
            activeTab: 'alerts',
            activeSubTab: 'alerts',
            data: {
                displayName: 'Alerts'
            }
        }).state('alerts.configuration.edit', {
            url: "/edit?alertId",
            views: {
                'right@': {
                    templateUrl: "app/views/alert/alertEditConfiguration.html",
                    controller: "editConfigurationController"
                }
            },
            activeTabIcon: 'fa fa-alert',
            activeTab: 'alerts',
            activeSubTab: 'configuration',
            data: {
                displayName: 'configuration'
            }
        }).state('alerts.configuration.saveConfig', {
            url: "/saveConfig?customerId&alertId",
            views: {
                'right@': {
                    templateUrl: "app/views/alert/alertEditConfiguration.html",
                    controller: "editConfigurationController"
                }
            },
            activeTabIcon: 'fa fa-alert',
            activeTab: 'alerts',
            activeSubTab: 'configuration',
            data: {
                displayName: 'configuration'
            }
        }).state('alerts.alertList.getCustomers', {
            url: "/customerList",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/alert/alertList.html",
                    controller: "customerController"
                }
            },
            activeSubTab: "alerts",
            activeTab: 'alerts',
            activeTabIcon: "fa fa-alerts",
            actionType: 'selectCustomer',
            data: {
                displayName: "alerts"
            }
        }).state('sites', {
            url: "/sites",
            activeTab: 'sites',
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'sites.sitesList'
        }).state('sites.sitesList', {
            url: "/sitesList?sourcePage&customerId&singlePage",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'sites',
            data: {
                displayName: "Sites"
            }
        }).state('sites.sitesList.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('sites.sitesList.groupingSites', {
            url: "/groupingSites",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/groupImport.html",
                    controller: "groupSiteController"
                }
            },
            modal: true,
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Grouping Site"
            }
        }).state('sites.sitesList.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('sites.sitesList.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('sites.sitesList.viewSite.devices', {
            url: "/devices",
            views: {
                'sites-info': {
                    templateUrl: "app/views/customers/devices/devicesList.html",
                    controller: "deviceListController"
                }
            },
            activeSubTab: "devices",
            activeTab: 'sites',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Devices"
            }
        }).state('sites.sitesList.viewSite.forecasts', {
            url: "/forecasts",
            views: {
                'sites-info': {
                    templateUrl: "app/views/customers/devices/deviceProfile/forecasts.html",
                    controller: "forecastsProfileController"
                }
            },
            activeSubTab: "forecasts",
            activeTab: 'sites',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Forecasts"
            }
        }).state('sites.sitesList.viewSite.forecasts.applyView', {
            url: "/applyView",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/devices/deviceProfile/applyForecat.html",
                    controller: "applyForecastsProfileController"
                }
            },
            modal: true,
            activeSubTab: "forecasts",
            activeTab: 'sites',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Forecasts"
            }
        }).state('sites.sitesList.viewSite.alerts', {
            url: "/alerts?dashboardAlertStatus",
            views: {
                'sites-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeSubTab: "alerts",
            activeTab: 'sites',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "alerts"
            }
        }).state('sites.sitesList.viewSite.schedules', {
            url: "/schedules",
            views: {
                'sites-info': {
                    templateUrl: "app/views/scheduler/schedulerList.html",
                    controller: "schedulerListController"
                }
            },
            activeSubTab: "schedules",
            activeTab: 'sites',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        })
        .state('sites.sitesList.viewSite.activityLog', {
            url: "/activityLog",
            views: {
                'sites-info': {
                    templateUrl: "app/views/customers/sites/activityLogList.html",
                    controller: "activityLogSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            activeSubTab: "activityLog",
            actionType: 'view',
            data: {
                displayName: "Activity Log"
            }
        })
        .state('sites.sitesList.viewSite.users', {
            url: "/users",
            views: {
                'sites-info': {
                    templateUrl: "app/views/users/usersList.html",
                    controller: "usersListController"
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            activeSubTab: "users",
            actionType: 'view',
            data: {
                displayName: "Users"
            }
        })
        .state('sites.sitesList.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'change',
            data: {
                displayName: "Edit Site"
            }
        }).state('sites.sitesList.viewSite.editLocations.editStates', {
            url: "/editStates?zipCode",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/editStates.html",
                    controller: "loadStatesCitiesController"
                }
            },
            modal: true,
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'sites',
            activeTabIcon: "fa fa-building",
            actionType: 'change',
            data: {
                displayName: "Edit Site"
            }
        }).state('groups', {
            url: "/groups",
            activeTab: 'groups',
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'groups.groupList'
        }).state('groups.groupList', {
            url: "/groupList?sourcePage&customerId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/groupList.html",
                    controller: "groupListController"
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('groups.groupList.newGroup', {
            url: "/newGroup",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "addGroupController"
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('groups.groupList.newGroup.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/groups/newGroup.html",
                    controller: "newGroupController"
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "Groups"
            }
        }).state('groups.groupList.groupInfo', {
            url: "/groupInfo?groupId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/groupInfo.html",
                    controller: "groupInfoController"
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'groupInfo',
            data: {
                displayName: "Group Info"
            }
        }).state('customers.customersList.getCustomer.schedule', {
            url: "/schedule",
            views: {
                'customer-info': {
                    templateUrl: "app/views/scheduler/schedulerList.html",
                    controller: "schedulerListController"
                }
            },
            activeSubTab: "schedule",
            activeTab: 'customers',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Schedules"
            }
        }).state('groups.groupList.groupInfo.editGroup', {
            url: "/editGroup",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/editGroup.html",
                    controller: "editGroupController"
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'editGroupDetails',
            data: {
                displayName: "Edit Group"
            }
        }).state('customers.customersList.getCustomer.sites.viewSite.schedules', {
            url: "/schedules",
            views: {
                'sites-info': {
                    templateUrl: "app/views/scheduler/schedulerList.html",
                    controller: "schedulerListController"
                }
            },
            activeSubTab: "schedules",
            activeTab: 'customers',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        }).state('groups.groupList.groupInfo.schedules', {
            url: "/schedules",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/scheduler/schedulerList.html",
                    controller: "schedulerListController"
                }
            },
            activeTab: 'groups',
            activeSubTab: "schedules",
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "schedules"
            }
        }).state('groups.groupList.groupInfo.alerts', {
            url: "/alerts?dashboardAlertStatus",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/alert/alerts.html",
                    controller: "customerAlertsController"
                }
            },
            activeTab: 'groups',
            activeSubTab: "alerts",
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "alerts"
            }
        })
        /*.state('groups.groupList.groupInfo.users', {
                    url: "/users",
                    views: {
                        'group-sites-info': {
                            templateUrl: "app/views/users/usersList.html",
                            controller: "usersListController"
                        }
                    },
                    activeSubTab: "users",
                    activeTab: 'groups',
                    actionType: 'view',
                    activeTabIcon: "fa fa-building",
                    data: {
                        displayName: "Users"
                    }
                })*/
        .state('groups.groupList.groupInfo.sites', {
            url: "/sites",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeSubTab: "sites",
            activeTab: 'groups',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        }).state('groups.groupList.groupInfo.devices', {
            url: "/devices",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/devices/devicesList.html",
                    controller: "deviceListController"
                }
            },
            activeSubTab: "devices",
            activeTab: 'groups',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Devices"
            }
        }).state('groups.groupList.groupInfo.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('groups.groupList.groupInfo.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('groups.groupList.groupInfo.sites.viewSite', {
            url: "/viewSite",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('groups.groupList.groupInfo.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('groups.groupList.groupInfo.activitylog', {
            url: "/activitylog",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/groups/activityLogList.html",
                    controller: "groupActivityController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "activities",
            activeTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('groups.groupList.groupInfo.users', {
            url: "/users",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/users/usersList.html",
                    controller: "usersListController"
                }
            },
            activeSubTab: "users",
            activeTab: 'groups',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Users"
            }
        }).state('eaidashboard', {
            url: "/eaidashboard?customer&group&site&time",
            views: {
                'right@': {
                    templateUrl: "app/views/dashboard/dashboard.html",
                    controller: "dashboardController"
                }
            },
            activeTabIcon: 'fa fa-tachometer',
            activeTab: 'dashboard',
            data: {
                displayName: "Dashboard"
            }
        }).state('report', {
            url: "/report?reportType&customer&group&site&time&device&oldType",
            activeTab: 'report',
            views: {
                'right@': {
                    templateUrl: "app/views/reports/mainReports.html",
                    controller: "reportMainController"
                }
            },
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'report.reports'
        }).state('report.reports', {
            url: "/reports",
            views: {
                'report-info': {
                    templateUrl: "app/views/reports/reports.html",
                    controller: "reportController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTabIcon: 'fa fa-building',
            activeSubTab: "reports",
            activeTab: 'reports',
            data: {
                displayName: "Reports"
            }
        }).state('report.customers', {
            url: "/customers",
            views: {
                'report-info': {
                    templateUrl: "app/views/reports/customers.html",
                    controller: "customerReportsController"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeSubTab: "customers",
            activeTab: 'reports',
            data: {
                displayName: "Reports"
            }
        }).state('report.groups', {
            url: "/groups",
            views: {
                'report-info': {
                    templateUrl: "app/views/reports/groups.html",
                    controller: "groupReportsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTabIcon: 'fa fa-building',
            activeSubTab: "groups",
            activeTab: 'reports',
            data: {
                displayName: "Groups"
            }
        }).state('report.sites', {
            url: "/sites",
            views: {
                'report-info': {
                    templateUrl: "app/views/reports/sites.html",
                    controller: "siteReportsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTabIcon: 'fa fa-building',
            activeSubTab: "sites",
            activeTab: 'reports',
            data: {
                displayName: "Sites"
            }
        }).state('report.devices', {
            url: "/devices",
            views: {
                'report-info': {
                    templateUrl: "app/views/reports/devices.html",
                    controller: "deviceReportsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTabIcon: 'fa fa-building',
            activeSubTab: "devices",
            activeTab: 'reports',
            data: {
                displayName: "Devices"
            }
        }).state('pdfreport', {
	       	 url: "/pdfreport",
	         activeTab: 'pdfreports',
	         activeTabIcon: 'fa fa-building',
	         defaultSubstate: 'pdfreport.reportlist',
	         abstract: true,
	         data: {
	             breadcrumbProxy: 'pdfreport.reportList'
	         }
	    }).state('pdfreport.reportList', {
	        url: "/reportList",
	        views: {
	            'right@': {
	                templateUrl: "app/views/pdfreport/reportList.html",
	                controller: "reportListController"
	            }
	        },
	        resolve: {
	            deps: function ($ocLazyLoad) {
	                return $ocLazyLoad.load('ui.bootstrap');
	            }
	        },
	        activeTabIcon: 'fa fa-building',
	        activeTab: 'pdfreports',
	        data: {
	            displayName: "Reports"
	        }
	    }).state('activitylog', {
            url: "/activitylog",
            activeTab: 'activities',
            abstract: true,
            activeTabIcon: "fa fa-pencil-square-o",
            defaultSubstate: 'activitylog.activityLogList'
        }).state('activitylog.activityLogList', {
            url: "/activityLogList",
            views: {
                'right@': {
                    templateUrl: "app/views/activitylog/activityLogList.html",
                    controller: "activityLogListController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTabIcon: 'fa fa-pencil-square-o',
            activeTab: 'activities',
            data: {
                displayName: "Activity Log"
            }
        }).state('activitylog.activityLogList.addNew', {
            url: "/addNew",
            views: {
                'right@': {
                    templateUrl: "app/views/activitylog/newActivityLog.html",
                    controller: "newactivityLogController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'activities',
            activeTabIcon: "fa fa-pencil-square-o",
            actionType: 'new',
            data: {
                displayName: "New Activity Log"
            }
        }).state('scheduler', {
            url: "/scheduler?sourcePage&customerId&groupId&siteId",
            activeTab: 'scheduler',
            abstract: true,
            activeTabIcon: "fa fa-building",
            defaultSubstate: 'scheduler.schedulerList'
        }).state('scheduler.schedulerList', {
            url: "/schedulerList",
            views: {
                'right@': {
                    templateUrl: "app/views/scheduler/schedulerList.html",
                    controller: "schedulerListController"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            data: {
                displayName: "Schedules"
            }
        }).state('scheduler.schedulerList.applyView', {
            url: "/applyView?seheduleId",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/scheduler/applyView.html",
                    controller: "applyViewController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            data: {
                displayName: "Schedules"
            }
        }).state('customers.customersList.getCustomer.schedule.applyView', {
            url: "/applyView?seheduleId",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/scheduler/applyView.html",
                    controller: "applyViewController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'customers',
            data: {
                displayName: "Schedules"
            }
        }).state('groups.groupList.groupInfo.schedules.applyView', {
            url: "/applyView?seheduleId",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/scheduler/applyView.html",
                    controller: "applyViewController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'groups',
            data: {
                displayName: "Schedules"
            }
        }).state('sites.sitesList.viewSite.schedules.applyView', {
            url: "/applyView?seheduleId",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/scheduler/applyView.html",
                    controller: "applyViewController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'sites',
            data: {
                displayName: "Schedules"
            }
        }).state('scheduler.addSchedule', {
            url: "/addschedule?from",
            views: {
                'right@': {
                    templateUrl: "app/views/scheduler/addSchedule.html",
                    controller: "addScheduleController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css', 'ui.bootstrap']);

                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            actionType: 'new',
            data: {
                displayName: "Schedules"
            }
        }).state('scheduler.schedulerList.getSchedule', {
            url: "/getSchedule?scheduleId",
            views: {
                'right@': {
                    templateUrl: "app/views/scheduler/scheduleInfo.html",
                    controller: "scheduleInfoController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css']);

                }
            },
            activeSubTab: "scheduler",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-user",
            actionType: 'view',
            data: {
                displayName: "Schedule Info"
            }
        }).state('scheduler.schedulerList.getSchedule.editSchedule', {
            url: "/editSchedule",
            views: {
                'right@': {
                    templateUrl: "app/views/scheduler/editSchedule.html",
                    controller: "editScheduleController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['ui.bootstrap']);
                }
            },
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-user",
            actionType: 'edit',
            data: {
                displayName: "Edit Schedule"
            }
        }).state('scheduler.schedulerList.copySchedule', {
            url: "/copySchedule?scheduleId",
            views: {
                'right@': {
                    templateUrl: "app/views/scheduler/copySchedule.html",
                    controller: "copyScheduleController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css', 'ui.bootstrap']);

                }
            },
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-user",
            actionType: 'edit',
            data: {
                displayName: "Copy Schedule"
            }
        }).state('scheduler.schedulerList.getSchedule.scheduler', {
            url: "/scheduler",
            views: {
                'scheduler-view-info': {
                    templateUrl: "app/views/scheduler/viewSchedule.html",
                    controller: "viewScheduleController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['assets/utils/calendar/fullcalendar.min.js', 'assets/utils/calendar/fullcalendar.css']);

                }
            },
            activeSubTab: "scheduler",
            activeTab: 'scheduler',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Scheduler"
            }
        }).state('scheduler.schedulerList.getSchedule.scheduler.applyView', {
            url: "/applyView?seheduleId",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/scheduler/applyView.html",
                    controller: "applyViewController"
                }
            },
            modal: true,
            activeTabIcon: 'fa fa-building',
            activeTab: 'scheduler',
            data: {
                displayName: "Schedules"
            }
        }).state('scheduler.schedulerList.getSchedule.groups', {
            url: "/groups",
            views: {
                'scheduler-view-info': {
                    templateUrl: "app/views/customers/groups/groupList.html",
                    controller: "groupListController"
                }
            },
            activeSubTab: "groups",
            activeTab: 'scheduler',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Groups"
            }
        }).state('scheduler.schedulerList.getSchedule.devices', {
            url: "/devices",
            views: {
                'scheduler-view-info': {
                    templateUrl: "app/views/customers/devices/devicesList.html",
                    controller: "deviceListController"
                }
            },
            activeSubTab: "devices",
            activeTab: 'scheduler',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Devices"
            }
        }).state('scheduler.schedulerList.getSchedule.sites', {
            url: "/sites",
            views: {
                'scheduler-view-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeSubTab: "sites",
            activeTab: 'scheduler',
            actionType: 'view',
            activeTabIcon: "fa fa-building",
            data: {
                displayName: "Sites"
            }
        }).state('scheduler.schedulerList.getSchedule.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeSubTab: "sites",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('scheduler.schedulerList.getSchedule.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'scheduler',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('scheduler.schedulerList.getSchedule.sites.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('scheduler.schedulerList.getSchedule.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('scheduler.schedulerList.getSchedule.sites.groupingSites', {
            url: "/groupingSites",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/groupImport.html",
                    controller: "groupSiteController"
                }
            },
            modal: true,
            activeTab: 'scheduler',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Grouping Site"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.newGroup', {
            url: "/newGroup",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "addGroupController"
                }
            },
            activeTab: 'scheduler',
            activeSubTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.newGroup.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/groups/newGroup.html",
                    controller: "newGroupController"
                }
            },
            activeTab: 'scheduler',
            activeSubTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "Groups"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo', {
            url: "/groupInfo",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/groupInfo.html",
                    controller: "groupInfoController"
                }
            },
            activeTab: 'scheduler',
            activeSubTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'groupInfo',
            data: {
                displayName: "Group Info"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.editGroup', {
            url: "/editGroup?groupId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/editGroup.html",
                    controller: "editGroupController"
                }
            },
            activeTab: 'scheduler',
            activeSubTab: 'groups',
            activeTabIcon: "fa fa-building",
            actionType: 'editGroupDetails',
            data: {
                displayName: "Edit Group"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo.sites', {
            url: "/sites?groupId",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeSubTab: "sites",
            activeTab: 'scheduler',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeSubTab: "sites",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo.sites.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('scheduler.schedulerList.getSchedule.groups.groupInfo.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'scheduler',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('users.usersList.userProfile.groups', {
            url: "/groups",
            views: {
                'userProfile-info': {
                    templateUrl: "app/views/customers/groups/groupList.html",
                    controller: "groupListController"
                }
            },
            activeTab: 'users',
            activeSubTab: "groups",
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('users.usersList.userProfile.groups.newGroup', {
            url: "/newGroup",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "addGroupController"
                }
            },
            activeTab: 'users',
            activeSubTab: "groups",
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Groups"
            }
        }).state('users.usersList.userProfile.groups.newGroup.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/groups/newGroup.html",
                    controller: "newGroupController"
                }
            },
            activeTab: 'users',
            activeSubTab: "groups",
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "Groups"
            }
        }).state('users.usersList.userProfile.groups.groupInfo', {
            url: "/groupInfo",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/groupInfo.html",
                    controller: "groupInfoController"
                }
            },
            activeTab: 'users',
            activeSubTab: "groups",
            activeTabIcon: "fa fa-building",
            actionType: 'groupInfo',
            data: {
                displayName: "Group Info"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.editGroup', {
            url: "/editGroup?groupId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/groups/editGroup.html",
                    controller: "editGroupController"
                }
            },
            activeTab: 'users',
            activeSubTab: "groups",
            activeTabIcon: "fa fa-building",
            actionType: 'editGroupDetails',
            data: {
                displayName: "Edit Group"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.sites', {
            url: "/sites?groupId",
            views: {
                'group-sites-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeSubTab: "sites",
            activeTab: 'users',
            actionType: 'groupInfoSites',
            activeTabIcon: "fa fa-building"
        }).state('users.usersList.userProfile.sites', {
            url: "/sites",
            views: {
                'userProfile-info': {
                    templateUrl: "app/views/customers/sites/siteList.html",
                    controller: "siteListController"
                }
            },
            activeTabIcon: 'fa fa-building',
            activeTab: 'users',
            activeSubTab: "sites",
            data: {
                displayName: "Sites"
            }
        }).state('users.usersList.userProfile.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('users.usersList.userProfile.sites.groupingSites', {
            url: "/groupingSites",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/groupImport.html",
                    controller: "groupSiteController"
                }
            },
            modal: true,
            activeTab: 'users',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Grouping Site"
            }
        }).state('users.usersList.userProfile.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'users',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('users.usersList.userProfile.sites.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'users',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('users.usersList.userProfile.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeTab: 'users',
            activeSubTab: "sites",
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.sites.addLocations', {
            url: "/addLocations",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/import/import.html",
                    controller: "newSiteController"
                }
            },
            modal: true,
            activeSubTab: "sites",
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "New Site"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.sites.addLocations.manually', {
            url: "/manually",
            views: {
                'dialog-template@': {
                    templateUrl: "app/views/customers/sites/addSite.html",
                    controller: "newLocationsController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            actionType: 'newManually',
            data: {
                displayName: "New Site"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.sites.viewSite', {
            url: "/viewSite?siteId",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/siteInfo.html",
                    controller: "viewSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            actionType: 'view',
            data: {
                displayName: "Site Info"
            }
        }).state('users.usersList.userProfile.groups.groupInfo.sites.viewSite.editLocations', {
            url: "/editLocations",
            views: {
                'right@': {
                    templateUrl: "app/views/customers/sites/editSite.html",
                    controller: "editSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
            activeSubTab: "sites",
            activeTab: 'users',
            activeTabIcon: "fa fa-building",
            actionType: 'new',
            data: {
                displayName: "Edit Site"
            }
        }).state('bulkImport', {
            url: "/bulkImport",
            activeTab: 'bulkImport',
            views: {
                'right@': {
                    templateUrl: "app/views/bulkimport/mainImport.html",
                    controller: "bulkImportMainController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('app/views/bulkimport/bulkImportController.js');
                }
            }
        }).state('bulkImport.sites', {
            url: "/sites",
            activeTab: "bulkImport",
            activeSubTab: "sites",
            views: {
                'bulk-upload-info': {
                    templateUrl: "app/views/bulkimport/sites.html",
                    controller: "bulkImportSiteController"
                }
            },
            resolve: {
                deps: function ($ocLazyLoad) {
                    return $ocLazyLoad.load('ui.bootstrap');
                }
            },
        })
        /**
         *Default routing
         */
    $urlRouterProvider.otherwise("/eaidashboard?customer=0&group=0&site=0&time=7");
});