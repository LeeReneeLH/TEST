<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>
<%@page import="com.coffer.core.modules.sys.utils.UserUtils"%>
<html>
<head>
<!-- 款箱拆箱列表 -->
<title><spring:message code="clear.atmCashBox.history" /></title>
<meta name="decorator" content="default" />
	<style type="text/css">
		td {
			word-wrap: break-word;
			word-break: break-all; 
		}
		@media only screen and (max-width: 1760px){
		    #divTable{overflow: auto;}
		}
	</style>
<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	$(document).ready(function() {

	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		window.top.clickSelect();
		$("#searchForm").submit();
		return false;
	}

	// 授权按钮按下处理
	function authorization() {
		if ($("#authorizationForm").valid()) {
			$.post($("#authorizationForm").attr("action"), $(
					"#authorizationForm").serialize(), function(data) {
				if (data.message == "0") {
					var doorId = $('#doorId').val();
					var authUserName = $('#authUserName').val();
					var url = "${ctx}/door/checkCashMain/delete?id=" + doorId
							+ "&authUserName=" + authUserName;
					$("#inputForm").attr("action", url);
					$("#inputForm").submit();
				} else {
					$("#message").text(data.message);
				}
			}, "json");
		}
	}

	// 删除处理
	function rowDelete(v_doorId) {
		var url = "${ctx}/door/checkCashMain/delete?id=" + v_doorId;
		$("#inputForm").attr("action", url);
		$("#inputForm").submit();
	}

	var runKbn = "0"; //执行区分

	// 授权页面的呼出（删除处理）
	function authDelete(v_id) {
		$('#doorId').val(v_id);
		$('#myModal').modal('show');
		if (runKbn == "0") {
			runKbn = "1";
			$("#myModal").on("hidden.bs.modal", function() {
				$('#doorId').val("");
				document.getElementById('message').innerHTML = "";
				$('#authUserName').val("");
				$('#authPassword').val("");

				try {
					$("#authorizationForm").data('validator').resetForm(); //验证结果的清除
				} catch (e) {
				}
			});
		}
		return false;
	}

	//冲正授权
	function authorize(id) {
		var content = "iframe:${ctx}/collection/v03/checkCash/authorizeDetail";
		//授权操作
		top.$.jBox
				.open(
						content,
						"<spring:message code='message.I7239' />",
						470,
						220,
						{
							buttons : {
								//确认
								"<spring:message code='common.confirm' />" : "ok",
								//关闭
								"<spring:message code='common.close' />" : true
							},
							submit : function(v, h, f) {
								if (v == "ok") {
									var contentWindow = h.find("iframe")[0].contentWindow;
									var vData;
									// 用户名
									var authorizer = contentWindow.document
											.getElementById("authorizer").value;
									if (authorizer == null || authorizer == "") {
										//请输入用户名
										alertx("<spring:message code='message.I7218' />");
										return false;
									}
									//密码
									var authorizeBy = contentWindow.document
											.getElementById("authorizeBy").value;
									if (authorizeBy == null
											|| authorizeBy == "") {
										//请输入密码
										alertx("<spring:message code='message.I7219' />");
										return false;
									}
									//授权后台验证
									$
											.ajax({
												url : ctx
														+ '/collection/v03/checkCash/authorizeUser?authorizer='
														+ authorizer
														+ '&authorizeBy='
														+ authorizeBy,
												type : 'post',
												dataType : 'json',
												async : false,
												success : function(data, status) {
													vData = data;
												},
												error : function(data, status,
														e) {
													return false;
												}
											});
									//授权验证结果的判断
									var vResult = vData.result;
									//正常
									if (vResult == "OK") {
										$("#inputForm").attr(
												"action",
												"${ctx}/collection/v03/checkCash/reverse?id="
														+ id);
										loading('正在提交，请稍等...');
										$("#inputForm").submit();
										return true;
									} else {
										//异常
										alertx(vResult);
										return false;
									}
								}
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "hidden");
							}
						});
	}
	
	/**
	 * 将金额转换成数字  gzd 2020-05-07
	 * @param amount
	 * @returns
	 */
	function changeAmountToNum(amount) {
		if (typeof(amount) != "undefined") {
			amount = amount.toString().replace(/\$|\,/g,'');
		}
		return amount;
	}
	
	// 一键拆箱 gzd 2020-05-07
	function cx(outNo,checkAmount) {
		// 设置弹窗路径
		var content = "iframe:${ctx}/collection/v03/checkCash/quickUnboxing?id=" + outNo + '&checkAmount=' + checkAmount;
		top.$.jBox.open(
			content,
			"快速拆箱", 650, 350, {
				buttons: {
					//确认
					"<spring:message code='common.confirm' />" : "ok",
					//关闭
					"<spring:message code='common.close' />": true
				},
				submit : function(v, h, f) {
					if (v == "ok") {
						var contentWindow = h.find("iframe")[0].contentWindow;
						// inputAmount
						var inputAmount = contentWindow.document.getElementById("inputAmount").value;
						// checkAmount
						var checkAmount = contentWindow.document.getElementById("checkAmount").value;
						// authUserId
						var authUserId = contentWindow.document.getElementById("authUserId").value;
						// hidPrincipalId
						var hidPrincipalId = contentWindow.document.getElementById("hidPrincipalId").value;
						// outNo
						var outNo = contentWindow.document.getElementById("hidOutNo").value;
						// updateCnt
						var updateCnt = contentWindow.document.getElementById("updateCnt").value;
						// custNo
						var custNo = contentWindow.document.getElementById("custNo").value;
						// custName
						var custName = contentWindow.document.getElementById("custName").value;
						// rfid
						var rfid = contentWindow.document.getElementById("rfid").value;
						// detailAmount
						var detailAmount = contentWindow.document.getElementById("detailAmount").value;
						// rowCount
						var rowCount = contentWindow.document.getElementById("rowCount").value;
						// saveErrorMoney
						var saveErrorMoney = contentWindow.document.getElementById("saveErrorMoney").value;
						// trueSumMoney
						var trueSumMoney = contentWindow.document.getElementById("trueSumMoney").value;
						// diffAmount
						var diffAmount = contentWindow.document.getElementById("diffAmount").value;
						// boxCount
						var boxCount = contentWindow.document.getElementById("boxCount").value;
						// remarks
						var remarks = contentWindow.document.getElementById("remarks").value;
						
						if(inputAmount == 0){
							//总金额不能为0
							alertx("<spring:message code='message.I7205' />");
							return false;
						}
						if(checkAmount == null || checkAmount == ''){
							//清机金额不能为空
							alertx("清点金额不能为空");
							return false;
						}
						var registerAmountShow=parseFloat(changeAmountToNum(contentWindow.document.getElementById("registerAmountShow").innerHTML));
						var registerAmountShow2=parseFloat(changeAmountToNum(contentWindow.document.getElementById("registerAmountShow2").innerHTML));
						var amountShow=registerAmountShow+registerAmountShow2;
						var checkAmount=parseFloat(changeAmountToNum(contentWindow.document.getElementById("checkAmount").value));
						if(amountShow != 0 && amountShow != checkAmount){
							alertx("清点金额与明细总额不相等！");
							return false;
						}
						//金额明细
						var i=0;
						var amountListStr='';
						$(contentWindow.document.getElementById("tblDetailData")).find("tr").each(function(){
							if(i > 0){
								amountListStr=amountListStr+","+$(this).children('input:eq(0)').val()+"_"+changeAmountToNum($(this).children('td:eq(2)').text().trim());
							}
							i++;
						});
						amountListStr=amountListStr.substring(1);
						
						//是否存在差额
						if(changeAmountToNum(contentWindow.document.getElementById('diffAmount').value)!=0 && Math.abs(changeAmountToNum(contentWindow.document.getElementById('diffAmount').value)) > 1){
							var content1 = "iframe:${ctx}/collection/v03/checkCash/authorizeDetail";
							//授权操作
							top.$.jBox.open(
								content1,
								"<spring:message code='message.I7239' />", 470, 220, {
									buttons : {
										//上一步
										"上一步" : "back",
										//确认
										"<spring:message code='common.confirm' />" : "ok",
										//关闭
										"<spring:message code='common.close' />" : true 
									},
									submit : function(v1, h1, f1) {
										if (v1 == "back") {
											// '上一步'操作
											cx(outNo,checkAmount);
										}else if (v1 == "ok") {
											var contentWindow1 = h1.find("iframe")[0].contentWindow;
											var vData;
											
											// 用户名
											var authorizer = contentWindow1.document.getElementById("authorizer").value;
											if(authorizer == null || authorizer == ""){
												//请输入用户名
												alertx("<spring:message code='message.I7218' />");
												return false;
											}
											//密码
											var authorizeBy = contentWindow1.document.getElementById("authorizeBy").value;
											if(authorizeBy == null || authorizeBy == ""){
												//请输入密码
												alertx("<spring:message code='message.I7219' />");
												return false;
											}
											//授权后台验证
											$.ajax({
												url : ctx + '/collection/v03/checkCash/authorizeUser?authorizer=' + authorizer + '&authorizeBy=' + authorizeBy ,
												type : 'post',
												dataType : 'json',
												async:false,
												success : function(data, status) {
													vData = data;
												},
												error : function(data, status, e) {
													return false;
												}
											});
											//授权验证结果的判断
											var vResult = vData.result;
											//正常
											if (vResult == "OK"){
												authUserId = vData.userId;
												$("#inputForm").attr(
														"action",
														"${ctx}/collection/v03/checkCash/save?inputAmount=" + inputAmount
																+ "&checkAmount=" + checkAmount
																+ "&authUserId=" + authUserId
																+ "&hidPrincipalId=" + hidPrincipalId
																+ "&outNo=" + outNo
																+ "&updateCnt=" + updateCnt
																+ "&custNo=" + custNo
																+ "&custName=" + custName
																+ "&rfid=" + rfid
																+ "&detailAmount=" + detailAmount
																+ "&rowCount=" + rowCount
																+ "&saveErrorMoney=" + saveErrorMoney
																+ "&trueSumMoney=" + trueSumMoney
																+ "&diffAmount=" + diffAmount
																+ "&boxCount=" + boxCount
																+ "&remarks=" + remarks
																+ "&amountListStr=" + amountListStr );
												loading('正在提交，请稍等...');
												window.top.clickSelect();
												$("#inputForm").submit();
												return true;
											}else{
												//异常
												alertx(vResult);
												return false;
											}
										}
									},
									loaded : function(h) {
										$(".jbox-content", top.document).css(
												"overflow-y", "hidden");
									}
								}
							);
						}else if(changeAmountToNum(contentWindow.document.getElementById('diffAmount').value)!=0 && Math.abs(changeAmountToNum(contentWindow.document.getElementById('diffAmount').value)) <= 1){
							// 有差错金额且在许可范围内  确认人设置为当前登陆人
							authUserId = hidPrincipalId;
							$("#inputForm").attr(
									"action",
									"${ctx}/collection/v03/checkCash/save?inputAmount=" + inputAmount
											+ "&checkAmount=" + checkAmount
											+ "&authUserId=" + authUserId
											+ "&hidPrincipalId=" + hidPrincipalId
											+ "&outNo=" + outNo
											+ "&updateCnt=" + updateCnt
											+ "&custNo=" + custNo
											+ "&custName=" + custName
											+ "&rfid=" + rfid
											+ "&detailAmount=" + detailAmount
											+ "&rowCount=" + rowCount
											+ "&saveErrorMoney=" + saveErrorMoney
											+ "&trueSumMoney=" + trueSumMoney
											+ "&diffAmount=" + diffAmount
											+ "&boxCount=" + boxCount
											+ "&remarks=" + remarks
											+ "&amountListStr=" + amountListStr );
							loading('正在提交，请稍等...');
							window.top.clickSelect();
							$("#inputForm").submit();
						}else{
							$("#inputForm").attr(
									"action",
									"${ctx}/collection/v03/checkCash/save?inputAmount=" + inputAmount
											+ "&checkAmount=" + checkAmount
											+ "&authUserId=" + authUserId
											+ "&hidPrincipalId=" + hidPrincipalId
											+ "&outNo=" + outNo
											+ "&updateCnt=" + updateCnt
											+ "&custNo=" + custNo
											+ "&custName=" + custName
											+ "&rfid=" + rfid
											+ "&detailAmount=" + detailAmount
											+ "&rowCount=" + rowCount
											+ "&saveErrorMoney=" + saveErrorMoney
											+ "&trueSumMoney=" + trueSumMoney
											+ "&diffAmount=" + diffAmount
											+ "&boxCount=" + boxCount
											+ "&remarks=" + remarks
											+ "&amountListStr=" + amountListStr );
							loading('正在提交，请稍等...');
							$("#inputForm").submit();
						}
					}
				},
				loaded: function (h) {
					$(".jbox-content", top.document).css(
							"overflow-y", "hidden");
				}
		});
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 款箱拆箱列表 -->
		<li><a onClick="window.top.clickSelect();" href="${ctx}/collection/v03/checkCash/"><spring:message code="door.checkCash.title" /><spring:message code="common.list" /></a></li>
		<!-- 款箱拆箱添加 -->
		<%--<shiro:hasPermission name="check:checkCash:edit">
		<li><a href="${ctx}/collection/v03/checkCash/form"><spring:message code="door.checkCash.title" /><spring:message code="common.add" /></a></li>
		</shiro:hasPermission> --%>
		<!-- 拆箱历史记录 -->
		<li class="active"><a onClick="window.top.clickSelect();" href="${ctx}/collection/v03/checkCash/historyList"><spring:message code="clear.atmCashBox.history" /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="checkCashMain" action="" method="post" class="form-horizontal"></form:form>
	<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>" />
	<c:set var="ClearRoleId" value="<%=Constant.RoleId.CLEAR%>" />
	<c:set var="user" value="<%=UserUtils.getUser()%>" />
	<!-- <div class="row"> -->
	<form:form id="searchForm" modelAttribute="checkCashMain" action="${ctx}/collection/v03/checkCash/historyList?uninitDateFlag=0" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();" />
		<div class="row search-flex">
			<!-- <div class="span14" style="margin-top:5px"> -->
			<%-- 拆箱单号 --%>
			<div>
				<label><spring:message code="door.checkCash.codeNo" /> ：</label>
				<form:input path="outNo" htmlEscape="false" maxlength="32" class="input-small" />
			</div>
			<%-- 款袋编号 --%>
			<div>
				<label><spring:message code="door.checkCash.packNum" />：</label>
				<form:input path="rfid" htmlEscape="false" maxlength="32" class="input-small" />
			</div>
			<%-- 凭条号 --%>
			<div>
				<label><spring:message code="door.checkCash.tickertape" />：</label>
				<form:input path="tickertape" htmlEscape="false" maxlength="32" class="input-small" />
			</div>
			<%-- 门店 --%>
			<div>
				<label><spring:message code="door.public.cust" /> ：</label>
				<sys:treeselect id="searchCustName" name="searchCustNo"
					value="${checkCashMain.searchCustNo}" labelName="searchCustName"
					labelValue="${checkCashMain.searchCustName}"
					title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData" cssClass="required input-small"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="8"
					maxType="9" isAll="true" allowClear="true"
					checkMerchantOffice="true" clearCenterFilter="true" />
			</div>
			<%-- 清分人员 --%>
			<div>
				<label><spring:message code="door.checkCash.clearMan" /> ：</label>
				<form:select path="searchClearManNo" id="searchClearManNo"
					class="input-small required" style="font-size:15px;color:#000000">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getUsersByTypeRoleOffice(ConClear,ClearRoleId,fns:getUser().getOffice().getId())}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
			</div>
			<%-- 是否清分 --%>
			<%-- <div>
				<label><spring:message code="door.checkCash.checked" /> ：</label>
				<form:select path="checked" id="checked"
					class="input-small required" style="font-size:15px;color:#000000">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${fns:getDictList('check_status')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div> --%>
			<%-- 登记日期 --%>
			<div>
				<label><spring:message code="door.checkCash.registerDate" />
					：</label> <input id="regTimeStart" name="regTimeStart" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${checkCashMain.regTimeStart}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'regTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<div>
				<label>~</label> <input id="regTimeEnd" name="regTimeEnd"
					type="text" readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${checkCashMain.regTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'regTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			<%-- 7位码(备注) --%>
			<div>
				<label><spring:message code="door.historyChange.remarks" /> ：</label>
				<form:input path="oRemarks" htmlEscape="false" maxlength="7" class="input-small" />
			</div>
			<%--  --%>
			<div>
				<label><spring:message code="door.errorInfo.status" /> ：</label>
				<form:select path="errorStatus" id="errorStatus"
					class="input-small required" style="font-size:15px;color:#000000">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:option value="00">
						<spring:message code="common.other" />
					</form:option>
					<form:options items="${fns:getFilterDictList('check_error_status', true , '5')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
			&nbsp;&nbsp;
			<%-- 查询 --%>
			<div>
				<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>" />
			</div>
			<!-- </div> -->
		</div>
	</form:form>
	<!-- </div> -->
	<sys:message content="${message}" />
	<div id="divTable" class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 拆箱单号 --%>
				<th class="sort-column a.out_no"><spring:message
						code="door.checkCash.codeNo" /></th>
				<%-- 包号 --%>
				<th class="sort-column a.rfid"><spring:message
						code="door.doorOrder.packNum" /></th>
				<%-- 门店名称 --%>
				<th class="sort-column a.cust_name"><spring:message
						code="door.public.custName" /></th>
				<%-- 录入金额(元) --%>
				<th class="sort-column inputAmount"><spring:message
						code="door.checkCash.inputMoney" />
					<spring:message code="common.units.yuan.alone" /></th>
				<%-- 存款差错金额(元) --%>
				<th class="sort-column saveErrorMoney"><spring:message
						code="door.checkCash.saveErrorMoney" />
					<spring:message code="common.units.yuan.alone" /></th>
				<%-- 实际存款金额(元) --%>
				<th class="sort-column trueSumMoney"><spring:message
						code="door.checkCash.trueSumMoney" />
					<spring:message code="common.units.yuan.alone" /></th>
				<%-- 实际金额(元) --%>
				<th class="sort-column a.check_amount"><spring:message
						code="door.checkCash.realMoney" />
					<spring:message code="common.units.yuan.alone" /></th>
				<%-- 差额(元) --%>
				<th class="sort-column a.diff_amount"><spring:message
						code="door.public.diffMoney" />
					<spring:message code="common.units.yuan.alone" /></th>
				<%-- 总笔数 --%>
				<th class="sort-column boxCount"><spring:message
						code="door.checkCash.sumCount" /></th>
				<%-- 未拆数 --%>
				<th class="sort-column noBoxCount"><spring:message
						code="door.checkCash.noCount" /></th>
				<%-- 清分人员 --%>
				<th class="sort-column a.create_name"><spring:message
						code="door.checkCash.clearMan" /></th>
				<%-- 登记时间 --%>
				<th class="sort-column a.reg_date"><spring:message
						code="door.checkCash.registerTime" /></th>
				<%-- GJ start --%>
				<%-- 款袋使用时间 --%>
				<th  style="padding-right:48px"><spring:message code="door.doorOrder.packNumUseTime" /></th>
				<%-- GJ end  --%>
				<%-- gzd 2020-04-14  添加业务备注字段 --%>
				<%-- 业务备注 --%>
				<th ><spring:message code="door.historyChange.remarks" /></th>
				<%-- 操作 --%>
				<th><spring:message code='common.operation' /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="checkCashMain"
				varStatus="status">
				<tr style="background-color:${checkCashMain.errorStatus eq 5 ? '#bfdcae' : ''};">
					<td>${status.index + 1}</td>
					<td><a
						href="${ctx}/collection/v03/checkCash/view?id=${checkCashMain.outNo}&type=1">${checkCashMain.outNo}</a>
					</td>
					<td><a href="${ctx}/collection/v03/checkCash/checkCashDetail?bagNo=${checkCashMain.rfid}&orderId=${checkCashMain.outNo}">${checkCashMain.rfid}</a></td>
					<td>${checkCashMain.custName}</td>
					<td style="text-align: right;"><fmt:formatNumber
							value="${checkCashMain.inputAmount}" type="currency"
							pattern="#,##0.00" /></td>
					<td style="text-align: right;"><fmt:formatNumber
							value="${checkCashMain.saveErrorMoney}" type="currency"
							pattern="#,##0.00" /></td>
					<td style="text-align: right;"><fmt:formatNumber
							value="${checkCashMain.trueSumMoney}" type="currency"
							pattern="#,##0.00" /></td>
					<td style="text-align: right;"><fmt:formatNumber
							value="${checkCashMain.checkAmount}" type="currency"
							pattern="#,##0.00" /></td>
					<td style="text-align: right;"><fmt:formatNumber
							value="${checkCashMain.diffAmount}" type="currency"
							pattern="#,##0.00" /></td>
					<td>${checkCashMain.boxCount}</td>
					<td>${checkCashMain.noBoxCount}</td>
					<td>${checkCashMain.updateBy.name}</td>
					<td><fmt:formatDate value="${checkCashMain.updateDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<%-- 钞袋使用时间  时间段展示  ZXK start--%>
			 	    <td>
				 	    <c:if test="${checkCashMain.lastTime ne null}">
				 	       <fmt:formatDate value="${checkCashMain.lastTime}"
								pattern="yyyy-MM-dd HH:mm" />~<br/>
				 	       <fmt:formatDate value="${checkCashMain.thisTime}"
								pattern="yyyy-MM-dd HH:mm" />
				 	    </c:if>
			 	    </td>
			 	    
			 	    <%-- 钞袋使用时间   end--%>
					<%-- GJ start --%>
					<%-- 钞袋使用时间  zxk start--%>
					<%-- <c:choose>
					    值为'0'时显示 '不足1分钟'
						<c:when test="${checkCashMain.packNumUseTime eq '0'}">
							<td><spring:message code="door.public.lessMinute" /></td>
						</c:when>
						 值为null 时不显示
						<c:when test="${checkCashMain.packNumUseTime eq null}">
							<td>${checkCashMain.packNumUseTime}</td>
						</c:when>
						 有值且不为'0' 正常显示
						<c:otherwise>
							<td>${checkCashMain.packNumUseTime}<spring:message code="door.public.minute" /></td>
						</c:otherwise>
					</c:choose> --%>
					<%-- 钞袋使用时间  end --%>
					<%-- gzd 2020-04-14 添加业务备注字段 --%>
					<td>${checkCashMain.remarks}</td>
					<%-- GJ end --%>
					<td>
						<shiro:hasPermission name="check:checkCash:edit">
							<c:if test="${checkCashMain.noBoxCount !=0}">
								<!-- 修改 -->
								<span style='width: 25px; display: inline-block;'> <a
									href="${ctx}/collection/v03/checkCash/form?id=${checkCashMain.outNo}"
									title="<spring:message code='common.modify'/>"> <i
										class="fa fa-edit text-green  fa-lg"></i>
								</a>
								</span>
								<!-- 删除 -->
								<c:if test="${checkCashMain.dataFlag eq '0'}">
									<span style='width: 25px; display: inline-block;'>
											<a id="aDelete"
												href="${ctx}/collection/v03/checkCash/delete?id=${checkCashMain.outNo}"
												onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)"
												title="<spring:message code='common.delete'/>"> <i
												class="fa fa-trash-o text-red  fa-lg"></i>
											</a>
									</span>
								</c:if>
								<!-- 快速拆箱 gzd 2020-05-07 -->
								<span style='width: 25px; display: inline-block;'>
									<a id="cx" href="#" title="拆箱" onclick="cx('${checkCashMain.outNo}','');return false;">
										<i class="fa fa-cube text-red  fa-lg"></i>
									</a>
								</span>
							</c:if>
							<c:if test="${checkCashMain.noBoxCount ==0}">
								<span style='width: 25px; display: inline-block;'> <%-- 冲正 --%>
									<a id="aReverse" href="#"
									onclick="authorize('${checkCashMain.outNo}');return false;"
									title="<spring:message code='common.reverse' />"> <i
										class="fa fa-exchange"></i>
								</a>
								</span>
							</c:if>
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