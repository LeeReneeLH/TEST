<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>汇款记录保存管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		
		//明细金额验证
		function detailAmountCheck(){
			if(isNaN($("#paidAmount").val())){
				$("#paidAmount").val(0);
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/paidRecord/">汇款记录列表</a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/paidRecord/form?id=${paidRecord.id}">汇款记录<shiro:hasPermission name="doororder.v01:paidRecord:edit">${not empty paidRecord.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="paidRecord" action="${ctx}/doorOrder/v01/paidRecord/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">受理编号：</label>
			<div class="controls">
				<form:input path="toAcceptTheNumber" htmlEscape="false" maxlength="64" class="number "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">交易流水号：</label>
			<div class="controls">
				<form:input path="tradeSerialNumber" htmlEscape="false" maxlength="64" class="required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">汇款金额：</label>
			<div class="controls">
				<form:input path="paidAmount" htmlEscape="false" class="required number" onkeyup="detailAmountCheck()" onchange="detailAmountCheck()"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商户：</label>
			<div class="controls">
			<sys:treeselect id="merchanOfficeId" name="merchanOfficeId"
				value="${paidRecord.merchanOfficeId}" labelName="merchanOfficeName"
				labelValue="${paidRecord.merchanOfficeName}" title="商户名称"
				url="/sys/office/treeData"  allowClear="true"  type="9"
				cssClass="required input-medium" isAll="true"  clearCenterFilter="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
			
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="doororder.v01:paidRecord:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>