<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>访客统计</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
		});		
		function showView(visitorId){
			var content = "iframe:${ctx}/cloudPlateform/v04/visitorStatistics/visitorStatisticsView?visitorId=" + visitorId;
			top.$.jBox.open(
					content,
					"实时比对", 550, 400, {
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
	<ul class="nav nav-tabs">
	    <c:if test="${vistor eq '访客统计列表'}">
		<li class="active"><a href="#">访客统计列表</a></li>
		</c:if>
		<c:if test="${attend eq '员工考勤列表'}">
		<li class="active"><a href="#">员工考勤列表</a></li>
		</c:if>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="eyeCheckVisitorInfo" action="" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<div class="row">
				<div class="span14" style="margin-top:5px">
					<label>人员姓名：</label>
					<form:input path="escortName" htmlEscape="false" maxlength="20" class=""/>
					
				<%-- 	<!-- 人员类型 -->
		<label class="control-label"><spring:message code="store.userType"/>：</label>
		<form:select path="escortType" class="input-medium" style="width:100px;">
			<!-- 请选择 -->
			<form:option value="">
				<spring:message code="common.select"/>
			</form:option>
			<form:options items="${fns:getFilterDictList('eye_check_type', false, showType)}" 
				itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select> --%>
	    
					<label><spring:message code="common.startDate" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${eyeCheckVisitorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
					<label><spring:message code="common.endDate" />：</label>
						<!--  清分属性去除 wzj 2017-11-16 begin -->
						<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
							value="<fmt:formatDate value="${eyeCheckVisitorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
							onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
							&nbsp;	
					<label>所属机构：</label>
					<sys:treeselect id="office" name="office.id"
						value="${eyeCheckVisitorInfo.office.id}" labelName="office.name"
						labelValue="${eyeCheckVisitorInfo.office.name}" title="机构"
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
				<th style="width:20%">人员类型</th>
				<th style="width:20%">所属机构</th>
				<th style="padding-left:45px;width:20%">登记时间</th>			
				<th style="width:10%"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="eyeCheckVisitorInfo" varStatus="statusIndex">
			<tr>
				<td style="padding-left:15px">
					${eyeCheckVisitorInfo.escortName}
				</td>
				<c:if test="${eyeCheckVisitorInfo.escortType=='V'}">
				<td style=""><spring:message code='common.visitor'/></td>
				</c:if>
				<c:if test="${eyeCheckVisitorInfo.escortType=='E'}">
				<td style=""><spring:message code='common.escort'/></td>
				</c:if>
				<td>
					${eyeCheckVisitorInfo.office.name}
				</td>
				<td>
					<fmt:formatDate value="${eyeCheckVisitorInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<!-- 操作 -->
				<td style="padding-left:13px">					
					<!-- 查看 -->
					<a href="#" onclick="showView('${eyeCheckVisitorInfo.visitorId}')" title = "<spring:message code='common.view'/>"><i class="fa fa-eye fa-lg"></i></a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>