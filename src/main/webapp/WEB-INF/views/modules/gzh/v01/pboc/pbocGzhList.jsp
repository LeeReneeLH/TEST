<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>GZH列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		// 修改物品
		function importDetail() {
			
			var content = "iframe:${ctx}/gzh/v02/pbocGzh/toPbocImportDetail";
			top.$.jBox.open(
					content,
					"查看详情", 300, 400, {
						buttons : {
							//关闭
							"<spring:message code='common.close' />" : true
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
	<div class="row">
		<form:form id="searchForm" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
				
			<%-- 银行名称 --%>
			<label>银行名称：</label>
			<input type="text" id="bankName" name="bankName" class="input-small">
		
			<%-- 开始日期 --%>
			<label>登记日期：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
			<%-- 结束日期 --%>
			<label>至</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value=""
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
			&nbsp;
			<%-- 查询 --%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				
			
		</form:form>
	</div>
	<div class="row" style="margin-top: 10px;margin-bottom: 10px;margin-left:2px">
		<%-- 导入 --%>
		<input id="btnSubmit" class="btn btn-primary" type="button" value="导入" onclick="importDetail();"/>
		<%-- 导出 --%>
		<input id="btnSubmit" class="btn btn-default" type="button" value="导出" style="margin-left:10px;"/ >
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 银行名称 --%>
				<th>银行名称</th>
				<%-- GZH名称 --%>
				<th>GZH名称</th>
				<%-- 上传日期 --%>
				<th>上传日期</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- GZH名称 --%>
				<td >8546234157686.gzh</td>
				<%-- 上传日期--%>
				<td>2016-07-05 10:20</td>
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国农业银行</td>
				<%-- GZH名称 --%>
				<td >6745354324432.gzh</td>
				<%-- 上传日期--%>
				<td>2016-07-05 10:20</td>
			</tr>
			
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
