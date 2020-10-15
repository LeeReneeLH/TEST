/**
 * wenjian:    StoreGoodsInfoController.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午11:16:16
 */
package com.coffer.businesses.modules.store.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoreGoodsInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.businesses.modules.store.v01.service.StoreGoodsInfoService;
import com.coffer.businesses.modules.store.v01.service.StoreManagementInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
* Title: StoreGoodsInfoController 
* <p>Description: 库房货品Controller</p>
* @author wangbaozhong
* @date 2017年8月9日 上午11:16:16
*/
@Controller
@RequestMapping(value = "${adminPath}/store/v01/storeGoodsInfo")
public class StoreGoodsInfoController extends BaseController {
	@Autowired
	private StoreGoodsInfoService service;
	
	@Autowired
	private StoreManagementInfoService storeManagementInfoService;
	
	@ModelAttribute
	public StoreGoodsInfo get(String id) {
		if (StringUtils.isNotBlank(id)) {
			return service.get(id);
		} else {
			return new StoreGoodsInfo();
		}
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 列表页面</p>
	 * @author:     wangbaozhong
	 * @param storeGoodsInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = {"list", ""})
	public String list(StoreGoodsInfo storeGoodsInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<StoreGoodsInfo> page = new Page<StoreGoodsInfo>(request, response);
		page = service.findPage(page, storeGoodsInfo);
		
		
		StoreManagementInfo storeInfo = new StoreManagementInfo();
		storeInfo.setOffice(UserUtils.getUser().getOffice());
		storeInfo.setDelFlag(Constant.deleteFlag.Valid);
		List<StoreManagementInfo> storeList = storeManagementInfoService.findList(storeInfo);
		
		model.addAttribute("page", page);
		model.addAttribute("storeGoodsInfo", storeGoodsInfo);
		
		model.addAttribute("storeList", storeList);
		return "modules/store/v01/storeManagementInfo/storegoodsInfoList";
	}
	
	/**
	 * 
	 * Title: showDetail
	 * <p>Description: 显示明细</p>
	 * @author:     wangbaozhong
	 * @param storeGoodsInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "showDetail")
	public String showDetail(StoreGoodsInfo storeGoodsInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		model.addAttribute("storeGoodsInfo", storeGoodsInfo);
		return "modules/store/v01/storeManagementInfo/storeGoodsInfoShowDetail";
	}
}
