<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="atm.brands.manage"/></title>
	<meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		// 初始化ATM型号列表信息
		var delFlagValue = $('#delFlag').val();
		changeByStatus(delFlagValue);
		
	});
	
		/* 根据状态加载型号 */
		function changeByStatus(delVal){
			var url = '${ctx}/atm/v01/atmBrandsInfo/changeByStatus';
			/* 清空ATM型号内容  */
			$('#atmTypeNo').select2("val",'');
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(delVal)
				},
				success : function(serverResult, textStatus) {
					$('#atmTypeNo').select2({
						containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
					});
					/* 增加返回结果非空校验  */
					/* if(serverResult.length>1){
						$('#atmTypeNo').select2("val",serverResult[0].id);
					} */ 
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
			return item.label;
		}
		
		// 删除操作判断
		function checkDelete(checkId){
			
			var delUrl = "${ctx}/atm/v01/atmBrandsInfo/delete?id="+checkId;
			
			$.ajax({
				url : ctx + '/atm/v01/atmBrandsInfo/checkDel?checkId=' + checkId,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					// 判断该品牌型号下是否存在ATM机 true不存在，false存在
					if(data == true){
						confirmx('<spring:message code="message.I0001"/>', delUrl);
					}else if(data == false){
						confirmx('<spring:message code="message.E4054"/>', delUrl);
					}
				},
				error : function(data, status, e) {
					
				}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/atm/v01/atmBrandsInfo/"><spring:message code="atm.brands.list"/></a></li>
		<shiro:hasPermission name="atm:atmBrandsInfo:edit">
			<li><a href="${ctx}/atm/v01/atmBrandsInfo/form"><spring:message code="atm.brandModel" /><spring:message code="common.add" /></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="atmBrandsInfo" action="${ctx}/atm/v01/atmBrandsInfo/list?isSearch=true" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		
		<label><spring:message code="common.status" />：</label>
		<form:select id="delFlag" path="delFlag" class="input-medium" onchange="changeByStatus(this.value)">
			<!--<form:option value=""><spring:message code="common.select"/></form:option> -->
			<form:options items="${fns:getDictList('del_flag')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		&nbsp;&nbsp;<label><spring:message code="atm.model" />：</label>
		<form:input id="atmTypeNo" type="hidden" path="atmTypeNo" style="width:210px;"/>
		<%-- <form:select path="atmTypeNo" class="input-xlarge">
		    <form:option value=""><spring:message code="common.select"/></form:option>
			<form:options items="${atm:getAtmTypesinfoList()}"
				itemLabel="atmTypeName" itemValue="atmTypeNo" htmlEscape="false" />
		</form:select> --%>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
		    <tr>
				<th><spring:message code="common.seqNo"/></th>
				<th class="sort-column a.atm_brands_no"><spring:message code="atm.brands.id"/></th>
				<th><spring:message code="atm.brands.name"/></th>
				<th class="sort-column a.atm_type_no"><spring:message code="atm.model.id"/></th>	
				<th><spring:message code="atm.model.name"/></th>
				
				<th><spring:message code="atm.wayNum"/></th>
			
				<th class="sort-column a.get_box_type"><spring:message code="atm.drawBox.model"/></th>
			
				<th><spring:message code="atm.drawBox.wayNum"/></th>
			
				<th class="sort-column a.back_box_type"><spring:message code="atm.reclaimBox.model"/></th>
			
				<th><spring:message code="atm.reclaimBox.wayNum"/></th>
			
				<th class="sort-column a.cycle_box_type"><spring:message code="atm.recyclingBox.model"/></th>
			
				<th><spring:message code="atm.recyclingBox.wayNum"/></th>
			
				<th class="sort-column a.deposit_box_type"><spring:message code="atm.depositBox.model"/></th>
			
				<th><spring:message code="atm.depositBox.wayNum"/></th>
				
				<shiro:hasPermission name="atm:atmBrandsInfo:edit"><th><spring:message code="common.operation"/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="atmBrandsInfo" varStatus="status">
			<tr>
			    <td>${status.index+1}</td>
			   
				<td>${atmBrandsInfo.atmBrandsNo}</td>
				<td>${atmBrandsInfo.atmBrandsName}</td>
				 <td><a href="${ctx}/atm/v01/atmBrandsInfo/view?id=${atmBrandsInfo.id}">${atmBrandsInfo.atmTypeNo}</a></td>
				<td>${atmBrandsInfo.atmTypeName}</td>
				<td>${atmBrandsInfo.boxNum}</td>
				<td>${atmBrandsInfo.getBoxType}</td>
				<td>${atmBrandsInfo.getBoxNumber}</td>
				<td>${atmBrandsInfo.backBoxType}</td>
				<td>${atmBrandsInfo.backBoxNumber}</td>
				<td>${atmBrandsInfo.cycleBoxType}</td>
				<td>${atmBrandsInfo.cycleBoxNumber}</td>
				<td>${atmBrandsInfo.depositBoxType}</td>
				<td>${atmBrandsInfo.depositBoxNumber}</td>
					<shiro:hasPermission name="atm:atmBrandsInfo:edit">
						<td>
							<c:choose>
								<c:when test="${atmBrandsInfo.delFlag == '0' }">
									<a href="${ctx}/atm/v01/atmBrandsInfo/form?id=${atmBrandsInfo.id}" title="编辑">
									<i class="fa fa-edit text-green fa-lg"></i></a>
									<a href="#" onclick="checkDelete('${atmBrandsInfo.id}')" title="删除">
										<i class="fa fa-trash-o text-red fa-lg"></i></a>
								</c:when>
								<c:otherwise>
									<a href="${ctx}/atm/v01/atmBrandsInfo/recovery?id=${atmBrandsInfo.id}" title="恢复删除">
									<spring:message code="common.recovery" /></a>
								</c:otherwise>
							</c:choose>
						</td>
					</shiro:hasPermission>
				</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
