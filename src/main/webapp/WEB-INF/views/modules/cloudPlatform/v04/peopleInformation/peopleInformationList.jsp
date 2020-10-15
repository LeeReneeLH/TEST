<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>考勤管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
		});		
		function showView(escortId){
			var content = "iframe:${ctx}/cloudPlateform/v04/PeopleInformation/PeopleInformationView?escortId=" + escortId;
			top.$.jBox.open(
					content,
					"个人信息", 800, 445, {
						buttons : {
							// 关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
					});
		}
	</script>
</head>
<body>
<!-- Tab页 -->
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="${ctx}/cloudPlateform/v04/PeopleInformation/">
				<spring:message code="store.user.list"/>
			</a>
		</li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="eyeCheckEscortInfo" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<label>人员姓名：</label>
					<form:input path="escortName" htmlEscape="false" maxlength="20" class=""/>
		<!-- 身份证号 -->
		<label><spring:message code="common.idcardNo"/>：</label>
		<form:input path="idcardNo" htmlEscape="false" maxlength="18" onkeyup="value=value.replace(/[^0-9a-zA-Z]/g,'')" class="input-medium"/>
		<!-- 人员类型 -->
		<label class="control-label"><spring:message code="store.userType"/>：</label>
		<form:select path="escortType" class="input-medium" style="width:100px;">
			<!-- 请选择 -->
			<form:option value="">
				<spring:message code="common.select"/>
			</form:option>
			<form:options items="${fns:getFilterDictList('eye_check_type', false, showType)}" 
				itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
					<label>所属机构：</label>
					<sys:treeselect id="office" name="office.id"
						value="${eyeCheckEscortInfo.office.id}" labelName="office.name"
						labelValue="${eyeCheckEscortInfo.office.name}" title="机构"
						url="/sys/office/treeData" notAllowSelectRoot="false"
						notAllowSelectParent="false" allowClear="true" 
						cssClass="input-medium" />
							<!-- 查询 -->
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th style="width:20%">人员姓名</th>
				<!-- <th style="width:20%">证件</th> -->
				<th style="width:20%">人员类型</th>
				<th style="width:20%">所属机构</th>
				<th style="padding-left:45px;width:20%">登记时间</th>			
				<th style="width:10%"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="eyeCheckEscortInfo" varStatus="statusIndex">
			<tr>
				<td>
					${eyeCheckEscortInfo.escortName}
				</td>
				<c:if test="${eyeCheckEscortInfo.escortType=='V'}">
				<td style=""><spring:message code='common.visitor'/></td>
				</c:if>
				<c:if test="${eyeCheckEscortInfo.escortType=='E'}">
				<td style=""><spring:message code='common.escort'/></td>
				</c:if>
				<td>
					${eyeCheckEscortInfo.office.name}
				</td>
				<td>
					<fmt:formatDate value="${eyeCheckEscortInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<!-- 操作 -->
				<td>					
					<!-- 查看 -->
					<a href="#" onclick="showView('${eyeCheckEscortInfo.escortId}')" title = "<spring:message code='common.view'/>"><i class="fa fa-eye fa-lg"></i></a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>