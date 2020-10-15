package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;



import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 临时线路调缴功能DAO接口
 * 
 * @author qipeihong
 * @version 2017-08-08
 */
@MyBatisDao
public interface TempAllAllocateInfoDao extends CrudDao<AllAllocateInfo> {
	
	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       页面分页用查询
	 */
	public List<String> findPageList(AllAllocateInfo allocateInfo);
	
	/**
	 * 查询库间调拨信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月7日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询条件
	 * @return 查询库间调拨信息列表
	 */
	public List<AllAllocateInfo> findBetweenPageList(AllAllocateInfo allocateInfo);
	
	/**
	 * 查询库外上缴信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月29日
	 * 
	 * 
	 * @param allocateInfo
	 *            查询条件
	 * @return 库外上缴信息列表
	 */
	public List<AllAllocateInfo> findHandinPageList(AllAllocateInfo allocateInfo);
	
	/**
	 * 根据流水号查询库间调拨信息列表
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月8日
	 * 
	 * 
	 * @param allId
	 *            查询条件 流水号
	 * @return 查询库间调拨信息列表
	 */
	public AllAllocateInfo findAllocateInfoByAllId(String allId);
	
	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       调拨信息查询
	 */
	public List<AllAllocateInfo> findAllocationList(AllAllocateInfo allocateInfo);
	
	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       库间现金调拨合计
	 */
	public List<AllAllocateInfo> countCashBetween(AllAllocateInfo allocateInfo);
	
	/**
	 * @author chengshu
	 * @date 2015-05-14
	 * 
	 *       更新调拨状态
	 */
	public int updateStatus(AllAllocateInfo allocateInfo);
	


	
	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月11日
	 * 
	 *          更新配款信息
	 * @param allocateInfo
	 * @return
	 */
	public int updateAllocateInfoStatus(AllAllocateInfo allocateInfo);
	
	/**
	 * @author qipeihong
	 * @date 2017-08-10
	 * 
	 *       调拨信息查询
	 */
	public List<AllAllocateInfo> findAllocationAndTempList(AllAllocateInfo allocateInfo);
	
	/**
	 * 
	 * @author chengshu
	 * @version 2015年10月23日
	 * 
	 *          取得个数（总个数，尾箱个数，款箱个数）
	 * @param allAllocateInfo
	 *            查询条件
	 * @return 调拨箱子列表
	 */

	public AllAllocateInfo getBoxCount(AllAllocateInfo allAllocateInfo);


	

	


	


}