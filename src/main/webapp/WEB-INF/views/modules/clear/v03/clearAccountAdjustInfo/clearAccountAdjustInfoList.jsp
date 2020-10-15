<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 柜员调账列表 -->
	<title><spring:message code='clear.accountAdjust.list' /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		setToday(".createTime");
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}	

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- >柜员调账列表 -->
		<li class="active"><a href="${ctx}/clear/v03/clearAccountAdjustInfo/list" ><spring:message code='clear.accountAdjust.list' /> </a></li>
		<shiro:hasPermission name="clear:clearAccountAdjustInfo:add"> 
			<!-- 柜员调账登记 -->
			<li><a href="${ctx}/clear/v03/clearAccountAdjustInfo/form"><spring:message code='clear.accountAdjust.title' /></a></li>
		 </shiro:hasPermission> 
	</ul>
	<div class="row">
	<form:form id="searchForm" modelAttribute="clearAccountAdjustInfo"
		action="${ctx}/clear/v03/clearAccountAdjustInfo/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>	
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${clearAccountAdjustInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${clearAccountAdjustInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		<%-- 金额类型 --%>
		<label class="control-label"><spring:message code="clear.accountAdjust.cashType" />：</label>
		<form:select path="cashType" id="cashType" class="input-large">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>				
			<form:options items="${fns:getFilterDictList('cash_type_provisions',true,'')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<!-- 交款人员 -->
		<div class="controls">
		<label class="control-label"><spring:message code='clear.accountAdjust.payTeller' />：</label>
		<form:select path="payTellerBy" id="payTellerBy" class="input-large required">
			<form:option value=""><spring:message code="common.select" /></form:option>
			<form:options items="${sto:getUsersByTypeAndOffice('31;34',fns:getUser().office.id)}" 
			itemLabel="escortName" itemValue="id" htmlEscape="false" />
		</form:select>
		</div>
		<form:hidden path="payTellerName" />
		<!-- 收款人员 -->
		<div class="controls">
		<label class="control-label"><spring:message code='clear.accountAdjust.reTeller' />：</label>
		<form:select path="reTellerBy" id="reTellerBy" class="input-large required">
			<form:option value=""><spring:message code="common.select" /></form:option>
			<form:options items="${sto:getUsersByTypeAndOffice('31;34',fns:getUser().office.id)}" 
			itemLabel="escortName" itemValue="id" htmlEscape="false" />
			</form:select>
		</div>
		<form:hidden path="reTellerName" />
		<!-- 按钮 -->
		&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" />
	</form:form>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<!--序号 -->
				<th><spring:message code='common.seqNo'/></th>
			    <!--业务编号 -->
				<th><spring:message code='clear.task.business.id'/></th>
				<!--金额类型 -->
				<th><spring:message code="clear.accountAdjust.cashType" /></th>
				<!--交款人 -->
				<th><spring:message code='clear.accountAdjust.payTeller'/></th>
				<!--收款人 -->
				<th><spring:message code='clear.accountAdjust.reTeller'/></th>
				<!--调账金额-->
				<th><spring:message code='clear.accountAdjust.adjustMoney'/></th>
				<!--登记人 -->
				<th><spring:message code='clear.public.registerName'/></th>
				<!--登记时间 -->
				<th><spring:message code='clear.register.date'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clearAccountAdjustInfo"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td><a href="${ctx}/clear/v03/clearAccountAdjustInfo/view?accountId=${clearAccountAdjustInfo.accountsId}">
						${clearAccountAdjustInfo.accountsId}
					</a></td>
					<td>${fns:getDictLabel(clearAccountAdjustInfo.cashType,'cash_type_provisions',"")}</td>
					<td>${clearAccountAdjustInfo.payTellerName}</td>
					<td>${clearAccountAdjustInfo.reTellerName}</td>
					<td>
						<c:choose>
							<c:when test="${clearAccountAdjustInfo.adjustMoney==null}">
							<fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${clearAccountAdjustInfo.adjustMoney}" 
							pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>${clearAccountAdjustInfo.createName}</td>	
					<td><fmt:formatDate value="${clearAccountAdjustInfo.createDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>	
					<shiro:hasPermission name="clear.clearAccountAdjustInfo:view"><td>
				</td></shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
