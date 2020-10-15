<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>${officeType}ID列表</title>
    <meta name="decorator" content="default"/>

</head>
<body>

<div class="row" style="margin-top:15px;margin-left:10px;">
    <table  id="contentTable" class="table table-hover">
        <thead>
        <tr class="bg-light-blue disabled color-palette">
           <%-- 序号--%>
            <th>序号</th> 
            <%-- 商户名称--%>
            <th>${officeType}名称</th>
            <%-- 商户ID--%>
            <th>${officeType}ID</th>
            
        </tr>
        </thead>
        <tbody>
        <c:forEach items = "${officeIdList}" var = "office" varStatus="status">
            <tr>
            	<td style="text-align:left;" >${status.index+1}</td>
                <td style="text-align:left;">${office.name}</td>
                <td style="text-align:left;">${office.id}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
   
</div>
</body>
</html>