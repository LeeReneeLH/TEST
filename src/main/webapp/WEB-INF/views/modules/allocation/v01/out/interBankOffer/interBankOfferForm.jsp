<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 申请代理登记 --%>
	<title><spring:message code="allocation.application.agent.handin.registe" /></title>
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
            	var url = "${ctx}/allocation/v02/pbocAgentHandinOrder/add";
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			
         	// 提交按钮
			$("#btnSubmit").click(function(){
				$("#moneyNumber").removeClass();
				$("#inputForm").attr("action","${ctx}/allocation/v02/pbocAgentHandinOrder/save?pageType=bussnessApplicationList&strUpdateDate=" + $("#strUpdateDate").val());
				$("#inputForm").submit();
			});
		});
		// 修改物品
		function updateItem(goodsId) {
			//清除tr背景色
			clearTrBackgroundColor();
			var content = "iframe:${ctx}/allocation/v02/pbocAgentHandinOrder/toUpdateGoodsItem?goodsId=" + goodsId;
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
								// 币种
								var goodsId = contentWindow.document.getElementById("goodsId").value;
								
								var url = "${ctx}/allocation/v02/pbocAgentHandinOrder/updateGoodsItem";
								url = url + "?goodsId=" + goodsId;
								url = url + "&moneyNumber=" + moneyNumber;
								$("#moneyNumber").removeClass();
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
	<style type="text/css">
		.table thead tr { 
			display:block; 
			} 
			.table tbody { 
			display: block; 
			height: 50px; 
			overflow: auto; 
			} 
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 申请代理列表 --%>
		<li><a href="${ctx}/allocation/v02/pbocAgentHandinOrder/list?pageType=bussnessApplicationList&bInitFlag=true"><spring:message code="allocation.application.agent.handin.list" /></a></li>
		<%-- 申请代理登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.application.agent.handin.registe" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="allId"/>
		<form:hidden path="version"/>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span11">
				<div class="control-group item">
					<%-- 申请机构 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.application.office" />：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:input path="rofficeName" readOnly="readOnly" style="width:149px;"/>
					</div>
				</div>
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.order.date" />：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<input id="applyDate"  name="applyDate" type="text" readOnly="readOnly" maxlength="20" class="input-medium Wdate required" style="width:130px;"
					   	value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>" 
					   	onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '%y-%M-%d'});"/>
					</div>
				</div>
				<div class="control-group item">
					<%-- 代理机构 --%>
					<label class="control-label" style="width:80px;"><spring:message code="common.agent.office" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<%-- <form:hidden path="agentOffice.id"/>
						<form:input path="agentOffice.name" readOnly="readOnly" style="width:149px;"/> --%>
				   		<sys:treeselect id="agentOfficeId" name="agentOffice.id"
							value="${pbocAllAllocateInfo.agentOffice.id}" labelName="agentOffice.name"
							labelValue="${pbocAllAllocateInfo.agentOffice.name}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-small" 
							allowClear="true" notAllowSelectParent="true"
							type="6" isAll="true" isSameLevelClearCenter="true"/>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span9">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				<div class="clear"></div>
				<sys:pbocgoodselect currencyReserve="${fns:getConfig('sto.relevance.currency.cny')}"
			     classificationReserve="${fns:getConfig('sto.goods.classification.currencyNote')},${fns:getConfig('sto.goods.classification.damaged.recounting.no')}"/>
			    <div class="clear"></div>
				<div class="control-group item">
					<label class="control-label" style="width: 80px;"><spring:message
							code="common.number" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:input id="moneyNumber"
							path="stoGoodSelect.moneyNumber" htmlEscape="false"
							maxlength="3" style="width:117px;text-align:right;" class="digits required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="clear"></div>
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
	</form:form>
	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${agentHandinOrderSession.strUpdateDate }">
	<div class="row" style="margin-left:10px">
		
		<div class="span1" style="text-align: left">
			<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
		</div>
		<div class="span8" style="text-align: right">
			<%-- 申请总金额 --%>
			<label ><spring:message code="allocation.application.totalAmount" />：</label>
				<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${agentHandinOrderSession.registerAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
			<%-- 申请总金额(格式化) --%>
			<label style="margin-left:10px">${agentHandinOrderSession.registerAmountBig}</label>
    	</div>
	</div>
	<div class="row" style="margin-left:10px">
		<div class="span9">
			<div style="overflow-y: auto; height: 315px;">
				<!-- <table id="contentTable" class="table table-striped table-bordered table-condensed table-hover" >
					<thead> -->
					<table id="contentTable" width="100%" style="font-size:14px; border:1px solid #ddd; color:#333; ">
						<thead style="background-color:#bbdcfc;">
						<tr>
							<%-- 申请明细 --%>
							<th style="text-align: center" colspan="5"><spring:message code="allocation.application.detail" /></th>
						</tr>
						<tr>
							<%-- 序号 --%>
							<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
							<%-- 物品名称 --%>
							<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
							<%-- 申请数量 --%>
							<th style="text-align: center" ><spring:message code="allocation.application.number" /></th>
							<%-- 申请金额(元) --%>
							<th style="text-align: center" ><spring:message code="allocation.application.amount" /><spring:message code="common.units.yuan.alone" /></th>
							<%-- 操作（删除/修改） --%>
							<th style="text-align: center" ><spring:message code='common.operation'/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${agentHandinOrderSession.pbocAllAllocateItemList}" var="item" varStatus="status">
							<tr id="${item.goodsId}">
								<td style="text-align:right;">${status.index + 1}</td>
								<td style="text-align:right;"><a href="#" onclick="updateItem('${item.goodsId}');javascript:return false;">${item.goodsName}</a></td>
								<td style="text-align:right;">${item.moneyNumber}</td>
								<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
								<td style="text-align:center;">
									<a href="${ctx}/allocation/v02/pbocAgentHandinOrder/deleteGoods?goodsId=${item.goodsId}"
									   onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)">
									   <spring:message code="common.delete" />
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>	
	<div class="row" style="margin-left:10px">
		<div class="span9">
			<div class="form-actions">
				<shiro:hasPermission name="allocation.v02:businessOrder:edit">
					<%-- 保存 --%>
					<input id="btnSubmit" class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>"/>
					&nbsp;
				</shiro:hasPermission>
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/allocation/v02/pbocAgentHandinOrder/back?pageType=bussnessApplicationList'"/>
			</div>
		</div>
	</div>
</body>
</html>