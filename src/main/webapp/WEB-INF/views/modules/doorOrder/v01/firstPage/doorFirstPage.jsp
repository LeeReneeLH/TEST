<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 商户首页 -->
	<title>门店首页</title>
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
		var panoramaPie;
		var weekPie;
		var searchEqpId = '';
		var dataEqp = '';
		var lastValue = '';
		$(document).ready(function() {
			// 报表初始化
			panoramaPie = echarts.init(document.getElementById('panoramaPie'),
			'shine');
			mainLine = echarts.init(document.getElementById('mainLine'),
			'shine');
			weekPie = echarts.init(document.getElementById('weekPie'),
			'shine');
			usePercent = echarts.init(document.getElementById('usePercent'),
			'shine');
			
			searchPanPieData();
			searchPanLineData();
			searchWeekPieData();
			setUsePercent();
			clear();
			searchEqpId = $("#searchEqpId");
			searchEqpId.bind("change cut input propertychange", searchNode);
			searchEqpId.bind('keydown', function (e){if(e.which == 13){searchNode();}});
		});
		
		function searchPanPieData(){
			$.ajax({
				url : '${ctx}/doorOrder/v01/fristPage/doorPie',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : true,
				cache : false,
				dataType : 'json',
				
				success : function(res) {
					var option = {
						 /* color:['#4472C5','#ED7C30','#80FF80','#FF8096'], */
						 color:['#f68787','#f8a978','#f1eb9a','#a4f6a5'],
						 backgroundColor: '#fff',
						 title : {
						        text: '今日存款业务类型占比',
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
				url : '${ctx}/doorOrder/v01/fristPage/doorLine',
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
							              return html + '当前张数'+':'+currentCount[params.dataIndex]+'<br/>'+ html1 + '可容纳张数'+':'+bagCapacity[params.dataIndex]+'<br/>'+ html2 +'当前金额'+':'+amount[params.dataIndex]
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
							                      var num = myColor.length;
							                      return myColor[params.dataIndex % num]
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
		
		function clear(){
			setTimeout(function (){
        	    window.onresize = function () {
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
				                      //var num = myColor.length;
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
     .all_tabs{height: 150px; padding: 10px 16px; color: #666;width:25%;float: left;}
     .chart_tab{border: 1px solid #e1ebf7;border-radius: 10px;margin: 6px 20px 6px 10px;padding: 5px 18px 36px;display: inline-block;position: relative;width: 92%;float: left;background: #fff;height: 100px;}
	 .h3_self{text-align: center;}
	 ul li{list-style: none;line-height:2;padding: 3px;}
	 
	 .client_panorama{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 59%;height:400px;float:left;overflow-x:auto;padding: 20px;margin: 10px 10px 10px 20px;}
	 .main_line_s{background: #e398fb1a !important;border-radius: 10px;width: 40%;height:400px;float:left;background: #fff;padding: 15px 0px;margin: 15px 0px 10px 20px;}
	 .week_pie{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 28%;height:400px;float:left;padding: 15px;margin: 15px 0px 15px 20px;}
	 #panoramaPie{background: #fffacd66 !important;border-radius: 10px;width: 22%;height:400px;float:left;padding: 15px;margin: 15px;}
	 .deposit_error_s{background:rgb(255, 251, 244) !important;border-radius: 10px;width: 66%;height: 400px; float: left;background: #fff;padding: 15px;margin: 15px 0px 15px 20px;}
	 .title_self{color: #333;font-size: 18px;line-height: 40px;font-weight: bold;}
	 .accordion-heading, .table th{color: #4e108a}
	 .count_self{color: #ea4c4c;padding: 0 0 0 10px;font-weight: bold;}
	 table, td, th{    border: 1px transparent solid;}
	 .use_percent{background: #f0fff2 !important;border-radius: 10px;width: 46%;height:400px;float:left;padding: 15px;margin: 15px 10px 15px 20px;}
	 .module_status{background:rgb(255, 244, 244) !important;border-radius: 10px;width: 44.5%;height:400px;float:left;background: #fff;padding: 15px;margin: 15px 10px 15px 20px;}
	 /* .conn_status{background: rgb(244, 249, 255) !important;border-radius: 10px;width: 40%;height:400px;float:left;padding: 15px;margin: 15px 20px 15px 33px;} */
	 @media only screen and (min-width: 1680px){
	        .all_tabs {width: 26%;height: 195px;}.chart_tab {height: 145px;}
	   }
	  @media only screen and (min-width: 1540px) and (max-width: 1680px){
	        .all_tabs {width: 25%;height: 150px;}
	        .chart_tab {height: 145px;}
	   }
	   @media only screen and (min-width: 1200px) and (max-width: 1540px){
	   		.all_tabs{height: 185px;}
		    .deposit_error_s{width: 63%;}
		    #panoramaPie{width: 44%;}
		    .main_line_s{width: 95%;}
		    .week_pie{width: 46%;}
		    .use_percent{width:44%;}
			.module_status{width:45%;}
			.chart_tab {height: 145px;}
			.all_tabs {width: 28%;}
	   }
	    @media only screen and (min-width: 1064px) and (max-width: 1200px){
	    	.all_tabs {width: 100%;margin-bottom: 55px;height: 185px;}
	        .chart_tab {width: 42%;height: 185px;}
		    .client_panorama,.week_pie,.deposit_error_s,#panoramaPie,.use_percent,.module_status{width: 92%;}
		    .main_line_s{width: 96%;}
	   }
	    @media only screen and (min-width: 400px) and (max-width: 1064px){
	    	.all_tabs {width: 100%;margin-bottom: 55px;height: 185px;}
	        .chart_tab {width: 40%;height: 185px;}
		    .client_panorama,.week_pie,.deposit_error_s,#panoramaPie{width: 92.2%;}
		    .use_percent,.module_status{width: 92.2%}
		    .main_line_s{width:95%;}
	   }
	   @media only screen and (max-width: 400px){
		    .chart_tab,.client_panorama,.week_pie,.deposit_error_s,.main_line_s,#panoramaPie,.use_percent,.module_status {width: 90%;}
	   }
	    #contentTable thead, .table_tbody_self tr {
		    display:table;
		    width:100%;
		    table-layout:fixed;
		}
		#contentTable tbody {
		    display:block;
		    height: 300px;
		    overflow-y:auto;
		    overflow-x:hidden;
		    border-top: 2px solid #ddd;		   
		}
		#contentTable th,td {
		    border:0px;
		}
		#contentTable thead {
		    width: calc( 100% - 1em ) 
		}
		.center-dev{display:flex;}
		.center-dev li{flex:1;text-align:center;font-size:14px;color:#666;margin-top:0px;}
		.center-dev li:nth-child(2){flex:2; }
		.center-dev li>span{
			display:block;font-size:23px;width: 70px;
		    height: 70px; border-radius: 50%;line-height: 70px;margin:0 auto;
		    color: #fff;}
	    .center-dev li:nth-child(1)>span{background: #ec5e5e;}
	    .center-dev li:nth-child(2)>span{background:#ffbe00 }
	    .th-back-color{background: rgb(255, 251, 244) !important;}
	    .th-back-color2{background-color: rgb(255, 244, 244) !important;}
	    .center-exc{display:flex;margin:0 0 10px 30px;}
		.center-exc li{flex:1;text-align:center;font-size:14px;color:#666;margin-top:0px;line-height: 1.5}
		.center-exc li:nth-child(2){flex:2; }
		.center-exc li>span{display:block;font-size:23px;line-height:40px;color:black;}
	</style>
</head>
<body>
<div class="whole_page">
	<div style="height: 90%;width: 100%;padding: 0px 34px;">
	    <div class="row">
	    	<div class="all_tabs">
				<div class="chart_tab" style="float: left;z-index:1">
					<h4 class="h3_self">今日存款情况</h4>
					<ul style="text-align: center;">
						<li>存款总额：<span class="text-red" style="font-size: 23px;font-weight: bold;">￥<fmt:formatNumber value="${officeAmount.depositAmount}" pattern="#,##0.00#" /></span></li>
					</ul>
					<ul class="center-exc">
					    <li><span class="text-red">${officeAmount.exceptionCount}</span>异常(未处理)</li>
						<li><span class="text-green">${officeAmount.processedCount}</span>异常(已处理)</li>
					</ul>
				</div>
				<div class="chart_tab" style="float: left;height:185px">
					<h4 class="h3_self">机具连线状态监控</h4>
					<ul >
					<li style="line-height:50px;text-align:center;text-indent: -40px;">机具总数：<span style="font-size:23px;color:#000">${officeAmount.count} </span> 台</li>
					</ul>
					<ul class="center-dev">
						<li><span >${officeAmount.exCount}  </span>异常(台)</li>
						<li><span >${officeAmount.elCount}  </span>正常/停用/关机/故障锁定(台)</li>
					</ul>
					<%-- <ul>
						<li>机具总数：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&doorFlag=1">${officeAmount.count}</a>  台</li>
						<li>异常：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=1&doorFlag=1">${officeAmount.exCount}</a>  台</li>
						<li>正常/停用/关机/故障锁定：<a onclick='readMessage(this.href)' href="${ctx}/doorOrder/v01/equipmentInfo?menuId=38&firstFlag=0&doorFlag=1">${officeAmount.elCount}</a>  台</li>
						<li>机具总数：${officeAmount.count}  台</li>
						<li>异常：${officeAmount.exCount}  台</li>
						<li>正常/停用/关机/故障锁定：${officeAmount.elCount}  台</li>
					</ul> --%>
				</div>
			</div>
			<!-- 差错统计表 -->
            <div class="deposit_error_s">
          	   <div style="text-align: center;"><span class="title_self">今日差错统计</span><span class="count_self">(总计：${officeAmount.wholeCount} 笔)</span></div>
	           <table id="contentTable" class="table table-hover" style="overflow-x:auto;">
				<thead>
					<tr>
						<!-- 序号 -->
						<th style="width: 8%;text-align:center" class="th-back-color"><spring:message code='common.seqNo'/></th>
						<!-- 预约单号 -->
						<%-- <th style="width: 24%;" class="th-back-color"><spring:message code="door.doorOrder.codeNo" /></th> --%>
						<!-- 差错类型 -->
						<th style="width: 12%;text-align:center" class="th-back-color"><spring:message code="door.errorInfo.type"/></th>
						<%-- <!-- 操作类型 -->
						<th style="width: 15%;text-align:center" class="th-back-color"><spring:message code="door.errorInfo.operateType"/></th> --%>
						<!-- 处理状态 -->
						<th style="width: 13%;text-align:center" class="th-back-color"><spring:message code="door.errorInfo.deal"/><spring:message code="common.status"/></th>
						<!-- 业务类型 -->
						<th style="width: 13%;text-align:center" class="th-back-color"><spring:message code="door.errorInfo.businessType"/></th>
						<!-- 差错金额 -->
						<th style="width: 18%;text-align:center" class="th-back-color"><spring:message code="door.errorInfo.amount"/>(元)</th>
					</tr>
				</thead>
				<tbody class="table_tbody_self">
					<c:forEach items="${depositErrorList}" var="doorCenterAccountsMain" varStatus="status">
						<tr>
							<td style="width: 8%;text-align:center">
								${status.index+1}
							</td>
							<%-- <td style="width: 24%;">
								${doorCenterAccountsMain.businessId}
							</td> --%>
							<td style="width: 12%;text-align:center">
								<c:if test="${doorCenterAccountsMain.errorType eq 2}">
									<spring:message code="door.errorInfo.long"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.errorType eq 3}">
									<spring:message code="door.errorInfo.short"/>
								</c:if>
							</td>
							<%-- <td style="width: 15%;">
								<c:if test="${doorCenterAccountsMain.businessStatus eq 1}">
									<spring:message code="door.errorInfo.register"/>
								</c:if>
								<c:if test="${doorCenterAccountsMain.businessStatus eq 2}">
									<spring:message code="door.errorInfo.cancel"/>
								</c:if>
							</td> --%>
							<td style="width: 13%;text-align:center">
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
							<td style="width: 13%;text-align:center">
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
							<td style="width: 18%;text-align:center">
								<fmt:formatNumber value="${doorCenterAccountsMain.totalAmount}" pattern="#,##0.00#" />
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
        	</div>
	    </div>
	    <div class="row">
	    	<!-- 门店各月份存款走势图 -->
        	<div id="mainLine" class="main_line_s"></div>
            <div id="weekPie" class="week_pie"></div>
	    	<!-- 存款类型占比扇形图 -->
			<div class="span12" id="panoramaPie"></div>
	    </div>
	    <!-- 钞袋使用状态百分比柱状图 -->
	    <div class="row" style="position: relative;">
	        <div style="position: absolute;z-index:100;left:50px;top:40px;">
		    <input id="searchEqpId" type="text" style="text-align:left;border:1px solid #b6d4ef;width:65%;background: #f0fff2;font-size: 12px;" placeholder="请输入机具编号" />
		    </div>
            <div id="usePercent" class="use_percent"></div>

            <!-- 机具模块状态-->
            <div class="module_status">
          	   	<div style="text-align: center;"><span class="title_self">预警提示</span></div>
	           	<table id="contentTable" class="table table-hover" style="overflow-x:auto;">
					<thead>
						<tr>
							<!-- 序号 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code='common.seqNo'/></th>
							<!-- 机具编号 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code="door.equipment.number"/></th>
							<!-- 清分机 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code="door.equipment.module.clearStatus"/></th>
							<!-- 打印机 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code="door.equipment.module.printerStatus"/></th>
							<!-- 保险柜仓门 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code="door.equipment.module.doorStatus"/></th>
							<!-- 在线状态 -->
							<th style="width: 16%;text-align:center;" class="th-back-color2"><spring:message code='door.equipment.updateDate'/></th>
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
									<!-- 凭条打印机状态：1正常，2故障，3掉线 ，4缺纸-->
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
