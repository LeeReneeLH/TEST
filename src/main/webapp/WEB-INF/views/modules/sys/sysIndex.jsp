<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')}</title>
	<meta name="decorator" content="blank"/><c:set var="tabmode" value="${empty cookie.tabmode.value ? '0' : cookie.tabmode.value}"/>
    <c:if test="${tabmode eq '1'}"><link rel="Stylesheet" href="${ctxStatic}/jerichotab/css/jquery.jerichotab.css" />
    <script type="text/javascript" src="${ctxStatic}/jerichotab/js/jquery.jerichotab.js"></script></c:if>
    <script type="text/javascript" src="${ctxStatic}/script/websocket/sockjs-1.0.0.js"></script>
	<style type="text/css">
		#main {padding:0;margin:0;} #main .container-fluid{padding:0 4px 0 6px;}
		#header {margin:0 0 8px;position:static;} #header li {font-size:14px;_font-size:12px;}
		#header .brand {font-family:Helvetica, Georgia, Arial, sans-serif, 黑体;font-size:26px;padding-left:33px;}
		#footer {position: fixed;margin:8px 0 0 0;padding:3px 0 0 0;font-size:11px;text-align:center;border-top:2px solid #0663A2;}
		#footer, #footer a {color:#999;} #left{overflow-x:hidden;overflow-y:auto;} #left .collapse{position:static;}
		#userControl>li>a{/*color:#fff;*/text-shadow:none;} #userControl>li>a:hover, #user #userControl>li.open>a{background:transparent;}
         @media only screen and (max-width:1246px) {.off_sel{display: none;}}
        .tip-con{box-shadow: 0 0 8px rgba(0,0,0,0.3);font-size:12px;position:absolute;bottom: 60px;right: 0px;padding-bottom: 10px;width:350px;border:0px solid #d0d0d0;background: #fff;z-index:500;}
		.tip-con h4{font-size: 16px;    font-weight: normal;width:350px;line-height: 35px;background: #755890;padding:0 0 0 10px;    box-sizing: border-box; color: #fff;}
    	.tip-con h4 a{display: inline-block;float: right;width: 40px;height: 100%;color:#fff;background:#5a2f81;text-align: center;}
   		.tip-con h4 a:hover{background-color:#4d0e85}
   		.tip-con ul{width:100%;box-sizing: border-box;padding: 5px 10px;border-bottom:1px solid #ccc;margin: 0;display:block;cursor:pointer;}
   		.tip-con ul li:nth-child(1){color:#000;font-size:14px;}
   		.tip-con-first{color:#000 !important;font-size:14px;}
   		.tip-con-red {color: #ff0000 !important;font-size: 14px;}
   		.tip-con ul li{width:100%;line-height:25px;color:#999;list-style: none;box-sizing: border-box;}
    	.tip-con ul li a{color:#333;}
    	.tip-con ul li+li{font-size:12px;}
    	.tip-con ul:hover{background-color:#ffe3e3}
    	.tip-con ul li span{display:inline-block;float:right;}
   		.tip-con h5{color: #666;position: absolute;bottom:0px;width:350px;background: #d9dee6;line-height:25px;height:25px;}
   		.tip-con h5 a{float:right;padding:0 20px;color:#666;}
   		.tip-con h5 a:hover{color:#e66b67}
   		.tip-min:hover{background-color:#5a2f81}
   		.tip-min{width: 65px;cursor:pointer;height: 65px;border-radius: 100% 0 0 0;background: #755890;position: absolute;bottom: 60px;right: 0px;box-sizing: border-box;padding-right: 10px;color: #fff;text-align: right;z-index: 501;font-size: 30px;line-height: 65px;}
		.tip-num{width: 6px;height: 6px;font-size: 12px;position: absolute;border-radius: 100%;background: red;text-align: center;line-height: 20px;color: #fff;top: 12px;right: 4px;border: 2px solid #fff;}   
		#loadingDiv {
		    background-color:white;
		    filter: alpha(opacity=50); 
		    opacity: 0.1;
		    display: none;
		    position: absolute;
		   	top: 60px;
		    left: 190px;
		    width: 100%;
		   /*  height: 100%; */
		    z-index: 100; 
		    display:none;
	    }
	    #loadingDiv img	{
			user-select: none;
	    }
	</style>
	<script type="text/javascript">
		var isConnected = false;
		var websocket;
		var timer;
		var searchDate;
		var pollingSwitch = "${fns:getConfig('page.message.polling')}";
		// 消息url连接符号
		var connSymbol;
		$(document).ready(function() {
			messageBoxRefresh();
			$.ajaxSetup({ cache: false });
			// <c:if test="${tabmode eq '1'}"> 初始化页签
			$.fn.initJerichoTab({
                renderTo: '#right', uniqueId: 'jerichotab',
                contentCss: { 'height': $('#right').height() - tabTitleHeight },
                tabs: [], loadOnce: true, tabWidth: 110, titleHeight: tabTitleHeight
            });//</c:if>
            $("#left,#openClose").show();
            $("#left .accordion").hide();
            var menuId = "#menu-left";
            $(menuId).show();
            
			// 获取二级菜单数据
			$.get("${ctx}/sys/menu/tree", function(data){
				if (data.indexOf("id=\"loginForm\"") != -1){
					alert('您已登录超时或在其他地点登录，请重新登录！');
					top.location = "${ctx}";
					return false;
				}
				$("#left .accordion").hide();
				$("#left").append(data);
				// 链接去掉虚框
				$(menuId + " a").bind("focus",function() {
					if(this.blur) {this.blur()};
				});
				// 二级标题
				$(menuId + " .accordion-heading a").click(function(){
					$(menuId + " .accordion-toggle").removeClass('active');
					
					// 重置图标
					var menuList = ${fns:toJson(fns:getMenuList())};
					for(var index in menuList) {
						if(menuList[index].parentId == '1' &&menuList[index].isShow =='1') {
							$("#image-"+menuList[index].id).attr("src","${ctxStatic}/images/ico-new-"+menuList[index].id+".png");
						}
					}
					
					$($(menuId + " .accordion-toggle i").attr('data-href')).removeClass('in')
					if(!$($(this).attr('data-href')).hasClass('in')){
						/* $(this).children("i").removeClass('icon-chevron-right').addClass('icon-chevron-down'); */
						$(this).addClass("active");
						
						// 设定选中图标
						var index1 = $(this).attr('data-href').split('-');
						if(index1.length>1) {
							$("#image-"+index1[1]).attr("src","${ctxStatic}/images/ico-new-"+index1[1]+"-1.png");
						}
					}
				});
				// 二级内容
				$(menuId + " .accordion-body a").click(function(){
					$(menuId + " li").removeClass("active");
					$(menuId + " li i").removeClass("");
					$(this).parent().addClass("active");
					$(this).children("i").addClass("");
				});
				// 展现三级
				$(menuId + " .accordion-inner a").click(function(){
				    var href = $(this).attr("data-href");
					if($(href).length > 0){
						$(href).toggle().parent().toggle();
						return false;
					}
					// <c:if test="${tabmode eq '1'}"> 打开显示页签
					return addTab($(this)); // </c:if>
				});
				// 默认选中第一个菜单
				//$(menuId + " .accordion-heading:first a").click();
				$(menuId + " .accordion-body a:first ").click();
				$(menuId + " .accordion-body li:first a:first i").click();
			});
			/* // 绑定菜单单击事件
			$("#menu a.menu").click(function(){
				// 一级菜单焦点
				$("#menu li").removeClass("menu_ico_active");
				$(this).parent().addClass("menu_ico_active");
				
				// 左侧区域隐藏
				if ($(this).attr("target") == "mainFrame"){
					//$("#left,#openClose").show();
					wSizeWidth();
					// <c:if test="${tabmode eq '1'}"> 隐藏页签
					$(".jericho_tab").hide();
					$("#mainFrame").show();//</c:if>
					return true;
				}
				// 左侧区域显示
				$("#left,#openClose").show();
				if(!$("#openClose").hasClass("close")){
					$("#openClose").click();
				}
				// 显示二级菜单
				var menuId = "#menu-" + $(this).attr("data-id");
				if ($(menuId).length > 0){
					$("#left .accordion").hide();
					$(menuId).show();
					// 初始化点击第一个二级菜单
					if (!$(menuId + " .accordion-body:first").hasClass('in')){
						$(menuId + " .accordion-heading:first a").click();
					}
					if (!$(menuId + " .accordion-body li:first ul:first").is(":visible")){
						$(menuId + " .accordion-body a:first i").click();
					}
					// 初始化点击第一个三级菜单
					$(menuId + " .accordion-body li:first li:first a:first i").click();
				}else{
					// 获取二级菜单数据
					$.get($(this).attr("data-href"), function(data){
						if (data.indexOf("id=\"loginForm\"") != -1){
							alert('您已登录超时或在其他地点登录，请重新登录！');
							top.location = "${ctx}";
							return false;
						}
						$("#left .accordion").hide();
						$("#left").append(data);
						// 链接去掉虚框
						$(menuId + " a").bind("focus",function() {
							if(this.blur) {this.blur()};
						});
						// 二级标题
						$(menuId + " .accordion-heading a").click(function(){
							$(menuId + " .accordion-toggle i").removeClass('icon-chevron-down').addClass('icon-chevron-right');
							if(!$($(this).attr('data-href')).hasClass('in')){
								$(this).children("i").removeClass('icon-chevron-right').addClass('icon-chevron-down');
							}
						});
						// 二级内容
						$(menuId + " .accordion-body a").click(function(){
							$(menuId + " li").removeClass("active");
							$(menuId + " li i").removeClass("icon-white");
							$(this).parent().addClass("active");
							$(this).children("i").addClass("icon-white");
						});
						// 展现三级
						$(menuId + " .accordion-inner a").click(function(){
							var href = $(this).attr("data-href");
							if($(href).length > 0){
								$(href).toggle().parent().toggle();
								return false;
							}
							// <c:if test="${tabmode eq '1'}"> 打开显示页签
							return addTab($(this)); // </c:if>
						});
						// 默认选中第一个菜单
						$(menuId + " .accordion-body a:first i").click();
						$(menuId + " .accordion-body li:first li:first a:first i").click();
					});
				}
				// 大小宽度调整
				wSizeWidth();
				return false;
			}); */
			// 初始化点击第一个一级菜单
			$("#menu a.menu:first span").click();
			// <c:if test="${tabmode eq '1'}"> 下拉菜单以选项卡方式打开
			$("#userInfo .dropdown-menu a").mouseup(function(){
				return addTab($(this), true);
			});// </c:if>
			// 鼠标移动到边界自动弹出左侧菜单
			$("#openClose").mouseover(function(){
				if($(this).hasClass("open")){
					$(this).click();
				}
			});
			if("${fns:getConfig('sys.message.open')}"=='true'){
				//过滤机构类型为6的  并 具有消息查看权限的 可以查看消息盒子 hzy  2020/04/14
				if("${fns:getUser().office.type}"=='6' || "${fns:getUser().office.type}"=='7' ){
					<shiro:hasPermission name="sys:message:view">
						if("${fns:getConfig('page.communication.type')}"=='01'){
							if (isConnected == false) {
				            	initWebSocket();
				            }
						} else if("${fns:getConfig('page.communication.type')}"=='02') {
							switchButtonOpen();
							timer = setInterval(function() {
			    				if($(".tip-con").css("display")!="none" && $("#connectSockStatus").val() == "true"){
			    					messageBoxRefresh();
			    					try{ 
			                			if(typeof(eval($("#mainFrame")[0].contentWindow.todoBoxRefresh))=="function") {
			                				$("#mainFrame")[0].contentWindow.todoBoxRefresh();
			                			}
			                		}catch(e){}
			    				}
			    			}, 10000);
						}
					</shiro:hasPermission>
				}
			}
		});
		// websocket处理方法
		function initWebSocket() {
			/* 注释之前servContext路径  hzy 2020/04/14 start  */
        	/* var serverContext = "${severScheme}" + "://" + "${severName}" + ":" + "${serverPort}" + "${contextPath}";
            var urlSockjs = serverContext + "/sockjs/webSocketServer";
            websocket = new SockJS(urlSockjs); */
			/* 注释之前servContext路径  hzy 2020/04/14 end */
			/* servContext路径   hzy 2020/04/14 start */
			var serverContext;
        	if(window.location.protocol==="https:"){
        		 serverContext = "https" + "://" + "${severName}" + "${contextPath}";        		
        	}else{
        		 serverContext ="${severScheme}" + "://" + "${severName}" + ":" + "${serverPort}" + "${contextPath}";
        	}
            var urlSockjs = serverContext + "/sockjs/webSocketServer";
            websocket = new SockJS(urlSockjs);
            /* servContext路径  hzy 2020/04/14 end */
            // 打开时
            websocket.onopen = function(evnt) {
            	isConnected = true;
            	switchButtonOpen();
            	clearInterval(timer);
            };

            // 处理消息时
            websocket.onmessage = function(evnt) {
            	$(".tip-num").css("display","block");
            	var result =eval('('+ evnt.data+')');
            	if(result.type=='carTrack'){
            		var location = [parseFloat(result.longitude),parseFloat(result.latitude)];	
            		try{ 
            			if(typeof(eval($("#mainFrame")[0].contentWindow.realLineMoveTo))=="function") {
            				$("#mainFrame")[0].contentWindow.realLineMoveTo(result.carNo, location, result.speed);
            			}
            		}catch(e){} 	
            	}
            	if(result.type=='sysMessage'){
            		wsMessageExcuete(result);
            	}
            };

            websocket.onerror = function(evnt) {
            	isConnected = false;
            	if(pollingSwitch=='true'){
            		timer = setInterval(function() {
        				if($(".tip-con").css("display")!="none"){
        					messageBoxRefresh();
        					try{ 
                    			if(typeof(eval($("#mainFrame")[0].contentWindow.todoBoxRefresh))=="function") {
                    				$("#mainFrame")[0].contentWindow.todoBoxRefresh();
                    			}
                    		}catch(e){}
        				}
        			}, 10000);
            	}        	
            };

            websocket.onclose = function(evnt) {
            	isConnected = false;
            	switchButtonClose();
            };

            window.close=function(){
            	isConnected = false;
				websocket.onclose();
            }	
        }
		// 断开websocket连接
		function disconnect() {  
            if (websocket != null) {  
            	websocket.close();
            	websocket = null;  
            }
            searchDate = null;// 重新打开开关重新获取消息数据
            switchButtonClose();
            isConnected = false;
        }
		// <c:if test="${tabmode eq '1'}"> 添加一个页签
		function addTab($this, refresh){
			$(".jericho_tab").show();
			$("#mainFrame").hide();
			$.fn.jerichoTab.addTab({
                tabFirer: $this,
                title: $this.text(),
                closeable: true,
                data: {
                    dataType: 'iframe',
                    dataLink: $this.attr('href')
                }
            }).loadData(refresh);
			return false;
		}// </c:if> 
		// 右下角弹窗
		function showHideDiv(){
			if($(".tip-con").css("display")!="none"){
				$(".tip-con").css("display","none");
			  	$(".tip-min").css("display","block");
			}else{
				$(".tip-con").css("display","block");
			  	$(".tip-min").css("display","none");
			}
		}
		//接收websocket消息时处理方法
		function wsMessageExcuete(result){
			if($('#downmsg_content').children().length==0){
				$("#mainFrame").contents().find("#todo").empty();
			}
			if(result.url==null){
        		$("#mainFrame").contents().find("#todo").prepend("<ul><a onclick='readMessage(this.href);return false' href='#?menuId="
						+ result.menuId +"&id="
						+ result.id +"'><li>"
						+ result.messageTopic +"</li><li>"+result.createName+"<span style='display:inline-block;float:right;'>"+result.createDate+"</span></li></a></ul>");
            	$("#downmsg_content").prepend("<ul><a onclick='readMessage(this.href);return false' href='#?menuId="
						+ result.menuId +"&id="
						+ result.id +"'><li>"
						+ result.messageTopic +"</li><li>"+result.createName+"<span>"+result.createDate+"</span></li></a></ul>");
        	}else{
        		connSymbol=(result.url.indexOf('?')>0) ? '&' : '?';
        		/* $("#mainFrame").contents().find("#todo").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='/frame/main"
						+ result.url +connSymbol+"menuId="
						+ result.menuId +"&id="
						+ result.id +"'><li>"
						+ result.messageTopic +"</li><li>"+result.createName+"<span style='display:inline-block;float:right;'>"+result.createDate+"</span></li></a></ul>");
            	$("#downmsg_content").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='/frame/main"
						+ result.url +connSymbol+"menuId="
						+ result.menuId +"&id="
						+ result.id +"'><li>"
						+ result.messageTopic +"</li><li>"+result.createName+"<span>"+result.createDate+"</span></li></a></ul>");
        		}  	 */
        		/* 更改 消息盒子内容布局 样式    hzy 2020/04/14 start */
        		console.log("result.messageTopic"+result.toString());
        		if(result.messageTopic.indexOf("[机具报警]") != -1  ||result.messageTopic.indexOf("[定时日结完成]") != -1){
        			$("#mainFrame").contents().find("#todo").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
    						+ result.url +connSymbol+"menuId="
    						+ result.menuId +"&id="
    						+ result.id +"'><li class='tip-con-first'>"	
    						+ result.messageTopic.substring(0,result.messageTopic.indexOf(']')+1) +"</li><li class='tip-con-first'>"+ result.messageTopic.substring(result.messageTopic.indexOf(']')+1) +"</li><li>"+result.createName+"<span style='display:inline-block;float:right;'>"+result.createDate+"</span></li></a></ul>");
                	$("#downmsg_content").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
    						+ result.url +connSymbol+"menuId="
    						+ result.menuId +"&id="
    						+ result.id +"'><li class='tip-con-first'>"
    						+ result.messageTopic.substring(0,result.messageTopic.indexOf(']')+1) +"</li><li class='tip-con-first'>"+ result.messageTopic.substring(result.messageTopic.indexOf(']')+1) +"</li><li>"+result.createName+"<span>"+result.createDate+"</span></li></a></ul>");
        		}else{
        			$("#mainFrame").contents().find("#todo").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
    						+ result.url +connSymbol+"menuId="
    						+ result.menuId +"&id="
    						+ result.id +"'><li class='tip-con-red'>"	
    						+ result.messageTopic.substring(0,result.messageTopic.indexOf(']')+1) +"</li><li class='tip-con-first'>"+ result.messageTopic.substring(result.messageTopic.indexOf(']')+1) +"</li><li>"+result.createName+"<span style='display:inline-block;float:right;'>"+result.createDate+"</span></li></a></ul>");
                	$("#downmsg_content").prepend("<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
    						+ result.url +connSymbol+"menuId="
    						+ result.menuId +"&id="
    						+ result.id +"'><li class='tip-con-red'>"
    						+ result.messageTopic.substring(0,result.messageTopic.indexOf(']')+1) +"</li><li class='tip-con-first'>"+ result.messageTopic.substring(result.messageTopic.indexOf(']')+1) +"</li><li>"+result.createName+"<span>"+result.createDate+"</span></li></a></ul>");	
        		}
        		
			}
        	/* 更改 消息盒子内容布局 样式    hzy 2020/04/14 end */
        	/* $("#downmsg_title").html("系统消息("+$('#downmsg_content').children().length+")<a href='javascript:showHideDiv()'>—</a>"); */
        	messageLength();//消息数值显示
		}
		/* 添加消息盒子中消息数值显示    hzy 2020/04/14 */
		function messageLength(){
			if($('#downmsg_content').children().length > 99){
				$("#downmsg_title").html("系统消息(99+)<a href='javascript:showHideDiv()'>—</a>");
			}else{
	        	$("#downmsg_title").html("系统消息("+$('#downmsg_content').children().length+")<a href='javascript:showHideDiv()'>—</a>");
			}
		}
		
		// 忽略全部消息
		function readAllMessage(){
			$.ajax({
				url : '${ctx}/sys/message/readAllMessage',
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : false,
				cache : false
			});
			$("#downmsg_content").text("");
			$("#mainFrame").contents().find("#todo").empty();
			$(".tip-num").css("display","none");
			$("#downmsg_title").html("系统消息("+$("#downmsg_content").children().length+")<a href='javascript:showHideDiv()'>—</a>");
		}
		// 读取消息
		function readMessage(href){
			var urlStart = href.indexOf("${ctx}");
			var urlEnd = href.indexOf("?");
			var menuId = href.substr(href.indexOf("menuId=")+7,2);
			if(urlEnd-urlStart!=11){
				if(!$("#collapse-"+menuId).hasClass("in")){
					if(!$("#image-"+menuId).parent().hasClass("active")){
						$("#image-"+menuId).click();
					}
				}
				$("[href*='${ctx}']").parent().removeClass("active");
				//查找href中是否带机构树
				if($("[href*='/page/a?menuname="+href.substring(urlStart+'${ctx}'.length,urlEnd)+"']").parent().length === 1){
					//有机构树高亮显示
					$("[href*='/page/a?menuname="+href.substring(urlStart+'${ctx}'.length,urlEnd)+"']").parent().addClass("active");
					clickSelect();
					mainFrame.location.href="${ctx}/page/a?menuname="+href.substring(urlStart+'${ctx}'.length,urlEnd);	
				}else{
					//无机构树高亮显示
					$("[href*='"+href.substring(urlStart,urlEnd)+"']").parent().addClass("active");	
					clickSelect();
					mainFrame.location.href=href					
				}
			}		
			if(href.indexOf("&id=")>0){
				var messageId = href.substring(href.indexOf("&id=")+4);
				$.ajax({
					url : '${ctx}/sys/message/readMessage?messageId='+messageId,
					type : 'Post',
					contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
					async : false,
					cache : false
				});
			}
			$("[href*="+messageId+"]").parent().remove();
			$("#mainFrame").contents().find("[href*="+messageId+"]").parent().remove();
			if($("#downmsg_content").children().length){
				$(".tip-num").css("display","block");
			}else{
				$(".tip-num").css("display","none");
			}
			messageLength();
		}
		// 初始登录获取消息
		function messageBoxRefresh(){
			$.ajax({
				url : '${ctx}/sys/message/boxList?searchDate=' + searchDate,
				type : 'Post',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				async : false,
				cache : false,
				dataType : 'json',
				success : function(res) {
					if(searchDate == null){
						$("#downmsg_content").text("");
						$("#mainFrame").contents().find("#todo").empty();
					}
					searchDate = res.searchDate;
					for(var i=0;i<res.idList.length;i++){
						if(res.urlList[i]==null){
							$("#downmsg_content").append(
									"<ul><a onclick='readMessage(this.href);return false' href='#?menuId="
									+ res.menuIdList[i] +"&id="
									+ res.idList[i] +"'><li class='tip-con-first'>"
									+ res.messageTopicList[i] +"</li><li>"+res.createNameList[i]+"<span>"+res.createDateList[i]+"</span></li></a></ul>");							
						}else{
							connSymbol=(res.urlList[i].indexOf('?')>0) ? '&' : '?';
							/* $("#downmsg_content").append(
									"<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
									+ res.urlList[i] +connSymbol+"menuId="
									+ res.menuIdList[i] +"&id="
									+ res.idList[i] +"'><li class='tip-con-first'>"
									+ res.messageTopicList[i] +"</li><li>"+res.createNameList[i]+"<span>"+res.createDateList[i]+"</span></li></a></ul>"); */	
							/*更改消息盒子布局样式  hzy 2020-04-14 start  */
							if(res.messageTopicList[i].indexOf("[机具报警]") != -1 ||res.messageTopicList[i].indexOf("[定时日结完成]") != -1){
								$("#downmsg_content").prepend(
										"<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
										+ res.urlList[i] +connSymbol+"menuId="
										+ res.menuIdList[i] +"&id="
										+ res.idList[i] +"'><li class='tip-con-first'>"
									    + res.messageTopicList[i].substring(0,res.messageTopicList[i].indexOf(']')+1) +"</li><li class='tip-con-first'>"+ res.messageTopicList[i].substring(res.messageTopicList[i].indexOf(']')+1) +"</li><li>"+res.createNameList[i]+"<span>"+res.createDateList[i]+"</span></li></a></ul>");	
							}else{
								$("#downmsg_content").prepend(
										"<ul><a onclick='readMessage(this.href);return false' target='mainFrame' href='${ctx}"
										+ res.urlList[i] +connSymbol+"menuId="
										+ res.menuIdList[i] +"&id="
										+ res.idList[i] +"'><li class='tip-con-red'>"
									    + res.messageTopicList[i].substring(0,res.messageTopicList[i].indexOf(']')+1) +"</li><li class='tip-con-first'>"+ res.messageTopicList[i].substring(res.messageTopicList[i].indexOf(']')+1) +"</li><li>"+res.createNameList[i]+"<span>"+res.createDateList[i]+"</span></li></a></ul>");		
							}
							/*更改消息盒子布局样式  hzy 2020-04-14 end  */
						}
					}
					messageLength();
					if($("#downmsg_content").children().length){
						$(".tip-num").css("display","block");
					}else{
						$(".tip-num").css("display","none");
					}
				}
			});
		}
	</script>
</head>
<body>
	<div id="main">
		<div id="header" class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="brand"><span id="productName">${fns:getConfig('productFullName')}</span></div>
			<%--	<c:choose>
					<c:when test="${fns:getUser().office.type == fns:getConfig('office.type.pboc')}">
						<div class="brand" style="background-image: url(${ctxStatic}/images/name.png);"><span id="productName"></span></div>
					</c:when>
					<c:otherwise>
						<div class="brand" style="background-image: url(${ctxStatic}/images/name_bank.png);"><span id="productName"></span></div>
					</c:otherwise>
				</c:choose> --%>
				
				<ul id="userControl" class="nav pull-right">
				<%--	<li id="themeSwitch" class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="页签模式"><i class="icon-th-large"></i></a>
						<ul class="dropdown-menu">
							<li><a href="javascript:cookie('tabmode','${tabmode eq '1' ? '0' : '1'}');location=location.href">${tabmode eq '1' ? '关闭' : '开启'}页签模式</a></li>
							<!-- <li><A href="tencent://message/?uin=939048409&amp;Site=有事Q我&amp;Menu=yes"><img style="border:0px;" src=http://wpa.qq.com/pa?p=1:939048409:1></a></li> -->
						</ul>
						<!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
					</li>--%>
					<li class="off_sel"><a href="#"><i class="fa  fa-bank  text-red"></i>&nbsp;归属机构：${fns:getUser().office.name}</a></li>
					<li id="userInfo" class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="个人信息"><i class="fa fa-user  text-red"></i>&nbsp;&nbsp;您好, ${fns:getUser().name}&nbsp;<span id="notifyNum" class="label label-info hide"></span></a>
						<ul class="dropdown-menu">
							<li><a href="${ctx}/sys/user/info" target="mainFrame"><i class="icon-user"></i>&nbsp; 个人信息</a></li>
							<li><a href="${ctx}/sys/user/modifyPwd" target="mainFrame"><i class="icon-lock"></i>&nbsp;  修改密码</a></li>
							<shiro:hasPermission name="sys:user:veins">
								<li><a href="${ctx}/store/v01/stoEscortInfo/acquisitionVeins?userId=${fns:getUser().id}" target="mainFrame"><i class="icon-hand-up"></i>&nbsp;  静脉采集</a></li>
							</shiro:hasPermission>
							<%-- <li><a href="${ctx}/oa/oaNotify/self" target="mainFrame"><i class="icon-bell"></i>&nbsp;  我的通知 <span id="notifyNum2" class="label label-info hide"></span></a></li> --%>
						</ul>
					</li>
					<li><a href="${ctx}/logout" onclick="disconnect();" title="退出登录"><i class="fa fa-sign-out  text-red"></i>&nbsp;退出</a></li>
				</ul>

				<div class="nav-collapse">
					<ul id="menu" class="nav" style="*white-space:nowrap;float:none;">
						<c:set var="firstMenu" value="true"/>
						<c:forEach items="${fns:getMenuList()}" var="menu" varStatus="idxStatus">
							<c:if test="${menu.parent.id eq '1'&&menu.isShow eq '1'}">
						
								<li class="menu_ico ${not empty firstMenu && firstMenu ? ' active' : ''}">
									<c:if test="${empty menu.href}">
										<%-- <a class="menu" href="javascript:" data-href="${ctx}/sys/menu/tree?parentId=${menu.id}" data-id="${menu.id}">
										<img src="${ctxStatic}/images/ico_${menu.id}.png"/><br /><span>${menu.name}</span>
										</a> --%>
									</c:if>
									<c:if test="${not empty menu.href}">
										<%-- <a class="menu" href="${fn:indexOf(menu.href, '://') eq -1 ? ctx : ''}${menu.href}" data-id="${menu.id}" target="mainFrame">
											<c:if test="${'02' eq menu.id}">
												<img src="${ctxStatic}/images/ico_${menu.id}.png"/><br />
											</c:if>
											<span>${menu.name}</span>
										</a> --%>
									</c:if>
								</li>
								<c:if test="${firstMenu}">
									<c:set var="firstMenuId" value="${menu.id}"/>
								</c:if>
								<c:set var="firstMenu" value="false"/>
							</c:if>
						</c:forEach>
					</ul>
				</div>
			</div>
	    </div>
	    <div class="container-fluid">
			<div id="content" class="row-fluid">
				<div id="left">
				<div class="logo-con"></div>
				<%-- 
					<iframe id="menuFrame" name="menuFrame" src="" style="overflow:visible;" scrolling="yes" frameborder="no" width="100%" height="650"></iframe> --%>
				</div>
				<div id="openClose" class="close">&nbsp;</div>
				<div id="right">
						<div style="padding:10px 0px 0px 0px;">
						<iframe id="mainFrame" name="mainFrame" src="" style="overflow:visible;box-sizing: border-box;" scrolling="yes" frameborder="no" width="100%" height="650" ></iframe>							
						</div>
				</div>
				<c:if test="${fns:getConfig('sys.message.open')=='true' && (fns:getUser().office.type == '6' || fns:getUser().office.type == '7')}">
				<shiro:hasPermission name="sys:message:view">
				<div class="tip-min" onclick="javascript:showHideDiv()" style="display: none"><i class="fa-volume-up fa fa-lg"></i><span class="tip-num" style="display: none"></span></div>
				<div class="tip-con" style="display: block">
				<h4 id="downmsg_title"></h4>
				<div id="downmsg_content" style="margin-bottom:20px;max-height:250px;overflow-y:auto;overflow-x: hidden;">
				</div>
				<h5><span style="float:left;position: relative;margin-left:10px;margin-right:10px;">通知开关</span>
				<sys:switchConButton />
				<a onclick='readAllMessage()' href="#">忽略全部</a>
				<a target="mainFrame" onclick='readMessage(this.href);return false' href="${ctx}/sys/message/?menuId=98">查看全部</a></h5>
				</div>
				</shiro:hasPermission>
				</c:if>
			</div>
			<!-- 2020-09-29新加加载中 -->
			<div id="loadingDiv">
				<img src="${ctxStatic}/images/lndexloading.gif" style="margin-top: 12%; margin-left: auto; width: 23%; margin-right: auto; display: block; position: relative;left: -6%;}" /> 
				<!-- <div style="width: 100%; text-align: center; font-size: 20px; position: absolute; margin-top: -5%; left: -6%;">数据加载中</div>  -->
			</div>
			
		    <div id="footer" class="row-fluid">
	            <!--  Copyright &copy; 2017-${fns:getConfig('copyrightYear')} ${fns:getConfig('productName')} - Powered By ${fns:getConfig('author')} ${fns:getConfig('version')} -->
	            ${fns:getConfig('author')}版权所有&nbsp;&nbsp;版本：${fns:getConfig('productName')}&nbsp${fns:getConfig('version')}
			</div>
		</div>
		  
	</div>
	<script type="text/javascript"> 
		var leftWidth = 190; // 左侧窗口大小
		var tabTitleHeight = 33; // 页签的高度
		var htmlObj = $("html"), mainObj = $("#main");
		var headerObj = $("#header"), footerObj = $("#footer");
		var frameObj = $("#left, #openClose, #right, #right iframe");
		function wSize(){
			var minHeight = 500, minWidth = 980;
			var strs = getWindowSize().toString().split(",");
			htmlObj.css({"overflow-x":strs[1] < minWidth ? "auto" : "hidden", "overflow-y":strs[0] < minHeight ? "auto" : "hidden"});
			mainObj.css("width",strs[1] < minWidth ? minWidth - 10 : "auto");
			frameObj.height((strs[0] < minHeight ? minHeight : strs[0]) - headerObj.height() - footerObj.height() - (strs[1] < minWidth ? 42 : 28));
			$("mainFrame").height(frameObj.height());
			$("#openClose").height($("#openClose").height() - 5);// <c:if test="${tabmode eq '1'}"> 
			$(".jericho_tab iframe").height($("#right").height() - tabTitleHeight; // </c:if>
			$("#loadingDiv").height($("#right").height());
			wSizeWidth();
		}
		function wSizeWidth(){
			if (!$("#openClose").is(":hidden")){
				var leftWidth = ($("#left").width() <= 0 ? 0 : 190);
				$("#right").width($("#content").width()- leftWidth - $("#openClose").width() -5);
			}else{
				$("#right").width("100%");
			}
		}// <c:if test="${tabmode eq '1'}"> 
		function openCloseClickCallBack(b){
			$.fn.jerichoTab.resize();
		} // </c:if>
		//2020-09-29显示加载（菜单id）
		function clickallMenuTree(id){
			// 依次：中心总账、公司账务、客户账务、财务日结、门店存款、缴存全景、现在使用记录、历史更换记录、历史使用记录、存款时间分析报表、存款情况分析报表、中心首页、商户首页、门店首页、清点差错、款箱拆箱、管理分析报表
			if(id == "510101" || id == "510201" || id == "510301" || id == "510401" || id == "380101" || id == "380123" || id == "380121" || id == "380122"
				|| id == "380120" || id == "900120" || id == "900121" || id == "0203" || id == "0204" || id == "0205" || id == "380114" || id == "380105"
				|| id == "900118"){
				$("#loadingDiv").fadeTo(200,0.5);
			}else{
				$("#loadingDiv").fadeOut(200);
			}
		}
		// 2020-09-29显示加载动画
		function clickSelect(){
			$("#loadingDiv").fadeTo(200,0.5);
		}
		// 2020-09-29隐藏加载
		function clickallMenuTreeFadeOut(){
			 $("#loadingDiv").fadeOut(200);
		}
	</script>
	<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
</body>
</html>