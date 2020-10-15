<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 重空管理标题 -->
	<title><spring:message code="common.importantEmpty.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#detailDiv").load("${ctx}/store/v01/stoEmptyDocument/getList");
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
		
		function addDetail(){
			if (checkNumber()) {
				$.post($("#detailForm").attr("action"), $("#detailForm")
						.serialize(), function(data) {
					if (data.errorMessage != null) {
						alert(data.errorMessage);
					}
					if (data.message != null) {
						alert(data.message);
					} 
					$("#id").val('');
					$("#balanceNumber").val('');
					$("#detailDiv").load("${ctx}/store/v01/stoEmptyDocument/getList");
				}, "json");
			}
		}
		
		function checkNumber() {
			if($("#detailForm").valid()) {
				var endNumber = $("#endNumber").val();
				var startNumber = $("#startNumber").val();
				var createNumber = endNumber - startNumber;
				if(createNumber>=0) {
					$("#createNumber").val(createNumber+1);
					return true;
				} else {
					alert("<spring:message code='message.E1040'/>");
					$("#endNumber").focus();
						return false;
				}
			}
		}
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 重空列表 -->
		<li><a href="${ctx}/store/v01/stoEmptyDocument/"><spring:message code="common.importantEmpty.list"/></a></li>
		<!-- 重空添加或修改 -->
		<li class="active"><a href="#">
			<shiro:hasPermission name="store:stoEmptyDocument:edit">
					<c:choose>
						<c:when test="${not empty stoEmptyDocument.id}">
							<spring:message code="common.importantEmpty"/><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code="common.importantEmpty"/><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
			</shiro:hasPermission>
		<!-- 查看 -->
		<shiro:lacksPermission name="store:stoEmptyDocument:edit"><spring:message code="common.view" /></shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="detailForm" modelAttribute="stoEmptyDocument" action="${ctx}/store/v01/stoEmptyDocument/addDetail" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<sys:blbiSelect type="blankBillselect"/>
		<div class="control-group">
			<!-- 区间值 -->
			<label class="control-label" style="width:120px;"><spring:message code="store.intervalValue"/>：</label>
			<div class="controls" style="margin-left:130px;">
				<form:input path="startNumber" htmlEscape="false" maxlength="10" class="input-medium required digits" style="width:149px;"/>
				-
				<form:input path="endNumber" htmlEscape="false" maxlength="10" class="input-medium required digits" style="width:149px;"/>
			</div>
		</div>
		<div class="control-group">
			<!-- 数量 -->
			<label class="control-label" style="width:120px;"><spring:message code="common.number"/>：</label>
			<div class="controls" style="margin-left:130px;">
				<form:input path="createNumber" htmlEscape="false" onfocus="checkNumber()" readonly="true" class="input-medium" style="width:149px;"/>
				<form:hidden path="balanceNumber"/>
				<!-- 确定 -->
				<input id="btnDetail" class="btn btn-primary" type="button" onclick="addDetail()" value="<spring:message code='common.confirm'/>"/>
			</div>
		</div>
	</form:form>
	<form:form id="inputForm" modelAttribute="stoEmptyDocument" action="${ctx}/store/v01/stoEmptyDocument/save" method="post" class="form-horizontal">
		<div id="detailDiv"
			style="width: 700px; min-height: 150px; margin-top:20px;max-height: 150px; overflow: auto; overflow-x: hidden;">
		</div>
		<div class="form-actions">
			<!-- 保存 -->
			<shiro:hasPermission name="store:stoEmptyDocument:edit">
				<!-- 金库人员权限 -->
				<shiro:hasPermission name="store:stoEmptyDocument:storeEdit">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;
				</shiro:hasPermission>
			</shiro:hasPermission>
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick = "window.location.href ='${ctx}/store/v01/stoEmptyDocument/back'"/>
		</div>
	</form:form>
</body>
</html>
