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
		var inputSelectAmountId = "";	//每笔金额-选中行录入金额ID
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
		
		$(window).load(function() {
			$("#inputAmount").val(formatCurrencyFloat($("#inputAmount").val()));
			$("#checkAmount").val(formatCurrencyFloat($("#checkAmount").val()));
			$("#diffAmount").val(formatCurrencyFloat($("#diffAmount").val()));
			$("#trueSumMoney").val(formatCurrencyFloat($("#trueSumMoney").val()));
			setSumDiff();
			setSumDiff2();
        });
		
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
			var strdiff = changeAmountToNum($("#inputAmount").val())- changeAmountToNum($("#checkAmount").val());
			$("#diffAmount").val(formatCurrencyFloat(strdiff));
			//笔数
			$('#boxCount').val(rowCnt);
	 	} 
			
		//----------------------------
		//---------页面右侧脚本-----------
		//----------------------------
		
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
		<!-- 设备缴存列表 -->
		<li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/" target="mainFrame"><spring:message code="door.panorama.equipDeposit" /></a></li>
		<!-- 封包缴存列表 -->
		<li><a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/packageList" target="mainFrame"><spring:message code="door.panorama.otherDeposit" /></a></li>
		<!-- 拆箱查看-->
		<li i class="active"><a><spring:message code="door.panorama.checkCash" /><spring:message code="common.view" /></a></li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="checkCashMain" action="${ctx}/collection/v03/checkCash/save" method="post" class="form-horizontal">
		<form:hidden id="hidOutNo" path="outNo"/>
		<form:hidden path="amountListStr"/>
		<form:hidden path="updateCnt"/>
		<input id="hidUpdateCnt"  type="hidden" value="${checkCashMain.updateCnt}"/>
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
					<%-- 包号 --%>
					<label ><spring:message code="door.doorOrder.packNum" /></label><span class="help-inline"><font color="red">*</font> </span>
					<br>
					<form:input path="rfid" style="width:515px;text-align:left;" readOnly="true"/>
				</div>
				<c:set var="col1" value="70px"/>
				<c:set var="col2" value="180px"/>
				<c:set var="col3" value="150px"/>
				<c:set var="col4" value="80px"/>
				<%-- 每笔金额明细 --%>
				<div class="control-group">
					<input type="hidden" id="rowCount" name="rowCount" value="0" />
					<div  style="width:550px">
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
					<%-- 实际总金额(元) --%>
					<label ><spring:message code="door.checkCash.realSumMoney" /><spring:message code="common.units.yuan.alone" /></label>
					<br>
					<form:input id="checkAmount" 
								path="checkAmount" 
								htmlEscape="false" 
								readonly="true" 
								cssClass="input-xxlarge" 
								style="width:515px;text-align:right;background-color:#E6E6E6"/>
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
					<form:textarea readOnly="readOnly"  id="remarks"  path="remarks" htmlEscape="false" rows="3" maxlength="60" style="width:515px;ime-mode:active;"/>
				</div>

			</div>
			<%-- 页面中部 --%>
			<div class="span4" style="margin-left: 80px">
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
											UseType="CountD"
											readOnly="readOnly"
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
			<div class="span4" style="margin-left: 80px">
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
											UseType="CountD2"
											readOnly="readOnly"
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
		<div id="displayDiv1" class="row">
			<div class="form-actions" style="width:100%">
				<%-- 返回 --%>
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="history.go(-1)"/>
					<%-- onclick="window.location.href='${ctx}/collection/v03/checkCash/back'"/> --%>
			</div>
		</div>
		
		<%-- 隐藏行（每笔金额行追加用） --%>
		<div  style="display:none" >
				 <table id="tblDetailDataTemplate" class="table table-hover" cellpadding="0">
						<tbody id="tbody">
							<tr id="trDetailRowTemplate0" >
							 <td name="tdCol1" style="text-align:center;" width="${col1}"></td>
							 
							<td name="tdCol2" style="text-align:center;" width="${col2}"></td>

							 <td name="tdCol3" style="text-align:center;" UseType="detailInputAmount" width="${col3}"></td>
							 
							<%--  <td name="tdcol4" style="text-align:center;margin: 0px;padding:4px" width="${col4}" valign="top" >
							 <nobr>
								 删除
								 <input type="button" 
								 		name="delBtn" 
								 		class="btn btn-primary" 
								 		value="<spring:message code='common.delete'/>"
								 		style="height:25px;margin-top: 0px;padding-top:0px" 
								 		onclick="delRowConfirm(this)"/>
							 </nobr>
							 </td> --%>
							 <input  name="hidAmountId"  type="hidden" value=""/>
							</tr>
						</tbody>
				</table> 
		</div>
	</form:form>
</body>
</html>