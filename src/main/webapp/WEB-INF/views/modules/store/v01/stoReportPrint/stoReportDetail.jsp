<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.boxCount" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript"
	src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnExport").click(function() {
			top.$.jBox.confirm("确认要导出数据吗？", "系统提示", function(v, h, f) {
				if (v == "ok") {
					$("#reportForm").submit();
				}
			}, {
				buttonsFocus : 1
			});
			top.$('.jbox-body .jbox-icon').css('top', '55px');
		});
	});
</script>
<style type="text/css">
table, th, tr, td {
	border: 1px solid #000;
	border-collapse: collapse;
}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/store/v01/stoReportPrint"><spring:message
					code="store.boxCount" /></a></li>
		<li class="active"><a href="${ctx}/store/v01/stoReportPrint/printDetail">详细列表</a></li>
	</ul>
	<form:form id="reportForm" method="post"
		action="${ctx}/store/v01/stoReportPrint/export"
		class="breadcrumb form-search">
		<%-- 打印（按钮）： --%>
		<input id="btnSubmit" class="btn btn-primary" type="button"
			value="<spring:message code="common.print" />" onclick="$('#printDiv').jqprint();"/> &nbsp;
				
		<%-- 导出（按钮）： --%>
		<input id="btnExport" class="btn btn-primary" type="button"
				value="导出" />
	</form:form>
	<div id="printDeailDiv">
		<link
			href="${ctxStatic}/bootstrap/2.3.1/css_${not empty cookie.theme.value ? cookie.theme.value:''}/bootstrap.min.css"
			type="text/css" rel="stylesheet" />
		<div id="printDiv" style="width: 100%;">
			<h3 align="center">钞箱使用情况统计表</h3>
			<div align="center">
				<table width="1065" height="306" border="1px" cellspacing="0px"
					id="contentTable" style="border-collapse: collapse">
					<caption align="left">
						<div align="left">
							<h4>制表机构：</h4>
						</div>
					</caption>
					<thead>
						<tr>
							<!-- 标题 -->
							<th><spring:message code="common.seqNo" /></th>
							<th>网点名称</th>
							<th>设备编号</th>
							<th>柜员号</th>
							<th>钞箱编号</th>
							<th>钞箱种别</th>
							<th>状态</th>
							<th>备注</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${test}" var="list" varStatus="status">
							<tr>
								<td>${status.index + 1}</td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
						</c:forEach>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
