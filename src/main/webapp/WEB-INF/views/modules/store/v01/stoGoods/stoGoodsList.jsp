<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.goods" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
		<li class="active"><a href="${ctx}/store/v01/goods/stoGoods/"><spring:message code="store.goods" /><spring:message code="common.list" /></a></li>
		<shiro:hasPermission name="store:goods:edit">
		<li><a href="${ctx}/store/v01/goods/stoGoods/form?goodType=${fns:getConfig('sto.goods.goodsType.currency')}"><spring:message code="store.goods" /><spring:message code="common.add" /></a></li>
		<li><a href="${ctx}/store/v01/goods/stoGoods/form?goodType=${fns:getConfig('sto.goods.goodsType.blankBill')}"><spring:message code="common.importantEmpty" /><spring:message code="common.add" /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoGoods" action="${ctx}/store/v01/goods/stoGoods/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 物品类型 -->
		<label><spring:message code='store.goodsType'/>：</label>
		<form:select path="goodsType" class="input-medium">
		    <form:options items="${fns:getDictList('good_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
		<!-- 物品编号 -->
		<label><spring:message code='store.goodsID'/>：</label>
		<input id="id" name="id"  class="input-medium" type="text" value="${stoGoods.id}" onkeyup="value=value.replace(/[^0-9]/g,'')"/>
		<!-- 物品名称 -->
		<label><spring:message code='store.goodsName'/>：</label>
		<input id="goodsName" name="goodsName" type="text" value="${stoGoods.goodsName}"/>
	    <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search"/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="store.goodsID" /></th>
				<th><spring:message code="store.goodsName" /></th>
				<%-- <th><spring:message code="store.goodsType" /></th> --%>
				<th><spring:message code="store.goodsDescription" /></th>
				<th><spring:message code="store.goodsWorth" /></th>
				<th><spring:message code="store.updateDate" /></th>
				<shiro:hasPermission name="store:goods:edit"><th><spring:message code="common.operation"/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoGoods">
			<tr>
				<td>${stoGoods.id}</td>
				<td>${stoGoods.goodsName}</td>
				<%-- <td>${fns:getDictLabel(stoGoods.goodsType,'good_type',"")}</td> --%>
				<td>${stoGoods.description}</td>
				<td style="text-align: right">
					<!-- 只有是货币的时候才显示物品价值 -->
					<c:choose>
						<c:when test="${stoGoods != null && stoGoods.goodsType == '01'}">
							<fmt:formatNumber value="${stoGoods.goodsVal}" pattern="#,##0.00#" />
						</c:when>
					</c:choose>
				</td>
				<td><fmt:formatDate value="${stoGoods.updateDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<shiro:hasPermission name="store:goods:edit">
					<td>
						<a href="${ctx}/store/v01/goods/stoGoods/form?id=${stoGoods.id}&goodType=''"  title="编辑"><%-- <spring:message code="common.modify"/> --%><i class="fa fa-edit text-green fa-lg"></i></a>
						<a href="${ctx}/store/v01/goods/stoGoods/delete?id=${stoGoods.id}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"  title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i><%-- <spring:message code="common.delete"/> --%></a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
