<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	
	</script>
</head>
<body>
	<!-- Tab页 -->
	<ul class="nav nav-tabs">
		<li>
			<a href="${ctx}/doorOrder/v01/equipmentWarnings/list">
				机具报警列表
			</a>
		</li>
		<li class="active">
		<a href=" " onclick="javascript:return false;">
				机具查看列表
			</a>
		</li>	
	</ul><br/>
	
	<!-- 输入项 -->
	<form:form id="inputForm" modelAttribute="equipmentInfo" 
		action="${ctx}/doorOrder/v01/equipmentWarnings/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<!-- 机具编号 -->
		<div class="control-group">
			<label class="control-label">机具编号：</label>
			<div class="controls">
				<form:input path="id" htmlEscape="false" 
					readOnly="true" maxlength="20" class="required" />
					<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<!-- 机具名称 -->
		<div class="control-group">
			<label class="control-label">机具名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" 
					readOnly="true" maxlength="20" class="required" />
					<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<!-- 所属机构ID -->
		<div class="control-group">
			<label class="control-label">所属机构ID：</label>
			<div class="controls">
				<form:input path="aOffice.id" htmlEscape="false" 
					readOnly="true" maxlength="20" class="required" />
					<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<!-- 所属机构名称 -->
		<div class="control-group">
			<label class="control-label">所属机构名称：</label>
			<div class="controls">
				<form:input path="vinOffice.name" htmlEscape="false" 
					readOnly="true" maxlength="20" class="required" />
					<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<%-- 返回 --%>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>"
			 onclick = "window.location.href ='${ctx}/doorOrder/v01/equipmentWarnings/back'"/>
		</div>
		
		<!-- end -->
	</form:form>
</body>
</html>
