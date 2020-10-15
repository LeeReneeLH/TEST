package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportGuest;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户账务日结DAO接口
 * 
 * @author QPH
 * @version 2017-09-04
 */
@MyBatisDao
public interface DoorDayReportGuestDao extends CrudDao<DoorDayReportGuest> {
	/**
	 * 
	 * @author QPH
	 * @version 2017年9月5日
	 * 
	 *          根据账务类型以及客户ID查询信息
	 * @param dayReportGuest
	 * 
	 * @return
	 */
	public List<DoorDayReportGuest> getAccountByAccountsType(DoorDayReportGuest dayReportGuest);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 通过账务日结主表主键查询客户账务日结信息
	 * 
	 * @param reportMainId
	 *            对应账务日结主表主键
	 * @return
	 */
	public List<DoorDayReportGuest> getAccountByreportMainId(String reportMainId);

	/**
	 * 
	 * @author sg
	 * @version 2017年12月20日 通过账务日结主表主键查询客户账务日结信息
	 * 
	 * @param reportMainId
	 *            对应账务日结主表主键
	 * @return
	 */
	public List<DoorDayReportGuest> findAccountByAccountsType(DoorDayReportGuest dayReportGuest);
}