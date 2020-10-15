package com.coffer.hessian.localserver.common;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.StringUtils;
import com.google.common.collect.Maps;

/**
 * 外部接口支持类
 * 
 * @author yuxixuan
 *
 */
public abstract class BaseHessianServiceImpl extends BaseService implements BaseHessianService {

	private static final String LANG_EN = "EN";
	private static final String LANG_ZH = "ZH";

	/**
	 * 本地化
	 */
	protected Locale locale = Locale.CHINESE;
	/**
	 * 支持语言类型
	 */
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
	 * 初始化本地化类
	 */
	public void initLocale(Map<String, Object> paramMap) {
		String lang = StringUtils.toString(paramMap.get(BaseHessianParameter.LANG_KEY));
		lang = lang.toUpperCase(Locale.ENGLISH);
		if (localMap.containsKey(lang)) {
			this.locale = localMap.get(lang);
		} else {
			this.locale = Locale.CHINESE;
		}
	}

}
