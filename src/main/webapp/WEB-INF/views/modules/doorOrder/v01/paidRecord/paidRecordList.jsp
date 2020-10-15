<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>汇款记录保存管理</title>
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
		<li class="active"><a href="${ctx}/doorOrder/v01/paidRecord/">汇款记录列表</a></li>
		 <shiro:hasPermission name="doororder.v01:paidRecord:edit"><li><a href="${ctx}/doorOrder/v01/paidRecord/form">汇款记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="paidRecord" action="${ctx}/doorOrder/v01/paidRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<%-- 商户名称 --%>
			<label class="control-label">商户名称：</label>
				<sys:treeselect id="merchanOfficeId" name="merchanOfficeId"
				value="${paidRecord.merchanOfficeId}" labelName="merchanOfficeName"
				labelValue="${paidRecord.merchanOfficeName}" title="商户名称"
				url="/sys/office/treeData"  allowClear="true"  type="9"
				cssClass="required input-small" isAll="true"  clearCenterFilter="true"/>
		<!-- 开始日期 -->
		<label><spring:message code="door.accountManage.paidTime" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${paidRecord.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<!-- end -->
		<!-- 结束日期 -->
		<label>~</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${paidRecord.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>&nbsp;&nbsp;
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
	
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 商户名称 -->
				<th><spring:message code="door.accountManage.store" /></th>
				<!-- 受理编号 -->
				<th><spring:message code="door.accountMacage.toAcceptTheNumber"/></th>
				<!-- 交易流水号 -->
				<th><spring:message code="door.accountMacage.tradeSerialNumber"/></th>
				<!-- 汇款金额 -->
				<th><spring:message code="door.accountManage.paidAmount"/></th>
				<!-- 汇款发生机构 -->
				<th><spring:message code="door.accountMacage.paidOfficeName"/></th>
				<!-- 汇款时间 -->
				<th><spring:message code="door.accountManage.paidTime"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="paidRecord" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${paidRecord.merchanOfficeName}</td>
				<td>${paidRecord.toAcceptTheNumber}</td>
				<td>${paidRecord.tradeSerialNumber}</td>
				<td><fmt:formatNumber value="${paidRecord.paidAmount}" pattern="#,##0.00#" /></td>
				<td>${paidRecord.recordOfficeName}</td>
				<td><fmt:formatDate value="${paidRecord.createDate}"  pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<shiro:hasPermission name="doororder.v01:paidRecord:edit">
				<td>
				<a href="${ctx}/doorOrder/v01/paidRecord/delete?id=${paidRecord.id}" onclick="return confirmx('确认要删除该汇款记录吗？', this.href)" title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>