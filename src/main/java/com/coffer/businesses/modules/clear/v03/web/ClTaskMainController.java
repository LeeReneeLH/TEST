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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroupDetail;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
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
 * 清分管理Controller
 * 
 * @author QPH
 * @version 2017-08-15
 */
@Controller
@SessionAttributes({ "clearTaskMainSession" })
@RequestMapping(value = "${adminPath}/clear/v03/clTaskMain")
public class ClTaskMainController extends BaseController {

	@Autowired
	private ClTaskMainService clTaskMainService;

	@Autowired
	private ClearingGroupService clearingGroupService;

	@ModelAttribute
	public ClTaskMain get(@RequestParam(required = false) String taskNo) {
		ClTaskMain entity = null;
		if (StringUtils.isNotBlank(taskNo)) {
			entity = clTaskMainService.get(taskNo);
		}
		if (entity == null) {
			entity = new ClTaskMain();
		}
		return entity;
	}

	@RequiresPermissions("clear:v03:clTaskMain:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClTaskMain clTaskMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (clTaskMain.getCreateTimeStart() == null) {
			clTaskMain.setCreateTimeStart(new Date());
		}
		if (clTaskMain.getCreateTimeEnd() == null) {
			clTaskMain.setCreateTimeEnd(new Date());
		}
		/* end */

		// 设置业务类型为清分
		clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
		Page<ClTaskMain> page = clTaskMainService.findPage(new Page<ClTaskMain>(request, response), clTaskMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/cleartask/clTaskMainList";
	}

