<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.add.cash.group.info" />
			<spring:message code="common.view" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoAddCashGroup/list"><spring:message
					code="store.add.cash.group.list" /></a></li><!-- 加钞组信息列表 -->
		<li class="active"><a
			href="${ctx}/store/v01/stoAddCashGroup/form?id=${stoAddCashGroup.id}">
			<spring:message code="store.add.cash.group.info" /><spring:message code="common.view" /><!-- 加钞组信息 -->
		</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoAddCashGroup"
		action="" method="post"
		class="form-horizontal">
		<!-- 加钞组ID-->
		<sys:message content="${message}" />
		<form:hidden path="id" /><!-- 加钞组ID -->
		<!-- 加钞组名称 -->
		<div class="control-group">
			<label class="control-label"><spring:message
						code="store.add.cash.group.name" />：</label><!-- 加钞组名称 -->
			<div class="controls">
					<form:input path="groupName" id="groupName" htmlEscape="false"
						maxlength="10" class="required" readonly="true" style="font-size:16px"/>
			</div>
		</div>
		
		<!-- 车辆 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code="store.add.cash.group.car" />：</label><!-- 车辆  -->
			<div class="controls">
				<form:select path="carId" id="carId" class="input-large" disabled="true">
					<c:if test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
						<option value="${stoAddCashGroup.carId}">${stoAddCashGroup.carName}</option>
					</c:if>
				</form:select>
				<form:hidden path="carName" />
			</div>
		</div>
		
		<!-- 押运人员1 -->
		<div class="control-group" >
			<label class="control-label"><spring:message code='store.escortUser1'/>：</label><!-- 押运人员1 -->
			<div class="controls">
				<form:select path="escortNo1" id="escortNo1" class="input-large" disabled="true">
					<c:if test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
						<option value="${stoAddCashGroup.escortNo1}">${stoAddCashGroup.escortName1}</option>
					</c:if>
				</form:select>
				<form:hidden path="escortName1"/>
			</div>
		</div>
		
		<!-- 押运人员2 -->
		<div class="control-group" id="atmBoxTypeGroup">
			<label class="control-label"><spring:message code='store.escortUser2'/>：</label><!-- 押运人员2 -->
			<div class="controls">
				<form:select path="escortNo2" id="escortNo2" class="input-large" disabled="true">
					<c:if test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
						<option value="${stoAddCashGroup.escortNo2}">${stoAddCashGroup.escortName2}</option>
					</c:if>
				</form:select>
				<form:hidden path="escortName2"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/stoAddCashGroup/back'"/>
		</div>
	</form:form>
</body>
</html>