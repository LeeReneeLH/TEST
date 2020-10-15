<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%-- 定时任务添加 --%>
<title>定时任务添加</title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript"
	src="${ctxStatic}/layer/layer-v3.1.1/layer/layer.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// 提交按钮
		$("#transferSubmit").click(function() {
			debugger
			var payName = $("#payName").val();
			var accNo = $("#accNo").val();
			var bankName = $("#bankName").val();
			var orderAmount = $("#orderAmount").val();
			var type = $("#type").val();
			$("#inputForm").attr("action", "${ctx}/pay/transfer?payName="+payName+"&accNo="+accNo+"&bankName="+bankName+"&orderAmount="+orderAmount+"&type="+type);
			$("#inputForm").submit();
		});

		// 提交按钮
		$("#chargeSubmit").click(function() {
			var orderAmount = $("#orderAmount").val();
			var type = $("#type").val();
			$("#inputForm").attr("action", "${ctx}/pay/charge?orderAmount="+orderAmount+"&type="+type);
			$("#inputForm").submit();
		});
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
	<li><a href="${ctx}/pay/test" ">支付平台测试</a></li>
	<c:choose>
			<c:when test="${type eq '1'}">
				<%-- 浦发代付测试 --%>
				<li class="active"><a href="#" onclick="javascript:return false;">浦发代付测试</a></li>
			</c:when>
			<c:otherwise>
				<%-- 聚合支付测试 --%>
				<li class="active"><a href="#" onclick="javascript:return false;">聚合支付测试</a></li>
		</c:otherwise>
		</c:choose>
	</ul>
	<br />
	<input type="hidden" id="type" value="${type}">
	<sys:message content="${message}"/>
	<c:if test="${type eq '1'}">
		<form:form id="inputForm" action="" method="post"
			class="form-horizontal">
			<div class="control-group">
				<%-- 收款人姓名 --%>
				<label class="control-label"><spring:message
						code='common.payName' />：</label>
				<div class="controls">
					<input id="payName" htmlEscape="false" maxlength="20"
						class="required" />
				</div>
			</div>
			<div class="control-group">
				<%-- 银行卡号--%>
				<label class="control-label"><spring:message
						code='common.accNo' />：</label>
				<div class="controls">
					<input id="accNo" htmlEscape="false" maxlength="20"
						class="required codeValidate" />
				</div>
			</div>
			<div class="control-group">
				<%-- 银行名称--%>
				<label class="control-label"><spring:message
						code='common.bankName' />：</label>
				<div class="controls">
					<input id="bankName" htmlEscape="false" maxlength="20"
						class="required codeValidate" />
				</div>
			</div>
			<div class="control-group">
				<%-- 付款金额 --%>
				<label class="control-label"><spring:message
						code='common.orderAmount' />：</label>
				<div class="controls">
					<input id="orderAmount" htmlEscape="false" maxlength="50"
						class="required" />
				</div>
			</div>
			<div class="form-actions" style="margin-left: 70px">
				<input id="transferSubmit" class="btn btn-primary" type="button"
					value="提交"/>
			</div>
		</form:form>
	</c:if>
	<c:if test="${type eq '2'}">
		<form:form id="inputForm" action="" method="post"
			class="form-horizontal">
			<div class="control-group">
				<%-- 付款金额 --%>
				<label class="control-label"><spring:message
						code='common.orderAmount' />：</label>
				<div class="controls">
					<input id="orderAmount" htmlEscape="false" maxlength="50"
						class="required" />
				</div>
			</div>
			<div class="form-actions" style="margin-left: 70px">
				<input id="chargeSubmit" class="btn btn-primary" type="button"
					value="提交"
					 /> 
			</div>
		</form:form>
	</c:if>

</body>
</html>