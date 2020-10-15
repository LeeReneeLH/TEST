<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title></title>
	<meta name="decorator" content="default"/>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
    	<script type="text/javascript" src="${fns:getConfig('firstPage.amap.js.url')}&plugin=AMap.Autocomplete,AMap.Geocoder"></script>
    </c:if>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#btnSubmit").click(function(){
                var url = "${ctx}/atm/v01/atmInfoMaintain/save";
	            $("#inputForm").attr("action", url);
	            
				$("#inputForm").submit();
			});
			
			var parameters = {
					rules: {
						atmId: {remote:{ url:"${ctx}/atm/v01/atmInfoMaintain/atm",data:{oldAtmId:function(){return $("#oldAtmId").val();}}}},
						tellerId: {remote:{ url:"${ctx}/atm/v01/atmInfoMaintain/teller",data:{oldTellerId:function(){return $("#oldTellerId").val();}}}},
					},
					messages: {
						atmId: {remote: "该ATM机编号已存在！"},
						tellerId: {remote: "该柜员号已存在！"},
					},
					submitHandler: function(form){
						loading('正在提交，请稍等...');
						form.submit();	
					}, 
					////errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						//$("#messageBox").text("输入有误，请先更正。");
						if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
							error.appendTo(element.parent().parent());
						} else {
							error.insertAfter(element);
						}
					}
			};
			
			// 初始化型号名称及型号编号 下拉列表
			changeAtmType($("#atmBrandsNo").val());
			
			$("#inputForm").validate(parameters);
			//机构经纬度输入验证
			$.validator.addMethod("checkLocation",function(value,element){  
					           var checkLocation = /^([1-9]\d{0,5}|0)(\.\d{0,6})?$/;  
					           return this.optional(element)||(checkLocation.test(value));  
					        },"请输入合法数字");
			
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
			//为地图注册click事件获取鼠标点击出的经纬度坐标
			var clickEventListener = map.on('click', function(e) {
				map.clearMap();  // 清除地图覆盖物
	            var geocoder = new AMap.Geocoder();
				geocoder.getAddress(e.lnglat, function(status, result) {
				    if (status === 'complete' && result.info === 'OK') {
				    	//返回地址描述
				    	var pickAddress = result.regeocode.formattedAddress;
				    	var pointMarker = new AMap.Marker({
					          map: map,
					          position: e.lnglat
					      });
			            pointMarker.setLabel({offset: new AMap.Pixel(0, -23),//修改label相对于marker的位置
					          content: pickAddress});
			            
			            $("#address").val(pickAddress);
				    }
				});
	            
			    document.getElementById("latitude").value =  e.lnglat.getLat();
			    
			    document.getElementById("longitude").value = e.lnglat.getLng();
			});
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

		
		
		// 根据选择的品牌名称及编号，加载型号名称及型号编号
		function changeAtmType(typeNo){
			var strUpdateDate = $("#strUpdateDate").val();
			var url = '${ctx}/atm/v01/atmInfoMaintain/changeAtmType';
			
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						param : JSON.stringify(typeNo)
					},
					success : function(serverResult, textStatus) {
						$('#atmTypeNo').select2({
							containerCss:{width:'163px',display:'inline-block'},
							data:{ results: serverResult, text: 'label' },
							formatSelection: format,
							formatResult: format 
						});
						// 加载型号编号下拉列表
						if(strUpdateDate != null && strUpdateDate != ""){
							// 点击修改(编辑)操作，回显数据
						} else {
							// 点击新增操作，根据选择的品牌编号及名称 加载型号名称
							//2017-12-06 wanglin edit  begin
							if (serverResult != undefined && serverResult != null && serverResult.length >0) {
								$('#atmTypeNo').select2("val",serverResult[0].id);
							}
							//$('#atmTypeNo').select2("val",serverResult[0].id);
							//2017-12-06 wanglin edit  end
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
					}
				});
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) {
			return item.label;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- ATM信息列表 -->
		<li><a href="${ctx}/atm/v01/atmInfoMaintain/"><spring:message code="title.atm.addPlan.list" /></a></li>
	   	<li class="active"><a href="${ctx}/atm/v01/atmInfoMaintain/form?id=${atmInfoMaintain.id}">
	   	<shiro:hasPermission name="atm:atmInfoMaintain:edit">
					<c:choose>
						<c:when test="${not empty atmInfoMaintain.id}">
							<!-- ATM信息修改 -->
							<spring:message code="title.atm.info" /><spring:message code="common.modify" />
						</c:when>
						<c:otherwise>
							<!-- ATM信息添加 -->
							<spring:message code="title.atm.info" /><spring:message code="common.add" />
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission> 
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="atmInfoMaintain" action="${ctx}/atm/v01/atmInfoMaintain/save" method="post" class="form-horizontal">
	    <!--主键  -->
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<input type="hidden" id="strUpdateDate" name="strUpdateDate" value="${atmInfoMaintain.strUpdateDate}">	
		<!--ATM机编号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="label.atm.id.no" />：</label>
			<div class="controls">
				<input type="hidden" id="oldAtmId" name="oldAtmId" value="${atmInfoMaintain.atmId}"/>
				<form:input path="atmId" htmlEscape="false" maxlength="8" minlength="4" class="input-large required digits" style="font-size:16px"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--柜员号  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="label.atm.add.teller"/>：</label>
			<div class="controls">
				<input type="hidden" id="oldTellerId" name="oldTellerId" value="${atmInfoMaintain.tellerId}"/>
				<form:input path="tellerId" htmlEscape="false" maxlength="8" class="input-large required" style="font-size:16px"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--维护机构名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.vindicate.officeName"/>：</label>
			<div class="controls">
				<%-- <sys:treeselect id="wOffice" name="vinofficeId"
					value="${atmInfoMaintain.vinofficeId}" labelName="vinofficeName"
					labelValue="${atmInfoMaintain.vinofficeName}" title="维护机构"
					url="/sys/office/treeData" cssClass="input-medium required"
					allowClear="true" notAllowSelectParent="false"
					notAllowSelectRoot="false" type="3"
							    isAll="true" /> --%>
				<c:set var="currentOffice" value="${fns:getUser()}"/>		    
				<sys:treeselect id="wOffice" name="vinofficeId" cssStyle="font-size:16px"
					value="${not empty atmInfoMaintain.vinofficeId?atmInfoMaintain.vinofficeId:currentOffice.office.id}" labelName="vinofficeName"
					labelValue="${not empty atmInfoMaintain.vinofficeName?atmInfoMaintain.vinofficeName:currentOffice.office.name}" title="维护机构"
					url="/sys/office/treeData" notAllowSelectRoot="false"
					notAllowSelectParent="false" allowClear="false"
					cssClass="input-medium required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--归属机构名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.ascription.officeName"/>：</label>
			<div class="controls">
				<%-- <sys:treeselect id="gOffice" name="aofficeId"
					value="${atmInfoMaintain.aofficeId}" labelName="aofficeName"
					labelValue="${atmInfoMaintain.aofficeName}" title="归属机构"
					url="/sys/office/treeData" cssClass="required input-medium"
					allowClear="true" notAllowSelectParent="false"
					notAllowSelectRoot="false" type="4"
							    isAll="true" /> --%>
							
				<sys:treeselect id="gOffice" name="aofficeId" cssStyle="font-size:16px"
					value="${not empty atmInfoMaintain.aofficeId?atmInfoMaintain.aofficeId:currentOffice.office.id}" labelName="aofficeName"
					labelValue="${not empty atmInfoMaintain.aofficeName?atmInfoMaintain.aofficeName:currentOffice.office.name}" title="归属机构"
					url="/sys/office/treeData" notAllowSelectRoot="false"
					notAllowSelectParent="false" allowClear="false"
					cssClass="input-medium required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--所属金库名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.ascription.coffersName"/>：</label>
			<div class="controls">
				<form:input type="text" htmlEscape="false" value="${fns:getUser().office.name}" style="font-size:16px" 
				readonly="true" path="tofficeName" class="input-large required"/>
				<form:input type="hidden" value="${fns:getUser().office.id}" path="tofficeId"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--品牌名称 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.brands.name"/>：</label>
			<div class="controls">
				<%-- <form:input id="atmBrandsName" type="text" htmlEscape="false" readonly="true" path="atmBrandsName" class="input-large required"/> --%>
				<form:select path="atmBrandsNo" id="atmBrandsNo" class="input-large required" style="width:224px;font-size:16px;" onchange="changeAtmType(this.value)">
		    		<form:option value=""><spring:message code="common.select"/></form:option>
					<form:options items="${atm:getAtmBrandsinfoList()}"
						itemLabel="atmBrandsName" itemValue="atmBrandsNo" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!--型号名称  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.type.name"/>：</label>
			<div class="controls">
				<input type="hidden" id="atmTypeName" value="${atmInfoMaintain.atmTypeName}">
				<form:input id="atmTypeNo" type="hidden" path="atmTypeNo" class="input-large required" style="width:224px;font-size:16px;" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 地图显示机构经纬度 -->
		<!--装机经度 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.longitude"/>：</label>
			<div class="controls">
				<form:input path="longitude" htmlEscape="false" maxlength="13" class="checkLocation" style="font-size:16px"/>
			</div>
		</div>
			
		<!--装机纬度 -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.latitude"/>：</label>
			<div class="controls">
				<form:input path="latitude" htmlEscape="false" maxlength="13" class="checkLocation" style="font-size:16px"/>
			</div>
		</div>
		
		<!--装机地址  -->
		<div class="control-group">
			<label class="control-label"><spring:message code="atm.install.address"/>：</label>
			<div class="controls">
				<form:textarea path="address" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge" style="font-size:16px"/>
			</div>
		</div>
		<br>
		<br>
		<br>
		<br>
		<!--操作按钮  -->
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.commit'/>"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/atm/v01/atmInfoMaintain/back'"/>
		</div>
	</form:form>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
		<div style="width: 60%;height:50%;position: absolute;right: 1%;top: 5%;">
		
			<div id="myPageTop" >
				<div class="control-group">
					<!-- 按关键字搜索 -->
					<label class="control-label"><spring:message code="atm.keyWord.search"/>：</label>
					<div class="controls">
						<input type="text" placeholder="请输入关键字进行搜索" id="tipinput">
					</div>
				</div>
			</div>
			<div id="container" style="width: 100%;height:100%;"></div>
		</div>
	</c:if>
	
	
	
</body>
</html>
