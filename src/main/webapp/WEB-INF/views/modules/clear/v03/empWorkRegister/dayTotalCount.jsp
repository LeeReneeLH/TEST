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
			<table id="contentListTable"
								class="table table-hover"
								style="width: 1100px">
								<thead>
									<tr>
										<!-- 面值 -->
										<th style="text-align: center;"><spring:message
												code='common.denomination' /></th>
										<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}"
											var="item">
											<th style="text-align: right;">${item.label}(<spring:message code='clear.public.bundle' />)</th>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<!-- 当日累计分配入库量-->
										<td style="text-align: center;"><spring:message code='clear.empWorkRegister.nowCumulativeIn' /></td>
										<c:forEach items="${holeClTaskMainList}" var="clTaskMain">
											<td style="text-align: center;">${clTaskMain.countBank}</td>
										</c:forEach>
									</tr> 
									
									<tr>
										<!-- 当日累计登记量 -->
										<td style="text-align: center;"><spring:message code='clear.empWorkRegister.nowCumulativeDistribution' /></td>
										<c:forEach items="${holeClTaskRecoveryList}" var="clTaskRecovery">
											<td style="text-align: center;">${clTaskRecovery.totalCount}</td>
										</c:forEach>
									</tr> 
								</tbody>
					</table>
	
</body>
</html>