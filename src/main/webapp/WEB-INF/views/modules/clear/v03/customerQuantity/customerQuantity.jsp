<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<!-- 客户清点量统计图 -->
<title><spring:message code="clear.report.customerStatistics" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
<!-- 引入 vintage 主题 -->
<script src="${ctxStatic}/echart3/theme/shine.js"></script>
<script type="text/javascript">
	//图表对象
	var quantity;
	//返回的结果集
	var results;
	//对应的图例
	var names;
	//总金额
	var totals;
	$(document).ready(function() {
		/*过滤条件选项默认显示日 wzj 2017-11-22 begin */ 
		$("#filterCondition").select2().get(0).selectedIndex=2;
		$("#filterCondition").select2().get(0).selectedIndex=2;
		/* end */
		 // 初始化图表标签
	    quantity = echarts.init(document.getElementById('customerQuantity'),'shine');    
			searchData();
		$("#btnSubmit").click(function(){
			searchData();
		});
		  //导出业务量表单
		 $("#btnExport").click(function(){
			$("#searchForm").attr("action", "${ctx}/clear/v03/customerQuantity/exportQuantityReport");
			$("#searchForm").submit();
		});
		//legend切换事件上进行total的重计算
		  quantity.on('legendselectchanged', function (params) {
		     var legends = params.selected;
		     var keys = Object.keys(legends);
		     var totals = reBuildTotal(legends,keys);
		 	//添加总金额
		     quantity.setOption({series:seriess(results,names,totals)}); 
		}); 
		//还原事件
		  quantity.on('restore', function (params) {
			//添加总金额
				 quantity.setOption({
					series : seriess(results, names, totals)
				});  
			});
	});
	
	/**
	发送ajax查询数据
	 */
	function searchData() {
		var boo = false;
		quantity.showLoading();
		$
				.ajax({
					url : '${ctx}/clear/v03/customerQuantity/getCustomerQuantity',
					type : 'Post',
					contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
					data : {
						'custNo' : $("#custNo").val(),
						'createTimeStart' : $("#createTimeStart").val(),
						'createTimeEnd' : $("#createTimeEnd").val(),
						'filterCondition' : $("#filterCondition").val(),
						'officeId': $("#officeId").val()

					},

					dataType : 'json',
					async : false,
					cache : false,
					success : function(result) {
						//隐藏loading
						quantity.hideLoading();
						//清空图表对象
						quantity.clear();
						//标签名称
						names = result.legendDataList;
						quantity
								.setOption(option = {
									tooltip : {
									   	trigger : 'axis',
									   	axisPointer : { // 坐标轴指示器，坐标轴触发有效
											type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
										},
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
									},
									//图例
									legend : {
										//设置为无法点击
										//selectedMode : false,
										data : names
									},
									//工具栏
									toolbox : {
										show : true,
										orient : 'horizontal',
										y : 'top', // 垂直安放位置，默认为全图顶端，可选为：
										color : [ '#1e90ff', '#22bb22',
												'#4b0082', '#d2691e' ],
										backgroundColor : 'rgba(0,0,0,0)', // 工具箱背景颜色
										borderColor : '#ccc', // 工具箱边框颜色
										borderWidth : 0, // 工具箱边框线宽，单位px，默认为0（无边框）
										padding : 5, // 工具箱内边距，单位px，默认各方向内边距为5，
										calculable : true,
										feature : {
											mark : {
												show : true
											},
											// dataZoom : {show: true},
											dataView : {
												show : true,
												// 数据视图
												title : '<spring:message code='report.data.view'/>',
												readOnly : true,
												lang : [
														'<spring:message code='report.data.view'/>',
														'<spring:message code='common.close'/>',
														'<spring:message code='common.refresh'/>' ],
												optionToContent : function(opt) {
													var axisData = opt.xAxis[0].data;
													var series = opt.series;
													var table = '<table style="width:100%;line-height: 30px;text-align:center;  white-space: nowrap;" class="table table-bordered"><tbody><tr>'
															+ '<td style="text-align:center;"><spring:message code='common.time'/></td>';
													for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
														table = table
																+ '<td style="text-align:center;">'
																+ series[seriesIndex].name
																+ '</td>';
													}
													table = table + '</tr>';
													for (var i = 0, l = axisData.length; i < l; i++) {
														table += '<tr>'
																+ '<td style="text-align:center;">'
																+ axisData[i]
																+ '</td>';
														for (var seriesIndex = 0; seriesIndex < series.length; seriesIndex++) {
															table = table
																	+ '<td style="text-align:right;">'
																	+ formatCurrencyFloat(series[seriesIndex].data[i])
																	+ '</td>';
														}
														table = table + '</tr>';
													}
													table += '</tbody></table>';
													return table;
												}
											},

											//  magicType : {show: true, type: ['line']},
											restore : {
												show : true,
											},
											saveAsImage : {
												show : true
											},
										},
										right : '3%'
									},
									//布局
									 grid : {
										left : '3%',
										right : '8%',
										bottom : '8%',
										containLabel : true
									}, 
									//滑动轴
									 dataZoom : {
										show : true,
										start : 75,
										end : 100
									}, 
									//X轴
									xAxis : [ {
										name : result.name,
										type : 'category',
										data : result.xAxisDataList
									} ],
									//Y轴
									yAxis : [ {

										name : '<spring:message code = "report.money.element"/>',
										type : 'value',
										axisLine : {
											show : true
										}
									} ],
									series : series(result, names)
								});
						//计算总计
						totals = buildTotal();
						quantity.setOption({
							series : seriess(result, names, totals)
						}); 
						results=result;
						boo = true;
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						if("error"!=textStatus){
							alert('您已登录超时或在其他地点登录，请重新登录！');
							top.location = "${ctx}";
							}
						return false;
					}
				});

		return boo;
	}
	
	/**
	第一次不加总计时构建的series对象
	 */
	function series(result, names) {
		var series = [];
		for (var i = 0; i < names.length; i++) {
			var serie = {
				name : names[i],
				type : 'bar',
				stack : 'sum',
				barWidth : '15%',
				data : result.seriesDataList[i]
			}
			series.push(serie);
		}

		return series;
	}

	/**
	重新构建series数组
	 */
	function seriess(result, names, totals) {
		var series = [];
		for (var i = 0; i < names.length; i++) {
			var serie = {
				name : names[i],
				type : 'bar',
				stack : 'sum',
				barWidth : '15%',
				data : result.seriesDataList[i]
			}
			series.push(serie);
		}
		var serie = buildTotalSerie(totals);
		series.push(serie);
		return series;
	}

	/**
	组织serices里面的数据变成想要的加总数组,就是按列求和,然后返回这个列求和的数组
	 */
	function buildTotal() {
		var series = quantity.getOption().series;
		var totalLength = series[0].data.length;
		var totals = [];
		for (var k = 0; k < totalLength; k++) {
			totals[k] = 0;
		}
		for (var i = 0; i < series.length; i++) {
			for (var j = 0; j < series[i].data.length; j++) {
				totals[j] +=Number(changeAmountToNum(series[i].data[j]));
				
			}
		}	
		return totals;
	}
	
	/**
	重新组织serices里面的数据变成想要的加总数组
	 */
	function reBuildTotal(legends, keys) {
		var series = quantity.getOption().series;
		var totalLength = series[0].data.length;
		var totals = [];
		for (var k = 0; k < totalLength; k++) {
			totals[k] = 0;
		}
		for (var i = 0; i < series.length - 1; i++) {
			var name = series[i].name;
			for (var j = 0; j < series[i].data.length; j++) {
				if (legends[keys[i]] === true) {
					totals[j] += Number(changeAmountToNum(series[i].data[j]));
				}
			}
		}
		return totals;
	}
	
	/**
	构建总计serie对象
	 */
	function buildTotalSerie(totals) {
		var isZero = isAllZero(totals);
		var serie = {};
		if (isZero === false) {
			serie = {
				name : "总计",
				type : 'bar',
				stack : 'sum',
				barWidth : '15%',
				itemStyle : {
					normal : {
						color : 'rgba(0,0,0,0)'
					}
				},
				label : {
					normal : {
						position : 'insideBottom',
						show : true,
						textStyle : {
							color : '#000',
							fontStyle : 'normal',
							fontSize : '14',
							fontWeight : 'bold'
						},
						formatter : function(params) {
							return "总计:" + formatCurrencyFloat(params.data);
						}
					}
				},
				data : totals
			};
		} else {
			serie = {
				name : "总计",
				type : 'bar',
				stack : 'sum',
				barWidth : '15%',
				itemStyle : {
					normal : {
						color : 'rgba(0,0,0,0)'
					}
				},
				label : {
					normal : {
						position : 'insideBottom',
						textStyle : {
							color : '#000',
							fontStyle : 'normal',
							fontSize : '14',
							fontWeight : 'bold'
						},
						formatter : function(params) {
							return "总计:" + formatCurrencyFloat(params.data);
						}
					},
				},
				data : totals
			};
		}
		return serie;
	}
	
	/**
	判断数组内所有数字全部为0,如果是返回true,如果不全是返回false
	 */
	function isAllZero(totals) {
		for (var i = 0; i < totals.length; i++) {
			if (totals[i] !== 0) {
				return false;
			}
		}
		return true;
	}

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
	/**
	  将金额转换成数字
	 */
	function changeAmountToNum(amount) {
		if (typeof (amount) != "undefined"&&amount != null) {
			amount = amount.toString().replace(/\$|\,/g, '');
		}
		return amount;
	}
