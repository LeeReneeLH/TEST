package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 账务日结Service
 * 
 * @author QPH
 * @version 2017-09-08
 */
@Service
@Transactional(readOnly = true)
public class DoorDayReportMainService extends CrudService<DoorDayReportMainDao, DoorDayReportMain> {

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	public DoorDayReportMain get(String reportId) {
		return super.get(reportId);
	}

	public List<DoorDayReportMain> findList(DoorDayReportMain dayReportMain) {
		return super.findList(dayReportMain);
	}

	public List<DoorDayReportMain> findChartList(DoorDayReportMain dayReportMain) {
		Office office = null;
		if (dayReportMain.getOffice() != null) {
			office = SysCommonUtils.findOfficeById(dayReportMain.getOffice().getId());
		}
		if (office == null || (office.getType()).equals(Constant.OfficeType.DIGITAL_PLATFORM)) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			dayReportMain.setOffice(userInfo.getOffice());
			dayReportMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		} else {

			dayReportMain.setOffice(office);
			dayReportMain.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + office.getId() + "%'");
		}
		return dao.findChartList(dayReportMain);
	}

	// 根据开始时间结束时间查询数据(清分统计图用)
	public List<DoorDayReportMain> findChartaList(DoorDayReportMain dayReportMain) {
		Office office = null;
		if (dayReportMain.getOffice() != null) {
			office = SysCommonUtils.findOfficeById(dayReportMain.getOffice().getId());
		}
		if (office == null || (office.getType()).equals(Constant.OfficeType.DIGITAL_PLATFORM)) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			dayReportMain.setOffice(userInfo.getOffice());
			dayReportMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		} else {

			dayReportMain.setOffice(office);
			dayReportMain.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + office.getId() + "%'");
		}
		return dao.findChartaList(dayReportMain);
	}

	public List<DoorDayReportMain> findChartsList(DoorDayReportMain dayReportMain) {
		return dao.findChartsList(dayReportMain);
	}

	public Page<DoorDayReportMain> findPage(Page<DoorDayReportMain> page, DoorDayReportMain dayReportMain) {
		dayReportMain.getSqlMap().put("dsf", dataScopeFilter(dayReportMain.getCurrentUser(), "o", null));
		// 查询条件：开始时间
		if (dayReportMain.getCreateTimeStart() != null) {
			dayReportMain
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(dayReportMain.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (dayReportMain.getCreateTimeEnd() != null) {
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(dayReportMain.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}

		return super.findPage(page, dayReportMain);
	}

	@Transactional(readOnly = false)
	public void save(DoorDayReportMain dayReportMain) {
		super.save(dayReportMain);
	}

	@Transactional(readOnly = false)
	public void delete(DoorDayReportMain dayReportMain) {
		super.delete(dayReportMain);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 按结账日期降序查询
	 * 
	 * @param dayReportMain
	 * @return
	 */
	public List<DoorDayReportMain> findListByReportDate(DoorDayReportMain dayReportMain) {
		return dao.findListByReportDate(dayReportMain);
	}

	/**
	 * 清分中心:折线图数据过滤
	 * 
	 * @author sg
	 * @version 2017年10月16日
	 * 
	 * @param dayReportMain
	 *            主表list
	 */
	public Map<String, Object> graphicalFoldLine(List<DoorDayReportMain> dayReportMain,
			DoorDayReportMain alllatticePointHandin) {
		// 初始化
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 图表X轴的值
		List<String> xAxisDataList = Lists.newArrayList();
		// 添加数据
		// 收入
		List<BigDecimal> dataList = Lists.newArrayList();
		// 支出
		List<BigDecimal> dataLists = Lists.newArrayList();
		// 余额
		List<BigDecimal> dataLista = Lists.newArrayList();
		// 将查询当天的时间转化成字符型
		String searchDateStart = DateUtils.formatDate(DateUtils.getDateStart(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
		String searchDateEnd = DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 查询条件：开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 判断用户类型
		User userInfo = UserUtils.getUser();
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			centerAccountsMain.setClientId(userInfo.getOffice().getId());
			// 清分中心及平台
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// add qph 设置发生机构 2017-11-17 begin
			centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			// end
		}
		// 业务类型为现金
		List<String> busTypelists = Lists.newArrayList();
		busTypelists = Arrays.asList(Global.getStringArray("accounts.businessType.cash"));
		centerAccountsMain.setBusinessTypes(busTypelists);
		// 查询当天所有的收入和支出
		DoorCenterAccountsMain list = centerAccountsMainService.findSumList(centerAccountsMain);

		// 求得当天的余额
		BigDecimal totelAmounts = null;
		// 判断查询是否为空
		if (Collections3.isEmpty(dayReportMain) && list != null) {
			if (list.getInAmount() == null) {
				list.setInAmount(new BigDecimal(0));
			}
			if (list.getOutAmount() == null) {
				list.setOutAmount(new BigDecimal(0));
			}
			// 判断查询日期是否包含今天
			if (searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
					|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
					|| alllatticePointHandin.getSearchDateEnd() == null) {
				DoorCenterAccountsMain centerAccountsMains = new DoorCenterAccountsMain();

				// add qph 设置发生机构 2017-11-17 begin
				// centerAccountsMains.setRofficeId(userInfo.getOffice().getId());
				// centerAccountsMains.getSqlMap().put("dsf",
				// "OR o.parent_ids LIKE '%" +
				// UserUtils.getUser().getOffice().getId() + "%'");
				// end
				// 商行或人行
				if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
						|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
					centerAccountsMains.setClientId(userInfo.getOffice().getId());
					// 清分中心及平台
				} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
						|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
					centerAccountsMains.setRofficeId(userInfo.getOffice().getId());
					centerAccountsMains.getSqlMap().put("dsf",
							"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
					// end
				}
				// 业务类型
				List<String> busTypelist = Lists.newArrayList();
				// 现金业务
				busTypelist = Arrays.asList(Global.getStringArray("accounts.businessType.cash"));
				centerAccountsMains.setBusinessTypes(busTypelist);
				// 获取现金业务余额
				BigDecimal cash = new BigDecimal(0);
				List<DoorCenterAccountsMain> centerAccountsMainList = centerAccountsMainService
						.findList(centerAccountsMains);
				if (!Collections3.isEmpty(centerAccountsMainList)) {
					cash = centerAccountsMainList.get(0).getTotalAmount();
				}
				/*
				 * // 备付金业务 busTypelist = Arrays.asList(Global.getStringArray(
				 * "accounts.businessType.pro"));
				 * centerAccountsMains.setBusinessTypes(busTypelist); BigDecimal
				 * pro = new BigDecimal(0); List<CenterAccountsMain>
				 * centerAccountsMainLists = centerAccountsMainService
				 * .findList(centerAccountsMains); if
				 * (!Collections3.isEmpty(centerAccountsMainLists)) { pro =
				 * centerAccountsMainLists.get(0).getTotalAmount(); }
				 */
				// 总余额
				totelAmounts = cash;
				if (list.getOutAmount() != null || list.getInAmount() != null) {
					Calendar c = Calendar.getInstance();
					String year = String.valueOf(c.get(Calendar.YEAR));
					if ("yyyy-IW".equals(alllatticePointHandin.getFilterCondition())) {
						// 判断当前时间是今年的第几周
						int i = c.get(Calendar.WEEK_OF_YEAR);
						xAxisDataList.add(year + "-" + String.valueOf(i));
					} else if ("yyyy-Q".equals(alllatticePointHandin.getFilterCondition())) {
						int i = (c.get(Calendar.MONTH) + 1);
						// 判断当前时间是今年的第几季度
						int quarter = 0;
						if (1 <= i && i <= 3) {
							quarter = 1;
						} else if (4 <= i && i <= 6) {
							quarter = 2;
						} else if (7 <= i && i <= 9) {
							quarter = 3;
						} else {
							quarter = 4;
						}
						xAxisDataList.add(year + "-" + String.valueOf(quarter));
					} else {
						xAxisDataList.add(
								DateUtils.getDate(alllatticePointHandin.getFilterCondition().replaceAll("mm", "MM")));
					}
					dataList.add(list.getInAmount());
					dataLists.add(list.getOutAmount());
					dataLista.add(totelAmounts);
				}
			}

		} else {
			// 将取得的折线图数据进行循环
			for (DoorDayReportMain entitys : dayReportMain) {
				// 添加收入曲线数据
				dataList.add(entitys.getInAmount());
				// 添加支出曲线数据
				dataLists.add(entitys.getOutAmount());
				// 添加余额曲线数据
				dataLista.add(entitys.getTotalAmount());
				// x轴中添加时间
				if (!xAxisDataList.contains(entitys.getHandInDate())) {
					xAxisDataList.add(entitys.getHandInDate());
				}
			}

			if (list != null && !Collections3.isEmpty(dataList) && !Collections3.isEmpty(dataLists)
					&& !Collections3.isEmpty(dataLista)) {
				if (list.getInAmount() == null) {
					list.setInAmount(new BigDecimal(0));
				}
				if (list.getOutAmount() == null) {
					list.setOutAmount(new BigDecimal(0));
				}
				if (dataLista.get(dataLista.size() - 1) == null) {
					dataLista.set(dataLista.size() - 1, new BigDecimal(0));
				}
				// 判断是否需要显示出当天的数据
				if ("yyyy-mm-dd".equals(alllatticePointHandin.getFilterCondition())
						&& ((alllatticePointHandin.getSearchDateEnd() == null
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd()))
								|| searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
								|| (alllatticePointHandin.getSearchDateStart() != null
										&& alllatticePointHandin.getSearchDateEnd() == null))) {
					// 判断是否存在今天数据有则修改没有则新增一条
					if (DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD)
							.equals(xAxisDataList.get(xAxisDataList.size() - 1))) {
						// 用当天的数据替换曲线中最后一条数据
						totelAmounts = (dataLista.get(dataLista.size() - 1)
								.add(dataLists.get(dataLists.size() - 1).subtract(dataList.get(dataList.size() - 1))))
										.add(list.getInAmount().subtract(list.getOutAmount()));
						dataList.remove(dataList.size() - 1);
						dataLists.remove(dataLists.size() - 1);
						dataList.add(list.getInAmount());
						dataLists.add(list.getOutAmount());
						dataLista.set(dataLista.size() - 1, totelAmounts);
					} else {
						totelAmounts = dataLista.get(dataLista.size() - 1)
								.add(list.getInAmount().subtract(list.getOutAmount()));
						xAxisDataList
								.add(DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD));
						dataList.add(list.getInAmount());
						dataLists.add(list.getOutAmount());
						dataLista.add(totelAmounts);
					}
				} else if ((!"yyyy-mm-dd".equals(alllatticePointHandin.getFilterCondition()))
						&& ((alllatticePointHandin.getSearchDateEnd() == null
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd()))
								|| searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
								|| (alllatticePointHandin.getSearchDateStart() != null
										&& alllatticePointHandin.getSearchDateEnd() == null))) {
					BigDecimal inAmounts = dataList.get(dataList.size() - 1);
					BigDecimal outAmounts = dataLists.get(dataList.size() - 1);
					BigDecimal totalAmounts = dataLista.get(dataList.size() - 1);
					DoorDayReportMain dayReportMains = new DoorDayReportMain();
					dayReportMains.setSearchDateStart(searchDateStart);
					dayReportMains.setSearchDateEnd(searchDateEnd);
					dayReportMains.setFilterCondition("yyyy-mm-dd");
					// 设置类型为有效
					dayReportMains.setStatus(ClearConstant.AccountsStatus.SUCCESS);
					// 设置账务类型不为备付金
					dayReportMains.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
					List<DoorDayReportMain> handInDegree = this.findChartList(dayReportMains);
					List<DoorDayReportMain> handInDegrees = this.findChartaList(dayReportMains);
					for (DoorDayReportMain dayReport : handInDegree) {
						for (DoorDayReportMain dayReports : handInDegrees) {
							// 判断日期是否一样
							Calendar c = Calendar.getInstance();
							c.setTime(dayReports.getReportDate());
							String year = String.valueOf(c.get(Calendar.YEAR));
							// 如果是按周查询
							if ("yyyy-IW".equals(dayReportMains.getFilterCondition())) {
								// 判断当前时间是今年的第几周
								int i = c.get(Calendar.WEEK_OF_YEAR);
								if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(i))) {
									dayReport.setTotalAmount(dayReports.getTotalAmount());
									dayReport.setBeforeAmount(dayReports.getBeforeAmount());
									break;
								}
								// 如果是按季度查询
							} else if ("yyyy-Q".equals(dayReportMains.getFilterCondition())) {
								int i = (c.get(Calendar.MONTH) + 1);
								// 判断当前时间是今年的第几季度
								int quarter = 0;
								if (1 <= i && i <= 3) {
									quarter = 1;
								} else if (4 <= i && i <= 6) {
									quarter = 2;
								} else if (7 <= i && i <= 9) {
									quarter = 3;
								} else {
									quarter = 4;
								}
								if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(quarter))) {
									dayReport.setTotalAmount(dayReports.getTotalAmount());
									dayReport.setBeforeAmount(dayReports.getBeforeAmount());
									break;
								}
							} else {
								if (dayReport.getHandInDate().equals(DateUtils.formatDate(dayReports.getReportDate(),
										dayReportMains.getFilterCondition().replaceAll("mm", "MM")))) {
									dayReport.setTotalAmount(dayReports.getTotalAmount());
									dayReport.setBeforeAmount(dayReports.getBeforeAmount());
									break;
								}
							}
						}
					}
					// 判断今天是否有结算
					if (Collections3.isEmpty(handInDegree)) {
						// 获取Calendar对象
						Calendar c = Calendar.getInstance();
						// 获取年
						String year = String.valueOf(c.get(Calendar.YEAR));
						// 如果过滤条件为周
						if ("yyyy-IW".equals(alllatticePointHandin.getFilterCondition())) {
							// 判断当前时间是今年的第几周
							int i = c.get(Calendar.WEEK_OF_YEAR);
							// 将日期转换成字符串
							String weekDay = year + "-" + String.valueOf(i);
							// 判断查询的最后一条信息是否与当天所匹配
							if (!weekDay.equals(xAxisDataList.get(xAxisDataList.size() - 1))) {
								dataList.add(list.getInAmount());
								dataLists.add(list.getOutAmount());
								dataLista.add(list.getInAmount().subtract(list.getOutAmount()));
								xAxisDataList.add(weekDay);
							} else {
								dataList.remove(dataList.size() - 1);
								dataLists.remove(dataLists.size() - 1);
								dataLista.remove(dataLista.size() - 1);
								dataList.add(inAmounts.add(list.getInAmount()));
								dataLists.add(outAmounts.add(list.getOutAmount()));
								dataLista.add(totalAmounts.add(list.getInAmount().subtract(list.getOutAmount())));
							}
						} else if ("yyyy-Q".equals(alllatticePointHandin.getFilterCondition())) {
							int i = (c.get(Calendar.MONTH) + 1);
							// 判断当前时间是今年的第几季度
							int quarter = 0;
							if (1 <= i && i <= 3) {
								quarter = 1;
							} else if (4 <= i && i <= 6) {
								quarter = 2;
							} else if (7 <= i && i <= 9) {
								quarter = 3;
							} else {
								quarter = 4;
							}
							String weekQ = year + "-" + String.valueOf(quarter);
							// 判断查询的最后一条信息是否与当天所匹配
							if (!weekQ.equals(xAxisDataList.get(xAxisDataList.size() - 1))) {
								dataList.add(list.getInAmount());
								dataLists.add(list.getOutAmount());
								dataLista.add(list.getInAmount().subtract(list.getOutAmount()));
								xAxisDataList.add(weekQ);
							} else {
								dataList.remove(dataList.size() - 1);
								dataLists.remove(dataLists.size() - 1);
								dataLista.remove(dataLista.size() - 1);
								dataList.add(inAmounts.add(list.getInAmount()));
								dataLists.add(outAmounts.add(list.getOutAmount()));
								dataLista.add(totalAmounts.add(list.getInAmount().subtract(list.getOutAmount())));
							}
						} else if (!DateUtils.getDate(alllatticePointHandin.getFilterCondition().replaceAll("mm", "MM"))
								.equals(xAxisDataList.get(xAxisDataList.size() - 1))) {
							dataList.add(list.getInAmount());
							dataLists.add(list.getOutAmount());
							dataLista.add(list.getInAmount().subtract(list.getOutAmount()));
							xAxisDataList.add(DateUtils
									.getDate(alllatticePointHandin.getFilterCondition().replaceAll("mm", "MM")));
						} else {
							dataList.remove(dataList.size() - 1);
							dataLists.remove(dataLists.size() - 1);
							dataLista.remove(dataLista.size() - 1);
							dataList.add(inAmounts.add(list.getInAmount()));
							dataLists.add(outAmounts.add(list.getOutAmount()));
							dataLista.add(totalAmounts.add(list.getInAmount().subtract(list.getOutAmount())));
						}
					} else {
						for (DoorDayReportMain entity : handInDegree) {
							if (entity.getInAmount() == null) {
								entity.setInAmount(new BigDecimal(0));
							}
							if (entity.getOutAmount() == null) {
								entity.setOutAmount(new BigDecimal(0));
							}
							dataList.remove(dataList.size() - 1);
							dataLists.remove(dataLists.size() - 1);
							dataLista.remove(dataLista.size() - 1);
							dataList.add((inAmounts.subtract(entity.getInAmount())).add(list.getInAmount()));
							dataLists.add((outAmounts.subtract(entity.getOutAmount())).add(list.getOutAmount()));
							dataLista.add(
									entity.getBeforeAmount().add(list.getInAmount().subtract(list.getOutAmount())));
						}
					}
				}

			}
		}
		List<List<BigDecimal>> seriesDataList = Lists.newArrayList();
		seriesDataList.add(dataList);
		seriesDataList.add(dataLists);
		seriesDataList.add(dataLista);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}
}