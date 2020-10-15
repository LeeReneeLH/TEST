package com.coffer.businesses.modules.clear;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskRecoveryService;
import com.coffer.businesses.modules.clear.v03.service.ClearingGroupService;
import com.coffer.businesses.modules.clear.v03.service.DayReportCenterService;
import com.coffer.businesses.modules.clear.v03.service.DayReportGuestService;
import com.coffer.businesses.modules.clear.v03.service.TellerAccountsMainService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.service.StoDictService;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 共同处理类(清分管理)
 * 
 * @author wanglin
 * @version 2014-11-18
 */
public class ClearCommonUtils extends BusinessUtils {
	/** 物品字典Service **/
	private static StoDictService stoDictService = SpringContextHolder.getBean(StoDictService.class);

	/** 清分组管理-服务层 **/
	private static ClearingGroupService clearingGroupService = SpringContextHolder.getBean(ClearingGroupService.class);

	/** 账务管理-服务层 **/
	private static CenterAccountsMainService centerAccountsMainService = SpringContextHolder
			.getBean(CenterAccountsMainService.class);
	/** 账务日结管理-服务层 **/
	private static DayReportCenterService dayReportMainService = SpringContextHolder
			.getBean(DayReportCenterService.class);

	/** 账务日结管理-服务层 **/
	private static DayReportGuestService dayReportGuestService = SpringContextHolder
			.getBean(DayReportGuestService.class);

	/** 任务分配-服务层 **/
	private static ClTaskMainService clTaskMainService = SpringContextHolder.getBean(ClTaskMainService.class);

	/** 任务分配-服务层 **/
	private static ClTaskRecoveryService clTaskRecoveryService = SpringContextHolder
			.getBean(ClTaskRecoveryService.class);

	/** 柜员账务-服务层 **/
	private static TellerAccountsMainService tellerAccountsMainService = SpringContextHolder
			.getBean(TellerAccountsMainService.class);

	public static List<DenominationInfo> getDenominationList() {
		return getDenominationList(null, null);
	}

	/**
	 * 面值列表的数据取得
	 * 
	 * @author wanglin
	 * @version 2017年8月22日
	 * @param
	 * @return
	 */
	public static List<DenominationInfo> getDenominationList(List<?> plist, DenominationCtrl pDenomCtrl) {
		List<DenominationInfo> DenomList = Lists.newArrayList();

		StoDict stoDict = new StoDict();
		stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		List<StoDict> denDictList = stoDictService.findList(stoDict);

		for (StoDict itemDen : denDictList) {
			DenominationInfo DenomInfo = new DenominationInfo();
			DenomInfo.setMoneyKey(itemDen.getValue());
			DenomInfo.setMoneyName(itemDen.getLabel());
			DenomInfo.setMoneyValue(String.valueOf(itemDen.getUnitVal()));
			String strValueKey = "";
			String strTotalAmt = "";
			String strColValue1 = "", strColValue2 = "";
			int intTempValue1 = 0, intTempValue2 = 0;

			if (plist != null && pDenomCtrl != null) {
				for (int i = 0; i < plist.size(); i++) {
					strValueKey = (String) getFieldValueByName(pDenomCtrl.getMoneyKeyName(), plist.get(i));
					if (itemDen.getValue().equals(strValueKey)) {
						// 第一列捆数取得
						strColValue1 = (String) getFieldValueByName(pDenomCtrl.getColumnName1(), plist.get(i));
						// 第二列捆数取得
						strColValue2 = (String) getFieldValueByName(pDenomCtrl.getColumnName2(), plist.get(i));

						// 金额的取得
						if ((pDenomCtrl.getTotalAmtName() != null && !pDenomCtrl.getTotalAmtName().equals(""))) {
							strTotalAmt = getFieldValueByName(pDenomCtrl.getTotalAmtName(), plist.get(i));
						} else {
							if ((strColValue1 == null || strColValue1.equals(""))
									&& (strColValue2 == null || strColValue2.equals(""))) {
								break;
							}
							if ((strColValue1 != null && !strColValue1.equals(""))) {
								intTempValue1 = Integer.valueOf(strColValue1);
							}
							if ((strColValue2 != null && !strColValue2.equals(""))) {
								intTempValue2 = Integer.valueOf(strColValue2);
							}
							/* 将1000移动到开始的地方 修改人:sg 修改日期:2017-12-19 begin */
							strTotalAmt = String.valueOf(1000 * Double.valueOf((intTempValue1 + intTempValue2))
									* Double.valueOf(itemDen.getUnitVal().toString()));
							/* end */
						}
						break;
					}
				}
			}
			DenomInfo.setColumnValue1((String) strColValue1);
			DenomInfo.setColumnValue2((String) strColValue2);
			DenomInfo.setTotalAmt(strTotalAmt);
			DenomList.add(DenomInfo);
		}
		return DenomList;
	}

