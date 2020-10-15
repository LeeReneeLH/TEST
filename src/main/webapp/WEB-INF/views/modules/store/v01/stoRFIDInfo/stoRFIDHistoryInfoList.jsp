<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>箱袋历史明细</title>
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
		<%-- RFID箱袋列表 --%>
		<li><a href="${ctx}/store/v01/stoRFIDInfo/list"><spring:message code="store.rfid.list" /></a></li>
		<li class="active"><a href="#" onclick="javascript:return false;">箱袋历史明细</a></li>
	</ul>
	<form:form id="inputForm" action="" class="form-horizontal">
		<div class="row">
			<div class="span10" style="margin-top:4px">
				<div class="clear"></div>
					<div class="control-group item">
						<%-- 箱袋编号 --%>
						<label class="control-label" style="width:80px;"><spring:message code="store.rfid.box" />：</label>
						<div class="controls" style="margin-left:90px; width:170px;">
							<input type="text" id="rfid"  value="${fns:left(rfid,8)}"
							class="input-small" style="width:149px;text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 序号--%>
				<th><spring:message code="common.seqNo"/></th>
				<%-- 更新时间 --%>
				<th><spring:message code="common.updateDateTime" /></th>
				<%-- 更新者 --%>
				<th><spring:message code="common.updater" /></th>
				<%-- 初始绑定机构 --%>
				<th>初始绑定机构</th>
				<%-- 绑定物品名称 --%>
				<th>绑定物品名称</th>
				<%-- 绑定业务类型--%>
				<th>绑定业务类型</th>
				<%-- 绑定流水单号 --%>
				<th>绑定流水单号</th>
				<%-- 当前所在机构--%>
				<th>当前所在机构</th>
				<%-- 变更前所在机构--%>
				<th>变更前所在机构</th>
				<%-- 状态 --%>
				<th>状态</th>
				<%-- 使用标识 --%>
				<th>使用标识</th>
				<%-- 有效标识 --%>
				<th>有效标识</th>
				<%-- 替换卡号 --%>
				<th>替换卡号</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${historyList}" var="boxHistoryInfo" varStatus="status"> 
				<tr>
					<%-- 序号--%>
					<td>${status.index+1}</td>
					<%-- 更新时间 --%>
					<td><fmt:formatDate value="${boxHistoryInfo.oldStoRfidDenomination.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 更新者 --%>
					<td>${boxHistoryInfo.oldStoRfidDenomination.updateBy.name}</td>
					<%-- 初始绑定机构 --%>
					<td>${boxHistoryInfo.oldStoRfidDenomination.officeName}</td>
					<%-- 绑定物品名称 --%>
					<td>${boxHistoryInfo.oldStoRfidDenomination.goodsName}</td>
					<%-- 绑定业务类型--%>
					<td>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.businessType,'all_businessType','')}</td>
					<%-- 绑定流水单号 --%>
					<td>${boxHistoryInfo.oldStoRfidDenomination.allId}</td>
					<%-- 当前所在机构--%>
					<td>${boxHistoryInfo.updatedAtOfficeName}</td>
					<%-- 变更前所在机构--%>
					<td>${boxHistoryInfo.oldStoRfidDenomination.atOfficeName}</td>
					<%-- 状态 --%>
					<td>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.boxStatus,'sto_box_status','')}</td>
					<%-- 使用标识 --%>
					<td>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.useFlag,'RFID_USE_FLAG','')}</td>
					<%-- 有效标识 --%>
					<td>${fns:getDictLabel(boxHistoryInfo.oldStoRfidDenomination.delFlag,'RFID_DEL_FLAG','')}</td>
					<%-- 替换卡号 --%>
					<td>${fns:left(boxHistoryInfo.oldStoRfidDenomination.destRfid, 8)}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>