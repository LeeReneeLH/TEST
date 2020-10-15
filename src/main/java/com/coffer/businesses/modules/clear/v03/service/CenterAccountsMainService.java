package com.coffer.businesses.modules.clear.v03.service;

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
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.CenterAccountsDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.CenterAccountsMainDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportGuestDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportMainDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsDetail;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportCenter;
import com.coffer.businesses.modules.clear.v03.entity.DayReportGuest;
import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
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
public class CenterAccountsMainService extends CrudService<CenterAccountsMainDao, CenterAccountsMain> {

	@Autowired
	private CenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	private CenterAccountsDetailDao centerAccountsDetailDao;

	@Autowired
	private DayReportMainDao dayReportMainDao;

	@Autowired
	private DayReportGuestDao dayReportGuestDao;

	/** 现金业务类型 */
	private static String[] cashBusinessType = { "01", "02", "03", "04", "05" };

	/** 备份金业务类型 */
	private static String[] proBusinessType = { "06", "07", "70" };

	public CenterAccountsMain get(String id) {
		return super.get(id);
	}

	public List<CenterAccountsMain> findList(CenterAccountsMain centerAccountsMain) {
		return super.findList(centerAccountsMain);
	}

	public CenterAccountsMain findSumList(CenterAccountsMain centerAccountsMain) {
		return dao.findSumList(centerAccountsMain);
	}

