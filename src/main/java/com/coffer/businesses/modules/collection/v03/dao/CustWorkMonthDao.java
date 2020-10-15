package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CustWorkMonth;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户清点量(月)统计DAO接口
 * 
 * @author wanglin
 * @date 2017-09-04
 */
@MyBatisDao
public interface CustWorkMonthDao extends CrudDao<CustWorkMonth> {

	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          明细行的查看（面值列表）
	 * @param custWorkMonth
	 * 
	 * @return
	 */
	public List<CustWorkMonth> findDetailParList(CustWorkMonth custWorkMonth);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          明细行的查看（人员列表）
	 * @param custWorkMonth
	 * 
	 * @return
	 */
	public List<CustWorkMonth> findDetailManList(CustWorkMonth custWorkMonth);
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkMonth
	 * 
	 * @return
	 */
	public List<CustWorkMonth> findExcelList(CustWorkMonth custWorkMonth);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年10月12日
	 * 
	 *          查找款箱拆箱月度列表
	 * @return
	 */
	public List<String> findMonthList(CustWorkMonth custWorkMonth);
	
}