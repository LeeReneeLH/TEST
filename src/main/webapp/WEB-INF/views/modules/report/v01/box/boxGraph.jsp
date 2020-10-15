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
<script type="text/javascript">
	var showToolBox = true;
	if($.browser.msie){
		if($.browser.version <= 8){
			showToolBox = false;
		}
	}
	
	var barTypeChart;
	var barStatusChart;
	$(document).ready(function() {
		//初始化箱袋库存柱图
		barTypeChart = echarts.init(document.getElementById('mainBarNum'),
		'shine');
		
		//初始化箱袋状态图
		barStatusChart = echarts.init(document.getElementById('mainBarStats'),
		'shine');
		
		searchData();
			
		$("#btnSubmit").click(function(){
			searchData();
		});
		
		$("#btnExport").click(function(){
			//alert($("#officeIdId").val());
			$("#searchForm").attr("action", "${ctx}/report/v01/box/exportBoxGraphDate?office.id=" + $("#officeIdId").val());
			$("#searchForm").submit();
			
		});
	});
	// 指定箱袋库存柱图的配置项和数据
	var barTypeChartOption = {
		title : {
			text : "<spring:message code='report.box.type'/>"
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			type:'scroll',
			orient : 'vertical',
			right : 20,
			bottom: 30,
			top: 60,
			//align : 'right',
			data : []
		},
		grid : {
			left : '3%',
			right : '30%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
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
                            + '<td>机构</td>';
		                    for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
		                    	table = table + '<td>' + series[seriesIndex].name + '</td>';
		                    }
		                    table = table + '</tr>';
		                    for (var i = 0; i < axisData.length; i++) {
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
			name:'<spring:message code='report.num.element'/>',
			type : 'value'
		}
		
			};
	
	// 指定箱袋状态柱图的配置项和数据
	var barStatusChartOption = {
		title : {
			text : "<spring:message code='report.box.status'/>"
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			type:'scroll',
			orient : 'vertical',
			right : 20,
			bottom: 30,
			top: 60,
			//align : 'right',
			data : []
		},
		grid : {
			left : '3%',
			right : '30%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
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
                            + '<td>机构</td>';
		                    for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
		                    	table = table + '<td>' + series[seriesIndex].name + '</td>';
		                    }
		                    table = table + '</tr>';
		                    for (var i = 0; i < axisData.length; i++) {
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
			name:'<spring:message code='report.num.element'/>',
			type : 'value'
		}
		
			};
	
     //箱袋库存查询
	function searchTypeData() {
		var boo=false;
		barTypeChart.showLoading();
		$.ajax({
			url : '${ctx}/report/v01/box/getBoxTypeGraphData',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {'office.id':$("#officeIdId").val()},
				
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				
				barTypeChart.hideLoading();
				barTypeChart.clear();
				
				if (res.seriesDataList.length == 0) {
					
					$("#btnExport").hide();
					
					//alertx("<spring:message code='message.E1067'/>");
				} else {
					
					$("#btnExport").show();
					barTypeChart.setOption(barTypeChartOption);
					// 填入数据
					barTypeChart.setOption({
						yAxis : {
							type : 'value'
						},
						legend : {
							data : res.legendDataList
						},
						xAxis : {
							data : res.xAxisDataList
						},
						series : res.seriesDataList
					});
					
					boo=true;
				}
				
			},
			error : function() {
				return null;
			}
		});
		
		return boo;
	}
	
	//箱袋状态查询
	function searchStatusData() {
		
		barStatusChart.showLoading();
		$.ajax({
			url : '${ctx}/report/v01/box/getBoxStatusGraphData',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {'office.id':$("#officeIdId").val()},
				
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				
				barStatusChart.hideLoading();
				barStatusChart.clear();
				
				if (res.seriesDataList.length == 0) {
					
					$("#btnExport").hide();
					
				} else {
					
					$("#btnExport").show();
					barStatusChart.setOption(barStatusChartOption);
					// 填入数据
					barStatusChart.setOption({
						yAxis : {
							type : 'value'
						},
						legend : {
							data : res.legendDataList
						},
						xAxis : {
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
	
	function searchData() {
		
		
		var bRtn=false;
		bRtn = searchTypeData();
		if (bRtn) {
			searchStatusData();
		}else{
			
			barTypeChart.clear();
			barStatusChart.clear();
			alertx("<spring:message code='message.E1067' />");
		}
	}
	
</script>
</head>
<body>

    <ul class="nav nav-tabs">
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='report.box.graph'/></a></li>
	</ul>
   
    <div class="row">
		<form:form id="searchForm"  modelAttribute="stoBoxInfoGraphEntity"
			method="post" class="breadcrumb form-search">
			<div class="row">
				<div class="span14" style="margin-top: 5px">
					<c:if test="${fns:getUser().office.type != '3'}"> 
						<%-- 归属机构 --%>
						<label><spring:message code='common.institutionName'/>：</label>
						<sys:treeselect id="officeId" name="officeId.id"
							value="${stoBoxInfoGraphEntity.office.id}" labelName="office.name"
							labelValue="${stoBoxInfoGraphEntity.office.name}" title="<spring:message code='common.office'/>"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" type='3'
							isAll="false" allowClear="true" />
					</c:if> 
				</div>	
			<div class="span2">
			<%-- 查询 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button" style="margin-top: 5px;" value="<spring:message code='common.search'/>" />
			
				<input id="btnExport" class="btn btn-red" type="submit" style="margin-top: 5px;" value="<spring:message code='common.export'/>"/>
			</div>
			</div>
		</form:form>
	</div>
	<div style="overflow-y: auto; height: 600px;">
		<!-- 箱袋库存柱图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainBarNum" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    
	    <!-- 箱袋状态图 -->
	     <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainBarStats" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
    </div>
</html>
