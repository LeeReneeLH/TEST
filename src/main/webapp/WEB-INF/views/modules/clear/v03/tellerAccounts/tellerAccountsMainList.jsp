<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 柜员账务管理 -->
	<title><spring:message code='clear.tellerAccount.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
		<li class="active"><a href="${ctx}/clear/v03/tellerAccountsMain/"><spring:message code='clear.tellerAccount.list'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="tellerAccountsMain" action="${ctx}/clear/v03/tellerAccountsMain/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 柜员姓名 -->
		<label><spring:message code="clear.tellerAccount.tellerName" /> ：</label>
		<form:input path="tellerName" htmlEscape="false" maxlength="10" class=""/>
		<label class="control-label"><spring:message code="clear.tellerAccount.cashType"/>：</label>
		<form:select path="cashType" id="cashType" class="input-large">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>				
			<form:options items="${fns:getFilterDictList('cash_type_provisions',true,'')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>&nbsp;&nbsp;
		<!-- 增加清分机构 qph 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${tellerAccountsMain.office.id}" labelName="office.name"
				labelValue="${tellerAccountsMain.office.name}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 柜员姓名 -->
				<th><spring:message code="clear.tellerAccount.tellerName" /></th>
				<!-- 余额（元） -->
				<th><spring:message code="clear.public.balance"/></th>
				<!-- 余额类型 -->
				<th><spring:message code="clear.tellerAccount.cashType"/></th>
				<!-- 增加机构显示 qph 2017-11-27 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tellerAccountsMain" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${tellerAccountsMain.tellerName}</td>	
				<td><fmt:formatNumber value="${tellerAccountsMain.totalAmount}" pattern="#,##0.00#" /></td>
				<td>${fns:getDictLabel(tellerAccountsMain.cashType,'cash_type_provisions',"")}</td>
				<!-- 增加机构 qph 2017-11-27 begin -->
					<td>
					${tellerAccountsMain.office.name} 
					</td>
				<shiro:hasPermission name="clear.v03:tellerAccountsMain:view"><td>
    				<!-- 查看 -->
    				<a href="${ctx}/clear/v03/tellerAccountsMain/tellerAccountsDetail?tellerBy=${tellerAccountsMain.tellerBy}&cashType=${tellerAccountsMain.cashType}" title="<spring:message code='common.view'/>">
    					<i class="fa fa-eye fa-lg"></i>
    				</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>