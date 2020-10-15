
		$(document).ready(function() {
			$("#registerAmountShow").html("0.00");

			// 初始显示
			initShow();
			computeAmount();
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
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
		
		// 计算物品金额
		function computeAmount(toId, goodsNumId, goodsValueId){
			var goodsNum = $("#" + goodsNumId).val();
			var goodsValue = $("#" + goodsValueId).val();
			var amount = 0;
			if (typeof(goodsNum) != "undefined" && typeof(goodsValue) != "undefined") {
				amount = goodsNum * goodsValue;
			}
			
			if (goodsNum == 0) {
				amount = 0;
			}
			
			if (!isNaN(amount)) {
				$("#" + toId).val(amount);
				amount = formatCurrency(amount);
				$("#" + toId + "Show").html(amount);
			}
			// 设置申请差额，并转换大写金额
			setOrderDiffence();
		}
		
		/**
		 * 设置申请差额，并转换大写金额
		 * @returns {Number}
		 */
		function setOrderDiffence() {
			var sumOrderAmount = getOrderSum();
			$("#registerAmount").val(sumOrderAmount);
			$("#registerAmountShow").html(formatCurrency(sumOrderAmount));
			var registerAmount = changeAmountToNum($("#registerAmount").val());
			//var diffence = -1;
			if (typeof(registerAmount) != "undefined" && !isNaN(registerAmount)) {
				changeAmountToBig(registerAmount,"orderBigRmb");
				//diffence = registerAmount - sumOrderAmount;
				//$("#orderDifference").html("申请差额：" + formatCurrency(diffence));
			}
			//return diffence;
		}

		// 取得申请物品总金额
		function getOrderSum() {
			var sumOrder = 0;
			// 完整券
			// 100元
			var goodsId100YuanOrderAmount = changeAmountToNum($("#goodsId100YuanOrderAmount").val());
			if (!isNaN(goodsId100YuanOrderAmount)) {
				sumOrder += Number(goodsId100YuanOrderAmount);
			}
			// 50元
			var goodsId50YuanOrderAmount = changeAmountToNum($("#goodsId50YuanOrderAmount").val());
			if (!isNaN(goodsId50YuanOrderAmount)) {
				sumOrder += Number(goodsId50YuanOrderAmount);
			}
			// 20元
			var goodsId20YuanOrderAmount = changeAmountToNum($("#goodsId20YuanOrderAmount").val());
			if (!isNaN(goodsId20YuanOrderAmount)) {
				sumOrder += Number(goodsId20YuanOrderAmount);
			}
			// 10元
			var goodsId10YuanOrderAmount = changeAmountToNum($("#goodsId10YuanOrderAmount").val());
			if (!isNaN(goodsId10YuanOrderAmount)) {
				sumOrder += Number(goodsId10YuanOrderAmount);
			}
			// 5元
			var goodsId5YuanOrderAmount = changeAmountToNum($("#goodsId5YuanOrderAmount").val());
			if (!isNaN(goodsId5YuanOrderAmount)) {
				sumOrder += Number(goodsId5YuanOrderAmount);
			}
			// 2元
			var goodsId2YuanOrderAmount = changeAmountToNum($("#goodsId2YuanOrderAmount").val());
			if (!isNaN(goodsId2YuanOrderAmount)) {
				sumOrder += Number(goodsId2YuanOrderAmount);
			}
			// 1元
			var goodsId1YuanOrderAmount = changeAmountToNum($("#goodsId1YuanOrderAmount").val());
			if (!isNaN(goodsId1YuanOrderAmount)) {
				sumOrder += Number(goodsId1YuanOrderAmount);
			}
			// 5角
			var goodsId5JiaoOrderAmount  = changeAmountToNum($("#goodsId5JiaoOrderAmount").val());
			if (!isNaN(goodsId5JiaoOrderAmount)) {
				sumOrder += Number(goodsId5JiaoOrderAmount);
			}
			// 2角
			var goodsId2JiaoOrderAmount  = changeAmountToNum($("#goodsId2JiaoOrderAmount").val());
			if (!isNaN(goodsId2JiaoOrderAmount)) {
				sumOrder += Number(goodsId2JiaoOrderAmount);
			}
			// 1角
			var goodsId1JiaoOrderAmount  = changeAmountToNum($("#goodsId1JiaoOrderAmount").val());
			if (!isNaN(goodsId1JiaoOrderAmount)) {
				sumOrder += Number(goodsId1JiaoOrderAmount);
			}
		
			// 损伤券
			// 100元
			var goodsIdDamaged100YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged100YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged100YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged100YuanOrderAmount);
			}
			// 50元
			var goodsIdDamaged50YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged50YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged50YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged50YuanOrderAmount);
			}
			// 20元
			var goodsIdDamaged20YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged20YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged20YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged20YuanOrderAmount);
			}
			// 10元
			var goodsIdDamaged10YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged10YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged10YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged10YuanOrderAmount);
			}
			// 5元
			var goodsIdDamaged5YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged5YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged5YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged5YuanOrderAmount);
			}
			// 2元
			var goodsIdDamaged2YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged2YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged2YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged2YuanOrderAmount);
			}
			// 1元
			var goodsIdDamaged1YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged1YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged1YuanOrderAmount)) {
				sumOrder += Number(goodsIdDamaged1YuanOrderAmount);
			}
			// 5角
			var goodsIdDamaged5JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged5JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged5JiaoOrderAmount)) {
				sumOrder += Number(goodsIdDamaged5JiaoOrderAmount);
			}
			// 2角
			var goodsIdDamaged2JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged2JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged2JiaoOrderAmount)) {
				sumOrder += Number(goodsIdDamaged2JiaoOrderAmount);
			}
			// 1角
			var goodsIdDamaged1JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged1JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged1JiaoOrderAmount)) {
				sumOrder += Number(goodsIdDamaged1JiaoOrderAmount);
			}
			return sumOrder;
		}
		
		/**
		 * 将申请金额大写
		 * @param amount
		 * @param toId
		 */
		function changeAmountToBig(amount, toId) {
			$.ajax({
				url : CTX + '/allocation/v02/pbocHandIn/changRMBAmountToBig?amount=' + amount,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					
					$("#" + toId).html(data.bigAmount);
				},
				error : function(data, status, e) {
					
				}
			});
		}
		
		
		/**
		 * 初始化显示
		 */
		function initShow() {
			/* 申请物品金额 完整券*/
			// 100元
			var goodsId100YuanOrderAmount = changeAmountToNum($("#goodsId100YuanOrderAmount").val());
			if (!isNaN(goodsId100YuanOrderAmount)) {
				$("#goodsId100YuanOrderAmountShow").html(formatCurrency(goodsId100YuanOrderAmount));
			}
			// 50元
			var goodsId50YuanOrderAmount = changeAmountToNum($("#goodsId50YuanOrderAmount").val());
			if (!isNaN(goodsId50YuanOrderAmount)) {
				$("#goodsId50YuanOrderAmountShow").html(formatCurrency(goodsId50YuanOrderAmount));
			}
			// 20元
			var goodsId20YuanOrderAmount = changeAmountToNum($("#goodsId20YuanOrderAmount").val());
			if (!isNaN(goodsId20YuanOrderAmount)) {
				$("#goodsId20YuanOrderAmountShow").html(formatCurrency(goodsId20YuanOrderAmount));
			}
			// 10元
			var goodsId10YuanOrderAmount = changeAmountToNum($("#goodsId10YuanOrderAmount").val());
			if (!isNaN(goodsId10YuanOrderAmount)) {
				$("#goodsId10YuanOrderAmountShow").html(formatCurrency(goodsId10YuanOrderAmount));
			}
			// 5元
			var goodsId5YuanOrderAmount = changeAmountToNum($("#goodsId5YuanOrderAmount").val());
			if (!isNaN(goodsId5YuanOrderAmount)) {
				$("#goodsId5YuanOrderAmountShow").html(formatCurrency(goodsId5YuanOrderAmount));
			}
			// 2元
			var goodsId2YuanOrderAmount = changeAmountToNum($("#goodsId2YuanOrderAmount").val());
			if (!isNaN(goodsId2YuanOrderAmount)) {
				$("#goodsId2YuanOrderAmountShow").html(formatCurrency(goodsId2YuanOrderAmount));
			}
			// 1元
			var goodsId1YuanOrderAmount = changeAmountToNum($("#goodsId1YuanOrderAmount").val());
			if (!isNaN(goodsId1YuanOrderAmount)) {
				$("#goodsId1YuanOrderAmountShow").html(formatCurrency(goodsId1YuanOrderAmount));
			}
			// 5角
			var goodsId5JiaoOrderAmount  = changeAmountToNum($("#goodsId5JiaoOrderAmount").val());
			if (!isNaN(goodsId5JiaoOrderAmount)) {
				$("#goodsId5JiaoOrderAmountShow").html(formatCurrency(goodsId5JiaoOrderAmount));
			}
			// 2角
			var goodsId2JiaoOrderAmount  = changeAmountToNum($("#goodsId2JiaoOrderAmount").val());
			if (!isNaN(goodsId2JiaoOrderAmount)) {
				$("#goodsId2JiaoOrderAmountShow").html(formatCurrency(goodsId2JiaoOrderAmount));
			}
			// 1角
			var goodsId1JiaoOrderAmount  = changeAmountToNum($("#goodsId1JiaoOrderAmount").val());
			if (!isNaN(goodsId1JiaoOrderAmount)) {
				$("#goodsId1JiaoOrderAmountShow").html(formatCurrency(goodsId1JiaoOrderAmount));
			}
		
			/*申请金额 损伤券*/
			// 100元
			var goodsIdDamaged100YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged100YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged100YuanOrderAmount)) {
				$("#goodsIdDamaged100YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged100YuanOrderAmount));
			}
			// 50元
			var goodsIdDamaged50YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged50YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged50YuanOrderAmount)) {
				$("#goodsIdDamaged50YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged50YuanOrderAmount));
			}
			// 20元
			var goodsIdDamaged20YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged20YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged20YuanOrderAmount)) {
				$("#goodsIdDamaged20YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged20YuanOrderAmount));
			}
			// 10元
			var goodsIdDamaged10YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged10YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged10YuanOrderAmount)) {
				$("#goodsIdDamaged10YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged10YuanOrderAmount));
			}
			// 5元
			var goodsIdDamaged5YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged5YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged5YuanOrderAmount)) {
				$("#goodsIdDamaged5YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged5YuanOrderAmount));
			}
			// 2元
			var goodsIdDamaged2YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged2YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged2YuanOrderAmount)) {
				$("#goodsIdDamaged2YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged2YuanOrderAmount));
			}
			// 1元
			var goodsIdDamaged1YuanOrderAmount = changeAmountToNum($("#goodsIdDamaged1YuanOrderAmount").val());
			if (!isNaN(goodsIdDamaged1YuanOrderAmount)) {
				$("#goodsIdDamaged1YuanOrderAmountShow").html(formatCurrency(goodsIdDamaged1YuanOrderAmount));
			}
			// 5角
			var goodsIdDamaged5JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged5JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged5JiaoOrderAmount)) {
				$("#goodsIdDamaged5JiaoOrderAmountShow").html(formatCurrency(goodsIdDamaged5JiaoOrderAmount));
			}
			// 2角
			var goodsIdDamaged2JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged2JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged2JiaoOrderAmount)) {
				$("#goodsIdDamaged2JiaoOrderAmountShow").html(formatCurrency(goodsIdDamaged2JiaoOrderAmount));
			}
			// 1角
			var goodsIdDamaged1JiaoOrderAmount  = changeAmountToNum($("#goodsIdDamaged1JiaoOrderAmount").val());
			if (!isNaN(goodsIdDamaged1JiaoOrderAmount)) {
				$("#goodsIdDamaged1JiaoOrderAmountShow").html(formatCurrency(goodsIdDamaged1JiaoOrderAmount));
			}
			
			
			/* 审批确认金额 完整券*/
			// 100元
			var goodsId100YuanConfirmAmount = changeAmountToNum($("#goodsId100YuanConfirmAmount").val());
			if (!isNaN(goodsId100YuanConfirmAmount)) {
				$("#goodsId100YuanConfirmAmountShow").html(formatCurrency(goodsId100YuanConfirmAmount));
			}
			// 50元
			var goodsId50YuanConfirmAmount = changeAmountToNum($("#goodsId50YuanConfirmAmount").val());
			if (!isNaN(goodsId50YuanConfirmAmount)) {
				$("#goodsId50YuanConfirmAmountShow").html(formatCurrency(goodsId50YuanConfirmAmount));
			}
			// 20元
			var goodsId20YuanConfirmAmount = changeAmountToNum($("#goodsId20YuanConfirmAmount").val());
			if (!isNaN(goodsId20YuanConfirmAmount)) {
				$("#goodsId20YuanConfirmAmountShow").html(formatCurrency(goodsId20YuanConfirmAmount));
			}
			// 10元
			var goodsId10YuanConfirmAmount = changeAmountToNum($("#goodsId10YuanConfirmAmount").val());
			if (!isNaN(goodsId10YuanConfirmAmount)) {
				$("#goodsId10YuanConfirmAmountShow").html(formatCurrency(goodsId10YuanConfirmAmount));
			}
			// 5元
			var goodsId5YuanConfirmAmount = changeAmountToNum($("#goodsId5YuanConfirmAmount").val());
			if (!isNaN(goodsId5YuanConfirmAmount)) {
				$("#goodsId5YuanConfirmAmountShow").html(formatCurrency(goodsId5YuanConfirmAmount));
			}
			// 2元
			var goodsId2YuanConfirmAmount = changeAmountToNum($("#goodsId2YuanConfirmAmount").val());
			if (!isNaN(goodsId2YuanConfirmAmount)) {
				$("#goodsId2YuanConfirmAmountShow").html(formatCurrency(goodsId2YuanConfirmAmount));
			}
			// 1元
			var goodsId1YuanConfirmAmount = changeAmountToNum($("#goodsId1YuanConfirmAmount").val());
			if (!isNaN(goodsId1YuanConfirmAmount)) {
				$("#goodsId1YuanConfirmAmountShow").html(formatCurrency(goodsId1YuanConfirmAmount));
			}
			// 5角
			var goodsId5JiaoConfirmAmount  = changeAmountToNum($("#goodsId5JiaoConfirmAmount").val());
			if (!isNaN(goodsId5JiaoConfirmAmount)) {
				$("#goodsId5JiaoConfirmAmountShow").html(formatCurrency(goodsId5JiaoConfirmAmount));
			}
			// 2角
			var goodsId2JiaoConfirmAmount  = changeAmountToNum($("#goodsId2JiaoConfirmAmount").val());
			if (!isNaN(goodsId2JiaoConfirmAmount)) {
				$("#goodsId2JiaoConfirmAmountShow").html(formatCurrency(goodsId2JiaoConfirmAmount));
			}
			// 1角
			var goodsId1JiaoConfirmAmount  = changeAmountToNum($("#goodsId1JiaoConfirmAmount").val());
			if (!isNaN(goodsId1JiaoConfirmAmount)) {
				$("#goodsId1JiaoConfirmAmountShow").html(formatCurrency(goodsId1JiaoConfirmAmount));
			}
			
			/* 审批确认金额 损伤券*/
			// 100元
			var goodsIdDamaged100YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged100YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged100YuanConfirmAmount)) {
				$("#goodsIdDamaged100YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged100YuanConfirmAmount));
			}
			// 50元
			var goodsIdDamaged50YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged50YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged50YuanConfirmAmount)) {
				$("#goodsIdDamaged50YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged50YuanConfirmAmount));
			}
			// 20元
			var goodsIdDamaged20YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged20YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged20YuanConfirmAmount)) {
				$("#goodsIdDamaged20YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged20YuanConfirmAmount));
			}
			// 10元
			var goodsIdDamaged10YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged10YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged10YuanConfirmAmount)) {
				$("#goodsIdDamaged10YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged10YuanConfirmAmount));
			}
			// 5元
			var goodsIdDamaged5YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged5YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged5YuanConfirmAmount)) {
				$("#goodsIdDamaged5YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged5YuanConfirmAmount));
			}
			// 2元
			var goodsIdDamaged2YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged2YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged2YuanConfirmAmount)) {
				$("#goodsIdDamaged2YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged2YuanConfirmAmount));
			}
			// 1元
			var goodsIdDamaged1YuanConfirmAmount = changeAmountToNum($("#goodsIdDamaged1YuanConfirmAmount").val());
			if (!isNaN(goodsIdDamaged1YuanConfirmAmount)) {
				$("#goodsIdDamaged1YuanConfirmAmountShow").html(formatCurrency(goodsIdDamaged1YuanConfirmAmount));
			}
			// 5角
			var goodsIdDamaged5JiaoConfirmAmount  = changeAmountToNum($("#goodsIdDamaged5JiaoConfirmAmount").val());
			if (!isNaN(goodsIdDamaged5JiaoConfirmAmount)) {
				$("#goodsIdDamaged5JiaoConfirmAmountShow").html(formatCurrency(goodsIdDamaged5JiaoConfirmAmount));
			}
			// 2角
			var goodsIdDamaged2JiaoConfirmAmount  = changeAmountToNum($("#goodsIdDamaged2JiaoConfirmAmount").val());
			if (!isNaN(goodsIdDamaged2JiaoConfirmAmount)) {
				$("#goodsIdDamaged2JiaoConfirmAmountShow").html(formatCurrency(goodsIdDamaged2JiaoConfirmAmount));
			}
			// 1角
			var goodsIdDamaged1JiaoConfirmAmount  = changeAmountToNum($("#goodsIdDamaged1JiaoConfirmAmount").val());
			if (!isNaN(goodsIdDamaged1JiaoConfirmAmount)) {
				$("#goodsIdDamaged1JiaoConfirmAmountShow").html(formatCurrency(goodsIdDamaged1JiaoConfirmAmount));
			}
			
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
		 * 将数值四舍五入(保留2位小数)后格式化成金额形式
		 *
		 * @param num 数值(Number或者String)
		 * @return 金额格式的字符串,如'1,234,567.45'
		 * @type String
		 */
		function formatCurrency(num) {
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