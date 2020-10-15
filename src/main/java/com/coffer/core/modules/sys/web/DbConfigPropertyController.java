package com.coffer.core.modules.sys.web;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.entity.DbConfigProperty;
import com.coffer.core.modules.sys.service.DbConfigPropertyService;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 系统共通项controller
 * 
 * @author xp
 * @version 2017-12-9
 */
@Controller
@RequestMapping({ "${adminPath}/sys/dbConfig/DbConfigPropertyController" })
public class DbConfigPropertyController extends BaseController {
	@Autowired
	private DbConfigPropertyService dbConfigPropertyService;

	@ModelAttribute
	public DbConfigProperty get(@RequestParam(required = false) String id) {
		DbConfigProperty entity = new DbConfigProperty();
		if (StringUtils.isNotBlank(id)) {
			entity.setId(id);
			entity = dbConfigPropertyService.get(entity);
		}
		return entity;
	}

	/**
	 * 查询所有参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 * @param model
	 * @return
	 */
	@RequestMapping({ "list", "" })
	public String findList(DbConfigProperty dbConfigProperty, Model model) {
		List<DbConfigProperty> list = Lists.newArrayList();
		// 查询出所有参数
		List<DbConfigProperty> sourcelist = DbConfigUtils.getDbConfigList(dbConfigProperty);
		// 按照父级id进行排序
		DbConfigProperty.sortList(list, sourcelist, DbConfigProperty.getRootId(), true);
		model.addAttribute("propertiesList", list);
		model.addAttribute("parentDbConfig", new DbConfigProperty());
		return "modules/sys/dbConfigPropertyList";
	}

