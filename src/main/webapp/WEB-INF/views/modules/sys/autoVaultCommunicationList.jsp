<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 通信列表 --%>
	<title>通信列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				// $("#searchForm").attr("action", "${ctx}/sys/autoVaultCommunication/list");
				$("#searchForm").attr("action", "${ctx}/sys/autoVaultCommunication/save");
				$("#searchForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 通信列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">通信列表</a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="autoVaultCommunication" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span7" style="margin-top:5px">
					<%-- 通信状态 --%>
					<label>通信状态：</label>
					<form:select path="status" id="status" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('COMMUNICATION_STATUS')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					&nbsp;
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
				<%-- 通信消息 --%>
				<th>通信消息</th>
				<%-- 通信状态 --%>
				<th>通信状态</th>
				<%-- 创建人 --%>
				<th>创建人</th>
				<%-- 创建时间 --%>
				<th>创建时间</th>
				<%-- 更新人 --%>
				<th>更新人</th>
				<%-- 更新时间 --%>
				<th>更新时间</th>
				<%-- 操作 --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="mes" varStatus="status">
				<c:choose>
					<c:when test="${mes.status == '01' || mes.status == '03'}">
						<tr bgcolor="#FFCOCB">
					</c:when>
					<c:otherwise>
						<tr>
					</c:otherwise>
				</c:choose>
					<%-- 通信消息 --%>
					<td>${mes.message}</td>
					<%-- 通信状态 --%>
					<td>${fns:getDictLabel(mes.status,'COMMUNICATION_STATUS','')}</td>
					<%-- 创建人 --%>
					<td>${mes.createBy.name}</td>
					<%-- 创建时间 --%>
					<td><fmt:formatDate value="${mes.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 更新者 --%>
					<td>${mes.updateBy.name}</td>
					<%-- 更新时间 --%>
					<td><fmt:formatDate value="${mes.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 操作 --%>
					<td>
						<a href="${ctx}/sys/autoVaultCommunication/send?id=${mes.id}" title="发送数据">
							<i class="fa fa-edit text-green fa-lg"></i>
						</a> 
						&nbsp;
						<c:if test="${mes.status == '01' || mes.status == '03'}">
							<a href="${ctx}/sys/autoVaultCommunication/change?id=${mes.id}&strUpdateDate=${mes.strUpdateDate}" 
							onclick="return confirmx('<spring:message code="message.I00010"/>', this.href)" title="切换状态">
								<i class="fa fa-ban text-red fa-lg"></i>
							</a>
							&nbsp;
						</c:if>
						<a href="${ctx}/sys/autoVaultCommunication/view?id=${mes.id}" title="详情"><i class="fa fa-eye fa-lg"></i></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
