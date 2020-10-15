<!-- 共通组件：重空查询条件 -->
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
			if(serverResult.length > 0 && serverResult[0] != null) {
				var select = '{"label":"请选择","value":"000000","id":"000000"}';
				var resultString = JSON.stringify(serverResult)
				resultString = resultString.substring(1,resultString.length);
				resultString = "[" + select + "," + resultString;
				serverResult = jQuery.parseJSON(resultString);
			} else {
				var select = '[{"label":"请选择","value":"000000","id":"000000"}]';
				serverResult = jQuery.parseJSON(select);
			}
			$('#blankBillKind').select2({
				width:'163px',
				placeholder: "请选择",
				containerCss:function(){
					return {display:"inline-block"};
				},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、重空分类默认值
			var blankBillKind = "${stoGoods.stoBlankBillSelect.blankBillKind}";
			if(blankBillKind){
				$('#blankBillKind').select2("val","${stoGoods.stoBlankBillSelect.blankBillKind}");
				// 编辑物品的场合
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#blankBillKind').select2("val",serverResult[0].value);
				}
			}
			// 3、作成重空类型的选项
			makeBlankBillTypeOptions();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeBlankBillKindOptions error");
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
			if(serverResult.length > 0 && serverResult[0] != null) {
				var select = '{"label":"请选择","value":"000000","id":"000000"}';
				var resultString = JSON.stringify(serverResult)
				resultString = resultString.substring(1,resultString.length);
				resultString = "[" + select + "," + resultString;
				serverResult = jQuery.parseJSON(resultString);
			} else {
				var select = '[{"label":"请选择","value":"000000","id":"000000"}]';
				serverResult = jQuery.parseJSON(select);
			} 
			$('#blankBillType').select2({
				width:'163px',
				placeholder: "请选择",
				containerCss:function(){
					return {display:"inline-block"};
				},
				data:{ results: serverResult, text: 'label' },
				formatSelection: format,
				formatResult: format
			});
			// 2、重空类型默认值
			var blankBillType = "${stoGoods.stoBlankBillSelect.blankBillType}";
			if(blankBillType){
				// 编辑物品的场合 begin 修改人：LLF 修改时间：2016-01-12 修改内容：解决查询后，切换分类，类型出现空值问题
				var flag = true;
				for(var i=0;i<serverResult.length;i++) {
					if(blankBillType == serverResult[i].value) {
						flag = false
						break;
					}
				}
				if(flag) {
					if(serverResult.length > 0 && serverResult[0] != null){
						$('#blankBillType').select2("val",serverResult[0].value);
					}
				} else {
					$('#blankBillType').select2("val","${stoGoods.stoBlankBillSelect.blankBillType}");
				}
				// end
			}else{
				// 新增物品的场合
				// 默认选择第一个选项
				if(serverResult.length > 0 && serverResult[0] != null){
					$('#blankBillType').select2("val",serverResult[0].value);
				}
			}
			// 3、生成物品名称和描述
			genGoodsName();
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			// TODO
			alert("makeBlankBillTypeOptions error");
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
<!-- ‘重空分类’保留项，逗号分隔 -->
<input id="blankBillKindReserve" type="hidden" value="<%=blankBillKindReserve == null ? "" : blankBillKindReserve%>"/>
<!-- ‘重空分类’移除项，逗号分隔 -->
<input id="blankBillKindRemove" type="hidden" value="<%=blankBillKindRemove == null ? "" : blankBillKindRemove%>"/>
<!-- ‘重空类别’保留项，逗号分隔 -->
<input id="blankBillTypeReserve" type="hidden" value="<%=blankBillTypeReserve == null ? "" : blankBillTypeReserve%>"/>
<!-- ‘重空类别’移除项，逗号分隔 -->
<input id="blankBillTypeRemove" type="hidden" value="<%=blankBillTypeRemove == null ? "" : blankBillTypeRemove%>"/>

<div id="blankBillSelect" style="float:left;margin-right:5px">
<%-- 重空分类 --%>
<label class="control-label"><spring:message code="common.importantEmpty.kind" />：</label>
<form:input id="blankBillKind" type="hidden" path="stoBlankBillSelect.blankBillKind"/>
<%-- 重空类别 --%>
<label class="control-label"><spring:message code="common.importantEmpty.type" />：</label>
<form:input id="blankBillType" type="hidden" path="stoBlankBillSelect.blankBillType" />
</div>
