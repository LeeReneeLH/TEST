<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 库存管理 标题 -->
	<title><spring:message code="store.stockManage"/></title>
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
		<!-- 库存列表 -->
		<li class="active"><a href="${ctx}/store/stoStoresInfo/"><spring:message code="store.stockList"/></a></li>
		<!-- 库存变更列表 -->
		<li><a href="${ctx}/store/stoStoresInfo/historyList"><spring:message code="store.stockHistorylist"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoStoresInfo" action="${ctx}/store/stoStoresInfo/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 物品编号 -->
		<label><spring:message code="store.goodsID" />：</label>
		<form:input path="goodsId" htmlEscape="false" maxlength="12" class="input-small number" />
		<%-- 库存日期 --%>
		<label><spring:message code="store.stockDate"/>：</label>
		<input id="createDate"  name="createDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoStoresInfo.createDate}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd'});"/>
		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" />
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 物品编号 -->
				<th><spring:message code="store.goodsID" /></th>
				<!-- 物品名称 -->
				<th><spring:message code="store.goodsName" /></th>
				<!-- 库存数量 -->
				<th><spring:message code="store.stockNum"/></th>
				<!-- 总价值 -->
				<th><spring:message code="store.totalValue"/></th>
				<!-- 所属机构 -->
				<th><spring:message code="store.affiliatedOffice"/></th>
				<!-- 物品类型 -->
				<th><spring:message code="store.goodsType"/></th>
				
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoStoresInfo" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${stoStoresInfo.goodsId}</td>
				<td>${stoStoresInfo.goodsName}</td>
				<td>${stoStoresInfo.stoNum}</td>
				<td>${stoStoresInfo.amount}</td>
				<td>${stoStoresInfo.office.name}</td>
				<td>${fns:getDictLabel(stoStoresInfo.goodsType,'good_type',"")}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
