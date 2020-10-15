<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 单据印章登记 --%>
	<title>单据印章登记</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(document).ready(function() {
		changeOfficeStamper();
		businessTypeChange();
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
			// 添加物品
            $("#add").click(function () {
            	var url = "${ctx}/store/v02/stoDocStamperMgr/add?operationType=toRegistPage&userCacheId=" + $("#cacheId").val();
            	$("#inputForm").attr("action", url);
				$("#inputForm").submit();
            });
			// 根据印章类型变化机构印章图片
			$("#officeStamperType").change(function () {
				changeOfficeStamper();
			});
         	// 提交按钮
			$("#btnSubmit").click(function(){
				$("#responsibilityType").removeClass();
				$("#escortId").removeClass();
				$("#inputForm").attr("action","${ctx}/store/v02/stoDocStamperMgr/save?operationType=saveRegist&userCacheId=" + $("#cacheId").val());
				$("#inputForm").submit();
			});
			// 返回按钮
			$("#btnCancel").click(function(){
				
				$("#backForm").attr("action","${ctx}/store/v02/stoDocStamperMgr/back?userCacheId=" + $("#cacheId").val());
				$("#backForm").submit();
			});
	});
	
	function changeOfficeStamper() {
		$('#offcieStamperImage').empty();
		var stamperType = $("#officeStamperType").val();
		if (stamperType != "") {
			
			$('#offcieStamperImage').html("<img id='target' src='${ctx}/store/v02/stoDocStamperMgr/showOfficeStamperImage?stamperType=" 
					+ stamperType + "' height='100%' width='100%' >").show;
		}
	}
	function businessTypeChange(){
		$("#allocateStatus").hide();
		$("#handinStatus").hide();
		var businessType = $("#businessType").val();
		if (businessType == "52") {
			$("#handinStatus").show();
			$("#allocateStatus").hide();
		} else {
			$("#handinStatus").hide();
			$("#allocateStatus").show();
		}
	}
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 单据印章管理列表 --%>
		<li><a href="${ctx}/store/v02/stoDocStamperMgr/list">单据印章管理列表</a></li>
		<%-- 单据印章登记 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">单据印章登记</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoDocTempInfo" action="" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 申请明细 --%>
			<div class="span5">
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
				   		<form:select path="officeStamperType" id="officeStamperType" class="required input-medium" >
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('OFFICE_STAMPER_TYPE')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group item">
					<%-- 单据类型 --%>
					<label class="control-label" style="width:80px;">单据类型：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:select path="documentType" id="documentType" class="required input-medium" >
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('DOCUMENT_TYPE')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group item">
					<%-- 申请类型 --%>
					<label class="control-label" style="width:80px;"><spring:message code="allocation.application.type" />：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:select path="businessType" id="businessType" class="required input-medium" onChange="businessTypeChange()">
							<form:options items="${fns:getFilterDictList('all_businessType', true, '50,51,52')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div id="allocateStatus" class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:80px;"><spring:message code='common.status'/>：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:select path="allocateStatus" id="allocateStatusValue" class="required input-medium">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('pboc_order_quota_status')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div id="handinStatus" class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" style="width:80px;"><spring:message code='common.status'/>：</label>
					<div class="controls" style="margin-left: 90px;">
						<form:select path="handinStatus" id="handinStatusValue" class="required input-medium">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('pboc_order_handin_status')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group item">
					<%-- 职责类型 --%>
					<label class="control-label" style="width:80px;">职责类型：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:select path="stoDocTempUserDetail.responsibilityType" id="responsibilityType" class="required input-medium" >
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options items="${fns:getDictList('RESPONSIBILITY_TYPE')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group item" style="margin-top: 20px;">
					<%-- 人员姓名 --%>
					<label class="control-label" style="width:80px;">人员姓名：</label>
					<div class="controls" style="margin-left: 90px;">
				   		<form:select path="stoDocTempUserDetail.escortId" id="escortId" class="required input-medium">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getCommercialBankUserInfoListByOffice(stoDocTempInfo.office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
			</div>
			<div class="span5">
				<span style="width:180px;height:140px; position:absolute;left:350px;z-index:100;" id="offcieStamperImage">
				</span>
				
			</div>
		</div>
	</form:form>
	<div class="row" style="margin-left:10px">
		<div class="span1" style="text-align: left">
			<input id="add" class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" />
		</div>
	</div>
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
							<%-- 操作（删除/修改） --%>
							<th style="text-align: center" ><spring:message code='common.operation'/></th>
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
								<td style="text-align:center;">
									<a href="${ctx}/store/v02/stoDocStamperMgr/deleteEscort?escortId=${item.escortId}&operationType=toRegistPage&userCacheId=${userCacheId }"
									   onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
									</a>
								</td>
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
				<shiro:hasPermission name="allocation.v02:businessOrder:edit">
					<%-- 保存 --%>
					<input id="btnSubmit" class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>"/>
					&nbsp;
				</shiro:hasPermission>
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					/>
			</div>
			<input type="hidden" id="cacheId" value="${userCacheId }">
			<form id="backForm"></form>
		</div>
	</div>
</body>
</html>