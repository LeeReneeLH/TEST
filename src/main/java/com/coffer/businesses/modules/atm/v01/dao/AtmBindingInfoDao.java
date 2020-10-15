package com.coffer.businesses.modules.atm.v01.dao;

import java.util.List;
import java.util.Map;

import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机加钞查询DAO接口
 * @author LLF
 * @version 2015-05-22
 */
@MyBatisDao
public interface AtmBindingInfoDao extends CrudDao<AtmBindingInfo> {
	public List<AtmBindingInfo> findByBoxNo(String boxNo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年6月15日
	 * 
	 *  验证自助设备是否需要清机
	 * @param atmBindingInfo
	 * @return
	 */
	public AtmBindingInfo validateATMIsOrNotClearing(AtmBindingInfo atmBindingInfo);
	
	
	/**
	 * 
	 * @author LP
	 * @version 2015年7月17日
	 * 
	 *  手动添加核心余额时 验证是该ATM是否可以修改核心清机金额  
	 * @param atmBindingInfo
	 * @return
	 */
	public Integer validateATMCoreAmount(AtmBindingInfo atmBindingInfo);
	
	
	/**
	 * 
	 * @author LiuPeng
	 * @version 2015年7月17日
	 * 
	 *  修改清机金额
	 * @param map
	 */
	public void updateATMCoreAmount(AtmBindingInfo atmBindingInfo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年7月2日
	 * 
	 *  清点使用 ATM机清点完成
	 * @param map
	 */
	public void updateStatus(Map<String,Object> map);
	
	/**
	 * 设备名称下拉列表
	 * @author wxz
	 * @version 2017年11月23日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByAtmName(AtmInfoMaintain atmInfoMaintain);

	/**
	 * 绑定信息列表
	 * 
	 * @author xl
	 * @version 2017年11月23日
	 * @param AtmBindingInfo
	 * @return
	 */
	public List<AtmBindingInfo> findAtmBindingList(AtmBindingInfo atmBindingInfo);
}