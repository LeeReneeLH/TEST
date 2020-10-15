<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 存款管理 -->
	<title><spring:message code="door.deposit.title" /></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
    	.fixedThead{
        	display: block;
            width: 100%;
        }
        .scrollTbody{
            display: block;
            height: 262px;
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
        td {
		  word-wrap: break-word;
		  word-break: break-all; 
		}
		@media only screen and (max-width: 1200px){
		    #divRight{overflow: auto;}
		}
    </style>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		var selectTickertape='';	//选中凭条号
		//上一次凭条号(模糊查询)  本次凭条号(模糊查询)  上一次业务备注(模糊查询)  本次业务备注(模糊查询)
		var lastValue,tickertapeId,lastRemarks,remarksId='';
		$(document).ready(function() {
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
		});
		
		$(window).load(function() {
			var amount = $('#amountList').val();
			var busType= $('#busTypeList').val();
			var tickertape = $('#tickertapeList').val();
			var detailId = $('#detailIdList').val();
			var createBy = $('#createByList').val();
			var createDate = $('#createDateList').val();
			// 存款备注 gzd 2019-12-16
			var remarks = $('#remarksList').val();
			if (amount != null && amount != '') {
				var amountList = new Array(); //定义一数组
				var tickertapeList = new Array(); //定义一数组
				var detailIdList = new Array(); //定义一数组
				var busTypeList = new Array();
				var createByList = new Array();
				var createDateList = new Array();
				// 存款备注 gzd 2019-12-16
				var remarksList = new Array();
				amountList = amount.split(","); //字符分割 
				busTypeList = busType.split(","); //字符分割 
				tickertapeList = tickertape.split(","); //字符分割
				detailIdList = detailId.split(","); //字符分割
				createByList = createBy.split(",");
				createDateList = createDate.split(",");
				// 存款备注 gzd 2019-12-16
				remarksList = remarks.split(",");
				$.each(amountList, function (index, value) {
					if (index > 0) {
						var newRow = $("#trTikertapeTemplate0").clone(true).attr("id", "tickertape_" + tickertapeList[index]);
						newRow.removeAttr("style");
						// 添加一行
						newRow.appendTo("#tblTikertape");
						// 序号
						newRow.children('td:eq(0)').text(index);
						// 凭条
						newRow.children('td:eq(1)').text(tickertapeList[index]);
						// 业务类型
						newRow.children('td:eq(2)').text(busTypeList[index]);
						// 金额
						newRow.children('td:eq(3)').text(formatCurrencyFloat(value));
						// 创建人（存款日期）
						newRow.children('td:eq(4)').text(createByList[index]);
						// 创建日期（存款时间）
						newRow.children('td:eq(5)').text(createDateList[index]);
						newRow.children('td:eq(6)').text(detailIdList[index]);
						// 存款备注 gzd 2019-12-16
						newRow.children('td:eq(7)').text(remarksList[index]);
						if (index == 1) {
							selectRow(newRow);
						}
					}
				});
			}
			tickertapeId = $("#tickertapeId");
			tickertapeId.bind("change cut input propertychange", searchNode);
			tickertapeId.bind('keydown', function (e){if(e.which == 13){searchNode();}});
			
			remarksId = $("#remarksId");
			remarksId.bind("change cut input propertychange", searchNode);
			remarksId.bind('keydown', function (e){if(e.which == 13){searchNode();}});
        });

     function searchNode() {
			// 取得输入的关键字的值
	        var tickertapeId = $("#tickertapeId").val().trim();
	        var remarksId = $("#remarksId").val().trim();
	        // 如果和上次一次，就退出不查了。
	        if (lastValue === tickertapeId && lastRemarks === remarksId) {
	           	 return;
			} 
	        if (tickertapeId == "") {
	        	$("#tickertapeId").attr('placeholder','请输入凭条号');
            }
			if (remarksId == "") {
		        	$("#remarksId").attr('placeholder','请输入业务备注');
	        }
	        // 保存最后一次
	        lastValue = tickertapeId; 
	        lastRemarks = remarksId;
			var amount = $('#amountList').val();
			var busType= $('#busTypeList').val();
			var tickertape = $('#tickertapeList').val();
			var detailId = $('#detailIdList').val();
			var createBy = $('#createByList').val();
			var createDate = $('#createDateList').val();
			var remarks = $('#remarksList').val();
			var num=0;
			if (amount != null && amount != '') {
				var amountList = new Array(); //定义一数组
				var tickertapeList = new Array(); //定义一数组
				var detailIdList = new Array(); //定义一数组
				var busTypeList = new Array();
				var createByList = new Array();
				var createDateList = new Array();
				var remarksList = new Array();
				amountList = amount.split(","); //字符分割 
				busTypeList = busType.split(","); //字符分割 
				tickertapeList = tickertape.split(","); //字符分割
				detailIdList = detailId.split(","); //字符分割
				createByList = createBy.split(",");
				createDateList = createDate.split(",");
				remarksList = remarks.split(",");
				 $("#tbdTikertape").empty();//清空每笔金额
				 $("#tbdAmount").empty();//清空明细
				$.each(amountList,function(index,value){ 
					if (index > 0) {
						if(tickertapeList[index].indexOf(tickertapeId)!=-1 && remarksList[index].indexOf(remarksId)!=-1){
							var newRow = $("#trTikertapeTemplate0").clone(true).attr("id", "tickertape_" + tickertapeList[index]);
							newRow.removeAttr("style");
							// 添加一行
							newRow.appendTo("#tblTikertape");
							// 序号
							num++;
							newRow.children('td:eq(0)').text(num);
							// 凭条
							newRow.children('td:eq(1)').text(tickertapeList[index]);
							// 业务类型
							newRow.children('td:eq(2)').text(busTypeList[index]);
							// 金额
							newRow.children('td:eq(3)').text(formatCurrencyFloat(value));
							// 创建人（存款日期）
							newRow.children('td:eq(4)').text(createByList[index]);
							// 创建日期（存款时间）
							newRow.children('td:eq(5)').text(createDateList[index]);
							newRow.children('td:eq(6)').text(detailIdList[index]);
							//业务备注
							newRow.children('td:eq(7)').text(remarksList[index]);
							if(num==1){
								selectRow(newRow);
							}
						}
					}
				});
			}
		}
		
     //gzd 2020-04-14 方法优化
     /* function searchNode() {
			// 取得输入的关键字的值
	        var tickertapeId = $("#tickertapeId").val().trim();
	        var remarksId = $("#remarksId").val().trim();
	        // 如果和上次一次，就退出不查了。
	        if (lastValue === tickertapeId && lastRemarks === remarksId) {
	           	 return;
			} 
	        if (tickertapeId == "") {
	        	$("#tickertapeId").attr('placeholder','请输入凭条号');
         }
			if (remarksId == "") {
		        	$("#remarksId").attr('placeholder','请输入业务备注');
	        }
			var i=1;
			$("#tblTikertape").find("tr[name='abc']").each(function () {
				if($(this).children('td:eq(1)').text().trim().indexOf(tickertapeId)!=-1 && $(this).children('td:eq(7)').text().trim().indexOf(remarksId)!=-1){
					if( i == "1"){
						$(this).trigger("click");
					}
					$(this).attr("style","display:''");
					$(this).children('td:eq(0)').text(i++);
				}else{
					$(this).attr("style","display:none");
				}
			});
		} */
     	
		//凭条-选中行处理
		function selectRow(vRow){
			//当前选中凭条
			selectTickertape=$(vRow).attr('id').replace("tickertape_","");
		 	//选中样式
			$(vRow).addClass("rowSel").siblings().removeClass("rowSel");
			$("#tbdAmount").html("");
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				url : "${ctx}/weChat/v03/doorOrderInfo/getDetailList",
				data : {
					detailId:$(vRow).children('td:eq(6)').text(),
					id:$("#id").val()
				},
				success : function(data, status) {
					if(data.result == 'OK'){
						for(var i=0;i<data.detailList.length;i++){
							addAmountRow(data.detailList[i]);
						}
						// 明细序号重新赋值
						resetAmountNum();
					}else{
						alertx("获取金额明细失败！");
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alertx("获取金额明细失败！");
				}
			});
		}
		
		//存款明细-添加行 
		function addAmountRow(data){
			var newRow = $("#trAmountTemplate0").clone(true).attr("name",selectTickertape);
			newRow.removeAttr("style");
			// 添加一行
			newRow.appendTo("#tbdAmount");
			// 存款类型
			newRow.children('td:eq(1)').text(data.saveMethod);
			// 面值
			var denomination = data.parValueId;
			var parValue = "";
			if(denomination==""||denomination==null){
				parValue = "";
			}else if(denomination=="15"){
				parValue = "100元_纸币";
			}else if(denomination=="16"){
				parValue = "50元_纸币";
			}else if(denomination=="17"){
				parValue = "20元_纸币";
			}else if(denomination=="18"){
				parValue = "10元_纸币";
			}else if(denomination=="19"){
				parValue = "5元_纸币";
			}else if(denomination=="20"){
				parValue = "2元_纸币";
			}else if(denomination=="21"){
				parValue = "1元_纸币";
			}else if(denomination=="22"){
				parValue = "5角_纸币";
			}else if(denomination=="23"){
				parValue = "2角_纸币";
			}else if(denomination=="24"){
				parValue = "1角_纸币";
			}
			/* hzy  添加分数     START*/
			else if(denomination=="25"){
				parValue = "5分_纸币";
			}else if(denomination=="26"){
				parValue = "2分_纸币";
			}else if(denomination=="27"){
				parValue = "1分_纸币";
			}
			else if(denomination=="28"){
				parValue = "1元_硬币";
			}else if(denomination=="29"){
				parValue = "5角_硬币";
			}else if(denomination=="30"){
				parValue = "1角_硬币";
			}
			else if(denomination=="31"){
				parValue = "5分_硬币";
			}else if(denomination=="32"){
				parValue = "2分_硬币";
			}else if(denomination=="33"){
				parValue = "1分_硬币";
			}
			newRow.children('td:eq(2)').text(parValue);
			// 张数
			newRow.children('td:eq(3)').text(data.count);
			// 金额
			newRow.children('td:eq(4)').text(formatCurrencyFloat(data.amount));
			var tbody1 = document.getElementById('tbdAmount');
			tbody1.scrollTop = tbody1.scrollHeight;
		}
		
		//明细序号重新赋值
		function resetAmountNum(){
			var i=1;
			$("#tbdAmount").find("tr[name='"+selectTickertape+"']").each(function () {
				$(this).children('td:eq(0)').text(i++);
			});
		}
		 
		// 整数千分位overflow-y:scroll;
		function toThousands(num) {
			var newStr = "";
			var count = 0;
		    var num = (num || 0).toString(), result = '';
		    if(num.indexOf(".")==-1){
			    while (num.length > 3) {
			    	result = ',' + num.slice(-3) + result;
			        num = num.slice(0, num.length - 3);
			    }
		    }else {
		    	for(var i = num.indexOf(".")-1;i>=0;i--){
				if(count % 3 == 0 && count != 0){
					newStr = num.charAt(i) + "," + newStr;
					}else{
					  newStr = num.charAt(i) + newStr; //逐个字符相接起来
					}
					count++;
				}
		    	num = newStr + (num + "00").substr((num + "00").indexOf("."),3);
		     }
		     if (num) { result = num + result; }
		     if (num.indexOf(".") == 0){
		    	 result = "0" + result;
		     }
		     return result;
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 设备缴存列表 -->
		<li id="disLi1"><a onClick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/" target="mainFrame"><spring:message code="door.panorama.equipDeposit" /></a></li>
		<!-- 封包缴存列表 -->
	    <li><a onClick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/packageList" target="mainFrame"><spring:message code="door.panorama.otherDeposit" /></a></li>
		<!-- 存款查看 -->
		<li id="disLi2" class="active">
			<%-- gzd 2020-04-14  修改链接  ${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=${doorOrderInfo.id} --%>
			<a>
				<spring:message code="door.doorOrder.deposit" /><spring:message code="common.view" /> 
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="depositPanorama" action="${ctx}/weChat/v03/doorOrderInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="amountList"/>
		<form:hidden path="detailIdList"/>
		<form:hidden path="tickertapeList"/>
		<form:hidden path="busTypeList"/>
		<form:hidden path="createByList"/>
		<form:hidden path="createDateList"/>
		<form:hidden path="remarksList"/>
		<sys:message content="${message}"/>	
		<div class="row">
		<%-- 页面左侧 --%>
		<div class="span7" style="width:55%">
			<div class="control-group">
				<%-- 门店 --%>
				<label class="control-label"><spring:message code="door.public.cust" />：</label>
				<div class="controls">
					<form:input path="doorName" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 机具 --%>
				<label class="control-label"><spring:message code="door.equipment.equipment" />：</label>
				<div class="controls">
					<form:input path="equipmentName" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 款袋编号 --%>
				<label class="control-label">
				<c:if test="${depositPanorama.method eq '4' }">
					<spring:message code="door.doorOrder.packNum" />
				</c:if>
				<c:if test="${depositPanorama.method eq '1' || depositPanorama.method eq '2'}">
					<spring:message code="door.panorama.packageNum" />
				</c:if>
				：</label>
				<div class="controls">
					<form:input path="rfid" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 总金额 --%>
				<label class="control-label"><spring:message code="common.totalMoney" />：</label>
				<div class="controls">
					<span class="help-inline"><font color="#555555"><fmt:formatNumber value="${doorOrderInfo.amount}" type="currency" pattern="#,##0.00"/></font> </span>
				</div>
			</div>
			<div class="control-group">
				<%-- 凭条号(模糊查询) --%>
				<label class="control-label"><spring:message code="door.checkCash.tickertape" />：</label>
				<div class="controls">
					<input type="text" class="input-large" style="text-align:right;" placeholder="请输入凭条号" id="tickertapeId" name="tickertapeId" maxlength="50">
				</div>
			</div>
			<div class="control-group">
				<%-- 业务备注(模糊查询) --%>
				<label class="control-label"><spring:message code="door.historyChange.remarks" />：</label>
				<div class="controls">
					<input type="text" class="input-large" style="text-align:right;" placeholder="请输入业务备注" id="remarksId" name="remarksId" maxlength="50">
				</div>
			</div>
			<div class="control-group" style="height: 400px; width: 100%;">
				<%-- 每笔金额 --%>
				<label class="control-label"><spring:message code="door.doorOrder.detailMoney" />：</label>
				<div class="controls" style="height: 400px;overflow: auto">
					<table id="tblTikertape" class="table">
						<thead>
							<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
								<%-- 序号 --%>
								<th style="text-align: center"><spring:message code="common.seqNo" /></th>
								<%-- 凭条 --%>
								<th style="text-align: center"><spring:message code="door.doorOrder.tickertape" /></th>
								<%-- 业务类型 --%>
								<th style="text-align: center"><spring:message code="door.doorOrder.businessType" /></th>
								<%-- 金额 --%>
								<th style="text-align: center"><spring:message code="door.public.money" /></th>
								<%-- 存款人 --%>
								<th style="text-align: center"><spring:message code="door.public.createBy" /></th>
								<%-- 存款日期 --%>
								<th style="text-align: center"><spring:message code="door.public.createDate" /></th>
								<%-- 存款备注 --%>
								<th style="text-align: center"><spring:message code="door.historyChange.remarks" /></th>
							</tr>
						</thead>
						<tbody id="tbdTikertape"></tbody>
					</table> 
				</div>
			</div>
		</div>
		<%-- 页面右侧 --%>
		<div class="span5" style="width:40%;margin-left:0px;margin-top:223px">
			<div class="control-group" id="divRight" style="height: 400px; width: 100%; "><!-- overflow: auto; -->
			<%-- 面值明细 --%>
			<label class="control-label"><spring:message code="door.public.denominationDetail" />：</label>
			<div class="controls" style="width:75%;">
				 <table id="tblAmount" class="table">
					 <thead>
						<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
							<%-- 序号 --%>
							<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
							<%-- 存款方式 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrder.saveMethod" /></th>
							<%-- 面值 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.parValue" /></th>
							<%-- 张数 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.sheetCount" /></th>
							<%-- 金额 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.money" /></th>
						</tr>
					</thead>
						<tbody id="tbdAmount"></tbody>
					</table> 
				</div>
			</div>
		</div>
		</div>
		
		<div id="disDiv1" class="form-actions" style="width:100%">
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)" />
				<%-- onclick="window.location.href='${ctx}/weChat/v03/doorOrderInfo/back'"/> --%>
		</div>
		<div style="display:none" >
			 <table id="tblTikertapeTemplate" class="table">
				<tbody id="tbdTikertape">
					<tr id="trTikertapeTemplate0" onclick="selectRow($(this))">
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;display:none"></td>
						<td style="text-align:center;"></td>
					</tr>
				</tbody>
			</table> 
		</div>
		
		<div style="display:none" >
			 <table id="tblAmountTemplate" class="table">
				<tbody id="tbdAmount">
					<tr id="trAmountTemplate0">
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					</tr>
				</tbody>
			</table> 
		</div>
	</form:form>
</body>
</html>