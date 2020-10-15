<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="door.historyChange.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyChange/exportDetail");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyChange/historyChangeInfo");		
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<!-- 历史更换列表 -->
		<li><a href="${ctx}/doorOrder/v01/historyChange/"><spring:message
					code="door.historyChange.list" /></a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/historyChange/historyChangeInfo?equipmentId=${historyChange.equipmentId}"><spring:message
					code="door.historyChange.detail" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="historyChange" action="${ctx}/doorOrder/v01/historyChange/historyChangeInfo" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="equipmentId" name="equipmentId" type="hidden" value="${historyChange.equipmentId}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<div>
			<%-- CIT-解款员--%>
			<label><spring:message code="door.historyChange.payPeople" />: </label>
				<form:input path="payPeople" htmlEscape="false" maxlength="200" class="input-small"/>
			</div>
			<!-- 钞袋号 -->
			<div>
			<label><spring:message code="door.historyChange.bagNo" />：</label>
			<form:input path="bagNo" htmlEscape="false" maxlength="20" class="input-small" />
			</div>
			<%-- 开始日期 --%>
			<div>
			<label><spring:message code="door.historyChange.changeDate" />：</label> <input
				id="createTimeStart" name="createTimeStart" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${historyChange.createTimeStart}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<%-- 结束日期 --%>
			<div>
			<label>~</label> <input
				id="createTimeEnd" name="createTimeEnd" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${historyChange.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
			&nbsp;&nbsp;
			<%-- <a href="${ctx}/doorOrder/v01/historyChange/ExportDetail?equipmentId=${historyChange.equipmentId}"> --%>
			<!-- 导出 -->
			<div>
			<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
			</div>
			&nbsp;&nbsp;
			<%-- 返回 --%>
			<div>
				<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
					onclick="history.go(-1)"/>
					<%-- onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/> --%>
			</div>
			<!-- </a> -->
			
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			    <%-- 序号--%>
				<th><spring:message code="common.seqNo" /></th>
				<%--机具编号--%>
				<th class="sort-column ei.series_number"><spring:message code="door.historyChange.no" /></th>
				<%-- 区域--%>
				<th class="sort-column pr.NAME"><spring:message code="door.historyChange.area" /></th>
				<%-- CIT-解款员--%>
				<th class="sort-column a.payPeople"><spring:message code="door.historyChange.payPeople" /></th>
				<%-- 袋号--%>
				<th class="sort-column a.bagNo"><spring:message code="door.historyChange.bagNo" /></th>
				<%-- 替换钞袋时间--%>
				<th class="sort-column changeDate"><spring:message code="door.historyChange.changeDate" /></th>
				<%-- 批次号--%>
				<th><spring:message code="door.historyChange.batchNo" /></th>
				<%-- 纸币数量--%>
				<th><spring:message code="door.historyChange.paperCount" /></th>
				<%-- 纸币金额--%>
				<th><spring:message code="door.historyChange.paperAmount" /></th>
				<%-- 强制金额--%>
				<th><spring:message code="door.historyChange.forceAmount" /></th>
				<%-- 其他金额 --%>
				<th><spring:message code="door.historyChange.otherAmount" /></th>
				<%-- 数量--%>
				<th><spring:message code="door.historyChange.count" /></th>
				<%-- 金额--%>
				<th><spring:message code="door.historyChange.amount" /></th>
				<%-- 所属公司--%>
				<th class="sort-column do.name"><spring:message code="door.historyChange.office" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="historyChange" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>
					${historyChange.seriesNumber}
				</td>
				<td>
					${historyChange.area }
				</td>
				<td>
					${historyChange.payPeople}
				</td>
				<td>
					${historyChange.bagNo }
				</td>
				<td><fmt:formatDate value="${historyChange.changeDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<td>
					${historyChange.batchNo }
				</td>
				<td>
					${historyChange.paperMoneyCount }
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${historyChange.paperMoney }" pattern="#,##0.00#" />
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${historyChange.comperMoney }" pattern="#,##0.00#" />
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${historyChange.otherMoney }" pattern="#,##0.00#" />
				</td>
				<td>
					${historyChange.count }
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${historyChange.amount }" pattern="#,##0.00#" />
				</td>
				<td>
					${historyChange.doorName }
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
	<%-- <div class="form-actions" style="width:100%">
	返回
	<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
					onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/>
	</div> --%>
</body>
</html>