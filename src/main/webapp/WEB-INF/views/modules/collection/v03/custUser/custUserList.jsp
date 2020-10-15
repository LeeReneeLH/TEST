<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<li class="active"><a href="${ctx}/collection/v03/custUser/list">客户列表</a></li>
		<shiro:hasPermission name="collection:custUser:edit"><li><a href="${ctx}/collection/v03/custUser/form">客户添加</a></li></shiro:hasPermission>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="custUser" action="${ctx}/collection/v03/custUser/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
				
					<%-- 商户--%>
					<label class="control-label">商户：</label>
					<form:select path="storeId" id="storeId" class="input-large required" style="font-size:15px;color:#000000">
						<form:option value=""><spring:message code="common.select" /></form:option>
						<form:options items="${storeList}" htmlEscape="false" itemLabel="label" itemValue="value"/>
					</form:select>
					&nbsp;&nbsp;&nbsp;
					<%-- 登录名 --%>
					<label>登录名：</label>
					<form:input path="loginName" class="input-medium" htmlEscape="false" maxlength="15" />
					&nbsp;&nbsp;&nbsp;
					<%-- 姓名 --%>
					<label>姓名：</label>
					<form:input path="name" class="input-medium" htmlEscape="false" maxlength="15" />
					&nbsp;&nbsp;&nbsp;
					<%-- 客户类别 --%>
					<label>客户类别：</label>
					<form:select path="userType" class="input-large required" id ="selectStatus">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('cust_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	
	<sys:message content="${message}"/>
	
	
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th>商户名称</th>
				<th>登录名</th>
				<th>姓名</th>
				<th>客户类型</th>
				<th>手机</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="itemData">
			<tr>
				<td>${itemData.storeName}</td>
				<td><a href="${ctx}/collection/v03/custUser/view?id=${itemData.id}">${itemData.loginName}</a></td>
				<td>${itemData.name}</td>
				<td>${fns:getDictLabel(itemData.userType, 'cust_user_type', '')} </td>
				<td>${itemData.phone}</td>
				<td>
				<shiro:hasPermission name="collection:custUser:edit">
    				<a href="${ctx}/collection/v03/custUser/form?id=${itemData.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>&nbsp;&nbsp;&nbsp;
					<a  href="${ctx}/collection/v03/custUser/delete?id=${itemData.id}" onclick="return confirmx('确认要删除该客户吗？', this.href)"   title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</shiro:hasPermission>
				</td>
			</tr>

		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>