/**
 * wenjian:    StoreManagementInfoDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午8:32:35
 */
package com.coffer.businesses.modules.store.v01.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoreManagementInfoDao 
* <p>Description: 库房管理主表</p>
* @author wangbaozhong
* @date 2017年8月9日 上午8:32:35
*/
@MyBatisDao
public interface StoreManagementInfoDao extends CrudDao<StoreManagementInfo>{
	
	/**
	 * 
	 * Title: updateStatus
	 * <p>Description: 更新库房状态</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @return 更新数量
	 * int    返回类型
	 */
	public int updateStatus(StoreManagementInfo storeManagementInfo);
	
	/**
	 * 
	 * Title: getStoreInfoByStoreName
	 * <p>Description: 库房名称</p>
	 * @author:     wangbaozhong
	 * @param storeName
	 * @return 
	 * StoreManagementInfo    返回类型
	 */
	public StoreManagementInfo getStoreInfoByStoreName(@Param(value = "storeName") String storeName);
}
