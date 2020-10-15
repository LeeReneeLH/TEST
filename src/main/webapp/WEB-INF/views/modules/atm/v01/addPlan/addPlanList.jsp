<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='title.atm.plan.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//setToday(".createTime");		
		});		
		// 打印明细
		function print(addPlanId,amount,boxNum,addCashGroupName){
			var content = "iframe:${ctx}/atm/v01/atmPlanInfo/print?addPlanId="+addPlanId+"&amount="+amount+"&boxNum="+boxNum+"&addCashGroupName="+addCashGroupName;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 1100, 700, {
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
									"overflow-y", "hidden");
						}
					});
		}
		
		/* 追加查看加钞组明细详情 修改人：wanglu 修改时间：2017-11-16 begin */
		function showDetail(groupId) {
			//var content = "iframe:${ctx}/store/v01/stoBoxInfo/getStoBoxDetail?boxNo ="+$(this).parent().siblings(1).text();
			var content = "iframe:${ctx}/atm/v01/atmPlanInfo/getAddCashGroupDetail?groupId="+groupId;
			top.$.jBox.open(
					content,
					"查看详情", 800, 700, {
						buttons : {
							//关闭
							"<spring:message code='common.close' />" : true
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/atm/v01/atmPlanInfo/importFile"><spring:message code='label.atm.addPlan.menu.01'/></a></li>
		<li class="active"><a href="${ctx}/atm/v01/atmPlanInfo/list"><spring:message code='label.atm.addPlan.menu.02'/></a></li>
	</ul>
	<sys:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="atmPlanInfo" action="${ctx}/atm/v01/atmPlanInfo/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}" callback="page();"/>
		<!-- 设备编号 -->
		<label><spring:message code='label.atm.add.plan.atm.no'/>：</label><form:input path="atmNo" htmlEscape="false" maxlength="30" class="input-small"/>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${atmPlanInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${atmPlanInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th ><spring:message code="common.seqNo"/></th>
				<!-- 计划ID -->
				<th ><spring:message code='label.atm.add.plan.id'/></th>
				<!-- 计划名称 -->
				<th ><spring:message code='label.atm.add.plan.title.name'/></th>
				<!-- 加钞组名称 -->
				<th ><spring:message code='label.atm.add.plan.group.name'/></th>
				<!-- ATM机数量 -->
				<th ><spring:message code='label.atm.add.plan.atm.num'/></th>
				<!-- 加钞总金额 -->
				<th ><spring:message code='label.atm.add.plan.amount'/><spring:message code='label.atm.add.money.unit'/></th>
				<!-- 钞箱数量 -->
				<th ><spring:message code='label.atm.add.plan.box.num'/></th>
				<!-- 计划状态 -->
				<th ><spring:message code='label.atm.add.plan.status'/></th>
				<!-- 所属机构 -->
				<th ><spring:message code='store.affiliatedOffice'/></th>
				<!-- 创建日期 -->
				<th ><spring:message code='label.atm.add.plan.create'/></th>
				<shiro:hasPermission name="atm:atmPlanInfo:edit">
					<!-- 操作 -->
					<th ><spring:message code='common.operation'/></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="addPlanItem" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td><a href="${ctx}/atm/v01/atmPlanInfo/addPlanView?addPlanId=${addPlanItem.addPlanId}&amount=${addPlanItem.addAmount}&boxNum=${addPlanItem.boxNum}&addCashGroupName=${addPlanItem.addCashGroupName}">${addPlanItem.addPlanId}</a></td>
				<td>${addPlanItem.addPlanName}</td>
				<td>
					<%-- <c:if test="${addPlanItem.addCashGroupName != '' && addPlanItem.addCashGroupName != null}"> --%>
					<a href="###" onclick="showDetail('${addPlanItem.addCashGroupId}')">${addPlanItem.addCashGroupName}</a>
					<%-- </c:if> --%>
				</td>
				<td>${addPlanItem.atmNum}</td>
				<td><fmt:formatNumber value="${addPlanItem.addAmount}" pattern="#,###"/></td>
				<td>${addPlanItem.boxNum}</td>
				<td>${fns:getDictLabel(addPlanItem.status,'atm_plan_status',"")}</td>
				<td>${fns:getOfficeName(addPlanItem.office.id).name}</td>
				<td><fmt:formatDate value="${addPlanItem.createDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				<shiro:hasPermission name="atm:atmPlanInfo:edit">
					<td>
					<!-- 修改		修改人：wxz 2017-12-12 -->
					<a href="${ctx}/atm/v01/atmPlanInfo/edit?addPlanId=${addPlanItem.addPlanId}" onclick="return confirmx('确认要修改该加钞计划吗？', this.href)" title="<spring:message code='common.modify'/>"><i class="fa fa-edit text-green fa-lg"></i></a>
					<!-- 删除 -->
					<c:if test="${addPlanItem.status==0}">
						<a href="${ctx}/atm/v01/atmPlanInfo/delete?addPlanId=${addPlanItem.addPlanId}" onclick="return confirmx('确认要删除该加钞计划吗？', this.href)" title="<spring:message code='common.delete'/>">
							<i class="fa fa-trash-o text-red fa-lg"></i>
						</a>
					</c:if>
					<!-- 打印 -->
						<a href="###" onclick="print('${addPlanItem.addPlanId}','${addPlanItem.addAmount}','${addPlanItem.boxNum}','${addPlanItem.addCashGroupName}')"  title="<spring:message code='common.print'/>">
							<i class="fa fa-print text-yellow fa-lg"></i>
						</a>
						<a href="${ctx}/atm/v01/atmPlanInfo/bindingAddCashGroupForm?addPlanId=${addPlanItem.addPlanId}&amount=${addPlanItem.addAmount}&boxNum=${addPlanItem.boxNum}&addCashGroupName=${addPlanItem.addCashGroupName}&addCashGroupId=${addPlanItem.addCashGroupId}" title="绑定加钞组">
							<i class="fa  fa-user fa-lg"></i>
						</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>