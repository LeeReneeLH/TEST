<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="allocation.cash.order" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v01/cashOrder/list?searchFlag=true");				
			});
		});
		
		function toCancel(allId,taskType) {
			var content = "iframe:${ctx}/allocation/v01/cashOrder/toCancel?allId=" + allId+"&taskType="+taskType;
			top.$.jBox.open(
					content,
					"撤回", 660, 500,{
						buttons : {
							//确认
							"<spring:message code='common.confirm' />" : "ok",
							//关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var closeFlag;
								var url = "${ctx}/allocation/v01/cashOrder/cancel";
								var contentWindow = h.find("iframe")[0].contentWindow;
								// 流水单号
								var allId = contentWindow.document.getElementById("allId").value;
								// 撤回原因
								var cancelReason = contentWindow.document.getElementById("cancelReason").value;
								// 错误信息
								var errorMessage = contentWindow.document.getElementById("errorMessage");
								// 任务类型
								var taskType = contentWindow.document.getElementById("taskType").value;
								$.ajax({
									type : "POST",
								    /*contentType: "application/json; charset=utf-8",*/ 
									dataType : "text",
									async: false,
									url : url,
									data : {
										allId : allId,
										cancelReason : cancelReason,
										taskType : taskType
									},
									success : function(serverResult, textStatus) {
										if(serverResult == 'success'){
											closeFlag = true;
											var refreshUrl = "${ctx}/allocation/v01/cashOrder/list";
											$("#cancelForm").attr("action", refreshUrl);
											$("#cancelForm").submit();
										}else{
											closeFlag = false;
											errorMessage.innerHTML = serverResult;
										}
									},
									error : function(XmlHttpRequest, textStatus, errorThrown) {
										closeFlag = false;
										errorMessage.innerHTML = "异常:["+errorThrown+"]，请联系管理员！";
									}
								});
								return closeFlag;
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
			var content = "iframe:${ctx}/allocation/v01/cashOrder/displayCancelReason?allId=" + allId;
			top.$.jBox.open(
					content,
					"撤回原因", 660, 500, {
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

		
		// 打印明细
		function printDetail(allID){
			var content = "iframe:${ctx}/allocation/v01/cashOrder/printDetail?allID=" + allID;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 860, 600, {
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
								return false;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
					});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 现金预约列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cash.order.list"/></a></li>
		<%-- 网点现金预约登记(link) 只在网点登录时显示--%>
		<shiro:hasPermission name="allocation:point:edit">
			<li><a href="${ctx}/allocation/v01/cashOrder/form?pageType=pointAdd"><spring:message code="allocation.cash.order.register"/></a></li>
			<li><a href="${ctx}/allocation/v01/cashOrder/form?pageType=pointTempAdd"><spring:message code="allocation.cash.order.temporary"/></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="cancelForm" modelAttribute="cancelParam" action="" method="post" >
		
		</form:form>
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="" method="post" class="breadcrumb form-search">
		
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		
		<%-- 预约机构 --%>
		<label class="control-label"><spring:message code="allocation.order.office" />：</label>
			<sys:treeselect id="searchRoffice" name="searchRoffice.id"
				value="${allAllocateInfo.searchRoffice.id}" labelName="searchRoffice.name"
				labelValue="${allAllocateInfo.searchRoffice.name}" title="预约机构"
				url="/sys/office/treeData" cssClass="required input-small"
				notAllowSelectParent="false" notAllowSelectRoot="false"
				checkGroupOffice="true"	isAll="false" allowClear="true" />
		<%-- 状态 --%>
		<label><spring:message code='common.status'/>：</label>
		<form:select path="status" id="status" class="input-medium" >
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('all_status')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>

	<sys:message content="${message}"/>
	<form:form id="boxCountForm" modelAttribute="allAllocateInfo" class="form-search">
	<div class="boxhand-num text-center">
		<h4 style="font-weight:normal" class="text-red"><spring:message code="allocation.cash.unregister.task"/></h4>
		<span class="text-red">${allAllocateInfo.unApproved}</span>
		</div>
		<div class="boxhand-num text-center">
		<h4 style="font-weight:normal" class="text-yellow"><spring:message code="allocation.cash.unreceived.task"/></h4>
		<span class="text-yellow">${allAllocateInfo.unAccept}</span>
		</div>
		<div class="boxhand-num text-center">
		<h4 style="font-weight:normal" class="text-muted"><spring:message code="allocation.cash.invalid.task"/></h4>
		<span class="text-muted">${allAllocateInfo.invalid}</span>
		</div>
		<%-- <label><spring:message code="allocation.box.number.all"/>：</label>${allAllocateInfo.boxCount}
		<label><spring:message code="allocation.box.number.tail"/>：</label>${allAllocateInfo.tailBoxCount}
		<label><spring:message code="allocation.box.number.paragraph"/>：</label>${allAllocateInfo.paragraphBoxCount} --%>
	</form:form>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 网点名称(网点查询时不显示) --%>
				<%-- 流水号 --%>
				<th><spring:message code='allocation.allId'/></th>
				<%-- 预约机构 --%>
				<th><spring:message code='allocation.order.office'/></th>
				<%-- 任务类型 --%>
				<th><spring:message code='allocation.tasktype'/></th>
				<%-- 预约金额 --%>
				<th><spring:message code="allocation.cash.order.amount" /></th>
				<%-- 审批金额 --%>
				<th><spring:message code="allocation.approve.amount" /></th>
				<%-- 预约人 --%>
				<th><spring:message code="allocation.order.name" /></th>
				<%-- 预约时间 --%>
				<th><spring:message code="allocation.order.date" /></th>
				<%-- 审批人 --%>
				<th><spring:message code='allocation.approve.person'/></th>
				<%-- 审批时间 --%>
				<th><spring:message code='allocation.approve.datetime'/></th>
				<%-- 线路 --%>
				<th><spring:message code="allocation.route.name" /></th>
				<%-- 状态 --%>
				<th ><spring:message code='common.status'/></th>
				<%-- 操作（查看/修改/配款） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		
			<c:forEach items="${page.list}" var="allocation" >
				<c:choose>
					<c:when test="${allocation.tempFlag eq 1}">
						<tr bgcolor="#C6C6C6">
					</c:when>
					<c:otherwise>
						<tr>
					</c:otherwise>
				</c:choose>
					<%-- 流水号 --%>
					<td><a href="${ctx}/allocation/v01/cashOrder/workflow?allId=${allocation.allId}&backFlag=toCashOrderList">${allocation.allId}</a></td>
					<%-- 预约机构 --%>
					<td>${allocation.rOffice.name}</td>
					<%-- 任务类型 --%>
					<td>${fns:getDictLabel(allocation.taskType,'TASK_TYPE',"")}</td>
					<%-- 登记金额 --%>
					<td><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
					<%-- 确认金额 --%>
					<td><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td> 
					<%-- 登记人 --%>
					<td>${allocation.createName}</td>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- 确认人 --%>
					<td>${allocation.confirmName}</td> 
					<%-- 确认日期 --%>
					<td><fmt:formatDate value="${allocation.confirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td> 
					<%-- 线路 --%>
					<td>${allocation.routeName}</td>
					<%-- 状态 --%>
					<c:choose>
						<c:when test="${allocation.status == '90'}">
							<td>
								<a href="#" onclick="displayCancelReason('${allocation.allId}');javascript:return false;">
									${fns:getDictLabel(allocation.status,'all_status',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(allocation.status,'all_status',"")}</td>
						</c:otherwise>
					</c:choose>
					<%-- 操作（查看/修改/配款） --%>
					<td>	
						<shiro:lacksPermission name="allocation:storemaster:show">
							<%-- 网点权限：未配款：显示修改  已配款：只能查看 --%>
							<shiro:hasPermission name="allocation:point:edit">							
								<%-- 未配款 --%>
								<c:if test="${allocation.status eq fns:getConfig('allocation.status.register') and allocation.rOffice.id == fns:getUser().office.id}">
									<c:if test="${allocation.taskType eq 01}">
										<a href="${ctx}/allocation/v01/cashOrder/form?allId=${allocation.allId}&pageType=pointEdit" title = "修改">
											<!-- 修改 -->
											<i class="fa fa-edit text-green fa-lg"></i>
										</a>
									</c:if>									
									<c:if test="${allocation.taskType eq 02  and allocation.tempFlag ne 1}">
										<a href="${ctx}/allocation/v01/cashOrder/form?allId=${allocation.allId}&pageType=pointTempEdit" title = "修改">
											<!-- 修改 -->
											<i class="fa fa-edit text-green fa-lg"></i>
										</a>
									</c:if>
									<a href="${ctx}/allocation/v01/cashOrder/delete?allId=${allocation.allId}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title = "删除">
										<!-- 删除 -->
										<i class="fa fa-trash-o text-red fa-lg"></i>
									</a>
								</c:if>
								<%-- 已配款 --%>
								<c:if test="${(!(allocation.status eq fns:getConfig('allocation.status.register')) or (allocation.status eq fns:getConfig('allocation.status.register') and allocation.tempFlag eq 1)  ) }">									
									<a href="${ctx}/allocation/v01/cashOrder/view?allId=${allocation.allId}&pageType=pointView" title = "查看">
										<!-- 查看 -->
										<i class="fa fa-eye fa-lg"></i>
									</a>
									<c:if test="${(allocation.status eq '11' or allocation.status eq '12')}">
										<a href="${ctx}/allocation/v01/cashOrder/confirm?allId=${allocation.allId}&tempFlag=${allocation.tempFlag}"
											onclick="return confirmx('<spring:message code="message.I0009"/>', this.href)" title = "接收确认">
											<!-- 确认 -->
											<i class="fa fa-check-square-o text-green fa-lg"></i>
										</a>
									</c:if>
									<c:if test="${(allocation.status eq '15' or allocation.status eq '21') and allocation.rOffice.id == fns:getUser().office.id}">
										<a href="#" onclick="toCancel('${allocation.allId}','${allocation.taskType}');javascript:return false;" title="撤回">
										 	<!-- 撤回 -->
										 	<i class="fa fa-mail-reply-all fa-lg"></i>
										</a>
									</c:if>
									<c:if test="${allocation.status eq '11' or allocation.status eq '12' or allocation.status eq '14'}">
										<!-- 打印 -->
										<a href="#" onclick="printDetail('${allocation.allId}');" title="<spring:message code='common.print'/>">
											<i class="fa fa-print text-yellow fa-lg"></i>
										</a>
									</c:if>
								</c:if>
							</shiro:hasPermission>
						</shiro:lacksPermission>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
