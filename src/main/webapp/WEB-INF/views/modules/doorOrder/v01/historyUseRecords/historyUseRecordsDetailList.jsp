<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title><spring:message code="door.historyUseRecords.list" /></title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
    $(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyUseRecords/exportDetail");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/historyUseRecords/detailList");		
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
    <li><a href="${ctx}/doorOrder/v01/historyUseRecords/"><spring:message code="door.historyUseRecords.list" /></a></li>
    <!-- 存款明细列表 -->
    <li class="active"><a><spring:message code="door.historyUseRecords.historyUseDetail" /></a></li>
</ul>
<form:form id="searchForm" modelAttribute="historyUseRecordsDetail" action="${ctx}/doorOrder/v01/historyUseRecords/detailList" method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
    <div class="row search-flex">
        <!-- <div class="span14" style="margin-top:5px;"> -->
	        <form:hidden path="bagNo"/>
	        <form:hidden path="equipmentId"/>
	        <form:hidden path="orderId"/>
        <div>
            <%-- 结算批次 --%>
            <label><spring:message code="door.historyUseRecords.depositBatches" />：</label>
            <form:input path="depositBatches" htmlEscape="false" maxlength="32" class="input-small"/>
        </div>
        <div>
            <%-- 开始日期 --%>
			<label><spring:message code="door.historyUseRecords.depositTime" />：</label>
			<input 	id="createTimeStart"  
					name="createTimeStart" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${historyUseRecordsDetail.createTimeStart}" pattern="yyyy-MM-dd"/>" 
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
					value="<fmt:formatDate value="${historyUseRecordsDetail.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
        </div>
        <div>
            <%-- 店员 --%>
            <label><spring:message code="door.historyUseRecords.clerkName" />：</label>
            <form:input path="clerkName" htmlEscape="false" maxlength="15" class="input-small"/>
        </div>
        <div>
            <%-- 存款方式 --%>
            <label><spring:message code="door.historyUseRecords.type" />：</label>
            <form:select path="saveMethodValue" class="input-medium required" id ="selectStatus">
                <option value=""><spring:message code="common.select" /></option>
                <form:options items="${fns:getDictList('save_method')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
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
        &nbsp;&nbsp;
        <div>
        	<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
				onclick="history.go(-1)"/>
				<%-- onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/> --%>
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
        <%-- 结算批次 --%>
        <%-- <th class="sort-column d.ORDER_ID"><spring:message code="door.historyUseRecords.settleBatches" /></th> --%>
        <%-- 存款批次 --%>
        <th class="sort-column d.tickertape"><spring:message code="door.historyUseRecords.depositBatches" /></th>
        <%-- 存入时间--%>
        <th class="sort-column d.CREATE_DATE"><spring:message code="door.historyUseRecords.depositTime" /></th>
        <%-- 店员 --%>
        <th class="sort-column u.LOGIN_NAME"><spring:message code="door.historyUseRecords.clerk" /></th>
        <%-- 店员姓名 --%>
        <th class="sort-column u.`NAME`"><spring:message code="door.historyUseRecords.clerkName" /></th>
        <%-- 机具编号 --%>
        <th class="sort-column e.series_number"><spring:message code="door.equipment.no" /></th>
        <%-- 区域 --%>
        <th class="sort-column pr.NAME"><spring:message code="door.historyUseRecords.area" /></th>
        <%-- 款项日期 --%>
        <th class="sort-column d.CREATE_DATE"><spring:message code="door.historyUseRecords.depositDate" /></th>
        <%-- 业务类型 --%>
        <th class="sort-column s.LABEL"><spring:message code="door.historyUseRecords.type" /></th>
        <%-- 币种 --%>
        <th><spring:message code="door.historyUseRecords.currency" /></th>
        <%-- 100 --%>
        <th><spring:message code="door.historyUseRecords.currency100" /></th>
        <%-- 50 --%>
        <th><spring:message code="door.historyUseRecords.currency50" /></th>
        <%-- 20 --%>
        <th><spring:message code="door.historyUseRecords.currency20" /></th>
        <%-- 10 --%>
        <th><spring:message code="door.historyUseRecords.currency10" /></th>
        <%-- 5 --%>
        <th><spring:message code="door.historyUseRecords.currency5" /></th>
        <%-- 1 --%>
        <th><spring:message code="door.historyUseRecords.currency1" /></th>
        <%-- 纸币金额 --%>
        <th><spring:message code="door.historyUseRecords.paperAmount" /></th>
        <%-- 硬币金额 --%>
        <%-- <th><spring:message code="door.historyUseRecords.coinAmount" /></th> --%>
        <%-- 强制金额 --%>
        <th><spring:message code="door.historyUseRecords.forceAmount" /></th>
        <%-- 其他金额 --%>
        <th><spring:message code="door.historyUseRecords.otherAmount" /></th>
        <%-- 总金额 --%>
        <th><spring:message code="door.historyUseRecords.amount" /></th>
        <%-- 所属公司 --%>
        <th class="sort-column do.`NAME`"><spring:message code="door.historyUseRecords.office" /></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="historyUseRecordsDetail" varStatus="status">
        <tr>
            <td>
            <c:if test="${status.index eq 0}"></c:if>
            <c:if test="${status.index ne 0}">
                ${status.index}
            </c:if>
            </td>
            <%-- <td>${historyUseRecordsDetail.settleBatches}</td> --%>
            <td>${historyUseRecordsDetail.depositBatches}</td>
            <td><fmt:formatDate value="${historyUseRecordsDetail.depositTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>${historyUseRecordsDetail.clerk}</td>
            <td>${historyUseRecordsDetail.clerkName}</td>
            <td>${historyUseRecordsDetail.equipmentId}</td>
            <td>${historyUseRecordsDetail.countyName}</td>
            <td><fmt:formatDate value="${historyUseRecordsDetail.depositDate}" pattern="yyyy-MM-dd"/></td>
            <td>${historyUseRecordsDetail.saveMethod}</td>
            <td>${historyUseRecordsDetail.currency}</td>
            <td>${historyUseRecordsDetail.hundred}</td>
            <td>${historyUseRecordsDetail.fifty}</td>
            <td>${historyUseRecordsDetail.twenty}</td>
            <td>${historyUseRecordsDetail.ten}</td>
            <td>${historyUseRecordsDetail.five}</td>
            <td>${historyUseRecordsDetail.one}</td>
            <td style="text-align:right;"><fmt:formatNumber value="${historyUseRecordsDetail.paperAmount}" type="currency" pattern="#,##0.00"/></td>
            <!-- <td>0.00</td> -->
            <td style="text-align:right;"><fmt:formatNumber value="${historyUseRecordsDetail.forceAmount}" type="currency" pattern="#,##0.00"/></td>
            <td style="text-align:right;"><fmt:formatNumber value="${historyUseRecordsDetail.otherAmount}" type="currency" pattern="#,##0.00"/></td>
            <td style="text-align:right;"><fmt:formatNumber value="${historyUseRecordsDetail.amount}" type="currency" pattern="#,##0.00"/></td>
            <td>${historyUseRecordsDetail.officeName}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
<div class="pagination">${page}</div>
<%-- <div class="form-actions" style="width:100%">
	返回
	<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
					onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/>
</div> --%>
</body>
</html>