package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanUserDetailDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanUserDetail;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;

/**
 * 清机任务人员明细Service
 * 
 * @author XL
 * @version 2019-08-12
 */
@Service
@Transactional(readOnly = true)
public class ClearPlanUserDetailService extends CrudService<ClearPlanUserDetailDao, ClearPlanUserDetail> {

	public ClearPlanUserDetail get(String id) {
		return super.get(id);
	}

	public List<ClearPlanUserDetail> findList(ClearPlanUserDetail clearPlanUserDetail) {
		return super.findList(clearPlanUserDetail);
	}

	public Page<ClearPlanUserDetail> findPage(Page<ClearPlanUserDetail> page, ClearPlanUserDetail clearPlanUserDetail) {
		return super.findPage(page, clearPlanUserDetail);
	}

	@Transactional(readOnly = false)
	public void save(ClearPlanUserDetail clearPlanUserDetail) {
		super.save(clearPlanUserDetail);
	}

	@Transactional(readOnly = false)
	public void delete(ClearPlanUserDetail clearPlanUserDetail) {
		super.delete(clearPlanUserDetail);
	}
	
	/**
	 * 根据清机计划编号删除人员明细
	 *
	 * @author XL 
	 * @version 2019年8月13日 
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteByPlanId(String planId) {
		dao.deleteByPlanId(planId);
	}

}