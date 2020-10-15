/**
 * wenjian:    StoreGoodsDetailDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午8:39:30
 */
package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoreGoodsDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoreGoodsDetailDao 
* <p>Description: 库房物品明细表</p>
* @author wangbaozhong
* @date 2017年8月9日 上午8:39:30
*/
@MyBatisDao
public interface StoreGoodsDetailDao extends CrudDao<StoreGoodsDetail> {

	/**
	 * 
	 * Title: getStoreGoodsId
	 * <p>Description: 按照库房货品ID查询货品明细列表</p>
	 * @author:     wangbaozhong
	 * @param storeGoodsId 库房货品ID
	 * @return 货品明细列表
	 * List<StoreGoodsDetail>    返回类型
	 */
	public List<StoreGoodsDetail> getByStoreGoodsId(@Param(value = "storeGoodsId") String storeGoodsId);
}
