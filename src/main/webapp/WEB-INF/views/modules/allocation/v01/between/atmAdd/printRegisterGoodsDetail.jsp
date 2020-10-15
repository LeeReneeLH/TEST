<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 库间配钞配款清单 --%>
	<title><spring:message code="allocation.cashBetween.atm.add.detail.list" /></title>
	<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv">
		<div style="text-align: center;margin-top: 20px;">
			<%-- 现金库登记清单 --%>
			<label style="text-align: center"><font size="4"><b><spring:message code="allocation.cashBetween.atm.add.detail.list" /></b></font></label>
		</div>
		<div style="float: left;width: 98%;position: relative;height:20px;">
			<%-- 打印时间 --%>
			<div style="width: 45%;	text-align: right;position: absolute;right:5%;	top: 0px;"><spring:message code="common.printDateTime" />：${fns:getDate('yyyy-MM-dd HH:mm:ss')}</div>
		</div>
		<div style="width: 100%">
			<%-- 登记机构 --%>
			<label style="text-align: left;"><spring:message code="clear.orderClear.registerOffice" />：${allAllocateInfo.rOffice.name}</label>
		</div>
		<div style="width: 100%">
			<%-- 配钞机构 --%>
			<label style="text-align: left;"><spring:message code="allocation.atm.add.office" />：${allAllocateInfo.aOffice.name}</label>
		</div>
		<div style="width: 100%">
			<%-- 登记日期 --%>
			<label style="text-align: left;"><spring:message code="clear.atmCashBox.registerDate" />：<fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></label>
		</div>
		<div style="width: 100%">
			<%-- 登记金额 --%>
			<label style="text-align: left;"><spring:message code="allocation.register.amount" />：<fmt:formatNumber value="${allAllocateInfo.registerAmount}" pattern="#,##0.00#" /></label>
			<%-- 登记金额(格式化) --%>
			<label style="margin-left:10px">${sto:getUpperAmount(allAllocateInfo.registerAmount)}</label> 
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
					<%-- 金额 --%>
					<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
					
				</tr>
			</thead>
			<tbody>
				<% int iRegisterIndex = 0; %>
				<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
					<%-- 登记物品 --%>
					<c:if test="${item.confirmFlag == '0'}">
						<% iRegisterIndex = iRegisterIndex + 1; %>
						<tr id="${item.goodsId}">
							<td style="text-align:center;"><%=iRegisterIndex %></td>
							<td style="text-align:center;">${sto:getGoodsName(item.goodsId)}</td>
							<td style="text-align:right;">${item.moneyNumber}</td>
							<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
		<div style="width: 90%;margin-top: 10px;">
			<%-- 制单 --%>
			<ul style="text-align: right;"><spring:message code="common.voucherMaking" />：${fns:getUser().name}</ul>
		</div>
	</div>
</body>
</html>
