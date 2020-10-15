<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>

<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(".handorder-01").click(function(){	
			searchOperateInfo('${workFlowInfo.register.status}');
		}); 
		$(".handorder-02").click(function(){	
			searchOperateInfo('${workFlowInfo.confirm.status}');
		}); 
		$(".handin-01").click(function(){	
			searchOperateInfo('${workFlowInfo.packScan.status}');
		}); 
		$(".handin-03").click(function(){	
			searchOperateInfo('${workFlowInfo.doorScan.status}');
		}); 
		$(".handin-04").click(function(){	
			searchOperateInfo('${workFlowInfo.onload.status}');
		}); 
		$(".handin-02").click(function(){	
			searchOperateInfo('pointHandover');
		}); 
		$(".handorder-06").click(function(){	
			searchOperateInfo('${workFlowInfo.finish.status}');
		}); 
		$("#workflow-return-btn").click(function(){	
			searchOperateInfo("90");
		}); 
		$("#workflow-return-btn-01").click(function(){	
			searchOperateInfo("90");
		}); 
		function searchOperateInfo(status){
			var content = "iframe:${ctx}/allocation/v01/cashOrder/showOperateInfo?allId="+$("#allId").val()+"&status="+status;
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
.handin-01, .handin-03, .handin-02,.handin-04, .handorder-06,.handorder-01,.handorder-02,#workflow-return-btn,#workflow-return-btn-01:HOVER {
	cursor: pointer;
}
</style>

</head>
<body>
    <ul class="nav nav-tabs">
	<c:choose>
	<c:when test="${fns:getUser().office.type == '4'}">
		<li><a href="${ctx}/allocation/v01/cashOrder"><spring:message
								code="allocation.cash.box.cashorder.list" /></a></li>
	</c:when>
    <c:otherwise>
		<li><a href="${ctx}/allocation/v01/boxHandover/handout"><spring:message
								code="allocation.cash.box.handout.list" /></a></li>
	</c:otherwise>
	</c:choose>							
		<li class="active"><a href="#" onclick="javascript:return false;"><spring:message code="allocation.workFlow.handout" /></a></li>
	</ul>

	<c:set var="picInfo" value="${workFlowInfo}" />
	<input value="${picInfo.allId}" id="allId" style="display: none">
	<div class="content" style="overflow-x:auto;margin-top:50px;margin-left: 50px;">
	<div style="width:1850px;float:left;">
		<div style="float: left; display:block">
			<div class="workflow-div">
				<!-- 撤回线 01-->
				<c:if test="${picInfo.confirm.showCancelFlag}">
					<div id="workflow-return-btn-01" class="workflow-return-btn-01">
						<span><spring:message code="allocation.cancel" /></span>
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
				<!-- 撤回线 01 end -->
				<!-- 撤回线 02-->
				<c:if test="${picInfo.packScan.showCancelFlag}">
					<div id="workflow-return-btn" class="workflow-return-btn">
						<span><spring:message code="allocation.cancel" /></span>
					</div>
					<div class="workflow-return"
						style="margin-left: 80px; display: inline-block">
						<div class="workflow-return-01">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01"
							style="position: absolute; right: 0"></div>
					</div>
				</c:if>
				<!-- 撤回线 02 end -->
			</div>
			<c:if test="${picInfo.doorScan.showHandoverFlag}">
				<div class="workflow-div">
					<div class="workflow-return-btn" style="margin-left: 850px;">
						<span><spring:message code="allocation.hanout.handoverTodo" /></span>
					</div>
					<!-- 交接线 -->
					<div class="workflow-return"
						style="margin-left: 900px; display: block">
						<div class="workflow-return-01"
							style="position: absolute; right: 0">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01"></div>
					</div>
					<!-- 交接线  end -->
				</div>
			</c:if>
		</div>
		<div style="float: left;display:block">
		<!-- 下拨流程 01 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.outlets.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.register.colorFlag}">
					<span class="handorder-01"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handorder-01-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.cash.order.register" /></a>
		</div>
		<!-- 下拨流程 01 end-->
		<c:choose>
			<c:when test="${picInfo.register.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 02 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.confirm.colorFlag}">
					<span class="handorder-02"></span>
					<div class="workflow-tip">
					    <i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handorder-02-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.cash.order.approve" /></a>
		</div>
		<!-- 下拨流程 02 end-->
		<c:choose>
			<c:when test="${picInfo.confirm.colorFlag && !picInfo.confirm.showCancelFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 03 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.packScan.colorFlag}">
					<span class="handin-01"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handin-01-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.hanout.packScan" /></a>
		</div>
		<!-- 下拨流程 03 end-->
		<c:choose>
			<c:when test="${picInfo.packScan.colorFlag && !picInfo.packScan.showCancelFlag&&!picInfo.packScan.showTempFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 04 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.doorScan.colorFlag}">
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
		<!-- 下拨流程 04 end-->
		<c:choose>
			<c:when test="${picInfo.doorScan.colorFlag && picInfo.onload.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 05 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate" />/<spring:message code="store.escortUser" /></h4>
			<c:choose>
				<c:when test="${picInfo.onload.colorFlag}">
					<span class="handin-04"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handin-04-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.hanout.handover" /></a>
		</div>
		<!-- 下拨流程 05 end-->
		<c:choose>
			<c:when test="${picInfo.onload.colorFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 06 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.outlets.operate"/>/<spring:message code="store.escortUser"/></h4>
			<c:choose>
				<c:when test = "${pointHandover.pointHandover.detailList.size() != '0' && pointHandover != null}">
						<span class="handin-02"></span>
						<div class="workflow-tip">
							<i class="fa fa-check text-green fa-lg"></i>
						</div>
				</c:when>
				<c:otherwise>
					<span class="handin-02-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.handin.handBox"/></a>
		</div>
		<!-- 下拨流程 06 end-->
		<c:choose>
			<c:when test="${picInfo.doorScan.colorFlag || picInfo.packScan.showTempFlag}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		<!-- 下拨流程 07 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.outlets.operate" /></h4>
			<c:choose>
				<c:when test="${picInfo.finish.colorFlag}">
					<span class="handorder-06"></span>
					<div class="workflow-tip">
						<i class="fa fa-check text-green fa-lg"></i>
					</div>
				</c:when>
				<c:otherwise>
					<span class="handorder-06-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.finish" /></a>
		</div>
		<!-- 下拨流程 07 end-->
		</div>
		<!-- 临时下拨线-->
		<c:if test="${picInfo.packScan.showTempFlag}">
				<div style="float: left; display: block;">
					<div class="workflow-return-btn-bottom"
						style="position: relative; left: 730px;">
						<span><spring:message code="allocation.tasktype.temporary_task" /></span>
					</div>
					<div class="workflow-return-bottom" style="display: inline-block">
						<div class="workflow-return-01"></div>
						<div class="workflow-return-01"
							style="position: absolute; right: 0">
							<div class="workflow-return-target-bottom"></div>
						</div>
					</div>
				</div>
			</c:if>
		<!-- 临时下拨线 end-->
	</div>
</div>
	<div>
		<input id="btnCancel" class="btn btn-default" style="margin-left: 100px;" type="button" 
		value="<spring:message code='common.return'/>" onclick="window.location.href ='${ctx}/allocation/v01/cashOrder/backList?backFlag=${backFlag}'" />
	</div>
</body>
</html>
