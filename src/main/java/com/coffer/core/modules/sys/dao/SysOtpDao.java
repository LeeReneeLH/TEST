package com.coffer.core.modules.sys.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.SysOtp;

/**
 * otp动态口令管理DAO接口
 * @author qph
 * @version 2018-07-02
 */
@MyBatisDao
public interface SysOtpDao extends CrudDao<SysOtp> {
	
	/**
	 * 根据条件查询数据
	 * 
	 * @param entity
	 * @return
	 */
	public SysOtp getByTokenId(SysOtp sysOtp);

}