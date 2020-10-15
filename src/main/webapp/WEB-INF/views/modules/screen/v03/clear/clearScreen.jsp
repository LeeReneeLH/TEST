<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/head.jsp" %>
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart3/echarts.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
<script type="text/javascript" src="${ctxStatic}/echart3/theme/shine.js"></script>
<script type="text/javascript" src="${ctxStatic}/esl/esl.js"></script>

<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/modules/screen/v03/default.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/modules/screen/v03/multilevelmenu.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/modules/screen/v03/component.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/modules/screen/v03/animations.css" />
<script src="${ctxStatic}/script/modules/screen/v03/modernizr.custom.js"></script>
<link rel="stylesheet" type="text/css" href="${ctxStatic}/css/modules/screen/v03/init.css">
<script src="${ctxStatic}/script/modules/screen/v03/jquery.min.js"></script>
<html>
<head>
	<title>数字化金融服务平台-数据可视化监控中心</title>
	
	<style type="text/css">
		.circle1{    width: 13px;      height: 13px;   border-radius: 50%;      -moz-border-radius: 50%;      -webkit-border-radius: 50%;    } 
		body {font-family: Microsoft YaHei;}
		@-webkit-keyframes scrollText2 {
		    0%{
		        transform: translateX(0px);
		    }
		    16%{
		        transform: translateX(0px);
		    }
		    17%{
		        transform: translateX(-1204px);
		    }
		    33%{
		        transform: translateX(-1204px);
		    }
		    34%{
		        transform: translateX(-2408px);
		    }
		    50%{
		        transform: translateX(-2408px);
		    }
		    51%{
		        transform: translateX(-3612px);
		    }
		    67%{
		        transform: translateX(-3612px);
		    }
		    68%{
		        transform: translateX(-4816px);
		    }
		    
		    84%{
		        transform: translateX(-4816px);
		    }
		    85%{
		        transform: translateX(-6020px);
		    }
		    
		    99%{
		        transform: translateX(-6020px);
		    }
		    100%{
		        transform: translateX(-7224px);
		    }
		    
		}
		@keyframes scrollText2 {
		    0%{
		        transform: translateX(0px);
		    }
		    16%{
		        transform: translateX(0px);
		    }
		    17%{
		        transform: translateX(-1204px);
		    }
		    33%{
		        transform: translateX(-1204px);
		    }
		    34%{
		        transform: translateX(-2408px);
		    }
		    50%{
		        transform: translateX(-2408px);
		    }
		    51%{
		        transform: translateX(-3612px);
		    }
		    67%{
		        transform: translateX(-3612px);
		    }
		    68%{
		        transform: translateX(-4816px);
		    }
		    
		    84%{
		        transform: translateX(-4816px);
		    }
		    85%{
		        transform: translateX(-6020px);
		    }
		    
		    99%{
		        transform: translateX(-6020px);
		    }
		    100%{
		        transform: translateX(-7224px);
		    }
		
		}
		
		.box4{
		  top: 0px;
		  left: 0px;
		  width: 1200px;
		  height: 280px;
		  overflow: hidden;
		}
		.border4{
		  top: 0px;
		  left: 0px;
		  width: 9300px;
		  -webkit-animation:scrollText2 50s  infinite  cubic-bezier(0.25, 0.1, 0.25, 1.0);
		  animation:scrollText2  50s  infinite  cubic-bezier(0.25, 0.1, 0.25, 1.0);
		  
		}
		.border4 div{
		  height: 280px;
		  width: 1200px;
		  overflow: hidden;
		  display: inline-block;
		
		}

	</style>
		

	<script type="text/javascript">
	//颜色设定(上部)
	var arrColor = new Array();
	arrColor[0]=""
	arrColor[1]="#FBC878";        	//今日 商行上缴额 -1
	arrColor[2]="#7ECFF4";        	//今日 商行取款额-2
	arrColor[3]="#B0EE91";        	//今日 清分中心清点额 -3
	arrColor[4]="#F7716C";        	//今日 清分差错额- 4
	arrColor[5]="#FBC878";        	//商行 累计上缴额- 5
	arrColor[6]="#7ECFF4";        	//商行 累计取款额-6
	arrColor[7]="#B0EE91";        	//清分中心 累计清点额-7
	arrColor[8]="#F7716C";          //清分中心 累计差错额-8

	//颜色设定(下部)
	var serviceColor = "#FBC878";
	
	
	//文字样式的设定
	var arrType = new Array();
	arrType[0]=""
	arrType[1]="2";        			//今日 商行上缴额-1
	arrType[2]="2";        			//今日 商行取款额-2
	arrType[3]="2";        			//今日 清分中心清点额-3
	arrType[4]="2";        			//今日 清分差错额- 4
	arrType[5]="2";        			//商行 累计上缴额- 5
	arrType[6]="2";        			//商行 累计取款额-6
	arrType[7]="2";        			//清分中心 累计清点额-7
	arrType[8]="2";                	//清分中心 累计差错额-8
	
	//颜色设定
	var arrRingColor = new Array();
	arrRingColor[0]=""
	arrRingColor[1]="#C9EAFB";  
	arrRingColor[2]="#A5E1FD"; 
	arrRingColor[3]="#6ECFFC";  
	arrRingColor[4]="#3BBDFB"; 
	arrRingColor[5]="#049EE6"

	//数字化金融服务平台服务统计
    var arrServiceColor = {"清分服务":"#FFD79F","发行库":"#7ECDF5","现金库":"#B1EE91","上门收款":"#F9706C","自助设备":"#5680E2"};
    
	//服务名字设定
	var arrServiceNm = new Array();
	arrServiceNm[0]="清分服务"
	arrServiceNm[1]="发行库";  
	arrServiceNm[2]="现金库"; 
	arrServiceNm[3]="上门收款";  
	arrServiceNm[4]="自助设备"; 

	
	
	//当前地区编号
	var mapCode = "";
	//省地图编号保留（用于返回时使用）
	var mapProvinceCode = "";
	//市地图编号保留（用于刷新时使用）
	var mapCityCode = "";
	
	
	//地图类型
	var strmapTypeCode = "";
	//全国地图
	var myChinaChart;
	//省地图
	var myProvinceChart;
	//市地图
	var myCityChart;
	
	
	//日线图（环形图）
	var dayChart1;
	var dayChart2;
	//今日合计图（柱状图）
	var toDayBarChart;
	//累计合计图（柱状图）
	var totalBarChart;
	//上门收款（环形图）
	var rightRingChart;
	//自助设备（环形图）
	var leftRingChart;
	//7日折线图
	var sevenDayChart1;
	var sevenDayChart2;
	var sevenDayChart3;
	var sevenDayChart4;
	var sevenDayChart5;
	var sevenDayChart6;
	var sevenDayChart7;
	$(document).ready(function (){

		//服务统计（柱状图）
		sevenDayChart1 = echarts.init(document.getElementById('sevenDayChart1')); 
		createServiceTotalGraph(sevenDayChart1,'1');
		sevenDayChart2 = echarts.init(document.getElementById('sevenDayChart2')); 
		createServiceTotalGraph(sevenDayChart2,'2');
		sevenDayChart3 = echarts.init(document.getElementById('sevenDayChart3')); 
		createServiceTotalGraph(sevenDayChart3,'3');
		sevenDayChart4 = echarts.init(document.getElementById('sevenDayChart4')); 
		createServiceTotalGraph(sevenDayChart4,'4');
		sevenDayChart5 = echarts.init(document.getElementById('sevenDayChart5')); 
		createServiceTotalGraph(sevenDayChart5,'5');
		sevenDayChart6 = echarts.init(document.getElementById('sevenDayChart6')); 
		createServiceTotalGraph(sevenDayChart6,'6');
		sevenDayChart7 = echarts.init(document.getElementById('sevenDayChart7')); 
		createServiceTotalGraph(sevenDayChart7,'1');
		
		//自助设备（环形图）
		leftRingChart = echarts.init(document.getElementById('leftRingChart')); 
		createLeftRingGraph();
		
		//上门收款（环形图）
		rightRingChart = echarts.init(document.getElementById('rightRingChart')); 
		createRightRingGraph();
		
		
		strmapTypeCode =  '${mapTypeCode}';
		mapCode = '${mapCode}';

		
		if (strmapTypeCode == '1'){
			createChinaMap();		//全国地图
		}
		if (strmapTypeCode == '2'){
			createProvinceMap();	//全省地图
		}
		
		if (strmapTypeCode == '3'){
			createCityMap();		//鞍山市地图
		}
		
	});
	
	var colorList1;
	
    // -------服务统计（柱状折线图）---------------------------
    function createServiceTotalGraph(v_obj,v_kbn) {
        // 设置图形参数
        var strColor1 = "";
        var colorList;

        var myArray1 = new Array();   //不定义元素个数
        var data1;
        var data2;
        var data3;
        var strTitleNm;
        strTitleNm = "数字化金融服务平台服务统计";
        if (v_kbn * 1 > 1){
        	strTitleNm = strTitleNm  + " - " + arrServiceNm[v_kbn * 1 -2];
        }
        
        data1 = [
	            	<c:forEach items="${serviceList}" var="tabbean" varStatus="status">
	            	"${tabbean['officeName']}",
	            	</c:forEach>
              ];
        
        if (v_kbn == "1"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" varStatus="status">
 	            	getBarValue("${row1['maxPct']}"),
 	            	</c:forEach>
                  ];
     		
     		data3 = [
  	            	<c:forEach items="${serviceList}" var="row1" varStatus="status">
  	            	"${row1['maxPct']}",
  	            	</c:forEach>
                   ];
     		colorList1 = [
     	            	<c:forEach items="${serviceList}" var="row2" varStatus="status">
     	            	arrServiceColor["${row2['maxName']}"],
    	            	</c:forEach>
                       ];
            strColor1 = '#87B9D7';
     		
        }

		//清分服务
        if (v_kbn == "2"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" >
 	            		<c:forEach items="${row1['businessList']}" var="row2" >
	 	   				<c:if test="${row2['businessName'] eq '清分服务'}">
	 	   				getBarValue("${row2['businessPct']}"),
						</c:if>
 	            		</c:forEach>
 	            	</c:forEach>
                  ];
     		
     		data3 = [
  	            	<c:forEach items="${serviceList}" var="row1" >
  	            		<c:forEach items="${row1['businessList']}" var="row2" >
 	 	   				<c:if test="${row2['businessName'] eq '清分服务'}">
 	 	   				"${row2['businessPct']}",
 						</c:if>
  	            		</c:forEach>
  	            	</c:forEach>
                   ];
     		
     		
     		
            colorList = [
                         '#FFD89D','#FFD89D','#FFD89D','#FFD89D','#FFD89D',
                          '#FFD89D','#FFD89D','#FFD89D','#FFD89D','#FFD89D'
                       ];
            strColor1 = '#DAF9CA';
     		
        }
        
		//发行库
        if (v_kbn == "3"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" >
 	            		<c:forEach items="${row1['businessList']}" var="row2" >
	 	   				<c:if test="${row2['businessName'] eq '发行库'}">
	 	   				getBarValue("${row2['businessPct']}"),
						</c:if>
 	            		</c:forEach>
 	            	</c:forEach>
                  ];
     		
     		data3 = [
  	            	<c:forEach items="${serviceList}" var="row1" >
  	            		<c:forEach items="${row1['businessList']}" var="row2" >
 	 	   				<c:if test="${row2['businessName'] eq '发行库'}">
 	 	   				"${row2['businessPct']}",
 						</c:if>
  	            		</c:forEach>
  	            	</c:forEach>
                   ];
            colorList = [
                         '#7DCFF4','#7DCFF4','#7DCFF4','#7DCFF4','#7DCFF4',
                          '#7DCFF4','#7DCFF4','#7DCFF4','#7DCFF4','#7DCFF4'
                       ];
            strColor1 = '#DAF9CA';
     		
        }
        
		
		
		
        //现金库
        if (v_kbn == "4"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" >
 	            	RandomNumBoth(15,98),
 	            	</c:forEach>
                  ];
     		data3 = data2;
            colorList = [
                         '#B1EE91','#B1EE91','#B1EE91','#B1EE91','#B1EE91',
                          '#B1EE91','#B1EE91','#B1EE91','#B1EE91','#B1EE91'
                       ];
            strColor1 = '#DAF9CA';
     		
     		
        }
        
		//上门收款
        if (v_kbn == "5"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" >
 	            		<c:forEach items="${row1['businessList']}" var="row2" >
	 	   				<c:if test="${row2['businessName'] eq '上门收款'}">
	 	   				getBarValue("${row2['businessPct']}"),
						</c:if>
 	            		</c:forEach>
 	            	</c:forEach>
                  ];
     		data3 = [
  	            	<c:forEach items="${serviceList}" var="row1" >
  	            		<c:forEach items="${row1['businessList']}" var="row2" >
 	 	   				<c:if test="${row2['businessName'] eq '上门收款'}">
 	 	   				"${row2['businessPct']}",
 						</c:if>
  	            		</c:forEach>
  	            	</c:forEach>
                   ];
     		
            colorList = [
                         '#F7716C','#F7716C','#F7716C','#F7716C','#F7716C',
                          '#F7716C','#F7716C','#F7716C','#F7716C','#F7716C'
                       ];
            strColor1 = '#DAF9CA';
     	
     		
        }
        
		//自助设备
        if (v_kbn == "6"){
     		data2 = [
 	            	<c:forEach items="${serviceList}" var="row1" >
 	            		<c:forEach items="${row1['businessList']}" var="row2" >
	 	   				<c:if test="${row2['businessName'] eq '自助设备'}">
	 	   				getBarValue("${row2['businessPct']}"),
						</c:if>
 	            		</c:forEach>
 	            	</c:forEach>
                  ];
     		
     		data3 = [
  	            	<c:forEach items="${serviceList}" var="row1" >
  	            		<c:forEach items="${row1['businessList']}" var="row2" >
 	 	   				<c:if test="${row2['businessName'] eq '自助设备'}">
 	 	   				"${row2['businessPct']}",
 						</c:if>
  	            		</c:forEach>
  	            	</c:forEach>
                   ];
     		
            colorList = [
                         '#5680E2','#5680E2','#5680E2','#5680E2','#5680E2',
                          '#5680E2','#5680E2','#5680E2','#5680E2','#5680E2'
                       ];
            strColor1 = '#DAF9CA';
     		
     		
        }


        
        
            var  option = {
					toolbox: {
							show : false,
						},
                   
                	title:{
               	     text:strTitleNm,
                        x:130,
                        y: 'top',
                        textStyle: {
                            fontFamily: '微软雅黑',
                            fontSize: 22,
                            fontStyle: 'normal',
                            fontWeight: 'normal',
                            color:'#B2D3F5',
                        },
               	},
                    
                    
    			    xAxis: {
    			        type: 'category',
    			        data: data1,
   			            axisLabel:{
                            interval:0,
                            rotate:-40,
			                textStyle:{
			                   color:"#B2D4F7", //刻度颜色
			                   fontSize:12  //刻度大小
			              }
			            },
			            
	                    //  改变x轴颜色
	                    axisLine:{
	                        lineStyle:{
	                            color:'#1C4076',
	                            width:2,
	                        }
	                    },  
	                    
			            axisTick: {
			                alignWithLabel: true
			            }
    			    },

                    
                    
    			    yAxis : [
         			        {
                                 type : 'value',  
                                 min: 0,
                                 max: 100,
                                 interval: 20,
                                 splitLine: {  
                                     lineStyle: {  
                                         // 使用深浅的间隔色  
                                         color: ['#1C4076']  
                                     }  
                                 },  
                                 nameTextStyle: {  
                                 	fontSize:16,  //刻度大小
                                     color: ['#B2D4F7']  
                                     },  
             			            axisLabel:{
             			            	formatter: '{value}%',
             			                textStyle:{
             			                   color:"#B2D4F7", //刻度颜色
             			                   fontSize:16  //刻度大小
             			                  
             			              }
             			            },
                                     
                                     
                                 axisLine:{  
                                     lineStyle:{  
                                         color:'#1C4076',  
                                         width:2,//这里是为了突出显示加上的  
                                     }  
                                 }  
         			        }               
         			    ],
                    
                    
                    
                    
                    series: [
                         
                        {
                            name:'',
                            type:'bar',
                            barWidth : 12,//柱图宽度
                            
                            /*设置柱状图颜色*/
                            itemStyle: {
                                normal: {
                                    color: function(params) {
                                        // build a color map as your need.
                                        
                                        if (v_kbn == "1"){
                                        	return colorList1[params.dataIndex];
                                        }else{
                                        	return colorList[params.dataIndex];
                                        }

 
                                    },
                                    /*信息显示方式*/
                                    label: {
                                        show: false,
                                        position: 'top',
                                        formatter: '{b}\n{c}'
                                    }
                                }
                            },
                            data:data2
                        },
                        {
                            name:'',
                            type:'line', 
    			            symbol: "circle",      // 默认是空心圆（中间是白色的），改成实心圆
    			            symbolSize: 10,
    			            itemStyle: {
    			                normal: {
    			                    color: strColor1,  // 会设置点和线的颜色，所以需要下面定制 line
    			                    borderColor: strColor1  // 点边线的颜色
    			                }
    			            },
    			            lineStyle:{
    			                normal:{
    			                    width:1,  //连线粗细
    			                    color: strColor1  //连线颜色
    			                }
    			            },
    			            data:data3
                        }
                    ]
            };
            v_obj.setOption(option);
    }

	
	


    // -------自助设备（环形图）---------------------------
    function createLeftRingGraph() {
    	
       var atmData = [
	            	<c:forEach items="${atmList}" var="tabbean" varStatus="status">
	                {value:"${tabbean['atmAmount']}", name:"${tabbean['officeName']}",itemStyle:{ normal:{color:arrRingColor[${ status.index + 1}]} }},
	            	</c:forEach>
                 ];
    	
    	
    	
        // 设置图形参数
        var optionPie = ({
        	title:{
        	     text:'自助设备\n服务统计',
                 x:'center',
                 y: 'center',
                 textStyle: {
                     fontFamily: '微软雅黑',
                     fontSize: 22,
                     fontStyle: 'normal',
                     fontWeight: 'normal',
                     color:'#B2D3F5',
                 },
        	},
    	    tooltip: {
    	        trigger: 'item',
    	        formatter: "{b}: {c} ({d}%)"
    	    },
    	    series: [
    	        {
    	            name:'',
    	            type:'pie',
    	            radius: ['33%', '63%'],
    	            center : ['50%', '50%'],    // 默认全局居中 
    	            avoidLabelOverlap: false,
    	            label: {
    	                normal: {
    	                    formatter: '{a|{b}}\n\n',
    	                    borderWidth: 0,
    	                    borderRadius: 4,
    	                    // shadowBlur:3,
    	                    // shadowOffsetX: 2,
    	                    // shadowOffsetY: 2,
    	                    // shadowColor: '#999',
    	                    padding: [0, -150],
    	                    rich: {
    	                        a: {
    	                            color: '#B2D4F7',
    	                            fontSize: 16,
    	                            lineHeight: 20
    	                        },
    	                        // abg: {
    	                        //     backgroundColor: '#333',
    	                        //     width: '100%',
    	                        //     align: 'right',
    	                        //     height: 22,
    	                        //     borderRadius: [4, 4, 0, 0]
    	                        // },
    	                        hr: {
    	                            borderColor: '#FFFFFF',
    	                            width: '100%',
    	                            borderWidth: 0.5,
    	                            height: 0
    	                        },
    	                        b: {
    	                            fontSize: 16,
    	                            lineHeight: 20,
    	                            color: '#FFFFFF'
    	                        }
    	                        // per: {
    	                        //     color: '#333',
    	                        //     padding: [2, 4],
    	                        //     borderRadius: 2
    	                        // }
    	                    }
    	                }
    	            },
    	            labelLine: {
    	                normal: {
    	                    length: 15,
    	                    length2: 150,
    	                    lineStyle: {
    	                        color: '#B2D4F7'
    	                    }
    	                }

    	            },
    	            data:atmData
    	        },
    	        
    	        {
    	            name:'',
    	            type:'pie',
    	            radius: ['33%', '63%'],
    	            center : ['50%', '50%'],    // 默认全局居中 
    	            avoidLabelOverlap: false,
    	            label: {
    	                normal: {
    	                    formatter: '{a|{d}%}',
    	                    borderWidth: 0,
    	                    borderRadius: 4,
    	                    position: 'inner',
    	                    //padding: [0, -105],
    	                    rich: {
    	                        a: {
    	                        	color: '#000000',
    	                            fontSize: 14,
    	                            lineHeight: 20
    	                        }
 
    	                    }
    	                }
    	            },
    	            data:atmData
    	        }

    	    ]
    	});
        leftRingChart.setOption(optionPie);
    }
    
    
    // -------上门收款（环形图）---------------------------
    function createRightRingGraph() {
    	
        var doorData = [
   	            	<c:forEach items="${businessList}" var="tabbean" varStatus="status">
   	                {value:"${tabbean['businessAmount']}", name:"${tabbean['officeName']}",itemStyle:{ normal:{color:arrRingColor[${ status.index + 1}]} }},
   	            	</c:forEach>
                    ];
       	
    	
    	
    	
        // 设置图形参数
        var optionPie = ({
        	title:{
        	     text:'上门收款\n服务统计',
                 x:'center',
                 y: 'center',
                 textStyle: {
                     fontFamily: '微软雅黑',
                     fontSize: 22,
                     fontStyle: 'normal',
                     fontWeight: 'normal',
                     color:'#B2D3F5',
                 },
        	},
    	    tooltip: {
    	        trigger: 'item',
    	        formatter: "{b}: {c} ({d}%)"
    	    },
    	    series: [
    	        {
    	            name:'',
    	            type:'pie',
    	            radius: ['33%', '63%'],
    	            center : ['50%', '50%'],    // 默认全局居中 
    	            avoidLabelOverlap: false,
    	            label: {
    	                normal: {
    	                    formatter: '{a|{b}}\n\n',
    	                    borderWidth: 0,
    	                    borderRadius: 4,
    	                    // shadowBlur:3,
    	                    // shadowOffsetX: 2,
    	                    // shadowOffsetY: 2,
    	                    // shadowColor: '#999',
    	                    padding: [0, -118],
    	                    rich: {
    	                        a: {
    	                            color: '#B2D4F7',
    	                            fontSize: 16,
    	                            lineHeight: 20
    	                        },
    	                        // abg: {
    	                        //     backgroundColor: '#333',
    	                        //     width: '100%',
    	                        //     align: 'right',
    	                        //     height: 22,
    	                        //     borderRadius: [4, 4, 0, 0]
    	                        // },
    	                        hr: {
    	                            borderColor: '#FFFFFF',
    	                            width: '100%',
    	                            borderWidth: 0.5,
    	                            height: 0
    	                        },
    	                        b: {
    	                            fontSize: 16,
    	                            lineHeight: 20,
    	                            color: '#FFFFFF'
    	                        }
    	                        // per: {
    	                        //     color: '#333',
    	                        //     padding: [2, 4],
    	                        //     borderRadius: 2
    	                        // }
    	                    }
    	                }
    	            },
    	            labelLine: {
    	                normal: {
    	                    length: 20,
    	                    length2: 120,
    	                    lineStyle: {
    	                        color: '#B2D4F7'
    	                    }
    	                }

    	            },
    	            data:doorData
    	        },
    	        
    	        {
    	            name:'',
    	            type:'pie',
    	            radius: ['33%', '63%'],
    	            center : ['50%', '50%'],    // 默认全局居中 
    	            avoidLabelOverlap: false,
    	            label: {
    	                normal: {
    	                    formatter: '{a|{d}%}',
    	                    borderWidth: 0,
    	                    borderRadius: 4,
    	                    position: 'inner',
    	                    //padding: [0, -105],
    	                    rich: {
    	                        a: {
    	                            color: '#000000',
    	                            fontSize: 14,
    	                            lineHeight: 20
    	                        }
 
    	                    }
    	                }
    	            },
    	            data:doorData
    	        } 
    	    ]
    	});
        rightRingChart.setOption(optionPie);
    }
    
    
    //获取系统时间
    function getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var seperator2 = ":";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        
        var strHour = date.getHours();
        var strMinute = date.getMinutes();
        var strSecond = date.getSeconds();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        
        if (strHour >= 1 && strHour <= 9) {
        	strHour = "0" + strHour;
        }
        if (strMinute >= 0 && strMinute <= 9) {
        	strMinute = "0" + strMinute;
        }
        if (strSecond >= 0 && strSecond <= 9) {
        	strSecond = "0" + strSecond;
        }
        var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                + " " + strHour + seperator2 + strMinute
                + seperator2 + strSecond;
        return currentdate;
    } 
    
    function displayTime() {
        document.getElementById("sysTime").value = getNowFormatDate();
    }
	
    

	window.onload = function() {
		displayTime();
		setInterval(displayTime,1000);      			// 每隔1秒钟调用displayTime函数
		setInterval(refreshAmountData,4000);      		// 数据刷新(商行及清分数据)
		setInterval(refreshServiceData,7000);      		// 数据刷新(服务数据)
		
		setInterval(refreshMap,1000000);      			// 地图刷新
		
	}


    // 地图(市图)
	function createCityMap(cityCode) {
    	
		var strUrl = '';
		if (cityCode == undefined || cityCode == "") {
			strUrl = '${contextPath}/f/getAddress';
		}else{
			strUrl = '${contextPath}/f/getAddressByCityCode?cityCode=' + cityCode;
		}
    	
    	
		document.getElementById("mapDiv3").style.display="block";
		$("#mapDiv3").show();
  		// 路径配置
         require.config({
  	        packages: [
  	            {
  	                name: 'echarts',
  	                location: '${ctxStatic}/echart2',
  	                main: 'echarts'
  	            }
  	        ]
  	    });
      	// 使用
         require(
             [
                 'echarts',
                 'echarts/chart/map' // 使 用柱状图就加载bar模块，按需加载
             ],
             function (ec) {
            	 
                 	// 基于准备好的dom，初始化echarts图表 ${curOfficeId}
                    myCityChart = ec.init(document.getElementById('mapDiv3'));
                 

 					//var showMaps = res.mapJsonInfo;
 					var showName = true;
		 		 	var mapType = [];
		 		 	var mapGeoData = require('echarts/util/mapData/params');

   	                 $.ajax({
   	                	url : strUrl,
 	     				type : 'Post',
 	     				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
 	     				dataType : 'json',
 	     				async : true,
 	     				cache : false,
 	     				success : function(res) {
 	     					var showMaps = res.mapJsonInfo;
 
 	   			 		 	var mapType = [];
 	   			 		 	var mapGeoData = require('echarts/util/mapData/params');
 	   			 		 	for (var city in showMaps) {
 	   			 		   		mapType.push(city);
 	   			 		    	// 自定义扩展图表类型
 	   			 		    	mapGeoData.params[city] = {
 		   			 		        getGeoJson: (function (mapIndex) {
 		   			 		            var geoJsonName = showMaps[mapIndex];
 		   			 		            if (geoJsonName == '210300'){
 		   			 		           		geoJsonName = "an_shan_geo";
 		   			 		            }
 		   			 		            return function (callback) {
 		   			 		                $.getJSON('${ctxStatic}/mapjson/geoJson/' + geoJsonName + '.json', callback);
 		   			 		            }
 		   			 		        })(city)
 		   			 		    }
 		   			 		}

 	   			 			var strMapName = res.mapType;
 							option = {
   									backgroundColor: '',
   		 							color: ['gold','aqua','lime'],
   		 							title : {
   		 								text: strMapName,
   		 								subtext:'',
   		 								padding:  [12, 0, 0, 50],
   		 								x:'left',
   		 								textStyle : {
   		 									color: '#B2D4F7',
   		 									fontWeight: 'bolder',
   		 									fontFamily: '微软雅黑',
   		 									fontSize:'20',
   		 								}
   		 							},
   		 							toolbox: {
   		 								show : false,
   		 							},
   		 							dataRange: {
   		 								show : false,
   		 								min : 0,
   		 								max : 100,
   		 								calculable : true,
   		 								color: ['#ff3333', 'orange', 'yellow','lime','aqua'],
   		 								textStyle:{
   		 									color:'#fff'
   		 								}
   		 							},
		                		    series : [
		                		        {
		                		            //name: res.name,
		                		            type: "<spring:message code='common.map.type'/>",
		                		            roam: false,
		                		            hoverable: false,
		                		            mapType: res.mapType,

		                		            
 											itemStyle:{
 												normal:{
 													label:{
 														show:showName,
 														textStyle: {color: "#FFFFFF"}
 													},
 													borderColor:'rgba(100,149,237,1)',
 													borderWidth:1,
 													areaStyle:{
 														//color: '#1B1B1B'
 														color: 'rgba(255, 255, 255, 0.0470588)'
 													}
 												}
 											},
		                		            
		                		            
		                		            data:[],
		                		            geoCoord: res.geoCoordMap
		                		        },
		                		        {
		                		            name: "<spring:message code='common.map.name'/>",
		                		            type: "<spring:message code='common.map.type'/>",
		                		            mapType: res.mapType,
		                		            data:[],
		                		            markLine : {
		                		                smooth:true,
		                		                effect : {
		                		                    show: true,
		                		                    scaleSize: 1,
		                		                    period: 30,
		                		                    color: '#fff',
		                		                    shadowBlur: 10
		                		                },
		                		                itemStyle : {
		                		                    normal: {
		                		                    	label:{show:false,formatter: '{b0}'},
		                		                        borderWidth:1,
		                		                        lineStyle: {
		                		                            type: 'solid',
		                		                            shadowBlur: 10
		                		                        }
		                		                    }
		                		                },
		                		                data : res.lineDataList
		                		            },
		                		            
		                		            
		                		            
		                		            
		                		            
		                		            
		                		            markPoint : {
		                		                symbol:'emptyCircle',
		                		                symbolSize : function (v){
		                		                    //return 5 + v/10
		                		                	return 10 + v/10
		                		                },
		                		                effect : {
		                		                    show: true,
		                		                    shadowBlur : 0
		                		                },
		                		                itemStyle:{
		                		                    normal:{
		                		                        label:{show:false}
		                		                    },
		                		                    emphasis:{label:{show:true, formatter: '{b}'}}
		                		                },
		                		                data : res.pointDataList
		                		            }
		                		            
		                		        },
		                		        
										{
		                		            name: "<spring:message code='common.map.name'/>",
		                		            type: "<spring:message code='common.map.type'/>",
		                		            mapType: res.mapType,
 											data:[],
 								           itemStyle:{
 								               normal:{label:{show:true,textStyle: {color: "#FFFFFF"}}},
 								               emphasis:{label:{show:true,textStyle: {color: "#FFFFFF"}}}
 								           }, 
 								           
 								           
 								           
 								          markPoint : {  
 							                    symbol: 'star',  
 							                    symbolSize:0,  
 							                    showDelay:0,  
 							                    itemStyle:{  
 							                        normal:{label:{  
 							                                    show: true,  
 							                                    position: 'left',  
 							                                    formatter: '{b}'   
 							                                  }  
 							                                }  
 							                          
 							                    },  
 							                   data : res.pointDataList
 							                }  
 										}
 		
		                		    ]
		                		};
 	   			 		 	
 	   			 		 	
 	   			 		 	
 	   			 		 	
 	     					
 	     					myCityChart.setOption(option);
 	     				},
 	     				error : function() {
 	     					return null;
 	     				}
 	     			}); 
	     		//myChart.setOption(option);

                
             }
         );
  	 }
	
    // 地图(市图)
	function refreshCityMap(cityCode) {
    	
		var strUrl = '';
		if (cityCode == undefined || cityCode == "") {
			strUrl = '${contextPath}/f/getAddress';
		}else{
			strUrl = '${contextPath}/f/getAddressByCityCode?cityCode=' + cityCode;
		}

		$.ajax({
			url : strUrl,
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'json',
			async : true,
			cache : false,
			success : function(res) {
		           //更新数据
		           var option = myCityChart.getOption();
		           option.series[0].geoCoord = res.geoCoordMap;    
		           option.series[1].markLine.data = res.lineDataList; 
		          option.series[1].markPoint.data = res.pointDataList; 
		          option.series[2].markPoint.data = res.pointDataList; 
		          myCityChart.setOption(option);
			},
			error : function() {
				return null;
			}
		}); 

  	 }
	
	

	//------------------------------------------
    
    // 地图(省图)
	function createProvinceMap(provinceCode) {

		var strUrl = '';
		if (provinceCode == undefined || provinceCode == "") {
			strUrl = '${contextPath}/f/getAddress';
		}else{
			strUrl = '${contextPath}/f/getAddressByProvinceCode?provinceCode=' + provinceCode;
		}
		
		
		document.getElementById("mapDiv2").style.display="block";
		$("#mapDiv2").show();
  		// 路径配置
         require.config({
  	        packages: [
  	            {
  	                name: 'echarts',
  	                location: '${ctxStatic}/echart2',
  	                main: 'echarts'
  	            }
  	        ]
  	    });
      	// 使用
         require(
             [
                 'echarts',
                 'echarts/chart/map' // 使 用柱状图就加载bar模块，按需加载
             ],
             function (ec) {
            	 
                 // 基于准备好的dom，初始化echarts图表 ${curOfficeId}
                 myProvinceChart = ec.init(document.getElementById('mapDiv2'));
                
				//var showMaps = res.mapJsonInfo;
				var showName = true;
				var mapType = [];

                $.ajax({
     				url : strUrl,
     				type : 'Post',
     				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
     				dataType : 'json',
     				async : true,
     				cache : false,
     				success : function(res) {
     					var showMaps = res.mapJsonInfo;
   			 		 	var mapType = [];
   			 		 	var mapGeoData = require('echarts/util/mapData/params');
   			 		 	for (var city in showMaps) {
   			 		   		mapType.push(city);
   			 		    	// 自定义扩展图表类型
   			 		    	mapGeoData.params[city] = {
	   			 		        getGeoJson: (function (mapIndex) {
	   			 		            var geoJsonName = showMaps[mapIndex];
	   			 		            mapCode = geoJsonName;
	   			 		            return function (callback) {
	   			 		                $.getJSON('${ctxStatic}/mapjson/geoJson/' + geoJsonName + '.json', callback);
	   			 		            }
	   			 		        })(city)
	   			 		    }
	   			 		}

						var strMapName = res.mapType + '省';
     					option = {
	                		    color: ['gold','aqua','lime'],
	 							title : {
	 								text: strMapName,
	 								subtext:'',
	 								padding:  [12, 0, 0, 50],
	 								x:'left',
	 								textStyle : {
	 									color: '#B2D4F7',
										fontWeight: 'bolder',
	 									fontFamily: '微软雅黑',
	 									fontSize:'20',
	 								}
	 							},
	                		    dataRange: {
	                		    	show : false,
	                		        min : 0,
	                		        max : 100,
	                		        calculable : true,
	                		        color: ['#ff3333', 'orange', 'yellow','lime','aqua'],
	                		        textStyle:{
	                		            color:'#fff'
	                		        }
	                		    },
	                		    series : [
	                		        {
	                		            //name: res.name,
	                		            type: "<spring:message code='common.map.type'/>",
	                		            roam: false,
	                		            hoverable: false,
	                		            mapType: res.mapType,

	                		            
											itemStyle:{
												normal:{
													label:{
														show:showName,
														textStyle: {color: "#FFFFFF"}
													},
													borderColor:'rgba(100,149,237,1)',
													borderWidth:1,
													areaStyle:{
														//color: '#1B1B1B'
														color: 'rgba(255, 255, 255, 0.0470588)'
													}
												}
											},
	                		            
	                		            
	                		            data:[],
	                		            geoCoord: res.geoCoordMap
	                		        },
	                		        {
	                		            name: "<spring:message code='common.map.name'/>",
	                		            type: "<spring:message code='common.map.type'/>",
	                		            mapType: res.mapType,
	                		            data:[],
	                		            markLine : {
	                		                smooth:true,
	                		                effect : {
	                		                    show: true,
	                		                    scaleSize: 1,
	                		                    period: 30,
	                		                    color: '#fff',
	                		                    shadowBlur: 10
	                		                },
	                		                itemStyle : {
	                		                    normal: {
	                		                    	label:{show:false,formatter: '{b0}'},
	                		                        borderWidth:1,
	                		                        lineStyle: {
	                		                            type: 'solid',
	                		                            shadowBlur: 10
	                		                        }
	                		                    }
	                		                },
	                		                data : res.lineDataList
	                		            },
	                		            markPoint : {
	                		                symbol:'emptyCircle',
	                		                symbolSize : function (v){
	                		                    //return 5 + v/10
	                		                	return 10 + v/10
	                		                },
	                		                effect : {
	                		                    show: true,
	                		                    shadowBlur : 0
	                		                },
	                		                itemStyle:{
	                		                    normal:{
	                		                        label:{show:false}
	                		                    },
	                		                    emphasis:{label:{show:true, formatter: '{b}'}}
	                		                },
	                		                data : res.pointDataList
	                		            }
	                		        }
	                		    ]
	                		};
     					myProvinceChart.setOption(option);
     					myProvinceChart.on("click", chartProvinceClick);
     				},
     				error : function() {
     					return null;
     				}
     			}); 
	   			 		 			
	     		//myChart.setOption(option);

                
             }
         );
  	 }
    
    
    // 地图(省图)
	function refreshProvinceMap(provinceCode) {

		var strUrl = '';
		if (provinceCode == undefined || provinceCode == "") {
			strUrl = '${contextPath}/f/getAddress';
		}else{
			strUrl = '${contextPath}/f/getAddressByProvinceCode?provinceCode=' + provinceCode;
		}
		
         $.ajax({
			url : strUrl,
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'json',
			async : true,
			cache : false,
			success : function(res) {
		           //更新数据
		           var option = myProvinceChart.getOption();
		           option.series[0].geoCoord = res.geoCoordMap;    
		           option.series[1].markLine.data = res.lineDataList; 
		          option.series[1].markPoint.data = res.pointDataList; 
		          myProvinceChart.setOption(option);
			},
			error : function() {
				return null;
			}
		}); 
	   			 		 			

  	 }
    
    
	
	
    // 地图（全国）
	function createChinaMap() {
		document.getElementById("mapDiv1").style.display="block";
		$("#mapDiv1").show();
  		// 路径配置
         require.config({
  	        packages: [
  	            {
  	                name: 'echarts',
  	                location: '${ctxStatic}/echart2',
  	                main: 'echarts'
  	            }
  	        ]
  	    });
  		
  		
  		
      	// 使用
         require(
             [
                 'echarts',
                 'echarts/chart/map' // 使 用柱状图就加载bar模块，按需加载
             ],
             function (ec) {
            	 
                 // 基于准备好的dom，初始化echarts图表
                 myChinaChart = ec.init(document.getElementById('mapDiv1'));
                 
                 
                 $.ajax({
	     				url : '${contextPath}/f/getAddress',
	     				type : 'Post',
	     				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	     				dataType : 'json',
	     				async : true,
	     				cache : false,
	     				success : function(res) {
	     					var showMaps = res.mapJsonInfo;
	   			 		 	var mapType = [];
	   			 		 	var mapGeoData = require('echarts/util/mapData/params');
	   			 		 	for (var city in showMaps) {
	   			 		   		mapType.push(city);
	   			 		    	// 自定义扩展图表类型
	   			 		    	mapGeoData.params[city] = {
		   			 		        getGeoJson: (function (mapIndex) {
		   			 		            var geoJsonName = showMaps[mapIndex];
		   			 		            return function (callback) {
		   			 		                $.getJSON('${ctxStatic}/mapjson/geoJson/' + geoJsonName + '.json', callback);
		   			 		            }
		   			 		        })(city)
		   			 		    }
		   			 		}

	   	                 
	   						option = {
	   	 							backgroundColor: '',
	   	 							color: ['gold','aqua','lime'],
	   	 							title : {
	   	 								text: '全国',
	   	 								subtext:'',
	   	 								padding:  [12, 0, 0, 50],
	   	 								x:'left',
	   	 								textStyle : {
	   	 									color: '#B2D4F7',
	   	 									fontWeight: 'bolder',
	   	 									fontFamily: '微软雅黑',
	   	 									fontSize:'20',
	   	 								}
	   	 							},

	   	 							toolbox: {
	   	 								show : false,
	   	 							},
	   	 							dataRange: {
	   	 								show : false,
	   	 								min : 0,
	   	 								max : 100,
	   	 								calculable : true,
	   	 								color: ['#ff3333', 'orange', 'yellow','lime','aqua'],
	   	 								textStyle:{
	   	 									color:'#fff'
	   	 								}
	   	 							},
	   	 							series : [
	   	 										{
	   	 											name: '全国',
	   	 											type: 'map',
	   	 											roam: false,
	   	 											hoverable: false,
	   	 											mapType: 'china',
	   	 											itemStyle:{
	   	 												normal:{
	   	 													label:{
	   	 														show:true,
	   	 														textStyle: {color: "#FFFFFF"}
	   	 													},
	   	 													//borderColor:'#689BF8',
	   	 													borderColor:'rgba(100,149,237,1)',
	   	 													borderWidth:1,
	   	 													areaStyle:{
	   	 														//color: '#1B1B1B'
	   	 														color: 'rgba(255, 255, 255, 0.0470588)'
	   	 													}
	   	 												}
	   	 											},
	   	 											data:[],
	   	 											markLine : {
	   	 												smooth:true,
	   	 												symbol: ['none', 'circle'],  
	   	 												symbolSize : 1,
	   	 												itemStyle : {
	   	 													normal: {
	   	 														color:'#fff',
	   	 														borderWidth:1,
	   	 														borderColor:'rgba(30,144,255,0.5)'
	   	 													}
	   	 												},
	   	 												data : [
	   	 													//这里是虚线
	   	 													/*
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'洛阳'}],
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'郑州'}],
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'宜昌'}],
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'重庆'}],
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'长沙'}],
	   	 													[{name:'鞍山聚龙现钞中心'},{name:'成都'}]
	   	 													*/
	   	 												],
	   	 											},
	   	 											geoCoord: res.geoCoordMap
	   	 										},
	   	 										{
	   	 											name: '鞍山聚龙现钞中心 Top10',
	   	 											type: 'map',
	   	 											mapType: 'china',
	   	 											data:[],
	   	 											
	   	 											markLine : {
	   	 												smooth:true,
	   	 												effect : {
	   	 													show: true,
	   	 													scaleSize: 1,
	   	 													period: 30,
	   	 													color: '#fff',
	   	 													shadowBlur: 10
	   	 												},
	   	 												itemStyle : {
	   	 													normal: {
	   	 													 	label : {show: false},
	   	 														borderWidth:1,
	   	 														lineStyle: {
	   	 															type: 'solid',
	   	 															shadowBlur: 10
	   	 														}
	   	 													}
	   	 												},
	   	 												data : res.lineDataList
	   	 											},
	   	 											markPoint : {
	   	 												symbol:'emptyCircle',
	   	 												symbolSize : function (v){
	   	 													return 10 + v/10
	   	 												},
	   	 												effect : {
	   	 													show: true,
	   	 													shadowBlur : 0
	   	 												},
	   	 												itemStyle:{
	   	 													normal:{
	   	 														label:{show:true}
	   	 													},
	   	 													emphasis: {
	   	 														label:{position:'top'}
	   	 													}
	   	 												},

	   	 											data : res.pointDataList
	   	 											}
	   	 										},
	   	 										
	   	 										
	   											{
	   	 											name: '鞍山聚龙现钞中心 Top1',
	   	 											type: 'map',
	   	 											mapType: 'china',
	   	 											data:[],
	   	 								           itemStyle:{
	   	 								               normal:{label:{show:true,textStyle: {color: "#FFFFFF"}}},
	   	 								               emphasis:{label:{show:true,textStyle: {color: "#FFFFFF"}}}
	   	 								           }, 
	   	 								           
	   	 								           
	   	 								           
	   	 								          markPoint : {  
	   	 							                    symbol: 'star',  
	   	 							                    symbolSize:0,  
	   	 							                    showDelay:0,  
	   	 							                    itemStyle:{  
	   	 							                        normal:{label:{  
	   	 							                                    show: true,  
	   	 							                                    position: 'left',  
	   	 							                                    formatter: '{b}'   
	   	 							                                  }  
	   	 							                                }  
	   	 							                          
	   	 							                    },  
	   	 							                data : res.pointDataList
	   	 							                }  
	   	 										}
	   	 										
	   	 									]
	   	                		};
	   	 					

	     					myChinaChart.setOption(option);
	     					myChinaChart.on("click", chartChinaClick);
	     				},
	     				error : function() {
	     					return null;
	     				}
	     			}); 
              
                
             }
         );
  	 }
    
    
    // 地图刷新
	function refreshMap() {
		//全国地图
		if (strmapTypeCode == '1'){
			refreshChinaMap();		
		}
		//全省地图
		if (strmapTypeCode == '2'){
			refreshProvinceMap(mapProvinceCode);
		}
		//全市地图
		if (strmapTypeCode == '3'){
			refreshCityMap(mapCityCode);
		}
    }
    
    
    
    // 地图刷新（全国）
	function refreshChinaMap() {
          $.ajax({
			url : '${contextPath}/f/getAddress',
			type : 'Post',
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'json',
			async : true,
			cache : false,
			success : function(res) {
	           //更新数据
	           var option = myChinaChart.getOption();
	           option.series[0].geoCoord = res.geoCoordMap;    
	           option.series[1].markLine.data = res.lineDataList; 
	          option.series[1].markPoint.data = res.pointDataList; 
	          option.series[2].markPoint.data = res.pointDataList; 
	          myChinaChart.setOption(option);
			},
			error : function() {
				return null;
			}
		}); 
  	 }
    
    
    function chartChinaClick(param){ 
        var selectedPro = param.name;
    	var url = "${contextPath}/f/getMapCode?mapName=" + selectedPro + "&mapType=" + strmapTypeCode;
    	var newUrl  = encodeURI(url);
		var boo=true;
		$.ajax({
			url : newUrl,
			type : 'POST',
			async: false,
			dataType : 'json',
			data : {},
			success : function(serverResult, textStatus) {
				if (serverResult.result=="success") {
					var strMapCode = serverResult.MapCode;
 					if ((strMapCode == undefined || strMapCode == "")) {
 						alert('地图加载失败：地图编号不存在！');
 						return;
 						
 					}
 					mapCode = strMapCode;
 					mapProvinceCode = mapCode;
 					document.getElementById("mapDiv1").useType="1";			//被使用过
					document.getElementById("mapDiv1").style.display="none";//隐藏
					
			        createProvinceMap(strMapCode);
			        document.getElementById("mapDiv2").style.display="block";//显示
			        strmapTypeCode = "2";
 					//数据刷新
 					refreshAmountData(); 
 					refreshServiceData();  
				}
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
			}
		});
		return boo;
    	
     };
    
     function chartProvinceClick(param){ 
         var selectedPro = param.name;
     	var url = "${contextPath}/f/getMapCode?mapName=" + selectedPro + "&mapType=" + strmapTypeCode;
     	var newUrl  = encodeURI(url);
 		var boo=true;
 		$.ajax({
 			url : newUrl,
 			type : 'POST',
 			async: false,
 			dataType : 'json',
 			data : {},
 			success : function(serverResult, textStatus) {
 				if (serverResult.result=="success") {
 					var strMapCode = serverResult.MapCode;
 					if ((strMapCode == undefined || strMapCode == "")) {
 						alert('地图加载失败：地图编号不存在！');
 						return;
 						
 					}
 					mapCode = strMapCode;
 					document.getElementById("mapDiv2").useType="1";			//被使用过
 					document.getElementById("mapDiv2").style.display="none";//隐藏
 					mapCityCode = strMapCode;
 					createCityMap(strMapCode);
 			        document.getElementById("mapDiv3").style.display="block";//显示
 			       strmapTypeCode = "3";
 					//数据刷新
 					refreshAmountData(); 
 					refreshServiceData();   
 				}
 			},
 			error : function(XmlHttpRequest, textStatus, errorThrown) {
 			}
 		});
 		return boo;
     	
      };
      
    
    
	function RandomNumBoth(Min,Max){
		try{
	      var Range = Max - Min;
	      var Rand = Math.random();
	      var num = Min + Math.round(Rand * Range); //四舍五入
	      return num;
		}catch(e){
			return "0";
		}

	}
    
	function getBarValue(v_value){
		try{
	      var num = 0;
	      if (v_value * 1 > 10){
	    	  num =  v_value * 1 - 10;
	      }
	      return num;
		}catch(e){
			return 0;
		}

	}
    
	
	
	
	
    
    var pageData= "";
    var atmDataP;
    var doorListP;
    var pageServiceData= "";
    var businessDataP= "";

	function setPageData() {
		var strValue01 = numChange(pageData.toDayBankUpAmount);
		var strValue02 = numChange(pageData.toDayBankBackAmount);
		var strValue03 = numChange(pageData.toDayClearAmount);
		var strValue04 = numChange(pageData.toDayClearErrorAmount);
		var strValue05 = numChange(pageData.totalBankUpAmount);
		var strValue06 = numChange(pageData.totalBankBackAmount);
		var strValue07 = numChange(pageData.totalClearAmount);
		var strValue08 = numChange(pageData.totalClearErrorAmount);
		setPageValue(pageData.toDayBankUpAmount,'1');
		setPageValue(pageData.toDayBankBackAmount,'2');
		setPageValue(pageData.toDayClearAmount,'3');
		setPageValue(pageData.toDayClearErrorAmount,'4');
		setPageValue(pageData.totalBankUpAmount,'5');
		setPageValue(pageData.totalBankBackAmount,'6');
		setPageValue(pageData.totalClearAmount,'7');
		setPageValue(pageData.totalClearErrorAmount,'8');
		//自助设备服务统计刷新
		  if(atmDataP){
	           var atmData1=new Array();
	           for(i = 0;i<atmDataP.length;i++){
	               var  rowObj = atmDataP[i];
	               atmData1[i] = {value:rowObj.atmAmount, name:rowObj.officeName,itemStyle:{ normal:{color: arrRingColor[i*1+1]} }};
	           }
	           //更新数据
	           var option = leftRingChart.getOption();
	           option.series[0].data = atmData1;    
	           option.series[1].data = atmData1;    
	           leftRingChart.setOption(option);
      	  }
		//上门收款服务统计刷新
		  if(doorListP){
	           var atmData2=new Array();
	           for(i = 0;i<doorListP.length;i++){
	               var  rowObj = doorListP[i];
	               atmData2[i] = {value:rowObj.businessPct, name:rowObj.officeName,itemStyle:{ normal:{color: arrRingColor[i*1+1]} }};
	           }
	           //更新数据
	           var option = rightRingChart.getOption();
	           option.series[0].data = atmData2;    
	           option.series[1].data = atmData2;    
	           rightRingChart.setOption(option);
      	  }

		//今日柱状图的刷新
        //var data = [strValue01, strValue02, strValue03, strValue04];
        //var option = toDayBarChart.getOption();
        //option.series[0].data = data;   
        //toDayBarChart.setOption(option);
        
        
		//合计柱状图的刷新
        //var data2 = [strValue05, strValue06, strValue07, strValue08];
        //var option2 = totalBarChart.getOption();
        //option2.series[0].data = data2;   
        //totalBarChart.setOption(option2);
        
        
	}
	
	//服务数据的设定
	function setPageServiceData() {
		setPageServiceValue(pageServiceData.clearCount,'1'); 				//服务清分业务
		setPageServiceValue(pageServiceData.atmCount,'2');					//加钞自助设备(ATM)
		setPageServiceValue(pageServiceData.goldBankCount,'3');			//服务金库业务
		setPageServiceValue(pageServiceData.atmCustCount,'4')				//服务自助设备客户(ATM)
		setPageServiceValue(pageServiceData.doorCustCount + "/" +  pageServiceData.doorBusinessCount,'5'); //上门收款门店/商户
		setPageServiceValue(pageServiceData.doorGoldBankCount,'6');		//服务上门收款

		//数字化金融服务平台服务统计刷新
		  if(businessDataP){
	           var arrNameData=new Array();
	           var arrData2=new Array(7);
	           var arrData3=new Array(7);
	           var arrColorData=new Array();
	           var arrTemp1=new Array();
	           var arrTemp2=new Array();
	           for(i = 0;i<businessDataP.length;i++){
	               var  rowObj = businessDataP[i];
	               arrNameData[i] = rowObj.officeName;
	               arrTemp1[i] =getBarValue(rowObj.maxPct);
	               arrTemp2[i] =rowObj.maxPct;
	               arrColorData[i] = arrServiceColor[rowObj.maxName];
	           }
	           arrData2[0] = arrTemp1;
	           arrData3[0] = arrTemp2;
	           colorList1 = arrColorData;
	           
	           //更新数据
	           var option = sevenDayChart1.getOption();
	           option.xAxis[0].data = arrNameData;       
	           option.series[0].data = arrData2[0];      
	           option.series[1].data = arrData3[0];      
	           sevenDayChart1.setOption(option);
	           

	           for(var k=0;k<arrServiceNm.length;k++){
	 	           arrTemp1=new Array();
		           arrTemp2=new Array();
		           for(i = 0;i<businessDataP.length;i++){
		        	   var  listObj = businessDataP[i];
		        	   for(j = 0;j<listObj.businessList.length;j++){
		        		   var  rowObj = listObj.businessList[j];
		        		   if (arrServiceNm[k] == rowObj.businessName){
				               arrTemp1[i] =getBarValue(rowObj.businessPct);
				               arrTemp2[i] =rowObj.businessPct; 
		        		   }
			               
		        	   }
		           }
		           arrData2[k*1+1] = arrTemp1;
		           arrData3[k*1+1] = arrTemp2;
	        	}

	           
	           //更新数据
	           setChartOption(sevenDayChart1,arrNameData,arrData2[0],arrData3[0]);
	           setChartOption(sevenDayChart2,arrNameData,arrData2[1],arrData3[1]);
	           setChartOption(sevenDayChart3,arrNameData,arrData2[2],arrData3[2]);
	           setChartOption(sevenDayChart4,arrNameData,arrData2[3],arrData3[3]);
	           setChartOption(sevenDayChart5,arrNameData,arrData2[4],arrData3[4]);
	           setChartOption(sevenDayChart6,arrNameData,arrData2[5],arrData3[5]);
	           setChartOption(sevenDayChart7,arrNameData,arrData2[0],arrData3[0]);
      	  }
		
	}
	
	function setChartOption(obj,arrNameData,arrData2,arrData3){
        var option = obj.getOption();
        option.xAxis[0].data = arrNameData;       
        option.series[0].data = arrData2;      
        option.series[1].data = arrData3;      
        obj.setOption(option);
	}
	
	//数据刷新
	function refreshAmountData(){

		var boo=true;
		$.ajax({
			url : "${contextPath}/f/refreshAmountData?mapCode=" + mapCode + "&mapType=" + strmapTypeCode,
			type : 'POST',
			async: false,
			dataType : 'json',
			data : {},
			success : function(serverResult, textStatus) {
				if (serverResult.result=="success") {
					pageData = serverResult.clearScreenMain;
					atmDataP = serverResult.atmList;
					doorListP = serverResult.doorList;
					
					//alert(serverResult.atmList);
					//alert(serverResult.doorList);
					//dataChange();
					setPageData();
				}
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
			}
		});
		return boo;
	}
	
	
	
	
	//数据刷新
	function refreshServiceData(){
		var boo=true;
		$.ajax({
			url : "${contextPath}/f/refreshServiceData?mapCode=" + mapCode + "&mapType=" + strmapTypeCode,
			type : 'POST',
			async: false,
			dataType : 'json',
			data : {},
			success : function(serverResult, textStatus) {
				if (serverResult.result=="success") {
					pageServiceData = serverResult.clearScreenMain;
					businessDataP = serverResult.serviceList;
					
					setPageServiceData();
				}
				//if (serverResult.result=="error") {
				//}
			},
			error : function(XmlHttpRequest, textStatus, errorThrown) {
			}
		});
		return boo;
	}
	
	
	
	
	function setPageValue(v_num,v_kbn) {
		
		if (v_num == undefined || v_num == "") {
			v_num = 0;
		}
		
		
		
		var strColor = arrColor[v_kbn];
		var strValue = "";
		var strKbn = arrType[v_kbn];
		strValue = formatFloat(v_num);
		var strShowValue = $('#hid_numDiv' + v_kbn).val();
		//if (strShowValue != strValue){
			$('#numDiv' + v_kbn).vTicker({
				nextValue: showValue(strColor,strValue,strKbn)
			});
		//}
		$('#hid_numDiv' + v_kbn).val(strValue);
	}
	
	function setPageServiceValue(v_num,v_kbn) {
		if (v_num == undefined || v_num == "") {
			v_num = "0";
		}
		if (v_kbn == "5"){
			if(v_num.indexOf("undefined")>=0 || v_num == "/"){
				v_num = "0/0";
			}
		}
		var strValue = "";

		$('#numServiceDiv' + v_kbn).vTicker({
			nextValue: showValue(serviceColor,v_num,"3")
		});
	}
	
	
	
	
	/**
	千分位用逗号分隔
	 */
	 function formatFloat(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
			return (((sign) ? '' : '-') + num + '.' + cents);
		}
	
	
	
	/**
	千分位用逗号分隔
	 */
	 function formatNum(num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))
				num = "0";
			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();
			if (cents < 10)
				cents = "0" + cents;
			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
			return (((sign) ? '' : '-') + num);
		}

	function showValue(v_color,v_value,v_kbn) {
		if (v_color == ""){
			v_color = "#FFFFFF";
		}
		if (v_kbn == "1"){
			return "<span style='font-size:44px;color:" + v_color + ";'>" + v_value + "</span><span style='font-size:32px;color:" + v_color + ";'>万</span>";
		}
		if (v_kbn == "2"){
			return "<span style='font-size:33px;color:" + v_color + ";'>￥" + v_value + "</span>";
		}
		
		if (v_kbn == "3"){
			return "<span style='font-size:32px;color:" + v_color + ";'>" + v_value + "</span>";
		}
		
		
		
	}


	function numChange(num) {
		if (num == undefined || num == "") {
			num = 0;
		}
		
		var num = num > 9999 ? Math.floor(num/10000) : num; 
		return num;
	}
	//地图返回按钮区域鼠标移入时
	function overShow() {
		
		
		if (strmapTypeCode == '1'){
			return;
		}
		//全省地图
		if (strmapTypeCode == '2'){
			if (document.getElementById("mapDiv1").useType == '0') {
				return;
			}
		}
		
		if (strmapTypeCode == '3'){
			if (document.getElementById("mapDiv2").useType == '0') {
				return;
			}
		}
		
		
		document.getElementById("imgSplit").style.display="block";//显示	
	}
	//地图返回按钮区域鼠标移出时
	function outHide() {
		document.getElementById("imgSplit").style.display="none";//隐藏
	}
	
	//地图返回按钮按下时
	function changeMap() {
		if (strmapTypeCode == '1'){
			return;
		}
		//全省地图
		if (strmapTypeCode == '2'){
			if (document.getElementById("mapDiv1").useType == '1') {
				document.getElementById("mapDiv2").style.display="none";//隐藏
		        document.getElementById("mapDiv1").style.display="block";//显示
		        strmapTypeCode = "1";
			}
		}
		
		if (strmapTypeCode == '3'){
			if (document.getElementById("mapDiv2").useType == '1') {
				document.getElementById("mapDiv3").style.display="none";//隐藏
		        document.getElementById("mapDiv2").style.display="block";//显示
		        strmapTypeCode = "2";
		        mapCode = mapProvinceCode;
			}
		}
		
		//数据刷新
		refreshAmountData(); 
		refreshServiceData();      	
		
		
	}
	
	
	</script>
