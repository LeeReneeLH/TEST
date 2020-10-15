package com.coffer.businesses.modules.cloudPlatform.v04.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

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

import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckEscortInfo;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckEscortInfoService;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckVisitorInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StreamUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 人员信息Controller
 * 
 * @author WQJ
 * @version 2018-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/cloudPlateform/v04/PeopleInformation")
public class PeopleInformationController extends BaseController {

	@Autowired
	private EyeCheckVisitorInfoService eyeCheckVisitorInfoService;
	@Autowired
	private EyeCheckEscortInfoService eyeCheckEscortInfoService;

	/**
	 * 根据主键获取人员信息
	 * 
	 * @author WQJ
	 * @version 2018-12-18
	 * @param escortId
	 * @return
	 */
	@ModelAttribute
	public EyeCheckEscortInfo get(@RequestParam(required = false) String escortId) {
		EyeCheckEscortInfo entity = null;
		if (StringUtils.isNotBlank(escortId)) {
			entity = eyeCheckEscortInfoService.get(escortId);
		}
		if (entity == null) {
			entity = new EyeCheckEscortInfo();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author WQJ
	 * @version 2018-12-18
	 * @param EyeCheckEscortInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("cloudPlateform:EyeCheckEscortInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(EyeCheckEscortInfo eyeCheckEscortInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 查询访客信息列表
		Page<EyeCheckEscortInfo> page = eyeCheckEscortInfoService
				.findPage(new Page<EyeCheckEscortInfo>(request, response), eyeCheckEscortInfo);
		model.addAttribute("page", page);
		return "modules/cloudPlatform/v04/peopleInformation/peopleInformationList";
	}

	/**
	 * 跳转至人员信息页面
	 * 
	 * @author WQJ
	 * @version 2018-12-07
	 * @param eyeCheckEscortInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "PeopleInformationView" })
	public String attendManageView(EyeCheckEscortInfo eyeCheckEscortInfo, Model model) {
		// 人员信息
		EyeCheckEscortInfo eyeCheckEscort = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(
				eyeCheckEscortInfo.getIdcardNo(), eyeCheckEscortInfo.getOffice().getId());
		model.addAttribute("eyeCheckEscortInfo", eyeCheckEscort);
		return "modules/cloudPlatform/v04/peopleInformation/peopleInformationView";
	}

	/**
	 * 显示人员照片
	 * 
	 * @author WQJ
	 * @version 2018-12-19
	 * @param escortIdCard
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showEscortImage")
	public void showEscortImage(@RequestParam(required = false) String idcardNo, @RequestParam(required = false) String officeId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		// 照片格式设置(身份证采集的是bmp格式)
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/bmp");
		OutputStream out = response.getOutputStream();
		InputStream inputStream = null;
		byte[] imageBytes = null;
		// 获取人员信息
		EyeCheckEscortInfo eyeCheckEscortInfo = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(idcardNo,officeId);
		try {
			if (eyeCheckEscortInfo != null) {
				imageBytes = eyeCheckEscortInfo.getPhoto();
				if (imageBytes == null) {
					// 将文件转成byte[]
					inputStream = request.getSession().getServletContext()
							.getResourceAsStream(Global.getConfig("image.escort.path"));
					if (inputStream == null) {
						throw new IOException();
					}
					imageBytes = StreamUtils.InputStreamTOByte(inputStream);
				}
				out.write(imageBytes);
				out.flush();
			}
		} catch (IOException e) {
			logger.error(msg.getMessage("message.E1071", null, locale));
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} finally {
				logger.error(msg.getMessage("message.E1071", null, locale));
			}
			out.close();
		}
	}
}