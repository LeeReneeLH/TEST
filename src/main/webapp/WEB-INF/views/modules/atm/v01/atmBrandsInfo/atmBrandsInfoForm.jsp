<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="atm.brands.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			/* $("#btnSubmit").click(function(){
                var url = "${ctx}/atm/v01/atmBrandsInfo/save";
	            $("#inputForm").attr("action", url);
				$("#inputForm").submit();
			}); */ 
			
			$.validator.addMethod("boxTypeValid",function(value,element,params){
				var atmBrandsNo = $("#atmBrandsNo").val();
				var boxNum = 0;
				// 取款箱数量
				if(params ==='2'){
					boxNum = $("#getBoxNumber").val();
				}
				// 回收箱数量
				if(params ==='1'){
					boxNum = $("#backBoxNumber").val();
				}
				// 存款箱数量
				if(params ==='3'){
					boxNum = $("#depositBoxNumber").val();
				}
				// 循环箱数量
				if(params ==='4'){
					boxNum = $("#cycleBoxNumber").val();
				}
				 if(parseInt(boxNum,10) > 0){
					 if(value != '' && value != null && value.length == 6 && value.substring(4,5)==params && value.substring(0,4)== atmBrandsNo){
						 return true;
					 }else{
						 return false;
					 } 
				 } else {
					 if(value != '' && value != null && (value.length < 6 || value.substring(4,5)!=params || value.substring(0,4)!= atmBrandsNo)){
						 return false;
					 }else{
						 return true;
					 } 
				 }
				 return true;
			},"<spring:message code='message.E4007'/>");
			
			$.validator.addMethod("boxNumValid",function(value,element,params){
				var boxTypeNo = '';
				// 取款箱类型
				if(params ==='2'){
					boxTypeNo = $("#getBoxType").val();
				}
				// 回收箱类型
				if(params ==='1'){
					boxTypeNo = $("#backBoxType").val();
				}
				// 存款箱类型
				if(params ==='3'){
					boxTypeNo = $("#depositBoxType").val();
				}
				// 循环箱类型
				if(params ==='4'){
					boxTypeNo = $("#cycleBoxType").val();
				}
				if(parseInt(value,10) > 0){
					 if(boxTypeNo != '' && boxTypeNo != null){
						 return true;
					 }else{
						 return false;
					 } 
				 } 
				 return true;
			},"<spring:message code='message.E4008'/>");
			
			$.validator.addMethod("alnum", function(value, element) {
				return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
				}, "<spring:message code='message.E4009'/>");
			
			// 校验品牌编号不能以0作为开头
			$.validator.addMethod("brandsValid", function(value, element) {
				if(parseInt(value,10) > 0 || parseInt(value,10) == 0){
					 if(value.substring(0,1) == 0 || value.substring(0,1) == '0'){
						 return false;
					 }else{
						 return true;
					 } 
				 } 
				 return true;
				}, "<spring:message code='message.E4043'/>");

			var parameters = {
					rules: {
						atmTypeNo: {remote:{ url:"${ctx}/atm/v01/atmBrandsInfo/atmType",data:{atmBrandsNo:function(){return $("#atmBrandsNo").val();},oldAtmTypeNo:function(){return $("#oldAtmTypeNo").val();}}}},
					},
					messages: {
						atmTypeNo: {remote: "<spring:message code='message.E4002'/>"},
					},
					submitHandler: function(form){
						confirmx("<spring:message code='message.E4053'/>",function submit(){
							loading('正在提交，请稍等...');
							form.submit();	
						}) 
					}, 
					////errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						//$("#messageBox").text("输入有误，请先更正。");
						if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
							error.appendTo(element.parent().parent());
						} else {
							error.insertAfter(element);
						}
					}
			};
			
			$("#atmBrandsNo").change(function(){
				var atmBrandsId = $("#id").val();
				var atmBrandsNo = $(this).val();
				
				if(atmBrandsNo == '' || atmBrandsNo == null){
					$("#atmBrandsName").attr("readonly",false);
				}
				$.post('${ctx}/atm/v01/atmBrandsInfo/atmBrand', {atmBrandsNo:atmBrandsNo}, function(data){
					var atmBrandsName = data.atmBrandsName;
					if(atmBrandsName !=null && atmBrandsName !=''){
						if (atmBrandsId !=null && atmBrandsId !='') {
							$("#atmBrandsName").val(atmBrandsName);
							$("#atmBrandsName").attr("readonly",false);
						}else{
							$("#atmBrandsName").val(atmBrandsName);
							$("#atmBrandsName").attr("readonly",true);
						}
					}else{
						$("#atmBrandsName").attr("readonly",false);
					}
				},"json");	
				$("#backBoxType,#getBoxType,#cycleBoxType,#depositBoxType").trigger("change");
			}); 
		
			$("#getBoxNumber, #backBoxNumber, #cycleBoxNumber, #depositBoxNumber").blur(function(){
				var getBoxNum = $("#getBoxNumber").val();
				var backBoxNum = $("#backBoxNumber").val();
				var cycleBoxNum = $("#cycleBoxNumber").val();
				var depositBoxNum = $("#depositBoxNumber").val();
				var boxNum=0;
				if(getBoxNum != '' && getBoxNum != null){
					boxNum+=parseInt(getBoxNum, 10);
				}
				if(backBoxNum != '' && backBoxNum != null){
					boxNum+=parseInt(backBoxNum, 10);
				}
				if(cycleBoxNum != '' && cycleBoxNum != null){
					boxNum+=parseInt(cycleBoxNum, 10);
				}
				if(depositBoxNum != '' && depositBoxNum != null){
					boxNum+=parseInt(depositBoxNum, 10);
				}
				$("#boxNum").val(boxNum);
			});
			
			$("#atmBrandsNo").focus();
			
			
			$("#inputForm").validate(parameters);
			
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/atm/v01/atmBrandsInfo/"><spring:message code="atm.brands.list"/></a></li>
	   	<li class="active"><a
			href="${ctx}/atm/v01/atmBrandsInfo/form?id=${atmBrandsInfo.id}">
				<shiro:hasPermission name="atm:atmBrandsInfo:edit">
					<c:choose>
						<c:when test="${not empty atmBrandsInfo.id}">
							<spring:message code="atm.brandModel" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="atm.brandModel" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmBrandsInfo" action="${ctx}/atm/v01/atmBrandsInfo/save" method="post" class="form-horizontal">
	    <!--主键  -->
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${atmBrandsInfo.strUpdateDate}">			
		<!--品牌编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.id"/>：</label>
			<div class="controls">
					<c:choose>
						<c:when test="${not empty atmBrandsInfo.id}">
							<form:input path="atmBrandsNo" htmlEscape="false" maxlength="4" minlength="4" brandsValid="0" readonly="true" class="input-large required digits"/> 
						</c:when>
						<c:otherwise>
							<form:input path="atmBrandsNo" htmlEscape="false" maxlength="4" minlength="4" brandsValid="0" class="input-large required digits"/>
						</c:otherwise>
					</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--品牌名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.name"/>：</label>
			<div class="controls">
					<c:choose>
						<c:when test="${not empty atmBrandsInfo.id}">
							<form:input path="atmBrandsName" htmlEscape="false" maxlength="15" readonly="true" class="input-large required"/> 
						</c:when>
						<c:otherwise>
							<form:input path="atmBrandsName" htmlEscape="false" maxlength="15" class="input-large required"/>
						</c:otherwise>
					</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--机型编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.model.id"/>：</label>
			<div class="controls">
			    <input type="hidden" id="oldAtmTypeNo" name="oldAtmTypeNo" value="${atmBrandsInfo.atmTypeNo}"/>
			    	<c:choose>
						<c:when test="${not empty atmBrandsInfo.id}">
							<form:input path="atmTypeNo" htmlEscape="false" maxlength="10" readonly="true" class="input-large required abc"/> 
						</c:when>
						<c:otherwise>
							<form:input path="atmTypeNo" htmlEscape="false" maxlength="10" class="input-large required abc"/>
						</c:otherwise>
					</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--机型名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.model.name"/>：</label>
			<div class="controls">
				<form:input path="atmTypeName" htmlEscape="false" maxlength="15" class="input-large required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--总钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.wayNum"/>：</label>
			<div class="controls">
				<form:input path="boxNum" htmlEscape="false" maxlength="1" min="1" class="input-large digits" readonly="true" />
			</div>
		</div>
		<!--取款箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.drawBox.model"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBrandsInfo.id}">
						<form:input path="getBoxType" htmlEscape="false" maxlength="6" boxTypeValid='2' readonly="true" class="input-large required"/> 
					</c:when>
					<c:otherwise>
						<form:input path="getBoxType" htmlEscape="false" maxlength="6" boxTypeValid='2' class="input-large required"/>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--取款钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.drawBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="getBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='2' class="input-large required digits" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--回收箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.reclaimBox.model"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBrandsInfo.id}">
						<form:input path="backBoxType" htmlEscape="false" maxlength="6" boxTypeValid='1' readonly="true" class="input-large required"/> 
					</c:when>
					<c:otherwise>
						<form:input path="backBoxType" htmlEscape="false" maxlength="6" boxTypeValid='1' class="input-large required"/>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--回收钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.reclaimBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="backBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='1' class="input-large required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--存款箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.depositBox.model"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBrandsInfo.id}">
						<form:input path="depositBoxType" htmlEscape="false" maxlength="6" boxTypeValid='3' readonly="true" class="input-large required" /> 
					</c:when>
					<c:otherwise>
						<form:input path="depositBoxType" htmlEscape="false" maxlength="6" boxTypeValid='3' class="input-large required" />
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--存款钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.depositBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="depositBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='3' class="input-large required digits" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--循环箱型号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.recyclingBox.model"/>：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBrandsInfo.id}">
						<form:input path="cycleBoxType" htmlEscape="false" maxlength="6" boxTypeValid='4' readonly="true" class="input-large required"/> 
					</c:when>
					<c:otherwise>
						<form:input path="cycleBoxType" htmlEscape="false" maxlength="6" boxTypeValid='4' class="input-large required"/>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--循环钞道数  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.recyclingBox.wayNum"/>：</label>
			<div class="controls">
				<form:input path="cycleBoxNumber" htmlEscape="false" maxlength="1" min='0' boxNumValid='4' class="input-large required digits" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--操作按钮  -->
		<div class="form-actions">
			<shiro:hasPermission name="atm:atmBrandsInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/atm/v01/atmBrandsInfo/back'"/>
		</div>
	</form:form>
</body>
</html>
