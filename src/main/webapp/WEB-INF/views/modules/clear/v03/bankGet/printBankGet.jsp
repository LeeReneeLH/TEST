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
				<table id="contentTable"
					style="width: 100%; border: 1px black solid; border-collapse: collapse; border: none; font-size:12px; line-height:18px;"
					border="1">
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
							<!-- 现金取款凭证 -->
							<h1 style="color: black;">
								<spring:message code="clear.bankGet.printName" />
							</h1>
						</td>
						<td colspan="3" style="text-align: right; border: none; font-size:12px;">
							<!-- 流水号 -->
							No:${clOutMain.outNo}<br>${fns:getDate('yyyy年MM月dd日')}
						</td>
					</tr>
					<tr>
						<td rowspan="14"
							style="letter-spacing: 1px; width: 20px; border: none;"
							valign="bottom">
							<div
								style="Writing-mode: tb-rl; Text-align: right; transform: rotate(180deg);">QG/JLZZ430014A</div>
						</td>
						<!--收款单位  -->
						<td
							style="text-align: center; width: 90px; height: 20px; border: 1px solid;">
							<font size="2"><b><spring:message
										code="clear.public.receivingUnit" /></b></font>
						</td>
						<td style="text-align: left; width: 270px; border: 1px solid;">${clOutMain.rOffice.name}</td>
						<!-- 付款单位 -->
						<td style="text-align: center; border: 1px solid;">
							<font size="2"><b><spring:message
										code="clear.public.payUnit" /></b></font>
						</td>
						<td style="text-align: left; border: 1px solid;"
							colspan="5">${officeName}</td>
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
							<spring:message
								code="clear.public.xiaKuoHao" /><br> 
							<!-- 付 --> 
							<spring:message
								code="clear.public.fu" /><br> 
							<!-- 款 --> 
							<spring:message code="clear.public.kuan" /><br> 
							<!-- 单 --> 
							<spring:message code="clear.public.dan" /><br> 
							<!-- 位 --> 
							<spring:message code="clear.public.wei" /><br> 
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
						<td id="tdHeight" colspan="2"
							style="width: 340px; border: 1px solid; border-bottom: none; text-align: left;">
							<!-- 摘要 --> <b><spring:message code="clear.public.abstracts" />：</b>
							<!-- 现钞处理中心交出款项给收款单位  --> <spring:message
								code="clear.bankGet.abstractsText" /><br>
						</td>
						<!-- 类别 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold; width: 120px; height: 15px;">
							<spring:message code="common.classification" />
						</td>
						<!-- 面值 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold; width: 80px; height: 15px;">
							<spring:message code="common.denomination" />
						</td>
						<!-- 捆 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold; width: 30px;">
							<spring:message code="clear.public.bundle" />
						</td>
						<!-- 把 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold; width: 30px;">
							<spring:message code="clear.public.the" />
						</td>
						<!-- 张 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold; width: 30px;">
							<spring:message code="clear.public.zhang" />
						</td>
						<!-- 金额 -->
						<td
							style="text-align: center; border: 1px solid; font-weight: bold;">
							<spring:message code="clear.public.moneyFormat" />
						</td>
					</tr>
					<c:forEach items="${clOutDetailList}" var="clOutMain"
						begin="${0+10*(status.index-1)}" end="${9+10*(status.index-1)}">
						<c:if
							test="${clOutMain.countYqf != null && clOutMain.countYqf != '0' && clOutMain.countYqf != ''}">
							<tr>
								<td style="border:none; border-left:1px solid;"></td>
								<td style="border:none;"></td>
								<!-- 已清分-->
								<td
									style="border: 1px black solid; text-align: center; border-top: none; height: 13px;">
									<spring:message code="clear.public.haveClear" />
								</td>
								<!-- 面值 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">
									${sto:getDenLabel(clOutMain.currency, clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">${clOutMain.countYqf}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 金额 -->
								<td
									style="width: 150px; border: 1px solid; text-align: right; border-top: none;">
									<fmt:formatNumber value="${clOutMain.moneyYqf}"
										 pattern="￥ #,##0.00" />
								</td>
							</tr>
						</c:if>
						<c:if
							test="${clOutMain.countDqf != null && clOutMain.countDqf != '0' && clOutMain.countDqf != ''}">
							<tr>
								<td style="border:none; border-left:1px solid;"></td>
								<td style="border:none;"></td>
								<!-- 未清分 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none; height: 13px; ">
									<spring:message code="clear.public.noneClear" />
								</td>
								<!-- 面值 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">${sto:getDenLabel(clOutMain.currency, clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">${clOutMain.countDqf}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 金额 -->
								<td
									style="border: 1px solid; border-top: none; width: 150px; text-align: right;">
									<fmt:formatNumber value="${clOutMain.moneyDqf}"
										 pattern="￥ #,##0.00" />
								</td>
							</tr>
						</c:if>
						<c:if
							test="${clOutMain.countAtm != null && clOutMain.countAtm != '0' && clOutMain.countAtm != ''}">
							<tr>
								<td style="border:none; border-left:1px solid;"></td>
								<td style="border:none;"></td>
								<!-- ATM -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none; height: 13px;">
									<spring:message code="clear.public.ATM" />
								</td>
								<!-- 面值 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">
									${sto:getDenLabel(clOutMain.currency, clOutMain.denomination, "")}</td>
								<!-- 捆 -->
								<td
									style="border: 1px black solid; text-align: center; border-top: none;">${clOutMain.countAtm}</td>
								<!-- 把 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 张 -->
								<td style="border: 1px black solid; border-top: none;"></td>
								<!-- 金额 -->
								<td
									style="width: 150px; border: 1px solid; text-align: right; border-top: none;">
									<fmt:formatNumber value="${clOutMain.moneyAtm}"
										 pattern="￥ #,##0.00" />
								</td>
							</tr>
						</c:if>
					</c:forEach>
					<!-- 判断是否是最后一次循环 -->
					<c:if test="${status.last}">
						<!-- 判断数据是否达到10行如果不足进行补充 -->
						<c:if test="${((fn:length(clOutDetailList))%10)!= 0}">
							<c:forEach var="i" begin="1"
								end="${10-((fn:length(clOutDetailList))%10)}">
								<tr>
									<td style="border:none; border-left:1px solid;"></td>
									<td style="border:none;"></td>
									<td style="border: 1px solid; border-top:none; height: 13px;"></td>
									<td style="border: 1px solid; border-top:none;"></td>
									<td style="border: 1px solid; border-top:none;"></td>
									<td style="border: 1px solid; border-top:none;"></td>
									<td style="border: 1px solid; border-top:none;"></td>
									<td style="border: 1px solid; border-top:none;"></td>
								</tr>
							</c:forEach>
						</c:if>
					</c:if>
					<tr>
						<td colspan="2"  style="width:330px; border:none; border-left:1px solid; text-align: left;" valign = "bottom">
							<!-- 上述款项已交接完毕 -->
							<span style="margin-left: 75px"><spring:message code="clear.public.abstractsTextOne" /></span> <br>
							<!-- 盖现金交接专用章 -->
							 <span style="float: right"><spring:message code="clear.public.abstractsTextTwo" /></span>			
						</td>
						<!-- 合计 -->
						<td
							style="text-align: center; height: 20px; font-weight: bold; border: 1px black solid;">
							<spring:message code="clear.public.totUp" />
						</td>
						<td colspan="5" style="text-align: right; border: 1px solid;">
							<fmt:formatNumber value="${clOutMain.outAmount}"
								 pattern="￥ #,##0.00" />
						</td>
					</tr>
					<tr>
						<td colspan="2"  style="width:330px; border:1px solid; border-top:none; text-align: left; line-height:25px;" valign = "bottom">
							<!-- 银行方签章 -->
							<b><spring:message code="clear.public.bankSignature" />：</b> 
							<!-- 中心方签章 -->
							<br><b><spring:message code="clear.public.centerSignature" />：</b>					
						</td>
						<!-- 人民币(大写) -->
						<td
							style="height: 20px; text-align: center; font-weight: bold; border: 1px black solid;">
							<spring:message code="common.rmb.upperCase" />
						</td>
						<td colspan="5" style="text-align: center; border: 1px solid;">${sto:getUpperAmount(clOutMain.outAmount)}</td>
					</tr>
				</table>
				<table style="float: left; margin-left: 20px; width:90%; border:none; font-size:12px;">
								<tr>
									<!-- 制单 -->
									<td><b><spring:message code="common.voucherMaking" />：</b></td>
									<td>${clOutMain.createName}</td>
									<!-- 中心交接员 -->
									<td><b><spring:message code="clear.public.centerTransName" />：</b></td>
									<td>${clOutMain.transManName}</td>
									<!-- 中心复核员 -->
									<td><b><spring:message code="clear.public.centerCheckName" />：</b></td>
									<td style="border:none;">${clOutMain.checkManName}</td>
									<td style="text-align:right; border:none;">第${status.count}/${size}页</td>
								</tr>
			</table>
			</div>
		</c:forEach>
	</div>
</body>
</html>