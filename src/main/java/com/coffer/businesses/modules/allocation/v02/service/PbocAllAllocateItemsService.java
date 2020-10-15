package com.coffer.businesses.modules.allocation.v02.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateItemsDao;

/**
 * 人行调拨物品管理Service
 * @author LLF
 * @version 2016-05-25
 */
@Service
@Transactional(readOnly = true)
public class PbocAllAllocateItemsService extends CrudService<PbocAllAllocateItemsDao, PbocAllAllocateItem> {

	public PbocAllAllocateItem get(String id) {
		return super.get(id);
	}
	
	public List<PbocAllAllocateItem> findList(PbocAllAllocateItem pbocAllAllocateItems) {
		return super.findList(pbocAllAllocateItems);
	}
	
	public Page<PbocAllAllocateItem> findPage(Page<PbocAllAllocateItem> page, PbocAllAllocateItem pbocAllAllocateItems) {
		return super.findPage(page, pbocAllAllocateItems);
	}
	
	@Transactional(readOnly = false)
	public void save(PbocAllAllocateItem pbocAllAllocateItems) {
		super.save(pbocAllAllocateItems);
	}
	
	@Transactional(readOnly = false)
	public void delete(PbocAllAllocateItem pbocAllAllocateItems) {
		super.delete(pbocAllAllocateItems);
	}
	
}