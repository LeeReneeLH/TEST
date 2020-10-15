<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><c:choose>
		<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
			<c:if test="${allAllocateInfo.pageType == 'pointAdd'}">
				<%-- 重空预约登记 --%>
				<spring:message code="allocation.imp.blank.doc.order.register" />
			</c:if>
			<c:if test="${allAllocateInfo.pageType == 'pointEdit'}">
				<%-- 重空预约修改 --%>
				<spring:message code="allocation.imp.blank.doc.order.update" />
			</c:if>
		</c:when>
		<c:when test="${allAllocateInfo.pageType == 'storeEdit'}">
			<%-- 重空装配登记 --%>
			<spring:message code="allocation.imp.blank.doc.quota.register" />
		</c:when>
		<c:otherwise>
			<%-- 明细查询 --%>
			<spring:message code="allocation.show.detail" />
		</c:otherwise>
	</c:choose></title>
<meta name="decorator" content="default" />
<!-- 打印用CSS -->
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
															"${ctx}/allocation/v01/impBlankDocOrder/save?saveType=insert");
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
																"${ctx}/allocation/v01/impBlankDocOrder/save?saveType=save");
												$("#detailForm").submit();
											}
											return;
										});
						$("#btnPrint").click(
								function() {
									var content = "iframe:${ctx}/allocation/v01/impBlankDocOrder/printOrder?allId=" + $("#hidenAllId").val();
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
	
	// 编辑详细
	function editDetail(detailId) {
		$("#detailForm").attr(
				"action",
				"${ctx}/allocation/v01/impBlankDocOrder/save?saveType=edit&detailId="
						+ detailId);
		$("#hidenAllId").attr("value", $("#inputFormAllId").val());

		$("#detailForm").submit();
	}
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
		<%-- 重空预约列表(link) --%>
		<li><a href="${ctx}/allocation/v01/impBlankDocOrder/list"><spring:message
					code="allocation.imp.blank.doc.order.list" /></a></li>

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
									code="allocation.imp.blank.doc.order.register" /></a></li>
							</c:if>
							<c:if test="${allAllocateInfo.pageType == 'pointEdit'}">
								<li class="active"><a href="#"
									onclick="javascript:return false;"><spring:message
									code="allocation.imp.blank.doc.order.update" /></a></li>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:when>
			</c:choose>
		</shiro:hasPermission>
		<shiro:hasPermission name="allocation:store:edit">
			<c:choose>
				<c:when test="${allAllocateInfo.pageType == 'storeView' or allAllocateInfo.pageType eq 'storeEdit'}">
					<c:choose>
						<c:when test="${allAllocateInfo.pageType == 'storeView'}">
							<li class="active"><a href="#"
								onclick="javascript:return false;"><spring:message
								code="allocation.show.detail" /></a></li>
						</c:when>
						<c:otherwise>
							<li class="active"><a href="#"
								onclick="javascript:return false;"><spring:message
								code="allocation.imp.blank.doc.quota.register" /></a></li>
						</c:otherwise>
					</c:choose>
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
				test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' || allAllocateInfo.pageType eq 'storeEdit'}">

				<%-- 网点端显示 --%>
				<shiro:hasPermission name="allocation:point:edit">
					<c:choose>
						<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
							<%-- 重空类型 --%>
							<sys:blbiSelect type="blankBillselect"/>
							<%-- 预约数量 --%>
							<div class="control-group item">
								<label class="control-label" style="width: 120px;"><spring:message
										code="allocation.order.number" />：</label>
								<div class="controls" style="margin-left: 130px;">
									<form:input id="moneyNumberOrder"
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
				</shiro:hasPermission>

				<%-- 库房端显示--%>
				<shiro:hasPermission name="allocation:store:edit">
					<c:choose>
						<c:when test="${allAllocateInfo.pageType eq 'storeEdit'}">
							<%-- 编辑状态 显示 --%>
							<%-- 重空类型 --%>
							<form:hidden path="allAllocateItem.currency" />
							<div class="control-group item">
								<label class="control-label" style="width: 120px;"><spring:message
										code="common.importantEmpty.kind" />：</label>
								<div class="controls" style="margin-left: 130px;">
									<form:input path="stoBlankBillSelect.blankBillKind"
												value="${sto:getGoodDictLabel(allAllocateInfo.stoBlankBillSelect.blankBillKind,'blank_bill_kind','')}"
												style="width:150px;" disabled="true" />
								</div>
								<form:hidden path="stoBlankBillSelect.blankBillKind" />
							</div>
							<div class="clear"></div>
							<%-- 重空类型 --%>
							<form:hidden path="allAllocateItem.currency" />
							<div class="control-group item">
								<label class="control-label" style="width: 120px;"><spring:message
										code="common.importantEmpty.type" />：</label>
								<div class="controls" style="margin-left: 130px;">
									<form:input path="stoBlankBillSelect.blankBillType"
												value="${sto:getGoodDictLabel(allAllocateInfo.stoBlankBillSelect.blankBillType,'blank_bill_type','')}"
												style="width:150px;" disabled="true" />
								</div>
								<form:hidden path="stoBlankBillSelect.blankBillType" />
							</div>
							<div class="clear"></div>
							<%-- 预约数量 --%>
							<div class="control-group item">
								<label class="control-label" style="width: 120px;"><spring:message
										code="allocation.order.number" />：</label>
								<div class="controls" style="margin-left: 130px;">
									<form:input id="moneyNumberOrder"
										path="allAllocateItem.moneyNumber" style="width:150px;"
										disabled="true" />
								</div>
							</div>
							<div class="clear"></div>
							<%-- 装配数量 --%>
							<div class="control-group item">
								<label class="control-label" style="width: 120px;"><spring:message
										code="allocation.imp.blank.doc.quota.number" />：</label>
								<div class="controls" style="margin-left: 130px;">
									<form:input id="moneyNumberQuota"
										path="allAllocateItem.confirmNumber" htmlEscape="false"
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
				</shiro:hasPermission>
			</c:when>
		</c:choose>
	</form:form>
	<br />
	<%-- 重空详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">

		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />
		<div id="detailDiv"
			style="margin-left: 102px; height: 305px; width: 500px; overflow: auto; overflow-x: hidden;">
			<shiro:hasPermission name="allocation:store:edit">
				<c:choose>
					<c:when test="${allAllocateInfo.pageType == 'storeView' or allAllocateInfo.pageType eq 'storeEdit'}">
						<%-- 网点名称 库房端显示--%>
						<label><spring:message
								code='common.outletsName' />：${allAllocateInfo.rOffice.name}</label>
						<br />
					</c:when>
				</c:choose>
			</shiro:hasPermission>
			<table class="table table-hover">
				<thead>
					<tr>
						<%-- 重空分类 --%>
						<th style="text-align: center"><spring:message
								code='common.importantEmpty.kind' /></th>
						<%-- 重空类型 --%>
						<th style="text-align: center"><spring:message
								code='common.importantEmpty.type' /></th>
						<%-- 预约数量 --%>
						<th style="text-align: center"><spring:message
								code='allocation.order.number' /></th>
						
						<shiro:hasPermission name="allocation:store:edit">
							<c:choose>
								<c:when test="${allAllocateInfo.pageType == 'storeView' or allAllocateInfo.pageType eq 'storeEdit'}">
									<%-- 装配数量 --%>
									<th style="text-align: center"><spring:message
											code='allocation.imp.blank.doc.quota.number' /></th>
								</c:when>
							</c:choose>
						</shiro:hasPermission>
						<c:choose>
							<c:when test="${allAllocateInfo.pageType eq 'pointView'}">
								<%-- 装配数量 --%>
								<th style="text-align: center"><spring:message
										code='allocation.imp.blank.doc.quota.number' /></th>
							</c:when>
						</c:choose>
						<c:choose>
							<%-- 编辑状态 显示 --%>
							<c:when
								test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' || allAllocateInfo.pageType eq 'storeEdit'}">
								<%-- 操作 --%>
								<th style="text-align: center"><spring:message
										code='common.operation' /></th>
							</c:when>
						</c:choose>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${allocationImpBlankDoc.allAllocateItemMap}"
						var="allAllocateItem" varStatus="status">
						<tr>
							<%-- 重空分类 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.currency,'blank_bill_kind','')}
							</td>
							<%-- 重空类型 --%>
							<td style="text-align: center">
								${sto:getGoodDictLabel(allAllocateItem.value.classification,'blank_bill_type','')}
							</td>
							<%-- 预约数量 --%>
							<td style="text-align: center">
								${allAllocateItem.value.moneyNumber}</td>
							<shiro:hasPermission name="allocation:store:edit">
								<c:choose>
									<c:when test="${allAllocateInfo.pageType eq 'storeView' or allAllocateInfo.pageType eq 'storeEdit'}">
										<%-- 装配数量 --%>
										<c:choose>										
											<c:when test="${allAllocateInfo.pageType eq 'storeView' and allocationImpBlankDoc.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
												<td style="text-align: center"><fmt:formatNumber
														value="0" pattern="0" /></td>
											</c:when>
											<c:otherwise>
												<td style="text-align: center">
												${allAllocateItem.value.confirmNumber}</td>
											</c:otherwise>
										</c:choose>
									</c:when>
								</c:choose>
							</shiro:hasPermission>
							<%-- 网点端显示 --%>
							<c:choose>
								<c:when test="${allocationImpBlankDoc.pageType eq 'pointView'}">

									<c:choose>
										<%-- 未装配 --%>
										<c:when
											test="${allocationImpBlankDoc.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
											<td style="text-align: center"><fmt:formatNumber
													value="0" pattern="0" /></td>
										</c:when>
										<%-- 已装配 --%>
										<c:otherwise>
											<td style="text-align: center">
												${allAllocateItem.value.confirmNumber}</td>
										</c:otherwise>
									</c:choose>
								</c:when>
							</c:choose>
							<%-- 操作 --%>
							<c:choose>
								<%-- 编辑状态 显示 --%>
								<c:when
									test="${allAllocateInfo.pageType eq 'pointEdit' || allAllocateInfo.pageType eq 'pointAdd' || allAllocateInfo.pageType eq 'storeEdit'}">
									<td style="text-align: center">
										<%-- 库房端显示 --%> 
										<shiro:hasPermission name="allocation:store:edit">
											<c:choose>
												<c:when test="${allAllocateInfo.pageType eq 'storeEdit'}">
													<a href="javascript:void(0);"
														onclick="editDetail('${allAllocateItem.value.allItemsId}')" title="编辑">
														<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
													</a>
												</c:when>
											</c:choose>
										</shiro:hasPermission> 
										<%-- 网点端显示 --%> 
										<shiro:hasPermission
											name="allocation:point:edit">
											<c:choose>
												<c:when test="${allAllocateInfo.pageType == 'pointEdit' or allAllocateInfo.pageType == 'pointAdd'}">
													<a href="${ctx}/allocation/v01/impBlankDocOrder/save?saveType=delete&detailId=${allAllocateItem.value.allItemsId}&allId=${allocationImpBlankDoc.allId}"
														onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
														<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
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
					<%-- 保存 --%>
					<input id="btnSubmit" class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>" />
					&nbsp;
				</c:when>
			</c:choose>
			<c:if test="${allAllocateInfo.pageType eq 'storeView'}">
				<!-- 打印 -->
				<input id="btnPrint" type="button" class="btn btn-primary" value="<spring:message code='common.print'/>" />
			</c:if>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/impBlankDocOrder/back?allId=${allAllocateInfo.allId}'" />
		</div>
	</form:form>
</body>
</html>
