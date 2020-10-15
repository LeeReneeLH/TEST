<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<style>
		table #rfid1 a {
			color: green !important;
		}
		table #rfid1 a:hover {
			color: #f7716c !important;
		}
	</style>
	<!-- 缴存全景 -->
	<title><spring:message code="door.panorama.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$("#searchForm").validate({
				submitHandler: function(form){
					var beforeAmount = changeAmountToNum($("#beforeAmount").val());
					var afterAmount = changeAmountToNum($("#afterAmount").val());
					// 没有金额为空 且 前小于后
					if($("#beforeAmount").val()!="" && $("#afterAmount").val()!="" && parseFloat(beforeAmount) > parseFloat(afterAmount)){
						alertx("总金额范围有误！");
						return;
					}
					// 金额格式化
					$("#beforeAmount").val(changeAmountToNum($("#beforeAmount").val()));
					$("#afterAmount").val(changeAmountToNum($("#afterAmount").val()));
					
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			//add-start  设备缴存导出     HaoShijie    2020.06.04
			$("#exportSubmit").on('click',
					function() {
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/depositPanorama/exportDepositPanorama");
						$("#searchForm").submit();
						$("#searchForm").prop("action","${ctx}/doorOrder/v01/depositPanorama/list");
						$("#btnSubmit").attr("disabled",false);
						});
			//add-start 设备缴存导出      HaoShijie    2020.06.04
			
		});
		//总金额模糊查询输入限制  gzd 2020-04-16
		$(document).ready(function(){
			bindKeyEvent($("#beforeAmount"));
			bindKeyEvent($("#afterAmount"));
		});
		
		//正则   gzd 2020-04-16
		function bindKeyEvent(obj){
			obj.keyup(function () {
		        var reg = $(this).val().match(/\d+\.?\d{0,2}/);
		        var txt = '';
		        if (reg != null) {
		            txt = reg[0];
		        }
		        $(this).val(txt);
		    }).change(function () {
		        $(this).keypress();
		        var v = $(this).val();
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
		    });
		}
		
		/**
		 * 将金额转换成数字
		 * @param amount
		 * @author gzd 2020-04-16
		 * @returns
		 */
		function changeAmountToNum(amount) {
			if (typeof(amount) != "undefined") {
				amount = amount.toString().replace(/\$|\,/g,'');
			}
			return amount;
		}
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
			return false;
		}

	</script>
</head>
<body>

<ul class="nav nav-tabs">
	<!-- 设备缴存列表 -->
	<li class="active"><a href="${ctx}/doorOrder/v01/depositPanorama/?uninitDateFlag=0"><spring:message code="door.panorama.equipDeposit" /></a></li>
	<!-- 封包缴存列表 -->
	<li><a onClick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/packageList" target="mainFrame"><spring:message code="door.panorama.otherDeposit" /></a></li>
	<!-- 存款登记-->
	<%-- <shiro:hasPermission name="door:doorOrderInfo:edit"><li><a href="${ctx}/weChat/v03/doorOrderInfo/form"><spring:message code="door.doorOrder.deposit" /><spring:message code="common.register" /></a></li></shiro:hasPermission> --%>
