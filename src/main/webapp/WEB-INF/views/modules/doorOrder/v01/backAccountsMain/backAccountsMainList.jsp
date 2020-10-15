<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>回款管理管理</title>
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
		<shiro:hasPermission name="doororder.v01:backAccountsMain:view"><li class="active"><a href="${ctx}/doorOrder/v01/backAccountsMain/">回款信息列表</a></li></shiro:hasPermission>
		<shiro:hasPermission name="doororder.v01:backAccountsMain:edit"><li><a href="${ctx}/doorOrder/v01/backAccountsMain/form">回款信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="backAccountsMain" action="${ctx}/doorOrder/v01/backAccountsMain/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<div>
		<!-- 流水号 -->
		<label class="control-label"><spring:message code="door.accountManage.businessId" />：</label>
		<form:input path="businessId" htmlEscape="false" maxlength="20" class="input-small" />
		</div>
		<!-- 公司 -->
		<div>
		<label class="control-label"><spring:message
				code="common.company" />：</label>
		<form:select path="companyId" id="companyId"
			class="input-medium required" disabled="disabled">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getPlatform()}"
				itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select>
		</div>
		<div>
		<form:hidden path="companyName" />
		</div>
		<!-- 门店 -->
		<div>
			<label class="control-label"><spring:message code="door.doorOrderException.cust" />：</label>
			<sys:treeselect id="doorName" name="doorId"
				value="${backAccountsMain.doorId}" labelName="doorName"
				labelValue="${backAccountsMain.doorName}" title="<spring:message code='door.doorOrderException.cust' />"
				url="/sys/office/treeData" cssClass="required input-small"
				notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
				isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
		</div>
		<!-- 回款单号 -->
		<div>
		<label class="control-label"><spring:message code="door.accountManage.backNo" />：</label>
		<form:input path="backNumber" htmlEscape="false" maxlength="20" class="input-small" />
		</div>
		<!-- 开始日期 -->
		<div>
		<label><spring:message code="clear.centerAccounts.tradingHours" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${backAccountsMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		</div>
		<!-- 结束日期 -->
		<div>
		<label>~</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${backAccountsMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>&nbsp;
		<!-- end -->
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
				<th class="sort-column a.business_id"><spring:message code="door.accountManage.businessId" /></th>
				<!-- 增加门店  回款单号显示 gzd 2019-11-29 begin -->
				<!-- 门店 -->
				<th class="sort-column o7.name"><spring:message code="door.public.cust" /></th>
				<!-- 回款单号 -->
				<th class="sort-column a.back_number"><spring:message code="door.accountManage.backNo" /></th>
				<!-- end -->
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th class="sort-column o6.name"><spring:message code="clear.orderClear.office"/></th>
				<%-- 流水单号 --%>
				<!-- 操作类型 -->
				<th class="sort-column a.status"><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 借方(元) -->
				<th class="sort-column a.out_amount"><spring:message code="clear.centerAccounts.centerBackAmount"/></th>
				<!-- 贷方(元) -->
				<%--<th><spring:message code="clear.centerAccounts.lender"/></th>--%>
				<!-- 交易时间 -->
				<th class="sort-column a.create_date"><spring:message code="clear.centerAccounts.tradingHours"/></th>
				<!-- 公司名称 -->
				<th class="sort-column o5.name"><spring:message code="door.accountManage.companyName" /></th>
				<!-- 操作 -->
				<shiro:hasPermission name="doororder.v01:backAccountsMain:edit">
					<th><spring:message code="common.operation"/></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="backAccountsMain" varStatus="status">
			<tr>
				    <td>
						${status.index+1}
					</td>
					<td>
			 		<a href="${ctx}/doorOrder/v01/backAccountsMain/form?id=${backAccountsMain.id}">${backAccountsMain.businessId}</a>
			 		</td>
					<!-- 增加包号 门店显示 gzd 2019-11-29 begin -->
				    <td>
						${backAccountsMain.doorName}
					</td>
				    <td>
						${backAccountsMain.backNumber}
					</td>
					<!-- end -->
			 	    <!-- 增加机构 wzj 2017-11-27 begin -->
					<td>
					${backAccountsMain.officeName} 
					</td>
					<td>
					<!-- update hzy 配置颜色  2020/05/26 start -->
						${fns:getDictLabelWithCss(backAccountsMain.status, "cl_status_type", "未命名", true)}
					<!-- update hzy 配置颜色  2020/05/26 end -->
					</td>
					<td>
						<c:choose>
							<c:when test="${backAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${backAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<%--<td>
						<fmt:formatNumber value="0" pattern="#,##0.00#" />
					</td>--%>
					<td>
						<fmt:formatDate value="${backAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${backAccountsMain.companyName}
					</td>
					<!-- end -->
				<shiro:hasPermission name="doororder.v01:backAccountsMain:edit">
				<td>
				<%-- 冲正过的不再显示冲正按钮 --%>
				<c:if test="${backAccountsMain.status eq '1'}">
   				<span style='width:25px;display:inline-block;'> 
				<%-- 冲正 --%>
				<a id="aReverse" href="${ctx}/doorOrder/v01/backAccountsMain/reverse?id=${backAccountsMain.id}"
						onclick="return confirmx('是否确认冲正？', this.href)" title="<spring:message code='common.reverse' />">
				<i class="fa fa-exchange"></i>
				</a>
				</span>
				</c:if>
				<%-- 查看 --%>
				<a href="${ctx}/doorOrder/v01/backAccountsMain/form?id=${backAccountsMain.id}" title="<spring:message code='common.view' />">
									<i class="fa fa-eye  fa-lg"></i>
				</a>
				</td>
				</shiro:hasPermission>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>