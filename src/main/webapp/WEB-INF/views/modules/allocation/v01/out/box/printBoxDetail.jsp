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
				<table style="width:100%; border: 1px black solid; border-collapse: collapse; border: none; text-align: center; font-size:12px; line-height:18px;" border="1">
					<tr>
						<td style="border: none;"></td>
						<td colspan="2"  style="text-align:left;border: none;">
						 <span style="text-decoration:underline"><font size="3">
							<!-- 辽宁聚龙金融自助装备有限公司 -->
							<b><spring:message code="allocation.print.company" /></b></font>
							<!-- 现钞处理中心 -->
							<!-- <font size="0.5"><spring:message code="clear.public.cashHandling" /></font></span>  -->
						</td>
						<td colspan="3" style="border: none;text-align: left;">
							<!-- 款箱出入库交接凭证 -->
							<h3 style="color:black;"><spring:message code="allocation.print.title" /></h3>
						</td>
						<td style="text-align: right; border: none;font-size:12px;">
							<!-- 交接时间 -->
							<spring:message code="allocation.print.handtime" /><br>${fns:getDate('yyyy年MM月dd日')}
						</td>
					</tr>
					<tr>
						<td rowspan="15" style="letter-spacing: 1px;  width: 20px; border:none;" valign="bottom" ></td>
						<!-- 出库机构  -->
						<td style="text-align: center; height:40px; width: 190px; border: 1px solid;"><font size="2">
							<b><spring:message code="allocation.outstore.office" /></b></font></td>
							<c:if test="${allAllocateInfo.businessType eq 30}">
								<td style="text-align: center; width: 240px; border: 1px solid;">${allAllocateInfo.rOffice.name}</td>
							</c:if>
							<c:if test="${allAllocateInfo.businessType eq 31}">
								<td style="text-align: center; width: 240px; border: 1px solid;">${allAllocateInfo.aOffice.name}</td>
							</c:if>
						<!-- 接收机构 -->
						<td style="text-align: center; border: 1px solid;width: 150px;">
							<font size="2"><b><spring:message code="allocation.instore.office" /></b></font></td>						
							<c:if test="${allAllocateInfo.businessType eq 30}">
								<td style="text-align: center; border: 1px solid;" colspan="4">${allAllocateInfo.aOffice.name}</td>
							</c:if>
							<c:if test="${allAllocateInfo.businessType eq 31}">
								<td style="text-align: center; border: 1px solid;" colspan="4">${allAllocateInfo.rOffice.name}</td>
							</c:if>
						<td rowspan="15" style="border: none;">
							<!-- 第 -->
							<spring:message code="clear.public.di" /><br>
							<!-- 一 -->
							<spring:message code="clear.public.yi" /><br>
							<!-- 联 -->
							<spring:message code="clear.public.lian" /><br>
							<br>
							<!-- 出 -->
							<spring:message code="allocation.print.chu" /><br>
							<!-- 库 -->
							<spring:message code="allocation.print.ku" /><br>
							<!-- 机 -->
							<spring:message code="allocation.print.ji" /><br>
							<!-- 构 -->
							<spring:message code="allocation.print.gou" /><br>
							<br>
							<!-- 第 -->
							<spring:message code="clear.public.di" /><br>
							<!-- 二 -->
							<spring:message code="clear.public.er" /><br>
							<!-- 联 -->
							<spring:message code="clear.public.lian" /><br>
							<br>
							<!-- 接 -->
							<spring:message code="allocation.print.jie" /><br>
							<!-- 收 -->
							<spring:message code="allocation.print.shou" /><br>
							<!-- 机 -->
							<spring:message code="allocation.print.ji" /><br>
							<!-- 构 -->
							<spring:message code="allocation.print.gou" /><br>
						</td>
					</tr>
					<tr>
						<!--移交款箱总数（小写）  -->
						<td style="text-align: center; height:40px;  border: 1px solid;"><font size="2">
							<b><spring:message code="allocation.print.boxNumLowCase" /></b></font></td>
						<td style="text-align: center; width: 240px; border: 1px solid;">${boxNum}</td>
						<!-- 移交总数（大写）-->
						<td style="text-align: center; border: 1px solid;">
							<font size="2"><b><spring:message code="allocation.print.handoverNumUpperCase" /></b></font></td>
						<td style="text-align: center; border: 1px solid;" colspan="4">${strBigAmount}</td>
					</tr>
					<tr>		
						<td colspan="2" style="width:340px; border:1px solid; border-bottom:none; text-align: left;">
							<!-- 备注 -->
							<b><spring:message code="allocation.print.remarks" />：</b>
						</td>
						<!-- 款箱编号 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:120px; height:20px">
							<spring:message code="allocation.print.boxNo" /></td>
						<!-- 箱袋类型 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:125px; height:20px;">
							<spring:message code="allocation.print.boxType" /></td>
						<!-- 扫描状态 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold; width:125px;">
							<spring:message code="allocation.scan.status" /></td>
						<!-- 扫描时间 -->
						<td style="text-align: center; border: 1px solid; font-weight: bold;">
							<spring:message code="allocation.scan.date" /></td>
					</tr>
					<c:forEach items="${allDetailList}" var="allDetail" begin="${0+10*(status.index-1)}" end="${9+10*(status.index-1)}" varStatus="statuss">
						<c:if test="${boxNum <= 4}">
							<tr>
								<td style="border:none; border-left:1px solid;" colspan="2"></td>
								<td style="border: 1px black solid; border-top: none; height:20px;">${allDetail.boxNo}</td>
								<td style="border: 1px black solid; border-top: none;">${boxType}</td>
								<td style="border: 1px black solid; border-top: none;">${fns:getDictLabel(allDetail.scanFlag,'all_box_scanFlag',"")}</td>
								<td style="width:150px; text-align: right; border: 1px solid; border-top:none;"><fmt:formatDate value="${allDetail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							</tr>
						</c:if>
						<c:if test="${boxNum > 4}">
							<c:if test="${statuss.index == 3 }">
								<tr>
									<td style="border:none; border-left:1px solid; border-bottom: 1px solid"  colspan="2"></td>
									<td style="border: 1px solid; height:20px;">${allDetail.boxNo}</td>
									<td style="border: 1px solid; ">${boxType}</td>
									<td style="border: 1px solid; ">${fns:getDictLabel(allDetail.scanFlag,'all_box_scanFlag',"")}</td>
									<td style="width:150px; text-align: right; border: 1px solid; border-top:none;"><fmt:formatDate value="${allDetail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
								</tr>
							</c:if>
							<c:if test="${statuss.index == 11 }">
								<tr style="height:30px;">
									<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
									<!-- 寄存尾箱数量 -->
									<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
									<td colspan="3" style="border: 1px solid; text-align: center;" >
									<font><b></b></font></td>
								</tr>
							</c:if>
							<c:if test="${statuss.index != 3 and statuss.index != 11 }">
								<tr>
									<td style="border:none; border-left:1px solid; " ></td>	
									<td style="border:none;"></td>
									<td style="border: 1px solid; height:20px;">${allDetail.boxNo}</td>
									<td style="border: 1px solid; ">${boxType}</td>
									<td style="border: 1px solid; ">${fns:getDictLabel(allDetail.scanFlag,'all_box_scanFlag',"")}</td>
									<td style="width:150px; text-align: right; border: 1px solid; border-top:none;"><fmt:formatDate value="${allDetail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
								</tr>
							</c:if>
						</c:if>
					</c:forEach>
					<!-- 判断是否是最后一次循环 -->
					<c:if test="${status.last}">
						<!-- 判断数据是否达到10行如果不足进行补充 -->
						<c:if test="${((fn:length(allDetailList))%10)!= 0}">
							<c:forEach var="i" begin="1" end="${11-((fn:length(allDetailList))%10)}" varStatus="abc">
							<c:if test="${blankcount < 4}">
								<c:if test="${abc.index == (4-blankcount) }">
									<tr>
										<td style="border:none; border-left:1px solid; border-bottom: 1px solid"  colspan="2"></td>
										<td style="border: 1px solid; height:20px;"></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
									</tr>
								</c:if>
								<c:if test="${abc.index == (11-blankcount) }">
									<tr style="height:30px;">
										<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
										<!-- 寄存尾箱数量 -->
										<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
										<td colspan="3" style="border: 1px solid; text-align: center;">
										<font><b></b></font></td>
									</tr>
								</c:if>
								<c:if test="${abc.index != (4-blankcount) and abc.index != (11-blankcount) }">
									<tr>
										<td style="border:none; border-left:1px solid; " ></td>	
										<td style="border:none;"></td>
										<td style="border: 1px solid; height:20px;"></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
									</tr>
								</c:if>
							</c:if>
							<c:if test="${blankcount == 4}">
								<c:if test="${abc.index == 1 }">
									<tr>
										<td style="border:none; border-left:1px solid;  border-top:1px solid;"  colspan="2"></td>
										<td style="border: 1px solid; height:20px;"></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
									</tr>
								</c:if>
								<c:if test="${abc.index == (11-blankcount) }">
									<tr style="height:30px;">
										<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
										<!-- 寄存尾箱数量 -->
										<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
										<td colspan="3" style="border: 1px solid; text-align: center;">
										<font><b></b></font></td>
									</tr>
								</c:if>
								<c:if test="${abc.index != 1 and abc.index != (11-blankcount) }">
									<tr>
										<td style="border:none; border-left:1px solid; " ></td>	
										<td style="border:none;"></td>
										<td style="border: 1px solid; height:20px;"></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
										<td style="border: 1px solid; "></td>
									</tr>
								</c:if>
							</c:if>
							<c:if test="${blankcount > 4 && blankcount<10}">
								<c:if test="${abc.index == (11-blankcount) }">
								<tr style="height:30px;">
									<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
									<!-- 寄存尾箱数量 -->
									<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
									<td colspan="3" style="border: 1px solid; text-align: center;">
									<font><b></b></font></td>
								</tr>
								</c:if>
								<c:if test="${abc.index != (11-blankcount) }">
								<tr>
									<td style="border:none; border-left:1px solid; " ></td>	
									<td style="border:none;"></td>
									<td style="border: 1px solid; height:20px;"></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
									<td style="border: 1px solid; "></td>
								</tr>
								</c:if>
							</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${((fn:length(allDetailList))%10)== 0}">
							<tr style="height:30px;">
								<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
								<!-- 寄存尾箱数量 -->
								<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
								<td colspan="3" style="border: 1px solid; text-align: center;">
								<font><b></b></font></td>
							</tr>
						</c:if>
					</c:if>
					<c:if test="${!status.last}">
						<tr style="height:30px;">
							<td style="border:none; border-left:1px solid; text-align: left;"  colspan="2" ></td>
							<!-- 寄存尾箱数量 -->
							<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.storeBoxNum" /></td>
							<td colspan="3" style="border: 1px solid; text-align: center;">
							<font><b></b></font></td>
						</tr>
					</c:if>
					<tr style="height:30px;">
						<!-- 盖交接专用章 -->
						<td style="text-align: left; border:none;border-left: 1px black solid;border-bottom: 1px black solid;"  colspan="2" ><spring:message code="allocation.print.seal" /> </td>
						<!-- 上缴款箱数量 -->
						<td style="border: 1px black solid; font-weight: bold; "><spring:message code="allocation.print.handoverBoxNum" /></td>
						<td colspan="3" style="border: 1px solid; text-align: center;">
						<font><b></b></font></td>
					</tr>
				</table>
				<table style="float: left; margin-left: 20px; width:90%; font-size:12px;">
					<tr>
						<td><b><spring:message code="allocation.print.handoutMan1" />：</b></td>
						<td></td>
						<td><b><spring:message code="allocation.print.handoutMan2" />：</b></td>
						<td></td>
						<!-- 中心复核员 -->
						<td><b><spring:message code="allocation.print.receiveMan1" />：</b></td>
						<td></td>
						<td><b><spring:message code="allocation.print.receiveMan2" />：</b></td>
						<td></td>
						<td style="text-align:right;">第${status.count}/${size}页</td>
					</tr>
				</table>
			</div>
		</c:forEach>
	</div>
</body>
</html>