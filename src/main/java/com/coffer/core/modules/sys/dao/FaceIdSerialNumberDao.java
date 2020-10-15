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
import com.coffer.core.modules.sys.entity.FaceIdSerialNumber;

/**
 * 获取脸谱ID DAO
 * @author WangBaozhong
 *
 */
@MyBatisDao
public interface FaceIdSerialNumberDao extends CrudDao<FaceIdSerialNumber> {
	/**
	 * 按照参数查询当前用户所属机构最后生成的脸谱ID
	 * @author WangBaozhong
	 * @version 2017年5月31日
	 * 
	 *  
	 * @param userOfficeType	用户所属机构类型
	 * @param pbocOfficeId	所在人行机构ID
	 * @return	当前用户所属机构最后生成的脸谱ID
	 */
	public FaceIdSerialNumber findFaceId(@Param("userOfficeType") String userOfficeType, @Param("pbocOfficeId") String pbocOfficeId);
}
