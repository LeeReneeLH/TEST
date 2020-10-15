<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易报表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();

			$("#inputForm").validate({
				rules: {
                    inBatch: {remote: "${ctx}/doorOrder/v01/businessTransactionStatement/checkInBatch?id=${businessTransactionStatement.id}&batchNo=" + encodeURIComponent('${businessTransactionStatement.inBatch}')}
				},
				messages: {
                    inBatch: {remote: "存款批次不存在，或已经在报表中"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			//保存
			$("#btnSubmit").click(function () {
				var inBatch = $.trim($('#inBatch').val());
				if (inBatch == "") {
					//总金额不能为0
					alertx("<spring:message code='message.E8001' />");
					return;
				}
				$("#inputForm").submit();
			});
			
			//add by guojian 2020-05-21 start
			bindKeyEvent($('#realClearAmount'));
			//add by guojian 2020-05-21 end
		});



		//明细金额验证
		function amountCheck(){
			if(isNaN($("#realClearAmount").val())){
				$("#realClearAmount").val(0);
			}
		}

		function getTransactionDetail() {
			var inBatch = $("#inBatch").val();
			$.ajax({
				url: "${ctx}/doorOrder/v01/businessTransactionStatement/transactionDetail?inBatch="+inBatch,
				dataType:"json",
				success: function (res)  {
					$('#eqpId').val(res.eqpid);
					$('#doorId').val(res.doorId);
					$('#seriesNumber').val(res.equipmentInfo.seriesNumber);
					$('#doorName').val(res.door.name);
					$('#costTime').val(res.costTime);
                    $('#userId').val(res.user.id);
					$('#userLoginName').val(res.user.loginName);
					$('#username').val(res.user.name);
					$('#remarks').val(res.remarks);
					$('#cashAmount').val(res.cashAmount);
					$('#packAmount').val(res.packAmount);
					$('#totalAmount').val(res.totalAmount);
					$('#inDate').val(res.inDate);
					$('#startTime').val(res.startTime);
					$('#endTime').val(res.endTime);
					$('#backDate').val(res.backDate);
					$('#clearDate').val(res.clearDate);
					$('#realClearAmount').val(res.totalAmount);
				}
			})
		}
		
		//add by guojian 2020-05-21 start
		function bindKeyEvent(obj){
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
		        var txt = '';
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
		    });
		}
		//add by guojian 2020-05-21 end
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li>
			<a href="${ctx}/doorOrder/v01/businessTransactionStatement/">
				<spring:message code="report.businessTransactionStatement.list" />
			</a>
		</li>
		<li class="active"><a <%--zxk 返回页面取消自点  href="${ctx}/doorOrder/v01/businessTransactionStatement/form?id=${businessTransactionStatement.id}" --%>>交易报表<shiro:hasPermission name="doorOrder:v01:businessTransactionStatement:edit">${not empty businessTransactionStatement.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="doorOrder:v01:businessTransactionStatement:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="businessTransactionStatement" action="${ctx}/doorOrder/v01/businessTransactionStatement/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden id="eqpId" path="eqpid"/>
		<form:hidden id="doorId" path="doorId"/>
        <form:hidden id="userId" path="user.id"/>
		<sys:message content="${message}"/>
		<%-- 存款批次 --%>
        <div class="control-group">
            <label class="control-label"><spring:message code="report.businessTransactionStatement.inBatch" />：</label>
            <div class="controls">
                <form:input id="inBatch" path="inBatch" htmlEscape="false" maxlength="64" class="input-xlarge " onblur="getTransactionDetail()" readonly="${empty businessTransactionStatement.id?'false':'true'}"/>
            </div>
        </div>
		<%-- 机具编号 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.seriesNumber" />：</label>
			<div class="controls">
				<form:input id="seriesNumber" path="equipmentInfo.seriesNumber" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 门店（仓库） --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.doorName" />：</label>
			<div class="controls">
				<form:input id="doorName" path="door.name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 存款日期 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.inStartDate" />：</label>
			<div class="controls">
				<input id="inDate" name="inDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${businessTransactionStatement.inDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<%-- 开始时间 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.startTime" />：</label>
			<div class="controls">
				<input id="startTime" name="startTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${businessTransactionStatement.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<%-- 结束时间 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.endTime" />：</label>
			<div class="controls">
				<input id="endTime" name="endTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${businessTransactionStatement.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<%-- 耗时 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.costTime" />：</label>
			<div class="controls">
				<form:input path="costTime" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 店员 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.user" />：</label>
			<div class="controls">
				<form:input id="userLoginName" path="user.loginName" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 姓名 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.name" />：</label>
			<div class="controls">
				<form:input id="username" path="user.name" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 装运单号 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.remarks" />：</label>
			<div class="controls">
				<form:input path="remarks" htmlEscape="false" maxlength="64" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 自助存款金额 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.cashAmount" />：</label>
			<div class="controls">
				<form:input path="cashAmount" htmlEscape="false" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 强制存款金额 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.packAmount" />：</label>
			<div class="controls">
				<form:input path="packAmount" htmlEscape="false" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 总金额 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.totalAmount" />：</label>
			<div class="controls">
				<form:input path="totalAmount" htmlEscape="false" class="input-xlarge " readonly="true"/>
			</div>
		</div>
		<%-- 上门收款日期 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.backStartDate" />：</label>
			<div class="controls">
				<input id="backDate" name="backDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${businessTransactionStatement.backDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<%-- 清分日期 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.clearStartDate" />：</label>
			<div class="controls">
				<input id="clearDate" name="clearDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${businessTransactionStatement.clearDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<%-- 实际清点金额 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.realClearAmount" />：</label>
			<div class="controls">
				<form:input id="realClearAmount" path="realClearAmount" htmlEscape="false" class="input-xlarge " onkeyup="amountCheck()" onchange="amountCheck()"/>
			</div>
		</div>
		<%-- 差错处理情况 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="report.businessTransactionStatement.errorCheckCondition" />：</label>
			<div class="controls">
				<form:input path="errorCheckCondition" htmlEscape="false"  maxlength="200"  class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="doorOrder:v01:businessTransactionStatement:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>