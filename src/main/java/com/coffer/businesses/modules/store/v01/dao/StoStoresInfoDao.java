package com.coffer.businesses.modules.store.v01.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.report.v01.entity.StoInfoReportEntity;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfoEntity;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 库存管理DAO接口
 * @author LLF
 * @version 2015-09-09
 */
@MyBatisDao
public interface StoStoresInfoDao extends CrudDao<StoStoresInfo> {
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月22日
	 * 
	 *  获取最新库存日期信息
	 * @return
	 */
	public Date getMaxStoreDate(StoStoresInfo stoStoresInfo);

	/**
	 * 根据物品Id取得最新的库存信息
	 * 
	 * @author niguoyong
	 * @return
	 */
	public StoStoresInfo getStoStoresInfoByGoodsId(@Param("goodsId") String goodsId, @Param("officeId") String officeId,
			@Param("excludeZeroFg") String excludeZeroFg, @Param("dbName") String dbName);
	
	/**
	 * 
	 * Title: getGraphData
	 * <p>Description: 获取库存报表变化数据</p>
	 * @author:     wangbaozhong
	 * @param entity
	 * @return 
	 * List<StoInfoReportEntity>    返回类型
	 */

	public List<StoInfoReportEntity> getGraphData(StoInfoReportEntity entity);
	
	/**
	 * @author:wh Title: findListGraph Description: 获取库存报表数据
	 * @param stoReportInfo
	 * @return List<StoReportInfo> 返回类型
	 */
	public List<StoStoresInfoEntity> findListGraph(StoStoresInfoEntity stoStoresInfo);
}