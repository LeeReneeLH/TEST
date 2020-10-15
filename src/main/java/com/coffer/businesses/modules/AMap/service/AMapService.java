package com.coffer.businesses.modules.AMap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.AMap.dao.AMapDao;
import com.coffer.businesses.modules.AMap.entity.AMap;
import com.coffer.core.common.service.BaseService;

/**
 * 
 * @author liuyaowen
 * @version
 *
 */
@Service
@Transactional(readOnly = true)
public class AMapService extends BaseService {
	@Autowired
	private AMapDao dao;

	public List<AMap> findMap() {
		return dao.findMap();
	}
}
