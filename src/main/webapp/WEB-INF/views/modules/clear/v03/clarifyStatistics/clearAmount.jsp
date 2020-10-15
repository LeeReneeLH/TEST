<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分业务统计图 -->
	<title><spring:message code="clear.report.clearAmount"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
	var clearAmount;
	//折线图
	var lineChart;
	$(document).ready(function (){
		var clear = document.getElementById('clearAmount');
		if(clear != null){
			clearAmount = echarts.init(document.getElementById('clearAmount'),'shine'); 
			/* 加载页面时查询数据	 修改人：wxz 2017-11-27 */
			createGraph();
		}
	
		/* end */
		//点击查询按钮
		$("#btnSubmit").click(function(){
			var clear = document.getElementById('clearAmount');
			if(clear != null){
				createGraph();
			}
		});
	});
		/* 清分业务出入库总金额查询 	修改人:wxz	2017-11-27 */
	 	function searchClearInOrOut() {
			var bRtn = false;
			//查询折线图
			clearAmount.showLoading(); 
			$.ajax({
				url : '${ctx}/clear/v03/ClarifyStatisticsCtrl/clearAmount',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
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
						           timeline: {
						               axisType: 'category',
						               autoPlay: false,
						               playInterval: ${fns:getConfig("firstPage.echart.amount.playInterval")},
						               data: res.timelineDataList,
						               currentIndex:res.timelineDataList.length-1
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
						       options:res.seriesDataList,
						   });
							bRtn = true;
						}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					if("error"!=textStatus){
						alert('您已登录超时或在其他地点登录，请重新登录！');
						top.location = "${ctx}";
					}
					return false;
				}
			});
			return bRtn;
		}
	 	/* end */
		
		/* 使用Echart3创建数据图形		修改人：wxz  2017-11-27 */
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
					searchClearInOrOut(); 
			      });
	  	     }
		/* end */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <!-- 清分业务统计图 -->
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.report.clearAmount"/></a></li>
	</ul>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="dayReportMain" action="${ctx}/report/v01/AllocateReportController" method="post" class="breadcrumb form-search">

			<label><spring:message code="common.startDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime"  
				   value="<fmt:formatDate value="${dayReportMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- end -->
			<label><spring:message code="common.endDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${dayReportMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			<!-- end -->
			<!-- 查询按钮 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
		</form:form>
	</div>
	
	<div style="overflow-y: auto; height: 900px;">
	    <!-- 清分业务出入库金额统计图   修改人：wxz 2017-11-27 begin -->
		<div class="row" style="margin-left: 80px;margin-top: 50px">
			<div>
				<div id="clearAmount" class="span12" style="width: 85%;height:400px;"></div>
			</div>
		</div>
		<!-- end -->
    </div>
</body>
</html>
