<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.cashHandinReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
	//上缴业务量
	 var lineChart;
	//上缴总金额
	 var histogram;
	//上缴各物品金额
	 var histogramGoods;
	
	 var showToolBox = true;
	 if($.browser.msie){
		if($.browser.version <= 8){
			showToolBox = false;
		}
	 }
		$(document).ready(function() {
			
			lineChart = echarts.init(document.getElementById('mainline'),
			'shine');
			histogram = echarts.init(document.getElementById('histogram'),
			'shine');
			histogramGoods = echarts.init(document.getElementById('histogramGoods'),
			'shine');
			//查询业务量
			//searchData();
			judgeSearchData();
			$("#btnSubmit").click(function(){
				//searchData();
				judgeSearchData();
			});
	         //导出业务量表单
			 $("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/handinController/exportCashHandinReport");
				$("#searchForm").submit();
			}); 
		});
		// 指定图表的配置项和数据：业务量
		var lineChartOption = {
			title : {
				text : "<spring:message code='allocation.cash.handinPoint'/><spring:message code='allocation.cash.handinDegree'/>"
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

				//align : 'right',
				data : []
			},
			grid : {
				left : '5.4%',
				right : '25%',
				bottom : '3%',
				containLabel : true
			},
			toolbox : {
				show : showToolBox,
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
						show : true
					}
				},right:'3%'
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

		function searchData() {
			
			var bRtn = false;
			//查询折线图
			lineChart.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/handinController/graphicalHandInList',
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
						$('#btnExport').hide();
						bRtn = false;
					}else{
						$('#btnExport').show();
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
	
		var histogramOption = {
				title : {
					text : "<spring:message code='allocation.cash.handinPoint'/><spring:message code='allocation.cash.handinCount'/>"
				}, tooltip : {
			        trigger: 'axis'
			    },
			    toolbox : {
					show : showToolBox,
					feature : { 
						 dataZoom: {
				                yAxisIndex: 'none'
				            },  dataView : {
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
				                    	table = table + '<td>' + series[seriesIndex].name   + '</td>';
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
							show : true
						}
					},right:'3%'
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
			        left: '1%',
			        right: '25%',
			        bottom: '3%',
			        containLabel: true
			    },
			   yAxis: {
				   name:'<spring:message code = "report.money.element"/>',
			        type: 'value'
			    },
			     xAxis: {
			    	 type : 'category',
					 boundaryGap : true,
					 data : []
			    }
			   
		}
		
		function searchDataHis(){
			var bRtn = false;
			histogram.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/handinController/graphicalHistogram',
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
					if (res.seriesDataList.length == 0) {
						$('#btnExport').hide();
						bRtn = false;
					}else{
						$('#btnExportCount').show();
						histogram.setOption(histogramOption);
						// 填入数据
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
						bRtn = true;
					}
					
				},
				error : function() {
					bRtn = false;
				}
			});
			
			return bRtn;
		}
		/* 各物品金额 */
		var histogramGoodsOption = {
				title : {
					text : "<spring:message code='allocation.cash.handinPoint'/><spring:message code='allocation.cash.handinGoods'/>"
				}, tooltip : {
			        trigger: 'axis'
			    },
			    toolbox : {
					show : showToolBox,
					feature : {
						 dataZoom: {
				                yAxisIndex: 'none'
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
				                    	table = table + '<td>' + series[seriesIndex].name   + '</td>';
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
							show : true
						}
					},right:'3%'
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
			        left: '1%',
			        right: '25%',
			        bottom: '3%',
			        containLabel: true
			    },
			   yAxis: {
				   name:'<spring:message code = "report.money.element"/>',
			        type: 'value'
			    },
			     xAxis: {
			    	 type : 'category',
					 boundaryGap : true,
					 data : []
			    }
			   
		}
		function searchDatahistogramGoods(){
			
			var bRtn = false;
			
			histogramGoods.showLoading();
			$.ajax({
				url : '${ctx}/report/v01/handinController/graphicalHistogramGoods',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
					    'officeId':$("#officeIdId").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false, // false同步
				cache : false,
				success : function(res) {
					histogramGoods.hideLoading();
					histogramGoods.clear();
					if (res.seriesDataList.length == 0) {
						$('#btnExport').hide();
						bRtn = false;
					}else{
						$('#btnExportMoneyAmount').show();
						histogramGoods.setOption(histogramGoodsOption);
						// 填入数据
						histogramGoods.setOption({
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
		function judgeSearchData(){
			histogram.clear();
			histogramGoods.clear();
			var bRtn = searchData();
			if(bRtn){
				//查询总金额
				searchDataHis();
				//查询物品及金额
				searchDatahistogramGoods();
			}
			if(!bRtn){
				alertx("<spring:message code='message.E1067' />");
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 网点现金上缴报表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.cashHandinReport" /></a></li>
	</ul>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="reportCondition" action="${ctx}/allocation/v01/handinController/" method="post" class="breadcrumb form-search">
			<!-- 选择机构 -->
			<c:if test ="${fns:getUser().office.type == '7' }">
				<label class="control-label"><spring:message code='common.institutionName'/>：</label>
				<sys:treeselect id="officeId" name="office.id" value="${reportCondition.officeId}" labelName="office.name" labelValue="${latticePointHandin.office.name}" title="机构"
				url="/sys/office/treeData" allowClear="true" type= "3"  isAll="true" notAllowSelectRoot="false" notAllowSelectParent="false" cssClass="required input-medium"/>
			</c:if>
			<!-- 过滤条件 -->
			<label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small" >
				<form:options items="${fns:getDictList('report_filter_condition')}" 
						itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
			<!-- 开始时间 -->
			<label><spring:message code="common.startDate"/>：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="<fmt:formatDate value="${reportCondition.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- 结束时间 -->
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
		<!-- 折线图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 柱状图 -->
	    <!-- 上缴总金额 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="histogram" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 上缴个物品金额 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="histogramGoods" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	</div>
</body>
</html>
