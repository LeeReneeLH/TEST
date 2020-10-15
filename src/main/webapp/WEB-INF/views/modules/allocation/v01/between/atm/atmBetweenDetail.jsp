<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="title.atm.inout.detail" /></title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/allocation/v01/atmBetween"><spring:message code='allocation.atm.box.list'/></a></li>
        <%-- atm出入库明细 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.atm.box.detail" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="" method="post" class="form-horizontal">
		<table id="contentTable" class="table table-hover">
			<thead>
				<tr>
					<%-- 日期 --%>
					<th style="text-align:center"><spring:message code='common.date'/></th>
					<%-- 出入库 --%>
					<th style="text-align:center"><spring:message code='allocation.inOut'/></th>
					<%-- 钞箱类型 --%>
					<th style="text-align:center"><spring:message code='common.atmBoxType'/></th>
					<%-- 钞箱编号 --%>
					<th style="text-align:center"><spring:message code='common.atmBoxNo'/></th>
					<%-- 钞箱数量 --%>
					<th style="text-align:center"><spring:message code='common.atmBoxNum'/></th>
					<%-- 移交人 --%>
					<th style="text-align:center"><spring:message code='allocation.userType.handover'/></th>
					<%-- 接收人 --%>
					<th style="text-align:center"><spring:message code='allocation.userType.accepter'/></th>
					<%-- 交接时间 --%>
					<th style="text-align:center"><spring:message code='common.handoverDate'/></th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${allAllocateInfo.allDetailList}" var="allocationDetail" varStatus="status">
				<tr>
					<td style="text-align:center"><fmt:formatDate value="${allocationDetail.allocationInfo.createDate}" pattern="yyyy-MM-dd"/></td>
					<td style="text-align:center">${fns:getDictLabel(allocationDetail.allocationInfo.inoutType,'all_inout_type',"")}</td>
					<td style="text-align:center">${fns:getDictLabel(allocationDetail.boxType,'sto_box_type',"")}</td>
					<td style="text-align:center">${allocationDetail.boxId}</td>
					<td style="text-align:center">1</td>
					<td style="text-align:center">${allocationDetail.allocationInfo.handoverUserName}</td>
					<td style="text-align:center">${allocationDetail.allocationInfo.escortUserName}</td>
					<td style="text-align:center"><fmt:formatDate value="${allocationDetail.allocationInfo.acceptDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</form:form>
	<div class="form-horizontal">
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
		</div>
	</div>
</body>
</html>
