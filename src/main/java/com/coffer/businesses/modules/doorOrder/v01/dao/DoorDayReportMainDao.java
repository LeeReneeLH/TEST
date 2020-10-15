package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 账务日结DAO接口
 * 
 * @author QPH
 * @version 2017-09-08
 */
@MyBatisDao
public interface DoorDayReportMainDao extends CrudDao<DoorDayReportMain> {

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 按结账日期降序查询
	 * 
	 * @param dayReportMain
	 * @return
	 */
	public List<DoorDayReportMain> findListByReportDate(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月11日
	 * 
	 *          获取最新账务日结日期信息
	 * @return
	 */
	public Date getDayReportMaxDate(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月15日 更新状态
	 * 
	 * @param dayReportMain
	 * @return
	 */
	public int updateStatus(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年10月16日 根据年月日季度周进行分组查询(清分中心及平台)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DoorDayReportMain> findChartList(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年12月14日 根据开始时间结束时间查询数据(清分统计图用)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DoorDayReportMain> findChartaList(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年12月06日 根据年月日季度周进行分组查询(人行及商行)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DoorDayReportMain> findChartsList(DoorDayReportMain dayReportMain);

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月3日
	 * 
	 *          获取本次日结的上一次日结时间
	 * @return
	 */
	public Date getThisDayReportMaxDate(DoorDayReportMain dayReportMain);

	/**
	 * 清分中心下的商户和门店的日结信息和未代付信息dao dayReportMain
	 * 
	 * @author zhr
	 * @version 2019年8月27日
	 */
	public List<DayReportMain> findListByDateAndStatus(DayReportMain dayReportMain);

	/**
	 * 除了清分中心和商户的其他机构的日结信息和未代付信息dao dayReportMain
	 * 
	 * @author zhr
	 * @version 2019年8月27日
	 */
	public List<DayReportMain> findXiaListByDateAndStatus(DayReportMain dayReportMain);

}