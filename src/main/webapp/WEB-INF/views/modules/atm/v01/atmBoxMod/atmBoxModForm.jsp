<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="atm.modelConfigure.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#btnSubmit").click(function(){
                var url = "${ctx}/atm/v01/atmBoxMod/save";
	            $("#inputForm").attr("action", url);
				$("#inputForm").submit();
			});
			
			$("#modName").focus();
		//	$("#atmTypesNo").chained("#atmBrandsNo");
		    getBoxTypeNos();
		    setReadOnly();
		    
			$("#atmBrandsNo").change(function(){
				var atmBrandNo = $(this).val();
				var atmBrandName = $(this).find("option:selected").text();
				if(atmBrandNo != '' && atmBrandNo != null){
					$("#atmBrandsName").val(atmBrandName);
				}
				getBoxTypeNos();
			});
			
		 	$("#boxTypeNos").change(function(){
				var boxTypeNo = $(this).find("option:selected").val();
				var boxType = '';
				if(boxTypeNo != '' && boxTypeNo != null){
					$("#boxTypeNo").val(boxTypeNo);
					boxType = boxTypeNo.substring(4,5);
					$("#boxType").val(boxType);
				}
			}); 
			
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
		
	   function getBoxTypeNos(){
			var atmBrandsNo = $("#atmBrandsNo").val();
			var atmBrandsId = $("#id").val();
			var boxTypeNo = $("#boxTypeNo").val();
			$("#boxTypeNos option[value != '']").remove();
			if(atmBrandsNo != null && atmBrandsNo != ''){
				$.post('${ctx}/atm/v01/atmBoxMod/getBoxTypeNos', {atmBrandsNo:atmBrandsNo,atmBrandsId:atmBrandsId,boxTypeNo:boxTypeNo}, function(data){
					var list = data.boxTypeNos;
					var len = list.length;	
					var boxTypeNo = $("#boxTypeNo").val();
					for(var i=0;i<len;i++){
						var val = list[i];
						var type = val.substring(4,5);
						var textVal = '';
						if(type == "${fns:getConfig('atm.box.boxtype.getbox')}"){
							textVal = "<spring:message code='common.drawBox'/>" +val;
						}else if(type == "${fns:getConfig('atm.box.boxtype.backbox')}"){
							textVal = "<spring:message code='common.reclaimBox'/>" +val;
						}else if(type == "${fns:getConfig('atm.box.boxtype.cyclebox')}"){
							textVal = "<spring:message code='common.recyclingBox'/>" +val;
						}else if(type == "${fns:getConfig('atm.box.boxtype.depositbox')}"){
							textVal = "<spring:message code='common.depositBox'/>" +val;
						}
						if(boxTypeNo != null && boxTypeNo != '' && boxTypeNo == val){
							$("#boxTypeNos").append("<option value='"+val+"' selected='selected'>"+textVal+"</option>");						
						}else{
							$("#boxTypeNos").append("<option value='"+val+"'>"+textVal+"</option>");
						}				
					}
					$("#boxTypeNos").trigger("change");
				},"json");
			}	
		}
	   
	   function setReadOnly() {
			if ($("#id").val()) {
				// 编辑的场合
				$("#atmBrandsNo").attr("readOnly", true);
				$("#boxTypeNos").attr("readOnly", true);
			} else {
				// 新增的场合
				$("#atmBrandsNo").attr("readOnly", false);
				$("#boxTypeNos").attr("readOnly", false);
			}
		}

	</script>

</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/atm/v01/atmBoxMod/"><spring:message code="atm.modelConfigure.list"/></a></li>
	   	<li class="active"><a
			href="${ctx}/atm/v01/atmBoxMod/form?id=${atmBoxMod.id}">
				<shiro:hasPermission name="atm:atmBoxMod:edit">
					<c:choose>
						<c:when test="${not empty atmBoxMod.id}">
							<spring:message code="atm.modelConfigure" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="atm.modelConfigure" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmBoxMod" action="${ctx}/atm/v01/atmBoxMod/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${atmBoxMod.strUpdateDate}">	
		<!-- 钞箱类型名称 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.atmBox.modelName"/>：</label>
			<div class="controls">
				<form:input path="modName" htmlEscape="false" maxlength="10" class="input-large required" style="font-size:16px"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 品牌编号 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.name"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBoxMod.id }">
                       <form:hidden path="atmBrandsNo" htmlEscape="false"  class="input-large " value="${atmBoxMod.atmBrandsNo}" />
                       <form:input path="atmBrandsName" htmlEscape="false" maxlength="15" class="input-large " value="${atmBoxMod.atmBrandsName}" readonly="true" style="font-size:16px"/>
					</c:when>
					<c:otherwise>
						<form:select path="atmBrandsNo" class="input-large required">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${atm:getAtmBrandsinfoList()}"
								itemLabel="atmBrandsName" itemValue="atmBrandsNo"
								htmlEscape="false" />
						</form:select>
						<form:hidden path="atmBrandsName" htmlEscape="false" maxlength="15" class="input-large " value="${atmBoxMod.atmBrandsName}"/>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 钞箱类型编号 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.atmBox.model"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBoxMod.id }">
						<select id="boxTypeNos" name="boxTypeNos" class="input-large required" required="required">
							<option value="">
								<spring:message code="common.select" />
							</option>
						</select>
					</c:when>
					<c:otherwise>
						<select id="boxTypeNos" name="boxTypeNos" class="input-large required" required="required">
							<option value="">
								<spring:message code="common.select" />
							</option>
						</select>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
				<form:hidden path="boxTypeNo" htmlEscape="false" value="${atmBoxMod.boxTypeNo}"/>
				<form:hidden path="boxType" htmlEscape="false" value="${atmBoxMod.boxType}"/>
			</div>
		</div>
		<!-- 操作按钮 -->
		<div class="form-actions">
			<shiro:hasPermission name="atm:atmBoxMod:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/atm/v01/atmBoxMod/back'"/>
		</div>
	</form:form>
</body>
</html>
