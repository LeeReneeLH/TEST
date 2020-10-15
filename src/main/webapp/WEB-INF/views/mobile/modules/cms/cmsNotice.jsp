<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%><!DOCTYPE >
<html>
<head>
    <meta charset="utf-8">
    <title>${fns:getConfig('productName')}</title>
    <!-- <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no"> -->
    <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
    
   	<link rel="stylesheet" type="text/css" href="${ctxStatic}/bootstrap/3.3.5/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/common/myScroll.css">

	<script type='text/javascript' src="${ctxStatic}/jquery/jquery-1.11.3.min.js"></script>
	<script type='text/javascript' src="${ctxStatic}/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<script type='text/javascript' src="${ctxStatic}/common/iscroll-probe.js"></script>
	<script type='text/javascript' src="${ctxStatic}/common/common.js"></script>
	<style>
		.btn-group button {
			border-radius: 0;
		}
	</style>
</head>
<body>
<!--注释掉原因：Container中设置了padding属性，Padding-left和Padding-right均为15px-->
<!--<div class="container">-->
<nav class="navbar navbar-fixed-top bg-primary" id="navDiv">
	<ul class="nav nav-pills">
		<div class="container">
			<div class="row">
				<li><h4 class="text-center">物流信息公告平台</h4></li>
			</div>
		</div>
	</ul>
</nav>
<div class="carousel slide" id="myCarousel">
</div>
<div id="wrapper">
	<div id="scroller">
		<div id="pullDown">
			<div class="pullDownIcon"></div>
			<div class="pullDownLabel">下拉刷新</div>
		</div>
		<ul id="listview" class="list-group">
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/2896/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科拉
					</h4>

					<p style="margin-left: 75px">
						百公里9升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/3554/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科威
					</h4>

					<p style="margin-left: 75px">
						百公里10升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/592/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科雷
					</h4>

					<p style="margin-left: 75px">
						百公里12升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/2896/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科拉
					</h4>

					<p style="margin-left: 75px">
						百公里9升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/3554/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科威
					</h4>

					<p style="margin-left: 75px">
						百公里10升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/592/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科雷
					</h4>

					<p style="margin-left: 75px">
						百公里12升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/2896/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科拉
					</h4>

					<p style="margin-left: 75px">
						百公里9升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/3554/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科威
					</h4>

					<p style="margin-left: 75px">
						百公里10升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/592/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科雷
					</h4>

					<p style="margin-left: 75px">
						百公里12升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/2896/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科拉
					</h4>

					<p style="margin-left: 75px">
						百公里9升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/3554/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科威
					</h4>

					<p style="margin-left: 75px">
						百公里10升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/592/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科雷
					</h4>

					<p style="margin-left: 75px">
						百公里12升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/2896/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科拉
					</h4>

					<p style="margin-left: 75px">
						百公里9升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/3554/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科威
					</h4>

					<p style="margin-left: 75px">
						百公里10升
					</p>
				</a>
			</li>
			<li class="list-group-item">
				<a href="http://m.autohome.com.cn/592/#pvareaid=101353">
					<img src="../image/car_logo/42.png" style="width: 64px;float:left;">
					<h4 style="margin-left: 75px">
						别克-昂科雷
					</h4>

					<p style="margin-left: 75px">
						百公里12升
					</p>
				</a>
			</li>
		</ul>
		<div id="pullUp">
			<div class="pullUpIcon"></div>
			<div class="pullUpLabel">上拉显示更多...</div>
		</div>
	</div>
</div>
	<div class="navbar-fixed-bottom btn-group btn-group-justified">
		<div class="btn-group" role="group">
			<button type="button" class="btn btn-primary btn-lg active">
				<span class="glyphicon glyphicon-bullhorn" aria-hidden="true"></span>
				需求公告
			</button>
		</div>
		<div class="btn-group" role="group">
			<button type="button" class="btn btn-primary btn-lg">
				<span class="glyphicon glyphicon-flag" aria-hidden="true"></span>
				中标信息
			</button>
		</div>
	</div>
</body>

