<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户清点量(年)-面值明细 -->
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-left:2px;margin-top:20px;">
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 面额-->
				<th style="text-align: center;"><spring:message code='door.public.denomination'/></th>
				<!-- 数量（张）-->
				<th style="text-align: center;"><spring:message code='door.public.countSheet'/></th>
				<!-- 金额（元）-->
				<th style="text-align: center;"><spring:message code='door.public.moneyYuan'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${data}" var="itemDetail" varStatus="status">
				<tr>
					<td style="text-align: left;">${itemDetail.rowPayValue}</td>
					<td style="text-align: right;">${itemDetail.rowCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${itemDetail.rowAmount}" type="currency" pattern="#,##0.00"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>