<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='title.atm.addPlan.file.import'/></title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#uploadAddPlanFile").click(function(){
			$("#addPlanForm").submit();
		});
	});
	
	/* 下载模版选择ATM机    修改人：wxz 2017-12-6	*/
	function downLoadPlan(){
		var content = "iframe:${ctx}/atm/v01/atmPlanInfo/downLoadAtmPlan";
		top.$.jBox.open(
				content,
				//选择ATM机
				"<spring:message code='label.atm.add.chooseAtm' />", 900, 600, {
					buttons : {
						// 确认
						"<spring:message code='common.confirm' />" : "ok",
						// 关闭
						"<spring:message code='common.close' />" : true
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							// 获取jbox弹窗 子页面数据
							var contentWindow = h.find("iframe")[0].contentWindow;
							// 调用子页面下载方法
                            contentWindow.downLoad();
							return false;
						}
					},
					loaded : function(h) {
						 $(".jbox-content", top.document).css(
								"overflow-y", "hidden"); 
					}
				});
	}
	/*	end	*/
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/atm/v01/atmPlanInfo/importFile"><spring:message code='label.atm.addPlan.menu.01'/></a></li>
		<li><a href="${ctx}/atm/v01/atmPlanInfo/list"><spring:message code='label.atm.addPlan.menu.02'/></a></li>
	</ul>
	<br/>
	<form id="addPlanForm" action="${ctx}/atm/v01/atmPlanInfo/importFile" method="post"
		enctype="multipart/form-data" class="form-horizontal">
		<div class="control-group">
			<sys:message content="${message}"/>
			<!-- 请选择计划文件 -->
			<label class="control-label"><spring:message code="label.atm.addPlan.selectMonitorFile"/>：</label>
			<div class="controls">
				<!-- 文件 -->
				<input type="file" name="addPlanFile" id="addPlanFile"/> 
				<!-- 导入 -->
				<input type="button" value="<spring:message code="button.atm.addPlan.import"/>" 
						class="btn btn-primary" id="uploadAddPlanFile"/>
				<!-- 下载模板  	修改人：wxz	2017-12-6 begin -->
				<%-- <a href="#" onclick="downLoadPlan();"><spring:message code='atm.addPlan.downLoad.templet'/></a> --%>		
				<input type="button" value="<spring:message code='atm.addPlan.downLoad.templet'/>" class="btn btn-primary" onclick="downLoadPlan();"/>
				<!-- end -->
				<!-- 手动生成 	修改人：wxz	2017-12-6 begin -->
				<input type="button" value="手动生成" class="btn btn-primary" 
					onclick='javascrtpt:window.location.href="${ctx}/atm/v01/atmPlanInfo/addPlanForm"'>
				<!-- end -->
			</div>
		</div>
	</form>
	<c:if test="${importSuccess eq true}">
		<jsp:include page="addPlanPrint.jsp"></jsp:include>
	</c:if>
</body>
</html>