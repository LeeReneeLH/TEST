<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 钞箱出库详细信息-->
	<title>钞箱出库详细信息</title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-top:15px;margin-left:2px;">
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr class="bg-light-blue disabled color-palette">
				<!-- 箱号-->
				<th>箱号</th>
				<!-- 钞箱类型-->
				<th>钞箱类型</th>
				<!-- 扫描状态-->
				<th>扫描状态</th>
				<!-- 登记金额-->
				<%-- <th>登记金额<spring:message code="common.units.yuan.alone" /></th> --%>
				<!-- RFID-->
				<th>RFID</th>
				<!-- 扫描时间-->
				<th>扫描时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items = "${atmDetailList}" var = "atmDetail">
			<tr>
				<td style="text-align:left;">${atmDetail.boxNo}</td>
				<td style="text-align:left;">${fns:getDictLabel(atmDetail.boxType,'sto_box_type',"")}</td>
				<td style="text-align:left;">${fns:getDictLabel(atmDetail.scanFlag,'all_box_scanFlag',"")}</td>
				<%-- <td style="text-align:right;"><fmt:formatNumber value="${atmDetail.amount}" pattern="#,##0.00#" /></td> --%>
				<td style="text-align:left;">${atmDetail.rfid}</td>
				<td style="text-align:left;"><fmt:formatDate value="${atmDetail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

</body>
</html>