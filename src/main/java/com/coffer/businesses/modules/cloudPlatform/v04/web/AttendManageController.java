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
import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckVisitorInfo;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckEscortInfoService;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckVisitorInfoService;
import com.coffer.businesses.modules.cloudPlatform.CloudPlatformConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StreamUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 考勤管理Controller
 * 
 * @author WQJ
 * @version 2018-12-25
 */
@Controller
@RequestMapping(value = "${adminPath}/cloudPlateform/v04/attendManage")
public class AttendManageController extends BaseController {

	@Autowired
	private EyeCheckVisitorInfoService eyeCheckVisitorInfoService;
	@Autowired
	private EyeCheckEscortInfoService eyeCheckEscortInfoService;

	/**
	 * 根据主键获取员工信息
	 * 
	 * @author WQJ
	 * @version 2018-12-25
	 * @param visitorId
	 * @return
	 */
	@ModelAttribute
	public EyeCheckVisitorInfo get(@RequestParam(required = false) String visitorId) {
		EyeCheckVisitorInfo entity = null;
		if (StringUtils.isNotBlank(visitorId)) {
			entity = eyeCheckVisitorInfoService.get(visitorId);
		}
		if (entity == null) {
			entity = new EyeCheckVisitorInfo();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author WQJ
	 * @version 2018-12-25
	 * @param eyeCheckVisitorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("cloudPlateform:attendManage:view")
	@RequestMapping(value = { "list", "" })
	public String list(EyeCheckVisitorInfo eyeCheckVisitorInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		eyeCheckVisitorInfo.setEscortType(CloudPlatformConstant.escortType.ESCORT);
		// 查询访客信息列表
		Page<EyeCheckVisitorInfo> page = eyeCheckVisitorInfoService
				.findPage(new Page<EyeCheckVisitorInfo>(request, response), eyeCheckVisitorInfo);
		model.addAttribute("page", page);
		model.addAttribute("attend", "员工考勤列表");
		return "modules/cloudPlatform/v04/visitorStatistics/visitorStatisticsList";
	}

	/**
	 * 跳转至实时比对页面
	 * 
	 * @author XL
	 * @version 2018-12-07
	 * @param eyeCheckVisitorInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "visitorStatisticsView" })
	public String visitorStatisticsView(EyeCheckVisitorInfo eyeCheckVisitorInfo, Model model) {
		// 访客信息
		model.addAttribute("eyeCheckVisitorInfo", eyeCheckVisitorInfo);
		// 人员信息
		EyeCheckEscortInfo eyeCheckEscortInfo = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(
				eyeCheckVisitorInfo.getIdcardNo(), eyeCheckVisitorInfo.getOffice().getId());
		model.addAttribute("eyeCheckEscortInfo", eyeCheckEscortInfo);
		return "modules/cloudPlatform/v04/visitorStatistics/visitorStatisticsView";
	}

	/**
	 * 显示访客照片
	 * 
	 * @author XL
	 * @version 2018-12-07
	 * @param visitorId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showVisitorImage")
	public void showVisitorImage(@RequestParam(required = false) String visitorId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		// 照片格式设置
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		InputStream inputStream = null;
		byte[] imageBytes = null;
		// 获取访客信息
		EyeCheckVisitorInfo eyeCheckVisitorInfo = eyeCheckVisitorInfoService.get(visitorId);
		try {
			if (eyeCheckVisitorInfo != null) {
				imageBytes = eyeCheckVisitorInfo.getPhoto();
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

	/**
	 * 显示人员照片
	 * 
	 * @author XL
	 * @version 2018-12-07
	 * @param visitorId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showEscortImage")
	public void showEscortImage(@RequestParam(required = false) String escortIdCard, @RequestParam(required = false) String officeId, HttpServletRequest request,
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
		EyeCheckEscortInfo eyeCheckEscortInfo = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(escortIdCard,officeId);
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