	/**
	 * 商行出款 面值列表的数据取得(ATM(捆))
	 * 
	 * @author wxz
	 * @version 2017年8月25日
	 * @param
	 * @return
	 */
	public static List<DenominationInfo> getDenominationOutList(List<?> plist, DenominationCtrl pDenomCtrl) {
		List<DenominationInfo> DenomList = Lists.newArrayList();

		StoDict stoDict = new StoDict();
		stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		List<StoDict> denDictList = stoDictService.findList(stoDict);

		for (StoDict itemDen : denDictList) {
			DenominationInfo DenomInfo = new DenominationInfo();
			DenomInfo.setMoneyKey(itemDen.getValue());
			DenomInfo.setMoneyName(itemDen.getLabel());
			DenomInfo.setMoneyValue(String.valueOf(itemDen.getUnitVal()));
			String strValueKey = "";
			String strTotalAmt = "";
			String strColValue1 = "", strColValue2 = "", strColValue3 = "";
			int intTempValue1 = 0, intTempValue2 = 0, intTempValue3 = 0;

			if (plist != null && pDenomCtrl != null) {
				for (int i = 0; i < plist.size(); i++) {
					strValueKey = (String) getFieldValueByName(pDenomCtrl.getMoneyKeyName(), plist.get(i));
					if (itemDen.getValue().equals(strValueKey)) {
						// 第一列捆数取得
						strColValue1 = (String) getFieldValueByName(pDenomCtrl.getColumnName1(), plist.get(i));
						// 第二列捆数取得
						strColValue2 = (String) getFieldValueByName(pDenomCtrl.getColumnName2(), plist.get(i));
						// 第三列捆数取得
						strColValue3 = (String) getFieldValueByName(pDenomCtrl.getColumnName3(), plist.get(i));

						// 金额的取得
						if ((pDenomCtrl.getTotalAmtName() != null && !pDenomCtrl.getTotalAmtName().equals(""))) {
							strTotalAmt = getFieldValueByName(pDenomCtrl.getTotalAmtName(), plist.get(i));
						} else {
							if ((strColValue1 == null || strColValue1.equals(""))
									&& (strColValue2 == null || strColValue2.equals(""))
									&& (strColValue3 == null || strColValue3.equals(""))) {
								break;
							}
							if ((strColValue1 != null && !strColValue1.equals(""))) {
								intTempValue1 = Integer.valueOf(strColValue1);
							}
							if ((strColValue2 != null && !strColValue2.equals(""))) {
								intTempValue2 = Integer.valueOf(strColValue2);
							}
							if ((strColValue3 != null && !strColValue3.equals(""))) {
								intTempValue3 = Integer.valueOf(strColValue3);
							}
							strTotalAmt = String.valueOf(Double.valueOf((intTempValue1 + intTempValue2 + intTempValue3))
									* Double.valueOf(itemDen.getUnitVal().toString()) * 1000);
						}
						break;
					}
				}
			}
			DenomInfo.setColumnValue1((String) strColValue1);
			DenomInfo.setColumnValue2((String) strColValue2);
			DenomInfo.setColumnValue3((String) strColValue3);
			DenomInfo.setTotalAmt(strTotalAmt);
			DenomList.add(DenomInfo);
		}
		return DenomList;
	}

