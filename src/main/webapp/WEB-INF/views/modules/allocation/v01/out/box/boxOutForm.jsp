<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 取款审批登记 --%>
	<title><spring:message code="allocation.allocated.approve.registe" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//清除tr背景色
			clearTrBackgroundColor();
			var existsGoodsId = "${existsGoodsId}";
			// 如果添加物品重复，标记物品列表对应物品行背景色
			if (typeof(existsGoodsId) != '') {
				$("#" + existsGoodsId).addClass("alert-info");
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
			// 添加物品
            $("#add").click(function () {
            	//清除tr背景色
            	clearTrBackgroundColor();
            	var url = "${ctx}/allocation/v01/boxHandover/add";
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			
			// 审批
			$("#btnApproval").click(function(){
				$("#moneyNumber").removeClass();
                var url = "${ctx}/allocation/v01/boxHandover/aloneOption?targetStatus=10&strUpdateDate=" + $("#strUpdateDate").val();
	            $("#inputForm").attr("action", url);
				$("#inputForm").submit();
			});
		});
		function updateItem(goodsId) {	
			//清除tr背景色
			clearTrBackgroundColor();
			var content = "iframe:${ctx}/allocation/v01/boxHandover/toUpdateGoodsItem?goodsId=" + goodsId;
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
								var url = "${ctx}/allocation/v01/boxHandover/updateGoodsItem";
								url = url + "?goodsId=" + goodsId;
								url = url + "&moneyNumber=" + moneyNumber;
								$("#moneyNumber").removeClass();
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
		//清除tr背景色
		function clearTrBackgroundColor(){
			$("tr").removeClass();
		}
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 取款审批列表 --%>
				<li><a href="${ctx}/allocation/v01/boxHandover/handout"><spring:message code="allocation.cash.box.handout.list" /></a></li>
		<%-- 取款审批登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cash.order.approve" /></a></li>
	</ul><br/>
	<sys:message content="${message}"/>	
	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${allocatedOrderSession.strUpdateDate }">
	<!-- 申请明细部分 -->
	<div class="row" style="margin-left:10px">
		<div class="span8">
			<div class="row" >
				<div class="span8">
					<h4  style="border-bottom:1px solid #eee;color:#dc776a;text-align:center;margin-bottom:15px;">
						<spring:message code="allocation.order.detail" />
					</h4>
				</div>
			</div>
			<div class="row" >
				<div class="span8">
					<form:form id="tempform" class="form-horizontal">
						<div class="control-group item">
							<%-- 流水单号 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.allId" />：</label>
							<div class="controls" style="margin-left: 90px;">
								<input id="allId"  name="allId" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allocatedOrderSession.allId}"/>
							</div>
						</div>
						<div class="control-group item"style="margin-left: 20px;">
							<%-- 业务种别 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.business.type" />：</label>
							<div class="controls" style="margin-left: 90px;">
								<input id="businessType"  name="businessType" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${fns:getDictLabel(allocatedOrderSession.businessType, 'all_businessType', '')}"/>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 预约机构 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.order.office" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="rofficeName"  name="rofficeName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allocatedOrderSession.rOffice.name}"/>
							</div>
						</div>
						<div class="control-group item"style="margin-left: 20px;">
							<%-- 申请类型 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.tasktype" />：</label>
							<div class="controls" style="margin-left: 90px;">
								<input id="taskType"  name="taskType" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${fns:getDictLabel(allocatedOrderSession.taskType, 'TASK_TYPE', '')}"/>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
						</div>
					</form:form>
				</div>
			</div>
			<%-- 申请明细分隔线 --%>
			<div class="row" style="margin-top: 100px">
				<div class="span8">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				</div>
			</div>
			<div class="row">
		    	<div class="span8" style="text-align: left">
				<%-- 预约总金额 --%>
					<label ><spring:message code="allocation.cash.order.amount.all" />：</label>
						<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${allocatedOrderSession.registerAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:170px;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="row">
				<div class="span8">
					<div style="overflow-y: auto; height: 315px;">
					<%-- <h4  style="border-top:1px solid #eee;color:#dc776a;text-align:center;"><spring:message code="allocation.application.detail" /></h4>
					 --%>	<table id="contentTable" class="table table-hover" style="border-top:1px solid #eee;">
							<thead>
								<%-- <tr>
									申请明细表头
									<th style="text-align: center" colspan="4"><spring:message code="allocation.application.detail" /></th>
								</tr> --%>
								<tr>
									<%-- 序号 --%>
									<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
									<%-- 券别信息 --%>
									<th style="text-align: center" ><spring:message code="allocation.classificationInfo" /></th>
									<%-- 预约数量 --%>
									<th style="text-align: center" ><spring:message code="allocation.order.number" /></th>
									<%-- 预约金额(元) --%>
									<th style="text-align: center" ><spring:message code="allocation.cash.order.amount" /></th>
									
								</tr>
							</thead>
							<tbody>
								<% int iRegistIndex = 0; %>
								<c:forEach items="${allocatedOrderSession.allAllocateItemList}" var="item" varStatus="status">
									<%-- 申请登记物品 --%>
									<c:if test="${item.confirmFlag == '0'}">
										<% iRegistIndex = iRegistIndex + 1; %>
										<tr>
											<td style="text-align:right;"><%=iRegistIndex %></td>
											<td style="text-align:right;">${item.goodsName}</td>
											<td style="text-align:right;">${item.moneyNumber}</td>
											<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!-- 审批明细部分 -->
		<div class="span9" >
			<div class="row" >
				<div class="span9">
				<h4  style="border-bottom:1px solid #eee;color:#34b15b;text-align:center;margin-bottom:15px;">
					
						<b><spring:message code="allocation.approve.detail" /></b>
					</h4>
				</div>
			</div>
			<div class="row">
				<div class="span10" style="margin-left: -5px">
					<form:form id="inputForm" modelAttribute="allAllocateInfo"
						action="" method="post" class="form-horizontal">
						<form:hidden path="allId" />
						<form:hidden path="version" />

						<div class="clear"></div>
						<%-- 现金预约：“类别”保留“流通币”、“ATM” “单位”保留“捆”、“把”、“张”、“盒”、“卷”、“枚” --%>
						<sys:goodselect
							classificationReserve="${fns:getConfig('sto.goods.classification.circulation')},${fns:getConfig('sto.goods.classification.atm')},${fns:getConfig('sto.goods.classification.waitClear')}"
							unitReserve="${fns:getConfig('sto.goods.unit.bundle')},${fns:getConfig('sto.goods.unit.control')},${fns:getConfig('sto.goods.unit.sheet')},${fns:getConfig('sto.goods.unit.box')},${fns:getConfig('sto.goods.unit.roll')},${fns:getConfig('sto.goods.unit.trunk')}" />
						<div class="clear"></div>
						<!-- 审批数量 -->
						<div class="control-group item">
							<label class="control-label" style="width: 80px;"><spring:message
									code="common.number" />：</label>
							<label>
								<form:input id="moneyNumber" path="stoGoodSelect.moneyNumber"
									htmlEscape="false" maxlength="5"
									style="width:150px;text-align:right;" class="digits required" />
								<span class="help-inline"><font color="red">*</font> </span>
							</label>
						</div>
						<div class="clear"></div>
						<c:if test="${ allocatedOrderSession.taskType eq 2}">
							
							<div class="controls" style="margin-left: 0px" >
								<label class="control-label" style="width: 80px;"><spring:message code="allocation.tasktype.car_no" />：</label>
								<select id="carNo" name="carNo" class="input-medium required" >
									<c:if test="${not empty allocatedOrderSession.carNo}">
										<option value="${allocatedOrderSession.carNo}"
											selected="selected">${allocatedOrderSession.carNo}</option>
									</c:if>
									<c:if test="${empty allocatedOrderSession.carNo}">
										<option value="" selected="selected"><spring:message code="common.select" /></option>
									</c:if>
									<c:forEach var="carNo" items="${sto:getStoCarInfoListByPoint(allocatedOrderSession.rOffice.id)}">

										<c:if test="${!(allocatedOrderSession.carNo eq carNo.carNo)}">
											<option value="${carNo.carNo}">${carNo.carNo}</option>
										</c:if>
									</c:forEach>

								</select> <span class="help-inline"><font color="red">*</font> </span>
							</div>
						</c:if>
					</form:form>
				</div>
			</div>
					<input type="hidden"  value="${allocatedOrderSession.pageType}" />		
			<!-- 审批明细分隔线 -->
			<div class="row" >
				<c:if test="${ allocatedOrderSession.taskType eq 2}">
					<div class="span9" style="margin-top:46px">
						<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
					</div>
				</c:if>	
				<c:if test="${ allocatedOrderSession.taskType eq 1}">
					<div class="span9" style="margin-top:74px">
						<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
					</div>
			</c:if>
			</div>
			<div class="row">
				<div class="span8" style="text-align: left">
					<%-- 审批总金额 --%>
					<label ><spring:message code="allocation.approve.totalAmount" />：</label>
						<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${allocatedOrderSession.confirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:170px;" readOnly="readOnly"/>
		    	</div>
		    	<!-- 添加按钮 -->
		    	<div class="span1" style="text-align: left;">
					<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
				</div>
			</div>
			<!-- 审批明细列表 -->
			<div class="row">
				<div class="span10">
					 <div style="overflow-y: auto; height: 315px;">
					<%--  <h4  style="border-top:1px solid #eee;color:#34b15b;text-align:center;">
					 <spring:message code="allocation.approve.detail" />
				</h4> --%>
						<table id="contentTable" class="table table-hover" style="border-top:1px solid #eee;">
							<thead>
								<%-- <tr>
									审批明细表头
									<th style="text-align: center" colspan="5"><spring:message code="allocation.approve.detail" /></th>
								</tr> --%>
								<tr>
									<%-- 序号 --%>
									<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
									<%-- 券别信息 --%>
									<th style="text-align: center" ><spring:message code="allocation.classificationInfo" /></th>
									<%-- 审批数量 --%>
									<th style="text-align: center" ><spring:message code="allocation.approve.number" /></th>
									<%-- 审批金额 --%>
									<th style="text-align: center" ><spring:message code="allocation.approve.amount" /></th>
									<%-- 操作（删除/修改） --%>
									<th style="text-align: center" ><spring:message code='common.operation'/></th>
								</tr>
							</thead>
							<tbody>
								<% int iConfirmIndex = 0; %>
								<c:forEach items="${allocatedOrderSession.allAllocateItemList}" var="item" varStatus="status">
									<%-- 审批登记物品 --%>
									<c:if test="${item.confirmFlag == '1'}">
										<% iConfirmIndex = iConfirmIndex + 1; %>
										<tr id="${item.goodsId}">
											<td style="text-align:right;"><%=iConfirmIndex %></td>
											<td style="text-align:right;"><a href="#" onclick="updateItem('${item.goodsId}');javascript:return false;">${item.goodsName}</a></td>
											<td style="text-align:right;">${item.moneyNumber}</td>
											<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											<td style="text-align:center;">
												<a href="${ctx}/allocation/v01/boxHandover/deleteGoods?goodsId=${item.goodsId}"
												   onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
												   <%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
												</a>
											</td>
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="span12">
					<div class="form-actions" style="text-align: right;width:740px;">				
							<%-- 审批 --%>
							<input type="button" id="btnApproval" value="<spring:message code='common.confirm' />" class="btn btn-primary">&nbsp;			
						<%-- 返回 --%>
						<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashOrder/back?allId=${allAllocateInfo.allId}&pagetype=${allAllocateInfo.pageType} '" />
					</div>
				</div>
			</div>
		</div>
	</div>	
	
</body>
</html>