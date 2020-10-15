<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 标题：物品关联 -->
	<title><spring:message code="store.goodsRelevance" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			// 加载select2下拉列表选项用
			function format(item) { return item.label; }
			
			// 组ID
			var groupId = $("#groupId").val();
			
			// 设置控件只读属性
			setReadOnly();
			
			// 页面初始化时，判断是否显示套别
			setSets();
			
			// 页面初始化时，设置面值的选项
			setDenOptions();
			
			// 页面初始化时，设置单位的选项
			setUnitOptions();
			
			// 币种变化时,判断是否显示套别
			$("#currency").change(setSets);
			
			// 币种变化时,设置面值选项
			$("#currency").change(setDenOptions);
			
			// 材质变化时,设置面值选项
			$("#cash").change(setDenOptions);
			
			// 材质变化时,设置单位选项
			$("#cash").change(setUnitOptions);
			
			// 设置套别是否可用
			function setSets(){
				var currency = $("#currency").val();
				if(currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
					// 人民币的场合，显示“套别”
					$("#setsGroup").show();
					$("#sets").removeAttr('disabled');
				}else{
					// 其他币种的场合，不显示“套别”
					$("#setsGroup").hide();
					$("#sets").attr('disabled','disabled');
				}
			}
			
			// 设置控件只读属性
			function setReadOnly(){
				if(groupId){
					// 编辑物品关联的场合
					$("#currency").attr('readOnly','readOnly');
					$("#classification").attr('readOnly','readOnly');
					$("#sets").attr('readOnly','readOnly');
					$("#cash").attr('readOnly','readOnly');
				}
			}
			
			// 设置面值的选项
			function setDenOptions(){
				var currency = $("#currency").val();
				var cash = $("#cash").val(); 
				var url = '${ctx}/store/v01/stoRelevance/getDenOptions';
				var stoRelevance = {};
				stoRelevance.currency = currency;
				stoRelevance.cash = cash;
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						param : JSON.stringify(stoRelevance)
					},
					success : function(serverResult, textStatus) {
						// 1、作成面值的选项
						$('#denominationSel').select2({
							data:{ results: serverResult, text: 'label' },
							multiple: true,
							formatSelection: format,
							formatResult: format
						});
						// 2、编辑的时赋值
						if(groupId){
							$('#denominationSel').select2("val",jQuery.parseJSON($("#denomiListJson").val()));
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						// TODO
						alert("error")
					}
				});
			}
			
			// 设置单位的选项
			function setUnitOptions(){
				var cash = $("#cash").val(); 
				var url = '${ctx}/store/v01/stoRelevance/getUnitOptions';
				var stoRelevance = {};
				stoRelevance.cash = cash;
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						param : JSON.stringify(stoRelevance)
					},
					success : function(serverResult, textStatus) {
						// 1、作成单位的选项
						$('#unitSel').select2({
							data:{ results: serverResult, text: 'label' },
							multiple: true,
							formatSelection: format,
							formatResult: format
						});
						// 2、编辑的时赋值
						if(groupId){
							$('#unitSel').select2("val",jQuery.parseJSON($("#unitListJson").val()));
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						// TODO
						alert("error")
					}
				});
			}
			
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 物品关联列表 -->
		<li>
			<a href="${ctx}/store/v01/stoRelevance/">
				<spring:message code="store.goodsRelevance" /><spring:message code="common.list" />
			</a>
		</li>
		<!-- 物品关联修改/添加/查看 -->
		<li class="active">
			<a href="${ctx}/store/v01/stoRelevance/form?groupId=${stoRelevance.groupId}">
				<!-- 修改/添加 -->
				<shiro:hasPermission name="store:stoRelevance:edit">
				<c:choose>
					<c:when test="${not empty stoRelevance.groupId}">
						<spring:message code="store.goodsRelevance" /><spring:message code="common.modify" />
					</c:when>
					<c:otherwise>
						<spring:message code="store.goodsRelevance" /><spring:message code="common.add" />
					</c:otherwise>
				</c:choose>
				</shiro:hasPermission>
				<!-- 查看 -->
				<shiro:lacksPermission name="store:stoRelevance:edit">
				<spring:message code="store.goodsRelevance" /><spring:message code="common.view" />
				</shiro:lacksPermission>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stoRelevance" action="${ctx}/store/v01/stoRelevance/save" method="post" class="form-horizontal">
		<!-- 隐藏域：组ID -->
		<form:hidden path="groupId" id="groupId"/>
		<!-- 消息 -->
		<sys:message content="${message}"/>
		<!-- 币种 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.currency" />：</label>
			<div class="controls">
				<form:select path="currency" id="currency" class="input-large required">
				    <form:options items="${empty(stoRelevance.groupId) == true ? sto:getGoodDictListWithFg('currency') : sto:getGoodDictListWithOutFg('currency')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 类别 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.classification" />：</label>
			<div class="controls">
				<form:select path="classification" id="classification" class="input-large required">
				    <form:options items="${empty(stoRelevance.groupId) == true ? sto:getGoodDictListWithFg('classification') : sto:getGoodDictListWithOutFg('classification')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 套别 -->
		<div class="control-group" id="setsGroup">
			<label class="control-label"><spring:message code="common.edition" />：</label>
			<div class="controls">
				<form:select path="sets" id="sets" class="input-large required">
				    <form:options items="${empty(stoRelevance.groupId) == true ? sto:getGoodDictListWithFg('edition') : sto:getGoodDictListWithOutFg('edition')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 材质 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="common.cash" />：</label>
			<div class="controls">
				<form:select path="cash" id="cash" class="input-large required">
				    <form:options items="${empty(stoRelevance.groupId) == true ? sto:getGoodDictListWithFg('cash') : sto:getGoodDictListWithOutFg('cash')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			    </form:select>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 面值 -->
		<form:input type="hidden" path="denomiListJson" id="denomiListJson" ></form:input>
		<div class="control-group">
			<label class="control-label"><spring:message code="common.denomination" />：</label>
			<div class="controls">
				<form:input path="denomiList" id="denominationSel" class="input-large required"/>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 单位 -->
		<form:input type="hidden" path="unitListJson" id="unitListJson"></form:input>
		<div class="control-group">
			<label class="control-label"><spring:message code="common.units" />：</label>
			<div class="controls">
				<form:input path="unitList" id="unitSel" class="input-large required"/>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<!-- 保存 -->
			<shiro:hasPermission name="store:stoRelevance:edit">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.save'/>"/>&nbsp;
			</shiro:hasPermission>
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick = "window.location.href ='${ctx}/store/v01/stoRelevance/back'"/>
		</div>
	</form:form>
</body>
</html>
