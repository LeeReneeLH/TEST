<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<style type="text/css">
#div1 {
	width: 40px;
	height: 20px;
	border-radius: 50px;
	position: relative;
	display: block;
	float: left;
	top: 3px;
}

#div2 {
	width: 20px;
	height: 17px;
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
		var mainFrame = document.getElementById("mainFrame");
		div2.onclick = function() {
			div1.className = (div1.className == "close1") ? "open1" : "close1";
			div2.className = (div2.className == "close2") ? "open2" : "close2";
			if (div1.className == "close1" && div2.className == "close2") {
				$("#connectSockStatus").val("false");
				$("#showOpenDiv").hide();
				$("#showCloseDiv").show();
				if("${fns:getConfig('page.communication.type')}"=='01'){
					disconnect();
				} else if("${fns:getConfig('page.communication.type')}"=='02'){
					clearInterval(timer);
				}
			} else if (div1.className == "open1" && div2.className == "open2") {
				$("#connectSockStatus").val("true");
				$("#showCloseDiv").hide();
				$("#showOpenDiv").show();
				if("${fns:getConfig('page.communication.type')}"=='01'){
					initWebSocket();
					messageBoxRefresh();
					try{ 
	        			if(typeof(eval(mainFrame.contentWindow.todoBoxRefresh))=="function") {
	        				mainFrame.contentWindow.todoBoxRefresh();
	        			}
	        		}catch(e){}
				} else if("${fns:getConfig('page.communication.type')}"=='02'){
					timer = setInterval(function() {
        				if($(".tip-con").css("display")!="none"){
        					messageBoxRefresh();
        					try{ 
        	        			if(typeof(eval(mainFrame.contentWindow.todoBoxRefresh))=="function") {
        	        				mainFrame.contentWindow.todoBoxRefresh();
        	        			}
        	        		}catch(e){}
        				}
        			}, 10000);
				}
			}
		};
	});
	
	function switchButtonClose() {
		$("#connectSockStatus").val("false");
		div1.className = "close1";
		div2.className = "close2";
		$("#showOpenDiv").hide();
		$("#showCloseDiv").show();
	}
	function switchButtonOpen() {
		$("#connectSockStatus").val("true");
		div1.className = "open1";
		div2.className = "open2";
		$("#showCloseDiv").hide();
		$("#showOpenDiv").show();
	}
</script>
<div id="switchButton">
	<div id="div1" class="open1">
    	<div id="div2" class="open2"></div>
	    <div class="btn-off" id="showCloseDiv">关</div>
	    <div class="btn-on" id="showOpenDiv">开</div>
	</div>
	<input id="connectSockStatus" type="hidden" />
</div>