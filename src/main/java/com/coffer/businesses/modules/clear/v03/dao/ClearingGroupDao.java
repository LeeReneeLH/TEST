package com.coffer.businesses.modules.clear.v03.dao;

import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分组管理DAO接口
 * 
 * @author XL
 * @version 2017-08-14
 */
@MyBatisDao
public interface ClearingGroupDao extends CrudDao<ClearingGroup> {

	/**
	 * 编号、组名一致性
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @return
	 */
	public ClearingGroup findByNoAndName(ClearingGroup clearingGroup);

}