<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="type" type="java.lang.String"
	description="页面类型"%>
<%@ attribute name="currencyReserve" type="java.lang.String"
	description="‘币种’保留项，逗号分隔"%>
<%@ attribute name="currencyRemove" type="java.lang.String"
	description="‘币种’移除项，逗号分隔"%>
<%@ attribute name="classificationReserve" type="java.lang.String"
	description="‘类别’保留项，逗号分隔"%>
<%@ attribute name="classificationRemove" type="java.lang.String"
	description="‘类别’移除项，逗号分隔"%>
<%@ attribute name="editionReserve" type="java.lang.String"
	description="‘套别’保留项，逗号分隔"%>
<%@ attribute name="editionRemove" type="java.lang.String"
	description="‘套别’移除项，逗号分隔"%>
<%@ attribute name="cashReserve" type="java.lang.String"
	description="‘材质’保留项，逗号分隔"%>
<%@ attribute name="cashRemove" type="java.lang.String"
	description="‘材质’移除项，逗号分隔"%>
<%@ attribute name="denominationReserve" type="java.lang.String"
	description="‘面值’保留项，逗号分隔"%>
<%@ attribute name="denominationRemove" type="java.lang.String"
	description="‘面值’移除项，逗号分隔"%>
<%@ attribute name="unitReserve" type="java.lang.String"
	description="‘单位’保留项，逗号分隔"%>
<%@ attribute name="unitRemove" type="java.lang.String"
	description="‘单位’移除项，逗号分隔"%>
	
<script type="text/javascript">

// 物品ID
var goodsId = $("#id").val();

$(function() {
	// 一、初始化币种下拉列表选项
	makeCurrencyOptions();
	// 二、币种变化时的处理
	$('#currency').change(makeClassificationOptions);
	// 三、类别变化时的处理
	$('#classification').change(
			function(){
				var currency = $("#currency").val();
				if(currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
					// 人民币的场合，类别变化联动套别
					makeEditionOptions();
				}else{
					// 外币的场合，类别变化联动材质
					makeCashOptions();
				}
			});
	// 四、套别变化时的处理
	$('#edition').change(makeCashOptions);
	// 五、材质变化时的处理
	$('#cash').change(makeDenOptions);
	// 六、面值变化时的处理
	$('#denomination').change(makeUnitOptions);
	// 七、单位变化时的处理
	$('#unit').change(unitChangeHandler);
});


/**
 * 加载select2下拉列表选项用
 */
function format(item) 
{
	return item.label;
}

/**
 * 作成币种选项
 */
function makeCurrencyOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleCurrencyList';
	var stoRelevance = {};
	if($("#currencyReserve").val()){
		stoRelevance.currencyReserve = $("#currencyReserve").val();
	}
	if($("#currencyRemove").val()){
		stoRelevance.currencyRemove = $("#currencyRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			// 1、作成币种的选项
			$('#currency').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、币种默认值
			if(goodsId){
				// 编辑物品的场合
				$('#currency').select2("val","${stoGoods.stoGoodSelect.currency}");
				$('#currency').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#currency').select2("val",serverResult[0].value);
				}
			}
			// 3、作成类别的选项
			makeClassificationOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeCurrencyOptionsError")
			//window.parent.location.href = "${ctx}/views/error/500.jsp";
			//window.location.href = "http://localhost:8080/frame/views/error/500.jsp";
		}
	});
}

/**
 * 作成类别选项
 */
function makeClassificationOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleClassificationList';
	var stoRelevance = {};
	stoRelevance.currency = $("#currency").val();
	if($("#classificationReserve").val()){
		stoRelevance.classificationReserve = $("#classificationReserve").val();
	}
	if($("#classificationRemove").val()){
		stoRelevance.classificationRemove = $("#classificationRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			
			// 1、作成类别的选项
			$('#classification').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、类别默认值
			if(goodsId){
				// 编辑物品的场合
				$('#classification').select2("val","${stoGoods.stoGoodSelect.classification}");
				$('#classification').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#classification').select2("val",serverResult[0].value);
				}
			}
			// 3、作成套别的选项
			makeEditionOptions();
			// 4、判断是否显示套别
			if(stoRelevance.currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
				// 人民币的场合，显示“套别”
				$("#setsGroup").show();
			}else{
				// 其他币种的场合，不显示“套别”
				$("#setsGroup").hide();
				$("#edition").select2("val","");
			}
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeClassificationOptionsError")
			//window.parent.location.href = "${ctx}/views/error/500.jsp";
			//window.location.href = "http://localhost:8080/frame/views/error/500.jsp";
		}
	});
}

/**
 * 作成套别选项
 */
function makeEditionOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleSetsList';
	var stoRelevance = {};
	stoRelevance.currency = $("#currency").val();
	stoRelevance.classification = $("#classification").val();
	if($("#editionReserve").val()){
		stoRelevance.editionReserve = $("#editionReserve").val();
	}
	if($("#editionRemove").val()){
		stoRelevance.editionRemove = $("#editionRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			// 1、作成套别的选项
			$('#edition').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			})
			// 2、套别默认值
			if(goodsId){
				// 编辑物品的场合
				$('#edition').select2("val","${stoGoods.stoGoodSelect.edition}");
				$('#edition').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#edition').select2("val",serverResult[0].value);
				}
			}
			// 3、作成材质的选项
			makeCashOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeEditionOptionsError")
		}
	});
}

/**
 * 作成材质选项
 */
function makeCashOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleCashList';
	var stoRelevance = {};
	stoRelevance.currency = $("#currency").val();
	stoRelevance.classification = $("#classification").val();
	stoRelevance.sets = $("#edition").select2("val");
	if($("#cashReserve").val()){
		stoRelevance.cashReserve = $("#cashReserve").val();
	}
	if($("#cashRemove").val()){
		stoRelevance.cashRemove = $("#cashRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			// 1、作成材质的选项
			$('#cash').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、材质默认值
			if(goodsId){
				// 编辑物品的场合
				$('#cash').select2("val","${stoGoods.stoGoodSelect.cash}");
				$('#cash').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#cash').select2("val",serverResult[0].value);
				}
			}
			// 3、作成面值的选项
			makeDenOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeCashOptionsError")
		}
	});
}

/**
 * 作成面值选项
 */
function makeDenOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleDenominationList';
	var stoRelevance = {};
	stoRelevance.currency = $("#currency").val();
	stoRelevance.classification = $("#classification").val();
	stoRelevance.sets = $("#edition").val();
	stoRelevance.cash = $("#cash").val();
	if($("#currency").select2("data")){
		stoRelevance.currencyRefCode = $("#currency").select2("data").refCode;
	}
	if($("#cash").select2("data")){
		stoRelevance.cashRefCode = $("#cash").select2("data").refCode;
	}
	if($("#denominationReserve").val()){
		stoRelevance.denominationReserve = $("#denominationReserve").val();
	}
	if($("#denominationRemove").val()){
		stoRelevance.denominationRemove = $("#denominationRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			// 1、作成面值的选项
			$('#denomination').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、面值默认值
			if(goodsId){
				// 编辑物品的场合
				$('#denomination').select2("val","${stoGoods.stoGoodSelect.denomination}");
				$('#denomination').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#denomination').select2("val",serverResult[0].value);
				}
			}
			// 3、作成单位的选项
			makeUnitOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeDenOptionsError")
		}
	});
}

/**
 * 作成单位选项
 */
function makeUnitOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getReleUnitList';
	var stoRelevance = {};
	stoRelevance.currency = $("#currency").val();
	stoRelevance.classification = $("#classification").val();
	stoRelevance.sets = $("#edition").val();
	stoRelevance.cash = $("#cash").val();
	stoRelevance.denomination = $("#denomination").val();
	if($("#unitReserve").val()){
		stoRelevance.unitReserve = $("#unitReserve").val();
	}
	if($("#unitRemove").val()){
		stoRelevance.unitRemove = $("#unitRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			// 1、作成“单位”的选项
			$('#unit').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、“单位”默认值
			if(goodsId){
				// 编辑物品的场合
				$('#unit').select2("val","${stoGoods.stoGoodSelect.unit}");
				$('#unit').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#unit').select2("val",serverResult[0].value);
				}
				// 计算物品价值、生成物品名称
				unitChangeHandler();
			}
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeUnitOptionsError")
		}
	});
}

// 面值变化时的处理
function unitChangeHandler(){
	// 计算物品价值
	calcGoodsVal();
	// 生成物品名称
	genGoodsName();
}

/**
 * 计算物品价值
 */
function calcGoodsVal(){
	// 判断物品价值输入框是否存在
	var $goodsVal = $('#goodsVal');
	if($goodsVal.length ==  0){
		// 不存在的场合
		return false;
	}
	// 修改物品时，不重新计算物品价值
	if(goodsId){
		// 编辑物品的场合
		return false;
	}
	// 增加物品时，如果类型为假币，则物品价值为0
	/* var $classification = $('#classification');
	if($classification.length != 0 
			&& $classification.val() == "${fns:getConfig('sto.goods.classification.counterfeit')}"){
		$goodsVal.val(0);
		return false;
	} */
	var url = '${ctx}/store/v01/goods/stoGoods/calcGoodsVal';
	var stoRelevance = {};
	stoRelevance.denomination = $("#denomination").val();
	stoRelevance.unit = $("#unit").val();
	if($("#currency").select2("data")){
		stoRelevance.currencyRefCode = $("#currency").select2("data").refCode;
	}
	if($("#cash").select2("data")){
		stoRelevance.cashRefCode = $("#cash").select2("data").refCode;
	}
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoRelevance)
		},
		success : function(serverResult, textStatus) {
			$goodsVal.val(serverResult);
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("calcGoodsValError")
		}
	});
}

/**
 * 生成物品名称和描述
 */
