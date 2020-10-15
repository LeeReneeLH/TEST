<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 备付金管理 -->
	<title><spring:message code="clear.provision.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 备付金交入列表 -->
		<li><a href="${ctx}/clear/v03/provisionsIn/"><spring:message code="clear.provisionIn.list" /></a></li>
		<!-- 备付金交入查看 -->
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.provisionIn.view" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bankPayInfo" action="${ctx}/clear/v03/provisionsIn/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!-- 明细 -->
		<div class="row">
			<!-- 备付金明细 -->
			<div class="span7">
				<div class="control-group">
					<br/>
				</div>
				<div class="control-group">
					<!-- 客户名称 -->
					<label class="control-label"><spring:message code="clear.public.custName" />：</label>
					<div class="controls">
						<form:input id="rOffice" path="rOffice.name" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
					<!-- 总金额 -->
					<label class="control-label"><spring:message code="clear.public.totalMoney"/>：</label>
						<div class="controls">
					<input id="inAmount" name="inAmount"  type="text" value="<fmt:formatNumber value="${bankPayInfo.inAmount}" pattern="#,##0.00#"/>"  disabled/>
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
					<!-- 总金额（格式化） -->
					<label class="control-label"><spring:message code="clear.public.totalMoneyFormat"/>：</label>
					<div class="controls">
						<textarea id="inAmountBigRmb" name="inAmountBigRmb" disabled>${sto:getUpperAmount(bankPayInfo.inAmount)}</textarea>
					</div>
				</div>

				<div class="control-group">
					<!-- 银行交接人员A -->
					<label class="control-label"><spring:message code="clear.public.bankManNameA"/>：</label>
					<div class="controls">
						<form:input id="bankManNameA" path="bankManNameA" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<!-- 银行交接人员B -->
					<label class="control-label"><spring:message code="clear.public.bankManNameB"/>：</label>
					<div class="controls">
						<form:input id="bankManNameB" path="bankManNameB" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>

				<div class="control-group">
					<!-- 交接人员 -->
					<label class="control-label"><spring:message code="clear.public.transManName"/>：</label>
					<div class="controls">
						<form:input id="transManName" path="transManName" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
	    
				<div class="control-group">
					<!-- 复核人员 -->
					<label class="control-label"><spring:message code="clear.public.checkManName"/>：</label>
					<div class="controls">
						<form:input id="checkManName" path="checkManName" htmlEscape="false" readOnly="readOnly"   class="required" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
	    
				<div class="control-group">
					<!-- 备注 -->
					<label class="control-label"><spring:message code="common.remark"/>：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="60" class="input-large "  readOnly="readOnly"/>
					</div>
				</div>
			</div>
			
			<!-- 面值明细 -->
			<div class="span6">
				<sys:provisionsIn dataSource="${bankPayInfo.denominationList}"  bindListName="denominationList" totalAmountId="inAmount" allowInput="false"/>
			</div>
		</div>
	
		<div class="row">
			<div class="form-actions" style="width:100%;">
				<!-- 返回 -->
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/provisionsIn/list'" />
			</div>
		</div>
	</form:form>
</body>
</html>