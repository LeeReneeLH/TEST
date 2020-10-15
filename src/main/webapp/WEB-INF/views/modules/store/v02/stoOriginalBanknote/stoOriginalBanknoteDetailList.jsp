<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>原封新券管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="">原封新券出库明细</a></li>
	</ul>
	<div class="breadcrumb form-search">
		<label>出库编号：</label><label>${outId}</label>
		<label>出库机构：</label><label>${cofficeName}</label>
		<label>出库总金额：</label><label><fmt:formatNumber value="${totalAmount}" type="currency" pattern="#,#00.00#"/></label>
	</div>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th>序号</th>
				<th>套别</th>
				<th>原封箱流水</th>
				<th>原封券翻译</th>
				<th>面值</th>
				<th>金额</th>
				<th>入库机构</th>
				<th>入库操作人</th>
				<th>入库操时间</th>
				<th>出库操作人</th>
				<th>出库操时间</th>
				<th>是否回收</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="stoOriginalBanknote" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${fns:getDictLabel(stoOriginalBanknote.sets,'sto_original_sets',"")}</td>
				<td>${stoOriginalBanknote.id}</td>
				<td>${stoOriginalBanknote.originalTranslate}</td>
				<td>${fns:getDictLabel(stoOriginalBanknote.denomination,'sto_original_denomination',"")}</td>
				<td><fmt:formatNumber value="${stoOriginalBanknote.amount}" type="currency" pattern="#,#00.00#"/></td>
				<td>${stoOriginalBanknote.roffice.name}</td>
				<td>${stoOriginalBanknote.createName}</td>
				<td><fmt:formatDate value="${stoOriginalBanknote.createDate}" pattern="yyyy-MM-dd" /></td>
				<td>${stoOriginalBanknote.outName}</td>
				<td><fmt:formatDate value="${stoOriginalBanknote.outDate}" pattern="yyyy-MM-dd" /></td>
				<td>${fns:getDictLabel(stoOriginalBanknote.recoverStatus,'sto_original_recover_status',"")}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>