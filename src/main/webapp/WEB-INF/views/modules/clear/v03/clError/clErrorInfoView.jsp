<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 差错管理 -->
	<title><spring:message code="clear.clErrorInfo.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 差错处理列表 -->
		<li><a href="${ctx}/clear/v03/clErrorInfo/"><spring:message code="clear.clErrorInfo.list" /></a></li>
		<!-- 差错处理查看 -->
		<li class="active"><a href="${ctx}/clear/v03/clErrorInfo/view?errorNo=${clErrorInfo.errorNo}"><spring:message code="clear.clErrorInfo.view" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clErrorInfo" action="${ctx}/clear/v03/clErrorInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!--第一列 -->
		<div style="float:left;">
			<!-- 客户名称 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.public.custName" />：</label>
					
					<div class="controls" >
					<form:input path="" value="${clErrorInfo.custName}" readOnly="true" htmlEscape="false" class="required"/>
						<span class="help-inline" ><font color="red">*</font> </span>
					</div>
			</div>
			<!-- 类别-->
			<div class="control-group">
				<label class="control-label"><spring:message code='common.classification'/>：</label>
				<div class="controls">
					<form:input path="errorType" value="${fns:getDictLabel(clErrorInfo.errorType,'clear_error_type','')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 面值-->
			<div class="control-group">
				<label class="control-label"><spring:message code='common.denomination'/>：</label>
				<div class="controls">
					<form:input path="denomination" value="${sto:getGoodDictLabel(clErrorInfo.denomination, 'cnypden', '')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 数量 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.number"/>：</label>
				<div class="controls">
					<form:input path="count" readOnly="true" id="count" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--差错类别 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.errorType"/>：</label>
				<div class="controls">
					<form:input path="subErrorType" value="${fns:getDictLabel(clErrorInfo.subErrorType,'clear_subError_type','')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 差错金额 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.amountError"/>：</label>
				<div class="controls">
					<input id="errorMoney" name="errorMoney"  type="text" value="<fmt:formatNumber value="${clErrorInfo.errorMoney}" pattern="#,##0.00#"/>"  disabled/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<!--第二列 -->
		<div style="float:left;=">
			<!--版本 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.edition"/>：</label>
				<div class="controls">
					<form:input path="versionError" readOnly="true" htmlEscape="false"/>
				</div>
			</div>
			<!--冠字号 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.crownSize"/>：</label>
				<div class="controls">
					<form:input path="sno" readOnly="true" htmlEscape="false" class=""/>
				</div>
			</div>
			<!--封签单位 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.sealUnit"/>：</label>
				<div class="controls">
					<form:input path="seelOrg" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 腰条名章 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.articleWaist"/>：</label>
				<div class="controls">
					<form:input path="stripChap" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--封签名章 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.chapterSeal"/>：</label>
				<div class="controls">
					<form:input path="seelChap" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 业务类型 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.business.type"/>：</label>
				<div class="controls">	
					<form:input path="busType" value="${fns:getDictLabel(clErrorInfo.busType,'clear_businesstype','')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<!--第三列 -->
		<div style="float:left;">
			<!--清分人员 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.clearMan"/>：</label>
				<div class="controls">
					<form:input path="clearManName" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--差错管理员 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.errorAdmin"/>：</label>
				<div class="controls">
					<form:input path="checkManName"  readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--发现时间 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.findDate"/>：</label>
				<div class="controls">
					<input name="findTime" type="text" readonly="readonly" maxlength="20" class="required"
						value="<fmt:formatDate value="${clErrorInfo.findTime}" pattern="yyyy-MM-dd HH:mm"/>"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--笔数 -->
			<%-- <div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.theNumber"/>：</label>
				<div class="controls">
					<form:input path="strokeCount" readOnly="true" htmlEscape="false" class="digits required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div> --%>
			<!--工位编号 -->
			<%-- <div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.locationNumber"/>：</label>
				<div class="controls">
					<form:input path="stationNo" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div> --%>
			<!--封签日期 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.sealTheDate"/>：</label>
				<div class="controls">
					<input id="seelDate"  name="seelDate" type="text" readonly="readonly" maxlength="20" class="required" 
						value="<fmt:formatDate value="${clErrorInfo.seelDate}" pattern="yyyy-MM-dd"/>" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<!-- 备注 -->
		<div style="float:left; clear:both;">
			<div class="control-group">
				<label class="control-label"><spring:message code="common.remark"/>：</label>
				<div class="controls">
					<form:textarea path="remarks" readOnly="true" style="width:950px;"  htmlEscape="false" rows="4" class="input-medium "/>
				</div>
			</div>
		</div>
		<!--返回 -->
		<br style=" clear:both;"/>
		<!-- <div style="float:left; clear:both;width:100%;"> -->
			<div class="form-actions" style="width:100%;">
				<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/clear/v03/clErrorInfo/back'"/>
			</div>
	<!-- 	</div> -->
	</form:form>
</body>
</html>