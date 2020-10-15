<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 显示归属机构
			isShowOfficeDiv();
			// 独自处理人员信息，屏蔽此方法
			disabledUserUpdate();
		});
		
		// 用户管理人员信息，屏蔽部分修改功能
		function disabledUserUpdate() {
			var id = $("#id").val()
			var escortType = $("#escortType").val()
			if(null != id && '' != id) {
				$("form input").prop("readonly", true);
				$("#escortType").attr('readonly',true);
				$("#officeButton").addClass('disabled');
			} else {
				var escortNo = "${fns:getConfig('escort.type.escort')}";
				$("#escortType").val(escortNo);
				$("#escortType").attr('readonly',true);
			}
		}
		
		// 显示归属机构
		function isShowOfficeDiv(){
			var escortType = $("#escortType").val();
			var escortNo = "${fns:getConfig('escort.type.escort')}";
			if(escortType == escortNo){
				$("#officeDiv").show();
				$("#handlePassword").hide();
			}else{
				$("#officeDiv").show();
				$("#escortPassword").hide();
			}
	 	}
	</script>
</head>
<body>
	<!-- Tab页 -->
	<ul class="nav nav-tabs">
		<li>
			<a href="${ctx}/store/v01/stoEscortInfo/">
				<spring:message code="store.user.list"/>
			</a>
		</li>
		<li class="active">
		<a href=" " onclick="javascript:return false;"><spring:message code="store.userInfo" /><spring:message code="common.view" /></a>
					
		</li>
		<%-- <li>
			<a href="${ctx}/store/v01/stoEscortInfo/createUser">
				<spring:message code="store.addUser"/>
			</a>
		</li> --%>
	</ul><br/>
	<!-- 输入项 -->
	<form:form id="inputForm" modelAttribute="stoEscortInfo" 
		action="${ctx}/store/v01/stoEscortInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>

		<sys:message content="${message}"/>
		<!-- 姓名 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.name"/>：</label>
			<div class="controls">
				<form:input path="escortName" htmlEscape="false" 
					maxlength="6" class="required" />
				
			</div>
		</div>
		<!-- 人员类型（默认为[11：押运人员]） -->
		<%-- <form:hidden path="escortType" value="11"/> --%>
		<!-- 人员类型 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="store.userType"/>：</label>
			<div class="controls">
				<%-- <c:set var="showType" value="${fns:getConfig('escort.type.show')} " /> --%>
		<%-- 	<form:input path="escortType" value="${fns:getConfig('escort.type.escort.name')} " htmlEscape="false"/>
			<form:input path="escortType" value="${fns:getFilterDictList('sys_user_type', false, showType)} " htmlEscape="false"/>	 --%>
			<form:select path="escortType" class="input-large">
					<!-- 请选择 -->
					<form:option value="">
						<spring:message code="common.select"/>
					</form:option>
					<!-- 解款员 -->
					<form:option value="${fns:getConfig('escort.type.escort')}">
						<spring:message code="store.escortUser"/>
					</form:option>
					<form:options items="${fns:getFilterDictList('sys_user_type', false, showType)}" 
						itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>  
			    
		    </div>
	    </div>
		<!-- 身份证 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls">
			    <input type="hidden" id="oldIdcardNo" value="${stoEscortInfo.idcardNo}"/>
				<form:input path="idcardNo" htmlEscape="false" 
					maxlength="18" class="required card"/>
				
			</div>
		</div>
		
		<!-- 电话 -->
	
		<div class="control-group">
			<label class="control-label"><spring:message code="common.phone"/>：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" 
					maxlength="11" class="required mobile"/>
			
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">脸部识别ID：</label>
			<div class="controls">
				<%-- <input id="oldFaceId" name="oldFaceId" type="hidden" value="${user.userFaceId}"> --%>
				<form:input path="userFaceId" htmlEscape="false" maxlength="8" readOnly="readOnly" />
				
			</div>
		</div>
		<!-- 机构 -->
		<div class="control-group" id="officeDiv">
			<label class="control-label"><spring:message code="common.office"/>：</label>
			<div class="controls">
				<form:input path="office.id" name="office.id" value="${stoEscortInfo.office.name}" htmlEscape="false" maxlength="8" readOnly="readOnly" />
			
			</div>
		</div>
		<!-- 押运人员密码 -->
		<%-- <shiro:hasPermission name="store:stoEscortInfo:edit">
		<div class="control-group" id="escortPassword">
			<label class="control-label"><spring:message code="store.password"/>：</label>
			<div class="controls">
				${stoEscortInfo.password}
			</div>
		</div> --%>
		<!-- 网点人员设置交接密码 -->
		<!-- <div id="handlePassword"> -->
		<!-- 查看不显示密码项 -->
			<%-- <div class="control-group">
				<label class="control-label"><spring:message code="store.password"/>：</label>
				<div class="controls">
					<form:input path="password" htmlEscape="false"  minlength="6"
						maxlength="16" class="required number"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		</shiro:hasPermission> --%>
		<!-- 网点人员设置图片上传 -->
		<shiro:hasPermission name="store:stoEscortInfo:useImage">
			<div class="control-group">
				<label class="control-label"><spring:message code="store.image"/>：</label>
					<div class="controls">
						<img src="${ctx}/store/v01/stoEscortInfo/showImage?id=${stoEscortInfo.id}"
				 		height="30%" width="30%"/>
					</div>
				</div>
		</shiro:hasPermission>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" 
				value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/store/v01/stoEscortInfo/back'"/>
		</div>
		<!-- end -->
	</form:form>
</body>
</html>
