<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.add.cash.group.manager" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			rules:{
				groupName:{remote:"${ctx}/store/v01/stoAddCashGroup/checkGroupName?oldGroupName=" + encodeURIComponent('${stoAddCashGroup.groupName}')}
			},
			messages: {
				groupName:{remote:"<spring:message code='store.add.cash.group.name.exist' />"}
			},
			submitHandler: function(form){
				/* 押运人员1和押运人员2校验修改 	by:wxz	2017-12-28 */
				var getEscort = $('#escortId').val();
				var subCheck = "";
				if(getEscort == "escortNo1" && getEscort != ''){
					subCheck = checkEscortselect("1");
				} else if (getEscort == "escortNo2" && getEscort != '') {
					subCheck = checkEscortselect("2");
				} else if (getEscort == ''){
					subCheck = true;
				}
				if(subCheck == true) {
					//设置车辆
					setSelectName('carId','carName');
					//设置押运人员1
					setSelectName('escortNo1','escortName1');
					//设置押运人员2
					setSelectName('escortNo2','escortName2');
					loading('正在提交，请稍等...');
					form.submit();	
				}
				/* end */
			},
			//errorContainer: "#messageBox",
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
	
	//补全提交信息
	function setSelectName(valueId, valueNm) {
		$("#" + valueNm).val("");
		var selectValue=$("#" + valueId).val(); 
		if ((selectValue != undefined && selectValue != "")) {
			var selectText = $("#" + valueId).find("option:selected").text();
			$("#" + valueNm).val(selectText);
		}
	}
	
	//检查押运人1和押运人2不能是同一个人
	function checkEscortselect(code) {
		/* 押运人员1和押运人员2校验修改 	by:wxz	2017-12-28 */
		$("#escortNo1-error").hide();
		$("#escortNo2-error").hide();
		$("#escortNoLabel1").remove();
		$("#escortNoLabel2").remove();
		var escortNo1 = $("#escortNo1").val();
		var escortNo2 = $("#escortNo2").val();
		// 押运人员1的id
		var escortId1 = "escortNo1";
		// 押运人员2的id
		var escortId2 = "escortNo2";
		if(escortNo1 == escortNo2) {
			if(code == '1'){
				// 两个押运人不能是同一个人
				$("#escortNoError1").before("<label id='escortNoLabel1' class='error' for='escortNo1'><spring:message code='store.add.cash.group.escort.repeat' /></label>");//两个押运人不能是同一个人
				// 赋值押运人员1的id
				$('#escortId').val(escortId1);
			}else if(code == '2') {
				// 两个押运人不能是同一个人
				$("#escortNoError2").before("<label id='escortNoLabel2' class='error' for='escortNo2'><spring:message code='store.add.cash.group.escort.repeat' /></label>");
				// 赋值押运人员1的id
				$('#escortId').val(escortId2);
			}
			return false;
		} else {
			$("#escortNoLabel1").remove();
			$("#escortNoLabel2").remove();
		}
		return true;
		/* end */
	} 	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoAddCashGroup/list"><spring:message code="store.add.cash.group.list" /></a></li><!-- 加钞组信息列表 -->
		<li class="active"><a
			href="${ctx}/store/v01/stoAddCashGroup/form?id=${stoAddCashGroup.id}">
				<%-- <shiro:hasPermission name="store:stoAddCashGroup:edit"> --%>
					<c:choose>
						<c:when test="${not empty stoAddCashGroup.id}">
							<spring:message code="store.add.cash.group.info" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code="store.add.cash.group.info" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
		</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoAddCashGroup"
		action="${ctx}/store/v01/stoAddCashGroup/save" method="post"
		class="form-horizontal">
		<!-- 加钞组id -->
		<sys:message content="${message}" />
		<form:hidden path="id" /><!-- 加钞组ID -->
		
		<!-- 加钞组名称 -->
		<div class="control-group">
			<label class="control-label"><spring:message
						code="store.add.cash.group.name" />：</label><!-- 加钞组名称 -->
			<div class="controls">
				<form:input path="groupName" id="groupName" htmlEscape="false"
						maxlength="10" class="required" style="font-size:16px"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<!-- 车辆 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code="store.add.cash.group.car" />：</label><!-- 车辆  -->
			<div class="controls">
				<form:select path="carId" id="carId" class="input-large required"><!-- items="${sto:getStoCarInfoAllList()}" -->
					<c:if test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
						<option value="${stoAddCashGroup.carId}">${stoAddCashGroup.carName}</option>
					</c:if>
					<form:options items="${stoCarInfoList}"
						itemLabel="carNo" itemValue="id" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
				<form:hidden path="carName" />
			</div>
		</div>
		
		<!-- 押运人员1 -->
		<div class="control-group" >
			<label class="control-label"><spring:message code='store.escortUser1'/>：</label><!-- 押运人员1 -->
			<div class="controls">
				<form:select path="escortNo1" id="escortNo1" class="input-large required" onchange="checkEscortselect('1')"><!-- sto:getUsersByTypeAndOffice('90', '') -->
					<c:if test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
						<option value="${stoAddCashGroup.escortNo1}">${stoAddCashGroup.escortName1}</option>
					</c:if>
					<form:options items="${stoEscortInfoList}"
						itemLabel="escortName" itemValue="id" htmlEscape="false" />
				</form:select>
				<span id="escortNoError1" class="help-inline"><font color="red">*</font></span>
				<input type="hidden" id="escortId">
				<form:hidden path="escortName1"/>
			</div>
		</div>
		
		<!-- 押运人员2 -->
		<div class="control-group" id="escortNo2Select">
			<label class="control-label"><spring:message code='store.escortUser2'/>：</label><!-- 押运人员2 -->
			<div class="controls">
				<form:select path="escortNo2" id="escortNo2" class="input-large required" onchange="checkEscortselect('2')">
					<c:choose>
						<c:when test="${stoAddCashGroup.id != null && stoAddCashGroup.id != ''}">
							<option value="${stoAddCashGroup.escortNo2}">${stoAddCashGroup.escortName2}</option>
						</c:when>
						<c:otherwise>
							<option value="">
							<spring:message code="common.select" />
						</option>
						</c:otherwise>
					</c:choose>
					<form:options items="${stoEscortInfoList}"
							itemLabel="escortName" itemValue="id" htmlEscape="false"/><!--  cssClass="required" -->
				</form:select>
				<span id="escortNoError2" class="help-inline"><font color="red">*</font></span>
				<form:hidden path="escortName2"/>
			</div>
		</div>
		<div class="form-actions">
			<%-- <shiro:hasPermission name="store:stoAddCashGroup:edit"> --%>
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.save'/>" />&nbsp;
			<%-- </shiro:hasPermission> --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/stoAddCashGroup/back'"/>
		</div>
	</form:form>
</body>
</html>