<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">

	$(document).ready(function() {
		// 隐藏详细
		toggle();
		//$("#name").focus();
		$.validator.addMethod("notStartWithZero", function ( value, element, param ) {
			var reg = /^([1-9][0-9]*)$/;
			return reg.test(value);
		}, "请输入不以0开头的数字！"
		);
		$.validator.addMethod("userNameValidate", function ( value, element ) {
			return this.optional(element) || /^[a-zA-Z0-9]{2,19}$/.test(value);
		},"3-20位字母或数字"
		);
		$("#inputForm").validate({
			rules: {
				loginName: {remote: "${ctx}/collection/v03/custUser/checkLoginName?oldLoginName=" + encodeURIComponent('${custUser.loginName}')},
				idcardNo: {remote: {url:"${ctx}/collection/v03/custUser/checkIdcardNo?id=" + encodeURIComponent('${custUser.id}')+"&oldIdcardNo=" + encodeURIComponent('${custUser.idcardNo}'),cache:false}}
			},
			messages: {
				loginName: {remote: "用户登录名已存在"},
				idcardNo: {remote: "身份证号输入有误，或与他人信息重复"},
				confirmNewPassword: {equalTo: "输入与上面相同的密码"}
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
		<li><a href="${ctx}/collection/v03/custUser/list">客户列表</a></li>
		<li class="active"><a href="${ctx}/collection/v03/custUser/form?id=${custUser.id}">客户<shiro:hasPermission name="collection:custUser:edit">${not empty custUser.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="collection:custUser:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="custUser" action="${ctx}/collection/v03/custUser/save" method="post" class="form-horizontal">
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
			<label class="control-label">姓名：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="10" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" id="loginNameDiv">
			<label class="control-label">登录名：</label>
			<div class="controls">
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${custUser.loginName}">
				<form:input path="loginName" htmlEscape="false" maxlength="15" class="required userNameValidate" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">密码：</label>
			<div class="controls">
				<input id="newPassword" name="newPassword" type="password" value="" maxlength="15" minlength="3" class="${empty custUser.id?'required':''}"/>
				<c:if test="${empty custUser.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
				<c:if test="${not empty custUser.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">确认密码：</label>
			<div class="controls">
				<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="15" minlength="3" equalTo="#newPassword"/>
				<c:if test="${empty custUser.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">客户类别：</label>
			<div class="controls">
				<form:select path="userType" class="input-large required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('cust_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls">
				<form:input path="idcardNo" htmlEscape="false" maxlength="18" class="card"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		
	
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		
		

		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="15" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机：</label>
			<div class="controls">
				<form:input path="mobile" htmlEscape="false" maxlength="11" class="mobile"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" class="email"/>
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
			<input id="btnCancel" class="btn" type="button" value="返回" onclick = "window.location.href ='${ctx}/collection/v03/custUser/back'"/>
		</div>
	</form:form>
</body>
</html>