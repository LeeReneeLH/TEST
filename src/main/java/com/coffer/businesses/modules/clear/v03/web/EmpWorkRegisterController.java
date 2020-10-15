package com.coffer.businesses.modules.clear.v03.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
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
import com.coffer.businesses.modules.clear.v03.service.EmpWorkRegisterService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 员工工作量登记Controller
 * 
 * @author wxz
 * @version 2017-09-20
 */
@Controller
@SessionAttributes({ "clearTaskMainSession" })
@RequestMapping(value = "${adminPath}/clear/v03/empWorkRegister")
public class EmpWorkRegisterController extends BaseController {

	@Autowired
	private EmpWorkRegisterService empWorkRegisterService;

	@Autowired
	private ClearingGroupService clearingGroupService;
	@Autowired
	private ClTaskMainService clTaskMainService;

	@ModelAttribute
	public ClTaskMain get(@RequestParam(required = false) String taskNo) {
		ClTaskMain entity = null;
		if (StringUtils.isNotBlank(taskNo)) {
			entity = empWorkRegisterService.get(taskNo);
		}
		if (entity == null) {
			entity = new ClTaskMain();
		}
		return entity;
	}

	@RequiresPermissions("clear:v03:empWorkRegister:view")
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
		Page<ClTaskMain> page = empWorkRegisterService.findPage(new Page<ClTaskMain>(request, response), clTaskMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/empWorkRegister/empWorkRegisterList";
	}

