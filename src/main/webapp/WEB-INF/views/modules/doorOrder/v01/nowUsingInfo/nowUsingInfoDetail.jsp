<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.depositError.list' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/NowUsingInfo/exportNowUsingDetailInfo");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/NowUsingInfo/detail");		
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<!-- 现在使用情况 -->
		<li><a href="${ctx}/doorOrder/v01/NowUsingInfo/"><spring:message
					code='now.list' /></a></li>
		<!-- 明细一览-->
		<%-- <shiro:hasPermission name="doorOrder:v01:depositError:edit"> --%>
		<li class="active"><a href="#"><spring:message
					code='now.detail' /></a></li>
		<%-- </shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="nowUsingDetailInfo"
		action="${ctx}/doorOrder/v01/NowUsingInfo/detail" method="post"
		class="breadcrumb form-search">
		<%-- <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" 
			value="${page.pageSize}" />--%>
		<form:hidden path="bagNo"/>
        <form:hidden path="equipmentId"/>
        <form:hidden path="orderId"/>
        <div class="row search-flex">
        	<div>
				<!-- 结算批次-->
				<%-- <label><spring:message code='now.detail.settlementBatch' /> ：</label>
				<form:input path="saveBatch" htmlEscape="false" maxlength="20"
					class="input-small" /> --%>
					
				<!-- 存款批次-->
				<label><spring:message code='now.detail.saveBatch' />：</label>
				<form:input path="saveBatch" htmlEscape="false" maxlength="20"
					class="input-small" />
		
        	</div>
        	<div>
				<!-- 存入时间 -->
				<label><spring:message code="now.detail.saveTime" />：</label> <input
							id="saveTime" name="saveTime" type="text"
							readonly="readonly" maxlength="20"
							class="input-small Wdate createTime"
							value="<fmt:formatDate value="${nowUsingDetailInfo.saveTime}" pattern="yyyy-MM-dd"/>"
							onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'saveTime\')}',maxDate:'%y-%M-%d'});" />
				
				
        	</div>
        	<div>
				<%-- <label><spring:message code='now.detail.saveTime' />：</label>
				<form:input path="saveTime" htmlEscape="false" maxlength="20"
					class="input-small" /> --%>
				
				
				<!-- 店员-->
				<label><spring:message code='door.historyUseRecords.clerkName' />：</label>
				<form:input path="userId" htmlEscape="false" maxlength="20"
					class="input-small" />
        	</div>
        	&nbsp;&nbsp;
        	<div>
				<!-- 按钮 -->
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.search'/>" />
        	</div>
        	&nbsp;&nbsp;
        	<div>
				<%-- 导出 --%>
				<input id="exportSubmit"  class="btn btn-red" type="button"
					value="<spring:message code='common.export'/>" />
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
	<sys:message content="${message}" />
	<div style="width:100%;height:100%;" align="center">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='now.detail.order' /></th>
				<!-- 结算批次 -->
				<%-- <th><spring:message code='now.detail.settlementBatch' /></th> --%>
				<!-- 存款批次 -->
				<th><spring:message code='now.detail.saveBatch' /></th>
				<!-- 存入时间 -->
				<th><spring:message code='now.detail.saveTime' /></th>
				<!-- 店员 -->
				<th><spring:message code='now.detail.userId' /></th>
				<!-- 店员姓名-->
				<th><spring:message code='now.detail.userName' /></th>
				<!-- 机具编号 -->
				<th><spring:message code='now.detail.equipId' /></th>
				<!-- 区域 -->
				<th><spring:message code='now.detail.area' /></th>
				<!-- 款项日期 -->
				<th><spring:message code='now.detail.moenyDate' /></th>
				<!-- 存款方式 -->
				<th><spring:message code='door.historyUseRecords.type' /></th>
				<!-- 币种 -->
				<th><spring:message code='now.detail.moneyType' /></th>
				<!-- 100 -->
				<th><spring:message code='now.detail.hundred' /></th>
				<!-- 50 -->
				<th><spring:message code='now.detail.fifty' /></th>
				<!-- 20 -->
				<th><spring:message code='now.detail.twenty' /></th>
				<!-- 10 -->
				<th><spring:message code='now.detail.ten' /></th>
				<!-- 5 -->
				<th><spring:message code='now.detail.five' /></th>
				<!-- 1 -->
				<th><spring:message code='now.detail.one' /></th>
				<!-- 0.5 -->
				<th><spring:message code='now.detail.ofive' /></th>
				<!-- 0.1 -->
				<th><spring:message code='now.detail.oone' /></th>
				<!-- 速存数量 -->
				<th><spring:message code='now.detail.papaerCount' /></th>
				<!-- 速存金额 -->
				<th><spring:message code='now.detail.paperAmount' /></th>
				<!-- 强制金额 -->
				<th><spring:message code='now.detail.forceAmount' /></th>
				<!-- 其他金额 -->
				<th><spring:message code='now.detail.otherAmount' /></th>
				<!-- 总金额 -->
				<th><spring:message code='now.detail.totalAmount' /></th>
				<!-- 所属公司 -->
				<th><spring:message code='now.detail.belongTo' /></th>
			</tr>
		</thead>
		<tbody>
			<!-- 序号 -->
			<tr>
				<td></td>
				<!-- 结算批次 -->
				<!-- <td></td> -->
				<!-- 存款批次 -->
				<td></td>
				<!-- 存入时间 -->
				<td><spring:message code='now.detail.total' /></td>
				<!-- 店员 -->
				<td></td>
				<!-- 店员姓名-->
				<td></td>
				<!-- 存款机ID -->
				<td></td>
				<!-- 区域 -->
				<td></td>
				<!-- 款项日期 -->
				<td></td>
				<!-- 业务类型 -->
				<td></td>
				<!-- 币种 -->
				<td></td>
				<!-- 100 -->
				<td>${total.hundred}</td>
				<!-- 50 -->
				<td>${total.fifty}</td>
				<!-- 20 -->
				<td>${total.twenty}</td>
				<!-- 10 -->
				<td>${total.ten}</td>
				<!-- 5 -->
				<td>${total.five}</td>
				<!-- 1 -->
				<td>${total.one}</td>
				<!-- 0.5 -->
				<td>${total.ofive}</td>
				<!-- 0.1 -->
				<td>${total.oone}</td>
				<!-- 纸币数量 -->
				<td>${total.paperCount}</td>
				<!-- 纸币金额 -->
				<td style="text-align:right"><fmt:formatNumber value="${total.paperAmount}" type="currency" pattern="#,##0.00"/></td>
				<!-- 强制金额 -->
				<td style="text-align:right"><fmt:formatNumber value="${total.forceAmount}" type="currency" pattern="#,##0.00"/></td>
				<!-- 其他金额 -->
				<td style="text-align:right"><fmt:formatNumber value="${total.otherAmount}" type="currency" pattern="#,##0.00"/></td>
				<!-- 总金额 -->
				<td style="text-align:right"><fmt:formatNumber value="${total.totalAmount}" type="currency" pattern="#,##0.00"/></td>
				<!-- 所属公司 -->
				<td></td>
			</tr>
			<c:forEach items="${detailList}" var="nowUsingDetailInfo" varStatus="status">
				<tr>
					<td>${status.index + 1}</td>
					<%-- <td>${nowUsingDetailInfo.settlementBatch}</td> --%>
					<td>${nowUsingDetailInfo.saveBatch}</td>
					<td><fmt:formatDate value="${nowUsingDetailInfo.saveTime}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${nowUsingDetailInfo.userId}</td>
					<td>${nowUsingDetailInfo.userName}</td>
					<td>${nowUsingDetailInfo.equipmentId}</td>
					<td>${nowUsingDetailInfo.area}</td>
					<td><fmt:formatDate value="${nowUsingDetailInfo.moenyDate}"
							pattern="yyyy-MM-dd" /></td>
					<td>${nowUsingDetailInfo.businessType}</td>
					<td>${nowUsingDetailInfo.moneyType}</td>
					<td>${nowUsingDetailInfo.hundred}</td>
					<td>${nowUsingDetailInfo.fifty}</td>
					<td>${nowUsingDetailInfo.twenty}</td>
					<td>${nowUsingDetailInfo.ten}</td>
					<td>${nowUsingDetailInfo.five}</td>
					<td>${nowUsingDetailInfo.one}</td>
					<td>${nowUsingDetailInfo.ofive}</td>
					<td>${nowUsingDetailInfo.oone}</td>
					<td>${nowUsingDetailInfo.paperCount}</td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingDetailInfo.paperAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingDetailInfo.forceAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingDetailInfo.otherAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingDetailInfo.totalAmount}" type="currency" pattern="#,##0.00"/></td>
					<td>${nowUsingDetailInfo.belongTo}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<%-- <div class="form-actions" style="width:100%">
	返回
	<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
					onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/>
	</div> --%>
	<%-- <div class="pagination">${page}</div> --%>
</body>
</html>