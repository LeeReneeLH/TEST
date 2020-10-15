package com.coffer.businesses.modules.cloudPlatform.v04.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.cloudPlatform.v04.dao.EyeCheckEscortInfoDao;
import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 双目识别人员信息Service
 * 
 * @author XL
 * @version 2018-12-07
 */
@Service
@Transactional(readOnly = true)
public class EyeCheckEscortInfoService extends CrudService<EyeCheckEscortInfoDao, EyeCheckEscortInfo> {

	public EyeCheckEscortInfo get(String id) {
		return super.get(id);
	}

	public List<EyeCheckEscortInfo> findList(EyeCheckEscortInfo eyeCheckEscortInfo) {
		return super.findList(eyeCheckEscortInfo);
	}

	public Page<EyeCheckEscortInfo> findPage(Page<EyeCheckEscortInfo> page, EyeCheckEscortInfo eyeCheckEscortInfo) {
		eyeCheckEscortInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o10", null));
		return super.findPage(page, eyeCheckEscortInfo);
	}

	@Transactional(readOnly = false)
	public void save(EyeCheckEscortInfo eyeCheckEscortInfo) {
		super.save(eyeCheckEscortInfo);
	}

	@Transactional(readOnly = false)
	public void delete(EyeCheckEscortInfo eyeCheckEscortInfo) {
		super.delete(eyeCheckEscortInfo);
	}

	/**
	 * 根据身份证号和归属机构获取人员信息
	 * 
	 * @author wangqingjie
	 * @version 2018-12-07
	 */
	public EyeCheckEscortInfo getEscortFromIdcardAndOfficeId(String idcardNo,String officeId) {
		return dao.getEscortFromIdcardAndOfficeId(idcardNo,officeId);
	}

}