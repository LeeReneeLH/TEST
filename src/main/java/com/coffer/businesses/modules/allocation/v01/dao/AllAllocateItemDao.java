package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AtmClearBoxInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调拨物品表DAO接口
 * @author wangbaozhong
 * @version 2015-09-07
 */
@MyBatisDao
public interface AllAllocateItemDao extends CrudDao<AllAllocateItem>{
	/**
	 * 调拨物品表
	 * @author wangbaozhong
	 * @version 2015年9月9日
	 * 
	 *  
	 * @param allAllocateItem
	 * @return
	 */
	public List<AllAllocateItem> findItemsListByAllId(AllAllocateItem allAllocateItem);
	
	/**
	 * 按流水号，登记种别 查询物品列表
	 * @author wangbaozhong
	 * @version 2015年9月29日
	 * 
	 *  
	 * @param allAllocateItem
	 * @return 物品列表
	 */
	public List<AllAllocateItem> findItemsList(AllAllocateItem allAllocateItem);
	
	/**
	 * 修改del_flag='1'
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月9日
	 * 
	 *  
	 * @param allAllocateItem
	 * @return 删除数量
	 */
	public int deleteDetail(AllAllocateItem allAllocateItem);
	
	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月11日
	 * 
	 *  删除预约明细
	 * @param allAllocateItem
	 * @return
	 */
	public int delOrderDetail(AllAllocateItem allAllocateItem);
	/**
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月18日
	 * 
	 *  删除已确认物品明细
	 * @param allAllocateItem
	 * @return
	 */
	public int deleteByConfirmed(AllAllocateItem allAllocateItem);
	
	/**
	 * 根据加钞计划批次号查询ATM清机钞箱信息
	 * 
	 * @author xp
	 * @param planId
	 * @return list
	 */
	public List<AtmClearBoxInfo> getAtmClearBoxInfoByPlanId(AllAllocateItem allAllocateItem);
}
