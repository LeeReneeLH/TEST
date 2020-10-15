<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>
<%@page import="com.coffer.core.modules.sys.utils.UserUtils"%>
<html>
<head>
	<!-- 存款管理 -->
	<title><spring:message code="door.deposit.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		// 验证   gzd 2020-04-16
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
					window.top.clickSelect();
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
		});
		
		// 提交按钮事件   gzd 2020-04-16
		$(document).ready(function(){
			$("#btnSubmit").click(function(){
				window.top.clickSelect();
				$("#searchForm").submit();
			});
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }		
		
		// 删除处理
		function rowDelete(v_doorId) {
			var url = "${ctx}/door/doorOrderInfo/delete?id=" + v_doorId ;
			$("#inputForm").attr("action", url);
			$("#inputForm").submit();
		}
		
		//冲正授权
		function authorize(id){
			var content = "iframe:${ctx}/collection/v03/checkCash/authorizeDetail";
			//授权操作
			top.$.jBox.open(
					content,
					"<spring:message code='message.I7239' />", 470, 220, {
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
								var authorizer = contentWindow.document.getElementById("authorizer").value;
								if(authorizer == null || authorizer == ""){
									//请输入用户名
									alertx("<spring:message code='message.I7218' />");
									return false;
								}
								//密码
								var authorizeBy = contentWindow.document.getElementById("authorizeBy").value;
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
									$("#inputForm").attr("action", "${ctx}/weChat/v03/doorOrderInfo/reverse?id="+id);
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
			});
		}
		
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
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 存款列表 -->
		<li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/weChat/v03/doorOrderInfo/"><spring:message code="door.doorOrder.deposit" /><spring:message code="common.list" /></a></li>
		<!-- 存款登记-->
		<shiro:hasPermission name="door:doorOrderInfo:edit"><li><a href="${ctx}/weChat/v03/doorOrderInfo/form"><spring:message code="door.doorOrder.deposit" /><spring:message code="common.register" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="inputForm" modelAttribute="doorOrderInfo" action="" method="post" class="form-horizontal">
	</form:form>
	
	<c:set var="user" value="<%=UserUtils.getUser()%>" />
		<form:form id="searchForm" modelAttribute="doorOrderInfo" action="${ctx}/weChat/v03/doorOrderInfo/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
			<div class="row search-flex">
			    <div>
				<%--款袋编号--%>
				<label><spring:message code="door.doorOrder.packNum" />：</label>
				<form:input path="rfid" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%--凭条号 --%>
				<div>
				<label><spring:message code="door.checkCash.tickertape" />：</label>
				<form:input path="tickertape" htmlEscape="false" maxlength="40" class="input-small"/>
				</div>
				<%-- 门店 --%>
				<div>
				<label><spring:message code="door.public.cust" /> ：</label>
				<sys:treeselect id="doorName" name="doorId"
						value="${doorOrderInfo.doorId}" labelName="doorName"
						labelValue="${doorOrderInfo.doorName}" title="<spring:message code='door.public.cust' />"
						url="/sys/office/treeData" cssClass="input-small"
						notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
					    isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
				</div>
				<%--机具 --%>
			    <div>
			    <label><spring:message code="door.equipment.equipment" />：</label>
                   <form:input path="equipmentName" htmlEscape="false" maxlength="32" class="input-small"/>
                   </div>
                   <%--存款人 --%>
                   <div>
				<label><spring:message code="door.public.createBy" />：</label>
				<form:input path="updateName" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%-- 状态 --%>
				<div>
				<label><spring:message code="common.status" />：</label>
				<form:select path="status" class="input-medium" id ="selectStatus">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getFilterDictList('sys_clear_type','true','0,2,3,4')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				</div>
				<%-- 申请方式--%>
				<div>
				<label><spring:message code="door.doorOrder.method" />：</label>
				<form:select path="method" class="input-medium">
					<option value=""><spring:message code="common.select" /></option>
					<form:options items="${fns:getDictList('door_method_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				</div>
				<%-- 开始日期 --%>
				<div>
				<label><spring:message code="door.checkCash.registerDate" />：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorOrderInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				</div>
				<%-- 结束日期 --%>
				<div>
				<label>~</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorOrderInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				</div>
				<%-- 业务备注  gzd 2020-04-16 --%>
				<div>
				<label><spring:message code="door.historyChange.remarks" />：</label>
				<form:input path="remarks" htmlEscape="false" maxlength="32" class="input-small"/>
				</div>
				<%-- 起始金额  gzd 2020-04-16 --%>
				<div>
					<label><spring:message code="common.totalMoney" />：</label>
					<input id="beforeAmount"  name="beforeAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
						value="${doorOrderInfo.beforeAmount}" placeholder="0.00" />
				</div>
				<%-- 结束金额  gzd 2020-04-16 --%>
				<div>
					<label>~</label>
					<input id="afterAmount" name="afterAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
						value="${doorOrderInfo.afterAmount}" placeholder="9999999999.99" />
				</div>
				<%-- 查询 --%>
				&nbsp;&nbsp;
				<div>
				<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.search'/>"/>
			    </div>
			</div>
		</form:form>
	</div>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 序号 --%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 存款单号 --%>
				<%--<th class="sort-column a.order_id"><spring:message code="door.doorOrder.codeNo" /></th>--%>
				<%-- 凭条号 --%>
				<!-- GJ start -->
				<%-- <c:if test="${doorOrderInfo.tickertape != null && doorOrderInfo.tickertape != ''}">
					<th class="sort-column a.rfid"><spring:message code="door.checkCash.tickertape" /></th>
				</c:if> --%>
				<!-- GJ end -->
				<%-- 包号 --%>
				<th class="sort-column a.rfid"><spring:message code="door.doorOrder.packNum" /></th>
				<%-- 门店 --%>
				<th class="sort-column a.door_name"><spring:message code="door.public.cust" /></th>
				<%-- 机具 --%>
				<th class="sort-column a.equipment_id"><spring:message code="door.equipment.equipment" /></th>
				<%-- 总金额 --%>
				<th class="sort-column a.amount"><spring:message code="common.totalMoney" /></th>
				<%-- 业务备注 --%>
				<th class="sort-column doe.remarks"><spring:message code="door.historyChange.remarks" /></th>
				<%-- 登记人 --%>
				<%--<th class="sort-column d.name"><spring:message code="common.registerPerson" /></th>--%>
				<%-- 登记时间 --%>
				<th class="sort-column a.create_date"><spring:message code="common.registerTime" /></th>
				<%-- 更新人 --%>
				<th class="sort-column a.update_by"><spring:message code="common.updatePerson" /></th>
				<%-- 更新时间 --%>
				<th class="sort-column a.update_date"><spring:message code="common.updateDateTime" /></th>
				<%-- 状态 --%>
				<th class="sort-column a.status"><spring:message code="common.status" /></th>	
				<%-- 申请方式 --%>
				<th class="sort-column a.method"><spring:message code="door.doorOrder.method" /></th>
				<%-- 操作 --%>
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="doorOrderInfo" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
			 	<%--<td>
			 		<a href="${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=${doorOrderInfo.id}">${doorOrderInfo.orderId}</a>
			 	</td>--%>
			 	<!-- GJ start -->
			 	<%-- <c:if test="${doorOrderInfo.tickertape != null && doorOrderInfo.tickertape != ''}">
			 		<td>${doorOrderInfo.tickertape}</td>
			 	</c:if> --%>
			 	<!-- GJ end -->
				<td>
					<a href="${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=${doorOrderInfo.id}">${doorOrderInfo.rfid}</a>
				</td>
			    <td>${doorOrderInfo.doorName}</td>
			    <td>${doorOrderInfo.equipmentName}</td>
			    <!-- GJ start -->
			    <%-- <c:if test="${doorOrderInfo.tickertapeAmount != null && doorOrderInfo.tickertapeAmount != ''}">
			    	<td>${doorOrderInfo.tickertapeAmount}</td>
			    </c:if>
			    <c:if test="${doorOrderInfo.tickertapeAmount == null || doorOrderInfo.tickertapeAmount == ''}"> --%>
			    	<td style="text-align:right;"><fmt:formatNumber value="${doorOrderInfo.amount}" type="currency" pattern="#,##0.00"/></td>
			    <%-- </c:if> --%>
			 	<!-- GJ end -->
			 	<c:if test="${doorOrderInfo.method eq '2'}">
				 <td>${doorOrderInfo.remarks}</td>
				</c:if>
			 	<c:if test="${doorOrderInfo.method ne '2'}">
				 <td style="text-align:center;">-</td>
				</c:if>
				<%--<td >${doorOrderInfo.createName}</td>--%>
				<td><fmt:formatDate value="${doorOrderInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${doorOrderInfo.updateName}</td>
				<td><fmt:formatDate value="${doorOrderInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${fns:getDictLabelWithCss(doorOrderInfo.status, 'sys_clear_type', '未命名',true)}</td>
			    <td>${fns:getDictLabel(doorOrderInfo.method, 'door_method_type', '')}</td>
				<td>
				<!-- 中心的财务，业务人员可冲正 -->
				<c:if test="${fns:getUser().getOffice().type=='6'}">
				<c:if test="${fns:getUser().userType=='21'||fns:getUser().userType=='22'}">
					    <!-- PC端，微信端可以冲正-->
						<c:if test="${doorOrderInfo.method eq '2' or doorOrderInfo.method eq '1'}">
						<!-- 状态为已收回，已清分的可见冲正按钮-->
						   <c:if test="${doorOrderInfo.status eq '2' or doorOrderInfo.status eq '3'}">
						   <shiro:hasPermission name="door:doorOrderInfo:reverse">
						   <span style='width:25px;display:inline-block;'> 
								<%-- 冲正 --%>
								<a id="aReverse" href="#"
									onclick="authorize('${doorOrderInfo.id}');return false;" title="<spring:message code='common.reverse' />">
									<i class="fa fa-exchange"></i>
								</a>
							</span>
						</shiro:hasPermission>
						   </c:if>
						</c:if>
				</c:if>
				</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>	
	<div class="pagination">${page}</div>
</body>
</html>