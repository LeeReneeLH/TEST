package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoBoxInfoHistory;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 箱袋状态授权管理DAO接口
 * 
 * @author xp
 * @version 2017-7-12
 */
@MyBatisDao
public interface StoBoxInfoHistoryDao extends CrudDao<StoBoxInfoHistory> {

	/**
	 * @author niguoyong
	 * @date 2015-09-01
	 *
	 *       根据箱袋类型取得当前最大的箱袋编号
	 */
	public String getCurrentMaxBoxNo(String boxType, @Param("dbName") String dbName);

	/**
	 * @author niguoyong
	 * @date 2015-09-01
	 * 
	 *       根据箱号或RFID取得箱子信息
	 */
	public List<StoBoxInfoHistory> getBoxInfoByIdOrRfid(StoBoxInfoHistory stoBoxInfoHistory);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          箱袋绑定查询接口
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfoHistory> searchBoxInfoList(StoBoxInfoHistory stoBoxInfoHistory);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * 
	 * @param headInfo
	 * @return
	 */
	public int updateBoxDelflag(StoBoxInfoHistory stoBoxInfoHistory);

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          查询指定条件的箱袋信息
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfoHistory> findBoxList(StoBoxInfoHistory stoBoxInfoHistory);
	
	/**
	 * 
	 * @author SongYuanYang
	 * @version 2017-09-13
	 * 
	 *          根据rfid及箱号查询所有历史信息
	 * @param stoBoxInfoHistory
	 * @return
	 */
	public List<StoBoxInfoHistory> findListByRfidAndBoxNo(StoBoxInfoHistory stoBoxInfoHistory);
	
	/***
	 * 根据箱袋编号物理删除箱子历史信息
	 * @author WQJ
	 * @version 2019-1-9
	 */
	public int realDeleteHistory(String boxNo);
}