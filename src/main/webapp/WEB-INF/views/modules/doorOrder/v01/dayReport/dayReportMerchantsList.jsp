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
		<li class="active"><a href="">商户日结明细</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 返回 -->
		 <a href="${ctx}/doorOrder/v01/dayReportMain/centerView?reportId=${reportMainId}"><input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" /></a> 
	</form:form>
	<sys:message content="${message}"/>
	<div style="overflow-y: auto; height: 600px;">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 商户名称 -->
				<th><spring:message code="door.accountManage.store"/></th>
				<!-- 借方(元) -->
				<%-- <th><spring:message code="clear.centerAccounts.borrower"/></th> --%>
				<!-- 贷方(元) -->
				<%-- <th><spring:message code="clear.centerAccounts.lender"/></th> --%>
				<!-- 商户日结金额类型 -->
				<th><spring:message code="door.accountManage.settlementType"/></th>
				<!-- 商户日结金额 -->
				<th><spring:message code="door.accountManage.merchantAmount"/></th>
				<!-- 交易时间 -->
				<th><spring:message code="clear.dayReportMain.checkDate"/></th>
				<!-- 代付状态 -->
				<th><spring:message code="door.accountManage.payStatus"/></th>
				<!-- 操作 -->
				<th colspan="2"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${pageForMerchantInfo.list}" var="merchantInfo" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${merchantInfo.officeName}
					</td>
					<td>
						${fns:getDictLabel(merchantInfo.settlementType,'door_businesstype',"")}
					</td>
					<td>
						<c:choose>
							<c:when test="${merchantInfo.totalAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${merchantInfo.totalAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<%-- <td>
							<fmt:formatNumber value="0" pattern="#,##0.00#" />
					</td> --%>
					<td>
						<fmt:formatDate value="${merchantInfo.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<c:if test="${merchantInfo.paidStatus eq '1'}">
					<td style="color: red">
						${fns:getDictLabel(merchantInfo.paidStatus,'deposit_payment_status',"")}
					</td>
					<td>
					<!-- 商户存款总额不为0，可以汇款  start-->
					<c:if test="${merchantInfo.totalAmount != 0}">
						<a href="${ctx}/doorOrder/v01/dayReportCenter/paid?accountsType=${merchantInfo.accountsType}&reportDate=${merchantInfo.reportDate}&officeId=${merchantInfo.officeId}&reportMainId=${reportMainId}" title="汇款" onclick="return confirmx('是否确认汇款？', this.href)"><i class="fa fa-credit-card text-green fa-lg"></i></a>
						&nbsp&nbsp
					</c:if>
						<!-- end -->
						<!-- 查看 -->
						<a href="${ctx}/doorOrder/v01/dayReportCenter/detailView?accountsType=${merchantInfo.accountsType}&reportDate=${merchantInfo.reportDate}&officeId=${merchantInfo.officeId}&settlementType=${merchantInfo.settlementType}" title = "<spring:message code='common.view'/>">
										<%-- 	<spring:message code="common.view" /> --%>
											<i class="fa fa-eye fa-lg"></i>
						</a>
					</td>
					</c:if>
					<c:if test="${merchantInfo.paidStatus eq '0'}">
					<td style="color: green">
						${fns:getDictLabel(merchantInfo.paidStatus,'deposit_payment_status',"")}
					</td>
					<td>
						<!-- 查看 -->
						<a href="${ctx}/doorOrder/v01/dayReportCenter/detailView?accountsType=${merchantInfo.accountsType}&reportDate=${merchantInfo.reportDate}&officeId=${merchantInfo.officeId}&settlementType=${merchantInfo.settlementType}" title = "<spring:message code='common.view'/>">
											<i class="fa fa-eye fa-lg"></i>
						</a>
					</td>
					
					</c:if>
					
					<c:if test="${merchantInfo.paidStatus==null}">
					<td>
					</td>
					<td>
						<!-- 查看 -->
						<a href="${ctx}/doorOrder/v01/dayReportCenter/detailView?accountsType=${merchantInfo.accountsType}&reportDate=${merchantInfo.reportDate}&officeId=${merchantInfo.officeId}&settlementType=${merchantInfo.settlementType}" title = "<spring:message code='common.view'/>">
											<i class="fa fa-eye fa-lg"></i>
						</a>
					</td>
					
					</c:if>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
		
	<%-- <div class="pagination">${pageForMerchantInfo}</div> --%>
</body>
</html>