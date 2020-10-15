<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.importantEmptyChangeReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");
			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/store/exportEmptyChangeReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action", "${ctx}/report/v01/store/emptyChangeReport");
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 库房赔款统计表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.importantEmptyChangeReport" /></a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="stoReportInfo" action="${ctx}/report/v01/store/emptyChangeReport" method="post" class="breadcrumb form-search">
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoReportInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${stoReportInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		<%-- 查询按钮 --%>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		&nbsp;<input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.export'/>"/>
	</form:form>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 日期 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='common.date'/></th>
				<!-- 变更类型 -->
				<th style="text-align:center;vertical-align:middle;"><spring:message code="store.exchangeType"/></th>
				<%-- 流水号 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='allocation.allId'/></th>
				<%-- 网点名称 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='common.outletsName'/></th>
				<%-- 物品名称 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='store.goodsName'/></th>
				<%-- 变更数量 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='store.exchangeNum'/></th>
				<%-- 变更人 --%>
				<th style="text-align:center;vertical-align:middle;"><spring:message code='store.changeUser'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${emptyHistoryList}" var="reportInfo" varStatus="status">
				<c:choose>
					<c:when test="${ not empty reportInfo.time }">
						<tr>
							<td style="text-align:center">${reportInfo.time}</td>
							<td style="text-align:center">${reportInfo.stoStatusName}</td>
							<td style="text-align:center">${reportInfo.businessId}</td>
							<td style="text-align:center">${reportInfo.office.name}</td>
							<td style="text-align:center">${reportInfo.goodsName}</td>
							<td style="text-align:right">${reportInfo.changeNum}</td>
							<td style="text-align:center">${reportInfo.createName}</td>
						</tr>	
					</c:when>
				</c:choose>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
