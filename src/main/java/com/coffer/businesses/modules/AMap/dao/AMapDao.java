package com.coffer.businesses.modules.AMap.dao;

import java.util.List;

import com.coffer.businesses.modules.AMap.entity.AMap;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 
 * @author liuyaowen
 * @version
 *
 */
@MyBatisDao
public interface AMapDao extends CrudDao<AMap> {
	public List<AMap> findMap();
}
