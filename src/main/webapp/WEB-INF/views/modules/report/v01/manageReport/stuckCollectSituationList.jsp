<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 卡钞情况页面 -->
	<title><spring:message code='report.manageReport.stuckCollectSituation'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$(document).ready(function() {
				$("#exportSubmit").on('click',function(){
					$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/exportStuckCollectSituation");
					$("#searchForm").submit();
					$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/stuckCollectSituation");		
				});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 上门收钞情况 -->
		<shiro:hasPermission name="door:manageReport:view" ><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/collectMoneySituation/" target="mainFrame"><spring:message code='report.manageReport.collectMoneySituation'/></a></li></shiro:hasPermission>
		<!-- 卡钞情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/stuckCollectSituation/" target="mainFrame"><spring:message code='report.manageReport.stuckCollectSituation'/></a></li></shiro:hasPermission>
		<!-- 差错情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/errorCollectSituation/" target="mainFrame"><spring:message code='report.manageReport.errorCollectSituation'/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="manageReport" action="${ctx}/report/v01/manageReport/stuckCollectSituation" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<div class="row search-flex">
		<!-- 开始日期 -->
		<div>
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		value="<fmt:formatDate value="${manageReport.createTimeStart}" pattern="yyyy-MM-dd"/>" 
		onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		</div>
		<!-- 结束日期 -->
		<div>
		<label>~</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		value="<fmt:formatDate value="${manageReport.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>&nbsp;
		<%-- 门店 --%>
		</div>
		<div style="display:none;">
		<label>仓库 ：</label>
		<sys:treeselect id="doorName" name="doorId"
			value="${manageReport.doorId}" labelName="doorName"
			labelValue="${manageReport.doorName}" title="<spring:message code='door.public.cust' />"
			url="/sys/office/treeData" cssClass="required input-small"
			notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
			isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
		</div>
		
		<div>
		<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
		</div>
		
		<!-- 导出 -->
		<div class="control-group">
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
		</div>
	</div>
	</form:form>
	<sys:message content="${message}"/>
<!-- 	<div style="overflow-y: auto; height: 600px;"> -->
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column d.`NAME`"><spring:message code="report.manageReport.doorName"/></th>
				<!-- 卡钞次数  -->
				<th class="sort-column stuckCount"><spring:message code="report.manageReport.stuckCount"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="manageReport" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${manageReport.doorName}
					</td>
					<td>
						${manageReport.stuckCount}
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
  <div class="pagination">${page}</div>
</body>
</html>