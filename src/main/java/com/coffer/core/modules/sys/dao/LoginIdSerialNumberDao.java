/**
 * @author WangBaozhong
 * @version 2017年5月31日
 * 
 * 
 */
package com.coffer.core.modules.sys.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.LoginIdSerialNumber;

/**
 * 获取登陆用户ID DAO
 * @author WangBaozhong
 *
 */
@MyBatisDao
public interface LoginIdSerialNumberDao extends CrudDao<LoginIdSerialNumber> {
	/**
	 * 按照参数查询最后生成登陆用户ID序列
	 * @author WangBaozhong
	 * @version 2017年5月31日
	 * 
	 *  
	 * @param officeId	所属机构ID
	 * @return 最后生成登陆用户ID序列
	 */
	public LoginIdSerialNumber findByOfficeId(@Param("officeId") String officeId);
}
