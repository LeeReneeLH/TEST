<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>动态延展路径</title>
<style>
html, body, #container {
	width: 100%;
	height: 100%;
	margin: 0px;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		init();
	});
	function init() {
		var url = "${ctx}/AMap/cancel";
		var pointList;
		
		$.ajax({
			type : "POST",
			url : url,
			success : function(resp) {
				pointList = resp.data;
				
			}
		});

	}
</script>
</head>

<body>
	<div id="container"></div>
	<script type="text/javascript"
		src="https://webapi.amap.com/maps?v=1.4.0&key=0c28b8ad235f48ef73c1b987de018e81"></script>
	<!-- UI组件库 1.0 -->
	<script src="//webapi.amap.com/ui/1.0/main.js?v=1.0.11"></script>
	<script type="text/javascript">
		var lineArr = [];
		<c:forEach items="${positionList}" var="position" varStatus="status">
		var strs = new Array(); //定义一数组 
		strs = "${position}".split(","); //字符分割 
		lineArr.push(strs);
		</c:forEach>
		
		map = new AMap.Map("container", {
			resizeEnable : true,
			center : [ 116.397428, 39.90923 ],
			zoom : 14
		});
		map.plugin([ "AMap.ToolBar" ], function() {
			//加载工具条
			var tool = new AMap.ToolBar();
			map.addControl(tool);
		});

		//绘制轨迹  
		var polyline = new AMap.Polyline({
			map : map,
			path : lineArr,
			strokeColor : "#00A",//线颜色  
			strokeOpacity : 1,//线透明度  
			strokeWeight : 3,//线宽  
			strokeStyle : "solid",//线样式  
		});
	</script>

</body>
</html>
