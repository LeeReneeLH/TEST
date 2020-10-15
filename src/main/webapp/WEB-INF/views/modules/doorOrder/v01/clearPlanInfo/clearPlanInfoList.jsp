<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="door.clearPlan.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<!-- 清机任务列表 -->
		<li class="active"><a href="${ctx}/doorOrder/v01/clearPlanInfo/"><spring:message code="door.clearPlan.list" /></a></li>
		<shiro:hasPermission name="doorOrder:v01:clearPlanInfo:edit">
			<li><a href="${ctx}/doorOrder/v01/clearPlanInfo/form"><spring:message code="door.clearPlan.form" /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="clearPlanInfo" action="${ctx}/doorOrder/v01/clearPlanInfo/" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<!-- <div class="span14" style="margin-top:5px"> -->
				<%-- 人员编号 --%>
				<%-- <label><spring:message code="door.clearPlan.perId" />：</label>
				<form:input path="clearManNo" htmlEscape="false" maxlength="15" class="input-medium"/> --%>
				<%-- 人员名称 --%>
				 <%-- <label><spring:message code="door.clearPlan.perName" />：</label>
				<form:input path="clearManName" htmlEscape="false" maxlength="15" class="input-medium"/>  --%>
				
				<%-- 任务编号 --%>
				<div>
					<label><spring:message code="door.clearPlan.planNo" />：</label>
					<form:input path="id" htmlEscape="false" maxlength="15" class="input-small"/>
				</div>
				<%-- 设备编号 --%>
				<div>
					<label><spring:message code="door.clearPlan.equipmentName" />：</label>
					<form:input path="equipmentName" htmlEscape="false" maxlength="15" class="input-small"/>
				</div>
				<%-- 清机状态 --%>
				<div>
					<label><spring:message code="door.clearPlan.status" />：</label>
					<form:select path="status" class="input-medium required" id ="selectStatus">
						<option value=""><spring:message code="common.select" /></option>
						<form:options items="${fns:getDictList('door_clear_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</div>
				<%-- 开始日期 --%>
				<div>
					<label><spring:message code="door.clearPlan.date" />：</label>
					<input 	id="createTimeStart"  
							name="createTimeStart" 
							type="text" 
							readonly="readonly" 
							maxlength="20" 
							class="input-small Wdate createTime" 
							value="<fmt:formatDate value="${clearPlanInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
							onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
				</div>
				<%-- 结束日期 --%>
				<div>
					<label>~</label>
					<input 	id="createTimeEnd" 
							name="createTimeEnd" 
							type="text" 
							readonly="readonly" 
							maxlength="20" 
							class="input-small Wdate createTime" 
							value="<fmt:formatDate value="${clearPlanInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
							onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
				</div>
				&nbsp;&nbsp;
				<%-- 查询 --%>
				<div>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
            <%-- 任务编号 --%>
            <th class="sort-column a.id" ><spring:message code="door.clearPlan.planNo" /></th>
			<%-- 机具编号 --%>
			<th class="sort-column e.series_number" ><spring:message code="door.clearPlan.equipmentName" /></th>
			<%-- 清机组 --%>
			<th class="sort-column g.clear_group_name" ><spring:message code="door.clearPlan.group" /></th>
			<%-- 清机状态 --%>
			<th class="sort-column a.status" ><spring:message code="door.clearPlan.status" /></th>
			<%-- 任务类型 --%>
			<th class="sort-column a.plan_type"><spring:message code="door.clearPlan.type" /></th>
			<%-- 清机时间 --%>
			<th class="sort-column a.update_date"><spring:message code="door.clearPlan.date" /></th>
			<%-- 操作 --%>
			<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ClearPlanInfo" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
                <td>${ClearPlanInfo.id}</td>
				<td>${ClearPlanInfo.equipmentName}</td>
			 	<td>${ClearPlanInfo.clearingGroupName}</td>
			 	<td>
			 		<!-- 状态颜色动态配置 gzd 2020-05-26  -->
					${fns:getDictLabelWithCss(ClearPlanInfo.status,'door_clear_status',"",true)}
				    <%-- <c:if test="${ClearPlanInfo.status eq 0}">
						<spring:message code="door.clearPlan.unfinished"/>
					</c:if>
					<c:if test="${ClearPlanInfo.status eq 1}">
						<spring:message code="door.clearPlan.finished"/>
					</c:if>
                    <c:if test="${ClearPlanInfo.status eq 2}">
                        <spring:message code="door.clearPlan.reverse"/>
                    </c:if> --%>
				</td>
				<td>
					<c:if test="${ClearPlanInfo.planType eq '01'}">
						固定任务
					</c:if>
					<c:if test="${ClearPlanInfo.planType eq '02'}">
						临时任务
					</c:if>
				</td>
				<td><fmt:formatDate value="${ClearPlanInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				
				<td>
			    <c:if test="${ClearPlanInfo.status == '0' && ClearPlanInfo.planType eq '02'}">
			    	<a href="${ctx}/doorOrder/v01/clearPlanInfo/form?id=${ClearPlanInfo.id}" title="编辑">
						<i class="fa fa-edit text-green fa-lg"></i>
					</a>
					&nbsp;&nbsp;
				</c:if>
					<a href="${ctx}/doorOrder/v01/clearPlanInfo/detail?id=${ClearPlanInfo.id}" title="查看">
						<i class="fa fa-eye  fa-lg"></i>
					</a>
					&nbsp;&nbsp;
				<%-- <c:if test="${ClearPlanInfo.status == '1'}">
					<a href="${ctx}/doorOrder/v01/clearPlanInfo/delete?id=${ClearPlanInfo.id}&planId=${ClearPlanInfo.planId}"
						onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
						<i class="fa fa-trash-o  text-red fa-lg"></i>
					</a>
					&nbsp;&nbsp;
				</c:if> --%>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>