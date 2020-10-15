<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 预约清分查看 -->
	<title><spring:message code="clear.orderClear.title" /><spring:message code="common.view" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//取得一列的合计值
			var sumValue1 = getSumValue('columnValue1');		//未清分(捆)
			var sumValue2 = getSumValue('columnValue2');		//已清分(捆)
			var sumTotalAmt = getSumValue('totalAmt');			//金额(元)
			//合计值的设定
			$("#daySumValue1Show").html(formatCurrencyNum(sumValue1));
			$("#daySumValue2Show").html(formatCurrencyNum(sumValue2));
			$("#daySumAmountShow").html(formatCurrencyNum(sumTotalAmt));
	
		});
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 预约清分列表 -->
		<li><a href="${ctx}/clear/v03/orderClear/list"><spring:message code="clear.orderClear.title" /><spring:message code="common.list" /></a></li>
		<!-- 预约清分查看 -->
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.orderClear.title" /><spring:message code="common.view" /></a></li>
	</ul>

	<form:form id="inputForm" modelAttribute="orderClearInfo"  method="post" class="form-horizontal">
		<sys:message content="${message}"/>

		<!-- 明细 -->
		<div class="row">
			<!-- 预约清分明细 -->
			<div class="span7" style="margin-top:-20px;">
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
					<input id="inAmount" name="inAmount"  type="text" value="<fmt:formatNumber value="${orderClearInfo.inAmount}" pattern="#,###.##"/>"  disabled/>
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
					<!-- 总金额（格式化） -->
					<label class="control-label"><spring:message code="clear.public.totalMoneyFormat"/>：</label>
					<div class="controls">
						<input id="inAmountBigRmb" name="inAmountBigRmb"  type="text" value="${sto:getUpperAmount(orderClearInfo.inAmount)}"  disabled/>
					</div>
				</div>
	    
				<div class="control-group">
					<!-- 备注 -->
					<label class="control-label"><spring:message code="common.remark"/>：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="255" class="input-large "  readOnly="readOnly"/>
					</div>
				</div>
				
				
				<%-- 所属商行当日合计 --%>
				<div class="span5" style="margin-top:10px;">
					<div >
					
						<table id="contentTable" class="table table-hover" >
						<thead>
								<tr>
									<%-- 面值 --%>
									<th style="text-align:center;line-height:14px;"><spring:message code="common.denomination" /></th>
									<%-- 未清分(捆) --%>
									<th style="text-align: center;line-height:14px;" ><spring:message code="clear.public.noneClear"/>(<spring:message code="clear.public.bundle"/>)</th>
									<%-- 已清分(捆) --%>
									<th style="text-align:center;line-height:14px;"><spring:message code="clear.public.haveClear"/>(<spring:message code="clear.public.bundle"/>)</th>
									<!-- 金额(元) -->
									<th style="text-align:center;line-height:14px;"><spring:message code="clear.public.moneyFormat"/></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${totalDenomList}" var="varItem" varStatus="status">
									<tr>
										<input  UseType="columnValue1"  type="hidden" value="${varItem.columnValue1}"/>
										<input  UseType="columnValue2"  type="hidden" value="${varItem.columnValue2}"/>
										<input  UseType="totalAmt"  type="hidden" value="${varItem.totalAmt}"/>
										<%-- 序号 --%>
										<td style="text-align:center;line-height:14px;">${varItem.moneyName}</td>
										<%-- 未清分(捆) --%>
										<td style="text-align:right;line-height:14px;">${varItem.columnValue1}</td>
										<%-- 已清分(捆) --%>
										<td style="text-align:right;line-height:14px;">${varItem.columnValue2}</td>
										<!-- 金额(元)  -->
										<td style="text-align:right;line-height:14px;"><fmt:formatNumber value="${varItem.totalAmt}" pattern="###,###,###,##0" /></td>
										
									</tr>
								</c:forEach>
								<!-- 当日合计 -->
								<tr>
									<!-- 合计 -->
									<td style="text-align:center;" ><spring:message code="common.total" /></td>
									<%-- 未清分(捆) --%>
									<td style="text-align:right;line-height:14px;">
										<label id="daySumValue1Show"></label>
									</td>
									<%-- 已清分(捆) --%>
									<td style="text-align:right;line-height:14px;">
										<label id="daySumValue2Show"></label>
									</td>
									<!-- 金额(元)  -->
									<td style="text-align:right;line-height:14px;">
										<label id="daySumAmountShow"></label>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				
				
			</div>
			<!-- 面值明细 -->
			<div class="span7" >
				<sys:denominationList dataSource="${orderClearInfo.denominationList}"  bindListName="denominationList" totalAmountId="inAmount" allowInput="false"/>
			</div>
		</div>
		<div class="row">
			<div class="form-actions" style="width:100%;">
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/orderClear/back'"/>
			</div>
		</div>

	</form:form>
</body>
</html>
