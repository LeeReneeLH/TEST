<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 上门收钞情况页面 -->
	<title><spring:message code='report.manageReport.collectMoneySituation'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/exportCollectMoneySituation");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/collectMoneySituation");		
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
		<shiro:hasPermission name="door:manageReport:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/collectMoneySituation" target="mainFrame"><spring:message code='report.manageReport.collectMoneySituation'/></a></li></shiro:hasPermission>
		<!-- 卡钞情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/stuckCollectSituation" target="mainFrame"><spring:message code='report.manageReport.stuckCollectSituation'/></a></li></shiro:hasPermission>
		<!-- 差错情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/errorCollectSituation" target="mainFrame"><spring:message code='report.manageReport.errorCollectSituation'/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="manageReport" action="${ctx}/report/v01/manageReport/collectMoneySituation" method="post" class="breadcrumb form-search">
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
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<!-- end -->
		</div>
		
		<!-- 结束日期 -->
		<div>
		<label>~</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${manageReport.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>&nbsp;
		</div>
		
		<%-- 门店 --%>
		<div style="display:none;">
		<label>仓库 ：</label>
		<sys:treeselect id="doorId" name="doorId"
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
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column c.name"><spring:message code="report.manageReport.doorName"/></th>
				<!-- 一月  -->
				<th class="sort-column January"><spring:message code="report.manageReport.January"/></th>
				<!-- 二月 -->
			    <th class="sort-column February"><spring:message code="report.manageReport.February"/></th>
				<!-- 三月 -->
				<th class="sort-column March"><spring:message code="report.manageReport.March"/></th>
				<!-- 四月-->
				<th class="sort-column April"><spring:message code="report.manageReport.April"/></th>
				<!-- 五月 -->
				<th class="sort-column May"><spring:message code="report.manageReport.May"/></th>
				<!-- 六月-->
				<th class="sort-column June"><spring:message code="report.manageReport.June"/></th>
				<!-- 七月 -->
				<th class="sort-column July"><spring:message code="report.manageReport.July"/></th>
				<!-- 八月 -->
				<th class="sort-column August"><spring:message code="report.manageReport.August"/></th>
				<!-- 九月 -->
				<th class="sort-column Septemper"><spring:message code="report.manageReport.Septemper"/></th>
				<!-- 十月 -->
				<th class="sort-column October"><spring:message code="report.manageReport.October"/></th>
				<!-- 十一月 -->
				<th class="sort-column November"><spring:message code="report.manageReport.November"/></th>
				<!-- 十二月 -->
				<th class="sort-column December"><spring:message code="report.manageReport.December"/></th>
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
						${manageReport.january}
					</td>
					<td>
						${manageReport.february}
					</td>
					
					<td>
						${manageReport.march}
					</td>
				
					<td>
						${manageReport.april}
					</td>
					<td>
						${manageReport.may}
					</td>
					<td>
						${manageReport.june}
					</td>
					<td>
						${manageReport.july}
					</td>
					<td>
						${manageReport.august}
					</td>
					<td>
						${manageReport.septemper}
					</td>
					<td>
						${manageReport.october}
					</td>
					<td>
						${manageReport.november}
					</td>
					<td>
						${manageReport.december}
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>