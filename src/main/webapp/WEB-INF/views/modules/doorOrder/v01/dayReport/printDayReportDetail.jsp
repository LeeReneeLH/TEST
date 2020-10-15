<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv" >
		<div
			style="float: left; width: 98%; position: relative; height: 60px; margin-top: 20px;">
			<div style="margin-left: 45px;">
				<span style="text-decoration:underline"><font size="4">
			<!-- 聚龙股份 -->
			<b><spring:message code="clear.public.julong" /></b></font>
			<!-- 现钞处理中心 -->
			<font size="0.5"><spring:message code="clear.public.cashHandling" /></font></span>
			</div>
			<!-- 打印时间 -->
			<div>
				<h3 align="center">
					<!--现金中心日结单  -->
					<spring:message code="clear.dayReportMain.Statement" />
					<c:if test="${type eq 1}">
					<!--（清分）-->
					<spring:message code="clear.dayReportMain.clear" />
					</c:if>
					<c:if test="${type eq 2}">
					<!--（复点）-->
					<spring:message code="clear.dayReportMain.point" />
					</c:if>
					<c:if test="${type eq 3}">
					<!--（汇总）-->
					<spring:message code="clear.dayReportMain.sum" />
					</c:if>
				</h3>
			</div>

		</div>
		<div
			style="float: left; width: 98%; position: relative; height: 20px;">
			<!-- 打印时间 -->
			<div
				style="width: 45%; text-align: right; position: absolute; right: 5%; top: 0px;">
				<!-- 结账日期 -->
				<spring:message code="clear.dayReportMain.payDate" />：${reportDate}</div>
		</div>


	<div style="text-align: center">
		<table id="contentTable"
			style="width: 90%; border: 1px black solid; border-collapse: collapse; height:300px; text-align: left; margin: auto;"
			border="1" >
			<thead>
				<tr>
					<!-- 项目 -->
					<td style="text-align: center; border:1px solid;" rowspan="2"><spring:message code="clear.public.project" /></td>
					<!-- 昨日清分余额 -->
					<c:if test="${type eq 1}">
						<td style="text-align: center; border:1px solid;" rowspan="2"><spring:message code="clear.public.clearYesterday" /></td>
					</c:if>
					<!-- 昨日复点余额 -->
					<c:if test="${type eq 2}">
						<td style="text-align: center; border:1px solid;" rowspan="2"><spring:message code="clear.public.pointYesterday" /></td>
					</c:if>
					<!-- 昨日总余额 -->
					<c:if test="${type eq 3}">
						<td style="text-align: center; border:1px solid;" rowspan="2"><spring:message code="clear.public.sumYesterday" /></td>
					</c:if>

					<!-- 借方 -->
					<td style="text-align: center; border:1px solid;" colspan="2"><spring:message code="clear.public.borrower" /></td>
					<!-- 贷方 -->
					<td style="text-align: center; border:1px solid;" colspan="2"><spring:message code="clear.public.lender" /></td>
					<!-- 余额 -->
					<td style="text-align: center; border:1px solid;" rowspan="2"><spring:message code="clear.public.balance" /></td>

				</tr>
				<tr>
					<!-- 笔数 -->
					<td style="text-align: center; border:1px solid;"><spring:message code="clear.clErrorInfo.theNumber" /></td>
					<!-- 金额 -->
					<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.moneyFormat" /></td>
					<!-- 笔数 -->
					<td style="text-align: center; border:1px solid;"><spring:message code="clear.clErrorInfo.theNumber" /></td>
					<!-- 金额 -->
					<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.moneyFormat" /></td>
				</tr>
			</thead>
			<tbody>
				<c:if test="${type eq 1}">
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '01' }">
							<tr>
								<!-- 商行交款 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.bankpay" /></td>
								<!--删除rowspan="${size}"以及value="${beforeAmount}" wzj 2017-11-16 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								
								<c:choose>
									<c:when test="${item.inCount == '0' or empty item.inCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<td style="text-align: center; border:1px solid;"></td>
								 <c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose> 
								<!-- 删除rowspan="${size}"以及value="${todayAmount}" wzj 2017-11-16 begin -->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '02' }">
							<tr>
								<!-- 商行取款 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.bankget" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<!-- 增加列  wzj 2017-11-16 begin--> 							
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '03' }">
							<tr>
								<!-- 代理上缴 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.agencyPay" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>
					</c:forEach>
					<tr>
						<!-- 合计 -->
						<td style="text-align: center; border:1px solid;"><spring:message code="common.total" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${beforeAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalInCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalInAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalOutCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalOutAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${todayAmount}" pattern="#,##0.00#" /></td>
					</tr>
				</c:if>
				<c:if test="${type eq 2}">
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '04' }">
							<tr>
								<!-- 从人行复点入库 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.peopleBankIn" /></td>
								<!-- 删除rowspan="${size}"以及value="${beforeAmount}" wzj 2017-11-17 begin  -->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<!-- 删除rowspan="${size}"以及value="${todayAmount}" wzj 2017-11-17 begin -->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '05' }">
							<tr>
								<!-- 从人行复点出库 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.peopleBankOut" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								
								<c:choose>
									<c:when test="${item.inCount == '0' or empty item.inCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<tr>
						<!-- 合计 -->
						<td style="text-align: center; border:1px solid;"><spring:message code="common.total" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${beforeAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalInCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalInAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalOutCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalOutAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${todayAmount}" pattern="#,##0.00#" /></td>
					</tr>
				</c:if>
				
				<c:if test="${type eq 3}">
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '01' }">
							<tr>
								<!-- 商行交款 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.bankpay" /></td>
								<!-- 删除rowspan="${size}"以及value="${beforeAmount}" wzj 2017-11-17 begin -->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<c:choose>
									<c:when test="${item.inCount == '0' or empty item.inCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<!-- 删除rowspan="${size}"以及value="${todayAmount}" wzj 2017-11-17 begin -->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '02' }">
							<tr>
								<!-- 商行取款 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.bankget" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '03' }">
							<tr>
								<!-- 代理上缴 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.agencyPay" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>
					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '04' }">
							<tr>
								<!-- 从人行复点入库 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.peopleBankIn" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outCount == '0' or empty item.outCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.outCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"> <fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /> </td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
					<c:forEach items="${centerAccountsMainsList}" var="item"
						varStatus="status">
						<!-- 审批登记物品 -->

						<c:if test="${item.businessType eq '05' }">
							<tr>
								<!-- 从人行复点出库 -->
								<td style="text-align: center; border:1px solid;"><spring:message code="clear.public.peopleBankOut" /></td>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
								<c:choose>
									<c:when test="${item.inCount == '0' or empty item.inCount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inCount}"/></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.inAmount == '0' or empty item.inAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><fmt:formatNumber value="${item.inAmount}" pattern="#,##0.00#" /></td>
									</c:otherwise>
								</c:choose>
								<td style="text-align: center; border:1px solid;"></td>
								<c:choose>
									<c:when test="${item.outAmount == '0' or empty item.outAmount}">
										<td style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="0" pattern="#,##0.00#" /> --%></td>
									</c:when>
									<c:otherwise>
										<td  style="text-align: center; border:1px solid;"><%-- <fmt:formatNumber value="${item.outAmount}" pattern="#,##0.00#" /> --%></td>
									</c:otherwise>
								</c:choose>
								<!--增加列 wzj 2017-11-17 begin-->
								<td style="text-align: center; border:1px solid;"></td>
								<!-- end -->
							</tr>
						</c:if>

					</c:forEach>
				
					<tr>
						<!-- 合计 -->
						<td style="text-align: center; border:1px solid;"><spring:message code="common.total" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${beforeAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalInCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalInAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;">${totalOutCount}</td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${totalOutAmount}" pattern="#,##0.00#" /></td>
						<td style="text-align: center; border:1px solid;"><fmt:formatNumber value="${todayAmount}" pattern="#,##0.00#" /></td>
					</tr>
				</c:if>

			</tbody>
		</table>
		</div>
		<div style="width: 90%;">
			<div align="left" style="margin-left: 60px;margin-top: 5px">
				<!-- 操作员 -->
				<font><spring:message code="clear.public.operator" />：${fns:getUser().name}</font>
				<font style="margin-left: 110px"><spring:message code="clear.public.godownKeeperA" />：</font>
				<font style="margin-left: 110px"><spring:message code="clear.public.godownKeeperB" />：</font>
			</div>
		</div>
	</div>
</body>
</html>
