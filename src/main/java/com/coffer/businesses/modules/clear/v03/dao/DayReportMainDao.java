package com.coffer.businesses.modules.clear.v03.dao;

import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 账务日结DAO接口
 * 
 * @author QPH
 * @version 2017-09-08
 */
@MyBatisDao
public interface DayReportMainDao extends CrudDao<DayReportMain> {

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月8日 按结账日期降序查询
	 * 
	 * @param dayReportMain
	 * @return
	 */
	public List<DayReportMain> findListByReportDate(DayReportMain dayReportMain);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月11日
	 * 
	 *          获取最新账务日结日期信息
	 * @return
	 */
	public Date getDayReportMaxDate(DayReportMain dayReportMain);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月15日 更新状态
	 * 
	 * @param dayReportMain
	 * @return
	 */
	public int updateStatus(DayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年10月16日 根据年月日季度周进行分组查询(清分中心及平台)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DayReportMain> findChartList(DayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年12月14日 根据开始时间结束时间查询数据(清分统计图用)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DayReportMain> findChartaList(DayReportMain dayReportMain);

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年12月06日 根据年月日季度周进行分组查询(人行及商行)
	 * @param dayReportMain
	 * 
	 * @return
	 */
	public List<DayReportMain> findChartsList(DayReportMain dayReportMain);

}