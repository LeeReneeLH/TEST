<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>面值明细</title>
    <meta name="decorator" content="default"/>

</head>
<body>
<div class="row" style="margin-top:15px;margin-left:10px;">
    <table  id="contentTable" class="table table-hover">
        <thead>
        <tr class="bg-light-blue disabled color-palette">
            <%-- 序号--%>
            <th>序号</th>
            <%-- 面值--%>
            <th>面值</th>
            <%-- 张数--%>
            <th>张数</th>
            <%-- 金额--%>
            <th>金额</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items = "${amountList}" var = "amount">
            <tr>
                <td style="text-align:left;">${amount.rowNo}</td>
                <td style="text-align:left;">${amount.parValue}</td>
                <td style="text-align:left;">${amount.countZhang}</td>
                <td style="text-align:left;"><fmt:formatNumber value="${amount.detailAmount}" type="currency" pattern="#,##0.00"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>