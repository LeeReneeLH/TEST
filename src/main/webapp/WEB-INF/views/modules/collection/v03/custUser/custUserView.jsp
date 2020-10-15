<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>门店管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 隐藏详细
			//toggle();

		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/collection/v03/custUser/list">门店列表</a></li>
		<li class="active"><a href="#" onclick="javascript:return false;">门店查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="custUser" action="${ctx}/collection/v03/custUser/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
		
		
		<div class="control-group">
			<label class="control-label">商户：</label>
			<div class="controls">
				<form:input path="storeName" htmlEscape="false"   readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">姓名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="10" readOnly="readOnly" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
	
		<div class="control-group" id="loginNameDiv">
			<label class="control-label">登录名：</label>
			<div class="controls">
				<form:input path="loginName" htmlEscape="false" maxlength="15" readOnly="readOnly" class="required userNameValidate" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">客户类别：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" value="${fns:getDictLabel(custUser.userType, 'cust_user_type', '')} " class="required" readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls">
				<form:input path="idcardNo" htmlEscape="false" maxlength="18" readOnly="readOnly"  class="card"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
	
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		
		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="15" readOnly="readOnly" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机：</label>
			<div class="controls">
				<form:input path="mobile" htmlEscape="false" maxlength="11" readOnly="readOnly" class="mobile"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" readOnly="readOnly" class="email"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" readOnly="readOnly" class="input-xlarge"/>
			</div>
		</div>

		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返回" onclick = "window.location.href ='${ctx}/collection/v03/custUser/back'"/>
		</div>
	</form:form>
</body>
</html>