package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllHandoverInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
* Title: Service45
* <p>Description: 人行交接接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service45")
@Scope("singleton")
public class Service45 extends HardwardBaseService {

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private PbocAllHandoverInfoService pbocAllHandoverInfoService;

	/** 判断重复提交静态列表：保存流水号 **/
//	private static List<String> interfaceList = Lists.newArrayList();
	    
	 /**
     * @author chengs
     * @version 2015年10月13日
     * 
     * 
     * 45 交接任务交接接口
     * @param paramMap 交接人员信息
     * @return 更新结果信息
     */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		Map<String, Object> respMap = new HashMap<String, Object>();
        // 版本号、服务代码
        respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
        respMap.put(Parameter.SERVICE_NO_KEY, serviceNo);

//        // 验证是否是重复提交
//        String serialorderNo = initInterface(paramMap, respMap, serviceNo);
//        if (Constant.FAILED.equals(serialorderNo)) {
//            return gson.toJson(respMap);
//        }
//
//        try {
            // 取得交接人员信息
            PbocAllAllocateInfo inputParam = getHandoverParam(paramMap);
            if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
                return gson.toJson(respMap);
            }
            
            if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(inputParam.getBusinessType())) {
            	// 复点出入库交接时，按照出入库类型更新交接表数据
            	// 更新交接表信息
                pbocAllHandoverInfoService.udateRCHandover(inputParam);
                // 插入交接人员信息
                pbocAllHandoverInfoService.insertHandoverUserDetail(inputParam);
                if (AllocationConstant.InOutCoffer.OUT.equals(inputParam.getInoutType())) {
                	// 复点出库交接后，状态改为 清分中
                    inputParam.setStatus(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS);
                } else {
                	// 更新调拨状态完成
                    inputParam.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
                }
                
            } else {
            	// 更新交接表信息
                pbocAllHandoverInfoService.udateHandover(inputParam);
                // 插入交接人员信息
                pbocAllHandoverInfoService.insertHandoverUserDetail(inputParam);
                // 业务状态为 申请上缴，代理上缴，调拨出库，销毁出库，调拨入库 时，交接后状态改为完成
                if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(inputParam.getBusinessType())
                		|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(inputParam.getBusinessType())
                		|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(inputParam.getBusinessType())
                		|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(inputParam.getBusinessType())
                		|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(inputParam.getBusinessType())
                		|| AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE.equals(inputParam.getBusinessType())) {
                	// 更新调拨状态完成
                    inputParam.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
                } else {
                	// 更新调拨状态待接收
                    inputParam.setStatus(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_ACCEPT_STATUS);
                }
                
            }
            pbocAllAllocateInfoService.updateAllocateConfirmStatus(inputParam);
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

            return gson.toJson(respMap);
//        } finally {
//            cleanSerialorderNo(serialorderNo);
//        }
	}

	/**
	 * 执行完成的流水号清除
	 * 
	 * @author: ChengShu
	 * @param inputParam
	 * @param param
	 * @return PbocAllAllocateInfo 返回类型
	 */
//	private void cleanSerialorderNo(String serialorderNo) {
//		interfaceList.remove(serialorderNo);
//	}
	
	/**
     * @author chengshu
     * @version 2016年06月16日
     * 
     *          重复调用接口屏蔽
     * @param requestMap
     * @param serviceNo
     * @return
     */
