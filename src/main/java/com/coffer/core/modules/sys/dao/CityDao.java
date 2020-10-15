package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.CityEntity;
import com.coffer.core.modules.sys.entity.Office;

@MyBatisDao
public interface CityDao extends CrudDao<CityEntity>{
	
	
	/**
	 * 
	 * Title: findSelect2CityData
	 * <p>Description: 查询城市表有效数据</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @return 城市表实体类对象集合
	 * List<CityEntity>    返回类型
	 */
	public List<CityEntity> findSelect2CityData(CityEntity cityEntity);
	
	/**
	 * 
	 * Title: findCityName
	 * <p>Description: 查询城市名称</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return  城市名称
	 * String    返回类型
	 */
	public String findCityName(String cityCode);
	
	/**
	 * 
	 * Title: findCityJson
	 * <p>Description: 查询城市地图json编码</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return  城市地图编码
	 * String    返回类型
	 */
	public String findCityJson(String cityCode);
	
	/**
	 * 
	 * Title: findGenBankCityDate
	 * <p>Description: 查询人行地图数据</p>
	 * @author:     wanghan
	 * @param office 当前机构实体类对象
	 * @return 城市表实体类对象集合
	 * List<CityEntity>    返回类型
	 */
	public List<CityEntity> findGenBankCityDate(Office office);
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 删除标识更改为有效</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * void    返回类型
	 */
	public void revert(CityEntity cityEntity);
	
	/**
	 * 
	 * Title: findCityCodeNum
	 * <p>Description: 查询当前条件的城市记录个数</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return 符合当前条件的城市记录个数
	 * int    返回类型
	 */
	public int findCityCodeNum(String cityCode);
		
	/**
	 * 
	 * Title: findCityJsonNum
	 * <p>Description: 查询当前条件的城市记录个数</p>
	 * @author:     wanghan
	 * @param cityJsonCode 城市地图编码
	 * @return 符合当前条件的城市记录个数
	 * int    返回类型
	 */
	public int findCityJsonNum(String cityJsonCode);
	
	/**
	 * 
	 * Title: findCityData
	 * <p>Description: 查询当前金库/网点的父/子机构城市信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CityEntity>    返回类型
	 */
	public List<CityEntity> findCityData(Office curOffice);
	
	/**
	 * 
	 * Title: findCityGenData
	 * <p>Description: 查询当前人行的父/子机构以及同级人行的城市信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CityEntity>    返回类型
	 */
	public List<CityEntity> findCityGenData(Office curOffice);
	
	//人行业务相关代码，暂时不用，以后也许会用
	/*public List<CityEntity> findGenChildCityData(String gentralId);
	
	public List<CityEntity> findGenParCityData(String gentralId);
	
	public CityEntity findCurCityOffice(String gentralId);*/
	
	/**
	 * 
	 * Title: findClearCityData
	 * <p>Description: 查询当前清分中心的父级人行以及同级金库的城市信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CityEntity>    返回类型
	 */
	public List<CityEntity> findClearCityData(Office curOffice);

	/**
	 * 
	 * Title: findClearCityData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的城市信息(清分中心权限)
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findClearCityDataByclear(Office curOffice);

	/**
	 * 
	 * Title: findClearCityData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的城市信息(平台权限)
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findClearCityDataByPlat(Office curOffice);

	/**
	 * 
	 * Title: findClearCityData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的城市信息(平台权限)
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findOnlineClearCityDataByClear(Office curOffice);

	/**
	 * 
	 * Title: findClearCityData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的城市信息(平台权限)
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findOnlineClearCityDataByPlat(Office curOffice);

	/**
	 * 
	 * Title: findClearCityData
	 * <p>
	 * Description: 按省份过滤清分中心
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findClearCityDataByProvinceCode(Office curOffice);

	/**
	 * 
	 * Title: findPeopleBankByProvinceCode
	 * <p>
	 * Description: 按省份过滤清分中心
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<Office> findPeopleBankByProvinceCode(Office curOffice);

	/**
	 * 
	 * Title: findPeopleBankByProvinceCode
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的城市信息(人民银行权限)
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<CityEntity> findClearCityDataByPeopleBank(Office curOffice);

	/**
	 * 
	 * Title: findPeopleBankByProvinceCode
	 * <p>
	 * Description: 通过cityJsonCode查询JsonCode
	 * </p>
	 * 
	 * @author: QPH
	 * @param cityJsonCode
	 *            当前机构实体类对象
	 * @return List<String> 返回类型
	 */
	public String findCityCodeByJson(String cityJsonCode);
}
