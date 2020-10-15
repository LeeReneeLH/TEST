<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	
<script type="text/javascript">

$(function() {
	if($('#messageType').val()=='01'){
		$("#businessType").attr("readOnly","readOnly");
		$("#businessStatus").attr("readOnly","readOnly");
	}
	makeBusinessStatusOptions();
	// 消息类型变化时的处理
	$('#messageType').change(makeBusinessTypeOptions);
	// 业务类型变化时的处理
	$('#businessType').change(makeBusinessStatusOptions);
});

function makeBusinessTypeOptions(){
	if($('#messageType').val()=='01'){
		$("#businessType").attr("readOnly","readOnly");
		$("#businessStatus").attr("readOnly","readOnly");
	}else{
		$("#businessType").removeAttr("readOnly");
		$("#businessStatus").removeAttr("readOnly");
		$.ajax({
			url : '${ctx}/sys/messageRelevance/makeBusinessTypeOptions?messageType=' + $("#messageType").val(),
			type : 'Post',
			contentType : 'application/json; charset=UTF-8',
			async : false,
			cache : false,
			dataType : 'json',
			success : function(res) {
				$("#businessType option").remove();
				for(i=0;i<res.dictValueList.length;i++){
					$("#businessType").append("<option value='"+res.dictValueList[i]+"'>"+res.dictLabelList[i]+"</option>")
				}
				$("#businessType").prepend("<option value='' selected='selected'>请选择</option>")
			}
		});
	}
}

function makeBusinessStatusOptions(){
	$.ajax({
		url : '${ctx}/sys/messageRelevance/makeBusinessStatusOptions?businessType=' + $("#businessType").val(),
		type : 'Post',
		contentType : 'application/json; charset=UTF-8',
		async : false,
		cache : false,
		dataType : 'json',
		success : function(res) {
			$("#businessStatus option").remove();
			for(i=0;i<res.dictValueList.length;i++){
				$("#businessStatus").append("<option value='"+res.dictValueList[i]+"'>"+res.dictLabelList[i]+"</option>")
			}
			$("#businessStatus").prepend("<option value='' selected='selected'>请选择</option>")
		}
	});
}

</script>
<div id="messageTypeSelect">
	<form:select path="messageType" itemLabel="label" itemValue="value"
		class="input-medium required">
		<form:option value="">
			<spring:message code="common.select" />
		</form:option>
		<form:options id="messageType" items="${fns:getDictList('all_messageType')}"
			itemLabel="label" itemValue="value" htmlEscape="false" />
	</form:select>
	<form:select path="businessType" itemLabel="label" itemValue="value"
		class="input-medium">
		<form:option value="">
			<spring:message code="common.select" />
		</form:option>
		<form:options id="businessType" items="${fns:getDictList('all_businessType')}"
			itemLabel="label" itemValue="value" htmlEscape="false" />
	</form:select>
	<form:select path="businessStatus" itemLabel="label" itemValue="value"
		class="input-medium">
		<form:option value="">
			<spring:message code="common.select" />
		</form:option>
		<form:options id="businessStatus" items="${fns:getDictList('all_status')}"
			itemLabel="label" itemValue="value" htmlEscape="false" />
	</form:select>
	<span class="help-inline"><font color="red">*</font> </span>
</div>