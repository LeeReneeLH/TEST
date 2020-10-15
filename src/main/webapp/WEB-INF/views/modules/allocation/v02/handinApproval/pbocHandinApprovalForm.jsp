<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 上缴审批登记 --%>
	<title><spring:message code="allocation.handin.approve.registe" /></title>
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
            	var url = "${ctx}/allocation/v02/pbocHandinApproval/add?pageType=storeHandinApprovalEdit&userCacheId=" + $("#cacheId").val();
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			
			// 审批
			$("#btnApproval").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
                var url = "${ctx}/allocation/v02/pbocHandinApproval/aloneOption?targetStatus=40&pageType=storeHandinApprovalList&strUpdateDate=" 
                		+ $("#strUpdateDate").val() + "&userCacheId=" + $("#cacheId").val();
	            $("#inputForm").attr("action", url);
				$("#inputForm").submit();
			});
			// 驳回
			$("#btnReject").click(function(){
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
				var url = "${ctx}/allocation/v02/pbocHandinApproval/aloneOption?targetStatus=21&pageType=storeHandinApprovalList&strUpdateDate=" 
						+ $("#strUpdateDate").val() + "&userCacheId=" + $("#cacheId").val();
				
				$("#inputForm").attr("action", url);
				$("#inputForm").submit();
			});
		});
		
		function updateItem(goodsId, goodsName, number) {	
			//清除tr背景色
			clearTrBackgroundColor();
			// 如果原updateItem方法中有红框内的清除tr背景色方法，拷贝到此处
			var url = "${ctx}/allocation/v02/pbocHandinApproval/updateGoodsItem?userCacheId=" + $("#cacheId").val();
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
		<%-- 上缴审批列表 --%>
		<li><a href="${ctx}/allocation/v02/pbocHandinApproval/list?pageType=storeHandinApprovalList&bInitFlag=true"><spring:message code="allocation.handin.approve.list" /></a></li>
		<%-- 上缴审批登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.handin.approve.registe" /></a></li>
	</ul><br/>
	<div class="row" style="margin-left:10px">
		<div class="span8">
			<div class="row" >
				<div class="span8">
				<h4 style="border-bottom:1px solid #eee;color:#dc776a;text-align:center;margin-bottom:20px;"><spring:message code="allocation.application.detail" /></h4>
				
					
				</div>
			</div>
			<div class="row" >
				<%-- 申请明细 --%>
				<div class="span8">
					<form:form id="tempform" class="form-horizontal">
						<div class="control-group item">
							<%-- 流水单号 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.allId" />：</label>
							<label>
								<input id="allId"  name="allId" type="text" readOnly="readOnly"  style="width:149px;"
							   	value="${fns:getCache(userCacheId, null).allId}"/>
							</label>
						</div>
						<div class="control-group item">
							<%-- 申请类型 --%>
							<label class="control-label" style="width:100px;"><spring:message code="allocation.application.type" />：</label>
							<label>
								<input id="businessType"  name="businessType" type="text" readOnly="readOnly"  style="width:149px;"
							   	value="${fns:getDictLabel(fns:getCache(userCacheId, null).businessType, 'all_businessType', '')}"/>
							</label>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 申请机构 --%>
							<label class="control-label" style="width:80px;"><spring:message code="allocation.application.office" />：</label>
							<label>
						   		<input id="rofficeName"  name="rofficeName" type="text" readOnly="readOnly"  style="width:149px;"
							   	value="${fns:getCache(userCacheId, null).rofficeName}"/>
							</label>
						</div>
						<div class="control-group item">
							<%-- 预约日期 --%>
							<label class="control-label" style="width:100px;"><spring:message code="allocation.order.date" />：</label>
							<label>
						   		<input id="applyDate"  name="applyDate" type="text" readOnly="readOnly"  style="width:149px;"
							   	value="<fmt:formatDate value="${fns:getCache(userCacheId, null).applyDate}" pattern="yyyy-MM-dd"/>"  />
							</label>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 申请机构 --%>
							<label class="control-label" style="width:80px;"><spring:message code="common.agent.office" />：</label>
							<label>
						   		<input id="agentOfficeName"  name="agentOfficeName" type="text" readOnly="readOnly"  style="width:149px;"
							   	value="${fns:getCache(userCacheId, null).agentOfficeName}"/>
							</label>
						</div>
					</form:form>
				</div>
			</div>
			<div class="row">
				<%-- 申请明细 --%>
				<div class="span8">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				</div>
			</div>
			<div class="row">
		    	<div class="span8" style="text-align: left;border-bottom:1px solid #ddd">
				<%-- 申请总金额 --%>
					<label ><spring:message code="allocation.application.totalAmount" />：</label>
						<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${fns:getCache(userCacheId, null).registerAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
					<%-- 申请总金额(格式化) --%>
					<label style="margin-left:10px">${fns:getCache(userCacheId, null).registerAmountBig}</label>
				</div>
			</div>
			<div class="row">
				<div class="span8">
					<div style="overflow-y: auto; height: 315px;">
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
									
								</tr>
							</thead>
							<tbody>
								<% int iRegistIndex = 0; %>
								<c:forEach items="${fns:getCache(userCacheId, null).pbocAllAllocateItemList}" var="item" varStatus="status">
									<%-- 申请登记物品 --%>
									<c:if test="${item.registType == '11'}">
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
		<div class="span9" >
			<div class="row" >
				<div class="span9">
				<h4 style="border-bottom:1px solid #eee;color:#34b15b;text-align:center;margin-bottom:20px;"><spring:message code="allocation.approve.detail" /></h4>
				
				</div>
			</div>
			<div class="row">
				<div class="span9">
					<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
						<form:hidden path="allId"/>
						<form:hidden path="version"/>
						<sys:message content="${message}"/>	
							<%-- 申请明细 --%>
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
										maxlength="2" style="width:117px;text-align:right;" class="digits required" />
									<span class="help-inline"><font color="red">*</font> </span>
								</div>
							</div>
					</form:form>
				</div>
			</div>
			<div class="row">
				<%-- 审批明细 --%>
				<div class="span10" style="margin-top:4px">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				</div>
			</div>
			<div class="row">
				
				<div class="span10" style="text-align: left;border-bottom:1px solid #ddd">
					<%-- 审批总金额 --%>
					<label ><spring:message code="allocation.approve.totalAmount" />：</label>
						<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${fns:getCache(userCacheId, null).confirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
					<%-- 审批总金额(格式化) --%>
					<label style="margin-left:10px">${fns:getCache(userCacheId, null).confirmAmountBig}</label>
		    	<div class="span1 pull-right" style="    margin-top: 0px !important;">
					<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
				</div>
		    	</div>
		    	
			</div>
			<div class="row">
				<div class="span10">
					 <div style="overflow-y: auto; height: 315px;">
					 	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${fns:getCache(userCacheId, null).strUpdateDate }">
						<table id="contentTable" class="table table-hover">
							<thead>
								<%-- <tr>
									审批明细
									<th style="text-align: center" colspan="5"><spring:message code="allocation.approve.detail" /></th>
								</tr> --%>
								<tr>
									<%-- 序号 --%>
									<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
									<%-- 物品名称 --%>
									<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
									<%-- 审批数量 --%>
									<th style="text-align: center" ><spring:message code="allocation.approve.number" /></th>
									<%-- 审批金额(元) --%>
									<th style="text-align: center" ><spring:message code="allocation.approve.amount" /><spring:message code="common.units.yuan.alone" /></th>
									<%-- 操作（删除/修改） --%>
									<th style="text-align: center" ><spring:message code='common.operation'/></th>
								</tr>
							</thead>
							<tbody>
								<% int iConfirmIndex = 0; %>
								<c:forEach items="${fns:getCache(userCacheId, null).pbocAllAllocateItemList}" var="item" varStatus="status">
									<%-- 审批登记物品 --%>
									<c:if test="${item.registType == '10'}">
										<% iConfirmIndex = iConfirmIndex + 1; %>
										<tr id="${item.goodsId}">
											<td style="text-align:right;"><%=iConfirmIndex %></td>
											<td style="text-align:right;"><a href="#" onclick="updateItem('${item.goodsId}','${item.goodsName}', '${item.moneyNumber}');javascript:return false;">${item.goodsName}</a></td>
											<td style="text-align:right;">${item.moneyNumber}</td>
											<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											<td style="text-align:center;">
												<a href="${ctx}/allocation/v02/pbocHandinApproval/deleteGoods?goodsId=${item.goodsId}&pageType=storeHandinApprovalEdit&userCacheId=${userCacheId }"
												   onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
												  <%--  <spring:message code="common.delete" /> --%><i class="fa fa-trash-o  text-red fa-lg"></i>
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
				<div class="span10">
					<div class="form-actions" style="text-align: right;width:740px">
						
						<shiro:hasPermission name="allocation.v02:pbocHandinApproval:approval">
							<%-- 审批 --%>
							<input type="button" id="btnApproval" value="<spring:message code='common.approve' />" class="btn btn-primary">&nbsp;
							<%-- 驳回 --%>
							<input type="button" id="btnReject" value="<spring:message code='common.reject' />" class="btn btn-danger">&nbsp;
						</shiro:hasPermission>
						<%-- 返回 --%>
						<input id="btnCancel" class="btn" type="button"
							value="<spring:message code='common.return'/>"
							onclick="window.location.href='${ctx}/allocation/v02/pbocHandinApproval/back?pageType=storeHandinApprovalList'"/>
						<input id="cacheId" type="hidden" value="${userCacheId }"/>
					</div>
				</div>
			</div>
		</div>
	</div>	
</body>
</html>