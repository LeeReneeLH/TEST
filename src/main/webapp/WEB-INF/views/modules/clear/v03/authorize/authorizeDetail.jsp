<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<style>
 table,td,th{
    border:0px !important;
</style>
</head>

<body>
<div class="row" style="margin-left:2px;">
		
			<form:form id="inputForm" modelAttribute="" action ="" method="post">
					
					<br/>
					<label class="control-label" style="color:grey; margin-left: 2px">请登录进行授权：</label>
					<div class="controls">
					<input type="hidden" id="clearId" name="clearId" value="${clearId}"  maxlength="15" class="required" />
					</div>
					
					<br>
					<div class="controls">
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 60px"><spring:message code="clear.public.username" />：</label>
					<input type="text" id="authorizeLogin" name="authorizeLogin"  maxlength="15" class="required" />
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<br/>
					<div class="controls">
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 75px"><spring:message code="clear.public.password" />：</label>
					<input type="password" id="authorizePass" name="authorizePass" maxlength="15" class="required" />
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<br/>
					<label class="control-label" style="color:rgb(51,51,51);  margin-left: 43px"><spring:message code="clear.public.authorizeReason" />：</label>
					<div class="controls">
					<textarea cols="10" rows="3" style="width:280px; height :120px; margin-left: 128px;margin-top: -20px"  id="authorizeReason" name="authorizeReason" maxlength="30"  class="required" ></textarea>
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<div class="control-group item">
						<label class="control-label"></label>
						<span id="errorMessage" style="color: red;"></span>
					</div>
					
			</form:form>	
		
</div>
</body>
</html>