package com.coffer.businesses.modules.report.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.report.v01.entity.ClearReportAmount;
import com.coffer.core.common.persistence.BaseDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分业务报表DAO接口
 * 
 * @author xp
 * @version 2017-11-16
 */
@MyBatisDao
public interface ClearReportDao extends BaseDao {
	/**
	 * 清分业务根据用户所属机构显示对应的出入库金额（首页）
	 * 
	 * @author xp
	 * @version 2017-11-16
	 * @param clearReportAmount
	 * @return List
	 */
	public List<ClearReportAmount> findInOrOutAmount(ClearReportAmount clearReportAmount);

}