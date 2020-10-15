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
	table,th,tr,td{border:1px solid #000; border-collapse:collapse;
	}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#" onClick="javascript:return false;"><spring:message
					code="store.boxCount" /></a></li>
	</ul>
	<form:form id="reportForm" method="post" action="${ctx}/store/v01/stoReportPrint/export"  class="breadcrumb form-search">
			<%-- 打印（按钮）： --%>
			<input id="btnSubmit" class="btn btn-primary" type="button"
				value="<spring:message code="common.print" />"
				onclick="$('#printDiv').jqprint();" /> &nbsp;
			<%-- 导出（按钮）： 
			 <input id="btnExport" class="btn btn-primary" type="button"
				value="<spring:message code='store.exportIntransit'/>" /> --%>
	</form:form>
	<div id="printDeailDiv">
		<link
			href="${ctxStatic}/bootstrap/2.3.1/css_${not empty cookie.theme.value ? cookie.theme.value:'default'}/bootstrap.min.css"
			type="text/css" rel="stylesheet" />
		<div id="printDiv" style="width: 100%;">
			
			<h3 align="center">
				<spring:message code="store.dayEndReport" />
		    </h3>
				<div align="center">
				  <spring:message code="store.reportDate" />
				：
				<fmt:formatDate value="${date}" pattern="yyyy-MM-dd" />
			    <table width="1065" border="1px" cellspacing="0px" id="contentTable" style="border-collapse:collapse">
				<thead>
					<tr>
						<!-- 标题 -->
						<th ><spring:message
								code="common.seqNo" /></th>
						<th><spring:message code="store.project" /></th>
						<th colspan=4><spring:message
								code="store.content" /></th>
					</tr>
				</thead>
				<tbody>
					<!-- 在途核对 -->
					<c:set var="countNum" value="${fn:length(reportMap.list)}"/>
					<c:forEach items="${reportMap.list}" var="list" varStatus="status">
							<tr height="70px;">
								<c:if test="${status.index+1==1}">
						  		<td rowspan="${fn:length(reportMap.list)}" style="width: 40px;"><div align="center">1</div></td>
								<td rowspan="${fn:length(reportMap.list)}" style="width: 160px;"><spring:message code="store.checkIntransit"/></td>
								</c:if>
								<td width="160px" ><div align="right"><spring:message code="${list.name}" />：</div></td>
								<td width="160px" ><div align="center">${list.num}</div></td>
								<td colspan=2 style="word-break:break-all; word-wrap:break-word; "><div align="left">&nbsp;${list.value}</div></td>
							</tr>
					</c:forEach>
					
					<!-- 在网点核对 -->
					<c:set var="countNum2" value="${fn:length(reportMap.outlets_list)}"/>
					<c:forEach items="${reportMap.outlets_list}" var="outlets_list" varStatus="status">
							<tr height="70px;">
								<c:if test="${status.index+1==1}">
								<c:if test="${countNum == 0}">
						  			<td rowspan="${fn:length(reportMap.outlets_list)}" style="width: 40px;"><div align="center">1</div></td>
						  		</c:if>
						  		<c:if test="${countNum != 0}">
						  			<td rowspan="${fn:length(reportMap.outlets_list)}" style="width: 40px;"><div align="center">2</div></td>
						  		</c:if>
								<td rowspan="${fn:length(reportMap.outlets_list)}" style="width: 160px;"><spring:message code="store.checkOutlets"/></td>
								</c:if>
								<td width="160px" ><div align="right"><spring:message code="${outlets_list.name}" />：</div></td>
								<td width="160px" ><div align="center">${outlets_list.num}</div></td>
								<td colspan=2 style="word-break:break-all; word-wrap:break-word; "><div align="left">&nbsp;${outlets_list.value}</div></td>
							</tr>
					</c:forEach>					
					
					<!-- 在库核对 -->					
					<tr height="70px;">
						<c:if test="${countNum == 0 && countNum2 == 0}">
						<td rowspan=1 style="width: 40px;"><div align="center">1</div></td>
						</c:if>
						<c:if test="${countNum != 0 && countNum2 == 0}">
						<td rowspan=1 style="width: 40px;"><div align="center">2</div></td>
						</c:if>
						<c:if test="${countNum == 0 && countNum2 != 0}">
						<td rowspan=1 style="width: 40px;"><div align="center">2</div></td>
						</c:if>
						<c:if test="${countNum != 0 && countNum2 != 0}">
						<td rowspan=1 style="width: 40px;"><div align="center">3</div></td>
						</c:if>
						<td rowspan=1 style="width: 160px;"><spring:message code="store.storeCheck" /></td>
					  <td width="160px" ><div align="right">
				        <spring:message
								code="store.cashBox" />
			          ：</div></td>
						<td width="160px" ><div align="center">${reportMap.cashBoxNum}</div></td>
					  <td width="160px" ><div align="right">
				        <spring:message
								code="store.trailBox" />
			          ：</div></td>
						<td width="160px" ><div align="center">${reportMap.trailBoxNum}</div></td>
					  <!--  <td width="100" ><div align="right">
				        <spring:message
								code="store.cashBag" />
			          ：</div></td>
						<td width="50" ><div align="center">${reportMap.cashBagNum}</div></td>
						<td width="200" ><div align="right"></div></td>
					    <td width="100" ><div align="center"></div></td>-->
					</tr>
					<!--  <tr>
					  <td width="100" ><div align="right">
				        <spring:message
								code="store.auditBag" />
			          ：</div></td>
						<td width="50" ><div align="center">${reportMap.auditBag}</div></td>
					  <td width="100" ><div align="right">
				        
				        <spring:message
								code="store.exchangeBag" />
			          ：</div></td>
						<td width="50" ><div align="center">${reportMap.changeBag}</div></td>
											    <td width="100" ><div align="right"></div></td>
					    <td width="50" ><div align="center"></div></td>
					</tr>
					<tr>
					  <td width="100" ><div align="right">
				        <spring:message
								code="store.importantEmptyDocument" />
			          ：</div></td>
						<td width="50" ><div align="center">${reportMap.emptyCertificates}</div></td>
					  <td width="100" ><div align="right">
				        <spring:message
								code="store.atmBox" />
			          ：</div></td>
						<td width="50" ><div align="center">${reportMap.atmBoxNum}</div></td>
					    <td width="100" ><div align="right"></div></td>
					    <td width="50" ><div align="center"></div></td>
					</tr>-->
					</tbody>
			    </table>
				</div>
		</div>
	</div>
</body>
</html>
