<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready( function() {
				
				$("#inputForm").validate( {
						rules: {
							clearGroupName: {remote: {url:"${ctx}/doorOrder/v01/clearGroupMain/checkName?clearGroupId=" + $("#clearGroupId").val(), cache:false}},
						},
						messages: {
							clearGroupName: {remote: "该清机组名称已存在"},
						},
						submitHandler : function(form) {
							//提交按钮失效，防止重复提交
							$("#btnSubmit").attr("disabled", true);
							loading('正在提交，请稍等...');
							form.submit();
						},
						errorPlacement : function(error, element) {
							if (element.is(":checkbox")
									|| element.is(":radio")
									|| element.parent().is(
											".input-append")) {
								error.appendTo(element.parent()
										.parent());
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
		<!-- 清机组列表 -->
		<li><a href="${ctx}/doorOrder/v01/clearGroupMain/"><spring:message code='clear.clearGroupMain.list' /></a></li>
		<!-- 清机组添加(修改) -->
		<li class="active">
			<%-- gzd 2020-04-14  修改链接  ${ctx}/doorOrder/v01/clearGroupMain/form?clearGroupId=${clearGroupMain.clearGroupId} --%>
			<a>
				<shiro:hasPermission name="doorOrder:v01:clearGroupMain:edit">
					<c:choose>
						<c:when test="${not empty clearGroupMain.clearGroupId}">
							<spring:message code='clear.clearGroupMain.clearGroup' /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code='clear.clearGroupMain.clearGroup' /><spring:message code="common.add" /> 
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			</a>
		</li>
	</ul>
	<br/>

	<form:form id="inputForm" modelAttribute="clearGroupMain"
		action="${ctx}/doorOrder/v01/clearGroupMain/save" method="post"
		class="form-horizontal">
		<form:hidden path="clearGroupId" />
		<sys:message content="${message}" />
		<!-- 清机组 -->
		<div class="control-group">
			<label class="control-label"><spring:message code='clear.clearGroupMain.name' />：</label>
			<div class="controls">
				<form:input path="clearGroupName" htmlEscape="false"  maxlength="20" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<!-- 清机门店-->
		<div class="control-group">
				<label class="control-label"><spring:message code='clear.clearGroupMain.clearDoor' />：</label>
				<div class="controls">
					<form:select path="doorList" items="${doorsList}" itemLabel="name" itemValue="id" htmlEscape="false" class="input-xxlarge required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		<!-- 清机人员-->
		 <div class="control-group">
			<label class="control-label"><spring:message code='clear.clearGroupMain.clearUser' />：</label>
			<div class="controls">
				<form:select path="userList" items="${sto:getUsersByTypeAndOffice('40',fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span> 
			</div>
		</div>  
		<!-- 保存  返回 -->
		<div class="form-actions">
			<shiro:hasPermission name="doorOrder:v01:clearGroupMain:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.save'/>" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)" />
				<%-- onclick="window.location.href ='${ctx}/doorOrder/v01/clearGroupMain/back'" /> --%>
		</div>
	</form:form>
</body>
</html>