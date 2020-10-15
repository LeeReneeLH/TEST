package com.coffer.businesses.modules.common.service;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.common.dao.SerialNumberDao;
import com.coffer.businesses.modules.common.entity.SerialNumber;
import com.coffer.core.common.service.CrudService;

/**
 * 业务流水号Service
 * 
 * @author Clark
 * @version 2015-04-28
 */
@Service
@Transactional(readOnly = true)
public class SerialNumberService extends CrudService<SerialNumberDao, SerialNumber> {

	/**
	 * 查询指定业务序号
	 * 
	 * @param businessType
	 * @return
	 */
	private SerialNumber find(String businessType) {
		return dao.findByBusinessType(DateFormatUtils.format(new Date(), "yyyyMMdd"), businessType);
	}

	/**
	 * 取得业务序号：当前表中最大值+1
	 * 
	 * @param businessType
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public synchronized int getSerialNumber(String businessType) {
		int sequence = 1;
		SerialNumber serialNumber = find(businessType);
		if (serialNumber != null) {
			sequence = serialNumber.getSequence() + 1;
		} else {
			serialNumber = new SerialNumber();
			// 插入数据标示
			serialNumber.setSequenceDate(DateFormatUtils.format(new Date(), "yyyyMMdd"));
			serialNumber.setBusinessType(businessType);
		}
		serialNumber.setSequence(sequence);
		save(serialNumber);

		return sequence;
	}
}