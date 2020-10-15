package com.coffer.businesses.modules.clear.v03.dao;

import com.coffer.businesses.modules.clear.v03.entity.ClErrorInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 差错管理DAO接口
 * 
 * @author XL
 * @version 2017-09-07
 */
@MyBatisDao
public interface ClErrorInfoDao extends CrudDao<ClErrorInfo> {
	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: sg
	 * @param orderClearMain
	 * @return ClErrorInfo 返回类型
	 */
	public ClErrorInfo findClearList(ClErrorInfo clErrorInfo);
}