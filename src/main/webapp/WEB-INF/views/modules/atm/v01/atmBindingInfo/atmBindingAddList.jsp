<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='title.add.clear.searsh.manager'/></title><!-- 清机加钞查询管理 -->
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//setToday(".createTime");	
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		// 显示明细
		function displayDetail(bindingId,type) {
			var content ="iframe:${ctx}/atm/v01/atmBindingInfo/view?bindingId="+bindingId+"&type="+type;
			var title="";
			if (type) {
				title="<spring:message code='atm.add.detail'/>";//加钞明细
			}else{
				title="<spring:message code='atm.clear.detail'/>";//清点明细
			}
			top.$.jBox.open(
					content,
					//显示明细
					title,1100, 600, {
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
	</script>
	<style>
	.dropdown-menu{top:30px !important;right:0 !important;left: auto !important;min-width:110px !important;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/atm/v01/atmBindingInfo/addList"><spring:message code='atm.add.cash.binding.list'/></a></li><!-- 加钞绑定列表 -->
		<li><a href="${ctx}/atm/v01/atmBindingInfo/form"><spring:message code='atm.add.binding.register'/></a></li><!-- 加钞绑定登记 -->
	</ul>
	<form:form id="searchForm" modelAttribute="atmBindingInfo" action="${ctx}/atm/v01/atmBindingInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="isSearch" name="isSearch" type="hidden" value="true"/>
		<!-- 添加查询信息 	修改人：wxz 2017-11-23 begin -->
		
		<!-- 加钞计划ID -->
		<label><spring:message code='label.atm.add.plan.id'/>：</label>
		<form:input path="addPlanId" htmlEscape="false" maxlength="20" class="input-medium"/>
		<!-- 设备名称 -->
		<label><spring:message code='label.atm.machine.name'/>：</label>
		<form:select path="atmNo" class="input-xlarge">
		    <form:option value=""><spring:message code="common.select"/></form:option>
			<form:options items="${atmInfoList}"
				itemLabel="atmTypeName" itemValue="atmId" htmlEscape="false" />
		</form:select>
		<!-- end -->
		
		<%-- 开始日期 --%>
		<label><spring:message code="common.startDate"/>：</label>
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${atmBindingInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<%-- 结束日期 --%>
		<label><spring:message code="common.endDate"/>：</label>
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${atmBindingInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code='common.seqNo'/></th><!-- 序号 -->
				<th><spring:message code='label.atm.add.plan.atm.no'/></th><!-- 设备编号 -->
				<th><spring:message code='label.atm.machine.name'/></th><!-- 设备名称 -->
				<th><spring:message code='label.atm.add.plan.id'/></th><!-- 加钞计划ID -->
				<th><spring:message code='label.atm.add.plan.account.no'/></th><!-- 柜员编号 -->
				<th><spring:message code='label.atm.day.amount'/><spring:message code="common.units.yuan.alone" /></th><!-- 加钞金额 -->
				<th><spring:message code='label.atm.add.date'/></th><!-- 加钞时间 -->
				<th><spring:message code='label.atm.clear.amount'/><spring:message code="common.units.yuan.alone" /></th><!-- 清机金额 -->
				<th><spring:message code='label.atm.clear.date'/></th><!-- 清机时间 -->
				<th><spring:message code='label.atm.binding.data.type'/></th><!-- 数据来源 -->
				<%-- <th><spring:message code='common.status'/></th><!-- 状态 --> --%>
				<th><spring:message code='common.operation'/></th><!-- 操作 -->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmBindingInfo" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${atmBindingInfo.atmNo}</td>
				<td>${atmBindingInfo.atmTypeName}</td>
				<td>${atmBindingInfo.addPlanId}</td>
				<td>${atmBindingInfo.atmAccount}</td>
				<td><fmt:formatNumber value="${atmBindingInfo.addAmount}" pattern="#,##0.00#" /></td>
				<td><fmt:formatDate value="${atmBindingInfo.addDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatNumber value="${atmBindingInfo.amount}" pattern="#,##0.00#" /></td>
				<td><fmt:formatDate value="${atmBindingInfo.clearDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
					${fns:getDictLabel(atmBindingInfo.dataType,'atm_binding_info_data_type',"")}
				</td>
				<%-- <td>${fns:getDictLabel(atmBindingInfo.status,'atm_binding_info_status',"")}</td> --%>
				<shiro:hasPermission name="atm:v01:atmBindingInfo:edit">
					<td>
						<ul class="nav" style="float:left;">
						<li class="dropdown">
			       			<a href="" title = "<spring:message code='common.view'/>" id="" href="#" class="dropdown-toggle" data-toggle="dropdown">
								<i class="fa fa-eye fa-lg"></i>
							</a>
					    	<ul class="dropdown-menu">
					          	<!-- 加钞明细 -->
								<li><a href="#" onclick="displayDetail('${atmBindingInfo.bindingId}',true);"><spring:message code='atm.add.detail'/></a></li>
								<!-- 清机明细 -->
								<li><a href="#" onclick="displayDetail('${atmBindingInfo.bindingId}',false);"><spring:message code='atm.clear.detail'/></a></li>
				      		</ul>
			        	</li>
		        		</ul>
		        		<c:if test="${atmBindingInfo.dataType == '1' }">
						<a href="${ctx}/atm/v01/atmBindingInfo/form?bindingId=${atmBindingInfo.bindingId}" title = "<spring:message code='common.edit'/>" style="float:right;">
							<i class="fa fa-edit text-green fa-lg"></i>
						</a>
		        		</c:if>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>