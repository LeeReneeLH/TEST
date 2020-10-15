<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 明细查询 --%>
	<title><spring:message code="allocation.show.detail" /></title>
	<meta name="decorator" content="default"/>
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
		<%-- 原封新券入库列表(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocOriginalBankNoteIn/list"><spring:message code="allocation.originalBanknot.instore.list" /></a></li>
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
					<%-- 状态 --%>
					<label class="control-label" style="width:70px;"><spring:message code="common.status" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="status" name="status" 
							value="${fns:getDictLabel(pbocAllAllocateInfo.status,'pboc_original_instore_status','')}"  
							style="width:149px;text-align:right;"  readOnly="readOnly"/>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
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
							<%-- 人行交接人 --%>
							<label class="control-label" style="width:100px;"><spring:message code="allocation.pboc.handover" />：</label>
							<div class="controls" style="margin-left:100px;">
								<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='2' }">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 行外交接人 --%>
							<label class="control-label" style="width:100px;"><spring:message code="allocation.pboc.outside.handover" />：</label>
							<div class="controls" style="margin-left:100px;">
								<c:if test="${pbocAllAllocateInfo.pbocAllHandoverInfo != null }">
									<c:forEach items="${pbocAllAllocateInfo.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
										<c:if test="${handoverUserDetail.type =='1' }">
											${handoverUserDetail.escortName }&nbsp;&nbsp;
										</c:if>
									</c:forEach>
								</c:if>
							</div>
						</div>
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 入库总金额 --%>
							<label class="control-label" style="width:100px;"><spring:message code="common.inStore.totalAmount" />：</label>
							<div class="controls" style="margin-left:100px;">
								<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${pbocAllAllocateInfo.confirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 入库总金额(格式化) --%>
								<label style="margin-left:10px">${pbocAllAllocateInfo.confirmAmountBig}</label>
							</div>
						</div>
			    	</div>
				</div>
			</div>
		</div>
		
		<div class="row" style="margin-top: 10px;">
			<%-- 箱袋调拨明细 --%>
			<div class="span12">
				<div style="overflow-y: auto; height: 315px;">
				<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="allocation.box.detail" /></h4>
				<table id="contentTable" class="table table-hover" >
					<thead>
						<%-- <tr>
							申请明细
							<th style="text-align: center" colspan="5"><spring:message code="allocation.box.detail" /></th>
						</tr> --%>
							<tr>
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 物品名称 --%>
								<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
								<%-- 金额(元) --%>
								<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								<%-- 库区位置 --%>
								<th style="text-align:center;"><spring:message code="store.areaPosition" /></th>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
								<%-- 原封券翻译 --%>
								<th style="text-align:center;"><spring:message code="store.original.translation" /></th>
							    
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
						            <%-- 库区位置 --%>
						            <td style="text-align:right;">${fns:getDictLabel(pbocAllAllocateDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${pbocAllAllocateDetail.goodsLocationInfo.storeAreaName}</td>
									<%-- 箱袋编号 --%>
									<td style="text-align:center;">${pbocAllAllocateDetail.rfid }</td>
									<%-- 原封券翻译 --%>
									<td style="text-align:center;">${pbocAllAllocateDetail.stoOriginalBanknote.originalTranslate}</td>
								</tr>
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
								onclick="window.location.href='${ctx}/allocation/v02/pbocOriginalBankNoteIn/back'" />
			</div>
		</div>
	</form:form>
</body>
</html>