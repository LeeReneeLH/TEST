<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>令牌管理</title>
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
		<li class="active"><a href="${ctx}/sys/sysOtp/"><spring:message code="sys.otp.list" /></a></li>
		<shiro:hasPermission name="sys:sysOtp:edit"><li><a href="${ctx}/sys/sysOtp/form"><spring:message code="sys.otp.add" /></a></li></shiro:hasPermission>
		<shiro:hasPermission name="sys:sysOtp:office"><li><a href="${ctx}/sys/sysOtp/formOffice"><spring:message code="sys.otp.office" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysOtp" action="${ctx}/sys/sysOtp/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div class="row search-flex">		
			<div>
			<label><spring:message code="sys.otp.number" />：</label>
			<form:input path="tokenId" htmlEscape="false" maxlength="15" class="input-medium"/>
			</div>
			<div>
				<label><spring:message code="store.ownershipInstitution" />：</label>
				<sys:treeselect id="office" name="office.id"
					value="${sysOtp.office.id}" labelName="office.name"
					labelValue="${sysOtp.office.name}" title="机构"
					url="/sys/office/treeData" notAllowSelectRoot="false"
					notAllowSelectParent="false" allowClear="true"
					cssClass="input-medium" checkGroupOffice="true"/>
			</div>
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="sys.otp.number" /></th>
				<th><spring:message code="sys.otp.office" /></th>
				<th><spring:message code="sys.otp.bindingUser" /></th>
				<th><spring:message code="sys.otp.operator" /></th>
				<th><spring:message code="sys.otp.registerDate" /></th>
				<shiro:hasPermission name="sys:sysOtp:edit"><th><spring:message code="sys.otp.operate" /></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysOtp">
			<tr>
				<td><a href="${ctx}/sys/sysOtp/form?id=${sysOtp.id}">${sysOtp.tokenId}</td>
					<td>${sysOtp.office.name}</td>
					<td>${sysOtp.user.name}</td>
					<td>${sysOtp.createName}</td>
					<td><fmt:formatDate value="${sysOtp.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</a></td>
				<shiro:hasPermission name="sys:sysOtp:edit"><td>
					<a href="${ctx}/sys/sysOtp/toSynOtp?id=${sysOtp.id}" title="同步"><i class="fa fa-refresh text-green fa-lg"></i></a>&nbsp;&nbsp;
					<a href="${ctx}/sys/sysOtp/toBindingUser?id=${sysOtp.id}" title="绑定"><i class="fa fa-credit-card text-green fa-lg"></i></a>&nbsp;&nbsp;				
    				<a href="${ctx}/sys/sysOtp/form?id=${sysOtp.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/sys/sysOtp/delete?id=${sysOtp.id}" onclick="return confirmx('确认要删除该令牌吗？', this.href)" title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>