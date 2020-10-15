<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	</script>
</head>
<body> 
		<!-- Tab页 -->
	    <ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/doorOrder/v01/saveType/">
				  款项类型列表
			</a>
		</li>
		<shiro:hasPermission name="doorOrder:saveType:edit">
		<li>
			<a href="${ctx}/doorOrder/v01/saveType/form">
				款项类型添加
			</a>
		</li>
		</shiro:hasPermission>
	</ul>
	
	  <form:form id="searchForm" modelAttribute="saveType" action="${ctx}/doorOrder/v01/saveType/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<!-- 代码 -->
		<div>
		<label><spring:message code="door.saveType.typeCode" />：</label>
		<form:input path="typeCode" maxlength="10" class="input-small" style="width:150px;"/>
		</div>
		<!-- 名称 -->
		<div>
		<label><spring:message code="door.saveType.typeName" />：</label>
		<form:input path="typeName" maxlength="10" class="input-small" style="width:150px;"/>
		</div>
		<%-- 商户名称 --%>
		<div>
		<label><spring:message code="door.saveType.clientName" />：</label>
			<sys:treeselect id="merchantName" name="merchantId"
			value="${saveType.merchantId}" labelName="merchantName"
			labelValue="${saveType.merchantName}" title="商户名称"
			url="/sys/office/treeData"  allowClear="true"  type="9"
			cssClass="required input-small" isAll="true"  clearCenterFilter="true"/>
		</div>
		<!-- 按钮 -->
		&nbsp;&nbsp;
		<div>
		<input id="btnSubmit" class="btn btn-primary" 
			type="submit" value="<spring:message code='common.search'/>"/>
		</div>
		</div>
	</form:form>  
	<sys:message content="${message}" />
	<!-- 列表 -->
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="door.saveType.seq" /></th>
				<!-- 存款类型代码 -->
				<th class="sort-column a.type_code"><spring:message code="door.saveType.typeCode" /></th>
				<!-- 存款类型名称-->
				<th class="sort-column a.type_name"><spring:message code="door.saveType.typeName" /></th>
				<!-- 商户名称 -->
				<th class="sort-column o1.name"><spring:message code="door.saveType.clientName" /></th>
				<!-- 创建人 -->
				<th class="sort-column a.create_name"><spring:message code="door.saveType.creator" /></th>
				<!-- 创建时间 -->
				<th class="sort-column a.create_date"><spring:message code="door.saveType.createDate" /></th>
				<!-- 操作 -->
				<shiro:hasPermission name="doorOrder:saveType:edit">
				<th><spring:message code='common.operation'/></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="saveType" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${saveType.typeCode}</td>
				<td>${saveType.typeName}</td>
				<td>${saveType.merchantName}</td>
				<td>${saveType.createName}</td>
				<td><fmt:formatDate value="${saveType.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td>
				<!--编辑  -->
   				<a href="${ctx}/doorOrder/v01/saveType/form?id=${saveType.id}" title="编辑">
   					<i class="fa fa-edit text-green fa-lg"></i>
   				</a>
   				&nbsp;
   				<!--删除  -->
   				<a href="${ctx}/doorOrder/v01/saveType/delete?id=${saveType.id}" 
   				onclick="return confirmx('确认要删除吗？', this.href)" title="删除"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div> 
</body>
</html>
