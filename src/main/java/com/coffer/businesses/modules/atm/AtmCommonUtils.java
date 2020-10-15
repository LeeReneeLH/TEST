/**
 * @author Murhpy
 * @version 2015年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.atm;

import java.util.ArrayList;
import java.util.List;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingDetail;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBindingInfoService;
import com.coffer.businesses.modules.atm.v01.service.AtmBoxModService;
import com.coffer.businesses.modules.atm.v01.service.AtmBrandsInfoService;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;

/**
 * @author Murphy
 * 
 */
public class AtmCommonUtils extends BusinessUtils {

	// ATM品牌型号服务层
	private static AtmBrandsInfoService atmBrandsInfoService = SpringContextHolder.getBean(AtmBrandsInfoService.class);

	// 钞箱类型配置服务层
	private static AtmBoxModService atmBoxModService = SpringContextHolder.getBean(AtmBoxModService.class);
	
	// ATM绑定信息服务层
	private static AtmBindingInfoService atmBindingInfoService = SpringContextHolder.getBean(AtmBindingInfoService.class);
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
	
	/**
	 * @author wanglu
	 * @serialData 20171115
	 * @param boxNo
	 * @return
	 */
	public static String getPlanIdByBoxNo(String boxNo) {
		
		String planId = "";
		List<AtmBindingDetail> atmBindingDetailList = new ArrayList<AtmBindingDetail>();
		
		/*当boxNo不为null并且不为""时
		 获取该钞箱号的ATM绑定详情，详情按
		 照降序排列*/
		if(StringUtils.isNotBlank(boxNo)) {
			AtmBindingDetail atmBindingDetail = new AtmBindingDetail(); 
			atmBindingDetail.setBoxNo(boxNo);
			atmBindingDetailList = atmBindingInfoService.getAtmBindingDetailListByBoxNo(atmBindingDetail);	
		}
		
		if(atmBindingDetailList.size() > 0) {
			AtmBindingInfo paramAtmBindingInfo = new AtmBindingInfo();
			paramAtmBindingInfo.setBindingId(atmBindingDetailList.get(0).getBindingId());
			planId = atmBindingInfoService.get(paramAtmBindingInfo).getAddPlanId();	//根据bindingId获取planId
		}else {
			planId = null;
		}
		
		return planId;
	}
}
