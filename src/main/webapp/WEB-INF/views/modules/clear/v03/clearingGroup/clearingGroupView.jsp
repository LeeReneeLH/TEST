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
		});
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
		<!-- 清分组管理查看 -->
		<li class="active"><a href="${ctx}/clear/v03/clearingGroup/view?id=${clearingGroup.id}"><spring:message code="clear.clearingGroup.view"/></a></li>
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
					<form:input path="groupNo" htmlEscape="false" readOnly="true" maxlength="20" class="required"/><span class="help-inline">
					<font color="red">*</font></span>
				</div>
			</div>
			<!-- 分组名称-->
			<div class="control-group">
				<label class="control-label"><spring:message code='clear.clearingGroup.appellation'/>：</label>
				<div class="controls">
					<form:input path="groupName" htmlEscape="false" readOnly="true" maxlength="20" class="required"/>
					<span class="help-inline"><font color="red">*</font></span>
				</div>
			</div>
			<!-- 业务类型-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.business.type"/>：</label>
				<div class="controls">
					<form:input path="groupType" value="${fns:getDictLabel(clearingGroup.groupType, 'clear_businesstype', '')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 工位类型-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.task.positionType"/>：</label>
				<div class="controls">
					<form:input path="workingPositionType" value="${fns:getDictLabel(clearingGroup.workingPositionType, 'clear_working_position_type', '')}" readOnly="true" htmlEscape="false" class="required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<!-- 备注信息-->
			<div class="control-group">
				<label class="control-label"><spring:message code="clear.clearingGroup.noteInformation"/>：</label>
				<div class="controls">
					<form:textarea path="remarks" readOnly="true" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge "/>
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
								<button id="btnAdd" onclick="return false;" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.addSelected'/>">&gt;</button><br>
								<!-- 添加所有 -->
								<button id="btnAddAll" onclick="return false;" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.addAllSelected'/>">&gt;&gt;</button><br>
								<!-- 移除选中 -->
								<button id="btnRemove" onclick="return false;" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.removeSelected'/>">&lt;</button><br>
								<!-- 移除所有 -->
								<button id="btnRemoveAll" onclick="return false;" style="text-align:center; margin-bottom:10px; width:60px;" title="<spring:message code='clear.clearingGroup.removeAllSelected'/>">&lt;&lt;</button>
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
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/clear/v03/clearingGroup/back'"/>
		</div>
	</form:form>
</body>
</html>