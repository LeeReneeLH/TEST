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
		<li><a href="${ctx}/collection/v03/shopOffice/list">门店列表</a></li>
		<li class="active"><a href="#" onclick="javascript:return false;">门店查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="shopOffice" action="${ctx}/collection/v03/shopOffice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
		
		
		<div class="control-group">
			<label class="control-label">商户：</label>
			<div class="controls">
				<form:input path="storeNm" htmlEscape="false"   readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">门店编号：</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="15" class="checkCode" readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">门店名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="required" readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">启用标识：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" value="${fns:getDictLabel(shopOffice.enabledFlag, 'enabled_type', '')} " class="required" readOnly="readOnly"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
	
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		
		
		<div class="control-group">
			<label class="control-label">负责人：</label>
			<div class="controls">
				<form:input path="master" htmlEscape="false" maxlength="10" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="15" class="simplePhone" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">传真：</label>
			<div class="controls">
				<form:input path="fax" htmlEscape="false" maxlength="15" class="simplePhone" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" class="email" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮政编码：</label>
			<div class="controls">
				<form:input path="zipCode" htmlEscape="false" maxlength="6" class="zipCode" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系地址：</label>
			<div class="controls">
				<form:textarea path="address" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge" readOnly="readOnly"/>
			</div>
		</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返回" onclick = "window.location.href ='${ctx}/collection/v03/shopOffice/back'"/>
		</div>
	</form:form>
</body>
</html>