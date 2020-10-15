package com.coffer.businesses.modules.store.v01.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.dao.StoAddCashGroupDao;
import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.CrudService;

/**
 * 加钞组管理Service
 * 
 * @author wanlgu
 * @version 2017-11-10
 */
@Service
@Transactional(readOnly = true)
public class StoAddCashGroupService extends CrudService<StoAddCashGroupDao, StoAddCashGroup> {

	@Autowired
	private StoAddCashGroupDao stoAddCashGroupDao;

	/**
	 * 
	 * @author wanglu
	 * @version 2017-11-10
	 * 
	 * 
	 * @param StoAddCashGroup
	 * @return
	 */
	public List<StoAddCashGroup> getStoAddCashGroupList(StoAddCashGroup stoAddCashGroup) {
		return stoAddCashGroupDao.getStoAddCashGroupList(stoAddCashGroup, Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * @author wanglu
	 * @version 2017-11-10
	 * 
	 * 
	 * @param StoAddCashGroup
	 * @return
	 */
	public StoAddCashGroup getSingleStoAddCashGroupInfo(StoAddCashGroup stoAddCashGroup) {
		return stoAddCashGroupDao.getSingleStoAddCashGroupInfo(stoAddCashGroup);
	}

	/**
	 * 
	 * @author sg
	 * @version 2017-11-17
	 * 
	 * 
	 * @param StoAddCashGroup
	 * @return
	 */
	public StoAddCashGroup getSingleStoAddCashGroupInfoNoDel(StoAddCashGroup stoAddCashGroup) {
		return stoAddCashGroupDao.getSingleStoAddCashGroupInfoNoDel(stoAddCashGroup);
	}

	/**
	 * @author wanglu
	 * @date 2017-11-13
	 *
	 *       获取押运车辆信息，排除已分配押运人的押运车辆
	 */
	public List<StoCarInfo> getUnSetCarInfo(Map<String, Object> parameter) {
		return stoAddCashGroupDao.getUnSetCarInfo(parameter);
	}

	/**
	 * @author wanglu
	 * @date 2017-11-13
	 *
	 *       获取未分配车辆的押运员
	 */
	public List<StoEscortInfo> getUnCheeseEscortInfo(Map<String, Object> parameter) {
		return stoAddCashGroupDao.getUnCheeseEscortInfo(parameter);
	}
}
