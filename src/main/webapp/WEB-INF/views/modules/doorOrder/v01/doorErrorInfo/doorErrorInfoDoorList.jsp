
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
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
		
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/doorErrorInfo/doorExport");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/doorErrorInfo/doorList");		
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 商户差错列表 -->
		<%-- <c:if test="${fns:getUser().getOffice().type!='8'}"> --%>
			<shiro:hasPermission name="doororder:doorErrorInfo:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/"><spring:message code="door.errorInfo.merchantList" /></a></li></shiro:hasPermission>
		<%-- </c:if> --%>
		<!-- 门店差错列表 -->
		<shiro:hasPermission name="doororder:doorErrorList:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/doorList?officeId=${doorErrorInfo.officeId}"><spring:message code="door.errorInfo.doorList" /></a></li></shiro:hasPermission>
		<!-- 门店差错明细 -->
		<shiro:hasPermission name="doororder:doorErrorDetailList:view"><li><a onclick="window.top.clickSelect();"href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList"><spring:message code="door.errorInfo.doorDetailList" /></a></li></shiro:hasPermission>
		
	</ul>
	<form:form id="searchForm" modelAttribute="doorErrorInfo" action="${ctx}/doorOrder/v01/doorErrorInfo/doorList" method="post" class="breadcrumb form-search ">
		<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="officeId" name="officeId" type="hidden" value="${doorErrorInfo.officeId}"/>
		<c:if test="${fns:getUser().getOffice().type=='8'}">
			<input id="custNo" name="custNo" type="hidden" value="${doorErrorInfo.custNo}"/>
		</c:if>
		<%-- <input id="userOfficeId" type="hidden" value="${fns:getUser().getOffice().id}"/> --%>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row">
			<div class="span14" style="margin-top:5px">
				<c:if test="${fns:getUser().getOffice().type!='8'}">
					<%-- 门店 --%>
					<label><spring:message code="door.public.cust" />：</label>
					<sys:treeselect id="custNo" name="custNo"
							value="${doorErrorInfo.custNo}" labelName="custName"
							labelValue="${doorErrorInfo.custName}" title="<spring:message code='door.public.cust' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
				</c:if>
				<%-- 清分人员 --%>
				<%-- <label><spring:message code="door.checkCash.clearMan" /> ：</label>
				<form:select path="clearManNo" id="clearManNo" class="input-large required" style="font-size:15px;color:#000000">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${sto:getUsersByTypeAndOffice(ConClear,fns:getUser().getOffice().getId())}" itemLabel="escortName" itemValue="user.id" htmlEscape="false" />
				</form:select> --%>
				<!-- 类别-->
				<label><spring:message code='door.errorInfo.type'/>：</label>
				<form:select path="errorType" class="input-medium">
					<form:option value=""><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getFilterDictList('clear_error_type',false,'1')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<!-- 状态-->
				<label><spring:message code="door.errorInfo.status" />：</label>
				<form:select path="status" class="input-medium" id ="selectStatus">
					<form:option value=""><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getFilterDictList('check_error_status',false,'4')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<!-- 开始日期 -->
				<label><spring:message code="door.errorInfo.createDate" />：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				<!-- 结束日期 -->
				<label>~</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				<%-- 拆箱单号 --%>
				<%-- <label><spring:message code="door.checkCash.codeNo" />：</label>
				<form:input path="businessId" htmlEscape="false" maxlength="32" class="input-small"/> --%>
				<%-- 查询 --%>
				 &nbsp;
				<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				<!-- 导出 -->
				 &nbsp;
				<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
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
			<%-- 门店名称 --%>
			<th class="sort-column cust.name"><spring:message code="door.errorInfo.custName" /></th>
			<%-- 清分中心 --%>
			<th class="sort-column o.name"><spring:message code="door.errorInfo.officeName" /></th>
			<%-- 差错类型 --%>
			<th><spring:message code="door.errorInfo.type" /></th>
			<%-- 录入金额 --%>
			<!-- <th>录入金额</th> -->
			<%-- 清分金额 --%>
			<!-- <th>清分金额</th> -->
			<%-- 差额 --%>
			<th class="sort-column diffAmount"><spring:message code="door.errorInfo.diffAmount" /></th>
			<%-- 清分人员 --%>
			<!-- <th>清分人员</th> -->
			<%-- 确认人 --%>
			<!-- <th>确认人</th> -->
			<%-- 登记日期--%>
			<!-- <th>登记日期</th> -->
			<%-- 状态 --%>
			<th ><spring:message code="door.errorInfo.status" /></th>
			<%-- 操作 --%>
			<th><spring:message code="door.errorInfo.operate" /></th>
			
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="doorErrorInfo1" varStatus="status">
			<tr>
				<%-- <td>${status.index + 1}</td> --%>
				<c:if test="${status.index eq 0}"><td></td></c:if>
        		<c:if test="${status.index ne 0}"><td>${status.index}</td></c:if>
				<td>
        		<c:if test="${status.index eq 0}">${doorErrorInfo1.custName}</c:if>
        		<c:if test="${status.index ne 0}">
        			<a href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList?custNo=${doorErrorInfo1.custNo}&officeId=${doorErrorInfo.officeId}&status=${doorErrorInfo1.status}&createTimeStart=<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>&createTimeEnd=<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>" title="<spring:message code="common.view" />">
        				${doorErrorInfo1.custName}
        			</a>
       			</c:if>
        		</td>
				<td>${doorErrorInfo1.office.name}</td>
				<td>${fns:getDictLabel(doorErrorInfo1.errorType,'clear_error_type',"")}</td>
				<%-- <td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo1.inputAmount}" type="currency" pattern="#,##0.00"/></td>
				<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo1.checkAmount}" type="currency" pattern="#,##0.00"/></td> --%>
			 	<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo1.diffAmount}" type="currency" pattern="#,##0.00"/></td>
			 	<%-- <td>${doorErrorInfo1.clearManName}</td>
			 	<td>${doorErrorInfo1.makesureManName}</td>
			 	<td><fmt:formatDate value="${doorErrorInfo1.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td> --%>
				<td>
					<!-- 状态颜色动态配置 gzd 2020-05-26  -->
					${fns:getDictLabelWithCss(doorErrorInfo1.status,'check_error_status',"",true)}
				</td>
				<td style="text-align: center;">
				<c:if test="${status.index eq 0}"></c:if>
        		<c:if test="${status.index ne 0}">
        			<a href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList?custNo=${doorErrorInfo1.custNo}&officeId=${doorErrorInfo.officeId}&status=${doorErrorInfo1.status}&createTimeStart=<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>&createTimeEnd=<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>" title="<spring:message code="common.view" />">
        				<i class="fa fa-eye fa-lg"></i>
       				</a>
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