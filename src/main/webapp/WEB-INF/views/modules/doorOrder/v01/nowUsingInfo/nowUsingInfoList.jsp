<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.depositError.list' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	$(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/NowUsingInfo/exportNowUsingInfo/");
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/NowUsingInfo/");		
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
		<li class="active"><a href="${ctx}/doorOrder/v01/NowUsingInfo/"><spring:message
					code='now.list' /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="nowUsingInfo"
		action="${ctx}/doorOrder/v01/NowUsingInfo/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<div>
				<!-- 机具编号-->
				<label><spring:message code='now.equipId' /> ：</label>
				<form:input path="seriesNumber" htmlEscape="false" maxlength="20"
					class="input-small" />
			</div>	
			<div>
				<!-- 钞袋号 -->
				<label><spring:message code='now.bagNo' />：</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="20"
					class="input-small" />
			</div>	
			<div>
				<!-- 所属公司 -->
				<label><spring:message code='now.belongTo' />：</label>
				<sys:treeselect id="aOffice" name="aOffice.id"
		            value="${nowUsingInfo.aOffice.id}" labelName="aOffice.name"
		            labelValue="${nowUsingInfo.aOffice.name}" title="<spring:message code='door.public.cust' />"
		            url="/sys/office/treeData" cssClass="required input-small"
		            notAllowSelectParent="true" notAllowSelectRoot="false" minType="8" maxType="9"
		            isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
			</div>
			&nbsp;&nbsp;	
			<div>
				<!-- 硬币盒号-->
				<%-- <label><spring:message code='now.boxNo' />：</label>
				<form:input path="boxNo" htmlEscape="false" maxlength="20"
					class="input-small" /> --%>
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
		</div>
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='now.order' /></th>
				<!-- 机具编号 -->
				<th class="sort-column ei.series_number"><spring:message code='now.equipId' /></th>
				<!-- 区域 -->
				<th class="sort-column so2.NAME"><spring:message code='now.area' /></th>
				<!-- 钞袋号 -->
				<th class="sort-column cam.BAG_NO"><spring:message code='now.bagNo' /></th>
				<!-- 硬币盒号 -->
				<%-- <th><spring:message code='now.boxNo' /></th> --%>
				<!-- 速存数量-->
				<th class="sort-column t2.PAPER_COUNT"><spring:message code='now.paperNo' /></th>
				<!-- 速存金额 -->
				<th class="sort-column t2.PAPER_AMOUNT"><spring:message code='now.paperAmount' /></th>
				<!-- 硬币数量 -->
				<%-- <th><spring:message code='now.coinNo' /></th> --%>
				<!-- 硬币金额 -->
				<%-- <th><spring:message code='now.coinAmount' /></th> --%>
				<!-- 强制金额 -->
				<th class="sort-column t2.FORCE_AMOUNT"><spring:message code='now.forceAmount' /></th>
				<!-- 其他金额 -->
				<th class="sort-column t2.OTHER_AMOUNT"><spring:message code='now.otherAmount' /></th>
				<!-- 存入总量 -->
				<th class="sort-column t2.COUNT"><spring:message code='now.count' /></th>
				<!-- 总金额 -->
				<th class="sort-column t2.AMOUNT"><spring:message code='now.amount' /></th>
				<!-- 上次替换款袋时间 -->
				<th class="sort-column t3.CHANGE_DATE"><spring:message code='now.lastChangeTime' /></th>
				<!-- 所属公司-->
				<th class="sort-column so.NAME"><spring:message code='now.belongTo' /></th>
				<!-- 存款明细-->
				<th><spring:message code='now.saveDetail' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="nowUsingInfo" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${nowUsingInfo.seriesNumber}</td>
					<td>${nowUsingInfo.area}</td>
					<td>${nowUsingInfo.bagNo}</td>
					<%-- <td>${nowUsingInfo.boxNo}</td> --%>
					<td>${nowUsingInfo.banknoteCount}</td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingInfo.banknoteAmount}" type="currency" pattern="#,##0.00"/></td>
					<%-- <td>${nowUsingInfo.coinCount}</td>
					<td>${nowUsingInfo.coinAmount}</td> --%>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingInfo.forceAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingInfo.otherAmount}" type="currency" pattern="#,##0.00"/></td>
					<td>${nowUsingInfo.totalCount}</td>
					<td style="text-align:right"><fmt:formatNumber value="${nowUsingInfo.totalAmount}" type="currency" pattern="#,##0.00"/></td>
					<td><fmt:formatDate value="${nowUsingInfo.lastChangeTime}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${nowUsingInfo.belongTo}</td>
					<td>
						<span style='width: 25px; display: inline-block;'>
							<!-- 存款明细  -->
							<a href="${ctx}/doorOrder/v01/NowUsingInfo/detail?equipmentId=${nowUsingInfo.equipmentId}&bagNo=${nowUsingInfo.bagNo}&orderId=${nowUsingInfo.orderId}"
							title="<spring:message code='now.saveDetail'/>"> <i
								class="fa fa-eye  fa-lg"></i>
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