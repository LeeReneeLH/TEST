package com.coffer.core.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.dao.CityDao;
import com.coffer.core.modules.sys.dao.CountyDao;
import com.coffer.core.modules.sys.entity.CityEntity;
import com.coffer.core.modules.sys.entity.CountyEntity;

/**
 * 
* Title: CityService 
* <p>Description: 城市业务层</p>
* @author wanghan
* @date 2017年11月1日 上午9:27:19
 */
@Service
@Transactional(readOnly = true)
public class CityService extends CrudService<CityDao,CityEntity>{
	
	@Autowired
	CountyDao countyDao;
	
	/**
	 * 
	 * Title: findSelect2CityData
	 * <p>Description: 查询城市下拉菜单有效数据</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @return 返回城市实体对象集合
	 * List<CityEntity>    返回类型
	 */
	@Transactional(readOnly = true)
	public List<CityEntity> findSelect2CityData(CityEntity cityEntity) {
		return dao.findSelect2CityData(cityEntity);
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 更改城市记录无效为有效</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void revert(CityEntity cityEntity) {
		dao.revert(cityEntity);
	}
	
	/**
	 * 
	 * Title: findCityCodeNum
	 * <p>Description: 查询当前条件的城市记录个数</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return 符合当前条件的城市记录个数
	 * int    返回类型
	 */
	@Transactional(readOnly = true)
	public int findCityCodeNum(String cityCode) {
		return dao.findCityCodeNum(cityCode);
	}
	
	/**
	 * 
	 * Title: findCityJsonNum
	 * <p>Description: 查询当前条件的城市记录个数</p>
	 * @author:     wanghan
	 * @param cityJsonCode 城市地图编码
	 * @return 符合当前条件的城市记录个数
	 * int    返回类型
	 */
	@Transactional(readOnly = true)
	public int findCityJsonNum(String cityJsonCode) {
		return dao.findCityJsonNum(cityJsonCode);
	}
	
	/**
	 * 
	 * Title: deleteAssociateCity
	 * <p>Description: 禁用当前城市以及当前城市下县区</p>
	 * @author:     wanghan
	 * @param cityEntity  城市表实体类对象
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void deleteAssociateCity(CityEntity cityEntity) {
		CountyEntity countyEntity = new CountyEntity();
		countyEntity.setCityCode(cityEntity.getCityCode());
		dao.delete(cityEntity);
		countyDao.delete(countyEntity);
	}

	/**
	 * 
	 * Title: findCityName
	 * <p>
	 * Description: 查询当前条件的城市名称
	 * </p>
	 * 
	 * @author: wzj
	 * @param cityCode
	 *            城市地图编码
	 * @return 符合当前条件的城市记录个数 String 返回类型
	 */
	@Transactional(readOnly = false)
	public String findCityName(String cityCode) {
		return dao.findCityName(cityCode);
	}
}
