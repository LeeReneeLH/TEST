<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="allocation.imp.blank.doc.order" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v01/impBlankDocOrder/list");				
			});
		});

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 重空预约列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.imp.blank.doc.order.list"/></a></li>
		<%-- 网点重空预约登记(link) 只在网点登录时显示--%>
		<shiro:hasPermission name="allocation:point:edit">
			<li><a href="${ctx}/allocation/v01/impBlankDocOrder/form?pageType=pointAdd"><spring:message code="allocation.imp.blank.doc.order.register"/></a></li>
		</shiro:hasPermission>
		
	</ul>
	
	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="" method="post" class="breadcrumb form-search">
		
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		
		<%-- 网点名称 --%>
		<%-- 库房端显示 --%>
		<shiro:hasPermission name="allocation:store:edit">
			<%-- 网点名称 --%>
			<label class="control-label"><spring:message code="common.outletsName"/>：</label>
			<sys:treeselect id="rOfficeId" name="rOffice.id" 		
	 			value="${allAllocateInfo.rOffice.id}" labelName="rOffice.name"  labelValue="${allAllocateInfo.rOffice.name}"
	 			title="机构" 
	 			url="/sys/office/treeData" 
	 			cssClass="required input-small" allowClear="true" notAllowSelectParent="true" notAllowSelectRoot="true"/>
		</shiro:hasPermission>
		
		<%-- 重空状态 --%>
		<label><spring:message code='allocation.imp.blank.doc.status'/>：</label>
		<form:select path="status" id="status" class="input-medium" >
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('imp_blk_doc_status')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>

	<sys:message content="${message}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 网点名称(网点查询时不显示) --%>
				<%-- 流水号 --%>
				<th><spring:message code='allocation.allId'/></th>
				<shiro:hasPermission name="allocation:store:edit">
					<th class="sort-column t_aai.roffice_name"><spring:message code='common.outletsName'/></th>
				</shiro:hasPermission>
				<%-- 预约人 --%>
				<th><spring:message code='allocation.order.name'/></th>
				<%-- 预约日期 --%>
				<th  class="sort-column t_aai.create_date"><spring:message code='allocation.order.date'/></th>
				<%-- 装配人 --%>
				<th><spring:message code='allocation.imp.blank.doc.quota.name'/></th>
				<%-- 装配日期 --%>
				<th class="sort-column t_aai.accept_date"><spring:message code='allocation.imp.blank.doc.quota.date'/></th>
				<%-- 重空状态 --%>
				<th class="sort-column t_aai.status"><spring:message code='allocation.imp.blank.doc.status'/></th>
				<%-- 操作（查看/修改/配款） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="allocation" >
				<tr>
					<%-- 流水号 --%>
					<td>${allocation.allId}</td>
					<shiro:hasPermission name="allocation:store:edit">
						<%-- 网点名称(网点查询时不显示) --%>
						<td>${allocation.rOffice.name}</td>
					</shiro:hasPermission>
					<%-- 预约人 --%>
					<td>${allocation.createName}</td>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.createDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 装配人 --%>
					<td>${allocation.acceptName}</td>
					<%-- 装配日期 --%>
					<td><fmt:formatDate value="${allocation.acceptDate}" pattern="yyyy-MM-dd"/></td>
					<%-- 重空状态 --%>
					<td>${fns:getDictLabel(allocation.status,'imp_blk_doc_status','')}</td>
					<%-- 操作（查看/修改/配款） --%>
					<td>	
						<fmt:formatDate value="${allocation.createDate}" type="both" dateStyle="long" pattern="yyyy-MM-dd" var="createDate"/>
						<fmt:formatDate value="${currentDate }" type="both" dateStyle="long" pattern="yyyy-MM-dd" var="varCurrentDate"/>
						<shiro:hasPermission name="allocation:storemaster:show">
							<c:choose>
								<%-- 当天 --%>
								<c:when test="${createDate eq varCurrentDate and allocation.allDetailList.size() == 0}">
									
									<%-- 同行 --%>
									<c:if test="${allocation.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeEdit">
											<spring:message code="common.doc.quota" />
										</a>
									</c:if>
									<c:if test="${allocation.status ne fns:getConfig('allocation.status.imp.blank.doc.quotaNo') or allocation.status eq ''}">
										<%--
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeEdit">
											<spring:message code="common.modify" />
										</a>
										--%>
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeView" title="查看">
												<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
										</a>
									</c:if>
								</c:when>
								<%-- 非当天 --%>
								<c:otherwise>
									<%-- 同行 --%>
									<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeView" title="查看">
									<%-- 	<spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
									</a>
								</c:otherwise>
							</c:choose>
						</shiro:hasPermission> 
						<shiro:lacksPermission name="allocation:storemaster:show">
							<%-- 库房权限：1、当天：显示装配 --%>
							<%-- 库房权限：2、非当天：只显示查看 --%>
							<shiro:hasPermission name="allocation:store:edit">
								<c:choose>
									<%-- 当天 --%>
									<c:when test="${createDate eq varCurrentDate and allocation.allDetailList.size() == 0}">
										<%-- 同行 --%>
										<c:if test="${allocation.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
											<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeEdit">
												<spring:message code="common.doc.quota" />
											</a>
										</c:if>
										<c:if test="${allocation.status ne fns:getConfig('allocation.status.imp.blank.doc.quotaNo')}">
											<%--
											<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeEdit">
												<spring:message code="common.modify" />
											</a>
											--%>
											<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeView" title="查看">
												<%-- 	<spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
											</a>
										</c:if>
									</c:when>
									<%-- 非当天 --%>
									<c:otherwise>
										<%-- 同行 --%>
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=storeView" title="查看">
											<%-- 	<spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
										</a>
									</c:otherwise>
								</c:choose>
							</shiro:hasPermission>
							<%-- 网点权限：未装配：显示修改  已装配：只能查看 --%>
							<shiro:hasPermission name="allocation:point:edit">
								<c:choose>
									<%-- 未装配 --%>
									<c:when test="${allocation.status eq fns:getConfig('allocation.status.imp.blank.doc.quotaNo') and createDate eq varCurrentDate}">
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=pointEdit" title="编辑">
											<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
										</a>
										<a href="${ctx}/allocation/v01/impBlankDocOrder/delete?allId=${allocation.allId}"
											onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
											<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
										</a>
									</c:when>
									<%-- 已装配 --%>
									<c:otherwise>
										<a href="${ctx}/allocation/v01/impBlankDocOrder/form?allId=${allocation.allId}&pageType=pointView" title="查看">
											<%-- 	<spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
										</a>
									</c:otherwise>
								</c:choose>
							</shiro:hasPermission>
						</shiro:lacksPermission>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
