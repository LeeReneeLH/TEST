<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务日结管理 -->
	<title><spring:message code='clear.dayReportMain.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/exportErrorCollectSituation");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/report/v01/manageReport/errorCollectSituation");		
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
		<shiro:hasPermission name="door:manageReport:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/collectMoneySituation" target="mainFrame"><spring:message code='report.manageReport.collectMoneySituation'/></a></li></shiro:hasPermission>
		<!-- 卡钞情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/stuckCollectSituation" target="mainFrame"><spring:message code='report.manageReport.stuckCollectSituation'/></a></li></shiro:hasPermission>
		<!-- 差错情况 -->
		<shiro:hasPermission name="door:manageReport:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/report/v01/manageReport/errorCollectSituation" target="mainFrame"><spring:message code='report.manageReport.errorCollectSituation'/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="manageReport" action="${ctx}/report/v01/manageReport/errorCollectSituation" method="post" class="breadcrumb form-search">
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
		</div>
		<%-- 门店 --%>
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
	<div id="box_sel" class="row" style="margin-left:10px;">
		<div class="boxhand-num text-center">
			<h4 style="font-weight: normal" class="text-purple"><spring:message code="report.manageReport.total" /></h4>
		<%-- <a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}"><span class="text-blue"><fmt:formatNumber value="${total ne null? total:0}" /></span></a> --%>
		<span class="purple-bg"><fmt:formatNumber value="${total ne null? total:0}" /></span>
		</div>
		<div class="boxhand-num text-center">
			<h4 style="font-weight: normal" class="text-aqua"><spring:message code="report.manageReport.longMoneyTotal" /></h4>
			<a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&errorType=2&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}"><span class="blue-bg"><fmt:formatNumber value="${longMoney ne null? longMoney:0}" pattern="#,##0.00#" /></span></a>
		</div>
		<div class="boxhand-num text-center">
			<h4 style="font-weight: normal" class="text-red"><spring:message code="report.manageReport.shortMoneyTotal" /></h4>
			<a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&errorType=3&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}""><span class="red-bg"><fmt:formatNumber value="${shortMoney ne null? shortMoney:0}" pattern="#,##0.00#" /></span></a>
		</div>		
	</div>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column doorName"><spring:message code="report.manageReport.doorName"/></th>
				<!-- 笔数  -->
				<th class="sort-column saveCount"><spring:message code="report.manageReport.count"/></th>
				<!-- 长款金额  -->
				<th class="sort-column longMoney"><spring:message code="report.manageReport.longMoney"/></th>
				<!-- 短款金额  -->
				<th class="sort-column shortMoney"><spring:message code="report.manageReport.shortMoney"/></th>
				<!-- 合计  -->
				<th class="sort-column totalMoney"><spring:message code="report.manageReport.totalMoney"/></th>
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
						<%-- 笔数    add: ZXK --%>
						<%-- <a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}">${manageReport.saveCount}</a> --%>
					    ${manageReport.saveCount}
					</td>
					<td>
						<%-- 长款金额  超链接  add: ZXK --%>
						<a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&errorType=2&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}">${manageReport.longMoney}</a>
					</td>
					<td>
						<%-- 短款金额  超链接  add: ZXK --%>
						<a href="${ctx}/doorOrder/v01/doorErrorInfo/errorDetail?custNo=${manageReport.doorId}&errorType=3&searchDateStart=${createTimeStart}&searchDateEnd=${createTimeEnd}">${manageReport.shortMoney}</a>
					</td>
					<!-- 金额颜色判定 : 负为红  正为绿-->
					<td class="${fn:contains(manageReport.totalMoney,'-') ? 'text-red' : 'text-green'} ">
					    ${manageReport.totalMoney}
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
  <div class="pagination">${page}</div>
</body>
</html>