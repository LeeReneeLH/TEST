<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 单据印章查看 --%>
	<title>单据印章查看</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(document).ready(function() {
		businessTypeChange();
	});
	
	function businessTypeChange(){
		$("#allocateStatus").hide();
		$("#handinStatus").hide();
		var businessType = '${stoDocTempInfo.businessType}';
		if (businessType == "52") {
			$("#handinStatus").show();
		} else {
			$("#allocateStatus").show();
		}
	}
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 单据印章管理列表 --%>
		<li><a href="${ctx}/store/v02/stoDocStamperMgr/list">单据印章管理列表</a></li>
		<shiro:hasPermission name="store.v02:stoDocStamperMgr:regedit">
			<c:if test="${fns:getUser().office.type == '3'}">
				<%-- 单据印章登记(link) --%>
				<li><a href="${ctx}/store/v02/stoDocStamperMgr/form?operationType=toRegistPage">单据印章登记</a></li>
			</c:if>
		</shiro:hasPermission>
		<%-- 明细查看 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">明细查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoDocTempInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span3">
				<div class="control-group item">
					<%-- 申请机构 --%>
					<label class="control-label" style="width:80px;">机构名称：</label>
					<div class="controls" style="margin-left: 90px;">
					 	<input id="officeName" type="text" style="width:150px;" value="${stoDocTempInfo.office.name}" readonly="readonly"/>	
					</div>
				</div>
				<div class="control-group item">
					<%-- :80px;"> --%>
					<label class="control-label" style="width:80px;">印章类型：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:select path="officeStamperType" id="officeStamperType" class="input-medium" disabled="true">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('OFFICE_STAMPER_TYPE')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</div>
				</div>
				<div class="control-group item">
					<%-- 单据类型 --%>
					<label class="control-label" style="width:80px;">单据类型：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:select path="documentType" id="documentType" class="input-medium" disabled="true">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('DOCUMENT_TYPE')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</div>
				</div>
				<div class="control-group item">
					<%-- 申请类型 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.application.type" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:select path="businessType" id="businessType" class="input-medium" disabled="true">
							<form:options items="${fns:getFilterDictList('all_businessType', true, '51,52,50')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</div>
				</div>
				<div id="allocateStatus" class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:80px;"><spring:message code='common.status'/>：</label>
					<div class="controls" style="margin-left: 90px;">
						<input id="allocateStatusValue" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.allocateStatus,'pboc_order_quota_status','')}" readonly="readonly"/>	
					</div>
				</div>
				<div id="handinStatus" class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:80px;"><spring:message code='common.status'/>：</label>
					<div class="controls" style="margin-left: 90px;">
						<input id="handinStatusValue" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.handinStatus,'pboc_order_handin_status','')}" readonly="readonly"/>	
					</div>
				</div>
				
			</div>
			<div class="span5">
				<span style="width:180px;height:140px; position:absolute;left:350px;z-index:100;" id="offcieStamperImage">
					<img src="${ctx}/store/v02/stoDocStamperMgr/showOfficeStamperImageById?officeStamperInfoId=${stoDocTempInfo.officeStamperId}" 
						style="width:100%;height:100%;"/>
				</span>
			</div>
			<c:if test="${!empty stoDocTempInfo.pbocOfficeStamperId }">
				<div class="span5">
					<span style="width:180px;height:140px; position:absolute;left:660px;z-index:100;" id="pbocOffcieStamperImage">
						<img src="${ctx}/store/v02/stoDocStamperMgr/showOfficeStamperImageById?officeStamperInfoId=${stoDocTempInfo.pbocOfficeStamperId}" 
							style="width:100%;height:100%;"/>
					</span>
				</div>
			</c:if>
			
		</div>
	</form:form>
	<div class="row" style="margin-left:10px">
		<div class="span9">
			<div style="overflow-y: auto; height: 215px;">
				<table id="contentTable" class="table table-hover" >
					<thead>
						<tr>
							<%-- 序号 --%>
							<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
							<%-- 人员姓名 --%>
							<th style="text-align: center" >人员姓名</th>
							<%-- 印章 --%>
							<th style="text-align: center" >印章</th>
							<%-- 职责类型 --%>
							<th style="text-align: center" >职责类型</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${fns:getCache(userCacheId, null).docTempUserDetailList}" var="item" varStatus="status">
							<tr >
								<td style="text-align:right;">${status.index + 1}</td>
								<td style="text-align:right;">${item.stoEscortInfo.escortName}</td>
								<td style="text-align:center;"><span style="width: 50px;height:20px;float:left;">
									<img src="${ctx}/store/v02/stoDocStamperMgr/showEscortStamperImage?escortId=${item.escortId}"
				 						height="100%" width="100%"/></span>
				 				</td>
								<td style="text-align:right;">${fns:getDictLabel(item.responsibilityType,'RESPONSIBILITY_TYPE',"")}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>	
	<div class="row" style="margin-left:10px">
		<div class="span9">
			<div class="form-actions">
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/store/v02/stoDocStamperMgr/back'"/>
			</div>
		</div>
	</div>
</body>
</html>