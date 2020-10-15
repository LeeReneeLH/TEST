<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 单据印章管理列表 --%>
	<title>单据印章管理列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/store/v02/stoDocStamperMgr/list");
				$("#searchForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 单据印章管理列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;">单据印章管理列表</a></li>
		<shiro:hasPermission name="store.v02:stoDocStamperMgr:regedit">
			<c:if test="${fns:getUser().office.type == '3'}">
				<%-- 单据印章登记(link) --%>
				<li><a href="${ctx}/store/v02/stoDocStamperMgr/form?operationType=toRegistPage">单据印章登记</a></li>
			</c:if>
		</shiro:hasPermission>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="stoDocTempInfo" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			
			<div class="row">
				<div class="span8" style="margin-top:5px">
					<%-- 机构名称 --%>
					<label>机构名称：</label>
					<sys:treeselect id="officeId" name="office.id"
							value="${stoDocTempInfo.office.id}" labelName="office.name"
							labelValue="${stoDocTempInfo.office.name}" title="机构名称"
							url="/sys/office/treeData" cssClass="required input-small" 
							allowClear="true" notAllowSelectParent="true" notAllowSelectRoot="false"
							type="3" isNotNeedSubPobc="true" isAll="false" />
					
					<%-- 单据类型 --%>
					<label>单据类型：</label>
					<form:select path="documentType" id="documentType" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('DOCUMENT_TYPE')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
				<div class="span2" style="margin-top:5px">
					
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 机构名称 --%>
				<th>机构名称</th>
				<%-- 单据类型 --%>
				<th>单据类型</th>
				<%-- 申请类型 --%>
				<th><spring:message code="allocation.application.type" /></th>
				<%-- 状态 --%>
				<th><spring:message code="common.status" /></th>
				<%-- 创建人 --%>
				<th>创建人</th>
				<%-- 创建时间 --%>
				<th>创建时间</th>
				<%-- 更新人 --%>
				<th>更新人</th>
				<%-- 更新时间--%>
				<th>更新时间</th>
				<%-- 操作（删除/修改） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="stoDocTempInfo" varStatus="status">
				<tr>
					<%-- 机构名称 --%>
					<td>${stoDocTempInfo.office.name }</td>
					<%-- 单据类型 --%>
					<td>${fns:getDictLabel(stoDocTempInfo.documentType,'DOCUMENT_TYPE',"")}</td>
					<%-- 申请类型 --%>
					<td>${fns:getDictLabel(stoDocTempInfo.businessType, 'all_businessType', '')}</td>
					<%-- 状态 --%>
					<td>
						<%-- 代理上缴 --%>
						<c:if test="${stoDocTempInfo.businessType == '52' }">
							${fns:getDictLabel(stoDocTempInfo.status,'pboc_order_handin_status',"")}
						</c:if>
						<%-- 申请下拨 --%>
						<c:if test="${stoDocTempInfo.businessType == '51' }">
							${fns:getDictLabel(stoDocTempInfo.status,'pboc_order_quota_status',"")}
						</c:if>
						<%-- 申请上缴 --%>
						<c:if test="${stoDocTempInfo.businessType == '50' }">
							${fns:getDictLabel(stoDocTempInfo.status,'pboc_order_handin_status',"")}
						</c:if>
					</td>
					<%-- 创建人  --%>
					<td>${stoDocTempInfo.createName}</td>
					<%-- 创建时间  --%>
					<td><fmt:formatDate value="${stoDocTempInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 更新人  --%>
					<td>${stoDocTempInfo.updateName}</td>
					<%-- 更新时间  --%>
					<td><fmt:formatDate value="${stoDocTempInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 操作 --%>
					<td>
						<shiro:hasPermission name="store.v02:stoDocStamperMgr:addPbocStamper">
							<a href="${ctx}/store/v02/stoDocStamperMgr/form?id=${stoDocTempInfo.id}&operationType=toAddPbocStamperPage" title="添加印章">
								<i class="fa fa-plus-square fa-lg"></i>
							</a>
						</shiro:hasPermission>
						<a href="${ctx}/store/v02/stoDocStamperMgr/form?id=${stoDocTempInfo.id}&operationType=toShowDetailPage" title="查看" style="margin-left:10px;"><i class="fa fa-eye fa-lg"></i><%-- <spring:message code="common.view" /> --%></a>
						<shiro:hasPermission name="store.v02:stoDocStamperMgr:update">
							<a href="${ctx}/store/v02/stoDocStamperMgr/form?id=${stoDocTempInfo.id}&operationType=toUpdatePage" title="编辑" style="margin-left:10px;">
								<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
							</a>
						</shiro:hasPermission>
						<shiro:hasPermission name="store.v02:stoDocStamperMgr:delete">
							<a href="${ctx}/store/v02/stoDocStamperMgr/delete?id=${stoDocTempInfo.id}"
								onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除" style="margin-left:10px;">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
							</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
