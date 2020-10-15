<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>${officeType}ID明细</title>
    <meta name="decorator" content="default"/>
	<script type="text/javascript">
	function subForm(){
		var row = '';
        $("tbody tr").each(function(){
            if($(this).children('td').eq(0).children('input:checkbox').attr('checked')){
            	row +=$(this).children('td').eq(0).children('input:checkbox').val()+',';
            }
            });
       row = row.substring(0, row.lastIndexOf(','));
		$("#id").val(row);
		var form_submit = document.getElementById('searchForm');
		form_submit.submit();
	}
	 $(document).ready(function() {
		 $("#name").bind('keydown', function (e){if(e.which == 13){subForm();}});		 
	 });
	</script>
</head>
<body>
<form:form id="searchForm" modelAttribute="office" action="${ctx}/sys/quartz/officeSearch"  method="post" class="breadcrumb form-search ">
		<ul class="ul-form">
		
			<li><label>${officeType}名称：</label><form:input path="name" htmlEscape="false" maxlength="15" class="input-medium"/>
			<form:hidden path="id" htmlEscape="false"  class="input-medium"/>
			<form:hidden path="type" value="${officeType}" htmlEscape="false"  class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" onclick="subForm()" value="查询"/>
			</li>
		</ul>
	</form:form>
<div class="row" style="margin-top:15px;margin-left:10px;">
<sys:message content="${message}"/>
	
    <table  id="contentTable" class="table table-hover">
        <thead>
        <tr class="bg-light-blue disabled color-palette">
            <%-- 勾选框--%>
            <th>勾选框</th>
            <%-- 商户名称--%>
            <th>${officeType}名称</th>
            <%-- 商户ID--%>
            <th>${officeType}ID</th>
            
        </tr>
        </thead>
        <tbody>
        <c:forEach items = "${officeList}" var = "office1">
        <c:choose>
        	<c:when test="${empty office1.type}">
        	<tr hidden="true">
				<td style="text-align:left;" ><input type="checkbox" checked="checked" name="${office1.name}" value="${office1.id}"></td>
                <td style="text-align:left;">${office1.name}</td>
                <td style="text-align:left;">${office1.id}</td>
            </tr>
        	</c:when>
        	<c:otherwise>
        	<tr>
            <c:choose>
            <c:when test="${empty office1.parentIds}">
				<td style="text-align:left;" ><input type="checkbox" checked="checked" name="${office1.name}" value="${office1.id}"></td>
			</c:when>
			<c:otherwise>
				<td style="text-align:left;" ><input type="checkbox"  name="${office1.name}" value="${office1.id}"></td>
			</c:otherwise>
			</c:choose>
                <td style="text-align:left;">${office1.name}</td>
                <td style="text-align:left;">${office1.id}</td>
            </tr>
        	</c:otherwise>
            </c:choose>
        </c:forEach>
        </tbody>
    </table>
    <script type="text/javascript">
    function callbackdata() {
    	var row = '';
        $("tbody tr").each(function(){
            if($(this).children('td').eq(0).children('input:checkbox').attr('checked')){
            	row +=$(this).children('td').eq(0).children('input:checkbox').val()+',';
            }
            });
       row = row.substring(0, row.lastIndexOf(','));
       
        return row;
    } 
    </script>
</div>
</body>
</html>