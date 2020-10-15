<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
	});
</script>
<style>
.like-con{    margin: 30px;
    text-align: center;
    display: inline-block;
    vertical-align: middle;
    width: 100px;
    height: 100px;
    border-radius: 50%;
  
    line-height: 30px;
    box-sizing: border-box;
    padding-top: 20px;
    background: #eee;}
</style>
</head>
<body>
	<!-- 通过身份证获取人员照片信息 -->
	<div class="control-group">
		<div style="padding-left: 72px; padding-top: 28px">
			<img
				src="${ctx}/cloudPlateform/v04/visitorStatistics/showVisitorImage?visitorId=${eyeCheckVisitorInfo.visitorId}" 
				style="height: 130px; width: 107px;border:1px solid #ccc;padding:5px;background:#f2f2f2" />
				 <span
				class="like-con"><label
				class="control-label"><spring:message
						code="common.similarity" /></label><br> <span style=" font-size: 24px;   font-weight: bold;
    color: #2883f9;">${eyeCheckVisitorInfo.similarity}</span></span> 
			<img src="${ctx}/cloudPlateform/v04/visitorStatistics/showEscortImage?escortIdCard=${eyeCheckVisitorInfo.idcardNo}&officeId=${eyeCheckVisitorInfo.office.id}"
				style="height: 130px; width: 107px;border:1px solid #ccc;padding:5px;background:#f2f2f2" />
		</div>
	</div>
	<br />

	<!--第一行，姓名和性别  -->
	<div class="control-group" style="float: left; padding-left: 72px">
		<span style="    width: 200px;    display: inline-block;"><label class="control-label" style="color:#999"><spring:message
					code="common.name" />：</label> ${eyeCheckVisitorInfo.escortName}</span> <span
			style="padding-left: 83px"><label class="control-label" style="color:#999"><spring:message
					code="common.identityGender" />：</label>
			${eyeCheckVisitorInfo.identityGender}</span>
	</div>
	<br />
	<br />

	<!--第二行，身份证和年龄  -->
	<div class="control-group" style="float: left; padding-left: 72px">
		<span style="    width: 200px;    display: inline-block;"><label class="control-label" style="color:#999"><spring:message
					code="common.idcardNo" />：</label> ${eyeCheckVisitorInfo.idcardNo}</span> <span
			style="padding-left: 82px"><label class="control-label" style="color:#999"><spring:message
					code="common.age" />：</label> ${eyeCheckVisitorInfo.age}</span>
	</div>
</body>
</html>