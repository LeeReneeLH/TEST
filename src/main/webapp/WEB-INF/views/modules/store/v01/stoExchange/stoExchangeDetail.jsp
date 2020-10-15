<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.goodsExchange" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<style>
<!--
/* 输入项 */
.item {display: inline; float: left;}
/* 清除浮动 */
.clear {clear:both;}
/* 标签宽度 */
.label_width {width:120px;}
-->
</style>
</head>
<body>
		<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoExchange/"> <spring:message
					code="store.goodsExchange.list" /></a></li>
		<shiro:hasPermission name="store:stoExchange:edit">
			<li><a href="${ctx}/store/v01/stoExchange/form"> 
			<spring:message	code="store.goodsExchange" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/store/v01/stoExchange/form?id=${stoExchangeDetail.id}"> <spring:message
			code="store.exchangeDetail" /></a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoExchange"
		action="" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<div class="breadcrumb form-search" style="height: auto;">
			<%--兑换原始物品 --%>
			<label><spring:message code="store.fromGoods" />：</label>
			<div class="clear"></div>	
			<%-- 币种 --%>
			<div class="control-group item" >
				<label class="control-label" style="width:120px;"><spring:message code="common.currency" />：</label>
				<div class="controls" style="margin-left:130px;">
				    <form:input id="currency2" path="stoGoodSelectFrom.currency" value = "${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.currency,'currency','')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<%-- 类别 --%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.classification" />：</label>
				<div class="controls" style="margin-left:130px;">
					<form:input id="classification2" path="stoGoodSelectFrom.classification" value = "${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.classification,'classification','')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<%-- 套别 --%>
			<div class="control-group item" id = "setsGroup2">
				<label class="control-label" style="width:120px;"><spring:message code="common.edition" />：</label>
				<div class="controls" style="margin-left:130px;">
				    <form:input id="edition2" path="stoGoodSelectFrom.edition" value = "${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.edition,'edition','')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<div class="clear"></div>	
			<%-- 材质--%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.cash" />：</label>
				<div class="controls" style="margin-left:130px;">
				    <form:input id="cash2" path="stoGoodSelectFrom.cash" value = "${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.cash,'cash','')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<%-- 面值 --%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.denomination" />：</label>
				<div class="controls" style="margin-left:130px;">
				    <form:input id="denomination2" path="stoGoodSelectFrom.denomination" value = "${sto:getDenLabel(stoExchangeDetail.stoGoodSelectFrom.currency, stoExchangeDetail.stoGoodSelectFrom.denomination, '')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<%-- 单位 --%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.units" />：</label>
				<div class="controls" style="margin-left:130px;">
				    <form:input id="unit2" path="stoGoodSelectFrom.unit" value = "${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.unit,'p_unit','')}${sto:getGoodDictLabel(stoExchangeDetail.stoGoodSelectFrom.unit,'c_unit','')}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
			</div>
			<div class="clear"></div>	
			<%-- 数量 --%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.number" />：</label>
				<div class="controls"  style="margin-left:130px;">
					<form:input id="changeGoodsNum" path="stoGoodSelectFrom.moneyNumber" value = "${stoExchangeDetail.changeGoodsNum}" htmlEscape="false" maxlength="9" style="width:150px;" readonly="true" />
				</div>
		    </div>
		    <div class="clear"></div>	
	    </div>
	    
			<label style ="margin-left:25px;"><spring:message code="store.exchangeDetail" />：</label>
			<div class="clear"></div>
			<%-- 总金额 --%>
			<div class="control-group item">
			<label class="control-label" style="width:200px; margin-left:40px;">
				<spring:message code="common.totalMoney"/>：<fmt:formatNumber value="${stoExchangeDetail.changeAmount}" pattern="#,##0.00#"/>
			</label>
			</div>
			<div class="clear"></div>
	   	<%-- 出库现金详细信息 --%>
	    <div id="detailDiv" style="margin-left:102px;height:192px;width:800px;overflow:auto;overflow-x:hidden;">


		
			<table class="table  table-hover">
				<thead>
					<tr>
						<%-- 币种 --%>
						<th style="text-align:center"><spring:message code='common.currency'/></th>
						<%-- 类别 --%>
						<th style="text-align:center"><spring:message code="common.classification" /></th>
						<%-- 现金材质 --%>
						<th style="text-align:center"><spring:message code='common.cash'/></th>
						<%-- 套别 --%>
						<th style="text-align:center"><spring:message code="common.edition" /></th>
						<%-- 面值 --%>
						<th style="text-align:center"><spring:message code='common.denomination'/></th>
						<%-- 单位 --%>
						<th style="text-align:center"><spring:message code="common.units" /></th>
						<%-- 数量 --%>
						<th style="text-align:center"><spring:message code="common.number" /></th>
						<%-- 金额 --%>
						<th style="text-align:center"><spring:message code="common.amount" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${stoExchangeDetail.stoExchangeGoodList}" var="item" varStatus="status">
						<tr>
							<%-- 币种 --%>
							<td style="text-align:center">
								${sto:getGoodDictLabel(item.currency,'currency',"")}
							</td>
							<%-- 类别 --%>
							<td style="text-align:center">
								${sto:getGoodDictLabel(item.classification,'classification',"")}
							</td>
							<%-- 现金材质 --%>
							<td style="text-align:center">
								${sto:getGoodDictLabel(item.cash,'cash',"")}
							</td>
							<%-- 套别 --%>
							<td style="text-align:center">
								${sto:getGoodDictLabel(item.edition,'edition',"")}
							</td>
							<%-- 面值 --%>
							<td style="text-align:center">
								${sto:getDenLabel(item.currency, item.denomination, "")}</td>
							</td>
							<%-- 单位 --%>
							<td style="text-align:center">
								${sto:getGoodDictLabel(item.unit,'c_unit',"")}${sto:getGoodDictLabel(item.unit,'p_unit',"")}
							</td>
							<%-- 数量 --%>
							<td style="text-align:right">
								${item.num}
							</td>
							<%-- 金额 --%>
							<td style="text-align:right">
								<fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		 
		<div class="form-actions">
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/store/v01/stoExchange/back'"/>
		</div>
	</form:form>
	
</body>
</html>
