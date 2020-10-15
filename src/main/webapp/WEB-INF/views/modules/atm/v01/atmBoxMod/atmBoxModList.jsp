<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="atm.modelConfigure.manage"/></title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/atm/v01/atmBoxMod/"><spring:message code="atm.modelConfigure.list"/></a></li>
		<shiro:hasPermission name="atm:atmBoxMod:edit">
			<li><a href="${ctx}/atm/v01/atmBoxMod/form"><spring:message code="atm.modelConfigure"/><spring:message code="common.add"/></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="atmBoxMod" action="${ctx}/atm/v01/atmBoxMod/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<label><spring:message code="atm.atmBox.modelName"/>：</label>
		<form:input path="modName" htmlEscape="false" maxlength="20" class="input-medium"/>
		&nbsp;&nbsp;<label><spring:message code="atm.brands" />：</label>
		<form:select path="atmBrandsNo" class="input-large">
		    <form:option value=""><spring:message code="common.select"/></form:option>
			<form:options items="${atm:getAtmBrandsinfoList()}"
				itemLabel="atmBrandsName" itemValue="atmBrandsNo" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			    <th><spring:message code="common.seqNo"/></th>
			    <th class="sort-column a.mod_name"><spring:message code="atm.atmBox.modelName"/></th>
			    <th class="sort-column a.atm_brands_no"><spring:message code="atm.brands.id"/></th>
				<th><spring:message code="atm.brands.name"/></th>
			    <th class="sort-column a.box_type"><spring:message code="common.atmBoxType"/></th>
				<th class="sort-column a.box_type_no"><spring:message code="common.atmBox.model"/></th>
				<shiro:hasPermission name="atm:atmBoxMod:edit"><th><spring:message code="common.operation"/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmBoxMod" varStatus="status">
			<tr>
			    <td>${status.index+1}</td>
				<td><a href="${ctx}/atm/v01/atmBoxMod/view?id=${atmBoxMod.id}">
					${atmBoxMod.modName}
				</a></td>
				<td>${atmBoxMod.atmBrandsNo}</td>
				<td>${atmBoxMod.atmBrandsName}</td>
				<c:if test="${atmBoxMod.boxType eq 1 }">
					<td>${fns:getDictLabel('41','sto_box_type',null)}</td>
				</c:if>
				<c:if test="${atmBoxMod.boxType eq 2 }">
					<td>${fns:getDictLabel('42','sto_box_type',null)}</td>
				</c:if>
				<c:if test="${atmBoxMod.boxType eq 3 }">
					<td>${fns:getDictLabel('43','sto_box_type',null)}</td>
				</c:if>
				<c:if test="${atmBoxMod.boxType eq 4}">
					<td>${fns:getDictLabel('44','sto_box_type',null)}</td>
				</c:if>
				
				<td>${atmBoxMod.boxTypeNo}</td>
				<shiro:hasPermission name="atm:atmBoxMod:edit"><td>
    				<a href="${ctx}/atm/v01/atmBoxMod/form?id=${atmBoxMod.id}" title="编辑">
    				<i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/atm/v01/atmBoxMod/delete?id=${atmBoxMod.id}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" 
					title="删除"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
