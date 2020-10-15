<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title><spring:message code="door.historyUseRecords.list" /></title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
    <%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
    $(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositCensusReport/export");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositCensusReport/");		
		});
	});
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <!-- 存款情况分析列表 -->
    <li class="active"><a><spring:message code="door.depositCensusReport.list" /></a></li>
</ul>
<form:form id="searchForm" modelAttribute="depositCensusReport" action="${ctx}/doorOrder/v01/depositCensusReport/" method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
    <!-- <div class="row"> -->
       <!--  <div class="span14" style="margin-top:5px;"> -->
       <div class="row search-flex">
            <%-- 门店 --%>
            <div style="display:none;">
            <label><spring:message code="door.depositCensusReport.door" />：</label>
            <sys:treeselect id="office" name="doorId"
                            value="${depositCensusReport.doorId}" labelName="office.name"
                            labelValue="${depositCensusReport.office.name}" title="<spring:message code='door.public.cust' />"
                            url="/sys/office/treeData" cssClass="required input-small"
                            notAllowSelectParent="true" notAllowSelectRoot="false" minType="8" maxType="9"
                            isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
			</div>
			<%-- 开始日期 --%>
			 <div>
			<label><spring:message code="common.startDate" />：</label>
			<input 	id="createTimeStart"  
					name="createTimeStart" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${depositCensusReport.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			 </div>
			<%-- 结束日期 --%>
			 <div>
			<label>~</label>
			<input 	id="createTimeEnd" 
					name="createTimeEnd" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${depositCensusReport.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
            </div>
            &nbsp;&nbsp;
            <%-- 查询 --%>
             <div>
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
			&nbsp;&nbsp;
			<!-- 导出 -->
			 <div>
			<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
             </div>
     </div>
      <!--   </div> -->
    <!-- </div> -->
</form:form>
<sys:message content="${message}"/>
<div class="table-con">
<table id="contentTable" class="table table-hover">
    <thead>
    <tr>
		<%-- 序号--%>
		<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.seqNo" /></th>
		<%-- 结算办--%>
		<%-- <th class="sort-column o.settle_office" rowspan="2" style="text-align: center; line-height:80px;font-size:17px" ><spring:message code="door.depositCensusReport.settleOffice" /></th> --%>
		<%-- 门店--%>
		<th class="sort-column o.NAME" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositCensusReport.door" /></th>
		<%-- 存款天数--%>
		<th class="sort-column main.depositDays" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositCensusReport.depositDays" /></th>
		<%-- 存款人数--%>
		<th class="sort-column main.depositors" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositCensusReport.depositors" /></th>
		<%-- 存款总额--%>
		<th class="sort-column totalAmount" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositCensusReport.totalAmount" /></th>
		<%-- 存款次数--%>
		<th class="sort-column main.totalCount" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositCensusReport.totalCount" /></th>
		<%-- 速存存款--%>
		<th  colspan="4" style="text-align: center;"><spring:message code="door.depositCensusReport.paper" /></th>
		<%-- 强制存款--%>
		<th  colspan="4" style="text-align: center;"><spring:message code="door.depositCensusReport.force" /></th>
		<%-- 每天平均存款--%>
		<th  colspan="2" style="text-align: center;"><spring:message code="door.depositCensusReport.average" /></th>
	</tr>
	<tr>
		<%-- 金额--%>
		<th class="sort-column paperAmount" style="text-align: center;"><spring:message code="door.depositCensusReport.amount" /></th>
		<%-- 占比--%>
		<th class="sort-column paperAmountPercent" style="text-align: center;"><spring:message code="door.depositCensusReport.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column paperCount" style="text-align: center;"><spring:message code="door.depositCensusReport.times" /></th>
		<%-- 占比--%>
		<th class="sort-column paperCountPercent" style="text-align: center;"><spring:message code="door.depositCensusReport.percent" /></th>
		<%-- 金额--%>
		<th class="sort-column forceAmount" style="text-align: center;"><spring:message code="door.depositCensusReport.amount" /></th>
		<%-- 占比--%>
		<th class="sort-column forceAmountPercent" style="text-align: center;"><spring:message code="door.depositCensusReport.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column forceCount" style="text-align: center;"><spring:message code="door.depositCensusReport.times" /></th>
		<%-- 占比--%>
		<th class="sort-column forceCountPercent" style="text-align: center;"><spring:message code="door.depositCensusReport.percent" /></th>
		<%-- 金额--%>
		<th class="sort-column aveAmount" style="text-align: center;"><spring:message code="door.depositCensusReport.amount" /></th>
		<%-- 次数--%>
		<th class="sort-column aveCount" style="text-align: center;"><spring:message code="door.depositCensusReport.times" /></th>
	</tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="DepositCensusReport" varStatus="status">
        <tr style="color:${status.index eq 0 ? 'green':''}">
        	<c:if test="${status.index eq 0}"><td></td></c:if>
        	<c:if test="${status.index ne 0}"><td>${status.index}</td></c:if>
            <%-- <td>${DepositCensusReport.settleOffice}</td> --%>
            <td>${DepositCensusReport.doorName}</td>
            <td>${DepositCensusReport.depositDays}</td>
            <td>${DepositCensusReport.depositors}</td>
            <td style="text-align:right;"><fmt:formatNumber value="${DepositCensusReport.totalAmount}" type="currency" pattern="#,##0.00"/></td>
            <td>${DepositCensusReport.totalCount}</td>
            <td style="text-align:right;"><fmt:formatNumber value="${DepositCensusReport.paperAmount}" type="currency" pattern="#,##0.00"/></td>
            <td><fmt:formatNumber value="${DepositCensusReport.paperAmountPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${DepositCensusReport.paperCount}</td>
            <td><fmt:formatNumber value="${DepositCensusReport.paperCountPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td style="text-align:right;"><fmt:formatNumber value="${DepositCensusReport.forceAmount}" type="currency" pattern="#,##0.00"/></td>
            <td><fmt:formatNumber value="${DepositCensusReport.forceAmountPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${DepositCensusReport.forceCount}</td>
            <td><fmt:formatNumber value="${DepositCensusReport.forceCountPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td style="text-align:right;"><fmt:formatNumber value="${DepositCensusReport.aveAmount}" type="currency" pattern="#,##0.00"/></td>
            <td>${DepositCensusReport.aveCount}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
<div class="pagination">${page}</div>
</body>
</html>