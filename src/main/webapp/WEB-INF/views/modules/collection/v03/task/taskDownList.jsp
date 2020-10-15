<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<!-- 任务分配 -->
	<title><spring:message code="door.taskDown.title" /></title>
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
	

		function updateItem(id) {	

			var content = "get:${ctx}/collection/v03/taskDown/toSelectClearMan?orderId=" + id;
			//任务分配
			top.$.jBox.open(
					content,
					"<spring:message code='door.taskDown.taskAllot' />", 550, 200, {
						buttons : {
							//确认
							"<spring:message code='common.confirm' />" : "ok",
							//关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var clearManNo = "";
								// 清分人员
								clearManNo = h.find("#clearManNo").val();
								if(clearManNo==""){
									//请选择清分人员
									alert("<spring:message code='message.I7241' />");
									return false;
								}
								// ID
								var orderId  = h.find("#orderId").val(); 
								var allotStatus  = h.find("#allotStatus").val(); 
								var url = "${ctx}/collection/v03/taskDown/taskAllot";
								url = url + "?id=" + orderId;
								url = url + "&clearManNo=" + clearManNo;
								//$("#moneyNumber").removeClass();
								//$("#rofficeId").removeClass();
								$("#searchForm").attr("action", url);
								$("#searchForm").submit();
								
								return true;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
			});
			
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 任务分配列表 -->
		<li class="active"><a href="${ctx}/collection/v03/taskDown/"><spring:message code="door.taskDown.title" /><spring:message code="common.list" /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="taskDown" action="" method="post" class="form-horizontal">
	</form:form>
	
	
	<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
	<div class="row">
		<form:form id="searchForm" modelAttribute="taskDown" action="${ctx}/collection/v03/taskDown/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<%-- 门店 --%>
					<label><spring:message code="door.public.cust" /> ：</label>
					<sys:treeselect id="searchDoorName" name="searchDoorId"
							value="${taskDown.searchDoorId}" labelName="searchDoorName"
							labelValue="${taskDown.searchDoorName}" title="<spring:message code='door.public.cust' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
					&nbsp;
					<%-- 清分人员 --%>
					<label><spring:message code="door.taskDown.clearMan" /> ：</label>
					<form:select path="searchClearManNo" id="searchClearManNo" class="input-large required" style="font-size:15px;color:#000000">
						<form:option value=""><spring:message code="common.select" /></form:option>
						<form:options items="${sto:getUsersByTypeAndOffice(ConClear,fns:getUser().getOffice().getId())}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
					</form:select>
					&nbsp;
					<%-- 状态 --%>
					<label><spring:message code="common.status" />：</label>
					<form:select path="searchAllotStatus" class="input-medium required" id ="selectStatus">
						<option value=""><spring:message code="common.select" /></option>
						<form:options items="${fns:getDictList('task_allot_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					&nbsp;
					<%-- 分配日期 --%>
					<label><spring:message code="door.taskDown.allotDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
						value="<fmt:formatDate value="${taskDown.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
					<label>~</label>
					&nbsp;
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
						value="<fmt:formatDate value="${taskDown.createTimeEnd}" pattern="yyyy-MM-dd"/>"
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
				<th class="sort-column a.order_id"><spring:message code="door.taskDown.codeNo" /></th>
				<%-- 包号 --%>
				<th class="sort-column a.rfid"><spring:message code="door.doorOrder.packNum" /></th>
				<%-- 门店名称 --%>
				<th class="sort-column a.door_name"><spring:message code="door.public.custName" /></th>
				<%-- 总金额 --%>
				<th><spring:message code="common.totalMoney" /></th>
				<%-- 笔数 --%>
				<th><spring:message code="door.taskDown.count" /></th>
				<%-- 清分人员 --%>
				<th class="sort-column f.escort_name"><spring:message code="door.taskDown.clearMan" /></th>
				<%-- 分配日期 --%>
				<th class="sort-column a.allot_date"><spring:message code="door.taskDown.allotDate" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.allot_status"><spring:message code="common.status" /></th>	
				<%-- 操作 --%>
				<%-- <th><spring:message code='common.operation'/></th> --%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="taskDown" varStatus="status">
			<tr>
			 	<td>${status.index + 1}</td>
			 	<td>
			 		${taskDown.orderId}
			 	</td>
			 	<td>${taskDown.rfid}</td>
			    <td>${taskDown.doorName}</td>
				<td style="text-align:right;"><fmt:formatNumber value="${taskDown.amount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;">${taskDown.totalCount}</td>
				<td >${taskDown.clearManName}</td>
				<td><fmt:formatDate value="${taskDown.allotDate}" pattern="yyyy-MM-dd"/></td>
				<td>${fns:getDictLabel(taskDown.allotStatus, 'task_allot_type', '')}</td>
				<%-- <td>
					 <c:if test="${taskDown.allotStatus eq '0' || taskDown.allotStatus eq '3' }">
						<shiro:hasPermission name="task:taskDown:allot">
							分配
							<a href="#" onclick="updateItem('${taskDown.id}');javascript:return false;" title="<spring:message code='door.taskDown.allot' />">
								<i class="fa fa-user   fa-lg"></i>
							</a>
						</shiro:hasPermission>
				    </c:if>
				</td> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	
</body>
</html>