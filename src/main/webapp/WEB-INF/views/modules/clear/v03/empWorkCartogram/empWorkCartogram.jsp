<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 员工工作量统计图 -->
	<title><spring:message code="clear.report.empWorkCartogram"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
		
	//清分类型统计
	 var mainline;
	//员工工作量统计
	 var histogram;
		$(document).ready(function() {
			
			mainline = echarts.init(document.getElementById('mainline'),
			'shine');
			histogram = echarts.init(document.getElementById('histogram'),
			'shine');
			//mainline.setOption(mainlineOption);
			//searchData();
			judgeSearchData();
			$("#btnSubmit").click(function(){
				//searchData();
				judgeSearchData();
			});

		});
		//根据清分类型 统计
		var mainlineOption = {
			    title : {},
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    legend: {
			        orient : 'vertical',
			        x : 'left',
			        data:[]
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, 
			            	// 数据视图
			                title : '<spring:message code='report.data.view'/>',
			            	readOnly: true,
			            	// 数据视图关闭及刷新
			            	lang : ['<spring:message code='report.data.view'/>', '<spring:message code='common.close'/>', '<spring:message code='common.refresh'/>'],
			            	optionToContent: function(opt) {
			                    // 获取series
			            		var series = opt.series;
			                    // 获取series中data的长度（数据量）
			                    var seriesLength = series[0].data.length;
			                    var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
	                                 + '<td><spring:message code='clear.task.positionType'/></td>' 
	                                 + '<td><spring:message code='clear.public.quantity'/></td></tr>';
	                                 for(var i = 0;i < seriesLength; i++){
	                                	// 判断三种工位类型是否都存在数据
	                                	if(series[0].data[i].value != undefined){
				                       		table += '<tr>' + '<td>' + series[0].data[i].name + '</td>';
						                  	table = table + '<td style="text-align:right;">' + series[0].data[i].value + '</td></tr>';
	                                	}
	                                 }
			                    table += '</tbody></table>';
			                    return table;
			                }
			            },
			            magicType : {
			                show: true, 
			                type: ['pie', 'funnel'],
			                option: {
			                    funnel: {
			                        x: '25%',
			                        width: '55%',
			                        funnelAlign: 'left',
			                        max: 1548
			                    }
			                }
			            },
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    calculable : true,
			    series : []
			};
			                    
