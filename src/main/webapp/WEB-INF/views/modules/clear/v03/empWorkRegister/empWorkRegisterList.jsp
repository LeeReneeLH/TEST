<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
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
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 员工工作量登记 -->
		<shiro:hasPermission name="clear:v03:empWorkRegister:edit"><li><a href="${ctx}/clear/v03/empWorkRegister/form"><spring:message code="clear.empWorkRegister.register" /></a></li></shiro:hasPermission>
		<!-- 员工工作量登记列表 -->
		<li class="active"><a href="${ctx}/clear/v03/empWorkRegister/"><spring:message code="clear.empWorkRegister.list" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="clTaskMain"
		action="${ctx}/clear/v03/empWorkRegister/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-16 begin -->
		<input id="createTimeStart" name="createTimeStart" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${clTaskMain.createTimeStart}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-16 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${clTaskMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		&nbsp;
		<!-- 增加清分机构 qph 2017-11-28 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${clTaskMain.office.id}" labelName="office.name"
				labelValue="${clTaskMain.office.name}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
	<!-- end -->
	<!-- 查询 -->
	<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />

	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 业务流水 -->
				<th><spring:message code='clear.task.business.id' /></th>
				<!-- 业务类型 -->
				<th><spring:message code='clear.task.business.type' /></th>
				<!-- 币种 -->
				<th><spring:message code='common.currency' /></th>
				<!-- 面值 -->
				<th><spring:message code='common.denomination' /></th>
				<!-- 计划类型 -->
				<th><spring:message code='clear.task.planType' /></th>
				<!-- 捆数 -->
				<th><spring:message code='clear.task.totalCount' /></th>
				<!-- 总金额 -->
				<th><spring:message code='clear.task.totalAmt' /></th>
				<!-- 操作人 -->
				<th><spring:message code='clear.task.operatorName' /></th>
				<!-- 登记时间 -->
				<th><spring:message code='clear.register.date' /></th>
				<!-- 创建时间 -->
				<th><spring:message code='common.createDateTime' /></th>
				<!-- 增加机构显示 qph 2017-11-28 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clTaskMain" varStatus="status">
				<tr>
					<shiro:hasPermission name="clear:v03:empWorkRegister:view">
						<!-- 业务流水 -->
						<td><a href="${ctx}/clear/v03/empWorkRegister/view?taskNo=${clTaskMain.taskNo}">${clTaskMain.taskNo}</a></td>
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
						<td><fmt:formatNumber value="${clTaskMain.totalAmt}"
								pattern="#,##0.00#" /></td>
						<!-- 操作人 -->
						<td>${clTaskMain.operatorName}</td>
						<!-- 登记时间 -->
						<td><fmt:formatDate value="${clTaskMain.operateDate}"
								pattern="yyyy-MM-dd" /></td>
						<!-- 创建时间 -->
						<td><fmt:formatDate value="${clTaskMain.createDate}"
								pattern="yyyy-MM-dd HH:mm:ss" /></td>
						<!-- 增加机构 qph 2017-11-27 begin -->
						<td>${clTaskMain.office.name}</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>