<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code='clear.task.distribution.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/collection/v03/empWorkDetail/exportExcel");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/collection/v03/empWorkDetail");		
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		/**
		 * 行明细
		 *
		 * @param clearDate  ：日期
		 * @param clearManId ：清分人
		 */
		function showDetail(clearDate,clearManId) {
			var content = "iframe:${ctx}/collection/v03/empWorkDetail/showDetail?clearDate=" + clearDate + "&clearManId=" + clearManId;
			//明细
			top.$.jBox.open(
					content,
					"<spring:message code='common.detail' />", 550, 550, {
						buttons : {
							//关闭
							"<spring:message code='common.close' />" : true 
						},
						submit : function(v, h, f) {
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
			});
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 员工工作量明细 -->
		<li class="active"><a><spring:message code="door.empWorkDetail.title" /></a></li>
	</ul>
	<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
	<form:form id="searchForm" modelAttribute="empWorkDetail"
		action="${ctx}/collection/v03/empWorkDetail" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<%-- 清分人 --%>
		<label class="control-label"><spring:message code="door.empWorkDetail.clearMan" />：</label>
		<form:select path="searchClearManNo" id="searchClearManNo" class="input-large required" style="font-size:15px;color:#000000">
			<form:option value=""><spring:message code="common.select" /></form:option>
			<form:options items="${sto:getUsersByTypeAndOffice(ConClear,'')}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
		</form:select>
		
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate" />：</label>
		<input 	id="createTimeStart"  
				name="createTimeStart" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${empWorkDetail.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate" />：</label>
		<input 	id="createTimeEnd" 
				name="createTimeEnd" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${empWorkDetail.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- 查询 --%>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
		&nbsp;&nbsp;
		<%-- 导出 --%>
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 日 期 -->
				<th style="text-align: center;"><spring:message code="common.date" /></th>
				<!-- 清分人 -->
				<th style="text-align: center;"><spring:message code="door.empWorkDetail.clearMan" /></th>
				<!-- 合计单数 -->
				<th style="text-align: center;"><spring:message code="door.empWorkDetail.sumCount" /></th>
				<!-- 合计数量（张） -->
				<th style="text-align: center;"><spring:message code="door.empWorkDetail.sumSheetCount" /></th>
				<!-- 合计金额 -->
				<th style="text-align: center;"><spring:message code="common.totalAmount" /></th>
				<!-- 操作 -->
				<th style="text-align: center;"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="empWorkDetail" varStatus="status">
				<tr>
					<td style="text-align: center;">${empWorkDetail.clearDate}</td>
					<td style="text-align: left;">${empWorkDetail.empName}</td>
					<td style="text-align: right;">${empWorkDetail.sumBillCount}</td>
					<td style="text-align: right;">${empWorkDetail.sumNumCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${empWorkDetail.sumAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: center;">
						<!-- 查看明细 -->
						<a href="#" onclick="showDetail('${empWorkDetail.clearDate}','${empWorkDetail.clearManId}');javascript:return false;"  title="<spring:message code='door.empWorkDetail.viewDetail'/>">
							<i class="fa fa-eye fa-lg"></i>
						</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>