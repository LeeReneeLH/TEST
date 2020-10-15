<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 存款异常信息管理 -->
	<title><spring:message code="door.doorOrderException.title" /></title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
    	.fixedThead{
        	display: block;
            width: 100%;
        }
        .scrollTbody{
            display: block;
            height: 262px;
            overflow: auto;
            width: 100%;
        }
        thead.fixedThead tr th:last-child {
            color:#FF0000;
            width: 130px;
        }
        .rowSel {
		  background-color: #5599FF;
		}
    </style>
	<script type="text/javascript">
		var selectTickertape='';	//选中凭条号
		var validateTotalAmount;    //明细总金额（用于验证）
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					validateTotalAmount=sumAmount111();
					// if($("#totalAmount").val()!=validateTotalAmount){
					// 	alertx("总金额与明细不符，请校对！");
					// 	return;
					// }
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
					loading('正在提交，请稍等...');
					$(".doorIdHidden").remove();
					//$("#doorId").prop("name","doorIdHidden");
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		
		$(document).ready(function(){
			var detailList ="";
			//保存
			$("#btnSubmit").click(function(){
				detailList ="";
				$('#detailList').val("");
				$("#tblTikertape").find("tr").each(function () {
					//存款类型
					var saveMethod = $.trim($(this).children('td:eq(2)').text());
					//面值
					var parValue = $.trim($(this).children('td:eq(3)').text());
		        	//张数
					var count = $.trim($(this).children('td:eq(5)').text());
		        	//速存张数为空提示
		        	if(saveMethod == "速存存款" && (count == ""||count == "0")){
		        		detailList ="";
		        		alertx("明细内容有误，请核对！");
		        		return false;
		        	}
					//金额
					var detailAmount = changeAmountToNum($.trim($(this).children('td:eq(6)').text()));
		        	detailList=detailList+","+saveMethod+"_"+parValue+"_"+count+"_"+detailAmount;
				});
				//速存张数为空提示后不提交
				if(detailList == ""){
					return false;
				}
				$('#detailList').val(detailList.substr(1));
				$("#inputForm").submit();
			});
		});
		
		//显示店员
		/* $(document).ready(function(){
			if($("#doorId").val()!=""&&$("#doorId").val()!=null){
				doorUser();
			}
		}); */
		
		//店员
		function doorUser(){
			//$("#userOption").html($("#userSelect1").val());
			$(".userOptions").remove();
			denominationName();
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				url : "${ctx}/doorOrder/v01/doorOrderException/getPerson",
				data : {
					officeId:$("#doorId").val()
				},
				success : function(data, status) {
					if(data.length>0){
						for(var i=0;i<data.length;i++){
							var newOption = $("#userOption").clone(true);
							newOption.attr("value",data[i].id);
							newOption.attr("class","userOptions");
							newOption.html(data[i].name);
							newOption.removeAttr("style");
							newOption.prop("selected","");
							// 添加一行
							newOption.appendTo("#userSelect");
						}
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alertx("系统异常，请联系管理员！");
				}
			});
		}
		
		//店员传值
		$(document).ready(function(){
			$("#userSelect").change(function(){
				$("#useridHidden").val($("#userSelect").val());
				$("#userNameHidden").val($(this).find("option:selected").text());
			});
		});
		
		//机构选择回调函数
		function doorNameTreeselectCallBack(v, h, f){
			if (v=="ok"){
				$("#eqpId").find("option[name='equipmentOption']").each(function () {
					$(this).remove();
				});
				$("#businessType").find("option[name='businessTypeOption']").each(function () {
					$(this).remove();
				});
				var tree = h.find("iframe")[0].contentWindow.tree;
				var ids = [], names = [], nodes = [];
				nodes = tree.getSelectedNodes();
				if(nodes.length>0){
					var doorId=nodes[0].id;
					$("#doorId").val(doorId);
					doorUser();
					remarks();
					 $.ajax({
						type : "POST",
						dataType : "json",
						async: false,
						url : "${ctx}/weChat/v03/doorOrderInfo/selectDoorOffice",
						data : {
							doorId:doorId
						},
						success : function(data, status) {
							if(data.result == 'OK'){
								if(data.equipmentList.length>0){
									for(var i=0;i<data.equipmentList.length;i++){
										var newOption = $("#equipmentOptionTemplate0").clone(true);
										newOption.attr("value",data.equipmentList[i].id);
										newOption.html(data.equipmentList[i].seriesNumber);
										newOption.removeAttr("style");
										// 添加一行
										newOption.appendTo("#eqpId");
									}
								}
								if(data.savetTypeList.length>0){
									for(var i=0;i<data.savetTypeList.length;i++){
										var newOption = $("#businessTypeOptionTemplate0").clone(true);
										newOption.attr("value",data.savetTypeList[i].typeCode);
										newOption.html(data.savetTypeList[i].name);
										newOption.removeAttr("style");
										// 添加一行
										newOption.appendTo("#businessType");
									}
								}
							}else{
								alertx("系统异常，请联系管理员！");
							}
						},
						error : function(XmlHttpRequest, textStatus, errorThrown) {
							alertx("系统异常，请联系管理员！");
						}
					}); 
				}
			}
			$("#eqpId option:first").prop("selected","selected");
			$('#eqpId').trigger("change");
			$("#businessType option:first").prop("selected","selected");
			$('#businessType').trigger("change");
		}
		 
		// 整数千分位overflow-y:scroll;
		function toThousands(num) {
			var newStr = "";
			var count = 0;
		    var num = (num || 0).toString(), result = '';
		    if(num.indexOf(".")==-1){
			    while (num.length > 3) {
			    	result = ',' + num.slice(-3) + result;
			        num = num.slice(0, num.length - 3);
			    }
		    }else {
		    	for(var i = num.indexOf(".")-1;i>=0;i--){
				if(count % 3 == 0 && count != 0){
					newStr = num.charAt(i) + "," + newStr;
					}else{
					  newStr = num.charAt(i) + newStr; //逐个字符相接起来
					}
					count++;
				}
		    	num = newStr + (num + "00").substr((num + "00").indexOf("."),3);
		     }
		     if (num) { result = num + result; }
		     if (num.indexOf(".") == 0){
		    	 result = "0" + result;
		     }
		     return result;
		 }
		
		/* 金额验证修改 gzd 2020-05-21 start */
		$(document).ready(function(){
			detailAmountCheck($("#detailAmount"));
		});
		//明细金额验证
		function detailAmountCheck(obj){			
			obj.keyup(function () {
				var value = $(this).val().replace(/,/g, "")
				if(value.split(".")[0].length <= 9) {
					var reg = value.match(/\d+\.?\d{0,2}/);
			        var txt = '';
			        if (reg != null) {
			            txt = reg[0];
			        }
			        $(this).val(txt);
				} else {
					var value2 = value.slice(0,9)
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
		/* 金额验证修改 gzd 2020-05-21 end */
		
		/* //明细金额验证
		function detailAmountCheck(){
			if(isNaN($("#detailAmount").val())){
				$("#detailAmount").val(0);
			}
		} */
		
		/**
		 * 将数值四舍五入(保留2位小数)后格式化成金额形式
		 *
		 * @param num 数值(Number或者String)
		 * @return 金额格式的字符串,如'1,234,567.45'
		 * @type String
		 */
		function formatCurrencyFloat(num) {
		    num = num.toString().replace(/\$|\,/g,'');
		    if(isNaN(num))
		    num = "0";
		    sign = (num == (num = Math.abs(num)));
		    num = Math.floor(num*100+0.50000000001);
		    cents = num%100;
		    num = Math.floor(num/100).toString();
		    if(cents<10)
		    cents = "0" + cents;
		    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
		    num = num.substring(0,num.length-(4*i+3))+','+
		    num.substring(num.length-(4*i+3));
		    return (((sign)?'':'-') + num + '.' + cents);
		}
	
		function formatCurrencyNum(num) {
		    var result = '', counter = 0;
		    num = (num || 0).toString();
		    for (var i = num.length - 1; i >= 0; i--) {
		        counter++;
		        result = num.charAt(i) + result;
		        if (!(counter % 3) && i != 0) { result = ',' + result; }
		    }
		    return result;
		}
		
		/**
		 * 将金额转换成数字
		 * @param amount
		 * @returns
		 */
		function changeAmountToNum(amount) {
			if (typeof(amount) != "undefined") {
				amount = amount.toString().replace(/\$|\,/g,'');
			}
			return amount;
		}
		
		//存款方式
		function saveMethodChange(){
			var saveMethod=$("#saveMethod").val();
			// 01 速存、 02 强存、 03 其他、 else 请选择
			if(saveMethod == "01"){
				$("#amountType").attr("readOnly","");
				$("#amountType").find("option[value=0]").attr("selected","selected");
				$("#amountType").trigger("change");
				$("#amountType").attr("readOnly","readOnly");
			}else if(saveMethod == "02"||saveMethod == "03"){
				$("#amountType").attr("readOnly","");
				$("#amountType").find("option[value=1]").attr("selected","selected");
				$("#amountType").trigger("change");
				$("#amountType").attr("readOnly","readOnly");
			}else{
				$("#amountType").removeAttr("readOnly");
				$("#amountType").trigger("change");
			}
		}
		
		//金额类型
		$(document).ready(function(){
			$("#detailAmountDiv").attr("style","display:none");
			$("#detailAmount").val("");
		});
		function amountTypeChange(){
			var amountType=$("#amountType").val();
			if(amountType==0){
				$("#parValueDiv").attr("style","visibility:''");
				$("#countDiv").attr("style","display:''");
				$("#detailAmountDiv").attr("style","display:none");
				$("#detailAmount").val("");
			}else{
				$("#parValue option:first").prop("selected","selected");
				$('#parValue').trigger("change");
				$("#parValueDiv").attr("style","visibility:hidden");
				$("#countDiv").attr("style","display:none");
				$("#count").val("");
				$("#detailAmountDiv").attr("style","display:''");
			}
		}
		
		//金额求和
		function sumAmount111(){
			var tbObj = document.getElementById("tblTikertape"), sumValue = 0, j;
			  // 从第二行开始循环，认为第一行为标题行
			for (j = 1; j < tbObj.rows.length; j++) {
				// 速存有面值张数、强存 统计总金额
				if($.trim(tbObj.rows[j].cells[2].childNodes[0].nodeValue)!="其他存款"){
					if($.trim(tbObj.rows[j].cells[2].childNodes[0].nodeValue)=="强制存款"||($.trim(tbObj.rows[j].cells[2].childNodes[0].nodeValue)=="速存存款"&&$.trim(tbObj.rows[j].cells[3].childNodes[0].nodeValue)!=""&&$.trim(tbObj.rows[j].cells[5].childNodes[0].nodeValue)!="")){
						// 这里认为单元格内的第一个元素就是数值文本，未作有效性检查 
						sumValue += eval(tbObj.rows[j].cells[6].childNodes[0].nodeValue); 
					}
				}
			}
			sumValue=formatCurrencyFloat(sumValue);
			sumValue=changeAmountToNum(sumValue);
			//明细金额合计赋值
			$("#detailAmountSpan").text(sumValue);
			//$("#totalAmount").val(sumValue);
			return sumValue;
		}
		
		//金额 张数input 回车事件
		function inputChoose(){
			if($("input[name=choose]:checked").val() == "01"){
				addClick();
			}
			if($("input[name=choose]:checked").val() == "02"){
				updateClick();
			}
		}
		
		//添加 点击
		function addClick(){
			var amount ;
			var denomination="" ;
			if($("#saveMethod").val()==''){
				alertx("请选择存款方式！");
				return false;
			}
			//金额类型
			var amountType=$("#amountType").val();
			if(amountType==0){
				//面值
				if($("#parValue").val()==''){
					alertx("请选择面值！");
					return false;
				}
				//张数
				if($("#count").val()==null ||$("#count").val()==''||$("#count").val()==0){
					alertx("请输入张数！");
					return false;
				}
				$.ajax({
					type : "POST",
					dataType : "json",
					async: false,
					url : "${ctx}/weChat/v03/doorOrderInfo/getAmount",
					data : {
						count:$("#count").val(),
						parId:$("#parValue  option:selected").val()
					},
					success : function(data, status) {
						if(data.result == 'OK'){
							denomination = $("#parValue").val();
							amount=data.amount;
						}else{
							alertx("获取金额失败！");
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						alertx("获取金额失败！");
					}
				});
			}else{
				//明细金额
				if($("#detailAmount").val()==null ||$("#detailAmount").val()==''||parseFloat($("#detailAmount").val())==0){
					alertx("请输入金额！");
					return false;
				}
				amount=$("#detailAmount").val();
			}
			var type = $("#saveMethod").val();
			//var denomination = $("#parValue").val();
			var count = $("#count").val();
			var typeName;
			if(type == '01'){
				typeName = '速存存款';
			}else if(type == '02'){
				typeName = '强制存款';
			}else if(type == '03'){
				typeName = '其他存款';
			}
			amount=formatCurrencyFloat(changeAmountToNum(amount));
			amount=changeAmountToNum(amount);
			var trAdd ="<tr class='tr' name='trDetail'><td style=' text-align: center;'>"+${status.index+1}+"</td><td style='display:none'></td><td style=' text-align: center;'>"+typeName+"</td><td style='display:none'>"+denomination+"</td><td style=' text-align: center;'></td><td style=' text-align: center;'>"+count+"</td><td style=' text-align: center;'>"+amount+"</td><td style=' text-align: center;'><a class='a1' href='#' title='<spring:message code='common.delete' />' style='margin-left:10px;'><i class='fa fa-trash-o text-red fa-lg'></i></a></td></tr>";
			//添加一行
			$("#tblTikertape").append(trAdd);
			//金额求和
			sumAmount111();
			//序号重新赋值
			resetAmountNum();
		}
		
		//修改 点击
		function updateClick(){
			var amount ;
			if($("#saveMethod").val()==''){
				alertx("请选择存款方式！");
				return false;
			}
			//金额类型
			var amountType=$("#amountType").val();
			if(amountType==0){
				//面值
				if($("#parValue").val()==''){
					alertx("请选择面值！");
					return false;
				}
				//张数
				if($("#count").val()==null ||$("#count").val()==''||$("#count").val()==0){
					alertx("请输入张数！");
					return false;
				}
				$.ajax({
					type : "POST",
					dataType : "json",
					async: false,
					url : "${ctx}/weChat/v03/doorOrderInfo/getAmount",
					data : {
						count:$("#count").val(),
						parId:$("#parValue  option:selected").val()
					},
					success : function(data, status) {
						if(data.result == 'OK'){
							amount=data.amount;
						}else{
							alertx("获取金额失败！");
						}
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						alertx("获取金额失败！");
					}
				});
			}else{
				//明细金额
				if($("#detailAmount").val()==null ||$("#detailAmount").val()==''||parseFloat($("#detailAmount").val())==0){
					alertx("请输入金额！");
					return false;
				}
				amount=$("#detailAmount").val();
			}
			var type = $("#saveMethod").val();
			var denomination = $("#parValue").val();
			var count = $("#count").val();
			var typeName;
			if(type == '01'){
				typeName = '速存存款';
			}else if(type == '02'){
				typeName = '强制存款';
			}else if(type == '03'){
				typeName = '其他存款';
			}
			amount=formatCurrencyFloat(changeAmountToNum(amount));
			amount=changeAmountToNum(amount);
			//明细修改一行
			var amountType=$("#amountType").val();
			if(amountType==0){
				$(".tr[bgcolor=#5599FF]").find("td:eq(3)").text(denomination);
				$(".tr[bgcolor=#5599FF]").find("td:eq(5)").text(count);
			}else{
				$(".tr[bgcolor=#5599FF]").find("td:eq(3)").text("");
				$(".tr[bgcolor=#5599FF]").find("td:eq(5)").text("");
			}
			$(".tr[bgcolor=#5599FF]").find("td:eq(2)").text(typeName);
			$(".tr[bgcolor=#5599FF]").find("td:eq(6)").text(amount);
			//金额求和
			sumAmount111();
			//序号重新赋值
			denominationName();
		}
		
		//修改
		$(document).ready(function(){
			$("input[name=choose]").change(function(){
				//判断——01添加、02修改
				if(this.value == "01"){
					$(".tr").attr("bgcolor","#fff");
					$("#tblTikertape").off('click','tr');
					$(".update").prop("type","hidden");
					$(".add").prop("type","button");
					$(".update").off('click');
					$("#saveMethod option:first").prop("selected","selected");
					$('#saveMethod').trigger("change");
					$("#amountType option:first").prop("selected","selected");
					$('#amountType').trigger("change");
					$("#parValue option:first").prop("selected","selected");
					$('#parValue').trigger("change");
					$("#count").val("");
					$("#detailAmount").val("");
				}else if(this.value == "02"){
					$(".add").prop("type","hidden");
					$(".update").prop("type","button");
					$(".update").on('click',function(){
						updateClick();
					});
					//明细列表添加点击事件
					$("#tblTikertape").on('click','tr',function(){
						$(".tr").attr("bgcolor","#fff");
						$(this).attr("bgcolor","#5599FF");
						//存款方式返显
						var saveMethod =$.trim($(this).find("td:eq(2)").text());
						if(saveMethod=='速存存款'){
							$("#saveMethod").find("option[value=01]").attr("selected","selected");
							$("#saveMethod").change();
						}
						if(saveMethod=='强制存款'){
							$("#saveMethod").find("option[value=02]").attr("selected","selected");
							$("#saveMethod").change();
						}
						if(saveMethod=='其他存款'){
							$("#saveMethod").find("option[value=03]").attr("selected","selected");
							$("#saveMethod").change();
						}
						//张数、金额类型返显
						var count =$.trim($(this).find("td:eq(5)").text());
						/* if(count==null||count==""){
							$("#amountType").find("option[value=1]").attr("selected","selected");
							$("#amountType").change();
							$("#detailAmount").val($.trim($(this).find("td:eq(6)").text()));
						}else{
							$("#amountType").find("option[value=0]").attr("selected","selected");
							$("#amountType").change();
							$("#count").val(count);
						} */
						$("#detailAmount").val($.trim($(this).find("td:eq(6)").text()));
						$("#count").val(count);
						//面值返显
						var parValue =$.trim($(this).find("td:eq(3)").text());
						$("#parValue").find("option[value="+parValue+"]").attr("selected","selected");
						$("#parValue").change();
					});
					//自动点击明细第一行
					$("#tblTikertape").find(".tr:eq(0)").trigger("click");
				}
				
			});
		});
		
		
		/* //金额明细-删除行询问
		function delDetailConfirm(Row){
			delDetailObj = Row;
			var message = "<spring:message code='message.I7215'/>";
			confirmx(message, delDetail);
		}
		
		//金额明细-删除行 
		function delDetail(){
			var tr=delDetailObj.parentNode.parentNode;
			var tbody=tr.parentNode;
			// 删除行
			tbody.removeChild(tr);
			// 总金额重新赋值
			renewDetailAmount();
			// 凭条序号重新赋值
			resetAmountNum();
		} */
		
		// 删除运行 
		function delDetail(a){
			a.parent().parent().remove()
			resetAmountNum();
			sumAmount111();
		}
		
		// 重写 confirmx
		function confirmx(mess, href, closed){
		    console.log('href',href)
		    top.$.jBox.confirm(mess,'系统提示',function(v,h,f){
			    if(v=='ok'){
			    	delDetail(href);
			    }
		    },{buttonsFocus:1, closed:function(){
		    	if (typeof closed == 'function') {
		    		closed();
		    	}
		    }});
		    top.$('.jbox-body .jbox-icon').css('top','55px');
		    return false;
	    }
		
		//添加or删除
		$(document).ready(function(){
			/* //删除一条
			$("#tblTikertape").on('click','a',function(){
				var flag= confirm('确认要删除吗？');
				if(flag){
					// alert(1);
					$(this).parent().parent().remove();
					//金额求和
					//sumAmount111();
					//序号重新赋值
					resetAmountNum();
				}else{
					return;
				}		
			}); */
			   //删除一条
			$("#tblTikertape").on('click','a',function(){
				confirmx('确认要删除吗？',$(this));
			}); 
		
			//添加一条
			$(".add").click(function(){
				addClick();
			});
		});
		
		//明细序号重新赋值
		function resetAmountNum(){
			var k=1;
			$("#tblTikertape").find("tr[name=trDetail]").each(function(){
				$(this).children('td:eq(0)').text(k);
				k=k+1;
			});
			denominationName();
		}
		
		//面值显示
		function denominationName(){
			//var k=1;
			$("#tblTikertape").find("tr[name=trDetail]").each(function(){
				// 获取面值编号 （隐藏的）
				var denomination=$.trim($(this).children('td:eq(3)').text());
				if(denomination==""||denomination==null){
					$(this).children('td:eq(4)').text("");
				}else if(denomination=="15"){
					$(this).children('td:eq(4)').text("100元_纸币");
				}else if(denomination=="16"){
					$(this).children('td:eq(4)').text("50元_纸币");
				}else if(denomination=="17"){
					$(this).children('td:eq(4)').text("20元_纸币");
				}else if(denomination=="18"){
					$(this).children('td:eq(4)').text("10元_纸币");
				}else if(denomination=="19"){
					$(this).children('td:eq(4)').text("5元_纸币");
				}else if(denomination=="20"){
					$(this).children('td:eq(4)').text("2元_纸币");
				}else if(denomination=="21"){
					$(this).children('td:eq(4)').text("1元_纸币");
				}else if(denomination=="22"){
					$(this).children('td:eq(4)').text("5角_纸币");
				}else if(denomination=="23"){
					$(this).children('td:eq(4)').text("2角_纸币");
				}else if(denomination=="24"){
					$(this).children('td:eq(4)').text("1角_纸币");
				}
				/* hzy  添加分数     START*/
				else if(denomination=="25"){
					$(this).children('td:eq(4)').text("5分_纸币");
				}else if(denomination=="26"){
					$(this).children('td:eq(4)').text("2分_纸币");
				}else if(denomination=="27"){
					$(this).children('td:eq(4)').text("1分_纸币");
				}
				else if(denomination=="28"){
					$(this).children('td:eq(4)').text("1元_硬币");
				}else if(denomination=="29"){
					$(this).children('td:eq(4)').text("5角_硬币");
				}else if(denomination=="30"){
					$(this).children('td:eq(4)').text("1角_硬币");
				}
				else if(denomination=="31"){
					$(this).children('td:eq(4)').text("5分_硬币");
				}else if(denomination=="32"){
					$(this).children('td:eq(4)').text("2分_硬币");
				}else if(denomination=="33"){
					$(this).children('td:eq(4)').text("1分_硬币");
				}
				/* END */
			});
		}
		
		// 业务备注 文本框形式
		function inputRemarks(){
			$("#zjgRemarks").prop("style","display:none;");
			$("#otherRemarks").prop("style","");
			$("#remarks").prop("name","remarksOther");
			$("#remarks").prop("id","remarksOther");
			$("#remarksLast").prop("name","remarksLastOther");
			$("#remarksLast").prop("id","remarksLastOther");
			$("#remarkOther").prop("name","remarks");
			$("#remarkOther").prop("id","remarks");
			$("#remarks").val("");
			$("#valRemarks").val("2");
		}
		
		// 业务备注 前五位 下拉栏形式
		function selectRemarks(){
			$("#zjgRemarks").prop("style","");
			$("#otherRemarks").prop("style","display:none;");
			$("#remarks").prop("name","remarkOther");
			$("#remarks").prop("id","remarkOther");
			$("#remarksLastOther").prop("name","remarksLast");
			$("#remarksLastOther").prop("id","remarksLast");
			$("#remarksOther").prop("name","remarks");
			$("#remarksOther").prop("id","remarks");
			$("#valRemarks").val("1");
		}
		
		// 业务备注 后两位 下拉栏形式
		function selectRemarksLast(){
			$("#remarksLast").find("option[name='remarksLastOption']").each(function () {
				$(this).remove();
			});
			for(var i=0;i<=31;i++){
				var newOption = $("#remarksLastOptionTemplate0").clone(true);
				var j=i;
				if(i<10){
					j="0"+i;
				}
				newOption.attr("value",j);
				newOption.html(j);
				newOption.removeAttr("style");
				// 添加一行
				newOption.appendTo("#remarksLast");
			}
		}
		
		//存款备注（七位码）
		function remarks(){
			$("#remarks").find("option[name='remarksOption']").each(function () {
				$(this).remove();
			});
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				url : "${ctx}/doorOrder/v01/doorOrderException/getRemarks",
				data : {
					officeId:$("#doorId").val()
				},
				success : function(data, status) {
					if(!data){
						if($("#valRemarks").val()=="1"){
							inputRemarks();
						}
					}else{
						if($("#valRemarks").val()=="2"){
							selectRemarks();
							selectRemarksLast();
						}
						if(data.length>0){
							for(var i=0;i<data.length;i++){
								var newOption = $("#remarksOptionTemplate0").clone(true);
								newOption.attr("value",data[i].code);
								newOption.html(data[i].code);
								newOption.removeAttr("style");
								// 添加一行
								newOption.appendTo("#remarks");
							}
						}
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alerx("系统异常，请联系管理员！");
				}
			});
			$("#remarks option:first").prop("selected","selected");
			$('#remarks').trigger("change");
			$("#remarksLast option:first").prop("selected","selected");
			$('#remarksLast').trigger("change");
		}
		
		//机具 业务类型 返显
		$(document).ready(function(){
			inputRemarks();
			sumAmount111();
			denominationName();
			//alert($("#seriesNumber").val());
			var a1="<option name='equipmentOption' selected='selected' value="+$('#eqpIdHidden').val()+">"+$('#seriesNumber').val()+"</option>";
			$("#eqpId").append(a1);
			$("#eqpId").val($('#eqpIdHidden').val());
			$("#eqpIdOption").trigger("change");
			$('#eqpIdHidden').remove();
			var a2="<option name='businessTypeOption' selected='selected' value="+$('#businessTypeHidden').val()+">"+$('#businessTypeNameHidden').val()+"</option>";
			//value="+$('#businessTypeHidden').val()+"
			$("#businessType").append(a2);
			$("#businessType").val($('#businessTypeHidden').val());
			$("#businessType").trigger("change");
			//$('#businessTypeNameHidden').remove();
			//$('#businessTypeHidden').remove();
			//$('#businessTypeNameHidden').prop("name","bTNH");
			//$('#businessTypeNameHidden').prop("path","bTNH");
			$('#businessTypeHidden').prop("name","bTH");
			//$('#businessTypeHidden').prop("path","bTH");
			var a3="<option class='userOptions' selected='selected' value="+$('#useridHidden').val()+">"+$('#userNameHidden').val()+"</option>";
			if($('#useridHidden').val()!="" && $('#useridHidden').val()!=null){
				$("#userSelect").append(a3);
			}
			$("#userSelect").val($('#useridHidden').val());
			$("#userSelect").trigger("change");
			
			selectRemarksLast();
			/* if($('#remarksHidden').val()!="" && $('#remarksHidden').val()!=null){
				var rFirst = $('#remarksHidden').val().substr(0,5);
				var rSecond = $('#remarksHidden').val().substr(5,2);
				var a4="<option name='remarksOption' selected='selected' value="+rFirst+">"+rFirst+"</option>";
				$("#remarks").append(a4);
				$("#remarks").val(rFirst);
				$("#remarks").trigger("change");
				$("#remarksLast").find("option[value="+rSecond+"]").attr("selected","selected");
				$("#remarksLast").trigger("change");
			} */
			$("#remarks").val($('#remarksHidden').val());
			$("#remarksHidden").remove();
			//$('#eqpIdHidden').remove();
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 存款信息异常列表 -->
		<li><a href="${ctx}/doorOrder/v01/doorOrderException/"><spring:message code="door.doorOrderException.list" /></a></li>
		<!-- 存款异常信息修改 -->
		<li class="active">
			<%-- gzd 2020-04-14  修改链接  ${ctx}/doorOrder/v01/doorOrderException/detailUpdate?id=${doorOrderException.id} --%>
			<a><spring:message code="door.doorOrderException.update" /></a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="doorOrderException" action="${ctx}/doorOrder/v01/doorOrderException/update" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="user.id" id="useridHidden"/>
		<form:hidden path="remarks" id="remarksHidden"/>
		<form:hidden path="seriesNumber" id="seriesNumber"/>
		<form:hidden path="userName" id="userNameHidden"/>
		<form:hidden path="doorId" id="doorId" class="doorIdHidden"/>
		<form:hidden path="eqpId" id="eqpIdHidden"/>
		<form:hidden path="businessTypeName" id="businessTypeNameHidden"/>
		<form:hidden path="businessType" id="businessTypeHidden"/>
		<form:hidden path="amountList"/>
		<form:hidden path="detailList"/>
		<form:hidden path="tickertapeList"/>
		<form:hidden path="busTypeList"/>
		<!-- 异常原因 -->
		<form:hidden path="exceptionReason"/>		
		<input type="hidden" id="valRemarks" value="1"/>
		<%-- <form:hidden path="createByList"/>
		<form:hidden path="createDateList"/> --%>
		<sys:message content="${message}"/>	
		<div class="row">
			<%-- 页面左侧 --%>
			<div class="span5" style="width:45%">
				<div class="control-group">
					<!-- 开始时间 -->
					<label class="control-label"><spring:message code="door.doorOrderException.startTime" />：</label>
					<div class="controls">
						<input id="startTime" name="startTime" type="text" readonly="readonly" maxlength="20"
							   class="input-large Wdate createTime required" value="<fmt:formatDate value="${doorOrderException.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
							   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'endTime\')||\'%y-%M-%d\'}'});" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<!-- 结束时间 -->
					<label class="control-label"><spring:message code="door.doorOrderException.endTime" />：</label>
					<div class="controls">
						<input id="endTime" name="endTime" type="text" readonly="readonly" maxlength="20"
							   class="input-large Wdate createTime required" value="<fmt:formatDate value="${doorOrderException.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
							   onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startTime\')}',maxDate:'%y-%M-%d'});" />
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<!-- 门店 -->
					<label class="control-label"><spring:message code="door.doorOrderException.cust" />：</label>
					<div class="controls">
						<sys:treeselect id="doorName" name="doorId"
							value="${doorOrderException.doorId}" labelName="doorName"
							labelValue="${doorOrderException.doorName}" title="<spring:message code='door.doorOrderException.cust' />"
							url="/sys/office/treeData" cssClass="required input-medium"
							notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
							isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group" id="zjgRemarks">
					<!-- 存款备注（七位码） -->
					<label class="control-label"><spring:message code="door.historyChange.remarks" />：</label>
					<div class="controls">
						<form:select path="remarks" class="input-large required remarksOther" style="font-size:15px; width:115px;">
							<form:option value="" selected="selected"><spring:message code="common.select" /></form:option>
						</form:select>
						<form:select path="remarksLast" class="input-large required remarksOther" style="font-size:15px; width:90px;">
							<form:option value="" selected="selected"><spring:message code="common.select" /></form:option>
						</form:select>
						<span id="remarkSpan" class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group" id="otherRemarks" style="display:none;">
					<!-- 存款备注（七位码） -->
					<label class="control-label"><spring:message code="door.historyChange.remarks" />：</label>
					<div class="controls">
						<form:input path=""  id="remarkOther" value="" htmlEscape="false" class="noneRemarksDiv" style="text-align:right;" maxlength="32"/>
					</div>
				</div>
				<div class="control-group">
					<!-- 机具 -->
					<label class="control-label"><spring:message code="door.doorOrderException.equipment" />：</label>
					<div class="controls">
						<form:select id="eqpId" path="eqpId" class="input-large required" style="font-size:15px;">
							<form:option id="eqpIdOption" value="" selected="selected"><spring:message code="common.select" /></form:option>
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<!-- 业务类型 -->
					<label class="control-label"><spring:message code="door.doorOrderException.businessType" />：</label>
					<div class="controls">
					<form:select path="businessType" id="businessType" class="input-large required" style="font-size:15px;color:#000000">
						<form:option id="businessTypeNameOption" value=""><spring:message code="common.select" /></form:option>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<%-- 店员 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.user" />：</label>
					<div class="controls">
						<select id="userSelect" path="userName" class="input-large required" style="text-align:left;">
							<option id="userOption" selected="selected" ><spring:message code="common.select" /></option>
						</select>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				<div class="control-group">
					<%-- 款袋编号 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.packNum" />：</label>
					<div class="controls">
						<form:input path="bagNo" class="input-large required" style="text-align:right;" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" maxlength="32"/>
					</div>
				</div>
				<div class="control-group">
					<%-- 凭条号 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.tickerTape" />：</label>
					<div class="controls">
						<form:input path="tickerTape" class="input-large required" style="text-align:right;" onkeyup="value=value.replace(/\s/ig,'')" maxlength="40"/>
					</div>
				</div>
				<div class="control-group">
					<%-- 币种 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.currency" />：</label>
					<div class="controls">
						<form:input path="" value="人民币" class="input-large required" style="text-align:right;" readOnly="readOnly"/>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span>明细金额合计：</span><span id="detailAmountSpan"></span>
					</div>
				</div>
				<div class="control-group" style="display: none;">
					<%-- 总金额 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.totalAmount" />：</label>
					<div class="controls">
						<form:input id="totalAmount" path="totalAmount" class="input-large required" pattern="#,##0.00" style="text-align:right;" readOnly="readOnly"/>
					</div>
				</div>
				<div class="control-group" style="height: 400px; width: 100%;">
					<%-- 金额明细 --%>
					<label class="control-label"><spring:message code="door.doorOrderException.parValueDetail" />：</label>
					<div class="controls" style="height: 400px;overflow: auto">
						<table id="tblTikertape" class="table">
							<!-- <thead> -->
								<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
									<!-- 序号 -->
									<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
									<!-- 机具 -->
									<th style="text-align: center ; display: none" width="10%"><spring:message code="door.doorOrderException.equipment" /></th>
									<!-- 存款方式 -->
									<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.saveMethod" /></th>
									<!-- 面值 -->
									<th style="text-align: center ; display: none" width="20%"><spring:message code="door.doorOrderException.parValue" /></th>
									<!-- 面值 -->
									<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.parValue" /></th>
									<!-- 张数 -->
									<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.count" /></th>
									<!-- 金额 -->
									<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.amount" /></th>
									<th style="text-align: center" width="20%"><spring:message code="door.doorOrderException.operate" /></th>
								</tr>
								<c:forEach items="${doorOrderException.exceptionDetailList}" var="DoorOrderException" varStatus="status">
									<tr class="tr" name="trDetail">
										<td style=" text-align: center;">${status.index+1}</td>
										<td style="display:none">
											${doorOrderException.eqpId}
										</td>
										<td style=" text-align: center;">
											${fns:getDictLabel(DoorOrderException.type, 'save_method', '')}
										</td>
										<td style="display:none">
											${DoorOrderException.denomination}
										</td>
										<td style=" text-align: center;"></td>
										<td style=" text-align: center;">
											${DoorOrderException.count}
										</td>
										<td style=" text-align: center;">
											${DoorOrderException.amount}
										</td>
										<td style=" text-align: center;">
											<a class="a1" href="#" title="<spring:message code='common.delete' />" style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
										</td>
									</tr>
								</c:forEach>
							<!-- </thead> -->
							<tbody id="tbdTikertape"></tbody>
						</table> 
					</div>
				</div>
			</div>
			<%-- 页面右侧 --%>
				<div class="span7" style="width:50%;margin-left:0px;margin-top:145px">
					<!-- 添加、修改 单选 -->
					<div class="control-group" style="margin-top:60px;">
						&nbsp;&nbsp;&nbsp;&nbsp;<label for="radioAdd"><spring:message code="common.add" />：</label><input id="radioAdd" name="choose" type="radio" value="01" checked="checked"/>
						<label for="radioUpdate"><spring:message code="common.modify" />：</label><input id="radioUpdate" name="choose" type="radio" value="02"/>
					</div>
					<div class="control-group">
						<%-- 存款方式  ${fns:getDictList('save_method')}已修改 --%>
						<label class="control-label"><spring:message code="door.doorOrderException.saveMethod" />：</label>
						<div class="controls">
						<form:select path="" id="saveMethod" class="input-large" style="font-size:15px;color:#000000" onchange="saveMethodChange()">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<form:options items="${fns:getFilterDictList('save_method',true,'01,02,03')}" itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="door.doorOrderException.amountType" />：</label>
						<div class="controls">
						<form:select path="" id="amountType" class="input-large" style="font-size:15px;color:#000000" onchange="amountTypeChange()">
							<form:option value="0"><spring:message code="door.doorOrderException.parValue" /></form:option>
							<form:option value="1"><spring:message code="door.doorOrderException.amount" /></form:option>
						</form:select>
						</div>
					</div>
					<div class="control-group" id="parValueDiv">
						<%-- 面值 --%>
						<label class="control-label"><spring:message code="door.doorOrderException.parValue" />：</label>
						<div class="controls">
						<form:select path="" id="parValue" class="input-large" style="font-size:15px;color:#000000">
							<form:option value=""><spring:message code="common.select" /></form:option>
							<c:forEach items="${sto:getGoodDictList('cnypden')}" var="label">
				                <option value="${label.id}">${label.label}_纸币</option>
		        			</c:forEach>
							<c:forEach items="${sto:getGoodDictList('cnyhden')}" var="label">
				                <option value="${label.id}">${label.label}_硬币</option>
		        			</c:forEach>
							<%-- <form:options items="${sto:getGoodDictList('cnypden')}" itemLabel="label" itemValue="id" htmlEscape="false"/> --%>
						</form:select>
						</div>
					</div>
					<div class="control-group" id="countDiv">
						<%-- 张数 --%>
						<label class="control-label"><spring:message code="door.doorOrderException.count" />：</label>
						<div class="controls">
						<form:input path="" id="count" class="" style="text-align:right;"  maxlength="5" onkeydown="if(event.keyCode==13){inputChoose();return false;}" onkeyup="value=value.replace(/[^0-9]/g,'');"/>
						<input class="btn btn-primary add" type="button" value="<spring:message code='common.add'/>"/>
						<input class="btn btn-primary update" type="hidden" value="<spring:message code='common.modify' />"/>
						</div>
					</div>
					<div class="control-group" id="detailAmountDiv">
						<%-- 金额 --%>
						<label class="control-label"><spring:message code="door.doorOrderException.amount" />：</label>
						<div class="controls">
						<form:input path="" id="detailAmount" class="" style="text-align:right;"  maxlength="12" onkeydown="if(event.keyCode==13){inputChoose();return false;}" />
						<input class="btn btn-primary add" type="button" value="<spring:message code='common.add'/>"/>
						<input class="btn btn-primary update" type="hidden" value="<spring:message code='common.modify' />"/>
						</div>
					</div>
				</div>
			</div>
		
		
		<div class="form-actions" style="width:100%">
			<%-- 提交 --%>
			<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code='common.commit' />"/>
			&nbsp;
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)"/>
				<%-- onclick="window.location.href='${ctx}/doorOrder/v01/doorOrderException/'"/> --%>
		</div>
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="equipmentOption" id="equipmentOptionTemplate0"/>
		</form:select>
		</div>
		
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="businessTypeOption" id="businessTypeOptionTemplate0"/>
		</form:select>
		</div>
		
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="remarksOption" id="remarksOptionTemplate0"/>
		</form:select>
		</div>
		
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="remarksLastOption" id="remarksLastOptionTemplate0"/>
		</form:select>
		</div>
	</form:form>
</body>
</html>