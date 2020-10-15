package com.coffer.core.modules.sys.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.SysOtpOffice;

/**
 * 令牌机构管理DAO接口
 * @author XL
 * @version 2018-10-26
 */
@MyBatisDao
public interface SysOtpOfficeDao extends CrudDao<SysOtpOffice> {
	
	/**
	 * 关闭所有机构令牌功能
	 * 
	 * @author XL
	 * @version 2018-10-29
	 */
	public void deleteAll();
}