<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 明细查询 --%>
	<title>
	<spring:message code="allocation.show.detail" />
	</title>
	<meta name="decorator" content="default" />
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
	var submitFlag;
	$(document)
			.ready(
					function() {
						resetSubmitFlag();
						$("#inputForm").validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									//errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										//$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
										} else {
											error.insertAfter(element);
										}
									}
								});

						// 打印
						$("#btnPrint").click(
								function() {
									var content = "iframe:${ctx}/allocation/v01/cashOrder/printCashOrder?allId=" + $("#hidenAllId").val();
									top.$.jBox.open(
											content,
											"打印", 750, 520, {
												buttons : {
													"打印" : "ok",
													"关闭" : true
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
								});
					});
	
	// 编辑详细
	function editDetail(detailId) {
		$("#detailForm").attr(
				"action",
				"${ctx}/allocation/v01/cashOrder/save?saveType=edit&detailId="
						+ detailId);
		$("#hidenAllId").attr("value", $("#inputFormAllId").val());

		$("#detailForm").submit();
	}
	
	function resetSubmitFlag() {
		submitFlag = false;
	}
	
	function getStorePerson(allId,personList) {
		if(personList != '[]' && personList != ''){
			//交接人员字符串变数组
			var arr = personList.split(",");
			//替换第一个元素
			arr.splice(0,1,arr[0].substring(1));
			//替换最后一个元素
			arr.splice(arr.length-1,arr.length,arr[arr.length-1].substring(0,arr[arr.length-1].length-1));
			showCenterWorkFlowDetail(allId,arr);
		}
	}
	
	function showCenterWorkFlowDetail(allId,arr){
		var content = "iframe:${ctx}/allocation/v01/cashHandin/getPersonInfo?allId="+allId+"&personList="+arr;
		top.$.jBox.open(
	  	  	content,
	  		"人员信息", 800, 600,{
			buttons : {
				"<spring:message code='common.close' />" : true
			},
			loaded : function(h) {
				$(".jbox-content", top.document).css("overflow-y", "hidden");
			}
		});
	}
</script>
	<style>
	<!-- /* 输入项 */
	.item {display: inline;float: left;}
	/* 清除浮动 */
	.clear {clear: both;}
	/* 标签宽度 */
	.label_width {width: 120px;}
	-->
	</style>
</head>
<body>

	<ul class="nav nav-tabs">
	<c:set var="officeType" value="${officeType}"/>
	<%-- 取款审批列表 --%><c:choose>
	<c:when test="${officeType=='4'}">
		<li><a href="${ctx}/allocation/v01/cashOrder"><spring:message
								code="allocation.cash.order.list" /></a></li>
	</c:when>
    <c:otherwise>
		<li><a href="${ctx}/allocation/v01/boxHandover/handout"><spring:message
								code="allocation.cash.box.handout.list" /></a></li>
	</c:otherwise>
	</c:choose>
								
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message
								code="allocation.show.detail" /></a></li>
	</ul>
	
	<%-- 现金详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">
		<sys:message content="${message}" />
		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />

		<div class="row">
			<div class="">
				<div class="clear"></div>
				<%-- 流水单号 --%>
				<div class="control-group item">
					<label class="control-label" ><spring:message
							code="allocation.allId" />：</label>
					<label>
						<input type="text" id="allId" name="allId"
							value="${allAllocateInfo.allId}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<%-- 预约机构 --%>
				<div class="control-group item">
					<label class="control-label" ><spring:message
							code="allocation.order.office" />：</label>
					<label>
						<input type="text" id="rofficeName" name="rofficeName"
							value="${allAllocateInfo.rOffice.name}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		<div class="row">
			<div class="">
				<div class="control-group item">
					<%-- 预约日期 --%>
					<label class="control-label" ><spring:message
							code="allocation.order.date" />：</label>
					<label>
						<input type="text" id="applyDate" name="applyDate"
							value="<fmt:formatDate value="${allAllocateInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
							class="input-medium" style="width: 170px; text-align: right;"
							readOnly="readOnly" />
					</label>
				</div>
				<div class="control-group item">
					<%-- 状态 --%>
					<label class="control-label" ><spring:message
							code="common.status" />：</label>
					<label >
						<input type="text" id="status" name="status"
							value="${fns:getDictLabel(allAllocateInfo.status,'all_status','')}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
			</div>
		</div>
		
		<c:if test="${empty allAllocateInfo.carNo}">
		<div class="row">
			<div class="">
				<div class="control-group item">
					<%-- 任务类型 --%>
					<label class="control-label" ><spring:message code='allocation.tasktype'/>：</label>
					<label>
						<input type="text" id="applyDate" name="applyDate"
							value=" <spring:message code='allocation.tasktype.routinet_task'/>" 
							class="input-medium" style="width:170px; text-align: right;"
							readOnly="readOnly" />
					</label>
				</div>
			</div>
		</div>
		</c:if>
		
		<c:if test="${not empty allAllocateInfo.carNo}">
		<div class="row">
			<div class="">
			<div class="control-group item">
					<%-- 任务类型 --%>
					<label class="control-label"><spring:message code='allocation.tasktype'/>：</label>
					<label>
						<input type="text" id="applyDate" name="applyDate"
							value=" <spring:message code='allocation.tasktype.temporary_task'/>" 
							class="input-medium" style="width: 170px; text-align: right;"
							readOnly="readOnly" />
					</label>
				</div>
				<div class="control-group item" >
					<%-- 车牌号码 --%>
					<label class="control-label" ><spring:message code="allocation.tasktype.car_no" />：</label>
					<label>
						<input type="text" id="applyDate" name="applyDate"
							value=" ${allAllocateInfo.carNo}" 
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
			</div>
		</div>
		</c:if>
		<div class="row">
			<div class="">
				<div class="control-group item">
					<%-- 押运人员 --%>
					<label class="control-label" >押运人员：</label>
					<%-- UPDATE-START  原因：交接人员详情显示  update by SonyYuanYang  2018/04/04  --%>
					<label>
						<input type="text" id="escortName" name="escortName"
							value="${escortName}" onclick="getStorePerson('${allAllocateInfo.allId}','${handoverIdList}')"
							class="input-medium" style="width: 170px; text-align: right;color: blue;"
							readOnly="readOnly" />
					</label>
					<%-- UPDATE-END  原因：交接人员详情显示  update by SonyYuanYang  2018/04/04  --%>
				</div>
			</div>
		</div>
		<!-- 分隔线 -->
		<!-- 预约金额明细 -->
		<div class="row">
			<div style="margin-top:4px">
				<HR style="width:1260px;FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
			<div class="row">
				<div class="span8">	
				<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div class="clear"></div>
						<div class="control-group item">
							<%-- 预约总金额 --%>
							<label class="control-label" ><spring:message code="allocation.cash.order.amount.all" />：</label>
							<label >
								<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.registerAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
								<%-- 申请总金额(格式化) --%>
								<%-- <label style="margin-left:10px">${allAllocateInfo.registerAmountBig}</label> --%>
							</label>
						</div>
							
					</div>
				</div>
			<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div style="overflow-y: auto; height: 315px;">
			    			<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="allocation.order.detail" /></h4>
							<table id="contentTable" class="table table-hover" >
								<thead>
										<%-- <tr>
											登记明细
											<th style="text-align: center" colspan="5"><spring:message code="allocation.register.detail" /></th>
										</tr> --%>
										<tr class="bg-light-blue">
										<%-- 序号 --%>
										<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
										<%-- 券别列表 --%>
										<th style="text-align: center" ><spring:message code="allocation.classificationInfo" /></th>
										<%-- 预约数量 --%>
										<th style="text-align: center" ><spring:message code="allocation.order.number" /></th>
										<%-- 预约金额 --%>
										<th style="text-align: center" ><spring:message code="allocation.cash.order.amount" /></th>
									</tr>
								</thead>
								<tbody>
								<% int iRegistIndex = 0; %>
									<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
									<c:if test="${item.confirmFlag == '0'}">
										<%-- 登记物品 --%>
										<% iRegistIndex = iRegistIndex + 1; %>
											<tr>
												<td width="10%" style="text-align:center"><%=iRegistIndex %></td>
												<td style="text-align:center;">${sto:getGoodsName(item.goodsId)}</td>
												<td style="text-align:center;">${item.moneyNumber}</td>
												<td style="text-align:center;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											</tr>
										</c:if>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>	
		<!--审批金额明细  -->		
			<div class="span8">
				<div class="row">
					<div class="span8" style="text-align: right">
						<div class="clear"></div>
						<div class="control-group item">
							<%-- 审批总金额 --%>
							<label class="control-label" ><spring:message code="allocation.approve.totalAmount" />：</label>
							<label >
								<input type="text" id="confirmAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.confirmAmount}" pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:140px;" readOnly="readOnly"/>
							</label>
						</div>
			    	</div>
				</div>
				<div class="row">
					<div class="span8" style="text-align: right">
						<div style="overflow-y: auto; height: 315px;">
							<h4 style="border-top:1px solid #eee;color:#34b15b;text-align:center"><spring:message code="allocation.approve.detail" /></h4>
							<table id="contentTable" class="table table-hover" >
								<thead>
									<tr class="bg-red">
										<%-- 序号 --%>
										<th style="text-align: center" ><spring:message code="common.seqNo" /></th>
										<%-- 券别列表 --%>
										<th style="text-align: center" ><spring:message code="allocation.classificationInfo" /></th>
										<%-- 审批数量 --%>
										<th style="text-align: center" ><spring:message code="allocation.approve.number" /></th>
										<%-- 审批金额 --%>
										<th style="text-align: center" ><spring:message code="allocation.approve.amount" /></th>
									</tr>
								</thead>
								<tbody>
									<% int iConfirmIndex = 0; %>
									<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
										<%-- 审批登记物品 --%>
										<c:if test="${item.confirmFlag == '1'}">
										<% iConfirmIndex = iConfirmIndex + 1; %>	
											<tr>
												<td width="10%" style="text-align:center"><%=iConfirmIndex %></td>
												<td style="text-align:center;">${sto:getGoodsName(item.goodsId)}</td>
												<td style="text-align:center;">${item.moneyNumber}</td>
												<td style="text-align:center;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											</tr>
										</c:if>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>	
		</div>	
			<div class="row" >
			
			<%-- 箱袋调拨明细 --%>
			<div class="span15">
				<div style="overflow-y: auto; height: 315px;">
					<h4 style="color:#e8a611;text-align:center"><spring:message code="allocation.box.detail" /></h4>
							
					<table  id="contentTable" class="table table-hover">
						<thead>
	<%-- 						<tr >
								<th style="text-align:center;" colspan="7"><spring:message code="allocation.box.detail" /></th>
							</tr> --%>
							<tr class="bg-green">
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
								<%-- 箱袋类型 --%>
								<th style="text-align:center;"><spring:message code="store.boxType" /></th>
								<!-- 扫描时间（PDA） -->
								<th style="text-align:center;"><spring:message code="allocation.scan.date" />（手持设备）</th>

								<c:if test="${empty allAllocateInfo.carNo}">
									<!-- 扫描状态 （扫描门） -->
									<th style="text-align: center;"><spring:message code="allocation.scan.status" />（扫描门）</th>
									<!-- 扫描时间（扫描门） -->
									<th style="text-align: center;"><spring:message code="allocation.scan.date" />（扫描门）</th>
									<!-- 出库日期  -->
									<th style="text-align: center;"><spring:message code="common.outDate" />（尾箱）</th>
								</c:if>
								<c:if test="${allAllocateInfo.status=='14'}">
									<%-- 接收方式 --%>
									<th style="text-align:center;">接收方式</th>
									<%-- 授权人--%>
									<th style="text-align:center;">授权人</th>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${allAllocateInfo.allDetailList}"
								var="allAllocateDetail" varStatus="status">
								<tr>
									<%-- 序号 --%>
									<td style="text-align: center;">${status.index + 1}</td>
									<%-- 箱袋编号 --%>
									<td style="text-align: center;">${allAllocateDetail.boxNo}</td>
									<%-- 箱袋类型 --%>
									<td style="text-align: center;">${fns:getDictLabel(allAllocateDetail.boxType,'sto_box_type',"")}</td>
									<!-- 扫描时间（PDA） -->
									<td style="text-align: center;"><fmt:formatDate
											value="${allAllocateDetail.pdaScanDate}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<c:if test="${empty allAllocateInfo.carNo}">
										<!-- 扫描状态 （扫描门） -->
										<c:choose>
											<c:when test="${allAllocateDetail.scanFlag eq '0'}">
												<td style="text-align: center; color: red;">${fns:getDictLabel(allAllocateDetail.scanFlag,'all_box_scanFlag',"")}</td>
											</c:when>
											<c:otherwise>
												<td style="text-align: center;">${fns:getDictLabel(allAllocateDetail.scanFlag,'all_box_scanFlag',"")}</td>
											</c:otherwise>
										</c:choose>

										<!-- 扫描时间（扫描门） -->
										<td style="text-align: center;"><fmt:formatDate
												value="${allAllocateDetail.scanDate}"
												pattern="yyyy-MM-dd HH:mm:ss" /></td>
										<!-- 出库日期  -->
										<td style="text-align: center;"><fmt:formatDate
												value="${allAllocateDetail.outDate}" pattern="yyyy-MM-dd" /></td>
									</c:if>
									<c:if test="${allAllocateInfo.status=='14'}">
									<c:choose>
											<c:when test="${allAllocateDetail.outletsScanFlag eq'1'}">
												<%-- 授权状态  --%>
												<td style="text-align: center;">PDA扫描接收</td>
												<%-- 授权人  --%>
												<td style="text-align: center;"></td>
											</c:when>
											<c:when test="${allAllocateDetail.outletsScanFlag eq'2'}">
												<%-- 授权状态  --%>
												<td style="text-align: center;">页面点击接收</td>
												<%-- 授权人  --%>
												<td style="text-align: center;"></td>
											</c:when>
											<c:otherwise>
												<%-- 授权状态  --%>
												<td style="text-align: center;">授权接收</td>
												<%-- 授权人  --%>
												<td style="text-align: center;">${managerName}</td>
											</c:otherwise>
									</c:choose>
									</c:if>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
			
			
		<div>
			<c:if test="${allAllocateInfo.pageType eq 'storeView'and allAllocateInfo.status eq '15'}">
				<!-- 打印 -->
				<input id="btnPrint" type="button" class="btn btn-primary" value="<spring:message code='common.print'/>" />
			</c:if>
			
			<%-- 返回 --%>
			<input id="btnCancel" class="btn btn-default" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashOrder/back?allId=${allAllocateInfo.allId}&pagetype=${allAllocateInfo.pageType} '" />
		</div>
	</form:form>
</body>
</html>
