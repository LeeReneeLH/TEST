package com.coffer.core.modules.sys.service;

import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.FaceIdSerialNumberDao;
import com.coffer.core.modules.sys.entity.FaceIdSerialNumber;

/**
 * 获取脸谱ID Service
 * 
 * @author wangbaozhong
 * @version 2017-05-31
 */
@Service
@Transactional(readOnly = true)
public class FaceIdSerialNumberService extends CrudService<FaceIdSerialNumberDao, FaceIdSerialNumber> {

	/**
	 * 按照参数查询当前用户所属机构最后生成的脸谱ID
	 * 
	 * @author WangBaozhong
	 * @version 2017年5月31日
	 * 
	 *  
	 * @param userOfficeType	用户所属机构类型
	 * @param pbocOfficeId		所属人行机构ID
	 * @return	当前用户所属机构最后生成的脸谱ID
	 */
	private FaceIdSerialNumber find(String userOfficeType, String pbocOfficeId) {
		return dao.findFaceId(userOfficeType, pbocOfficeId);
	}

	/**
	 * 取得脸谱序号：按照用户所在机构类型、所属人行 ，取得当前表中最大值+1
	 * @author WangBaozhong
	 * @version 2017年5月31日
	 * 
	 *  
	 * @param userOfficeType	用户所在机构类型
	 * @param pbocOfficeId	所属人行ID
	 * @param officeId		所属机构ID
	 * @param userId		用户ID
	 * @return	-1:脸谱ID生成区间未设定或生成脸谱机构类型与预定不符。
	 * 			-2:脸谱ID超出设定上限
	 * 			其他：脸谱序列号
	 */
//	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	@Transactional(readOnly = false)
	public synchronized long getSerialNumber(String userOfficeType, String pbocOfficeId) {
		String initId = "";
		String maxId = "";
		Long sequence = null;
		Long maxValue = null;
		if (Constant.OfficeType.CENTRAL_BANK.equals(userOfficeType)) {
			// 1：人民银行
			initId = Global.getConfig("user.faceId.pboc.init");
			maxId = Global.getConfig("user.faceId.pboc.max");
		} else if (Constant.OfficeType.COFFER.equals(userOfficeType)) {
			//3：金库
			initId = Global.getConfig("user.faceId.commercialBank.treasury.init");
			maxId = Global.getConfig("user.faceId.commercialBank.treasury.max");
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(userOfficeType)) {
			// 6：清分中心
			initId = Global.getConfig("user.faceId.pboc.clear.init");
			maxId = Global.getConfig("user.faceId.pboc.clear.max");
		} else if (Constant.OfficeType.DIGITAL_PLATFORM.equals(userOfficeType)) {
			// 7：数字化平台
			initId = Global.getConfig("user.faceId.digital.platform.init");
			maxId = Global.getConfig("user.faceId.digital.platform.max");
		} else {
			return -1;
		}
		if (StringUtils.isNotBlank(initId) && StringUtils.isNotBlank(maxId)) {
			maxValue = Long.parseLong(maxId);
			sequence = Long.parseLong(initId);
			FaceIdSerialNumber serialNumber = find(userOfficeType, pbocOfficeId);
			// 超出最大ID，不设定
			if (serialNumber != null) {
				sequence = serialNumber.getSequence() + 1;
			} else {
				serialNumber = new FaceIdSerialNumber();
				// 插入数据标识
				serialNumber.setOfficeType(userOfficeType);
				serialNumber.setPbocOfficeId(pbocOfficeId);
			}
			if (maxValue <= sequence) {
				return -2;
			}
			serialNumber.setSequence(sequence);
			save(serialNumber);
		} else {
			return -1;
		}
		logger.info("为机构类型："  + userOfficeType  + "生成脸谱序列：" + sequence + "   时间："+ DateUtils.getDateTime());
		return sequence;
	}
}