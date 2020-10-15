package com.coffer.businesses.modules.clear.v03.dao;

import com.coffer.businesses.modules.clear.v03.entity.ClOutDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 商行取款DAO接口
 * @author wxz
 * @version 2017-08-24
 */
@MyBatisDao
public interface ClOutDetailDao extends CrudDao<ClOutDetail> {
	
}