<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/sys/message/list");
				$("#searchForm").submit();
			});
		});
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		// 弹出撤回画面
		function toCancel(id) {
			var content = "<div style='padding:10px;'>撤回原因（不得少于10个字）：<textarea rows='5' maxlength='80' class='input-xxlarge' id='cancelReason' name='cancelReason'/></div>";
			top.$.jBox.open(
					content,
					"撤回", 660, 250, {
						buttons : {
							//确认
							"<spring:message code='common.confirm' />" : "ok",
							//关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var url = "${ctx}/sys/message/cancel";	
								var cancelReason = f.cancelReason;
								if(cancelReason.length<10){
									return false;
								}
								$.ajax({
									type : "POST",
									dataType : "text",
									async: false,
									url : url,
									data : {
										id : id,
										cancelReason : cancelReason
									},
									success : function(res){
										if(res=="success"){
											window.location.reload();
										}									
									}
								});
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
			});
		}
	</script>
	<style>
	.ul-form{margin:0;display:table;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="sys:message:view"><!-- 2020/04/14 hzy 消息页面添加查看权限  -->
			<li class="active"><a href="#"><spring:message code='sys.message.list' /></a></li>
		</shiro:hasPermission>
		<c:if test="${fns:getUser().userType!='15' && fns:getUser().userType!='16'}">
		<shiro:hasPermission name="sys:message:edit"><!-- 2020/04/14 hzy 消息页面添加修改权限  -->
			<li><a href="${ctx}/sys/message/form"><spring:message code='sys.message.form' /></a></li>
		</shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="message" action="${ctx}/sys/message/list" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		</div>
	</form:form>
	<div class="table-con" >
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 消息主题 --%>
				<th class="sort-column message_topic"><spring:message code='sys.message.topic' /></th>
				<%-- 发送人 --%>
				<th class="sort-column create_name"><spring:message code='sys.message.sendUser' /></th>
				<%-- 发送机构 --%>
				<th><spring:message code='sys.message.sendOffice' /></th>
				<%-- 发送时间 --%>
				<th class="sort-column create_date"><spring:message code='sys.message.sendTime' /></th>
				<%-- 操作（查看） --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="msg">
			<tr>
				<td>${msg.messageTopic}</td>
				<td>${msg.createBy.name}
					<c:if test="${msg.createBy.name == '' || msg.createBy.name == null}"><!-- 机具报警消息时 显示createName  -->
						${msg.createName}
					</c:if>
				</td>
				<td><c:if test="${msg.createBy.office.name == '' || msg.createBy.office.name == null}">
						${msg.createName}	<!-- 机具报警消息时 发送机构 显示与发送人相同createName  -->
					</c:if>
					${msg.createBy.office.name}
				</td>
				<td><fmt:formatDate value="${msg.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><c:choose>
							<c:when test="${msg.messageType=='01'&&msg.createBy.id==fns:getUser().id&&msg.delFlag=='0'}">
								<a href="${ctx}/sys/message/form?id=${msg.id}" title="编辑"><i
									class="fa fa-pencil-square fa-lg"></i></a>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/sys/message/view?id=${msg.id}" title="查看"><i
									class="fa fa-eye fa-lg"></i></a>
							</c:otherwise>
						</c:choose> 
					<c:if test="${msg.delFlag=='0'&&msg.messageType=='01'&&msg.createBy.id==fns:getUser().id}">
						<a onclick="toCancel('${msg.id}');" title="撤回"><i class="fa fa-mail-reply-all fa-lg"></i></a>
					</c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>