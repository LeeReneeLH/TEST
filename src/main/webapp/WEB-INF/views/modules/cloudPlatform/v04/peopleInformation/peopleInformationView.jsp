<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
	});
</script>
<style type="text/css">
table {
	border-collapse: collapse;
	table-layout: fixed;
}

th, td {
	border: 1px solid #dcdcdc;
	font-size: 17px;
	text-align: center;
}
</style>
</head>
<body>
	<table style="width: 100%; height: 100%">

		<tr style="height: 44px">

			<td rowspan="3" style="width: 91px; height: 129px;"><img
				src="${ctx}/cloudPlateform/v04/PeopleInformation/showEscortImage?idcardNo=${eyeCheckEscortInfo.idcardNo}&officeId=${eyeCheckEscortInfo.office.id}"
				style="height: 100%; width: 100%;" /></td>

			<td style="width: 80px"><spring:message code="common.name" /></td>

			<td style="width: 113px">${eyeCheckEscortInfo.escortName}</td>
			<td style="width: 80px"><spring:message
					code="common.identityGender" /></td>

			<td style="width: 113px">${eyeCheckEscortInfo.identityGender}</td>
			<td style="width: 80px"><spring:message
					code="common.identityNational" /></td>

			<td style="width: 113px">${eyeCheckEscortInfo.identityNational}</td>
		</tr>

		<tr style="height: 44px; width: 500px">
			<td><spring:message code="common.identityBirth" /></td>
			<td style="width: 156px">${eyeCheckEscortInfo.identityBirth}</td>
			<td><spring:message code="common.escortType" /></td>
			<c:if test="${eyeCheckEscortInfo.escortType=='V'}">
				<td><spring:message code="common.visitor" /></td>
			</c:if>
			<c:if test="${eyeCheckEscortInfo.escortType=='E'}">
				<td><spring:message code="common.escort" /></td>
			</c:if>
			<td><spring:message code="common.endDate" /></td>
			<td style="width: 156px"><fmt:formatDate
					value="${eyeCheckEscortInfo.endDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
		</tr>

		<tr style="height: 44px;">
			<td><spring:message code="common.affiliation" /></td>
			<td>${eyeCheckEscortInfo.office.name}</td>
			<td><spring:message code="common.idcardNo" /></td>
			<td colspan="3">${eyeCheckEscortInfo.idcardNo}</td>
		</tr>
		<tr style="height: 116px; width: 350px">
			<td><spring:message code="common.identityVisa" /></td>

			<td colspan="6">${eyeCheckEscortInfo.identityVisa}</td>
		</tr>
		<tr style="height: 116px; width: 350px">
			<td><spring:message code="common.address" /></td>
			<td colspan="6">${eyeCheckEscortInfo.address}</td>
		</tr>
	</table>


</body>


</html>