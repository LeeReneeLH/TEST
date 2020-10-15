<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<!-- 柜员调账登记 -->
<title><spring:message code='clear.accountAdjust.title' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript"
	src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		//千分位分割 
		if(	document.getElementById("adjustMoney").value!=""){
			addComma(document.getElementById("adjustMoney"));
		}
		
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
		changeBankConnect($("#cashType").val());
		// 保存
		$("#btnSubmit").click(function() {			
			//页面检查
			var blnChk = chkFrom();
			if (blnChk == false){
				return false;
			}
			//交款人
			setSelectName('payTellerBy','payTellerName');
			//收款人
			setSelectName('reTellerBy','reTellerName');			
			//将金额去掉逗号
			update(document.getElementById("adjustMoney"))
			$("#inputForm").submit();			
		});
	});
	
	//页面检查
	function chkFrom() {	
		//-----------总金额检查---------------
		var strOutAmount=$("#adjustMoney").val(); 
		if (typeof(strOutAmount) != "undefined" && !isNaN(strOutAmount)) {
			if (strOutAmount <= 0) {
				//总金额小于等于零，不能保存！
            	alertx("<spring:message code='message.I7004' />");
				return false;
			}	
		}
		//交款收款人员重复检查
		var strPayTellerBy=$("#payTellerBy").val(); 
		var strReTellerBy=$("#reTellerBy").val(); 
		//--------银行交接人员重复检查------------
		/* 增加交收款人员不能为空验证 qph 2018-05-03 begin */
		if(strPayTellerBy == ""){
			alertx("交款人不能为空！");
			return false;
		}
		if(strReTellerBy == ""){
			alertx("收款人不能为空！");
			return false;
		}
		// end
		if (strPayTellerBy != "" && strPayTellerBy == strReTellerBy){
			//交款收款人员选择重复，不能保存！
			alertx("<spring:message code='message.I7017' />");
			return false;
		}
		return true;
	}
	
	//人员名称的设定
	function setSelectName(valueId, valueNm) {	
		$("#" + valueNm).val("");
		var selectValue=$("#" + valueId).val(); 
		if ((selectValue != undefined && selectValue != "")) {
			var selectText = $("#" + valueId).find("option:selected").text();
			$("#" + valueNm).val(selectText);
		}
	}
	
	//总金额格式化
	function upperCase(obj){	
		changeAmountToBig(obj.value,"outAmountBigRmb");
	}
	//移出事件
	function update(obj){	
		var money= changeAmountToNum(obj.value); 
		document.getElementById("adjustMoney").value=money;
	}
	//格式化 
	function upperCaseAll(obj){
		changeAmountToBig(obj.value,"outAmountBigRmb");
		addComma(obj);
	}
	//逗号方法
	function addComma(obj){
		var totalMoney=formatCurrencyFloat(obj.value);
		document.getElementById("adjustMoney").value=totalMoney;
	}
	//千分位用逗号分隔
	 function formatCurrencyFloat(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
			return (((sign) ? '' : '-') + num + '.' + cents);
		}
	//将金额转换成数字
	function changeAmountToNum(amount) {
		if (typeof (amount) != "undefined"&&amount != null) {
			amount = amount.toString().replace(/\$|\,/g, '');
		}
		return amount;
	}
	//金额转换成大写
	function changeAmountToBig(amount, toId) {
		if (amount == "" || isNaN(amount)) {
			$("#" + toId).val("");
			return;
		}else{
			if (!isNaN(amount) && amount==0){
				//零元整
				$("#" + toId).val("<spring:message code='message.I7008' />");
				return;
			}
		}
		
		$.ajax({
			url : ctx + '/clear/v03/clearAccountAdjustInfo/changRMBAmountToBig?amount=' + amount,
			type : 'post',
			dataType : 'json',
			success : function(data, status) {
				$("#" + toId).val(data.bigAmount);
			},
			error : function(data, status, e) {				
			}
		});
	}
	//根据选择金额类型（备付金，非备付金），获取相应人员
	function changeBankConnect(cashType){
		//清空交接人员内容
		$('#payTellerBy').select2("val",'');
		$('#reTellerBy').select2("val",'');
		$.ajax({
			url : "${ctx}/clear/v03/clearAccountAdjustInfo/changeBankConnect?cashType=" + cashType,
			type : "POST",
			async : false,	
			dataType : "json",
			success : function(serverResult, textStatus) {
				$('#payTellerBy').select2({
					containerCss:{width:'163px',display:'inline-block'},
					data:{ results: serverResult, text: 'label' },
					formatSelection: format,
					formatResult: format 
				});
				$('#reTellerBy').select2({
					containerCss:{width:'163px',display:'inline-block'},
					data:{ results: serverResult, text: 'label' },
					formatSelection: format,
					formatResult: format 
				});
				//收交款人员 保持
				var payTellerBy = $("#payTellerBy").val();
				var reTellerBy = $("#reTellerBy").val();
				if((payTellerBy!=null&& payTellerBy!='')&&(reTellerBy!=null && reTellerBy!="")){
					$('#payTellerBy').select2("val",payTellerBy);
					$('#reTellerBy').select2("val",reTellerBy);
				}else{
					if(serverResult.length>1){
						$('#payTellerBy').select2("val",serverResult[0].id);
						$('#reTellerBy').select2("val",serverResult[1].id);
					}
				}
				//返回结果非空校验 
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
			}
		});
		//获取柜员余额div	
		$("#totalAmountDiv").load(
				"${ctx}/clear/v03/clearAccountAdjustInfo/totalAmount?cashType="+cashType); 
	}
	/**
	 * 加载select2下拉列表选项用
	 */
	function format(item) 
	{
		return item.label;
	}	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 柜员调账列表 -->
		<li><a href="${ctx}/clear/v03/clearAccountAdjustInfo/list"><spring:message code='clear.accountAdjust.list' /></a></li>
		<!-- 柜员调账登记 -->
		<li class="active"><a
			href="${ctx}/clear/v03/clearAccountAdjustInfo/form"><spring:message code='clear.accountAdjust.title' /></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="clearAccountAdjustInfo"
		onkeydown="if(event.keyCode==13)return false;"
		action="${ctx}/clear/v03/clearAccountAdjustInfo/save" method="post"		
