package com.coffer.core.common.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import com.coffer.core.common.utils.StringUtils;
import com.google.common.collect.Maps;

/**
 * 全局配置类
 * 
 * @author Clark
 * @version 2015-04-22
 * 
 *          修改为使用apache.commons.configuration加载配置文件
 */
public class Global {
	private static Logger logger = LoggerFactory.getLogger(Global.class);

	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();

	/**
	 * @author Clark
	 * @version 2014-12-31
	 * 
	 *          属性文件加载对象
	 */
	private static Configuration config = null;

	/**
	 * 显示/隐藏
	 */
	public static final String SHOW = "1";
	public static final String HIDE = "0";

	/**
	 * 是/否
	 */
	public static final String YES = "1";
	public static final String NO = "0";

	/**
	 * 对/错
	 */
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	/**
	 * 上传文件基础虚拟路径
	 */
	public static final String USERFILES_BASE_URL = "/userfiles/";

	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}

	/**
	 * @author Clark
	 * @version 2014-12-31
	 * 
	 *          加载资源文件
	 */
	public static void loadProperties() {
		try {
			config = new PropertiesConfiguration("application.properties");
		} catch (ConfigurationException e) {
			logger.error("Properties File Loads Failed");
		}
	}

	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		if (config == null) {
			loadProperties();
		}
		String value = map.get(key);
		if (value == null) {
			value = config.getString(key);
			map.put(key, value);
		}
		return value;
	}

	/**
	 * @author Clark
	 * @version 2014-12-31 value必须以【,】分隔，并且只能以String[]或List方式取得，否则只能有一个下标值
	 * 
	 * @param key
	 * @return
	 */
	public static String[] getStringArray(String key) {
		if (config == null) {
			loadProperties();
		}
		return config.getStringArray(key);
	}

	/**
	 * @author Clark
	 * @version 2014-12-31 value必须以【,】分隔，并且只能以String[]或List方式取得，否则只能有一个下标值
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> getList(String key) {
		return Arrays.asList(getStringArray(key));
	}

	/////////////////////////////////////////////////////////

	/**
	 * 获取管理端根路径
	 */
	public static String getAdminPath() {
		return getConfig("adminPath");
	}

	/**
	 * 获取前端根路径
	 */
	public static String getFrontPath() {
		return getConfig("frontPath");
	}

	/**
	 * 获取URL后缀
	 */
	public static String getUrlSuffix() {
		return getConfig("urlSuffix");
	}

	/**
	 * 是否是演示模式，演示模式下不能修改用户、角色、密码、菜单、授权
	 */
	public static Boolean isDemoMode() {
		String dm = getConfig("demoMode");
		return "true".equals(dm) || "1".equals(dm);
	}

	/**
	 * 在修改系统用户和角色时是否同步到Activiti
	 */
	public static Boolean isSynActivitiIndetity() {
		String dm = getConfig("activiti.isSynActivitiIndetity");
		return "true".equals(dm) || "1".equals(dm);
	}

	/**
	 * 页面获取常量
	 * 
	 * @see ${fns:getConst('YES')}
	 */
	public static Object getConst(String field) {
		try {
			return Global.class.getField(field).get(null);
		} catch (Exception e) {
			// 异常代表无配置，这里什么也不做
		}
		return null;
	}

	/**
	 * 获取工程路径
	 * 
	 * @return
	 */
	public static String getProjectPath() {
		// 如果配置了工程路径，则直接返回，否则自动获取。
		String projectPath = Global.getConfig("projectPath");
		if (StringUtils.isNotBlank(projectPath)) {
			return projectPath;
		}
		try {
			File file = new DefaultResourceLoader().getResource("").getFile();
			if (file != null) {
				while (true) {
					File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
					if (f == null || f.exists()) {
						break;
					}
					if (file.getParentFile() != null) {
						file = file.getParentFile();
					} else {
						break;
					}
				}
				projectPath = file.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectPath;
	}

	/**
	 * @author Clark
	 * @version 2015-01-08 是否与C#程序共用加密算法
	 * 
	 * @return
	 */
	public static boolean isCommonMd5Used() {
		String isUse = getConfig("common.md5.use");
		if (StringUtils.isBlank(isUse) || "0".equals(isUse)) {
			return false;
		}
		return true;
	}

}
