package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.NowUsingInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.NowUsingInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

@Service
public class NowUsingInfoService extends CrudService<NowUsingInfoDao, NowUsingInfo> {

	@Autowired
	NowUsingInfoDao nowUsingInfoDao;

	public Page<NowUsingInfo> findPage(Page<NowUsingInfo> page, NowUsingInfo nowUsingInfo) {
		nowUsingInfo.setPage(page);
		page.setList(findList(nowUsingInfo));
		return page;
	}

	public List<NowUsingInfo> findList(NowUsingInfo nowUsingInfo) {
		if (null != UserUtils.getUser()) {
			Office office = UserUtils.getUser().getOffice();
			if (StringUtils.isBlank(office.getType())) {
				throw new BusinessException("message.E5004", "", new String[] {});
			}
			switch (office.getType()) {
//			case Constant.OfficeType.CLEAR_CENTER:
//				nowUsingInfo.getSqlMap().put("dsf", "s3.CLEAR_CENTER_ID = " + UserUtils.getUser().getOffice().getId());
//				break;
//			case Constant.OfficeType.STORE:
//				nowUsingInfo.getSqlMap().put("dsf", "s3.DOOR_ID = " + UserUtils.getUser().getOffice().getId());
//				break;
			case Constant.OfficeType.CLEAR_CENTER:
				nowUsingInfo.getSqlMap().put("dsf", "cam.CLEAR_CENTER_ID = " + UserUtils.getUser().getOffice().getId());
				break;
			case Constant.OfficeType.STORE:
				nowUsingInfo.getSqlMap().put("dsf", "cam.DOOR_ID = " + UserUtils.getUser().getOffice().getId());
				break;
			default:
				nowUsingInfo.getSqlMap().put("dsf",
						"(so.parent_ids LIKE '%" + office.getId() + "%' OR so.ID = " + office.getId() + ")");
			}
		}
		return nowUsingInfoDao.findList(nowUsingInfo);
	}
}
