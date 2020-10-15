package com.coffer.businesses.modules.weChat.v03.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.utils.weChat.AccessToken;
import com.coffer.businesses.common.utils.weChat.WeixinUtil;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.clear.v03.service.OrderClearService;
import com.coffer.businesses.modules.common.entity.ReceiveEntity;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.WechatMessageUtils;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.businesses.modules.weChat.v03.service.GuestService;
import com.coffer.businesses.modules.weChat.v03.service.WechatAccountService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.google.common.collect.Maps;

import net.sf.json.JSONObject;
/**
 * 
 * @author LLF
 * @version 2015年9月17日 ajax请求控制类
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/wechatAccount")
public class WechatAccountController extends BaseController {

	@Autowired
	WechatAccountService ipadAjaxService;

	@Autowired
	private DoorOrderInfoService doorOrderInfoService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private GuestService guestService;
	@Autowired
	private WechatAccountService wechatAccountService;
	@Autowired
	private OrderClearService orderClearService;
	
	
	public static final String KEY = "r9MEmklnH6uC7nFh";		//签名用静态常量key
	
	public static int guestNumber = 0;
	public static int doorNumber = 0;

	@Value("${DNBX_TOKEN}")
	private String DNBX_TOKEN;

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月17日
	 * 
	 *          接收ajax请求
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "ajax", method = RequestMethod.POST)
	@ResponseBody
	public String ajaxDatas(ReceiveEntity entity, HttpServletRequest request, HttpServletResponse response) {
		String serviceNo = entity.getServiceNo();
		logger.info("接收JSON:" + entity.toString());
		String resultString = "";
		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", entity.getServiceNo());
		map.put("versionNo", entity.getVersionNo());
		try {
			if ("***".equals(serviceNo)) {

			} else {
				resultString = ipadAjaxService.erro(serviceNo);
			}
		} catch (BusinessException be) {
			// 业务异常
			map.put("resultFlag", WeChatConstant.Interface.RESULT_FLAG_FAILURE);
			map.put("errorMsg", msg.getMessage(be.getMessageCode(), be.getParameters(), null) );
			resultString = gson.toJson(map);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			resultString = gson.toJson(map);
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info("返回JSON:" + resultString);
		return resultString;
	}
	
	/**
	 * 健康网址
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "Healthy")
	@ResponseBody
	public String ajaxDatas(HttpServletRequest request, HttpServletResponse response) {
		
		logger.info("==========**服务健康检测**==========");
		return "TRUE";
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月17日
	 * 
	 *          接收ajax请求 此处代码还需优化处理
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "branchCallBack")
	public String branchCallBack(DoorOrderInfo doorOrderInfo, HttpServletRequest request, HttpServletResponse response,Model model) {
		Locale locale = LocaleContextHolder.getLocale();
		Map<String, Object> map = Maps.newHashMap();
		//-----------------------------------
		map.put("serviceNo", doorOrderInfo.getServiceNo());
		map.put("versionNo", doorOrderInfo.getVersionNo());
		
		String code = request.getParameter("code");
		// 获得微信用token和openId
		String appId = Global.getConfig(WeChatConstant.WechatCommonCha.APPID);
		String appSecret = Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET);
//		String businesstype1=(String) request.getSession().getAttribute("businesstype");
		String businesstype =request.getParameter("businesstype");
		AccessToken resultToken = getUserOpenId(appId,appSecret,code);
		//User user = new User(); 
		//user.setOpenId(resultToken.getOpenId());
		String branchValue =request.getParameter("branch_value");
		Guest guest =new Guest();
		guest.setOpenId(resultToken.getOpenId());
		guest.setBusType(businesstype);
	
		
//		String businesstype =request.getParameter("businesstype");
//		String openid = request.getParameter("openid");
//		String sign = request.getParameter("sign");
//		String ts = request.getParameter("ts");
//		String subscribe = request.getParameter("subscribe"); 
//		Map<String, String> backmap=getsignbackmap(openid, subscribe, ts);
////		if(!getSignback(backmap).equals(sign)){
////			model.addAttribute("errorMessage", "签名无效");
////			return "modules/sys/error";
////		}
//		Guest guest =new Guest();
//		guest.setOpenId(openid);
		//获取到系统当前时间
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		//查询此用户是否获得授权
		guest=guestService.getByopenID(guest);
		//-----------------回调分支修改  修改人：XL 修改时间：2018-05-21 begin-------------------------
		// 门店预约
		if (businesstype.equals(WeChatConstant.BusinessType.DOOR_ORDER)) {
			//门店预约登记
			if(branchValue.equals(WeChatConstant.BranchValue.DOOR_ORDER)){
				//判断此用户是否获取到授权
				if(guest==null || !(guest.getGrantstatus().equals(WeChatConstant.Weixintype.TYPE_ACCESS)&&guest.getBusType().equals(businesstype))) {
					String message = msg.getMessage("message.I3009", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}
				//判断此用户的授权时间是否过期
				if(guest.getGrantDate().getTime()<currentTime.getTime()){
					String message = msg.getMessage("message.I3010", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}

				//String doorName = (String) request.getAttribute("doorName");	
				
				//设置门店名、门店编号、用户编号、申请时间。
				model.addAttribute("doorName", guest.getGofficeName());
				model.addAttribute("doorId", guest.getGofficeId());
				model.addAttribute("userId", guest.getId());
				model.addAttribute("guestType", guest.getGuestType());
				model.addAttribute("orderDate",dateString);

				//获取该门店当天的所有请求
				DoorOrderInfo doorOrderInfo1=doorOrderInfoService.getByorderdate(dateString, guest.getGofficeId());
				//将该门店当天所有预约请求传递到前台
				model.addAttribute("doorOrderInfo", doorOrderInfo1);
				//model.addAttribute("doorName", doorOrderInfo.getBranchName());
				
				return "modules/sys/branchInfo";
			}
			//门店预约授权
			if(branchValue.equals(WeChatConstant.BranchValue.AUTHORIZATION)){
				if (guest!=null&&!guest.getBusType().equals(businesstype)) {
					String message = msg.getMessage("message.I3011", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}
				model.addAttribute("Guest",guest);
				//获取所有门店信息
				Office office=new Office();
				List<Office> list=officeService.findOfficeByType(office);
				//model.addAttribute("openId",openid);
				model.addAttribute("openId",resultToken.getOpenId());
				model.addAttribute("storelist", list);
				model.addAttribute("sysDate",dateString);
				model.addAttribute("businesstype",businesstype);
				return "modules/sys/authorization";
			}
		}
		//预约清分
		if (businesstype.equals(WeChatConstant.BusinessType.ORDER_CLEAR)) {
			if(branchValue.equals(WeChatConstant.BranchValue.ORDER_CLEAR)){
				//判断此用户是否获取到授权
				if(guest==null || !(guest.getGrantstatus().equals(WeChatConstant.Weixintype.TYPE_ACCESS)&&guest.getBusType().equals(businesstype))) {
					String message = msg.getMessage("message.I3009", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}
				//判断此用户的授权时间是否过期
				if(guest.getGrantDate().getTime()<currentTime.getTime()){
					String message = msg.getMessage("message.I3010", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}
				//获取该金库当天的所有请求
				OrderClearMain orderClearInfo=orderClearService.getByDateAndOffice(dateString, guest.getGofficeId());
				//用户编号
				model.addAttribute("userId", guest.getId());
				model.addAttribute("orderClearInfo", orderClearInfo);
				return "modules/sys/orderClear";
			}
			if(branchValue.equals(WeChatConstant.BranchValue.AUTHORIZATION)){
				if (guest!=null&&!guest.getBusType().equals(businesstype)) {
					String message = msg.getMessage("message.I3011", new String[] {}, locale);
					model.addAttribute("errorMessage", message);
					return "modules/sys/error";
				}
				model.addAttribute("Guest",guest);
				model.addAttribute("openId",resultToken.getOpenId());
				model.addAttribute("sysDate",dateString);
				model.addAttribute("businesstype",businesstype);
				return "modules/sys/authorization";
			}
		}
		//----------------------------end------------------------------------
		return null;
	}

	/**
	 * 测试用
	 */
	public static String testSub(){  

		String appId = Global.getConfig("appID");
		String secret = Global.getConfig("appSecret");

		//获取当前公众号通用的access_token
		//AccessToken resultToken = null;  
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appid&secret=$appsecret";  
		requestUrl = requestUrl.replace("$appid", appId);  
		requestUrl = requestUrl.replace("$appsecret", secret);  

		JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);  
		String straccess_token =jsonObject.getString("access_token");
		//        if(null != jsonObject){  
		//        	resultToken = new AccessToken();
		//        	resultToken.setToken(jsonObject.getString("access_token"));
		//        }  

		//获取公众号下所有用户
		requestUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=$access_token";  
		requestUrl = requestUrl.replace("$access_token", straccess_token);  
		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);  
		//        if(null != jsonObject){  
		//        	resultToken = new AccessToken();
		//        	resultToken.setToken(jsonObject.getString("access_token"));
		//        }  

		//        //获取公众号下指定用户信息
		requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";  
		requestUrl = requestUrl.replace("ACCESS_TOKEN", straccess_token);  
		requestUrl = requestUrl.replace("OPENID", "");  

		jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null); 

		return "";  
	} 



	/**
	 * 根据code获取关注者账号信息
	 * @author LLF
	 * @param appId
	 * @param secret
	 * @param code
	 * @return
	 */
	public static AccessToken getUserOpenId(String appId,String secret,String code){  
		AccessToken resultToken = null;  
		String requestUrl = Global.getConfig("CodeGetAccessTokenURL");  
		requestUrl = requestUrl.replace("APPID", appId);  
		requestUrl = requestUrl.replace("SECRET", secret);  
		requestUrl = requestUrl.replace("CODE", code);  
		JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);  
		if(null != jsonObject){  
			resultToken = new AccessToken();
			resultToken.setToken(jsonObject.getString("access_token"));
			resultToken.setOpenId(jsonObject.getString("openid"));
			resultToken.setRefreshToken(jsonObject.getString("refresh_token"));
			resultToken.setExpiresIn(jsonObject.getInt("expires_in"));
			resultToken.setScope(jsonObject.getString("scope"));
		}  
		return resultToken;  
	} 

	/**
	 * 网站通过微信官方授权认证
	 * @author LLF
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "branchInfo")
	public static void getCode(HttpServletRequest request, HttpServletResponse response,Model model){  
		String state = request.getRequestURI();
		String appId = Global.getConfig(WeChatConstant.WechatCommonCha.APPID);
		String requestUrl = Global.getConfig(WeChatConstant.WechatCommonCha.WEBAUTHOURL);
		String redirect_uri = Global.getConfig(WeChatConstant.WechatCommonCha.REDIRECTURL);
		//传递业务类型
		redirect_uri = redirect_uri.replace(WeChatConstant.WechatCommonCha.BUSINESSTYPE,
				WeChatConstant.BusinessType.DOOR_ORDER);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.APPIDUP, appId);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.REDIRECT_URI, redirect_uri);
		//requestUrl = requestUrl.replace("SIGN",getSign(DateUtils.getSecondTimestampTwo(new Date()),WeChatConstant.Weixintype.TYPE_ACCESS));
		//requestUrl = requestUrl.replace("TS", DateUtils.getSecondTimestampTwo(new Date()));
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.STATE, state);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.BRANCHVALUE,
				WeChatConstant.BranchValue.DOOR_ORDER);
/*		传递业务类型
		HttpSession session=request.getSession();
		session.setAttribute("businesstype", AjaxConstant.Weixintype.TYPE_ACCESS);*/
		try {			
			response.sendRedirect(requestUrl);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 

	/**
	 * 网站通过微信官方授权认证
	 * @author LLF
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "authorization")
	public static void branchInfoTest(HttpServletRequest request, HttpServletResponse response,Model model){  
		String state = request.getRequestURI();
		String appId = Global.getConfig(WeChatConstant.WechatCommonCha.APPID);
		String requestUrl = Global.getConfig(WeChatConstant.WechatCommonCha.WEBAUTHOURL);
		String redirect_uri = Global.getConfig(WeChatConstant.WechatCommonCha.REDIRECTURL);
		//传递业务类型
		redirect_uri = redirect_uri.replace(WeChatConstant.WechatCommonCha.BUSINESSTYPE,
				WeChatConstant.BusinessType.DOOR_ORDER);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.APPIDUP, appId);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.REDIRECT_URI, redirect_uri);
//		requestUrl = requestUrl.replace("SIGN", getSign(DateUtils.getSecondTimestampTwo(date),WeChatConstant.Weixintype.TYPE_EXCEPTION));
//		requestUrl = requestUrl.replace("TS", DateUtils.getSecondTimestampTwo(date));
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.STATE, state);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.BRANCHVALUE,
				WeChatConstant.BranchValue.AUTHORIZATION);
		/*传递业务类型
		HttpSession session=request.getSession();
		session.setAttribute("businesstype", AjaxConstant.Weixintype.TYPE_EXCEPTION);*/
		try {
			response.sendRedirect(requestUrl);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 





	/**
	 * 
	 * @author WL
	 * @version 2017年3月17日
	 * 
	 *          保存处理
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "branchSave")
	public String branchSave(DoorOrderInfo doorOrderInfo, HttpServletRequest request, HttpServletResponse response,Model model) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", doorOrderInfo.getServiceNo());
		map.put("versionNo", doorOrderInfo.getVersionNo());

		if(doorOrderInfo.getDoorId() == null || doorOrderInfo.getDoorId().isEmpty()){
			//model.addAttribute("errorMessage", "此关注者无权上门收款预约，请先在上门收款公众号管理平台注册！");
			//return "modules/sys/error";
			throw new IllegalArgumentException("error");  
		}

		//机构信息的取得
		//		List<Office> officeList = officeService.findByOfficeCode(doorOrderInfo.getDoorId());
		//		if (officeList.size()>0){
		//			Office office = officeList.get(0);
		//			doorOrderInfo.setDoorId(office.getId());
		//			doorOrderInfo.setDoorName(office.getName());
		//		}
		doorOrderInfo.setDoorId(doorOrderInfo.getDoorId());
		doorOrderInfo.setDoorName(doorOrderInfo.getDoorName());

		doorOrderInfo.setAmount(doorOrderInfo.getAmount().replace(",", ""));

		User user = new User();
		user.setId(doorOrderInfo.getUserId());
		doorOrderInfo.setCreateBy(user);
		doorOrderInfo.setUpdateBy(user);

		//判断今天此门店是否已经有过预约
		DoorOrderInfo doorOrderInfo1 = doorOrderInfoService.getByorderdate(doorOrderInfo.getOrderDate(),doorOrderInfo.getDoorId());
		if(doorOrderInfo1==null){
			//添加
			doorOrderInfo.setMethod(WeChatConstant.MethodType.METHOD_WECHAT);  //设置申请方式为微信端
			doorOrderInfoService.save(doorOrderInfo);
		}else{
			doorOrderInfo.setOrderId(doorOrderInfo1.getOrderId());//获得预约ID
			//修改
			doorOrderInfoService.updateAmount(doorOrderInfo);	
		}

		//addMessage(redirectAttributes, "保存预约明细操作成功");
		//return "redirect:"+Global.getAdminPath()+"/door/doorOrderInfo/?repage";

		//String doorName = (String) request.getAttribute("doorName");
		
		//		List<String> lists1 = Lists.newArrayList();
		//		lists1.add("笔数1");
		//		lists1.add("笔数2");
		//		lists1.add("笔数3");
		//		lists1.add("笔数4");
		//		model.addAttribute("list", lists1);
		//model.addAttribute("doorName", doorOrderInfo.getBranchName());

		//model.addAttribute("result","1");	//返回结果

		return "modules/sys/branchInfo";
	}


	/**
	 * 
	 * @author qph
	 * @version 2017年4月18日
	 * 
	 *    获取授权的申请      
	 * @param request
	 * @param response
	 * @return
	 */

	@RequestMapping(value = "authorizationSave")
	public String authorizationSave(Guest guest, HttpServletRequest request, HttpServletResponse response,Model model) {
		List<Guest> list = guestService.findListByOpenId(guest);
		//通过officeId获取所在机构名
		Office office1=officeService.get(guest.getGofficeId());
		guest.setGofficeName(office1.getName());
		guest.setMethod(WeChatConstant.MethodType.METHOD_WECHAT);				//将申请方式设置为微信端
		guest.setGrantstatus(WeChatConstant.Weixintype.TYPE_EXCEPTION);		//将状态改为未授权
		if(!Collections3.isEmpty(list)){
			if(list.get(0).getGrantDate()!= null){	
				guest.setGrantstatus(list.get(0).getGrantstatus());		//将状态改为已授权
			}
			guest.setGrantDate(list.get(0).getGrantDate());				//设置授权日期
			guest.setGuestType(list.get(0).getGuestType()); 			//设置用户类型
			guestService.updateByopenId(guest);
			return "modules/sys/authorization";
		}


		guestService.save(guest);

		return "modules/sys/authorization";
	}



	/**
	 * 
	 * @author QPH
	 * @version 2017年4月25日
	 * 
	 *          管理员用户确认预约信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "branchConfirm")
	public String branchConfirm(DoorOrderInfo doorOrderInfo, HttpServletRequest request, HttpServletResponse response,Model model) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", doorOrderInfo.getServiceNo());
		map.put("versionNo", doorOrderInfo.getVersionNo());
		String userId=request.getParameter("userId");
		if(doorOrderInfo.getDoorId() == null || doorOrderInfo.getDoorId().isEmpty()){
			//model.addAttribute("errorMessage", "此关注者无权上门收款预约，请先在上门收款公众号管理平台注册！");
			//return "modules/sys/error";
			throw new IllegalArgumentException("error");  
		}
		doorOrderInfo.setDoorId(doorOrderInfo.getDoorId());
		//获取系统当前时间
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		doorOrderInfo.setOrderDate(dateString);
		User user = new User();
		user.setId(userId);
		doorOrderInfo.setUpdateBy(user);
		//确认
		doorOrderInfoService.confirm(doorOrderInfo);
		return "modules/sys/branchInfo";
	}
	
	
	
//	/**
//	 * 
//	 * @author QPH
//	 * @version 2017年5月4日
//	 * 
//	 *          Sign签名算法(请求)
//	 * @param 
//	 * @param 
//	 * @return
//	 */
//	public static String getSign(String date,String businesstype){
//		String redirect_uri = Global.getConfig("redirectURLbysign");
//		redirect_uri = redirect_uri.replace("BUSINESSTYPE", businesstype);
//		Map<String,String> map =new TreeMap<>();
//		map.put("appId", Global.getConfig("appID"));
//		map.put("ts",date);
//		map.put("redirect_uri",redirect_uri);
//		map.put("scope", "snsapi_base");
//		StringBuffer sign = new StringBuffer("");
//		Iterator<String> iter = map.keySet().iterator();
//		while (iter.hasNext()) {
//			String key1 = iter.next();	   
//			sign.append(map.get(key1)); 
//		}
//		sign.append(KEY);
//		String signmd5= new Md5Hash(sign.toString()).toString();
//		return signmd5;
//		//return sign.toString();
//	}
	
//	/**
//	 * 
//	 * @author QPH
//	 * @version 2017年5月11日
//	 * 
//	 *          Sign签名算法(返回)
//	 * @param 
//	 * @param 
//	 * @return
//	 */
//	public static String getSignback(Map<String, String> map){
//		StringBuffer sign = new StringBuffer("");
//		Iterator<String> iter = map.keySet().iterator();
//		while (iter.hasNext()) {
//			String key1 = iter.next();	   
//			sign.append(map.get(key1)); 
//		}
//		sign.append(KEY);
//		String signmd5= new Md5Hash(sign.toString()).toString();
//		return signmd5;
//		//return sign.toString();
//	}
	/**
	 * 
	 * @author QPH
	 * @version 2017年5月11日
	 * 
	 *          封装参数(用作sign签名算法--请求)
	 * @param 	
	 * @param 
	 * @return
	 */
	public static Map<String, String> getsignmap(String date,String businesstype){
		String redirect_uri = Global.getConfig("redirectURLbysign");
		redirect_uri = redirect_uri.replace("BUSINESSTYPE", businesstype);
		Map<String,String> map =new TreeMap<>();
		map.put("appId", Global.getConfig("appID"));
		map.put("ts",date);
		map.put("redirect_uri",redirect_uri);
		map.put("scope", "snsapi_base");
		return map;
		
	}
	
	/**
	 * 
	 * @author QPH
	 * @version 2017年5月11日
	 * 
	 *          封装参数(用作sign签名算法--返回)
	 * @param 	
	 * @param 
	 * @return
	 */
	public static Map<String, String> getsignbackmap(String openId,String subscribe,String ts){
		Map<String,String> map =new TreeMap<>();
		map.put("openId", openId);
		map.put("subscribe",subscribe);
		map.put("ts", ts);
		return map;
		
	}
	
	/**
	 * 检索压力测试
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "findStressTesting")
	@ResponseBody
	public String findStressTesting(HttpServletRequest request, HttpServletResponse response) {
		if("true".equals(Global.getConfig("weChat.system.Stress.Testing.open"))) {
			List<Guest> GuestList = guestService.findList(new Guest());
			// 获取到系统当前时间
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(currentTime);
			doorOrderInfoService.getByorderdate(dateString, GuestList.get(0).getGofficeId());
		}
		
		return "TRUE";
	}
	
	/**
	 * 预约保存压力测试
	 * @param doorOrderInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "branchSaveStressTesting")
	@ResponseBody
	public String branchSaveStressTesting(DoorOrderInfo doorOrderInfo, HttpServletRequest request, HttpServletResponse response,Model model) {
		if("true".equals(Global.getConfig("weChat.system.Stress.Testing.open"))) {
			doorOrderInfo.setAmount("1000000");
			doorOrderInfo.setAmountList("500000,300000,200000");
			doorOrderInfo.setRfidList("1000,2000,3000");
			doorOrderInfo.setDoorId("10000163"+doorNumber);
			doorOrderInfo.setDoorName("test"+doorNumber);
			doorNumber++;
			branchSave(doorOrderInfo, request, response, model);
		}
		return "TRUE";
	}
	
	/**
	 * 授权申请压力测试
	 * @param guest
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "authorizationSaveStressTesting")
	@ResponseBody
	public String authorizationSaveStressTesting(Guest guest, HttpServletRequest request, HttpServletResponse response,Model model) {
		if("true".equals(Global.getConfig("weChat.system.Stress.Testing.open"))) {
			guest.setGname("guest"+guestNumber);
			guest.setGofficeId("10000163");
			guest.setGofficeName("大佛寺");
			guest.setGidcardNo("123456789");
			guest.setGphone("1234567");
			guest.setOpenId("test"+guestNumber);
			guestNumber++;
			authorizationSave(guest, request, response, model);
		}
		return "TRUE";
	}
	
	/**
	 * 
	 * 
	 * @author qph
	 * @version 2018年5月17日 微信公众号接收信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/connect", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	// post方法用于接收微信服务端消息
	public void connectWeixin(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			request.setCharacterEncoding(WeChatConstant.DEFAULT_CHARACTER_ENCODING); // 微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
			response.setCharacterEncoding(WeChatConstant.DEFAULT_CHARACTER_ENCODING); // 在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；boolean
													// isGet =
													// request.getMethod().toLowerCase().equals("get");
			String signature = request.getParameter(WeChatConstant.WechatCommonCha.SIGNATURE);// 微信加密签名
			String timestamp = request.getParameter(WeChatConstant.WechatCommonCha.TIMESTAMP);// 时间戳
			String nonce = request.getParameter(WeChatConstant.WechatCommonCha.NONCE);// 随机数
			String echostr = request.getParameter(WeChatConstant.WechatCommonCha.ECHOSTR);// 随机字符串
			if (echostr == null) {
				// 获取消息返回
				String message = wechatAccountService.weiXinMessage(request);
				logger.info(message);
				out.write(message);

			} else { // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
				if (WechatMessageUtils.checkSignature(DNBX_TOKEN, signature, timestamp, nonce)) {
					logger.info("Connect the weixin server is successful.");
					out.write(echostr);
				} else {
					logger.error("Failed to verify the signature!");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			out.close();
			out = null; 
		}
	}
	
	
	/**
	 * 预约清分授权
	 * 
	 * @version 2018-05-17
	 * @author XL
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "orderClearAuthorization")
	public static void orderClearAuthorization(HttpServletRequest request, HttpServletResponse response, Model model) {
		String state = request.getRequestURI();
		String appId = Global.getConfig(WeChatConstant.WechatCommonCha.APPID);
		String requestUrl = Global.getConfig(WeChatConstant.WechatCommonCha.WEBAUTHOURL);
		String redirect_uri = Global.getConfig(WeChatConstant.WechatCommonCha.REDIRECTURL);
		// 传递业务类型
		redirect_uri = redirect_uri.replace(WeChatConstant.WechatCommonCha.BUSINESSTYPE, WeChatConstant.BusinessType.ORDER_CLEAR);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.APPIDUP, appId);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.REDIRECT_URI, redirect_uri);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.STATE, state);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.BRANCHVALUE,
				WeChatConstant.BranchValue.AUTHORIZATION);
		try {
			response.sendRedirect(requestUrl);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 预约清分
	 * 
	 * @version 2018-05-17
	 * @author XL
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "orderClear")
	public static void orderClear(HttpServletRequest request, HttpServletResponse response, Model model) {
		String state = request.getRequestURI();
		String appId = Global.getConfig(WeChatConstant.WechatCommonCha.APPID);
		String requestUrl = Global.getConfig(WeChatConstant.WechatCommonCha.WEBAUTHOURL);
		String redirect_uri = Global.getConfig(WeChatConstant.WechatCommonCha.REDIRECTURL);
		// 传递业务类型
		redirect_uri = redirect_uri.replace(WeChatConstant.WechatCommonCha.BUSINESSTYPE,
				WeChatConstant.BusinessType.ORDER_CLEAR);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.APPIDUP, appId);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.REDIRECT_URI, redirect_uri);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.STATE, state);
		requestUrl = requestUrl.replace(WeChatConstant.WechatCommonCha.BRANCHVALUE,
				WeChatConstant.BranchValue.ORDER_CLEAR);
		try {
			response.sendRedirect(requestUrl);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存预约信息
	 * 
	 * @version 2018-05-17
	 * @author XL
	 * @param orderClearMain
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "orderClearSave")
	@ResponseBody
	public String orderClearSave(OrderClearMain orderClearMain, HttpServletRequest request,
			HttpServletResponse response) {
		// 获取客户信息
		Guest guest = guestService.get(orderClearMain.getCreateBy().getId());
		// 登录用户
		User user = new User();
		// 设置用户id
		user.setId(guest.getId());
		// 设置用户名
		user.setName(guest.getGname());
		// 设置用户机构
		user.setOffice(officeService.get(guest.getGofficeId()));
		// ------------------设置预约信息------------------------
		// 设置申请类型
		orderClearMain.setMethod(WeChatConstant.MethodType.METHOD_WECHAT);
		// 设置当前用户
		orderClearMain.setCurrentUser(user);
		// 设置登记人
		orderClearMain.setRegisterBy(user.getId());
		// 设置创建人
		orderClearMain.setCreateBy(user);
		// 设置创建人姓名
		orderClearMain.setCreateName(guest.getGname());
		// 设置更新人
		orderClearMain.setUpdateBy(user);
		// 设置更新人姓名
		orderClearMain.setUpdateName(guest.getGname());
		// 保存预约信息
		orderClearService.save(orderClearMain);
		// 保存成功返回预约单号
		return orderClearMain.getInNo();
	}
	
	/**
	 * 根据预约单号查询预约信息
	 * 
	 * @version 2018-05-21
	 * @author XL
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "getByInNo")
	public String getByInNo(String inNo, String openId, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 预约清分信息
		OrderClearMain orderClearMain = orderClearService.get(inNo);
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		// 设置列表信息
		orderClearMain.setDenominationList(
				ClearCommonUtils.getDenominationList(orderClearMain.getOrderClearDetailList(), denomCtrl));
		// 当前登录人信息
		Guest guest = new Guest();
		// 设置openId
		guest.setOpenId(openId);
		// 查询用户
		guest = guestService.getByopenID(guest);
		model.addAttribute("userId", guest.getId());
		model.addAttribute("orderClearInfo", orderClearMain);
		return "modules/sys/orderClear";
	}
	
	/**
	 * 根据openId查询客户信息
	 * 
	 * @version 2018-05-21
	 * @author XL
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "getGuestByOpenId")
	public String getGuestByOpenId(String openId, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 当前登录人信息
		Guest guest = new Guest();
		// 设置openId
		guest.setOpenId(openId);
		// 查询用户
		guest = guestService.getByopenID(guest);
		model.addAttribute("Guest", guest);
		model.addAttribute("businesstype", guest.getBusType());
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		model.addAttribute("sysDate",dateString);
		model.addAttribute("openId",openId);
		return "modules/sys/authorization";
	}
	
	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @version 2018-05-22
	 * @author XL
	 * @param amount
	 * @return
	 */
	@RequestMapping(value = "/changRMBAmountToBig")
	@ResponseBody
	public String changRMBAmountToBig(@RequestParam(value = "amount", required = true) String amount) {
		Map<String, String> rtnMap = new HashMap<String, String>();
		double dAmount = Double.parseDouble(amount);
		// 转换为大写金额
		String strBigAmount = NumToRMB.changeToBig(dAmount);
		rtnMap.put("bigAmount", strBigAmount);
		return gson.toJson(rtnMap);
	}
}
