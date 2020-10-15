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
		var selctRowCode = "";			//每笔金额list选中行
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

			$("#btnSubmit").click(function(){
				if($('#inputAmount').val()==0){
					//总金额不能为0
					alertx("<spring:message code='message.I7205' />");
					return;
				}
				$("#inputForm").submit();
			});
		});
		
		
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

		//每笔金额-添加行 
		 function addDetailRow(){
			var detailAmount=$("#detailAmount").val();		//取值
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
			 
			 var num=$("#rowCount").val();//取值
			 num++;
			 var newRow = $("#trDetailRowTemplate0").clone(true).attr("id","trDetailRow_"+num);
			 //newRow.removeAttr("style");
			 // 添加一行
			 newRow.appendTo("#tblDetailData");
			 
			 // tr单元格赋值
			 //包号
			 newRow.children('td:eq(1)').text("");
			 //金额
			 newRow.children('td:eq(2)').text(formatCurrencyFloat(detailAmount));
			 newRow.children('td:eq(2)').attr("id","tdInputAmount_" + num);
			 newRow.children('td:eq(3)').attr("id","tdCheckAmount_" + num);
			 
			
			 $('#rowCount').val(num);//重新赋值
			 // 行追加后每笔金额清空
			 $("#detailAmount").val('');
			//总金额重新赋值
			renewAmount();
			arrMainAdd();		//左侧金额数组的追加
			selectRow(newRow); 	//每笔金额选中
			$("#moneyCountD_0").focus();
			var tbody1 = document.getElementById('tbody');
			tbody1.scrollTop = tbody1.scrollHeight;
		 } 
		
		  
		//每笔金额-删除行询问
		 function delRowConfirm(vRow) {	
			delRowObj = vRow;
			var message = "<spring:message code='message.I7215'/>";
			var tr=delRowObj.parentNode.parentNode.parentNode;
			var amountId = $(tr).find('input[name=hidAmountId]').val(); 					//每笔金额ID
			if (amountId == undefined || amountId == "") {
				//数据无的场合：询问-->删除
				confirmx(message, delAmountRow);
			}else{
				//数据库有的场合：询问-->授权-->删除
				confirmx(message, toAuthorize);
			}
			
	     }
	  
		 //每笔金额-授权跳转
		 function toAuthorize(){ 
			 showAuthorize("","2");
		 }
		
		 //每笔金额-删除行 
		 function delAmountRow(){ 
			 var vNextRow;
			 var tr=delRowObj.parentNode.parentNode.parentNode;
			 var vData;

			 
		 	//删除处理(后台)
			var parms = new Object();
			var amountId = $(tr).find('input[name=hidAmountId]').val(); 					//每笔金额ID
			if ((amountId == undefined)) {
				amountId = "";
			}
			var hidUpdateCnt = $("#hidUpdateCnt").val();
			$.ajax({
				url : ctx + '/collection/v03/checkCash/deleteDetail?amountId=' + amountId + '&oldUpdateCnt=' + hidUpdateCnt,
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
			 
			var vResult = vData.result;
			
			//后台非正常的场合
			if (vResult != "OK"){
				alertx(vResult);
				return false;
			}

			//删除处理(前台)
			//主表已删除的场合
			if (vData.mainDel == "1"){
				$("#hidOutNo").val("");								//单号
			}
			$("#hidUpdateCnt").val(vData.chkUpdateCnt);				//更新回数
			 vNextRow = tr.nextElementSibling;
			 var rowIndex = getRealValue($(tr).children("td:eq(0)").text());

			 var tbody=tr.parentNode;
			 tbody.removeChild(tr);
			 arrMainDel(rowIndex*1-1);
			 
			// 总金额重新赋值
			 renewAmount();
			
			 if (vNextRow != null){
				 selectRow(vNextRow);	//下一行为选中行
			 }else{
				 clearList();			//面值列表的清空
			 }
			 
		 }

			//每笔金额-清空行询问
		 function clearRowConfirm(vRow) {	
			clearRowObj = vRow;
			var message = "<spring:message code='message.I7223'/>";
			confirmx(message, clearAmountRow);
	     }
		 
		 //每笔金额-清空行 
		 function clearAmountRow(){ 
			 var tr=clearRowObj.parentNode.parentNode.parentNode;
			 var vData;
			 
			//清空处理
			 var rowIndex = getRealValue($(tr).children("td:eq(0)").text());
			 $(tr).children('td:eq(3)').text("");
			 var detailTitle = $("#detailNum").html();
			 clearList();					//面值列表的清空
			 disabledFaceValue(false);		//左侧面值可用
			 arrMainUpd(rowIndex*1-1);		//左侧金额数组的更新
			 $("#detailNum").html(detailTitle);
			// 总金额重新赋值
			 renewAmount();
		 }
			
			
		 //每笔金额-确认行
		 function confirmAmountRow(vBtn){ 
			 //门店检查
			 var custNameId = $('#custNameId').val();
			 if ((custNameId == undefined || custNameId == "")) {
				 alertx("<spring:message code='message.I7214' />");
				 return false;
			 }
			 
			 var tr=vBtn.parentNode.parentNode.parentNode;
			 var inputAmountValue = getRealValue($(tr).children("td:eq(2)").text());
			 var checkAmountValue = getRealValue($(tr).children("td:eq(3)").text());
			 if (checkAmountValue == "" || parseFloat(changeAmountToNum(checkAmountValue)) == 0){
				 //实际输入额不能为空
				 alertx("<spring:message code='message.I7216' />");
				 return false;
			 }
			 
			 //输入额和实际额不等的场合
			 if (parseFloat(changeAmountToNum(inputAmountValue)) != parseFloat(changeAmountToNum(checkAmountValue)) ){
				 //授权验证
				 showAuthorize(vBtn,"1");
			 }else{
				 saveAmountRow(vBtn,"");
			 }
			 return;
		 }
		 
		/**
		 * 每笔金额-录入和实际金额不等的场合授权验证
		 *
		 * @param vBtn  ：按钮对象
		 * @param vType ：1=每笔金额行的确认，2=每笔金额行的删除
		 */
		function showAuthorize(vBtn,vType) {
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
									//确认
									if (vType == "1"){
										saveAmountRow(vBtn,vData.userId);
									}
									//删除
									if (vType == "2"){
										delAmountRow(vData.userId);
									}
									return true;
								}else{
									//异常
									alertx(vResult);
								}
								return false;
							}
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "hidden");
						}
			});
		}
			
			
		//每笔金额-确认行的保存
		function saveAmountRow(vBtn,vAuthUserId){ 
			 var tr=vBtn.parentNode.parentNode.parentNode;
			 var rowIndex = getRealValue($(tr).children("td:eq(0)").text());
			 rowIndex = rowIndex*1-1;
			 
		 	//保存处理
			var parms = new Object();
			parms['outNo'] = $("#hidOutNo").val();											//单号
			parms['custNo'] = $("#custNameId").val();										//门店ID
			parms['remarks'] = $("#remarks").val();											//备注
			parms['inputAmount'] = changeAmountToNum(getRealValue($(tr).children("td:eq(2)").text()));	//拆箱金额
			var amountId = $(tr).find('input[name=hidAmountId]').val(); 					//每笔金额ID
			if ((amountId == undefined || amountId == "")) {
				parms['amountId'] = "";
			}else{
				parms['amountId'] = amountId;
			}
			
			//面值
			var arrDetail = arrMainAmount[rowIndex];
			var strCheckAmount = "";
			for(var item in arrDetail) {
				strCheckAmount =strCheckAmount + "," + arrDetail[item].denomination + "-" + arrDetail[item].count
			}
			parms['payValueJoin'] = strCheckAmount;					//面值列表(面值-张数)
			parms['authUserId'] = vAuthUserId;						//授权用户ID
			parms['oldUpdateCnt'] = $("#hidUpdateCnt").val();		//原有更新者ID
			
			$.post('${ctx}/collection/v03/checkCash/confirmDetail', parms, function(data){
				var vResult = data.result;
				if (vResult == "OK"){
					$("#hidOutNo").val(data.outNo);											//单号
					$(tr).find('input[name=hidAmountId]').val(data.amountId); 				//每笔金额ID
					var amountId1 = $(tr).find('input[name=hidAmountId]').val(); 			//每笔金额ID
					$("#hidUpdateCnt").val(data.chkUpdateCnt);								//更新回数
					vBtn.style.visibility="hidden";
				}else{
					alertx(vResult);
				}
			},"json");
			
		}

		// 左侧合计金额及笔数的再计算
		function  renewAmount(){ 
			 var i=0
			 var rowCnt = 0;
			 $("#tblDetailData").find("tr").each(function () {
                  // 从第三行开始
                  if (i>0) {
                	  // 第二列单元格的值eq(索引)
                	  rowCnt++;
                      $(this).children('td:eq(0)').text(rowCnt);
                  }
                 i++;
			});
			 
			var sumInputAmount = getListSumValue('detailInputAmount');			//录入金额的合计
			$("#inputAmount").val(formatCurrencyFloat(sumInputAmount));
			
			var sumCheckAmount = getListSumValue('detailCheckAmount');			//实际金额的合计
			$("#checkAmount").val(formatCurrencyFloat(sumCheckAmount));
			 
			$('#boxCount').val(rowCnt);						//笔数
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
		
		
		/**
		 * 每笔金额-标题部包号，录入金额的查找
		 *
		 * @param vKbn  ：1：包号 2：录入金额
		 */
		function SerchDetailRow(vKbn){
			var i=0;
			if (event.keyCode != 13){
				return;
			}
			 $("#tblDetailData").find("tr").each(function () {
	             // 从第三行开始
	             if (i>0) {
					var strRowPackNum = getRealValue($(this).children('td:eq(1)').text());								//包号
					var strRowInputAmount = changeAmountToNum(getRealValue($(this).children('td:eq(2)').text()));		//录入金额     
					//包号 
					if (vKbn == "1"){
						var packNumSearch = $('#packNumSearch').val();
						if (strRowPackNum != "" && packNumSearch != "" && strRowPackNum == packNumSearch){
							selectRow(this);
							return;
						}
					}
					
					//录入金额
					if (vKbn == "2"){
						var inputAmountSearch = $('#inputAmountSearch').val();
						if (strRowInputAmount != ""  && inputAmountSearch != "" &&  isNaN(inputAmountSearch) == false && parseFloat(strRowInputAmount) == parseFloat(inputAmountSearch)){
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
			var obj = $("input[name='confirmBtn']");
			obj[vIndex].style.visibility="visible";
	
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
		<li><a href="${ctx}/collection/v03/checkCash/"><spring:message code="door.checkCash.title" /><spring:message code="common.list" /></a></li>
		<!-- 款箱拆箱登记(修改) -->
		<li class="active">
			<a href="${ctx}/collection/v03/checkCash/form?id=${checkCashMain.outNo}">
				<shiro:hasPermission name="check:checkCash:edit">
					<c:choose>
						<c:when test="${not empty checkCashMain.outNo}">
							<spring:message code="door.checkCash.title" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="door.checkCash.title" /><spring:message code="common.add" /> 
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			</a>
		</li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="checkCashMain" action="${ctx}/collection/v03/checkCash/save" method="post" class="form-horizontal">
		<form:hidden id="hidOutNo" path="outNo"/>
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
					<sys:treeselect id="custName" name="custNo"
							value="${checkCashMain.custNo}" labelName="custName"
							labelValue="${checkCashMain.custName}" title="门店"
							url="/sys/office/treeData" cssClass="required "  cssStyle="width:470px;"
						    notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
						    isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
				</div>
				
				
				<div class="control-group">
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
									<th style="text-align: center;${colDisplay2}" width="${col2}">
									  	<spring:message code="door.checkCash.tickertape" />
									  	<br>
									 	<input 	type="text" 
									 			id="packNumSearch" 
									 			name="packNumSearch" 
									 			maxlength="5" 
									 			value="" 
									 			onkeydown="SerchDetailRow(1)" 
									 			style="height:12px;width:45px;text-align:left;${colDisplay2}"> 
									</th>
									<%-- 录入金额 --%>
									<th style="text-align: center" width="${col3}">
										<spring:message code="door.checkCash.inputMoney" />
									  	<br>
									 	<input 	type="text" 
									 			id="inputAmountSearch" 
									 			name="inputAmountSearch" 
									 			maxlength="13" 
									 			value="" 
									 			onkeydown="SerchDetailRow(2)" 
									 			style="height:12px;width:85px;text-align:left;${colDisplay2}"> 
									</th>
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
										<td name="tdCol3" UseType="detailInputAmount" style="text-align:right;" width="${col3}" id="tdInputAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.inputAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										<td name="tdCol4" style="text-align:right;" UseType="detailCheckAmount" width="${col4}" id="tdCheckAmount_${status.index + 1}">
											<fmt:formatNumber value="${amountItem.checkAmount}" type="currency" pattern="#,##0.00"/>
										</td>
										
										<td name="tdCol5" style="text-align:center;margin: 0px;padding:4px" width="${col5}" valign="top" >
											<nobr>
												<%-- 确认 --%>
												<input 	name="confirmBtn" 
														type="button" 
														class="btn btn-primary" 
														style="visibility:hidden;height:25px;margin-top: 0px;padding-top:0px" 
														value="<spring:message code='common.confirm'/>"
														onclick="confirmAmountRow(this)"/>
												 &nbsp;&nbsp;&nbsp;
												<c:choose>
													<c:when test="${amountItem.dataFlag eq '1'}">
														<%-- 清空 --%>
														 <input type="button" 
														 		name="delBtn" 
														 		class="btn btn-primary" 
														 		value="<spring:message code='common.clear'/>"
														 		style="height:25px;margin-top: 0px;padding-top:0px;${btnDelDisplay}" 
														 		onclick="clearRowConfirm(this)"/>
													</c:when>
													<c:otherwise>
														<%-- 删除 --%>
														 <input type="button" 
														 		name="delBtn" 
														 		class="btn btn-primary" 
														 		value="<spring:message code='common.delete'/>"
														 		style="height:25px;margin-top: 0px;padding-top:0px;" 
														 		onclick="delRowConfirm(this)"/>
													</c:otherwise>
												</c:choose>
											</nobr>
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
					<form:textarea id="remarks"  path="remarks" htmlEscape="false" rows="3" maxlength="60" style="width:515px;ime-mode:active;"/>
				</div>

			</div>
			
			<%-- 页面右侧 --%>
			<div class="span5" >
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
											onkeyup="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
											onchange="value=value.replace(/[^0-9]/g,'');computeAmount('${status.index}',true)" 
											onkeydown="value=value.replace(/[^0-9]/g,'');setNextFocus(this)" 
											UseType="CountD"
											maxlength="6" class="digits" style="width:90px;text-align:right;ime-mode:disabled;"
											tabindex="2" /></td>
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
		
		<%-- 隐藏行（每笔金额行追加用） --%>
		<div  style="display:none" >
				 <table id="tblDetailDataTemplate" class="table table-hover" cellpadding="0">
						<tbody id="tbody">
							<tr id="trDetailRowTemplate0" >
							 <td name="tdCol1" style="text-align: left;" width="${col1}"></td>
							 
							<td name="tdCol2" style="text-align:left;${colDisplay2}" width="${col2}"></td>

							 <td name="tdCol3" style="text-align:right;" UseType="detailInputAmount" width="${col3}"></td>
							 
							 <td name="tdCol4" style="text-align:right;" UseType="detailCheckAmount" width="${col4}"></td>
							 
							 <td name="tdCol5" style="text-align:center;margin: 0px;padding:4px" width="${col5}" valign="top" >
							 <nobr>
							 	<%-- 确认 --%>
								 <input name="confirmBtn" 
								 		type="button" 
								 		class="btn btn-primary" 
								 		style="height:25px;margin-top: 0px;padding-top:0px" 
								 		value="<spring:message code='common.confirm'/>"
								 		onclick="confirmAmountRow(this)"/>
								 &nbsp;&nbsp;&nbsp;
								 <%-- 删除 --%>
								 <input type="button" 
								 		name="delBtn" 
								 		class="btn btn-primary" 
								 		value="<spring:message code='common.delete'/>"
								 		style="height:25px;margin-top: 0px;padding-top:0px" 
								 		onclick="delRowConfirm(this)"/>
							 </nobr>
							 </td>
							 <input  name="hidAmountId"  type="hidden" value=""/>
							</tr>
						</tbody>
				</table> 
		</div>
	</form:form>
</body>
</html>