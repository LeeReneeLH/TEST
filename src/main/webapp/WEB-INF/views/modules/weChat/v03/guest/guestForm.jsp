<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 客户 -->
	<title><spring:message code="door.guest.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate(
				{
					submitHandler : function(form) {
						loading('正在提交，请稍等...');
						form.submit();
					},
				//errorContainer : "#messageBox",
				errorPlacement : function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")|| element.is(":radio")|| element.parent().is(".input-append")) 
						{
						error.appendTo(element.parent().parent());
						} else 
						{
						error.insertAfter(element);
						}
					}
				});
			
			$("#openId").change(function(){
				selectopenId();
			});
			$("#btnSubmit").click(function(){
				/* 增加机构不能为空验证  XL 2018-05-23 begin */
				var gofficeId=$("#gofficeId").val();	
				if(gofficeId == "" ||gofficeId==""){
					alertx("机构不能为空！");
					return false;
				}
				/* end */
				$("#inputForm").submit();
			});
			
			
			changeBusType($("#busType").val(),true);

		});
	
		
		function selectopenId(){
			var openId=$("#openId").val();
			var parms = new Object();
			parms['openId'] = openId;
			$.post(ctx+'/weChat/v03/Guest/selectopenId',
				parms,
				function(data) {
					if(data[2]=="s"){	
                	//[提示]：您输入的微信标识已存在
                	alertx("<spring:message code='message.I7204' />");
					 $("#openId").val('');
					return false;
					}
						return true;
					}
				
			);
			return true;
		}
		
		//根据选择的业务类型，获取机构列表
		function changeBusType(busType,isInit){
			var gofficeIdTemp=$('#gofficeId').val();
			if(busType===''){
				$("#guestTypeDiv").show();
			}
			//预约清分
			if(busType==='73'){
				busType='3'				
				$("#guestType").val("1");
				$("#guestType").trigger("change");
				$("#guestTypeDiv").hide();
			}
			//门店预约
			if(busType==='74'){
				busType='8'
				$("#guestTypeDiv").show();
			}
			var url = '${ctx}/weChat/v03/Guest/changeBusType';
			//清空机构列表
			$('#gofficeId').select2("val",'');
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(busType)
				},
				success : function(serverResult, textStatus) {
					$('#gofficeId').select2({
						containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
					});
					if(serverResult.length>0){
						$('#gofficeId').select2("val",serverResult[0].id);
						if(gofficeIdTemp!=''&& isInit){
							$("#gofficeId").select2("val",gofficeIdTemp);
						}
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) 
		{
			//alert(item.label);
			return item.label;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	
		<!-- 客户列表 -->
		<li><a href="${ctx}/weChat/v03/Guest/"><spring:message code="door.guest.title" /><spring:message code="common.list" /></a></li>
		<!-- 客户登记(修改) -->
		<li class="active">
			<!-- gzd 2020-04-14  修改链接  ${ctx}/weChat/v03/Guest/form?id=${Guest.id}  -->
			<a>
				<shiro:hasPermission name="guest:Guest:edit">
					<c:choose>
						<c:when test="${not empty Guest.id}">
							<spring:message code="door.guest.title" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="door.guest.title" /><spring:message code="common.add" /> 
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="Guest" action="${ctx}/weChat/v03/Guest/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<!-- 用户名 -->
			<label class="control-label"><spring:message code="door.guest.userName" />：</label>
			<div class="controls">
			<c:if test="${!(Guest.method eq '2')}">		
				<form:input  path="gname" htmlEscape="false" maxlength="10" class="input-xlarge realName required" />
			 </c:if>
			 	<c:if test="${Guest.method eq '2'}">	
				<form:input style="background-color:#D5D5D5" readonly="true"  path="gname" htmlEscape="false" maxlength="10" class="input-xlarge "/>
			 </c:if>
			 <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<!-- 手机号 -->
			<label class="control-label"><spring:message code="door.guest.tel" />：</label>
			<div class="controls">
				<c:if test="${!(Guest.method eq '2')}">	
				<form:input id="gphone"  path="gphone" htmlEscape="false" maxlength="11" class="input-xlarge mobile required"  onkeyup="value=value.replace(/[^0-9]/g,'')"
/>
			 </c:if>																						
			 	<c:if test="${Guest.method eq '2'}">	
				<form:input style="background-color:#D5D5D5" id="gphone" readonly="true"  path="gphone" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			 </c:if>
			 <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<!-- 身份证号 -->
			<label class="control-label"><spring:message code="door.guest.cardNo" />：</label>
			<div class="controls">
			<c:if test="${!(Guest.method eq '2')}">	
				<form:input id="gidcardNo"  path="gidcardNo" htmlEscape="false" maxlength="18" class="input-xlarge card required" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')"/>
			 </c:if>
			 	<c:if test="${Guest.method eq '2'}">	
				<form:input style="background-color:#D5D5D5" id="gidcardNo" readonly="true"  path="gidcardNo" htmlEscape="false" maxlength="18" class="input-xlarge "/>
			 </c:if>
			 <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<c:if test="${!(Guest.method eq '2')}">	
				<!-- 微信标识 -->
				<label class="control-label" ><spring:message code="door.guest.weChatFlag" />：</label>
				<div class="controls">
				<form:input id="openId"  path="openId" htmlEscape="false" maxlength="30" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
				</div>
			 </c:if>
			 <c:if test="${Guest.method eq '2'}">
			 	<div class="controls">	
				<form:hidden id="openId"  path="openId" htmlEscape="false" maxlength="30" class="input-xlarge "/>
				</div>
			 </c:if>
		</div>
	
		
	<div class="control-group">
		<!-- 授权期限 -->
		<label class="control-label"><spring:message code="door.guest.authTerm" />：</label>
		<div class="controls">
			<input  id="grantDate" name="grantDate" type="text" readonly="readonly" maxlength="20" class="input-small required" 
				       value="<fmt:formatDate value="${Guest.grantDate}" pattern="yyyy-MM-dd"/>" 
				     onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false,isShowToday:false,minDate:'#F{$dp.$D(\'sysDate\')||\'%y-%M-%d\'}'});"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<!-- 业务类型 -->
		<label class="control-label"><spring:message code="clear.task.business.type" />：</label>
		<div class="controls">  
            <c:if test="${!(Guest.method eq '2')}">
            <form:select path="busType" class="input-large required" onchange="changeBusType(this.options[this.selectedIndex].value,false)">
               	<option value=""><spring:message code="common.select" /></option>
				<form:options items="${fns:getFilterDictList('clear_businesstype',true,'73,74')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
			</c:if>
			<c:if test="${Guest.method eq '2'}">
				<form:input style="background-color:#D5D5D5" readonly="true"  path="" htmlEscape="false" maxlength="20" class="input-xlarge " value="${fns:getDictLabel(Guest.busType,'clear_businesstype', '')}"/>
				<form:hidden readonly="true"  path="busType" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</c:if>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
		
	<div class="control-group" id="guestTypeDiv">
		<!-- 用户类型 -->
		<label class="control-label"><spring:message code="door.guest.userType" />：</label>
		<div class="controls">  
            <form:select path="guestType" class="input-large required" id="guestType" >
            	<option value="" ><spring:message code="common.select" /></option>
				<form:options items="${fns:getDictList('guest_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
		
	<div class="control-group">
		<!-- 机构 -->
		<label class="control-label"><spring:message code="common.office" />：</label>
		<div class="controls">
		<c:if test="${!(Guest.method eq '2')}">
			<form:input type="hidden" path="gofficeId" style="width:210px;"/>	
				<%-- <sys:treeselect id="goffice" name="gofficeId"
						value="${Guest.gofficeId}" labelName="gofficeName"
						labelValue="${Guest.gofficeName}" title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData" cssClass="required"
						notAllowSelectParent="false" notAllowSelectRoot="false" type="8"
					    isAll="true"  allowClear="true"/> --%>
		</c:if>
		
		<c:if test="${(Guest.method eq '2')}">	
			<form:input style="background-color:#D5D5D5" readonly="true"  path="gofficeName" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			<div style="display:none"><form:hidden path="gofficeId" htmlEscape="false" maxlength="20" class="input-xlarge "/></div>
		</c:if>
		<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
		
		<div class="form-actions">
			<!-- 保 存 -->
			<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.save' />"/>	&nbsp;
			<!-- 返 回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return' />" 
			onclick = "history.go(-1)"/>
			<%-- onclick = "window.location.href ='${ctx}/weChat/v03/Guest/back'"/> --%>
		</div>
		
			<div class="control-group">
		
			<div class="controls">
				<input  id="loginDate" name="loginDate" type="hidden" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${sysdate}" pattern="yyyy-MM-dd"/>"
					     onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				
			</div>
			<div class="controls">
				<input  id="sysDate" name="sysDate" type="hidden" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${systomorrow}" pattern="yyyy-MM-dd"/>"
					     onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				
			</div>
		</div>
	</form:form>
</body>
</html>