<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>省份管理</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript">
	
		$(function() {
			makeProOptionName();
		});

		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}

		function makeProOptionName() {
			var url = "${ctx}/sys/province/getSelect2ProDataName";

			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data) {
					// 1、作成省份的选项
					$('#proName').select2({
						containerCss : {
							width : '163px',
							display : 'inline-block'
						},
						data : {
							results : data,
							text : 'text'
						},
						//请选择
						placeholder : "<spring:message code='common.select'/>",
						allowClear : true,
						formatSelection : format,
						formatResult : format
					});
				},

				error : function() {
					// 系统内部异常，请稍后再试或与系统管理员联系
					alertx("<spring:message code='message.E0101'/>")
				}
			});
		}

		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) {
			return item.text;
		}
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/province/">省份列表</a></li>
		<shiro:hasPermission name="sys:province:edit"><li><a href="${ctx}/sys/province/form">省份添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="provinceEntity" action="${ctx}/sys/province/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();" />
			
		<label>省份编码：</label>
		<form:input path="provinceCode" htmlEscape="false" maxlength="50" class="input-medium"/>
		
		<label>省份名称：</label>
		<form:input path="proName" htmlEscape="false" maxlength="50" class="input-medium"/>
		
		<label>状态：</label>
		<form:select path="delFlag" class="input-medium" maxlength="50">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getDictList('disabled_flag')}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	
	<sys:message content="${message}"/>
	
	<table id="contentTable" class="table table-hover">
		<thead><tr><th class="sort-column PROVINCE_CODE">省份编码</th><th>地图编码</th><th>省份名称</th><th class="sort-column LONGITUDE">经度</th><th class="sort-column LATITUDE">纬度</th><th>状态</th><shiro:hasPermission name="sys:province:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="pro">
			<tr>
				<td>${pro.provinceCode}</td>
				<td>${pro.proJsonCode}</td>
				<td><a href="${ctx}/sys/province/form?id=${pro.id}">${pro.proName}</a></td>
				<td>${pro.longitude}</td>
				<td>${pro.latitude}</td>
				<c:choose>
					<c:when test="${pro.delFlag == '1'}">
						<td class="text-red">停用</td>
					</c:when>
					<c:when test="${pro.delFlag == '0'}">
						<td>正常</td>
					</c:when>
				</c:choose>
				<shiro:hasPermission name="sys:province:edit"><td>
    				<a href="${ctx}/sys/province/form?id=${pro.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
					<c:choose>
					<c:when test="${pro.delFlag == '1'}">
						<a href="${ctx}/sys/province/revert?id=${pro.id}" onclick="return confirmx('确认要恢复该省份吗？', this.href)" title="恢复"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
					</c:when>
					<c:when test="${pro.delFlag == '0'}">
						<a href="${ctx}/sys/province/delete?id=${pro.id}" onclick="return confirmx('确认要停用该省份及其所属城市和县区吗？', this.href)" title="停用" style="margin-left:10px;"><i class="fa fa-ban text-red fa-lg"></i></a>
					</c:when>
					</c:choose>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
