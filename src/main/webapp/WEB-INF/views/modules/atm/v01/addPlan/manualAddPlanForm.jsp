<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 刷新页面
			/* $("#cancel").click(function(){
				 window.location.reload();
				 
			}); */
			
			$("#inputForm").validate({
				submitHandler: function(form){
					// 获取表格 tr的长度(初始长度为2)
					var tableCheck = $('#contentTable tr').length;
					// 判断是否新增和填写加钞计划
					if(tableCheck == 2){
						alertx("请添加加钞计划信息！");
						return false;
					} else {
						loading('正在提交，请稍等...');
						form.submit();
					}
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
		// 动态添加tr
		function addRow(list, idx, tpl, row){
			$(list).append(Mustache.render(tpl, {
				idx: idx, delBtn: true, row: row
			}));
			$(list+idx).find("select").each(function(){
				$(this).val($(this).attr("data-value"));
			});
			$(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
				var ss = $(this).attr("data-value").split(',');
				for (var i=0; i<ss.length; i++){
					if($(this).val() == ss[i]){
						$(this).attr("checked","checked");
					}
				}
			});
		}
		// 动态删除tr
		function delRow(obj, prefix){
			var id = $(prefix+"_id");
			var delFlag = $(prefix+"_delFlag");
			if (id.val() == ""){
				$(obj).parent().parent().remove();
			}else if(delFlag.val() == "0"){
				delFlag.val("1");
				$(obj).html("&divide;").attr("title", "撤销删除");
				$(obj).parent().parent().addClass("error");
			}else if(delFlag.val() == "1"){
				delFlag.val("0");
				$(obj).html("&times;").attr("title", "删除");
				$(obj).parent().parent().removeClass("error");
			}
		}
		
		// 根据选择的atm机编号，加载atm机信息
		function changeAtm(atmNo,sum){
			$.ajax({
				url : ctx + '/atm/v01/atmPlanInfo/changeAtm?atmNo=' + atmNo,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					// 判断ATM机信息是否存在
					if(data.getAtmPlanList.length > 0){
						// 赋值柜员号
						$('#addPlanList'+sum+'_atmAccount').val(data.getAtmPlanList[0].atmAccount);
						// 赋值网点名称
						$('#addPlanList'+sum+'_atmAddress').val(data.getAtmPlanList[0].atmAddress);
						// 赋值设备型号编号
						$('#addPlanList'+sum+'_atmTypeNo').val(data.getAtmPlanList[0].atmTypeNo);
						// 赋值设备型号
						$('#addPlanList'+sum+'_atmTypeName').val(data.getAtmPlanList[0].atmTypeName);
					} else {
						// 赋值柜员号
						$('#addPlanList'+sum+'_atmAccount').val("");
						// 赋值网点名称
						$('#addPlanList'+sum+'_atmAddress').val("");
						// 赋值设备型号编号
						$('#addPlanList'+sum+'_atmTypeNo').val("");
						// 赋值设备型号
						$('#addPlanList'+sum+'_atmTypeName').val("");
					}
				},
				error : function(data, status, e) {
					
				}
			});
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/atm/v01/atmPlanInfo/importFile"><spring:message code='label.atm.addPlan.menu.01'/></a></li>
		<li><a href="${ctx}/atm/v01/atmPlanInfo/list"><spring:message code='label.atm.addPlan.menu.02'/></a></li>
		<li class="active"><a href="${ctx}/atm/v01/atmPlanInfo/addPlanForm"><spring:message code="label.atm.add.manuallyGenerated"/></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmPlanInfo" action="${ctx}/atm/v01/atmPlanInfo/manualSave" method="post" class="form-horizontal">
		<%-- <form:hidden path="id"/> --%>
		<sys:message content="${message}"/>		
			<div class="control-group">
				<!-- 手动生成加钞计划 -->
				<label class="control-label"><spring:message code="label.atm.add.manuallyGenerated"/>：</label>
				<div class="controls">
					<table id="contentTable" class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<th class="hide"></th>
								<!-- 终端号  -->
								<th><spring:message code="label.atm.add.terminal"/></th>
								<!-- 柜员号  -->
								<th><spring:message code="label.atm.add.teller"/></th>
								<!-- 网点名称  -->
								<th><spring:message code="label.atm.add.plan.office.name"/></th>
								<!-- 加钞金额(万元)  -->
								<th><spring:message code="label.atm.day.amount"/><spring:message code="label.atm.add.money.unit"/></th>
								<!-- 取款箱个数  -->
								<th><spring:message code="label.atm.add.plan.get.box.num"/></th>
								<!-- 设备型号编号  -->
								<th><spring:message code="label.atm.add.plan.atm.typename"/><spring:message code="clear.clearingGroup.numbering"/></th>
								<!-- 设备型号  -->
								<th><spring:message code="label.atm.add.plan.atm.typename"/></th>
								<!-- <th>逻辑删除</th> -->
								<th width="10">&nbsp;</th>
							</tr>
						</thead>
						<tbody id="addPlanList">
						</tbody>
						<tfoot>
							<tr><td colspan="9"><a href="javascript:" onclick="addRow('#addPlanList', addPlanRowIdx, addPlanTpl);addPlanRowIdx = addPlanRowIdx + 1;" class="btn">新增</a></td></tr>
						</tfoot>
					</table>
					<script type="text/template" id="addPlanTpl">//<!--
						<tr id="addPlanList{{idx}}">
							<td class="hide">
								<input id="addPlanList{{idx}}_id" name="addPlanList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
								<input id="addPlanList{{idx}}_delFlag" name="addPlanList[{{idx}}].delFlag" type="hidden" value="0"/>
							</td>
							<td>
								<select id="addPlanList{{idx}}_atmNo" name="addPlanList[{{idx}}].atmNo" data-value="{{row.atmNo}}" class="input-small required"
									onchange="changeAtm(this.value,{{idx}})">
									<option value=""><spring:message code="common.select" /></option>
									<c:forEach items="${dataList}" var="data">
										<option value="${data.atmNo}">${data.atmNo}</option>
									</c:forEach>
								</select>
								<span class="help-inline"><font color="red">*</font> </span>
							</td>
							<td>
								<input id="addPlanList{{idx}}_atmAccount" name="addPlanList[{{idx}}].atmAccount" readonly="readonly" type="text" value="{{row.tellerId}}" maxlength="60" class="input-small"/>
							</td>
							<td>
								<input id="addPlanList{{idx}}_atmAddress" name="addPlanList[{{idx}}].atmAddress" readonly="readonly" type="text" value="{{row.aofficeName}}" maxlength="60" class="input-small "/>
							</td>
							<td>
								<input id="addPlanList{{idx}}_addAmountStr" name="addPlanList[{{idx}}].addAmountStr" maxlength="5" type="text" value="{{row.addAmountStr}}" maxlength="60" class="input-small required digits"/>
								<span class="help-inline"><font color="red">*</font> </span>
							</td>
							<td>
								<input id="addPlanList{{idx}}_getBoxNumStr" name="addPlanList[{{idx}}].getBoxNumStr" maxlength="3" type="text" value="{{row.getBoxNumStr}}" maxlength="60" class="input-small required digits"/>
								<span class="help-inline"><font color="red">*</font> </span>
							</td>
							<td>
								<input id="addPlanList{{idx}}_atmTypeNo" name="addPlanList[{{idx}}].atmTypeNo" readonly="readonly" type="text" value="{{row.atmTypeNo}}" maxlength="60" class="input-small "/>
							</td>
							<td>
								<input id="addPlanList{{idx}}_atmTypeName" name="addPlanList[{{idx}}].atmTypeName" readonly="readonly" type="text" value="{{row.atmTypeName}}" maxlength="60" class="input-small "/>
							</td>
							<td class="text-center" width="10">
								{{#delBtn}}<span class="close" onclick="delRow(this, '#addPlanList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
							</td>
						</tr>//-->
					</script>
					<script type="text/javascript">
						var addPlanRowIdx = 0, addPlanTpl = $("#addPlanTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
						$(document).ready(function() {
							var data = ${fns:toJson(atmPlanInfo.addPlanList)};
							for (var i=0; i<data.length; i++){
								addRow('#addPlanList', addPlanRowIdx, addPlanTpl, data[i]);
								addPlanRowIdx = addPlanRowIdx + 1;
							}
						});
					</script>
				</div>
			</div>
		<div class="form-actions" style="width:100%;">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>&nbsp;
			<!-- <input id="cancel" class="btn btn-primary" type="button" value="刷 新"/> -->
		</div>
	</form:form>
</body>
</html>