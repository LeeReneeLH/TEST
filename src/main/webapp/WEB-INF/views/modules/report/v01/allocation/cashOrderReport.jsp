<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.cashOrderReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");

			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/allocation/exportCashOrderReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action", "${ctx}/report/v01/allocation/cashOrderReport?searchFlag=true");
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 库房赔款统计表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.cashOrderReport" /></a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="AllAllocateInfo" action="${ctx}/report/v01/allocation/cashOrderReport?searchFlag=true" method="post" class="breadcrumb form-search">
		<%-- 网点名称 --%>
			<label class="control-label"><spring:message code="common.outletsName"/>：</label>
			<sys:treeselect id="rOfficeId" name="rOffice.id" 		
	 			value="${allAllocateInfo.rOffice.id}" labelName="rOffice.name"  labelValue="${allAllocateInfo.rOffice.name}"
	 			title="机构" 
	 			url="/sys/office/treeData?type=4" 
	 			cssClass="required input-small" allowClear="true" notAllowSelectParent="false" />

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
				<%-- 网点名称 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.outletsName'/></th>
				<%-- 物品名称 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='store.goodsName'/></th>
				<%-- 配款数量 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='allocation.cash.quota.number'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="allocationInfo" >
			<c:forEach items="${allocationInfo.reportInfoList}" var="reportInfo" varStatus="status">
				<c:choose>
					<c:when test="${ not empty reportInfo.time }">
						<tr>
							<td style="text-align:center">${reportInfo.time}</td>
							<td style="text-align:center">${reportInfo.officeName}</td>
							<td style="text-align:center">${reportInfo.goodsName}</td>
							<td style="text-align:center">${reportInfo.moneyNumber}</td>
						</tr>	
					</c:when>
				</c:choose>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
