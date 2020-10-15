<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='title.add.clear.searsh.manager'/></title><!-- 清机加钞查询管理 -->
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code='common.seqNo'/></th><!-- 序号 -->
				<th><spring:message code='label.atm.box.no'/></th><!-- 钞箱编号 -->				
				<th><spring:message code='label.atm.box.type.name'/></th><!-- 钞箱类型名 -->
				<c:if test="${type eq true}">
					<th><spring:message code='label.atm.day.amount'/><spring:message code="common.units.yuan.alone" /></th><!-- 加钞金额 -->
				</c:if>
				<c:if test="${type eq false}">
					<th><spring:message code='label.atm.clear.count.amount'/><spring:message code="common.units.yuan.alone" /></th><!-- 清点金额 -->
				</c:if>
				<c:if test="${type eq true}">
					<th><spring:message code='label.atm.add.date'/></th><!-- 加钞时间 -->
				</c:if>
				<c:if test="${type eq false}">
					<th><spring:message code='label.atm.clear.count.date'/></th><!-- 清点时间 -->
				</c:if>
				<c:if test="${type eq false}">
					<th><spring:message code='common.status'/></th><!-- 状态 -->
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmBindingDetail" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${atmBindingDetail.boxNo}</td>
				<td>${atmBindingDetail.modName}</td>
				<td>
					<c:if test="${type eq true}">
						<fmt:formatNumber value="${atmBindingDetail.addAmount}" pattern="#,##0.00#"/>
					</c:if>
					<c:if test="${type eq false}">
						<fmt:formatNumber value="${atmBindingDetail.clearAmount}" pattern="#,##0.00#"/>
					</c:if>
				</td>
				<td>
					<c:if test="${type eq true}">
						<fmt:formatDate value="${atmBindingDetail.addDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</c:if>
					<c:if test="${type eq false}">
						<fmt:formatDate value="${atmBindingDetail.clearDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</c:if>
				</td>
				<c:if test="${type eq false}">
					<td>${fns:getDictLabel(atmBindingDetail.delFlag,'atm_binding_detail_delFlag',"")}</td>
				</c:if>	
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>