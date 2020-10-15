<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="door.doorOrderException.title" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {

	});
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	
	//耗时显示
	$().ready(function(){
		$("tbody").find("tr").each(function(){
			var aa=$.trim($(this).children('td:eq(14)').text());
			var costTime = getCostTime(aa);
				$(this).children('td:eq(14)').text(costTime);
		});
	});
	
	//消耗时间
	function getCostTime(aa) { 
		/* var dateLeft = 0;
		var hourLeft = 0;
		var minuteLeft = 0;
		var secondLeft = 0; */
		var timeLeft = [0,0,0,0];
		var dateStr = "";
		var ts = parseInt(aa / 1);
		timeLeft[0] = (ts > 86400) ? parseInt(ts / 86400) : 0;
		ts = ts - timeLeft[0] * 86400;
		timeLeft[1] = (ts > 3600) ? parseInt(ts / 3600) : 0;
		ts = ts - timeLeft[1] * 3600;
		timeLeft[2] = (ts > 60) ? parseInt(ts / 60) : 0;
		timeLeft[3] = ts - timeLeft[2] * 60;
		timeStr = (timeLeft[0] > 0) ? timeLeft[0] + "天" : "";
		timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0) ? "" : (timeLeft[1] + "小时");
		timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0 && timeLeft[2] <= 0) ? "" : (timeLeft[2] + "分钟");
		timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0 && timeLeft[2] <= 0 && timeLeft[3] <= 0) ? "" : (timeLeft[3] + "秒");
    	return timeStr; 
    } 
	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a
			href="${ctx}/doorOrder/v01/doorOrderException/"><spring:message code="door.doorOrderException.list" /></a></li>
		<shiro:hasPermission name="doorOrder:v01:doorOrderException:edit">
			<li><a href="${ctx}/doorOrder/v01/doorOrderException/form"><spring:message code="door.doorOrderException.form" /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="doorOrderException"
		action="${ctx}/doorOrder/v01/doorOrderException/" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<!-- <div class="span14" style="margin-top: 5px"> -->
			<div>
				<!-- 机具编号-->
				<label><spring:message code="door.doorOrderException.eqpId" />：</label>
				<form:input path="eqpId" htmlEscape="false" maxlength="32"
					class="input-small" />
			</div>
			<div>
				<!-- 凭条号-->
				<label><spring:message code="door.doorOrderException.tickerTape" />：</label>
				<form:input path="tickerTape" htmlEscape="false" maxlength="32" class="input-small" />
			</div>
			<div>
				<!-- 款袋编号-->
				<label><spring:message code="door.doorOrderException.bagNo" />：</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="32" class="input-small" />
			</div>
			<div>
				<%-- 状态 --%>
				<label><spring:message code="common.status" />：</label>
				<form:select path="status" class="input-medium required"
					id="selectStatus">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('exception_status')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<div>
				<%-- 更换日期 --%>
				<label><spring:message code="door.doorOrderException.timeStart" />：</label> 
				<input id="createTimeStart"
					name="createTimeStart" type="text" readonly="readonly"
					maxlength="20" class="input-small Wdate createTime"
					value="<fmt:formatDate value="${doorOrderException.createTimeStart}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<div>
				<%-- 结束日期 --%>
				<label>~</label> 
				<input id="createTimeEnd" name="createTimeEnd"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${doorOrderException.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<div>
				<!-- 所属公司 -->
				<label><spring:message code="door.doorOrderException.office" />：</label>
				<sys:treeselect id="doorName" name="doorId"
					value="${doorOrderException.doorId}" labelName="doorName"
					labelValue="${doorOrderException.doorName}"
					title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData" cssClass="required input-small"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="8"
					maxType="9" isAll="true" allowClear="true"
					checkMerchantOffice="true" clearCenterFilter="true" />
			</div>
			<div>
				<%-- 备注 --%>
				<label><spring:message code="door.historyChange.remarks" />：</label>
				<form:input path="remarks" value="" type="text" maxlength="32" class="input-small"/>
			</div>
			&nbsp;&nbsp;
			<div>
				<%-- 查询 --%>
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.search'/>" />
			</div>
		</div>
	</form:form>
	<sys:message content="${message}" />
    <div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<th class="sort-column a.ticker_tape"><spring:message code="door.doorOrderException.tickerTape" /></th>
				<th class="sort-column a.eqp_id"><spring:message code="door.doorOrderException.eqpId" /></th>
				<th class="sort-column a.bag_no"><spring:message code="door.doorOrderException.bagNo" /></th>
				<th class="sort-column a.status"><spring:message code="door.doorOrderException.status" /></th>
				<th class="sort-column a.total_amount"><spring:message code="door.doorOrderException.totalAmount" /></th>
				<th class="sort-column a.business_type"><spring:message code="door.doorOrderException.businessType" /></th>
				<th class="sort-column a.currency"><spring:message code="door.doorOrderException.currency" /></th>
				<th class="sort-column u9.name"><spring:message code="door.doorOrderException.user" /></th>
				<th class="sort-column a.exception_reason"><spring:message code="door.doorOrderException.exceptionReason" /></th>
				<th class="sort-column a.create_date"><spring:message code="door.doorOrderException.timeStart" /></th>
				<th class="sort-column so.name"><spring:message code="door.doorOrderException.office" /></th>
				<th class="sort-column a.start_time"><spring:message code="door.doorOrderException.startTime" /></th>
				<th class="sort-column a.end_time"><spring:message code="door.doorOrderException.endTime" /></th>
				<th><spring:message code="door.doorOrderException.costTime" /></th>
				<th class="sort-column a.remarks"><spring:message code="door.historyChange.remarks" /></th>
				<shiro:hasPermission name="doorOrder:v01:doorOrderException:edit">
					<th><spring:message code="door.doorOrderException.operate" /></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="doorOrderException" varStatus="status">
				<tr>
					<td>${status.index + 1}</td>
					<td><a
						href="${ctx}/doorOrder/v01/doorOrderException/detailForm?id=${doorOrderException.id}&pageFlag=modify">
							${doorOrderException.tickerTape} </a></td>
					<td>${doorOrderException.seriesNumber}</td>
					<td>${doorOrderException.bagNo}</td>

					<c:if test="${doorOrderException.status eq '2'}">
						<td style="color: green">
							${fns:getDictLabel(doorOrderException.status, 'exception_status', '')}
						</td>
					</c:if>
					<c:if test="${doorOrderException.status eq '0'}">
						<td style="color: black">
							${fns:getDictLabel(doorOrderException.status, 'exception_status', '')}
						</td>
					</c:if>
					<c:if test="${doorOrderException.status eq '1'}">
						<td style="color: red">
							${fns:getDictLabel(doorOrderException.status, 'exception_status', '')}
						</td>
					</c:if>
					<c:if test="${doorOrderException.status ==null}">
						<td></td>
					</c:if>
					<td>${doorOrderException.totalAmount}</td>
					<td>${doorOrderException.businessTypeName}</td>
					<%-- <td>${doorOrderException.currency}</td> --%>
					<td>人民币</td>
					<td>${doorOrderException.userName}</td>
					<td>${doorOrderException.exceptionReason}</td>
					<td><fmt:formatDate value="${doorOrderException.createDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${doorOrderException.doorName}</td>
					<td><fmt:formatDate value="${doorOrderException.startTime}"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${doorOrderException.endTime}"
										pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${doorOrderException.costTime}</td>
					<td>${doorOrderException.remarks}</td>


					<shiro:hasPermission name="doorOrder:v01:doorOrderException:edit">
						<td width="60px"><c:if test="${doorOrderException.status eq '1'}">
								<a
									href="${ctx}/doorOrder/v01/doorOrderException/detailUpdate?id=${doorOrderException.id}&pageFlag=modify"
									title="<spring:message code='door.doorOrderException.exceptionHandling' />"><i class="fa fa-edit text-green fa-lg"></i></a>
								<a href="${ctx}/doorOrder/v01/doorOrderException/delete?id=${doorOrderException.id}&pageFlag=modify"
									title="<spring:message code='common.delete' />" onclick="return confirmx('确认要删除吗？', this.href)">
									<i class="fa fa-trash-o text-red fa-lg"></i></a>
							</c:if></td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>