<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code="door.equipment.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("span:first").trigger("click");
		});
	</script>
</head>
<body>
	<ul style="display: none;">
		<!-- 差错列表 -->
		<shiro:hasPermission name="doororder:doorErrorInfo:view"><li><a href="${ctx}/doorOrder/v01/doorErrorInfo/merchantList"><span><spring:message code="door.errorInfo.merchantList" /></span></a></li></shiro:hasPermission>
	    <shiro:hasPermission name="doororder:doorErrorList:view"><li><a href="${ctx}/doorOrder/v01/doorErrorInfo/doorList"><span><spring:message code="door.errorInfo.doorList" /></span></a></li></shiro:hasPermission>
	    <shiro:hasPermission name="doororder:doorErrorDetailList:view"><li><a href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList"><span><spring:message code="door.errorInfo.doorDetailList" /></span></a></li></shiro:hasPermission>
	</ul>
</body>
</html>