<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.goods" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
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
	</script>
	<style>
	<!--
	/* 输入项 */
/* 	.item {display: inline; float: left;} */
	/* 清除浮动 */
/* 	.clear {clear:both;} */
	/* 标签宽度 */
	.label_width {width:120px;}
	-->
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 物品列表 -->
		<li>
			<a href="${ctx}/store/v01/goods/stoGoods/">
				<spring:message code="store.goods" /><spring:message code="common.list" />
			</a>
		</li>
		<!-- 物品添加 -->
		<c:choose>
			<c:when test="${empty stoGoods.id}">
				<li>
					<a href="${ctx}/store/v01/goods/stoGoods/form?goodType=${fns:getConfig('sto.goods.goodsType.currency')}">
						<spring:message code="store.goods" /><spring:message code="common.add" />
					</a>
				</li>
			</c:when>
		</c:choose>
		<!-- 重空修改/添加/查看 -->
		<li class="active">
			<a href="${ctx}/store/v01/goods/stoGoods/form?id=${stoGoods.id}&goodType=${fns:getConfig('sto.goods.goodsType.blankBill')}">
				<!-- 修改/添加 -->
				<shiro:hasPermission name="store:goods:edit">
				<c:choose>
					<c:when test="${not empty stoGoods.id}">
						<spring:message code="common.importantEmpty"/><spring:message code="common.modify" />
					</c:when>
					<c:otherwise>
						<spring:message code="common.importantEmpty"/><spring:message code="common.add" />
					</c:otherwise>
				</c:choose>
				</shiro:hasPermission>
				<!-- 查看 -->
				<shiro:lacksPermission name="store:goods:edit">
				<spring:message code="common.importantEmpty"/><spring:message code="common.view" />
				</shiro:lacksPermission>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoGoods" action="${ctx}/store/v01/goods/stoGoods/save" method="post" class="form-horizontal">
		<form:hidden path="id" id="id"/>
		<!-- 物品类型：固定值【03：重空】  -->
		<form:hidden path="goodsType" value="${fns:getConfig('sto.goods.goodsType.blankBill')}"/>
		<sys:message content="${message}"/>
		<!-- 共通组件 -->
		<sys:blankBillselect type="blank_bill_manager" />
		<%-- 重空类型 --%>	
		<%-- <div class="control-group">
			<label class="control-label" style="width:120px;">重空类型：</label>
			<div class="controls" style="margin-left:130px; width:163px;">
				<form:select path="blankBillType" onchange="setGoodsName()"
					id="blankBillType" class="input-medium">
					<form:options items="${sto:getGoodDictList('blank_bill_type')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div> --%>
		<%-- 物品名称 --%>	
		<div class="control-group">
			<label class="control-label" style="width:120px;">
				<spring:message code="store.goodsName" />：
			</label>
			<div class="controls" style="margin-left:130px;">
				<form:input path="goodsName" id="goodsName" htmlEscape="false" maxlength="30" class="input-medium required" style="width:149px;"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 物品描述 --%>
		<div class="control-group">
			<label class="control-label" style="width:120px;"><spring:message code="store.goodsDescription" />：</label>
			<div class="controls" style="margin-left:130px;">
				<form:input path="description" id="description" htmlEscape="false" maxlength="30" class="input-medium" style="width:149px;"/>
			</div>
		</div>
		<div class="form-actions">
			<%-- 保存 --%>
			<input id="btnSubmit" class="btn btn-primary" type="submit"  value="<spring:message code='common.commit'/>" />
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick = "window.location.href ='${ctx}/store/v01/goods/stoGoods/back'"/>
		</div>
	</form:form>
</body>
</html>
