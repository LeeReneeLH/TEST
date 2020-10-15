<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 发行基金调拨入库登记 --%>
	<title><spring:message code="allocation.issueFund.sametrade.inStore.register" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/script/modules/allocation/v02/common/pbocUpdateGoodsItem.js"></script>
	
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
            	var url = "${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/add?userCacheId=" + $("#cacheId").val();
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			
         	// 审批
			$("#btnApproval").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
                var url = "${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/savePbocOrder?userCacheId=" + $("#cacheId").val();
	              $("#inputForm").attr("action", url);
				  $("#inputForm").submit();
			});
         	
			// 查询调拨命令号
			$("#searchByCommandId").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
				$("#rofficeIdId").removeClass();
				$("#rofficeIdName").removeClass();
				$("#applyDate").removeClass();
				$("#aofficeIdId").removeClass();
				$("#aofficeIdName").removeClass();
				
                var url = "${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/searchByCommandId?userCacheId=" + $("#cacheId").val();
	              $("#inputForm").attr("action", url);
				  $("#inputForm").submit();
			});
		});
		
		function updateItem(goodsId, goodsName, number) {	
			
			//清除tr背景色
			clearTrBackgroundColor();
			var url = "${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/updateGoodsItem?userCacheId=" + $("#cacheId").val();
			updateGoodsItem(goodsId, goodsName, number, url);
		}	
		
		function deleteItem(goodsId){
			var url = "${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/deleteGoods?goodsId="+ goodsId +"&userCacheId=" + $("#cacheId").val();
			confirmx('<spring:message code="message.I0001"/>', function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
				$("#inputForm").attr("action", url);
				$("#inputForm").submit();
			});
		}

		//清除tr背景色
		function clearTrBackgroundColor(){
			$("tr").removeClass();
		}
	</script>
	<style type="text/css">
		.table thead tr { 
		/* 	display:block;  */
			} 
			.table tbody { 
		/* 	display: block;  */
			height: 50px; 
			overflow: auto; 
			} 
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%--发行基金调拨入库列表(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/list"><spring:message code="allocation.issueFund.sametrade.inStore.list" /></a></li>
		<%-- 发行基金调拨入库登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.issueFund.sametrade.inStore.register" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="allId"/>
		<form:hidden path="version"/>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 调拨命令号 --%>
			<div class="span12">
				<div class="control-group item">
					<%-- 调拨命令号 --%>
					<label class="control-label" style="width:100px;"><spring:message code="allocation.sametrade.commondnum" />：</label>
					<label>
						<form:input path="commondNumber" maxlength="20" style="width:170px;" class ="digits required"/>
					</label>
				</div>
				<div class="control-group item">
					<div class="controls" style="margin-left: 48px;">
						<%-- 查询调拨出库 --%>
						<input id="searchByCommandId" class="btn btn-primary" type="button" value="查询调拨出库" />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span12">
				<div class="control-group item">
					<%-- 申请类型 --%>
					<label class="control-label" style="width:100px;"><spring:message code="allocation.application.type" />：</label>
					<label>
						<input style="width:170px;" readOnly="readOnly" type="text"
							value="${fns:getDictLabel(pbocAllAllocateInfo.businessType, 'all_businessType', '53')}"/>
						<form:hidden path="businessType" id="businessType" value="${pbocAllAllocateInfo.businessType}"/>
					</label>
				</div>
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" style="width:165px;"><spring:message code="allocation.order.date" />：</label>
					<label>
				   		<input id="applyDate"  name="applyDate" type="text" readOnly="readOnly" maxlength="20" class="Wdate required" style="width:149px;"
					   	value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>" 
					   	onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '%y-%M-%d'});"/>
					</label>
				</div>
			</div>
			<div class="span12">
				<div class="control-group item">
					<%-- 出库机构 --%>
					<label class="control-label" style="width:100px;"><spring:message code="allocation.outstore.office" />：</label>
					<label>
						<sys:treeselect id="rofficeId" name="roffice.id"
							value="${pbocAllAllocateInfo.roffice.id}" labelName="roffice.name"
							labelValue="${pbocAllAllocateInfo.roffice.name}" title="<spring:message code='allocation.outstore.office' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false"
							type="1" isAll="true" />
					</label>
				</div>
				<c:if test="${fns:getUser().office.type == '7' }">
					<div class="control-group item">
						<%-- 入库机构 --%>
						<label class="control-label" style="width:90px;">入库机构：</label>
						<label>
							<sys:treeselect id="aofficeId" name="aoffice.id"
								value="${pbocAllAllocateInfo.aoffice.id}" labelName="aoffice.name"
								labelValue="${pbocAllAllocateInfo.aoffice.name}" title="<spring:message code='allocation.outstore.office' />"
								url="/sys/office/treeData" cssClass="required input-medium"
								notAllowSelectParent="false" notAllowSelectRoot="false"
								type="1" isAll="true" />
						</label>
					</div>
				</c:if>
			</div>
		</div>
		
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span10">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				<div class="clear"></div>
				<sys:pbocgoodselect currencyReserve="${fns:getConfig('sto.relevance.currency.cny')}"
			     classificationReserve="${fns:getConfig('sto.goods.classification.currencyNote')},${fns:getConfig('sto.goods.classification.damaged.recounting.no')},${fns:getConfig('sto.goods.classification.damaged.recounting.yes')}"/>
			    <div class="clear"></div>
				<div class="control-group item">
					<label class="control-label" style="width: 80px;"><spring:message
							code="common.number" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:input id="moneyNumber"
							path="stoGoodSelect.moneyNumber" htmlEscape="false"
							maxlength="4" style="width:117px;text-align:right;" class="digits required" />
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
		<div class="span8" style="text-align: right">
			<%-- 申请总金额 --%>
			<label ><spring:message code="allocation.application.totalAmount" />：</label>
				<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${fns:getCache(userCacheId, null).registerAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
			<%-- 申请总金额(格式化) --%>
			<label style="margin-left:10px">${fns:getCache(userCacheId, null).registerAmountBig}</label>
    	</div>
	</div>
	<div class="row" style="margin-left:10px">
		<div class="span10">
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
									<a href="#"
										onclick="deleteItem('${item.goodsId}')" title="<spring:message code="common.delete"/>">
									   <%-- <spring:message code="common.delete" /> --%>
									   <i class="fa fa-trash-o  text-red fa-lg"></i>
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<c:if test="${fns:getCache(userCacheId, null).pbocAllAllocateDetailList != null && fns:getCache(userCacheId, null).pbocAllAllocateDetailList.size() != 0}">
			
			<%-- 箱袋调拨明细 --%>
			<div class="row">
				<div class="span10">
					<div style="overflow-y: auto; height: 315px;">
						<table  id="contentTable" class="table table-hover">
							<thead>
								<tr >
									<th style="text-align:center;" colspan="5"><spring:message code="allocation.box.detail" /></th>
								</tr>
								<tr>
									<%-- 序号 --%>
									<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
									<%-- 物品名称 --%>
									<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
									<%-- 金额(元) --%>
									<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
									<%-- 箱袋编号 --%>
									<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
									<%-- 原封券翻译 --%>
									<th style="text-align:center;"><spring:message code="store.original.translation" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${fns:getCache(userCacheId, null).pbocAllAllocateDetailList}" var="pbocAllAllocateDetail" varStatus="status">
									<tr>
										<%-- 序号 --%>
										<td style="text-align:right;">${status.index + 1}</td>
										<%-- 物品名称 --%>
										<td style="text-align:left;">${sto:getGoodsName(pbocAllAllocateDetail.goodsLocationInfo.goodsId)}</td>
										<%-- 金额(元) --%>
										<td style="text-align:right;"><fmt:formatNumber value="${pbocAllAllocateDetail.goodsLocationInfo.amount}" pattern="#,##0.00#" /></td>
										<%-- 箱袋编号 --%>
										<td style="text-align:center;">${pbocAllAllocateDetail.rfid}</td>
										<%-- 原封券翻译 --%>
										<td style="text-align:center;">${pbocAllAllocateDetail.stoOriginalBanknote.originalTranslate}</td>
										
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>
	</div>	
	<div class="row" style="margin-left:10px">
		<div class="span12">
			<div class="form-actions" style="width:740px;">
				<%-- 审批 --%>
				<input type="button" id="btnApproval" value="<spring:message code="common.commit" />" class="btn btn-primary" tabIndex="">&nbsp;
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/back'"/>
			</div>
		</div>
		<input type="hidden" id="cacheId" value="${userCacheId }">
	</div>
</body>
</html>