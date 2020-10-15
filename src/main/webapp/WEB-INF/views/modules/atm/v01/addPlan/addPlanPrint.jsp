<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	
<div id="printDiv">
	<table width="1000" align="center" border="1px" cellspacing="0px" style="border-collapse:collapse">
	  <tr>
	  	<!-- 标题 -->
	    <td colspan="9"><div align="center"><h1>${addPlanName}</h1></div></td>
	    </tr>
	  <tr>
	  	<td align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.addPlanId'></spring:message></td>
	  	<td colspan="2" align="center">${addPlanId}</td>
	  	<!-- 钞箱数量 -->
	    <td ><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.box.num'/></div></td>
	    <td><div align="center">${boxNum}</div></td>
	    <td><div align="center"><spring:message code='label.atm.add.box.unit'/></div></td>
	    <!-- 加钞总金额 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.amount'/></div></td>
	    <td><div align="center"><fmt:formatNumber value="${amount}" pattern="#,###" /></div></td>
	    <td><div align="center"><spring:message code='label.atm.add.money.unit'/></div></td>
	  </tr>
	  <tr>
	  	<!-- 序号 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='common.seqNo'/></div></td>
	    <!-- 终端号 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.atm.no'/></div></td>
	    <!-- 柜员号 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.account.no'/></div></td>
	    <!-- 网点名称 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.office.name'/></div></td>
	    <!-- 加钞金额（万元） -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.day.amount'/><spring:message code='label.atm.add.money.unit'/></div></td>
	    <!-- 设备型号 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.atm.typename'/></div></td>
	    <!-- 取款箱数量 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.get.box.num'/></div></td>
	    <!-- 回收箱数量 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.back.box.num'/></div></td>
	    <!-- 存款箱数量 -->
	    <td><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.deposit.box.num'/></div></td>
	  </tr>
	   <c:forEach items="${page.list}" var="addPlanItem" varStatus="status">
			<tr>
				<td><div align="center">${status.index+1}</div></td>
				<td><div align="center">${addPlanItem.atmNo}</div></td>
				<td><div align="center">${addPlanItem.atmAccount}</div></td>
				<td><div align="center">${addPlanItem.atmAddress}</div></td>
				<td><div align="center"><fmt:formatNumber value="${addPlanItem.addAmount}" pattern="#,###" /></div></td>
				<td><div align="center">${addPlanItem.atmTypeName}</div></td>
				<td><div align="center">${addPlanItem.getBoxNum}</div></td>
				<td><div align="center">${addPlanItem.backBoxNum}</div></td>
				<td><div align="center">${addPlanItem.depositBoxNum}</div></td>
			</tr>
		</c:forEach>
		<c:if test="${ not empty boxTypeCollect and boxTypeCollect.size() != 0 }">
		  <tr>
		  	<!-- 合计 -->
		  	<td  rowspan=${boxTypeCollect.size()+1} style="font-weight:bold;color:#317eac"><div align="center"><spring:message code='common.total'/></div></td>
		  	<!-- 设备型号 -->
		  	<td colspan="2"><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.box.typename'/></div></td>
		  	<!-- 取款箱数量 -->
		  	<td colspan="2"><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.get.box.num'/></div></td>
		  	<!-- 回收箱数量 -->
		  	<td colspan="2"><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.back.box.num'/></div></td>
		  	<!-- 存款箱数量 -->
		  	<td colspan="2"><div align="center" style="font-weight:bold;color:#317eac"><spring:message code='label.atm.add.plan.deposit.box.num'/></div></td>
		  </tr>
		  <c:forEach items="${boxTypeCollect}" var="boxTypeItem" varStatus="status">
			<tr>
				<td colspan="2"><div align="center">${boxTypeItem.boxTypeName}</div></td>
				
				<c:if test="${boxTypeItem.type eq '1' }">
					<td colspan="2"><div align="center">0</div></td>
					<td colspan="2"><div align="center">${boxTypeItem.boxNum}</div></td>
				<td colspan="2"><div align="center">0</div></td>
				</c:if>
				<c:if test="${boxTypeItem.type eq '2' }">
					<td colspan="2"><div align="center">${boxTypeItem.boxNum}</div></td>
					<td colspan="2"><div align="center">0</div></td>
				<td colspan="2"><div align="center">0</div></td>
				</c:if>
				<c:if test="${boxTypeItem.type eq '3' }">
					<td colspan="2"><div align="center">0</div></td>
					<td colspan="2"><div align="center">0</div></td>
				<td colspan="2"><div align="center">${boxTypeItem.boxNum}</div></td>
				</c:if>
			</tr>
		  </c:forEach>
	  </c:if>
	</table>
	<br>
</div>