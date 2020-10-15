/**
 * @author Murhpy
 * @version 2015年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.gzh;

import java.util.List;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBoxModService;
import com.coffer.businesses.modules.atm.v01.service.AtmBrandsInfoService;
import com.coffer.core.common.utils.SpringContextHolder;

/**
 * @author Murphy
 * 
 */
public class GzhCommonUtils extends BusinessUtils {

	// ATM品牌型号服务层
	private static AtmBrandsInfoService atmBrandsInfoService = SpringContextHolder.getBean(AtmBrandsInfoService.class);

	// 钞箱类型配置服务层
	private static AtmBoxModService atmBoxModService = SpringContextHolder.getBean(AtmBoxModService.class);
	
	/**
	 * 获取ATM品牌信息列表
	 * 
	 * @author Murphy
	 * @version 2015年5月13日
	 * 
	 * 
	 * @return
	 */
	public static List<AtmBrandsInfo> getAtmBrandsinfoList() {
		return atmBrandsInfoService.getAtmBrandsinfoList();
	}

	/**
	 * 获取ATM型号信息列表
	 * 
	 * @author Murphy
	 * @version 2015年5月13日
	 * 
	 * 
	 * @return
	 */
	public static List<AtmBrandsInfo> getAtmTypesinfoList() {
		return atmBrandsInfoService.getAtmTypesinfoList();
	}

	/**
	 * 根据钞箱类型获取钞箱类型配置信息
	 * 
	 * @author Murphy
	 * @version 2015年5月20日
	 * 
	 * 
	 * @param boxType
	 * @return
	 */
	public static List<AtmBoxMod> getAtmBoxModInfo(String boxType) {
		AtmBoxMod atmBoxMod = new AtmBoxMod();
		atmBoxMod.setBoxType(boxType);
		return atmBoxModService.findList(atmBoxMod);
	}
}
