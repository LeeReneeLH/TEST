<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate(
				{
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
<div class="row" style="margin-top:15px;height: 650">
	<div class="span12" style="margin-top:15px">
		<form:form id="inputForm" modelAttribute="updateGoodsItem" action="" method="post" class="form-horizontal">
			<form:hidden path="goodsId" id="goodsId"/>
			<div class="control-group item">
				<label class="control-label" style="width: 120px;">物品名称：</label>
				<div class="controls" style="margin-left: 130px;">
					<form:input path="goodsName" id="goodsName" readonly="true" style="width:300px;"/>
				</div>
			</div>
			<div class="control-group item">
				<label class="control-label" style="width: 120px;"><spring:message
						code="common.number" />：</label>
				<div class="controls" style="margin-left: 130px;">
					<form:input id="moneyNumber"
						path="moneyNumber" htmlEscape="false"
						maxlength="5" style="width:100px;text-align:right;" class="digits required"  onkeyup="value=value.replace(/[^0-9]/g,'')"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</form:form>
		<input type="hidden" id="checkResult" name="checkResult">
	</div>
</div>

</body>
</html>