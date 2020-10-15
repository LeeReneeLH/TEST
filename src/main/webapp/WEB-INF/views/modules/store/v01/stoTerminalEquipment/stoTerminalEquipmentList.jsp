<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.equipmentManage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
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
		<li class="active">
			<a href="${ctx}/store/v01/stoTerminalEquipment/"><spring:message code="store.equipmentList"/></a>
		</li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoTerminalEquipment" action="${ctx}/store/v01/stoTerminalEquipment/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 网点名称 -->
		<div class="btn-group-vertical">
			&nbsp;<label><spring:message code="common.office"/>：</label>
			<sys:treeselect id="office" name="office.id"
				value="${stoTerminalEquipment.office.id}" labelName="office.name"
				labelValue="${stoTerminalEquipment.office.name}" title="机构"
				url="/sys/office/treeData" notAllowSelectRoot="true"
				notAllowSelectParent="true" allowClear="true"
				cssClass="input-medium" />
		</div>
		<!-- 设备类型 -->
		<div class="btn-group-vertical">
			&nbsp;<label><spring:message code="store.equipment.type"/>：</label>
			<form:select path="teType" class="input-small">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<form:options items="${fns:getDictList('sto_te_type')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div>
		<!-- 设备编号 -->
		&nbsp;<label><spring:message code="common.equipmentID"/>：</label>
		<form:input path="id" htmlEscape="false" maxlength="50"
			class="input-large" onkeyup="value=value.replace(/[^0-9a-zA-Z\-]/g,'')"/>
		<!-- 状态 -->
		<div class="btn-group-vertical">
			&nbsp;<label><spring:message code="common.status"/>：</label>
			<form:select path="teStatus" class="input-small">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<form:options items="${fns:getDictList('sto_te_status')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div>
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search"/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="common.office"/></th>
				<th><spring:message code="common.equipmentID"/></th>
				<th><spring:message code="store.equipment.type"/></th>
				<c:choose>
				<c:when test="${displayDialFg == true}">
					<th><spring:message code="store.equipment.dialId"/></th>
					<th><spring:message code="store.equipment.dialPwd"/></th>
				</c:when>
				</c:choose>
				<th><spring:message code="common.status"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoTerminalEquipment">
			<tr>
				<td>${stoTerminalEquipment.office.name}</td>
				<td>${stoTerminalEquipment.id}</td>
				<td>${fns:getDictLabel(stoTerminalEquipment.teType,'sto_te_type',"")}</td>
				<c:choose>
				<c:when test="${displayDialFg == true}">
					<td>${stoTerminalEquipment.dialId}</td>
					<td>${stoTerminalEquipment.dialPwd}</td>
				</c:when>
				</c:choose>
				<td>
    				<a href="${ctx}/store/v01/stoTerminalEquipment/changeTeStatus?teId=${stoTerminalEquipment.id}">
    					${fns:getDictLabel(stoTerminalEquipment.teStatus,'sto_te_status',"")}
    				</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
