<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%-- 箱袋调拨历史 --%>
<title><spring:message code="store.rfid.allocation.history"/></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<link href="${ctxStatic}/css/modules/store/v01/stoRFIDInfo/rfidLifeCycle.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(document).ready(function() {
		// 查询
		$("#btnSubmit").click(function(){
			$("#searchForm").attr("action", "${ctx}/store/v01/stoBoxInfo/getStoBoxHistoryGraph?rfid=${rfid}&boxNo=${boxNo}");
			$("#searchForm").submit();
		});
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 箱袋信息列表 --%>
		<li><a href="${ctx}/store/v01/stoBoxInfo/list"><spring:message code="store.boxInfo.List" /></a></li>
		<%-- 箱袋调拨历史 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="store.rfid.allocation.history"/></a></li>
	</ul>
	<div class="row" style="margin-left: 10px;">
		<form:form id="searchForm" modelAttribute="stoBoxInfoHistory" action="#" method="post" class="breadcrumb form-search">
			<input id="rfid" value="${rfid}" type="hidden"/>
			<input id="boxNo" value="${boxNo}" type="hidden"/>
			<label><spring:message code="common.startDate"/>：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			   value="<fmt:formatDate value="${stoBoxInfoHistory.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeEnd\',{d:-7})}',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			 <label><spring:message code="common.endDate"/>：</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
		       value="<fmt:formatDate value="${stoBoxInfoHistory.createTimeEnd}" pattern="yyyy-MM-dd"/>"
		       onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'#F{$dp.$D(\'createTimeStart\',{d:7})||\'%y-%M-%d\'}'});"/>
			<!-- 查询按钮 -->
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
		</form:form>
	</div>
	<div class="row" style="margin-left: 50px; margin-top: 10px;">
		<sys:message content="${message}"/>
		<div class="container">
			<div class="step_title text-center"><spring:message code="common.start"/></div>
			<div class="target_turn_down"></div>
		</div>
		<c:forEach items="${historyList}" var="boxHistoryInfo" varStatus="status">
			
			<c:if test="${status.index % 2 == 0 }">
				<div class="container-left">
			</c:if>
			<div class="step"> 
				<%-- 绑卡机构 --%>
				<span><spring:message code="store.rfid.tiedCard.officeName"/>：</span><b title="${initBindOfficeName}">${initBindOfficeName}</b><br /> 
				<%-- 箱袋编号 --%>
				<span><spring:message code="store.rfid.box"/>：</span><strong>${boxNo}</strong><br />
				<%-- RFID卡号 --%>
				<span><spring:message code="store.rfid.name"/>：</span><strong>${rfid}</strong><br />
				<c:choose>
					<c:when test="${not empty boxHistoryInfo.authorizeBy }">
						<div style="color: red;">
							<%-- 更新状态 --%>
							<span style="color: red;"><spring:message code="store.update.status"/>：</span><strong style="color: red;">${fns:getDictLabel(boxHistoryInfo.stoBoxInfo.boxStatus,'sto_box_status','')}</strong> <br />
							<%-- 授权时间 --%>
							<span style="color: red;"><spring:message code="store.authorize.time"/>：</span><fmt:formatDate value="${boxHistoryInfo.privilegedTime}" pattern="yyyy-MM-dd HH:mm:ss"/><br /> 
							<%-- 授权人 --%>
							<span style="color: red;"><spring:message code="store.authorize.person"/>：</span>${boxHistoryInfo.authorizer}
						</div>
					</c:when>
					<c:otherwise>
						<%-- 状态 --%>
						<span><spring:message code="common.status"/>：</span><strong>${fns:getDictLabel(boxHistoryInfo.stoBoxInfo.boxStatus,'sto_box_status','')}</strong> <br />
						<%-- 更新时间 --%>
						<span><spring:message code="common.updateDateTime"/>：</span><fmt:formatDate value="${boxHistoryInfo.stoBoxInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/><br /> 
						<%-- 更新人 --%>
						<span><spring:message code="common.updatePerson"/>：</span>${boxHistoryInfo.stoBoxInfo.updateName}
					</c:otherwise>
				</c:choose>
			</div>
			<c:if test="${status.index % 2 == 0 and (status.index + 1) != historyList.size()}">
				<div class="target_turn_right"></div>
			</c:if>
			<c:choose>
				<c:when test="status.index == 0">
					<c:if test="${(status.index + 1) == historyList.size()}">
							<div class="target_turn_down"></div>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${status.index % 2 == 1 or (status.index + 1) == historyList.size()}">
						<c:choose>
							<c:when test="${(status.index + 1) == historyList.size() and historyList.size() % 2 == 0}">
								<div class="target_turn_down_left"></div>
							</c:when>
							<c:when test="${(status.index + 1) == historyList.size() and historyList.size() % 2 == 1}">
								<div class="target_turn_down"></div>
							</c:when>
							<c:when test="${(status.index + 1) != historyList.size()}">
								<div class="target_turn_down_left"></div>
							</c:when>
						</c:choose>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<div class="container">
			<%-- 结束 --%>
			<div class="step_title text-center"><spring:message code="common.end"/></div>
		</div>
	</div>
</body>
</html>