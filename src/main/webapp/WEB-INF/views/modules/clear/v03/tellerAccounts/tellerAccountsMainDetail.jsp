<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 柜员账务管理 -->
	<title><spring:message code='clear.tellerAccount.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
		<li><a href="${ctx}/clear/v03/tellerAccountsMain/"><spring:message code='clear.tellerAccount.list'/></a></li>
		<li class="active"><a href="#"><spring:message code='clear.tellerAccount.detail'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="tellerAccountsMain" action="${ctx}/clear/v03/tellerAccountsMain/tellerAccountsDetail" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="cashType" name="cashType" type="hidden" value="${tellerAccountsMain.cashType}"/>
		<input id="tellerBy" name="tellerBy" type="hidden" value="${tellerAccountsMain.tellerBy}"/>
		<!-- 业务类型 -->
		<label><spring:message code="clear.task.business.type"/>：</label>
		<c:choose>
			<c:when test="${tellerAccountsMain.cashType eq '02'}">
				<form:select path="bussinessType" class="input-large required">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('clear_businesstype',true,'01,02,03,04,05')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:when>
			<c:otherwise>
				<form:select path="bussinessType" class="input-large required">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('clear_businesstype',true,'06,07,70')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:otherwise>
		</c:choose>
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  还原清空属性去除 wzj 2017-11-17 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${tellerAccountsMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  还原清空属性去除 wzj 2017-11-17 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${tellerAccountsMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			&nbsp;
		<!-- end -->
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />&nbsp;
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 柜员姓名 -->
				<th><spring:message code="clear.tellerAccount.tellerName" /></th>
				<!-- 客户名称 -->
				<th><spring:message code="clear.public.custName" /></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型 -->
				<th><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 收入金额（元） -->
				<th><spring:message code="clear.tellerAccount.inAmount"/></th>
				<!-- 付出金额（元） -->
				<th><spring:message code="clear.tellerAccount.outAmount"/></th>
				<!-- 余额（元） -->
				<th><spring:message code="clear.public.balance"/></th>
				<!-- 登记时间 -->
				<th><spring:message code="common.registerTime"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tellerAccountsMain" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${tellerAccountsMain.tellerName}</td>
				<td>${tellerAccountsMain.custName}</td>
				<td>${fns:getDictLabel(tellerAccountsMain.bussinessType,'clear_businesstype',"")}</td>	
				<td>${fns:getDictLabel(tellerAccountsMain.bussinessStatus,'cl_status_type',"")}</td>
				<td>
					<c:choose>
						<c:when test="${tellerAccountsMain.inAmount==null}">
							<fmt:formatNumber value="0" pattern="#,##0.00#" />
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tellerAccountsMain.inAmount}" pattern="#,##0.00#" />
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${tellerAccountsMain.outAmount==null}">
							<fmt:formatNumber value="0" pattern="#,##0.00#" />
						</c:when>
						<c:otherwise>
							<fmt:formatNumber value="${tellerAccountsMain.outAmount}" pattern="#,##0.00#" />
						</c:otherwise>
					</c:choose>
				</td>
				<td><fmt:formatNumber value="${tellerAccountsMain.totalAmount}" pattern="#,##0.00#" /></td>
				<td><fmt:formatDate value="${tellerAccountsMain.registerDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>