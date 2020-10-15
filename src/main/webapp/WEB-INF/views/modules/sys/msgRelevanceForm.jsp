<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息配置管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			if('${pageType}'=='form'){
				$("#inputForm").attr("action","${ctx}/sys/messageRelevance/save?pageType=${pageType}&messageType=${msgType}")
			}
			$("#inputForm").validate();
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/messageRelevance/list"><spring:message code='sys.message.relevance.list' /></a></li>
		<li class="active"><a href="#"><spring:message code='sys.message.relevance' />${not empty message.messageType?'编辑':'添加'}</a></li>
	</ul><br/>
	<sys:message content="${warning}"/>
	<form:form id="inputForm" modelAttribute="message" action="${ctx}/sys/messageRelevance/save?pageType=${pageType}" method="post" class="form-horizontal">		
		<c:if test="${pageType!='form'}">
			<div class="control-group" id="typeSelect">
				<label class="control-label"><spring:message code='sys.message.relevance.type' />：</label>
				<div class="controls">
					<sys:messageTypeSelect/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.name' />：</label>
			<div class="controls">
				<form:input id="messageTypeName" path="messageTypeName" htmlEscape="false" class="required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.construct' />：</label>
			<div class="controls">
				<form:input id="messageConstruct" path="messageConstruct" htmlEscape="false"  />
				<span class="help-inline"><font color="red">参数部分使用{0}{1}{2}...替换</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.menu' />：</label>
			<div class="controls">
				<sys:treeselect id="menuSelect" name="menuId" value="${menu.id}" labelName="name" labelValue="${menu.name}" 
				title="菜单" url="/sys/menu/treeData?isShowHide=0" cssClass="input-small" allowClear="true" type="4" notAllowSelectParent="true"/>
			</div>
		</div>
		<c:if test="${pageType=='form'}">
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.url' />：</label>
			<div class="controls">
				<form:input id="url" path="url" htmlEscape="false" readonly="true" class="input-xlarge"/>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.role' />：</label>
			<div class="controls">
				<form:select path="userAuthorityList" items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.message.relevance.defaultOffice' />：</label>
			<div class="controls">
				<form:select id="officeAuthorityList" path="officeAuthorityList" items="${fns:getOfficeList()}" itemLabel="name" itemValue="id" htmlEscape="false" class="input-xxlarge"/>
			</div>
		</div>

		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/messageRelevance/back'"/>
		</div>
	</form:form>
</body>
</html>