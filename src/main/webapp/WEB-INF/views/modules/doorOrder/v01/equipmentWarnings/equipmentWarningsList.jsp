<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		//window.parent.refreshTree();
	});
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	</script>
	
	<style>
		.ul-form{margin:0;display:table;}
	</style>
</head>
<body> 
		<!-- Tab页 -->
	    <ul class="nav nav-tabs">
		<li class="active">
			<a href="#"> 
				<!--  机具报警信息 -->
				  <spring:message code="sys.equWarn.equWarnTitle" />
			</a> 
		</li>
		</ul>
	
	  <form:form id="searchForm" modelAttribute="equipmentWarnings" 
	  		action="${ctx}/doorOrder/v01/equipmentWarnings/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		
		<!-- 机具编号(序列号) -->
		<div>
		<label><spring:message code="door.equipment.no" />：</label>
		<form:input path="seriesNumber" maxlength="10" class="input-small" style="width:150px;"/>
		</div>
		<!-- 机具名称 -->
		<%-- <li>
		<label>机具名称：</label>
		<form:input path="machName" maxlength="10" class="input-small" style="width:150px;"/></li>
		&nbsp;
		 --%>
		<!-- 机构名称 -->
	<%-- 	<li>
		<label>机构名称：</label>
				<sys:treeselect id="office" name="office.id"
				value="${equipmentWarnings.office.id}" labelName="office.name"
				labelValue="${equipmentWarnings.office.name}" title="机构名称"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="8"
				cssClass="input-medium" isAll="true"/> </li>
		&nbsp;  --%>
		
		<%-- 开始日期 --%>
		<div>
		<label><spring:message code="sys.equWarn.warnTime" />：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${equipmentWarnings.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		</div>
		<div>
		<label>~</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${equipmentWarnings.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		</div>
		<!-- 按钮 -->
		&nbsp;&nbsp;
		<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		</div>
			<%-- <li><input id="btnSubmit" class="btn btn-primary" 
			type="submit" value="<spring:message code='common.search'/>"/></li> --%>
	</div>
	</form:form>  
	<div class="table-con">
	<!-- 列表 -->
	<table id="treeTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo" /></th>
				<!-- 机具编号(序列号) -->
				<th class="sort-column E.SERIES_NUMBER"><spring:message code="door.equipment.no" /></th>
				<!-- 机具名称-->
				<!-- <th>机具名称</th> -->
				<!-- 机构名称-->
				<th class="sort-column E.aoffice_name"><spring:message code="sys.equWarn.doorName" /></th>
				<!-- 报警时间 -->
				<th class="sort-column E.warn_time" width="25%"><spring:message code="sys.equWarn.warnTime" /></th>
				<!-- 报警信息 -->
				<th class="sort-column E.warn_code"><spring:message code="sys.equWarn.warnCode" /></th>
				<!-- 报警 -->
				<th class="sort-column E.warn_name"><spring:message code="sys.equWarn.warnName" /></th>
				<th class="sort-column E.CLEAR_STATUS"><spring:message code="sys.equWarn.clearStatus" /></th>
				<th class="sort-column E.PRINTER_STATUS"><spring:message code="sys.equWarn.printerStatus" /></th>
				<th class="sort-column E.DOOR_STATUS"><spring:message code="sys.equWarn.doorStatus" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="equipmentWarnings" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td><%-- <a href="${ctx}/doorOrder/v01/equipmentWarnings/view?machNo=${equipmentWarnings.machNo}"> --%>${equipmentWarnings.seriesNumber}</td>
				<%-- <td>${equipmentWarnings.machName}</td> --%>
				<td>
					<c:if test="${equipmentWarnings.aofficeName == null}">
						<spring:message code="sys.equWarn.nobind" />
					</c:if>
					${equipmentWarnings.aofficeName}
				</td>
				<td><fmt:formatDate value="${equipmentWarnings.warnTime}" pattern="yyyy-MM-dd HH:mm:ss" />
					&nbsp;&nbsp;~ &nbsp;&nbsp;
				<c:if test="${equipmentWarnings.updateDate eq null || equipmentWarnings.updateDate eq ''}">
					<fmt:formatDate value="${equipmentWarnings.warnTime}" pattern="yyyy-MM-dd HH:mm:ss" />
				</c:if>
				<c:if test="${equipmentWarnings.updateDate ne null || equipmentWarnings.updateDate ne ''}"> 
					<fmt:formatDate value="${equipmentWarnings.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</c:if>
				<td>${equipmentWarnings.warnCode}</td>
				<td>${equipmentWarnings.warnName}</td>
				<td>${fns:getDictLabelWithCss(equipmentWarnings.clearStatus, 'equ_clear_status', '未知',true)}</td>
			    <td>${fns:getDictLabelWithCss(equipmentWarnings.printerStatus, 'equ_printer_status', '未知',true)}</td>
			    <td>${fns:getDictLabelWithCss(equipmentWarnings.doorStatus, 'equ_door_status', '未知',true)}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div> 
</body>
</html>
