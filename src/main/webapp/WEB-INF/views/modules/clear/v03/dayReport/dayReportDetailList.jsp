<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务日结管理 -->
	<title><spring:message code='clear.dayReportMain.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");
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
		<!-- 账务日结列表 -->
		<li ><a href="${ctx}/clear/v03/dayReportMain/"><spring:message code='clear.dayReportMain.list'/></a></li>
		<!-- 日结每笔明细 -->
		<li class="active"><a href=""><spring:message code='clear.dayReportMain.view'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 返回 -->
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 客户名称 -->
				<th><spring:message code="clear.public.custName"/></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型  -->
				<th><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 借方(元) -->
				<th><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 贷方(元) -->
				<th><spring:message code="clear.centerAccounts.lender"/></th>
				<!-- 交易时间 -->
				<th><spring:message code="clear.centerAccounts.tradingHours"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="centerAccountsMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${centerAccountsMain.clientName}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessType,'clear_businesstype',"")}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessStatus,'cl_status_type',"")}
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.inAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.inAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<fmt:formatDate value="${centerAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
		
	<div class="pagination">${page}</div>
</body>
</html>