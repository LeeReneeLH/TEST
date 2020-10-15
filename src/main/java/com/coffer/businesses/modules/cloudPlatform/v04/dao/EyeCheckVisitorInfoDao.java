package com.coffer.businesses.modules.cloudPlatform.v04.dao;

import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckVisitorInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 双目识别访客信息DAO接口
 * @author XL
 * @version 2018-12-07
 */
@MyBatisDao
public interface EyeCheckVisitorInfoDao extends CrudDao<EyeCheckVisitorInfo> {
	
}