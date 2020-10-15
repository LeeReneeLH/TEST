package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecords;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecordsDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机具管理DAO接口
 * 
 * @author 机具管理
 * @version 2019-06-26
 */
@MyBatisDao
public interface EquipmentInfoDao extends CrudDao<EquipmentInfo> {

	/**
	 * 
	 * Title: checkDoorBinded
	 * <p>
	 * Description: 查询门店是否已绑定机具
	 * </p>
	 * 
	 * @author: lihe
	 * @param id
	 * @return EquipmentInfo 返回类型
	 */
	List<EquipmentInfo> checkDoorBinded(String id);

	/**
	 * 
	 * Title: getCountByConnStatus
	 * <p>
	 * Description: 按照机具连线状态查询机具数量
	 * </p>
	 * 
	 * @author: lihe
	 * @return EquipmentInfo 返回类型
	 */
	List<EquipmentInfo> getCountByConnStatus(EquipmentInfo equipmentInfo);

	/**
	 * Title: updateByCondition
	 * <p>
	 * Description: 条件更新设备信息
	 * </p>
	 * 
	 * @param equipmentInfo
	 *            设备信息
	 * @author yinkai
	 */
	void updateByCondition(EquipmentInfo equipmentInfo);

	/**
	 * 
	 * Title: getHistoryUseRecords
	 * <p>
	 * Description: 查询机具历史使用记录
	 * </p>
	 * 
	 * @author: lihe
	 * @return HistoryUseRecords 返回类型
	 */
	List<HistoryUseRecords> getHistoryUseRecords(HistoryUseRecords historyUseRecords);

	/**
	 * 
	 * Title: getDepositDetail
	 * <p>
	 * Description: 查询存款明细
	 * </p>
	 * 
	 * @author: lihe
	 * @param historyUseRecordsDetail
	 * @return List<HistoryUseRecordsDetail> 返回类型
	 */
	List<HistoryUseRecordsDetail> getDepositDetail(HistoryUseRecordsDetail historyUseRecordsDetail);

	/**
	 * Title: findEquipmentByMerchant
	 * <p>
	 * Description: 查询商户下所有设备
	 * </p>
	 *
	 * @author: yinkai
	 * @param office 商户
	 * @return 商户下所有设备列表
	 */
	List<EquipmentInfo> findEquipmentByMerchant(Office office);
}