<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>车辆管理</title>
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
		<li class="active"><a href="${ctx}/store/v01/stoCarInfo/">车辆列表</a></li>
		<shiro:hasPermission name="store:v01:stoCarInfo:edit"><li><a href="${ctx}/store/v01/stoCarInfo/form">车辆添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoCarInfo" action="${ctx}/store/v01/stoCarInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>车牌号码：</label>
		<form:input path="carNo" htmlEscape="false" maxlength="10" class="input" onkeyup="this.value=this.value.toUpperCase()"/>
		<%-- <c:set var="officeType" value="${officeType}"/>
		<c:if test="${officeType=='7'}">
		<label class="control-label">所属机构：</label>
		<sys:treeselect id="office" name="office.id" value="${stoCarInfo.office.id}" labelName="office.name" labelValue="${stoCarInfo.office.name}" title="机构"
			url="/sys/office/treeData" allowClear="true" notAllowSelectRoot="false" notAllowSelectParent="false" cssClass="required input-medium" />
		</c:if> --%>
		<!-- 按钮 -->
		<input id="btnSubmit" class="btn btn-primary" 
			type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<th>序号</th>
				<th>车牌号</th>
				<th>所属机构</th>
				<th>创建人</th>
				<th>创建日期</th>
				<th>更新人</th>
				<th>更新日期</th>
				<shiro:hasPermission name="store:v01:stoCarInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoCarInfo" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td><a href="${ctx}/store/v01/stoCarInfo/form?id=${stoCarInfo.id}&pageFlag=modify">
					${stoCarInfo.carNo}
				</a></td>
				<td>${stoCarInfo.office.name}</td>
				<td>${stoCarInfo.createName}</td>
				<td>
					<fmt:formatDate value="${stoCarInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>${stoCarInfo.updateName}</td>
				<td>
					<fmt:formatDate value="${stoCarInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="store:v01:stoCarInfo:edit"><td>
    				<a href="${ctx}/store/v01/stoCarInfo/form?id=${stoCarInfo.id}&pageFlag=modify" title="修改"><i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/store/v01/stoCarInfo/delete?id=${stoCarInfo.id}" onclick="return confirmx('确认要删除该车辆吗？', this.href)" title="删除"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>