<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务管理 -->
	<title><spring:message code="clear.centerAccounts.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				if ($("#custNo").val()=='') {
					alertx("客户名称不能为空");
					return false;
				}
				$("#searchForm").prop("action", "${ctx}/clear/v03/guestAccounts/exportGuestAccounts/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/clear/v03/guestAccounts/");	
			});
 			$("#btnSubmit").on('click',function(){
 				if ($("#custNo").val()=='') {
					alertx("客户名称不能为空");
					return false;
				}
				$("#searchForm").submit();
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
		<c:choose>
			<c:when test="${type eq 'cash'}">
				<!-- 现金 -->
				<li class="active"><a href="${ctx}/clear/v03/guestAccounts/list?type=cash"><spring:message code="clear.centerAccounts.cash"/></a></li>
				<!-- 备付金 -->
				<li><a href="${ctx}/clear/v03/guestAccounts/list?type=pro"><spring:message code="clear.centerAccounts.cover"/></a></li>
			</c:when>
			<c:otherwise>
				<!-- 现金 -->
				<li><a href="${ctx}/clear/v03/guestAccounts/list?type=cash"><spring:message code="clear.centerAccounts.cash"/></a></li>
				<!-- 备付金 -->
				<li class="active"><a href="${ctx}/clear/v03/guestAccounts/list?type=pro"><spring:message code="clear.centerAccounts.cover"/></a></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="${ctx}/clear/v03/guestAccounts/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 客户名称 -->
		<label><spring:message code="clear.public.custName" />：</label>
		<form:select path="clientId" id="custNo"
					class="input-large required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('1,3',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select>
		<!-- 业务类型 -->
		<label><spring:message code="clear.task.business.type"/>：</label>
		<c:choose>
			<c:when test="${type eq 'cash'}">
				<input id="type" type="hidden" name="type" value="cash">
				<form:select path="businessType" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('clear_businesstype',true,'01,02,03,04,05')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:when>
			<c:otherwise>
				<input id="type" type="hidden" name="type" value="pro">
				<form:select path="businessType" class="input-large" id="businessType">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('clear_businesstype',true,'06,07,70')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:otherwise>
		</c:choose>
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${centerAccountsMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${centerAccountsMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			&nbsp;
		<!-- end -->
		<!--  清分机构查询qph 2017-11-27 begin -->	
			 <label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="rofficeId" name="rofficeId"
				value="${centerAccountsMain.rofficeId}" labelName="office.name"
				labelValue="${centerAccountsMain.rofficeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />&nbsp;
		<!-- 导出 -->
		<input id="exportSubmit" class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 客户名称 -->
				<th><spring:message code="clear.public.custName" /></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型 -->
				<th><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 借方(元) -->
				<th><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 贷方(元) -->
				<th><spring:message code="clear.centerAccounts.lender"/></th>
				<!-- 余额(元) -->
				<th><spring:message code="clear.centerAccounts.balance"/></th>
				<!-- 交易日期 -->
				<th><spring:message code="clear.dayReportMain.transactionDate"/></th>
				<!-- 增加机构显示 qph 2017-11-27 begin -->
				<%--<th><spring:message code="clear.orderClear.office"/></th>--%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="centerAccountsMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${centerAccountsMain.clientName}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessType,'clear_businesstype',"")}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessStatus,'cl_status_type',"")}
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.inAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.inAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<fmt:formatNumber value="${centerAccountsMain.guestTotalAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatDate value="${centerAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<!-- 增加机构 qph 2017-11-27 begin -->
					<%--<td>
					${centerAccountsMain.rofficeName} 
					</td>--%>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>