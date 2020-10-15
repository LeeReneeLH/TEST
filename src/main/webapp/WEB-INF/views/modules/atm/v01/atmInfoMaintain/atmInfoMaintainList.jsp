<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#searchForm").validate({});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/atm/v01/atmInfoMaintain/"><spring:message code="title.atm.addPlan.list" /></a></li>
		<shiro:hasPermission name="atm:atmBrandsInfo:view">
			<li><a href="${ctx}/atm/v01/atmInfoMaintain/form"><spring:message code="title.atm.info" /><spring:message code="common.add" /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="atmInfoMaintain" action="${ctx}/atm/v01/atmInfoMaintain/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<label><spring:message code="atm.model" />：</label>
		<form:select path="atmTypeNo" class="input-xlarge">
		    <form:option value=""><spring:message code="common.select"/></form:option>
			<form:options items="${atm:getAtmTypesinfoList()}"
				itemLabel="atmTypeName" itemValue="atmTypeNo" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<label><spring:message code="atm.brands" />：</label>
		<form:select path="atmBrandsNo" class="input-large">
		    <form:option value=""><spring:message code="common.select"/></form:option>
			<form:options items="${atm:getAtmBrandsinfoList()}"
				itemLabel="atmBrandsName" itemValue="atmBrandsNo" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<label><spring:message code="common.status" />：</label>
		<form:select path="delFlag" class="input-medium">
			<form:options items="${fns:getDictList('del_flag')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
		    <tr>
		    	<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!--ATM机编号  -->
				<th><spring:message code="label.atm.id.no" /></th>
				<!--柜员号  -->
				<th><spring:message code="label.atm.add.teller"/></th>
				<!--维护机构名称  -->
				<th><spring:message code="atm.vindicate.officeName"/></th>
				<!--归属机构名称  -->
				<th><spring:message code="atm.ascription.officeName"/></th>
				<!--所属金库名称  -->
				<th><spring:message code="atm.ascription.coffersName"/></th>
				<!--品牌名称  -->
				<th><spring:message code="atm.brands.name"/></th>
				<!-- 型号名称 -->
				<th><spring:message code="atm.type.name"/></th>
				<!-- RFID -->
				<th><spring:message code="atm.rfid"/></th>
				<shiro:hasPermission name="atm:atmInfoMaintain:edit"><th><spring:message code="common.operation"/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmInfoMaintain" varStatus="status">
			<tr>
			    <td>${status.index+1}</td>
				<td><a href="${ctx}/atm/v01/atmInfoMaintain/view?id=${atmInfoMaintain.id}">${atmInfoMaintain.atmId}</a></td>
				<td>${atmInfoMaintain.tellerId}</td>
				<td>${atmInfoMaintain.vinofficeName}</td>
				<td>${atmInfoMaintain.aofficeName}</td>
				<td>${atmInfoMaintain.tofficeName}</td>
				<td>${atmInfoMaintain.atmBrandsName}</td>
				<td>${atmInfoMaintain.atmTypeName}</td>
				<td>${atmInfoMaintain.rfid}</td>
					<shiro:hasPermission name="atm:atmInfoMaintain:edit">
						<td>
							<c:choose>
								<c:when test="${atmInfoMaintain.delFlag == '0' }">
									<a href="${ctx}/atm/v01/atmInfoMaintain/form?id=${atmInfoMaintain.id}" title="编辑">
									<i class="fa fa-edit text-green fa-lg"></i></a>
									<a href="${ctx}/atm/v01/atmInfoMaintain/delete?id=${atmInfoMaintain.id}"
										onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
										<i class="fa fa-trash-o text-red fa-lg"></i></a>
								</c:when>
								<c:otherwise>
									<a href="${ctx}/atm/v01/atmInfoMaintain/recovery?id=${atmInfoMaintain.id}" title="恢复删除">
									<spring:message code="common.recovery" /></a>
								</c:otherwise>
							</c:choose>
						</td>
					</shiro:hasPermission>
				</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
