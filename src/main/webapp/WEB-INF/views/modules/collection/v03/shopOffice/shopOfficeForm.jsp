<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>门店管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 隐藏详细
			toggle();
			$("#code").focus();
			if ($("input[name='enabledFlag']:checked").val() == undefined || $("input[name='enabledFlag']:checked").val() =="") {
				$("input[value=1]").attr("checked",true);
			}
			
			$("#inputForm").validate({
				rules: {
					code: {remote: {url:"${ctx}/collection/v03/shopOffice/checkCode",data:{code:function(){return $("#code").val();},oldCode:function(){return $("#oldCode").val();}}}}
				},
				messages: {
					code: {remote: "门店编号已经存在"},
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
			$.validator.addMethod("checkCode",function(value,element){  
				            var checkCode = /^[0-9]+$/;  
				            return this.optional(element)||(checkCode.test(value));  
				        },"请输入合法数字");  
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/collection/v03/shopOffice/list">门店列表</a></li>
		<li class="active"><a href="${ctx}/collection/v03/shopOffice/form?id=${shopOffice.id}">门店<shiro:hasPermission name="collection:shopOffice:edit">${not empty shopOffice.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="collection:shopOffice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="shopOffice" action="${ctx}/collection/v03/shopOffice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<%-- 商户--%>
		<div class="control-group">
			<label class="control-label">商户：</label>
			<div class="controls">
				<form:select path="storeId" id="storeId" class="input-large required" style="font-size:15px;color:#000000">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${storeList}" htmlEscape="false" itemLabel="label" itemValue="value"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">门店编号：</label>
			<div class="controls">
				<input id="oldCode" name="oldCode" type="hidden" value="${shopOffice.oldCode}">
				<form:input path="code" htmlEscape="false" maxlength="15" class="checkCode required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">门店名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">启用标识：</label>
			<div class="controls">
				<form:radiobuttons path="enabledFlag" items="${fns:getDictList('enabled_type')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
	
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		
		
		<div class="control-group">
			<label class="control-label">负责人：</label>
			<div class="controls">
				<form:input path="master" htmlEscape="false" maxlength="10"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="15" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">传真：</label>
			<div class="controls">
				<form:input path="fax" htmlEscape="false" maxlength="15" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮政编码：</label>
			<div class="controls">
				<form:input path="zipCode" htmlEscape="false" maxlength="6" class="zipCode"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系地址：</label>
			<div class="controls">
				<form:textarea path="address" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge"/>
			</div>
		</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返回" onclick = "window.location.href ='${ctx}/collection/v03/shopOffice/back'"/>
		</div>
	</form:form>
</body>
</html>