<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 原封新券管理 -->
	<title><spring:message code="store.original.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			setToday(".createTime");
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 原封新券出库列表 -->
		<li class="active"><a href="#"><spring:message code="store.original.outList"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoOriginalBanknote" action="${ctx}/v02/stoOriginalBanknote/outList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div class="row">
			<div class="span">
				<!-- 流水单号 -->
				<label><spring:message code="common.serial.orderNo"/>：</label>
				<form:input path="outId" htmlEscape="false" maxlength="64" class="input-small"/>
			</div>
			<div class="span">
				<!-- 出库机构 -->
				<label><spring:message code="store.original.outOffice"/>：</label>
				<sys:treeselect id="coffice" name="coffice.id"
						value="${stoOriginalBanknote.coffice.id}" labelName="coffice.name"
						labelValue="${stoOriginalBanknote.coffice.name}" title="机构"
						url="/sys/office/treeData" notAllowSelectRoot="false"
						notAllowSelectParent="false" allowClear="true"
						cssClass="input-medium" />
			</div>
			<div class="span">
				<!-- 出库操作人 -->
				<label><spring:message code="store.original.outUser"/>：</label>
				<form:input path="outName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</div>
			<div class="span">
				<!-- 出库日期 -->
				<label><spring:message code="store.original.outDate"/>：</label>
				<input id="outDate"  name="outDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					   value="<fmt:formatDate value="${stoOriginalBanknote.outDate}" pattern="yyyy-MM-dd"/>" 
					   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});"/>
				<!-- 查询 -->
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search"/>" />
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 流水单号 -->
				<th><spring:message code="common.serial.orderNo"/></th>
				<!-- 出库机构 -->
				<th><spring:message code="store.original.outOffice"/></th>
				<!-- 金额 -->
				<th><spring:message code="common.amount"/></th>
				<!-- 出库操作人 -->
				<th><spring:message code="store.original.outUser"/></th>
				<!-- 出库操时间 -->
				<th><spring:message code="store.original.outDate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoOriginalBanknote" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td><a href="${ctx}/v02/stoOriginalBanknote/findDetail?outId=${stoOriginalBanknote.outId}&totalAmount=${stoOriginalBanknote.totalAmount}&cofficeName=${stoOriginalBanknote.cofficeName}">${stoOriginalBanknote.outId}</a></td>
				<td>${stoOriginalBanknote.cofficeName}</td>
				<td><fmt:formatNumber value="${stoOriginalBanknote.totalAmount}" type="currency" pattern="#,#00.00#"/></td>
				<td>${stoOriginalBanknote.outName}</td>
				<td><fmt:formatDate value="${stoOriginalBanknote.outDate}" pattern="yyyy-MM-dd" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>