package com.coffer.businesses.modules.allocation.v02.dao;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;

/**
 * 人行调拨箱袋管理DAO接口
 * @author LLF
 * @version 2016-05-25
 */
@MyBatisDao
public interface PbocAllAllocateDetailDao extends CrudDao<PbocAllAllocateDetail> {

    /** 插入箱袋信息 */
    void save(PbocAllAllocateDetail allocateDetail);
	
}