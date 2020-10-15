<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>令牌管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		

		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/sysOtp/"><spring:message code="sys.otp.list" /></a></li>
		<li class="active"><a href="${ctx}/sys/sysOtp/toSynOtp?id=${sysOtp.id}"><spring:message code="sys.otp.synchronization" /></a></li>
		<shiro:hasPermission name="sys:sysOtp:office"><li><a href="${ctx}/sys/sysOtp/formOffice"><spring:message code="sys.otp.office" /></a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysOtp" action="${ctx}/sys/sysOtp/Synchronous" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.number" />：</label>
			<div class="controls">
				<form:input path="tokenId" htmlEscape="false" maxlength="25" class="input-xmedium" readonly="true" />
			</div>
		</div>
		</br>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.secretKey" />：</label>
			<div class="controls">
				<form:textarea path="authKey" htmlEscape="false" rows="3" maxlength="60" class="input-large abc required" style="ime-mode:active;" readonly="true" />
			</div>
		</div>
		</br>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.dynamicPassword" />：</label>
			<div class="controls">
				<form:input path="command" cssClass="required" htmlEscape="false" maxlength="10" class="input-xmedium"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		</br>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.nextDynamicPassword" />：</label>
			<div class="controls">
				<form:input path="nextcommand" cssClass="required" htmlEscape="false" maxlength="10" class="input-xmedium"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="sys:sysOtp:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="同步"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/sys/sysOtp/back'"/>
		</div>
	</form:form>
</body>
</html>