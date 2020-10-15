package com.coffer.businesses.modules.clear.v03.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClErrorInfoDao;
import com.coffer.businesses.modules.clear.v03.dao.EmpWorkStatisticsDao;
import com.coffer.businesses.modules.clear.v03.entity.ClErrorInfo;
import com.coffer.businesses.modules.clear.v03.entity.EmpWorkStatistics;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 员工工作量统计Service
 * 
 * @author XL
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class EmpWorkStatisticsService extends CrudService<EmpWorkStatisticsDao, EmpWorkStatistics> {

	@Autowired
	private EmpWorkStatisticsDao empWorkStatisticsDao;

	@Autowired
	private ClErrorInfoDao clErrorInfoDao;

	public EmpWorkStatistics get(String detailId) {
		return super.get(detailId);
	}

	/**
	 * 获取员工工作量统计列表
	 * 
	 * @author XL
	 * @version 2017-09-04
	 * @param page
	 * @param empWorkStatistics
	 * @return 员工工作量统计列表
	 */
	public Page<EmpWorkStatistics> findPage(Page<EmpWorkStatistics> page, EmpWorkStatistics empWorkStatistics) {
		// 设置任务类型，任务回收,工作量登记
		empWorkStatistics.getTaskTypes().add(ClearConstant.TaskType.TASK_RECOVERY);
		empWorkStatistics.getTaskTypes().add(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 设置清分状态，已清分
		empWorkStatistics.setTranStatus(ClearConstant.TranStatus.CLEARING_COMPLETE);
		// 查询条件： 开始时间
		if (empWorkStatistics.getOperateTimeStart() != null) {
			empWorkStatistics.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(empWorkStatistics.getOperateTimeStart())));
		}
		// 查询条件： 结束时间
		if (empWorkStatistics.getOperateTimeEnd() != null) {
			empWorkStatistics.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(empWorkStatistics.getOperateTimeEnd())));
		}
		// 工作量数据集合
		List<EmpWorkStatistics> dataList = empWorkStatisticsDao.findListData(empWorkStatistics);
		// 工作量页面显示集合,分页
		empWorkStatistics.setPage(page);
		List<EmpWorkStatistics> viewList = empWorkStatisticsDao.findListView(empWorkStatistics);
		// 设置页面显示数据
		for (EmpWorkStatistics empWorkData : dataList) {
			for (EmpWorkStatistics empWorkView : viewList) {
				// 用户编号与面值相等
				if (empWorkData.getEmpNo().equals(empWorkView.getEmpNo())
						&& empWorkData.getDenomination().equals(empWorkView.getDenomination())) {
					// 正常清分
					if (empWorkData.getPlanType().equals(ClearConstant.PlanType.NORMAL_CLEAR)) {
						// 机械正常
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.MECHANICS_CLEAR)) {
							empWorkView.setJxNormal(empWorkView.getJxNormal() + empWorkData.getTotalCount());
						}
						// 流水线正常
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.ASSEMBLY_LINE_CLEAR)) {
							empWorkView.setLsxNormal(empWorkView.getLsxNormal() + empWorkData.getTotalCount());
						}
						// 手工正常
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.MANUAL_CLEAR)) {
							empWorkView.setSgNormal(empWorkView.getSgNormal() + empWorkData.getTotalCount());
						}
					}
					// 重复清分
					if (empWorkData.getPlanType().equals(ClearConstant.PlanType.REPEAT_CLEAR)) {
						// 机械重复
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.MECHANICS_CLEAR)) {
							empWorkView.setJxRepeat(empWorkView.getJxRepeat() + empWorkData.getTotalCount());
						}
						// 流水线重复
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.ASSEMBLY_LINE_CLEAR)) {
							empWorkView.setLsxRepeat(empWorkView.getLsxRepeat() + empWorkData.getTotalCount());
						}
						// 手工重复
						if (empWorkData.getWorkingPositionType()
								.equals(ClearConstant.WorkingPositionType.MANUAL_CLEAR)) {
							empWorkView.setSgRepeat(empWorkView.getSgRepeat() + empWorkData.getTotalCount());
						}
					}
					// 抽查
					if (empWorkData.getPlanType().equals(ClearConstant.PlanType.CHECK_CLEAR)) {
						// 复点
						if (empWorkData.getBusType().equals(ClearConstant.TranStatus.CLEARING_COMPLETE)) {
							empWorkView
									.setCcComplexPoint(empWorkData.getTotalCount() + empWorkView.getCcComplexPoint());
						}
						// 清分
						if (empWorkData.getBusType().equals(ClearConstant.TranStatus.CLEARING)) {
							empWorkView.setCcClear(empWorkData.getTotalCount() + empWorkView.getCcClear());
						}

					}
					// 计算合计
					empWorkView.setTotalCountSta(empWorkView.getCcClear() + empWorkView.getCcComplexPoint()
							+ empWorkView.getJxNormal() + empWorkView.getJxRepeat() + empWorkView.getLsxNormal()
							+ empWorkView.getLsxRepeat() + empWorkView.getSgNormal() + empWorkView.getSgRepeat());
					// 设置员工名
					User user = UserUtils.get(empWorkView.getEmpNo());
					empWorkView.setEmpName(user.getName());
					break;
				}
			}
		}
		// 差错集合
		ClErrorInfo clErrorInfo = new ClErrorInfo();
		// 设置状态（登记）
		clErrorInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 设置业务类型
		clErrorInfo.setBusType(empWorkStatistics.getBusType());
		// 设置时间
		clErrorInfo.setSearchDateStart(empWorkStatistics.getSearchDateStart());
		clErrorInfo.setSearchDateEnd(empWorkStatistics.getSearchDateEnd());
		List<ClErrorInfo> errorList = clErrorInfoDao.findList(clErrorInfo);
		// 差错发现人是否有回收任务
		for (int i = errorList.size() - 1; i >= 0; i--) {
			boolean boo = true;
			ClErrorInfo empWorkError = errorList.get(i);
			for (EmpWorkStatistics empWorkData : dataList) {
				// 员工，面值，业务类型一致，显示差错
				if (empWorkData.getEmpNo().equals(empWorkError.getClearManNo())) {
					if (empWorkData.getDenomination().equals(empWorkError.getDenomination())) {
						if (empWorkData.getBusType().equals(empWorkError.getBusType())) {
							boo = false;
							break;
						}
					}
				}
			}
			// 差错发现人没有回收任务，不显示该差错
			if (boo) {
				errorList.remove(i);
			}
		}
		// 设置差错笔数
		for (ClErrorInfo empWorkError : errorList) {
			for (EmpWorkStatistics empWorkView : viewList) {
				// 用户编号与面值相等
				if (empWorkView.getEmpNo().equals(empWorkError.getClearManNo())
						&& empWorkView.getDenomination().equals(empWorkError.getDenomination())) {
					empWorkView.setErrorCount(empWorkView.getErrorCount() + empWorkError.getStrokeCount());
					break;
				}
			}
		}
		page.setList(viewList);
		return page;
	}

	/**
	 * 根据工位类型统计
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	@Transactional(readOnly = true)
	public List<EmpWorkStatistics> findWorkingType(EmpWorkStatistics empWorkStatistics) {
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));
		return empWorkStatisticsDao.findWorkingType(empWorkStatistics);
	}

	/**
	 * 根据人员姓名统计
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	@Transactional(readOnly = true)
	public List<EmpWorkStatistics> findPeople(EmpWorkStatistics empWorkStatistics) {
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));
		return empWorkStatisticsDao.findPeople(empWorkStatistics);
	}

	/**
	 * 根据工位类型和捆数统计图表
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> graphicalHandInList(List<EmpWorkStatistics> handClear,
			List<EmpWorkStatistics> handPoint) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 统计时间
		// List<String> timeDataList = Lists.newArrayList();
		// 统计量
		List<EmpWorkStatistics> dataListTotal = Lists.newArrayList();
		EmpWorkStatistics emp = new EmpWorkStatistics();
		// 机械清分数量
		long clearJx = 0;
		// 手工清分数量
		long clearSg = 0;
		for (EmpWorkStatistics entity : handClear) {

			if (ClearConstant.WorkingPositionType.MECHANICS_CLEAR.equals(entity.getWorkingPositionType())) {
				// seriesDataList.add(String.valueOf(entity.getTotalCount()));
				// 机械清分进行数量增加
				clearJx = clearJx + entity.getTotalCount();
			} else if (ClearConstant.WorkingPositionType.MANUAL_CLEAR.equals(entity.getWorkingPositionType())) {
				// seriesDataList.add(String.valueOf(entity.getTotalCount()));
				// 手工清分进行数量增加
				clearSg = clearSg + entity.getTotalCount();
			}

		}
		/* 判断机械清分和手动清分是否为0如果为零设他为空 修改人:sg 修改日期:2017-12-07 begin */
		// 放机械清分数量
		emp.setMechanicsClear(String.valueOf(clearJx).equals("0") ? null : String.valueOf(clearJx));
		// 放手工清分数量
		emp.setManualClear(String.valueOf(clearSg).equals("0") ? null : String.valueOf(clearSg));
		/* end */
		for (EmpWorkStatistics entity : handPoint) {
			// 复点数量
			emp.setAssemblyLineClear(String.valueOf(entity.getTotalCount()));
		}
		/* 追加如果三种方式全为空则认定没有数据 修改人:sg 修改日期: 2017-12-07 begin */
		if (emp.getMechanicsClear() != null || emp.getManualClear() != null || emp.getAssemblyLineClear() != null) {
			dataListTotal.add(emp);
		}
		/* end */
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, dataListTotal);
		// rtnMap.put("timeDataList", timeDataList);
		return rtnMap;
	}

	/**
	 * 根据人员和捆数统计图表
	 * 
	 * @author wxz
	 * @version 2017-10-17
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> peopleHandInList(List<EmpWorkStatistics> handIn) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 统计时间
		// List<String> timeDataList = Lists.newArrayList();
		// 统计量
		List<EmpWorkStatistics> dataListTotal = Lists.newArrayList();
		// 工位类型和姓名的统计量
		List<EmpWorkStatistics> workListTotal = Lists.newArrayList();
		/* 修改查询结果 wzj 2017-12-5 begin */
		long jx = 0;
		long sg = 0;
		long fd = 0;
		long total = 0;
		// 根据工位类型员工姓名统计
		for (EmpWorkStatistics workType : handIn) {
			EmpWorkStatistics empWork = new EmpWorkStatistics();
			EmpWorkStatistics emp = new EmpWorkStatistics();
			// 机械清分
			jx = Long.parseLong(workType.getJ1());
			// 手工清分
			sg = Long.parseLong(workType.getS1());
			// 复点
			fd = Long.parseLong(workType.getF1());
			// 总和
			total = jx + sg + fd;
			// 放员工名称
			emp.setEmpName(workType.getEmpName());
			// 放入总和
			emp.setTotalCount(total);
			/* 判断总和是否为零，如果为零则视为无数据 修改人:sg 修改日期:2017-12-07 begin */
			if (total != 0) {
				dataListTotal.add(emp);
			}
			// 放入机械清分
			empWork.setJ1(workType.getJ1());
			// 放入手工清分
			empWork.setS1(workType.getS1());
			// 放入复点
			empWork.setF1(workType.getF1());
			// 放入 员工名称
			empWork.setEmpName(workType.getEmpName());
			// 放入总和
			empWork.setTotalCount(total);
			workListTotal.add(empWork);
		}
		/* end */
		rtnMap.put("workListTotal", workListTotal);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, dataListTotal);
		// rtnMap.put("timeDataList", timeDataList);
		return rtnMap;
	}

	/**
	 * 分页
	 * 
	 * @author wzj
	 * @version 2017-11-22
	 * @param empWorkStatistics
	 * @param page
	 * @return 分页员工工作量信息页面表
	 */
	public Page<EmpWorkStatistics> findPageAllList(Page<EmpWorkStatistics> page, EmpWorkStatistics empWorkStatistics) {
		// 进行数据查询和分页
		empWorkStatistics.setPage(page);
		// 分页查询
		List<EmpWorkStatistics> empWorkStatisticsPageList = findEmpWorkStatisticsList(empWorkStatistics);
		page.setList(empWorkStatisticsPageList);
		return page;
	}

	/**
	 * 根据查询条件，查询客户清分量信息
	 * 
	 * @author wzj
	 * @version 2017-11-22
	 * @param empWorkStatistics
	 * @param page
	 * @return 查询员工工作量信息页面表
	 */
	public List<EmpWorkStatistics> findEmpWorkStatisticsList(EmpWorkStatistics empWorkStatistics) {
		// 查询条件： 开始时间
		if (empWorkStatistics.getOperateTimeStart() != null) {
			empWorkStatistics.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(empWorkStatistics.getOperateTimeStart())));
		}
		// 查询条件： 结束时间
		if (empWorkStatistics.getOperateTimeEnd() != null) {
			empWorkStatistics.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(empWorkStatistics.getOperateTimeEnd())));
		}
		List<String> type = Lists.newArrayList();
		// 员工工作量类型"03"
		type.add(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 任务回收类型"02"
		type.add(ClearConstant.TaskType.TASK_RECOVERY);
		empWorkStatistics.setTaskTypes(type);
		// qph add2017-11-24 数据穿透
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));
		List<EmpWorkStatistics> empWorkStatisticsAllDate = empWorkStatisticsDao.findListAllEmp(empWorkStatistics);
		return empWorkStatisticsAllDate;
	}

	/**
	 * 查询人员姓名统计图
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	@Transactional(readOnly = true)
	public List<EmpWorkStatistics> findPeopleList(EmpWorkStatistics empWorkStatistics) {
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));
		List<String> type = Lists.newArrayList();
		// 员工工作量类型"03"
		type.add(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 任务回收类型"02"
		type.add(ClearConstant.TaskType.TASK_RECOVERY);
		empWorkStatistics.setTaskTypes(type);
		return empWorkStatisticsDao.findPeopleList(empWorkStatistics);
	}

	/**
	 * 查询机械清分、 手工清分统计图
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	@Transactional(readOnly = true)
	public List<EmpWorkStatistics> findWorkClearingList(EmpWorkStatistics empWorkStatistics) {
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));
		List<String> type = Lists.newArrayList();
		// 员工工作量类型"03"
		type.add(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 任务回收类型"02"
		type.add(ClearConstant.TaskType.TASK_RECOVERY);
		empWorkStatistics.setTaskTypes(type);
		// 09为清点业务
		empWorkStatistics.setBusType(ClearConstant.BusinessType.CLEAR);
		return empWorkStatisticsDao.findWorkClearingList(empWorkStatistics);
	}

	/**
	 * 查询复点统计图
	 * 
	 * @author wzj
	 * @version 2017-12-4
	 */
	@Transactional(readOnly = true)
	public List<EmpWorkStatistics> findWorkPointList(EmpWorkStatistics empWorkStatistics) {
		empWorkStatistics.getSqlMap().put("dsf", dataScopeFilter(empWorkStatistics.getCurrentUser(), "o", null));

		List<String> type = Lists.newArrayList();
		// 员工工作量类型"03"
		type.add(ClearConstant.TaskType.STAFF_WORKLOAD);
		// 任务回收类型"02"
		type.add(ClearConstant.TaskType.TASK_RECOVERY);
		empWorkStatistics.setTaskTypes(type);
		// 08为复点业务
		empWorkStatistics.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
		return empWorkStatisticsDao.findWorkPointList(empWorkStatistics);
	}
}
