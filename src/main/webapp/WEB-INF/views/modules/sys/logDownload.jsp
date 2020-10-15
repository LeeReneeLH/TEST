<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title><spring:message code="door.equipment.title"/></title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/log/">日志列表</a></li>
    <li class="active"><a href="${ctx}/sys/log/download">日志下载</a></li>
</ul>
<form:form id="searchForm" action="${ctx}/sys/log/download/" method="post"
           class="breadcrumb form-search ">
    <%-- 查询日期 --%>
    <label><spring:message code="common.startDate"/>：</label>
    <input id="dataTime"
           name="dataTime"
           type="text"
           readonly="readonly"
           maxlength="20"
           class="input-small Wdate createTime"
           onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'});"/>
    <%-- 下载 --%>
    <select name="logType">
        <option value="whole">全量</option>
        <option value="accountChecking">对账</option>
    </select>
    <input id="btnSubmit" class="btn btn-primary" type="submit" value="下载"/>
</form:form>
<sys:message content="${message}" />
</body>
</html>