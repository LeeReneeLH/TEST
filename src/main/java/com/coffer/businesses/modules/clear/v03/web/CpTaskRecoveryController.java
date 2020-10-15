package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskRecoveryService;
import com.coffer.businesses.modules.clear.v03.service.ClearingGroupService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 复点管理任务回收Controller
 * 
 * @author sg
 * @version 2017-09-04
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/cpTaskRecovery")
public class CpTaskRecoveryController extends BaseController {

	@Autowired
	private ClTaskMainService clTaskMainService;
	@Autowired
	private ClTaskRecoveryService clTaskRecoveryService;
	@Autowired
	private ClearingGroupService clearingGroupService;

	@ModelAttribute
	public ClTaskMain get(@RequestParam(required = false) String taskNo) {
		ClTaskMain entity = null;
		if (StringUtils.isNotBlank(taskNo)) {
			entity = clTaskRecoveryService.get(taskNo);
		}
		if (entity == null) {
			entity = new ClTaskMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param clTaskMain
	 * @return 任务回收列页面表
	 */
	@RequiresPermissions("clear:v03:cpTaskRecovery:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClTaskMain cpTaskMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (cpTaskMain.getCreateTimeStart() == null) {
			cpTaskMain.setCreateTimeStart(new Date());
		}
		if (cpTaskMain.getCreateTimeEnd() == null) {
			cpTaskMain.setCreateTimeEnd(new Date());
		}
		/* end */
		cpTaskMain.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);

		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		// User userInfo = UserUtils.getUser();
		// cpTaskMain.setOffice(userInfo.getOffice());
		/* end */

		// 查询现金商行交款信息
		Page<ClTaskMain> page = clTaskRecoveryService.findPage(new Page<ClTaskMain>(request, response), cpTaskMain);
		model.addAttribute("cpTaskMain", cpTaskMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/cpTaskRecovery/cpTaskRecoveryList";
	}

	/**
	 * 跳转至登记页面
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param clTaskMain
	 * @return 任务回收登记页面
	 */
	@RequiresPermissions("clear:v03:cpTaskRecovery:view")
	@RequestMapping(value = "form")
	public String form(ClTaskMain cpTaskMain, Model model) {
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		// 当日已分配列表
		List<ClTaskMain> holeClTaskMainList = Lists.newArrayList();
		// 当日已回收列表
		List<ClTaskMain> holeClTaskRecoveryList = Lists.newArrayList();
		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型为复点
		info.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
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
		List<ClTaskMain> clTaskList = clTaskMainService.findList(info);
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

		model.addAttribute("holeClTaskMainList", holeClTaskMainList);
		model.addAttribute("holeClTaskRecoveryList", holeClTaskRecoveryList);
		model.addAttribute("cpTaskMain", cpTaskMain);
		return "modules/clear/v03/cpTaskRecovery/cpTaskRecoveryForm";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param model
	 * @return 任务回收列页面表
	 */
	@RequiresPermissions("clear:v03:cpTaskRecovery:edit")
	@RequestMapping(value = "save")
	public String save(ClTaskMain cpTaskMain, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "userCount", required = false) String userCount) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			String[] userSplit = userCount.split(",");
			List<String> userList = Arrays.asList(userSplit);
			// 设置清分组人员详细
			cpTaskMain.setUserList(userList);
			// 设置登录人
			cpTaskMain.setLoginUser(UserUtils.getUser());
			// 设置业务类型为复点
			cpTaskMain.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			cpTaskMain.setOffice(userInfo.getOffice());
			/* end */
			// 执行保存
			clTaskRecoveryService.save(cpTaskMain);
			// 保存任务成功
			message = msg.getMessage("message.I7300", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(cpTaskMain, model);
		}
		addMessage(model, message);
		return form(cpTaskMain, model);
	}

