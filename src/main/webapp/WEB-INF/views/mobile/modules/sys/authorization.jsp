<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE >
<html>
<head>
    <meta charset="utf-8">

    <title>${fns:getConfig('productName')}</title>
    <!--<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
    <link rel="stylesheet" href="${ctxStatic}/jingle/css/app.css">   -->
    
	<!-- 开始设置桌面图标，自适应屏幕   -->
	<meta name="apple-mobile-web-app-capable" content="yes">
       <meta name="apple-mobile-web-app-status-bar-style" content="black">
       <meta name="viewport" content="width=device-width,initial-scale=1, minimum-scale=1.0, maximum-scale=1, user-scalable=no">
       <meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=no,maximum-scale=1.0">
	<!-- 结束设置桌面图标，自适应屏幕   -->

	<link rel="stylesheet" href="${ctxStatic}/jqueryMobile/css/jquery.mobile-1.3.2.min.css">
	<script src="${ctxStatic}/jqueryMobile/js/jquery.js"></script>
	<script src="${ctxStatic}/jqueryMobile/js/jquery.mobile-1.3.2.min.js"></script>
<link rel="stylesheet" href="${ctxStatic}/jqueryMobile/css/modify.css">
	<style type="text/css">
		.table thead{background-color:#bbdcfc !important;color:#333;border:1px solid #fff !important;}
		table a{color:#0066cc !important;}
		table a:hover{color:#ff6600 !important;}
		tr {
		    border-bottom: 1px solid #d6d6d6;height:30px;
		}
.ui-content{background-color: #f0f0f0;}
		input[type="text"]{
		    border: 1px solid #ccc;
		    padding: 2px;
		    color: #444;
		    width: 100%;
		    height: 35px;
		    font-size:10px;
		    font-size:1rem;border-radius:10px;
		}
		input[type="tel"]{
		    border-radius:10px;
		}
		select{border-radius:10px;}
    </style>

	<script type="text/javascript">

	
		
		
		//提交页面
        function formSubmit()
        {
        	var gofficeId=$("#gofficeId").val();//取值
			 if(gofficeId == null || gofficeId=='' ) {
				 $("#doorId").popup('open');
				 setInterval(function(){
					   $("#doorId").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
        	var gname=$("#gname").val();//取值
			 if(gname == null || gname=='' ) {
				 $("#gname1").popup('open');
				 setInterval(function(){
					   $("#gname1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
			 var idcard=$("#gidcardNo").val();//取值
			 if(idcard == null || idcard=='' ) {
				 $("#gidcardNo1").popup('open');
				 setInterval(function(){
					   $("#gidcardNo1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
			 if(idcard.match(/^[\u4e00-\u9fa5]+$/)){
				 $("#gidcardNo2").popup('open');
				 setInterval(function(){
					   $("#gidcardNo2").popup("close");
					}, 2000);//两秒后关闭
					return;
			 }
			 var gphone=$("#gphone").val();//取值
			 if(gphone == null || gphone=='' ) {
				 $("#gphone1").popup('open');
				 setInterval(function(){
					   $("#gphone1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
			 /*  var reg=/^1[0-9]{10}/; //验证规则 */
			  var reg = /^1[3|4|5|7|8][0-9]{9}/; //验证规则
			 
			 if(!reg.test(gphone)){
				 $("#gphone2").popup('open');
				 setInterval(function(){
					   $("#gphone1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
			 
			 var loginDate=$("#loginDate").val();//取值
			 if(loginDate == null || loginDate=='' ) {
				 $("#loginDate1").popup('open');
				 setInterval(function(){
					   $("#loginDate1").popup("close");
					}, 2000);//两秒后关闭
				 return;
			 }
			 
			
			 
		
		
		idcard = idcard.toString();
		//var Errors=new Array("验证通过!","身份证号码位数不对!","身份证号码出生日期超出范围或含有非法字符!","身份证号码校验错误!","身份证地区非法!");
		var Errors=new Array(true,false,false,false,false);
		var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}
		var idcard,Y,JYM;
		var S,M;
		var idcard_array = new Array();
		idcard_array = idcard.split("");
		//地区检验
		if(area[parseInt(idcard.substr(0,2))]==null){
			
			$("#gidcardNo2").popup('open');
			 setInterval(function(){
				   $("#gidcardNo2").popup("close");
				}, 2000);//两秒后关闭
			return ;
		} 
		//身份号码位数及格式检验
		switch(idcard.length){
			case 15:
				if ( (parseInt(idcard.substr(6,2))+1900) % 4 == 0 || ((parseInt(idcard.substr(6,2))+1900) % 100 == 0 && (parseInt(idcard.substr(6,2))+1900) % 4 == 0 )){
					ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/;//测试出生日期的合法性
				} else {
					ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/;//测试出生日期的合法性
				}
				if(ereg.test(idcard)){
					
					$.mobile.showPageLoadingMsg(); 
			           var formData = $("#inputForm").serialize();
			           $.ajax({
			               type: "POST",
			               url: "${ctx}/wechatAccount/authorizationSave",
			               cache: false,
			               data: formData,
			               success: onSuccess,
			               error: onError
			           });
			           return false;
				} 
					
				else {$("#gidcardNo2").popup('open');
				 setInterval(function(){
					   $("#gidcardNo2").popup("close");
					}, 2000);//两秒后关闭
				return;
				break;}
			case 18:
				//18 位身份号码检测
				//出生日期的合法性检查
				//闰年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))
				//平年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))
				if ( parseInt(idcard.substr(6,4)) % 4 == 0 || (parseInt(idcard.substr(6,4)) % 100 == 0 && parseInt(idcard.substr(6,4))%4 == 0 )){
					ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/;//闰年出生日期的合法性正则表达式
				} else {
					ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/;//平年出生日期的合法性正则表达式
				}
				if(ereg.test(idcard)) {//测试出生日期的合法性
					//计算校验位
					S = (parseInt(idcard_array[0]) + parseInt(idcard_array[10])) * 7
						+ (parseInt(idcard_array[1]) + parseInt(idcard_array[11])) * 9
						+ (parseInt(idcard_array[2]) + parseInt(idcard_array[12])) * 10
						+ (parseInt(idcard_array[3]) + parseInt(idcard_array[13])) * 5
						+ (parseInt(idcard_array[4]) + parseInt(idcard_array[14])) * 8
						+ (parseInt(idcard_array[5]) + parseInt(idcard_array[15])) * 4
						+ (parseInt(idcard_array[6]) + parseInt(idcard_array[16])) * 2
						+ parseInt(idcard_array[7]) * 1
						+ parseInt(idcard_array[8]) * 6
						+ parseInt(idcard_array[9]) * 3 ;
					Y = S % 11;
					M = "F";
					JYM = "10X98765432";
					M = JYM.substr(Y,1);//判断校验位
					if(M == idcard_array[17]) 
					{
						
						$.mobile.showPageLoadingMsg(); 
				           var formData = $("#inputForm").serialize();
				           $.ajax({
				               type: "POST",
				               url: "${ctx}/wechatAccount/authorizationSave",
				               cache: false,
				               data: formData,
				               success: onSuccess,
				               error: onError
				           });
				           return false;
					} 
					else {$("#gidcardNo2").popup('open');
					 setInterval(function(){
						   $("#gidcardNo2").popup("close");
						}, 2000);//两秒后关闭
					return;}
				}
				else {$("#gidcardNo2").popup('open');
				 setInterval(function(){
					   $("#gidcardNo2").popup("close");
					}, 2000);//两秒后关闭
				return;
				break;}
			default:
			{$("#gidcardNo2").popup('open');
			 setInterval(function(){
				   $("#gidcardNo2").popup("close");
				}, 2000);//两秒后关闭
			return;
			break;}
		}
	
			 
			 
           $.mobile.showPageLoadingMsg(); 
           var formData = $("#inputForm").serialize();
           $.ajax({
               type: "POST",
               url: "${ctx}/wechatAccount/authorizationSave",
               cache: false,
               data: formData,
               success: onSuccess,
               error: onError
           });
           return false;
        }
		
		//清空页面
        function formClear()
        {
        	$('#gname').val("");
        	$('#gphone').val("");
        	$('#gidcardNo').val("");
        	
        }
		
        function UpdateStatus() {
        	 if(confirm("确定要清空数据吗？"))
        	 {

        	 }else{
        		 
        	 }
        	}
        
        //返回处理
        function formBack() {
        	WeixinJSBridge.call('closeWindow');
       	}
   
        
        window.onload = function () { 
        	var strResult = "${result}";
        	if(strResult == undefined || strResult=="" || strResult==null){ 
        		return;
        	}
        	if(strResult == "1"){
        		alert('保存成功!');
        	}
        }
        
        
        var initialScreenSize = window.innerHeight;
        window.addEventListener("resize", function() {
         if(window.innerHeight < initialScreenSize){
         }
         else{
        	    var isFocus=$("#detailAmount").is(":focus");  
        	    if(true==isFocus){  
        	    	$("#detailAmount").blur(); 
        	    }  
         }
        });
        
        
        function onSuccess(data, status)
        {
        	$.mobile.hidePageLoadingMsg(); 
        	$("#success").popup('open');
        	setInterval(function(){
				   $("#success").popup("close");
				}, 2000);//两秒后关闭
				return;
        }
  
  
        function onError(data, status)
        {
        	$.mobile.hidePageLoadingMsg(); 
        	$("#failure").popup('open');
        	setInterval(function(){
				   $("#failure").popup("close");
				}, 2000);//两秒后关闭
				return;
        }        
  

        
	</script>
</head>
<body>	
	<div data-role="page" id="pageone" >
	<!-- 弹窗设置 -->
		<div data-role="popup" id="success" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;保存成功！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>

		<div data-role="popup" id="failure" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;保存失败！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="doorId" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;门店不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="gname1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;用户名不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
		<div data-role="popup" id="gidcardNo1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;身份证号不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
			<div data-role="popup" id="gidcardNo2" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;身份证号有误！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
			<div data-role="popup" id="gphone1" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;电话号码不能为空！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>
			<div data-role="popup" id="gphone2" data-position-to="window">
			<br>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;请输入正确的电话号码！&nbsp;&nbsp;&nbsp;</h3>
			<br>
		</div>						
	<!-- end -->	
		<div data-role="header" data-position="fixed" data-theme="b">
			<a href="javascript:void(0);" onclick="formBack()" data-icon="back" data-ajax="false" data-rel="back">返回</a>
				<h1>授权申请</h1>
		</div>
		<div data-role="content">
			<form method="post" id="inputForm" action="${ctx}/wechatAccount/authorizationSave" >
				<div class="order-con-1">
					<ul>
						<c:choose>
							<c:when test="${businesstype eq '73'}"><li>金库</li></c:when>
							<c:when test="${businesstype eq '74'}"><li>门店</li></c:when>
						</c:choose>
						<c:if test="${!(Guest.grantstatus eq '1')}">
							<li> 
						 		<select id="gofficeId" name="gofficeId"  data-role="none" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;border:solid #ccc 1px">
    						 		<c:if test="${not empty Guest.gofficeId}">
    						 			<option value="${Guest.gofficeId}" selected="selected">${Guest.gofficeName}</option>
    						 		</c:if>
    						 		<c:if test="${empty Guest.gofficeId}">
    						 			<option value="" selected="selected">请选择</option>
    						 		</c:if>
    						 		<c:if test="${businesstype eq '73'}">
    						 			<c:set value="${sto:getStoCustList('3',true)}" var="storelist"></c:set>
    						 		</c:if>
    						 		<c:forEach var="storelist" items="${storelist}"> 
    						 			<c:if test="${!(Guest.gofficeId eq storelist.id)}">
    						 				<option value="${storelist.id}">${storelist.name}</option>
    						 			</c:if>	
    						 		</c:forEach>
          						</select> <font color="red">&nbsp;*</font> 
							</li>
						</c:if>
						<c:if test="${Guest.grantstatus eq '1'}">
			      			<li>
			      	 			<input type="text"  data-role="none" name="gofficeName" id="gofficeName" value="${Guest.gofficeName}" readonly="readonly" 
			      	 			style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;background-color:#E6E6E6" /><font color="red">&nbsp;&nbsp;*</font>
								<input type="hidden" data-role="none" name="gofficeId" id="gofficeId" value="${Guest.gofficeId}" readonly="readonly">
							</li>
						</c:if>
					</ul>
					<ul>
				       <li>用户名	</li>
				       <li>
				         <input type="text"  data-role="none" placeholder="用户名" id="gname" name="gname" value="${Guest.gname}" maxlength="10" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;" /><font color="red">&nbsp;&nbsp;*</font>
				       </li>
				    </ul>     
				     <ul>
				       <li>身份证号</li>
				       <li>
				         <input type="text" value="${Guest.gidcardNo}" data-role="none" id="gidcardNo" name="gidcardNo" placeholder="身份证号" maxlength="18"  style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;border:solid #ccc 1px;text-indent: 6px"" ><font color="red">&nbsp;&nbsp;*</font>
				       </li>
				     </ul>
				     <ul>
				       <li>手机号码</li>
				       <li >
				         <input type="tel" value="${Guest.gphone}" data-role="none" id="gphone" name="gphone" placeholder="手机号码"  size="30px" maxlength="11"  style="height:35px; width: 180px;font-size: 16px;text-indent: 6px;border:solid #ccc 1px" ><font color="red">&nbsp;&nbsp;*</font>
				       </li>
				     </ul>
				     <ul>
				     	<li>
				     	<input type="hidden" readonly="readonly"  data-role="none" placeholder="申请日期" id="loginDate" name="loginDate"
					           value="${sysDate}" readonly="readonly"  />
					     </li>
					 </ul>
				     <ul>
				     	<li>
							<input type="hidden" data-role="none" id="openId" name=openId placeholder="微信号" value="${openId}"  >
							<input type="hidden" data-role="none" id="busType" name=busType placeholder="业务类型" value="${businesstype}"  >
						</li>
					 </ul>
					</div>
				<div >
				 <%--   <table border=0 style="width:100%;height:100%">
				     <tr>
				       <td style="width:20px">
				         <label>门店</label>
				       </td>
				        <c:if test="${!(Guest.grantstatus eq '1')}">
				       <td colspan="2" style="width: 180px">	
						 <select id="gofficeId" name="gofficeId"  data-role="none" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;border:solid #ccc 1px">
     						 			
     						 			<c:if test="${not empty Guest.gofficeId}">
     						 			<option value="${Guest.gofficeId}" selected="selected">${Guest.gofficeName}</option>
     						 			</c:if>
     						 			<c:if test="${empty Guest.gofficeId}">
     						 			<option value="" selected="selected">请选择</option>
     						 			</c:if>
     						 		<c:forEach var="storelist" items="${storelist}"> 
     						 			
       						 			<c:if test="${!(Guest.gofficeId eq storelist.id)}">
     						 			<option value="${storelist.id}">${storelist.name}</option>
     						 			</c:if>	
     						 		</c:forEach>
           						 </select> <font color="red">&nbsp;*</font> 
   

				       </td>
				       </c:if>
				       <c:if test="${Guest.grantstatus eq '1'}">
				      	 <td colspan="2">
				      	 <input type="text"  data-role="none" name="gofficeName" id="gofficeName"
								value="${Guest.gofficeName}" readonly="readonly" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px;background-color:#E6E6E6" /><font color="red">&nbsp;&nbsp;*</font>
						<input type="hidden" data-role="none" name="gofficeId" id="gofficeId"
								value="${Guest.gofficeId}" readonly="readonly">
						</td>
						</c:if>
				     </tr>
				     
				      <tr>
				       <td>
				         <label>用户名</label>
				       </td>
				       <td colspan="2">
				         <input type="text"  data-role="none" placeholder="用户名" id="gname" name="gname" value="${Guest.gname}" maxlength="10" style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;text-indent: 6px" /><font color="red">&nbsp;&nbsp;*</font>
				       </td>
				     </tr>
				     
				
				     
				     <tr>
				       <td>
				         <label>身份证号</label>
				       </td>
				       <td colspan="2">
				         <input type="text" value="${Guest.gidcardNo}" data-role="none" id="gidcardNo" name="gidcardNo" placeholder="身份证号" maxlength="18"  style="ime-mode:disabled;height: 35px;width: 180px;font-size: 16px;border:solid #ccc 1px;text-indent: 6px"" ><font color="red">&nbsp;&nbsp;*</font>
				       </td>
				     </tr>
				     
				     
				     
				          <tr>
				       <td>
				         <label>手机号码</label>
				       </td>
				       <td colspan="2">
				         <input type="tel" value="${Guest.gphone}" data-role="none" id="gphone" name="gphone" placeholder="手机号码"  size="30px" maxlength="11"  style="height:35px; width: 180px;font-size: 16px;border:solid #ccc 1px" ><font color="red">&nbsp;&nbsp;*</font>
				       </td>
				     </tr>
				     <tr>
				     	<td>
				     	<input type="hidden" readonly="readonly"  data-role="none" placeholder="申请日期" id="loginDate" name="loginDate"
					           value="${sysDate}" readonly="readonly"  />
					     </td>
					 </tr>
				     <tr>
				     	<td>
							<input type="hidden" data-role="none" id="openId" name=openId placeholder="微信号" value="${openId}"  >
						</td>
					</tr>
				   </table> --%>
				</div>
				
				</form>
		</div>
			
		<div data-role="footer" data-position="fixed" data-id="footernav" data-position="fixed" data-tap-toggle="false">  
		        <div data-role="navbar">  
		            <ul>  
		                <li><a href="javascript:void(0);" onclick="formClear()" >清空</a></li>  
		            
		                <li><a id="btnSave" href="javascript:void(0);" onclick="formSubmit()" >保存</a></li>  
		            </ul>  
		        </div>  
		</div>  
	</div>


	<!--- app class="ui-btn-active"--->
	<script type="text/javascript">var ctx = '${ctx}';</script>

      
				       
     



</body>
</html>