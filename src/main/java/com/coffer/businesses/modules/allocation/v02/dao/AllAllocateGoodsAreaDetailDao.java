/**
 * @author WangBaozhong
 * @version 2016年5月23日
 * 
 * 
 */
package com.coffer.businesses.modules.allocation.v02.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调拨物品与库区位置关联表DAO
 * @author WangBaozhong
 *
 */
@MyBatisDao
public interface AllAllocateGoodsAreaDetailDao extends CrudDao<AllAllocateGoodsAreaDetail> {

	/**
	 * 根据调拨流水单号删除关联表信息
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 *  
	 * @param allId 调拨流水单号
	 * @return	删除数量
	 */
	public int deleteByAllId(@Param(value="allId") String allId);
	
	/**
	 * 根据流水单号和登记类型 查询打印配款物品位置信息列表
	 * @author WangBaozhong
	 * @version 2016年5月24日
	 * 
	 *  
	 * @param allId 流水单号
	 * @param registType 登记类型
	 * @return 打印配款物品位置信息列表
	 */
	public PbocAllAllocateInfo getPrintQuotaDataByAllId(@Param(value="allId") String allId, @Param(value="registType") String registType);
	
    /**
     * 查询所有应出库物品箱袋信息
     * 
     * @author chengshu
     * @version 2016年5月23日
     * 
     * @param allId 调拨流水单号
     * @return 删除数量
     */
    public List<AllAllocateGoodsAreaDetail> findIsNecessaryOut(@Param(value="allId") String allId);
}
