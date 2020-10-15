package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.dao.StoCarInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;

/**
 * 车辆管理Service
 * @author LLF
 * @version 2017-07-30
 */
@Service
@Transactional(readOnly = true)
public class StoCarInfoService extends CrudService<StoCarInfoDao, StoCarInfo> {

	@Autowired
	private StoCarInfoDao stoCarInfoDao;
	
	public StoCarInfo get(String id) {
		return super.get(id);
	}
	
	public List<StoCarInfo> findList(StoCarInfo stoCarInfo) {
		return super.findList(stoCarInfo);
	}
	
	public List<StoCarInfo> findAllList(StoCarInfo stoCarInfo) {
		return stoCarInfoDao.findAllList(stoCarInfo);
	}
	
	public Page<StoCarInfo> findPage(Page<StoCarInfo> page, StoCarInfo stoCarInfo) {
		return super.findPage(page, stoCarInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(StoCarInfo stoCarInfo) {
		super.save(stoCarInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoCarInfo stoCarInfo) {
		super.delete(stoCarInfo);
	}
	
	/**
	 * 根据车牌编号查询车辆信息
	 * 
	 * @author XL
	 * @version 2018-09-06
	 * @param stoCarInfo
	 * @return
	 */
	public StoCarInfo getByCarNo(String carNo) {
		StoCarInfo StoCarInfo=new StoCarInfo();
		StoCarInfo.setCarNo(carNo);
		return stoCarInfoDao.getByCarNo(StoCarInfo);
	}
	
}