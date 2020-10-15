<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-left:2px;">
		
			<form:form id="inputForm" modelAttribute="authorizeParam" action ="" method="post">
				<br>
					<br>
					<div class="controls">
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 60px"><spring:message code='common.authorizer' />：</label>
					<label>${authorizeParam.authorizeName}</label>
					</div>
					<br/>
					<div class="controls">
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 45px"><spring:message code='clear.public.authorizeDate' />：</label>
					<label><fmt:formatDate value="${authorizeParam.authorizeDate}" pattern="yyyy-MM-dd HH:mm:ss" /></label>
					</div>
					<br/>
					<label class="control-label" style="color:rgb(51,51,51);  margin-left: 45px"><spring:message code='clear.public.authorizeReason' />：</label>
					<div class="controls">
					<form:textarea path="authorizeReason" readonly="true" cols="10" rows="3" style="width:280px; height :120px; margin-left: 128px;margin-top: -20px"  maxlength="30"/>
					</div>
					<div class="control-group item">
						<label class="control-label"></label>
						<span id="errorMessage" style="color: red;"></span>
					</div>
					
			</form:form>	
		
</div>
</body>
</html>