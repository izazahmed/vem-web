app.directive('hcSolidChart', function () {
	return {
	    restrict: 'E',
		scope: {
		    title: '@',
		    name: '@',
		    data: '=',
		    someCtrlFn: '&callbackFn'
		},
		link: function (scope, element) {
			var graphData = [];
			var tickInterval = null;
			var max = 100;
			var suffix = '%';
			if (scope.data[0].id !== 3) {
				if (scope.data[0].y <= scope.data[0].max) {
					scope.data[0].y = Math.round(( scope.data[0].y / scope.data[0].max )*100);
				} else {
					scope.data[0].y = 0;
				}
				
				if(isNaN(scope.data[0].y)) {
					scope.data[0].y = 0;
				}
			}
			graphData = scope.data;
			if (scope.data[0].id === 1) {
			 	if (scope.data[0].y < 50) {
					scope.data[0].color = "#8DC643";
				} else if (scope.data[0].y <= 75) {
					scope.data[0].color = "#EDEA63";
				} else if (scope.data[0].y > 75) {
					scope.data[0].color = "#DD2133";
				}
			}
			if (scope.data[0].id === 2) {
				if (scope.data[0].y < 50) {
					scope.data[0].color = "#DD2133";
				} else if (scope.data[0].y <= 75) {
					scope.data[0].color = "#EDEA63";
				} else if (scope.data[0].y > 75) {
					scope.data[0].color = "#8DC643";
				}
			}
			if (scope.data[0].id === 4) {
				if (scope.data[0].y < 25) {
					scope.data[0].color = "#8DC643";
				} else if (scope.data[0].y <= 50) {
					scope.data[0].color = "#EDEA63";
				} else if (scope.data[0].y > 50) {
					scope.data[0].color = "#DD2133";
				}
			}
			if (scope.data[0].id === 3) {
				var oldYData = 0;
				var max_val = scope.data[0].max;
				if (max_val < scope.data[0].y || max_val < scope.data[0].extraValue) {
					max_val = 0;
				}
				var heat = 0;
				var cool = 0;
				var heatHours = scope.data[0].y;
				var coolHours = scope.data[0].extraValue;
				if (max_val > 0) {
					oldYData = scope.data[0].max;
					heat = ( scope.data[0].y / max_val )*100;
					cool = ( scope.data[0].extraValue / max_val)*100;
				}				
				graphData = [];
				var heatTemp;
				var coolTemp;
				if (Math.round(heat) >= Math.round(cool)) {
					heatTemp = heat+cool;
					coolTemp = cool;
				} else {
					heatTemp = heat;
					coolTemp = cool+heat;
				}
				
				// we should maintain the order by pushing larger one first
				if(heatTemp > coolTemp){
					graphData.push({
						id: scope.data[0].id,
						max: scope.data[0].max,
						name: scope.data[0].name,
						reportId: scope.data[0].reportId,
						y: heatTemp,
						msg: 'Heat :' + heatHours + 'hr',
						color: "#f7a35c"
					});
					graphData.push({
						id: scope.data[0].id,
						max: scope.data[0].max,
						name: scope.data[0].name,
						reportId: scope.data[0].reportId,
						y: coolTemp,
						msg: 'Cool :'+ coolHours + 'hr',
						color: "#7cb5ec"
					});
				}else{
					graphData.push({
						id: scope.data[0].id,
						max: scope.data[0].max,
						name: scope.data[0].name,
						reportId: scope.data[0].reportId,
						y: coolTemp,
						msg: 'Cool :'+ coolHours + 'hr',
						color: "#7cb5ec"
					});
					graphData.push({
						id: scope.data[0].id,
						max: scope.data[0].max,
						name: scope.data[0].name,
						reportId: scope.data[0].reportId,
						y: heatTemp,
						msg: 'Heat :' + heatHours + 'hr',
						color: "#f7a35c"
					});
				}
				oldYData = heatHours + coolHours;
	            suffix = oldYData + 'hr';
	        } else {
	        	suffix = scope.data[0].y + '%'
	        }
			Highcharts.chart(element[0], {
		        chart: {
		            type: 'solidgauge',
		            backgroundColor: 'transparent'	                	
		        },
                exporting : {
            	        enabled: false,
            	        buttons: {
            	            exportButton: {
            	                enabled: false
            	            },
            	            printButton: {
            	                enabled: false
            	            }
            	        }
                },
		        title: {
		            text: scope.name,
		            align: 'center',
		            verticalAlign: 'bottom',
		            style: {
		                fontSize: '8px',
		                fontWeight: 'bold'
		            },
		            y: -25,
		        },
		        pane: {
		            startAngle: 0,
		            endAngle: 360,
		            background: {
		                backgroundColor: '#EEE',
		                innerRadius: '75%',
		                outerRadius: '100%',
		                shape: 'arc',
		                borderColor: 'transparent'
		            }
		        },
		        tooltip: {
		        	formatter: function() {
		        		if (this.series.name === "HVAC USAGE") {
		        			return '<b>'+ this.point.msg +'</b>';
		        		} else {
		        			return '<b>'+ (this.y) +'%</b>';
		        		}
	        	    }
		        },
		        // the value axis
		        yAxis: {
		        	min: 0,
		            max: max,
		            tickInterval: tickInterval,
		            minorTickInterval: null,
		            tickPixelInterval: 400,
		           	tickAmount:0,
		            tickWidth: 0,
		            lineColor: 'transparent',
		            gridLineWidth: 0,
		            gridLineColor: 'transparent',
		            labels: {
		                enabled: false
		            },
		            title: {
		                enabled: false
		            }
		        },
		        credits: {
		            enabled: false
		        },
		        plotOptions: {
		            solidgauge: {
		                innerRadius: '75%',
		                dataLabels: {
		                    y: -15,
		                    borderWidth: 0,
		                    useHTML: true
		                }
		            },
		            series: {
		                cursor: 'pointer',
		                point: {
		                    events: {
		                        click: function () {
		                           scope.someCtrlFn({arg1: this.series.data[0]});
		                        }
		                    }
		                }
		            }
		        },
		        series: [{
		        	name: scope.name,
		            data: graphData,
		            dataLabels: {
		                format: '<p style="text-align:center;">'+suffix+'</p>'
			        }
			    }]
		    });
        }
    };
}).directive('hcCriticalChart', function () {
	return {
	    restrict: 'E',
		scope: {
			options: '=',
		    someCtrlFn: '&callbackFn'
		},
		 link: function (scope, element) {
        	 scope.$watch('options', function (data) {
    			 if (data) {
    				 var flag = 0;
    				 var max = null;
    				 var tickInterval = null;
    				 for (var i = 0; i < data.length; i++) {
    					  if (data[i].data[0].y > 10) {
    						  flag++; 
    					  }
            		 }
    				 if (flag === 0) {
    					 max = 10;
    					 tickInterval = 2;
    				 }
    				 Highcharts.chart(element[0], {
         		        chart: {
         		            type: 'column'
         		        },
	                    exporting : {
	            	        enabled: false,
	            	        buttons: {
	            	            exportButton: {
	            	                enabled: false
	            	            },
	            	            printButton: {
	            	                enabled: false
	            	            }
	            	        }
	                    },
         		        title: {
         		            text: null
         		        },
         		        credits: {
         		            enabled: false
         		        },
         		        xAxis: {
         		            type: 'category'
         		        },
         		        yAxis: {
         		        	allowDecimals: false,
         		            min: 0,
         		            max: max,
         		            tickInterval: tickInterval,
         		            endOnTick:false,
           					lineWidth:1,
           					tickWidth:1,
           					tickLength:3,
         		            title: {
         		                text: null
         		            },
         		            stackLabels: {
         		            	enabled: true
         		            }
         		        },
         		        legend: {
         		            enabled: false
         		        },
     		        	plotOptions: {
			                column: {
			                    stacking: 'normal',
			                    pointWidth: 20,
								groupPadding:0,
								cursor: 'pointer',
 				                point: {
 				                    events: {
 				                    	click: function () {
 			                                scope.someCtrlFn({arg1: this.series.data[0].options});
 			                           }
 				                    }
 				                }								
			                }
			            },
         		        tooltip: {
         		        	formatter: function() {
         		        		if (this.series.options.dataId == 1) {
         		        			return "<b>Number of thermostats currently in a OFF Mode</b>";
         		        		} else if (this.series.options.dataId == 2) {
         		        			return "<b>Number of thermostats currently in communication failure</b>";
         		        		} else if (this.series.options.dataId == 5) {
         		        			return "<b>Number of HVAC units currently unable to reach setpoint</b>";
         		        		}
			                },
			                useHTML: true,
			                shared:false
         		        },
         		        series: scope.options
                	 });
                 }
           });
		 }
    };
}).directive('hcChart', function () {
	return {
	    restrict: 'E',
		scope: {
			asc: '@',
        	desc: '@',
			options: '=',
		    someCtrlFn: '&callbackFn'
		},
		 link: function (scope, element) {
			 var chart;
        	 scope.$watch('options', function (data) {
        			 if (data) {
        				 chart = Highcharts.chart(element[0], {
             		        chart: {
             		           type: 'column',
             		           width: $(element[0]).closest(".column-chart").width()
             		        }, 		                 
             		        title: {
             		            text: null
             		        },
             		        credits: {
             		            enabled: false
             		        },
             		        xAxis: {
             		           type: 'category',
             		           labels: {
             		                style: {
             		                	fontWeight: 'bold'
             		                }
             		            }
             		        },
             		        yAxis: {
             		        	allowDecimals: false,
             		            min: 0,
             		            title: {
             		                text: null
             		            },
	        					lineWidth:1,
	        					tickWidth:1,
	        					tickLength:0
             		        },
             		        lang: {
					            some_key: 'Download Chart'
					        },
				        	exporting: {
							    enabled: true,
							    buttons: {
					                contextButton: {
					                	menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
					                   	symbol: 'url(assets/images/download.png)',		
						                symbolY: 25,
						                theme: {
					                      states: {
					                            hover: {
					                                fill: null
					                            },
					                            select: {
					                                fill: null
					                            }
					                        }    
					                    },
					                    _titleKey: 'some_key'
					                }
					            },
							    chartOptions: {
					                title: {
					                    text: scope.asc
					                },
					                subtitle: {
					                 	text: scope.desc
					                }
				            	}
							},
             		       legend: {
             		    	   enabled: false
           		        	 	//align: 'center',
           		             	//verticalAlign: 'bottom',
           		             	//layout: 'horizontal'
           		        	},
         		           	plotOptions: {
				                column: {
				                    stacking: 'normal',
				                    pointWidth: 20,
									groupPadding:0										
				                }
				            },
             		        tooltip: {
             		            pointFormat: '<b>{point.y}</b>'
             		        },
             		        series: scope.options
                	 });
                 }
           });
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
                        var height = chart.height
            		    chart.setSize($(element[0]).closest(".column-chart").width(), height, doAnimation = true);
                    }
                }
		 }
    };
}).directive('hcChartCombine', function () {
	return {
	    restrict: 'E',
		scope: {
			asc: '@',
        	desc: '@',
			options: '=',
		    someCtrlFn: '&callbackFn'
		},
		 link: function (scope, element) {
			 var chart;
			 var maxData = scope.options.data;
			 var maxVal = 0;
			 var isDataLableToShow = true;
			 for(var i=0;i<maxData.length;i++){
				 var inMaxData = maxData[i].data;
				 for(var j=0;j<inMaxData.length;j++){
					 if(maxVal < inMaxData[j].y){
						 maxVal = inMaxData[j].y
					 }
				 }
			 };
			 // incrementing max value to maintain max crop
			 maxVal = maxVal+12;
			 
			 // this is to hide labels for single site and single group 
			 if(maxData.length === 1){
				 maxVal = (maxVal-12)+2;
				 isDataLableToShow = false;
			 }
			 
        	 scope.$watch('options', function (data) {
        			 if (data) {
        				 chart = Highcharts.chart(element[0], {
             		        chart: {
             		           type: 'column',
             		           width: $(element[0]).closest(".column-chart").width()
             		        }, 		                 
             		        title: {
             		            text: null
             		        },
             		        credits: {
             		            enabled: false
             		        },
             		        xAxis: {
             		           categories: scope.options.categories,
             		           labels: {
             		                style: {
             		                	fontWeight: 'bold'
             		                }
             		            }
             		        },
             		        yAxis: {
             		        	allowDecimals: false,
             		            min: 0,
             		            max:maxVal,
             		            title: {
             		                text: null
             		            },
	        					lineWidth:1,
	        					tickWidth:1,
	        					tickLength:0
             		        },
             		        lang: {
					            some_key: 'Download Chart'
					        },
				        	exporting: {
							    enabled: true,
							    buttons: {
					                contextButton: {
					                	menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
					                   	symbol: 'url(assets/images/download.png)',		
						                symbolY: 25,
						                theme: {
					                      states: {
					                            hover: {
					                                fill: null
					                            },
					                            select: {
					                                fill: null
					                            }
					                        }    
					                    },
					                    _titleKey: 'some_key'
					                }
					            },
							    chartOptions: {
					                title: {
					                    text: scope.asc
					                },
					                subtitle: {
					                 	text: scope.desc
					                }
				            	}
							},
             		       legend: {
             		    	   enabled: false
           		        	},
         		           	plotOptions: {
				                column: {
				                	pointPadding: 0.2,
				                    borderWidth: 0,
				                    dataLabels: {
				                    	enabled: isDataLableToShow,
                                        align:"left",
                                        rotation: -90,
                                        // format: '{point.series.name}'
                                        formatter: function() {
                                        	return  (this.y > 0) ? (this.point.series.name + " : ( " + this.y+" )") : "";
                                        },
                                        crop:false,
                                        overflow:"none",
                                        allowOverlap:true
		             		        }
				                }
				            },
				            tooltip: {
				               	formatter: function() {              
				               		return "<b>"+ this.x + "</b></br>"+ this.point.series.name + ": ( " + this.y+" )";
				                },
				                useHTML: true,
				                shared:false
				            },
             		        series: scope.options.data
                	 });
                 }
           });
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
                        var height = chart.height
            		    chart.setSize($(element[0]).closest(".column-chart").width(), height, doAnimation = true);
                    }
                }
		 }
    };
}).directive('hcPieChart', function () {
    return {
        restrict: 'E',
        scope: {              	
        	name: '@',
        	asc: '@',
        	desc: '@',
            data: '=',
            type: '@',
    		someCtrlFn: '&callbackFn'
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
                if (data) {
		            Highcharts.chart(element[0], {
		                chart: {
		                    type: 'pie',
		                    marginBottom: 80
		                },
		                title: {
				            text: null				           
				        },
				        credits: {
				            enabled: false
				        },
				        tooltip: {
				            pointFormat: ''
				        },
				        lang: {
					    	some_key: 'Download Chart'
					    },
				        exporting: {
						    enabled: true,
						    sourceWidth: 900,
						    buttons: {
						    	contextButton: {
						    		menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                    symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },					                   
				                    _titleKey: 'some_key'
				                }
				            },
						    chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
						},
		                plotOptions: {
		                    pie: {
		                    	size:'100%',
		                        allowPointSelect: true,
		                        cursor: 'pointer',
		                        dataLabels: {
		                            enabled: true,
		                            format: '<b>{point.name}</b>',
		                            /*formatter: function () {
		                            	if((scope.type == 1 || scope.type == 4 || scope.type == 5) && this.point.y == 1){
		                            		return '<b>' + this.point.name + '</b>';
		                            	}else if((scope.type == 2) && this.point.y == 1){
		                            		return '<b>' + this.point.name + '</b> ('+this.point.y+' min)'
		                            	}else if((scope.type == 2) && this.point.y > 1){
		                            		return '<b>' + this.point.name + '</b> ('+this.point.y+' mins)'
		                            	}else{
		                            		return '<b>' + this.point.name + '</b> ('+this.point.y+')';
		                            	}
		        	                },*/
		                            style:{
		                            	width:'100'
		                            }
		                        },
		                        tooltip: {
		                            pointFormat: ''
		                        },
		                    },
		                    series: {
				                cursor: 'pointer',
				                point: {
				                    events: {
				                    	click: function () {
			                                scope.someCtrlFn({arg1: this.id, arg2: this.series.name});
			                           }
				                    }
				                }
				            }
		                },
		                series: [{
		                	name: scope.name,
		                    data: scope.data,
		                    animation: false
		                }]
		            });
		        }
        	});
        }
    };
}).directive('hcDountPieChart', function () {
    return {
        restrict: 'E',
        scope: {            
        	name: '@',
        	asc: '@',
        	desc: '@',
            data: '=',
    		someCtrlFn: '&callbackFn'
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		var colors = Highcharts.getOptions().colors,        		 
        	        categories = scope.data.categories,
        	        data = scope.data.data,
        	        browserData = [],
        	        versionsData = [],
        	        i,
        	        j,
        	        k = 0,
        	        dataLen = data.length,
        	        drillDataLen,
        	        brightness;

        	    // Build the data arrays
        	    for (i = 0; i < dataLen; i += 1) {
        	    	if (k === 0 || k===1 || k===3){
        	    		k = 4;
       				}
        	    	brightness = 0.2 - (i / dataLen) / 5;  
        	        browserData.push({
        	        	id: data[i].id,
        	            name: categories[i],
        	            y: data[i].y,
        	            color: Highcharts.Color(colors[k]).brighten(brightness).get()//colors[Number(i+5)] data[i].color
        	        });
        	        k += 1;
	       			if (k > 10) {
	       				k = 0;
	       			}
        	        drillDataLen = data[i].drilldown.data.length;
        	        for (j = 0; j < drillDataLen; j += 1) {
        	        	if (j===0) {
        	        		brightness = "#7cb5ec";
        	        	} else {
        	        		brightness = "#f7a35c";
        	        	}
        	            versionsData.push({
        	                name: data[i].drilldown.categories[j],
        	                y: data[i].drilldown.data[j],
        	                color: brightness,
        	                id: data[i].id
        	            });
        	        }
        	    }

        	    // Create the chart
        	    Highcharts.chart(element[0], {
        	        chart: {
        	        	type: 'pie',
        	            marginBottom: 80
        	        },
        	        title: {
        	            text: null
        	        },
        	        yAxis: {
        	            title: {
        	                text: null
        	            }
        	        },
        	        plotOptions: {
        	            pie: {
        	                shadow: false,
        	                center: ['50%', '50%']
        	            }
        	        },
        	        lang: {
				    	some_key: 'Download Chart'
				    },
				    credits: {
			            enabled: false
			        },
    	          	exporting: {
					    enabled: true,
					    sourceWidth: 900,
					    buttons: {
					    	contextButton: {
					    		menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
								symbol: 'url(assets/images/download.png)',		
				                symbolY: 25,
				                theme: {
			                      states: {
			                            hover: {
			                                fill: null
			                            },
			                            select: {
			                                fill: null
			                            }
			                        }    
			                    },					                   
			                    _titleKey: 'some_key'
			                }
			            },
					    chartOptions: {
			                title: {
			                    text: scope.asc
			                },
			                subtitle: {
			                 	text: scope.desc
			                }
			            }
					},
        	        tooltip: {
        	            valueSuffix: ' hr'
        	        },
        	        series: [{
        	            name: scope.name,
        	            data: browserData,
        	            size: '60%',
        	            dataLabels: {
	    	                formatter: function () {
	    	                    return null;
	    	                }
        	            },
        	            tooltip: {
				            pointFormat: ''
				        },
				        cursor: 'pointer',
		                point: {
		                    events: {
		                    	click: function () {
	                                scope.someCtrlFn({arg1: this.id, arg2: this.series.name});
	                           }
		                    }
		                }
        	        }, {
        	            name: scope.name,
        	            data: versionsData,
        	            size: '80%',
        	            innerSize: '60%',
        	            dataLabels: {
        	                formatter: function () {
        	                    return this.y > 0 ? '<b>' + this.point.name + '</b>' : null;
        	                },
        	                style:{
                            	width:'100'
                            }
        	            },
        	            tooltip: {
				            pointFormat: ''
				        },
				        cursor: 'pointer',
		                point: {
		                    events: {
		                    	click: function () {
	                                scope.someCtrlFn({arg1: this.series.chart.get(this.options.id).id, arg2: this.series.name});
	                           }
		                    }
		                }
        	        }]
        	    });
        	});
        }
    };
}).directive('hcSplineChart', function () {
    return {
        restrict: 'E',
        scope: {        	
            data: '=',
            asc: '@',
        	desc: '@'            
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        			var dataTemp = scope.data;
    				var coolingR = 0, coolingG = 0, coolingB = 255;
    				var heatingR = 255, heatingG = 100, heatingB = 0;
    				var IATR = 0, IATG = 255, IATB = 0;
    				var OATR = 0, OATG = 0, OATB = 0;
    				var tempRecords = [];
    			 	for (var i = 0; i < dataTemp.length; i += 1) {
    			 		if (dataTemp[i].paramId === 1) {
    						dataTemp[i]["color"] = "rgb("+IATR+","+IATG+","+IATB+")";
    						IATG = IATG - 50;
    					}
    			 		if (dataTemp[i].paramId === 2) {
    						dataTemp[i]["color"] = "rgb("+OATR+","+OATG+","+OATB+")";
							OATR = OATR + 30;
    			 	 		OATG = OATG + 30;
    			 	 		OATB = OATB + 30;
    					}
    					if (dataTemp[i].paramId === 3) {
    						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
							coolingR = coolingR + 50;
    			 	 		coolingG = coolingG + 50;
    					}
    					if (dataTemp[i].paramId === 4) {
    						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
							heatingG = heatingG + 30;
    			 	 		heatingB = heatingB + 30;
    					}
    					if (dataTemp[i].data.length === 1) {
    						var tempData = dataTemp[i].data;
    						dataTemp[i].id = '' + i;
    						
    						var temp = {};
    						temp["linkedTo"] = '' + i;
    						temp["data"] = tempData;
    						temp["color"] = dataTemp[i].color;
    						temp["name"] = dataTemp[i].name;
    						temp["paramFullName"] = dataTemp[i].paramFullName;
    						temp["deviceName"] = dataTemp[i].deviceName;
    						temp.marker = {};
    						temp.marker.enabled = true;
    						temp.marker.symbol = 'circle';
    						tempRecords.push(temp)
    						dataTemp[i].data = null
        			 	}
					}
    			 	for (var k = 0; k < tempRecords.length; k += 1) {
    			 		dataTemp.push(tempRecords[k])
					}
        			Highcharts.chart(element[0], {
        				chart: {
        					type: 'line',
        					zoomType: 'x',
        					spacingLeft: 13,
        					resetZoomButton: {
    			                position: {
    			                    align: 'right', // by default
    			                    verticalAlign: 'top', // by default
    			                    x: -10,
    			                    y: 45
    			                },
    			                relativeTo: 'chart'
    			            }
        				},
        				title: null,
        				xAxis: {
        					type:'datetime',
        					startOnTick: true,
        			        endOnTick: true,
        					dateTimeLabelFormats: {
        						month: '%e. %b',
				                year: '%b'
        	                },  
    						labels: {
        						style: {
        							color: '#89A54E'
        						},
    						}
        				},
        				lang: {
					    	some_key: 'Download Chart'
					    },
        				exporting : {
        					enabled: true,
        					sourceWidth: 900,
        					buttons: {
        						contextButton: {
        							menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                   	symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },							                   
				                    _titleKey: 'some_key'
				                }
				            },
        		            allowHTML: true,
        		            chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
        				},
        				yAxis: {
        					labels: {
        						format: '{value}<sup>o</sup>F',
        						style: {
        							color: '#89A54E'
        						},
        						useHTML:true
        					},
        					min: 40,
        					lineWidth:1,
        					tickWidth:1,
        					tickLength:3,
        					tickInterval: 10,
        					title: {
        						text: '',
        						style: {
        							color: '#89A54E'
        						}
        					}
        				},
        				credits: {
        		            enabled: false
        		        },
        				tooltip: {
        					shared:false, 
        					useHTML:true, 
        					formatter:null,
        					valueSuffix: '<sup>o</sup>F',
        					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName}): {point.y}'
        				},
        				legend: {
        					align: 'center',
        					verticalAlign: 'bottom',
        					layout: 'horizontal',
        					symbolHeight: 5,
           		         	symbolWidth: 10,
           		         	symbolRadius: 10,
           		         	padding: 3,
           		         	itemMarginTop: 5,
           		         	itemMarginBottom: 5
        				},        				
        				plotOptions:{
        					series:{
        						pointStart: Date.UTC(2016, 0, 1),
        						step: 'left',
        						marker: {
         		                    enabled: false
         		                }
        					}
        				},
        				series: dataTemp
        			});
        		});
        	});
        }
    };
}).directive('hcSplineFanChart', function () {
    return {
        restrict: 'E',
        scope: {        	
            data: '=',
            asc: '@',
        	desc: '@',            
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
	        			var dataTemp = scope.data;
	    				var fanR = 0, fanG = 255, fanB = 0;
	    				var coolingR = 0, coolingG = 0, coolingB = 255;
	    				var heatingR = 255, heatingG = 100, heatingB = 0;
	    			 	for (var i = 0; i < dataTemp.length; i += 1) {
	    			 		if (dataTemp[i].paramId === 7) {
	    						dataTemp[i]["color"] = "rgb("+fanR+","+fanG+","+fanB+")";
        						fanG = fanG - 50;
	    					}
	    					if (dataTemp[i].paramId === 5 || dataTemp[i].paramId === 31 || dataTemp[i].paramId === 59 || dataTemp[i].paramId === 61) {
	    						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
    							coolingR = coolingR + 100;
	    			 	 		coolingG = coolingG + 100;
        					}
	    					if (dataTemp[i].paramId === 6 || dataTemp[i].paramId === 33 || dataTemp[i].paramId === 63 || dataTemp[i].paramId === 64) {
	    						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
    							heatingG = heatingG + 60;
	    			 	 		heatingB = heatingB + 60;
        					}
						}
        				Highcharts.chart(element[0], {
        					 chart: {
             					type: 'spline',
             					height:275,
             					spacingLeft: 50,
             					zoomType: 'x',
            					resetZoomButton: {
        			                position: {
        			                    align: 'right', // by default
        			                    verticalAlign: 'top', // by default
        			                    x: -10,
        			                    y: 45
        			                },
        			                relativeTo: 'chart'
        			            }
             				},
             				title: null,
             				xAxis: {
             					type:'datetime',
             					startOnTick: true,
            			        endOnTick: true,
             					dateTimeLabelFormats: {
             						month: '%e. %b',
             						year: '%b'
             	                },  
         						labels: {
             						style: {
             							color: '#89A54E'
             						},
         						}
             				},
             				yAxis: {
             					allowDecimals: false,
             					min: 0,
             					max: scope.data.length + 1,
             					title: null,
							   	labels: {
							       enabled: false
							   	},
	        					lineWidth:1,
	        					tickWidth:1,
	        					tickLength:0
             				},
             				lang: {
						    	some_key: 'Download Chart'
						    },
             				exporting : {
        						enabled: true,
        						sourceWidth: 900,
        						buttons: {
        							contextButton: {
        								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
					                   	symbol: 'url(assets/images/download.png)',		
						                symbolY: 25,
						                theme: {
					                      states: {
					                            hover: {
					                                fill: null
					                            },
					                            select: {
					                                fill: null
					                            }
					                        }    
					                    },							                   
					                    _titleKey: 'some_key'
					                }
					            },
        		           		allowHTML: true,
	        		            chartOptions: {
					                title: {
					                    text: scope.asc
					                },
					                subtitle: {
					                 	text: scope.desc
					                }
					            }
	        				},
             				credits: {
             		            enabled: false
             		        },
             				tooltip: {
             					shared:false, 
            					useHTML:true,
            					formatter: null,
            					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName})'
     				        },
             				legend: {
             					align: 'center',
            					verticalAlign: 'bottom',
            					layout: 'horizontal',
            					symbolHeight: 5,
               		         	symbolWidth: 10,
               		         	symbolRadius: 10,
               		         	padding: 3,
               		         	itemMarginTop: 5,
               		         	itemMarginBottom: 5
             				},
             				plotOptions:{
             					series:{
             						pointStart: Date.UTC(2016, 0, 1),
             						marker: {
             		                    enabled: false
             						},
             						lineWidth: 20,
             						linecap:'square'
             					}
             				}, 
        				    series: dataTemp 
        			});    			
        		});
        	});
        }
    };
}).directive('hcSplineModeChartDevice', function () {
    return {
        restrict: 'E',
        scope: {    
        	asc: '@',
        	desc: '@',    	
            data: '='
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        			
        			var dataTemp = scope.data;
    				        				
    			 	for (var i = 0; i < dataTemp.length; i += 1) {
    					if (dataTemp[i].paramId === 39) {
    						dataTemp[i]["color"] = "#90ed7d";
    					}
    					if (dataTemp[i].paramId === 40) {
    						dataTemp[i]["color"] = "#7cb5ec";
    					}
    					if (dataTemp[i].paramId === 41) {
    						dataTemp[i]["color"] = "#434348";
    					}
					}
        			Highcharts.chart(element[0], {
        				chart: {
        					type: 'line',
        					zoomType: 'x',
        					resetZoomButton: {
    			                position: {
    			                    align: 'right', // by default
    			                    verticalAlign: 'top', // by default
    			                    x: -10,
    			                    y: 45
    			                },
    			                relativeTo: 'chart'
    			            }
        				},
        				title: null,
        				xAxis: {
        					type:'datetime',
        					startOnTick: true,
        			        endOnTick: true,
        					dateTimeLabelFormats: {
        						month: '%e. %b',
				                year: '%b'
        	                },
    						labels: {
        						style: {
        							color: '#89A54E'
        						},
    						}
        				},
        				yAxis: {
        					 max:4,
        					 lineWidth:1,
        					 tickWidth:1,
        					 tickLength:4,
        					 categories: [
					                'Off',
					                'Heat',
					                'Cool',
					                'Auto',
					                'Hold'
					            ],
        					title: {
        						text: null        					
        					}
        				},
        				credits: {
        		            enabled: false
        		        },
        		        lang: {
					    	some_key: 'Download Chart'
					    },
         				exporting : {
    						enabled: true,
    						sourceWidth: 900,
    						buttons: {
    							contextButton: {
    								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                    symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },							                   
				                    _titleKey: 'some_key'
				                }
				            },
						    chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
						},
        				tooltip: {
    						shared:false, 
        					useHTML:true, 
        					formatter: null,
         					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName})'
        				},
        				legend: {
        					align: 'center',
        					verticalAlign: 'bottom',
        					layout: 'horizontal',
        					symbolHeight: 5,
           		         	symbolWidth: 10,
           		         	symbolRadius: 10,
           		         	padding: 3,
           		         	itemMarginTop: 5,
           		         	itemMarginBottom: 5
        				},        				
        				plotOptions:{
        					series:{
        						pointStart: Date.UTC(2016, 0, 1),
        						step: 'left',
        						marker: {
         		                    enabled: false
         		                }
        					}
        				},
        				series: dataTemp 
        			});
        		});
        	});
        }
    };
}).directive('hcSplineChartDevice', function () {
    return {
        restrict: 'E',
        scope: {      
        	asc: '@',
        	desc: '@',  	
            data: '='
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        			var dataTemp = scope.data;
    				var coolingR = 0, coolingG = 0, coolingB = 255;
    				var heatingR = 255, heatingG = 100, heatingB = 0;
    				var IATR = 0, IATG = 255, IATB = 0;
    				var OATR = 0, OATG = 0, OATB = 0;
    				var tempRecords = [];
    			 	for (var i = 0; i < dataTemp.length; i += 1) {
    			 		if (dataTemp[i].paramId === 1) {
    						dataTemp[i]["color"] = "rgb("+IATR+","+IATG+","+IATB+")";
    						IATG = IATG - 50;
    					}
    			 		if (dataTemp[i].paramId === 2) {
    						dataTemp[i]["color"] = "rgb("+OATR+","+OATG+","+OATB+")";
    			 	 		OATR = OATR + 30;
    			 	 		OATG = OATG + 30;
    			 	 		OATB = OATB + 30;
    					}
    					if (dataTemp[i].paramId === 3) {
    						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
							coolingR = coolingR + 50;
    			 	 		coolingG = coolingG + 50;
    					}
    					if (dataTemp[i].paramId === 4) {
    						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
							heatingG = heatingG + 30;
    			 	 		heatingB = heatingB + 30;
    					}
    					if (dataTemp[i].data.length === 1) {
    						var tempData = dataTemp[i].data;
    						dataTemp[i].id = '' + i;
    						
    						var temp = {};
    						temp["linkedTo"] = '' + i;
    						temp["data"] = tempData;
    						temp["color"] = dataTemp[i].color;
    						temp["name"] = dataTemp[i].name;
    						temp["paramFullName"] = dataTemp[i].paramFullName;
    						temp["deviceName"] = dataTemp[i].deviceName;
    						temp.marker = {};
    						temp.marker.enabled = true;
    						temp.marker.symbol = 'circle';
    						tempRecords.push(temp)
    						dataTemp[i].data = null
        			 	}
					}
    			 	for (var k = 0; k < tempRecords.length; k += 1) {
    			 		dataTemp.push(tempRecords[k])
					}
        			Highcharts.chart(element[0], {
        				chart: {
        					type: 'line',
        					zoomType: 'x',
        					spacingLeft: 13,
        					resetZoomButton: {
    			                position: {
    			                    align: 'right', // by default
    			                    verticalAlign: 'top', // by default
    			                    x: -10,
    			                    y: 45
    			                },
    			                relativeTo: 'chart'
    			            }
        				},
        				title: null,
        				xAxis: {
        					type:'datetime',
        					startOnTick: true,
        			        endOnTick: true,
        					dateTimeLabelFormats: {
        						month: '%e. %b',
				                year: '%b'
        	                },  
    						labels: {
        						style: {
        							color: '#89A54E'
        						},
    						}
        				},
        				lang: {
					    	some_key: 'Download Chart'
					    },
         				exporting : {
    						enabled: true,
    						allowHTML: true,
    						sourceWidth: 900,
    						buttons: {
    							contextButton: {
    								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                    symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },							                   
				                    _titleKey: 'some_key'
				                }
				            }, 				
						    chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
						},
        				yAxis: {
        					labels: {
        						format: '{value}<sup>o</sup>F',
        						style: {
        							color: '#89A54E'
        						},
        						useHTML:true
        					},
        					min: 40,
        					lineWidth:1,
        					tickWidth:1,
        					tickLength:3,
        					tickInterval: 10,
        					title: {
        						text: '',
        						style: {
        							color: '#89A54E'
        						}
        					}
        				},
        				credits: {
        		            enabled: false
        		        },
        				tooltip: {
        					shared:false, 
        					useHTML:true, 
        					formatter:null,
        					valueSuffix: '<sup>o</sup>F',
        		        	pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName}): {point.y}'
        				},
        				legend: {
        					align: 'center',
        					verticalAlign: 'bottom',
        					layout: 'horizontal',
        					symbolHeight: 5,
           		         	symbolWidth: 10,
           		         	symbolRadius: 10,
           		         	padding: 3,
           		         	itemMarginTop: 5,
           		         	itemMarginBottom: 5
        				},
        				plotOptions:{
        					series:{
        						pointStart: Date.UTC(2016, 0, 1),
        						step: 'left',
        						marker: {
         		                    enabled: false
         		                }
        					}
        				},
        				series: dataTemp 
        			});
        		});
        	});
        }
    };
}).directive('hcSplineFanChartDevice', function () {
    return {
        restrict: 'E',
        scope: {     
        	asc: '@',
        	desc: '@',
        	count: '@',
            data: '=',
            height:'='
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        				var dataTemp = scope.data;
        				var fanR = 0, fanG = 255, fanB = 0;
        				var coolingR = 0, coolingG = 0, coolingB = 255;
        				var heatingR = 255, heatingG = 100, heatingB = 0;
        				        				
        			 	for (var i = 0; i < dataTemp.length; i += 1) {
        			 		if (dataTemp[i].paramId === 7) {
	    						dataTemp[i]["color"] = "rgb("+fanR+","+fanG+","+fanB+")";
        						fanG = fanG - 50;
	    					}
        					if (dataTemp[i].paramId === 5 || dataTemp[i].paramId === 31 || dataTemp[i].paramId === 59 || dataTemp[i].paramId === 61) {
        						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
    							coolingR = coolingR + 50;
    							coolingG = coolingG + 50;
        			 	 		if (scope.count == 1) {
        			 	 			coolingR = coolingR + 50;
            			 	 		coolingG = coolingG + 50;
        						}
        					}
        					if (dataTemp[i].paramId === 6 || dataTemp[i].paramId === 33 || dataTemp[i].paramId === 63 || dataTemp[i].paramId === 64) {
        						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
    							heatingG = heatingG + 30;
        			 	 		heatingB = heatingB + 30;
        						if (scope.count == 1) {
        			 	 			heatingG = heatingG + 30;
            			 	 		heatingB = heatingB + 30;
        						}
        					}
    					}
        				Highcharts.chart(element[0], {
        					 chart: {
             					type: 'spline',
             					zoomType: 'x',
             					spacingLeft: 50,
             					height: scope.height ? 275 : null,
            					resetZoomButton: {
        			                position: {
        			                    align: 'right', // by default
        			                    verticalAlign: 'top', // by default
        			                    x: -10,
        			                    y: 45
        			                },
        			                relativeTo: 'chart'
        			            }
             				},
             				title: null,
             				xAxis: {
             					type:'datetime',
             					startOnTick: true,
            			        endOnTick: true,
             					dateTimeLabelFormats: {
             						month: '%e. %b',
             						year: '%b'
             	                },  
         						labels: {
             						style: {
             							color: '#89A54E'
             						},
         						}
             				},
             				yAxis: {
             					allowDecimals: false,
             					min: 0,
             					max: scope.data.length + 1,
             					title: null,
							   	labels: {
							       enabled: false
							   	},
	        					lineWidth:1,
	        					tickWidth:1,
	        					tickLength:0
             				},
             				lang: {
						    	some_key: 'Download Chart'
						    },
	         				exporting : {
	    						enabled: true,
	    						sourceWidth: 900,
	    						buttons: {
	    							contextButton: {
	    								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
					                    symbol: 'url(assets/images/download.png)',		
						                symbolY: 25,
						                theme: {
					                      states: {
					                            hover: {
					                                fill: null
					                            },
					                            select: {
					                                fill: null
					                            }
					                        }    
					                    },						                   
					                    _titleKey: 'some_key'
					                }
					            }, 			
							    chartOptions: {
					                title: {
					                    text: scope.asc
					                },
					                subtitle: {
					                 	text: scope.desc
					                }
					            }
							},
             				credits: {
             		            enabled: false
             		        },
             				tooltip: {
             					shared:false, 
            					useHTML:true, 
            					formatter: null,
             					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName})'
     				        },
     				       legend: {
            					align: 'center',
            					verticalAlign: 'bottom',
            					layout: 'horizontal',
            					symbolHeight: 5,
   	           		         	symbolWidth: 10,
   	           		         	symbolRadius: 10,
   	           		         	padding: 3,
   	           		         	itemMarginTop: 5,
   	           		         	itemMarginBottom: 5
            				},
             				plotOptions:{
             					series:{
             						pointStart: Date.UTC(2016, 0, 1),
             						marker: {
             							enabled: false
             		                },
             		                lineWidth: 20,
             		                linecap:'square'
             					}
             				}, 
        				    series: dataTemp   
        			});    			
        		});
        	});
        }
    };
}).directive('hcSplineDaysChartDevice', function () {
    return {
        restrict: 'E',
        scope: {      
        	asc: '@',
        	desc: '@',  	
            data: '='
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        			var dataTemp = scope.data;
    				var coolingR = 0, coolingG = 0, coolingB = 255;
    				var heatingR = 255, heatingG = 100, heatingB = 0;
    				var tempRecords = [];
    			 	for (var i = 0; i < dataTemp.length; i += 1) {
    					if (dataTemp[i].paramId === 58 || dataTemp[i].paramId === 62) {
    						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
							coolingR = coolingR + 50;
    			 	 		coolingG = coolingG + 50;
    					}
    					if (dataTemp[i].paramId === 60 || dataTemp[i].paramId === 65) {
    						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
							heatingG = heatingG + 30;
    			 	 		heatingB = heatingB + 30;
    					}
    					/*if (dataTemp[i].data.length === 1) {
    						var tempData = dataTemp[i].data;
    						dataTemp[i].id = '' + i;
    						
    						var temp = {};
    						temp["linkedTo"] = '' + i;
    						temp["data"] = tempData;
    						temp["color"] = dataTemp[i].color;
    						temp["name"] = dataTemp[i].name;
    						temp["paramFullName"] = dataTemp[i].paramFullName;
    						temp["deviceName"] = dataTemp[i].deviceName;
    						temp.marker = {};
    						temp.marker.enabled = true;
    						temp.marker.symbol = 'circle';
    						tempRecords.push(temp)
    						dataTemp[i].data = null
        			 	}*/
					}
    			 	for (var k = 0; k < tempRecords.length; k += 1) {
    			 		dataTemp.push(tempRecords[k])
					}
        			Highcharts.chart(element[0], {	
        				chart: {
        					type: 'line',
        					zoomType: 'x',
        					resetZoomButton: {
    			                position: {
    			                    align: 'right', // by default
    			                    verticalAlign: 'top', // by default
    			                    x: -10,
    			                    y: 45
    			                },
    			                relativeTo: 'chart'
    			            }
        				},
        				title: null,
        				xAxis: {
        					type:'datetime',
        					startOnTick: true,
        			        endOnTick: true,
        					dateTimeLabelFormats: {
        						month: '%e. %b',
				                year: '%b'
        	                },  
    						labels: {
        						style: {
        							color: '#89A54E'
        						},
    						}
        				},
        				yAxis: {
        					labels: {
        						format: '{value}',
        						style: {
        							color: '#89A54E'
        						},
        						useHTML:true
        					},
        					min: 0,
        					lineWidth:1,
        					tickWidth:1,
        					tickLength:3,
        					tickInterval: 20,
        					title: {
        						text: 'Days',
        						style: {
        							color: '#89A54E'
        						}
        					}
        				},
        				exporting : {
    						enabled: true,
    						sourceWidth: 900,
    						buttons: {
    							contextButton: {
    								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                    symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },							                   
				                    _titleKey: 'some_key'
				                }
				            }, 			
						    chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
						},
        				credits: {
        		            enabled: false
        		        },
        				tooltip: {
        					shared:false, 
        					useHTML:true, 
        					formatter: null,
        					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName}): {point.y}'
        				},
        				legend: {
        					align: 'center',
        					verticalAlign: 'bottom',
        					layout: 'horizontal',
        					symbolHeight: 5,
           		         	symbolWidth: 10,
           		         	symbolRadius: 10,
           		         	padding: 3,
           		         	itemMarginTop: 5,
           		         	itemMarginBottom: 5
        				},        				
        				plotOptions:{
        					series:{
        						pointStart: Date.UTC(2016, 0, 1),
        						step: 'left',
        						marker: {
         		                    enabled: true
         		                }
        					}
        				},
        				series: dataTemp
        			}, function (chart) {
        		        var series = chart.series;
        		        $(series).each(function (i, serie) {
        		            if (serie.legendSymbol) serie.legendSymbol.destroy();
        		            //if (serie.legendLine) serie.legendLine.destroy();
        		        });
        		    });
        		});
        	});
        }
    };
}).directive('hcSplineStagesDegreeDaysChart', function () {
    return {
        restrict: 'E',
        scope: {      
        	asc: '@',
        	desc: '@',
        	count: '@', 
            data: '='
        },
        link: function (scope, element) {
        	scope.$watch('data', function (data) {
        		$(function () {
        			var dataTemp = scope.data;
    				var coolingR = 0, coolingG = 0, coolingB = 255;
    				var heatingR = 255, heatingG = 100, heatingB = 0;
    				var tempRecords = [];
    			 	for (var i = 0; i < dataTemp.length; i += 1) {
    					if (dataTemp[i].paramId === 59 || dataTemp[i].paramId === 61 || dataTemp[i].paramId === 62) {
    						dataTemp[i]["color"] = "rgb("+coolingR+","+coolingG+","+coolingB+")";
							coolingR = coolingR + 50;
    			 	 		coolingG = coolingG + 50;
    						if (scope.count == 1 && dataTemp[i].paramId === 59 || dataTemp[i].paramId === 61) {
    			 	 			coolingR = coolingR + 50;
        			 	 		coolingG = coolingG + 50;
    						}
    					}
    					if (dataTemp[i].paramId === 63 || dataTemp[i].paramId === 64 || dataTemp[i].paramId === 65) {
    						dataTemp[i]["color"] = "rgb("+heatingR+","+heatingG+","+heatingB+")";
							heatingG = heatingG + 30;
    			 	 		heatingB = heatingB + 30;
    						if (scope.count == 1 && dataTemp[i].paramId === 63 || dataTemp[i].paramId === 64) {
    							heatingG = heatingG + 30;
        			 	 		heatingB = heatingB + 30;
    						}
    					}
    					
    					if (dataTemp[i].data.length === 1) {
    						var tempData = dataTemp[i].data;
    						dataTemp[i].id = '' + i;
    						
    						var temp = {};
    						temp["linkedTo"] = '' + i;
    						temp["data"] = tempData;
    						temp["color"] = dataTemp[i].color;
    						temp["name"] = dataTemp[i].name;
    						temp["paramFullName"] = dataTemp[i].paramFullName;
    						temp["deviceName"] = dataTemp[i].deviceName;
    						temp.marker = {};
    						temp.marker.enabled = true;
    						temp.marker.symbol = 'circle';
    						tempRecords.push(temp)
    						dataTemp[i].data = null
        			 	}
    					if (dataTemp[i].paramId === 62 || dataTemp[i].paramId === 65 || dataTemp[i].paramId === 58 || dataTemp[i].paramId === 60) {
    						var tempData = dataTemp[i].data;
    						dataTemp[i].id = '' + i;
    						
    						var temp = {};
    						temp["linkedTo"] = '' + i;
    						temp["data"] = tempData;
    						temp["color"] = dataTemp[i].color;
    						temp["name"] = dataTemp[i].name;
    						temp["paramFullName"] = dataTemp[i].paramFullName;
    						temp["deviceName"] = dataTemp[i].deviceName;
    						temp.marker = {};
    						temp.marker.enabled = true;
    						tempRecords.push(temp)
    						dataTemp[i].data = null
        			 	}
    					/*if (dataTemp[i].paramId === 62 || dataTemp[i].paramId === 65 || dataTemp[i].paramId === 58 || dataTemp[i].paramId === 60) {
    						dataTemp[i].marker = {};
    						dataTemp[i].marker.enabled = true;
    					}*/
					}
    			 	for (var k = 0; k < tempRecords.length; k += 1) {
    			 		dataTemp.push(tempRecords[k])
					}
        			Highcharts.chart(element[0], {	
        				chart: {
        					zoomType: 'xy',
        					resetZoomButton: {
    			                position: {
    			                    align: 'right', // by default
    			                    verticalAlign: 'top', // by default
    			                    x: -10,
    			                    y: 45
    			                },
    			                relativeTo: 'chart'
    			            }
        				},
        				title: null,
        				xAxis: {
        					type:'datetime',
        					startOnTick: true,
        			        endOnTick: true,
        					dateTimeLabelFormats: {
        						month: '%e. %b',
				                year: '%b'
        	                },  
    						labels: {
        						style: {
        							color: '#89A54E'
        						},
    						}
        				},
        				yAxis: [{ // Primary yAxis
							        labels: {
		        						format: '{value}',
		        						style: {
		        							color: '#89A54E'
		        						},
		        						useHTML:true
		        					},
		        					min: 0,
		        					lineWidth:1,
		        					tickWidth:1,
		        					tickLength:3,
		        					title: {
		        						text: 'Days',
		        						style: {
		        							color: '#89A54E'
		        						}
		        					}
							    }, { // Secondary yAxis
							        title: {
							            text: 'Stages',
							            style: {
				        							color: '#89A54E'
				        						}
							        },
							        labels: {
							            format: '{value}',
							            style: {
				        							color: '#89A54E'
				        						}
							        },        
							        opposite: true
							    }],
        				exporting : {
    						enabled: true,
    						sourceWidth: 900,
    						buttons: {
    							contextButton: {
    								menuItems: Highcharts.getOptions().exporting.buttons.contextButton.menuItems.slice(2),
				                    symbol: 'url(assets/images/download.png)',		
					                symbolY: 25,
					                theme: {
				                      states: {
				                            hover: {
				                                fill: null
				                            },
				                            select: {
				                                fill: null
				                            }
				                        }    
				                    },							                   
				                    _titleKey: 'some_key'
				                }
				            }, 			
						    chartOptions: {
				                title: {
				                    text: scope.asc
				                },
				                subtitle: {
				                 	text: scope.desc
				                }
				            }
						},
        				credits: {
        		            enabled: false
        		        },
        				tooltip: {
        					shared:false, 
        					useHTML:true, 
        					formatter: null,
        					pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.options.paramFullName} ({series.options.deviceName}): {point.y}'
        				},
        				legend: {
        					align: 'center',
        					verticalAlign: 'bottom',
        					layout: 'horizontal',
        					symbolHeight: 5,
           		         	symbolWidth: 10,
           		         	symbolRadius: 10,
           		         	padding: 3,
           		         	itemMarginTop: 5,
           		         	itemMarginBottom: 5
        				},        				
        				plotOptions:{
        					series:{
        						pointStart: Date.UTC(2016, 0, 1),
        						step: 'left',
        						marker: {
         		                    enabled: false
         		                }
        					}
        				},
        				series: dataTemp
        			}, function (chart) {
        		        var series = chart.series;
        		        $(series).each(function (i, serie) {
        		            if (serie.legendSymbol) serie.legendSymbol.destroy();
        		            //if (serie.legendLine) serie.legendLine.destroy();
        		        });
        		    });
        		});
        	});
        }
    };
})
