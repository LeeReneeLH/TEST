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
			var message = "<spring:message code='message.I1028'/>";
			confirmx(message, initBtn);
		});
	});
	
	function initBtn() {
		$("#inputForm").attr("action", "${ctx}/store/v01/storeManagementInfo/makeStoreInfo");
		$("#inputForm").submit();
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 库房管理列表 --%>
		<li><a href="${ctx}/store/v01/storeManagementInfo/graph">库房管理</a></li>
		<%-- 库房箱袋列表 --%>
		<li><a href="${ctx}/store/v01/storeGoodsInfo/list">库房箱袋列表</a></li>
		<shiro:hasPermission name="store:storeManagementInfo:edit">
			<%-- 库房管理列表 --%>
			<li><a href="${ctx}/store/v01/storeManagementInfo/">库房管理列表</a></li>
			<%-- 库房创建 --%>
			<li class="active"><a href="#" onclick="javascript:return false;">库房创建</a></li>
		</shiro:hasPermission>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="storeManagementInfo"
		action="" method="post"
		class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}" />
		<%-- 金库名称 --%>
		<div class="control-group">
			<label class="control-label">库房名称：</label>
			<div class="controls">
				<form:input path="storeName" htmlEscape="false" class="required" maxlength="10" style="width:150px;" />
				<span class="help-inline"><font color="red">*(最大10文字)</font> </span>
			</div>
		</div>
		<input id="oldStoreName" name="oldStoreName" type="hidden" value="${storeManagementInfo.storeName}">
		<%-- 业务类型 --%>
		<div class="control-group">
			<label class="control-label">业务类型：</label>
			<div class="controls">
				<form:select multiple="multiple" path="boxTypeList" 
				items="${fns:getDictList('sto_box_type')}" itemLabel="label" 
				itemValue="value" htmlEscape="false" class="input-xxlarge"/>
			</div>
		</div>
		<%-- 使用机构 --%>
		<div class="control-group">
			<label class="control-label">使用机构：</label>
			<div class="controls">
				<form:select multiple="multiple" path="officeIdList" 
				items="${allCofferOffices}" itemLabel="name" itemValue="id" 
				htmlEscape="false" class="input-xxlarge "/>
			</div>
		</div>
		<%-- 管库员 --%>
		<div class="control-group" >
			<label class="control-label">管库员：</label>
			<div class="controls">
				<form:select multiple="multiple" path="userIdList" items="${allManagerUsers}"
				 itemLabel="name" itemValue="id" htmlEscape="false" class="input-xxlarge"/>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnInit" class="btn btn-primary" type="button"
				value="<spring:message code='common.save'/>" />&nbsp;
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/store/v01/storeManagementInfo/back'"/>
		</div>
	</form:form>
</body>
</html>
