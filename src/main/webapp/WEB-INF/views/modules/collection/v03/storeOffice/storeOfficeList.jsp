<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		
		//删除商户时验证当前商户下是否有有效用户
		function checkOffice(officeId) {
			
			var url="${ctx}/collection/v03/storeOffice/deleteCheck";
			//异步验证
			$.ajax({
				url:url,
				type:"post",
				
				data:'id='+officeId,
				dataType:"json",
				async:false,
				success:function(data){
					
					if(data == "false"){
						//要删除该商户下的所有门店吗？
						confirmx("<spring:message code='message.I7225'/>","${ctx}/collection/v03/storeOffice/delete?id="+officeId);
						
					} else {
						//当前商户下拥有有效用户，不能删除
						alertx("<spring:message code='message.I7226'/>");
					}
				},
				error:function(){
					//系统内部异常，请稍后再试或与系统管理员联系
					alertx("<spring:message code='message.E0101'/>")
				}
				
			});
		}
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/collection/v03/storeOffice/list">商户列表</a></li>
		<shiro:hasPermission name="collection:storeOffice:edit"><li><a href="${ctx}/collection/v03/storeOffice/form">商户添加</a></li></shiro:hasPermission>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="storeOffice" action="${ctx}/collection/v03/storeOffice/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 商户编号 --%>
					<label>商户编号：</label>
					<form:input path="code" class="input-medium" htmlEscape="false" maxlength="15" />
					&nbsp;&nbsp;&nbsp;
					<%-- 门店 --%>
					<label>商户名称：</label>
					<form:input path="name" class="input-medium" htmlEscape="false" maxlength="15" />
					&nbsp;&nbsp;&nbsp;
					<%-- 商户编号 --%>
					<label>启用标识：</label>
					<form:select path="enabledFlag" class="input-large required" id ="selectStatus">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('enabled_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
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
				<th>商户编号</th>
				<th>商户名称</th>
				<th>启用标识</th>
				<th>负责人</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="itemData">
			<tr>
				<td><a href="${ctx}/collection/v03/storeOffice/view?id=${itemData.id}">${itemData.code}</a></td>
				<td>${itemData.name}</td>
				<td>${fns:getDictLabel(itemData.enabledFlag, 'enabled_type', '')} </td>
				<td>${itemData.master}</td>
				<td>
				<shiro:hasPermission name="collection:storeOffice:edit">
    				<a href="${ctx}/collection/v03/storeOffice/form?id=${itemData.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>&nbsp;&nbsp;&nbsp;
					<a  href="#" onclick="checkOffice('${itemData.id}')"  title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</shiro:hasPermission>
				</td>
			</tr>

		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>