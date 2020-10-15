package com.coffer.businesses.modules.store.v02.dao;

import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;
import com.coffer.core.common.persistence.CrudDao;

/**
 * 印章管理DAO接口
 * @author Zhengkaiyuan
 * @version 2016-09-09
 */
@MyBatisDao
public interface StoOfficeStamperInfoDao extends CrudDao<StoOfficeStamperInfo> {
	
	StoOfficeStamperInfo getById(String id);
}
