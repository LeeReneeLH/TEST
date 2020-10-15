<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 库间清分列表 --%>
	<title><spring:message code="allocation.cashBetween.clear.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v01/cashBetweenClear/list");
				$("#searchForm").submit();
			});
		});
		
		// 打印登记物品明细
		function printApprovalGoodDetail(allId){
			var content = "iframe:${ctx}/allocation/v01/cashBetweenClear/printRegisterGoodsDetail?allId=" + allId;
			top.$.jBox.open(
				content,
				//打印
				"<spring:message code='common.print' />", 800, 600, {
					buttons : {
						//打印
						"<spring:message code='common.print' />" : "ok",
						// 关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							var printDiv = h.find("iframe")[0].contentWindow.printDiv;
							$(printDiv).show();
							//打印 
							$(printDiv).jqprint();
							return true;
						}
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "auto");
					}
				}
			);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 现金调拨列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cashBetween.clear.list" /></a></li>
		<shiro:hasPermission name="allocation:cashStoreClear:edit">
			<%-- 清分出库登记 --%>
			<li><a href="${ctx}/allocation/v01/cashBetweenClear/toCashOutStoreForm"><spring:message code="allocation.clear.outStore.register" /></a></li>
		</shiro:hasPermission>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="allAllocateInfo" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId" class="input-medium"/>
					<c:if test="${fns:getUser().office.type != '3'}">
						<%-- 登记机构 --%>
						<label><spring:message code="allocation.register.office" />：</label>
						<sys:treeselect id="rOfficeId" name="rOffice.id"
								value="${allAllocateInfo.rOffice.id}" labelName="rOffice.name"
								labelValue="${allAllocateInfo.rOffice.name}" title="<spring:message code='allocation.register.office' />"
								url="/sys/office/treeData" cssClass="required input-small"
								notAllowSelectParent="false" notAllowSelectRoot="false" type="3"
							    isAll="true"  allowClear="true"/>
					</c:if>
					<%-- 接收机构 --%>
					<label><spring:message code="allocation.accept.office" />：</label>
					<sys:treeselect id="aOfficeId" name="aOffice.id"
							value="${allAllocateInfo.aOffice.id}" labelName="aOffice.name"
							labelValue="${allAllocateInfo.aOffice.name}" title="<spring:message code='allocation.accept.office' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" type="6"
						    isAll="true"  allowClear="true"/>
					<%-- 业务状态 --%>
					<label><spring:message code="allocation.business.status" />：</label>
					<form:select path="status" id="status" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('transfer_status')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<div class="span7" style="margin-top:5px">
					<%-- 登记日期 --%>
					<label><spring:message code="allocation.register.date" />：</label>
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
					<input id="btnSubmit" style="margin-left:20px;" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 业务类型 --%>
				<th class="sort-column a.business_type"><spring:message code="clear.task.business.type" /></th>
				<shiro:hasPermission name="allocation:cashStoreClear:edit">
					<%-- 登记金额 --%>
					<th class="sort-column a.register_amount"><spring:message code="allocation.register.amount" /></th>
				</shiro:hasPermission>
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
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocate" varStatus="statusIndex">
				<tr>
					<%-- 流水单号 --%>
				    <td>${allocate.allId}</td> 
					<%-- 业务类型 --%>
					<td>
						${fns:getDictLabel(allocate.businessType, 'all_businessType', '')} 
					</td>
					<shiro:hasPermission name="allocation:cashStoreClear:edit">
						<%-- 登记金额 --%>
						<td  style="text-align:right;"><fmt:formatNumber value="${allocate.registerAmount}" pattern="#,##0.00#" /></td>
					</shiro:hasPermission>
					<%-- 确认金额 --%>
					<td  style="text-align:right;"><fmt:formatNumber value="${allocate.confirmAmount}" pattern="#,##0.00#" /></td>
					<%-- 业务状态 --%>
					<td>${fns:getDictLabel(allocate.status, 'transfer_status', '')}</td>
					<%-- 登记机构--%>
					<td>${allocate.rOffice.name}</td>
					<%-- 接收机构 --%>
					<td>${allocate.aOffice.name}</td>
					<%-- 登记时间 --%>
					<td><fmt:formatDate value="${allocate.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				    <%-- 确认时间 --%>
					<td><fmt:formatDate value="${allocate.confirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 操作 --%>
					<td>
						<shiro:hasPermission name="allocation:cashStoreClear:edit">
							<c:if test="${allocate.status == '11' }">
								
								<%-- 配款打印 --%>
								<a href="#" onclick="printApprovalGoodDetail('${allocate.allId}');" title="<spring:message code='common.quotaPrint'/>">
									<i class="fa fa-print fa-lg text-yellow"></i>
								</a>
								<a href="${ctx}/allocation/v01/cashBetweenClear/toCashOutStoreForm?allId=${allocate.allId}" 
									title="<spring:message code='common.modify' />">
									<i class="fa fa-edit text-green fa-lg"></i>
								</a>
								<a href="${ctx}/allocation/v01/cashBetweenClear/delete?allId=${allocate.allId}"
									onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="<spring:message code='common.delete' />">
									<i class="fa fa-trash-o text-red fa-lg"></i>
								</a>
							</c:if>
							<c:if test="${allocate.status == '13' }">
								<%-- 入库确认 --%>
								<a href="${ctx}/allocation/v01/cashBetweenClear/toCashInStoreReceive?allId=${allocate.allId}" title="<spring:message code='allocation.inStore.confirm' />">
									<i class="fa fa-check-square-o fa-lg text-green"></i>
								</a>
								
							</c:if>
						</shiro:hasPermission>
						<shiro:hasPermission name="allocation:cashClassficationRoomClear:edit">
							<c:if test="${allocate.status == '11' }">
								<%-- 接收确认 --%>
								<a href="${ctx}/allocation/v01/cashBetweenClear/toClearCenterReceiveForm?allId=${allocate.allId}" title="<spring:message code='common.receiveConfirm' />">
									<i class="fa fa-check-square-o fa-lg text-green"></i>
								</a>
							</c:if>
							<c:if test="${allocate.status == '12' }">
								<%-- 入库登记 --%>
								<a href="${ctx}/allocation/v01/cashBetweenClear/toClearCenterInStoreForm?allId=${allocate.allId}" title="<spring:message code='allocation.inStore.register' />">
									<i class="fa fa-pencil-square fa-lg text-red"></i>
								</a>
							</c:if>
							<c:if test="${allocate.status == '13' }">
								<%-- 入库修改 --%>
								<a href="${ctx}/allocation/v01/cashBetweenClear/toClearCenterInStoreForm?allId=${allocate.allId}" title="<spring:message code='allocation.inStore.modify' />">
									<i class="fa fa-edit text-green fa-lg"></i>
								</a>
							</c:if>
						</shiro:hasPermission>
						<c:if test="${allocate.status == '13' || allocate.status == '99' || allocate.status == '12'}">
							<%-- 查看 --%>
							<a href="${ctx}/allocation/v01/cashBetweenClear/cashShowDetail?allId=${allocate.allId}" title='<spring:message code='common.view' />'>
								<i class="fa fa-eye fa-lg"></i>
							</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
