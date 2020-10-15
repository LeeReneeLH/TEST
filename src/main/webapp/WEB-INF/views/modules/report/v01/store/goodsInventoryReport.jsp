<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="report.goodsInventoryReport" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");

			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/store/exportGoodsInventoryReport");
				$("#searchForm").submit();
				//$("#searchForm").attr("action", "${ctx}/report/v01/store/goodsInventoryReport?searchFlag=true");
				
			});
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/report/v01/store/goodsInventoryReport?searchFlag=true");
				$("#searchForm").submit();
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
        <%-- 物品库存报表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="report.goodsInventoryReport" /></a></li>
       
	</ul>
	
	<form:form id="searchForm" modelAttribute="stoReportInfo" method="post" class="breadcrumb form-search">
		<shiro:hasPermission name="report:system:user">
			<!-- 机构 -->
			<label class="control-label"><spring:message code="allocation.register.office"/>：</label>
			<%-- <c:if test="${fns:getUser().office.type != '0'}"> --%>
			<sys:treeselect id="office" 
							name="office.id" 
				 			value="${stoReportInfo.office.id}" 
				 			labelName="office.name"  
				 			labelValue="${stoReportInfo.office.name}"
				 			title="金库" 
				 			url="/sys/office/treeData" 
				 			cssClass="required input-small" notAllowSelectParent="false" notAllowSelectRoot="false" type="3" allowClear="true" />
		
		   <%-- 	</c:if>	 --%>
		</shiro:hasPermission>
		<%-- 币种 --%>
		<label><spring:message code='common.currency'/>：</label>
		<form:select path="currency" id="currency" class="input-small">
			<form:options items="${sto:getGoodDictList('currency')}" 
                      itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>

		<%-- 查询按钮 --%>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		&nbsp;<input id="btnExport" class="btn btn-red" type="submit" value="<spring:message code='common.export'/>"/>
	</form:form>
	
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 机构 -->
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.office'/></th>
				<%-- 日期 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.date'/></th>
				<%-- 币种 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.currency'/></th>
				<%-- 类别 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.classification'/></th>
				<%-- 套别 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.edition'/></th>
				<%-- 材质 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.cash'/></th>
				<%-- 面值 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.denomination'/></th>
				<%-- 单位 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.units'/></th>
				<%-- 数量 --%>
				<th style="text-align:center;vertical-align:middle;" rowspan=2><spring:message code='common.number'/></th>
				<%-- 金额(币种：&) --%>
				<th style="text-align:center" colspan=4>
					<%-- 金额 --%>
					<spring:message code='common.amount'/>
					(<spring:message code='common.currency'/>：${sto:getGoodDictLabel(stoReportInfo.currency, "currency", "")}
					)
				</th>
			</tr>
			<tr>
				<th style="text-align:center"><spring:message code='report.moneyType.trade'/></th>
				<th style="text-align:center"><spring:message code='report.moneyType.damage'/></th>
				<th style="text-align:center"><spring:message code='report.moneyType.counterfeit'/></th>
				<th style="text-align:center"><spring:message code='report.moneyType.countwait'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="goodsInfo" varStatus="status">
			<tr>
			    <td style="text-align:center">${goodsInfo.office.name}</td>
				<td style="text-align:center">${goodsInfo.time}</td>
				<td style="text-align:center">${goodsInfo.currency}</td>
				<td style="text-align:center">${goodsInfo.classification}</td>
				<td style="text-align:center">${goodsInfo.edition}</td>
				<td style="text-align:center">${goodsInfo.cash}</td>
				<td style="text-align:center">${goodsInfo.denomination}</td>
				<td style="text-align:center">${goodsInfo.unit}</td>
				<td style="text-align:center">${goodsInfo.number}</td>
				<td style="text-align:right"><fmt:formatNumber value="${goodsInfo.amount}" pattern="#,##0.00#"/></td>
				<td style="text-align:right"><fmt:formatNumber value="${goodsInfo.amountDamage}" pattern="#,##0.00#"/></td>
				<td style="text-align:right"><fmt:formatNumber value="${goodsInfo.amountCounterfeit}" pattern="#,##0.00#"/></td>
				<td style="text-align:right"><fmt:formatNumber value="${goodsInfo.amountCountwait}" pattern="#,##0.00#"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
