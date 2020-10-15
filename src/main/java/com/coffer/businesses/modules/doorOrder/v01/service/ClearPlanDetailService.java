package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanDetailDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanDetail;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;

/**
 * 清机明细Service
 * 
 * @author XL
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class ClearPlanDetailService extends CrudService<ClearPlanDetailDao, ClearPlanDetail> {

	public ClearPlanDetail get(String id) {
		return super.get(id);
	}

	public List<ClearPlanDetail> findList(ClearPlanDetail clearPlanDetail) {
		return super.findList(clearPlanDetail);
	}

	public Page<ClearPlanDetail> findPage(Page<ClearPlanDetail> page, ClearPlanDetail clearPlanDetail) {
		return super.findPage(page, clearPlanDetail);
	}

	@Transactional(readOnly = false)
	public void save(ClearPlanDetail clearPlanDetail) {
		super.save(clearPlanDetail);
	}

	@Transactional(readOnly = false)
	public void delete(ClearPlanDetail clearPlanDetail) {
		super.delete(clearPlanDetail);
	}

}