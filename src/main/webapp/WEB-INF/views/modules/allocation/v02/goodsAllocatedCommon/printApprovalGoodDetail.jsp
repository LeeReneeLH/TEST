<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv">
		<div style="text-align: center;margin-top: 20px;">
			<%-- 51：申请下拨,50：申请上缴,52：代理上缴 --%>
			<c:if test="${pbocAllAllocateInfo.businessType == '51'}">
				<%-- 商行调款审批清单 --%>
				<label style="text-align: center"><font size="4"><b><spring:message code="allocation.cb.allocate.approve.infoList" /></b></font></label>
			</c:if>
			<c:if test="${pbocAllAllocateInfo.businessType == '50'
							or pbocAllAllocateInfo.businessType == '52' }">
				<%-- 商行上缴审批清单 --%>
				<label style="text-align: center"><font size="4"><b><spring:message code="allocation.cb.handin.approve.infoList" /></b></font></label>
			</c:if>
		</div>
		<div style="float: left;width: 98%;position: relative;height:20px;">
			<div style="width: 45%;position: absolute;left: 0px;top: 0px;">${fns:getUser().office.name}</div>
			<%-- 打印时间 --%>
			<div style="width: 45%;	text-align: right;position: absolute;right:5%;	top: 0px;"><spring:message code="common.printDateTime" />：${fns:getDate('yyyy-MM-dd HH:mm:ss')}</div>
		</div>
		<div style="width: 100%">
			<%-- 申请机构 --%>
			<label style="text-align: left;"><spring:message code="allocation.application.office" />：${pbocAllAllocateInfo.rofficeName}</label>
		</div>
		<div style="width: 100%">
			<%-- 预约日期 --%>
			<label style="text-align: left;"><spring:message code="allocation.order.date" />：<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/></label>
		</div>
		<div style="width: 100%">
			<%-- 总金额 --%>
			<label style="text-align: left;"><spring:message code="common.totalMoney" />：<fmt:formatNumber value="${pbocAllAllocateInfo.confirmAmount}" pattern="#,##0.00#" /></label>
			<%-- 总金额(格式化) --%>
			<label style="margin-left:10px">${pbocAllAllocateInfo.confirmAmountBig}</label>
		</div>
		<table id="contentTable"
			style="width: 100%; border:1px black solid; border-collapse:collapse; text-align: left; " border="1" >
			<thead>
				<tr>
					<%-- 序号 --%>
					<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
					<%-- 物品名称 --%>
					<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
					<%-- 数量 --%>
					<th style="text-align: center" ><spring:message code="common.number" /></th>
					<%-- 金额(元) --%>
					<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
					
				</tr>
			</thead>
			<tbody>
				<% int iConfirmIndex = 0; %>
				<c:forEach items="${pbocAllAllocateInfo.pbocAllAllocateItemList}" var="item" varStatus="status">
					<%-- 审批登记物品 --%>
					<c:if test="${item.registType == '10'}">
						<% iConfirmIndex = iConfirmIndex + 1; %>
						<tr id="${item.goodsId}">
							<td style="text-align:right;"><%=iConfirmIndex %></td>
							<td style="text-align:right;">${sto:getGoodsName(item.goodsId)}</td>
							<td style="text-align:right;">${item.moneyNumber}</td>
							<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
		<div style="width: 90%;">
			<%-- 制单 --%>
			<ul style="text-align: right;"><spring:message code="common.voucherMaking" />：${fns:getUser().name}</ul>
		</div>
	</div>
</body>
</html>
