<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<script type="text/javascript">

$(function() {
	//初始化下拉菜单
	makeOption();
	
	//所属城市下拉菜单作成
	$("#provinceCode").on("change",function(){
		var provinceCode = $("#provinceCode").val();
		
		if(provinceCode!=''){
			//省份栏有值刷新城市下拉菜单
			$('#cityCode').val("");
			makeCityOption();
			
			$('#countyCode').val("");
			$('#countyCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: [], text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				formatSelection: format,
				formatResult: format
			});
			
		}else{
			//省份栏无值清空城市下拉菜单
			$('#cityCode').val("");
			$('#cityCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: [], text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				formatSelection: format,
				formatResult: format
			});
			
			//省份栏无值清空县区下拉菜单
			$('#countyCode').val("");
			$('#countyCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: [], text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				formatSelection: format,
				formatResult: format
			});
		}
		
	});
	
	//所属县下拉菜单作成
	$("#cityCode").on("change",function(){
		var cityCode=$("#cityCode").val();
		if(cityCode != ''){
			//城市栏有值刷新县区下拉菜单
			$('#countyCode').val("");
			makeCountyOption();
		}else{
			//城市栏无值清空县区列表
			$('#countyCode').val("");
			$('#countyCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: [], text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				formatSelection: format,
				formatResult: format
			});
		}
		
	});
	
})

//下拉菜单初始项作成
function makeOption() {
	
	makeProOption();
	//当前省份有值则初始化城市下拉菜单，无值时不显示城市数据（修改页用）
	var provinceCode = $("#provinceCode").val();
	if(provinceCode!=''){
		makeCityOption();
		//当前城市有值则初始化县级下拉菜单，无值时不显示数据（修改页用）
		var cityCode = $("#cityCode").val();
		if(cityCode != ''){
			makeCountyOption();
		}else{
			
			$('#countyCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: [], text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				formatSelection: format,
				formatResult: format
			});
		}
	}else{
		
		$('#cityCode').select2({
			containerCss:{width:'163px',display:'inline-block'},
			data:{ results: [], text: 'text' },
			//请选择
			placeholder: "<spring:message code='common.select'/>",
			formatSelection: format,
			formatResult: format
		});
		
		$('#countyCode').select2({
			containerCss:{width:'163px',display:'inline-block'},
			data:{ results: [], text: 'text' },
			//请选择
			placeholder: "<spring:message code='common.select'/>",
			formatSelection: format,
			formatResult: format
		});
	}
	
	
}

//省份下拉菜单作成
function makeProOption() {
	var url="${ctx}/sys/province/getSelect2ProData";
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		success : function(data) {
			// 1、作成省份的选项
			$('#provinceCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: data, text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				allowClear  : true,
				formatSelection: format,
				formatResult: format
			});
			},
	
		error : function() {
			 // 系统内部异常，请稍后再试或与系统管理员联系
			alertx("<spring:message code='message.E0101'/>")
		}
	});
}

//城市下拉菜单作成
function makeCityOption() {
	var url="${ctx}/sys/city/getSelect2CityData";
	var provinceCode=$("#provinceCode").val();
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {"provinceCode":provinceCode},
		
		success : function(data) {
			// 1、作成城市的选项
			$('#cityCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: data, text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				allowClear  : true,
				formatSelection: format,
				formatResult: format
			});
			
			},
			
		error : function() {
			// 系统内部异常，请稍后再试或与系统管理员联系
			alertx("<spring:message code='message.E0101'/>")
		}
	});
}

//区县下拉菜单作成
function makeCountyOption() {
	var url="${ctx}/sys/county/getSelect2CountyData";
	var provinceCode=$("#provinceCode").val();
	var cityCode=$("#cityCode").val();
	//alert(cityCode);
	$.ajax({
		type : "POST",
		dataType : "json",
		url : url,
		data : {"provinceCode":provinceCode,"cityCode":cityCode},
		
		success : function(data) {
			// 1、作成城市的选项
			$('#countyCode').select2({
				containerCss:{width:'163px',display:'inline-block'},
				data:{ results: data, text: 'text' },
				//请选择
				placeholder: "<spring:message code='common.select'/>",
				allowClear  : true,
				formatSelection: format,
				formatResult: format
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
function format(item) 
{
	return item.text;
}

</script>

<div class="control-group">
	<label class="control-label"><spring:message code="common.province" />：</label>
	<div class="controls">
		<form:input type="text" path="provinceCode" cssStyle="width : 210px;" />
	</div>
</div>

<div class="control-group">
	<label class="control-label"><spring:message code="common.city" />：</label>
	<div class="controls">
		<form:input type="hidden" path="cityCode" cssStyle="width : 210px;" />
	</div>
</div>

<div class="control-group">
	<label class="control-label"><spring:message code="common.county" />：</label>
	<div class="controls">
		<form:input type="hidden" path="countyCode" cssStyle="width : 210px;" />
	</div>
</div>

<div class="clear"></div>