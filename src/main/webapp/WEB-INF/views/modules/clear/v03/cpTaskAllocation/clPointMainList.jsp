<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 任务分配列表 -->
	<title><spring:message code='clear.task.distribution.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
	     	// 打印全选
	        $("#allPrintCheckbox").click(function () {
	        	
	        	if ($(this).attr("checked") == "checked") {
	        		$(this).attr("checked", true);
	        	} else {
	        		$(this).attr("checked", false);
	        	}
	        	var checkStatus = $(this).attr("checked");
	            $("[id = chkPrintItem]:checkbox").each(function () {
	                $(this).attr("checked", checkStatus == "checked" ? true : false);
	            });
	        });
			
         	// 批量审批打印
			$("#btnPrint").click(function(){
				
				var result = new Array();
				$("[id = chkPrintItem]:checkbox").each(function () {
				    if ($(this).attr("checked") == "checked") {
				        result.push($(this).attr("value"));
				    }
				});
				
				var taskNos = result.join(",");
				if (taskNos == '') {
                	//[提示]：请选择打印流水单号！
                	alertx("<spring:message code='message.I2018' />");
					return;
				}
				
				var content = "iframe:${ctx}/clear/v03/cpTaskAllocation/batchPrint?taskNos=" + taskNos;
				top.$.jBox.open(
						content,
						//打印
						"<spring:message code='common.print' />", 900, 600, {
							buttons : {
								//打印
								"<spring:message code='common.print' />" : "ok",
								// 关闭
								"<spring:message code='common.close' />" : true
							},
							submit : function(v, h, f) {
								if (v == "ok") {
									var printDiv = h.find("iframe")[0].contentWindow.printDiv;
									$(printDiv).show();
									//打印 
									$(printDiv).jqprint();
									return false;
								}
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "auto");
							}
						});
				
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		// 打印明细
		function printDetail(taskNo){
			var content = "iframe:${ctx}/clear/v03/cpTaskAllocation/printClPoint?taskNo=" + taskNo;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 900, 600, {
						buttons : {
							//打印
							"<spring:message code='common.print' />" : "ok",
							// 关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var printDiv = h.find("iframe")[0].contentWindow.printDiv;
								$(printDiv).show();
								//打印 
								$(printDiv).jqprint();
								return false;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
					});
		}
		

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 任务分配列表 -->
		<li class="active"><a href="${ctx}/clear/v03/cpTaskAllocation/"><spring:message code='clear.task.distribution.list'/></a></li>
		<!-- 任务分配登记 -->
		<shiro:hasPermission name="clear:v03:cpTaskAllocation:edit"><li><a href="${ctx}/clear/v03/cpTaskAllocation/form"><spring:message code='clear.task.distribution.register'/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="clTaskMain"
		action="${ctx}/clear/v03/cpTaskAllocation/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart" name="createTimeStart" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${clTaskMain.createTimeStart}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${clTaskMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		<!-- 增加清分机构 wzj 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${clTaskMain.office.id}" labelName="office.name"
				labelValue="${clTaskMain.office.name}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		&nbsp;
		<!--end-->
	<!-- 查询 -->
	<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />

	</form:form>
	<!-- <div class="row" style="margin-left:2px"> -->
		<sys:message content="${message}"/>
		<shiro:hasPermission name="clear:v03:cpTaskAllocation:print">
		<!-- 批量打印 -->
		<input type="button" id="btnPrint" value="<spring:message code='common.batchPrint' />" class="btn btn-primary">
		</shiro:hasPermission>
	<!-- </div>-->
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
			<shiro:hasPermission name="clear:v03:cpTaskAllocation:print">
				<!-- 全打印 -->
				<th style="text-align: center;"><%-- <spring:message code='common.AllPrint' /> --%>
					<input type="checkbox" id="allPrintCheckbox"/></th>
					</shiro:hasPermission>
				<!-- 业务流水 -->
				<th><spring:message code='clear.task.business.id'/></th>
				<!-- 业务类型 -->
				<th><spring:message code='clear.task.business.type'/></th>
				<!-- 币种 -->
				<th><spring:message code='common.currency'/></th>
				<!-- 面值 -->
				<th><spring:message code='common.denomination'/></th>
				<!-- 计划类型 -->
				<th><spring:message code='clear.task.planType'/></th>
				<!-- 捆数 -->
				<th><spring:message code='clear.task.totalCount'/></th>
				<!-- 总金额 -->
				<th><spring:message code='clear.task.totalAmt'/></th>
				<!-- 交接人 -->
				<th><spring:message code='clear.task.handover.name'/></th>
				<!-- 任务类型 -->
				<th><spring:message code='allocation.tasktype'/></th>
				<!-- 操作人 -->
				<th><spring:message code='clear.task.operatorName'/></th>
				<!-- 登记时间 -->
				<th><spring:message code='clear.register.date'/></th>
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 操作 -->
				<shiro:hasPermission name="clear:v03:cpTaskAllocation:edit"><th><spring:message code='common.operation'/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clTaskMain" varStatus="status">
		<shiro:hasPermission name="clear:v03:cpTaskAllocation:view">
			<tr>
				<shiro:hasPermission name="clear:v03:cpTaskAllocation:print">
				<!-- 多选框 -->
				<td style="text-align: center" >
						<input type="checkbox" id="chkPrintItem" value="${clTaskMain.taskNo}"/>
				</td>
				</shiro:hasPermission>
				<!-- 业务流水 -->
				<td>
					<a href="${ctx}/clear/v03/cpTaskAllocation/view?taskNo=${clTaskMain.taskNo}">${clTaskMain.taskNo}</a>
				</td>
				<!-- 业务类型 -->
				<td>${fns:getDictLabel(clTaskMain.busType,'clear_businesstype',"")}</td>
				<!-- 币种 -->
				<td>${fns:getDictLabel(clTaskMain.currency,'money_currency',"")}</td>
				<!-- 面值 -->
				<td>${sto:getGoodDictLabel(clTaskMain.denomination, 'cnypden', "")}</td>
				<!-- 计划类型 -->
				<td>${fns:getDictLabel(clTaskMain.planType,'clear_plan_type',"")}</td>
				<!-- 捆数 -->
				<td>${clTaskMain.totalCount}</td>
				<!-- 总金额 -->
				<td><fmt:formatNumber value="${clTaskMain.totalAmt}" pattern="#,##0.00#" /></td>
				<!-- 清分交接人 -->
				<td>${clTaskMain.joinManName}</td>
				<!-- 任务类型 -->
				<td>${fns:getDictLabel(clTaskMain.taskType,'clear_task_type',"")}</td>
				<!-- 操作人 -->
				<td>${clTaskMain.operatorName}</td>
				<!-- 登记时间 -->
				<td><fmt:formatDate value="${clTaskMain.operateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<!-- 增加机构 wzj 2017-11-27 begin -->
				<td>
				${clTaskMain.office.name} 
				
				</td>
				<shiro:hasPermission name="clear:v03:cpTaskAllocation:print">
				<!-- end -->
				<td> 
				<%-- <span style='width:30px;display:inline-block;'> 
					<!-- 查看详情 -->	
					<a href="${ctx}/clear/v03/cpTaskAllocation/view?taskNo=${clTaskMain.taskNo}" title="<spring:message code='clear.task.checketails' />"><i class="fa fa-eye fa-lg">
					</i></a>
				</span> --%>
				<span style='width:20px;display:inline-block;'>
					
						<!-- 打印 -->
    					<a href="#" onclick="printDetail('${clTaskMain.taskNo}');"  title="<spring:message code='common.print'/>"><i class="fa fa-print text-yellow fa-lg"></i></a>
					
				</span>
				</td>
				</shiro:hasPermission>
				
			</tr>
			</shiro:hasPermission>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>