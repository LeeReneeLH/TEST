<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.rfid.title" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- RFID箱袋列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.rfid.list" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="stoRfidDenomination" action="${ctx}/store/v01/stoRFIDInfo" method="post" class="breadcrumb form-search">
		
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row">
			<div class="span11" style="margin-top:5px">
				<!-- 查询条件 -->
				<!-- 箱袋编号 -->
				<label><spring:message code="store.rfid.box"/>：</label>
				<form:input path="rfid" htmlEscape="false" class="input-small"/>
				<!-- 机构 -->
				<div class="btn-group-vertical" id="officeGroup">
					&nbsp;<label><spring:message code="common.office" />：</label>
					<sys:treeselect id="office" name="office.id"
						value="${stoRfidDenomination.office.id}" labelName="office.name"
						labelValue="${stoRfidDenomination.office.name}" title="机构"
						url="/sys/office/treeData" notAllowSelectRoot="true" maxType="3"
						notAllowSelectParent="false" allowClear="true" 
						cssClass="input-medium" isAll="true" />
				</div>
				<!-- 业务类型 -->	
				<label class="control-label"><spring:message code="store.rfid.bussinessType"/>：</label>
				
				<form:select path="businessType" class="input-medium">
					<!-- 请选择 -->
					<form:option value="">
						<spring:message code="common.select"/>
					</form:option>
					<form:options items="${fns:getDictList('all_businessType')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
			    </form:select>
			</div>
			<div class="span7" style="margin-top:5px">
				<%-- 创建日期 --%>
				<label><spring:message code="common.createDate" />：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					   value="<fmt:formatDate value="${stoRfidDenomination.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
				<label>~</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				       value="<fmt:formatDate value="${stoRfidDenomination.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
				&nbsp;
				<%-- 查询 --%>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
		</div>
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 序号--%>
				<th><spring:message code="common.seqNo"/></th>
				<%-- 箱袋编号 --%>
				<th class="sort-column rfid"><spring:message code="store.rfid.box" /></th>
				<%-- 流水单号 --%>
				<th class="sort-column allId">当前绑定流水单号</th>
				<%-- 所属机构 --%>
				<th class="sort-column officeName">初始绑定机构</th>
				<%-- 物品名称 --%>
				<th class="sort-column goodsId">当前绑定物品名称</th>
				<%-- 业务类型--%>
				<th class="sort-column businessType">业务类型</th>
				<%-- 当前所在机构--%>
				<th>当前所在机构</th>
				<%-- 状态 --%>
				<th>状态</th>
				<%-- 使用标识 --%>
				<th>使用标识</th>
				<%-- 有效标识 --%>
				<th>有效标识</th>
				<%-- 替换卡号 --%>
				<th>替换卡号</th>
				<%-- 创建者 --%>
				<th ><spring:message code="common.creator" /></th>
				<%-- 创建时间 --%>
				<th class="sort-column createDate"><spring:message code="common.createDateTime" /></th>
				<%-- 更新者 --%>
				<th><spring:message code="common.updater" /></th>
				<%-- 更新时间 --%>
				<th class="sort-column updateDate"><spring:message code="common.updateDateTime" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="boxInfo" varStatus="status"> 
			<tr>
				<td>${status.index+1}</td>
				<td><a href="${ctx}/store/v01/stoRFIDInfo/getStoRFIDLifeCycleGraph?rfid=${boxInfo.rfid}">${fns:left(boxInfo.rfid,8)}</a></td>
				<td>${boxInfo.allId}</td>
				<td>${boxInfo.officeName}</td>
				<td>${boxInfo.goodsName}</td>
				<td>${fns:getDictLabel(boxInfo.businessType,'all_businessType','')}</td>
				<td>${boxInfo.atOfficeName}</td>
				<td>${fns:getDictLabel(boxInfo.boxStatus,'sto_box_status','')}</td>
				<%-- 使用标识 --%>
				<td>${fns:getDictLabel(boxInfo.useFlag,'RFID_USE_FLAG','')}</td>
				<%-- 有效标识 --%>
				<td>${fns:getDictLabel(boxInfo.delFlag,'RFID_DEL_FLAG','')}</td>
				<%-- 替换卡号 --%>
				<td>${fns:left(boxInfo.destRfid, 8)}</td>
				<td>${boxInfo.createName}</td>
				<td><fmt:formatDate value="${boxInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${boxInfo.updateName}</td>
				<td><fmt:formatDate value="${boxInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>