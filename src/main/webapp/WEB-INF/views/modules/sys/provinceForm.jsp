<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#value").focus();
			
			$("#inputForm").validate({
				
				rules: {
					provinceCode: {remote: {url:"${ctx}/sys/province/checkProCode",data:{provinceCode:function(){return $("#provinceCode").val();}}}},
				},
				messages: {
					provinceCode: {remote: "该省份编码已存在，请重新输入"},
				},
				submitHandler: function(form){
					/* var url="${ctx}/sys/map/checkLongiLati";
					//异步验证
					$.ajax({
						url:url,
						type:"post",
						data:'longitude='+$("#longitude").val()+'&latitude='+$("#latitude").val(),
						dataType:"json",
						async:false,
						success:function(data){
							if(data){
								loading('正在提交，请稍等...');
								form.submit();
							} else {
								alertx("经度或纬度不能为空，请重新输入");
							}
						},
						error:function(){
							//系统内部异常，请稍后再试或与系统管理员联系
							alertx("<spring:message code='message.E0101'/>")
						}
					}); */
					
					//校验经纬度，一个有值的情况下另一个不能为空
					if($("#longitude").val() != '' && $("#latitude").val() == ''){
						//$("#longitudeSpan").text("");
						//$("#latitudeSpan").text("纬度不能为空")
						$("#latitude").focus();
						alertx("纬度不能为空");
						
					}else if($("#latitude").val() != '' && $("#longitude").val() == ''){
						//$("#latitudeSpan").text("");
						//$("#longitudeSpan").text("经度不能为空");
						$("#longitude").focus();
						alertx("经度不能为空");
						
					}else{
						//$("#latitudeSpan").text("");
						//$("#longitudeSpan").text("");
						loading('正在提交，请稍等...');
						form.submit();
					}
				},
				
				 errorPlacement: function(error, element) {
					
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			//经纬度输入验证
			$.validator.addMethod("checkLocation",function(value,element){  
				            var checkLocation = /^([1-9]\d{0,5}|0)(\.\d{0,6})?$/;  
				            return this.optional(element)||(checkLocation.test(value));  
				        },"请输入合法数字");
			
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/province/">省份列表</a></li>
		<li class="active"><a href="${ctx}/sys/province/form?id=${provinceEntity.id}">省份<shiro:hasPermission name="sys:province:edit">${not empty provinceEntity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:province:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="provinceEntity" action="${ctx}/sys/province/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">省份编码:</label>
			<div class="controls">
				<form:input path="provinceCode" htmlEscape="false" maxlength="50" class="required digits" disabled="${empty provinceEntity.id ? false:true }"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地图编码:</label>
			<div class="controls">
				<form:input path="proJsonCode" htmlEscape="false" maxlength="50" class="number"/>
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">省份名称:</label>
			<div class="controls">
				<form:input path="proName" htmlEscape="false" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经度:</label>
			<div class="controls">
				<form:input path="longitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
				<span id="longitudeSpan" style="color: red;"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">纬度:</label>
			<div class="controls">
				<form:input path="latitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
				<span id="latitudeSpan" style="color: red;"></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:province:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/province/back'"/>
		</div>
	</form:form>
</body>
</html>