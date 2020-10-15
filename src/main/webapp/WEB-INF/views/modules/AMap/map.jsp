<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>地图</title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<style type="text/css">
body, html, #container {
	height: 100%;
	margin: 0px;
	font: 12px Helvetica, 'Hiragino Sans GB', 'Microsoft Yahei', '微软雅黑',
		Arial;
}

.info-title {
	color: white;
	font-size: 14px;
	background-color: rgba(0, 155, 255, 0.8);
	line-height: 26px;
	padding: 0px 0 0 6px;
	font-weight: lighter;
	letter-spacing: 1px
}

.info-content {
	padding: 4px;
	color: #666666;
	line-height: 23px;
}

.info-content img {
	float: left;
	margin: 3px;
}
</style>
</head>
<body>
	<div id="container" tabindex="0"></div>
	<div id='panel'></div>
	<div class="button-group">
		<input type="button" class="button" value="开始动画" id="start" />


	</div>
	<script type="text/javascript" src="${ctxStatic}/common/map.js"></script>
	<script type="text/javascript"
		src="https://webapi.amap.com/maps?v=1.4.0&key=0c28b8ad235f48ef73c1b987de018e81"></script>
	<script type="text/javascript">
   	var positionList = "${positionList}";
    var map = new AMap.Map('container',{
            resizeEnable: true,
            zoom: 14,
            mapStyle: 'amap://styles/macaron',//样式URL
            center: [121.461247,38.968423]
    });
    <!--描点-->
    <c:forEach items="${positionList}" var="position" varStatus="status">
    	var strs=new Array();//定义一数组 
    	strs="${position}".split(";");//字符分割 
    	new AMap.Marker({
    		 map:map,
    		 icon:"http://webapi.amap.com/theme/v1.3/markers/n/mark_b"+"${status.index+1}"+".png",
    		 position:strs,
    		 offset:new AMap.Pixel(-12, -36)
    	})
    </c:forEach>
	var lineArr = [];
    <c:forEach items="${positionList}" var="position" varStatus="status">
		var strs= new Array(); //定义一数组 
       	strs="${position}".split(";"); //字符分割 
       	lineArr.push(strs);
	</c:forEach>
	marker = new AMap.Marker({
        map: map,
        position: lineArr[0],
        icon: "http://webapi.amap.com/images/car.png",
        offset: new AMap.Pixel(-26, -13),
        autoRotation: true
    });
     // 绘制轨迹
     var polyline = new AMap.Polyline({
          map: map,
          path: lineArr,
          strokeColor: "#00A",  //线颜色
          // strokeOpacity: 1,     //线透明度
          strokeWeight: 3,      //线宽
          // strokeStyle: "solid"  //线样式
      });
     var passedPolyline = new AMap.Polyline({
         map: map,
         // path: lineArr,
         strokeColor: "#F00",  //线颜色
         // strokeOpacity: 1,     //线透明度
         strokeWeight: 3,      //线宽
         // strokeStyle: "solid"  //线样式
     });
     marker.on('moving',function(e){
         passedPolyline.setPath(e.passedPath);
     })
     map.setFitView();
     AMap.event.addDomListener(document.getElementById('start'), 'click', function() {
         marker.moveAlong(lineArr, 100);
     }, false);
    /* var marker = new AMap.Marker({
    		icon: "http://webapi.amap.com/theme/v1.3/markers/n/mark_b.png",
            position: strs
    });
    marker.setMap(map); */
    
    /* marker.on('click',function(e){
      infowindow.open(map,e.target.getPosition());
    }) */
   /*  AMap.plugin('AMap.AdvancedInfoWindow',function(){
       infowindow = new AMap.AdvancedInfoWindow({
        content: '<div class="info-title">高德地图</div><div class="info-content">'+
                '<img src="https://webapi.amap.com/images/amap.jpg">'+
                '高德是中国领先的数字地图内容、导航和位置服务解决方案提供商。<br/>'+
                '<a target="_blank" href = "https://mobile.amap.com/">点击下载高德地图</a></div>',
        offset: new AMap.Pixel(0, -30)
      });
      infowindow.open(map,[116.48, 39.99]);
    }) */
    AMap.plugin(['AMap.ToolBar','AMap.Scale','AMap.OverView'],function(){
        var toolBar = new AMap.ToolBar();
        var scale = new AMap.Scale();
        map.addControl(toolBar);
        map.addControl(scale);
        map.addControl(new AMap.OverView({isOpen:true}));
    })
    
   </script>
	<script type="text/javascript"
		src="https://webapi.amap.com/demos/js/liteToolbar.js"></script>
</body>
</html>
