package com.coffer.businesses.modules.doorOrder.v01.dao;
import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 存款差错DAO接口
 * 
 * @author ZXK
 * @version 2019-07-17
 */
@MyBatisDao
public interface DepositErrorDao extends CrudDao<DepositError> {

	/**
	 * 根据id 修改差错表状态
	 * 
	 * @param depositError
	 * @return
	 */
	 int updateStatus(DepositError depositError);

	/**
	 * 
	 * Title: getDepositErrorForDay
	 * <p>
	 * Description: 按差错类型查询今日存款差错
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<DepositError> 返回类型
	 */
	List<DepositError> getDepositErrorForDay(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDepositErrorForDay
	 * <p>
	 * Description: 获取门店今日存款差错
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<DepositError> 返回类型
	 */
	List<DepositError> getDoorErrorForDay(OfficeAmount officeAmount);
	
	/**
	 * 
	 * Title: isOrderExists
	 * <p>
	 * Description: 判断未冲正的带差错存款单号是否存在
	 * </p>
	 * 
	 * @author: guojian
	 * @param orderId
	 * @return String 返回类型
	 */
	String isOrderExists(String orderId);
	
	/**
	 * 
	 * Title: getDoorIdByOrderId
	 * <p>
	 * Description: 根据单号去查找对应单号的门店，保证二者一致性
	 * </p>
	 * 
	 * @author: guojian
	 * @param orderId
	 * @return String 返回类型
	 */
	String getDoorIdByOrderId(String orderId);
	
	
	/**
	 * 
	 * Title: getDoorIdByOrderId
	 * <p>
	 * Description: 在单号未冲正的情况下保证该单号下仅有一条差错记录处于登记状态
	 * </p>
	 * 
	 * @author: guojian
	 * @param orderId
	 * @return Integer 返回类型
	 */
	Integer getLoginCount(String orderId);

	/**
	 * 
	 * Title: isMoreThanSave
	 * <p>
	 * Description: 登记差错时，短款金额不能多于存款金额
	 * </p>
	 * 
	 * @author: guojian
	 * @param orderId
	 * @return Integer 返回类型
	 */
	String isMoreThanSave(String orderId);
}
