<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通信管理</title>
	<meta name="decorator" content="default"/>
	<style>
		/* 输入项 */
		.item {display: inline; float: left;}
		/* 清除浮动 */
		.clear {clear:both;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/autoVaultCommunication/list">通信列表</a></li>
		<li class="active"><a href="#" onclick="javascript:return false;">通信详情</a></li>
	</ul>
	<br>
	<div id="comMessage" class="form-horizontal">
		<div class="control-group">
			<label class="control-label">通信状态:</label>
			<div class="controls">
				<input value="${fns:getDictLabel(autoVaultCommunication.status,'COMMUNICATION_STATUS','')}" class="input-medium" type="text" readonly="readonly"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">通信消息:</label>
			<div class="controls">
				<textarea style="width: 400px;height: 20px;" rows="3" cols="30" readonly="readonly">${autoVaultCommunication.message }</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">请求报文:</label>
			<div class="controls">
				<textarea style="width: 400px;height: 80px;" rows="3" cols="30" readonly="readonly">${autoVaultCommunication.inJson }</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">返回报文:</label>
			<div class="controls">
				<textarea style="width: 400px;height: 80px;" rows="3" cols="30" readonly="readonly">${autoVaultCommunication.outJson }</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">异常信息:</label>
			<div class="controls">
				<textarea style="width: 400px;height: 80px;" rows="3" cols="30" readonly="readonly">${autoVaultCommunication.exception }</textarea>
			</div>
		</div>
		<div class="control-group item">
			<label class="control-label">创建人:</label>
			<div class="controls">
				<input type="text" id="createName" value="${autoVaultCommunication.createBy.name }"
				class="input-medium" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group item">
			<label class="control-label" style="width: 80px;">创建时间:</label>
			<div class="controls" style="margin-left: 100px;">
				<input type="text" id="createDate" value="<fmt:formatDate value="${autoVaultCommunication.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				class="input-medium" readOnly="readOnly"/>
			</div>
		</div>
		<div class="clear"></div>
		<div class="control-group item">
			<label class="control-label">更新人:</label>
			<div class="controls">
				<input type="text" id="updateName" value="${autoVaultCommunication.updateBy.name }"
				class="input-medium" readOnly="readOnly"/>
			</div>
		</div>
		<div class="control-group item">
			<label class="control-label" style="width: 80px;">更新时间:</label>
			<div class="controls" style="margin-left: 100px;">
				<input type="text" id="updateDate" value="<fmt:formatDate value="${autoVaultCommunication.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				class="input-medium" readOnly="readOnly"/>
			</div>
		</div>
		<div class="clear"></div>
	</div>
	<div class="form-actions" style="margin-left: 60px;">
		<input id="btnCancel" class="btn btn-primary" type="button" value="返 回" onclick="window.location.href ='${ctx}/sys/autoVaultCommunication/back'"/>
	</div>
</body>
</html>