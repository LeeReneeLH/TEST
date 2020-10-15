package com.coffer.businesses.modules.store.v01.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.Constant.SysUserType;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.utils.FileUtils;
import com.coffer.core.common.utils.StreamUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * @author niguoyong
 * @date 2015-09-06
 * 
 * @Description 人员管理Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoEscortInfo")
public class StoEscortInfoController extends BaseController {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	@Autowired
	private SystemService systemService;

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 根据押运人员ID查询人员信息 @param 参数 @return @throws
	 */
	@ModelAttribute
	public StoEscortInfo get(@RequestParam(required = false) String id) {
		StoEscortInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = stoEscortInfoService.get(id);
		}
		if (entity == null) {
			entity = new StoEscortInfo();
		}
		return entity;
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 跳转到“人员列表”画面 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoEscortInfo stoEscortInfo, @RequestParam(required = false) boolean isSearch,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoEscortInfo> page = new Page<StoEscortInfo>(request, response);
		// if (isSearch) {
		// User user = UserUtils.getUser();
		// if (!user.isAdmin()) {
		// stoEscortInfo.setUpdateBy(user);
		// }
		page = stoEscortInfoService.findPage(page, stoEscortInfo);
		model.addAttribute("page", page);
		// }
		return "modules/store/v01/stoEscortInfo/stoEscortInfoList";
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 跳转到“人员增加”（或“人员编辑”）画面 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:view")
	@RequestMapping(value = "form")
	public String form(StoEscortInfo stoEscortInfo, Model model) {
		if(stoEscortInfo.getEscortType()==null){
			stoEscortInfo.setEscortType(Constant.SysUserType.ESCORT);
		}
		model.addAttribute("stoEscortInfo", stoEscortInfo);
		return "modules/store/v01/stoEscortInfo/stoEscortInfoForm";
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 处理“人员增加”（或“人员编辑”）画面的保存操作 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:edit")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(StoEscortInfo stoEscortInfo, Model model, RedirectAttributes redirectAttributes) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 如果人员类型为押运人员，进行验证
		if (Constant.SysUserType.ESCORT.equals(stoEscortInfo.getEscortType())) {
			// 实体验证
			if (!beanValidator(model, stoEscortInfo)) {
				return form(stoEscortInfo, model);
			}
		}
		if (StringUtils.isBlank(stoEscortInfo.getId())) {
			// 创建：取得身份信息
			StoEscortInfo validstoEscortInfo = stoEscortInfoService.checkIdcardNo(stoEscortInfo.getIdcardNo());
			// 判断身份信息是否存在
			if (validstoEscortInfo != null) {
				message = msg.getMessage("message.E1017", new Object[] { stoEscortInfo.getIdcardNo() }, locale);
				addMessage(redirectAttributes, message);
				return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
			}
		}

		try {
			if (SysUserType.ESCORT.equals(stoEscortInfo.getEscortType())) {
				/** 押运人员创建指定当前登陆人所属机构 修改人：LLF 修改时间：2016-06-17 */

				// Office office = new Office();
				// office.setId(Global.getConfig("top.office.id"));
				stoEscortInfo.setOffice(UserUtils.getUser().getOffice());
			}
			// 保存
			stoEscortInfoService.save(stoEscortInfo);
			// 成功
			message = msg.getMessage("message.I1004", new Object[] { stoEscortInfo.getEscortName() }, locale);

		} catch (ServiceException se) {
			// 失败
			message = msg.getMessage("message.E1017", new Object[] { stoEscortInfo.getIdcardNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
		}

		if (Constant.ImageSeting.UPLOAD_FLAG_YES.equals(stoEscortInfo.getUploadFlag())) {
			addMessage(model, message);
			model.addAttribute("stoEscortInfo", stoEscortInfo);
			return "modules/store/v01/stoEscortInfo/ajaxFileUpload2";
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	}

	/**
	 * 返回
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequestMapping(value = "back")
	public String back(StoEscortInfo stoEscortInfo, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	}

	/**
	 * 图片剪裁页面返回到列表页面，并删除已上传图片
	 * 
	 * @author wangbaozhong
	 * @version 2016-05-08
	 */
	@RequestMapping(value = "backToList")
	public String backToList(HttpServletRequest request, StoEscortInfo stoEscortInfo, Model model,
			RedirectAttributes redirectAttributes) {
		// 文件保存目录路径
		String savePath = request.getSession().getServletContext().getRealPath("/")
				+ Constant.ImageSeting.IMAGE_BASE_PATH + File.separatorChar + Constant.ImageSeting.IMAGE_DIR_NAME
				+ File.separatorChar;
		String fileFullName = savePath + stoEscortInfo.getTmpPicFileName();

		// 删除原始图片
		FileUtils.delFile(fileFullName);

		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 删除 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(StoEscortInfo stoEscortInfo, RedirectAttributes redirectAttributes) {

		String message = "";
		Locale locale = LocaleContextHolder.getLocale();

		try {
			if (Constant.Escort.BINDING_ROUTE.equals(stoEscortInfo.getBindingRoute())) {
				message = msg.getMessage("message.E1065", new Object[] { stoEscortInfo.getId() }, locale);
			} else {
				stoEscortInfoService.delete(stoEscortInfo);
				message = msg.getMessage("message.I1005", new Object[] { stoEscortInfo.getId() }, locale);
			}

		} catch (ServiceException se) {
			message = msg.getMessage("message.E1018", new Object[] { stoEscortInfo.getId() }, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";

	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 在“添加用户信息”画面查询用户列表 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:view")
	@RequestMapping(value = { "createUser" })
	public String createUserList(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = new Page<User>(request, response);
		page = systemService.findUser(page, user);
		model.addAttribute("page", page);
		return "modules/store/v01/stoEscortInfo/stoEscortInfoUserList";
	}

	// /**
	// * @author niguoyong
	// * @date 2015-09-06
	// *
	// * @Description 在“添加用户信息”画面添加押运人员
	// * @param 参数
	// * @return
	// * @throws
	// */
	// @RequiresPermissions("store:stoEscortInfo:edit")
	// @RequestMapping(value = "saveUser")
	// public String saveUser(String[] userId, Model model, RedirectAttributes
	// redirectAttributes) {
	// String message = "";
	// Locale locale = LocaleContextHolder.getLocale();
	//
	// try {
	// stoEscortInfoService.saveEscortInfo(userId);
	// message = msg.getMessage("message.I1006", null, locale);
	// } catch (ServiceException se) {
	// message = msg.getMessage("message.E1020", null, locale);
	// }
	// addMessage(redirectAttributes, message);
	// return "redirect:" + Global.getAdminPath() +
	// "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	// }

	/**
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 *       验证身份证号信息是否存在
	 * 
	 * @param id
	 * @param oldIdcardNo
	 * @param idcardNo
	 * @return
	 */
	@ResponseBody
	//@RequiresPermissions("store:stoEscortInfo:edit")
	@RequestMapping(value = "checkIdcardNo")
	public String checkIdcardNo(String id, String oldIdcardNo, String idcardNo) {
		if (StringUtils.isBlank(oldIdcardNo) && StringUtils.isBlank(idcardNo)) {
			return "true";
		}
		// 身份信息无变化
		if (oldIdcardNo.equals(idcardNo)) {
			return "true";
		}

		if (StringUtils.isNotBlank(id)) {
			// 变更：取得旧身份信息
			StoEscortInfo stoEscortInfo = stoEscortInfoService.checkIdcardNo(oldIdcardNo);
			// 判断当前身份信息是否存在
			if (stoEscortInfo == null) {
				return "true";
			}
			// 身份信息未采集
			if (StoEscortInfo.DEL_FLAG_NORMAL.equals(stoEscortInfo.getDelFlag())) {
				// 变更：取得新身份信息
				stoEscortInfo = stoEscortInfoService.checkIdcardNo(idcardNo);
				// 判断身份信息是否存在
				if (stoEscortInfo == null) {
					return "true";
				}
			}
		} else {
			// 创建：取得身份信息
			StoEscortInfo stoEscortInfo = stoEscortInfoService.checkIdcardNo(idcardNo);
			// 判断身份信息是否存在
			if (stoEscortInfo == null) {
				return "true";
			}
		}

		return "false";
	}

	/**
	 * 
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "showImage")
	public void createImage(@RequestParam(required = false) String id, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		InputStream inputStream = null;
		byte[] imageBytes = null;
		StoEscortInfo stoEscortInfo = stoEscortInfoService.get(id);
		try {
			if (stoEscortInfo != null) {
				imageBytes = stoEscortInfo.getPhoto();
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
	 * 上传图片上传
	 * 
	 * @param stoEscortInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toUploadImagePage")
	public String toUploadImagePage(StoEscortInfo stoEscortInfo, Model model) {
		model.addAttribute("stoEscortInfo", stoEscortInfo);
		return "modules/store/v01/stoEscortInfo/ajaxFileUpload2";
	}

	/**
	 * 上传图片
	 * 
	 * @param request
	 * @param imageFile
	 * @param stoEscortInfo
	 * @return
	 */
	@RequestMapping(value = "uploadImage")
	@ResponseBody
	public String uploadImage(HttpServletRequest request,
			@RequestParam(value = "fileToUpload", required = true) MultipartFile imageFile,
			StoEscortInfo stoEscortInfo) {

		Map<String, String> rtnMap = new HashMap<String, String>();
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();

		String picFileName = imageFile.getOriginalFilename();
		// 取得文件扩展名
		String strExtentions = picFileName.substring(picFileName.lastIndexOf(Constant.ImageSeting.FILE_DOT));

		Random random = new Random();

		// 使用人员ID替换文件名
		picFileName = stoEscortInfo.getId() + random.nextInt(1000) + strExtentions;

		String basePath = request.getSession().getServletContext().getRealPath("/")
				+ Constant.ImageSeting.IMAGE_BASE_PATH;
		// 文件保存目录路径
		String savePath = basePath + File.separatorChar + Constant.ImageSeting.IMAGE_DIR_NAME;
		// 文件保存目录URL
		String picUrl = request.getContextPath() + "/" + Constant.ImageSeting.IMAGE_BASE_PATH + "/"
				+ Constant.ImageSeting.IMAGE_DIR_NAME + "/" + picFileName;

		String fileFullName = savePath + File.separatorChar + picFileName;

		try {
			FileUtils.deleteDirectory(savePath);
			FileUtils.createDirectory(savePath);

			File toFile = new File(fileFullName);
			imageFile.transferTo(toFile);
			BufferedImage buff = ImageIO.read(toFile);

			String strWidth = Global.getConfig("image.setting.weight");
			String strHeight = Global.getConfig("image.setting.height");
			int iWidth = StringUtils.isNotBlank(strWidth) ? Integer.parseInt(strWidth) : 150;
			int iHeight = StringUtils.isNotBlank(strHeight) ? Integer.parseInt(strHeight) : 205;

			// 判断图片大小
			if (buff.getWidth() < iWidth || buff.getHeight() < iHeight) {
				message = msg.getMessage("message.E1053",
						new String[] { Integer.toString(iWidth), Integer.toString(iHeight) }, locale);
				rtnMap.put(Constant.ImageSeting.RTN_STATUS_FLG_KEY, Constant.ImageSeting.RTN_FLG_FAILED_KEY);
				rtnMap.put(Constant.ImageSeting.MSG_KEY, message);
				FileUtils.deleteFile(fileFullName);
				return gson.toJson(rtnMap);
			}

			rtnMap.put(Constant.ImageSeting.PIC_URL_KEY, picUrl);
			rtnMap.put(Constant.ImageSeting.RTN_STATUS_FLG_KEY, Constant.ImageSeting.RTN_FLG_SUCCESS_KEY);
			rtnMap.put(Constant.ImageSeting.PIC_FILE_NAME, picFileName);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// [上传失败]图片上传失败！
			message = msg.getMessage("message.E1045", null, locale);
			rtnMap.put(Constant.ImageSeting.RTN_STATUS_FLG_KEY, Constant.ImageSeting.RTN_FLG_FAILED_KEY);
			rtnMap.put(Constant.ImageSeting.MSG_KEY, message);
		} catch (IOException e) {
			e.printStackTrace();
			// [上传失败]图片上传失败！
			message = msg.getMessage("message.E1045", null, locale);
			rtnMap.put(Constant.ImageSeting.RTN_STATUS_FLG_KEY, Constant.ImageSeting.RTN_FLG_FAILED_KEY);
			rtnMap.put(Constant.ImageSeting.MSG_KEY, message);
		}

		return gson.toJson(rtnMap);
	}

	/**
	 * 压缩图片
	 * 
	 * @param request
	 * @param stoEscortInfo
	 * @param redirectAttributes
	 * @return 图片剪裁保存成功会后跳转到列表页面
	 */
	@RequestMapping(value = "cutAndSaveImage", method = RequestMethod.POST)
	public String cutAndSaveImage(HttpServletRequest request, StoEscortInfo stoEscortInfo,
			RedirectAttributes redirectAttributes, Model model) {
		logger.debug("---------------压缩图片（cutAndSaveImage）开始");
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		String savePath = request.getSession().getServletContext().getRealPath("/")
				+ Constant.ImageSeting.IMAGE_BASE_PATH + File.separatorChar + Constant.ImageSeting.IMAGE_DIR_NAME
				+ File.separatorChar;
		String fileFullName = savePath + stoEscortInfo.getTmpPicFileName();

		File localImageFile = new File(fileFullName);
		if (!localImageFile.exists()) {
			message = msg.getMessage("message.E1046", null, locale);
			addMessage(model, message);
			stoEscortInfo.setPicPath("");
			stoEscortInfo.setTmpPicFileName("");
			return toUploadImagePage(stoEscortInfo, model);
		}

		String x = stoEscortInfo.getX();
		String y = stoEscortInfo.getY();
		String w = stoEscortInfo.getW();
		String h = stoEscortInfo.getH();
		logger.debug("---------------x：" + x);
		logger.debug("---------------y：" + y);
		logger.debug("---------------w：" + w);
		logger.debug("---------------h：" + h);

		if (StringUtils.isBlank(x) || StringUtils.isBlank(y) || StringUtils.isBlank(w) || StringUtils.isBlank(h)) {
			// 请在图片上选择剪裁区域！
			message = msg.getMessage("message.E1047", null, locale);
			addMessage(model, message);
			return toUploadImagePage(stoEscortInfo, model);
		}
		int iX = Double.valueOf(x).intValue();
		int iY = Double.valueOf(y).intValue();
		int iW = Double.valueOf(w).intValue();
		int iH = Double.valueOf(h).intValue();

		if (iW == 0 || iH == 0) {
			// 请在图片上选择剪裁区域！
			message = msg.getMessage("message.E1047", null, locale);
			addMessage(model, message);
			return toUploadImagePage(stoEscortInfo, model);
		}

		byte[] photoImg = CommonUtils.shrinkPhotoByUploadFile(fileFullName, Constant.ImageSeting.IMAGE_TYPE, iX, iY, iW,
				iH);

		if (photoImg != null) {
			logger.debug("---------------压缩后图片字节长度：" + photoImg.length);

			stoEscortInfo.setPhoto(photoImg);
			// 保存
			stoEscortInfoService.save(stoEscortInfo);

			// 删除原始图片
			FileUtils.delFile(fileFullName);
			// [保存成功]：图片保存成功!
			message = msg.getMessage("message.I1018", null, locale);
		} else {
			// 删除原始图片
			FileUtils.delFile(fileFullName);
			// [保存失败]图片保存失败，请重新选择图片然后上传！
			message = msg.getMessage("message.E1044", null, locale);
			addMessage(model, message);
			return toUploadImagePage(stoEscortInfo, model);
		}

		addMessage(redirectAttributes, message);
		logger.debug("---------------压缩图片（cutAndSaveImage）结束");
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	}

	
	/**
	 * 上传图片
	 * 
	 * @param request
	 * @param imageFile
	 * @param stoEscortInfo
	 * @return
	 */
	@RequestMapping(value = "uploadAndSaveImage")
	public String uploadAndSaveImage(HttpServletRequest request,
			@RequestParam(value = "fileToUpload", required = true) MultipartFile imageFile,
			StoEscortInfo stoEscortInfo, RedirectAttributes redirectAttributes, Model model) {

		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		
		logger.debug("---------------压缩保存图片（uploadAndSaveImage）开始");
		
		long fileSize = imageFile.getSize();
		String maxSize = Global.getConfig("image.max.size");
		long uploadFileVaildSize = StringUtils.isNotBlank(maxSize) ? Long.parseLong(maxSize) : 1024l * 1024l;
		if (fileSize > uploadFileVaildSize) {
			// message.E1069=[上传失败]图片上传失败，请上传小于1M大小的图片！
			long mSize = fileSize / 1024l;
			message = msg.getMessage("message.E1069", new String[] {Long.toString(mSize)}, locale);
			addMessage(model, message);
			return toUploadImagePage(stoEscortInfo, model);
		}
		
		byte[] photoImg;
		try {
			if (!imageFile.isEmpty()) {
				photoImg = CommonUtils.shrinkPhotoByUploadFileIO(imageFile.getInputStream(),
						Constant.ImageSeting.IMAGE_TYPE);
				if (photoImg != null) {
					logger.debug("---------------压缩后图片字节长度：" + photoImg.length);

					stoEscortInfo.setPhoto(photoImg);
					// 保存
					stoEscortInfoService.save(stoEscortInfo);

					// [保存成功]：图片保存成功!
					message = msg.getMessage("message.I1018", null, locale);
				} else {
					// [保存失败]图片保存失败，请重新选择图片然后上传！
					message = msg.getMessage("message.E1044", null, locale);
					addMessage(model, message);
					return toUploadImagePage(stoEscortInfo, model);
				}
			} else {
				// [上传失败]上传图片不能为空
				message = msg.getMessage("message.E1072", null, locale);
				addMessage(model, message);
				return toUploadImagePage(stoEscortInfo, model);
			}
		} catch (BusinessException e) {
			// [保存失败]图片保存失败，请重新选择图片然后上传！
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			addMessage(model, message);
			return toUploadImagePage(stoEscortInfo, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addMessage(redirectAttributes, message);
		logger.debug("---------------压缩保存图片（cutAndSaveImage）结束");
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEscortInfo/list?isSearch=true&repage";
	}
	/**
	 * @author xp @date 2017-8-2
	 * 
	 * @Description 跳转到人员查看画面 @param 参数 @return @throws
	 */
	@RequiresPermissions("store:stoEscortInfo:view")
	@RequestMapping(value = "view")
	public String view(StoEscortInfo stoEscortInfo, Model model) {
		model.addAttribute("stoEscortInfo", stoEscortInfo);
		return "modules/store/v01/stoEscortInfo/stoEscortInfoView";
	}
}
