package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service39
* <p>Description: 现金预约、上缴删除</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service39")
@Scope("singleton")
public class Service39 extends HardwardBaseService {
	
	@Autowired
	private AllocationService allocationService;
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月24日
	 * 
	 *  39 现金预约、上缴删除
	 * @param paramMap 参数
	 * @return 结果信息
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			// 取得查询条件
			AllAllocateInfo allAllocateInfoparam = this.delAllAllocateInfoByIdParamFromMap(paramMap);
			if (allAllocateInfoparam == null) {
				// 参数异常
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			} else {
				
				// 根据查询条件，取得调拨全部信息
				AllAllocateInfo allocationInfo = allocationService.getAllocateBetween(allAllocateInfoparam.getAllId());
				// 调拨信息不存在
				if (allocationInfo == null) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E34);
					return gson.toJson(map);
				}
				
				//现金操作：现金预约 
				if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(allocationInfo.getBusinessType())) {
					// 现金预约已配款时网点不能修改
					if (AllocationConstant.Status.CashOrderQuotaYes.equals(allocationInfo.getStatus())) {
						logger.debug("现金预约、上缴删除接口-------- 流水单号(" + allocationInfo.getAllId() + ")状态已变更："  + allocationInfo.getStatus());
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E30);
						map.put(Parameter.STATUS_KEY, allocationInfo.getStatus());
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						return gson.toJson(map);
					}
				} else if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allocationInfo.getBusinessType())) {
					// 已经上传箱袋信息，不能修改
					if (this.isBoxRegister(allocationInfo, AllocationConstant.InoutType.In) == true) {
						logger.debug("现金预约、上缴删除接口-------- 流水单号(" + allocationInfo.getAllId() + ")箱袋信息已上传，不能修改。");
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E33);
						map.put(Parameter.STATUS_KEY, allocationInfo.getStatus());
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						return gson.toJson(map);
					}
				}
				
				// 执行删除处理
				allocationInfo.setLoginUser(allAllocateInfoparam.getLoginUser());
				allocationService.deleteCashForInterFace(allocationInfo);
				
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
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
	 * @version 2015年12月24日
	 * 
	 *  取得 39 现金预约、上缴删除接口参数
	 * @param headInfo CS上传信息
	 * @return 列表查询条件
	 */
	private AllAllocateInfo delAllAllocateInfoByIdParamFromMap(Map<String, Object> headInfo) {
		AllAllocateInfo rtnAllAllocateInfo = new AllAllocateInfo();
		// 取得网点机构Id
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("现金预约、上缴删除接口-------- 网点机构Id未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		}
		
		// 取得流水单号
		if (headInfo.get(Parameter.SERIALORDER_NO_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString())) {
			logger.debug("现金预约、上缴删除接口-------- 流水单号未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		} 
		
		// 取得用户编号
		if (headInfo.get(Parameter.USER_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("现金预约、上缴删除接口-------- 用户编号:" + headInfo.get(Parameter.USER_ID_KEY));
			return null;
		}
		// 取得系统登录用户姓名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("现金预约、上缴删除接口-------- 系统登录用户姓名:" + headInfo.get(Parameter.USER_NAME_KEY));
			return null;
		}
		
		Office loginUserOffice = StoreCommonUtils.getOfficeById(headInfo.get(Parameter.OFFICE_ID_KEY).toString());
		// 流水单号
		rtnAllAllocateInfo.setAllId(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString());
		
		User loginUser = new User();
		//用户编号
		loginUser.setId(headInfo.get(Parameter.USER_ID_KEY).toString());
		//系统登录用户姓名
		loginUser.setName(headInfo.get(Parameter.USER_NAME_KEY).toString());
		// 登陆用户所在机构
		loginUser.setOffice(loginUserOffice);
		
		rtnAllAllocateInfo.setLoginUser(loginUser);
				
		return rtnAllAllocateInfo;
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
	
}
