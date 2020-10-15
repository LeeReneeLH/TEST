package com.coffer.businesses.modules.weChat.v03.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositCensusReport;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约金额明细
 * 
 * @author XL
 * @version 2019年7月23日
 */
@MyBatisDao
public interface DoorOrderAmountDao extends CrudDao<DoorOrderAmount> {

	/**
	 * 获取凭条金额明细
	 *
	 * @author XL
	 * @version 2019年7月25日
	 * @param id
	 * @param tickertape
	 * @return
	 */
	public List<DoorOrderAmount> getDetailList(@Param("id") String id, @Param("detailId") String detailId);

	/**
	 * detailId获取面值明细
	 *
	 * @author yinkai
	 * @version 2019年7月25日
	 * @param detailId
	 * @return
	 */
	// public List<DoorOrderAmount> getAmountList(@Param("detailId") String
	// detailId);

	/**
	 * detailId获取面值明细
	 *
	 * gzd 2020-05-27 交易报表功能移植
	 * 
	 * @author yinkai
	 * @version 2019年7月25日
	 * @param detailId
	 * @return
	 */
	public List<DoorOrderAmount> getAmountList(@Param("detailId") String detailId, @Param("types") String... types);

	/**
	 * 
	 * Title: getDepositCensusList
	 * <p>
	 * Description: 获取整体存款情况分析统计报表数据
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositCensusReport
	 * @return List<DepositCensusReport> 返回类型
	 */
	public List<DepositCensusReport> getDepositCensusList(DepositCensusReport depositCensusReport);
	
}