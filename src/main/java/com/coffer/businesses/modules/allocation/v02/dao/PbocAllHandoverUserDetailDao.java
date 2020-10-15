package com.coffer.businesses.modules.allocation.v02.dao;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人行交接管理DAO接口
 * @author chengshu
 * @version 2016-05-25
 */
@MyBatisDao
public interface PbocAllHandoverUserDetailDao extends CrudDao<PbocAllHandoverUserDetail> {
    /**
     * 插入交接信息
     * @author chengshu
     * @version 2016年6月03日
     *  
     * @param handoverInfo 交接信息
     */
    public void insertHandoverUserDetail(PbocAllHandoverUserDetail handoverUserDetail);
}