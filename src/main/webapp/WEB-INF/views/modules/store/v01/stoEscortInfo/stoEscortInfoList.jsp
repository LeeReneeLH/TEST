<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
</head>
<body>
	<!-- Tab页 -->
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/store/v01/stoEscortInfo/">
				<spring:message code="store.user.list"/>
			</a>
		</li>
		<shiro:hasPermission name="store:stoEscortInfo:edit">
		<li>
			<a href="${ctx}/store/v01/stoEscortInfo/form">
				<spring:message code="store.userInfo"/><spring:message code="common.add" />
			</a>
		</li>
		<%-- <li>
			<a href="${ctx}/store/v01/stoEscortInfo/createUser">
				<spring:message code="store.addUser"/>
			</a>
		</li> --%>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoEscortInfo" 
	action="${ctx}/store/v01/stoEscortInfo/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 查询条件 -->
		<!-- 姓名 -->
		<label><spring:message code="common.name"/>：</label>
		<form:input path="escortName" htmlEscape="false" maxlength="10" class="input-small"/>
		<!-- 身份证号 -->
		<label><spring:message code="common.idcardNo"/>：</label>
		<form:input path="idcardNo" htmlEscape="false" maxlength="18" onkeyup="value=value.replace(/[^0-9a-zA-Z]/g,'')" class="input-medium"/>
		<%-- <c:set var="values" value="${fns:getFilterDictList('sys_user_type',false,'01')}"/> --%>
		<!-- 人员类型 -->
		<label class="control-label"><spring:message code="store.userType"/>：</label>
		<c:set var="showType" value="${fns:getConfig('escort.type.show')} " />
		<form:select path="escortType" class="input-medium" style="width:200px;">
			<!-- 请选择 -->
			<form:option value="">
				<spring:message code="common.select"/>
			</form:option>
			<!-- 解款员 -->
			<form:option value="${fns:getConfig('escort.type.escort')}">
				<spring:message code="store.escortUser"/>
			</form:option>
			<form:options items="${fns:getFilterDictList('sys_user_type', false, showType)}" 
				itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
	    <div class="btn-group-vertical" id="officeGroup">
			&nbsp;<label><spring:message code="common.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${stoEscortInfo.office.id}" labelName="office.name"
				labelValue="${stoEscortInfo.office.name}" title="机构"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true"
				cssClass="input-medium" />
		</div>	
		&nbsp;
		<!-- 按钮 -->
		<input id="btnSubmit" class="btn btn-primary" 
			type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<!-- 消息 -->
	<sys:message content="${message}"/>
	<!-- 列表 -->
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 姓名 -->
				<th class="sort-column a.escort_name"><spring:message code="common.name"/></th>
				<!-- 身份证 -->
				<th class="sort-column a.idcard_no"><spring:message code="common.idcardNo"/></th>
				<!-- 机构 -->
				<th class="sort-column o15.name"><spring:message code="common.office"/></th>
				<!-- 电话 -->
				<th><spring:message code="common.phone"/></th>
				<!-- RFID -->
				<th>RFID</th>
				<!-- RFID绑定 -->
				<th>RFID绑定</th>
				<!-- 脸部识别ID -->
				<th>脸部识别ID</th>
				<!-- 人员类型 -->
				<th class="sort-column a.escort_type"><spring:message code="store.userType"/></th>
				<%-- <!-- 指纹采集 -->
				<th><spring:message code="store.fingerCollect"/></th>
				<!-- PDA指纹采集 -->
				<th><spring:message code="store.fingerCollect.pda"/></th> --%>
				<th><spring:message code="store.faceCollect"/></th>
				<!-- 操作 -->
				<shiro:hasPermission name="store:stoEscortInfo:edit">
					<th><spring:message code="common.operation"/></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoEscortInfo" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${stoEscortInfo.escortName}</td>
				<td><a href="${ctx}/store/v01/stoEscortInfo/view?id=${stoEscortInfo.id}">${stoEscortInfo.idcardNo}</a></td>
				<td>${stoEscortInfo.office.name}</td>
				<td>${stoEscortInfo.phone}</td>
				<td>${stoEscortInfo.rfid}</td>
				<c:if test="${stoEscortInfo.bindingRfid == '1'}">
					<td>已绑定</td>
				</c:if>
				<c:if test="${stoEscortInfo.bindingRfid==null || stoEscortInfo.bindingRfid == '0'}">
					<td>未绑定</td>
				</c:if>
				<td>${stoEscortInfo.userFaceId}</td>
				<td>${fns:getDictLabel(stoEscortInfo.escortType,'sys_user_type',fns:getConfig('escort.type.escort.name'))}</td>
				<%-- <c:if test="${not empty stoEscortInfo.fingerNo1}">
					<td><spring:message code="store.fingerCollectFlag.yes"/></td>
				</c:if>
				<c:if test="${stoEscortInfo.fingerNo1==null}">
					<td><spring:message code="store.fingerCollectFlag.no"/></td>
				</c:if>
				<c:if test="${not empty stoEscortInfo.pdaFingerNo1}">
					<td><spring:message code="store.fingerCollectFlag.yes"/></td>
				</c:if>
				<c:if test="${stoEscortInfo.pdaFingerNo1==null}">
					<td><spring:message code="store.fingerCollectFlag.no"/></td>
				</c:if> --%>
				<c:if test="${stoEscortInfo.bindingFace == '1'}">
					<td><spring:message code="store.faceCollect.yes"/></td>
				</c:if>
				<c:if test="${stoEscortInfo.bindingFace==null || stoEscortInfo.bindingFace == '0'}">
					<td><spring:message code="store.faceCollect.no"/></td>
				</c:if>
				<shiro:hasPermission name="store:stoEscortInfo:edit">
				<td>
				<!--修改  -->
   				<a href="${ctx}/store/v01/stoEscortInfo/form?id=${stoEscortInfo.id}" title="修改">
   					<%-- <spring:message code="common.modify"/> --%><i class="fa fa-edit text-green fa-lg"></i>
   				</a>
   				<!-- 绑定线路的押运人员不能删除 -->
   				<c:if test="${stoEscortInfo.escortType eq '90'}">
    				<c:if test="${stoEscortInfo.bindingRoute!='1'}">
						<a href="${ctx}/store/v01/stoEscortInfo/delete?id=${stoEscortInfo.id}" 
						onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除" style="margin-left:10px;">
						<%-- <spring:message code="common.delete"/> --%><i class="fa fa-trash-o text-red fa-lg"></i>
						</a>
					</c:if>
				</c:if>
				<!-- 其他人员只能查看 -->
					<%-- <a href="${ctx}/store/v01/stoEscortInfo/view?id=${stoEscortInfo.id}">
    					<spring:message code="common.view"/>
    				</a> --%>
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
