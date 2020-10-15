<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='title.atm.addplan.bind.add.cash.group'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			var index = $("#addCashGroupId option:selected").index();
			showAddCashGrouInfo(index);
			//setToday(".createDate");		
		});
		
		/* var groupList = ${sto:getStoAddCashGroupList()}; */
		
		function showAddCashGrouInfo(value){
			var carName = $("#carName"+value).html();
			var escortName1 = $("#escortName1"+value).html();
			var escortName2 = $("#escortName2"+value).html();
			$("#scarName").html(carName);
			$("#sescortName1").html(escortName1);
			$("#sescortName2").html(escortName2);
		}
	</script>
	<style type="text/css">
	table,th,tr,td{border:1px solid #000; border-collapse:collapse;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/atm/v01/atmPlanInfo/importFile"><spring:message code='label.atm.addPlan.menu.01'/></a></li>
		<li ><a href="${ctx}/atm/v01/atmPlanInfo/list"><spring:message code='label.atm.addPlan.menu.02'/></a></li>
		<li class="active"><a href="###"><spring:message code='label.atm.addPlan.menu.04'/></a></li>
	</ul>
	<sys:message content="${message}"/>
	<form:form id="bindingAddCashGroupForm" modelAttribute="atmPlanInfo" action="${ctx}/atm/v01/atmPlanInfo/bindingAddCashGroup" method="post" class="breadcrumb form-search">
		<!-- 打印 -->
		<label style="vertical-align:middle;margin-top:5%;font-size:18px;"><%-- <spring:message
					code="store.boxType" /> --%>加钞组 :</label>
					<c:forEach var="addCashGroup" items="${sto:getStoAddCashGroupList()}" varStatus="status">
						<div style="display:none;">
							<div id="carName${status.index}" style="display:none;">${addCashGroup.carName }</div>
							<div id="escortName1${status.index}" style="display:none;">${addCashGroup.escortName1 }</div>
							<div id="escortName2${status.index}" style="display:none;">${addCashGroup.escortName2 }</div></div></c:forEach>
			<div class="controls">
				<form:select path="addCashGroupId" id="addCashGroupId" class="input-large required" onChange="showAddCashGrouInfo(this.options.selectedIndex)">
					<c:forEach var="addCashGroup" items="${sto:getStoAddCashGroupList()}" varStatus="status">	
						<form:option value="${addCashGroup.id}">
							${addCashGroup.groupName }
						</form:option>
					</c:forEach>
				</form:select>
			</div>
		<form:input id="addPlanId" path="addPlanId" readonly="true" type="hidden"/>
		<input id="saveBtn" type="submit" class="btn btn-primary" value="<spring:message code='common.save'/>" style="margin-left:8px"/>
	</form:form>

	<div style="float:left;margin-top:24px;margin-right:24px;">
		<table id="showacg" width="180" style="border:0px">
			<tbody style="border:0px">
				<!-- 押运车辆 -->
				<tr style="border:0px">
					<td style="border:0px"><spring:message code='store.escortCar'/>:</td>
					<td id="scarName" style="border:0px"></td>
				</tr>
				<!-- 押运人1 -->
				<tr style="border:0px">
					<td style="border:0px"><spring:message code='store.escortUser1'/>:</td>
					<td id="sescortName1" style="border:0px"></td>
				</tr>
				<!-- 押运人2 -->
				<tr style="border:0px">
					<td style="border:0px"><spring:message code='store.escortUser2'/>:</td>
					<td id="sescortName2" style="border:0px"></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="printDiv1" style="margin">
		<jsp:include page="addPlanPrint.jsp"></jsp:include>
	</div>
</body>
</html>