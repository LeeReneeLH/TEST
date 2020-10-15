<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- ATM钞箱拆箱 -->
	<title><spring:message code="clear.atmCashBox.title" /></title>
	<meta name="decorator" content="default"/>
	
	<style type="text/css">
        .fixedThead{
            display: block;
            width: 100%;
        }
        .scrollTbody{
            display: block;
            height: 162px;
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
		 
		.icon {
		    background:url(${ctxStatic}/bootstrap/2.3.1/img/glyphicons-halflings.png) no-repeat;
		    width: 20px;
		    line-height: 20px;
		    display: inline-block;
		    cursor: pointer;
		}
		
		.icon-add
		{
		 background-position: -42px 0px;
		}
		 
    </style>
	
	
	<script type="text/javascript">
		var selctRowCode = "";			//每笔金额list选中行
		//var inputSelectAmountId = "";	//每笔金额-选中行录入金额ID
		var realSelectTotalId = "";		//每笔金额-选中行实际金额ID
		var delRowObj;					//每笔金额-删除行 对象
		var clearRowObj;				//每笔金额-清空行 对象
		
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
		
		
		//页面初期化
		$(window).load(function() {
			//每笔金额数组的初期化
			var vCount = 0;
			<c:forEach items="${clAtmMain.amountList}" var="vItem1" varStatus="status">
				arrMainAdd();		//左侧金额数组的追加
				vCount = vCount +1;
			</c:forEach>
			$("#rowCount").val(vCount);
			
			//每笔金额数组的设定
			<c:forEach items="${clAtmMain.amountDetailList}" var="vItem2" varStatus="status">
				setAmountDetail("${vItem2.outRowNo}","${vItem2.parValue}","${vItem2.denomination}","${vItem2.countZhang}","${vItem2.detailAmount}");
			</c:forEach>
			
			$("#checkAmount").val(formatCurrencyFloat($("#checkAmount").val()));
			
			clearList();		//清空列表
        });

		//钞箱编号-添加行 
		 function addDetailRow(){
			var cashBoxCode=$("#cashBoxCode").val();		//取值
			//每笔金额的检查
		 	if(cashBoxCode == null || cashBoxCode=='' ) {
		 		//请输入钞箱编号
				 alertx("<spring:message code='message.I7302' />");
				 return;
			 }
			
			 
			 var num=$("#rowCount").val();//取值
			 num++;
			 var newRow = $("#trDetailRowTemplate0").clone(true).attr("id","trDetailRow_"+num);
			 //newRow.removeAttr("style");
			 // 添加一行
			 newRow.appendTo("#tblDetailData");
			 
			 // tr单元格赋值
			 //钞箱编号
			 newRow.children('td:eq(1)').text(cashBoxCode);
			 newRow.children('td:eq(1)').attr("id","tdCashBoxCode_" + num);
			 
			 //金额
			 newRow.children('td:eq(2)').attr("id","tdCheckAmount_" + num);
			 
			
			 $('#rowCount').val(num);//重新赋值
			 // 行追加后每笔金额清空
			 $("#cashBoxCode").val('');
			//总金额重新赋值
			renewAmount();
			arrMainAdd();		//左侧金额数组的追加
			selectRow(newRow); 	//每笔金额选中
			$("#moneyCountD_0").focus();
			var tbody1 = document.getElementById('tbody');
			tbody1.scrollTop = tbody1.scrollHeight;
		 } 
		
	
		
		//每笔金额-行选择
		$(function() {
			$("#tbody tr").click(function(e) {
			 	selectRow(this);
			 });
		});
		
		//每笔金额-选中行处理
		function selectRow(vRow){
		 	$(vRow).addClass("rowSel").siblings().removeClass("rowSel");
			var rowIndex = $(vRow).children("td:eq(0)").text();
			selctRowCode = rowIndex * 1 - 1;
			var checkAmountId = $(vRow).children("td:eq(2)").attr("id");
			clearList();		//面值列表的清空
			realSelectTotalId = checkAmountId;
			disabledFaceValue(false);		//左侧面值可用
			objDetailShow(selctRowCode);	//右侧面值对象的显示
			
		}
		
		
		/**
		 * 每笔金额-标题部钞箱编号，录入金额的查找
		 *
		 * @param vKbn  ：1：钞箱编号 2：录入金额
		 */
		function SerchDetailRow(vKbn){
			var i=0;
			if (event.keyCode != 13){
				return;
			}
			 $("#tblDetailData").find("tr").each(function () {
	             // 从第三行开始
	             if (i>0) {
					var strRowPackNum = getRealValue($(this).children('td:eq(1)').text());								//钞箱编号
					var strRowInputAmount = changeAmountToNum(getRealValue($(this).children('td:eq(2)').text()));		//录入金额     
					//钞箱编号 
					if (vKbn == "1"){
						var packNumSearch = $('#packNumSearch').val();
						if (strRowPackNum != "" && packNumSearch != "" && strRowPackNum == packNumSearch){
							selectRow(this);
							return;
						}
					}
		        }
	            i++;
			});
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
		
		
		
		
		//----------------------------
		//---------页面右侧脚本-----------
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
				arrMainUpd(selctRowCode);
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
			
			arrMainUpd(selctRowCode);
			
			
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
		 * 设置合计栏及差额栏
		 */
		function setSumDiff() {
			//取得一列的合计值
			var sumCountD = getSumValue('CountD');			//未清分(捆)
			var sumOrderAmount = getSumValue('Amount');		//金额(元)

			//合计值的设定
			$("#SumCountD").html(formatCurrencyNum(sumCountD));
			$("#registerAmountShow").html(formatCurrencyFloat(sumOrderAmount));
			$("#" + realSelectTotalId).html(formatCurrencyFloat(sumOrderAmount));		//明细实际金额的设定
			var sumCheckAmount = getListSumValue('detailCheckAmount');					//实际金额的合计
			$("#checkAmount").val(formatCurrencyFloat(sumCheckAmount));
			
			setDiffAmount();
			return true;
		}
		
		
		/**
		 * 差额的取得
		 */
		function getDiffAmount() {
			//清机金额
			var inputTotalAmount = $("#inputAmount").val();
			if (inputTotalAmount == undefined || inputTotalAmount == "" || isNaN(changeAmountToNum(inputTotalAmount))) {
				inputTotalAmount ="0";
			}
			inputTotalAmount = changeAmountToNum(inputTotalAmount);
			
			//清机金额
			var checkSumAmount = $("#checkAmount").val();
			if (checkSumAmount == undefined || checkSumAmount == "" || isNaN(changeAmountToNum(checkSumAmount))) {
				checkSumAmount ="0";
			}
			checkSumAmount = changeAmountToNum(checkSumAmount);
			
			//差额的计算 
			var strdiff =  inputTotalAmount * 1 - checkSumAmount * 1;
			return strdiff;
		}

		/**
		 * 设置差额栏
		 */
		function setDiffAmount() {
			$("#diffAmount").val(formatCurrencyFloat(getDiffAmount()));
		}

		/**
		 * 取得一列的合计值(面值列表)
		 */
		function getSumValue(vType) {
			var sumOrder = 0;
			var strFilter = "input[UseType^='" + vType + "']";
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
		
		/**
		 * 清空列表
		 */
		function clearList() {
	
			$("input[UseType^='RowIndex']").each(function(){
			    var strRowIndex = $(this).attr("value");
				var moneyCountDId = "moneyCountD_" + strRowIndex;					//未清分(捆)-数量列1
				var totalAmount = "totalAmount_" + strRowIndex;								//金额
				var totalAmountShow = "totalAmount_" + strRowIndex + "Show";				//金额
				//值的取得
				$("#" + moneyCountDId).val("");
				$("#" + totalAmount).val("");
				$("#" + totalAmountShow).html("");
			});
			
			$("#SumCountD").html("0");
			$("#registerAmountShow").html("0.00");
			//$("#diffAmountShow").html("0.00");
			$("#detailNum").html("每笔序号：");
			//左侧面值不可用
			disabledFaceValue(true);
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
		 * 左侧面值栏是否可用
		 */
		function disabledFaceValue(vFlag) {
			$("input[UseType^='CountD']").each(function(){
			    $(this).attr("disabled",vFlag); 
			});
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

		//----------------------------------------------------
		//--------------- 每笔金额列表和面值列表联动处理 ------------------
		//----------------------------------------------------
		//每笔金额列表数组
		var arrMainAmount = new Array();
		
		//左侧金额数组的追加
		function arrMainAdd(){
			var arrLength = arrMainAmount.length;
			arrMainAmount[arrLength] = createObjDetail();
		}
		
		//左侧金额数组的更新
		function arrMainUpd(vIndex){
			arrMainAmount[vIndex] = updateObjDetail(vIndex);
			//var obj = $("input[name='confirmBtn']");
			//obj[vIndex].style.visibility="visible";
		}
		
		//左侧金额数组的删除
		function arrMainDel(vIndex){
			arrMainAmount.splice(vIndex,1);
		}
		
		
		//明细金额的设定
		function setAmountDetail(vRowNo,vPayValue,vDenomination,vCount,vAmount){
			var arrIndex = "";
			var arrDetailAmount = new Array();
			arrIndex = $("#hidIndex_" + vRowNo).val();
			if (arrIndex == undefined || arrIndex == null || arrIndex == "") {
				return;
			}
			
			if ((arrMainAmount[arrIndex] == undefined || arrMainAmount[arrIndex] == null)) {
			}else{
				arrDetailAmount = arrMainAmount[arrIndex];
			}
	
			arrDetailAmount[getArrIndex(vPayValue)] = setObjDetail(vCount,vAmount,vDenomination);
			arrMainAmount[arrIndex] = arrDetailAmount;
		}
		
		//数字下标的取得
		function getArrIndex(vDeno){
			return vDeno * 100;
		}
		
		//面值明细行对象取得
		function getObjDetail(vIndex){
			var objDetail = new Object();
			if ((vIndex == undefined || vIndex == "")) {
				objDetail.count = "";
				objDetail.amount = "";
				objDetail.amountShow = "";
				objDetail.denomination = "";
			}else{
				var moneyCountDId = "moneyCountD_" + vIndex;					//张数
				var totalAmount = "totalAmount_" + vIndex;						//金额
				var totalAmountShow = "totalAmount_" + vIndex + "Show";			//金额（表示用）
				var moneyKeyId = "moneyKey_" + vIndex;							//券别
	
				objDetail.count = $("#" + moneyCountDId).val();
				objDetail.amount = $("#" + totalAmount).val();
				objDetail.amountShow = $("#" + totalAmountShow).html();
				objDetail.denomination = $("#" + moneyKeyId).val();
			}
			
			return objDetail;
		}
		
		
		
		//面值明细行对象设定
		function setObjDetail(vCount,vAmount,vDenomination){
			var objDetail = new Object();
			objDetail.count = vCount;
			objDetail.amount = vAmount;
			objDetail.denomination = vDenomination;
			objDetail.amountShow = formatCurrencyFloat(vAmount);
			return objDetail;
		}
		
		
		//右侧面值对象的做成
		function createObjDetail(){
			var arrDetailAmount = new Array();
			arrDetailAmount[getArrIndex(100)] = getObjDetail();
			arrDetailAmount[getArrIndex(50)] = getObjDetail();
			arrDetailAmount[getArrIndex(20)] = getObjDetail();
			arrDetailAmount[getArrIndex(10)] = getObjDetail();
			arrDetailAmount[getArrIndex(5)] = getObjDetail();
			arrDetailAmount[getArrIndex(2)] = getObjDetail();
			arrDetailAmount[getArrIndex(1)] = getObjDetail();
			arrDetailAmount[getArrIndex(0.5)] = getObjDetail();
			arrDetailAmount[getArrIndex(0.2)] = getObjDetail();
			arrDetailAmount[getArrIndex(0.1)] = getObjDetail();
			
			return arrDetailAmount;
		}
		
		//右侧面值对象的更新
		function updateObjDetail(vIndex){
			
			var arrDetailAmount = new Array();
			$("input[UseType^='RowIndex']").each(function(){
			    var strRowIndex = $(this).attr("value");
			    var parValueId = "parValue_" + strRowIndex;							//面值
				var parValue = $("#" + parValueId).val();							//面值下标
				arrDetailAmount[getArrIndex(parValue)] = getObjDetail(strRowIndex);
			});
			return arrDetailAmount;
		}
		
		//右侧面值对象的显示
		function objDetailShow(vIndex){
			var arrDetailAmount = new Array();
			arrDetailAmount = arrMainAmount[vIndex];
			$("#detailNum").html("每笔序号：" + (vIndex*1+1));
			$("input[UseType^='RowIndex']").each(function(){
			    var strRowIndex = $(this).attr("value");
			    var parValueId = "parValue_" + strRowIndex;								//面值
				var parValue = $("#" + parValueId).val();								//面值下标
				var objDetail = arrDetailAmount[getArrIndex(parValue)];
				var moneyCountDId = "moneyCountD_" + strRowIndex;						//张数
				var totalAmount = "totalAmount_" + strRowIndex;							//金额
				var totalAmountShow = "totalAmount_" + strRowIndex + "Show";			//金额（表示用）
				//值的设定
				$("#" + moneyCountDId).val(objDetail.count);
				$("#" + totalAmount).val(objDetail.amount);
				$("#" + totalAmountShow).html(objDetail.amountShow);
			});
			//设置合计栏及差额栏
			setSumDiff();
		}

		//控制检索可见（钞箱编号）
		 function searchVisible() {	
			if (document.getElementById("cashBox").style.display =='none'){
				document.getElementById("cashBox").style.display="block";//显示
			}else{
				document.getElementById("cashBox").style.display="none";//隐藏
			}
	     }
		
	</script>
</head>



<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/clear/v03/clAtm/"><spring:message code="clear.atmCashBox.title" /><spring:message code="common.list" /></a></li>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="clear.atmCashBox.title" /><spring:message code="common.view" /></a></li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="clAtmMain" action="${ctx}/clear/v03/clAtm/save" method="post" class="form-horizontal">
		<form:hidden id="hidOutNo" path="outNo"/>
		
		<input id="hidUpdateCnt"  type="hidden" value="${clAtmMain.updateCnt}"/>
		<sys:message content="${message}"/>		
		
		<%-- 明细 --%>
		<div class="row">
			<%-- 页面左侧 --%>
			<div class="span7" >
				<%-- 机构 --%>
				<div class="control-group">
					<label ><spring:message code="clear.atmCashBox.office"/></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input id="custName" path="custName" htmlEscape="false" readOnly="readOnly" style="width:475px;text-align:left;"    class="required" />
				</div>
				<%-- 清机金额 --%>
				<div class="control-group">
					<label ><spring:message code="clear.atmCashBox.clearAmount"/></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="inputAmount" 
								cssClass="money required"  
								style="width:475px;text-align:left;"  
								onblur = "setDiffAmount()"
								maxlength="12"
								readOnly="readOnly" 
								tabindex="1"
								/>
				</div>
				
				
				<c:set var="col1" value="35px"/>
				<c:set var="col2" value="120px"/>
				<c:set var="col4" value="120px"/>
				<c:set var="col5" value="150px"/>
				
				<%-- 每笔金额明细 --%>
				<div class="control-group">
					<input type="hidden" id="rowCount" name="rowCount" value="0" />
						
					<div  style="width:505px">
						 <table id="tblDetailData" class="table " cellpadding="0">
							 <thead>
								<tr style="border-bottom:none;border-right:none;background:#FFFFFF;"  class="fixedThead">
									<%-- 序号 --%>
									<th style="text-align: center" width="${col1}"><spring:message code="clear.atmCashBox.no"/></th>
									<%-- 钞箱编号 --%>
									<th style="text-align: center;" width="${col2}">
									  	<spring:message code="clear.atmCashBox.cashBoxCode"/><div class="icon  icon-add" onclick="searchVisible()"></div>
									  	<div id="cashBox" style="display:none">
									 	<input 	type="text" 
									 			id="packNumSearch" 
									 			name="packNumSearch" 
									 			maxlength="15" 
									 			value="" 
									 			onkeydown="SerchDetailRow(1)" 
									 			style="height:12px;width:95px;text-align:left;"> 
									 	</div>
									</th>
									<%-- 实际金额 --%>
									<th style="text-align: center" width="${col4}"><spring:message code="clear.atmCashBox.realAmountB"/></th>
									<%-- 操作 --%>
									<th style="text-align: center" width="${col5}"><spring:message code='common.operation'/></th>
								</tr>
							</thead>
							<tbody id="tbody"  class="scrollTbody">
								<c:forEach items="${clAtmMain.amountList}" var="amountItem" varStatus="status">
									<tr id="trDetailRow_${status.index + 1}" >
										<td name="tdCol1" style="text-align: left;" width="${col1}">${status.index + 1}</td>
										<td name="tdCol2" style="text-align:left;" width="${col2}">
											${amountItem.packNum}
										</td>
										<td name="tdCol4" style="text-align:right;" UseType="detailCheckAmount" width="${col4}" id="tdCheckAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.checkAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										
										<td name="tdCol5" style="text-align:center;margin: 0px;padding:4px" width="${col5}" valign="top" >

										</td>
										<input id="hidIndex_${amountItem.outRowNo}"  type="hidden" value="${status.index}"/>
										<input  name="hidAmountId"  type="hidden" value="${amountItem.id}"/>
										<form:hidden   path="amountInfo" value=""/>
										<form:hidden   path="amountDetailInfo" value=""/>
									</tr>
								</c:forEach>
							</tbody>
						 </table> 
					</div>
				</div>
				
				
				<div class="control-group" style="margin-top:-20px;">
					<%-- 实际总金额(元) --%>
					<label ><spring:message code="clear.atmCashBox.realSumAmount"/></label>
					<br>
					<form:input id="checkAmount" 
								path="checkAmount" 
								htmlEscape="false" 
								readonly="true" 
								maxlength="18" 
								cssClass="input-xxlarge" 
								style="width:475px;text-align:right;background-color:#E6E6E6"/>
				</div>

				<div class="control-group">
					<%-- 差额(元) --%>
					<label ><spring:message code="clear.atmCashBox.diffAmount"/></label>
					<br>
					<form:input id="diffAmount" 
								path="diffAmount"  
								htmlEscape="false"  
								cssClass="input-xxlarge" 
								readonly="true" 
								style="width:475px;text-align:right;background-color:#E6E6E6"   />
				</div>


				<div class="control-group">
					<%-- 加钞计划ID --%>
					<label ><spring:message code="clear.atmCashBox.cashPlanId"/></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="cashPlanId" 
								style="width:475px;text-align:left;"  
								class="required input-xxlarge"
								readOnly="readOnly" 
								maxlength="20"
								/>
				</div>

				<div class="control-group"  style="display:none">
					<%-- 笔数 --%>
					<label ><spring:message code="clear.atmCashBox.count"/></label>
					<br>
					<form:input id="boxCount" 
								path="boxCount"  
								htmlEscape="false"  
								cssClass="required input-xxlarge" 
								readonly="true" 
								style="width:475px;text-align:right;background-color:#E6E6E6"   />
				</div>
				
				<div class="control-group">
					<%-- 备注 --%>
					<label ><spring:message code="common.remark" /></label>
					<br>
					<form:textarea id="remarks"  path="remarks" htmlEscape="false" rows="2" readOnly="readOnly"  maxlength="60" style="width:475px;ime-mode:active;"/>
				</div>

			</div>
			
			<%-- 页面右侧 --%>
			<div class="span5" >
				<%-- 每笔序号 --%>
				<label id="detailNum"><spring:message code="clear.atmCashBox.rowCode"/>：</label>
				<br>
				<%-- 面值明细 --%>
				<table  id="contentTable" class="table table-hover">
					<thead>
						<tr>
							<%-- 面值 --%>
							<th style="text-align:center;line-height:33px;"><spring:message code="common.denomination"/></th>
							<%-- 张数 --%>
							<th style="text-align:center;line-height:33px;"><spring:message code="clear.public.zhangShu"/></th>
							<%-- 金额(元)--%>
							<th style="text-align:center;line-height:33px;"><spring:message code="clear.public.moneyFormat"/></th>
						</tr>
					</thead>

					<tbody>
						<c:forEach items="${clAtmMain.denominationList}" var="parItem" varStatus="status">
				
							<tr >
								<input id="row_${status.index}" UseType="RowIndex" type="hidden" value="${status.index}"/>
								
								<%-- 面值 --%>
								<td style="text-align:center;width:70px;line-height:32px;" >
									${parItem.moneyName}
									<form:hidden id="moneyKey_${status.index}"  path="denominationList[${status.index}].moneyKey" value="${parItem.moneyKey}"/>
									<form:hidden id="parValue_${status.index}" path="denominationList[${status.index}].moneyValue" value="${parItem.moneyValue}"/>
									<form:hidden path="denominationList[${status.index}].moneyName" value="${parItem.moneyName}"/>
								</td>

								<%-- 张数 --%>
									<td style="text-align:right;width:100px;line-height:32px;">
										<form:input id="moneyCountD_${status.index}" path="denominationList[${status.index}].columnValue1" 
											UseType="CountD"
											readOnly="readOnly" 
											maxlength="6" class="digits" style="width:90px;text-align:right;ime-mode:disabled;"
											tabindex="2" /></td>
								<%-- 金额 --%>
								<td style="text-align:right;line-height:32px;"><label id='totalAmount_${status.index}Show'><fmt:formatNumber value="${parItem.totalAmt}" pattern="#,###.##"/></label></td>
								<form:hidden  UseType="Amount" id="totalAmount_${status.index}" path="denominationList[${status.index}].totalAmt" />
							</tr>
						
						</c:forEach>
						<%-- 合计行 --%>
						<tr>
							<%-- 合计--%>
							<td style="text-align:center;" >
								<spring:message code="common.total"/>：
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
		</div>
		<div class="row">
			<div class="form-actions">
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/clAtm/back'"/>
			</div>
		</div>
		

	</form:form>
</body>
</html>