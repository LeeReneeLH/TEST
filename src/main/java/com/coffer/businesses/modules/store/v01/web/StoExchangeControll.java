package com.coffer.businesses.modules.store.v01.web;

import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoExchange;
import com.coffer.businesses.modules.store.v01.entity.StoExchangeGood;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.service.StoExchangeService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 兑换管理Controller
 * 
 * @author niguoyong
 * @version 2015年9月21日
 */
@Controller
@SessionAttributes({ "stoExchangeDetail" })
@RequestMapping(value = "${adminPath}/store/v01/stoExchange")
public class StoExchangeControll extends BaseController {

	@Autowired
	private StoExchangeService stoExchangeService;

	/**
	 * Json实例对象
	 */
	private Gson gson = new GsonBuilder().create();

	@ModelAttribute
	public StoExchange get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return stoExchangeService.getDetailById(id);
		} else {
			return new StoExchange();
		}
	}

	/**
	 * 兑换检索
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 */
	@RequiresPermissions("store:stoExchange:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoExchange stoExchange, @RequestParam(required = false) boolean isSearch,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoExchange> page = new Page<StoExchange>(request, response);
		// 点击查询
//		if (isSearch) {
			User user = UserUtils.getUser();
			if (!user.isAdmin()) {
				stoExchange.setCreateBy(user);
			}
			if (stoExchange.getCreateTimeStart() == null) {
				stoExchange.setCreateTimeStart(new Date());
			}
			if (stoExchange.getCreateTimeEnd() == null) {
				stoExchange.setCreateTimeEnd(new Date());
			}
			page = stoExchangeService.findPage(page, stoExchange);
			model.addAttribute("page", page);
//		}
		return "modules/store/v01/stoExchange/stoExchangeList";

	}

	/**
	 * 兑换添加画面
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 */
	@RequiresPermissions("store:stoExchange:view")
	@RequestMapping(value = "form")
	public String form(StoExchange stoExchange, Model model, SessionStatus status) {
		if (StringUtils.isNotBlank(stoExchange.getId())) {
			// 兑换物品ID解析成物品信息
			StoGoodSelect good = StoreCommonUtils.splitGood(stoExchange.getChangeGoods().getGoodsID());
			itemCopy(stoExchange.getStoGoodSelectFrom(), good);

			// 目标物品
			for (StoExchangeGood stoExchangeGood : stoExchange.getStoExchangeGoodList()) {
				// 物品ID解析成物品信息
				good = StoreCommonUtils.splitGood(stoExchangeGood.getGoodsId());
				itemCopy(stoExchangeGood, good);
			}
			model.addAttribute("stoExchangeDetail", stoExchange);
			return "modules/store/v01/stoExchange/stoExchangeDetail";
		} else {
			model.addAttribute("stoExchangeDetail", stoExchange);
			model.addAttribute("stoExchange", stoExchange);
		}
		return "modules/store/v01/stoExchange/stoExchangeForm";
	}

	/**
	 * @author ChengShu
	 * @date 2014/11/18
	 * 
	 * @Description 取得箱子列表
	 * @return 跳转页面
	 */
	@RequestMapping(value = "getDetailList")
	public String getBoxList() {
		return "modules/store/v01/stoExchange/detailList";
	}

	// /**
	// * 数量计算 根据原始物品ID取得物品价值，计算出总价值， 根据目标物品ID取得物品价值，计算出物品数量
	// *
	// * @author niguoyong
	// * @version 2015-09-22
	// */
	// private String calculateNum(String param, Model model, RedirectAttributes
	// redirectAttributes) {
	// // 取得参数
	// param = param.replace("&quot;", "\"").replace("\"\"", "\"0\"");
	//
	// Gson gson = new Gson();
	// String message;
	// Locale locale = LocaleContextHolder.getLocale();
	// StoExchange stoExchange = gson.fromJson(param, StoExchange.class);
	// // 取得原始物品价值
	// BigDecimal goodsValue = getGoodsValue(stoExchange.getStoGoodSelect());
	// // 取得目标物品价值
	// BigDecimal ChangeoGoodsValue =
	// getGoodsValue(stoExchange.getStoGoodSelectFrom());
	// if (goodsValue == null) {
	// message = msg.getMessage("message.E1008",
	// new String[] { getGoodsKey(stoExchange.getStoGoodSelect()) }, locale);
	// stoExchange.setStoNum((long) 0);
	// return new Gson().toJson(message);
	// } else if (ChangeoGoodsValue == null || ChangeoGoodsValue.longValue() ==
	// 0) {
	// message = msg.getMessage("message.E1008",
	// new String[] { getGoodsKey(stoExchange.getStoGoodSelectFrom()) },
	// locale);
	// stoExchange.setStoNum((long) 0);
	// return new Gson().toJson(message);
	// }
	//
	// // 总价值计算
	// BigDecimal amount =
	// goodsValue.multiply(BigDecimal.valueOf(stoExchange.getStoGoodSelectFrom().getMoneyNumber()));
	//
	// // 目标物品数量计算
	// BigDecimal toGoodsNum = amount.divide(ChangeoGoodsValue);
	//
	// stoExchange.getStoGoodSelect().setMoneyNumber(toGoodsNum.longValue());
	// return new Gson().toJson(stoExchange);
	// }

	/**
	 * 兑换添加
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequiresPermissions("store:stoExchange:edit")
	@ResponseBody
	@RequestMapping(value = "add")
	public String add(StoExchange stoExchange, @ModelAttribute("stoExchangeDetail") StoExchange stoExchangeDetail,
			Model model, SessionStatus status) {
		// 生成物品ID
		String strGoodsKey = getGoodsKey(stoExchange.getStoGoodSelectFrom());
		stoExchange.getChangeGoods().setId(strGoodsKey);
		// 兑换添加check
		String message = exchangeAddCheck(stoExchange, stoExchangeDetail);
		if (StringUtils.isNotBlank(message)) {
			return this.addMessageByJson(ERROR_MESSAGE_KEY, message);
		}

		// 兑换添加
		detailAdd(stoExchange, stoExchangeDetail, model);

		return addMessageByJson("");
	}

	/**
	 * 兑换删除
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequiresPermissions("store:stoExchange:edit")
	@ResponseBody
	@RequestMapping(value = "delete")
	public String delete(StoExchange stoExchange, @ModelAttribute("stoExchangeDetail") StoExchange stoExchangeDetail,
			@RequestParam(required = false) String goodsId, Model model, SessionStatus status) {

		for (StoExchangeGood stoExchangeGood : stoExchangeDetail.getStoExchangeGoodList()) {
			if (goodsId.equals(stoExchangeGood.getGoodsId())) {
				// 兑换总金额
				stoExchangeDetail.setChangeAmount(stoExchangeDetail.getChangeAmount().subtract(
						stoExchangeGood.getMoneyAmount()));
				// 明细删除
				stoExchangeDetail.getStoExchangeGoodList().remove(stoExchangeGood);
				break;
			}
		}

		return addMessageByJson("");
	}

	/**
	 * 兑换保存
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequiresPermissions("store:stoExchange:edit")
	@RequestMapping(value = "save")
	public String save(StoExchange stoExchange, @ModelAttribute("stoExchangeDetail") StoExchange stoExchangeDetail,
			Model model, RedirectAttributes redirectAttributes, SessionStatus status) {
		Locale locale = LocaleContextHolder.getLocale();
		// 生成物品ID
		String strGoodsKey = getGoodsKey(stoExchange.getStoGoodSelectFrom());
		stoExchange.getChangeGoods().setId(strGoodsKey);

		if (!beanValidator(model, stoExchange)) {
			model.addAttribute("stoExchange", stoExchange);
			return "modules/store/v01/stoExchange/stoExchangeForm";
		}

		// 兑换保存check
		String message = exchangeSaveCheck(stoExchange, stoExchangeDetail);
		if (StringUtils.isNotBlank(message)) {
			addMessage(model, message);
			model.addAttribute("stoExchange", stoExchange);
			return "modules/store/v01/stoExchange/stoExchangeForm";
		}

		// 兑换保存
		try {
			Office currentOffice = UserUtils.getUser().getOffice();
			stoExchangeService.save(stoExchange, stoExchangeDetail,currentOffice);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return "modules/store/v01/stoExchange/stoExchangeForm";
		}
		addMessage(redirectAttributes, "保存库存保存成功");
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoExchange/list?isSearch=true&repage";
	}

	/**
	 * 返回
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequestMapping(value = "back")
	public String back(StoExchange stoExchange, SessionStatus status, RedirectAttributes redirectAttributes) {
		// 清空Session
		status.setComplete();
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoExchange/list?isSearch=true&repage";
	}

	/**
	 * 兑换添加check
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 * @param stoExchangeDetail
	 */
	private String exchangeAddCheck(StoExchange stoExchange, StoExchange stoExchangeDetail) {
		Locale locale = LocaleContextHolder.getLocale();
		String message;

		// 输入数量是0
		if (stoExchange.getStoGoodSelectFrom().getMoneyNumber() == 0
				|| stoExchange.getStoGoodSelect().getMoneyNumber() == 0) {
			message = msg.getMessage("message.E1012", new String[] {}, locale);
			return message;
		}

		// 库存数量不足//getStoNum需要修改
		if (stoExchange.getStoGoodSelectFrom().getMoneyNumber() > stoExchange.getStoNum()) {
			message = msg.getMessage("message.E1010", new String[] {}, locale);
			return message;
		}

		// 币种不同不能兑换
		if (!stoExchange.getStoGoodSelectFrom().getCurrency().equals(stoExchange.getStoGoodSelect().getCurrency())) {
			message = msg.getMessage("message.E1006", new String[] {}, locale);
			return message;
		}

		// 假币残损币不能兑换
		if (Constant.MoneyType.COUNTERFEIT_MONEY.equals(stoExchange.getStoGoodSelectFrom().getClassification())
				|| Constant.MoneyType.DEMANGED_MONEY.equals(stoExchange.getStoGoodSelectFrom().getClassification())) {
			message = msg.getMessage("message.E1007", new String[] {}, locale);
			return message;
		}
		// 假币残损币不能兑换
		if (Constant.MoneyType.COUNTERFEIT_MONEY.equals(stoExchange.getStoGoodSelect().getClassification())
				|| Constant.MoneyType.DEMANGED_MONEY.equals(stoExchange.getStoGoodSelect().getClassification())) {
			message = msg.getMessage("message.E1007", new String[] {}, locale);
			return message;
		}

		// 相同物品不能兑换
		if (stoExchange.getStoGoodSelectFrom().getCurrency().equals(stoExchange.getStoGoodSelect().getCurrency())
				&& stoExchange.getStoGoodSelectFrom().getClassification()
						.equals(stoExchange.getStoGoodSelect().getClassification())
				&& stoExchange.getStoGoodSelectFrom().getCash().equals(stoExchange.getStoGoodSelect().getCash())
				&& stoExchange.getStoGoodSelectFrom().getDenomination()
						.equals(stoExchange.getStoGoodSelect().getDenomination())
				&& stoExchange.getStoGoodSelectFrom().getEdition().equals(stoExchange.getStoGoodSelect().getEdition())
				&& stoExchange.getStoGoodSelectFrom().getUnit().equals(stoExchange.getStoGoodSelect().getUnit())) {

			message = msg.getMessage("message.E1014", new String[] {}, locale);
			return message;
		}

		// 取得原始物品价值
		BigDecimal goodsValue = getGoodsValue(stoExchange.getStoGoodSelectFrom());
		if (goodsValue == null) {
			message = msg.getMessage("message.E1008",
					new String[] { getGoodsKey(stoExchange.getStoGoodSelectFrom()) }, locale);
			return message;
		}

		// 取得兑换物品价值
		BigDecimal ChangeGoodsValue = getGoodsValue(stoExchange.getStoGoodSelect());
		if (ChangeGoodsValue == null) {
			message = msg.getMessage("message.E1008",
					new String[] { getGoodsKey(stoExchange.getStoGoodSelect()) }, locale);
			return message;
		}

		// 兑换总金额=本次金额+明细总金额
		BigDecimal amount = ChangeGoodsValue.multiply(
				BigDecimal.valueOf(stoExchange.getStoGoodSelect().getMoneyNumber())).add(
				stoExchangeDetail.getChangeAmount());

		// 兑换总金额大于原始总金额
		if (amount.compareTo(goodsValue.multiply(BigDecimal
				.valueOf(stoExchange.getStoGoodSelectFrom().getMoneyNumber()))) > 0) {
			message = msg.getMessage("message.E1009", new String[] { goodsValue.multiply(BigDecimal
					.valueOf(stoExchange.getStoGoodSelectFrom().getMoneyNumber())).toString() }, locale);
			return message;
		}

		return "";
	}

	/**
	 * 兑换明细添加
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 * @param stoExchangeDetail
	 */
	private void detailAdd(StoExchange stoExchange, StoExchange stoExchangeDetail, Model model) {
		boolean findFalg = false;
		// 生成物品ID
		String strGoodsKey = getGoodsKey(stoExchange.getStoGoodSelect());
		// 物品金额
		BigDecimal ChangeGoodsValue = getGoodsValue(stoExchange.getStoGoodSelect());
		BigDecimal amount = ChangeGoodsValue.multiply(BigDecimal.valueOf(stoExchange.getStoGoodSelect()
				.getMoneyNumber()));

		// 判断物品Id存在
		for (StoExchangeGood stoExchangeGood : stoExchangeDetail.getStoExchangeGoodList()) {
			if (strGoodsKey.equals(stoExchangeGood.getGoodsId())) {
				// 数量、金额
				stoExchangeGood.setNum(stoExchangeGood.getNum() + stoExchange.getStoGoodSelect().getMoneyNumber());
				stoExchangeGood.setMoneyAmount(stoExchangeGood.getMoneyAmount().add(amount));
				findFalg = true;
				break;
			}
		}
		// 物品不存在
		if (!findFalg) {
			StoExchangeGood stoExchangeGood = new StoExchangeGood();
			stoExchangeGood.setGoodsId(strGoodsKey);
			// 将共通详细信息拷贝到物品详细
			itemCopy(stoExchangeGood, stoExchange.getStoGoodSelect());

			// 数量、金额
			stoExchangeGood.setNum(stoExchange.getStoGoodSelect().getMoneyNumber());
			stoExchangeGood.setMoneyAmount(stoExchangeGood.getMoneyAmount().add(amount));
			stoExchangeDetail.getStoExchangeGoodList().add(stoExchangeGood);
		}

		// 兑换总金额
		stoExchangeDetail.setChangeAmount(stoExchangeDetail.getChangeAmount().add(amount));
	}

	/**
	 * 兑换保存check
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 * @param stoExchangeDetail
	 */
	private String exchangeSaveCheck(StoExchange stoExchange, StoExchange stoExchangeDetail) {
		Locale locale = LocaleContextHolder.getLocale();
		String message;

		// 输入数量是0
		if (stoExchange.getStoGoodSelectFrom().getMoneyNumber() == 0) {
			message = msg.getMessage("message.E1012", new String[] {}, locale);
			return message;
		}

		// 库存再检索
		StoStoresInfo stoStoresInfo = StoreCommonUtils.getStoStoresInfoByGoodsId(getGoodsKey(stoExchange
				.getStoGoodSelectFrom()),UserUtils.getUser().getOffice().getId());

		// 库存数量不足
		if (stoStoresInfo == null || stoStoresInfo.getSurplusStoNum() == null
				|| stoExchange.getStoGoodSelectFrom().getMoneyNumber() > stoStoresInfo.getSurplusStoNum()) {
			message = msg.getMessage("message.E1010", new String[] {}, locale);
			return message;
		}

		// 明细未添加
		if (stoExchangeDetail == null || stoExchangeDetail.getStoExchangeGoodList() == null
				|| stoExchangeDetail.getStoExchangeGoodList().size() == 0) {
			message = msg.getMessage("message.E1011",
					new String[] { getGoodsKey(stoExchange.getStoGoodSelectFrom()) }, locale);
			return message;
		}
		// 币种不同不能兑换
		if (!stoExchange.getStoGoodSelectFrom().getCurrency()
				.equals(stoExchangeDetail.getStoExchangeGoodList().get(0).getCurrency())) {
			message = msg.getMessage("message.E1006", new String[] {}, locale);
			return message;
		}
		// 假币残损币不能兑换
		if (Constant.MoneyType.COUNTERFEIT_MONEY.equals(stoExchange.getStoGoodSelectFrom().getClassification())
				|| Constant.MoneyType.DEMANGED_MONEY.equals(stoExchange.getStoGoodSelectFrom().getClassification())) {
			message = msg.getMessage("message.E1007", new String[] {}, locale);
			return message;
		}

		// 取得原始物品价值
		BigDecimal goodsValue = getGoodsValue(stoExchange.getStoGoodSelectFrom());
		if (goodsValue == null) {
			message = msg.getMessage("message.E1008",
					new String[] { getGoodsKey(stoExchange.getStoGoodSelectFrom()) }, locale);
			return message;
		}
		// 兑换金额不一致
		if (stoExchangeDetail.getChangeAmount().compareTo(
				goodsValue.multiply(BigDecimal.valueOf(stoExchange.getStoGoodSelectFrom().getMoneyNumber()))) != 0) {
			message = msg.getMessage(
					"message.E1013",
					new String[] {
							goodsValue
							.multiply(BigDecimal.valueOf(stoExchange.getStoGoodSelectFrom().getMoneyNumber()))
							.toString(),
							stoExchangeDetail.getChangeAmount().toString()
							 }, locale);
			return message;
		}
		return "";
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          根据物品编号对应的物品价值，计算金额
	 * @param stoGoodSelect
	 *            物品编号信息
	 * @param model
	 *            Model
	 * @return 物品价值
	 */
	private BigDecimal getGoodsValue(StoGoodSelect stoGoodSelect) {
		// 金额换算
		String strGoodsKey = getGoodsKey(stoGoodSelect);
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsKey);
		return goodsValue;

	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          取得物品Key
	 * 
	 * @param param
	 *            物品参数
	 * @return 物品Key
	 */
	private String getGoodsKey(StoGoodSelect param) {
		StringBuffer strBuf = new StringBuffer();
		// 币种
		strBuf.append(StringUtils.isEmpty(param.getCurrency()) ? "" : param.getCurrency())
		// 类别
				.append(StringUtils.isEmpty(param.getClassification()) ? "" : param.getClassification())
				// 套别
				.append(StringUtils.isEmpty(param.getEdition()) ? "" : param.getEdition())
				// 材质
				.append(StringUtils.isEmpty(param.getCash()) ? "" : param.getCash())
				// 面值
				.append(StringUtils.isEmpty(param.getDenomination()) ? "" : param.getDenomination())
				// 单位
				.append(StringUtils.isEmpty(param.getUnit()) ? "" : param.getUnit());

		return strBuf.toString();
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          库存量检索
	 * @param stoGoodSelect
	 *            物品编号信息
	 * @param model
	 *            Model
	 * @return 物品价值
	 */
	@RequestMapping(value = "getstoNum")
	@ResponseBody
	public String getstoNum(String param, Model model, RedirectAttributes redirectAttributes) {
		// 取得参数
		param = param.replace("&quot;", "\"");

		Gson gson = new Gson();
		StoGoodSelect stoGoodSelect = gson.fromJson(param, StoGoodSelect.class);

		StoStoresInfo stoStoresInfo = StoreCommonUtils.getStoStoresInfoByGoodsId(getGoodsKey(stoGoodSelect),UserUtils.getUser().getOffice().getId());
		if (stoStoresInfo != null) {
			if (stoStoresInfo.getSurplusStoNum() == null) {
				stoStoresInfo.setSurplusStoNum((long) 0);
			}
		} else {
			stoStoresInfo = new StoStoresInfo();
			stoStoresInfo.setSurplusStoNum((long) 0);
		}
		// BigDecimal amount =
		// stoStoresInfo.getAmount().divide(BigDecimal.valueOf(stoStoresInfo.getStoNum())).multiply(BigDecimal.valueOf(stoGoodSelect.getMoneyNumber()));
		BigDecimal goodsValue = getGoodsValue(stoGoodSelect);
		BigDecimal amount = (goodsValue == null ? BigDecimal.ZERO : goodsValue).multiply(BigDecimal
				.valueOf(stoGoodSelect.getMoneyNumber()));
		stoStoresInfo.setAmount(amount);
		return new Gson().toJson(stoStoresInfo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          将共通详细信息拷贝到共通详细信息
	 */
	private void itemCopy(StoGoodSelect stoGoodSelectFrom, StoGoodSelect good) {

		// 币种
		stoGoodSelectFrom.setCurrency(good.getCurrency());
		// 类别
		stoGoodSelectFrom.setClassification(good.getClassification());
		// 软/硬币
		stoGoodSelectFrom.setCash(good.getCash());
		// 面值
		stoGoodSelectFrom.setDenomination(good.getDenomination());
		// 单位
		stoGoodSelectFrom.setUnit(good.getUnit());
		// 套别
		stoGoodSelectFrom.setEdition(good.getEdition());
		// 数量
		stoGoodSelectFrom.setMoneyNumber(good.getMoneyNumber());
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          将共通详细信息拷贝到物品详细
	 */
	private void itemCopy(StoExchangeGood stoExchangeGood, StoGoodSelect good) {
		// 币种
		stoExchangeGood.setCurrency(good.getCurrency());
		// 类别
		stoExchangeGood.setClassification(good.getClassification());
		// 软/硬币
		stoExchangeGood.setCash(good.getCash());
		// 面值
		stoExchangeGood.setDenomination(good.getDenomination());
		// 单位
		stoExchangeGood.setUnit(good.getUnit());
		// 套别
		stoExchangeGood.setEdition(good.getEdition());
	}

	/**
	 * 添加JSON消息
	 * 
	 * @param messages
	 *            消息
	 */
	protected String addMessageByJson(String messages) {
		return addMessageByJson(MESSAGE_KEY, messages);
	}

	/**
	 * 添加JSON消息
	 * 
	 * @param messagesKey
	 *            消息Key
	 * @param messages
	 *            消息
	 */
	protected String addMessageByJson(String messagesKey, String messages) {
		Map<String, Object> messageMap = Maps.newHashMap();
		messageMap.put(messagesKey, messages);
		return this.gson.toJson(messageMap);
	}
}
