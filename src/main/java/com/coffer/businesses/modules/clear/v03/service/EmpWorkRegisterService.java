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
 * 员工工作量登记Service
 * 
 * @author wxz
 * @version 2017-09-20
 */
@Service
@Transactional(readOnly = true)
public class EmpWorkRegisterService extends CrudService<ClTaskMainDao, ClTaskMain> {

	@Autowired
	private ClTaskMainDao cltaskMainDao;
	@Autowired
	private ClTaskDetailDao cltaskDetailDao;
	@Autowired
	private ClInMainDao clInMainDao;
	@Autowired
	private ClearingGroupDao clearingGroupDao;

	public ClTaskMain get(String taskNo) {
		return cltaskMainDao.get(taskNo);
	}

	public List<ClTaskMain> findList(ClTaskMain clTaskMain) {
		return super.findList(clTaskMain);
	}

	public Page<ClTaskMain> findPage(Page<ClTaskMain> page, ClTaskMain clTaskMain) {
		clTaskMain.getSqlMap().put("dsf", dataScopeFilter(clTaskMain.getCurrentUser(), "o", null));
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
		clTaskMain.setTaskType(ClearConstant.TaskType.STAFF_WORKLOAD);
		// clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
		return super.findPage(page, clTaskMain);
	}

	@Transactional(readOnly = false)
	public void save(ClTaskMain clTaskMain) {
		// 设置流水号
		clTaskMain.setTaskNo(
				BusinessUtils.getClearNewBusinessNo(clTaskMain.getBusType(), clTaskMain.getLoginUser().getOffice()));
		// 设置业务类型
		clTaskMain.setBusType(clTaskMain.getBusType());
		// 设置清分状态
		clTaskMain.setTranStatus(ClearConstant.TranStatus.CLEARING_COMPLETE);
		// 设置任务类型为员工工作量
		clTaskMain.setTaskType(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 设置操作人操作时间
		// clTaskMain.setOperateDate(clTaskMain.getCreateDate());
		clTaskMain.setOperatorBy(clTaskMain.getLoginUser().getId());
		clTaskMain.setOperatorName(clTaskMain.getLoginUser().getName());
		// 获取物品价值
		BigDecimal goodsValue = this.getGoodsValue(clTaskMain);
		// 设置总金额
		BigDecimal totalAmt = this.sumTotal(goodsValue, clTaskMain.getTotalCount());
		clTaskMain.setTotalAmt(totalAmt);
		// 设置交接人以及交接人编号
		/*
		 * User joinUser =
		 * UserUtils.getByLoginName(clTaskMain.getJoinManName());
		 * clTaskMain.setJoinManNo(joinUser.getId());
		 * clTaskMain.setJoinManName(joinUser.getName());
		 */
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

		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())) {
			// 验证当日入库清分量
			this.checkCountByEmp(clTaskMain);
		}
		// 设置任务管理明细
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
			//
			ClearingGroupDetail cl = ClearCommonUtils.getClearGroupByUserId(clTaskMain.getClearGroup(), userId,
					clTaskMain.getBusType(), null);

