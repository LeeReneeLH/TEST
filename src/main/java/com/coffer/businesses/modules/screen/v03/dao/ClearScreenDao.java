package com.coffer.businesses.modules.screen.v03.dao;
import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.screen.v03.entity.AtmInfo;
import com.coffer.businesses.modules.screen.v03.entity.ClearScreenMain;
import com.coffer.businesses.modules.screen.v03.entity.DoorInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分接收DAO接口
 * @author wl
 * @date 2017-02-13
 */
@MyBatisDao
public interface ClearScreenDao extends CrudDao<ClearScreenMain> {
	

	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public ClearScreenMain findUpList(ClearScreenMain clearScreenMain);
	
	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public ClearScreenMain findBackList(ClearScreenMain clearScreenMain);
	
	
	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public ClearScreenMain findErrorList(ClearScreenMain clearScreenMain);
	

	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public ClearScreenMain findClearList(ClearScreenMain clearScreenMain);
	
	
	/**
	 * 
	 * Title: findAtmCountList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public List<AtmInfo> findAtmCountList(ClearScreenMain clearScreenMain);
	
	/**
	 * 
	 * Title: findDoorOrderList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wanglin
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public List<DoorInfo> findDoorOrderList(ClearScreenMain clearScreenMain);
	
	

	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询服务清分业务机构数量
	 * @param id
	 * @return 机构数量
	 */
	public int getClearCount(ClearScreenMain clearScreenMain);
	
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询服务金库业务机构数量
	 * @param id
	 * @return 机构数量
	 */
	public int getGoldBankCount(ClearScreenMain clearScreenMain);
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询上门收款门店
	 * @param id
	 * @return 机构数量
	 */
	public int getDoorCustCount(ClearScreenMain clearScreenMain);
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询上门收款商户
	 * @param id
	 * @return 机构数量
	 */
	public int getDoorBusinessCount(ClearScreenMain clearScreenMain);
	
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询服务上门收款
	 * @param id
	 * @return 机构数量
	 */
	public int getDoorGoldBankCount(ClearScreenMain clearScreenMain);
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询加钞自助设备(ATM)
	 * @param id
	 * @return 机构数量
	 */
	public int getAtmCount(ClearScreenMain clearScreenMain);
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询服务自助设备客户(ATM)
	 * @param id
	 * @return 机构数量
	 */
	public int getAtmCustCount(ClearScreenMain clearScreenMain);

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询省图编号
	 * @param id
	 * @return 省图编号
	 */
	public String getProvinceCode(String name);
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          机构表查询省图编号
	 * @param id
	 * @return 省图编号
	 */
	public String getOfficeProvinceCode(String name);

	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询市图编号
	 * @param id
	 * @return 市图编号
	 */
	public String getCityCode(String name);
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          机构表查询市图编号
	 * @param id
	 * @return 市图编号
	 */
	public String getOfficeCityCode(String name);
	
	
	
	/**
	 * 
	 * @author qph
	 * @version 2018年02月06日
	 * 
	 *          获取清分服务金额（接口）
	 * @param id
	 * @return
	 */
	public ClOutMain getClearServiceAmount(String id);

	/**
	 * 
	 * @author qph
	 * @version 2018年02月06日
	 * 
	 *          获取现金服务金额（接口）
	 * @param id
	 * @return
	 */
	public AllAllocateInfo getCashAmount(String id);

	/**
	 * 
	 * @author qph
	 * @version 2018年02月06日
	 * 
	 *          获取现金服务金额（接口）
	 * @param id
	 * @return
	 */
	public CheckCashMain getDoorAmount(String id);

	/**
	 * 
	 * @author qph
	 * @version 2018年02月07日
	 * 
	 *          获取自助设备金额（接口）
	 * @param id
	 * @return
	 */
	public AtmBindingInfo getAtmAmount(String id);

}

