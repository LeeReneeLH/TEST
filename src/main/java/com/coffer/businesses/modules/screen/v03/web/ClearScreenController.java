package com.coffer.businesses.modules.screen.v03.web;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.screen.ScreenConstant;
import com.coffer.businesses.modules.screen.v03.entity.ClearScreenMain;
import com.coffer.businesses.modules.screen.v03.service.ClearScreenService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 数字化大屏Controller
 * 
 * @author wanglin
 * @version 2017-09-13
 */
@Controller
@RequestMapping(value = "${frontPath}")
public class ClearScreenController extends BaseController {

	@Autowired
	private ClearScreenService clearScreenService;
	@Autowired
	private OfficeService officeService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping
	public String list(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(value = "curOfficeId", required = false) String curOfficeId) {
		ClearScreenMain clearReturn = new ClearScreenMain();
		ClearScreenMain clearService = new ClearScreenMain();
		
		List<Map<String, Object>> AtmList = Lists.newArrayList();
		List<Map<String, Object>> doorList = Lists.newArrayList();
		
		Map<String, Object> map = getAddress(model);

		String mapTypeCode =String.valueOf(map.get("mapTypeCode"));
		Map<String, String> jsonInfoMap = Maps.newHashMap();


		jsonInfoMap =(Map<String, String>) map.get(ReportGraphConstant.MAP_JSON_INFO_KEY);
		
		String mapCode = "";
		// 验证是否有地图
		if (jsonInfoMap != null) {
			for (String k : jsonInfoMap.keySet()) {
				mapCode = jsonInfoMap.get(k);
			}
		}

		String officeId = clearScreenService.getOfficeList(mapCode, mapTypeCode);
		if ((mapTypeCode.equals("2") || mapTypeCode.equals("3")) && StringUtils.isBlank(officeId)) {

		} else {
		
			// 自助设备列表
			AtmList = clearScreenService.findAtmCountList(officeId);
			model.addAttribute("atmList", AtmList);
		
			// 上门收款列表
			doorList = clearScreenService.findDoorOrderList(officeId);
			model.addAttribute("businessList", doorList);
		
			// 今日及累计金额的取得
			clearReturn = getAmountData(officeId);
			clearService = getServiceData(officeId);
		}
		
		clearReturn.setClearCount(clearService.getClearCount());
		clearReturn.setGoldBankCount(clearService.getGoldBankCount());
		clearReturn.setDoorCustCount(clearService.getDoorCustCount());
		clearReturn.setDoorBusinessCount(clearService.getDoorBusinessCount());
		clearReturn.setDoorGoldBankCount(clearService.getDoorGoldBankCount());
		clearReturn.setAtmCount(clearService.getAtmCount());
		clearReturn.setAtmCustCount(clearService.getAtmCustCount());
		
		List<Map<String, Object>> list = clearScreenService.getAllAmountList(officeId);
		
		
		model.addAttribute("serviceList", list);
		model.addAttribute("clearScreenMain", clearReturn);
		model.addAttribute("curOfficeId", curOfficeId);
		return "modules/screen/v03/clear/clearScreen";
	}
	
	
 	/**
     * 今日及累计金额的取得
     * @param 
     * @return
     */
    public  ClearScreenMain getAmountData(String officeId){
		ClearScreenMain clearReturn = new ClearScreenMain();
		
		ClearScreenMain clearMain = new ClearScreenMain();

		clearMain = clearScreenService.findUpList(officeId, new Date());
		clearReturn.setToDayBankUpAmount(clearMain.getInAmount());
		
		//商行上缴额取得(合计)
		clearMain = clearScreenService.findUpList(officeId,null);
		clearReturn.setTotalBankUpAmount(clearMain.getInAmount());
		
		//商行取款额取得(今日)
		clearMain = clearScreenService.findBackList(officeId, new Date());
		clearReturn.setToDayBankBackAmount(clearMain.getOutAmount());
		//商行取款额取得(合计)
		clearMain = clearScreenService.findBackList(officeId, null);
		clearReturn.setTotalBankBackAmount(clearMain.getOutAmount());
		
		//清分差错额取得(今日)
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00"); 
		clearMain = clearScreenService.findErrorList(officeId, new Date());
		clearReturn.setToDayClearErrorAmount(clearMain.getErrorMoney());
		
		//清分差错额取得(合计)
		clearMain = clearScreenService.findErrorList(officeId, null);
		clearReturn.setTotalClearErrorAmount(clearMain.getErrorMoney());
		
		//清分中心清点额取得(今日)
		clearMain = clearScreenService.findClearList(officeId, new Date());
		double douAmount3 = Double.valueOf(clearMain.getCountAmount()) + Double.valueOf(clearMain.getReCountAmount()) ;
		clearReturn.setToDayClearAmount(String.valueOf(df.format(douAmount3)));
		
		//清分中心清点额取得(合计)
		clearMain = clearScreenService.findClearList(officeId, null);
		double douAmount4 = Double.valueOf(clearMain.getCountAmount()) + Double.valueOf(clearMain.getReCountAmount()) ;
		clearReturn.setTotalClearAmount(String.valueOf(df.format(douAmount4)));
		clearReturn.setTotalClearCountAmount(clearMain.getCountAmount());
		clearReturn.setTotalClearReCountAmount(clearMain.getReCountAmount());
		
		
    	return clearReturn;
    }

