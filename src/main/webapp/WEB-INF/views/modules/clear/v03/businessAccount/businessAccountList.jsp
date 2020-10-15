<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户券别 -->
	<title><spring:message code="clear.businessAccount.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").on('click',function(){
				 if($('#custNo').val()==""){
						alertx("请选择客户名称");
						return false;
					} 
				$("#searchForm").prop("action", "${ctx}/clear/v03/businessAccount/list/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/clear/v03/businessAccount/");
			});
			$("#exportSubmit").on('click',function(){
				 if($('#custNo').val()==""){
					alertx("请选择客户名称");
					return false;
				} 
				$("#searchForm").prop("action", "${ctx}/clear/v03/businessAccount/exportBusinessAccounts/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/clear/v03/businessAccount/");		
			});
			 if($('#custNo').val()==""){
				 	$("#total").hide();
				} else{
					$("#total").show();
				}
		});
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
		<!-- 现金 -->
		<li class="active"><a href="${ctx}/clear/v03/businessAccount/list"><spring:message code="clear.centerAccounts.cash"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="${ctx}/clear/v03/businessAccount/" method="post" class="breadcrumb form-search">
		<%-- <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/> --%>
		
		<!-- 客户名称 -->
		<label><spring:message code="clear.public.custName" />：</label>
			<form:select path="clientId" id="custNo"
					class="input-large required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('1,3',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
			</form:select>
			&nbsp;&nbsp;
		<!--  清分属性去除 wzj 2017-11-16 begin -->
			<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="rofficeId" name="rofficeId"
				value="${centerAccountsMain.rofficeId}" labelName="office.name"
				labelValue="${centerAccountsMain.rofficeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->	
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>" />&nbsp;
		<!-- 导出 -->
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover" >
		<thead>
			<tr>
				<!-- 序号 -->
				<th style="text-align: center;"><spring:message code='common.seqNo'/></th>
				<!-- 物品类型 -->
				<th style="text-align: center;"><spring:message code='clear.businessAccount.itemType'/></th>
				<!-- 面值 -->
				<th style="text-align: center;"><spring:message code='common.denomination'/></th>
				<!-- 总金额 -->
				<th style="text-align: center; "><spring:message code='clear.public.totalMoney'/></th>
				
			</tr>
		</thead>
		<tbody>
		<!-- <div style="width:50%"> -->
			<c:choose>
			
						<c:when test="${centerAccountsMain.clientId!=null}">
							<c:forEach  items="${centerAccountsMain.centerAccountsDetailList}" var="centerAccountsMains" varStatus="status" >
								<tr>
									<td style="text-align: center;">${status.index+1}</td>
									<%-- <td>${fns:getDictLabel(centerAccountsMains.currency,'money_currency',"")}</td> --%>
									<td style="text-align: center;">${centerAccountsMains.currency}</td>
									<td style="text-align: center;">${sto:getGoodDictLabel(centerAccountsMains.denomination, 'cnypden', "")}</td>
									<td style="text-align: right;width:20%"><fmt:formatNumber value="${centerAccountsMains.totalAmount}" pattern="#,##0.00#" /></td>
								</tr>
							</c:forEach>
			
						</c:when>
						<c:otherwise>
						
						</c:otherwise>
			</c:choose>
		<!-- 	</div> -->
		</tbody>
	</table>
		<div id="total" style="width:200px;float:right ;" >
			<!-- 总计 -->
			<spring:message code='clear.businessAccount.total'/>:<fmt:formatNumber value="${totalMoney}" pattern="#,##0.00#" /><spring:message code='clear.businessAccount.yuan'/>
		</div>
</body>
</html>