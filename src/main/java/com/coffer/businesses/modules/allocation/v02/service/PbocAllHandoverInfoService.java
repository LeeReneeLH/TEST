package com.coffer.businesses.modules.allocation.v02.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllHandoverInfoDao;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllHandoverUserDetailDao;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.google.common.collect.Lists;

/**
 * 人行交接管理Service
 * @author LLF
 * @version 2016-05-25
 */
@Service
@Transactional(readOnly = true)
public class PbocAllHandoverInfoService extends CrudService<PbocAllHandoverInfoDao, PbocAllHandoverInfo> {
    
    @Autowired
    private PbocAllHandoverInfoDao handoverInfoDao;

    @Autowired
    private PbocAllHandoverUserDetailDao handoverDetailDao;
    
    /**
     * 插入交接信息
     * @author:     ChengShu
     * @param handoverInfo 交接信息
     */
    public void insertHandover(PbocAllAllocateInfo inputParam) {

        PbocAllHandoverInfo handoverInfo = new PbocAllHandoverInfo();
        // UUID主键
        handoverInfo.setId(IdGen.uuid());
        handoverInfo.setAllId(inputParam.getAllId());

        // 库房扫描授权的场合
        if (null != inputParam.getManagerList()) {
            // 库房扫描授权人列表
            List<String> managerUserIdList = Lists.newArrayList();
            // 库房扫描授权人名称列表
            List<String> managerUserNameList = Lists.newArrayList();
            // 库房扫描授权时间列表
            List<String> managerHandTypeList = Lists.newArrayList();
            // 库房扫描授权原因列表
            List<String> managerReasonList = Lists.newArrayList();

            for (PbocAllHandoverInfo pbocHandoverInfo : inputParam.getManagerList()) {
                managerUserIdList.add(pbocHandoverInfo.getScanManagerUserId());
                managerUserNameList.add(pbocHandoverInfo.getScanManagerUserName());
                managerHandTypeList.add(pbocHandoverInfo.getScanManagerHandType());
                managerReasonList.add(pbocHandoverInfo.getScanManagerReason());
            }
            // 库房扫描授权人
            handoverInfo.setScanManagerUserId(Collections3.convertToString(managerUserIdList, Constant.Punctuation.COMMA));
            // 库房扫描授权人名称
            handoverInfo.setScanManagerUserName(Collections3.convertToString(managerUserNameList, Constant.Punctuation.COMMA));
            // 库房扫描授权时间
            handoverInfo.setScanManagerHandType(Collections3.convertToString(managerHandTypeList, Constant.Punctuation.COMMA));
            // 库房扫描授权原因
            handoverInfo.setScanManagerReason(Collections3.convertToString(managerReasonList, Constant.Punctuation.COMMA));
        }

        insertHandoverInfo(handoverInfo);
    }

    /**
     * 更新交接信息
     * @author:     ChengShu
     * @param handoverInfo 交接信息
     */
    @Transactional(readOnly = false)
    public int udateHandover(PbocAllAllocateInfo inputParam) {
        PbocAllHandoverInfo handoverInfo = new PbocAllHandoverInfo();

        handoverInfo.setAllId(inputParam.getAllId());
        handoverInfo.setAcceptDate(new Date());

        if (null != inputParam.getManagerList()) {

            // 授权人列表
            List<String> managerUserIdList = Lists.newArrayList();
            // 授权人名称列表
            List<String> managerUserNameList = Lists.newArrayList();
            // 授权方式列表
            List<String> managerHandTypeList = Lists.newArrayList();
            // 授权原因列表
            List<String> managerReasonList = Lists.newArrayList();

            // 授权的场合
            for (PbocAllHandoverInfo pbocHandoverInfo : inputParam.getManagerList()) {
                managerUserIdList.add(pbocHandoverInfo.getManagerUserId());
                managerUserNameList.add(pbocHandoverInfo.getManagerUserName());
                managerHandTypeList.add(pbocHandoverInfo.getManagerHandType());
                managerReasonList.add(pbocHandoverInfo.getManagerReason());
            }

            // 授权人
            handoverInfo.setManagerUserId(Collections3.convertToString(managerUserIdList, Constant.Punctuation.COMMA));
            // 授权人名称
            handoverInfo.setManagerUserName(Collections3.convertToString(managerUserNameList, Constant.Punctuation.COMMA));
            // 授权方式
            handoverInfo.setManagerHandType(Collections3.convertToString(managerHandTypeList, Constant.Punctuation.COMMA));
            // 授权原因
            handoverInfo.setManagerReason(Collections3.convertToString(managerReasonList, Constant.Punctuation.COMMA));
        }

        return udateHandoverInfoByAllId(handoverInfo);
    }
    
