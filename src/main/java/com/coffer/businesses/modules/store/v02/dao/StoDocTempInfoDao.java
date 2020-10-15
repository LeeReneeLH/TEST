/**
 * wenjian:    StoDocTempInfoDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 上午10:05:34
 */
package com.coffer.businesses.modules.store.v02.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v02.entity.StoDocTempInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoDocTempInfoDao 
* <p>Description: 单据模板信息主表DAO</p>
* @author wangbaozhong
* @date 2016年9月12日 上午10:05:34
*/
@MyBatisDao
public interface StoDocTempInfoDao extends CrudDao<StoDocTempInfo> {
	/**
	 * 
	 * Title: getByBusinessAndStatus
	 * <p>Description: 按照业务类型和状态查询单据印章表</p>
	 * @author:     wangbaozhong
	 * @param businessType	业务类型
	 * @param status	状态
	 * @param officeId	所属机构ID
	 * @return 
	 * StoDocTempInfo    返回类型
	 */
	StoDocTempInfo getByBusinessAndStatus(@Param(value="businessType") String businessType, @Param(value="status") String status, @Param(value="officeId") String officeId);
	
	/**
	 * 
	 * Title: updatePbocOfficeStamperId
	 * <p>Description: 按照单据模板信息主表主键ID，更新人行机构印章ID</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo 更新信息
	 * @return 
	 * int    返回类型
	 */
	int updatePbocOfficeStamperId(StoDocTempInfo stoDocTempInfo);
}
