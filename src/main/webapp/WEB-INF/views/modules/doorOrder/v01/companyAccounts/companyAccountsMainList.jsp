<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公司账务管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
	
<style>
/* 样式注释改为全局样式 gzd 2020-09-07 */
/* .boxhand-num {
	width: 200px;
	padding: 5px;
	margin: 10px;
	float: left;
	border: 1px solid #eee;
	border-radius: 10px;
}

.text-center {
	text-align: center;
}
.text-red {
    color: #dd4b39 !important;
} */
/* @media screen and (max-height:1080px) {
	.table-con{height:488px}
}
@media screen and (max-height:1200px) and (max-width:1600px){
	.table-con{height:403px}
}
@media screen and (max-height:900px) and (max-width:1440px){
	.table-con{height:400px}
} */
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="doorOrder:v01:companyAccountsMain:view"><li class="active"><a href="${ctx}/doorOrder/v01/companyAccountsMain/">公司账务列表</a></li></shiro:hasPermission>
		<shiro:hasPermission name="doorOrder:v01:companyAccountsMain:edit"><li ><a href="${ctx}/doorOrder/v01/companyAccountsMain/form">公司存款添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="companyAccountsMain" action="${ctx}/doorOrder/v01/companyAccountsMain/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<!-- 公司名称 -->
		<%-- <label class="control-label"><spring:message
				code="common.company" />：</label>
		<form:select path="companyId" id="companyId"
			class="input-large required" disabled="disabled">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getPlatform()}"
				itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select> --%>	
		<div>
		<form:hidden path="companyName" />
		<!-- 业务类型 -->
		<label><spring:message code="clear.task.business.type"/>：</label>
				<form:select path="type" class="input-medium required">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('door_businesstype',true,'75,76,77')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
		</div>
		<div>
		<!-- 开始日期 -->
		<label><spring:message code="clear.centerAccounts.tradingHours" />：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${companyAccountsMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		</div>
		<div>
		<!-- 结束日期 -->
		<label>~</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${companyAccountsMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		</div>
		<div>
			
			<label><spring:message code="door.accountManage.accountHappenOffice" />：</label>
			<%-- <sys:treeselect id="officeId" name="officeId"
				value="${companyAccountsMain.officeId}" labelName="officeName"
				labelValue="${companyAccountsMain.officeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" /> --%>
				
				<sys:treeselect id="officeId" name="officeId"
						value="${companyAccountsMain.officeId}" labelName="${sto:getOfficeById(companyAccountsMain.officeId).name}"
						labelValue="${sto:getOfficeById(companyAccountsMain.officeId).name}"
						title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData"
						cssClass="required input-small required"
						notAllowSelectParent="false" notAllowSelectRoot="false"
						isAll="false" allowClear="true" checkMerchantOffice="false"/>&nbsp;&nbsp;
		</div>
		&nbsp;&nbsp;
		<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</div>
	</form:form>
	<sys:message content="${message}"/>
	<div id="box_sel" class="row" style="margin-left:10px;">
		<div class="boxhand-num text-center">
				<h4 style="font-weight: normal" class="text-red"><spring:message code="door.accountManage.companyNotBackAmount" /></h4>
				<span class="red-bg"><fmt:formatNumber value="${companyNotBackAmount}" pattern="#,##0.00#" /></span>
				
		</div>
	</div>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 账务发生机构 -->
				<th class="sort-column o7.NAME"><spring:message code="door.accountManage.accountHappenOffice"/></th>
				<!-- 业务类型 -->
				<th class="sort-column a.type"><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型 -->
				<th class="sort-column a.business_status"><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 借方(元) -->
				<th class="sort-column a.out_amount"><spring:message code="clear.companyAccounts.payAmount"/></th>
				<!-- 贷方(元) -->
				<th class="sort-column a.in_Amount"><spring:message code="clear.companyAccounts.saveOrBackAmount"/></th>
				<!-- 余额(元) 
				<th><spring:message code="door.accountManage.companyAmount"/></th>-->
				<!-- 交易时间 -->
				<th class="sort-column a.create_date"><spring:message code="clear.centerAccounts.tradingHours"/></th>
				<!-- end -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="companyAccountsMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${companyAccountsMain.custName}
					</td>
					 <td>
						${fns:getDictLabel(companyAccountsMain.type,'door_businesstype',"")}
					</td> 
					 <td>
						${fns:getDictLabelWithCss(companyAccountsMain.businessStatus, 'cl_status_type', '',true)}
					</td> 
					<td>
						<c:choose>
							<c:when test="${companyAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${companyAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${companyAccountsMain.inAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${companyAccountsMain.inAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<!--  
					<td>
						<fmt:formatNumber value="${companyAccountsMain.companyAmount}" pattern="#,##0.00#" />
					</td>
					-->
					<td>
						<fmt:formatDate value="${companyAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>