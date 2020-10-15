<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 重空管理标题 -->
	<title><spring:message code="common.importantEmpty.manage"/></title>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 重空列表 -->
		<li class="active"><a href="${ctx}/store/v01/stoEmptyDocument/"><spring:message code="common.importantEmpty.list"/></a></li>
		<!-- 重空添加 -->
		<shiro:hasPermission name="store:stoEmptyDocument:edit"><li><a href="${ctx}/store/v01/stoEmptyDocument/form"><spring:message code="common.importantEmpty"/><spring:message code="common.add"/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoEmptyDocument" action="${ctx}/store/v01/stoEmptyDocument/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<sys:blankBillList/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 重空分类 -->
				<th><spring:message code="common.importantEmpty.kind"/></th>
				<!-- 重空类型 -->
				<th><spring:message code="common.importantEmpty.type"/></th>
				<!-- 起止区间值 -->
				<th><spring:message code="store.intervalValue"/></th>
				<!-- 数量 -->
				<th><spring:message code="common.number"/></th>
				<!-- 可用区间 -->
				<th><spring:message code="store.usableRange"/></th>
				<!-- 可用数量 -->
				<th class="sort-column a.balance_number"><spring:message code="store.usable.num"/></th>
				<!-- 操作 -->
				<shiro:hasPermission name="store:stoEmptyDocument:edit"><th><spring:message code="common.operation"/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoEmptyDocument" varStatus="status">
			<tr>
				<td>${status.index+1 }</td>
				<td>${sto:getGoodDictLabel(stoEmptyDocument.stoBlankBillSelect.blankBillKind,'blank_bill_kind','')}</td>
				<td>${sto:getGoodDictLabel(stoEmptyDocument.stoBlankBillSelect.blankBillType,'blank_bill_type','')}</td>
				<td>${stoEmptyDocument.startNumber}-${stoEmptyDocument.endNumber}</td>
				<td>${stoEmptyDocument.createNumber}</td>
				<c:if test="${stoEmptyDocument.balanceNumber==0}">
					<td>无</td>
				</c:if>
				<c:if test="${stoEmptyDocument.balanceNumber>0}">
					<td>${stoEmptyDocument.createNumber-stoEmptyDocument.balanceNumber+stoEmptyDocument.startNumber}-${stoEmptyDocument.endNumber}</td>
				</c:if>
				<td>${stoEmptyDocument.balanceNumber}</td>
				<shiro:hasPermission name="store:stoEmptyDocument:edit"><td>
				<c:if test="${stoEmptyDocument.createNumber==stoEmptyDocument.balanceNumber}">
    				<a href="${ctx}/store/v01/stoEmptyDocument/form?id=${stoEmptyDocument.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i><%-- <spring:message code="common.modify"/> --%></a>
					<a href="${ctx}/store/v01/stoEmptyDocument/delete?id=${stoEmptyDocument.id}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i><%-- <spring:message code="common.delete"/> --%></a>
				</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
