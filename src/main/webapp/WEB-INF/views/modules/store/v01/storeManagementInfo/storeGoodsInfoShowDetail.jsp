<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-top:15px;margin-left:2px;">
	<div style="color:rgb(51,51,51); margin-left: 2px"><spring:message code="common.boxNo" />:${storeGoodsInfo.boxNo}</div>
	<div style="color:rgb(51,51,51); margin-left: 2px"><spring:message code="store.rfid" />:${storeGoodsInfo.rfid}</div>
	<div style="color:rgb(51,51,51); margin-left: 2px"><spring:message code="store.boxType" />:${fns:getDictLabel(storeGoodsInfo.boxType,'sto_box_type',"")}</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 物品名称 --%>
				<th style="text-align:center;"><spring:message code="store.goodsName" /></th>
				<%-- 数量 --%>
				<th style="text-align:center;"><spring:message code="common.number" /></th>
				<%-- 金额 --%>
				<th style="text-align:center;"><spring:message code="common.amount" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items = "${storeGoodsInfo.storeGoodsDetailList}" var = "goodsDetail">
			<tr>
				<td style="text-align:center;">${sto:getGoodsName(goodsDetail.goodsId)}</td>
				<td style="text-align:right;">${goodsDetail.goodsNum}</td>
				<td style="text-align:right;"><fmt:formatNumber value="${goodsDetail.amount}" pattern="#,##0.00#" /></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</body>
</html>