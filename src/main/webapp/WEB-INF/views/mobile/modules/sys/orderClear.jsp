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
.table thead{background-color:#bbdcfc !important;color:#333;border:1px solid #fff !important;}
		table a{color:#0066cc !important;}
		table a:hover{color:#ff6600 !important;}
		tr {
		    border-bottom: 1px solid #d6d6d6;height:40px;
		}
.ui-content{/* background-color:#f0f0f0; */}
		input[type="text"]{
		    border: 1px solid #ccc;
		    padding: 2px;
		    color: #444;
		    width: 100%;
		    height: 35px;
		    font-size:10px;
		    font-size:1rem;border-radius:10px;
		}
		input[type="tel"]{
		    border-radius:10px;
		}
		select{border-radius:10px;}
		textarea{
		    border: 1px solid #ccc;
		    padding: 2px;
		    color: #444;
		    width: 100%;
		    font-size:10px;
		    font-size:1rem;border-radius:10px;
		}
</style>

<script type="text/javascript">
	
	$(window).load(
		function() {
			//是否显示预约单号
			if ($("#inNo").val()!='') {
				$("#inNoUl").attr("style","");
			}
			//金额格式化
			changeAmountToBig($("#inAmount").val(),"inAmountBigRmb");
			if ($("#inAmount").val()!=null && $("#inAmount").val()!='') {
				$("#inAmount").val(formatCurrencyFloat($("#inAmount").val()))
			}		
		}
	);

	//总金额格式化
	function inAmountOnkeyup(obj){		
		setSumDiff();
		changeAmountToBig(obj.value,"inAmountBigRmb");
	}
	//总金额失去焦点
	function inAmountOnblur(obj){
		$("#inAmount").val(formatCurrencyFloat(obj.value));
	}
	//总金额获得焦点
	function inAmountOnfocus(obj){
		$("#inAmount").val(changeAmountToNum(obj.value));
	}
	
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
			url : '${ctx}/wechatAccount/changRMBAmountToBig?amount=' + amount,
			type : 'POST',
			cache : false,
			dataType : 'JSON',
			success : function(data, status) {
				$("#" + toId).val(data.bigAmount);
			},
			error : function(data, status, e) {
				
			}
		});
	}

	//提交页面
	function formSubmit() {
		//清分机构
		var clearOffice=$("#rOffice\\.id").val();
		if (clearOffice == null || clearOffice == '') {
			$("#clearOfficeCheck").popup('open');
			setInterval(function() {
				$("#clearOfficeCheck").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
		//总金额
		var inAmount=changeAmountToNum($("#inAmount").val());
		if (inAmount==null|inAmount==''|inAmount==0) {
			$("#inAmountCheckNull").popup('open');
			setInterval(function() {
				$("#inAmountCheckNull").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
		var reg = /^[1-9]\d*(\.(\d){1,2})?$/; //验证规则
		if (!reg.test(inAmount)) {
			$("#inAmountCheck").popup('open');
			setInterval(function() {
				$("#inAmountCheck").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
		//差额
		var diffValue=getDiffValue(); 
		if (diffValue != 0) {
			$("#diffValueCheck").popup('open');
			setInterval(function() {
				$("#diffValueCheck").popup("close");
			}, 2000);//两秒后关闭
			return;
		}
		//提交
		$("#inAmount").val(inAmount);
		$.mobile.showPageLoadingMsg();
		var formData = $("#inputForm").serialize();
		$.ajax({
			type : "POST",
			url : "${ctx}/wechatAccount/orderClearSave",
			cache : false,
			data : formData,
			success : onSuccess,
			error : onError
		});
		$("#inAmount").val(formatCurrencyFloat(inAmount));
		return false;
	}

	//清空页面
	function formClear() {
		$('#inAmount').val("");
		$('#inAmountBigRmb').val("");
		$('#remarks').val("");
		$('#rOffice\\.id').val("");
		$('#rOffice\\.id').trigger("select");
		$("input[UseType^='CountD']").val("");
		$("input[UseType^='CountY']").val("");
		$("input[UseType^='Amount']").val("")
		$("label[id^='totalAmount_']").html("");
		setSumDiff();
	}

	//返回处理
	function formBack() {
		WeixinJSBridge.call('closeWindow');
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
	
	//保存成功
	function onSuccess(data, status) {
		$.mobile.hidePageLoadingMsg();
		$("#success").popup('open');
		setInterval(function() {
			$("#success").popup("close");
		}, 2000);//两秒后关闭
		$("#inNo").val(data);
		$("#inNoUl").attr("style","");
		return;
	}

	//保存失败
	function onError(data, status) {
		$.mobile.hidePageLoadingMsg();
		$("#failure").popup('open');
		setInterval(function() {
			$("#failure").popup("close");
		}, 2000);//两秒后关闭
		return;
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
		
		<div data-role="popup" id="clearOfficeCheck" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请选择清分机构！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		
		<div data-role="popup" id="inAmountCheckNull" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请输入总金额！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		
		<div data-role="popup" id="inAmountCheck" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请输入合法数字！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		
		<div data-role="popup" id="diffValueCheck" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;差额不为0，不能保存！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<!-- end -->

		<div data-role="header" data-position="fixed" data-theme="b">
			<a href="javascript:void(0);" onclick="formBack()" data-icon="back"
				data-ajax="false" data-rel="back">返回</a>
			<h1>预约清分</h1>
		</div>
		<div data-role="content">
			<form method="post" id="inputForm">
				<div>
					<div class="order-con">
						<ul id="inNoUl" style="display:none">
							<li>预约单号</li>
							<li>
								<input value="${orderClearInfo.inNo}" type="text" data-role="none" id="inNo" name="inNo" readOnly="readOnly"
								style="ime-mode: disabled; height: 35px; width: 210px; font-size: 16px; text-indent: 6px; background-color: #E6E6E6">
							</li>
						</ul>
						<ul>
							<li>清分机构</li>
							<li>
								<c:choose>
									<c:when test="${orderClearInfo.status eq '2'}">
										<input value="${orderClearInfo.rOffice.name}" type="text" maxlength="14"
										data-role="none" id="rOffice.id" name="rOffice.id" readOnly="readOnly"
										style="ime-mode: disabled; height: 35px; width: 180px; font-size: 16px; text-indent: 6px; background-color: #E6E6E6">
									</c:when>
									<c:otherwise>
										<select id="rOffice.id" name="rOffice.id"  data-role="none" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;border:solid #ccc 1px">
		     						 	<option value="" selected="selected">请选择</option>
		     						 	<c:if test="${not empty orderClearInfo.rOffice.id}">
		     						 		<option value="${orderClearInfo.rOffice.id}" selected="selected">${orderClearInfo.rOffice.name}</option>
		     						 	</c:if>
		     						 	
		     						 	<c:forEach var="clearOffice" items="${sto:getStoCustList('6',true)}"> 
		       						 		<c:if test="${!(orderClearInfo.rOffice.id eq clearOffice.id)}">
		     						 			<option value="${clearOffice.id}">${clearOffice.name}</option>
		     						 		</c:if>	
		     						 	</c:forEach>
		           						</select>
									</c:otherwise>
								</c:choose>							
	           					<font color="red">&nbsp;*</font> 
	           					<input type="hidden" id="userId" name="createBy.id" value="${userId}" />
           					</li>
						</ul>
						<ul>
							<li>总金额</li>
							<li>
								<c:choose>	
									<c:when test="${orderClearInfo.status eq '2'}">
										<input value="${orderClearInfo.inAmount}" type="text" maxlength="14"
										data-role="none" id="inAmount" name="inAmount" readOnly="readOnly" 
										style="ime-mode: disabled; height: 35px; width: 180px; font-size: 16px; text-indent: 6px; background-color: #E6E6E6">
									</c:when>
									<c:otherwise>
										<input value="${orderClearInfo.inAmount}" type="text" maxlength="14"
										data-role="none" id="inAmount" name="inAmount" placeholder="总金额(元)" onkeyup="inAmountOnkeyup(this)" onblur="inAmountOnblur(this)" onfocus="inAmountOnfocus(this)"
										style="ime-mode: disabled; height: 35px; width: 180px; font-size: 16px; text-indent: 6px;">
									</c:otherwise>
								</c:choose>	
								<font color="red">&nbsp;*</font>
							</li>
						</ul>
						<ul>
							<li>总金额</li>
							<li>
								<textarea data-role="none" id="inAmountBigRmb" name="inAmountBigRmb" placeholder="总金额(大写)" readOnly="readOnly"
								rows="3" cols="20" style="ime-mode: disabled; font-size: 16px;  background-color: #E6E6E6"></textarea>
							</li>
						</ul>
						<ul>
							<li>备注</li>
							<li>
							<c:choose>	
								<c:when test="${orderClearInfo.status eq '2'}">
									<textarea data-role="none" id="remarks" name="remarks" readOnly="readOnly" 
									style="ime-mode: disabled; font-size: 16px;  background-color: #E6E6E6"
									rows="3" cols="20" maxlength="60" class="input-large ">${orderClearInfo.remarks}</textarea>
								</c:when>
								<c:otherwise>
									<textarea data-role="none" id="remarks" name="remarks" placeholder="备注"
									style="ime-mode: disabled;  font-size: 16px;"
									rows="3" cols="20" maxlength="60">${orderClearInfo.remarks}</textarea>
								</c:otherwise>						
							</c:choose>
							</li>
						</ul>			
					</div>
				</div>
				<!-- 明细 -->
				<c:set var="allowInput" value="${orderClearInfo.status eq '2'?false:true}"/>
				<sys:orderClearMobile dataSource="${orderClearInfo.denominationList}"  bindListName="denominationList" totalAmountId="inAmount" allowInput="${allowInput}"/>
			</form>
		</div>

		<c:if test="${!(orderClearInfo.status eq '2')}">
		<div data-role="footer" data-id="footernav"
			data-position="fixed" data-tap-toggle="false">
			<div data-role="navbar">
				<ul>
					<li><a href="javascript:void(0);" onclick="formClear()">清空</a></li>					
					<li><a id="btnSave" href="javascript:void(0);" onclick="formSubmit()">确认</a></li>
				</ul>
				<ul>
					<li style="font-size:11px;text-align:center;">聚龙股份版权所有  版本：数字化金融服务平台 1.0.0-SNAPSHOT</li>
				</ul>
			</div>
		</div>
		</c:if>			
	</div>

	<!--- app class="ui-btn-active"--->
	<script type="text/javascript">
		var ctx = '${ctx}';
	</script>
</body>
</html>