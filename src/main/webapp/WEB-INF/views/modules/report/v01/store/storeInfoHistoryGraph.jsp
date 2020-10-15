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
	var lineChart;
	$(document).ready(function() {
		
		lineChart = echarts.init(document.getElementById('mainline'),
		'shine');
		
		if(!searchData()){
			alertx("<spring:message code='message.E1067'/>")
		}
		
		$("#btnSubmit").click(function(){
			if(!searchData()){
				alertx("<spring:message code='message.E1067'/>")
			}
		
		});
		
		$("#btnExport").click(function(){
			$("#searchForm").attr("action", "${ctx}/report/v01/store/exportStoreInfoGraph?officeId=" + $("#officeIdId").val());
			$("#searchForm").submit();
			
		});
	});
	// 指定图表的配置项和数据
	var lineChartOption = {
		title : {
			text : "<spring:message code='report.history.goods'/>"
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
			name:'<spring:message code='report.money.element'/>',
			type : 'value'
		}
		
			};

	function searchData() {
		var boo=false;
		lineChart.showLoading();
		$.ajax({
			url : '${ctx}/report/v01/store/getGraphData',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {'officeId':$("#officeIdId").val(),
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
				
				if (res.seriesDataList.length == 0) {
					
					$("#btnExport").hide();
					
					//alertx("<spring:message code='message.E1067'/>");
				} else {
					
					$("#btnExport").show();
					lineChart.setOption(lineChartOption);
					// 填入数据
					lineChart.setOption({
						yAxis : {
							type : 'value'
						},
						legend : {
							data : res.legendDataList
						},
						xAxis : {
							name :res.name,
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
	
</script>
</head>
<body>

    <ul class="nav nav-tabs">
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='report.history.goods'/></a></li>
	</ul>
   
    <div class="row">
		<form:form id="searchForm"  modelAttribute="stoInfoReportEntity"
			method="post" class="breadcrumb form-search">
			<div class="row">
				<div class="span14" style="margin-top: 5px">
					<c:if test="${fns:getUser().office.type != '3'}">
						<%-- 归属机构 --%>
						<label><spring:message code='common.institutionName'/>：</label>
						<sys:treeselect id="officeId" name="office.id"
							value="${stoInfoReportEntity.office.id}" labelName="office.name"
							labelValue="${stoInfoReportEntity.office.name}" title="选择机构"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" type="3"
							isAll="false" allowClear="true" />
					</c:if>
					<%-- 过滤条件 --%>
					<label><spring:message code="common.filterCondition"/>：</label>
					<form:select path="filterCondition" id="filterCondition" class="input-small" >
						<form:options items="${fns:getDictList('report_filter_condition')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<div class="span7" style="margin-top: 5px">
					<%-- 登记日期 --%>
					<label><spring:message code="common.startDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="" 
						   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
					
					<label><spring:message code="common.endDate" />：</label>
					&nbsp;
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value=""
					       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
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
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
    </div>
</html>
