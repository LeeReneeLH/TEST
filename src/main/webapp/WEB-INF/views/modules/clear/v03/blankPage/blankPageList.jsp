<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 空白页 -->
	<title>空白页</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
		//折线图
		var lineChart;
		var barChart;
		$(document).ready(function() {
			var mainline = document.getElementById('mainline');
			if(mainline != null){
				//报表初始化
			lineChart = echarts.init(document.getElementById('mainline'),
					'shine');
				}
			var mainlines = document.getElementById('mainlines');
			if(mainlines != null){
				//报表初始化
			barChart = echarts.init(document.getElementById('mainlines'),
					'shine');
				}
			searchData();
		});
		var lineChartOption = {
				legend: {
					data : ["商行预约清分量","代理上缴","商行回款"]
			    },
			    tooltip : {
			        trigger: 'axis',
			        //设置hover显示的样式
			        formatter:function(params){
				  		var result = '';
		 				var res='';
		 			for(var i=0;i<params.length;i++){
		 				result = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
						res+='<p>'+result+params[i].seriesName+':'+formatCurrencyFloat(params[i].data)+'</p >'
			 		}
			 				return res;
					   } 
			    },
				grid : {
					left : '3%',
					right : '20%',
					bottom : '10%',
					containLabel : true
				},
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			           	boundaryGap : false,
			            data : [""]
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
			             name:"商行预约清分量",
			    		 type:'bar',
			             data:[${orderClearMain.totalAmt}],
			             itemStyle: {
			            	 normal: {
			            		 label : {
			            			 show: true, 
			                         position: 'top',
			                         formatter: function (params) {
			                        	 return formatCurrencyFloat(params.value);
			                        	}
			                         }
			            	 }
			             }
			         },
			         {
			             name:"代理上缴",
			    		 type:'bar',
			             data:[${clOutMain.totalAmt+clOutMain.totalAmtc}],
			             itemStyle: {
			            	 normal: {
			            		 label : {
			            			 show: true, 
			                         position: 'top',
			                         formatter: function (params) {
			                        	 return formatCurrencyFloat(params.value);
			                        	}
			                         }
			            	 }
			             }
			         },
			         {
			             name:"商行回款",
			    		 type:'bar',
			             data:[${clOutMains.totalAmt+clOutMains.totalAmtc}],
			             itemStyle: {
			            	 normal: {
			            		 label : {
			            			 show: true, 
			                         position: 'top',
			                         formatter: function (params) {
			                        	 return formatCurrencyFloat(params.value);
			                        	}
			                         }
			            	 }
			             }
			         }
			    ]
			};
		var barChartOption = {
				legend: {
					data : ["100元","50元","20元","10元","5元","2元","1元","5角","2角","1角"]
			    },
			    tooltip : {
			        trigger: 'axis'
			    },
				grid : {
					left : '3%',
					right : '20%',
					bottom : '10%',
					containLabel : true
				},
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			           	boundaryGap : false,
			            data : [""]
			        }
			    ],
			    yAxis : [
			        {
			        	name : '数量(捆)',
			            type : 'value'
			        }
			    ],
			    series : [
			    	 {
			             name:"100元",
			    		 type:'bar',
			             data:[${orderClearMain.cl01+clOutMain.cl01+clOutMain.ccl01+clOutMains.cl01+clOutMains.ccl01}],
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
			             name:"50元",
			    		 type:'bar',
			             data:[${orderClearMain.cl02+clOutMain.cl02+clOutMain.ccl02+clOutMains.cl02+clOutMains.ccl02}],
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
			             name:"20元",
			    		 type:'bar',
			             data:[${orderClearMain.cl03+clOutMain.cl03+clOutMain.ccl03+clOutMains.cl03+clOutMains.ccl03}],
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
			             name:"10元",
			    		 type:'bar',
			             data:[${orderClearMain.cl04+clOutMain.cl04+clOutMain.ccl04+clOutMains.cl04+clOutMains.ccl04}],
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
			             name:"5元",
			    		 type:'bar',
			             data:[${orderClearMain.cl05+clOutMain.cl05+clOutMain.ccl05+clOutMains.cl05+clOutMains.ccl05}],
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
			             name:"2元",
			    		 type:'bar',
			             data:[${orderClearMain.cl06+clOutMain.cl06+clOutMain.ccl06+clOutMains.cl06+clOutMains.ccl06}],
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
			             name:"1元",
			    		 type:'bar',
			             data:[${orderClearMain.cl07+clOutMain.cl07+clOutMain.ccl07+clOutMains.cl07+clOutMains.ccl07}],
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
			             name:"5角",
			    		 type:'bar',
			             data:[${orderClearMain.cl08+clOutMain.cl08+clOutMain.ccl08+clOutMains.cl08+clOutMains.ccl08}],
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
			             name:"2角",
			    		 type:'bar',
			             data:[${orderClearMain.cl09+clOutMain.cl09+clOutMain.ccl09+clOutMains.cl09+clOutMains.ccl09}],
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
			             name:"1角",
			    		 type:'bar',
			             data:[${orderClearMain.cl10+clOutMain.cl10+clOutMain.ccl10+clOutMains.cl10+clOutMains.ccl10}],
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
			};
		function searchData() {
			lineChart.clear();
			var bRtn = false;
			lineChart.showLoading();
			lineChart.hideLoading();
			lineChart.clear();
			lineChart.setOption(lineChartOption);
			barChart.clear();
			var bRtn = false;
			barChart.showLoading();
			barChart.hideLoading();
			barChart.clear();
			barChart.setOption(barChartOption);
		}
		
		
		/* 千分位用逗号分隔 */
		function formatCurrencyFloat(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
			return (((sign) ? '' : '-') + num + '.' + cents);
		}
	</script>
	<style>
     .table tr td {vertical-align: middle;}
     .table ,.table tr,.table tr th,.table tr td {flex:auto}
     .bg-gray { background-color: #f1f2f5 !important}
	</style>
</head>
<body>
	<div >
		<table border="0" cellspacing="0" cellpadding="0" class="table table-bordered text-center">
  			<tr>
  			 	<!--  券            别         (捆) -->
 			    <td colspan="12"><b><spring:message code="clear.public.bankDraft" /></b></td>
 		   		<!-- 清 分 金 额 -->
 		   		<td colspan="3"><b><spring:message code="clear.public.clarifyAmount" /></b></td>
  			</tr>
		    <tr>
    			<!-- 商行预约清分量 -->
    			<td rowspan="2"><spring:message code="clear.public.orderClarift" />(当日)</td>
    			<td>&nbsp;</td>
     			<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
				<!-- 捆 -->
				<th>${item.label}</th>
				</c:forEach>
    			<!-- 金额 -->
    			<td class="text-light-blue"><b><spring:message code="clear.public.moneyFormat" /></b></td>
    			<!-- 总金额 -->
    			<td class="text-light-blue" colspan="2"><b><spring:message code="clear.task.totalAmt" /></b></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette ">
   				<td>&nbsp;</td>
    			<td>${orderClearMain.cl01}</td>
    			<td>${orderClearMain.cl02}</td>
    			<td>${orderClearMain.cl03}</td>
    			<td>${orderClearMain.cl04}</td>
    			<td>${orderClearMain.cl05}</td>
    			<td>${orderClearMain.cl06}</td>
    			<td>${orderClearMain.cl07}</td>
    			<td>${orderClearMain.cl08}</td>
    			<td>${orderClearMain.cl09}</td>
    			<td>${orderClearMain.cl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${orderClearMain.totalAmt}" pattern="#,##0.00#" /></td>
    			<td colspan="2"><fmt:formatNumber value="${orderClearMain.totalAmt}" pattern="￥ #,##0.00#" /></td>
  			</tr>
  			<tr>
  				<!-- 代理上缴 -->
    			<td rowspan="2"><spring:message code="clear.public.agencyPay" />(当日)</td>
    			<!-- 完整券 -->
    			<td><spring:message code="clear.public.Wzq" /></td>
    			<td>${clOutMain.cl01}</td>
    			<td>${clOutMain.cl02}</td>
    			<td>${clOutMain.cl03}</td>
    			<td>${clOutMain.cl04}</td>
    			<td>${clOutMain.cl05}</td>
    			<td>${clOutMain.cl06}</td>
    			<td>${clOutMain.cl07}</td>
    			<td>${clOutMain.cl08}</td>
    			<td>${clOutMain.cl09}</td>
    			<td>${clOutMain.cl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMain.totalAmt}" pattern="#,##0.00#" /></td>
    			<td colspan="2" rowspan="4"><fmt:formatNumber value="${clOutMain.totalAmt+clOutMain.totalAmtc+clOutMains.totalAmt+clOutMains.totalAmtc}" pattern="￥ #,##0.00#" /></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette text-red">
    			<!-- 残损券 -->
    			<td><spring:message code="clear.public.Csq" /></td>
    			<td>${clOutMain.ccl01}</td>
    			<td>${clOutMain.ccl02}</td>
    			<td>${clOutMain.ccl03}</td>
    			<td>${clOutMain.ccl04}</td>
    			<td>${clOutMain.ccl05}</td>
    			<td>${clOutMain.ccl06}</td>
    			<td>${clOutMain.ccl07}</td>
    			<td>${clOutMain.ccl08}</td>
    			<td>${clOutMain.ccl09}</td>
    			<td>${clOutMain.ccl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMain.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
  			<tr>
  				<!-- 商行回款 -->
    			<td rowspan="2"><spring:message code="clear.public.bankgets" />(当日)</td>
   		 		<!-- 已清分 -->
   		 		<td><spring:message code="clear.public.haveClear" /></td>
   				<td>${clOutMains.ccl01}</td>
   				<td>${clOutMains.ccl02}</td>
   				<td>${clOutMains.ccl03}</td>
   				<td>${clOutMains.ccl04}</td>
    			<td>${clOutMains.ccl05}</td>
    			<td>${clOutMains.ccl06}</td>
        		<td>${clOutMains.ccl07}</td>
    			<td>${clOutMains.ccl08}</td>
    			<td>${clOutMains.ccl09}</td>
    			<td>${clOutMains.ccl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMains.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette text-red">
    			<!-- 未清分 -->
    			<td><spring:message code="clear.public.noneClear" /></td>
    			<td>${clOutMains.cl01}</td>
    			<td>${clOutMains.cl02}</td>
    			<td>${clOutMains.cl03}</td>
   		 		<td>${clOutMains.cl04}</td>
    			<td>${clOutMains.cl05}</td>
    			<td>${clOutMains.cl06}</td>
    			<td>${clOutMains.cl07}</td>
    			<td>${clOutMains.cl08}</td>
    			<td>${clOutMains.cl09}</td>
    			<td>${clOutMains.cl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMains.totalAmt}" pattern="#,##0.00#" /></td>
  			</tr>
  			<tr>
  				<!-- 当日差错 -->
    			<td rowspan="3"><spring:message code="clear.clErrorInfo.errorToday" /></td>
    			<td>&nbsp;</td>
    			<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
				<!-- 捆 -->
				<th>${item.label}</th>
				</c:forEach>
    			<!-- 差错合计 -->
    			<td class="text-light-blue"><b><spring:message code="clear.clErrorInfo.errorTotal" /></b></td>
    			<!-- 备付金昨日余额 -->
    			<td class="text-light-blue"><b><spring:message code="clear.provision.yesterdayBalance" /></b></td>
   	 			<!-- 备付金本日余额 -->
    			<td class="text-light-blue"><b><spring:message code="clear.provision.todayBalance" /></b></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette">
    			<!-- 笔数 -->
    			<td><spring:message code="clear.clErrorInfo.theNumber" /></td>
    			<td>${clErrorInfo.cl01}</td>
    			<td>${clErrorInfo.cl02}</td>
    			<td>${clErrorInfo.cl03}</td>
    			<td>${clErrorInfo.cl04}</td>
    			<td>${clErrorInfo.cl05}</td>
    			<td>${clErrorInfo.cl06}</td>
    			<td>${clErrorInfo.cl07}</td>
    			<td>${clErrorInfo.cl08}</td>
    			<td>${clErrorInfo.cl09}</td>
    			<td>${clErrorInfo.cl10}</td>
    			<td>&nbsp;</td>
    			<td>&nbsp;</td>
  			</tr>
  			<tr>
  				<!-- 金额 -->
    			<td><spring:message code="clear.public.moneyFormat" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl01}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl02}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl03}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl04}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl05}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl06}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl07}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl08}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl09}" pattern="#,##0.00#" /></td>
    			<td><fmt:formatNumber value="${clErrorInfo.ccl10}" pattern="#,##0.00#" /></td>
    			<td class="text-red"><fmt:formatNumber value="${clErrorInfo.errorMoney}" pattern="#,##0.00#" /></td>
    			<td class="text-yellow"><fmt:formatNumber value="${beforeAmount}" pattern="#,##0.00#" /></td>
    			<td class="text-green"><fmt:formatNumber value="${totalAmount}" pattern="#,##0.00#" /></td>
  			</tr>
		</table>
		<div class="row" style="margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 45%; height:400px; float:left;"></div>
	            <div class="span12" id="mainlines" style="width: 50%; height:400px; float:left;"></div>
	        </div>
		</div>
	</div>
</body>
</html>
