<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html style="overflow-x:scroll !important">
<head>
<title><spring:message code="store.actualArea" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">


</script>
<link href="${ctxStatic}/css/modules/store/v01/stoAreaSettingInfo/area.css" rel="stylesheet" />
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 实时库区  --%>
		<c:forEach items="${fns:getDictList('store_area_type')}" var="typeItem" >
			<c:choose>
				<c:when test="${currentStoreAreaType == typeItem.value }">
					<li class="active"><a href="#" onclick="javascript:return false;">${typeItem.label }</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="${ctx}/store/v01/stoAreaSettingInfo/showAreaGoodsGraph?storeAreaType=${typeItem.value}&displayHref=${displayHref }">${typeItem.label }</a></li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<%-- 库区物品列表 --%>
		<li><a href="${ctx}/store/v01/StoGoodsLocationInfo/findAreaGoodInfoList?displayHref=${displayHref }"><spring:message code="store.areaGoodsList" /></a></li>
	</ul>
	<br />
	<div class="area_list">
		<sys:message content="${message}"/>
		<table>
			<tbody>
				<c:forEach items="${areaGoodsInfoList}" var="colList" >
					<tr>
						<c:forEach items="${colList}" var="colInfo" > 
							<td>
								<div class="${colInfo.slamCode}">
									<c:if test="${fns:getUser().office.type == '7'}">
										<div class="area_time">
											${fns:getOfficeNameById(colInfo.officeId)}
										</div>
									</c:if>
									<div class="area_title">
										<span class="area_title_num">
											<c:if test="${colInfo.stoNum > 0 }">
												<a href="${ctx}/store/v01/StoGoodsLocationInfo/findAreaGoodInfoList?storeAreaId=${colInfo.id}&displayHref=${displayHref }&delFlag=">
													${colInfo.storeAreaName }
												</a>
											</c:if>
											<c:if test="${colInfo.stoNum == 0 }">
												${colInfo.storeAreaName }
											</c:if>
										</span>
										<c:if test="${colInfo.delFlag =='0' }">
											<c:if test="${colInfo.stoNum == 0 }">
												<span class="area_title_bag">0&nbsp;/&nbsp;${colInfo.maxCapability}<spring:message code="store.goodsUnit" /></span>
											</c:if>
											<c:if test="${colInfo.stoNum > 0 and colInfo.stoNum > colInfo.maxCapability}">
												<span class="area_title_bag"><font color="red"><B>${colInfo.stoNum}</B></font>&nbsp;/&nbsp;${colInfo.maxCapability}<spring:message code="store.goodsUnit" /></span>
											</c:if>
											<c:if test="${colInfo.stoNum > 0 and colInfo.stoNum <= colInfo.maxCapability}">
												<span class="area_title_bag">${colInfo.stoNum}&nbsp;/&nbsp;${colInfo.maxCapability}<spring:message code="store.goodsUnit" /></span>
											</c:if>
										</c:if>
									</div>
									<div class="area_time">
										<c:if test="${colInfo.delFlag =='0' and colInfo.stoNum > 0 }">
											<fmt:formatDate value="${colInfo.areaMinDate}" pattern="yyyy-MM-dd" />&nbsp;～&nbsp;<fmt:formatDate value="${colInfo.areaMaxDate}" pattern="yyyy-MM-dd" />
										</c:if>
									</div>
									<div class="widget-body no-padding"></div>
								</div>
							</td>
					
						</c:forEach>
					</tr>
			
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
