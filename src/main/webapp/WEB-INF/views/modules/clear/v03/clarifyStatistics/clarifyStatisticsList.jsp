<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分中心统计图 -->
	<title><spring:message code="clear.report.clarifyStatistict"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
	<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
    <script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>
	<link rel = "stylesheet" type= "text/css" href = "${ctxStatic}/css/modules/report/v01/graph.css"/>
	<script type="text/javascript">
	//var clearAmount;
	//折线图
	var lineChart;
	$(document).ready(function (){
		
		/*过滤条件选项默认显示日 wzj 2017-11-22 begin */ 
		$("#filterCondition").select2().get(0).selectedIndex=2;
		$("#filterCondition").select2().get(0).selectedIndex=2;
		/* end */
		var mainline = document.getElementById('mainline');
		if(mainline != null){
			//报表初始化
		lineChart = echarts.init(document.getElementById('mainline'),
				'shine');
		//加载页面时查询数据
		searchData();
		}
		/* var clear = document.getElementById('clearAmount');
		if(clear != null){
			clearAmount = echarts.init(document.getElementById('clearAmount'),'shine');  */
			/* 加载页面时查询数据	 修改人：wxz 2017-11-27 */
		/* 	createGraph();
		} */
		
	
		/* end */
		//点击查询按钮
		$("#btnSubmit").click(function(){
			var mainline = document.getElementById('mainline');
			//var clear = document.getElementById('clearAmount');
			if(mainline != null){
			//加载页面时查询数据
				searchData();
			}
		/* 	if(clear != null){
				createGraph();
			} */
		});
		//点击导出按钮
		$("#btnExport").click(function(){
			$("#searchForm").attr("action", "${ctx}/clear/v03/ClarifyStatisticsCtrl/exportCashHandinReport");
			$("#searchForm").submit();
		});
	});
	var lineChartOption = {
		    tooltip : {
		        trigger: 'axis',
		        /* 增加千分位 wzj 2017-12-1 begin */
		      //设置hover显示的样式
			  	 formatter:function(params){
			  		var result = '';
	 				var res='<div><p>时间：'+params[0].name+'</p ></div>' 
	 			for(var i=0;i<params.length;i++){
	 				result = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
					res+='<p>'+result+params[i].seriesName+':'+formatCurrencyFloat(params[i].data)+'</p >'
		 		}
		 				return res;
				   } 
				/* end */
		    },
		    legend: {
		        data:[]
		    },
		    grid : {
				left : '3%',
				right : '20%',
				bottom : '10%',
				containLabel : true
			},
		    toolbox: {
		        show : true,
		        orient: 'horizontal',
		        feature : {
		            mark : {show: true},
		            dataView : {
		                show : true,
		                // 数据视图
		                title : '<spring:message code='report.data.view'/>',
		                readOnly: true,
		                // 数据视图
		                optionToContent: function(opt) {
		                	var axisData = opt.xAxis[0].data; 
		                    var series = opt.series;
		                    var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
                                 + '<td style="text-align:center;"><spring:message code='common.time'/></td>';
		                    for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
		                    	table = table + '<td style="text-align:center;">' + series[seriesIndex].name + '</td>';
		                    }
		                    table = table + '</tr>';
		                    for (var i = 0, l = axisData.length; i < l; i++) {
		                        table += '<tr>' + '<td style="text-align:center;">' + axisData[i] + '</td>';
                                for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
				                  	table = table + '<td style="text-align:right;">' + toThousands(series[seriesIndex].data[i])  + '</td>';
				                 }        
                                table = table + '</tr>';
		                    }
		                    table += '</tbody></table>';
		                    return table;
		                }
		            },
		            restore : {show: true},
		            saveAsImage : {show: true}
		        },right:'3%'
		    }, 
		    calculable : true,
		    xAxis : [
		        {
		            type : 'category',
		           	boundaryGap : false,
		            data : []
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
	
	
	// 查询折线图
		function searchData() {
		
			lineChart.clear();
			var bRtn = false;
			lineChart.showLoading();
			$.ajax({
				url : '${ctx}/clear/v03/ClarifyStatisticsCtrl/graphicalFoldLine',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				data : {
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(), 
						'filterCondition' : $("#filterCondition").val()
					},
				dataType : 'json',
				async : false,
				cache : false,
				success : function(res) {
					if(res.seriesDataList.length==3 && res.seriesDataList[0]=="" && res.seriesDataList[1]=="" && res.seriesDataList[2]==""){
						alertx("<spring:message code='message.E1067' />");
						$("#mainline").hide();
					}
					lineChart.hideLoading();
					lineChart.clear();
					// 填入数据
					if (res.seriesDataList.length == 0) {
						$("#btnExport").hide();
					}else{
						$("#btnExport").show();
						var dataZoom = [];
						if(res.xAxisDataList.length>8){
							 dataZoom = {
							        show : true,
							        start : 75,
							        end : 100
							    }
						}else{
							dataZoom = {
							        show : true,
							        start : 0,
							        end : 100
							    }
						}
						lineChart.setOption(lineChartOption);
						lineChart.setOption({
							legend : {
								 data:["收入","支出","余额"]
							},
							xAxis : {
								name:res.name,
								data : res.xAxisDataList
							},
							dataZoom:{
								show : true,
						        start : 70
							},
							yAxis : {
								
							},
							series : [
								 	{
							            name:'收入',
							            type:'line',
							            smooth:true,
							            data:res.seriesDataList[0]
							        },
							        {
							            name:'支出',
							            type:'line',
							            smooth:true,
							            data:res.seriesDataList[1]
							        },
							        {
							            name:'余额',
							            type:'line',
							            smooth:true,
							            data:res.seriesDataList[2]
							        }
							]
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
			return bRtn;
		}
		// 整数千分位overflow-y:scroll;
		function toThousands(num) {
			var newStr = "";
			var count = 0;
			var num = (num || 0).toString(), result = '';
			if (num.indexOf(".") == -1) {
				while (num.length > 3) {
					result = ',' + num.slice(-3) + result;
					num = num.slice(0, num.length - 3);
				}
				num = num;
			} else {
				for (var i = num.indexOf(".") - 1; i >= 0; i--) {
					if (count % 3 == 0 && count != 0) {
						newStr = num.charAt(i) + "," + newStr;
					} else {
						newStr = num.charAt(i) + newStr; //逐个字符相接起来
					}
					count++;
				}
				num = newStr + (num + "00").substr((num + "00").indexOf("."), 3);

			}
			if(num.indexOf(".") == -1){
				if (num) {
					result = num + result+ ".00";
				}
			}else{
				if (num) {
					result = num + result;
				}
			}
			
			return result;
		}
		
		/* 清分业务出入库总金额查询 	修改人:wxz	2017-11-27 */
		/* 修改人:sg 修改日期:2017-12-11 begin*/
	 /* 	function searchClearInOrOut() {
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
				error : function() {
					bRtn = false;
				}
			});
			return bRtn;
		}
	 	/* end */
		/* end */
		/* 使用Echart3创建数据图形		修改人：wxz  2017-11-27 */
		/* 修改人:sg 修改日期:2017-12-11 begin*/
		/*function createGraph() {
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
	  	     } */
	 	/* end */
		/* end */
		/* 增加千分位方法wzj 2017-12-1 begin */
		/**
	千分位用逗号分隔
	 */
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
		/* end */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <!-- 清分中心统计图 -->
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.report.clarifyStatistict"/></a></li>
	</ul>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="dayReportMain" action="${ctx}/report/v01/AllocateReportController" method="post" class="breadcrumb form-search">
			<!-- 过滤条件 -->
			<label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small">
			<form:options items="${fns:getDictList('report_filter_condition')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
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
			<!-- 导出按钮 -->
			&nbsp;<input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.export'/>"/>
		</form:form>
	</div>
	
	<div style="overflow-y: auto; height: 900px;">

		<!-- 业务量折线图 -->
		 <shiro:hasPermission name="report:clearcenter:user">
	    <div class="row" style="margin-left: 50px;margin-top: 50px">
	        <div>
	            <div class="span12" id="mainline" style="width: 85%;height:400px;"></div>
	        </div>
	    </div>
	 </shiro:hasPermission>
	    <!-- 清分业务出入库金额统计图   修改人：wxz 2017-11-27 begin -->
	    <!-- 修改人:sg 修改日期:2017-12-11 begin -->
		<!-- <div class="row" style="margin-left: 80px;margin-top: 50px">
			<div>
				<div id="clearAmount" class="span12" style="width: 85%;height:400px;"></div>
			</div>
		</div> -->
		<!-- end -->
		<!-- end -->
    </div>
</body>
</html>
