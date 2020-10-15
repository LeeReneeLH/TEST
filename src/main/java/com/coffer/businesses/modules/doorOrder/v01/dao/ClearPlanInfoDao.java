package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机主表DAO接口
 *
 * @author XL
 * @version 2019-06-26
 */
@MyBatisDao
public interface ClearPlanInfoDao extends CrudDao<ClearPlanInfo> {

    /**
     * 修改任务状态
     *
     * @param planId
     * @param status
     * @author yinkai
     */
    void completeByPlanId(@Param(value = "planId") String planId,
                          @Param(value = "status") String status,
                          @Param(value = "currentDate") Date currentDate);

    /**
     * 获取设备未完成任务
     *
     * @author yinkai
     * @param eqpId
     * @return
     */
    List<ClearPlanInfo> getUnCompletePlanList(@Param("eqpId") String eqpId);

    /**
     * Title: insertBatch
     * <p>
     * Description: 批量插入清机任务明细
     * </p>
     *
     * @param list void 返回类型
     * @author: lihe
     */
    int batchInsert(ClearPlanInfo clearInfo);
    
    /**
     * 根据机具编号查询单条数据
     *
     * @author zhr
     * @param equipmentId
     * @return
     */
    public List<ClearPlanInfo> getByEquipmentId(@Param("equipmentId") String equipmentId);
}