	public List<CenterAccountsMain> findListGroupByBusinessType(CenterAccountsMain centerAccountsMain) {
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
	public List<CenterAccountsMain> findList(CenterAccountsMain centerAccountsMain, String type) {
		// 查询条件： 开始时间
		if (centerAccountsMain.getCreateTimeStart() != null) {
			centerAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(centerAccountsMain.getCreateTimeStart())));
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
	 * 中心总账列表
	 * 
	 * @author XL
	 * @version 2017年9月12日
	 * @param page
	 * @param centerAccountsMain
	 * @param type
	 */
	public Page<CenterAccountsMain> findPage(Page<CenterAccountsMain> page, CenterAccountsMain centerAccountsMain,
			String type) {
		centerAccountsMain.setPage(page);
		page.setList(findList(centerAccountsMain, type));
		return page;
	}

	/**
	 * 查询中心总账列表
	 * 
	 * @author
	 * @version 2017年9月12日
	 * @param centerAccountsMain
	 */
	public Page<CenterAccountsMain> findPageByDetail(Page<CenterAccountsMain> page,
			CenterAccountsMain centerAccountsMain, String type) {
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
		page.setList(dao.findList(centerAccountsMain));
		return page;
	}

	@Transactional(readOnly = false)
	public void save(CenterAccountsMain centerAccountsMain) {
		super.save(centerAccountsMain);
	}

	@Transactional(readOnly = false)
	public void delete(CenterAccountsMain centerAccountsMain) {
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
	public synchronized void insertAccounts(CenterAccountsMain centerAccountsMain) {
		// 验证账务表所需参数
		this.checkAccountsParameter(centerAccountsMain);
		// 若账务出入库类型为出库业务
		if (centerAccountsMain.getInoutType().equals(ClearConstant.AccountsInoutType.ACCOUNTS_OUT)) {
			// 验证该客户是否存在余额
			String result = getNewestStoreInfo(centerAccountsMain.getBusinessType(), centerAccountsMain.getClientId(),
					centerAccountsMain.getOutAmount());
			// 余额不足
			if (result.equals(Constant.FAILED)) {
				if (ClearConstant.BusinessType.BANK_PAY.equals(centerAccountsMain.getBusinessType())
						|| ClearConstant.BusinessType.BANK_GET.equals(centerAccountsMain.getBusinessType())
						|| ClearConstant.BusinessType.AGENCY_PAY.equals(centerAccountsMain.getBusinessType())
						|| ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT
								.equals(centerAccountsMain.getBusinessType())
						|| ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN
								.equals(centerAccountsMain.getBusinessType())) {
					throw new BusinessException("message.A1000", "", new String[] {
							StoreCommonUtils.getOfficeById(centerAccountsMain.getClientId()).getName(), "现金" });
				} else {
					// 差错处理不验证柜员账务是否满足只需要记账即可 LLF 20171114
					// ClearConstant.BusinessType.ERROR_HANDLING.equals(tellerAccountsMain.getBussinessType())
					throw new BusinessException("message.A1000", "", new String[] {
							StoreCommonUtils.getOfficeById(centerAccountsMain.getClientId()).getName(), "备付金" });
				}
			}
		}

		// 获取客户账务余额
		BigDecimal guestTotalAmount = this.getGuestTotalAmount(centerAccountsMain);
		// 获取中心账务余额
		BigDecimal centerTotalAmount = this.getCenterTotalAmount(new CenterAccountsMain(),
				centerAccountsMain.getBusinessType());
		// 计算客户账务余额以及中心账务余额
		if (centerAccountsMain.getInoutType().equals(ClearConstant.AccountsInoutType.ACCOUNTS_IN)) {
			guestTotalAmount = guestTotalAmount.add(centerAccountsMain.getInAmount());
			centerTotalAmount = centerTotalAmount.add(centerAccountsMain.getInAmount());
		} else {
			guestTotalAmount = guestTotalAmount.subtract(centerAccountsMain.getOutAmount());
			centerTotalAmount = centerTotalAmount.subtract(centerAccountsMain.getOutAmount());
		}
		// 设置客户名称
		centerAccountsMain.setClientName(StoreCommonUtils.getOfficeById(centerAccountsMain.getClientId()).getName());
		// 设置账务发生机构名称以及账务所属机构名称
		centerAccountsMain.setRofficeName(StoreCommonUtils.getOfficeById(centerAccountsMain.getRofficeId()).getName());
		centerAccountsMain.setAofficeName(StoreCommonUtils.getOfficeById(centerAccountsMain.getAofficeId()).getName());
		// 设置账务主表主键
		centerAccountsMain.setAccountsId(IdGen.uuid());
		// 设置中心账务余额
		centerAccountsMain.setTotalAmount(centerTotalAmount);
		// 设置客户账务余额
		centerAccountsMain.setGuestTotalAmount(guestTotalAmount);
		// 账务明细表设置
		for (CenterAccountsDetail accountDetail : centerAccountsMain.getCenterAccountsDetailList()) {
			// 设置明细表主键
			accountDetail.setDetailId(IdGen.uuid());
			// 设置主表主键
			accountDetail.setAccountsId(centerAccountsMain.getAccountsId());
			// 有效标识
			accountDetail.setDelFlag(ClearConstant.deleteFlag.Valid);
			// 账务明细表插入
			int detailResult = centerAccountsDetailDao.insert(accountDetail);
			if (detailResult == 0) {
				String strMessageContent = "账务明细表：" + centerAccountsMain.getAccountsId() + "更新失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7701", "", new String[] { centerAccountsMain.getAccountsId() });
			}
		}

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
	public String getNewestStoreInfo(String businessType, String clientId, BigDecimal outAmount) {
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		// 验证是否需要验证备付金余额 修改人：qph 2017-12-21
		Office office = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
		if ((ClearConstant.clearProvisionsOpen.CLEARPROVISIONSOPEN_FALSE.equals(office.getProvisionsSwitch())
				|| StringUtils.isEmpty(office.getProvisionsSwitch()))
				&& proList.contains(businessType)) {
			return Constant.SUCCESS;
		}
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
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 通过业务类型以及客户ID获取信息
		List<CenterAccountsMain> accountList = centerAccountsMainDao.getAccountByBusinessType(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			CenterAccountsMain centerMain = accountList.get(0);
			amount = amount.add(centerMain.getGuestTotalAmount());
		} else {
			// 获取该客户昨天结算的余额
			BigDecimal amountByGuest = this.getTotalAmountByBusinessType(businessType, clientId);
			if (amountByGuest != null) {
				amount = amountByGuest.add(amount);
			} else {
				return Constant.FAILED;
			}
		}
		if (outAmount.compareTo(amount) == 1) {
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
	private BigDecimal getTotalAmountByBusinessType(String businessType, String clientId) {
		DayReportGuest dayReportGuest = new DayReportGuest();
		List<String> accountsList = Lists.newArrayList();
		if (ClearConstant.BusinessType.BANK_PAY.equals(businessType)
				|| ClearConstant.BusinessType.BANK_GET.equals(businessType)
				|| ClearConstant.BusinessType.AGENCY_PAY.equals(businessType)) {
			// 设置账务类型为清点业务
			accountsList.add(ClearConstant.AccountsType.ACCOUNTS_CLEAR);
		} else if (ClearConstant.BusinessType.PROVISIONS_IN.equals(businessType)
				|| ClearConstant.BusinessType.PROVISIONS_OUT.equals(businessType)
				|| ClearConstant.BusinessType.ERROR_HANDLING.equals(businessType)) {
			// 设置账务类型为备付金业务
			accountsList.add(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		}
		// 设置账务类型
		dayReportGuest.setAccountsTypes(accountsList);
		// 设置客户ID
		dayReportGuest.setClientId(clientId);
		// 设置账务发生机构
		User userInfo = UserUtils.getUser();
		dayReportGuest.setOffice(userInfo.getOffice());
		List<DayReportGuest> dayReportList = dayReportGuestDao.getAccountByAccountsType(dayReportGuest);
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

	private BigDecimal getGuestTotalAmount(CenterAccountsMain centerAccountsMain) {
		// 客户总金额
		BigDecimal guestTotalAmount = new BigDecimal(0);
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		// 客户日结
		DayReportGuest dayReportGuest = new DayReportGuest();
		List<String> accountsTypeList = Lists.newArrayList();
		// 设置业务类型
		if (cashList.contains(centerAccountsMain.getBusinessType())) {
			centerAccountsMain.setBusinessTypes(cashList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_CLEAR);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_COMPLEX);
		} else if (proList.contains(centerAccountsMain.getBusinessType())) {
			centerAccountsMain.setBusinessTypes(proList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		}
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 通过业务类型以及客户ID获取信息
		List<CenterAccountsMain> accountList = centerAccountsMainDao.getAccountByBusinessType(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			guestTotalAmount = accountList.get(0).getGuestTotalAmount();
			return guestTotalAmount;
		} else {
			// 设置客户日结表账务类型
			dayReportGuest.setAccountsTypes(accountsTypeList);
			// 设置客户ID
			dayReportGuest.setClientId(centerAccountsMain.getClientId());
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			dayReportGuest.setOffice(userInfo.getOffice());
			// 获取客户日结
			List<DayReportGuest> dayReportList = dayReportGuestDao.getAccountByAccountsType(dayReportGuest);
			if (!Collections3.isEmpty(dayReportList)) {
				// 将客户日结余额设置为余额
				guestTotalAmount = dayReportList.get(0).getTotalAmount();
				return guestTotalAmount;
			} else {
				return new BigDecimal(0);
			}
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

	private BigDecimal getCenterTotalAmount(CenterAccountsMain centerAccountsMain, String businessType) {
		// 中心总金额
		BigDecimal totalAmount = new BigDecimal(0);
		// 现金业务集合
		List<String> cashList = Arrays.asList(cashBusinessType);
		// 备付金业务集合
		List<String> proList = Arrays.asList(proBusinessType);
		// 中心日结
		DayReportCenter dayReportmain = new DayReportCenter();
		List<String> accountsTypeList = Lists.newArrayList();
		// 设置业务类型
		if (cashList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(cashList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_CLEAR);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_COMPLEX);
		} else if (proList.contains(businessType)) {
			centerAccountsMain.setBusinessTypes(proList);
			accountsTypeList.add(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		}
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 设置账务发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 通过业务类型以及客户ID获取信息
		List<CenterAccountsMain> accountList = centerAccountsMainDao.getAccountByBusinessType(centerAccountsMain);
		// 若当天有流水
		if (!Collections3.isEmpty(accountList)) {
			totalAmount = accountList.get(0).getTotalAmount();
			return totalAmount;
		} else {
			// 设置中心日结表账务类型
			dayReportmain.setAccountsTypes(accountsTypeList);
			//// 设置结账日期为昨日
			Date maxdate = ClearCommonUtils.getDayReportMaxDate(UserUtils.getUser().getOffice());
			// 获取昨日日结主表最后一条记录主键
			DayReportMain dayReportMain = new DayReportMain();

			List<DayReportMain> dayReportList = Lists.newArrayList();
			if (maxdate != null) {
				// 设置开始时间
				dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
						ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				// 设置结束时间
				dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
						ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
				// 设置账务发生机构
				dayReportMain.setOffice(userInfo.getOffice());
				// 获取昨日日结总表数据
				dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
				// 获取日结总表最近一条中心日结数据
				List<DayReportCenter> centerList = dayReportList.get(0).getDayReportCenterList();

				if (!Collections3.isEmpty(centerList)) {
					// 获取到离今天最近的一条中心账务日结金额作为期初余额
					for (DayReportCenter center : centerList) {
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

	private void checkAccountsParameter(CenterAccountsMain centerAccountsMain) {
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
		// 验证账务明细
		if (centerAccountsMain.getCenterAccountsDetailList() == null) {
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
		for (CenterAccountsDetail centerAccountsDetail : centerAccountsMain.getCenterAccountsDetailList()) {
			if (centerAccountsDetail.getCurrency() == null) {
				result = Constant.FAILED;
			}
			if (centerAccountsDetail.getDenomination() == null) {
				result = Constant.FAILED;
			}
			if (centerAccountsDetail.getUnit() == null) {
				result = Constant.FAILED;
			}
			if (centerAccountsDetail.getTotalCount() == null || centerAccountsDetail.getTotalAmount() == null) {
				result = Constant.FAILED;
			}
		}
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
	public CenterAccountsMain businessAccount(CenterAccountsMain centerAccountsMain) {

		// 查询客户名称对应的所有id
		String cId = centerAccountsMain.getClientId();
		String Name = null;
		// 客户名称
		if (StringUtils.isNotBlank(centerAccountsMain.getClientName())) {
			Name = centerAccountsMain.getClientName();
		}
		// 错误查询信息
		if (StringUtils.isBlank(cId)) {
			return null;
		}
		/* 判断是否选择清分机构 修改人:sg 修改日期:2017-12-08 begin */
		boolean check = true;
		/* end */
		CenterAccountsMain centerAccount = new CenterAccountsMain();
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
		List<CenterAccountsMain> accountList = centerAccountsMainDao.getDetailListByAccountsId(centerAccount);
		CenterAccountsMain result = new CenterAccountsMain();

		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		Map<String, BigDecimal> map = Maps.newHashMap();
		for (StoDict stoDict : stoDictList) {
			map.put(stoDict.getValue(), new BigDecimal(0));
		}

		// 总计
		BigDecimal totalMoney = new BigDecimal(0.0);
		// 机构号获得的所有数据及明细
		for (CenterAccountsMain centerAccountsMain2 : accountList) {
			// 客户业务明细不统计备付金相关业务 LLF 20171114 begin
			StringBuffer exitSB = new StringBuffer();
			exitSB.append(ClearConstant.BusinessType.PROVISIONS_IN);
			exitSB.append(",");
			exitSB.append(ClearConstant.BusinessType.PROVISIONS_OUT);
			exitSB.append(",");
			exitSB.append(ClearConstant.BusinessType.ERROR_HANDLING);
			if (exitSB.toString().indexOf(centerAccountsMain2.getBusinessType()) == -1) {
				// end
				// 每个的明细数据
				List<CenterAccountsDetail> accountsDetail = centerAccountsMain2.getCenterAccountsDetailList();

				for (CenterAccountsDetail centerAccountsDetail : accountsDetail) {
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
		List<CenterAccountsDetail> resultDates = Lists.newArrayList();
		Iterator<String> keyIterator = map.keySet().iterator();
		String strKey = "";
		// 循环物品列表
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			totalMoney = totalMoney.add(map.get(strKey));

		}
		for (StoDict stoDict : stoDictList) {
			CenterAccountsDetail detail = new CenterAccountsDetail();
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
		result.setClientName(Name);
		result.setCenterAccountsDetailList(resultDates);
		// 返回
		return result;
	}

}