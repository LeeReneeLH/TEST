<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务管理 -->
	<title><spring:message code="clear.centerAccounts.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				if ($("#custNo").val()=='') {
					alertx("商户名称不能为空");
					return false;
				}
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/guestAccounts/exportGuestAccounts/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/merchantlist/");	
			});
 			$("#btnSubmit").on('click',function(){
 				if ($("#custNo").val()=='') {
					alertx("商户名称不能为空");
					return false;
				}
				$("#searchForm").submit();
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:choose>
			<c:when test="${type eq 'cash'}">
				<!-- 现金 -->
				<li><a onClick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/guestAccounts/list?type=cash"><spring:message code="door.accountManage.doorAccountsList"/></a></li>
				<li class="active"><a href="${ctx}/doorOrder/v01/guestAccounts/merchantlist?type=cash"><spring:message code="door.accountManage.merchantAccountsList"/></a></li>
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</ul>
	<form:form id="searchForm" modelAttribute="doorCenterAccountsMain" action="${ctx}/doorOrder/v01/guestAccounts/merchantlist" method="post">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<!-- 商户名称 -->
		<%-- <label><spring:message code="door.accountManage.store" />：</label>
		<form:select path="merchantOfficeId" id="custNo"
					class="input-large required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('9',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select> --%>
		<!-- 查询 -->
		<%-- <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />&nbsp; --%>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 商户名称 -->
				<th class="sort-column r.name"><spring:message code="door.accountManage.store" /></th>
				<!-- 借方(元) -->
				<th><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 贷方(元) -->
				<th><spring:message code="clear.centerAccounts.lender"/></th>
				<!-- 余额(元) -->
				<th><spring:message code="clear.centerAccounts.balance"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="centerAccountsMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${centerAccountsMain.merchantOfficeName}
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.inAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.inAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<fmt:formatNumber value="${centerAccountsMain.guestTotalAmount}" pattern="#,##0.00#" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>