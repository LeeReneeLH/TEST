package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;
import java.util.Map;

import com.coffer.businesses.modules.collection.v03.entity.CustWorkQuarter;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户清点量(季)统计DAO接口
 * 
 * @author wanglin
 * @date 2017-09-04
 */
@MyBatisDao
public interface CustWorkQuarterDao extends CrudDao<CustWorkQuarter> {

	/**
	 * 
	 * @author wanglin
	 * @date 2017年9季4日
	 * 
	 *          明细行的查看（面值列表）
	 * @param custWorkQuarter
	 * 
	 * @return
	 */
	public List<CustWorkQuarter> findDetailParList(CustWorkQuarter custWorkQuarter);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9季4日
	 * 
	 *          明细行的查看（人员列表）
	 * @param custWorkQuarter
	 * 
	 * @return
	 */
	public List<CustWorkQuarter> findDetailManList(CustWorkQuarter custWorkQuarter);
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9季4日
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkQuarter
	 * 
	 * @return
	 */
	public List<CustWorkQuarter> findExcelList(CustWorkQuarter custWorkQuarter);
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年10季12日
	 * 
	 *          查找款箱拆箱季度列表
	 * @param custWorkQuarter
	 * @return
	 */
	public List<SelectItem> findQuarterList(CustWorkQuarter custWorkQuarter);
	
}