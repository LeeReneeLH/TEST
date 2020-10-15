package com.coffer.core.modules.sys.service;

import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.dao.LoginIdSerialNumberDao;
import com.coffer.core.modules.sys.entity.LoginIdSerialNumber;

/**
 * 获取登陆ID Service
 * 
 * @author wangbaozhong
 * @version 2017-05-31
 */
@Service
@Transactional(readOnly = true)
public class LoginIdSerialNumberService extends CrudService<LoginIdSerialNumberDao, LoginIdSerialNumber> {

	/**
	 * 查询指定业务序号
	 * 
	 * @param businessType
	 * @return
	 */
	private LoginIdSerialNumber find(String officeId) {
		return dao.findByOfficeId(officeId);
	}

	/**
	 * 取得登陆用户名序号：按照机构，取得当前表中最大值+1
	 * 
	 * @param businessType
	 * @return
	 */
//	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	@Transactional(readOnly = false)
	public synchronized int getSerialNumber(String officeId) {
		int sequence = 1;
		LoginIdSerialNumber serialNumber = find(officeId);
		if (serialNumber != null) {
			sequence = serialNumber.getSequence() + 1;
		} else {
			serialNumber = new LoginIdSerialNumber();
			// 插入数据标识
			serialNumber.setOfficeId(officeId);
		}
		serialNumber.setSequence(sequence);
		save(serialNumber);
		logger.info("为："  + officeId  + "生成登陆用户名序列：" + sequence + "   时间："+ DateUtils.getDateTime());
		return sequence;
	}
}