package com.coffer.businesses.modules.store.v02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;
import com.coffer.core.common.service.CrudService;
import com.coffer.businesses.modules.store.v02.dao.StoOfficeStamperInfoDao;

/**
 * 印章管理Service
 * 
 * @author Zhengkaiyuan
 * @version 2016-09-09
 */
@Service
@Transactional(readOnly = true)
public class StoOfficeStamperInfoService extends CrudService<StoOfficeStamperInfoDao, StoOfficeStamperInfo> {
	@Autowired
	private StoOfficeStamperInfoDao stoOfficeStamperInfoDao;
	
	/**
	 * 
	 * Title: getById
	 * <p>Description: 根据ID查询机构印章</p>
	 * @author:     wangbaozhong
	 * @param id
	 * @return 
	 * StoOfficeStamperInfo    返回类型
	 */
	@Transactional(readOnly = true)
	public StoOfficeStamperInfo getById(String id) {
		return stoOfficeStamperInfoDao.getById(id);
	}
}
