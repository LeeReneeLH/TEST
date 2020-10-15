<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 预约清分业务量统计图 -->
	<title>预约清分业务量统计图</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
	var reserveClearVolumeChart;

	$(document).ready(function (){
		/* 加载页面时查询数据	 修改人：wxz 2017-11-27 begin */
		createGraph();
		/* end */
		//点击查询按钮
		$("#btnSubmit").click(function(){
			/* 查询数据 	修改人：wxz	2017-11-27  begin */
			reserveClearVolume();
			/* end */
		});
	});
	
	// 预约清分业务量图表的配置项和数据
	var lineChartOption = {
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
			url : '${ctx}/report/v01/orderClearVolume/reserveClearVolume',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {
				'createTimeStart' : $("#createTimeStart").val(),
				'createTimeEnd' : $("#createTimeEnd").val()
			},
			dataType : 'json',
			async : true,
			cache : false,
			success : function(res) {
				if (res.seriesDataList.length == 0) {
					$("#reserveClearVolume").hide();
				} else {
					reserveClearVolumeChart.setOption(lineChartOption);
					
					$("#reserveClearVolume").show();
				    // 填入数据
				    reserveClearVolumeChart.setOption({
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
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
				if("error"!=textStatus){
					alert('您已登录超时或在其他地点登录，请重新登录！');
					top.location = "${ctx}";
				}
				return false;
			}
		});
	}
		
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
					reserveClearVolumeChart = echarts.init(document.getElementById('reserveClearVolume'),'shine');
					reserveClearVolume();
			      });
	  	     }
		/* end */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <!-- 清分中心统计图 -->
        <li class="active"><a href="#" onclick="javascript:return false;">预约清分业务量统计图</a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="orderClearMain" action="${ctx}/report/v01/orderClearVolume" method="post" class="breadcrumb form-search">
			<!-- 过滤条件 -->
			<%-- <label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small">
			<form:options items="${fns:getDictList('report_filter_condition')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select> --%>
			<label><spring:message code="common.startDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime"  
				   value="<fmt:formatDate value="${orderClearMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- end -->
			<label><spring:message code="common.endDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${orderClearMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			<!-- end -->
			<!-- 查询按钮 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
		</form:form>
	</div>
	<div style="overflow-y: auto; height: 900px;">
		<!-- 业务量折线图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="reserveClearVolume" style="width: 85%;height:400px;"></div>
	        </div>
	    </div>
    </div>
</body>
</html>
