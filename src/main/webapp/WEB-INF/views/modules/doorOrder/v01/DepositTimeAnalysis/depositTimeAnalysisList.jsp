<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title><spring:message code="door.depositTimeAnalysis.totalCount" /></title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
    <%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
    $(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositTimeAnalysis/export");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositTimeAnalysis/");		
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
    <li class="active"><a href="${ctx}/doorOrder/v01/depositTimeAnalysis/"><spring:message code="door.depositTimeAnalysis.list" /></a></li>
</ul>
<form:form id="searchForm" modelAttribute="depositTimeAnalysis" action="${ctx}/doorOrder/v01/depositTimeAnalysis/" method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
   <!--  <div class="row">
        <div class="span14" style="margin-top:5px;"> -->
        <div class="row search-flex">
            <%-- 门店 --%>
            <div style="display:none;">
            <label><spring:message code="door.depositTimeAnalysis.door" />：</label>
            <sys:treeselect id="office" name="doorId"
                            value="${depositTimeAnalysis.doorId}" labelName="office.name"
                            labelValue="${depositTimeAnalysis.office.name}" title="<spring:message code='door.public.cust' />"
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
					value="<fmt:formatDate value="${depositTimeAnalysis.createTimeStart}" pattern="yyyy-MM-dd"/>" 
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
					value="<fmt:formatDate value="${depositTimeAnalysis.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
             </div>
            <%-- 查询 --%>
             &nbsp;&nbsp;
             <div>
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			 </div>
			<!-- 导出 -->
			 &nbsp;&nbsp;
			 <div>
			<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
			 </div>
        </div>
   <!--  </div> -->
</form:form>
<sys:message content="${message}"/>
<div class="table-con">
<table id="contentTable" class="table table-hover">
    <thead>
    <tr>
		<%-- 序号--%>
		<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.seqNo" /></th>
		<%-- 结算办
		<th class="sort-column a1.equipment_id" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositTimeAnalysis.settleOffice" /></th> --%>
		<%-- 门店--%>
		<th class="sort-column main.DOOR_NAME" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositTimeAnalysis.door" /></th>
		<%-- 存款次数--%>
		<th class="sort-column main.totalCount" rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 5分钟以下 <spring:message code="door.depositTimeAnalysis.paper" /> --%>
		<th colspan="2" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.ltFiveCount" /></th>
		<%-- 10分钟--%>
		<th colspan="2" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.fiveToTenCount" /></th>
		<%-- 15分钟--%>
		<th colspan="2" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.tenToFifteenCount" /></th>
		<%-- 20分钟--%>
		<th colspan="2" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.fifteenToTwentyCount" /></th>
		<%-- 20分钟以上--%>
		<th colspan="2" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.gtTwentyCount" /></th>
	</tr>
	<tr>
		<%-- 次数--%>
		<th class="sort-column ltFiveCount" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 占比--%>
		<th class="sort-column ltFivePercent" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column fiveToTenCount" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 占比--%>
		<th class="sort-column fiveToTenPercent" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column tenToFifteenCount" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 占比--%>
		<th class="sort-column tenToFifteenPercent" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column fifteenToTwentyCount" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 占比--%>
		<th class="sort-column fifteenToTwentyPercent" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.percent" /></th>
		<%-- 次数--%>
		<th class="sort-column gtTwentyCount" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.totalCount" /></th>
		<%-- 占比--%>
		<th class="sort-column gtTwentyPercent" style="text-align: center;"><spring:message code="door.depositTimeAnalysis.percent" /></th>
	</tr>
    </thead>
    <tbody>
	<c:forEach items="${page.list}" var="depositTimeAnalysis" varStatus="status">
        <tr style="color:${status.index eq 0 ? 'green':''}">
        	<c:if test="${status.index eq 0}"><td></td></c:if>
        	<c:if test="${status.index ne 0}"><td>${status.index}</td></c:if>
        	<%-- <td>${depositTimeAnalysis.settleOffice}</td> --%>
            <td>${depositTimeAnalysis.doorName}</td>
            <td>${depositTimeAnalysis.totalCount}</td>
            <td>${depositTimeAnalysis.ltFiveCount}</td>
            <td><fmt:formatNumber value="${depositTimeAnalysis.ltFivePercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${depositTimeAnalysis.fiveToTenCount}</td>
            <td><fmt:formatNumber value="${depositTimeAnalysis.fiveToTenPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${depositTimeAnalysis.tenToFifteenCount}</td>
            <td><fmt:formatNumber value="${depositTimeAnalysis.tenToFifteenPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${depositTimeAnalysis.fifteenToTwentyCount}</td>
            <td><fmt:formatNumber value="${depositTimeAnalysis.fifteenToTwentyPercent*100}" type="currency" pattern="#0.00"/>%</td>
            <td>${depositTimeAnalysis.gtTwentyCount}</td>
            <td><fmt:formatNumber value="${depositTimeAnalysis.gtTwentyPercent*100}" type="currency" pattern="#0.00"/>%</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
<div class="pagination">${page}</div>
</body>
</html>