	@RequestMapping(value = "form")
	public String form(ClTaskMain clTaskMain, Model model) {
		Subject subject = SecurityUtils.getSubject();

		if (subject.isPermitted("clear:v03:empWorkRegister:edit")) {
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clTaskMain.setOffice(userInfo.getOffice());
			/* end */
			/* 设置员工工作量默认时间以及业务类型 修改人：qph 修改时间：2017-12-21 begin*/
			clTaskMain.setOperateDate(new Date());
			clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);
			/* end*/
			List<ClInMain> bankPayInfoList = empWorkRegisterService.getDetailList(clTaskMain);
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
			}
			model.addAttribute("bankPayInfo", holeBankPayInfolist);
			model.addAttribute("clTaskMain", clTaskMain);
			model.addAttribute("clearTaskMainSession", clTaskMain);
			return "modules/clear/v03/empWorkRegister/empWorkRegisterForm";
		} else {
			return "redirect:" + Global.getAdminPath() + "/clear/v03/empWorkRegister/?repage";
		}
	}

	@RequiresPermissions("clear:v03:empWorkRegister:edit")
	@RequestMapping(value = "save")
	public String save(ClTaskMain clTaskMain, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "userCount", required = false) String userCount) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 获取前台传递过来的任务分配详细
			String[] userSplit = userCount.split(",");
			List<String> userList = Arrays.asList(userSplit);
			User user = UserUtils.getUser();
			// 设置登录人
			clTaskMain.setLoginUser(user);
			// 设置清分组人员详细
			clTaskMain.setUserList(userList);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clTaskMain.setOffice(userInfo.getOffice());
			/* end */
			// 执行保存
			empWorkRegisterService.save(clTaskMain);
			// 保存任务成功
			message = msg.getMessage("message.I7300", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(clTaskMain, model);
			// return "modules/allocation/v01/out/box/boxOutForm";
		}
		addMessage(model, message);
		// return "redirect:" + Global.getAdminPath() +
		// "/clear/v03/empWorkRegister/?repage";
		return form(clTaskMain, model);
	}

	@RequiresPermissions("clear:v03:empWorkRegister:edit")
	@RequestMapping(value = "delete")
	public String delete(ClTaskMain clTaskMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		empWorkRegisterService.delete(clTaskMain);
		// 删除任务成功！
		addMessage(redirectAttributes, msg.getMessage("message.I7301", null, locale));
		return "redirect:" + Global.getAdminPath() + "/clear/v03/empWorkRegister/?repage";
	}

	/**
	 * @author qph
	 * @version 2017年11月29日
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
		return "modules/clear/v03/empWorkRegister/empWorkRegisterDetail";
	}

	/**
	 * 获取清分组信息
	 * 
	 * @author wxz
	 * @date 2017-09-20
	 * 
	 * @Description
	 * @param model
	 * @return
	 */

	@RequestMapping(value = "getClearGroup")
	public String getClearGroup(@RequestParam(value = "clearGroupId", required = false) String clearGroupId,
			@RequestParam(value = "date", required = false) Date date,
			@RequestParam(value = "busType", required = false) String busType, Model model, HttpServletRequest request,
			HttpSession session) {
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
			List<String> userList = Lists.newArrayList();
			User clUser = UserUtils.get(clGroupDetail.getUser().getId());
			clGroupDetail.setUserType(clUser.getUserType());
			// add qph 2017-11-20 begin
			userList.add(clGroupDetail.getUser().getId());
			ClTaskMain clTaskMain = new ClTaskMain();
			// 设置工位类型
			clTaskMain.setTaskType(ClearConstant.TaskType.STAFF_WORKLOAD);
			// 设置发生机构
			clTaskMain.setOffice(UserUtils.getUser().getOffice());
			// 设置业务类型
			clTaskMain.setBusType(busType);
			// 设置用户
			clTaskMain.setUserList(userList);
			// 设置时间
			if (date == null) {
				clTaskMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
				clTaskMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
			} else {
				clTaskMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
				clTaskMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
			}
			List<ClTaskDetail> clTaskDetailList = clTaskMainService.getClearGroupByUserId(clTaskMain);
			List<ClTaskDetail> clTaskDetailListForPage = Lists.newArrayList();
			for (int a = 0; a < stoDictList.size(); a++) {
				StoDict tempStoDict = stoDictList.get(a);
				String code = tempStoDict.getValue();
				ClTaskDetail existBankPayInfo = null;
				// 设置默认为false
				boolean flag = false;
				for (ClTaskDetail clTaskDetail : clTaskDetailList) {
					clTaskDetail.setEmpNo(clGroupDetail.getUser().getId());
					clTaskDetail.setEmpName(clGroupDetail.getUser().getName());
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
	 * @author wxz
	 * @date 2017年9月20日
	 * 
	 *       员工工作量登记返回
	 * @param clTaskMain
	 *            调拨信息
	 * @param redirectAttributes
	 *            对象重定向传参
	 * @return 员工工作量登记列表页面
	 */
	@RequestMapping(value = "back")
	public String back(ClTaskMain clTaskMain, RedirectAttributes redirectAttributes) {
		return "redirect:" + adminPath + "/clear/v03/empWorkRegister/list";
	}

	/**
	 * 根据选择的业务类型，获取清分/复点组
	 * 
	 * @author wxz
	 * @param type
	 *            业务类型
	 * @version 2017-9-20
	 * @return
	 */
	@RequestMapping(value = "changeGroup")
	@ResponseBody
	public String changeGroup(@RequestParam String param, Model model, RedirectAttributes redirectAttributes) {
		// 将参数多余的符号清除
		param = param.replace("&quot;", "");
		List<Map<String, Object>> dataList = Lists.newArrayList();

		// 判断业务业务类型不为空
		if (!"".equals(param)) {
			List<ClearingGroup> list = ClearCommonUtils.getClearingGroupName("0", param);
			// 判断list是否为空
			if (!Collections3.isEmpty(list)) {
				if (list != null) {
					for (ClearingGroup clear : list) {
						Map<String, Object> map = Maps.newHashMap();
						map.put("label", clear.getGroupName());
						map.put("id", clear.getId());
						dataList.add(map);
					}
				}
			}
		}
		return gson.toJson(dataList);
	}

	/**
	 * 获取当日累积分配和回收列表
	 * 
	 * @author wxz
	 * @date 2017-09-21
	 * 
	 * @param model
	 * @return
	 * @throws ParseException
	 */

	@RequestMapping(value = "getDayTotalCount")
	public String getDayTotalCount(@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "date", required = false) Date date, Model model, HttpServletRequest request,
			HttpSession session, ClTaskMain clTaskMain) throws ParseException {
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		// 当日入库复点列表
		List<ClInMain> holeBankPayInfolist = Lists.newArrayList();
		// 当日已回收列表
		List<ClTaskMain> holeClTaskRecoveryList = Lists.newArrayList();
		// 判断业务类型为空
		if (type == null) {
			request.setAttribute("holeClTaskMainList", holeBankPayInfolist);
			request.setAttribute("holeClTaskRecoveryList", holeClTaskRecoveryList);
			model.addAttribute("holeClTaskMainList", holeBankPayInfolist);
			model.addAttribute("holeClTaskRecoveryList", holeClTaskRecoveryList);
		} else if (type.equals(ClearConstant.BusinessType.COMPLEX_POINT)) {
			// 查询今日已分配数量
			ClTaskMain info = new ClTaskMain();

			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			info.setOffice(userInfo.getOffice());
			/* end */
			info.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			info.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
			// 设置业务类型为复点
			info.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
			// 人行交款入库清分量
			List<ClInMain> bankPayInfoList = clTaskMainService.getDetailList(info);

			// 查询今日已回收和已登记数量
			ClTaskMain complexInfo = new ClTaskMain();
			// 设置业务类型为复点
			complexInfo.setBusType(ClearConstant.BusinessType.COMPLEX_POINT);
			// 设置开始结束时间
			complexInfo.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(date)));
			complexInfo.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(date)));
			// 设置计划类型为正常清分
			complexInfo.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);

			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			complexInfo.setOffice(userInfo.getOffice());
			/* end */

			// 获得当天清分类型下任务分配流水
			List<ClTaskMain> clTaskList = clTaskMainService.findList(complexInfo);
			// 任务回收流水
			List<ClTaskMain> clTaskRecoveryList = Lists.newArrayList();
			for (ClTaskMain clTask : clTaskList) {
				if (clTask.getTaskType().equals(ClearConstant.TaskType.TASK_DISTRIBUTION)) {
					clTaskRecoveryList.add(clTask);
				}
				if (clTask.getTaskType().equals(ClearConstant.TaskType.STAFF_WORKLOAD)) {
					clTaskRecoveryList.add(clTask);
				}
			}

			// 遍历出数据字典面值对应的code
			for (int a = 0; a < stoDictList.size(); a++) {
				StoDict tempStoDict = stoDictList.get(a);
				String code = tempStoDict.getValue();
				ClInMain existBankPayInfo = null;
				ClTaskMain existClTaskRecovery = null;
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
						if (tempBankPayInfo.getCountPeopleBank() == null) {
							tempBankPayInfo.setCountBank("0");
						} else {
							tempBankPayInfo.setCountBank(tempBankPayInfo.getCountPeopleBank());
						}
						existBankPayInfo = tempBankPayInfo;
						flag = true;
					}
				}
				// 如果查询的面值code与数据字典code不相等
				if (flag == false) {
					ClInMain newBankPayInfo = new ClInMain();
					newBankPayInfo.setDenomination(code);
					newBankPayInfo.setCountBank("0");
					holeBankPayInfolist.add(newBankPayInfo);
				} else {
					holeBankPayInfolist.add(existBankPayInfo);
				}


				for (int b = 0; b < clTaskRecoveryList.size(); b++) {
					ClTaskMain tempClTaskMain = clTaskRecoveryList.get(b);
					String keyCode = tempClTaskMain.getDenomination();
					// 判断查询的面值code是否与数据字典code相等
					if (keyCode.equals(code)) {
						if (existClTaskRecovery != null) {
							tempClTaskMain.setTotalCount(
									tempClTaskMain.getTotalCount() + existClTaskRecovery.getTotalCount());
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
			model.addAttribute("holeClTaskMainList", holeBankPayInfolist);
			model.addAttribute("holeClTaskRecoveryList", holeClTaskRecoveryList);
		} else if (type.equals(ClearConstant.BusinessType.CLEAR)) {

			// 查询今日已分配数量
			ClTaskMain info = new ClTaskMain();

			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			info.setOffice(userInfo.getOffice());
			/* end */
			info.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			info.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
			// 设置业务类型为清分
			info.setBusType(ClearConstant.BusinessType.CLEAR);
			// 商行交款入库清分量
			List<ClInMain> bankPayInfoList = clTaskMainService.getDetailList(info);

			// 如果业务类型是清分
			clTaskMain.setBusType(ClearConstant.BusinessType.CLEAR);

			// 查询今日已分配数量
			ClTaskMain clearInfo = new ClTaskMain();
			// 设置业务类型为清分
			clearInfo.setBusType(ClearConstant.BusinessType.CLEAR);
			// 设置任务类型为任务分配
			// info.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
			// 设置开始结束时间
			clearInfo.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(date)));
			clearInfo.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(date)));
			// 设置计划类型为正常清分
			clearInfo.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			clearInfo.setOffice(userInfo.getOffice());
			/* end */

			// 获得当天清分类型下任务分配流水
			List<ClTaskMain> clTaskList = clTaskMainService.findList(clearInfo);
			// 任务回收流水
			List<ClTaskMain> clTaskRecoveryList = Lists.newArrayList();
			for (ClTaskMain clTask : clTaskList) {

				if (clTask.getTaskType().equals(ClearConstant.TaskType.TASK_DISTRIBUTION)) {
					clTaskRecoveryList.add(clTask);
				}
				if (clTask.getTaskType().equals(ClearConstant.TaskType.STAFF_WORKLOAD)) {
					clTaskRecoveryList.add(clTask);
				}
			}


			// 遍历出数据字典面值对应的code
			for (int a = 0; a < stoDictList.size(); a++) {
				StoDict tempStoDict = stoDictList.get(a);
				String code = tempStoDict.getValue();
				ClInMain existBankPayInfo = null;
				ClTaskMain existClTaskRecovery = null;

				// 设置默认为false
				boolean flag = false;
				boolean exist = false;

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
						flag = true;
					}
				}
				// 如果查询的面值code与数据字典code不相等
				if (flag == false) {
					ClInMain newBankPayInfo = new ClInMain();
					newBankPayInfo.setDenomination(code);
					newBankPayInfo.setCountBank("0");
					holeBankPayInfolist.add(newBankPayInfo);
				} else {
					holeBankPayInfolist.add(existBankPayInfo);
				}


				for (int b = 0; b < clTaskRecoveryList.size(); b++) {
					ClTaskMain tempClTaskMain = clTaskRecoveryList.get(b);
					String keyCode = tempClTaskMain.getDenomination();
					// 判断查询的面值code是否与数据字典code相等
					if (keyCode.equals(code)) {
						if (existClTaskRecovery != null) {
							tempClTaskMain.setTotalCount(
									tempClTaskMain.getTotalCount() + existClTaskRecovery.getTotalCount());
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
			model.addAttribute("holeClTaskMainList", holeBankPayInfolist);
			model.addAttribute("holeClTaskRecoveryList", holeClTaskRecoveryList);
		}

		return "modules/clear/v03/empWorkRegister/dayTotalCount";
	}

}