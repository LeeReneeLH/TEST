<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%-- 库间清机入库列表 --%>
<title><spring:message code="allocation.cashBetween.atm.clear.inStore.list" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function() {
			$("#searchForm").attr("action",	"${ctx}/allocation/v01/cashBetweenAtmClear/list");
			$("#searchForm").submit();
		});
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库间清机入库列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cashBetween.atm.clear.inStore.list" /></a></li>
		<shiro:hasPermission name="allocation:cashClassficationRoomClearAtm:edit">
			<%-- 入库登记 --%>
			<li><a href="${ctx}/allocation/v01/cashBetweenAtmClear/toClearCenterForm"><spring:message code="allocation.inStore.register" /></a></li>
		</shiro:hasPermission>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="allAllocateInfo" action=""
			method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
			<input id="pageSize" name="pageSize" type="hidden"
				value="${page.pageSize}" />
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}"
				callback="page();" />
			<div class="row">
				<div class="span14" style="margin-top: 5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId" class="input-medium" />
					<c:if test="${fns:getUser().office.type != '6'}">
						<%-- 登记机构 --%>
						<label><spring:message code="allocation.register.office" />：</label>
						<sys:treeselect id="rOfficeId" name="rOffice.id"
							value="${allAllocateInfo.rOffice.id}" labelName="rOffice.name"
							labelValue="${allAllocateInfo.rOffice.name}" title="<spring:message code='allocation.register.office' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" type="6"
							isAll="true" allowClear="true" />
					</c:if>
					<c:if test="${fns:getUser().office.type != '3'}">
						<%-- 接收机构 --%>
						<label><spring:message code="sys.message.receiveOffice" />：</label>
						<sys:treeselect id="aOfficeId" name="aOffice.id"
							value="${allAllocateInfo.aOffice.id}" labelName="aOffice.name"
							labelValue="${allAllocateInfo.aOffice.name}" title="<spring:message code='sys.message.receiveOffice' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" type="3"
							isAll="true" allowClear="true" />
					</c:if>
					<%-- 业务状态 --%>
					<label><spring:message code="allocation.business.status" />：</label>
					<form:select path="status" id="status" class="input-medium">
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getFilterDictList('transfer_status', false, '11,12')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<div class="span7" style="margin-top: 5px">
					<%-- 登记日期 --%>
					<label><spring:message code="clear.orderClear.registerDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					&nbsp;
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
					&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" style="margin-left:20px;" type="submit" value="<spring:message code='common.search'/>" />
				</div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-hover">
		<thead>
			<tr>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message
						code="allocation.allId" /></th>
				<%-- 业务类型 --%>
				<th class="sort-column a.business_type"><spring:message code="clear.task.business.type" /></th>
				<%-- 登记金额 --%>
				<th class="sort-column a.register_amount"><spring:message code="allocation.register.amount" /></th>
				<%-- 接收金额 --%>
				<th class="sort-column a.confirm_amount"><spring:message code="allocation.confirm.amount" /></th>
				<%-- 业务状态 --%>
				<th><spring:message code="allocation.business.status" /></th>
				<%-- 登记机构 --%>
				<th><spring:message code="allocation.register.office" /></th>
				<%-- 接收机构 --%>
				<th><spring:message code="allocation.instore.office" /></th>
				<%-- 登记时间 --%>
				<th class="sort-column a.create_date"><spring:message code="allocation.register.date" /></th>
				<%-- 接收时间 --%>
				<th class="sort-column a.confirm_date"><spring:message code="allocation.accept.datetime" /></th>
				<%-- 操作（删除/修改） --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocate"
				varStatus="statusIndex">
				<tr>
					<%-- 流水单号 --%>
					<td>${allocate.allId}</td>
					<%-- 业务类型 --%>
					<td>${fns:getDictLabel(allocate.businessType, 'all_businessType', '')}
					</td>
					<%-- 登记金额 --%>
					<td  style="text-align:right;"><fmt:formatNumber value="${allocate.registerAmount}"
							pattern="#,##0.00#" /></td>
					<%-- 接收金额 --%>
					<td  style="text-align:right;"><fmt:formatNumber value="${allocate.confirmAmount}"
							pattern="#,##0.00#" /></td>
					<%-- 业务状态 --%>
					<td>${fns:getDictLabel(allocate.status, 'transfer_status', '')}</td>
					<%-- 登记机构--%>
					<td>${allocate.rOffice.name}</td>
					<%-- 接收机构 --%>
					<td>${allocate.aOffice.name}</td>
					<%-- 登记时间 --%>
					<td><fmt:formatDate value="${allocate.createDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- 确认时间 --%>
					<td><fmt:formatDate value="${allocate.confirmDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- 操作 --%>
					<td>
						<shiro:hasPermission name="allocation:cashStoreClearAtm:edit">
							<c:if test="${allocate.status == '13' }">
								<a href="${ctx}/allocation/v01/cashBetweenAtmClear/toCashInStoreReceive?allId=${allocate.allId}" title="<spring:message code="allocation.inStore.confirm" />">
									<i class="fa fa-check-square-o text-green  fa-lg"></i>
								</a>
							</c:if>
						</shiro:hasPermission> 
						<shiro:hasPermission
							name="allocation:cashClassficationRoomClearAtm:edit">
							<c:if test="${allocate.status == '13' }">
								<a href="${ctx}/allocation/v01/cashBetweenAtmClear/toClearCenterForm?allId=${allocate.allId}" title='<spring:message code='common.modify' />'>
									<i class="fa fa-edit text-green  fa-lg"></i>
								</a>
								<!-- 添加删除权限 修改人：xl 2018-01-10 begin -->
								<shiro:hasPermission name="allocation:cashBetweenAtmClear:delete">
									<a href="${ctx}/allocation/v01/cashBetweenAtmClear/delete?allId=${allocate.allId}"
										onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title='<spring:message code='common.delete' />'>
										<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red  fa-lg"></i>
									</a>
								</shiro:hasPermission>
								<!-- end -->
							</c:if>
						</shiro:hasPermission> 
						 <c:if
							test="${allocate.status == '13' || allocate.status == '99'}">
							<a href="${ctx}/allocation/v01/cashBetweenAtmClear/cashShowDetail?allId=${allocate.allId}" title="<spring:message code='common.view' />"><i class="fa fa-eye  fa-lg"></i></a>
						</c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
