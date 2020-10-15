package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 清机明细DAO接口
 *
 * @author XL
 * @version 2019-06-26
 */
@MyBatisDao
public interface ClearPlanDetailDao extends CrudDao<ClearPlanDetail> {

    /**
     * 查询未完成清机任务
     *
     * @param doorId 非必填 门店ID
     * @param planId 非必填 清机任务号
     * @return
     * @author yinkai
     */
    List<ClearPlanDetail> unCompleteList(@Param(value = "doorId") String doorId, @Param(value = "planId") String planId);
}