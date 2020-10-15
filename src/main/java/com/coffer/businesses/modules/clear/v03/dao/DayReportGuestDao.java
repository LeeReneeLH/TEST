package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.DayReportGuest;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户账务日结DAO接口
 * 
 * @author QPH
 * @version 2017-09-04
 */
@MyBatisDao
public interface DayReportGuestDao extends CrudDao<DayReportGuest> {
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
	public List<DayReportGuest> getAccountByAccountsType(DayReportGuest dayReportGuest);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 通过账务日结主表主键查询客户账务日结信息
	 * 
	 * @param reportMainId
	 *            对应账务日结主表主键
	 * @return
	 */
	public List<DayReportGuest> getAccountByreportMainId(String reportMainId);

	/**
	 * 
	 * @author sg
	 * @version 2017年12月20日 通过账务日结主表主键查询客户账务日结信息
	 * 
	 * @param reportMainId
	 *            对应账务日结主表主键
	 * @return
	 */
	public List<DayReportGuest> findAccountByAccountsType(DayReportGuest dayReportGuest);
}