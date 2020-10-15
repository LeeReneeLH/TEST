<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物品字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
			<a href="${ctx}/store/v01/dict">物品字典列表</a>
		</li>
		<shiro:hasPermission name="sto:dict:edit">
		<li>
			<a href="${ctx}/store/v01/dict/form?sort=10">物品字典添加</a>
		</li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoDict"
		 action="${ctx}/store/v01/dict" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>描述：</label>
		<form:select id="type" path="type" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${typeList}" 
			itemLabel="label" itemValue="value" htmlEscape="false"/>
		</form:select>
		<label>状态：</label>
		<form:select id="delFlag" path="delFlag" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('disabled_flag')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<label>描述 ：</label>
		<form:input path="description" htmlEscape="false" maxlength="50" class="input-medium"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<th>键值</th>
			<th>标签</th>
			<th>类型</th>
			<th>描述</th>
			<th>排序</th>
			<th>计算列</th>
			<th>关联代码</th>
			<th>状态</th>
			<shiro:hasPermission name="sto:dict:edit">
			<th>操作</th>
			</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dict">
			<tr>
				<td>${dict.value}</td>
				<td><a href="${ctx}/store/v01/dict/form?id=${dict.id}">${dict.label}</a></td>
				<td><a href="javascript:" onclick="$('#type').val('${dict.type}');$('#searchForm').submit();return false;">${dict.type}</a></td>
				<td>${dict.description}</td>
				<td>${dict.sort}</td>
				<td>${dict.unitVal}</td>
				<td>${dict.refCode}</td>
				<c:choose>
					<c:when test="${dict.delFlag == '1'}">
						<td class="text-red">停用</td>
					</c:when>
					<c:when test="${dict.delFlag == '0'}">
						<td>正常</td>
					</c:when>
				</c:choose>
				<shiro:hasPermission name="sto:dict:edit">
				<td>
    				<a href="${ctx}/store/v01/dict/form?id=${dict.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
    				<c:choose>
					<c:when test="${dict.delFlag == '1'}">
						<a href="${ctx}/store/v01/dict/revert?id=${dict.id}&type=${dict.type}" onclick="return confirmx('确认要恢复该字典吗？', this.href)" title="恢复"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
					</c:when>
					<c:when test="${dict.delFlag == '0'}">
						<a href="${ctx}/store/v01/dict/delete?id=${dict.id}&type=${dict.type}" onclick="return confirmx('确认要停用该字典吗？', this.href)" title="停用" style="margin-left:10px;"><i class="fa fa-ban text-red fa-lg"></i></a>
					</c:when>
					</c:choose>
    				<a href="${ctx}/store/v01/dict/copy?id=${dict.id}"  title="添加键值" style="margin-left:10px;"><i class="fa  fa-plus-circle fa-lg"></i></a>
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
