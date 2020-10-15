package com.coffer.businesses.modules.doorOrder.v01.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.BackAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.CompanyAccountsMainService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.FileUtils;
import com.coffer.core.common.utils.StreamUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 回款管理Controller
 * 
 * @author XL
 * @version 2019-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/backAccountsMain")
public class BackAccountsMainController extends BaseController {

	@Autowired
	private BackAccountsMainService backAccountsMainService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private CompanyAccountsMainService companyAccountsMainService;

	@ModelAttribute
	public BackAccountsMain get(@RequestParam(required = false) String id) {
		BackAccountsMain entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = backAccountsMainService.get(id);
		}
		if (entity == null) {
			entity = new BackAccountsMain();
		}
		return entity;
	}

	@RequiresPermissions("doororder.v01:backAccountsMain:view")
	@RequestMapping(value = { "list", "" })
	public String list(BackAccountsMain backAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 查询条件： 开始时间
		if (backAccountsMain.getCreateTimeStart() != null) {
			backAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(backAccountsMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (backAccountsMain.getCreateTimeEnd() != null) {
			backAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(backAccountsMain.getCreateTimeEnd())));
		}
		/* end */
		// 数据过滤
		User userInfo = UserUtils.getUser();
		backAccountsMain.setOfficeId(userInfo.getOffice().getId());
		backAccountsMain.getSqlMap().put("dsf",
				"OR o6.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		Page<BackAccountsMain> page = backAccountsMainService.findPage(new Page<BackAccountsMain>(request, response),
				backAccountsMain);
		model.addAttribute("page", page);
		// 获取公司待回款金额
		BigDecimal companyNotBackAmount = companyAccountsMainService
				.getCompanyNotBackAmount(officeService.getPlatform().get(0).getId());
		model.addAttribute("companyNotBackAmount", companyNotBackAmount);
		return "modules/doorOrder/v01/backAccountsMain/backAccountsMainList";
	}

	@RequiresPermissions("doororder.v01:backAccountsMain:view")
	@RequestMapping(value = "form")
	public String form(BackAccountsMain backAccountsMain, Model model) {
		model.addAttribute("backAccountsMain", backAccountsMain);
		return "modules/doorOrder/v01/backAccountsMain/backAccountsMainForm";
	}

	@RequiresPermissions("doororder.v01:backAccountsMain:edit")
	@RequestMapping(value = "save")
	public synchronized String save(BackAccountsMain backAccountsMain, Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileToUpload", required = true) MultipartFile imageFile) {

		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		logger.debug("---------------压缩保存图片（uploadAndSaveImage）开始");
		long fileSize = imageFile.getSize();
		String maxSize = Global.getConfig("image.max.size");
		long uploadFileVaildSize = StringUtils.isNotBlank(maxSize) ? Long.parseLong(maxSize) : 1024l * 10240l;
		if (fileSize > uploadFileVaildSize) {
			// message.E1069=[上传失败]图片上传失败，请上传小于10M大小的图片！
			long mSize = fileSize / 1024l;
			message = msg.getMessage("message.E1069", new String[] { Long.toString(mSize) }, locale);
			addMessage(model, message);
			return toUploadImagePage(backAccountsMain, model);
		}

		byte[] photoImg;
		try {
			if (!imageFile.isEmpty()) {
				photoImg = CommonUtils.shrinkPhotoByUploadFileIO(imageFile.getInputStream(),
						Constant.ImageSeting.IMAGE_TYPE);
				if (photoImg != null) {
					logger.debug("---------------压缩后图片字节长度：" + photoImg.length);
					backAccountsMain.setPhoto(photoImg);
					// [保存成功]：图片保存成功!
					// message = msg.getMessage("message.I1018", null, locale);
				} else {
					// [保存失败]图片保存失败，请重新选择图片然后上传！
					message = msg.getMessage("message.E1044", null, locale);
					addMessage(redirectAttributes, message);
					return toUploadImagePage(backAccountsMain, model);
				}
			} else {
				// [上传失败]上传图片不能为空
				message = msg.getMessage("message.E1072", null, locale);
				addMessage(redirectAttributes, message);
				return toUploadImagePage(backAccountsMain, model);
			}
		} catch (BusinessException e) {
			// [保存失败]图片保存失败，请重新选择图片然后上传！
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return toUploadImagePage(backAccountsMain, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("---------------压缩保存图片（cutAndSaveImage）结束");
		if (!beanValidator(model, backAccountsMain)) {
			return form(backAccountsMain, model);
		}
		// 设置清分机构ID及名称
		backAccountsMain.setCustNo(UserUtils.getUser().getOffice().getId());
		backAccountsMain.setCustName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		// 设置账务发生机构及归属机构ID
		backAccountsMain.setOfficeId(UserUtils.getUser().getOffice().getId());
		backAccountsMain.setCompanyId(backAccountsMain.getCompanyId());
		// 设置账务发生机构名称及归属机构名称
		backAccountsMain
				.setOfficeName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		backAccountsMain.setCompanyName(StoreCommonUtils.getOfficeById(backAccountsMain.getCompanyId()).getName());
		try {
			backAccountsMainService.save(backAccountsMain);
			addMessage(redirectAttributes, "保存回款信息成功!");
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/?repage";
	}

	@RequiresPermissions("doororder.v01:backAccountsMain:edit")
	@RequestMapping(value = "delete")
	public String delete(BackAccountsMain backAccountsMain, RedirectAttributes redirectAttributes) {
		backAccountsMainService.delete(backAccountsMain);
		addMessage(redirectAttributes, "删除回款信息成功!");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/?repage";
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
		BackAccountsMain backAccountsMain = backAccountsMainService.get(id);
		try {
			if (backAccountsMain == null) {
				// 将文件转成byte[]
				inputStream = request.getSession().getServletContext()
						.getResourceAsStream(Global.getConfig("image.escort.path"));
				if (inputStream == null) {
					throw new IOException();
				}
				imageBytes = StreamUtils.InputStreamTOByte(inputStream);
			} else {
				imageBytes = backAccountsMain.getPhoto();
				if (imageBytes == null) {
					// 将文件转成byte[]
					inputStream = request.getSession().getServletContext()
							.getResourceAsStream(Global.getConfig("image.escort.path"));
					if (inputStream == null) {
						throw new IOException();
					}
					imageBytes = StreamUtils.InputStreamTOByte(inputStream);
				}
			}
			out.write(imageBytes);
			out.flush();
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
	 * 上传失败跳转回首页
	 * 
	 * @param backAccountsMain
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toUploadImagePage")
	public String toUploadImagePage(BackAccountsMain backAccountsMain, Model model) {
		model.addAttribute("backAccountsMain", backAccountsMain);
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/?repage";
	}

	/**
	 * 上传图片
	 * 
	 * @param request
	 * @param imageFile
	 * @param backAccountsMain
	 * @return
	 */
	@RequestMapping(value = "uploadImage")
	@ResponseBody
	public String uploadImage(HttpServletRequest request,
			@RequestParam(value = "fileToUpload", required = true) MultipartFile imageFile,
			BackAccountsMain backAccountsMain) {

		Map<String, String> rtnMap = new HashMap<String, String>();
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();

		String picFileName = imageFile.getOriginalFilename();
		// 取得文件扩展名
		String strExtentions = picFileName.substring(picFileName.lastIndexOf(Constant.ImageSeting.FILE_DOT));

		Random random = new Random();

		// 使用人员ID替换文件名
		picFileName = backAccountsMain.getId() + random.nextInt(1000) + strExtentions;

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
	 * @param backAccountsMain
	 * @param redirectAttributes
	 * @return 图片剪裁保存成功会后跳转到列表页面
	 */
	@RequestMapping(value = "cutAndSaveImage", method = RequestMethod.POST)
	public String cutAndSaveImage(HttpServletRequest request, BackAccountsMain backAccountsMain,
			RedirectAttributes redirectAttributes, Model model) {
		logger.debug("---------------压缩图片（cutAndSaveImage）开始");
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		String savePath = request.getSession().getServletContext().getRealPath("/")
				+ Constant.ImageSeting.IMAGE_BASE_PATH + File.separatorChar + Constant.ImageSeting.IMAGE_DIR_NAME
				+ File.separatorChar;
		String fileFullName = savePath + backAccountsMain.getTmpPicFileName();

		File localImageFile = new File(fileFullName);
		if (!localImageFile.exists()) {
			message = msg.getMessage("message.E1046", null, locale);
			addMessage(model, message);
			backAccountsMain.setPicPath("");
			backAccountsMain.setTmpPicFileName("");
			return toUploadImagePage(backAccountsMain, model);
		}

		String x = backAccountsMain.getX();
		String y = backAccountsMain.getY();
		String w = backAccountsMain.getW();
		String h = backAccountsMain.getH();
		logger.debug("---------------x：" + x);
		logger.debug("---------------y：" + y);
		logger.debug("---------------w：" + w);
		logger.debug("---------------h：" + h);

		if (StringUtils.isBlank(x) || StringUtils.isBlank(y) || StringUtils.isBlank(w) || StringUtils.isBlank(h)) {
			// 请在图片上选择剪裁区域！
			message = msg.getMessage("message.E1047", null, locale);
			addMessage(model, message);
			return toUploadImagePage(backAccountsMain, model);
		}
		int iX = Double.valueOf(x).intValue();
		int iY = Double.valueOf(y).intValue();
		int iW = Double.valueOf(w).intValue();
		int iH = Double.valueOf(h).intValue();

		if (iW == 0 || iH == 0) {
			// 请在图片上选择剪裁区域！
			message = msg.getMessage("message.E1047", null, locale);
			addMessage(model, message);
			return toUploadImagePage(backAccountsMain, model);
		}

		byte[] photoImg = CommonUtils.shrinkPhotoByUploadFile(fileFullName, Constant.ImageSeting.IMAGE_TYPE, iX, iY, iW,
				iH);

		if (photoImg != null) {
			logger.debug("---------------压缩后图片字节长度：" + photoImg.length);

			backAccountsMain.setPhoto(photoImg);
			// 保存
			backAccountsMainService.save(backAccountsMain);

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
			return toUploadImagePage(backAccountsMain, model);
		}

		addMessage(redirectAttributes, message);
		logger.debug("---------------压缩图片（cutAndSaveImage）结束");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/list?isSearch=true&repage";
	}

	/**
	 * 冲正处理
	 *
	 * @author WQJ
	 * @version 2019年7月8日
	 * @param doorOrderInfo
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("doororder.v01:backAccountsMain:edit")
	@RequestMapping(value = "reverse")
	public String reverse(BackAccountsMain backAccountsMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 设置状态
			backAccountsMain.setStatus(DoorOrderConstant.StatusType.DELETE);
			// 冲正处理
			backAccountsMainService.reverse(backAccountsMain);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/list?repage";
		}
		// 提示信息
		message = msg.getMessage("message.E7217", new String[] { backAccountsMain.getBusinessId() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/list?repage";
	}

	/**
	 * 返回
	 * 
	 * @author wqj
	 * @version 2019-07-09
	 */
	@RequestMapping(value = "back")
	public String back(BackAccountsMain backAccountsMain, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/backAccountsMain/list?isSearch=true&repage";
	}

	/**
	 * 回款单号列表
	 * 
	 * @author gzd
	 * @version 2019年11月29日
	 * @param door_id
	 * @return
	 */
	@RequestMapping(value = "getOrderId")
	@ResponseBody
	public String getOrderId(String doorId) {
		// 返回内容
		List<BackAccountsMain> resultMap = backAccountsMainService.getOrderId(doorId);
		// 回款单号列表
		if(resultMap == null) {
			resultMap = new ArrayList<BackAccountsMain>();
		}
		return gson.toJson(resultMap);
	}
}