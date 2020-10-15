<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE >
<html>
<head>
    <meta charset="utf-8">
    <title>通知</title>
    <!--<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/app.css">   -->
    
		<!-- 开始设置桌面图标，自适应屏幕   -->
		<meta name="apple-mobile-web-app-capable" content="yes">
        <meta name="apple-mobile-web-app-status-bar-style" content="black">
        <meta name="viewport" content="width=device-width,initial-scale=1, minimum-scale=1.0, maximum-scale=1, user-scalable=no">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no,maximum-scale=1.0">
		<!-- 结束设置桌面图标，自适应屏幕   -->

		<link rel="stylesheet" href="${ctxStatic}/jqueryMobile/css/jquery.mobile-1.3.2.min.css">
		<script src="${ctxStatic}/jqueryMobile/js/jquery.js"></script>
		<script src="${ctxStatic}/jqueryMobile/js/jquery.mobile-1.3.2.min.js"></script>

		<!--mobiscroll日期插件-->
		<link href="${ctxStatic}/jqueryMobile/css/mobiscroll.css" rel="stylesheet" type="text/css" />
		<script src="${ctxStatic}/jqueryMobile/js/mobiscroll.js" type="text/javascript"></script>

	<script type="text/javascript">


	</script>
	<style type="text/css">
		
    </style>
    
</head>
<body>

		<div data-role="page" id="pageone">
			<div data-role="header" data-position="fixed" data-theme="b">
				<h1>通知</h1>
			</div>
			<div data-role="content">
				${errorMessage}
			</div>
		</div>


<!--- app --->
<script type="text/javascript">var ctx = '${ctx}';</script>

<script type="text/javascript">
App.page('form',function(){
    this.init = function(){
        
    }
    
})
</script>



</body>
</html>