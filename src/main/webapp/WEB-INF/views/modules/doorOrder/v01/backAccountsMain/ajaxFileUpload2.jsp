<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>上传图片</title>
<script type="text/javascript"
	src="${ctxStatic}/ajaxFileUpload/ajaxfileupload.js"></script>
<script src="${ctxStatic}/Jcrop/js/jquery.Jcrop.js"></script>
<script type="text/javascript">
	var submitFlag;
	
	$(document).ready(function() {
		
		$("#fileToUpload").change(function(){
			change();
			
		});
		
		resetSubmitFlag();
		// 保存按钮
		$("#btnSubmit").click(function() {
			if (submitFlag != true) {
				submitFlag = true;
				$("#submitForm").submit();
			}
			return;
		});
		// 返回按钮
		$("#btnCancel").click(function() {
			if (submitFlag != true) {
				submitFlag = true;
				$("#submitForm").attr("action",	"${ctx}/doorOrder/v01/backAccountsMain/backToList");
				$("#submitForm").submit();
			}
			return;
		});
		
	});
	
	function resetSubmitFlag() {
		submitFlag = false;
	}
	
	function change() {
		
		var filePath = $.trim($("#fileToUpload").val());
		
		// 效验文件不为空
		if(filePath.length > 0){
			// 效验文件类型
			var fileFormate = "${fns:getConfig('image.setting.import.format')}";
			var fileArr=filePath.split("\\");
			var fileTArr=fileArr[fileArr.length-1].toLowerCase().split(".");
			var filetype=fileTArr[fileTArr.length-1];
			if(fileFormate.indexOf(filetype) == -1){ 
				//$("#messageBox").html("<spring:message code='message.E1042'/>").addClass("alert alert-error hide").show(); //检测允许的上传文件类型
				alertx("<spring:message code='message.E1042'/>");
				$("#fileToUpload").val('');
				return; 
			}
		} else {
			//$("#messageBox").html("<spring:message code='message.E1043'/>").addClass("alert alert-error hide").show(); //请选择要上传的图片
			alertx("<spring:message code='message.E1043'/>");
			$("#fileToUpload").val('');
			return; 
		}
		
		var pic = document.getElementById("target"); 
		var file = document.getElementById("fileToUpload");

		var isIE = navigator.userAgent.match(/MSIE/) != null, isIE6 = navigator.userAgent
				.match(/MSIE 6.0/) != null;

		if (isIE) {
			
			file.select();
			var reallocalpath = document.selection.createRange().text;

			// IE6浏览器设置img的src为本地路径可以直接显示图片
			if (isIE6) {
				pic.src = reallocalpath;
			} else {
				// 非IE6版本的IE由于安全问题直接设置img的src无法显示本地图片，但是可以通过滤镜来实现
				pic.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src=\""
						+ reallocalpath + "\")";
				// 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
				pic.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
			}
		} else {
			html5Reader(file);
		}

		AutoResizeImage(pic);
	}

	function html5Reader(file) {
		var file = file.files[0];
		var reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = function(e) {
			var pic = document.getElementById("target");
			pic.src = this.result;
		}
	}
	
	
	function AutoResizeImage(objImg) {
		
		$("#target").css("height", objImg.offsetHeight + "px");
		$("#target").css("width", objImg.offsetWidth + "px");

	}
</script>
<link rel="stylesheet" href="${ctxStatic}/Jcrop/css/jquery.Jcrop.css" type="text/css" />
</head>

<body>
	<sys:message content="${message}" />
	<div class="row">
		<form:form id="submitForm" modelAttribute="backAccountsMain" method="post"
			class="form-horizontal" enctype="multipart/form-data" 
			action="${ctx}/doorOrder/v01/backAccountsMain/uploadAndSaveImage">
			<%-- 请选择小于1M的图片后，点击保存。 --%>
			<h1>
				<spring:message code="message.E1070"/>(<span class="help-inline"><font
					color="red">*.${fns:getConfig('image.setting.import.format')}</font>
				</span>)
			</h1>

			<input id="fileToUpload" type="file" accept="image/*"	name="fileToUpload"  />&nbsp; 
			<input id="btnSubmit" class="btn btn-primary" type="button" value="上传" />&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" />

			<form:hidden path="id" />
			<form:hidden path="picPath" />
			<form:hidden path="tmpPicFileName" />
			<form:hidden path="x" />
			<form:hidden path="y" />
			<form:hidden path="w" />
			<form:hidden path="h" />
		</form:form>
	</div>
	
	<div id="targetImgDiv">
		<img id="target" src="${ctx}/doorOrder/v01/backAccountsMain/showImage?id=${backAccountsMain.id}" >
	</div>
			
</body>
</html>