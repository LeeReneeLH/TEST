<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务日结管理 -->
	<title><spring:message code='clear.dayReportMain.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		//拆箱单号弹窗拆箱明细
		function showBusinessIdDetail(id) {
			var content = "iframe:${ctx}/collection/v03/checkCash/view?id=" + id + "&type=1";
			top.$.jBox.open(
				content,
				"款箱拆箱查看详情", 1550, 700, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.displayDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi2.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi3.setAttribute("style","display:none");
					}
			});
		}
		function showDetail(id) {
			var content = "iframe:${ctx}/doorOrder/v01/centerAccounts/amountList?detailId=" +id;
			top.$.jBox.open(
					content,
					"查看详情", 800, 700, {
						buttons: {
							//关闭
							"<spring:message code='common.close' />": true
						},
						loaded: function (h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
					});
		}
		//款袋编号弹窗存款明细
		 function showBagNoDetail(id) {
			var content = "iframe:${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=" +id;
			top.$.jBox.open(
				content,
				"查看详情", 1400, 700, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.disDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi2.setAttribute("style","display:none");
					}
			});
		} 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <!-- 商户日结列表 -->
		<li ><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/dayReportDoorMerchan/"><spring:message code='door.accountManage.merchanReportList'/></a></li>
		<!-- 差错结算列表 -->
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorError:view">
		<li ><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/dayReportDoorMerchan/errorList"><spring:message code='door.accountManage.errorReportList'/></a></li>
		</shiro:hasPermission>
		<!-- 日结每笔明细 -->
		<li class="active"><a>
		<c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
		<spring:message code='door.accountManage.errorView'/>
		</c:if>
		<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
		<spring:message code='clear.dayReportMain.view'/>
		</c:if>
		</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="centerAccountsMain" action="${ctx}/doorOrder/v01/dayReportDoorMerchan/detailView?settlementType=${settlementType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<form:input type="hidden" path="reportId" />
		<form:input type="hidden" path="merchantOfficeId" />
		<form:input type="hidden" path="clientId" />
		<!-- 业务类型 -->
		<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
		<label><spring:message code="clear.task.business.type"/>：</label>
		<form:select path="businessType" class="input-medium required">
			<form:option value=""><spring:message code="common.select" /></form:option>				
			<form:options items="${fns:getFilterDictList('door_businesstype',true,'74,78,81,82')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</c:if>
		<c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
		<label><spring:message code="clear.task.business.type"/>：</label>
		<form:select path="businessType" class="input-medium required">
			<form:option value=""><spring:message code="common.select" /></form:option>				
			<form:options items="${fns:getFilterDictList('door_businesstype',true,'79,80')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</c:if>&nbsp;
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />&nbsp;
		<div>
		&nbsp;
		<!-- 返回 -->
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
		onclick="history.go(-1)"/>
		<%-- onclick="window.location.href='${ctx}/doorOrder/v01/dayReportDoorMerchan/back?settlementType=${settlementType}'"/> --%>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
				<!-- 拆箱编号 -->
				<th class="sort-column checkCash.codeNo"><spring:message code="door.checkCash.codeNo" /></th>
				<!-- 包号 -->
				<th class="sort-column checkCash.packNum"><spring:message code="door.doorOrder.packNum" /></th>
				</c:if>
				
				<!-- 客户名称 -->
				<th class="sort-column s.name"><spring:message code="clear.public.custName"/></th>
				
				<c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
					<!-- 清分员 -->
					<th class="sort-column a.create_name"><spring:message code="door.checkCash.clearMan"/></th>
					<!-- 清分员ID -->
					<th class="sort-column user.login_name"><spring:message code="door.checkCash.clearManId"/></th>
				</c:if>
				<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
					<!-- 存款人 -->
					<th class="sort-column a.create_name"><spring:message code="clear.public.inManName"/></th>
					<!-- 存款人ID -->
				<th class="sort-column user.login_name"><spring:message code="door.accountManage.savePeopleId"/></th>
				</c:if>
				<!-- 业务类型 -->
				<th class="sort-column a.business_type"><spring:message code="clear.task.business.type"/></th>
				<!-- 操作类型  -->
				<th class="sort-column a.business_status"><spring:message code="clear.centerAccounts.operationType"/></th>
				<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
				<!-- 现钞存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.cashAmount"/></th>
				<!-- 封包存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.packAmount"/></th>
				<!-- 封包存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.otherAmount"/></th> 
				</c:if>
				
				<!-- 客户存款金额(元) -->
				<th class="sort-column a.in_amount"><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 公司汇款金额(元) -->
				<th class="sort-column a.out_amount"><spring:message code="clear.centerAccounts.lender"/></th>
				<!-- 交易时间 -->
				<th class="sort-column a.create_date"><spring:message code="clear.centerAccounts.tradingHours"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="centerAccountsMain" varStatus="status">
				<tr>
			       <td>
					   <c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
						   <c:if test="${status.index eq 0}"></c:if>
						   <c:if test="${status.index ne 0}">
							   ${status.index}
						   </c:if>
					   </c:if>
					   <c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
						   ${status.index + 1}
					   </c:if>
                   </td>
                   <c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
					<td>
                   <a href="javascript:void(0);" onclick="showBusinessIdDetail('${centerAccountsMain.codeNo}')">
						${centerAccountsMain.codeNo}
					</a>
						
					</td>
					<td>
					<a href="javascript:void(0);" onclick="showBagNoDetail('${centerAccountsMain.infoId}')">
						${centerAccountsMain.packNum} 
					</a>
						
					</td>
					</c:if>
                   
					<td>
						${centerAccountsMain.clientName}
					</td>
					<td>
						${centerAccountsMain.createBy.name}
					</td>
					<td>
						${centerAccountsMain.createBy.loginName}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessType,'door_businesstype',"")}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessStatus,'cl_status_type',"")}
					</td>
					<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.cash==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise>
								<c:if test="${dayReportDoorMerchan.settlementType eq '02'}">
									<a href="javascript:void(0);" onclick="showDetail('${centerAccountsMain.detailId}')">
										<fmt:formatNumber value="${centerAccountsMain.cash}" pattern="#,##0.00#" />
									</a>
								</c:if>
								<c:if test="${dayReportDoorMerchan.settlementType ne '02'}">
									<c:if test="${status.index eq 0}">	
										<fmt:formatNumber value="${centerAccountsMain.cash}" pattern="#,##0.00#" />	
									</c:if>
									<c:if test="${status.index ne 0}">
										<a href="javascript:void(0);" onclick="showDetail('${centerAccountsMain.detailId}')">
											<fmt:formatNumber value="${centerAccountsMain.cash}" pattern="#,##0.00#" />
										</a>
									</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.pack==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.pack}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.other==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.other}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					</c:if>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.inAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.inAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.outAmount==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${centerAccountsMain.outAmount}" pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
					<td>
						<fmt:formatDate value="${centerAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
	<%-- <div class="form-actions">
			<!-- 返回 -->
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
		onclick="history.go(-1)"/>
		onclick="window.location.href='${ctx}/doorOrder/v01/dayReportDoorMerchan/back?settlementType=${settlementType}'"/>
	</div> --%>
</body>
</html>