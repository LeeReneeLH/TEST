<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>GZH列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		// 上传GZH
		function gzhUpload() {
			
			var content = "iframe:${ctx}/gzh/v02/comBankGzh/toComBankgzhUpload";
			top.$.jBox.open(
					content,
					"查看详情", 350, 400, {
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
		
			<%-- 生成日期 --%>
			<label>生成日期：</label>
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
			<%-- 导入 --%>
		<input id="btnSubmit" class="btn btn-red" type="button" value="导入" onclick="gzhUpload();"/>	
			
		</form:form>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 多选框 --%>
				<th><input type="checkbox" id="allCheckbox"/></th>
				<%-- GZH名称 --%>
				<th>GZH名称</th>
				<%-- 上传日期 --%>
				<th>上传日期</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 多选框 --%>
				<td><input type="checkbox" id="chkItem"/></td>
				<%-- GZH名称 --%>
				<td style="text-align:left;">8546234157686.gzh</td>
				<%-- 上传日期--%>
				<td>2016-07-05 10:20</td>
			</tr>
			<tr>
				<%-- 多选框 --%>
				<td><input type="checkbox" id="chkItem"/></td>
				<%-- GZH名称 --%>
				<td style="text-align:left;">6745354324432.gzh</td>
				<%-- 上传日期--%>
				<td>2016-07-05 10:20</td>
			</tr>
			
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
