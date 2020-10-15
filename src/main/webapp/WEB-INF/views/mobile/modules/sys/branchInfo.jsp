<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE >
<html>
<head>
<meta charset="utf-8">
<title>${fns:getConfig('productName')}</title>
<!--<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/app.css">   -->

<!-- 开始设置桌面图标，自适应屏幕   -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="viewport"
	content="width=device-width,initial-scale=1, minimum-scale=1.0, maximum-scale=1, user-scalable=no">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,user-scalable=no,maximum-scale=1.0">
<!-- 结束设置桌面图标，自适应屏幕   -->

<link rel="stylesheet"
	href="${ctxStatic}/jqueryMobile/css/jquery.mobile-1.3.2.min.css">
<script src="${ctxStatic}/jqueryMobile/js/jquery.js"></script>
<script src="${ctxStatic}/jqueryMobile/js/jquery.mobile-1.3.2.min.js"></script>
<link rel="stylesheet" href="${ctxStatic}/jqueryMobile/css/modify.css">



<style type="text/css">
.table thead {
	background-color: #bbdcfc !important;
	color: #333;
	border: 1px solid #fff !important;
}

a {
	text-decoration: none;
}

table a {
	color: #0066cc !important;
}

table a:hover {
	color: #ff6600 !important;
}

tr {
	border-bottom: 1px solid #E5E5E5;
	height: 30px;
}

input[type="text"] {
	border: 1px solid #ccc;
	padding: 2px;
	color: #444;
	width: 100%;
	height: 35px;
	font-size: 10px;
	font-size: 1rem;
	border-radius: 5px;
}
</style>

