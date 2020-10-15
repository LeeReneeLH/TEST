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
			<%-- 同业配款登记--%>
			<h3><spring:message code="allocation.cash.same.trade.quota.register" /></h3>
		</div>
		<hr style="margin: 10px;"/>
		<%-- 网点名称 库房端显示--%>
		<div>
			<label><spring:message
					code='common.outletsName' />：${allocationSameTradeOrder.rOffice.name}</label>
		</div>
		<div class="tr">
			<div class="td">
				<label><spring:message code='allocation.point.type' />：<spring:message code='allocation.point.type.same.trade.yes' />
				</label>
			</div>
			<div class="td">
				<label><spring:message code='allocation.cash.quota.date'/>：<fmt:formatDate value="${allocationSameTradeOrder.acceptDate}" pattern="yyyy-MM-dd"/>
				</label>
			</div>
		</div>
		<div class="clear"></div>
		<div class="tr">
		<%-- 配款总金额  --%>
			<div class="td">
				<c:choose>
					<c:when
						test="${allocationSameTradeOrder.countItemMap != null and allocationSameTradeOrder.countItemMap.size() != 0}">
						<label> <spring:message
								code="allocation.cash.quota.amount.all" />（<c:forEach
								items="${allocationSameTradeOrder.countItemMap}" var="countItem"
								varStatus="status">${sto:getGoodDictLabel(countItem.value.currency,'currency',"")}：<fmt:formatNumber
									value="${countItem.value.moneyAmount}" pattern="#,##0.00#" /><c:choose>
									<c:when test="${status.index !=0 and status.index % 4 == 0}"><br /></c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${status.index != allocationSameTradeOrder.countItemMap.size() - 1}">&nbsp;&nbsp;</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:forEach>）
						</label>
					</c:when>
				</c:choose>
			</div>
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
					<%-- 币种 --%>
					<th style="text-align: center"><spring:message
							code='common.currency' /></th>
					<%-- 类别 --%>
					<th style="text-align: center"><spring:message
							code="common.classification" /></th>
					<%-- 套别 --%>
					<th style="text-align: center"><spring:message
							code="common.edition" /></th>
					<%-- 现金材质 --%>
					<th style="text-align: center"><spring:message
							code='common.cash' /></th>
					<%-- 面值--%>
					<th style="text-align: center"><spring:message
							code='common.denomination' /></th>
					<%-- 单位 --%>
					<th style="text-align: center"><spring:message
							code="common.units" /></th>
					<%-- 配款数量 --%>
					<th style="text-align: center"><spring:message
							code='allocation.cash.quota.number' /></th>
					<%-- 配款金额 --%>
					<th style="text-align: center"><spring:message code='allocation.cash.quota.amount' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${allocationSameTradeOrder.allAllocateItemMap}"
					var="allAllocateItem" varStatus="status">
					<tr>
						<%-- 币种 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.currency,'currency',"")}
						</td>
						<%-- 类别 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.classification,'classification',"")}
						</td>
						<%-- 套别 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.sets,'edition',"")}
						</td>
						<%-- 现金材质 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.cash,'cash',"")}</td>
						<%-- 面值 --%>
						<td style="text-align: center">
							${sto:getDenLabel(allAllocateItem.value.currency, allAllocateItem.value.denomination, "")}
						</td>
						<%-- 单位 --%>
						<td style="text-align: center">
							${sto:getGoodDictLabel(allAllocateItem.value.unit,'c_unit',"")}${sto:getGoodDictLabel(allAllocateItem.value.unit,'p_unit',"")}
						</td>
						<%-- 配款数量 --%>
						<td style="text-align: center">
							${allAllocateItem.value.moneyNumber}</td>
						<%-- 配款金额 --%>
						<td style="text-align: right"><fmt:formatNumber
								value="${allAllocateItem.value.moneyAmount}"
								pattern="#,##0.00#" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
