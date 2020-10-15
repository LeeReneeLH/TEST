<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 明细查询 --%>
	<title><spring:message code="allocation.show.detail" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		function getStorePerson(allId,personList) {
		
			if(personList != '[]' && personList != ''){
				//交接人员字符串变数组
				var arr = personList.split(",");
				//替换第一个元素
				arr.splice(0,1,arr[0].substring(1));
				//替换最后一个元素
				arr.splice(arr.length-1,arr.length,arr[arr.length-1].substring(0,arr[arr.length-1].length-1));
				showCenterWorkFlowDetail(allId,arr);
			}
		}
		
		function showCenterWorkFlowDetail(allId,arr){
			var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/getPersonInfo?allId="+allId+"&personList="+arr;
			top.$.jBox.open(
		  	  	content,
		  		"人员信息", 800, 600,{
				buttons : {
					"<spring:message code='common.close' />" : true
				},
				loaded : function(h) {
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});
		}
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
		<c:if test="${pbocAllAllocateInfo.pageType == 'bussnessApplicationView'}">
			<c:choose>
				<c:when test="${pbocAllAllocateInfo.businessType == '51' }">
					<%-- 申请下拨列表(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocAllocatedOrder/list?pageType=bussnessApplicationList&bInitFlag=true"><spring:message code="allocation.application.allocated.list" /></a></li>
					<%-- 申请下拨登记(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?pageType=bussnessApplicationEdit"><spring:message code="allocation.application.allocated.registe" /></a></li>
				</c:when>
				<c:when test="${pbocAllAllocateInfo.businessType == '50' }">
					<%-- 申请上缴列表(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocHandinOrder/list?pageType=bussnessApplicationList&bInitFlag=true"><spring:message code="allocation.application.handin.list" /></a></li>
					<%-- 申请上缴登记(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocHandinOrder/form?pageType=bussnessApplicationEdit"><spring:message code="allocation.application.handin.registe" /></a></li>
				</c:when>
				<c:when test="${pbocAllAllocateInfo.businessType == '52' }">
					<%-- 申请代理列表(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocAgentHandinOrder/list?pageType=bussnessApplicationList&bInitFlag=true"><spring:message code="allocation.application.agent.handin.list" /></a></li>
					<%-- 申请代理登记(link) --%>
					<li><a href="${ctx}/allocation/v02/pbocAgentHandinOrder/form?pageType=bussnessApplicationEdit"><spring:message code="allocation.application.agent.handin.registe" /></a></li>
				</c:when>
			</c:choose>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView'}">
			<%-- 下拨审批列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocAllocatedOrder/list?pageType=storeApprovalList&bInitFlag=true"><spring:message code="allocation.allocated.approve.list" /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'storeHandinApprovalView'}">
			<%-- 上缴审批列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHandinApproval/list?pageType=storeHandinApprovalList&bInitFlag=true"><spring:message code="allocation.handin.approve.list" /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'storeQuotaView'}">
			<%-- 出库列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocAllocatedQuota/list?pageType=storeQuotaList&bInitFlag=true"><spring:message code="allocation.outStore.list" /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'storeHandinView'}">
			<%-- 入库列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHandinInStore"><spring:message code='allocation.inStore.list' /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'destroyApprovalView'}">
			<%--发行基金销毁出库列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocDestroyOutStore/list?pageType=destroyApprovalList&bInitFlag=true"><spring:message code="allocation.issueFund.destory.outStore.list" /></a></li>
			<%-- 发行基金销毁出库登记(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocDestroyOutStore/form"><spring:message code="allocation.issueFund.destory.outStore.register" /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'}">
			<%--发行基金调拨出库列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/list?bInitFlag=true"><spring:message code="allocation.issueFund.sametrade.outStore.list"/></a></li>
			<%-- 发行基金调拨出库登记(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/form"><spring:message code="allocation.issueFund.sametrade.outStore.register" /></a></li>
		</c:if>
		<c:if test="${pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
			<%--发行基金调拨入库列表(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/list"><spring:message code="allocation.issueFund.sametrade.inStore.list"/></a></li>
			<%-- 发行基金调拨入库登记(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/form"><spring:message code="allocation.issueFund.sametrade.inStore.register" /></a></li>
		</c:if>
		<%-- 明细查询(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.show.detail" /></a></li>
		
	</ul>
	<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="allId"/>
		<sys:message content="${message}"/>	
		<div class="row">
			
			<div class="span12">
				<div class="clear"></div>
				<%-- 流水单号 --%>
				<div class="control-group item">
					<label class="control-label" style="width:110px;"><spring:message code="allocation.allId" />：</label>
					<label>
						<input type="text" id="allId" name="allId" 
							value="${pbocAllAllocateInfo.allId}" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</label>
				</div>
				<%-- 申请机构 --%>
				<div class="control-group item">
					<label class="control-label" style="width:100px;">
					<c:choose>
						<%-- 53:调拨入库 --%>
						<c:when test="${pbocAllAllocateInfo.businessType == '53' }">
							<spring:message code="allocation.outstore.office" />：
						</c:when>
						<%-- 54:调拨出库 --%>
						<c:when test="${pbocAllAllocateInfo.businessType == '54'}">
							<spring:message code="allocation.instore.office" />：
						</c:when>
						<%-- 55:销毁出库 --%>
						<c:when test="${pbocAllAllocateInfo.businessType == '55' }">
							<spring:message code="allocation.destory.office" />：
						</c:when>
						<c:otherwise>
							<spring:message code="allocation.application.office" />：
						</c:otherwise>
					</c:choose></label>
					<label>
						<%-- 51：申请下拨,50：申请上缴,52：代理上缴,53:调拨入库 --%>
						<c:if test="${pbocAllAllocateInfo.businessType == '51'
									or pbocAllAllocateInfo.businessType == '50'
									or pbocAllAllocateInfo.businessType == '52'
									or pbocAllAllocateInfo.businessType == '53' }">
							<input type="text" id="rofficeName" name="rofficeName" 
							value="${pbocAllAllocateInfo.rofficeName}" style="width:149px;text-align:right;" readOnly="readOnly"/>
						</c:if>
						<%-- 54:调拨出库,55:销毁出库 --%>
						<c:if test="${pbocAllAllocateInfo.businessType == '54'
									or pbocAllAllocateInfo.businessType == '55' }">	
							<input type="text" id="aofficeName" name="aofficeName" 
							value="${pbocAllAllocateInfo.aofficeName}" style="width:149px;text-align:right;" readOnly="readOnly"/>
						</c:if>
					</label>
					
				</div>
				<c:if test="${pbocAllAllocateInfo.businessType == '52' }">
					<%-- 代理机构 --%>
					<div class="control-group item">
						<label class="control-label" style="width:100px;"><spring:message code="common.agent.office" />：</label>
						<label >
							<input type="text" id="agentOfficeName" name="agentOfficeName" 
								value="${pbocAllAllocateInfo.agentOfficeName}" style="width:149px;text-align:right;" readOnly="readOnly"/>
						</label>
					</div>
				</c:if>
				<div class="clear"></div>
			</div>
		</div>
		<div class="row">
			<div class="span12">
				<%-- 申请类型 --%>
				<%-- 
				<div class="control-group item">
					<label class="control-label" style="width:70px;"><spring:message code="allocation.application.type" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="businessType" name="businessType" 
						value="${fns:getDictLabel(pbocAllAllocateInfo.businessType, 'all_businessType', '')}"
						class="input-medium" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				--%>
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label"  style="width:110px;"><spring:message code="allocation.order.date" />：</label>
					<label>
						<input type="text" id="applyDate" name="applyDate" 
						value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>"
						class="input-medium" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</label>
				</div>
				<div class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:100px;"><spring:message code="common.status" />：</label>
					<label >
						<c:choose>
							<%-- 业务类型为申请下拨，销毁出库，调拨出库 --%>
							<c:when test="${pbocAllAllocateInfo.businessType == '51' 
											or pbocAllAllocateInfo.businessType == '54' 
											or pbocAllAllocateInfo.businessType == '55'}">
								<input type="text" id="status" name="status" 
									value="${fns:getDictLabel(pbocAllAllocateInfo.status,'pboc_order_quota_status','')}"  
									 style="width:149px;text-align:right;"  readOnly="readOnly"/>
							</c:when>
							<c:otherwise>
								<input type="text" id="status" name="status" 
									value="${fns:getDictLabel(pbocAllAllocateInfo.status,'pboc_order_handin_status','')}"  
									style="width:149px;text-align:right;"  readOnly="readOnly"/>
							</c:otherwise>
						</c:choose>
					</label>
				</div>
				<%-- 业务类型为销毁出库，调拨出库,调拨入库 --%>
				<c:if test="${pbocAllAllocateInfo.businessType == '53' 
								or pbocAllAllocateInfo.businessType == '54' 
								or pbocAllAllocateInfo.businessType == '55'}">
					<div class="control-group item">
							<label class="control-label" style="width:120px;">
							<c:choose>
								<%-- 53:调拨入库 --%>
								<c:when test="${pbocAllAllocateInfo.businessType == '53' }">
									<spring:message code="allocation.sametrade.instore.commondnum" />：
								</c:when>
								<%-- 54:调拨出库 --%>
								<c:when test="${pbocAllAllocateInfo.businessType == '54'}">
									<spring:message code="allocation.sametrade.outstore.commondnum" />：
								</c:when>
								<%-- 55:销毁出库 --%>
								<c:when test="${pbocAllAllocateInfo.businessType == '55' }">
									<spring:message code="allocation.sametrade.destory.commondnum" />：
								</c:when>
								<c:otherwise>
									<spring:message code="allocation.application.office" />：
								</c:otherwise>
							</c:choose></label>
							<div class="controls" style="margin-left:120px; width:170px;">
								<input type="text" id="commondNumber" name="commondNumber" 
								value="${pbocAllAllocateInfo.commondNumber}" style="width:170px;text-align:right;" readOnly="readOnly"/>
							</div>
					</div>
				</c:if>
			</div>
		</div>
		<c:if test="${fn:length(fn:trim(pbocAllAllocateInfo.remarks)) > 0 }">
			<div class="row">
				<div class="span12">
					<div class="control-group item">
						<%-- 备注 --%>
						<label class="control-label" style="width:70px;"><spring:message code="common.remark" />：</label>
						<div class="controls" style="margin-left:80px; width:170px;">
							<textarea style="width:700px;height:55px;color:red" class="input-xxlarge" readonly="readonly">${pbocAllAllocateInfo.remarks}</textarea>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<div class="row">
				<%-- 审批明细 --%>
				<div class="span10" style="margin-top:4px">
					<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
				</div>
			</div>
		<div class="row">
			<div class="span8">
				<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div class="clear"></div>
			    		<div class="control-group item">
							<c:choose>
								<c:when test="${pbocAllAllocateInfo.businessType == '52' }">
									<%-- 代理交接人 --%>
									<label class="control-label" style="width:110px;"><spring:message code="allocation.agentOfcommerceBank.handover" />：</label>
								</c:when>
								<c:when test="${pbocAllAllocateInfo.businessType == '54' 
												or pbocAllAllocateInfo.businessType == '55'
												or pbocAllAllocateInfo.businessType == '53' }">
									<%-- 调拨出库， 销毁出库，调拨入库--%>
									<%-- 行外交接人 --%>
									<label class="control-label" style="width:110px;"><spring:message code="allocation.pboc.outside.handover" />：</label>
								</c:when>
								<c:otherwise>
									<%-- 商行交接人 --%>
									<label class="control-label" style="width:110px;"><spring:message code="allocation.bankOfcommerce.handover" />：</label>
								</c:otherwise>
							</c:choose>
							<label>
							<%--delete-start 照片显示  by yanbingxu 2018/04/03--%>
								<%-- <c:choose>
									申请下拨，调拨出库， 销毁出库
									<c:when test="${pbocAllAllocateInfo.businessType == '51' 
													or pbocAllAllocateInfo.businessType == '54' 
													or pbocAllAllocateInfo.businessType == '55'}">
										<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
											<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
												<c:if test="${handoverUserDetail.type =='2' }">
													${handoverUserDetail.escortName }&nbsp;&nbsp;
												</c:if>
											</c:forEach>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
											<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
												<c:if test="${handoverUserDetail.type =='1' }">
													${handoverUserDetail.escortName }&nbsp;&nbsp;
												</c:if>
											</c:forEach>
										</c:if>
									</c:otherwise>
								</c:choose> --%>
							<%--delete-end 照片显示  by yanbingxu 2018/04/03--%>
							<%--add-start 照片显示  by yanbingxu 2018/04/03--%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${cofferHandoverIdList}');" value="${cofferHandover}"/>
							<%--add-end 照片显示  by yanbingxu 2018/04/03--%>
							</label>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 申请总金额 --%>
							<label class="control-label" style="width:110px;"><spring:message code="allocation.application.totalAmount" />：</label>
							<label>
								<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.registerAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 申请总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.registerAmountBig}</label>
							</label>
						</div>
					</div>
				</div>
				<div class="row">
			    	<div class="span8" style="text-align: right">
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
										
									</tr>
								</thead>
								<tbody>
									<% int iRegistIndex = 0; %>
									<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateItemList}" var="item" varStatus="status">
										<%-- 申请登记物品 --%>
										<c:if test="${item.registType == '11'}">
											<% iRegistIndex = iRegistIndex + 1; %>
											<tr >
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
			<div class="span8">
				<div class="row">
					<div class="span8" style="text-align: right">
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 人行交接人 --%>
							<label class="control-label" style="width:110px;"><spring:message code="allocation.pboc.handover" />：</label>
							<label >
							<%--delete-start 照片显示  by yanbingxu 2018/04/03--%>
								<%-- <c:choose>
									申请下拨，调拨出库， 销毁出库
									<c:when test="${pbocAllAllocateInfo.businessType == '51' 
													or pbocAllAllocateInfo.businessType == '54' 
													or pbocAllAllocateInfo.businessType == '55'}">
										<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
											<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
												<c:if test="${handoverUserDetail.type =='1' }">
													${handoverUserDetail.escortName }&nbsp;&nbsp;
												</c:if>
											</c:forEach>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
											<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
												<c:if test="${handoverUserDetail.type =='2' }">
													${handoverUserDetail.escortName }&nbsp;&nbsp;
												</c:if>
											</c:forEach>
										</c:if>
									</c:otherwise>
								</c:choose> --%>
							<%--delete-end 照片显示  by yanbingxu 2018/04/03--%>
							<%--add-start 照片显示  by yanbingxu 2018/04/03--%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${pbocHandoverIdList}');" value="${pbocHandover}"/>
							<%--add-end 照片显示  by yanbingxu 2018/04/03--%>
							</label>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 审批总金额 --%>
							<label class="control-label" style="width:110px;"><spring:message code="allocation.approve.totalAmount" />：</label>
							<label>
								<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.confirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 审批总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.confirmAmountBig}</label>
							</label>
						</div>
			    	</div>
				</div>
				<div class="row">
					<div class="span8" style="text-align: right">
						<div style="overflow-y: auto; height: 315px;">
					<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="allocation.approve.detail" /></h4>
					<table id="contentTable" class="table table-hover" >
					<thead>
						<%-- <tr>
							申请明细
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
										
									</tr>
								</thead>
								<tbody>
									<% int iConfirmIndex = 0; %>
									<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateItemList}" var="item" varStatus="status">
										<%-- 审批登记物品 --%>
										<c:if test="${item.registType == '10'}">
											<% iConfirmIndex = iConfirmIndex + 1; %>
											<tr id="${item.goodsId}">
												<td style="text-align:right;"><%=iConfirmIndex %></td>
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
		</div>
		<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
		<div class="row" style="margin-top: 10px;">
			<c:choose>
				<c:when test="${(pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								or pbocAllAllocateInfo.pageType == 'storeQuotaView' 
								or pbocAllAllocateInfo.pageType == 'destroyApprovalView'
								or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView') 
								and printDataList != null and printDataList.size() > 0 }">
					<div class="span12">
						<div style="overflow-y: auto; height: 315px;">
							<table  id="contentTable" class="table table-hover">
								<thead>
									<tr>
										<%-- 取包明细 --%>
										<th style="text-align: center" colspan="7"><spring:message code="allocation.getBagDetail" /></th>
									</tr>
									<tr>
										<%-- 序号 --%>
										<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
										<%-- 物品名称 --%>
										<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
										<%-- 数量(包) --%>
										<th style="text-align: center"><spring:message code="common.number" /><spring:message code="common.units.bag" /></th>
										<%-- 库区位置 --%>
										<th style="text-align:center;"><spring:message code="store.areaPosition" /></th>
										<%-- 箱袋编号 --%>
										<th style="text-align: center"><spring:message code="common.boxNo" /></th>
										<%-- 日期 --%>
										<th style="text-align: center"><spring:message code="common.date" /></th>
										<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
										or pbocAllAllocateInfo.pageType == 'storeQuotaView' 
										or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
										or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
											<%-- 原封券翻译 --%>
											<th style="text-align:center;"><spring:message code="store.original.translation" /></th>
										</c:if>
									</tr>
								</thead>
								<tbody>
									<% int iGoodsAreaDetailIndex = 0; %>
									<c:forEach items="${printDataList}" var="allAllocateInfo">
										<%-- 取包明细 --%>
										<c:forEach items="${allAllocateInfo.pbocAllAllocateItemList}" var="allAllocateItem">
											<c:forEach items="${allAllocateItem.goodsAreaDetailList}" var="areaDetail" varStatus="status">
												<% iGoodsAreaDetailIndex = iGoodsAreaDetailIndex + 1; %>
												<tr>
													<td style="text-align: right"><%=iGoodsAreaDetailIndex %></td>
													<%-- 物品名称 --%>
													<td style="text-align: left">
														${sto:getGoodsName(areaDetail.goodsLocationInfo.goodsId)}
													</td>
													<%-- 数量(包) --%>
													<td style="text-align: right">
														${areaDetail.goodsLocationInfo.goodsNum}
													</td>
													<%-- 库区位置 --%>
													<c:choose>
														<c:when test="${areaDetail.isNecessaryOut =='1' }">
															<td style="text-align: center;">
																${fns:getDictLabel(areaDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${areaDetail.goodsLocationInfo.storeAreaName}
															</td>
															<td style="text-align: right;">${areaDetail.goodsLocationInfo.rfid}</td>
															<td style="text-align: center;"><fmt:formatDate value="${areaDetail.goodsLocationInfo.inStoreDate}" pattern="yyyy-MM-dd"/></td>
															<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
																		or pbocAllAllocateInfo.pageType == 'storeQuotaView' 
																		or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
																		or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
																<%-- 原封券翻译 --%>
																<td style="text-align:center;">${areaDetail.stoOriginalBanknote.originalTranslate}</td>
															</c:if>
															
														</c:when>
														<c:when test="${areaDetail.isNecessaryOut =='0' }">
															<td style="text-align: center;">
																<font color="red"> <B>${fns:getDictLabel(areaDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${areaDetail.goodsLocationInfo.storeAreaName}</B></font>
															</td>
															
															<td style="text-align: right;">
																<font color="red"> <B>${areaDetail.goodsLocationInfo.rfid}</B></font>
															</td>
															<td style="text-align: center;">
																<font color="red"> <B><fmt:formatDate value="${areaDetail.goodsLocationInfo.inStoreDate}" pattern="yyyy-MM-dd"/></B></font>
															</td>
															<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
																			or pbocAllAllocateInfo.pageType == 'storeQuotaView' 
																			or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
																			or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
																<%-- 原封券翻译 --%>
																<td style="text-align:center;">
																	<font color="red"> <B>${areaDetail.stoOriginalBanknote.originalTranslate}</B></font>
																</td>
															</c:if>
															
														</c:when>
													</c:choose>
												</tr>
											</c:forEach>
										</c:forEach>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</c:when>
			</c:choose>
		</div>
		</c:if>
		<div class="row" style="margin-top: 10px;">
			<%-- 授权人 --%>
			<div class="span8" style="text-align: right">
				
				<div class="clear"></div>
				<div class="control-group item">
					<%-- 授权人 --%>
					<label class="control-label" style="width:110px;"><spring:message code="common.authorizer" />：</label>
					<label>
					<%--delete-start 照片显示 by yanbingxu 2018/04/03 --%>
						<%-- <label>${pbocAllAllocateInfo.pbocAllHandoverInfo.managerUserName}</label> --%>
					<%--delete-end 照片显示 by yanbingxu 2018/04/03 --%>
					<%--add-start 照片显示 by yanbingxu 2018/04/03 --%>
						<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
							onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${authorizeIdList}');" value="${authorize}"/>
					<%--add-end 照片显示 by yanbingxu 2018/04/03 --%>
					</label>
				</div>
	    	</div>
		</div>
		<div class="row">
			<%-- 入库时显示入库总金额 --%>
			<c:if test="${pbocAllAllocateInfo.businessType == '50' 
							or pbocAllAllocateInfo.businessType == '52'
							or pbocAllAllocateInfo.businessType == '53'}">
				<div class="span8" style="text-align: right">
					
					<div class="clear"></div>
					<div class="control-group item">
						<%-- 入库总金额 --%>
						<label class="control-label" style="width:110px;"><spring:message code="common.inStore.totalAmount" />：</label>
						<label>
							<input type="text" id="instoreAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.instoreAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
							<%-- 入库总金额(格式化) --%>
							<label style="margin-left:10px">${pbocAllAllocateInfo.instoreAmountBig}</label>
						</label>
					</div>
		    	</div>
	    	</c:if>
	    	<%-- 出库时显示出库总金额 --%>
			<c:if test="${pbocAllAllocateInfo.businessType == '51' 
							or pbocAllAllocateInfo.businessType == '54'
							or pbocAllAllocateInfo.businessType == '55'}">
				<div class="span8" style="text-align: right">
					
					<div class="clear"></div>
					<div class="control-group item">
						<%-- 出库总金额 --%>
						<label class="control-label" style="width:110px;"><spring:message code="common.outStore.totalAmount" />：</label>
						<label>
							<input type="text" id="outstoreAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.outstoreAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
							<%-- 出库总金额(格式化) --%>
							<label style="margin-left:10px">${pbocAllAllocateInfo.outstoreAmountBig}</label>
						</label>
					</div>
		    	</div>
	    	</c:if>
		</div>
		<div class="row" >
			<%-- 箱袋调拨明细 --%>
			<div class="span12">
				<div style="overflow-y: auto; height: 315px;">
					<table  id="contentTable" class="table table-hover">
						<thead>
							<tr >
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeHandinApprovalView'
								            or pbocAllAllocateInfo.pageType == 'storeHandinView'
								            or pbocAllAllocateInfo.pageType == 'destroyApprovalView'}">
									<th style="text-align:center;" colspan="5"><spring:message code="allocation.box.detail" /></th>
								</c:if>
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeQuotaView'
								            or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
								            or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
									<th style="text-align:center;" colspan="6"><spring:message code="allocation.box.detail" /></th>
								</c:if>
								<c:if test="${pbocAllAllocateInfo.pageType == 'bussnessApplicationView'}">
									<th style="text-align:center;" colspan="4"><spring:message code="allocation.box.detail" /></th>
								</c:if>
							</tr>
							<tr>
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 物品名称 --%>
								<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
								<%-- 金额(元) --%>
								<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeHandinApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeQuotaView'
								            or pbocAllAllocateInfo.pageType == 'storeHandinView'
								            or pbocAllAllocateInfo.pageType == 'destroyApprovalView'
								            or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
								            or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
								    <%-- 库区位置 --%>
									<th style="text-align:center;"><spring:message code="store.areaPosition" /></th>
								</c:if>
								</c:if>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeQuotaView'
								            or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
								            or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
								    <%-- 原封券翻译 --%>
									<th style="text-align:center;"><spring:message code="store.original.translation" /></th>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateDetailList}" var="pbocAllAllocateDetail" varStatus="status">
								<tr>
									<%-- 序号 --%>
									<td style="text-align:right;">${status.index + 1}</td>
									<%-- 物品名称 --%>
									<td style="text-align:left;">${sto:getGoodsName(pbocAllAllocateDetail.goodsLocationInfo.goodsId)}</td>
									<%-- 金额(元) --%>
									<td style="text-align:right;"><fmt:formatNumber value="${pbocAllAllocateDetail.goodsLocationInfo.amount}" pattern="#,##0.00#" /></td>
									<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
									<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeHandinApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeQuotaView'
								            or pbocAllAllocateInfo.pageType == 'storeHandinView'
								            or pbocAllAllocateInfo.pageType == 'destroyApprovalView'
								            or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
								            or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
							            <%-- 库区位置 --%>
							            <td style="text-align:right;">${fns:getDictLabel(pbocAllAllocateDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${pbocAllAllocateDetail.goodsLocationInfo.storeAreaName}</td>
								    </c:if>
								    </c:if>
									<%-- 箱袋编号 --%>
									<td style="text-align:center;">${pbocAllAllocateDetail.rfid}</td>
									<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView' 
								            or pbocAllAllocateInfo.pageType == 'storeQuotaView'
								            or pbocAllAllocateInfo.pageType == 'allocatedOutStoreView'
								            or pbocAllAllocateInfo.pageType == 'allocatedInStoreView'}">
									    <%-- 原封券翻译 --%>
										<td style="text-align:center;">${pbocAllAllocateDetail.stoOriginalBanknote.originalTranslate}</td>
									</c:if>
									
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			
		</div>
		<div class="row">
			<div class="form-actions" style="margin-left:5px;">
				<%-- 返回 --%>
				<c:if test="${pbocAllAllocateInfo.pageType == 'bussnessApplicationView'}">
					<c:choose>
						<c:when test="${pbocAllAllocateInfo.businessType == '51' }">
							<input id="btnCancel" class="btn btn-primary" type="button"
								value="<spring:message code='common.return'/>"
								onclick="window.location.href='${ctx}/allocation/v02/pbocAllocatedOrder/back?allId=${pbocAllAllocateInfo.allId}&pageType=bussnessApplicationList'" />
						</c:when>
						<c:when test="${pbocAllAllocateInfo.businessType == '50' }">
							<input id="btnCancel" class="btn btn-primary" type="button"
								value="<spring:message code='common.return'/>"
								onclick="window.location.href='${ctx}/allocation/v02/pbocHandinOrder/back?allId=${pbocAllAllocateInfo.allId}&pageType=bussnessApplicationList'" />
						</c:when>
						<c:when test="${pbocAllAllocateInfo.businessType == '52' }">
							<input id="btnCancel" class="btn btn-primary" type="button"
								value="<spring:message code='common.return'/>"
								onclick="window.location.href='${ctx}/allocation/v02/pbocAgentHandinOrder/back?allId=${pbocAllAllocateInfo.allId}&pageType=bussnessApplicationList'" />
						</c:when>
					</c:choose>
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'storeApprovalView'}">
					<input id="btnCancel" class="btn btn-primary" type="button"
						value="<spring:message code='common.return'/>"
						onclick="window.location.href='${ctx}/allocation/v02/pbocAllocatedOrder/back?allId=${pbocAllAllocateInfo.allId}&pageType=storeApprovalList'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'storeHandinApprovalView'}">
					<input id="btnCancel" class="btn btn-primary" type="button"
						value="<spring:message code='common.return'/>"
						onclick="window.location.href='${ctx}/allocation/v02/pbocHandinApproval/back?allId=${pbocAllAllocateInfo.allId}&pageType=storeHandinApprovalList'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'storeQuotaView'}">
					<input id="btnCancel" class="btn btn-primary" type="button"
						value="<spring:message code='common.return'/>"
						onclick="window.location.href='${ctx}/allocation/v02/pbocAllocatedQuota/back?allId=${pbocAllAllocateInfo.allId}&pageType=storeQuotaList'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'storeHandinView'}">
					<input id="btnCancel" class="btn btn-primary" type="button"
							value="<spring:message code='common.return'/>"
							onclick="window.location.href='${ctx}/allocation/v02/pbocHandinInStore/back?allId=${pbocAllAllocateInfo.allId}'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'destroyApprovalView'}">
					<input id="btnCancel" class="btn btn-primary" type="button"
							value="<spring:message code='common.return'/>"
							onclick="window.location.href='${ctx}/allocation/v02/pbocDestroyOutStore/back?allId=${pbocAllAllocateInfo.allId}'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'allocatedOutStoreView' }">
					<input id="btnCancel" class="btn btn-primary" type="button"
								value="<spring:message code='common.return'/>"
								onclick="window.location.href='${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/back?allId=${pbocAllAllocateInfo.allId}'" />
				</c:if>
				<c:if test="${pbocAllAllocateInfo.pageType == 'allocatedInStoreView' }">
					<input id="btnCancel" class="btn btn-primary" type="button"
								value="<spring:message code='common.return'/>"
								onclick="window.location.href='${ctx}/allocation/v02/pbocHorizontalAllocatedInStore/back?allId=${pbocAllAllocateInfo.allId}'" />
				</c:if>
				
			</div>
		</div>
	</form:form>
</body>
</html>