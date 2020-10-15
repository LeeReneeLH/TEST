<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="sys.property.title"/></title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			//展开级数2级
			$("#treeTable").treeTable({expandLevel :2}).show();
			<fmt:formatDate value="${menu.updateDate}" pattern="yyyyMMddHHmmssSSSSSS"/>
		});
		/* 添加下级参数或分组 */
    	 function addChildProperties(propertyKey,configType,id) {
    		var content = "iframe:${ctx}/sys/dbConfig/DbConfigPropertyController/form?propertyKey="+encodeURIComponent(propertyKey)+"&configType="+encodeURIComponent(configType)+"&id="+encodeURIComponent(id);
    		top.$.jBox.open(
    				content,
    				"<spring:message code='sys.property.jboxName'/>", 700, 500, {
    					buttons : {
    						//确认
    						"<spring:message code='common.confirm' />" : "ok",
    						//关闭
    						"<spring:message code='common.close' />" : true 
    					},
    					submit : function(v, h, f) {
    						if (v == "ok") {
    							var contentWindow = h.find("iframe")[0].contentWindow;
    							// 父级Id
    							var parentId= contentWindow.document.getElementById("parentId").value;
    							// 参数的键
    							var propertyKey = contentWindow.document.getElementById("propertyKey").value;
    							// 参数的值
    							var propertyValue = contentWindow.document.getElementById("value").value;
    							//备注
    							var remark = contentWindow.document.getElementById("remark").value;
    							//操作类型
    							var configType = contentWindow.document.getElementById("configType").value;
    							//单选按钮
    							var value = contentWindow.document.getElementById("key");
    							//参数的更新时间
    							var strUpdateDate = contentWindow.document.getElementById("strUpdateDate").value;
    							//参数的父类Ids
    							var parentIds = contentWindow.document.getElementById("parentIds").value;
    							
    							if(propertyKey == null || propertyKey == ""){
    								//参数名称不能为空
    								alert("<spring:message code='sys.property.keyIsNotNull'/>");
    								return false;
    							}
    							
    							if(value != null){
    								if(value.checked){
        								if(propertyValue == null || propertyValue == ""){
        									//参数值不能为空
        									alert("<spring:message code='sys.property.valueIsNotNull'/>");
        									return false;
        								}
        							}
    							}
    							
    							var url = "${ctx}/sys/dbConfig/DbConfigPropertyController/save";
    							//将参数值设置到表单中
    							$("#parentId").val(parentId);
    							$("#propertyKey").val(propertyKey);
    							$("#propertyValue").val(propertyValue);
    							$("#remark").val(remark);
    							$("#configType").val(configType);
    							$("#strUpdateDate").val(strUpdateDate);
    							$("#parentIds").val(parentIds);
    							$("#updateForm").attr("action", url);
    								$("#updateForm").submit();
    								return true;
    						}
    					},
    					loaded : function(h) {
    						$(".jbox-content", top.document).css(
    								"overflow-y", "hidden");
    					}
    		}); 
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/dbConfig/DbConfigPropertyController/list"><spring:message code='sys.property.list'/></a></li>
	</ul>
	<sys:message content="${message}"/>
	<!-- jbox窗口提交 -->
	<form:form id="updateForm" modelAttribute="parentDbConfig" action="" method="post" >
		<form:hidden id ="parentId" path="parentId"/>
		<form:hidden id ="propertyKey" path="propertyKey"/>
		<form:hidden id ="propertyValue" path="propertyValue"/>
		<form:hidden id ="remark" path="remark"/>
		<form:hidden id ="configType" path="configType"/>
		<form:hidden id ="strUpdateDate" path="strUpdateDate"/>
		<form:hidden id ="parentIds" path="parentIds"/>
	</form:form>
	<form id="listForm" method="post">
		<table id="treeTable" class="table table-hover">
			<thead><tr><th><spring:message code='sys.property.key'/></th><th><spring:message code='sys.property.value'/></th><th><spring:message code='sys.property.remark'/></th><th><spring:message code='sys.property.operate'/></th></tr></thead>
			<tbody><c:forEach items="${propertiesList}" var="menu">
				<tr id="${menu.id}" pId="${menu.parent.id ne '1'?menu.parent.id:'0'}">
					<td nowrap><a href="#" onclick = "addChildProperties('${menu.propertyKey}','update','${menu.id}')">${menu.propertyKey}</a></td>
					<td nowrap>${menu.propertyValue}</td>
					<td nowrap title="${menu.remark}">${fns:abbr(menu.remark,15)}</td>
					<td nowrap>
						<a href="#" onclick = "addChildProperties('${menu.propertyKey}','update','${menu.id}')" title='<spring:message code="sys.property.update"/>'><i class="fa fa-edit text-green fa-lg"></i></a>
						<!-- 判断参数的value是否为空，如果为空则显示添加按钮 -->
						<c:choose>
							<c:when test="${empty(menu.propertyValue)}">
								<a href="${ctx}/sys/dbConfig/DbConfigPropertyController/deleteGroup?propertyKey=${menu.propertyKey}&id=${menu.id}&strUpdateDate=${menu.strUpdateDate}" onclick="return confirmx('<spring:message code="sys.property.deleteGroup"/>', this.href)" title='<spring:message code="sys.property.delete"/>'><i class="fa fa-trash-o text-red fa-lg"></i></a>
								<a href="#" title='<spring:message code="sys.property.addChildren"/>' onclick="addChildProperties('${menu.propertyKey}','insert','${menu.id}')"><i class="fa fa-plus-square-o fa-lg"></i></a> 
							</c:when>
							<c:otherwise>
								<a href="${ctx}/sys/dbConfig/DbConfigPropertyController/delete?propertyKey=${menu.propertyKey}&id=${menu.id}&strUpdateDate=${menu.strUpdateDate}" onclick="return confirmx('<spring:message code="sys.property.deleteProperty"/>', this.href)" title='<spring:message code="sys.property.delete"/>'><i class="fa fa-trash-o text-red fa-lg"></i></a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach></tbody>
		</table>
	 </form>
</body>
</html>