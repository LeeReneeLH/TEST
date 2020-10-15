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
		<c:if test="${handIn != null}">
			<div style="margin-top: 5px; margin-left: 5px; margin-right: 5px;">
				<div style="overflow-y:auto;height: 125px;">
			<!-- 撤回、交接窗口 -->
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
			
			<c:if test="${handIn.pointHandover.detailList.size() != '0'}">
				<div class="control-group">
					<label class="control-label"><spring:message
							code="allocation.handoverName" />：</label> <label> <textarea
							style="width: 520px;height:30px scroll;display: inline-block;resize:none;" rows="1" readonly="readonly" ><c:forEach items="${handIn.pointHandover.detailList}" var="pointHandover">${pointHandover.escortName}&nbsp;</c:forEach></textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
			<!-- 撤回原因 -->
			<c:if test = "${handIn.status == '90'}">
				<div class="control-group">
					<label class="control-label">
						<spring:message code="allocation.cancelReason" />：
					</label><label> <textarea style="width: 524px;height:30px ;overFlow-y:scroll;display: inline-block;resize:none;" rows="2" readonly="readonly">${handIn.cancelReason}</textarea>
					</label>
				</div>
				<div class="clear"></div>
			</c:if>
			
			<!-- 交接信息 -->
			<c:if test = "${handIn.status == '14'}">
				<!-- 交接授权人 -->
				<c:if test="${managerList!= null}">
					<div class="clear"></div>
					<c:forEach items="${managerList}" var = "manager">
						<div class="control-group item">
							<%-- 授权人--%>
							<label class="control-label">
								<spring:message code="allocation.managerName" />：
							</label> 
							<label> 
							<input type="text" value="${manager.escortName}" style="width: 170px; text-align: right;" readOnly="readOnly" />
							</label>
						</div>
						<div class="control-group item">
							<%-- 授权原因 --%>
							<label class="control-label">
								<spring:message code="allocation.managerReason" />：
							</label> 
							<label> 
								<input type="text" value="${fns:getDictLabel(manager.managerReason,'allocation_manager_reason','')}"
									style="width: 170px; text-align: right;" readOnly="readOnly" />
							</label>
						</div>
						<div class="clear"></div>
					</c:forEach>
				</c:if>
				
				<!-- 交接人 -->
				<c:if test="${handoverList!= null}">
					<div class="clear"></div>
					<div class="control-group item">
						<label class="control-label">
							<spring:message code="allocation.handoverName" />：
							<textarea style="width: 170px;height:30px scroll;display: inline-block;resize: none;" rows="1" readonly="readonly"><c:forEach items="${handoverList}" var="handover">${handover.escortName}&nbsp;</c:forEach>
							</textarea>
						</label>
				</c:if>
			</c:if>
			</div>
		</div>
	</c:if>
		<!-- 调拨历史 -->
		<br>
		<div class="bg-light-blue disabled color-palette"><span style="margin-left: 20px"><spring:message code="allocation.history"/></span></div>
		<div style="overflow:scroll;height:130px">
			<table  id="contentTable" class="table table-hover" >
				<thead>
					<tr class="bg-light-blue disabled color-palette">
						<%-- 更新时间 --%>
						<th style="text-align:center; width:390px"><spring:message code="allocation.updateDate"/></th>
						<%-- 当前状态 --%>
						<th style="text-align:center;width:210px"><spring:message code="allocation.business.status"/></th>
						<%-- 操作人 --%>
						<th style="text-align:center;width:200px"><spring:message code="allocation.updateName"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items = "${allocateInfoList}" var = "history">
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