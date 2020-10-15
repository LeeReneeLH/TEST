<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='clear.task.distribution.list'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//点击导出按钮
			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/empWorkStatistics/exportEmpWorkInfoReport");
				$("#searchForm").submit();
			});
			
			//点击查询按钮
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/empWorkStatistics/");
				$("#searchForm").submit();
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		// 打印明细
		function printDetail(taskNo){
			alertx("暂不处理");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 员工工作量列表 -->
		<li class="active"><a href="${ctx}/clear/v03/empWorkStatistics/"><spring:message code="clear.empWorkStatistics.list" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="empWorkStatistics"
		action="${ctx}/clear/v03/empWorkStatistics" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<!-- 员工名称 -->
		<label class="control-label"><spring:message code="clear.empWorkStatistics.empName" />：</label>
		<!--  清分属性去除 wzj 2017-11-22 begin -->
		<form:select path="empNo" id="empNo" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>				
			<form:options items="${empNameList}" 
					itemLabel="name" itemValue="id" htmlEscape="false" />
		</form:select>
		<!-- end -->
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-16 begin -->
		<input id="operateTimeStart"  name="operateTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${empWorkStatistics.operateTimeStart}" pattern="yyyy-MM-dd"/>" 
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'operateTimeEnd\')||\'%y-%M-%d\'}'});" />
		<!-- end -->
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清空属性去除 wzj 2017-11-16 begin -->
		<input id="operateTimeEnd" name="operateTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${empWorkStatistics.operateTimeEnd}" pattern="yyyy-MM-dd"/>"
			onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'operateTimeStart\')}',maxDate:'%y-%M-%d'});" />&nbsp;
		<!-- end -->
		<!-- 增加清分机构 wzj 2017-11-27 begin -->
		<label><spring:message code="common.agent.office" />：</label>
			<sys:treeselect id="officeId" name="officeId"
				value="${empWorkStatistics.officeId}" labelName="officeName"
				labelValue="${empWorkStatistics.officeName}" title="清分中心"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true" type="6"
				cssClass="input-medium" />
		<!-- end -->
		<!-- 查询 -->
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>" />
		<!--  增加导出 wzj 2017-11-16 begin -->
		<!-- 导出excel -->
		<input id="btnExport" class="btn btn-red" type="button"
			value="<spring:message code='common.export'/>" />
		<!-- end -->
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table table-hover">
		<thead>
		<tr>
		<!--  清分属性去除 wzj 2017-11-22 begin -->
				<!-- 序号 -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.seqNo" /></th>
				<th colspan="1" rowspan="2" style="text-align: center;line-height:80px;font-size:17px"><spring:message code="clear.empWorkStatistics.empName" /></th>
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 机械清分 -->
				<th colspan="10" style="text-align:center;background-color:#f9c9c9"><spring:message code='clear.empWorkStatistics.machinery'/>(<spring:message code="clear.public.bundle"/>)</th>
				<!-- 手工清分-->
				<th colspan="10" style="text-align: center;background-color:#ddd"><spring:message code='clear.empWorkStatistics.manualAllocation'/>(<spring:message code="clear.public.bundle"/>)</th>
				<!-- 复点 -->
				<th colspan="10" style="text-align: center;background-color:#c8def5"><spring:message code='clear.empWorkStatistics.afterPoint'/>(<spring:message code="clear.public.bundle"/>)</th>		
			</tr>
			<tr>
				<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#f5e6e6">${item.label}</th>
				</c:forEach>
				<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#eee">${item.label}</th>
				</c:forEach>
				 <c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
					<th style="text-align: center;background-color:#edf6ff">${item.label}</th>
				</c:forEach> 
				
			</tr>
			<%-- <tr>
				<!-- 序号 -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code="common.seqNo" /></th>
				<!-- 员工名称 -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px" ><spring:message code="clear.empWorkStatistics.empName" /></th>
				<!-- 面值-->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code='common.denomination'/></th>
				<!-- 机械清分 -->
				<th colspan="2" style="text-align: center;background-color:#f9c9c9"><spring:message code='clear.empWorkStatistics.machinery'/></th>
				<!-- 清分流水线 -->
				<th colspan="2" style="text-align: center;background-color:#c8def5"><spring:message code='clear.empWorkStatistics.clearingLine'/></th>
				<!-- 手工清分 -->
				<th colspan="2" style="text-align: center;background-color:#f9c9c9"><spring:message code='clear.empWorkStatistics.manualAllocation'/></th>
				<!-- 抽查 -->
				<th colspan="2" style="text-align: center;background-color:#c8def5"><spring:message code='clear.task.planType.checkClear'/></th>
				<!-- 合计 -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code='common.total'/></th>
				<!-- 发现差错笔数 -->
				<th rowspan="2" style="text-align: center; line-height:80px;font-size:17px"><spring:message code='clear.empWorkStatistics.findErrorNumber'/></th>
			</tr>
			<tr>
				<!-- 正常清分 -->
				<th style="text-align: center;background-color:#f5e6e6"><spring:message code='clear.task.planType.normalClear'/></th>
				<!-- 重复清分 -->
				<th style="text-align: center;background-color:#f5e6e6"><spring:message code='clear.task.planType.repeatClear'/></th>
				<!-- 正常清分 -->
				<th style="text-align: center;background-color:#edf6ff"><spring:message code='clear.task.planType.normalClear'/></th>
				<!-- 重复清分 -->
				<th style="text-align: center;background-color:#edf6ff"><spring:message code='clear.task.planType.repeatClear'/></th>
				<!-- 正常清分 -->
				<th style="text-align: center;background-color:#f5e6e6"><spring:message code='clear.task.planType.normalClear'/></th>
				<!-- 重复清分 -->
				<th style="text-align: center;background-color:#f5e6e6"><spring:message code='clear.task.planType.repeatClear'/></th>
				<!-- 清分 -->
				<th style="text-align: center;background-color:#edf6ff"><spring:message code='clear.empWorkStatistics.clarify'/></th>
				<!-- 复点 -->
				<th style="text-align: center;background-color:#edf6ff"><spring:message code='clear.empWorkStatistics.afterPoint'/></th>
			</tr> --%>
		</thead>
		<tbody>
			<tr>
				<c:forEach items="${page.list}" var="empWorkStatistics" varStatus="status">
				<!-- style="width:45px; word-break:break-all" -->
				<td style="text-align: center;">${status.index+1}</td>
				<td>${empWorkStatistics.empName}</td>
				<!-- 增加机构 wzj 2017-11-27 begin -->
				<td>${empWorkStatistics.officeName} </td>
				<!-- end -->
				<td style="background-color:#f5e6e6">${empWorkStatistics.j1}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j2}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j3}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j4}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j5}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j6}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j7}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j8}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j9}</td>
			   	<td style="background-color:#f5e6e6">${empWorkStatistics.j10}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s1}</td>
				<td style="background-color:#eee">${empWorkStatistics.s2}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s3}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s4}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s5}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s6}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s7}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s8}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s9}</td>
			   	<td style="background-color:#eee">${empWorkStatistics.s10}</td>
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f1}</td>
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f2}</td>
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f3}</td> 
				<td style="background-color:#edf6ff">${empWorkStatistics.f4}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f5}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f6}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f7}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f8}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f9}</td> 
			   	<td style="background-color:#edf6ff">${empWorkStatistics.f10}</td> 
			   
			</tr>
			</c:forEach>
			<%-- <c:forEach items="${page.list}" var="empWorkStatistics" varStatus="status">
				<tr>
					<td style="text-align: center;">${status.index+1}</td>
					
					<td style="text-align: center;">${empWorkStatistics.empName}</td>
					<td style="text-align: center;">${sto:getGoodDictLabelWithFg(empWorkStatistics.denomination,'cnypden', '')}</td>
					<td style="text-align: center;background-color:#f5e6e6">${empWorkStatistics.jxNormal}</td>
					<td style="text-align: center;background-color:#f5e6e6">${empWorkStatistics.jxRepeat}</td>
					<td style="text-align: center;background-color:#edf6ff">${empWorkStatistics.lsxNormal}</td>
					<td style="text-align: center;background-color:#edf6ff">${empWorkStatistics.lsxRepeat}</td>
					<td style="text-align: center;background-color:#f5e6e6">${empWorkStatistics.sgNormal}</td>
					<td style="text-align: center;background-color:#f5e6e6">${empWorkStatistics.sgRepeat}</td>
					<td style="text-align: center;background-color:#edf6ff">${empWorkStatistics.ccClear}</td>
					<td style="text-align: center;background-color:#edf6ff">${empWorkStatistics.ccComplexPoint}</td>
					<td style="text-align: center;">${empWorkStatistics.totalCountSta}</td>
					<td style="text-align: center;">${empWorkStatistics.errorCount}</td>
				</tr>
			</c:forEach> --%>
			<!-- end -->
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>