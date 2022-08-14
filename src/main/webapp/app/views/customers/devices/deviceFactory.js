/** 
 *@file : deviceFactory 
 *
 *@usersFactory :Load reusable methods for device module
 *
 *@author :(Nagaraju SVP Goli - ngoli@ctepl.com) 
 *
 *@Contact :(Umang - ugupta@ctepl.com)
 * 
 *@Contact : (Chenna - yreddy@ctepl.com)
 *
 *@version     VEM2-1.0
 *@date        14-09-2016
 *
 * MODIFICATION HISTORY
 * ===============================================================
 * SNO      DATE        USER                        COMMENTS
 * ===============================================================
 * 01       14-09-2016  Nagaraju SVP Goli           File Created
 * 02       14-09-2016  Nagaraju SVP Goli           Added getSites method
 *
 */
app.factory('deviceFactory', function (ApiFactory,commonFactories) {

    var locations = [{
        "label": "-- choose an option --",
        "value": 0
 }, {
        "label": "Banquet Room",
        "value": 1
    }, {
        "label": "Call Center",
        "value": 2
    }, {
        "label": "Counter",
        "value": 3
    }, {
        "label": "Dining Room",
        "value": 4
    }, {
        "label": "Dining Room Left",
        "value": 5
    }, {
        "label": "Dining Room Right",
        "value": 6
    }, {
        "label": "Dining Room 1",
        "value": 7
    }, {
        "label": "Dining Room 2",
        "value": 8
    }, {
        "label": "Kitchen",
        "value": 9
    }, {
        "label": "Kitchen Front",
        "value": 10
    }, {
        "label": "Kitchen Back",
        "value": 11
    }, {
        "label": "Lobby",
        "value": 12
    }, {
        "label": "Office",
        "value": 13
    }, {
        "label": "Storage Area",
        "value": 14
    }, {

        "label": "Backroom",
        "value": 15
    }, {

        "label": "Front",
        "value": 16
    }, {

        "label": "Back",
        "value": 17
    }, {

        "label": "Right",
        "value": 18
    }, {

        "label": "Left",
        "value": 19
    }, {

        "label": "Restroom",
        "value": 20
    }, {

        "label": "Entry",
        "value": 21
    }, {

        "label": "Main",
        "value": 22
    }, {

        "label": "Other",
        "value": 23
    }];



    return {
        getSites: function (params, callback) {
            ApiFactory.getApiData({
                serviceName: "listSiteForDevices",
                data: params,
                onSuccess: function (data) {
                    if (data.data.length > 0) {
                        callback(data.data);
                    }
                },
                onFailure: function () {}
            });
        },

        getLocations: function () {

            return commonFactories.getSortedArray(locations,'label');
        },

        getlocationLabel: function (locationId) {

            var location;

            $.each(locations, function (key, value) {

                if (value.value == locationId) {

                    location = value.label;

                    return;
                }

            })

            return location;

        },

        getThermostatLabel: function (thermoStatId) {


            var deviceTypes = [
                {
                    value: 2,
                    label: "Thermostat"
            }
        ];
            var thermostat;

            $.each(deviceTypes, function (key, value) {

                if (value.value == thermoStatId) {

                    thermostat = value.label;

                    return;
                }

            })

            return thermostat;

        },
        getWeekDay: function (day) {
            if (!day) {
                return;
            }
            var obj = {
                mo: "Mon",

                tu: "Tue",

                we: "Wed",

                th: "Thu",

                fr: "Fri",

                sa: "Sat",

                su: "Sun",
            };

            return obj[day.toLowerCase()];


        },
        getWeekDayNumber: function (day) {
            if (!day) {
                return;
            }
            var obj = {
                mo: 1,

                tu: 2,

                we: 3,

                th: 4,

                fr: 5,

                sa: 6,

                su: 7,
            };

            return obj[day];


        }
    }
})