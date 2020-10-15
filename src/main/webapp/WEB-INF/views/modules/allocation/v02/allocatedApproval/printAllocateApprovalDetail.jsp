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
			<%--调款审批信息清单 --%>
			<label style="text-align: center"><font size="4"><b><spring:message code="allocation.allocate.approve.infoList" /></b></font></label>
		</div>
		<div style="float: left;width: 98%;position: relative;height:20px;">
			<div style="width: 30%;position: absolute;left: 0px;top: 0px;">${fns:getUser().office.name}</div>
			<%-- 记录笔数 --%>
			<div style="width: 30%;text-align: center;position: absolute;left: 30%;top: 0;"><spring:message code="common.recordCnt" />：${printDataList.size()}</div>
			<%-- 打印时间 --%>
			<div style="width: 30%;	text-align: right;position: absolute;right:5%;	top: 0px;"><spring:message code="common.printDateTime" />：${fns:getDate('yyyy-MM-dd HH:mm:ss')}</div>
		</div>
		
		<table id="contentTable"
			style="width: 100%; border:1px black solid; border-collapse:collapse; text-align: left; " border="1" >
			<thead>
				<tr>
					<%-- 申请机构 --%>
					<th style="text-align: center" ><spring:message code="allocation.application.office" /></th>
					<%-- 状态 --%>
					<th style="text-align: center" ><spring:message code="common.status" /></th>
					<%-- 预约日期 --%>
					<th style="text-align: center" ><spring:message code="allocation.order.date" /></th>
					<%-- 申请类型 --%>
					<th style="text-align: center" ><spring:message code="allocation.application.type" /></th>
					<%-- 流通券金额 --%>
					<th style="text-align: center"><spring:message code="common.circulation.amount" /></th>
					<%-- 原封新券金额 --%>
					<th style="text-align: center"><spring:message code="common.original.amount" /></th>
					<%-- 合计金额 --%>
					<th style="text-align:center;"><spring:message code="common.totalAmount" /></th>
					<%-- 审批人 --%>
					<th><spring:message code="allocation.approve.person" /></th>
					<%-- 创建时间 --%>
					<th><spring:message code="common.createDateTime" /></th>
				</tr>
				
			</thead>
			<tfoot>
				<tr>
					<%-- 合计 --%>
					<td style="text-align: right"><spring:message code="common.total" /></td>
					<td> </td>
					<td> </td>
					<td> </td>
					<%-- 流通券合计金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${fullCouponAllAmount}" pattern="#,##0.00#" /></td>
					<%-- 原封新券金额合计金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${originalCouponAllAmount}" pattern="#,##0.00#" /></td>
					<%-- 合计金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allAmount}" pattern="#,##0.00#" /></td>
					<td> </td>
					<td> </td>
				</tr>
			</tfoot>
			<tbody>
				<c:forEach items="${printDataList}"	var="approvalPrintDetail" varStatus="status">
						<tr>
							<%-- 申请机构 --%>
							<td style="text-align: left">
								${approvalPrintDetail.rofficeName}
							</td>
							<%-- 状态:已审批 --%>
							<td style="text-align: center"><spring:message code="common.status.approved" /></td>
							<%-- ${fns:getDictLabel(approvalPrintDetail.status,'pboc_order_handin_status',"")} --%>
							<%-- 预约日期 --%>
							<td><fmt:formatDate value="${approvalPrintDetail.applyDate}" pattern="yyyy-MM-dd" /></td>
							<%-- 申请类型 --%>
							<td>${fns:getDictLabel(approvalPrintDetail.businessType, 'all_businessType', '')}</td>
							<%-- 流通券金额 --%>
							<td style="text-align:right;"><fmt:formatNumber value="${approvalPrintDetail.fullCouponAmount}" pattern="#,##0.00#" /></td>
							<%-- 原封新券金额 --%>
							<td style="text-align:right;"><fmt:formatNumber value="${approvalPrintDetail.originalCouponAmount}" pattern="#,##0.00#" /></td>
							<%-- 合计金额 --%>
							<td style="text-align:right;"><fmt:formatNumber value="${approvalPrintDetail.confirmAmount}" pattern="#,##0.00#" /></td>
							<%-- 审批人 --%>
							<td>${approvalPrintDetail.approvalName}</td>
							<%-- 审批日期 --%>
							<td><fmt:formatDate value="${approvalPrintDetail.approvalDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						</tr>
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
