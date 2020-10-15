<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>县/区管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	$(function(){
		//初始化下拉菜单
		makeOption();

		//所属城市下拉菜单作成
		$("#provinceCode").on("change", function() {
			var provinceCode = $("#provinceCode").val();

			if (provinceCode != '') {
				//省份栏有值刷新城市下拉菜单
				$('#cityCode').val("");
				makeCityOption();
				
				$('#countyName').val("");
				$('#countyName').select2({
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
				//省份栏无值清空城市下拉菜单
				$('#cityCode').val("");
				$('#cityCode').select2({
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

				//省份栏无值清空县区下拉菜单
				$('#countyName').val("");
				$('#countyName').select2({
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

		//所属县下拉菜单作成
		$("#cityCode").on("change", function() {
			var cityCode = $("#cityCode").val();
			if (cityCode != '') {
				//城市栏有值刷新县区下拉菜单
				$('#countyName').val("");
				makeCountyOptionName();
			} else {
				//城市栏无值清空县区列表
				$('#countyName').val("");
				$('#countyName').select2({
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

		function makeOption() {

			makeProOption();
			//当前省份有值则初始化城市下拉菜单，无值时不显示城市数据（查询时用）
			var provinceCode = $("#provinceCode").val();
			if (provinceCode != '') {
				makeCityOption();
				//当前城市有值则初始化县级下拉菜单，无值时不显示数据（查询时用）
				var cityCode = $("#cityCode").val();
				if (cityCode != '') {
					makeCountyOptionName();
				} else {

					$('#countyName').select2({
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
			} else {

				$('#cityCode').select2({
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

				$('#countyName').select2({
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
		function makeCityOption() {
			var url = "${ctx}/sys/city/getSelect2CityData";
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
					$('#cityCode').select2({
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

		//县/区下拉菜单作成(实体类对象用县/区名称接收)
		function makeCountyOptionName() {
			var url = "${ctx}/sys/county/getSelect2CountyDataName";
			var provinceCode = $("#provinceCode").val();
			var cityCode = $("#cityCode").val();
			//alert(cityCode);
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					"provinceCode" : provinceCode,
					"cityCode" : cityCode
				},

				success : function(data) {
					// 1、作成城市的选项
					$('#countyName').select2({
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
		<li class="active"><a href="${ctx}/sys/county/">县/区列表</a></li>
		<shiro:hasPermission name="sys:county:edit"><li><a href="${ctx}/sys/county/form">县/区添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="countyEntity" action="${ctx}/sys/county/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();" />
		
		<label>县/区编码：</label>
		<form:input path="countyCode" htmlEscape="false" maxlength="50" class="input-medium"/>
		
		<label>所属省份：</label>
		<form:input type="text" path="provinceCode" maxlength="50" class="input-medium" htmlEscape="false"/>
		
		<label>所属城市：</label>
		<form:input type="text" path="cityCode" maxlength="50" class="input-medium" htmlEscape="false"/>
		
		<label>县/区名称：</label>
		<form:input path="countyName" htmlEscape="false" maxlength="50" class="input-medium"/>
		
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
		<thead><tr><th class="sort-column COUNTY_CODE">县/区编码</th><th>县/区名称</th><th class="sort-column LONGITUDE">经度</th><th class="sort-column LATITUDE">纬度</th><th>所属省份</th><th>所属城市</th><th>状态</th><shiro:hasPermission name="sys:county:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="county">
			<tr>
				<td>${county.countyCode}</td>
				<td><a href="${ctx}/sys/county/form?id=${county.id}">${county.countyName}</a></td>
				<td>${county.longitude}</td>
				<td>${county.latitude}</td>
				<td>${county.proName}</td>
				<td>${county.cityName}</td>
				<c:choose>
					<c:when test="${county.delFlag == '1'}">
						<td class="text-red">停用</td>
					</c:when>
					<c:when test="${county.delFlag == '0'}">
						<td>正常</td>
					</c:when>
				</c:choose>
				<shiro:hasPermission name="sys:county:edit"><td>
    				<a href="${ctx}/sys/county/form?id=${county.id}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
					<c:choose>
					<c:when test="${county.delFlag == '1'}">
						<a href="${ctx}/sys/county/revert?id=${county.id}" onclick="return confirmx('确认要恢复该县/区吗？', this.href)" title="恢复"><i class="fa fa-check-square-o text-yellow fa-lg"></i></a>
					</c:when>
					<c:when test="${county.delFlag == '0'}">
						<a href="${ctx}/sys/county/delete?id=${county.id}" onclick="return confirmx('确认要停用该县/区吗？', this.href)" title="停用" style="margin-left:10px;"><i class="fa fa-ban text-red fa-lg"></i></a>
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
