<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 盘点管理 标题 -->
	<title><spring:message code="store.stockTacking.manage"/></title>
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
		<!-- 盘点列表 -->
		<li class="active"><a href="${ctx}/store/stoStockCountInfo/"><spring:message code="store.stockTacking.list"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoStockCountInfo" action="${ctx}/store/stoStockCountInfo/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 盘点编号 -->
		<label><spring:message code="store.stockTackingNo"/>：</label>
		<form:input path="stockCountNo" htmlEscape="false" maxlength="18" class="input-medium number" />
		<!-- 物品编号 -->
		<label><spring:message code="store.goodsID"/>：</label>
		<form:input path="goodsId" htmlEscape="false" maxlength="12" class="input-small number" />
		<!-- 物品类型 -->
		<label><spring:message code='store.goodsType'/>：</label>
		<form:select path="stockCountType" class="input-small">
			<!-- 请选择 -->
			<form:option value=""><spring:message code="common.select" /></form:option>
		    <form:options items="${fns:getDictList('good_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoStockCountInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${stoStockCountInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		
		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" />
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 盘点编号 -->
				<th><spring:message code="store.stockTackingNo"/></th>
				<!-- 物品编号 -->
				<th class="sort-column a.goods_id"><spring:message code="store.goodsID" /></th>
				<!-- 物品名称 -->
				<th><spring:message code="store.goodsName" /></th>
				<!-- 物品类型 -->
				<th><spring:message code="store.goodsType"/></th>
				<!-- 物品数量 -->
				<th><spring:message code="store.goodsNum"/></th>
				<!-- 所属机构 -->
				<th><spring:message code="store.affiliatedOffice"/></th>
				<!-- 盘点人 -->
				<th><spring:message code="store.stockTacking.user"/></th>
				<!-- 盘点时间 -->
				<th><spring:message code="store.stockTacking.date"/></th>
				<!-- 库存更新人 -->
				<th><spring:message code="store.stockTacking.updateUser"/></th>
				<!-- 更新时间 -->
				<th><spring:message code="store.updateDate"/></th>
				<!-- 授权人 -->
				<th><spring:message code="common.authorizer"/></th>
				<!-- 盘点状态 -->
				<th><spring:message code="store.stockTacking.status"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoStockCountInfo" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${stoStockCountInfo.stockCountNo}</td>
				<td>${stoStockCountInfo.goodsId}</td>
				<td>${stoStockCountInfo.goodsName}</td>
				<td>${fns:getDictLabel(stoStockCountInfo.stockCountType,'good_type',"")}</td>
				<td>${stoStockCountInfo.stockCountNum}</td>
				<td>${stoStockCountInfo.office.name}</td>
				<td>${stoStockCountInfo.createName}</td>
				<td><fmt:formatDate value="${stoStockCountInfo.createDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td>${stoStockCountInfo.updateName}</td>
				<td><fmt:formatDate value="${stoStockCountInfo.updateDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td>${stoStockCountInfo.managerUsername}</td>
				<td>${fns:getDictLabel(stoStockCountInfo.updateFlag,'stock_count_status',"")}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
