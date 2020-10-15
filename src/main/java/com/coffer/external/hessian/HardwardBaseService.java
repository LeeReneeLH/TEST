package com.coffer.external.hessian;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;

/**
 * 
 * Title: HardwardBaseService
 * <p>
 * Description: 外部接口支持类
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月11日 上午10:50:06
 */
public abstract class HardwardBaseService extends BaseService implements HardwardServiceInterface {
	
	/**
	 * 初始化本地化类
	 */
	protected Locale locale = Locale.CHINESE;
	
	private static final String LANG_EN = "EN";
	
	private static final String LANG_ZH = "ZH";
	/** 支持语言类型 **/
	private static Map<String, Locale> localMap = Maps.newHashMap();
	
	/**
	 * 初始化支持语言类型
	 */
	static {
		localMap.put(LANG_EN, Locale.ENGLISH);
		localMap.put(LANG_ZH, Locale.CHINESE);
	}
	
	/**
	 * 国际化资源管理器
	 */
	@Autowired
	protected MessageSource msg;
	
	/**
	 * 初始化地化类
	 * @param paramMap 接口参数
	 */
	public void initLocale(Map<String, Object> paramMap) {
		String lang = StringUtils.toString(paramMap.get(Parameter.LANG_KEY));
		lang = lang.toUpperCase(Locale.ENGLISH);
		if (localMap.containsKey(lang)) {
			this.locale = localMap.get(lang);
		} else {
			this.locale = Locale.CHINESE;
		}
	}
	
}
