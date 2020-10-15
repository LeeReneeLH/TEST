<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.goodsExchange" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// 加载select2下拉列表选项用
		$("#detailDiv").load("${ctx}/store/v01/stoExchange/getDetailList");
		function format(item) { return item.label; }
		// 物品ID
		var goodsId = $("#goodsId").val();
		var init = false;
		$('#amount').hide();
		// 一、初始化币种下拉列表选项
		initCurrencySel2();
		// 初始化币种下拉列表选项
		function initCurrencySel2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleCurrencyList';
			var stoRelevance = {};
			$.ajax({
				type : "POST",
				dataType : "json",
				data : {
					param : JSON.stringify(stoRelevance)
				},
				url : url,
				success : function(serverResult, textStatus) {
					// 1、作成币种的选项
					$('#currency2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
							if(goodsId){
								$('#currency2').select2("val","${stoExchange.stoGoodSelectFrom.currency}");
							}else{
								$('#currency2').select2("val",serverResult[0].value);
							}
							init = true;
						}
					// 2、作成类别的选项
					currencyChangeHandler2();
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		

		// 二、币种变化时的处理
		$('#currency2').change(currencyChangeHandler2);
		// 币种变化时的处理
		function currencyChangeHandler2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleClassificationList';
			var stoRelevance = {};
			stoRelevance.currency = $("#currency2").val();
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(stoRelevance)
				},
				success : function(serverResult, textStatus) {
					
					// 1、作成类别的选项
					$('#classification2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
							if(goodsId && init){
								$('#classification2').select2("val","${stoExchange.stoGoodSelectFrom.classification}");
							}else{
								$('#classification2').select2("val",serverResult[0].value);
							}
						}
					// 2、作成套别的选项
					classificationChangeHandler2();
					if(stoRelevance.currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
						// 人民币的场合，显示“套别”
						$("#setsGroup2").show();
					}else{
						// 其他币种的场合，不显示“套别”
						$("#setsGroup2").hide();
						$("#edition2").select2("val","");
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}

		
		// 三、类别变化时的处理
		$('#classification2').change(
				function(){
					var currency = $("#currency2").val();
					if(currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
						// 人民币的场合，类别变化联动套别
						classificationChangeHandler2();
					}else{
						// 外币的场合，类别变化联动材质
						editionChangeHandler2();
					}
				});
		// 类别变化时的处理
		function classificationChangeHandler2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleSetsList';
			var stoRelevance = {};
			stoRelevance.currency = $("#currency2").val();
			stoRelevance.classification = $("#classification2").val();
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(stoRelevance)
				},
				success : function(serverResult, textStatus) {
					// 1、作成套别的选项
					$('#edition2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
							
							
							if(goodsId&& init){
								$('#edition2').select2("val","${stoExchange.stoGoodSelectFrom.edition}");
							}else{
								$('#edition2').select2("val",serverResult[0].value);
							}
						}
					// 2、作成材质的选项
					editionChangeHandler2();
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		
		// 四、套别变化时的处理
		$('#edition2').change(editionChangeHandler2);
		// 套别变化时的处理
		function editionChangeHandler2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleCashList';
			var stoRelevance = {};
			stoRelevance.currency = $("#currency2").val();
			stoRelevance.classification = $("#classification2").val();
			stoRelevance.sets = $("#edition2").select2("val");
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(stoRelevance)
				},
				success : function(serverResult, textStatus) {
					// 1、作成材质的选项
					$('#cash2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
														
							if(goodsId&& init){
								$('#cash2').select2("val","${stoExchange.stoGoodSelectFrom.cash}");
							}else{
								$('#cash2').select2("val",serverResult[0].value);
							}
						}
					// 2、作成面值的选项
					cashChangeHandler2();
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		// 五、材质变化时的处理
		$('#cash2').change(cashChangeHandler2);
		// 材质变化时的处理
		function cashChangeHandler2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleDenominationList';
			var stoRelevance = {};
			stoRelevance.currency = $("#currency2").val();
			stoRelevance.classification = $("#classification2").val();
			stoRelevance.sets = $("#edition2").val();
			stoRelevance.cash = $("#cash2").val();
			stoRelevance.currencyRefCode = $("#currency2").select2("data").refCode;
			stoRelevance.cashRefCode = $("#cash2").select2("data").refCode;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(stoRelevance)
				},
				success : function(serverResult, textStatus) {
					// 1、作成面值的选项
					$('#denomination2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
							if(goodsId&& init){
								$('#denomination2').select2("val","${stoExchange.stoGoodSelectFrom.denomination}");
							}else{
								$('#denomination2').select2("val",serverResult[0].value);
							}
						}
					// 2、作成单位的选项
					denominationChangeHandler2();
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		// 六、面值变化时的处理
		$('#denomination2').change(denominationChangeHandler2);
		// 面值变化时的处理
		function denominationChangeHandler2(){
			var url = '${ctx}/store/v01/stoRelevance/getReleUnitList';
			var stoRelevance = {};
			stoRelevance.currency = $("#currency2").val();
			stoRelevance.classification = $("#classification2").val();
			stoRelevance.sets = $("#edition2").val();
			stoRelevance.cash = $("#cash2").val();
			stoRelevance.denomination = $("#denomination2").val();
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(stoRelevance)
				},
				success : function(serverResult, textStatus) {
					// 1、作成单位的选项
					$('#unit2').select2({
						width:'163px',
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format
					});
						// 新增物品的场合
						if(serverResult.length > 0 && serverResult[0] != null){
							if(goodsId&& init){
								$('#unit2').select2("val","${stoExchange.stoGoodSelectFrom.unit}");
							}else{
								$('#unit2').select2("val",serverResult[0].value);
							}
						}
					// 计算物品价值
					getStoNum();
					init = false;
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		// 七、单位变化时的处理
		$('#unit2').change(unitChangeHandler2);
		// 面值变化时的处理
		function unitChangeHandler2(){
			// 计算物品价值
			getStoNum();
			init = false;
		}
		
		// 八、数量变化处理
		$('#changeGoodsNum').change(changeGoodsNum);
		// 面值变化时的处理
		function changeGoodsNum(){
			// 计算物品价值
			getStoNum();
		}
		
		function getStoNum(){
			if(isNaN($('#changeGoodsNum').val())){
				return;
			}
			var url = '${ctx}/store/v01/stoExchange/getstoNum';
			var stoGoodSelectFrom= {};
			stoGoodSelectFrom.currency = $("#currency2").val();
			stoGoodSelectFrom.classification = $("#classification2").val();
			stoGoodSelectFrom.edition = $("#edition2").val();
			stoGoodSelectFrom.cash = $("#cash2").val();
			stoGoodSelectFrom.denomination = $("#denomination2").val();
			stoGoodSelectFrom.unit = $("#unit2").val();
			stoGoodSelectFrom.moneyNumber = $("#changeGoodsNum").val()==""?0:$("#changeGoodsNum").val();
			$.ajax({
			    type: 'POST',
			    dataType : "json",
			    url: url,
			    data : {param : JSON.stringify(stoGoodSelectFrom)},
			    success: function(serverResult, textStatus) {
			    	$('#stoNum').val(serverResult.surplusStoNum);
			    	$('#amount').val(serverResult.amount);
			    },
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		// 兑换		
		$("#btnSubmit").click(function(){
			check();
			$("#inputForm").attr("action", "${ctx}/store/v01/stoExchange/save");
			$("#inputForm").submit();
		});
	});
	
	//验证
	function check(){
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
	}
	// 添加
	function addDetail() {
		// 设置Action跳转路径
		$("#inputForm").attr("action", "${ctx}/store/v01/stoExchange/add");
		if ($("#inputForm").valid()) {
			$.post($("#inputForm").attr("action"), $("#inputForm").serialize(),
					function(data) {
						if (data.errorMessage != null) {
							top.$.jBox.tip(data.errorMessage, "error");
						}
						$("#detailDiv").load("${ctx}/store/v01/stoExchange/getDetailList");
					}, "json");
		}
	}
	
	// 删除现金详细详细
	function deleteDetail(goodsId){
		$("#inputForm").attr("action", "${ctx}/store/v01/stoExchange/delete?goodsId=" + goodsId);
		$.post($("#inputForm").attr("action"), $("#inputForm").serialize(),
				function(data) {
					if (data.errorMessage != null) {
						top.$.jBox.tip(data.errorMessage, "error");
					}
					$("#detailDiv").load("${ctx}/store/v01/stoExchange/getDetailList");
				}, "json");
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoExchange/"><spring:message
					code="store.goodsExchange.list" /></a></li>
		<li class="active">
			<a href="#" onclick="javascript:return false;">
				<shiro:lacksPermission name="store:exchange:edit">
					<spring:message code="store.goodsExchange" /><spring:message code="common.add" />
				</shiro:lacksPermission>
		</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoExchange"
		action="" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<div class="breadcrumb form-search" >
			<%--兑换原始物品 --%>
			<label><spring:message code="store.fromGoods" />：</label>
			<div class="clear"></div>	
			<div style="display:inline-block">
			<%-- 币种 --%>
			<div class="control-group item" >
				<label class="control-label" style="width:80px;"><spring:message code="common.currency" />：</label>
				<label >
				    <form:input id="currency2" path="stoGoodSelectFrom.currency" type="hidden"/>
				</label>
			</div>
			<%-- 类别 --%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="common.classification" />：</label>
				<label>
					<form:input id="classification2" path="stoGoodSelectFrom.classification" type="hidden"/>
				</label>
			</div>
			<%-- 套别 --%>
			<div class="control-group item" id = "setsGroup2">
				<label class="control-label" style="width:80px;"><spring:message code="common.edition" />：</label>
				<label>
				    <form:input id="edition2" path="stoGoodSelectFrom.edition" type="hidden"/>
				</label>
			</div>
			<div class="clear"></div>
			<%-- 材质--%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="common.cash" />：</label>
				<label>
				    <form:input id="cash2" path="stoGoodSelectFrom.cash" type="hidden"/>
				</label>
			</div>
			<%-- 面值 --%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="common.denomination" />：</label>
				<label>
				    <form:input id="denomination2" path="stoGoodSelectFrom.denomination" type="hidden"/>
				</label>
			</div>
			<%-- 单位 --%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="common.units" />：</label>
				<label>
				    <form:input id="unit2" path="stoGoodSelectFrom.unit" type="hidden"/>
				</label>
			</div>
			<div class="clear"></div>
			<%-- 库存数量 --%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="store.surplusStock" />：</label>
				<label>
					<form:input id="stoNum" path="stoNum" htmlEscape="false" maxlength="9" style="width:150px;"  readonly="true" />
				</label>
		    </div>
			<%-- 数量 --%>
			<div class="control-group item">
				<label class="control-label" style="width:80px;"><spring:message code="common.number" />：</label>
				<label>
					<form:input id="changeGoodsNum" path="stoGoodSelectFrom.moneyNumber" htmlEscape="false" maxlength="5" style="width:150px;" class="digits required" />
					<span class="help-inline"><font color="red">*</font> </span>
				</label>
		    </div>
		    <%-- 总价值 --%>
			<div class="control-group item">
				<%--<label class="control-label" style="width:120px;"><spring:message code="common.totalMoney" />：</label>--%>
				<label>
					<form:input id="amount" path="amount" htmlEscape="false" maxlength="11"  style="width:150px;" readonly="true" />
				</label>
		    </div>
		    <%-- 物品ID --%>
		    <form:hidden id="goodsId" path="changeGoods.id"/>
			</div>
		    <div class="clear"></div>
	    </div>
	    
	    <div class=" breadcrumb form-search">
		    <%--兑换目标物品 --%>
		    <label><spring:message code="store.toGoods" />：</label>
		    <div class="clear"></div>	
		    <sys:goodselect type="good_manager"/>
		    
			<div class="clear"></div>
			<%-- 数量 --%>
			<div class="control-group item">
				<label class="control-label" style="width:120px;"><spring:message code="common.number" />：</label>
				<div class="controls"  style="margin-left:130px;">
					<form:input id="toMoneyNumber" path="stoGoodSelect.moneyNumber" htmlEscape="false" maxlength="5" style="width:150px;" class="digits required" />
					<span class="help-inline"><font color="red">*</font> </span>
					<input id="add" style="margin-left:67px;" class="btn btn-primary" type="button" onclick="addDetail()" value="<spring:message code='common.add'/>" />
				</div>
		    </div>
		    <div class="clear"></div>
	   	</div>

	   	<%-- 出库现金详细信息 --%>
	    <div id="detailDiv" style="overflow:auto;overflow-x:hidden;">
		</div>
		 
		<div class="form-actions">
			<shiro:hasPermission name="store:stoExchange:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.exchange'/>" />&nbsp;
			</shiro:hasPermission>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button" 
				value="<spring:message code='common.return'/>" 
				onclick="window.location.href='${ctx}/store/v01/stoExchange/back'"/>
		</div>
	</form:form>
	
</body>
</html>
