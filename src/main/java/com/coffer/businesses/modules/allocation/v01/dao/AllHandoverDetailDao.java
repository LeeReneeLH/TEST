package com.coffer.businesses.modules.allocation.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface AllHandoverDetailDao extends CrudDao<AllHandoverDetail> {

	/**
	 * @author LLF
	 * @param handoverId
	 *            根据交接ID获取交接人员信息
	 * @return
	 */
	public List<AllHandoverDetail> findListByHandoverId(String handoverId);
	
}
