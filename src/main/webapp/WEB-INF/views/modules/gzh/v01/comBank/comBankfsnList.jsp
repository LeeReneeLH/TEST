<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>FSN列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		function btnFsnUpload() {
		
		var content = "iframe:${ctx}/gzh/v02/comBankGzh/toComBankfsnUpload";
		top.$.jBox.open(
				content,
				"查看详情", 350, 400, {
					buttons : {
						//确定
						"确定" : "ok",
						//取消
						"取消" : true
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
				
			<%-- 上传日期 --%>
			<label>上传日期：</label>
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
			<%-- FSN上传 --%>
		    <input id="btnSubmit" class="btn btn-red" type="button" value="FSN上传" onclick="btnFsnUpload();"/>	
		    <%-- GZH生成 --%>
		    <input id="btnSubmit" class="btn btn-default" type="button" value="GZH生成"/>
			
		</form:form>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 多选框 --%>
				<th><input type="checkbox" id="allCheckbox"/></th>
				<%-- FSN名称 --%>
				<th>FSN名称</th>
				
				<%-- 上传日期 --%>
				<th>上传日期</th>
				
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 多选框 --%>
				<td><input type="checkbox" id="chkItem"/></td>
				<%-- FSN名称 --%>
				<td>1232435.fsn</td>
			
				<%-- 登记日期--%>
				<td>2016-07-05 10:00</td>
				
				
			</tr>
			<tr>
				<%-- 多选框 --%>
				<td><input type="checkbox" id="chkItem"/></td>
			    <%-- FSN名称 --%>
				<td>535353453245.fsn</td>
				
				 <%-- 登记日期--%>
				<td>2016-07-05 10:00</td>
				
			</tr>
			
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
