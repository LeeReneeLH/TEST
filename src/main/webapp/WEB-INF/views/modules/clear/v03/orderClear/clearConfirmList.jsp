<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分接收列表 -->
	<title><spring:message code="clear.orderClear.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
		});

		var submitUrl = "";
		//清分接收
		function confirm(id) {	
			submitUrl = "${ctx}/clear/v03/clearConfirm/confirm?id=" + id;
			var message = "<spring:message code='message.I7236'/>";
			confirmx(message, formSubmit);
		}
		//提交处理
		function formSubmit() {
			$("#searchForm").attr("action", submitUrl);
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 清分接收列表 -->
		<li class="active"><a href="#" ><spring:message code="clear.clearConfirm.title" /><spring:message code="common.list" /></a></li>
	</ul>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="clearConfirm" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<!-- 清分机构 -->
					<label><spring:message code="clear.orderClear.office" />：</label>
					<form:select path="rOffice.id" id="custNo"
							class="input-large required" disabled="disabled">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
							 	items="${sto:getStoCorrespondenceCustList('6',true)}"
								itemLabel="name" itemValue="id" htmlEscape="false" /> 
						</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
					
					<%-- 状态 --%>
					<label><spring:message code="common.status" />：</label>
					<form:select path="searchStatus" class="input-small required" id ="selectStatus">
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('cl_order_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
					
					<!-- 登记日期 -->
					<label><spring:message code="common.startDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${clearConfirm.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
					<!-- 结束日期 -->
					<label><spring:message code="common.endDate" />：</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${clearConfirm.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- end -->
					<!-- 查询 -->
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>

			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 预约单号 -->
				<th class="sort-column a.in_no"><spring:message code="clear.orderClear.orderNo"/></th>
				<!-- 清分机构 -->
				<th class="sort-column a.clear_office"><spring:message code="clear.orderClear.office"/></th>
				<!-- 预约金额 -->
				<th><spring:message code="clear.orderClear.orderAmount"/></th>
				<!-- 状态 -->
				<th class="sort-column a.status"><spring:message code="common.status"/></th>
				
				<!-- 登记机构 -->
				<th class="sort-column o1.name"><spring:message code="clear.orderClear.registerOffice"/></th>
				<!-- 登记日期 -->
				<th class="sort-column a.register_date"><spring:message code="clear.orderClear.registerDate"/></th>
				
				<!-- 接收人 -->
				<th class="sort-column e.name"><spring:message code="clear.clearConfirm.receiveName"/></th>
				<!-- 接收日期 -->
				<th class="sort-column a.receive_date"><spring:message code="clear.clearConfirm.receiveDate"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocate" varStatus="statusIndex">
				<tr>
					<!-- 预约单号 -->
					<td>
					<a href="${ctx}/clear/v03/clearConfirm/view?inNo=${allocate.inNo}">
						${allocate.inNo}
					</a>
					</td>
					<!-- 客户名称 -->
					<td>
						${allocate.rOffice.name}
					</td>
					<!-- 金额 -->
					<td  style="text-align:right;"><fmt:formatNumber value="${allocate.inAmount}" pattern="#,##0.00#" /></td>
					<!-- 状态 -->
					<td>${fns:getDictLabel(allocate.status,'cl_order_type',"")}</td>
					<!-- 登记机构 -->
					<td>${allocate.registerOfficeNm}</td>	
					<!-- 登记日期 -->
					<td><fmt:formatDate value="${allocate.registerDate}" pattern="yyyy-MM-dd"/></td>
					<!-- 接收人 -->
					<td>${allocate.receiveName}</td>
					<!-- 接收日期-->
					<td><fmt:formatDate value="${allocate.receiveDate}" pattern="yyyy-MM-dd"/></td>
					<!-- 操作 -->
					<td style="text-align:center;">
						<shiro:hasPermission name="clear:clearConfirm:edit">
							<c:if test="${'1' eq allocate.status}">
							<!-- 接收 -->
							<a href="${ctx}/clear/v03/clearConfirm/form?inNo=${allocate.inNo}" title="<spring:message code="clear.clearConfirm.receive"/>">
							<i class="fa fa-bookmark  fa-lg"></i>
							</a>
							</c:if>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
