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
		<li ><a href="${ctx}/doorOrder/v01/dayReportMain/"><spring:message code='clear.dayReportMain.list'/></a></li>
		
		<!-- 日结每笔明细 -->
		<li class="active"><a href=""><spring:message code='clear.dayReportMain.view'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="${ctx}/doorOrder/v01/dayReportCenter/detailView" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="reportDate" name="reportDate" type="hidden" value="${reportDate}"/>	
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 客户名称 -->
				<th><spring:message code="clear.public.custName"/></th>
				<!-- 存款人 -->
				<th><spring:message code="clear.public.inManName"/></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型  -->
				<th><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 现钞存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.cashAmount"/></th>
				<!-- 封包存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.packAmount"/></th>
				<!-- 客户存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 公司汇款金额(元) -->
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
						${centerAccountsMain.createName}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessType,'door_businesstype',"")}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessStatus,'cl_status_type',"")}
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.cash==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.cash}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.pack==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.pack}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
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
	<div class="form-actions">
			<!-- 返回 -->
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
	</div>
</body>
</html>