package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryChange;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 历史更换DAO接口
 * 
 * @author ZXK
 * @version 2019-10-30
 */
@MyBatisDao
public interface HistoryChangeDao extends CrudDao<HistoryChange> {
	/**
	 * 获取机具历史更换记录列表
	 *
	 * @author ZXK
	 * @version 2019-10-30
	 * @param historyChange
	 * @return
	 */
	public List<HistoryChange> findEquiHistoryChangeList(HistoryChange historyChange);
	/**
	 * 根据机具id获取机具明细列表
	 *
	 * @author ZXK
	 * @version 2019-10-30
	 * @param historyChange
	 * @return
	 */
	public List<HistoryChange> getHistoryChangePageDetail(HistoryChange historyChange);
	
	/**
	 * 
	 * @author zxk
	 * @date 2019-10-31
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkDay
	 * 
	 * @return
	 */
	public List<HistoryChange> findExcelList(HistoryChange historyChange);
}
