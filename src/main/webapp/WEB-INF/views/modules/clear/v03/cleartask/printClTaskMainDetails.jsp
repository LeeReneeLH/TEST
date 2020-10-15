<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
</head>
<body>
	<div id="printDiv" >
		<c:forEach var="i" begin="1" end="${size}" varStatus="status">
			<div style="page-break-after: always;">
				<div style="text-align: center;">
					<!-- 当前清分中心 -->
					<h1 style="text-align: center; font-weight: bold; color: black">
					<font size="5">${user.office.name}</font></h1>
				</div>
				<div style="text-align: center;">
					<label style="text-align: center; margin-top: 10px; font-weight: bold;">
					<!-- 人民币清分分配登记簿 -->
					<font size="3"><spring:message code="clear.task.allotLable" /></font></label>
				</div>
				<!-- 时间 -->
				<div style="float: left; width: 100%; position: relative; height: 20px;">
					<div style="width: 98%; text-align: right">${fns:getDate('yyyy-MM-dd HH:mm:ss')}</div>
				</div>
				<table id="contentTable" style="border: 1px black solid; border-collapse: collapse; margin-left: 2%; 
						width: 96%; text-align: center" border="1">
					<thead>
						<tr>
							<!-- 工位类型 -->
							<th style="border: 1px solid;"><spring:message code="clear.task.positionType" /></th>
							<!-- 员工姓名 -->
							<th style="border: 1px black solid;"><spring:message code="clear.task.empName" /></th>
							<!-- 面值 -->
							<th style="border: 1px black solid;"><spring:message code="common.denomination" /></th>
							<!-- 捆数 -->
							<th style="border: 1px black solid;"><spring:message code="clear.task.totalCount" /></th>
							<!-- 金额（元） -->
							<th style="border: 1px black solid;"><spring:message code="clear.public.moneyFormat" /></th>
							<!-- 收款人签章 -->
							<th style="border: 1px black solid;"><spring:message code="clear.public.payeeSignature" /></th>
							<!-- 交款人签章 -->
							<th style="border: 1px black solid;"><spring:message code="clear.public.payerSignature" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${clTaskDetailList}" var="clTaskMains">
							<c:if test="${clTaskMains.totalCount!=null && clTaskMains.totalCount!='' && clTaskMains.totalCount!='0' }">
								<tr>
									<!-- 工位类型 -->
									<td style="border: 1px black solid;">${fns:getDictLabel(clTaskMains.workingPositionType,'clear_working_position_type',"")}</td>
									<!-- 员工姓名 -->
									<td style="border: 1px black solid;">${clTaskMains.empName}</td>
									<!-- 面值 -->
									<td style="border: 1px black solid;">${sto:getGoodDictLabel(clTaskMains.denomination, 'cnypden', "")}</td>
									<!-- 捆数 -->
									<td style="border: 1px black solid;">${clTaskMains.totalCount}</td>
									<!-- 金额 (元)-->
									<td style="border: 1px black solid; text-align: right;"><fmt:formatNumber
											value="${clTaskMains.totalAmt}" pattern=" ##,###.00" /></td>
									<!-- 收款人签章 -->
									<td style="border: 1px black solid;"></td>
									<!-- 交款人签章 -->
									<td style="border: 1px black solid;"></td>
								</tr>
							</c:if>
						</c:forEach>
						<!-- 判断是否是最后一次循环 -->
						<c:if test="${status.last}">
							<!-- 判断数据是否达到10行如果不足进行补充 -->
							<c:if test="${(fn:length(clTaskDetailList)%10)!= 0}">
								<c:forEach var="i" begin="1" end="${10-fn:length(clTaskDetailList)%10}">
									<tr>
										<td style="height:20px; border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
										<td style="border: 1px black solid;"></td>
									</tr>
								</c:forEach>
							</c:if>
						</c:if>
						<tr>
							<!-- 合计 -->
							<td style="border: 1px solid;"><spring:message code="common.total" /></td>
							<td style="border: 1px black solid;"></td>
							<td style="border: 1px black solid;"></td>
							<!-- 总捆数 -->
							<td style="border: 1px black solid;">${totalCount}</td>
							<!-- 总金额 -->
							<td style="border: 1px black solid; text-align: right;"><fmt:formatNumber
									value="${totalAmt}" pattern=" ##,###.00" /></td>
							<td style="border: 1px black solid;"></td>
							<td style="border: 1px black solid;"></td>
						</tr>
					</tbody>
				</table>
				<br><br>
				<!-- 制单人 -->
				<div style="float: left; width: 100%; position: relative; height: 20px;">
					<div style="width: 98%; text-align: right;"><spring:message code="clear.public.preparedBy" />：${user.name}&nbsp;&nbsp;&nbsp;&nbsp;第${status.count}/${size}页</div>
				</div>
			</div>
		</c:forEach>
	</div>
</body>
</html>