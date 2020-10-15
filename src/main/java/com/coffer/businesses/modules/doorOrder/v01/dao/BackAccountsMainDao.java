package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 回款管理DAO接口
 * 
 * @author XL
 * @version 2019-06-26
 */
@MyBatisDao
public interface BackAccountsMainDao extends CrudDao<BackAccountsMain> {

	/**
	 * 
	 * Title: getBackAmount
	 * <p>
	 * Description: 查询汇款总金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param backAccountsMain
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getBackAmount(BackAccountsMain backAccountsMain);

	/**
	 * 
	 * Title: getBackAccountsList
	 * <p>
	 * Description: 获取已办回款信息列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param backAccountsMain
	 * @return List<BackAccountsMain> 返回类型
	 */
	List<BackAccountsMain> getBackAccountsList(BackAccountsMain backAccountsMain);
	
	/**
	 * 回款单号列表
	 * 
	 * @author gzd
	 * @version 2019年11月29日
	 * @param door_id
	 * @return
	 */
	List<BackAccountsMain> getOrderId(@Param("doorId")String doorId);
}