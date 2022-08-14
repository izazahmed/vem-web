/** 
 *@file : ApiFactory 
 *
 *@config :Load API's for application 
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
 * 02       17-08-2016  Nagaraju SVP Goli           Added saveuser API
 * 03       22-08-2016  Nagaraju SVP Goli           Added updateuser API
 * 04       22-08-2016  Nagaraju SVP Goli           Added updateuser API
 * 05       23-08-2016  Nagaraju SVP Goli           Added deleteuser API
 * 06       24-08-2016  Nagaraju SVP Goli           Added getallusersdetails API
 * 07       25-08-2016  Nagaraju SVP Goli           Added getuserdetails API
 * 08       25-08-2016  Nagaraju SVP Goli           Added useracitivity API
 * 09       22-08-2016  Bhoomika Rabadiya           Added addrole API
 * 10       22-08-2016  Bhoomika Rabadiya           Added listroles API
 * 11       23-08-2016  Bhoomika Rabadiya           Added getrole API
 * 12       23-08-2016  Bhoomika Rabadiya           Added updaterole API
 * 13       24-08-2016  Bhoomika Rabadiya           Added deleterole API
 * 14       06-09-2016  Bhoomika Rabadiya           Added addsite API
 * 15       12-09-2016  Bhoomika Rabadiya           Added listsite API
 * 16       13-09-2016  Bhoomika Rabadiya           Added loadaddsite API
 * 17       14-09-2016  Bhoomika Rabadiya           Added siteacitivity API
 * 18       15-09-2016  Bhoomika Rabadiya           Added getsite API
 * 19      	23-09-2016  Bhoomika Rabadiya           Added checksiteinternalid API
 * 20       23-09-2016  Bhoomika Rabadiya           Added deletesite API
 * 21       28-09-2016  Bhoomika Rabadiya           Added updatesite API
 * 22       22-09-2016  Madhu Bantu                 Added createcustomer API
 * 23       24-09-2016  Madhu Bantu                 Added updatecustomer API
 * 24      	25-09-2016  Madhu Bantu                 Added deletecustomer API
 * 25       23-09-2016  Madhu Bantu                 Added customercode API
 * 26       30-09-2016  Madhu Bantu                 Added fileupload API
 * 27       14-10-2016  Bhoomika Rabadiya           Added getcustomerid API
 * 28       04-11-2016  Nagarjuna Eerla		        Added getsitesbygroups & getcustomergroups API's
 */
