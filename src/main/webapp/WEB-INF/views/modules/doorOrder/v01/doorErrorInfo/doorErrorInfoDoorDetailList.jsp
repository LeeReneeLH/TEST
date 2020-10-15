<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@page import="com.coffer.businesses.common.Constant"%>  
<html>
<head>
	<title><spring:message code="door.equipment.title" /></title>
	<meta name="decorator" content="default"/>
	<!-- <style type="text/css">
		td {
			word-wrap: break-word;
			word-break: break-all; 
		}
		@media only screen and (max-width: 1760px){
		    #divTable{overflow: auto;}
		}
	</style> -->
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
		
		//拆箱单号弹窗拆箱明细
		function showBusinessIdDetail(id) {
			var content = "iframe:${ctx}/collection/v03/checkCash/view?id=" + id + "&type=1";
			top.$.jBox.open(
				content,
				"款箱拆箱查看详情", 1550, 700, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.displayDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi2.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi3.setAttribute("style","display:none");
					}
			});
		}
		
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/doorErrorInfo/doorDetailExport");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList");		
			});
		});
		
		//款袋编号弹窗存款明细
		 function showBagNoDetail(id) {
			var content = "iframe:${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=" + id;
			top.$.jBox.open(
				content,
				"查看详情", 1400, 700, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.disDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi2.setAttribute("style","display:none");
					}
			});
		} 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 商户差错列表 -->
		<shiro:hasPermission name="doororder:doorErrorInfo:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/"><spring:message code="door.errorInfo.merchantList" /></a></li></shiro:hasPermission>
		<!-- 门店差错列表 -->
		<shiro:hasPermission name="doororder:doorErrorList:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/doorList"><spring:message code="door.errorInfo.doorList" /></a></li></shiro:hasPermission>
		<!-- 门店差错明细 -->
		<shiro:hasPermission name="doororder:doorErrorDetailList:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList"><spring:message code="door.errorInfo.doorDetailList" /></a></li></shiro:hasPermission>
		
	</ul>
	<form:form id="searchForm" modelAttribute="doorErrorInfo" action="${ctx}/doorOrder/v01/doorErrorInfo/doorDetailList" method="post" class="breadcrumb form-search ">
		<c:set var="ConClear" value="<%=Constant.SysUserType.CLEARING_CENTER_OPT%>"/>  
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="officeId" name="officeId" type="hidden" value="${doorErrorInfo.officeId}"/>
		<c:if test="${!fn:contains(doorErrorInfo.custNo,',')}"><input id="custNo" name="custNo" type="hidden" value="${doorErrorInfo.custNo}"/></c:if>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
			<!-- <div class="span14" style="margin-top:5px"> -->
			<div>
				<%-- 拆箱单号 --%>
				<label><spring:message code="door.checkCash.codeNo" />：</label>
				<form:input path="businessId" htmlEscape="false" maxlength="32" class="input-small"/>
			</div><div>
				<%-- 款袋编号 --%>
				<label><spring:message code="door.errorInfo.bagNo" />：</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="32" class="input-small"/>
			</div><div>
				<%-- 门店 --%>
				<c:if test="${fn:contains(doorErrorInfo.custNo,',')}"><label><spring:message code="door.public.cust" />：</label>
					<sys:treeselect id="custNo" name="custNo"
							value="${doorErrorInfo.custNo}" labelName="custName"
							labelValue="${doorErrorInfo.custName}" title="<spring:message code='door.public.cust' />"
							url="/sys/office/treeData" cssClass="required input-small"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
				</c:if>
			</div><div>
				<!-- 类别-->
				<label><spring:message code='door.errorInfo.type'/>：</label>
				<form:select path="errorType" class="input-medium">
					<form:option value=""><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getFilterDictList('clear_error_type',false,'1')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div><div>
				<%-- 清分人员 --%>
				<label><spring:message code="door.checkCash.clearMan" /> ：</label>
				<form:select path="clearManNo" id="clearManNo" class="input-small required" style="font-size:15px;color:#000000">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${fns:getUsersByTypeRoleOffice(ConClear,ClearRoleId,fns:getUser().getOffice().getId())}" itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
			</div><div>
				<!-- 状态-->
				<label><spring:message code="door.errorInfo.status" />：</label>
				<form:select path="status" class="input-medium" id ="selectStatus">
					<form:option value="00"><spring:message code="common.select" /></form:option>		
					<form:options items="${fns:getDictList('check_error_status')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div><div>
				<!-- 开始日期 -->
				<label><spring:message code="door.errorInfo.createDate" />：</label>
				<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeStart}" pattern="yyyy-MM-dd"/>" 
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div><div>
				<!-- 结束日期 -->
				<label>~</label>
				<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
					value="<fmt:formatDate value="${doorErrorInfo.createTimeEnd}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate: '#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div><div>
				<%-- 查询 --%>
				&nbsp;
				<input id="btnSubmit" onclick="window.top.clickSelect();" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div><div>
				<!-- 导出 -->
				&nbsp;
				<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
			</div><div>
				<!-- 返回 -->
				&nbsp;
				<input id="btnCancel" class="btn btn-default" type="button" value="<spring:message code='common.return'/>" onclick="history.go(-1)"/>
			
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			<%-- 序号 --%>
			<th><spring:message code="common.seqNo" /></th>
			<%-- 拆箱单号 --%>
			<th class="sort-column a.business_id"><spring:message code="door.errorInfo.businessId" /></th>
			<%-- 款袋编号 --%>
			<th class="sort-column ccm.rfid"><spring:message code="door.errorInfo.bagNo" /></th>
			<%-- 门店名称 --%>
			<th class="sort-column cust.name"><spring:message code="door.errorInfo.custName" /></th>
			<%-- 清分中心 --%>
			<th class="sort-column o.name"><spring:message code="door.errorInfo.officeName" /></th>
			<%-- 差错类型 --%>
			<th><spring:message code="door.errorInfo.type" /></th>
			<%-- 录入金额 --%>
			<!-- <th>录入金额</th> -->
			<%-- 清分金额 --%>
			<!-- <th>清分金额</th> -->
			<%-- 差额 --%>
			<th class="sort-column a.diff_amount"><spring:message code="door.errorInfo.diffAmount" /></th>
			<%-- 清分人员 --%>
			<th class="sort-column a.clear_man_name"><spring:message code="door.errorInfo.clearManName" /></th>
			<%-- 确认人 --%>
			<th class="sort-column a.makesure_man_name"><spring:message code="door.errorInfo.makesureManName" /></th>
			<%-- 登记日期--%>
			<th class="sort-column a.create_date"><spring:message code="door.errorInfo.createDate" /></th>
			<%-- 钞袋使用时间 --%>
			<th style="padding-right:48px"><spring:message code="door.errorInfo.bagNoUseTime" /></th>
			<%-- 状态 --%>
			<th><spring:message code="door.errorInfo.status" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="doorErrorInfo" varStatus="status">
			<tr>
				<c:if test="${status.index eq 0}"><td></td></c:if>
        		<c:if test="${status.index ne 0}"><td>${status.index}</td></c:if>
				<%-- <td>${doorErrorInfo.businessId}</td> --%>
				<td>
					<c:if test="${status.index eq 0}">
						${doorErrorInfo.custName}
					</c:if>
	       			<c:if test="${status.index ne 0}">
								<a href="javascript:void(0);"
									onclick="showBusinessIdDetail('${doorErrorInfo.businessId}')">
									${doorErrorInfo.businessId} </a>
							</c:if>
				</td>
				<td>
					<a href="javascript:void(0);" onclick="showBagNoDetail('${doorErrorInfo.doiId}')"> 
						${doorErrorInfo.bagNo}
					 </a>
				</td>
				<td>
					<c:if test="${status.index eq 0}"></c:if>
	       			<c:if test="${status.index ne 0}">
							${doorErrorInfo.custName}
	      			</c:if>
				</td>
				<td>${doorErrorInfo.office.name}</td>
				<td>${fns:getDictLabel(doorErrorInfo.errorType,'clear_error_type',"")}</td>
				<%-- <td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.inputAmount}" type="currency" pattern="#,##0.00"/></td>
				<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.checkAmount}" type="currency" pattern="#,##0.00"/></td> --%>
			 	<td  style="text-align:right;"><fmt:formatNumber value="${doorErrorInfo.diffAmount}" type="currency" pattern="#,##0.00"/></td>
			 	<td>${doorErrorInfo.clearManName}</td>
			 	<td>${doorErrorInfo.makesureManName}</td>
			 	<td><fmt:formatDate value="${doorErrorInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			 	<%-- 钞袋使用时间  时间段展示  ZXK start--%>
			 	 <td>
			 	    <c:if test="${doorErrorInfo.lastTime ne null}">
			 	       <fmt:formatDate value="${doorErrorInfo.lastTime}"
							pattern="yyyy-MM-dd HH:mm" />~<br/>
			 	       <fmt:formatDate value="${doorErrorInfo.thisTime}"
							pattern="yyyy-MM-dd HH:mm" />
			 	    </c:if>
			 	  </td>
			 	<%-- 钞袋使用时间   end--%>
			 	<%-- 钞袋使用时间  无值显示'不足1分钟' start--%>
				<%-- <c:choose>
				    值为'0'时显示 '不足1分钟'
					<c:when test="${doorErrorInfo.bagNoUseTime eq '0'}">
						<td><spring:message code="door.public.lessMinute" /></td>
					</c:when>
					 值为null 时不显示
					<c:when test="${doorErrorInfo.bagNoUseTime eq null}">
						<td>${doorErrorInfo.bagNoUseTime}</td>
					</c:when>
					 有值且不为'0' 正常显示
					<c:otherwise>
						<td>${doorErrorInfo.bagNoUseTime}<spring:message code="door.public.minute" /></td>
					</c:otherwise>
				</c:choose> --%>
				<%-- 钞袋使用时间   end--%>
				<td>
					<!-- 状态颜色动态配置 gzd 2020-05-26  -->
					${fns:getDictLabelWithCss(doorErrorInfo.status,'check_error_status',"",true)}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>