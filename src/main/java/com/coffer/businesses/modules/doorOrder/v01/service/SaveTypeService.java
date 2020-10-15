package com.coffer.businesses.modules.doorOrder.v01.service;

import org.springframework.stereotype.Service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 款项类型Service
 * 
 * @author zhaohaoran
 * @version 2019-07-15
 */
@Service
public class SaveTypeService extends CrudService<SaveTypeDao, SaveType> {
	/**
	 * 查询分页数据
	 * 
	 * @param page
	 *            分页对象 过滤商户
	 */
	public Page<SaveType> findPage(Page<SaveType> page, SaveType saveType) {
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())) {
			saveType.getSqlMap().put("dsf",
					"AND o1.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getParentId() + "%' AND o1.type='9'");
		} else {
			saveType.getSqlMap().put("dsf", dataScopeFilter(saveType.getCurrentUser(), "o1", null));
		}
		return super.findPage(page, saveType);
	}

}
