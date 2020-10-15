<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 审批登记 --%>
	<title><spring:message code="allocation.approve.registe" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/script/modules/allocation/v02/common/pbocUpdateGoodsItem.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputOfficeDiv").hide();
			$("#selectOfficeDiv").show();
			$("#agentOfficeDiv").hide();
			businessTypeChange();
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
            	$("#rofficeId").removeClass();
            	var url = "${ctx}/allocation/v02/pbocApprovalRegiste/add?userCacheId=" + $("#cacheId").val();
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			
         	// 审批
			$("#btnApproval").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
                var url = "${ctx}/allocation/v02/pbocApprovalRegiste/savePbocOrder?userCacheId=" + $("#cacheId").val();
	              $("#inputForm").attr("action", url);
				  $("#inputForm").submit();
			});
		});
		
		//修改物品
		function updateItem(goodsId, goodsName, number) {
			//清除tr背景色
			clearTrBackgroundColor();
			var url = "${ctx}/allocation/v02/pbocApprovalRegiste/updateGoodsItem?userCacheId=" + $("#cacheId").val();
			
			updateGoodsItem(goodsId, goodsName, number, url);
		}

		//清除tr背景色
		function clearTrBackgroundColor(){
			$("tr").removeClass();
		}
		
		function businessTypeChange(){
			var businessType = $("#businessType").val();
			if (businessType == "53" || businessType == "54") {
				$("#inputOfficeDiv").show();
				$("#selectOfficeDiv").hide();
				$("#agentOfficeDiv").hide();
			} else if (businessType == "52") {
				$("#inputOfficeDiv").hide();
				$("#selectOfficeDiv").show();
				$("#agentOfficeDiv").show();
			} else {
				$("#inputOfficeDiv").hide();
				$("#selectOfficeDiv").show();
				$("#agentOfficeDiv").hide();
			}
		}
	</script>
	<style type="text/css">
		.table thead tr { 
			/* display:block; */ 
			} 
			.table tbody { 
			/* display: block;  */
			height: 50px; 
			overflow: auto; 
			} 
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 审批登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.approve.registe" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="allId"/>
		<form:hidden path="version"/>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span12">
				<div class="control-group item">
					<%-- 申请类型 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.application.type" />：</label>
					<label>
						<form:select path="businessType" id="businessType" class="input-small" style="width:150px;" onChange="businessTypeChange()">
							<form:options items="${fns:getFilterDictList('all_businessType', true, '50,51,52')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</label>
				</div>
				<div class="control-group item">
					<%-- 申请机构 --%>
					<label class="control-label" style="width:100px;"><spring:message code="allocation.application.office" />：</label>
					<label id="selectOfficeDiv">
				   		<sys:treeselect id="rofficeId" name="roffice.id"
							value="${pbocAllAllocateInfo.roffice.id}" labelName="roffice.name"
							labelValue="${pbocAllAllocateInfo.roffice.name}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-small" 
							notAllowSelectParent="true" notAllowSelectRoot="true"
							type="3" isAll="false" />
					</label>
					<label id="inputOfficeDiv">
						<form:input path="rofficeName" maxlength="40" class="required" style="width:149px;"/>
					</label>
				</div>
				
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" style="width:100px;"><spring:message code="allocation.order.date" />：</label>
					<label>
				   		<input id="applyDate"  name="applyDate" type="text" readOnly="readOnly" maxlength="20" class="Wdate required" style="width:149px;"
					   	value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>" 
					   	onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '%y-%M-%d'});"/>
					</label>
				</div>
				<div class="clear"></div>
				<div class="control-group item" id="agentOfficeDiv">
					<%-- 清分机构 --%>
					<label class="control-label" style="width:80px;"><spring:message code="common.agent.office" />：</label>
					<label>
						<form:hidden path="agentOffice.id"/>
						<%-- <form:input path="agentOffice.name" readOnly="readOnly" style="width:149px;"/> --%>
						
				   		<sys:treeselect id="agentOfficeId" name="agentOffice.id"
							value="${pbocAllAllocateInfo.agentOffice.id}" labelName="agentOffice.name"
							labelValue="${pbocAllAllocateInfo.agentOffice.name}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-small" 
							allowClear="true" notAllowSelectParent="true"
							type="6" isAll="false" />
							
					</label>
				</div>
			</div>
		</div>
		
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span12">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				<div class="clear"></div>
				<sys:pbocgoodselect currencyReserve="${fns:getConfig('sto.relevance.currency.cny')}"
			     classificationReserve="${fns:getConfig('sto.goods.classification.currencyNote')},${fns:getConfig('sto.goods.classification.damaged.recounting.no')},${fns:getConfig('sto.goods.classification.damaged.recounting.yes')},${fns:getConfig('sto.goods.classification.natureNote')}"/>
			    <div class="clear"></div>
				<div class="control-group item">
					<label class="control-label" style="width: 80px;"><spring:message
							code="common.number" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:input id="moneyNumber"
							path="stoGoodSelect.moneyNumber" htmlEscape="false"
							maxlength="2" style="width:117px;text-align:right;" class="digits required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="clear"></div>
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
	</form:form>
	<div class="row" style="margin-left:10px">
		
		<div class="span1" style="text-align: left">
			<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
		</div>
		<div class="span10" style="text-align: right">
			<%-- 申请总金额 --%>
			<label ><spring:message code="allocation.application.totalAmount" />：</label>
				<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${fns:getCache(userCacheId, null).registerAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
			<%-- 申请总金额(格式化) --%>
			<label style="margin-left:10px">${fns:getCache(userCacheId, null).registerAmountBig}</label>
    	</div>
	</div>
	<div class="row" style="margin-left:10px">
		<div class="span12">
			<div style="overflow-y: auto; height: 315px;">
			<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="allocation.application.detail" /></h4>
					<table id="contentTable" class="table table-hover" >
					<thead>
						<%-- <tr>
							申请明细
							<th style="text-align: center" colspan="5"><spring:message code="allocation.application.detail" /></th>
						</tr> --%>
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
						<c:forEach items="${fns:getCache(userCacheId, null).pbocAllAllocateItemList}" var="item" varStatus="status">
							<tr id="${item.goodsId}">
								<td style="text-align:right;">${status.index + 1}</td>
								<td style="text-align:right;"><a href="#" onclick="updateItem('${item.goodsId}','${item.goodsName}', '${item.moneyNumber}');javascript:return false;">${item.goodsName}</a></td>
								<td style="text-align:right;">${item.moneyNumber}</td>
								<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
								<td style="text-align:center;">
									<a href="${ctx}/allocation/v02/pbocApprovalRegiste/deleteGoods?goodsId=${item.goodsId}&userCacheId=${userCacheId }"
									   onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
									  <%--  <spring:message code="common.delete" /> --%><i class="fa fa-trash-o  text-red fa-lg"></i>
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
		<div class="span12">
			<div class="form-actions">
				<%-- 审批 --%>
				<input type="button" id="btnApproval" value="<spring:message code="common.approve" />" class="btn btn-primary" tabIndex="">&nbsp;
			</div>
		</div>
		<input type="hidden" id="cacheId" value="${userCacheId }">
	</div>
</body>
</html>