<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<table id="contentTable" class="table table-hover">
	<c:forEach items="${routeDetailList}" var="stoRouteDetail" varStatus="status">
		<tr>
			<td width="10%">${status.index+1}</td>
			<td width="20%"><spring:message code="store.selectOffice"/>：</td>
			<td width="50%">${stoRouteDetail.office.name}</td>
			<td width="20%"><a href="javascript:deleteDetail('${ctx}/store/v01/stoRouteInfo/deleteDetail?index=${status.index}')" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i></a>
			</td>
		</tr>
	</c:forEach>
</table>

<script type="text/javascript">
	function deleteDetail(url){
		$.post(url, function (data) {
			//alert(data.message);
			$("#detailDiv").load("${ctx}/store/v01/stoRouteInfo/getList");
		},"json");
	}
</script>
