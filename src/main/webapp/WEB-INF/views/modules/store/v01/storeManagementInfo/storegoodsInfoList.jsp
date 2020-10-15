<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.areaList" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	//批量配款打印
	function showBoxDetail(id){
		var content = "iframe:${ctx}/store/v01/storeGoodsInfo/showDetail?id=" + id;
		top.$.jBox.open(
				content,
				//打印
				"<spring:message code='common.detail' />", 1024, 600, {
					buttons : {
						// 关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
						
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "auto");
					}
				});
		
	}
</script>
<style>
.breadcrumb{height:100% !important;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库房管理列表 --%>
		<li><a href="${ctx}/store/v01/storeManagementInfo/graph">库房管理</a></li>
		<%-- 库房箱袋列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">库房箱袋列表</a></li>
		<shiro:hasPermission name="store:storeManagementInfo:edit">
			<%-- 库房管理列表 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/">库房管理列表</a></li>
			<%-- 库房创建 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/toCreateStorePage">库房创建</a></li>
		</shiro:hasPermission>
	</ul>
	
	<div class="row">
	
		<form:form id="searchForm" modelAttribute="storeGoodsInfo" 
		action="${ctx}/store/v01/storeGoodsInfo/list" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span12" style="margin-top:5px">
					<label class="control-label">归属机构：</label>
					<sys:treeselect id="officeId" name="office.id"
					value="${storeGoodsInfo.office.id}" labelName="office.name"
					labelValue="${storeGoodsInfo.office.name}" title="选择机构"
					url="/sys/office/treeData" cssClass="required input-small" 
					notAllowSelectParent="true" notAllowSelectRoot="false"
					maxType="4" isAll="false" allowClear="true" type='3'/>
					&nbsp;
					<label>库房名称：</label>
					<form:select path="storeId" id="delFlag" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${storeList}"
							itemLabel="storeName" itemValue="id" htmlEscape="false" />
					</form:select>	
					<label>状态：</label>
					<form:select path="delFlag" id="delFlag" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getFilterDictList('sto_area_status', true, '0,1')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>	
					&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
				
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<%-- 库房名称 --%>
				<th>库房名称</th>
				<%-- 箱袋编号 --%>
				<th class="sort-column box_no"><spring:message code="common.boxNo" /></th>
				<%-- RFID --%>
				<th class="sort-column rfid"><spring:message code="store.rfid" /></th>
				<%-- 归属机构 --%>
				<th>归属机构</th>
				<%-- 箱袋类型 --%>
				<th class="sort-column box_type"><spring:message code="store.boxType" /></th>
				<%-- 入库时间--%>
				<th class="sort-column out_date">入库时间</th>
				<%-- 尾箱出库时间--%>
				<th class="sort-column out_date"><spring:message code="common.outTime" /></th>
				<%-- 创建时间 --%>
				<th class="sort-column CREATE_DATE">创建时间</th>
				<%-- 创建人 --%>
				<th>创建人</th>
				<%-- 更新时间 --%>
				<th class="sort-column UPDATE_DATE">更新时间</th>
				<%-- 更新人 --%>
				<th>更新人</th>
				<%-- 状态 --%>
				<th  class="sort-column DEL_FLAG">状态</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="goodsInfo" > 
				<tr>
					<td>${goodsInfo.storeName}</td>
					<c:choose>
						<c:when test="${goodsInfo.boxType == '12' }">
							<td>${goodsInfo.boxNo}</td>
						</c:when>
						<c:otherwise>
							<td><a href = "#" onclick="showBoxDetail('${goodsInfo.id}');javascript:return false;">${goodsInfo.boxNo}</a></td>
							
						</c:otherwise>
					</c:choose>
					
					<td>${goodsInfo.rfid}</td>
					<td>${goodsInfo.office.name}</td>
					<td>${fns:getDictLabel(goodsInfo.boxType,'sto_box_type',"")}</td>
					<td><fmt:formatDate value="${goodsInfo.inStoreDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${goodsInfo.outDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${goodsInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${goodsInfo.createName}</td>
					<td><fmt:formatDate value="${goodsInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${goodsInfo.updateName}</td>
					<td>${fns:getDictLabel(goodsInfo.delFlag,'sto_area_status',"")}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
</body>
</html>
