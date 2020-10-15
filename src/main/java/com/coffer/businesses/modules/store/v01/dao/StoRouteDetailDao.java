package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.common.persistence.CrudDao;

/**
 * 线路绑定网点DAO接口
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
@MyBatisDao
public interface StoRouteDetailDao extends CrudDao<StoRouteDetail> {
	/**
	 * 物理删除
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 */
	public int deleteRouteDetail(String routeId) ;

	/**
	 * 网点与线路组合物理删除明细
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * @param id
	 * @return
	 */
	public int deleteByRouteIdAndOfficeId(@Param("routeId")String routeId, @Param("officeId")String officeId) ;



	/**
	 * 获取所有常规线路下的网点信息
	 * 
	 * @param routeId
	 *            是否除了此线路下的网点信息，is null查询所有常规线路下网点
	 * @return
	 */
	public List<Office> getConventionalRouteOfficeByrouteId(@Param("routeId")String routeId) ;
	
	/**
	 * 
	 * Title: findListByRouteId
	 * <p>Description: 按路线ID查询路线明细信息列表</p>
	 * @author:     wangbaozhong
	 * @param routeId 路线ID
	 * @return 路线明细信息列表
	 * List<StoRouteDetail>    返回类型
	 */
	public List<StoRouteDetail> findListByRouteId(@Param("routeId")String routeId);
	

}
