package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClInMainDao;
import com.coffer.businesses.modules.clear.v03.dao.ClTaskDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.ClTaskMainDao;
import com.coffer.businesses.modules.clear.v03.dao.ClearingGroupDao;
import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 任务分配Service
 * 
 * @author QPH
 * @version 2017-08-15
 */
@Service
@Transactional(readOnly = true)
public class ClTaskMainService extends CrudService<ClTaskMainDao, ClTaskMain> {

	@Autowired
	private ClTaskMainDao cltaskMainDao;
	@Autowired
	private ClTaskDetailDao cltaskDetailDao;
	@Autowired
	private ClearingGroupDao clearingGroupDao;
	@Autowired
	private ClInMainDao clInMainDao;

	public ClTaskMain get(String taskNo) {
		return cltaskMainDao.get(taskNo);
	}

	public List<ClTaskMain> findList(ClTaskMain clTaskMain) {
		clTaskMain.getSqlMap().put("dsf", dataScopeFilter(clTaskMain.getCurrentUser(), "o", null));
		return super.findList(clTaskMain);
	}

	public Page<ClTaskMain> findPage(Page<ClTaskMain> page, ClTaskMain clTaskMain) {
		// 设置为任务分配
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		/* 增加数据穿透 wzj 2017-11-27 begin */
		clTaskMain.getSqlMap().put("dsf", dataScopeFilter(clTaskMain.getCurrentUser(), "o", null));
		/* end */
		// 查询条件：开始时间
		if (clTaskMain.getCreateTimeStart() != null) {
			clTaskMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(clTaskMain.getCreateTimeStart()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (clTaskMain.getCreateTimeEnd() != null) {
			clTaskMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clTaskMain.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		return super.findPage(page, clTaskMain);
	}

	@Transactional(readOnly = false)
	public void save(ClTaskMain clTaskMain) {
		// 设置流水号
		if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
			// 复点单号
			clTaskMain.setTaskNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.COMPLEX_POINT,
					clTaskMain.getLoginUser().getOffice()));
		}
		if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
			// 清点单号
			clTaskMain.setTaskNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.CLEAR,
					clTaskMain.getLoginUser().getOffice()));
		}
		// 若计划类型为抽查
		if (ClearConstant.PlanType.CHECK_CLEAR.equals(clTaskMain.getPlanType())) {
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_YES);
			// 任务种类为抽查任务
			clTaskMain.setCheckType(ClearConstant.CheckType.CHECK_TASK);
		} else {
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_NO);
			// 任务种类为普通任务
			clTaskMain.setCheckType(ClearConstant.CheckType.NORMAL_TASK);
		}
		// 设置交接人姓名
		clTaskMain.setJoinManName(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortName());
		// 设置清分状态为清分中
		clTaskMain.setTranStatus(ClearConstant.TranStatus.CLEARING);
		// 设置任务类型为任务分配
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		// 获取物品价值
		BigDecimal goodsValue = this.getGoodsValue(clTaskMain);
		// 设置总金额
		BigDecimal totalAmt = this.sumTotal(goodsValue, clTaskMain.getTotalCount());
		clTaskMain.setTotalAmt(totalAmt);
		// 设置创建信息
		clTaskMain.setCreateBy(clTaskMain.getLoginUser());
		clTaskMain.setCreateName(clTaskMain.getLoginUser().getName());
		clTaskMain.setCreateDate(new Date());
		// 设置更新信息
		clTaskMain.setUpdateBy(clTaskMain.getCreateBy());
		clTaskMain.setUpdateName(clTaskMain.getCreateName());
		clTaskMain.setUpdateDate(clTaskMain.getCreateDate());
		// 设置操作信息
		clTaskMain.setOperatorBy(clTaskMain.getCreateBy().getId());
		clTaskMain.setOperatorName(clTaskMain.getCreateName());
		clTaskMain.setOperateDate(clTaskMain.getCreateDate());

		this.checkCount(clTaskMain);
		// 追加柜员账务 修改人：xl 修改时间：2017-11-02 begin
		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())) {
			// 柜员账务
			TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
			// 设置流水单号
			tellerAccountsMain.setBussinessId(clTaskMain.getTaskNo());
			// 设置柜员ID
			tellerAccountsMain.setTellerBy(clTaskMain.getJoinManNo());
			// 设置柜员姓名
			tellerAccountsMain.setTellerName(clTaskMain.getJoinManName());
			// 设置柜员类型
			tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortType());
			// 客户编号
			tellerAccountsMain.setCustNo(clTaskMain.getLoginUser().getOffice().getId());
			// 客户名称
			tellerAccountsMain.setCustName(clTaskMain.getLoginUser().getOffice().getName());
			// 设置金额类型
			tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
			// 设置业务类型
			tellerAccountsMain.setBussinessType(clTaskMain.getBusType());
			// 设置业务状态
			tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
			// 设置支出金额
			tellerAccountsMain.setOutAmount(clTaskMain.getTotalAmt());
			// 设置登录人
			tellerAccountsMain.setLoginUser(clTaskMain.getLoginUser());
			// 设置支出类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			tellerAccountsMain.setOffice(clTaskMain.getOffice());
			/* end */
			ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		}
		// end
		int clMainResult = cltaskMainDao.insert(clTaskMain);
		if (clMainResult == 0) {
			if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务分配主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7300", "", new String[] { clTaskMain.getTaskNo() });
			}
			if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务分配主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7304", strMessageContent,
						new String[] { clTaskMain.getTaskNo() });
			}
		}
		// 任务管理明细
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		// 判断是否接口调用
		if (clTaskMain.getUserList() == null) {
			// 接口调用，设置明细金额
			clTaskMain.getClTaskDetailList().get(0).setTotalAmt(clTaskMain.getTotalAmt());
			// 设置主表ID
			clTaskMain.getClTaskDetailList().get(0).setMId(clTaskMain.getTaskNo());
			clTaskDetailList = clTaskMain.getClTaskDetailList();
		} else {
			// 非接口调用，设置任务管理明细
			for (String user : clTaskMain.getUserList()) {
				String userId = user.substring(0, 32);
				String userCount = user.substring(32);
				if (StringUtils.isBlank(userCount) || Integer.parseInt(userCount) == 0) {
					continue;
				}
				ClTaskDetail clTaskDetail = new ClTaskDetail();
				// 设置明细ID
				clTaskDetail.setDetailId(IdGen.uuid());
				// 设置主表ID
				clTaskDetail.setMId(clTaskMain.getTaskNo());
				ClearingGroupDetail cl = ClearCommonUtils.getClearGroupByUserId(clTaskMain.getClearGroup(), userId,
						clTaskMain.getBusType(), null);

				// 设置员工编号、 员工姓名、工位类型
				clTaskDetail.setEmpNo(cl.getUser().getId());
				clTaskDetail.setEmpName(cl.getUserName());
				clTaskDetail
						.setWorkingPositionType(clearingGroupDao.get(cl.getClearingGroupId()).getWorkingPositionType());
				// 设置职位编号
				clTaskDetail.setOfficeNo(UserUtils.get(cl.getUser().getId()).getUserType());
				// 设置明细捆数
				Long total = Long.parseLong(userCount);
				clTaskDetail.setTotalCount(total);
				// 计算金额
				BigDecimal detailTotalAmt = this.sumTotal(goodsValue, total);
				clTaskDetail.setTotalAmt(detailTotalAmt);
				clTaskDetailList.add(clTaskDetail);
			}
		}
		// 执行插入操作
		for (ClTaskDetail clTaskDetail : clTaskDetailList) {
			int clDetailResult = cltaskDetailDao.insert(clTaskDetail);
			if (clDetailResult == 0) {
				if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务分配明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7305", "", new String[] { clTaskMain.getTaskNo() });
				}
				if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务分配明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7301", "", new String[] { clTaskMain.getTaskNo() });
				}
			}
		}
	}

	/**
	 * 小车接口保存数据
	 * 
	 * @author wzj
	 * @version 2017-12-22
	 */
	@Transactional(readOnly = false)
	public void saveDate(ClTaskMain clTaskMain) {
		// 设置流水号
		if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
			// 复点单号
			clTaskMain.setTaskNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.COMPLEX_POINT,
					clTaskMain.getLoginUser().getOffice()));
		}
		if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
			// 清点单号
			clTaskMain.setTaskNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.CLEAR,
					clTaskMain.getLoginUser().getOffice()));
		}
		// 若计划类型为抽查
		if (ClearConstant.PlanType.CHECK_CLEAR.equals(clTaskMain.getPlanType())) {
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_YES);
			// 任务种类为抽查任务
			clTaskMain.setCheckType(ClearConstant.CheckType.CHECK_TASK);
		} else {
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_NO);
			// 任务种类为普通任务
			clTaskMain.setCheckType(ClearConstant.CheckType.NORMAL_TASK);
		}
		// 设置交接人姓名
		clTaskMain.setJoinManName(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortName());
		// 设置清分状态为清分中
		clTaskMain.setTranStatus(ClearConstant.TranStatus.CLEARING);
		// 设置任务类型为任务分配
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		// 获取物品价值
		BigDecimal goodsValue = this.getGoodsValue(clTaskMain);
		// 设置总金额
		BigDecimal totalAmt = this.sumTotal(goodsValue, clTaskMain.getTotalCount());
		clTaskMain.setTotalAmt(totalAmt);
		// 设置创建信息
		clTaskMain.setCreateBy(clTaskMain.getLoginUser());
		clTaskMain.setCreateName(clTaskMain.getLoginUser().getName());
		clTaskMain.setCreateDate(new Date());
		// 设置更新信息
		clTaskMain.setUpdateBy(clTaskMain.getCreateBy());
		clTaskMain.setUpdateName(clTaskMain.getCreateName());
		clTaskMain.setUpdateDate(clTaskMain.getCreateDate());
		// 设置操作信息
		clTaskMain.setOperatorBy(clTaskMain.getCreateBy().getId());
		clTaskMain.setOperatorName(clTaskMain.getCreateName());
		clTaskMain.setOperateDate(clTaskMain.getCreateDate());

		this.checkCount(clTaskMain);
		// 追加柜员账务 修改人：xl 修改时间：2017-11-02 begin
		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())) {
			// 柜员账务
			TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
			// 设置流水单号
			tellerAccountsMain.setBussinessId(clTaskMain.getTaskNo());
			// 设置柜员ID
			tellerAccountsMain.setTellerBy(clTaskMain.getJoinManNo());
			// 设置柜员姓名
			tellerAccountsMain.setTellerName(clTaskMain.getJoinManName());
			// 设置柜员类型
			tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortType());
			// 客户编号
			tellerAccountsMain.setCustNo(clTaskMain.getLoginUser().getOffice().getId());
			// 客户名称
			tellerAccountsMain.setCustName(clTaskMain.getLoginUser().getOffice().getName());
			// 设置金额类型
			tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
			// 设置业务类型
			tellerAccountsMain.setBussinessType(clTaskMain.getBusType());
			// 设置业务状态
			tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
			// 设置支出金额
			tellerAccountsMain.setOutAmount(clTaskMain.getTotalAmt());
			// 设置登录人
			tellerAccountsMain.setLoginUser(clTaskMain.getLoginUser());
			// 设置支出类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			tellerAccountsMain.setOffice(clTaskMain.getOffice());
			/* end */
			ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		}
		// end
		int clMainResult = cltaskMainDao.insert(clTaskMain);
		if (clMainResult == 0) {
			if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务分配主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7300", "", new String[] { clTaskMain.getTaskNo() });
			}
			if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务分配主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7304", strMessageContent,
						new String[] { clTaskMain.getTaskNo() });
			}
		}
		// 任务管理明细
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		List<ClTaskDetail> ClTaskDetailTempList = Lists.newArrayList();
		// 判断是否接口调用
		if (clTaskMain.getUserList() == null) {
			ClTaskDetailTempList = clTaskMain.getClTaskDetailList();
			for (ClTaskDetail clTaskDetail : ClTaskDetailTempList) {
				// 设置明细金额
				clTaskDetail.setTotalAmt(this.sumTotal(goodsValue, clTaskDetail.getTotalCount()));
				// 设置主表ID
				clTaskDetail.setMId(clTaskMain.getTaskNo());
			}
			clTaskDetailList = ClTaskDetailTempList;
		}
		// 执行插入操作
		for (ClTaskDetail clTaskDetail : clTaskDetailList) {
			int clDetailResult = cltaskDetailDao.insert(clTaskDetail);
			if (clDetailResult == 0) {
				if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务分配明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7305", "", new String[] { clTaskMain.getTaskNo() });
				}
				if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务分配明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7301", "", new String[] { clTaskMain.getTaskNo() });
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(ClTaskMain clTaskMain) {
		super.delete(clTaskMain);
	}

	/**
	 * @author qipeihong
	 * @version 2017年8月25日 获取goodsId
	 * 
	 * @param clTaskMain
	 * 
	 * @return goodsId
	 */
	public String getclTaskMainMapKey(ClTaskMain clTaskMain) {
		StringBuilder goodsId = new StringBuilder();
		// 币种
		goodsId.append(StringUtils.isEmpty(clTaskMain.getCurrency()) ? "" : clTaskMain.getCurrency());
		// 类别
		goodsId.append(StringUtils.isEmpty(clTaskMain.getClassification()) ? "" : clTaskMain.getClassification());
		// 套别
		goodsId.append(StringUtils.isEmpty(clTaskMain.getSets()) ? "" : clTaskMain.getSets());
		// 现金材质
		goodsId.append(StringUtils.isEmpty(clTaskMain.getCash()) ? "" : clTaskMain.getCash());
		// 券别
		goodsId.append(StringUtils.isEmpty(clTaskMain.getDenomination()) ? "" : clTaskMain.getDenomination());
		// 单位
		goodsId.append(StringUtils.isEmpty(clTaskMain.getUnit()) ? "" : clTaskMain.getUnit());
		return goodsId.toString();
	}

	/**
	 * @author qipeihong
	 * @version 2017年8月25日 获取物品价值
	 * 
	 * @param goodsId
	 * 
	 * @return totalAmt
	 */
	public BigDecimal getGoodsValue(ClTaskMain clTaskMain) {
		// 获取goodsId
		String goodsId = this.getclTaskMainMapKey(clTaskMain);
		// 获取物品价值
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId);
		if (goodsValue == null) {
			String strMessageContent = "物品：" + goodsId + "不存在，请与管理员联系！";
			throw new BusinessException("message.A1009", strMessageContent, new String[] { goodsId });
		}
		return goodsValue;
	}

	/**
	 * @author qipeihong
	 * @version 2017年8月25日 计算总价值
	 * 
	 * @param goodsId
	 * 
	 * @return totalAmt
	 */
	public BigDecimal sumTotal(BigDecimal goodsValue, Long totalCount) {
		BigDecimal bigTotalCount = new BigDecimal(totalCount);
		// 计算
		BigDecimal goodsAmt = goodsValue.multiply(bigTotalCount);
		return goodsAmt;

	}

	/**
	 * @author qipeihong
	 * @version 2017年8月28日 根据任务编号查询任务分配详细
	 * 
	 * @param taskNo
	 * 
	 * @return ClTaskDetail
	 */
	public List<ClTaskDetail> getByMid(String taskNo) {
		return cltaskDetailDao.getByMid(taskNo);

	}

	/**
	 * 根据系统当前时间，获取当天商行交款入库清分量
	 * 
	 * @author wxz
	 * @version 2017年9月15日
	 * 
	 * @return
	 */
	public List<ClInMain> getDetailList(ClTaskMain clTaskMain) {
		ClInMain bankPayInfo = new ClInMain();
		if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.CLEAR)) {
			bankPayInfo.setBusType(ClearConstant.BusinessType.BANK_PAY);
		}
		if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.COMPLEX_POINT)) {
			bankPayInfo.setBusType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		}
		if (clTaskMain.getSearchDateStart() == null) {
			bankPayInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		} else {
			bankPayInfo.setSearchDateStart(clTaskMain.getSearchDateStart());
		}
		if (clTaskMain.getSearchDateEnd() == null) {
			bankPayInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		} else {
			bankPayInfo.setSearchDateEnd(clTaskMain.getSearchDateEnd());
		}

		bankPayInfo.setStatus(ClearConstant.StatusType.CREATE);
		bankPayInfo.setOffice(clTaskMain.getOffice());

		return clInMainDao.getDetailList(bankPayInfo);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月19日 验证当日入库清分量
	 * 
	 * 
	 * @param clTaskMain
	 */
	public synchronized void checkCount(ClTaskMain clTaskMain) {
		List<ClInMain> bankPayInfoList = this.getDetailList(clTaskMain);
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		List<ClInMain> holeBankPayInfolist = new ArrayList<ClInMain>();
		// 遍历出数据字典面值对应的code
		for (int a = 0; a < stoDictList.size(); a++) {
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			ClInMain existBankPayInfo = null;
			// 设置默认为false
			boolean exist = false;
			// 遍历出数据库当日存入的面值code
			for (int b = 0; b < bankPayInfoList.size(); b++) {
				ClInMain tempBankPayInfo = bankPayInfoList.get(b);
				String keyCode = tempBankPayInfo.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					existBankPayInfo = tempBankPayInfo;
					exist = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (exist == false) {
				ClInMain newBankPayInfo = new ClInMain();
				newBankPayInfo.setDenomination(code);
				newBankPayInfo.setCountBank("0");
				newBankPayInfo.setCountPeopleBank("0");
				holeBankPayInfolist.add(newBankPayInfo);
			} else {
				holeBankPayInfolist.add(existBankPayInfo);
			}
		}
		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型为清分
		info.setBusType(clTaskMain.getBusType());
		// 设置任务类型为任务分配/员工工作量登记
		List<String> taskTypes = Lists.newArrayList();
		taskTypes.add(ClearConstant.TaskType.TASK_DISTRIBUTION);
		taskTypes.add(ClearConstant.TaskType.STAFF_WORKLOAD);
		info.setTaskTypes(taskTypes);
		// 设置计划类型为正常清分
		info.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		info.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		info.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));

		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		User userInfo = UserUtils.getUser();
		info.setOffice(userInfo.getOffice());
		/* end */

		// 获得当天清分类型下任务分配流水
		List<ClTaskMain> clTaskMainList = this.findList(info);
		// 当天已分配数量
		long distribution = 0;
		for (ClTaskMain clTask : clTaskMainList) {
			if (clTask.getDenomination().equals(clTaskMain.getDenomination())) {
				distribution = distribution + clTask.getTotalCount();
			}
		}
		// 获取到当日入库量可分配数量
		for (ClInMain bankPayInfo : holeBankPayInfolist) {
			if (clTaskMain.getDenomination().equals(bankPayInfo.getDenomination())) {
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.CLEAR)) {
					distribution = Long.parseLong(bankPayInfo.getCountBank()) - distribution;
				}
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.COMPLEX_POINT)) {
					distribution = Long.parseLong(bankPayInfo.getCountPeopleBank()) - distribution;
				}
			}
		}
		// 若可分配数量小于分配数量
		if (distribution < clTaskMain.getTotalCount()) {
			String strMessageContent = GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", "")
					+ "此面值分配数量大于可分配数量，分配数量：" + clTaskMain.getTotalCount().toString() + ",可分配数量："
					+ Long.toString(distribution) + ",分配失败";

			throw new BusinessException("message.A1005", strMessageContent,
					new String[] { GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", ""),
							clTaskMain.getTotalCount().toString(), Long.toString(distribution) });
		}

	}

	/**
	 * 验证当日入库清分量(接口用)
	 * 
	 * @author XL
	 * @version 2017年10月27日
	 * @param clTaskMain
	 * @return
	 */
	public synchronized boolean checkCountForService(ClTaskMain clTaskMain) {
		List<ClInMain> bankPayInfoList = this.getDetailList(clTaskMain);
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		List<ClInMain> holeBankPayInfolist = new ArrayList<ClInMain>();
		// 遍历出数据字典面值对应的code
		for (int a = 0; a < stoDictList.size(); a++) {
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			ClInMain existBankPayInfo = null;
			// 设置默认为false
			boolean exist = false;
			// 遍历出数据库当日存入的面值code
			for (int b = 0; b < bankPayInfoList.size(); b++) {
				ClInMain tempBankPayInfo = bankPayInfoList.get(b);
				String keyCode = tempBankPayInfo.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					existBankPayInfo = tempBankPayInfo;
					exist = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (exist == false) {
				ClInMain newBankPayInfo = new ClInMain();
				newBankPayInfo.setDenomination(code);
				newBankPayInfo.setCountBank("0");
				newBankPayInfo.setCountPeopleBank("0");
				holeBankPayInfolist.add(newBankPayInfo);
			} else {
				holeBankPayInfolist.add(existBankPayInfo);
			}
		}
		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型为清分
		info.setBusType(clTaskMain.getBusType());
		// 设置任务类型为任务分配
		info.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		// 设置计划类型为正常清分
		info.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		info.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		info.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		User userInfo = UserUtils.getUser();
		info.setOffice(userInfo.getOffice());
		/* end */
		// 获得当天清分类型下任务分配流水
		List<ClTaskMain> clTaskMainList = this.findList(info);
		// 当天已分配数量
		long distribution = 0;
		for (ClTaskMain clTask : clTaskMainList) {
			if (clTask.getDenomination().equals(clTaskMain.getDenomination())) {
				distribution = distribution + clTask.getTotalCount();
			}
		}
		// 获取到当日入库量可分配数量
		for (ClInMain bankPayInfo : holeBankPayInfolist) {
			if (clTaskMain.getDenomination().equals(bankPayInfo.getDenomination())) {
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.CLEAR)) {
					distribution = Long.parseLong(bankPayInfo.getCountBank()) - distribution;
				}
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.COMPLEX_POINT)) {
					distribution = Long.parseLong(bankPayInfo.getCountPeopleBank()) - distribution;
				}

			}
		}
		// 若可分配数量小于分配数量
		if (distribution < clTaskMain.getTotalCount()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年11月20日
	 * 
	 * 
	 * @param clTaskMain
	 * @return
	 */

	public List<ClTaskDetail> getClearGroupByUserId(ClTaskMain clTaskMain) {
		return cltaskMainDao.getClearGroupByUserId(clTaskMain);
	}

}