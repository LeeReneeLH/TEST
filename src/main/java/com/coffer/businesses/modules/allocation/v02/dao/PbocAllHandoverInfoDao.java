package com.coffer.businesses.modules.allocation.v02.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人行交接管理DAO接口
 * @author chengshu
 * @version 2016-05-25
 */
@MyBatisDao
public interface PbocAllHandoverInfoDao extends CrudDao<PbocAllHandoverInfo> {
    /**
     * 插入交接信息
     * @author chengshu
     * @version 2016年6月03日
     *  
     * @param handoverInfo 交接信息
     */
    public void insertHandoverInfo(PbocAllHandoverInfo handoverInfo);
    
    /**
     * 根据调拨流水号更新交接信息
     * @author chengshu
     * @version 2016年6月03日
     *  
     * @param handoverInfo 交接信息
     */
    public int udateHandoverInfoByAllId(PbocAllHandoverInfo handoverInfo);
    
    /**
     * 根据流水号取得交接信息
     * @author chengshu
     * @version 2016年6月06日
     *  
     * @param allId 流水号
     */
    public PbocAllHandoverInfo getByAllId(@Param(value="allId") String allId);
}