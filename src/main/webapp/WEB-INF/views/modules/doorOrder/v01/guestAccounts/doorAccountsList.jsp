<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务管理 -->
	<title><spring:message code="clear.centerAccounts.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				if ($("#custNo").val()=='') {
					alertx("客户名称不能为空");
					return false;
				}
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/guestAccounts/guestAccountsExport/");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/guestAccounts/");	
			});
 			$("#btnSubmit").on('click',function(){
 				if ($("#custNo").val()=='') {
					alertx("门店名称不能为空");
					return false;
				}
 				window.top.clickSelect();
				$("#searchForm").submit();
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
		function showDetail(id) {
			var content = "iframe:${ctx}/doorOrder/v01/centerAccounts/amountList?detailId=" +　id;
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:choose>
			<c:when test="${type eq 'cash'}">
				<!-- 现金 -->
				<shiro:hasPermission name="doorOrder:v01:guestAccounts:view"><li  class="active"><a href="${ctx}/doorOrder/v01/guestAccounts/list?type=cash"><spring:message code="door.accountManage.doorAccountsList"/></a></li></shiro:hasPermission>
				<shiro:hasPermission name="doorOrder:v01:merchantAccounts:view"><li><a onClick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/guestAccounts/merchantlist?type=cash"><spring:message code="door.accountManage.merchantAccountsList"/></a></li></shiro:hasPermission>
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</ul>
	<form:form id="searchForm" modelAttribute="doorCenterAccountsMain" action="${ctx}/doorOrder/v01/guestAccounts/?uninitDateFlag=0" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<div>
		<!-- 客户名称 -->
		<label><spring:message code="door.accountManage.doorName" />：</label>
		<form:select path="clientId" id="custNo"
					class="input-medium required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('8',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select>
		</div>
		<div>
		<!-- 存款人姓名 -->
		<label><spring:message code='clear.public.inManName' />：</label>
		<form:input path="createName" htmlEscape="false" maxlength="20"
			class="input-small" />
		</div>
		<div>
		<!-- 业务类型 -->
		<label><spring:message code="clear.task.business.type"/>：</label>
		<c:choose>
			<c:when test="${type eq 'cash'}">
				<input id="type" type="hidden" name="type" value="cash">
				<form:select path="businessType" class="input-medium">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('door_businesstype',true,'74,78,79,80,81')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:when>
			<c:otherwise>
				<input id="type" type="hidden" name="type" value="pro">
				<form:select path="businessType" class="input-medium" id="businessType">
					<form:option value=""><spring:message code="common.select" /></form:option>				
					<form:options items="${fns:getFilterDictList('clear_businesstype',true,'06,07,70')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</c:otherwise>
		</c:choose>
		</div>
		<div>
		<!-- 开始日期 -->
		<label><spring:message code="clear.dayReportMain.transactionDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${doorCenterAccountsMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		</div>
		<div>
		<!-- 结束日期 -->
		<label>~</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${doorCenterAccountsMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		<!-- end -->
		</div>
		<!--  清分机构查询qph 2017-11-27 begin -->	
			<%--  <label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="rofficeId" name="rofficeId"
				value="${doorCenterAccountsMain.rofficeId}" labelName="office.name"
				labelValue="${doorCenterAccountsMain.rofficeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" /> --%>
		<!-- end -->
		<div>
		<!-- 查询 -->
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
		</div>
		<div>
		<!-- 导出 -->
		&nbsp;&nbsp;<input id="exportSubmit" class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
		</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<%-- 追加显示各类型总金额 add by lihe 2020-04-08 start--%>
	<div id="box_sel" class="row" style="margin-left:10px;">
		<c:forEach items="${tabList}" var="accountsMain" varStatus="status">
			<c:if test="${accountsMain.businessType == '74'}">
				<div class="boxhand-num text-center">
					<h4 style="font-weight: normal" class="text-olive"><spring:message code="door.doorOrder.title" /></h4>
					<span class="green-bg"><fmt:formatNumber value="${accountsMain.totalAmount ne null?accountsMain.totalAmount:0}" pattern="#,##0.00#" /></span>
				</div>
			</c:if>
			<c:if test="${accountsMain.businessType == '81'}">
				<div class="boxhand-num text-center">
					<h4 style="font-weight: normal" class="text-purple"><spring:message code="door.accountManage.tradition" /></h4>
					<span class="purple-bg"><fmt:formatNumber value="${accountsMain.traditionalAmount ne null?accountsMain.traditionalAmount:0}" pattern="#,##0.00#" /></span>
				</div>
			</c:if>
			<c:if test="${accountsMain.businessType == '78'}">
				<div class="boxhand-num text-center">
					<h4 style="font-weight: normal" class="text-aqua"><spring:message code="door.accountManage.remit" /></h4>
					<span class="blue-bg"><fmt:formatNumber value="${accountsMain.outAmount ne null? accountsMain.outAmount:0}" pattern="#,##0.00#" /></span>
				</div>
			</c:if>
			<c:if test="${accountsMain.businessType == '79'}">
				<div class="boxhand-num text-center">
					<h4 style="font-weight: normal" class="text-red"><spring:message code="door.accountManage.error" /></h4>
					<span class="red-bg"><fmt:formatNumber value="${accountsMain.errorAmount ne null? accountsMain.errorAmount:0}" pattern="#,##0.00#" /></span>
				</div>
			</c:if>
			<c:if test="${accountsMain.businessType == '80'}">
				<div class="boxhand-num text-center">
					<h4 style="font-weight: normal" class="text-yellow"><spring:message code="door.accountManage.errorDeal" /></h4>
					<span class="yellow-bg"><fmt:formatNumber value="${accountsMain.errorDealAmount ne null? accountsMain.errorDealAmount:0}" pattern="#,##0.00#" /></span>
				</div>
			</c:if>
		</c:forEach>
	</div>
	<%-- 追加显示各类型总金额 add by lihe 2020-04-08 end--%>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column s.NAME"><spring:message code="door.accountManage.doorName" /></th>
				<!-- 业务类型 -->
				<th class="sort-column a.business_type"><spring:message code="clear.task.business.type"/></th>
				<!-- 业务备注 -->
				<th class="sort-column detail.remarks"><spring:message code="common.remark"/></th>
				<!-- 操作类型 -->
				<th class="sort-column a.business_status"><spring:message code="clear.centerAccounts.operationType"/></th>
				<!-- 存款人名称 -->
				<th class="sort-column a.create_name"><spring:message code="clear.public.inManName"/></th>
				<!-- 存款人编号 -->
				<th class="sort-column USER.login_name"><spring:message code="clear.public.inManNo"/></th>
				<!-- 现钞存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.cashAmount"/></th>
				<!-- 封包存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.packAmount"/></th>
				<!-- 其他存款金额(元) -->
				<th><spring:message code="clear.centerAccounts.otherAmount"/></th>
				<!-- 借方(元) -->
				<th class="sort-column a.in_amount"><spring:message code="clear.centerAccounts.borrower"/></th>
				<!-- 贷方(元) -->
				<th class="sort-column a.out_amount"><spring:message code="clear.centerAccounts.lender"/></th>
				<!-- 余额(元) -->
				<%-- <th class="sort-column a.guest_totalAmount"><spring:message code="clear.centerAccounts.balance"/></th> --%>
				<!-- 交易日期 -->
				<th class="sort-column a.create_date" width="7%"><spring:message code="clear.dayReportMain.transactionDate"/></th>
				<!-- 增加机构显示 qph 2017-11-27 begin -->
				<%--<th><spring:message code="clear.orderClear.office"/></th>--%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="centerAccountsMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						${centerAccountsMain.clientName}
					</td>
					<td>
						${fns:getDictLabel(centerAccountsMain.businessType,'door_businesstype',"")}
					</td>
					<td>
						${centerAccountsMain.sevenCode}
					</td>
					<td>
						${fns:getDictLabelWithCss(centerAccountsMain.businessStatus, 'cl_status_type', '',true)}
					</td>
					<td>${centerAccountsMain.createName}</td>
					<td>${centerAccountsMain.createBy.loginName}</td>
					<td>
						<c:choose>
							<c:when test="${centerAccountsMain.cash==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise>
								<a href="javascript:void(0);" onclick="showDetail('${centerAccountsMain.detailId}')">
									<fmt:formatNumber value="${centerAccountsMain.cash}" pattern="#,##0.00#" />
								</a>
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
					<%-- <td>
						<fmt:formatNumber value="${centerAccountsMain.guestTotalAmount}" pattern="#,##0.00#" />
					</td> --%>
					<td>
						<fmt:formatDate value="${centerAccountsMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<!-- 增加机构 qph 2017-11-27 begin -->
					<%--<td>
					${centerAccountsMain.rofficeName} 
					</td>--%>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>