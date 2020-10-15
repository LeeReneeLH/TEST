<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.areaList" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<style>
.breadcrumb{height:100% !important;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 实时库区 --%>
		<c:forEach items="${fns:getDictList('store_area_type')}" var="typeItem" >
			<li><a href="${ctx}/store/v01/stoAreaSettingInfo/showAreaGoodsGraph?storeAreaType=${typeItem.value}&displayHref=${stoGoodsLocationInfo.displayHref }">${typeItem.label }</a></li>
		</c:forEach>
		<%-- 库区物品列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.areaGoodsList" /></a></li>
		
	</ul>
	
	<div class="row">
	
		<form:form id="searchForm" modelAttribute="stoGoodsLocationInfo" 
		action="${ctx}/store/v01/StoGoodsLocationInfo/findAreaGoodInfoList" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<label class="control-label"><spring:message code="store.areaType" />：</label>
					<form:select path="storeAreaType" id="storeAreaType" class="input-medium">
						<form:option value=""><spring:message code="common.select" /></form:option>
						<form:options items="${fns:getDictList('store_area_type')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<%-- 库区名称 --%>
					<label><spring:message code='store.areaName'/>：</label>
					<form:input path="storeAreaName" htmlEscape="false" maxlength="5" class="input-small"/>
					<%-- 物品名称 --%>
					<label><spring:message code='store.goodsName'/>：</label>
					<form:input path="goodsName" htmlEscape="false" maxlength="30" class="input-small"/>
					<%-- 箱袋编号 --%>
					<label><spring:message code='common.boxNo'/>：</label>
					<form:input path="rfid" htmlEscape="false" maxlength="30" class="input-small"/>
					
				</div>
				<form:hidden path="displayHref"/>
				<div class="span14" style="margin-top:10px">
					<%-- 状态 --%>
					<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code='common.status'/>：</label>
					<form:select path="delFlag" id="delFlag" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('store_location_goods_stutus')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<%-- 开始日期 --%>
					<label><spring:message code="store.original.inDate"/>：</label>
					<input id="inStoreDateStart"  name="inStoreDateStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${stoGoodsLocationInfo.inStoreDateStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'inStoreDateEnd\')||\'%y-%M-%d\'}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					<input id="inStoreDateEnd" name="inStoreDateEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${stoGoodsLocationInfo.inStoreDateEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'inStoreDateStart\')}',maxDate:'%y-%M-%d'});"/>
					&nbsp;
					<c:if test="${fns:getUser().office.type == '7'}">
						<%-- 机构名称 --%>
						<label class="control-label">机构名称：</label>
						<sys:treeselect id="rofficeId" name="officeId"
							value="${stoAreaSettingInfo.officeId}" labelName="officeName"
							labelValue="${stoAreaSettingInfo.officeName}" title="<spring:message code='allocation.application.office' />"
							url="/sys/office/treeData" cssClass="required input-small"
							allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"
							type="1" isAll="false"/>
						</c:if>
					&nbsp;
					<%-- 查询 --%><label>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</label>
				</div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<c:if test="${stoGoodsLocationInfo.displayHref == 'hide' }">
		<div style="width: 100% !important;overflow-x: scroll !important;">
			<div style="width: 1300px;">
	</c:if>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 库区类型 --%>
				<th><spring:message code="store.areaType" /></th>
				<%-- 库区名称 --%>
				<th class="sort-column areaInfo.STORE_AREA_NAME"><spring:message code='store.areaName'/></th>
				<c:if test="${fns:getUser().office.type == '7' }">
					<%-- 所属机构 --%>
					<th class="sort-column areaInfo.OFFICE_ID">所属机构</th>
				</c:if>
				<%-- 箱袋编号 --%>
				<th><spring:message code='common.boxNo'/></th>
				<%-- 物品名称--%>
				<th class="sort-column stoGoos.GOODS_NAME"><spring:message code='store.goodsName'/></th>
				<%-- 物品数量 --%>
				<th><spring:message code='store.goodsNum'/></th>
				<%-- 总价值(单位：元) --%>
				<th><spring:message code='store.totalValue'/><spring:message code='common.units.yuan'/></th>
				<%-- 入库流水单号 --%>
				<th class="sort-column goodsInfo.IN_STORE_ALL_ID"><spring:message code='store.inStoreAllId'/></th>
				<%-- 入库时间 --%>
				<th class="sort-column goodsInfo.IN_STORE_DATE"><spring:message code='store.inStoreDateTime'/></th>
				<%-- 出库流水单号 --%>
				<th class="sort-column goodsInfo.OUT_STORE_ALL_ID"><spring:message code='store.outStoreAllId'/></th>
				<%-- 出库时间 --%>
				<th class="sort-column goodsInfo.OUT_STORE_DATE"><spring:message code='store.outStoreDateTime'/></th>
				<%-- 状态 --%>
				<th  class="sort-column goodsInfo.DEL_FLAG"><spring:message code="common.status" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="goodsInfo" > 
				<tr>
					<td>${fns:getDictLabel(goodsInfo.storeAreaType,'store_area_type',"")}</td>
					<td>${goodsInfo.storeAreaName}</td>
					<c:if test="${fns:getUser().office.type == '7' }">
						<td style="text-align: left;">${fns:getOfficeNameById(goodsInfo.officeId)}</td>
					</c:if>
					<c:choose>
						<c:when test="${goodsInfo.storeAreaType =='02' }">
							<td>${goodsInfo.rfid}</td>
						</c:when>
						<c:otherwise>
							<td>${fns:left(goodsInfo.rfid,8)}</td>
						</c:otherwise>
					</c:choose>
					<td>${sto:getGoodsName(goodsInfo.goodsId)}</td>
					<td style="text-align: right">${goodsInfo.goodsNum}</td>
					<td style="text-align: right"><fmt:formatNumber value="${goodsInfo.amount}" pattern="#,##0.00#" /></td>
					<td>
						<c:choose>
							<c:when test="${stoGoodsLocationInfo.displayHref == 'show' }">
								<a href="${ctx}/store/v01/StoGoodsLocationInfo/toListPage?allId=${goodsInfo.inStoreAllId}">${goodsInfo.inStoreAllId}</a>
							</c:when>
							<c:otherwise>
								${goodsInfo.inStoreAllId}
							</c:otherwise>
						</c:choose>
					</td>
					<td><fmt:formatDate value="${goodsInfo.inStoreDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						<c:choose>
							<c:when test="${stoGoodsLocationInfo.displayHref == 'show' }">
								<a href="${ctx}/store/v01/StoGoodsLocationInfo/toListPage?allId=${goodsInfo.outStoreAllId}">${goodsInfo.outStoreAllId}</a>
							</c:when>
							<c:otherwise>
								${goodsInfo.outStoreAllId}
							</c:otherwise>
						</c:choose>
					</td>
					<td><fmt:formatDate value="${goodsInfo.outStoreDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${fns:getDictLabel(goodsInfo.delFlag,'store_location_goods_stutus',"")}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<c:if test="${stoGoodsLocationInfo.displayHref == 'hide' }">
			</div>
		</div>
	</c:if>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button"
			value="<spring:message code='common.return'/>"
			onclick="window.location.href='${ctx}/store/v01/stoAreaSettingInfo/back?storeAreaType=01&displayHref=${stoGoodsLocationInfo.displayHref }'"/>
	</div>
	
</body>
</html>
