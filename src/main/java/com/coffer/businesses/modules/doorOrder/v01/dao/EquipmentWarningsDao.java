package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机具报警DAO接口
 * 
 * @author zhaohaoran
 * @version 2019-07-24
 */
@MyBatisDao
public interface EquipmentWarningsDao extends CrudDao<EquipmentWarnings> {

	/**
	 * 
	 * Title: findDoorList
	 * <p>
	 * Description: 查询当前机构下所有门店
	 * </p>
	 * 
	 * @author: zhr
	 * @param officeId
	 * @param type
	 * @return List<Office> 返回类型
	 */
	public List<Office> findDoorList(@Param("office") Office office, 
				@Param("type") String otype);

	/**
	 * 查找首页显示的机具报警列表
	 *
	 * Description: 
	 *
	 * @author: GJ
	 * @return
	 */
	public List<EquipmentWarnings> findWarningList(EquipmentWarnings equipmentWarnings);
	
	/**
	 * 
	 * Title: findDoorEqNow
	 * <p>
	 * Description: 查询当前机具当天是否发生过该类型异常信息
	 * </p>
	 * 
	 * @author: hzy
	 * @param warnings
	 * @return List<Office> 返回类型
	 */
	public List<EquipmentWarnings> findDoorEqNow(EquipmentWarnings warnings);
}
