<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 代理上缴列表 -->
	<title><spring:message code="clear.agencyPay.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", "${ctx}/clear/v03/agencyPayCtrl/list");
				$("#searchForm").submit();
			});
		});
		
		// 打印明细
		function printDetail(outNo){
			var content = "iframe:${ctx}/clear/v03/agencyPayCtrl/printAgencyPayDetail?outNo=" + outNo;
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
									"overflow-y", "hidden");
						}
					});
		}
		
		/* 追加冲正授权修改人：qipeihong 修改时间：2017-9-18 begin */
		function authorize(inNo) {
			if (!checkDate(inNo)) {
				return false;
			}
			var url = "${ctx}/clear/v03/agencyPayCtrl/reverse";
			var refreshUrl = "${ctx}/clear/v03/agencyPayCtrl/reverseList";
			var ctx  = "${ctx}";
			reverseAuthorize(inNo,url,refreshUrl,ctx);
		}
		/* end */
		
		//验证时间
		function checkDate(inNo){
			var boo=true;
			$.ajax({
				url : "${ctx}/clear/v03/bankGetCtrl/checkDate",
				type : 'POST',
				async: false,
				dataType : 'json',
				data : {
					outNo : inNo,
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
		function displayAuthorizeReason(outNo) {
			var content = "iframe:${ctx}/clear/v03/agencyPayCtrl/displayAuthorizeReason?outNo="+outNo;
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	<!-- onclick="javascript:return false; -->
		<!-- 代理上缴列表 -->
		<li class="active"><a href="${ctx}/clear/v03/agencyPayCtrl/list "><spring:message code="clear.agencyPay.title" /></a></li>
		<shiro:hasPermission name="clear:agencyPayCtrl:add">
		<!-- 代理上缴登记 -->
		<li><a href="${ctx}/clear/v03/agencyPayCtrl/form"><spring:message code="clear.agencyPay.register" /></a></li>
		</shiro:hasPermission>
	</ul>
	<div class="row">
	<form:form id="updateForm" modelAttribute="updateParam" action="" method="post" >
		
		</form:form>
		<form:form id="searchForm" modelAttribute="agencyPayMain" action="" method="post" class="breadcrumb form-search">
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
						<form:options items="${sto:getStoCustList('3',false)}"
							itemLabel="name" itemValue="id" htmlEscape="false" />
					</form:select>
					&nbsp;&nbsp;
					<!-- 登记日期 -->
					<label><spring:message code="common.startDate" />：</label>
					<!--  清分属性去除 wzj 2017-11-15 begin -->
					<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
						   value="<fmt:formatDate value="${agencyPayMain.createTimeStart}" pattern="yyyy-MM-dd"/>" 
						   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
					<!-- end -->
					<!-- 结束日期 -->
					<label><spring:message code="common.endDate" />：</label>
					<!--  清分属性去除 wzj 2017-11-15 begin -->
					<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					       value="<fmt:formatDate value="${agencyPayMain.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>
					<!-- 增加清分机构 wzj 2017-11-27 begin -->
					<label><spring:message code="common.agent.office" />：</label>
					<sys:treeselect id="office" name="office.id"
						value="${agencyPayMain.office.id}" labelName="office.name"
						labelValue="${agencyPayMain.office.name}" title="清分中心"
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
	</div>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 上缴单号 -->
				<th class="sort-column a.out_no"><spring:message code="clear.agencyPay.outNo"/></th>
				<!-- 客户名称 -->
				<th class="sort-column a.cust_name"><spring:message code="clear.public.custName"/></th>
				<!-- 出库金额 -->
				<th class="sort-column a.out_amount"><spring:message code="clear.public.outAmount"/></th>
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
		<c:forEach items="${page.list}" var="agencyPayMain" varStatus="statusIndex">
			<tr>
				<!-- 上缴单号 -->
				<td><a href="${ctx}/clear/v03/agencyPayCtrl/view?outNo=${agencyPayMain.outNo}">
					${agencyPayMain.outNo}
				</a></td>
				<!-- 客户名称 -->
				<td>
					${agencyPayMain.rOffice.name}
				</td>
				<!-- 出库金额 -->
				<td  style="text-align:right;"><fmt:formatNumber value="${agencyPayMain.outAmount}" pattern="#,##0.00#" /></td>
				<!-- 交接人 -->
				<td>${agencyPayMain.transManName}</td>			
				<!-- 复核人 -->
				<td>${agencyPayMain.checkManName}</td>
				<!-- 状态 -->
				<c:choose>
						<c:when test="${agencyPayMain.status == '2'}">
							<td>
								<a href="#" onclick="displayAuthorizeReason('${agencyPayMain.outNo}');javascript:return false;">
									${fns:getDictLabel(agencyPayMain.status,'cl_status_type',"")}
								</a>
							</td>
						</c:when>
						<c:otherwise>
							<td>${fns:getDictLabel(agencyPayMain.status,'cl_status_type',"")}</td>
						</c:otherwise>
					</c:choose>
				<!-- 确认人 -->
				<td>${agencyPayMain.makesureManName}</td>
				<!-- 登记时间 -->
				<td><fmt:formatDate value="${agencyPayMain.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<!-- 增加机构 wzj 2017-11-27 begin -->
					<td>
					${agencyPayMain.office.name} 
					</td>
				<!-- end -->
				<!-- 操作 -->
				<td>
				<span style='width:30px;display:inline-block;'> 
				<shiro:hasPermission name="clear:bankGetCtrl:reverse">
					<c:if test="${'1' eq agencyPayMain.status}">
						<!-- 冲正 -->
						<a id="aReverse" href="#"
							onclick="authorize('${agencyPayMain.outNo}');" title="<spring:message code='common.reverse'/>">
							<i class="fa fa-exchange"></i>
						</a>
					</c:if>
				</shiro:hasPermission>
				</span>
				
				<span style='width:20px;display:inline-block;'> 
				<shiro:hasPermission name="clear:bankGetCtrl:print"> 
					<!-- 打印 -->
					<a href="#" onclick="printDetail('${agencyPayMain.outNo}');" title="<spring:message code='common.print'/>">
						<i class="fa fa-print text-yellow fa-lg"></i>
					</a>
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