<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
					submitHandler : function(form) {
						loading('正在提交，请稍等...');
						form.submit();
					},
					//errorContainer : "#messageBox",
					errorPlacement : function(error, element) {
						//$("#messageBox").text("输入有误，请先更正。");
						if (element.is(":checkbox")
								|| element.is(":radio")
								|| element.parent().is(
										".input-append")) {
							error.appendTo(element.parent()
									.parent());
						} else {
							error.insertAfter(element);
						}
						$("#checkResult").val("error");
					}
			});
		});
	</script>
</head>
<body>
<div class="row" style="margin-top:15px;height: 350">
	<div class="span12" style="margin-top:15px">
		<form:form id="inputForm" modelAttribute="cancelParam" action="" method="post" class="form-horizontal">
				<form:hidden path="taskType" id="taskType" readonly="true" />
				<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${cancelParam.strUpdateDate }">
				<div class="control-group item">
					<label class="control-label" style="width: 120px;">流水号：</label>
					<form:input  path="allId" id="allId" readonly="true" />
				</div>
				<div class="control-group item">
			<label class="control-label" style="width: 250px;margin-left: 26px">撤回原因（不得少于10个字）：</label>
			</div>
			<div class="control-group item" style="margin-left: 50px">
				<div style="width:550px;">
					<c:choose>
						<c:when test="${cancelParam.displayCancelReasonFlag == true}">
							<form:textarea path="cancelReason" style="width:500px; height :200px;" readonly="true" maxlength="600" minlenght="10"/>
						</c:when>
						<c:otherwise>
							<form:textarea path="cancelReason" style="width:500px; height :200px;" maxlength="600" minlenght="10"/>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group item"style="margin-left: -70px">
				<label class="control-label" style="width: 120px;margin-left: 0px"></label>
				<span id="errorMessage" style="color: red;"></span>
			</div>
			<!-- 修正撤回信息不对齐的bug 修改人：xp 修改时间：2017-11-6 begin -->
			<c:choose>
				<c:when test="${cancelParam.displayCancelReasonFlag == true}">
					<div class="control-group item"style="margin-left: 10px">
						<label style="margin-left: 40px">撤回机构：</label>
						<label style="padding-left: 5px;">${cancelParam.cancelOffice.name}</label>
					</div>
					<div class="control-group item"style="margin-left: 10px">
						<label style="margin-left: 56px">撤回人：</label>
						<label style="padding-left: 5px;">${cancelParam.cancelUserName}</label>
					</div>
					<div class="control-group item"style="margin-left: 10px">
						<label style="margin-left: 40px">撤回日期：</label>
						<label style="padding-left: 5px;"><fmt:formatDate value="${cancelParam.cancelDate}" pattern="yyyy-MM-dd" /></label>
					</div>
				</c:when>
			</c:choose>
			<!-- end -->
		</form:form>
	</div>
</div>

</body>
</html>