	/**
	 * 查看任务分配详细
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param clTaskMain
	 * @return 任务分配详细画面
	 */
	@RequestMapping(value = "view")
	public String view(ClTaskMain cpTaskMain, Model model) {
		// 获取对应goodsId
		String goodsId = clTaskRecoveryService.getclTaskMainMapKey(cpTaskMain);
		// 通过任务分配主表主键获取任务分配明细
		List<ClTaskDetail> clTaskDetailList = clTaskRecoveryService.getByMid(cpTaskMain.getTaskNo());
		cpTaskMain.setClTaskDetailList(clTaskDetailList);
		cpTaskMain.setGoodsId(goodsId);
		model.addAttribute("cpTaskMain", cpTaskMain);
		return "modules/clear/v03/cpTaskRecovery/cpTaskRecoveryDetail";
	}

	/**
	 * 获取清分组信息
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getClearGroup")
	public String getClearGroup(@RequestParam(value = "clearGroupId", required = false) String clearGroupId,
			Model model, HttpServletRequest request, HttpSession session) {
		// 获取当前面值
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		List<ClearingGroupDetail> clGroupDetailList = clearingGroupService.getByGroupId(clearGroupId);
		List<ClTaskMain> clTaskMainList = Lists.newArrayList();
		// 合计列表
		List<ClTaskDetail> clTaskMainListSum = Lists.newArrayList();
		// 根据清分组编号获取清分组
		ClearingGroup clearingGroup = clearingGroupService.get(clearGroupId);
		for (ClearingGroupDetail clGroupDetail : clGroupDetailList) {
			Long totalSum = 0L;
			User clUser = UserUtils.get(clGroupDetail.getUser().getId());
			clGroupDetail.setUserType(clUser.getUserType());
			// add qph 2017-11-20 begin
			List<String> userList = Lists.newArrayList();
			userList.add(clGroupDetail.getUser().getId());
			ClTaskMain clTaskMain = new ClTaskMain();
			// 设置工位类型
			clTaskMain.setTaskType(ClearConstant.TaskType.TASK_RECOVERY);
			// 设置业务类型
			clTaskMain.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
			// 设置发生机构
			clTaskMain.setOffice(UserUtils.getUser().getOffice());
			clTaskMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
			clTaskMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
			// 设置用户
			clTaskMain.setUserList(userList);
			List<ClTaskDetail> clTaskDetailList = clTaskMainService.getClearGroupByUserId(clTaskMain);
			List<ClTaskDetail> clTaskDetailListForPage = Lists.newArrayList();
			for (int a = 0; a < stoDictList.size(); a++) {
				StoDict tempStoDict = stoDictList.get(a);
				String code = tempStoDict.getValue();
				ClTaskDetail existBankPayInfo = null;
				// 设置默认为false
				boolean flag = false;
				for (ClTaskDetail clTaskDetail : clTaskDetailList) {
					clTaskDetail.setWorkingPositionType(clearingGroup.getWorkingPositionType());
					String keyCode = clTaskDetail.getDenomination();
					// 判断查询的面值code是否与数据字典code相等
					if (keyCode.equals(code)) {
						// 判断如果查询结果待清分为空，则页面显示0
						if (clTaskDetail.getTotalCount() == null) {
							clTaskDetail.setTotalCount(0L);
						} else {
							clTaskDetail.setTotalCount(clTaskDetail.getTotalCount());
							totalSum += clTaskDetail.getTotalCount();
						}
						existBankPayInfo = clTaskDetail;
						flag = true;
					}
				}
				// 如果查询的面值code与数据字典code不相等
				if (flag == false) {
					ClTaskDetail newBankPayInfo = new ClTaskDetail();
					newBankPayInfo.setDenomination(code);
					newBankPayInfo.setTotalCount(0L);
					clTaskDetailListForPage.add(newBankPayInfo);
				} else {
					clTaskDetailListForPage.add(existBankPayInfo);
				}
			}

			clTaskMain.setTotalCount(totalSum);
			clTaskMain.setClTaskDetailList(clTaskDetailListForPage);
			clTaskMain.setTaskUser(UserUtils.get(clGroupDetail.getUser().getId()));
			clTaskMain.setWorkingPositionType(clearingGroup.getWorkingPositionType());
			clTaskMainList.add(clTaskMain);
			// end
		}

		Long total = this.calTotalSum(clTaskMainList, stoDictList, clTaskMainListSum);
		model.addAttribute("total", total);
		model.addAttribute("clTaskMainListSum", clTaskMainListSum);
		model.addAttribute("clTaskMainList", clTaskMainList);
		model.addAttribute("clearingGroup", clearingGroup);
		request.setAttribute("clGroupDetailList", clGroupDetailList);
		model.addAttribute("clGroupDetailList", clGroupDetailList);

		return "modules/clear/v03/cleartask/clearGroup";
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年11月20日 计算清分组分配数量各面值总和
	 * 
	 * @param clTaskMainList
	 * @param stoDictList
	 * @param clTaskMainListSum
	 */

