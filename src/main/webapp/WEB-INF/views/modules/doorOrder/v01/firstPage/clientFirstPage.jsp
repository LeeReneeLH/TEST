<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 商户首页 -->
	<title>商户首页</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		//折线图
		var mainLine;
		var doorPanorama;
		var panoramaPie;
		var weekPie;
		var doorIds;
		var doorName ='';
		var mechartId = '';
		var usePercent;
		var eqpConnStatus;
		var flag = false;
		var data;
		var searchEqpId = '';
		var dataEqp = '';
		var lastValue = '';
		var lastSelectedId = '';
		$(document).ready(function() {
			
			// 报表初始化
			doorPanorama = echarts.init(document.getElementById('doorPanorama'),
			'shine');
			panoramaPie = echarts.init(document.getElementById('panoramaPie'),
			'shine');
			mainLine = echarts.init(document.getElementById('mainLine'),
			'shine');
			weekPie = echarts.init(document.getElementById('weekPie'),
			'shine');
			usePercent = echarts.init(document.getElementById('usePercent'),
			'shine');
			/* eqpConnStatus = echarts.init(document.getElementById('eqpConnStatus'),
			'shine'); */
			
			searchPanBarData();
			searchPanPieData();
			searchPanLineData();
			searchWeekPieData();
			setUsePercent();
			/* setEqpConnStatus(); */
			
			
			
			resize();
			doorPanorama.on("click", function (param) { 
				doorName = param.value;
				var selectedId = param.event.target.anid.slice(6);
				mechartId = doorIds[selectedId];
				 if(lastSelectedId == mechartId){
					 doorName =''
					 mechartId = '';
				}
				lastSelectedId = mechartId; 
				searchPanPieData();
			})
			searchEqpId = $("#searchEqpId");
			searchEqpId.bind("change cut input propertychange", searchNode);
			searchEqpId.bind('keydown', function (e){if(e.which == 13){searchNode();}});
		});
		
		function searchPanBarData() {
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/clientBar',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {
					doorIds = res.doorIdList;
					var option = {
							color:['#8186d5','#ff78ae','#32dbc6'],
							backgroundColor: '#fff',
							dataZoom: [
						        {
						            id: 'dataZoomX',
						            type: 'slider',
						            xAxisIndex: [0],
						            filterMode: 'filter'
						        }
						    ],
							title : {
						        text: '今日各门店存款汇款柱形图',
						        x:'left',
						        y:'top',
						        textStyle:{
						            //文字颜色
						            color:'#525252',
						            //字体风格,'normal','italic','oblique'
						            fontStyle:'normal',
						            //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
						            fontWeight:'bold',
						            //字体系列
						            fontFamily:'sans-serif',
						            //字体大小
						            fontsize:18
						        }
						    },
							legend: {
								x:'30%',
						        y:'top',
						        type:'scroll',
								data : ["门店存款总和","已汇款金额","待汇款金额"]
						    },
						    tooltip : {
						        trigger: 'axis'
						    },
						    calculable : true,
						    toolbox : {
								show : true,
								feature : {
									 dataZoom: {
							                yAxisIndex: 'none'
							            },
							            dataView : {
							                show : true,
							                // 数据视图
							                title : '<spring:message code='report.data.view'/>',
							                readOnly: true,
							                lang : ['<spring:message code='report.data.view'/>', '<spring:message code='common.close'/>', '<spring:message code='common.refresh'/>'],
							                optionToContent: function(opt) {
							                    var axisData = opt.xAxis[0].data;
							                    var series = opt.series;
							                    var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
					                                 + '<td><spring:message code='common.time'/></td>';
							                    for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
							                    	table = table + '<td>' + series[seriesIndex].name + '</td>';
							                    }
							                    table = table + '</tr>';
							                    for (var i = 0, l = axisData.length; i < l; i++) {
							                        table += '<tr>' + '<td>' + axisData[i] + '</td>';
					                                for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
									                  	table = table + '<td style="text-align:right;">' + formatCurrencyFloat(series[seriesIndex].data[i])  + '</td>';
									                 }        
					                                table = table + '</tr>';
							                    }
							                    table += '</tbody></table>';
							                    return table;
							                }
							            },
									magicType: {
						                show : true,
						                title : {
						                	// 动态类型切换-折线图
						                    line : '<spring:message code='report.dynamic.swich.foldLine'/>',
						                 	// 动态类型切换-柱状图
						                    bar : '<spring:message code='report.dynamic.swich.column'/>'
						                },
						                type : ['line', 'bar']
						            },
						            restore : {
						                show : true,
						            	// 还原
						                title : '<spring:message code='report.reduction'/>',
						                color : 'black'
						            },
									saveAsImage : {
										show : true
									}
								},
								 x:'75%',
							     y:'top'
							},
						    xAxis : [
						        {
						            type : 'category',
						            data: res.doorNameList,
						            triggerEvent: true
						        }
						    ],
						    yAxis : [
						        {
						        	name : '金额(元)',
						            type : 'value'
						        }
						    ],
						    series : [
						    	 {
						             name:"门店存款总和",
						    		 type:'bar',
						             data: res.doorAmountList,
						             itemStyle: {
						            	 normal: {
						            		 label : {
						            			 show: true, 
						                         position: 'top'
						                     }
						            	 }
						             }
						         },
						         {
						             name:"已汇款金额",
						    		 type:'bar',
						             data:res.remitAmountList,
						             itemStyle: {
						            	 normal: {
						            		 label : {
						            			 show: true, 
						                         position: 'top'
						                     }
						            	 }
						             }
						         },
						         {
						             name:"待汇款金额",
						    		 type:'bar',
					            	 data:res.unremittedAmountList,
						             itemStyle: {
						            	 normal: {
						            		 label : {
						            			 show: true, 
						                         position: 'top'
						                     }
						            	 }
						             }
						         }
						    ]
						}
					doorPanorama.setOption(option);
				}
			});
		}
		
		function searchPanPieData(){
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/clientPie',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				data:'merchantId='+mechartId,
				success : function(res) {
					var option = {
						 /* color:['#4472C5','#ED7C30','#80FF80','#FF8096'], */
						 color:['#f68787','#f8a978','#f1eb9a','#a4f6a5'],
						 backgroundColor: '#fff',
						 title : {
						        text: doorName+'\n\n'+'今日存款业务类型占比',
						        x:'center',
						        textStyle:{
						            //文字颜色
						            color:'#525252',
						            //字体风格,'normal','italic','oblique'
						            fontStyle:'normal',
						            //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
						            fontWeight:'bold',
						            //字体系列
						            fontFamily:'sans-serif',
						            //字体大小
						            fontsize:18
						        }
						    },
						    tooltip : {
						        trigger: 'item',
						        formatter: "{c}(元)({d}%)"
						    },
						    legend: {
						        orient: 'vertical',
						        left: 'left',
						        data: res.typeNameList
						    },
						    series : [
						        {
						            name: '存款类型',
						            type: 'pie',
						            radius : '55%',
						            center: ['50%', '50%'],
						            data: res.typeAmountList,
						            itemStyle: {
						                emphasis: {
						                    shadowBlur: 10,
						                    shadowOffsetX: 0,
						                    shadowColor: 'rgba(0, 0, 0, 0.5)'
						                }
						            }
						        }
						    ]
						}
					panoramaPie.setOption(option);
				}
			})
			panoramaPie.resize();
		}
		
		function searchPanLineData() {
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/clientLine',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {
					var option = {
							backgroundColor: '#fff',
							title: {
						        text: '门店存款走势图',
						        textStyle:{
						            //文字颜色
						            color:'#525252',
						            //字体风格,'normal','italic','oblique'
						            fontStyle:'normal',
						            //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
						            fontWeight:'bold',
						            //字体系列
						            fontFamily:'sans-serif',
						            //字体大小
						            fontsize:18
						        },
						        x:50,
						    },
						    tooltip: {
						        trigger: 'axis'
						    },
						    legend: {
						        data:['存款总和']
						    },
						    calculable : true,
						    toolbox : {
								show : true,
								feature : {
									 dataZoom: {
							                yAxisIndex: 'none'
							            },
							            dataView : {
							                show : true,
							                // 数据视图
							                title : '<spring:message code='report.data.view'/>',
							                readOnly: true,
							                lang : ['<spring:message code='report.data.view'/>', '<spring:message code='common.close'/>', '<spring:message code='common.refresh'/>'],
							                optionToContent: function(opt) {
							                    var axisData = opt.xAxis[0].data;
							                    var series = opt.series;
							                    var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
					                                 + '<td><spring:message code='common.time'/></td>';
							                    for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
							                    	table = table + '<td>' + series[seriesIndex].name + '</td>';
							                    }
							                    table = table + '</tr>';
							                    for (var i = 0, l = axisData.length; i < l; i++) {
							                        table += '<tr>' + '<td>' + axisData[i] + '</td>';
					                                for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
									                  	table = table + '<td style="text-align:right;">' + formatCurrencyFloat(series[seriesIndex].data[i]) + '</td>';
									                 }        
					                                table = table + '</tr>';
							                    }
							                    table += '</tbody></table>';
							                    return table;
							                }
							            },
									magicType: {
						                show : true,
						                title : {
						                	// 动态类型切换-折线图
						                    line : '<spring:message code='report.dynamic.swich.foldLine'/>',
						                 	// 动态类型切换-柱状图
						                    bar : '<spring:message code='report.dynamic.swich.column'/>'
						                },
						                type : ['line', 'bar']
						            },
						            restore : {
						                show : true,
						            	// 还原
						                title : '<spring:message code='report.reduction'/>',
						                color : 'black'
						            },
									saveAsImage : {
										show : true
									}
								},
								 x:'75%',
							     y:'top'
							},
						    xAxis:  {
						    	name: '月份(月)',
						        type: 'category',
						        data: res.lineNameList
						    },
						    yAxis: {
							    name : '金额(元)',
					            type : 'value'
						    },
						    series: [
						        {
						            name:'存款总和',
						            type:'line',
						            data:res.lineAmountList,
						            markPoint: {
						                data: [
						                    {type: 'max', name: '最大值'},
						                    {type: 'min', name: '最小值'}
						                ]
						            },
						            markLine: {
						                data: [
						                    {type: 'average', name: '平均值'}
						                ]
						            }
						        }
						    ]
						}
					mainLine.setOption(option);
				}
			})
		}
		
		function searchWeekPieData(){
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/clientWeekPie',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {
					var option = {
							color:['#f68787','#f8a978','#f1eb9a','#a4f6a5'],
							backgroundColor: '#fff',
							title: {
						        text: '存款周统计',
						        textStyle:{
						            //文字颜色
						            color:'#525252',
						            //字体风格,'normal','italic','oblique'
						            fontStyle:'normal',
						            //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
						            fontWeight:'bold',
						            //字体系列
						            fontFamily:'sans-serif',
						            //字体大小
						            fontsize:18
						        }
						    },
						    tooltip: {
						        trigger: 'axis'
						    },
						    angleAxis: {
						        type: 'category',
						        data: res.pieNameList,
						        z: 10
						    },
						    radiusAxis: {
						    	name : '金额(元)',
					            type : 'value'
						    },
						    polar: {
						    },
						    series: [
						    	{
							        type: 'bar',
							        data: res.depositList,
							        coordinateSystem: 'polar',
							        name: '存款',
							        stack: 'a'
							    }],
						    legend: {
						        show: true,
						        orient: 'vertical',
						        left: 'right',
						        data: ['存款']
						    }
						}
					weekPie.setOption(option);
				}
			})
		}
		
		function resize(){
			setTimeout(function (){
        	    window.onresize = function () {
        	    	doorPanorama.resize();
        	    	panoramaPie.resize();
        	    	mainLine.resize();
        	    	weekPie.resize();
        	    	usePercent.resize();
        	    }
        	},200)
		}
		
		/**
		   * 将数值四舍五入(保留2位小数)后格式化成金额形式
		   *
		   * @param num 数值(Number或者String)
		   * @return 金额格式的字符串,如'1,234,567.45'
		   * @type String
		   */
		  function formatCurrencyFloat(num) {
		      num = num.toString().replace(/\$|\,/g,'');
		      if(isNaN(num))
		      num = "0";
		      sign = (num == (num = Math.abs(num)));
		      num = Math.floor(num*100+0.50000000001);
		      cents = num%100;
		      num = Math.floor(num/100).toString();
		      if(cents<10)
		      cents = "0" + cents;
		      for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
		      num = num.substring(0,num.length-(4*i+3))+','+
		      num.substring(num.length-(4*i+3));
		      return (((sign)?'':'-') + num + '.' + cents);
		  }
		
		  /**
		   * 超链接定位菜单
		   */
		  function readMessage(href){
			  var urlStart = href.indexOf("/frame");
			  var urlEnd = href.indexOf("?");
			  var menuId = href.substr(href.indexOf("menuId=")+7,2);
			  if(urlEnd-urlStart!=11){
				  if(!$("#collapse-"+menuId).hasClass("in")){
					  if(!$("#image-"+menuId).parent().hasClass("active")){
					  	$("#image-"+menuId, window.parent.document).click();
					  }
				  }
				  $("[href*='/frame']", window.parent.document).parent().removeClass("active");
				  $("[href*='"+href.substring(urlStart,urlEnd)+"']", window.parent.document).parent().addClass("active"); 
			  }  
		  }
		  
		  /**
		   * 设置钞袋使用情况柱状图
		   */
		  function setUsePercent() {
			  $.ajax({
					url : '${ctx}/doorOrder/v01/equipmentInfo/getEquipmentStatus',
					type : 'Post',
					contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
					async : true,
					cache : false,
					dataType : 'json',
					success : function(res) {
						if(res.code == 200 && res.isok == true) {
							var percent = []//显示百分比
							var currentCount = []//当前张数
							var percentHundred = []//百分比中的百分百
							var bagCapacity = []//钞袋容量
							var seriesNumber = [] //机具序列号
							var amount = []//当前金额
							var myColor = [];
							var percentData = [];
							dataEqp = res.data;
							var data = dataEqp;
							for(var i = 0; i < data.length; i++) {
								if(data[i].orderInfo != null && data[i].orderInfo.bagCapacity != null) {
									var num = parseFloat(data[i].orderInfo.percent);
									var result = num * 100;
									percent.push(result.toFixed(2));
									currentCount.push(data[i].orderInfo.totalCount);
									percentHundred.push(100);
									bagCapacity.push(data[i].orderInfo.bagCapacity);
									seriesNumber.push(data[i].seriesNumber);
									amount.push(data[i].orderInfo.amount);
									if (result <= 50) {
								       myColor.push('#21BF57');
							        } else if (result > 50 && result <= 90) {
							           myColor.push('#F8B448');
							        } else {
							           myColor.push('#F57474');
							        }
									if (result > 100) {
								       percentData.push(100);
								    } else {
								       percentData.push(result);
								    }   
								}
							}
							  var data = {
							      seriesNumber: seriesNumber,
							      percent: percent,
							      percentHundred: percentHundred,
							      bagCapacity: bagCapacity,
							      currentCount:currentCount,
							      percentData: percentData
							  };
							  var option = {
							      backgroundColor: '#05274C',
							      tooltip: { 
							          trigger: 'item',
							          extraCssText: 'font-size:14px;',
							          axisPointer: {
							              type: 'shadow',
							              label: {
							                  show: true,
							              }
							          },  
							          formatter: function(params) {
							        	  var html ='<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#1089E7"></span>';
							              var html1 = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#F57474"></span>';
							              var html2 = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#F8B448"></span>';
							              return  html + '当前张数'+':'+currentCount[params.dataIndex]+'<br/>'+ html1 + '可容纳张数'+':'+bagCapacity[params.dataIndex]+'<br/>'+ html2 +'当前金额'+':'+amount[params.dataIndex]
							          }
							       },
							      title: {
							          top: '2%',
							          left: 'center',
							          text: '钞袋使用情况统计',
							          textStyle: {
							              align: 'center',
							              fontSize: 18,
							              fontWeight:'bold',
							              fontFamily:'sans-serif',
							          }
							      },
							      grid: {
							          left: '130',
							          right: '110'
							      },
							      xAxis: {
							          show: false,
							      },
							      yAxis: {
							          type: 'category',
							          axisLabel: {
							              margin:30,
							              show: true,
							              fontSize: 14
							          },
							          axisTick: {
							              show: false,
							          },
							          axisLine: {
							              show: false,
							          },
							          data: data.seriesNumber
							      },
							      dataZoom: [
							          {
							              type: 'slider',
							              yAxisIndex: 0,
							              start: 0,
							              end: 2,
							              right:'20',
							              minSpan: 2,
							              maxSpan: 2
							          },
							          {
							              type: 'inside',
							              yAxisIndex: 0,
							              start: 0,
							              end: 2,
							              right:'20',
							              minSpan: 2,
							              maxSpan: 2
							          }
							      ],
							      series: [{
							          type: 'bar',
							          barGap: '-65%',
							          label: {
							              normal: {
							                  show: true,
							                  position: 'right',
							                  color: 'black',
							                  fontSize: 12,
							                  formatter: 
							                  function(param) {
							                      return data.percent[param.dataIndex] + '%';
							                  },
							              }
							          },
							          barWidth: '30%',
							          itemStyle: {
							              normal: {
							                  borderColor: '#4DCEF8',
							                  borderWidth: 2,
							                  barBorderRadius: 15,
							                  color: 'rgba(102, 102, 102,0)'
							              },
							          },
							          z: 1,
							          data: data.percentHundred,
							      }, {
							          type: 'bar',
							          barGap: '-85%',
							          barWidth: '21%',
							          itemStyle: {
							               normal: {
							                  barBorderRadius: 16,
							                  color: function(params) {
							                      return myColor[params.dataIndex]
							                  },
							              }
							          },
							          max: 1,
							          label: {
							              normal: {
							                  show: true,
							                  position: 'insideLeft',
							                  formatter: function(param) {
							                      return data.currentCount[param.dataIndex] + " / " + data.bagCapacity[param.dataIndex];
							                  },
							              }
							          },
							          labelLine: {
							              show: true,
							          },
							          z: 2,
							          data: data.percentData,
							      }]
							  }
							  usePercent.setOption(option);
						}
					}
			  })
			  
		  }
		  
		  /* function setEqpConnStatus() {
			  $.ajax({
					url : '${ctx}/doorOrder/v01/equipmentInfo/getEquipmentStatus?methodName=connStatus',
					type : 'Post',
					contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
					async : true,
					cache : false,
					dataType : 'json',
					success : function(res) {
						if(res.code == 200 && res.isok == true) {
							var mycolor = [];
							var echartData = [];
							var title = '机具连线状态';
							for(let i = 0; i < res.data.length; i++) {
								var eqpStatus
								if(res.data[i].connStatus == "01") {
									mycolor.push('#008000');
									eqpStatus = "在线";
								} else if(res.data[i].connStatus == "03") {
									mycolor.push('#FF8352')
									eqpStatus = "关机";
								} else {
									mycolor.push('#FF0000')
									eqpStatus = "离线";
								}
								var data = {name:"",value:"",eqpStatus:""};
								data.name = res.data[i].name;
								data.value = 1;
								data.eqpStatus = eqpStatus;
								echartData.push(data);
							}
							console.log('echartData',echartData)
						}
					}
			  })
		  } */
		  /**
		   * 搜索框查询
		   */
		  function  searchNode() {
			    var percent = []//显示百分比
				var currentCount = []//当前张数
				var percentHundred = []//百分比中的百分百
				var bagCapacity = []//钞袋容量
				var seriesNumber = [] //机具序列号
				var amount = []//当前金额
				var myColor = [];
			    var percentData = [];
			    seriesNumer = $("#searchEqpId").val().toUpperCase();
			    var data = dataEqp;
			    if (lastValue === seriesNumer) {
		           	 return;
				}
			    //保存最后一次
			    lastValue = seriesNumer; 
				for(var i = 0; i < data.length; i++) {
					if(data[i].seriesNumber.toUpperCase().indexOf(seriesNumer) != -1) {
					if(data[i].orderInfo != null && data[i].orderInfo.bagCapacity != null) {
						var num = parseFloat(data[i].orderInfo.percent);
						var result = num * 100;
						/* console.log('result',result) */
						percent.push(result.toFixed(2));
						currentCount.push(data[i].orderInfo.totalCount);
						percentHundred.push(100);
						bagCapacity.push(data[i].orderInfo.bagCapacity);
						seriesNumber.push(data[i].seriesNumber);
						amount.push(data[i].orderInfo.amount);
						if (result <= 50) {
						   myColor.push('#21BF57');
						} else if (result > 50 && result <= 90) {
						   myColor.push('#F8B448');
						} else {
						   myColor.push('#F57474');
						}
						if (result > 100) {
						   percentData.push(100);
						} else {
						   percentData.push(result);
						}
					}
				 }
				}
				  var data = {
				      seriesNumber: seriesNumber,
				      percent: percent,
				      percentHundred: percentHundred,
				      bagCapacity: bagCapacity,
				      currentCount:currentCount,
				      percentData: percentData
				  };
				  var option = {
				      backgroundColor: '#05274C',
				      tooltip: { 
				          trigger: 'item',
				          extraCssText: 'font-size:14px;',
				          axisPointer: {
				              type: 'shadow',
				              label: {
				                  show: true,
				              }
				          },  
				          formatter: function(params) {
				        	  var html ='<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#1089E7"></span>';
				              var html1 = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#F57474"></span>';
				              var html2 = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#F8B448"></span>';
				              return html + '当前张数'+':'+currentCount[params.dataIndex]+'<br/>'+html1+ '可容纳张数'+':'+bagCapacity[params.dataIndex]+'<br/>'+html2+ '当前金额'+':'+amount[params.dataIndex]
				          }
				       },
				      title: {
				          top: '2%',
				          left: 'center',
				          text: '钞袋使用情况统计',
				          textStyle: {
				              align: 'center',
				              fontSize: 18,
				              fontWeight:'bold',
				              fontFamily:'sans-serif',
				          }
				      },
				      grid: {
				          left: '130',
				          right: '110'
				      },
				      xAxis: {
				          show: false,
				      },
				      yAxis: {
				          type: 'category',
				          axisLabel: {
				              margin:30,
				              show: true,
				              fontSize: 14
				          },
				          axisTick: {
				              show: false,
				          },
				          axisLine: {
				              show: false,
				          },
				          data: data.seriesNumber
				      },
				      dataZoom: [
				          {
				              type: 'slider',
				              yAxisIndex: 0,
				              start: 0,
				              end: 2,
				              right:'20',
				              minSpan: 2,
				              maxSpan: 2
				          },
				          {
				              type: 'inside',
				              yAxisIndex: 0,
				              start: 0,
				              end: 2,
				              right:'20',
				              minSpan: 2,
				              maxSpan: 2
				          }
				      ],
				      series: [{
				          type: 'bar',
				          barGap: '-65%',
				          label: {
				              normal: {
				                  show: true,
				                  position: 'right',
				                  color: 'black',
				                  fontSize: 12,
				                  formatter: 
				                  function(param) {
				                      return data.percent[param.dataIndex] + '%';
				                  },
				              }
				          },
				          barWidth: '30%',
				          itemStyle: {
				              normal: {
				                  borderColor: '#4DCEF8',
				                  borderWidth: 2,
				                  barBorderRadius: 15,
				                  color: 'rgba(102, 102, 102,0)'
				              },
				          },
				          z: 1,
				          data: data.percentHundred,
				      }, {
				          type: 'bar',
				          barGap: '-85%',
				          barWidth: '21%',
				          itemStyle: {
				               normal: {
				                  barBorderRadius: 16,
				                  color: function(params) {
				                      return myColor[params.dataIndex]
				                  },
				              }
				          },
				          max: 1,
				          label: {
				              normal: {
				                  show: true,
				                  position: 'insideLeft',
				                  formatter: function(param) {
				                      return data.currentCount[param.dataIndex] + " / " + data.bagCapacity[param.dataIndex];
				                  },
				              }
				          },
				          labelLine: {
				              show: true,
				          },
				          z: 2,
				          data: data.percentData,
				      }]
				  }
				  usePercent.setOption(option);
				  
		}
	</script>
	<style>
		.whole_page {margin: 0;padding: 0;}
		.all_tabs{height: 150px; padding: 10px 20px; color: #666;width:100%;}
		.chart_tab{border: 1px solid #e1ebf7;border-radius: 10px;margin: 5px 1%;padding: 5px 18px 25px 18px;display: inline-block;position: relative;float: left;background: #fff;height: 120px;}
		.h3_self{text-align: center;}
		ul li{list-style: none;line-height:1.5;padding: 3px;}
		.client_panorama{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 59%;height:400px;float:left;overflow-x:auto;padding: 20px;margin: 10px 10px 10px 20px;}
		.main_line_s{background:rgb(255, 251, 244) !important;border-radius: 10px;width: 58%;height:400px;float:left;background: #fff;padding: 15px 0px;margin: 15px 0 10px 20px;}
		.week_pie{background:rgb(236, 255, 235) !important;border-radius: 10px;width: 34%;height:400px;float:left;padding: 15px;margin: 15px 20px 15px 20px;}
		.deposit_error_s{background: rgb(255, 244, 244) !important;border-radius: 10px;width: 92%;height: 258px; float: left;background: #fff;padding: 0px 20px 20px 20px;margin: 5px 0 0 20px;}
		.title_self{color: #333;font-size: 18px;line-height: 40px;font-weight: bold;}
		.accordion-heading, .table th{color: #4e108a}
		.use_percent{background:#fdeef3 !important;border-radius: 10px;width: 46%;height:400px;float:left;padding: 15px;margin: 15px 0px 15px 20px;}
		.module_status{background:rgb(244, 249, 255) !important;border-radius: 10px;width: 44%;height:400px;float:left;background: #fff;padding: 15px;margin: 15px 0 15px 20px;}
	 /* .conn_status{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 40%;height:400px;float:left;padding: 15px;margin: 15px 20px 15px 33px;} */
	 
		.count_self{color: #ea4c4c;padding: 0 0 0 10px;font-weight: bold;}
		#panoramaPie{background: #FCF4FF !important;border-radius: 10px;width: 30%;height:400px;float:left;padding: 20px;margin: 10px 10px;}
		#eq_tab{width:21%;}
		#ex_tab{width:21%;}
		#de_tab{width:42%;}
		@media only screen and (min-width: 1680px){
		}
		@media only screen and (min-width: 1540px) and (max-width: 1680px){ /* 110% */
			#panoramaPie{width: 28%;}
		}
		@media only screen and (min-width: 1200px) and (max-width: 1540px){ /* 125% */
			/* .chart_tab {width: 43%;} */
			.deposit_error_s{width: 92%;}
			/* .client_panorama{width: 92%;} */
			#panoramaPie{width: 28%;}
			.main_line_s{width: 58%;}
			.week_pie{width: 32.5%;}
			.use_percent{width:44%;}
			.module_status{width: 44.5%;;}
			/* #eq_tab{width:20%;}
			#ex_tab{width:20%;}
			#de_tab{width:41%;} */
		}
		@media only screen and (min-width: 1064px) and (max-width: 1200px){ /* 150% */
			/* .chart_tab {width: 42%;} */
			.week_pie,.deposit_error_s,#panoramaPie,.use_percent,.module_status{width: 92%;}
			.client_panorama{width: 91%;}
			.main_line_s{width: 96%;}
			/* #eq_tab{width:20.5%;}
			#ex_tab{width:20%;}
			#de_tab{width:41%;} */
		}
	    @media only screen and (min-width: 400px) and (max-width: 1064px){ /* 175% */
	    	/* .chart_tab {width: 39%;} */
		    .week_pie,.deposit_error_s,.use_percent,.module_status{width: 92%;}
		    #panoramaPie{left: 10px;width: 91%;}
		    .client_panorama{width: 91%;}
		    .main_line_s{width: 95%;}
		    #eq_tab{width:42.5%;}
			#ex_tab{width:42.5%;}
			#de_tab{width:92%;}
		}
		@media only screen and (max-width: 400px){
			.chart_tab,.client_panorama,.week_pie,.deposit_error_s,.main_line_s,#panoramaPie,.use_percent,.module_status{width: 90%;}
			/* #eq_tab{width:93%;}
			#ex_tab{width:93%;}
			#de_tab{width:93%;} */
		}
		#contentTable thead, .table_tbody_self tr {
			display:table;
			width:100%;
			table-layout:fixed;
		}
		#contentTable tbody {
		    display:block;
		    height: 180px;
		    overflow-y:auto;
		    overflow-x:hidden;
		    border-top: 2px solid #ddd;
		}
		#contentTable th,td {
		    border:0px;
		}
		#contentTable thead {
		    width: calc( 100% - 1.2em )
		}
		.center-dev{display:flex;}
		.center-dev li{flex:1;text-align:center;font-size:12px;color:#666;margin-top:10px;}
		/* .center-dev li:nth-child(3){flex:1.5} */
		#li3{flex:1.5}
		.center-dev li>span{display:block;font-size:23px;line-height:50px;color:black}
		.th-back-color{background: rgb(255, 244, 244) !important;}
		.th-status-color{background: rgb(244, 249, 255) !important;}
		 table, td, th{    border: 1px transparent solid;}
		.row-left{width:100%;padding-left:20px;}
		ul{margin: 0px;}
	</style>
</head>
<body>
<div class="whole_page">
	<div class="all_tabs">
		<div class="chart_tab" id="eq_tab">
			<h4 class="h3_self">机具连线状态监控</h4>
			<ul class="center-dev">
				<li><span>${officeAmount.count} </span>机具总数(台) </li>
				<li><span class="text-red">${officeAmount.exCount}  </span>异常(台)</li>
				<li id="li3"><span class="text-yellow">${officeAmount.elCount}  </span>正常/停用/关机/故障锁定(台)</li>
			
			</ul>
			<%-- <ul>
				<li>机具总数：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=0&clientFlag=1">${officeAmount.count}</a>  台</li>
				<li>异常：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=1&clientFlag=1">${officeAmount.exCount}</a>  台</li>
				<li>正常/停用/关机/故障锁定：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=0&clientFlag=1">${officeAmount.elCount}</a>  台</li>
				<li>机具总数：${officeAmount.count}  台</li>
				<li>异常：${officeAmount.exCount}  台</li>
				<li>正常/停用/关机/故障锁定：${officeAmount.elCount}  台</li>
			</ul> --%>
		</div>
		<div class="chart_tab" id="ex_tab">
			<h4 class="h3_self">今日异常数量</h4>
			<ul class="center-dev">
				<li><span class="text-red">${officeAmount.exceptionCount}  </span>异常(未处理)</li>
				<li><span class="text-green">${officeAmount.processedCount} </span>异常(已处理) </li>
			</ul>
		</div>
		<div class="chart_tab" id="de_tab">
			<h4 class="h3_self">今日存款汇总</h4>
			<ul class="center-dev">
				<li><span>￥<fmt:formatNumber value="${officeAmount.depositAmount}" pattern="#,##0.00#" /></span> 存款总额(元)</li>
				<li><span class="text-red">￥<fmt:formatNumber value="${officeAmount.repayAmount}" pattern="#,##0.00#" /> </span>代付金额(元)</li>
				<li><span class="text-green">￥<fmt:formatNumber value="${officeAmount.backAmount}" pattern="#,##0.00#" /> </span>回款金额(元)</li>
			</ul>
			<%-- <ul>
				<li>存款总额：￥<fmt:formatNumber value="${officeAmount.depositAmount}" pattern="#,##0.00#" /> 元</li>
				<li>代付金额：￥<fmt:formatNumber value="${officeAmount.repayAmount}" pattern="#,##0.00#" /> 元</li>
				<li>回款金额：￥<fmt:formatNumber value="${officeAmount.backAmount}" pattern="#,##0.00#" /> 元</li>
			</ul> --%>
		</div>
		<c:if test="${depositErrorList.size() > 0}">
		 <div class="deposit_error_s">
           	<div style="text-align: center;"><span class="title_self">今日差错统计</span><span class="count_self">(总计：${officeAmount.wholeCount} 笔)</span></div>
            <table id="contentTable" class="table table-hover" style="overflow-x:auto;">
				<thead>
					<tr>
						<!-- 序号 -->
						<th style="width: 10%" class="th-back-color"><spring:message code='common.seqNo'/></th>
						<!-- 门店名称 -->
						<th style="width: 30%" class="th-back-color"><spring:message code="door.public.custName" /></th>
						<!-- 差错类型 -->
						<th class="th-back-color"><spring:message code="door.errorInfo.type"/></th>
						<!-- 业务状态 -->
						<th class="th-back-color"><spring:message code="door.errorInfo.businessType"/></th>
						<!-- 处理状态 -->
						<th class="th-back-color"><spring:message code="door.errorInfo.deal"/><spring:message code="common.status"/></th>
						<!-- 笔数 -->
						<th class="th-back-color"><spring:message code="door.public.count"/></th>
						<!-- 差错金额 -->
						<th class="th-back-color"><spring:message code="door.errorInfo.amount"/>(元)</th>
					</tr>
				</thead>
				<tbody class="table_tbody_self">
					<c:forEach items="${depositErrorList}" var="doorCenterAccountsMain" varStatus="status">
						<tr>
							<td style="width: 10%">
								${status.index+1}
							</td>
							<td style="width: 30%">
								${doorCenterAccountsMain.clientName}
							</td>
							<td>
								<c:if test="${doorCenterAccountsMain.errorType eq 2}">
									<spring:message code="door.errorInfo.long"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.errorType eq 3}">
									<spring:message code="door.errorInfo.short"/>
								</c:if>
							</td>
							<td>
								<%-- <c:if test="${doorCenterAccountsMain.businessStatus eq 1}">
									<spring:message code="door.errorInfo.register"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.businessStatus eq 2}">
									<spring:message code="door.errorInfo.cancel"/>
								</c:if>--%>
								<!--   update-start   动态配置业务类型冲正状态的颜色     HaoShijie    2020.05.26 -->
									${fns:getDictLabelWithCss(doorCenterAccountsMain.businessStatus,'cl_status_type','未命名',true)}
								<!--   update-end     动态配置业务类型冲正状态的颜色     HaoShijie    2020.05.26 -->
							</td>
							<td>
								<c:if test="${doorCenterAccountsMain.paidStatus eq 0}">
									<spring:message code="door.accountManage.finished"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.paidStatus eq 1}">
									<spring:message code="door.accountManage.unfinished"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.paidStatus eq 2}">
									<spring:message code="door.accountManage.settled"/>
								</c:if>
							</td>
							<td>${doorCenterAccountsMain.errorCount}</td>
							<td><fmt:formatNumber value="${doorCenterAccountsMain.totalAmount}" pattern="#,##0.00#" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
         </div>
		</c:if>
	</div>
	<div>
	    <div class="row row-left" >
	        <!-- 商户全景柱形图 -->
        	<div id="doorPanorama" class="client_panorama"></div>
          	<!-- 存款类型占比扇形图 -->
            <div class="span12" id="panoramaPie"></div>
	    </div>
		<!-- 折线图 -->
	    <div class="row row-left" >
            <div id="mainLine" class="main_line_s"></div>
            <div id="weekPie" class="week_pie"></div>
	    </div>
	    <!-- 钞袋使用状态百分比柱状图 -->
	    <div class="row row-left" style="position: relative;">
	        <div style="position: absolute;z-index:100;left:70px;top:40px;">
		    <input id="searchEqpId" type="text" style="text-align:left;border:1px solid #b6d4ef;width:65%;background: #fdeef3;font-size: 12px;" placeholder="请输入机具编号" />
		    </div>
            <div id="usePercent" class="use_percent"></div>

            <!-- 机具模块状态-->
            <div class="module_status">
          	   	<div style="text-align: center;"><span class="title_self">预警提示</span></div>
	           	<table id="contentTable" class="table table-hover" style="overflow-x:auto;">
					<thead>
						<tr>
							<!-- 序号 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code='common.seqNo'/></th>
							<!-- 机具编号 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code="door.equipment.number"/></th>
							<!-- 清分机 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code="door.equipment.module.clearStatus"/></th>
							<!-- 打印机 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code="door.equipment.module.printerStatus"/></th>
							<!-- 保险柜仓门 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code="door.equipment.module.doorStatus"/></th>
							<!-- 在线状态 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code='door.equipment.updateDate'/></th>
						</tr>
					</thead>
					<tbody class="table_tbody_self">
						<c:forEach items="${warningList}" var="warning" varStatus="status">
							<tr>
								<td style="width: 16%;text-align:center;">
									${status.index+1}
								</td>
								<td style="width: 16%;text-align:center;">
									${warning.seriesNumber}
								</td>
								<td style="width: 16%;text-align:center;">
									<!-- 清分机状态：1正常，2故障，3掉线 -->
									${fns:getDictLabelWithCss(warning.clearStatus, "equ_clear_status", "未命名", true)}
								</td>
								<td style="width: 16%;text-align:center;">
									<!-- 凭条打印机状态：1正常，2故障，3掉线，4缺纸 -->
									${fns:getDictLabelWithCss(warning.printerStatus, "equ_printer_status", "未命名", true)}
								</td>
								<td style="width: 16%;text-align:center;">
									<!-- 保险柜仓门状态：1正常，2故障，3掉线，4打开 -->
									${fns:getDictLabelWithCss(warning.doorStatus, "equ_door_status", "未命名", true)}
								</td>
								<td style="width: 16%;text-align:center;">
									<fmt:formatDate value="${warning.createDate}" pattern="HH:mm:ss"/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
        	</div>
    	</div>
</div>
</body>
</html>