class="form-horizontal">
		<sys:message content="${message}" />
		<!-- 明细 -->
		<div class="row">
		
			<div class="span8">
				<%-- 金额类型 --%>
				<br>&nbsp;<label class="control-label"><spring:message
							code="clear.accountAdjust.cashType" />：</label> &nbsp;&nbsp;
				<form:select path="cashType" id="cashType" class="input-large required"
					onchange="changeBankConnect(this.options[this.selectedIndex].value)">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getFilterDictList('cash_type_provisions',true,'')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
				<div class="control-group">
					<!-- 总金额 -->
					<br />
					<label class="control-label"><spring:message
							code="clear.public.totalMoney" />：</label>
					<div class="controls">
						<form:input id="adjustMoney" path="adjustMoney" htmlEscape="false"
							onkeyup="upperCase(this)" onblur="upperCaseAll(this)"
							onfocus="update(this)" maxlength="13" class="required number"
							style="ime-mode:disabled;" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
					<!-- 总金额（格式化） -->
					<br />
					<label class="control-label"><spring:message
							code="clear.public.totalMoneyFormat" />：</label>
					<div class="controls">
						<textarea id="outAmountBigRmb" name="outAmountBigRmb" disabled >${sto:getUpperAmount(clearAccountAdjustInfo.adjustMoney)}</textarea>
					</div>
					<!-- 交款人 -->
					<br />
					<label class="control-label"><spring:message code='clear.accountAdjust.payTeller' />：</label>
					<div class="controls">
						<form:input id="payTellerBy" type="hidden" path="payTellerBy"
							style="width:210px;" />
						<span class="help-inline "><font color="red">*</font> </span>
						<form:hidden path="payTellerName" />
					</div>
					<!-- 收款人 -->
					<br />
					<label class="control-label required"><spring:message code='clear.accountAdjust.reTeller' />：</label>
					<div class="controls">
						<form:input id="reTellerBy" type="hidden" path="reTellerBy"
							style="width:210px;" />
						<span class="help-inline"><font color="red">*</font> </span>
						<form:hidden path="reTellerName" />
					</div>
					<!-- 备注 -->
					<br />
					<label class="control-label"><spring:message
							code="common.remark" />：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="3"
							maxlength="64" class="input-large " style="ime-mode:active;" />
					</div>
				</div>
			</div>
			<!-- 柜员余额DIV -->
			<div class="span8"
				style="overflow-x: hidden; width: 770px; height: 500px"
				id="totalAmountDiv"></div>
		</div>
		<div class="row">
			<div class="form-actions" style="width: 100%;">
				<!-- 保存 -->
				<input id="btnSubmit" style="margin-left: 1000px"
					class="btn btn-primary" type="button"
					value="<spring:message code='common.commit'/>" /> &nbsp;&nbsp;
				<!-- 返回 -->
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/clearAccountAdjustInfo/back'" />
			</div>
		</div>
	</form:form>
</body>
</html>