			// 设置员工编号、 员工姓名、工位类型
			clTaskDetail.setEmpNo(cl.getUser().getId());
			clTaskDetail.setEmpName(cl.getUserName());
			clTaskDetail.setWorkingPositionType(clearingGroupDao.get(cl.getClearingGroupId()).getWorkingPositionType());
			// 设置明细捆数
			Long total = Long.parseLong(userCount);
			clTaskDetail.setTotalCount(total);
			// 计算金额
			BigDecimal detailTotalAmt = this.sumTotal(goodsValue, total);
			clTaskDetail.setTotalAmt(detailTotalAmt);
			User detailUser = UserUtils.get(cl.getUser().getId());
			// 设置职位编号
			clTaskDetail.setOfficeNo(detailUser.getUserType());
			// 执行插入操作
			int clDetailResult = cltaskDetailDao.insert(clTaskDetail);
			if (clDetailResult == 0) {
				String strMessageContent = "任务分配明细表：" + clTaskMain.getTaskNo() + "插入失败！";
				logger.error(strMessageContent);
				throw new BusinessException("message.E7601", "", new String[] { clTaskMain.getTaskNo() });
			}
		}

		// 执行插入操作
		clTaskMain.preInsert();
		int clMainResult = cltaskMainDao.insert(clTaskMain);
		if (clMainResult == 0) {
			String strMessageContent = "任务分配主表：" + clTaskMain.getTaskNo() + "插入失败！";
			logger.error(strMessageContent);
			throw new BusinessException("message.E7600", "", new String[] { clTaskMain.getTaskNo() });
		}
	}

	@Transactional(readOnly = false)
	public void delete(ClTaskMain clTaskMain) {
		super.delete(clTaskMain);
	}

	/**
	 * @author wxz
	 * @version 2017年9月20日 获取goodsId
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
	 * @author wxz
	 * @version 2017年9月20日 获取物品价值
	 * @param goodsId
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
	 * @author wxz
	 * @version 2017年9月20日 计算总价值
	 * 
	 * @param goodsId
	 * @return totalAmt
	 */
	public BigDecimal sumTotal(BigDecimal goodsValue, Long totalCount) {
		BigDecimal bigTotalCount = new BigDecimal(totalCount);
		// 计算
		BigDecimal goodsAmt = goodsValue.multiply(bigTotalCount);
		return goodsAmt;

	}

	/**
	 * @author wxz
	 * @version 根据任务编号查询任务分配详细
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
	 * @version 2017年9月20日
	 * 
	 * @return
	 */
	public List<ClInMain> getDetailList(ClTaskMain clTaskMain) {
		ClInMain bankPayInfo = new ClInMain();
		if (ClearConstant.BusinessType.CLEAR.equals(clTaskMain.getBusType())) {
			bankPayInfo.setBusType(ClearConstant.BusinessType.BANK_PAY);
		}
		if (ClearConstant.BusinessType.COMPLEX_POINT.equals(clTaskMain.getBusType())) {
			bankPayInfo.setBusType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		}
		if (clTaskMain.getOperateDate() == null) {
			bankPayInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
			bankPayInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		} else {
			bankPayInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clTaskMain.getOperateDate())));
			bankPayInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clTaskMain.getOperateDate())));
		}

		// bankPayInfo.setBusType(ClearConstant.BusinessType.BANK_PAY);
		bankPayInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 设置发生机构
		if (clTaskMain.getOffice() == null) {
			bankPayInfo.setOffice(UserUtils.getUser().getOffice());
		} else {
			bankPayInfo.setOffice(clTaskMain.getOffice());
		}

		return clInMainDao.getDetailList(bankPayInfo);
	}

	/**
	 * @author
	 * @version
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
		// 查询当日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型
		info.setBusType(clTaskMain.getBusType());
		// 设置任务类型为任务分配
		info.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		// 设置计划类型为正常清分
		info.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		if (clTaskMain.getOperateDate() != null) {
			info.setOperateDateStart(DateUtils.formatDate(DateUtils.getDateStart(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));

			info.setOperateDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			info.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
			info.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		}
		// 获得当天任务分配流水
		List<ClTaskMain> clTaskMainList = this.findList(info);
		// 当天已分配数量
		long distribution = 0;
		for (ClTaskMain clTask : clTaskMainList) {
			if (clTask.getDenomination().equals(clTaskMain.getDenomination())) {
				distribution = distribution + clTask.getTotalCount();
			}
		}

		// 查询当日已回收数量
		ClTaskMain infos = new ClTaskMain();
		// 设置业务类型
		infos.setBusType(clTaskMain.getBusType());
		// 设置任务类型为任务回收
		infos.setTaskType(ClearConstant.TaskType.TASK_RECOVERY);
		// 设置计划类型为正常清分
		infos.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		if (clTaskMain.getOperateDate() != null) {
			infos.setOperateDateStart(DateUtils.formatDate(DateUtils.getDateStart(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));

			infos.setOperateDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			infos.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
			infos.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		}
		// 获得当天任务回收流水
		List<ClTaskMain> clTaskMainLists = this.findList(infos);
		// 当天已回收数量
		long distributions = 0;
		for (ClTaskMain clTasks : clTaskMainLists) {
			if (clTasks.getDenomination().equals(clTaskMain.getDenomination())) {
				distributions = distributions + clTasks.getTotalCount();
			}
		}

		// 查询当日员工工作量数量
		ClTaskMain infoa = new ClTaskMain();
		// 设置业务类型
		infoa.setBusType(clTaskMain.getBusType());
		// 设置任务类型为员工工作量
		infoa.setTaskType(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 设置计划类型为正常清分
		infoa.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		if (clTaskMain.getOperateDate() != null) {
			infoa.setOperateDateStart(DateUtils.formatDate(DateUtils.getDateStart(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));

			infoa.setOperateDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			infoa.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
			infoa.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		}
		// 获得当天员工工作量流水
		List<ClTaskMain> clTaskMainLista = this.findList(infoa);
		// 当天员工工作量
		long distributiona = 0;
		for (ClTaskMain clTaska : clTaskMainLista) {
			if (clTaska.getDenomination().equals(clTaskMain.getDenomination())) {
				distributiona = distributiona + clTaska.getTotalCount();
			}
		}
		// 获得可分配数量
		long distributionz = distribution - distributions - distributiona;
		// 若可分配数量小于分配数量
		if (distributionz < clTaskMain.getTotalCount()) {
			String strMessageContent = GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", "")
					+ "此面值分配数量小于可分配数量，分配数量：" + clTaskMain.getTotalCount().toString() + ",可分配数量："
					+ Long.toString(distributionz) + ",分配失败";

			throw new BusinessException("message.A1005", strMessageContent,
					new String[] { GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", ""),
							clTaskMain.getTotalCount().toString(), Long.toString(distributionz) });
		}

	}
	
	/**
	 * @author QPH	
	 * @version
	 * 员工工作量分配验证
	 * 
	 * 
	 * @param clTaskMain
	 */

	public synchronized void checkCountByEmp(ClTaskMain clTaskMain) {
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
		long distribution = 0;
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



		// 查询当日员工工作量数量
		ClTaskMain infoa = new ClTaskMain();
		// 设置业务类型
		infoa.setBusType(clTaskMain.getBusType());
		// 设置计划类型为正常清分
		infoa.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		if (clTaskMain.getOperateDate() != null) {
			infoa.setOperateDateStart(DateUtils.formatDate(DateUtils.getDateStart(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));

			infoa.setOperateDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clTaskMain.getOperateDate()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			infoa.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
			infoa.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		}
		infoa.getSqlMap().put("dsf", dataScopeFilter(infoa.getCurrentUser(), "o", null));
		// 获得当天员工工作量流水
		List<ClTaskMain> clTaskMainLista = this.findList(infoa);
		List<ClTaskMain> clTaskMainListUse = Lists.newArrayList();
		for (ClTaskMain clMain : clTaskMainLista) {
			if (!clMain.getTaskType().equals(ClearConstant.TaskType.TASK_RECOVERY)) {
				clTaskMainListUse.add(clMain);
			}
		}
		// 当天员工工作量
		long distributiona = 0;
		for (ClTaskMain clTaska : clTaskMainListUse) {
			if (clTaska.getDenomination().equals(clTaskMain.getDenomination())) {
				distributiona = distributiona + clTaska.getTotalCount();
			}
		}
		// 获得可分配数量
		long distributionz = distribution - distributiona;
		// 若可分配数量小于分配数量
		if (distributionz < clTaskMain.getTotalCount()) {
			String strMessageContent = GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", "")
					+ "此面值分配数量小于可分配数量，分配数量：" + clTaskMain.getTotalCount().toString() + ",可分配数量："
					+ Long.toString(distributionz) + ",分配失败";

			throw new BusinessException("message.A1005", strMessageContent,
					new String[] { GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", ""),
							clTaskMain.getTotalCount().toString(), Long.toString(distributionz) });
		}

	}
	
	
}