	private Long calTotalSum(List<ClTaskMain> clTaskMainList, List<StoDict> stoDictList,
			List<ClTaskDetail> clTaskMainListSum) {
		Long total = 0L;
		for (int a = 0; a < stoDictList.size(); a++) {
			ClTaskDetail clTaskDetailSum = new ClTaskDetail();
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			Long sumTotal = 0L;

			for (ClTaskMain clTaskMain : clTaskMainList) {
				for (ClTaskDetail clTaskDetail : clTaskMain.getClTaskDetailList()) {
					if (clTaskDetail.getDenomination().equals(code)) {
						sumTotal += clTaskDetail.getTotalCount();
						total += clTaskDetail.getTotalCount();
					}
				}
			}
			clTaskDetailSum.setDenomination(code);
			clTaskDetailSum.setTotalCount(sumTotal);
			clTaskMainListSum.add(clTaskDetailSum);
		}
		return total;
	}

	/**
	 * 返回列表
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param clTaskMain
	 * @param redirectAttributes
	 *            对象重定向传参
	 * @return 任务回收列表页面
	 */
	@RequestMapping(value = "back")
	public String back(ClTaskMain cpTaskMain, RedirectAttributes redirectAttributes) {
		return "redirect:" + adminPath + "/clear/v03/cpTaskRecovery/list";
	}

	/**
	 * 删除
	 * 
	 * @author sg
	 * @version 2017-09-04
	 * @param clTaskMain
	 * @return 任务回收列表页面
	 */
	@RequiresPermissions("clear:v03:cpTaskRecovery:edit")
	@RequestMapping(value = "delete")
	public String delete(ClTaskMain cpTaskMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		clTaskRecoveryService.delete(cpTaskMain);
		// 删除任务成功！
		addMessage(redirectAttributes, msg.getMessage("message.I7301", null, locale));
		return "redirect:" + Global.getAdminPath() + "/clear/v03/cpTaskRecovery/?repage";
	}

