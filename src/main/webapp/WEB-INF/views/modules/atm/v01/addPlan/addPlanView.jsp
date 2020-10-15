<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='title.atm.plan.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//setToday(".createDate");		
		});
	</script>
	<style type="text/css">
	table,th,tr,td{border:1px solid #000; border-collapse:collapse;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/atm/v01/atmPlanInfo/importFile"><spring:message code='label.atm.addPlan.menu.01'/></a></li>
		<li ><a href="${ctx}/atm/v01/atmPlanInfo/list"><spring:message code='label.atm.addPlan.menu.02'/></a></li>
		<li class="active"><a href="###"><spring:message code='label.atm.addPlan.menu.03'/></a></li>
	</ul>
	<sys:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="atmPlanInfo" action="${ctx}/atm/v01/atmPlanInfo/addPlanView/" method="post" class="breadcrumb form-search">
		<!-- 打印 -->
		<input id="printBtn" onClick="$('#printDiv').jqprint();" type="button" class="btn btn-primary" value="<spring:message code='common.print'/>" />
	</form:form>
	<jsp:include page="addPlanPrint.jsp"></jsp:include>
	<!-- 添加返回按钮	by：wxz 2017-12-19 -->
	<div class="form-actions" style="width:100%;">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>&nbsp;
	</div>
	<!-- end -->
</body>
</html>