<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 原封新券管理管理 -->
	<title><spring:message code="store.original.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			setToday(".createTime");
		});
	</script>
	<style>
	/* .breadcrumb{height:80px !important;} */
	.breadcrumb{height:100% !important;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 原封箱管理列表 -->
		<li class="active"><a href=""><spring:message code="store.original.boxList"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoOriginalBanknote" action="${ctx}/allocation/v02/pbocOriginalBoxMgr" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<div class="row">
		
		<div class="span" style="margin-top:5px">
			<!-- 原封箱流水 -->
			<label><spring:message code="store.original.boxNo"/>：</label>
			<form:input path="id" htmlEscape="false" maxlength="64" class="input-small"/>
		</div>
		
		<div class="span" style="margin-top:5px">
			<!-- 入库日期 -->
			<label><spring:message code="store.original.inDate"/>：</label>
			<input id="createDate"  name="createDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate" 
				   value="<fmt:formatDate value="${stoOriginalBanknote.createDate}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});"/>
		</div>
	
		<div class="span" style="margin-top:5px">
			<!-- 出库日期 -->
			<label><spring:message code="store.original.outDate"/>：</label>
			<label><input id="outDate"  name="outDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate" 
				   value="<fmt:formatDate value="${stoOriginalBanknote.outDate}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});"/></label>
		</div>
		
		<div class="span" style="margin-top:5px">
			<!-- 是否回收 -->
			<label><spring:message code="store.original.isOrNo"/>：</label>
			<label><form:select path="recoverStatus" class="input-medium">
				<option value=""><spring:message code="common.select" /></option>
				<form:options items="${fns:getDictList('sto_original_recover_status')}" itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select></label>
			<!-- 查询 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search"/>" />
		</div>
	</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 原封箱流水 --%>
				<th><spring:message code="store.original.boxNo"/></th>
				<%-- 原封券翻译 --%>
				<th><spring:message code="store.original.translation"/></th>
				<%-- 物品名称 --%>
				<th><spring:message code="store.goodsName" /></th>
				<%-- 金额 --%>
				<th class="sort-column a.amount"><spring:message code="common.amount"/></th>
				<%-- 入库流水单号 --%>
				<th class="sort-column a.in_id"><spring:message code='store.inStoreAllId'/></th>
				<%-- 入库时间 --%>
				<th class="sort-column a.create_date"><spring:message code='store.inStoreDateTime'/></th>
				<%-- 出库流水单号 --%>
				<th class="sort-column a.out_id"><spring:message code='store.outStoreAllId'/></th>
				<%-- 出库时间 --%>
				<th class="sort-column a.out_date"><spring:message code='store.outStoreDateTime'/></th>
				<%-- 是否回收 --%>
				<th class="sort-column a.recover_status"><spring:message code="store.original.isOrNo"/></th>
				<%-- 上缴机构 --%>
				<th><spring:message code="store.original.recoverOffice"/></th>
				<%-- 上缴时间 --%>
				<th class="sort-column a.recover_date"><spring:message code="store.original.recoverDate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoOriginalBanknote" varStatus="status">
			<c:choose>
				<c:when test="${stoOriginalBanknote.recoverStatus =='0' }">
					<tr style="background-color: #ffff00;">
				</c:when>
				<c:otherwise>
					<tr>
				</c:otherwise>
			</c:choose>
			
				<%-- 原封箱流水 --%>
				<td>${stoOriginalBanknote.id}</td>
				<%-- 原封券翻译 --%>
				<td>${stoOriginalBanknote.originalTranslate}</td>
				<%-- 物品名称 --%>
				<td style="text-align:left;">${stoOriginalBanknote.goodsName}</td>
				<%-- 金额 --%>
				<td style="text-align:right;"><fmt:formatNumber value="${stoOriginalBanknote.amount}" type="currency" pattern="#,#00.00#"/></td>
				<%-- 入库流水单号 --%>
				<td><a href="${ctx}/allocation/v02/pbocOriginalBoxMgr/toListPage?allId=${stoOriginalBanknote.inId}">${stoOriginalBanknote.inId}</a></td>
				<%-- 入库时间 --%>
				<td><fmt:formatDate value="${stoOriginalBanknote.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<%-- 出库流水单号 --%>
				<td><a href="${ctx}/allocation/v02/pbocOriginalBoxMgr/toListPage?allId=${stoOriginalBanknote.outId}">${stoOriginalBanknote.outId}</a></td>
				<%-- 出库时间 --%>
				<td><fmt:formatDate value="${stoOriginalBanknote.outDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<%-- 是否回收 --%>
				<td>${fns:getDictLabel(stoOriginalBanknote.recoverStatus,'sto_original_recover_status',"")}</td>
				<%-- 上缴机构 --%>
				<td>${stoOriginalBanknote.hoffice.name}</td>
				<%-- 上缴时间 --%>
				<td><fmt:formatDate value="${stoOriginalBanknote.recoverDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>