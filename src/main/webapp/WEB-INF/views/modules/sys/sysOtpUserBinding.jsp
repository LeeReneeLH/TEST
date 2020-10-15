<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>令牌管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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
			var officeId = $("#officeId").val();
			var bindingManNo = $('#bindingManNo').val();
				selectUserByOfficeId(officeId,bindingManNo);
			// 保存
			$("#btnSubmit").click(function() {			
				//页面检查
				var blnChk = chkFrom();
				if (blnChk == false){
					return false;
				}
				$("#inputForm").submit();			
			});

		});	
		
		//页面检查
		function chkFrom() {	
			var bindingManNo=$("#bindingManNo").val(); 
			if(bindingManNo == ""){
				alertx("绑定用户不能为空");
				return false;
			}
			return true;
		}
		
		function officeTreeselectCallBack(v, h, f){
			if (v=="ok"){
			var officeId = $("#officeId").val();
			selectUserByOfficeId(officeId,'');
			}
		}
		
		function selectUserByOfficeId(officeId,bindingManNo){
			$('#bindingManNo').select2("val",'');
			$.ajax({
				url : "${ctx}/sys/sysOtp/selectByOfficeId?officeId=" + officeId,
				type : "POST",
				dataType : "json",
				success : function(serverResult, textStatus) {
					$('#bindingManNo').select2({
						containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
					});
						if(serverResult.length>1){
							if(bindingManNo == null || bindingManNo == undefined || bindingManNo == ''){
								$('#bindingManNo').select2("val",serverResult[0].id);
							}else{
								$('#bindingManNo').select2("val",bindingManNo);
							}
						}
						/* end */
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			}); 
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) 
		{
			//alert(item.label);
			return item.label;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/sysOtp/"><spring:message code="sys.otp.list" /></a></li>
		<li class="active"><a href="${ctx}/sys/sysOtp/toBindingUser?id=${sysOtp.id}"><spring:message code="sys.otp.binding" /></a></li>
		<shiro:hasPermission name="sys:sysOtp:office"><li><a href="${ctx}/sys/sysOtp/formOffice"><spring:message code="sys.otp.office" /></a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysOtp" action="${ctx}/sys/sysOtp/binding" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.number" />：</label>
			<div class="controls">
				<form:input path="tokenId" htmlEscape="false" maxlength="25" class="input-xmedium" readonly="true" />
			</div>
		</div>
		</br>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.secretKey" />：</label>
			<div class="controls">
				<form:textarea path="authKey" htmlEscape="false" rows="3" maxlength="60" class="input-large abc required" style="ime-mode:active;" readonly="true" />
			</div>
		</div>
		</br>
		<div class="control-group" id="officeGroup">
			<label class="control-label"><spring:message code="sys.otp.bindingOffice" />：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id"
					value="${sysOtp.office.id}" labelName="office.name"
					labelValue="${sysOtp.office.name}" title="机构"
					url="/sys/office/treeData" notAllowSelectRoot="false"
					notAllowSelectParent="false" cssClass="required" checkGroupOffice="true" />
			<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		</br>		
		<!-- 绑定用户 -->
		<div class="control-group ">
			<!--  绑定用户-->
			<label class="control-label"><spring:message code="sys.otp.bindingUser" />：</label>
			<div class="controls">
				<form:input id="bindingManNo" type="hidden" path="bindingManNo" style="width:210px;" cssClass="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		</br>
		<div class="control-group">
			<label class="control-label"><spring:message code="sys.otp.dynamicPassword" />：</label>
			<div class="controls">
				<form:input path="command" cssClass="required" htmlEscape="false" maxlength="10" class="input-xmedium"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:sysOtp:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="绑定"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/sys/sysOtp/back'"/>
		</div>
	</form:form>
</body>
</html>