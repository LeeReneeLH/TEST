package com.coffer.core.modules.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeApp;
import com.coffer.core.common.persistence.TreeDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机构DAO接口
 * 
 * @author Clark
 * @version 2015-05-13
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<Office> {
	/**
	 * 根据条件查询数据
	 * 
	 * @param entity
	 * @return
	 */
	public List<Office> findListBySearch(Office entity);

	/**
	 * 查询最大机构ID
	 * 
	 * @param entity
	 * @return
	 */
	public Integer getMaxOfficeId();

	/**
	 * 
	 * @author LLF
	 * @version 2015年11月10日
	 * 
	 * 
	 * @param office
	 * @return
	 */
	public List<Office> checkOfficeCode(Office office);

	/**
	 * 
	 * Title: getClearCenterByParentId
	 * <p>
	 * Description: 根据父机构ID查询清分中心
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param parentsId
	 * @return Office 返回类型
	 */
	public Office getClearCenterByParentId(String parentId);

	/**
	 * 
	 * Title: findOfficeListForInterface
	 * <p>
	 * Description:按照参数条件查询本机构及其下属机构信息列表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            本机构ID
	 * @param parentIds
	 *            本机构的所有父ID
	 * @param lastSearchDate
	 *            上次查询日期
	 * @return List<Office> 返回类型
	 */
	public List<Office> findOfficeListForInterface(@Param("officeId") String officeId,
			@Param("parentIds") String parentIds, @Param("lastSearchDate") String lastSearchDate,
			@Param("dbName") String dbName);

	/**
	 * 
	 * Title: findCustList
	 * <p>
	 * Description:根据当前机构的officeId,parentID,type获取机构的下拉列表
	 * </p>
	 * 
	 * @author: sg
	 * @param officeId
	 *            当前机构的ID
	 * @param officeParentId
	 *            当前机构的父ID
	 * @param type
	 *            查询方式
	 * @return List<Office> 返回类型
	 */
	public List<Office> findCustList(@Param("officeId") String officeId, @Param("officeParentId") String officeParentId,
			@Param("list") List<String> list, @Param("dbName") String dbName);

	/**
	 * 
	 * Title: getVaildCntByOfficeId
	 * <p>
	 * Description: 查询有效用户数
	 * </p>
	 * 
	 * @author: wanghan
	 * @param id
	 * @return 有效用户数 int 返回类型
	 */
	public int getVaildCntByOfficeId(String id);

	/**
	 * 
	 * Title: getProCityOffice
	 * <p>
	 * Description: 获取当前机构和父子机构的省份和城市
	 * </p>
	 * 
	 * @author: wanghan
	 * @param currentId
	 *            当前机构id
	 * @return 以map形式返回省份数量和城市数量 Map<String, Integer> 返回类型
	 */
	public List<Office> getProCityOffice(String currentId);

	/**
	 * 
	 * Title: getAssoGenOffice
	 * <p>
	 * Description: 查询与人行有业务往来的机构
	 * </p>
	 * 
	 * @author: wanghan
	 * @param gentralId
	 *            当前人行机构id
	 * @return 返回与人行有业务往来的机构实体类集合 List<Office> 返回类型
	 */
	public List<Office> getAssoGenBusinOffice(String gentralId);

	/**
	 * 
	 * Title: getAssWithClearOffice
	 * <p>
	 * Description: 查询与清中心有业务往来的机构（人行调拨业务表）
	 * </p>
	 * 
	 * @author: wanghan
	 * @param id
	 *            当前机构id
	 * @param parentId
	 *            当前机构的父级id
	 * @return List<Office> 返回类型
	 */
	public List<Office> getAssWithClearOffice(@Param("id") String id, @Param("parentId") String parentId);

	/**
	 * 
	 * Title: getAssoGenOffice
	 * <p>
	 * Description: 查询与当前人行有关联的机构（机构表）
	 * </p>
	 * 
	 * @author: wanghan
	 * @param currentId
	 * @param parentId
	 * @return List<Office> 返回类型
	 */
	public List<Office> getAssoGenOffice(@Param("currentId") String currentId, @Param("parentId") String parentId);

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
	public List<Office> findClearCityDataByProvinceCode(Office curOffice);

	/**
	 * 
	 * Title: findClearCityDataByCityCode
	 * <p>
	 * Description: 按省份过滤清分中心
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<Office> findClearCityDataByCityCode(Office curOffice);

	/**
	 * 
	 * Title: findClearCityDataByCityCode
	 * <p>
	 * Description: 按省份过滤清分中心
	 * </p>
	 * 
	 * @author: QPH
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<Map<String, Object>> getOfficeListByOffice(Office curOffice);

	/**
	 * 
	 * 根据父机构获取下属的所有人行机构
	 * 
	 * @author: SongYuanYang
	 * @version 2018年3月15日
	 * 
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<Office> findPbocByParentId(@Param("parentId") String parentId);

	/**
	 * 根据机构id查询机构名字
	 * 
	 * @param officeId
	 * @return String
	 */
	public String getOfficeNameById(@Param("officeId") String officeId);

	/**
	 * 
	 * Title: findDoorList
	 * <p>
	 * Description: 查询当前机构下所有门店
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeId
	 * @param type
	 * @return List<Office> 返回类型
	 */
	public List<Office> findDoorList(Office office);

	/**
	 * 查询所有门店
	 * 
	 * @return
	 */
	public List<Office> selectStore();

	/**
	 * 获取数字化金融服务平台机构
	 * 
	 * @version 2019年7月10日
	 * @author: wqj
	 */
	public List<Office> getPlatform();

	/**
	 * 根据设备号设备所属查询商户
	 *
	 * @param eqpId
	 *            设备ID
	 * @return
	 * @author yinkai
	 *
	 */
	public Office getMerchantByEqpId(@Param("eqpId") String eqpId);

	/**
	 * 
	 * 通过登录机构查询商户信息
	 * 
	 * @author zxk
	 * @version 2019-8-31
	 * @return
	 */
	public List<OfficeApp> getMerListByOffice(OfficeApp officeApp);

	/**
	 * 根据机具连接状态查询门店设备列表
	 * 
	 * @author zxk
	 * @version 2019-8-31
	 * @return
	 */
	public List<OfficeApp> selectDoorAndEquipList(OfficeApp officeApp);

	/**
	 * 验证门店和公司所属关系
	 * 
	 * @param companyId
	 * @param doorId
	 * @return
	 */
	public Office validateDoorCompany(@Param("companyId") String companyId, @Param("doorId") String doorId);

	/**
	 * 分页查询机构信息
	 * 
	 * @author xp
	 */
	public List<Office> findByParentIdsLike(Office office);

	/**
	 * 根据条件查询数据
	 * 
	 * @param entity
	 * @return
	 */
	public List<Office> findPageListBySearch(Office entity);

	/**
	 * 
	 * 根据父机构获取下属的所有机构
	 * 
	 * @author: xp
	 * 
	 * 
	 * @param curOffice
	 *            当前机构实体类对象
	 * @return List<CityEntity> 返回类型
	 */
	public List<Office> findByParentId(@Param("parentId") String parentId, @Param("dbName") String dbName);

	/**
	 * 查询所有商户
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public List<Office> findOfficeByBusiness(Office office);

	/**
	 * 根据ID查询商户
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public Office findOfficeById(String id);

	/**
	 * 根据ID查询非条件商户
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public Office findOfficeByOtherId(String id);

	/**
	 * 按条件查询商户列表
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public List<Office> officeSearch(Office office);

	/**
	 * 
	 * Title: getClearCenterList
	 * <p>
	 * Description:查询清分中心
	 * </p>
	 * 
	 * @author: hzy
	 * @return List<Office> 返回类型
	 */
	public List<Office> getClearCenterList();

	/**
	 * 
	 * Title: getOfficeByParentId
	 * <p>
	 * Description:根据父机构id查询机构列表
	 * </p>
	 * 
	 * @author: ZXK
	 * @param office
	 * @return
	 * 
	 */
	public List<Office> getOfficeByParentId(Office office);

	/**
	 * 
	 * Title: getClearCenterList
	 * <p>
	 * Description:查询清分中心
	 * </p>
	 * 
	 * @author: lihe
	 * @return List<Office> 返回类型
	 */
	public Office getCenterByParentIds(Office office);
	
	/**
	 * 
	 * Title: getClearCenterList
	 * <p>
	 * Description:查询清分中心
	 * </p>
	 * 
	 * @author: hzy
	 * @return List<Office> 返回类型
	 */
	public List<Office> getClearCenterOfficeList(Office office);
}
