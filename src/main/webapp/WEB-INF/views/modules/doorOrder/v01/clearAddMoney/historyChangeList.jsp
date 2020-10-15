<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="door.historyChange.list" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	$(document).ready(
		function() {
			$("#exportSubmit").on('click',
				function() {
					$("#searchForm").prop("action","${ctx}/doorOrder/v01/historyChange/exportExcel");
					$("#searchForm").submit();
					$("#searchForm").prop("action","${ctx}/doorOrder/v01/historyChange/list");
					});
			});
	$(document).ready(function() {

	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 历史更换记录 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/historyChange/"><spring:message
					code="door.historyChange.list" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="historyChange" action="${ctx}/doorOrder/v01/historyChange/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<div>
			<!-- 机具编号-->
			<label><spring:message code="door.historyChange.no" />：</label>
			<form:input path="seriesNumber" htmlEscape="false" maxlength="20" class="input-small" />
			</div>
			<!-- 钞袋号 -->
			<div>
			<label><spring:message code="door.historyChange.bagNo" />：</label>
			<form:input path="bagNo" htmlEscape="false" maxlength="20" class="input-small" />
			</div>
			<%-- 更换日期 --%>
			<div>
			<label><spring:message code="door.historyChange.timeStart" />：</label>
			<input id="createTimeStart" name="createTimeStart" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${historyChange.createTimeStart}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<%-- 结束日期 --%>
			<div>
			<label>~</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${historyChange.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<!-- 所属公司 -->
			<div>
			<label><spring:message code="door.historyChange.office" />：</label>
			<sys:treeselect id="doorName" name="doorId"
				value="${historyChange.doorId}" labelName="doorName"
				labelValue="${historyChange.doorName}"
				title="<spring:message code='door.public.cust' />"
				url="/sys/office/treeData" cssClass="required input-small"
				notAllowSelectParent="false" notAllowSelectRoot="false" minType="8"
				maxType="9" isAll="true" allowClear="true"
				checkMerchantOffice="true" clearCenterFilter="true" />
			</div>
			<!-- 按钮 -->
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
			</div>
			&nbsp;&nbsp;
			<%-- 导出 --%>
			<div>
			<input id="exportSubmit" class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
			</div>
		</div>
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 机具编号 --%>
				<th class="sort-column ei.SERIES_NUMBER"><spring:message code="door.historyChange.no" /></th>
				<%-- 区域 --%>
				<th class="sort-column pr.NAME"><spring:message code="door.historyChange.area" /></th>
				<%-- 前一钞袋号 --%>
				<th class="sort-column a3.lastBagNo"><spring:message code="door.historyChange.lastBagNo" /></th>
				<%-- 钞袋号--%>
				<th class="sort-column a5.BAG_NO"><spring:message code="door.historyChange.bagNo" /></th>
				<%--CIT-解款员 --%>
				<th class="sort-column s.NAME"><spring:message code="door.historyChange.payPeople" /></th>
				<%-- 批次号 --%>
				<th><spring:message code="door.historyChange.batchNo" /></th>
				<%-- 纸币数量 --%>
				<th><spring:message code="door.historyChange.paperCount" /></th>
				<%-- 纸币金额 --%>
				<th><spring:message code="door.historyChange.paperAmount" /></th>
				<%-- 强制金额 --%>
				<th><spring:message code="door.historyChange.forceAmount" /></th>
				<%-- 其他金额 --%>
				<th><spring:message code="door.historyChange.otherAmount" /></th>
				<%-- 替换钞袋时间 --%>
				<th class="sort-column a5.change_date"><spring:message code="door.historyChange.changeDate" /></th>
				<%-- 数量 --%>
				<th><spring:message code="door.historyChange.count" /></th>
				<%-- 金额 --%>
				<th><spring:message code="door.historyChange.amount" /></th>
				<%-- 所属公司 --%>
				<th class="sort-column DO.NAME"><spring:message code="door.historyChange.office" /></th>
				<%-- 操作 --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="historyChange"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${historyChange.seriesNumber}</td>
					<td>${historyChange.area}</td>
					<td>${historyChange.lastBagNo}</td>
					<td>${historyChange.bagNo}</td>
					<td>${historyChange.payPeople}</td>
					<td>${historyChange.batchNo}</td>
					<td>${historyChange.paperMoneyCount}</td>
					<td style="text-align:right;"><fmt:formatNumber value="${historyChange.paperMoney}" pattern="#,##0.00#" /></td>
				    <td style="text-align:right;"><fmt:formatNumber value="${historyChange.comperMoney}" pattern="#,##0.00#" /></td>
				    <td style="text-align:right;"><fmt:formatNumber value="${historyChange.otherMoney}" pattern="#,##0.00#" /></td>
					<td><fmt:formatDate value="${historyChange.changeDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${historyChange.count}</td>
					<td style="text-align:right;"><fmt:formatNumber value="${historyChange.amount}"
							pattern="#,##0.00#" /></td>
					<td>${historyChange.doorName}</td>
					<td style="text-align: center;"><a
						href="${ctx}/doorOrder/v01/historyChange/historyChangeInfo?equipmentId=${historyChange.equipmentId}">
							<i class="fa fa-eye fa-lg"></i>
					</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>