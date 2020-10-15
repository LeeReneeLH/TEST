<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>角色管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/role/">角色列表</a></li>
		<shiro:hasPermission name="sys:role:edit"><li><a href="${ctx}/sys/role/form">角色添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
	<thead>
		<tr>
			<th>角色名称</th>
			<%-- ADD-START  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17  --%>
			<th>英文名称</th>
			<%-- ADD-END  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17  --%>
			<th>备注</th>
			<shiro:hasPermission name="sys:role:edit">
				<th>操作</th>
			</shiro:hasPermission>
		</tr>
		</thead>
		<c:forEach items="${list}" var="role">
			<tr>
				<td><a href="form?id=${role.id}">${role.name}</a></td>
				<%-- ADD-START  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17  --%>
				<td><a href="form?id=${role.id}">${role.enname}</a></td>
				<%-- ADD-END  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17  --%>
				<td>${role.remarks}</td>
				<shiro:hasPermission name="sys:role:edit">
				<td>
					<a href="${ctx}/sys/role/assign?id=${role.id}" title="分配角色"><i class="fa  fa-user fa-lg"></i></a>
					<c:if test="${fns:getUser().id eq '1' || role.id ne '1'}">
						<a href="${ctx}/sys/role/form?id=${role.id}" title="编辑" style="margin-left:10px;"><i class="fa fa-edit text-green fa-lg"></i></a>
						<a href="${ctx}/sys/role/delete?id=${role.id}" onclick="return confirmx('确认要删除该角色吗？', this.href)" title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
					</c:if>
				</td>
				</shiro:hasPermission>	
			</tr>
		</c:forEach>
	</table>
</body>
</html>