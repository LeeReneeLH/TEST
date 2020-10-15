package com.coffer.businesses.modules.report.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.report.v01.entity.StoBoxInfoGraphEntity;
import com.coffer.core.common.persistence.BaseDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 
 * @author wh
 *
 */
@MyBatisDao
public interface BoxReportGraphDao extends BaseDao {

	/**
	 * 查询机构箱袋的类型和对应的数量
	 * 
	 * @author wh
	 * @version 2017年9月12日
	 * @param stoBoxInfoGraphEntity
	 * @return List<StoBoxInfoGraphEntity>
	 */
	public List<StoBoxInfoGraphEntity> findBoxNumGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity);

	/**
	 * 查询机构箱袋的状态和对应的数量
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @return
	 */
	public List<StoBoxInfoGraphEntity> findBoxStatusGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity);
}
