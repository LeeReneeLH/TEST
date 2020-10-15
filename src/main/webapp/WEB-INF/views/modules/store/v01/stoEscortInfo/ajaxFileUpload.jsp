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
	var jcrop_api;
	$(document).ready(function() {
		if ($('#picPath').val() != '') {
			if (jcrop_api != undefined) {
				jcrop_api.destroy();
			}
			$('#targetImgDiv').html("<img id='target' src='" + $('#picPath').val() + "' >").show;
			initJcrop();
		}
		
		
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
				$("#submitForm").attr("action",	"${ctx}/store/v01/stoEscortInfo/backToList");
				$("#submitForm").submit();
			}
			return;
		});
	});
	function resetSubmitFlag() {
		submitFlag = false;
	}
	// 上传图片
	function ajaxFileUpload() {
		$("#messageBox").hide();
		$("#messageBox").removeClass();
		var filePath = $.trim($("#fileToUpload").val());
		
		// 效验文件不为空
		if(filePath.length > 0){
			// 效验文件类型
			var fileFormate = "${fns:getConfig('image.setting.import.format')}";
			var fileArr=filePath.split("\\");
			var fileTArr=fileArr[fileArr.length-1].toLowerCase().split(".");
			var filetype=fileTArr[fileTArr.length-1];
			if(fileFormate.indexOf(filetype) == -1){ 
				$("#messageBox").html("<spring:message code='message.E1042'/>").addClass("alert alert-error hide").show(); //检测允许的上传文件类型
				return; 
			}
		} else {
			$("#messageBox").html("<spring:message code='message.E1043'/>").addClass("alert alert-error hide").show(); //请选择要上传的图片
			return; 
		}
		
		$.ajaxFileUpload({
			url : '${ctx}/store/v01/stoEscortInfo/uploadImage?id='
					+ $("#id").val() + '&tmpPicFileName=' + $('#tmpPicFileName').val(),
			secureuri : false,
			fileElementId : 'fileToUpload',
			type : 'post',
			dataType : 'json',
			success : function(data, status) {
				if (jcrop_api != undefined) {
					jcrop_api.destroy();
				}
				$('#picPath').attr('value', '');
				$('#tmpPicFileName').attr('value', '');
				$('#targetImgDiv').html("").show;
				if (data.status == "success") {
					
					$('#targetImgDiv').html("<img id='target' src='" + data.picUrl + "' >").show;
					$('#picPath').attr('value', data.picUrl);
					$('#tmpPicFileName').attr('value', data.picFileName);
					
					initJcrop();
				} else {
					$("#messageBox").html(data.msg).addClass("alert alert-error hide").show(); 
				}
				
				
			},
			error : function(data, status, e) {
				//[上传失败]图片上传失败，请重新选择图片然后上传！
				$("#messageBox").html("<spring:message code='message.E1045'/>").addClass("alert alert-error hide").show(); 
			}
		});

		return false;

	}
	// 初始化剪裁工具
	function initJcrop() {
		// Create variables (in this scope) to hold the API and image size
	    var imageW = "${fns:getConfig('image.setting.weight')}";
	    var imageH = "${fns:getConfig('image.setting.height')}";
	    var imageBoxWidth = "${fns:getConfig('image.box.width')}";
	    var imageBoxHeight = "${fns:getConfig('image.box.height')}";

	    $('#target').Jcrop({
	      onChange: updatePreview,
	      onSelect: updatePreview,
	      aspectRatio: imageW / imageH, // 设定剪裁框形状
	      minSize : [ imageW, imageH ],
		  allowResize : true, // 不允许选框缩放
		  allowSelect : true,//允许新选框
		  boxWidth : imageBoxWidth, // 画布宽度
		  boxHeight : imageBoxHeight // 画布高度
		  
	    },function(){
	      jcrop_api = this;
	      jcrop_api.setImage($('#target').attr('src'));
	    });

	    function updatePreview(c)
	    {
	      if (parseInt(c.w) > 0)
	      {
	        $('#x').val(c.x);
			$('#y').val(c.y);
			$('#w').val(c.w);
			$('#h').val(c.h);
			
	      }
	    };
	}
</script>
<link rel="stylesheet" href="${ctxStatic}/Jcrop/css/jquery.Jcrop.css" type="text/css" />
</head>

<body>
	<sys:message content="${message}" />
	
		<div class="control-group">
			<h1>请选择图片点击上传，剪裁图片后点击保存。(<span class="help-inline"><font color="red">*.${fns:getConfig('image.setting.import.format')},${fns:getConfig('image.setting.weight')}*${fns:getConfig('image.setting.height')}</font> </span>)</h1>
			<div class="controls">
				
				<input id="fileToUpload" type="file" size="45" name="fileToUpload" />
				<input id="uploadBtn" class="btn" type="button" 
					value="<spring:message code='common.upload'/>" onclick="ajaxFileUpload()"/>
			</div>
		</div>
	
	<div class="row">
		<div class="control-group">
			<div style="width:${fns:getConfig('image.box.width') + 20}px;height:${fns:getConfig('image.box.height') + 20}px; ">
				<div class="controls" id="targetImgDiv"></div>
			</div>
		</div>
	</div>
	<div class="row">
		<form:form id="submitForm" modelAttribute="stoEscortInfo" method="post" class="form-horizontal" action="${ctx}/store/v01/stoEscortInfo/cutAndSaveImage">
			<form:hidden path="id"/>
			<form:hidden path="picPath"/>
			<form:hidden path="tmpPicFileName"/>
			<form:hidden path="x" />
			<form:hidden path="y" />
			<form:hidden path="w" />
			<form:hidden path="h" />
			
			<div class="control-group">
				<div class="controls">
					<input id="btnSubmit" class="btn btn-primary" type="button"	value="保存" />&nbsp;
					<input id="btnCancel" class="btn" type="button" 
					value="<spring:message code='common.return'/>" />
				</div>
			</div>
		</form:form>
	</div>
</body>
</html>