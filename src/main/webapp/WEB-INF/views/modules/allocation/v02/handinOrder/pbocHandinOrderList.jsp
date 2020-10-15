<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 申请上缴列表 --%>
	<title><spring:message code="allocation.application.handin.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocHandinOrder/list?bInitFlag=false&pageType=" + $("#pageType").val());
				$("#searchForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 申请上缴列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.application.handin.list" /></a></li>
		<%-- 申请上缴登记(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocHandinOrder/form?pageType=bussnessApplicationEdit"><spring:message code="allocation.application.handin.registe" /></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			
			<div class="row">
				<div class="span7" style="margin-top:5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId" class="input-medium"/>
					
					<%-- 状态 --%>
					<label><spring:message code='common.status'/>：</label>
					<form:select path="status" id="status" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('pboc_order_handin_status')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<div class="span7" style="margin-top:5px">
					<%-- 开始日期 --%>
					<label><spring:message code="allocation.order.date" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
					&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	<input type="hidden" id="pageType" value="${pbocAllAllocateInfo.pageType }">
	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 申请金额 --%>
				<th class="sort-column a.register_amount"><spring:message code="allocation.application.amount" /></th>
				<%-- 申请人 --%>
				<th><spring:message code="allocation.application.person" /></th>
				<%-- 预约日期 --%>
				<th class="sort-column a.apply_date"><spring:message code="allocation.order.date" /></th>
				<%-- 审批人 --%>
				<th><spring:message code="allocation.approve.person" /></th>
				<%-- 审批时间 --%>
				<th class="sort-column a.approval_date"><spring:message code="allocation.approve.datetime" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>
				<%-- 操作（删除/修改） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocation" varStatus="status">
				<tr>
					<%-- 流水单号 --%>
					<td><a href="${ctx}/allocation/v02/pbocWorkflow/findApplicationHandinStatus?allId=${allocation.allId}">${allocation.allId}</a></td>
					<%-- 申请金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
					<%-- 申请人 --%>
					<td>${allocation.createName}</td>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.applyDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 审批人 --%>
					<td>${allocation.approvalName}</td>
					<%-- 审批日期 --%>
					<td><fmt:formatDate value="${allocation.approvalDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					
					<%-- 状态 --%>
					<td>${fns:getDictLabel(allocation.status,'pboc_order_handin_status',"")}</td>
					<%-- 操作 --%>
					<td>
						<c:if test="${allocation.status == '20'}">
							<a href="${ctx}/allocation/v02/pbocHandinOrder/form?allId=${allocation.allId}&pageType=bussnessApplicationEdit" title="编辑">
								<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
							</a>
							&nbsp;
							<a href="${ctx}/allocation/v02/pbocHandinOrder/delete?allId=${allocation.allId}&pageType=bussnessApplicationList&strUpdateDate=${allocation.strUpdateDate}"
								onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o  text-red fa-lg"></i>
							</a>
							&nbsp;
						</c:if>
						<c:choose>
							<c:when test="${allocation.status == '20'}">
								<a href="${ctx}/allocation/v02/pbocHandinOrder/form?allId=${allocation.allId}&pageType=bussnessApplicationEdit" title="编辑"><i class="fa fa-eye fa-lg"></i></a>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/allocation/v02/pbocHandinOrder/form?allId=${allocation.allId}&pageType=bussnessApplicationView" title="查看"><i class="fa fa-eye fa-lg"></i></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
