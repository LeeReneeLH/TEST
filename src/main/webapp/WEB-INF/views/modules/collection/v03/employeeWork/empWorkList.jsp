<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code='clear.task.distribution.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/collection/v03/empWork/exportExcel");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/collection/v03/empWork");		
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
		<!-- 员工工作量 -->
		<li class="active"><a><spring:message code="door.empWork.title" /></a></li>
		
	</ul>
	<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
	<form:form id="searchForm" modelAttribute="empWork"
		action="${ctx}/collection/v03/empWork" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<%-- 清分人 --%>
		<label class="control-label"><spring:message code="door.empWork.clearMan" />：</label>
		<form:select path="searchClearManNo" id="searchClearManNo" class="input-large required" style="font-size:15px;color:#000000">
			<form:option value=""><spring:message code="common.select" /></form:option>
			<form:options items="${sto:getUsersByTypeAndOffice(ConClear,'')}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
		</form:select>
		
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate" />：</label>
		<input 	id="createTimeStart"  
				name="createTimeStart" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${empWork.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate" />：</label>
		<input 	id="createTimeEnd" 
				name="createTimeEnd" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${empWork.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
		&nbsp;&nbsp;
		<%-- 导出 --%>
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<%-- 序号--%>
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.seqNo" /></th>
				<%-- 清分人--%>
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px" ><spring:message code="door.empWork.clearMan" /></th>
				<%-- 机械清分--%>
				<th colspan="2" style="text-align: center;"><spring:message code="door.empWork.autoClear" /></th>
				<%-- 手工清分--%>
				<th colspan="2" style="text-align: center;"><spring:message code="door.empWork.handClear" /></th>
				<%-- 合计--%>
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.total" /></th>
				<%-- 差错--%>
				<th colspan="2" style="text-align: center;"><spring:message code="door.public.error" /></th>
			</tr>
			<tr>
				<%-- 笔数--%>
				<th style="text-align: center;"><spring:message code="door.empWork.count" /></th>
				<%-- 金额--%>
				<th style="text-align: center;"><spring:message code="door.public.money" /></th>
				<%-- 笔数--%>
				<th style="text-align: center;"><spring:message code="door.empWork.count" /></th>
				<%-- 金额--%>
				<th style="text-align: center;"><spring:message code="door.public.money" /></th>
				<%-- 笔数--%>
				<th style="text-align: center;"><spring:message code="door.empWork.count" /></th>
				<%-- 金额--%>
				<th style="text-align: center;"><spring:message code="door.public.money" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="empWork" varStatus="status">
				<tr>
					<td style="text-align: center;">${status.index+1}</td>
					<td style="text-align: left;">${empWork.empName}</td>
					<td style="text-align: right;">${empWork.machineCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${empWork.machineAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: right;">${empWork.handCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${empWork.handAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: right;"><fmt:formatNumber value="${empWork.sumAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: right;">${empWork.diffCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${empWork.diffAmount}" type="currency" pattern="#,##0.00"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>