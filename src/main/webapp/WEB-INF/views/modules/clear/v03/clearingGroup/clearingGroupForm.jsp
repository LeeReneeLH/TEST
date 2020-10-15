<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 清分组管理 -->
	<title><spring:message code="clear.clearingGroup.title"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#groupName").focus();<input id="oldClearingGroupName" name="oldClearingGroupName" type="hidden" value="${clearingGroup.groupName}">
			$("#inputForm").validate({
				rules: {
					groupNo: {remote: {url:"${ctx}/clear/v03/clearingGroup/checkNo?oldNo=" + encodeURIComponent('${clearingGroup.groupNo}'),cache:false}},
					groupName: {remote: {url:"${ctx}/clear/v03/clearingGroup/checkName?oldName=" + encodeURIComponent('${clearingGroup.groupName}'),cache:false}}
				},
				messages: {
					groupNo: {remote: "分组编号已存在"},
					groupName: {remote: "分组名称已存在"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorPlacement: function(error, element) {
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			//提交
			$('#btnSubmit').on('click',function(){
				//提交，循环当前请分组人员
				var i=0;
				$(".groupUserList").each(function(){
					$(this).prop("checked","checked");					
					$(this).attr("name","userListForm["+i+"].id");
					i++;
				});
				if (i==0) {
					alertx("未分配人员");
					return false;
				}
			});
			//添加分配
			$('#btnAdd').on('click',function(){
				//循环未分配用户
				$(".groupUserListSelect").each(function(){
					//判断是否选中
					if ($(this).prop("checked")==true) {
						var span=$('#'+$(this).val()).html();
						//移除选中
						$('#'+$(this).val()).remove();
						//改变class属性
						span=span.replace("groupUserListSelect","groupUserList");
						span="<span id=\""+$(this).val()+"\"onclick=\"select('"+$(this).val()+"')\" ondblclick=\"selectD('"+$(this).val()+"')\">"+span+"</span>";
						//添加节点
						$("#div1").append(span);
					}
				});
				return false;
			});
			//添加所有
			$('#btnAddAll').on('click',function(){
				//循环未分配用户
				$(".groupUserListSelect").each(function(){
					var span=$('#'+$(this).val()).html();
					//移除选中
					$('#'+$(this).val()).remove();
					//改变class属性
					span=span.replace("groupUserListSelect","groupUserList");
					span="<span id=\""+$(this).val()+"\"onclick=\"select('"+$(this).val()+"')\" ondblclick=\"selectD('"+$(this).val()+"')\">"+span+"</span>";
					//添加节点
					$("#div1").append(span);
				});
				return false;
			});
			//移除分配
			$('#btnRemove').on('click',function(){
				//循环当前分配用户
				$(".groupUserList").each(function(){
					//判断是否选中
					if ($(this).prop("checked")==true) {
						var span=$('#'+$(this).val()).html();
						//移除选中
						$('#'+$(this).val()).remove();
						//改变class属性
						span=span.replace("groupUserList","groupUserListSelect");
						span="<span id=\""+$(this).val()+"\"onclick=\"select('"+$(this).val()+"')\" ondblclick=\"selectD('"+$(this).val()+"')\">"+span+"</span>";
						//添加节点
						$("#div2").append(span);
					}
				});
				return false;
			});
			//移除所有
			$('#btnRemoveAll').on('click',function(){
				//循环当前分配用户
				$(".groupUserList").each(function(){
					var span=$('#'+$(this).val()).html();
					//移除选中
					$('#'+$(this).val()).remove();
					//改变class属性
					span=span.replace("groupUserList","groupUserListSelect");
					span="<span id=\""+$(this).val()+"\"onclick=\"select('"+$(this).val()+"')\" ondblclick=\"selectD('"+$(this).val()+"')\">"+span+"</span>";
					//添加节点
					$("#div2").append(span);
				});
				return false;
			});
		});		
		//选中事件
		function select(id){
			//判断是否选中
			if ($("#"+id+"ipt").prop("checked")==true) {
				//改为未选中状态
				$("#"+id+"ipt").prop("checked","");
				$("#"+id).attr("style","width:100px;display:block");
			}else{
				//改为选中状态
				$("#"+id+"ipt").prop("checked","checked");
				$("#"+id).attr("style","background-color:lightskyblue;width:100px;display:block");
			}
		}
		//双击事件
		function selectD(id){
			var span=$("#"+id).html();
			//判断双击的是否被分配
			if (span.indexOf("groupUserListSelect")>0) {
				//未分配改为分配
				$('#'+id).remove();
				span=span.replace("groupUserListSelect","groupUserList");
				span="<span id=\""+id+"\"onclick=\"select('"+id+"')\" ondblclick=\"selectD('"+id+"')\">"+span+"</span>";
				$("#div1").append(span);
			}else{
				//分配改为未分配
				$('#'+id).remove();
				span=span.replace("groupUserList","groupUserListSelect");
				span="<span id=\""+id+"\"onclick=\"select('"+id+"')\" ondblclick=\"selectD('"+id+"')\">"+span+"</span>";
				$("#div2").append(span);
			}
		}
	</script>
 	<style>
         table thead tr{
            height: 50px;
            line-height: 50px;
        }
         table tr th:first-child,table tr td:first-child {/*设置table左边边框*/
            border-left: 2px solid #eaeaea;
        }
        table tr th:last-child,table tr td:last-child {/*设置table右边边框*/
            border-right: 2px solid #eaeaea;
        } 
        table tr td:first-child,
        table tr td:nth-child(2),
        table tr td:nth-child(3),
        table tr td:last-child{/*设置table表格每列底部边框*/
            border-bottom: 2px solid #eaeaea;
        }
        table tr:first-child th:first-child {
            border-top-left-radius: 12px;
        }
        table tr:first-child th:last-child {
            border-top-right-radius: 12px;
        }
        table tr:last-child td:first-child {
            border-bottom-left-radius: 12px;
        }

        table tr:last-child td:last-child {
            border-bottom-right-radius: 12px;
        }
    </style>	
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 清分组管理列表 -->
		<li><a href="${ctx}/clear/v03/clearingGroup/"><spring:message code="clear.clearingGroup.list"/></a></li>
		<li class="active"><a href="${ctx}/clear/v03/clearingGroup/form?id=${clearingGroup.id}">
			<c:choose>
				<c:when test="${not empty clearingGroup.id}"><spring:message code="clear.clearingGroup.modify"/></c:when>
				<c:otherwise><spring:message code="clear.clearingGroup.register"/></c:otherwise>
			</c:choose>
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="clearingGroup" action="${ctx}/clear/v03/clearingGroup/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="delFlag"/>
		<sys:message content="${message}"/>		
		<div style="float:left">
			<!-- 分组编号-->
			<div class="control-group">
				<label class="control-label"><spring:message code='clear.clearingGroup.numbering'/>：</label>
				<div class="controls">
					<form:input path="groupNo" htmlEscape="false" maxlength="10" class="required digits" onkeyup="value=value.replace(/[^0-9]/g,'')"/><span class="help-inline">
					<font color="red">*</font></span>
				</div>
			</div>
			<!-- 分组名称-->
			<div class="control-group">
				<label class="control-label"><spring:message code='clear.clearingGroup.appellation'/>：</label>
				<div class="controls">
					<form:input path="groupName" htmlEscape="false" maxlength="10" class="required"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<!-- 业务类型-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.business.type"/>：</label>
				<div class="controls">
					<form:select path="groupType" class="input-large required">
						<form:option value=""><spring:message code="common.select" /></form:option>				
						<form:options items="${fns:getFilterDictList('clear_businesstype',true,'08,09')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select><span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<!-- 工位类型-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.positionType"/>：</label>
				<div class="controls">
					<form:select path="workingPositionType" class="input-large required">
						<form:option value=""><spring:message code="common.select" /></form:option>				
						<form:options items="${fns:getDictList('clear_working_position_type')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
					</form:select><span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<!-- 备注信息-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clearingGroup.noteInformation"/>：</label>
				<div class="controls">
					<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="80" class="input-xxlarge "/>
				</div>
			</div>
		</div>
		<div style="float:left;margin-left:60px;">
			<table>
				<thead>
					<tr>
						<!-- 未清分组人员 -->
						<th align="center" width="150px"><spring:message code="clear.clearingGroup.unclearedGroupMembers"/></th>
						<!-- 操作 -->
						<th align="center" width="100px"><spring:message code='common.operation'/></th>
						<!-- 当前清分组人员 -->
						<th align="center" width="150px"><spring:message code='clear.clearingGroup.nowGroupMembers'/></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td align="center" width="150px" height="200px">
							<div id="div2" style="height:200px;overflow: auto">
								<c:forEach items="${clearingGroup.userListSelect}" var="user">
									<span style='width:100px;display:block' id="${user.id}" onclick="select('${user.id}')" ondblclick="selectD('${user.id}')">
										<input style="display:none;" id="${user.id}ipt" class="groupUserListSelect" type="checkbox" value="${user.id}"/>${user.name}<br>
									</span>
								</c:forEach>
							</div>
						</td>
						<td align="center" width="100px" height="200px">
							<div>
								<!-- 添加选中 -->
								<button id="btnAdd" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.addSelected'/>">&gt;</button><br>
								<!-- 添加所有 -->
								<button id="btnAddAll" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.addAllSelected'/>">&gt;&gt;</button><br>
								<!-- 移除选中 -->
								<button id="btnRemove" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.removeSelected'/>">&lt;</button><br>
								<!-- 移除所有 -->
								<button id="btnRemoveAll" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.removeAllSelected'/>">&lt;&lt;</button>
							</div>
						</td>
						<td align="center" width="150px" height="200px">
							<div id="div1" style="height:200px;overflow: auto">
								<c:forEach items="${clearingGroup.clearingGroupDetailList}" var="clearingGroupDetail">
									<span style='width:100px;display:block' id="${clearingGroupDetail.user.id}" onclick="select('${clearingGroupDetail.user.id}')" ondblclick="selectD('${clearingGroupDetail.user.id}')">
										<input style="display:none;" id="${clearingGroupDetail.user.id}ipt" class="groupUserList" type="checkbox" value="${clearingGroupDetail.user.id}"/>${clearingGroupDetail.userName}<br>
									</span>
								</c:forEach>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="form-actions" style="clear:both;width:100%;">
			<shiro:hasPermission name="group:clearingGroup:edit">
				<!-- 保存 -->
				<input id="btnSubmit" class="btn btn-primary" id="btnSubmit" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;
			</shiro:hasPermission>
			<!-- 返回 -->
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/clear/v03/clearingGroup/back'"/>
		</div>
	</form:form>
</body>
</html>