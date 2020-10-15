package com.coffer.businesses.modules.pay.utils;
/**
 * 支付平台常量类
 * @author wangpengyu
 *
 */
public class PayConstant {
	/**
	 * 8255支付平台商户编号
	 */
	public static final String PAY_MERCHANT_NO = "10005";
	/**
	 * 扫码方式
	 */
	public static final String PAY_CHARGE_TYPE = "主动扫码";
	/**
	 * 8255支付平台应用编号
	 */
	public static final String PAY_APP_ID = "31277844651885281281";
	/**
	 * 回调通知地址
	 */
	public static final String PAY_BACK_URL = "162.16.3.100:8090/szyh/main/pay/back";
	/**
	 * 币种
	 */
	public static final String PAY_CURRENCY = "CNY";
	/**
	 * 支付平台浦发代付支付请求接口（post）
	 */
	public static final String PAY_TRANSFER_URL = "http://162.16.3.100:9092/gateway/transfer/pay/";
	/**
	 * 支付平台聚合支付预支付接口（get）
	 */
	public static final String PAY_CHARGE_URL = "http://162.16.3.100:9092/gateway/charge/";
	/**
	 * 支付平台验签密钥
	 */
	public static final String PAY_SECRET = "0a1655669ae44a28922ce73e3e0ddfa2";
}
