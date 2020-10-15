<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default" />
</head>
<body style="margin: 0;padding: 10px; font-size:10px;line-height:25px;font-family: Arial, Helvetica, sans-serif '微软雅黑';">
	<div id="printDiv">
		<div style="text-align: center;margin-top: 20px;margin-bottom: 20px;">
			<%-- 中国人民银行现金交款单 --%>
			<label style="clear:both;font-size:20px;width:100%;text-align:center;"><spring:message code="allocation.handinFinish.cashSinglePayment" /></label>
		</div>
		<div style="float: left;width: 98%;position: relative;height:20px;">
			
			<%-- 交款单位：（公章） --%>
			<div style="width: 20%;position: absolute;left: 0px;top: 0px;"><spring:message code="common.paymentBank.officialSeal" />
				<c:if test="${!empty officeStamperInfoId }">
					<span style="width:140px;height:100px; position:absolute;left:10px;z-index:100;top:-50px">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showOfficeStamperImage?officeStamperInfoId=${officeStamperInfoId}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<%-- yyyy年MM月dd日 --%>
			<div style="width: 50%;text-align: center;position: absolute;left: 25%;top: 0;">${fns:getDate('yyyy年MM月dd日')}</div>
			<%-- 单位：元 --%>
			<div style="width: 20%;	text-align: right;position: absolute;right:5%;	top: 0px;"><spring:message code="common.units.yuan.noBracket" /></div>
		</div>
		<div class="float: left;width: 100%;position: relative;">

			<table style="width:95%;float:left;border:1px #666 solid;border-collapse:collapse;padding:5px;">
				<colgroup>
					<col width="12%">
					<col width="30%">
					<col width="25%">
					<col width="28%">
				</colgroup>
				<tr>
					<%-- 摘要 --%>
					<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;"><spring:message code="common.remark2" /></td>
					<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">${remark}</td>
					<%-- 券别 --%>
					<td style="text-align:center;border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;"><spring:message code="common.notes" /></td>
					<%-- 金额 --%>
					<td style="text-align:center;border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;"><spring:message code="common.amount" /></td>
				</tr>
				<tr>
					<td colspan="2" rowspan="10" style="position:relative;border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 上款已如数交存 --%>
						<div style="position:absolute;top:10px;left:10px;"><spring:message code="allocation.handinFinish.confirmMessage" /></div>
							<c:if test="${!empty pbocOfficeStamperInfoId }">
								<span style="width:140px;height:100px; position:absolute;left:10px;z-index:100;top:35px">
									<img src="${ctx}/allocation/v02/pbocHandinApproval/showPbocOfficeStamperImage?officeStamperInfoId=${pbocOfficeStamperInfoId}" 
										style="width:100%;height:100%;"/>
								</span>
							</c:if>
						<%-- 现金收讫章及管库员章 --%>
						<div style="position:absolute;bottom:70px;left:10px;"><spring:message code="allocation.handinFinish.paymentReceivedAndGodownKeeper.seal" /></div>
						<c:if test="${!empty managerUserId }">
							<%-- 授权通过 --%>
							<div style="position:absolute;bottom:20px;left:40px;"><font size="6"><b><spring:message code="common.authorization.passed" /></b></font></div>
						</c:if>
						<c:if test="${handoverUserSize >= 1 }">
							<span style="width:50px;height:20px;position:absolute;bottom:35px;left:30px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId1}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 2 }">
							<span style="width:50px;height:20px;position:absolute;bottom:35px;left:100px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId2}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 3 }">
							<span style="width:50px;height:20px;position:absolute;bottom:5px;left:30px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId3}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 4 }">
							<span style="width:50px;height:20px;position:absolute;bottom:5px;left:100px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId4}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
					</td>
				</tr>
				<c:forEach items="${printDataList}"	var="item" varStatus="status">
					<tr>
						<c:if test="${item.goodsName != ''}">
							<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">${item.goodsName}</td>
							<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;text-align: right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
						</c:if>
						<c:if test="${item.goodsName == ''}">
							<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">&nbsp;</td>
							<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">&nbsp;</td>
						</c:if>
					</tr>
				</c:forEach>
				<tr>
					<%-- 合计 --%>
					<td style="text-align:center;border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;"><spring:message code="common.total" /></td>
					<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;text-align: right;">
						<fmt:formatNumber value="${allMoney}" pattern="#,##0.00#" />
					</td>
				</tr>
				<tr>
					<%-- 人民币（大写） --%>
					<td style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;"><spring:message code="common.rmb.upperCase" /></td>
					<td colspan="3" style="border:1px #666 solid;border-collapse:collapse;padding:5px;height: 25px;">
						${allMoneyBig}
					</td>
				</tr>
			</table>
			<%-- 第一联 回执 --%>
			<div style="width: 20px;float:left;vertical-align:middle;text-align:center;writing-mode:lr-tb;margin-top:100px;"><spring:message code="allocation.handinFinish.printFirstPage" /></div>
		</div>

		<div style="float: left;width: 100%;position: relative;height:25px;">
			<%-- 业务主管 --%>
			<div style="width: 20%;	position: absolute;	left: 0px;top: 0px;"><span style="float:left;"><spring:message code="common.businessExecutive" />：</span>
				<c:if test="${!empty businessMgr }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${businessMgr}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<%-- 复核 --%>
			<div style="width: 50%;text-align: center;position: absolute;left: 40%;top: 0;"><span style="float:left;"><spring:message code="common.voucherVerifying" />：</span>
				<c:if test="${!empty rechecker }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${rechecker}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<%-- 制单 --%>
			<div style="width: 20%;position: absolute;right:5%;top: 0px;"><span style="float:left;"><spring:message code="common.voucherMaking" />：</span>
				<c:if test="${!empty makeTable }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${makeTable}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
		</div>
		<div style="text-align: center;margin-top: 30px;">
			<label style="width:100%;text-align:center;">
				--------------------------------------------------------------------------------------------------------------------------
			</label>
		</div>
		<%-- 第二页 --%>
		<div style="text-align: center;margin-top: 30px;margin-bottom: 20px;">
			<%-- 中国人民银行现金交款单 --%>
			<label style="clear:both;font-size:20px;width:100%;text-align:center;color: #dc2d2d !important;">
				<spring:message code="allocation.handinFinish.cashSinglePayment" />
			</label>
		</div>
		<div style="float: left;width: 98%;position: relative;height:20px;">
			<%-- 交款单位：（公章） --%>
			<div style="width: 20%;position: absolute;left: 0px;top: 0px; color: #dc2d2d !important;"><spring:message code="common.paymentBank.officialSeal" />
				<c:if test="${!empty officeStamperInfoId }">
					<span style="width:140px;height:100px; position:absolute;left:10px;z-index:100;top:-50px">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showOfficeStamperImage?officeStamperInfoId=${officeStamperInfoId}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<div style="width: 50%;text-align: center;position: absolute;left: 25%;top: 0;">
				${fns:getDate('yyyy')}
				<label style="color: #dc2d2d !important;"><spring:message code="common.year" /></label>
				${fns:getDate('MM')}
				<label style="color: #dc2d2d !important;"><spring:message code="common.month" /></label>
				${fns:getDate('dd')}
				<label style="color: #dc2d2d !important;"><spring:message code="common.day" /></label>
			</div>
			<div style="width: 20%;	text-align: right;position: absolute;right:5%;	top: 0px;">
				<label style="color: #dc2d2d !important;"><spring:message code="common.units.yuan.noBracket" /></label>
			</div>
		</div>
		<div class="float: left;width: 100%;position: relative;">

			<table style="width:95%;float:left;border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;">
				<colgroup>
					<col width="12%">
					<col width="30%">
					<col width="25%">
					<col width="28%">
				</colgroup>
				<tr>
					<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 摘要 --%>
						<label style="color: #dc2d2d !important;"><spring:message code="common.remark2" /></label>
					</td>
					<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height:25px;">${remark}</td>
					<td style="text-align:center;border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 券别 --%>
						<label style="color: #dc2d2d !important;"><spring:message code="common.notes" /></label>
					</td>
					<td style="text-align:center;border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 金额 --%>
						<label style="color: #dc2d2d !important;"><spring:message code="common.amount" /></label>
					</td>
				</tr>
				<tr>
					<td colspan="2" rowspan="10" style="position:relative;border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<div style="position:absolute;top:10px;left:10px;">
							<%-- 上款已如数交存 --%>
							<span style="color:#dc2d2d !important"><spring:message code="allocation.handinFinish.confirmMessage" /></span>
						</div>
						<c:if test="${!empty pbocOfficeStamperInfoId }">
							<span style="width:140px;height:100px; position:absolute;left:10px;z-index:100;top:35px">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showPbocOfficeStamperImage?officeStamperInfoId=${pbocOfficeStamperInfoId}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<div style="position:absolute;bottom:70px;left:10px;">
							<%-- 现金收讫章及管库员章 --%>
							<label style="color: #dc2d2d !important;"><spring:message code="allocation.handinFinish.paymentReceivedAndGodownKeeper.seal" /></label>
						</div>
						<c:if test="${!empty managerUserId }">
							<%-- 授权通过 --%>
							<div style="position:absolute;bottom:20px;left:40px;"><font size="6" ><b><spring:message code="common.authorization.passed" /></b></font></div>
						</c:if>
						<c:if test="${handoverUserSize >= 1 }">
							<span style="width:50px;height:20px;position:absolute;bottom:35px;left:30px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId1}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 2 }">
							<span style="width:50px;height:20px;position:absolute;bottom:35px;left:100px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId2}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 3 }">
							<span style="width:50px;height:20px;position:absolute;bottom:5px;left:30px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId3}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
						<c:if test="${handoverUserSize >= 4 }">
							<span style="width:50px;height:20px;position:absolute;bottom:5px;left:100px;">
								<img src="${ctx}/allocation/v02/pbocHandinApproval/showStamperImageByUserId?userId=${storeMgrId4}" 
									style="width:100%;height:100%;"/>
							</span>
						</c:if>
					</td>
				</tr>
				<c:forEach items="${printDataList}"	var="item" varStatus="status">
					<tr>
						<c:if test="${item.goodsName != ''}">
							<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">${item.goodsName}</td>
							<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;text-align: right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
						</c:if>
						<c:if test="${item.goodsName == ''}">
							<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">&nbsp;</td>
							<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">&nbsp;</td>
						</c:if>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align:center;border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 合计 --%>
						<label style="color: #dc2d2d !important;"><spring:message code="common.total" /></label>
					</td>
					<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;text-align: right;">
						<fmt:formatNumber value="${allMoney}" pattern="#,##0.00#" />
					</td>
				</tr>
				<tr>
					<td style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						<%-- 人民币（大写） --%>
						<label style="color: #dc2d2d !important;"><spring:message code="common.rmb.upperCase" /></label>
					</td>
					<td colspan="3" style="border:1px #dc2d2d solid;border-collapse:collapse;padding:5px;height: 25px;">
						${allMoneyBig}
					</td>
				</tr>
			</table>
			<div style="width: 20px;float:left;vertical-align:middle;text-align:center;writing-mode:lr-tb;margin-top:100px;">
				<%-- 第二联 存根 --%>
				<label style="color: #dc2d2d !important;"><spring:message code="allocation.handinFinish.printSecondPage" /></label>
			</div>
		</div>

		<div style="float: left;width: 100%;position: relative;height:25px;">
			<div style="width: 20%;	position: absolute;	left: 0px;top: 0px;"><span style="float:left;">
				<%-- 业务主管 --%>
				<label style="color: #dc2d2d !important;"><spring:message code="common.businessExecutive" />：</label>
			</span>
				<c:if test="${!empty businessMgr }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${businessMgr}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<div style="width: 50%;text-align: center;position: absolute;left: 40%;top: 0;">
				<span style="float:left;">
					<%-- 复核 --%>
					<label style="color: #dc2d2d !important;"><spring:message code="common.voucherVerifying" />：</label>
				</span>
				<c:if test="${!empty rechecker }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${rechecker}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
			<div style="width: 20%;position: absolute;right:5%;top: 0px;">
				<span style="float:left;">
					<%-- 制单 --%>
					<label style="color: #dc2d2d !important;"><spring:message code="common.voucherMaking" />：</label>
				</span>
				<c:if test="${!empty makeTable }">
					<span style="width:50px;height:20px;float:left;">
						<img src="${ctx}/allocation/v02/pbocHandinApproval/showEscortStamperImage?escortId=${makeTable}" 
							style="width:100%;height:100%;"/>
					</span>
				</c:if>
			</div>
		</div>
	</div>
</body>
</html>
