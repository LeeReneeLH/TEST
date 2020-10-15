package com.coffer.businesses.modules.report.v01.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.report.v01.entity.StoBoxInfoGraphEntity;
import com.coffer.businesses.modules.report.v01.service.BoxGraphService;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 
 * @author wh
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/box")
public class BoxGraphController extends BaseController {

	@Autowired
	BoxGraphService boxGraphService;

	/**
	 * 箱袋统计图页面
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toBoxGraph")
	public String toBoxGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity, Model model) {

		if (stoBoxInfoGraphEntity == null) {
			stoBoxInfoGraphEntity = new StoBoxInfoGraphEntity();

		}

		model.addAttribute("stoBoxInfoGraphEntity", stoBoxInfoGraphEntity);
		return "/modules/report/v01/box/boxGraph";
	}

	/**
	 * 箱袋类型数据查询
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getBoxTypeGraphData")
	@ResponseBody
	public String getBoxTypeGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		// 获取当前用户
		User user = UserUtils.getUser();
		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {

			stoBoxInfoGraphEntity.setOffice(user.getOffice());

		}

		Map<String, Object> jsonData = boxGraphService.makeBoxTypeGraphData(stoBoxInfoGraphEntity);

		return gson.toJson(jsonData);
	}

	/**
	 * 箱袋状态数据查询
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getBoxStatusGraphData")
	@ResponseBody
	public String getBoxStatusGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		// 获取当前用户
		User user = UserUtils.getUser();
		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			stoBoxInfoGraphEntity.setOffice(user.getOffice());

		}

		Map<String, Object> jsonData = boxGraphService.makeBoxStatusGraphData(stoBoxInfoGraphEntity);

		return gson.toJson(jsonData);
	}

	/**
	 * 箱袋统计图导出
	 * 
	 * @param stoBoxInfoGraphEntity
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "exportBoxGraphDate")
	public String exportBoxGraphDate(StoBoxInfoGraphEntity stoBoxInfoGraphEntity, HttpServletRequest request,
			HttpServletResponse response) {

		// 获取当前用户
		User user = UserUtils.getUser();

		// 判断权限，除顶级机构和平台，只查询当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			stoBoxInfoGraphEntity.setOffice(user.getOffice());

		}

		boxGraphService.exportBoxGraph(stoBoxInfoGraphEntity, request, response, msg);
		return null;
	}

}
