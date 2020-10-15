package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;

/**
 * 清机主表Service
 *
 * @author XL
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class ClearPlanInfoService extends CrudService<ClearPlanInfoDao, ClearPlanInfo> {

	public ClearPlanInfo get(String id) {
		return super.get(id);
	}

	public List<ClearPlanInfo> findList(ClearPlanInfo clearPlanInfo) {
		return super.findList(clearPlanInfo);
	}

	public Page<ClearPlanInfo> findPage(Page<ClearPlanInfo> page, ClearPlanInfo clearPlanInfo) {
		return super.findPage(page, clearPlanInfo);
	}

	@Transactional(readOnly = false)
	public void save(ClearPlanInfo clearPlanInfo) {
		super.save(clearPlanInfo);
	}

	@Transactional(readOnly = false)
	public void delete(ClearPlanInfo clearPlanInfo) {
		super.delete(clearPlanInfo);
	}

	/**
	 * 查询中心总账列表
	 *
	 * @author lihe
	 * @version 2019年7月8日
	 * @param clearPlanInfo
	 */
	public List<ClearPlanInfo> findClearList(ClearPlanInfo clearPlanInfo) {
		// 查询条件： 开始时间
		if (clearPlanInfo.getCreateTimeStart() != null) {
			clearPlanInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearPlanInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearPlanInfo.getCreateTimeEnd() != null) {
			clearPlanInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearPlanInfo.getCreateTimeEnd())));
		}
		clearPlanInfo.getSqlMap().put("dsf", dataScopeFilter(clearPlanInfo.getCurrentUser(), "o", null));
		return dao.findList(clearPlanInfo);
	}

	/**
	 * 清机任务列表
	 *
	 * @author lihe
	 * @version 2019年7月8日
	 * @param page
	 * @param centerAccountsMain
	 */
	public Page<ClearPlanInfo> findClearPage(Page<ClearPlanInfo> page, ClearPlanInfo clearPlanInfo) {
		clearPlanInfo.setPage(page);
		page.setList(findClearList(clearPlanInfo));
		return page;
	}

	/**
	 *
	 * Title: batchInsert
	 * <p>
	 * Description: 清机任务明细批量插入
	 * </p>
	 *
	 * @author: lihe
	 * @param clearPlanDetail
	 *            void 返回类型
	 */
	@Transactional(readOnly = false)
	public void batchInsert(ClearPlanInfo clearInfo) {
		dao.batchInsert(clearInfo);
	}


    /**
     * 根据机具编号查询单条数据
     *
     * @author zhr
     * @param equipmentId
     * @return
     */
	public List<ClearPlanInfo> getByEquipmentId(String equipmentId) {
		return dao.getByEquipmentId(equipmentId);
	}

    /**
     * 撤销设备的清机任务，门店预约冲正时调用此方法
     * @author yinkai
     * @param eqpId 设备ID
     */
	@Transactional(readOnly = false)
    public void reversePlan(String eqpId) {
	    // 设备未完成任务，一个设备未完成只有一个
        List<ClearPlanInfo> unCompletePlanList = dao.getUnCompletePlanList(eqpId);
        if (Collections3.isEmpty(unCompletePlanList)) {
            throw new BusinessException();
        }
        if (unCompletePlanList.size() > 1) {
            throw new BusinessException();
        }
        ClearPlanInfo unCompletePlanInfo = unCompletePlanList.get(0);
        // 任务状态改成撤销
        unCompletePlanInfo.setStatus(Constant.ClearPlanStatus.REVERSE);
        // 更新到数据库
        dao.update(unCompletePlanInfo);
    }
}