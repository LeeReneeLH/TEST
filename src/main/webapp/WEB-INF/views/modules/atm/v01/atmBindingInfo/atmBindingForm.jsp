<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='atm.add.binding.register'/><!-- 加钞绑定登记 --></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		var checkBoxResult = true;
		var addAmount=0;
		$(document).ready(function() {
			setReadOnly();
			boxInfoList();
			var bindingId = $("#bindingId").val();
			if(bindingId == "" || bindingId == null || bindingId == undefined) {
				var selectAddPlan = $("#addPlanId").val();
				if(selectAddPlan != "" && selectAddPlan != null && selectAddPlan != undefined){
					getAtmInfo(selectAddPlan);
				}
			}else{
				/* 修改时格式化金额  修改人：xl 修改时间：2017-12-25 begin */
				addAmount=$("#addAmount").val();
				$("#addAmount").val(formatCurrencyFloat(addAmount));
				$("#amount").val(formatCurrencyFloat($("#amount").val()));
				/* end */
			}
			
			/* 提交  xl 2017-12-25*/
			$("#btnSubmit").click(function(){
				$("#amount").val($("#amount").val().toString().replace(/\$|\,/g, ''));
				$("#addAmount").val($("#addAmount").val().toString().replace(/\$|\,/g, ''));
				$("#inpytForm").submit();
			});
			
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
		
		/*修改时设置只读*/
		function setReadOnly() {
			var bindingId = $("#bindingId").val();
			if(bindingId != "" && bindingId != null && bindingId != undefined) {
				$("#addPlanId").attr("readOnly", true);
				$("#atmNo").attr("readOnly", true);
				$("#atmAccount").attr("readOnly", true);
			}
		}
	
	
		function getAtmInfo(addPlanId) {
			$("#atmPlanId").val(addPlanId);
			$("#atmNo option").remove();
			$.post('${ctx}/atm/v01/atmBindingInfo/getAtmInfo', {addPlanId: addPlanId}, function(data){
					var list = data.atmPlanInfoList;
					var len = list.length;	
					//var mod = $(".modId").val();
					for(var i=0;i<len;i++){
						var val = list[i];
                        var atmNo = val.atmNo;
                        //var modName = val.modName;
                        if(i == 0){
                        	$("#atmNo").append("<option value='"+atmNo+"' selected='selected'>"+atmNo+"</option>");
                        }else{
                        	$("#atmNo").append("<option value='"+atmNo+"'>"+atmNo+"</option>");
						}										
					}
					$("#atmNo").trigger("change");
				},"json");
		}
		
		/*根据钞箱号检查结果判断是否新增行*/
		function addRow() {
			var repeatValue = false;
			var inputBoxNo = $("#inputBoxNo").val().trim();
			
			/*判断输入箱号是否重复*/
			var rowCount = $("#boxNoTable tbody tr").length;
			for(a=0; a<rowCount; a++) {
				if($("#box"+(a+1)).val() == inputBoxNo){
					$("#checkMsg").html("<font style=\"font-weight: bold;color:#ea5200\"><spring:message code='atmbox.add.cash.box.repead'/></font>");
					repeatValue = true;	
					break;
				}
			}
			
			/*当箱号不重复时，检查箱号与ATM机是否匹配，匹配时新增一行*/
			if(!repeatValue) {
				var rowCount = $("#boxNoTable tbody tr").length;
				var rowNo = rowCount+1;
				var row = "<tr style='height:36px;'><td class='control-label'><label  class='control-label' style='margin-top:2px;'><spring:message code='label.atmbox.add.cash.box'/>"+rowNo+"：</label></td><td class='controls'  style='width:240px;'><div style='margin-left: 7%;'>"
				+"<input id='box"+rowNo+"' type='text' style='font-size:16px' class='input required' readonly='true'/></div></td><td class='controls'><a onclick='delRow("+rowNo+")'><i class='fa fa-trash-o text-red fa-lg'></i></a></td></tr>";
				$("#boxNoTable tbody").append(row);
				$("#box"+rowNo).val(inputBoxNo);
				boxInfoList();
				$("#inputBoxNo").val("");
				//改变加钞金额  修改人：xl 修改时间：2017-12-14 begin
				var resultAmount=getBoxAmount(inputBoxNo);
				addAmount=Number(addAmount)+Number(resultAmount);
				$("#addAmount").val(formatCurrencyFloat(addAmount));
				//end
			}
		}
		
		/*删除钞箱号*/
		function delRow(rowNo) {
			//改变加钞金额  修改人：xl 修改时间：2017-12-14 begin
			var inputBoxNo = $("#box"+rowNo).val();
			var resultAmount=getBoxAmount(inputBoxNo);
			addAmount=Number(addAmount)-Number(resultAmount);
			$("#addAmount").val(formatCurrencyFloat(addAmount));
			//end
			var deleteRow = "#boxNoTable tbody tr:eq("+(rowNo-1)+")";
			$(deleteRow).remove();
			var rowCount = $("#boxNoTable tbody tr").length;
			for(a=0;a<rowCount;a++){
				$("#boxNoTable tbody tr:eq("+a+") td:eq(0) label").text("<spring:message code='label.atmbox.add.cash.box'/>"+(a+1)+"：");
				$("#boxNoTable tbody tr:eq("+a+") td:eq(1) div:eq(0) input:eq(0)").attr("id","box"+(a+1));
				$("#boxNoTable tbody tr:eq("+a+") td:eq(2) a:eq(0)").attr("onclick","delRow("+(a+1)+")");
			}
			boxInfoList();
		}
		
		function cleanBoxTable() {
			$("#boxNoTable tbody tr").remove();
		}
		
		function checkBoxNo(){
			/*验证钞箱是否为空 修改人：xl 修改时间：2017-12-29 begin*/
			$("#checkMsg").html("");
			var inputBoxNo = $("#inputBoxNo").val();
			if(inputBoxNo == '' || inputBoxNo == null || typeof (inputBoxNo) == "undefined" ){
				$("#checkMsg").html("<font style=\"font-weight: bold;color:#ea5200\"><spring:message code='atm.add.binding.boxNo.required'/></font>");
				checkBoxResult = false;
				return false;
			}
			//钞箱编号列表
			var rowCount = $("#boxNoTable tbody tr").length;
			var boxNoList="";
			for(a=0; a<rowCount; a++) {
				boxNoList+=($("#box"+(a+1)).val())+",";
			}
			boxNoList+=inputBoxNo;
			/*end*/
			var atmNo = $("#atmNo").val();
			if(atmNo != null && atmNo != "" && atmNo != undefined){
				$.post('${ctx}/atm/v01/atmBindingInfo/checkBoxInfo', {boxNo: inputBoxNo, atmNo: atmNo,boxNoList:boxNoList}, function(data){
					var resultMsg = data.msg;
					var resultStatur = data.status;
					if(resultStatur == false){
						$("#checkMsg").html("<font style=\"font-weight: bold;color:#ea5200\">"+resultMsg+"</font>");
						checkBoxResult = false;
					}else {
						$("#checkMsg").html("");
						checkBoxResult = true;
						addRow();
					}
				},"json");
			}else{
				alertx("<spring:message code='atm.add.binding.addPlanId.empty'/>");
			}
		}
		
		function boxInfoList(){
			$("#boxList").val("");
			var rowCount = $("#boxNoTable tbody tr").length;
			var boxList = new Array();
			for(a=0;a<rowCount;a++){
				var id = "#box"+(a+1);
				if($(id).val() != null && $(id).val() != "" && $(id).val() != undefined){
					boxList.push($(id).val());	
				}
				
			}
			$("#boxList").val(boxList);
		}
		
		/* 根据钞箱号查询金额  修改人：xl 2017-12-25 */
		function getBoxAmount(boxNo){
			var resultAmount = 0;
			$.ajax({
				url:'${ctx}/atm/v01/atmBindingInfo/getBoxAmount', 
				data:{boxNo: boxNo}, 
				async: false,
				cache : false, 
				type:"POST",
				dataType:"JSON",
				success :function(data){
					resultAmount = data.amount;				
				}
			});
			return resultAmount;
		}
		/* 千分位用逗号分隔  修改人： xl 2017-12-25 */
		function formatCurrencyFloat(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
			return (((sign) ? '' : '-') + num + '.' + cents);
		}

		/* 将金额转换成数字   xl 2017-12-25 */
		function update(obj){ 
			if (typeof (obj.value) != "undefined" && obj.value != null && obj.value != "") {
				var money = obj.value.toString().replace(/\$|\,/g, '');
				$("#amount").val(money);
			}
		}
		/* 增加格式化  xl 2017-12-25 */
		function formatCurrency(obj){
			if (obj.value!==null&&obj.value!=="") {
				var money=formatCurrencyFloat(obj.value);
				$("#amount").val(money);
			}
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/atm/v01/atmBindingInfo/addList"><spring:message code='atm.add.cash.binding.list'/></a></li><!-- 加钞绑定列表 -->
		<c:choose>
			<c:when test="${atmBindingInfo.bindingId == '' || atmBindingInfo.bindingId == null }">
				<li class="active"><a href="${ctx}/atm/v01/atmBindingInfo/form"><spring:message code='atm.add.binding.register'/></a></li><!-- 加钞绑定登记 -->
			</c:when>
			<c:when test="${atmBindingInfo.bindingId != null && atmBindingInfo.bindingId != '' }">
				<li class="active"><a href="${ctx}/atm/v01/atmBindingInfo/form?bindingId=${atmBindingInfo.bindingId}"><spring:message code='atm.add.binding.modify'/></a></li><!-- 加钞绑定修改 -->
			</c:when>
		</c:choose>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="atmBindingInfo"
		action="${ctx}/atm/v01/atmBindingInfo/save" method="post"
		class="form-horizontal">
		<sys:message content="${message}" />
		<form:hidden path="bindingId" />
		<form:hidden path="abdL" />
		<!-- 加钞计划 -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code="label.atm.add.cash.plan" />：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty atmBindingInfo.addPlanId}">
						<form:select path="addPlanId" id="addPlanId" class="input-large required" onChange="getAtmInfo(this.value)">
							<option value="${atmBindingInfo.addPlanId}" selected="selected">${addPlanName}</option>
						</form:select>
					</c:when>
					<c:otherwise>
						<form:select path="addPlanId" id="addPlanId" class="input-large required" onChange="getAtmInfo(this.value)">
							<form:options items="${atmPlanInfoList}" itemLabel="addPlanName" itemValue="addPlanId" htmlEscape="false" />
						</form:select>
					</c:otherwise>
				</c:choose>			
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 加钞计划Id -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code='label.atm.add.plan.id'/>：</label>
			<div class="controls">	
				<form:input path="" value="${atmBindingInfo.addPlanId}" id="atmPlanId" readOnly="true" class="required" style="font-size:16px"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<!-- ATM编号 -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code="label.atm.id.no" />：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${atmBindingInfo.atmNo != '' && atmBindingInfo.atmNo != null }">
						<form:select path="atmNo" id="atmNo" class="input-large required" onchange="cleanBoxTable()">
							<option  value="${atmBindingInfo.atmNo }" selected="selected">${atmBindingInfo.atmNo }</option>
						</form:select>
					</c:when>
					<c:otherwise>
						<form:select path="atmNo" id="atmNo" class="input-large required" onchange="cleanBoxTable()">
						</form:select>
					</c:otherwise>
				</c:choose>			
				<span class="help-inline"><font color="red">*</font> </span>
				<form:hidden path="atmAccount" />
			</div>
		</div>
		
		<!-- 加钞金额 -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code="label.atm.day.amount" /><spring:message code="common.units.yuan.alone" />：
			</label>
			<div class="controls">
				<form:input id="addAmount" path="addAmount" htmlEscape="false" class="input" readonly="true" style="font-size:16px"/>
			</div>
		</div>
		
		<!-- 加钞时间 -->
		<div class="control-group">
			<label  class="control-label"><spring:message code="label.atm.add.date"/>：</label>
			<div class="controls">
				<input id="addDate"  name="addDate" type="text"  maxlength="20" style="font-size:16px" readonly="readonly" class="input Wdate createTime"  
					value="<fmt:formatDate value='${atmBindingInfo.addDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"
			  		onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'});"/></div><!-- ,maxDate:'#F{$dp.$D(\'addDate\')||\'%y-%M-%d\'}' -->
		</div>
			   
		<!-- 清机金额 -->
		<div class="control-group">
			<label class="control-label">
				<spring:message code="label.atm.clear.amount" /><spring:message code="common.units.yuan.alone" />：
			</label>
			<div class="controls">
				<form:input id="amount" path="amount"  htmlEscape="false" oninput='if(value.length>9)value=value.slice(0,9)' style="font-size:16px" onblur="formatCurrency(this)" onfocus="update(this)" class="input required number" />
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		
		<!-- 清机时间 -->
		<div class="control-group">
			<label  class="control-label"><spring:message code="label.atm.clear.date"/>：</label>
			<div class="controls">
			<input id="clearDate"  name="clearDate" type="text" style="font-size:16px" readonly="readonly" maxlength="20" class="input Wdate createTime" 
				value="<fmt:formatDate value='${atmBindingInfo.addDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"
			   	onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss'});"/></div><!-- ,maxDate:'#F{$dp.$D(\'clearDate\')||\'%y-%M-%d\'}' -->
		</div>
		<!-- 加钞钞箱 -->
		<div class="control-group">
			<label  class="control-label"><spring:message code="label.atmbox.add.cash.box"/>：</label>
			<div class="controls">
				<input id="inputBoxNo" type="text" class="input" style="font-size:16px" />
				<span class="help-inline"><input id="btnAddRow" class="btn btn-primary" onclick="checkBoxNo()" type="button" value="<spring:message code="common.add"/>" /></span>
				<span id="checkMsg"></span>
			</div>
		</div> 
		<input id="boxList" name="boxList" type="hidden"/>
		<!-- 加钞箱 -->
		<div class="control-group">
			<table id="boxNoTable">
				<tbody>
					<c:if test="${atmBindingInfo.abdL != null}">
						<c:forEach items="${atmBindingInfo.abdL }" var="atmBindingDetail" varStatus="status">
							<tr style="height:36px;">
								<!-- 加钞钞箱 -->
								<td class="control-label"><label class="control-label" style="margin-top:2px;"><spring:message code="label.atmbox.add.cash.box"/>${status.index+1}：</label></td>
								<td class="controls" style="width:240px;">
									<div style="margin-left: 7%;">
										<input id="box${status.index+1}" type="text" class="input required" value="${atmBindingDetail.boxNo }" readonly="true" style="font-size:16px"/>
									</div>
								</td>
								<td class="controls"><a onclick="delRow(${status.index+1})"><i class='fa fa-trash-o text-red fa-lg'></i></a></td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
			
		</div>
		
		
		<div class="form-actions">
			<shiro:hasPermission name="atm:v01:atmBindingInfo:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.save'/>" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="window.location.href='${ctx}/atm/v01/atmBindingInfo/back'"/>
		</div>
	</form:form>
</body>
</html>