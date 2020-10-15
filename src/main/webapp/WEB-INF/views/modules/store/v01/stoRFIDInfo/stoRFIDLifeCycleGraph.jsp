<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>箱袋生命周期图</title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<link href="${ctxStatic}/css/modules/store/v01/stoRFIDInfo/rfidLifeCycle.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- RFID箱袋列表 --%>
		<li><a href="${ctx}/store/v01/stoRFIDInfo/list"><spring:message code="store.rfid.list" /></a></li>
		<li class="active"><a href="#" onclick="javascript:return false;">箱袋生命周期图</a></li>
	</ul>
	<sys:message content="${message}"/>
	<div class="container">
		<div class="step_title">
			<span>初始绑卡机构：</span>${initBindingOfficeName }<br /> 
			<span>业务类型：</span>${fns:getDictLabel(initBindingBusinessType,'all_businessType','')}
		</div>
		<div class="target_turn_down"></div>
	</div>
	<c:forEach items="${historyList}" var="boxHistoryInfo" varStatus="status">
		
		<c:if test="${status.index % 2 == 0 }">
			<div class="container-left">
		</c:if>
		
		<div class="step">
			<span>所在机构：</span><b title="${boxHistoryInfo.updatedAtOfficeName}">${boxHistoryInfo.updatedAtOfficeName}</b><br /> 
			<span>业务类型：</span>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.businessType,'all_businessType','')}<br />
			<span>状态：</span><strong>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.useFlag,'RFID_USE_FLAG','')}</strong> 
				<c:choose>
					<c:when test="${boxHistoryInfo.oldStoRfidDenomination.useFlag eq '4' }">
						<font color="red">(新卡:${fns:left(boxHistoryInfo.oldStoRfidDenomination.destRfid,8)})</font>
					</c:when>
					<c:otherwise>
						(卡号:${fns:left(boxHistoryInfo.oldStoRfidDenomination.rfid,8)})
					</c:otherwise>
				</c:choose>
				<br /> 
			<span>位置：</span><strong>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.boxStatus,'sto_box_status','')}</strong><br />
			<span>更新时间：</span><fmt:formatDate value="${boxHistoryInfo.oldStoRfidDenomination.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/><br /> 
			<span>更新者：</span>${boxHistoryInfo.oldStoRfidDenomination.updateBy.name}
		</div>
		<c:if test="${status.index % 2 == 0 and (status.index + 1) != historyList.size()}">
			<div class="target_turn_right"></div>
		</c:if>
		<c:choose>
			<c:when test="status.index == 0">
				<c:if test="${(status.index + 1) == historyList.size()}">
						<div class="target_turn_down"></div>
					</div>
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
					</div>
				</c:if>
			</c:otherwise>
		</c:choose>
		
		
	</c:forEach>
	<div class="container">
		<div class="step_title text-center">终止</div>

	</div>

</body>
</html>