package com.coffer.businesses.modules.pay.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.runners.Parameterized.Parameters;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.util.StringUtils;
import com.coffer.businesses.modules.pay.httpclient.SimpleHttpParam;
import com.coffer.businesses.modules.pay.httpclient.SimpleHttpResult;
import com.coffer.businesses.modules.pay.httpclient.SimpleHttpUtils;
import com.coffer.businesses.modules.pay.utils.MD5Util;
import com.coffer.businesses.modules.pay.utils.PayConstant;
import com.coffer.businesses.modules.pay.utils.SignUtils;
import com.drew.lang.StringUtil;

import net.sf.json.JSONObject;

/**
 * 支付收款接口
 * 
 * @author wangpengyu
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/pay")
public class PayController {

	private static final String PAY_RESULT_CODE = "200";
	private static final String PAY_CHARGE_CODE_URL = "商户上送重复订单";
	private static Logger log = LoggerFactory.getLogger(PayController.class);

	/**
	 * 支付测试页面跳转
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "test")
	public String test(Model model) {
		return "modules/pay/paySelect";
	}
	/**
	 * 页面跳转
	 * @param model
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "select")
	public String select(Model model,String type) {
		model.addAttribute("type", type);
		return "modules/pay/payForm";
	}
	/**
	 * 支付平台聚合支付预支付接口
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "charge")
	public String charge(Model model,String orderAmount,String type) {
		String messages = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 支付平台应用ID
		paramMap.put("appId", PayConstant.PAY_APP_ID);
		// 时间戳
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(new Date());
		paramMap.put("timestamp", dateString);
		// 订单编号
		paramMap.put("merchantOrderNo", dateString);
		// 扫码方式
		paramMap.put("chargeType", PayConstant.PAY_CHARGE_TYPE);
		if (com.coffer.core.common.utils.StringUtils.isNotBlank(orderAmount)) {
			String amount1 =BigDecimal.valueOf(Long.valueOf(orderAmount)).multiply(new BigDecimal(100)).toString(); 
			// 订单金额(请转换成以分为单位)
			paramMap.put("orderAmount", amount1);	
		}else {
			messages = "请求失败：支付金额为空";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
		// 回调通知地址
		paramMap.put("backUrl", PayConstant.PAY_BACK_URL);
		// 币种
		paramMap.put("currency", PayConstant.PAY_CURRENCY);
		// 签名
		paramMap.put("sign", SignUtils.doSign(paramMap));
		// 支付平台商户编号
		String merchantNo = PayConstant.PAY_MERCHANT_NO;
		// 支付平台聚合支付预支付接口
		String url = PayConstant.PAY_CHARGE_URL + merchantNo;
		// httpclient
		SimpleHttpParam param = new SimpleHttpParam(url);
		// GET
		param.setMethod(SimpleHttpUtils.HTTP_METHOD_GET);
		// 参数传递
		param.setParameters(paramMap);
		// 接口请求
		SimpleHttpResult result = SimpleHttpUtils.httpRequest(param);
		JSONObject resultJson = JSONObject.fromObject(result.getContent());
		JSONObject dataJson = (JSONObject) resultJson.get("data");
		String code = String.valueOf(resultJson.get("code"));
		if (PAY_RESULT_CODE.equals(code)) {
			String codeUrl = (String) dataJson.get("codeUrl");
			// 支付接口URL（根据此URL生成二维码）
			if (PAY_CHARGE_CODE_URL.equals(codeUrl)) {
				messages ="请求失败："+PAY_CHARGE_CODE_URL;
				addMessage(model, messages);
				model.addAttribute("type", type);
				return "modules/pay/payForm";
			} else {
				model.addAttribute("codeUrl", codeUrl);
				// 支付金额
				String amount = (String) paramMap.get("orderAmount");
				// 金额换算
				model.addAttribute("orderPrice",
						BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)).toString());
				return "modules/pay/buisnesscashier";
			}
		} else {
			messages = "请求失败：支付平台系统异常";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}

	}

	/**
	 * 支付平台浦发代付支付接口
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "transfer")
	public String transfer(Model model,@RequestParam Map<String, String> map) {
		Map<String, String> orderMap = new HashMap<String, String>();
		// 订单业务信息编辑
		String messages = "";
		String payaName = map.get("payName");
		String accNo = map.get("accNo");
		String bankName = map.get("bankName");
		String orderAmount = map.get("orderAmount");
		String type = map.get("type");
		// 收款人姓名（企业名称）
		if (com.coffer.core.common.utils.StringUtils.isNotBlank(payaName)) {
			orderMap.put("payName", payaName);
		}else {
			messages = "请求失败：收款人姓名为空";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
		// 银行卡号
		if (com.coffer.core.common.utils.StringUtils.isNotBlank(accNo)) {
			orderMap.put("accNo", accNo);
		}else {
			messages = "请求失败：银行卡号为空";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
		// 银行名称
		if (com.coffer.core.common.utils.StringUtils.isNotBlank(bankName)) {
			orderMap.put("bankName", bankName);
		}else {
			messages = "请求失败：银行名称为空";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
		JSONObject extra = JSONObject.fromObject(orderMap);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(new Date());
		// 接口参数编辑
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 支付平台应用ID
		paramMap.put("appId", PayConstant.PAY_APP_ID);
		// 回调通知地址
		paramMap.put("backUrl", PayConstant.PAY_BACK_URL);
		// 币种
		paramMap.put("currency", PayConstant.PAY_CURRENCY);
		// 订单业务信息（{payName:收款人姓名，accNo:银行卡卡号：bankName:银行名称}）
		paramMap.put("extra", extra);
		// 订单编号
		paramMap.put("merchantOrderNo", dateString);
		// 订单金额
		if (com.coffer.core.common.utils.StringUtils.isNotBlank(orderAmount)) {
			String amount1 =BigDecimal.valueOf(Long.valueOf(orderAmount)).multiply(new BigDecimal(100)).toString(); 
			paramMap.put("orderAmount", amount1);
		}else {
			messages = "请求失败：支付金额为空";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
		paramMap.put("orderAmount", "100");
		// 时间戳
		paramMap.put("timestamp", dateString);
		// 签名
		paramMap.put("sign", SignUtils.doSign(paramMap));
		JSONObject paramJson = JSONObject.fromObject(paramMap);
		// 支付平台商户编号
		String merchantNo = PayConstant.PAY_MERCHANT_NO;
		// 支付平台浦发代付支付接口
		String url = PayConstant.PAY_TRANSFER_URL + merchantNo;
		SimpleHttpParam param = new SimpleHttpParam(url);
		// POST
		param.setMethod(SimpleHttpUtils.HTTP_METHOD_POST);
		// 参数传递
		param.setPostData(paramJson.toString());
		// 接口请求
		SimpleHttpResult result = SimpleHttpUtils.httpRequest(param);
		JSONObject resultJson = JSONObject.fromObject(result.getContent());
		String code = String.valueOf(resultJson.get("code"));
		if (PAY_RESULT_CODE.equals(code)) {
			JSONObject dataJson = (JSONObject) resultJson.get("data");
			String status = (String) dataJson.get("status");
			messages = status;
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		} else {
			messages = "请求失败：支付平台系统异常";
			addMessage(model, messages);
			model.addAttribute("type", type);
			return "modules/pay/payForm";
		}
	}
	/**
	 * 接收支付平台回调接口
	 */
	@RequestMapping(value = "back")
	public void payBack(HttpServletResponse response , HttpServletRequest req) {
		PrintWriter pWriter = null;
		String param = req.getParameter("data");
		System.out.println(param);
		log.info(param);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
			pWriter = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
        pWriter.println("success");
	}
	/**
	 * 添加Model消息
	 * 
	 * @param message
	 */
	protected void addMessage(Model model, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages) {
			sb.append(message).append(messages.length > 1 ? "<br/>" : "");
		}
		model.addAttribute("message", sb.toString());
	}

}
