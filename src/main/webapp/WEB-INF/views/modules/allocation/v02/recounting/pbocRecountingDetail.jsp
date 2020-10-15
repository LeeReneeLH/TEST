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
		<%-- 发行基金复点管理(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocRecounting/list"><spring:message code="allocation.pboc.recounting.mgr" /></a></li>
		<shiro:hasPermission name="allocation.v02:pbocRecounting:registe">
			<%-- 发行基金复点出库登记(link) --%>
			<li><a href="${ctx}/allocation/v02/pbocRecounting/form"><spring:message code="allocation.pboc.recounting.outStore.register" /></a></li>
		</shiro:hasPermission>
		<%-- 明细查询(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.show.detail" /></a></li>
		
	</ul>
	<form:form id="inputForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="allId"/>
		<sys:message content="${message}"/>	
		<div class="row">
			
			<div class="span10">
				<div class="clear"></div>
				<%-- 流水单号 --%>
				<div class="control-group item">
					<label class="control-label" style="width:80px;"><spring:message code="allocation.allId" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="allId" name="allId" 
							value="${pbocAllAllocateInfo.allId}" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="control-group item">
					<%-- 申请类型 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.application.type" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="businessType" name="businessType" 
						value="${fns:getDictLabel(pbocAllAllocateInfo.businessType, 'all_businessType', '')}"
						class="input-medium" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.order.date" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="applyDate" name="applyDate" 
						value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>"
						class="input-medium" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<div class="row">
			<div class="span10">
				
				<div class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:70px;"><spring:message code="common.status" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="status" name="status" 
									value="${fns:getDictLabel(pbocAllAllocateInfo.status,'pboc_recounting_status','')}"  
									style="width:149px;text-align:right;"  readOnly="readOnly"/>
					</div>
				</div>
			</div>
		</div>
		<c:if test="${fn:length(fn:trim(pbocAllAllocateInfo.remarks)) > 0 }">
			<div class="row">
				<div class="span10">
					<div class="control-group item">
						<%-- 备注 --%>
						<label class="control-label" style="width:70px;"><spring:message code="common.remark" />：</label>
						<div class="controls" style="margin-left:80px; width:170px;">
							<textarea style="width:700px;height:55px;color:red" class="input-xxlarge" readonly="readonly" >${pbocAllAllocateInfo.remarks}</textarea>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<div class="row">
			<%-- 复点明细 --%>
			<div class="span10" style="margin-top:4px">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)"  align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
		<div class="row">
			<div class="span8">
				<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div class="clear"></div>
						<div class="control-group item">
							<%-- 出库人行交接人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.outStore" /><spring:message code="allocation.pboc.handover" />：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='1' and handoverUserDetail.inoutType == '0'}">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${pbocOutHandoverIdList}');" value="${pbocOutHandover}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
			    		<div class="clear"></div>
			    		<div class="control-group item">
							
							<%-- 出库清分中心交接人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.clearCenter.handover.outStore" />：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='2' and handoverUserDetail.inoutType == '0'}">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${clearOutHandoverIdList}');" value="${clearOutHandover}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 审批总金额 --%>
							<label class="control-label" style="width:160px;"><spring:message code="allocation.approve.totalAmount" />：</label>
							<div class="controls" style="margin-left:160px;">
								<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.registerAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 复点出库总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.registerAmountBig}</label>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div style="overflow-y: auto; height: 315px;">
							<table id="contentTable" class="table table-hover" >
								<thead>
									<tr>
										<%-- 复点出库明细 --%>
										<th style="text-align: center" colspan="4"><spring:message code="allocation.pboc.recounting.outStore.detail" /></th>
									</tr>
									<tr>
										<%-- 序号 --%>
										<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
										<%-- 物品名称 --%>
										<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
										<%-- 数量 --%>
										<th style="text-align: center" ><spring:message code="common.number" /></th>
										<%-- 金额(元) --%>
										<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
										
									</tr>
								</thead>
								<tbody>
									<% int iRegistIndex = 0; %>
									<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateItemList}" var="item" varStatus="status">
										<%-- 复点出库登记物品 --%>
										<c:if test="${item.registType == '10'}">
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
			<div class="span8">
				<div class="row">
					<div class="span8" style="text-align: right">
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 入库人行交接人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.inStore" /><spring:message code="allocation.pboc.handover" />：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='2' and handoverUserDetail.inoutType == '1'}">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${pbocInHandoverIdList}');" value="${pbocInHandover}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 入库清分中心交接人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.clearCenter.handover.inStore" />：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='1' and handoverUserDetail.inoutType == '1' }">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${clearInHandoverIdList}');" value="${clearInHandover}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 复点入库总金额 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.inStore.totalAmount" />：</label>
							<div class="controls" style="margin-left:160px;">
								<input type="text" id="instoreAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.instoreAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 复点入库总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.instoreAmountBig}</label>
							</div>
						</div>
			    	</div>
				</div>
				<div class="row">
					<div class="span8" style="text-align: right">
						<div style="overflow-y: auto; height: 315px;">
							<table id="contentTable" class="table table-hover" >
								<thead>
									<tr>
										<%-- 复点入库明细 --%>
										<th style="text-align: center" colspan="5"><spring:message code="allocation.pboc.recounting.inStore.detail" /></th>
									</tr>
									<tr>
										<%-- 序号 --%>
										<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
										<%-- 物品名称 --%>
										<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
										<%-- 数量 --%>
										<th style="text-align: center" ><spring:message code="common.number" /></th>
										<%-- 金额(元) --%>
										<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
										
									</tr>
								</thead>
								<tbody>
									<% int iConfirmIndex = 0; %>
									<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateItemList}" var="item" varStatus="status">
										<%-- 复点入库登记物品 --%>
										<c:if test="${item.registType == '11'}">
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
		<shiro:hasAnyPermissions name="allocation.v02:pbocRecounting:registe,allocation.v02:pbocRecounting:quota">
		<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
		<div class="row" style="margin-top: 10px;">
			<c:choose>
				<c:when test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'  and printDataList != null and printDataList.size() > 0 }">
					<div class="span9">
						<div style="overflow-y: auto; height: 315px;">
							<table  id="contentTable" class="table table-hover">
								<thead>
									<tr>
										<%-- 复点出库取包明细 --%>
										<th style="text-align: center" colspan="6"><spring:message code="common.recounting.outStore" /><spring:message code="allocation.getBagDetail" /></th>
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
		</shiro:hasAnyPermissions>
		<div class="row" style="margin-top: 10px;">
			<%-- 复点出库箱袋调拨明细 --%>
			<div class="span9">
				<div class="row" style="margin-top: 10px;">
					<%-- 出库授权人 --%>
					<div class="span9" style="text-align: right">
						
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 出库授权人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.authorizer.outStore" />：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <label>${pbocAllAllocateInfo.pbocAllHandoverInfo.managerUserName}</label> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${outAuthorizeIdList}');" value="${outAuthorize}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 复点出库总金额 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.outStore.totalAmount" />：</label>
							<div class="controls" style="margin-left:160px;">
								<input type="text" id="outstoreAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.outstoreAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 复点出库总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.outstoreAmountBig}</label>
							</div>
						</div>
			    	</div>
				</div>
				<div style="overflow-y: auto; height: 315px;">
					<table  id="contentTable" class="table table-hover">
						<thead>
							<tr >
								<c:choose>
									<c:when test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
										<th style="text-align:center;" colspan="5"><spring:message code="common.recounting.outStore" /><spring:message code="allocation.box.detail" /></th>
									</c:when>
									<c:otherwise>
										<th style="text-align:center;" colspan="4"><spring:message code="common.recounting.outStore" /><spring:message code="allocation.box.detail" /></th>
									</c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 物品名称 --%>
								<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
								<%-- 金额(元) --%>
								<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
								<shiro:hasAnyPermissions name="allocation.v02:pbocRecounting:registe,allocation.v02:pbocRecounting:quota">
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
								    <%-- 库区位置 --%>
									<th style="text-align:center;"><spring:message code="store.areaPosition" /></th>
								</c:if>
								</shiro:hasAnyPermissions>
								</c:if>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
							</tr>
						</thead>
						<tbody>
							<% int iOutStoreInfoIndex = 0; %>
							<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateDetailList}" var="pbocAllAllocateDetail" varStatus="status">
								<c:if test="${pbocAllAllocateDetail.inoutType == '0'}">
									<% iOutStoreInfoIndex = iOutStoreInfoIndex + 1; %>
									<tr>
										<%-- 序号 --%>
										<td style="text-align:right;"><%=iOutStoreInfoIndex %></td>
										<%-- 物品名称 --%>
										<td style="text-align:left;">${sto:getGoodsName(pbocAllAllocateDetail.goodsLocationInfo.goodsId)}</td>
										<%-- 金额(元) --%>
										<td style="text-align:right;"><fmt:formatNumber value="${pbocAllAllocateDetail.goodsLocationInfo.amount}" pattern="#,##0.00#" /></td>
										<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
										<shiro:hasAnyPermissions name="allocation.v02:pbocRecounting:registe,allocation.v02:pbocRecounting:quota">
										<c:if test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
								            <%-- 库区位置 --%>
								            <td style="text-align:right;">${fns:getDictLabel(pbocAllAllocateDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${pbocAllAllocateDetail.goodsLocationInfo.storeAreaName}</td>
									    </c:if>
									    </shiro:hasAnyPermissions>
									    </c:if>
										<%-- 箱袋编号 --%>
										<td style="text-align:center;">${pbocAllAllocateDetail.rfid}</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<%-- 复点入库箱袋调拨明细 --%>
			<div class="span9">
				<div class="row" style="margin-top: 10px;">
					<%-- 入库授权人 --%>
					<div class="span9" style="text-align: right">
						
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 入库授权人 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.authorizer.inStore"/>：</label>
							<div class="controls" style="margin-left:160px;">
							<%-- update-start 照片显示 by yanbingxu 2018/04/04 --%>
								<%-- <label>${pbocAllAllocateInfo.pbocAllHandoverInfo.rcInManagerUserName}</label> --%>
								<input type="text" readonly="readonly" style="width: 400px;color: blue;" 
									onclick="getStorePerson('${pbocAllAllocateInfo.allId}','${inAuthorizeIdList}');" value="${inAuthorize}"/>
							<%-- update-end 照片显示 by yanbingxu 2018/04/04 --%>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 复点入库总金额 --%>
							<label class="control-label" style="width:160px;"><spring:message code="common.inStore.totalAmount" />：</label>
							<div class="controls" style="margin-left:160px;">
								<input type="text" id="instoreAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.instoreAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 复点入库总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.instoreAmountBig}</label>
							</div>
						</div>
			    	</div>
				</div>
				<div style="overflow-y: auto; height: 315px;">
					<table  id="contentTable" class="table table-hover">
						<thead>
							<tr >
								<c:choose>
									<c:when test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
										<th style="text-align:center;" colspan="5"><spring:message code="common.recounting.inStore" /><spring:message code="allocation.box.detail" /></th>
									</c:when>
									<c:otherwise>
										<th style="text-align:center;" colspan="4"><spring:message code="common.recounting.inStore" /><spring:message code="allocation.box.detail" /></th>
									</c:otherwise>
								</c:choose>
							</tr>
							<tr>
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 物品名称 --%>
								<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
								<%-- 金额(元) --%>
								<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
								<shiro:hasAnyPermissions name="allocation.v02:pbocRecounting:registe,allocation.v02:pbocRecounting:quota">
								<c:if test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
								    <%-- 库区位置 --%>
									<th style="text-align:center;"><spring:message code="store.areaPosition" /></th>
								</c:if>
								</shiro:hasAnyPermissions>
								</c:if>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
							</tr>
						</thead>
						<tbody>
							<% int iInStoreInfoIndex = 0; %>
							<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateDetailList}" var="pbocAllAllocateDetail" varStatus="status">
								<c:if test="${pbocAllAllocateDetail.inoutType == '1'}">
									<% iInStoreInfoIndex = iInStoreInfoIndex + 1; %>
									<tr>
										<%-- 序号 --%>
										<td style="text-align:right;"><%=iInStoreInfoIndex %></td>
										<%-- 物品名称 --%>
										<td style="text-align:left;">${sto:getGoodsName(pbocAllAllocateDetail.goodsLocationInfo.goodsId)}</td>
										<%-- 金额(元) --%>
										<td style="text-align:right;"><fmt:formatNumber value="${pbocAllAllocateDetail.goodsLocationInfo.amount}" pattern="#,##0.00#" /></td>
										<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
										<shiro:hasAnyPermissions name="allocation.v02:pbocRecounting:registe,allocation.v02:pbocRecounting:quota">
										<c:if test="${pbocAllAllocateInfo.pageType == 'storeRecountingView'}">
								            <%-- 库区位置 --%>
								            <td style="text-align:right;">${fns:getDictLabel(pbocAllAllocateDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${pbocAllAllocateDetail.goodsLocationInfo.storeAreaName}</td>
									    </c:if>
									    </shiro:hasAnyPermissions>
									    </c:if>
										<%-- 箱袋编号 --%>
										<td style="text-align:center;">${pbocAllAllocateDetail.rfid}</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			
		</div>
		<div class="row">
			<div class="form-actions">
				<%-- 返回 --%>
				<input id="btnCancel" class="btn btn-primary" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/allocation/v02/pbocRecounting/back?allId=${pbocAllAllocateInfo.allId}&pageType=storeRecountingList'" />
			</div>
		</div>
	</form:form>
</body>
</html>