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
			<%-- 发行基金调拨出库 --%>
			<label style="text-align: center"><font size="4"><b><spring:message code="allocation.issueFund.sametrade.outStore" /></b></font></label>
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
					<%-- 机构 --%>
					<th style="text-align: center" rowspan="2"><spring:message code="allocation.application.office" /></th>
					<%-- 配款明细 --%>
					<th style="text-align: center" colspan="3"><spring:message code="allocation.quota.detail" /></th>
					<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
					<%-- 取包明细 --%>
					<th style="text-align: center" colspan="3"><spring:message code="allocation.getBagDetail" /></th>
					</c:if>
				</tr>
				<tr>
				
					<%-- 物品名称 --%>
					<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
					<%-- 数量(包) --%>
					<th style="text-align: center" ><spring:message code="common.number" /><spring:message code="common.units.bag" /></th>
					<%-- 金额(元) --%>
					<th style="text-align: center" ><spring:message code="common.amount" /><spring:message code="common.units.yuan.alone" /></th>
					<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
					<%-- 物品名称 --%>
					<th style="text-align: center" ><spring:message code="store.goodsName" /></th>
					<%-- 数量(包) --%>
					<th style="text-align: center"><spring:message code="common.number" /><spring:message code="common.units.bag" /></th>
					<%-- 库区 / RFID编号  / 日期 --%>
					<th style="text-align: center"><spring:message code="store.area" />&nbsp;/&nbsp;<spring:message code="common.boxNo" />&nbsp;/&nbsp;<spring:message code="common.date" /></th>
					</c:if>
				</tr>
				
			</thead>
			<tbody>
				<c:forEach items="${printDataList}"	var="allAllocateInfo" varStatus="status">
						<tr>
							<%-- 流水单号 --%>
							<td style="text-align: center" rowspan="${allAllocateInfo.printRowSpanNum + 1}">
								${allAllocateInfo.allId}
							</td>
							<%-- 机构 --%>
							<td style="text-align: center" rowspan="${allAllocateInfo.printRowSpanNum + 1}">
								${allAllocateInfo.roffice.name}
							</td>
							<%-- 取包明细 --%>
							<c:forEach items="${allAllocateInfo.pbocAllAllocateItemList}" var="allAllocateItem">
								<tr>
									<%-- 物品名称 --%>
									<td style="text-align: left" rowspan="${allAllocateItem.goodsAreaDetailList.size() + 1}">
										${allAllocateItem.goodsName}
									</td>
									<%-- 数量(包) --%>
									<td style="text-align: right" rowspan="${allAllocateItem.goodsAreaDetailList.size() + 1}">
										${allAllocateItem.moneyNumber}
									</td>
									<%-- 金额 --%>
									<td style="text-align: right" rowspan="${allAllocateItem.goodsAreaDetailList.size() + 1}">
										<fmt:formatNumber value="${allAllocateItem.moneyAmount}" pattern="#,##0.00#" />
									</td>
									<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
									<c:forEach items="${allAllocateItem.goodsAreaDetailList}" var="areaDetail">
										<tr>
											<%-- 物品名称 --%>
											<td style="text-align: left">
												${sto:getGoodsName(areaDetail.goodsLocationInfo.goodsId)}
											</td>
											<%-- 数量(包) --%>
											<td style="text-align: right">
												${areaDetail.goodsLocationInfo.goodsNum}
											</td>
											<%-- 库区位置 --%>
											<td style="text-align: left">
												<c:if test="${areaDetail.isNecessaryOut =='1' }">
													${fns:getDictLabel(areaDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${areaDetail.goodsLocationInfo.storeAreaName}
													&nbsp;/&nbsp;${areaDetail.goodsLocationInfo.rfid}
													&nbsp;/&nbsp;<fmt:formatDate value="${areaDetail.goodsLocationInfo.inStoreDate}" pattern="yyyy-MM-dd"/>
												</c:if>	
												<c:if test="${areaDetail.isNecessaryOut =='0' }">
													<font color="red">
														<B>
															${fns:getDictLabel(areaDetail.goodsLocationInfo.storeAreaType,'store_area_type',"")}&nbsp;${areaDetail.goodsLocationInfo.storeAreaName}
															&nbsp;/&nbsp;${areaDetail.goodsLocationInfo.rfid}
															&nbsp;/&nbsp;<fmt:formatDate value="${areaDetail.goodsLocationInfo.inStoreDate}" pattern="yyyy-MM-dd"/>
														</B>
													</font>
												</c:if>	
											</td>
										</tr>
									</c:forEach>
									</c:if>
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