</script>
</head>
<body>	
	<ul class="nav nav-tabs">
		<!-- 客户出入量 -->
		<li class="active"><a href="#"><spring:message code="clear.report.customerStatistics" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="customerClearance" action="${ctx}/clear/v03/customerQuantity/exportQuantityReport" method="post" class="breadcrumb form-search">
		 <c:choose>
     <c:when test="${fns:getUser().office.type eq '1'}">
     </c:when>
      <c:when test="${fns:getUser().office.type eq '3'}">
     </c:when>
     <c:otherwise>
		<!-- 客户名称 -->
		<label><spring:message code="clear.public.custName" />：</label>
			<form:select path="custNo" id="custNo"
					class="input-large required" disabled="disabled">
					<form:options items="${sto:getStoCustList('1,3',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
			</form:select>
			&nbsp;&nbsp;
	 </c:otherwise>
	 </c:choose>	
			<!-- 过滤条件 -->
			<label class="control-label"><spring:message code="common.filterCondition"/>：</label>
			<form:select path="filterCondition" id="filterCondition" class="input-small" >
				<form:options items="${fns:getDictList('report_filter_condition')}" 
						itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
			&nbsp;
			<!-- 开始时间 -->
			<label><spring:message code="common.startDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-15 begin -->
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="<fmt:formatDate value="${customerClearance.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- end -->
			&nbsp;
			<!-- 结束时间 -->
			<label><spring:message code="common.endDate"/>：</label>
			<!--  清分属性去除 wzj 2017-11-15 begin -->
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${customerClearance.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		<!-- 增加清分机构 qph 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${officeId}" labelName="office.name"
				labelValue="${officeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"	
				cssClass="input-medium"  isAll="${fns:getUser().office.type eq '3'?'true':'false'}"/>
		<!-- end -->
		&nbsp;&nbsp;
		<!-- end -->
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>" />&nbsp;
		<%-- <input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.export'/>"/> --%>
	</form:form>
	
	<div id="customerQuantity"class="span14" style="margin-top: 50px;width: 80%;height:500px;">
	</div>	
	
</html>
