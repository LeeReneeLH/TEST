<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.goodsExchange" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		setToday(".createTime");
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/store/v01/stoExchange/"> <spring:message
					code="store.goodsExchange.list" /></a></li>
		<shiro:hasPermission name="store:stoExchange:edit">
			<li><a href="${ctx}/store/v01/stoExchange/form"> 
			<spring:message	code="store.goodsExchange" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoExchange"
		action="${ctx}/store/v01/stoExchange/list?isSearch=true" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoExchange.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${stoExchange.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="common.seqNo" /></th>
				<th class="sort-column a.excheng_Id"><spring:message code="store.serialorderNo" /></th>
				<th class="sort-column min(a.change_goods_id)"><spring:message
						code="store.exchange.goodsName" /></th>
				<th ><spring:message
						code="store.exchange.goodsNum" /></th>
				<!--  <th class="sort-column a.to_goods_id"><spring:message
						code="store.exchange.toGoodsName" /></th>
				<th class="sort-column a.to_goods_num"><spring:message
						code="store.exchange.toGoodsNum" /></th>-->
				<th ><spring:message
						code="store.exchange.amount" /></th>					
				<th class="sort-column min(o3.name)"><spring:message
						code="common.office" /></th>
				<th class="sort-column min(a.create_by)"><spring:message
						code="store.exchangeUser" /></th>
				<th class="sort-column min(a.create_date)"><spring:message
						code="store.exchangeTime" /></th>
				<!--<th><spring:message	code="common.remark" /></th>-->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="stoExchange" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td><a href="${ctx}/store/v01/stoExchange/form?id=${stoExchange.id}">${stoExchange.id}</a></td>
					<td>${stoExchange.changeGoods.goodsName}</td>
					<td>${stoExchange.changeGoodsNum}</td>
					<%--<td>${stoExchange.toGoods.goodsName}</td>
					<td>${stoExchange.toGoodsNum}</td>--%>
					<td><fmt:formatNumber value="${stoExchange.amount}" pattern="#,##0.00#"/></td>
					<td>${stoExchange.office.name}</td>
					<td>${stoExchange.createName}</td>
					<td><fmt:formatDate value="${stoExchange.createDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					<%--<td>${stoExchange.remarks}</td>--%>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
