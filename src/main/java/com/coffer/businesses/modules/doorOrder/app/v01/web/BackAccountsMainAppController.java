package com.coffer.businesses.modules.doorOrder.app.v01.web;

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

import com.coffer.businesses.modules.doorOrder.app.v01.service.BackAccountsMainAppService;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.BackAccountsMainService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 小程序回款管理Controller
 * 
 * @author lihe
 * @version 2019-08-22
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/backAccountsMain")
public class BackAccountsMainAppController extends BaseController {

	protected static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm")// 时间转化为特定格式
			.create();

	@Autowired
	private BackAccountsMainAppService backAccountsMainAppService;

	@Autowired
	private BackAccountsMainService backAccountsMainService;

	@RequestMapping(value = "backAccountsList", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String backAccountsList(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Map<String, Object> jsonData = Maps.newHashMap();
		Page<BackAccountsMain> page = new Page<BackAccountsMain>(pageNo, pageSize);
		BackAccountsMain backAccountsMain = new BackAccountsMain();
		backAccountsMain.setPage(page);
		User userInfo = UserUtils.get(userId);
		if (userInfo == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
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
			backAccountsMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(date)));
			// 查询条件： 结束时间
			backAccountsMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(date)));
		}
		page.setList(backAccountsMainAppService.findPage(backAccountsMain, userInfo));
		jsonData.put(Parameter.RESULT, page);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * 清分回款凭证图片上传
	 * 
	 * @author XL
	 * @version 2019年8月28日
	 * @param file
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/saveBackAccountsPic", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String saveBackAccountsPic(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "id") String id, @RequestParam(value = "userId") String userId,
			@RequestParam(value = "officeId") String officeId) {
		// 返回内容
		Map<String, Object> jsonData = Maps.newHashMap();
		// -----------------验证参数-----------------
		if (StringUtils.isBlank(id)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数异常，回款编号不能为空！");
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
					jsonData = backAccountsMainAppService.saveBackAccountsPic(id, photoImg, user);
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
	}

	/**
	 * 显示清分回款凭证图片
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "showImage")
	public void showImage(@RequestParam(value = "id") String id, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = "officeId") String officeId) throws IOException {
			// 查询回款信息
		BackAccountsMain backAccountsMain = backAccountsMainService.get(id);
		DoorCommonUtils.showImage(backAccountsMain, "photo", request, response);
	}

}