	/**
	 * 代理上缴 面值列表的数据取得()
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param
	 * @return
	 */
	public static List<DenominationInfo> getDenominationAgencyPayCtrlList(List<?> plist, DenominationCtrl pDenomCtrl) {
		List<DenominationInfo> DenomList = Lists.newArrayList();

		StoDict stoDict = new StoDict();
		stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
		List<StoDict> denDictList = stoDictService.findList(stoDict);

		for (StoDict itemDen : denDictList) {
			DenominationInfo DenomInfo = new DenominationInfo();
			DenomInfo.setMoneyKey(itemDen.getValue());
			DenomInfo.setMoneyName(itemDen.getLabel());
			DenomInfo.setMoneyValue(String.valueOf(itemDen.getUnitVal()));
			String strValueKey = "";
			String strTotalAmt = "";
			String strColValue1 = "", strColValue2 = "", strColValue3 = "", strColValue4 = "";
			int intTempValue1 = 0, intTempValue2 = 0, intTempValue3 = 0, intTempValue4 = 0;

			if (plist != null && pDenomCtrl != null) {
				for (int i = 0; i < plist.size(); i++) {
					strValueKey = (String) getFieldValueByName(pDenomCtrl.getMoneyKeyName(), plist.get(i));
					if (itemDen.getValue().equals(strValueKey)) {
						// 第一列捆数取得
						strColValue1 = (String) getFieldValueByName(pDenomCtrl.getColumnName1(), plist.get(i));
						// 第二列捆数取得
						strColValue2 = (String) getFieldValueByName(pDenomCtrl.getColumnName2(), plist.get(i));
						// 第三列捆数取得
						strColValue3 = (String) getFieldValueByName(pDenomCtrl.getColumnName3(), plist.get(i));
						// 第四列捆数取得
						strColValue4 = (String) getFieldValueByName(pDenomCtrl.getColumnName4(), plist.get(i));
						// 金额的取得
						if ((pDenomCtrl.getTotalAmtName() != null && !pDenomCtrl.getTotalAmtName().equals(""))) {
							strTotalAmt = (String) getFieldValueByName(pDenomCtrl.getTotalAmtName(), plist.get(i));
						} else {
							if ((strColValue1 == null || strColValue1.equals(""))
									&& (strColValue2 == null || strColValue2.equals(""))
									&& (strColValue3 == null || strColValue3.equals(""))
									&& (strColValue4 == null || strColValue4.equals(""))) {
								break;
							}
							if ((strColValue1 != null && !strColValue1.equals(""))) {
								intTempValue1 = Integer.valueOf(strColValue1);
							}
							if ((strColValue2 != null && !strColValue2.equals(""))) {
								intTempValue2 = Integer.valueOf(strColValue2);
							}
							if ((strColValue3 != null && !strColValue3.equals(""))) {
								intTempValue3 = Integer.valueOf(strColValue3);
							}
							if ((strColValue4 != null && !strColValue4.equals(""))) {
								intTempValue4 = Integer.valueOf(strColValue4);
							}
							strTotalAmt = String.valueOf(
									Double.valueOf((intTempValue1 + intTempValue2 + intTempValue3 + intTempValue4))
											* Double.valueOf(itemDen.getUnitVal().toString()) * 1000);
						}
						break;
					}
				}
			}
			DenomInfo.setColumnValue1((String) strColValue1);
			DenomInfo.setColumnValue2((String) strColValue2);
			DenomInfo.setColumnValue3((String) strColValue3);
			DenomInfo.setColumnValue4((String) strColValue4);
			DenomInfo.setTotalAmt(strTotalAmt);
			DenomList.add(DenomInfo);
		}
		return DenomList;
	}

