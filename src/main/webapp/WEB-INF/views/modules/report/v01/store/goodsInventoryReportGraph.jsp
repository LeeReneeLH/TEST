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
		
		lineChart = echarts.init(document.getElementById('mainbar'),
		'shine');
		
		//searchData();
		if(!searchData()){
			alertx("<spring:message code='message.E1067'/>")
		}
		
		$("#btnSubmit").click(function(){
			if(!searchData()){
				alertx("<spring:message code='message.E1067'/>")
			}
			//searchData();
		});
		
		$("#btnExport").on("click",function(){
			
			$("#searchForm").attr("action", "${ctx}/report/v01/store/exportReportGraph?office.id="+$("#officeIdId").val());
			$("#searchForm").submit();
		});
	});
	// 指定图表的配置项和数据
	var lineChartOption = {
		title : {
			text : "<spring:message code='report.stores.goods'/>"
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
		         
		            restore : {
		                show : true,
		                //还原
		                title : '<spring:message code='report.reduction'/>',
		                color : 'black'
		            },
		            saveAsImage : {
		                show : true,
		                //保存为图片
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
			name : '<spring:message code='report.money.element'/>',
			type : 'value'
		}
	};
	
	
	function searchData() {
		
		lineChart.showLoading();
		var boo=false;
		$.ajax({
			url : '${ctx}/report/v01/store/getReportGraphData',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			data : {'office.id':$("#officeIdId").val(),
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

				}else{
					$("#btnExport").show();
					lineChart.setOption(lineChartOption);
					// 填入数据
					lineChart.setOption({
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
	
</script>
</head>
<body>
   <ul class="nav nav-tabs">
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='report.stores.goods'/></a></li>
	</ul>
	
    <div class="row">
		<form:form id="searchForm"  modelAttribute="stoReportInfo"
			method="post" class="breadcrumb form-search">
			<div class="row">
				<div class="span14" style="margin-top: 5px">
				
					<c:if test="${fns:getUser().office.type != '3'}">
						<%-- 归属机构 --%>
						<label><spring:message code='common.institutionName'/>：</label>
						<sys:treeselect id="officeId" name="officeId"
							value="${stoReportInfo.office.id}" labelName="office.name"
							labelValue="${stoReportInfo.office.name}" title="<spring:message code='common.office'/>"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" type="3"
							isAll="false" allowClear="true" />
					</c:if>
				
				</div>
				<div class="span7" style="margin-top: 5px">
				<%-- 查询 --%>
				<c:if test="${fns:getUser().office.type != '3'}">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>" />
				</c:if>
				&nbsp;<input id="btnExport" class="btn btn-red" type="submit" value="<spring:message code='common.export'/>"/>
			</div>
			</div>
		</form:form>
	</div>
	<div style="overflow-y: auto; height: 600px;">
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainbar" style="width:80%;height:400px;"></div>
	        </div>
	    </div>
    </div>
</html>