	/**
	 * 复点管理任务分配打印明细
	 * 
	 * @author wxz
	 * @version 2017年9月11日
	 * 
	 * 
	 * @param taskNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printClPoint")
	public String printClPoint(@RequestParam(value = "taskNo", required = true) String taskNo, Model model) {

		// 获取主表信息
		ClTaskMain clTaskMain = clTaskRecoveryService.get(taskNo);
		// 获取当前明细表信息
		List<ClTaskDetail> clTaskDetailList = clTaskRecoveryService.getByMid(taskNo);
		// clTaskMain.setClTaskDetailList(clTaskDetailList);
		for (ClTaskDetail clDetail : clTaskDetailList) {
			clDetail.setDenomination(clTaskMain.getDenomination());
			clDetail.setCurrency(clTaskMain.getCurrency());
		}
		model.addAttribute("clTaskDetailList", clTaskDetailList);
		model.addAttribute("countMoney", clTaskMain.getTotalAmt());
		model.addAttribute("countSum", clTaskMain.getTotalCount());
		/* 追加计算页码 修改人：sg 修改日期：2017-11-30 begin */
		// 获取最大页数
		int size = 0;
		if (clTaskDetailList.size() % 10 == 0) {
			size = clTaskDetailList.size() / 10;
		} else {
			size = clTaskDetailList.size() / 10 + 1;
		}
		model.addAttribute("size", size);
		/* end */
		// 打印审批明细
		return "modules/clear/v03/cpTaskRecovery/printTaskRecovery";
	}

	/**
	 * 批量打印
	 * 
	 * @author wxz
	 * @version 2017年9月11日
	 * 
	 * 
	 * @param taskNos
	 *            业务流水列表
	 * @return 列表页面
	 */
	@RequestMapping(value = "/batchPrint")
	public String batchPrint(@RequestParam(value = "taskNos", required = true) String taskNos, Model model) {

		String[] taskNoArray = taskNos.split(Constant.Punctuation.COMMA);
		List<String> taskNosList = Arrays.asList(taskNoArray);
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();

		// 总计金额
		BigDecimal countMoney = new BigDecimal("0");

		// 总计捆数
		Long countSum = 0L;
		/* 修改打印按人员分配数量和金额 wzj 2017-11-28begin */
		Map<String, List<ClTaskDetail>> map = Maps.newHashMap();
		// 遍历流水号
		for (String allId : taskNosList) {
			ClTaskMain clTaskMains = clTaskMainService.get(allId);
			// 通过任务分配主表主键获取任务分配明细
			List<ClTaskDetail> clTaskDetailLists = clTaskMainService.getByMid(clTaskMains.getTaskNo());
			for (ClTaskDetail clTaskDetailListsa : clTaskDetailLists) {
				// 将员工编号、面值进行拼接成键
				String keys = clTaskDetailListsa.getEmpNo() + clTaskMains.getDenomination();
				// map中存在该键
				if (map.containsKey(keys)) {
					map.get(keys).add(clTaskDetailListsa);
				} else {
					List<ClTaskDetail> list = Lists.newArrayList();
					list.add(clTaskDetailListsa);
					map.put(keys, list);
				}
			}

		}
		// 遍历map
		Iterator<String> keyIterator = map.keySet().iterator();
		String strKey = "";
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			// 员工钱捆数
			long empTotalCount = 0l;
			// 员工钱数
			BigDecimal empTotalAmt = new BigDecimal("0");
			// 键中截取出面值
			String denominationStr = strKey.substring(strKey.length() - 2);
			// 员工姓名
			String empName = null;
			List<ClTaskDetail> tempItem = map.get(strKey);
			for (ClTaskDetail clTaskDetail : tempItem) {
				// 计算员工捆数和
				empTotalCount = empTotalCount + clTaskDetail.getTotalCount();
				// 计算员工钱数和
				empTotalAmt = empTotalAmt.add(clTaskDetail.getTotalAmt());
				empName = clTaskDetail.getEmpName();
			}
			ClTaskDetail clTaskDetailDate = new ClTaskDetail();
			// 放入面值
			clTaskDetailDate.setDenomination(denominationStr);
			// 放入员工捆数和
			clTaskDetailDate.setTotalCount(empTotalCount);
			// 放入员工金额和
			clTaskDetailDate.setTotalAmt(empTotalAmt);
			// 放入员工姓名
			clTaskDetailDate.setEmpName(empName);

			clTaskDetailList.add(clTaskDetailDate);
			// 计算合计数量
			countSum = countSum + empTotalCount;
			// 计算合计金额
			countMoney = countMoney.add(empTotalAmt);
		}
		// for (String taskNo : taskNosList) {
		// ClTaskMain clTaskMain = clTaskRecoveryService.get(taskNo);
		// // 获取当前明细表信息
		// List<ClTaskDetail> clTaskDetail =
		// clTaskRecoveryService.getByMid(taskNo);
		// for (ClTaskDetail clTask : clTaskDetail) {
		// clTask.setDenomination(clTaskMain.getDenomination());
		// clTask.setCurrency(clTaskMain.getCurrency());
		// clTaskDetailList.add(clTask);
		// }
		//
		// countMoney = countMoney.add(clTaskMain.getTotalAmt());
		// countSum = countSum + clTaskMain.getTotalCount();
		// }
		/* end */
		model.addAttribute("clTaskDetailList", clTaskDetailList);
		model.addAttribute("countMoney", countMoney);
		model.addAttribute("countSum", countSum);
		/* 追加计算页码 修改人：sg 修改日期：2017-11-30 begin */
		// 获取最大页数
		int size = 0;
		if (clTaskDetailList.size() % 10 == 0) {
			size = clTaskDetailList.size() / 10;
		} else {
			size = clTaskDetailList.size() / 10 + 1;
		}
		model.addAttribute("size", size);
		/* end */
		return "modules/clear/v03/cpTaskRecovery/printTaskRecovery";
	}

}