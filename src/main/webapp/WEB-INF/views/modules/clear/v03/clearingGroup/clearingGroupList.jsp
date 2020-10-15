<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分组管理 -->
	<title><spring:message code="clear.clearingGroup.title"/></title>
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
		<!-- 清分组管理列表 -->
		<li class="active"><a href="${ctx}/clear/v03/clearingGroup/"><spring:message code="clear.clearingGroup.list"/></a></li>
		<!-- 清分组管理添加 -->
		<shiro:hasPermission name="group:clearingGroup:edit"><li><a href="${ctx}/clear/v03/clearingGroup/form"><spring:message code="clear.clearingGroup.register"/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="clearingGroup" action="${ctx}/clear/v03/clearingGroup/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div class="row">
		<div class="" style="margin-top:5px">
		<!-- 分组编号 -->
		<label><spring:message code="clear.clearingGroup.groupNumberr"/>：</label>
		<form:input path="groupNo" htmlEscape="false" maxlength="10" class=""/>
		<!-- 分组名称 -->
		<c:if test="${sto:getClearingGroupName('','')!=null}">
			<label class="control-label"><spring:message code="clear.clearingGroup.groupName"/>：</label>
			<form:select path="groupName" class="input-large">
				<form:option value=""><spring:message code="common.select"/></form:option>
				<form:options items="${sto:getClearingGroupName('','')}" itemLabel="groupName" itemValue="groupName" htmlEscape="false" />
			</form:select>
		</c:if>
		<!-- 业务类型 -->
		<label class="control-label"><spring:message code="clear.task.business.type"/>：</label>
		<form:select path="groupType" id="type" class="input-large">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>				
			<form:options items="${fns:getFilterDictList('clear_businesstype',true,'08,09')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		
		<!-- <div class="span12" style="margin-top:10px;width:1500px;"> -->
		<!-- 状态 -->
		<label class="control-label" style="margin-left:22px"><spring:message code="common.status"/>：</label>
		<form:select path="groupStatus" id="status" class="input-large">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>				
			<form:options items="${fns:getDictList('clearing_group_status')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<!-- 去掉开始时间结束时间 wzj 2017-11-21 begin -->
		<!-- 开始日期 -->
		<%-- <label><spring:message code="common.startDate" />：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
			   value="<fmt:formatDate value="${clearingGroup.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/> --%>
		<!-- 结束日期 -->
		<%-- <label style="margin-left:43px"><spring:message code="common.endDate" />：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate createTime" 
		       value="<fmt:formatDate value="${clearingGroup.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>		 --%>
		<!-- end -->
		<!-- 增加清分机构 wzj 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${clearingGroup.office.id}" labelName="office.name"
				labelValue="${clearingGroup.office.name}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		 &nbsp;&nbsp;&nbsp; 
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		</div>
		</div>
		<!-- </div> -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 清分组编号 -->
				<th><spring:message code='clear.clearingGroup.blockNumber'/></th>
				<!-- 清分组名称 -->
				<th><spring:message code='clear.clearingGroup.blockName'/></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 人数 -->
				<th><spring:message code="clear.clearingGroup.peopleNumber"/></th>
				<!-- 创建者姓名 -->
				<th><spring:message code="clear.clearingGroup.createName"/></th>
				<!-- 更新者姓名 -->
				<th><spring:message code="clear.clearingGroup.updateName"/></th>
				<!-- 更新时间 -->
				<th><spring:message code="common.updateDateTime"/></th>
				<!-- 状态 -->
				<th><spring:message code="common.status"/></th>
				<!-- 增加机构显示 wzj 2017-11-24 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 操作 -->
				<shiro:hasPermission name="group:clearingGroup:edit"><th><spring:message code='common.operation'/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clearingGroup">
				<tr>
					<td><a href="${ctx}/clear/v03/clearingGroup/view?id=${clearingGroup.id}">
						${clearingGroup.groupNo}
					</a></td>
					<td>
						${clearingGroup.groupName}
					</td>
					<td>
						${fns:getDictLabel(clearingGroup.groupType,'clear_businesstype',"")}
					</td>
					<td>
						${clearingGroup.number}
					</td>
					<td>
						${clearingGroup.createName}
					</td>
					<td>
						${clearingGroup.updateName}
					</td>
					<td>
						<fmt:formatDate value="${clearingGroup.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${fns:getDictLabel(clearingGroup.groupStatus,'clearing_group_status',"")}
					</td>
					<!-- 增加机构 wzj 2017-11-24 begin -->
					<td>
					${clearingGroup.office.name} 
					</td>
					<!-- end -->
					<shiro:hasPermission name="group:clearingGroup:edit"><td>
	    				<span style='width:30px;display:inline-block;'>
	    					<!-- 修改 -->
	    					<a href="${ctx}/clear/v03/clearingGroup/form?id=${clearingGroup.id}" title="<spring:message code='common.modify'/>"><i class="fa fa-edit text-green fa-lg"></i></a>
						</span>
						<span style='width:30px;display:inline-block;'>
							<!-- 删除 -->
							<a href="${ctx}/clear/v03/clearingGroup/delete?id=${clearingGroup.id}" onclick="return confirmx('确认要删除该清分组管理吗？', this.href)" title="<spring:message code='common.delete'/>"><i class="fa fa-trash-o text-red fa-lg"></i></a>
						</span>
						<span style='width:30px;display:inline-block;'>
							<c:choose>
								<c:when test="${clearingGroup.groupStatus=='0'}">
									<!-- 停用 -->
									<a href="${ctx}/clear/v03/clearingGroup/update?id=${clearingGroup.id}&groupStatus=1" onclick="return confirmx('确认要停用该清分组管理吗？', this.href)" title="<spring:message code='clear.clearingGroup.disable'/>"><i class="fa fa-ban text-red fa-lg"></i></a>
								</c:when>
								<c:when test="${clearingGroup.groupStatus=='1'}">
									<!-- 启用 -->
									<a href="${ctx}/clear/v03/clearingGroup/update?id=${clearingGroup.id}&groupStatus=0" onclick="return confirmx('确认要启用该清分组管理吗？', this.href)" title="<spring:message code='clear.clearingGroup.enablement'/>"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
								</c:when>
							</c:choose>
						</span>
					</td></shiro:hasPermission>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>