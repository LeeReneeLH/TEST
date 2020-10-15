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
		<!-- 库存列表 
		<li ><a href="${ctx}/store/stoStoresInfo/"><spring:message code="store.stockList"/></a></li>-->
		<!-- 库存变更列表 -->
		<li class="active"><a href="${ctx}/store/stoStoresInfo/historyList"><spring:message code="store.stockHistorylist"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoStoresHistory" action="${ctx}/store/stoStoresInfo/historyList?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 物品编号 -->
		<label><spring:message code="store.goodsID" />：</label>
		<form:input path="goodsId" htmlEscape="false" maxlength="12" class="input-small number" />
		<!-- 变更类型 -->
		<label><spring:message code="store.exchangeType"/>：</label>
		<form:select path="stoStatus" class="input-medium">
			<!-- 请选择 -->
			<form:option value="">
				<spring:message code="common.select"/>
			</form:option>
			<form:options items="${fns:getDictList('all_businessType')}" 
				itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoStoresHistory.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${stoStoresHistory.createTimeEnd}" pattern="yyyy-MM-dd"/>"
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
				<!-- 业务流水 -->
				<th><spring:message code="store.business.serialorderNo"/></th>
				<!-- 物品编号 -->
				<th class="sort-column a.goods_id"><spring:message code="store.goodsID" /></th>
				<!-- 物品名称 -->
				<th><spring:message code="store.goodsName" /></th>
				<!-- 原库存数量 -->
				<th><spring:message code="store.primitiveNum"/></th>
				<!-- 变更数量 -->
				<th><spring:message code="store.exchangeNum"/></th>
				<!-- 现库存数量 -->
				<th><spring:message code="store.nowNum"/></th>
				<!-- 所属机构 -->
				<th><spring:message code="store.affiliatedOffice"/></th>
				<!-- 变更类型 -->
				<th><spring:message code="store.exchangeType"/></th>
				<!-- 物品类型 -->
				<th><spring:message code="store.goodsType"/></th>
				<!-- 变更日期 -->
				<th><spring:message code="store.exchangeDate"/></th>
				<!-- 变更人 -->
				<th><spring:message code="store.changeUser"/></th>
				
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoStoresHistory" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${stoStoresHistory.businessId}</td>
				<td>${stoStoresHistory.goodsId}</td>
				<td>${stoStoresHistory.goodsName}</td>
				<td>${stoStoresHistory.stoNum}</td>
				<td>${stoStoresHistory.changeNum}</td>
				<td>${stoStoresHistory.stoNum+stoStoresHistory.changeNum}</td>
				<td>${stoStoresHistory.office.name}</td>
				<td>${fns:getDictLabel(stoStoresHistory.stoStatus,'all_businessType',"")}</td>
				<td>${fns:getDictLabel(stoStoresHistory.goodsType,'good_type',"")}</td>
				<td><fmt:formatDate value="${stoStoresHistory.createDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td>${stoStoresHistory.createName}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
