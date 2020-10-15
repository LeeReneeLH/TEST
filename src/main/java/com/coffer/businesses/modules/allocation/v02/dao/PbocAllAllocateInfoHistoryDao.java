package com.coffer.businesses.modules.allocation.v02.dao;

import java.util.List;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人行调缴历史表DAO接口
 * 
 * @author yanbingxu
 * @version 2018-03-26
 */
@MyBatisDao
public interface PbocAllAllocateInfoHistoryDao extends CrudDao<PbocAllAllocateInfoHistoryDao> {

	/**
	 * 查询历史操作记录
	 * 
	 * @author yanbingxu
	 * @version 2018-03-26
	 * @return
	 */
	public List<PbocAllAllocateInfo> showOperateHistory(PbocAllAllocateInfo pbocAllAllocateInfo);

	/**
	 * 查询上缴业务状态
	 * 
	 * @author yanbingxu
	 * @version 2018-03-26
	 * @param PbocAllAllocateInfo
	 * @return
	 */
	public List<PbocAllAllocateInfo> findStatus(PbocAllAllocateInfo pbocAllAllocateInfo);

	/**
	 * 查询上缴过程每个阶段的人员，时间，物品等信息
	 * 
	 * @author yanbingxu
	 * @version 2018-03-26
	 * @param PbocAllAllocateInfo
	 * @return
	 */
	public PbocAllAllocateInfo findDetail(PbocAllAllocateInfo pbocAllAllocateInfo);

}
