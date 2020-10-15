<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 定时任务列表 --%>
	<title>定时任务列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/layer/layer-v3.1.1/layer/layer.js"></script>
	<script type="text/javascript">
		function page(n,s){
	    	location = '${ctx}/sys/quartz/list/?pageNo='+n+'&pageSize='+s;
	    }
		
		function refreshPage() {
			location.reload();
		}
		function showDetail(id) {
			layer.open({
				  type: 2 //Page层类型
				  ,area: ['850px', '570px']
			 	  ,title: '商户选择'
				  ,shade: 0.6 //遮罩透明度
				  ,scrollbar: false // 父页面滚动条禁止
				  ,maxmin: true //允许全屏最小化
				  ,anim: 2 //0-6的动画形式，-1不开启
				  ,content: '${ctx}/sys/quartz/officeList?officeId='+id
				  ,btn: ['关闭']
				  ,yes: function (index) {
			         
			         layer.close(index);
				  },
			      cancel: function(){
			         //右上角关闭回调
			      }
			});
		}
		function showCenterDetail(id) {
			layer.open({
				  type: 2 //Page层类型
				  ,area: ['850px', '570px']
			 	  ,title: '中心选择'
				  ,shade: 0.6 //遮罩透明度
				  ,scrollbar: false // 父页面滚动条禁止
				  ,maxmin: true //允许全屏最小化
				  ,anim: 2 //0-6的动画形式，-1不开启
				  ,content: '${ctx}/sys/quartz/officeList?centerOfficeId='+id
				  ,btn: ['关闭']
				  ,yes: function (index) {
			         
			         layer.close(index);
				  },
			      cancel: function(){
			         //右上角关闭回调
			      }
			}); 
		}
	</script>
</head>
<body>
<ul class="nav nav-tabs">
		<%-- 定时任务列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;">定时任务列表</a></li>
		<%-- 定时任务添加 --%>
		<li><a href="${ctx}/sys/quartz/form">定时任务添加</a></li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="quartz" action="${ctx}/sys/quartz/search" method="post" class="breadcrumb form-search ">
		<ul class="ul-form">
			<li><label>任务名称：</label><form:input path="taskName" htmlEscape="false" maxlength="15" class="input-medium"/></li>
			<li><label>机构ID：</label><form:input path="officeId" htmlEscape="false" maxlength="15" class="input-medium"/></li>
			<li><label>任务状态：</label><form:select path="status" style="width: 100px;" >
					<form:option value="">请选择</form:option>
					<form:option value="0">关闭</form:option>
					<form:option value="1">运行</form:option>
				</form:select></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<%-- 任务名 --%>
				<th><spring:message code='clear.quartz.taskName'/></th>
				<%-- 工作名 --%>
				<th><spring:message code='clear.quartz.jobName'/></th>
				<%-- 工作组 --%>
				<th><spring:message code='clear.quartz.jobGroup'/></th>
				<%-- cron表达式 --%>
				<th><spring:message code='clear.quartz.cron'/></th>
				<%-- 商户ID--%>
				<th><spring:message code='clear.quartz.officeId'/></th>
				<%-- 执行类 --%>
				<th><spring:message code='clear.quartz.executionClass'/></th>
				<%-- 任务描述 --%>
				<th><spring:message code='clear.quartz.describe'/></th>
				<%-- 任务状态 --%>
				<th><spring:message code='clear.quartz.status'/></th>
				<%-- 操作（暂停&恢复/删除） --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="quartz" varStatus="status">
				<tr>
					<!-- 序号 -->
					<td>${status.index+1}</td>
					<%-- 任务名 --%>
					<td>${quartz.taskName}</td>
					<%-- 工作名 --%>
					<td>${quartz.jobName}</td>
					<%-- 工作组 --%>
					<td>${quartz.jobGroup}</td>
					<%-- cron表达式 --%>
					<td>${quartz.cron}</td>
					<%-- 商户ID--%>
					<c:choose>
							<c:when  test="${!empty quartz.officeId}">
									<td><a  href="javascript:void(0);" onclick="showDetail('${quartz.officeId}')">
										商户详情
									</a></td>
								</c:when>
								<c:when  test="${!empty quartz.centerOfficeId}">
									<td><a  href="javascript:void(0);" onclick="showCenterDetail('${quartz.centerOfficeId}')">
										中心详情
									</a></td>
								</c:when>
							<c:otherwise>
								<td></td>
							</c:otherwise>
					</c:choose>
					
					<%-- 执行类 --%>
					<td>${quartz.executionClass}</td>
					<%-- 任务描述 --%>
					<td>${quartz.describe}</td>
					<%-- 任务状态 --%>
					<td>
						<c:choose>
							<c:when test="${quartz.status eq '1'}">
								<%-- 正常 --%>
								正常
							</c:when>
							<c:when test="${quartz.status eq '0'}">
								<%-- 等待 --%>
								关闭
							</c:when>
						</c:choose>
					</td>
					<%-- 操作 --%>
					<td>
						<c:choose>
							<c:when test="${quartz.status eq '1'}">
								<%-- 是否暂停任务? --%>
								<a href="${ctx}/sys/quartz/pauseJob?jobName=${quartz.jobName}" 
								onclick="return confirmx('是否暂停此任务', this.href)" title="暂停任务">
									<i class="fa fa-pause text-red"></i>
								</a> 
							</c:when>
							<c:otherwise>
								<%-- 是否恢复任务? --%>
								<a href="${ctx}/sys/quartz/resumeJob?jobName=${quartz.jobName}" 
								onclick="return confirmx('是否恢复此任务', this.href)" title="恢复任务">
									<i class="fa fa-play text-green"></i>
								</a> 
							</c:otherwise>
						</c:choose>
						&nbsp;
						<a href="${ctx}/sys/quartz/form?jobName=${quartz.jobName}&jobGroup=${quartz.jobGroup}" title="">
							<i class="fa fa-pencil-square-o text-green fa-lg"></i>
						</a>
						&nbsp;
						<a href="${ctx}/sys/quartz/deleteJob?jobName=${quartz.jobName}&jobGroup=${quartz.jobGroup}"
							onclick="return confirmx('是否删除此任务', this.href)" title="删除任务">
							<i class="fa fa-trash-o text-red fa-lg"></i>
						</a>
						&nbsp;
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>