<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<!-- 任务确认 -->
	<title><spring:message code="door.taskConfirm.title" /></title>
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
	
		
		var submitUrl = "";
		//任务确认
		function confirm(id) {	
			submitUrl = "${ctx}/collection/v03/taskConfirm/confirm?id=" + id;
			var message = "<spring:message code='message.I7212'/>";
			confirmx(message, formSubmit);
		}
		//任务驳回
		function reject(id) {	
			submitUrl = "${ctx}/collection/v03/taskConfirm/reject?id=" + id;
			var message = "<spring:message code='message.I7213'/>";
			confirmx(message, formSubmit);
		}
		//提交处理
		function formSubmit() {
			$("#searchForm").attr("action", submitUrl);
			$("#searchForm").submit();
		}
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 任务确认列表 -->
		<li class="active"><a href="${ctx}/collection/v03/taskConfirm/"><spring:message code="door.taskConfirm.title" /><spring:message code="common.list" /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="taskConfirm" action="" method="post" class="form-horizontal">
	</form:form>
	
	
	<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
	<div class="row">
		<form:form id="searchForm" modelAttribute="taskConfirm" action="${ctx}/collection/v03/taskConfirm/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 门店 --%>
					<label><spring:message code="door.public.cust" /> ：</label>
					<sys:treeselect id="searchDoorName" name="searchDoorId"
							value="${taskConfirm.searchDoorId}" labelName="searchDoorName"
							labelValue="${taskConfirm.searchDoorName}" title="<spring:message code='door.public.cust' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
					&nbsp;
					<%-- 清分人员 --%>
					<%-- <label><spring:message code="door.taskConfirm.clearMan" /> ：</label>
					<form:select path="searchClearManNo" id="searchClearManNo" class="input-large required" style="font-size:15px;color:#000000">
						<form:option value=""><spring:message code="common.select" /></form:option>
						<form:options items="${sto:getUsersByTypeAndOffice(ConClear,fns:getUser().getOffice().getId())}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
					</form:select>
					&nbsp; --%>
					<%-- 状态 --%>
					<label><spring:message code="common.status" />：</label>
					<form:select path="searchAllotStatus" class="input-medium required" id ="selectStatus">
						<option value=""><spring:message code="common.select" /></option>
						<form:options items="${fns:getFilterDictList('task_allot_type',false,'0,1')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;
					<%-- 分配日期 --%>
					<label><spring:message code="door.taskConfirm.allotDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
						value="<fmt:formatDate value="${taskConfirm.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
					<label>~</label>
					&nbsp;
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
						value="<fmt:formatDate value="${taskConfirm.createTimeEnd}" pattern="yyyy-MM-dd"/>"
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
					&nbsp;&nbsp;
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
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 单号 --%>
				<th class="sort-column a.order_id"><spring:message code="door.taskConfirm.codeNo" /></th>
				<%-- 包号 --%>
				<th class="sort-column a.rfid"><spring:message code="door.doorOrder.packNum" /></th>
				<%-- 门店名称 --%>
				<th class="sort-column a.door_name"><spring:message code="door.public.custName" /></th>
				<%-- 总金额 --%>
				<th><spring:message code="common.totalMoney" /></th>
				<%-- 笔数 --%>
				<th><spring:message code="door.taskConfirm.count" /></th>
				<%-- 分配人 --%>
				<th class="sort-column g.name"><spring:message code="door.taskConfirm.allotMan" /></th>
				<%-- 分配日期 --%>
				<th class="sort-column a.allot_date"><spring:message code="door.taskConfirm.allotDate" /></th>
				<%-- 清分人员 --%>
				<th class="sort-column f.escort_name"><spring:message code="door.taskConfirm.clearMan" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.allot_status"><spring:message code="common.status" /></th>	
				<%-- 操作 --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="taskConfirm" varStatus="status">
			<tr>
			 	<td>${status.index + 1}</td>
			 	<td>
			 		${taskConfirm.orderId}
			 	</td>
			 	<td>${taskConfirm.rfid}</td>
			    <td>${taskConfirm.doorName}</td>
				<td style="text-align:right;"><fmt:formatNumber value="${taskConfirm.amount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;">${taskConfirm.totalCount}</td>
				<td >${taskConfirm.allotManName}</td>
				<td><fmt:formatDate value="${taskConfirm.allotDate}" pattern="yyyy-MM-dd"/></td>
				<td >${taskConfirm.clearManName}</td>
				<td>${fns:getDictLabel(taskConfirm.allotStatus, 'task_allot_type', '')}</td>
				<td>
					<c:if test="${taskConfirm.allotStatus eq '4'  }">
						<shiro:hasPermission name="task:taskConfirm:edit">
							<%-- 确认 --%>
							<a href="#" onclick="confirm('${taskConfirm.id}');javascript:return false;" title="<spring:message code='common.confirm' />">
								<i class="fa fa-pencil-square  fa-lg"></i>
							</a>
							&nbsp;&nbsp;&nbsp;
							<%-- 驳回 --%>
							<a id="areject" href="#" onclick="reject('${taskConfirm.id}');javascript:return false;" title="<spring:message code='door.public.reject' />">
								<i class="fa fa-ban text-red  fa-lg"></i>
							</a>

						</shiro:hasPermission>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	
</body>
</html>