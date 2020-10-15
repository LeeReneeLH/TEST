<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="door.equipment.equipBind" /></title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
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
			
			$("#btnSubmit").click(function(){
				$("#inputForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/equipmentInfo/"><spring:message code="door.equipment.list" /></a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/equipmentInfo/form?id=${equipmentInfo.id}"><spring:message code="door.equipment.equipBind" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="equipmentInfo" action="${ctx}/doorOrder/v01/equipmentInfo/bind" method="post" class="form-horizontal">
		<sys:message type="${errorType}" content="${message}"/>	
		<form:hidden path="id"/>
		<div class="control-group">
			<%-- 门店绑定 --%>
			<label class="control-label"><spring:message code="door.equipment.doorBind" />：</label>
			<div class="controls">
				<sys:treeselect id="aOffice" name="aOffice.id"
					value="${equipmentInfo.aOffice.id}" labelName="aOffice.name"
					labelValue="${equipmentInfo.aOffice.name}" title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData" cssClass="required input-xsmall"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9" 
				    isAll="true" allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<%-- 操作 --%>
			<shiro:hasPermission name="doorOrder:equipmentInfo:bind">
			<input id="btnSubmit" class="btn btn-primary" type="button"  value="<spring:message code='common.confirm'/>"/>&nbsp;
			</shiro:hasPermission>
			
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/doorOrder/v01/equipmentInfo/back'"/>
		</div>
	</form:form>
</body>
</html>