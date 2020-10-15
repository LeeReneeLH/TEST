<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<table id="contentTable" class="table  table-hover">
	<tr>
		<!-- 序号 -->
				<th><spring:message code="common.seqNo"/></th>
				<!-- 重空分类 -->
				<th><spring:message code="common.importantEmpty.kind"/></th>
				<!-- 重空类型 -->
				<th><spring:message code="common.importantEmpty.type"/></th>
				<!-- 起止区间值 -->
				<th><spring:message code="store.intervalValue"/></th>
				<!-- 数量 -->
				<th><spring:message code="common.number"/></th>
				<!-- 操作 -->
				<th><spring:message code="common.operation"/></th>
	</tr>
	<c:forEach items="${emptyDocumentList}" var="stoEmptyDocument" varStatus="status">
		<tr>
			<td >${status.index+1}</td>
			<td>${sto:getGoodDictLabel(stoEmptyDocument.stoBlankBillSelect.blankBillKind,'blank_bill_kind','')}</td>
			<td>${sto:getGoodDictLabel(stoEmptyDocument.stoBlankBillSelect.blankBillType,'blank_bill_type','')}</td>
			<td >${stoEmptyDocument.startNumber}-${stoEmptyDocument.endNumber}</td>
			<td>${stoEmptyDocument.createNumber}</td>
			<td>
			<c:if test="${stoEmptyDocument.id==null or stoEmptyDocument.id==''}">
				<a href="javascript:deleteDetail('${ctx}/store/v01/stoEmptyDocument/deleteDetail?index=${status.index}')" title="删除">
								<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i></a>
			</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<script type="text/javascript">
	function deleteDetail(url){
		$.post(url, function (data) {
			//alert(data.message);
			$("#detailDiv").load("${ctx}/store/v01/stoEmptyDocument/getList");
		},"json");
	}
</script>
