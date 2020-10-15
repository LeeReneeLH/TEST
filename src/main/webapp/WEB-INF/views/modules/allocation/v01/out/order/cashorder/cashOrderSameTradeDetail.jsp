<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><c:choose>
		<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' }">
				<%-- 同业配款登记--%>
				<spring:message code="allocation.cash.same.trade.quota.register" />
			</c:if>
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeEdit' }">
				<%-- 同业配款修改--%>
				<spring:message code="allocation.cash.same.trade.quota.update" />
			</c:if>
		</c:when>
		<c:otherwise>
			<%-- 同业配款明细查询 --%>
			<spring:message code="allocation.cash.trade.quota.show.detail" />
		</c:otherwise>
	</c:choose></title>
<meta name="decorator" content="default" />
<!-- 打印“现金配款登记”用CSS -->
<link href="${ctxStatic}/css/modules/allocation/v01/out/order/cashorder/printCashOrderDetail.css" media="print" rel="stylesheet" />
<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
<script type="text/javascript">
	var submitFlag;
	$(document)
			.ready(
					function() {
						resetSubmitFlag();
						$("#inputForm").validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									//errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										//$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
										} else {
											error.insertAfter(element);
										}
									}
								});

						// 添加
						$("#add")
								.click(
										function() {
											$("#inputForm")
													.attr("action",
															"${ctx}/allocation/v01/cashOrder/saveSameTrade?saveType=insert");
											$("#inputForm").submit();
										});

						// 提交
						$("#btnSubmit")
								.click(
										function() {
											if (submitFlag != true) {
												submitFlag = true;
												$("#detailForm")
														.attr("action",
																"${ctx}/allocation/v01/cashOrder/saveSameTrade?saveType=save");
												$("#detailForm").submit();
											}
											return;
										});
						$("#btnPrint").click(
								function() {
									var content = "iframe:${ctx}/allocation/v01/cashOrder/print?allId=" + $("#hidenAllId").val();
									top.$.jBox.open(
											content,
											"打印", 750, 520, {
												buttons : {
													"打印" : "ok",
													"关闭" : true
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
											});
								});
					   
					});
	function resetSubmitFlag() {
		submitFlag = false;
	}
