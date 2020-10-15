package com.coffer.businesses.modules.store.v02.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v02.dao.StoOriginalBanknoteDao;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 原封新券管理Service
 * @author LLF
 * @version 2016-05-30
 */
@Service
@Transactional(readOnly = true)
public class StoOriginalBanknoteService extends CrudService<StoOriginalBanknoteDao, StoOriginalBanknote> {
	@Transactional(readOnly = true)
	public StoOriginalBanknote get(String id) {
		return super.get(id);
	}
	@Transactional(readOnly = false)
	public List<StoOriginalBanknote> findList(StoOriginalBanknote stoOriginalBanknote) {
		return super.findList(stoOriginalBanknote);
	}
	@Transactional(readOnly = true)
	public Page<StoOriginalBanknote> findPage(Page<StoOriginalBanknote> page, StoOriginalBanknote stoOriginalBanknote) {
		return super.findPage(page, stoOriginalBanknote);
	}
	
	/**
	 * 
	 * @author LLf
	 * @version 2016年5月31日
	 * 
	 *  出库列表查询
	 * @param page
	 * @param stoOriginalBanknote
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<StoOriginalBanknote> findOutPage(Page<StoOriginalBanknote> page, StoOriginalBanknote stoOriginalBanknote) {
		stoOriginalBanknote.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o", null));
		stoOriginalBanknote.setPage(page);
		page.setList(dao.findOutList(stoOriginalBanknote));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(StoOriginalBanknote stoOriginalBanknote) {
		dao.insert(stoOriginalBanknote);
	}
	
	@Transactional(readOnly = false)
	public int update(StoOriginalBanknote stoOriginalBanknote) {
		return dao.update(stoOriginalBanknote);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoOriginalBanknote stoOriginalBanknote) {
		super.delete(stoOriginalBanknote);
	}
	/**
	 * 
	 * Title: getOriginalBanknoteById
	 * <p>Description: 根据原封新券箱号及所属机构查询原封新券信息</p>
	 * @author:     wangbaozhong
	 * @param id
	 * @param officeId
	 * @return 
	 * StoOriginalBanknote    返回类型
	 */
	@Transactional(readOnly = true)
	public StoOriginalBanknote getOriginalBanknoteById(String id, String officeId) {
		return dao.getOriginalBanknoteById(id, officeId);
	}
	
}