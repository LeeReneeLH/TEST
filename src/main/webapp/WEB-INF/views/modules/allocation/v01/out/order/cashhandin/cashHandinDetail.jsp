<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 明细查询 --%>
	<title>
	<spring:message code="allocation.show.detail" />
	</title>
	<meta name="decorator" content="default" />
	<style>
	<!-- /* 输入项 */
	.item {display: inline;float: left;}
	/* 清除浮动 */
	.clear {clear: both;}
	/* 标签宽度 */
	.label_width {width: 120px;}
	-->
	</style>
</head>
<body>

	<ul class="nav nav-tabs">
		<%-- 现金上缴列表(link) --%>

		<li><a href="${ctx}/allocation/v01/cashHandin/list"><spring:message
					code="allocation.cash.handin.list" /></a></li>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message
								code="allocation.show.detail" /></a></li>
	</ul>
	
	<%-- 现金详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">
		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />
		<form:hidden path="authorization" />
		
		<div class="row">
			<div class="span10">
				<div class="clear"></div>
				<%-- 流水单号 --%>
				<div class="control-group item">
					<label class="control-label" style="width:70px;"><spring:message code="allocation.allId" />：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="allId" name="allId" 
							value="${allAllocateInfo.allId}" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" style="width:70px;">登记日期：</label>
					<div class="controls" style="margin-left:80px; width:170px;">
						<input type="text" id="applyDate" name="applyDate" 
						value="<fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd"/>"
						class="input-medium" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<!-- 分隔线 -->
		<div class="row">
			<div style="margin-top:4px">
				<HR style="width:1260px;FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
		<%-- <div id="detailDiv"
			style="margin-left: 102px; height: 305px; width: 800px; overflow: auto; overflow-x: hidden;">
			<c:choose>
				<c:when
					test="${allocationHandinCash.countItemMap != null and allocationHandinCash.countItemMap.size() != 0}">
					<label> 总金额（<c:forEach
							items="${allocationHandinCash.countItemMap}" var="countItem"
							varStatus="status">
												${sto:getGoodDictLabel(countItem.value.currency,'currency',"")}：<fmt:formatNumber
								value="${countItem.value.moneyAmount}" pattern="#,##0.00#" />
							<c:choose>
								<c:when test="${status.index !=0 and status.index % 4 == 0}">
									<br />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when
											test="${status.index != allocationHandinCash.countItemMap.size() - 1}">
																&nbsp;&nbsp;
															</c:when>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:forEach>）
					</label>
				</c:when>
			</c:choose> --%>
			<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div class="clear"></div>
						<div class="control-group item">
							<%-- 申请总金额 --%>
							<label class="control-label" style="width:90px;"><spring:message code="allocation.application.totalAmount" />：</label>
							<div class="controls" style="margin-left:100px;">
								<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.registerAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 申请总金额(格式化) --%>
								<%-- <label style="margin-left:10px">${allAllocateInfo.registerAmountBig}</label> --%>
							</div>
						</div>
					</div>
				</div>
			<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div style="overflow-y: auto; height: 315px;">
			    		<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center">登记明细</h4>
					<table id="contentTable" class="table table-hover" >
					<thead>
						<%-- <tr>
							申请明细
							<th style="text-align: center" colspan="5">登记明细</th>
						</tr> --%>
									<tr>
										<%-- 序号 --%>
										<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
										<%-- 券别列表 --%>
										<th style="text-align: center" >券别信息</th>
										<%-- 登记数量 --%>
										<th style="text-align: center" >登记数量</th>
										<%-- 登记金额(元) --%>
										<th style="text-align: center" >登记金额(元)</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
										<%-- 申请登记物品 --%>
											<tr>
												<td width="10%" style="text-align:center">${status.index+1}</td>
												<td style="text-align:right;">${sto:getGoodsName(item.goodsId)}</td>
												<td style="text-align:right;">${item.moneyNumber}</td>
												<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			<div class="row" >
			
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
								<%-- 券别列表 --%>
								<th style="text-align: center" >卷别信息</th>
								<%-- 金额(元) --%>
								<th style="text-align:center;"><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
								<%-- 箱袋编号 --%>
								<%-- <th style="text-align:center;"><spring:message code="common.boxNo" /></th> --%>
								<%-- 旧箱袋编号 --%>
								<%-- <th style="text-align:center;"><spring:message code="allocation.oldRfid" /></th> --%>
								<%-- 抽检状态 --%>
								<%-- <th style="text-align:center;"><spring:message code="allocation.isCheck" /></th> --%>
								<!-- 抽检结果  -->
								<%-- <th style="text-align:center;"><spring:message code="allocation.checkResult" /></th> --%>
								<!-- 人行扫描状态  -->
								<th style="text-align:center;">扫描状态</th>
								<!-- 人行出入库类型  -->
								<%-- <th style="text-align:center;"><spring:message code="allocation.inOut.type" /></th> --%>
								<!-- 所属机构  -->
								<!-- <th style="text-align:center;">所属机构</th> -->
								<%-- 位置状态 --%>
								<!-- <th style="text-align:center;">位置状态</th> -->
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${allAllocateInfo.allDetailList}" var="allAllocateDetail" varStatus="status">
								<tr>
									<%-- 序号 --%>
									<td style="text-align:right;">${status.index + 1}</td>
									<%-- 券别列表 --%>
									<td style="text-align:left;">${sto:getGoodsName(allAllocateDetail.goodsId)}</td>
									<%-- 金额(元) --%>
									<td style="text-align:right;"><fmt:formatNumber value="${allAllocateDetail.amount}" pattern="#,##0.00#" /></td>
									<%-- 箱袋编号 --%>
									<%-- <td style="text-align:center;">
										<a href="#" onclick="detailInfo('${pbocAllAllocateDetail.rfid}')">${pbocAllAllocateDetail.rfid}</a>
									</td> --%>
									<%-- 旧箱袋编号 --%>
									<%-- <td style="text-align:center;">
										<a href="#" onclick="detailInfo('${pbocAllAllocateDetail.stoRfidDenomination.oldRfid}')">${pbocAllAllocateDetail.stoRfidDenomination.oldRfid}</a>
									</td> --%>
									<%-- <c:if test="${empty pbocAllAllocateDetail.stoRfidDenomination.oldRfid }">
										<!-- 抽检状态  -->
										<td style="text-align:center;">${fns:getDictLabel(pbocAllAllocateDetail.stoRfidDenomination.isCheck,'pboc_all_isCheck',"")}</td>
										<!-- 抽检结果  -->
										<td style="text-align:center;">${fns:getDictLabel(pbocAllAllocateDetail.stoRfidDenomination.checkResult,'pboc_all_checkResult',"")}</td>
									</c:if>
									<c:if test="${not empty pbocAllAllocateDetail.stoRfidDenomination.oldRfid }">
										<!-- 旧箱袋抽检状态  -->
										<td style="text-align:center;">${fns:getDictLabel(pbocAllAllocateDetail.oldStoRfidDenomination.isCheck,'pboc_all_isCheck',"")}</td>
										<!-- 旧箱袋抽检结果  -->
										<td style="text-align:center;">${fns:getDictLabel(pbocAllAllocateDetail.oldStoRfidDenomination.checkResult,'pboc_all_checkResult',"")}</td>
									</c:if> --%>
									<!-- 人行扫描状态  -->
									<td style="text-align:center;">${fns:getDictLabel(allAllocateDetail.scanFlag,'all_box_scanFlag',"")}</td>
									<!-- 人行出入库类型  -->
									<%-- <td style="text-align:center;">${fns:getDictLabel(allAllocateDetail.inoutType,'all_inout_type',"")}</td> --%>
									<!-- 所属机构  -->
									<%-- <td style="text-align:center;">${allAllocateDetail.stoRfidDenomination.office.name}</td> --%>
									<%-- 位置状态 --%>
									<%-- <td style="text-align:center;">${fns:getDictLabel(allAllocateDetail.stoRfidDenomination.boxStatus,'sto_box_status','')}</td> --%>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
			
		<div>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashHandin/back?allId=${allAllocateInfo.allId}'" />
		</div>
	</form:form>
</body>
</html>
