package com.coffer.businesses.modules.allocation.v02.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;

/**
 * 人行调拨物品管理DAO接口
 * @author LLF
 * @version 2016-05-25
 */
@MyBatisDao
public interface PbocAllAllocateItemsDao extends CrudDao<PbocAllAllocateItem> {
	/**
	 * 根据流水单号删除物品明细
	 * @author WangBaozhong
	 * @version 2016年5月31日
	 * 
	 *  
	 * @param allId 流水单号
	 * @return 删除数据条数
	 */
	public int deleteBYAllId(@Param(value="allId") String allId);
	
	/**
	 * 按流水号，登记种别 查询物品列表
	 * @author wangbaozhong
	 * @version 2015年9月29日
	 * 
	 *  
	 * @param allAllocateItem
	 * @return 物品列表
	 */
	public List<PbocAllAllocateItem> findItemsList(PbocAllAllocateItem allAllocateItem);
	
	/**
	 * 根据流水单号和登记类型删除物品明细
	 * @author WangBaozhong
	 * @version 2016年8月31日
	 * 
	 *  
	 * @param allId 流水单号
	 * @param registType 登记类型
	 * @return 删除数据条数
	 */
	public int deleteBYAllIdAndRegistType(@Param(value="allId") String allId, @Param(value="registType") String registType);
}