package com.coffer.businesses.modules.store.v01.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 线路管理DAO接口
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
@MyBatisDao
public interface StoRouteInfoDao extends CrudDao<StoRouteInfo> {

	/**
	 * LF 根据线路ID查询线路
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param routeId
	 * @return
	 */
	public StoRouteInfo findByRouteId(String routeId);

	/**
	 * 获取临时路线当日最大序列
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @return
	 */
	public String getMaxTemporarySeq(@Param("routeType")String routeType, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

	/**
	 * 逻辑删除
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param routeId
	 * @return
	 */
	public int deleteByRouteId(String routeId);
	
	/**
	 * 根据网点查询路线信息
	 * 
	 * @param officeId
	 * @return
	 */
	public List<StoRouteInfo> searchStoRouteInfoByOffice(String officeId) ;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月22日
	 * 
	 *          接口查询使用
	 * @param stoRouteInfo
	 * @return
	 */
	public List<StoRouteInfo> findInterfaceList(StoRouteInfo stoRouteInfo);
	
	/***
	 * 
	 * Title: findRouteInfoListByOfficeId
	 * <p>Description: 根据机构ID查询线路信息列表 </p>
	 * @author:     wangbaozhong
	 * @param officeId 机构ID
	 * @return 线路信息列表 
	 * List<StoRouteInfo>    返回类型
	 */
	public List<StoRouteInfo> findRouteInfoListByOfficeId(@Param("officeId") String officeId);
	
	/**
	 * 
	 * Title: presaveRoutePlan
	 * <p>Description: 保存线路规划前使原纪录失效</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo
	 * @return 
	 * int    返回类型
	 */
	public int presaveRoutePlan(StoRouteInfo stoRouteInfo);
	
	/**
	 * 
	 * Title: saveRoutePlan
	 * <p>Description: 保存线路规划信息</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo
	 * @return 
	 * int    返回类型
	 */
	public int saveRoutePlan(StoRouteInfo stoRouteInfo);
	
	/**
	 * 
	 * Title: getRoutePlan
	 * <p>Description: 查询线路规划历史信息</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo
	 * @return 
	 * StoRouteInfo    返回类型
	 */
	public StoRouteInfo getRoutePlan(StoRouteInfo stoRouteInfo);
	
	/**
	 * 
	 * Title: saveCarLocation
	 * <p>Description: 保存接口上传的车辆实时位置信息</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo
	 * @return 
	 * int    返回类型
	 */
	public int saveCarLocation(StoRouteInfo stoRouteInfo);
}