//    public String initInterface(Map<String, Object> headInfo, Map<String, Object> respMap, String serviceNo) {
//
//        // 流水号
//        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
//            logger.warn("输入参数错误：" + Parameter.SERIALORDER_NO_KEY + " 不存在或是空。");
//            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
//            return Constant.FAILED;
//
//        } else {
//            // 流水号+接口编号
//            String key = StringUtils.join(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)), serviceNo);
//            if (interfaceList.contains(key)) {
//                // 重复提交错误发生
//                logger.warn(StringUtils.join("重复提交错误:接口", serviceNo, "; 流水号:" + headInfo.get(Parameter.SERIALORDER_NO_KEY)));
//                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E44);
//                return Constant.FAILED;
//
//            } else {
//                // 保存当前流水号，验证是否重复提交
//                interfaceList.add(key);
//            }
//        }
//
//        // 返回流水号
//        return StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY));
//    }
    
    /**
     * @author chengshu
     * @version 2015年06月03日
     * 
     *  
     * 从参数取得交接人员信息(库外交接任务交接接口)
     * @param headInfo 交接人员信息
     * @return AllAllocateInfo 对象
     */
    @SuppressWarnings("unchecked")
    private PbocAllAllocateInfo getHandoverParam(Map<String, Object> headInfo) {

        // 移交人信息列表
        List<PbocAllHandoverUserDetail> handoverList = Lists.newArrayList();
        // 接收人信息列表
        List<PbocAllHandoverUserDetail> acceptList = Lists.newArrayList();
        // 授权人信息列表
        List<PbocAllHandoverInfo> managerList = Lists.newArrayList();

        PbocAllAllocateInfo inputParam = new PbocAllAllocateInfo();

        // 取得出入库类型
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)))) {
            return checkParamFaile(inputParam, Parameter.INOUT_TYPE_KEY);
        } else {
            inputParam.setInoutType(StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)));
        }

        // 取得流水号
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
            return checkParamFaile(inputParam, Parameter.SERIALORDER_NO_KEY);
        } else {
            inputParam.setAllId(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)));
        }

        // 用户ID
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)))) {
            return checkParamFaile(inputParam, Parameter.USER_ID_KEY);
        } else {
            inputParam.setUserId(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
        }

        // 用户名称
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)))) {
            return checkParamFaile(inputParam, Parameter.USER_NAME_KEY);
        } else {
            inputParam.setUserName(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
        }

        // 电文传入移交人信息
        List<Map<String, String>> handoverInputList = (List<Map<String, String>>) headInfo.get(Parameter.HANDOVER_LIST_KEY);
        // 设定移交人信息
        setHandoverUserInfo(inputParam, handoverInputList, handoverList, Parameter.HANDOVER_LIST_KEY);
        if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
            return inputParam;
        } else {
            inputParam.setHandoverList(handoverList);
        }

        // 电文传入接收人信息
        List<Map<String, String>> acceptInputList = (List<Map<String, String>>) headInfo.get(Parameter.ACCEPT_LIST_KEY);
        // 设定接收人信息
        setHandoverUserInfo(inputParam, acceptInputList, acceptList, Parameter.ACCEPT_LIST_KEY);
        if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
            return inputParam;
        } else {
            inputParam.setAcceptList(acceptList);
        }

        // 电文传入授权人信息
        List<Map<String, String>> managerInputList = (List<Map<String, String>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
        setManagerInfo(inputParam, managerInputList, managerList, Parameter.MANAGER_LIST_KEY);
        if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
            return inputParam;
        }
        // 设定业务类型
        String businessType = BusinessUtils.getBusinessTypeFromAllId(inputParam.getAllId());
        inputParam.setBusinessType(businessType);
        return inputParam;
    }
    
    /**
     * 设置交接人员信息
     * @author:     ChengShu
     * @param inputParam 电文参数保存实体
     * @param handoverInputList 电文传入人员列表
     * @param handoverList 保存人员信息列表
     * @param handoverType 人员类型
     * @return 
     * PbocAllAllocateInfo    返回类型
     */
    private PbocAllAllocateInfo setHandoverUserInfo(PbocAllAllocateInfo inputParam, List<Map<String, String>> handoverInputList,
            List<PbocAllHandoverUserDetail> handoverList, String handoverType) {
        // 设定交接人信息
        if (!Collections3.isEmpty(handoverInputList)) {

            PbocAllHandoverUserDetail userDetail = new PbocAllHandoverUserDetail();

            for (Map<String, String> map : handoverInputList) {
                
                if(null == map){
                    break;
                }
                userDetail = new PbocAllHandoverUserDetail();

                // 人员类型
                if(Parameter.HANDOVER_LIST_KEY.equals(handoverType)){
                    // 移交人
                    userDetail.setType(AllocationConstant.UserType.handover);
                }else{
                    // 接收人
                    userDetail.setType(AllocationConstant.UserType.accept);
                }

                // 人员ID
                if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)))) {
                    return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_ID_KEY));
                } else {
                    userDetail.setEscortId(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)));
                }

                // 人员名称
                if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_NAME_KEY)))) {
                    return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_NAME_KEY));
                } else {
                    userDetail.setEscortName(StringUtils.toString(map.get(Parameter.OPT_USER_NAME_KEY)));
                }

                // 交接方式
                if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_HAND_TYPE_KEY)))) {
                    return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_HAND_TYPE_KEY));
                } else {
                    userDetail.setHandType(StringUtils.toString(map.get(Parameter.OPT_USER_HAND_TYPE_KEY)));
                }

                handoverList.add(userDetail);
            }
        }
        
        return inputParam;
    }
    
    /**
     * 设置授权人员信息
     * @author:     ChengShu
     * @param inputParam 电文参数保存实体
     * @param managerInputList 电文传入授权人员列表
     * @param managerList 保存授权人员信息列表
     * @param handoverType 人员类型
     * @return 
     * PbocAllAllocateInfo    返回类型
     */
    private PbocAllAllocateInfo setManagerInfo(PbocAllAllocateInfo inputParam, List<Map<String, String>> managerInputList,
            List<PbocAllHandoverInfo> managerList, String handoverType) {
        // 设定移交人信息
        if (!Collections3.isEmpty(managerInputList)) {

            managerList = Lists.newArrayList();
            PbocAllHandoverInfo managerInfo = new PbocAllHandoverInfo();

            for (Map<String, String> map : managerInputList) {
                managerInfo = new PbocAllHandoverInfo();

                // 人员ID
                if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)))) {
                    return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_ID_KEY));
                } else {
                	User managerUser = UserUtils.getByLoginName(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)));
                    managerInfo.setManagerUserId(managerUser.getId());
                    managerInfo.setManagerUserName(managerUser.getName());
                }

                // 授权原因
                if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.REASON_KEY)))) {
                    return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.REASON_KEY));
                } else {
                    managerInfo.setManagerReason(StringUtils.toString(map.get(Parameter.REASON_KEY)));
                }

                managerList.add(managerInfo);
            }
            inputParam.setManagerList(managerList);
        }

        return inputParam;
    }
    
    /**
     * 电文传入参数验证失败返回
     * @author:     ChengShu
     * @param inputParam
     * @param param
     * @return 
     * PbocAllAllocateInfo    返回类型
     */
    private PbocAllAllocateInfo checkParamFaile(PbocAllAllocateInfo inputParam, String param) {
        inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
        logger.warn("输入参数错误：" + param + " 不存在或是空。");
        return inputParam;
    }
}
