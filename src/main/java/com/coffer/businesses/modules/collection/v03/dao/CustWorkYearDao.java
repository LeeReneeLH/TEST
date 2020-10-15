package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CustWorkYear;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户清点量(年)统计DAO接口
 * 
 * @author wanglin
 * @date 2017-09-04
 */
@MyBatisDao
public interface CustWorkYearDao extends CrudDao<CustWorkYear> {

	/**
	 * 
	 * @author wanglin
	 * @date 	2017年9月4日
	 * 
	 *          明细行的查看（面值列表）
	 * @param custWorkYear
	 * 
	 * @return
	 */
	public List<CustWorkYear> findDetailParList(CustWorkYear custWorkYear);
	
	/**
	 * 
	 * @author wanglin
	 * @date 	2017年9月4日
	 * 
	 *          明细行的查看（人员列表）
	 * @param custWorkYear
	 * 
	 * @return
	 */
	public List<CustWorkYear> findDetailManList(CustWorkYear custWorkYear);
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @date 	2017年9月4日
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkYear
	 * 
	 * @return
	 */
	public List<CustWorkYear> findExcelList(CustWorkYear custWorkYear);
	
	/**
	 * 
	 * @author wanglin
	 * @date 	2017年10月12日
	 * 
	 *          查找款箱拆箱年度列表
	 * @return
	 */
	public List<String> findYearList(CustWorkYear custWorkYear);
	
}