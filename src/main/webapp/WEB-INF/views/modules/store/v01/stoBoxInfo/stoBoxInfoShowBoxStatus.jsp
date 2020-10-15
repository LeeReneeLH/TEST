<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
<div class="row" style="margin-left:2px;">
		
			<form:form id="inputForm" modelAttribute="stoBoxInfoHistory" action ="" method="post">
				<div class="form-actions">
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 2px">箱袋编号：</label>
					<br/>
			 		<%-- <form:input type="text" value="${id}" disabled="disabled" name = "id">   --%>
			 		<div class="controls">
					 <form:input path="StoBoxInfo.id" htmlEscape="false" maxlength="15" class="digits" readonly="true" id="id"/>
					 </div>
					 <br/>
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 2px">请选择箱袋状态：</label>
					<br/>
					<div class="controls">
						<form:select path="StoBoxInfo.boxStatus" class="input-large required" id = "boxStatus">
							<!-- 追加判断是否是钞箱 修改人：sg 修改时间：2017-11-02 begin -->
							<!-- 获取钞箱的箱袋状态 -->
							<c:set var="showAtmbox" value="${fns:getConfig('sto.box.boxstatus.atmboxs')} " />
							<!-- 如果不是钞箱显示正常的状态 -->
							<c:if test="${fn:indexOf(fns:getConfig('sto.box.boxtype.atmShow'), stoBoxInfoHistory.stoBoxInfo.boxType)==-1}">
								<form:options items="${fns:getFilterDictList('sto_box_status', true, '10,11,12,13')}" itemLabel="label" itemValue="value" htmlEscape="false" />
							</c:if>
							<!-- 如果是钞箱显示钞箱的状态 -->
							<c:if test="${fn:indexOf(fns:getConfig('sto.box.boxtype.atmShow'), stoBoxInfoHistory.stoBoxInfo.boxType)!=-1}">
								<!--钞箱 -->
							<form:options items="${fns:getFilterDictList('sto_box_status',true,showAtmbox)}" itemLabel="label" itemValue="value" htmlEscape="false" />
							</c:if>
							<!-- end -->
						</form:select>
					</div>
					<br/>
					<label class="control-label" style="color:blue; margin-left: 2px">请登录进行授权：</label>
					<br/>
					<br>
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 2px">用户名：</label>
					<br/>
					<div class="controls">
					<form:input path="authorizer" htmlEscape="false" maxlength="15" class="required" />
					</div>
					<br/>
					<label class="control-label" style="color:rgb(51,51,51); margin-left: 2px">密码：</label>
					<br/>
					<div class="controls">
					<form:password path="authorizeBy" htmlEscape="false" maxlength="15" class="required" />
					</div>
					<!-- <div style="margin-left: 50px">
						<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存" />&nbsp;
						<input id="btnCancel" class="btn" type="button" value="返回" onclick="history.go(-1)"/>
					</div> -->
				</div>
			</form:form>	
		
</div>
</body>
</html>