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
			
			$("#escortName").focus();
			$("#inputForm").validate({
				rules: {
					idcardNo: {remote: {url:"${ctx}/store/v01/stoEscortInfo/checkIdcardNo",data:{id:function(){return $("#id").val();},oldIdcardNo:function(){return $("#oldIdcardNo").val();},cache:false}}}
				},
				messages: {
					idcardNo: {remote: "<spring:message code='message.E1022'/>"}
				},
				submitHandler: function(form){
					var filePath = $.trim($("#imgFile").val());
					// 效验文件不为空
					if(filePath.length > 0){
						// 效验文件类型
						var fileFormate = "${fns:getConfig('image.setting.import.format')}";
						var fileArr=filePath.split("\\");
						var fileTArr=fileArr[fileArr.length-1].toLowerCase().split(".");
						var filetype=fileTArr[fileTArr.length-1];
						if(fileFormate.indexOf(filetype) == -1){ 
							$("#messageBox").html("<spring:message code='message.E1042'/>").show(); //检测允许的上传文件类型
							return; 
						}
					}
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
			var bindingRoute = "${stoEscortInfo.bindingRoute}";
			if(bindingRoute=='1') {
				$("#escortType").attr("readonly","readonly");
			}
			
			$("#escortType").change(function(){
				isShowOfficeDiv();
				$("#officeName").val('');
			});
			//如果不是押运人员，移除必填项样式
			var escortType = '${stoEscortInfo.escortType}';
			if(escortType != '90'){
				$("input").removeClass("required");
			}
			// 保存后上传图片
			$("#btnUploadImage").click(function() {
				
				$("#uploadFlag").val("yes");
				$("#inputForm").submit();
				
			});
			// 保存,跳过上传图片
			$("#btnExceptImage").click(function() {
				
				$("#uploadFlag").val("no");
				$("#inputForm").submit();
				
			});
			
			// 独自处理人员信息，屏蔽此方法
			disabledUserUpdate();
		});
		
		// 用户管理人员信息，屏蔽部分修改功能
		function disabledUserUpdate() {
			var id = $("#id").val()
			var escortType = $("#escortType").val()
			if(null != id && '' != id) {
				$("form input").prop("readonly", true);
				$(".help-inline").remove();
				if(escortType == '90'){
					$("#phone").prop("readonly",false);
					$("#phone").after('<span class="help-inline"><font color="red">*</font> </span>');
				}
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
				$("#officeDiv").hide();
				$("#escortPassword").show();
				$("#handlePassword").hide();
			}else{
				$("#officeDiv").show();
				$("#escortPassword").hide();
				// 网点人员
				var userType =  "${fns:getConfig('escort_type_setting_password_phone')}";
				if(userType.indexOf(escortType) != -1) {
					$("#handlePassword").show();
				}else {
					$("#handlePassword").hide();
				}
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
			<a href="${ctx}/store/v01/stoEscortInfo/form?id=${stoEscortInfo.id}">
				<shiro:hasPermission name="store:stoEscortInfo:edit">
					<c:choose>
						<c:when test="${not empty stoEscortInfo.id}">
							<spring:message code="store.userInfo" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code="store.userInfo" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
				<shiro:lacksPermission name="store:stoEscortInfo:edit">
					<spring:message code="store.userInfo" /><spring:message code="common.view" />
				</shiro:lacksPermission>
			</a>
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
					maxlength="10" class="required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 人员类型（默认为[11：押运人员]） -->
		<%-- <form:hidden path="escortType" value="11"/> --%>
		<!-- 人员类型 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="store.userType"/>：</label>
			<div class="controls">
				<c:set var="showType" value="${fns:getConfig('escort.type.show')} " />
				<form:select path="escortType" class="input-large">
					<!-- 请选择 -->
					<%-- <form:option value="">
						<spring:message code="common.select"/>
					</form:option> --%>
					<!-- 解款员 -->
					<form:option value="${fns:getConfig('escort.type.escort')}">
						<spring:message code="store.escortUser"/>
					</form:option>
					<form:options items="${fns:getFilterDictList('sys_user_type', false, showType)}" 
						itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>
			     <%-- <span class="help-inline">
			     <font color="red">*
			     	<!-- 绑定人员不能删除 -->
    				<c:if test="${stoEscortInfo.bindingRoute=='1'}">
    					绑定线路不可修改
    				</c:if>
			     </font> </span> --%>
		    </div>
	    </div>
		<!-- 身份证 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls">
			    <input type="hidden" id="oldIdcardNo" value="${stoEscortInfo.idcardNo}"/>
				<form:input path="idcardNo" htmlEscape="false" 
					maxlength="18" class="required card"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<!-- 电话 -->
	
		<div class="control-group">
			<label class="control-label"><spring:message code="common.phone"/>：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" 
					maxlength="11" class="required mobile"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">脸部识别ID：</label>
			<div class="controls">
				<%-- <input id="oldFaceId" name="oldFaceId" type="hidden" value="${user.userFaceId}"> --%>
				<form:input path="userFaceId" htmlEscape="false" maxlength="8" readOnly="readOnly" />
				<c:if test="${stoEscortInfo.escortType == '90' }">
					<%-- 是否生成 --%>
			
					<form:checkbox path="initFaceIdFlag" value="1"/>&nbsp;&nbsp;<spring:message code="common.isMake" />
				
				</c:if>
			</div>
		</div>
		<!-- 机构 -->
		<div class="control-group" id="officeDiv">
			<label class="control-label"><spring:message code="common.office"/>：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${stoEscortInfo.office.id}" labelName="office.name" labelValue="${stoEscortInfo.office.name}"
					title="机构" url="/sys/office/treeData" cssClass="required" allowClear="true" notAllowSelectParent="false" notAllowSelectRoot="false"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 押运人员密码 -->
		
		<%-- <shiro:hasPermission name="store:stoEscortInfo:edit"> --%>
		<%-- <div class="control-group" id="escortPassword">
			<label class="control-label" ><spring:message code="store.password"/>：</label>
			<div class="controls">
				${stoEscortInfo.password}
			</div>
		</div> --%>
		
		<!-- 网点人员设置交接密码和图片上传 -->
		<%-- <div id="handlePassword">
		<!-- 查看不显示密码项 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="store.password"/>：</label>
				<div class="controls">
					<form:input path="password" htmlEscape="false"  minlength="6"
						maxlength="16" class="required number"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		</shiro:hasPermission> --%>
		
		<shiro:hasPermission name="store:stoEscortInfo:useImage">
			<c:if test="${not empty(stoEscortInfo.id)}">
				<div class="control-group">
					<label class="control-label"><spring:message code="store.image"/>：</label>
						<div class="controls">
							<img src="${ctx}/store/v01/stoEscortInfo/showImage?id=${stoEscortInfo.id}" 
					 		height="30%" width="30%"/>
						</div>
				</div>
			</c:if>
		</shiro:hasPermission>
		
		<!-- </div> -->
		<!-- 添加过滤，非押运人员不可更改 修改人xp 修改时间：2017-8-1 begin-->
		<%-- <c:if test="${stoEscortInfo.escortType eq '90' or stoEscortInfo.escortType == null}"> --%>
		<!-- 按钮 -->
		<div>
			<div class="form-actions">
				<shiro:hasPermission name="store:stoEscortInfo:edit">
					<shiro:hasPermission name="store:stoEscortInfo:useImage">
						<form:hidden path="uploadFlag" value="yes"/>
						<input id="btnUploadImage" type="button" class="btn btn-primary" value="<spring:message code='common.uploadPicAfterSave'/>" />&nbsp;
						<input id="btnExceptImage" class="btn btn-primary" type="button" value="<spring:message code='common.save'/>" />&nbsp;
					</shiro:hasPermission>
					<shiro:lacksPermission name="store:stoEscortInfo:useImage">
						<input id="btnSubmit" class="btn btn-primary" type="submit" 
							value="<spring:message code='common.commit'/>"/>&nbsp;
					</shiro:lacksPermission>
				</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" 
					value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/store/v01/stoEscortInfo/back'"/>
			</div>
		</div>
		<%-- </c:if> --%>
		<!-- end -->
	</form:form>
</body>
</html>
