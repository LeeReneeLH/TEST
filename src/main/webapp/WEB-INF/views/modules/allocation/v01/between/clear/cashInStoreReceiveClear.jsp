<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 入库接收确认 --%>
	<title><spring:message code="allocation.inStore.receive.confirm" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//清除tr背景色
			$("#inputForm").validate(
				{   submitHandler : function(form) {
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
	     	// 提交按钮
			$("#btnSubmit").click(function(){
				$("#commitForm").attr("action","${ctx}/allocation/v01/cashBetweenClear/saveCashReceiveInfo?allId=" + $("#allId").val());
				$("#commitForm").submit();
			});
		});
	</script>
	<style>
		<!--
		/* 输入项 */
		.item {display: inline; float: left;}
		/* 清除浮动 */
		.clear {clear:both;}
		/* 标签宽度 */
		.label_width {width:120px;}
		-->
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库间清分列表 --%>
		<li><a href="${ctx}/allocation/v01/cashBetweenClear/list"><spring:message code="allocation.cashBetween.clear.list" /></a></li>
		<%-- 清分出库登记 --%>
		<li><a href="${ctx}/allocation/v01/cashBetweenClear/toCashOutStoreForm"><spring:message code="allocation.clear.outStore.register" /></a></li>
		<%-- 入库接收确认 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.inStore.receive.confirm" /></a></li>
	</ul><br/>
	
	<div class="row" style="margin-left:10px">
		<div class="span8">
			<div class="row" >
				<div class="span8">
					<div class="alert alert-info">
						<%-- 出库明细 --%>
						<b><spring:message code="allocation.outStore.detail" /></b>
					</div>
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
							   	value="${allAllocateInfo.allId}"/>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 登记类型 --%>
							<label class="control-label" style="width:80px;"><spring:message code="clear.clErrorInfo.registerType" />：</label>
							<div class="controls" style="margin-left: 90px;">
								<input id="businessType"  name="businessType" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${fns:getDictLabel(allAllocateInfo.businessType, 'all_businessType', '')}"/>
							</div>
						</div>
						<div class="control-group item">
							<%-- 登记机构 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.register.office" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="rofficeName"  name="rofficeName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.rOffice.name}"/>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 出库登记 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.outStore.register" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="createName"  name="createName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.createName}"/>
							</div>
						</div>
						<div class="control-group item">
							<%-- 出库日期 --%>
							<label class="control-label" style="width:80px;"><spring:message code="common.outDate" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="createDate"  name="createDate" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="<fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"  />
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 接收人员 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.receive.person" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="receiveName"  name="receiveName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.clearInReceiveName}"/>
							</div>
						</div>
						<div class="control-group item">
							<%-- 接收日期 --%>
							<label class="control-label" style="width:80px;"><spring:message code="clear.clearConfirm.receiveDate" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="receiveDate"  name="receiveDate" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="<fmt:formatDate value="${allAllocateInfo.clearInReceiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"  />
							</div>
						</div>
						<div class="clear"></div>
					</form:form>
				</div>
			</div>
			<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			<div class="row" style="padding-left: 30px">
		    	<div class="span8" style="text-align: left">
					<%-- 登记金额 --%>
					<label><spring:message code="allocation.register.amount" />：</label>
					<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.registerAmount}" 
					pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:120px;" readOnly="readOnly"/>
					<%-- 登记金额(格式化) --%>
					<label style="margin-left:150px;">${sto:getUpperAmount(allAllocateInfo.registerAmount)}</label>
				</div>
			</div>
			<div class="row">
				<div class="span8">
					<div style="overflow-y: auto; height: 215px;">
						<table id="contentTable" class="table table-hover" >
							<thead>
								<tr>
									<%-- 出库明细 --%>
									<th style="text-align: center" colspan="5"><spring:message code="allocation.outStore.detail" /></th>
								</tr>
								<tr>
									<%-- 序号 --%>
									<th style="text-align: center"><spring:message code="common.seqNo" /></th>
									<%-- 物品名称 --%>
									<th style="text-align: center"><spring:message code="store.goodsName" /></th>
									<%-- 数量 --%>
									<th style="text-align: center"><spring:message code="common.number" /></th>
									<%-- 金额 --%>
									<th style="text-align: center"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								</tr>
							</thead>
							<tbody>
								<% int iRegistIndex = 0; %>
								<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
									<c:if test="${item.confirmFlag == '0'}">
										<% iRegistIndex = iRegistIndex + 1; %>
										<tr id="${item.goodsId}">
											<td style="text-align:center;"><%=iRegistIndex %></td>
											<td style="text-align:center;">${sto:getGoodsName(item.goodsId)}</td>
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
		<div class="span8" >
			<div class="row" >
				<div class="span8">
					<div class="alert alert-info">
						<%-- 入库明细 --%>
						<b><spring:message code="allocation.inStore.detail" /></b>
					</div>
				</div>
			</div>
			<div class="row" >
				<%-- 清分明细 --%>
				<div class="span8">
					<form:form id="inputForm" class="form-horizontal">
						<div class="control-group item">
							<%-- 清分机构 --%>
							<label class="control-label" style="width:80px;"><spring:message code="common.agent.office" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="aofficeName"  name="aofficeName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.aOffice.name}"/>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 接收人员 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.receive.person" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="confirmName"  name="confirmName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.confirmName}"/>
							</div>
						</div>
						<div class="control-group item">
							<%-- 接收日期 --%>
							<label class="control-label" style="width:80px;"><spring:message code="clear.clearConfirm.receiveDate" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="confirmDate"  name="confirmDate" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="<fmt:formatDate value="${allAllocateInfo.confirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"  />
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 入库登记 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.inStore.register" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="clearName"  name="clearName" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="${allAllocateInfo.clearInRegisterName}"/>
							</div>
						</div>
						<div class="control-group item">
							<%-- 清分日期 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.clearDate" />：</label>
							<div class="controls" style="margin-left: 90px;">
						   		<input id="clearDate"  name="clearDate" type="text" readOnly="readOnly"  style="width:170px;"
							   	value="<fmt:formatDate value="${allAllocateInfo.clearInRegisterDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"  />
							</div>
						</div>
					</form:form>
				</div>
			</div>
			<div style="height: 38px;"></div>
			<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			<div class="row" style="padding-left: 30px">
				<div class="span8" style="text-align: left">
					<%-- 接收金额 --%>
					<label><spring:message code="allocation.confirm.amount" />：</label>
					<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.confirmAmount}" 
					pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:120px;" readOnly="readOnly"/>
		    		<%-- 接收金额(格式化) --%>
		    		<label style="margin-left:150px;">${sto:getUpperAmount(allAllocateInfo.confirmAmount)}</label>
		    	</div>
			</div>
			<div class="row">
				<div class="span8">
					 <div style="overflow-y: auto; height: 215px;">
					 	
						<table id="contentTable" class="table table-hover" >
							<thead>
								<tr>
									<%-- 入库明细 --%>
									<th style="text-align: center" colspan="5"><spring:message code="allocation.inStore.detail" /></th>
								</tr>
								<tr>
									<%-- 序号 --%>
									<th style="text-align: center"><spring:message code="common.seqNo" /></th>
									<%-- 物品名称 --%>
									<th style="text-align: center"><spring:message code="store.goodsName" /></th>
									<%-- 数量 --%>
									<th style="text-align: center"><spring:message code="common.number" /></th>
									<%-- 金额 --%>
									<th style="text-align: center"><spring:message code="common.amount" /></th>
								</tr>
							</thead>
							<tbody>
								<% int iConfirmIndex = 0; %>
								<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
										<c:if test="${item.confirmFlag == '1'}">
											<% iConfirmIndex = iConfirmIndex + 1; %>
											<tr id="${item.goodsId}">
												<td style="text-align:center;"><%=iConfirmIndex %></td>
												<td style="text-align:center;">${sto:getGoodsName(item.goodsId)}</a></td>
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
	</div>	
	<div class="row">
		<form:form id="commitForm">
			<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${allAllocateInfo.strUpdateDate}">
		</form:form>
		<div class="span10">
			<div class="form-actions" style="text-align: left;">
				<%-- 确认接收 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code="allocation.confirm.receive" />"/>
					&nbsp;
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/allocation/v01/cashBetweenClear/back'"/>
			</div>
		</div>
	</div>
</body>
</html>