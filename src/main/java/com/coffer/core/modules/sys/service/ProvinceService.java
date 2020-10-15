package com.coffer.core.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.dao.CityDao;
import com.coffer.core.modules.sys.dao.CountyDao;
import com.coffer.core.modules.sys.dao.ProvinceDao;
import com.coffer.core.modules.sys.entity.CityEntity;
import com.coffer.core.modules.sys.entity.CountyEntity;
import com.coffer.core.modules.sys.entity.ProvinceEntity;

/**
 * 
* Title: ProvinceService 
* <p>Description: 省份业务层</p>
* @author wanghan
* @date 2017年11月1日 上午9:26:41
 */
@Service
@Transactional(readOnly = true)
public class ProvinceService extends CrudService<ProvinceDao, ProvinceEntity>{
	
	@Autowired
	CityDao cityDao;
	
	@Autowired
	CountyDao countyDao;
	
	/**
	 * 
	 * Title: findSelect2ProList
	 * <p>Description: 查询有效省份下拉菜单数据</p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * @return 返回省份实体对象集合
	 * List<ProvinceEntity>    返回类型
	 */
	@Transactional(readOnly = true)
	public List<ProvinceEntity> findSelect2ProList(ProvinceEntity proEntity) {
		return dao.findSelect2ProData(proEntity);
	}
	
	/**
	 * 
	 * Title: findProNum
	 * <p>Description: 查询有效的省份记录个数</p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * @return 返回有效记录个数
	 * int    返回类型
	 */
	@Transactional(readOnly = true)
	public int findProNum(ProvinceEntity proEntity) {
		return dao.findProNum(proEntity);
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 更改省份记录无效为有效</p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void revert(ProvinceEntity proEntity) {
		dao.revert(proEntity);
	}
	
	/**
	 * 
	 * Title: deleteAssociatePro
	 * <p>Description: 禁用省份以及当前省份下的城市和县/区</p>
	 * @author:     wanghan
	 * @param proEntity  省份表实体类对象
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void deleteAssociatePro(ProvinceEntity proEntity) {
		CityEntity cityEntity = new CityEntity();
		CountyEntity countyEntity = new CountyEntity();
		cityEntity.setProvinceCode(proEntity.getProvinceCode());
		countyEntity.setProvinceCode(proEntity.getProvinceCode());
		dao.delete(proEntity);
		cityDao.delete(cityEntity);
		countyDao.delete(countyEntity);
	}
}
