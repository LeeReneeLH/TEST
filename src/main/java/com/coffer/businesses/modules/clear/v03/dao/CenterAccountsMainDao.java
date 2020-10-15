package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 账务管理DAO接口
 * 
 * @author QPH
 * @version 2017-09-04
 */
@MyBatisDao
public interface CenterAccountsMainDao extends CrudDao<CenterAccountsMain> {
	/**
	 * 
	 * @author QPH
	 * @version 2017年9月4日
	 * 
	 *          根据业务类型以及客户ID查询信息
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<CenterAccountsMain> getAccountByBusinessType(CenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author QPH
	 * @version 2017年12月5日
	 * 
	 *          根据业务类型以及客户ID查询信息(结算用)
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<CenterAccountsMain> getAccountByBusinessTypeForReport(CenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author wzj
	 * @version 2017年9月13日
	 * 
	 *          根据客户id查询明细信息
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<CenterAccountsMain> getDetailListByAccountsId(CenterAccountsMain centerAccountsMain);

	/**
	 * 
	 * @author QPH
	 * @version 2017年9月14日
	 * 
	 *          根据业务类型分组查询
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<CenterAccountsMain> findListGroupByBusinessType(CenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author sg
	 * @version 2017年10月17日
	 * 
	 *          根据开始结束时间查询入库金额及出库金额总数
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public CenterAccountsMain findSumList(CenterAccountsMain CenterAccountsMain);

}