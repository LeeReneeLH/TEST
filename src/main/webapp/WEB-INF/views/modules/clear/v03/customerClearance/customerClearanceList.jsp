<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='clear.task.distribution.list'/></title>
	<meta name="decorator" content="default"/>
	<%-- <script type="text/javascript" src="${ctxStatic}/common/common.js"></script> --%>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			 $("#btnExport").click(function(){
				  if($('#createTimeStart').val()==""&&$('#createTimeEnd').val()!=""){
						alertx("请选择开始日期");
						return false;
					} 
				 if($('#createTimeEnd').val()==""&&$('#createTimeStart').val()!=""){
						alertx("请选择结束日期");
						return false;
					}   	
					$("#searchForm").attr("action", "${ctx}/clear/v03/customerClearance/exportCustomerReport");
					$("#searchForm").submit();
					$("#searchForm").attr("action", "${ctx}/clear/v03/customerClearance/");
				}); 
			 
			 $("#btnSubmit").on('click',function(){
				 if($('#createTimeStart').val()==""&&$('#createTimeEnd').val()!=""){
						alertx("请选择开始日期");
						return false;
					} 
				 if($('#createTimeEnd').val()==""&&$('#createTimeStart').val()!=""){
						alertx("请选择结束日期");
						return false;
					}   
				$("#searchForm").prop("action", "${ctx}/clear/v03/customerClearance/list/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/clear/v03/customerClearance/");
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		// 打印明细
		function printDetail(taskNo){
			alertx("暂不处理");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 查询一览 -->
		<li class="active"><a href="${ctx}/clear/v03/customerClearance/"><spring:message code='clear.customerClearance.queryIn'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="customerClearance"
		action="${ctx}/clear/v03/customerClearance/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
	 <c:choose>
     <c:when test="${fns:getUser().office.type eq '1'}">
     </c:when>
      <c:when test="${fns:getUser().office.type eq '3'}">
     </c:when>
     <c:otherwise>
         <!-- 客户名称 -->
		<label><spring:message code="clear.public.custName"/>：</label>
			<form:select path="rOffice.id" id="custNo"
					class="input-large required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('1,3',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
			</form:select>
			<form:hidden path="rOffice.name" />
			&nbsp;&nbsp;
     </c:otherwise>
	 </c:choose>		
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dateStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeEnd\',{y:-1})}',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dateEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'#F{$dp.$D(\'createTimeStart\',{y:+1})||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		<!-- 增加清分机构 qph 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${officeId}" labelName="office.name"
				labelValue="${officeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"	
				cssClass="input-medium"  isAll="${fns:getUser().office.type eq '3'?'true':'false'}"/>
		<!-- end -->
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
		&nbsp;
		<!-- 导出excel -->
		<input id="btnExport" class="btn btn-red" type="button"
			value="<spring:message code='common.export'/>" />
	</form:form>
	
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 日期 -->
				<th colspan="1" rowspan="2" style="text-align: center;line-height:80px;font-size:17px;"><spring:message code='common.date'/></th>
				<!-- 客户名称 -->
				<th colspan="1" rowspan="2" style="text-align: center;line-height:80px;font-size:17px"><spring:message code="clear.public.custName"/></th>
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 清分币 -->
				<th colspan="10" style="text-align:center;background-color:#f9c9c9"><spring:message code="clear.customerClearance.qingCents"/></th>
				<!-- 复点币 -->
				<th colspan="10" style="text-align: center;background-color:#ddd"><spring:message code="clear.customerClearance.afterSomeCoins"/></th>
				<!-- ATM币 -->
				<th colspan="3" style="text-align: center;background-color:#c8def5"><spring:message code="clear.customerClearance.ATMCents"/></th>
			
			</tr>
			<tr>
				<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#f5e6e6">${item.label}</th>
				</c:forEach>
				<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#eee">${item.label}</th>
				</c:forEach>
				<th style="text-align: center;background-color:#eee">100元</th>
				<th style="text-align: center;background-color:#eee">50元</th>
				<th style="text-align: center;background-color:#eee">20元</th>
				<%-- <c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#edf6ff">${item.label}</th>
				</c:forEach> --%>
				
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="customer">
			<tr>
				<!-- style="width:45px; word-break:break-all" -->
				<td >${customer.filterCondition}</td>
				<td>${customer.custName}</td>
				<!-- 增加机构 wzj 2017-11-27 begin -->
				<td>${customer.officeName} </td>
				<!-- end -->
				<td style="background-color:#f5e6e6">${customer.cl1}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl2}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl3}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl4}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl5}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl6}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl7}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl8}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl9}</td>
			   	<td style="background-color:#f5e6e6">${customer.cl10}</td>
			   	<td style="background-color:#eee">${customer.re1}</td>
				<td style="background-color:#eee">${customer.re2}</td>
			   	<td style="background-color:#eee">${customer.re3}</td>
			   	<td style="background-color:#eee">${customer.re4}</td>
			   	<td style="background-color:#eee">${customer.re5}</td>
			   	<td style="background-color:#eee">${customer.re6}</td>
			   	<td style="background-color:#eee">${customer.re7}</td>
			   	<td style="background-color:#eee">${customer.re8}</td>
			   	<td style="background-color:#eee">${customer.re9}</td>
			   	<td style="background-color:#eee">${customer.re10}</td>
			   	<td style="background-color:#edf6ff">${customer.atm1}</td>
			   	<td style="background-color:#edf6ff">${customer.atm2}</td>
			   	<td style="background-color:#edf6ff">${customer.atm3}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>