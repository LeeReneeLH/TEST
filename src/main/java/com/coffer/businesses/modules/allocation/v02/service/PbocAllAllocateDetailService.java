package com.coffer.businesses.modules.allocation.v02.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateDetailDao;

/**
 * 人行调拨箱袋管理Service
 * @author LLF
 * @version 2016-05-25
 */
@Service
@Transactional(readOnly = true)
public class PbocAllAllocateDetailService extends CrudService<PbocAllAllocateDetailDao, PbocAllAllocateDetail> {

	public PbocAllAllocateDetail get(String id) {
		return super.get(id);
	}
	
	public List<PbocAllAllocateDetail> findList(PbocAllAllocateDetail pbocAllAllocateDetail) {
		return super.findList(pbocAllAllocateDetail);
	}
	
	public Page<PbocAllAllocateDetail> findPage(Page<PbocAllAllocateDetail> page, PbocAllAllocateDetail pbocAllAllocateDetail) {
		return super.findPage(page, pbocAllAllocateDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(PbocAllAllocateDetail pbocAllAllocateDetail) {
		super.save(pbocAllAllocateDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(PbocAllAllocateDetail pbocAllAllocateDetail) {
		super.delete(pbocAllAllocateDetail);
	}
	
}