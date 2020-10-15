<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 定时任务添加 --%>
	<title>定时任务添加</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/layer/layer-v3.1.1/layer/layer.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$.validator.addMethod("codeValidate", function ( value, element ) {
				return this.optional(element) || /^[a-zA-Z0-9]{0,19}$/.test(value);
				},"1-20位字母或数字"
			);
			$("#inputForm").validate({
				submitHandler : function(form) {
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorPlacement : function(error, element) {
					if (element.is(":checkbox")
							|| element.is(":radio")
							|| element.parent().is(
									".input-append")) {
						error.appendTo(element.parent()
								.parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
	     	// 提交按钮
			$("#btnSubmit").click(function(){
				$("#inputForm").attr("action","${ctx}/sys/quartz/addJob");
				$("#inputForm").submit();
			});
	     	
			// 提交按钮
			$("#btnSubmitCron").click(function(){
				$("#inputForm").attr("action","${ctx}/sys/quartz/updateJobCron");
				$("#inputForm").submit();
			});
			selectReport();
		});
		
		function cronEditor(){
			layer.open({
				  type: 2 //Page层类型
				  ,area: ['850px', '570px']
			 	  ,title: 'Cron表达式生成器'
				  ,shade: 0.6 //遮罩透明度
				  ,scrollbar: false // 父页面滚动条禁止
				  ,maxmin: true //允许全屏最小化
				  ,anim: 2 //0-6的动画形式，-1不开启
				  ,content: '${ctxStatic}/Cron/index.htm'
				  ,btn: ['确定','关闭']
				  ,yes: function (index) {
			         //获取选择的row,并加载到页面
			         var row = window["layui-layer-iframe" + index].callbackdata();
			         $("#cron").val(row);
			         layer.close(index);
				  },
			      cancel: function(){
			         //右上角关闭回调
			         
			      }
			});    
		}
		function showDetail(){
			var officeId = $('#officeId').val();
			var id = $('#id').val();
			var content = '${ctx}/sys/quartz/officeIdList';
			if (id!=null) {
				content = '${ctx}/sys/quartz/officeIdList?officeId='+officeId+'&id='+id;
			}
			layer.open({
				  type: 2 //Page层类型
				  ,area: ['850px', '570px']
			 	  ,title: '商户选择'
				  ,shade: 0.6 //遮罩透明度
				  ,scrollbar: false // 父页面滚动条禁止
				  ,maxmin: true //允许全屏最小化
				  ,anim: 2 //0-6的动画形式，-1不开启
				  ,content: content
				  ,btn: ['确定','关闭']
				  ,yes: function (index) {
			         //获取选择的row,并加载到页面
			         var row = window["layui-layer-iframe" + index].callbackdata();
			         $("#officeId").val(row);
			         layer.close(index);
				  },
			      cancel: function(){
			         //右上角关闭回调
			      }
			});    
		}
		function showCenterDetail(){
			var centerOfficeId = $('#centerOfficeId').val();
			var executionClass = $('#executionClass').val();
			var id = $('#id').val();
			var content = '${ctx}/sys/quartz/centerOfficeIdList';
			if (id!=null) {
				content = '${ctx}/sys/quartz/centerOfficeIdList?centerOfficeId='+centerOfficeId+'&id='+id+'&executionClass='+executionClass;
			}
			layer.open({
				  type: 2 //Page层类型
				  ,area: ['850px', '570px']
			 	  ,title: '中心选择'
				  ,shade: 0.6 //遮罩透明度
				  ,scrollbar: false // 父页面滚动条禁止
				  ,maxmin: true //允许全屏最小化
				  ,anim: 2 //0-6的动画形式，-1不开启
				  ,content: content
				  ,btn: ['确定','关闭']
				  ,yes: function (index) {
			         //获取选择的row,并加载到页面
			         var row = window["layui-layer-iframe" + index].callbackdata();
			         $("#centerOfficeId").val(row);
			         layer.close(index);
				  },
			      cancel: function(){
			         //右上角关闭回调
			      }
			});    
		}
		function selectReport(){
			var reportType = $('#reportType').val();
			   if(reportType === "0"){
				   $('#centerOfficeId').val("");
				   $("#center").css("display","none");
				   $("#merchants").css("display","block");
			   }else if(reportType === "1"){
				   $('#officeId').val("");
				   $("#merchants").css("display","none");
				   $("#center").css("display","block");
			   }else {
				   $('#officeId').val("");
				   $('#centerOfficeId').val("");
				   $("#merchants").css("display","none");
				   $("#center").css("display","none");
			   }
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 定时任务列表 --%>
		<li><a href="${ctx}/sys/quartz/list">定时任务列表</a></li>
		<c:choose>
			<c:when test="${empty quartz.id}">
				<%-- 定时任务添加 --%>
				<li class="active"><a href="#" onclick="javascript:return false;">定时任务添加</a></li>
			</c:when>
			<c:otherwise>
				<%-- 定时任务编辑 --%>
				<li class="active"><a href="#" onclick="javascript:return false;">定时任务编辑</a></li>
			</c:otherwise>
		</c:choose>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="quartz" action="" method="post" class="form-horizontal">
		
		<sys:message content="${message}"/>
		<form:hidden  path="id"/>
		<div class="control-group">
			<%-- 任务名 --%>
			<label class="control-label"><spring:message code='clear.quartz.taskName'/>：</label>
			<div class="controls">
				<form:input id="taskName" path="taskName" htmlEscape="false" maxlength="20" class="required" readonly="${empty quartz.id ? false : true}"/>
				<c:if test="${empty quartz.id }">
					<span class="help-inline"><font color="red">*</font></span>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<%-- 工作名--%>
			<label class="control-label"><spring:message code='clear.quartz.jobName'/>：</label>
			<div class="controls">
				<form:input path="jobName" htmlEscape="false" maxlength="20" class="required codeValidate" readonly="${empty quartz.id ? false : true}"/>
				<c:if test="${empty quartz.id }">
					<span class="help-inline"><font color="red">*</font></span>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<%-- 工作组--%>
			<label class="control-label"><spring:message code='clear.quartz.jobGroup'/>：</label>
			<div class="controls">
				<form:input path="jobGroup" htmlEscape="false" maxlength="20" class="required codeValidate" readonly="${empty quartz.id ? false : true}"/>
				<c:if test="${empty quartz.id }">
					<span class="help-inline"><font color="red">*</font></span>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<%-- cron表达式 --%>
			<label class="control-label"><spring:message code='clear.quartz.cron'/>：</label>
			<div class="controls">
				<form:input path="cron" htmlEscape="false" maxlength="50" class="required"  readonly="true"/>
				<span class="help-inline"><font color="red">*</font></span>
				<input id="btnEdit" class="btn btn-primary" type="button" value="编辑" onclick="cronEditor()"/>
			</div>
		</div>
		<div class="control-group">
			<%-- 结算方式 --%>
			<label class="control-label">结算方式：</label>
			
			<div class="controls">
				<form:select path="reportType" style="width: 210px;" onchange="selectReport()">
					<form:option value="">请选择</form:option>
					<form:option value="0">商户结算</form:option>
					<form:option value="1">中心结算</form:option>
				</form:select>
			</div>
		</div>
		<div id="merchants" class="control-group" style="display:none;">
			<%-- 商户ID --%>
			<label class="control-label">商户ID：</label>
			<div class="controls">
				<form:input id="officeId" path="officeId" htmlEscape="false"   readonly="true"/>
				<span class="help-inline"><font color="red">*</font></span> 
						<input id="btnEdit1" class="btn btn-primary" type="button" value="编辑" onclick="showDetail()"/>
			<%-- (仅商户结算定时任务需要填写) --%>
				<span class="help-inline">仅商户结算定时任务需要填写</span>
			</div>
		</div> 
		<div id="center"  class="control-group" style="display:none;">
			<%-- 中心ID --%>
			<label class="control-label">中心ID：</label>
			<div class="controls">
				<form:input id="centerOfficeId" path="centerOfficeId" htmlEscape="false"   readonly="true"/>
				<span class="help-inline"><font color="red">*</font></span> 
						<input id="btnEdit2" class="btn btn-primary" type="button" value="编辑" onclick="showCenterDetail()"/>
			<%-- (仅中心结算定时任务需要填写) --%>
				<span class="help-inline">仅中心结算定时任务需要填写</span>
			</div>
		</div> 
		<div class="control-group">
			<%-- 工作状态 --%>
			<label class="control-label">工作状态：</label>
			
			<div class="controls">
				<form:select path="status" style="width: 210px;" class="required">
					<form:option value="">请选择</form:option>
					<form:option value="0">关闭</form:option>
					<form:option value="1">运行</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<%-- 执行类 --%>
			<label class="control-label"><spring:message code='clear.quartz.executionClass'/>：</label>
			<div class="controls" >
				<form:input style="width: 350px;" path="executionClass" htmlEscape="false" maxlength="90" class="required" readonly="${empty quartz.id ? false : true}"/>
				<c:if test="${empty quartz.id}">
					<span class="help-inline"><font color="red">*</font></span> 
				</c:if>
				<%-- (需实现job接口) --%>
				<span class="help-inline">需要实现job接口</span>
			</div>
		</div>
		<div class="control-group">
				<%-- 描述 --%>
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="describe" htmlEscape="false" maxlength="30" cssStyle="width: 350px"/>
			</div>
		</div>
		<div class="form-actions" style="margin-left: 70px">
			<c:choose>
				<c:when test="${empty quartz.id}">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.save'/>"/>&nbsp;
				</c:when>
				<c:otherwise>
					<input id="btnSubmitCron" class="btn btn-primary" type="button" value="<spring:message code='common.save'/>"/>&nbsp;
				</c:otherwise>
			</c:choose>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href ='${ctx}/sys/quartz/back'"/>
		</div>
	</form:form>
</body>
</html>