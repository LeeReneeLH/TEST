<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户日结管理</title>
	<meta name="decorator" content="default"/>
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/">商户日结列表</a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/form?id=${dayReportDoorMerchan.id}">账务日结列表<shiro:hasPermission name="doorOrder:v01:dayReportDoorMerchan:edit">${not empty dayReportDoorMerchan.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="doorOrder:v01:dayReportDoorMerchan:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="dayReportDoorMerchan" action="${ctx}/doorOrder/v01/dayReportDoorMerchan/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">日结ID：</label>
			<div class="controls">
				<form:input path="reportId" htmlEscape="false" maxlength="64" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商户ID：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${dayReportDoorMerchan.office.id}" labelName="office.name" labelValue="${dayReportDoorMerchan.office.name}"
					title="部门" url="/sys/office/treeData?type=2" cssClass="" allowClear="true" notAllowSelectParent="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商户名称：</label>
			<div class="controls">
				<form:input path="officeName" htmlEscape="false" maxlength="64" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商户结算类型（74-存款汇总，79-存款差错）：</label>
			<div class="controls">
				<form:input path="settlementType" htmlEscape="false" maxlength="2" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">借入金额：</label>
			<div class="controls">
				<form:input path="inAmount" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">贷出金额：</label>
			<div class="controls">
				<form:input path="outAmount" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">总金额：</label>
			<div class="controls">
				<form:input path="totalAmount" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态（0：已代付；1-未代付）：</label>
			<div class="controls">
				<form:input path="paidStatus" htmlEscape="false" maxlength="1" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">日结时间：</label>
			<div class="controls">
				<input name="reportDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${dayReportDoorMerchan.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">账务类型：</label>
			<div class="controls">
				<form:input path="accountType" htmlEscape="false" maxlength="1" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="doorOrder:v01:dayReportDoorMerchan:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/doorOrder/v01/dayReportDoorMerchan/back'"/>
		</div>
	</form:form>
</body>
</html>