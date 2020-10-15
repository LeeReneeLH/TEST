<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>车辆管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$.validator.addMethod("carNoValidator", function ( value, element, param ) {
				var reg = /^[A-Z]{1}[A-Z0-9]{5}$/;
				return reg.test(value);
			  } , "输入的车牌号格式不正确!"
			);
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					carNo: {remote: {url:"${ctx}/store/v01/stoCarInfo/checkcarNo?carHeader=" + $("#carHeader").val(), cache:false}},
				    carNo: {carNoValidator : true}
				},
				messages: {
					carNo: {remote: "车辆信息已存在"},
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
		<li><a href="${ctx}/store/v01/stoCarInfo/">车辆列表</a></li>
		<li class="active"><a href="${ctx}/store/v01/stoCarInfo/form?id=${stoCarInfo.id}">车辆<shiro:hasPermission name="store:v01:stoCarInfo:edit">${not empty stoCarInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="store:v01:stoCarInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoCarInfo" action="${ctx}/store/v01/stoCarInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">车牌号码：</label>
			<div class="controls">
			<c:set var="flag" value="${pageFlag}"/>
			<c:choose>
			    <c:when test="${flag=='modify'}">
				    <form:input path="carNo" htmlEscape="false" style="width:100px" disabled="true"/>
					<span class="help-inline"><font color="red">*例(辽B12345)</font> </span>
			    </c:when>
			    <c:otherwise>
				    <form:select path="carHeader" id="carHeader">
					    <form:option value="辽" selected="selected">辽</form:option>
				    	<form:options items="${sto:getCarHeaderList()}" />
			        </form:select>
					<form:input id="carNumber" path="carNo" htmlEscape="false" style="width:100px" maxlength="6" class="required carNoValidator" onkeyup="this.value=this.value.toUpperCase()"/>
					<span class="help-inline"><font color="red">*例:(辽B12345)</font> </span>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">车辆颜色：</label>
			<div class="controls">
				<form:input path="carColor" htmlEscape="false" maxlength="13" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">车辆型号：</label>
			<div class="controls">
				<form:input path="carType" htmlEscape="false" maxlength="13" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="80" class="input-xxlarge "/>
				<span class="help-inline"><font color="red">(最多可输入80个字符)</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="store:v01:stoCarInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/store/v01/stoCarInfo/back'"/>
		</div>
	</form:form>
</body>
</html>