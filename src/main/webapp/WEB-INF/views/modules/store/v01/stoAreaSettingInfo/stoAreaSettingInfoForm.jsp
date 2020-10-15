<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.box.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	
	$("#inputForm").validate({
		rules:{
			maxCapability:{
				required:true, 
				digits:true, 
				min:1
			},
			maxSaveDays:{
                required:true, 
                digits:true, 
                min:0
            },
            minSaveDays:{
                required:true, 
                digits:true, 
                min:0
            },
            storeAreaName:{
            	required:true
            },
            sortKey:{
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
});

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库区位置列表 --%>
		<li><a href="${ctx}/store/v01/stoAreaSettingInfo/"><spring:message code="store.areaList" /></a></li>
		<%-- 初始化库区 --%>
		<li><a href="${ctx}/store/v01/stoAreaSettingInfo/initForm"><spring:message code="store.initArea" /></a></li>
		<%-- 修改库区 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.updateArea" /></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoAreaSettingInfo"
		action="${ctx}/store/v01/stoAreaSettingInfo/save" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<form:hidden path="id"/>
		<%-- 库区类型 --%>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaType" />：</label>
			<div class="controls">
				<form:select path="storeAreaType" id="storeAreaType" class="input-medium" readOnly="readOnly">
					<form:options items="${fns:getDictList('store_area_type')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<!-- 库区名称 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaName" />：</label>
			<div class="controls">
				<form:input path="storeAreaName" htmlEscape="false" maxlength="5" style="width:150px;" />
			</div>
		</div>
		<!-- 行位置 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="store.rowPosition" />：</label>
			<div class="controls">
				<form:input path="xPosition" htmlEscape="false" maxlength="5" style="width:150px;" readOnly="readOnly" />
			</div>
		</div>
		<!-- 列位置 -->
		<div class="control-group" >
			<label class="control-label"><spring:message code="store.colPosition" />：</label>
			<div class="controls">
				<form:input path="yPosition" htmlEscape="false" maxlength="5" style="width:150px;" readOnly="readOnly" />
			</div>
		</div>
		<!-- 库区最大容量 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="store.areaMaxCapability" />：</label>
			<div class="controls">
				<form:input path="maxCapability" htmlEscape="false" maxlength="5" style="width:150px;"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 最大保存日数 -->
		<div class="control-group" >
			<label class="control-label"><spring:message code="store.areaMaxSaveDays" />：</label>
			<div class="controls">
				<form:input path="maxSaveDays" htmlEscape="false" maxlength="5" style="width:150px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 最小保存日数 -->
		<div class="control-group" >
			<label class="control-label"><spring:message code="store.areaMinSaveDays" />：</label>
			<div class="controls">
				<form:input path="minSaveDays" htmlEscape="false" maxlength="5" style="width:150px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 物品ID -->
		<div class="control-group" >
			<label class="control-label">库区物品：</label>
			<div class="controls">
				<form:select id="goodsId" path="goodsId" style="width:300px;">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${goodsList}" itemValue="goodsID" itemLabel="goodsName" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<!-- 排序 -->
		<div class="control-group" >
			<label class="control-label">排序：</label>
			<div class="controls">
				<form:input path="sortKey" htmlEscape="false" maxlength="5" style="width:150px;"/>
			</div>
		</div>
		<!-- 备注 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.remark" />：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="300" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit"
				value="<spring:message code='common.save'/>" />&nbsp;
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/stoAreaSettingInfo/backToList'"/>
		</div>
	</form:form>
</body>
</html>
