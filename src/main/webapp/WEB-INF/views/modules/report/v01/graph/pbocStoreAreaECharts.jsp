<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>

<head>
    <title>
        <spring:message code="store.box.manage" />
    </title>
    <meta name="decorator" content="default" />
    <link href="${ctxStatic}/common/css/echartsHome.css" rel="stylesheet">
    <script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
    <script type="text/javascript" src="${ctxStatic}/echart/echarts-all.js"></script>
    <script type="text/javascript">

		// 金库人员类型		
		var cofferUserArray = '${cofferUser}'.split(",");
		// 网点人员类型		
		var bankOutletsUserArray = '${bankOutletsUser}'.split(",");
		// 登录人员类型		
		var userType = '${userType}';

        // -------出入库 （线形图）---------------------------
        var dateLine;
        var inDateLine;
        var outDateLine;
        var chartLine;

        function makeInOutAmountLine() {

            var main = document.getElementById('grapInOutAmountLine');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartLine = echarts.init(div);

            // 调用后台，取得数据
            ajaxInOutAmountLine();

            var optionLine = ({
                grid: {
                    x: '7%'
                },
                title: {
                    text: '出入库现金量',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'axis'
                },
                /*                     legend: {
                    orient: 'vertical',
                    x: 'right',
                    data: ['出库现金量', '入库现金量']
                    }, */
                calculable: true,
                xAxis: [{
                    type: 'category',
                    axisLabel: {
                        'interval': 0
                    },
                    // data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    data: dateLine
                }],
                yAxis: [{
                    type: 'value'
                }],
                series: [{
                    name: '出库现金量',
                    type: 'line',
                    smooth:true,
                    //data: [ 11, 11, 15, 13, 12, 13, 10 ],
                    data: outDateLine,
                    markPoint: {
                        data: [{
                            type: 'max',
                            name: '最大值'
                        }, {
                            type: 'min',
                            name: '最小值'
                        }]
                    },
                    markLine: {
                        data: [{
                            type: 'average',
                            name: '平均值'
                        }]
                    }
                }, {
                    name: '入库现金量',
                    type: 'line',
                    smooth:true,
                    //data: [ 1, -2, 2, 5, 3, 2, 0 ],
                    data: inDateLine,
                    markPoint: {
                        data: [{
                            type: 'max',
                            name: '最大值'
                        }, {
                            type: 'min',
                            name: '最小值'
                        }]
                    },
                    markLine: {
                        data: [{
                            type: 'average',
                            name: '平均值'
                        }]
                    }
                }]
            });

            chartLine.setOption(optionLine);
        }

        // 调用后台组装线装数据
        function ajaxInOutAmountLine() {
            $.ajax({
                url: '${ctx}/report/v01/graph/getInOutAmountLineData?date=' + $('#searchDate').val(),
                type: 'Post',
                data: '',
                dataType: 'json',
                async: false,
                cache: false,
                success: function(res) {
                    dateLine = res.date;
                    inDateLine = res.inDate;
                    outDateLine = res.outDate;
                },
                error: function() {
                    return null;
                }
            });
        }

        // 刷新线图数据
        function refreshLineData() {
            if (!chartLine) {
                return;
            }

            // 调用后台，取得数据
            ajaxInOutAmountLine();

            //更新数据
            var optionLine = chartLine.getOption();

            // 出库数据
            optionLine.series[0].data = outDateLine;
            // 入库数据
            optionLine.series[1].data = inDateLine;
            // 区间
            optionLine.xAxis[0].data = dateLine;

            // 初始化清空图形
            chartLine.clear();
            chartLine.setOption(optionLine);
        }

        // -------网点接收（饼形图）---------------------------
        var titleInPie;
        var dataInPie;
        var chartInPie;

        function makeBankOutletsInPie() {
            var main = document.getElementById('graphBankOutletsInPie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartInPie = echarts.init(div);

            // 调用后台，取得数据
            ajaxBankOutletsPie("in");

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: '接收信息',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c}"
                },
                animation: false,
                series: [{
                    name: '接收信息',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '65%',
                    center: ['50%', '50%'],
                    data: dataInPie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartInPie.setOption(optionPie);
        }

        // 刷新网点接收数据
        function refreshBankOutletsInPieData() {
        	
            if (!chartInPie) {
                return;
            }
            
            // 调用后台，取得数据
            ajaxBankOutletsPie("in");
            
            //更新数据
            var optionPie = chartInPie.getOption();
            optionPie.series[0].data = dataInPie;
            
            // 初始化清空图形
            chartInPie.clear();
            chartInPie.setOption(optionPie);
        }

        // -------网点上缴（饼形图）---------------------------
        var titleOutPie;
        var dataOutPie;
        var chartOutPie;

        function makeBankOutletsOutPie() {
            var main = document.getElementById('graphBankOutletsOutPie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartOutPie = echarts.init(div);

            // 调用后台，取得数据
            ajaxBankOutletsPie("out");

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: '上缴信息',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{b}: {c}"
                },
                animation: false,
                series: [{
                    name: '上缴信息',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '65%',
                    center: ['50%', '50%'],
                    data: dataOutPie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartOutPie.setOption(optionPie);
        }

        // 刷新网点上缴数据
        function refreshBankOutletsOutPieData() {
        	
            if (!chartOutPie) {
                return;
            }

            // 调用后台，取得数据
            ajaxBankOutletsPie("out");

            //更新数据
            var optionPie = chartOutPie.getOption();
            optionPie.series[0].data = dataOutPie;
            
            // 初始化清空图形
            chartOutPie.clear();
            chartOutPie.setOption(optionPie);
        }

        function ajaxBankOutletsPie(type) {
            $.ajax({
                url: '${ctx}/report/v01/graph/getBankOutletsPieData?type=' + type + '&date=' + $('#searchDate').val(),
                type: 'Post',
                data: '',
                dataType: 'json',
                async: false,
                cache: false,
                success: function(res) {
                    if ("out" == type) {
                        titleOutPie = res.title;
                        dataOutPie = res.data;

                    } else {
                        titleInPie = res.title;
                        dataInPie = res.data;
                    }
                },
                error: function() {
                    return null;
                }
            });
        }

        // -------金库库存变量---------------------------
        var titleFullPie;
        var dataFullPie;

        var titleDamagePie;
        var dataDamagePie;

        var titleAtmPie;
        var dataAtmPie;

        var titleCountwaitPie;
        var dataCountwaitPie;
        
        // -------金库库存完整券（饼形图）---------------------------
        var chartFullPie;
        function makeStoreInfoFullPie() {
            var main = document.getElementById('graphStoreInfoFullPie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartFullPie = echarts.init(div);

            // 调用后台，取得数据
            // ajaxStoreInfoPie();

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: '完整券库存',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{b}: {c}"
                },
                animation: false,
                series: [{
                    name: '完整券',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '45%',
                    center: ['50%', '50%'],
                    data: dataFullPie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartFullPie.setOption(optionPie);
        }

        // -------金库库存残损券（饼形图）---------------------------
        var chartDamagePie;
        function makeStoreInfoDamagePie() {
            var main = document.getElementById('graphStoreInfoDamagePie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartDamagePie = echarts.init(div);

            // 调用后台，取得数据
            // ajaxStoreInfoPie();

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: '残损券库存',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{b}: {c}"
                },
                animation: false,
                series: [{
                    name: '残损券',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '45%',
                    center: ['50%', '50%'],
                    data: dataDamagePie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartDamagePie.setOption(optionPie);
        }

        // -------金库库存ATM券（饼形图）---------------------------
        var chartAtmPie;
        function makeStoreInfoAtmPie() {
            var main = document.getElementById('graphStoreInfoAtmPie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartAtmPie = echarts.init(div);

            // 调用后台，取得数据
            // ajaxStoreInfoPie();

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: 'ATM券库存',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{b}: {c}"
                },
                animation: false,
                series: [{
                    name: 'ATM券',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '45%',
                    center: ['50%', '50%'],
                    data: dataAtmPie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartAtmPie.setOption(optionPie);
        }

        // -------金库库存待整点券（饼形图）---------------------------
        var chartCountwaitPie;
        function makeStoreInfoCountwaitPie() {
            var main = document.getElementById('graphStoreInfoCountwaitPie');
            var div = document.createElement('div');
            var width = document.body.clientWidth;
            div.style.cssText = width + 'px; height:390px';
            main.appendChild(div);
            chartCountwaitPie = echarts.init(div);

            // 调用后台，取得数据
            // ajaxStoreInfoPie();

            // 设置图形参数
            var optionPie = ({
                title: {
                    text: '待整点券库存',
                    x: 'center'
                },
                legend: {
                    //orient: 'vertical',
                    //x: 'right',
                    data: []
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{b}: {c}"
                },
                animation: false,
                series: [{
                    name: '待整点券',
                    type: 'pie',
                    selectedMode: false,
                    hoverAnimation: false,
                    selectedOffset: 30,
                    clockwise: true,
                    radius: '45%',
                    center: ['50%', '50%'],
                    data: dataCountwaitPie,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                formatter: '{b}: {c}'
                            },
                            labelLine: {
                                show: true
                            }
                        }
                    }
                }]
            });

            chartCountwaitPie.setOption(optionPie);
        }

        // 刷新库存数据
        function refreshStoreInfoPieData() {
            // 调用后台，取得数据
            ajaxStoreInfoPie();

            // 完整券
            if (!chartFullPie) {
                return;
            }
            var optionFullPie = chartFullPie.getOption();
            optionFullPie.series[0].data = dataFullPie;
            chartFullPie.clear();   // 初始化清空图形
            chartFullPie.setOption(optionFullPie);

            // 残损券
            if (!chartDamagePie) {
                return;
            }
            var optionDamagePie = chartDamagePie.getOption();
            optionDamagePie.series[0].data = dataDamagePie;
            chartDamagePie.clear();   // 初始化清空图形
            chartDamagePie.setOption(optionDamagePie);

            // ATM券
            if (!chartAtmPie) {
                return;
            }
            var optionAtmPie = chartAtmPie.getOption();
            optionAtmPie.series[0].data = dataAtmPie;
            chartAtmPie.clear();   // 初始化清空图形
            chartAtmPie.setOption(optionAtmPie);

            // 待整点券
            if (!chartCountwaitPie) {
                return;
            }
            var optionCountwaitPie = chartCountwaitPie.getOption();
            optionCountwaitPie.series[0].data = dataCountwaitPie;
            chartCountwaitPie.clear();  // 初始化清空图形
            chartCountwaitPie.setOption(optionCountwaitPie);
        }

        // 取得金库库存信息
        function ajaxStoreInfoPie(type) {
            $.ajax({
                url: '${ctx}/report/v01/graph/getCofferStoreInfoPieData?date=' + $('#searchDate').val(),
                type: 'Post',
                data: '',
                dataType: 'json',
                async: false,
                cache: false,
                success: function(res) {
                    titleFullPie = res.titleFullPie;
                    dataFullPie = res.dataFullPie;

                    titleDamagePie = res.titleDamagePie;
                    dataDamagePie = res.dataDamagePie;

                    titleAtmPie = res.titleAtmPie;
                    dataAtmPie = res.dataAtmPie;

                    titleCountwaitPie = res.titleCountwaitPie;
                    dataCountwaitPie = res.dataCountwaitPie;
                },
                error: function() {
                    return null;
                }
            });
        }

        // -------定时刷新---------------------------
        function refresh() {
            // 线图数据
            refreshLineData();
            // 网点接收明细数据
            refreshBankOutletsInPieData();
            // 网点上缴明细数据
            refreshBankOutletsOutPieData();
            // 金库库存
            refreshStoreInfoPieData();
        }

        //setInterval(refresh(), 30000); // 30秒刷新（修改秒数时，不要忘了修改注释）
		setInterval(function(){ refresh(); }, 30000);
        // ---出入库线图---------------------------------------------------
        setTimeout(function() {
            makeInOutAmountLine();
        }, 1500);

        // ---网点---------------------------------------------------
        if($.inArray(userType, bankOutletsUserArray) >= 0) {
        	
            // 网点接收饼图
            setTimeout(function() {
            	makeBankOutletsInPie();
            }, 1500);
            // 网点上缴饼图
            setTimeout(function() {
            	makeBankOutletsOutPie();
            }, 1500);
        }

        // ---金库---------------------------------------------------
        if($.inArray(userType, cofferUserArray) >= 0) {
        	
            // 获取数据
            setTimeout(function() {
                ajaxStoreInfoPie();
            }, 10);

            // 完整券
            setTimeout(function() {
                makeStoreInfoFullPie();
            }, 1500);

            // 残损券
            setTimeout(function() {
                makeStoreInfoDamagePie();
            }, 1500);

            // ATM券
            setTimeout(function() {
                makeStoreInfoAtmPie();
            }, 1500);

            // 待整点券
            setTimeout(function() {
                makeStoreInfoCountwaitPie();
            }, 1500);
        }

    </script>
</head>

<body>
	<ul class="nav nav-tabs">
		<li class=""><a href="#"></a></li>
	</ul>
	<c:choose>
		<c:when test="${'bankOutlets' == officeType}">
			<!-- 网点展示 -->
			<form:form id="searchForm" modelAttribute="reportInfo" action=""
				method="post" class="breadcrumb form-search">
				<%-- 开始日期 --%>
				<label><spring:message code="common.time" />：</label>
				<input id="searchDate" name="searchDate" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${reportInfo.searchDate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});" />
	
				<%-- 刷新 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.refresh'/>" onclick="refresh();" />
			</form:form>
	
			<div class="row">
				<div class="row-fluid">
					<div class="span6" id="graphBankOutletsInPie"></div>
					<div class="span6" id="graphBankOutletsOutPie"></div>
				</div>
			</div>
	
			<div class="row">
				<div class="row-fluid">
					<div class="span12" id="grapInOutAmountLine"></div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<!-- 金库展示 -->
			<div class="row">
				<div class="row-fluid">
					<div class="span3" id="graphStoreInfoFullPie"></div>
					<div class="span3" id="graphStoreInfoAtmPie"></div>
					<div class="span3" id="graphStoreInfoCountwaitPie"></div>
					<div class="span3" id="graphStoreInfoDamagePie"></div>
				</div>
			</div>
	
			<form:form id="searchForm" modelAttribute="reportInfo" action=""
				method="post" class="breadcrumb form-search">
				<%-- 开始日期 --%>
				<label><spring:message code="common.time" />：</label>
				<input id="searchDate" name="searchDate" type="text"
					readonly="readonly" maxlength="20"
					class="input-small Wdate createTime"
					value="<fmt:formatDate value="${reportInfo.searchDate}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'});" />
	
				<%-- 刷新 --%>
				<input id="btnSubmit" class="btn btn-primary" type="button"
					value="<spring:message code='common.refresh'/>" onclick="refresh();" />
			</form:form>
	
			<div class="row">
				<div class="row-fluid">
					<div class="span12" id="grapInOutAmountLine"></div>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

</body>

</html>