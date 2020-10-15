<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.initArea" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			rules:{
				rowCnt:{
					required:true, 
					digits:true, 
					min:1
				},
				colCnt:{
					required:true, 
					digits:true, 
					min:0
				},
				maxCapability:{
					required:true, 
					digits:true, 
					min:0
				}
				
			},
			submitHandler: function(form){
				loading('正在提交，请稍等...');
				form.submit();
			},
			errorPlacement: function(error, element) {
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
		// 初始化库区按钮
		$("#btnInit").click(function(){
			var message = "<spring:message code='message.I1022'/>";
			confirmx(message, initBtn);
		});
	});
	
	function initBtn() {
		$("#inputForm").attr("action", "${ctx}/store/v01/stoAreaSettingInfo/makeAreaInfos");
		$("#inputForm").submit();
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库区位置列表 --%>
		<li><a href="${ctx}/store/v01/stoAreaSettingInfo/"><spring:message code="store.areaList" /></a></li>
		<%-- 初始化库区 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.initArea" /></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoAreaSettingInfo"
		action="" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<c:if test="${fns:getUser().office.type == '7'}">
			<div class="control-group">
				<%-- 机构名称 --%>
				<label class="control-label">机构名称：</label>
				<sys:treeselect id="rofficeId" name="officeId"
					value="${stoAreaSettingInfo.officeId}" labelName="officeName"
					labelValue="${stoAreaSettingInfo.officeName}" title="<spring:message code='allocation.application.office' />"
					url="/sys/office/treeData" cssClass="required" cssStyle="margin-left: 20px; width: 150px;"
					allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"
					type="1" isAll="false"/>
			</div>
		</c:if>
		<%-- 库区类型 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaType" />：</label>
			<div class="controls">
				<form:select path="storeAreaType" id="storeAreaType" class="input-medium">
					<form:options items="${fns:getDictList('store_area_type')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<%-- 最大行数 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaMaxRow" />：</label>
			<div class="controls">
				<form:input path="rowCnt" htmlEscape="false" maxlength="5" style="width:150px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 最大列数 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaMaxCol" />：</label>
			<div class="controls">
				<form:input path="colCnt" htmlEscape="false" maxlength="5" style="width:150px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 库区最大容量 --%>
		<div class="control-group" >
			<label class="control-label"><spring:message code="store.areaMaxCapability" />：</label>
			<div class="controls">
				<form:input path="maxCapability" htmlEscape="false" maxlength="5" style="width:150px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 库区最大保存日数 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaMaxSaveDays" />：</label>
			<div class="controls">
				<form:input path="maxSaveDays" htmlEscape="false" maxlength="5" style="width:150px;" class="digits required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 库区最小保存日数 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaMinSaveDays" />：</label>
			<div class="controls">
				<form:input path="minSaveDays" htmlEscape="false" maxlength="5" style="width:150px;" class="digits required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnInit" class="btn btn-primary" type="button"
				value="<spring:message code='common.save'/>" />&nbsp;
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/stoAreaSettingInfo/list'"/>
		</div>
	</form:form>
</body>
</html>
