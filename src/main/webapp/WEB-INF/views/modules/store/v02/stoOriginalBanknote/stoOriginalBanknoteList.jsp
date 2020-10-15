<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 原封新券管理管理 -->
	<title><spring:message code="store.original.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			setToday(".createTime");
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 原封新券入库列表 -->
		<li class="active"><a href=""><spring:message code="store.original.inList"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoOriginalBanknote" action="${ctx}/v02/stoOriginalBanknote/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div class="row">
			<div class="span">
				<!-- 套别 -->
				<label><spring:message code="common.edition"/>：</label>
				<form:select path="sets" class="input-medium">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('sto_original_sets')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<div class="span">
				<!-- 原封箱流水 -->
				<label><spring:message code="store.original.boxNo"/>：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-small"/>
			</div>
			<div class="span">
				<!-- 面值 -->
				<label><spring:message code="common.denomination"/>：</label>
				<form:select path="denomination" class="input-medium">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('sto_original_denomination')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<div class="span">
				<!-- 入库操作人 -->
				<label><spring:message code="store.original.inUser"/>：</label>
				<form:input path="outName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</div>
			<div class="span">
				<!-- 入库日期 -->
				<label><spring:message code="store.original.inDate"/>：</label>
				<input id="createDate"  name="createDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					   value="<fmt:formatDate value="${stoOriginalBanknote.createDate}" pattern="yyyy-MM-dd"/>" 
					   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});"/>
				<!-- 查询 -->
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search"/>" />
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 套别 -->
				<th><spring:message code="common.edition"/></th>
				<!-- 原封箱流水 -->
				<th><spring:message code="store.original.boxNo"/></th>
				<!-- 原封券翻译 -->
				<th><spring:message code="store.original.translation"/></th>
				<!-- 单位 -->
				<th><spring:message code="common.units"/></th>
				<!-- 面值 -->
				<th><spring:message code="common.denomination"/></th>
				<!-- 金额 -->
				<th><spring:message code="common.amount"/></th>
				<!-- 入库机构 -->
				<th><spring:message code="store.original.inOffice"/></th>
				<!-- 入库操作人 -->
				<th><spring:message code="store.original.inUser"/></th>
				<!-- 入库操时间 -->
				<th><spring:message code="store.original.inDate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoOriginalBanknote" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${fns:getDictLabel(stoOriginalBanknote.sets,'sto_original_sets',"")}</td>
				<td>${stoOriginalBanknote.id}</td>
				<td>${stoOriginalBanknote.originalTranslate}</td>
				<!-- 箱 -->
				<td><spring:message code="store.original.box"/></td>
				<td>${fns:getDictLabel(stoOriginalBanknote.denomination,'sto_original_denomination',"")}</td>
				<td><fmt:formatNumber value="${stoOriginalBanknote.amount}" type="currency" pattern="#,#00.00#"/></td>
				<td>${stoOriginalBanknote.roffice.name}</td>
				<td>${stoOriginalBanknote.createName}</td>
				<td><fmt:formatDate value="${stoOriginalBanknote.createDate}" pattern="yyyy-MM-dd" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>