</head>
 <body style="width: 100%; height: 100%; background: url(&quot;${ctxStatic}/images/screen_clear_bg.jpg&quot;) 0% 0% / 100%; ">
<form:form id="clearScreenForm" modelAttribute="clearScreenMain" action="" method="post" >

	<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
		<tr height="770px">
			<td style="width: 100%; height: 100%;" >
			<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
				<tr height="100px">
					<td style="width: 500px;" >
						<table style="width: 100%;height: 100%" cellspacing="0" cellpadding="0" border=0>
							<tr height="50px"></tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">今日</span>
									<span  style="font-size:20px;color:#FFFFFF;">商行上缴额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv1" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">今日</span>
									<span  style="font-size:20px;color:#FFFFFF;">商行取款额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv2" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 45px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">今日</span>
									<span  style="font-size:20px;color:#FFFFFF;">清分中心清点额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv3" liHeight="45px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">今日</span>
									<span  style="font-size:20px;color:#FFFFFF;">清分差错额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv4" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr  height="10px"></tr>
							<tr height="350px">
								<td >
								</td>
								<td colspan="2">
									<div id="leftRingChart"  style="margin-right:-60px;width: 520px;height:350px;"></div>
								</td>
							</tr>
						</table>
					</td>
					
					<td>
						<table style="width: 100%;height: 100%" cellspacing="0" cellpadding="0" border=0>
							<tr height="100px">
								<td>
								</td>
							</tr>
							<tr height="10px">
								<td>
								</td>
							</tr>
							
							<tr  height="660px" >
								<td>
									<div  onmouseover="overShow()" onmouseout="outHide()" style="z-index:9999;position:absolute;left:500px;top:350px;padding-top:50px;padding-left:10px; background-color:rgba(0,0,0,0); width: 40px;height: 200px">
										<img id="imgSplit" onclick="changeMap()"  style="display:none;" src="${ctxStatic}/images/splitter_h_l.gif"/>
									</div>

									<div id="mapDiv1" useType="0" style="height:660px;width:900px;display:none;"></div>
									<div id="mapDiv2" useType="0" style="height:660px;width:900px;display:none;"></div>
									<div id="mapDiv3" useType="0" style="height:660px;width:900px;display:none;"></div>
								</td>
							</tr>
						</table>
					</td>
					<td style="width: 500px;">
						<table style="width: 100%;height: 100%" cellspacing="0" cellpadding="0" border=0>
							<tr height="40px">
								<td style="width: 20px;">
								</td>
								<td>
								</td>
								<td  style="width: 200px;padding-left:2px;padding-bottom:2px" align="left" valign="middle">
								<nobr>
								<img  src="${ctxStatic}/images/screenTime.png"/>
								<input 	id="sysTime" 
										type="text" 
										readonly="readonly" 
										onfocus="this.blur()"
										style="padding-top:9px;width: 170px;height:30px;font-size:17px;color:#FFFFFF;border:0px; background-color: transparent; " 
										maxlength="20"  
										value=""/>
								</nobr>
								</td>
							</tr>
							<tr height="10px"></tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">商行</span>
									<span  style="font-size:20px;color:#FFFFFF;">累计上缴额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv5" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">商行</span>
									<span  style="font-size:20px;color:#FFFFFF;">累计取款额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv6" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">清分中心</span>
									<span  style="font-size:20px;color:#FFFFFF;">累计清点额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv7" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr height="45px">
								<td style="width: 50px;"></td>
								<td colspan="2" >
									<span  style="font-size:24px;color:#FFFFFF;">清分中心</span>
									<span  style="font-size:20px;color:#FFFFFF;">累计差错额</span>
								</td>
							</tr>
							<tr height="45px">
								<td >
								</td>
								<td colspan="2" style="padding-right:20px;" class="text-right" align="right" >
									<table style="width: 100%;height: 100%;border-bottom:#286BAF solid 2px;" cellspacing="0" cellpadding="0" border=0>
										<tr>
											<td style="text-align:right;"  align="right" >
												<sys:labelScroll id="numDiv8" liHeight="50px"/>
											</td>
											<td style="width: 40px;" align="center">
												<span style='font-size:20px;color:#A7CBEA;'>(元)</span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr  height="10px"></tr>
							<tr height="350px">
								<td >
								</td>
								<td colspan="2" >
									<div id="rightRingChart"  style="margin-left:-60px;width: 520px;height:350px;"></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
		
			</table>
			</td>
		</tr>
		<tr>
			<td style="width: 100%; height: 100%;" >
				<table style="width: 100%;height: 100%" cellspacing="0" cellpadding="0" border=0>
					<tr height="340px">
						<td align="left" >
							<div align="left" style="margin-left:14px;margin-top:-100px;width: 690px; height: 190px; border: none; background: rgba(255, 255, 255, 0.0470588); border-radius: 8px;">
								<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
									<tr height="15px">
									</tr>
									<tr>
										<td align="right" valign="middle" style="width: 33%; border-right:#2E67A5 1px solid;">
											<table style="padding-right:15px;width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
												<tr height="30px">
													<td valign="top" align="right" >
														<span style="font-size:20px;color:#B2D1F5;">服务清分业务</span>
													</td>
												</tr>
												
												<tr height="45px">
													<td align="right" >
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv1" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(家)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr height="30px">
													<td align="right">
														<span style="font-size:20px;color:#B2D1F5;">加钞自助设备</span><span style="font-size:12px;color:#B2D1F5;">(ATM)</span>
													</td>
												</tr>
												<tr valign="bottom" height="45px">
													<td align="right" >
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv2" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(台)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
										<td align="right" style="width: 33%;border-right:#2E67A5 1px solid;">
											<table style="padding-right:15px;width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
												<tr height="30px">
													<td valign="top" align="right" >
														<span style="font-size:20px;color:#B2D1F5;">服务金库业务</span>
													</td>
												</tr>
												
												<tr height="45px">
													<td align="right" >
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv3" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(家)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr height="30px">
													<td align="right" >
														<span style="font-size:20px;color:#B2D1F5;">服务自助设备</span><span style="font-size:12px;color:#B2D1F5;">(ATM)</span><span style="font-size:20px;color:#B2D1F5;">客户</span>
													</td>
												</tr>
												<tr valign="bottom" height="45px">
													<td align="right" >
													
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv4" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(家)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
										<td align="right" style="width: 33%;">
											<table style="padding-right:15px;width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
												<tr height="30px">
													<td valign="top" align="right" >
														<span style="font-family:微软雅黑;font-size:19px;color:#B2D1F5;">上门收款门店/商户</span>
													</td>
												</tr>
												
												<tr height="45px">
													<td align="right" >
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv5" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(家)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr height="30px">
													<td align="right" >
														<span style="font-size:20px;color:#B2D1F5;">服务上门收款</span>
													</td>
												</tr>
												<tr valign="bottom" height="45px">
													<td align="right" >
														<table style="width: 100%; height: 100%;" cellspacing="0" cellpadding="0" border=0>
															<tr style="height: 100%;">
																<td align="right" valign="top">
																	<sys:labelScroll id="numServiceDiv6" liHeight="50px"/>
																</td>
																<td align="center" valign="middle" style="width: 40px;padding-top:9px;" >
																	<span style="font-size:18px;color:#B2D1F5;">(家)</span>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr height="15px">
									</tr>
								</table>
							</div>
						</td>
						
						<td align="left" style="width: 900px;">
							<div class="box4" style="margin-top:-120px;margin-left:-20px;">
							  <div class="border4" id="div1" >
							    <div id="sevenDayChart1"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart2"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart3"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart4"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart5"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart6"  style="width: 1200px;height:280px;"></div>
							    <div id="sevenDayChart7"  style="width: 1200px;height:280px;"></div>								    
							    
							    
							    
							    
							  </div>
							</div>
						</td>
						<td align="left" style="width: 90px;">
							<table style="margin-top:0px;margin-left:-70px;width: 80px;height: 100%" cellspacing="0" cellpadding="0" border=0>
								<tr height="25px">
									<td style="width: 23px">
										<div class="circle1" style="background-color:#FFD89D; display:inline-block ; "></div>
									</td>
									<td>
										<span style="display:inline-block ; font-size:12px;color:#B2D1F5;">清分服务</span>
									</td>
								</tr>
								<tr height="25px">
									<td style="width: 23px">
										<div class="circle1" style="background-color:#7DCFF4; display:inline-block ; "></div>
									</td>
									<td>
										<span style="display:inline-block ; font-size:12px;color:#B2D1F5;">发行库</span>
									</td>
								</tr>
								<tr height="25px">
									<td style="width: 23px">
										<div class="circle1" style="background-color:#B1EE91; display:inline-block ; "></div>
									</td>
									<td>
										<span style="display:inline-block ; font-size:12px;color:#B2D1F5;">现金库</span>
									</td>
								</tr>
								<tr height="25px">
									<td style="width: 23px">
										<div class="circle1" style="background-color:#F7716C; display:inline-block ; "></div>
									</td>
									<td>
										<span style="display:inline-block ; font-size:12px;color:#B2D1F5;">上门收款</span>
									</td>
								</tr>
								<tr height="25px">
									<td style="width: 23px">
										<div class="circle1" style="background-color:#5680E2; display:inline-block ; "></div>
									</td>
									<td>
										<span style="display:inline-block ; font-size:12px;color:#B2D1F5;">自助设备</span>
									</td>
								</tr>
								<tr >
								</tr>
							</table>
						</td>
					</tr>
					<tr ></tr>
				</table>

			</td>
		</tr>
			
	</table>
	<script type="text/javascript">
	
	
	$(function(){
		$('#numDiv1').vTicker({
			nextValue: showValue(arrColor[1],formatFloat("${clearScreenMain.toDayBankUpAmount}"),"2")
		});
		$('#hid_numDiv1').val(formatFloat("${clearScreenMain.toDayBankUpAmount}"));
	});
	$(function(){
		$('#numDiv2').vTicker({
			nextValue: showValue(arrColor[2],formatFloat("${clearScreenMain.toDayBankBackAmount}"),"2")
		});
		$('#hid_numDiv2').val(formatFloat("${clearScreenMain.toDayBankBackAmount}"));
	});
	$(function(){
		$('#numDiv3').vTicker({
			nextValue: showValue(arrColor[3],formatFloat("${clearScreenMain.toDayClearAmount}"),"2")
		});
		$('#hid_numDiv3').val(formatFloat("${clearScreenMain.toDayClearAmount}"));
	});
	$(function(){
		$('#numDiv4').vTicker({
			nextValue: showValue(arrColor[4],formatFloat("${clearScreenMain.toDayClearErrorAmount}"),"2")
		});
		$('#hid_numDiv4').val(formatFloat("${clearScreenMain.toDayClearErrorAmount}"));
	});
	
	$(function(){
		$('#numDiv5').vTicker({
			nextValue: showValue(arrColor[5],formatFloat("${clearScreenMain.totalBankUpAmount}"),"2")
		});
		$('#hid_numDiv5').val(formatFloat("${clearScreenMain.totalBankUpAmount}"));
	});
	$(function(){
		$('#numDiv6').vTicker({
			nextValue: showValue(arrColor[6],formatFloat("${clearScreenMain.totalBankBackAmount}"),"2")
		});
		$('#hid_numDiv6').val(formatFloat("${clearScreenMain.totalBankBackAmount}"));
	});
	$(function(){
		$('#numDiv7').vTicker({
			nextValue: showValue(arrColor[7],formatFloat("${clearScreenMain.totalClearAmount}"),"2")
		});
		$('#hid_numDiv7').val(formatFloat("${clearScreenMain.totalClearAmount}"));
	});
	$(function(){
		$('#numDiv8').vTicker({
			nextValue: showValue(arrColor[8],formatFloat("${clearScreenMain.totalClearErrorAmount}"),"2")
		});
		$('#hid_numDiv8').val(formatFloat("${clearScreenMain.totalClearErrorAmount}"));
	});

	
	//服务数据的设定
	$(function(){
		$('#numServiceDiv1').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.clearCount}","3")
		});
	});
	$(function(){
		$('#numServiceDiv2').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.atmCount}","3")
		});
	});
	$(function(){
		$('#numServiceDiv3').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.goldBankCount}","3")
		});
	});
	$(function(){
		$('#numServiceDiv4').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.atmCustCount}","3")
		});
	});
	$(function(){
		$('#numServiceDiv5').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.doorCustCount}/${clearScreenMain.doorBusinessCount}","3")
		});
	});
	$(function(){
		$('#numServiceDiv6').vTicker({
			nextValue: showValue(serviceColor,"${clearScreenMain.doorGoldBankCount}","3")
		});
	});
	
	
	</script>

	
	
	
	
</form:form>
 </body>
</html>