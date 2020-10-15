<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 商行取款登记 -->
	<title><spring:message code="clear.bankGet.register" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			/* 增加千分位分割 wzj 2017-11-16 begin */
			if(	document.getElementById("outAmount").value!=""){
				addComma(document.getElementById("outAmount"));
			}
			/* end */
			
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
			changeBankConnect($("#custNo").val());
			
			//打印
			if($("#outNo").val() != "" && $("#outNo").val() != null){
				printDetail($("#outNo").val());
			}
			
			// 保存
			$("#btnSubmit").click(function() {
				
				//页面检查
				var blnChk = chkFrom();
				if (blnChk == false){
					return false;
				}
				
				//银行交接人员A
				setSelectName('bankManNoA','bankManNameA');
				//银行交接人员B
				setSelectName('bankManNoB','bankManNameB');
				//交接人
				setSelectName('transManNo','transManName');
				//复核人员
				setSelectName('checkManNo','checkManName');
				
				/* 将金额去掉逗号 wzj 2017-11-16 begin */
				update(document.getElementById("outAmount"))
				/* end */
				$("#inputForm").submit();
				
			});
		});
		
		
		//页面检查
		function chkFrom() {	
			//-----------总金额检查---------------
			var strOutAmount=$("#outAmount").val(); 
			if (typeof(strOutAmount) != "undefined" && !isNaN(strOutAmount)) {
				if (strOutAmount <= 0) {
					//总金额小于等于零，不能保存！
                	alertx("<spring:message code='message.I7004' />");
					return false;
				}	
			}

			//--------银行交接人员重复检查------------
			var strBankManNoA=$("#bankManNoA").val(); 
			var strBankManNoB=$("#bankManNoB").val();
			if(strBankManNoA == "" ||strBankManNoB==""){
				alertx("银行交接人不能为空！");
				return false;
			}
			if (strBankManNoA != "" && strBankManNoA == strBankManNoB){
				//银行交接人员选择重复，不能保存！
				alertx("<spring:message code='message.I7005' />");
				return false;
			}
			
			//--------交接复合人员重复检查------------
			var strTransManNo=$("#transManNo").val(); 
			var strCheckManNo=$("#checkManNo").val(); 
			if (strTransManNo != "" && strTransManNo == strCheckManNo){
				//交接复合人员选择重复，不能保存！
				alertx("<spring:message code='message.I7006' />");
				return false;
			}

			//------------差额检查----------------
			//面值列表整体刷新
			AllRefresh();
			//面值列表总差额的取得
			vDiffValue = getDiffValue();
			if (vDiffValue != 0){
				//差额不为零，不能保存！
				alertx("<spring:message code='message.I7007' />");
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
			setSumDiff();
			changeAmountToBig(obj.value,"outAmountBigRmb");
			/* 增加千分位分割 wzj 2017-11-14 begin */
			//addComma(obj);
			/* end */
		}
		/* 增加移出事件 wzj 2017-11-14 begin */
		function update(obj){
			setSumDiff();
			var money= changeAmountToNum(obj.value); 
			document.getElementById("outAmount").value=money;
		}
		/* 增加格式化 qph 2017-12-20 begin */
		function upperCaseAll(obj){
			setSumDiff();
			changeAmountToBig(obj.value,"outAmountBigRmb");
			addComma(obj);
		}
		//增加逗号方法
		function addComma(obj){
			var totalMoney=formatCurrencyFloat(obj.value);
			document.getElementById("outAmount").value=totalMoney;
		}
		/* end */
		/* 增加千分位分割和金额转换方法 wzj 2017-11-14 begin */
		/**
		千分位用逗号分隔
		 */
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
		 /**
		  将金额转换成数字
		 */
		function changeAmountToNum(amount) {
			if (typeof (amount) != "undefined"&&amount != null) {
				amount = amount.toString().replace(/\$|\,/g, '');
			}
			return amount;
		}
		 /* end */
		/**
		 * 金额转换成大写
		 * @param amount
		 * @param toId
		 */
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
				url : ctx + '/clear/v03/bankGetCtrl/changRMBAmountToBig?amount=' + amount,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					$("#" + toId).val(data.bigAmount);
					//$("#" + toId).html(data.bigAmount);
				},
				error : function(data, status, e) {
					
				}
			});
		}
		
		
		
		//根据选择的商业银行或者人民银行，获取该银行的交接员
		function changeBankConnect(officeId){
			var url = '${ctx}/clear/v03/bankGetCtrl/changeBankConnect';
			/* 清空交接人员内容 wzj 2017-11-29 begin */
			$('#bankManNoA').select2("val",'');
			$('#bankManNoB').select2("val",'');
			/* end */
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					param : JSON.stringify(officeId)
				},
				success : function(serverResult, textStatus) {
					$('#bankManNoA').select2({
						containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
					});
					
					$('#bankManNoB').select2({
						containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
					});
					/* 增加返回结果非空校验 wzj 2017-11-16 begin */
					if(serverResult.length>1){
						$('#bankManNoA').select2("val",serverResult[0].id);
						$('#bankManNoB').select2("val",serverResult[1].id);
					}
					/* end */
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) 
		{
			//alert(item.label);
			return item.label;
		}
		
		// 打印明细
		function printDetail(outNo){
			var content = "iframe:${ctx}/clear/v03/bankGetCtrl/printBankGet?outNo=" + outNo;
			top.$.jBox.open(
					content,
					//打印
					"<spring:message code='common.print' />", 900, 600, {
						buttons : {
							//打印
							"<spring:message code='common.print' />" : "ok",
							// 关闭
							"<spring:message code='common.close' />" : true
						},
						submit : function(v, h, f) {
							if (v == "ok") {
								var printDiv = h.find("iframe")[0].contentWindow.printDiv;
								$(printDiv).show();
								//打印 
								$(printDiv).jqprint();
								return false;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
					});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 商行取款列表 -->
		<li><a href="${ctx}/clear/v03/bankGetCtrl/list"><spring:message code="clear.bankGet.title" /></a></li>
		<!-- 商行取款登记  -->
		<li class="active"><a href="${ctx}/clear/v03/bankGetCtrl/form"><spring:message code="clear.bankGet.register" /></a></li>
	</ul>

	<form:form id="inputForm" modelAttribute="clOutMain" onkeydown="if(event.keyCode==13)return false;" action="${ctx}/clear/v03/bankGetCtrl/save" method="post" class="form-horizontal" >
		<sys:message content="${message}"/>
		
		<!-- 业务单号（保存后打印使用） -->
		<input id="outNo" type="hidden" value="${outNo}">

		<!-- 明细 -->
		<div class="row">
			<!-- 商行交款明细 -->
			<div class="span8" >
				<div class="control-group">
					<br/>
				</div>
				<div class="control-group">
					<!-- 客户名称 -->
					<label class="control-label"><spring:message code="clear.public.custName" />：</label>
					<div class="controls">
						<form:select path="rOffice.id" id="custNo"
							class="input-large required" disabled="disabled"
							onchange="changeBankConnect(this.options[this.selectedIndex].value)">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${sto:getStoCustList('3',false)}"
								itemLabel="name" itemValue="id" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
						<form:hidden path="rOffice.name" />
					</div>
				</div>
					
				
					<div class="control-group">
						<!-- 总金额 -->
						<label class="control-label"><spring:message code="clear.public.totalMoney"/>：</label>
						<div class="controls">
						<!-- 添加移出事件 wzj 2017-11-14 bgein -->
						<form:input id="outAmount" path="outAmount" htmlEscape="false" onkeyup="upperCase(this)" onblur="upperCaseAll(this)" onfocus="update(this)" maxlength="13" class="required number" style="ime-mode:disabled;"/>
							<span class="help-inline"><font color="red">*</font> </span>
						<!-- end -->
						</div>
					</div>
	
					<div class="control-group">
						<!-- 总金额（格式化） -->
						<label class="control-label"><spring:message code="clear.public.totalMoneyFormat"/>：</label>
						<div class="controls">
							<%-- <input id="outAmountBigRmb" name="outAmountBigRmb"  type="text" value="${sto:getUpperAmount(clOutMain.outAmount)}"  disabled/> --%>
							<textarea id="outAmountBigRmb" name="outAmountBigRmb" disabled>${sto:getUpperAmount(clOutMain.outAmount)}</textarea>
						</div>
					</div>
				<div class="control-group ">
					<!-- 银行交接人员A -->
					<label class="control-label"><spring:message code="clear.public.bankManNameA"/>：</label>
					<div class="controls">
						<form:input id="bankManNoA" type="hidden" path="bankManNoA" style="width:210px;"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group ">
					<!-- 银行交接人员B -->
					<label class="control-label"><spring:message code="clear.public.bankManNameB"/>：</label>
					<div class="controls">
						<form:input id="bankManNoB" type="hidden" path="bankManNoB" style="width:210px;"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
						<!-- 交接人员 -->
						<label class="control-label"><spring:message code="clear.public.transManName"/>：</label>
						<div class="controls">
							<form:select path="transManNo" id="transManNo" class="input-large required">
								<form:option value=""><spring:message code="common.select" /></form:option>
								<form:options items="${sto:getUsersByTypeAndOffice(transManType,fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
							</form:select>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
						<form:hidden path="transManName" />
					</div>
		    
					<div class="control-group">
						<!-- 复核人员 -->
						<label class="control-label"><spring:message code="clear.public.checkManName"/>：</label>
						<!-- 将checkManType修改为transManType wzj 2017-11-16 begin -->
						<div class="controls">
							<form:select path="checkManNo" id="checkManNo" class="input-large required">
								<form:option value=""><spring:message code="common.select" /></form:option>
								<form:options items="${sto:getUsersByTypeAndOffice(transManType,fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
							</form:select>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
						<form:hidden path="checkManName" />
						<!-- end -->
					</div>
		    
					<div class="control-group">
						<!-- 备注 -->
						<label class="control-label"><spring:message code="common.remark"/>：</label>
						<div class="controls">
							<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="255" class="input-large " style="ime-mode:active;"/>
						</div>
					</div>
			</div>
			<!-- 面值明细 -->
			<div class="span8">
				<sys:denominationGetList dataSource="${clOutMain.denominationList}"  bindListName="denominationList" totalAmountId="outAmount"/>
			</div>
		</div>
		<div class="row">
			<div class="form-actions" style="width:100%;">
					<!-- 保存 -->
					<input id="btnSubmit"  style="margin-left: 1000px"  class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>" />
						
					&nbsp;
	
				<!-- 返回 -->
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/bankGetCtrl/back'" />
			</div>
		</div>

	</form:form>
</body>
</html>