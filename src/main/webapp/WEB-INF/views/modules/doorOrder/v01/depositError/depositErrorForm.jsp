<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code='clear.depositError.register' /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready( function() {
				/* $.validator.addMethod("idCardNoValidator", function ( value, element, param ) {
					var reg = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;
					return reg.test(value);
				  } , "输入的身份证号格式不正确!"
				); */
				var doorId = null;
				var orderId = null;
				var errorType = null;
				var amount = null;
				$.validator.addMethod("amountValidator", function(value,
						element, param) {
					var reg = /^[0-9]+(\.[0-9]{1,2})?$/;
					return reg.test(value);
				}, "输入的金额格式不正确!");

				$("#inputForm")
						.validate(
								{
									rules : {
										idCardNo : {
											idCardNoValidator : true
										},
										//idCardNo: {remote: {url:"${ctx}/doorOrder/v01/depositError/checkIdcardNo", cache:false}},
										doorId : {
											required : true
										},
										errorType : {
											required : true
										},
										amount : {
											amountValidator : true
										}
									},
									messages : {
										doorId : {
											required : "请选择门店"
										},
										errorType : {
											required : "请选择差错类型"
										}
									//idCardNo: {remote: "身份证号格式不正确,或身份信息不存在"}
									},

									submitHandler : function(form) {
										//提交按钮失效，防止重复提交
										$("#btnSubmit").attr("disabled", true);
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorPlacement : function(error, element) {
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
										} else {
											error.insertAfter(element);
										}
									}
								});
				
				$("#doorId").change(function() {
					console.log('局部',$("#doorId").val())
					doorId = $("#doorId").val()
					console.log('全局',doorId)
					if(orderId != null && orderId != '') {
						$.ajax({
				            //同步
				            async : false,
				            //请求方式
				            type : "POST",
				            //请求的媒体类型
				            dataType : 'json',
				            //请求地址
				            url : ctx +'/doorOrder/v01/depositError/getDoorIdByOrderId?orderId=' + orderId,
				          	//请求成功
				            success : function(result) {
				            	if(doorId == result) {
				            		$("#errorOrder").html("*");
				            	} else {
				            		$("#errorOrder").html("<b style='color:#F2540B'>当前门店下无此笔单号，请重试！ *</b>");
				            		flag2 = true
				            	}
				            }
						})
					}
				})
				
				$("#orderId").keyup(function(e) {
					console.log('e',e)
					console.log('orderId',$("#orderId").val())
					orderId = $("#orderId").val()
					if(orderId == '' || orderId == null) {
						$("#errorOrder").html("*");
					}
					console.log('全局',orderId)
					if(orderId != null && orderId != '') {
						$.ajax({
				            //同步
				            async : false,
				            //请求方式
				            type : "POST",
				            //请求的媒体类型
				            dataType : 'json',
				            //请求地址
				            url : ctx +'/doorOrder/v01/depositError/isOrderExists?orderId=' + orderId,
				          	//请求成功
				            success : function(result) {
								if(result == true) {
									$("#errorOrder").html("*");
									if(doorId != null) {
										$.ajax({
											async : false,
								            //请求方式
								            type : "POST",
								            //请求的媒体类型
								            dataType : 'json',
								            //请求地址
								            url : ctx +'/doorOrder/v01/depositError/getDoorIdByOrderId?orderId=' + orderId,
								            //请求成功
								            success : function(result) {
								            	console.log('doorId',result)
								            	if(doorId == result) {
								            		$("#errorOrder").html("*");
								            	} else {
								            		$("#errorOrder").html("<b style='color:#F2540B'>当前门店下无此笔单号，请重试！ *</b>");
								            		flag2 = true
								            	}
								            }
										})
									}
				            	} else {
				            		if($("#orderId").val() != null && $("#orderId").val() != '') {
			            				$("#errorOrder").html("<b style='color:#F2540B'>单号不存在，请重试！ *</b>");
										flag1 = true
				            		} 
				            	}
				            }
						})
					}
				});
				
				$("#orderId").mouseout(function(e) {
					console.log('e',e)
					console.log('orderId',$("#orderId").val())
					orderId = $("#orderId").val()
					if(orderId == '' || orderId == null) {
						$("#errorOrder").html("*");
					}
					console.log('全局',orderId)
					if(orderId != null && orderId != '') {
						$.ajax({
				            //同步
				            async : false,
				            //请求方式
				            type : "POST",
				            //请求的媒体类型
				            dataType : 'json',
				            //请求地址
				            url : ctx +'/doorOrder/v01/depositError/isOrderExists?orderId=' + orderId,
				          	//请求成功
				            success : function(result) {
								if(result == true) {
									$("#errorOrder").html("*");
									if(doorId != null) {
										$.ajax({
											async : false,
								            //请求方式
								            type : "POST",
								            //请求的媒体类型
								            dataType : 'json',
								            //请求地址
								            url : ctx +'/doorOrder/v01/depositError/getDoorIdByOrderId?orderId=' + orderId,
								            //请求成功
								            success : function(result) {
								            	console.log('doorId',result)
								            	if(doorId == result) {
								            		$("#errorOrder").html("*");
								            	} else {
								            		$("#errorOrder").html("<b style='color:#F2540B'>当前门店下无此笔单号，请重试！ *</b>");
								            		flag2 = true
								            	}
								            }
										})
									}
				            	} else {
				            		if($("#orderId").val() != null && $("#orderId").val() != '') {
			            				$("#errorOrder").html("<b style='color:#F2540B'>单号不存在，请重试！ *</b>");
										flag1 = true
				            		} 
				            	}
				            }
						})
					}
				});
				
				$("#errorType").change(function() {
					console.log($("#errorType").val())
					errorType = $("#errorType").val()
					if(errorType == 3) {
						if(errorType != null && errorType != '' && orderId != '' && orderId != null) {
							$.ajax({
					            //同步
					            async : false,
					            //请求方式
					            type : "POST",
					            //请求的媒体类型
					            dataType : 'json',
					            //请求地址
					            url : ctx +'/doorOrder/v01/depositError/isMoreThanSave?orderId=' + orderId,
					            //请求成功
					            success : function(result) {
					            	console.log(result)
					            	if(result < amount) {
					            		$("#errorAmount").html("<b style='color:#F2540B'>短款金额不能多于存款金额！ *</b>");
					            	} else {
					            		$("#errorAmount").html("*");
					            	}
					            }
							})
						}
					} else {
						$("#errorAmount").html("*")
					}
				})
				
				$("#amount").keyup(function(e) {
					amount = $("#amount").val()
					if(errorType == 3) {
						if(errorType != null && errorType != '' && orderId != '' && orderId != null) {
							$.ajax({
					            //同步
					            async : false,
					            //请求方式
					            type : "POST",
					            //请求的媒体类型
					            dataType : 'json',
					            //请求地址
					            url : ctx +'/doorOrder/v01/depositError/isMoreThanSave?orderId=' + orderId,
					            //请求成功
					            success : function(result) {
					            	console.log(result)
					            	if(result < amount) {
					            		$("#errorAmount").html("<b style='color:#F2540B'>短款金额不能多于存款金额！ *</b>");
					            	} else {
					            		$("#errorAmount").html("*");
					            	}
					            }
							})
						}
					} else {
						$("#errorAmount").html("*")
					}
				})
				
				$("#btnSubmit").click(function() {
					var flag1 = false;
					var flag2 = false;
					var flag3 = false;
					var flag4 = false;
					if(orderId != null && orderId != '' && doorId != null && doorId != '') {
						$.ajax({
				            //同步
				            async : false,
				            //请求方式
				            type : "POST",
				            //请求的媒体类型
				            dataType : 'json',
				            //请求地址
				            url : ctx +'/doorOrder/v01/depositError/isOrderExists?orderId=' + orderId,
				            //请求成功
				            success : function(result) {
								if(result == true) {
									$.ajax({
										async : false,
							            //请求方式
							            type : "POST",
							            //请求的媒体类型
							            dataType : 'json',
							            //请求地址
							            url : ctx +'/doorOrder/v01/depositError/getDoorIdByOrderId?orderId=' + orderId,
							            //请求成功
							            success : function(result) {
							            	console.log(result)
							            	if(doorId == result) {
							            		$.ajax({
													async : false,
										            //请求方式
										            type : "POST",
										            //请求的媒体类型
										            dataType : 'json',
										            //请求地址
										            url : ctx +'/doorOrder/v01/depositError/getLoginCount?orderId=' + orderId,
										            //请求成功
										            success : function(result) {
										            	console.log(result)
										            	if(result >= 1) {
										            		$("#errorOrder").html("<b style='color:#F2540B'>该单号已存在差错登记记录，请核对后重试！ *</b>");
										            		flag3 = true
										            	}
										            }
												})
							            	} else {
							            		$("#errorOrder").html("<b style='color:#F2540B'>当前门店下无此笔单号，请重试！ *</b>");
							            		flag2 = true
							            	}
							            }
									})
								} else {
									$("#errorOrder").html("<b style='color:#F2540B'>单号不存在，请重试！ *</b>");
									flag1 = true
								}
				            },
				            //请求失败，包含具体的错误信息
				            error : function(e){
				                console.log(e.status);
				            }
						});
						if(flag1) {
							return false;
						} else {
							if(flag2) {
								return false;
							} else {
								if(flag3) {
									return false;
								}
							}
						}
					}
					if(errorType != null && errorType != '' && orderId != '' && orderId != null) {
						if(errorType == 3) {
							$.ajax({
					            //同步
					            async : false,
					            //请求方式
					            type : "POST",
					            //请求的媒体类型
					            dataType : 'json',
					            //请求地址
					            url : ctx +'/doorOrder/v01/depositError/isMoreThanSave?orderId=' + orderId,
					            //请求成功
					            success : function(result) {
					            	console.log(result)
					            	if(result < amount) {
					            		$("#errorAmount").html("<b style='color:#F2540B'>短款金额不能多于存款金额！ *</b>");
					            		flag4 = true
					            	}
					            }
							});
							if(flag4) {
								return false;
							}	
						}
					}
					
				})
	});
	
	/* 金额验证修改 gzd 2020-05-21 start */
	$(document).ready(function(){
		detailAmountCheck($("#amount"));
	});
	//明细金额验证
	function detailAmountCheck(obj){			
		obj.keyup(function () {
			var value = $(this).val().replace(/,/g, "")
			if(value.split(".")[0].length <= 10) {
				var reg = value.match(/\d+\.?\d{0,2}/);
		        var txt = '';
		        if (reg != null) {
		            txt = reg[0];
		        }
		        $(this).val(txt);
			} else {
				var value2 = value.slice(0,10)
				var reg2 = value2.match(/\d+\.?\d{0,2}/);
				var txt2 = '';
		        if (reg2 != null) {
		            txt2 = reg2[0];
		        }
				$(this).val(txt2);
			}
	    }).change(function () {
	        $(this).keypress();
	        var v = $(this).val();
	        console.log('v',v)
	        var txt = '';
	        if (/\.$/.test(v))
	        {
	            $(this).val(v.substr(0, v.length - 1));
	        }
	    });
		
	}
	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 存款差错列表 -->
		<li><a href="${ctx}/doorOrder/v01/depositError/"><spring:message
					code='clear.depositError.list' /></a></li>
		<!-- 存款差错登记 -->
		<%-- gzd 2020-04-14 修改链接  ${ctx}/doorOrder/v01/depositError/form?id=${depositError.id} --%>
		<li class="active"><a><spring:message code='clear.depositError.register' /> </a></li>
	</ul>
	<br />

	<form:form id="inputForm" modelAttribute="depositError"
		action="${ctx}/doorOrder/v01/depositError/save" method="post"
		class="form-horizontal">
		<form:hidden path="id" />
		<sys:message content="${message}" />
		<!-- 门店 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code='clear.depositError.door' />：</label>
			<div class="controls">
				<form:select path="doorId" id="doorId" style="width:209px"
					class="required">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options items="${sto:getStoCustList('8',false)}"
						itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>


		<!-- 差错类型 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code='clear.depositError.errorType' />：</label>
			<div class="controls">
				<form:select path="errorType" style="width:209px" class="required" id="errorType">
					<form:option value="">
						<spring:message code="common.select" />
					</form:option>
					<form:options
						items="${fns:getFilterDictList('clear_error_type',true,'2,3')}"
						itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<!-- 存款单号 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code='door.checkCash.tickertape' />：</label>
			<div class="controls">
				<form:input path="orderId" htmlEscape="false" class="required" id="orderId"/>
				<span class="help-inline"><font color="red" id="errorOrder">*</font> </span>
			</div>
		</div>

		<!-- 差错金额 -->
		<div class="control-group">
			<label class="control-label"><spring:message
					code='clear.depositError.amount' />：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="required" id="amount"/>
				<span class="help-inline"><font color="red" id="errorAmount">*</font> </span>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label">登记人身份证号：</label>
			<div class="controls">
				<form:input path="idCardNo" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> --%>

		<div class="control-group">
			<label class="control-label"><spring:message
					code='clear.depositError.comments' />：</label>
			<div class="controls">
				<form:textarea path="comments" htmlEscape="false" rows="3"
					maxlength="80" class="input-xxlarge " style="width:209px" />
			</div>
		</div>
		<!-- 保存  返回 -->
		<div class="form-actions">
			<shiro:hasPermission name="doorOrder:v01:depositError:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit"
					value="<spring:message code='common.save'/>" />&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)" />
				<%-- onclick="window.location.href ='${ctx}/doorOrder/v01/depositError/back'" /> --%>
		</div>
	</form:form>
</body>
</html>