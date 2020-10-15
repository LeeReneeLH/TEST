<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code="door.equipment.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(
			function() {
				$("#exportSubmit").on('click',
					function() {
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/doorErrorInfo/exportMerchantList");
						$("#searchForm").submit();
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/doorErrorInfo/list");
						});
				});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 差错列表 -->
		<shiro:hasPermission name="doororder:doorErrorInfo:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/"><spring:message code="door.errorInfo.merchantList" /></a></li></shiro:hasPermission>
	    <shiro:hasPermission name="doororder:doorErrorList:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/doorList"><spring:message code="door.errorInfo.doorList" /></a></li></shiro:hasPermission>
	    <shiro:hasPermission name="doororder:doorErrorDetailList:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList"><spring:message code="door.errorInfo.doorDetailList" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="doorErrorInfo" action="${ctx}/doorOrder/v01/doorErrorInfo/merchantList" method="post" class="breadcrumb form-search ">
		<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row  search-flex">
			<!-- <div class="span14" style="margin-top:5px"> -->
				<%-- 门店 --%>
				<div>
					<label><spring:message code="door.errorInfo.merchant" />：</label>
					<sys:treeselect id="merchantId" name="merchantId"
							value="${doorErrorInfo.merchantId}" labelName="merchantName"
							labelValue="${doorErrorInfo.merchantName}" title="<spring:message code='door.public.cust' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="9" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="false" clearCenterFilter="true"/>
				</div>
				<!-- 类别(差错类型)-->
				<div>				
					<label><spring:message code="door.errorInfo.type" />：</label>
					<form:select path="errorType" class="input-medium">
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${fns:getFilterDictList('clear_error_type',false,'1')}" 
									itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<!-- 开始日期 -->
				<div>
					<label><spring:message code="door.errorInfo.createDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						value="<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				</div>
				<!-- 结束日期 -->
				<div>
					<label>~</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						value="<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				</div>
				&nbsp;&nbsp;
				<%-- 查询 --%>
				<div>
					<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
				&nbsp;&nbsp;
				<%-- 导出 --%>
				<div>	
					<input id="exportSubmit" class="btn btn-red" type="button"
						value="<spring:message code='common.export'/>" />
				</div>
				<!-- </div> -->
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
			<%-- 商户名称 --%>
			<th class="sort-column mc.id"><spring:message code="door.errorInfo.merchantName" /></th> 
			<%-- 差错类型 --%>
			<th><spring:message code="door.errorInfo.type" /></th>
			<%-- 差额合计 --%>
			<th style="text-align: center;"   class="sort-column merchantAmount"><spring:message code="door.errorInfo.diffAmount" />合计</th>
			<%-- 操作 --%>
			<th style="text-align: center;"><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="doorError" varStatus="status">
			<tr>
				<td>
				   <c:if test="${status.index eq 0}"></c:if>
                    <c:if test="${status.index ne 0}">
                    ${status.index}
           			 </c:if>
            	</td>
				<td>
				 <c:if test="${status.index eq 0}">${doorError.merchantName}</c:if>
				 <c:if test="${status.index ne 0}">
                   <a href="${ctx}/doorOrder/v01/doorErrorInfo/doorList?officeId=${doorError.merchantId}&status=0&createTimeStart=<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>&createTimeEnd=<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>" >${doorError.merchantName}</a>
				 </c:if>
				</td>
				<td>${fns:getDictLabel(doorError.errorType,'clear_error_type',"")}</td>
			 	<td style="text-align:right;"><fmt:formatNumber value="${doorError.merchantAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align: center;">
					<c:if test="${status.index eq 0}"></c:if>
					<c:if test="${status.index ne 0}">
	                      <a href="${ctx}/doorOrder/v01/doorErrorInfo/doorList?officeId=${doorError.merchantId}&status=0&createTimeStart=<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>&createTimeEnd=<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>" title="查看"><i class="fa fa-eye fa-lg"></i></a>
					 </c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>