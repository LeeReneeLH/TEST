<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<link href="${ctxStatic}/css/modules/allocation/v01/out/order/cashorder/printCashOrderDetail.css" rel="stylesheet" />
</head>
<body>
	<div id="printDiv">
		<div  style="text-align: center">
			<%-- 重空装配登记 --%>
			<h3><spring:message code="allocation.imp.blank.doc.quota.register" /></h3>
		</div>
		<hr style="margin: 10px;"/>
		<%-- 网点名称 库房端显示--%>
		<div>
			<label><spring:message
					code='common.outletsName' />：${allocationOrder.rOffice.name}</label>
		</div>
		<div>
			<label><spring:message code='allocation.imp.blank.doc.quota.date'/>：<fmt:formatDate value="${allocationOrder.acceptDate}" pattern="yyyy-MM-dd"/>
			</label>
		</div>
		<div class="clear"></div>
		<table class="headTable">
			<tr class="tr1">
				<td colspan="2">出库机构盖章：</td>
				<td colspan="2">入库机构盖章：</td>
			</tr>
			<tr class="tr2">
				<td>库管员：</td>
				<td>复核人：</td>
				<td>收款人：</td>
				<td>复核人：</td>
			</tr>
		</table>
		<table id="contentTable"
			style="width: 100%; border-color: black; text-align: left" border="1">
			<thead>
				<tr>
					<%-- 重空分类 --%>
					<th style="text-align: center"><spring:message
							code='common.importantEmpty.kind' /></th>
					<%-- 重空类型 --%>
					<th style="text-align: center"><spring:message
							code='common.importantEmpty.type' /></th>
					<%-- 预约数量 --%>
					<th style="text-align: center"><spring:message
							code='allocation.order.number' /></th>
					<%-- 装配数量 --%>
					<th style="text-align: center"><spring:message
							code='allocation.imp.blank.doc.quota.number' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${allocationOrder.allAllocateItemMap}"
					var="allAllocateItem" varStatus="status">
					<tr>
						<%-- 重空分类 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.currency,'blank_bill_kind','')}
						</td>
						<%-- 重空类型 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.classification,'blank_bill_type','')}
						</td>
						<%-- 预约数量 --%>
						<td style="text-align: center">
							${allAllocateItem.value.moneyNumber}
						</td>
						<%-- 装配数量 --%>
						<c:choose>										
							<c:when test="${allocationOrder.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
								<td style="text-align: center"><fmt:formatNumber
										value="0" pattern="0" /></td>
							</c:when>
							<c:otherwise>
								<td style="text-align: center">
								${allAllocateItem.value.confirmNumber}</td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
