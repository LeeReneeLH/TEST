<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 添加人行印章 --%>
	<title>添加人行印章</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(document).ready(function() {
		businessTypeChange();
		changeOfficeStamper();
		$("#inputForm").validate(
				{
					submitHandler : function(form) {
						loading('正在提交，请稍等...');
						form.submit();
					},
					//errorContainer : "#messageBox",
					errorPlacement : function(error, element) {
						//$("#messageBox").text("输入有误，请先更正。");
						if (element.is(":checkbox")
								|| element.is(":radio")
								|| element.parent().is(
										".input-append")) {
							error.appendTo(element.parent()
									.parent());
						} else {
							error.insertAfter(element);
						}
					}
			});
		// 根据印章类型变化机构印章图片
		$("#pbocOfficeStamperType").change(function () {
			changeOfficeStamper();
		});
		// 提交按钮
		$("#btnSubmit").click(function(){
			$("#inputForm").attr("action","${ctx}/store/v02/stoDocStamperMgr/save?operationType=savePobcStamper&userCacheId=" + $("#cacheId").val());
			$("#inputForm").submit();
		});
		
		// 返回按钮
		$("#btnCancel").click(function(){
			
			$("#backForm").attr("action","${ctx}/store/v02/stoDocStamperMgr/back?userCacheId=" + $("#cacheId").val());
			$("#backForm").submit();
		});
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
	function changeOfficeStamper() {
		$('#pbocOffcieStamperImage').empty();
		var stamperType = $("#pbocOfficeStamperType").val();
		if (stamperType != "") {
			
			$('#pbocOffcieStamperImage').html("<img id='target' src='${ctx}/store/v02/stoDocStamperMgr/showOfficeStamperImage?officeId=${stoDocTempInfo.office.parent.id}&stamperType=" 
					+ stamperType + "' height='100%' width='100%' >").show;
		}
	}
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 单据印章管理列表 --%>
		<li><a href="${ctx}/store/v02/stoDocStamperMgr/list">单据印章管理列表</a></li>
		
		<%-- 添加人行印章--%>
		<li class="active"><a href="#" onclick="javascript:return false;">添加人行印章</a></li>
	</ul><br/>
	<div class="row">
		<form:form id="inputForm" modelAttribute="stoDocTempInfo" action="" method="post" class="form-horizontal">
			<form:hidden path="id"/>
			<sys:message content="${message}"/>	
		
			<div class="span6">
				<div class="row">
					<div class="span6">
						<div class="control-group item">
							<label class="control-label" style="width:120px;">人行机构名称：</label>
							<div class="controls" style="margin-left: 120px;">
								<c:choose>
									<c:when test="${fns:getUser().office.type == '7'}">
										<input id="pbocOfficeName" type="text" style="width:150px;" value="${fns:getOfficeNameById(stoDocTempInfo.office.parent.id)}" readonly="readonly"/>	
									</c:when>
									<c:otherwise>
										<input id="pbocOfficeName" type="text" style="width:150px;" value="${fns:getUser().office.name}" readonly="readonly"/>	
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="control-group item">
							<%-- :80px;"> --%>
							<label class="control-label" style="width:120px;">印章类型：</label>
							<div class="controls" style="margin-left: 120px;">
						   		<form:select path="pbocOfficeStamperType" id="pbocOfficeStamperType" class="required input-medium" >
									<form:option value="">
										<spring:message code="common.select" />
									</form:option>
									<form:options items="${fns:getDictList('PBOC_OFFICE_STAMPER_TYPE')}"
										itemLabel="label" itemValue="value" htmlEscape="false" />
								</form:select>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="span6">
						<div class="span6">
							<span style="width:180px;height:140px;" id="pbocOffcieStamperImage">
							</span>
						</div>	
					</div>
				</div>
			</div>
			
				
			<div class="span9">
				<div class="row" style="margin-left:10px">
					<div class="span5">
						<div class="control-group item">
							<%-- 申请机构 --%>
							<label class="control-label" style="width:120px;">商业机构名称：</label>
							<div class="controls" style="margin-left: 120px;">
							 	<input id="officeName" type="text" style="width:150px;" value="${stoDocTempInfo.office.name}" readonly="readonly"/>	
							</div>
						</div>
						<div class="control-group item">
							<%-- 印章类型 --%>
							<label class="control-label" style="width:120px;">印章类型：</label>
							<div class="controls" style="margin-left: 120px;">
								<input id="officeStamperType" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.officeStamperType,'OFFICE_STAMPER_TYPE','')}" readonly="readonly"/>	
							</div>
						</div>
						<div class="control-group item">
							<%-- 单据类型 --%>
							<label class="control-label" style="width:120px;">单据类型：</label>
							<div class="controls" style="margin-left: 120px;">
								<input id="documentType" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.documentType,'DOCUMENT_TYPE','')}" readonly="readonly"/>	
							</div>
						</div>
						<div class="control-group item">
							<%-- 申请类型 --%>
							<label class="control-label" style="width:120px;"><spring:message code="allocation.application.type" />：</label>
							<div class="controls" style="margin-left: 120px;">
								<input id="businessType" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.businessType,'all_businessType','')}" readonly="readonly"/>	
							</div>
						</div>
						<div id="allocateStatus" class="control-group item">
							<%-- 状态 --%>
							<label class="control-label" style="width:120px;"><spring:message code='common.status'/>：</label>
							<div class="controls" style="margin-left: 120px;">
								<input id="allocateStatusValue" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.allocateStatus,'pboc_order_quota_status','')}" readonly="readonly"/>	
							</div>
						</div>
						<div id="handinStatus" class="control-group item">
							<%-- 状态 --%>
							<label class="control-label" style="width:120px;"><spring:message code='common.status'/>：</label>
							<div class="controls" style="margin-left: 120px;">
								<input id="handinStatusValue" type="text" style="width:150px;" value="${fns:getDictLabel(stoDocTempInfo.handinStatus,'pboc_order_handin_status','')}" readonly="readonly"/>	
							</div>
						</div>
						
					</div>
					<div class="span3" style="margin-left: 50px;">
						<span style="width:180px;height:140px;" id="offcieStamperImage">
							<img src="${ctx}/store/v02/stoDocStamperMgr/showOfficeStamperImageById?officeStamperInfoId=${stoDocTempInfo.officeStamperId}" 
								style="width:100%;height:100%;"/>
						</span>
					</div>
				</div>
			
				<div class="row" style="margin-left:10px">
					<div class="span8">
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
			</div>
		</form:form>
	</div>
	
	<div class="row" style="margin-left:10px">
		<div class="span9">
			<div class="form-actions">
				<%-- 保存 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.commit'/>"/>
				&nbsp;
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/store/v02/stoDocStamperMgr/back?userCacheId=${userCacheId }'"/>
			</div>
			<input type="hidden" id="cacheId" value="${userCacheId }">
		</div>
		<form id="backForm"></form>
	</div>
</body>
</html>