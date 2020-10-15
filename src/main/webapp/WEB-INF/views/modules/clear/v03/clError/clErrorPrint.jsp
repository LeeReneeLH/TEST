<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">	
</script>
<style type="text/css">
 	tr{height:44px;}
 	.tda{
 	width:85px;text-align: center;
 	}
 	.tdb{
 	width:170px;text-align: center;
 	}
 	.tdc{
 	width:255px;text-align: center;
 	}
 	td{border:1px solid black;}
</style>
</head>
<body>
	<div id="printDiv">
		<%-- <div style="margin-left:35px;float:left;width:170px;margin-top:20px;text-align:left;font-size:20px;"><u><label><b>聚龙股份</b></label><label style="font-size:8px">现钞处理中心</label></u></div>
		<div style="float:left;width:420px; height:60px;margin-top:20px;text-align:center;font-size:30px;"><label><b>差 错（假币）款 凭 证</b></label></div>
		<div style="float:left;width:235px;text-align:center;font-size:15px;margin-top:20px;text-align:right;"><label>No:${clErrorInfo.errorNo}</label></div> --%>
			<table style="float:left;margin-left:20px; border-collapse:collapse; border:none; border-right:none; font-size:12px;  line-height:15px;" border="1" >
				<tr>
						<td colspan="2" style="text-align: left; border: none;"><%-- <span
							style="text-decoration: underline">
							<font size="4">
								<!-- 聚龙股份 --> 
								<b><spring:message code="clear.public.julong" /></b>
							</font> 
							<!-- 现钞处理中心 --> 
							<font size="0.5"><spring:message code="clear.public.cashHandling" /></font></span> --%>
						</td>
						<td colspan="5" style="border: none; text-align:center;">
							<!-- 差 错（假币）款 凭 证 --><br>
							<h1 style="color:black;"><spring:message code="clear.clErrorInfo.printName" /></h1>
						</td>
						<td colspan="2" style="text-align: right; border: none; font-size:12px;">
							No:${clErrorInfo.errorNo}<br>${fns:getDate('yyyy-MM-dd HH:mm:ss')}
						</td>
					</tr>
				<tr style="height:10px;">
					<td style="width:150px; text-align: center; border:1px solid">
					<!-- 差错款单位 -->
					<b><spring:message code="clear.clErrorInfo.unit" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid" colspan="3">${clErrorInfo.custName}</td>
					<td style="width:170px;text-align: center; border:1px solid" colspan="2">
					<!-- 差错发现单位 -->
					<b><spring:message code="clear.clErrorInfo.findUnit" /></b></td>
					<td style="width:255px;text-align: center; border:1px solid" colspan="3">${find}</td>
					<td rowspan="12" style="border:none;">
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
						<!-- 差 -->
						<spring:message code="clear.public.cha" /><br>
						<!-- 错 -->
						<spring:message code="clear.public.cuo" /><br>
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
						<!-- 现 -->
						<spring:message code="clear.public.xian" /><br>
						<!-- 钞 -->
						<spring:message code="clear.public.chao" /><br>
						<!-- 处 -->
						<spring:message code="clear.public.chu" /><br>
						<!-- 理 -->
						<spring:message code="clear.public.li" /><br>
						<!-- 中 -->
						<spring:message code="clear.public.zhong" /><br>
						<!-- 心 -->
						<spring:message code="clear.public.xin" />
					</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:150px;text-align: center; border:1px solid;">
					<!-- 差错金额 -->
					<b><spring:message code="clear.clErrorInfo.amountError" /></b></td>
					<td style="width:200px;text-align: right; border:1px solid;" colspan="3"><fmt:formatNumber value="${clErrorInfo.errorMoney}" pattern="￥ #,##0.00" /></td>
					<td style="width:170px;text-align: center; border:1px solid;"  colspan="2">
					<!-- 差错金额大写 -->
					<b><spring:message code="clear.clErrorInfo.moneyUpper" /></b></td>
					<td style="width:255px;text-align: center; border:1px solid;" colspan="3">${strBigAmount}</td>
				</tr>
	
				<tr style="height:10px;">
					<td style="width:150px;text-align: center; border:1px solid;" rowspan="9"></td>
					<td style="width:170px;text-align: center; border:1px solid;" colspan="2">
					<!-- 差错类型 -->
					<b><spring:message code="clear.clErrorInfo.type" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${fns:getDictLabel(clErrorInfo.errorType,'clear_error_type',"")}</td>
					<!-- 去掉笔数 wzj 2017-11-17 begin -->
					<!-- <td style="width:85px;text-align: center; border:1px solid;"> -->
					<!-- 笔数 -->
					<%-- <b><spring:message code="clear.clErrorInfo.theNumber" /></b></td>
					<td style="width:170px;text-align: right; border:1px solid;" colspan="2">
					${clErrorInfo.strokeCount} <spring:message code="clear.public.bi" /></td> --%><!-- 笔 -->
					<td style="width:255px;text-align: center; border:1px solid;text-align:left;vertical-align:top" colspan="3" rowspan="9"> 
					<!-- 备注 -->
					<label><b><spring:message code="common.remark" />:</b></label>
					<div style="margin-left:10px;width:240px;overflow:hidden; word-wrap:break-word;">${clErrorInfo.remarks}</div></td>
					<!-- end -->
				</tr>
				<tr style="height:10px;">
					<td style="width:75px;text-align: center; border:1px solid;" rowspan="3">
					<!-- 封签 -->
					<b><spring:message code="clear.clErrorInfo.facingSlip" /></b></td>
					<td style="width:95px;text-align: center; border:1px solid;" >
					<!-- 封签单位 -->
					<b><spring:message code="clear.clErrorInfo.sealUnit" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${clErrorInfo.seelOrg}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 封签名章 -->
					<b><spring:message code="clear.clErrorInfo.chapterSeal" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;"colspan="3">${clErrorInfo.seelChap}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 封签日期 -->
					<b><spring:message code="clear.clErrorInfo.sealTheDate" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3"><fmt:formatDate value="${clErrorInfo.seelDate}" pattern="yyyy-MM-dd" /></td>
				</tr>
				<tr style="height:10px;">
					<td style="width:75px;text-align: center; border:1px solid;">
					<!-- 腰条 -->
					<b><spring:message code="clear.clErrorInfo.article" /></b></td>
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 腰条名章 -->
					<b><spring:message code="clear.clErrorInfo.articleWaist" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${clErrorInfo.stripChap}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:75px;text-align: center; border:1px solid;" rowspan="4">
					<!-- 差错币 -->
					<b><spring:message code="clear.clErrorInfo.errorCoin" /></b></td>
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 版别 -->
					<b><spring:message code="clear.clErrorInfo.differentEditions" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${clErrorInfo.versionError}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 面值 -->
					<b><spring:message code="common.denomination" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${sto:getGoodDictLabel(clErrorInfo.denomination, 'cnypden', "")}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 张数 -->
					<b><spring:message code="clear.public.zhangShu" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${clErrorInfo.count}</td>
				</tr>
				<tr style="height:10px;">
					<td style="width:95px;text-align: center; border:1px solid;">
					<!-- 冠字号 -->
					<b><spring:message code="clear.clErrorInfo.crownSize" /></b></td>
					<td style="width:200px;text-align: center; border:1px solid;" colspan="3">${clErrorInfo.sno}</td>
				</tr>
				<tr style="height:1px;">
				<td style="width:150px; border:none;"></td>
				<td style="width:75px; border:none;"></td>
				<td style="width:95px; border:none;"></td>
				<td style="width:30px; border:none;"></td>
				<td style="width:85px; border:none;"></td>
				<td style="width:85px; border:none;"></td>
				<td style="width:85px; border:none;"></td>
				<td style="width:85px; border:none;"></td>
				<td style="width:85px; border:none;"></td>
				</tr>
			</table>
			<!-- <div style="float:left;width:20px;margin-left:1px;margin-top:0px ;">
				第一联︵<br>白<br>︶<br>差错款单位第二联︵<br>彩<br>︶<br>现钞处理中心
			</div> -->
		
		<div style="float:left;width:60px;margin-left:20px;clear:both;text-align:left;">
		<!-- 制单 -->
		<b><spring:message code="common.voucherMaking" />:</b></div>
		<div style="float:left;width:100px;margin-left:0px;text-align:left;">${clErrorInfo.makesureManName}</div>
		<div style="float:left;width:70px;margin-left:5px;text-align:left;">
		<!-- 发现人 -->
		<b><spring:message code="clear.public.findMan" />:</b></div>
		<div style="float:left;width:100px;margin-left:0px;text-align:left;">${clErrorInfo.clearManName}</div>
		<div style="float:left;width:90px;margin-left:5px;text-align:left;">
		<!-- 差错复核 -->
		<b><spring:message code="clear.clErrorInfo.review" />:</b></div>
		<div style="float:left;width:100px;margin-left:0px;text-align:left;">${clErrorInfo.checkManName}</div>
		<div style="float:left;width:60px;margin-left:5px;text-align:left;">
		<!--主管  -->
		<b><spring:message code="clear.public.director" />:</b></div>
	</div>
</body>
</html>