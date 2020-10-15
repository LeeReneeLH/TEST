<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
    	<script type="text/javascript" src="${fns:getConfig('firstPage.amap.js.url')}&plugin=AMap.Autocomplete,AMap.Geocoder"></script>
    </c:if>
    <style type="text/css">
#div1 {
	width: 50px;
	height: 25px;
	border-radius: 50px;
	position: relative;
	display: block;
	float: left;
	top: 3px;
}

#div2 {
	width: 25px;
	height: 22px;
	border-radius: 48px;
	position: absolute;
	background: #fff;
	box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.4);
}
#div3 {
	width: 50px;
	height: 25px;
	border-radius: 50px;
	position: relative;
	display: block;
	float: left;
	top: 3px;
}

#div4 {
	width: 25px;
	height: 22px;
	border-radius: 48px;
	position: absolute;
	background: #fff;
	box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.4);
}

.open1 {
	background: #2bbf2e;
	border: 1px solid #2bbf2e;
}

.open2 {
	top: 1px;
	right: 1px;
	z-index: 10;
}

.close1 {
	background: #999;
	border: 1px solid #999;
	border-left: transparent;
}

.close2 {
	left: 1px;
	z-index: 10;
	top: 1px;
}

.btn-on {
	position: absolute;
	left: 3px;
	top: -2px;
	font-size: 12px;
	color: #fff;
}

