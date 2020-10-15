<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">	
</script>

</head>
<body>
	<table id="contentListTable" class="table table-hover" style="width: 1100px">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
			    <!-- 柜员姓名 -->
				<th><spring:message code='clear.tellerAccount.tellerName'/></th>
				<!-- 余额 -->
				<th><spring:message code='clear.public.balance'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${tellerAccounts}" var="tellerAccountsMain"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${tellerAccountsMain.tellerName}</td>
					<td>
						<c:choose>
							<c:when test="${tellerAccountsMain.totalAmount==null}">
							<fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${tellerAccountsMain.totalAmount}" 
							pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>