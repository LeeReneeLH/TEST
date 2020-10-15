package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.PaidRecordDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.PaidRecord;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 汇款记录保存Service
 * @author WQJ
 * @version 2019-08-14
 */
@Service
@Transactional(readOnly = true)
public class PaidRecordService extends CrudService<PaidRecordDao, PaidRecord> {

	public PaidRecord get(String id) {
		return super.get(id);
	}
	
	public List<PaidRecord> findList(PaidRecord paidRecord) {
		return super.findList(paidRecord);
	}
	
	public Page<PaidRecord> findPage(Page<PaidRecord> page, PaidRecord paidRecord) {
		return super.findPage(page, paidRecord);
	}
	
	@Transactional(readOnly = false)
	public void save(PaidRecord paidRecord) {
		paidRecord.setRecordOfficeId(UserUtils.getUser().getOffice().getId());
		paidRecord.setRecordOfficeName(UserUtils.getUser().getName());
		super.save(paidRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(PaidRecord paidRecord) {
		super.delete(paidRecord);
	}
	
}