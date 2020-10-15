/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Log;
import com.coffer.core.modules.sys.service.LogService;

/**
 * 日志Controller
 *
 * @author ThinkGem
 * @version 2013-6-2
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	/**
	 * 本controller中的业务异常统一在这个方法中处理
	 *
	 * @param e
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler({ BusinessException.class })
	public RedirectView fileNotFoundHandler(BusinessException e, HttpServletRequest request,
			HttpServletResponse response) {
		RedirectView rw = new RedirectView(
				request.getServletContext().getContextPath() + Global.getAdminPath() + "/sys/log/download");
		rw.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
		if (outputFlashMap != null) {
			outputFlashMap.put("message", e.getMessageContent());
		}
		return rw;
	}

	@RequiresPermissions("sys:log:view")
	@RequestMapping(value = { "list", "" })
	public String list(Log log, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Log> page = logService.findPage(new Page<Log>(request, response), log);
		model.addAttribute("page", page);
		model.addAttribute("log", log);
		return "modules/sys/logList";
	}

	@RequiresPermissions("sys:log:view")
	@RequestMapping(value = "download")
	public String logDownload() {
		return "modules/sys/logDownload";
	}

	@RequestMapping(value = "/download/{yyyy}/{MM}/{dd}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> download(@PathVariable String yyyy, @PathVariable String MM, @PathVariable String dd)
			throws IOException {
		String logPath = "/datadisk/data/log/anda/";
		String currentDate = DateUtils.getDate();
		String targetDate = yyyy + "-" + MM + "-" + dd;
		String targetFileName, filename;
		if (currentDate.equals(targetDate)) {
			targetFileName = logPath + "anda.log";
			filename = "anda.log";
		} else {
			targetFileName = logPath + "anda.log" + "." + targetDate;
			filename = "anda.log" + "." + targetDate;
		}
		File file = new File(targetFileName);
		HttpHeaders headers = new HttpHeaders();// 设置头信息
		String downloadFileName = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);// 设置响应的文件名
		headers.setContentDispositionFormData("attachment", downloadFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		// MediaType:互联网媒介类型 contentType：具体请求中的媒体类型信息
		return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ResponseEntity<byte[]> download(@RequestParam("dataTime") String dataTime,@RequestParam("logType") String logType) {
		String logPath = "/datadisk/data/log/szyh/" + logType + "/";
		String currentDate = DateUtils.getDate();
		String targetFileName, filename;
		if (currentDate.equals(dataTime)) {
			targetFileName = logPath + logType + ".log";
			filename = logType + ".log";
		} else {
			targetFileName = logPath + logType + ".log" + "." + dataTime;
			filename = logType + ".log" + "." + dataTime;
		}
		File file = new File(targetFileName);
		HttpHeaders headers = new HttpHeaders();// 设置头信息
		String downloadFileName = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		headers.setContentDispositionFormData("attachment", downloadFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		// MediaType:互联网媒介类型 contentType：具体请求中的媒体类型信息
		byte[] downloadFileBytes;
		try {
			downloadFileBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			throw new BusinessException("E99", "[下载失败]未找到当天日志文件");
		}
		return new ResponseEntity<>(downloadFileBytes, headers, HttpStatus.CREATED);
	}

}
