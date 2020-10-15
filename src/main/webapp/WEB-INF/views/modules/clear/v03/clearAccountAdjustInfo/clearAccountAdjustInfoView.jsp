<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 柜员调账查看 -->
	<title><spring:message code='clear.accountAdjust.view' /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">	
	$().ready(function(){
		var ct = $('#cashType').val();
		if(ct=="01"){
			$('#cashType').val('备付金');
		}else{
			$('#cashType').val('非备付金');
		}
		var payTellerBy=$("#payTellerBy").val(); 
		
		var reTellerBy=$("#reTellerBy").val(); 
		
	});
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 柜员调账列表 -->
		<li><a href="${ctx}/clear/v03/clearAccountAdjustInfo/list"><spring:message code='clear.accountAdjust.list' /></a></li>
		<!-- 柜员调账查看 -->
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='clear.accountAdjust.view' /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="clearAccountAdjustInfo"  method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<div class="row">
		
			<div class="span8">
		
					<!-- 金额类型 -->
					<br><label class="control-label"><spring:message code="clear.accountAdjust.cashType" />：</label>
					<div class="controls">
					<form:input id="cashType" path="cashType" htmlEscape="false" readOnly="readOnly" class="required" />
					</div>			
				<div class="control-group">
					<!-- 总金额 -->
					<br/><label class="control-label"><spring:message code="clear.public.totalMoney" />：</label>
					<div class="controls">
						<input id="adjustMoney" name="adjustMoney"  type="text" 
						value="<fmt:formatNumber value="${clearAccountAdjustInfo.adjustMoney}" 
						pattern="#,###.##"/>"  disabled/>
					</div>
					<!-- 总金额（格式化） -->
					<br/><label class="control-label"><spring:message code="clear.public.totalMoneyFormat" />：</label>
					<div class="controls">
						<input id="outAmountBigRmb" type="text" name="outAmountBigRmb" 
						value ="${sto:getUpperAmount(clearAccountAdjustInfo.adjustMoney)}" disabled >
					</div>
					<!-- 交款人 -->
					<br/><label class="control-label"><spring:message code='clear.accountAdjust.payTeller' />：</label>
					<div class="controls">
						<form:input id="payTellerName" path="payTellerName" style="width:197px;" readonly="true"/>
						<form:hidden path="payTellerBy" id="payTellerBy"/>
					</div>
					<!-- 收款人 -->
					<br/><label class="control-label"><spring:message code='clear.accountAdjust.reTeller' />：</label>
					<div class="controls">
						<form:input id="reTellerName" path="reTellerName" style="width:197px;" readonly="true"/>
					<form:hidden path="reTellerBy" id="reTellerBy"/>
					</div>
					<!-- 备注 -->
					<br/><label class="control-label"><spring:message code="common.remark" />：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="64" style="width:197px;"  readOnly="readOnly"/>
					</div>
				
				
				</div>
			
		</div>
		<!-- 柜员余额DIV -->
			<div class="span8"
				style="overflow-x: hidden; width: 770px; height: 500px"
				id="totalAmountDiv">
				<table id="contentListTable" class="table table-hover" style="width: 1100px">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
			    <!-- 柜员姓名 -->
				<th><spring:message code='clear.tellerAccount.tellerName'/></th>
				<!-- 余额 -->
				<th><spring:message code='clear.public.balance'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${tellerAccounts}" var="tellerAccountsMain"
				varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${tellerAccountsMain.tellerName}</td>
					<td>
						<c:choose>
							<c:when test="${tellerAccountsMain.totalAmount==null}">
							<fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
							<c:otherwise><fmt:formatNumber value="${tellerAccountsMain.totalAmount}" 
							pattern="#,##0.00#" /></c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
				
			
			
			</div>
		</div>
		<div class="row">
			<div class="form-actions" style="width:100%;">
				<input id="btnCancel" style="margin-left: 202px;border:1px solid #ccc;"  class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/clearAccountAdjustInfo/back'"/>
			</div>
		</div>
	</form:form>
</body>
</html>
