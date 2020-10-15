package com.coffer.businesses.modules.atm.v01.dao;

import java.util.List;
import java.util.Map;

import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 加钞计划导入DAO接口
 * @author LLF
 * @version 2015-05-19
 */
@MyBatisDao
public interface AtmPlanInfoDao extends CrudDao<AtmPlanInfo> {
	
	public List<AtmPlanInfo> findAddPlanList(AtmPlanInfo atmPlanInfo);
	
	public List<AtmPlanInfo> addPlanView(AtmPlanInfo atmPlanInfo);
	
	public List<Map<String, Object>> findByMap(Map<String, Object> map);
	
	public List<Map<String, Object>> findByAddPlanId(AtmPlanInfo atmPlanInfo);
	
	public void updateStatus(AtmPlanInfo atmPlanInfo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年6月15日
	 * 
	 *          验证自助设备是否在计划中
	 * @param atmPlanInfo
	 * @return
	 */
	public List<AtmPlanInfo> validatePlanExist(AtmPlanInfo atmPlanInfo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年6月18日
	 * 
	 *          统计当前计划下每个型号下个类型钞箱数量
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getATMtypenoBoxNum(String addPlanId);
	
	/**
	 * 
	 * @author QPH
	 * @version 2017年11月23日
	 * 
	 * 
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getATMtypeByAddPlanId(String addPlanId);

	/**
	 * 
	 * @author LLF
	 * @version 2015年8月14日
	 * 
	 *          汇总当前计划各类型钞箱数量
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> boxTypeCollect(AtmPlanInfo atmPlanInfo);	
	
	/**
	 *单独修改加钞计划的加钞组信息 
	 * @author wanglu
	 * @version 2017年11月16日
	 * @param AtmPlanInfo
	 * @return
	 */
	public void bindPlanAddCashGroup(AtmPlanInfo atmPlanInfo);
	
	/**
	 *获取当日问完成ATM机所属加钞计划信息 
	 * @author wanglu
	 * @version 2017年11月23日
	 * @param Map
	 * @return
	 */
	public List<AtmPlanInfo> getAtmPlanInfoByStatus(AtmPlanInfo atmPlanInfo);
	
	/**
	 *根据加钞计划ID获取ATM机信息 
	 * @author wanglu
	 * @version 2017年11月23日
	 * @param Map
	 * @return
	 */
	public List<AtmPlanInfo> getAtmInfoByPlanId(AtmPlanInfo atmPlanInfo);

}