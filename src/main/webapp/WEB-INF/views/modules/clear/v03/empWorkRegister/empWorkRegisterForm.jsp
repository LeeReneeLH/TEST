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
			//初始页面加载清分复点组
			changeGroup();
			//设置计划类型
			change($("#planTypes").val());
			//重置
			formClear();
			var types = $("#busType").val();
			var date = $("#operateDate").val();
			$("#dayTotal").load("${ctx}/clear/v03/empWorkRegister/getDayTotalCount?type=" +types+"&date="+date);
			//初始页面加载当日累计分配量/回收量统计
			//$("#dayTotal").load("${ctx}/clear/v03/empWorkRegister/getDayTotalCount");
			//$("#dayTotal").load("${ctx}/clear/v03/empWorkRegister/getDayTotalCount?type="+$("#busType").val());
			//初始页面加载清分列表
			//$("#clearGroupDiv").load("${ctx}/clear/v03/empWorkRegister/getClearGroup");

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
								var url = "${ctx}/clear/v03/empWorkRegister/save?userCount="+userCount;
								var totalsum = document.getElementById("totalsum").innerText;
								if(totalsum == 'NaN'){
									alertx("分配数量有误，请重新输入");
									return;
								}
								// 验证分配数 
								$("#distributionAmount").removeClass();
								$("#inputForm").attr("action",url);
								$("#inputForm").submit();

							});

				});
		
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
			$("#hi").text("");
			$("#distributionAmount").val("");
			var clearGroupId = document.getElementById("clearGroup").value;
			var date = $("#operateDate").val();
			var busType = $("#busType").val();
			checkSession();
			$("#clearGroupDiv").load(
					"${ctx}/clear/v03/empWorkRegister/getClearGroup?clearGroupId="
							+ clearGroupId+"&date="+date+"&busType="+busType);
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
		
		function changeGroup(type,as){
			var types;
			if(type==null){
				types = $("#busType").val();
			}else{
				types = type;
				//更改业务类型,重新加载统计页面
				$("#clearGroupDiv").load(
						"${ctx}/clear/v03/empWorkRegister/getClearGroup?clearGroupId="
								+ "");
			}
			var date = $("#operateDate").val();
			var clearGroupId = $("#clearGroup").val();
			//清空请分组 wzj 2017-11-28 begin
			$("#clearGroup").val('');
			//end
			//更改业务类型，重新加载当日累计分配量、当日累计回收量
			$("#dayTotal").load("${ctx}/clear/v03/empWorkRegister/getDayTotalCount?type=" +types+"&date="+date);
			//初始化清分/复点组 默认值
			// $("#clearGroup").val("");
			 /*$("#clearGroup").append("<option value='' selected='selected'><spring:message code='common.select' /></option>"); */
			/* $("#clearGroupDiv").load(
					"${ctx}/clear/v03/empWorkRegister/getClearGroup?clearGroupId="
							+ clearGroupId+"&date="+date+"&busType="+types);  */
			$.ajax({
				url : ctx + '/clear/v03/empWorkRegister/changeGroup?type=' + types,
				type : 'post',
				dataType : 'json',
				data : {
					param : JSON.stringify(types)
				},
				success : function(serverResult, textStatus) {
				 	 /* for(var i=0;i<data.length;i++){
						var option = "<option value="+data[i].id+">"+data[i].groupName+"</option>";
						$("#clearGroup").append(option);
					} */
					/* 	$.each(data,function(index,value){
                        $("#clearGroup").append("<option value='"+value.id+"'>"+value.groupName+"</option>");
                    })  */ 
                    $('#clearGroup').select2({
                    	containerCss:{width:'163px',display:'inline-block'},
						data:{ results: serverResult, text: 'label' },
						formatSelection: format,
						formatResult: format 
                    });
                    //$('#clearGroup').select2("val",serverResult.id);
                    
                   // $('#groupDiv').val("");
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alert('您已登录超时或在其他地点登录，请重新登录！');
					top.location = "${ctx}";
					return false;
				}
			});
			
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) {
			return item.label;
		}
		
		function getClearGroupA(type) {
			var date = $("#operateDate").val();
			var busType = $("#busType").val();
			checkSession();
			$("#clearGroupDiv").load(
					"${ctx}/clear/v03/empWorkRegister/getClearGroup?clearGroupId="
							+ type+"&date="+date+"&busType="+busType); 
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
		<!-- 员工工作量登记 -->
		<li class="active"><a href="${ctx}/clear/v03/empWorkRegister/form"><spring:message code="clear.empWorkRegister.register" /></a></li>
		<shiro:hasPermission name="clear:v03:empWorkRegister:view">
		<!-- 员工工作量登记列表 -->
		<li><a href="${ctx}/clear/v03/empWorkRegister/"><spring:message code="clear.empWorkRegister.list" /></a></li>
		</shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clTaskMain" action=""
		method="post" class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="totalCount" id="totalCount" />
		<input type="hidden" id="planType" name="planType" value="01" />
		<input type="hidden" id="planTypes"  value="${clTaskMain.planType}" />
		<sys:message content="${message}" />

		<div class="span6">
			<div class="row">
				<!--登记时间 -->
				<div class="control-group">
					<label class="control-label"><spring:message code='clear.register.date' />：</label>
					<div class="controls" >
						<input id="operateDate" name="operateDate" type="text" readonly="readonly" maxlength="20" style="width:150px;" class="input-input-large Wdate required"
							value="<fmt:formatDate value="${clTaskMain.operateDate}" pattern="yyyy-MM-dd"/>"
							onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,oncleared:function(){changeGroup();},onpicked:function(){changeGroup();},maxDate:'%y-%M-%d'});"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group item" style="margin-left: 80px;">
					<!-- 业务类型 -->
					<label><spring:message code='clear.task.business.type' />：</label>
					<div style="margin-left: 100px; margin-top: -20px">
						<form:select path="busType" id="busType"
							class="input-medium required"
							onchange="changeGroup(this.options[this.selectedIndex].value,null)">
							<form:option selected="selected" value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${fns:getFilterDictList('clear_businesstype',true,'08,09')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>

				<div class="control-group item" style="margin-left: 80px;">
					<!-- 清分/复点组 -->
					<label style="margin-left: -20px;"><spring:message code='clear.empWorkRegister.clarifyAfterPointGroup' />：</label>
					<div style="margin-left: 100px; margin-top: -20px" id="groupDiv">
							<%-- <form:options items="${sto:getClearingGroupName('0','09')}"
								itemLabel="groupName" itemValue="id" htmlEscape="false"
								id="clearGroupId" /> --%>
						<%-- <form:select path="clearGroup" id="clearGroup" class="input-medium required" 
						onchange="getClearGroupA(this.options[this.selectedIndex].value)"><!-- this.options[this.selectedIndex].value -->
							<form:option value="" selected="selected"><spring:message code="common.select" /></form:option>
						</form:select> --%>
						<label>
						<form:input id ="clearGroup" type="hidden" path="clearGroup" onchange="getClearGroupA(value)"/>
						</label><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>

				<div class="control-group item" style="margin-left: 80px;">
				<!-- 面值 -->
					<label style="margin-left: 32px;"><spring:message code='common.denomination' />：</label>
					<div style="margin-left: 100px; margin-top: -20px">
						<form:select path="denomination" id="denomination"
							class="input-medium required" onchange="formClear()">
							<form:options items="${sto:getGoodDictListWithFg('cnypden')}"
								itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select><span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<!-- 物品明细列表（隐藏） -->
					<input type="hidden" id="currency" name="currency" value="101" /> 
					<input type="hidden" id="classification" name="classification" value="02" />
					<input type="hidden" id="sets" name="sets" value="5" /> 
					<input type="hidden" id="cash" name="cash" value="1" /> 
					<input type="hidden" id="unit" name="unit" value="101" />

				<div class="span9">
					<div class="row">
						<div class="control-group item" style=" margin-left: -10px;margin-top: 10px">
							<!-- 分配数量(捆) -->
							<label  style="margin-left: 10px;" class="control-label"><spring:message
									code="clear.task.distributionAmount" />(<spring:message code='clear.public.bundle' />)：</label>
							<div class="controls" style="margin-left: 190px;">
								<form:input id="distributionAmount"
									onkeyup="value=value.replace(/[^0-9|^\\-]/g,'')"
									path="distributionAmount" htmlEscape="false" maxlength="3"
									class="" style="width:150px;" /><span class="help-inline"><font color="red">*</font> </span>
							</div>
		
							<!-- 批量分配 -->
							<div class="controls" style="margin-left: 450px; margin-top: -30px">
								<input id="add" class="btn btn-primary" type="button" value="<spring:message code='clear.task.distribution'/>" onclick="changeAllSum()" /> 
								<input id="reset" class="btn btn-primary required" type="button" value="<spring:message code='clear.public.reset'/>" onclick="formClear()" />
							</div>
						</div>
						
						<!-- 总计 -->
						<div id="dayTotal" class="control-group item" style="margin-top: 25px"></div>
						
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
						<!-- 获取清分组列表 -->
						<div class="control-group item"
							style="overflow-y: auto; height: 350px; width: 1200px; margin-top: 0px "
							id="clearGroupDiv">
						</div>
					</div>
				</div>
			</div>
		</div>
				
		<div class="form-actions" style="clear:both;width:100%;">
			<!-- 保存 -->
			<input id="btnSubmit"  style="margin-left: 1000px"  class="btn btn-primary" type="button" value="<spring:message code='common.commit'/>" />&nbsp; 
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/clear/v03/empWorkRegister/back'" />
		</div>
	</form:form>
</body>
</html>