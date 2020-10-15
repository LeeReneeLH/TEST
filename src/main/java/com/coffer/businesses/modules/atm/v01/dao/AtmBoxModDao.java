package com.coffer.businesses.modules.atm.v01.dao;

import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 钞箱类型配置DAO接口
 * 
 * @author Murphy
 * @version 2015-05-15
 */
@MyBatisDao
public interface AtmBoxModDao extends CrudDao<AtmBoxMod> {

}