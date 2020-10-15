<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 网点预约现金统计图 -->
	<title><spring:message code="report.point.order.chart"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
	
		var showToolBox = true;
		if($.browser.msie){
			if($.browser.version <= 8){
				showToolBox = false;
			}
		}
	
		// 折线图
		var lineChart;
		// 堆叠柱状图
		var stackColumn;
		// 柱状图
		var histogram;
		// 柱状图
		var histogramTemp;
		$(document).ready(function() {
			// 报表初始化
			lineChart = echarts.init(document.getElementById('mainline'),
			'shine');
		    stackColumn = echarts.init(document.getElementById('stackColumn'),
		    'shine');
		    histogram = echarts.init(document.getElementById('histogram'),
		    'shine');
		    histogramTemp = echarts.init(document.getElementById('histogramTemp'),
		    'shine');
		    // 查询数据
		    searchDataAll()
			
			$("#btnSubmit").click(function(){
				// 查询数据
				searchDataAll();
			});
	         //导出表单
			 $("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/AllocateReportController/exportCashAllocateReport");
				$("#searchForm").submit();
			}); 
		});
		// 指定折线图的配置项和数据
		var lineChartOption = {
			title : {
				// 网点常规预约业务量统计图
				text : '<spring:message code='report.point.rule.business'/><spring:message code='report.chart'/>'
			},
			tooltip : {
				trigger : 'axis'
			},
			legend : {
				 type:'scroll',
				 orient : 'vertical',
				 right : 10,
				 bottom: 10,
				 top: 40,
				// align : 'right',
				 data : []
			},
			grid : {
				left : '3%',
				right : '25%',
				bottom : '3%',
				containLabel : true
			},
		    toolbox: {
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
			        feature : {
			            dataZoom : {
			                show : true,
			                title : {
			                    dataZoom : '<spring:message code='report.zone.scaling'/>',
			                    dataZoomReset : '<spring:message code='report.zone.scaling.back'/>'
			                }
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
			                    table += '</tbody></table>';
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
			                show : true,
			                // 保存为图片
			                title : '<spring:message code='report.save.as.picture'/>',
			                type : 'jpeg',
			                // 点击本地保存
			                lang : ['<spring:message code='report.click.save'/>'] 
			            }
			        }
			    },
			xAxis : {
				type : 'category',
				boundaryGap : true,
				data : []
			},
			yAxis : {
				// 业务量(次)
				name:'<spring:message code='report.business.degree'/>',
				type : 'value'
			}
		};
		
		var histogramOption = {
				title : {
					// 常规线路库房审批金额统计图
					text : '<spring:message code='report.store.approve.money.rule'/><spring:message code='report.chart'/>'
				}, tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			    	type:'scroll',
			    	//align : 'right',
			    	orient : 'vertical',
					right : 10,
					bottom: 10,
					top: 40,

					data : []
			    },
			    grid: {
			        left: '3%',
			        right: '25%',
			        bottom: '3%',
			        containLabel: true
			    },
			    toolbox: {
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
			        feature : {
			            dataZoom : {
			                show : true,
			                title : {
			                    dataZoom : '<spring:message code='report.zone.scaling'/>',
			                    dataZoomReset : '<spring:message code='report.zone.scaling.back'/>'
			                }
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
			                    table += '</tbody></table>';
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
			                show : true,
			                // 保存为图片
			                title : '<spring:message code='report.save.as.picture'/>',
			                type : 'jpeg',
			                // 点击本地保存
			                lang : ['<spring:message code='report.click.save'/>'] 
			            }
			        }
			    },
			    yAxis: {
				    name:'<spring:message code='report.money.element'/>',
			        type: 'value'
			    },
			     xAxis: {
			    	 type : 'category',
					 boundaryGap : true,
					 data : []
			    }
		};
		
		var histogramTempOption = {
				title : {
					// 临时线路库房审批金额统计图
					text : '<spring:message code='report.store.approve.money.temp'/><spring:message code='report.chart'/>'
				}, tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			    	type:'scroll',
			    	//align : 'right',
			    	orient : 'vertical',
					right : 10,
					bottom: 10,
					top: 40,

					data : []
			    },
			    grid: {
			        left: '3%',
			        right: '25%',
			        bottom: '3%',
			        containLabel: true
			    },
			    toolbox: {
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
			        feature : {
			            dataZoom : {
			                show : true,
			                title : {
			                    dataZoom : '<spring:message code='report.zone.scaling'/>',
			                    dataZoomReset : '<spring:message code='report.zone.scaling.back'/>'
			                }
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
			                    table += '</tbody></table>';
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
			                show : true,
			                // 保存为图片
			                title : '<spring:message code='report.save.as.picture'/>',
			                type : 'jpeg',
			                // 点击本地保存
			                lang : ['<spring:message code='report.click.save'/>'] 
			            }
			        }
			    },
			    yAxis: {
				    name:'<spring:message code='report.money.element'/>',
			        type: 'value'
			    },
			     xAxis: {
			    	 type : 'category',
					 boundaryGap : true,
					 data : []
			    }
		};
		
		var StackColumnOption = {
				title : {
					// 常规/临时业务量统计图
					text : '<spring:message code='report.ruleOrtemporary.business'/><spring:message code='report.chart'/>'
				},
				tooltip : {
					trigger : 'axis'
				},
			    angleAxis: {
			    },
			    radiusAxis: {
			        type: 'category',
			        data: [],
			        z: 10
			    },
			    polar: {
			    },
			    legend: {
			    	type:'scroll',
					orient : 'vertical',
					right : 10,
					bottom: 10,
					top: 40,
					//align : 'right',
					data : [],
			    },
			    toolbox: {
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
			        feature : {
			            dataZoom : {
			                show : true,
			                title : {
			                    dataZoom : '<spring:message code='report.zone.scaling'/>',
			                    dataZoomReset : '<spring:message code='report.zone.scaling.back'/>'
			                }
			            },
			            dataView : {
			                show : true,
			                // 数据视图
			                title : '<spring:message code='report.data.view'/>',
			                readOnly: true,
			                // 数据视图关闭及刷新
			                lang : ['<spring:message code='report.data.view'/>', '<spring:message code='common.close'/>', '<spring:message code='common.refresh'/>'],
			                optionToContent: function(opt) {
			                    var axisData = opt.radiusAxis[0].data;
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
			                    table += '</tbody></table>';
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
			                show : true,
			                // 保存为图片
			                title : '<spring:message code='report.save.as.picture'/>',
			                type : 'jpeg',
			                // 点击本地保存
			                lang : ['<spring:message code='report.click.save'/>'] 
			            }
			        }
			    }
			};
		
		// 查询折线图
		function searchData() {
			var bRtn = false;
			lineChart.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/AllocateReportController/graphicalFoldLine',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'officeId':$("#officeIdId").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					lineChart.hideLoading();
					lineChart.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						$("#btnExport").hide();
						bRtn = false;
					}else{
						$("#btnExport").show();
						lineChart.setOption(lineChartOption);
						lineChart.setOption({
							legend : {
								data : res.legendDataList
							},
							xAxis : {
								name:res.name,
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
		// 查询常规金额柱状图
		function searchDataHis(){
			histogram.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/AllocateReportController/graphicalColumn',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'officeId':$("#officeIdId").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					histogram.hideLoading();
					histogram.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						$("#btnExport").hide();
					}else{
						$("#btnExport").show();
						histogram.setOption(histogramOption);
						histogram.setOption({
							legend : {
								data : res.legendDataList
							},
							xAxis : {
								name:res.name,
								data : res.xAxisDataList
							},
							series : res.seriesDataList
						});
				    }
				},
				error : function() {
					return null;
				}
			});
		}
		
		// 查询临时金额柱状图
		function searchDataTempHis(){
			histogramTemp.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/AllocateReportController/graphicalColumnTemp',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'officeId':$("#officeIdId").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					histogramTemp.hideLoading();
					histogramTemp.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						$("#btnExport").hide();
					}else{
						$("#btnExport").show();
						histogramTemp.setOption(histogramTempOption);
						histogramTemp.setOption({
							legend : {
								data : res.legendDataList
							},
							xAxis : {
								name:res.name,
								data : res.xAxisDataList
							},
							series : res.seriesDataList
						});
				    }
				},
				error : function() {
					return null;
				}
			});
		}
		// 查询折叠柱状图
		function searchDataSta(){
			stackColumn.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/AllocateReportController/graphicalStackColumn',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'officeId':$("#officeIdId").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					stackColumn.hideLoading();
					stackColumn.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						$("#btnExport").hide();
					}else{
						$("#btnExport").show();
						stackColumn.setOption(StackColumnOption);
						stackColumn.setOption({
							legend : {
								data : res.legendDataList
							},
							radiusAxis : {
								data : res.xAxisDataList
							},
							series : res.seriesDataList
						});
				    }
				},
				error : function() {
					return null;
				}
			});
		}
		
		function searchDataAll() {
			var bRtn = false;
			bRtn = searchData();
			if (bRtn) {
				searchDataHis();
				searchDataSta();
				searchDataTempHis();
			}else{
				histogram.clear();
				stackColumn.clear();
				histogramTemp.clear();
				alertx("<spring:message code='message.E1067' />");
			}
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <!-- 网点预约现金统计图 -->
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.point.order.chart"/></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="reportCondition" action="${ctx}/report/v01/AllocateReportController" method="post" class="breadcrumb form-search">
			<!-- 选择机构 -->
			<c:if test ="${fns:getUser().office.type == '7' }">
				<label class="control-label"><spring:message code='common.institutionName'/>：</label>
				<sys:treeselect id="officeId" name="officeId" value="${reportCondition.officeId}" labelName="office.name" labelValue="${latticePointHandin.office.name}" title="机构"
				url="/sys/office/treeData" allowClear="true" type= "3"  isAll="true" notAllowSelectRoot="false" notAllowSelectParent="false" cssClass="required input-medium"/>
			</c:if>
			<!-- 过滤条件 -->
			<label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small">
				
			<form:options items="${fns:getDictList('report_filter_condition')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
			<label><spring:message code="common.startDate"/>：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="<fmt:formatDate value="${latticePointHandin.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<label><spring:message code="common.endDate"/>：</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${latticePointHandin.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			<!-- 查询按钮 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
			<!-- 导出按钮 -->
			&nbsp;<input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.export'/>"/>
		</form:form>
	</div>
	<div style="overflow-y: auto; height: 600px;">
		<!-- 业务量折线图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 总业务量堆叠柱状图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="stackColumn" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 常规线路审批金额柱状图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="histogram" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 临时线路审批金额柱状图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="histogramTemp" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
    </div>
</body>
</html>
