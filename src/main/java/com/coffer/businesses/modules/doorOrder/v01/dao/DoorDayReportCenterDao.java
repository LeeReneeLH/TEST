package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 账务日结DAO接口
 * @author QPH
 * @version 2017-09-04
 */
@MyBatisDao
public interface DoorDayReportCenterDao extends CrudDao<DoorDayReportCenter> {
	
	/**
	 * 
	 * @author QPH
	 * @version 2017年9月5日
	 * 
	 *          根据账务类型查询信息
	 * @param DayReportMain
	 * 
	 * @return
	 */
	public List<DoorDayReportCenter> getAccountByAccountsType(DoorDayReportCenter DayReportCenter);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 通过账务日结主表主键查询中心账务日结信息
	 * 
	 * @param reportMainId
	 *            对应账务日结主表主键
	 * @return
	 */
	public List<DoorDayReportCenter> getAccountByreportMainId(String reportMainId);
}