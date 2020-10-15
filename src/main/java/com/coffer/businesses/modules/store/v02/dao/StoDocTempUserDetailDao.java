/**
 * wenjian:    StoDocTempUserDetailDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 上午10:01:59
 */
package com.coffer.businesses.modules.store.v02.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v02.entity.StoDocTempUserDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoDocTempUserDetailDao 
* <p>Description: 单据人员信息子表DAO </p>
* @author wangbaozhong
* @date 2016年9月12日 上午10:01:59
*/
@MyBatisDao
public interface StoDocTempUserDetailDao extends CrudDao<StoDocTempUserDetail> {
	/**
	 * 
	 * Title: deleteByDocInfoId
	 * <p>Description: 按照单据模板信息主表ID删除用户信息</p>
	 * @author:     wangbaozhong
	 * @param docInfoId
	 * @return 
	 * int    返回类型
	 */
	int deleteByDocInfoId(@Param(value="docInfoId") String docInfoId);
}
