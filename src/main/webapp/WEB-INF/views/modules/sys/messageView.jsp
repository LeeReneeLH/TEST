<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/message/list"><spring:message code='sys.message.list' /></a></li>
		<c:if test="${fns:getUser().userType!='15' && fns:getUser().userType!='16'}">
		<shiro:hasPermission name="sys:message:edit"><!-- 2020/04/14 hzy 消息页面添加修改权限  -->
		<li><a href="${ctx}/sys/message/form"><spring:message code='sys.message.form' /></a></li>
		</shiro:hasPermission>
		</c:if>
		<li class="active"><a href="#"><spring:message code='sys.message.view' /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="message" action="${ctx}/sys/message/save" method="post" class="form-horizontal">
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.topic' />：</label>
			<div class="controls">
				<form:input path="messageTopic" htmlEscape="false" maxlength="30" class="input-xxlarge required" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.context' />：</label>
			<div class="controls">
				<form:textarea path="messageBody" htmlEscape="false" rows="5" maxlength="80" class="input-xxlarge required" readonly="true"/>
			</div>
		</div>
		<c:if test="${message.delFlag=='1'}">
			<div class="control-group">
				<label class="control-label"><spring:message code='sys.message.cancelReason' />：</label>
				<div class="controls">
					<form:textarea path="cancelReason" htmlEscape="false" rows="5" maxlength="80" class="input-xxlarge required" readonly="true"/>
				</div>
			</div>
		</c:if>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/message/back'"/>
		</div>
	</form:form>
</body>
</html>