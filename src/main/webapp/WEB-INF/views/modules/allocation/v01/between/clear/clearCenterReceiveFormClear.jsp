<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 清分接收登记 --%>
	<title><spring:message code="allocation.clearConfirm.register" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
			// 保存
			$("#btnSubmit").click(function(){
				$("#inputForm").attr("action", "${ctx}/allocation/v01/cashBetweenClear/saveConfirmInfo?strUpdateDate="+ $("#strUpdateDate").val());
				$("#inputForm").submit();
				return;
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 现金调拨列表 --%>
		<li><a href="${ctx}/allocation/v01/cashBetweenClear/list"><spring:message code="allocation.cash.list"/></a></li>
		<%-- 清分接收登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.clearConfirm.register" /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="allAllocateInfo" action="" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<form:hidden path="allId"/>
		<%-- 流水单号 --%>
		<div class="control-group" style="margin-top: 10px;">
			<label class="control-label" style="width:80px;"><spring:message code="allocation.allId" />：</label>
			<div class="controls" style="margin-left:90px;">
				<form:input id="allId" path="allId" style="width:170px;" htmlEscape="false" class="required" disabled="true"/>
			</div>
	    </div>
	    <%-- 业务类型 --%>
		<div class="control-group" style="margin-top: 10px;">
			<label class="control-label" style="width:80px;"><spring:message code="clear.task.business.type" />：</label>
			<div class="controls" style="margin-left:90px;">
				<input id="businessType" value="${fns:getDictLabel(allAllocateInfo.businessType, 'all_businessType', '')}" 
				type="text" style="width:170px;" htmlEscape="false" class="required" disabled="true"/>
			</div>
	    </div>
	    <%-- 登记机构 --%>
		<div class="control-group" style="margin-top: 10px;">
			<label class="control-label" style="width:80px;"><spring:message code="allocation.register.office" />：</label>
			<div class="controls" style="margin-left:90px;">
				<form:input id="name" path="rOffice.name" style="width:170px;" htmlEscape="false" class="required" disabled="true"/>
			</div>
	    </div>
	     <%-- 登记时间 --%>
		<div class="control-group" style="margin-top: 10px;">
			<label class="control-label" style="width:80px;"><spring:message code="allocation.register.date" />：</label>
			<div class="controls" style="margin-left:90px;">
				<input id="createDate" value="<fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" 
				type="text" style="width:170px;" htmlEscape="false" class="required" disabled="true"/>
			</div>
	    </div>
	     <%-- 接收金额 --%>
		<div class="control-group" style="margin-top: 10px;">
			<label class="control-label" style="width:80px;"><spring:message code="allocation.confirm.amount" />：</label>
			<div class="controls" style="margin-left:90px;">
				<form:input id="confirmAmount" path="confirmAmount" style="width:170px;" htmlEscape="false" maxlength="15" class="required money" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
	    </div>
	</form:form>
	<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${allAllocateInfo.strUpdateDate}">
	<div class="form-actions" style="padding-left: 25px">
		<%-- 保存 --%>
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.commit'/>" />
		&nbsp;
		<%-- 返回 --%>
		<input id="btnCancel" class="btn" type="button" 
			value="<spring:message code='common.return'/>" 
			onclick="window.location.href='${ctx}/allocation/v01/cashBetweenClear/back'"/>
	</div>
</body>
</html>
