<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物品字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#value").focus();
			
			
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
		<li><a href="${ctx}/store/v01/dict">物品字典列表</a></li>
		<li class="active">
			<a href="${ctx}/store/v01/dict/form?id=${stoDict.id}">
				物品字典<shiro:hasPermission name="sto:dict:edit">${not empty stoDict.id?'修改':'添加'}
				</shiro:hasPermission>
				<shiro:lacksPermission name="sto:dict:edit">
				查看
				</shiro:lacksPermission>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoDict" action="${ctx}/store/v01/dict/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">键值：</label>
			<div class="controls">
				<form:input path="value" htmlEscape="false" maxlength="3" class="required digits" disabled="${empty(stoDict.id) == true ? false : true}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标签：</label>
			<div class="controls">
				<form:input path="label" htmlEscape="false" maxlength="12" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型：</label>
			<div class="controls">
				<form:input path="type" htmlEscape="false" maxlength="25" class="required abc" disabled="${empty(stoDict.id) == true ? false : true}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="12" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="11" class="required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计算列：</label>
			<div class="controls">
				<form:input path="unitVal" htmlEscape="false" maxlength="9" max="999999.99" min="0" class="number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关联代码：</label>
			<div class="controls">
				<form:input path="refCode" htmlEscape="false" maxlength="10" class="abc"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="80" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sto:dict:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/store/v01/dict/back'"/>
		</div>
	</form:form>
</body>
</html>