<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>授权管理</title>
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
		<li class="active"><a href="${ctx}/doorOrder/v01/checkCashAuthorize/list">授权列表</a></li>
	   <shiro:hasPermission name="collection:checkCashAuthorize:edit">
			<li><a href="${ctx}/doorOrder/v01/checkCashAuthorize/form">授权登记</a></li>
	   </shiro:hasPermission>
	</ul>
		<form:form id="searchForm" modelAttribute="checkCashAuthorize" action="${ctx}/doorOrder/v01/checkCashAuthorize" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row search-flex">
			      <!--  
					<div>
						<label class="control-label">机构类型：</label>
							<form:select path="officeType" class="input-small required"
								id="selectOfficeType">
								<option value=""><spring:message code="common.select" /></option>
								<form:options items="${fns:getDictList('sys_office_type')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
					</div>
					-->
			       <%-- 机构 --%>
				   <div>
				   <label><spring:message code="checkCash.authorize.officeId"/>：</label>
				   <sys:treeselect id="officeName" name="officeId"
						value="${checkCashAuthorize.officeId}" labelName="officeName"
						labelValue="${checkCashAuthorize.officeName}" title="机构" url="/sys/office/treeData" cssClass="input-small"
						notAllowSelectParent="false" notAllowSelectRoot="false" minType="0" maxType="9"
					    isAll="true"  allowClear="true" checkMerchantOffice="false" clearCenterFilter="true" checkTopOffice="true"/>
				    </div>
					<%-- 是否设置授权标识 --%>
					<div>
					<label><spring:message code="checkCash.authorize.isUse"/>：</label>
					<form:select path="isUse" class="input-small required" id ="selectStatus">
						<form:option value=""  label="请选择"/>
						<form:option value="0" label="开启"/>
						<form:option value="1" label="关闭"/>
					</form:select>
					</div>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<!--授权类型  -->
					<div>
					<label><spring:message code="checkCash.authorize.type"/>：</label>
					<form:select path="type" class="input-small required" id ="selectStatus">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('authorize_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					</div>
					<%-- 开始日期 --%>
					<div>
					<label><spring:message code="door.checkCash.registerDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						value="<fmt:formatDate value="${checkCashAuthorize.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
					</div>
					<%-- 结束日期 --%>
					<div>
					<label>~</label>
					<input  path id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						value="<fmt:formatDate value="${checkCashAuthorize.createTimeEnd}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
					</div>
					&nbsp;&nbsp;
					<%-- 查询 --%>
					<div>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
					</div>
			</div>
		</form:form>
	</div>
	
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			    <%-- 机构  --%>
				<th class="sort-column o.name"><spring:message code="checkCash.authorize.officeId"/></th>
				<%-- 机构类型  --%>
                <th  class="sort-column a.office_type"><spring:message code="checkCash.authorize.officeType"/></th>
				<%-- 是否开启  --%>
				<th class="sort-column a.is_use"><spring:message code="checkCash.authorize.isUse"/></th>
				<%-- 授权金额  --%>
				<th class="sort-column a.amount"><spring:message code="checkCash.authorize.amount"/></th>
				<%-- 授权类型  --%>
				<th class="sort-column a.type"><spring:message code="checkCash.authorize.type"/></th>
				<%-- 登记时间  --%>
				<th class="sort-column a.create_date"><spring:message code="checkCash.authorize.createDate"/></th>
				<%-- 操作  --%>
				<th><spring:message code="common.operation"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="itemData">
			<tr>
				<td>${itemData.officeName}</td>
				<td>${fns:getDictLabel(itemData.officeType, 'sys_office_type', '')}</td>
				<td>${itemData.isUse eq '1'?'关闭':'开启'}</td>
				<td>${fns:getDictLabel(itemData.expressionType,'expression_type','')}${itemData.amount}</td>
				<td>${fns:getDictLabel(itemData.type, 'authorize_type', '')}</td>
				<td><fmt:formatDate value="${itemData.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td>
				<shiro:hasPermission name="collection:checkCashAuthorize:edit">
				    <c:choose>
							<c:when test="${itemData.isUse eq '1'}">
								<%-- 是否开启? --%>
								<a href="${ctx}/doorOrder/v01/checkCashAuthorize/resumeAuthorize?id=${itemData.id}" 
								onclick="return confirmx('是否开启授权', this.href)" title="开启任务">
									<i class="fa fa-play text-green"></i>
								</a> 
							</c:when>
							<c:otherwise>
								<%-- 是否关闭? --%>
								<a href="${ctx}/doorOrder/v01/checkCashAuthorize/pauseAuthorize?id=${itemData.id}" 
								onclick="return confirmx('是否关闭授权', this.href)" title="关闭任务">
									<i class="fa fa-pause text-red"></i>
								</a> 
							</c:otherwise>
					</c:choose>
					&nbsp;
    				<a href="${ctx}/doorOrder/v01/checkCashAuthorize/form?id=${itemData.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
    				&nbsp;
					<a href="${ctx}/doorOrder/v01/checkCashAuthorize/delete?id=${itemData.id}" onclick="return confirmx('确认要删除该授权信息吗？', this.href)"   title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
					&nbsp;
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>