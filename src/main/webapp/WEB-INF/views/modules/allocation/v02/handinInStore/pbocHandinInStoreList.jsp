<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='allocation.inStore.list' /></title>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	    $(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocHandinInStore/list");
				$("#searchForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 入库列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='allocation.inStore.list' /></a></li>
	</ul>
	<div class="row">
	<form:form id="searchForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="breadcrumb form-search">
		
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row">
			<div class="span12" style="margin-top:5px">
				<%-- 流水单号 --%>
				<label><spring:message code="allocation.allId" />：</label>
				<form:input path="allId"/>
				<c:choose>
					<c:when test="${fns:getUser().office.type == '7'}">
						<%-- 申请机构 --%>
						<label class="control-label"><spring:message code="allocation.application.office" />：</label>
						<sys:treeselect id="rofficeId" name="roffice.id"
							value="${pbocAllAllocateInfo.roffice.id}" labelName="roffice.name"
							labelValue="${pbocAllAllocateInfo.roffice.name}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"
							type="3" isAll="false"/>
					</c:when>
					<c:otherwise>
						<%-- 申请机构 --%>
						<label class="control-label"><spring:message code="allocation.application.office" />：</label>
						<sys:treeselect id="rofficeId" name="roffice.id"
							value="${pbocAllAllocateInfo.roffice.id}" labelName="roffice.name"
							labelValue="${pbocAllAllocateInfo.roffice.name}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"
						    isNotNeedSubPobc="true" type="3" isAll="false"/>
					</c:otherwise>
				</c:choose>
				<%-- 状态 --%>
				<label><spring:message code='common.status'/>：</label>
				<c:choose>
					<c:when test="${fns:getUser().userType == '19'}">
						<form:select path="status" id="status" class="input-medium" >
							<form:option value="">
									<spring:message code="common.select" />
								</form:option>
								<form:options items="${fns:getFilterDictList('pboc_order_handin_status', false, '20,21,41')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
					</c:when>
					<c:otherwise>
						<form:select path="status" id="status" class="input-medium" >
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getFilterDictList('pboc_order_handin_status', false, '20,21,41,99')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</c:otherwise>
				</c:choose>
			</div>
			<%-- 申请类型 --%>
			<%--
			<shiro:hasPermission name="allocation.v02:pbocHandinApproval:approval">
				<div class="span4" style="margin-top:5px">
					<label><spring:message code="allocation.application.type" />：</label>
					<form:select path="businessType" id="businessType" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getFilterDictList('all_businessType', true, '50,52')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			</shiro:hasPermission>
		</div>
		<div class="row">
		--%>
			<div class="span7" style="margin-top:5px">
				<%-- 入库时间 --%>
				<%-- 开始日期 --%>
				<label><spring:message code="store.inStoreDateTime" />：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					   value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
				<%-- 结束日期 --%>
				<label>~</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				       value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}'});"/>
				&nbsp;
				<%-- 查询 --%>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
		</div>
	</form:form>
	</div>
	<input type="hidden" id="pageType" value="${pbocAllAllocateInfo.pageType }">
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 申请机构 --%>
				<th class="sort-column a.roffice_name"><spring:message code="allocation.application.office" /></th>
				<%-- 申请类型 --%>
				<%--
				<th class="sort-column a.business_type"><spring:message code="allocation.application.type" /></th>
				--%>
				<%-- 预约日期 --%>
				<th class="sort-column a.apply_date"><spring:message code="allocation.order.date" /></th>
				<%-- 审批金额 --%>
				<th class="sort-column a.confirm_amount"><spring:message code="allocation.approve.amount" /></th>
				<%-- 入库金额 --%>
				<th class="sort-column a.instore_amount"><spring:message code="allocation.inStore.amount" /></th>
				<%-- 人行交接人 --%>
				<th><spring:message code="allocation.pboc.handover" /></th>
				<%-- 商行交接人 --%>
				<th><spring:message code="allocation.bankOfcommerce.handover" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>
				<%-- 入库时间 --%>
				<th class="sort-column a.scan_gate_date"><spring:message code="store.inStoreDateTime" /> </th>
				<%-- 操作（删除/修改） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocation" varStatus="status">
				<tr>
					<%-- 流水单号 --%>
					<td><a href="${ctx}/allocation/v02/pbocWorkflow/findApplicationHandinStatus?allId=${allocation.allId}">${allocation.allId}</a></td>
					<%-- 申请机构 --%>
					<td>${allocation.rofficeName}</td>
					<%-- 申请类型 --%>
					<%--
					<td>${fns:getDictLabel(allocation.businessType, 'all_businessType', '')}</td>
					--%>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.applyDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 审批金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td>
					<%-- 入库金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.instoreAmount}" pattern="#,##0.00#" /></td>
					<%-- 人行交接人 --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='2' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 商行交接人  --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='1' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 状态 --%>
					<td>${fns:getDictLabel(allocation.status,'pboc_order_handin_status',"")}</td>
					<%-- 入库日期 --%>
					<td><fmt:formatDate value="${allocation.scanGateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 操作 --%>
					<td>
						<shiro:hasPermission name="allocation.v02:pbocHandinStore:delete">
							<c:if test="${allocation.status != '99'}">
								<a href="${ctx}/allocation/v02/pbocHandinInStore/delete?allId=${allocation.allId}&strUpdateDate=${allocation.strUpdateDate}"
										onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"  title="删除">
										<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o  text-red fa-lg"></i>
									&nbsp;
								</a>
							</c:if>
						</shiro:hasPermission>
						<a href="${ctx}/allocation/v02/pbocHandinInStore/form?allId=${allocation.allId}&pageType=storeQuotaView"  title="查看">
						<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i></a>&nbsp;
					</td>
					
				</tr>
				
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
