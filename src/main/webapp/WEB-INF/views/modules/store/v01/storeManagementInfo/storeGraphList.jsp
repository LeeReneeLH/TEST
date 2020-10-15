<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html style="overflow-x:scroll !important">
<head>
<title><spring:message code="store.actualArea" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">


</script>
<link href="${ctxStatic}/css/modules/store/v01/storeManagementInfo/gold-list.css" rel="stylesheet" />
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库房管理 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">库房管理</a></li>
		<%-- 库房箱袋列表 --%>
		<li><a href="${ctx}/store/v01/storeGoodsInfo/list">库房箱袋列表</a></li>
		<shiro:hasPermission name="store:storeManagementInfo:edit">
			<%-- 库房管理列表 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/">库房管理列表</a></li>
			<%-- 库房创建 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/toCreateStorePage">库房创建</a></li>
		</shiro:hasPermission>
	</ul>
	<br />
	<div class="gold-list">
		<sys:message content="${message}"/>
		<ul>
			<c:forEach items="${storeManagementInfoList}" var="info" >
				<li><a href="${ctx}/store/v01/storeGoodsInfo/list?storeId=${info.id }"><span></span>${info.storeName }</a></li>
			</c:forEach>
		</ul>
	</div>
</body>
</html>