function searchData() {
			
			var bRtn = false;
			//查询折线图
			mainline.showLoading();
			$.ajax({
				url : '${ctx}/clear/v03/empWorkCartogram/graphicalHandInList',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'operateTimeStart' : $("#operateTimeStart").val(),
						'operateTimeEnd' : $("#operateTimeEnd").val(),
						/* 'filterCondition' : $("#filterCondition").val() */
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					mainline.hideLoading();
					mainline.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						bRtn = false;
					}else{
						mainline.setOption(mainlineOption);
						mainline.setOption({
							title : {
						        text: '根据工位类型统计',
						        subtext:'',
						        x:'center'
						    },
							legend: {
						        orient : 'vertical',
						        x : 'left',
						        data:['机械清分','手工清分','复点']
						    }, 
							series :[{
								 	name:'工位类型及对应工作量',
						            type:'pie',
						       	 /*修改显示饼图大小  wzj 2017-11-3 start*/
						            radius : '55%',
						         /*    end */
						            center: ['50%', '60%'],
						            data:[{
						            		value : res.seriesDataList[0].mechanicsClear,
											name : '机械清分'
										},
											{
											value : res.seriesDataList[0].manualClear,
											name : '手工清分',
										},
											{
											value : res.seriesDataList[0].assemblyLineClear,
											name : '复点'
										}],
										itemStyle:{ 
								            normal:{ 
								                  label:{ 
								                    show: true, 
								                    formatter: '{b} : {c} ({d}%)' 
								                  }, 
								                  labelLine :{show:true} 
								                } 
								            },
							}]
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
		
//根据人员姓名 统计
var histogramOption = {
	    title : {},
	    tooltip : {},
	    legend: {
	        orient : 'vertical',
	        x : 'left',
	        data:[]
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {
	            	show: true, 
	            	// 数据视图
	                title : '<spring:message code='report.data.view'/>',
	            	readOnly: true,
	            	lang : ['<spring:message code='report.data.view'/>', '<spring:message code='common.close'/>', '<spring:message code='common.refresh'/>'],
	            	optionToContent: function(opt) {
		            		// 获取series
		            		var series = opt.series;
		                    // 获取series中data的长度（数据量）
		                    var seriesLength = series[0].data.length;
		                    var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
	                             + '<td><spring:message code='clear.empWorkStatistics.empName'/></td>' 
	                             + '<td><spring:message code='clear.public.quantity'/></td></tr>';
	                             for(var i = 0;i < seriesLength; i++){
		                       		table += '<tr>' + '<td>' + series[0].data[i].name + '</td>';
				                  	table = table + '<td style="text-align:right;">' + series[0].data[i].value + '</td></tr>';
	                             }
		                    table += '</tbody></table>';
		                    return table;
	               		}
	            	},
	            magicType : {
	                show: true, 
	                type: ['pie', 'funnel'],
	                option: {
	                    funnel: {
	                        x: '25%',
	                        width: '50%',
	                        funnelAlign: 'left',
	                        max: 1548
	                    }
	                }
	            },
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    calculable : true,
	    series : []
	};
	
function searchDataHistogram() {
	
	var bRtn = false;
	//查询折线图
	histogram.showLoading();
	$.ajax({
		url : '${ctx}/clear/v03/empWorkCartogram/peopleHandInList',
		type : 'Post',
		contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
		data : {
				'operateTimeStart' : $("#operateTimeStart").val(),
				'operateTimeEnd' : $("#operateTimeEnd").val(), 
				/* 'filterCondition' : $("#filterCondition").val() */
			},
		dataType : 'json',
		async : false,
		cache : false,
		success : function(res) {
			histogram.hideLoading();
			histogram.clear();
			
			var workLength = res.workListTotal.length;
			var peopleLength = res.seriesDataList.length;
			var peopleName = new Array();
			//遍历出工作的人员
			for(var i = 0;i < peopleLength; i++){
				var dataName = res.seriesDataList[i].empName;
				peopleName.push(dataName);
			}
			//遍历出人员姓名及对应的工作量
			var arrays = new Array();
			for(var j = 0; j < peopleLength; j++){
			    arrays[j] = {
			        value:res.seriesDataList[j].totalCount,
			        name:res.seriesDataList[j].empName
			    }
			}
			
			// 填入数据
			if (res.seriesDataList.length == 0) {
				bRtn = false;
			}else{
				histogram.setOption(histogramOption);
				histogram.setOption({
					title : {
				        text: '根据员工姓名统计',
				        subtext:'',
				        x:'center'
				    },
				    tooltip : {
				        trigger: 'item',
				        formatter: function(params){
				        	var text = "工位类型数据统计：";
					        	for(var i = 0; i < workLength; i++){
					        		//判断对应员工的姓名
					        		if(params.name == res.workListTotal[i].empName){
					        		text += '<br/>'+'机械清分'+' : '+res.workListTotal[i].j1;
					        		text += '<br/>'+'手工清分'+' : '+res.workListTotal[i].s1;
					        		text += '<br/>'+'复点'+' : '+res.workListTotal[i].f1;
					        		}
				        	}
				        	return text;
				        }
				    },
					legend: {
				        orient : 'vertical',
				        x : 'left',
				        data:peopleName
				    }, 
					series :[{
						 	name:'清分人员姓名及对应工作量',
				            type:'pie',
							 /*修改显示饼图大小  wzj 2017-11-3 start*/
				            radius : '55%',
				          /*   end */
				            center: ['50%', '60%'],
				            data:arrays,
				            itemStyle:{ 
				                normal:{ 
				                      label:{ 
				                        show: true, 
				                        formatter: '{b} : {c} ({d}%)' 
				                      }, 
				                      labelLine :{show:true} 
				                    } 
				                }
					}]
						
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
	mainline.clear();
	histogram.clear();
	var bRtn = searchData();
	var aRtn = searchDataHistogram();
	if(bRtn){
		//查询根据清分类型统计的数据
		searchData();
	}
	if(aRtn){
		//查询根据人员姓名统计的数据
		searchDataHistogram();
	}
	if(!bRtn && !aRtn){
		alertx("<spring:message code='message.E1067' />");
	}
	if(!bRtn && aRtn){
		alertx("<spring:message code='message.E1078' />");
	}
	if(!aRtn && bRtn){
		alertx("<spring:message code='message.E1079' />");
	}
}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <!-- 员工工作量统计图 -->
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.report.empWorkCartogram"/></a></li>
	</ul>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="empWorkStatistics" action="${ctx}/clear/v03/empWorkCartogram/" method="post" class="breadcrumb form-search">
			<!-- 选择机构 -->
			<%-- <c:if test ="${fns:getUser().office.type == '7' }">
				<label class="control-label"><spring:message code='common.institutionName'/>：</label>
				<sys:treeselect id="officeId" name="office.id" value="${reportCondition.officeId}" labelName="office.name" labelValue="${latticePointHandin.office.name}" title="机构"
				url="/sys/office/treeData" allowClear="true" type= "3"  isAll="true" notAllowSelectRoot="false" notAllowSelectParent="false" cssClass="required input-medium"/>
			</c:if> --%>
			<!-- 过滤条件 -->
			<%-- <label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small" >
				<form:options items="${fns:getDictList('report_filter_condition')}" 
						itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select> --%>
			<!-- 开始时间 -->
			<label><spring:message code="common.startDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="operateTimeStart"  name="operateTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="<fmt:formatDate value="${empWorkStatistics.operateTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'operateTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- end -->
			<!-- 结束时间 -->
			<label><spring:message code="common.endDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-16 begin -->
			<input id="operateTimeEnd" name="operateTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${empWorkStatistics.operateTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'operateTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			<!-- end -->
			<!-- 查询按钮 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
		</form:form>
	</div>	
	<div style="overflow-y: auto; height: 700px;">
		<!-- 清分类型统计图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	    <!-- 员工工作量统计图 -->
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="histogram" style="width: 80%;height:400px;"></div>
	        </div>
	    </div>
	</div>
</body>
</html>
