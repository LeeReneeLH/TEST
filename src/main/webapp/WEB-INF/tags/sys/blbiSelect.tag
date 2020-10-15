<!-- 共通组件：业务画面用，数据来源为物品表的重空物品-->
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="type" type="java.lang.String"
	description="页面类型"%>
<%@ attribute name="blankBillKindReserve" type="java.lang.String"
	description="‘重空分类’保留项，逗号分隔"%>
<%@ attribute name="blankBillKindRemove" type="java.lang.String"
	description="‘重空分类’移除项，逗号分隔"%>
<%@ attribute name="blankBillTypeReserve" type="java.lang.String"
	description="‘重空类型’保留项，逗号分隔"%>
<%@ attribute name="blankBillTypeRemove" type="java.lang.String"
	description="‘重空类型’移除项，逗号分隔"%>

	
<script type="text/javascript">

// 物品ID
var goodsId = $("#id").val();

$(function() {
	// 一、初始化重空分类下拉列表选项
	makeBlankBillKindOptions();
	// 二、重空分类变化时的处理
	$('#blankBillKind').change(makeBlankBillTypeOptions);
	// 三、重空类型变化时的处理
	$('#blankBillType').change(genGoodsName);
});


/**
 * 加载select2下拉列表选项用
 */
function format(item) 
{
	return item.label;
}

/**
 * 作成重空分类选项
 */
function makeBlankBillKindOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getBlbiKindList';
	var stoBlankBillSelect = {};
	if($("#blankBillKindReserve").val()){
		stoBlankBillSelect.blankBillKindReserve = $("#blankBillKindReserve").val();
	}
	if($("#blankBillKindRemove").val()){
		stoBlankBillSelect.blankBillKindRemove = $("#blankBillKindRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoBlankBillSelect)
		},
		success : function(serverResult, textStatus) {
			// 1、作成重空分类的选项
			$('#blankBillKind').select2({
				width:'163px',
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、重空分类默认值
			if(goodsId){
				// 编辑物品的场合
				$('#blankBillKind').select2("val","${stoGoods.stoBlankBillSelect.blankBillKind}");
				$('#blankBillKind').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				if(serverResult.length > 0 && serverResult[0] != null){
					// 有选项时，默认选择第一个选项
					$('#blankBillKind').select2("val",serverResult[0].value);
				}else{
					// 没有选项时，清空赋值
					$('#blankBillKind').val("");
				}
			}
			// 3、作成重空类型的选项
			makeBlankBillTypeOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeBlankBillKindOptions error")
			//window.parent.location.href = "${ctx}/views/error/500.jsp";
			//window.location.href = "http://localhost:8080/frame/views/error/500.jsp";
		}
	});
}

/**
 * 作成重空类型选项
 */
function makeBlankBillTypeOptions(){
	var url = '${ctx}/store/v01/stoRelevance/getBlbiTypeList';
	var stoBlankBillSelect = {};
	stoBlankBillSelect.blankBillKind = $("#blankBillKind").val();
	//if($("#blankBillKind").select2("data")){
	//	stoBlankBillSelect.blankBillKind = $("#blankBillKind").select2("data").value;
	//}
	if($("#blankBillTypeReserve").val()){
		stoBlankBillSelect.blankBillTypeReserve = $("#blankBillTypeReserve").val();
	}
	if($("#blankBillTypeRemove").val()){
		stoBlankBillSelect.blankBillTypeRemove = $("#blankBillTypeRemove").val();
	}
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {
			param : JSON.stringify(stoBlankBillSelect)
		},
		success : function(serverResult, textStatus) {
			
			// 1、作成重空类型的选项
			$('#blankBillType').select2({
				width:'163px',
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、重空类型默认值
			if(goodsId){
				// 编辑物品的场合
				$('#blankBillType').select2("val","${stoGoods.stoBlankBillSelect.blankBillType}");
				$('#blankBillType').attr("readOnly","readOnly");
			}else{
				// 新增物品的场合
				if(serverResult.length > 0 && serverResult[0] != null){
					// 有选项时，默认选择第一个选项
					$('#blankBillType').select2("val",serverResult[0].value);
				}else{
					// 没有选项时，清空赋值
					$('#blankBillType').val("");
				}
			}
			
			// 3、生成物品名称和描述
			genGoodsName();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeBlankBillTypeOptions error")
			//window.parent.location.href = "${ctx}/views/error/500.jsp";
			//window.location.href = "http://localhost:8080/frame/views/error/500.jsp";
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
	// 重空分类
	var blankBillKindName = "";
	if($("#blankBillKind").select2("data")){
		blankBillKindName = $("#blankBillKind").select2("data").label;
	}
	var goodsName = blankBillKindName;
	
	// 重空类型
	var blankBillTypeName = "";
	if($("#blankBillType").select2("data")){
		blankBillTypeName = $("#blankBillType").select2("data").label;
	}
	if(blankBillTypeName){
		goodsName += "_" + blankBillTypeName;
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
<!-- ‘重空分类’保留项，逗号分隔 -->
<input id="blankBillKindReserve" type="hidden" value="<%=blankBillKindReserve == null ? "" : blankBillKindReserve%>"/>
<!-- ‘重空分类’移除项，逗号分隔 -->
<input id="blankBillKindRemove" type="hidden" value="<%=blankBillKindRemove == null ? "" : blankBillKindRemove%>"/>
<!-- ‘重空类别’保留项，逗号分隔 -->
<input id="blankBillTypeReserve" type="hidden" value="<%=blankBillTypeReserve == null ? "" : blankBillTypeReserve%>"/>
<!-- ‘重空类别’移除项，逗号分隔 -->
<input id="blankBillTypeRemove" type="hidden" value="<%=blankBillTypeRemove == null ? "" : blankBillTypeRemove%>"/>

<div id="blankBillSelect">
<%-- 重空分类 --%>
<div class="control-group item" >
	<label class="control-label" style="width:120px;"><spring:message code="common.importantEmpty.kind" />：</label>
	<div class="controls" style="margin-left:130px; width:270px;">
		<form:input id="blankBillKind" path="stoBlankBillSelect.blankBillKind"/>
		<span class="help-inline"><font color="red">*</font> </span>
	</div>
</div>
<div class="clear"></div>
<%-- 重空类别 --%>
<div class="control-group item">
	<label class="control-label" style="width:120px;"><spring:message code="common.importantEmpty.type" />：</label>
	<div class="controls" style="margin-left:130px; width:270px;">
		<form:input id="blankBillType" path="stoBlankBillSelect.blankBillType" />
		<span class="help-inline"><font color="red">*</font> </span>
	</div>
</div>
<div class="clear"></div>
</div>
