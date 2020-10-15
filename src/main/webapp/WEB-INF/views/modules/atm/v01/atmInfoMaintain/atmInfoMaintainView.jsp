<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>ATM机信息维护详情</title>
	<meta name="decorator" content="default"/>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
    	<script type="text/javascript" src="${fns:getConfig('firstPage.amap.js.url')}&plugin=AMap.Autocomplete,AMap.Geocoder"></script>
    </c:if>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		//地图显示
	    if (${fns:getConfig('firstPage.map.isOnline') eq '0'}) {
			initMap();
	    }
		
	});
	
	/*
	 * 初始化在线地图 坐标地址选取地图
	 */
	function initMap() {
		var map = new AMap.Map("container", {
		    resizeEnable: true
		});
		//添加地图工具
		AMap.plugin('AMap.ToolBar',function(){
		   var toolbar = new AMap.ToolBar();
		   map.addControl(toolbar)
		});
		// 纬度
		var latitude = $("#latitude").val();
		//经度
		var longitude = $("#longitude").val();
		// 反显已选地址
		if (longitude != "" && latitude != "") {
			var latLng = new AMap.LngLat(parseFloat(longitude), parseFloat(latitude));
			map.clearMap();  // 清除地图覆盖物
			map.setZoom(18);
			map.setCenter(latLng);
			var pointMarker = new AMap.Marker({
		          map: map,
		          position: latLng
		      });
           pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
		          content: $("#address").val()});
           pointMarker.setAnimation('AMAP_ANIMATION_BOUNCE');
		}

	   var auto = new AMap.Autocomplete({
	       input: "tipinput"
	   });
	   AMap.event.addListener(auto, "select", select);//注册监听，当选中某条记录时会触发
	   function select(e) {
	       if (e.poi && e.poi.location) {
	    	   map.clearMap();  // 清除地图覆盖物
	           var geocoder = new AMap.Geocoder();
			   geocoder.getAddress(e.poi.location, function(status, result) {
				    if (status === 'complete' && result.info === 'OK') {
						map.setZoom(18);
						map.setCenter(e.poi.location);
						
				    	//返回地址描述
				    	var pickAddress = result.regeocode.formattedAddress;
				    	var pointMarker = new AMap.Marker({
					          map: map,
					          position: e.poi.location
					      });
			           pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
					          content: pickAddress});
			           $("#latitude").val(e.poi.location.getLat());
			           $("#longitude").val(e.poi.location.getLng());
			           $("#address").val(pickAddress);
				    }
				});
	           
	       }
	   }
	}

	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- ATM信息列表 -->
		<li><a href="${ctx}/atm/v01/atmInfoMaintain/"><spring:message code="title.atm.addPlan.list" /></a></li>
	   	<li class="active"><a href="${ctx}/atm/v01/atmInfoMaintain/view?id=${atmInfoMaintain.id}">
	   		<!-- ATM信息列表查看 -->
			<spring:message code="title.atm.info" /><spring:message code="common.view" /> 
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmInfoMaintain" action="${ctx}/atm/v01/atmInfoMaintain/save" method="post" class="form-horizontal">
	    <!--主键  -->
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<!--ATM机编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="label.atm.id.no" />：</label>
			<div class="controls">
				<form:input path="atmId" htmlEscape="false" class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--柜员号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="label.atm.add.teller"/>：</label>
			<div class="controls">
				<form:input path="tellerId" htmlEscape="false" class="input-large required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--维护机构  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.vindicate.officeName"/>：</label>
			<div class="controls">
				<form:input path="vinofficeName" htmlEscape="false" class="input-large required abc" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--归属机构 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.ascription.officeName"/>：</label>
			<div class="controls">
				<form:input path="aofficeName" htmlEscape="false" class="input-large required" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--所属金库  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.ascription.coffersName"/>：</label>
			<div class="controls">
				<form:input path="tofficeName" htmlEscape="false" class="input-large digits" readonly="true" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--品牌编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.id"/>：</label>
			<div class="controls">
				<form:input path="atmBrandsNo" htmlEscape="false" class="input-large" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--品牌名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.name"/>：</label>
			<div class="controls">
				<form:input path="atmBrandsName" htmlEscape="false" class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--型号编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.type.number"/>：</label>
			<div class="controls">
				<form:input path="atmTypeNo" htmlEscape="false" class="input-large " readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--型号名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.type.name"/>：</label>
			<div class="controls">
				<form:input path="atmTypeName" htmlEscape="false" class="input-large required digits" readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--RFID  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.rfid"/>：</label>
			<div class="controls">
				<form:input path="rfid" htmlEscape="false" class="input-large " readonly="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		

		<!-- 地图显示机构经纬度 -->
		<!--装机经度 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.longitude"/>：</label>
			<div class="controls">
				<form:input path="longitude" htmlEscape="false" maxlength="13" class="checkLocation" readonly="true"/>
			</div>
		</div>
			
		<!--装机纬度 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.latitude"/>：</label>
			<div class="controls">
				<form:input path="latitude" htmlEscape="false" maxlength="13" class="checkLocation" readonly="true"/>
			</div>
		</div>
		
		<!--装机地址  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.address"/>：</label>
			<div class="controls">
				<form:textarea path="address" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge" readonly="true"/>
			</div>
		</div>
		
		
		
		
		<!--操作按钮  -->
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/atm/v01/atmInfoMaintain/back'"/>
		</div>
	</form:form>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
		<div style="width: 60%;height:50%;position: absolute;right: 1%;top: 5%;">
		
			<div id="myPageTop" >
				<br>
				
			</div>
			<div id="container" style="width: 100%;height:100%;" ></div>
		</div>
	</c:if>
	
	
</body>
</html>
