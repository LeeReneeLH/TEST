<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 隐藏详细
			toggle();
			$("#name").focus();
			//if ('${user.id}' == '') {
			//	$("#loginNameDiv").hide();
			//} else {
			//	$("#loginName").attr("disabled", "true");
			//	$("#loginNameDiv").show();
			//}
			$.validator.addMethod("notStartWithZero", function ( value, element, param ) {
				var reg = /^([1-9][0-9]*)$/;
				return reg.test(value);
			}, "请输入不以0开头的数字！"
			);
			$.validator.addMethod("userNameValidate", function ( value, element ) {
				return this.optional(element) || /^[a-zA-Z0-9]{2,19}$/.test(value);
			},"3-20位字母或数字"
			);
			$.validator.addMethod("notFullNumber", function ( value, element, param ) {
				if ($("#updateLoginNameCheckbox").attr("checked") == "checked" && (/^\d+$/.test( value ))) { 
					return false;
				} 
				return true;
			}, "修改登陆名时，不能全为数字!"
			);
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')},
					//userFaceId: {remote: "${ctx}/sys/user/checkFaceId?oldFaceId=" + encodeURIComponent('${user.userFaceId}')},
					idcardNo: {remote: {url:"${ctx}/store/v01/stoEscortInfo/checkIdcardNo?id=" + encodeURIComponent('${user.id}')+"&oldIdcardNo=" + encodeURIComponent('${user.idcardNo}'),cache:false}}
					//userFaceId : {
					//	notStartWithZero : true
					//}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					//userFaceId: {remote: "用户脸部识别ID已存在，或登记数量超出上限"},
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
			
			// 审批全选
            $("#updateLoginNameCheckbox").click(function () {
            	
            	if ($(this).attr("checked") == "checked") {
            		$(this).attr("checked", true);
            		$("#loginName").removeAttr("disabled");
            	} else {
            		$(this).attr("checked", false);
            		$("#loginName").val($("#oldLoginName").val());
            		$("#loginName").attr("disabled", "true");
            	}
            	
            });
			
			// 用户类型改变
			$("#userType").change(function(){
				setCheck();
			});
			// 初始化验证
			setCheck();
		});
		
		// 判断用户类型是否需要身份证号
		/* function setCheck(){
			var value = $("#userType").val();
			var type = '${fns:getConfig("need.idcardNo.userType")}';
			if(type.indexOf(value)>=0){
				$("#idcardNo").attr("required",true);
			}else{
				$("#idcardNo").removeAttr("required");
			}
		} */
		// 判断用户类型是否需要身份证号(清机人员必填)
		function setCheck(){
			var value = $("#userType").val();
			var type = '${fns:getConfig("need.idcardNo.userType")}';
			if(type.indexOf(value)==72){
				$("#idcardNo").attr("required",true);
			}else{
				$("#idcardNo").removeAttr("required");
			}
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/user/list">用户列表</a></li>
		<li class="active"><a href="${ctx}/sys/user/form?id=${user.id}">用户<shiro:hasPermission name="sys:user:edit">${not empty user.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:user:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/user/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">归属机构：</label>
			<div class="controls">
                <sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
					title="机构" url="/sys/office/treeData" cssClass="required" checkGroupOffice="true"/>
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
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${user.loginName}">
				<form:input path="loginName" htmlEscape="false" maxlength="15" class="required userNameValidate" />
				<span class="help-inline"><font color="red">*</font> </span>
				<!-- 更新登陆用户名 -->
				<!-- <input id="updateLoginNameCheckbox" type="checkbox" />&nbsp;&nbsp;<spring:message code="common.isUpdate" />  -->
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码：</label>
			<div class="controls">
				<input id="newPassword" name="newPassword" type="password" value="" maxlength="15" minlength="3" class="${empty user.id?'required':''}"/>
				<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
				<c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">确认密码：</label>
			<div class="controls">
				<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="15" minlength="3" equalTo="#newPassword"/>
				<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">岗位职务：</label>
			<div class="controls">
				<form:select path="userType" class="input-large required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls">
				<form:input path="idcardNo" htmlEscape="false" maxlength="18" class="card" readOnly="${empty user.id?'':'readOnly'}"/>
				<!-- <span class="help-inline"><font color="red">*</font> </span> -->
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">角色权限：</label>
			<div class="controls">
				<form:select path="roleIdList" items="${allRoles}" itemLabel="name" itemValue="id" htmlEscape="false" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%--<div class="control-group">
			<label class="control-label">硬件设备权限：</label>
			<div class="controls">
				<form:select multiple="multiple" path="csPermlist" items="${fns:getDictList('cs_permission')}" itemLabel="label" itemValue="value" htmlEscape="false" class="input-xxlarge"/>
			</div>
		</div>--%>
		<%--<div class="control-group">
			<label class="control-label">脸部识别ID：</label>
			<div class="controls">
				&lt;%&ndash; <input id="oldFaceId" name="oldFaceId" type="hidden" value="${user.userFaceId}"> &ndash;%&gt;
				<form:input path="userFaceId" htmlEscape="false" maxlength="8" readOnly="readOnly" />
				<c:if test="${fn:length(fn:trim(user.userFaceId)) == 0 }">
					&lt;%&ndash; 是否生成 &ndash;%&gt;
					<form:checkbox path="initFaceIdFlag" value="1"/>&nbsp;&nbsp;<spring:message code="common.isMake" />
				</c:if>
				<span class="help-inline"><font color="red"><spring:message code="message.E9007" /></font></span>
			</div>
		</div>--%>
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		<div class="control-group">
			<label class="control-label">工号：</label>
			<div class="controls">
				<form:input path="no" htmlEscape="false" maxlength="15" class="alnum"/>
			</div>
		</div>
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
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" class="input-xxlarge"/>
			</div>
		</div>
		</div>
		<c:if test="${not empty user.id}">
			<div class="control-group">
				<label class="control-label">创建时间：</label>
				<div class="controls">
					<label class="lbl"><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">最后登录：</label>
				<div class="controls">
					<label class="lbl">IP: ${user.loginIp}&nbsp;&nbsp;&nbsp;&nbsp;时间：<fmt:formatDate value="${user.loginDate}" type="both" dateStyle="full"/></label>
				</div>
			</div>
		</c:if>
		<div class="form-actions">
			<shiro:hasPermission name="sys:user:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/user/back'"/>
		</div>
	</form:form>
</body>
</html>