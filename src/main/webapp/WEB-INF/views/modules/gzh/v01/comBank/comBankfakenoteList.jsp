<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>假币</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		// 登记
		function showDetail() {
			
			var content = "iframe:${ctx}/gzh/v02/comBankGzh/toComBankRegister";
			top.$.jBox.open(
					content,
					"假币登记", 450, 300, {
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
				
			<%-- 冠字号码 --%>
			<label>冠字号码：</label>
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
			<%-- 登记 --%>
		    <input id="btnSubmit" class="btn btn-red" type="button" value="登记"onclick="showDetail();javascript:return false;"/>		
			
		</form:form>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 银行名称 --%>
				<th>银行名称</th>
				<%-- 冠字号码 --%>
				<th>冠字号码</th>
				<%-- 所属包号 --%>
				<th>所属包号</th>
				<%-- 面值 --%>
				<th>面值</th>
				<%-- 登记日期 --%>
				<th>登记日期</th>
				
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- 冠字号码 --%>
				<td>jl245636654</td>
				<%-- 所属包号--%>
				<td>BA52475424</td>
			    <%-- 面值--%>
				<td>100</td>
				<%-- 登记日期--%>
				<td>2016-07-05</td>
				
				
			</tr>
			<tr>
			    <%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- 冠字号码 --%>
				<td>jl245636654</td>
				<%-- 所属包号--%>
				<td>BA52475424</td>
			    <%-- 面值--%>
				<td>100</td>
			    <%-- 登记日期--%>
				<td>2016-07-05</td>
			
			</tr>
			
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
