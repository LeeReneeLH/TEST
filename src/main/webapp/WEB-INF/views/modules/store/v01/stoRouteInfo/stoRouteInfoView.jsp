<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%@include file="/WEB-INF/views/include/treeview.jsp"%>
<title><spring:message code="store.route.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	/* 初始化加载明细 */
	$(document).ready(function() {
		disabledUserUpdate();
		var setting = {check:{enable:true},view:{selectedMulti:false},
				data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
					tree2.checkNode(node, node.checked, true,true);
					return false;
				}}};  
		var zNodes2=[
			<c:forEach items="${officeList}" var="office">
				{
					id:'${office.id}', pId:'${not empty office.parent?office.parent.id:0}', name:"${office.name}", type:"${office.type}",nocheck:'true'
				},
					
            </c:forEach>];
	// 初始化树结构
	var tree2 = $.fn.zTree.init($("#officeTree"), setting, zNodes2);
	
	// 默认展开全部节点
	tree2.expandAll(true);
	// 显示机构
	$("#officeTree").show();
	});
	function disabledUserUpdate() {
		$("form input").prop("readonly", true);
	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoRouteInfo/"><spring:message code="store.route.list" /></a></li>
		<li class="active">
				<a href=" " onclick="javascript:return false;"><spring:message code="store.route" /><spring:message code="common.view" /></a>
		</li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoRouteInfo"
		action="" method="post"
		class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label"><spring:message code="store.routeName" />：</label>
			<div class="controls">
				<form:input path="routeName" htmlEscape="false" maxlength="25" class="required" />
			</div>
		</div>
		<!-- 是否使用押运人员开关 -->
		<c:if test="${fns:getConfig('route.used.escort')=='1'}">
		<div class="control-group">
			<label class="control-label"><spring:message code="store.escortUser1" />：</label>
			<div class="controls">
				<form:input path="escortInfo1" value = "${stoRouteInfo.escortInfo1.escortName}" htmlEscape="false" maxlength="25" class="required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.escortUser2" />：</label>
			<div class="controls">
				<form:input path="escortInfo2" value = "${stoRouteInfo.escortInfo2.escortName}" htmlEscape="false" maxlength="25" class="required" />
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">车牌号码：</label>
			<div class="controls">
			<form:input path="carNo" value = "${stoRouteInfo.carNo}" htmlEscape="false" maxlength="25" class="required" />
			</div>
		</div>
		<div class="control-group" style="width:488px;min-height: 200px; max-height: 200px; overflow:auto; ">
			<label class="control-label">线路下网点：</label>
			<div class="controls">
				<div id="officeTree" class="ztree" style="margin-top: 3px; float: left;"></div>
				<form:hidden path="officeIds"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" 
				value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/store/v01/stoEscortInfo/back'"/>
		</div>
	</form:form>
</body>
</html>
