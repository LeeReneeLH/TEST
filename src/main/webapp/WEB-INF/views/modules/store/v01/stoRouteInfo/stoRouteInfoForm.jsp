<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%@include file="/WEB-INF/views/include/treeview.jsp"%>
<title><spring:message code="store.route.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript" src="${ctxStatic}/script/colorSelect/jscolor.js"></script>
<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
    <script type="text/javascript" src="${fns:getConfig('firstPage.amap.js.url')}"></script>
    <%-- <script type="text/javascript" src="${fns:getConfig('firstPage.amap.main.js.url')}"></script> --%>
</c:if>
<script type="text/javascript">
	var map,route,path,completeRoute,pointItem,routeColor;
	/* 初始化加载明细 */
	$(document).ready(function() {
//		$("#detailDiv").load("${ctx}/store/v01/stoRouteInfo/getList");	
		
		//地图显示
		if (${fns:getConfig('firstPage.map.isOnline') eq '0'}) {
			
			if (${fns:getUser().office.longitude==null}) {
				top.$.jBox.tip("<spring:message code='message.E1081' />","error");
				$(".colorSeclect").hide();
			}else{
				initMap();
				$("[id*='officeTree']").click(function(){
					setTimeout(function(){
						var tempPath = [];
						var nodes2 = tree2.getCheckedNodes(true);
						tempPath.push([${fns:getUser().office.longitude},${fns:getUser().office.latitude}]);
						for(var i=0; i<nodes2.length; i++) {
							if (nodes2[i].type != 4){
								continue; // 过滤掉父节点
							}
							if (nodes2[i].longitude==""||nodes2[i].longitude==null) {
								top.$.jBox.tip("<spring:message code='message.E1080' />","error");
							}
							tempPath.push([nodes2[i].longitude,nodes2[i].latitude]);			
						}
						tempPath.push([${fns:getUser().office.longitude},${fns:getUser().office.latitude}]);
						if(tempPath.toString()!=path.toString()){
							map.clearMap();  // 清除地图覆盖物
							path = tempPath;
							map.plugin("AMap.DragRoute", function() {
								route = new AMap.DragRoute(map, tempPath, AMap.DrivingPolicy.LEAST_FEE); //构造拖拽导航类
								route.search(); //查询导航路径并开启拖拽导航
							});		
						}
						route.on('complete',function(e){
							completeRoute = route.getRoute();
							$("#distance").html("<spring:message code='store.routePlanDistance' />"+e.data.routes[0].distance/1000);
							$("#routeLnglat").val(completeRoute);
						});
					}, 1000)
				});	
			
				var routeId = '${stoRouteInfo.id}';
				path = [];
				if(routeId!=""&&routeId!=null){
					//绘制初始路径		    
					$.ajax({
						url : '${ctx}/store/v01/stoRouteInfo/getRoutePlanning',
						type : 'Post',
						contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
						async : false,
						cache : false,
						data : {
							routeId :routeId
						},
						dataType : 'json',
						success : function(res) {	
							pointItem = res.officeList;
							routeColor = res.routePlanColor;
							$("#routePlanColor").val(routeColor);
							for(var count=0;count<res.longitudeList.length;count++){
								path.push([res.longitudeList[count],res.latitudeList[count]]);
							}
							if(res.defaultPlan){
								initRoutePlan();
							}else{
								changedRoutePlan();
							}
						}
					});					
				}
			}
		}
		$("#routeName").focus();
	 	$("#inputForm").validate({
			//focusCleanup:true,
			onkeyup:false,
			submitHandler: function(form){
				var ids2 = [], nodes2 = tree2.getCheckedNodes(true);
				for(var i=0; i<nodes2.length; i++) {
					if (nodes2[i].type != 4){
						continue; // 过滤掉父节点
					}
					ids2.push(nodes2[i].id);
				}
				if(ids2.length!=0) {
					$("#officeIds").val(ids2);
					loading('正在提交，请稍等...');
					form.submit();
			 	} else {
					top.$.jBox.tip("<spring:message code='message.E1024' />","error");
				} 
			},
			//errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				//$("#messageBox").text("输入有误，请先更正。");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
			
		}); 
		var setting = {check:{enable:true},view:{selectedMulti:false},
			data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
				tree2.checkNode(node, node.checked, true, true);
				return false;
			}}};  
		// 用户-机构
 		var zNodes2=[
				<c:forEach items="${officeList}" var="office">
					{
						id:'${office.id}', pId:'${not empty office.parent?office.parent.id:0}', name:"${office.name}", type:"${office.type}", 
						latitude:"${office.latitude}", longitude:"${office.longitude}"
						<c:if test="${office.type == '3'}">
							,nocheck:'true'
						</c:if>
						<c:forEach items="${routOfficelist}" var="routOffice">
							<c:if test="${office.id==routOffice.id}">
								,nocheck:'true'
							</c:if>
						</c:forEach>
					},					
	            </c:forEach>];
		// 初始化树结构
		var tree2 = $.fn.zTree.init($("#officeTree"), setting, zNodes2);
		// 不选择父节点
		tree2.setting.check.chkboxType = { "Y" : "s", "N" : "s" };
		// 默认选择节点
		var ids2 = "${stoRouteInfo.officeIds}".split(",");
		for(var i=0; i<ids2.length; i++) {
			var node = tree2.getNodeByParam("id", ids2[i]);
			try{tree2.checkNode(node, true, false);}catch(e){}
		}
		// 默认展开全部节点
		tree2.expandAll(true);
		// 显示机构
		$("#officeTree").show();
	});
	/* 保存 */
	function save(){
		$("#inputForm").validate();
		var escort1 = $("#escortId1").val();
		var escort2 = $("#escortId2").val();
		if(escort1!=""&&escort1==escort2) {
			top.$.jBox.tip("<spring:message code='message.E1026' />","error");
			return;
		}
		if($("#inputForm").valid()){
	    	$("#inputForm").submit();
	 	}
	}
	/*
	 * 初始化在线地图
	 */
	function initMap() {
		map = new AMap.Map("container", {
		    resizeEnable: true
		});   
	}
	//重新规划线路
	function initRoutePlan(){
		map.clearMap();  // 清除地图覆盖物
		var officeLnglat = [];
		for (var index = 0; index < pointItem.length; index ++) {
			officeLnglat.push(pointItem[index].lnglat);
		}
	    map.plugin("AMap.DragRoute", function() {
	        route = new AMap.DragRoute(map, officeLnglat, AMap.DrivingPolicy.LEAST_FEE); //构造拖拽导航类
	        route.search(); //查询导航路径并开启拖拽导航
	    });
	    
	    route.on('complete',function(e){
	    	completeRoute = route.getRoute();
	    	$("#distance").html("<spring:message code='store.routePlanDistance' />"+e.data.routes[0].distance/1000);
	    	$("#routeLnglat").val(completeRoute);
	    });
	}
	//显示规划完成的线路
	function changedRoutePlan(){
		if($("#distance").html()==""||$("#distance").html()==null){
			for (var index = 0; index < pointItem.length; index ++) {
				// 标记网点
				var pointMarker = new AMap.Marker({
				          map: map,
				          position: pointItem[index].lnglat,
				          icon: "${ctxStatic}/images/map_office_" + pointItem[index].pointType + ".png",
				          offset: new AMap.Pixel(-12, -36)
				});
				pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
			          content: pointItem[index].name});
			}		
			// 绘制已规划线路
			new AMap.Polyline({
			     map: map,
			     path: path,
			     zIndex:10,
			     strokeColor: "#"+routeColor,  //线颜色
			     lineJoin : "round", // 折线拐点的绘制样式，默认值为'miter'尖角，其他可选值：'round'圆角、'bevel'斜角
			     lineCap : "round", // 折线两端线帽的绘制样式，默认值为'butt'无头，其他可选值：'round'圆头、'square'方头
			     // strokeOpacity: 1,     //线透明度
			     strokeStyle: "dashed",  //线样式，实线:solid，虚线:dashed
			     strokeWeight: 6,      //线宽
			     strokeDasharray: [5,2,5] //表示5个像素的实线和2个像素的空白 + 5个像素的实线和5个像素的空白 （如此反复）组成的虚线
			 });
			map.setFitView();
		}
	}
	//颜色改变事件
	function colorChange(){
		routeColor = $(".jscolor").val();
		changedRoutePlan();
	}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/store/v01/stoRouteInfo/"><spring:message code="store.route.list" /></a></li>
		<li class="active"><a
			href="${ctx}/store/v01/stoRouteInfo/form?id=${stoRouteInfo.id}">
				<shiro:hasPermission name="store:stoRouteInfo:edit">
					<c:choose>
						<c:when test="${not empty stoRouteInfo.id}">
							<spring:message code="store.route" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<spring:message code="store.route" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> <shiro:lacksPermission name="store:stoRouteInfo:edit">
					<spring:message code="store.route" /><spring:message code="common.view" />
				</shiro:lacksPermission>
		</a></li>
	</ul>
	<br />
	<form:form id="inputForm" modelAttribute="stoRouteInfo"
		action="${ctx}/store/v01/stoRouteInfo/save" method="post"
		class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="routeLnglat" />
		<sys:message content="${message}" />
		<div class="control-group">
			<label class="control-label"><spring:message code="store.routeName" />：</label>
			<div class="controls">
				<form:input path="routeName" htmlEscape="false" maxlength="25" class="required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 是否使用押运人员开关 -->
		<c:if test="${fns:getConfig('route.used.escort')=='1'}">
		<div class="control-group">
			<label class="control-label"><spring:message code="store.escortUser1" />：</label>
			<div class="controls">
				<!-- 押运人员存在反显 -->
				<c:if test="${not empty(stoRouteInfo.escortInfo1)}">
				<form:select path="escortInfo1.id" id="escortId1" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<!-- 是否可以重复绑定押运人员开关 -->
					<c:if test="${not empty (fns:getConfig('route.double.binding.escort'))&& fns:getConfig('route.double.binding.escort')=='0'}">
						<!-- 此操作可以保证下拉列表不存在也可容易反显 -->
						<c:if test="${not empty (stoRouteInfo.escortInfo1.id)}">
							<form:option value="${stoRouteInfo.escortInfo1.id}" selected="selected">${stoRouteInfo.escortInfo1.escortName}</form:option>
						</c:if>
					</c:if>
					<form:options items="${sto:getFilterStoEscortinfoList('noBindingEscortList', fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
				</form:select>
				</c:if>
				<c:if test="${empty(stoRouteInfo.escortInfo1)}">
				<form:select path="escortInfo1" id="escortId1" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${sto:getFilterStoEscortinfoList('noBindingEscortList', fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
				</form:select>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code="store.escortUser2" />：</label>
			<div class="controls">
				<c:if test="${not empty(stoRouteInfo.escortInfo2)}">
				<form:select path="escortInfo2.id" id="escortId2" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<!-- 是否重复绑定押运人员开关 -->
					<c:if test="${not empty (fns:getConfig('route.double.binding.escort')) && fns:getConfig('route.double.binding.escort')=='0'}">
						<!-- 此操作可以保证下拉列表不存在也可容易反显 -->
						<c:if test="${not empty(stoRouteInfo.escortInfo2.id)}">
							<form:option value="${stoRouteInfo.escortInfo2.id}" selected="selected">${stoRouteInfo.escortInfo2.escortName}</form:option>
						</c:if>
					</c:if>
					<form:options items="${sto:getFilterStoEscortinfoList('noBindingEscortList',fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
				</form:select>
				</c:if>
				<c:if test="${empty(stoRouteInfo.escortInfo2)}">
				<form:select path="escortInfo2" id="escortId2" class="input-large">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${sto:getFilterStoEscortinfoList('noBindingEscortList', fns:getUser().office.id)}" itemLabel="escortName" itemValue="id" htmlEscape="false" />
				</form:select>
				</c:if>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">车牌号码：</label>
					<div class="controls">
						<form:select path="carNo" id="carNo" class="input-large">
					    	<form:option value=""><spring:message code="common.select" /></form:option>
						   <c:if test="${not empty(stoRouteInfo.carNo)}"> 
								<form:option value="${stoRouteInfo.carNo}" selected="selected">${stoRouteInfo.carNo}</form:option>
						</c:if>	
 					<form:options items="${sto:getStoCarInfoList(fns:getUser().office.id)}" itemLabel="carNo" itemValue="carNo" htmlEscape="false" />
						</form:select>
					</div>
		</div>
		<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
		<div class="control-group colorSeclect">
			<label class="control-label"><spring:message code="store.routePlanColor" />：</label>
			<div class="controls">
				<form:input id="routePlanColor" path="routePlanColor" htmlEscape="false" maxlength="6" class="jscolor input-mini" value="FF0000" onchange="colorChange()" />
			</div>
		</div>
		<div class="control-group colorSeclect">
			<label class="control-label"><spring:message code="store.carTrackColor" />：</label>
			<div class="controls">
				<form:input id="carTrackColor" path="carTrackColor" readOnly="readOnly" htmlEscape="false" maxlength="6" class="input-mini" value="888888" />
			</div>
		</div>
		</c:if>
		<%-- <div class="control-group">
			<label class="control-label"><spring:message code="common.remark" />:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="80" class="input-xxlarge" />
			</div>
		</div> --%>
		<div class="control-group" style="width:488px;min-height: 200px; max-height: 200px; overflow:auto; ">
			<label class="control-label"><spring:message code="store.selectOffice" />：</label>
			<div class="controls">
				<div id="officeTree" class="ztree" style="margin-top: 3px; float: left;"></div>
				<form:hidden path="officeIds"/>
			</div>
		</div>
	</form:form>
	<div class="form-horizontal">
		<!-- <div id="detailDiv"
			style="margin-left: 150px; width: 390px; min-height: 150px; max-height: 150px; overflow: auto; overflow-x: hidden;">
		</div> -->
		<div class="form-actions" style="width:250px;">
			<shiro:hasPermission name="store:stoRouteInfo:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.save'/>" onclick="save()" />&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/store/v01/stoRouteInfo/back'"/>&nbsp;
			<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
				<c:if test="${not empty stoRouteInfo.id}">
					<input class="btn btn-primary" type="button" value="重新规划" onclick="initRoutePlan()"/>
				</c:if>
			</c:if>
		</div>
	</div>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
		<div style="width: 60%;height:80%;position: absolute;right: 1%;top: 10%;">
			<div id="distance"></div>
			<div id="container" style="width: 100%;height:100%;"></div>
		</div>
	</c:if>
</body>
</html>
