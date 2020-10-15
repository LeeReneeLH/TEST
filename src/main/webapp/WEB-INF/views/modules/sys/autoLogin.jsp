<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 自动提交
			$("#loginForm").submit();
		});
	</script>
</head>
<body>
	<form id="loginForm" action="${ctx}/login" method="post">
		<input type="hidden" id="username" name="username"  value="${username}" >
		<input type="hidden" id="password" name="password" value="${password}" />
	</form>
</body>
</html>