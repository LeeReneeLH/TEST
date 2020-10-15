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
			peopleDayChart();
			peopleMouthChart();
			//设置日合计宽度
			$("#tWidth").width($("#aWidth").width());
			//设置日金额宽度
			$("#jeWidth").width($("#jWidth").width()+20);
			//设置月合计宽度
			$("#tmWidth").width($("#amWidth").width());
			//设置月金额宽度
			$("#jemWidth").width($("#jmWidth").width()+20);
		});
		var lineChartOption = {
				legend: {
			        data:[]
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
			    ]
			};
		//人民银行首页统计图（日）
		 function peopleDayChart(){
			lineChart.clear();
			lineChart.showLoading();
			$.ajax({
				url : '${ctx}/clear/v03/blankPage/peopleChart',
				type : 'post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
					'param' : 'day'
				},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res){
					lineChart.hideLoading();
					lineChart.clear();
					lineChart.setOption(lineChartOption);
					lineChart.setOption({
						legend : {
							itemGap : 1,
							itemHeight : 10,
							orient : 'vertical',
							x : 'right',
			                data : res.legendDataList
			           },
			           series : res.seriesDataList
					});
				},
				error : function() {
				}
			});
		} 
		
		//人民银行首页统计图（月）
		function peopleMouthChart(){
			barChart.clear();
			barChart.showLoading();
			$.ajax({
				url : '${ctx}/clear/v03/blankPage/peopleMouthChart',
				type : 'post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
					'param' : 'mouth'
				},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res){
					barChart.hideLoading();
					barChart.clear();
					barChart.setOption(lineChartOption);
					barChart.setOption({
						legend : {
							itemGap : 1,
							itemHeight : 10,
							orient : 'vertical',
							x : 'right',
			                data : res.seriesDataList
			           },
			           xAxis : {
			        	   name : '日',
							data : res.xAxisDataList
						},
			           series : res.seriesDataList
					});
				},
				error : function() {
				}
			});
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
	<div>
		<div style="height:340px; width:60%; float:left; overflow-y:scroll; ">
		<table  class="table table-bordered text-center">
  			<tr>
  			 	<!--  券            别         (捆) -->
 			    <td colspan="12"><b><spring:message code="clear.public.bankDraft" /></b></td>
 		   		<!--金 额 -->
 		   		<td colspan="3" id="jWidth"><b><spring:message code="clear.public.moneyFormat" /></b></td>
  			</tr>
		    <tr>
    			<!-- 代理上缴 -->
    			<td colspan="2" id="aWidth"><spring:message code="clear.public.agencyPay" />(<spring:message code="clear.public.today" />)</td>
     			<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
				<!-- 捆 -->
				<th>${item.label}</th>
				</c:forEach>
    			<!-- 金额 -->
    			<td class="text-light-blue"><b><spring:message code="clear.public.moneyFormat" /></b></td>
    			<!-- 总金额 -->
    			<td class="text-light-blue" colspan="2"><b><spring:message code="clear.task.totalAmt" /></b></td>
  			</tr>
  			<c:forEach items="${clOutMainDayList}" var="clOutMainDay">
  			<tr>
  				<!-- 商行名称 -->
    			<td rowspan="2">${fns:getOfficeName(clOutMainDay.custNo).name}</td>
    			<!-- 完整券 -->
    			<td><spring:message code="clear.public.Wzq" /></td>
    			<td>${clOutMainDay.cl01}</td>
    			<td>${clOutMainDay.cl02}</td>
    			<td>${clOutMainDay.cl03}</td>
    			<td>${clOutMainDay.cl04}</td>
    			<td>${clOutMainDay.cl05}</td>
    			<td>${clOutMainDay.cl06}</td>
    			<td>${clOutMainDay.cl07}</td>
    			<td>${clOutMainDay.cl08}</td>
    			<td>${clOutMainDay.cl09}</td>
    			<td>${clOutMainDay.cl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMainDay.totalAmt}" pattern="#,##0.00#" /></td>
    			<td colspan="2" rowspan="2"><fmt:formatNumber value="${clOutMainDay.totalAmt+clOutMainDay.totalAmtc}" pattern="￥ #,##0.00#" /></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette text-red">
    			<!-- 残损券 -->
    			<td><spring:message code="clear.public.Csq" /></td>
    			<td>${clOutMainDay.ccl01}</td>
    			<td>${clOutMainDay.ccl02}</td>
    			<td>${clOutMainDay.ccl03}</td>
    			<td>${clOutMainDay.ccl04}</td>
    			<td>${clOutMainDay.ccl05}</td>
    			<td>${clOutMainDay.ccl06}</td>
    			<td>${clOutMainDay.ccl07}</td>
    			<td>${clOutMainDay.ccl08}</td>
    			<td>${clOutMainDay.ccl09}</td>
    			<td>${clOutMainDay.ccl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMainDay.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
  			</c:forEach>
		</table>
		</div>
		<div class="span12" id="mainline" style="width: 38.5%; height:360px; float:left;"></div>
		<div style="width:60%;">
		<table  class="table table-bordered text-center">
		    <tr>
    			<!-- 合计 -->
    			<td  id="tWidth"><spring:message code="common.total" /></td>
    			<td>${clOutMainDay.cl01+clOutMainDay.ccl01}</td>
    			<td>${clOutMainDay.cl02+clOutMainDay.ccl02}</td>
    			<td>${clOutMainDay.cl03+clOutMainDay.ccl03}</td>
    			<td>${clOutMainDay.cl04+clOutMainDay.ccl04}</td>
    			<td>${clOutMainDay.cl05+clOutMainDay.ccl05}</td>
    			<td>${clOutMainDay.cl06+clOutMainDay.ccl06}</td>
    			<td>${clOutMainDay.cl07+clOutMainDay.ccl07}</td>
    			<td>${clOutMainDay.cl08+clOutMainDay.ccl08}</td>
    			<td>${clOutMainDay.cl09+clOutMainDay.ccl09}</td>
    			<td>${clOutMainDay.cl10+clOutMainDay.ccl10}</td>
    			<!-- 总金额 -->
    			<td class="text-light" id="jeWidth"><fmt:formatNumber value="${clOutMainDay.totalAmt+clOutMainDay.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
		</table>
		</div>
		<div style="height:340px; width:60%; float:left; overflow-y:scroll;">
		<table class="table table-bordered text-center">
  			<tr>
  			 	<!--  券            别         (捆) -->
 			    <td colspan="12"><b><spring:message code="clear.public.bankDraft" /></b></td>
 		   		<!--金 额 -->
 		   		<td colspan="3" id="jmWidth"><b><spring:message code="clear.public.moneyFormat" /></b></td>
  			</tr>
		    <tr>
    			<!-- 代理上缴 -->
    			<td colspan="2" id="amWidth"><spring:message code="clear.public.agencyPay" />(<spring:message code="clear.public.mouth" />)</td>
     			<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
				<!-- 捆 -->
				<th >${item.label}</th>
				</c:forEach>
    			<!-- 金额 -->
    			<td class="text-light-blue"><b><spring:message code="clear.public.moneyFormat" /></b></td>
    			<!-- 总金额 -->
    			<td class="text-light-blue" colspan="2"><b><spring:message code="clear.task.totalAmt" /></b></td>
  			</tr>
  			<c:forEach items="${clOutMainMouthList}" var="clOutMainMouth">
  			<tr>
  				<!-- 商行名称 -->
    			<td rowspan="2">${fns:getOfficeName(clOutMainMouth.custNo).name}</td>
    			<!-- 完整券 -->
    			<td><spring:message code="clear.public.Wzq" /></td>
    			<td>${clOutMainMouth.cl01}</td>
    			<td>${clOutMainMouth.cl02}</td>
    			<td>${clOutMainMouth.cl03}</td>
    			<td>${clOutMainMouth.cl04}</td>
    			<td>${clOutMainMouth.cl05}</td>
    			<td>${clOutMainMouth.cl06}</td>
    			<td>${clOutMainMouth.cl07}</td>
    			<td>${clOutMainMouth.cl08}</td>
    			<td>${clOutMainMouth.cl09}</td>
    			<td>${clOutMainMouth.cl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMainMouth.totalAmt}" pattern="#,##0.00#" /></td>
    			<td colspan="2" rowspan="2"><fmt:formatNumber value="${clOutMainMouth.totalAmt+clOutMainMouth.totalAmtc}" pattern="￥ #,##0.00#" /></td>
  			</tr>
  			<tr class="bg-gray disabled color-palette text-red">
    			<!-- 残损券 -->
    			<td><spring:message code="clear.public.Csq" /></td>
    			<td>${clOutMainMouth.ccl01}</td>
    			<td>${clOutMainMouth.ccl02}</td>
    			<td>${clOutMainMouth.ccl03}</td>
    			<td>${clOutMainMouth.ccl04}</td>
    			<td>${clOutMainMouth.ccl05}</td>
    			<td>${clOutMainMouth.ccl06}</td>
    			<td>${clOutMainMouth.ccl07}</td>
    			<td>${clOutMainMouth.ccl08}</td>
    			<td>${clOutMainMouth.ccl09}</td>
    			<td>${clOutMainMouth.ccl10}</td>
    			<td style="text-align:right;"><fmt:formatNumber value="${clOutMainMouth.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
  			</c:forEach>
		</table>
		</div>
		<div class="span12" id="mainlines" style="width: 38.5%; height:360px; float:left;"></div>
		<div style="width:60%;">
		<table  class="table table-bordered text-center">
		    <tr>
    			<!-- 合计 -->
    			<td id="tmWidth"><spring:message code="common.total" /></td>
    			<td style="width:46px;">${clOutMainMouth.cl01+clOutMainMouth.ccl01}</td>
    			<td style="width:36px;">${clOutMainMouth.cl02+clOutMainMouth.ccl02}</td>
    			<td style="width:39px;">${clOutMainMouth.cl03+clOutMainMouth.ccl03}</td>
    			<td style="width:36px;">${clOutMainMouth.cl04+clOutMainMouth.ccl04}</td>
    			<td style="width:px;">${clOutMainMouth.cl05+clOutMainMouth.ccl05}</td>
    			<td style="width:px;">${clOutMainMouth.cl06+clOutMainMouth.ccl06}</td>
    			<td style="width:;">${clOutMainMouth.cl07+clOutMainMouth.ccl07}</td>
    			<td style="width:px;">${clOutMainMouth.cl08+clOutMainMouth.ccl08}</td>
    			<td >${clOutMainMouth.cl09+clOutMainMouth.ccl09}</td>
    			<td>${clOutMainMouth.cl10+clOutMainMouth.ccl10}</td>
    			<!-- 总金额 -->
    			<td class="text-light" id="jemWidth"><fmt:formatNumber value="${clOutMainMouth.totalAmt+clOutMainMouth.totalAmtc}" pattern="#,##0.00#" /></td>
  			</tr>
		</table>
		</div>
		<div>
		</div>
	</div>
</body>
</html>
