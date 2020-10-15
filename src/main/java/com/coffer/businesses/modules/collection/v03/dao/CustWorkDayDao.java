package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CustWorkDay;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户清点量(日)统计DAO接口
 * 
 * @author XL
 * @version 2017-09-04
 */
@MyBatisDao
public interface CustWorkDayDao extends CrudDao<CustWorkDay> {

	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          明细行的查看（面值列表）
	 * @param custWorkDay
	 * 
	 * @return
	 */
	public List<CustWorkDay> findDetailParList(CustWorkDay custWorkDay);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          明细行的查看（人员列表）
	 * @param custWorkDay
	 * 
	 * @return
	 */
	public List<CustWorkDay> findDetailManList(CustWorkDay custWorkDay);
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkDay
	 * 
	 * @return
	 */
	public List<CustWorkDay> findExcelList(CustWorkDay custWorkDay);
	
	
}