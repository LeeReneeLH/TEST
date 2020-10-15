package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.CountyEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 
* Title: CountyDao 
* <p>Description: 县级表持久层</p>
* @author wanghan
* @date 2017年11月2日 上午10:34:43
 */
@MyBatisDao
public interface CountyDao extends CrudDao<CountyEntity>{

	/**
	 * 
	 * Title: findSelect2CountyData
	 * <p>Description: 查询县级表有效数据</p>
	 * @author:     wanghan
	 * @param countyEntity 县级表实体类对象
	 * @return 返回县级表实体类对象集合
	 * List<CountyEntity>    返回类型
	 */
	public List<CountyEntity> findSelect2CountyData(CountyEntity countyEntity);
	
	/**
	 * 
	 * Title: findGenBankCountyDate
	 * <p>Description: 查询人行城市地图数据</p>
	 * @author:     wanghan
	 * @param office
	 * @return 
	 * List<CountyEntity>    返回类型
	 */
	public List<CountyEntity> findGenBankCountyDate(Office office);
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 删除标识更改为有效</p>
	 * @author:     wanghan
	 * @param countyEntity  县级表实体类对象
	 * void    返回类型
	 */
	public void revert(CountyEntity countyEntity);
	
	/**
	 * 
	 * Title: findCountyCodeNum
	 * <p>Description: 查询当前条件的县/区记录个数</p>
	 * @author:     wanghan
	 * @param countyCode 县/区编码
	 * @return 县/区记录个数
	 * int    返回类型
	 */
	public int findCountyCodeNum(String countyCode);
	
	/**
	 * 
	 * Title: findCountyData
	 * <p>Description: 查询当前金库/网点的父/子机构区/县信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CountyEntity>    返回类型
	 */
	public List<CountyEntity> findCountyData(Office curOffice);
	
	/**
	 * 
	 * Title: findCountyGenData
	 * <p>Description: 查询当前人行的父/子机构以及同级人行的区/县信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CountyEntity>    返回类型
	 */
	public List<CountyEntity> findCountyGenData(Office curOffice);
	
	//人行业务相关代码，暂时不用，以后也许会用
	/*public List<CountyEntity> findGenChildCountyData(String gentralId);
	
	public List<CountyEntity> findGenParCountyData(String gentralId);
	
	public CountyEntity findCurCountyOffice(String gentralId);*/
	
	/**
	 * 
	 * Title: findClearCountyData
	 * <p>Description: 查询当前清分中心的父级人行以及同级金库的区/县信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<CountyEntity>    返回类型
	 */
	public List<CountyEntity> findClearCountyData(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的区/县信息(清分中心)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findClearCountyDataByclear(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的区/县信息(平台)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findClearCountyDataByPlat(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的区/县信息(清分中心(在线))
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findOnlineClearCountyDataByclear(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的区/县信息(平台(在线))
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findOnlineClearCountyDataByPlat(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 按城市过滤清分中心
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findClearCityDataByCityCode(Office curOffice);

	/**
	 * 
	 * Title: findPeopleBankByCityCode
	 * <p>
	 * Description: 获取市级人民银行
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<Office> findPeopleBankByCityCode(Office curOffice);

	/**
	 * 
	 * Title: findClearCountyData
	 * <p>
	 * Description: 按城市过滤清分中心
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CountyEntity> 返回类型
	 */
	public List<CountyEntity> findClearCountyDataByPeopleBank(Office curOffice);
}
