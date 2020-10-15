package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.CheckCashAuthorize;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
/**
 * 
* Title: CheckCashAuthorizeDao 
* <p>Description:授权管理Dao </p>
* @author HaoShijie
* @date 2020年5月6日 上午10:11:53
 */
@MyBatisDao
public interface CheckCashAuthorizeDao extends CrudDao<CheckCashAuthorize>{
	/**
	 * 
	 * Title: getAuthorizeList
	 * <p>Description: 查询授权信息列表</p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @return 
	 * List<CheckCashAuthorize>    返回类型
	 */
	public List<CheckCashAuthorize> getAuthorizeList(CheckCashAuthorize checkCashAuthorize);
	
	
	/**
	 * 根据门店和类型 获取 信息列表
	 * @param checkCashAuthorize
	 * @return
	 */
	List<CheckCashAuthorize> getListByTypeAndOffice(CheckCashAuthorize checkCashAuthorize);
	
	/**
	 * 根据门店和类型 获取 授权信息
	 * @param checkCashAuthorize
	 * @return
	 */
	public CheckCashAuthorize getByIdForCheckType(CheckCashAuthorize checkCashAuthorize);

}
