<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 任务管理 -->
	<title><spring:message code='clear.task.formTitle'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
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
			checkSession();
			$("#clearGroupDiv").load("${ctx}/clear/v03/cpTaskRecovery/getClearGroup?clearGroupId="+$('#clearGroup').val());
			//重置
			formClear();
			//设置计划类型
			change($("#planTypes").val());
			// 提交
			$("#btnSubmit").click(
				function() {
					var userCount="";	
					//获取ID为contentTable下面的input为text的ID
					 var list=document.getElementById("contentTable").getElementsByTagName("input");
					  //对表单中所有的input进行遍历
					 for(var i=0;i<list.length && list[i];i++){
					       //判断是否为文本框
					    var userIdcount = list[i].id;
					    var count = $("#" + userIdcount).val();
					    // 获取UserId
					    var userId = userIdcount.substring(0,32);
					    user = userId + count;
					    userCount = userCount+user + ",";
					    if(isNaN(count)){
					    	alertx('分配数量输入有误，请确认后重新输入！');
					    	return;
					    }
					    var flag= calUserNum(userId,count);
					    if(!flag){
					    	alertx('该面值有用户分配后数量为负数，请确认后重新输入！');
					    	return;
					    }
					 }
					var url = "${ctx}/clear/v03/cpTaskRecovery/save?userCount="+userCount;
					var totalsum = document.getElementById("totalsum").innerText;
					if(totalsum == 'NaN'){
						alertx("分配数量有误，请重新输入");
						return;
					}
					// 获取清分组
					var clearGroup=$("#clearGroup").val();
					if (clearGroup == ""){
						alertx("请选择复点组");
						return;
					}
					if(totalsum == 0){
						alertx("分配总数不能为空");
						return;
					}				
					$("#distributionAmount").removeClass();
					$("#inputForm").attr("action",url);
					$("#inputForm").submit();
				});
		});
		
		// 获取清分组人员
		function getClearGroup(type) {
			checkSession();
			$("#clearGroupDiv").load("${ctx}/clear/v03/cpTaskRecovery/getClearGroup?clearGroupId="+ type);
		}
		
		// 单独分配各人员数量
		function changeNum(userId) {
			// 获取面值
			var denomination = $("#denomination").val();
			// 获取输入的值
			var id = userId + "count";
			var count = document.getElementById(id).value;
			if (count == "") {
				count = 0;
			}
			// 获取对应人的对应面值
			//var a = document.getElementById(denomination + userId).value;
			//var sum = userId + "sum";
			//document.getElementById(a).innerText = count;
			//document.getElementById(sum).innerText = count;
			//计算总数量
			calculateTotal(denomination);
		}
		
		// 批量分配各人员数量
		function changeAllSum() {
			// 获取清分组
			var clearGroup = $("#clearGroup").val();
			if (clearGroup == "") {
				alertx("请选择清分组");
				return;
			}
			// 获取面值
			var denomination = $("#denomination").val();
			// 获取批量分配数量
			var distributionAmount = $("#distributionAmount").val();
			if (distributionAmount == "") {
				alertx("请输入分配数量");
				return;
			}

			//获取ID为contentTable下面的input为text的ID
			var list = document.getElementById("contentTable")
					.getElementsByTagName("input");

			//对表单中所有的input进行遍历
			for (var i = 0; i < list.length && list[i]; i++) {
				//判断是否为文本框
				if (list[i].type == "text") {
					var userIdcount = list[i].id;

					$("#" + userIdcount).val(distributionAmount);
					// 获取面值
					var denomination = $("#denomination").val();
					// 获取UserId
					var userId = userIdcount.substring(0, 32);
					// 获取对应user对应面值的ID
					var denoId = denomination + userId;
					// 获取对应user总计数量
					var userSum = userId + "sum";
					//document.getElementById(denoId).innerText = distributionAmount;
					//document.getElementById(userSum).innerText = distributionAmount;
				}
			}
			// 计算总金额
			calculateTotal(denomination);

		}

		// 重置
		function formClear() {
			//$("#hi").text("");
			$("#distributionAmount").val("");
			var clearGroupId = document.getElementById("clearGroup").value;
			checkSession();
			$("#clearGroupDiv").load("${ctx}/clear/v03/cpTaskRecovery/getClearGroup?clearGroupId="+ clearGroupId);
			$("#totalcount").val("0");
		}
		// 计算总数量
		function calculateTotal(denomination) {
			 var tableInfo = 0;
			 /*var tableId = document.getElementById("contentTable");
			for (var i = 1; i < tableId.rows.length - 1; i++) { //遍历Table的所有Row
				for (var j = 4; j < tableId.rows[i].cells.length - 1; j++) { //遍历Row中的每一列
					var value = tableId.rows[i].cells[j].innerText; //获取Table中单元格的
					tableInfo = parseFloat(tableInfo) + parseFloat(value); //计算总金额
				}
			} */
			
			var allInputs = document.getElementsByTagName('input');
			for (var i = 0; i < allInputs.length; i++) {
				if (allInputs[i].type === 'text'&& allInputs[i].name === 'countTotal') {
					var value=allInputs[i].value;
					if (value == "") {
						value = 0;
					}
					if(isNaN(value)){
						value = 0;
					}
					//alert(value);
					tableInfo =parseFloat(tableInfo) + parseFloat(value); 
				}
			}
			//alert(tableInfo);
			document.getElementById("totalsum").innerText = tableInfo;
			//document.getElementById("totalsumnum").innerText = tableInfo;
			//var totalde = denomination + "totalsum";
			//document.getElementById(totalde).innerText = tableInfo;
			$("#totalCount").val(tableInfo);
		}
		
		function calUserNum(userId,count){
			var flag = true;
			// 获取面值
			var denomination = $("#denomination").val();
			var a = document.getElementById(denomination + userId).innerText;
			if(parseFloat(count)<0){
				var sum = parseFloat(a)+parseFloat(count);
				if(sum<0){
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		}
		
		//计划类型
		function change(num){
			var sheet1 = document.getElementById('normalclear1');
			var sheet2 = document.getElementById('normalclear2');
			var sheet3 = document.getElementById('normalclear3');
			if(num == 1){
			
				sheet1.className = 'active';
				$("#normalclear2").removeClass();
				$("#normalclear3").removeClass();
				$("#planType").val("01");
				return;
			}
			if(num == 2){
			
				sheet2.className = 'active';
				$("#normalclear1").removeClass();
				$("#normalclear3").removeClass();
				$("#planType").val("02");
				return;
				
			}
			if(num == 3){
			
				sheet3.className = 'active';
				$("#normalclear2").removeClass();
				$("#normalclear1").removeClass();
				$("#planType").val("03");
				return;
		
			}

		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 任务回收列表 -->
		<li><a href="${ctx}/clear/v03/cpTaskRecovery/list"><spring:message code='clear.task.recovery.list'/></a></li>
		<!-- 任务回收登记 -->
		<li class="active"><a href="${ctx}/clear/v03/cpTaskRecovery/form"><spring:message code='clear.task.recovery.register'/></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="cpTaskMain" action="" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="totalCount" id="totalCount"/>
		<sys:message content="${message}"/>

		<div>
			<div class="row" >
				<!-- 交接人 -->
				<div class="control-group item" style="margin-left: 90px;">
					<label><spring:message code='clear.task.handover.name' />：</label>
					<div style="margin-left: 80px; margin-top: -20px">
						<form:select path="joinManNo" id="joinManNo"
							class="input-medium required">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getUsersByTypeAndOffice('31',fns:getUser().office.id)}"
								itemLabel="escortName" itemValue="id" htmlEscape="false" />
						</form:select><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<!--复点组 -->
				<div class="control-group item" style="margin-left: 90px;">
					<label><spring:message code='clear.task.afterPoint.group' />：</label>
					<div style="margin-left: 80px; margin-top: -20px">
						<form:select path="clearGroup" id="clearGroup"
							class="input-medium required" onchange="getClearGroup(this.options[this.selectedIndex].value)">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${sto:getClearingGroupName('0','08')}"
								itemLabel="groupName" itemValue="id" htmlEscape="false" id="clearGroupId" />
						</form:select><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<!-- 面值 -->
				<div class="control-group item" style="margin-left: 90px;">
					<label  style="margin-left: 16px;"><spring:message code='common.denomination' />：</label>
					<div style="margin-left: 80px; margin-top: -20px">
						<form:select path="denomination" id="denomination"
							class="input-medium required" onchange="formClear()">
							<form:options items="${sto:getGoodDictListWithFg('cnypden')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<!--券别信息-->
				<input type="hidden" id="currency" name="currency" value="101"/>
				<input type="hidden" id="classification" name="classification" value="02"/>
				<input type="hidden" id="cash" name="cash" value="1"/>
				<input type="hidden" id="unit" name="unit" value="101"/>
				<input type="hidden" id="sets" name="sets" value="5"/>
			</div>
		</div>
		<div class="span9">
			<div class="row" >
				<!-- 批量分配 -->
				<div class="control-group item" style=" margin-left: -25px;margin-top: 10px">
					<label class="control-label"><spring:message
							code="clear.task.distributionAmount" />(<spring:message code='clear.public.bundle' />)：</label>
					<div class="controls" style="margin-left: 175px;">
						<form:input id="distributionAmount"
							onkeyup="value=value.replace(/[^0-9]/g,'')"
							path="distributionAmount" htmlEscape="false" maxlength="3"
							class="digits required" style="width:150px;" /><span class="help-inline"><font color="red">*</font> </span>
					</div>
					<!-- 批量分配，重置按钮 -->
					<div class="controls" style="margin-left: 450px; margin-top: -30px">
						<input id="add" class="btn btn-primary" type="button"
							value="<spring:message code='clear.task.batch'/>"  onclick="changeAllSum()"/>
						<input id="reset" class="btn btn-primary required" type="button" value="<spring:message code='clear.public.reset'/>" onclick="formClear()"/>
					</div>
				</div>
				
				<div class="control-group item" style="margin-top: 25px">
							<table id="contentListTable"
								class="table  table-hover"
								style="width: 1100px">
								<thead>
									<tr>
										<th style="text-align: center;"><spring:message
												code='common.denomination' /></th>
										<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}"
											var="item">
											<th style="text-align: right;">${item.label}(<spring:message code='clear.public.bundle' />)</th>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<tr>
										<!-- 当日已分配数量 -->
										<td style="text-align: center;"><spring:message code='clear.public.nowAllotment' /></td>
										<c:forEach items="${holeClTaskMainList}" var="clTaskMain">
											<td style="text-align: center;">${clTaskMain.totalCount}</td>
										</c:forEach>
									</tr> 
									<tr>
										<!-- 当日已回收数量 -->
										<td style="text-align: center;"><spring:message code='clear.public.nowRecycling' /></td>
										<c:forEach items="${holeClTaskRecoveryList}" var="clTaskRecovery">
											<td style="text-align: center;">${clTaskRecovery.totalCount}</td>
										</c:forEach>
									</tr> 
								</tbody>
							</table>
						</div>
				<!-- 计划类型 -->
				<ul class="nav nav-tabs" style="width: 1200px;">
					<!-- 正常清分 -->
					<li id="normalclear1" class="active"><a href="#"
						onclick="change(1)"><spring:message code='clear.task.planType.normalClear'/></a></li>
					<!-- 重复清分 -->
					<li id="normalclear2" onclick="change(2)"><a href="#"><spring:message code='clear.task.planType.repeatClear'/></a></li>
					<!-- 抽查 -->
					<li id="normalclear3" onclick="change(3)"><a href="#"><spring:message code='clear.task.planType.checkClear'/></a></li>
				</ul>
				<!--清分组成员列表-->
				<div class="control-group item" style="overflow-y:auto;height:450px; width: 1200px;margin-top: 0px" id="clearGroupDiv"  >
				</div>
				<input type="hidden" id="planType" name="planType" value="01" />
				<input type="hidden" id="planTypes"  value="${cpTaskMain.planType}" />
				
			</div>
		</div>
		<%--提交，返回按钮--%>
					<div class="form-actions" style="clear:both;width:100%;">
						<input id="btnSubmit" class="btn btn-primary" type="button"
								value="<spring:message code='common.commit'/>" />&nbsp;
						<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>"
							onclick="window.location.href='${ctx}/clear/v03/cpTaskRecovery/back'"  />
					</div>
	</form:form>
</body>
</html>