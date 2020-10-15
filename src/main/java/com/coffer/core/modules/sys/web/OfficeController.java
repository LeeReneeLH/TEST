package com.coffer.core.modules.sys.web;

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
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 机构Controller
 * 
 * @author Clark
 * @version 2015-5-13
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

	@Autowired
	private OfficeService officeService;

	@ModelAttribute("office")
	public Office get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return officeService.get(id);
		} else {
			return new Office();
		}
	}

	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = { "" })
	public String index(Office office, Model model) {
		// model.addAttribute("list", officeService.findAll());
		return "modules/sys/officeIndex";
	}

	/** 注释机构列表及查询源代码 lihe 2019-12-11 start **/
	// @RequiresPermissions("sys:office:view")
	// @RequestMapping(value = { "list" })
	// public String list(Office office, Model model) {
	// Office office2 = UserUtils.getUser().getOffice();
	// office.setParentIds(office2.getParentIds() + office2.getId());
	// office.setId(office2.getId());
	// model.addAttribute("list", officeService.findList(office));
	// return "modules/sys/officeList";
	// }
	//
	// @RequiresPermissions("sys:office:view")
	// @RequestMapping(value = { "search" })
	// public String search(Office office, Model model) {
	// //if
	// (!Constant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType()))
	// {
	// // 当非顶级机构进行登录时设置过滤条件
	// Office office2 = UserUtils.getUser().getOffice();
	// office.setId(office2.getId());
	// office.setParentIds(office2.getParentIds() + office2.getId());
	// //}
	// model.addAttribute("list", officeService.findListBySearch(office));
	// return "modules/sys/officeList";
	// }
	/** 注释机构列表源代码 lihe 2019-12-11 end **/

	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = { "search" })
	public String list(Office office, Model model, HttpServletRequest request, HttpServletResponse response) {
		Page<Office> page = new Page<Office>(request, response);
		office.setPage(page);
		List<Office> list = officeService.findListByPage(office);
		page.setList(list);
		model.addAttribute("list", list);
		model.addAttribute("page", page);
		return "modules/sys/officeList";
	}

	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = { "list" })
	public String search(Office office, Model model, HttpServletRequest request, HttpServletResponse response) {
		Page<Office> page = new Page<Office>(request, response);
		Office office2 = UserUtils.getUser().getOffice();
		if (StringUtils.isBlank(office.getId())) {
			office.setId(office2.getId());
		}
		office.setParentIds(office2.getParentIds() + office2.getId());
		office.setPage(page);
		//office.setType(null);
		List<Office> list = officeService.findPageListBySearch(office);
		page.setList(list);
		// 设置状态为1 带模糊查询的分页
		page.setSearchType(1);
		model.addAttribute("list", list);
		model.addAttribute("page", page);
		return "modules/sys/officeList";
	}

	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "form")
	public String form(Office office, Model model) {
		User user = UserUtils.getUser();
		if (office.getParent() == null || office.getParent().getId() == null) {
			office.setParent(user.getOffice());
		}

		// 新建机构的场合
		if (StringUtils.isBlank(office.getId())) {
			office.setTradeFlag(Constant.TradeFlag.Bank);
		}

		office.setParent(officeService.get(office.getParent().getId()));
		// 查询归属机构 修改人：xp 修改时间：2017-7-4 begin
		// office.setAscriptionOfficeId(officeService.get(office.getAscriptionOfficeId().getName()));
		// end

		// 自动获取排序号
		// if (StringUtils.isBlank(office.getId()) && office.getParent() !=
		// null) {
		// int size = 0;
		// List<Office> list = officeService.findAll();
		// for (int i = 0; i < list.size(); i++) {
		// Office e = list.get(i);
		// if (e.getParent() != null && e.getParent().getId() != null
		// && e.getParent().getId().equals(office.getParent().getId())) {
		// size++;
		// }
		// }
		// office.setCode(office.getParent().getCode()
		// + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3,
		// "0"));
		// }
		model.addAttribute("office", office);
		return "modules/sys/officeForm";
	}

	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "save")
	public String save(Office office, Model model, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			if (Global.isDemoMode()) {
				addMessage(redirectAttributes, "演示模式，不允许操作！");
				return "redirect:" + adminPath + "/sys/office/";
			}
			// 默认设置行内机构 修改人：LLF 修改时间：20151029 begin
			if (!"4".equals(office.getType()) || StringUtils.isBlank(office.getTradeFlag())) {
				office.setTradeFlag("0");
			}
			// end
			if (!beanValidator(model, office)) {
				return form(office, model);
			}
			officeService.save(office);
			if (office.getChildDeptList() != null) {
				Office childOffice = null;
				for (String id : office.getChildDeptList()) {
					childOffice = new Office();
					childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
					childOffice.setParent(office);
					childOffice.setType("2");
					officeService.save(childOffice);
				}
			}
			addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + adminPath + "/sys/office/list";
		}
		return "redirect:" + adminPath + "/sys/office/list";
	}

	@RequiresPermissions("sys:office:edit")
	@RequestMapping(value = "delete")
	public String delete(Office office, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/office/list";
		}
		if (office.isRoot()) {
			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构！");
		} else {
			officeService.delete(office);
			addMessage(redirectAttributes, "删除机构成功！");
		}
		return "redirect:" + adminPath + "/sys/office/list";
	}

	/**
	 * 机构管理功能：机构树状图异步加载子机构方法
	 * 
	 * @author xp
	 * @param pId
	 * @param extId
	 * @param type
	 * @param maxType
	 * @param isAll
	 * @param tradeFlag
	 * @param isNotNeedSubPobc
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeDataOffice")
	public List<Map<String, Object>> treeDataOffice(@RequestParam(required = false) String pId,
			@RequestParam(required = false) String extId, @RequestParam(required = false) Long type,
			@RequestParam(required = false) Long maxType, @RequestParam(required = false) Boolean isAll,
			@RequestParam(required = false) String tradeFlag, @RequestParam(required = false) Boolean isNotNeedSubPobc,
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Office loginUserOffice = UserUtils.getUser().getOffice();
		List<Office> list = Lists.newArrayList();
		// 初始化节点的时候 默认查询最顶级的节点
		if (pId == null || pId == "") {
			Office office = officeService.get(loginUserOffice.getId());
			list.add(office);
		} else {
			list = officeService.findListByAsync(pId, Global.getConfig("jdbc.type"));
		}
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			if ((extId == null
					|| (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
					&& (type == null || (type != null && Integer.parseInt(e.getType()) == type.intValue()))
					&& (maxType == null || (maxType != null && Integer.parseInt(e.getType()) <= maxType.intValue()))
					&& (StringUtils.isBlank(tradeFlag)
							|| (StringUtils.isNotBlank(tradeFlag) && tradeFlag.equals(e.getTradeFlag())))
					&& (isNotNeedSubPobc == null || (isNotNeedSubPobc != null && isNotNeedSubPobc == true
							&& e.getParentId().equals(loginUserOffice.getId())))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("type", e.getType());

				if (pId == null || pId == "" || e.getSubagencyCount() != 0) {
					map.put("isParent", true);
				}
				mapList.add(setIconSkin(map,e));
			}
		}
		return mapList;
	}

	/**
	 * @author Clark
	 * @version 2015-05-19 获取机构JSON数据。
	 * 
	 * @param extId            排除的ID
	 * @param type             等于
	 * @param maxType          小于等于
	 * @param isAll
	 * @param isNotNeedSubPobc 是否列出当前人行机构下子人行及其商业机构
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			@RequestParam(required = false) Long type, @RequestParam(required = false) Long maxType,
			@RequestParam(required = false) Boolean isAll, @RequestParam(required = false) String tradeFlag,
			@RequestParam(required = false) Boolean isNotNeedSubPobc, HttpServletResponse response,
			@RequestParam(required = false) Long minType, @RequestParam(required = false) Boolean clearCenterFilter) {
		response.setContentType("application/json; charset=UTF-8");
		// 人民银行的管理员在机构管理画面只能看到“金库”以上级别的机构，“分组”以下的机构不可见。
		// User user = UserUtils.getUser();
		// if (Constant.SysUserType.CENTRAL_MANAGER.equals(user.getUserType()))
		// {
		// maxType = Long.parseLong(Constant.OfficeType.COFFER);
		// }
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Office> list = officeService.findList(isAll);
		Office loginUserOffice = UserUtils.getUser().getOffice();
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			if ((extId == null
					|| (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
					&& (type == null || (type != null && Integer.parseInt(e.getType()) == type.intValue()))
					&& (maxType == null || (maxType != null && Integer.parseInt(e.getType()) <= maxType.intValue()))
					&& (minType == null || (minType != null && Integer.parseInt(e.getType()) >= minType.intValue()))
					&& (StringUtils.isBlank(tradeFlag)
							|| (StringUtils.isNotBlank(tradeFlag) && tradeFlag.equals(e.getTradeFlag())))
					&& (isNotNeedSubPobc == null || (isNotNeedSubPobc != null && isNotNeedSubPobc == true
							&& e.getParentId().equals(loginUserOffice.getId())))
					&& (clearCenterFilter == null
							|| (clearCenterFilter != null && loginUserOffice.getId().equals(e.getId()))
							|| (clearCenterFilter != null && clearCenterFilter == true
									&& Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getParentId() + ",") != -1)
							|| (clearCenterFilter != null && clearCenterFilter == true
									&& !Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getId() + ",") != -1))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("type", e.getType());
				mapList.add(setIconSkin(map,e));
			}
		}
		return mapList;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年11月10日
	 * 
	 * 
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkCode")
	public String checkOfficeCode(String code, String oldCode) {
		return officeService.checkOfficeCode(code, oldCode);
	}

	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/office/list?repage";
	}

	/**
	 * 
	 * Title: deleteCheck
	 * <p>
	 * Description: 查看要删除的机构下是否有有效用户
	 * </p>
	 * 
	 * @author: wanghan
	 * @param response
	 * @param id
	 * @return
	 * @throws JsonProcessingException String 返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "deleteCheck")
	public String deleteCheck(HttpServletResponse response, String id) throws JsonProcessingException {

		int userNum = officeService.getVaildCntByOfficeId(id);

		if (userNum == 0) {
			return renderString(response, "false");
		}
		return renderString(response, "true");

	}

	/**
	 * 
	 * Title: getParentOffice
	 * <p>
	 * Description: 获取上级机构
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param office
	 * @return Office 返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getParentOffice")
	public Office getParentOffice(Office office) {
		return SysCommonUtils.findOfficeById(office.getParentId());
	}
	/**
	 * 
	 * Title: treeDataOfficeForDoor
	 * <p>
	 * Description: 存款查看功能：机构树状图数据查询
	 * </p>
	 * 
	 * @author: WQJ
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeDataOfficeForDoor")
	public List<Map<String, Object>> treeDataOfficeForDoor(@RequestParam(required = false) String pId,
			@RequestParam(required = false) String extId, @RequestParam(required = false) Long type,
			@RequestParam(required = false) Long maxType, @RequestParam(required = false) Boolean isAll,
			@RequestParam(required = false) String tradeFlag, @RequestParam(required = false) Boolean isNotNeedSubPobc,
			@RequestParam(required = false) Long isNotType,
			HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		Office loginUserOffice = UserUtils.getUser().getOffice();
		List<Office> list = Lists.newArrayList();
		// 初始化节点的时候默认查询当前节点下所有子节点
		if (pId == null || pId == "") {
			if (loginUserOffice.getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
				list = officeService.findByParentIdsLike(loginUserOffice);
			} else {
				list = officeService.findCustList(null, loginUserOffice.getId(), null);
			}
		}
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			boolean available =  !StringUtils.equals(e.getType(), Constant.OfficeType.CLEAR_CENTER)
                    && (extId == null || (extId != null && !extId.equals(e.getId())
                    && e.getParentIds().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && Integer.parseInt(e.getType()) == type.intValue()))
                    && (maxType == null || (maxType != null && Integer.parseInt(e.getType()) <= maxType.intValue()))
                    && (isNotType == null || (isNotType != null && Integer.parseInt(e.getType()) != isNotType.intValue()))
                    && (!StringUtils.equals(e.getType(),Constant.OfficeType.PETROL_CODE))			//过滤油站编码
                    && (StringUtils.isBlank(tradeFlag)
                    || (StringUtils.isNotBlank(tradeFlag) && tradeFlag.equals(e.getTradeFlag())))
                    && (isNotNeedSubPobc == null || (isNotNeedSubPobc != null && isNotNeedSubPobc == true
                    && e.getParentId().equals(loginUserOffice.getId())));
			if ( available || isAll) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("type", e.getType());
				mapList.add(setIconSkin(map,e));
			}
		}
		return mapList;
	}
	
	/**
	 * 
	 * Title: setIconSkin
	 * <p>
	 * Description: 添加iconSkin
	 * </p>
	 * 2020/06/05
	 * @author: HZY
	 */
	public Map<String, Object> setIconSkin(Map<String, Object> map,Office e) {
		if(StringUtils.equals(e.getType(),Constant.OfficeType.CLEAR_CENTER)){//中心
			map.put("iconSkin", "center");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.STORE)){//门店
			map.put("iconSkin",	"door");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.MERCHANT)){//商户
			map.put("iconSkin", "merchant");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.COFFER)){//公司
			map.put("iconSkin", "company");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.DIGITAL_PLATFORM)){//平台
			map.put("iconSkin", "platform");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.CENTRAL_BANK)){//区域
			map.put("iconSkin", "central");
		}
		if(StringUtils.equals(e.getType(),Constant.OfficeType.ROOT)){//顶级机构
			map.put("iconSkin", "root");
		}
		return map;
	}
}
