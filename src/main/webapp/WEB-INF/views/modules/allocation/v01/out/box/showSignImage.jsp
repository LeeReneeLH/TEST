<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv" >
		<div style="text-align: center">
			<%-- 交接人员签名信息 --%>
			<h3><spring:message code="allocation.handover.sign.pic" /></h3>
		</div>
		<hr style="margin: 10px;"/>
		<div>
			<label>
				<spring:message
						code='allocation.allId' />：${allocation.allId}
			</label>
		</div>
		<div>
			<label>
				<spring:message
						code='common.name' />：${handoverUserName}
			</label>
		</div>
		<hr style="margin: 10px;"/>
		<div style="width:300px; height:100px;">
			<img src="${ctx}/allocation/v01/boxHandover/showImage?allId=${allocation.allId}&signNum=${allocation.signNum}&signType=${allocation.signType}"
			 height="100%" width="100%"/>
		</div>
	</div>
</body>
</html>
