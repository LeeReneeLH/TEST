/**
 * wenjian:    StoreTypeAssocationDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午8:35:17
 */
package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoreTypeAssocation;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoreTypeAssocationDao 
* <p>Description: 库房物品类型关联表</p>
* @author wangbaozhong
* @date 2017年8月9日 上午8:35:17
*/
@MyBatisDao
public interface StoreTypeAssocationDao extends CrudDao<StoreTypeAssocation> {
	/**
	 * 
	 * Title: getByStoreId
	 * <p>Description: 按库房ID查询库房物品类型关联列表</p>
	 * @author:     wangbaozhong
	 * @param storeId 库房ID
	 * @return 库房物品类型关联列表
	 * List<StoreManagerAssocation>    返回类型
	 */
	public List<StoreTypeAssocation> getByStoreId(@Param(value = "storeId") String storeId);
	
	/**
	 * 
	 * Title: deleteByStoreId
	 * <p>Description: 按库房ID删除库房物品类型关联列表</p>
	 * @author:     wangbaozhong
	 * @param storeId 库房ID
	 * @return 删除数量
	 * int    返回类型
	 */
	public int deleteByStoreId(@Param(value = "storeId") String storeId);
}
