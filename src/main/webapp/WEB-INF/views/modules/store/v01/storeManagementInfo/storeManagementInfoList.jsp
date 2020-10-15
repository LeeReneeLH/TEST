<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.areaList" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<style>
.breadcrumb{height:100% !important;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库房管理列表 --%>
		<li><a href="${ctx}/store/v01/storeManagementInfo/graph">库房管理</a></li>
		<%-- 库房箱袋列表 --%>
		<li><a href="${ctx}/store/v01/storeGoodsInfo/list">库房箱袋列表</a></li>
		<shiro:hasPermission name="store:storeManagementInfo:edit">
			<%-- 库房管理列表 --%>
			<li class="active"><a href="#" onclick="javascript:return false;">库房管理列表</a></li>
			<%-- 库房创建 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/toCreateStorePage">库房创建</a></li>
		</shiro:hasPermission>
	</ul>
	
	<div class="row">
	
		<form:form id="searchForm" modelAttribute="storeManagementInfo" 
		action="${ctx}/store/v01/storeManagementInfo/list" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span12" style="margin-top:5px">
					<label>库房名称：</label>
					<form:input path="storeName" htmlEscape="false" maxlength="15" class="input-small"/>
					<label>状态：</label>
					<form:select path="delFlag" id="delFlag" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getFilterDictList('sto_area_status', true, '0,1')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>	
					&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
				
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 库房名称 --%>
				<th>库房名称</th>
				<%-- 创建机构 --%>
				<th>创建机构</th>
				<%-- 创建时间 --%>
				<th class="sort-column CREATE_DATE">创建时间</th>
				<%-- 创建人 --%>
				<th>创建人</th>
				<%-- 更新时间 --%>
				<th class="sort-column UPDATE_DATE">更新时间</th>
				<%-- 更新人 --%>
				<th>更新人</th>
				<%-- 状态 --%>
				<th  class="sort-column DEL_FLAG">状态</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="storeInfo" > 
				<tr>
					<td><a href="${ctx}/store/v01/storeManagementInfo/toCreateStorePage?id=${storeInfo.id}">${storeInfo.storeName}</a></td>
					<td>${storeInfo.office.name}</td>
					<td><fmt:formatDate value="${storeInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${storeInfo.createName}</td>
					<td><fmt:formatDate value="${storeInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${storeInfo.updateName}</td>
					<td>
						<a href="${ctx}/store/v01/storeManagementInfo/setStoreStatus?id=${storeInfo.id}">${fns:getDictLabel(storeInfo.delFlag,'sto_area_status',"")}</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
</body>
</html>
