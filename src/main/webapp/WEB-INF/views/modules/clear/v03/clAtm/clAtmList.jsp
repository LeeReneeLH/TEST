<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%> 
<html>
<head>
	<!-- ATM钞箱拆箱 -->
	<title><spring:message code="clear.atmCashBox.title" /></title>
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
		
		
		// 删除处理
		function rowDelete(v_doorId) {
			var url = "${ctx}/door/clAtmMain/delete?id=" + v_doorId ;
			$("#inputForm").attr("action", url);
			$("#inputForm").submit();
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/clear/v03/clAtm/"><spring:message code="clear.atmCashBox.title" /><spring:message code="common.list" /></a></li>
		<shiro:hasPermission name="clear:clAtm:edit"><li><a href="${ctx}/clear/v03/clAtm/form"><spring:message code="clear.atmCashBox.add" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="inputForm" modelAttribute="clAtmMain" action="" method="post" class="form-horizontal">
	</form:form>
	
	<div class="row">
		<form:form id="searchForm" modelAttribute="clAtmMain" action="${ctx}/clear/v03/clAtm/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 机构 --%>
					<label><spring:message code="clear.atmCashBox.office"/> ：</label>
					<sys:treeselect id="searchCustName" name="searchCustNo"
							value="${clAtmMain.searchCustNo}" labelName="searchCustName"
							labelValue="${clAtmMain.searchCustName}" title="<spring:message code='common.office'/>"
							url="/sys/office/treeData" cssClass="required input-xsmall"
							notAllowSelectParent="false" notAllowSelectRoot="false"   maxType="4" isNotNeedSubPobc="true"
						    isAll="true"  allowClear="true"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<%-- 登记日期 --%>
					<label><spring:message code="clear.atmCashBox.registerDate"/> ：</label>
					<input id="regTimeStart"  name="regTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${clAtmMain.regTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'regTimeEnd\')}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					&nbsp;
					<input id="regTimeEnd" name="regTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${clAtmMain.regTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'regTimeStart\')}'});"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
				<!-- 序号 -->
				<th><spring:message code="clear.atmCashBox.no"/></th>
				<!-- 拆箱单号 -->
				<th class="sort-column a.out_no"><spring:message code="clear.atmCashBox.code"/></th>
				<!-- 机构名称 -->
				<th class="sort-column a.cust_no"><spring:message code="clear.atmCashBox.officeName"/></th>
				<!-- 清机金额(元) -->
				<th><spring:message code="clear.atmCashBox.clearAmount"/></th>
				<!-- 实际金额(元) -->
				<th><spring:message code="clear.atmCashBox.realAmountA"/></th>
				<!-- 差额(元) -->
				<th><spring:message code="clear.atmCashBox.diffAmount"/></th>
				<!-- 总笔数 -->
				<th><spring:message code="clear.atmCashBox.sumCount"/></th>
				<!-- 登记人 -->
				<th class="sort-column a.create_name"><spring:message code="clear.public.registerName"/></th>
				<!-- 登记时间 -->
				<th class="sort-column a.reg_date"><spring:message code="common.registerTime"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clAtmMain" varStatus="status">
			<tr>
			 	<td>${status.index + 1}</td>
			 	<td>
			 		<a href="${ctx}/clear/v03/clAtm/view?id=${clAtmMain.outNo}">${clAtmMain.outNo}</a>
			 	</td>
			    <td>${clAtmMain.custName}</td>
				<td style="text-align:right;"><fmt:formatNumber value="${clAtmMain.inputAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;"><fmt:formatNumber value="${clAtmMain.checkAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;"><fmt:formatNumber value="${clAtmMain.diffAmount}" type="currency" pattern="#,##0.00"/></td>
				<td >${clAtmMain.boxCount}</td>
				<td >${clAtmMain.createBy.name}</td>
				<td><fmt:formatDate value="${clAtmMain.regDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
					<shiro:hasPermission name="clear:clAtm:edit">
						<!-- 修改 -->
						<a href="${ctx}/clear/v03/clAtm/form?id=${clAtmMain.outNo}" title="<spring:message code='common.modify'/>">
							<i class="fa fa-edit text-green  fa-lg"></i>
						</a>
						&nbsp;&nbsp;&nbsp;
						<!-- 删除 -->
						<a id="aDelete" href="${ctx}/clear/v03/clAtm/delete?id=${clAtmMain.outNo}"
							onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="<spring:message code='common.delete'/>">
							<i class="fa fa-trash-o text-red  fa-lg"></i>
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