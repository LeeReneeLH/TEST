package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import com.coffer.businesses.modules.doorOrder.app.v01.service.TransferFundsService;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
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

/**
 * 
 * Title: TransferFundsController
 * 
 * @author lihe
 * @date 2019年8月27日
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/transferFunds")
public class TransferFundsController extends BaseController {

	protected static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm")// 时间转化为特定格式
			.create();

	@Autowired
	private TransferFundsService transferFundsService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private DayReportDoorMerchanService dayReportDoorMerchanService;

	/**
	 * 
	 * Title: getTransferFundsList
	 * <p>
	 * Description: 小程序查询划款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @param orderBy
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getTransferFundsList", produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String getTransferFundsList(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "pageSize") int pageSize, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart) {
		Map<String, Object> jsonData = Maps.newHashMap();
		User user = UserUtils.get(userId);
		if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			return gson.toJson(jsonData);
		}
		Page<DayReportDoorMerchan> result = new Page<DayReportDoorMerchan>(pageNo, pageSize);
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		Office office = user.getOffice();
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND o1.parent_ids LIKE '%" + office.getParentId() + "%'");
		} else {
			dayReportDoorMerchan.getSqlMap().put("dsf",
					"AND( o.parent_ids LIKE '%" + office.getId() + "%' OR o.ID =" + office.getId() + ")");
		}

		// 日结时间
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
			dayReportDoorMerchan.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			// 查询条件： 结束时间
			dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
		}

		DayReportDoorMerchan dMerchan = transferFundsService.getTransferFundsTotal(dayReportDoorMerchan);
		if (dMerchan == null) {
			dMerchan = new DayReportDoorMerchan();
		}
		dayReportDoorMerchan.setPage(result);
		result.setList(transferFundsService.getTransferFundsList(dayReportDoorMerchan));
		jsonData.put(Parameter.RESULT, result);
		jsonData.put(Parameter.TOTAL_AMOUNT_KEY, dMerchan.getTotalAmount()); // 应划款合计
		jsonData.put(Parameter.PAID_AMOUNT_KEY, dMerchan.getPaidAmount()); // 实划款合计
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * service032 商户划款信息查询controller merchantId,reportId zhr
	 */
	@RequestMapping(value = "getTransferFundsInfo", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getTransferFundsInfo(@RequestParam(value = "merchantId") String merchantId, // 商户Id(现需要传门店Id)
			@RequestParam(value = "reportId") String reportId) { // 日结Id
		Map<String, Object> jsonData = Maps.newHashMap();
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		dayReportDoorMerchan.setOfficeId(merchantId);
		dayReportDoorMerchan.setReportId(reportId);
		List<DayReportDoorMerchan> list = transferFundsService.getfindByMerchantIdAndReportId(dayReportDoorMerchan);
		jsonData.put(Parameter.RESULT, list);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: getTransferFundsListByUser
	 * <p>
	 * Description: 查询当前用户已办划款
	 * </p>
	 * 
	 * @author: lihe
	 * @param userId
	 * @param createTimeStart
	 * @param pageSize
	 * @param pageNo
	 * @return String 返回类型
	 */
	@RequestMapping(value = "getTransferFundsListByUser", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getTransferFundsListByUser(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		Page<DayReportDoorMerchan> result = new Page<DayReportDoorMerchan>(pageNo, pageSize);
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		dayReportDoorMerchan.setPage(result);
		User user = UserUtils.get(userId);
		if (!"{}".equals(gson.toJson(checkParam(jsonData, user)))) {
			return gson.toJson(jsonData);
		}
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
			dayReportDoorMerchan.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			// 查询条件： 结束时间
			dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
		}
		Office office = user.getOffice();
		dayReportDoorMerchan.setUpdateBy(user);
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
			dayReportDoorMerchan.setRofficeId(office.getId());
		} else if (Constant.OfficeType.MERCHANT.equals(office.getType())) {
			dayReportDoorMerchan.setOfficeId(office.getId());
		} else {
			dayReportDoorMerchan.getSqlMap().put("dsf",
					"AND o2.parent_ids LIKE '%" + office.getParentId() + "%' AND o2.type='9'");
		}
		result.setList(transferFundsService.getTransferFundsListByUser(dayReportDoorMerchan));
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
		Office office = new Office();
		office.setId(user.getOffice().getId());
		if (null == officeService.get(office)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			jsonData.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + user.getOffice().getId() + "的数据");
			return jsonData;
		}
		return jsonData;
	}

	/**
	 * 
	 * 已办划款凭证图片上传
	 * 
	 * @author XL
	 * @version 2019年8月28日
	 * @param file
	 * @param officeId
	 * @param reportId
	 * @return
	 */
	@RequestMapping(value = "/saveTransferFundsPic", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String saveTransferFundsPic(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "officeId") String officeId, @RequestParam(value = "reportId") String reportId,
			@RequestParam(value = "userId") String userId) {
		// 返回内容
		Map<String, Object> jsonData = Maps.newHashMap();
		// -----------------验证参数-----------------
		if (StringUtils.isBlank(officeId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，商户不能为空！");
			return gson.toJson(jsonData);
		}
		if (StringUtils.isBlank(reportId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，日结编号不能为空！");
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
		// -----------------保存图片-----------------
		File targetFile = null;
		try {
			// 保存路径
			// String savePath =
			// request.getSession().getServletContext().getRealPath("/transferFundsImg"
			// + "/" + officeId);
			String savePath = Global.getConfig("miniProgram.saveImgUrl") + "/transferFundsImg/" + officeId;
			File f = new File(savePath);
			// img文件夹是否存在
			if (!f.exists()) {
				f.mkdirs();
			}
			String originalFilename = file.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			String fileName = reportId + suffix;
			targetFile = new File(savePath, fileName);
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			// 图片上传失败，请重新选择图片然后上传！！
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
			return gson.toJson(jsonData);
		}
		// 图片压缩
		byte[] photoImg;
		try {
			if (!file.isEmpty()) {
				photoImg = file.getBytes();
				// photoImg = CommonUtils.photoShrink(file);
				// if (photoImg == null) {
				// // [保存失败]图片保存失败，请重新选择图片然后上传！
				// jsonData.put(Parameter.RESULT_FLAG_KEY,
				// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				// jsonData.put(Parameter.ERROR_NO_KEY,
				// ExternalConstant.HardwareInterface.ERROR_NO_E03);
				// jsonData.put(Parameter.ERROR_MSG_KEY, "图片上传失败，请重新选择图片然后上传！");
				// } else {
				jsonData = transferFundsService.saveTransferFundsPic(reportId, officeId, photoImg, user);
				// }
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
	}

	/**
	 * 显示已办划款凭证图片
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showImage")
	public void showImage(HttpServletRequest request, @RequestParam(value = "officeId") String officeId,
			@RequestParam(value = "reportId") String reportId, HttpServletResponse response) throws IOException {
		// String savePath =
		// request.getSession().getServletContext().getRealPath("/transferFundsImg"
		// + "/" + officeId);
		String savePath = Global.getConfig("miniProgram.saveImgUrl") + "/transferFundsImg/" + officeId;
		File f = new File(savePath);
		File[] imgs = f.listFiles();

		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		// 日结ID
		dayReportDoorMerchan.setReportId(reportId);
		// 商户ID
		dayReportDoorMerchan.setOfficeId(officeId);
		// 查询划款列表
		List<DayReportDoorMerchan> dayReportDoorMerchanList = dayReportDoorMerchanService
				.findList(dayReportDoorMerchan);
		if (Collections3.isEmpty(dayReportDoorMerchanList)) {
			dayReportDoorMerchan = null;
		} else {
			byte[] b = new byte[0];
			FileInputStream fis;
			for (File img : imgs) {
				if (img.getName().substring(0, img.getName().length() - 4).equals(reportId)) {
					fis = new FileInputStream(img);
					b = new byte[(int) img.length()];
					fis.read(b);
					fis.close();
					break;
				}
			}
			dayReportDoorMerchan = dayReportDoorMerchanList.get(0);
			dayReportDoorMerchan.setPhoto(null);
			dayReportDoorMerchan.setPhoto(b);
		}
		DoorCommonUtils.showImage(dayReportDoorMerchan, "photo", request, response);
	}
}