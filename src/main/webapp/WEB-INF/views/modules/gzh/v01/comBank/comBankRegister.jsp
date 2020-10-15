<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
	
	<div class="row" style="margin-top:15px;margin-left:2px;">
		<div class="span12" >
			<form:form id="inputForm"  action="" method="post" class="form-horizontal">
				<div class="control-group item">
					<%-- 冠字号码 --%>
					<label class="control-label" style="width:80px;">冠字号码：</label>
					<div class="controls" style="margin-left: 120px;">
						<input type="text" id="bankName" name="bankName" class="input-middle">
					</div>
				</div>
				<div class="control-group item">
					<%-- 面值 --%>
		  			<label class="control-label" style="width:80px;">面值：</label>
					<div class="controls" style="margin-left: 120px;">
						<input type="text" id="bankName" name="bankName" class="input-middle">
					</div>
				</div>
				<div class="control-group item">
					<%-- 备注 --%>
		  			<label class="control-label" style="width:80px;">备注：</label>
					<div class="controls" style="margin-left: 120px;">
						<input type="text" id="bankName" name="bankName"  style="width:120px; height:50px;">
					</div>
				</div>
			</form:form>
		</div>
	</div>
</body>
</html>