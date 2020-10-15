/**
 * wenjian:    StoreGoodsInfoDao.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午8:35:55
 */
package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoreGoodsInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
* Title: StoreGoodsInfoDao 
* <p>Description: 库房物品信息表</p>
* @author wangbaozhong
* @date 2017年8月9日 上午8:35:55
*/
@MyBatisDao
public interface StoreGoodsInfoDao extends CrudDao<StoreGoodsInfo> {
	/**
	 * 
	 * Title: getByStoreId
	 * <p>Description: 按库房ID查询库房货品信息</p>
	 * @author:     wangbaozhong
	 * @param storeId	库房ID
	 * @return 
	 * StoreGoodsInfo    返回类型
	 */
	public List<StoreGoodsInfo> getByStoreId(@Param (value = "storeId") String storeId);
	
	/**
	 * 
	 * Title: deleteByRfid
	 * <p>Description: 按RFID设置货品无效</p>
	 * @author:     wangbaozhong
	 * @param entity	修改参数
	 * @return 
	 * int    返回类型
	 */
	public int deleteByRfidAndBoxNo(StoreGoodsInfo entity);
}
