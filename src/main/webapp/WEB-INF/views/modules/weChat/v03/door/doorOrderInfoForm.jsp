<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 存款管理 -->
	<title><spring:message code="door.deposit.title" /></title>
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
		var delTikertapeObj;		//凭条-删除行 对象
		var delDetailObj;			//金额明细-删除行 对象
	
		$(document).ready(function() {
			$("#inputForm").validate({
				rules: {
					rfid: {remote: {url:"${ctx}/weChat/v03/doorOrderInfo/checkRfid",cache:false}}
				},
				messages: {
					rfid: {remote: "款袋编号已存在，请重新输入！"}
				},
				submitHandler: function(form){
					//提交按钮失效，防止重复提交
					$("#btnSubmit").attr("disabled",true);
					loading('正在提交，请稍等...');
					$("#doorId").prop("name","doorIdHidden");
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					
						error.appendTo(element.parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			//保存
			$("#btnSubmit").click(function(){
				if($('#amount').val()==0){
					//总金额不能为0
					alertx("<spring:message code='message.I7205' />");
					return;
				}
				var amountMax = 999999999999.99;
				if(changeAmountToNum($("#amount").val())>amountMax){
					alertx("总金额过大，请重新核对！");
					return;
				}
				//金额列表
				var amountList= '';
				//业务类型列表
				var busTypeList= '';
				//凭条列表
				var tickertapeList = '';
				//存款备注列表
				var remarksList = '';
				//金额明细列表
				var detailList = '';
				//凭条金额验证
				var tikertapeAmountFlag=false;
				$("#tbdTikertape").find("tr").each(function () {
					var amountTemp=$(this).children('td:eq(3)').text().replace(new RegExp(",","gm"),"");
					var busTypeTemp=$(this).children('td:eq(5)').text().replace(new RegExp(",","gm"),"");
					var tickertapeTemp=$(this).children('td:eq(1)').text().replace(new RegExp(",","gm"),"");
					var remarksTemp=$(this).children('td:eq(6)').text().replace(new RegExp(",","gm"),"");
					if(amountTemp == 0){
						tikertapeAmountFlag=true;
						return false;
					}
	            	amountList = amountList+"," +amountTemp;
	            	busTypeList = busTypeList+","+busTypeTemp;
	                tickertapeList=tickertapeList+"," +tickertapeTemp;
	                remarksList=remarksList+"," +remarksTemp;
				});
				if(tikertapeAmountFlag){
					//凭条金额不能为0
					alertx("凭条金额不能为0");
					return;
				}
				$("#tbdAmount").find("tr").each(function () {
					var tickertape= $(this).attr("name");
					//存款类型
					var saveMethod = $(this).children('td:eq(6)').text();
					//面值
					var parValue = $(this).children('td:eq(7)').text();
		        	//张数
					var count = $(this).children('td:eq(3)').text();
					//金额
					var detailAmount = changeAmountToNum($(this).children('td:eq(4)').text());
		        	detailList=detailList+","+tickertape+"_"+saveMethod+"_"+parValue+"_"+count+"_"+detailAmount;
				});
				$('#detailList').val(detailList.substr(1));
				$('#amountList').val(amountList);
				$('#busTypeList').val(busTypeList);
				$('#tickertapeList').val(tickertapeList);
				$('#remarksList').val(remarksList);
				$("#inputForm").submit();
			});
		});
		
		$(window).load(function() {
			amountTypeChange();
        });
		
		//机构选择回调函数
		function doorNameTreeselectCallBack(v, h, f){
			if (v=="ok"){
				$("#equipmentId").find("option[name='equipmentOption']").each(function () {
					$(this).remove();
				});
				$("#saveType").find("option[name='saveTypeOption']").each(function () {
					$(this).remove();
				});
				var tree = h.find("iframe")[0].contentWindow.tree;
				var ids = [], names = [], nodes = [];
				nodes = tree.getSelectedNodes();
				if(nodes.length>0){
					var doorId=nodes[0].id;
					$("#doorId").val(doorId);
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
										newOption.html(data.equipmentList[i].name);
										newOption.removeAttr("style");
										// 添加一行
										newOption.appendTo("#equipmentId");
									}
								}
								if(data.savetTypeList.length>0){
									for(var i=0;i<data.savetTypeList.length;i++){
										var newOption = $("#saveTypeOptionTemplate0").clone(true);
										newOption.attr("value",data.savetTypeList[i].id);
										newOption.html(data.savetTypeList[i].name);
										newOption.removeAttr("style");
										// 添加一行
										newOption.appendTo("#saveType");
									}
								}
							}else{
								alerx("系统异常，请联系管理员！");
							}
						},
						error : function(XmlHttpRequest, textStatus, errorThrown) {
							alerx("系统异常，请联系管理员！");
						}
					}); 
				}
			}
			$("#equipmentId option:first").prop("selected","selected");
			$('#equipmentId').trigger("change");
			$("#saveType option:first").prop("selected","selected");
			$('#saveType').trigger("change");
		}
		
		//凭条-添加行 
		function addTickertape(){
			if($("#valRemarks").val()=="1"){
				if(($("#remarks  option:selected").val()+$("#remarksLast  option:selected").val()).length!="7"){
					alertx("请先选择存款备注！");
					return;
				}
			}
			/* //业务类型
			var saveType = $("#saveType").val();
			if(saveType==null ||saveType==""){
				alertx("请选择业务类型！");
				return;
			}
			//凭条
			var tickertape = $("#tickertape").val();
			if(tickertape==null ||tickertape==""){
				alertx("请输入凭条！");
				return;
			}
			//验证凭条是否重复
			var k=0
			var tickertapeTemp = '';
			var blnFlag = false;
			$("#tblTikertape").find("tr").each(function () {
            	// 从第三行开始
               	if (k>0) {
               		tickertapeTemp = $(this).children('td:eq(1)').text().replace(new RegExp(",","gm"),"");
               	 	if (tickertape == tickertapeTemp){
        				blnFlag = true;
               	 	}
               	}
           		k++;
			});
			if(blnFlag){
				//凭条重复，请重新输入
				alertx("凭条重复，请重新输入！");
				$("#tickertape").focus();
				return;
			} */
			var tickertape;
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				url : "${ctx}/weChat/v03/doorOrderInfo/getTickertape",
				success : function(data, status) {
					tickertape=data;
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alerx("系统异常，请联系管理员！");
				}
			});
			var newRow = $("#trTikertapeTemplate0").clone(true).attr("id","tickertape_"+tickertape);
			newRow.removeAttr("style");
			// 添加一行
			newRow.appendTo("#tblTikertape");
			// 凭条
			newRow.children('td:eq(1)').text(tickertape);
			// 业务类型
			if($("#saveType  option:selected").text()=="请选择"){
				newRow.children('td:eq(2)').text();
			}else{
				newRow.children('td:eq(2)').text($("#saveType  option:selected").text());
			}
			newRow.children('td:eq(5)').text($("#saveType  option:selected").val());
			// 存款备注
			if($("#valRemarks").val()=="1"){
				newRow.children('td:eq(6)').text($("#remarks  option:selected").val()+$("#remarksLast  option:selected").val());
			}else{
				newRow.children('td:eq(6)').text($("#remarks").val());
			}
			// 金额
			newRow.children('td:eq(3)').text(formatCurrencyFloat(0));
			// 行追加后凭条清空
			$("#tickertape").val('');
			$("#tickertape").focus();
			// 凭条序号重新赋值
			resetTikertapeNum();
			// 当前选中凭条
			selectTickertape=tickertape;
			// 选中当前行
			selectRow(newRow);
			var tbody1 = document.getElementById('tbdTikertape');
			tbody1.scrollTop = tbody1.scrollHeight;
		 }
		
		//凭条-删除行询问
		function delTikertapeConfirm(Row){
			delTikertapeObj = Row;
			var message = "<spring:message code='message.I7215'/>";
			confirmx(message, delTickertape);
		}
		
		 //凭条-删除行 
		 function delTickertape(){
			var tr=delTikertapeObj.parentNode.parentNode;
			var tbody=tr.parentNode;
			// 下一行
			var vNextRow = tr.nextElementSibling;
			// 删除行凭条号
			var tickertape =tr.id.replace("tickertape_","");
			// 删除行
			tbody.removeChild(tr);
			// 删除金额明细
			$("#tbdAmount").find("tr[name='"+tickertape+"']").each(function () {
				$(this).remove();
			});
			// 是否存在下一行			
			if (vNextRow != null){
				// 下一行为选中行
				selectRow($(vNextRow));	
			}else{
				// 选中为空
				selectTickertape='';
			}
			//凭条序号重新赋值
			resetTikertapeNum();
			// 总金额重新赋值
			renewAmount();
		 }
		
		//凭条-选中行处理
		function selectRow(vRow){
			//当前选中凭条
			selectTickertape=$(vRow).attr('id').replace("tickertape_","");
		 	//选中样式
			$(vRow).addClass("rowSel").siblings().removeClass("rowSel");
		 	//金额明细切换
		 	$("#tbdAmount").find("tr").each(function () {
		 		if($(this).attr("name")==selectTickertape){
		 			$(this).show();
		 		}else{
		 			$(this).hide();
		 		}
			});
		}
		
		/* 金额验证修改 gzd 2020-05-21 start */
		$(document).ready(function(){
			detailAmountCheck($("#detailAmount"));
		});
		//明细金额验证
		function detailAmountCheck(obj){
			/* if(isNaN($("#detailAmount").val()) || $("#detailAmount").val() < 0){
				$("#detailAmount").val(0);
			} */
			
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
		
		//存款方式
		function saveMethodChange(){
			var saveMethod=$("#saveMethod").val();
			if(saveMethod == "01"){
				$("#amountType").attr("readOnly","");
				$("#amountType").find("option[value=0]").attr("selected","selected");
				$("#amountType").trigger("change");
				$("#amountType").attr("readOnly","readOnly");
			}else if(saveMethod == "02"){
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
		function amountTypeChange(){
			var amountType=$("#amountType").val();
			if(amountType==0){
				$("#parValueDiv").attr("style","visibility:''");
				$("#countDiv").attr("style","display:''");
				$("#detailAmountDiv").attr("style","display:none");
				$("#detailAmount").val("");
			}else{
				$("#parValueDiv").attr("style","visibility:hidden");
				$("#countDiv").attr("style","display:none");
				$("#count").val("");
				$("#detailAmountDiv").attr("style","display:''");
			}
		}
		
		//存款明细-添加行 
		function addAmount(){
			if(selectTickertape==''){
				alertx("请选择凭条！");
				return false;
			}
			/* if($("#saveMethod").val()==''){
				alertx("请选择存款方式！");
				return false;
			} */
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
							data['count'] = parseInt($("#count").val());
							//data['saveMethodText'] = $("#saveMethod  option:selected").text();
							data['saveMethodText'] = "速存存款";
							//data['saveMethod'] = $("#saveMethod  option:selected").val();
							data['saveMethod'] = "01";
							data['parValueText'] = $("#parValue  option:selected").text();
							data['parValue'] = $("#parValue  option:selected").val();
							addAmountRow(data);
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
				var data = new Object();
				data['count'] = "";
				//data['saveMethodText'] = $("#saveMethod  option:selected").text();
				data['saveMethodText'] = "强制存款";
				//data['saveMethod'] = $("#saveMethod  option:selected").val();
				data['saveMethod'] = "02";
				data['parValueText'] = "";
				data['parValue'] = "";
				data['amount'] = $("#detailAmount").val();
				addAmountRow(data);
			}
		 }
		
		//存款明细-添加行 
		function addAmountRow(data){
			var newRow = $("#trAmountTemplate0").clone(true).attr("name",selectTickertape);
			newRow.removeAttr("style");
			// 添加一行
			newRow.appendTo("#tblAmount");
			// 存款类型
			newRow.children('td:eq(1)').text(data.saveMethodText);
			// 面值
			newRow.children('td:eq(2)').text(data.parValueText);
			// 张数
			newRow.children('td:eq(3)').text(data.count);
			// 金额
			newRow.children('td:eq(4)').text(formatCurrencyFloat(data.amount));
			// 存款类型编号
			newRow.children('td:eq(6)').text(data.saveMethod);
			// 面值编号
			newRow.children('td:eq(7)').text(data.parValue);
			// 明细序号重新赋值
			resetAmountNum();
			// 总金额重新赋值
			renewDetailAmount();
			var tbody1 = document.getElementById('tbdAmount');
			tbody1.scrollTop = tbody1.scrollHeight;
		}
		
		//金额明细-删除行询问
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
		}
		
		// 总金额重新赋值
		function renewAmount(){ 
			var amount = 0;
			$("#tbdTikertape").find("tr").each(function () {
               amount = parseFloat(changeAmountToNum(amount)) + parseFloat(changeAmountToNum($(this).children('td:eq(3)').text()));
			});
			$('#amount').val(formatCurrencyFloat(amount));
		}
		
		// 明细金额重新赋值
		function renewDetailAmount(){ 
			 var amount = 0;
			 $("#tbdAmount").find("tr[name='"+selectTickertape+"']").each(function () {
              	amount = parseFloat(changeAmountToNum(amount)) + parseFloat(changeAmountToNum($(this).children('td:eq(4)').text()));
			});
			//$("#tickertape_"+selectTickertape).children('td:eq(3)').text(formatCurrencyFloat(amount));
			$("[id='tickertape_"+selectTickertape+"']").children('td:eq(3)').text(formatCurrencyFloat(amount));
			renewAmount();
		}
		
		//凭条序号重新赋值
		function resetTikertapeNum(){
			var i=1;
			$("#tbdTikertape").find("tr").each(function () {
				$(this).children('td:eq(0)').text(i++);
			});
		}
		
		//明细序号重新赋值
		function resetAmountNum(){
			var i=1;
			$("#tbdAmount").find("tr[name='"+selectTickertape+"']").each(function () {
				$(this).children('td:eq(0)').text(i++);
			});
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
		
		/**
		 * 有无机具（单选框）
		 * @auther gzd
		 * @time 2019-12-12
		 */
		$(document).ready(function(){
			$(".eqpDiv").remove();
			$("#remarksLast").find("option[name='remarksLastOption']").each(function () {
				$(this).remove();
			});
			inputRemarks();
			/* for(var i=1;i<=31;i++){
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
			} */
			/* $("input[name=choose]").change(function(){
				//判断——01有机具存款、02无机具存款
				if(this.value == "01"){
					$(".noneRemarksDiv").attr("style","display:''");
					$("#equipmentId").prop("name","equipmentId");
					$("#amount").prop("readOnly","readOnly");
					$("#amount").prop("value","0.00");
					$("#amount").prop("style","text-align:right;background-color:#E6E6E6");
				}else if(this.value == "02"){
					$(".noneRemarksDiv").attr("style","display:none");
					$("#equipmentId").prop("name","");
					$("#amount").prop("readOnly","");
					$("#amount").prop("value","");
					$("#amount").prop("style","text-align:right;");
				}
				
			}); */
		});
		
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
			$("#remarksLast").trigger("change");
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
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 存款列表 -->
		<li><a href="${ctx}/weChat/v03/doorOrderInfo/"><spring:message code="door.doorOrder.deposit" /><spring:message code="common.list" /></a></li>
		<!-- 存款登记(修改) -->
		<li class="active">
		<%-- gzd 2020-04-14	修改链接  ${ctx}/weChat/v03/doorOrderInfo/form?id=${doorOrderInfo.id} --%>
			<a>
				<shiro:hasPermission name="door:doorOrderInfo:edit">
					<c:choose>
						<c:when test="${not empty doorOrderInfo.id}">
							<spring:message code="door.doorOrder.deposit" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="door.doorOrder.deposit" /><spring:message code="common.register" /> 
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="doorOrderInfo" action="${ctx}/weChat/v03/doorOrderInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="doorId" id="doorId"/>
		<form:hidden path="amountList"/>
		<form:hidden path="detailList"/>
		<form:hidden path="tickertapeList"/>
		<form:hidden path="busTypeList"/>
		<form:hidden path="remarksList"/>
		<input type="hidden" id="valRemarks" value="1"/>
		<sys:message content="${message}"/>	
		<div class="row">
		<%-- 页面左侧 --%>
		<div class="span5" style="width:42%">
			<!-- <div class="control-group noneRemarksDiv eqpDiv">
				有无机具
				<div class="">
					&nbsp;&nbsp;&nbsp;&nbsp;有机具存款：<input name="choose" type="radio" value="01" checked="checked"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无机具存款：<input name="choose" type="radio" value="02"/>
				</div>
			</div> -->
			<div class="control-group">
				<%-- 门店 --%>
				<label class="control-label"><spring:message code="door.public.cust" />：</label>
				<div class="controls">
					<sys:treeselect id="doorName" name="doorId"
						value="${doorOrderInfo.doorId}" labelName="doorName"
						labelValue="${doorOrderInfo.doorName}" title="<spring:message code='door.public.cust' />"
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
			<%-- <div class="control-group eqpDiv">
				机具
				<label class="control-label"><spring:message code="door.equipment.equipment" />：</label>
				<div class="controls">
					<form:select path="equipmentId" class="input-large required" style="font-size:15px;">
						<form:option value="" selected="selected"><spring:message code="common.select" /></form:option>
					</form:select>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div> --%>
			<div class="control-group">
				<%-- 业务类型 --%>
				<label class="control-label"><spring:message code="door.doorOrder.businessType" />：</label>
				<div class="controls">
				<form:select path="" id="saveType" class="input-large" style="font-size:15px;color:#000000">
					<form:option value=""><spring:message code="common.select" /></form:option>
				</form:select>
				<span class="help-inline eqpDiv"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group">
				<%-- 总金额 --%>
				<label class="control-label"><spring:message code="common.totalMoney" />：</label>
				<div class="controls">
					<form:input path="amount" value="0.00" htmlEscape="false" class="required" readonly="true" style="text-align:right;background-color:#E6E6E6"   />
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group">
				<%-- 款袋编号 --%>
				<label class="control-label"><spring:message code="door.doorOrder.packNum" />：</label>
				<div class="controls">
					<form:input path="rfid" class="" style="text-align:right;"  maxlength="32" onkeydown="if(event.keyCode==13){addTickertape();return false;}" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')"/>
					<span class="help-inline noneRemarksDiv eqpDiv"><font color="red">*</font> </span>
					<input id="btnAddDetail" class="btn btn-primary" type="button" value="<spring:message code='common.add' />" onclick="addTickertape()" />
				</div>
			</div>
			<div class="control-group noneRemarksDiv eqpDiv">
				<%-- 凭条 --%>
				<label class="control-label"><spring:message code="door.doorOrder.tickertape" />：</label>
				<div class="controls">
					<form:input path="tickertape" class="" style="text-align:right;" onkeydown="if(event.keyCode==13){addTickertape();return false;}" maxlength="40" onkeyup="value=value.replace(/\s/ig,'')"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group noneRemarksDiv" style="height: 400px; width: 100%;">
				<%-- 每笔金额 --%>
				<label class="control-label"><spring:message code="door.doorOrder.detailMoney" />：</label>
				<div class="controls" style="height: 400px; overflow: auto;">
					<table id="tblTikertape" class="table">
						<thead>
							<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
								<%-- 序号 --%>
								<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
								<%-- 凭条 --%>
								<th style="text-align: center" width="30%"><spring:message code="door.doorOrder.tickertape" /></th>
								<%-- 业务类型 --%>
								<th style="text-align: center" width="20%"><spring:message code="door.doorOrder.businessType" /></th>
								<%-- 金额 --%>
								<th style="text-align: center" width="30%"><spring:message code="door.public.money" /></th>
								<%-- 操作 --%>
								<th style="text-align: center" width="10%"><spring:message code='common.operation'/></th>
							</tr>
						</thead>
						<tbody id="tbdTikertape"></tbody>
					</table> 
				</div>
			</div>
		</div>
		<%-- 页面右侧 --%>
		<div class="span7 noneRemarksDiv" style="width:50%;margin-left:0px">
			<div class="control-group" style="margin-top:80px;">
				<%-- 存款方式 --%>
				<label class="control-label"><spring:message code="door.doorOrder.saveMethod" />：</label>
				<div class="controls">
				<form:select path="" id="saveMethod" class="input-large" style="font-size:15px;color:#000000" onchange="saveMethodChange()">
					<form:option value=""><spring:message code="common.select" /></form:option>
					<form:options items="${fns:getFilterDictList('save_method',true,'01,02')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
				<span class="help-inline eqpDiv"><font color="red">*</font> </span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">金额类型：</label>
				<div class="controls">
				<form:select path="" id="amountType" class="input-large" style="font-size:15px;color:#000000" onchange="amountTypeChange()">
					<form:option value="1">金额</form:option>
					<form:option value="0">面值</form:option>
				</form:select>
				</div>
			</div>
			<div class="control-group" id="parValueDiv">
				<%-- 面值 --%>
				<label class="control-label"><spring:message code="door.public.parValue" />：</label>
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
				<label class="control-label"><spring:message code="door.public.sheetCount" />：</label>
				<div class="controls">
				<form:input path="" id="count" class="" style="text-align:right;"  maxlength="5" onkeydown="if(event.keyCode==13){addAmount();return false;}" onkeyup="value=value.replace(/[^0-9]/g,'');"/>
				<input class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" onclick="addAmount()"/>
				</div>
			</div>
			<div class="control-group" id="detailAmountDiv">
				<%-- 金额 --%>
				<label class="control-label"><spring:message code="door.public.money" />：</label>
				<div class="controls">
				<form:input path="" id="detailAmount" class="" style="text-align:right;"  maxlength="12" onkeydown="if(event.keyCode==13){addAmount();return false;}" />
				<input class="btn btn-primary" type="button" value="<spring:message code='common.add'/>" onclick="addAmount()"/>
				</div>
			</div>
			<div class="control-group"  style="height: 400px; width: 100%; overflow: auto; overflow-x: hidden;">
			<%-- 面值明细 --%>
			<label class="control-label"><spring:message code="door.public.denominationDetail" />：</label>
			<div class="controls" style="height: 400px; overflow: auto;">
				 <table id="tblAmount" class="table">
					 <thead>
						<tr style="border-bottom:none;border-right:none;background:#FFFFFF;">
							<%-- 序号 --%>
							<th style="text-align: center" width="10%"><spring:message code="common.seqNo" /></th>
							<%-- 存款方式 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.doorOrder.saveMethod" /></th>
							<%-- 面值 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.parValue" /></th>
							<%-- 张数 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.sheetCount" /></th>
							<%-- 金额 --%>
							<th style="text-align: center" width="20%"><spring:message code="door.public.money" /></th>
							<%-- 操作 --%>
							<th style="text-align: center" width="10%"><spring:message code='common.operation'/></th>
						</tr>
					</thead>
						<tbody id="tbdAmount"></tbody>
					</table> 
				</div>
			</div>
		</div>
		</div>
		
		<div class="form-actions" style="width:100%">
			<%-- 保 存 --%>
			<shiro:hasPermission name="door:doorOrderInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="button"  value="<spring:message code='common.save'/>"/>&nbsp;</shiro:hasPermission>
			<%-- 返回 --%>
			<input id="btnCancel" class="btn" type="button"
				value="<spring:message code='common.return'/>"
				onclick="history.go(-1)" />
				<%-- onclick="window.location.href='${ctx}/weChat/v03/doorOrderInfo/back'"/> --%>
		</div>
		<div style="display:none" >
			 <table id="tblTikertapeTemplate" class="table">
				<tbody id="tbdTikertape">
					<tr id="trTikertapeTemplate0" onclick="selectRow($(this))">
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<td style="text-align:center;"></td>
						<%-- 删除 --%>
						<td style="text-align:center;">
					 		<a href="#" onclick="delTikertapeConfirm(this);return false" title='<spring:message code="common.delete" />'>
					 		<i class="fa fa-trash-o text-red fa-lg"></i></a>
					 	</td>
					 	<td style="text-align:center;display:none" width="0px"></td>
					 	<!-- 存放七位码  -->
					 	<td style="text-align:center;display:none" width="0px"></td>
					</tr>
				</tbody>
			</table> 
		</div>
		
		<div style="display:none" >
			 <table id="tblAmountTemplate" class="table">
				<tbody id="tbdAmount">
					<tr id="trAmountTemplate0">
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <td style="text-align:center;"></td>
					 <%-- 删除 --%>
					 <td style="text-align:center;">
					 	<a href="#" onclick="delDetailConfirm(this);return false" title='<spring:message code="common.delete" />'>
					 	<i class="fa fa-trash-o text-red fa-lg"></i></a>
					 </td>
					 <td style="text-align:center;display:none" width="0px"></td>
					 <td style="text-align:center;display:none" width="0px"></td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="equipmentOption" id="equipmentOptionTemplate0"/>
		</form:select>
		</div>
		
		<div style="display:none" >
		<form:select path="">
			<form:option label="" value="" htmlEscape="false" name="saveTypeOption" id="saveTypeOptionTemplate0"/>
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