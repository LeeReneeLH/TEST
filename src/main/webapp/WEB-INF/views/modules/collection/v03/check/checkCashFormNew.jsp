<%@page import="com.coffer.core.modules.sys.utils.UserUtils"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 款箱拆箱 -->
	<title><spring:message code="door.checkCash.title" /></title>
	<meta name="decorator" content="default"/>
	
	<style type="text/css">
        .fixedThead{
            display: block;
            width: 100%;
        }
        .scrollTbody{
            display: block;
            height: 202px;
            overflow: auto;
            width: 100%;
        }

        thead.fixedThead tr th:last-child {
            color:#FF0000;
            width: 130px;
        }
        
		.rowSel {
		  background-color: #5599FF;
		 }
    </style>
	
	<script type="text/javascript">
		var inputSelectAmountId = "";	//每笔金额-选中行录入金额ID
		var realSelectTotalId = "";		//每笔金额-选中行实际金额ID
		var delRowObj;					//每笔金额-删除行 对象
	
		$(document).ready(function() {
			//$("#name").focus();
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
		});
		
		$(window).load(function() {
			if($("#checkAmount").val()==0){
				$("#checkAmount").val("");
			}else{
				$("#checkAmount").val(formatCurrencyFloat($("#checkAmount").val()));
			}
			$("#inputAmount").val(formatCurrencyFloat($("#inputAmount").val()));
			$("#diffAmount").val(formatCurrencyFloat($("#diffAmount").val()));
			$("#trueSumMoney").val(formatCurrencyFloat($("#trueSumMoney").val()));
			setSumDiff();
			setSumDiff2();
			renewAmount(); 
        });
		
		// 保存
		function save() {
			if($('#inputAmount').val()==0){
				//总金额不能为0
				alertx("<spring:message code='message.I7205' />");
				return false;
			}
			if($('#checkAmount').val()==null||$('#checkAmount').val()==''){
				//清机金额不能为空
				alertx("清点金额不能为空");
				return false;
			}
			var registerAmountShow=parseFloat(changeAmountToNum($("#registerAmountShow").html()));
			var registerAmountShow2=parseFloat(changeAmountToNum($("#registerAmountShow2").html()));
			var amountShow=registerAmountShow+registerAmountShow2;
			var checkAmount=parseFloat(changeAmountToNum($("#checkAmount").val()));
			if(amountShow != 0 && amountShow != checkAmount){
				alertx("清点金额与明细总额不相等！");
				return false;
			}
			
			//金额明细
			var i=0;
			var amountListStr='';
			$("#tblDetailData").find("tr").each(function(){
				if(i > 0){
					amountListStr=amountListStr+","+$(this).children('input:eq(0)').val()+"_"+changeAmountToNum($(this).children('td:eq(2)').text().trim());
				}
				i++;
			});
			amountListStr=amountListStr.substring(1);
			$("#amountListStr").val(amountListStr);
			
			//获取限制金额
			var exAmount,exType;
		   $.ajax({
					url : ctx + '/collection/v03/checkCash/getDoorAuthorize?custNo=' + $("#custNo").val(),
					type : 'post',
					dataType : 'json',
					async:false,
					success : function(data, status) {
						//ex =data;
						exAmount = data.amount;
						exType = data.expressionType;
					} ,
					error : function(data, status, e) {
						return false;
					} 
				});
		     var pass = true;
		     // 表达式类型 1:大于，2:小于，3:等于，4:大于等于，5:小于等于
			if((exType == '') 
					//大于
				|| (exType == '1' && Math.abs(changeAmountToNum($('#diffAmount').val())) > parseFloat(exAmount)) 
				    //大于等于
				|| (exType == '4' && Math.abs(changeAmountToNum($('#diffAmount').val())) >= parseFloat(exAmount)) 
				    //等于
			    || (exType == '3' && Math.abs(changeAmountToNum($('#diffAmount').val())) == parseFloat(exAmount))
			    ){
				
			}else{
				 pass = false;
			}
			//是否存在差额
			if(changeAmountToNum($('#diffAmount').val())!=0 && pass){
				var content = "iframe:${ctx}/collection/v03/checkCash/authorizeDetail";
				//授权操作
				top.$.jBox.open(
						content,
						"<spring:message code='message.I7239' />", 470, 220, {
							buttons : {
								//确认
								"<spring:message code='common.confirm' />" : "ok",
								//关闭
								"<spring:message code='common.close' />" : true 
							},
							submit : function(v, h, f) {
								if (v == "ok") {
									var contentWindow = h.find("iframe")[0].contentWindow;
									var vData;
									
									// 用户名
									var authorizer = contentWindow.document.getElementById("authorizer").value;
									if(authorizer == null || authorizer == ""){
										//请输入用户名
										alertx("<spring:message code='message.I7218' />");
										return false;
									}
									//密码
									var authorizeBy = contentWindow.document.getElementById("authorizeBy").value;
									if(authorizeBy == null || authorizeBy == ""){
										//请输入密码
										alertx("<spring:message code='message.I7219' />");
										return false;
									}
									//授权后台验证
									$.ajax({
										url : ctx + '/collection/v03/checkCash/authorizeUser?authorizer=' + authorizer + '&authorizeBy=' + authorizeBy ,
										type : 'post',
										dataType : 'json',
										async:false,
										success : function(data, status) {
											vData = data;
										},
										error : function(data, status, e) {
											return false;
										}
									});
									//授权验证结果的判断
									var vResult = vData.result;
									//正常
									if (vResult == "OK"){
										$("#authUserId").val(vData.userId);
										$("#inputForm").submit();
										return true;
									}else{
										//异常
										alertx(vResult);
										return false;
									}
								}
							},
							loaded : function(h) {
								$(".jbox-content", top.document).css(
										"overflow-y", "hidden");
							}
				});
			}else if(changeAmountToNum($('#diffAmount').val())!=0 && !pass){
				$("#authUserId").val($("#hidPrincipalId").val());
				$("#inputForm").submit();
			}else{
				$("#inputForm").submit();
			}
		}
		
		//每笔金额-添加行 
		function addDetailRow(){
			//取值
			var detailAmount=$("#detailAmount").val();
			//每笔金额的检查
		 	if(detailAmount == null || detailAmount=='' ) {
		 		//请输入每笔金额
				alertx("<spring:message code='message.I7217' />");
				return;
			}
			var money = /^[1-9]\d*(\.(\d){1,2})?$/;
			if(!money.test(detailAmount)){
				//请输入合法的金额
				alertx("<spring:message code='message.I7206' />");
				return;
			}
			//取值
			var num=$("#rowCount").val();
			num++;
			var newRow = $("#trDetailRowTemplate0").clone(true).attr("id","trDetailRow_"+num);
			// 添加一行
			newRow.appendTo("#tblDetailData");
			// tr单元格赋值
			//凭条
			newRow.children('td:eq(1)').text("");
			//金额
			newRow.children('td:eq(2)').text(formatCurrencyFloat(detailAmount));
			newRow.children('td:eq(2)').attr("id","tdInputAmount_" + num);
			//重新赋值
			$('#rowCount').val(num);
			 // 行追加后每笔金额清空
			 $("#detailAmount").val('');
			//总金额重新赋值
			renewAmount();
			var tbody1 = document.getElementById('tbody');
			tbody1.scrollTop = tbody1.scrollHeight;
		 } 
		
		// 左侧合计金额及笔数的再计算
		function renewAmount(){
			var i=0
			var rowCnt = 0;
			//序号重置
			$("#tblDetailData").find("tr").each(function () {
	            // 从第三行开始
	            if (i>0) {
	            	// 第二列单元格的值eq(索引)
	               	rowCnt++;
	                $(this).children('td:eq(0)').text(rowCnt);
	            }
	            i++;
			});
			//录入金额的合计
			var sumInputAmount = getListSumValue('detailInputAmount');
			$("#inputAmount").val(formatCurrencyFloat(sumInputAmount));
			//差额的计算 
			var strdiff = changeAmountToNum($("#checkAmount").val())-changeAmountToNum($("#trueSumMoney").val());
			$("#diffAmount").val(formatCurrencyFloat(strdiff));
			//笔数
			$('#boxCount').val(rowCnt);
	 	} 
			
		//每笔金额-删除行询问
		function delRowConfirm(vRow) {	
			delRowObj = vRow;
			var message = "<spring:message code='message.I7215'/>";
			var tr=delRowObj.parentNode.parentNode.parentNode;
			//数据无的场合：询问-->删除
			confirmx(message, delAmountRow);

		}
		
		 //每笔金额-删除行 
		 function delAmountRow(){ 
			 var tr=delRowObj.parentNode.parentNode.parentNode;
			 var tbody=tr.parentNode;
			 tbody.removeChild(tr);
			// 总金额重新赋值
			 renewAmount();
		 }
		 
		 /* 金额验证修改 gzd 2020-05-21 start */
		$(document).ready(function(){
			detailAmountCheck($("#checkAmount"));
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
				renewAmount(); 
		    }).change(function () {
		        $(this).keypress();
		        var v = $(this).val();
		        console.log('v',v)
		        var txt = '';
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
				renewAmount(); 
		    });
		}
		 
		/*  //输入清机金额
		 function clearAmount(){
			if(isNaN($("#checkAmount").val())){
				 $("#checkAmount").val(0);
			}
			renewAmount(); 
		 } */
			
		//----------------------------
		//---------页面中部脚本-----------
		//----------------------------
		
		/**
		 * 面值列表-计算金额
		 *
		 * @param vId  ：面值list行索引
		 * @param vKbn ：是否设置合计栏及差额栏
		 */
		function computeAmount(vId,vKbn){
			//Id的取得
			var toId = "totalAmount_" + vId;				//金额(元)
			var moneyCountDId = "moneyCountD_" + vId;		//未清分(捆)-数量列1
			var goodsValueId = "parValue_" + vId;			//实际面值
			//值的取得
			var moneyCountD = $("#" + moneyCountDId).val();
			var goodsValue = $("#" + goodsValueId).val();
	
			//面值对应的捆数未输入的场合，退出
			if ((moneyCountD == undefined || moneyCountD == "")) {
				$("#" + toId).val("");
				$("#" + toId + "Show").html("");
				setSumDiff();		//设置合计栏及差额栏
				return;
			}
			//未清分(捆)
			if (moneyCountD == undefined || moneyCountD == "") {
				moneyCountD = "0";
			}
	
			//金额的计算及显示
			var amount = 0;
			if (typeof(goodsValue) != "undefined" && goodsValue != "") {
				amount =(moneyCountD * 1) * goodsValue;
			}
			
			if (!isNaN(amount)) {
				$("#" + toId).val(amount);
				amount = formatCurrencyFloat(amount);
				$("#" + toId + "Show").html(amount);
			} 
			if (vKbn){
				setSumDiff();
			}
		}
		
		/**
		 * 下一个焦点的设置
		 */
        function setNextFocus(vObj){
        	var arrId = vObj.id.split("_");
			if (event.keyCode == 13){
				var nextId = "moneyCountD_" + (arrId[1] * 1 + 1);
				try{$("#" + nextId).focus();}catch(e){}
			}
		}
		/**
		 * 设置合计栏及差额栏
		 */
		function setSumDiff() {
			//取得一列的合计值
			var sumCountD = getSumValue('CountD');			//未清分(捆)
			var sumOrderAmount = getSumValue('Amount');		//金额(元)
			//合计值的设定
			$("#SumCountD").html(formatCurrencyNum(sumCountD));
			$("#registerAmountShow").html(formatCurrencyFloat(sumOrderAmount));
			return true;
		}
				
		/**
		 * 取得一列的合计值(面值列表)
		 */
		function getSumValue(vType) {
			var sumOrder = 0;
			var strFilter = "input[UseType='" + vType + "']";
			$(strFilter).each(function(){
				var strAmount = $(this).val();
				if (typeof(strAmount) != "undefined" && !isNaN(changeAmountToNum(strAmount))) {
					sumOrder += Number(changeAmountToNum(strAmount));
				}
			});
			return sumOrder;
		}
		
		/**
		 * 每笔金额一列合计值的取得
		 */
		function getListSumValue(vType) {
			var sumOrder = 0;
			var strFilter = "td[UseType^='" + vType + "']";
			$(strFilter).each(function(){
				var strAmount = getRealValue($(this).html());
				if (typeof(strAmount) != "undefined" && !isNaN(changeAmountToNum(strAmount))) {
					sumOrder += Number(changeAmountToNum(strAmount));
				}
			});
			return sumOrder;
		}
			
		//TD单元格内真实值的取得
		function getRealValue(testStr) {
	        var resultStr = "";
	        resultStr = testStr.replace(/\ +/g, ""); //去掉空格
	        resultStr = resultStr.replace(/[ ]/g, "");    //去掉空格
	        resultStr = resultStr.replace(/[\r\n]/g, ""); //去掉回车换行
	        resultStr = resultStr.replace(/[\r]/g, ""); //去掉回车换行
	        resultStr = resultStr.replace(/[\n]/g, ""); //去掉回车换行
	        resultStr = resultStr.replace(/[\t]/g, ""); //去掉tab
	        return resultStr;
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
		
		/**
		 * 面值列表-计算金额
		 *
		 * @param vId  ：面值list行索引
		 * @param vKbn ：是否设置合计栏及差额栏
		 */
		function computeAmount2(vId,vKbn){
			//Id的取得
			var toId = "totalAmount2_" + vId;				//金额(元)
			var moneyCountDId = "moneyCountD2_" + vId;		//未清分(捆)-数量列1
			var goodsValueId = "parValue2_" + vId;			//实际面值
			//值的取得
			var moneyCountD = $("#" + moneyCountDId).val();
			var goodsValue = $("#" + goodsValueId).val();
	
			//面值对应的捆数未输入的场合，退出
			if ((moneyCountD == undefined || moneyCountD == "")) {
				$("#" + toId).val("");
				$("#" + toId + "Show2").html("");
				setSumDiff2();		//设置合计栏及差额栏
				return;
			}
			//未清分(捆)
			if (moneyCountD == undefined || moneyCountD == "") {
				moneyCountD = "0";
			}
	
			//金额的计算及显示
			var amount = 0;
			if (typeof(goodsValue) != "undefined" && goodsValue != "") {
				amount =(moneyCountD * 1) * goodsValue;
			}
			
			if (!isNaN(amount)) {
				$("#" + toId).val(amount);
				amount = formatCurrencyFloat(amount);
				$("#" + toId + "Show2").html(amount);
			} 
			if (vKbn){
				setSumDiff2();
			}
		}
		/**
		 * 设置合计栏及差额栏
		 */
		function setSumDiff2() {
			//取得一列的合计值
			var sumCountD = getSumValue('CountD2');			//未清分(捆)
			var sumOrderAmount = getSumValue('Amount2');		//金额(元)
			//合计值的设定
			$("#SumCountD2").html(formatCurrencyNum(sumCountD));
			$("#registerAmountShow2").html(formatCurrencyFloat(sumOrderAmount));
			return true;
		}
	</script>
</head>

<body>
	<ul class="nav nav-tabs">
		<!-- 款箱拆箱列表 -->
		<li><a href="${ctx}/collection/v03/checkCash/"><spring:message code="door.checkCash.title" /><spring:message code="common.list" /></a></li>
		<!-- 历史拆箱列表 -->
		<li><a href="${ctx}/collection/v03/checkCash/historyList"><spring:message code="clear.atmCashBox.history" /></a></li>
		<!-- 款箱拆箱登记(修改) -->
		<li class="active">
			<%-- <a href="${ctx}/collection/v03/checkCash/form?id=${checkCashMain.outNo}"> --%>
				<shiro:hasPermission name="check:checkCash:edit">
					<c:choose>
						<c:when test="${not empty checkCashMain.outNo}">
							<a>
								<spring:message code="door.checkCash.title" /><spring:message code="common.modify" /> 
							</a>
						</c:when>
						<c:otherwise>
							<a>
								<spring:message code="door.checkCash.title" /><spring:message code="common.add" /> 
							</a>
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			<!-- </a> -->
		</li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="checkCashMain" action="${ctx}/collection/v03/checkCash/save" method="post" class="form-horizontal">
		<form:hidden id="hidOutNo" path="outNo"/>
		<form:hidden path="amountListStr"/>
		<form:hidden path="updateCnt"/>
		<input id="authUserId" name="authUserId" type="hidden" value=""/>
		<input id="hidUpdateCnt"  type="hidden" value="${checkCashMain.updateCnt}"/>
		<input id="hidPrincipalId"  type="hidden" value="<%=UserUtils.getPrincipal().getId()%>"/>
		<c:set var="col1" value="50px"/>
		<c:set var="col2" value="190px"/>
		<c:set var="col3" value="150px"/>
		<c:set var="col4" value="80px"/>
		<c:choose>
			<c:when test="${checkCashMain.dataFlag eq '1'}">
				<c:set var="rfidReadOnly" value="true"/>
				<c:set var="amountDisplay" value="display:none"/>
			</c:when>
			<c:otherwise>
				<c:set var="rfidReadOnly" value=""/>
				<c:set var="amountDisplay" value=""/>	
			</c:otherwise>
		</c:choose>
		<sys:message content="${message}"/>		
		<%-- 明细 --%>
		<div class="row">
			<%-- 页面左侧 --%>
			<div class="span7" >
				<div class="control-group">
					<%-- 门店 --%>
					<label ><spring:message code="door.public.cust" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<c:choose>
						<c:when test="${not empty checkCashMain.outNo}">
							<form:hidden path="custNo"/>
							<form:input id="custName" path="custName" htmlEscape="false" readOnly="readOnly" style="width:515px;text-align:left;"    class="required" />
						</c:when>
						<c:otherwise>
							<sys:treeselect id="custName" name="custNo"
							value="${checkCashMain.custNo}" labelName="custName"
							labelValue="${checkCashMain.custName}" title="门店"
							url="/sys/office/treeData" cssClass="required "  cssStyle="width:470px;"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
						    isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="control-group">
					<%-- 包号 --%>
					<label ><spring:message code="door.doorOrder.packNum" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="rfid" style="width:515px;text-align:left;" readOnly="${rfidReadOnly}"/>
				</div>
				<div class="control-group" style="${amountDisplay }">
					<%-- 每笔金额 --%>
					<label ><spring:message code="door.checkCash.detailMoney" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="detailAmount" 
								cssClass="money"  
								style="width:515px;text-align:left;"  
								onkeydown="if(event.keyCode==13){addDetailRow();return false;}" 
								maxlength="9"
								tabindex="1"
								/>
				</div>
				<%-- 每笔金额明细 --%>
				<div class="control-group">
					<input type="hidden" id="rowCount" name="rowCount" value="0" />
					<div  style="width:560px">
						 <table id="tblDetailData" class="table " cellpadding="0">
							 <thead>
								<tr style="border-bottom:none;border-right:none;background:#FFFFFF;"  class="fixedThead">
									<%-- 序号 --%>
									<th style="text-align: center" width="${col1}"><spring:message code="common.seqNo" /></th>
									<%-- 包号 --%>
									<th style="text-align: center;" width="${col2}"><spring:message code="door.checkCash.tickertape" /></th>
									<%-- 录入金额 --%>
									<th style="text-align: center" width="${col3}"><spring:message code="door.checkCash.inputMoney" /></th>
									<%-- 备注 --%>
									<th style="text-align: center" width="${col4}"><spring:message code='common.remark'/></th>
								</tr>
							</thead>
							<tbody id="tbody"  class="scrollTbody">
								<c:forEach items="${checkCashMain.amountList}" var="amountItem" varStatus="status">
									<tr id="trDetailRow_${status.index + 1}" >
										<td name="tdCol1" style="text-align:center;" width="${col1}">${status.index + 1}</td>
										<td name="tdCol2" style="text-align:center;" width="${col2}">
											${amountItem.packNum}
										</td>
										<td name="tdCol3" UseType="detailInputAmount" style="text-align:center;" width="${col3}" id="tdInputAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.inputAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										<td name="tdCol4" style="text-align:center;" width="${col4}">
												${amountItem.oRemarks}
										</td>
										<input  name="hidAmountId"  type="hidden" value="${amountItem.id}"/>
									</tr>
								</c:forEach>
							</tbody>
						 </table> 
					</div>
				</div>
				
				<!-- 增加存款差错金额计算部分 wqj 2019-10-23    start -->
				<div class="control-group" style="margin-top: -10px;">
					<%-- 登记存款总额(元) --%>
					<label ><spring:message code="door.checkCash.inputSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="inputAmount" 
								path="inputAmount"
								htmlEscape="false"  
								cssClass="required" 
								readonly="true" 
								style="width:515px;text-align:right;background-color:#E6E6E6"   />
				</div>
				
				<div class="control-group" style="margin-top: 10px;">
					<%-- 存款差错金额(元) --%>
					<label ><spring:message code="door.checkCash.saveErrorMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="saveErrorMoney" 
								path="saveErrorMoney"
								htmlEscape="false"  
								cssClass="required" 
								readonly="true" 
								style="width:515px;text-align:right;background-color:#E6E6E6"   />
				</div>
				
				<div class="control-group" style="margin-top: 10px;">
					<%-- 实际存款总额(元) --%>
					<label ><spring:message code="door.checkCash.trueSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="trueSumMoney" 
								path="trueSumMoney"
								htmlEscape="false"  
								cssClass="required" 
								readonly="true" 
								style="width:515px;text-align:right;background-color:#E6E6E6"   />
				</div>
				
				<!-- 增加存款差错金额计算部分 wqj 2019-10-23    end -->
				
				<div class="control-group">
					<%-- 清点总额(元) onkeyup="clearAmount()" onchange="clearAmount()" --%>
					<label ><spring:message code="door.checkCash.realSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="checkAmount" 
								path="checkAmount" 
								htmlEscape="false" 
								cssClass="input-xxlarge" 
								maxlength="13" 
								style="width:515px;text-align:right;"/>
				</div>
				<div class="control-group">
					<%-- 差额(元) --%>
					<label ><spring:message code="door.public.diffMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="diffAmount" 
								path="diffAmount" 
								htmlEscape="false" 
								readonly="true" 
								cssClass="input-xxlarge" 
								style="width:515px;text-align:right;background-color:#E6E6E6"/>
				</div>

				<div class="control-group">
					<%-- 笔数 --%>
					<label ><spring:message code="door.checkCash.count" /></label>
					<br>
					<form:input id="boxCount" 
								path="boxCount"  
								htmlEscape="false"  
								cssClass="required input-xxlarge" 
								readonly="true" 
								style="width:515px;text-align:right;background-color:#E6E6E6"   />
				</div>

				<div class="control-group">
					<%-- 备注 --%>
					<label ><spring:message code="door.checkCash.remark" /></label>
					<br>
					<form:textarea id="remarks"  path="remarks" htmlEscape="false" rows="3" maxlength="60" style="width:515px;ime-mode:active;"/>
				</div>

			</div>
			<%-- 页面中部 --%>
			<div class="span5" style="margin-left: 80px">
				<%-- 纸币 --%>
				<label ><spring:message code="door.public.cnypden" />：</label>
				<%-- 面值明细 --%>
				<table  id="contentTable" class="table table-hover">
					<thead>
						<tr>
							<%-- 面值 --%>
							<th style="text-align:center;"><spring:message code="common.denomination" /></th>
							<%-- 张数 --%>
							<th style="text-align:center;"><spring:message code="door.public.sheetCount" /></th>
							<%-- 金额(元)--%>
							<th style="text-align:center;"><spring:message code="door.public.money" /><spring:message code="common.units.yuan.alone" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${checkCashMain.denominationList}" var="parItem" varStatus="status">
							<tr >
								<input id="row_${status.index}" UseType="RowIndex" type="hidden" value="${status.index}"/>
								
								<%-- 面值 --%>
								<td style="text-align:center;width:70px;" >
									${parItem.moneyName}
									<form:hidden id="moneyKey_${status.index}"  path="denominationList[${status.index}].moneyKey" value="${parItem.moneyKey}"/>
									<form:hidden id="parValue_${status.index}" path="denominationList[${status.index}].moneyValue" value="${parItem.moneyValue}"/>
									<form:hidden path="denominationList[${status.index}].moneyName" value="${parItem.moneyName}"/>
								</td>

								<%-- 张数 --%>
									<td style="text-align:right;width:100px;">
										<form:input id="moneyCountD_${status.index}" path="denominationList[${status.index}].columnValue1" 
											onkeyup="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
											onchange="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
											onkeydown="value=value.replace(/[^0-9]/g,'');setNextFocus(this)" 
											UseType="CountD"
											maxlength="6" class="digits" style="width:90px;text-align:right;ime-mode:disabled;"
											tabindex="2" /></td>
								<%-- 金额 --%>
								<td style="text-align:right;"><label id='totalAmount_${status.index}Show' UserType="detailCheckAmount"><fmt:formatNumber value="${parItem.totalAmt}" pattern="#,###.##"/></label></td>
								<form:hidden  UseType="Amount" id="totalAmount_${status.index}" path="denominationList[${status.index}].totalAmt" />
							</tr>
						
						</c:forEach>
						<%-- 合计行 --%>
						<tr>
							<%-- 合计--%>
							<td style="text-align:center;" >
								<spring:message code="common.total" />：
							</td>
							<td style="text-align:right;">
								<label id="SumCountD"></label>
							</td>
							<td style="text-align:right;">
								<label id="registerAmountShow"></label>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<%-- 页面右侧 --%>
			<div class="span5" style="margin-left: 80px">
				<%-- 硬币 --%>
				<label ><spring:message code="door.public.cnyhden" />：</label>
				<%-- 面值明细 --%>
				<table  id="contentTable" class="table table-hover">
					<thead>
						<tr>
							<%-- 面值 --%>
							<th style="text-align:center;"><spring:message code="common.denomination" /></th>
							<%-- 枚数 --%>
							<th style="text-align:center;"><spring:message code="door.public.coin" /></th>
							<%-- 金额(元)--%>
							<th style="text-align:center;"><spring:message code="door.public.money" /><spring:message code="common.units.yuan.alone" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${checkCashMain.cnyhdenList}" var="parItem" varStatus="status">
							<tr >
								<input id="row2_${status.index}" UseType="RowIndex2" type="hidden" value="${status.index}"/>
								
								<%-- 面值 --%>
								<td style="text-align:center;width:70px;" >
									${parItem.moneyName}
									<form:hidden id="moneyKey2_${status.index}"  path="cnyhdenList[${status.index}].moneyKey" value="${parItem.moneyKey}"/>
									<form:hidden id="parValue2_${status.index}" path="cnyhdenList[${status.index}].moneyValue" value="${parItem.moneyValue}"/>
									<form:hidden path="cnyhdenList[${status.index}].moneyName" value="${parItem.moneyName}"/>
								</td>

								<%-- 张数 --%>
									<td style="text-align:right;width:100px;">
										<form:input id="moneyCountD2_${status.index}" path="cnyhdenList[${status.index}].columnValue1" 
											onkeyup="value=value.replace(/[^0-9]/g,'');computeAmount2('${status.index}',true)" 
											onchange="value=value.replace(/[^0-9]/g,'');computeAmount2('${status.index}',true)" 
											onkeydown="value=value.replace(/[^0-9]/g,'');setNextFocus(this)" 
											UseType="CountD2"
											maxlength="6" class="digits" style="width:90px;text-align:right;ime-mode:disabled;"
											tabindex="2" /></td>
								<%-- 金额 --%>
								<td style="text-align:right;"><label id='totalAmount2_${status.index}Show2' UserType="detailCheckAmount2"><fmt:formatNumber value="${parItem.totalAmt}" pattern="#,###.##"/></label></td>
								<form:hidden  UseType="Amount2" id="totalAmount2_${status.index}" path="cnyhdenList[${status.index}].totalAmt" />
							</tr>
						
						</c:forEach>
						<%-- 合计行 --%>
						<tr>
							<%-- 合计--%>
							<td style="text-align:center;" >
								<spring:message code="common.total" />：
							</td>
							<td style="text-align:right;">
								<label id="SumCountD2"></label>
							</td>
							<td style="text-align:right;">
								<label id="registerAmountShow2"></label>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div class="row">
			<div class="form-actions">
				<!-- 保存 -->
				<input id="btnSubmit" class="btn btn-primary" type="button" onclick="save()"
					value="<spring:message code='common.commit'/>" />
				&nbsp;
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="history.go(-1)"/>
					<%-- onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/> --%>
			</div>
		</div>

		<%-- 隐藏行（每笔金额行追加用） --%>
		<div style="display:none">
			<table id="tblDetailDataTemplate" class="table table-hover" cellpadding="0">
				<tbody id="tbody">
				<tr id="trDetailRowTemplate0">
					<td name="tdCol1" style="text-align:center;" width="${col1}"></td>

					<td name="tdCol2" style="text-align:center;" width="${col2}"></td>

					<td name="tdCol3" style="text-align:center;" UseType="detailInputAmount" width="${col3}"></td>
					<input name="hidAmountId" type="hidden" value=""/>
				</tr>
				</tbody>
			</table>
		</div>
	</form:form>
</body>
</html>