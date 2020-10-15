package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportAccountExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 商户日结DAO接口
 * 
 * @author wqj
 * @version 2019-07-23
 */
@MyBatisDao
public interface DayReportDoorMerchanDao extends CrudDao<DayReportDoorMerchan> {

	/**
	 * 
	 * Title: getLastDayReportDate
	 * <p>
	 * Description: 获取当前商户上次日结时间
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return List<DayReportDoorMerchan> 返回类型
	 */
	List<DayReportDoorMerchan> getLastDayReportDate(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 根据商户ID和日结ID，查商户日结具体信息
	 * 
	 * @author zhr
	 * @version 2019.08.28
	 * @param merchantId,reportId
	 */
	public List<DayReportDoorMerchan> getfindByMerchantIdAndReportId(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 
	 * Title: getTransferFundsListByUser
	 * <p>
	 * Description:查询已办划款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return List<DayReportDoorMerchan> 返回类型
	 */
	List<DayReportDoorMerchan> getTransferFundsListByUser(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 根据条件查询日结列表（接口用）
	 *
	 * @author XL
	 * @version 2019年9月1日
	 * @param dayReportDoorMerchan
	 * @return
	 */
	public List<DayReportDoorMerchan> findListForApp(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 
	 * Title: getTransferFundsList
	 * <p>
	 * Description:小程序查询划款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return List<DayReportDoorMerchan> 返回类型
	 */
	List<DayReportDoorMerchan> getTransferFundsList(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 
	 * Title: getTransferFundsTotal
	 * <p>
	 * Description: 查询划款应付款和实付款总额
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return DayReportDoorMerchan 返回类型
	 */
	DayReportDoorMerchan getTransferFundsTotal(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 
	 * Title: getMerTransferInfo
	 * <p>
	 * Description: 商户划款信息查询
	 * </p>
	 * 
	 * @author: zxk
	 * @param dayReportDoorMerchan
	 * @return DayReportDoorMerchan 返回类型
	 */
	DayReportDoorMerchan getMerTransferInfo(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 根据条件查询商户展示列表（接口用）
	 *
	 * @author ZXK
	 * @version 2019年11月27日
	 * @param dayReportDoorMerchan
	 * @return
	 */
	public List<DayReportDoorMerchan> findMerchanListForApp(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 
	 * Title: confirm
	 * <p>
	 * Description: 商户确认汇款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return
	 */
	int confirm(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 获取该中心传统结算最新结算时间，在此时间之前的登记传统存款不可冲正修改。
	 * 
	 * @author wqj
	 * @version 2020年3月13日
	 */
	public Date getTraditionalsaveMaxDate(Office office);

	/**
	 * 
	 * Title: getSummation
	 * <p>
	 * Description: 获取日结金额合计
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return DayReportDoorMerchan 返回类型
	 */
	DayReportDoorMerchan getSummation(DayReportDoorMerchan dayReportDoorMerchan);
	
	/**
	 * 门店日结导出账户信息列表
	 *
	 * @author ZXK
	 * @version 2019年12月16日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DayReportAccountExport> findDayReportAccountExportList(DayReportDoorMerchan dayReportDoorMerchan);
}