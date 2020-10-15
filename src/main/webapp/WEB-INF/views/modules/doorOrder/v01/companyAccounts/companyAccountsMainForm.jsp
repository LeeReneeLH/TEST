 <%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公司账务管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){	
					//金额验证
					if(!checkCashIsZero()){
						return;
					}
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
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
			
			//提交时检查金额是否为0
			function checkCashIsZero(){
				if($("#inAmount").val()==0){
					alertx("存款金额不能为0，请重新输入！");
					return false;
				}
				//提示对话框
				//return confirm('是否确认向公司账中添加金额为：'+$("#inAmount").val()+'元的存款？');
		        
		return confirmx('是否确认向公司账中添加金额为：'
								+ $("#inAmount").val() + '元的存款？',
								'${ctx}/doorOrder/v01/companyAccountsMain/save?inAmount='
										+ $("#inAmount").val() + '&companyId='
										+ $("#companyId").val() + '&bankCard='
										+ $("#bankCard").val());
					}
				});
		//通过ajax,从后台获取机构银行卡号。
		function getBankCard(officeId) {
			var url = "${ctx}/doorOrder/v01/backAccountsMain/getBankCard?officeId="
					+ officeId;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data, textStatus) {
					$("#bankCard").val(data);
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}

		//通过ajax,从后台获取公司银行卡号和待存款金额。
		function getStayAmount(officeId) {
			var url = "${ctx}/doorOrder/v01/companyAccountsMain/getStayAmount?officeId="
					+ officeId;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data, textStatus) {
					$("#bankCard").val(data.bankCard);
					$("#inAmount").val(data.amount);
					$("#errorMessage").html(data.errorMessage);
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		/* 金额验证修改 gzd 2020-05-21 start */
		$(document).ready(function(){
			detailAmountCheck($("#inAmount"));
		});
		//明细金额验证
		function detailAmountCheck(obj){			
			obj.keyup(function () {
				var value = $(this).val().replace(/,/g, "")
				if(value.split(".")[0].length <= 10) {
					var reg = value.match(/\d+\.?\d{0,2}/);
			        var txt = '';
			        if (reg != null) {
			            txt = reg[0];
			        }
			        $(this).val(txt);
				} else {
					var value2 = value.slice(0,10)
					var reg2 = value2.match(/\d+\.?\d{0,2}/);
					var txt2 = '';
			        if (reg2 != null) {
			            txt2 = reg2[0];
			        }
					$(this).val(txt2);
				}
		    }).change(function () {
		        $(this).keypress();
		        var v = $(this).val();
		        console.log('v',v)
		        var txt = '';
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
		    });
			
		}
		/* 金额验证修改 gzd 2020-05-21 end */
		/* //明细金额验证
		function detailAmountCheck() {
			if (isNaN($("#inAmount").val())) {
				$("#inAmount").val(0);
			}
		} */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="doorOrder:v01:companyAccountsMain:view"><li><a href="${ctx}/doorOrder/v01/companyAccountsMain/">公司账务列表</a></li></shiro:hasPermission>
		<shiro:hasPermission name="doorOrder:v01:companyAccountsMain:edit"><li class="active"><a href="${ctx}/doorOrder/v01/companyAccountsMain/form">公司存款添加</a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="companyAccountsMain" action="${ctx}/doorOrder/v01/companyAccountsMain/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<label id="errorMessage" style="color:red;padding-bottom:10px;padding-left:80px"></label>
		<div class="control-group">
					<!-- 客户名称 -->
					<label class="control-label"><spring:message code="common.company" />：</label>
					<div class="controls">
						<form:select path="companyId" id="companyId"
							class="input-large required" disabled="disabled"
							onchange="getStayAmount(this.options[this.selectedIndex].value)">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${sto:getPlatform()}"
								itemLabel="name" itemValue="id" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
						<form:hidden path="companyName" />
					</div>
				</div>
		<div class="control-group">
			<label class="control-label">银行卡号：</label>
			<div class="controls">
			<!-- 为简便，卡号验证后期加 creditcard -->
				<form:input id="bankCard" path="bankCard" htmlEscape="false" maxlength="19" class="creditcard"
					style="ime-mode:disabled;" />
			</div>
		</div>
		<div class="control-group">
			<!-- 总金额 -->
			<label class="control-label"><spring:message
					code="door.accountManage.saveMoney" />：</label>
			<div class="controls">
				<form:input id="inAmount" path="inAmount" htmlEscape="false" maxlength="13" class="required number"
					style="ime-mode:disabled;" />
					<%-- readOnly="readOnly" --%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" onclick="checkCashIsZero();" />&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>