<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息配置管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/sys/messageRelevance/list");
				$("#searchForm").submit();
			});
		});
	</script>
	<style>
	.ul-form{margin:0;display:table;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#"><spring:message code='sys.message.relevance.list' /></a></li>
		<li><a href="${ctx}/sys/messageRelevance/form"><spring:message code='sys.message.relevance.add' /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="message" action="${ctx}/sys/messageRelevance/list" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	</form:form>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code='sys.message.relevance.name' /></th>
				<th><spring:message code='sys.message.relevance.url' /></th>
				<th><spring:message code='sys.message.relevance.menu' /></th>
				<th><spring:message code='sys.message.relevance.menuId' /></th>
				<%-- 操作（查看） --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="msg">
			<tr>
				<td>${msg.messageTypeName}</td>
				<td>${msg.url}</td>
				<td><c:if test="${msg.menuId!='' && msg.menuId!=null}">${fns:getMenuById(msg.menuId)}</c:if></td>
				<td>${msg.menuId}</td>
				<td><a href="${ctx}/sys/messageRelevance/form?messageType=${msg.messageType}&menuId=${msg.menuId}" title="编辑">
					<i class="fa fa-pencil-square fa-lg"></i></a>
					<c:choose>
					<c:when test="${msg.delFlag == '1'}">
						<a href="${ctx}/sys/messageRelevance/validChange?messageType=${msg.messageType}" onclick="return confirmx('确认要恢复该关联吗？', this.href)" title="恢复"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
					</c:when>
					<c:when test="${msg.delFlag == '0'}">
						<a href="${ctx}/sys/messageRelevance/validChange?messageType=${msg.messageType}" onclick="return confirmx('确认要停用该关联吗？', this.href)" title="停用"><i class="fa fa-ban text-red fa-lg"></i></a>
					</c:when>
					</c:choose></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>