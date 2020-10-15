package com.coffer.businesses.modules.report.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessCount;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessDegree;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessMoneyAmount;
import com.coffer.businesses.modules.report.v01.entity.ReportCondition;
import com.coffer.core.common.persistence.BaseDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 调缴功能DAO接口
 * 
 * @author xp
 * @version 2017-8-29
 */
@MyBatisDao
public interface AllocateReportBusinessDao extends BaseDao {

	/**
	 * 报表业务量统计
	 * 
	 * @author xp
	 * @version 2017年8月25日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessDegree> findDegree(ReportCondition reportCondition);

	/**
	 * 报表总金额统计
	 * 
	 * @author xp
	 * @version 2017年8月25日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessCount> findCount(ReportCondition reportCondition);

	/**
	 * 报表物品金额统计
	 * 
	 * @author xp
	 * @version 2017年8月25日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessMoneyAmount> findGoods(ReportCondition reportCondition);

	/**
	 * 查询常规业务所有业务量
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月6日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessDegree> findByAllBusiness(ReportCondition reportCondition);

	/**
	 * 查询临时业务所有业务量
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月6日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessDegree> findByAllBusinessFromTemp(
			ReportCondition reportCondition);
	
	/**
	 * 下拨业务量及总金额查询
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月6日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessDegree> findByAllocate(ReportCondition reportCondition);
	
	/**
	 * 查询临时业务所有金额
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月21日
	 * 
	 * @param latticePointHandin
	 *            主表信息
	 * @return 报表数据
	 */
	public List<AllocateReportBusinessDegree> findByAllocateFromTemp(ReportCondition reportCondition);

	/**
	 * 首页显示现金业务的金额
	 * 
	 * @author xp
	 * @param allocateReportBusinessDegree
	 * @return 业务量数据
	 */
	public List<AllocateReportBusinessCount> findBusinessCount(
			ReportCondition reportCondition);

	/**
	 * 首页显示临时现金预约的金额
	 * 
	 * @author xp
	 * @param allocateReportBusinessDegree
	 * @return 业务量数据
	 */
	public List<AllocateReportBusinessCount> findTempBusinessCount(
			ReportCondition reportCondition);

	/**
	 * 首页显示登录用户所属机构下各常规业务及临时业务的状态
	 * 
	 * @author xp 2017-11-6
	 * @param reportCondition
	 * @return list
	 */
	public List<AllocateReportBusinessDegree> findBusinessStatus(ReportCondition reportCondition);

}