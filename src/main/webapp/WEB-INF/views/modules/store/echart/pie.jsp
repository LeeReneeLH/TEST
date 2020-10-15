<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.box.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart/echarts.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		//makePie();
	});
	function makeChart () {
        var main = document.getElementById('main');
        var div = document.createElement('div');
        var width = document.body.clientWidth;
        div.style.cssText = width + 'px; height:400px';
        main.appendChild(div);
        return echarts.init(div);
    }
	
	function makePie() {
        var chart = makeChart();
        var title;
        var data;
        $.ajax({
			url : '${ctx}/store/v01/echart/getData',
			type : 'Post',
			data : '',
			dataType : 'json',
			async : false,
			cache : false,
			success : function(res) {
				title = res.title;
				data = res.data;
				
			},
			error : function() {
				return null;
			}
		});
        chart.setOption({
            legend: {
                //data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
                data:title
            },
            tooltip: {
            	trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            series: [{
                name: '库房箱袋统计',
                type: 'pie',
                selectedMode: 'single',
                hoverAnimation: false,
                selectedOffset: 30,
                clockwise: true,
                data:data
            }]
        });
    }
	setTimeout(function () {
        makePie();
    }, 1500);
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/store/v01/echart/pie">饼图</a></li>
	</ul>
	 <div id="main"></div>
</body>
</html>
