<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv">
		<div style="text-align: center;margin-top: 15px;">
			<%-- 原封新券入库库区摆放信息 --%>
			<label style="text-align: center"><font size="4"><b><spring:message code="allocation.originalBanknot.instore.positionInfo" /> </b></font></label>
		</div>
		<HR style="FILTER: alpha(opacity=100,finishopacity=100,style=1)" width="100%" color="#987cb9" SIZE="1">
		<label style="text-align: left;margin-left: 20px;">${fns:getUser().office.name}</label>
		<%-- 打印时间 --%>
		<label style="text-align: left;margin-left: 500px;"><spring:message code="common.printDateTime" />：${fns:getDate('yyyy-MM-dd HH:mm:ss')}</label>
		<table id="contentTable"
			style="width: 100%; border:1px black solid; border-collapse:collapse; text-align: left; " border="1" >
			<thead>
				<tr>
					<%-- 流水单号 --%>
					<th style="text-align: center" rowspan="2"><spring:message code="allocation.allId" /></th>
					<%-- 序号 --%>
					<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
					<%-- 物品名称 --%>
					<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
					<%-- 金额(元) --%>
					<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
					<%-- 箱袋编号 --%>
					<th style="text-align: center" ><spring:message code="common.boxNo" /></th>
					<%-- 原封券翻译 --%>
					<th style="text-align:center;"><spring:message code="store.original.translation" /></th>
					<%-- 库区  --%>
					<th style="text-align: center"><spring:message code="store.area" /></th>
				</tr>
				
			</thead>
			<tbody>
				<c:forEach items="${printDataList}"	var="allAllocateInfo" varStatus="status">
						<tr>
							<%-- 流水单号 --%>
							<td style="text-align: center" rowspan="${allAllocateInfo.pbocAllAllocateDetailList.size() + 1}">
								${allAllocateInfo.allId}
							</td>
							
							<%-- 取包明细 --%>
							<c:forEach items="${allAllocateInfo.pbocAllAllocateDetailList}" var="pbocAllAllocateDetail" varStatus="status">
								<tr>
									<%-- 序号 --%>
									<td style="text-align:right;">${status.index + 1}</td>
									<%-- 物品名称 --%>
									<td style="text-align:left;">${sto:getGoodsName(pbocAllAllocateDetail.goodsLocationInfo.goodsId)}</td>
									<%-- 金额(元) --%>
									<td style="text-align:right;"><fmt:formatNumber value="${pbocAllAllocateDetail.goodsLocationInfo.amount}" pattern="#,##0.00#" /></td>
									<%-- 箱袋编号 --%>
									<td style="text-align:center;">${pbocAllAllocateDetail.rfid}</td>
						           	<%-- 原封券翻译 --%>
									<td style="text-align:center;">${pbocAllAllocateDetail.stoOriginalBanknote.originalTranslate}</td>
						            <%-- 库区位置 --%>
						            <td style="text-align:right;">${fns:getDictLabel(pbocAllAllocateDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${pbocAllAllocateDetail.goodsLocationInfo.storeAreaName}</td>
								</tr>
							</c:forEach>
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
