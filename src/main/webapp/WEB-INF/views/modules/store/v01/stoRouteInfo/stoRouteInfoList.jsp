<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.route.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/store/v01/stoRouteInfo/"><spring:message code="store.route.list"/></a></li>
		<shiro:hasPermission name="store:stoRouteInfo:edit">
		<li><a href="${ctx}/store/v01/stoRouteInfo/form"><spring:message code="store.route"/><spring:message code="common.add" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoRouteInfo" action="${ctx}/store/v01/stoRouteInfo/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<label><spring:message code="store.routeName"/>：</label>
			<form:input path="routeName" htmlEscape="false" maxlength="25" class="input-medium"/>
			<!-- 查询条件添加过滤，不是管理员只显示当前机构的押运人员 修改人：xp 修改时间：2017-8-22 begin -->
			<c:choose>
				<c:when test="${fns:getUser().userType == '10' }">
					&nbsp;<label class="control-label"><spring:message code="store.escortUser"/>：</label>
					<form:select path="escortId" class="input-small">
						<form:option value=""><spring:message code="common.select"/></form:option>
						<form:options items="${sto:getStoEscortinfoList('allEscortList')}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
					</form:select>
				</c:when>
				<c:otherwise>
					<%-- <c:if test="${sto:getFilterStoEscortinfoList('allEscortList', fns:getUser().office.id) != null}"> --%>
						&nbsp;<label class="control-label"><spring:message code="store.escortUser"/>：</label>
						<form:select path="escortId" class="input-small">
							<form:option value=""><spring:message code="common.select"/></form:option>							
							<form:options items="${sto:getFilterStoEscortinfoList('allEscortList', fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />							
						</form:select>
					<%-- </c:if> --%>
				</c:otherwise>
			</c:choose>
			<!-- end -->
		<label class="control-label"><spring:message code="store.selectOffice" />：</label>
			<sys:treeselect id="office" name="office.id" value="${stoRouteInfo.office.id}" labelName="office.name" labelValue="${stoRouteInfo.office.name}" title="机构"
			url="/sys/office/treeData" allowClear="true" notAllowSelectRoot="true" notAllowSelectParent="true" cssClass="required input-medium" />
			
		<!-- 增加车辆过滤  修改人：xp 修改时间：2017-8-22 begin-->
			<c:choose>
				<c:when test="${fns:getUser().userType == '10' }">
					&nbsp;
					<label class="control-label"><spring:message code="store.carNumber"/>：</label>
						<form:select path="carNo" id="carNo" class="input-large">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getStoCarInfoAllList()}" itemLabel="carNo" itemValue="carNo" htmlEscape="false" />
						</form:select>
				</c:when>
				<c:otherwise>
					&nbsp;
					<label class="control-label"><spring:message code="store.carNumber"/>：</label>
						<form:select path="carNo" id="carNo" class="input-large">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getAllStoCarInfoList(fns:getUser().office.id)}" itemLabel="carNo" itemValue="carNo" htmlEscape="false" />
						</form:select>
				</c:otherwise>
			</c:choose>
		<!-- end -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="common.seqNo"/></th>
				<th class="sort-column a.route_name"><spring:message code="store.routeName"/></th>
				<th class="sort-column a.office_id"><spring:message code="store.affiliatedOffice"/></th>
				<th class="sort-column a.detail_num"><spring:message code="store.outletsNum"/></th>
				<th class="sort-column a.route_type"><spring:message code="store.routeType"/></th>
				<th class="sort-column a.escort_id1"><spring:message code="store.escortUser1"/></th>
				<th class="sort-column a.escort_id2"><spring:message code="store.escortUser2"/></th>
				<th class="sort-column a.escort_id2">车牌号码</th>
				<shiro:hasPermission name="store:stoRouteInfo:edit">
				<th><spring:message code="common.operation"/></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoRouteInfo" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
				    <%-- <td><a href="${ctx}/store/v01/stoRouteInfo/see?id=${stoRouteInfo.id}">${stoRouteInfo.routeName}</a></td> --%>
					<td>${stoRouteInfo.routeName}</td>
					<td>${stoRouteInfo.curOffice.name}</td>
					<td>${stoRouteInfo.detailNum}</td>
					<td>${fns:getDictLabel(stoRouteInfo.routeType,'sto_route_type',"")}</td>
					<td>${stoRouteInfo.escortInfo1.escortName}</td>
					<td>${stoRouteInfo.escortInfo2.escortName}</td>
					<td>${stoRouteInfo.carNo}</td>
					<shiro:hasPermission name="store:stoRouteInfo:edit">
					<td>
	    				<a href="${ctx}/store/v01/stoRouteInfo/form?id=${stoRouteInfo.id}" title="编辑">
	    				<%-- <spring:message code="common.modify"/> --%><i class="fa fa-edit text-green fa-lg"></i></a>
						<a href="${ctx}/store/v01/stoRouteInfo/delete?id=${stoRouteInfo.id}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除" style="margin-left:10px;">
						<%-- <spring:message code="common.delete"/> --%><i class="fa fa-trash-o text-red fa-lg"></i></a>
					</td>
					</shiro:hasPermission>
				</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
