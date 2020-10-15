//检查是否为正整数
function isUnsignedInteger(a) {
	var reg = /^[0-9]+$/;
	return reg.test(a);
}
// 修改物品
function updateGoodsItem(goodsId, goodsName, number, commitUrl) {
	var htmlShow = "<div class='row' style='margin-top:15px;height: 650'>"
			+ "<div class='span12' style='margin-top:15px'>"
			+ "<form  class='form-horizontal'>"
			+ "<div class='control-group item'>"
			+ "<label class='control-label' style='width: 120px;'>物品名称：</label>"
			+ "<div class='controls' style='margin-left: 130px;'>"
			+ "<input type='text' name='goodsName' id='goodsName' readonly='readonly' style='width:300px;' value='"
			+ goodsName
			+ "'/>"
			+ "</div>"
			+ "</div>"
			+ "<div class='control-group item'>"
			+ "<label class='control-label' style='width: 120px;'>数量：</label>"
			+ "<div class='controls' style='margin-left: 130px;'>"
			+ "<input type='text' name='moneyNumber' id='moneyNumber' maxlength='5' value='" + number + "' style='width:100px;text-align:right;' />"
			+ "<span class='help-inline'><font color='red'>* <span id='errorMsg'></span></font>"
			+ " </span>" + "</div>" + "</div>" + "</form>" + "</div>"
			+ "</div>";

	top.$.jBox.open(htmlShow, "修改物品数量", 550, 200, {
		buttons : {
			"确认" : 1,
			"关闭" : 0
		},
		submit : function(v, h, f) {
			if (v == 1) {
				h.find("#errorMsg").html("");

				moneyNumber = f.moneyNumber; // 或
				// h.find('#moneyNumber').val();

				if (moneyNumber == '') {
					h.find("#errorMsg").html("请输入物品数量");
					return false;
				}
				if (moneyNumber < 1 || isUnsignedInteger(moneyNumber) == false) {
					h.find("#errorMsg").html("请输入正整数！");
					return false;
				}

				// 币种
				var url = commitUrl;
				if (url.indexOf("?") > -1) {
					url = url + "&goodsId=" + goodsId;
				} else {
					url = url + "?goodsId=" + goodsId;
				}
				url = url + "&moneyNumber=" + moneyNumber;
				$("#moneyNumber").val("");
				$("#moneyNumber").removeClass();
				$("#inputForm").attr("action", url);
				$("#inputForm").submit();
			}
			return true;
		}
	});
}