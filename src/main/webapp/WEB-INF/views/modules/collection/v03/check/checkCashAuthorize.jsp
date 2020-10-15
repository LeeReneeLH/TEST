<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	table,td,th{
    border:0px !important;
	} 
	</style>
</head>
<body>
<div class="row" style="margin-left:2px;margin-top:20px;">
		<table >
			<tr>
				<!-- 用户名 -->
				<td  style="width:100px;text-align:right;"  >
					<spring:message code="door.public.userName" />：
				</td>
				<td style="width:280px;">
					<input autocomplete="off" type="text" id="authorizer" name="authorizer" class="required" maxlength="15" style="width:250px;">
				</td>
			</tr>
			<tr>
				<!-- 密码 -->
				<td  style="text-align:right;"  >
					<spring:message code="door.public.password" />：
				</td>
				<td >
					<input autocomplete="off" type="password" name="authorizeBy" id="authorizeBy" style="width:250px;" maxlength="15">
				</td>
			</tr>
		</table>
</div>
</body>
</html>