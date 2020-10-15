<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="allocation.handin.workFlow"/></title>
	<meta name="decorator" content="default" />
	<script type="text/javascript">
	$(document).ready(function() {
		$(".handin-01").click(function(){	
			showAllDetail('${workFlowInfo.allId}','${workFlowInfo.onload.status}');
		}); 
		$(".handin-02").click(function(){	
			showDetail('${workFlowInfo.allId}','${workFlowInfo.onload.status}');
		});
		$(".handin-03").click(function(){	
			showAllDetail('${workFlowInfo.allId}','${workFlowInfo.doorScan.status}');
		}); 
		$(".handin-04").click(function(){	
			showDetail('${workFlowInfo.allId}','${workFlowInfo.finish.status}');
		}); 
		$(".handin-05").click(function(){	
			showDetail('${workFlowInfo.allId}','');
		}); 
	});	
		function showDetail(allId,status) {
			var content = "iframe:${ctx}/allocation/v01/handInWorkFlow/findDetail?allId="+allId+"&status="+status;
			top.$.jBox.open(
				content,
				"<spring:message code='common.content'/>", 800, 420, {
					buttons : {
						//关闭
						"<spring:message code='common.close'/>" : true
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
					}
			});
		}
		function showAllDetail(allId,status) {
			var content = "iframe:${ctx}/allocation/v01/handInWorkFlow/findAllDetail?allId="+allId+"&status="+status;
			top.$.jBox.open(
				content,
				"<spring:message code='common.content'/>", 800, 700, {
					buttons : {
						//关闭
						"<spring:message code='common.close'/>" : true
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
											"overflow-y", "hidden");
					}
			});
		}
	</script>
	<style type="text/css">
		.handin-01,.handin-02,.handin-03,.handin-04,.handin-05:HOVER {
			cursor: pointer;
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<%-- 箱袋上缴列表(link) --%>
		<c:if test = "${fns:getUser().office.type == '3'}">
			<li>
				<a href="${ctx}/allocation/v01/boxHandover/handin">
					<spring:message code="allocation.cash.box.handin.list" />
				</a>
			</li>
		</c:if>
		<c:if test = "${fns:getUser().office.type=='7'}">
			<li>
				<a href="${ctx}/allocation/v01/boxHandover/handin">
					<spring:message code="allocation.cash.box.handin.list" />
				</a>
			</li>
		</c:if>
		<!-- 现金上缴列表 -->
		<c:if test = "${fns:getUser().office.type == '4'}">
			<li>
				<a href="${ctx}/allocation/v01/cashHandin" >
					<spring:message code="allocation.cash.handin.list" />
				</a>
			</li>
		</c:if>
        <%-- 上缴业务列表 --%>
        <li class="active">
        	<a href="#" onclick="javascript:return false;">
        		<spring:message code="allocation.handin.workFlow" />
        	</a>
        </li>
	</ul>
	<!-- 上缴业务工作流 -->
	<div class="content" style="margin-top: 50px;overflow-x:auto;margin-left: 50px;">
	<div style="width:1300px;float:left;">
		<div style="float: left;width: 100%;">
			<div class="workflow-div" style= "background-color: white;">
				<!-- 撤回线 01-->
				<c:if test = "${workFlowInfo.onload.showCancelFlag == true}">
					<div class="workflow-return-btn-01">
						<a href = "#" onclick="showDetail('${workFlowInfo.allId}','${workFlowInfo.onload.status}');javascript:return false;" id = "register">
							<!-- 撤回 -->
							<span><spring:message code="allocation.cancel"/></span>
						</a>
					</div>
					<div class="workflow-return-sort" style="margin-left:80px;display:inline-block">
						<div class="workflow-return-01">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01" style="position:absolute;right:0"></div>
					</div>
				</c:if>
				<!-- 撤回线 01 end -->
				
				<!-- 撤回线 02-->
				<c:if test = "${workFlowInfo.onload.showCancelFlag == true}">
					<div class="workflow-return-btn" style="display:none">
						<a href = "#" onclick="showDetail('${workFlowInfo.allId}','${workFlowInfo.onload.status}');javascript:return false;" id = "register">
							<!-- 撤回 -->
							<span><spring:message code="allocation.cancel"/></span>
						</a>
					</div>
					<div class="workflow-return" style="margin-left:80px;display:none">
						<div class="workflow-return-01">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01" style="position:absolute;right:0"></div>
					</div>
				</c:if>
				<!-- 撤回线 02 end -->
			</div>
			
			<c:if test = "${workFlowInfo.finish == null && workFlowInfo.doorScan !=null}">
				<div class="workflow-div" style = "margin-left :590px">
					<div class="workflow-return-btn">
						<span><spring:message code="allocation.handin.handoverTodo"/></span>
					</div>
					<!-- 撤回线 03-->
					<div class="workflow-return" style="  margin-left: 40px;display:block">
						<div class="workflow-return-01" style="position:absolute;right:0">
							<div class="workflow-return-target"></div>
						</div>
						<div class="workflow-return-01"></div>
					</div>
					<!-- 撤回线 03 end -->
				</div>
			</c:if>
		</div>
		<!-- 上缴流程 01 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.outlets.operate"/></h4>
			<c:choose>
				<c:when test="${workFlowInfo.onload.status == '90'}">
					<span class="handin-01"></span>
				</c:when>
				<c:otherwise>
					<span class="handin-01"></span>
				</c:otherwise>
			</c:choose>
			<!-- 对号 -->
			<div class="workflow-tip">
				<i class="fa fa-check text-green fa-lg"></i>
			</div>
			<a href="#"><spring:message code="allocation.handin.packScan"/></a>
		</div>
		<!-- 上缴流程 01 end-->
		
		<!-- 线 -->
		<div class="workflow-line-green"></div>
		
		<!-- 上缴流程 02 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.outlets.operate"/>/<spring:message code="store.escortUser"/></h4>
			<c:choose>
				<c:when test = "${pointHandover.pointHandover.detailList.size() != '0'}">
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
		<!-- 上缴流程 02 end-->
		
		<!-- 线 -->
		<c:choose>
			<c:when test = "${workFlowInfo.onload.showCancelFlag == false && workFlowInfo.onload !=null}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 上缴流程 03 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate"/></h4>
			<c:choose>
				<c:when test = "${workFlowInfo.doorScan.colorFlag == true && workFlowInfo.doorScan !=null}">
						<span class="handin-03"></span>
						<div class="workflow-tip">
							<i class="fa fa-check text-green fa-lg"></i>
						</div>
				</c:when>
				<c:otherwise>
					<span class="handin-03-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.handin.doorScan"/></a>
		</div>
		<!-- 上缴流程 03 end-->
		
		<!-- 线 -->
		<c:choose>
			<c:when test = "${workFlowInfo.finish != null}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 上缴流程 04 -->
		<div class="workflow-con">
			<h4 class="text-center"><spring:message code="allocation.coffer.operate"/>/<spring:message code="store.escortUser"/></h4>
			<c:choose>
				<c:when test = "${workFlowInfo.finish != null}">
						<span class="handin-04"></span>
						<!-- 对号 -->
						<div class="workflow-tip">
							<i class="fa fa-check text-green fa-lg"></i>
						</div>
				</c:when>
				<c:otherwise>
					<span class="handin-04-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.handin.handover"/></a>
		</div>
		<!-- 上缴流程 04 end-->
		
		<!-- 线 -->
		<c:choose>
			<c:when test = "${workFlowInfo.finish != null}">
				<div class="workflow-line-green"></div>
			</c:when>
			<c:otherwise>
				<div class="workflow-line"></div>
			</c:otherwise>
		</c:choose>
		
		<!-- 上缴流程 05 -->
		<div class="workflow-con">
			<h4 class="text-center"></h4>
			<c:choose>
				<c:when test = "${workFlowInfo.finish != null && workFlowInfo.doorScan !=null}">
					<span class="handin-05"></span>
					<div class="workflow-tip"><i class="fa fa-check text-green fa-lg"></i></div>
				</c:when>
				<c:when test = "${workFlowInfo.finish == null && workFlowInfo.doorScan !=null}">
					<span class="handin-05"></span>
				</c:when>
				<c:otherwise>
					<span class="handin-05-gray"></span>
				</c:otherwise>
			</c:choose>
			<a href="#"><spring:message code="allocation.finish"/></a>
		</div>
		<!-- 上缴流程 05 end-->
	</div>
	</div>
	<div>
		<input id="btnCancel" class="btn btn-default" style="margin-left: 100px;" type="button" 
		value="<spring:message code='common.return'/>" onclick="window.location.href ='${ctx}/allocation/v01/handInWorkFlow/backList?backFlag=${backFlag}'" />
	</div>
</body>
</html>