	/**
	 * 根据属性名获取属性值
	 * 
	 * @author wanglin
	 * @version 2017年8月22日
	 * @param
	 * @return
	 */
	private static String getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			if (value != null) {
				return value.toString();
			} else {
				return "";
			}
		} catch (Exception e) {

			return "";
		}
	}

	/**
	 * 获取分组名称集合（'':所有；0：启用；1：停用）
	 * 
	 * @author XL
	 * @version 2017年8月21日
	 * @param groupStatus
	 * @param groupType
	 * @return 清分组列表
	 */
	public static List<ClearingGroup> getClearingGroupName(String groupStatus, String groupType) {
		ClearingGroup clearingGroup = new ClearingGroup();
		// 设置清分组状态
		clearingGroup.setGroupStatus(groupStatus);
		// 设置清分组业务类型
		clearingGroup.setGroupType(groupType);
		User userInfo = UserUtils.getUser();
		clearingGroup.setOffice(userInfo.getOffice());

		return clearingGroupService.findList(clearingGroup);
	}

	/**
	 * 通过id获取单条清分组详细信息
	 * 
	 * @author qph
	 * @version 2017年8月25日
	 * @param id
	 * @return
	 */
	public static ClearingGroupDetail getClGroupDetailById(String id) {
		return clearingGroupService.getClGroupDetailById(id);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 账务余额判断
	 * 
	 * @param businessType
	 * @param clientId
	 * @param outAmount
	 * @return
	 */
	public static String getNewestStoreInfo(String businessType, String clientId, BigDecimal outAmount) {
		return centerAccountsMainService.getNewestStoreInfo(businessType, clientId, outAmount);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 账务主表以及账务明细表添加
	 * 
	 * @param centerAccountsMain
	 */
	public static void insertAccounts(CenterAccountsMain centerAccountsMain) {
		centerAccountsMainService.insertAccounts(centerAccountsMain);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心账务日结
	 *
	 */
	public static void dayCenterAccountsReport(String windupType, DayReportMain dayReportMain, Office office) {
		dayReportMainService.dayAccountReportByCenter(windupType, dayReportMain, office);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 客户账务日结
	 *
	 */
	public static void dayGuestAccountsReport(String windupType, DayReportMain dayReportMain, Office office) {
		dayReportGuestService.dayAccountReportByGuest(windupType, dayReportMain, office);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月11日 获取今天之前最近一次账务日结时间
	 * 
	 * @return
	 */
	public static Date getDayReportMaxDate(Office office) {
		return dayReportMainService.getDayReportMaxDate(office);
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月19日 验证分配数量
	 * 
	 * @param clTaskMain
	 */

	public static void checkCount(ClTaskMain clTaskMain) {
		clTaskMainService.checkCount(clTaskMain);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月20日 验证回收数量
	 * 
	 * @param clTaskMain
	 */

	public static void checkRecovery(ClTaskMain clTaskMain) {
		clTaskRecoveryService.checkRecovery(clTaskMain);
	}

	/**
	 * 柜员账务主表添加
	 * 
	 * @author XL
	 * @version 2017年10月23日
	 * @param tellerAccountsMain
	 */
	public static void insertTellerAccounts(TellerAccountsMain tellerAccountsMain) {
		tellerAccountsMainService.insertTellerAccounts(tellerAccountsMain);
	}

	/**
	 * 根据用户Id，金额类型查询柜员账务
	 * 
	 * @author XL
	 * @version 2017年10月25日
	 * @param EscortInfoId
	 * @param cashType
	 * @return 柜员账务信息
	 */
	public static TellerAccountsMain getNewestTellerAccounts(String EscortInfoId, String cashType) {
		return tellerAccountsMainService.getNewestTellerAccounts(EscortInfoId, cashType);
	}

	/**
	 *
	 * @author qipeihong
	 * @version 2017年10月25日 获取最新一条柜员帐
	 * 
	 * @param EscortInfoId
	 * @param cashType
	 * @return
	 */

	public static List<TellerAccountsMain> getNewestTellerAccounts(String EscortInfoId) {
		return tellerAccountsMainService.getNewestTellerAccounts(EscortInfoId);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年11月23日
	 * 
	 * 
	 * @param EscortInfoId
	 * @param groupType
	 * @param delFlag
	 * @return
	 */

	public static ClearingGroupDetail getClearGroupByUserId(String clearGroupId, String EscortInfoId, String groupType,
			String delFlag) {
		return clearingGroupService.getClGroupDetailByUser(clearGroupId, EscortInfoId, groupType, delFlag);
	}
	
	/**
	 * 面值（硬币）列表的数据取得
	 *
	 * @author XL
	 * @version 2019年8月7日
	 * @return
	 */
	public static List<DenominationInfo> getCnyhdenList() {
		return getDenominationList(null, null);
	}

	/**
	 * 面值（硬币）列表的数据取得
	 *
	 * @author XL
	 * @version 2019年8月7日
	 * @param plist
	 * @param pDenomCtrl
	 * @return
	 */
	public static List<DenominationInfo> getCnyhdenList(List<?> plist, DenominationCtrl pDenomCtrl) {
		List<DenominationInfo> DenomList = Lists.newArrayList();
		StoDict stoDict = new StoDict();
		stoDict.setType(Constant.DenominationType.RMB_HDEN); // 人民币：硬币
		List<StoDict> denDictList = stoDictService.findList(stoDict);

		for (StoDict itemDen : denDictList) {
			DenominationInfo DenomInfo = new DenominationInfo();
			DenomInfo.setMoneyKey(itemDen.getValue());
			DenomInfo.setMoneyName(itemDen.getLabel());
			DenomInfo.setMoneyValue(String.valueOf(itemDen.getUnitVal()));
			String strValueKey = "";
			String strTotalAmt = "";
			String strColValue1 = "", strColValue2 = "";
			int intTempValue1 = 0, intTempValue2 = 0;

			if (plist != null && pDenomCtrl != null) {
				for (int i = 0; i < plist.size(); i++) {
					strValueKey = (String) getFieldValueByName(pDenomCtrl.getMoneyKeyName(), plist.get(i));
					if (itemDen.getValue().equals(strValueKey)) {
						// 第一列捆数取得
						strColValue1 = (String) getFieldValueByName(pDenomCtrl.getColumnName1(), plist.get(i));
						// 第二列捆数取得
						strColValue2 = (String) getFieldValueByName(pDenomCtrl.getColumnName2(), plist.get(i));

						// 金额的取得
						if ((pDenomCtrl.getTotalAmtName() != null && !pDenomCtrl.getTotalAmtName().equals(""))) {
							strTotalAmt = getFieldValueByName(pDenomCtrl.getTotalAmtName(), plist.get(i));
						} else {
							if ((strColValue1 == null || strColValue1.equals(""))
									&& (strColValue2 == null || strColValue2.equals(""))) {
								break;
							}
							if ((strColValue1 != null && !strColValue1.equals(""))) {
								intTempValue1 = Integer.valueOf(strColValue1);
							}
							if ((strColValue2 != null && !strColValue2.equals(""))) {
								intTempValue2 = Integer.valueOf(strColValue2);
							}
							/* 将1000移动到开始的地方 修改人:sg 修改日期:2017-12-19 begin */
							strTotalAmt = String.valueOf(1000 * Double.valueOf((intTempValue1 + intTempValue2))
									* Double.valueOf(itemDen.getUnitVal().toString()));
							/* end */
						}
						break;
					}
				}
			}
			DenomInfo.setColumnValue1((String) strColValue1);
			DenomInfo.setColumnValue2((String) strColValue2);
			DenomInfo.setTotalAmt(strTotalAmt);
			DenomList.add(DenomInfo);
		}
		return DenomList;
	}

}
