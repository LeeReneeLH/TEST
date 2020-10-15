package com.coffer.core.modules.sys.utils;

import java.util.List;

import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.modules.sys.dao.DbConfigPropertyDao;
import com.coffer.core.modules.sys.entity.DbConfigProperty;

/**
 * 系统共同项工具类
 * 
 * @author xp
 *
 */
public class DbConfigUtils {

	private static DbConfigPropertyDao dbConfigDao = (DbConfigPropertyDao) SpringContextHolder
			.getBean(DbConfigPropertyDao.class);
	public static final String DBCONFIG_CACHE_ID_ = "id_";
	public static final String CACHE_DBCONFIG_ALL_LIST = "dbConfigList";

	/**
	 * 查询某个参数
	 * 
	 * @author xp
	 * @param configProperty
	 * @return
	 */
	public static DbConfigProperty get(DbConfigProperty configProperty) {
		// 从缓存中取出
		DbConfigProperty dbConfigProperty = (DbConfigProperty) CacheUtils.get(CACHE_DBCONFIG_ALL_LIST,
				DBCONFIG_CACHE_ID_ + configProperty.getPropertyKey());
		// 如果缓存中没有参数
		if (dbConfigProperty == null) {
			// 查询当前参数
			dbConfigProperty = (DbConfigProperty) dbConfigDao.get(configProperty);
			if (dbConfigProperty == null) {
				return null;
			}
			// 放入缓存
			CacheUtils.put(CACHE_DBCONFIG_ALL_LIST, DBCONFIG_CACHE_ID_ + dbConfigProperty.getPropertyKey(),
					dbConfigProperty);
		}
		return dbConfigProperty;
	}

	/**
	 * 获取所有参数
	 * 
	 * @param dbConfigProperty
	 * @return
	 */
	public static List<DbConfigProperty> getDbConfigList(DbConfigProperty dbConfigProperty) {
		@SuppressWarnings("unchecked")
		List<DbConfigProperty> dbconfigList = (List<DbConfigProperty>) CacheUtils.get(CACHE_DBCONFIG_ALL_LIST,
				CACHE_DBCONFIG_ALL_LIST);
		if (dbconfigList == null) {
			dbconfigList = dbConfigDao.findList(dbConfigProperty);
		}
		CacheUtils.put(CACHE_DBCONFIG_ALL_LIST, CACHE_DBCONFIG_ALL_LIST, dbconfigList);
		return dbconfigList;
	}

	/**
	 * 系统使用查询当前参数对应的value
	 * 
	 * @version 2017-12-27
	 * @author xp
	 * @param propertyKey
	 * @return
	 */
	public static String getDbConfig(String propertyKey) {
		// 从缓存中取出
		DbConfigProperty dbConfigProperty = (DbConfigProperty) CacheUtils.get(CACHE_DBCONFIG_ALL_LIST,
				DBCONFIG_CACHE_ID_ + propertyKey);
		// 如果缓存中没有参数
		if (dbConfigProperty == null) {
			// 查询当前参数
			DbConfigProperty property = new DbConfigProperty();
			property.setPropertyKey(propertyKey);
			dbConfigProperty = (DbConfigProperty) dbConfigDao.getProperty(property);
			if (dbConfigProperty == null) {
				return null;
			}
			// 放入缓存
			CacheUtils.put(CACHE_DBCONFIG_ALL_LIST, DBCONFIG_CACHE_ID_ + dbConfigProperty.getPropertyKey(),
					dbConfigProperty);
		}
		return dbConfigProperty.getPropertyValue();

	}
}
