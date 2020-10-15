<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分接收 -->
	<title><spring:message code="clear.orderClear.title" /><spring:message code="common.view" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(document).ready(function() {
		//$("#name").focus();
		
		$("#saveForm").validate({

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

		
		// 保存
		$("#btnSubmit").click(function() {
			$("#saveForm").submit();
			
		});
	});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 清分接收列表 -->
		<li><a href="${ctx}/clear/v03/clearConfirm/list"><spring:message code="clear.clearConfirm.title" /><spring:message code="common.list" /></a></li>
		<!-- 清分接收 -->
		<li class="active">
			<a href="#" onclick="javascript:return false;">
			<spring:message code="clear.clearConfirm.title" /><c:if test="${'1' eq formType}"><spring:message code="common.view" /></c:if>
			</a>
		</li>
	</ul>

	<form:form id="saveForm" modelAttribute="clearConfirmInfo" action="${ctx}/clear/v03/clearConfirm/confirm" method="post" class="form-horizontal" >
		<form:hidden path="inNo"/>
	</form:form>

	<form:form id="inputForm" modelAttribute="clearConfirmInfo"  method="post" class="form-horizontal">
		<sys:message content="${message}"/>

		<!-- 明细 -->
		<div class="row">
			<!-- 清分接收明细 -->
			<div class="span8" >
				<div class="control-group">
					<br/>
				</div>
				<div class="control-group">
					<!-- 客户名称 -->
					<label class="control-label"><spring:message code="clear.public.custName"/>：</label>
					<div class="controls">
						<form:input id="rOffice" path="rOffice.name" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
			
				<div class="control-group">
					<!-- 总金额 -->
					<label class="control-label"><spring:message code="clear.public.totalMoney"/>：</label>
					<div class="controls">
					<input id="inAmount" name="inAmount"  type="text" value="<fmt:formatNumber value="${clearConfirmInfo.inAmount}" pattern="#,###.##"/>"  disabled/>
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
					<!-- 总金额（格式化） -->
					<label class="control-label"><spring:message code="clear.public.totalMoneyFormat"/>：</label>
					<div class="controls">
						<input id="inAmountBigRmb" name="inAmountBigRmb"  type="text" value="${sto:getUpperAmount(clearConfirmInfo.inAmount)}"  disabled/>
					</div>
				</div>
	    
				<div class="control-group">
					<!-- 备注 -->
					<label class="control-label"><spring:message code="common.remark"/>：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="255" class="input-large "  readOnly="readOnly"/>
					</div>
				</div>
			</div>
			<!-- 面值明细 -->
			<div class="span7" >
				<sys:denominationList dataSource="${clearConfirmInfo.denominationList}"  bindListName="denominationList" totalAmountId="inAmount" allowInput="false"/>
			</div>
		</div>
		<div class="row">
			<div class="form-actions" style="width:100%;">
				<c:if test="${'2' eq formType}">
				<!-- 接收 -->
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='message.I7238'/>" />
					&nbsp;
				</c:if>
				<!-- 返回 -->
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/clearConfirm/back'"/>
			</div>
		</div>

	</form:form>
</body>
</html>
