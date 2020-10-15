<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
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
	<!-- 上缴登记、入库扫描页面 -->
	<div style="margin-top: 5px; margin-left: 5px; margin-right: 5px;">
			<div style="overflow-y:auto;height: 80px;">
				<div class="clear"></div>
				<div class="control-group item">
					<label class="control-label">
						<spring:message code="allocation.allId" />：
					</label>
					<label> 
						<input type="text" value="${handIn.allId}" style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<div class="clear"></div>
				
				<%-- 操作人员--%>
				<div class="control-group item">
					<label class="control-label">
						<spring:message code="allocation.updateName" />：
					</label> 
					<label> 
						<input type="text" value="${handIn.updateName}" style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<div class="control-group item">
					<%-- 操作时间 --%>
					<label class="control-label">
						<spring:message code="allocation.updateDate" />：
					</label> 
					<label> 
						<input type="text" value="<fmt:formatDate value="${handIn.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<div class="clear"></div>
				
				<c:if test = "${handIn.status == '11'}">
					<c:forEach items="${handIn.storeHandover.detailList}" var="handover">
					<!-- 扫描门授权人 -->
						<c:if test = "${handover.operationType == '3'}">
							<div class="clear"></div>
							<div class="control-group item">
								<%-- 授权人--%>
								<label class="control-label"><spring:message
										code="allocation.managerName" />：</label> <label> <input
									type="text" value="${handover.escortName}"
									style="width: 170px; text-align: right;" readOnly="readOnly" />
								</label>
							</div>
							<div class="control-group item">
								<%-- 授权原因 --%>
								<label class="control-label"><spring:message
										code="allocation.managerReason" />：</label> <label> <input
									type="text"
									value="${fns:getDictLabel(handover.managerReason,'allocation_manager_reason','')}"
									style="width: 170px; text-align: right;" readOnly="readOnly" />
								</label>
							</div>
							<div class="clear"></div>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
		</div>
		<!-- 箱子信息 -->
		<br>
		<div class="bg-light-blue disabled color-palette"><span style="margin-left: 20px;margin-top: 15px;"><spring:message code="store.boxInfo" /></span></div>
		<div style="overflow:scroll;height:130px">
			<table  id="contentTable" class="table table-hover " >
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 箱子编号 --%>
						<th style="text-align:center;"><spring:message code="common.boxNo"/></th>
						<%-- 箱子类型 --%>
						<th style="text-align:center;"><spring:message code="store.boxType"/></th>
						<c:if test = "${handIn.status == '11'}">
							<!-- 扫描门 扫描状态 -->
							<th style="text-align:center;"><spring:message code="allocation.scan.status"/></th>
						</c:if>
						<%-- 扫描时间 --%>
						<th style="text-align:center;"><spring:message code="allocation.scan.date"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${handIn.allDetailList}" var="detail">
						<tr>
							<td style="text-align:center;">${detail.boxNo}</td>
							<td style="text-align:center;">${fns:getDictLabel(detail.boxType,'sto_box_type',"")}</td>
							<!-- 在途状态显示PDA扫描时间 -->
							<c:if test = "${handIn.status == '12' || handIn.status == '90'}">
								<td style="text-align:center;">
									<fmt:formatDate value="${detail.pdaScanDate}" pattern="yyyy-MM-dd HH:mm:ss" />
								</td>
							</c:if>
							<!-- 已扫描状态显示入库扫描时间 -->
							<c:if test = "${handIn.status == '11'}">
								<!-- 入库箱子状态 -->
								<c:choose>
									<c:when test="${detail.scanFlag eq '0'}">
										<td style="text-align: center;" >${fns:getDictLabel(detail.scanFlag,'all_box_scanFlag',"")}</td>
	             					</c:when>
									<c:otherwise>
										<td style="text-align: center;">${fns:getDictLabel(detail.scanFlag,'all_box_scanFlag',"")}</td>
									</c:otherwise>
								</c:choose>
								<!-- 扫描门时间 -->
								<td style="text-align:center;">
									<fmt:formatDate value="${detail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss" />
								</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<!-- 物品信息 -->
		<br>
		<div class="bg-light-blue disabled color-palette"><span style="margin-left: 20px"><spring:message code="store.goodsInfo"/></span></div>
		<div style="overflow:scroll;height:130px">
			<table  id="contentTable" class="table table-hover">
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 物品名称 --%>
						<th style="text-align:center;"><spring:message code="store.goodsName"/></th>
						<%-- 物品数量 --%>
						<th style="text-align:center;"><spring:message code="store.goodsNum"/></th>
						<%-- 物品金额 --%>
						<th style="text-align:center;"><spring:message code="store.goodsAmount"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items = "${handIn.allAllocateItemList}" var = "items">
						<tr>
							<td style="text-align:center;">${items.goodsName}</td>
							<td style="text-align:center;">${items.moneyNumber}</td>
							<td style="text-align:center;">${items.moneyAmount}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<!-- 调拨历史 -->
		<br>
		<div class="bg-light-blue disabled color-palette"><span style="margin-left: 20px"><spring:message code="allocation.history"/></span></div>
		<div style="overflow:scroll;height:130px">
			<table  id="contentTable" class="table table-hover" >
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 更新时间 --%>
						<th style="text-align:center;"><spring:message code="allocation.updateDate"/></th>
						<%-- 当前状态 --%>
						<th style="text-align:center;"><spring:message code="allocation.business.status"/></th>
						<%-- 操作人 --%>
						<th style="text-align:center;"><spring:message code="allocation.updateName"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items = "${allocateInfoList}" var="history">
						<tr>
							<td style="text-align:center;"><fmt:formatDate value="${history.updateDate}" pattern="yyyy-MM-dd HH:mm:ss "/></td>
							<td style="text-align:center;">${fns:getDictLabel(history.status,'all_status',"")}</td>
							<td style="text-align:center;">${history.updateName}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>