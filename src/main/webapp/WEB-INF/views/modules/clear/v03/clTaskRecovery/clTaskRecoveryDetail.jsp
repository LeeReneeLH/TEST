<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 任务回收查看 -->
	<title><spring:message code="clear.task.recovery.view"/></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<meta name="decorator" content="default" />
	<style>
	<!-- /* 输入项 */
	.item {display: inline;float: left;}
	/* 清除浮动 */
	.clear {clear: both;}
	/* 标签宽度 */
	.label_width {width: 120px;}
	-->
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 任务回收列表 -->
		<li><a href="${ctx}/clear/v03/clTaskRecovery/"><spring:message code='clear.task.recovery.list'/></a></li>
		<!-- 任务回收明细 -->
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code='clear.task.recovery.detail'/></a></li>
	</ul><br/>
	<form:form id="detailForm" modelAttribute="clTaskMain" action=""
		method="post" class="form-horizontal">
		<div class="row">
			<div class="span12">
				<div class="clear"></div>
				<!-- 业务编号 -->
				<div class="control-group item">
					<label class="control-label"><spring:message code='clear.task.business.id'/>：</label>
					<div class="controls" style="margin-left: 185px;"> 
						<input type="text" id="taskNo" name="taskNo"
							value="${clTaskMain.taskNo}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</div>
				</div>
				<!-- 业务类型 -->
				<div class="control-group item">
					<label class="control-label" ><spring:message code='clear.task.business.type'/>：</label>
					<div class="controls" style="margin-left: 185px;"> 
						<input type="text" id="busType" name="busType"
							value="${fns:getDictLabel(clTaskMain.busType,'clear_businesstype','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<div class="row">
			<div class="span12">
				<div class="control-group item">
					<!-- 交接人 -->
					<label class="control-label" ><spring:message code='clear.task.handover.name'/>：</label>
					<div class="controls" style="margin-left: 185px;"> 
						<input type="text" id="joinManName" name="joinManName"
							value="${clTaskMain.joinManName}"
							class="input-medium" style="width: 170px; text-align: right;"
							readOnly="readOnly" />
					</div>
				</div>
				<div class="control-group item">
					<!-- 计划类型 -->
					<label class="control-label" ><spring:message code='clear.task.planType'/>：</label>
					<div class="controls" style="margin-left: 185px;"> 
						<input type="text" id="planType" name="planType"
							value="${fns:getDictLabel(clTaskMain.planType,'clear_plan_type','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="span12">
				<div class="control-group item">
					<!-- 任务类型 -->
					<label class="control-label" ><spring:message code='allocation.tasktype'/>：</label>
					<div class="controls" style="margin-left: 185px;">
						<input type="text" id="taskType" name="taskType"
							value="${fns:getDictLabel(clTaskMain.taskType,'clear_task_type','')}"
							class="input-medium" style="width: 170px; text-align: right;"
							readOnly="readOnly" />
					</div>
				</div>
				<div class="control-group item">
					<!-- 总金额 -->
					<label class="control-label"><spring:message
							code="clear.task.totalAmt" />：</label> 
						<div class="controls" style="margin-left: 185px;">
							 <input type="text" id="totalAmt"
								value="<fmt:formatNumber value="${clTaskMain.totalAmt}" 
								pattern="#,##0.00#" />"
								class="input-medium" style="text-align: right; width: 170px;"
								readOnly="readOnly" />
					</div>
				</div>
			</div>
		</div>
		<!-- 分隔线 -->
		<div class="row">
			<div class="span12" style="margin-top:4px">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
		<div class="row">
			<div class="span8" style="text-align: right">
				<div class="clear"></div>
				<div class="control-group item">
					<!-- 券别信息 -->
					<label class="control-label" ><spring:message code="allocation.classificationInfo" />：</label>
					<div class="controls" style="margin-left: 185px;">
						<input type="text" id="goodsId" name="goodsId"
							value="${sto:getGoodsName(clTaskMain.goodsId)}"
							style="width: 280px; text-align: right;" readOnly="readOnly" />
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			  <div class="span20" style="text-align: right">
			  	<!-- 任务明细 -->
			    <h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="clear.task.detail" /></h4>
			    <div style="overflow-y: auto; height: 515px;">
					<table id="contentTable" class="table table-hover">
						<thead>
							<tr class="bg-light-blue">
								<!-- 序号 -->
								<th style="text-align: center; width: 50px"><spring:message
										code="common.seqNo" /></th>
								<!-- 工位类型 -->
								<th style="text-align: center"><spring:message
										code="clear.task.positionType" /></th>
								<!-- 员工姓名 -->
								<th style="text-align: center"><spring:message
										code="clear.task.empName" /></th>
								<!-- 分配数量 (捆)-->
								<th style="text-align: center"><spring:message
										code="clear.task.distributionAmount" />(<spring:message code='clear.public.bundle' />)</th>
								<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
									<th style="text-align: center">${item.label}(<spring:message code='clear.public.bundle' />)</th>
								</c:forEach>
								<!-- 总分配数量 (捆)-->
								<th style="text-align: center"><spring:message
										code="clear.task.distributionTotalAmount" />(<spring:message code='clear.public.bundle' />)</th>
								<!--金额 -->
								<th style="text-align: center"><spring:message
										code="clear.public.moneyFormat" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${clTaskMain.clTaskDetailList}" var="clDetail" varStatus="status">
								<!-- 任务详细 -->
								<tr>
									<td width="10%" style="text-align: center">${status.index+1}</td>
									<td style="text-align: center;">${fns:getDictLabel(clDetail.workingPositionType,'clear_working_position_type',"")}</td>
									<td style="text-align: center;">${clDetail.empName}</td>
									<td style="text-align: center;">${clDetail.totalCount}</td>
									<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
										<c:choose>
											<c:when test="${clTaskMain.denomination eq item.value}">
												<td style="text-align: center">${clDetail.totalCount}</td>
											</c:when>
											<c:otherwise>
												<td style="text-align: center">0</td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									<td style="text-align: center;">${clDetail.totalCount}</td>
									<td style="text-align: center;"><fmt:formatNumber value="${clDetail.totalAmt}" pattern="#,##0.00#"/></td>
								</tr>
							</c:forEach>
						</tbody>
						<tr>
							<!-- 合计 -->
							<th style="text-align: center; width: 50px"><spring:message code='common.total' /></th>
							<th style="text-align: center"></th>
							<th style="text-align: center"></th>
							<th id="totalsum" style="text-align: center">${clTaskMain.totalCount}</th>
							<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
								<c:choose>
									<c:when test="${clTaskMain.denomination eq item.value}">
										<th style="text-align: center">${clTaskMain.totalCount}</th>
									</c:when>
									<c:otherwise>
										<th style="text-align: center">0</th>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<th id="totalsum" style="text-align: center">${clTaskMain.totalCount}</th>
							<th style="text-align: center"><fmt:formatNumber value="${clTaskMain.totalAmt}" pattern="#,##0.00#"/></th>
						</tr>
					</table>
				</div>
			</div>
		</div>	
		<div>
			<!-- 返回 -->
			<div class="form-actions" style="width:100%;">
			<input id="btnCancel" class="btn btn-default" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/clear/v03/clTaskRecovery/back'" />
			</div>
		</div>
	</form:form>
</body>
</html>