	/**
	 * 保存参数
	 * 
	 * @author xp
	 * @param model
	 * @param dbConfigProperty
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("save")
	public String saveDbConfigProperties(@RequestParam(required = false) String strUpdateDate,
			@RequestParam(required = false) String configType, Model model, DbConfigProperty dbConfigProperty,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		// 设置查询的参数信息
		DbConfigProperty parentDbConfig = new DbConfigProperty();
		parentDbConfig.setId(dbConfigProperty.getParentId());
		dbConfigProperty.setParent(parentDbConfig);
		// 设置更新人、更新时间的信息
		dbConfigProperty.setUpdateBy(UserUtils.getUser());
		dbConfigProperty.setUpdateName(UserUtils.getUser().getName());
		dbConfigProperty.setUpdateDate(new Date());
		dbConfigProperty.setConfigType(configType);
		// 如果当前操作类型为添加
		if (DbConfigConstant.ConfigType.INSERT.equals(dbConfigProperty.getConfigType())) {
			dbConfigProperty.setId(IdGen.uuid());
			// 查询父级参数
			DbConfigProperty parentProperties = dbConfigPropertyService.getProperty(parentDbConfig);
			// 设置新参数的父级id
			String parentIds = parentProperties.getParentIds() + Constant.Punctuation.COMMA + parentProperties.getId();
			dbConfigProperty.setParentIds(parentIds);
			// 设置创建人、创建时间等信息
			dbConfigProperty.setCreateDate(new Date());
			dbConfigProperty.setCreateBy(UserUtils.getUser());
			dbConfigProperty.setCreateName(UserUtils.getUser().getName());
			// 如果参数值不为空，设置类型为key
			if (StringUtils.isNotBlank(dbConfigProperty.getPropertyValue())) {
				dbConfigProperty.setType(DbConfigConstant.Type.KEY);
			}
			try {
				// 查询库内现在是否有当前参数
				DbConfigProperty currentProperty = dbConfigPropertyService.getProperty(dbConfigProperty);
				if (currentProperty == null) {
					// 插入
					dbConfigPropertyService.insert(dbConfigProperty);
					// 成功
					message = msg.getMessage("message.I9003", new String[] { dbConfigProperty.getPropertyKey() },
							locale);
				} else {
					// 当前参数已存在
					message = msg.getMessage("message.E9015", new String[] { dbConfigProperty.getPropertyKey() },
							locale);
				}
			} catch (ServiceException se) {
				// 失败
				message = msg.getMessage("message.E9009", new String[] { dbConfigProperty.getPropertyKey() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		} else {
			try {
				dbConfigProperty.setStrUpdateDate(strUpdateDate);
				// 更新
				dbConfigPropertyService.update(dbConfigProperty);
				// 成功
				message = msg.getMessage("message.I9005", new String[] { dbConfigProperty.getPropertyKey() }, locale);
			} catch (ServiceException se) {
				// 失败
				message = msg.getMessage("message.E9011", new String[] { dbConfigProperty.getPropertyKey() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		}
		// 添加信息
		addMessage(redirectAttributes, new String[] { message });
		return "redirect:" + adminPath + "/sys/dbConfig/DbConfigPropertyController/list";
	}

	/**
	 * 返回修改页面
	 * 
	 * @author xp
	 * @param model
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("form")
	public String form(DbConfigProperty dbConfigProperty, Model model, HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			// IE中文乱码处理
			String propertyKey = new String(request.getParameter("propertyKey").getBytes("iso-8859-1"), "utf-8");
			dbConfigProperty.setPropertyKey(propertyKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		// 返回的参数
		DbConfigProperty configProperty = new DbConfigProperty();
		DbConfigProperty parentConfigProperty = new DbConfigProperty();
		if (StringUtils.isNotBlank(dbConfigProperty.getPropertyKey())) {
			// 如果操作类型为修改
			if (DbConfigConstant.ConfigType.UPDATE.equals(dbConfigProperty.getConfigType())) {
				configProperty.setId(dbConfigProperty.getId());
				configProperty.setPropertyKey(dbConfigProperty.getPropertyKey());
				configProperty.setPropertyValue(dbConfigProperty.getPropertyValue());
				configProperty.setRemark(dbConfigProperty.getRemark());
				configProperty.setParent(dbConfigProperty.getParent());
				configProperty.setParentIds(dbConfigProperty.getParent().getParentIds());
				configProperty.setType(dbConfigProperty.getType());
				// 设置当前数据查询出的上一次更新时间
				configProperty.setStrUpdateDate(dbConfigProperty.getStrUpdateDate());
			} else {
				// 如果操作类型为插入
				parentConfigProperty.setId(dbConfigProperty.getId());
				parentConfigProperty.setPropertyKey(dbConfigProperty.getPropertyKey());
				configProperty.setParent(parentConfigProperty);
				configProperty.setType(dbConfigProperty.getType());
			}
			// 设置当前操作类型
			configProperty.setConfigType(dbConfigProperty.getConfigType());

			model.addAttribute("property", configProperty);
			} else {
				// 失败
				message = msg.getMessage("message.E9013", new String[] { dbConfigProperty.getPropertyKey() }, locale);
				// 添加信息
				addMessage(redirectAttributes, new String[] { message });
				return "redirect:" + adminPath + "/sys/dbConfig/DbConfigPropertyController/list";
			}
		return "/modules/sys/dbConfigPropertyForm";
	}

	/**
	 * 删除参数
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("delete")
	public String delete(@ModelAttribute("strUpdateDate") String strUpdateDate,
			DbConfigProperty dbConfigProperty,
			HttpServletResponse response, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// IE中文乱码处理
			String propertyKey = new String(request.getParameter("propertyKey").getBytes("iso-8859-1"), "utf-8");
			dbConfigProperty.setPropertyKey(propertyKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			dbConfigProperty.setStrUpdateDate(strUpdateDate);
			// 设置参数为无效
			dbConfigProperty.setDelFlag(DbConfigConstant.deleteFlag.Invalid);
			// 设置更新人、更新时间的信息
			dbConfigProperty.setUpdateBy(UserUtils.getUser());
			dbConfigProperty.setUpdateName(UserUtils.getUser().getName());
			dbConfigProperty.setUpdateDate(new Date());
			dbConfigProperty.setParentIds(dbConfigProperty.getParent().getParentIds());
			// 删除参数
			dbConfigPropertyService.delete(dbConfigProperty);
			// 成功
			message = msg.getMessage("message.I9004", new String[] { dbConfigProperty.getPropertyKey() }, locale);
		} catch (ServiceException se) {
			// 失败
			message = msg.getMessage("message.E9010", new String[] { dbConfigProperty.getPropertyKey() }, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		// 添加信息
		addMessage(redirectAttributes, new String[] { message });
		return "redirect:" + adminPath + "/sys/dbConfig/DbConfigPropertyController/list";
	}

	/**
	 * 删除分组
	 * 
	 * @author xp
	 * @param dbConfigProperty
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("deleteGroup")
	public String deleteGroup(@ModelAttribute("strUpdateDate") String strUpdateDate, DbConfigProperty dbConfigProperty,
			HttpServletResponse response, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// IE中文乱码处理
			String propertyKey = new String(request.getParameter("propertyKey").getBytes("iso-8859-1"), "utf-8");
			dbConfigProperty.setPropertyKey(propertyKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			dbConfigProperty.setStrUpdateDate(strUpdateDate);
			// 设置参数为无效
			dbConfigProperty.setDelFlag(DbConfigConstant.deleteFlag.Invalid);
			// 设置更新人、更新时间的信息
			dbConfigProperty.setUpdateBy(UserUtils.getUser());
			dbConfigProperty.setUpdateName(UserUtils.getUser().getName());
			dbConfigProperty.setUpdateDate(new Date());
			dbConfigProperty.setParentIds(dbConfigProperty.getParent().getParentIds());
			// 查询当前参数是否存在子参数
			List<DbConfigProperty> propertyList = dbConfigPropertyService.findByParentId(dbConfigProperty);
			// 如果存在
			if (!Collections3.isEmpty(propertyList)) {
				// 删除分组
				dbConfigPropertyService.deleteGroup(dbConfigProperty);
			}
			// 删除当前参数
			dbConfigPropertyService.delete(dbConfigProperty);
			// 成功
			message = msg.getMessage("message.I9006", new String[] { dbConfigProperty.getPropertyKey() }, locale);
		} catch (ServiceException se) {
			// 失败
			message = msg.getMessage("message.E9012", new String[] { dbConfigProperty.getPropertyKey() }, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		// 添加信息
		addMessage(redirectAttributes, new String[] { message });
		return "redirect:" + adminPath + "/sys/dbConfig/DbConfigPropertyController/list";
	}

	/**
	 * 参数的键是否重复验证
	 * 
	 * @author xp
	 * @param oldPropertyKey
	 * @param propertyKey
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkPropertyKey")
	public String checkPropertyKey(String propertyKey) {
		DbConfigProperty property = new DbConfigProperty();
		// 将参数键按“，”分割
		String oldPropertyKey = propertyKey.split(Constant.Punctuation.COMMA)[0];
		String key = propertyKey
				.split(Constant.Punctuation.COMMA)[(propertyKey.split(Constant.Punctuation.COMMA).length - 1)];
		property.setPropertyKey(key);
		if ((key != null & key.equals(oldPropertyKey))) {
			return DbConfigConstant.checkIfExit.TRUE;
		}
		if ((key != null) && (dbConfigPropertyService.getProperty(property) == null)) {
			return DbConfigConstant.checkIfExit.TRUE;
		}
		return DbConfigConstant.checkIfExit.FALSE;
	}


}
