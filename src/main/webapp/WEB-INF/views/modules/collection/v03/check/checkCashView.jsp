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
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		var selctRowCode = "";			//每笔金额list选中行
		var inputSelectAmountId = "";	//每笔金额-选中行录入金额ID
		var realSelectTotalId = "";		//每笔金额-选中行实际金额ID
		var delRowObj;					//每笔金额-删除行 对象
		
		
		//页面初期化
		$(window).load(function() {
			//每笔金额数组的初期化
			var vCount = 0;
			<c:forEach items="${checkCashMain.amountList}" var="vItem1" varStatus="status">
				arrMainAdd();		//左侧金额数组的追加
				vCount = vCount +1;
			</c:forEach>
			$("#rowCount").val(vCount);
			
			//每笔金额数组的设定
			<c:forEach items="${checkCashMain.amountDetailList}" var="vItem2" varStatus="status">
				setAmountDetail("${vItem2.outRowNo}","${vItem2.parValue}","${vItem2.denomination}","${vItem2.countZhang}","${vItem2.detailAmount}");
			</c:forEach>
			
			$("#inputAmount").val(formatCurrencyFloat($("#inputAmount").val()));
			$("#checkAmount").val(formatCurrencyFloat($("#checkAmount").val()));
			
			clearList();		//清空列表
        });


		  
		//每笔金额-删除行询问
		 function delRowConfirm(vRow) {	
			delRowObj = vRow;
			var message = "<spring:message code='message.I7215'/>";
			confirmx(message, delAmountRow);
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
			var inputAmountId = $(vRow).children("td:eq(2)").attr("id");
			var checkAmountId = $(vRow).children("td:eq(3)").attr("id");
			
			clearList();		//面值列表的清空
			inputSelectAmountId = inputAmountId;
			realSelectTotalId = checkAmountId;
			disabledFaceValue(false);		//左侧面值可用
			objDetailShow(selctRowCode);	//右侧面值对象的显示
			
		}
		
		//----------------------------
		//---------页面右侧脚本-----------
		//----------------------------
		

		
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
			
			var sumCheckAmount = getListSumValue('detailCheckAmount');			//实际金额的合计
			$("#checkAmount").val(formatCurrencyFloat(sumCheckAmount));
			
			//外部总金额的取得
			var inputTotalAmount = $("#" + inputSelectAmountId).html();
			if (inputTotalAmount == undefined || inputTotalAmount == "" || isNaN(changeAmountToNum(inputTotalAmount))) {
				inputTotalAmount ="0";
			}
			inputTotalAmount = changeAmountToNum(inputTotalAmount);
			
			//差额的计算 
			var strdiff = sumOrderAmount * 1 - inputTotalAmount * 1 ;
			$("#diffAmountShow").html(formatCurrencyFloat(strdiff));
			
			return true;
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
		 * 取得一列的合计值（每笔金额list）
		 */
		function getListSumValue(vType) {
			var sumOrder = 0;
			var strFilter = "td[UseType^='" + vType + "']";
			$(strFilter).each(function(){
				var strAmount = $(this).html();
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
			$("#diffAmountShow").html("0.00");
			//每笔序号
			$("#detailNum").html("<spring:message code='message.I7240' />：");
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
		

		
		//右侧面值对象的显示
		function objDetailShow(vIndex){
			var arrDetailAmount = new Array();
			arrDetailAmount = arrMainAmount[vIndex];
			//每笔序号
			$("#detailNum").html("<spring:message code='message.I7240' />：" + (vIndex*1+1));
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

		
	</script>
</head>



<body>
	<ul class="nav nav-tabs">
		<!-- 款箱拆箱列表 -->
		<li><a onClick="window.top.clickSelect();" href="${ctx}/collection/v03/checkCash/"><spring:message code="door.checkCash.title" /><spring:message code="common.list" /></a></li>
		<!-- 款箱拆箱查看 -->
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="door.checkCash.title" /><spring:message code="common.view" /></a></li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="checkCashMain" action="${ctx}/collection/v03/checkCash/save" method="post" class="form-horizontal">
		<form:hidden id="hidOutNo" path="outNo"/>
		<sys:message content="${message}"/>		
		
		<%-- 明细 --%>
		<div class="row">
			<%-- 页面左侧 --%>
			<div class="span7" >
				
				<div class="control-group">
					<%-- 门店 --%>
					<label ><spring:message code="door.public.cust" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input id="custName" path="custName" htmlEscape="false" readOnly="readOnly" style="width:515px;text-align:left;"    class="required" />
				</div>
				
				<div class="control-group">
					<%-- 每笔金额 --%>
					<label ><spring:message code="door.checkCash.detailMoney" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="detailAmount" 
								cssClass="money"  
								style="width:515px;text-align:left;"  
								readOnly="readOnly" 
								maxlength="9"/>
				</div>
				
				
				<c:choose>
					<c:when test="${checkCashMain.dataFlag eq '1'}">
						<c:set var="col1" value="35px"/>
						<c:set var="col2" value="60px"/>
						<c:set var="col3" value="100px"/>
						<c:set var="col4" value="100px"/>
						<c:set var="col5" value="150px"/>
						<c:set var="colDisplay2" value=""/>
					</c:when>
					<c:otherwise>
						<c:set var="col1" value="45px"/>
						<c:set var="col2" value="0px"/>
						<c:set var="col3" value="120px"/>
						<c:set var="col4" value="120px"/>
						<c:set var="col5" value="160px"/>
						<c:set var="colDisplay2" value="display:none"/>
					</c:otherwise>
				</c:choose>
				
				<%-- 每笔金额明细 --%>
				<div class="control-group">
					<input type="hidden" id="rowCount" name="rowCount" value="0" />
						
					<div  style="width:555px">
						 <table id="tblDetailData" class="table " cellpadding="0">
							 <thead>
								<tr style="border-bottom:none;border-right:none;background:#FFFFFF;"  class="fixedThead">
									<%-- 序号 --%>
									<th style="text-align: center" width="${col1}"><spring:message code="common.seqNo" /></th>
									<%-- 包号 --%>
									<th style="text-align: center;${colDisplay2}" width="${col2}"><spring:message code="door.checkCash.packNum" /></th>
									<%-- 录入金额 --%>
									<th style="text-align: center" width="${col3}"><spring:message code="door.checkCash.inputMoney" /></th>
									<%-- 实际金额 --%>
									<th style="text-align: center" width="${col4}"><spring:message code="door.checkCash.realMoney" /></th>
									<%-- 操作 --%>
									<th style="text-align: center" width="${col5}"><spring:message code='common.operation'/></th>
								</tr>
							</thead>
							<tbody id="tbody"  class="scrollTbody">
								<c:forEach items="${checkCashMain.amountList}" var="amountItem" varStatus="status">
									<tr id="trDetailRow_${status.index + 1}" >
										<td name="tdCol1" style="text-align: left;" width="${col1}">${status.index + 1}</td>
										<td name="tdCol2" style="text-align:left;${colDisplay2}" width="${col2}">
											${amountItem.packNum}
										</td>
										<td name="tdCol3" style="text-align:right;" width="${col3}" id="tdInputAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.inputAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										<td name="tdCol4" style="text-align:right;" UseType="detailCheckAmount" width="${col4}" id="tdCheckAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.checkAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										
										<td name="tdCol5" style="text-align:center;margin: 0px;padding:4px" width="${col5}" valign="top" >
										</td>
										<input id="hidIndex_${amountItem.outRowNo}"  type="hidden" value="${status.index}"/>
										<input  name="hidAmountId"  type="hidden" value="${amountItem.id}"/>
									</tr>
								</c:forEach>
							</tbody>
						 </table> 
					</div>
				</div>
				
				<div class="control-group" style="margin-top: -10px;">
					<%-- 录入总金额(元) --%>
					<label ><spring:message code="door.checkCash.inputSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="inputAmount" 
								path="inputAmount"  
								htmlEscape="false"  
								cssClass="required" 
								readonly="true" 
								style="width:515px;text-align:right;background-color:#E6E6E6"   />
				</div>
				
				<div class="control-group">
					<%-- 实际总金额(元) --%>
					<label ><spring:message code="door.checkCash.realSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="checkAmount" 
								path="checkAmount" 
								htmlEscape="false" 
								readonly="true" 
								maxlength="9" 
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
					<form:textarea id="remarks"  path="remarks" readOnly="readOnly"  htmlEscape="false" rows="3" maxlength="60" style="width:515px;ime-mode:active;"/>
				</div>

			</div>
			
			<%-- 页面右侧 --%>
			<div class="span5">
				<%-- 每笔序号 --%>
				<label id="detailNum"><spring:message code="door.checkCash.detailNo" />：</label>
				<br>
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
											UseType="CountD"
											readOnly="readOnly"
											maxlength="6" class="digits" style="width:90px;text-align:right;ime-mode:disabled;"
											tabindex="1" /></td>
								<%-- 金额 --%>
								<td style="text-align:right;"><label id='totalAmount_${status.index}Show'><fmt:formatNumber value="${parItem.totalAmt}" pattern="#,###.##"/></label></td>
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
						
						<%-- 差额行 --%>
						<tr>
							<td style="text-align:left;" colspan="3" >
								<%-- 差额 --%>
								<span style="float:left">　<spring:message code="door.public.diffMoney" />：</span><span style="float:right"><label id="diffAmountShow"></label></span>
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
					onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/>
			</div>
		</div>
		
	
	</form:form>
</body>
</html>