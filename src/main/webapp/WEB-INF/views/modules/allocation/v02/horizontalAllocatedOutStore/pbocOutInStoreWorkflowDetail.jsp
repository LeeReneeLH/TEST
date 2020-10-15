<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<style>
		/* 输入项 */
		.item {display: inline;width:350px;float: left;}
		/* 清除浮动 */
		.clear {clear: both;}
	</style>
</head>
<body>
	<div style="margin-top: 5px; margin-left: 5px; margin-right: 5px;">
		<%-- 流水单号 --%>
		<div class="control-group item" style="margin-left:20px">
			<label class="control-label">
				<spring:message code="allocation.allId" />：
			</label> 
			<label> 
				<input type="text" value="${operateInfo.allId}" style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>
		<%-- 操作人员--%>
		<div class="control-group item" style="margin-left:20px">
			<label class="control-label">
				<spring:message code="allocation.updateName" />：
			</label> 
			<label> 
				<input type="text" value="${operateInfo.updateName}" style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<%-- 操作时间 --%>
		<div class="control-group item">
			<label class="control-label">
				<spring:message code="allocation.updateDate" />：
			</label> 
			<label> 
				<input type="text" value="<fmt:formatDate value="${operateInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				style="width: 170px;" readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>
		<c:if test="${operateInfo.status == 41}">
			<%-- 交接人员--%>
			<div class="control-group item" style="margin-left:20px;width: 600px;">
				<label class="control-label">
					交接人员：
				</label> 
				<label> 
					<textarea type="text" style="width: 400px;" rows="1" readOnly="readOnly">${handoverStaff}</textarea>
				</label>
			</div>
			<div class="clear"></div>
			<%-- 授权人员--%>
			<div class="control-group item" style="margin-left:20px;">
				<label class="control-label">
					授权人员：
				</label> 
				<label> 
					<input type="text" value="${managerStaff}" style="width: 170px;" readOnly="readOnly" />
				</label>
			</div>
			<div class="clear"></div>
		</c:if>
		<%-- 物品名称 --%>
		<div class="bg-light-blue disabled color-palette">
			<span style="margin-left: 20px">
				<spring:message code="store.goodsInfo" />
			</span>
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
						<c:if test="${items.registType == '11'}">
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
		<%-- 操作历史 --%>
		<div class="bg-light-blue disabled color-palette">
			<span style="margin-left: 20px">
				<spring:message code="allocation.history" />
			</span>
		</div>
		<div style="height: 220px">
			<table id="contentTable" class="table table-hover">
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 操作时间 --%>
						<th style="text-align: center;">
							<spring:message code="allocation.updateDate" />
						</th>
						<%-- 状态 --%>
						<th style="text-align: center; width: 210px">
							<spring:message code="allocation.business.status" />
						</th>
						<%-- 操作人 --%>
						<th style="text-align: center;">
							<spring:message code="allocation.updateName" />
						</th>
					</tr>
				</thead>
				<c:forEach items="${historyList}" var="items">
					<tbody>
						<tr>
							<td style="text-align: center;">
								<fmt:formatDate value="${items.updateDate}" pattern="yyyy-MM-dd HH:mm:ss " />
							</td>
							<td style="text-align: center;">
								${fns:getDictLabel(items.status,'pboc_order_quota_status',"")}
							</td>
							<td style="text-align: center;">
								${items.updateName}
							</td>
						</tr>
					</tbody>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>