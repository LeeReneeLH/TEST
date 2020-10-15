<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 预约清分列表 -->
	<title><spring:message code="clear.orderClear.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/orderClear/list");
				$("#searchForm").submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 预约清分列表 -->
		<li class="active"><a href="${ctx}/clear/v03/orderClear/list" ><spring:message code="clear.orderClear.title" /><spring:message code="common.list" /></a></li>
		<shiro:hasAnyPermissions name="clear:orderClear:edit">
			<!-- 预约清分登记 -->
			<li><a href="${ctx}/clear/v03/orderClear/form"><spring:message code="clear.orderClear.title" /><spring:message code="common.register" /></a></li>
		</shiro:hasAnyPermissions>
	</ul>
	<div class="row">
	<form:form id="updateForm" modelAttribute="updateParam" action="" method="post" >
		</form:form>
		<form:form id="searchForm" modelAttribute="orderClearInfo" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<!-- 清分机构 -->
					<label><spring:message code="clear.orderClear.office" />：</label>
					<form:select path="rOffice.id" id="custNo"
							class="input-large required" disabled="disabled">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${sto:getStoCustList('6',false)}"
								itemLabel="name" itemValue="id" htmlEscape="false" />
						</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
					
					<%-- 状态 --%>
					<label><spring:message code="common.status" />：</label>
					<form:select path="searchStatus" class="input-small required" id ="selectStatus">
						<%-- 请选择 --%>
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
						<form:options items="${fns:getDictList('cl_order_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;&nbsp;&nbsp;&nbsp;
					
					<!-- 登记日期 -->
					<label><spring:message code="common.startDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${orderClearInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
					<!-- 结束日期 -->
					<label><spring:message code="common.endDate" />：</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${orderClearInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
					&nbsp;&nbsp;&nbsp;
					<!-- end -->
					<!-- 查询 -->
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>

			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 预约单号 -->
				<th class="sort-column a.in_no"><spring:message code="clear.orderClear.orderNo"/></th>
				<!-- 清分机构 -->
				<th class="sort-column a.clear_office"><spring:message code="clear.orderClear.office"/></th>
				<!-- 预约金额 -->
				<th><spring:message code="clear.orderClear.orderAmount"/></th>
				<!-- 状态 -->
				<th class="sort-column a.status"><spring:message code="common.status"/></th>
				
				<!-- 登记机构 -->
				<th class="sort-column o1.name"><spring:message code="clear.orderClear.registerOffice"/></th>
				
				
				<!-- 登记人 -->
				<th class="sort-column d.name"><spring:message code="clear.public.registerName"/></th>
				<!-- 登记日期 -->
				<th class="sort-column a.register_date"><spring:message code="clear.orderClear.registerDate"/></th>
				<%-- 申请方式 --%>
				<th class="sort-column a.method"><spring:message code="clear.orderClear.method"/></th>
				<!-- 操作 -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocate" varStatus="statusIndex">
				<tr>
					<!-- 预约单号 -->
					<td><a href="${ctx}/clear/v03/orderClear/view?inNo=${allocate.inNo}">
						${allocate.inNo}
					</a></td>
					<!-- 客户名称 -->
					<td>
						${allocate.rOffice.name}
					</td>
					<!-- 金额 -->
					<td  style="text-align:right;"><fmt:formatNumber value="${allocate.inAmount}" pattern="#,##0.00#" /></td>
					<!-- 状态 -->
					<td>${fns:getDictLabel(allocate.status,'cl_order_type',"")}</td>
					<!-- 登记机构 -->
					<td>${allocate.registerOfficeNm}</td>	
					<!-- 登记人 -->
					<td>${allocate.registerName}</td>	
					<!-- 登记日期 -->
					<td><fmt:formatDate value="${allocate.registerDate}" pattern="yyyy-MM-dd"/></td>
					<!-- 申请方式 -->
					<td>${fns:getDictLabel(allocate.method, 'guest_method', '')}</td>
					<!-- 操作 -->
					<td>
						<shiro:hasPermission name="clear:orderClear:edit">
							<c:if test="${'1' eq allocate.status}">
								<c:if test="${'1' eq allocate.method}">
								<!-- 编辑 -->
			    				<a href="${ctx}/clear/v03/orderClear/form?inNo=${allocate.inNo}" title="<spring:message code='common.edit'/>">
			    					<i class="fa fa-edit text-green fa-lg"></i>
			    				</a>&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test="${'2' eq allocate.method}">
								<!-- 查看 -->
			    				<a href="${ctx}/clear/v03/orderClear/view?inNo=${allocate.inNo}" title="<spring:message code='common.view'/>">
			    					<i class="fa fa-eye  fa-lg"></i>
			    				</a>&nbsp;&nbsp;&nbsp;
								</c:if>
		    				<!-- 删除 -->
							<a  href="${ctx}/clear/v03/orderClear/delete?inNo=${allocate.inNo}" onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"  title="<spring:message code='common.delete'/>" style="margin-left:10px;">
								<i class="fa fa-trash-o text-red fa-lg"></i>
							</a>
							</c:if>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
