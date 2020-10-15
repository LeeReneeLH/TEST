<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 发行基金调拨出库列表 --%>
	<title><spring:message code="allocation.issueFund.sametrade.outStore.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore");
				$("#searchForm").submit();
			});
			
			// 批量配款打印
			$("#btnQuota").click(function(){
				
				var result = new Array();
				$("[id = chkItem]:checkbox").each(function () {
				    if ($(this).attr("checked") == "checked") {
				        result.push($(this).attr("value"));
				    }
				});
				
				var allIds = result.join(",");
				if (allIds == '') {
                	//[提示]：请选择流水单号！
                	alertx("<spring:message code='message.I2012'/>");
					return;
				}
				
				var content = "iframe:${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/batchOperation?allIds=" + allIds + "&targetStatus=40&pageType=storeQuotaList";
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
								$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/list?bInitFlag=false&pageType=" + $("#pageType").val());
								$("#searchForm").submit();
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "auto");
							}
						});
				
			});
			
			
			// 全选
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
			
			// 实时库区
            $("#btnStoreArea").click(function () {
            	
            	var content = "iframe:${ctx}/store/v01/stoAreaSettingInfo/showAreaGoodsGraph?storeAreaType=01&displayHref=hide";
				top.$.jBox.open(
						content,
						//实时库区
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
 
		});
		// 批量配款打印
		function btnQuotaPrint(allIds){
			
			var content = "iframe:${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/batchOperation?allIds=" + allIds + "&targetStatus=40&pageType=storeQuotaList";
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
							$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/list?bInitFlag=false&pageType=" + $("#pageType").val());
							$("#searchForm").submit();
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
		<%-- 发行基金调拨出库列表 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.issueFund.sametrade.outStore.list" /></a></li>
		<%-- 发行基金调拨出库登记(link) --%>
		<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/form"><spring:message code="allocation.issueFund.sametrade.outStore.register" /></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>

			<div class="row">
				<div class="span11" style="margin-top:5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId" class ="input-small"/>
					<%-- 调拨出命令号 --%>
					<label><spring:message code="allocation.sametrade.outstore.commondnum" />：</label>
					<form:input path="commondNumber" class ="input-small"/>
					<%-- 接收机构 --%>
					<label class="control-label"><spring:message code="allocation.instore.office" />：</label>
					<sys:treeselect id="aofficeId" name="aoffice.id"
							value="${pbocAllAllocateInfo.aoffice.id}" labelName="aoffice.name"
							labelValue="${pbocAllAllocateInfo.aoffice.name}" title="<spring:message code='allocation.destory.office' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false"
							type="1" isAll="true" allowClear="true" />
					
				</div>
				<div class="span6" style="margin-top:5px">
					
					<%-- 出库状态 --%>
					<label><spring:message code='common.status'/>：</label>
					<c:choose>
						<c:when test="${fns:getUser().userType == '19'}">
							<form:select path="status" id="status" class="input-small" >
								<form:option value="">
									<spring:message code="common.select" />
								</form:option>
								<form:options items="${fns:getFilterDictList('pboc_order_quota_status',true,'22,40,99')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
						</c:when>
						<c:otherwise>
							<form:select path="status" id="status" class="input-small" >
								<form:option value="">
									<spring:message code="common.select" />
								</form:option>
								<form:options items="${fns:getFilterDictList('pboc_order_quota_status',true,'22,40')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
						</c:otherwise>
					</c:choose>
					<%-- 预约日期 --%>
					<label><spring:message code="allocation.order.date" />：</label>
			   		<input id="applyDate"  name="applyDate" type="text" readOnly="readOnly" maxlength="20" class="input-medium Wdate required" style="width:149px;"
					   	value="<fmt:formatDate value="${pbocAllAllocateInfo.applyDate}" pattern="yyyy-MM-dd"/>" 
					   	onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});"/>
				</div>
				<div class="span7" style="margin-top:5px">
					
					<%-- 出库时间 --%>
					<%-- 开始日期 --%>
					<label><spring:message code="store.outStoreDateTime" />：</label>
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
					<%-- 结束日期 --%>
					<label>~</label>
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${pbocAllAllocateInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}'});"/>
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
		
		<shiro:hasPermission name="allocation.v02:pbocHorizontalAllocatedOutStore:quota">
			<%-- 配款打印 --%>
			<%-- <input type="button" id="btnQuota" value="<spring:message code='common.quotaPrint' />" class="btn btn-primary">&nbsp; --%>
			<c:if test="${fns:getDbConfig('auto.vault.switch') != 0}">
				<%-- 实时库区 --%>
				<input type="button" id="btnStoreArea" value="<spring:message code='store.actualArea' />" class="btn btn-primary">
			</c:if>
		</shiro:hasPermission>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- <shiro:hasPermission name="allocation.v02:pbocHorizontalAllocatedOutStore:quota"> --%>
					<%-- 配款全选checkbox --%>
				<%-- 	<th><input type="checkbox" id="allCheckbox"/></th> --%>
				<%-- </shiro:hasPermission> --%>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 调拨出命令号 --%>
				<th class="sort-column a.COMMOND_NUMBER"><spring:message code="allocation.sametrade.outstore.commondnum" /></th>
				<%-- 接收机构 --%>
				<th class="sort-column a.roffice_name"><spring:message code="allocation.instore.office" /></th>
				<%-- 审批金额 --%>
				<th class="sort-column a.confirm_amount"><spring:message code="allocation.approve.amount" /></th>
				<%-- 出库金额 --%>
				<th class="sort-column a.outstore_amount"><spring:message code="allocation.outStore.amount" /></th>
				<%-- 是否一致 --%>
				<th class="sort-column a.check_result"><spring:message code="allocation.pboc.amount.issame" /></th>
				<%-- 预约日期 --%>
				<th class="sort-column a.apply_date"><spring:message code="allocation.order.date" /></th>
				<%-- 人行交接人 --%>
				<th><spring:message code="allocation.pboc.handover" /></th>
				<%-- 行外交接人 --%>
				<th><spring:message code="allocation.pboc.outside.handover" /></th>
				<%-- 配款人 --%>
				<th><spring:message code="allocation.cash.quota.name" /></th>
				<%-- 配款时间 --%>
				<th class="sort-column a.quota_date"><spring:message code="allocation.quota.datetime" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>
				<%-- 出库时间 --%>
				<th class="sort-column a.scan_gate_date"><spring:message code="allocation.outStore.datetime" /></th>
				<%-- 操作 --%>
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
		
			<c:forEach items="${page.list}" var="allocation" varStatus="status">
				<c:choose>
					<c:when test="${allocation.checkResult == '1'}">
						<tr class="alert-error"> 
					</c:when>
					<c:otherwise>
						<tr>
					</c:otherwise>
				</c:choose>
				
					<%-- <shiro:hasPermission name="allocation.v02:pbocHorizontalAllocatedOutStore:quota">  --%>
						<%-- 配款checkbox --%>
					<%-- 	<td>
						<%-- 	<c:if test="${allocation.status == '22' or allocation.status == '40'}">  --%>
						<%-- 		<input type="checkbox" id="chkItem" value="${allocation.allId}"/>  --%>
						<%-- 	</c:if> --%>
					<%-- 	</td> --%>
					<%-- </shiro:hasPermission> --%>
					<%-- 流水单号 --%>
					<td>
					<%-- <a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/form?allId=${allocation.allId}&pageType=allocatedOutStoreView">${allocation.allId}</a>--%>
						<a href="${ctx}/allocation/v02/pbocWorkflow/findAllocatedOutStoreStatus?allId=${allocation.allId}">${allocation.allId}</a>
					</td>
					<%-- 调拨出命令号 --%>
					<td>${allocation.commondNumber}</td>
					<%-- 申请机构  --%>
					<td>${allocation.aofficeName}</td>
					<%-- 审批金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td>
					<%-- 出库金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.outstoreAmount}" pattern="#,##0.00#" /></td>
					<%-- 是否一致 --%>
					<td>${fns:getDictLabel(allocation.checkResult,'all_check_result',"")}</td>
					
					<%-- 预约日期 --%>
					<td><fmt:formatDate value="${allocation.applyDate}" pattern="yyyy-MM-dd" /></td>
					<%-- 人行交接人 --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='1' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 行外交接人  --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='2' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 配款人 --%>
					<td>${allocation.quotaPersonName}</td>
					<%-- 配款日期 --%>
					<td><fmt:formatDate value="${allocation.quotaDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- 状态 --%>
					<td>${fns:getDictLabel(allocation.status,'pboc_order_quota_status',"")}</td>
					<%-- 出库日期 --%>
					<td><fmt:formatDate value="${allocation.scanGateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						<c:if test="${allocation.status == '22' or allocation.status == '40'}">
							<a href="#" onclick="btnQuotaPrint('${allocation.allId}');" title="打印" >
							<%-- <spring:message code='common.quotaPrint' /> --%><i class="fa fa-print text-yellow fa-lg"></i></a>
							&nbsp;
							<shiro:hasPermission name="allocation.v02:pbocHorizontalAllocatedOutStore:delete">
								<a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/deleteInfo?allId=${allocation.allId}&strUpdateDate=${allocation.strUpdateDate}"
									onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"  title="删除">
									<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i></a>
								&nbsp;	
							</shiro:hasPermission>
						</c:if>
						<a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/form?allId=${allocation.allId}&pageType=allocatedOutStoreView" title="查看">
						<i class="fa fa-eye fa-lg"></i></a>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
