package com.coffer.businesses.modules.pay.utils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.coffer.core.common.utils.StringUtils;
/**
 * 签名工具类
 * @author wangpengyu
 *
 */
public class SignUtils {
	
	public static String doSign(Map<String, Object> paramMap) {

		String paySecret = PayConstant.PAY_SECRET;
		String splicingTreatment = splicingTreatment(paramMap);
		StringBuffer stringBuffer = new StringBuffer();
		String originalSignature = stringBuffer.append(splicingTreatment).append("&paySecret=").append(paySecret)
				.toString();
		String signStr = MD5Util.encode(originalSignature).toUpperCase();
		paramMap.put("sign", signStr);
		String paramStr = splicingTreatment(paramMap);

		return signStr;
	}

	public static String splicingTreatment(Map<String, Object> paramMap) {
		SortedMap<String, Object> smap = new TreeMap<String, Object>(paramMap);
		StringBuffer stringBuffer = new StringBuffer();
		for (Map.Entry<String, Object> m : smap.entrySet()) {
			Object value = m.getValue();
			if (value != null && StringUtils.isNotBlank(String.valueOf(value))) {
				stringBuffer.append(m.getKey()).append("=").append(value).append("&");
			}
		}
		stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
		return stringBuffer.toString();
	}
}
