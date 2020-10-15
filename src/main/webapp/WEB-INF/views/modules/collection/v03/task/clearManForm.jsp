<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%> 

<meta name="renderer" content="webkit"><meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10" />
<meta http-equiv="Expires" content="0"><meta http-equiv="Cache-Control" content="no-cache"><meta http-equiv="Cache-Control" content="no-store">
<script src="${ctxStatic}/jquery-select2/3.4/select2.min.js" type="text/javascript"></script>
<link href="${ctxStatic}/common/jeesite.min.css" type="text/css" rel="stylesheet" />
<script src="${ctxStatic}/common/jeesite.min.js" type="text/javascript"></script>
<script type="text/javascript">var ctx = '${ctx}', ctxStatic='${ctxStatic}';</script>

<link href="${ctxStatic}/common/them.css" type="text/css" rel="stylesheet" />
 <!--[if lt IE 9]> 
<link href="${ctxStatic}/common/them-ie8.css" type="text/css" rel="stylesheet" />
 <![endif]-->
 <!--[if IE 8]> 
<link href="${ctxStatic}/common/them-ie8.css" type="text/css" rel="stylesheet" />
 <![endif]-->

<html>
<head>
	<title></title>

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
<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
<div class="row" style="margin-top:15px;height: 650">
	<div class="span12" style="margin-top:15px;position:relative; z-index:9999;">
		<form:form id="inputForm" modelAttribute="clearManInfo" action="" method="post" class="form-horizontal">
			<form:hidden path="orderId" id="orderId"/>
			<div class="control-group item">
				<%-- 清分人员 --%>
				<label class="control-label" style="width: 120px;"><spring:message code="door.taskDown.clearMan" />：</label>
				<div class="controls" style="margin-left: 130px;">
						<form:select path="clearManNo" id="clearManNo" class="input-large required" style="font-size:15px;color:#000000">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getUsersByTypeAndOffice(ConClear,'')}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
						</form:select>
				</div>
			</div>
		</form:form>
		<input type="hidden" id="checkResult" name="checkResult">
	</div>
</div>

</body>
</html>