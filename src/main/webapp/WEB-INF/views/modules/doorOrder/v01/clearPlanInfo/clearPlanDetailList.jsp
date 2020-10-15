<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="door.clearPlan.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<!-- 清机任务列表 -->
		<li><a href="${ctx}/doorOrder/v01/clearPlanInfo/"><spring:message code="door.clearPlan.list" /></a></li>
		<shiro:hasPermission name="doorOrder:v01:clearPlanInfo:edit">
			<li><a href="${ctx}/doorOrder/v01/clearPlanInfo/form"><spring:message code="door.clearPlan.form" /></a></li>
		</shiro:hasPermission>
		<li class="active"><a><spring:message code="door.clearPlan.detail" /></a></li>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
			<%-- 人员名称 --%>
			<th><spring:message code="door.clearPlan.perName" /></th>
			<%-- 门店 --%>
			<th><spring:message code="door.public.cust" /></th>
			<%-- 清机状态--%>
			<th><spring:message code="door.clearPlan.status" /></th>
			<%-- 操作 --%>
			<%-- <th><spring:message code='common.operation'/></th> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ClearPlanDetail" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
			 	<td>${ClearPlanDetail.clearManName}</td>
			 	<td>${ClearPlanDetail.doorName}</td>
			 	<td>
				    <c:if test="${ClearPlanDetail.status eq 1}">
						<spring:message code="door.clearPlan.unfinished"/>
					</c:if>
					<c:if test="${ClearPlanDetail.status ne 1}">
						<spring:message code="door.clearPlan.finished"/>
					</c:if>
				</td>
				<%-- <td><fmt:formatDate value="${ClearPlanInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>