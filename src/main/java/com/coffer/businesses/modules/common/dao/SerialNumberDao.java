package com.coffer.businesses.modules.common.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.common.entity.SerialNumber;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 业务流水号DAO接口
 * 
 * @author Clark
 * @version 2015-04-28
 */
@MyBatisDao
public interface SerialNumberDao extends CrudDao<SerialNumber> {
	public SerialNumber findByBusinessType(@Param(value = "sequenceDate") String sequenceDate,
			@Param(value = "businessType") String businessType);
}