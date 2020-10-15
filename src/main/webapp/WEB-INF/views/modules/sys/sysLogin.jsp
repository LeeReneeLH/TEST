<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/common/typica-login.css">
    <link rel="stylesheet" href="${ctxStatic}/common/login-page.css">
	<style type="text/css">
		.control-group{border-bottom:0px;}
		label.error{
			display:block;
			color:#fff;
			font-size:13px;
		}
		a{color:#fff;}
		.alert{padding:0px !important;margin-top:-20px;/* width:600px; */}
		.login_footer{position:absolute;bottom:0px;width:100%;background-color:#fff;text-align:center;color:#000;font-size:18px;height:30px;line-height:30px;}
	</style>
    <script src="${ctxStatic}/common/backstretch.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$.backstretch([
  		      "${ctxStatic}/images/login_bg.jpg", 
  		  	], {duration: 10000, fade: 1000});
			
			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			});
			var SysOtpSwitch = ${fns:getConfig("sys.login.sysOtpSwitch")};
			if(SysOtpSwitch==false){
				$("input[name=command]").hide(); 
			}
		});
		// 如果在框架或在对话框中，则弹出提示并跳转到首页
		if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
			alert('您已登录超时或在其他地点登录，请重新登录！');
			top.location = "${ctx}";
		}
	</script>
</head>
<body>
    <div class="container" >
	<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->
	<div class="login-title">${fns:getConfig('productName')}</div>
		<div id="login-wraper">
	
	<form id="loginForm" class="form login-form" action="${ctx}/login" method="post" autocomplete="off">
		<div class="body">
			<div id="messageBox" class="alert alert-error ${empty message ? 'hide' : ''}">
				<label id="loginError" class="error">${message}</label>
			</div>
			<div class="control-group">
				<div class="controls">
					<input autocomplete="off" type="text" id="username" name="username" class="required login_user" value="${username}" placeholder="登录名">
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<input  autocomplete="off" type="password" id="password" name="password" class="required login_lock" placeholder="密码"/>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<input  autocomplete="off" type="password" id="command" name="command" class="login_otp" placeholder="动态口令"/>
				</div>
			</div>
	<%-- 		<c:if test="${isValidateCodeLogin}"><div class="login_check"><div class="validateCode">
				<label class="input-label mid" for="validateCode">验证码</label>
				<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/></div>
			</div></c:if> --%>
			<c:if test="${isValidateCodeLogin}">
				<div class="login_check">
					<div class="validateCode" style="width: 400px;">
						<label for="password" style="color: #fff;" id="validateCode">验证码：</label>
						<sys:validateCode name="validateCode"
							inputCssStyle="margin-bottom:0;background-color:#fff;" />
					</div>
				</div>
			</c:if>
		</div>
		<div class="footer">
            <input class="login_btn" type="submit" value="登录"/>
        </div>
	</form>
	</div>
	</div>
	<div  class="white navbar-fixed-bottom login_footer" >
		<!--  Copyright &copy; 2017-${fns:getConfig('copyrightYear')} ${fns:getConfig('productName')} -->
		${fns:getConfig('author')}版权所有&nbsp;&nbsp;版本：${fns:getConfig('productName')}&nbsp${fns:getConfig('version')}
	</div>
</body>
</html>