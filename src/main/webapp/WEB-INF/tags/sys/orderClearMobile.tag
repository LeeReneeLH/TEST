<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="dataSource" required="true" type="java.util.List" description="数据源"%>  
<%@ attribute name="bindListName" required="true" type="java.lang.String" description="绑定的List名"%>
<%@ attribute name="totalAmountId" required="true" type="java.lang.String" description="总金额的Id"%>
<%@ attribute name="allowInput" required="false" type="java.lang.Boolean"  description="文本框可填写"%>

<script type="text/javascript">
	$(document).ready(function() {
		
		if ('${allowInput}'=='false') {
			$("#contentTable input").attr("readOnly","readOnly");
			$("#contentTable input").css("background-color","#E6E6E6");
		}
	});
	/**
	 * 计算金额
	 * @param vId 	行索引
	 *        vKbn 	是否计算差额
	 */
	function computeAmount(vId,vKbn){
		
		
		//Id的取得
		var toId = "totalAmount_" + vId;				//金额(元)
		var moneyCountDId = "moneyCountD_" + vId;		//未清分(捆)-数量列1
		var moneyCountYId = "moneyCountY_" + vId;		//已清分(捆)-数量列2
		var goodsValueId = "parValue_" + vId;			//实际面值
		//值的取得
		var moneyCountD = $("#" + moneyCountDId).val();
		var moneyCountY = $("#" + moneyCountYId).val();
		var goodsValue = $("#" + goodsValueId).val();
		
		//面值对应的捆数未输入的场合，退出
		if ((moneyCountD == undefined || moneyCountD == "") && (moneyCountY == undefined || moneyCountY == "")) {
			$("#" + toId).val("");
			$("#" + toId + "Show").html("");
			setSumDiff();		//设置合计栏及差额栏
			return;
		}
		//未清分(捆)
		if (moneyCountD == undefined || moneyCountD == "") {
			moneyCountD = "0";
		}
		//已清分(捆)
		if (moneyCountY == undefined || moneyCountY == "") {
			moneyCountY = "0";
		}
		//金额的计算及显示
		var amount = 0;
		if (typeof(goodsValue) != "undefined" && goodsValue != "") {
			amount =(moneyCountD * 1 + moneyCountY * 1) * 1000 * goodsValue;
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
	
	function formatCurrency(num) {
		if (isInteger(num)){
			var intT = toInt(num);
			return formatCurrencyNum(intT);
		}else{
			return formatCurrencyFloat(num);
		}
	}

	function toInt(number) {
	    return number*1 | 0 || 0;
	}
	
	function isInteger(obj) {
		 return obj%1 === 0
	}

	/**
	 * 设置合计栏及差额栏
	 */
	function setSumDiff() {
		//取得一列的合计值
		var sumCountD = getSumValue('CountD');			//未清分(捆)
		var sumCountY = getSumValue('CountY');			//已清分(捆)
		var sumOrderAmount = getSumValue('Amount');		//金额(元)
		//合计值的设定
		$("#SumCountD").html(formatCurrency(sumCountD));
		$("#SumCountY").html(formatCurrency(sumCountY));
		$("#registerAmountShow").html(formatCurrencyFloat(sumOrderAmount));
		
		//外部总金额的取得
		var inAmount = $("#<%=totalAmountId%>").val();
		
		if (inAmount == undefined || inAmount == "" || isNaN(changeAmountToNum(inAmount))) {
			inAmount ="0";
		}
		inAmount = changeAmountToNum(inAmount);
		
		//差额的计算
		var strdiff = inAmount * 1 - sumOrderAmount * 1;
		$("#diffAmountShow").html(formatCurrencyFloat(strdiff));
		
		 //if (typeof outFunc === 'function') {
		//	 outFunc();
		 //}
		 
		return true;
	}
	
	/**
	 * 取得一列的合计值
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
	 * 初期化处理（修正的场合）
	 */
	function initShow() {
		//设置合计栏及差额栏
		setSumDiff();
	}
	
	/**
	 * 向onload事件追加函数
	 * @param 函数
	 * @returns
	 */
	function addLoadEvent(func) {
		  var oldonload = window.onload;			//得到上一个onload事件的函数
		  if (typeof window.onload != 'function') {	//判断类型是否为'function',注意typeof返回的是字符串
			  window.onload = func;
		  } else {  
		    window.onload = function() {
		      oldonload();		//调用之前覆盖的onload事件的函数
		      func();			//调用当前事件函数
		    }
		  }
	}
	
	//onload事件追加函数
	addLoadEvent(initShow);

	
	/**
	 * 整体刷新
	 */
	function AllRefresh() {
		/* 面值对应金额的格式化 */
		$("input[UseType^='RowIndex']").each(function(){
		    var strRowIndex = $(this).attr("value");
		    computeAmount(strRowIndex,false);	//明细行再计算
		});
		//设置合计栏及差额栏
		setSumDiff();
	}
	
	/**
	 * 差额的取得
	 */
	function getDiffValue() {
		var amount  = changeAmountToNum($("#diffAmountShow").html());
		return amount;
	}
	
	// 回车事件，同列下个文本框
	function changeEnter(event,idName,index){
		var index = parseInt(index) + 1;
		var id = idName + index;
	    if(event.keyCode==13){
	    	//event.keyCode=9;
	    	 $("#"+id).focus();
	    } 
	}
	
</script>
<table  id="contentTable"  border=1  cellpadding=1 class="table" style="border-left-width: 0px; 
		border-right-width: 0px; border-color: #FFFFFF; width: 100%; border-collapse: collapse;">
	<colgroup>
		<col width=20%>
		<col width=20%>
		<col width=20%>
		<col width=40%>
	</colgroup>
	<thead>
		<tr>
			<!-- 面值 -->
			<th style="text-align:center;"><spring:message code="common.denomination"/></th>
			<!-- 未清分(捆) -->
			<th style="text-align:center;"><spring:message code="clear.public.noneClear"/>(<spring:message code="clear.public.bundle"/>)</th>
			<!-- 已清分(捆) -->
			<th style="text-align:center;"><spring:message code="clear.public.haveClear"/>(<spring:message code="clear.public.bundle"/>)</th>
			<!-- 金额(元)-->
			<th style="text-align:center;"><spring:message code="clear.public.moneyFormat"/></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dataSource}" var="parItem" varStatus="status">

			<tr>
				<input id="row_${status.index}" UseType="RowIndex" type="hidden" value="${status.index}"/>
				
				<!-- 面值 -->
				<td style="text-align:center;width:70px;" >
					${parItem.moneyName}
					<input type="hidden" id="${bindListName}[${status.index}].moneyKey" name="${bindListName}[${status.index}].moneyKey" value="${parItem.moneyKey}"/>
					<input type="hidden" id="parValue_${status.index}" name="${bindListName}[${status.index}].moneyValue" value="${parItem.moneyValue}"/>
					<input type="hidden" id="${bindListName}[${status.index}].moneyName" id="${bindListName}[${status.index}].moneyName" value="${parItem.moneyName}"/>
				</td>		
<%-- 				<c:set var="vReadOnly" value=""/>
				<c:if test="${allowInput == false}">
					<c:set var="vReadOnly" value="readOnly"/>
				</c:if> --%>
				
				<!-- 未清分（捆） -->
					<td style="text-align:right;">
						<input type="text" data-role="none" id="moneyCountD_${status.index}" name="${bindListName}[${status.index}].columnValue1" value="${parItem.columnValue1}"
							onkeyup="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
							onchange="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)"
							onkeydown="changeEnter(event,'moneyCountD_','${status.index}')" 
							UseType="CountD"
							maxlength="3" class="digits" style="text-align:right;ime-mode:disabled;height:80%;"
							tabindex="1"/></td>
				<!-- 已清分（捆） -->
					<td style="text-align:right;">
						<input type="text" data-role="none" id="moneyCountY_${status.index}" name="${bindListName}[${status.index}].columnValue2" value="${parItem.columnValue2}"
							onkeyup="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
							onchange="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
							onkeydown="changeEnter(event,'moneyCountY_','${status.index}')" 
							UseType="CountY"
							maxlength="3" class="digits" style="text-align:right;ime-mode:disabled;height:80%;"
							tabindex="1"/></td>
				<!-- 金额 -->
				<td style="text-align:right;">
					<label id='totalAmount_${status.index}Show'><fmt:formatNumber value="${parItem.totalAmt}" pattern="#,##0.00#"/></label>
					<input type="hidden"  UseType="Amount" id="totalAmount_${status.index}" name="${bindListName}[${status.index}].totalAmt" value="${parItem.totalAmt}"/>
				</td>
				
			</tr>
		
		</c:forEach>
		<!-- 合计 -->
		<tr>
			<td style="text-align:center;" >
				<spring:message code="common.total" />：
			</td>
			<td style="text-align:right;">
				<label id="SumCountD"></label>
			</td>
			<td style="text-align:right;">
				<label id="SumCountY"></label>
			</td>
			<td style="text-align:right;">
				<label id="registerAmountShow"></label>
			</td>
		</tr>
		
		<!-- 差额 -->
		<tr>
			<td style="text-align:center;"><spring:message code="clear.public.banlance" />：</td>
			<td style="text-align:left;" colspan="3" >
				<span style="float:right"><label id="diffAmountShow"></label></span>
			</td>
		</tr>
	</tbody>
</table>