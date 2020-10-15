package com.coffer.businesses.modules.report.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.report.v01.entity.ManageReport;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 管理分析报表DAO接口
 * 
 * @author wqj
 * @version 2020-1-6
 */
@MyBatisDao
public interface ManageReportDao extends CrudDao<ManageReport> {
	
	/**
	 *
	 * Title: collectSituation
	 * <p>
	 * Description: 报表统计：收钞情况
	 * </p>
	 *
	 * @author: WQJ
	 * @return List<ManageReport> 返回类型
	 */
	public List<ManageReport> collectSituation(ManageReport manageReport);
	
	/**
	 *
	 * Title: collectSituation
	 * <p>
	 * Description: 报表统计：卡钞情况
	 * </p>
	 *
	 * @author: WQJ
	 * @return List<ManageReport> 返回类型
	 */
	public List<ManageReport> stuckCollectSituation(ManageReport manageReport);
	
	/**
	 *
	 * Title: collectSituation
	 * <p>
	 * Description: 报表统计：差错情况
	 * </p>
	 *
	 * @author: WQJ
	 * @return List<ManageReport> 返回类型
	 */
	public List<ManageReport> errorCollectSituation(ManageReport manageReport);
	
	
	/**
	 *
	 * Title: collectSituation
	 * <p>
	 * Description: 报表统计：差错情况统计总计值(总笔数,长款总额,短款总额)
	 * </p>
	 *
	 * @author: ZXK
	 * @return List<ManageReport> 返回类型
	 */
	public ManageReport errorCollectSituationTotal(ManageReport manageReport);
}