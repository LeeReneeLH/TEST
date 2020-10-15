<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.areaList" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>

</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库区位置列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.areaList" /></a></li>
		<%-- 初始化库区 --%>
		<li><a href="${ctx}/store/v01/stoAreaSettingInfo/initForm"><spring:message code="store.initArea" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoAreaSettingInfo" action="${ctx}/store/v01/stoAreaSettingInfo" method="post" class="breadcrumb form-search">
		
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<%-- 库区类型 --%>
		<label class="control-label"><spring:message code="store.areaType" />：</label>
		<form:select path="storeAreaType" id="storeAreaType" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('store_area_type')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<%-- 库区名称 --%>
		<label><spring:message code="store.areaName" />：</label>
		<form:input path="storeAreaName"/>
		&nbsp;
		<c:if test="${fns:getUser().office.type == '7'}">
			<%-- 机构名称 --%>
			<label class="control-label">机构名称：</label>
			<sys:treeselect id="rofficeId" name="officeId"
				value="${stoAreaSettingInfo.officeId}" labelName="officeName"
				labelValue="${stoAreaSettingInfo.officeName}" title="<spring:message code='allocation.application.office' />"
				url="/sys/office/treeData" cssClass="required input-medium" 
				allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"
				type="1" isAll="false"/>
		</c:if>
		&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 库区类型 --%>
				<th><spring:message code="store.areaType" /></th>
				<%-- 库区名称 --%>
				<th class="sort-column STORE_AREA_NAME"><spring:message code="store.areaName" /></th>
				<c:if test="${fns:getUser().office.type == '7' }">
					<%-- 所属机构 --%>
					<th class="sort-column OFFICE_ID">所属机构</th>
				</c:if>
				<%-- 行位置 --%>
				<th class="sort-column X_POSITION"><spring:message code="store.rowPosition" /></th>
				<%-- 列位置--%>
				<th class="sort-column Y_POSITION"><spring:message code="store.colPosition" /></th>
				<%-- 最大容量 --%>
				<th class="sort-column MAX_CAPABILITY"><spring:message code="store.areaMaxCapability" /></th>
				<%-- 最大保存日数 --%>
				<th class="sort-column MAX_SAVE_DAYS"><spring:message code="store.areaMaxSaveDays" /></th>
				<%-- 最小保存日数 --%>
				<th class="sort-column MIN_SAVE_DAYS"><spring:message code="store.areaMinSaveDays" /></th>
				<%-- 库区物品 --%>
				<th style="text-align: center" >库区物品</th>
				<%-- 在库数量 --%>
				<th style="text-align: center" >在库数量</th>
				<%-- 排序 --%>
				<th>排序</th>
				<%-- 创建者 --%>
				<%-- <th><spring:message code="common.creator" /></th> --%>
				<%-- 创建时间 --%>
				<%-- <th><spring:message code="common.createDateTime" /></th> --%>
				<%-- 更新者 --%>
				<th><spring:message code="common.updater" /></th>
				<%-- 更新时间 --%>
				<th><spring:message code="common.updateDateTime" /></th>
				<%-- 状态 --%>
				<th><spring:message code="common.status"/></th>
				<%-- 操作（修改/删除） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="areaInfo" > 
			<tr>
				<td>${fns:getDictLabel(areaInfo.storeAreaType,'store_area_type',"")}</td>
				<td>${areaInfo.storeAreaName}</td>
				<c:if test="${fns:getUser().office.type == '7' }">
					<td style="text-align: left;">
						${fns:getOfficeNameById(areaInfo.officeId)}
					</td>
				</c:if>
				<td style="text-align: right">${areaInfo.xPosition}</td>
				<td style="text-align: right">${areaInfo.yPosition}</td>
				<td style="text-align: right">${areaInfo.maxCapability}</td>
				<td style="text-align: right">${areaInfo.maxSaveDays}</td>
				<td style="text-align: right">${areaInfo.minSaveDays}</td>
				<td style="text-align: right">${sto:getGoodsName(areaInfo.goodsId)}</td>
				<td style="text-align: right">
					<c:if test="${areaInfo.delFlag == '0'}">
						${areaInfo.goodsCnt}
					</c:if>
				</td>
					
				<td style="text-align: right">${areaInfo.sortKey}
				<%-- <td>${areaInfo.createName}</td>--%>
				<%-- <td><fmt:formatDate value="${areaInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>--%>
				<td>${areaInfo.updateName}</td>
				<td><fmt:formatDate value="${areaInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
					<a href="${ctx}/store/v01/stoAreaSettingInfo/changeStatus?id=${areaInfo.id}&delFlag=${areaInfo.delFlag}">
    					${fns:getDictLabel(areaInfo.delFlag,'sto_area_status',"")}
    				</a>
				
				</td>
				<td>
					<c:if test="${areaInfo.delFlag == '0'}">
						<a href="${ctx}/store/v01/stoAreaSettingInfo/form?id=${areaInfo.id}" title="编辑">
							<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
						</a>&nbsp;&nbsp;
					</c:if>
					<shiro:hasPermission name="store.v01:stoAreaSettingInfo:clear">
						<c:if test="${areaInfo.delFlag == '0'}">
							<a href="${ctx}/store/v01/stoAreaSettingInfo/clearRfidInStore?id=${areaInfo.id}"
									onclick="return confirmx('<spring:message code="message.I0008"/>', this.href)" title="清除库区">
									<%-- <spring:message code="store.area.clear" /> --%><i class="fa  fa-trash-o text-red fa-lg"></i>
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
