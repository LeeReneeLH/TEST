package com.coffer.businesses.modules.cloudPlatform.v04.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.cloudPlatform.v04.dao.EyeCheckVisitorInfoDao;
import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckVisitorInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 双目识别访客信息Service
 * 
 * @author XL
 * @version 2018-12-07
 */
@Service
@Transactional(readOnly = true)
public class EyeCheckVisitorInfoService extends CrudService<EyeCheckVisitorInfoDao, EyeCheckVisitorInfo> {

	public EyeCheckVisitorInfo get(String id) {
		return super.get(id);
	}

	public List<EyeCheckVisitorInfo> findList(EyeCheckVisitorInfo eyeCheckVisitorInfo) {
		return super.findList(eyeCheckVisitorInfo);
	}

	public Page<EyeCheckVisitorInfo> findPage(Page<EyeCheckVisitorInfo> page, EyeCheckVisitorInfo eyeCheckVisitorInfo) {
		// 查询条件： 开始时间
		if (eyeCheckVisitorInfo.getCreateTimeStart() != null) {
			eyeCheckVisitorInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(eyeCheckVisitorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (eyeCheckVisitorInfo.getCreateTimeEnd() != null) {
			eyeCheckVisitorInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(eyeCheckVisitorInfo.getCreateTimeEnd())));
		}
		eyeCheckVisitorInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o6", null));
		return super.findPage(page, eyeCheckVisitorInfo);
	}

	@Transactional(readOnly = false)
	public void save(EyeCheckVisitorInfo eyeCheckVisitorInfo) {
		super.save(eyeCheckVisitorInfo);
	}

	@Transactional(readOnly = false)
	public void delete(EyeCheckVisitorInfo eyeCheckVisitorInfo) {
		super.delete(eyeCheckVisitorInfo);
	}

}