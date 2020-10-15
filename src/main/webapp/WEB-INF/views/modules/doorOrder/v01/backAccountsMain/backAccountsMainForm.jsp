<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>回款管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					//回款金额验证
					if(!checkCashIsZero()){
						return;
					}
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
					$("#btnUploadImage").attr("disabled",true);
					loading('正在提交，请稍等...');
					var a=$("#outAmount").val();
					a=changeAmountToNum(a);
					//a=formatCurrencyNum(a);
					$("#outAmount").val(a);
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
			
			//提交时检查金额是否为0
			function checkCashIsZero(){
				if($("#outAmount").val()==0){
					alertx("回款金额不能为0，请重新输入！");
					return false;
				}
				return true;
			}
		
		});
		
		//通过ajax,从后台获取机构银行卡号。
		function getBankCard(officeId){
			var url = "${ctx}/doorOrder/v01/backAccountsMain/getBankCard?officeId="+officeId;
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data, textStatus) {
					$("#bankCard").val(data);
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
				}
			});
		}
		
		/* 金额验证修改 gzd 2020-05-21 start */
		$(document).ready(function(){
			detailAmountCheck($("#outAmount"));
		});
		//明细金额验证
		function detailAmountCheck(obj){			
			obj.keyup(function () {
				var value = $(this).val().replace(/,/g, "")
				if(value.split(".")[0].length <= 10) {
					var reg = value.match(/\d+\.?\d{0,2}/);
			        var txt = '';
			        if (reg != null) {
			            txt = reg[0];
			        }
			        $(this).val(txt);
				} else {
					var value2 = value.slice(0,10)
					var reg2 = value2.match(/\d+\.?\d{0,2}/);
					var txt2 = '';
			        if (reg2 != null) {
			            txt2 = reg2[0];
			        }
					$(this).val(txt2);
				}
		    }).change(function () {
		        $(this).keypress();
		        var v = $(this).val();
		        console.log('v',v)
		        var txt = '';
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
		    });
			
		}
		/* 金额验证修改 gzd 2020-05-21 end */
		/* //明细金额验证
		function detailAmountCheck(){
			if(isNaN($("#outAmount").val())){
				$("#outAmount").val(0);
			}
		} */
		
		//门店选择回调函数  gzd 2019-11-29
		function doorNameTreeselectCallBack(v, h, f){
			if (v=="ok"){
				$("#backNumber").find("option[name='backNumberOption']").each(function () {
					$(this).remove();
				});
				var tree = h.find("iframe")[0].contentWindow.tree;
				var ids = [], names = [], nodes = [];
				nodes = tree.getSelectedNodes();
				if(nodes.length>0){
					var doorId=nodes[0].id;
					//var doorName=nodes[0].name;
					//alert(doorName);
					//$("#doorId").val(doorId);
					//$("#doorName").val(doorName);
					 $.ajax({
						type : "POST",
						dataType : "json",
						async: false,
						url : "${ctx}/doorOrder/v01/backAccountsMain/getOrderId",
						data : {
							doorId:doorId
						},
						success : function(data, status) {
							if(data.length>0){
								for(var i=0;i<data.length;i++){
									var newOption = $("#backNumberOptionTemplate0").clone(true);
									newOption.attr("value",data[i].outAmount);
									newOption.html(data[i].backNumber);
									newOption.removeAttr("style");
									// 添加一行
									newOption.appendTo("#backNumber");
								}
							}
						},
						error : function(XmlHttpRequest, textStatus, errorThrown) {
							alertx("系统异常，请联系管理员！");
						}
					}); 
				}
			}
			$("#backNumber option:first").prop("selected","selected");
			$('#backNumber').trigger("change");
		}
		//回款单号改变 金额返显
		$(document).ready(function(){
			var a=formatCurrencyFloat($("#outAmount").val());
			//a=changeAmountToNum(a);
			//a=formatCurrencyNum(a);
			$("#outAmount").val(a);
			$("#backNumber").change(function(){
				$("#outAmount").val($("#backNumber").val());
				$("#backNumberHidden").val($(this).find("option:selected").text());
				var b=formatCurrencyFloat($("#outAmount").val());
				//b=changeAmountToNum(b);
				//b=formatCurrencyNum(b);
				$("#outAmount").val(b);
			});
		});
		
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
		
		function formatCurrencyNum(num) {
		    var result = '', counter = 0;
		    num = (num || 0).toString();
		    for (var i = num.length - 1; i >= 0; i--) {
		        counter++;
		        result = num.charAt(i) + result;
		        if (!(counter % 3) && i != 0) { result = ',' + result; }
		    }
		    return result;
		}
		/**
		 * 将金额转换成数字
		 * @param amount
		 * @returns
		 */
		function changeAmountToNum(amount) {
			if (typeof(amount) != "undefined") {
				amount = amount.toString().replace(/\$|\,/g,'');
			}
			return amount;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="doororder.v01:backAccountsMain:view"><li><a href="${ctx}/doorOrder/v01/backAccountsMain/">回款信息列表</a></li></shiro:hasPermission>
		<shiro:hasPermission name="doororder.v01:backAccountsMain:edit">
			<li class="active">
			<%-- gzd 2020-04-14 修改链接 ${ctx}/doorOrder/v01/backAccountsMain/form --%>
				<a>回款信息${not empty backAccountsMain.id?'查看':'添加'}	</a>
			</li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="backAccountsMain" action="${ctx}/doorOrder/v01/backAccountsMain/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<form:hidden path="backNumber" id="backNumberHidden"/>
		<%-- <form:hidden path="doorId" id="doorId"/> --%>
		<%-- <form:hidden path="doorName" id="doorName"/> --%>
		<sys:message content="${message}"/>
		<c:if test="${empty(backAccountsMain.id)}">
			<div class="control-group">
				<!-- 门店 -->
				<label class="control-label"><spring:message code="door.doorOrderException.cust" />：</label>
				<div class="controls">
					<sys:treeselect id="doorName" name="doorId"
						value="${backAccountsMain.doorId}" labelName="doorName"
						labelValue="${backAccountsMain.doorName}" title="<spring:message code='door.doorOrderException.cust' />"
						url="/sys/office/treeData" cssClass="required input-medium"
						notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
						isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group">
				<!-- 公司名称 -->
				<label class="control-label"><spring:message
						code="common.company" />：</label>
				<div class="controls">
					<form:select path="companyId" id="companyId"
						class="input-large required" disabled="disabled"
						onchange="getBankCard(this.options[this.selectedIndex].value)">
						<form:option value="">
							<spring:message code="common.select" />
						</form:option>
						<form:options items="${sto:getPlatform()}"
							itemLabel="name" itemValue="id" htmlEscape="false" />
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
					<form:hidden path="companyName" />
				</div>
			</div>
			<div class="control-group">
				<!-- 回款单号 -->
				<label class="control-label">回款单号：</label>
				<div class="controls">
					<form:select id="backNumber" path="" class="input-large required" style="font-size:15px;">
						<form:option id="backNumberOption" value="" selected="selected"><spring:message code="common.select" /></form:option>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>	
			<div class="control-group">
				<!-- 总金额 -->
				<label class="control-label"><spring:message
						code="clear.public.totalMoney" />：</label>
				<div class="controls">
					<form:input id="outAmount" path="outAmount" htmlEscape="false" maxlength="13" class="required number"
						style="ime-mode:disabled;text-align:right;" />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>	
		<c:if test="${not empty(backAccountsMain.id)}">
			<div class="control-group">
				<!-- 门店 -->
				<label class="control-label"><spring:message code="door.doorOrderException.cust" />：</label>
				<div class="controls">
					<form:input id="doorName" path="doorName" htmlEscape="false" maxlength="20" class="required"
					style="ime-mode:disabled;text-align:right;" readOnly="readOnly"/>
					
				</div>
			</div>
			<div class="control-group">
				<!-- 公司名称 -->
				<label class="control-label"><spring:message code="common.company" />：</label>
				<div class="controls">
					<form:input path="companyName" htmlEscape="false" maxlength="20" class="required"
					style="ime-mode:disabled;text-align:right;" readOnly="readOnly"/>
					
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">回款单号：</label>
				<div class="controls">
				<!-- 为简便，卡号验证后期加 creditcard -->
					<form:input id="backNumber" path="backNumber" htmlEscape="false" maxlength="20" class="required"
						style="ime-mode:disabled;text-align:right;" readOnly="readOnly"/>
					
				</div>
			</div>
			<div class="control-group">
				<!-- 总金额 -->
				<label class="control-label"><spring:message
						code="clear.public.totalMoney" />：</label>
				<div class="controls">
					<form:input id="outAmount" path="outAmount" htmlEscape="false" maxlength="13" class="required number"
						style="ime-mode:disabled;text-align:right;" readOnly="readOnly"/>
					
				</div>
			</div>	
		</c:if>	
		
		<%-- <div class="control-group">
			<label class="control-label">银行卡号：</label>
			<div class="controls">
			<!-- 为简便，卡号验证后期加 creditcard -->
				<form:input id="bankCard" path="bankCard" htmlEscape="false" maxlength="19" class="required creditcard"
					style="ime-mode:disabled;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> --%>
		
		<c:if test="${empty(backAccountsMain.id)}">
		<div class="control-group">
			<!-- 图片上传 -->
			<h1>
				<spring:message code="message.E1070" />
				(<span class="help-inline"><font color="red">*.${fns:getConfig('image.setting.import.format')}</font>
				</span>)
			</h1>
			<input id="fileToUpload" type="file" accept="image/*"
				name="fileToUpload" class="required" />&nbsp;
			<%-- <form:hidden path="id" /> --%>
			<form:hidden path="picPath" />
			<form:hidden path="tmpPicFileName" />
			<form:hidden path="x" />
			<form:hidden path="y" />
			<form:hidden path="w" />
			<form:hidden path="h" />
		</div>
		</c:if>
		<c:if test="${not empty(backAccountsMain.id)}">
			<div class="control-group">
				<label class="control-label"><spring:message
						code="door.accountManage.backCredential" />：</label>
				<div class="controls">
					<img
						src="${ctx}/doorOrder/v01/backAccountsMain/showImage?id=${backAccountsMain.id}"
						height="30%" width="30%" />
				</div>
			</div>
		</c:if>
		
		<div class="form-actions">
		<c:if test="${backAccountsMain.id==null}">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" onclick="checkCashIsZero()"/>&nbsp;
		</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" 
			onclick="history.go(-1)" />
			<%-- onclick="window.location.href='${ctx}/doorOrder/v01/backAccountsMain/back'"/> --%>
		</div>
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="backNumberOption" id="backNumberOptionTemplate0"/>
		</form:select>
		</div>
	</form:form>
</body>
</html>