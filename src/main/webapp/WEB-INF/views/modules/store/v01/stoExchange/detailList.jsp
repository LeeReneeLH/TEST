<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%-- 总金额 --%>
<h4 class="text-red"> <spring:message
		code="common.totalMoney" />：<fmt:formatNumber
		value="${stoExchangeDetail.changeAmount}" pattern="#,##0.00#" />
</h4>
<div class="clear"></div>
	<div id="detail"
	style=" height: 191px; width: 900px; overflow: auto; overflow-x: hidden;">
	<table id="contentTable"
		class="table  table-hover">
		<thead>
			<tr>
				<%-- 币种 --%>
				<th style="text-align: center"><spring:message
						code='common.currency' /></th>
				<%-- 类别 --%>
				<th style="text-align: center"><spring:message
						code="common.classification" /></th>
				<%-- 现金材质 --%>
				<th style="text-align: center"><spring:message
						code='common.cash' /></th>
				<%-- 套别 --%>
				<th style="text-align: center"><spring:message
						code="common.edition" /></th>
				<%-- 面值 --%>
				<th style="text-align: center"><spring:message
						code='common.denomination' /></th>
				<%-- 单位 --%>
				<th style="text-align: center"><spring:message
						code="common.units" /></th>
				<%-- 数量 --%>
				<th style="text-align: center"><spring:message
						code="common.number" /></th>
				<%-- 金额 --%>
				<th style="text-align: center"><spring:message
						code="common.amount" /></th>
				<%-- 操作 --%>
				<th style="text-align: center"><spring:message
						code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${stoExchangeDetail.stoExchangeGoodList}"
				var="item" varStatus="status">
				<tr>
					<%-- 币种 --%>
					<td style="text-align: center">
						${sto:getGoodDictLabel(item.currency,'currency',"")}</td>
					<%-- 类别 --%>
					<td style="text-align: center">
						${sto:getGoodDictLabel(item.classification,'classification',"")}</td>
					<%-- 现金材质 --%>
					<td style="text-align: center">
						${sto:getGoodDictLabel(item.cash,'cash',"")}</td>
					<%-- 套别 --%>
					<td style="text-align: center">
						${sto:getGoodDictLabel(item.edition,'edition',"")}</td>
					<%-- 面值 --%>
					<td style="text-align: center">
						${sto:getDenLabel(item.currency, item.denomination, "")}</td>
					<%-- 单位 --%>
					<td style="text-align: center">
						${sto:getGoodDictLabel(item.unit,'c_unit',"")}${sto:getGoodDictLabel(item.unit,'p_unit',"")}
					</td>
					<%-- 数量 --%>
					<td style="text-align: right">${item.num}</td>
					<%-- 金额 --%>
					<td style="text-align: right"><fmt:formatNumber
							value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
					<td style="text-align: center"><a href="javascript:void(0);"
						onclick="deleteDetail('${item.goodsId}')"> <spring:message
								code="common.delete" />
					</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<script type="text/javascript">
	function deleteClass(url) {
		$.post(url, function() {
			$("#detailDiv").load("${ctx}/store/v01/stoExchange/getDetailList");
		}, "json");
	}
</script>
