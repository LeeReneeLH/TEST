<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 存款管理 -->
	<title><spring:message code="door.doorOrderException.title" /></title>
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
    </style>
	<script type="text/javascript">
		var selectTickertape='';	//选中凭条号
	
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
				var exceptionDetailList = $('#exceptionDetail').val();
				//console.log('a',$('#exceptionDetailList').val());
				$.each(exceptionDetailList, function (index, value) {
					if (index > 0) {
						var newRow = $("#trAmountTemplate0").clone(true).attr("id", "tickertape_" + exceptionDetailList[index]);
						newRow.removeAttr("style");
						// 添加一行
						newRow.appendTo("#tblTikertape");
						// 序号
						newRow.children('td:eq(0)').text(index);
						newRow.children('td:eq(1)').text(exceptionDetailList[index].type);
						newRow.children('td:eq(2)').text(exceptionDetailList[index].count);
						newRow.children('td:eq(3)').text(exceptionDetailList[index].denomination);
						newRow.children('td:eq(4)').text(exceptionDetailList[index].amount);
						// 凭条
						
						
						if (index == 1) {
							selectRow(newRow);
						}
					}
				});
			/* } */
        });

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
			newRow.children('td:eq(1)').text(data.type);
			// 面值
			newRow.children('td:eq(2)').text(data.denomination);
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
		
		$(document).ready(function(){
			denominationName();
		});
		
		//面值显示
		function denominationName(){
			//var k=1;
			$("#tbdAmount").find("tr[name=trDetail]").each(function(){
				var denomination=$.trim($(this).children('td:eq(2)').text());
				if(denomination==""||denomination==null){
					$(this).children('td:eq(3)').text("");
				}else if(denomination=="15"){
					$(this).children('td:eq(3)').text("100元_纸币");
				}else if(denomination=="16"){
					$(this).children('td:eq(3)').text("50元_纸币");
				}else if(denomination=="17"){
					$(this).children('td:eq(3)').text("20元_纸币");
				}else if(denomination=="18"){
					$(this).children('td:eq(3)').text("10元_纸币");
				}else if(denomination=="19"){
					$(this).children('td:eq(3)').text("5元_纸币");
				}else if(denomination=="20"){
					$(this).children('td:eq(3)').text("2元_纸币");
				}else if(denomination=="21"){
					$(this).children('td:eq(3)').text("1元_纸币");
				}else if(denomination=="22"){
					$(this).children('td:eq(3)').text("5角_纸币");
				}else if(denomination=="23"){
					$(this).children('td:eq(3)').text("2角_纸币");
				}else if(denomination=="24"){
					$(this).children('td:eq(3)').text("1角_纸币");
				}
				/* hzy  添加分数     START*/
				else if(denomination=="25"){
					$(this).children('td:eq(3)').text("5分_纸币");
				}else if(denomination=="26"){
					$(this).children('td:eq(3)').text("2分_纸币");
				}else if(denomination=="27"){
					$(this).children('td:eq(3)').text("1分_纸币");
				}
				else if(denomination=="28"){
					$(this).children('td:eq(3)').text("1元_硬币");
				}else if(denomination=="29"){
					$(this).children('td:eq(3)').text("5角_硬币");
				}else if(denomination=="30"){
					$(this).children('td:eq(3)').text("1角_硬币");
				}
				else if(denomination=="31"){
					$(this).children('td:eq(3)').text("5分_硬币");
				}else if(denomination=="32"){
					$(this).children('td:eq(3)').text("2分_硬币");
				}else if(denomination=="33"){
					$(this).children('td:eq(3)').text("1分_硬币");
				}
				/* END */
			});
		}
		
		// 消耗时间
		function getCostTime(aa) { 
			/* var dateLeft = 0;
			var hourLeft = 0;
			var minuteLeft = 0;
			var secondLeft = 0; */
			var timeLeft = [0,0,0,0];
			var dateStr = "";
			var ts = parseInt(aa / 1);
			timeLeft[0] = (ts > 86400) ? parseInt(ts / 86400) : 0;
			ts = ts - timeLeft[0] * 86400;
			timeLeft[1] = (ts > 3600) ? parseInt(ts / 3600) : 0;
			ts = ts - timeLeft[1] * 3600;
			timeLeft[2] = (ts > 60) ? parseInt(ts / 60) : 0;
			timeLeft[3] = ts - timeLeft[2] * 60;
			timeStr = (timeLeft[0] > 0) ? timeLeft[0] + "天" : "";
			timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0) ? "" : (timeLeft[1] + "小时");
			timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0 && timeLeft[2] <= 0) ? "" : (timeLeft[2] + "分钟");
			timeStr += (timeLeft[0] <= 0 && timeLeft[1] <= 0 && timeLeft[2] <= 0 && timeLeft[3] <= 0) ? "" : (timeLeft[3] + "秒");
	    	return timeStr; 
	    }
		
		$(document).ready(function(){
	       $("#costTime").val(getCostTime($("#ct").val()));
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 存款异常列表 -->
		<li><a href="${ctx}/doorOrder/v01/doorOrderException/"><spring:message code="door.doorOrderException.list" /></a></li>
		<!-- 存款异常查看 -->
		<li class="active">
			<!-- gzd 2020-04-14  修改链接  ${ctx}/doorOrder/v01/doorOrderException/detailForm?id=${doorOrderException.id} -->
			<a>
				<spring:message code="door.doorOrderException.detailForm" /> 
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="doorOrderException" action="${ctx}/doorOrder/v01/doorOrderException/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="amountList"/>
		<form:hidden path="detailList"/>
		<form:hidden path="tickertapeList"/>
		<form:hidden path="busTypeList"/>
		<form:hidden path="costTime" id="ct"/>
		<form:hidden path="exceptionDetailList" id="exceptionDetailList"/>
		<sys:message content="${message}"/>	
		<div class="row">
		<%-- 页面左侧 --%>
		<div class="span5" style="width:430px">
			<div class="control-group">
				<!-- 开始时间 -->
				<label class="control-label"><spring:message code="door.doorOrderException.startTime" />：</label>
				<div class="controls">
					<input id="startTime" name="startTime" type="text" readonly="readonly" maxlength="20" 
					class="input-large Wdate" value="<fmt:formatDate value="${doorOrderException.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			<div class="control-group">
				<!-- 结束时间 -->
				<label class="control-label"><spring:message code="door.doorOrderException.endTime" />：</label>
				<div class="controls">
					<input id="endTime" name="endTime" type="text" readonly="readonly" maxlength="20"
					class="input-large Wdate" value="<fmt:formatDate value="${doorOrderException.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			<div class="control-group">
				<!-- 消耗时间 -->
				<label class="control-label"><spring:message code="door.doorOrderException.costTime" />：</label>
				<div class="controls">
					<input id="costTime" name="costTime" type="text" readonly="readonly" maxlength="20"
					class="input-large Wdate" value=""/>
				</div>
			</div>
			<div class="control-group">
				<%-- 门店 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.cust" />：</label>
				<div class="controls">
					<form:input path="doorName" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 存款备注 --%>
				<label class="control-label"><spring:message code="door.historyChange.remarks" />：</label>
				<div class="controls">
					<form:input path="remarks" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 机具 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.eqpId" />：</label>
				<div class="controls">
					<form:input path="seriesNumber" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 款袋编号 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.packNum" />：</label>
				<div class="controls">
					<form:input path="bagNo" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				<%-- 业务类型 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.businessType" />：</label>
				<div class="controls">
					<form:input path="businessTypeName" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			<div class="control-group">
				
				<label class="control-label"><spring:message code="door.doorOrderException.tickerTape" />：</label>
				<div class="controls">
					<form:input path="tickerTape" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div>
			 <div class="control-group">
				
				<label class="control-label"><spring:message code="door.doorOrderException.user" />：</label>
				<div class="controls">
					<form:input path="userName" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
				</div>
			</div> 
			<div class="control-group">
				<%-- 总金额 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.totalAmount" />：</label>
				<div class="controls">
					<span class="help-inline"><font color="#555555"><fmt:formatNumber value="${doorOrderException.totalAmount }" type="currency" pattern="#,##0.00"/></font> </span>
				</div>
			</div>
		</div>
		<div class="span7" style="width:60%">
			<div class="control-group" style="height: 400px; width: 100%;">
				<%-- 金额明细 --%>
				<label class="control-label"><spring:message code="door.doorOrderException.parValueDetail" />：</label>
				<div class="controls" style="height: 400px;overflow: auto">
					<table id="tbdAmount" class="table">
						<thead>
							<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
							<%-- 序号 --%>
							<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
							<%-- 存款方式 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.saveMethod" /></th>
							<%-- 面值 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.parValue" /></th>
							<%-- 张数 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.count" /></th>
							<%-- 金额 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.amount" /></th>
						</tr>
						</thead>
						<tbody>
							<c:forEach items="${doorOrderException.exceptionDetailList}" var="detail" varStatus="status">
							<tr  name="trDetail">
					           <td style="text-align: center">${status.index+1}</td>
					           <td style="text-align: center">${fns:getDictLabel(detail.type, 'save_method', '')}</td>
					            <td style="display:none">${detail.denomination}</td>
								<td style="text-align: center"></td>
					           <%-- <td>${sto:getGoodDictLabel(detail.denomination, 'cnypden','')}</td> --%>
					           <td style="text-align: center">${detail.count}</td>
					           <td style="text-align: center">${detail.amount}</td>
							</tr>
							</c:forEach>
		                </tbody>
					</table> 
				</div>
			</div>
		</div>
		</div>
		
		<div class="form-actions" style="width:100%">
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)"/>
				<%-- onclick="window.location.href='${ctx}/doorOrder/v01/doorOrderException/back'"/> --%>
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