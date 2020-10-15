<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%-- 明细查询 --%>
	<title>
	<spring:message code="allocation.show.detail" />
	</title>
	<meta name="decorator" content="default" />
	<script type="text/javascript">
	
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
    <c:set var="officeType" value="${officeType}"/>
	<ul class="nav nav-tabs">
	<c:choose>
	<c:when test="${officeType=='4'}">
		<li><a href="${ctx}/allocation/v01/cashHandin"><spring:message
								code="allocation.cash.handin.list" /></a></li>
	</c:when>
    <c:otherwise>
		<li><a href="${ctx}/allocation/v01/boxHandover/handin"><spring:message
								code="allocation.cash.box.handin.list" /></a></li>
	</c:otherwise>
	</c:choose>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message
								code="allocation.show.detail" /></a></li>
	</ul>
	
	<%-- 现金详细信息 --%>
	<form:form id="detailForm" modelAttribute="allAllocateInfo" action=""
		method="post" class="form-horizontal">
		<form:hidden id="hidenAllId" path="allId" />
		<form:hidden path="pageType" />

		<div class="row">
			<div class="span12">
				<div class="clear"></div>
				<%-- 流水单号 --%>
				<div class="control-group item">
					<label class="control-label"><spring:message
							code="allocation.allId" />：</label>
					<label >
						<input type="text" id="allId" name="allId"
							value="${allAllocateInfo.allId}"
							style="width: 170px; text-align: right;" readOnly="readOnly" />
					</label>
				</div>
				<%-- 上缴机构 --%>
				<div class="control-group item">
					<label class="control-label" ><spring:message
							code="allocation.cash.handin.office" />：</label>
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
			<div class="span12">
				<div class="control-group item">
					<%-- 上缴日期 --%>
					<label class="control-label" ><spring:message
							code="allocation.cash.handin.date" />：</label>
					<label >
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
		<div class="row">
			<div class="span12">
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
		<div class="row">
			<div class="span12" style="margin-top:4px">
				<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=1)" align="left" width="100%" color="#987cb9" SIZE="1">
			</div>
		</div>
			<div class="row">
			    	<div class="span8" style="text-align: right">
			    		<div class="clear"></div>
						<div class="control-group item">
							<%-- 上缴总金额 --%>
							<label class="control-label"><spring:message code="allocation.cash.handin.amount.all" />：</label>
							<label >
								<input type="text" id="registerAmountShow" value="<fmt:formatNumber value="${allAllocateInfo.registerAmount}" 
								pattern="#,##0.00#" />" class="input-medium" style="text-align:right;width:170px;" readOnly="readOnly"/>
							</label>
						</div>
					</div>
				</div>
			<div class="row">
			    	<div class="span12" style="text-align: right">
			    		<div style="overflow-y: auto; height: 315px;">
			    		<h4 style="border-top:1px solid #eee;color:#dc776a;text-align:center"><spring:message code="allocation.cash.handin.detail" /></h4>
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
										<%-- 上缴数量 --%>
										<th style="text-align: center" ><spring:message code="allocation.cash.handin.number" /></th>
										<%-- 上缴金额 --%>
										<th style="text-align: center" ><spring:message code="allocation.cash.handin.amount" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${allAllocateInfo.allAllocateItemList}" var="item" varStatus="status">
										<%-- 登记物品 --%>
											<tr>
												<td width="10%" style="text-align:center">${status.index+1}</td>
												<td style="text-align:right;">${sto:getGoodsName(item.goodsId)}</td>
												<td style="text-align:right;">${item.moneyNumber}</td>
												<td style="text-align:right;"><fmt:formatNumber value="${item.moneyAmount}" pattern="#,##0.00#" /></td>
											</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			<div class="row" >
			
			<%-- 箱袋调拨明细 --%>
			<div class="span16">
				<div style="overflow-y: auto;height: 315px;">
				<h4 style="color:#34b15b;text-align:center"><spring:message code="allocation.box.detail" /></h4>
					<table id="contentTable" class="table table-hover" >
					<thead>
						<%-- <tr>
							登记明细
							<th style="text-align: center" colspan="5"><spring:message code="allocation.box.detail" /></th>
						</tr> --%>
							<tr class="bg-light-blue">
								<%-- 序号 --%>
								<th style="text-align:center;"><spring:message code="common.seqNo" /></th>
								<%-- 箱袋编号 --%>
								<th style="text-align:center;"><spring:message code="common.boxNo" /></th>
								<!-- 箱袋类型  -->
								<th style="text-align:center;"><spring:message code="allocation.box.type" /></th> 
								<%-- 金额 --%>
								<th style="text-align:center;"><spring:message code="common.amount" /></th>
								<!-- 扫描时间（PDA） -->
								<th style="text-align:center;"><spring:message code="allocation.scan.date" />（手持设备）</th>
								<!-- 扫描状态 （扫描门） -->
								<th style="text-align:center;"><spring:message code="allocation.scan.status" />（扫描门）</th>
								<!-- 扫描时间（扫描门） -->
								<th style="text-align:center;"><spring:message code="allocation.scan.date" />（扫描门）</th>
								<!-- 出库日期  -->
								<th style="text-align:center;"><spring:message code="common.outDate" />（尾箱）</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${allAllocateInfo.allDetailList}" var="allAllocateDetail" varStatus="status">
								<tr>
									<%-- 序号 --%>
									<td style="text-align: right;">${status.index + 1}</td>
									<%-- 箱袋编号 --%>
									<td style="text-align: center;">${allAllocateDetail.boxNo}</td>
									<!-- 箱袋类型  -->
									<td style="text-align: center;">${fns:getDictLabel(allAllocateDetail.boxType,'sto_box_type',"")}</td>
									<%-- 金额 --%>
									<td style="text-align: center;"><fmt:formatNumber
											value="${allAllocateDetail.amount}" pattern="#,##0.00#" /></td>
									<!-- 扫描时间（PDA） -->
									<td style="text-align: center;"><fmt:formatDate
											value="${allAllocateDetail.pdaScanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<!-- 扫描状态 （扫描门） -->
									<c:choose>
										<c:when test="${allAllocateDetail.scanFlag eq '0'}">
										<td style="text-align: center; color:red;" >${fns:getDictLabel(allAllocateDetail.scanFlag,'all_box_scanFlag',"")}</td>
             							</c:when>
										<c:otherwise>
										<td style="text-align: center;">${fns:getDictLabel(allAllocateDetail.scanFlag,'all_box_scanFlag',"")}</td>
										 </c:otherwise>
									</c:choose>
									<!-- 扫描时间（扫描门） -->
									<td style="text-align: center;"><fmt:formatDate
											value="${allAllocateDetail.scanDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<!-- 出库日期  -->
									<td style="text-align: center;"><fmt:formatDate
											value="${allAllocateDetail.outDate}" pattern="yyyy-MM-dd" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
			
		<div>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn btn-default" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/allocation/v01/cashHandin/back?pageType=${allAllocateInfo.pageType}'" />
		</div>
	</form:form>
</body>
</html>
