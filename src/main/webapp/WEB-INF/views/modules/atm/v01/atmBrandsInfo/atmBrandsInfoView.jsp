<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="atm.brands.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/atm/v01/atmBrandsInfo/"><spring:message code="atm.brands.list"/></a></li>
	   	<li class="active"><a href="${ctx}/atm/v01/atmBrandsInfo/view?id=${atmBrandsInfo.id}">
			<spring:message code="atm.brandModel" /><spring:message code="common.view" />
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmBrandsInfo" action="${ctx}/atm/v01/atmBrandsInfo/save" method="post" class="form-horizontal">
	    <!--主键  -->
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!--品牌编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.id"/>：</label>
			<div class="controls">
				<form:input path="atmBrandsNo" htmlEscape="false" maxlength="4" minlength="4" class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--品牌名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.name"/>：</label>
			<div class="controls">
				<form:input path="atmBrandsName" htmlEscape="false" maxlength="15" class="input-large required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--机型编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.model.id"/>：</label>
			<div class="controls">
			    <input type="hidden" id="oldAtmTypeNo" name="oldAtmTypeNo" value="${atmBrandsInfo.atmTypeNo}"/>
				<form:input path="atmTypeNo" htmlEscape="false" maxlength="10" class="input-large required abc" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--机型名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.model.name"/>：</label>
			<div class="controls">
				<form:input path="atmTypeName" htmlEscape="false" maxlength="15" class="input-large required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--总钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.wayNum"/>：</label>
			<div class="controls">
				<form:input path="boxNum" htmlEscape="false" maxlength="1" min="1" class="input-large digits" readonly="true" />
			</div>
		</div>
		<!--取款箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.drawBox.model"/>：</label>
			<div class="controls">
				<form:input path="getBoxType" htmlEscape="false" maxlength="6" boxTypeValid='2'  class="input-large" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--取款钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.drawBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="getBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='2' class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--回收箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.reclaimBox.model"/>：</label>
			<div class="controls">
				<form:input path="backBoxType" htmlEscape="false" maxlength="6" boxTypeValid='1' class="input-large " readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--回收钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.reclaimBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="backBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='1' class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--存款箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.depositBox.model"/>：</label>
			<div class="controls">
				<form:input path="depositBoxType" htmlEscape="false" maxlength="6" boxTypeValid='3'  class="input-large " readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--存款钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.depositBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="depositBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='3' class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--循环箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.recyclingBox.model"/>：</label>
			<div class="controls">
				<form:input path="cycleBoxType" htmlEscape="false" maxlength="6" boxTypeValid='4' class="input-large " readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--循环钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.recyclingBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="cycleBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='4' class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--操作按钮  -->
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/atm/v01/atmBrandsInfo/back'"/>
		</div>
	</form:form>
</body>
</html>
