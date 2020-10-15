<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 差错管理 -->
	<title><spring:message code="clear.clErrorInfo.title" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			calculate();
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
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
			/* 当客户名称改变时自动填写封签单位  修改人:sg 修改日期:2017-12-21 begin*/
			$("#custNo").change(function(){
				$("#seelOrg").val($("#custNo option:selected").text());
			})
			/* end */
		});
		//计算差错金额
		function calculate(){
			var denomination=$('#denomination').val();
			var count=$('#count').val();
			var subErrorType=$('#subErrorType').val();
			if (denomination==""||count==""||subErrorType==""||isNaN(count)||count.indexOf(".")>=0) {
				$('#errorMoneyView').val("");
				return;
			}
			$.ajax({
				url : ctx + '/clear/v03/clErrorInfo/getDenominationValue?denomination=' + denomination,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					var sum=count*data;
					if (subErrorType=="01") {
						sum=sum/2;
					}
					$('#errorMoneyView').val(formatCurrencyFloat(sum));
					$('#errorMoney').val(toDecimal2(sum));
				},
				error : function(data, status, e) {
					
				}
			});
		}
		/**
		 * 将数值四舍五入(保留2位小数)后格式化成金额形式
		 *
		 * @param num 数值(Number或者String)
		 * @return 金额格式的字符串,如'1,234,567.45'
		 * @type String
		 */
		function formatCurrencyFloat(num) {
		    num = num.toString().replace(/\$|\,/g,'');
		    if(isNaN(num))
		    num = "0";
		    sign = (num == (num = Math.abs(num)));
		    num = Math.floor(num*100+0.50000000001);
		    cents = num%100;
		    num = Math.floor(num/100).toString();
		    if(cents<10)
		    cents = "0" + cents;
		    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
		    num = num.substring(0,num.length-(4*i+3))+','+
		    num.substring(num.length-(4*i+3));
		    return (((sign)?'':'-') + num + '.' + cents);
		}
		/**
		 * 强制保留两位小数
		 */
 		function toDecimal2(x) { 
      		var f = parseFloat(x); 
      		if (isNaN(f)) { 
        		return false; 
     		} 
		    var f = Math.round(x*100)/100; 
		    var s = f.toString(); 
		    var rs = s.indexOf('.'); 
		    if (rs < 0) { 
		    	rs = s.length; 
		        s += '.'; 
		    } 
		    while (s.length <= rs + 2) { 
		    	s += '0'; 
			} 
			return s; 
		} 
		
		// 腰条名章光标离开事件，触发返现封签名章 LLF 20171114
		function onblurStripToSeel(value) {
			$("#seelChap").val(value);
		}
		
		/*  类型：假币 默认“正常差错”，其他默认请选择 wzj 20171117 begin */
		function changeTypeToSub(value) {
			$("#subErrorType").select2().get(0).selectedIndex=1;
			$("#subErrorType").select2().get(0).selectedIndex=1;
		}
		/* end */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 差错处理列表 -->
		<li><a href="${ctx}/clear/v03/clErrorInfo/"><spring:message code="clear.clErrorInfo.list" /></a></li>
		<!-- 差错处理登记 -->
		<li class="active"><a href="${ctx}/clear/v03/clErrorInfo/form?id=${clErrorInfo.id}"><spring:message code="clear.clErrorInfo.register" /></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clErrorInfo" action="${ctx}/clear/v03/clErrorInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!--第一列 -->
		<div style="float:left;margin-left:10px">
			<!-- 客户名称 -->
			<div class="control-group">
					<label class="control-label"><spring:message code="clear.public.custName" />：</label>
					<div class="controls">
						<form:select path="custNo" id="custNo"
							class="input-large required" disabled="disabled">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${sto:getStoCustList('1,3',false)}"
								itemLabel="name" itemValue="id" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
						<form:hidden path="custName" />
					</div>
			</div>
			<!-- 类别-->
			<div class="control-group">
				<label class="control-label"><spring:message code='common.classification'/>：</label>
				<div class="controls">
					<form:select path="errorType" onchange="changeTypeToSub(this.value)" class="input-large required">		
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${fns:getDictList('clear_error_type')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 面值-->
			<div class="control-group">
				<label class="control-label"><spring:message code='common.denomination'/>：</label>
				<div class="controls">
					<form:select path="denomination" id="denomination"
								class="input-large required" onchange="calculate()">
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${sto:getGoodDictListWithFg('cnypden')}"
							itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 数量 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.number"/>：</label>
				<div class="controls">
					<form:input path="count" id="count" htmlEscape="false" maxlength="3" class="required digits" onkeyup="calculate()" onchange="calculate()"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--差错类别 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.errorType"/>：</label>
				<div class="controls">
					<form:select id="subErrorType" path="subErrorType" class="input-large required" onchange="calculate()">		
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${fns:getDictList('clear_subError_type')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 差错金额 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.amountError"/>：</label>
				<div class="controls">
					<form:input id="errorMoneyView" path="" readOnly="true" htmlEscape="false" maxlength="20" class="required" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
				<form:hidden id="errorMoney" path="errorMoney" htmlEscape="false"/>
			</div>
		</div>
		<!--第二列 -->
		<div style="float:left;">
			<!--版本 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.edition"/>：</label>
				<div class="controls">
					<form:input path="versionError" htmlEscape="false" maxlength="10" class=""/>
				</div>
			</div>
			<!--冠字号 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.crownSize"/>：</label>
				<div class="controls">
					<form:input path="sno" htmlEscape="false" maxlength="10" class=""/>
				</div>
			</div>
			<!--封签单位 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.sealUnit"/>：</label>
				<div class="controls">
					<form:input path="seelOrg" id="seelOrg" htmlEscape="false" maxlength="20" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 腰条名章 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.articleWaist"/>：</label>
				<div class="controls">
					<form:input path="stripChap" htmlEscape="false" maxlength="10" onblur="onblurStripToSeel(this.value)" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--封签名章 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.chapterSeal"/>：</label>
				<div class="controls">
					<form:input path="seelChap" htmlEscape="false" maxlength="10" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 业务类型 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.business.type"/>：</label>
				<div class="controls">
					<form:select path="busType" class="input-large required">		
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${fns:getFilterDictList('clear_businesstype',true,'08,09')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<!--第三列 -->
		<div style="float:left;">
			<!--清分人员 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.clearMan"/>：</label>
				<div class="controls">
					<form:select path="clearManNo" class="input-large required">			
						<form:option value=""><spring:message code="common.select" /></form:option>		
						<form:options items="${sto:getUsersByTypeAndOffice(clearManage,fns:getUser().office.id)}" 
								itemLabel="escortName" itemValue="user.id" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--现钞备付金管理员 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.provision.manager"/>：</label>
				<div class="controls">
					<form:select path="checkManNo" class="input-large required">			
						<%-- <form:option value=""><spring:message code="common.select" /></form:option> --%>		
						<form:options items="${sto:getUsersByTypeAndOffice(clearError,fns:getUser().office.id)}" 
								itemLabel="escortName" itemValue="id" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--发现时间 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.findDate"/>：</label>
				<div class="controls">
					<input name="findTime" type="text" readonly="readonly" maxlength="20" class="input-large Wdate required"
						value="<fmt:formatDate value="${clErrorInfo.findTime}" pattern="yyyy-MM-dd HH:mm"/>"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',isShowClear:true,maxDate:'%y-%M-%d %H:%m'});"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!--笔数
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.theNumber"/>：</label>
				<div class="controls">
					<form:input path="strokeCount" htmlEscape="false" maxlength="3" class="digits required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			 -->
			<!--工位编号
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.locationNumber"/>：</label>
				<div class="controls">
					<form:input path="stationNo" htmlEscape="false" maxlength="10" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			 -->
			<!--封签日期 -->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clErrorInfo.sealTheDate"/>：</label>
				<div class="controls">
					<input id="seelDate"  name="seelDate" type="text" readonly="readonly" maxlength="20" class="input-large Wdate required" 
						value="<fmt:formatDate value="${clErrorInfo.seelDate}" pattern="yyyy-MM-dd"/>" 
						onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'});"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</div>
		<!-- 备注 -->
		<div style="float:left; clear:both;">
			<div class="control-group">
				<label class="control-label"><spring:message code="common.remark"/>：</label>
				<div class="controls">
					<form:textarea path="remarks"  style="width:1030px;"  htmlEscape="false" rows="3" maxlength="80" class="input-medium "/>
				</div>
			</div>
		</div>
		<br style=" clear:both;"/>
		<!--保存返回 -->
		<!-- <div style="float:left; clear:both;"> -->
			<div class="form-actions" style="width:100%;">
				<shiro:hasPermission name="clear:v03:clErrorInfo:edit"><input id="btnSubmit"  style="margin-left: 1000px"  class="btn btn-primary" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/clear/v03/clErrorInfo/back'"/>
			</div>
	<!-- 	</div> -->
	</form:form>
</body>
</html>