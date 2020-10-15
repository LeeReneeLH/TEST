<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<%-- 追加机构查询的分页功能引入common.js  xp start --%>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<%-- 追加机构查询的分页功能引入common.js  xp end --%>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(page.list)}, pid = "${not empty office.id ? office.id : '0'}";
			addRow("#treeTableList", tpl, data, pid);
//			$("#treeTable").treeTable({expandLevel : 5});
			// 页面重新加载时刷新树
			<%-- 追加机构查询的分页功能,机构列表刷新时,同步刷新ztree的功能  xp start --%>
			//window.parent.refreshTree();
			<%-- 追加机构查询的分页功能,机构列表刷新时,同步刷新ztree的功能  xp end --%>
			$("#hrefid01").on("click",function(){
				//alert("aaaaa");
				
			});
		});
		function addRow(list, tpl, data, pid){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				//if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type),
							//trade: getDictLabel(${fns:toJson(fns:getDictList('sys_office_trade'))}, row.tradeFlag,'行内')
						}, pid: (i==0)?"":row.parentId, row: row
					}));
					//addRow(list, tpl, data, row.id);
				//}
			}
			
			
		}
		
		//删除机构时验证当前机构和子机构下是否有有效用户
		function checkOffice(officeId) {
			
			var url="${ctx}/sys/office/deleteCheck";
			//异步验证
			$.ajax({
				url:url,
				type:"post",
				
				data:'id='+officeId,
				dataType:"json",
				async:false,
				success:function(data){
					
					if(data == "false"){
						//要删除该机构及所有子机构项吗？
						confirmx("<spring:message code='message.I2021'/>","${ctx}/sys/office/delete?id="+officeId);
						
					} else {
						//当前机构下拥有有效用户，不能删除机构
						alertx("<spring:message code='message.I2022'/>");
					}
				},
				error:function(){
					//系统内部异常，请稍后再试或与系统管理员联系
					alertx("<spring:message code='message.E0101'/>")
				}
				
			});
		}
	 	function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        };
	</script>
<style>
	.ul-form{margin:0;display:table;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/office/list">机构列表</a></li>
		<shiro:hasPermission name="sys:office:edit"><li><a href="${ctx}/sys/office/form?parent.id=${office.id}">机构添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="office" action="${ctx}/sys/office/search" method="post" class="breadcrumb form-search ">
		<!-- 追加机构查询的分页功能  xp start -->
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 追加机构查询的分页功能  xp end -->
		<!-- 追加机构区分分页查询  0：不带模糊查询分页   1：带模糊查询分页 hzy start -->
		<input id="searchType" name="searchType" type="hidden"  value="${page.searchType}"/>
		<!-- 追加机构区分分页查询  0：不带模糊查询分页   1：带模糊查询分页   hzy end -->
		<div class="row search-flex">
			<div><label>机构名称：</label><form:input path="name" htmlEscape="false" maxlength="15" class="input-small"/></div>
			<div><label>机构号：</label><form:input path="code" htmlEscape="false" maxlength="15" class="input-small"/></div>
		<!-- 追加机构类型筛选条件  ZXK start -->
		<div>
			<label>机构类型：</label>
			<form:select path="type" class="input-medium required"
				id="selectStatus">
				<option value=""><spring:message code="common.select" /></option>
				<form:options items="${fns:getDictList('sys_office_type')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
			</div>
		<!-- 追加机构类型筛选条件  ZXK end -->	
		
		<!-- 追加机构通过左侧树查询的分页显示  hzy start -->
		<div>
		<form:input path="id"  htmlEscape="false" type="hidden" maxlength="15" class="input-medium"/> 
		</div>
		<!-- 追加机构通过左侧树查询的分页显示  hzy end -->	 
			<%-- <li><label>机构分类：</label>
				<form:select path="tradeFlag" id="tradeFlag" class="input-medium">
				<form:option value="">全部</form:option>
				<form:options items="${fns:getDictList('sys_office_trade')}" 
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</li> --%>
			&nbsp;&nbsp;
			<div><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"  formaction="${ctx}/sys/office/list"/>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="treeTable" class="table table-hover">
		<thead>
			<tr>
				<th class="sort-column a.name">机构名称</th>
				<th class="sort-column a.code">机构编号</th>
				<th class="sort-column a.id"><spring:message code="common.systemOfficeId" /></th>
				<th class="sort-column a.type">机构类型</th>
				<!-- <th>机构分类</th> -->
				<th class="sort-column a.remarks">备注</th>
				<shiro:hasPermission name="sys:office:edit">
					<th>操作</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>

	</table>
	</div>
	<div class="pagination">${page}</div>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/sys/office/form?id={{row.id}}">{{row.name}}</a></td>
			<td>{{row.code}}</td>
			<td>{{row.id}}</td>
			<td>{{dict.type}}</td>
			<!-- <td>{{dict.trade}}</td> -->
			<td>{{row.remarks}}</td>
			<shiro:hasPermission name="sys:office:edit"><td>
				<a href="${ctx}/sys/office/form?id={{row.id}}" title="编辑"><i class="fa fa-edit text-green fa-lg"></i></a>
				
				<a href="#" onclick="checkOffice({{row.id}})"  title="删除" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
				<a href="${ctx}/sys/office/form?parent.id={{row.id}}" title="添加下级机构" style="margin-left:10px;"><i class="fa fa-plus-square-o fa-lg"></i></a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>