<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 钞箱入库列表 -->
	<title>钞箱入库列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v01/AtmBoxHandIn/list");
				$("#searchForm").submit();
			});
		});
		
		//查看详细
		function showDetail(allId) {
			//var content = "iframe:${ctx}/store/v01/stoBoxInfo/getStoBoxDetail?boxNo ="+$(this).parent().siblings(1).text();
			var content = "iframe:${ctx}/allocation/v01/AtmBoxHandIn/getAllAllocateDetail?allId="+allId;
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
	<!-- onclick="javascript:return false; -->
		<!-- 钞箱入库列表 -->
		<li class="active"><a href="${ctx}/allocation/v01/AtmBoxHandIn/list">钞箱入库列表</a></li>
	</ul>
	<div class="row">
	<%-- <form:form id="updateForm" modelAttribute="updateParam" action="" method="post" ></form:form> --%>
		<form:form id="searchForm" modelAttribute="allAllocateInfo" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
				<!-- 业务状态 -->
				<label>业务状态：</label>
						<form:select path="status" id="boxType" class="input-medium">
							<form:option value="">
							<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getFilterDictList('ATM_BUSSINESS_STATUS', true, '')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					<!-- 开始日期 -->
					<label><spring:message code="common.startDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${allAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
					<!-- 结束日期 -->
					<label><spring:message code="common.endDate" />：</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${allAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
					&nbsp;&nbsp;&nbsp;
					<!-- 查询 -->
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>

		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 流水单号 -->
				<th class="sort-column a.all_id">流水单号</th>
				<!-- 业务类型 -->
				<th class="sort-column a.business_type">业务类型</th>
				<!-- 交接数量 -->
				<th class="sort-column a.register_number">交接数量</th>
				<!-- 业务状态 -->
				<th class="sort-column a.status">业务状态</th>
				<!-- 加钞计划ID -->
				<th class="sort-column a.route_id">加钞计划ID</th>
				<!-- 移交人-->
				<th class="sort-column a.store_handover_id">移交人</th>
				<!-- 接收人 -->
				<th class="sort-column a.point_handover_id">接收人</th>
				<!-- 登记机构 -->
				<th class="sort-column a.rOffice_name">登记机构</th>	
				<!-- 登记时间 -->
				<th class="sort-column a.create_date">登记时间</th>
				<!-- 交接时间 -->
				<th >交接时间 </th>
				<!-- 操作（冲正/打印） -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="allAllocateInfo" varStatus="statusIndex">
			<tr>
				<!-- 流水单号 -->
				<td>
					${allAllocateInfo.allId}
				</td>
				<!-- 业务类型 -->
				<td>
					${fns:getDictLabel(allAllocateInfo.businessType,'all_businessType',"")}
				</td>
				<!-- 交接数量  -->
				<td  style="text-align:right;">${allAllocateInfo.registerNumber}</td>
				<!-- 业务状态 -->
				<td>${fns:getDictLabel(allAllocateInfo.status,'ATM_BUSSINESS_STATUS',"")}</td>			
				<!-- 加钞计划ID -->
				<td>${allAllocateInfo.routeId}</td>
				<!-- 移交人 -->
				<td>${allAllocateInfo.allAllocateDetail.handoverUserName}</td>
				<!-- 接收人 -->
				<td>${allAllocateInfo.allAllocateDetail.escortUserName}</td>
				<!-- 登记机构 -->
				<td>${allAllocateInfo.rOffice.name}</td>
				<!-- 登记时间 -->
				<td><fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<!-- 交接时间 -->
				<td><fmt:formatDate value="${allAllocateInfo.storeHandover.acceptDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<!-- 操作 -->
				<td>
				<span style='width:30px;display:inline-block;'> 
				<shiro:hasPermission name="allocation:atmhandin:view">
				<!-- 查看明细 -->
				<a href="#" onclick="showDetail('${allAllocateInfo.allId}');javascript:return false;" title="查看明细"><i class="fa fa-eye fa-lg"></i></a>
				</shiro:hasPermission>
				</span>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>