package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.screen.v03.entity.ClearScreenMain;
import com.coffer.businesses.modules.screen.v03.service.ClearScreenService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service0402
* <p>Description: 数字化大屏-获取清分中心业务量</p>
* @author wanglin
* @date 2018年01月31日 上午10:41:10
*/
@Component("Service0402")
@Scope("singleton")
public class Service0402 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;
	
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 获取清分中心业务量
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			String strOfficeId = "";
			if (paramMap != null && paramMap.get("officeId") != null
					&& StringUtils.isNotBlank(paramMap.get("officeId").toString())) {
				strOfficeId = paramMap.get("officeId").toString();
			}
			//获取到系统当前时间
			Date currentTime = new Date();
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			//String dateString = formatter.format(currentTime);

			ClearScreenMain clearMain = new ClearScreenMain();

			java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
			//获取上缴额(今日)
			clearMain = clearScreenService.findUpList(strOfficeId, currentTime);
			map.put("toDayBankUpAmount", clearMain.getInAmount());
			
			//商行上缴额取得(合计)
			clearMain = clearScreenService.findUpList(strOfficeId , null);
			map.put("totalBankUpAmount", clearMain.getInAmount());
			
			//商行取款额取得(今日)
			clearMain = clearScreenService.findBackList(strOfficeId, currentTime);
			map.put("toDayBankBackAmount", clearMain.getOutAmount());
			
			//商行取款额取得(合计)
			clearMain = clearScreenService.findBackList(strOfficeId, null);
			map.put("totalBankBackAmount", clearMain.getOutAmount());
			
			//清分差错额取得(今日)

			clearMain = clearScreenService.findErrorList(strOfficeId, currentTime);
			map.put("toDayClearErrorAmount", clearMain.getErrorMoney());
			
			//清分差错额取得(合计)
			clearMain = clearScreenService.findErrorList(strOfficeId, null);
			map.put("totalClearErrorAmount", clearMain.getErrorMoney());
			
			
			//清分中心清点额取得(今日)
			clearMain = clearScreenService.findClearList(strOfficeId, currentTime);
			double douAmount3 = Double.valueOf(clearMain.getCountAmount()) + Double.valueOf(clearMain.getReCountAmount()) ;
			map.put("toDayClearAmount", String.valueOf(df.format(douAmount3)));
			
			//清分中心清点额取得(合计)
			clearMain = clearScreenService.findClearList(strOfficeId, null);
			double douAmount4 = Double.valueOf(clearMain.getCountAmount()) + Double.valueOf(clearMain.getReCountAmount()) ;
			map.put("totalClearAmount", String.valueOf(df.format(douAmount4)));
			
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(map);
	}

}
