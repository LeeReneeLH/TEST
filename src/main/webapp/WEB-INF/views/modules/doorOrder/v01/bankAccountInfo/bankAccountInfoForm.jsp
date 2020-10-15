<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>银行卡管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules : {
					/* provinceCode : {
						required : true
					},
					cityCode : {
						required : true
					},
					countyCode : {
						required : true
					} */
				},
				messages : {
					/* provinceCode : {
						required : "请选择省份"
					},
					cityCode : {
						required : "请选择城市"
					},
					countyCode : {
						required : "请选择区县"
					} */
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
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});	
			//如果修改，账户归属机构不可更改
			var bankAccountInfoOfficeId = '${bankAccountInfo.officeId}';
			if(bankAccountInfoOfficeId !=''&&bankAccountInfoOfficeId!=null){
				$("#officeIdButton").addClass("disabled");
			}
		});
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/doorOrder/v01/bankAccountInfo/">银行卡信息列表</a></li>
		<li class="active"><a href="${ctx}/doorOrder/v01/bankAccountInfo/form?id=${bankAccountInfo.id}">银行卡信息<shiro:hasPermission name="doorOrder:v01:bankAccountInfo:edit">${not empty bankAccountInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="doorOrder:v01:bankAccountInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bankAccountInfo" action="${ctx}/doorOrder/v01/bankAccountInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label"><spring:message code="door.bankAccountInfo.accountNo"/>：</label>
			<div class="controls">
				<form:input path="accountNo" htmlEscape="false" maxlength="64" class="input-xlarge  required creditcard"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code="door.bankAccountInfo.accountName"/>：</label>
			<div class="controls">
				<form:input path="accountName" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="door.bankAccountInfo.bankName"/>：</label>
            <div class="controls">
                <form:select path="bankName" maxlength="10" style="width:209px">
                    <form:option value="">
                        <spring:message code="common.select" />
                    </form:option>
                    <form:options
                            items="${fns:getFilterDictList('bank_company',true,'')}"
                            itemLabel="label" itemValue="value" htmlEscape="false" />
                </form:select>
            </div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="door.bankAccountInfo.merchant"/>：</label>
			<div class="controls">
				<c:if test="${fns:getUser().office.type eq '7'}">
					<sys:treeselect id="officeId" name="officeId"
						value="${bankAccountInfo.officeId}" labelName="${sto:getOfficeById(bankAccountInfo.officeId).name}"
						labelValue="${sto:getOfficeById(bankAccountInfo.officeId).name}"
						title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData"
						cssClass="required input-xsmall required"
						notAllowSelectParent="false" notAllowSelectRoot="false"
						isAll="false" allowClear="true" checkMerchantOffice="false"/>
				</c:if>
				<c:if test="${fns:getUser().office.type ne '7'}">
					<sys:treeselect id="officeId" name="officeId"
						value="${bankAccountInfo.officeId}" labelName="${sto:getOfficeById(bankAccountInfo.officeId).name}"
						labelValue="${sto:getOfficeById(bankAccountInfo.officeId).name}"
						title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData"
						cssClass="required input-xsmall required"
						notAllowSelectParent="false" notAllowSelectRoot="false" minType="9"
						maxType="9" isAll="true" allowClear="true"
						checkMerchantOffice="false" clearCenterFilter="true" />
				</c:if>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<!-- 省市区级联下拉列表 -->
		<sys:provinceCity/>
		<div class="form-actions">
			<shiro:hasPermission name="doorOrder:v01:bankAccountInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>