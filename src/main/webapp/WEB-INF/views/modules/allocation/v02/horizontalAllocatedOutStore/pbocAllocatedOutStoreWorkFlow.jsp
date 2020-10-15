<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<meta name="decorator" content="default" />
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$(".work_flow_01_done").click(function() {
				 showCenterWorkFlowDetail('${pbocWorkflowInfo.toQuota.status}');
			});
			$(".handin-01").click(function() {
				 showCenterWorkFlowDetail('${pbocWorkflowInfo.toInOutStore.status}');
			});
			$(".work_flow_05_done").click(function() {
				 showCenterWorkFlowDetail('${pbocWorkflowInfo.toHandover.status}');
			});
			$(".work_flow_07_done").click(function() {
				 showCenterWorkFlowDetail('${pbocWorkflowInfo.finish.status}');
			});
			
			function showCenterWorkFlowDetail(status){
				var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/showDetailInfo?allId=${pbocWorkflowInfo.allId}"+"&status="+status;
				top.$.jBox.open(
				    content,
				    "<spring:message code='common.content'/>", 800, 600,{
					buttons : {
						"<spring:message code='common.close' />" : true
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
					}
				});
			}
		});
	</script>
	<style type="text/css">
		.work_flow_01_done, .work_flow_02_done, .work_flow_03_done,.work_flow_05_done,.work_flow_07_done,#workflow-return-btn,#workflow-return-btn-01:HOVER {
			cursor: pointer;
		}
	</style>
</head>
<body>
    <ul class="nav nav-tabs">
    	<%-- 发行基金调拨出库列表 --%>
		<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/list?bInitFlag=true"><spring:message code="allocation.issueFund.sametrade.outStore.list" /></a></li>
		<%-- 发行基金调拨出库登记 --%>
		<li><a href="${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/form"><spring:message code="allocation.issueFund.sametrade.outStore.register" /></a></li>
		<%-- 工作流程 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.workFlow" /></a></li>
	</ul>
	<div class="content" style="overflow-x:auto;margin-top:50px;margin-left: 50px;">
		<!-- 调拨出库流程 01 -->
		<div class="workflow-con">
			<%-- 人民银行操作员 --%>
			<h4 class="text-center"><spring:message code="allocation.pboc.operator" /></h4>
			<span class="work_flow_01_done"></span>
			<div class="workflow-tip">
				 <i class="fa fa-check text-green fa-lg"></i>
			</div>
			<%-- 出库登记 --%>
			<a href="#"><spring:message code="allocation.outStore.register" /></a>
		</div>
		<!-- 调拨出库流程 01 end-->
		<div class="workflow-line-green"></div>
		<!-- 调拨出库流程 02 -->
		<div class="workflow-con">
			<%-- 人民银行库存员 --%>
			<h4 class="text-center"><spring:message code="allocation.pboc.storeKeeper" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toInOutStore.colorFlag}">
					<span class="handin-01"></span>
					<div class="workflow-tip">
					    <i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when> 
				<c:otherwise>
					<span class="handin-01-gray"></span>
				</c:otherwise>
			</c:choose>
			<%-- 已配款 --%>
			<a href="#"><spring:message code="allocation.pboc.quota" /></a>
		</div>
		<!-- 调拨出库流程 02 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toInOutStore.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 调拨出库流程 03 -->
		<div class="workflow-con">
			<%-- 人民银行库存员 --%>
			<h4 class="text-center"><spring:message code="allocation.pboc.storeKeeper" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toHandover.colorFlag}">
					<span class="work_flow_05_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_05_undo"></span>
				</c:otherwise>
			</c:choose>
			<%-- 出库交接 --%>
			<a href="#"><spring:message code="allocation.hanout.handover" /></a>
		</div>
		<!-- 调拨出库流程 03 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toHandover.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 调拨出库流程 04 -->
		<div class="workflow-con">
			<h4 class="text-center"></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.finish.colorFlag}">
					<span class="work_flow_07_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_07_undo"></span>
				</c:otherwise>
			</c:choose>
			<%-- 完成 --%>
			<a href="#"><spring:message code="allocation.finish" /></a>
		</div>
		<!-- 调拨出库流程 04 end-->
	</div>
	<div>
		<input id="btnCancel" class="btn btn-primary" style="margin-left: 100px;" type="button" 
		value="<spring:message code='common.return'/>" onclick="window.location.href ='${ctx}/allocation/v02/pbocHorizontalAllocatedOutStore/back'" />
	</div>
</body>
</html>
