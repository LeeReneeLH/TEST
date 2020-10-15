<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>令牌管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					tokenId: {remote:{ url:"${ctx}/sys/sysOtp/checkTokenId",data:{tokenId:function(){return $("#tokenId").val();},oldTokenId:function(){return '${sysOtp.tokenId}';}}}},
				},
				messages: {
					tokenId: {remote: "该令牌号已存在！"},
				},
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
		<li class="active"><a href="${ctx}/sys/sysOtp/form?id=${sysOtp.id}">令牌<shiro:hasPermission name="sys:sysOtp:edit">${not empty sysOtp.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:sysOtp:edit">查看</shiro:lacksPermission></a></li>
		<shiro:hasPermission name="sys:sysOtp:office"><li><a href="${ctx}/sys/sysOtp/formOffice"><spring:message code="sys.otp.office" /></a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysOtp" action="${ctx}/sys/sysOtp/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.number" />：</label>
			<div class="controls">
				<form:input path="tokenId" htmlEscape="false" maxlength="25" class="input-xmedium required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.secretKey" />：</label>
			<div class="controls">
				<form:textarea path="authKey" htmlEscape="false" rows="3" maxlength="60" class="input-large abc required" style="ime-mode:active;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="sys:sysOtp:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/sys/sysOtp/back'"/>
		</div>
	</form:form>
</body>
</html>