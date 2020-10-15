<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="allocation.cash.handin" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript"
	src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
<script type="text/javascript">
	$(document).ready(
			function() {
				// 表格排序
				$("#btnSubmit").click(
						function() {
							$("#searchForm").attr("action",
									"${ctx}/allocation/v01/cashHandin/list");
						});
			});
	// 弹出撤回画面
	function toCancel(allId) {
		var content = "iframe:${ctx}/allocation/v01/cashHandin/toCancel?allId=" + allId;
		top.$.jBox.open(
				content,
				"撤回", 660, 500, {
					buttons : {
						//确认
						"<spring:message code='common.confirm' />" : "ok",
						//关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							var closeFlag;
							var url = "${ctx}/allocation/v01/cashHandin/cancel";
							var contentWindow = h.find("iframe")[0].contentWindow;
							// 流水单号
							var allId = contentWindow.document.getElementById("allId").value;
							// 撤回原因
							var cancelReason = contentWindow.document.getElementById("cancelReason").value;
							// 错误信息
							var errorMessage = contentWindow.document.getElementById("errorMessage");
							$.ajax({
								type : "POST",
								dataType : "text",
								async: false,
								url : url,
								data : {
									allId : allId,
									cancelReason : cancelReason
								},
								success : function(serverResult, textStatus) {
									if(serverResult == 'success'){
										closeFlag = true;
										var refreshUrl = "${ctx}/allocation/v01/cashHandin/list";
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
		var content = "iframe:${ctx}/allocation/v01/cashHandin/displayCancelReason?allId=" + allId;
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
		var content = "iframe:${ctx}/allocation/v01/cashHandin/printDetail?allID=" + allID;
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
		<%-- 现金上缴列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message
					code="allocation.cash.handin.list" /></a></li>
	</ul>
	<div class="row">
		<form:form id="cancelForm" modelAttribute="cancelParam" action="" method="post" >
		
		</form:form>
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="breadcrumb form-search">

		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}"
			callback="page();" />
		<%-- 上缴机构 --%>
		<label class="control-label"><spring:message code="allocation.cash.handin.office" />：</label>
			<sys:treeselect id="searchRoffice" name="searchRoffice.id"
				value="${allAllocateInfo.searchRoffice.id}" labelName="searchRoffice.name"
				labelValue="${allAllocateInfo.searchRoffice.name}" title="上缴机构"
				url="/sys/office/treeData" cssClass="required input-small"
				notAllowSelectParent="false" notAllowSelectRoot="false"
				checkGroupOffice="true"	isAll="false" allowClear="true" />
		<%-- 状态 --%>
		<label>业务状态：</label>
		<form:select path="status" id="status" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options
				items="${fns:getFilterDictList('all_status',true,'12,11,14,90')}"
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
		&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code='common.search'/>" />
	</form:form>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable"
		class="table table-hover">
		<thead>
			<tr>
				<%-- 流水号 --%>
				<th><spring:message code='allocation.allId' /></th>
				<%-- 上缴机构 --%>
				<th><spring:message code='allocation.cash.handin.office' /></th>
				<%-- 上缴人 --%>
				<th><spring:message code='allocation.cash.handin.name' /></th>
				<%-- 上缴日期 --%>
				<th><spring:message code='allocation.cash.handin.date' /></th>
				<%-- 登记数量 --%>
				<th><spring:message code='allocation.cash.handin.number' /></th>
				<%-- 登记金额 --%>
				<th><spring:message code='allocation.cash.handin.amount' /></th>
				<%-- 接收数量 --%>
				<th><spring:message code='allocation.accept.number' /></th>
				<%-- 接收金额 --%>
				<th><spring:message code='allocation.confirm.amount' /></th>
				<%-- 业务状态 --%>
				<th><spring:message code='allocation.business.status' /></th>
				<%-- 操作（查看） --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocation">
				<tr>
					<%-- 流水号 --%>
					<td><a href = "${ctx}/allocation/v01/handInWorkFlow/findStatus?allId=${allocation.allId}&backFlag=toCashHandinList" style="text-decoration: none">${allocation.allId}</td>
					<%-- 上缴机构 --%>
					<td>${allocation.rOffice.name}</td>
					<%-- 上缴人 --%>
					<td>${allocation.createName}</td>
					<%-- 上缴日期 --%>
					<td><fmt:formatDate value="${allocation.createDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- 登记数量 --%>
					<td>${allocation.registerNumber}</td>
					<%-- 登记金额 --%>
					<td><fmt:formatNumber value="${allocation.registerAmount}"
							pattern="#,##0.00#" /></td>
					<%-- 接收数量 --%>
					<td>${allocation.acceptNumber}</td>
					<%-- 接收金额 --%>
					<td><fmt:formatNumber value="${allocation.confirmAmount}"
							pattern="#,##0.00#" /></td>
					<%-- 业务状态 --%>
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
					<%-- <td>${fns:getDictLabel(allocation.status,'all_status',"")}</td> --%>
					<%-- 操作（查看） --%>
					<td>
						<a href="${ctx}/allocation/v01/cashHandin/form?allId=${allocation.allId}&pageType=pointView"  title="查看">
							<!-- 查看-->
							<i class="fa fa-eye fa-lg"></i>
						</a>
						<c:if test="${allocation.status == '12' and allocation.rOffice.id == fns:getUser().office.id}">
							<!-- 撤回-->
							<a href="#" onclick="toCancel('${allocation.allId}');javascript:return false;" title="撤回">
								<i class="fa fa-mail-reply-all fa-lg"></i>
							</a>
						</c:if>
						<c:if test="${(allocation.status eq '14' or allocation.status eq '11') }">
							<!-- 打印 -->
							<a href="#" onclick="printDetail('${allocation.allId}');" title="<spring:message code='common.print'/>">
								<i class="fa fa-print text-yellow fa-lg"></i>
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
