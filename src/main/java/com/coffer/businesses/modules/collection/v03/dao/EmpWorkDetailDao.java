package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.EmpWorkDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 员工工作量明细统计DAO接口
 * 
 * @author wanglin
 * @date 2017-09-04
 */
@MyBatisDao
public interface EmpWorkDetailDao extends CrudDao<EmpWorkDetail> {

	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          明细行的查看（面值列表）
	 * @param empWorkDetail
	 * 
	 * @return
	 */
	public List<EmpWorkDetail> findRowList(EmpWorkDetail empWorkDetail);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          Excel下载数据的取得
	 * @param empWorkDetail
	 * 
	 * @return
	 */
	public List<EmpWorkDetail> findExcelList(EmpWorkDetail empWorkDetail);
	
	
}