	@RequiresPermissions("clear:v03:clTaskMain:view")
	@RequestMapping(value = "form")
	public String form(ClTaskMain clTaskMain, Model model) {
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		// 当日入库清分列表
		List<ClInMain> holeBankPayInfolist = Lists.newArrayList();
		// 当日已分配列表
		List<ClTaskMain> holeClTaskMainList = Lists.newArrayList();
		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		User userInfo = UserUtils.getUser();
		info.setOffice(userInfo.getOffice());
		/* end */
		// 设置业务类型为清分
		info.setBusType(ClearConstant.BusinessType.CLEAR);
		// 商行交款入库清分量
		List<ClInMain> bankPayInfoList = clTaskMainService.getDetailList(info);
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
		// 获得当天清分类型下任务分配流水
		List<ClTaskMain> clTaskMainList = clTaskMainService.findList(info);
		// 遍历出数据字典面值对应的code
		for (int a = 0; a < stoDictList.size(); a++) {
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			ClInMain existBankPayInfo = null;
			ClTaskMain existClTaskMain = null;
			// 设置默认为false
			boolean exist = false;
			boolean flag = false;
			// 遍历出数据库当日存入的面值code
			for (int b = 0; b < bankPayInfoList.size(); b++) {
				ClInMain tempBankPayInfo = bankPayInfoList.get(b);
				String keyCode = tempBankPayInfo.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					// 判断如果查询结果待清分为空，则页面显示0
					if (tempBankPayInfo.getCountBank() == null) {
						tempBankPayInfo.setCountBank("0");
					}
					existBankPayInfo = tempBankPayInfo;
					exist = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (exist == false) {
				ClInMain newBankPayInfo = new ClInMain();
				newBankPayInfo.setDenomination(code);
				newBankPayInfo.setCountBank("0");
				holeBankPayInfolist.add(newBankPayInfo);
			} else {
				holeBankPayInfolist.add(existBankPayInfo);
			}
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
		}
		model.addAttribute("holeClTaskMainList", holeClTaskMainList);
		model.addAttribute("bankPayInfo", holeBankPayInfolist);
		model.addAttribute("clTaskMain", clTaskMain);
		model.addAttribute("clearTaskMainSession", clTaskMain);
		return "modules/clear/v03/cleartask/clTaskMainForm";
	}

	@RequiresPermissions("clear:v03:clTaskMain:edit")
	@RequestMapping(value = "save")
	public String save(ClTaskMain clTaskMain, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "userCount", required = false) String userCount) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 获取前台传递过来的任务分配详细
			String[] userSplit = userCount.split(",");
			List<String> userList = Arrays.asList(userSplit);
			// 设置清分组人员详细
			clTaskMain.setUserList(userList);
			// 设置登录人
			clTaskMain.setLoginUser(UserUtils.getUser());
			// 设置业务类型为清分
			clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clTaskMain.setOffice(userInfo.getOffice());
			/* end */
			// 执行保存
			clTaskMainService.save(clTaskMain);
			// 保存任务成功
			message = msg.getMessage("message.I7300", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(clTaskMain, model);
		}
		// return "redirect:" + Global.getAdminPath() +
		// "/clear/v03/clTaskMain/form?repage";
		/* 通过保存登记后返回到登记页面 修改人：sg 修改时间：2017-10-26 begin */
		addMessage(model, message);
		return form(clTaskMain, model);
		/* end */
	}

	@RequiresPermissions("clear:v03:clTaskMain:edit")
	@RequestMapping(value = "delete")
	public String delete(ClTaskMain clTaskMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		clTaskMainService.delete(clTaskMain);
		addMessage(redirectAttributes, msg.getMessage("message.I7301", null, locale));
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clTaskMain/?repage";
	}

	/**
	 * @author qph
	 * @version 2017年8月28日
	 * 
	 *          查看任务分配详细
	 * @param clTaskMain
	 *            任务分配信息
	 * @param model
	 *            页面Session信息
	 * @return 任务分配详细画面
	 */
	@RequestMapping(value = "view")
	public String view(ClTaskMain clTaskMain, Model model) {
		// 获取对应goodsId
		String goodsId = clTaskMainService.getclTaskMainMapKey(clTaskMain);
		// 通过任务分配主表主键获取任务分配明细
		List<ClTaskDetail> clTaskDetailList = clTaskMainService.getByMid(clTaskMain.getTaskNo());
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		clTaskMain.setGoodsId(goodsId);
		model.addAttribute("clTaskMain", clTaskMain);

		return "modules/clear/v03/cleartask/clTaskMainDetail";
	}

	/**
	 * 获取清分组信息
	 * 
	 * @author qipeihong
	 * @date 2017-08-18
	 * 
	 * @Description
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
			clTaskMain.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
			// 设置业务类型
			clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
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
	 * @author qipeihong
	 * @date 2017年8月29日
	 * 
	 *       任务分配明细返回
	 * @param clTaskMain
	 *            调拨信息
	 * @param redirectAttributes
	 *            对象重定向传参
	 * @return 调拨信息列表页面
	 */
	@RequestMapping(value = "back")
	public String back(ClTaskMain clTaskMain, RedirectAttributes redirectAttributes) {
		return "redirect:" + adminPath + "/clear/v03/clTaskMain/list";
	}

	/**
	 * 打印
	 * 
	 * @author sg
	 * @version 2016年09月11日
	 * 
	 * 
	 * @param taskNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printclTaskMainDetail")
	public String printclTaskMainDetail(@RequestParam(value = "taskNo", required = true) String taskNo, Model model) {

		ClTaskMain clTaskMain = clTaskMainService.get(taskNo);
		// 通过任务分配主表主键获取任务分配明细
		List<ClTaskDetail> clTaskDetailList = clTaskMainService.getByMid(clTaskMain.getTaskNo());
		for (ClTaskDetail clTaskDetailLists : clTaskDetailList) {
			clTaskDetailLists.setDenomination(clTaskMain.getDenomination());
		}
		// 获取当前登录人所在的机构
		User user = UserUtils.getUser();
		model.addAttribute("user", user);
		model.addAttribute("clTaskDetailList", clTaskDetailList);
		model.addAttribute("totalCount", clTaskMain.getTotalCount());
		model.addAttribute("totalAmt", clTaskMain.getTotalAmt());
		// 获取最大页数
		int size = 0;
		if (clTaskDetailList.size() % 10 == 0) {
			size = clTaskDetailList.size() / 10;
		} else {
			size = clTaskDetailList.size() / 10 + 1;
		}
		model.addAttribute("size", size);
		// 打印任务分配明细
		return "modules/clear/v03/cleartask/printClTaskMainDetails";
	}

	/**
	 * 批量打印
	 * 
	 * @author sg
	 * @version 2017年9月11日
	 * 
	 * 
	 * @param allIds
	 *            业务编号列表
	 * @return 列表页面
	 */
	@RequestMapping(value = "/batchPrint")
	public String batchPrint(@RequestParam(value = "allIds", required = true) String allIds, Model model) {
		// 获得画面传过来的值并用(,)分隔
		String[] allIdArray = allIds.split(Constant.Punctuation.COMMA);
		// 将数组转换成集合
		List<String> allIdList = Arrays.asList(allIdArray);
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		// 总计捆数
		long totalCount = 0l;
		// 总计金额
		BigDecimal totalAmt = new BigDecimal("0");
		/* 修改打印按人员分配数量和金额 wzj 2017-11-28begin */
		Map<String, List<ClTaskDetail>> map = Maps.newHashMap();
		// 遍历流水号
		for (String allId : allIdList) {
			ClTaskMain clTaskMains = clTaskMainService.get(allId);
			// 通过任务分配主表主键获取任务分配明细
			List<ClTaskDetail> clTaskDetailLists = clTaskMainService.getByMid(clTaskMains.getTaskNo());
			for (ClTaskDetail clTaskDetailListsa : clTaskDetailLists) {
				// 将员工编号、面值、工位类型进行拼接成键
				String keys = clTaskDetailListsa.getEmpNo() + clTaskMains.getDenomination()
						+ clTaskDetailListsa.getWorkingPositionType();
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
			// 键中截取出面值和工位类型
			String denominationAndWordTypeStr = strKey.substring(strKey.length() - 4);
			// 面值
			String denominationStr = denominationAndWordTypeStr.substring(0, 2);
			// 工位类型
			String workTypeStr = denominationAndWordTypeStr.substring(denominationAndWordTypeStr.length() - 2);
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
			// 放入工位类型
			clTaskDetailDate.setWorkingPositionType(workTypeStr);
			clTaskDetailList.add(clTaskDetailDate);
			// 计算合计数量
			totalCount = totalCount + empTotalCount;
			// 计算合计金额
			totalAmt = totalAmt.add(empTotalAmt);
		}
		// 获取当前登录人信息
		User user = UserUtils.getUser();
		model.addAttribute("user", user);
		model.addAttribute("clTaskDetailList", clTaskDetailList);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalAmt", totalAmt);
		// 获取最大页数
		int size = 0;
		if (clTaskDetailList.size() % 10 == 0) {
			size = clTaskDetailList.size() / 10;
		} else {
			size = clTaskDetailList.size() / 10 + 1;
		}
		model.addAttribute("size", size);
		// 打印审批明细
		return "modules/clear/v03/cleartask/printClTaskMainDetails";
	}

	/**
	 * 清点任务分配捆数验证
	 * 
	 * @author XL
	 * @version 2017年10月26日
	 * @param clTaskMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "checkTaskCount")
	@ResponseBody
	public String checkTaskCount(ClTaskMain clTaskMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		try {
			// 设置业务类型
			clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clTaskMain.setOffice(userInfo.getOffice());
			/* end */
			// 验证捆数
			ClearCommonUtils.checkCount(clTaskMain);
			map.put("result", "success");
		} catch (BusinessException be) {
			map.put("result", "error");
			map.put("denominantionLabel", GoodDictUtils.getDictLabel(clTaskMain.getDenomination(), "cnypden", ""));
		}
		return gson.toJson(map);
	}
}