<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-top:15px;margin-left:2px;">
	<div style="color:rgb(51,51,51); margin-left:10px;font-size:18px;font-weight:bold;line-height:30px;">加钞组名称:${stoAddCashGroup.groupName}</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr class="bg-light-blue disabled color-palette">
				<%-- 车辆名称 --%>
				<th style="text-align:center;"><spring:message code='store.escortCar'/></th>
				<%-- 押运人1 --%>
				<th style="text-align:center;"><spring:message code='store.escortUser1'/></th>
				<%-- 押运人2 --%>
				<th style="text-align:center;"><spring:message code='store.escortUser2'/></th>
			</tr>
		</thead>
		<tbody>
			<td style="text-align:center;">${stoAddCashGroup.carName}</td>
			<td style="text-align:center;">${stoAddCashGroup.escortName1}</td>
			<td style="text-align:center;">${stoAddCashGroup.escortName2}</td>
		</tbody>
	</table>
</div>

</body>
</html>