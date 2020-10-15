package com.coffer.core.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.dao.SysOtpOfficeDao;
import com.coffer.core.modules.sys.entity.SysOtpOffice;

/**
 * 令牌机构管理Service
 * @author XL
 * @version 2018-10-26
 */
@Service
@Transactional(readOnly = true)
public class SysOtpOfficeService extends CrudService<SysOtpOfficeDao, SysOtpOffice> {

	public SysOtpOffice get(String id) {
		return super.get(id);
	}
	
	public List<SysOtpOffice> findList(SysOtpOffice sysOtpOffice) {
		return super.findList(sysOtpOffice);
	}
	
	public Page<SysOtpOffice> findPage(Page<SysOtpOffice> page, SysOtpOffice sysOtpOffice) {
		return super.findPage(page, sysOtpOffice);
	}
	
	@Transactional(readOnly = false)
	public void save(SysOtpOffice sysOtpOffice) {
		super.save(sysOtpOffice);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysOtpOffice sysOtpOffice) {
		super.delete(sysOtpOffice);
	}
	
	/**
	 * 关闭所有机构令牌功能
	 * 
	 * @author XL
	 * @version 2018-10-29
	 */
	@Transactional(readOnly = false)
	public void deleteAll() {
		dao.deleteAll();
	}
}