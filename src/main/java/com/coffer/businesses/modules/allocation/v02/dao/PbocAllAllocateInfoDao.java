package com.coffer.businesses.modules.allocation.v02.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人行调拨主表管理DAO接口
 * @author LLF
 * @version 2016-05-25
 */
@MyBatisDao
public interface PbocAllAllocateInfoDao extends CrudDao<PbocAllAllocateInfo> {
	/**
	 * 根据流水单号更新业务状态
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 更新信息
	 * @return 更新数量
	 */
	public int updateStatusByAllId(PbocAllAllocateInfo pbocAllAllocateInfo);
    /** 更新设置内容的项目 */
    public int updateSelective(PbocAllAllocateInfo allocationInfo);
    
    /**
     * 
     * @author LLF
     * @version 2016年6月2日
     * 
     *  接口任务查询
     * @param pbocAllAllocateInfo
     * @return
     */
    public List<PbocAllAllocateInfo> findListInterface(PbocAllAllocateInfo pbocAllAllocateInfo);

	/**
	 * 用于排他验证
	 * 
	 * @param pbocAllAllocateInfo
	 * @return
	 */
	public PbocAllAllocateInfo getByIdVersion(PbocAllAllocateInfo pbocAllAllocateInfo);
	
	/**
	 * 更新 调拨出库后入库标记
	 * @author WangBaozhong
	 * @version 2017年4月21日
	 * 
	 *  
	 * @param allId	流水单号
	 * @param allocateInAfterOutFlag	调拨出库后入库标记
	 * @return	修改次数
	 */
	public int updateAllocateInAfterOutFlag(@Param(value = "allId") String allId, 
			@Param(value = "allocateInAfterOutFlag") String allocateInAfterOutFlag);
	
}