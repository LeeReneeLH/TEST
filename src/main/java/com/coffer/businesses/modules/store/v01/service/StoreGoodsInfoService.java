/**
 * wenjian:    StoreGoodsInfoService.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午11:14:26
 */
package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoreGoodsDetailDao;
import com.coffer.businesses.modules.store.v01.dao.StoreGoodsInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreGoodsDetail;
import com.coffer.businesses.modules.store.v01.entity.StoreGoodsInfo;
import com.coffer.core.common.service.CrudService;

/**
* Title: StoreGoodsInfoService 
* <p>Description: 库房物品表Service</p>
* @author wangbaozhong
* @date 2017年8月9日 上午11:14:26
*/
@Service
@Transactional(readOnly = true)
public class StoreGoodsInfoService extends CrudService<StoreGoodsInfoDao, StoreGoodsInfo> {
	
	@Autowired
	private StoreGoodsDetailDao storeGoodsDetailDao;
	
	/**
	 * 
	 * Title: saveGoods
	 * <p>Description: 保存货品信息</p>
	 * @author:     wangbaozhong
	 * @param storeGoodsInfo 
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void saveGoods(List<StoreGoodsInfo> storeGoodsInfoList){
		
		for (StoreGoodsInfo entity : storeGoodsInfoList) {
			// 将旧信息置无效
			dao.deleteByRfidAndBoxNo(entity);
			super.save(entity);
			// 保存明细
			for (StoreGoodsDetail detailEntity : entity.getStoreGoodsDetailList()) {
				detailEntity.preInsert();
				detailEntity.setStoreGoodsId(entity.getId());
				storeGoodsDetailDao.insert(detailEntity);
			}
			// 更新箱袋状态
			StoBoxInfo boxInfo = new StoBoxInfo();
			boxInfo.setUpdateBy(entity.getUpdateBy());
			boxInfo.setUpdateDate(entity.getUpdateDate());
			boxInfo.setBoxStatus(AllocationConstant.Place.StoreRoom);
			boxInfo.setId(entity.getBoxNo());
			boxInfo.setRfid(entity.getRfid());
			StoreCommonUtils.updateBoxStatus(boxInfo);
		}
	}
}