	/**
	 * 获取全国/全省地图
	 * 
	 * @author qph
	 * 
	 * @version 2018年01月31日
	 * 
	 */
	@RequestMapping(value = "getAddress")
	@ResponseBody
	public Map<String, Object> getAddress(Model model) {
		// 获取地图在线离线标识
		String mapFlag = Global.getConfig("firstPage.map.isOnline");
		// 获取离线地图初始机构
		String officeId = Global.getConfig("clearScreen.office.id");
		// 获取机构
		Office office = SysCommonUtils.findOfficeById(officeId);
		Map<String, Object> rtnMap = Maps.newHashMap();
		if (office != null) {
			// 初始机构为平台
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				rtnMap = clearScreenService.getChinaAddress(officeId, mapFlag);
				rtnMap.put("mapTypeCode", ScreenConstant.MapTypeCode.CHINAMAPCODE);
				model.addAttribute("mapTypeCode", ScreenConstant.MapTypeCode.CHINAMAPCODE);
			}
			// 初始机构为清分中心
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				rtnMap = clearScreenService.getCityAddress(officeId, mapFlag);
				rtnMap.put("mapTypeCode", ScreenConstant.MapTypeCode.CITYMAPCODE);
				model.addAttribute("mapTypeCode", ScreenConstant.MapTypeCode.CITYMAPCODE);
				model.addAttribute("mapCode", office.getCityCode());
			}
			// 初始机构为人民银行
			if (Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
				// 省级人行
				if (StringUtils.isNotBlank(office.getProvinceCode()) && StringUtils.isBlank(office.getCityCode())) {
					rtnMap = clearScreenService.getProvinceAddress(officeId, mapFlag);
					rtnMap.put("mapTypeCode", ScreenConstant.MapTypeCode.PROVINCEMAPCODE);
					model.addAttribute("mapTypeCode", ScreenConstant.MapTypeCode.PROVINCEMAPCODE);
					model.addAttribute("mapCode", office.getProvinceCode());
				}
				// 市级人行
				if (StringUtils.isNotBlank(office.getProvinceCode()) && StringUtils.isNotBlank(office.getCityCode())
						&& StringUtils.isBlank(office.getCountyCode())) {
					rtnMap = clearScreenService.getCityAddress(officeId, mapFlag);
					rtnMap.put("mapTypeCode", ScreenConstant.MapTypeCode.CITYMAPCODE);
					model.addAttribute("mapTypeCode", ScreenConstant.MapTypeCode.CITYMAPCODE);
					model.addAttribute("mapCode", office.getCityCode());
				}
			}
		}


		return rtnMap;
	}

	/**
	 * 获取全国/全省地图
	 * 
	 * @author qph
	 * 
	 * @version 2018年01月31日
	 * 
	 */
	@RequestMapping(value = "getAddressByProvinceCode")
	@ResponseBody
	public Map<String, Object> getAddressByProvinceCode(Model model,
			@RequestParam(value = "provinceCode", required = false) String provinceCode) {
		Map<String, Object> rtnMap = clearScreenService.getProvinceAddressByProvinceCode(provinceCode);
		return rtnMap;
	}

	/**
	 * 获取全市地图
	 * 
	 * @author qph
	 * 
	 * @version 2018年01月31日
	 * 
	 */
	@RequestMapping(value = "getAddressByCityCode")
	@ResponseBody
	public Map<String, Object> getAddressByCityCode(Model model,
			@RequestParam(value = "cityCode", required = false) String cityCode) {
		Map<String, Object> rtnMap = clearScreenService.getCityAddressByCityCode(cityCode);
		return rtnMap;
	}
	/**
	 * 全国网点地址取得
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "getChinaAddress")
	@ResponseBody
	public Map<String, Object> getChinaAddress() {
		// 获取地图在线离线标识
		String mapFlag = Global.getConfig("firstPage.map.isOnline");
		// 获取离线地图初始机构
		String officeId = Global.getConfig("clearScreen.office.id");
		Map<String, Object> rtnMap = clearScreenService.getChinaAddress(officeId, mapFlag);
		return rtnMap;
	}

	/**
	 * 全省网点地址取得
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "getProvinceAddress")
	@ResponseBody
	public Map<String, Object> getProvinceAddress() {
		// 获取地图在线离线标识
		String mapFlag = Global.getConfig("firstPage.map.isOnline");
		// 获取离线地图初始机构
		String officeId = Global.getConfig("clearScreen.office.id");
		Map<String, Object> rtnMap = clearScreenService.getProvinceAddress(officeId, mapFlag);
		return rtnMap;
	}

	/**
	 * 全市网点地址取得
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "getCityAddress")
	@ResponseBody
	public Map<String, Object> getCityAddress() {
		// 获取地图在线离线标识
		String mapFlag = Global.getConfig("firstPage.map.isOnline");
		// 获取离线地图初始机构
		String officeId = Global.getConfig("clearScreen.office.id");
		// 市地图数据取得
		Map<String, Object> rtnMap = clearScreenService.getCityAddress(officeId, mapFlag);
		return rtnMap;
	}

 	/**
     * 当前日期加上天数后的日期
     * @param num 为增加的天数
     * @return
     */
    public static int[] getRate(double pAmount1 , double pAmount2){
    	int[] arr = new int[2];
    	pAmount1 = Math.abs(pAmount1);
    	pAmount2 = Math.abs(pAmount2);
		double douSumAmount = pAmount1 + pAmount2;
		if (douSumAmount == 0){
			arr[0] = 0;
			arr[1] = 0;
			return arr;
		}
		long intRate1 = Math.round((pAmount1 / douSumAmount) * 100);
		
		arr[0] =(int) intRate1;
		arr[1] =(int) (100 - intRate1);
        return arr;
    }
    
 	/**
      * 当前日期加上天数后的日期
      * @param num 为增加的天数
      * @return
      */
     public static Date plusDay(int num){
         Date d = new Date();
         Calendar ca = Calendar.getInstance();
         ca.add(Calendar.DATE, num);
         d = ca.getTime();
         return d;
     }
	
	/**
	 * 数据刷新（今日及累计金额）
	 * 
	 * @author WL
	 * @version 2017年10月26日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "refreshAmountData")
	@ResponseBody
	public String refreshAmountData(ClearScreenMain ClearScreenMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		String mapCode = request.getParameter("mapCode");
		String mapType = request.getParameter("mapType");
		ClearScreenMain clearReturn = new ClearScreenMain();
		List<Map<String, Object>> atmList = Lists.newArrayList();
		List<Map<String, Object>> doorList = Lists.newArrayList();
		
		String officeId = clearScreenService.getOfficeList(mapCode , mapType);
		if ((mapType.equals("2") || mapType.equals("3")) && StringUtils.isBlank(officeId)){
			
		}else{
			clearReturn = getAmountData(officeId);
			//自助设备列表
			atmList = clearScreenService.findAtmCountList(officeId);
			//上门收款列表
			doorList = clearScreenService.findDoorOrderList(officeId);
			
		}
		map.put("atmList", atmList);
		map.put("doorList", doorList);
		
		map.put("result", "success");
		//map.put("result", "error");
		map.put("clearScreenMain", clearReturn);

		return gson.toJson(map);
	}
	
	/**
	 * 数据刷新（服务数据）
	 * 
	 * @author WL
	 * @version 2017年10月26日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "refreshServiceData")
	@ResponseBody
	public String refreshServiceData(ClearScreenMain ClearScreenMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		String mapCode = request.getParameter("mapCode");
		String mapType = request.getParameter("mapType");
		ClearScreenMain clearReturn = new ClearScreenMain();
		List<Map<String, Object>> list = Lists.newArrayList();
		String officeId = clearScreenService.getOfficeList(mapCode , mapType);
		
		if ((mapType.equals("2") || mapType.equals("3")) && StringUtils.isBlank(officeId)){
			
		}else{
			clearReturn = getServiceData(officeId);
			list = clearScreenService.getAllAmountList(officeId);
			
		}

		map.put("serviceList", list);
		map.put("result", "success");
		map.put("clearScreenMain", clearReturn);

		return gson.toJson(map);
	}
	
	
	
	
	/**
	 * 
	 * Title: getMapGraphData
	 * <p>
	 * Description: 获取首页工作台地图数据
	 * </p>
	 * 
	 * @author: wanghan
	 * @return 地图数据以json形式返回 String 返回类型
	 */
	@RequestMapping(value = "getMapTotalData")
	@ResponseBody
	public String getMapTotalData(@RequestParam(value = "curOfficeId", required = false) String curOfficeId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("result", "success");
		map.put("clearScreenMain", "");
		
		return gson.toJson(map);
	}

	
	
 	/**
     * 服务数据的取得
     * @param 
     * @return
     */
    public  ClearScreenMain getServiceData(String officeId){
		ClearScreenMain clearReturn = new ClearScreenMain();
	
		//查询服务清分业务机构数量
		int intClearCount = clearScreenService.getClearCount(officeId);
		clearReturn.setClearCount(String.valueOf(intClearCount));
		
		
		//查询服务金库业务机构数量
		int intGoldBankCount = clearScreenService.getGoldBankCount(officeId);
		clearReturn.setGoldBankCount(String.valueOf(intGoldBankCount));
		
		//查询上门收款门店
		int intDoorCustCount = clearScreenService.getDoorCustCount(officeId);
		clearReturn.setDoorCustCount(String.valueOf(intDoorCustCount));
		
		//查询上门收款商户 
		int intDoorBusinessCount = clearScreenService.getDoorBusinessCount(officeId);
		clearReturn.setDoorBusinessCount(String.valueOf(intDoorBusinessCount));
		
		//查询服务上门收款
		int intDoorGoldBankCount = clearScreenService.getDoorGoldBankCount(officeId);
		clearReturn.setDoorGoldBankCount(String.valueOf(intDoorGoldBankCount));

		//查询加钞自助设备(ATM)
		int intAtmCount = clearScreenService.getAtmCount(officeId);
		clearReturn.setAtmCount(String.valueOf(intAtmCount));
		
		//查询服务自助设备客户(ATM)
		int intAtmCustCount = clearScreenService.getAtmCustCount(officeId);
		clearReturn.setAtmCustCount(String.valueOf(intAtmCustCount));
		
		
    	return clearReturn;
    }

 	/**
     * 机构ID的取得
     * @param 
     * @return
     */
    public  List<String> findOfficeIdList(String  strOfficeId , List<String> officeList){
    	List<Office> listOffice1 = officeService.findCustList(null, strOfficeId, officeList);
    	List<String> officeNoList1 = Lists.newArrayList();
    	for (Office resultOffice : listOffice1) {
    		officeNoList1.add(resultOffice.getId());
    	}
    	return officeNoList1;
    }
	
	/**
	 * 地图的区域编号的取得
	 * 
	 * @author WL
	 * @version 2017年10月26日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getMapCode")
	@ResponseBody
	public String getMapCode(ClearScreenMain ClearScreenMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		String mapName = "";
		String mapType = request.getParameter("mapType");
		try {
			mapName = new String(request.getParameter("mapName").getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		String strMapCode = clearScreenService.getMapCode(mapName , mapType);
		map.put("result", "success");
		//map.put("result", "error");
		map.put("MapCode", strMapCode);

		return gson.toJson(map);
	}
    
    

	
}