.btn-off {
	position: absolute;
	right: 3px;
	top: -2px;
	font-size: 12px;
	color: #fff;
}
</style>
	<script type="text/javascript">
		$(function() {
			var div2 = document.getElementById("div2");
			var div1 = document.getElementById("div1");
			var div3 = document.getElementById("div3");
			var div4 = document.getElementById("div4");
			var mainFrame = document.getElementById("mainFrame");
			div1.className = "close1";
			div2.className = "close2";
			div3.className = "close1";
			div4.className = "close2";
			var provisionsSwitch = document.getElementById("provisionsSwitch").value;
			var joinManSwitch = document.getElementById("joinManSwitch").value;
			if(provisionsSwitch == null){
				$("#provisionsSwitch").val("1");
			}else {
				if(provisionsSwitch == '1'){
					div1.className="close1";
					div2.className="close2";
					$("#showOpenDiv").hide();
					$("#showCloseDiv").show();
				}
				if(provisionsSwitch == '0'){
					div1.className="open1";
					div2.className="open2";
					$("#showCloseDiv").hide();
					$("#showOpenDiv").show();
					
				}
			}
			if(joinManSwitch == null){
				$("#joinManSwitch").val("1");
			}else{
				if(joinManSwitch == '1'){
					div3.className="close1";
					div4.className="close2";
					$("#showOpenDiv1").hide();
					$("#showCloseDiv1").show();
					
				}
				if(joinManSwitch == '0'){
					div3.className="open1";
					div4.className="open2";
					$("#showCloseDiv1").hide();
					$("#showOpenDiv1").show();
				}
			}
			div2.onclick = function() {
				div1.className = (div1.className == "close1") ? "open1" : "close1";
				div2.className = (div2.className == "close2") ? "open2" : "close2";
				if (div1.className == "close1" && div2.className == "close2") {
					$("#provisionsSwitch").val("1");
					$("#showOpenDiv").hide();
					$("#showCloseDiv").show();
					disconnect();
				} else if (div1.className == "open1" && div2.className == "open2") {
					$("#provisionsSwitch").val("0");
					$("#showCloseDiv").hide();
					$("#showOpenDiv").show();
					initWebSocket();
					messageBoxRefresh();
					try{ 
	        			if(typeof(eval(mainFrame.contentWindow.todoBoxRefresh))=="function") {
	        				mainFrame.contentWindow.todoBoxRefresh();
	        			}
	        		}catch(e){}
				}
			};
			
			div4.onclick = function() {
				div3.className = (div3.className == "close1") ? "open1" : "close1";
				div4.className = (div4.className == "close2") ? "open2" : "close2";
				if (div3.className == "close1" && div4.className == "close2") {
					$("#joinManSwitch").val("1");
					$("#showOpenDiv1").hide();
					$("#showCloseDiv1").show();
					disconnect();
				} else if (div3.className == "open1" && div4.className == "open2") {
					$("#joinManSwitch").val("0");
					$("#showCloseDiv1").hide();
					$("#showOpenDiv1").show();
					initWebSocket();
					messageBoxRefresh();
					try{ 
	        			if(typeof(eval(mainFrame.contentWindow.todoBoxRefresh))=="function") {
	        				mainFrame.contentWindow.todoBoxRefresh();
	        			}
	        		}catch(e){}
				}
			
			};
			
			selectClearCenter($("#type").val());
			// 隐藏详细
			toggle();
			$("#name").focus();
			var officeType = ${office.type==null?'4':office.type};
			/* if(officeType != '4') {
				$("#tradeFlag").hide();
			} */
			$("#inputForm").validate({
				rules: {
					code: {remote: {url:"${ctx}/sys/office/checkCode",data:{code:function(){return $("#code").val();},oldCode:function(){return $("#oldCode").val();}}}}
				},
				messages: {
					code: {remote: "机构编码已经存在"},
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
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
			//机构名称输入验证
			$.validator.addMethod("checkName",function(value,element){  
				            var checkCode = /^[\d\D]{1,50}$/;  
				            return this.optional(element)||(checkCode.test(value));  
				        },"最大可以输入50个汉字");   
			//银行卡号输入验证s
			$.validator.addMethod("checkBankCard",function(value,element){  
				            var checkCode = /^[0-9]{0,25}$/;  
				            return this.optional(element)||(checkCode.test(value));
				        },"最大可以输入25位数字");  
			//机构编号输入验证
			$.validator.addMethod("checkCode",function(value,element){  
				            var checkCode = /^[0-9]+$/;  
				            return this.optional(element)||(checkCode.test(value));  
				        },"请输入合法数字");  
				
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
		/* function changeType(value) {
			if(value==='4') {
				// 网点-同业
				$("#tradeFlag").show();
			} else {
				$("#tradeFlag").hide();
			}
		} */
		
		/*
		 * 选择清分中心时选择开关
		 */
		 
		 function switchButtonClose() {
				$("#provisionsSwitch").val("1");
				div1.className = "close1";
				div2.className = "close2";
				$("#showOpenDiv").hide();
				$("#showCloseDiv").show();
			}
		function switchButtonOpen() {
				$("#provisionsSwitch").val("0");
				div1.className = "open1";
				div2.className = "open2";
				$("#showCloseDiv").hide();
				$("#showOpenDiv").show();
			}
		
		function selectClearCenter(val){
			/*if(val == '6'){
				document.getElementById("provisionsSwitchId").style.display="";
				document.getElementById("joinManSwitchId").style.display="";
			} else {
				document.getElementById("provisionsSwitchId").style.display="none";
				document.getElementById("joinManSwitchId").style.display="none";
			}*/
			// 判断机构信息是否需要机构编号(油站编码必填)
			if(val == '10'){
				$("#code").attr("required",true);
				$("#code").attr("minlength","5");
				$("#code").removeAttr("maxlength");
				$("#code").attr("maxlength","5");
				
				$("#payerAccountId").attr("required",true);
				$("#payerAccountName").attr("required",true);
				$("#payeeAccountId").attr("required",true);
				$("#payeeAccountName").attr("required",true);
				
			} else {
				$("#code").removeAttr("required");
				$("#code").removeAttr("minlength");
				$("#code").removeAttr("maxlength");
				$("#code").attr("maxlength","15");
				
				$("#payerAccountId").removeAttr("required");
				$("#payerAccountName").removeAttr("required");
				$("#payeeAccountId").removeAttr("required");
				$("#payeeAccountName").removeAttr("required");
			}
			
		}
		
		
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
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/office/list">机构列表</a></li>
		<li class="active"><a href="${ctx}/sys/office/form?id=${office.id}&parent.id=${office.parent.id}">机构<shiro:hasPermission name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/office/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级机构：</label>
			<div class="controls">
                <sys:treeselect id="office" name="parent.id" value="${office.parent.id}" labelName="parent.name" labelValue="${office.parent.name}"
					title="机构" url="/sys/office/treeData" extId="${office.id}" checkGroupOffice="true" cssClass="required" allowClear="${office.currentUser.admin}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 增加归属机构   修改人：xp 修改时间：2017-7-3 begin-->
		<%-- <div class="control-group">
			<label class="control-label">归属机构：</label>
			<div class="controls">
                <sys:treeselect id="ascriptionOffice" name="ascriptionOfficeId.id" value="${office.ascriptionOfficeId.id}" labelName="ascriptionOfficeId.name" labelValue="${office.ascriptionOfficeId.name}"
					title="机构" url="/sys/office/treeData" extId="${ascriptionOfficeId.id}" checkGroupOffice="true"  allowClear="${office.currentUser.admin}"/>
				
			</div>
		</div> --%>
		<!-- end -->
		<div class="control-group">
			<label class="control-label">机构名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="checkName required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构编号：</label>
			<div class="controls">
			<input id="oldCode" name="oldCode" type="hidden" value="${office.code}" >
			<c:choose>
			<c:when test="${office.type eq '10'}">
			<form:input path="code" htmlEscape="false" maxlength="15" class="checkCode" readonly="true"/>
			</c:when>
			<c:otherwise>
			<form:input path="code" htmlEscape="false" maxlength="15" class="checkCode" />
			</c:otherwise>
			</c:choose>
				
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构类型：</label>
			<div class="controls">
				<%-- <form:select path="type" class="input-large required" onchange="changeType(this.value)"> --%>
				<form:select id="type" path="type" class="input-large required"  onchange="selectClearCenter(this.value)"  >
					<form:options items="${fns:getDictList('sys_office_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%-- 离线地图显示选择机构所在省市县时 --%>
		<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '1'}">
			<%--省份和城市下拉选项 --%>
			<sys:provinceCity/>
		</c:if>
			<!-- 增加归属机构   修改人：ZXK 修改时间：2019-12-12 begin-->
		<div class="control-group">
			<label class="control-label">付款方客户账号：</label>
			<div class="controls">
				<form:input path="payerAccountId" htmlEscape="false" maxlength="25" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款方账户名称：</label>
			<div class="controls">
				<form:input path="payerAccountName" htmlEscape="false" maxlength="50" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款方客户账号：</label>
			<div class="controls">
				<form:input path="payeeAccountId" htmlEscape="false" maxlength="25" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款方账户名称：</label>
			<div class="controls">
				<form:input path="payeeAccountName" htmlEscape="false" maxlength="50" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款方开户行名称：</label>
			<div class="controls">
				<form:input path="payeeBankName" htmlEscape="false" maxlength="50" />
			</div>
		</div>
			<!-- 增加归属机构   修改人：ZXK end-->
		<%-- 在线地图显示机构经纬度 --%>
		<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
			<%--机构经度 --%>
			<div class="control-group">
				<label class="control-label">机构经度：</label>
				<div class="controls">
					<form:input path="longitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
				</div>
			</div>
				
			<%--机构纬度 --%>
			<div class="control-group">
				<label class="control-label">机构纬度：</label>
				<div class="controls">
					<form:input path="latitude" htmlEscape="false" maxlength="13" class="checkLocation"/>
				</div>
			</div>
		</c:if>
		
		<div style="display: none" id="provisionsSwitchId" class="control-group">
			<label class="control-label">备付金余额验证开关：</label>
			<div class="controls">
				<div id="switchButton">
					<div id="div1" class="open1">
    					<div id="div2" class="open2"></div>
	   	 				<div class="btn-off" style="padding-top: 3px" id="showCloseDiv"><font size="3.5">关</font></div>
	    				<div class="btn-on" style="padding-top: 3px" id="showOpenDiv"><font size="3.5">开</font></div>
					</div>
					<form:input path="provisionsSwitch" id="provisionsSwitch" type="hidden" />
				</div>
			</div>
		</div>
		
		<div style="display: none" id="joinManSwitchId" class="control-group">
			<label class="control-label">银行交接人员开关：</label>
			<div class="controls">
				<div id="switchButton1">
					<div id="div3" class="open1">
    					<div id="div4" class="open2"></div>
	   	 				<div class="btn-off" style="padding-top: 3px" id="showCloseDiv1"><font size="3.5">关</font></div>
	    				<div class="btn-on"style="padding-top: 3px" id="showOpenDiv1"><font size="3.5">开</font></div>
					</div>
					<form:input path="joinManSwitch" id="joinManSwitch" type="hidden" />
				</div>
			</div>
		</div>
	
		
		<%-- 	<div id="tradeFlag">
				<div class="control-group">
					<label class="control-label">机构分类：</label>
					<div class="controls">
						<form:radiobuttons path="tradeFlag" items="${fns:getFilterDictList('sys_office_trade',false,'0')}" itemLabel="label"  itemValue="value"/>
					</div>
				</div>
			</div> --%>
		<div style="width:400px;">
		<%@ include file="/WEB-INF/views/include/divCollapse.jsp" %>
		</div>
		<div class="control-group accordion-body collapse in" id="collapseOne">
		<div class="control-group">
			<label class="control-label">负责人：</label>
			<div class="controls">
				<form:input path="master" htmlEscape="false" maxlength="10"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="15" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">传真：</label>
			<div class="controls">
				<form:input path="fax" htmlEscape="false" maxlength="15" class="simplePhone"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱：</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="50" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮政编码：</label>
			<div class="controls">
				<form:input path="zipCode" htmlEscape="false" maxlength="6" class="zipCode" />
			</div>
		</div>
        <div class="control-group">
            <label class="control-label">银行卡号：</label>
            <div class="controls">
                <form:input path="bankCard" htmlEscape="false" maxlength="25" class="checkBankCard"/>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label">联系地址：</label>
            <div class="controls">
                <form:textarea path="address" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge"/>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" class="input-xlarge"/>
			</div>
		</div>
		</div>
		<div class="form-actions" style="width:360px;">
			<shiro:hasPermission name="sys:office:edit">
			<c:if test="${fns:getUser().office.type == '0' or office.type != '0'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
			</c:if>
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返回" onclick = "window.location.href ='${ctx}/sys/office/back'"/>
		</div>
	</form:form>
	<c:if test="${fns:getConfig('firstPage.map.isOnline') eq '0'}">
		<div style="width: 50%;height:50%;position: absolute;right: 1%;top: 5%;">
		
			<div id="myPageTop" >
				<div class="control-group">
					<label class="control-label">按关键字搜索：</label>
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