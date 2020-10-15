<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>冠字号查询</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	
</head>
<body>
	<div class="row">
		<form:form id="searchForm" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
				
			<%-- 冠字号码 --%>
			<label>冠字号码：</label>
			<input type="text" id="bankName" name="bankName" class="input-small">
			<%-- 查询 --%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				
			
		</form:form>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 银行名称 --%>
				<th>银行名称</th>
				<%-- 冠字号码 --%>
				<th>冠字号码</th>
				<%-- 所属包号 --%>
				<th>所属包号</th>
				<%-- 面值 --%>
				<th>面值</th>
				<%-- 登记日期 --%>
				<th>登记日期</th>
				<%-- 类别 --%>
				<th>类别</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- 冠字号码 --%>
				<td>jl245636654</td>
				<%-- 所属包号--%>
				<td>BA52475424</td>
			    <%-- 面值--%>
				<td>100</td>
				<%-- 登记日期--%>
				<td>2016-07-05</td>
				<%-- 类别--%>
				<td>假币</td>
				
			</tr>
			<tr>
			    <%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- 冠字号码 --%>
				<td>jl245636654</td>
				<%-- 所属包号--%>
				<td>BA52475424</td>
			    <%-- 面值--%>
				<td>100</td>
				<%-- 登记日期--%>
				<td>2016-07-05</td>
				<%-- 类别--%>
				<td></td>
			</tr>
			
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
