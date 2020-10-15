<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>参数管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			remarkValidate();
			//参数的key输入格式为数字、字母和“.”
			$.validator.addMethod("propertyKeyValidator", function ( value, element, param ) {
				var reg = /^[a-zA-Z0-9.]{0,}$/;
				return reg.test(value);
			  } , "<spring:message code='sys.property.format'/>"
			);
			//备注长度验证
			$("#remark").keyup(function(){
				remarkValidate();
			});
			/* 参数验证 */
			$("#inputForm").validate({
				rules:{
					propertyKey:{
									//参数键是否重复验证
									remote:{url:"${ctx}/sys/dbConfig/DbConfigPropertyController/checkPropertyKey?propertyKey=" + $("#propertyKey").val(),cache:false},
									//输入格式验证
									propertyKeyValidator:true
								}
				},
				messages: {
					propertyKey:{remote: "<spring:message code='sys.property.key.exist'/>"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorPlacement: function(error, element) {
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			//判断如果参数值为空，那么当前元素即是分组，隐藏参数值
			if($("#value").val() == null || $("#value").val() == ''){
				$("#propertyValue").hide();
			}
		});
		//单选框
		/* 分组 */
		function divideIntoGroups(){
			$("#propertyValue").hide();
		}
		/* 添加键值 */
		function addChildProperty(){
			$("#propertyValue").show();
		}
		/*备注项长度验证*/
		function remarkValidate(){
			var length = $("#remark").val().length;
			if(length > 999){
				$("#remark").val($("#remark").val().substring(0,1000));
				length = $("#remark").val().length;
			}
			$("#contentLength").text(length);
		}
	</script>
</head>

<body>
	<form:form id="inputForm" modelAttribute="property" action="${ctx}/sys/dbConfig/DbConfigPropertyController/save" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<!-- 选项,如果当前操作时添加分组或参数时，显示选项 -->
		<div class = "control-group">
			<div class="controls">
				<c:if test="${property.parent.id ne '2' and property.configType eq 'insert' }">
					<label><form:radiobutton path="type" value = "group" onclick="divideIntoGroups()"/><spring:message code='sys.property.group'/></label>
					<label><form:radiobutton id = "key" path="type" value = "key" onclick="addChildProperty()"/><spring:message code='sys.property.keyValue'/></label>
				</c:if>
			</div>
		</div>
		<!-- 父级编号 -->
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.property.parentKey'/>:</label>
			<div class="controls">
				<form:input path = "parent.propertyKey" htmlEscape="false" maxlength="50"
							readonly="true"/>
                <form:hidden id ="parentId" path="parent.id" htmlEscape="false" maxlength="50"
							readonly="true"/>
			</div>
		</div>
		<!-- 当前参数id -->
		<form:hidden path="id"/>
		<!-- 参数的更新时间 -->
		<form:hidden path="strUpdateDate"/>
		<form:hidden path="parentIds"/>
		<!-- 当前参数的操作类型：修改还是插入 -->
		<form:hidden path="configType"/>
		<!-- 当前参数编号 -->
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.property.key'/>:</label>
			<div class="controls">
			<!-- 修改参数的时候，参数的键不能修改 -->
				<c:choose>
					<c:when test="${empty(property.propertyValue) and property.configType eq 'update'}">
						<form:input path="propertyKey" htmlEscape="false" maxlength="50" readonly="true"/>
					</c:when>
					<c:when test="${not empty(property.propertyValue) and property.configType eq 'update'}">
						<form:input path="propertyKey" htmlEscape="false" maxlength="50" readonly="true"/>
					</c:when>
					<c:otherwise>
						<form:input path="propertyKey" htmlEscape="false" maxlength="50"
							class="required propertyKeyValidator" />
							<span class="help-inline"><font color="red">*</font> </span>
					</c:otherwise>
				</c:choose>
				
			</div>
		</div>
		<!-- 当前参数值 ,如果类型是分组的话，不显示参数值-->
		<div id="propertyValue" class="control-group">
			<label class="control-label"><spring:message code='sys.property.value'/>:</label>
			<div class="controls">
				<form:input id ="value" path="propertyValue" htmlEscape="false" maxlength="300" class="required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<!-- 备注 -->
		<div class="control-group">
			<label class="control-label"><spring:message code='sys.property.remark'/>:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="3" maxlength="200" class="input-xxlarge"/>
				<span class="help-inline"><font color="red">(<span id = "contentLength">0</span>/1000字以内)</font></span>
			</div>
		</div>
	</form:form>
</body>
</html>