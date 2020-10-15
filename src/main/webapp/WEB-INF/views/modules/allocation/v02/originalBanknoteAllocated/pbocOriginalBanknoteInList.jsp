<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 原封新券入库列表 --%>
	<title><spring:message code="allocation.originalBanknot.instore.list" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocOriginalBankNoteIn/list");
				$("#searchForm").submit();
			});
			
			// 批量配款打印
			$("#btnPrint").click(function(){
				
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
				
				var content = "iframe:${ctx}/allocation/v02/pbocOriginalBankNoteIn/batchOperation?allIds=" + allIds + "&targetStatus=40";
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
								$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocOriginalBankNoteIn/list");
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
		function btnPrintPosition(allIds) {
			
			var content = "iframe:${ctx}/allocation/v02/pbocOriginalBankNoteIn/batchOperation?allIds=" + allIds + "&targetStatus=40";
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
							$("#searchForm").attr("action", "${ctx}/allocation/v02/pbocOriginalBankNoteIn/list");
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
		<%-- 原封新券入库列表(link) --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.originalBanknot.instore.list" /></a></li>
	</ul>
	<div class="row">
		<form:form id="searchForm" modelAttribute="pbocAllAllocateInfo" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>

			<div class="row">
				<div class="span8" style="margin-top:5px">
					<%-- 流水单号 --%>
					<label><spring:message code="allocation.allId" />：</label>
					<form:input path="allId"/>
				
					<%-- 状态 --%>
					<label><spring:message code='common.status'/>：</label>
					<form:select path="status" id="status" class="input-medium" >
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${fns:getDictList('pboc_original_instore_status')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				</div>
			
				<div class="span7" style="margin-top:5px">
					<%-- 入库时间 --%>
					<%-- 开始日期 --%>
					<label><spring:message code="store.inStoreDateTime" />：</label>
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
		
		<%-- 打印位置信息 --%>
		<%-- <input type="button" id="btnPrint" value="打印位置信息" class="btn btn-primary">&nbsp;--%>
		<%-- 实时库区 --%>
		<input type="button" id="btnStoreArea" value="<spring:message code='store.actualArea' />" class="btn btn-primary">
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 全选checkbox --%>
				<%-- <th><input type="checkbox" id="allCheckbox"/></th> --%>
				<%-- 流水单号 --%>
				<th class="sort-column a.all_id"><spring:message code="allocation.allId" /></th>
				<%-- 入库金额 --%>
				<th class="sort-column a.confirm_amount"><spring:message code="allocation.inStore.amount" /></th>
				<%-- 人行交接人 --%>
				<th><spring:message code="allocation.pboc.handover" /></th>
				<%-- 行外交接人 --%>
				<th><spring:message code="allocation.pboc.outside.handover" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>
				<%-- 入库时间 --%>
				<th class="sort-column a.scan_gate_date"><spring:message code="store.inStoreDateTime" /> </th>
				<%-- 操作 --%>
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
		
			<c:forEach items="${page.list}" var="allocation" varStatus="status">
				<tr>
					<%-- 打印checkbox --%>
					<%-- <td>
						<input type="checkbox" id="chkItem" value="${allocation.allId}"/>
					</td>
					 --%>
					<%-- 流水单号 --%>
					<td>
					 <%-- <a href="${ctx}/allocation/v02/pbocOriginalBankNoteIn/form?allId=${allocation.allId}">${allocation.allId}</a> --%>
					 ${allocation.allId}
					</td>
					<%-- 入库金额 --%>
					<td style="text-align:right;"><fmt:formatNumber value="${allocation.confirmAmount}" pattern="#,##0.00#" /></td>
					<%-- 人行交接人 --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='2' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 行外交接人  --%>
					<td>
						<c:if test="${allocation.pbocAllHandoverInfo != null }">
							<c:forEach items="${allocation.pbocAllHandoverInfo.handoverUserDetailList }" var="handoverUserDetail">
								<c:if test="${handoverUserDetail.type =='1' }">
									${handoverUserDetail.escortName }&nbsp;&nbsp;
								</c:if>
							</c:forEach>
						</c:if>
					</td>
					<%-- 状态 --%>
					<td>${fns:getDictLabel(allocation.status,'pboc_original_instore_status',"")}</td>
					<%-- 入库时间 --%>
					<td><fmt:formatDate value="${allocation.scanGateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						<%-- 打印位置信息 --%>
						<a href="#" onclick="btnPrintPosition('${allocation.allId}');"><spring:message code="common.positionInfo.print" /></a>&nbsp;&nbsp;
						<a href="${ctx}/allocation/v02/pbocOriginalBankNoteIn/form?allId=${allocation.allId}" title="查看">
						<%-- <spring:message code="common.view" /> --%><i class="fa fa-eye fa-lg"></i></a>
					</td>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<div class="pagination">${page}</div>
</body>
</html>
