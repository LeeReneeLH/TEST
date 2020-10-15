<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 中心首页 -->
	<title>中心首页</title>
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
		var clientPanorama;
		var panoramaPie;
		var weekPie;
		var clientIds;
		var clientName;
		var selectedId;
		var flag = false;
		var searchEqpId = '';
		var dataEqp = '';
		var lastValue = '';
		var lastSelectedId = '';
		$(document).ready(function() {
			// 报表初始化
			clientPanorama = echarts.init(document.getElementById('clientPanorama'),
			'shine');
			mainLine = echarts.init(document.getElementById('mainLine'),
			'shine');
			weekPie = echarts.init(document.getElementById('weekPie'),
			'shine');
			usePercent = echarts.init(document.getElementById('usePercent'),
			'shine');
			
			
			searchPanBarData();
			searchPanLineData();
			searchWeekPieData();
			setUsePercent();
			resize();
			clientPanorama.on("click", function (param) { 
				//flag =! flag
				//if (flag) {
					clientName = param.value;
					selectedId = param.event.target.anid.slice(6);
					panoramaPie = echarts.init(document.getElementById('panoramaPie'),
					'shine');
					if(lastSelectedId == selectedId){
						changeCss1();
						selectedId = '';
					}else{
						searchPanPieData();						
						changeCss();
					}
					lastSelectedId = selectedId;
				//}else{
					//changeCss1();
					//flag = false;
				//}
				
			})
			searchEqpId = $("#searchEqpId");
			searchEqpId.bind("change cut input propertychange", searchNode);
			searchEqpId.bind('keydown', function (e){if(e.which == 13){searchNode();}});
		});
		function searchPanBarData() {
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/centerBar',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {
					clientIds = res.clientIdList;
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
						        text: '今日各商户存款汇款柱形图',
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
								data : ["商户存款总和","已汇款金额","待汇款金额"]
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
						            /* ,
									myTool1: {
						                show: true,
						                title: '自定义扩展方法1',
						                icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
						                onclick: function (param){
						                    alert(JSON.stringify(param))
						                }
						            } */
								},
								 x:'75%',
							     y:'top'
							},
						    xAxis : [
						        {
						            type : 'category', 
						            data: res.clientNameList,
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
						             name:"商户存款总和",
						    		 type:'bar',
						             data: res.clientAmountList,
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
					clientPanorama.setOption(option);
				}
			});
		}
		
		function searchPanPieData(){
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/centerPie',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				data:'merchantId='+clientIds[selectedId],
				success : function(res) {
					var option = {
						 /* color:['#4472C5','#ED7C30','#80FF80','#FF8096'], */
						 color:['#f68787','#f8a978','#f1eb9a','#a4f6a5'],
						 backgroundColor: '#FCF4FF',
						 title : {
						        text: clientName+'\n\n'+'今日存款业务类型占比',
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
		}
		
		function searchPanLineData() {
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/centerLine',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				success : function(res) {
					var option = {
							backgroundColor: '#fff',
							title: {
						        text: '商户存款走势图',
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
				url : '${ctx}/doorOrder/v01/fristPage/centerWeekPie',
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
        	    	clientPanorama.resize();
        	    	mainLine.resize();
        	    	weekPie.resize();
        	    	usePercent.resize();
        	    }
        	},200)
		}
		
         function changeCss(){
             var div1=document.getElementById("clientPanorama");
             if (document.body.clientWidth > 1200) {
            	 div1.style.cssText="width: 59%";
			}
             //cssText会覆盖之前的设置  无兼容性问题  写法和css样式表相同
             var div2=document.getElementById("panoramaPie");
             div2.style.cssText="border-radius: 10px;height:400px;float:left;padding: 20px 0px;margin: 10px 30px 20px;display: inline-block;";
             clientPanorama.resize();
    		 panoramaPie.resize();
         }
         
         function changeCss1(){
             var div1=document.getElementById("clientPanorama");
             //	清除宽度样式
             div1.style.width=null;
             //div1.style.cssText="width:93.5%";
             //cssText会覆盖之前的设置  无兼容性问题  写法和css样式表相同
             var div2=document.getElementById("panoramaPie");
             div2.style.cssText="display: none;";
             clientPanorama.resize();
    		 panoramaPie.resize();
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
			  var urlStart = href.indexOf("${ctx}");
			  var urlEnd = href.indexOf("?");
			  var menuId = href.substr(href.indexOf("menuId=")+7,2);
			  if(urlEnd-urlStart!=11){
				  if(!$("#collapse-"+menuId).hasClass("in")){
					  if(!$("#image-"+menuId).parent().hasClass("active")){
					  	$("#image-"+menuId, window.parent.document).click();
					  }
				  }
				  $("[href*='${ctx}']", window.parent.document).parent().removeClass("active");
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
							var data = res.data;
							for(var i = 0; i < data.length; i++) {
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
							  var data = {
							      seriesNumber: seriesNumber,
							      percent: percent,
							      percentHundred: percentHundred,
							      bagCapacity: bagCapacity,
							      currentCount:currentCount,
							      percentData:percentData
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
							                      return myColor[params.dataIndex];
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
				      percentData:percentData
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
      .whole_page { background-color: #fff;margin: 0;padding:0;}
     .all_tabs{height: 150px; padding: 10px 20px; color: #666;width:100%;/* overflow-x:auto; */}
     .chart_tab{border: 1px solid #e1ebf7;border-radius: 10px;margin: 5px 1%;padding: 5px 18px 25px 18px;display: inline-block;position: relative;width: 30%;float: left;background: #fff;height: 120px;}
	 .h3_self{text-align: center;}
	 ul li{list-style: none;line-height:1.5;padding: 3px;}
	 .client_panorama{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 94%;height:400px;float:left;padding: 20px 0px;margin: 10px 10px 10px 30px;}
	 #panoramaPie{display: none;}
	 .main_line_s{background:rgb(255, 251, 244) !important;border-radius: 10px;width: 59%;height:400px;float:left;background: #fff;padding: 15px 0px;margin: 15px 0 10px 33px;}
	 .week_pie{background: #FCF4FF !important;border-radius: 10px;width: 33%;height:400px;float:left;padding: 15px;margin: 15px 20px 15px 33px;}
	 .deposit_error_s{width: 92%;height: 400px; float: left;background: #fff;padding: 20px;margin: 10px 0 0 20px;}
	 .client_list{background:rgb(255, 244, 244) !important;border-radius: 10px;width: 92%;margin: 5px 21px;padding: 5px 18px 25px 18px;display: inline-block;position: relative;float: left;background: #fff;height: 258px;}
	 .count_self{color: #ea4c4c;padding: 0 0 0 10px;font-weight: bold;}
	 .use_percent{background: #F0F8FF !important;border-radius: 10px;width: 45%;height:400px;float:left;padding: 15px 0px;margin: 15px 10px 15px 30px;/* box-sizing:border-box */}
	 .module_status{background:rgb(236, 255, 235) !important;border-radius: 10px;width: 45%;height:400px;float:left;background: #fff;padding: 15px;margin: 15px 0 10px 30px;/* box-sizing: border-box; */}
	 .title_self{color: #333;font-size: 18px;line-height: 40px;font-weight: bold;}
	 table, td, th{    border: 1px transparent solid;}
	 
	 ul, ol {padding: 0;margin: 0 0 10px 0;}
	 #u1{padding: 0;margin: 0 0 10px 25px;}
	 #eq_tab{width:21%;}
	 #ex_tab{width:21%;}
	 #de_tab{width:42%;}
	 /* .conn_status{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 40%;height:400px;float:left;padding: 15px;margin: 15px 20px 15px 33px;} */
	  @media only screen and (min-width: 1680px){
	        /* .chart_tab {width: 44%;} */
		    .client_list{width: 92%;}
		    #panoramaPie{width: 32%;}
		    
		   /*  #eq_tab{width:20.5%;}
			#ex_tab{width:20%;}
			#de_tab{width:43%;} */
	   }
	  @media only screen and (min-width: 1540px) and (max-width: 1680px){
	       /*  .chart_tab {width: 44%;} */
		    .client_list{width: 92%;}
		    #panoramaPie{width: 32%;}
		    
			/* #eq_tab{width:20.5%;}
			#ex_tab{width:20%;}
			#de_tab{width:42%;} */
	   }
	   @media only screen and (min-width: 1200px) and (max-width: 1540px){
	       /*  .chart_tab {width: 27%;} */
		    .client_list,.deposit_error_s{width: 92%;}
		    /* .client_panorama{width: 92%;} */
		    #panoramaPie{width: 31%;}
		    .main_line_s{width: 58%;}
		    .week_pie{width: 33%;left: 6px;}
		    .use_percent{width: 46%;}
			.module_status{width: 43%};
			.client_panorama{width: 93%;}
			
	   }
	    @media only screen and (min-width: 1064px) and (max-width: 1200px){
	        .chart_tab {width: 42%;}
		    .client_list,.week_pie,.deposit_error_s,.use_percent,#panoramaPie,.module_status{width: 92%;}
		    .client_panorama{width: 92.5%;}
		    .main_line_s{width: 96%;}
		    
	   }
	    @media only screen and (min-width: 400px) and (max-width: 1064px){
	    	.chart_tab {width: 39%;}
		    .client_list,.week_pie,.deposit_error_s,.use_percent,.client_panorama{width: 92%;}
		    .main_line_s{width: 95.3%;}
		    #panoramaPie{width:91%;}
		    .module_status{width:89%;}
		    
		    #eq_tab{width:42.5%;}
			#ex_tab{width:42.5%;}
			#de_tab{width:92%;}
	   }
	   @media only screen and (max-width: 400px){
		    .chart_tab,.client_list,.client_panorama,.week_pie,.deposit_error_s,.main_line_s,#panoramaPie,.use_percent,.module_status {width: 90%;}
	   }
	   #contentTable{}
	   #contentTable thead, .table_tbody_self tr {
		    display:table;
		    width:100%;
		    table-layout:fixed;
		}
	   #contentTable th,td {
		    border:0px;
		}
	   #contentTable tbody {
		    display:block;
		    height: 180px;
		    overflow-y:auto;
		    overflow-x:hidden;
		    border-top: 2px solid #ddd;
		}
		.accordion-heading, .table th{color: #4e108a}
	   #contentTable thead {
		    width: calc( 100% - 1.2em )
		}
		.center-dev{display:flex;}
		.center-dev li{flex:1;text-align:center;font-size:12px;color:#666;margin-top:10px;}
		/* .center-dev li:nth-child(3){flex:1.5} */
		 #li3{flex:1.5} 
		.center-dev li>span{display:block;font-size:23px;line-height:50px;color:black}
		.th-back-color{background: rgb(255, 244, 244) !important;}
		.th-status-color{background:rgb(236, 255, 235) !important;}
		
	</style>
</head>
<body>
<div class="whole_page">
	<div class="all_tabs">
		<div class="chart_tab" id="eq_tab">
			<h4 class="h3_self">机具连线状态监控</h4>
			<ul class="center-dev" id="u1">
				<li><span>${officeAmount.count} </span>机具总数(台) </li>
				<li><span class="text-red">${officeAmount.exCount}  </span>异常(台)</li>
				<li id="li3"><span class="text-yellow">${officeAmount.elCount}  </span>正常/停用/关机/故障锁定(台)</li>
				
			</ul>
			<%-- <ul>
				<li>机具总数：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=0">${officeAmount.count}</a>  台</li>
				<li>异常：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=1">${officeAmount.exCount}</a>  台</li>
				<li>正常/停用/关机/故障锁定：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=0">${officeAmount.elCount}</a>  台</li>
				<li>机具总数：${officeAmount.count}  台</li>
				<li>异常：${officeAmount.exCount}  台</li>
				<li>正常/停用/关机/故障锁定：${officeAmount.elCount}  台</li>
			</ul> --%>
		</div>
		<div class="chart_tab" id="ex_tab">
			<h4 class="h3_self">今日异常数量</h4>
			<ul class="center-dev">
				<li><span><a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/doorOrderException?status=1&pageSkipFlag=1&menuId=38" class="text-red">${officeAmount.exceptionCount}</a></span> 异常(未处理)</li>
				<li><span><a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/doorOrderException?status=2&pageSkipFlag=1&menuId=38"" class="text-green">${officeAmount.processedCount}</a></span>异常(已处理)</li>
				<!-- <li><span class="text-green">3</span>回款金额(元)</li> -->
			</ul>
			<%-- <ul>
				<li>存款总额：￥<fmt:formatNumber value="${officeAmount.depositAmount}" pattern="#,##0.00#" /> 元</li>
				<li>代付金额：￥<fmt:formatNumber value="${officeAmount.repayAmount}" pattern="#,##0.00#" /> 元</li>
				<li>回款金额：￥<fmt:formatNumber value="${officeAmount.backAmount}" pattern="#,##0.00#" /> 元</li>
			</ul> --%>
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
			<div class="client_list">
            	<div style="text-align: center;"><span class="title_self">今日差错统计</span><span class="count_self">(总计：${officeAmount.wholeCount} 笔)</span></div>
	            <table id="contentTable" class="table table-hover">
					<thead>
						<tr>
							<!-- 序号 -->
							<th style="width:10%;" class="th-back-color"><spring:message code='common.seqNo'/></th>
							<!-- 客户名称 -->
							<th style="width:30%;" class="th-back-color"><spring:message code="clear.public.custName" /></th>
							<!-- 差错类型 -->
							<th class="th-back-color"><spring:message code="door.errorInfo.type"/></th>
							<!-- 业务类型 -->
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
							<tr style="height: 37px;">
								<td style="width:10%;">
									${status.index+1}
								</td>
								<td style="width:30%;">
									${doorCenterAccountsMain.merchantOfficeName}
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
									<!--   update-start   动态配置业务类型状态的颜色     HaoShijie    2020.05.26 -->
									${fns:getDictLabelWithCss(doorCenterAccountsMain.businessStatus,'cl_status_type','未命名',true)}
									<!--   update-end     动态配置业务类型状态的颜色     HaoShijie    2020.05.26 -->
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
		<%-- <div class="chart_tab">
			<h3 class="h3_self">今日存款金额前三排名</h3>
			<ul>
			<c:forEach  items="${officeAmount.rankingList}" var="off" begin="0" end="2">
				<li>${off.name}：￥<fmt:formatNumber value="${off.depositAmount}" pattern="#,##0.00#" /> 元</li>
			</c:forEach>
			</ul>
		</div>
		<div class="chart_tab">
			<h3 class="h3_self">季度存款</h3>
			<ul>
				<li>第一季度：￥<fmt:formatNumber value="${officeAmount.one}" pattern="#,##0.00#" /> 元</li>
				<li>第二季度：￥<fmt:formatNumber value="${officeAmount.two}" pattern="#,##0.00#" /> 元</li>
				<li>第三季度：￥<fmt:formatNumber value="${officeAmount.three}" pattern="#,##0.00#" /> 元</li>
				<li>第四季度：￥<fmt:formatNumber value="${officeAmount.four}" pattern="#,##0.00#" /> 元</li>
			</ul>
		</div>
		<div class="chart_tab">
			<h3 class="h3_self">存款汇总</h3>
			<ul>
				<li>存款金额(年)：￥<fmt:formatNumber value="${officeAmount.yearDepositAmount}" pattern="#,##0.00#" /> 元</li>
				<li>代付金额(年)：￥<fmt:formatNumber value="${officeAmount.yearRepayAmount}" pattern="#,##0.00#" /> 元</li>
				<li>回款金额：￥<fmt:formatNumber value="${officeAmount.backAmountAll}" pattern="#,##0.00#" /> 元</li>
				<li>未回款金额：￥<fmt:formatNumber value="${officeAmount.unbackAmountAll}" pattern="#,##0.00#" /> 元</li>
			</ul>
		</div> --%>
	</div>
	<div style="height: 750px;width: 100%;padding: 0px 34px;">
	    <div class="row" style="margin-left: -35px;">
	        <!-- 商户全景柱形图 -->
        	<div id="clientPanorama" class="client_panorama"></div>
          	<!-- 存款类型占比扇形图 -->
            <div class="span12" id="panoramaPie"></div>
	    </div>
		<!-- 折线图 -->
	    <div class="row" style="width: 100%;margin-left: -35px;">
            <div id="mainLine" class="main_line_s"></div>
            <div id="weekPie" class="week_pie"></div>
	    </div>
	    <!-- 钞袋使用状态百分比柱状图 -->
	    <div class="row" style="margin-left: -35px;position:relative;">
		    <div style="position: absolute;z-index:100;left:50px;top:40px;">
		    <input id="searchEqpId" type="text"  style="text-align:left;border:1px solid #b6d4ef;width:65%;background: #f0f8ff;font-size: 12px;" placeholder="请输入机具编号" />
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
							<!-- 更新时间 -->
							<th style="width: 16%;text-align:center;" class="th-status-color"><spring:message code='door.equipment.updateDate'/></th>
						</tr>
					</thead>
					<tbody class="table_tbody_self">
						<c:forEach items="${warningList}" var="warning" varStatus="status">
							<tr>
								<td style="width: 16%;text-align:center;">
									${status.index + 1}
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
</div>
</body>
</html>
