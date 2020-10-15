package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
* Title: Service35 
* <p>Description: 现金（预约、上缴）登记</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service35")
@Scope("singleton")
public class Service35 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	
	/**
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 * 
	 * 35 现金（预约、上缴）登记
	 * @param paramMap
	 *            现金（预约、上缴）登记信息
	 * @return 结果信息
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			// 取得现金（预约、上缴）登记信息
			AllAllocateInfo allAllocateInfoParam = this.getAllAllocateInfoCashRegistInfo(paramMap);
			if (allAllocateInfoParam == null) {
				// 参数异常
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(map);
			}
			
			if (StringUtils.isBlank(allAllocateInfoParam.getAllId()) && this.isOrderExit(allAllocateInfoParam)) {
				// 当前机构当日存在预约或上缴记录
				// 流水单号
				map.put(Parameter.SERIALORDER_NO_KEY, allAllocateInfoParam.getAllId());
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E29);
				return gson.toJson(map);
			}
			
			Iterator<String> keyIterator = allAllocateInfoParam.getAllAllocateItemMap().keySet().iterator();
			String strMapKey = "";
			while (keyIterator.hasNext()) {
				strMapKey = keyIterator.next();
				AllAllocateItem tempItem = allAllocateInfoParam.getAllAllocateItemMap().get(strMapKey);
				if (!this.checkGoodsExist(tempItem)) {
					String strGoodsKey = AllocationCommonUtils.getGoodsKey(tempItem);
					// 物品不存在
					map.put(Parameter.ERROR_GOODS_ID_KEY, strGoodsKey);
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E35);
					return gson.toJson(map);
				}
			}
			
			//现金操作：现金预约 
			if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(allAllocateInfoParam.getBusinessType())) {
				
				if (StringUtils.isNotBlank(allAllocateInfoParam.getAllId())) {
					// 取得预约信息
					AllAllocateInfo allocateInfo = allocationService.getAllocateBetween(allAllocateInfoParam.getAllId());
					
					if (allocateInfo == null || allocateInfo.getAllAllocateItemList().size() == 0) {
						// 流水单号
						map.put(Parameter.SERIALORDER_NO_KEY, allAllocateInfoParam.getAllId());
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E31);
						return gson.toJson(map);
					}
					// 检查预约信息是否可以修改
					String strCheckResult = this.checkCanUpdate(allocateInfo, allAllocateInfoParam.getBusinessType());
					
					if (strCheckResult != null) {
						// 流水单号
						map.put(Parameter.SERIALORDER_NO_KEY, allocateInfo.getAllId());
						// 业务种别
						map.put(Parameter.BUSINESS_TYPE_KEY, allAllocateInfoParam.getBusinessType());
						// 状态
						map.put(Parameter.STATUS_KEY, allocateInfo.getStatus());
						
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, strCheckResult);
						return gson.toJson(map);
					}
				}
				// 保存预约登记信息
				allocationService.saveOrderAllocationForInterFace(allAllocateInfoParam);
			} else if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfoParam.getBusinessType())) {
				//现金操作：现金上缴 
				if (StringUtils.isNotBlank(allAllocateInfoParam.getAllId())) {
					// 取得预约信息
					AllAllocateInfo allocateInfo = allocationService.getAllocateBetween(allAllocateInfoParam.getAllId());
					
					if (allocateInfo == null || allocateInfo.getAllAllocateItemList().size() == 0) {
						// 流水单号
						map.put(Parameter.SERIALORDER_NO_KEY, allAllocateInfoParam.getAllId());
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E31);
						return gson.toJson(map);
					}
					// 检查预约信息是否可以修改
					String strCheckResult = this.checkCanUpdate(allocateInfo, allAllocateInfoParam.getBusinessType());
					
					if (strCheckResult != null) {
						// 流水单号
						map.put(Parameter.SERIALORDER_NO_KEY, allocateInfo.getAllId());
						// 业务种别
						map.put(Parameter.BUSINESS_TYPE_KEY, allAllocateInfoParam.getBusinessType());
						// 状态
						map.put(Parameter.STATUS_KEY, allocateInfo.getStatus());
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, strCheckResult);
						return gson.toJson(map);
					}
				}
				// 保存上缴登记信息
				allocationService.saveHandinAllocationForInterFace(allAllocateInfoParam);
			}
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  取得现金（预约、上缴）登记信息
	 *  
	 * @param headInfo JSON 参数
	 * @return 现金（预约、上缴）信息
	 */
	private AllAllocateInfo getAllAllocateInfoCashRegistInfo(Map<String, Object> headInfo) {
		AllAllocateInfo rtnAllAllocateInfo = new AllAllocateInfo();
		
		// 取得网点机构Id
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("现金（预约、上缴）登记接口-------- 网点机构Id未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		}
				
		// 取得业务种别
		if (headInfo.get(Parameter.BUSINESS_TYPE_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.BUSINESS_TYPE_KEY).toString())) {
			logger.debug("现金（预约、上缴）登记接口-------- 业务种别未指定:" + headInfo.get(Parameter.BUSINESS_TYPE_KEY));
			return null;
		}
		// 业务种别类型检查
		if (!AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(headInfo.get(Parameter.BUSINESS_TYPE_KEY).toString()) 
				&& !AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(headInfo.get(Parameter.BUSINESS_TYPE_KEY).toString())) {
			logger.debug("现金（预约、上缴）登记接口-------- 业务种别不正确:" + headInfo.get(Parameter.BUSINESS_TYPE_KEY));
			return null;
		}	
		// 取得流水号
		if (headInfo.get(Parameter.SERIALORDER_NO_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString())) {
			rtnAllAllocateInfo.setAllId(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString());
		} 
		
		// 取得用户编号
		if (headInfo.get(Parameter.USER_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("现金（预约、上缴）登记接口-------- 用户编号:" + headInfo.get(Parameter.USER_ID_KEY));
			return null;
		}
		// 取得系统登录用户姓名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("现金（预约、上缴）登记接口-------- 系统登录用户姓名:" + headInfo.get(Parameter.USER_NAME_KEY));
			return null;
		}
		
		List<AllAllocateItem> itemList = this.getAllAllocateItemList(headInfo);
		if (itemList == null) {
			return null;
		}
		String strMapKey = null;
		for (AllAllocateItem item : itemList) {
			strMapKey = allocationService.getAllAllocateItemMapKey(item);
			
			item.setAllItemsId(IdGen.uuid());
			//item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			// 状态标识
			item.setDelFlag(AllocationConstant.deleteFlag.Valid);
			// 物品信息列表
			rtnAllAllocateInfo.getAllAllocateItemMap().put(strMapKey, item);
		}
		
		// 网点机构ID
		String officeId = headInfo.get(Parameter.OFFICE_ID_KEY).toString();
		Office loginUserOffice = StoreCommonUtils.getOfficeById(officeId);
		
		User loginUser = new User();
		//用户编号
		loginUser.setId(headInfo.get(Parameter.USER_ID_KEY).toString());
		//系统登录用户姓名
		loginUser.setName(headInfo.get(Parameter.USER_NAME_KEY).toString());
		// 登陆用户所在机构
		loginUser.setOffice(loginUserOffice);
		rtnAllAllocateInfo.setLoginUser(loginUser);
		// 业务种别
		rtnAllAllocateInfo.setBusinessType(headInfo.get(Parameter.BUSINESS_TYPE_KEY).toString());
		
		return rtnAllAllocateInfo;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月24日
	 * 
	 *  判断当天登陆机构是否有预约上缴记录
	 * @param allAllocateInfoparam 查询参数
	 * @return true：有， false：没有
	 */
	private boolean isOrderExit(AllAllocateInfo allAllocateInfoparam) {
		
		AllAllocateInfo tempAllAllocateInfo = new AllAllocateInfo();

		// 设置业务种别(现金上缴)
		List<String> businessTypeList = Lists.newArrayList();
		businessTypeList.add(allAllocateInfoparam.getBusinessType());
		tempAllAllocateInfo.setBusinessTypes(businessTypeList);

		// 初始化开始时间和结束时间
		tempAllAllocateInfo.setCreateTimeStart(new Date());
		tempAllAllocateInfo.setCreateTimeEnd(new Date());

		// 设置用户的机构号，只能查询当前机构
		tempAllAllocateInfo.setrOffice(allAllocateInfoparam.getLoginUser().getOffice());

		tempAllAllocateInfo = allocationService.orderIsExit(tempAllAllocateInfo);
		
		if (tempAllAllocateInfo != null) {
			allAllocateInfoparam.setAllId(tempAllAllocateInfo.getAllId());
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月22日
	 * 
	 *  取得物品信息列表
	 * @param headInfo CS上传信息
	 * @return null时物品明细出错，否则返回转换后的物品明细列表
	 */
	@SuppressWarnings("unchecked")
	private List<AllAllocateItem> getAllAllocateItemList(Map<String, Object> headInfo) {
		List<AllAllocateItem> rtnList = Lists.newArrayList();
		List<Map<String, Object>> itemMapList = null;
		// 物品信息列表
		if (headInfo.get(Parameter.LIST_KEY) != null) {
			itemMapList = (List<Map<String, Object>>) headInfo.get(Parameter.LIST_KEY);
			if (Collections3.isEmpty(itemMapList)) {
				logger.debug("库外调拨 -------- 取得物品明细列表：" + headInfo.get(Parameter.LIST_KEY));
				return null;
			}
		} else {
			logger.debug("库外调拨 -------- 取得物品明细列表：" + headInfo.get(Parameter.LIST_KEY));
			return null;
		}
		AllAllocateItem item = null;
		for (Map<String, Object> map : itemMapList) {
			// 币种
			if (map.get(Parameter.CURRENCY_KEY) == null || StringUtils.isBlank(map.get(Parameter.CURRENCY_KEY).toString())) {
				logger.debug("库外调拨-------- 币种未指定:" + map.get(Parameter.CURRENCY_KEY));
				return null;
			}
			// 币种 长度检查
			if (!checkLength(map.get(Parameter.CURRENCY_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_CURRENCY)) {
				logger.debug("库外调拨-------- 币种长度不正确:" + map.get(Parameter.CURRENCY_KEY));
				return null;
			}
			// 类别
			if (map.get(Parameter.CLASSIFICATION_KEY) == null || StringUtils.isBlank(map.get(Parameter.CLASSIFICATION_KEY).toString())) {
				logger.debug("库外调拨-------- 类别未指定:" + map.get(Parameter.CLASSIFICATION_KEY));
				return null;
			}
			
			// 类别 长度检查
			if (!checkLength(map.get(Parameter.CLASSIFICATION_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_CLASSIFICATION)) {
				logger.debug("库外调拨-------- 类别长度不正确:" + map.get(Parameter.CLASSIFICATION_KEY));
				return null;
			}
			// 套别
			if (map.get(Parameter.SETS_KEY) == null || StringUtils.isBlank(map.get(Parameter.SETS_KEY).toString())) {
				logger.debug("库外调拨-------- 套别未指定:" + map.get(Parameter.SETS_KEY));
				return null;
			}
			// 套别 长度检查
			if (!checkLength(map.get(Parameter.SETS_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_SETS)) {
				logger.debug("库外调拨-------- 套别长度不正确:" + map.get(Parameter.SETS_KEY));
				return null;
			}
			// 材质
			if (map.get(Parameter.CASH_KEY) == null || StringUtils.isBlank(map.get(Parameter.CASH_KEY).toString())) {
				logger.debug("库外调拨-------- 材质未指定:" + map.get(Parameter.CASH_KEY));
				return null;
			}
			// 材质 长度检查
			if (!checkLength(map.get(Parameter.CASH_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_CASH)) {
				logger.debug("库外调拨-------- 材质长度不正确:" + map.get(Parameter.CASH_KEY));
				return null;
			}
			// 面值
			if (map.get(Parameter.DENOMINATION_KEY) == null || StringUtils.isBlank(map.get(Parameter.DENOMINATION_KEY).toString())) {
				logger.debug("库外调拨-------- 面值未指定:" + map.get(Parameter.DENOMINATION_KEY));
				return null;
			}
			// 面值 长度检查
			if (!checkLength(map.get(Parameter.DENOMINATION_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_DENOMINATION)) {
				logger.debug("库外调拨-------- 面值长度不正确:" + map.get(Parameter.DENOMINATION_KEY));
				return null;
			}
			// 单位
			if (map.get(Parameter.UNIT_KEY) == null || StringUtils.isBlank(map.get(Parameter.UNIT_KEY).toString())) {
				logger.debug("库外调拨-------- 单位未指定:" + map.get(Parameter.UNIT_KEY));
				return null;
			}
			// 单位 长度检查
			if (!checkLength(map.get(Parameter.UNIT_KEY).toString(), ExternalConstant.GoodsParamCheck.INT_LENGTH_UNIT)) {
				logger.debug("库外调拨-------- 单位长度不正确:" + map.get(Parameter.UNIT_KEY));
				return null;
			}
			// （预约、上缴）数量
			if (map.get(Parameter.POINT_NUMBER_KEY) == null || StringUtils.isBlank(map.get(Parameter.POINT_NUMBER_KEY).toString())) {
				logger.debug("库外调拨-------- （预约、上缴）数量未指定:" + map.get(Parameter.POINT_NUMBER_KEY));
				return null;
			}
			// （预约、上缴）数量 数字检查
			if (!isNumber(map.get(Parameter.POINT_NUMBER_KEY).toString())) {
				logger.debug("库外调拨-------- （预约、上缴）数量不是数字:" + map.get(Parameter.POINT_NUMBER_KEY));
				return null;
			}
			// （预约、上缴）金额
			if (map.get(Parameter.POINT_AMOUNT_KEY) == null || StringUtils.isBlank(map.get(Parameter.POINT_AMOUNT_KEY).toString())) {
				logger.debug("库外调拨-------- （预约、上缴）金额未指定:" + map.get(Parameter.POINT_AMOUNT_KEY));
				return null;
			}
			// （预约、上缴）金额 检查
			if (!isBigDecimal(map.get(Parameter.POINT_AMOUNT_KEY).toString())) {
				logger.debug("库外调拨-------- （预约、上缴）金额不正确:" + map.get(Parameter.POINT_AMOUNT_KEY));
				return null;
			}
			
			BigDecimal amount = new BigDecimal(Double.parseDouble(map.get(Parameter.POINT_AMOUNT_KEY).toString()));
			
			String strMaxMoneyConfig = Global.getConfig(ExternalConstant.GoodsParamCheck.GOODS_MAX_MONEY_CONFIG_KEY);
			strMaxMoneyConfig = StringUtils.isBlank(strMaxMoneyConfig) ? ExternalConstant.GoodsParamCheck.GOODS_MAX_MONEY_DEFAULT_VALUE : strMaxMoneyConfig;
			BigDecimal maxMoneyConfig = new BigDecimal(Double.parseDouble(strMaxMoneyConfig));
			
			if (amount.compareTo(maxMoneyConfig) == 1) {
				logger.debug("库外调拨-------- （预约、上缴）金额最大值超出上限(" + strMaxMoneyConfig + "):" + map.get(Parameter.POINT_AMOUNT_KEY));
				return null;
			}
			
			item = new AllAllocateItem();
			// 币种
			item.setCurrency(map.get(Parameter.CURRENCY_KEY).toString());
			// 类别
			item.setClassification(map.get(Parameter.CLASSIFICATION_KEY).toString());
			// 套别 (套别如果为-，那么认为是外币，没有套别)
			item.setSets(ExternalConstant.NULL_SETS.equals(map.get(Parameter.SETS_KEY).toString()) ? "" : map.get(Parameter.SETS_KEY).toString());
			// 材质
			item.setCash(map.get(Parameter.CASH_KEY).toString());
			// 面值
			item.setDenomination(map.get(Parameter.DENOMINATION_KEY).toString());
			// 单位
			item.setUnit(map.get(Parameter.UNIT_KEY).toString());
			// （预约、上缴）数量
			item.setMoneyNumber(Long.parseLong(map.get(Parameter.POINT_NUMBER_KEY).toString()));
			// （预约、上缴）金额
			item.setMoneyAmount(amount);
			
			rtnList.add(item);
		}
		return rtnList;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  验证参数是否为BigDecimal类型
	 * @param strParam 待验证参数
	 * @return true：为BigDecimal类型，false：不是BigDecimal类型
	 */
	public static boolean isBigDecimal(String strParam) {  
		java.util.regex.Matcher match = null;
		if (isNumber(strParam) == true) {
			return true;
		}

		if (strParam.trim().indexOf(".") == -1) {
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[+-]?[0-9]*");
			match = pattern.matcher(strParam.trim());
		} else {
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[+-]?[0-9]+(\\.\\d{1,100}){1}");
			match = pattern.matcher(strParam.trim());
		}

		return match.matches();
    }
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  验正参数长度是否符合预期
	 * @param strParam 待验证参数
	 * @param iLength 待验证参数预期长度
	 * @return
	 */
	public static boolean checkLength(String strParam, int iLength) {
		if (strParam == null) {
			return false;
		}
		
		if (strParam.length() != iLength) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  判断预约信息是否可以修改
	 * @param allocateInfo 现金预约信息 
	 * @param strParamBusinessType 参数业务种别
	 * @return null 可以修改；否则返回对应错误编码
	 */
	private String checkCanUpdate(AllAllocateInfo allocateInfo, String strParamBusinessType) {
		// 流水号对应业务种别检查
		if (!allocateInfo.getBusinessType().equals(strParamBusinessType)) {
			logger.debug("现金（预约、上缴）登记接口-------- 流水单号(" + allocateInfo.getAllId() + ")对应业务种别:" + strParamBusinessType
					+ "与待修改业务种别：" + allocateInfo.getBusinessType() + "不符。");
			return ExternalConstant.HardwareInterface.ERROR_NO_E32;
		}
		//现金操作：现金预约 
		if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(strParamBusinessType)) {
			
			// 现金预约已配款时网点不能修改
			if (AllocationConstant.Status.CashOrderQuotaYes.equals(allocateInfo.getStatus())) {
				logger.debug("现金（预约、上缴）登记接口-------- 流水单号(" + allocateInfo.getAllId() + ")状态已变更："  + allocateInfo.getStatus());
				return ExternalConstant.HardwareInterface.ERROR_NO_E27;
			}
			
		} else {
			
			//现金操作：现金上缴
			// 已经上传箱袋信息，不能修改
			if (this.isBoxRegister(allocateInfo, AllocationConstant.InoutType.In) == true) {
				logger.debug("现金（预约、上缴）登记接口-------- 流水单号(" + allocateInfo.getAllId() + ")箱袋信息已上传，不能修改。");
				return ExternalConstant.HardwareInterface.ERROR_NO_E28;
			}
		}
		
		return null;
	}
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年11月9日
	 * 
	 *  判断箱袋信息是否存在
	 * @param allocateInfo 查询条件
	 * @param strInOutType 出入库类型=1：入库 0：出库
	 * @return true:有箱袋登记信息 ，false:无箱袋登记信息
	 */
	private boolean isBoxRegister(AllAllocateInfo allocateInfo, String strInOutType) {
		Office office = StoreCommonUtils.getOfficeById(allocateInfo.getrOffice().getId());
		
		AllAllocateInfo conditionInfo = new AllAllocateInfo();
		if (AllocationConstant.InoutType.Out.equals(strInOutType)) {
			conditionInfo.setaOffice(office); //接收机构
		} else {
			conditionInfo.setrOffice(office); //登记机构
		}
		
		//业务类型=库外箱袋调拨
		conditionInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Box_Handover);
		//出入库类型=1：入库 0：出库
		//conditionInfo.setInoutType(strInOutType);
		// 查询时间为当日，每日上缴仅1次
		conditionInfo.setCreateTimeStart(allocateInfo.getCreateDate());
		conditionInfo.setCreateTimeEnd(allocateInfo.getCreateDate());
		conditionInfo.setSearchDateStart(DateUtils.formatDate(
				DateUtils.getDateStart(allocateInfo.getCreateDate()), 
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		conditionInfo.setSearchDateEnd(DateUtils.formatDate(
				DateUtils.getDateEnd(allocateInfo.getCreateDate()), 
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		List<AllAllocateInfo> allocationBoxInfoList = allocationService.findAllocationList(conditionInfo);
		if (allocationBoxInfoList.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年1月19日
	 * 
	 *          根据物品编号取得物品价值，如果取不到价值，判定物品不存在
	 * @param AllAllocateItem
	 *            物品编号信息
	 * @return true：物品存在 false：物品不存在
	 */
	private boolean checkGoodsExist(AllAllocateItem tempItem) {
		// 金额换算
		String strGoodsKey = AllocationCommonUtils.getGoodsKey(tempItem);
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsKey);
		if (goodsValue == null) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  验证参数是否为数字
	 * @param strParam 待验证参数
	 * @return true：为数字，false：不是数字
	 */
	public static boolean isNumber(String strParam) {  
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");  
        java.util.regex.Matcher match = pattern.matcher(strParam.trim());  
        return match.matches();  
    }

}
