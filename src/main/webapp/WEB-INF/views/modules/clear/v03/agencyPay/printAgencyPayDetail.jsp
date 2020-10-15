<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
	});
</script>
</head>
<body>
	<div id="printDiv">
		<c:forEach var="i" begin="1" end="${size}" varStatus="status">
			<div style="page-break-after: always;">
				<table
					style=" width:100%; border: 1px black solid; border-collapse: collapse; border: none; font-size:12px; line-height:18px;"
					border="1" >
					<tr>
						<td style="border: none;"></td>
						<td colspan="2" style="text-align: left; border: none;"><%-- <span
							style="text-decoration: underline">
							<font size="4">
								<!-- 聚龙股份 --> 
								<b><spring:message code="clear.public.julong" /></b>
							</font> 
							<!-- 现钞处理中心 --> 
							<font size="0.5"><spring:message code="clear.public.cashHandling" /></font></span> --%>
						</td>
						<td colspan="3" style="border: none;">
							<!-- 委托缴款凭证 -->
							<h1 style="color:black;"><spring:message code="clear.agencyPay.printName" /></h1>
						</td>
						<td colspan="3" style="text-align: right; border: none; font-size:12px;">
							<!-- 流水号 -->
							No:${clOutMain.outNo}<br>${fns:getDate('yyyy年MM月dd日')}
						</td>
					</tr>
					<tr>
						<td rowspan="14"
							style="letter-spacing: 1px; vertical-align: bottom; width:20px; border:none;">
							<div
								style="Writing-mode: tb-rl; Text-align: right; transform: rotate(180deg);">QG/JLZZ430014A</div>
						</td>
						<!--收款单位  -->
						<td style="text-align: center; width: 80px; height:20px; border: 1px solid;">
							<font size="2"><b><spring:message code="clear.public.receivingUnit" /></b></font></td>
						<td style="text-align: left; width: 250px; border: 1px solid;">${users}</td>
						<!-- 委托方 -->
						<td style="text-align: center; width: 125px; border: 1px solid;">
							<font size="2"><b><spring:message code="clear.public.entrusted" /></b></font></td>
						<td style="text-align: left; width: 448px; border: 1px solid;"
							colspan="5">${user}</td>
						<td rowspan="14" style="border: none;">
							<!-- 第 -->
							<spring:message code="clear.public.di" /><br>
							<!-- 一 -->
							<spring:message code="clear.public.yi" /><br>
							<!-- 联 -->
							<spring:message code="clear.public.lian" /><br>
							<!-- ︵ -->
							<spring:message code="clear.public.shangKuoHao" /><br>
							<!-- 白 -->
							<spring:message code="clear.public.bai" /><br> 
							<!-- ︶ -->
							<spring:message code="clear.public.xiaKuoHao" /><br>
							<!-- 委 -->
							<spring:message code="clear.public.weiWei" /><br>
							<!-- 托 -->
							<spring:message code="clear.public.tuo" /><br>
							<!-- 方 -->
							<spring:message code="clear.public.fang" /><br>
							<!-- 第 -->
							<spring:message code="clear.public.di" /><br>
							<!-- 二 -->
							<spring:message code="clear.public.er" /><br>
							<!-- 联 -->
							<spring:message code="clear.public.lian" /><br>
							<!-- ︵ -->
							<spring:message code="clear.public.shangKuoHao" /> <br>
							<!-- 彩 -->
							<spring:message code="clear.public.cai" /><br> 
							<!-- ︶ -->
							<spring:message code="clear.public.xiaKuoHao" /><br>
							<!-- 收 -->
							<spring:message code="clear.public.shou" /><br>
							<!-- 款 -->
							<spring:message code="clear.public.kuan" /><br>
							<!-- 单 -->
							<spring:message code="clear.public.dan" /><br>
							<!-- 位 -->
							<spring:message code="clear.public.wei" />
						</td>
					</tr>
					<tr>
						<td style="text-align: center; width: 70px; border: 1px solid;">
						<!-- 委托方 -->
							<font size="2"><b><spring:message code="clear.public.client" /></b></font></td>
						<td style="text-align: left; width: 250px; height:20px; border: 1px solid;">${clOutMain.rOffice.name}</td>
						<!-- 类别 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:120px; height:20px;">
							<spring:message code="common.classification" /></td>
						<!-- 面值 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:80px;">
							<spring:message code="common.denomination" /></td>
						<!-- 捆 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:70px;">
							<spring:message code="clear.public.bundle" /></td>
						<!-- 把 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:60px;">
							<spring:message code="clear.public.the" /></td>
						<!-- 张 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:60px;">
							<spring:message code="clear.public.zhang" /></td>
						<!-- 金额 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:150px;">
							<spring:message code="clear.public.moneyFormat" /></td>
					</tr>
					<c:forEach items="${clOutDetailList}" var="clOutMain"  begin="${0+10*(status.index-1)}" end="${9+10*(status.index-1)}" varStatus="statuss">
						<c:if test="${clOutMain.countWzq!=null && clOutMain.countWzq!='0' && clOutMain.countWzq!=''}">
							<tr>
								<c:if test="${statuss.first}">
									<td colspan="2" rowspan="10" style="width:330px; border: 1px solid; height:15px; border-bottom:none;" valign = "top">
										<!-- 摘要 -->
										<b><spring:message code="clear.public.abstracts" />：</b>
										<!--现钞处理中心受委托方委托将凭证记载款项上缴收款单位-->
										<font size="2"><spring:message code="clear.agencyPay.abstractsText" /></font><br>
									</td>
								</c:if>
								<!-- 完整券 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									<spring:message code="clear.public.Wzq" /></td>
								<!-- 面值 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${sto:getDenLabel(clOutMain.currency,clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${clOutMain.countWzq}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 金额 -->
								<td style="border: 1px black solid; border-top: none; text-align: right; width:150px; height:9px; font-size:10px;"><fmt:formatNumber
									value="${clOutMain.moneyWzq}" pattern="￥ #,##0.00" /></td>
							</tr>
						</c:if>
						<c:if test="${clOutMain.countCsq!=null && clOutMain.countCsq!='0' && clOutMain.countCsq!=''}">
							<tr>
								<c:if test="${clOutMain.countWzq==null || clOutMain.countWzq=='0' || clOutMain.countWzq==''}">
									<c:if test="${statuss.first}">
										<td colspan="2" rowspan="10" style="width:330px; border: 1px solid; height:15px; border-bottom:none;" valign = "top">
											<!-- 摘要 -->
											<b><spring:message code="clear.public.abstracts" />：</b>
											<!--现钞处理中心受委托方委托将凭证记载款项上缴收款单位-->
											<font size="2"><spring:message code="clear.agencyPay.abstractsText" /></font><br>
										</td>
									</c:if>
								</c:if>
								<!-- 残损券 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									<spring:message code="clear.public.Csq" /></td>
								<!-- 面值 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${sto:getDenLabel(clOutMain.currency,clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${clOutMain.countCsq}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 金额 -->
								<td style="border: 1px black solid; border-top: none; text-align: right; height:9px; width:150px; font-size:10px;"><fmt:formatNumber
									value="${clOutMain.moneyCsq}" pattern="￥ #,##0.00" /></td>
							</tr>
						</c:if>
						<c:if test="${clOutMain.countYqf!=null && clOutMain.countYqf!='0' && clOutMain.countYqf!=''}">
							<tr>
								<td style="border:none; border-left:1px solid;"></td>
								<td style="border:none;"></td>
								<!-- 已清分-->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									<spring:message code="clear.public.haveClear" /></td>
								<!-- 面值 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									${sto:getDenLabel(clOutMain.currency,clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${clOutMain.countYqf}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 金额 -->
								<td style="border: 1px black solid; border-top: none; text-align: right; width:150px; height:9px; font-size:10px;">
									<fmt:formatNumber value="${clOutMain.moneyYqf}" pattern="#,###.00" /></td>
							</tr>
						</c:if>
						<c:if test="${clOutMain.countAtm!=null && clOutMain.countAtm!='0' && clOutMain.countAtm!=''}">
							<tr>
								<c:if
									test="${clOutMain.countWzq==null || clOutMain.countWzq=='0' || clOutMain.countWzq==''||clOutMain.countCsq==null || clOutMain.countCsq=='0' || clOutMain.countCsq==''}">
									<c:if test="${statuss.first}">
										<td colspan="2" rowspan="10"
											style="width: 330px; border: 1px solid; height: 15px; border-bottom: none;"
											valign="top">
											<!-- 摘要 --> <b><spring:message
													code="clear.public.abstracts" />：</b> <!--现钞处理中心受委托方委托将凭证记载款项上缴收款单位-->
											<font size="2"><spring:message
													code="clear.agencyPay.abstractsText" /></font><br>
										</td>
									</c:if>
								</c:if>
								<!-- ATM -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									<spring:message code="clear.public.ATM" /></td>
								<!-- 面值 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">
									${sto:getDenLabel(clOutMain.currency,clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;">${clOutMain.countAtm}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none; text-align: center; height:9px; font-size:10px;"></td>
								<!-- 金额 -->
								<td style="border: 1px black solid; border-top: none; text-align: right;  width:150px; height:9px; font-size:10px;">
									<fmt:formatNumber value="${clOutMain.moneyAtm}" pattern="￥ #,##0.00" /></td>
							</tr>
						</c:if>
					</c:forEach>
					<!-- 判断是否是最后一次循环 -->
					<c:if test="${status.last}">
						<!-- 判断数据是否达到10行如果不足进行补充 -->
						<c:if test="${(fn:length(clOutDetailList)%10)!= 0}">
							<c:forEach var="i" begin="1" end="${10-(fn:length(clOutDetailList)%10)}">
								<tr>
									<td style="border: 1px solid; height:20px;"></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
								</tr>
							</c:forEach>
						</c:if>
					</c:if>
					<tr style="height:20px;">
						<td colspan="2" style="border:none; border-left:1px solid; border-right:1px solid; width:330px;"  valign = "bottom">
							<!-- 上述款项已交接完毕 -->
							<span style="margin-left: 75px"><spring:message code="clear.public.abstractsTextOne" /></span><br>
							<!-- 盖现金交接专用章 -->
							<span style="float: right"><spring:message code="clear.public.abstractsTextTwo" /></span>
						</td>
						<!-- 合计 -->
						<td style="text-align:center; font-weight: bold;"><spring:message code="clear.public.totUp" /></td>
						<td colspan="5" style="text-align: right; border: 1px solid;"><fmt:formatNumber
							value="${clOutMain.outAmount}" pattern="￥ #,##0.00" /></td>
					</tr>
					<tr>
						<td colspan="2" style="border: 1px solid; border-top:none; line-height:25px;"  valign = "bottom">
							<!-- 交出方签章 -->
							<b><spring:message code="clear.public.outSignature" />：</b> 
							<!-- 委托签章 -->
							<br><b><spring:message code="clear.public.entrustSignature" />：</b>
						</td>
						<!-- 人民币(大写) -->
						<td style="height:20px; border: 1px solid; font-weight: bold; text-align: center; font-size:12px;"><spring:message code="common.rmb.upperCase" /></td>
						<td colspan="5" style="text-align: center; border: 1px solid;">${strBigAmount}</td>
					</tr>
				</table>
				<table style="float: left; margin-left: 20px; width:90%; font-size:12px;">
								<tr>
									<!-- 制单 -->
									<td><b><spring:message code="common.voucherMaking" />：</b></td>
									<td>${clOutMain.createName}</td>
									<!-- 中心交接员 -->
									<td><b><spring:message code="clear.public.centerTransName" />：</b></td>
									<td>${clOutMain.transManName}</td>
									<!-- 中心复核员 -->
									<td><b><spring:message code="clear.public.centerCheckName" />：</b></td>
									<td>${clOutMain.checkManName}</td>
									<td>第${status.count}/${size}页</td>
								</tr>
							</table>
			</div>
		</c:forEach>
	</div>
</body>
</html>