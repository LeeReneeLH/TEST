package com.coffer.businesses.modules.collection.v03.dao;

import com.coffer.businesses.modules.collection.v03.entity.EmpWork;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 员工工作量统计DAO接口
 * 
 * @author 	wanglin
 * @date 	2017-09-04
 */
@MyBatisDao
public interface EmpWorkDao extends CrudDao<EmpWork> {

}