<script type="text/javascript">
	/* 	$(document).on("pageinit",function(event){
	 alert("pageinit 事件触发!");
	 }); */

	$(window).load(
			function() {
				var amount = $('#amountList').val();
				var rfid = $('#rfidList').val();
				if (amount != null && amount != '' && rfid != null
						&& rfid != '') {
					var amountList = new Array(); //定义一数组
					var rfidList = new Array(); //定义一数组 
					amountList = amount.split(","); //字符分割 
					var rfidList = new Array(); //定义一数组 
					rfidList = rfid.split(","); //字符分割
					$.each(amountList, function(index, value) {
						$.each(rfidList, function(index1, value1) {
							if (index > 0 && index1 == index) {
								var num = $("#rowCount").val();//取值
								num = parseFloat(num);
								num++;
								var newRow = $("#trDetailRowTemplate0").clone(
										true).attr("id", "trDetailRow" + num);
								newRow.removeAttr("style");
								// 添加一行
								newRow.appendTo("#tblDetailData");
								// tr单元格赋值
								//金额
								newRow.children('td:eq(2)').text(
										toThousands(value));
								if(value1=='null') {
									value1='';
								}
								newRow.children('td:eq(1)').text(value1);
								newRow.children('td:eq(0)').text(index);
								$('#rowCount').val(num);//重新赋值
							}
						})
					});

				}
			});
	//添加行 
	function addDetailRow() {
		var detailAmount = $("#detailAmount").val();//取值
		var rfid = $("#rfid").val();//取值
		var reg = /^[1-9]\d*(\.(\d){1,2})?$/; //验证规则

		if (!reg.test(detailAmount)) {
			$("#detailAmount1").popup('open');
			setInterval(function() {
				$("#detailAmount1").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
		/* 	 if(detailAmount=='' || detailAmount == '0') {
				 $("#detailAmount1").popup('open');
				 setInterval(function(){
					   $("#detailAmount1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }  */
		if (rfid.match(/^[\u4e00-\u9fa5]+$/)) {
			$("#rfid2").popup('open');
			setInterval(function() {
				$("#rfid2").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
			 
		//包号重复性检查
		var blnFlag = false;
		var allObj = $('[name=trDetailRowTemplate]');
		if (rfid.length > 0){
			for (var i = 0; i < allObj.length; i++) {
				if (allObj.eq(i).attr('id') != "trDetailRowTemplate0") {
					rfidList = allObj.eq(i).children('td').eq(1).text().replace(
									new RegExp(",", "gm"), "");
	          	  if (rfid == rfidList){
	   				blnFlag = true;
	   				break;
	          	  }
				}
			}
			 if(blnFlag){
					$("#rfid3").popup('open');
					setInterval(function() {
						$("#rfid3").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
		}

		var num = $("#rowCount").val();//取值
		num = parseFloat(num);
		num++;
		var newRow = $("#trDetailRowTemplate0").clone(true).attr("id",
				"trDetailRow" + num);
		newRow.removeAttr("style");
		// 添加一行
		newRow.appendTo("#tblDetailData");
		// tr单元格赋值
		//金额
		var childNum = newRow[0].children.length;
		var strAmount = toThousands(detailAmount);
		newRow.children('td').eq(1).text(rfid);
		newRow.children('td').eq(2).text(strAmount);
		$('#rowCount').val(num);//重新赋值
		// 行追加后每笔金额清空
		$("#detailAmount").val('');
		// 行追加后包号清空
		$("#rfid").val('');
		//总金额重新赋值
		renewAmount();

		//$('#detailAmount').focus();

		var tbody1 = document.getElementById('tbody');
		tbody1.scrollTop = tbody1.scrollHeight;

	}

	// 整数千分位overflow-y:scroll;
	function toThousands(num) {
		var newStr = "";
		var count = 0;
		var num = (num || 0).toString(), result = '';
		if (num.indexOf(".") == -1) {
			while (num.length > 3) {
				result = ',' + num.slice(-3) + result;
				num = num.slice(0, num.length - 3);
			}
		} else {
			for (var i = num.indexOf(".") - 1; i >= 0; i--) {
				if (count % 3 == 0 && count != 0) {
					newStr = num.charAt(i) + "," + newStr;
				} else {
					newStr = num.charAt(i) + newStr; //逐个字符相接起来
				}
				count++;
			}
			num = newStr + (num + "00").substr((num + "00").indexOf("."), 3);

		}
		if (num) {
			result = num + result;
		}
		return result;
	}
	// 总金额重新赋值
	function renewAmount() {
		var i = 0
		var amount = 0;
		var rowCnt = 0;
		var amountList = '';
		var rfidList = '';
		var element = document.getElementsByName("trDetailRowTemplate");

		var rfid = $("#rfid").val();//取值
		var allObj = $('[name=trDetailRowTemplate]');
		//alert(allObj.length);
		for (var i = 0; i < allObj.length; i++) {
			// alert(allObj.eq(i).children('td').eq(1).text());
			if (allObj.eq(i).attr('id') != "trDetailRowTemplate0") {
				rowCnt++;
				allObj.eq(i).children('td').eq(0).text(rowCnt);
				amount = parseFloat(amount)
						+ parseFloat(allObj.eq(i).children('td').eq(2).text()
								.replace(new RegExp(",", "gm"), ""));
				amountList = amountList
						+ ","
						+ allObj.eq(i).children('td').eq(2).text().replace(
								new RegExp(",", "gm"), "");
				rfidList = rfidList
						+ ","
						+ allObj.eq(i).children('td').eq(1).text().replace(
								new RegExp(",", "gm"), "");
			}
		}
		$('#amount').val(toThousands(amount));
		$('#amountList').val(amountList);
		$('#rfidList').val(rfidList);
	}

	//删除行 
	function delDetailRow(r) {

		var num = $("#rowCount").val();//取值
		num = parseFloat(num);
		var tr = r.parentNode.parentNode;
		var tbody = tr.parentNode;
		tbody.removeChild(tr);
		num--;
		$('#rowCount').val(num);//重新赋值
		// 总金额重新赋值
		renewAmount();

	}

	//提交页面
	function formSubmit() {
		var amount = $("#amount").val();//取值
		if (amount == null || amount == '' || amount == '0') {
			$("#amount1").popup('open');
			setInterval(function() {
				$("#amount1").popup("close");
			}, 2000);//两秒后关闭
			return;
		}

		$.mobile.showPageLoadingMsg();
		var formData = $("#inputForm").serialize();
		$.ajax({
			type : "POST",
			url : "${ctx}/wechatAccount/branchSave",
			cache : false,
			data : formData,
			success : onSuccess,
			error : onError
		});
		return false;
	}

	//提交页面
	function formConfirm() {
		var amount = $("#amount").val();//取值
		if (amount == null || amount == '' || amount == '0') {
			$("#amount1").popup('open');
			setInterval(function() {
				$("#amount1").popup("close");
			}, 2000);//两秒后关闭
			return;

		}
		$.mobile.showPageLoadingMsg();
		var formData = $("#inputForm").serialize();
		$.ajax({
			type : "POST",
			url : "${ctx}/wechatAccount/branchConfirm?userId=${userId}",
			cache : false,
			data : formData,
			success : onSuccess,
			error : onError
		});
		return false;
	}
	//清空页面
	function formClear() {
		$('#amount').val("");
		$('#orderDate').val("");
		$('#detailAmount').val("");
		$('#amountList').val("");
		$('#rowCount').val("0");
		var node = document.getElementById("tbody");
		while (node.hasChildNodes()) {
			node.removeChild(node.firstChild);
		}
	}

	//返回处理
	function formBack() {
		WeixinJSBridge.call('closeWindow');
	}

	//判断金额栏回车触发行追加
	function rowAdd(e) {
		if (e.keyCode == 13) {
			addDetailRow();
		}
	}

	window.onload = function() {
		var strResult = "${result}";
		if (strResult == undefined || strResult == "" || strResult == null) {
			return;
		}
		if (strResult == "1") {
			alert('保存成功!');
		}
	}

	var initialScreenSize = window.innerHeight;
	window.addEventListener("resize", function() {
		if (window.innerHeight < initialScreenSize) {
		} else {
			var isFocus = $("#detailAmount").is(":focus");
			if (true == isFocus) {
				$("#detailAmount").blur();
			}
		}
	});

	function onSuccess(data, status) {
		$.mobile.hidePageLoadingMsg();
		$("#success").popup('open');
		setInterval(function() {
			$("#success").popup("close");
		}, 2000);//两秒后关闭
		return;
	}

	function onError(data, status) {
		$.mobile.hidePageLoadingMsg();
		$("#failure").popup('open');
		setInterval(function() {
			$("#failure").popup("close");
		}, 2000);//两秒后关闭
		return;
	}
	function UpdateStatus(r) {
		var popupDialogId = 'popupDialog';
		$(
				'<div data-role="popup" id="' + popupDialogId + '" data-confirmed="no" data-transition="pop" data-overlay-theme="b" data-theme="b" data-dismissible="false" style="min-width:216px;max-width:500px;"> \
                            \
                            <div role="main" class="ui-content" style="background: #f9f9f9;">\
                                <h3 class="ui-title" style="color:#2F72AD; text-align:center;margin-bottom:15px">确认要删除吗？</h3>\
                                <a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionConfirm" data-rel="back" style="background: #2F72AD;  color: #fff;width: 33%;border-radius: 5px;height: 30px;line-height: 30px;padding: 0;font-size: .9em;margin: 0 0 0 12%;font-weight: 100;">确定</a>\
                                <a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionCancel" data-rel="back" data-transition="flow" style="background: #DBDBDB;width: 33%;border-radius: 5px;height: 30px;line-height: 30px;padding: 0;font-size: .9em;margin: 0 0 0 5%;font-weight: 100;color: #333;text-shadow: none;">取消</a>\
                            </div>\
                       </div>')
				.appendTo($.mobile.pageContainer);
		var popupDialogObj = $('#' + popupDialogId);
		popupDialogObj.trigger('create');
		popupDialogObj
				.popup({
					afterclose : function(event, ui) {
						popupDialogObj.find(".optionConfirm").first().off(
								'click');
						var isConfirmed = popupDialogObj.attr('data-confirmed') === 'yes' ? true
								: false;
						$(event.target).remove();
						if (isConfirmed) {
							delDetailRow(r);
						}
					}
				});
		popupDialogObj.popup('open');
		popupDialogObj.find(".optionConfirm").first().on('click', function() {
			popupDialogObj.attr('data-confirmed', 'yes');
		});
	}
</script>
</head>
<body>

	<div data-role="page" id="pageone">

		<!-- 弹窗设置 -->
		<div data-role="popup" id="success" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;保存成功！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>

		<div data-role="popup" id="failure" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;保存失败！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="detailAmount1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请输入合法的金额！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="detailAmount2" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请输入整数！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="rfid1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;包号不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="rfid2" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;包号格式有误！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="amount1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;总金额不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="rfid3" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;包号不能重复！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<!-- end -->

		<div data-role="header" data-position="fixed" data-theme="b">
			<a href="javascript:void(0);" onclick="formBack()" data-icon="back"
				data-ajax="false" data-rel="back">返回</a>
			<h1>上门收款预约</h1>
		</div>
		<div data-role="content">
			<form method="post" id="inputForm">
				<div>
					<div class="order-con">
						<ul>
							<li>门店</li>
							<li><input type="text" data-role="none" placeholder="门店名称"
								name="doorName" id="doorName" value="${doorName}"
								readonly="readonly"
								style="ime-mode: disabled; height: 35px; width: 180px; font-size: 16px; text-indent: 6px; background-color: #eee">
								<font color="red">&nbsp;&nbsp;*</font> <input type="hidden"
								id="doorId" name="doorId" value="${doorId}" /> <input
								type="hidden" id="userId" name="userId" value="${userId}" /></li>
						</ul>
						<ul>
							<li>总金额</li>
							<li><input value="${doorOrderInfo.amount}" type="text"
								data-role="none" id="amount" name="amount" placeholder="总金额"
								readonly="readonly"
								style="ime-mode: disabled; height: 35px; width: 180px; font-size: 16px; text-indent: 6px; background-color: #E6E6E6">
								<font color="red">&nbsp;&nbsp;*</font></li>
						</ul>
				
						<c:if test="${guestType eq '1'}">
							<ul>
								<li>包号</li>
								<li colspan="2">
									<input type="text" data-role="none"
									style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px"  placeholder="包号" name="rfid" id="rfid"
									onkeypress="return rowAdd(event);" maxlength="5" />
									
									<input type="hidden" id="orderDate" name="orderDate"
									value="${orderDate}"  />
								</li>
							</ul>
						</c:if>

						<c:if test="${guestType eq '1'}">
							<ul>
								<ol>每笔金额</ol>
								<ol><input type="number" data-role="none"
									 placeholder="每笔金额" name="detailAmount"
									id="detailAmount" style="ime-mode:disabled;height: 35px;width: 120px;font-size: 16px;border:solid #ccc 1px;border-radius:6px;text-indent: 6px;"  
									oninput="if(value.length>9)value=value.slice(0,9)" />
									<font color="red">&nbsp;&nbsp;*</font>
								</ol>
								<ol>
								<a  id="btnAddDetail" href="javascript:void(0);"onclick="addDetailRow()" >+</a>
							<!-- 	<a id="btnAddDetail" href="javascript:void(0);"
									onclick="addDetailRow()" data-role="button" data-theme="c"
									data-mini="true">行添加</a> -->
									</ol>
							</ul>
						</c:if>
						<ul>
						<li><input type="hidden" id="rowCount"
								name="rowCount" value="0" /> <input type="hidden"
								id="amountList" name="amountList"
								value="${doorOrderInfo.amountList}" /> <input type="hidden"
								id="rfidList" name="rfidList" value="${doorOrderInfo.rfidList}" /></li>
						<table border=1  cellpadding=1 id="tblDetailData" class="table"
									style="border-left-width: 0px; border-right-width: 0px; border-color: #FFFFFF; width: 100%; border-collapse: collapse;">
									<colgroup>
									<col width=15%>
									<col width=35%>
									<col width=35%>
									<col width=15%>
									</colgroup>
									<thead>
										<tr>
											<th>序号</th>
											<th>包号</th>
											<th >金额</th>
											<c:if test="${guestType eq '1'}">
											<th>操作</th>
											</c:if>
										</tr>
									</thead>
									<tbody id="tbody" class="scrollTbody">
									</tbody>
								</table>
						</ul>
					</div>
					<%-- <table border=0 style="width: 100%; height: 100%">
					
						<tr>
							<td style="width: 70px" class="text-right">
								<label>门店</label>
							</td>
							<td colspan="2"><input type="text" data-role="none"
								placeholder="门店名称" name="doorName" id="doorName"
								value="${doorName}" readonly="readonly" 
								style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px; background-color:#E6E6E6">
								<font color="red">&nbsp;&nbsp;*</font> 
								<input type="hidden" id="doorId" name="doorId" value="${doorId}" /> 
								<input type="hidden" id="userId" name="userId" value="${userId}" />
							</td>
						</tr>
						<tr class="text-right">
							<td><label>总金额</label></td>
							<td colspan="2"><input value="${doorOrderInfo.amount}"
								type="text" data-role="none" id="amount" name="amount"
								placeholder="总金额" readonly="readonly" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;background-color:#E6E6E6">
							<font color="red">&nbsp;&nbsp;*</font> 
							</td>
						</tr>
						<c:if test="${guestType eq '1'}">
							<tr class="text-right">
								<td><label>包号</label></td>
								<td colspan="2">
									<input type="text" data-role="none"
									style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px"  placeholder="包号" name="rfid" id="rfid"
									onkeypress="return rowAdd(event);" maxlength="5" />
									
									<input type="hidden" id="orderDate" name="orderDate"
									value="${orderDate}"  />
								</td>
							</tr>
						</c:if>

						<c:if test="${guestType eq '1'}">
							<tr class="text-right">
								<td><label>每笔金额</label></td>
								<td><input type="number" data-role="none"
									 placeholder="每笔金额" name="detailAmount"
									id="detailAmount" style="ime-mode:disabled;height: 35px;width: 100px;font-size: 16px;border:solid #ccc 1px"  
									oninput="if(value.length>9)value=value.slice(0,9)" onkeyup="value=value.replace(/[^\-?\d.]/g,'')"/>
									<font color="red">&nbsp;&nbsp;*</font>
								</td>
								<td><a id="btnAddDetail" href="javascript:void(0);"
									onclick="addDetailRow()" data-role="button" data-theme="c"
									data-mini="true">行添加</a></td>
							</tr>
						</c:if>

						<tr>
							<td colspan="3"><input type="hidden" id="rowCount"
								name="rowCount" value="0" /> <input type="hidden"
								id="amountList" name="amountList"
								value="${doorOrderInfo.amountList}" /> <input type="hidden"
								id="rfidList" name="rfidList" value="${doorOrderInfo.rfidList}" />
								<table border=1  cellpadding=1 id="tblDetailData" class="table"
									style="border-left-width: 0px; border-right-width: 0px; border-color: #FFFFFF; width: 100%; border-collapse: collapse;">
									<thead>
										<tr>
											<th
												style="width: 50px; text-align: center; vertical-align: middle;">序号</th>
											<th
												style="width: 80px; text-align: center; vertical-align: middle;">包号</th>
											<th style="text-align: center; vertical-align: middle;">金额</th>
											<c:if test="${guestType eq '1'}">
											<th 
												style="width: 80px; text-align: center; vertical-align: middle;">操作</th>
											</c:if>
										</tr>
									</thead>
									<tbody id="tbody" class="scrollTbody">
									</tbody>
								</table>
							</td>
						</tr>

					</table> --%>
				</div>

				<div style="display: none">
					<table id="tblDetailDataTemplate">
						<tbody id="tbody">
							<tr id="trDetailRowTemplate0" name="trDetailRowTemplate">
								<td
									style="border-width: 0px; text-align: center; vertical-align: middle;"></td>
								<td
									style="border-width: 0px; text-align: center; vertical-align: middle;"></td>
								<td
									style="border-width: 0px; text-align: center; vertical-align: middle;"></td>
								<c:if test="${guestType eq '1'}">
									<td>
										<a href="javascript:void(0);" onclick="UpdateStatus(this)"
										class="del-ico" >×</a>
									</td>
								</c:if>

							</tr>
						</tbody>
					</table>
				</div>


			</form>
		</div>

		<div data-role="footer" data-position="fixed" data-id="footernav"
			data-position="fixed" data-tap-toggle="false">
			<div data-role="navbar">
				<ul>
					<li><a href="javascript:void(0);" onclick="formClear()">清空</a></li>
					<c:if test="${guestType eq '1'}">
						<li><a id="btnSave" href="javascript:void(0);"
							onclick="formSubmit()">保存</a></li>
					</c:if>
					<c:if test="${guestType eq '2'}">
						<li><a id="btnSave" href="javascript:void(0);"
							onclick="formConfirm()">确认</a></li>
					</c:if>
				</ul>
			</div>
		</div>
	</div>


	<!--- app class="ui-btn-active"--->
	<script type="text/javascript">
		var ctx = '${ctx}';
	</script>





</body>
</html>