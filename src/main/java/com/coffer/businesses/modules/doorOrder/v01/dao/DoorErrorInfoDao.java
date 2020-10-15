package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;

/**
 * 差错管理DAO接口
 * @author XL
 * @version 2019-06-28
 */
@MyBatisDao
public interface DoorErrorInfoDao extends CrudDao<DoorErrorInfo> {
	
	/**
	 * 
	 * 门店差错列表
	 * 
	 * @author gzd
	 * @version 2020年3月5日
	 * @param doorErrorInfo
	 * @return
	 */
	public List<DoorErrorInfo> findDoorList(DoorErrorInfo doorErrorInfo);
	
	/**
	 * 
	 * 门店差错明细
	 * 
	 * @author gzd
	 * @version 2020年3月6日
	 * @param doorErrorInfo
	 * @return
	 */
	public List<DoorErrorInfo> findDoorDetailList(DoorErrorInfo doorErrorInfo);
	
	/**
	 * 
	 * 门店差错明细合计
	 * 
	 * @author gzd
	 * @version 2020年6月2日
	 * @param doorErrorInfo
	 * @return
	 */
	public List<DoorErrorInfo> findDoorDetailListPool(DoorErrorInfo doorErrorInfo);
	
	/**
	 * 
	 * 商户差错明细
	 * 
	 * @author ZXK
	 * @version 2020年3月6日
	 * @param doorErrorInfo
	 * @return
	 */
	public List<DoorErrorInfo> findMerchantList(DoorErrorInfo doorErrorInfo);
}