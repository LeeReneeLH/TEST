<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.box.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
<!-- 引入 vintage 主题 -->
<script src="${ctxStatic}/echart3/theme/shine.js"></script>
<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
<script type="text/javascript">
	var showToolBox = true;
	if($.browser.msie){
		if($.browser.version <= 8){
			showToolBox = false;
		}
	}
	var lineChart, inBarChart, outBarChart;
	$(document).ready(function() {

		lineChart = echarts.init(document.getElementById('mainline'), 'shine');
		inBarChart = echarts.init(document.getElementById('inbar'));
		outBarChart = echarts.init(document.getElementById('outbar'));
		
		searchData();

		$("#btnExport").click(function(){
			$("#searchForm").attr("action", "${ctx}/report/v01/allocation/exportCashBetweenReport");
			$("#searchForm").submit();
			$("#searchForm").attr("action", "${ctx}/report/v01/allocation/cashBetweenReport");
		});
		
		$("#btnSubmit").click(function(){
			searchData();
		});
	});

	function searchData() {
		
		lineChart.clear();
		inBarChart.clear();
		outBarChart.clear();
		
		var bRtn = false;
		if($("#businessType").val()=='21'){
			$("#inbar").show();
			$("#outbar").show();
			bRtn = searchInBarData();
			if (bRtn) {
				searchOutBarData();
				searchLineData();
			}
		}
		if($("#businessType").val()=='22'){
			outBarChart.clear();
			$("#inbar").hide();
			$("#outbar").show();
			bRtn = searchOutBarData();
			if (bRtn) {
				searchLineData();
			}
		}
		if($("#businessType").val()=='23'){
			inBarChart.clear();
			$("#outbar").hide();
			$("#inbar").show();
			bRtn = searchInBarData();
			if (bRtn) {
				searchLineData();
			}
		}
		if($("#businessType").val()==''){
			$("#inbar").show();
			$("#outbar").show();
			bRtn = searchInBarData();
			if (bRtn) {
				bRtn = searchOutBarData();
				if (bRtn) {
					searchLineData();
				}
			}
		}
		if (!bRtn) {
			alertx("<spring:message code='message.E1067' />");
		}
	}
	
	// 指定图表的配置项和数据
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
			right : '25%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
			show : showToolBox,
			feature : {
				dataZoom : {
					show : true
				},
				dataView : {
					show : true,
					// 数据视图
					title : '<spring:message code='report.data.view'/>',
	                readOnly: true,
	                // 数据视图关闭及刷新
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
			                  	table = table + '<td style="text-align:right;">' + series[seriesIndex].data[i]  + '</td>';
			                 }        
                            table = table + '</tr>';
	                    }
	                    table += '</tbody></table></div>';
	                    return table;
	                }

				},
				magicType: {
		                show : false,
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
			}
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

	var BarChartOption = {
		tooltip : {
			trigger : 'axis',
			axisPointer : { // 坐标轴指示器，坐标轴触发有效
				type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		legend : {
			type:'scroll',
			orient : 'vertical',
			right : 10,
			top: 40,
			bottom: 10,
			data : []
		},
		toolbox : {
			show : showToolBox,
			feature : {
				dataZoom : {
					show : true
				},
				dataView : {
					show : true,
					// 数据视图
					title : '<spring:message code='report.data.view'/>',
	                readOnly: true,
	                // 数据视图关闭及刷新
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
			                  	table = table + '<td style="text-align:right;">' + series[seriesIndex].data[i]  + '</td>';
			                 }        
                            table = table + '</tr>';
	                    }
	                    table += '</tbody></table></div>';
	                    return table;
	                }
				},
				magicType: {
		                show : false,
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
			}
		},
		grid : {
			left : '3%',
			right : '25%',
			bottom : '3%',
			containLabel : true
		},
		xAxis : {
			type : 'category',
			data : []

		},
		yAxis : {
			name:'<spring:message code = "report.money.element"/>',
			type : 'value'
		},
		series : []
	};
	
	function searchInBarData() {
		var bRtn = false;
		inBarChart.showLoading();
		$.ajax({
			url : '${ctx}/report/v01/allocation/searchGraphInfo',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {
				'createTimeStart' : $("#createTimeStart").val(),
				'createTimeEnd' : $("#createTimeEnd").val(),
				'dateFlag' : $("#dateFlag").val(),
				'businessType' : $("#businessType").val(),
				'inoutType' : '1',
				'type' :'bar'
			},
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				inBarChart.hideLoading();
				
				if (res.seriesDataList.length == 0) {
					$('#btnExport').hide();
					bRtn = false;
				} else {
					inBarChart.setOption(BarChartOption);
					
					$("#btnExport").show();
				    // 填入数据
				    inBarChart.setOption({				
					    title : {
						    text : res.titleMessage+'<spring:message code='report.goods.in.chart' />'
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
				    bRtn = true;
				}
			},
			error : function() {
				bRtn = false;
			}
		});
		
		return bRtn;
	}
	function searchOutBarData() {
		var bRtn = false;
		outBarChart.showLoading();
		$.ajax({
			url : '${ctx}/report/v01/allocation/searchGraphInfo',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {
				'createTimeStart' : $("#createTimeStart").val(),
				'createTimeEnd' : $("#createTimeEnd").val(),
				'dateFlag' : $("#dateFlag").val(),
				'businessType' : $("#businessType").val(),
				'inoutType' : '0',
				'type' :'bar'
			},
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				outBarChart.hideLoading();
				
				if (res.seriesDataList.length == 0) {
					$('#btnExport').hide();
					bRtn = false;
				} else {
					$("#btnExport").show();
					
					outBarChart.setOption(BarChartOption);
				    // 填入数据
				    outBarChart.setOption({
					
					    title : {
						    text : res.titleMessage+'<spring:message code='report.goods.out.chart' />'
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
				    bRtn = true;
				}
			},
			error : function() {
				bRtn = false;
			}
		});
		return bRtn;
	}
	function searchLineData() {
		var bRtn = false;
		lineChart.showLoading();

		$.ajax({
			url : '${ctx}/report/v01/allocation/searchGraphInfo',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {
				'createTimeStart' : $("#createTimeStart").val(),
				'createTimeEnd' : $("#createTimeEnd").val(),
				'dateFlag' : $("#dateFlag").val(),
				'businessType' : $("#businessType").val(),
				'type' :'line'
			},
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				lineChart.hideLoading();
				
				if (res.seriesDataList.length == 0) {
					$("#btnExport").hide();
					bRtn = false;
				} else {
					lineChart.setOption(lineChartOption);
					
					$("#btnExport").show();
				    // 填入数据
				    lineChart.setOption({
				    	title : {
						    text : res.titleMessage+'<spring:message code='report.work.volume.chart' />'
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
				    
				    bRtn = true;
				}
			},
			error : function() {
				bRtn = false;
			}
		});
		return bRtn;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#"><spring:message code="report.cashBetweenReport" /></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="allAllocateInfo"
			method="post" class="breadcrumb form-search">
	
			<%-- 业务种别 --%>
			<c:set var="cashBusinessType"
				value="${fns:getConfig('allocation.cash.businessType')}" />
			<label><spring:message code='allocation.business.type' />：</label>
			<form:select path="businessType" id="businessType" class="input-small">
				<form:options
					items="${fns:getFilterDictList('all_businessType', true, cashBusinessType)}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
	
			<%-- 报表过滤条件 --%>
			<label><spring:message code="common.filterCondition"/>：</label>
			<form:select path="dateFlag" id="dateFlag" class="input-small">
				<form:options items="${fns:getDictList('report_filter_condition')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
	
			<%-- 开始日期 --%>
			<label><spring:message code="common.startDate" />：</label>
			<input id="createTimeStart" name="createTimeStart" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			<%-- 结束日期 --%>
			<label><spring:message code="common.endDate" />：</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			<%-- 查询按钮 --%>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button"
				value="<spring:message code='common.search'/>" />
			&nbsp;<input id="btnExport" class="btn btn-red" type="button"
				value="<spring:message code='common.export'/>" />
		</form:form>
	</div>
	<div style="overflow-y: auto; height: 600px;">
		<div class="row" style="margin-left: 50px;margin-top: 50px">
			<div id="mainline" style="width: 80%; height: 400px;"></div>
		</div>
		<div class="row" style="margin-left: 50px;margin-top: 50px">
			<div id="inbar" style="width: 80%; height: 500px; float: left;"></div>
		</div>
		<div class="row" style="margin-left: 50px;margin-top: 50px">
			<div id="outbar" style="width: 80%; height: 500px; float: left;"></div>
		</div>
	</div>

</body>
</html>