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
		<!-- 账务日结明细 -->
		<li class="active"><a href=""><spring:message code='clear.dayReportMain.detail'/></a></li>
	</ul>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 账务类型 -->
				<th><spring:message code="clear.dayReportMain.accountingType"/></th>
				<!-- 结算前余额(元) -->
				<th><spring:message code='door.accountManage.beforeSettlementCash'/></th>
				<!-- 收入笔数 -->
				<th><spring:message code='clear.dayReportMain.incomeNumber'/></th>
				<!-- 收入金额(元) -->
				<th><spring:message code='clear.dayReportMain.incomeAmount'/></th>
				<!-- 付出笔数 -->
				<th><spring:message code='clear.dayReportMain.outcomeNumber'/></th>
				<!-- 付出金额(元) -->
				<th><spring:message code='clear.dayReportMain.payAmount'/></th>
				<!-- 结算后金额(元) -->
				<th><spring:message code='door.accountManage.afterSettlementCash'/></th>
				<!-- 日结人 -->
				<th><spring:message code='clear.dayReportMain.dailyPeople'/></th>
				<!-- 状态 -->
				<th><spring:message code="common.status"/></th>
				<!-- 结账类型 -->
				<th><spring:message code="clear.dayReportMain.checkType"/></th>
				<!-- 结算时间 -->
				<th><spring:message code="clear.dayReportMain.checkTime"/></th>
				<!-- 结算日期 -->
				<th><spring:message code="clear.dayReportMain.checkDate"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dayReportMain.dayReportCenterList}" var="dayReportCenter" varStatus="status">
				<tr>
					<td>
						${fns:getDictLabel(dayReportCenter.accountsType,'accounts_type',"")}	
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.beforeAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.inCount}" pattern="#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.inAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.outCount}" pattern="#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.outAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportCenter.totalAmount}" pattern="#,##0.00#" />
					</td>
					<!-- 判断结账类型是否为自动结账如果是自动结账显示系统日结 修改人:sg 修改日期:2017-12-07 begin -->
					<%-- <td>
						${dayReportMain.reportName}
					</td> --%>
					<c:if test="${dayReportMain.windupType eq 0}">
						<td>
							<spring:message code="clear.dayReportMain.reportName"/>
						</td>
					</c:if>
					<c:if test="${dayReportMain.windupType ne 0}">
						<td>
							${dayReportMain.reportName}
						</td>
					</c:if>
					<!-- end -->
					<c:choose>
						<c:when test="${dayReportMain.status eq 0}">
							<td style="color: green">
						</c:when>
						<c:otherwise>
							<td style="color: red">
						</c:otherwise>
					</c:choose>
						${fns:getDictLabel(dayReportMain.status,'day_report_status',"")}
					</td>
					<td>
						${fns:getDictLabel(dayReportMain.windupType,'day_report_windupType',"")}
					</td>
					<td>
						<fmt:formatDate value="${dayReportMain.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						<fmt:formatDate value="${dayReportMain.reportDate}" pattern="yyyy-MM-dd"/>
					</td>
					<td>
						<!-- 查看 -->
						<a href="${ctx}/doorOrder/v01/dayReportCenter/detailView?accountsType=${dayReportCenter.accountsType}&reportDate=${dayReportCenter.reportDate}&reportMainId=${dayReportCenter.reportMainId}" title = "<spring:message code='common.view'/>">
										<%-- 	<spring:message code="common.view" /> --%>
											<i class="fa fa-eye fa-lg"></i>
						</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>