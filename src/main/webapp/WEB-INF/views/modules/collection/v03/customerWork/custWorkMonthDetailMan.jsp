<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户清点量(月)-人员明细 -->
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-left:2px;margin-top:20px;">
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 清分人员 -->
				<th style="text-align: center;"><spring:message code='door.public.clearMan'/></th>
				<!-- 笔数 -->
				<th style="text-align: center;"><spring:message code='door.public.count'/></th>
				<!-- 金额（元） -->
				<th style="text-align: center;"><spring:message code='door.public.moneyYuan'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${data}" var="itemDetail" varStatus="status">
				<tr>
					<td style="text-align: left;">${itemDetail.rowManName}</td>
					<td style="text-align: right;">${itemDetail.rowManCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${itemDetail.rowManAmount}" type="currency" pattern="#,##0.00"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>