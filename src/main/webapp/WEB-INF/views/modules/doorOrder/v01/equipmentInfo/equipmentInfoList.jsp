<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title><spring:message code="door.equipment.title" /></title>
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
    <!-- 机具列表 -->
    <li class="active"><a href="${ctx}/doorOrder/v01/equipmentInfo/"><spring:message code="door.equipment.list" /></a></li>
</ul>
<form:form id="searchForm" modelAttribute="equipmentInfo" action="${ctx}/doorOrder/v01/equipmentInfo/" method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
    <div class="row search-flex row-left">
        <!-- <div class="span14" style="margin-top:5px;"> -->
			<%-- 机具编号 --%>
			<div>
				<label><spring:message code="door.equipment.no" />：</label>
				<form:input path="seriesNumber" htmlEscape="false" maxlength="15"
					class="input-small" />
			</div>
			<%-- 绑定状态 --%>
			<div>
				<label><spring:message code="door.equipment.status" />：</label>
				<form:select path="status" class="input-medium required"
					id="selectStatus">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('door_equip_status')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<%-- 门店 --%>
			<div>
				<shiro:hasPermission name="doorOrder:equipmentInfo:cust">
					<label><spring:message code="door.public.cust" />：</label>
					<sys:treeselect id="aOffice" name="aOffice.id"
						value="${equipmentInfo.aOffice.id}" labelName="aOffice.name"
						labelValue="${equipmentInfo.aOffice.name}"
						title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData" cssClass="required input-small"
						notAllowSelectParent="true" notAllowSelectRoot="false" minType="8"
						maxType="9" isAll="true" allowClear="true"
						checkMerchantOffice="true" clearCenterFilter="true" />
				</shiro:hasPermission>
			</div>
			<%-- 机具序列号 --%>
			<div>
				<label><spring:message code="door.equipment.seriesNumber" />：</label>
				<form:input path="id" htmlEscape="false" maxlength="15"
					class="input-small" />
			</div>
			<%--&lt;%&ndash; 机具状态 &ndash;%&gt;
          <label><spring:message code="door.equipment.use" />：</label>
          <form:select path="isUse" class="input-large required" id ="useStatus">
              <option value=""><spring:message code="common.select" /></option>
              <form:options items="${fns:getDictList('door_equip_useStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
          </form:select>--%>
			<%-- 机具状态 --%>
			<div>
				<label><spring:message code="door.equipment.connStatus" />：</label>
				<form:select path="connStatus" class="input-medium required"
					id="connStatus">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('CONN_STATUS')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<!-- <div class="time_self" style="float: right;"> -->
			<%-- 开始日期 --%>
			<div>
				<label><spring:message code="door.equipment.addDate" />：</label> <input
					id="createTimeStart" name="createTimeStart" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${equipmentInfo.createTimeStart}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<%-- 结束日期 --%>
			<div>
				<label>~</label> <input id="createTimeEnd" name="createTimeEnd"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${equipmentInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<%-- 查询 --%>
			&nbsp;&nbsp;
			<div>
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.search'/>" />
			</div>
			<!-- </div> -->
    </div>
</form:form>
<sys:message content="${message}"/>
<div class="table-con">
<table id="contentTable" class="table table-hover">
    <thead>
    <tr>
        <%-- 序号 --%>
        <th><spring:message code="common.seqNo" /></th>
        <%-- 机具编号 --%>
        <th class="sort-column a.series_number"><spring:message code="door.equipment.no" /></th>
        <%-- 绑定状态 --%>
        <th  class="sort-column a.status"><spring:message code="door.equipment.status" /></th>
        <%-- 设备状态 --%>
        <%--<th><spring:message code="door.equipment.use" /></th>--%>
        <%-- 门店 --%>
        <th class="sort-column a.aoffice_name"><spring:message code="door.public.cust" /></th>
        <%-- 商户 --%>
        <th class="sort-column a.pOfficeName"><spring:message code="door.equipment.client" /></th>
        <%-- 维护机构 --%>
        <th class="sort-column a.vinoffice_name"><spring:message code="door.equipment.vinOffice" /></th>
        <%-- 机具余额 --%>
        <th class="sort-column a.surplusAmount"><spring:message code="door.equipment.surplusAmount" /></th>
        <%-- 存款机序列号 --%>
        <th class="sort-column a.id"><spring:message code="door.equipment.seriesNumber" /></th>
        <%-- 机型 --%>
        <th class="sort-column a.type"><spring:message code="door.equipment.type" /></th>
        <%-- IP --%>
        <th class="sort-column a.ip"><spring:message code="door.equipment.IP" /></th>
        <%-- 连线状态 --%>
        <th class="sort-column a.conn_status"><spring:message code="door.equipment.connStatus" /></th>
        <%-- 添加时间 --%>
        <th class="sort-column a.create_date"><spring:message code="door.equipment.addDate" /></th>
        <%-- 操作 --%>
        <th><spring:message code='common.operation'/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="EquipmentInfo" varStatus="status">
        <tr>
            <td>${status.index + 1}</td>
            <td>${EquipmentInfo.seriesNumber}</td>
           <%--  <td>
                <c:if test="${EquipmentInfo.status eq 1}">
                    <spring:message code="door.equipment.statusBind"/>
                </c:if>
                <c:if test="${EquipmentInfo.status ne 1}">
                    <spring:message code="door.equipment.statusNobind"/>
                </c:if>
            </td> --%>
            <td>${fns:getDictLabelWithCss(EquipmentInfo.status, "door_equip_status", "未命名", true)}</td>
            <%--<td>
                <c:if test="${EquipmentInfo.isUse eq 1}">
                    <spring:message code="door.equipment.isUse"/>
                </c:if>
                <c:if test="${EquipmentInfo.isUse ne 1}">
                    <spring:message code="door.equipment.isNotUse"/>
                </c:if>
            </td>--%>
            <td>${EquipmentInfo.aOffice.name}</td>
            <td>${EquipmentInfo.pOfficeName}</td>
            <td>${EquipmentInfo.vinOffice.name}</td>
            <td><fmt:formatNumber value="${EquipmentInfo.surplusAmount}" pattern="#,##0.00#" /></td>
            <td>${EquipmentInfo.id}</td>
            <td>${EquipmentInfo.type}</td>
            <td>${EquipmentInfo.IP}</td>
            <%-- <td>
                <c:if test="${EquipmentInfo.connStatus eq '01'}">
                    <spring:message code="door.equipment.normal"/>
                </c:if>
                <c:if test="${EquipmentInfo.connStatus eq '02'}">
                    <spring:message code="door.equipment.paused"/>
                </c:if>
                <c:if test="${EquipmentInfo.connStatus eq '03'}">
                    <spring:message code="door.equipment.shutDown"/>
                </c:if>
                <c:if test="${EquipmentInfo.connStatus eq '04'}">
                    <spring:message code="door.equipment.breakDown"/>
                </c:if>
                <c:if test="${EquipmentInfo.connStatus eq '05'}">
                    <spring:message code="door.equipment.unusual"/>
                </c:if>
            </td> --%>
            <td>${fns:getDictLabelWithCss(EquipmentInfo.connStatus, "CONN_STATUS", "未命名", true)}</td>
            <td><fmt:formatDate value="${EquipmentInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <shiro:hasPermission name="doorOrder:equipmentInfo:bind">
                    <c:if test="${EquipmentInfo.status eq '0'}">
				    	<span style='width:35px;display:inline-block;'> 
							<%-- 绑定 --%>
							<a href="${ctx}/doorOrder/v01/equipmentInfo/form?id=${EquipmentInfo.id}" title="<spring:message code='door.equipment.bind' />">
								<i class="fa fa-chain text-green fa-lg"></i>
							</a>
						</span>
                    </c:if>
                </shiro:hasPermission>
                <shiro:hasPermission name="doorOrder:equipmentInfo:nobind">
                    <c:if test="${EquipmentInfo.status eq '1'}">
						<span style='width:25px;display:inline-block;'> 
							<%-- 解绑 --%>
							<a  href="${ctx}/doorOrder/v01/equipmentInfo/nobind?id=${EquipmentInfo.id}&office=${EquipmentInfo.aOffice.id}"
                                onclick="return confirmx('<spring:message code="message.E7203"/>', this.href)" title="<spring:message code='door.equipment.nobind' />">
								<i class="fa fa-chain-broken text-red fa-lg"></i>
							</a>
						</span>
                    </c:if>
                </shiro:hasPermission>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</div>
<div class="pagination">${page}</div>
</body>
</html>