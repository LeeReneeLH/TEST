<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<style>
	<!-- /* 输入项 */
	.item {display: inline;width:350px;float: left;}
	/* 清除浮动 */
	.clear {clear: both;}
	/* 标签宽度 */
	.label_width {width: 120px;}
	-->
	</style>
</head>
<body>
	<div style="margin-top: 5px; margin-left: 5px; margin-right: 5px;">
		<%-- 流水单号 --%>
		<div class="control-group item">
			<label class="control-label"><spring:message
					code="allocation.allId" />：</label> <label> <input type="text"
				value="${handOut.allId}" style="width: 170px; text-align: right;"
				readOnly="readOnly" />
			</label>
		</div>
		<%-- 线路类型 --%>
		<div class="control-group item">
			<label class="control-label"><spring:message
					code="allocation.tasktype" />：</label> <label> <input type="text"
				value="${fns:getDictLabel(handOut.tempFlag,'TASK_TYPE','')}" style="width: 170px; text-align: right;"
				readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>

		<div class="control-group item">
			<%-- 操作人员--%>
			<label class="control-label"><spring:message
					code="allocation.updateName" />：</label> <label> <input
				type="text" value="${handOut.updateName}"
				style="width: 170px; text-align: right;" readOnly="readOnly" />
			</label>
		</div>
		<div class="control-group item">
			<%-- 操作时间 --%>
			<label class="control-label"><spring:message
					code="allocation.updateDate" />：</label> <label> <input
				type="text"
				value="<fmt:formatDate value="${handOut.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				style="width: 170px; text-align: right;" readOnly="readOnly" />
			</label>
		</div>
		<div class="clear"></div>
		<c:if test="${handOut.carNo!=null}">
			<!-- 临时线路车辆 -->
			<div class="control-group item">
				<label class="control-label"><spring:message
						code="allocation.tasktype.car_no" />：</label> <label> <input
					type="text" value="${handOut.carNo}"
					style="width: 170px; text-align: right;" readOnly="readOnly" />
				</label>
			</div>
			<div class="clear"></div>
		</c:if>
		<c:if test="${handOut.status == '90'}">
			<!-- 撤回原因 -->
			<div class="control-group">
				<label class="control-label"><spring:message
						code="allocation.cancelReason" />：</label> <label> <textarea
						style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="2" readonly="readonly">${handOut.cancelReason}</textarea>
				</label>
			</div>
			<div class="clear"></div>
		</c:if>
		<!-- 金库交接信息 -->
		<c:if test="${handOut.status == '12'}">
			<!-- 交接授权人 -->
			<c:if test="${handoverManagerList != null}">
				<c:forEach items="${handoverManagerList}" var="manager">
					<div class="control-group item">
						<%-- 授权人--%>
						<label class="control-label"><spring:message
								code="allocation.managerName" />：</label> <label> <input
							type="text" value="${manager.escortName}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="control-group item">
						<%-- 授权原因 --%>
						<label class="control-label"><spring:message
								code="allocation.managerReason" />：</label> <label> <input
							type="text"
							value="${fns:getDictLabel(manager.managerReason,'allocation_manager_reason','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="clear"></div>
				</c:forEach>
			</c:if>
			<!-- 交接人 -->
			<c:if test="${handoverList.size() != '0'}">
				<div class="control-group">
					<label class="control-label"><spring:message
							code="allocation.handoverName" />：</label> <label> <textarea
							style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="1" readonly="readonly" ><c:forEach items="${handoverList}" var="handover">${handover.escortName}&nbsp;</c:forEach></textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
		</c:if>
		<!-- 网点交接信息 -->
		<c:if test="${handOut.status == 'pointHandover'}">
			<!-- 交接授权人 -->
			<c:if test="${pointHandoverManagerList != null}">
				<c:forEach items="${pointHandoverManagerList}" var="pointManager">
					<div class="control-group item">
						<%-- 授权人--%>
						<label class="control-label"><spring:message
								code="allocation.managerName" />：</label> <label> <input
							type="text" value="${pointManager.escortName}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="control-group item">
						<%-- 授权原因 --%>
						<label class="control-label"><spring:message
								code="allocation.managerReason" />：</label> <label> <input
							type="text"
							value="${fns:getDictLabel(pointManager.managerReason,'allocation_manager_reason','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="clear"></div>
				</c:forEach>
			</c:if>
			<!-- 交接人 -->
			<c:if test="${pointHandoverList.size() != '0'}">
				<div class="control-group">
					<label class="control-label"><spring:message
							code="allocation.handoverName" />：</label> <label> <textarea
							style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="1" readonly="readonly" ><c:forEach items="${pointHandoverList}" var="pointHandover">${pointHandover.escortName}&nbsp;</c:forEach></textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
		</c:if>
		<c:if test="${handOut.status == '11'}">
			<!-- 扫描门授权人 -->
			<c:if test="${doorManagerList != null}">
				<c:forEach items="${doorManagerList}" var="manager">
					<div class="control-group item">
						<%-- 授权人--%>
						<label class="control-label"><spring:message
								code="allocation.managerName" />：</label> <label> <input
							type="text" value="${manager.escortName}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="control-group item">
						<%-- 授权原因 --%>
						<label class="control-label"><spring:message
								code="allocation.managerReason" />：</label> <label> <input
							type="text"
							value="${fns:getDictLabel(manager.managerReason,'allocation_manager_reason','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
						</label>
					</div>
					<div class="clear"></div>
				</c:forEach>
			</c:if>
		</c:if>
		<c:if test="${handOut.status == '10'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.goodsInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- 预约物品 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.classificationInfo" /></th>
							<%-- 预约数量 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.order.number" /></th>
							<%-- 预约金额 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.cash.order.amount" /></th>
						</tr>
					</thead>
					<c:forEach items="${handOut.allAllocateItemList}" var="items">
						<tbody>
							<c:if test="${items.confirmFlag == '0'}">
								<tr>
									<td style="text-align: center;">${items.goodsName}</td>
									<td style="text-align: center;">${items.moneyNumber}</td>
									<td style="text-align: center;">${items.moneyAmount}</td>
								</tr>
							</c:if>
						</tbody>
					</c:forEach>
				</table>
			</div>
		</c:if>
		<c:if test="${handOut.status == '15'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.goodsInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- 审批物品 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.classificationInfo" /></th>
							<%-- 审批数量 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.approve.number" /></th>
							<%-- 审批金额 --%>
							<th style="text-align: center;"><spring:message
									code="allocation.approve.amount" /></th>
						</tr>
					</thead>
					<c:forEach items="${handOut.allAllocateItemList}" var="items">
						<tbody>
							<c:if test="${items.confirmFlag == '1'}">
								<tr>
									<td style="text-align: center;">${items.goodsName}</td>
									<td style="text-align: center;">${items.moneyNumber}</td>
									<td style="text-align: center;">${items.moneyAmount}</td>
								</tr>
							</c:if>
						</tbody>
					</c:forEach>
				</table>
			</div>
		</c:if>
		<c:if
			test="${handOut.status == '21'||handOut.status == '11'||handOut.status == '12'}">
			<div class="bg-light-blue disabled color-palette">
				<span style="margin-left: 20px"><spring:message
						code="store.boxInfo" /></span>
			</div>
			<div style="overflow: scroll; height: 130px">
				<table id="contentTable" class="table table-hover">
					<thead>
						<tr class="bg-light-blue disabled color-palette">
							<%-- 箱袋编号 --%>
							<th style="text-align: center;"><spring:message
									code="common.boxNo" /></th>
							<%-- 箱袋类别 --%>
							<th style="text-align: center;"><spring:message
									code="store.boxType" /></th>
							<%-- 箱袋状态 --%>
							<c:if test="${handOut.tempFlag == '01'}">
							<th style="text-align: center;"><spring:message
									code="allocation.scan.status" /></th>
							</c:if>
							<c:if test="${handOut.status == '21'||handOut.tempFlag == '02'}">
								<%-- PDA扫描时间 --%>
								<th style="text-align: center;"><spring:message
										code="allocation.scan.date" />（手持设备）</th>
							</c:if>
							<c:if test="${handOut.status == '11'}">
								<%-- 扫描门扫描时间 --%>
								<th style="text-align: center;"><spring:message
										code="allocation.scan.date" />（扫描门）</th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${handOut.allDetailList}" var="items">
							<tr>
								<td style="text-align: center;">${items.boxNo}</td>
								<td style="text-align: center;"><c:if
										test="${items.boxType=='11'}">
										<spring:message code="store.cashBox" />
									</c:if> <c:if test="${items.boxType=='12'}">
										<spring:message code="store.trailBox" />
									</c:if></td>
								<c:if test="${handOut.tempFlag == '01'}">
								<td style="text-align: center;">${fns:getDictLabel(items.scanFlag,'all_box_scanFlag',"")}</td>
								</c:if>
								<c:if test="${handOut.status == '21'||handOut.tempFlag == '02'}">
									<td style="text-align: center;"><fmt:formatDate
											value="${items.pdaScanDate}" pattern="yyyy-MM-dd HH:mm:ss " />
									</td>
								</c:if>
								<c:if test="${handOut.status == '11'}">
									<td style="text-align: center;"><fmt:formatDate
											value="${items.scanDate}" pattern="yyyy-MM-dd HH:mm:ss " /></td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>
		<div class="bg-light-blue disabled color-palette">
			<span style="margin-left: 20px"><spring:message
					code="allocation.history" /></span>
		</div>
		<div style="overflow: scroll; height: 130px">
			<table id="contentTable" class="table table-hover">
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 操作时间 --%>
						<th style="text-align: center;"><spring:message
								code="allocation.updateDate" /></th>
						<%-- 状态 --%>
						<th style="text-align: center; width: 210px"><spring:message
								code="allocation.business.status" /></th>
						<%-- 操作人 --%>
						<th style="text-align: center;"><spring:message
								code="allocation.updateName" /></th>
					</tr>
				</thead>
				<c:forEach items="${recordList}" var="items">
					<tbody>
						<tr>
							<td style="text-align: center;"><fmt:formatDate
									value="${items.updateDate}" pattern="yyyy-MM-dd HH:mm:ss " /></td>
							<td style="text-align: center;">${fns:getDictLabel(items.status,'all_status',"")}</td>
							<td style="text-align: center;">${items.updateName}</td>
						</tr>
					</tbody>
				</c:forEach>
			</table>
		</div>
	</div>

</body>
</html>