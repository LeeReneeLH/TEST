<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>

<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(".work_flow_01_done").click(function(){
			searchOperateInfo('${pbocWorkflowInfo.toQuota.status}');
		}); 
		 
		$(".handin-01").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toInOutStore.status}');
		}); 
		
		$(".handin-03").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toHandover.status}');
		}); 
	
		$("#work_flow_05_done_01").click(function(){
			searchOperateInfo('${pbocWorkflowInfo.clearing.status}');
		});
		
		$(".clear-01").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.toInStoreHandover.status}');
		}); 
		
		$("#work_flow_05_done_02").click(function(){	
			searchOperateInfo('${pbocWorkflowInfo.finish.status}');
		}); 
		
		$(".work_flow_07_done").click(function(){	
			searchOperateInfo();
		}); 
		
		function searchOperateInfo(status){
			if("undefined" != typeof status){ 
				var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/showRecountingOperateInfo?allId="+$("#allId").val()+"&status="+status;
			} else {
				var content = "iframe:${ctx}/allocation/v02/pbocWorkflow/showRecountingOperateInfo?allId="+$("#allId").val();
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
.work_flow_01_done, .handin-01, .handin-03,.work_flow_05_done,#work_flow_05_done_01,#work_flow_05_done_02,.work_flow_07_done:HOVER {
	cursor: pointer;
}
</style>

</head>
<body>
    <ul class="nav nav-tabs">
		<%-- 发行基金复点管理 --%>
		<li><a href="${ctx}/allocation/v02/pbocRecounting/list"><spring:message code="allocation.pboc.recounting.mgr" /></a></li>
		<%-- 复点工作流程 --%>
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.workFlow" /></a></li>
	</ul>

	<input value="${pbocWorkflowInfo.allId}" id="allId" style="display: none">
	<div class="content" style="overflow-x:auto;margin-top:50px;margin-left: 50px;">
	<div style="width:1800px;float:left;">
		<div style="float: left;display:block">
		<!-- 复点流程 01 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.operator"/></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toQuota.colorFlag}">
					<span class="work_flow_01_done"></span>
					<div class="workflow-tip">
						 <i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when> 
				<c:otherwise>
					<span class="work_flow_01_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.pboc.recounting.register"/></a>
		</div>
		<!-- 复点流程 01 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toQuota.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 复点流程 02 -->
		<div class="workflow-con">
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
			<a href="#"><spring:message code="allocation.pboc.quota" /></a>
		</div>
		<!-- 复点流程 02 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toInOutStore.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 复点流程 03 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.pboc.storeKeeper" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toHandover.colorFlag}">
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
		<!-- 复点流程 03 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toHandover.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 复点流程 04 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="common.agent.office" />/<spring:message code="allocation.pboc.peopleBank.man" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.clearing.colorFlag}">
					<span id="work_flow_05_done_01" class="work_flow_05_done"></span>
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
		<!-- 复点流程 04 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.clearing.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 复点流程 05 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="common.agent.office" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toInStoreHandover.colorFlag}">
					<span class="clear-01"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="clear-01-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.pboc.clearing" /></a>
		</div>
		<!-- 复点流程 05 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toInStoreHandover.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 复点流程 06 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="common.agent.office" />/<spring:message code="allocation.pboc.peopleBank.man" /></h4>
			<c:choose>
				<c:when test="${pbocWorkflowInfo.toInStoreHandover.colorFlag}">
					<span id="work_flow_05_done_02" class="work_flow_05_done"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="work_flow_05_undo"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.handin.handover" /></a>
		</div>
		<!-- 复点流程 06 end-->
		<c:choose>
			<c:when test="${pbocWorkflowInfo.toInStoreHandover.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 复点流程 07 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.finish" /></h4>
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
			<a href="#"><spring:message code="allocation.finish" /></a>
		</div>
		<!-- 复点流程 07 end-->
		
		</div>
		
	</div>
</div>
	<div>
		<input id="btnCancel" class="btn btn-primary" style="margin-left: 100px;" type="button" 
			value="<spring:message code='common.return'/>" onclick="window.location.href ='${ctx}/allocation/v02/pbocRecounting/list?repage'" />
	</div>
</body>
</html>
