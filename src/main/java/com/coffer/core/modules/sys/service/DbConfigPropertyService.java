package com.coffer.core.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.dao.DbConfigPropertyDao;
import com.coffer.core.modules.sys.entity.DbConfigProperty;
import com.coffer.core.modules.sys.utils.DbConfigUtils;

/**
 * 系统共通项service
 * 
 * @author xp
 * @version 2017-12-9
 */
@Service
@Transactional(readOnly = false)
public class DbConfigPropertyService extends CrudService<DbConfigPropertyDao, DbConfigProperty> {
	@Autowired
	private DbConfigPropertyDao dbConfigPropertyDao;

	/**
	 * 插入参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 */
	@Transactional(readOnly = false)
	public void insert(DbConfigProperty dbConfigProperty) {
		// 批量更新当前元素所有父级参数的更新时间
		String[] parentList = StringUtils.split(dbConfigProperty.getParentIds(), DbConfigConstant.Punctuation.COMMA);
		for (String parent : parentList) {
			if (DbConfigConstant.AllParent.FIRST_PARENT_ID.equals(parent)) {
				continue;
			}
			DbConfigProperty parentProperty = new DbConfigProperty();
			// 设置id
			parentProperty.setId(parent);
			// 设置更新时间为当前参数的更新时间
			parentProperty.setUpdateDate(dbConfigProperty.getUpdateDate());
			int result = dbConfigPropertyDao.update(parentProperty);
			// 如果受影响的行数为0，此数据已被其他人员修改
			if (DbConfigConstant.sqlResult.ZERO == result) {
				throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
			}
		}
		dbConfigPropertyDao.insert(dbConfigProperty);
		CacheUtils.removeAll(DbConfigConstant.DbUtils.CACHE_DBCONFIG_ALL_LIST);
	}

	/**
	 * 删除参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 */
	@Transactional(readOnly = false)
	public void delete(DbConfigProperty dbConfigProperty) {
		// 批量更新当前元素所有父级参数的更新时间
		String[] parentList = StringUtils.split(dbConfigProperty.getParentIds(), DbConfigConstant.Punctuation.COMMA);
		for (String parent : parentList) {
			if (DbConfigConstant.AllParent.FIRST_PARENT_ID.equals(parent)) {
				continue;
			}
			DbConfigProperty parentProperty = new DbConfigProperty();
			// 设置id
			parentProperty.setId(parent);
			// 设置更新时间为当前参数的更新时间
			parentProperty.setUpdateDate(dbConfigProperty.getUpdateDate());
			int result = dbConfigPropertyDao.update(parentProperty);
			// 如果受影响的行数为0，此数据已被其他人员修改
			if (DbConfigConstant.sqlResult.ZERO == result) {
				throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
			}
		}
		int result = dbConfigPropertyDao.delete(dbConfigProperty);
		// 如果受影响的行数为0，此数据已被其他人员修改
		if (DbConfigConstant.sqlResult.ZERO == result) {
			throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
		}
		CacheUtils.removeAll(DbConfigConstant.DbUtils.CACHE_DBCONFIG_ALL_LIST);
	}

	/**
	 * 删除分组
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 */
	@Transactional(readOnly = false)
	public void deleteGroup(DbConfigProperty dbConfigProperty) {
		// 批量更新当前元素所有父级参数的更新时间
		String[] parentList = StringUtils.split(dbConfigProperty.getParentIds(), DbConfigConstant.Punctuation.COMMA);
		for (String parent : parentList) {
			if (DbConfigConstant.AllParent.FIRST_PARENT_ID.equals(parent)) {
				continue;
			}
			DbConfigProperty parentProperty = new DbConfigProperty();
			// 设置id
			parentProperty.setId(parent);
			// 设置更新时间为当前参数的更新时间
			parentProperty.setUpdateDate(dbConfigProperty.getUpdateDate());
			int result = dbConfigPropertyDao.update(parentProperty);
			// 如果受影响的行数为0，此数据已被其他人员修改
			if (DbConfigConstant.sqlResult.ZERO == result) {
				throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
			}
		}
		int result = dbConfigPropertyDao.deleteGroup(dbConfigProperty);
		// 如果受影响的行数为0，此数据已被其他人员修改
		if (DbConfigConstant.sqlResult.ZERO == result) {
			throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
		}
		CacheUtils.removeAll(DbConfigConstant.DbUtils.CACHE_DBCONFIG_ALL_LIST);
	}

	/**
	 * 修改参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 */
	@Transactional(readOnly = false)
	public void update(DbConfigProperty dbConfigProperty) {
		// 批量更新当前元素所有父级参数的更新时间
		String[] parentList = StringUtils.split(dbConfigProperty.getParentIds(), DbConfigConstant.Punctuation.COMMA);
		for (String parent : parentList) {
			if (DbConfigConstant.AllParent.FIRST_PARENT_ID.equals(parent)) {
				continue;
			}
			DbConfigProperty parentProperty = new DbConfigProperty();
			// 设置id
			parentProperty.setId(parent);
			// 设置更新时间为当前参数的更新时间
			parentProperty.setUpdateDate(dbConfigProperty.getUpdateDate());
			int result = dbConfigPropertyDao.update(parentProperty);
			// 如果受影响的行数为0，此数据已被其他人员修改
			if (DbConfigConstant.sqlResult.ZERO == result) {
				throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
			}
		}
		int result = dbConfigPropertyDao.update(dbConfigProperty);
		// 如果受影响的行数为0，此数据已被其他人员修改
		if (DbConfigConstant.sqlResult.ZERO == result) {
			throw new BusinessException("message.E9014", "", new String[] { dbConfigProperty.getPropertyKey() });
		}
		CacheUtils.removeAll(DbConfigConstant.DbUtils.CACHE_DBCONFIG_ALL_LIST);
	}

	/**
	 * 查询所有参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<DbConfigProperty> findList(DbConfigProperty dbConfigProperty) {
		return DbConfigUtils.getDbConfigList(dbConfigProperty);
	}

	/**
	 * 获取某个参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 * @return
	 */
	@Transactional(readOnly = true)
	public DbConfigProperty getProperty(DbConfigProperty dbConfigProperty) {
		return dbConfigPropertyDao.getProperty(dbConfigProperty);
	}

	/**
	 * 根据当前元素id查询子参数
	 * 
	 * @author xp
	 * @version 2017-12-27
	 * @param paramDbConfigProperty
	 * @return
	 */
	public List<DbConfigProperty> findByParentId(DbConfigProperty dbConfigProperty) {
		return dbConfigPropertyDao.findByParentId(dbConfigProperty);
	}
	
	/**
	 * 
	 * Title: findByUpdateDate
	 * <p>Description: 根据更新时间查询所有参数</p>
	 * @author:     lihe
	 * @version 2018-1-23
	 * @param dbConfigProperty
	 * @return 
	 * List<DbConfigProperty>    返回类型
	 */
	public List<DbConfigProperty> findByUpdateDate(DbConfigProperty dbConfigProperty) {
		return dbConfigPropertyDao.findByUpdateDate(dbConfigProperty);
	}
}
