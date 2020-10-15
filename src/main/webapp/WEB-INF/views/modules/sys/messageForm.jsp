<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			if('${pageType}'=='form'){
				$("#userAuthorityList").attr("readOnly","readOnly");
				$("#officeAuthorityList").attr("readOnly","readOnly");
			}
			$("#inputForm").validate();
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="sys:message:view"><!-- 2020/04/14 hzy 消息页面添加查看权限  -->
			<li><a href="${ctx}/sys/message/list"><spring:message code='sys.message.list' /></a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:message:edit"><!-- 2020/04/14 hzy 消息页面添加修改权限  -->
			<li class="active"><a href="#"><spring:message code='sys.message.form' /></a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="message" action="${ctx}/sys/message/save?pageType=${pageType}" method="post" class="form-horizontal">
		<form:input path="id" htmlEscape="false" type="hidden"/>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.topic' />：</label>
			<div class="controls">
				<form:input path="messageTopic" htmlEscape="false" maxlength="30" class="input-xxlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.context' />：</label>
			<div class="controls">
				<form:textarea path="messageBody" htmlEscape="false" rows="5" maxlength="80" class="input-xxlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.role' />：</label>
			<div class="controls">
				<form:select id="userAuthorityList" path="userAuthorityList" items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.receiveOffice' />：</label>
			<div class="controls">
				<form:select id="officeAuthorityList" path="officeAuthorityList" items="${fns:getOfficeList()}" itemLabel="name" itemValue="id" htmlEscape="false" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="发 送"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/message/back'"/>
		</div>
	</form:form>
</body>
</html>