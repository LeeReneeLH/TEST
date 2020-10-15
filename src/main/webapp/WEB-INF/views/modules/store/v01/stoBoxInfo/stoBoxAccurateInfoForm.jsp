<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.box.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		selectBoxType($("#boxType").val());
		selectAtmBoxType($("#atmBoxType").val());
		setBoxType2ReadOnly();
		setOffice2ReadOnly();
		getAtmBoxMod();
			
		$("#modIds").change(function(){
			var modId = $(this).find("option:selected").val();
			var cls = $(this).find("option:selected").attr("class");
			if(modId != '' && modId != null){
				$(".modId").val(modId);
				$(".boxTypeNos").val(cls);
			}
		});
		
		$("#inputForm").validate({
			submitHandler : function(form) {
				loading('正在提交，请稍等...');
				form.submit();
			},
			////errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				//$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
	});

	function selectBoxType(val) {
		
		var boxTypeCar = "${fns:getConfig('sto.box.boxtype.car')}";
		var boxTypeBag = "${fns:getConfig('sto.box.boxtype.bagbox')}";
		var boxTypeNote = "${fns:getConfig('sto.box.boxtype.atmbox')}";
		if(val == boxTypeNote){
			$("#mod").show();
			$("#atmBoxTypeGroup").show();
		}else{
			$("#mod").hide();
			$("#atmBoxTypeGroup").hide();
		}
		if(val == boxTypeNote){
			$("#officeGroup").hide();
		} else if (val == boxTypeCar) {
			$("#officeGroup").hide();
		} else if (val == boxTypeBag) {
			$("#officeGroup").show();
		} else {
			$("#officeGroup").show();
		}
	}
	function selectAtmBoxType(val) {
		getAtmBoxMod();
	}

	function setBoxType2ReadOnly() {
		if ($("#id").val() != "" && $("#id").val() != undefined) {
			$("#boxType").attr("readOnly", true);
			$("#modIds").attr("readOnly", true);
			$("#atmBoxType").attr("readOnly", true);
		} else {
			$("#boxType").attr("readOnly", false);
			$("#modIds").attr("readOnly", false);
			$("#atmBoxType").attr("readOnly", false);
		}
	}
	
	function setOffice2ReadOnly(){
		if ($("#id").val() != "" && $("#id").val() != undefined) {
			$("#officeButton").attr("disabled", true);
			$("#officeId").attr("disabled", true);
			$("#officeName").attr("disabled", true);
			$("#officeGroup").show();
		}
	}
	 function getAtmBoxMod(){
			var atmBoxType = $("#atmBoxType").val();
			$("#modIds option[value != '']").remove();
			if(atmBoxType != null && atmBoxType != ''){
				$.post('${ctx}/store/v01/stoBoxInfo/getAtmBoxMod', {atmBoxType:atmBoxType}, function(data){
					var list = data.atmBoxMods;
					var len = list.length;	
					var mod = $(".modId").val();
					for(var i=0;i<len;i++){
						var val = list[i];
                        var modId = val.modId;
                        var modName = val.modName;
                        var boxtypeNo = val.boxTypeNo;
						if(mod != null && mod != '' && mod == modId){
							$("#modIds").append("<option class='"+boxtypeNo+"' value='"+modId+"' selected='selected'>"+modName+"</option>");						
						}else{
							$("#modIds").append("<option class='"+boxtypeNo+"' value='"+modId+"'>"+modName+"</option>");
						}				
					}
					$("#modIds").trigger("change");
				},"json");
			}
		}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoBoxInfo/"><spring:message
					code="store.boxInfo.List" /></a></li>
		<li><a
			href="${ctx}/store/v01/stoBoxInfo/form?id=${stoBoxInfo.id}">
				<shiro:hasPermission name="store:stoBoxInfo:edit">
					<c:choose>
						<c:when test="${not empty stoBoxInfo.id}">
							<spring:message code="store.rfid.boxAdd" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code="store.rfid.boxAdd" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
				<shiro:lacksPermission name="store:stoBoxInfo:edit">
					<spring:message code="store.boxInfo" /><spring:message code="common.view" />
				</shiro:lacksPermission>
		</a></li>
		<shiro:hasPermission name="store:stoBoxInfo:edit">
			<li  class="active"><a href="${ctx}/store/v01/stoBoxInfo/formAccurate"> 
			<spring:message	code="store.rfid.boxAccurateInfo" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/store/v01/stoBoxInfo/formATM"> 
			<spring:message	code="store.rfid.atmBoxAdd" /><spring:message code="common.add" />
		</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoBoxInfo"
		action="${ctx}/store/v01/stoBoxInfo/saveAccurate" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<%-- <c:if test="${stoBoxInfo.id != null && stoBoxInfo.id != ''}">
			<div class="control-group">
				<label class="control-label"><spring:message
						code="common.boxNo" />：</label>
				<div class="controls">
					<form:input path="id" id="id" htmlEscape="false"
						maxlength="10" class="required" readonly="true" />
				</div>
			</div>
		</c:if> --%>
		
		<!-- 使用机构 -->
		<div class="control-group" id="officeGroup">
			<label class="control-label"><spring:message
					code="store.usedOutlets" />：</label>
			<div class="controls">
				<!-- 如果是金库用户登录 -->
				<c:if test="${fns:getUser().office.type == '3' }">
					<sys:treeselect id="office1" name="office.id"
					value="${stoBoxInfo.office.id}" labelName="office.name"
					labelValue="${stoBoxInfo.office.name}" title="机构"
					url="/sys/office/treeData" notAllowSelectRoot="false" allowClear="true"
					notAllowSelectParent="false" type="4" cssClass="required"/>
				</c:if>
				<!-- 如果数字化平台用户登陆 -->
				<c:if test="${fns:getUser().office.type == '7' }">
					<sys:treeselect id="office2" name="office.id"
						value="${stoBoxInfo.office.id}" labelName="office.name"
						labelValue="${stoBoxInfo.office.name}" title="机构"
						url="/sys/office/treeData" notAllowSelectRoot="false" allowClear="true"
						notAllowSelectParent="false" isAll="true"  type="4"  cssClass="required"/>
				</c:if>
			<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<!-- 箱袋类型 -->
		<div class="control-group">
			<c:set var="showBoxType" value="${fns:getConfig('sto.box.boxtype.allocationShow')}" />
			<label class="control-label"><spring:message
					code="store.boxType" />：</label>
			<div class="controls">
				<form:select path="boxType" onchange="selectBoxType(this.value)"
					id="boxType" class="input-large">
					<form:options items="${fns:getFilterDictList('sto_box_type',true,showBoxType)}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		
		<div class="control-group" style="display: none">
			<label class="control-label"><spring:message
					code="store.boxUse" />：</label>
			<div class="controls">
				<form:select path="boxUse" class="input-large">
					<form:options items="${fns:getDictList('cfg_box_use')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<!-- 钞箱类型 -->
		<div class="control-group" id="atmBoxTypeGroup">
			<label class="control-label"><spring:message
					code="common.atmBoxType" />：</label>
			<div class="controls">
				<form:select path="atmBoxType" onchange="selectAtmBoxType(this.value)"
					id="atmBoxType" class="input-large">
					<option value="">
						<spring:message code="common.select" />
					</option>
					<form:options items="${fns:getFilterDictList('sto_box_type',true,'')}"
							itemLabel="label" itemValue="value" htmlEscape="false" cssClass="required"/>
				</form:select>
			</div>
		</div>
		
		<!--根据画面选择的钞箱类型名称，保存钞箱类型编号（6位） -->
		<div class="control-group" id="mod">
			<label class="control-label"><spring:message
					code="common.atmBox.model" />：</label>
			<div class="controls">
				<select id="modIds" name="modIds" class="input-large required">
					<option value="">
						<spring:message code="common.select" />
					</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
				<form:hidden path="atmBoxMod.id" htmlEscape="false" class="modId" value="${stoBoxInfo.atmBoxMod.id }"/>
				<form:hidden path="atmBoxMod.boxTypeNo" htmlEscape="false" class="boxTypeNos" value="${stoBoxInfo.atmBoxMod.boxTypeNo }"/>
			</div>
		</div>
		<div class="control-group" id="atmBoxGroup" style="display: none">
			<label class="control-label"><spring:message
					code="store.atmBox.denomination" />：</label>
			<div class="controls" style="margin-bottom: 8px">
				<form:select path="denomination" onchange="selectDenom(this.value)"
					id="denomination" class="input-large">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${fns:getDictList('cfg_denomination')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			<label class="control-label"><spring:message
					code="store.atmBox.pieceNum" />：</label>
			<div class="controls" style="margin-bottom: 8px">
				<form:input path="pieceNum" id="pieceNum" htmlEscape="false"
					maxlength="50" class="digits" onchange="changePieceNum(this.value)" />
			</div>
			<label class="control-label"><spring:message
					code="store.atmBox.amount" />：</label>
			<div class="controls">
				<form:input path="boxAmount" id="boxAmt" htmlEscape="false"
					maxlength="50" class="number" readonly="true" />
			</div>
		</div>
		<c:if test="${stoBoxInfo.id == null || stoBoxInfo.id == ''}">
			<div class="control-group">
				<label class="control-label"><spring:message
						code="store.rfid.box" />：</label>
				<div class="controls">
					<form:input path="searchBoxNo" htmlEscape="false" maxlength="8"
						class="digits required" />
				<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<c:if test="${(stoBoxInfo.id != null && stoBoxInfo.id != '')&&fn:contains(fns:getConfig('stoboxinfo.boxUser.type'),fns:getUser().userType)}">
		<div class="control-group">
			<!-- 箱袋状态 -->
			<label class="control-label"><spring:message code="common.boxStatus" />：</label>
			<div class="controls">
				<c:set var="showCar" value="${fns:getConfig('sto.box.boxstatus.car')} " />
				<c:set var="showAtmbox" value="${fns:getConfig('sto.box.boxstatus.atmbox')} " />
				<c:set var="showOther" value="${fns:getConfig('sto.box.boxstatus.other')} " />
				<form:select path="boxStatus" class="input-large">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<c:if test="${stoBoxInfo.boxType ne fns:getConfig('sto.box.boxtype.car') && stoBoxInfo.boxType ne fns:getConfig('sto.box.boxtype.atmbox')}">
						<form:options items="${fns:getFilterDictList('sto_box_status',true,showOther)}"
							itemLabel="label" itemValue="value" htmlEscape="false" cssClass="required"/>
					</c:if>
					<c:if test="${stoBoxInfo.boxType eq fns:getConfig('sto.box.boxtype.car')}">
						<!--小车只有空闲，在整点室，在库房 -->
						<form:options items="${fns:getFilterDictList('sto_box_status',true,showCar)}"
						itemLabel="label" itemValue="value" htmlEscape="false" cssClass="required"/>
					</c:if>
					<c:if test="${stoBoxInfo.boxType eq fns:getConfig('sto.box.boxtype.atmbox')}">
						<!--钞箱 -->
						<form:options items="${fns:getFilterDictList('sto_box_status',false,showAtmbox)}"
						itemLabel="label" itemValue="value" htmlEscape="false" cssClass="required"/>
					</c:if>
				</form:select>
			</div>
		</div>
		</c:if>
		
		<!-- 出库时间 -->
<%-- 		<c:if test="${stoBoxInfo.boxType eq fns:getConfig('sto.box.boxtype.tailbox')}">
			<div class="control-group">
				<!-- 箱袋状态 -->
				<label class="control-label"><spring:message code="common.outTime" />：</label>
				<div class="controls">
					<input id="outDate"  name="outDate" type="text" readonly="readonly" maxlength="20" class="input-middle Wdate createTime" 
					   value="<fmt:formatDate value="${stoBoxInfo.outDate}" pattern="yyyy-MM-dd"/>" 
					   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd', minDate:'%y-%M-%d '});"/>
				</div>
			</div>
		</c:if> --%>
		<div class="form-actions">
			<shiro:hasPermission name="store:stoBoxInfo:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.save'/>" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/stoBoxInfo/back'"/>
		</div>
	</form:form>
</body>
</html>
