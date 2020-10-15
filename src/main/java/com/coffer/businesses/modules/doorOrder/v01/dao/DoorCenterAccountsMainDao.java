package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositAmount;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 账务管理DAO接口
 * 
 * @author QPH
 * @version 2017-09-04
 */
@MyBatisDao
public interface DoorCenterAccountsMainDao extends CrudDao<DoorCenterAccountsMain> {
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
	public List<DoorCenterAccountsMain> getAccountByBusinessType(DoorCenterAccountsMain CenterAccountsMain);

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
	public List<DoorCenterAccountsMain> getAccountByBusinessTypeForReport(DoorCenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author wzj
	 * @version 2017年9月13日
	 * 
	 *          根据客户id查询明细信息
	 * @param DoorCenterAccountsMain
	 * 
	 * @return
	 */
	public List<DoorCenterAccountsMain> getDetailListByAccountsId(DoorCenterAccountsMain centerAccountsMain);

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
	public List<DoorCenterAccountsMain> findListGroupByBusinessType(DoorCenterAccountsMain CenterAccountsMain);

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
	public DoorCenterAccountsMain findSumList(DoorCenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * Title: getDepoRemitAmount
	 * <p>
	 * Description: 获取中心存款、已汇款、未汇款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<OfficeAmount> getDepoRemitAmount(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDepositAllAmount
	 * <p>
	 * Description: 查询中心存款汇款总额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getDepositAllAmount(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDepositByWeek
	 * <p>
	 * Description: 查询中心近一周存款
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<OfficeAmount> getDepositByWeek(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDepositByMonth
	 * <p>
	 * Description: 按照月份获取中心存款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<EchartLine> 返回类型
	 */
	List<OfficeAmount> getDepositByMonth(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: findUntakeErrorList
	 * <p>
	 * Description: 查询门店未处理过的差错记录
	 * </p>
	 * 
	 * @author: wqj
	 * @param CenterAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	public List<DoorCenterAccountsMain> findUntakeErrorList(DoorCenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * Title: getDepositByMonth
	 * <p>
	 * Description: 按照季度获取商户存款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<EchartLine> 返回类型
	 */
	// OfficeAmount getDepositByQuarter(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorAmountByKind
	 * <p>
	 * Description: 查询商户存款、汇款及未汇款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<OfficeAmount> getClientDeReAmount(OfficeAmount officeAmount);

	/**
	 * 
	 * @author lihe
	 * @version 2019年7月14日
	 * 
	 *          按存款类型查询存款金额(商户)
	 * @param OfficeAmount
	 * 
	 * @return List<OfficeAmount>
	 */
	List<OfficeAmount> getAmountByType(OfficeAmount officeAmount);

	/**
	 * 
	 * @author lihe
	 * @version 2019年7月27日
	 * 
	 *          按存款类型查询存款金额(门店)
	 * @param OfficeAmount
	 * 
	 * @return List<OfficeAmount>
	 */
	List<OfficeAmount> getDoorAmountByType(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorDepositByMonth
	 * <p>
	 * Description: 按月份查询商户存款
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<String> 返回类型
	 */
	List<OfficeAmount> getClientDepositByMonth(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorDepositByWeek
	 * <p>
	 * Description: 查询商户近一周存款
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<OfficeAmount> getClientDepositByWeek(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorDepoRepay
	 * <p>
	 * Description: 获取商户汇款汇总金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getClientDepoRepay(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorDepositByMonth
	 * <p>
	 * Description: 按照月份获取门店存款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<OfficeAmount> getDoorDepositByMonth(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getDoorDepoRepay
	 * <p>
	 * Description: 获取门店存款、汇款总额
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getDoorDepoRepay(OfficeAmount officeAmount);

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月29日
	 * 
	 *          按商户分组查询账务信息
	 * @param DoorCenterAccountsMain
	 * 
	 * @return
	 */
	public List<DoorCenterAccountsMain> findListByMerchant(DoorCenterAccountsMain centerAccountsMain);

	/**
	 * 
	 * Title: getCenterErrorForDay
	 * <p>
	 * Description: 获取中心差错记录
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<DoorCenterAccountsMain> getCenterErrorForDay(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getClientErrorForDay
	 * <p>
	 * Description: 获取商户或门店差错记录
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return List<OfficeAmount> 返回类型
	 */
	List<DoorCenterAccountsMain> getClientErrorForDay(OfficeAmount officeAmount);

	/**
	 * 
	 * 查询今日差错信息列表（清分中心）
	 * 
	 * @author XL
	 * @version 2019年8月19日
	 * @param officeAmount
	 * @return
	 */
	List<DoorCenterAccountsMain> getCenterDoorErrorForDay(OfficeAmount officeAmount);

	/**
	 * 
	 * 查询今日差错信息列表（商户或门店）
	 * 
	 * @author XL
	 * @version 2019年8月19日
	 * @param officeAmount
	 * @return
	 */
	List<DoorCenterAccountsMain> getClientDoorErrorForDay(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getClientInfoForCenter
	 * <p>
	 * Description: 小程序查询中心下商户存款信息
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositAmount
	 * @return List<DepositAmount> 返回类型
	 */
	List<DepositAmount> getClientInfoForCenter(DepositAmount depositAmount);

	/**
	 * 
	 * Title: getDoorDepositByMerchantId
	 * <p>
	 * Description: 小程序按照商户查询门店存款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositAmount
	 * @return List<DepositAmount> 返回类型
	 */
	List<DepositAmount> getDoorDepositByMerchantId(DepositAmount depositAmount);

	/**
	 * 
	 * Title: getCenterDepoAndCount
	 * <p>
	 * Description: 小程序查询中心(上次日结至今)总存款
	 * </p>
	 * 
	 * @author: lihe
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	OfficeAmount getCenterDepoAndCount(OfficeAmount officeAmount);

	/**
	 * 
	 * Title: getOrderInfoByDoorId
	 * <p>
	 * Description: 小程序根据门店编号查询存款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositInfo
	 * @return List<DepositInfo> 返回类型
	 */
	List<DepositInfo> getOrderInfoByDoorId(DepositInfo depositInfo);

	/**
	 * 获取差错笔数及金额汇总
	 *
	 * @author XL
	 * @version 2019年8月22日
	 * @param map
	 * @return
	 */
	Map<String, Object> getTotalDoorError(DayReportDoorMerchan dayReportDoorMerchan);

	/**
	 * 获取门店日结汇总列表
	 *
	 * @author XL
	 * @version 2019年8月23日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DoorCenterAccountsMain> findDoorSummaryList(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 通过商户ID获取门店日结汇总列表
	 *
	 * @author ZXK
	 * @version 2019年11月27日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DoorCenterAccountsMain> findDoorListByMerchan(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * Title: getDoorDayReportInfoList
	 * <p>
	 * Description: 获取门店日结信息列表
	 * </p>
	 * 
	 * @author: zxk
	 * @param doorCenter
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> getDoorDayReportInfoList(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 门店日结导出列表
	 *
	 * @author XL
	 * @version 2019年10月30日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DayReportExport> findDayReportExportList(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 账务导出部分
	 *
	 * @author WQJ
	 * @version 2019年10月30日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DoorCenterAccountsMain> excelExporterList(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 根据reportId和clientid获取某个门店结算过的差错记录
	 *
	 * @author WQJ
	 * @version 2019年12月4日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	List<DoorCenterAccountsMain> getErrorByReportId(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * @author WQJ
	 * @version 2019年12月13日
	 * 
	 *          获取中心最新余额，限制一条
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<DoorCenterAccountsMain> getAccountByBusinessTypeForCenter(DoorCenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author wqj
	 * @version 2019年12月17日 查询未代付的传统存款记录
	 * 
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> findNopaidTraditionalSave(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * @author WQJ
	 * @version 2019年12月13日
	 * 
	 *          获取客户最新余额，限制一条
	 * @param CenterAccountsMain
	 * 
	 * @return
	 */
	public List<DoorCenterAccountsMain> getAccountByBusinessTypeForClient(DoorCenterAccountsMain CenterAccountsMain);

	/**
	 * 
	 * @author wqj
	 * @version 2019年12月17日 查询未代付的电子线下存款记录
	 * 
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> offlineSave(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 *
	 * @author wqj
	 * @version 2020年3月5日 获取结算区间的存款总额，并按机具号汇总
	 *
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> getTotalAmountEquipId(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * Title: getDetailByReportId
	 * <p>
	 * Description: 根据日结id查询日结存款明细
	 * </p>
	 * 
	 * @author: lihe
	 * @param doorCenterAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> getDetailByReportId(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * Title: findGuestAccountList
	 * <p>
	 * Description: 获取客户账务列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param centerAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> findGuestAccountsList(DoorCenterAccountsMain centerAccountsMain);

	/**
	 * 
	 * Title: getAmountByBusinessType
	 * <p>
	 * Description: 获取中心总账tab金额（门店存款、汇款、存款差错、差错处理）
	 * </p>
	 * 
	 * @author: lihe
	 * @param doorCenterAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> getAmountByBusinessType(DoorCenterAccountsMain doorCenterAccountsMain);

	/**
	 * 
	 * Title: getReverseList
	 * <p>
	 * Description: 根据凭条获取冲正记录
	 * </p>
	 * 
	 * @author: lihe
	 * @param traditionalSaveList
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> getReverseList(
			@Param("traditionalSaveList") List<DoorCenterAccountsMain> traditionalSaveList);
	
	
	
	/**
	 * 
	 * Title: checkDepositErrorAccount
	 * <p>
	 * Description:验证存款差错记录是否已被结算
	 * </p>
	 * 
	 * @author: ZXK
	 * @param doorCenterAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	List<DoorCenterAccountsMain> checkDepositErrorAccount(DoorCenterAccountsMain doorCenterAccountsMain);
	
	/**
	 * 根据预约单号获取日结ID
	 * @author: HZY
	 * @param businessId
	 * @return	DoorCenterAccountsMain 返回类型
	 */
	DoorCenterAccountsMain getReportId(@Param("businessId") String businessId);
}