<script type="text/javascript">
	$(function () {
		setDivTop();

		loaded();
	});

	// 设置各块DIV距顶高度
	function setDivTop() {
		var navDiv = document.getElementById('navDiv');
		var myCarousel = document.getElementById('myCarousel');
		//设置DIV的高度
		myCarousel.style.marginTop = navDiv.offsetHeight + 'px';

		var wrapper = document.getElementById('wrapper');
		//设置DIV的高度
		wrapper.style.top = navDiv.offsetHeight + myCarousel.offsetHeight + 'px';
	}

	var myScroll;
	var pullDownEl, pullDownL;
	var pullUpEl, pullUpL;
	var downCount = 0, upCount = 0;
	var loadingStep = 0;//加载状态0默认，1显示加载状态，2执行加载数据，只有当为0时才能再次加载，这是防止过快拉动刷新

	function pullDownAction() {//下拉事件
		setTimeout(function () {
			var el, li, i;
			el = $('#listview');
			for (i = 0; i < 3; i++) {
				li = $("<li></li>");
				downCount++;
				li.text('new Add ' + downCount + " ！");
				el.prepend(li);
			}
			pullDownEl.removeClass('loading');
			pullDownL.html('下拉显示更多...');
			pullDownEl['class'] = pullDownEl.attr('class');
			pullDownEl.attr('class', '').hide();
			myScroll.refresh();
			loadingStep = 0;
		}, 1000); //1秒
	}
	function pullUpAction() {//上拉事件
		setTimeout(function () {
			var el, li, i;
			el = $('#listview');
			for (i = 0; i < 3; i++) {
				li = $("<li></li>");
				upCount++;
				li.text('new Add ' + upCount + " ！");
				el.append(li);
			}
			pullUpEl.removeClass('loading');
			pullUpL.html('上拉显示更多...');
			pullUpEl['class'] = pullUpEl.attr('class');
			pullUpEl.attr('class', '').hide();
			myScroll.refresh();
			loadingStep = 0;
		}, 1000);
	}

	function loaded() {
		pullDownEl = $('#pullDown');
		pullDownL = pullDownEl.find('.pullDownLabel');
		pullDownEl['class'] = pullDownEl.attr('class');
		pullDownEl.attr('class', '').hide();

		pullUpEl = $('#pullUp');
		pullUpL = pullUpEl.find('.pullUpLabel');
		pullUpEl['class'] = pullUpEl.attr('class');
		pullUpEl.attr('class', '').hide();

		myScroll = new IScroll('#wrapper', {
			probeType: 2,//probeType：1对性能没有影响。在滚动事件被触发时，滚动轴是不是忙着做它的东西。probeType：2总执行滚动，除了势头，反弹过程中的事件。这类似于原生的onscroll事件。probeType：3发出的滚动事件与到的像素精度。注意，滚动被迫requestAnimationFrame（即：useTransition：假）。
//			scrollbars: true,//有滚动条
			mouseWheel: true,//允许滑轮滚动
//			fadeScrollbars: true,//滚动时显示滚动条，默认影藏，并且是淡出淡入效果
			bounce: true,//边界反弹
//			interactiveScrollbars: true,//滚动条可以拖动
			shrinkScrollbars: 'scale',// 当滚动边界之外的滚动条是由少量的收缩。'clip' or 'scale'.
			click: true,// 允许点击事件
//			keyBindings: true,//允许使用按键控制
			momentum: true// 允许有惯性滑动
		});
		//滚动时
		myScroll.on('scroll', function () {
			if (loadingStep == 0 && !pullDownEl.attr('class').match('flip|loading') && !pullUpEl.attr('class').match('flip|loading')) {
				if (this.y > 5) {
					//下拉刷新效果
					pullDownEl.attr('class', pullUpEl['class'])
					pullDownEl.show();
					myScroll.refresh();
					pullDownEl.addClass('flip');
					pullDownL.html('准备刷新...');
					loadingStep = 1;
				} else if (this.y < (this.maxScrollY - 5)) {
					//上拉刷新效果
					pullUpEl.attr('class', pullUpEl['class'])
					pullUpEl.show();
					myScroll.refresh();
					pullUpEl.addClass('flip');
					pullUpL.html('准备刷新...');
					loadingStep = 1;
				}
			}
		});
		//滚动完毕
		myScroll.on('scrollEnd', function () {
			if (loadingStep == 1) {
				if (pullUpEl.attr('class').match('flip|loading')) {
					pullUpEl.removeClass('flip').addClass('loading');
					pullUpL.html('Loading...');
					loadingStep = 2;
					pullUpAction();
				} else if (pullDownEl.attr('class').match('flip|loading')) {
					pullDownEl.removeClass('flip').addClass('loading');
					pullDownL.html('Loading...');
					loadingStep = 2;
					pullDownAction();
				}
			}
		});
	}

	document.addEventListener('touchmove', function (e) {
		e.preventDefault();
	}, false);

	// 跳转我页面
	function gotoMe() {
		if (localStorage.isLogin) {
//			$('#me').attr('href', 'user.html');
		} else {
			location.href = "login.html";
//			$('#me').attr('href', 'login.html');
		}
	}

	// 清除本地库（测试用）
	function clearMe() {
		localStorage.clear();
	}
</script>
</html>
