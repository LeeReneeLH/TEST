<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 清分入库登记 --%>
	<title><spring:message code="allocation.clear.inStore.register" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/script/modules/allocation/v01/common/updateGoodsItem.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 清除tr背景色
			clearTrBackgroundColor();
			$("#inputForm").validate(
				  { submitHandler : function(form) {
						loading('正在提交，请稍等...');
						form.submit();
					},
					// errorContainer : "#messageBox",
					errorPlacement : function(error, element) {
						// $("#messageBox").text("输入有误，请先更正。");
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
			   	}
			);
			// 添加物品
	        $("#add").click(function () {
	        	//清除tr背景色
	        	clearTrBackgroundColor();
	        	var url = "${ctx}/allocation/v01/cashBetweenClear/addClear?userCacheId=" + $("#cacheId").val();
	        	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
	        });
			
	     	// 提交按钮
			$("#btnSubmit").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
				$("#inputForm").attr("action","${ctx}/allocation/v01/cashBetweenClear/saveClearInfo?clearConfirmAmount=${clearConfirmAmount}&strUpdateDate=" 
						+ $("#strUpdateDate").val() + "&userCacheId=" + $("#cacheId").val());
				$("#inputForm").submit();
			});
		});
		
		function updateItems(goodsId, goodsName, number) {	
			//清除tr背景色
			clearTrBackgroundColor();
			var url = "${ctx}/allocation/v01/cashBetweenClear/updateClearGoodsItem?userCacheId=" + $("#cacheId").val();
			updateGoodsItem(goodsId, goodsName, number, url);
		}	
	
		//清除tr背景色
		function clearTrBackgroundColor(){
			$("tr").removeClass();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 现金调拨列表 --%>
		<li><a href="${ctx}/allocation/v01/cashBetweenClear/list"><spring:message code="allocation.cash.list" /></a></li>
		<%-- 清分入库登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.clear.inStore.register" /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="allAllocateInfo" action="" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<form:hidden path="allId"/>
		<%-- 业务类型 --%>
		<div class="control-group item" >
			<label class="control-label" style="width:80px;"><spring:message code="clear.task.business.type" />：</label>
			<div class="controls" style="margin-left:90px;">
				<input id="businessType" value="${fns:getDictLabel(allAllocateInfo.businessType, 'all_businessType', '')}" 
				type="text" htmlEscape="false" style="width:150px;" disabled="true"/>
			</div>
		</div>
		<%-- 登记机构 --%>
		<div class="control-group item" >
			<label class="control-label" style="width:80px;"><spring:message code="allocation.register.office" />：</label>
			<div class="controls" style="margin-left:90px;">
				<form:input id="rofficeName" path="rOffice.name" htmlEscape="false" style="width:150px;" disabled="true"/>
			</div>
		</div>
		<div class="clear"></div>
		<%-- 清分机构 --%>
		<div class="control-group item" >
			<label class="control-label" style="width:80px;"><spring:message code="common.agent.office" />：</label>
			<div class="controls" style="margin-left:90px;">
				<form:input id="aofficeName" path="aOffice.name" htmlEscape="false" style="width:150px;" disabled="true"/>
			</div>
		</div>
		<%--接收金额 --%>
		<div class="control-group item" >
			<label class="control-label" style="width:80px;"><spring:message code="allocation.confirm.amount" />：</label>
			<div class="controls" style="margin-left:90px;">
				<input id="registerAmount" type="text" value="<fmt:formatNumber value="${allAllocateInfo.registerAmount}"
				 pattern="#,##0.00#" />" htmlEscape="false" style="width:150px;" disabled="true" />
				<label style="margin-left:30px;">${sto:getUpperAmount(allAllocateInfo.registerAmount)}</label>
			</div>
		</div>
		<div class="clear"></div>
		<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
		<sys:goodselect classificationReserve="${fns:getConfig('sto.goods.classification.waitClear')},${fns:getConfig('sto.goods.classification.circulation')},${fns:getConfig('sto.goods.classification.atm')},${fns:getConfig('sto.goods.classification.damaged')}" type="good_manager"/>
		<%-- 数量 --%>
		<div class="control-group item">
			<label class="control-label" style="width:80px;"><spring:message code="common.number" />：</label>
			<label>
				<form:input id="moneyNumber" path="allAllocateItem.moneyNumber" htmlEscape="false" style="width:150px;" maxlength="5" class="digits required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</label>
	    </div>
	     <div class="clear"></div>
	     <HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
	</form:form>
	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${allAllocateInfo.strUpdateDate}">
	<div class ="row" style="margin-left:5px">
		<%-- 添加 --%>
		<div class="span1" style="text-align: left">
			<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
		</div>
	    <div class="span8" style="text-align: right">
			<%-- 入库金额 --%>
			<label style="padding-bottom:10px;"><spring:message code="allocation.inStore.amount" />：</label>
			<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${clearConfirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:120px;" readOnly="readOnly"/>
    		<%-- 入库总金额(格式化) --%>
			<label style="margin-left:10px;padding-top:10px;">${sto:getUpperAmount(clearConfirmAmount)}</label>
    	</div>
    	
	</div>
	<%-- 出库现金详细信息 --%>
	<div style="margin-left:25px;height:205px;width:800px;overflow:auto;overflow-x:hidden;">
		<table class="table  table-hover">
			<thead>
				<tr>
					<%-- 序号 --%>
					<th style="text-align:center"><spring:message code="common.seqNo" /></th>
					<%-- 物品名称 --%>
					<th style="text-align:center"><spring:message code="store.goodsName" /></th>
					<%-- 数量 --%>
					<th style="text-align:center"><spring:message code="common.number" /></th>
					<%-- 金额 --%>
					<th style="text-align:center"><spring:message code="common.amount" /></th>
					<%-- 操作 --%>
					<th style="text-align:center"><spring:message code='common.operation'/></th>
				</tr>
			</thead> 
			<tbody>
				<% int index = 0; %>
				<c:forEach items="${fns:getCache(userCacheId, null).allAllocateItemList}" var="item" varStatus="status">
					<c:if test="${item.confirmFlag == '1'}">
						<% index = index + 1; %>
						<tr>
							<%-- 序号 --%>
							<td style="text-align:center">
								<%=index %>
							</td>
							<%-- 物品名称 --%>
							<td style="text-align:center">
								<a href="#" onclick="updateItems('${item.goodsId}','${sto:getGoodsName(item.goodsId)}','${item.moneyNumber}');"
								>${sto:getGoodsName(item.goodsId)}</a>
							</td>
							<%-- 数量 --%>
							<td style="text-align:right;">
								${item.moneyNumber}
							</td>
							<%-- 金额 --%>
							<td style="text-align:right;">
								<fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#"/>
							</td>
							<td style="text-align:center">
								<a href="${ctx}/allocation/v01/cashBetweenClear/deleteClearGoods?goodsId=${item.goodsId}&userCacheId=${userCacheId }"
									onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="<spring:message code='common.delete' />">
									<i class="fa fa-trash-o text-red fa-lg"></i>
								</a>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<input type="hidden" id="cacheId" value="${userCacheId }">
	<div class="form-actions" style="padding-left: 25px">
		<%-- 保存 --%>
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.commit'/>" />
		&nbsp;
		<%-- 返回 --%>
		<input id="btnCancel" class="btn" type="button"
			value="<spring:message code='common.return'/>"
			onclick="window.location.href='${ctx}/allocation/v01/cashBetweenClear/back'"/>
	</div>
</body>
</html>
