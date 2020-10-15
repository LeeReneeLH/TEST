<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv" >
		<table
			style="width:100%; border: 1px black solid; border-collapse: collapse; border: none; font-size:12px;"
			border="1">
			<tr>
				<td style="border: none;"></td>
				<td colspan="3" style="text-align: left; border: none;">
					<%-- <div style="float: left;">
						<span style="text-decoration:underline"><font size="4">
						<!-- 聚龙股份 -->
						<b><spring:message code="clear.public.julong" /></b></font>
						<!-- 现钞处理中心 -->
						<font size="0.5"><spring:message code="clear.public.cashHandling" /></font></span>
					</div> --%>
					<div style="float: right;">
						<!-- 备用金存取凭证 -->
						<h1 style="color:black;"><spring:message code="clear.public.provision" /></h1>
					</div>
				</td>
				<td  style="text-align: right; border: none; font-size:12px;">
					<!-- 流水号 -->
					No:${bankPayInfo.inNo}<br>${fns:getDate('yyyy年MM月dd日')}
				</td>
			</tr>
			<tr>
				<td rowspan="5"
					style="letter-spacing: 1px; vertical-align: bottom; width: 20px;border:none;">
					<!-- <div
						style="Writing-mode: tb-rl; Text-align: left; transform: rotate(180deg);">QG/JLZZ430014A4A</div> -->
				</td>
				<td style="text-align: center; width: 90px; border: 1px solid;">
				<!-- 交款单位 -->
				<font size="2"><b><spring:message code="clear.public.itemUnit" /></b></font></td>
				<td style="text-align: left; width: 300px; border: 1px solid;">${bankPayInfo.rOffice.name}</td>
				<td style="text-align: center; width: 90px; border: 1px solid;">
				<!-- 收款单位 -->
				<font size="2"><b><spring:message code="clear.public.receivingUnit" /></b></font></td>
				<td style="text-align: left; width: 340px; border: 1px solid;"
					>${user}</td>
				<td rowspan="5" style="border:none;">
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
					<!-- 收 -->
					<spring:message code="clear.public.shou" /><br>
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
					<!-- 交 -->
					<spring:message code="clear.public.jiao" /><br>
					<!-- 款 -->
					<spring:message code="clear.public.kuan" /><br>
					<!-- 单 -->
					<spring:message code="clear.public.dan" /><br>
					<!-- 位 -->
					<spring:message code="clear.public.wei" />
				</td>
			</tr>
			<tr>
				<td style="text-align: center; width: 90px; border: 1px solid; ">
				<!-- 收支类型 -->
				<font size="2"><b><spring:message code="clear.public.inOrOut" /></b></font></td>
				<!-- 收入 -->
				<td style="text-align: left; width: 300px; border: 1px solid;"><spring:message code="clear.public.inCome" /></td>
				<td style="text-align: center; width: 90px; border: 1px solid;">
				<!-- 总金额 -->
				<font size="2"><b><spring:message code="clear.task.totalAmt" /></b></font></td>
				<td style="text-align: right; width: 340px; border: 1px solid;"
					><fmt:formatNumber value="${bankPayInfo.inAmount}"
						 pattern="￥ #,##0.00" /></td>
			</tr>
			<tr>
				<td style="text-align: left; width: 365px; border: 1px solid; word-wrap:break-word;word-break:break-all;"
					rowspan="2" colspan="2" valign="top">
				<!-- 备注 -->
				<font size="2"><b><spring:message code="common.remark" />:</b></font><br><font size="2">${bankPayInfo.remarks}</font></td>
				<td style="text-align: center; width: 90px; border: 1px solid;">
				<!--总金额大写  -->
				<font size="2"><b><spring:message code="clear.public.money.upperCase" /></b></font></td>
				<td style="text-align: center; width: 340px; border: 1px solid;;"
					>${strBigAmount}</td>
			</tr>
			<tr>
				<td style="text-align: right;border: 1px solid; border-right:none;"
				 	valign="bottom"></td>
				<td style="text-align: right; height: 300px; border: 1px solid; border-left:none;"
					  valign="bottom">
					  <!-- 盖现金清讫章及经手人签章 -->
					  <span><spring:message code="clear.provision.text" />&nbsp;&nbsp;&nbsp;</span><br><br></td>
			</tr>
		</table>
		<table style="width:100%; float: left; margin-left: 20px;">
			<tr>
				<!-- 制单 -->
				<td style="width: 55px;"><b><spring:message code="common.voucherMaking" />：</b></td>
				<td style="width: 180px;">${bankPayInfo.createName}</td>
				<!-- 收款 -->
				<td style="width: 55px;"><b><spring:message code="clear.public.receipt" />：</b></td>
				<td style="width: 150px;">${bankPayInfo.transManName}</td>
				<!-- 复核 -->
				<td style="width: 55px;"><b><spring:message code="common.voucherVerifying" />：</b></td>
				<td style="width: 150px;">${bankPayInfo.checkManName}</td>
				<!-- 交款 -->
				<td style="width: 55px;"><b><spring:message code="clear.public.payment" />：</b></td>
				<td style="width: 150px;">${bankPayInfo.bankManNameA},${bankPayInfo.bankManNameB}</td>
			</tr>
		</table>
	</div>
</body>
</html>