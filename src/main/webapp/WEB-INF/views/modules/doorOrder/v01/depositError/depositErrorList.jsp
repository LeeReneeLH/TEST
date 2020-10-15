<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.depositError.list' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {

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
		<!-- 存款差错列表 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/depositError/"><spring:message
					code='clear.depositError.list' /></a></li>
		<!-- 存款差错登记-->
		<shiro:hasPermission name="doorOrder:v01:depositError:edit">
			<li><a href="${ctx}/doorOrder/v01/depositError/form"><spring:message
						code='clear.depositError.register' /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="depositError"
		action="${ctx}/doorOrder/v01/depositError/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<!-- 差错单号 -->
		<div>
		<label><spring:message code='door.checkCash.tickertape' />：</label>
		<form:input path="orderId" htmlEscape="false" maxlength="24" class="input-small required" />
		</div>
		<!-- 门店 -->
		<div>
		<label><spring:message code='clear.depositError.door' /> ：</label>
		<sys:treeselect id="doorName" name="doorId"
			value="${depositError.doorId}" labelName="doorName"
			labelValue="${depositError.doorName}"
			title="<spring:message code='door.public.cust' />"
			url="/sys/office/treeData" cssClass="required input-small"
			notAllowSelectParent="false" notAllowSelectRoot="false" minType="8"
			maxType="9" isAll="true" allowClear="true" checkMerchantOffice="true"
			clearCenterFilter="true" />
		</div>
		<div>
		<!-- 登记人姓名 -->
		<label><spring:message code='clear.depositError.registerName' />：</label>
		<form:input path="registerName" htmlEscape="false" maxlength="20" class="input-small required" />
		</div>
		<!-- 差错类型 -->
		<div>
		<label><spring:message code='clear.depositError.errorType' />：</label>
		<form:select path="errorType" maxlength="10" class="input-medium required">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options
				items="${fns:getFilterDictList('clear_error_type',true,'2,3')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		<!-- 状态-->
		<div>
		<label><spring:message code="door.errorInfo.status" />：</label>
		<form:select path="status" class="input-medium required" >
			<form:option value=""><spring:message code="common.select" /></form:option>		
			<form:options items="${fns:getDictList('CLEAR_BANKPAYINFO_STATUS')}" 
						itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		&nbsp;&nbsp;
		<!-- 按钮 -->
		<div>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
		</div>
		</div>
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='clear.depositError.indexCode' /></th>
				<!-- 差错单号 -->
				<th class="sort-column a.order_id"><spring:message code='door.checkCash.tickertape' /></th>
				<!-- 门店 -->
				<th class="sort-column o.name"><spring:message code='clear.depositError.door' /></th>
				<!-- 差错类型 -->
				<th class="sort-column a.error_type"><spring:message code='clear.depositError.errorType' /></th>
				<!-- 差错金额 -->
				<th class="sort-column a.amount"><spring:message code='clear.depositError.amount' /></th>
				<!-- 状态 -->
				<th class="sort-column a.status"><spring:message code='clear.depositError.status' /></th>
				<!-- 登记人 -->
				<th class="sort-column a.REGISTER_NAME"><spring:message code='clear.depositError.registerName' /></th>
				<!-- 登记时间 -->
				<th class="sort-column a.create_date"><spring:message code='clear.depositError.createDate' /></th>
				<!-- 操作 -->
				<shiro:hasPermission name="doorOrder:v01:depositError:edit">
					<th><spring:message code='clear.depositError.caoZuo' /></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="depositError" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${depositError.orderId}</td>
					<td>${depositError.doorName}</td>
					<td>${fns:getDictLabel(depositError.errorType,'clear_error_type',"")}</td>
					<td><fmt:formatNumber value="${depositError.amount}" pattern="#,##0.00#" /></td>
					<td>
					<!-- update hzy 配置颜色  2020/05/26 start -->
					${fns:getDictLabelWithCss(depositError.status, "CLEAR_BANKPAYINFO_STATUS", "未命名", true)}
					<!-- update hzy 配置颜色  2020/05/26 end -->
					</td>
					<td>${depositError.registerName}</td>
					<td><fmt:formatDate value="${depositError.createDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<c:choose>
						<c:when test="${depositError.status eq '1'}">
							<shiro:hasPermission name="doorOrder:v01:depositError:edit">
								<td><span style='width: 25px; display: inline-block;'>
										<%-- 冲正 --%> <a
										href="${ctx}/doorOrder/v01/depositError/reversal?id=${depositError.id}"
										onclick="return confirmx('是否确认冲正？', this.href)"
										title="<spring:message code='common.reverse'/>"> <i
											class="fa fa-exchange"></i>
									</a>
								</span></td>
							</shiro:hasPermission>
						</c:when>
						<c:otherwise>
							<td></td>
						</c:otherwise>
					</c:choose>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>