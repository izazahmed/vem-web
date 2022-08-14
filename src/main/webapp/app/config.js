/** 
 *@file : config 
 *
 *@config :Load modules for application 
 *
 *@Application Name : EAI
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
 * 02       22-08-2016  Nagaraju SVP Goli           Added toastr provider
 *
 */
var skipChecking = false;
var app = angular.module('EAI', ['ui.router', 'ngMessages', 'cfp.loadingBar', 'disableAll', 'ngAnimate', 'toastr', 'angularUtils.directives.uiBreadcrumbs', 'ui.select', 'ngSanitize', 'ui.mask', 'ui.numeric', 'mdo-angular-cryptography', 'oc.lazyLoad', 'angularjs-dropdown-multiselect', 'ngMask', 'ng-fastclick', 'datatables', 'datatables.bootstrap', 'bootstrapLightbox', 'ui.bootstrap'])
    .run(function ($state, $rootScope, cfpLoadingBar, $timeout, Lightbox) {
        FastClick.attach(document.body);
        $rootScope.toggle = {};
        var timezone = jstz.determine();
        var timezoneName = timezone.name();
        eaiDetails.timeZone = timezoneName;
        $rootScope.newAlertCount = '';
        $rootScope.customeTabCounts = {};
        $rootScope.userDetails = eaiDetails;
        $rootScope.appContext = appContext;
        $rootScope.apiRequestPool = [];
        $rootScope.apiRunning = [];
        $rootScope.apiServices = {};
        $rootScope.$state = $state;

        $rootScope.pdfFlag = pdfFlag;
        $rootScope.pdfValue = pdfValue;

        $rootScope.getObjectLength = function (obj) {
            if (obj) {
                return Object.keys(obj).length;
            }
        }
        $rootScope.toJson = function (obj) {

            return JSON.parse(obj);
        }
        $rootScope.timeKeydownPrevent = function ($event) {

            if ($($event.target).attr("ng-model") == "minutes") {

                var keyCode = $event.which || $event.keyCode;
                
                if ($($event.target).val() == "0" && keyCode != 8 && keyCode != 46 && keyCode == 51) {
                    $event.preventDefault();
                }
                if ($($event.target).val() == 3 && keyCode != 8 && keyCode != 46 && keyCode != 48) {
                    $event.preventDefault();
                }
                if (keyCode != 51 && keyCode != 48 && keyCode != 8 && keyCode != 46) {
                    $event.preventDefault();
                }
            }
        };

        $(document).keydown(function ($event) {
            
            var keyCode = $event.which || $event.keyCode;
            
            if (keyCode == 9) {
                loadProperTimePoints();
            }

        });

        $(document).mousedown(function () {
            loadProperTimePoints();
        });

        function loadProperTimePoints() {


            var appElement = document.querySelector('[name=addScheduleForm]') || document.querySelector('[name=updateScheduleForm]');
            if (!appElement) {
                return;
            }
            setTimeout(function () {
                var $scope = angular.element(appElement).scope();
                $scope.$apply(function () {

                    $.each($scope.schedule.timePoints, function (key, value) {
                        
                        if (value.time && (moment(value.time).format("mm") != "00" && moment(value.time).format("mm") != "30")) {
                            $scope.schedule.timePoints[key].time = new Date($scope.schedule.timePoints[key].time).setMinutes(0);
                        }
                    })
                });
            }, 100);




        }

        $rootScope.getFirst = function (str) {
            return str.split(",")[0];
        }
        $rootScope.isProperty = function (key, obj) {
            if (key && obj) {
                return obj.hasOwnProperty(key);
            }
        }
        $rootScope.mergeObject = function (a, b) {

            var first = angular.copy(a);

            var second = angular.copy(b);

            return $.extend(first, second);
        }

        $rootScope.aphaNumSort = function (obj, category) {
            obj.sort(function (a, b) {
                // Normalize the file names with fixed-
                // width numeric data.

                switch (category) {
                    case 'customer':
                        a = a.companyName.toLowerCase();
                        b = b.companyName.toLowerCase();
                        break;
                    case 'group':
                        a = a.groupName.toLowerCase();
                        b = b.groupName.toLowerCase();
                        break;
                    case 'customerLabel':
                        a = a.label.toLowerCase();
                        b = b.label.toLowerCase();
                        break;
                    case 'site':
                        a = a.siteName.toLowerCase();
                        b = b.siteName.toLowerCase();
                        break;
                    case 'device':
                        a = a.deviceName.toLowerCase();
                        b = b.deviceName.toLowerCase();
                        break;
                    case 'schedule':
                        a = a.scheduleName.toLowerCase();
                        b = b.scheduleName.toLowerCase();
                        break;
                    case 'user':
                        a = a.firstName.toLowerCase() + " " + a.lastName.toLowerCase();
                        b = b.firstName.toLowerCase() + " " + b.lastName.toLowerCase();
                        break;
                    case 'groupLabel':
                        a = a.lable.toLowerCase();
                        b = b.lable.toLowerCase();
                        break;
                    case 'role':
                        a = a.label.toLowerCase();
                        b = b.label.toLowerCase();
                        break;
                    case 'deviceLable':
                        a = a.name.toLowerCase();
                        b = b.name.toLowerCase();
                        break;
                    case 'name':
                        a = a.name.toLowerCase();
                        b = b.name.toLowerCase();
                        break;
                    case 'devicename':
                        a = a.name.toLowerCase();
                        b = b.name.toLowerCase();
                        break;
                    case 'city':
                        a = a.cityName.toLowerCase();
                        b = b.cityName.toLowerCase();
                        break;
                    case 'state':
                        a = a.stateName.toLowerCase();
                        b = b.stateName.toLowerCase();
                        break;
                    case 'customername':
                        a = a.customerName.toLowerCase();
                        b = b.customerName.toLowerCase();
                        break;
                    case 'sitelabel':
                        a = a.label.toLowerCase();
                        b = b.label.toLowerCase();
                        break;
                    case 'schedulelabel':
                        a = a.label.toLowerCase();
                        b = b.label.toLowerCase();
                        break;
                    case 'lable':
                        a = a.lable.toLowerCase();
                        b = b.lable.toLowerCase();
                        break;
                    case 'label':
                        a = a.label.toLowerCase();
                        b = b.label.toLowerCase();
                        break;
                    default:
                        ""
                }


                var aMixed = normalizeMixedDataValue(a);
                var bMixed = normalizeMixedDataValue(b);
                return (aMixed < bMixed ? -1 : 1);
            });
            return obj;
        }

        // I take a value and try to return a value in which
        // the numeric values have a standardized number of
        // leading and trailing zeros. This *MAY* help makes
        // an alphabetic sort seem more natural to the user's
        // intent.
        function normalizeMixedDataValue(value) {
            var padding = "000000000000000";
            // Loop over all numeric values in the string and
            // replace them with a value of a fixed-width for
            // both leading (integer) and trailing (decimal)
            // padded zeroes.
            value = value.replace(
                /(\d+)((\.\d+)+)?/g,
                function ($0, integer, decimal, $3) {
                    // If this numeric value has "multiple"
                    // decimal portions, then the complexity
                    // is too high for this simple approach -
                    // just return the padded integer.
                    if (decimal !== $3) {
                        return (
                            padding.slice(integer.length) +
                            integer +
                            decimal
                        );
                    }
                    decimal = (decimal || ".0");
                    return (
                        padding.slice(integer.length) +
                        integer +
                        decimal +
                        padding.slice(decimal.length)
                    );
                }
            );
            return (value);
        }

        $rootScope.customPhonePattern = function (phoneNumber) {
            var formattedString = '';
            if (phoneNumber) {
                phoneNumber = phoneNumber.replace(/-/g, "");
                formattedString = '(' + phoneNumber.substring(0, 3) + ') ' + phoneNumber.substring(3, 6) + '-' + phoneNumber.substring(6, 10);
            }
            return formattedString;
        }

        $rootScope.getObjectMergeLength = function (obj) {
            if (obj) {
                var arr = [];
                $.each(Object.keys(obj), function (key, value) {
                    arr = $.merge(arr, obj[value]);
                });
                return arr.length;
            } else {
                return 0;
            }
        };
        $rootScope.checkDeviceIsOnline = function (obj) {

            if (obj && obj.thingState) {
                var diff = moment(obj.currentUTCDateTime).diff($rootScope.formateDate(obj.thingState.datetime), 'minutes');
                return diff < 30;
            }

            return false;
        }
        $rootScope.checkDeviceIsOnlineSite = function (obj) {

            if (obj) {
                var diff = moment(obj.currentUTCDateTime).diff($rootScope.formateDate(obj.dateTime), 'minutes');
                return diff < 30;
            }
            return false;
        }
        $rootScope.makeRound = function (value) {

            return Math.round(Number(value));

        }
        $rootScope.checkSplitMore = function (value, splitWith) {

            return value.split(splitWith).length;

        }
        $rootScope.makeFormat = function (time, format) {
            return moment(time).format(format);
        }
        $rootScope.formateDate = function (date) {
            if (date)
                return date.split("+")[0];

            //return moment(date).format("MM-DD-YYYY HH:mm")
        };
        $rootScope.getSetDetails = function (data) {


            if (data && data.thingState && data.thingState.relay_state) {

                if (data.thingState.relay_state.relay2 == 'ON' || data.thingState.relay_state.relay6 == 'ON' || data.thingState.relay_state.relay7 == 'ON') {

                    return data.thingState.cool_set;
                }

                if (data.thingState.relay_state.relay1 == 'ON' || data.thingState.relay_state.relay4 == 'ON' || data.thingState.relay_state.relay5 == 'ON') {

                    return data.thingState.heat_set;
                }



            }
        }
        $rootScope.getSetAtLabel = function (data) {

            if (data && data.thingState && data.thingState.relay_state) {

                if (data.thingState.relay_state.relay2 == 'ON' || data.thingState.relay_state.relay6 == 'ON' || data.thingState.relay_state.relay7 == 'ON') {

                    return data.coolSetAt == 1;
                }

                if (data.thingState.relay_state.relay1 == 'ON' || data.thingState.relay_state.relay4 == 'ON' || data.thingState.relay_state.relay5 == 'ON') {

                    return data.heatSetAt == 1;
                }

            }

        }
        $rootScope.to12Hr = function (time, skipSpan) {
            if (time) {
                return moment(time, ["HH:mm"]).format("hh:mm A");
                /*   time = time.split(":").join("");
                   var hours = time[0] + time[1];
                   var min = time[2] + time[3];
                   if (hours < 12) {
                       if (skipSpan) {
                           return hours + ':' + min + ' AM';
                       }
                       return hours + ':' + min + '<span"> AM</span>';
                   } else {
                       hours = hours - 12;
                     
                       hours = (hours.length < 10) ? '0'+hours.toString() : hours;
                       // hours = (hours.length > 1) ? hours : "0"+hours;
                       if (skipSpan) {
                           return hours + ':' + min + ' PM';
                       }
                      
                       return hours + ':' + min + '<span> PM</span>';
                   }*/
            }
        }
        $rootScope.getLocalTime = function (time) {
            return moment.tz(time, $rootScope.userDetails.timeZone).format("MM/DD/YYYY HH:mm:ss") + " " + moment.tz(time, $rootScope.userDetails.timeZone).format("z");

        }
        $rootScope.getTimeZoneTime = function (time, timeZone) {
            return moment.tz(time, timeZone).format("MM/DD/YYYY HH:mm:ss") + " " + moment.tz(time, timeZone).format("z");

        }
        $rootScope.tConvert = function (h_24) {
            var h = h_24 % 12;
            if (h === 0) h = 12;
            return h + (h_24 < 12 ? ' AM' : ' PM');
        }
        $rootScope.checkIsObject = function (obj) {
            if (obj) {
                return angular.isObject(obj);
            }
        }
        $rootScope.useCopy = function (a) {

            return angular.copy(a);
        }
        $rootScope.getWordSchedule = function (text) {

            if (text && text.toLowerCase().indexOf("schedule") != -1) {
                return text;
            }

            return text + " Schedule";
        }
        $rootScope.isTouchDevice = function () {
            return (('ontouchstart' in window) || (navigator.MaxTouchPoints > 0) || (navigator.msMaxTouchPoints > 0));
        }

        $rootScope.$on('$stateChangeSuccess', function (event, to, toParams, from, fromParams) {
            $rootScope.$previousState = from;
            loadSingleCustomerLogo();
        });
        loadSingleCustomerLogo();

        function loadSingleCustomerLogo() {
            if ($rootScope.userDetails.companyLogo) {
                $timeout(function () {

                    if (!$(".blue-bg-bar").eq(0).find(".one-customer")[0] && !$(".blue-bg-bar").eq(0).find(".customer-thumg")[0] && !$(".blue-bg-bar").eq(0).find(".mobile-min-700")[0] && !$(".blue-bg-bar").eq(0).find(".mobile-min-700").find(".customer-thumg")[0]) {
                        if ($state.current.name == 'devices.devicesList') {
                            $(".blue-bg-bar").eq(0).find("li").before($(".one-customer").eq(0).clone());
                            return;
                        }
                        $(".blue-bg-bar").eq(0).prepend($(".one-customer").eq(0).clone());
                    }
                }, 1000);
            }

        }
        $rootScope.$on('$stateChangeStart',
            function (event, toState, toParams, fromState, fromParams, options) {

                if (toState.name === "customers.customersList.getCustomer.sites") {
                    $("#modelDialog").removeClass("fade");
                    $("#modelDialog").modal("hide");
                    $("#modelDialog").addClass("fade");
                    return;
                }
                if (!toState.modal && fromState.actionType !== "edit" && fromState.actionType !== "new") {
                    $("#modelDialog").removeClass("fade");
                    $("#modelDialog").modal("hide");
                    $("#modelDialog").addClass("fade");
                    return;
                }
                if (skipChecking) {
                    skipChecking = false;
                    return;
                }
                /*    if (fromState.actionType === "edit" || (fromState.actionType === "new" && toState.actionType !== "newManually")) {
                        event.preventDefault();
                        $('#modelDialog').on('show.bs.modal', function (event) {
                            var modal = $(this);
                            modal.find('.modal-title').text('Discard changes');
                            modal.find('.model-content').text('Are you sure you want to discard the changes?');
                        });
                        $("#modelDialog").modal("show");
                        event.targetScope.accept = function () {
                            $("#modelDialog").modal("hide");
                            var params = angular.copy(toParams);
                            params.skipSomeAsync = true;
                            skipChecking = true;
                            $state.go(toState.name, params);
                            $rootScope.$broadcast('discarted');
                        }
                    }*/
                /* event.preventDefault();*/
                // transitionTo() promise will be rejected with 
                // a 'transition prevented' error




            });



        $rootScope.gotoParent = function () {


            if (!$state.params.sourcePage) {
                history.back();
            }

            switch ($state.params.sourcePage.split("-")[0]) {

                case "sites":


                    var siteId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + siteId;

                    if ($state.current.name.indexOf("sites.sitesList.viewSite") != -1 && $state.params.siteId == siteId) {
                        $state.go("sites.sitesList", $state.params);
                        return;
                    }

                    if ($state.current.name.indexOf("sites.sitesList.viewSite") != -1 && $state.params.siteId != siteId) {
                        $state.go($state.current, $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            sourcePage: sourcePage
                        }), {
                            reload: true
                        });
                        return;
                    }


                    if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {

                        $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            sourcePage: sourcePage
                        }));

                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {

                        $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            sourcePage: sourcePage
                        }));

                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                        $state.go("sites.sitesList.viewSite.users", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            sourcePage: sourcePage
                        }));
                    } else {
                        $state.go("sites.sitesList.viewSite.activityLog", $rootScope.mergeObject($state.params, {
                            siteId: siteId,
                            sourcePage: sourcePage
                        }));
                    }



                    break;
                case "devices":


                    var deviceId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + deviceId;

                    if ($state.current.name.indexOf("devices.devicesList.profile") != -1) {
                        $state.go("devices.devicesList", {
                            customerId: $state.params.customerId,
                            sourcePage: "devices"
                        });
                        return;
                    }

                    $state.go("devices.devicesList.profile.settings", $rootScope.mergeObject($state.params, {
                        sourcePage: sourcePage,
                        deviceId: deviceId
                    }));


                    break;
                case "customers":

                    var customerId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + customerId;





                    if ($state.current.name.indexOf("customers.customersList.getCustomer") != -1 && $state.params.customerId == customerId) {
                        $state.go("customers.customersList", $state.params);
                        return;
                    }

                    if ($state.current.name.indexOf("customers.customersList.getCustomer") != -1 && $state.params.customerId != customerId) {
                        $state.go($state.current, $rootScope.mergeObject($state.params, {
                            customerId: customerId,
                            sourcePage: sourcePage
                        }), {
                            reload: true
                        });
                        return;
                    }




                    if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Group Management')) {
                        $state.go("customers.customersList.getCustomer.groups", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {
                        $state.go("customers.customersList.getCustomer.sites", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                        $state.go("customers.customersList.getCustomer.devices", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                        $state.go("customers.customersList.getCustomer.schedule", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('User Management')) {
                        $state.go("customers.customersList.getCustomer.users", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    } else {
                        $state.go("customers.customersList.getCustomer.activityLog", {
                            customerId: customerId,
                            sourcePage: sourcePage
                        });
                    }

                    break;
                case "groups":

                    /*  
                      $state.go("groups.groupList", {
                          customerId: $state.params.customerId,
                          sourcePage: "groups"
                      });*/

                    var groupId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + groupId;

                    if ($state.current.name.indexOf("groups.groupList.groupInfo") != -1 && $state.params.groupId == groupId) {
                        $state.go("groups.groupList", $state.params);

                        return;
                    }

                    if ($state.current.name.indexOf("groups.groupList.groupInfo") != -1 && $state.params.groupId != groupId) {
                        $state.go($state.current, $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }), {
                            reload: true
                        });
                        return;
                    }

                    if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Site Management')) {

                        $state.go("groups.groupList.groupInfo.sites", $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }));

                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {

                        $state.go("groups.groupList.groupInfo.devices", $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }));

                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {

                        $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }));

                    } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {

                        $state.go("groups.groupList.groupInfo.schedules", $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }));

                    } else {

                        $state.go("groups.groupList.groupInfo.alerts", $rootScope.mergeObject($state.params, {
                            groupId: groupId,
                            sourcePage: sourcePage
                        }));

                    }

                    break;
                case "schedules":


                    var scheduleId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + scheduleId;



                    if ($state.current.name.indexOf("scheduler.schedulerList.getSchedule") != -1 && $state.params.scheduleId == scheduleId) {



                        $state.go("scheduler.schedulerList", {
                            sourcePage: "schedules"
                        });

                        return;

                    }

                    if ($state.current.name.indexOf("scheduler.schedulerList.getSchedule") != -1 && $state.params.scheduleId != scheduleId) {



                        $state.go($state.current, $rootScope.mergeObject($state.params, {
                            scheduleId: scheduleId,
                            sourcePage: sourcePage
                        }), {
                            reload: true
                        });


                        return;

                    }

                    $state.go("scheduler.schedulerList.getSchedule.scheduler", $rootScope.mergeObject($state.params, {
                        scheduleId: scheduleId,
                        sourcePage: sourcePage
                    }));


                    break;
                case "users":


                    var userId = $state.params.sourcePage.split("-")[1];
                    var sourcePage = $state.params.sourcePage.split("-")[0] + "-" + userId;


                    if ($state.current.name.indexOf("users.usersList") != -1 && $state.params.userId == userId) {


                        $state.go("users.usersList", {
                            sourcePage: "users"
                        });

                        return;

                    }

                    if ($state.current.name.indexOf("users.usersList") != -1 && $state.params.userId != userId) {



                        $state.go($state.current, $rootScope.mergeObject($state.params, {
                            userId: userId,
                            sourcePage: sourcePage
                        }), {
                            reload: true
                        });


                        return;

                    }



                    if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Customer Management')) {

                        $state.go("users.usersList.userProfile.customers", $rootScope.mergeObject($state.params, {
                            userId: userId,
                            sourcePage: sourcePage
                        }));
                    } else {
                        $state.go("users.usersList.userProfile.activityLog", $rootScope.mergeObject($state.params, {
                            userId: userId,
                            sourcePage: sourcePage
                        }));
                    }



                    break;

                case "dashboard":


                    var customerId = $rootScope.userDetails.custSiteIds.split("~")[0] ? $rootScope.userDetails.custSiteIds.split("~")[0] : 0;
                    var siteId = $rootScope.userDetails.custSiteIds.split("~")[1] ? $rootScope.userDetails.custSiteIds.split("~")[1] : 0;
                    if (Number($rootScope.userDetails.siteCount) > 1 || Number($rootScope.userDetails.siteCount) === 0) {
                        $state.go("sites.sitesList", $rootScope.mergeObject($state.params, {
                            customerId: -1,
                            sourcePage: 'dashboard'
                        }));
                    } else {
                        if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Device Management')) {
                            $state.go("sites.sitesList.viewSite.devices", $rootScope.mergeObject($state.params, {
                                siteId: siteId,
                                customerId: customerId,
                                sourcePage: 'dashboard',
                                singlePage: 'true'
                            }));
                        } else if ($rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                            $state.go("sites.sitesList.viewSite.schedules", $rootScope.mergeObject($state.params, {
                                siteId: siteId,
                                customerId: customerId,
                                sourcePage: 'dashboard',
                                singlePage: 'true'
                            }));
                        } else if (!$rootScope.userDetails.rolePermissions.hasOwnProperty('Schedule Management')) {
                            $state.go("sites.sitesList.viewSite.forecasts", $rootScope.mergeObject($state.params, {
                                siteId: siteId,
                                customerId: customerId,
                                sourcePage: 'dashboard',
                                singlePage: 'true'
                            }));
                        }
                    }

                    break;



                default:
                    history.back();
                    break;


            }


        }

        var rtime;
        var timeout = false;
        var delta = 200;
        resizeend();
        $(window).resize(function () {
            rtime = new Date();
            if (timeout === false) {
                timeout = true;
                setTimeout(resizeend, delta);
            }
        });

        function resizeend() {
            if (new Date() - rtime < delta) {
                setTimeout(resizeend, delta);
            } else {
                timeout = false;
                if (window.innerWidth < 1025) {
                    $rootScope.toggle.leftMenuArea = true;
                } else {
                    $rootScope.toggle.leftMenuArea = false;
                }

            }
        }
        $rootScope.openLightboxModal = function (img) {
            if (img != undefined || img != null) {
                Lightbox.openModal([img], 0);
            }
        };



    }).config(function (toastrConfig) {
        /**
         *facilitates the centralized message configuration 
         *@param toastrConfig 
         */
        angular.extend(toastrConfig, {
            progressBar: true,
            closeButton: true,
            timeOut: 5000,
            allowHtml: true,
            positionClass: 'toast-top-center'
        });
    })
    /*.config(['uiMask.ConfigProvider', function(uiMaskConfigProvider) {
      uiMaskConfigProvider.maskDefinitions({ '*': /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/});
      uiMaskConfigProvider.clearOnBlur(false);
      uiMaskConfigProvider.eventsToHandle(['input', 'keyup', 'click']);
    }])*/
    .config(function ($ocLazyLoadProvider) {
        $ocLazyLoadProvider.config({
            debug: true,
            events: true,
            modules: [{
                name: "ui.bootstrap",
                files: [
	          "assets/utils/angular/ui-bootstrap-tpls.js"
	        ],
	      }]
        });
    });