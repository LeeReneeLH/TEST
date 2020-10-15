<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.tailBoxOutReport" /></title>
	<meta name="decorator" content="default" />
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
	    $(document).ready(function() {
		    $("#outDate").attr("value",getCookie('outDate'))
		    $("#btnDateChange").click(function(){
		    	setCookie('outDate',document.getElementById('outDate').value);
		    	var outDateRange=getCookie('outDate');
		    	$("#printForm").attr("action", "${ctx}/report/v01/store/outTailBoxInfoReport?outDateRange="+outDateRange+"");
				$("#printForm").submit();
		    })
    	});
		function printPlan(){
			$("#printConTainer").jqprint({importCSS: true});
		}
		function setCookie(sName, sValue){
			date = new Date();
			date.setDate(date.getDate()+1);
			document.cookie = sName+'='+escape(sValue)+'; expires='+date.toGMTString();
		}
		function getCookie(sName){
			var aCookie = document.cookie.split('; ');
			for(var i=0;i<aCookie.length;i++){
			   var aCrumb = aCookie[i].split('=');
			   if(sName==aCrumb[0]) return unescape(aCrumb[1]);
			}
			return null;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 应出库尾箱报表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.tailBoxOutReport" /></a></li>
	</ul>

	<form:form id="printForm" modelAttribute="stoReportInfo" class="breadcrumb form-search">
		<input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.print'/>" onclick="printPlan()" />
		可见天数：<input id="outDate" type="text" value="0" style="width:50px;"/>
		<input id="btnDateChange" type="button" class="btn btn-primary" value="<spring:message code='common.search'/>">
	</form:form>

	<div id="printConTainer">
		<form:form id="boxCountForm" modelAttribute="stoReportInfo" class="form-search">
<%-- 			<label><spring:message code="allocation.box.number.all"/>：</label>${stoReportInfo.boxCount} --%>
			<div class="boxhand-num text-center">
		<h4 style="font-weight:normal" class="text-red"><spring:message code="allocation.box.number.all"/></h4>
		<span class="text-red">${stoReportInfo.boxCount}</span>
		</div>
		</form:form>
		<table id="contentTable" class="table table-hover">
			<thead>
				<tr>
					<th><spring:message code="common.seqNo" /></th>
					<th class="sort-column a.box_no"><spring:message code="common.boxNo" /></th>
					<th><spring:message code="store.boxType" /></th>
					<th class="sort-column o5.name"><spring:message code="store.usedOutlets" /></th>
					<th class="sort-column a.box_status"><spring:message code="common.boxStatus" /></th>
					<th><spring:message code="common.outTime" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="box" varStatus="status">
					<tr>
						<td>${status.index+1}</td>
						<td>${box.id}</td>
						<td>${fns:getDictLabel(box.boxType,'sto_box_type',"")}</td>
						<td>${box.office.name}</td>
						<td>${fns:getDictLabel(box.boxStatus,'sto_box_status',"")}</td>
						<td><fmt:formatDate value="${box.outDate}" pattern="yyyy-MM-dd" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
