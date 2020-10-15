<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 凭条信息 -->
	<title><spring:message code="door.depositSerial.tickrtapeInfo" /></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
    	.fixedThead{
        	display: block;
            width: 100%;
        }
        .scrollTbody{
            display: block;
            height: 262px;
            overflow: auto;
            width: 100%;
        }
        thead.fixedThead tr th:last-child {
            color:#FF0000;
            width: 130px;
        }
        .rowSel {
		  background-color: #5599FF;
		}
        td {
		  word-wrap: break-word;
		  word-break: break-all; 
		}
		@media only screen and (max-width: 1200px){
		    #divRight{overflow: auto;}
		}
		.detailCo{
		 color:#dc776a;
		 font-weight:700;
		 font-size:15px;
		}
    </style>
</head>
<body>
	<form:form id="inputForm" modelAttribute="tickrtapeInfo" action="${ctx}/weChat/v03/doorOrderInfo/save" method="post" class="form-horizontal">
		<sys:message content="${message}"/>	
					<div style="margin-top: 20px;">
					<table id="tblTikertape" class="table">
						<thead>
							<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
								<%-- 序号 --%>
								<th style="text-align: center"><spring:message code="common.seqNo" /></th>
								<%-- 凭条 --%>
								<th style="text-align: center"><spring:message code="door.doorOrder.tickertape" /></th>
								<%-- 业务类型 --%>
								<th style="text-align: center"><spring:message code="door.doorOrder.businessType" /></th>
								<%-- 金额 --%>
								<th style="text-align: center"><spring:message code="door.public.money" /></th>
								<%-- 存款人 --%>
								<th style="text-align: center"><spring:message code="door.public.createBy" /></th>
								<%-- 存款日期 --%>
								<th style="text-align: center"><spring:message code="door.public.createDate" /></th>
								<%-- 存款备注 --%>
								<th style="text-align: center"><spring:message code="door.historyChange.remarks" /></th>
							</tr>
						</thead>
						<tbody id="tbdTikertape">
						<tr>
							<td>1</td>
						    <td>${tickrtapeInfo.tickertape}</td>
						    <td>${tickrtapeInfo.businessType}</td>
						    <td  style="text-align:right;"><fmt:formatNumber value="${tickrtapeInfo.amount}" type="currency" pattern="#,##0.00"/></td>
						    <td>${tickrtapeInfo.updateName}</td>
						    <td><fmt:formatDate value="${tickrtapeInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						    <td>${tickrtapeInfo.remarks}</td>
						</tr>
						</tbody>
					</table> 
					</div>
			<%-- 面值明细 --%>
			<label class="detailCo"><spring:message code="door.public.denominationDetail" />：</label>
			<div  style="width:76%;margin:0 auto;" align="center">
				 <table id="tblAmount" class="table">
					 <thead>
						<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
							<%-- 序号 --%>
							<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
							<%-- 存款方式 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrder.saveMethod" /></th>
							<%-- 面值 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.parValue" /></th>
							<%-- 张数 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.sheetCount" /></th>
							<%-- 金额 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.money" /></th>
						</tr>
					</thead>
						<tbody id="tbdAmount">
						<c:forEach items="${tickrtapeInfo.amountList}" var="amount" varStatus="status">
								<tr>
									<td style="text-align:center;">${status.index + 1}</td>
									<td style="text-align:center;">${fns:getDictLabel(amount.typeId,'save_method',"")}</td>
									<%-- <td>${amount.detailId}</td> --%>
									<td style="text-align:center;">
									<c:choose>
									<c:when test="${amount.denomination eq '15'}">
									   100元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '16'}">
									   50元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '17'}">
									   20元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '18'}">
									   10元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '19'}">
									   5元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '20'}">
									   2元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '21'}">
									   1元_纸币
									</c:when>
									<c:when test="${amount.denomination eq '22'}">
									   5角_纸币
									</c:when>
									<c:when test="${amount.denomination eq '23'}">
									   2角_纸币
									</c:when>
									<c:when test="${amount.denomination eq '24'}">
									   1角_纸币
									</c:when>
									<c:when test="${amount.denomination eq '25'}">
									   5分_纸币
									</c:when>
									<c:when test="${amount.denomination eq '26'}">
									   2分_纸币
									</c:when>
									<c:when test="${amount.denomination eq '27'}">
									   1分_纸币
									</c:when>
									<c:when test="${amount.denomination eq '28'}">
									   1元_硬币
									</c:when>
									<c:when test="${amount.denomination eq '29'}">
									   5角_硬币
									</c:when>
									<c:when test="${amount.denomination eq '30'}">
									   1角_硬币
									</c:when>
									<c:when test="${amount.denomination eq '31'}">
									   5分_纸币
									</c:when>
									<c:when test="${amount.denomination eq '32'}">
									   2分_纸币
									</c:when>
									<c:when test="${amount.denomination eq '33'}">
									   1分_纸币
									</c:when>
									
									</c:choose>
									</td>
									<td style="text-align:center;">${amount.countZhang}</td>
									<td  style="text-align:center;"><fmt:formatNumber value="${amount.detailAmount}" type="currency" pattern="#,##0.00"/></td>
								</tr>
						</c:forEach>	
						</tbody>
					</table> 
				</div>
	</form:form>
</body>
</html>