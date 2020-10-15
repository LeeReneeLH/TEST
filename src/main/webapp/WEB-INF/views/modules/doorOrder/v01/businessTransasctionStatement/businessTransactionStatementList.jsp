<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>交易报表管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		//全选
		$("#chooseAll").on('click',function(){
			if($(this).prop('checked')){
				$('[name="businessIds"]').each(function () {
					$(this).prop("checked",true);
				});
			}else{
				$('[name="businessIds"]').each(function () {
					$(this).prop("checked",false);
				});
			}
		});

		// 批量确认
		$("#confirm").on('click',function(){
			//日结主键列表
			var businessIds='';
			$('[name="businessIds"]').each(function () {
				if($(this).prop('checked')){
					businessIds+=","+$(this).val();
				}
			});
			if(businessIds==''){
				alertx("请选择要确认行！");
				return;
			}
			businessIds=businessIds.substr(1);
			top.$.jBox.confirm('是否确认记录？','系统提示',function(v,h,f){
				if(v=='ok'){
					$("#searchForm").prop("action", "${ctx}//doorOrder/v01/businessTransactionStatement/confirm?businessIds="+businessIds);
					$("#searchForm").submit();
					loading("请勿刷新页面！正在确认结算记录，请稍等...");
				}
			},{buttonsFocus:1, closed:function(){
					if (typeof closed == 'function') {
						closed();
					}
				}});
			top.$('.jbox-body .jbox-icon').css('top','55px');

		});

	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	$(document).ready(function() {
		$("#exportSubmit").on('click',function() {
			$("#searchForm").prop("action","${ctx}/doorOrder/v01/businessTransactionStatement/exportExcel");
			$("#searchForm").submit();
			$("#searchForm").prop("action","${ctx}/doorOrder/v01/businessTransactionStatement/list");
		});
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/doorOrder/v01/businessTransactionStatement/">
				<spring:message code="report.businessTransactionStatement.list" />
			</a>
		</li>
		<shiro:hasPermission name="doorOrder:v01:businessTransactionStatement:edit">
			<li>
				<a href="${ctx}/doorOrder/v01/businessTransactionStatement/form">
					<spring:message	code="report.businessTransactionStatement.form" />
				</a>
			</li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm"
		modelAttribute="businessTransactionStatement"
		action="${ctx}/doorOrder/v01/businessTransactionStatement/"
		method="post" class="breadcrumb form-search">
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div class="row search-flex" style="padding-left: 10px">
			<%--存款批次--%>
			<div>
				<label><spring:message code="report.businessTransactionStatement.inBatch" />：</label>
				<form:input path="inBatch" htmlEscape="false" maxlength="32"
					class="input-small" />
			</div>
			<%--存款机ID--%>
			<div>
				<label><spring:message	code="report.businessTransactionStatement.equipmentInfoId" />：</label>
				<form:input path="equipmentInfo.id" htmlEscape="false"
					maxlength="32" class="input-small" />
			</div>
			<%--店员编号--%>
			<div>
				<label><spring:message	code="report.businessTransactionStatement.loginName" />：</label>
				<form:input path="user.loginName" htmlEscape="false" maxlength="32"
					class="input-small" />
			</div>
			<%--装运单号--%>
			<div>
				<label><spring:message	code="report.businessTransactionStatement.remarks" />：</label>
				<form:input path="remarks" htmlEscape="false" maxlength="32"
					class="input-small" />
			</div>
			<%--确认状态--%>
			<div>
				<label><spring:message code="report.businessTransactionStatement.confirm" />：</label>
				<form:select path="custConfirm" class="input-medium required"
							 id="custConfirm">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('CUST_CONFIRM_STATUS')}"
								  itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<%--仓库--%>
			<div style="display: none;">
				<label><spring:message code="report.businessTransactionStatement.door" />：</label>
				<sys:treeselect id="door" name="doorId"
					value="${businessTransactionStatement.door.id}"
					labelName="door.name"
					labelValue="${businessTransactionStatement.door.name}" title="仓库"
					url="/sys/office/treeData" cssClass="required input-small"
					notAllowSelectParent="true" notAllowSelectRoot="false" minType="8"
					maxType="9" isAll="true" allowClear="true"
					checkMerchantOffice="true" clearCenterFilter="true" />

			</div>
			<%-- 存款日期 --%>
			<br />
			<div>
				<label><spring:message code="report.businessTransactionStatement.inStartDate" />：</label> <input id="inStartDate" name="inStartDate"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate inDate"
					value="<fmt:formatDate value="${businessTransactionStatement.inStartDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'inEndDate\')||\'%y-%M-%d\'}'});" />
			</div>
			<div>
			<label>~</label>
				<input id="inEndDate" name="inEndDate" type="text"
					readonly="readonly" maxlength="20" class="input-small Wdate inDate"
					value="<fmt:formatDate value="${businessTransactionStatement.inEndDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'inStartDate\')}',maxDate:'%y-%M-%d'});" />

			</div>
			<!-- 上门收款日期 -->
			<div>
				<label><spring:message	code="report.businessTransactionStatement.backStartDate" />：</label> <input id="backStartDate"
					name="backStartDate" type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate backDate"
					value="<fmt:formatDate value="${businessTransactionStatement.backStartDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'backEndDate\')||\'%y-%M-%d\'}'});" />
			</div>
			
			<div>
			<label>~</label>
				<input id="backEndDate" name="backEndDate" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate backDate"
					value="<fmt:formatDate value="${businessTransactionStatement.backEndDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'backStartDate\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<!-- 清分日期 -->
			<div>
				<label><spring:message	code="report.businessTransactionStatement.clearStartDate" />：</label> <input id="clearStartDate"
					name="clearStartDate" type="text" readonly="readonly"
					maxlength="20" class="input-small Wdate clearDate"
					value="<fmt:formatDate value="${businessTransactionStatement.clearStartDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'clearEndDate\')||\'%y-%M-%d\'}'});" />
			</div>
			
			<div>
			<label>~</label>
				<input id="clearEndDate" name="clearEndDate" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate clearDate"
					value="<fmt:formatDate value="${businessTransactionStatement.clearEndDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'clearStartDate\')}',maxDate:'%y-%M-%d'});" />
			</div>&nbsp;&nbsp;
			<div>
				<!-- 查询 -->
				<!-- 页面样式统一 屏蔽ul li -->
				<!-- <ul class="ul-form">
					<li class="btns"> -->
					<input id="btnSubmit" class="btn btn-primary"
						type="submit" value="查询" /></li>
					<!-- <li class="clearfix"></li>
				</ul> -->
			</div>&nbsp;&nbsp;
			<shiro:hasPermission name="doorOrder:v01:businessTransactionStatement:confirm">
				<div>
					<input id="confirm" class="btn btn-red" type="button" value="确认" />
				</div>&nbsp;&nbsp;
			</shiro:hasPermission>
			<div>
				<%-- 导出 --%>
				<input id="exportSubmit" class="btn btn-red" type="button"
					value="<spring:message code='common.export'/>" />
				<!-- </a> -->
			</div>&nbsp;&nbsp;
		</div>
		<!--  </div> -->
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
		<table id="contentTable" class="table  table-hover" style="overflow: scroll;width: 100%;">
			<thead>
				<tr>
					<th><input type="checkbox" name="chooseAll" value="" id="chooseAll"/></th>
					<!--  -->
					<th><spring:message code="common.seqNo" /></th>
					<!-- 存款机ID -->
					<th class="sort-column eqp.series_number"><spring:message code="report.businessTransactionStatement.equipmentInfoId" /></th>
					<!-- 仓库 -->
					<th class="sort-column door.name"><spring:message code="report.businessTransactionStatement.door" /></th>
					<!-- 存款批次 -->
					<th class="sort-column a.in_batch"><spring:message code="report.businessTransactionStatement.inBatch" /></th>
					<!-- 存款日期 -->
					<th class="sort-column a.in_date"><spring:message code="report.businessTransactionStatement.inStartDate" /></th>
					<!-- 开始时间 -->
					<th class="sort-column a.start_time"><spring:message code="report.businessTransactionStatement.startTime" /></th>
					<!-- 结束时间 -->
					<th class="sort-column a.end_time"><spring:message code="report.businessTransactionStatement.endTime" /></th>
					<!-- 耗时 -->
					<th><spring:message code="report.businessTransactionStatement.costTime" /></th>
					<!-- 店员编号 -->
					<th class="sort-column u9.login_name"><spring:message code="report.businessTransactionStatement.loginName" /></th>
					<!-- 店员姓名 -->
					<th class="sort-column u9.name"><spring:message code="report.businessTransactionStatement.userName" /></th>
					<!-- 装运单号 -->
					<th class="sort-column a.remarks"><spring:message code="report.businessTransactionStatement.remarks" /></th>
					<!-- 自助存款金额 -->
					<th class="sort-column a.cash_amount"><spring:message code="report.businessTransactionStatement.cashAmount" /></th>
					<!-- 强制存款金额 -->
					<th class="sort-column a.pack_amount"><spring:message code="report.businessTransactionStatement.packAmount" /></th>
					<!-- 总金额 -->
					<th class="sort-column a.total_amount"><spring:message code="report.businessTransactionStatement.totalAmount" /></th>
					<!-- 上门收款日期 -->
					<th class="sort-column a.back_date"><spring:message code="report.businessTransactionStatement.backStartDate" /></th>
					<!-- 清分日期 -->
					<th class="sort-column a.clear_date"><spring:message code="report.businessTransactionStatement.clearStartDate" /></th>
					<!-- 实际清点金额 -->
					<th class="sort-column a.real_clear_amount"><spring:message code="report.businessTransactionStatement.realClearAmount" /></th>
					<!-- 长款金额 -->
					<th class="sort-column a.long_currency_money"><spring:message code="report.businessTransactionStatement.longCurrencyMoney" /></th>
					<!-- 短款金额 -->
					<th class="sort-column a.short_currency_money"><spring:message code="report.businessTransactionStatement.shortCurrencyMoney" /></th>
					<!-- 差错处理情况 -->
					<th class="sort-column a.error_check_condition"><spring:message code="report.businessTransactionStatement.errorCheckCondition" /></th>
					<!-- 客户确认状态 -->
					<th class="sort-column a.cust_confirm"><spring:message code="report.businessTransactionStatement.custConfirm" /></th>
					<!-- 操作 -->
					<shiro:hasPermission
						name="doorOrder:v01:businessTransactionStatement:edit">
						<th><spring:message code="report.businessTransactionStatement.do" /></th>
					</shiro:hasPermission>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="businessTransactionStatement"
					varStatus="status">
					<tr>

							<td>
								<c:if test="${businessTransactionStatement.custConfirm eq '0'}">
									<input type="checkbox" name="businessIds" value="${businessTransactionStatement.id}"/>
								</c:if>
							</td>
							<%-- 序号 --%>
							<td>${status.index+1}</td>
							<%-- 设备序列号 --%>
							<td>${businessTransactionStatement.equipmentInfo.seriesNumber}</td>
							<%-- 仓库名称--%>
							<td>${businessTransactionStatement.door.name}</td>
							<%-- 存款结算批次 --%>
							<td>${businessTransactionStatement.inBatch}</td>
							<%-- 存入日期 --%>
							<td><fmt:formatDate
									value="${businessTransactionStatement.inDate}"
									pattern="yyyy-MM-dd" /></td>
							<%--开始时间--%>
							<td><fmt:formatDate
									value="${businessTransactionStatement.startTime}"
									pattern="HH:mm:ss" /></td>
							<%--结束时间--%>
							<td><fmt:formatDate
									value="${businessTransactionStatement.endTime}"
									pattern="HH:mm:ss" /></td>
							<%--耗时--%>
							<td>${businessTransactionStatement.costTime}</td>
							<%--存款人编号（登录名）--%>
							<td>${businessTransactionStatement.user.loginName}</td>
							<%--存款人姓名--%>
							<td>${businessTransactionStatement.user.name}</td>
							<%--装运单号（存款备注）--%>
							<td>${businessTransactionStatement.remarks}</td>
							<%--速存金额--%>
							<td><c:choose>
									<c:when
										test="${businessTransactionStatement.cashAmount==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.cashAmount}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose></td>
							<%--强存金额--%>
							<td><c:choose>
									<c:when
										test="${businessTransactionStatement.packAmount==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.packAmount}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose></td>
							<%--存入总金额--%>
							<td class="text-green"><c:choose>
									<c:when
										test="${businessTransactionStatement.totalAmount==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.totalAmount}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose>
							<td>
								<%--上门收款日期（清机日期）--%> <fmt:formatDate
									value="${businessTransactionStatement.backDate}"
									pattern="yyyy-MM-dd" />
							</td>
							<%--清分日期--%>
							<td><fmt:formatDate
									value="${businessTransactionStatement.clearDate}"
									pattern="yyyy-MM-dd" /></td>
							<%--实际清点金额--%>
							<td><c:choose>
									<c:when
										test="${businessTransactionStatement.realClearAmount==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.realClearAmount}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose></td>
							<%--长款金额--%>
							<td><c:choose>
									<c:when
										test="${businessTransactionStatement.longCurrencyMoney==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.longCurrencyMoney}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose></td>
							<%--短款金额--%>
							<td><c:choose>
									<c:when
										test="${businessTransactionStatement.shortCurrencyMoney==null}">
										<fmt:formatNumber value="0" pattern="#,##0.00#" />
									</c:when>
									<c:otherwise>
										<fmt:formatNumber
											value="${businessTransactionStatement.shortCurrencyMoney}"
											pattern="#,##0.00#" />
									</c:otherwise>
								</c:choose></td>
							<%--差错处理情况--%>
							<td class="${fn:contains(businessTransactionStatement.errorCheckCondition,'未处理') ? 'text-red':''}">
								  ${businessTransactionStatement.errorCheckCondition}
							</td>
							<%--客户确认状态--%>
							<td>
								${fns:getDictLabelWithCss(businessTransactionStatement.custConfirm, "CUST_CONFIRM_STATUS", "未发生长短款", true)}
							</td>
							<shiro:hasPermission
									name="doorOrder:v01:businessTransactionStatement:edit">
								<td><a href="${ctx}/doorOrder/v01/businessTransactionStatement/form?id=${businessTransactionStatement.id}"><i
									class="fa fa-edit text-green  fa-lg"></i></a></td>
							</shiro:hasPermission>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>