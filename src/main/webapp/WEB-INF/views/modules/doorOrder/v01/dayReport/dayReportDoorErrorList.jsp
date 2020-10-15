<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户日结管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
		$(document).ready(function() {
			//导出
			$("#exportSubmit").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				//dayReportIds=dayReportIds.substr(1);
				//$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/exportErrorList?dayReportIds="+dayReportIds);
				$("#dayReportIds").val(dayReportIds.substr(1));
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/exportErrorList");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/errorList");		
			});
			
			//差错处理
			$("#handleSubmit").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				if(dayReportIds==''){
					alertx("请选择要处理的记录！");
					return;
				}
				//dayReportIds=dayReportIds.substr(1);
				top.$.jBox.confirm('是否确认处理？','系统提示',function(v,h,f){
					if(v=='ok'){
						$("#dayReportIds").val(dayReportIds.substr(1));
						//$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/handleByIds?dayReportIds="+dayReportIds);
						$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/handleByIds");
						$("#searchForm").submit();
						loading("请勿刷新页面！正在进行差错处理操作，请稍等...");
					}
				},{buttonsFocus:1, closed:function(){
					if (typeof closed == 'function') {
						closed();
					}
				}});
				top.$('.jbox-body .jbox-icon').css('top','55px');
				
			});
			
			//全选
			$("#chooseAll").on('click',function(){
				if($(this).prop('checked')){
					$('[name="checkdayReportIds"]').each(function () {
						$(this).prop("checked",true);
					});
				}else{
					$('[name="checkdayReportIds"]').each(function () {
						$(this).prop("checked",false);
					});
				}
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
		
		function confirmy(mess, href, closed){
			top.$.jBox.confirm(mess,'系统提示',function(v,h,f){
				if(v=='ok'){
					if (typeof href == 'function') {
						href();
					}else{
						resetTip(); //loading();
						$("#pay").attr("href",'#');
						loading("请勿刷新页面！正在进行差错信息结算，请稍等...");
						location = href;
					}
				}
			},{buttonsFocus:1, closed:function(){
				if (typeof closed == 'function') {
					closed();
				}
			}});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			return false;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">   
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorMerchan:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/dayReportDoorMerchan/"><spring:message code='door.accountManage.merchanReportList'/></a></li></shiro:hasPermission>
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorError:view"><li class="active"><a><spring:message code='door.accountManage.errorReportList'/></a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dayReportDoorMerchan" action="${ctx}/doorOrder/v01/dayReportDoorMerchan/errorList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="dayReportIds" name="dayReportIds" type="hidden"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<div>
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportDoorMerchan.createTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		</div>
		<div>
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportDoorMerchan.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>&nbsp;
		</div>
		<div>
		<%-- 门店 --%>
		<label><spring:message code="door.public.cust" /> ：</label>
		<sys:treeselect id="officeName" name="officeId"
			value="${dayReportDoorMerchan.officeId}" labelName="officeName"
			labelValue="${dayReportDoorMerchan.officeName}" title="<spring:message code='door.public.cust' />"
			url="/sys/office/treeData" cssClass="required input-small"
			notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
			isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
		</div>
		<div>
		<!-- 处理状态 -->
		<label><spring:message code="door.accountManage.handleStatus"/>：</label>
		<form:select path="paidStatus" class="input-small required">
		<form:option value=""><spring:message code="common.select" /></form:option>				
		<form:options items="${fns:getFilterDictList('error_is_take_up',true,'1,0')}" 
			itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		<div>
		<!-- 差错类型 -->
		<label><spring:message code="door.errorInfo.type"/>：</label>
		<form:select path="errorType" class="input-small required">
		<form:option value=""><spring:message code="common.select" /></form:option>				
		<form:options items="${fns:getFilterDictList('clear_error_type',false,'1')}" 
			itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		&nbsp;&nbsp;
		<div>
		<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="查询"/>
		</div>
		&nbsp;&nbsp;
		<!-- 导出 -->
		<div>
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
		</div>
		&nbsp;&nbsp;
		<!-- 处理 -->
		<div>
		<c:if test="${fns:getUser().userType=='21'&&fns:getUser().getOffice().type=='6'}">
			<input id="handleSubmit" class="btn btn-red" type="button" value="处理" />
		</c:if>
		</div>
		&nbsp;&nbsp;
		<!-- 存款差错手动结算(只有清分中心管理员可见) -->
		<div id="reportDiv">
		<c:if test="${fns:getUser().userType=='21'&&fns:getUser().getOffice().type=='6'}">
		<a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/saveErrorReport?" onclick="return confirmy('<spring:message code="message.A1003"/>', this.href)"><input id="report" class="btn btn-primary" type="button" value="<spring:message code='door.public.error'/><spring:message code='door.accountManage.report'/>"/></a>
		</c:if>  
		</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><input type="checkbox" name="chooseAll" value="" id="chooseAll"/></th>
			    <!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column o2.NAME"><spring:message code='door.accountManage.doorName'/></th>
		        <!-- 差错总额 -->
		        <th class="sort-column a.total_amount"><spring:message code="door.accountManage.errorMoneyTotal"/></th>
		        <!-- 差错类型 -->
		        <th ><spring:message code="door.errorInfo.type"/></th>
		        <!-- 结算类型 -->
		        <%-- <th><spring:message code="door.accountManage.reportType"/></th> --%>
				<!-- 结算时间 -->
				<th class="sort-column a.report_date"><spring:message code="door.accountManage.reportDate"/></th>
				<!-- 结算人 -->
				<th class="sort-column a.rname"><spring:message code="door.accountManage.reportName"/></th>
				<!-- 结算机构 -->
				<th class="sort-column o3.name"><spring:message code="door.accountManage.reportOffice"/></th>
				<!-- 处理状态 -->
				<th class="sort-column a.paid_status"><spring:message code='door.accountManage.handleStatus'/></th>
				<!-- 实际处理金额 -->
				<th class="sort-column a.paid_amount"><spring:message code="door.accountManage.handleAmount"/></th>
				<!-- 处理时间 -->
				<th class="sort-column a.paid_date"><spring:message code="door.accountManage.handleTime"/></th>
				<!-- 操作 -->
				<th colspan="2" style="width:70px;"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dayReportDoorMerchan" varStatus="status">
			<tr>
				<td><input type="checkbox" name="checkdayReportIds" value="${dayReportDoorMerchan.id}"/></td>
				<td>${status.index+1}</td>
				<td>${dayReportDoorMerchan.officeName}</td>
				<td><fmt:formatNumber value="${dayReportDoorMerchan.totalAmount > 0 ? dayReportDoorMerchan.totalAmount : (-dayReportDoorMerchan.totalAmount)}" pattern="#,##0.00#" /></td>
				<td>${dayReportDoorMerchan.errorType}</td>
				<%-- <td>${fns:getDictLabel(dayReportDoorMerchan.settlementType,'report_type',"")}</td> --%>
				<td><fmt:formatDate value="${dayReportDoorMerchan.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<!-- 结算人 -->
				<td>${dayReportDoorMerchan.rname}</td>
				<!-- 结算机构 -->
				<td>${dayReportDoorMerchan.settleOfficeName}</td>
				<c:if test="${dayReportDoorMerchan.paidStatus eq '1'}">
				<td style="color: red">${fns:getDictLabel(dayReportDoorMerchan.paidStatus,'error_is_take_up',"")}</td>
				</c:if>
				<c:if test="${dayReportDoorMerchan.paidStatus eq '0'}">
				<td style="color: green">${fns:getDictLabel(dayReportDoorMerchan.paidStatus,'error_is_take_up',"")}</td>
				</c:if>
				<td><fmt:formatNumber value="${dayReportDoorMerchan.paidAmount}" pattern="#,##0.00#" /></td>
				<td><fmt:formatDate value="${dayReportDoorMerchan.paidDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td style="width:70px;">
				<a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/detailView?reportId=${dayReportDoorMerchan.reportId}&clientId=${dayReportDoorMerchan.officeId}&settlementType=${dayReportDoorMerchan.settlementType}" title = "<spring:message code='common.view'/>">
					<i class="fa fa-eye fa-lg"></i>
				</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>