</script>
<style>
<!-- /* 输入项 */
.item {
	display: inline;
	float: left;
}
/* 清除浮动 */
.clear {
	clear: both;
}
/* 标签宽度 */
.label_width {
	width: 120px;
}
-->
</style>
</head>
<body>

	<ul class="nav nav-tabs">
		<%-- 现金预约列表(link) --%>

		<li><a href="${ctx}/allocation/v01/cashOrder/list"><spring:message
					code="allocation.cash.order.list" /></a></li>

		<%-- 网点现金预约登记(link) 只在网点登录时显示--%>
		<shiro:hasPermission name="allocation:point:edit">
			<li><a href="${ctx}/allocation/v01/cashOrder/form?pageType=pointAdd"><spring:message code="allocation.cash.order.register"/></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="allocation:store:edit">
			<c:choose>
				<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
					<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' }">
						<li class="active"><a
							href="#" onclick="javascript:return false;"><spring:message
									code="allocation.cash.same.trade.quota.register" /></a></li>
					</c:if>
					<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeEdit' }">
						<li class="active"><a
							href="#" onclick="javascript:return false;"><spring:message
									code="allocation.cash.same.trade.quota.update" /></a></li>
					</c:if>
				</c:when>
				<c:when test="${allAllocateInfo.pageType == 'storeSameTradeView'}">
					<li class="active"><a href="#"
						onclick="javascript:return false;"><spring:message code="allocation.cash.trade.quota.show.detail" /></a></li>
				</c:when>
			</c:choose>
		</shiro:hasPermission>
	</ul>
	<br>
	<form:form id="inputForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">
		<sys:message content="${message}" />
		<form:hidden id="inputFormAllId" path="allId" />
		<form:hidden path="pageType" />
		<form:hidden path="allAllocateItem.allItemsId" />
		<c:choose>
			<%-- 编辑状态 显示 --%>
			<c:when
				test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
				<div class="control-group item">
					<%-- 网点名称 --%>
					<label class="control-label" style="width: 120px;" ><spring:message
							code="common.outletsName" />：</label>
					<div class="controls" style="margin-left: 130px;">
						<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd'}">
							<sys:treeselect id="rOfficeId" name="rOffice.id"
								value="${allAllocateInfo.rOffice.id}" labelName="rOffice.name"
								labelValue="${allAllocateInfo.rOffice.name}" title="机构"
								url="/sys/office/treeData?type=4&tradeFlag=1" cssClass="required" cssStyle="width:102px;"
								allowClear="true" notAllowSelectParent="true" />
							<span class="help-inline"><font color="red">*</font> </span>
						</c:if>
						<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
							${allocationOrderCash.rOffice.name}
						</c:if>
					</div>
				</div>
				<div class="clear"></div>
				<%-- 现金预约：“类别”移除“假币”、“待整点币”、“残损币” --%>
				<sys:goodselect classificationRemove="${fns:getConfig('sto.goods.classification.counterfeit')},${fns:getConfig('sto.goods.classification.waitClear')},${fns:getConfig('sto.goods.classification.damaged')}"/>
				<%-- 数量 --%>
				<div class="control-group item">
					<label class="control-label" style="width: 120px;"><spring:message
							code="common.number" />：</label>
					<div class="controls" style="margin-left: 130px;">
						<form:input id="moneyNumberQuota"
							path="allAllocateItem.moneyNumber" htmlEscape="false"
							maxlength="5" class="digits required" style="width:150px;" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<%-- 确认 --%>
				<div class="control-group item">
					<div class="controls"  style="margin-left:50px;">
						<input id="add" class="btn btn-primary" type="button"
							value="<spring:message code='common.confirm'/>" />
					</div>
			    </div>
				<div class="clear"></div>
				
			</c:when>
		</c:choose>
	</form:form>
	<br />
	<%-- 现金详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">

		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />
		<div id="detailDiv"
			style="margin-left: 102px; ${allocationOrderCash.pageType == 'storeSameTradeView' ? '' : 'height: 305px;'} width: 800px; overflow: auto; overflow-x: hidden;">
				
			<%-- 网点名称 库房端显示--%>
			<label><spring:message
					code='common.outletsName' />：${allocationOrderCash.rOffice.name}</label>
			<br />
			<%-- 总金额  --%>
			<c:choose>
				<c:when
					test="${allocationOrderCash.countItemMap != null and allocationOrderCash.countItemMap.size() != 0}">
					<label> <spring:message
							code="allocation.cash.quota.amount.all" />（<c:forEach
							items="${allocationOrderCash.countItemMap}" var="countItem"
							varStatus="status">
							${sto:getGoodDictLabel(countItem.value.currency,'currency',"")}：<fmt:formatNumber
								value="${countItem.value.moneyAmount}" pattern="#,##0.00#" />
							<c:choose>
								<c:when test="${status.index !=0 and status.index % 4 == 0}">
									<br />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${status.index != allocationOrderCash.countItemMap.size() - 1}">
											&nbsp;&nbsp;
										</c:when>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:forEach>）
					</label>
				</c:when>
			</c:choose>
			<table class="table table-hover">
				<thead>
					<tr>
						<%-- 币种 --%>
						<th style="text-align: center"><spring:message
								code='common.currency' /></th>
						<%-- 类别 --%>
						<th style="text-align: center"><spring:message
								code="common.classification" /></th>
						<%-- 套别 --%>
						<th style="text-align: center"><spring:message
								code="common.edition" /></th>
						<%-- 现金材质 --%>
						<th style="text-align: center"><spring:message
								code='common.cash' /></th>
						<%-- 面值--%>
						<th style="text-align: center"><spring:message
								code='common.denomination' /></th>
						<%-- 单位 --%>
						<th style="text-align: center"><spring:message
								code="common.units" /></th>
						<%-- 配款数量 --%>
						<th style="text-align: center"><spring:message
								code='allocation.cash.quota.number' /></th>
						<%-- 配款金额 --%>
						<th style="text-align: center"><spring:message code='allocation.cash.quota.amount' /></th>
						<c:choose>
							<%-- 编辑状态 显示 --%>
							<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
								<%-- 操作 --%>
								<th style="text-align: center"><spring:message code='common.operation' /></th>
							</c:when>
						</c:choose>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${allocationOrderCash.allAllocateItemMap}"
						var="allAllocateItem" varStatus="status">
						<tr>
							<%-- 币种 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.currency,'currency',"")}
							</td>
							<%-- 类别 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.classification,'classification',"")}
							</td>
							<%-- 套别 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.sets,'edition',"")}
							</td>
							<%-- 现金材质 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.cash,'cash',"")}</td>
							<%-- 面值 --%>
							<td style="text-align: center">
								${sto:getDenLabel(allAllocateItem.value.currency, allAllocateItem.value.denomination, "")}
							</td>
							<%-- 单位 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.unit,'c_unit',"")}${sto:getGoodDictLabel(allAllocateItem.value.unit,'p_unit',"")}
							</td>
							<%-- 配款数量 --%>
							<td style="text-align: center">
								${allAllocateItem.value.moneyNumber}</td>
							<%-- 配款金额 --%>
							<td style="text-align: right"><fmt:formatNumber
									value="${allAllocateItem.value.moneyAmount}"
									pattern="#,##0.00#" /></td>
							<c:choose>
								<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
									<%-- 操作 --%>
									<td style="text-align: center">
										<a href="${ctx}/allocation/v01/cashOrder/saveSameTrade?saveType=delete&detailId=${allAllocateItem.value.allItemsId}&allId=${allocationOrderCash.allId}"
											onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
										</a>
									</td>
								</c:when>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="form-actions">
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
				<%-- 保存 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.commit'/>" />
				&nbsp;
			</c:if>
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeView'}">
				<!-- 打印 -->
				<input id="btnPrint" type="button" class="btn btn-red" value="<spring:message code='common.print'/>" />
			</c:if>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn btn-default" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashOrder/back?allId=${allAllocateInfo.allId}'" />
		</div>
	</form:form>
</body>
</html>
