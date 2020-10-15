package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调缴功能DAO接口
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
@MyBatisDao
public interface AllAllocateDetailDao extends CrudDao<AllAllocateDetail> {

	/**
	 * @author chengshu
	 * @date 2015-06-05
	 * 
	 *       根据调拨详细ID，删除调拨详细记录
	 */
	public int deleteDetail(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 
	 *          更新库间调拨状态
	 * @param allocateDetail
	 * @return
	 */
	public int updateAllocateDetailStatus(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月16日
	 * 
	 *          根据箱袋编号更新状态
	 * @param allocateDetail
	 *            更新条件
	 * @return 更新数量
	 */
	public int updateDetailStatusByBoxNo(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月15日
	 * 
	 *          根据allId查询详细信息
	 * @param allId
	 * @return
	 */
	public AllAllocateDetail getByAllId(String allId);

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月20日
	 * 
	 *          根据箱袋编号更新箱袋状态
	 * @param allocateDetail
	 *            更新条件
	 * @return 更新数量
	 */
	public int updateDetailPlaceByBoxNo(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月29日
	 * 
	 *          根据allId删除信息
	 * @param allocateDetail
	 * @return
	 */
	public int deleteDetailByAllId(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月15日
	 * 
	 *          根据allId查询详细信息
	 * @param allId
	 * @return
	 */
	public List<AllAllocateDetail> getBoxNoByAllId(AllAllocateInfo allocateInfo);

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          插入补录箱子明细
	 * @param
	 * @return
	 */
	public int insertAdditional(AllAllocateDetail allocateDetail);

	/**
	 * 通过rfid更新对应调拨详细表信息（网点交接使用）
	 * 
	 * @author XP
	 * @param allocateDetail
	 * @return
	 */
	public int updateDetailByRfid(AllAllocateDetail allocateDetail);

	/**
	 * 通过rfid或boxNo更新对应详细表信息（ATM库外清分入库确认接口用）
	 * 
	 * @author sg
	 * @param allocateDetail
	 * @return
	 */
	public int updateDetailByBoxNoorRfid(AllAllocateDetail allocateDetail);

	/**
	 * 
	 * @author sg
	 * @version 2017年11月23日
	 * 
	 *          根据allId查询所有非未扫描的信息
	 * @param allId
	 * @return
	 */
	public List<AllAllocateDetail> getByAllIdscanFlag(String allId);
	
	/**
	 * 
	 * @author WQJ
	 * @version 2019年1月9日
	 * 
	 *          根据箱袋编号查询调拨详细信息
	 * @param allocateDetail
	 * @return
	 */
	public List<AllAllocateDetail> findAllocateDetailByNo(AllAllocateDetail allocateDetail);

}