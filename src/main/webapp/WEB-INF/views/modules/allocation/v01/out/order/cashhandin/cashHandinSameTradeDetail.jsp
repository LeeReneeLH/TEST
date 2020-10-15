<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><c:choose>
		<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd'}">
			<%-- 同业上缴登记--%>
			<spring:message code="allocation.cash.handin.same.trade.register" />
		</c:when>
		<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
			<%-- 同业上缴修改--%>
			<spring:message code="allocation.cash.handin.same.trade.update" />
		</c:when>
		<c:otherwise>
			<%-- 同业上缴明细查询 --%>
			<spring:message code="allocation.cash.trade.handin.show.detail" />
		</c:otherwise>
	</c:choose></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	var submitFlag;
	$(document)
			.ready(
					function() {
						resetSubmitFlag();
						if ($("#rOfficeIdName").val() != '' && "${allAllocateInfo.pageType}" != "storeSameTradeView") {
							$('#sameTradBoxNum').html("");
							var rOfficeId = $("#rOfficeIdId").val();
							if ("${allAllocateInfo.pageType}" == "storeSameTradeEdit") {
								rOfficeId = "${allocationHandinCash.rOffice.id}";
							}
							getHandInBoxNo(rOfficeId);
						}
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
															"${ctx}/allocation/v01/cashHandin/saveSameTrade?saveType=insert");
											$("#inputForm").submit();
										});

						// 提交
						$("#btnSubmit")
								.click(
										function() {
											if (submitFlag != true) {
												submitFlag = true;
												$("#detailForm").attr("action",
														"${ctx}/allocation/v01/cashHandin/saveSameTrade?saveType=save");
												$("#detailForm").submit();
											}
											return;
										});
						$("#rOfficeIdId").on('propertychange',
							function() {
								var rOfficeId = $("#rOfficeIdId").val();
								if (rOfficeId == '') {
									$('#sameTradBoxNum').html("");
								} else {
									getHandInBoxNo(rOfficeId);
								}
							}		
						);
						
					});

	// 根据网点ID取得上缴钞箱编号
	function getHandInBoxNo(rOfficeId){
		var url = '${ctx}/allocation/v01/cashHandin/getSameTradeBoxNo';
		$.ajax({
			type : "POST",
			dataType : "json",
			url : url,
			data : {
				param : rOfficeId
			},
			success : function(serverResult, textStatus) {
				if (serverResult.length == 0) {
					$('#sameTradBoxNum').addClass( "error");
					$('#sameTradBoxNum').html("<spring:message code='message.I2009'/>");
					$("#add").attr("disabled", true);
					return;
				}
				var strBoxNos = '';
				// 其他选项
				for (var iIndex = 0; iIndex < serverResult.length; iIndex++){
					strBoxNos = strBoxNos + serverResult[iIndex];
					if (iIndex != (serverResult.length - 1)) {
						strBoxNos = strBoxNos + '，';
					}
				}
				$('#sameTradBoxNum').removeClass( "error");
				$('#sameTradBoxNum').html(strBoxNos);
				$("#add").removeAttr("disabled");
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
				// TODO
				alert("getHandInBoxNoError");
			}
		});
	}
	// 授权
	function authorization() {
		if ($("#authorizationForm").valid()) {
			$.post($("#authorizationForm").attr("action"), $( "#authorizationForm").serialize(), function(data) {
				if (data.message == "0") {
					$("#authorization").val("authorization");
					$("#detailForm").attr("action",	"${ctx}/allocation/v01/cashHandin/saveSameTrade?saveType=save&userName=" + $("#username").val());
					$("#detailForm").submit();
				} else {
					$("#message").text(data.message);
				}
			}, "json");
		}
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
		<%-- 现金上缴列表(link) --%>

		<li><a href="${ctx}/allocation/v01/cashHandin/list"><spring:message
					code="allocation.cash.handin.list" /></a></li>

		<%-- 网点现金上缴登记(link) 只在网点登录时显示--%>
		<shiro:hasPermission name="allocation:point:edit">
			<li><a
				href="${ctx}/allocation/v01/cashHandin/form?pageType=pointAdd"><spring:message
						code="allocation.cash.handin.register" /></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="allocation:store:edit">
			<c:choose>
				<c:when test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
					<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd'}">
						<li class="active"><a
							href="#" onclick="javascript:return false;"><spring:message
									code="allocation.cash.handin.same.trade.register" /></a></li>
					</c:if>
					<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
						<li class="active"><a
							href="#" onclick="javascript:return false;"><spring:message
									code="allocation.cash.handin.same.trade.update" /></a></li>
					</c:if>			
				</c:when>
				<c:when test="${allAllocateInfo.pageType == 'storeSameTradeView'}">
					<li class="active"><a href="#"
						onclick="javascript:return false;"><spring:message code="allocation.cash.trade.handin.show.detail" /></a></li>
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
							${allocationHandinCash.rOffice.name}
						</c:if>
					</div>
				</div>
				<div class="clear"></div>
				<div class="control-group item">
					<%-- 上缴箱号 --%>
					<label class="control-label" style="width: 120px;" ><spring:message
							code="allocation.box.handin.boxNo" />：</label>
					<div class="controls" style="margin-left: 130px;">
						<label id="sameTradBoxNum"></label>
					</div>
				</div>
				<div class="clear"></div>
				<%-- 现金上缴：“类别”只保留“待整点币” --%>
				<sys:goodselect classificationReserve="${fns:getConfig('sto.goods.classification.waitClear')}"/>
				<%-- 数量 --%>
				<div class="control-group item">
					<label class="control-label" style="width: 120px;">
						<spring:message code="common.number" />：</label>
					<div class="controls" style="margin-left: 130px;">
						<form:input id="moneyNumberOrder"
							path="allAllocateItem.moneyNumber" htmlEscape="false"
							maxlength="5" style="width:150px;" class="digits required" />
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
		<form:hidden path="authorization" />
		<div id="detailDiv"
			style="margin-left: 102px; height: 305px; width: 800px; overflow: auto; overflow-x: hidden;">
			<%-- 网点名称 库房端显示--%>
			<label><spring:message
					code='common.outletsName' />：${allocationHandinCash.rOffice.name}</label>
			<br />
			<c:choose>
				<c:when
					test="${allocationHandinCash.countItemMap != null and allocationHandinCash.countItemMap.size() != 0}">
					<label> <spring:message
							code='common.totalMoney' />（<c:forEach
							items="${allocationHandinCash.countItemMap}" var="countItem"
							varStatus="status">${sto:getGoodDictLabel(countItem.value.currency,'currency',"")}：<fmt:formatNumber
								value="${countItem.value.moneyAmount}" pattern="#,##0.00#" />
							<c:choose>
								<c:when test="${status.index !=0 and status.index % 4 == 0}">
									<br />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${status.index != allocationHandinCash.countItemMap.size() - 1}">
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
						<%-- 上缴数量--%>
						<th style="text-align: center"><spring:message
								code='allocation.cash.handin.number' /></th>
						<%-- 上缴金额 --%>
						<th style="text-align: center"><spring:message
								code='allocation.cash.handin.amount' /></th>
						<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
							<%-- 操作 --%>
							<th style="text-align: center"><spring:message
									code='common.operation' /></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${allocationHandinCash.allAllocateItemMap}"
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

							<%-- 数量 --%>
							<td style="text-align: center">
								${allAllocateItem.value.moneyNumber}</td>
							<%-- 金额  --%>
							<td style="text-align: right"><fmt:formatNumber
									value="${allAllocateItem.value.moneyAmount}"
									pattern="#,##0.00#" /></td>
							<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd' or allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
								<td style="text-align: center">
								<%-- 操作 --%>
									<a href="${ctx}/allocation/v01/cashHandin/saveSameTrade?saveType=delete&detailId=${allAllocateItem.value.allItemsId}"
										onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
									</a>
								</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="form-actions">
			<%-- 保存 --%>
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeAdd'}">
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.commit'/>" />
				&nbsp;
			</c:if>
			<c:if test="${allAllocateInfo.pageType eq 'storeSameTradeEdit'}">
					<%-- 授权修改 --%>
					<input id="btnAuthorizeSubmit" class="btn btn-primary" type="submit"
						value="<spring:message code='common.authorizationModify'/>" data-toggle="modal" data-target="#myModal"/>
					&nbsp;
			</c:if>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashHandin/back?allId=${allAllocateInfo.allId}'" />
		</div>
	</form:form>
			<%-- 主管授权窗口：需要输入主管的账户 --%>
	<form:form id="authorizationForm" modelAttribute="allocation"
		action="${ctx}/allocation/v01/cashHandin/authorization" method="post"
		class="form-horizontal" onkeydown="if(event.keyCode==13){authorization();}" >

		
		<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">×</button>
				<h3 id="myModalLabel">
					<spring:message code="allocation.authorization.windows" />
				</h3>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="allocation.authorization.message" />
					：
				</p>
				<div style="color: red; margin-left: 28px;">
					<p id="message"></p>
				</div>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<%-- 用户名 --%>
				<spring:message code="common.userName" />
				： <input type="text" id="username" name="userName" class="required"
					value="${username}"> <br>
				<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<%-- 密码 --%>
				<spring:message code="allocation.authorization.pwd" />
				： <input type="password" id="password" name="password"
					class="required" />

			</div>
			<div class="modal-footer">
				<input class="btn btn-primary" type="button"
					value="<spring:message code="allocation.authorization.login"/>"
					onclick="authorization()" /> &nbsp;
				<button class="btn" data-dismiss="modal" aria-hidden="true">
					<spring:message code="allocation.authorization.close" />
				</button>
			</div>
		</div>
	</form:form>
</body>
</html>
