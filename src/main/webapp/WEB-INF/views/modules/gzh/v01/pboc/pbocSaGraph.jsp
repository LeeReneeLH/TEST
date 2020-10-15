<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>统计分析</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	
</head>
<body>
	<div class="row">
		<form:form id="searchForm" action="" method="post" class="breadcrumb form-search">
			<div class="row">
				<div class="span9" style="margin-top: 10px;">
					<%-- 银行名称 --%>
					<label>银行名称：</label>
					<input type="text" id="bankName" name="bankName" class="input-small">
					<%-- 地区选择 --%>
					<label>地区选择：</label>
					<input type="text" id="areaId" name="areaId" class="input-small">
					<%-- 可疑币类型 --%>
					<label>地区选择：</label>
					<input type="text" id="type" name="type" class="input-small">
				</div>
				<div class="span8" style="margin-top: 10px;">
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
					<label><input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				</label>
				</div>
			</div>
		</form:form>
	</div>
	<div class="row" style="margin-left: 2px">
		<div class="span12">
			<img src="${ctxStatic}/images/pbocgraph.png"/>
		</div>
		
	</div>
</body>
</html>
