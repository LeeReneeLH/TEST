<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.clearAddMoney.list' /></title>
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
		<!-- 清机加钞记录列表 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/clearAddMoney/"><spring:message
					code='clear.clearAddMoney.list' /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="clearAddMoney"
		action="${ctx}/doorOrder/v01/clearAddMoney/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
				<!--第一行 -->
				<div>
				<!-- 清分中心  -->
				<%-- <label><spring:message
						code='clear.clearAddMoney.clearCenterName' />：</label>

				<sys:treeselect id="clearCenterName" name="clearCenterId"
					value="${clearAddMoney.clearCenterId}" labelName="clearCenterName"
					labelValue="${clearAddMoney.clearCenterName}"
					title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData" cssClass="required input-small"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="6"
					maxType="6" isAll="true" allowClear="true"
					checkMerchantOffice="true" clearCenterFilter="true" />
				&nbsp;&nbsp;
 --%>
 				<!-- 业务流水 -->
 				<label><spring:message
						code='clear.clearAddMoney.businessId' />：</label>
				<form:input path="businessId" htmlEscape="false" maxlength="32" class="input-small" />
				</div>
				<!-- 门店 -->
				<div>
				<label><spring:message code="door.public.cust" />：</label>
				<sys:treeselect id="doorId" name="doorId"
						value="${clearAddMoney.doorId}" labelName="doorName"
						labelValue="${clearAddMoney.doorName}" title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData" cssClass="required input-small"
						notAllowSelectParent="true" notAllowSelectRoot="false" minType="8" maxType="9"
						isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
				</div>
				<!-- 机具名称 -->
				<div>
				<label><spring:message code='clear.clearAddMoney.equipmentName' />：</label>
				<form:input path="equipmentName" htmlEscape="false" maxlength="20" class="input-small" />
				</div>
				<%-- 款袋编号 --%>
				<div>
				<label><spring:message code="door.doorOrder.packNum" />：</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<!-- 款袋状态  -->
				<%-- <label><spring:message code='clear.clearAddMoney.bagStatus' />：</label>
				<form:select path="bagStatus" maxlength="10" style="width:209px">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getFilterDictList('BAG_STATUS',true,'0,1')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select> --%>
				<%-- 开始日期 --%>
				<div>
				<label><spring:message code="door.checkCash.registerTime" />：</label> <input
					id="createTimeStart" name="createTimeStart" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${clearAddMoney.createTimeStart}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				</div>
				<%-- 结束日期 --%>
				<div>
				<label>~</label> 
				<input id="createTimeEnd" name="createTimeEnd" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${clearAddMoney.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				</div>
				<!-- 类型  -->
				<div>
				<label><spring:message code='clear.clearAddMoney.type' />：</label>
				<form:select path="type" maxlength="10" class="input-small required">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getFilterDictList('CLEAR_ADD_MONEY_TYPE',true,'0,1')}"
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
				<th><spring:message code='clear.clearAddMoney.indexCode' /></th>
                <!-- 业务流水号 -->
                <th class="sort-column a.business_id" ><spring:message code='clear.clearAddMoney.businessId'></spring:message></th>
				<!-- 门店 -->
				<th class="sort-column door.name" ><spring:message code='clear.clearAddMoney.doorName' /></th>
				<!-- 机具名称 -->
				<th class="sort-column e.series_number" ><spring:message code='clear.clearAddMoney.equipmentName' /></th>
				<!-- 清分中心 -->
				<th class="sort-column a.clear_center_name" ><spring:message code='clear.clearAddMoney.clearCenterName' /></th>
				<!-- 款袋编号 -->
				<th class="sort-column a.bag_no" ><spring:message code='clear.clearAddMoney.bagNo' /></th>
				<!-- 款袋状态 -->
				<%-- <th><spring:message code='clear.clearAddMoney.bagStatus' /></th> --%>
				<!-- 总张数 -->
				<th><spring:message code='clear.clearAddMoney.count' /></th>
				<!-- 总金额 -->
				<th class="sort-column a.amount" ><spring:message code='clear.clearAddMoney.amount' /></th>
				<!-- 类型 -->
				<th class="sort-column a.type" ><spring:message code='clear.clearAddMoney.type' /></th>
				<!-- 余额 -->
				<th class="sort-column a.surplus_amount" ><spring:message code='clear.clearAddMoney.surplusAmount' /></th>
				<!-- 更换时间 -->
				<th class="sort-column a.change_date" ><spring:message code='clear.clearAddMoney.changeDate' /></th>
				<!-- 更换人-->
				<th class="sort-column a.update_name" ><spring:message code='clear.clearAddMoney.changeCode' /></th>
				<!-- 登记时间-->
				<th class="sort-column a.create_date" ><spring:message code='door.checkCash.registerTime' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clearAddMoney"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
                    <td>${clearAddMoney.businessId}</td>
					<td>${clearAddMoney.doorName}</td>
					<td>${clearAddMoney.equipmentName}</td>
					<td>${clearAddMoney.clearCenterName}</td>
				    <td>${clearAddMoney.bagNo}</td> 
					<%-- <td>${fns:getDictLabel(clearAddMoney.bagStatus,'BAG_STATUS',"")}</td> --%>
					<td>${clearAddMoney.count}</td>
					<td><fmt:formatNumber value="${clearAddMoney.amount}" pattern="#,##0.00#" /></td>
					<td>${fns:getDictLabel(clearAddMoney.type,'CLEAR_ADD_MONEY_TYPE',"")}</td>
					<td><fmt:formatNumber value="${clearAddMoney.surplusAmount}" pattern="#,##0.00#" /></td>
					<td><fmt:formatDate value="${clearAddMoney.changeDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${clearAddMoney.updateName}</td>
					<td><fmt:formatDate value="${clearAddMoney.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>