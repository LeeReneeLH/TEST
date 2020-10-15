<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.clearGroupMain.list' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {

	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 清机组列表 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/clearGroupMain/"><spring:message code='clear.clearGroupMain.list' /></a></li>
		<!-- 清机组添加-->
		<shiro:hasPermission name="doorOrder:v01:clearGroupMain:edit">
			<li><a href="${ctx}/doorOrder/v01/clearGroupMain/form"><spring:message code='clear.clearGroupMain.add' /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="clearGroupMain"
		action="${ctx}/doorOrder/v01/clearGroupMain/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<!-- 清机组名称-->
			<div>
			<label><spring:message code='clear.clearGroupMain.name' />：</label>
			<form:input path="clearGroupName" htmlEscape="false" maxlength="20"
				class="input-small" />
			</div>	
			<%-- 开始日期 --%>
			<div>
			<label><spring:message code="clear.clearGroupMain.createDate" />：</label>
			<input 	id="createTimeStart"  
					name="createTimeStart" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${clearGroupMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			</div>
			<%-- 结束日期 --%>
			<div>
			<label>~</label>
			<input 	id="createTimeEnd" 
					name="createTimeEnd" 
					type="text" 
					readonly="readonly" 
					maxlength="20" 
					class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${clearGroupMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			</div>
			<!-- 按钮 -->
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" /> 
			</div>
		</div>
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
			    <!-- 序号 -->
				<th><spring:message code='clear.clearGroupMain.indexCode' /></th>
				<!-- 清机组名 -->
				<th class="sort-column a.clear_group_name"><spring:message code='clear.clearGroupMain.name' /></th>
				<!-- 清分中心 -->
				<th class="sort-column o.name"><spring:message code='clear.clearGroupMain.clearCenter' /></th>
				<!-- 创建人 -->
				<th class="sort-column a.create_name"><spring:message code='clear.clearGroupMain.create' /></th>
				<!-- 创建日期 -->
				<th class="sort-column a.create_date"><spring:message code='clear.clearGroupMain.createDate' /></th>
				<!-- 更新人 -->
				<th class="sort-column a.update_name"><spring:message code='clear.clearGroupMain.update' /></th>
				<!-- 更新日期 -->
				<th class="sort-column a.update_date"><spring:message code='clear.clearGroupMain.updateDate' /></th>
				<!-- 操作 -->
			    <th><spring:message code='clear.clearGroupMain.caoZuo' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clearGroupMain" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td><a href="${ctx}/doorOrder/v01/clearGroupMain/view?clearGroupId=${clearGroupMain.clearGroupId}&pageFlag=modify">
					${clearGroupMain.clearGroupName}
				</a></td>
				<%-- <td>${clearGroupMain.clearGroupName}</td> --%>
				<td>${clearGroupMain.clearCenterName}</td>
				<td>${clearGroupMain.createName}</td>
				<td><fmt:formatDate value="${clearGroupMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${clearGroupMain.updateName}</td>
				<td><fmt:formatDate value="${clearGroupMain.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="doorOrder:v01:clearGroupMain:edit"><td>
    				<a href="${ctx}/doorOrder/v01/clearGroupMain/form?clearGroupId=${clearGroupMain.clearGroupId}&pageFlag=modify" title="<spring:message code='common.modify' />"><i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/doorOrder/v01/clearGroupMain/delete?clearGroupId=${clearGroupMain.clearGroupId}" onclick="return confirmx('确认要删除条信息吗？', this.href)" title="<spring:message code='common.delete' />" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td></shiro:hasPermission>	

			</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>