<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="allocation.cash.box.handin" /></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
	// 显示撤回原因
	function displayCancelReason(allId) {
		var content = "iframe:${ctx}/allocation/v01/boxHandover/displayCancelReason?allId=" + allId;
		top.$.jBox.open(
				content,
				"撤回原因", 750, 460, {
					buttons : {
						//关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
					}
		});
	}
	// 打印明细
	function printDetail(allID){
		var content = "iframe:${ctx}/allocation/v01/cashHandin/printDetail?allID=" + allID;
		top.$.jBox.open(
				content,
				//打印
				"<spring:message code='common.print' />", 860, 600, {
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
							return false;
						}
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
        <%-- 金库箱袋下拨列表 --%>
        <li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.cash.box.handin.list" /></a></li>
	</ul>

	<form:form id="searchForm" modelAttribute="allAllocateInfo" action="${ctx}/allocation/v01/boxHandover/handin" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 上缴机构 -->
		<label class="control-label"><spring:message
				code="allocation.cash.handin.office" />：</label>
		<sys:treeselect id="searchRoffice" name="searchRoffice.id"
			value="${allAllocateInfo.searchRoffice.id}" labelName="searchRoffice.name"
			labelValue="${allAllocateInfo.searchRoffice.name}" title="上缴机构"
			url="/sys/office/treeData" cssClass="required input-small"
			allowClear="true" notAllowSelectParent="false"
			notAllowSelectRoot="false" checkGroupOffice="true"
						    isAll="false"  />

		<c:if test="${officeType=='7'}">
				
				<!-- 接收机构 -->
			<label class="control-label"><spring:message
					code="allocation.instore.office" />：</label>
			<sys:treeselect id="aOffice" name="aOffice.id"
				value="${allAllocateInfo.aOffice.id}" labelName="aOffice.name"
				labelValue="${allAllocateInfo.aOffice.name}" title="接收机构"
				url="/sys/office/treeData" cssClass="required input-small"
				 notAllowSelectParent="false"
				notAllowSelectRoot="false" type="3"
						    isAll="true" allowClear="true" />
		</c:if>
		<%-- 业务状态 --%>
		<label><spring:message code="allocation.business.status"/>：</label>
		<form:select path="status" id="status" class="input-small">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getFilterDictList('all_status',true,'12,11,14,90')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		<%-- 查询按钮 --%>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 上缴机构 --%>
				<th><spring:message code="allocation.cash.handin.office" /></th>
				<%-- 上缴数量 --%>
				<th><spring:message code="allocation.cash.handin.number" /></th>
				<%-- 接收机构 --%>
				<th><spring:message code="allocation.accept.office" /></th>
				<%-- 接收数量 --%>
				<th><spring:message code="allocation.accept.number" /></th>
				<%-- 线路 --%>
				<th><spring:message code="allocation.route.name" /></th>
				<%-- 上缴人 --%>
				<th><spring:message code="allocation.cash.handin.name" /></th>
				<%-- 上缴金额 --%>
				<th><spring:message code="allocation.cash.handin.amount" /></th>
				<%-- 接收金额 --%>
				<th><spring:message code="allocation.confirm.amount" /></th>
				<%-- 上缴时间 --%>
				<th><spring:message code="allocation.cash.handin.date" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code='allocation.business.status'/></th>
				<%-- 操作（查看） --%>
				<th><spring:message code='common.operation' /></th>
				
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="allocation" varStatus="no">
			<tr>
		        <%-- 单号 --%>
				<td><a href = "${ctx}/allocation/v01/handInWorkFlow/findStatus?allId=${allocation.allId}&backFlag=toHandInList">${allocation.allId}</td>
				<%-- 上缴机构 --%>
				<td>${allocation.rOffice.name}</td>
				<%-- 上缴数量 --%>
				<td>${allocation.registerNumber}</td>
				<%-- 接收机构 --%>
				<td>${allocation.aOffice.name}</td>
				<%-- 接收数量 --%>
				<td>${allocation.acceptNumber}</td>
				<%-- 线路 --%>
				<td>${allocation.routeName}</td>
				<%-- 上缴人 --%>
				<td>${allocation.createName}</td>
				<%-- 上缴金额 --%>
				<td><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
				<%-- 接收金额 --%>
				<td><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td>
				<%-- 上缴日期 --%>
				<td><fmt:formatDate value="${allocation.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<%-- 状态 --%>
				<c:choose>
						<c:when test="${allocation.status == '90'}">
							<td>
								<a href="#" onclick="displayCancelReason('${allocation.allId}');javascript:return false;">
									${fns:getDictLabel(allocation.status,'all_status',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(allocation.status,'all_status',"")}</td>
						</c:otherwise>
					</c:choose>
				<td><a
					href="${ctx}/allocation/v01/cashHandin/form?allId=${allocation.allId}&pageType=storeView" title="查看">
						<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i>
				</a>
				<c:if test="${(allocation.status eq '14' or allocation.status eq '11') }">
										
										<!-- 打印 -->
							<a href="#" onclick="printDetail('${allocation.allId}');" title="<spring:message code='common.print'/>">
								<i class="fa fa-print text-yellow fa-lg"></i>
							</a>
										</c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
