<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>授权管理</title>
<meta name="decorator" content="default" />
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler : function(form) {
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled", true);
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
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
				}
			});
			    authorizeAmountCheck($('#amount'));
			    //授权类型关联表达式类型
			    makeExpressionTypeOptions();
			    $('#authorizeType').change(makeExpressionTypeOptions);
				$.validator.addMethod("checkAmount",function(value,element){ 
		    		if ($("#expressionType").val() === '3') {
			    	 var checkAmount = /^([1-9]\d*(\.\d*)?)|0[1-9]\d*(\.\d*)?|(0\.\d*[1-9][0-9])|(0\.\d*[1-9])$/;
			         return this.optional(element)||(checkAmount.test(value)); }
		    		else {
		    			return true;
		    		}},"授权金额不能等于0");
		});
		function authorizeAmountCheck(obj) {
			obj.keyup(function() {
				var value = $(this).val().replace(/,/g, "")
				if (value.split(".")[0].length <= 6) {
					var reg = value.match(/\d+\.?\d{0,2}/);
					var txt = '';
					if (reg != null) {
						txt = reg[0];
					}
					$(this).val(txt);
				} else {
					var value2 = value.slice(0, 6)
					var reg2 = value2.match(/\d+\.?\d{0,2}/);
					var txt2 = '';
					if (reg2 != null) {
						txt2 = reg2[0];
					}
					$(this).val(txt2);
				}
			}).change(function() {
				$(this).keypress();
				var v = $(this).val();
				var txt = '';
				if (/\.$/.test(v)) {
					$(this).val(v.substr(0, v.length - 1));
				}
			});
		}
		
		function makeExpressionTypeOptions(){
				$.ajax({
					url : '${ctx}/doorOrder/v01/checkCashAuthorize/makeExpressionTypeOptions?authorizeType=' + $("#authorizeType").val(),
					type : 'Post',
					contentType : 'application/json; charset=UTF-8',
					async : false,
					cache : false,
					dataType : 'json',
					success : function(res) {
						$("#expressionType option").remove();
						$("#expressionType").prepend("<option value='' selected='selected'>请选择</option>")
						for(i=0;i<res.dictValueList.length;i++){
							$("#expressionType").append("<option value='"+res.dictValueList[i]+"'>"+res.dictLabelList[i]+"</option>")
						}
						if($("#authorizeType").val() == $("#authorizeTypeHidden").val()){
							$("#expressionType").find("option[value="+$("#expressionTypeHidden").val()+"]").attr("selected","selected");
						}	
						$("#expressionType").trigger("change");
					}
				});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/checkCashAuthorize/list">授权列表</a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/checkCashAuthorize/form?id=${checkCashAuthorize.id}">授权<shiro:hasPermission name="collection:checkCashAuthorize:edit">${not empty checkCashAuthorize.id?'修改':'登记'}</shiro:hasPermission></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="checkCashAuthorize"
		action="${ctx}/doorOrder/v01/checkCashAuthorize/save" method="post"
		class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />	
		<%-- <div class="control-group">
			<label class="control-label"><spring:message
					code="checkCash.authorize.officeType" />：</label>
			<div class="controls">
				    <form:select path="officeType" class="input-large required"
					id="selectOfficeType" disabled="${not empty checkCashAuthorize.id ? true:false }">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('sys_office_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false"  name="officeType"/>
				    </form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>			
		</div> --%>
		<div class="control-group">
			<%-- 机构 --%>
			<label class="control-label"><spring:message
					code="checkCash.authorize.officeId" />：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty checkCashAuthorize.id}">
						<sys:treeselect id="officeName" name="officeId"
							value="${checkCashAuthorize.officeId}" labelName="officeName"
							labelValue="${checkCashAuthorize.officeName}" title="机构"
							url="/sys/office/treeData" notAllowSelectRoot="false"
							cssClass="required input-medium" notAllowSelectParent="false"
							isAll="true" minType="0" maxType="9" allowClear="true"
							checkMerchantOffice="false" clearCenterFilter="true"
							disabled="disabled" checkTopOffice="true"/>
					</c:when>
					<c:otherwise>
						<sys:treeselect id="officeName" name="officeId"
							value="${checkCashAuthorize.officeId}" labelName="officeName"
							labelValue="${checkCashAuthorize.officeName}" title="机构"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false"
							isAll="true" minType="0" maxType="9" allowClear="true"
							checkMerchantOffice="false" clearCenterFilter="true" checkTopOffice="true"/>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<%-- 是否开启 --%>
			<label class="control-label"><spring:message
					code="checkCash.authorize.isUse" />：</label>
			<div class="controls">
				<form:radiobutton path="isUse" value="0" checked="true" />
				开启
				<form:radiobutton path="isUse" value="1" />
				关闭
			</div>
		</div>
		<div class="control-group">
			<!--授权类型  -->
			<label class="control-label"><spring:message
					code="checkCash.authorize.type" />：</label>
			<div class="controls">
				<form:select path="type" class="input-large	 required" id="authorizeType"
					style="font-size:15px;" >
					<form:option value="" selected="selected">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${fns:getDictList('authorize_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<%-- 授权金额 --%>
			<label class="control-label"><spring:message
					code="checkCash.authorize.amount" />：</label>
			<div class="controls">
				<form:select path="expressionType"  class="required" style="font-size:15px; width:100px;" id="expressionType">
					<form:option value="" selected="selected">
						<spring:message code="common.select" />
					</form:option>
					<!--<form:options  id="expressionType" items="${fns:getDictList('expression_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
						-->
				</form:select>
				<form:input id="amount" path="amount" htmlEscape="false"
					maxlength="13" style="font-size:15px; width:92px;"
					class="checkAmount required"/>
				<span id="remarkSpan" class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="collection:checkCashAuthorize:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code="common.save" />" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code="common.return" />"
				onclick="history.go(-1)" />
		</div>
		<input type="hidden" id="expressionTypeHidden" name="expressionTypeHidden" value="${checkCashAuthorize.expressionType}"/>
		<input type="hidden" id="authorizeTypeHidden" name="authorizeTypeHidden" value="${checkCashAuthorize.type}"/>
	</form:form>
</body>
</html>