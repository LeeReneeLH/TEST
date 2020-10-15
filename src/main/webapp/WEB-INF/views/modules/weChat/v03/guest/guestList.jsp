<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户 -->
	<title><spring:message code="door.guest.title" /></title>
	<meta name="decorator" content="default"/>
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
		<!-- 客户列表 -->
		<li class="active"><a href="${ctx}/weChat/v03/Guest/"><spring:message code="door.guest.title" /><spring:message code="common.list" /></a></li>
		<!-- 客户添加 -->
		<shiro:hasPermission name="guest:Guest:edit"><li>
		<a href="${ctx}/weChat/v03/Guest/form"><spring:message code="door.guest.title" /><spring:message code="common.add" /></a>
		</li></shiro:hasPermission>
	</ul>
		<form:form id="searchForm" modelAttribute="guest" action="${ctx}/weChat/v03/Guest/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 用户名 --%>
					<label><spring:message code="door.guest.userName" />：</label>
					<form:input path="gname" id="gname"  maxlength="10" Class="input-small"/>
					<%-- 状态 --%>
					<label><spring:message code="common.status" />：</label>
					<form:select path="grantstatus" class="input-medium required" id ="grantstatus">
						<option value=""><spring:message code="common.select" /></option>
						<form:options items="${fns:getDictList('grantstatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<%-- 业务类型 --%>
					<label><spring:message code="clear.task.business.type" />：</label>
					<form:select path="busType" class="input-medium required" id ="busType">
						<option value=""><spring:message code="common.select" /></option>
						<form:options items="${fns:getFilterDictList('clear_businesstype',true,'73,74')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<%-- 机构 --%>
					<label><spring:message code="common.office" />：</label>
					<sys:treeselect id="gofficeId" name="gofficeId"
							value="${guest.gofficeId}" labelName="gofficeName"
							labelValue="${guest.gofficeName}" title="<spring:message code='common.office' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false"
						    isAll="true"  allowClear="true"/>
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>

			</div>
	
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
			<%-- 用户名 --%>
			<th class="sort-column a.gname"><spring:message code="door.guest.userName" /></th>
			<%-- 手机号码 --%>
			<th><spring:message code="door.guest.tel" /></th>
			<%-- 身份证号 --%>
			<th><spring:message code="door.guest.cardNo" /></th>
			<%-- 申请授权日期 --%>
			<th class="sort-column a.login_date"><spring:message code="door.guest.authDate" /></th>
			<%-- 授权期限 --%>
			<th><spring:message code="door.guest.authTerm" /></th>
			<%-- 机构 --%>
			<th class="sort-column a.goffice_name"><spring:message code="common.office" /></th>
			<%-- 用户类型 --%>
			<th class="sort-column a.guest_type"><spring:message code="door.guest.userType" /></th>
			<%-- 业务类型 --%>
			<th class="sort-column a.bus_type"><spring:message code="clear.task.business.type" /></th>			
			<%-- 状态 --%>
			<th class="sort-column a.grantstatus"><spring:message code="common.status" /></th>
			<%-- 申请方式 --%>
			<th class="sort-column a.method"><spring:message code="door.guest.applyMethod" /></th>
			<%-- 操作 --%>
			<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="Guest" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
			 	<td>${Guest.gname}</td>
			    <td>${Guest.gphone}</td>
				<td>${Guest.gidcardNo}</td>
				<td><fmt:formatDate value="${Guest.loginDate}" pattern="yyyy-MM-dd "/></td>
				<td><fmt:formatDate value="${Guest.grantDate}" pattern="yyyy-MM-dd "/></td>
				<td>${Guest.gofficeName}</td>
				<td>${fns:getDictLabel(Guest.guestType, 'guest_type', '')}</td>
				<td>${fns:getDictLabel(Guest.busType, 'clear_businesstype', '')}</td>
				<td>${fns:getDictLabel(Guest.grantstatus, 'grantstatus', '')}</td>
			    <td>${fns:getDictLabel(Guest.method, 'guest_method', '')}</td>	
				<td>
				 <c:if test="${Guest.grantstatus eq '2'}">
			    	<shiro:hasPermission name="guest:Guest:shiro">
						<span style='width:35px;display:inline-block;'> 
							<%-- 授权 --%>
							<a href="${ctx}/weChat/v03/Guest/form?id=${Guest.id}" title="<spring:message code='door.guest.authorize' />">
								<i class="fa fa-bookmark  fa-lg"></i></a>
							</a>
						</span>
			    	</shiro:hasPermission>
			    </c:if>
			    
			    <c:if test="${Guest.grantstatus eq '1'}">
			    	<shiro:hasPermission name="guest:Guest:edit">
						<span style='width:35px;display:inline-block;'> 
							<%-- 修改 --%>
							<a href="${ctx}/weChat/v03/Guest/form?id=${Guest.id}" title="<spring:message code='common.modify' />">
								<i class="fa fa-edit text-green  fa-lg"></i>
							</a>
						</span>
			    	</shiro:hasPermission>
			    </c:if>
				<shiro:hasPermission name="guest:Guest:edit">
					<span style='width:25px;display:inline-block;'> 
						<%-- 删除 --%>
						<a  href="${ctx}/weChat/v03/Guest/delete?id=${Guest.id}"
							onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="<spring:message code='common.delete' />">
							<i class="fa fa-trash-o text-red  fa-lg"></i>
						</a>
					</span>
				</shiro:hasPermission>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>