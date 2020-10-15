<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户清点量(日) -->
	<title><spring:message code='door.custWorkDay.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/collection/v03/custWorkDay/exportExcel");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/collection/v03/custWorkDay");		
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		 
		/**
		 * 行明细(面值)
		 *
		 * @param clearDate  ：日期
		 * @param custNo 	   ：门店ID
		 */
		function showParDetail(clearDate,custNo) {
			var content = "iframe:${ctx}/collection/v03/custWorkDay/showDetailPar?clearDate=" + clearDate + "&custNo=" + custNo;
			//面值明细
			top.$.jBox.open(
					content,
					"<spring:message code='door.public.denominationDetail' />", 550, 550, {
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
		
		/**
		 * 行明细(人员)
		 *
		 * @param clearDate  ：日期
		 * @param custNo 	   ：门店ID
		 */
		function showManDetail(clearDate,custNo) {
			var content = "iframe:${ctx}/collection/v03/custWorkDay/showDetailMan?clearDate=" + clearDate + "&custNo=" + custNo;
			//人员明细
			top.$.jBox.open(
					content,
					"<spring:message code='door.public.manDetail' />", 550, 550, {
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
		<!-- 客户清点量(日) -->
		<li class="active"><a><spring:message code="door.custWorkDay.title" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="custWorkDay"
		action="${ctx}/collection/v03/custWorkDay" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<%-- 门店 --%>
		<label><spring:message code="door.public.cust" />：</label>
		<spring:message code="door.custWorkDay.title" />
		<sys:treeselect id="searchCustName" name="searchCustNo"
				value="${custWorkDay.searchCustNo}" labelName="searchCustName"
				labelValue="${custWorkDay.searchCustName}" title="<spring:message code='door.public.cust' />"
				url="/sys/office/treeData" cssClass="required input-xsmall"
				notAllowSelectParent="false" notAllowSelectRoot="false" type="8"
			    isAll="true"  allowClear="true"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate" />：</label>
		<input 	id="createTimeStart"  
				name="createTimeStart" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${custWorkDay.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate" />：</label>
		<input 	id="createTimeEnd" 
				name="createTimeEnd" 
				type="text" 
				readonly="readonly" 
				maxlength="20" 
				class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${custWorkDay.createTimeEnd}" pattern="yyyy-MM-dd"/>"
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
				<%-- 日 期 --%>
				<th style="text-align: center;"><spring:message code="common.date" /></th>
				<%-- 门店名称 --%>
				<th style="text-align: center;"><spring:message code="door.public.custName" /></th>
				<%-- 合计单数 --%>
				<th style="text-align: center;"><spring:message code="door.public.sumCount" /></th>
				<%-- 合计数量（张） --%>
				<th style="text-align: center;"><spring:message code="door.public.sumSheetCount" /></th>
				<%-- 合计金额 --%>
				<th style="text-align: center;"><spring:message code="common.totalAmount" /></th>
				<%-- 操作 --%>
				<th style="text-align: center;"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="custWorkDay" varStatus="status">
				<tr>
					<td style="text-align: center;">${custWorkDay.clearDate}</td>
					<td style="text-align: left;">${custWorkDay.custName}</td>
					<td style="text-align: right;">${custWorkDay.sumBillCount}</td>
					<td style="text-align: right;">${custWorkDay.sumNumCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${custWorkDay.sumAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: center;">
						<%-- 人员明细 --%>
						<a href="#" onclick="showManDetail('${custWorkDay.clearDate}','${custWorkDay.custNo}');javascript:return false;"  title="<spring:message code='door.public.manDetail' />">
							<i class="fa fa-user  text-green fa-lg"></i>
						</a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<%-- 面值明细 --%>
						<a href="#" onclick="showParDetail('${custWorkDay.clearDate}','${custWorkDay.custNo}');javascript:return false;"  title="<spring:message code='door.public.denominationDetail' />">
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