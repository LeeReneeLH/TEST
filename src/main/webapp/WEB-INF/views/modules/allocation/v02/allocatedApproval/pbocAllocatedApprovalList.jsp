<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 下拨审批列表 --%>
	<title><spring:message code="allocation.allocated.approve.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocAllocatedOrder/list?bInitFlag=false&pageType=" + $("#pageType").val());
				$("#searchForm").submit();
			});
			// 批量审批
			$("#btnApproval").click(function(){
				
				var result = new Array();
                $("[id = chkItem]:checkbox").each(function () {
                    if ($(this).attr("checked") == "checked") {
                        result.push($(this).attr("value"));
                    }
                });
 
                var allIds = result.join(",");
                
                if (allIds == '') {
                	//[提示]：请选择审批流水单号！
                	alertx("<spring:message code='message.I2017' />");
					return;
				}
                var url = "${ctx}/allocation/v02/pbocAllocatedOrder/batchOperation?allIds=" + allIds + "&targetStatus=22&pageType=" + $("#pageType").val();
	            $("#searchForm").attr("action", url);
				$("#searchForm").submit();
			});
			// 批量驳回
			$("#btnReject").click(function(){
				
				var result = new Array();
				$("[id = chkItem]:checkbox").each(function () {
				    if ($(this).attr("checked") == "checked") {
				        result.push($(this).attr("value"));
				    }
				});
				
				var allIds = result.join(",");
				if (allIds == '') {
					//[提示]：请选择审批流水单号！
                	alertx("<spring:message code='message.I2017' />");
					return;
				}
				var url = "${ctx}/allocation/v02/pbocAllocatedOrder/batchOperation?allIds=" + allIds + "&targetStatus=21&pageType=" + $("#pageType").val();
				$("#searchForm").attr("action", url);
				$("#searchForm").submit();
			});
			
			// 审批全选
            $("#allCheckbox").click(function () {
            	
            	if ($(this).attr("checked") == "checked") {
            		$(this).attr("checked", true);
            	} else {
            		$(this).attr("checked", false);
            	}
            	var checkStatus = $(this).attr("checked");
                $("[id = chkItem]:checkbox").each(function () {
                    $(this).attr("checked", checkStatus == "checked" ? true : false);
                });
            });
			
         // 打印全选
            $("#allPrintCheckbox").click(function () {
            	
            	if ($(this).attr("checked") == "checked") {
            		$(this).attr("checked", true);
            	} else {
            		$(this).attr("checked", false);
            	}
            	var checkStatus = $(this).attr("checked");
                $("[id = chkPrintItem]:checkbox").each(function () {
                    $(this).attr("checked", checkStatus == "checked" ? true : false);
                });
            });
         
         	// 实时库区
            $("#btnStoreArea").click(function () {
            	
            	var content = "iframe:${ctx}/store/v01/stoAreaSettingInfo/showAreaGoodsGraph?storeAreaType=01&displayHref=hide";
				top.$.jBox.open(
						content,
						// 实时库区
						"<spring:message code='store.actualArea' />", 1024, 600, {
							buttons : {
								//关闭
								"<spring:message code='common.close' />" : true
							},
							submit : function(v, h, f) {
								
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "auto");
							}
						});
            });
         	
         	// 批量审批打印
			$("#btnPrint").click(function(){
				
				var result = new Array();
				$("[id = chkPrintItem]:checkbox").each(function () {
				    if ($(this).attr("checked") == "checked") {
				        result.push($(this).attr("value"));
				    }
				});
				
				var allIds = result.join(",");
				if (allIds == '') {
                	//[提示]：请选择打印流水单号！
                	alertx("<spring:message code='message.I2018' />");
					return;
				}
				
				var content = "iframe:${ctx}/allocation/v02/pbocAllocatedOrder/batchPrint?allIds=" + allIds;
				top.$.jBox.open(
						content,
						//打印
						"<spring:message code='common.print' />", 1024, 600, {
							buttons : {
								//打印
								"<spring:message code='common.print' />" : "ok",
								// 关闭
								"<spring:message code='common.close' />" : true
							},
							submit : function(v, h, f) {
								if (v == "ok") {
									var printDiv = h.find("iframe")[0].contentWindow.printDiv;
									$(printDiv).show();
									//打印 
									$(printDiv).jqprint();
									return true;
								}
								$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocAllocatedOrder/list?bInitFlag=false&pageType=" + $("#pageType").val());
								$("#searchForm").submit();
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "auto");
							}
						});
				
			});
			
		});
		// 打印审批物品明细
		function printApprovalGoodDetail(allId){
			var content = "iframe:${ctx}/allocation/v02/pbocAllocatedOrder/printApprovalGoodDetail?allId=" + allId;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 800, 600, {
						buttons : {
							//打印
							"<spring:message code='common.print' />" : "ok",
							// 关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var printDiv = h.find("iframe")[0].contentWindow.printDiv;
								$(printDiv).show();
								//打印 
								$(printDiv).jqprint();
								return true;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
					});
			
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 下拨审批列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.allocated.approve.list" /></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row">
				<div class="span7" style="margin-top:5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId" class="input-medium"/>
					<%-- 状态 --%>
					<label><spring:message code='common.status'/>：</label>
					<c:choose>
						<c:when test="${fns:getUser().userType == '19'}">
							<form:select path="status" id="status" class="input-medium" >
								<form:option value="">
									<spring:message code="common.select" />
								</form:option>
								<form:options items="${fns:getFilterDictList('pboc_order_quota_status', false, '21,41')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
					</c:when>
						<c:otherwise>
							<form:select path="status" id="status" class="input-medium" >
								<form:option value="">
									<spring:message code="common.select" />
								</form:option>
								<form:options items="${fns:getFilterDictList('pboc_order_quota_status', true, '20,22')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="span7" style="margin-top:5px">
					<%-- 开始日期 --%>
					<label><spring:message code="allocation.order.date" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
					&nbsp;
					<%-- 查询 --%>
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</div>
			</div>
		</form:form>
	</div>
	<input type="hidden" id="pageType" value="${pbocAllAllocateInfo.pageType }">
	<div class="row" style="margin-left:2px">
		<sys:message content="${message}"/>
		<shiro:hasPermission name="allocation.v02:pbocAllocatedOrder:approval">
			<%-- 批量审批 --%>
			<%-- <input type="button" id="btnApproval" value="<spring:message code='common.batchApprove' />" class="btn btn-primary">&nbsp; --%>
			<%-- 批量驳回 --%>
			<%-- <input type="button" id="btnReject" value="<spring:message code='common.batchReject' />" class="btn" >&nbsp; --%>
			<%-- 批量打印 --%>
			<input type="button" id="btnPrint" value="<spring:message code='common.batchPrint' />" class="btn btn-primary">
			<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
				<%-- 实时库区 --%>
				<input type="button" id="btnStoreArea" value="<spring:message code='store.actualArea' />" class="btn btn-primary">
			</c:if>
		</shiro:hasPermission>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<shiro:hasPermission name="allocation.v02:pbocAllocatedOrder:approval">
					<%-- <th style="text-align: center;" >全审批<br><input type="checkbox" id="allCheckbox"/></th> --%>
					<%-- 全打印 --%>
					<th  style="text-align: center;" ><spring:message code='common.AllPrint' /><br><input type="checkbox" id="allPrintCheckbox"/></th>
				</shiro:hasPermission>
				
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<shiro:hasAnyPermissions name="allocation.v02:pbocAllocatedOrder:approval">
					<%-- 申请机构 --%>
					<th class="sort-column a.roffice_name"><spring:message code="allocation.application.office" /></th>
					<%-- 申请类型 --%>
					<th class="sort-column a.business_type"><spring:message code="allocation.application.type" /></th>
				</shiro:hasAnyPermissions>
				<%-- 申请金额 --%>
				<th class="sort-column a.register_amount"><spring:message code="allocation.application.amount" /></th>
				<%-- 申请人 --%>
				<th><spring:message code="allocation.application.person" /></th>
				<%-- 预约日期 --%>
				<th class="sort-column a.apply_date"><spring:message code="allocation.order.date" /></th>
				<%-- 审批人 --%>
				<th><spring:message code="allocation.approve.person" /></th>
				<%-- 审批时间 --%>
				<th class="sort-column a.approval_date"><spring:message code="allocation.approve.datetime" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>
				<%-- 操作 --%>
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
		
			<c:forEach items="${page.list}" var="allocation" varStatus="status">
				<tr>
					<shiro:hasPermission name="allocation.v02:pbocAllocatedOrder:approval">
						<%-- check box --%>
						<%-- <td style="text-align: center">
							<c:if test="${allocation.status == '20'}">
								<input type="checkbox" id="chkItem" value="${allocation.allId}:${allocation.strUpdateDate}"/>
							</c:if>
						</td>
						 --%>
						<%-- check box --%>
						<td style="text-align: center" >
							<c:if test="${allocation.status == '22'}">
								<input type="checkbox" id="chkPrintItem" value="${allocation.allId}"/>
							</c:if>
						</td>
					</shiro:hasPermission>
					
					<%-- 流水单号 --%>
					<td>
						<%--
						<c:choose>
							<c:when test="${allocation.status == '20'}">
								<shiro:hasPermission name="allocation.v02:pbocAllocatedOrder:approval">
									<a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?allId=${allocation.allId}&pageType=storeApprovalEdit">${allocation.allId}</a>
								</shiro:hasPermission>
								<shiro:lacksPermission name="allocation.v02:pbocAllocatedOrder:approval">
									${allocation.allId}
								</shiro:lacksPermission>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?allId=${allocation.allId}&pageType=storeApprovalView">${allocation.allId}</a>
							</c:otherwise>
						</c:choose>
						--%>
						<a href="${ctx}/allocation/v02/pbocWorkflow/findApplicationAllocationStatus?allId=${allocation.allId}">${allocation.allId}</a>
					</td>
					<shiro:hasAnyPermissions name="allocation.v02:pbocAllocatedOrder:approval">
						<%-- 申请机构 --%>
						<td>${allocation.rofficeName}</td>
						<%-- 申请类型 --%>
						<td>${fns:getDictLabel(allocation.businessType, 'all_businessType', '')}</td>
					</shiro:hasAnyPermissions>
					<%-- 申请金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.registerAmount}" pattern="#,##0.00#" /></td>
					<%-- 申请人 --%>
					<td>${allocation.createName}</td>
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.applyDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 审批人 --%>
					<td>${allocation.approvalName}</td>
					<%-- 审批日期 --%>
					<td><fmt:formatDate value="${allocation.approvalDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 状态 --%>
					<td>${fns:getDictLabel(allocation.status,'pboc_order_quota_status',"")}</td>
					<%-- 操作 --%>
					<td>
						<c:choose>
							<c:when test="${allocation.status == '20'}">
								<shiro:hasPermission name="allocation.v02:pbocAllocatedOrder:approval">
									<a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?allId=${allocation.allId}&pageType=storeApprovalEdit" title="审批">
									<%-- <spring:message code="common.approve" /> --%><i class="fa fa-pencil-square text-red fa-lg"></i></a>
								</shiro:hasPermission>
								<shiro:lacksPermission name="allocation.v02:pbocAllocatedOrder:approval">
									<a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?allId=${allocation.allId}&pageType=storeApprovalView" title="查看" style="margin-left:10px;">
									<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i></a>
								</shiro:lacksPermission>
							</c:when>
							<c:otherwise>
								<c:if test="${allocation.status == '22'}">
									<%-- 打印明细 --%>
									<a href="#" onclick="printApprovalGoodDetail('${allocation.allId}');" title="打印" >
									<%-- <spring:message code="common.printDetail" /> --%><i class="fa fa-print text-yellow fa-lg"></i></a>
								</c:if>
								<a href="${ctx}/allocation/v02/pbocAllocatedOrder/form?allId=${allocation.allId}&pageType=storeApprovalView" title="查看" style="margin-left:10px;">
								<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i></a>
							</c:otherwise>
							
						</c:choose>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
