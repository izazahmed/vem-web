app.directive("compareTo", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=compareTo"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.compareTo = function (modelValue) {
                	if(scope.otherModelValue){
	                    return modelValue == scope.otherModelValue;
	                };
	                return true;
                };
                
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    }).directive("comparePrevious", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=comparePrevious"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.comparePrevious = function (modelValue) {
                    return modelValue != scope.otherModelValue;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive('ngFocus', function ($timeout) {
        return {
            link: function (scope, element, attrs) {
                scope.$watch(attrs.ngFocus, function (val) {
                    if (angular.isDefined(val) && val) {
                        $timeout(function () {
                            element[0].focus();
                        });
                    }
                }, true);
                element.bind('blur', function () {
                    if (angular.isDefined(attrs.ngFocusLost)) {
                        scope.$apply(attrs.ngFocusLost);
                    }
                });
            }
        };
    })
    .directive("maxlength20", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength20"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength20 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 20 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength42", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength42"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength42 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 42 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength10", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength10"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength10 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 10 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive('stringToNumber', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                ngModel.$parsers.push(function (value) {
                    return '' + value;
                });
                ngModel.$formatters.push(function (value) {
                    return parseFloat(value);
                });
            }
        };
    })
    .directive("maxlength15", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength15"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength15 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 15 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength5", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength5"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength5 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 5 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength6", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength6"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength6 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 6 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength32", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength32"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength32 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 32 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength30", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength30"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength30 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 30 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("noSpaces", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=noSpaces"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.noSpaces = function (modelValue) {
                    if (modelValue) {
                        return /\s/g.test(s);
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength48", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength48"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength48 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 48 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength50", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength50"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength50 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 50 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength500", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength500"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength500 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 500 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength105", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength105"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength105 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 105 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength255", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength255"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength255 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 255 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength104", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength104"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.maxlength104 = function (modelValue) {
                    if (modelValue) {
                        return modelValue.length > 104 ? false : true;
                    }
                    return true;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("selectValidation", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=selectValidation"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.selectValidation = function (modelValue) {
                    if (modelValue) {
                        return true;
                    } else {
                        return false;
                    }
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive('onlyNumbers', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    if (text) {
                        var transformedInput = text.toString().replace(/[^0-9]/g, '');

                        if (transformedInput !== text.toString()) {
                            ngModelCtrl.$setViewValue(transformedInput);
                            ngModelCtrl.$render();
                        }
                        return isNaN(transformedInput) ? transformedInput : Number(transformedInput);
                    }
                    return undefined;
                }
                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    })
    .directive('onlyNumbersDot', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^0-9.]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    })
    .directive('onlyNumbersHyphen', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^0-9-]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    })
    .directive('onlyNumbersCharHyphen', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^a-zA-Z\d\-]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    })
    .directive('file', function () {
        return {
            scope: {
                file: '='
            },
            link: function (scope, el, attrs) {
                el.bind('change', function (event) {
                    var file = event.target.files[0];
                    scope.file = file ? file : undefined;
                    scope.$apply();
                });
            }
        };
    }).directive('myEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                var regex = new RegExp("^[0-9]+$");
                var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
                if (!(event.keyCode == 8 // backspace
                        || event.keyCode == 9 // Tab
                        || event.keyCode == 13 // Enter
                        || event.keyCode == 27 // Escape
                        || event.keyCode == 46 // delete
                        || (event.keyCode >= 35 && event.keyCode <= 40) // arrow keys/home/end
                        || (event.keyCode >= 48 && event.keyCode <= 57) // numbers on keyboard
                        || (event.keyCode >= 96 && event.keyCode <= 105) // number on keypad
                        || ((event.charCode == 65 || event.charCode == 97) && event.ctrlKey === true) // Allow: Ctrl+A OR Ctrl+a
                        || ((event.charCode == 67 || event.charCode == 99) && event.ctrlKey === true) // Allow: Ctrl+C OR Ctrl+c
                        || ((event.charCode == 88 || event.charCode == 120) && event.ctrlKey === true) // Allow: Ctrl+X OR Ctrl+x
                        || ((event.charCode == 86 || event.charCode == 118) && event.ctrlKey === true) // Allow: Ctrl+V OR Ctrl+v
                        || regex.test(key))) {
                    event.preventDefault(); // Prevent character input
                    return false;
                }
                return true;
            });
        };
    }).directive('disallowSpaces', function () {
        return {
            restrict: 'A',
            link: function ($scope, $element) {
                $element.bind('keydown', function (e) {
                    if (e.which === 32) {
                        e.preventDefault();
                    }
                });
            }
        }
    })
    .directive("myCalendar", function () {
        var directive = {};
        directive.restrict = 'EA';
        directive.scope = {
            myEvents: '=',
            backdrop: '='
        };
        directive.link = function (scope, element, attributes) {
            scope.$watch('myEvents', function (data) {

                if (data) {

                    var sortArray = {};

                    $.each(data, function (key, value) {
                        sortArray[key] = value.sort(function (a, b) {

                            var time = (b.time) + " " + ((b.am == 1) ? "AM" : "PM");
                            time = moment(time, ["h:mm A"]).format("HH:mm");

                            var time1 = (a.time) + " " + ((a.am == 1) ? "AM" : "PM");
                            time1 = moment(time1, ["h:mm A"]).format("HH:mm");

                            return time > time1 ? 1 : time < time1 ? -1 : 0;
                        }).reverse();

                    });
                    scope.eventsData = angular.copy(sortArray);

                    rtime = new Date();
                    if (timeout === false) {
                        timeout = true;
                        setTimeout(resizeend, delta);
                    }

                }
            });

            function renderCalendar() {
                $(element).fullCalendar('destroy');
                $(element).css({
                    transform: "none"
                });
                $(element).css({
                    opacity: 0
                });
                loadCalendar();


            }

            function loadCalendar() {

                var multipleEvents = {};
                var timepointsmap = scope.eventsData;
                var events = [];

                for (var i in timepointsmap) {

                    var sortArray = [];
                    var sortTime = [];

                    for (var k = 0; k < timepointsmap[i].length; k++) {

                        var time = (timepointsmap[i][k].time) + " " + ((timepointsmap[i][k].am == 1) ? "AM" : "PM");
                        time = moment(time, ["h:mm A"]).format("HH:mm");
                        sortTime.push(time);
                        sortArray.push(Number(time.split(":")[0]));
                    }
                    var min = Math.min.apply(null, sortArray),
                        max = Math.max.apply(null, sortArray);

                    var timeMin = sortTime[sortArray.indexOf(min)];
                    var timeMax = sortTime[sortArray.indexOf(max)];

                    /*  var temp1 = {};

                      temp1.start = "2016-10-0" + (Number(i - 1) + 3) + "T" + timeMin;

                      temp1.end = "2016-10-0" + (Number(i - 1) + 3) + "T" + timeMax;
                      temp1.end = moment(temp1.end).add(0.5, 'hours').format('YYYY-MM-DDTHH:mm:ss');

                      temp1.color="rgba(175, 172, 172, 0.6)";
                      temp1.rendering = 'background';
                      if (scope.backdrop) {
                          events.push(temp1);
                      }*/

                    for (var j = 0; j < timepointsmap[i].length; j++) {

                        var temp = {};
                        var time = (timepointsmap[i][j].time) + " " + ((timepointsmap[i][j].am == 1) ? "AM" : "PM");

                        temp.start = "2016-10-0" + (Number(i - 1) + 3) + "T" + moment(time, ["h:mm A"]).format("HH:mm:ss");
                        temp.end = moment(temp.start).add(0.5, 'hours').format('YYYY-MM-DDTHH:mm:ss');

                        temp.htpoint = timepointsmap[i][j].htpoint;
                        temp.clpoint = timepointsmap[i][j].clpoint;
                        temp.from = timepointsmap[i][j].from;

                        events.push(temp);

                    }
                }
                if (!events || (events && events.length == 0)) {

                    events = [{
                        "start": "2016-10-03T02:00:00",
                        "end": "2016-10-03T02:30:00",
                        "htpoint": "66",
                        "clpoint": "77"
                    }];
                    events.noData = true;

                } else {
                    if (events.noData) {
                        delete events.noData;
                    }
                }

                if (scope.backdrop) {
                    $.each(scope.backdrop, function (key, value) {
                        var temp1 = {};

                        temp1.start = "2016-10-0" + (Number(key) + 3) + "T" + moment(value.openTime, ["h:mm A"]).format("HH:mm:ss");

                        temp1.end = "2016-10-0" + (Number(key) + 3) + "T" + moment(value.closeTime, ["h:mm A"]).format("HH:mm:ss");
                        // temp1.end = moment(temp1.end).add(0.5, 'hours').format('YYYY-MM-DDTHH:mm:ss');

                        temp1.color = "rgba(175, 172, 172, 0.6)";
                        temp1.rendering = 'background';

                        events.push(temp1);


                    });
                }
                $(element).fullCalendar({
                    header: {
                        left: '',
                        center: '',
                        right: '',
                    },
                    isRTL: true,
                    'contentHeight': 'auto',
                    defaultDate: '2016-10-03',
                    //defaultView: 'agendaWeek',
                    editable: false,
                    firstDay: 1,
                    allDaySlot: false,
                    eventOverlap: true,
                    views: {
                        settimana: {
                            type: 'agendaWeek',
                            duration: {
                                days: 7
                            },
                            title: 'Apertura',
                            columnFormat: 'ddd', // Format the day to only show like 'Monday'
                            // hiddenDays: [0, 6] // Hide Sunday and Saturday?
                        }
                    },
                    defaultView: 'settimana',
                    events: events,
                    eventRender: function (event, element) {
                        if (events && events.length > 0 && !events.noData) {
                            var dataAttr = moment(event.start).format("YYYY-MM-DD");
                            $("[data-date='" + dataAttr + "']").addClass("hasEvent");


                            if (event.rendering) {
                                return;
                            }
                            var time = moment(event.start).format("HH:mm");
                            var date = moment(event.start).format("YY/MM/DD");
                            var week = moment(event.start).format('dddd')

                            var dateTime = date + "-" + time + "-" + week;

                            $(element).addClass("from-"+event.from);
                            if (multipleEvents[dateTime]) {
                                if (multipleEvents[dateTime] == 1) {
                                    $(element).prev().addClass("two-events");
                                }
                                $(element).html('<div class="points" dateTime="' + dateTime + '" from="'+event.from+'" week-selector="' + week + '"><div class="htpoint from-'+event.from+'"><span>' + event.clpoint + '</span></div><div class="clpoint from-'+event.from+'"><span>' + event.htpoint + '</span></div></div>');
                                multipleEvents[dateTime] = (multipleEvents[dateTime] + 1);
                            } else {

                                $(element).html('<div class="points" dateTime="' + dateTime + '" from="'+event.from+'"><div class="htpoint from-'+event.from+'"><span>' + event.clpoint + '</span></div><div class="clpoint from-'+event.from+'"><span>' + event.htpoint + '</span></div></div>');
                                multipleEvents[dateTime] = 1;
                            }

                            if (multipleEvents[dateTime] && multipleEvents[dateTime] > 2) {
                                element.addClass("rem");
                                element.prev().addClass("rem");
                                element.prev().prev().removeClass("two-events").addClass("three-events");


                            }
                            if (multipleEvents[dateTime] && multipleEvents[dateTime] > 1) {

                            }

                            $(".fc-day-grid.fc-unselectable").next("hr").remove();
                            $(".fc-day-grid.fc-unselectable").remove();
                        }

                    },
                    eventAfterAllRender: function (view) {

                        /*      $('.fc-axis.fc-time.fc-widget-content').each(function() {
                                  $(this).parent().prepend($(this).parent().find(".fc-axis"));
                              });
                              $('.fc-axis.fc-widget-header').parent().prepend($('.fc-axis.fc-widget-header').last()[0]);

                              var ul = $('.fc-bg').find("tr"); // your parent ul element
                              ul.children().each(function(i, li) {
                                  ul.prepend(li)
                              })

                              var ul = $('.fc-content-skeleton').find("tr"); // your parent ul element
                              ul.children().each(function(i, li) {
                                  ul.prepend(li)
                              })*/
                        $.each(multipleEvents, function (key, value) {


                            var heatCompare = false;
                            var coolCompare = false;

                            var elements = [];

                            $.each($("[dateTime='" + key + "']"), function (key, value) {

                                elements.push(value);
                            })

                            $.each(elements, function (key, value) {
                                if (elements[key + 1] && ($(value).find(".htpoint").text().trim() != $(elements[key + 1]).find(".htpoint").text().trim())) {
                                    heatCompare = true;
                                }
                                if (elements[key + 1] && ($(value).find(".clpoint").text().trim() != $(elements[key + 1]).find(".clpoint").text().trim())) {
                                    coolCompare = true;
                                }
                            });
                            if (heatCompare) {
                                $("[dateTime='" + key + "']").find(".htpoint").addClass("no-bold");
                                $("[dateTime='" + key + "']").find(".clpoint").addClass("no-bold");
                            }
                            if (coolCompare) {
                                $("[dateTime='" + key + "']").find(".htpoint").addClass("no-bold");
                                $("[dateTime='" + key + "']").find(".clpoint").addClass("no-bold");
                            }


                            if (value > 1) {

                                $.each(elements, function (key, value) {

                                    if (!$(value).find(".no-bold")[0]) {

                                        var attr = $(value).attr('week-selector');

                                        if (typeof attr !== typeof undefined && attr !== false) {
                                            $(value).addClass("full-color");
                                        }else{
                                            $(value).addClass("hide");
                                        }
                                    }

                                });
                            }

                        });
                        $('.fc-axis.fc-time.fc-widget-content').each(function () {
                            $(this).parent().prepend($(this).parent().find(".fc-axis"));
                        });

                        $('.fc-axis.fc-widget-header').parent().prepend($('.fc-axis.fc-widget-header').last()[0]);

                        $('.fc-content-skeleton tr').prepend($('.fc-content-skeleton').find("td").last());
                        $('.fc-bg tr').prepend($('.fc-bg').find("td").last());

                        $(element).css({
                            transform: "rotate(-90deg)"
                        });

                        $(element).css({
                            opacity: 1
                        });


                        if ($('.fc-time-grid-event').length > 0) {
                            var renderedEvents = $('div.fc-event-container a');
                            var firstEventOffsetTop = renderedEvents && renderedEvents.length > 0 ? renderedEvents[0].offsetTop : 0;

                            $('div.fc-scroller').animate({
                                scrollTop: 0
                            }, 10);
                        }
                    }
                });
                $(".rem").remove();
            }
            var rtime;
            var timeout = false;
            var delta = 200;
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
                    renderCalendar();
                }
            }
        }
        return directive;
    })
    .directive("validTime", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=validTime"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.validTime = function (modelValue) {
                    if (modelValue) {
                        var left = Number(modelValue.split(":")[0]).between(1, 12);
                        var right = Number(modelValue.split(":")[1]).between(0, 59);


                        if (left && right) {
                            return true;
                        }
                        return false;
                    }
                    return false;
                };
            }
        }
    }).directive("compareDates", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=compareDates"
            },
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.compareDates = function (modelValue) {
                    return modelValue != scope.otherModelValue;
                };
                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    }).directive('noSpecialChar', function () {
        return {
            require: 'ngModel',
            restrict: 'A',
            link: function (scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue == undefined)
                        return ''
                    cleanInputValue = inputValue.replace(/[^\w\s]/gi, '');
                    if (cleanInputValue != inputValue) {
                        modelCtrl.$setViewValue(cleanInputValue);
                        modelCtrl.$render();
                    }
                    return cleanInputValue;
                });
            }
        }
    })
    .directive('myEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.myEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    }).directive("fileread", [function () {
        return {
            scope: {
                fileread: "="
            },
            link: function (scope, element, attributes) {
                element.bind("change", function (changeEvent) {
                        scope.$apply(function () {
                            scope.fileread = changeEvent.target.files[0] ? changeEvent.target.files[0].name : '';
                        });
                    
                   
                });
            }
        }
    }]);;