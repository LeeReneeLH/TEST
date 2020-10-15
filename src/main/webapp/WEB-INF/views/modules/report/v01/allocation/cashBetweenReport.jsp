<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.cashBetweenReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/allocation/exportCashBetweenReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action", "${ctx}/report/v01/allocation/cashBetweenReport");
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 现金出入库情况统计表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.cashBetweenReport" /></a></li>
        <li><a href="${ctx}/report/v01/allocation/cashBetweenECharts">库间现金调拨图</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="${ctx}/report/v01/allocation/cashBetweenReport" method="post" class="breadcrumb form-search">

		<%-- 业务种别 --%>
		<c:set var="cashBusinessType" value="${fns:getConfig('allocation.cash.businessType')}" />
		<label><spring:message code='allocation.business.type'/>：</label>
		<form:select path="businessType" id="businessType" class="input-small">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getFilterDictList('all_businessType', true, cashBusinessType)}" 
                      itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
         
		<%-- 报表过滤条件 --%>
		<label>过滤条件：</label>
		<form:select path="dateFlag" id="dateFlag" class="input-small">
		    <form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('report_filter_condition')}" 
			          itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>

		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		<%-- 查询按钮 --%>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code='common.export'/>"/>
	</form:form>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 日期 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.date'/></th>
				<%-- 预约机构 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code="allocation.order.office" /></th>
				<%-- 接收机构 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code="allocation.accept.office" /></th>
				<%-- 物品名称 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='store.goodsName'/></th>
				<%-- 数量 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.number'/></th>
				<%-- 金额 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.amount'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="allocation" varStatus="status">
			<c:forEach items="${allocation.reportInfoList}" var="reportInfo" varStatus="status">
				<tr>
					<td style="text-align:center">${reportInfo.time}</td>
					<td style="text-align:center">${reportInfo.rOfficeName}</td>
					<td style="text-align:center">${reportInfo.aOfficeName}</td>
					<td style="text-align:center">${reportInfo.goodsName}</td>
					<td style="text-align:center">${reportInfo.moneyNumber}</td>
					<td style="text-align:center">${reportInfo.moneyAmount}</td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
