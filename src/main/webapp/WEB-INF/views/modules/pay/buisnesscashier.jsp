<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>聚龙支付</title>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.8.3.js"></script>
    <script type="text/javascript" src="${ctxStatic}/payFolder/jquery.qrcode.min.js"></script>
    <link href="${ctxStatic}/payFolder/pay.css" rel="stylesheet" type="text/css">

</head>
<body>

<div class="wrap_header">
    <div class="header clearfix">
        <div class="logo_panel clearfix">
            <div class="logo fl"><img src="${ctxStatic}/payFolder/dljulonglogo.png" alt="logo"  style="height:75%"></div>
            <div class="lg_txt">聚龙收银台</div>
        </div>
        <div class="fr tip_panel">
            <div class="txt">欢迎使用聚龙支付付款</div>
        </div>
    </div>
</div>

<input type="hidden" id="codeUrl" value="${codeUrl}">

<div id="box" class="">
    <!-- 扫码 -->
    <div class="x-main">
        <div class="pro"><span>应付金额 <b>￥<fmt:parseNumber type="number"
                                                                                    pattern="#,#00.0#">${orderPrice}</fmt:parseNumber></b></span>
        </div>
        <div class="weixin" id="weixinDiv">
            <div class="x-left"><img src="${ctxStatic}/payFolder/weixin.jpg" alt="微信导航图"></div>
            <div class="x-right" style="height: 410px;">
                <div class="saoma_panel">
                    <h4>扫屏幕二维码即可付款</h4>
                    <p class="tip">提示:支付成功前请勿手动关闭页面</p>
                    <div class="er" id="code" oid="4835a85a4e01402aa17f8a73c356f80d"
                         style="height: 250px;width: 250px"></div>
                    <p class="tipa">二维码两小时内有效，请计算扫码支付</p>
                </div>
            </div>
        </div>
    </div>

    <div class="suc_panel" id="sucDiv">
        <div class="hd">支付成功</div>
        <div class="txt"><span id="tiaoSpan">5</span>s后将为你<a id="returnMerchnatA" href="">返回商家</a></div>
    </div>
</div>

<div class="">
    <div class="copyright" id="footer">Copyright © 2017-2019 大连聚龙金融安全装备有限公司版权所有</div>
</div>

<script type="text/javascript">
    $(function () {

        $("#footer").text("Copyright © 2017-"+new Date().getFullYear()+" 大连聚龙金融安全装备有限公司版权所有");

        var str = $("#codeUrl").val();
        $("#code").qrcode({
            render: "table",
            width: 250,
            height: 250,
            text: str
        });
    })
</script>
</body>
</html>