    /**
     * 更新复点交接信息
     * @author:     ChengShu
     * @param handoverInfo 交接信息
     */
    @Transactional(readOnly = false)
    public int udateRCHandover(PbocAllAllocateInfo inputParam) {
        PbocAllHandoverInfo handoverInfo = new PbocAllHandoverInfo();

        handoverInfo.setAllId(inputParam.getAllId());
        if (AllocationConstant.InOutCoffer.IN.equals(inputParam.getInoutType())) {
        	handoverInfo.setAcceptDate(new Date());
        } else {
        	handoverInfo.setRcInAcceptDate(new Date());
        }
       

        if (null != inputParam.getManagerList()) {

            // 授权人列表
            List<String> managerUserIdList = Lists.newArrayList();
            // 授权人名称列表
            List<String> managerUserNameList = Lists.newArrayList();
            // 授权方式列表
            List<String> managerHandTypeList = Lists.newArrayList();
            // 授权原因列表
            List<String> managerReasonList = Lists.newArrayList();

            // 授权的场合
            for (PbocAllHandoverInfo pbocHandoverInfo : inputParam.getManagerList()) {
                managerUserIdList.add(pbocHandoverInfo.getManagerUserId());
                managerUserNameList.add(pbocHandoverInfo.getManagerUserName());
                managerHandTypeList.add(pbocHandoverInfo.getManagerHandType());
                managerReasonList.add(pbocHandoverInfo.getManagerReason());
            }
            if (AllocationConstant.InOutCoffer.OUT.equals(inputParam.getInoutType())) {
            	// 复点出库授权
	            // 授权人
	            handoverInfo.setManagerUserId(Collections3.convertToString(managerUserIdList, Constant.Punctuation.COMMA));
	            // 授权人名称
	            handoverInfo.setManagerUserName(Collections3.convertToString(managerUserNameList, Constant.Punctuation.COMMA));
	            // 授权方式
	            handoverInfo.setManagerHandType(Collections3.convertToString(managerHandTypeList, Constant.Punctuation.COMMA));
	            // 授权原因
	            handoverInfo.setManagerReason(Collections3.convertToString(managerReasonList, Constant.Punctuation.COMMA));
            } else {
            	// 复点入库授权
            	 // 授权人
	            handoverInfo.setRcInManagerUserId(Collections3.convertToString(managerUserIdList, Constant.Punctuation.COMMA));
	            // 授权人名称
	            handoverInfo.setRcInManagerUserName(Collections3.convertToString(managerUserNameList, Constant.Punctuation.COMMA));
	            // 授权方式
	            handoverInfo.setRcInManagerHandType(Collections3.convertToString(managerHandTypeList, Constant.Punctuation.COMMA));
	            // 授权原因
	            handoverInfo.setRcInManagerReason(Collections3.convertToString(managerReasonList, Constant.Punctuation.COMMA));
            }
        }

        return udateHandoverInfoByAllId(handoverInfo);
    }
    
    /**
     * 插入交接详细信息
     * @author:     ChengShu
     * @param handoverUserDetail 交接信息
     */
    @Transactional(readOnly = false)
    public void insertHandoverUserDetail(PbocAllAllocateInfo inputParam) {
        
        PbocAllHandoverInfo pbocHandoverInfo = handoverInfoDao.getByAllId(inputParam.getAllId());

        // 插入移交人信息
        insertHandoverUser(inputParam.getHandoverList(), pbocHandoverInfo.getId(), inputParam.getInoutType());

        // 插入接收人信息
        insertHandoverUser(inputParam.getAcceptList(), pbocHandoverInfo.getId(), inputParam.getInoutType());
    }

    /**
     * 插入交接详细信息
     * @author:     ChengShu
     * @param handoverUserDetail 交接信息
     * @param handoverId 交接单号
     * @param inOutType 出入库类型
     */
    public void insertHandoverUser(List<PbocAllHandoverUserDetail> handoverUserDetailList, String handoverId, String inOutType) {

        for (PbocAllHandoverUserDetail userDetail : handoverUserDetailList) {
            userDetail.setId(IdGen.uuid());
            userDetail.setHandoverId(handoverId);
            userDetail.setInoutType(inOutType);
            
            insertHandoverUserDetail(userDetail);
        }
    }
    
    /**
     * 插入交接信息
     * @author:     ChengShu
     * @param handoverInfo 交接信息
     */
    public void insertHandoverInfo(PbocAllHandoverInfo handoverInfo) {
        handoverInfoDao.insertHandoverInfo(handoverInfo);
    }

    /**
     * 更新交接信息
     * @author:     ChengShu
     * @param handoverInfo 交接信息
     */
    public int udateHandoverInfoByAllId(PbocAllHandoverInfo handoverInfo) {
        return handoverInfoDao.udateHandoverInfoByAllId(handoverInfo);
    }

    /**
     * 插入交接详细信息
     * @author:     ChengShu
     * @param handoverUserDetail 交接信息
     */
    public void insertHandoverUserDetail(PbocAllHandoverUserDetail handoverUserDetail) {
        handoverDetailDao.insertHandoverUserDetail(handoverUserDetail);
    }
    
    /**
     * 
     * Title: getHandoverInfoByAllId
     * <p>Description: 按流水单号查询交接信息</p>
     * @author:     wangbaozhong
     * @param allId 流水单号
     * @return 
     * PbocAllHandoverInfo    返回类型
     */
    public PbocAllHandoverInfo getHandoverInfoByAllId (String allId) {
    	return handoverInfoDao.getByAllId(allId);
    }
     
}