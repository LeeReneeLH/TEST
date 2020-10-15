package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DayReportDoorMerchanDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * Title: TransferFundsService
 * 
 * @author lihe
 * @date 2019年8月27日
 */
@Service
public class TransferFundsService {

	@Autowired
	private DayReportDoorMerchanDao dayReportDoorMerchanDao;

	/**
	 * 清分中心下的商户和门店的日结信息和未代付信息service dayReportMain zhr
	 */
	/**
	 * 根据商户ID和日结ID，查商户日结具体信息
	 * 
	 * @author zhr
	 * @version 2019.08.27
	 */
	public List<DayReportDoorMerchan> getfindByMerchantIdAndReportId(DayReportDoorMerchan dayReportDoorMerchan) {

		List<DayReportDoorMerchan> list = Lists.newArrayList();
		DayReportDoorMerchan transferInfo = dayReportDoorMerchanDao.getMerTransferInfo(dayReportDoorMerchan);
		if (transferInfo == null) {
			return list;
		}
		list.add(transferInfo);
		return list;
	}

	/**
	 * 
	 * Title: getTransferFundsList
	 * <p>
	 * Description:小程序查询划款列表（查询当天及为划款记录）
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportMain
	 * @return List<DayReportMain> 返回类型
	 */
	public List<DayReportDoorMerchan> getTransferFundsList(DayReportDoorMerchan dayReportDoorMerchan) {
		List<String> paidStatusList = Lists.newArrayList();
		List<String> settlementTypeList = Lists.newArrayList();
		// 代付状态列表
		paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
		paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
		// 结算类型列表
		settlementTypeList.add(DoorOrderConstant.SettlementType.doorSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.traditionalSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
		dayReportDoorMerchan.setSettlementTypeList(settlementTypeList);
		dayReportDoorMerchan.setPaidStatusList(paidStatusList);
		return dayReportDoorMerchanDao.getTransferFundsList(dayReportDoorMerchan);
	}

	/**
	 * 
	 * Title: getTransferFundsTotal
	 * <p>
	 * Description: 查询划款应付款和实付款总额
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return DayReportDoorMerchan 返回类型
	 */
	public DayReportDoorMerchan getTransferFundsTotal(DayReportDoorMerchan dayReportDoorMerchan) {
		List<String> paidStatusList = Lists.newArrayList();
		List<String> settlementTypeList = Lists.newArrayList();
		// 代付状态列表
		paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
		paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
		// 结算类型列表
		settlementTypeList.add(DoorOrderConstant.SettlementType.doorSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.traditionalSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
		dayReportDoorMerchan.setSettlementTypeList(settlementTypeList);
		dayReportDoorMerchan.setPaidStatusList(paidStatusList);
		return dayReportDoorMerchanDao.getTransferFundsTotal(dayReportDoorMerchan);
	}

	/**
	 * 
	 * Title: getTransferFundsListByUser
	 * <p>
	 * Description:查询已办划款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportMain
	 * @return List<DayReportMain> 返回类型
	 */
	public List<DayReportDoorMerchan> getTransferFundsListByUser(DayReportDoorMerchan dayReportDoorMerchan) {
		List<String> settlementTypeList = Lists.newArrayList();
		// 结算类型列表
		settlementTypeList.add(DoorOrderConstant.SettlementType.doorSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.traditionalSave);
		settlementTypeList.add(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
		dayReportDoorMerchan.setSettlementTypeList(settlementTypeList);
		dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.HASPAID);
		return dayReportDoorMerchanDao.getTransferFundsListByUser(dayReportDoorMerchan);
	}

	/**
	 * 已办划款凭证图片上传
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param reportId
	 * @param officeId
	 * @param photoImg
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> saveTransferFundsPic(String reportId, String officeId, byte[] photoImg, User user) {
		Map<String, Object> jsonData = Maps.newHashMap();
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		// 日结ID
		dayReportDoorMerchan.setReportId(reportId);
		// 商户ID
		dayReportDoorMerchan.setOfficeId(officeId);
		// 查询划款列表
		List<DayReportDoorMerchan> dayReportDoorMerchanList = dayReportDoorMerchanDao.findList(dayReportDoorMerchan);
		if (Collections3.isEmpty(dayReportDoorMerchanList)) {
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E01);
			jsonData.put(Parameter.ERROR_MSG_KEY, "划款信息不存在！");
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		} else {
			dayReportDoorMerchan = dayReportDoorMerchanList.get(0);
			// 凭条照片
			dayReportDoorMerchan.setPhoto(new byte[0]);
			// dayReportDoorMerchan.setPhoto(photoImg);
			// 上传用户
			dayReportDoorMerchan.setUpdateBy(user);
			// 上传时间
			dayReportDoorMerchan.setUpdateDate(new Date());
			int i = dayReportDoorMerchanDao.update(dayReportDoorMerchan);
			if (i > 0) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
				jsonData.put(Parameter.ERROR_MSG_KEY, "图片保存失败,请重试！");
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			}
		}
		return jsonData;
	}

}