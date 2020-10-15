//共通js
/*
 * 分页查询共通
 * Clark
 * 2014-11-3
 */
function page(n,s){
	if(n) $("#pageNo").val(n);
	if(s) $("#pageSize").val(s);
	$("#searchForm").submit();
	return false;
}

/*
 * 日期控件设定默认值为当前日期
 * Clark
 * 2014-11-3
 */
function setToday(nameList,isSearch){
	var isSet = true;
	//判断日期控件全部为空
	$(nameList).each(function() {
		if ($(this).val() != null && $(this).val() != ''){
			isSet = false;
		}
	});
	// 点击查询时间控件不默认值
	if(isSearch === 'true' ) {
		isSet = false;
	}
	if (isSet) {
		$(nameList).each(function() {
			var today = new Date();
			var y = today.getFullYear();
			var m = today.getMonth() + 1;
			var d = today.getDate();
			
			m = m < 10 ? '0'+m : m;
			d = d < 10 ? '0'+d : d;
		
			var now = y+'-'+m+'-'+d;
			$(this).val(now);
		});
	}
}

//Ajax 文件下载
jQuery.download = function(url, data, method){
    // 获得url和data
    if( url && data ){ 
        // data 是 string 或者 array/object
        data = typeof data == 'string' ? data : jQuery.param(data);
        // 把参数组装成 form的  input
        var inputs = '';
        jQuery.each(data.split('&'), function(){ 
            var pair = this.split('=');
            
            inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />'; 
        });
        // request发送请求
        jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>')
        .appendTo('body').submit().remove();
    };
};

/*
 * 清分业务冲正授权
 * qipeihong
 * 2017-9-21
 * param inNo 业务流水编号
 * param url  授权路径
 * param refreshUrl 页面返回路径
 */
/*function reverseAuthorize(inNo,url,refreshUrl,ctx){
	var content = "iframe:"+ctx+"/Common/showClearAuthorize?clearId="+inNo;*/
function reverseAuthorize(inNo,url,refreshUrl,ctx){
	var content = "iframe:"+ctx+"/Common/showClearAuthorize?clearId="+inNo;
	top.$.jBox.open(
			content,
			"冲正授权", 480, 420, {
				buttons : {
					//确认
					"确认" : "ok",
					//关闭
					"关闭" : true 
				},
				submit : function(v, h, f) {
					if (v == "ok") {
						var closeFlag;
						var contentWindow = h.find("iframe")[0].contentWindow;
						// 业务单号
						var clearId = contentWindow.document.getElementById("clearId").value;
						// 用户名
						var authorizeLogin = contentWindow.document.getElementById("authorizeLogin").value;
						if(authorizeLogin == null || authorizeLogin == ""){
							alertx('用户名不能为空');
							return false;
						}
						//密码
						var authorizePass = contentWindow.document.getElementById("authorizePass").value;
						if(authorizePass == null || authorizePass == ""){
							alertx('密码不能为空');
							return false;
						}
						var authorizeReason = contentWindow.document.getElementById("authorizeReason").value;
						// 错误信息
						var errorMessage = contentWindow.document.getElementById("errorMessage");
							$.ajax({
								type : "POST",
								dataType : "text",
								async: false,
								url : url,
								data : {
									clearId : clearId,
									authorizeLogin : authorizeLogin,
									authorizePass  : authorizePass,
									authorizeReason : authorizeReason
								},
								success : function(serverResult, textStatus) {
									if(serverResult == 'success'){
										closeFlag = true;
										var reUrl = refreshUrl+"?clearId="+clearId;
										$("#updateForm").attr("action", reUrl);
										$("#updateForm").submit();
									}else{
										closeFlag = false;
										errorMessage.innerHTML = serverResult;
									}
								},
								error : function(XmlHttpRequest, textStatus, errorThrown) {
									closeFlag = false;
									errorMessage.innerHTML = "异常:["+errorThrown+"]，请联系管理员！";
								}
							});
							return closeFlag;
					}
				},
				loaded : function(h) {
					$(".jbox-content", top.document).css(
							"overflow-y", "hidden");
				}
	});
	
}

//判断session是否存在
function checkSession(){
	$.ajax({
		url : ctx + '/Common/checkSession',
		type : 'post',
		dataType : 'json',
		data : {
			
		},
		success : function(serverResult, textStatus) {
		},
		error : function(XmlHttpRequest, textStatus, errorThrown) {
			if("error"!=textStatus){
				alert('您已登录超时或在其他地点登录，请重新登录！');
				top.location = "${ctx}";
				}
			return false;
		}
	});
}
