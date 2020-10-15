<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 差错管理 -->
	<title><spring:message code="clear.clErrorInfo.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			/* 追加导出  修改人:sg 修改日期:2017-11-21 begin */
			//点击导出按钮
			$("#btnExport").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/clErrorInfo/exportClErrorInfoReport");
				$("#searchForm").submit();
			});
			
			//点击查看按钮
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/clErrorInfo/");
				$("#searchForm").submit();
			});
			/* end */
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		// 打印明细
		function print(errorNo){
			var content = "iframe:${ctx}/clear/v03/clErrorInfo/printClErrorInfo?errorNo=" + errorNo;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 900, 700, {
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
		
		/* 追加冲正授权修改人：qipeihong 修改时间：2017-9-18 begin */
		function authorize(inNo) {
			if (!checkTellerAccounts(inNo)) {
				return false;
			}
			if (!checkDate(inNo)) {
				return false;
			}
			var url = "${ctx}/clear/v03/clErrorInfo/reverse";
			var refreshUrl = "${ctx}/clear/v03/clErrorInfo/reverseList";
			var ctx  = "${ctx}";
			reverseAuthorize(inNo,url,refreshUrl,ctx);
		}
		
		//验证时间
		function checkDate(inNo){
			var boo=true;
			$.ajax({
				url : "${ctx}/clear/v03/clErrorInfo/checkDate",
				type : 'POST',
				async: false,
				dataType : 'json',
				data : {
					errorNo : inNo,
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
		function displayAuthorizeReason(errorNo) {
			var content = "iframe:${ctx}/clear/v03/clErrorInfo/displayAuthorizeReason?errorNo="+errorNo;
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
		
		/* end */
		
		//验证柜员帐
		function checkTellerAccounts(inNo){
			var boo=true;
			$.ajax({
				url : "${ctx}/clear/v03/clErrorInfo/checkTellerAccounts",
				type : 'POST',
				async: false,
				dataType : 'json',
				data : {
					errorNo : inNo,
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
		<!-- 差错处理列表 -->
		<li class="active"><a href="${ctx}/clear/v03/clErrorInfo/"><spring:message code="clear.clErrorInfo.list" /></a></li>
		<!-- 差错处理登记 -->
		<shiro:hasPermission name="clear:v03:clErrorInfo:edit"><li><a href="${ctx}/clear/v03/clErrorInfo/form"><spring:message code="clear.clErrorInfo.register" /></a></li></shiro:hasPermission>
	</ul>
	<form:form id="updateForm" modelAttribute="updateParam" action="" method="post" >
		
		</form:form>
	<form:form id="searchForm" modelAttribute="clErrorInfo" action="${ctx}/clear/v03/clErrorInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<div class="row">
			<!--第一行 -->
			<div class="" style="margin-top:5px">
				<!-- 登记单号-->
				<label><spring:message code="clear.clErrorInfo.registerNo" />：</label>
				<form:input path="errorNo" htmlEscape="false" maxlength="20" class=""/>
				<!-- 客户名称 -->
				<label><spring:message code="clear.public.custName" />：</label>
				<form:select path="custNo" id="custNo"
					class="input-large required" disabled="disabled">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('1,3',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
				<!-- 开始日期 -->
				<label><spring:message code="common.startDate" />：</label>
				<!--  清分属性去除 wzj 2017-11-15 begin -->
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-large Wdate createTime" 
					value="<fmt:formatDate value="${clErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
				<!-- end -->
				<!-- 结束日期 -->
				<label><spring:message code="common.endDate" />：</label>
				<!--  清分属性去除 wzj 2017-11-15 begin -->
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-large Wdate createTime" 
					value="<fmt:formatDate value="${clErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
				<!-- 增加清分机构 wzj 2017-11-27 begin -->
				<label><spring:message code="common.agent.office" />：</label>
					<sys:treeselect id="office" name="office.id"
						value="${clErrorInfo.office.id}" labelName="office.name"
						labelValue="${clErrorInfo.office.name}" title="清分中心"
						url="/sys/office/treeData" notAllowSelectRoot="false"
						notAllowSelectParent="false" allowClear="true" type="6"
						cssClass="input-medium" />
				<!-- end -->
					&nbsp;
				<!-- end -->
			</div>
			<!--第二行 -->
			<div class="span12" style="margin-top:10px;width:1500px;">
				<!-- 类别-->
				<label style="margin-left:22px"><spring:message code='common.classification'/>：</label>
				<form:select path="errorType" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getDictList('clear_error_type')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
				<!-- 业务类型 -->
				<label><spring:message code='clear.task.business.type'/>：</label>
				<form:select path="busType" class="input-large">		
					<form:option value=""><spring:message code="common.select" /></form:option>
						<form:options items="${fns:getFilterDictList('clear_businesstype',true,'08,09')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<!-- 面值-->
				<label style="margin-left:56px"><spring:message code='common.denomination'/>：</label>
				<form:select path="denomination" id="denomination" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${sto:getGoodDictListWithFg('cnypden')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>&nbsp;&nbsp;
				<!-- 查询 -->
				<input id="btnSubmit" class="btn btn-primary" type=button value="<spring:message code='common.search'/>" />
				<!-- 追加到处按钮  修改人:sg 修改日期:2017-11-21 begin -->
				<!-- 导出 -->
				<input id="btnExport" class="btn btn-red" type="button" value="<spring:message code='common.export'/>"/>
				<!-- end -->
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 登记单号 -->
				<th><spring:message code='clear.clErrorInfo.registerNo'/></th>
				<!-- 客户名称 -->
				<th><spring:message code="clear.public.custName"/></th>
				<!-- 登记类型 -->
				<th><spring:message code="clear.clErrorInfo.registerType"/></th>
				<!-- 业务类型 -->
				<th><spring:message code="clear.task.business.type"/></th>
				<!-- 类别 -->
				<th><spring:message code="common.classification"/></th>
				<!-- 币种 -->
				<th style="width:50px"><spring:message code="common.currency"/></th>
				<!-- 面值 -->
				<th style="width:50px"><spring:message code="common.denomination"/></th>
				<!-- 数量 -->
				<th><spring:message code="clear.clErrorInfo.number"/></th>
				<!-- 差错金额 -->
				<th><spring:message code="clear.clErrorInfo.amountError"/></th>
				<!-- 清分人 -->
				<th><spring:message code="clear.clErrorInfo.clearMan"/></th>
				<!-- 登记人 -->
				<th><spring:message code="clear.public.registerName"/></th>
				<!-- 登记时间 -->
				<th><spring:message code="clear.register.date"/></th>
				<!-- 发现时间 -->
				<th><spring:message code="clear.clErrorInfo.findDate"/></th>
				<!-- 腰条名章 -->
				<th><spring:message code="clear.clErrorInfo.articleWaist"/></th>
				<!-- 差错管理员 -->
				<th><spring:message code="clear.clErrorInfo.errorAdmin"/></th>
				<!-- 确认人 -->
				<th><spring:message code="clear.public.makesureManName"/></th>
				<!-- 状态 -->
				<th><spring:message code="common.status"/></th>
				<!-- 增加机构显示 wzj 2017-11-27 begin -->
				<th><spring:message code="clear.orderClear.office"/></th>
				<!-- end -->
				<!-- 操作 -->
				<shiro:hasPermission name="clear:v03:clErrorInfo:edit"><th style="width:60px"><spring:message code='common.operation'/></th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="clErrorInfo">
				<tr>
					<td><a href="${ctx}/clear/v03/clErrorInfo/view?errorNo=${clErrorInfo.errorNo}" title="<spring:message code='common.view'/>">${clErrorInfo.errorNo}</a></td>
					<td>${clErrorInfo.custName}</td>
					<td>${fns:getDictLabel(clErrorInfo.subErrorType,'clear_subError_type',"")}</td>
					<td>${fns:getDictLabel(clErrorInfo.busType,'clear_businesstype',"")}</td>
					<td>${fns:getDictLabel(clErrorInfo.errorType,'clear_error_type',"")}</td>
					<td>${fns:getDictLabel(clErrorInfo.currency,'money_currency',"")}</td>
					<td>${sto:getGoodDictLabel(clErrorInfo.denomination, 'cnypden', "")}</td>
					<td>${clErrorInfo.count}</td>
					<td><fmt:formatNumber value="${clErrorInfo.errorMoney}" pattern="#,##0.00#" /></td>
					<td>${clErrorInfo.clearManName}</td>
					<td>${clErrorInfo.createName}</td>
					<td><fmt:formatDate value="${clErrorInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td><fmt:formatDate value="${clErrorInfo.findTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${clErrorInfo.stripChap}</td>
					<td>${clErrorInfo.checkManName}</td>
					<td>${clErrorInfo.makesureManName}</td>
					<c:choose>
						<c:when test="${clErrorInfo.status == '2'}">
							<td>
								<a href="#" onclick="displayAuthorizeReason('${clErrorInfo.errorNo}');javascript:return false;">
									${fns:getDictLabel(clErrorInfo.status,'cl_status_type',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(clErrorInfo.status,'cl_status_type',"")}</td>
						</c:otherwise>
					</c:choose>
					<!-- 增加机构 wzj 2017-11-27 begin -->
					<td>
					${clErrorInfo.office.name} 
					</td>
					<!-- end -->
					<td>
						<span style='width:20px;display:inline-block;'> 
							<shiro:hasPermission name="clear:v03:clErrorInfo:reverse">
								<!-- 冲正 -->
								<c:if test="${'1' eq clErrorInfo.status}">
									<a href="#"  onclick="authorize('${clErrorInfo.errorNo}')" title="<spring:message code='common.reverse'/>"><i class="fa fa-exchange"></i></a>
								</c:if>
							</shiro:hasPermission>
						</span>
						<span style='width:15px;display:inline-block;'>
							<shiro:hasPermission name="clear:v03:clErrorInfo:print"> 
								<!-- 打印 -->
								<a href="###" onclick="print('${clErrorInfo.errorNo}')" title="<spring:message code='common.print'/>"><i class="fa fa-print text-yellow fa-lg"></i></a>
							</shiro:hasPermission>
						</span>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>