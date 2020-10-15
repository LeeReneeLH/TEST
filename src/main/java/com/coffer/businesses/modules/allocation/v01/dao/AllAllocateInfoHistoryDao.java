package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调缴历史表DAO接口
 * 
 * @author yanbingxu
 * @version 2017-09-06
 */
@MyBatisDao
public interface AllAllocateInfoHistoryDao extends CrudDao<AllAllocateInfoHistoryDao> {

	/**
	 * 查询历史操作记录
	 * 
	 * @author yanbingxu
	 * @version 2017-09-06
	 * @return
	 */
	public List<AllAllocateInfo> showOperateHistory(AllAllocateInfo allAllocateInfo);

	/**
	 * 查询上缴业务状态
	 * 
	 * @author xp
	 * @version 2017年9月7日
	 * @param allAllocateInfo
	 * @return
	 */
	public List<AllAllocateInfo> findStatus(AllAllocateInfo allAllocateInfo);

	/**
	 * 查询上缴过程每个阶段的人员，时间，物品等信息
	 * 
	 * @author xp
	 * @version 2017年9月8日
	 * @param allAllocateInfo
	 * @return
	 */
	public AllAllocateInfo findDetail(AllAllocateInfo allAllocateInfo);

}