app.factory('ApiFactory', function ($http, cfpLoadingBar, toastr, messageFactory, $rootScope, $timeout, $state, $q) {
    /**
     * facilitates communication with the remote HTTP
     *@param $http
     *
     *facilitates the loading bar while remote HTTP request going on
     *@param cfpLoadingBar
     *
     *facilitates the centralized messages
     *@param toastr
     *
     *It is our custom factory provides custom messages
     *@messageFactory
     */
    return {
        getApiData: function (service) {
            /**
             *Service with readable format
             *@param service
             */
            var parames = {
                method: "GET",
                data: null,
                type: "normal"
            };
            switch (service.serviceName.toLowerCase()) {

                case ("saveuser"):
                    parames.url = "admin/saveUser";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updateemail"):
                    parames.url = "admin/updateEmailId/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updateuser"):
                    parames.url = "admin/updateUserDetails/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updateusersettingsprofile"):
                    parames.url = "admin/updateMyProfile/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deleteuser"):
                    parames.url = "admin/deleteUserDetails/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getallusersdetails"):
                    parames.url = "admin/getAllUsersDetails/?type=" + service.data.type.toUpperCase() + "&value=" + service.data.value;
                    parames.method = "GET";
                    break;
                case ("getuserdetails"):
                    parames.url = "admin/getUserDetails/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("useracitivity"):
                    parames.url = "admin/userAcitivity";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("changepassword"):
                    parames.url = "admin/changePassword";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("addrole"):
                    parames.url = "role/add";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("loadpermissions"):
                    parames.url = "role/loadPermissions";
                    parames.method = "GET";
                    break;
                case ("listroles"):
                    parames.url = "role/list";
                    parames.method = "GET";
                    break;
                case ("getrole"):
                    parames.url = "role/get/" + service.data.roleId;
                    parames.method = "GET";
                    break;
                case ("updaterole"):
                    parames.url = "role/update";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("deleterole"):
                    parames.url = "role/delete/" + service.data.roleId;
                    parames.method = "GET";
                    break;
                case ("customerslist"):
                    parames.url = "customers/customerslist";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("customerstatus"):
                    parames.url = "customers/changeCustomerStatus";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("addsite"):
                    parames.url = "site/add";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("listsite"):
                    parames.url = "site/list?moduleName=" + service.data.fromPage + "&moduleId=" + service.data.id;
                    parames.method = "GET";
                    parames.data = service.data;
                    break;
                case ("loadaddsite"):
                    parames.url = "site/loadForm?moduleName=" + service.data.fromPage + "&moduleId=" + service.data.id;
                    parames.data = service.data;
                    break;
                case ("getsite"):
                    parames.url = "site/get/" + service.data.siteId + "?moduleName=" + service.data.fromPage + "&moduleId=" + service.data.id;
                    parames.method = "GET";
                    break;
                case ("updatesite"):
                    parames.url = "site/update";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getcustomerid"):
                    parames.url = "site/getCustomerId";
                    parames.method = "GET";
                    break;
                case ("getcities"):
                    parames.url = "site/getCities/" + service.data.stateId;
                    break;
                case ("getcitiessearch"):
                    parames.url = "site/getCitiesSearch/" + service.data.citySearchText;
                    break;
                case ("checksiteinternalid"):
                    parames.url = "site/checkInternalId";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deletesite"):
                    parames.url = "site/delete/" + service.data.siteId;
                    break;
                case ("customerprofile"):
                    parames.url = "customers/getCustomerProfile";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("siteacitivity"):
                    parames.url = "site/activateOrDeActivateSite";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;

                case ("getdeviceslist"):
                    parames.url = "things/getThingListSort?sort-by=" + service.data.module + "&value=" + service.data.id;
                    break;

                case ("listsitefordevices"):
                    parames.url = "things/listSite?sort-by=" + service.data.sortBy + "&value=" + service.data.value;
                    parames.method = "GET";
                    break;

                case ("registerdevice"):
                    parames.url = "things/registerThing";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deletedevice"):
                    parames.url = "things/deleteDevice/" + service.data.deviceId;
                    parames.method = "DELETE";
                    break;
                case ("disconnectdevice"):
                    parames.url = "things/disconnectDevice";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getdevicedata"):
                    parames.url = "things/getDevice/" + service.data.siteId;
                    break;
                case ("updatedevice"):
                    parames.url = "things/updateDevice";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("settemperature"):
                    parames.url = "things/" + service.data.deviceId + "/set-temperature";
                    parames.method = "POST";
                    delete service.data.deviceId;
                    parames.data = service.data;
                    break;
                case ("settstatdata"):
                    parames.url = "things/" + service.data.deviceId + "/set-tstat-data";
                    parames.method = "POST";
                    delete service.data.deviceId;
                    parames.data = service.data;
                    break;
                case ("setclock"):
                    parames.url = "things/" + service.data.deviceId + "/set-clock";
                    parames.method = "POST";
                    delete service.data.deviceId;
                    parames.data = service.data;
                    break;
                case ("getdevicedatabydeviceid"):
                    parames.url = "things/getDevice/" + service.data.deviceId;
                    break;

                case ("deviceauditlogs"):
                    parames.url = "activityLog/get?serviceId=4&specificId=" + service.data;
                    break;

                case ("updateheatpumpfield"):
                    parames.url = "things/update-heatpump-field";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;

                case ("listthermostatunit"):
                    parames.url = "things/listThermostatUnit/" + service.data;
                    break;
                case ("gettstatpref"):
                    parames.url = "things/getTstatPref/" + service.data;
                    break;
                case ("savecustomer"):
                    parames.url = "customers/createCustomer";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("updatecustomer"):
                    parames.url = "customers/updateCustomer";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("deletecustomer"):
                    parames.url = "customers/deleteCustomer";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("customercode"):
                    parames.url = "customers/generateCustomerCode/" + service.data;
                    parames.method = "GET";
                    break;
                case ("loadstates"):
                    parames.url = "customers/loadStates";
                    break;
                    /* added for group*/
                case ("grouplist"):
                    parames.url = "group/groupList";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("getsites"):
                    parames.url = "group/getSites";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("addgroup"):
                    parames.url = "group/addGroup";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updategroup"):
                    parames.url = "group/updateGroup";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deletegroup"):
                    parames.url = "group/deleteGroup";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("checkgroupduplicate"):
                    parames.url = "group/checkDuplicate";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getgroupinfo"):
                    parames.url = "group/getGroupInfo";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getgroupsites"):
                    parames.url = "group/getGroupSites";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                    /*end group state*/

                case ("getuserprofileinfo"):
                    parames.url = "admin/getUserProfileInfo";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("createcustomerusersandsites"):
                    parames.url = "customers/createUsersAndSites";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updatesite"):
                    parames.url = "site/updateSite";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("fileupload"):
                    parames.url = "fileUpload/uploadFile";
                    parames.method = "POST";
                    parames.type = "multiPart";
                    parames.noAuthToken = true;
                    parames.data = service.data;
                    break;
                case ("uploadsettingslogo"):
                    parames.url = "fileUpload/uploadSettingsLogo";
                    parames.method = "POST";
                    parames.type = "multiPart";
                    parames.noAuthToken = true;
                    parames.data = service.data;
                    break;
                case ("schedulerlist"):
                    parames.url = "schedule/scheduleList";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("scheduledynaselect"):
                    parames.url = "schedule/getFilterData";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("schedulesearch"):
                    parames.url = "schedule/getFilterSearch";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("scheduleapplyview"):
                    parames.url = "scheduleApply/applyViewList";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("scheduleapplysave"):
                    parames.url = "scheduleApply/applySchedule";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("report"):
                    parames.url = "report/hvac";
                    parames.method = "GET";
                    break;
                    /*Alerts changes start */
                case ("customeralertconfig"):
                    parames.url = "alerts/customerAlertConfig/";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getconfig"):
                    parames.url = "alerts/getConfig";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("saveconfig"):
                    parames.url = "alerts/saveConfig";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getcustomeralerts"):
                    parames.url = "alerts/customeralerts";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("getdashboardalerts"):
                    parames.url = "alerts/dashboardAlerts";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("getcustomersbyuser"):
                    parames.url = "customers/getCustomersByUser";
                    break;
                case ("getnewalertcount"):
                    parames.url = "alerts/getNewAlert";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updatealertstatus"):
                    parames.url = "alerts/updateAlertStatus";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deletecustomeralertconfig"):
                    parames.url = "alerts/deleteCustomerConfig";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                    /*Alerts changes end */
                case ("addschedule"):
                    parames.url = "scheduleinfo/addSchedule";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("adddeviceschedule"):
                    parames.url = "scheduleinfo/addDeviceSchedule";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getschedule"):
                    parames.url = "scheduleinfo/getScheduleDetails";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("updateschedule"):
                    parames.url = "scheduleinfo/updateSchedule";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("deleteschedule"):
                    parames.url = "scheduleinfo/deleteSchedule";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("getsitesbygroupsinreports"):
                    parames.url = "report/list/groups?customerId=" + service.data.customerId + "&groupIds=" + service.data.groupIds;
                    parames.method = "GET";
                    break;
                case ("getsitesbygroups"):
                    parames.url = "site/list/groups?groupIds=" + service.data;
                    parames.method = "GET";
                    break;
                case ("getcustomergroups"):
                    parames.url = "group/getCustomerGroups";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("getsitesgroupsbycustomers"):
                    parames.url = "admin/getGroupsSites?userId=" + service.data.userId + "&customerIds=" + service.data.customerIds;
                    parames.method = "GET";
                    break;
                case ("checkzipcode"):
                    parames.url = "site/getTimeZone?address=" + service.data.zipCode;
                    parames.method = "GET";
                    break;
                case ("checkcityname"):
                    parames.url = "comm/validateCityName?cityName=" + service.data.cityName + "&stateId=" + service.data.stateId;
                    parames.method = "GET";
                    break;
                case ("getactivitylog"):
                    parames.url = "activityLog/get?serviceId=" + service.data.serviceId + "&specificId=" + service.data.specificId + "&startDate=" + service.data.startDate + "&endDate=" + service.data.endDate;
                    parames.method = "GET";
                    break;
                case ("getactivitylogpagination"):
                    parames.url = "activityLog/getActivityLogPagination?serviceId=" + service.data.serviceId + "&specificId=" + service.data.specificId + "&startDate=" + service.data.startDate + "&endDate=" + service.data.endDate + "&currentPage=" + service.data.currentPage + "&recordsPerPage=" + service.data.recordsPerPage + "&filterByAction=" + service.data.filterByAction + "&filterByModule=" + service.data.filterByModule + "&description=" + service.data.description;
                    parames.method = "GET";
                    break;
                case ("filterbyselect"):
                    parames.url = "activityLog/getFilterData";
                    parames.method = "GET";
                    break;
                case ("gettimezone"):
                    parames.url = "comm/getTimeZone?zipCode=" + service.data.zipCode;
                    parames.method = "GET";
                    break;
                case ("gettimezonesite"):
                    parames.url = "site/getTimeZone?address=" + service.data.zipCode;;
                    parames.method = "GET";
                    break;
                case ("addactivity"):
                    parames.url = "activityLog/add";
                    parames.data = service.data;
                    parames.method = "POST";
                    break;
                case ("usersupload"):
                    parames.url = "fileUpload/usersUpload";
                    parames.method = "POST";
                    parames.type = "multiPart";
                    parames.noAuthToken = true;
                    parames.data = service.data;
                    break;
                case ("getthinglistsort"):
                    parames.method = "GET";
                    parames.url = "things/getThingListSort?sort-by=" + service.data.sortId + "&value=" + service.data.id;
                    break;
                case ("getalertsbyactionitems"):
                    parames.url = "alerts/getAlertsByActionItems";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getdashboarddata"):
                    parames.url = "report/getDashboardData";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getreportdata"):
                    parames.url = "report/getReportData";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getreportlist"):
                    parames.url = "pdfreport/getReportList";
                    parames.method = "GET";
                    break;
                case ("resendpdf"):
                    parames.url = "pdfreport/resendPDF?reportId=" + service.data.reportId + "&email=" + service.data.email;
                    parames.method = "GET";
                    break;
                case ("deletepdf"):
                    parames.url = "pdfreport/deletePDFReport?reportId=" + service.data.reportId;
                    parames.method = "GET";
                    break;
                case ("gettempsetpointreport"):
                    parames.url = "report/getTempSetpointReport";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("gettrendinganalytics"):
                    parames.url = "report/getTrendingAnalytics";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updateuseractionitems"):
                    parames.url = "alerts/updateUserActionItems";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("storetimezone"):
                    parames.url = "admin/setTimeZone?timeZone=" + service.data;
                    break;
                case ("addforecast"):
                    parames.url = "weather/add-forecast";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("updateforecastmode"):
                    parames.url = "weather/updateforecast-mode/" + service.data.deviceId + "/" + service.data.forecastMode + "/" + service.data.forcastType;
                    parames.method = "POST";
                    break;
                case ("updateforecast"):
                    parames.url = "weather/update-forecast";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("deleteforecast"):
                    parames.url = "weather/delete-forecast/" + service.data;
                    parames.method = "DELETE";
                    break;
                case ("getforecastlist"):
                    parames.url = "weather/forecast-list?type=" + service.data.type + "&type-id=" + service.data.typeId;
                    parames.data = service.data;
                    break;
                case ("getanalyticparams"):
                    parames.url = "report/getAnalyticParams";
                    parames.method = "GET";
                    break;
                case ("gethvacusagereport"):
                    parames.url = "report/getHVACUsageReport";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getcustomeranalyticsdata"):
                    parames.url = "report/getCustomerAnalyticsData";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getgroupanalyticsdata"):
                    parames.url = "report/getGroupAnalyticsData";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("getsiteanalyticsdata"):
                    parames.url = "report/getSiteAnalyticsData";
                    parames.method = "POST";
                    parames.data = service.data;
                    break;
                case ("loadstatesfromzip"):
                    parames.url = "site/getGeoCodeData?zipCode=" + service.data;
                    parames.method = "GET";
                    break;
                case ("sitesupload"):
                    parames.url = "fileUpload/sitesUpload";
                    parames.method = "POST";
                    parames.type = "multiPart";
                    parames.noAuthToken = true;
                    parames.data = service.data;
                    break;
                case ("getschedulelist"):
                    parames.url = "weather/get-schedule-list?sort-by=" + service.data.sortBy + "&value=" + service.data.value;
                    break;
                case ("listdevforecast"):
                    parames.url = "things/list-dev-forecast?site-id=" + service.data.siteId;
                    break;
                case ("readschedule"):
                    parames.url = "weather/read-schedule?device-id=" + service.data;
                    break;
                case ("runschedule"):
                    parames.url = "weather/run-schedule?device-id=" + service.data.deviceId + "&schedule-id=" + service.data.scheduleId + "&xcspec-id=" + service.data.xcspec;
                    break;
                case ("getuploadstatus"):
                    parames.url = "uploadprogress/getUploadStatus";
                    break;
                case ("deletesiteupload"):
                    parames.url = "uploadprogress/deleteSiteUpload?bulkUploadProgressId=" + service.data.bulkUploadProgressId + "&fileName=" + service.data.fileName;
                    break;
            }
            var escapeLoader = ["getnewalertcount", "getuploadstatus"];
            if (escapeLoader.indexOf(service.serviceName.toLowerCase()) == -1 && $state.current.name != "devices.devicesList.profile.settings") {
                cfpLoadingBar.start();
                // $("body,html").css("pointer-events", "none");
            }

            /**
             *Remote HTTP request
             */

            var httpObject = {
                method: parames.method,
                url: parames.noAuthToken ? parames.url : "api/" + parames.url,
                data: parames.data
            };

            var path = (httpObject.url.trim().replace("/?", "?")).split("?")[0];

            path = (path.slice(-1) == "/") ? path.substr(0, path.length - 1) : path;

            var requestUrl = path.split("/").join("@@");

            // var path = (httpObject.url.replace("/?","?")).split("?")[0];

            $rootScope.apiRunning.push(requestUrl);
            $rootScope.apiServices[requestUrl] = angular.copy(service);

            if (parames.type.trim().toLowerCase() == "multipart") {
                httpObject.headers = {
                    "Content-Type": undefined
                };

                var formData = new FormData();
                $.each(parames.data, function (key, value) {
                    if (key.toString() !== "files") {
                        formData.append(key, value);
                    }

                    if (key.toString() === "files") {
                        $.each(value, function (key1, value1) {
                            formData.append($(value1).attr("name"), value1.files[0]);
                        })
                    }
                });

                httpObject.data = formData;
                doubleHandShak(httpObject)
                return;
            }

            $rootScope.apiRequestPool.push(httpObject);

            $http.get("oauth/token?grant_type=password&client_id=restapp&client_secret=restapp&username=" + $rootScope.userDetails.restUserName + "&password=" + $rootScope.userDetails.restPassword).then(function (response) {

                var request = angular.copy($rootScope.apiRequestPool[0]);

                if (!parames.noAuthToken) {
                    if (request.url.indexOf("?") != -1) {

                        request.url += "&access_token=" + response.data.value;

                    } else {
                        request.url += "?access_token=" + response.data.value;
                    }

                }
                $rootScope.apiRequestPool.shift();

                doubleHandShak(request);
            }, function (response) {

            });

            function doubleHandShak(httpObject) {

                $http(httpObject).then(function successCallback(response) {

                    if (response.status == 401) {
                        window.location.reload();
                        return;
                    }

                    if (typeof (response.data) == "string") {
                        var div = $("<div></div>");
                        div.html(response.data);
                        if ($(div).find("#session-check")[0] && eval($(div).find("#session-check").val())) {
                            window.location.reload();
                            return;
                        }

                    }

                    if (response.data.code == "ERR_SESSION_1003") {
                        toastr.error(messageFactory.getMessage("ERR_SESSION_1003"));
                        window.location.reload();
                        return;
                    }


                    if ($rootScope.apiRunning.length > 0) {
                        var path = (response.config.url.trim().replace("/?", "?")).split("?")[0];
                        path = (path.slice(-1) == "/") ? path.substr(0, path.length - 1) : path;
                        var requestUrl = path.trim().split("/").join("@@");
                        $rootScope.apiRunning.splice($rootScope.apiRunning.indexOf(requestUrl), 1);
                    }
                    if ($rootScope.apiRunning.length === 0) {
                        cfpLoadingBar.complete();
                        $timeout(function () {
                            // $("body,html").css("pointer-events", "all");
                        }, 1000);
                    }
                    /* if (response.data.status.toLowerCase() !== "success" && response.data.code !=="INFO_ROLE_20012") {
                         toastr.error(messageFactory.getMessage(response.data.code));
                         return;
                     }*/

                    $rootScope.apiServices[requestUrl].onSuccess(response.data);

                }, function errorCallback(response) {

                    if (response.status == 401) {
                        window.location.reload();
                        return;
                    }

                    if (typeof (response.data) == "string") {
                        var div = $("<div></div>");
                        div.html(response.data);
                        if ($(div).find("#session-check")[0] && eval($(div).find("#session-check").val())) {
                            window.location.reload();
                            return;
                        }

                    }
                    if (response.data.code == "ERR_SESSION_1003") {
                        toastr.error(messageFactory.getMessage("ERR_SESSION_1003"));
                        window.location.reload();
                        return;
                    }
                    if ($rootScope.apiRunning.length > 0) {
                        var path = (response.config.url.replace("/?", "?")).split("?")[0];

                        var requestUrl = path.split("/").join("@@");
                        $rootScope.apiRunning.splice($rootScope.apiRunning.indexOf(requestUrl), 1);
                    }
                    if ($rootScope.apiRunning.length === 0) {
                        cfpLoadingBar.complete();
                        //$("body").css("pointer-events", "all");
                    }
                    // toastr.error(messageFactory.getMessage("serverError"));
                    $rootScope.apiServices[requestUrl].onFailure(response.data);
                    // $("body").css("pointer-events", "all");
                });
            }
        }
    };
});