function genGoodsName(){
		
	// 判断物品名称和物品描述输入框是否存在
	var $goodsName = $('#goodsName');
	var $description = $('#description');
	if($goodsName.length == 0 || $description.length == 0){
		// 不存在的场合
		return false;
	}
	// 修改物品时，不重新生成
	if(goodsId){
		return false;
	}
	// 生成名称
	// 币种
	var currencyName = "";
	if($("#currency").select2("data")){
		currencyName = $("#currency").select2("data").label;
	}
	var goodsName = currencyName;
	
	// 类别
	var classificationName = "";
	if($("#classification").select2("data")){
		classificationName = $("#classification").select2("data").label;
	}
	if(classificationName){
		goodsName += "_" + classificationName;
	}
	// 套别
	var editionName ="";
	if($("#edition").select2("data")){
		var editionName = $("#edition").select2("data").label;
	}
	if(editionName){
		goodsName += "_" + editionName;
	}
	// 材质
	var cashName = "";
	if($("#cash").select2("data")){
		cashName = $("#cash").select2("data").label;
	}
	if(cashName){
		goodsName += "_" + cashName;
	}
	// 面值
	var denominationName = "";
	if($("#denomination").select2("data")){
		denominationName = $("#denomination").select2("data").label;
	}
	if(denominationName){
		goodsName += "_" + denominationName;
	}
	// 单位
	var unitName = "";
	if($("#unit").select2("data")){
		unitName = $("#unit").select2("data").label;
	}
	if(unitName){
		goodsName += "_" + unitName;
	}
	$goodsName.val(goodsName);
	$description.val(goodsName);
}
</script>
<style>
<!--
/* 输入项 */
.item {display: inline; float: left;}
/* 清除浮动 */
.clear {clear:both;}
/* 标签宽度 */
.label_width {width:120px;}
-->
</style>
<!-- ‘币种’保留项，逗号分隔 -->
<input id="currencyReserve" type="hidden" value="<%=currencyReserve == null ? "" : currencyReserve%>"/>
<!-- ‘币种’移除项，逗号分隔 -->
<input id="currencyRemove" type="hidden" value="<%=currencyRemove == null ? "" : currencyRemove%>"/>
<!-- ‘类别’保留项，逗号分隔 -->
<input id="classificationReserve" type="hidden" value="<%=classificationReserve == null ? "" : classificationReserve%>"/>
<!-- ‘类别’移除项，逗号分隔 -->
<input id="classificationRemove" type="hidden" value="<%=classificationRemove == null ? "" : classificationRemove%>"/>
<!-- ‘套别’保留项，逗号分隔 -->
<input id="editionReserve" type="hidden" value="<%=editionReserve == null ? "" : editionReserve%>"/>
<!-- ‘套别’移除项，逗号分隔 -->
<input id="editionRemove" type="hidden" value="<%=editionRemove == null ? "" : editionRemove%>"/>
<!-- ‘材质’保留项，逗号分隔 -->
<input id="cashReserve" type="hidden" value="<%=cashReserve == null ? "" : cashReserve%>"/>
<!-- ‘材质’移除项，逗号分隔 -->
<input id="cashRemove" type="hidden" value="<%=cashRemove == null ? "" : cashRemove%>"/>
<!-- ‘面值’保留项，逗号分隔 -->
<input id="denominationReserve" type="hidden" value="<%=denominationReserve == null ? "" : denominationReserve%>"/>
<!-- ‘面值’移除项，逗号分隔 -->
<input id="denominationRemove" type="hidden" value="<%=denominationRemove == null ? "" : denominationRemove%>"/>
<!-- ‘单位’保留项，逗号分隔 -->
<input id="unitReserve" type="hidden" value="<%=unitReserve == null ? "" : unitReserve%>"/>
<!-- ‘单位’移除项，逗号分隔 -->
<input id="unitRemove" type="hidden" value="<%=unitRemove == null ? "" : unitRemove%>"/>


<div id="goodSelect">
<%-- 币种 --%>
<div class="control-group item" >
	<label class="control-label" style="width:80px;"><spring:message code="common.currency" />：</label>
	<label>
		<form:input id="currency" type="hidden" path="stoGoodSelect.currency"/>
	</label>
</div>
<%-- 类别 --%>
<div class="control-group item">
	<label class="control-label" style="width:80px;"><spring:message code="common.classification" />：</label>
	<label>
		<form:input id="classification" type="hidden" path="stoGoodSelect.classification" />
	</label>
</div>
<%-- 套别 --%>
<div class="control-group item" id = "setsGroup">
	<label class="control-label" style="width:80px;"><spring:message code="common.edition" />：</label>
	<label>
		<form:input id="edition" type="hidden" path="stoGoodSelect.edition"/>
	</label>
</div>
<div class="clear"></div>
<%-- 材质--%>
<div class="control-group item">
	<label class="control-label" style="width:80px;"><spring:message code="common.cash" />：</label>
	<label>
		<form:input id="cash" type="hidden" path="stoGoodSelect.cash"/>
	</label>
</div>
<%-- 面值 --%>
<div class="control-group item">
	<label class="control-label" style="width:80px;"><spring:message code="common.denomination" />：</label>
	<label>
		<form:input id="denomination" type="hidden" path="stoGoodSelect.denomination"/>
	</label>
</div>
<%-- 单位 --%>
<div class="control-group item">
	<label class="control-label" style="width:80px;"><spring:message code="common.units" />：</label>
	<label>
		<form:input id="unit" type="hidden" path="stoGoodSelect.unit"/>
	</label>
</div>
<div class="clear"></div>
</div>
