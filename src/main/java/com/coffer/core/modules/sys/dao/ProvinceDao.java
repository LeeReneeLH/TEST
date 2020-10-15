package com.coffer.core.modules.sys.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.ProvinceEntity;

@MyBatisDao
public interface ProvinceDao extends CrudDao<ProvinceEntity>{
	
	/**
	 * 
	 * Title: findSelect2ProData
	 * <p>Description: 查询省份表有效数据</p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * @return 返回有效数据实体类的集合
	 * List<ProvinceEntity>    返回类型
	 */
	public List<ProvinceEntity> findSelect2ProData(ProvinceEntity proEntity);
	
	/**
	 * 
	 * Title: findProName
	 * <p>Description: 查询省份名称</p>
	 * @author:     wanghan
	 * @param provinceCode 省份编码
	 * @return 返回省份名称
	 * String    返回类型
	 */
	public String findProName(String provinceCode);
	
	/**
	 * 
	 * Title: findProNum
	 * <p>Description: </p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * @return 
	 * int    返回类型
	 */
	public int findProNum(ProvinceEntity proEntity);
	
	/**
	 * 
	 * Title: findProJsonCode
	 * <p>Description: 查询地图json编码</p>
	 * @author:     wanghan
	 * @param provinceCode 省份编码
	 * @return 
	 * String    返回类型
	 */
	public String findProJsonCode(String provinceCode);
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 删除标识更改为有效</p>
	 * @author:     wanghan
	 * @param proEntity 省份表实体类对象
	 * void    返回类型
	 */
	public void revert(ProvinceEntity proEntity);
	
	/**
	 * 
	 * Title: findProvinceData
	 * <p>Description: 查询当前金库/网点的父/子机构省份信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<ProvinceEntity>    返回类型
	 */
	public List<ProvinceEntity> findProvinceData(Office curOffice);
	
	/**
	 * 
	 * Title: findProGenData
	 * <p>Description: 查询当前人行的父/子机构以及同级人行的省份信息</p>
	 * @author:     wanghan
	 * @param curOffice 当前机构实体类对象
	 * @return 
	 * List<ProvinceEntity>    返回类型
	 */
	public List<ProvinceEntity> findProGenData(Office curOffice);
	
	//人行业务相关代码，暂时不用，以后也许会用
	/*public List<ProvinceEntity> findGenChildProData(String gentralId);
	
	public List<ProvinceEntity> findGenParProData(String gentralId);
	
	public ProvinceEntity findCurProOffice(String gentralId);*/
	
	/**
	 * 
	 * Title: findClearProData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的省份信息
	 * </p>
	 * 
	 * @author: wanghan
	 * @param curOffice
	 * @return List<ProvinceEntity> 返回类型
	 */
	public List<ProvinceEntity> findClearProData(Office curOffice);
	 
	/**
	 * 
	 * Title: findClearProData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的省份信息(平台过滤)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 * @return List<ProvinceEntity> 返回类型
	 */
	public List<ProvinceEntity> findClearProDataByPlat(Office curOffice);

	/**
	 * 
	 * Title: findClearProData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的省份信息(清分中心过滤)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 * @return List<ProvinceEntity> 返回类型
	 */
	public List<ProvinceEntity> findClearProDataByClear(Office curOffice);

	/**
	 * 
	 * Title: findClearProData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的省份信息(在线地图过滤)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 * @return List<ProvinceEntity> 返回类型
	 */
	public List<ProvinceEntity> findOnlineClearProDataByClear(Office curOffice);
	
	/**
	 * 
	 * Title: findClearProData
	 * <p>
	 * Description: 查询当前清分中心的父级人行以及同级金库的省份信息(在线地图过滤)
	 * </p>
	 * 
	 * @author: qph
	 * @param curOffice
	 * @return List<ProvinceEntity> 返回类型
	 */
	public List<ProvinceEntity> findOnlineClearProDataByPlat(Office curOffice);

}

