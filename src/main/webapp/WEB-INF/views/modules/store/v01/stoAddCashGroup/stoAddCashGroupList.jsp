
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.add.cash.group.manager" /></title><!-- 加钞组管理 -->
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/store/v01/stoAddCashGroup/list"><spring:message
					code="store.add.cash.group.list" /></a></li>
		<shiro:hasPermission name="store:stoAddCashGroup:edit">			
		<li><a href="${ctx}/store/v01/stoAddCashGroup/form">
		<spring:message code="store.add.cash.group.info" /><spring:message code="common.add" />
		</a></li><%-- </shiro:hasPermission> --%>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoAddCashGroup"
		action="${ctx}/store/v01/stoAddCashGroup/list?isSearch=true" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		
		<label><spring:message code="store.add.cash.group.name" />：</label><!-- 加钞组名称 -->
		<form:input path="groupName" htmlEscape="false" maxlength="10"
			class="input-small number" />
		&nbsp;
		
		<label><spring:message code="store.add.cash.group.car.name" />：</label><!-- 车辆名称 -->
		<form:input path="carName" htmlEscape="false" maxlength="10"
			class="input-small number" />
		&nbsp;
		
		<label><spring:message code='store.escortUser1'/>：</label><!-- 押运人员1<spring:message code="common.boxNo" /> -->
		<form:input path="escortName1" htmlEscape="false" maxlength="10"
			class="input-small number" />
		&nbsp;
		
		<label><spring:message code='store.escortUser2'/>：</label><!-- 押运人员2<spring:message code="common.boxNo" /> -->
		<form:input path="escortName2" htmlEscape="false" maxlength="10"
			class="input-small number" />
		&nbsp;	
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" /><!-- <spring:message code="common.search"/> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="common.seqNo" /></th>
				<th class="sort-column a.box_no"><spring:message code="store.add.cash.group.name" /></th><!-- 名称 -->
				<th class="sort-column a.rfid"><spring:message code="store.add.cash.group.car" /></th><!-- 车辆 -->
				<th class="sort-column a.box_type"><spring:message code='store.escortUser1'/></th>
				<!-- 追加钞箱类型，将使用网点改为所属机构 修改人：sg 修改日期：2017-11-03 -->
				<th><spring:message code='store.escortUser2'/></th>
				<th class="sort-column o5.name"><spring:message code="common.registerTime" /></th><!-- 登记时间 -->
				<!-- end -->
				<th class="sort-column a.box_status"><spring:message code="common.registerPerson" /></th><!-- 登记人 -->
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="stoAddCashGroup" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td><a href="${ctx}/store/v01/stoAddCashGroup/view?id=${stoAddCashGroup.id}">${stoAddCashGroup.groupName}</a></td>
					<td>${stoAddCashGroup.carName}</td>
					<td>${stoAddCashGroup.escortName1}</td>
					<td>${stoAddCashGroup.escortName2}</td>
					<td><fmt:formatDate value="${stoAddCashGroup.createDate}" pattern="yyyy-MM-dd" /></td>
					<td>${stoAddCashGroup.createName}</td>
					<td>
						<shiro:hasPermission name="store:stoAddCashGroup:edit">
						<a href="${ctx}/store/v01/stoAddCashGroup/form?id=${stoAddCashGroup.id}" title="<spring:message code='common.modify'/>"><i class="fa fa-edit text-green fa-lg"></i></a>
						<a href="${ctx}/store/v01/stoAddCashGroup/delete?id=${stoAddCashGroup.id}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" 
						title='<spring:message code="common.delete" />'><i class="fa fa-trash-o text-red fa-lg"></i></i></a>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>