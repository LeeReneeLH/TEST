<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title> </title>
    <meta name="decorator" content="default" />
    <script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
  	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
    <script type="text/javascript" src="${ctxStatic}/zrender-2.1.1/zrender.js"></script>
    <c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
    	<script type="text/javascript" src="${fns:getConfig('firstPage.amap.js.url')}"></script>
    	<%-- <script type="text/javascript" src="${fns:getConfig('firstPage.amap.main.js.url')}"></script> --%>
    </c:if>
    <script type="text/javascript">
    var graphBc = "${fns:getConfig('firstPage.map.isOnline') eq '0' == true ? 'rgba(255,253,249,0.7)' : 'transparent'}";
    var amount;
    var clearAmount;
    var xAxis;
    var tooltip;
    var seriesData;
    var barOption;
    var pieOption;
    var reserveClearVolumeChart;
    var reserveClearAmountChart;
    var showToolBox = true;
    var carMarkerArray = new Array();
    var searchDate;
 	// 消息url连接符号
	var connSymbol;
	if($.browser.msie){
		if($.browser.version <= 8){
			showToolBox = false;
		}
	}
	
   $(document).ready(function() {
	   
	   $("#showGrapBtn").click(function(){
		   $('#showGrapBtn').attr('disabled',"disabled");
		   $('#showMapBtn').removeAttr("disabled");
		   $("#graphDiv").show();
		   $("#showGrapBtn").removeClass();
		   $("#showGrapBtn").attr("class", "btn");
		   $("#showMapBtn").removeClass();
		   $("#showMapBtn").attr("class", "btn btn-primary");
	   });
	   $("#showMapBtn").click(function(){
		   $('#showMapBtn').attr('disabled',"disabled");
		   $('#showGrapBtn').removeAttr("disabled");
		   $("#graphDiv").hide();
		   $("#showGrapBtn").removeClass();
		   $("#showGrapBtn").attr("class", "btn btn-primary");
		   $("#showMapBtn").removeClass();
		   $("#showMapBtn").attr("class", "btn");
	   });
	   
	   $('.carousel').carousel({
			  interval: 5000
			});
	   $("#mainbarsto").hide();
	   $("#businessStatus").hide();
	   $("#offLineMapDiv").hide();
	   $("#onLineMapDiv").hide();
	   
	   //地图显示
	   if (${fns:getConfig('firstPage.map.isOnline') eq '0'}) {
		   //显示高德在线地图
		   createOnLineMap();
	   } else {
		   // 显示Echarts离线地图
		   createOffLineMap();
	   }
 	   createGraph();
 	  todoBoxRefresh();
	});
   
    // 库存图表的配置项和数据
	var goodsStoreChartOption = {
		backgroundColor: graphBc,
		title : {
			//物品库存统计图
			text : "<spring:message code='report.stores.goods'/>"
		},
		tooltip : {
			
			trigger : 'axis'
		},
		legend : {
			type:'scroll',
			orient : 'vertical',
			right : 10,
			bottom : 20,
			data : []
			
		},
		grid : {
			left : '3%',
			right : '40%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
			  show : showToolBox,
		        orient: 'horizontal',      // 布局方式，默认为水平布局，可选为：
		                                   // 'horizontal' ¦ 'vertical'
		        x: 'right',                // 水平安放位置，默认为全图右对齐，可选为：
		                                   // 'center' ¦ 'left' ¦ 'right'
		                                   // ¦ {number}（x坐标，单位px）
		        y: 'top',                  // 垂直安放位置，默认为全图顶端，可选为：
		                                   // 'top' ¦ 'bottom' ¦ 'center'
		                                   // ¦ {number}（y坐标，单位px）
		        color : ['#1e90ff','#22bb22','#4b0082','#d2691e'],
		        backgroundColor: 'rgba(0,0,0,0)', // 工具箱背景颜色
		        borderColor: '#ccc',       // 工具箱边框颜色
		        borderWidth: 0,            // 工具箱边框线宽，单位px，默认为0（无边框）
		        padding: 5,                // 工具箱内边距，单位px，默认各方向内边距为5，
		        showTitle: true,
		},
		
		xAxis : {
			type : 'category',
			boundaryGap : true,
			data : []
		},
		yAxis : {
			name : '<spring:message code='report.money.element'/>',
			type : 'value'
		}
	};
	
	//库存查询
	function searchGoodsStoreData(goodsStoreChart) {
		
		goodsStoreChart.showLoading();
		var boo=false;
		$.ajax({
			url : '${ctx}/report/v01/store/getReportGraphData',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'json',
			async : true,
			cache : false,
			success : function(res) {
				
				goodsStoreChart.hideLoading();
				
				goodsStoreChart.clear();

					goodsStoreChart.setOption(goodsStoreChartOption);
					// 填入数据
					goodsStoreChart.setOption({
						legend : {
							data : res.legendDataList
						},
						xAxis : {
							
							data : res.xAxisDataList
						},
						    series : res.seriesDataList
						
					});
					
					boo=true;
			},
			error : function() {
				return null;
			}
		});
		return boo;
	}
	
	// 指定箱袋状态柱图的配置项和数据
	 var barStatusChartOption = {
	    backgroundColor: graphBc,
		title : {
			text : "<spring:message code='report.box.status'/>"
		},
		tooltip : {
			trigger : 'axis'
		},
		radiusAxis : {
				type : 'value'
		}, 
		    
		angleAxis: {
		        type: 'category',
		        data: [],
		        z: 10
		    },
		    polar: {
		    },
		    legend: {
		        show: true,
		        orient : 'vertical',
				right : 10,
				bottom: 50,
		        data: []
		    }
		}; 
	
	    //箱袋状态查询
		function searchStatusData(barStatusChart) {
			
			barStatusChart.showLoading();
			$.ajax({
				url : '${ctx}/ipadAjax/getFirstBoxStatusGraphData',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					
					    barStatusChart.hideLoading();
						barStatusChart.clear();
					
						barStatusChart.setOption(barStatusChartOption);
						
						for(var i=0;i<res.seriesDataList.length;i++){
							
							res.seriesDataList[i]['coordinateSystem']='polar';
						}
						// 填入数据
						barStatusChart.setOption({
							
							legend : {
								show:true,
								data : res.legendDataList
							},
							
							angleAxis : {
								data : res.xAxisDataList
							},
							
							series : res.seriesDataList
						});
				},
				error : function() {
					return null;
				}
			});
			
		}
		
		//调拨业务总金额查询
		function searchData() {
			var bRtn = false;
			//查询折线图
			amount.showLoading(); 
			$.ajax({
				url : '${ctx}/ipadAjax/AllocateAmount',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
					},
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					xAxis = res.xAxisDataList;
					series = res.seriesDataList;
					amount.hideLoading();
					amount.clear();
					//如果系列数据不为空则填入数据
					if(series != null && series.length != 0){
						amount.setOption({
						       baseOption: {
						    	   backgroundColor: graphBc,
						           timeline: {
						               axisType: 'category',
						               autoPlay: true,
						               playInterval: ${fns:getConfig("firstPage.echart.amount.playInterval")},
						               data: res.timelineDataList
						           },
						           tooltip: { },
						           grid : {
							   			left : '1%',
							   			right : '20%',
							   			bottom : '15%',
							   			containLabel : true
						   			},
						           legend: {
						        	    type:'scroll',
							   			orient : 'vertical',
							   			right : 10,
							   			bottom : 20,
						                data: res.legendDataList
						           },
						           calculable : true,
						           xAxis: [
						               {
						                   'type':'category',
						                   'axisLabel':{'interval':0},
						                   data:res.xAxisDataList,
						                   splitLine: {show: false}
						               }
						           ],
						           yAxis: [
						               {
						                   type: 'value',
						                   name: '<spring:message code='report.money.element'/>'
						               }
						           ],
						           series: res.baseOptionSeriesList
						       },
						       options:res.seriesDataList
						   });
							bRtn = true;
						}else{
							amount.setOption({
							       baseOption: {
							    	   backgroundColor: graphBc,
							           timeline: {
							               axisType: 'category',
							               autoPlay: true,
							               playInterval: ${fns:getConfig("firstPage.echart.amount.playInterval")},
							               data: res.timelineDataList
							           },
							           title: {
							               text: '调拨业务总金额'
							           },
							           tooltip: { },
							           grid : {
								   			left : '1%',
								   			right : '20%',
								   			bottom : '15%',
								   			containLabel : true
							   			}, 
							           legend: {
							        	    type:'scroll',
								   			orient : 'vertical',
								   			right : 10,
								   			bottom : 20,
							                data: res.legendDataList
							           },
							           calculable : true,
							           xAxis: [
							               {
							                   'type':'category',
							                   'axisLabel':{'interval':0},
							                   data:res.xAxisDataList,
							                   splitLine: {show: false}
							               }
							           ],
							           yAxis: [
							               {
							                   type: 'value',
							                   name: '<spring:message code='report.money.element'/>'
							               }
							           ],
							           series: res.baseOptionSeriesList
							       }
						});
						}
				},
				error : function() {
					bRtn = false;
				}
			});
			return bRtn;
		}
		
		//查询当前用户所属机构的常规业务及临时业务的状态
		function searchBusinessStatus(businessStatus){
			businessStatus.showLoading();
			var boo=false;
			$.ajax({
				url : '${ctx}/ipadAjax/businessStatus',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					
					businessStatus.hideLoading();
					
					businessStatus.clear();

						// 填入数据
						businessStatus.setOption({
							 backgroundColor: graphBc,
							 title: {
							        text: '网点业务状态统计图'
							    },
							    tooltip: {
							        trigger: 'axis'
							    },
							    legend: {
							    	type:'scroll',
							    	orient : 'vertical',
						   			right : 10,
						   			bottom : 20,
							        x: 'center',
							        data:res.legendDataList
							    },
							    radar:res.radarDataList,
							    series:res.seriesDataList
						});
						
						boo=true;
				},
				error : function() {
					return null;
				}
			});
			return boo;
		}
	
		// 预约清分业务量图表的配置项和数据
		var lineChartOption = {
			backgroundColor: graphBc,
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				type:'scroll',
				orient : 'vertical',
				right : 10,
				top: 40,
				bottom: 10,
				data : []
			},
			grid : {
				left : '3%',
				right : '15%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				boundaryGap : true,
				data : []
			},
			yAxis : {
				name:'<spring:message code = "report.business.degree"/>',
				type : 'value'
			}
		};
		// 预约清分业务量图表查询数据并填充
		function reserveClearVolume(){
			$.ajax({
				url : '${ctx}/report/v01/clear/reserveClearVolume',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					reserveClearVolumeChart.setOption(lineChartOption);
						
					$("#reserveClearVolume").show();
					// 填入数据
					reserveClearVolumeChart.setOption({
					    backgroundColor: graphBc,
					    title : {
							text : '<spring:message code='report.reserveClearVolume' />'
					    },
						legend : {
							data : res.legendDataList
						},
						xAxis : {
						    name : res.name,
							data : res.xAxisDataList
						},
						series : res.seriesDataList
					});
				}
			});
		}
		
		// 预约清分总金额图表查询数据并填充
		function reserveClearAmount(){
			$.ajax({
				url : '${ctx}/report/v01/clear/reserveClearAmount',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					reserveClearAmountChart.setOption({
						baseOption: {
							backgroundColor: graphBc,
							title: {
								text : '<spring:message code='report.reserveClearAmount' />'
							},
						    timeline: {
						        axisType: 'category',
						        /* 缩小长度 wzj 2017-11-30 begin */
						        width:'50%',
						        /* end */
						        autoPlay: true,
						        playInterval: ${fns:getConfig("firstPage.echart.amount.playInterval")},
						        data: res.timelineDataList
							},
						    tooltip:{},
						    grid : {
							   	left : '1%',
							   	right : '30%',
							   	bottom : '15%',
							   	containLabel : true,
							   	tooltip:tooltip
						   	},
						    legend: {
						        type:'scroll',
							   	orient : 'vertical',
							   	right : 10,
							   	bottom : 20,
						        data: res.legendDataList
						    },
						    calculable : true,
						    xAxis: [
						        {
						            'type':'category',
						            'axisLabel':{'interval':0},
						            data:res.xAxisDataList,
						            splitLine: {show: false}
						         }
						    ],
						    yAxis: [
						        {
						            type: 'value',
						            name: '<spring:message code='report.money.element'/>'
						        }
						    ],
						    series: res.baseOptionSeriesList
						},
						options:res.seriesDataList
					});
				}
			});
		}
		
		//清分业务出入库总金额查询
	 	function searchClearInOrOut() {
			var bRtn = false;
			//查询折线图
			clearAmount.showLoading(); 
			$.ajax({
				url : '${ctx}/report/v01/clear/clearAmount',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
					},
				dataType : 'json',
				async : true,
				cache : false,
				success : function(res) {
					tooltip = res.tooltip					
					xAxis = res.xAxisDataList;
					series = res.seriesDataList;
					clearAmount.hideLoading();
					clearAmount.clear();
					//如果系列数据不为空则填入数据
					if(series != null || series.length != 0){
						clearAmount.setOption({
						       baseOption: {
						    	   backgroundColor: graphBc,
						    	   title: {
										text : '<spring:message code='report.clearAmount' />'
								   },
						           timeline: {
						               axisType: 'category',
						               autoPlay: true,
						               /* 缩小长度 wzj 2017-11-30 begin */
						               width:'50%',
						               /* end */
						               playInterval: ${fns:getConfig("firstPage.echart.amount.playInterval")},
						               data: res.timelineDataList
						           },
						           tooltip:{},
						           grid : {
							   			left : '1%',
							   			right : '30%',
							   			bottom : '15%',
							   			containLabel : true,
							   			tooltip:tooltip
						   			},
						           legend: {
						        	    type:'scroll',
							   			orient : 'vertical',
							   			right : 10,
							   			bottom : 20,
						                data: res.legendDataList
						           },
						           calculable : true,
						           xAxis: [
						               {
						                   'type':'category',
						                   'axisLabel':{'interval':0},
						                   data:res.xAxisDataList,
						                   splitLine: {show: false}
						               }
						           ],
						           yAxis: [
						               {
						                   type: 'value',
						                   name: '<spring:message code='report.money.element'/>'
						               }
						           ],
						           series: res.baseOptionSeriesList
						       },
						       options:res.seriesDataList
						   });
							bRtn = true;
						}
				},
				error : function() {
					bRtn = false;
				}
			});
			return bRtn;
		} 
		
	    //使用Echart3创建数据图形
		function createGraph() {
	  		 require.config({
			        packages: [
			            {
			                name: 'echarts3',
			                location: '${ctxStatic}/echart3',
			                main: 'echarts'
			            }
			        ]
			    });
		   
			 require([
			          'echarts3'
			      ],function(echarts){
				 	var mainbarstoShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.mainbarsto'))};
				 	var mainBarStatsShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.mainBarStats'))};
				 	var businessStatusShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.businessStatus'))};
				 	var clearAmountShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.clearAmount'))};
				 	var amountShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.amount'))};
				 	var reserveClearVolumeShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.reserveClearVolume'))};
				 	var reserveClearAmountShow = ${fns:toJson(fns:getDbConfig('mainGraph.show.reserveClearAmount'))};
				 	var officeType = ${fns:toJson(fns:getUser().office.type)};
				 	
					if(mainbarstoShow.indexOf(officeType) != -1){
						 $("#mainbarsto").show();
					    var goodsStoreChart = echarts.init(document.getElementById('mainbarsto'));
					    searchGoodsStoreData(goodsStoreChart); 
					}  
					//箱袋状态
					if(mainBarStatsShow.indexOf(officeType) != -1){
						var barStatusChart = echarts.init(document.getElementById('mainBarStats'));
						searchStatusData(barStatusChart);
					}
					
					if(businessStatusShow.indexOf(officeType) != -1){
						//当前用户所属机构的常规业务及临时业务状态(网点)
						$("#businessStatus").show();
						var businessStatus = echarts.init(document.getElementById('businessStatus'),'shine');
						searchBusinessStatus(businessStatus);
					}
					//调拨业务总金额
					if(amountShow.indexOf(officeType) != -1){
						var amountWidth = document.getElementById("amount").style.width;
						var amountHeight = document.getElementById("amount").style.height;
						amount = echarts.init(document.getElementById('amount'),'shine', {width : amountWidth, height : amountHeight});
						searchData();
					}
					//清分业务总金额
					if(clearAmountShow.indexOf(officeType) != -1){
						var clearAmountWidth = document.getElementById("clearAmount").style.width;
						var clearAmountHeight = document.getElementById("clearAmount").style.height;
						clearAmount = echarts.init(document.getElementById('clearAmount'),'shine', {width : clearAmountWidth, height : clearAmountHeight});
						searchClearInOrOut(); 
					}
					
					if(reserveClearVolumeShow.indexOf(officeType) != -1){
						var reserveClearVolumeWidth = document.getElementById("reserveClearVolume").style.width;
						var reserveClearVolumeHeight = document.getElementById("reserveClearVolume").style.height;
						reserveClearVolumeChart = echarts.init(document.getElementById('reserveClearVolume'),'shine',{width : reserveClearVolumeWidth, height : reserveClearVolumeHeight});
						reserveClearVolume(); 
					}
					
					if(reserveClearAmountShow.indexOf(officeType) != -1){
						var reserveClearAmountWidth = document.getElementById("reserveClearAmount").style.width;
						var reserveClearAmountHeight = document.getElementById("reserveClearAmount").style.height;
						reserveClearAmountChart = echarts.init(document.getElementById('reserveClearAmount'),'shine',{width : reserveClearAmountWidth, height : reserveClearAmountHeight});
						reserveClearAmount();
					}
			      });
	  	     }
	    // 使用Echarts2创建地图
		function createOffLineMap() {
			$("#offLineMapDiv").show();
	  		// 路径配置
	         require.config({
	  	        packages: [
	  	            {
	  	                name: 'echarts',
	  	                location: '${ctxStatic}/echart2',
	  	                main: 'echarts'
	  	            }
	  	        ]
	  	    });
	      	// 使用
	         require(
	             [
	                 'echarts',
	                 'echarts/chart/map' // 使 用柱状图就加载bar模块，按需加载
	             ],
	             function (ec) {
	            	 
	                 // 基于准备好的dom，初始化echarts图表
	                 var myChart = ec.init(document.getElementById('offLineMapDiv'));
	                 
	                 $.ajax({
		     				url : '${ctx}/sys/map/getMapGraphData',
		     				type : 'Post',
		     				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
		     				dataType : 'json',
		     				async : true,
		     				cache : false,
		     				success : function(res) {
		     					var showMaps = res.mapJsonInfo;
		   			 		 	var mapType = [];
		   			 		 	var mapGeoData = require('echarts/util/mapData/params');
		   			 		 	for (var city in showMaps) {
		   			 		   		mapType.push(city);
		   			 		    	// 自定义扩展图表类型
		   			 		    	mapGeoData.params[city] = {
			   			 		        getGeoJson: (function (mapIndex) {
			   			 		            var geoJsonName = showMaps[mapIndex];
			   			 		            return function (callback) {
			   			 		                $.getJSON('${ctxStatic}/mapjson/geoJson/' + geoJsonName + '.json', callback);
			   			 		            }
			   			 		        })(city)
			   			 		    }
			   			 		}
		     					//alert(res.geoCoordMap);
		     					option = {
			                		    color: ['gold','aqua','lime'],
			                		    tooltip : {
			                		        trigger: 'item',
			                		        formatter: function (v) {
			                		            return v[1].replace(':', ' > ');
			                		        }
			                		    },
			                		    dataRange: {
			                		    	show : false,
			                		        min : 0,
			                		        max : 100,
			                		        calculable : true,
			                		        color: ['#ff3333', 'orange', 'yellow','lime','aqua'],
			                		        textStyle:{
			                		            color:'#fff'
			                		        }
			                		    },
			                		    series : [
			                		        {
			                		            //name: res.name,
			                		            type: "<spring:message code='common.map.type'/>",
			                		            roam: false,
			                		            hoverable: false,
			                		            mapType: res.mapType,
			                		            itemStyle:{
			                		                normal:{
			                		                  label:{show:true},
			                		                    borderColor:'rgba(100,149,237,1)',
			                		                    borderWidth:1
			                		                },
			                		                emphasis:{label:{show:false}}
			                		            },
			                		            data:[],
			                		            geoCoord: res.geoCoordMap
			                		        },
			                		        {
			                		            name: "<spring:message code='common.map.name'/>",
			                		            type: "<spring:message code='common.map.type'/>",
			                		            mapType: res.mapType,
			                		            data:[],
			                		            markLine : {
			                		                smooth:true,
			                		                effect : {
			                		                    show: true,
			                		                    scaleSize: 1,
			                		                    period: 30,
			                		                    color: '#fff',
			                		                    shadowBlur: 10
			                		                },
			                		                itemStyle : {
			                		                    normal: {
			                		                    	label:{show:false,formatter: '{b0}'},
			                		                        borderWidth:1,
			                		                        lineStyle: {
			                		                            type: 'solid',
			                		                            shadowBlur: 10
			                		                        }
			                		                    }
			                		                },
			                		                data : res.lineDataList
			                		            },
			                		            markPoint : {
			                		                symbol:'emptyCircle',
			                		                symbolSize : function (v){
			                		                    return 5 + v/10
			                		                },
			                		                effect : {
			                		                    show: true,
			                		                    shadowBlur : 0
			                		                },
			                		                itemStyle:{
			                		                    normal:{
			                		                        label:{show:false}
			                		                    },
			                		                    emphasis:{label:{show:true, formatter: '{b}'}}
			                		                },
			                		                data : res.pointDataList
			                		            }
			                		        }
			                		    ]
			                		};
		     					
		     					myChart.setOption(option);
		     				},
		     				error : function() {
		     					return null;
		     				}
		     			}); 
	                
	             }
	         );
	  	 }
	    
		function readMessage(href){
			var urlStart = href.indexOf("/frame");
			var urlEnd = href.indexOf("?");
			var menuId = href.substr(href.indexOf("menuId=")+7,2);
			var messageId = href.substring(href.indexOf("&id=")+4);
			if(!isNaN(menuId)){
				if(!$("#image-"+menuId, window.parent.document).parent().hasClass("active")){
					$("#image-"+menuId, window.parent.document).click();
				}
				$("[href*='/frame']", window.parent.document).parent().removeClass("active");
				$("[href*='"+href.substring(urlStart,urlEnd)+"']", window.parent.document).parent().addClass("active");	
			}	
			$.ajax({
				url : '${ctx}/sys/message/readMessage?messageId='+messageId,
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false
			});
			$("[href*="+messageId+"]").parent().remove();
			$("[href*="+messageId+"]", window.parent.document).parent().remove();
			$("#downmsg_title", window.parent.document).html("系统消息("+$("#downmsg_content", window.parent.document).children().length+")<a href='javascript:showHideDiv()'>—</a>");
		}
		function todoBoxRefresh(){
			$.ajax({
				url : '${ctx}/sys/message/boxList?searchDate=' + searchDate,
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {	
					searchDate = res.searchDate;
					for(var i=0;i<res.idList.length;i++){	
						if(res.urlList[i]==null){
							$("#todo").append(
									"<ul><a onclick='readMessage(this.href)' href='#?menuId="
									+ res.menuIdList[i] +"&id="
									+ res.idList[i] +"'><li class='tip-con-first'>"
									+ res.messageTopicList[i] +"</li><li>"+res.createNameList[i]+"<span style='display:inline-block;float:right;'>"+res.createDateList[i]+"</span></li></a></ul>");										
						}else{
							connSymbol=(res.url.indexOf('?')>0) ? '&' : '?';
							$("#todo").append(
									"<ul><a onclick='readMessage(this.href)' target='mainFrame' href='/frame/main"
									+ res.urlList[i] +connSymbol+"menuId="
									+ res.menuIdList[i] +"&id="
									+ res.idList[i] +"'><li class='tip-con-first'>"
									+ res.messageTopicList[i] +"</li><li>"+res.createNameList[i]+"<span style='display:inline-block;float:right;'>"+res.createDateList[i]+"</span></li></a></ul>");										
						}
					}
					if($("#todo").children().length==0){
						$("#todo").append("<span><spring:message code='sys.message.noResult' /></span>")
					}
				}
			});
		}
		/*
		 *高德在线地图
		 */
		function createOnLineMap() {
			$("#onLineMapDiv").show();
			var map = new AMap.Map('onLineMapDiv');
			map.clearMap();  // 清除地图覆盖物
			
			//添加地图工具
			AMap.plugin('AMap.ToolBar',function(){
                var toolbar = new AMap.ToolBar();
                map.addControl(toolbar)
             });
			/* // 添加地图交通状况插件
			AMapUI.loadUI(['control/BasicControl'], function(BasicControl) {

                 var traffic = new BasicControl.Traffic({
                     showButton: true,
                     theme: 'normal'
                 });

                 map.addControl(traffic);
             }); */
			
			// 初始查询线路数据
			$.ajax({
				url : '${ctx}/sys/map/getRouteLineData',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {	
					
					if (res.showType == 'routeType') {
						showRouteInfoMap(map, res.lineData);
					} else if (res.showType == 'allocateType') {
						showAllocateMap(map, res.lineData);
					}
					
				}
			});
			
		}
		/*
		 * 显示在线调拨关系数据
		 */
		function showAllocateMap(map, allRelData) {
			
			if (allRelData.length == 0) {
				return;
			}
			
			var linePassedMarkerArray = new Array();
			var linePointsArray = new Array();
			for (var index = 0; index < allRelData.length; index ++) {
				var allRelItems = allRelData[index];
				var lineArray = new Array();
				for (var jIndex = 0; jIndex < allRelItems.points.length; jIndex ++) {
					var pointItem = allRelItems.points[jIndex];
					// 标记网点
					var pointMarker = new AMap.Marker({
					          map: map,
					          position: pointItem.lnglat,
					          icon: "${ctxStatic}/images/map_office_" + pointItem.pointType + ".png",
					          offset: new AMap.Pixel(-12, -36)
					});
					// 标记当前机构点弹跳效果
					if (pointItem.isCurrentOffice == true) {
						pointMarker.setAnimation('AMAP_ANIMATION_BOUNCE');
					}
					pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
				          content: pointItem.name});
					// 线路坐标
					lineArray.push(pointItem.lnglat);
				 }
				
				if (allRelItems.points.length > 0) {
					// 标记线经过图标
					var linePassedMarker = new AMap.Marker({
					      map: map,
					      zIndex : 100,
					      position: allRelItems.points[0].lnglat,
					      icon: "${ctxStatic}/images/map_arrow.png",
					      offset: new AMap.Pixel(-26, -13),
					      autoRotation: true   //是否按方向选转图片
					});
					
					linePassedMarker.on('moving',function(e){
						 //轨迹经过样式
						 new AMap.Polyline({
							 zIndex:30,
			                 map: map,
			                 path:e.passedPath,
			                 // path: lineArr,
			                 strokeOpacity: 0,     //线透明度
			                // strokeWeight: 3,      //线宽
			                 strokeStyle: "dashed"  //线样式，实线:solid，虚线:dashed
			             });
					 });
					// 保存车辆 覆盖物
				  	linePassedMarkerArray.push({markerIndex : index, marker : linePassedMarker});
				    var speed = 10000;
					// 计算距离
					if (lineArray.length > 1) {
						var startLnglat = new AMap.LngLat(lineArray[0][0], lineArray[0][1]);
						var distance = startLnglat.distance(lineArray[1]);
						speed = (distance/1000)/(1/360);
					}
					
				  	linePointsArray.push({array : lineArray, moveSpeed : speed});
				}
				
				// 绘制关系
				new AMap.Polyline({
				     map: map,
				     path: lineArray,
				     zIndex:10,
				     strokeColor: allRelItems.lineColor,  //线颜色
				     lineJoin : "round", // 折线拐点的绘制样式，默认值为'miter'尖角，其他可选值：'round'圆角、'bevel'斜角
				     lineCap : "round", // 折线两端线帽的绘制样式，默认值为'butt'无头，其他可选值：'round'圆头、'square'方头
				     // strokeOpacity: 1,     //线透明度
				     strokeStyle: "solid",  //线样式，实线:solid，虚线:dashed
				     strokeWeight: 3      //线宽
				     
				 });
				
			}
			
			map.setFitView(); // 根据地图上添加的覆盖物分布情况，自动缩放地图到合适的视野级别
			
			for (var index = 0; index < linePointsArray.length; index ++) {
				var linePointItem = linePointsArray[index];
				for (var jIndex = 0; jIndex < linePassedMarkerArray.length; jIndex ++) {
					var linePassedMarker = linePassedMarkerArray[jIndex];
					
					if (index == linePassedMarker.markerIndex) {
						linePassedMarker.marker.moveAlong(linePointItem.array, linePointItem.moveSpeed);
						continue;
					}
				}
			}
			
			setInterval(function() {
				for (var index = 0; index < linePointsArray.length; index ++) {
					var linePointItem = linePointsArray[index];
					for (var jIndex = 0; jIndex < linePassedMarkerArray.length; jIndex ++) {
						var linePassedMarker = linePassedMarkerArray[jIndex];
						
						if (index == linePassedMarker.markerIndex) {
							linePassedMarker.marker.moveAlong(linePointItem.array, linePointItem.moveSpeed);
							continue;
						}
					}
				}
			}, 5000);
		}
		/*
		 * 显示在线地图线路数据
		 */
		function showRouteInfoMap (map, lineData) {
			if (lineData.length == 0) {
				return;
			}
			
			for (var index = 0; index < lineData.length; index ++) {
				var lineItems = lineData[index];
				var lineArray = new Array();
				for (var jIndex = 0; jIndex < lineItems.points.length; jIndex ++) {
					var pointItem = lineItems.points[jIndex];
					// 标记网点
					var pointMarker = new AMap.Marker({
					          map: map,
					          position: pointItem.lnglat,
					          offset: new AMap.Pixel(-12, -36),
					          icon: "${ctxStatic}/images/map_office_" + pointItem.pointType + ".png"
					      });
					pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
				          content: pointItem.name});
					// 标记当前机构点弹跳效果
					if (pointItem.isCurrentOffice == true) {
						pointMarker.setAnimation('AMAP_ANIMATION_BOUNCE');
					}
				 }
				if (lineItems.points.length > 0) {
					// 标记车辆图标
					var carMarker = new AMap.Marker({
					      map: map,
					      zIndex : 100,
					      position: lineItems.points[0].lnglat,
					      icon: "${ctxStatic}/images/escort_car.png",
					      offset: new AMap.Pixel(-26, -13),
					      autoRotation: false   //是否按方向选转图片
					});
					// 设定车辆标签
					carMarker.setLabel({offset: new AMap.Pixel(40, -20),//修改label相对于marker的位置
					        content: lineItems.carNo + "_" + lineItems.name});
					// 设定车辆行驶实时轨迹
					carMarker.on('moving',function(e){
						 //实时轨迹经过路线样式
						 new AMap.Polyline({
							 zIndex:30,
			                 map: map,
			                 path:e.passedPath,
			                 // path: lineArr,
			                 strokeColor: "#"+lineItems.carTrackColor,  //线颜色
			                 // strokeOpacity: 1,     //线透明度
			                 strokeWeight: 6,      //线宽
			                 strokeStyle: "solid"  //线样式，实线:solid，虚线:dashed
			             });
					 });
					// 保存车辆 覆盖物
				  	carMarkerArray.push({lineName : lineItems.name, carNo : lineItems.carNo, marker : carMarker});
				}
				for(var count=0;count<lineItems.longitudeList.length;count++){
					lineArray.push([lineItems.longitudeList[count],lineItems.latitudeList[count]]);
				}
				// 绘制规定线路轨迹
				new AMap.Polyline({
				     map: map,
				     path: lineArray,
				     zIndex:10,
				     strokeColor: "#"+lineItems.routePlanColor,  //线颜色
				     lineJoin : "round", // 折线拐点的绘制样式，默认值为'miter'尖角，其他可选值：'round'圆角、'bevel'斜角
				     lineCap : "round", // 折线两端线帽的绘制样式，默认值为'butt'无头，其他可选值：'round'圆头、'square'方头
				     // strokeOpacity: 1,     //线透明度
				     strokeStyle: "dashed",  //线样式，实线:solid，虚线:dashed
				     strokeWeight: 6,      //线宽
				     strokeDasharray: [5,2,5] //表示5个像素的实线和2个像素的空白 + 5个像素的实线和5个像素的空白 （如此反复）组成的虚线
				 });
			}
			
			map.setFitView(); // 根据地图上添加的覆盖物分布情况，自动缩放地图到合适的视野级别
		}
		
		/*
		 * 押运车实时轨迹
		 *
		 */
		function realLineMoveTo(carNo, lnglat, speed){
			for (var index = 0; index < carMarkerArray.length; index ++) {
				if (carMarkerArray[index].carNo == carNo) {
					carMarkerArray[index].marker.moveTo(lnglat, speed);
					break;
				}
			}
			
		}
   </script>
   <style>
   .work-con{padding:5px;width:25%;height:300px;position: absolute;left: 3%; top: 2%;border: 1px solid #ddd;border-radius: 6px;overflow-y:auto;OVERFLOW-X: hidden;z-index:500; }
   .work-con-bg{background: rgba(255,253,249,1);background: #fffdf9; }
   .work-con ul{padding:5px 10px;line-height:30px;margin:0;font-size:12px;color:#999;border-bottom:1px solid #ddd;}
   .work-con ul li{list-style: none;}
   .work-con ul li:nth-child(1){color:#000;font-size:14px;}
    .work-con ul li+li{font-size:12px;    color: #999;}
    .work-con ul:hover{background-color:#ffe3e3}
    .tip-con-first{color:#000;font-size:14px;}
    .chart-bg{ filter:alpha(opacity=90); opacity:0.9; background: #fffdf9;background: rgba(255,253,249,0.9);border-radius:10px;  /*  border: 1px solid #e8e1d3; */padding-top:5px}
   </style>
</head>

<body>
	<!-- <ul class="nav nav-tabs">
		<li class=""><a href="#"></a></li>
	</ul> -->
	
	<div class="btn-group" style="height:40px;width:100px;position: absolute;left: 40%; z-index: 500">
	  <button id="showGrapBtn" class="btn" disabled="disabled">显示图表</button>
	  <button id="showMapBtn" class="btn btn-primary">隐藏图表</button>
	</div>
	<div style="height:690px;width:100%;OVERFLOW-X: auto;overflow-y: hidden">
		<div id="mapDiv">
			<div id="offLineMapDiv" style="height:60%;width:60%;position: absolute;left: 15%; top: 20%;"></div>
			<div id="onLineMapDiv" style="height:100%;width:100%;position: absolute;"></div>
		</div>
		<div id="graphDiv">
			<c:if test="${fns:getConfig('sys.message.open')=='true'}">
				<!-- 待办项容器 -->
				<div class="span12 work-con ${fns:getConfig('firstPage.map.isOnline') eq '0' == true ? 'work-con-bg' : ''}" id="todo">
				</div>
			</c:if>
			
			<!-- 平台人员或金库人员登录显示库存 -->
	        <!-- 物品库存统计图容器 -->
	        <c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.mainbarsto'),fns:getUser().office.type)}">
			<div class="span12" id="mainbarsto" style="width:45%;height:300px;position: absolute;left: 1%; bottom: 2%;"></div>
			</c:if>
			<!--箱袋状态统计图容器 -->
			<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.mainBarStats'),fns:getUser().office.type)}">
			<div class="span12" id="mainBarStats" style="width: 40%;height:300px;position: absolute;right: 1px;bottom: 2%;"></div>
			</c:if>
	    	<!-- 网点用户登录显示业务状态图 -->
	    	<!-- 业务状态图 -->
	    	<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.businessStatus'),fns:getUser().office.type)}">
	    	<div id="businessStatus" style="width: 45%;height:300px;position: absolute;left: 1px; bottom: 2%;"></div>
	    	</c:if>
	    	<c:if test="${fns:getUser().office.type == '6' }">
	    		<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.clearAmount'),fns:getUser().office.type)}">
				<!-- 清分业务出入库金额统计图 -->
				<div class="item">
					<div id="clearAmount" style="width:600px;height:300px;position: absolute;left: 1%; bottom: 2%;"></div>
				</div>
				</c:if>
				<!-- 预约清分总金额统计图 -->
				<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.reserveClearAmount'),fns:getUser().office.type)}">
				<div class="item">
					<div id="reserveClearAmount" style="width: 600px;height:300px;position: absolute;right: 1px;bottom: 2%;"></div>
				</div>
				</c:if>
	    	</c:if>
	    	<div id="graphCarousel" class="carousel slide" style="width: 800px;height:300px;position: absolute;right: 1px; top: 2%;" >
	    		<ol class="carousel-indicators">
				<c:if test="${fns:getUser().office.type != '6' }">
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.amount'),fns:getUser().office.type)}">
					<li data-target="#graphCarousel" ></li>
					</c:if>
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.clearAmount'),fns:getUser().office.type)}">
					<li data-target="#graphCarousel" ></li>
					</c:if>
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.reserveClearAmount'),fns:getUser().office.type)}">
					<li data-target="#graphCarousel" ></li>
					</c:if>
				</c:if>
				<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.reserveClearVolume'),fns:getUser().office.type)}">
					<li data-target="#graphCarousel" ></li>
				</c:if>
				</ol>
				<!-- Carousel items -->
				<div class="carousel-inner">
				<c:if test="${fns:getUser().office.type != '6' }">		
					<!-- 调拨业务总金额图 -->
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.amount'),fns:getUser().office.type)}">
					<div class="item">
						<div id="amount" style="width: 600px;height:300px;margin-left: 70px;"></div>
					</div>	
					</c:if>	
					<!-- 清分业务出入库金额统计图 -->
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.clearAmount'),fns:getUser().office.type)}">
					<div class="item">
						<div id="clearAmount" style="width: 600px;height:300px;margin-left: 70px;"></div>
					</div>
					</c:if>
					<!-- 预约清分总金额统计图 -->
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.reserveClearAmount'),fns:getUser().office.type)}">
					<div class="item">
						<div id="reserveClearAmount" style="width: 600px;height:300px;margin-left: 70px;"></div>
					</div>
					</c:if>
				</c:if>
					<!-- 预约清分业务量统计图 -->
					<c:if test="${fn:contains(fns:getDbConfig('mainGraph.show.reserveClearVolume'),fns:getUser().office.type)}">
					<div class="item active">
						<div id="reserveClearVolume" style="width: 600px;height:300px;margin-left: 70px;"></div>
					</div>		
					</c:if>		
				</div>
				<!-- Carousel nav -->
				<a class="carousel-control left" href="#graphCarousel" data-slide="prev">&lsaquo;</a>
				<a class="carousel-control right" href="#graphCarousel" data-slide="next">&rsaquo;</a>
			</div>
    	</div>
    </div>
</body>

</html>