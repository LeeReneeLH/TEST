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
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyUseRecords/export");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyUseRecords/");		
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
    <!-- 历史使用记录 -->
    <li class="active"><a><spring:message code="door.historyUseRecords.list" /></a></li>
</ul>
<form:form id="searchForm" modelAttribute="historyUseRecords" action="${ctx}/doorOrder/v01/historyUseRecords/" method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
    <div class="row search-flex">
        <!-- <div class="span14" style="margin-top:5px;"> -->
        <div>
        	<%-- 机具编号 --%>
            <label><spring:message code="door.equipment.no" />：</label>
            <form:input path="seriesNumber" htmlEscape="false" maxlength="15" class="input-small"/>
        </div>
        <div>
            <%-- 钞袋号 --%>
            <label><spring:message code="door.historyUseRecords.bagNo" />：</label>
            <form:input path="bagNo" htmlEscape="false" maxlength="15" class="input-small"/>
        </div>
        <div>
            <%-- 硬币盒号 --%>
            <%-- <label><spring:message code="door.historyUseRecords.coinBoxNo" />：</label>
            <form:input path="coinBoxNo" htmlEscape="false" maxlength="15" class="input-small"/> --%>
			<%-- 开始日期 --%>
			<label><spring:message code="door.historyUseRecords.timeStart" />：</label>
			<input 	id="createTimeStart"  
					name="createTimeStart" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${historyUseRecords.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
        </div>
        <div>
			<%-- 结束日期 --%>
			<label>~</label>
			<input 	id="createTimeEnd" 
					name="createTimeEnd" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${historyUseRecords.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
        </div>
        <div>
             <%-- 所属公司 --%>
            <label><spring:message code="door.historyUseRecords.office" />：</label>
            <sys:treeselect id="aOffice" name="aOffice.id"
                    value="${historyUseRecords.aOffice.id}" labelName="officeName"
                    labelValue="${historyUseRecords.officeName}" title="<spring:message code='door.public.cust' />"
                    url="/sys/office/treeData" cssClass="required input-small"
                    notAllowSelectParent="true" notAllowSelectRoot="false" minType="8" maxType="9"
                    isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
        </div>
        &nbsp;&nbsp;
        <div>
            <%-- 查询 --%>
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
        </div>
        &nbsp;&nbsp;
        <div>
			<!-- 导出 -->
			<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
        </div>
    </div>
</form:form>
<sys:message content="${message}"/>
<div class="table-con">
<table id="contentTable" class="table table-hover">
    <thead>
    <tr>
        <%-- 序号 --%>
        <th><spring:message code="common.seqNo" /></th>
        <%-- 机具编号 --%>
        <th class="sort-column e.SERIES_NUMBER"><spring:message code="door.equipment.no" /></th>
        <%-- 区域 --%>
        <th class="sort-column so2.NAME"><spring:message code="door.historyUseRecords.area" /></th>
        <%-- 钞袋号 --%>
        <th class="sort-column doi.RFID"><spring:message code="door.historyUseRecords.bagNo" /></th>
        <%-- 硬币盒号 --%>
        <%-- <th><spring:message code="door.historyUseRecords.coinBoxNo" /></th> --%>
        <%-- 纸币数量 --%>
        <th class="sort-column dod.paperCount"><spring:message code="door.historyUseRecords.paperCount" /></th>
        <%-- 纸币金额 --%>
        <th class="sort-column paperAmount"><spring:message code="door.historyUseRecords.paperAmount" /></th>
        <%-- 硬币数量 --%>
        <%-- <th><spring:message code="door.historyUseRecords.coinCount" /></th> --%>
        <%-- 硬币金额 --%>
        <%-- <th><spring:message code="door.historyUseRecords.coinAmount" /></th> --%>
        <%-- 强制金额 --%>
        <th class="sort-column dod.forceAmount"><spring:message code="door.historyUseRecords.forceAmount" /></th>
        <%-- 其他金额 --%>
        <th class="sort-column dod.otherAmount"><spring:message code="door.historyUseRecords.otherAmount" /></th>
        <%-- 存入总量 --%>
        <th class="sort-column dod.totalCount"><spring:message code="door.historyUseRecords.count" /></th>
        <%-- 总金额 --%>
        <th class="sort-column dod.AMOUNT"><spring:message code="door.historyUseRecords.amount" /></th>
        <%-- 替换款袋时间 --%>
        <th class="sort-column c.changeDate"><spring:message code="door.historyChange.changeDate" /></th>
        <%-- 上次替换款袋时间 --%>
        <th class="sort-column c.lastChangeDate"><spring:message code="door.historyUseRecords.lastChangeDate" /></th>
        <%-- 所属公司 --%>
        <th class="sort-column do.NAME"><spring:message code="door.historyUseRecords.office" /></th>
        <%-- 操作 --%>
        <th><spring:message code='common.operation'/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="HistoryUseRecords" varStatus="status">
        <tr>
            <td>${status.index + 1}</td>
            <td>${HistoryUseRecords.seriesNumber}</td>
            <td>${HistoryUseRecords.countyName}</td>
            <td>${HistoryUseRecords.bagNo}</td>
            <%-- <td>${HistoryUseRecords.bagNo}</td> --%>
            <td>${HistoryUseRecords.paperCount}</td>
            <td style="text-align:right;"><fmt:formatNumber value="${HistoryUseRecords.paperAmount}" type="currency" pattern="#,##0.00"/></td>
            <%-- <td>0</td>
            <td>0.00</td>  --%>
            <td style="text-align:right;"><fmt:formatNumber value="${HistoryUseRecords.forceAmount}" type="currency" pattern="#,##0.00"/></td>
            <td style="text-align:right;"><fmt:formatNumber value="${HistoryUseRecords.otherAmount}" type="currency" pattern="#,##0.00"/></td>
            <td>${HistoryUseRecords.count}</td>
            <td style="text-align:right;"><fmt:formatNumber value="${HistoryUseRecords.amount}" type="currency" pattern="#,##0.00"/></td>
            <td><fmt:formatDate value="${HistoryUseRecords.changeDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td><fmt:formatDate value="${HistoryUseRecords.lastChangeDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>${HistoryUseRecords.officeName}</td>
            <td>
				<span style='width:25px;display:inline-block;'>
					<%-- 存款明细 --%>
					<a href="${ctx}/doorOrder/v01/historyUseRecords/detailList?equipmentId=${HistoryUseRecords.id}&bagNo=${HistoryUseRecords.bagNo}&orderId=${HistoryUseRecords.orderId}" 
					   title="<spring:message code='door.historyUseRecords.detail' />">
						<i class="fa fa-eye fa-lg"></i>
					</a>
				</span>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
<div class="pagination">${page}</div>
</body>
</html>