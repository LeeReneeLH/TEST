<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 箱袋信息管理 -->
	<title><spring:message code="store.box.manage" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 修改的场合，设置“箱袋类型”和“钞箱类型”是否为只读
			setReadOnly();
			getAtmBoxMod();
			boxTypeChange();
			setOffice2ReadOnly();
			if($("#boxAmounts").val()!="" && $("#boxAmounts").val()!=undefined){
				$("#boxAmounts").val(formatCurrencyFloat($("#boxAmounts").val()));
			}
			
			$("#boxType").change(function(){
				var boxType = $(this).val();
				if(boxType == "${fns:getConfig('sto.box.boxtype.car')}"){
					$("#mod").hide();
					$("#mods").hide();
				}else{
					$("#mod").show();
					$("#mods").show();
				}
				getAtmBoxMod();
			});			
			
			$("#modIds").change(function(){
				var modId = $(this).find("option:selected").val();
				var cls = $(this).find("option:selected").attr("class");
				if(modId != '' && modId != null){
					$(".modId").val(modId);
					$(".boxTypeNos").val(cls);
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			// 设置“箱袋类型”和“钞箱类型”是否为只读
			function setReadOnly() {
				if ($("#id").val()) {
					// 编辑的场合
					$("#boxType").attr("readOnly", true);
					$("#modIds").attr("readOnly", true);
					$("#boxStatus").attr("readOnly", true);
				} else {
					// 新增的场合
					$("#boxType").attr("readOnly", false);
					$("#modIds").attr("readOnly", false);
				}
			}
			
			function boxTypeChange(){
				var boxType = $("#boxType").val();
				if(boxType == "${fns:getConfig('sto.box.boxtype.car')}"){
					$("#mod").hide();
					$("#mods").hide();
					$("#boxAmounta").hide();
				}else{
					$("#mod").show();
					$("#mods").show();
					$("#boxAmounta").show();
				}
			}
		});
		
		 function getAtmBoxMod(){
			var boxType = $("#boxType").val();
			$("#modIds option[value != '']").remove();
			if(boxType != null && boxType != ''){
				 $.post('${ctx}/store/v01/stoBoxInfo/getAtmBoxMod', {atmBoxType:boxType}, function(data){
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
		
		function setOffice2ReadOnly() {
			var boxType = $("#boxType").val();
			if (boxType == "${fns:getConfig('sto.box.boxtype.car')}") {
				if ($("#id").val()) {
					$("#officeButton").attr("disabled",true);
					$("#officeId").attr("disabled", true);
					$("#officeName").attr("disabled", true);
				}
			}
		}
		
		/* 千分位用逗号分隔 */
		 function formatCurrencyFloat(num) {
				num = num.toString().replace(/\$|\,/g, '');
				if (isNaN(num))
					num = "0";
				sign = (num == (num = Math.abs(num)));
				num = Math.floor(num * 100 + 0.50000000001);
				cents = num % 100;
				num = Math.floor(num / 100).toString();
				if (cents < 10)
					cents = "0" + cents;
				for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
					num = num.substring(0, num.length - (4 * i + 3)) + ','
							+ num.substring(num.length - (4 * i + 3));
				return (((sign) ? '' : '-') + num + '.' + cents);
			}
	</script>

</head>
<body>
	<!-- Tab页 -->
	<ul class="nav nav-tabs">
		<!-- 箱袋管理列表 -->
		<li>
			<!-- 装箱信息列表  -->
			<a href="${ctx}/store/v01/stoBoxInfo/">
				<spring:message code="store.boxInfo.List" />
			</a>
		</li>
		<li><a href="${ctx}/store/v01/stoBoxInfo/form"> 
			<spring:message	code="store.rfid.boxAdd" /><spring:message code="common.add" />
			</a></li>
		<shiro:hasPermission name="store:stoBoxInfo:edit">
			<li><a href="${ctx}/store/v01/stoBoxInfo/formAccurate"> 
			<spring:message	code="store.rfid.boxAccurateInfo" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<li class="active">
			<a href="${ctx}/store/v01/stoBoxInfo/form?id=${stoBoxInfo.id}">
				<shiro:hasPermission name="store:stoBoxInfo:edit">
					<c:choose>
						<c:when test="${not empty stoBoxInfo.id}">
							<!-- 箱袋信息修改 -->
							<spring:message code="store.rfid.atmBoxAdd" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<!-- 箱袋信息添加 -->
							<spring:message code="store.rfid.atmBoxAdd" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
				<!-- 没有权限的时候，只可查看 -->
				<shiro:lacksPermission name="store:stoBoxInfo:edit">
					<!-- 箱袋信息查看-->
					<spring:message code="store.boxInfo" /><spring:message code="common.view" />
				</shiro:lacksPermission>
			</a>
		</li>
	</ul><br/>
	
	<form:form id="inputForm" modelAttribute="stoBoxInfo" 
		action="${ctx}/store/v01/stoBoxInfo/saveATM" method="post" 
		class="form-horizontal">
		<!-- 提示消息 -->
		<sys:message content="${message}"/>
		<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${stoBoxInfo.strUpdateDate}">
		<!-- 箱袋编号：仅修改时显示，且只读 -->
		<c:if test="${stoBoxInfo.id != null && stoBoxInfo.id != ''}">
			<div class="control-group">
				<label class="control-label">
					<spring:message code="common.boxNo" />：
				</label>
				<div class="controls">
					<form:input path="id" id="id" htmlEscape="false" readonly="true" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">
					<spring:message code="store.rfid" />：
				</label>
				<div class="controls">
					<form:input path="rfid" id="rfid" htmlEscape="false" readonly="true" />
				</div>
			</div>
		</c:if>
		<!-- 归属机构 -->
		<div class="control-group" id="officeGroup">
			<label class="control-label"><spring:message
					code="store.ownershipInstitution" />：</label>
			<div class="controls">
					 <c:choose>
		           <c:when test="${(stoBoxInfo.id != null && stoBoxInfo.id != '') && (stoBoxInfo.boxType == fns:getConfig('sto.box.boxtype.car'))}">
		                <sys:treeselect id="office" name="office.id" value="${stoBoxInfo.office.id}" labelName="office.name" labelValue="${stoBoxInfo.office.name}"
					title="机构" url="/sys/office/treeData?type=1" cssClass="required" hideBtn='true' allowClear="true" notAllowSelectParent="true" />
		           </c:when>
		           <c:otherwise>
		                <sys:treeselect id="office" name="office.id" value="${stoBoxInfo.office.id}" labelName="office.name" labelValue="${stoBoxInfo.office.name}" title="机构"
					url="/sys/office/treeData" notAllowSelectRoot="false" notAllowSelectParent="false" cssClass="required"/>
					</c:otherwise>
		        </c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 箱袋类型 -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code="store.boxType" />：
			</label>
			<div class="controls">
				<c:set var="showATMBoxType" value="${fns:getConfig('sto.box.boxtype.atmShow')}" />
				<c:choose>
					<c:when test="${not empty stoBoxInfo.id}">
						<form:select path="boxType" id="boxType" class="input-large required" style="font-size:14px">
							<form:options items="${fns:getFilterDictList('sto_box_type', true, showATMBoxType)}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</c:when>
					<c:otherwise>
						<form:select path="boxType" id="boxType" class="input-large required">
						<form:options items="${fns:getFilterDictList('sto_box_type', true, showATMBoxType)}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- TODO 根据画面选择的钞箱类型名称，保存钞箱类型编号（6位） -->
		<div class="control-group" id="mod">
			<label class="control-label">
				<!-- 钞箱型号 -->
				<spring:message code="common.atmBox.model" />：
			</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty stoBoxInfo.id}">
						<select id="modIds" name="modIds" class="input-large required" style="font-size:14px">
							<option value=""><spring:message code="common.select" /></option>
						</select>
					</c:when>
					<c:otherwise>
						<select id="modIds" name="modIds" class="input-large required">
							<option value=""><spring:message code="common.select" /></option>
						</select>
					</c:otherwise>
				</c:choose>
				<span class="help-inline"><font color="red">*</font> </span>
				<form:hidden path="atmBoxMod.id" htmlEscape="false" class="modId" value="${stoBoxInfo.atmBoxMod.id }"/>
				<form:hidden path="atmBoxMod.boxTypeNo" htmlEscape="false" class="boxTypeNos" value="${stoBoxInfo.atmBoxMod.boxTypeNo }"/>
			</div>
		</div>
		<!-- 钞箱金额 -->
		<c:if test="${stoBoxInfo.id == null || stoBoxInfo.id == ''}">
			<div class="control-group" id="mods">
				<label class="control-label">
					<spring:message code="store.atmBox.amount" />:
				</label>
				<div class="controls">
					<form:select path="boxAmount" id="boxAmount" class="input-large required">
						<form:options items="${fns:getFilterDictList('atm_box_amount', true, '')}" 
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<c:if test="${stoBoxInfo.id != null && stoBoxInfo.id != ''}">
			<div class="control-group" id="boxAmounta" >
				<label class="control-label">
					<!-- 钞箱金额 -->
					<spring:message code="store.atmBox.amount" /><spring:message code="common.units.yuan.alone" />：
				</label>
				<div class="controls">
					<form:input path="boxAmount" id="boxAmounts" htmlEscape="false" maxlength="10"
							 class="required" disabled="true"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<!-- 箱袋数量：仅新增时显示 -->
		<c:if test="${stoBoxInfo.id == null || stoBoxInfo.id == ''}">
			<div class="control-group">
				<label class="control-label">
					<!-- 数量 -->
					<spring:message code="common.number" />：
				</label>
				<div class="controls">
					<form:input path="boxNum" htmlEscape="false" maxlength="3"
							range="1,999" min='1' class="digits required" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<!-- 箱袋状态：仅编辑时，且用户类型是金库主管时显示 -->
		<c:if test="${(stoBoxInfo.id != null && stoBoxInfo.id != '')}">
			<div class="control-group">
				<label class="control-label">
					<!-- 箱袋状态 -->
					<spring:message code="common.boxStatus" />：
				</label>
				<div class="controls">
					<c:set var="showCar"
						value="${fns:getConfig('sto.box.boxstatus.car')} " />
					<c:set var="showAtmbox"
						value="${fns:getConfig('sto.box.boxstatus.atmbox')} " />
					<form:select path="boxStatus" class="input-large" style="font-size:14px">
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<!--钞箱 -->
						<form:options
							items="${fns:getFilterDictList('sto_box_status',false,showAtmbox)}"
							itemLabel="label" itemValue="value" htmlEscape="false"
							cssClass="required" />
					</form:select>
				</div>
			</div>
		</c:if>
		<!-- 按钮 -->
		<div class="form-actions">
			<shiro:hasPermission name="store:stoBoxInfo:edit">
				<!-- 保存按钮 -->
				<input id="btnSubmit" class="btn btn-primary" type="submit" 
					value="<spring:message code='common.save'/>"/>&nbsp;
			</shiro:hasPermission>
			<!-- 返回按钮 -->
			<input id="btnCancel" class="btn" type="button" 
				value="<spring:message code='common.return'/>" 
				onclick="window.location.href='${ctx}/store/v01/stoBoxInfo/back'"/>
		</div>
	</form:form>
</body>
</html>