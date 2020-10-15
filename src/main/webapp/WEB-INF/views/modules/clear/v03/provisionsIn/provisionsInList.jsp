<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 备付金管理 -->
	<title><spring:message code="clear.provision.title" /></title>
	<meta name="decorator" content="default" />
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript"src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(
				function() {
					$("#btnSubmit").click(
							function() {
								$("#searchForm").attr("action",
									"${ctx}/clear/v03/provisionsIn/list");
								$("#searchForm").submit();
							});
				});
		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}
		// 打印明细
		function printDetail(inNo) {
			var content = "iframe:${ctx}/clear/v03/provisionsIn/printProvisionsInDetail?inNo=" + inNo;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 900, 600, {
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
								return false;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
					});
		}
		
		/* 追加冲正授权修改人：qipeihong 修改时间：2017-9-18 begin */
		function authorize(inNo) {
			if (!checkTellerAccounts(inNo)) {
				return false;
			}
			if (!checkDate(inNo)) {
				return false;
			}
			var url = "${ctx}/clear/v03/provisionsIn/reverse";
			var refreshUrl = "${ctx}/clear/v03/provisionsIn/reverseList";
			var ctx  = "${ctx}";
			reverseAuthorize(inNo,url,refreshUrl,ctx);
		}
		
		//验证时间
		function checkDate(inNo){
			var boo=true;
			$.ajax({
				url : "${ctx}/clear/v03/bankPayCtrl/checkDate",
				type : 'POST',
				async: false,
				dataType : 'json',
				data : {
					inNo : inNo,
				},
				success : function(serverResult, textStatus) {
					if (serverResult.result=="success") {
					}
					if (serverResult.result=="error") {
						alertx("无法冲正非本日业务流水！");
						boo=false;
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
			return boo;
		}
		
		// 显示冲正原因
		function displayAuthorizeReason(inNo) {
			var content = "iframe:${ctx}/clear/v03/provisionsIn/displayAuthorizeReason?inNo="+inNo;
			top.$.jBox.open(
					content,
					// 冲正原因
					"<spring:message code='clear.public.reverseReason' />", 480, 420, {
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
		}
		
		//验证柜员帐
		function checkTellerAccounts(inNo){
			var boo=true;
			$.ajax({
				url : "${ctx}/clear/v03/provisionsIn/checkTellerAccounts",
				type : 'POST',
				async: false,
				dataType : 'json',
				data : {
					inNo : inNo,
				},
				success : function(serverResult, textStatus) {
					if (serverResult.result=="success") {
					}
					if (serverResult.result=="error") {
						alertx("交接人"+serverResult.transManName+"余额不足！");
						boo=false;
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
			return boo;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	<!-- 修复点击跳转页面 -->
		<!-- 备付金交入列表 -->
		<li class="active"><a href="${ctx}/clear/v03/provisionsIn/list"><spring:message code="clear.provisionIn.list" /></a></li>
		<!-- 备付金交入登记 -->
		<shiro:hasPermission name="v03:provisionsIn:edit"><li><a href="${ctx}/clear/v03/provisionsIn/form"><spring:message code="clear.provisionIn.register" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="updateForm" modelAttribute="updateParam" action="" method="post" >
		
		</form:form>
	<form:form id="searchForm" modelAttribute="bankPayInfo" action="${ctx}/clear/v03/provisionsIn/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<div class="row">
		<div class="span14" style="margin-top:5px">
			<!-- 客户名称 -->
			<label><spring:message code="clear.public.custName" />：</label>
			<form:select path="rOffice.id" id="custNo"
				class="input-large required" disabled="disabled">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<form:options items="${sto:getStoCustList('1,3',false)}"
					itemLabel="name" itemValue="id" htmlEscape="false" />
			</form:select>
			&nbsp;&nbsp;		
			<!-- 登记日期 -->
			<label><spring:message code="common.startDate" />：</label>
			<!--  清分属性去除 wzj 2017-11-15 begin -->
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="<fmt:formatDate value="${bankPayInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
			<!-- end -->
			<!-- 结束日期 -->
			<label><spring:message code="common.endDate" />：</label>
			<!--  清分属性去除 wzj 2017-11-15 begin -->
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value="<fmt:formatDate value="${bankPayInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
			<!-- 增加清分机构 wzj 2017-11-27 begin -->
			<label><spring:message code="common.agent.office" />：</label>
				<sys:treeselect id="office" name="office.id"
					value="${bankPayInfo.office.id}" labelName="office.name"
					labelValue="${bankPayInfo.office.name}" title="清分中心"
					url="/sys/office/treeData" notAllowSelectRoot="false"
					notAllowSelectParent="false" allowClear="true" type="6"
					cssClass="input-medium" />
			<!-- end -->
			&nbsp;&nbsp;&nbsp;
			<!-- end -->
			<!-- 查询 -->
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
		</div>
	</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 交入单号 -->
				<th class="sort-column a.in_no"><spring:message code="clear.provisionIn.inNo"/></th>
				<!-- 客户名称 -->
				<th class="sort-column a.cust_name"><spring:message code="clear.public.custName"/></th>
				<!-- 交入金额 -->
				<th><spring:message code="clear.provisionIn.inAmount"/></th>
				<!-- 银行交接人员A -->
				<th class="sort-column a.bank_man_name_a"><spring:message code="clear.public.bankManNameA"/></th>
				<!-- 银行交接人员B -->
				<th class="sort-column a.bank_man_name_b"><spring:message code="clear.public.bankManNameB"/></th>
				<!-- 交接人 -->
				<th><spring:message code="clear.public.transManName"/></th>
				<!-- 复核人 -->
				<th><spring:message code="clear.public.checkManName"/></th>
				<!-- 状态 -->
				<th class="sort-column a.status"><spring:message code="common.status"/></th>
				<!-- 确认人 -->
				<th><spring:message code="clear.public.makesureManName"/></th>
				<!-- 登记时间 -->
				<th class="sort-column a.create_date"><spring:message code="common.registerTime"/></th>
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 操作（冲正/打印） -->
				<th><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bankPayInfo">
			<tr>
					<!-- 交入单号 -->
					<td><a href="${ctx}/clear/v03/provisionsIn/view?inNo=${bankPayInfo.inNo}">${bankPayInfo.inNo}</a></td>
					<!-- 客户名称 -->
					<td>${bankPayInfo.rOffice.name}</td>
					<!-- 交入金额 -->
					<td  style="text-align:right;"><fmt:formatNumber value="${bankPayInfo.inAmount}" pattern="#,##0.00#" /></td>
					<!-- 银行交接人员A -->
					<td>${bankPayInfo.bankManNameA}</td>
					<!-- 银行交接人员B-->
					<td>${bankPayInfo.bankManNameB}</td>
					<!-- 交接人 -->
					<td>${bankPayInfo.transManName}</td>
					<!-- 复核人 -->
					<td>${bankPayInfo.checkManName}</td>
					<!-- 状态 -->
						<!-- 状态 -->
					<c:choose>
						<c:when test="${bankPayInfo.status == '2'}">
							<td>
								<a href="#" onclick="displayAuthorizeReason('${bankPayInfo.inNo}');javascript:return false;">
									${fns:getDictLabel(bankPayInfo.status,'cl_status_type',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(bankPayInfo.status,'cl_status_type',"")}</td>
						</c:otherwise>
					</c:choose>
					<!-- 确认人 -->
					<td>${bankPayInfo.makesureManName}</td>
					<!-- 登记时间 -->
					<td><fmt:formatDate value="${bankPayInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<!-- 增加机构 wzj 2017-11-27 begin -->
					<td>
					${bankPayInfo.office.name} 
					</td>
				<!-- end -->
					<!-- 操作 -->
					<td>
					<span style='width:30px;display:inline-block;'> 
						<shiro:hasAnyPermissions name="clear:provisionsIn:reverse">
							<c:if test="${'1' eq bankPayInfo.status}"> 
							<!-- 冲正 -->
							<a id="aReverse" href="#"
								onclick="authorize('${bankPayInfo.inNo}')" title="<spring:message code='common.reverse'/>">
								<i class="fa fa-exchange"></i>
							</a>
							</c:if> 
						</shiro:hasAnyPermissions>
					</span>

					<span style='width:20px;display:inline-block;'> 
						<shiro:hasAnyPermissions name="clear:provisionsIn:print">
							<!-- 打印 -->
							<a href="#" onclick="printDetail('${bankPayInfo.inNo}');" title="<spring:message code='common.print'/>">
								<i class="fa fa-print text-yellow fa-lg"></i>
							</a>
						</shiro:hasAnyPermissions>
					</span>
					</td>
				</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>