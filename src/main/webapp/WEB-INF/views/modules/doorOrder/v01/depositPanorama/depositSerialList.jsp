<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.clearAddMoney.list' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	$(document).ready(function() {
		$("#exportSubmit").on('click',function(){
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositPanorama/depositSerialExport?equipmentId" + $("#equipmentId").val());
			$("#searchForm").submit();
			$("#searchForm").prop("action", "${ctx}/doorOrder/v01/depositPanorama/depositSerial?equipmentId" + $("#equipmentId").val());		
		});
	}); 
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	
	//凭条信息 弹窗
	function showTickrtapeInfo(id) {
		var content = "iframe:${ctx}/doorOrder/v01/depositPanorama/tickrtapeInfo?tickrtapeId=" + id;
		top.$.jBox.open(
			content,
			"凭条信息", 760, 430, {
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
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 设备缴存列表 -->
		<li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/depositPanorama/"><spring:message code="door.panorama.equipDeposit" /></a></li>
		<!-- 封包缴存列表 -->
		<li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/packageList" target="mainFrame"><spring:message code="door.panorama.otherDeposit" /></a></li> 
		<!-- 存款流水列表 -->
		<li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/depositPanorama/depositSerial?equipmentId=${clearAddMoney.equipmentId}"><spring:message code="door.panorama.deposit" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="clearAddMoney"
		action="${ctx}/doorOrder/v01/depositPanorama/depositSerial" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<input id="equipmentId" name="equipmentId" type="hidden" value="${clearAddMoney.equipmentId}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex" style="padding-left: 15px;">
				<%-- 款袋编号 --%>
				<div>
				<label><spring:message code="door.doorOrder.packNum" />：</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%-- 凭条号 --%>
				<div>
				<label><spring:message code='door.depositSerial.businessId' />：</label>
				<form:input path="businessId" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%-- 业务备注 --%>
				<div>
				<label><spring:message code='door.depositSerial.remarks' />：</label>
				<form:input path="remarks" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%-- 操作人 --%>
				<div>
				<label><spring:message code='door.depositSerial.updateName' />：</label>
				<form:input path="operator" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				
				<%-- 开始日期 --%>
				<div>
				<label><spring:message code="door.depositSerial.updateDate" />：</label> <input
					id="createTimeStart" name="createTimeStart" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${clearAddMoney.createTimeStart}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				</div>
				<%-- 结束日期 --%>
				<div>
				<label>~</label> 
				<input id="createTimeEnd" name="createTimeEnd" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${clearAddMoney.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				</div>
				<!-- 类型  -->
				<div>
				<label><spring:message code='clear.clearAddMoney.type' />：</label>
				<form:select path="type" maxlength="10" class="input-small required">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getFilterDictList('CLEAR_ADD_MONEY_TYPE',true,'0,1')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				</div>
				&nbsp;&nbsp;
				<!-- 查询 -->
				<div>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
				</div>
				&nbsp;&nbsp;
				<!-- 导出 -->
				<div>
				<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
				</div>
				<div>
				<!-- 返回 -->
				&nbsp;
				<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/doorOrder/v01/depositPanorama/'"/>
				</div>
		</div>
	</form:form>
	<sys:message content="${message}" />
	<div class="table-con">
	<table id="contentTable" class="table  table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='clear.clearAddMoney.indexCode' /></th>
				<!-- 门店 -->
				<th ><spring:message code='clear.clearAddMoney.doorName' /></th>
				<!-- 机具名称 -->
				<th ><spring:message code='clear.clearAddMoney.equipmentName' /></th>
				<!-- 凭条号 -->
                <th class="sort-column a.business_id" ><spring:message code='door.depositSerial.businessId' /></th>
				<!-- 款袋编号 -->
				<th class="sort-column a.bag_no" ><spring:message code='clear.clearAddMoney.bagNo' /></th>
				<!-- 累计总张数 -->
				<th ><spring:message code='door.depositSerial.totalCount' /></th>
				<!-- 本次金额 -->
				<th class="sort-column a.amount" ><spring:message code='door.depositSerial.amount' /></th>
				<!-- 类型 -->
				<th class="sort-column a.type" ><spring:message code='clear.clearAddMoney.type' /></th>
				<!-- 余额 -->
				<th class="sort-column a.surplus_amount" ><spring:message code='clear.clearAddMoney.surplusAmount' /></th>
				<!-- 业务备注-->
				<th class="sort-column remarks" ><spring:message code='door.depositSerial.remarks' /></th>
				<!-- 操作人-->
				<th class="sort-column a.update_name" ><spring:message code='door.depositSerial.updateName' /></th>
				<!-- 操作时间 -->
				<th class="sort-column a.update_date" ><spring:message code='door.depositSerial.updateDate' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clearAddMoney"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${clearAddMoney.doorName}</td>
					<td>${clearAddMoney.equipmentName}</td>
					<td>
					 <c:choose>
						<c:when test="${clearAddMoney.type=='1'}">
						 ${clearAddMoney.businessId}
						</c:when>
						<c:otherwise>
						<a href="javascript:void(0);" onclick="showTickrtapeInfo('${clearAddMoney.businessId}')"> 
					     ${clearAddMoney.businessId}
				 		</a>
						</c:otherwise>
					</c:choose>
					</td>
				    <td>${clearAddMoney.bagNo}</td> 
					<td style="text-align:right;">${clearAddMoney.count}</td>
					<td style="text-align:right;" ><fmt:formatNumber value="${clearAddMoney.amount}" pattern="#,##0.00#" /></td>
					<td>${fns:getDictLabel(clearAddMoney.type,'CLEAR_ADD_MONEY_TYPE',"")}</td>
					<td style="text-align:right;"><fmt:formatNumber value="${clearAddMoney.surplusAmount}" pattern="#,##0.00#" /></td>
					<td>${clearAddMoney.remarks}</td>
					<td>${clearAddMoney.operator}</td>
					<td><fmt:formatDate value="${clearAddMoney.updateDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
	<%-- <div class="form-actions" style="width:100%">
	<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/doorOrder/v01/depositPanorama/'"/>
	</div> --%>
</body>
</html>