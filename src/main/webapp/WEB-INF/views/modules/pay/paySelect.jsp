<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 聚合支付测试 --%>
	<title>聚合支付测试</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/layer/layer-v3.1.1/layer/layer.js"></script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#" onclick="javascript:return false;">支付平台测试</a></li>
	</ul><br/>
		
		<sys:message content="${message}"/>
		<div class="form-actions" style="margin-left: 70px">
			<input id="chargeSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.charge'/>"onclick="window.location.href ='${ctx}/pay/select?type=2'"/>&nbsp;
			<input id="transferSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.transfer'/>"onclick="window.location.href ='${ctx}/pay/select?type=1'"/>
		</div>
	
</body>
</html>