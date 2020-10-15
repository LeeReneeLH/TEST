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
				setSelectName('clearManNo','clearManName')
				$("#inputForm").submit();
			});
			
			//人员名称的设定
			function setSelectName(valueId, valueNm) {
				$("#" + valueNm).val("");
				var selectValue=$("#" + valueId).val(); 
				if ((selectValue != undefined && selectValue != "")) {
					var selectText = $("#" + valueId).find("option:selected").text();
					$("#" + valueNm).val(selectText);
				}
			}	
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/clearPlanInfo/"><spring:message code="door.clearPlan.list" /></a></li>
		<li class="active">
			<%-- gzd 2020-04-14  修改链接  ${ctx}/doorOrder/v01/clearPlanInfo/detail?id=${clearPlanInfo.id} --%>
			<a>清机任务查看</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clearPlanInfo" action="${ctx}/doorOrder/v01/clearPlanInfo/save" method="post" class="form-horizontal">
		<sys:message content="${message}"/>	
		<form:hidden path="id"/>
			<div class="control-group" style="margin-left: 80px;">
				<!-- 清机人员 -->
				<label><spring:message code='door.clearPlan.clearMan' />：</label>
				<div style="margin-left: 100px; margin-top: -20px">
					<form:select path="clearPlanUserDetailList" items="${sto:getUsersByTypeAndOffice('40',fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" class="input-xxlarge required" disabled="true"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">机具：</label>
				<div class="controls">
					<form:select path="clearPlanInfoList" items="${clearPlanList}" itemLabel="equipmentName" itemValue="id" htmlEscape="false" class="input-xxlarge required" disabled="true"/>
				</div>
			</div>
		
		<div class="form-actions" style="clear:both;width:100%;">
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
				onclick="history.go(-1)" />
				<%-- onclick="window.location.href='${ctx}/doorOrder/v01/clearPlanInfo/back'" /> --%>
		</div>
	</form:form>
</body>
</html>