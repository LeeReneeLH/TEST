<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='allocation.atm.box.out'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='allocation.atm.box.list'/></a></li>
	</ul>
	<tags:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="${ctx}/allocation/v01/atmOut/list?searchFlag=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<label><spring:message code='allocation.inOut.type'/>：</label>
		<%-- 出入库类型 --%>
		<form:select path="inoutType" id="inoutType" class="input-medium" >
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('all_inout_type')}"
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
	</form:form>

	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 交接日期 --%>
				<th class="sort-column a.create_date"><spring:message code='common.handoverDate'/></th>
				<%-- 出入库类型 --%>
				<th class="sort-column a.inout_type"><spring:message code='allocation.inOut.type'/></th>
				<%-- 交接数量 --%>
				<th><spring:message code='allocation.handover.number'/></th>
				<%-- 加钞金额 --%>
				<th><spring:message code='label.atm.add.plan.amount'/><spring:message code='common.units.yuan'/></th>
				<%-- 移交人 --%>
				<th><spring:message code='allocation.userType.handover'/></th>
				<%-- 接收人 --%>
				<th><spring:message code='allocation.userType.accepter'/></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code='allocation.business.status'/></th>
				<%-- 流水号 --%>
				<th class="sort-column a.add_plan_id"><spring:message code='allocation.allId'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmInfo">
			<tr>
				<td><fmt:formatDate value="${atmInfo.createDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td>${fns:getDictLabel(atmInfo.inoutType,'all_inout_type',"")}</td>
				<td>${atmInfo.registerNumber}</td>
				<td><fmt:formatNumber value="${atmInfo.addAmount}" pattern="#,##0.00#"/></td>
				<td>${atmInfo.handoverUserName}</td>
				<td>${atmInfo.escortUserName}</td>
				<td>${fns:getDictLabel(atmInfo.status,'all_status',"")}</td>
				<td><a href="${ctx}/allocation/v01/atmOut/form?allId=${atmInfo.allId}">${atmInfo.allId}</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
