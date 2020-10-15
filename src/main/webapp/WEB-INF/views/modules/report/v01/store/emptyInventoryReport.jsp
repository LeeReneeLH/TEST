<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.importantEmptyInventoryReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 重空库存报表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.importantEmptyInventoryReport" /></a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="stoReportInfo" action="${ctx}/report/v01/store/exportEmptyInventoryReport" method="post" class="breadcrumb form-search">
		<input id="btnExport" class="btn btn-primary" type="submit" value="<spring:message code='common.export'/>"/>
	</form:form>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 日期 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='common.date'/></th>
				<%-- 重空名称 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='common.importantEmpty.name'/></th>
				<%-- 数量 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='common.number'/></th>
			</tr>
		</thead>
		<tbody>

		<c:forEach items="${emptyInventoryList}" var="emptyInfo" varStatus="status">
			<c:choose>
				<c:when test="${not empty emptyInfo.time}">
					<tr>
						<td style="text-align:center">${emptyInfo.time}</td>
						<td style="text-align:center">${emptyInfo.goodsName}</td>
						<td style="text-align:right">${emptyInfo.stoNum}</td>
					</tr>
				</c:when>
			</c:choose>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
