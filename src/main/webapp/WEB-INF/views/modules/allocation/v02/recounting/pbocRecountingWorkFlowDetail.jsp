<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<style>
	<!-- /* 输入项 */
	.item {display: inline;width:350px;float: left;}
	/* 清除浮动 */
	.clear {clear: both;}
	/* 标签宽度 */
	.label_width {width: 120px;}
	-->
	</style>
</head>
<body>
	<div style="margin-top: 5px; margin-left: 5px; margin-right: 5px;">
		<%-- 流水单号 --%>
		<div class="control-group item" style="margin-left:17px">
			<label class="control-label"><spring:message code="allocation.allId" />：</label> 
			<label> 
				<input type="text" value="${operateInfo.allId}" style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>

		<div class="control-group item" style="margin-left:17px">
			<%-- 操作人员--%>
			<label class="control-label"><spring:message code="allocation.updateName" />：</label> 
			<label> 
				<input type="text" value="${operateInfo.updateName}" style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<div class="control-group item">
			<%-- 操作时间 --%>
			<label class="control-label"><spring:message code="allocation.updateDate" />：</label> 
			<label> 
				<input type="text" value="<fmt:formatDate value="${operateInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>
		<!-- 出库交接信息 -->
		<c:if test="${operateInfo.status eq '42'}">
			<!-- 交接授权人 -->
			<c:if test="${outManagerList != null}">
				<c:forEach items="${outManagerList}" var="manager">
					<div class="control-group item" style="margin-left:17px">
						<%-- 授权人--%>
						<label class="control-label"><spring:message
								code="allocation.managerName" />：</label> <label> <input
							type="text" value="${manager}"
							style="width: 170px;" readOnly="readOnly" />
						</label>
					</div>
					<div class="clear"></div>
				</c:forEach>
			</c:if>
			<!-- 交接人 -->
			<c:if test="${outHandoverList.size() != '0'}">
				<div class="control-group" style="margin-left:17px">
					<label class="control-label"><spring:message
							code="allocation.handoverName" />：</label> <label> <textarea
							style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="1" readonly="readonly" ><c:forEach items="${outHandoverList}" var="handover">${handover.escortName}&nbsp;</c:forEach></textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
		</c:if>
		
		<!-- 入库交接信息 -->
		<c:if test="${operateInfo.status eq '99'}">
			<!-- 交接授权人 -->
			<c:if test="${inManagerList != null}">
				<c:forEach items="${inManagerList}" var="manager">
					<div class="control-group item" style="margin-left:17px">
						<%-- 授权人--%>
						<label class="control-label"><spring:message
								code="allocation.managerName" />：</label> <label> <input
							type="text" value="${manager}"
							style="width: 170px;" readOnly="readOnly" />
						</label>
					</div>
					<div class="clear"></div>
				</c:forEach>
			</c:if>
			<!-- 交接人 -->
			<c:if test="${inHandoverList.size() != '0'}">
				<div class="control-group" style="margin-left:17px">
					<label class="control-label"><spring:message
							code="allocation.handoverName" />：</label> <label> <textarea
							style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="1" readonly="readonly" ><c:forEach items="${inHandoverList}" var="handover">${handover.escortName}&nbsp;</c:forEach></textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
		</c:if>
		
		<c:if test="${operateInfo.status eq '40'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.goodsInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- RFID编号 --%>
							<th style="text-align: center" ><spring:message code="common.boxNo" /></th>
							<%-- 箱袋状态 --%>
							<th style="text-align: center;"><spring:message code="common.status" /></th>
						</tr>
					</thead>
					<c:forEach items="${printDataList}" var="allAllocateInfo">
						<c:forEach items="${allAllocateInfo.pbocAllAllocateItemList}" var="allAllocateItem">
							<c:forEach items="${allAllocateItem.goodsAreaDetailList}" var="areaDetail" varStatus="status">
								<td style="text-align: center;">${areaDetail.goodsLocationInfo.rfid}</td>
								<td style="text-align: center;"><spring:message code="allocation.pboc.quota" /></td>
							</c:forEach>
						</c:forEach>
					</c:forEach>
				</table>
			</div>
		</c:if>
		
		<c:if test="${operateInfo.status eq '41'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.goodsInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- RFID编号 --%>
							<th style="text-align: center" ><spring:message code="common.boxNo" /></th>
							<%-- 箱袋状态 --%>
							<th style="text-align: center;"><spring:message code="common.status" /></th>
						</tr>
					</thead>
					<c:forEach items="${operateInfo.pbocAllAllocateDetailList}" var="items">
					<c:if test="${items.inoutType eq '0'}">
						<tbody>
							<tr>
								<td style="text-align: center;">${items.rfid}</td>
								<td style="text-align: center;">${fns:getDictLabel(items.scanFlag,'all_box_scanFlag',"")}</td>
							</tr>
						</tbody>
					</c:if>
					</c:forEach>
				</table>
			</div>
		</c:if>
		
		<c:if test="${operateInfo.status eq '22'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.goodsInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- 物品名称 --%>
							<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
							<%-- 数量 --%>
							<th style="text-align: center" ><spring:message code="common.number" /></th>
							<%-- 金额(元) --%>
							<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
						</tr>
					</thead>
					<c:forEach items="${operateInfo.pbocAllAllocateItemList}" var="items">
						<tbody>
							<c:if test="${items.registType == '10'}">
								<tr>
									<td style="text-align: center;">${sto:getGoodsName(items.goodsId)}</td>
									<td style="text-align: center;">${items.moneyNumber}</td>
									<td style="text-align: center;"><fmt:formatNumber value="${items.moneyAmount}" pattern="#,##0.00#" /></td>
								</tr>
							</c:if>
						</tbody>
					</c:forEach>
				</table>
			</div>
		</c:if>
		
		<div class="bg-light-blue disabled color-palette">
			<span style="margin-left: 20px"><spring:message
					code="allocation.history" /></span>
		</div>
		<div style="overflow: scroll; height: 130px">
			<table id="contentTable" class="table table-hover">
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 操作时间 --%>
						<th style="text-align: center;"><spring:message
								code="allocation.updateDate" /></th>
						<%-- 状态 --%>
						<th style="text-align: center; width: 210px"><spring:message
								code="allocation.business.status" /></th>
						<%-- 操作人 --%>
						<th style="text-align: center;"><spring:message
								code="allocation.updateName" /></th>
					</tr>
				</thead>
				<c:forEach items="${historyList}" var="items">
					<tbody>
						<tr>
							<td style="text-align: center;"><fmt:formatDate
									value="${items.updateDate}" pattern="yyyy-MM-dd HH:mm:ss " /></td>
							<td style="text-align: center;">${fns:getDictLabel(items.status,'pboc_recounting_status',"")}</td>
							<td style="text-align: center;">${items.updateName}</td>
						</tr>
					</tbody>
				</c:forEach>
			</table>
		</div>
	</div>

</body>
</html>