<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="allocation.cash.box.handout" /></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
		<script type="text/javascript">
	// 显示撤回原因
	function displayCancelReason(allId) {
		var content = "iframe:${ctx}/allocation/v01/boxHandover/displayCancelReason?allId=" + allId;
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
								//修正存在两条下拉框的bug 修改人：xp 修改时间：2017-11-6 begin
								"overflow-y", "hidden");
								//end
					}
		});
	}
	
	// 打印明细
	function printDetail(allID){
		var content = "iframe:${ctx}/allocation/v01/boxHandover/printDetail?allID=" + allID;
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
        <%-- 金库箱袋下拨列表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cash.box.handout.list" /></a></li>
		<%-- <shiro:hasPermission name="allocation:point:edit">
			<li><a href="${ctx}/allocation/v01/cashOrder/form?pageType=pointAdd"><spring:message code="allocation.cash.order.register"/></a></li>
		</shiro:hasPermission> --%>
	</ul>

	<sys:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="allAllocateInfo"
		action="${ctx}/allocation/v01/boxHandover/handout?searchFlag=true"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}"
			callback="page();" />

		<shiro:hasPermission name="allocation:handout:coffer">
			<!-- 登记机构 -->
			<label class="control-label"><spring:message
					code="allocation.order.office" />：</label>
			<sys:treeselect id="searchRoffice" name="searchRoffice.id"
				value="${allAllocateInfo.searchRoffice.id}" labelName="searchRoffice.name"
				labelValue="${allAllocateInfo.searchRoffice.name}" title="登记机构"
				url="/sys/office/treeData" cssClass="required input-small"
				allowClear="true" notAllowSelectParent="false" 
				notAllowSelectRoot="false" checkGroupOffice="true"
						    isAll="false" />
			<c:if test="${officeType=='7'}">
				
				<!-- 接收机构 -->
			<label class="control-label"><spring:message
					code="allocation.instore.office" />：</label>
			<sys:treeselect id="aOffice" name="aOffice.id"
				value="${allAllocateInfo.aOffice.id}" labelName="aOffice.name"
				labelValue="${allAllocateInfo.aOffice.name}" title="接收机构"
				url="/sys/office/treeData" cssClass="required input-small"
				 notAllowSelectParent="false"
				notAllowSelectRoot="false" type="3"
						    isAll="true" allowClear="true" />
		   </c:if>
				
		</shiro:hasPermission>
		<%-- 业务状态 --%>
		<label><spring:message code="allocation.business.status" />：</label>
		<form:select path="status" id="status" class="input-small">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('all_status')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate" />：</label>
		<input id="createTimeStart" name="createTimeStart" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate" />：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text"
			readonly="readonly" maxlength="20"
			class="input-small Wdate createTime"
			value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		<%-- 查询按钮 --%>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code='common.search'/>" />
	</form:form>

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
	</form:form>

	<table id="contentTable" class="table table-hover ">
		<thead>
			<tr>
				<%-- 单号 --%>
				<th class="sort-column temp.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 预约机构 --%>
				<th><spring:message code="allocation.order.office" /></th>
				<%-- 接收机构 --%>
				<th><spring:message code="allocation.accept.office" /></th>
				<%-- 预约数量 --%>
				<th><spring:message code="allocation.order.number" /></th>
				<%-- 接收数量 --%>
				<th><spring:message code="allocation.accept.number" /></th>
				<%-- 线路 --%>
				<th><spring:message code="allocation.route.name" /></th>
				<%-- 预约人 --%>
				<th><spring:message code="allocation.order.name" /></th>
				<%-- 预约金额 --%>
				<th><spring:message code="allocation.cash.order.amount" /></th>
				<%-- 预约时间 --%>
				<th><spring:message code="allocation.order.date" /></th>
				<%-- 审批人 --%>
				<th><spring:message code="allocation.approve.person" /></th>
				<%-- 审批金额 --%>
				<th><spring:message code="allocation.approve.amount" /></th>
				<%-- 审批时间 --%>
				<th><spring:message code="allocation.approve.datetime" /></th>
				<%-- 任务类型 --%>
				<th><spring:message code='allocation.tasktype'/></th>
				<%-- 状态 --%>
				<th class="sort-column temp.status"><spring:message code='allocation.business.status'/></th>
				<%-- 操作 --%>
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="allocation" varStatus="no">
				<c:choose>
					<c:when test="${allocation.tempFlag eq 1}">
						<tr bgcolor="#C6C6C6">
					</c:when>
					<c:otherwise>
						<tr>
					</c:otherwise>
				</c:choose>
				<%-- 单号 --%>
				<td>
					<a href="${ctx}/allocation/v01/cashOrder/workflow?allId=${allocation.allId}&backFlag=toHandOutList">${allocation.allId}</a>
				</td>
				<%-- 预约机构 --%>
				<td>${allocation.rOffice.name}</td>
				<%-- 接收机构 --%>
				<td>${allocation.aOffice.name}</td>
				<%-- 预约数量 --%>
				<td>
					<c:if test="${!(allocation.registerNumber eq 0)}">
						${allocation.registerNumber}
					</c:if>
				</td>
				<%-- 接收数量 --%>
				<td>${allocation.acceptNumber}</td>
				<%-- 线路 --%>
				<td>${allocation.routeName}</td>
				<%-- 预约人 --%>
				<td>${allocation.createName}</td>
				<%-- 预约金额 --%>
				<td><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
				<%-- 预约日期 --%>
				<td><fmt:formatDate value="${allocation.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<%-- 审批人 --%>
				<td>${allocation.confirmName}</td>
				<%-- 审批金额 --%>
				<td><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td> 
				<%-- 审批日期 --%>
				<td><fmt:formatDate value="${allocation.confirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td> 
				<%-- 任务类型 --%>
				<td>${fns:getDictLabel(allocation.taskType,'TASK_TYPE',"")}</td>
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
				<%-- 操作 --%>
				<shiro:hasPermission name="allocation:handout:view">
						<td style="30px"><c:choose>
								<c:when
									test="${allocation.status == fns:getConfig('allocation.status.register') and allocation.tempFlag ne 1  }">
									<c:if test="${allocation.taskType eq 01}">
									<a href="${ctx}/allocation/v01/boxHandover/form?allId=${allocation.allId}" title="审批">
										<%-- <spring:message code="common.approve" /> --%><i class="fa fa-pencil-square text-red fa-lg"></i>
									</a>
									</c:if>
									<c:if test="${allocation.taskType eq 02}">
									<a href="${ctx}/allocation/v01/boxHandover/form?allId=${allocation.allId}&pageType=tempStoreEdit" title="审批">
										<%-- <spring:message code="common.approve" /> --%><i class="fa fa-pencil-square text-red fa-lg"></i>
									</a>
									</c:if>
								</c:when>
								<c:when
									test="${(!(allocation.status eq fns:getConfig('allocation.status.register')) or (allocation.status eq fns:getConfig('allocation.status.register') and allocation.tempFlag eq 1)  ) }">
										<a href="${ctx}/allocation/v01/boxHandover/view?allId=${allocation.allId}">
													<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
										</a>
										<c:if test="${allocation.status eq '11' or allocation.status eq '12' or allocation.status eq '14'}">
										
										<!-- 打印 -->
							<a href="#" onclick="printDetail('${allocation.allId}');" title="<spring:message code='common.print'/>">
								<i class="fa fa-print text-yellow fa-lg"></i>
							</a>
										</c:if>
								</c:when>
					
							</c:choose> 
							</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
