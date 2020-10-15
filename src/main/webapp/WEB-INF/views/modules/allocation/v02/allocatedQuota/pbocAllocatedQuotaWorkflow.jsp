<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>

<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(".work_flow_01_done").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toApproval.status}');
		}); 
		$(".work_flow_02_done").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toQuota.status}');
		}); 
		$(".handin-01").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toInOutStore.status}');
		}); 
		$(".handin-03").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toHandover.status}');
		}); 
		$(".work_flow_05_done").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toAccept.status}');
		}); 
		$(".work_flow_03_done").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.finish.status}');
		}); 
		$(".work_flow_07_done").click(function(){	
			searchOperateInfo();
		}); 
		function searchOperateInfo(status){
			if("undefined" != typeof status){ 
				var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/showApplicationOperateInfo?allId="+$("#allId").val()+"&status="+status;
			} else {
				var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/showApplicationOperateInfo?allId="+$("#allId").val();
			}
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
.work_flow_01_done,.work_flow_02_done,.work_flow_05_done,.work_flow_07_done,.work_flow_03_done,.handin-01,.handin-03,#workflow-return-btn-01:HOVER {
	cursor: pointer;
}
</style>

</head>
<body>
    <ul class="nav nav-tabs">					
	<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.workFlow" /></a></li>
	</ul>

	<c:set var="picInfo" value="${pbocWorkflowInfo}" />
	<input value="${picInfo.allId}" id="allId" style="display: none">
	<div class="content" style="overflow-x:auto;margin-top:50px;margin-left: 50px;">
	<div style="float:left;">
		<div style="width:1800px;float: left; display:block">
			<div class="workflow-div">
				<!-- 驳回线 01-->
				<c:if test="${picInfo.reject.colorFlag}">
					<div id="workflow-return-btn-01" class="workflow-return-btn-01">
						<span><spring:message code="common.reject" /></span>
					</div>
					<div class="workflow-return-sort"
						style="margin-left: 80px; display: inline-block">
						<div class="workflow-return-01">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01"
							style="position: absolute; right: 0"></div>
					</div>
				</c:if>
				<!-- 驳回线 01 end -->
			</div>
		</div>
		<div style="float: left;display:block">
		<!-- 流程 01 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.toApproval.colorFlag}">
					<span class="work_flow_01_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_01_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.application.allocated.registe" /></a>
		</div>
		<!-- 流程 01 end-->
		<c:choose>
			<c:when test="${picInfo.toApproval.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 02 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.approver" /></h4>
			<c:choose>
				<c:when test="${picInfo.toQuota.colorFlag||picInfo.reject.colorFlag}">
					<span class="work_flow_02_done"></span>
					<div class="workflow-tip">
					    <i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_02_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.pboc.approval" /></a>
		</div>
		<!-- 流程 02 end-->
		<c:choose>
			<c:when test="${picInfo.toQuota.colorFlag and !picInfo.reject.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 03 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.storeKeeper" /></h4>
			<c:choose>
				<c:when test="${picInfo.toInOutStore.colorFlag}">
					<span class="handin-01"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handin-01-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.pboc.quota" /></a>
		</div>
		<!-- 流程 03 end-->
		<c:choose>
			<c:when test="${picInfo.toInOutStore.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 04 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.storeKeeper" /></h4>
			<c:choose>
				<c:when test="${picInfo.toHandover.colorFlag}">
					<span class="handin-03"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handin-03-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.hanout.doorScan" /></a>
		</div>
		<!-- 流程 04 end-->
		<c:choose>
			<c:when test="${picInfo.toHandover.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 05 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.businessBank.man" />/<spring:message code="allocation.pboc.peopleBank.man" /></h4>
			<c:choose>
				<c:when test="${picInfo.finish.colorFlag}">
					<span class="work_flow_05_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_05_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.hanout.handover" /></a>
		</div>
		<!-- 流程 05 end-->
		<c:choose>
			<c:when test="${picInfo.finish.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 06 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.finish.colorFlag}">
					<span class="work_flow_03_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_03_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.confirm.receive" /></a>
		</div>
		<!-- 流程 06 end-->
		<c:choose>
			<c:when test="${picInfo.finish.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 流程 07 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.finish" /></h4>
			<c:choose>
				<c:when test="${picInfo.finish.colorFlag}">
					<span class="work_flow_07_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_07_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.finish" /></a>
		</div>
		<!-- 流程 07 end-->
		</div>
	</div>
</div>
<br>
	<div>
		<input id="btnCancel" class="btn btn-primary" style="margin-left: 100px;" type="button" 
			value="<spring:message code='common.return'/>" onclick="history.go(-1)" />
	</div>
</body>
</html>