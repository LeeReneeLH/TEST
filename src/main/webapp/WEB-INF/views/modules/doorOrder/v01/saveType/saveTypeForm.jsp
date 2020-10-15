<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	 $(document).ready(function() {		
			$("#inputForm").validate({
				rules: {
					typeCode: {remote: {url:"${ctx}/doorOrder/v01/saveType/checkSaveType?id=" + $("#id").val(), cache:false}},
				},
				messages: {
					typeCode: {remote: "存款类型已存在"},
				},
				submitHandler: function(form){
					//提交按钮失效，防止重复提交
		        	$("#btnSubmit").attr("disabled", true);
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
	</script>
</head>
<body> <!-- Tab页 -->
	<ul class="nav nav-tabs">
		<li>
			<a href="${ctx}/doorOrder/v01/saveType/">
				<spring:message code="door.saveType"/><spring:message code="common.list"/>
			</a>
		</li>
		<li class="active">
			<a href="${ctx}/doorOrder/v01/saveType/form?id=${saveType.id}">
				<shiro:hasPermission name="doorOrder:saveType:edit">
					<c:choose>
						<c:when test="${not empty saveType.id}">
							<spring:message code="door.saveType"/><spring:message code="common.modify"/>
						</c:when>
						<c:otherwise>
							<spring:message code="door.saveType"/><spring:message code="common.add"/>
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
				<shiro:lacksPermission name="doorOrder:saveType:edit">
					<spring:message code="store.userInfo" /><spring:message code="common.view" />
				</shiro:lacksPermission>
			</a>
		</li>
	</ul><br/>
		<!-- 输入项 -->
		 <form:form id="inputForm" modelAttribute="saveType" 
		action="${ctx}/doorOrder/v01/saveType/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		
		<%-- 商户名称 --%>
		<div class="control-group" >
		<label class="control-label" style="width:120px;"><spring:message code="door.saveType.clientName" />：</label>&nbsp;&nbsp;
				<sys:treeselect id="merchantName" name="merchantId"
				value="${saveType.merchantId}" labelName="merchantName"
				labelValue="${saveType.merchantName}" title="商户名称"
				url="/sys/office/treeData"  allowClear="true"  type="9"
				cssClass="required input-medium" isAll="true"  clearCenterFilter="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
		</div>
		
		<!-- 存款类型代码 -->
		<div class="control-group">
			<label class="control-label" style="width:120px;">
			<spring:message code="door.saveType.typeCode" />：</label>
			<div class="controls" style="margin-left:130px;" >
				<form:input path="typeCode" id="typeCode"  htmlEscape="false" 
					maxlength="20" class="required" style="width:213px;"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>	
		</div>
		
		<!-- 存款类型名称 -->
		  <div class="control-group">
			<label class="control-label" style="width:120px;">
			<spring:message code="door.saveType.typeName" />：</label>
			<div class="controls" style="margin-left:130px;" >
				<form:input path="typeName" id="typeName"  htmlEscape="false" 
					maxlength="20" class="required" style="width:213px;"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<!-- 按钮 -->
		<div>	<div class="form-actions">
				<shiro:hasPermission name="doorOrder:saveType:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.save'/>"/>&nbsp;	</shiro:hasPermission>
				<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>"
			 onclick = "window.location.href ='${ctx}/doorOrder/v01/saveType/back'"/>
			</div>
		</div>
	</form:form>  
</body>
</html>
