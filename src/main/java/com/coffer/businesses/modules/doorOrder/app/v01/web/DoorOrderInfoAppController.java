package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.app.v01.service.DoorOrderInfoAppService;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositAmount;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositInfo;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;

/**
 * 门店存款Controller
 * 
 * @author lihe
 * @version 2019-08-14
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/doorOrderInfo")
public class DoorOrderInfoAppController extends BaseController {

	protected static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm")// 时间转化为特定格式
			.create();

	@Autowired
	private DoorOrderInfoAppService doorOrderInfoAppService;

	@Autowired
	private OfficeService officeService;

	@RequestMapping(value = "getAmountAndCount", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getAmountAndCount(@RequestParam(value = "userId") String userId) {
		Map<String, Object> jsonData = Maps.newHashMap();
		if (StringUtils.isBlank(userId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户编号不能为空");
			return gson.toJson(jsonData);
		}
		User user = UserUtils.get(userId);
		if ("{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			jsonData = doorOrderInfoAppService.getAmountAndCount(user);
		}
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: getClientInfoForCenter
	 * <p>
	 * Description: 根据当前登录人获取存款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getClientInfoForCenter", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getClientInfoForCenter(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		if (StringUtils.isBlank(userId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户编号不能为空");
			return gson.toJson(jsonData);
		}
		Page<DepositAmount> result = new Page<DepositAmount>(pageNo, pageSize);
		DepositAmount depositAmount = new DepositAmount();
		depositAmount.setPage(result);
		// 获取用户信息
		User user = UserUtils.get(userId);
		// 判断用户是否存在及所属机构是否存在
		if ("{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			depositAmount.setCurrentUser(user);
			result.setList(doorOrderInfoAppService.getClientInfoForCenter(depositAmount, user.getOffice()));
			jsonData.put(Parameter.RESULT, result);
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		}
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: getDoorInfoByMerchantId
	 * <p>
	 * Description: 根据商户获取门店存款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param merchantId
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getDoorInfoByMerchantId", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getDoorInfoByMerchantId(@RequestParam(value = "merchantId") String merchantId,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		Office office = new Office();
		office.setId(merchantId);
		if (null == officeService.get(office)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + merchantId + "的数据");
			return gson.toJson(jsonData);
		}
		Page<DepositAmount> result = new Page<DepositAmount>(pageNo, pageSize);
		DepositAmount depositAmount = new DepositAmount();
		depositAmount.setPage(result);
		depositAmount.setMerchantId(merchantId);
		if (StringUtils.isNotBlank(userId)) {
			User user = UserUtils.get(userId);
			if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
				return gson.toJson(jsonData);
			}
			if (null != user && StringUtils.isNotBlank(user.getOffice().getType())
					&& Constant.OfficeType.STORE.equals(user.getOffice().getType())) {
				depositAmount.setDoorId(user.getOffice().getId());
			}
		}
		result.setList(doorOrderInfoAppService.getDoorInfoByMerchantId(depositAmount, UserUtils.get(userId)));
		jsonData.put(Parameter.RESULT, result);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: getOrderInfoByDoorId
	 * <p>
	 * Description: 根据门店编号获取存款信息
	 * </p>
	 * 
	 * @author: lihe
	 * @param doorId
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getOrderInfoByDoorId", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getOrderInfoByDoorId(@RequestParam(value = "doorId") String doorId,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		Office office = new Office();
		office.setId(doorId);
		if (null == officeService.get(office)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + doorId + "的数据");
			return gson.toJson(jsonData);
		}
		if (StringUtils.isNotBlank(userId)) {
			User user = UserUtils.get(userId);
			if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
				return gson.toJson(jsonData);
			}
		}
		Page<DepositInfo> result = new Page<DepositInfo>(pageNo, pageSize);
		DepositInfo depositInfo = new DepositInfo();
		depositInfo.setPage(result);
		depositInfo.setDoorId(doorId);
		result.setList(doorOrderInfoAppService.getOrderInfoByDoorId(depositInfo, UserUtils.get(userId)));
		jsonData.put(Parameter.RESULT, result);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 *
	 * Title: getOrderListByUser
	 * <p>
	 * Description: 根据登录用户获取存款列表
	 * </p>
	 *
	 * @author: lihe
	 * @param userId
	 * @param createTimeStart
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getOrderDetailList", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getOrderListByUser(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "doorId") String doorId, @RequestParam(value = "orderId") String orderId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		if (StringUtils.isBlank(userId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户编号不能为空");
			return gson.toJson(jsonData);
		}
		if (StringUtils.isBlank(doorId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "门店编号不能为空");
			return gson.toJson(jsonData);
		}
		User user = UserUtils.get(userId);
		if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			return gson.toJson(jsonData);
		}
		Page<DepositInfo> result = new Page<DepositInfo>(pageNo, pageSize);
		DepositInfo depositInfo = new DepositInfo();
		if (StringUtils.isNotBlank(createTimeStart)) {
			Date date = new Date();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.parse(createTimeStart);
			} catch (ParseException e) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				jsonData.put(Parameter.ERROR_MSG_KEY, "时间格式有误");
				return gson.toJson(jsonData);
			}
			// 查询条件： 开始时间
			depositInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			// 查询条件： 结束时间
			depositInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
		}
		depositInfo.setPage(result);
		depositInfo.setCreateBy(user);
		depositInfo.setCurrentUser(user);
		depositInfo.setDoorId(doorId);
		depositInfo.setOrderId(orderId);
		result.setList(doorOrderInfoAppService.getOrderInfoByDoorId(depositInfo, user));
		jsonData.put(Parameter.RESULT, result);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: getDoorListByUser
	 * <p>
	 * Description: 根据用户获取缴存门店列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param userId
	 * @param createTimeStart
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getDoorListByUser", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getDoorListByUser(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		if (StringUtils.isBlank(userId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户编号不能为空");
			return gson.toJson(jsonData);
		}
		User user = UserUtils.get(userId);
		if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			return gson.toJson(jsonData);
		}
		Page<DoorOrderInfo> result = new Page<DoorOrderInfo>(pageNo, pageSize);
		DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
		// 时间校验
		if (StringUtils.isNotBlank(createTimeStart)) {
			Date date = new Date();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.parse(createTimeStart);
			} catch (ParseException e) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				jsonData.put(Parameter.ERROR_MSG_KEY, "时间格式有误");
				return gson.toJson(jsonData);
			}
			// 查询条件： 开始时间
			doorOrderInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			// 查询条件： 结束时间
			doorOrderInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
		}
		doorOrderInfo.setCreateBy(user);
		doorOrderInfo.setCurrentUser(user);
		doorOrderInfo.setPage(result);
		result.setList(doorOrderInfoAppService.getDoorListByUser(doorOrderInfo, user, pageSize));
		jsonData.put(Parameter.RESULT, result);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: checkParam
	 * <p>
	 * Description: 验证参数
	 * </p>
	 * 
	 * @author: lihe
	 * @param jsonData
	 * @param user
	 * @return Map<String,Object> 返回类型
	 */
	private Map<String, Object> checkParam(Map<String, Object> jsonData, User user) {
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
			return jsonData;
		}
		return jsonData;
	}

	// /**
	// *
	// * 凭条图片上传接口
	// *
	// * @author XL
	// * @version 2019年8月28日
	// * @param file
	// * @param tickerNo
	// * @param orderId
	// * @return
	// */
	// @RequestMapping(value = "/saveTickerPicInfo", produces =
	// "application/json; charset=utf-8")
	// @ResponseBody
	// public String saveTickerPicInfo(HttpServletRequest request,
	// @RequestParam(value = "file") MultipartFile file,
	// @RequestParam(value = "tickerNo") String tickerNo, @RequestParam(value =
	// "orderId") String orderId,
	// @RequestParam(value = "userId") String userId) {
	// // 返回内容
	// Map<String, Object> jsonData = Maps.newHashMap();
	// // -----------------验证参数-----------------
	// if (StringUtils.isBlank(tickerNo)) {
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，凭条号不能为空！");
	// return gson.toJson(jsonData);
	// }
	// if (StringUtils.isBlank(orderId)) {
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，存款单号不能为空！");
	// return gson.toJson(jsonData);
	// }
	// if (file == null) {
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，图片不存在！");
	// return gson.toJson(jsonData);
	// }
	// User user = UserUtils.get(userId);
	// if (user == null) {
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，用户不存在！");
	// return gson.toJson(jsonData);
	// }
	// // -----------------上传图片-----------------
	// File targetFile = null;
	// try {
	// // 目标文件
	// targetFile = new
	// File(request.getSession().getServletContext().getRealPath(""),
	// file.getOriginalFilename());
	// // 上传
	// file.transferTo(targetFile);
	// } catch (Exception e) {
	// e.printStackTrace();
	// // 图片上传失败，请重新选择图片然后上传！！
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
	// return gson.toJson(jsonData);
	// }
	// // -----------------二维码解码-----------------
	// BufferedImage image = null;
	// Result results = null;
	// try {
	// image = ImageIO.read(targetFile);
	// LuminanceSource source = new BufferedImageLuminanceSource(image);
	// BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	// Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType,
	// Object>();
	// hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	// results = new MultiFormatReader().decode(bitmap, hints);
	// // if (!tickerNo.equals(results.getText())) {
	// // // 二维码内容与凭条号不符，请重新选择图片！
	// // jsonData.put(Parameter.RESULT_FLAG_KEY,
	// // ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// // jsonData.put(Parameter.ERROR_NO_KEY,
	// // ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// // jsonData.put(Parameter.ERROR_MSG_KEY, "二维码内容与凭条号不符，请重新选择图片！");
	// // return gson.toJson(jsonData);
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// // 二维码解析失败，请重新选择二维码图片！
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "二维码解析失败，请重新选择二维码图片！");
	// return gson.toJson(jsonData);
	// } finally {
	// // 删除本地图片
	// File delFile = targetFile;
	// if (delFile.exists() && delFile.isFile()) {
	// delFile.delete();
	// }
	// }
	// // -----------------保存二维码图片-----------------
	// // 图片压缩
	// byte[] photoImg;
	// try {
	// if (!file.isEmpty()) {
	// photoImg = CommonUtils.photoShrink(file);
	// if (photoImg == null) {
	// // [保存失败]图片保存失败，请重新选择图片然后上传！
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
	// } else {
	// jsonData = doorOrderInfoAppService.checkAndSaveTicker(orderId, tickerNo,
	// photoImg, user);
	// }
	// } else {
	// // [上传失败]上传图片不能为空
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，上传图片不能为空！");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// // [保存失败]图片保存失败，请重新选择图片然后上传！
	// jsonData.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// jsonData.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
	// }
	// return gson.toJson(jsonData);
	// }
	//
	// /**
	// * 显示凭条图片
	// *
	// * @author XL
	// * @version 2019年8月29日
	// * @param tickerNo
	// * @param orderId
	// * @param request
	// * @param response
	// * @throws IOException
	// */
	// @RequestMapping(value = "showImage")
	// public void showImage(@RequestParam(value = "tickerNo", required = false)
	// String tickerNo,
	// @RequestParam(value = "orderId", required = false) String orderId,
	// HttpServletRequest request,
	// HttpServletResponse response) throws IOException {
	// // 查询凭条信息
	// DoorOrderDetail doorOrderDetail =
	// doorOrderInfoAppService.getDoorOrderDetailPhoto(orderId, tickerNo);
	// DoorCommonUtils.showImage(doorOrderDetail, "photo", request, response);
	// }

	/**
	 * 
	 * 凭条图片上传接口
	 * 
	 * @author XL
	 * @version 2019年8月28日
	 * @param file
	 * @param tickerNo
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/saveTickerPicInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String saveTickerPicInfo(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "tickerNo") String tickerNo, @RequestParam(value = "orderId") String orderId,
			@RequestParam(value = "userId") String userId, @RequestParam(value = "doorId") String doorId) {
		// 返回内容
		Map<String, Object> jsonData = Maps.newHashMap();
		// -----------------验证参数-----------------
		if (StringUtils.isBlank(tickerNo)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，凭条号不能为空！");
			return gson.toJson(jsonData);
		}
		if (StringUtils.isBlank(orderId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，存款单号不能为空！");
			return gson.toJson(jsonData);
		}
		if (file == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，图片不存在！");
			return gson.toJson(jsonData);
		}
		User user = UserUtils.get(userId);
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，用户不存在！");
			return gson.toJson(jsonData);
		}
		// -----------------上传图片-----------------
		File targetFile = null;
		try {
			// 保存路径
			// String savePath =
			// request.getSession().getServletContext().getRealPath("/doorOrderImg"
			// + "/" + doorId);
			String savePath = Global.getConfig("miniProgram.saveImgUrl") + "/doorOrderImg/" + doorId;
			File f = new File(savePath);
			// img文件夹是否存在
			if (!f.exists()) {
				f.mkdirs();
			}
			String originalFilename = file.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			String fileName = tickerNo + "-" + orderId + suffix;
			targetFile = new File(savePath, fileName);
			file.transferTo(targetFile);
			// BufferedInputStream in = new BufferedInputStream(new
			// FileInputStream(savePath + "//" +fileName));
			// //字节流转图片对象
			// Image bi = ImageIO.read(in);
			// //获取图像的高度，宽度
			// int height = bi.getHeight(null);
			// int width = bi.getWidth(null);
			// //构建图片流
			// BufferedImage tag = new BufferedImage(width / 10, height / 10,
			// BufferedImage.TYPE_INT_RGB);
			// //绘制改变尺寸后的图
			// tag.getGraphics().drawImage(bi, 0, 0,width / 10, height / 10,
			// null);
			// //输出流
			// BufferedOutputStream out = new BufferedOutputStream(new
			// FileOutputStream(savePath + "//" +fileName));
			// ImageIO.write(tag, suffix, out);
			// in.close();
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();
			// 图片上传失败，请重新选择图片然后上传！！
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
			return gson.toJson(jsonData);
		}
		// -----------------二维码解码-----------------
		BufferedImage image = null;
		Result results = null;
		try {
			// image = ImageIO.read(targetFile);
			// LuminanceSource source = new BufferedImageLuminanceSource(image);
			// BinaryBitmap bitmap = new BinaryBitmap(new
			// HybridBinarizer(source));
			// Hashtable<DecodeHintType, Object> hints = new
			// Hashtable<DecodeHintType, Object>();
			// hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			// results = new MultiFormatReader().decode(bitmap, hints);
			// if (!tickerNo.equals(results.getText())) {
			// // 删除不匹配图片
			// File delFile = targetFile;
			// if (delFile.exists() && delFile.isFile()) {
			// delFile.delete();
			// }
			// // 二维码内容与凭条号不符，请重新选择图片！
			// jsonData.put(Parameter.RESULT_FLAG_KEY,
			// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// jsonData.put(Parameter.ERROR_NO_KEY,
			// ExternalConstant.HardwareInterface.ERROR_NO_E03);
			// jsonData.put(Parameter.ERROR_MSG_KEY, "二维码内容与凭条号不符，请重新选择图片！");
			// return gson.toJson(jsonData);
			// } else {
			byte[] photoImg;
			try {
				if (!file.isEmpty()) {
					photoImg = file.getBytes();
					if (photoImg == null) {
						// [保存失败]图片保存失败，请重新选择图片然后上传！
						jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
						jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
					} else {
						jsonData = doorOrderInfoAppService.checkAndSaveTicker(orderId, tickerNo, photoImg, user);
					}
				} else {
					// [上传失败]上传图片不能为空
					jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，上传图片不能为空！");
				}
			} catch (Exception e) {
				e.printStackTrace();
				// [保存失败]图片保存失败，请重新选择图片然后上传！
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
			}
			return gson.toJson(jsonData);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			// 二维码解析失败，请重新选择二维码图片！
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "二维码解析失败，请重新选择二维码图片！");
			targetFile.delete();
			return gson.toJson(jsonData);
		} finally {
			// // 删除本地图片
			// File delFile = targetFile;
			// if (delFile.exists() && delFile.isFile()) {
			// delFile.delete();
			// }
		}
		// -----------------保存二维码图片-----------------
		// 图片压缩
		// byte[] photoImg;
		// try {
		// if (!file.isEmpty()) {
		//// photoImg = CommonUtils.photoShrink(file);
		// photoImg = file.getBytes();
		// if (photoImg == null) {
		// // [保存失败]图片保存失败，请重新选择图片然后上传！
		// jsonData.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// jsonData.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E03);
		// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
		// } else {
		// jsonData = doorOrderInfoAppService.checkAndSaveTicker(orderId,
		// tickerNo, photoImg, user);
		// }
		// } else {
		// // [上传失败]上传图片不能为空
		// jsonData.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// jsonData.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E03);
		// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，上传图片不能为空！");
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// // [保存失败]图片保存失败，请重新选择图片然后上传！
		// jsonData.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// jsonData.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E03);
		// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
		// }
		// return gson.toJson(jsonData);
	}

	/**
	 * 显示凭条图片
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param tickerNo
	 * @param orderId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showImage")
	public void showImage(@RequestParam(value = "tickerNo", required = false) String tickerNo,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "doorId") String doorId, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// String savePath =
		// request.getSession().getServletContext().getRealPath("/doorOrderImg"
		// + "/" + doorId);
		String savePath = Global.getConfig("miniProgram.saveImgUrl") + "/doorOrderImg/" + doorId;
		File f = new File(savePath);
		File[] imgs = f.listFiles();
		if (!Collections3.isEmpty(Arrays.asList(imgs))) {
			byte[] b = new byte[0];
			FileInputStream fis;
			for (File img : imgs) {
				if (img.getName().substring(0, img.getName().length() - 4).equals(tickerNo + "-" + orderId)) {
					fis = new FileInputStream(img);
					b = new byte[(int) img.length()];
					fis.read(b);
					fis.close();
					break;
				}
			}

			// 查询凭条信息
			DoorOrderDetail doorOrderDetail = doorOrderInfoAppService.getDoorOrderDetailPhoto(orderId, tickerNo);
			doorOrderDetail.setPhoto(null);
			doorOrderDetail.setPhoto(b);
			DoorCommonUtils.showImage(doorOrderDetail, "photo", request, response);
		}
	}
}