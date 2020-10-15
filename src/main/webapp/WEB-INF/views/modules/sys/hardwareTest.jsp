<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%-- 接口调试窗口 --%>
	<title>接口调试窗口</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#testButton").click(function(){
				$("#testForm").attr("action", "${ctx}/sys/HardwareTest/test");
				$("#testForm").submit();
			});
		});
	</script>
	
</head>
<body>
<form id="testForm" action="" method="post" class="form-horizontal">
	<div class="row" style="margin-top:10px;">
		<div class="span5">
			<label>Json输入：</label>
		</div>
	</div>
	<div class="row" style="margin-top:10px;">
		<textarea name="input" size="10" style="width:700px;height:150px"  datasrc="input" class="input-xxlarge">${input}</textarea>
	</div>
	<div class="row" style="margin-top:10px;">
		<div class="span5" style="text-align:left;">
			<label>Json输出：</label>
		</div>
		<div class="span4" style="text-align:right;">
			<labe>${timeMessage}&nbsp;&nbsp;&nbsp;</label>
		</div>
	</div>
	<div class="row" style="margin-top:10px;">
		<textarea style="width:700px;height:150px" class="input-xxlarge" readonly="readonly">${output}</textarea>
	</div>
	<div class="row" style="margin-top:10px;">
		<input type="submit" class="btn btn-primary" size="10"/>
	</div>

</form>
</body>
</html>