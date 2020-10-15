<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><c:choose>
		<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
			<c:if test="${allAllocateInfo.pageType == 'pointAdd'}">
				<%-- 现金预约登记 --%>
				<spring:message code="allocation.cash.order.register" />
			</c:if>
			<c:if test="${allAllocateInfo.pageType == 'pointEdit'}">
				<%-- 现金预约修改 --%>
				<spring:message code="allocation.cash.order.update" />
			</c:if>
		</c:when>
		<c:when test="${allAllocateInfo.pageType == 'storeEdit'}">
			<%-- 现金配款登记 --%>
			<spring:message code="allocation.cash.quota.register" />
		</c:when>
	</c:choose></title>
<meta name="decorator" content="default" />
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
															"${ctx}/allocation/v01/cashOrder/save?saveType=insert");
											$("#inputForm").submit();
										});

						// 提交
						$("#btnSubmit")
								.click(
										function() {
											if (submitFlag != true) {
												submitFlag = true;
								                var url = "${ctx}/allocation/v01/cashOrder/save?saveType=save";
												$("#detailForm")
														.attr("action",
																url);
												$("#detailForm").submit();
											}
											return;
										});
						$("#btnPrint").click(
								function() {
									var content = "iframe:${ctx}/allocation/v01/cashOrder/printCashOrder?allId=" + $("#hidenAllId").val();
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
	function updateItem(goodsId) {
		//清除tr背景色
		var content = "iframe:${ctx}/allocation/v01/cashOrder/toUpdateGoodsItem?goodsId=" + goodsId;
		top.$.jBox.open(
				content,
				"修改物品数量", 550, 200, {
					buttons : {
						//确认
						"<spring:message code='common.confirm' />" : "ok",
						//关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							var contentWindow = h.find("iframe")[0].contentWindow;
							// 数量
							var moneyNumber = contentWindow.document.getElementById("moneyNumber").value;
							if (moneyNumber < 0) {
								return false;
							}
							if(moneyNumber==""){
								alert("请输入数量");
								return false;
							}
							if(moneyNumber==0){
								alert("数量不能为0");
								return false;
							}
							// 币种
							var goodsId = contentWindow.document.getElementById("goodsId").value;
							var url = "${ctx}/allocation/v01/cashOrder/updateGoodsItem";
							url = url + "?goodsId=" + goodsId;
							url = url + "&moneyNumber=" + moneyNumber;
							$("#moneyNumberOrder").removeClass();
							$("#rofficeId").removeClass();
							$("#inputForm").attr("action", url);
							$("#inputForm").submit();
							
							return true;
						}
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "auto");
					}
		});
		
	}
	
	// 编辑详细
	function resetSubmitFlag() {
		submitFlag = false;
	}
	
</script>
<!-- 打印“现金配款登记”用CSS -->
<link href="${ctxStatic}/css/modules/allocation/v01/out/order/cashorder/printCashOrderDetail.css" media="print" rel="stylesheet" />
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
	<c:choose>
			<c:when test="${allAllocateInfo.pageType == 'pointView' or allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
		<li><a href="${ctx}/allocation/v01/cashOrder/list"><spring:message
					code="allocation.cash.order.list" /></a></li>

			</c:when>
