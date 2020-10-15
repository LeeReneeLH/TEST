package com.coffer.businesses.modules.store.v01.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoRouteDetailDao;
import com.coffer.businesses.modules.store.v01.dao.StoRouteInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 线路管理Service
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
@Service
@Transactional(readOnly = true)
public class StoRouteInfoService extends CrudService<StoRouteInfoDao, StoRouteInfo> {

	@Autowired
	private StoRouteInfoDao stoRouteInfoDao;
	@Autowired
	private StoRouteDetailDao stoRouteDetaildao;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public StoRouteInfo get(String routeId) {
		return stoRouteInfoDao.get(routeId);
	}

	/**
	 * 查询线路信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param page
	 * @param stoRouteInfo
	 * @return
	 */
	public Page<StoRouteInfo> findPage(Page<StoRouteInfo> page, StoRouteInfo stoRouteInfo) {

		// 筛选当前有效的线路
		stoRouteInfo.setDelFlag(StoRouteInfo.DEL_FLAG_NORMAL);
		// 查询常规路线
		stoRouteInfo.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);

		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		stoRouteInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o4", null));
		// 查询数据列表
		return super.findPage(page, stoRouteInfo);
	}

	/**
	 * 线路信息保存
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param stoRouteInfo
	 */
	@Transactional(readOnly = false)
	public void save(StoRouteInfo stoRouteInfo) {
		// 常规线路绑定人员车辆
		if (stoRouteInfo != null && StringUtils.isNotBlank(stoRouteInfo.getRouteType())
				&& Constant.RouteInfo.CONVENTIONAL_ROUTE.equals(stoRouteInfo.getRouteType())) {
			StoRouteInfo hisStoRouteInfo = null;
			// 新创建线路不会存在历史
			if (StringUtils.isNotBlank(stoRouteInfo.getId())) {
				hisStoRouteInfo = stoRouteInfoDao.get(stoRouteInfo.getId());
			}
			// 修改押运人员
			updateEscortInfoBinding(stoRouteInfo, hisStoRouteInfo);
		}

		// 保存主表信息
		stoRouteInfo.setDetailNum(stoRouteInfo.getStoRouteDetailList().size());
		if (StringUtils.isNotBlank(stoRouteInfo.getId())) {
			super.save(stoRouteInfo);

		} else {
			stoRouteInfo.setIsNewRecord(true);
			stoRouteInfo.setDelFlag(StoRouteInfo.DEL_FLAG_NORMAL);
			stoRouteInfo.setId(IdGen.getIdByTime());
			stoRouteInfo.setCreateDate(new Date());
			super.save(stoRouteInfo);
		}
		// 保存线路规划表信息
		stoRouteInfo.setRoutePlanId(IdGen.uuid());
		stoRouteInfoDao.presaveRoutePlan(stoRouteInfo);
		stoRouteInfoDao.saveRoutePlan(stoRouteInfo);
		// 删除原有明细
		stoRouteDetaildao.deleteRouteDetail(stoRouteInfo.getId());
		for (StoRouteDetail stoRouteDetail : stoRouteInfo.getStoRouteDetailList()) {
			if (stoRouteDetail.getRoute() == null) {
				// 明细表主键和附加主表主键
				stoRouteDetail.setId(IdGen.uuid());
				stoRouteDetail.setRoute(stoRouteInfo);
			}
			// 保存现有明细
			stoRouteDetaildao.insert(stoRouteDetail);
		}
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月29日
	 * 
	 *  追加理由：接口至修改押运人员，如果利用save方法，需要重新更新明细影响效率
	 * @param stoRouteInfo
	 */
	@Transactional(readOnly = false)
	public void saveInterface(StoRouteInfo stoRouteInfo) {
		super.save(stoRouteInfo);
	}

	/**
	 * 逻辑删除
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param routeId
	 */
	@Transactional(readOnly = false)
	public void delete(String routeId) {
		// 获取当前线路
		StoRouteInfo stoRouteInfo = get(routeId);
		// 押运人员信息1
		StoEscortInfo stoEscortInfo1 = stoRouteInfo.getEscortInfo1();
		if (stoEscortInfo1 != null && StringUtils.isNotBlank(stoEscortInfo1.getId())) {
			stoEscortInfo1.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
			stoEscortInfoService.updateBinding(stoEscortInfo1);
		}
		// 押运人员信息2
		StoEscortInfo stoEscortInfo2 = stoRouteInfo.getEscortInfo2();
		if (stoEscortInfo2 != null && StringUtils.isNotBlank(stoEscortInfo2.getId())) {
			stoEscortInfo2.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
			stoEscortInfoService.updateBinding(stoEscortInfo2);
		}
		
		// 删除原有明细
		stoRouteDetaildao.deleteRouteDetail(stoRouteInfo.getId());

		stoRouteInfoDao.deleteByRouteId(routeId);

		// 清理人员缓存
		 CacheUtils.remove(StoEscortInfoService.CACHE_STOESCORT_MAP);
	}

	/**
	 * 物理删除明细
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param routeId
	 */
	@Transactional(readOnly = false)
	public void delete(String routeId, String officeId) {
		stoRouteDetaildao.deleteByRouteIdAndOfficeId(routeId, officeId);
	}

	/**
	 * 根据网点查询线路信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param officeId
	 * @return
	 */
	public StoRouteInfo searchStoRouteInfoByOfficeId(String officeId) {

		List<StoRouteInfo> list = stoRouteInfoDao.searchStoRouteInfoByOffice(officeId);
		if (!Collections3.isEmpty(list)) {
			for (StoRouteInfo stoRouteInfo : list) {
				// 某个网点常规线路
				if (StringUtils.isNotBlank(stoRouteInfo.getRouteType())
						&& Constant.RouteInfo.CONVENTIONAL_ROUTE.equals(stoRouteInfo.getRouteType())) {
					return stoRouteInfo;
				}
			}
		}
		return null;
	}

	/**
	 * 获取新的临时路线名字
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @return
	 */
	public String getTemporaryRouteSeq() {

		// 当天日期
		Date date = new Date();
		// 当日开始时间
		Date startDate = DateUtils.getDateStart(date);
		// 当日结束时间
		Date endDate = DateUtils.getDateEnd(date);

		String maxName = stoRouteInfoDao.getMaxTemporarySeq(Constant.RouteInfo.TEMPORARY_ROUTE, startDate, endDate);
		int seqNo = 0;
		if (!StringUtils.isBlank(maxName)) {
			// 获取命名长度
			int number = Constant.RouteInfo.TEMPORARY_ROUTE_NAME_HEAD.length();
			if (number < maxName.length()) {
				seqNo = Integer.parseInt(maxName.substring(number));
			}
		}
		int length = Integer.valueOf(Global.getConfig("temporary.route.seqLength"));
		return Constant.RouteInfo.TEMPORARY_ROUTE_NAME_HEAD + StoreCommonUtils.fillSeqNo(seqNo + 1, length);
	}

	/**
	 * 除去常规线路已经绑定的网点信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description
	 * @param routeId
	 * @return
	 */
	public List<Office> getOfficeNotInRouteOffice(String routeId) {
		// 当前用户下所属机构
		//List<Office> list = UserUtils.getOfficeList();
		// 当前已经绑定线路的网点信息
		List<Office> routeOffice = stoRouteDetaildao.getConventionalRouteOfficeByrouteId(routeId);
		// 闲置的网点信息
		//list = Collections3.subtract(list, routeOffice);
		// 过滤掉无网点机构信息
		//list = removeBranchOffice(list);
		return routeOffice;
	}

//	/**
//	 * 删除无子节点的父节点信息
//	 * 
//	 * @author niguoyong
//	 * @version 2015-09-02
//	 * 
//	 * @param list
//	 * @param type
//	 *            子节点类型
//	 * @param parentType
//	 *            父节点类型
//	 * @return
//	 */
//	@SuppressWarnings("unused")
//	private List<Office> removeBranchOffice(List<Office> list, String type, String parentType) {
//
//		Set<String> set = Sets.newConcurrentHashSet();
//		// 获取存在子节点的父节点ID
//		for (Office office : list) {
//			if (type.equals(office.getType())) {
//				set.add(office.getParent().getId());
//			}
//		}
//		// 去除无子节点的父节点
//		for (int j = list.size() - 1; j >= 0; j--) {
//			// 过滤父节点
//			if (parentType.equals(list.get(j).getType())) {
//				boolean flag = true;
//				for (String temp : set) {
//					if (temp.equals(list.get(j).getId())) {
//						flag = false;
//						break;
//					}
//				}
//				// 除去不存在子节点的父节点
//				if (flag) {
//					list.remove(j);
//				}
//			}
//		}
//		// 顶级机构过滤掉
//		if (list.size() == 1) {
//			list.remove(0);
//		}
//		return list;
//	}

	/**
	 * 过滤无子节点的父节点信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * @param list
	 * @return
	 */
	private List<Office> removeBranchOffice(List<Office> list) {
		if (!Collections3.isEmpty(list)) {
			for (int j = list.size() - 1; j >= 0; j--) {
				Office office = list.get(j);
				// 网点节点保留
				if (!Constant.OfficeType.OUTLETS.equals(office.getType())) {
					// 过滤非网点节点是否存在子节点
					boolean flag = true;
					for (Office tempOffice : list) {
						// 判断机构存在子节点
						if (StringUtils.contains(tempOffice.getParentIds(), office.getId())) {
							// 保留存在子节点数据
							flag = false;
							break;
						}
					}
					// 无子节点
					if (flag) {
						list.remove(j);
					}
				}
			}
		}
		return list;
	}


	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description 线路修改绑定人员状态
	 * @param stoRouteInfo
	 * @param hisStoRouteInfo
	 */
//	public void updateEscortInfoBinding(StoRouteInfo stoRouteInfo, StoRouteInfo hisStoRouteInfo) {
//		// 历史人员
//		StoEscortInfo hisStoEscort1 = null;
//		StoEscortInfo hisStoEscort2 = null;
//		if (hisStoRouteInfo != null) {
//			hisStoEscort1 = hisStoRouteInfo.getEscortInfo1();
//			hisStoEscort2 = hisStoRouteInfo.getEscortInfo2();
//		}
//
//		// 当前人员
//		StoEscortInfo stoEscort1 = null;
//		StoEscortInfo stoEscort2 = null;
//		if (stoRouteInfo.getEscortInfo1() != null && StringUtils.isNotBlank(stoRouteInfo.getEscortInfo1().getId())) {
//			stoEscort1 = stoEscortInfoService.get(stoRouteInfo.getEscortInfo1().getId());
//			stoRouteInfo.setEscortInfo1(stoEscort1);
//		}
//		if (stoRouteInfo.getEscortInfo2() != null && StringUtils.isNotBlank(stoRouteInfo.getEscortInfo2().getId())) {
//			stoEscort2 = stoEscortInfoService.get(stoRouteInfo.getEscortInfo2().getId());
//			stoRouteInfo.setEscortInfo2(stoEscort2);
//		}
//
//		updateEscortInfoBinding(stoEscort1, hisStoEscort1);
//		updateEscortInfoBinding(stoEscort2, hisStoEscort2);
//
//		// 清理车辆缓存
//		 CacheUtils.remove(StoEscortInfoService.CACHE_STOESCORT_MAP);
//	}
	@Transactional(readOnly = false)
	public void updateEscortInfoBinding(StoRouteInfo stoRouteInfo, StoRouteInfo hisStoRouteInfo) {
		// 历史人员
		if (hisStoRouteInfo != null) {
			StoEscortInfo hisStoEscort1 = hisStoRouteInfo.getEscortInfo1();
			StoEscortInfo hisStoEscort2 = hisStoRouteInfo.getEscortInfo2();
			if (hisStoEscort1 != null && StringUtils.isNotBlank(hisStoEscort1.getId())) {
				// 释放原有押运人员1
				hisStoEscort1.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
				stoEscortInfoService.updateBinding(hisStoEscort1);
			}
			if (hisStoEscort2 != null && StringUtils.isNotBlank(hisStoEscort2.getId())) {
				// 释放原有押运人员2
				hisStoEscort2.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
				stoEscortInfoService.updateBinding(hisStoEscort2);
			}
		}

		// 当前人员
		if (stoRouteInfo.getEscortInfo1() != null && StringUtils.isNotBlank(stoRouteInfo.getEscortInfo1().getId())) {
			StoEscortInfo stoEscort1 = stoEscortInfoService.get(stoRouteInfo.getEscortInfo1().getId());
			// 绑定修改后押运人员1
			stoEscort1.setBindingRoute(Constant.Escort.BINDING_ROUTE);
			stoEscortInfoService.updateBinding(stoEscort1);
			stoRouteInfo.setEscortInfo1(stoEscort1);
		}
		if (stoRouteInfo.getEscortInfo2() != null && StringUtils.isNotBlank(stoRouteInfo.getEscortInfo2().getId())) {
			StoEscortInfo stoEscort2 = stoEscortInfoService.get(stoRouteInfo.getEscortInfo2().getId());
			// 绑定修改后押运人员2
			stoEscort2.setBindingRoute(Constant.Escort.BINDING_ROUTE);
			stoEscortInfoService.updateBinding(stoEscort2);
			stoRouteInfo.setEscortInfo2(stoEscort2);
		}
		
		// 清理车辆缓存
		CacheUtils.remove(StoEscortInfoService.CACHE_STOESCORT_MAP);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-02
	 * 
	 * @Description 线路修改绑定人员
	 * @param escortInfo
	 * @param hisEscortInfo
	 */
	private void updateEscortInfoBinding(StoEscortInfo escortInfo, StoEscortInfo hisEscortInfo) {

		if (escortInfo != null && hisEscortInfo != null) {
			// 修改前后都存在押运人员
			if (StringUtils.isNotBlank(escortInfo.getId()) && StringUtils.isNotBlank(escortInfo.getId())
					&& !escortInfo.getId().equals(hisEscortInfo.getId())) {
				// 绑定修改后押运人员
				escortInfo.setBindingRoute(Constant.Escort.BINDING_ROUTE);
				stoEscortInfoService.updateBinding(escortInfo);
				// 释放原有押运人员
				hisEscortInfo.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
				stoEscortInfoService.updateBinding(hisEscortInfo);
			}
		} else {// 线路存在没有绑定的人员
			// 修改后绑定人员，修改前未绑定人员，将人员绑定
			if (escortInfo != null && StringUtils.isNotBlank(escortInfo.getId())) {
				// 绑定押运人员
				escortInfo.setBindingRoute(Constant.Escort.BINDING_ROUTE);
				stoEscortInfoService.updateBinding(escortInfo);
			}
			// 修改前绑定押运人员，修改后未绑定押运人员，将人员释放
			if (hisEscortInfo != null && StringUtils.isNotBlank(hisEscortInfo.getId())) {
				// 释放押运人员
				hisEscortInfo.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
				stoEscortInfoService.updateBinding(hisEscortInfo);
			}
		}
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月22日
	 * 
	 *          接口查询使用
	 * @param stoRouteInfo
	 * @return
	 */
	public List<StoRouteInfo> findInterfaceList(StoRouteInfo stoRouteInfo) {
		return dao.findInterfaceList(stoRouteInfo);
	}
	
	/***
	 * 
	 * Title: findRouteInfoListByOfficeId
	 * <p>Description: 根据机构ID查询线路信息列表 </p>
	 * @author:     wangbaozhong
	 * @param officeId 机构ID
	 * @return 线路信息列表 
	 * List<StoRouteInfo>    返回类型
	 */
	public List<StoRouteInfo> findRouteInfoListByOfficeId(String officeId) {
		return stoRouteInfoDao.findRouteInfoListByOfficeId(officeId);
	}
	
	/**
	 * 
	 * Title: getRoutePlan
	 * <p>Description: 获取线路规划信息</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo
	 * @return 
	 * StoRouteInfo    返回类型
	 */
	@Transactional(readOnly = true)
	public StoRouteInfo getRoutePlan(StoRouteInfo stoRouteInfo) {
		return stoRouteInfoDao.getRoutePlan(stoRouteInfo);
	}
	
	/**
	 * 
	 * Title: saveCarLocation
	 * <p>Description: 保存接口上传的车辆实时位置信息</p>
	 * @author:     yanbingxu
	 * @param stoRouteInfo 
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void saveCarLocation(StoRouteInfo stoRouteInfo) {
		stoRouteInfoDao.saveCarLocation(stoRouteInfo);
	}
}
