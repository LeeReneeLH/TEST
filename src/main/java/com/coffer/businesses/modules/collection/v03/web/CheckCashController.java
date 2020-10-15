package com.coffer.businesses.modules.collection.v03.web;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashConfirm;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashDetail;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.CheckCashAuthorize;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecordsDetail;
import com.coffer.businesses.modules.doorOrder.v01.service.CheckCashAuthorizeService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.HistoryUseRecordsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 款箱拆箱Controller
 * 
 * @author wanglin
 * @version 2017-09-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/checkCash")
public class CheckCashController extends BaseController {

	@Autowired
	private CheckCashService checkCashService;
	@Autowired
	private CheckCashAmountDao checkCashAmountDao;
	@Autowired
	private HistoryUseRecordsService historyUseRecordsService;
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private CheckCashAuthorizeService checkCashAuthorizeService;
	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	@ModelAttribute
	public CheckCashMain get(@RequestParam(required = false) String id) {
		CheckCashMain entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = checkCashService.get(id);
		}
		if (entity == null) {
			entity = new CheckCashMain();
		}
		return entity;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param CheckCashMain
	 * @return
	 */
	@RequiresPermissions("check:checkCash:view")
	@RequestMapping(value = { "list", "" })
	public String list(CheckCashMain checkCashMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 查询条件： 开始时间
		if (checkCashMain.getRegTimeStart() != null) {
			checkCashMain.setSearchRegDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(checkCashMain.getRegTimeStart())));
		}
		/** 初始化查询开始时间默认上月的今天(优化查询速度) add by lihe 2020-05-21 */
		if (checkCashMain.getRegTimeStart() == null && !"0".equals(checkCashMain.getUninitDateFlag())) {
			checkCashMain.setSearchRegDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("month")));
			checkCashMain.setRegTimeStart(DateUtils.parseDate(checkCashMain.getSearchRegDateStart()));
		}
		// 查询条件： 结束时间
		if (checkCashMain.getRegTimeEnd() != null) {
			checkCashMain.setSearchRegDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(checkCashMain.getRegTimeEnd())));
		}
		// 当前用户
		User user = UserUtils.getUser();
		// 用户类型
		checkCashMain.setCurUserType(user.getUserType());
		// 当前用户编号
		checkCashMain.setCurUserId(user.getId());
		// 清分中心操作员
		checkCashMain.setClearOpt(Constant.SysUserType.CLEARING_CENTER_OPT);
		Page<CheckCashMain> page = checkCashService.getCheckCashPage(new Page<CheckCashMain>(request, response),
				checkCashMain);
		model.addAttribute("page", page);
		return "modules/collection/v03/check/checkCashList";
	}

	/**
	 * 
	 * @author lihe
	 * @version 2020年5月20日
	 * 
	 *          一览页面
	 * @param CheckCashMain
	 * @return
	 */
	@RequiresPermissions("check:checkCash:view")
	@RequestMapping(value = "historyList")
	public String historyList(CheckCashMain checkCashMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 查询条件： 开始时间
		if (checkCashMain.getRegTimeStart() != null) {
			checkCashMain.setSearchRegDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(checkCashMain.getRegTimeStart())));
		}
		/** 初始化查询开始时间默认上月的今天(优化查询速度) add by lihe 2020-05-21 */
		if (checkCashMain.getRegTimeStart() == null && !"0".equals(checkCashMain.getUninitDateFlag())) {
			checkCashMain.setSearchRegDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("month")));
			checkCashMain.setRegTimeStart(DateUtils.parseDate(checkCashMain.getSearchRegDateStart()));
		}
		// 查询条件： 结束时间
		if (checkCashMain.getRegTimeEnd() != null) {
			checkCashMain.setSearchRegDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(checkCashMain.getRegTimeEnd())));
		}
		// 当前用户
		User user = UserUtils.getUser();
		// 用户类型
		checkCashMain.setCurUserType(user.getUserType());
		// 当前用户编号
		checkCashMain.setCurUserId(user.getId());
		// 清分中心操作员
		checkCashMain.setClearOpt(Constant.SysUserType.CLEARING_CENTER_OPT);
		Page<CheckCashMain> page = checkCashService.getHistoryCheckCashPage(new Page<CheckCashMain>(request, response),
				checkCashMain);
		model.addAttribute("page", page);
		return "modules/collection/v03/check/checkCashHistoryList";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          登记修改页面
	 * @param CheckCashMain
	 * @return
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "form")
	public String form(CheckCashMain checkCashMain, Model model, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 每笔明细
		List<CheckCashAmount> amountList = Lists.newArrayList();
		// 每笔面值明细
		List<CheckCashDetail> amountDetailList = Lists.newArrayList();
		// 纸币面值明细
		List<CheckCashDetail> cnypdenDetailList = Lists.newArrayList();
		// 硬币面值明细
		List<CheckCashDetail> cnyhdenDetailList = Lists.newArrayList();
		// 判断该箱是否已经被拆过
		if (String.valueOf(checkCashAmountDao.findNoBoxCount(checkCashMain.getOutNo())).equals(Constant.SUCCESS)) {
			String message = msg.getMessage("message.I7308", new String[] { checkCashMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		// 修改的场合，如果已删除
		if (StringUtils.isNotBlank(checkCashMain.getId())
				&& Constant.deleteFlag.Invalid.equals(checkCashMain.getDelFlag())) {
			String message = msg.getMessage("message.E0008", new String[] { checkCashMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		// 面值（纸币）列表数据的取得
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList());
		// 面值（硬币）列表数据的取得
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList());
		// 修改场合
		if (checkCashMain != null && checkCashMain.getId() != null) {
			// 每笔金额列表的设定
			amountList = checkCashService.findAmountList(checkCashMain);
			// 每笔金额面值列表的设定
			amountDetailList = checkCashService.findAmountDetailList(checkCashMain);
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			checkCashDetail.setOutNo(checkCashMain.getOutNo());
			// 纸币面值明细
			checkCashDetail.setUnitId(Constant.Unit.piece);
			cnypdenDetailList = checkCashService.findList(checkCashDetail);
			// 硬币面值明细
			checkCashDetail.setUnitId(Constant.Unit.coin);
			cnyhdenDetailList = checkCashService.findList(checkCashDetail);
		}
		checkCashMain.setAmountList(amountList);
		checkCashMain.setAmountDetailList(amountDetailList);
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countZhang");
		// 设置总金额
		denomCtrl.setTotalAmtName("detailAmount");
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList(cnypdenDetailList, denomCtrl));
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList(cnyhdenDetailList, denomCtrl));
		// 存款差错，实际存款金额
		checkCashMain = checkCashService.getDepositErrorByOutNo(checkCashMain);

		model.addAttribute("checkCashMain", checkCashMain);
		return "modules/collection/v03/check/checkCashFormNew";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          查看页面
	 * @param CheckCashMain
	 * @param type
	 *            用于区分是款箱拆箱还是清点差错来的请求
	 * @return
	 */
	@RequiresPermissions("check:checkCash:view")
	@RequestMapping(value = "view")
	public String view(CheckCashMain checkCashMain, Model model, int type) {
		List<CheckCashAmount> amountList = Lists.newArrayList();
		List<CheckCashDetail> amountDetailList = Lists.newArrayList();
		// 纸币面值明细
		List<CheckCashDetail> cnypdenDetailList = Lists.newArrayList();
		// 硬币面值明细
		List<CheckCashDetail> cnyhdenDetailList = Lists.newArrayList();
		// 面值（纸币）列表数据的取得
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList());
		// 面值（硬币）列表数据的取得
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList());
		if (checkCashMain != null && checkCashMain.getId() != null) {
			// 每笔金额列表的设定
			amountList = checkCashService.findAmountList(checkCashMain);

			// 每笔金额面值列表的设定
			amountDetailList = checkCashService.findAmountDetailList(checkCashMain);
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			checkCashDetail.setOutNo(checkCashMain.getOutNo());
			// 纸币面值明细
			checkCashDetail.setUnitId(Constant.Unit.piece);
			cnypdenDetailList = checkCashService.findList(checkCashDetail);
			// 硬币面值明细
			checkCashDetail.setUnitId(Constant.Unit.coin);
			cnyhdenDetailList = checkCashService.findList(checkCashDetail);
		}
		checkCashMain.setAmountList(amountList);
		checkCashMain.setAmountDetailList(amountDetailList);
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countZhang");
		// 设置总金额
		denomCtrl.setTotalAmtName("detailAmount");
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList(cnypdenDetailList, denomCtrl));
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList(cnyhdenDetailList, denomCtrl));
		// 存款差错，实际存款金额
		checkCashMain = checkCashService.getDepositErrorByOutNo(checkCashMain);

		model.addAttribute("checkCashMain", checkCashMain);
		// 当type==2时为清点差错传来的请求返回弹窗信息
		if (type == 2) {
			return "modules/collection/v03/check/checkCashViewNew2";
		} else {
			// 返回款箱拆箱请求的页面信息
			return "modules/collection/v03/check/checkCashViewNew";
		}

	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          整单删除
	 * @param CheckCashMain
	 * @return
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "delete")
	public String delete(CheckCashMain checkCashMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			checkCashService.deleteMain(checkCashMain);
			// 删除成功
			message = msg.getMessage("message.I7243", new String[] { checkCashMain.getOutNo() }, locale);
		} catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param bankPayInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(CheckCashMain checkCashMain) {
		return "redirect:" + adminPath + "/collection/v03/checkCash/list?repage";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年4月21日 每笔确认处理
	 * @param outNo
	 *            单号
	 * @param custNo
	 *            客户CD
	 * @param amountId
	 *            每笔ID
	 * @param inputAmount
	 *            每笔录入金额
	 * @param payValueJoin
	 *            每笔面值拼接（券别-张数）
	 * @param remarks
	 *            备注
	 * @param authUserId
	 *            授权用户ID
	 * @param oldUpdateCnt
	 *            更新回数
	 * @return String 处理结果（OK：成功）
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "confirmDetail")
	@ResponseBody
	public String confirmDetail(@RequestParam(required = false) String outNo, // 单号
			@RequestParam(required = false) String custNo, // 客户CD
			@RequestParam(required = false) String amountId, // 每笔ID
			@RequestParam(required = false) String inputAmount, // 每笔录入金额
			@RequestParam(required = false) String payValueJoin, // 每笔面值拼接（券别-张数）
			@RequestParam(required = false) String remarks, // 备注
			@RequestParam(required = false) String authUserId, // 授权用户ID
			@RequestParam(required = false) String oldUpdateCnt, // 更新回数
			Model model) {
		Map<String, Object> map = Maps.newHashMap();

		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 参数的组织
			CheckCashConfirm checkCashConfirm = new CheckCashConfirm();
			checkCashConfirm.setOutNo(outNo);
			checkCashConfirm.setCustNo(custNo);
			checkCashConfirm.setAmountId(amountId);
			checkCashConfirm.setInputAmount(inputAmount);
			checkCashConfirm.setPayValueJoin(payValueJoin);
			checkCashConfirm.setRemarks(remarks);
			checkCashConfirm.setAuthUserId(authUserId);
			checkCashConfirm.setChkUpdateCnt(oldUpdateCnt);
			// 确认处理
			checkCashService.confirmDetail(checkCashConfirm);
			map.put("outNo", checkCashConfirm.getNewOutNo()); // 拆箱单号(新)
			map.put("amountId", checkCashConfirm.getNewAmountId()); // 每笔ID(新)
			map.put("chkUpdateCnt", checkCashConfirm.getChkUpdateCnt()); // 更新回数
			map.put("result", "OK");

			return gson.toJson(map);

		} catch (BusinessException e) {
			String message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			map.put("result", message);
			return gson.toJson(map);

		}
	}

	/**
	 * 每笔明细授权页面
	 * 
	 * @author wl
	 * @version 2017-7-10
	 * 
	 */
	@RequestMapping(value = "authorizeDetail")
	public String authorizeDetail(Model model) {
		return "modules/collection/v03/check/checkCashAuthorize";
	}

	/**
	 * 授权人员的验证
	 * 
	 * @author wl
	 * @version 2017年8月24日
	 * @param authorizer
	 *            用户名
	 * @param authorizeBy
	 *            密码
	 * @return String 处理结果（OK：成功）
	 */
	@RequestMapping(value = "/authorizeUser")
	@ResponseBody
	public String authorizeUser(@RequestParam(value = "authorizer", required = true) String authorizer,
			@RequestParam(value = "authorizeBy", required = true) String authorizeBy) {
		Map<String, String> rtnMap = new HashMap<String, String>();
		Locale locale = LocaleContextHolder.getLocale();
		String message = "OK";
		// 清分中心管理员
		User user = UserUtils.getByLoginName(authorizer);
		if (user == null || !user.getOffice().getId().equals(UserUtils.getUser().getOffice().getId())) {
			// 此用户不存在
			message = msg.getMessage("message.I7220", null, locale);
			rtnMap.put("result", message);
			return gson.toJson(rtnMap);
		}
		// 清分中心管理员的判断
		if (!Constant.SysUserType.CLEARING_CENTER_MANAGER.equals(user.getUserType())) {
			// 不是清分管理员
			message = msg.getMessage("message.I7221", null, locale);
			rtnMap.put("result", message);
			return gson.toJson(rtnMap);
		}
		// 密码验证
		if (user == null || !user.getPassword().equals(SystemService.entryptPassword(authorizeBy))) {
			message = msg.getMessage("message.I7222", null, locale);
			rtnMap.put("result", message);
			return gson.toJson(rtnMap);
		}
		// 正常终了
		rtnMap.put("userId", user.getId());
		rtnMap.put("result", message);
		return gson.toJson(rtnMap);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年4月21日 每笔金额删除处理
	 * @param amountId
	 *            每笔金额ID
	 * @param oldUpdateCnt
	 *            更新回数
	 * @return String 处理结果（OK：成功）
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "deleteDetail")
	@ResponseBody
	public String deleteDetail(@RequestParam(required = false) String amountId, // 每笔金额ID
			@RequestParam(required = false) String oldUpdateCnt, // 更新回数
			Model model) {
		Map<String, Object> map = Maps.newHashMap();
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 确认处理
			Map<String, Object> returnMap = checkCashService.deleteDetail(amountId, oldUpdateCnt);
			map.put("mainDel", returnMap.get("mainDelFlag"));
			map.put("chkUpdateCnt", returnMap.get("chkUpdateCnt"));
			map.put("result", "OK");
			return gson.toJson(map);
		} catch (BusinessException e) {
			String message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			map.put("result", message);
			return gson.toJson(map);

		}
	}

	/**
	 * 款箱拆箱保存
	 *
	 * @author XL
	 * @version 2019年7月10日
	 * @param checkCashMain
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "save")
	public synchronized String save(CheckCashMain checkCashMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 提示消息
		String message = "";
		User user = UserUtils.getUser(); // 当前登录用户
		Office office = user.getOffice(); // 当前登录用户所属机构
		// 1.当前登录用户所属机构是管理中心人员可以做清分业务，否则返回异常消息：只有管理中心人员才可以做清分业务
		if (office == null || StringUtils.isEmpty(office.getId())) {
			message = msg.getMessage("message.E7308", new String[] {}, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		if (!Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			message = msg.getMessage("message.E7309", new String[] {}, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		// 2.当前登录用户是管理中心人员，且中心确实对本条业务所属客户公司有押运清分权限，可以做清分业务，否则返回异常消息：
		// 没有对该门店清分的权限
		Office area = office.getParent();
		Office cust = officeDao.get(checkCashMain.getCustNo());
		checkCashMain.setCustName(cust.getName());
		String custParentIds = cust.getParentIds();
		String areaId = area.getId();
		if (!custParentIds.contains(areaId)) {
			message = msg.getMessage("message.E7310", new String[] {}, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		// 判断该箱是否已经被拆过
		if (String.valueOf(checkCashAmountDao.findNoBoxCount(checkCashMain.getOutNo())).equals(Constant.SUCCESS)) {
			message = msg.getMessage("message.I7308", new String[] { checkCashMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		try {
			// 保存拆箱信息
			logger.info("开始拆箱 : ===========参数===========");
			logger.info(gson.toJson(checkCashMain));
			checkCashService.save(checkCashMain);
			message = msg.getMessage("message.I7304", new String[] { checkCashMain.getOutNo() }, locale);
		} catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			logger.info("拆箱失败 : ===========参数===========");
			logger.info(gson.toJson(checkCashMain));
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
	}

	/**
	 * 款箱拆箱冲正操作
	 *
	 * @author XL
	 * @version 2019年7月26日
	 * @param checkCashMain
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("check:checkCash:edit")
	@RequestMapping(value = "reverse")
	public synchronized String reverse(CheckCashMain checkCashMain, Model model,
			RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 判断该箱是否可以冲正
		if (!String.valueOf(checkCashAmountDao.findNoBoxCount(checkCashMain.getOutNo())).equals(Constant.SUCCESS)) {
			String message = msg.getMessage("message.I7309", new String[] { checkCashMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		//判断该箱是否已经结算  若已结算  不允许冲正操作
		List<DoorCenterAccountsMain> list = centerAccountsMainService.checkDepositErrorAccount(checkCashMain.getOutNo(),true);
		if(!Collections3.isEmpty(list) && DoorOrderConstant.StatusType.CREATE.equals(list.get(0).getBusinessStatus())){
			// 该存款差错已被结算或处理  
			String message = msg.getMessage("message.I7310", new String[] { checkCashMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
		}
		// 提示消息
		String message = "";
		try {
			// 保存拆箱信息
			checkCashService.reverse(checkCashMain);
			message = msg.getMessage("message.I7311", new String[] { checkCashMain.getOutNo() }, locale);
		} catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/collection/v03/checkCash/?repage";
	}

	/**
	 * 款箱拆箱存款查看
	 *
	 * @author hzy
	 * @version 2020年04月16日
	 * @param historyUseRecordsDetail
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "checkCashDetail")
	public String detailList(HistoryUseRecordsDetail historyUseRecordsDetail, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HistoryUseRecordsDetail> page = historyUseRecordsService
				.findDetailPage(new Page<HistoryUseRecordsDetail>(request, response), historyUseRecordsDetail);
		model.addAttribute("page", page);
		return "modules/collection/v03/check/checkCashDetail";
	}

	/**
	 * 款箱拆箱存款导出
	 *
	 * @author hzy
	 * @version 2020年04月16日
	 * @param historyUseRecordsDetail
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "exportDetail")
	public String exportDetail(HistoryUseRecordsDetail historyUseRecords, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		// 查询条件： 开始时间
		if (historyUseRecords.getCreateTimeStart() != null) {
			historyUseRecords.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyUseRecords.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyUseRecords.getCreateTimeEnd() != null) {
			historyUseRecords.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyUseRecords.getCreateTimeEnd())));
		}
		try {
			// 存款明细列表
			List<HistoryUseRecordsDetail> historyUseRecordsDetailList = historyUseRecordsService
					.findDetailList(historyUseRecords);
			// 合计行
			HistoryUseRecordsDetail useRecords = new HistoryUseRecordsDetail();
			// 初始化
			Integer count100 = 0;
			Integer count50 = 0;
			Integer count20 = 0;
			Integer count10 = 0;
			Integer count5 = 0;
			Integer count1 = 0;
			BigDecimal paperAmount = new BigDecimal(0);
			BigDecimal coinAmount = new BigDecimal(0);
			BigDecimal forceAmount = new BigDecimal(0);
			BigDecimal otherAmount = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			// 合计计算
			for (HistoryUseRecordsDetail records : historyUseRecordsDetailList) {
				records.setExlDepositDate(DateUtils.formatDate(records.getDepositDate(), "yyyy-MM-dd"));
				count100 = count100 + records.getHundred();
				count50 = count50 + records.getFifty();
				count20 = count20 + records.getTwenty();
				count10 = count10 + records.getTen();
				count5 = count5 + records.getFive();
				count1 = count1 + records.getOne();
				// 纸币金额
				paperAmount = paperAmount.add(records.getPaperAmount());
				// 硬币金额
				coinAmount = coinAmount.add(records.getCoinAmount());
				// 强制金额
				forceAmount = forceAmount.add(records.getForceAmount());
				// 其他金额
				otherAmount = otherAmount.add(records.getOtherAmount());
				// 总金额
				amount = amount.add(records.getAmount());
			}
			// 添加合计及各存款类型金额总计
			useRecords.setDepositBatches("合计");
			useRecords.setHundred(count100);
			useRecords.setFifty(count50);
			useRecords.setTwenty(count20);
			useRecords.setTen(count10);
			useRecords.setFive(count5);
			useRecords.setOne(count1);
			useRecords.setPaperAmount(paperAmount);
			useRecords.setCoinAmount(coinAmount);
			useRecords.setForceAmount(forceAmount);
			useRecords.setOtherAmount(otherAmount);
			useRecords.setAmount(amount);
			historyUseRecordsDetailList.add(useRecords);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(historyUseRecordsDetailList)) {
				historyUseRecordsDetailList.add(new HistoryUseRecordsDetail());
			}
			// 模板文件名 /历史使用记录.xls
			String fileName = msg.getMessage("door.checkCash.detailTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.historyUseRecords.detail", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, HistoryUseRecordsDetail.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, historyUseRecordsDetailList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.checkCash.detailExcel", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return detailList(historyUseRecords, request, response, model);
		}
		return null;
	}

	/**
	 * 获取拆箱明细
	 * 
	 * @author gzd
	 * @version 2020-05-07
	 * 
	 */
	@RequestMapping(value = "getForm")
	public CheckCashMain getForm(CheckCashMain checkCashMain) {
		// 每笔明细
		List<CheckCashAmount> amountList = Lists.newArrayList();
		// 每笔面值明细
		List<CheckCashDetail> amountDetailList = Lists.newArrayList();
		// 纸币面值明细
		List<CheckCashDetail> cnypdenDetailList = Lists.newArrayList();
		// 硬币面值明细
		List<CheckCashDetail> cnyhdenDetailList = Lists.newArrayList();
		// 面值（纸币）列表数据的取得
		//checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList());
		// 面值（硬币）列表数据的取得
		//checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList());
		// 修改场合
		if (checkCashMain.getId() != null) {
			// 每笔金额列表的设定
			amountList = checkCashService.findAmountList(checkCashMain);
			// 每笔金额面值列表的设定
			amountDetailList = checkCashService.findAmountDetailList(checkCashMain);
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			checkCashDetail.setOutNo(checkCashMain.getOutNo());
			// 纸币面值明细
			checkCashDetail.setUnitId(Constant.Unit.piece);
			cnypdenDetailList = checkCashService.findList(checkCashDetail);
			// 硬币面值明细
			checkCashDetail.setUnitId(Constant.Unit.coin);
			cnyhdenDetailList = checkCashService.findList(checkCashDetail);
		}
		checkCashMain.setAmountList(amountList);
		checkCashMain.setAmountDetailList(amountDetailList);
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countZhang");
		// 设置总金额
		denomCtrl.setTotalAmtName("detailAmount");
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList(cnypdenDetailList, denomCtrl));
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList(cnyhdenDetailList, denomCtrl));
		// 存款差错，实际存款金额
		checkCashMain = checkCashService.getDepositErrorByOutNo(checkCashMain);
		return checkCashMain;
	}

	/**
	 * 快速拆箱
	 * 
	 * @author gzd
	 * @param checkCashMain
	 * @param model
	 * @version 2020-05-07
	 * @return
	 */
	@RequestMapping(value = "quickUnboxing")
	public String quickUnboxing(CheckCashMain checkCashMain, Model model, String checkAmount) {
		checkCashMain = getForm(checkCashMain);
		checkCashMain.setCheckAmount(checkAmount);
		model.addAttribute("checkCashMain", checkCashMain);
		return "modules/collection/v03/check/quickUnboxing";
	}

	/**
	 * 查看门店限制金额信息
	 * 
	 * @author ZXK
	 * @param checkCashMain
	 * @param model
	 * @version 2020-05-21
	 * @return
	 */
	@RequestMapping(value = "getDoorAuthorize")
	@ResponseBody
	public String getDoorAuthorize(@RequestParam(required = false) String custNo) {
		CheckCashAuthorize cca = new CheckCashAuthorize();
		// 门店id
		cca.setOfficeId(custNo);
		// 授权类型
		cca.setType(DoorOrderConstant.AuthorizeType.CHECK_CASH);
		CheckCashAuthorize authorize = checkCashAuthorizeService.selectDoorRestrictAmount(cca);
		Map<String, String> rtnMap = new HashMap<String, String>();
		/*if (authorize != null) {*/
			rtnMap.put("amount", authorize.getAmount() == null ? "0" :authorize.getAmount().toString());
			rtnMap.put("expressionType", authorize.getExpressionType() == null ? "":authorize.getExpressionType());
		/*}*/
		return gson.toJson(rtnMap);
	}
}