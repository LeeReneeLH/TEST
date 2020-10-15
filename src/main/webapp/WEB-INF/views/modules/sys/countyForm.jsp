<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			$("#value").focus();
			//初始化下拉菜单
			makeOption();

			//所属城市下拉菜单作成
			$("#provinceCode").on("change", function() {
				var provinceCode = $("#provinceCode").val();

				if (provinceCode != '') {
					//省份栏有值刷新城市下拉菜单
					$('#cityCode').val("");
					makeCityOption();
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
				}

			});

			//验证县/区，同类型的键值不能执行操作
			$("#inputForm").validate(
					{

						rules : {
							countyCode : {
								remote : {
									url : "${ctx}/sys/county/checkCountyCode",
									data : {
										countyCode : function() {
											return $("#countyCode").val();
										}
									}
								}
							}
						},
						messages : {
							countyCode : {
								remote : "该县/区编码已存在，请重新输入"
							}
						},
						submitHandler : function(form) {
							loading('正在提交，请稍等...');
							form.submit();
						},

						errorPlacement : function(error, element) {

							if (element.is(":checkbox") || element.is(":radio")
									|| element.parent().is(".input-append")) {
								error.appendTo(element.parent().parent());
							} else {
								error.insertAfter(element);
							}
						}
					});

			//经纬度输入验证
			$.validator.addMethod("checkLocation", function(value, element) {
				var checkLocation = /^([1-9]\d{0,5}|0)(\.\d{0,6})?$/;
				return this.optional(element) || (checkLocation.test(value));
			}, "请输入合法数字");

		});

		function makeOption() {

			makeProOption();
			//当前省份有值则初始化城市下拉菜单，无值时不显示城市数据（查询时用）
			var provinceCode = $("#provinceCode").val();
			if (provinceCode != '') {
				makeCityOption();
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
		<li><a href="${ctx}/sys/county/">县/区列表</a></li>
		<li class="active"><a href="${ctx}/sys/county/form?id=${countyEntity.id}">县/区<shiro:hasPermission name="sys:county:edit">${not empty countyEntity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:county:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="countyEntity" action="${ctx}/sys/county/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">县/区编码:</label>
			<div class="controls">
				<form:input path="countyCode" htmlEscape="false" maxlength="50" class="required digits" disabled="${empty countyEntity.id ? false:true }"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属省份:</label>
			<div class="controls">
				<form:input path="provinceCode" htmlEscape="false" maxlength="50" class="number" cssStyle="width : 210px;"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属城市:</label>
			<div class="controls">
				<form:input path="cityCode" htmlEscape="false" maxlength="50" class="number" cssStyle="width : 210px;"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">县/区名称:</label>
			<div class="controls">
				<form:input path="countyName" htmlEscape="false" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经度:</label>
			<div class="controls">
				<form:input path="longitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">纬度:</label>
			<div class="controls">
				<form:input path="latitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
				
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:county:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick = "window.location.href ='${ctx}/sys/county/back'"/>
		</div>
	</form:form>
</body>
</html>