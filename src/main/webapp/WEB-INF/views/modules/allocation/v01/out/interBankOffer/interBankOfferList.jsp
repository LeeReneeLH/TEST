<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 申请代理列表 --%>
	<title><spring:message code="allocation.application.agent.handin.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 提交
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocAgentHandinOrder/list?bInitFlag=false&pageType=" + $("#pageType").val());
				$("#searchForm").submit();
			});
		});
		
		// 弹出撤回画面
		function toCancel(allId) {
			var content = "iframe:${ctx}/allocation/v02/pbocAgentHandinOrder/toCancel?allId=" + allId;
			top.$.jBox.open(
					content,
					"撤回", 750, 400, {
						buttons : {
							//确认
							"<spring:message code='common.confirm' />" : "ok",
							//关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var contentWindow = h.find("iframe")[0].contentWindow;
								// 流水单号
								var allId = contentWindow.document.getElementById("allId").value;
								// 撤回原因
								var cancelReason = contentWindow.document.getElementById("cancelReason").value;
								
								var url = "${ctx}/allocation/v02/pbocAgentHandinOrder/cancel";
								url = url + "?allId=" + allId;
								url = url + "&cancelReason=" + cancelReason;
								//$("#cancelReason").removeClass();
								$("#cancelForm").attr("action", url);
								$("#cancelForm").submit();
								return true;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
			});
		}
		
		// 显示撤回原因
		function displayCancelReason(allId) {
			var content = "iframe:${ctx}/allocation/v02/pbocAgentHandinOrder/displayCancelReason?allId=" + allId;
			top.$.jBox.open(
					content,
					"撤回原因", 750, 460, {
						buttons : {
							//关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 申请代理列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.application.agent.handin.list" /></a></li>
		<%-- 申请代理登记(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocAgentHandinOrder/form?pageType=bussnessApplicationEdit"><spring:message code="allocation.application.agent.handin.registe" /></a></li>
	</ul>
	<div class="row">
		<form:form id="cancelForm" modelAttribute="cancelParam" action="" method="post" >
		
		</form:form>
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
				<div class="span6" style="margin-top:5px">
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
				<%-- 审批金额 --%>
				<th class="sort-column a.confirm_amount">审批金额</th>
				<%-- 入库金额 --%>
				<th class="sort-column a.instore_amount"><spring:message code="allocation.inStore.amount" /></th>
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
					<td>
						<c:choose>
							<c:when test="${allocation.status == '20'}">
								<a href="${ctx}/allocation/v02/pbocAgentHandinOrder/form?allId=${allocation.allId}">${allocation.allId}</a>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/allocation/v02/pbocAgentHandinOrder/view?allId=${allocation.allId}">${allocation.allId}</a>
							</c:otherwise>
						</c:choose>
					</td>
					<%-- 申请金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
					<%-- 审批金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td>
					<%-- 入库金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.instoreAmount}" pattern="#,##0.00#" /></td>
					<%-- 申请人 --%>
					<td>${allocation.createName}</td>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.applyDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 审批人 --%>
					<td>${allocation.approvalName}</td>
					<%-- 审批日期 --%>
					<td><fmt:formatDate value="${allocation.approvalDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					
					<%-- 状态 --%>
					<c:choose>
						<c:when test="${allocation.status == '90'}">
							<td>
								<a href="#" onclick="displayCancelReason('${allocation.allId}');javascript:return false;">
									${fns:getDictLabel(allocation.status,'pboc_order_handin_status',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(allocation.status,'pboc_order_handin_status',"")}</td>
						</c:otherwise>
					</c:choose>
					<%-- 操作 --%>
					<td>
						<c:if test="${allocation.status == '20'}">
							<a href="${ctx}/allocation/v02/pbocAgentHandinOrder/form?allId=${allocation.allId}&pageType=bussnessApplicationEdit">
								<spring:message code="common.modify" />
							</a>
							<%-- <a href="${ctx}/allocation/v02/pbocAgentHandinOrder/delete?allId=${allocation.allId}&pageType=bussnessApplicationList&strUpdateDate=${allocation.strUpdateDate}"
								onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)">
								<spring:message code="common.delete" />
							</a> --%>
						</c:if>
						<c:choose>
							<c:when test="${allocation.status == '20' or allocation.status == '35' or allocation.status == '40'}">
								<a href="#" onclick="toCancel('${allocation.allId}');javascript:return false;" title="撤回"><i class="fa fa-mail-reply-all fa-lg"></i></a>
							</c:when>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
