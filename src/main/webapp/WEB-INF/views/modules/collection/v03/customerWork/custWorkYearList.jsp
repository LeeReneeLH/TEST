<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 客户清点量(年) --%>
	<title><spring:message code='door.custWorkYear.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/collection/v03/custWorkYear/exportExcel");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/collection/v03/custWorkYear");		
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
			var content = "iframe:${ctx}/collection/v03/custWorkYear/showDetailPar?clearDate=" + clearDate + "&custNo=" + custNo;
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
			var content = "iframe:${ctx}/collection/v03/custWorkYear/showDetailMan?clearDate=" + clearDate + "&custNo=" + custNo;
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
		<%-- 客户清点量(年) --%>
		<li class="active"><a><spring:message code='door.custWorkYear.title'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="custWorkYear"
		action="${ctx}/collection/v03/custWorkYear" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<%-- 门店 --%>
		<label><spring:message code="door.public.cust" /> ：</label>
		<sys:treeselect id="searchCustName" name="searchCustNo"
				value="${custWorkYear.searchCustNo}" labelName="searchCustName"
				labelValue="${custWorkYear.searchCustName}" title="门店"
				url="/sys/office/treeData" cssClass="required input-xsmall"
				notAllowSelectParent="false" notAllowSelectRoot="false" type="8"
			    isAll="true"  allowClear="true"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		<%-- 年份--%>
		<label class="control-label"><spring:message code="door.custWorkYear.year" />：</label>
		<form:select path="searchYear" id="searchYear" class="input-large required" style="font-size:15px;color:#000000">
			<form:option value=""><spring:message code="common.select" /></form:option>
			<form:options items="${yearList}" htmlEscape="false" />
		</form:select>
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
				<%-- 操作--%>
				<th style="text-align: center;"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="custWorkYear" varStatus="status">
				<tr>
					<td style="text-align: center;">${custWorkYear.clearDate}</td>
					<td style="text-align: left;">${custWorkYear.custName}</td>
					<td style="text-align: right;">${custWorkYear.sumBillCount}</td>
					<td style="text-align: right;">${custWorkYear.sumNumCount}</td>
					<td style="text-align: right;"><fmt:formatNumber value="${custWorkYear.sumAmount}" type="currency" pattern="#,##0.00"/></td>
					<td style="text-align: center;">
						<%-- 人员明细--%>
						<a href="#" onclick="showManDetail('${custWorkYear.clearDate}','${custWorkYear.custNo}');javascript:return false;"  title="<spring:message code='door.public.manDetail' />">
							<i class="fa fa-user  text-green fa-lg"></i>
						</a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<%-- 面值明细--%>
						<a href="#" onclick="showParDetail('${custWorkYear.clearDate}','${custWorkYear.custNo}');javascript:return false;"  title="<spring:message code='door.public.denominationDetail' />">
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