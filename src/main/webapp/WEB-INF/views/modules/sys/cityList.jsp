<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>城市管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(function() {
			makeProOption();
			var provinceCode = $("#provinceCode").val();
			//省份有值时城市下拉菜单初始化，无值时城市下拉菜单无值（查询时用）
			if (provinceCode == '') {
				$('#cityName').select2({
					containerCss : {
						width : '163px',
						display : 'inline-block'
					},
					data : {
						results : [],
						text : 'text'
					},
					//请选择
					placeholder : "<spring:message code='common.select'/>",
					formatSelection : format,
					formatResult : format
				});
			} else {
				makeCityOptionName();
			}
			
			//选择省份时初始化城市下拉菜单
			$("#provinceCode").on("change", function() {
				var provinceCode = $("#provinceCode").val();
				if (provinceCode != '') {
					//省份栏有值刷新城市下拉菜单
					$('#cityName').val("");
					makeCityOptionName();
				} else {
					//省份栏无值清空城市下拉菜单
					$('#cityName').val("");
					$('#cityName').select2({
						containerCss : {
							width : '163px',
							display : 'inline-block'
						},
						data : {
							results : [],
							text : 'text'
						},
						//请选择
						placeholder : "<spring:message code='common.select'/>",
						formatSelection : format,
						formatResult : format
					});
				}

			});
		});

		function page(n, s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
			return false;
		}

		//省份下拉菜单作成(实体类对象用省份编码接收)
		function makeProOption() {
			var url = "${ctx}/sys/province/getSelect2ProData";
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data) {
					// 1、作成省份的选项
					$('#provinceCode').select2({
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

		//城市下拉菜单作成(实体类对象用城市编码接收)
		function makeCityOptionName() {
			var url = "${ctx}/sys/city/getSelect2CityDataName";
			var provinceCode = $("#provinceCode").val();

			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					"provinceCode" : provinceCode
				},

				success : function(data) {
					// 1、作成城市的选项
					$('#cityName').select2({
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
		<li class="active"><a href="${ctx}/sys/city/">城市列表</a></li>
		<shiro:hasPermission name="sys:city:edit"><li><a href="${ctx}/sys/city/form">城市添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="cityEntity" action="${ctx}/sys/city/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();" />
		
		<label>城市编码：</label>
		<form:input path="cityCode" htmlEscape="false" maxlength="50" class="input-medium"/>
		
		<label>所属省份：</label>
		<form:input type="text" path="provinceCode" maxlength="50" class="input-medium" htmlEscape="false"/>
		
		<label>城市名称：</label>
		<form:input path="cityName" htmlEscape="false" maxlength="50" class="input-medium"/>
		<%-- <form:input type="text" path="cityCode" cssStyle="width : 210px;" /> --%>
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
		<thead><tr><th class="sort-column CITY_CODE">城市编码</th><th>地图编码</th><th>城市名称</th><th class="sort-column LONGITUDE">经度</th><th class="sort-column LATITUDE">纬度</th><th>所属省份</th><th>状态</th><shiro:hasPermission name="sys:city:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="city">
			<tr>
				<td>${city.cityCode}</td>
				<td>${city.cityJsonCode}</td>
				<td><a href="${ctx}/sys/city/form?id=${city.id}">${city.cityName}</a></td>
				<td>${city.longitude}</td>
				<td>${city.latitude}</td>
				<td>${city.proName}</td>
				<c:choose>
					<c:when test="${city.delFlag == '1'}">
						<td class="text-red">停用</td>
					</c:when>
					<c:when test="${city.delFlag == '0'}">
						<td>正常</td>
					</c:when>
				</c:choose>
				<shiro:hasPermission name="sys:city:edit"><td>
    				<a href="${ctx}/sys/city/form?id=${city.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
					<c:choose>
					<c:when test="${city.delFlag == '1'}">
						<a href="${ctx}/sys/city/revert?id=${city.id}" onclick="return confirmx('确认要恢复该城市吗？', this.href)" title="恢复"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
					</c:when>
					<c:when test="${city.delFlag == '0'}">
						<a href="${ctx}/sys/city/delete?id=${city.id}" onclick="return confirmx('确认要停用该城市及其所属县区吗？', this.href)" title="停用" style="margin-left:10px;"><i class="fa fa-ban text-red fa-lg"></i></a>
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
