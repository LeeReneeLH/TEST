package com.coffer.businesses.modules.doorOrder.v01.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.doorOrder.v01.entity.PaidRecord;
import com.coffer.businesses.modules.doorOrder.v01.service.PaidRecordService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 汇款记录保存Controller
 * 
 * @author WQJ
 * @version 2019-08-14
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/paidRecord")
public class PaidRecordController extends BaseController {

	@Autowired
	private PaidRecordService paidRecordService;

	@ModelAttribute
	public PaidRecord get(@RequestParam(required = false) String id) {
		PaidRecord entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = paidRecordService.get(id);
		}
		if (entity == null) {
			entity = new PaidRecord();
		}
		return entity;
	}

	@RequiresPermissions("doororder.v01:paidRecord:view")
	@RequestMapping(value = { "list", "" })
	public String list(PaidRecord paidRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询条件： 开始时间
		if (paidRecord.getCreateTimeStart() != null) {
			paidRecord.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(paidRecord.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (paidRecord.getCreateTimeEnd() != null) {
			paidRecord
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(paidRecord.getCreateTimeEnd())));
		}
		// 数据过滤
		User userInfo = UserUtils.getUser();
		paidRecord.setRecordOfficeId(userInfo.getOffice().getId());
		paidRecord.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		Page<PaidRecord> page = paidRecordService.findPage(new Page<PaidRecord>(request, response), paidRecord);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/paidRecord/paidRecordList";
	}

	@RequiresPermissions("doororder.v01:paidRecord:view")
	@RequestMapping(value = "form")
	public String form(PaidRecord paidRecord, Model model) {
		model.addAttribute("paidRecord", paidRecord);
		return "modules/doorOrder/v01/paidRecord/paidRecordForm";
	}

	@RequiresPermissions("doororder.v01:paidRecord:edit")
	@RequestMapping(value = "save")
	public String save(PaidRecord paidRecord, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, paidRecord)) {
			return form(paidRecord, model);
		}
		paidRecordService.save(paidRecord);
		addMessage(redirectAttributes, "保存汇款记录成功!");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/paidRecord/?repage";
	}

	@RequiresPermissions("doororder.v01:paidRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(PaidRecord paidRecord, RedirectAttributes redirectAttributes) {
		paidRecordService.delete(paidRecord);
		addMessage(redirectAttributes, "删除汇款记录成功!");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/paidRecord/?repage";
	}

}