package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportGuestDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsDetail;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportGuest;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 账务管理Service
 * 
 * @author QPH
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class DoorCenterAccountsMainService extends CrudService<DoorCenterAccountsMainDao, DoorCenterAccountsMain> {

	@Autowired
	private DoorCenterAccountsMainDao centerAccountsMainDao;
	@Autowired
	private DoorDayReportMainDao dayReportMainDao;
	@Autowired
	private DoorDayReportGuestDao dayReportGuestDao;
	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	/** 上门收款业务类型 */
	private static String[] cashBusinessType = { "74", "78", "79", "80", "81", "82" };

	/** 备份金业务类型 */
	private static String[] proBusinessType = { "06", "07", "70" };

	public DoorCenterAccountsMain get(String id) {
		return super.get(id);
	}

	public List<DoorCenterAccountsMain> findList(DoorCenterAccountsMain centerAccountsMain) {
		return super.findList(centerAccountsMain);
	}

	public DoorCenterAccountsMain findSumList(DoorCenterAccountsMain centerAccountsMain) {
		return dao.findSumList(centerAccountsMain);
	}

	public List<DoorCenterAccountsMain> findListGroupByBusinessType(DoorCenterAccountsMain centerAccountsMain) {
		return centerAccountsMainDao.findListGroupByBusinessType(centerAccountsMain);
	}

	/**
	 * 查询中心总账列表
	 * 
	 * @author XL
	 * @version 2017年9月12日
	 * @param centerAccountsMain
	 * @param type
	 */
	public List<DoorCenterAccountsMain> findList(DoorCenterAccountsMain centerAccountsMain, String type) {
		// 查询条件： 开始时间
		if (centerAccountsMain.getCreateTimeStart() != null) {
			centerAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(centerAccountsMain.getCreateTimeStart())));
		}
		/** 初始化查询开始时间默认上月的今天(优化查询速度) add by lihe 2020-05-21 */
		if (centerAccountsMain.getCreateTimeStart() == null && !"0".equals(centerAccountsMain.getUninitDateFlag())) {
			centerAccountsMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("month")));
			centerAccountsMain.setCreateTimeStart(DateUtils.parseDate(centerAccountsMain.getSearchDateStart()));
		}
		// 查询条件： 结束时间
		if (centerAccountsMain.getCreateTimeEnd() != null) {
			centerAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(centerAccountsMain.getCreateTimeEnd())));
		}
		if (centerAccountsMain.getBusinessTypes() == null) {
			List<String> busTypelist = Lists.newArrayList();
			if (!StringUtils.isBlank(type)) {
				if (type.equals(ClearConstant.AccountCashProType.CASH)) {
					// 现金业务
					busTypelist = Arrays.asList(cashBusinessType);
				} else {
					// 备付金业务
					busTypelist = Arrays.asList(proBusinessType);
				}
			}
			// 设置业务类型列表
			centerAccountsMain.setBusinessTypes(busTypelist);
		}
		return dao.findList(centerAccountsMain);
	}

	/**
	 * 查询客户总账列表
	 * 
	 * @author lihe
	 * @version 2020年5月27日
	 * @param centerAccountsMain
	 * @param type
	 */
	public List<DoorCenterAccountsMain> findGuestAccountsList(DoorCenterAccountsMain centerAccountsMain, String type) {
		// 查询条件： 开始时间
		if (centerAccountsMain.getCreateTimeStart() != null) {
			centerAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(centerAccountsMain.getCreateTimeStart())));
		}
		/** 初始化查询开始时间默认上月的今天(优化查询速度) add by lihe 2020-05-21 */
		if (centerAccountsMain.getCreateTimeStart() == null && !"0".equals(centerAccountsMain.getUninitDateFlag())) {
			centerAccountsMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("month")));
			centerAccountsMain.setCreateTimeStart(DateUtils.parseDate(centerAccountsMain.getSearchDateStart()));
		}
		// 查询条件： 结束时间
		if (centerAccountsMain.getCreateTimeEnd() != null) {
			centerAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(centerAccountsMain.getCreateTimeEnd())));
		}
		if (centerAccountsMain.getBusinessTypes() == null) {
			List<String> busTypelist = Lists.newArrayList();
			if (!StringUtils.isBlank(type)) {
				if (type.equals(ClearConstant.AccountCashProType.CASH)) {
					// 现金业务
					busTypelist = Arrays.asList(cashBusinessType);
				} else {
					// 备付金业务
					busTypelist = Arrays.asList(proBusinessType);
				}
			}
			// 设置业务类型列表
			centerAccountsMain.setBusinessTypes(busTypelist);
		}
		return dao.findGuestAccountsList(centerAccountsMain);
	}

	/**
	 * 中心总账列表
	 * 
	 * @author XL
	 * @version 2017年9月12日
	 * @param page
	 * @param centerAccountsMain
	 * @param type
	 */
	public Page<DoorCenterAccountsMain> findPage(Page<DoorCenterAccountsMain> page,
			DoorCenterAccountsMain centerAccountsMain, String type) {
		centerAccountsMain.setPage(page);
		page.setList(findList(centerAccountsMain, type));
		return page;
	}

	/**
	 * 客户总账列表
	 * 
	 * @author lihe
	 * @version 2020年5月27日
	 * @param page
	 * @param centerAccountsMain
	 * @param type
	 */
	public Page<DoorCenterAccountsMain> findGuestAccountsPage(Page<DoorCenterAccountsMain> page,
			DoorCenterAccountsMain centerAccountsMain, String type) {
		centerAccountsMain.setPage(page);
		page.setList(findGuestAccountsList(centerAccountsMain, type));
		return page;
	}

	/**
	 * 中心总账列表(按商户分组查询)
	 * 
	 * @author WQJ
	 * @version 2019年7月29日
	 * @param page
	 * @param centerAccountsMain
	 * @param type
	 */
	public Page<DoorCenterAccountsMain> findPageByMerchant(Page<DoorCenterAccountsMain> page,
			DoorCenterAccountsMain centerAccountsMain, String type) {
		centerAccountsMain.setPage(page);
		page.setList(findListByMerchant(centerAccountsMain, type));
		return page;
	}

	/**
	 * 查询中心总账列表(按商户分组查询)
	 * 
	 * @author WQJ
	 * @version 2019年7月29日
	 * @param page
	 * @param centerAccountsMain
	 * @param type
	 */
	public List<DoorCenterAccountsMain> findListByMerchant(DoorCenterAccountsMain centerAccountsMain, String type) {
		if (centerAccountsMain.getBusinessTypes() == null) {
			List<String> busTypelist = Lists.newArrayList();
			if (!StringUtils.isBlank(type)) {
				if (type.equals(ClearConstant.AccountCashProType.CASH)) {
					// 现金业务
					busTypelist = Arrays.asList(cashBusinessType);
				} else {
					// 备付金业务
					busTypelist = Arrays.asList(proBusinessType);
				}
			}
			// 设置业务类型列表
			centerAccountsMain.setBusinessTypes(busTypelist);
		}
		List<DoorCenterAccountsMain> doorCenterAccountsMains = dao.findListByMerchant(centerAccountsMain);
		// 计算商户总金额
		for (DoorCenterAccountsMain doorCenterAccountsMain : doorCenterAccountsMains) {
			doorCenterAccountsMain.setGuestTotalAmount(new BigDecimal(0));
			if (doorCenterAccountsMain.getInAmount() != null) {
				doorCenterAccountsMain.setGuestTotalAmount(
						doorCenterAccountsMain.getGuestTotalAmount().add(doorCenterAccountsMain.getInAmount()));
			}
			if (doorCenterAccountsMain.getOutAmount() != null) {
				doorCenterAccountsMain.setGuestTotalAmount(
						doorCenterAccountsMain.getGuestTotalAmount().subtract(doorCenterAccountsMain.getOutAmount()));
			}
		}
		return doorCenterAccountsMains;
	}

	/**
	 * 查询中心总账列表
	 * 
	 * @author
	 * @version 2017年9月12日
	 * @param centerAccountsMain
	 */
	public Page<DoorCenterAccountsMain> findPageByDetail(Page<DoorCenterAccountsMain> page,
			DoorCenterAccountsMain centerAccountsMain, String type) {
		centerAccountsMain.setPage(page);
		if (centerAccountsMain.getBusinessTypes() == null) {
			List<String> busTypelist = Lists.newArrayList();
			if (!StringUtils.isBlank(type)) {
				if (type.equals(ClearConstant.AccountCashProType.CASH)) {
					// 现金业务
					busTypelist = Arrays.asList(cashBusinessType);
				} else {
					// 备付金业务
					busTypelist = Arrays.asList(proBusinessType);
				}
			}
			centerAccountsMain.setBusinessTypes(busTypelist);
		}
		/** 优化查询，注释原查询方法 lihe 2020-05-25 start */
		// page.setList(dao.findList(centerAccountsMain));
		page.setList(dao.getDetailByReportId(centerAccountsMain));
		/** 优化查询，注释原查询方法 lihe 2020-05-25 end */
		return page;
	}

	@Transactional(readOnly = false)
	public void save(DoorCenterAccountsMain centerAccountsMain) {
		super.save(centerAccountsMain);
	}

	@Transactional(readOnly = false)
	public void delete(DoorCenterAccountsMain centerAccountsMain) {
		super.delete(centerAccountsMain);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 账务主表以及账务明细表
	 * 
	 * @param centerAccountsMain
	 */
	@Transactional(readOnly = false)
	public synchronized void insertAccounts(DoorCenterAccountsMain centerAccountsMain) {
		// 验证账务表所需参数
		this.checkAccountsParameter(centerAccountsMain);
		// // 获取客户账务余额
		// BigDecimal guestTotalAmount =
		// this.getGuestTotalAmount(centerAccountsMain);
		// // 获取中心账务余额
		// BigDecimal centerTotalAmount = this.getCenterTotalAmount(new
		// DoorCenterAccountsMain(),
		// centerAccountsMain.getRofficeId(),
		// centerAccountsMain.getBusinessType());
		// 计算客户账务余额以及中心账务余额
		// if
		// (centerAccountsMain.getInoutType().equals(ClearConstant.AccountsInoutType.ACCOUNTS_IN))
		// {
		// guestTotalAmount =
		// guestTotalAmount.add(centerAccountsMain.getInAmount());
		// centerTotalAmount =
		// centerTotalAmount.add(centerAccountsMain.getInAmount());
		// } else {
		// guestTotalAmount =
		// guestTotalAmount.subtract(centerAccountsMain.getOutAmount());
		// centerTotalAmount =
		// centerTotalAmount.subtract(centerAccountsMain.getOutAmount());
		// }
		// 设置客户名称
		centerAccountsMain.setClientName(StoreCommonUtils.getOfficeById(centerAccountsMain.getClientId()).getName());
		// 设置账务发生机构名称以及账务所属机构名称
		centerAccountsMain.setRofficeName(StoreCommonUtils.getOfficeById(centerAccountsMain.getRofficeId()).getName());
		centerAccountsMain.setAofficeName(StoreCommonUtils.getOfficeById(centerAccountsMain.getAofficeId()).getName());
		// 设置账务主表主键
		centerAccountsMain.setAccountsId(IdGen.uuid());
		// // 设置中心账务余额
		// centerAccountsMain.setTotalAmount(centerTotalAmount);
		// // 设置客户账务余额
		// centerAccountsMain.setGuestTotalAmount(guestTotalAmount);
		// 有效标识
		centerAccountsMain.setDelFlag(ClearConstant.deleteFlag.Valid);
		centerAccountsMain.preInsert();
		// 账务主表
		int insertResult = centerAccountsMainDao.insert(centerAccountsMain);
		if (insertResult == 0) {
			String strMessageContent = "账务主表：" + centerAccountsMain.getAccountsId() + "更新失败！";
			logger.error(strMessageContent);
			throw new BusinessException("message.E7700", "", new String[] { centerAccountsMain.getAccountsId() });
		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月4日
	 * 
	 *          获取最新账务信息，根据业务类型以及客户来判断
	 * @param businessType,clientId,outAmount
	 * @return
	 */
	@Transactional(readOnly = false)
	public String getNewestStoreInfo(String businessType, String clientId, BigDecimal outAmount, String rofficeId) {
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		// 设置业务类型
		if (cashList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(cashList);
		} else if (proList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(proList);
		}
		BigDecimal amount = new BigDecimal(0);

		// 设置客户ID
		centerAccountsMain.setClientId(clientId);
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(rofficeId);
		// 通过业务类型以及客户ID获取信息
		List<DoorCenterAccountsMain> accountList = centerAccountsMainDao.getAccountByBusinessType(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			DoorCenterAccountsMain centerMain = accountList.get(0);
			amount = amount.add(centerMain.getGuestTotalAmount());
		} else {
			// 获取该客户昨天结算的余额
			BigDecimal amountByGuest = this.getTotalAmountByBusinessType(businessType, clientId, rofficeId);
			if (amountByGuest != null) {
				amount = amountByGuest.add(amount);
			} else {
				return Constant.FAILED;
			}
		}
		if (outAmount.compareTo(amount) > 0) {
			return Constant.FAILED;
		} else {
			return Constant.SUCCESS;

		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 获取该客户该业务类型下的期末剩余
	 * 
	 * @param businessType
	 * @param clientId
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getTotalAmountByBusinessType(String businessType, String clientId, String rofficeId) {
		DoorDayReportGuest dayReportGuest = new DoorDayReportGuest();
		List<String> accountsList = Lists.newArrayList();
		if (DoorOrderConstant.BusinessType.CENTER_PAID.equals(businessType)
				|| DoorOrderConstant.BusinessType.DOOR_ORDER.equals(businessType)) {
			// 设置账务类型为上门收款业务
			accountsList.add(DoorOrderConstant.AccountsType.ACCOUNTS_DOOR);
		}
		// 设置账务类型
		dayReportGuest.setAccountsTypes(accountsList);
		// 设置客户ID
		dayReportGuest.setClientId(clientId);
		// 设置账务发生机构
		dayReportGuest.setOffice(StoreCommonUtils.getOfficeById(rofficeId));
		List<DoorDayReportGuest> dayReportList = dayReportGuestDao.getAccountByAccountsType(dayReportGuest);
		if (!Collections3.isEmpty(dayReportList)) {
			return dayReportList.get(0).getTotalAmount();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 获取客户剩余金额
	 * 
	 * @param centerAccountsMain
	 * @return
	 */

	public BigDecimal getGuestTotalAmount(DoorCenterAccountsMain centerAccountsMain) {
		// 客户总金额
		BigDecimal guestTotalAmount = new BigDecimal(0);
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		List<String> accountsTypeList = Lists.newArrayList();
		// 设置业务类型
		if (cashList.contains(centerAccountsMain.getBusinessType())) {
			centerAccountsMain.setBusinessTypes(cashList);
			accountsTypeList.add(DoorOrderConstant.AccountsType.ACCOUNTS_DOOR);
		} else if (proList.contains(centerAccountsMain.getBusinessType())) {
			centerAccountsMain.setBusinessTypes(proList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		}
		// 通过业务类型以及客户ID获取信息
		List<DoorCenterAccountsMain> accountList = centerAccountsMainDao
				.getAccountByBusinessTypeForClient(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			guestTotalAmount = accountList.get(0).getGuestTotalAmount();
			return guestTotalAmount;
		} else {
			return new BigDecimal(0);
		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 获取中心剩余金额
	 * 
	 * @param centerAccountsMain
	 * @return
	 */

	private BigDecimal getCenterTotalAmount(DoorCenterAccountsMain centerAccountsMain, String rofficeId,
			String businessType) {
		// 中心总金额
		BigDecimal totalAmount = new BigDecimal(0);
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		// 中心日结
		DoorDayReportCenter dayReportmain = new DoorDayReportCenter();
		List<String> accountsTypeList = Lists.newArrayList();
		// 设置业务类型
		if (cashList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(cashList);
			accountsTypeList.add(DoorOrderConstant.AccountsType.ACCOUNTS_DOOR);
		} else if (proList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(proList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		}
		// 设置开始时间为上一次日结时间
		centerAccountsMain.setSearchDateStart(DateUtils
				.formatDateTime(DoorCommonUtils.getDayReportMaxDate(StoreCommonUtils.getOfficeById(rofficeId))));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 设置账务发生机构
		// User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(rofficeId);
		// 通过业务类型以及客户ID获取信息
		List<DoorCenterAccountsMain> accountList = centerAccountsMainDao
				.getAccountByBusinessTypeForCenter(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			totalAmount = accountList.get(0).getTotalAmount();
			return totalAmount;
		} else {
			// 设置中心日结表账务类型
			dayReportmain.setAccountsTypes(accountsTypeList);
			//// 设置结账日期为昨日
			Date maxdate = DoorCommonUtils.getDayReportMaxDate(StoreCommonUtils.getOfficeById(rofficeId));
			// 获取昨日日结主表最后一条记录主键
			DoorDayReportMain dayReportMain = new DoorDayReportMain();

			List<DoorDayReportMain> dayReportList = Lists.newArrayList();
			if (maxdate != null) {
				// 设置开始时间
				dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
						ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				// 设置结束时间
				dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
						ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				// 设置账务发生机构
				dayReportMain.setOffice(StoreCommonUtils.getOfficeById(rofficeId));
				// 获取昨日日结总表数据
				dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
				// 获取日结总表最近一条中心日结数据
				List<DoorDayReportCenter> centerList = dayReportList.get(0).getDayReportCenterList();

				if (!Collections3.isEmpty(centerList)) {
					// 获取到离今天最近的一条中心账务日结金额作为期初余额
					for (DoorDayReportCenter center : centerList) {
						if (accountsTypeList.contains(center.getAccountsType())) {
							totalAmount = totalAmount.add(center.getTotalAmount());
						}
					}
					return totalAmount;
				} else {
					return new BigDecimal(0);
				}
			} else {
				return new BigDecimal(0);
			}
		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 验证账务主表所需参数是否为空
	 * 
	 * @param centerAccountsMain
	 */

	private void checkAccountsParameter(DoorCenterAccountsMain centerAccountsMain) {
		String result = "";
		// 验证账务出入库类型
		if (centerAccountsMain.getInoutType() == null) {
			result = Constant.FAILED;
		}
		// 验证业务类型
		if (centerAccountsMain.getBusinessType() == null) {
			result = Constant.FAILED;
		}
		// 验证对应业务流水
		if (centerAccountsMain.getBusinessId() == null) {
			result = Constant.FAILED;
		}
		// 验证客户ID
		if (centerAccountsMain.getClientId() == null) {
			result = Constant.FAILED;
		}
		// 验证出入库金额
		if (centerAccountsMain.getInAmount() == null && centerAccountsMain.getOutAmount() == null) {
			result = Constant.FAILED;
		}
		// 验证机构
		if (centerAccountsMain.getRofficeId() == null || centerAccountsMain.getAofficeId() == null) {
			result = Constant.FAILED;
		}
		// 验证账务明细
		if (Constant.FAILED.equals(result)) {
			throw new BusinessException("message.A1001", "", new String[] {});
		}

	}

	/**
	 * 
	 * @author wzj
	 * @version 2017年9月14日 客户业务账的数据查询
	 * 
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public DoorCenterAccountsMain businessAccount(DoorCenterAccountsMain centerAccountsMain) {

		// 查询客户名称对应的所有id
		String cId = centerAccountsMain.getClientId();
		String name = null;
		// 客户名称
		if (StringUtils.isNotBlank(centerAccountsMain.getClientName())) {
			name = centerAccountsMain.getClientName();
		}
		// 错误查询信息
		if (StringUtils.isBlank(cId)) {
			return null;
		}
		/* 判断是否选择清分机构 修改人:sg 修改日期:2017-12-08 begin */
		boolean check = true;
		/* end */
		DoorCenterAccountsMain centerAccount = new DoorCenterAccountsMain();
		centerAccount.setClientId(cId);
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		if (StringUtils.isBlank(centerAccountsMain.getRofficeId())) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			centerAccount.setRofficeId(userInfo.getOffice().getId());
			centerAccount.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			/* 没选择清分机构 修改人:sg 修改日期:2017-12-08 begin */
			check = false;
			/* end */
		} else {
			centerAccount.setRofficeId(centerAccountsMain.getRofficeId());
			centerAccount.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + centerAccountsMain.getRofficeId() + "%'");
		}
		/* end */
		// String currencys = null;
		// 根据所有id进行明细查询
		List<DoorCenterAccountsMain> accountList = centerAccountsMainDao.getDetailListByAccountsId(centerAccount);
		DoorCenterAccountsMain result = new DoorCenterAccountsMain();

		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		Map<String, BigDecimal> map = Maps.newHashMap();
		for (StoDict stoDict : stoDictList) {
			map.put(stoDict.getValue(), new BigDecimal(0));
		}

		// 总计
		BigDecimal totalMoney = new BigDecimal(0);
		// 机构号获得的所有数据及明细
		for (DoorCenterAccountsMain centerAccountsMain2 : accountList) {
			// 客户业务明细不统计备付金相关业务 LLF 20171114 begin
			StringBuilder exitSB = new StringBuilder();
			exitSB.append(ClearConstant.BusinessType.PROVISIONS_IN);
			exitSB.append(",");
			exitSB.append(ClearConstant.BusinessType.PROVISIONS_OUT);
			exitSB.append(",");
			exitSB.append(ClearConstant.BusinessType.ERROR_HANDLING);
			if (exitSB.toString().indexOf(centerAccountsMain2.getBusinessType()) == -1) {
				// end
				// 每个的明细数据
				List<DoorCenterAccountsDetail> accountsDetail = centerAccountsMain2.getCenterAccountsDetailList();

				for (DoorCenterAccountsDetail centerAccountsDetail : accountsDetail) {
					for (StoDict stoDict : stoDictList) {
						BigDecimal stoAmount = centerAccountsDetail.getTotalAmount();
						if (centerAccountsDetail.getDenomination().equals(stoDict.getValue())) {
							stoAmount = stoAmount.add(map.get(stoDict.getValue()));
							map.put(stoDict.getValue(), stoAmount);
							continue;
						}
					}
				}
			}
		}
		// 将数据拼接进行返回
		List<DoorCenterAccountsDetail> resultDates = Lists.newArrayList();
		Iterator<String> keyIterator = map.keySet().iterator();
		String strKey = "";
		// 循环物品列表
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			totalMoney = totalMoney.add(map.get(strKey));

		}
		for (StoDict stoDict : stoDictList) {
			DoorCenterAccountsDetail detail = new DoorCenterAccountsDetail();
			// 面值填充
			detail.setCurrency(DictUtils.getDictLabel("101", "currency", "人民币"));
			detail.setDenomination(stoDict.getValue());
			detail.setTotalAmount(map.get(stoDict.getValue()));
			resultDates.add(detail);
		}
		// 设置清分机构 ADD qph 2017-11-27
		/* 没选择清分机构界面不显示 修改人:sg 修改日期:2017-12-08 begin */
		if (check) {
			result.setRofficeId(centerAccount.getRofficeId());
			result.setRofficeName(SysCommonUtils.findOfficeById(centerAccount.getRofficeId()).getName());
		}
		/* end */
		result.setTotalAmount(totalMoney);
		result.setClientId(cId);
		result.setClientName(name);
		result.setCenterAccountsDetailList(resultDates);
		// 返回
		return result;
	}

	/**
	 * 
	 * @author wzj
	 * @version 2019年7月19日 查询未处理过的差错记录
	 * 
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> findUntakeErrorList(DoorCenterAccountsMain doorCenterAccountsMain) {
		return centerAccountsMainDao.findUntakeErrorList(doorCenterAccountsMain);
	}

	/**
	 * 获取门店日结汇总列表
	 *
	 * @author XL
	 * @version 2019年8月23日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DoorCenterAccountsMain> findDoorSummaryList(DoorCenterAccountsMain doorCenterAccountsMain) {
		return centerAccountsMainDao.findDoorSummaryList(doorCenterAccountsMain);
	}

	/**
	 * 通过商户获取门店日结汇总列表
	 *
	 * @author ZXK
	 * @version 2019年11月27日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DoorCenterAccountsMain> findDoorListByMerchan(DoorCenterAccountsMain doorCenterAccountsMain) {
		return centerAccountsMainDao.findDoorListByMerchan(doorCenterAccountsMain);
	}

	/**
	 * 门店日结导出列表
	 *
	 * @author XL
	 * @version 2019年10月30日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DayReportExport> findDayReportExportList(DoorCenterAccountsMain doorCenterAccountsMain) {
		// 业务类型
		doorCenterAccountsMain.setBusinessTypes(Arrays.asList(new String[] { DoorOrderConstant.BusinessType.DOOR_ORDER,
				DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE, DoorOrderConstant.BusinessType.TRADITIONAL_SAVE }));
		doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		// 业务状态
		doorCenterAccountsMain.setBusinessStatus(ClearConstant.StatusType.CREATE);
		return centerAccountsMainDao.findDayReportExportList(doorCenterAccountsMain);
	}

	/**
	 * 账务导出部分基础数据
	 *
	 * @author WQJ
	 * @version 2019年10月30日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DoorCenterAccountsMain> excelExporterList(DoorCenterAccountsMain doorCenterAccountsMain) {
		// 查询条件： 开始时间
		if (doorCenterAccountsMain.getCreateTimeStart() != null) {
			doorCenterAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(doorCenterAccountsMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorCenterAccountsMain.getCreateTimeEnd() != null) {
			doorCenterAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorCenterAccountsMain.getCreateTimeEnd())));
		}
		return centerAccountsMainDao.excelExporterList(doorCenterAccountsMain);
	}

	/**
	 * 账务导出部分追加处理
	 *
	 * @author WQJ
	 * @version 2019年11月2日
	 * @param doorCenterAccountsMain
	 * @return
	 */

	public List<DoorCenterAccountsMain> operaTion(List<DoorCenterAccountsMain> doorCenterAccountsMainList) {
		DoorOrderAmount doorOrderAmount = new DoorOrderAmount();
		for (DoorCenterAccountsMain doorCenterAccountsMain : doorCenterAccountsMainList) {
			// 对存款业务类型数据的业务类型，实际请点金额进行处理
			if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
				// 每笔存款ID
				String detailId = doorCenterAccountsMain.getDetailId();
				doorOrderAmount.setDetailId(detailId);
				// 现钞存款标志
				boolean cashFlag = false;
				// 封包存款标志
				boolean bagFlag = false;
				// 找到每笔存款的存款方式明细，并遍历
				List<DoorOrderAmount> doorOrderAmountsList = doorOrderAmountDao.findList(doorOrderAmount);
				for (DoorOrderAmount amount : doorOrderAmountsList) {
					// 有现钞存款
					if (amount.getTypeId().equals(DoorOrderConstant.SaveMethod.CASH_SAVE)) {
						cashFlag = true;
					}
					// 有封包存款
					if (amount.getTypeId().equals(DoorOrderConstant.SaveMethod.BAG_SAVE)) {
						bagFlag = true;
					}
				}
				// 既有现钞存款，也有封包存款
				if (cashFlag && bagFlag) {
					// 设置交易明细中业务类型为现钞存款
					doorCenterAccountsMain
							.setBusinessTypeReport(DictUtils.getDictLabel(DoorOrderConstant.SaveMethod.CASH_SAVE,
									DoorOrderConstant.SaveMethodType.SAVE_METHOD_TYPE, ""));
					// 设置交易明细中实际清点金额为总金额
					doorCenterAccountsMain.setRealClearAmount(doorCenterAccountsMain.getTotalMoney());
				}

				// 只有现钞存款
				if (cashFlag && !bagFlag) {
					// 设置交易明细中业务类型为现钞存款
					doorCenterAccountsMain
							.setBusinessTypeReport(DictUtils.getDictLabel(DoorOrderConstant.SaveMethod.CASH_SAVE,
									DoorOrderConstant.SaveMethodType.SAVE_METHOD_TYPE, ""));
					// 设置交易明细中实际清点金额为总金额
					doorCenterAccountsMain.setRealClearAmount(doorCenterAccountsMain.getTotalMoney());
				}
				// 只有封包存款
				if (!cashFlag && bagFlag) {
					// 设置交易明细中业务类型为封包存款
					doorCenterAccountsMain
							.setBusinessTypeReport(DictUtils.getDictLabel(DoorOrderConstant.SaveMethod.BAG_SAVE,
									DoorOrderConstant.SaveMethodType.SAVE_METHOD_TYPE, ""));
				}
			}
		}
		return doorCenterAccountsMainList;
	}

	/**
	 * 日结明细页计算合计数据
	 *
	 * @author WQJ
	 * @version 2019年11月13日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DoorCenterAccountsMain> dayReportTotal(DoorCenterAccountsMain doorCenterAccountsMain) {
		DoorCenterAccountsMain result = new DoorCenterAccountsMain();
		result.setReportId(doorCenterAccountsMain.getReportId());
		result.setClientId(doorCenterAccountsMain.getClientId());
		// 设置业务类型 计算不同业务类型合计 2020/04/14 hzy
		result.setBusinessType(doorCenterAccountsMain.getBusinessType());
		List<DoorCenterAccountsMain> resultList = Lists.newArrayList();
		result.setCash(BigDecimal.ZERO);
		result.setPack(BigDecimal.ZERO);
		result.setOther(BigDecimal.ZERO);
		result.setInAmount(BigDecimal.ZERO);
		result.setOutAmount(BigDecimal.ZERO);
		/** 优化查询，注释原查询方法 lihe 2020-05-25 start */
		// List<DoorCenterAccountsMain> list =
		// centerAccountsMainDao.findList(result);
		List<DoorCenterAccountsMain> list = centerAccountsMainDao.getDetailByReportId(result);
		/** 优化查询，注释原查询方法 lihe 2020-05-25 end */
		for (DoorCenterAccountsMain door : list) {
			result.setCash(result.getCash().add(door.getCash()));
			result.setPack(result.getPack().add(door.getPack()));
			result.setOther(result.getOther().add(door.getOther()));
			if (door.getInAmount() != null) {
				result.setInAmount(result.getInAmount().add(door.getInAmount()));
			}
			if (door.getOutAmount() != null) {
				result.setOutAmount(result.getOutAmount().add(door.getOutAmount()));
			}
		}
		result.setClientName("合计");
		resultList.add(result);
		return resultList;
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年12月17日 查询未代付的传统存款记录
	 * 
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> findNopaidTraditionalSave(DoorCenterAccountsMain doorCenterAccountsMain) {
		return centerAccountsMainDao.findNopaidTraditionalSave(doorCenterAccountsMain);
	}

	/**
	 *
	 * @author wqj
	 * @version 2020年3月5日 获取结算区间的存款总额，并按机具号汇总
	 *
	 * @param centerAccountsMain
	 * @return CenterAccountsMain
	 */
	public List<DoorCenterAccountsMain> getTotalAmountEquipId(DoorCenterAccountsMain doorCenterAccountsMain) {
		return centerAccountsMainDao.getTotalAmountEquipId(doorCenterAccountsMain);
	}

	/**
	 * 
	 * Title: getAmountByBusinessType
	 * <p>
	 * Description: 根据业务类型获取总金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param doorCenterAccountsMain
	 * @return List<DoorCenterAccountsMain> 返回类型
	 */
	public List<DoorCenterAccountsMain> getAmountByBusinessType(DoorCenterAccountsMain doorCenterAccountsMain,
			String type) {
		// 查询条件： 开始时间
		if (doorCenterAccountsMain.getCreateTimeStart() != null) {
			doorCenterAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(doorCenterAccountsMain.getCreateTimeStart())));
		}
		/** 初始化查询开始时间默认上月的今天(优化查询速度) add by lihe 2020-05-21 */
		if (doorCenterAccountsMain.getCreateTimeStart() == null
				&& !"0".equals(doorCenterAccountsMain.getUninitDateFlag())) {
			doorCenterAccountsMain
					.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("month")));
			doorCenterAccountsMain.setCreateTimeStart(DateUtils.parseDate(doorCenterAccountsMain.getSearchDateStart()));
		}
		// 查询条件： 结束时间
		if (doorCenterAccountsMain.getCreateTimeEnd() != null) {
			doorCenterAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorCenterAccountsMain.getCreateTimeEnd())));
		}
		if (doorCenterAccountsMain.getBusinessTypes() == null) {
			List<String> busTypelist = Lists.newArrayList();
			if (!StringUtils.isBlank(type)) {
				if (type.equals(ClearConstant.AccountCashProType.CASH)) {
					// 现金业务
					busTypelist = Arrays.asList(cashBusinessType);
				} else {
					// 备付金业务
					busTypelist = Arrays.asList(proBusinessType);
				}
			}
			// 设置业务类型列表
			doorCenterAccountsMain.setBusinessTypes(busTypelist);
		}
		return centerAccountsMainDao.getAmountByBusinessType(doorCenterAccountsMain);
	}

	/**
	 * 验证存款差错记录是否已被结算
	 *
	 * @author ZXK
	 * @version 2020-7-1
	 * @param isCheck
	 *            是否为拆箱记录
	 * @param orderId
	 *            凭条号(或拆箱号)
	 * @return List
	 */
	public List<DoorCenterAccountsMain> checkDepositErrorAccount(String orderId, boolean isCheck) {
		DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
		if (isCheck) {
			// 拆箱单号
			doorCenterAccountsMain.setCodeNo(orderId);
		} else {
			// 凭条号(流水号)
			doorCenterAccountsMain.setBusinessId(orderId);
		}
		// 业务类型
		doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		// 处理状态(除了未处理)
		// doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
		// 是否冲正
		// doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);

		return centerAccountsMainDao.checkDepositErrorAccount(doorCenterAccountsMain);
	}

}