</ul>
<form:form id="searchForm" modelAttribute="depositPanorama" action="${ctx}/doorOrder/v01/depositPanorama/?uninitDateFlag=0" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<div class="row search-flex">
		<div>
		    <%--机具编号 --%>
		    <label><spring:message code="door.panorama.equipId" />：</label>
	        <form:input path="equipmentName" htmlEscape="false" maxlength="32" class="input-small"/>
	    </div>
		<div>
			<%--款袋编号--%>
			<label><spring:message code="door.doorOrder.packNum" />：</label>
			<form:input path="rfid" htmlEscape="false" maxlength="32" class="input-small"/>
		</div>
		<div>
			<%--凭条号 --%>
			<label><spring:message code="door.checkCash.tickertape" />：</label>
			<form:input path="tickertape" htmlEscape="false" maxlength="40" class="input-small"/>
		</div>
		<div>
			<%-- 业务备注  --%>
			<label><spring:message code="door.historyChange.remarks" />：</label>
			<form:input path="remarks" htmlEscape="false" maxlength="32" class="input-small"/>
		</div>
		<div>
	        <%--存款人 --%>
			<label><spring:message code="door.public.createBy" />：</label>
			<form:input path="updateName" htmlEscape="false" maxlength="32" class="input-small"/>
		</div>
		<div style="display: none;">
			<%-- 门店 --%>
			<label><spring:message code="door.public.cust" /> ：</label>
			<sys:treeselect id="doorName" name="doorId"
					value="${doorOrderInfo.doorId}" labelName="doorName"
					labelValue="${doorOrderInfo.doorName}" title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData" cssClass="input-small"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
				    isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
		</div>
		<div>
			<%-- 开始日期 --%>
			<label><spring:message code="door.panorama.lastCleanDate" />：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${doorOrderInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
		</div>
		<div>
			<%-- 结束日期 --%>
			<label>~</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				value="<fmt:formatDate value="${doorOrderInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
		</div>
		<div>
			<%-- 钞袋总金额  gzd 2020-04-16 --%>
			<label><spring:message code="door.panorama.rfidAmount" />：</label>
			<input id="beforeAmount"  name="beforeAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
				value="${doorOrderInfo.beforeAmount}" placeholder="0.00" />
		</div>
		<div>
			<%-- 结束金额   gzd 2020-04-16 --%>
			<label>~</label>
			<input id="afterAmount" name="afterAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
				value="${doorOrderInfo.afterAmount}" placeholder="9999999999.99" />
		</div>
		<div>
			<%-- 状态 --%>
			<label><spring:message code="common.status" />：</label>
			<form:select path="status" class="input-small" id ="selectStatus">
				<option value="00"><spring:message code="door.panorama.all" /></option>
				<form:options items="${fns:getFilterDictList('sys_clear_type','true','0,2,3,99')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
		</div>
		&nbsp;&nbsp;
		<div>
			<%-- 查询 --%>
			<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		</div>
		&nbsp;&nbsp;
		   <%-- 导出 --%>
		<div>	
			<input id="exportSubmit" class="btn btn-red" type="button"
				value="<spring:message code='common.export'/>" />
		</div>
	</div>
</form:form>
<sys:message content="${message}"/>
<div class="table-con" >
	<table id="contentTable" class="table table-hover">
		<thead>
		<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
			<%-- 门店 --%>
			<th class="sort-column o1.name"><spring:message code="door.public.cust" /></th>
			<%-- 机具编号 --%>
		    <th class="sort-column e.series_number"><spring:message code="door.panorama.equipId" /></th>
		    <%-- 款袋编号 --%>
		    <th class="sort-column a.rfid"><spring:message code="door.doorOrder.packNum" /></th>
		    <%-- 装袋时间 --%>
		    <th class="sort-column a.create_date"><spring:message code="door.panorama.lastCleanDate" /></th>
		    <%-- 速存张数 --%>
		    <th><spring:message code="door.panorama.paperCount" /></th>
		    <%-- 速存金额 --%>
		    <th><spring:message code="door.panorama.paperAmount" /></th>
		    <%-- 强存金额 --%>
		    <th><spring:message code="door.panorama.forceAmount" /></th>
		    <%-- 其他金额 --%>
		    <th><spring:message code="door.panorama.otherAmount" /></th>
		    <%-- 总金额 --%>
		    <th class="sort-column a.amount"><spring:message code="door.panorama.totalAmount" /></th>
		    <%-- 更新日志 --%>
		    <%-- <th colspan="2" style="text-align: center;"><spring:message code="door.panorama.updateLog" /></th> --%>
		    <%-- 更新人 --%>
		    <th class="sort-column c.name"><spring:message code="door.panorama.updateBy" /></th>
		    <%-- 更新时间 --%>
		    <th class="sort-column a.update_date"><spring:message code="door.panorama.updateDate" /></th>
		    <%-- 状态 --%>
		    <th class="sort-column a.status"><spring:message code="common.status" /></th>
			<%-- 操作 --%>
			<th rowspan="2"><spring:message code='common.operation' /></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="depositPanorama" varStatus="status">
			<tr>
                <td>${status.index + 1}</td>
				<td>${depositPanorama.doorName}</td>
				<!-- 机具序列号 -->
				<td>${depositPanorama.equipmentName}</td>
				<c:choose>
					<c:when test="${depositPanorama.status == 0}">
						<td id="rfid1"><a href="${ctx}/doorOrder/v01/depositPanorama/depositPanoramaDetailForm?id=${depositPanorama.id}">${depositPanorama.rfid}</a></td>
					</c:when>
				    <c:otherwise>
				    	<td><a href="${ctx}/doorOrder/v01/depositPanorama/depositPanoramaDetailForm?id=${depositPanorama.id}">${depositPanorama.rfid}</a></td>
				    </c:otherwise>
				</c:choose>
				<td><fmt:formatDate value="${depositPanorama.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td style="text-align:right;">${depositPanorama.paperCount}</td>
				<td style="text-align:right;"><fmt:formatNumber value="${depositPanorama.paperAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;"><fmt:formatNumber value="${depositPanorama.forceAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;"><fmt:formatNumber value="${depositPanorama.otherAmount}" type="currency" pattern="#,##0.00"/></td>
				<td style="text-align:right;"><fmt:formatNumber value="${depositPanorama.amount}" type="currency" pattern="#,##0.00"/></td>
				<td>${depositPanorama.updateName}</td>
				<td><fmt:formatDate value="${depositPanorama.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${fns:getDictLabelWithCss(depositPanorama.status, 'sys_clear_type', '未命名',true)}</td>
				<td>
					<shiro:hasPermission name="doorOrder:depositPanorama:view">
						<a href="${ctx}/doorOrder/v01/clearEquipmentRecord/clearEquipmentRecordList?equipmentId=${depositPanorama.equipmentId}" title = "<spring:message code='door.panorama.clean'/>">
							<i class="fa fa-recycle text-green fa-lg"></i>
						</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="doorOrder:depositPanorama:view">
						<a href="${ctx}/doorOrder/v01/depositPanorama/depositSerial?equipmentId=${depositPanorama.equipmentId}" title = "<spring:message code='door.panorama.deposit'/>">
							<i class="fa fa-dedent text-orange fa-lg"></i>
						</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<div class="pagination">${page}</div>
</body>
</html>