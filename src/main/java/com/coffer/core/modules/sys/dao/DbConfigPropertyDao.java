package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.DbConfigProperty;

/**
 * 系统共通项dao
 * 
 * @author xp
 * @version 2017-12-9
 */
@MyBatisDao
public abstract interface DbConfigPropertyDao extends CrudDao<DbConfigProperty> {

	/** 查询所有参数 */
	public abstract List<DbConfigProperty> findList(DbConfigProperty DbConfigProperty);

	/** 根据当前元素id查询子参数 */
	public abstract List<DbConfigProperty> findByParentId(DbConfigProperty DbConfigProperty);

	/** 删除分组 */
	public abstract int deleteGroup(DbConfigProperty DbConfigProperty);

	/** 查询参数 */
	public abstract DbConfigProperty getProperty(DbConfigProperty DbConfigProperty);
	
	/** 按照更新时间查询所有参数 */
	public abstract List<DbConfigProperty> findByUpdateDate(DbConfigProperty DbConfigProperty);

}
