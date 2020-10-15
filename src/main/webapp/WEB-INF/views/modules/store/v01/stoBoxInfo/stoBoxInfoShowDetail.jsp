<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-top:15px;margin-left:2px;">
	<div style="color:rgb(51,51,51); margin-left:10px;font-size:18px;font-weight:bold;line-height:30px;">箱袋编号:${boxNo.boxNo}</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr class="bg-light-blue disabled color-palette">
				<%-- 物品名称 --%>
				<th>物品名称</th>
				<%-- 物品数量 --%>
				<th>物品数量</th>
				<%-- 物品金额 --%>
				<th>物品金额</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items = "${boxDetailList}" var = "stoBoxDetail">
			<tr>
				<td style="text-align:center;">${sto:getGoodsName(stoBoxDetail.goodsId)}</td>
				<td style="text-align:center;">${stoBoxDetail.goodsNum}</td>
				<td style="text-align:center;"><fmt:formatNumber value="${stoBoxDetail.goodsAmount}" pattern="#,##0.00#" /></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</body>
</html>