</c:choose>
		<%-- 网点现金预约登记(link) 网点权限时显示--%>
		<shiro:hasPermission name="allocation:point:edit">
			<c:choose>
				<c:when test="${allAllocateInfo.pageType == 'pointView' or allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
					<c:choose>
						<c:when test="${allAllocateInfo.pageType == 'pointView'}">
							<li class="active"><a href="#"
								onclick="javascript:return false;"><spring:message
								code="allocation.show.detail" /></a></li>
						</c:when>
						<c:otherwise>
							<c:if test="${allAllocateInfo.pageType == 'pointAdd'}">
								<li class="active"><a href="#"
									onclick="javascript:return false;"><spring:message
									code="allocation.cash.order.register" /></a></li>
							</c:if>
							<c:if test="${allAllocateInfo.pageType == 'pointEdit'}">
								<li class="active"><a href="#"
									onclick="javascript:return false;"><spring:message
									code="allocation.cash.order.update" /></a></li>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:when>
			</c:choose>
		</shiro:hasPermission>
		<li><a href="${ctx}/allocation/v01/cashOrder/form?pageType=pointTempAdd"><spring:message code="allocation.cash.order.temporary"/></a></li>
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
				test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' || allAllocateInfo.pageType eq 'storeEdit'}">

				<%-- 网点端显示 --%>
				<shiro:hasPermission name="allocation:point:edit">
					<c:choose>
						<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
							<%-- 现金预约：“类别”保留“流通币”、“ATM” “单位”保留“捆”、“把”、“张”、“盒”、“卷”、“枚” --%>
							<sys:goodselect
								classificationReserve="${fns:getConfig('sto.goods.classification.circulation')},${fns:getConfig('sto.goods.classification.atm')},${fns:getConfig('sto.goods.classification.waitClear')}"
								unitReserve="${fns:getConfig('sto.goods.unit.bundle')},${fns:getConfig('sto.goods.unit.control')},${fns:getConfig('sto.goods.unit.sheet')},${fns:getConfig('sto.goods.unit.box')},${fns:getConfig('sto.goods.unit.roll')},${fns:getConfig('sto.goods.unit.trunk')}" />
							<%-- 预约数量 --%>
							<div class="control-group item" >
								<label class="control-label" style="width:80px;"><spring:message
										code="allocation.order.number" />：</label>
								<label>
									<form:input id="moneyNumberOrder"
										path="allAllocateItem.moneyNumber" htmlEscape="false"
										maxlength="5" class="digits required" style="width:150px;" />
									<span class="help-inline"><font color="red">*</font> </span>
								</label>
							</div>
							
							<%-- 确认 --%>
							<div class="control-group item" >
								<div class="controls" style="margin-left: 100px" >
									<input id="add" class="btn btn-primary" type="button"
										value="<spring:message code='common.confirm'/>" />
								</div>
						    </div>
							<div class="clear"></div>
						</c:when>
					</c:choose>
				</shiro:hasPermission>
			</c:when>
		</c:choose>
	</form:form>
	<br />
	<%-- 现金详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">
	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${allAllocateInfo.strUpdateDate }">
		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />
		<div id="detailDiv"
			style="margin-left: 0px; height: 305px; width: 800px; overflow: auto; overflow-x: hidden;">
			<shiro:hasPermission name="allocation:point:edit">
				<c:choose>
					<c:when test="${allAllocateInfo.pageType == 'pointView' or allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
						<%-- 预约总金额 --%>
						<c:choose>
							<c:when
								test="${allocationOrderCash.countItemMap != null and allocationOrderCash.countItemMap.size() != 0}">
								<label style="    background: #f5f5f5;line-height:30px;width:100%;padding:10px;"> <spring:message
										code="allocation.cash.order.amount.all" />（<c:forEach
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
					</c:when>
				</c:choose>
			</shiro:hasPermission>
			<table class="table table-hover  table-responsive">
				<thead>
					<tr>
						<%-- 序号 --%>
						<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
						<%-- 券别列表 --%>
						<th style="text-align: center" ><spring:message code="allocation.classificationInfo" /></th>
						<%-- 预约数量 --%>
						<th style="text-align: center"><spring:message
								code='allocation.order.number' /></th>
						<%-- 预约金额 --%>
						<th style="text-align: center"><spring:message
								code='allocation.cash.order.amount' /></th>
						<c:choose>
							<%-- 编辑状态 显示 --%>
							<c:when
								test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' }">
								<%-- 操作 --%>
								<th style="text-align: center"><spring:message
										code='common.operation' /></th>
							</c:when>
						</c:choose>
					</tr>
				</thead>
				<tbody>
				<% int iRegistIndex = 0; %>
					<c:forEach items="${allocationOrderCash.allAllocateItemMap}"
						var="allAllocateItem" varStatus="status">
						<% iRegistIndex = iRegistIndex + 1; %>
						<tr id="${allAllocateItem.value.goodsId}">
							<%-- 序号--%>
							<td style="text-align:right;"><%=iRegistIndex %></td>
							<%-- 券别信息 --%>
							<td style="text-align: right;"><a href="#"
								onclick="updateItem('${allAllocateItem.value.goodsId}');javascript:return false;">${sto:getGoodsName(allAllocateItem.value.goodsId)}</a></td>
							<%-- 预约数量 --%>
							<td style="text-align: right">
								${allAllocateItem.value.moneyNumber}</td>
							<%-- 预约金额 --%>
							<td style="text-align: right"><fmt:formatNumber
									value="${allAllocateItem.value.moneyAmount}"
									pattern="#,##0.00#" /></td>
							<%-- 操作 --%>
							<c:choose>
								<%-- 编辑状态 显示 --%>
								<c:when
									test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd'}">
									<td style="text-align: center">
										<%-- 网点端显示 --%> 
										<shiro:hasPermission
											name="allocation:point:edit">
											<c:choose>
												<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
													<a href="${ctx}/allocation/v01/cashOrder/save?saveType=delete&detailId=${allAllocateItem.value.allItemsId}&allId=${allocationOrderCash.allId}"
														onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"  title="删除">
														<%-- <spring:message code="common.delete" /> --%><i class="fa   fa-trash-o text-red fa-lg"></i>
													</a>
													
												</c:when>
											</c:choose>
										</shiro:hasPermission>
									</td>
								</c:when>
							</c:choose>

						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="form-actions">
			<c:choose>
				<%-- 编辑状态 显示 --%>
				<c:when
					test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' || allAllocateInfo.pageType eq 'storeEdit'}">
					<%-- 提交 --%>
					<input id="btnSubmit" class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>" />
					&nbsp;
				</c:when>
			</c:choose>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashOrder/back?allId=${allAllocateInfo.allId}&pagetype=${allAllocateInfo.pageType} '" />
		</div>
	</form:form>
</body>
</html>
