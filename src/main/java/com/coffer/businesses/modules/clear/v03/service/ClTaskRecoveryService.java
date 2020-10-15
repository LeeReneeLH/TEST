package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClTaskDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.ClTaskMainDao;
import com.coffer.businesses.modules.clear.v03.dao.ClearingGroupDao;
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
 * 任务回收Service
 * 
 * @author XL
 * @version 2017-08-29
 */
@Service
@Transactional(readOnly = true)
public class ClTaskRecoveryService extends CrudService<ClTaskMainDao, ClTaskMain> {

	@Autowired
	private ClTaskMainDao cltaskMainDao;
	@Autowired
	private ClTaskDetailDao cltaskDetailDao;
	@Autowired
	private ClearingGroupDao clearingGroupDao;

	public ClTaskMain get(String taskNo) {
		return cltaskMainDao.get(taskNo);
	}

	public List<ClTaskMain> findList(ClTaskMain clTaskMain) {
		return super.findList(clTaskMain);
	}

	/**
	 * 获取任务回收列表
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param clTaskMain
	 * @param page
	 * @return
	 */
	public Page<ClTaskMain> findPage(Page<ClTaskMain> page, ClTaskMain clTaskMain) {
		// 设置任务类型为任务回收
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_RECOVERY);
		/* 增加数据穿透 wzj 2017-11-27 begin */
		clTaskMain.getSqlMap().put("dsf", dataScopeFilter(clTaskMain.getCurrentUser(), "o", null));
		/* end */
		// 查询条件： 开始时间
		if (clTaskMain.getCreateTimeStart() != null) {
			clTaskMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clTaskMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clTaskMain.getCreateTimeEnd() != null) {
			clTaskMain
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clTaskMain.getCreateTimeEnd())));
		}
		return super.findPage(page, clTaskMain);
	}

	/**
	 * 保存任务回收信息
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param clTaskMain
	 * @return
	 */
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
			// 是否抽查
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_YES);
			// 任务种类为抽查任务
			clTaskMain.setCheckType(ClearConstant.CheckType.CHECK_TASK);
		} else {
			// 是否抽查
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_NO);
			// 任务种类为普通任务
			clTaskMain.setCheckType(ClearConstant.CheckType.NORMAL_TASK);
		}
		// 设置交接人姓名
		clTaskMain.setJoinManName(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortName());
		// 设置清分状态为已清分
		clTaskMain.setTranStatus(ClearConstant.TranStatus.CLEARING_COMPLETE);
		// 设置任务类型为任务回收
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_RECOVERY);
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
		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())) {
			// 验证回收数量
			this.checkRecovery(clTaskMain);
		}
		// 追加柜员账务
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
			// 设置收入金额
			tellerAccountsMain.setInAmount(clTaskMain.getTotalAmt());
			// 设置登录人
			tellerAccountsMain.setLoginUser(clTaskMain.getLoginUser());
			// 设置收入类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			tellerAccountsMain.setOffice(clTaskMain.getOffice());
			/* end */
			ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		}
		int clMainResult = cltaskMainDao.insert(clTaskMain);
		if (clMainResult == 0) {
			if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务回收主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7306", "", new String[] { clTaskMain.getTaskNo() });
			}
			if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务回收主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7302", "", new String[] { clTaskMain.getTaskNo() });
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
					String strMessageContent = "任务回收明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7307", "", new String[] { clTaskMain.getTaskNo() });
				}
				if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务回收明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7303", "", new String[] { clTaskMain.getTaskNo() });
				}
			}
		}
	}

	/**
	 * 保存小车任务回收信息
	 * 
	 * @author wzj
	 * @version 2017-12-25
	 * @param clTaskMain
	 * @return
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
			// 是否抽查
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_YES);
			// 任务种类为抽查任务
			clTaskMain.setCheckType(ClearConstant.CheckType.CHECK_TASK);
		} else {
			// 是否抽查
			clTaskMain.setCheckStatus(ClearConstant.CheckStatus.CHECK_NO);
			// 任务种类为普通任务
			clTaskMain.setCheckType(ClearConstant.CheckType.NORMAL_TASK);
		}
		// 设置交接人姓名
		clTaskMain.setJoinManName(StoreCommonUtils.getEscortById(clTaskMain.getJoinManNo()).getEscortName());
		// 设置清分状态为已清分
		clTaskMain.setTranStatus(ClearConstant.TranStatus.CLEARING_COMPLETE);
		// 设置任务类型为任务回收
		clTaskMain.setTaskType(ClearConstant.TaskType.TASK_RECOVERY);
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
		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())) {
			// 验证回收数量
			this.checkRecovery(clTaskMain);
		}
		// 追加柜员账务
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
			// 设置收入金额
			tellerAccountsMain.setInAmount(clTaskMain.getTotalAmt());
			// 设置登录人
			tellerAccountsMain.setLoginUser(clTaskMain.getLoginUser());
			// 设置收入类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			tellerAccountsMain.setOffice(clTaskMain.getOffice());
			/* end */
			ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		}
		int clMainResult = cltaskMainDao.insert(clTaskMain);
		if (clMainResult == 0) {
			if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务回收主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7306", "", new String[] { clTaskMain.getTaskNo() });
			}
			if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
				String strMessageContent = "任务回收主表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7302", "", new String[] { clTaskMain.getTaskNo() });
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
					String strMessageContent = "任务回收明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7307", "", new String[] { clTaskMain.getTaskNo() });
				}
				if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
					String strMessageContent = "任务回收明细表：" + clTaskMain.getTaskNo() + "插入失败！";
					logger.error(strMessageContent);
					throw new BusinessException("message.E7303", "", new String[] { clTaskMain.getTaskNo() });
				}
			}
		}
	}

	/**
	 * 获取goodsId
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param clTaskMain
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
	 * 获取物品价值
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param clTaskMain
	 * @return totalAmt
	 */
	public BigDecimal getGoodsValue(ClTaskMain clTaskMain) {
		// 获取goodsId
		String goodsId = this.getclTaskMainMapKey(clTaskMain);
		// 获取物品价值
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId);
		return goodsValue;
	}

	/**
	 * 计算总价值
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param goodsValue
	 * @param totalCount
	 * @return totalAmt
	 */
	public BigDecimal sumTotal(BigDecimal goodsValue, Long totalCount) {
		BigDecimal bigTotalCount = new BigDecimal(totalCount);
		// 计算
		BigDecimal goodsAmt = goodsValue.multiply(bigTotalCount);
		return goodsAmt;
	}

	/**
	 * 根据任务编号查询任务回收明细列表
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param taskNo
	 * @return ClTaskDetail
	 */
	public List<ClTaskDetail> getByMid(String taskNo) {
		return cltaskDetailDao.getByMid(taskNo);
	}

	/**
	 * 删除任务回收信息
	 * 
	 * @author XL
	 * @version 2017-8-29
	 * @param clTaskMain
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(ClTaskMain clTaskMain) {
		super.delete(clTaskMain);
	}

	/**
	 * 验证任务分配量
	 * 
	 * @author qipeihong
	 * @version 2017年9月19日
	 * @param clTaskMain
	 */
	public synchronized void checkRecovery(ClTaskMain clTaskMain) {
		// 取得字典数据列表
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		// 当日已分配列表
		List<ClTaskMain> holeClTaskMainList = Lists.newArrayList();
		// 当日已回收列表
		List<ClTaskMain> holeClTaskRecoveryList = Lists.newArrayList();

		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型为清分
		info.setBusType(clTaskMain.getBusType());
		// 设置开始结束时间
		info.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		info.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 设置计划类型为正常清分
		info.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);

		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		User userInfo = UserUtils.getUser();
		info.setOffice(userInfo.getOffice());
		/* end */

		// 获得当天清分类型下任务分配流水
		List<ClTaskMain> clTaskList = this.findList(info);
		// 任务分配流水
		List<ClTaskMain> clTaskMainList = Lists.newArrayList();
		// 任务回收流水
		List<ClTaskMain> clTaskRecoveryList = Lists.newArrayList();
		for (ClTaskMain clTask : clTaskList) {
			if (clTask.getTaskType().equals(ClearConstant.TaskType.TASK_DISTRIBUTION)) {
				clTaskMainList.add(clTask);
			}
			if (clTask.getTaskType().equals(ClearConstant.TaskType.TASK_RECOVERY)) {
				clTaskRecoveryList.add(clTask);
			}
		}

		// 遍历出数据字典面值对应的code
		for (int a = 0; a < stoDictList.size(); a++) {
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			ClTaskMain existClTaskMain = null;
			ClTaskMain existClTaskRecovery = null;

			// 设置默认为false
			boolean flag = false;
			boolean exist = false;
			// 遍历出当日已分配的信息
			for (int b = 0; b < clTaskMainList.size(); b++) {
				ClTaskMain tempClTaskMain = clTaskMainList.get(b);
				String keyCode = tempClTaskMain.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					if (existClTaskMain != null) {
						tempClTaskMain.setTotalCount(tempClTaskMain.getTotalCount() + existClTaskMain.getTotalCount());
					}
					existClTaskMain = tempClTaskMain;
					flag = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (flag == false) {
				ClTaskMain newClTaskMain = new ClTaskMain();
				newClTaskMain.setDenomination(code);
				newClTaskMain.setTotalCount(Long.parseLong("0"));
				holeClTaskMainList.add(newClTaskMain);
			} else {
				holeClTaskMainList.add(existClTaskMain);
			}

			for (int b = 0; b < clTaskRecoveryList.size(); b++) {
				ClTaskMain tempClTaskMain = clTaskRecoveryList.get(b);
				String keyCode = tempClTaskMain.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					if (existClTaskRecovery != null) {
						tempClTaskMain
								.setTotalCount(tempClTaskMain.getTotalCount() + existClTaskRecovery.getTotalCount());
					}
					existClTaskRecovery = tempClTaskMain;
					exist = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (exist == false) {
				ClTaskMain newClTaskMain = new ClTaskMain();
				newClTaskMain.setDenomination(code);
				newClTaskMain.setTotalCount(Long.parseLong("0"));
				holeClTaskRecoveryList.add(newClTaskMain);
			} else {
				holeClTaskRecoveryList.add(existClTaskRecovery);
			}
		}
		// 可回收数量
		long recoveryCount = 0;
		// 获得已分配数量
		for (ClTaskMain clTask : holeClTaskMainList) {
			if (clTask.getDenomination().equals(clTaskMain.getDenomination())) {
				recoveryCount = recoveryCount + clTask.getTotalCount();
			}
		}
		// 获得出去已回收数量的剩余数量
		for (ClTaskMain clTaskRecovery : holeClTaskRecoveryList) {
			if (clTaskMain.getDenomination().equals(clTaskRecovery.getDenomination())) {
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.CLEAR)) {
					recoveryCount = recoveryCount - clTaskRecovery.getTotalCount();
				}
				if (clTaskMain.getBusType().equals(ClearConstant.BusinessType.COMPLEX_POINT)) {
					recoveryCount = recoveryCount - clTaskRecovery.getTotalCount();
				}
			}
		}
		// 若可分配数量小于分配数量
		if (recoveryCount < clTaskMain.getTotalCount()) {
			String strMessageContent = GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", "")
					+ "此面值回收数量大于可回收数量，回收数量：" + clTaskMain.getTotalCount().toString() + ",可回收数量："
					+ Long.toString(recoveryCount) + ",回收失败";
			throw new BusinessException("message.A1006", strMessageContent,
					new String[] { GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", ""),
							clTaskMain.getTotalCount().toString(), Long.toString(recoveryCount) });
		}

	}

}