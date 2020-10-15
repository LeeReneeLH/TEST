<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		// 下载点击左上角复选框，选择全部信息
        $("#allDownLoadCheckbox").click(function () {
        	
        	if ($(this).attr("checked") == "checked") {
        		$(this).attr("checked", true);
        	} else {
        		$(this).attr("checked", false);
        	}
        	var checkStatus = $(this).attr("checked");
            $("[id = chkPrintItem]:checkbox").each(function () {
                $(this).attr("checked", checkStatus == "checked" ? true : false);
            });
        });
	});
	
	// 下载模版
	function downLoad () {
		var result = new Array();
		$("[id = chkPrintItem]:checkbox").each(function () {
		    if ($(this).attr("checked") == "checked") {
		        result.push($(this).attr("value"));
		    }
		});
		
		var atmNos = result.join(",");
		// 判断ATM机编号是否为空
		if (atmNos == '') {
        	//[提示]：请选择需要下载的ATM机！
        	alertx("<spring:message code='message.I4039' />");
			return;
		}
		var url = "${ctx}/atm/v01/atmPlanInfo/template?atmNos=" + atmNos;
		document.location.href = url;
	}
</script>
</head>
<body>
	<table id="contentTable" class="table table table-hover">
		<thead>
			<tr>
				<!-- 全下载 -->
				<th style="text-align: center;">
					<input type="checkbox" id="allDownLoadCheckbox"/>
				<!-- 终端号 -->
				<th><spring:message code="label.atm.add.terminal"/></th>
				<!-- 柜员号 -->
				<th><spring:message code="label.atm.add.teller"/></th>
				<!-- 网点名称 -->
				<th><spring:message code="label.atm.add.plan.office.name"/></th>
				<!-- 设备型号编号 -->
				<th><spring:message code="label.atm.add.plan.atm.typename"/><spring:message code="clear.clearingGroup.numbering"/></th>
				<!-- 设备型号 -->
				<th><spring:message code="label.atm.add.plan.atm.typename"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${dataList}" var="downLoad" varStatus="status">
			<c:if test="${not empty dataList[0].atmNo}">
				<tr>
					<!-- 多选框 -->
					<td style="text-align: center">
							<input type="checkbox" id="chkPrintItem" value="${downLoad.atmNo}"/>
					</td>
					<!-- 终端号 -->
					<td>${downLoad.atmNo}</td>
					<!-- 柜员号 -->
					<td>${downLoad.atmAccount}</td>
					<!-- 网点名称 -->
					<td>${downLoad.atmAddress}</td>
					<!-- 设备型号编号 -->
					<td>${downLoad.atmTypeNo}</td>
					<!-- 设备型号 -->
					<td>${downLoad.atmTypeName}</td>
				</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>