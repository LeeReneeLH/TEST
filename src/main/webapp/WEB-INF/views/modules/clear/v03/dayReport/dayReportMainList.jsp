<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 账务日结管理 -->
	<title><spring:message code='clear.dayReportMain.title'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<style>
	.dropdown-menu{top:30px !important;right:0 !important;left: auto !important;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			setToday(".createTime");
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		// 打印审批物品明细
		function printDetail(reportId,type){
	
			var content = "iframe:${ctx}/clear/v03/dayReportMain/printDetail?reportId=" + reportId+"&type="+type;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 900, 600, {
						buttons : {
							//打印
							"<spring:message code='common.print' />" : "ok",
							// 关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var printDiv = h.find("iframe")[0].contentWindow.printDiv;
								$(printDiv).show();
								//打印 
								$(printDiv).jqprint();
								return true;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
					});
			
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 账务日结列表 -->
		<li class="active"><a href="${ctx}/clear/v03/dayReportMain/"><spring:message code='clear.dayReportMain.list'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="dayReportMain" action="${ctx}/clear/v03/dayReportMain/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>&nbsp;
		<!-- 增加清分机构 wzj 2017-11-24 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="office" name="office.id"
				value="${dayReportMain.office.id}" labelName="office.name"
				labelValue="${dayReportMain.office.name}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		&nbsp;&nbsp;&nbsp;&nbsp;
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<!-- 账务结算 -->
		<a href="${ctx}/clear/v03/dayReportMain/report" onclick="return confirmx('<spring:message code="message.A1003"/>', this.href)"><input id="report" class="btn btn-primary" type="button" value="<spring:message code='clear.dayReportMain.billingSettlement'/>"/></a>
		
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 昨日余额(元) -->
				<th><spring:message code='clear.dayReportMain.yesterdayBalance'/></th>
				<!-- 收入笔数 -->
				<th><spring:message code='clear.dayReportMain.incomeNumber'/></th>
				<!-- 收入金额(元) -->
				<th><spring:message code='clear.dayReportMain.incomeAmount'/></th>
				<!-- 付出笔数 -->
				<th><spring:message code='clear.dayReportMain.outcomeNumber'/></th>
				<!-- 付出金额(元) -->
				<th><spring:message code='clear.dayReportMain.payAmount'/></th>
				<!-- 金额(元) -->
				<th><spring:message code='clear.public.moneyFormat'/></th>
				<!-- 日结人 -->
				<th><spring:message code='clear.dayReportMain.dailyPeople'/></th>
				<!-- 状态 -->
				<th><spring:message code="common.status"/></th>
				<!-- 结账类型 -->
				<th><spring:message code="clear.dayReportMain.checkType"/></th>
				<!-- 结算时间 -->
				<th><spring:message code="clear.dayReportMain.checkTime"/></th>
				<!-- 结算日期 -->
				<th><spring:message code="clear.dayReportMain.checkDate"/></th>
				<!-- 增加机构显示 qph 2017-11-27 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- 操作 -->
				<th colspan="2"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="dayReportMain" varStatus="status">
				<tr>
					<td>
						${status.index+1}
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.beforeAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.inCount}" pattern="#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.inAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.outCount}" pattern="#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.outAmount}" pattern="#,##0.00#" />
					</td>
					<td>
						<fmt:formatNumber value="${dayReportMain.totalAmount}" pattern="#,##0.00#" />
					</td>
					<!-- 判断结账类型是否为自动结账如果是自动结账显示系统日结 修改人:sg 修改日期:2017-12-07 begin -->
					<c:if test="${dayReportMain.windupType eq 0}">
						<td>
							<spring:message code="clear.dayReportMain.reportName"/>
						</td>
					</c:if>
					<c:if test="${dayReportMain.windupType ne 0}">
						<td>
							${dayReportMain.reportName}
						</td>
					</c:if>
					<!-- end -->
					<c:choose>
						<c:when test="${dayReportMain.status eq 0}">
							<td style="color: green">
						</c:when>
						<c:otherwise>
							<td style="color: red">
						</c:otherwise>
					</c:choose>
						${fns:getDictLabel(dayReportMain.status,'day_report_status',"")}
					</td>
					<td>
						${fns:getDictLabel(dayReportMain.windupType,'day_report_windupType',"")}
					</td>
					<td>
						<fmt:formatDate value="${dayReportMain.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						<fmt:formatDate value="${dayReportMain.reportDate}" pattern="yyyy-MM-dd"/>
					</td>
					
					<!-- 增加机构 qph 2017-11-27 begin -->
					<td>
					${dayReportMain.office.name} 
					</td>
					<td>
					<!-- 查看 -->
					<a href="${ctx}/clear/v03/dayReportMain/centerView?reportId=${dayReportMain.reportId}" title = "<spring:message code='common.view'/>">
										<%-- 	<spring:message code="common.view" /> --%>
											<i class="fa fa-eye fa-lg"></i>
							</a>
								</td>
								<td>
					 <ul class="nav">
						
						
			       			<li class="dropdown">
			       			
				    		 <a href="" title = "打印" id="" href="#" class="dropdown-toggle" data-toggle="dropdown">
											<i class="fa fa-print text-yellow fa-lg"></i>
							 </a>
				          <ul class="dropdown-menu">
				          	<!-- 清分日结单打印 -->
							<li><a href="#" onclick="printDetail('${dayReportMain.reportId}','1');"><spring:message code="accounts.report.clearPrint" /></a></li>
							<!-- 复点日结单打印 -->
							<li><a href="#" onclick="printDetail('${dayReportMain.reportId}','2');"><spring:message code="accounts.report.complexPrint" /></a></li>
							<!-- 汇总日结单打印 -->
							<li><a href="#" onclick="printDetail('${dayReportMain.reportId}','3');"><spring:message code="accounts.report.totalPrint" /></a></li>
			      		</ul>
			        </li>
		        </ul>
				</td>		
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>