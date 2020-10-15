package com.coffer.external.service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentWarningsDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: Service0808
 * <p>
 * Description:设备报警上传接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.23
 */
@Component("Service0808")
@Scope("singleton")
public class Service0808 extends HardwardBaseService {

    @Autowired
    private EquipmentWarningsDao equipmentWarningsDao;

    @Autowired
    private EquipmentInfoDao equipmentInfoDao;

    @Autowired
  	private OfficeDao officeDao;
    @Override
    @Transactional
    public String execute(Map<String, Object> paramMap) {
        // 检查参数
        String checkResult = checkParam(paramMap);
        if (checkResult != null) {
            throw new BusinessException("E99", checkResult);
        }
        // 提取参数
        String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
        String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
        String warnTime = (String) paramMap.get(Parameter.WARN_TIME_KEY);
        String warnLevel = (String) paramMap.get(Parameter.WARN_LEVEL_KEY);
        String warnType = (String) paramMap.get(Parameter.WARN_TYPE_KEY);
        String warnCode = (String) paramMap.get(Parameter.WARN_CODE_KEY);
        String warnName = (String) paramMap.get(Parameter.WARN_NAME_KEY);
        String remarks = (String) paramMap.get(Parameter.REMARKS_KEY);
        String clearStatus = (String) paramMap.get(Parameter.CLEAR_STATUS);
        String printerStatus = (String) paramMap.get(Parameter.PRINTER_STATUS);
        String doorStatus = (String) paramMap.get(Parameter.DOOR_STATUS);
//        String connStatus = (String) paramMap.get(Parameter.CONN_STATUS_KEY);

        Date currentDate = new Date();
        EquipmentWarnings equipmentWarnings = new EquipmentWarnings();
       
        equipmentWarnings.setMachNo(eqpId);
        equipmentWarnings.setMachName(eqpId);
        equipmentWarnings.setWarnTime(DateUtils.parseTimestampToDate(warnTime));
        equipmentWarnings.setWarnTimeSearch(DateUtils.formatDateTime(DateUtils.parseTimestampToDate(warnTime)));
        equipmentWarnings.setWarnLevel(warnLevel);
        equipmentWarnings.setWarnType(warnType);
        equipmentWarnings.setWarnCode(warnCode);
        equipmentWarnings.setWarnName(warnName);
        equipmentWarnings.setRemarks(remarks);
        
        //预警信息
        equipmentWarnings.setClearStatus(clearStatus);
        equipmentWarnings.setPrinterStatus(printerStatus);
        equipmentWarnings.setDoorStatus(doorStatus);
        
        //查询哪台机具报警
        EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
        if (equipmentInfo != null) {
            equipmentWarnings.setOffice(equipmentInfo.getaOffice());
        }
        //验证当天该报警信息是否存在	以30秒间隔为准   预警信息相关关数据相同 则 改变 当前记录的报警时间
        List<EquipmentWarnings> list = equipmentWarningsDao.findDoorEqNow(equipmentWarnings);
        if(Collections3.isEmpty(list)){
        	equipmentWarnings.setId(IdGen.uuid());
        	equipmentWarnings.setCreateBy(new User());
            equipmentWarnings.setCreateDate(currentDate);
            equipmentWarnings.setUpdateBy(new User());
            equipmentWarnings.setUpdateDate(DateUtils.parseTimestampToDate(warnTime));
            equipmentWarningsDao.insert(equipmentWarnings);
            
            /* 发送机具报警异常消息通知  修改人：hzy 修改日期：2020-04-14 start */
    		List<String> paramsList = Lists.newArrayList();
    		User user = new User();
    		//判断机具是否绑定
    		if(equipmentInfo != null && StringUtils.equals(Constant.EquipmentStatus.BIND,equipmentInfo.getStatus()) && equipmentInfo.getVinOffice() != null && StringUtils.isNotEmpty(equipmentInfo.getVinOffice().getId())){
    			//内容
    			paramsList.add(equipmentInfo.getSeriesNumber());
    			paramsList.add(warnName);
    			
    			//机构名称为机构表中的 
    			Office office = officeDao.get(equipmentInfo.getaOffice().getId());
    			user.setName(office.getName());
    			SysCommonUtils.equWarnMessageQueueAdd(Constant.MessageType.EQUEXCEPTIONTYPE, Constant.IsUse.IS_NOT_USE, paramsList,	
    			equipmentInfo.getVinOffice().getId(),user);
    		}else{
    			//未绑定机具 错误信息 发送给平台   (先发给商资易汇平台)
    			if(equipmentInfo != null){
    				paramsList.add(equipmentInfo.getSeriesNumber());
    			}else{
    				paramsList.add(Constant.NoBindOffice.UNKNOWN);
    			}
    			paramsList.add(warnName);	
    			user.setName(Constant.NoBindOffice.NOBINDOFFICE);
    			SysCommonUtils.equWarnMessageQueueAdd(Constant.MessageType.EQUEXCEPTIONTYPE, Constant.IsUse.IS_NOT_USE, paramsList,	
    			Constant.OfficeId.SZYH,user);
    		}
    		 /* 发送机具报警异常消息通知  修改人：hzy 修改日期：2020-04-14 end */
            
        }else{
        	EquipmentWarnings eqw = list.get(0);
        	eqw.setUpdateDate(DateUtils.parseTimestampToDate(warnTime));
        	equipmentWarningsDao.update(eqw);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(Parameter.ERROR_NO_KEY, null);
        resultMap.put(Parameter.ERROR_MSG_KEY, null);
        resultMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        return setReturnMap(resultMap, serviceNo);
    }

    private String checkParam(Map<String, Object> paramMap) {
        String errorMsg;
        // 字符串空校验
        if (paramMap.get(Parameter.EQUIPMENT_ID_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.EQUIPMENT_ID_KEY))) {
            logger.debug("参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY)));
            errorMsg = "参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.WARN_TIME_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.WARN_TIME_KEY))) {
            logger.debug("参数错误--------" + Parameter.WARN_TIME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_TIME_KEY)));
            errorMsg = "参数错误--------" + Parameter.WARN_TIME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_TIME_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.WARN_LEVEL_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.WARN_LEVEL_KEY))) {
            logger.debug("参数错误--------" + Parameter.WARN_LEVEL_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_LEVEL_KEY)));
            errorMsg = "参数错误--------" + Parameter.WARN_LEVEL_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_LEVEL_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.WARN_TYPE_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.WARN_TYPE_KEY))) {
            logger.debug("参数错误--------" + Parameter.WARN_TYPE_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_TYPE_KEY)));
            errorMsg = "参数错误--------" + Parameter.WARN_TYPE_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_TYPE_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.WARN_CODE_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.WARN_CODE_KEY))) {
            logger.debug("参数错误--------" + Parameter.WARN_CODE_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_CODE_KEY)));
            errorMsg = "参数错误--------" + Parameter.WARN_CODE_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_CODE_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.WARN_NAME_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.WARN_NAME_KEY))) {
            logger.debug("参数错误--------" + Parameter.WARN_NAME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_NAME_KEY)));
            errorMsg = "参数错误--------" + Parameter.WARN_NAME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.WARN_NAME_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.CLEAR_STATUS) == null ||
        		StringUtils.isEmpty((String) paramMap.get(Parameter.CLEAR_STATUS))) {
        	logger.debug("参数错误--------" + Parameter.CLEAR_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.CLEAR_STATUS)));
        	errorMsg = "参数错误--------" + Parameter.CLEAR_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.CLEAR_STATUS));
        	return errorMsg;
        }
        if (paramMap.get(Parameter.PRINTER_STATUS) == null ||
        		StringUtils.isEmpty((String) paramMap.get(Parameter.PRINTER_STATUS))) {
        	logger.debug("参数错误--------" + Parameter.PRINTER_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.PRINTER_STATUS)));
        	errorMsg = "参数错误--------" + Parameter.PRINTER_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.PRINTER_STATUS));
        	return errorMsg;
        }
        if (paramMap.get(Parameter.DOOR_STATUS) == null ||
        		StringUtils.isEmpty((String) paramMap.get(Parameter.DOOR_STATUS))) {
        	logger.debug("参数错误--------" + Parameter.DOOR_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.DOOR_STATUS)));
        	errorMsg = "参数错误--------" + Parameter.DOOR_STATUS + ":" + CommonUtils.toString(paramMap.get(Parameter.DOOR_STATUS));
        	return errorMsg;
        }
        return null;
    }

    private String setReturnMap(Map<String, Object> map, String serviceNo) {
        map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
        map.put(Parameter.SERVICE_NO_KEY, serviceNo);
        return gson.toJson(map);
    }
}
