<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						var paramStr = "pageNo=" + $("#pageNo").val();
						paramStr += "&pageSize=" + $("#pageSize").val();
						paramStr += "&orderBy=" + $("#orderBy").val();
						paramStr += "&office.id=" + $("#officeId").val();
						paramStr += "&loginName=" + $("#loginName").val();
						paramStr += "&name=" + $("#name").val();
						$.download('${ctx}/sys/user/export',paramStr,'post' );
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx、xlsm”格式文件！"});
			});
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/sys/user/list");
				$("#searchForm").submit();
			});
		});
	</script>
	<style>
	.ul-form{margin:0;display:table;}
	</style>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/sys/user/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/sys/user/import/template2">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/user/list">用户列表</a></li>
		<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form">用户添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<div>
			<label>归属机构：</label><sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}" 
				title="机构" url="/sys/office/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="false" checkGroupOffice="true"/>
			</div>
			<div>
			<label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="15" class="input-small"/>
			</div>
			<div>
			<label>姓名：</label><form:input path="name" htmlEscape="false" maxlength="10" class="input-small"/>
			</div>
			<!-- gzd 2020-04-16 添加岗位筛选 -->
			<div>
				<label class="control-label">岗位：</label>
				<form:select path="userType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
			</div>
			&nbsp;&nbsp;
			<div>
			<input id="btnExport" class="btn btn-default" type="button" value="导出"/>
			</div>
			&nbsp;&nbsp;
			<shiro:hasPermission name="sys:user:edit">
				<div>
				<input id="btnImport" class="btn btn-red" type="button" value="导入"/>
				</div>
			</shiro:hasPermission>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th>归属机构</th>
				<th class="sort-column login_name">登录名</th>
				<th class="sort-column name">姓名</th>
				<th class="sort-column user_type">岗位</th>
				<th>手机</th>
				<%--<th class="sort-column USER_FACE_ID">脸部识别ID</th>--%>
				<shiro:hasPermission name="sys:user:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="user">
			<tr>
				<td>${user.office.name}</td>
				<c:if test="${user.id ne fns:getUser().id}">
					<td><a href="${ctx}/sys/user/form?id=${user.id}">${user.loginName}</a></td>
				</c:if>
				<c:if test="${user.id eq fns:getUser().id}">
					<td>${user.loginName}</td>
				</c:if>
				<td>${user.name}</td>
				<td>${fns:getDictLabel(user.userType, 'sys_user_type', '')}</td>
				<td>${user.mobile}</td>
				<%--<td>${user.userFaceId}</td>--%>
				<shiro:hasPermission name="sys:user:edit"><td>
    				<c:if test="${user.id ne fns:getUser().id}">
    				<a href="${ctx}/sys/user/form?id=${user.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/sys/user/delete?id=${user.id}" onclick="return confirmx('确认要删除该用户吗？', this.href)" title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
					<c:if test="${user.bindFlag eq 1 && (userType eq '10' || userType eq '0')}">
						<a href="${ctx}/sys/user/noBind?userId=${user.id}" onclick="return confirmx('确认要将该用户解除微信绑定吗？', this.href)" title="解绑" style="margin-left:10px;"><i class="fa fa-chain-broken text-red fa-lg"></i></a>
					</c:if>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
	
</body>
</html>