<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code="door.equipment.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(document).ready(
			function() {
				$("#exportSubmit").on('click',
					function() {
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/doorErrorInfo/exportExcel");
						$("#searchForm").submit();
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/doorErrorInfo/errorDetail");
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
		<li><a onclick="window.top.clickSelect();" href="${ctx}/report/v01/manageReport/errorCollectSituation"><spring:message code='report.manageReport.errorCollectSituation'/></a></li>
		<!-- 差错列表 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail"><spring:message code='report.manageReport.errorDetail'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="doorErrorInfo" action="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail" method="post" class="breadcrumb form-search ">
		<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="errorType" name="errorType" type="hidden" value="${doorErrorInfo.errorType}"/>
		<input id="officeId" name="officeId" type="hidden" value="${doorErrorInfo.custNo}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<!-- <div class="span14" style="margin-top:5px"> -->
			<%-- 拆箱单号 --%>
			<div>
				<label><spring:message code="door.checkCash.codeNo" />：</label>
				<form:input path="businessId" htmlEscape="false" maxlength="32" class="input-small"/>
			</div>
			&nbsp;
			<%-- 门店 --%>
			<div  style="display:none;">
				<label><spring:message code="door.public.custName" />：</label>
				<sys:treeselect id="custNo" name="custNo"
						value="${doorErrorInfo.custNo}" labelName="custName"
						labelValue="${doorErrorInfo.custName}" title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData" cssClass="required input-small"
						notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
						isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
			</div>
			&nbsp;
			<%-- 清分人员 --%>
			<%-- <div>
				<label><spring:message code="door.checkCash.clearMan" /> ：</label>
				<form:select path="clearManNo" id="clearManNo" class="input-medium required" style="font-size:15px;color:#000000">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${sto:getUsersByTypeAndOffice(ConClear,fns:getUser().getOffice().getId())}" itemLabel="escortName" itemValue="user.id" htmlEscape="false" />
				</form:select>
			</div>
			&nbsp;
			<!-- 类别-->
			<div>
				<label><spring:message code='commom.errorType'/>：</label>
				<form:select path="errorType" class="input-medium">
					<form:option value=""><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getFilterDictList('clear_error_type',false,'1')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div> --%>
			&nbsp;
			<!-- 开始日期 -->
			<div>
				<label>登记日期：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			&nbsp;
			<!-- 结束日期 -->
			<div>
				<label>~&nbsp;&nbsp;</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<%-- 查询 --%>
			&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
			&nbsp;
			<div>
			<%-- 导出 --%>
			<input id="exportSubmit" class="btn btn-red" type="button"
				value="<spring:message code='common.export'/>" />
			</div>
			&nbsp;
			 <div >
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.history.back()"/>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
		<table id="contentTable" class="table table-hover">
			<thead>
				<tr>
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 拆箱单号 --%>
				<th class="sort-column a.business_id">拆箱单号</th>
				<%-- 门店名称 --%>
				<th class="sort-column cust.name">门店名称</th>
				<%-- 清分中心 --%>
				<th class="sort-column o.name">清分中心</th>
				<%-- 差错类型 --%>
				<th class="sort-column a.error_type">差错类型</th>
				<%-- 录入金额 --%>
			<!-- 	<th>录入金额</th> -->
				<%-- 清分金额 --%>
				<!-- <th>清分金额</th> -->
				<%-- 差额 --%>
				<th class="sort-column a.diff_amount">差额</th>
				<%-- 清分人员 --%>
				<th class="sort-column a.clear_man_name">清分人员</th>
				<%-- 确认人 --%>
				<th class="sort-column a.makesure_man_name">确认人</th>
				<%-- 登记日期--%>
				<th class="sort-column a.create_date">登记日期</th>
				<%-- 状态 --%>
				<th class="sort-column a.status">状态</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="doorErrorInfo" varStatus="status">
				<tr>
					<td>${status.index + 1}</td>
					<td>${doorErrorInfo.businessId}</td>
					<td>${doorErrorInfo.custName}</td>
					<td>${doorErrorInfo.office.name}</td>
					<td>${fns:getDictLabel(doorErrorInfo.errorType,'clear_error_type',"")}</td>
					<%-- <td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.inputAmount}" type="currency" pattern="#,##0.00"/></td>
					<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.checkAmount}" type="currency" pattern="#,##0.00"/></td> --%>
				 	<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.diffAmount}" type="currency" pattern="#,##0.00"/></td>
				 	<td>${doorErrorInfo.clearManName}</td>
				 	<td>${doorErrorInfo.makesureManName}</td>
				 	<td><fmt:formatDate value="${doorErrorInfo.registerDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${fns:getDictLabelWithCss(doorErrorInfo.status,'check_error_status',"未命名",true)}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
	 <%--  <div class="form-actions" style="width:100%">
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.history.back()"/>
		</div> --%>
</body>
</html>