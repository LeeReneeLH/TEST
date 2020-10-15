package com.coffer.core.modules.sys.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoRouteInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.CityDao;
import com.coffer.core.modules.sys.dao.CountyDao;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.ProvinceDao;
import com.coffer.core.modules.sys.entity.CityEntity;
import com.coffer.core.modules.sys.entity.CountyEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.ProvinceEntity;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * 
* Title: MapService 
* <p>Description: 地图相关的逻辑层</p>
* @author wanghan
* @date 2017年11月28日 上午11:16:54
 */
@Service
@Transactional(readOnly = true)
public class MapService extends BaseService{
	/** 名称 KEY */
	private static final String ONLINE_MAP_KEY_NAME = "name"; 
	/** 车牌号 KEY */
	private static final String ONLINE_MAP_KEY_CARNO = "carNo";
	/** 经纬度数组 KEY */
	private static final String ONLINE_MAP_KEY_LNGLAT = "lnglat";
	/** 点数据 KEY */
	private static final String ONLINE_MAP_KEY_POINTS = "points";
	/** 线路数据 KEY */
	private static final String ONLINE_MAP_KEY_LINEDATA = "lineData";
	/** 是否为当前机构标识 KEY */
	private static final String ONLINE_MAP_KEY_IS_CURRENT_OFFICE = "isCurrentOffice";
	/** 点图片类型 KEY 使用机构类型*/
	private static final String ONLINE_MAP_KEY_POINT_TYPE = "pointType";
	/** 路线轨迹颜色KEY*/
	private static final String ONLINE_MAP_KEY_LINE_COLOR = "lineColor";
	/** 路线轨迹颜色VALUE*/
	private static final String ONLINE_MAP_VALUE_LINE_COLOR = "#F00";
	/** 规划线路颜色 */
	private static final String ROUTE_PLAN_COLOR = "routePlanColor";
	/** 车辆经过轨迹颜色 */
	private static final String CAR_TRACK_COLOR = "carTrackColor";
	
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private ProvinceDao proDao;
	@Autowired
	private CityDao cityDao;
	@Autowired
	private CountyDao countyDao;
	@Autowired
	private StoRouteInfoDao stoRouteInfoDao;
	
	/**
	 * 
	 * Title: makeMapGraphData
	 * <p>Description: 离线地图</p>
	 * @author:     wanghan
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> makeMapGraphData(){
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		List<String> strList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		//省份编码集合
		HashSet<String> proSet = new HashSet<String>();
		//城市编码集合
		HashSet<String> citySet = new HashSet<String>();
		
		// 获取当前用户
		User user = UserUtils.getUser();
		// 获取当前机构
		Office curOffice = SysCommonUtils.findOfficeById(user.getOffice().getId());
		
		// 顶级用户
		if (Constant.OfficeType.ROOT.equals(curOffice.getType())) {
			// 全国地图
			rtnMap = makeMapChinaOption();
		}
		
		// 平台用户
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(curOffice.getType())) {
			// 平台下的所有机构(包括平台)
			List<Office> associateOffice = officeDao.findByParentIdsLike(curOffice);

			for (Office office : associateOffice) {
				if (StringUtils.isNotBlank(office.getProvinceCode())) {
					proSet.add(office.getProvinceCode());
				}

				if (StringUtils.isNotBlank(office.getCityCode())) {
					citySet.add(office.getCityCode());
				}
			}

			int proNum = proSet.size();
			int cityNum = citySet.size();

			if (proNum == 0) {
				// 全国空地图
				rtnMap = makeMapChinaOption();
			} else if (proNum == 1) {

				if (cityNum == 0) {
					// 省级空地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);
				} else if (cityNum == 1) {
					// 市级地图
					String cityCode = "";
					Iterator<String> it = citySet.iterator();
					while (it.hasNext()) {
						cityCode = it.next();
					}
					rtnMap = makeMapCityOption(cityCode);
					List<Map<String, String>> lineList = Lists.newArrayList();
					
					for (Office office : associateOffice) {
						
						List<CountyEntity> list = Lists.newArrayList();
						
						if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
							list = countyDao.findCountyGenData(office);
						}else {
							list = countyDao.findCountyData(office);
						}
						
						CountyEntity currentCounty = null;

						List<CountyEntity> childCountyList = Lists.newArrayList();

						for (CountyEntity county : list) {

							// 获取地图 geoCoord数据
							if (!(geoCoordMap.containsKey(county.getOfficeName()))) {
								strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
								geoCoordMap.put(county.getOfficeName(), strList);
							}
							// 获取地图markPoint项的data数据
							// 当前机构
							if (county.getOfficeId().equals(office.getId())) {
								if(county.getOfficeId().equals(curOffice.getId())) {
									pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
									currentCounty = county;
									currentCounty.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
								}else {
									
									if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
										pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
										currentCounty = county;
										currentCounty.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
									}
									
									if(Constant.OfficeType.COFFER.equals(office.getType())) {
										pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
										currentCounty = county;
										currentCounty.setBankNum(Constant.MapPointValue.COFFER_VALUE);
									}
									
									if(Constant.OfficeType.OUTLETS.equals(office.getType())) {
										pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
										currentCounty = county;
									}
									
								}
								if(!(PointDataList.contains(pointMap))) {
									PointDataList.add(pointMap);
								}
							}

							// 子机构
							if (county.getParentId().equals(office.getId())) {
								if(Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
									pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
								}
								
								if(Constant.OfficeType.COFFER.equals(county.getType())) {
									pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
								}
								
								if(Constant.OfficeType.OUTLETS.equals(county.getType())) {
									pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
								}
								
								childCountyList.add(county);
								if(!(PointDataList.contains(pointMap))) {
									PointDataList.add(pointMap);
								}
							}
							
							//如果是人行有同级机构
							if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())
									&& county.getParentId().equals(office.getParentId())) {
								if (!(office.getId().equals(county.getOfficeId()))) {
									pointMap = getMarkPointData(county.getOfficeName(),
											Constant.MapPointValue.GENTRAL_VALUE);
									childCountyList.add(county);
									if(!(PointDataList.contains(pointMap))) {
										PointDataList.add(pointMap);
									}
								}
							}
						}

						// 获取地图makeline项的data数据
						if (currentCounty != null) {
							if (childCountyList.size() != 0) {
								for (CountyEntity childCounty : childCountyList) {
									lineList = getMarkLineData(currentCounty.getOfficeName(),
											childCounty.getOfficeName(), currentCounty.getBankNum());
									lineDataList.add(lineList);
								}
							}
						}

					}
				} else {
					// 省级地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);

					List<Map<String, String>> lineList = Lists.newArrayList();

					for (Office office : associateOffice) {
						
						List<CityEntity> list = Lists.newArrayList();
						
						if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
							list = cityDao.findCityGenData(office);
						}else {
							list = cityDao.findCityData(office);
						}
						
						CityEntity currentCity = null;
						List<CityEntity> childCityList = Lists.newArrayList();
						
						for (CityEntity city : list) {
							// 获取地图 geoCoord数据
							if (!(geoCoordMap.containsKey(city.getOfficeName()))) {
								strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
								geoCoordMap.put(city.getOfficeName(), strList);
							}
							
							// 获取地图markPoint项的data数据
							// 当前机构
							
							if (city.getOfficeId().equals(office.getId())) {
								if(city.getOfficeId().equals(curOffice.getId())) {
									pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
									currentCity = city;
									currentCity.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
								}else {
									if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
										pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
										currentCity = city;
										currentCity.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
									}
									
									if(Constant.OfficeType.COFFER.equals(office.getType())) {
										pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
										currentCity = city;
										currentCity.setBankNum(Constant.MapPointValue.COFFER_VALUE);
									}
									
									if(Constant.OfficeType.OUTLETS.equals(office.getType())) {
										pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
										currentCity = city;
									}
								}
								
								if(!(PointDataList.contains(pointMap))) {
									PointDataList.add(pointMap);
								}
							}

							// 子机构
							if (city.getParentId().equals(office.getId())) {
								
								if(Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
									pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
									
								}
								
								if(Constant.OfficeType.COFFER.equals(city.getType())) {
									pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
									
								}
								
								if(Constant.OfficeType.OUTLETS.equals(city.getType())) {
									pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
								}
								
								if(!(PointDataList.contains(pointMap))) {
									PointDataList.add(pointMap);
								}
								childCityList.add(city);
								
							}
							
							//如果是人行有同级机构
							if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())
									&& city.getParentId().equals(office.getParentId())) {
								if (!(office.getId().equals(city.getOfficeId()))) {
									pointMap = getMarkPointData(city.getOfficeName(),
											Constant.MapPointValue.GENTRAL_VALUE);
									childCityList.add(city);
									if(!(PointDataList.contains(pointMap))) {
										PointDataList.add(pointMap);
									}
								}
							}
						}

						// 获取地图makeline项的data数据
						if (currentCity != null) {
							if (childCityList.size() != 0) {
								for (CityEntity childCity : childCityList) {
									lineList = getMarkLineData(currentCity.getOfficeName(), childCity.getOfficeName(),
											currentCity.getBankNum());
									lineDataList.add(lineList);
								}
							}
						}
					}
				}
			} else {
				// 全国地图
				rtnMap = makeMapChinaOption();
				List<Map<String, String>> lineList = Lists.newArrayList();

				for (Office office : associateOffice) {
					List<ProvinceEntity> list = Lists.newArrayList();
					
					if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
						list = proDao.findProGenData(office);
					}else {
						list = proDao.findProvinceData(office);
					}
					ProvinceEntity currentPro = null;
					List<ProvinceEntity> childProList = Lists.newArrayList();

					for (ProvinceEntity pro : list) {

						// 获取地图 geoCoord数据
						if (!(geoCoordMap.containsKey(pro.getOfficeName()))) {
							strList = getGeoCoordData(pro.getLongitude(), pro.getLatitude());
							geoCoordMap.put(pro.getOfficeName(), strList);
						}
						// 获取地图markPoint项的data数据
						// 当前机构
						if (pro.getOfficeId().equals(office.getId())) {
							if(pro.getOfficeId().equals(curOffice.getId())) {
								pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
								currentPro = pro;
								currentPro.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							}else {
								if(Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
									pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
									currentPro = pro;
									currentPro.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
								}
								
								if(Constant.OfficeType.COFFER.equals(office.getType())) {
									pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
									currentPro = pro;
									currentPro.setBankNum(Constant.MapPointValue.COFFER_VALUE);
								}
								
								if(Constant.OfficeType.OUTLETS.equals(office.getType())) {
									pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
									currentPro = pro;
								}
							}
							if(!(PointDataList.contains(pointMap))) {
								PointDataList.add(pointMap);
							}
						}

						// 子机构
						if (pro.getParentId().equals(office.getId())) {
							if(Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
								pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							}
							
							if(Constant.OfficeType.COFFER.equals(pro.getType())) {
								pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							}
							
							if(Constant.OfficeType.OUTLETS.equals(pro.getType())) {
								pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);
							}
							childProList.add(pro);
							if(!(PointDataList.contains(pointMap))) {
								PointDataList.add(pointMap);
							}
						}
						
						//如果是人行有同级机构
						if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())
								&& pro.getParentId().equals(office.getParentId())) {
							if (!(office.getId().equals(pro.getOfficeId()))) {
								pointMap = getMarkPointData(pro.getOfficeName(),
										Constant.MapPointValue.GENTRAL_VALUE);
								childProList.add(pro);
								if(!(PointDataList.contains(pointMap))) {
									PointDataList.add(pointMap);
								}
							}
						}
					}

					// 获取地图makeline项的data数据
					if (currentPro != null) {

						if (childProList.size() != 0) {
							for (ProvinceEntity childPro : childProList) {
								lineList = getMarkLineData(currentPro.getOfficeName(), childPro.getOfficeName(), currentPro.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}
				}
			}
		}

		// 人行用户
		/*if (Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())) {
			// 获取与当前人行有业务来往的机构
			List<Office> officeList = officeDao.getAssoGenOffice(curOffice.getId());

			if (StringUtils.isNotBlank(curOffice.getProvinceCode())) {
				proSet.add(curOffice.getProvinceCode());
			}

			if (StringUtils.isNotBlank(curOffice.getCityCode())) {
				citySet.add(curOffice.getCityCode());
			}

			for (Office office : officeList) {
				if (StringUtils.isNotBlank(office.getProvinceCode())) {
					proSet.add(office.getProvinceCode());
				}

				if (StringUtils.isNotBlank(office.getCityCode())) {
					citySet.add(office.getCityCode());
				}
			}
			// 与当前人行有业务往来的机构的省份数量
			int proNum = proSet.size();
			// 与当前人行有业务往来的机构的城市数量
			int cityNum = citySet.size();

			if (proNum == 0) {
				// 全国空地图
				rtnMap = makeMapChinaOption();
			} else if (proNum == 1) {

				if (cityNum == 0) {
					// 省级空地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);
				} else if (cityNum == 1) {
					// 市级地图
					String cityCode = "";
					Iterator<String> it = citySet.iterator();
					while (it.hasNext()) {
						cityCode = it.next();
					}
					rtnMap = makeMapCityOption(cityCode);

					List<CountyEntity> parentList = countyDao.findGenParCountyData(curOffice.getId());
					List<CountyEntity> childList = countyDao.findGenChildCountyData(curOffice.getId());
					CountyEntity currentCounty = countyDao.findCurCountyOffice(curOffice.getId());

					List<Map<String, String>> lineList = Lists.newArrayList();

					if (currentCounty != null) {
						// 获取地图 geoCoord数据
						strList = getGeoCoordData(currentCounty.getLongitude(), currentCounty.getLatitude());
						geoCoordMap.put(currentCounty.getOfficeName(), strList);

						// 获取地图markPoint项的data数据
						pointMap = getMarkPointData(currentCounty.getOfficeName(),
								Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						currentCounty.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						PointDataList.add(pointMap);
					}

					for (CountyEntity county : parentList) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
						geoCoordMap.put(county.getOfficeName(), strList);
						// 获取地图markPoint项的data数据
						if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
							pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
							county.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
						}

						// 获取地图markline项的data数据
						if (currentCounty != null) {
							lineList = getMarkLineData(county.getOfficeName(), currentCounty.getOfficeName(),
									county.getBankNum());
							lineDataList.add(lineList);
						}

					}

					for (CountyEntity county : childList) {
						if (county != null) {
							// 获取地图 geoCoord数据
							strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
							geoCoordMap.put(county.getOfficeName(), strList);

							// 获取地图markPoint项的data数据
							if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(),
										Constant.MapPointValue.GENTRAL_VALUE);
								PointDataList.add(pointMap);
							}
							if (Constant.OfficeType.COFFER.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(),
										Constant.MapPointValue.COFFER_VALUE);
								PointDataList.add(pointMap);
							}
							// 获取地图makeline项的data数据
							if (currentCounty != null) {
								lineList = getMarkLineData(currentCounty.getOfficeName(), county.getOfficeName(),
										currentCounty.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}

				} else {
					// 省级地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);

					List<CityEntity> parentList = cityDao.findGenParCityData(curOffice.getId());
					List<CityEntity> childList = cityDao.findGenChildCityData(curOffice.getId());
					CityEntity currentCity = cityDao.findCurCityOffice(curOffice.getId());
					List<Map<String, String>> lineList;

					if (currentCity != null) {
						// 获取地图 geoCoord数据
						strList = getGeoCoordData(currentCity.getLongitude(), currentCity.getLatitude());
						geoCoordMap.put(currentCity.getOfficeName(), strList);
						// 获取地图markPoint项的data数据
						pointMap = getMarkPointData(currentCity.getOfficeName(),
								Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						currentCity.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						PointDataList.add(pointMap);
					}

					for (CityEntity city : parentList) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
						geoCoordMap.put(city.getOfficeName(), strList);

						// 获取地图markPoint项的data数据
						if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
							pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
							city.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
						}
						// 获取地图makeline项的data数据
						if (currentCity != null) {
							lineList = getMarkLineData(city.getOfficeName(), currentCity.getOfficeName(),
									city.getBankNum());
							lineDataList.add(lineList);
						}
					}

					for (CityEntity city : childList) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
						geoCoordMap.put(city.getOfficeName(), strList);
						// 获取地图markPoint项的data数据
						if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
							pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
						}
						if (Constant.OfficeType.COFFER.equals(city.getType())) {
							pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							PointDataList.add(pointMap);
						}
						// 获取地图makeline项的data数据
						if (currentCity != null) {
							lineList = getMarkLineData(currentCity.getOfficeName(), city.getOfficeName(),
									currentCity.getBankNum());
							lineDataList.add(lineList);
						}
					}
				}
			} else {
				// 全国地图
				rtnMap = makeMapChinaOption();

				List<ProvinceEntity> parentList = proDao.findGenParProData(curOffice.getId());
				List<ProvinceEntity> childList = proDao.findGenChildProData(curOffice.getId());
				ProvinceEntity currentPro = proDao.findCurProOffice(curOffice.getId());
				List<Map<String, String>> lineList;

				if (currentPro != null) {
					// 获取地图 geoCoord数据
					strList = getGeoCoordData(currentPro.getLongitude(), currentPro.getLatitude());
					geoCoordMap.put(currentPro.getOfficeName(), strList);
					// 获取地图markPoint项的data数据
					pointMap = getMarkPointData(currentPro.getOfficeName(),
							Constant.MapPointValue.CURRENT_OFFICE_VALUE);
					currentPro.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
					PointDataList.add(pointMap);
				}

				for (ProvinceEntity pro : parentList) {
					// 获取地图 geoCoord数据
					strList = getGeoCoordData(pro.getLongitude(), pro.getLatitude());
					geoCoordMap.put(pro.getOfficeName(), strList);
					// 获取地图markPoint项的data数据
					if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
						pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
						PointDataList.add(pointMap);
						pro.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
						PointDataList.add(pointMap);
					}

					// 获取地图makeline项的data数据
					if (currentPro != null) {
						lineList = getMarkLineData(pro.getOfficeName(), currentPro.getOfficeName(), pro.getBankNum());
						lineDataList.add(lineList);
					}
				}

				for (ProvinceEntity pro : childList) {

					// 获取地图 geoCoord数据
					strList = getGeoCoordData(pro.getLongitude(), pro.getLatitude());
					geoCoordMap.put(pro.getOfficeName(), strList);
					// 获取地图markPoint项的data数据
					if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
						pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
						PointDataList.add(pointMap);
					}
					if (Constant.OfficeType.COFFER.equals(pro.getType())) {
						pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
						PointDataList.add(pointMap);
					}
					// 获取地图makeline项的data数据
					if (currentPro != null) {
						lineList = getMarkLineData(currentPro.getOfficeName(), pro.getOfficeName(),
								currentPro.getBankNum());
						lineDataList.add(lineList);
					}
				}
			}
		}*/
			
		// 人行金库和网点用户
		if (Constant.OfficeType.COFFER.equals(curOffice.getType())
				|| Constant.OfficeType.OUTLETS.equals(curOffice.getType())
				|| Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
			
			List<Office> officeList = Lists.newArrayList();
			if(Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
				officeList = officeDao.getAssoGenOffice(curOffice.getId(), curOffice.getParentId());
			}else {
				officeList = officeDao.getProCityOffice(curOffice.getId());
			}
			

			for (Office office : officeList) {
				if (StringUtils.isNotBlank(office.getProvinceCode())) {
					proSet.add(office.getProvinceCode());
				}
				if (StringUtils.isNotBlank(office.getCityCode())) {
					citySet.add(office.getCityCode());
				}
			}

			int proNum = proSet.size();
			int cityNum = citySet.size();

			if (proNum == 0) {
				// 全国空地图
				rtnMap = makeMapChinaOption();
			} else if (proNum == 1) {

				if (cityNum == 0) {
					// 省级空地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);
				} else if (cityNum == 1) {
					// 市级地图
					String cityCode = "";
					Iterator<String> it = citySet.iterator();
					while (it.hasNext()) {
						cityCode = it.next();
					}
					rtnMap = makeMapCityOption(cityCode);
					
					List<CountyEntity> list = Lists.newArrayList();
					
					if(Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
						list = countyDao.findCountyGenData(curOffice);
					}else {
						list = countyDao.findCountyData(curOffice);
					}
					
					CountyEntity currentCounty = null;
					CountyEntity parentCounty = null;
					List<CountyEntity> childCountyList = Lists.newArrayList();

					for (CountyEntity county : list) {
						// 获取地图 geoCoord数据
						strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
						geoCoordMap.put(county.getOfficeName(), strList);
						// 获取地图markPoint项的data数据
						// 当前机构
						if (county.getOfficeId().equals(curOffice.getId())) {
							
							pointMap = getMarkPointData(county.getOfficeName(),
									Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							currentCounty = county;
							currentCounty.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							PointDataList.add(pointMap);
						}

						// 父机构
						if (county.getOfficeId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
								PointDataList.add(pointMap);
								parentCounty = county;
								parentCounty.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
							}
							if (Constant.OfficeType.COFFER.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
								PointDataList.add(pointMap);
								parentCounty = county;
								parentCounty.setBankNum(Constant.MapPointValue.COFFER_VALUE);
							}
						}

						// 子机构
						if (county.getParentId().equals(curOffice.getId())) {
							if(Constant.OfficeType.COFFER.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							}
							if(Constant.OfficeType.OUTLETS.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);	
							}
							if(Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);	
							}
							childCountyList.add(county);
							PointDataList.add(pointMap);
						}
						
						//如果当前用户是人行有同级人行调拨
						if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())
								&& !(curOffice.getId().equals(county.getOfficeId()))) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())
									&& county.getParentId().equals(curOffice.getParentId())) {
								pointMap = getMarkPointData(county.getOfficeName(),
										Constant.MapPointValue.GENTRAL_VALUE);
								childCountyList.add(county);
								PointDataList.add(pointMap);
							}
						}
						
					}

					// 获取地图makeline项的data数据
					if (currentCounty != null) {
						List<Map<String, String>> lineList = Lists.newArrayList();
						if (parentCounty != null) {
							lineList = getMarkLineData(parentCounty.getOfficeName(), currentCounty.getOfficeName(),
									parentCounty.getBankNum());
							lineDataList.add(lineList);
						}

						if (childCountyList.size() != 0) {
							for (CountyEntity childCounty : childCountyList) {
								lineList = getMarkLineData(currentCounty.getOfficeName(), childCounty.getOfficeName(),
										currentCounty.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}

				} else {
					// 省级地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);
					
					List<CityEntity> list = Lists.newArrayList();
					
					if(Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
						list = cityDao.findCityGenData(curOffice);
					}else {
						list = cityDao.findCityData(curOffice);
					}
					
					List<Map<String, String>> lineList;
					CityEntity currentCity = null;
					CityEntity parentCity = null;
					List<CityEntity> childCityList = Lists.newArrayList();

					for (CityEntity city : list) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
						geoCoordMap.put(city.getOfficeName(), strList);

						// 获取地图markPoint项的data数据
						// 当前机构
						if (city.getOfficeId().equals(curOffice.getId())) {
							pointMap = getMarkPointData(city.getOfficeName(),
									Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							currentCity = city;
							currentCity.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							PointDataList.add(pointMap);
						}

						// 父机构
						if (city.getOfficeId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
								PointDataList.add(pointMap);
								parentCity = city;
								parentCity.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
							}
							if (Constant.OfficeType.COFFER.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
								PointDataList.add(pointMap);
								parentCity = city;
								parentCity.setBankNum(Constant.MapPointValue.COFFER_VALUE);
							}
						}

						// 子机构
						if (city.getParentId().equals(curOffice.getId())) {
							if(Constant.OfficeType.COFFER.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							}
							if(Constant.OfficeType.OUTLETS.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);	
							}
							if(Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);	
							}
							childCityList.add(city);
							PointDataList.add(pointMap);
						}
						
						//如果当前用户是人行有同级人行调拨
						if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())
								&& !(curOffice.getId().equals(city.getOfficeId()))) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())
									&& city.getParentId().equals(curOffice.getParentId())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
								childCityList.add(city);
								PointDataList.add(pointMap);
							}
						}
						
					}

					// 获取地图makeline项的data数据
					if (currentCity != null) {
						if (parentCity != null) {
							lineList = getMarkLineData(parentCity.getOfficeName(), currentCity.getOfficeName(), parentCity.getBankNum());
							lineDataList.add(lineList);
						}

						if (childCityList.size() != 0) {
							for (CityEntity childCity : childCityList) {
								lineList = getMarkLineData(currentCity.getOfficeName(), childCity.getOfficeName(),
										currentCity.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}
				}

			} else {
				// 全国地图
				rtnMap = makeMapChinaOption();
				List<ProvinceEntity> list = Lists.newArrayList();
				
				if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
					list = proDao.findProGenData(curOffice);
				} else {
					list = proDao.findProvinceData(curOffice);
				}
				 
				ProvinceEntity currentPro = null;
				ProvinceEntity parentPro = null;
				List<ProvinceEntity> childProList = Lists.newArrayList();

				for (ProvinceEntity pro : list) {

					// 获取地图 geoCoord数据
					strList = getGeoCoordData(pro.getLongitude(), pro.getLatitude());
					geoCoordMap.put(pro.getOfficeName(), strList);
					// 获取地图markPoint项的data数据
					// 当前机构
					if (pro.getOfficeId().equals(curOffice.getId())) {
						pointMap = getMarkPointData(pro.getOfficeName(),
								Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						currentPro = pro;
						currentPro.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						PointDataList.add(pointMap);
					}

					// 父机构
					if (pro.getOfficeId().equals(curOffice.getParentId())) {
						if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
							parentPro = pro;
							parentPro.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
						}
						if (Constant.OfficeType.COFFER.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							PointDataList.add(pointMap);
							parentPro = pro;
							parentPro.setBankNum(Constant.MapPointValue.COFFER_VALUE);
						}
					}

					// 子机构
					if (pro.getParentId().equals(curOffice.getId())) {
						if(Constant.OfficeType.COFFER.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
						}
						if(Constant.OfficeType.OUTLETS.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.OUTLETS_VALUE);	
						}
						if(Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);	
						}
						childProList.add(pro);
						PointDataList.add(pointMap);
					}
					
					//如果当前用户是人行有同级人行调拨
					if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())
							&& !(curOffice.getId().equals(pro.getOfficeId()))) {
						if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())
								&& pro.getParentId().equals(curOffice.getParentId())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							childProList.add(pro);
							PointDataList.add(pointMap);
						}
					}
				}

				// 获取地图makeline项的data数据
				if (currentPro != null) {
					List<Map<String, String>> lineList = Lists.newArrayList();
					if (parentPro != null) {
						lineList = getMarkLineData(parentPro.getOfficeName(), currentPro.getOfficeName(), parentPro.getBankNum());
						lineDataList.add(lineList);
					}

					if (childProList.size() != 0) {
						for (ProvinceEntity childPro : childProList) {
							lineList = getMarkLineData(currentPro.getOfficeName(), childPro.getOfficeName(), currentPro.getBankNum());
							lineDataList.add(lineList);
						}
					}
				}
			}
		}
		
		// 清分中心用户
		if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
			// 获取与清分中心有业务往来的机构（包括清分中心本身）
			List<Office> officeList = officeDao.getAssWithClearOffice(curOffice.getId(),curOffice.getParentId());

			for (Office office : officeList) {
				if (StringUtils.isNotBlank(office.getProvinceCode())) {
					proSet.add(office.getProvinceCode());
				}

				if (StringUtils.isNotBlank(office.getCityCode())) {
					citySet.add(office.getCityCode());
				}

			}

			int proNum = proSet.size();
			int cityNum = citySet.size();

			if (proNum == 0) {
				// 全国空地图
				rtnMap = makeMapChinaOption();
			} else if (proNum == 1) {

				if (cityNum == 0) {
					// 省级空地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);
				} else if (cityNum == 1) {
					// 市级地图
					String cityCode = "";
					Iterator<String> it = citySet.iterator();
					while (it.hasNext()) {
						cityCode = it.next();
					}
					rtnMap = makeMapCityOption(cityCode);
					List<CountyEntity> list = countyDao.findClearCountyData(curOffice);
					CountyEntity currentCounty = null;
					CountyEntity parentCounty = null;
					List<CountyEntity> childCountyList = Lists.newArrayList();

					for (CountyEntity county : list) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
						geoCoordMap.put(county.getOfficeName(), strList);
						// 获取地图markPoint项的data数据
						// 当前机构
						if (county.getOfficeId().equals(curOffice.getId())) {
							pointMap = getMarkPointData(county.getOfficeName(),
									Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							currentCounty = county;
							currentCounty.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							PointDataList.add(pointMap);
						}

						// 父机构
						if (county.getOfficeId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(),
										Constant.MapPointValue.GENTRAL_VALUE);
								parentCounty = county;
								parentCounty.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
								PointDataList.add(pointMap);
							}
						}

						// 同级金库
						if (county.getParentId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.COFFER.equals(county.getType())) {
								pointMap = getMarkPointData(county.getOfficeName(),
										Constant.MapPointValue.COFFER_VALUE);
								childCountyList.add(county);
								PointDataList.add(pointMap);
							}
						}
					}

					// 获取地图makeline项的data数据
					if (currentCounty != null) {
						List<Map<String, String>> lineList = Lists.newArrayList();
						if (parentCounty != null) {
							lineList = getMarkLineData(parentCounty.getOfficeName(), currentCounty.getOfficeName(),
									parentCounty.getBankNum());
							lineDataList.add(lineList);
						}

						if (childCountyList.size() != 0) {
							for (CountyEntity childCounty : childCountyList) {
								lineList = getMarkLineData(currentCounty.getOfficeName(), childCounty.getOfficeName(),
										currentCounty.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}

				} else {
					// 省级地图
					String provinceCode = "";
					Iterator<String> it = proSet.iterator();
					while (it.hasNext()) {
						provinceCode = it.next();
					}
					rtnMap = makeMapProvinceOption(provinceCode);

					List<CityEntity> list = cityDao.findClearCityData(curOffice);
					CityEntity currentCity = null;
					CityEntity parentCity = null;
					List<CityEntity> childCityList = Lists.newArrayList();

					for (CityEntity city : list) {

						// 获取地图 geoCoord数据
						strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
						geoCoordMap.put(city.getOfficeName(), strList);

						// 获取地图markPoint项的data数据
						// 当前机构
						if (city.getOfficeId().equals(curOffice.getId())) {
							pointMap = getMarkPointData(city.getOfficeName(),
									Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							currentCity = city;
							currentCity.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
							PointDataList.add(pointMap);
						}

						// 父机构
						if (city.getOfficeId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
								parentCity = city;
								parentCity.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
								PointDataList.add(pointMap);
							}
						}

						// 同级机构
						if (city.getParentId().equals(curOffice.getParentId())) {
							if (Constant.OfficeType.COFFER.equals(city.getType())) {
								pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
								childCityList.add(city);
								PointDataList.add(pointMap);
							}
						}
					}

					// 获取地图makeline项的data数据
					if (currentCity != null) {
						List<Map<String, String>> lineList = Lists.newArrayList();
						if (parentCity != null) {
							lineList = getMarkLineData(parentCity.getOfficeName(), currentCity.getOfficeName(),
									parentCity.getBankNum());
							lineDataList.add(lineList);
						}

						if (childCityList.size() != 0) {
							for (CityEntity childCity : childCityList) {
								lineList = getMarkLineData(currentCity.getOfficeName(), childCity.getOfficeName(),
										currentCity.getBankNum());
								lineDataList.add(lineList);
							}
						}
					}
				}

			} else {
				// 全国地图
				rtnMap = makeMapChinaOption();

				List<ProvinceEntity> list = proDao.findClearProData(curOffice);
				ProvinceEntity currentPro = null;
				ProvinceEntity parentPro = null;
				List<ProvinceEntity> childProList = Lists.newArrayList();

				for (ProvinceEntity pro : list) {

					// 获取地图 geoCoord数据
					strList = getGeoCoordData(pro.getLongitude(), pro.getLatitude());
					geoCoordMap.put(pro.getOfficeName(), strList);
					// 获取地图markPoint项的data数据
					// 当前机构
					if (pro.getOfficeId().equals(curOffice.getId())) {
						pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						currentPro = pro;
						currentPro.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
						PointDataList.add(pointMap);
					}

					// 父机构
					if (pro.getOfficeId().equals(curOffice.getParentId())) {
						if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
							parentPro = pro;
							parentPro.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
							PointDataList.add(pointMap);
						}
					}

					// 同级机构
					if (pro.getParentId().equals(curOffice.getParentId())) {
						if (Constant.OfficeType.COFFER.equals(pro.getType())) {
							pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
							childProList.add(pro);
							PointDataList.add(pointMap);
						}
					}
				}

				// 获取地图makeline项的data数据
				if (currentPro != null) {
					List<Map<String, String>> lineList = Lists.newArrayList();
					if (parentPro != null) {
						lineList = getMarkLineData(parentPro.getOfficeName(), currentPro.getOfficeName(),
								parentPro.getBankNum());
						lineDataList.add(lineList);
					}

					if (childProList.size() != 0) {
						for (ProvinceEntity childPro : childProList) {
							lineList = getMarkLineData(currentPro.getOfficeName(), childPro.getOfficeName(),
									currentPro.getBankNum());
							lineDataList.add(lineList);
						}
					}
				}
			}
		}
		// 把地图数据返回前台
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_GEOCOORMAP, geoCoordMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_LINEDATALIST, lineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_POINTDATALIST, PointDataList);
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: makeMapChinaOption
	 * <p>Description: 全国地图数据设置项</p>
	 * @author:     wanghan
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> makeMapChinaOption() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, String> jsonInfoMap = Maps.newHashMap();

		rtnMap.put(ReportGraphConstant.SeriesProperties.SERIES_MAPTYPE_KEY, ReportGraphConstant.SeriesProperties.MAP_JSON_CHINA);
		jsonInfoMap.put(ReportGraphConstant.SeriesProperties.MAP_JSON_CHINA, Global.getConfig("map.mapJson.graph"));
		rtnMap.put(ReportGraphConstant.MAP_JSON_INFO_KEY, jsonInfoMap);
		return rtnMap;
	}

	/**
	 * 
	 * Title: makeMapProvinceOption
	 * <p>Description: 省份地图数据设置项</p>
	 * @author:     wanghan
	 * @param provinceCode 省份编码
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> makeMapProvinceOption(String provinceCode) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, String> jsonInfoMap = Maps.newHashMap();

		jsonInfoMap.put(proDao.findProName(provinceCode), proDao.findProJsonCode(provinceCode));
		rtnMap.put(ReportGraphConstant.MAP_JSON_INFO_KEY, jsonInfoMap);
		rtnMap.put(ReportGraphConstant.SeriesProperties.SERIES_MAPTYPE_KEY, proDao.findProName(provinceCode));
		return rtnMap;
	}

	/**
	 * 
	 * Title: makeMapCityOption
	 * <p>Description: 城市地图数据设置项</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> makeMapCityOption(String cityCode) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, String> jsonInfoMap = Maps.newHashMap();

		jsonInfoMap.put(cityDao.findCityName(cityCode), cityDao.findCityJson(cityCode));
		rtnMap.put(ReportGraphConstant.MAP_JSON_INFO_KEY, jsonInfoMap);
		rtnMap.put(ReportGraphConstant.SeriesProperties.SERIES_MAPTYPE_KEY, cityDao.findCityName(cityCode));
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: getGeoCoordData
	 * <p>Description: 获取地图 geoCoord数据</p>
	 * @author:     wanghan
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @return 
	 * List<String>    返回类型
	 */
	public List<String> getGeoCoordData(String longitude,String latitude){
		List<String> strList = Lists.newArrayList();
		strList.add(longitude);
		strList.add(latitude);
		return strList;
	}
	
	/**
	 * 
	 * Title: getMarkPointData
	 * <p>Description: 地图markPoint项的data数据</p>
	 * @author:     wanghan
	 * @param name 地图的点的名字
	 * @param value 常量（用来区分颜色）
	 * @return 
	 * Map<String,String>    返回类型
	 */
	public Map<String, String> getMarkPointData(String name,String value){
		Map<String, String> pointMap = Maps.newHashMap();
		pointMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
		pointMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, value);
		return pointMap;
	}
	
	/**
	 * 
	 * Title: getMarkLineData
	 * <p>Description: 地图markline项的data数据</p>
	 * @author:     wanghan
	 * @param startName 地图起始点名字
	 * @param endName 地图结束点名字
	 * @param value 常量（用来区分颜色）
	 * @return 
	 * List<Map<String,String>>    返回类型
	 */
	public List<Map<String, String>> getMarkLineData(String startName, String endName, String value) {
		List<Map<String, String>> lineList = Lists.newArrayList();
		Map<String, String> startPointMap = Maps.newHashMap();
		Map<String, String> endPointMap = Maps.newHashMap();
		startPointMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, startName);
		startPointMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, value);
		endPointMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, endName);
		lineList.add(startPointMap);
		lineList.add(endPointMap);
		return lineList;
	}
	
	/**
	 * 
	 * Title: getRouteLineData
	 * <p>Description: 查询当前用户线路信息</p>
	 * @author:     wangbaozhong
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> getRouteLineData() {
		// 线路信息查询
		List<StoRouteInfo> infoList = StoreCommonUtils.getRouteInfoListByOfficeId(UserUtils.getUser().getOffice().getId());
		Map<String, Object> lineDataMap = Maps.newHashMap();
		List<Map<String, Object>> lineMapList = Lists.newArrayList();
		Map<String, Object> lineMap = null;
		Map<String, Object> pointMap = null;
		List<Map<String, Object>> pointsMapList = null;
		List<String> lnglatList = null;
		for (StoRouteInfo info : infoList) {
			if (StringUtils.isBlank(info.getCurOffice().getLongitude()) || StringUtils.isBlank(info.getCurOffice().getLatitude())) {
				continue;
			}
			lineMap = Maps.newHashMap();
			// 加入线路名称
			lineMap.put(ONLINE_MAP_KEY_NAME, info.getRouteName());
			// 加入车辆名称
			lineMap.put(ONLINE_MAP_KEY_CARNO, info.getCarNo());
			pointsMapList = Lists.newArrayList();
			
			// 加入当前机构坐标信息
			pointMap = Maps.newHashMap();
			// 机构名称
			pointMap.put(ONLINE_MAP_KEY_NAME, info.getCurOffice().getName());
			lnglatList = Lists.newArrayList();
			lnglatList.add(info.getCurOffice().getLongitude());
			lnglatList.add(info.getCurOffice().getLatitude());
			// 机构所在经纬度
			pointMap.put(ONLINE_MAP_KEY_LNGLAT, lnglatList);
			// 标记为当前机构
			pointMap.put(ONLINE_MAP_KEY_IS_CURRENT_OFFICE, true);
			// 标记机构类型
			pointMap.put(ONLINE_MAP_KEY_POINT_TYPE, info.getCurOffice().getType());
			pointsMapList.add(pointMap);
			// 加入网点坐标信息
			for (StoRouteDetail detail : info.getStoRouteDetailList()) {
				if (StringUtils.isBlank(detail.getOffice().getLongitude()) || StringUtils.isBlank(detail.getOffice().getLatitude())) {
					continue;
				}
				pointMap = Maps.newHashMap();
				// 机构所在经纬度
				pointMap.put(ONLINE_MAP_KEY_NAME, detail.getOffice().getName());
				lnglatList = Lists.newArrayList();
				lnglatList.add(detail.getOffice().getLongitude());
				lnglatList.add(detail.getOffice().getLatitude());
				pointMap.put(ONLINE_MAP_KEY_LNGLAT, lnglatList);
				// 标记机构类型
				pointMap.put(ONLINE_MAP_KEY_POINT_TYPE, detail.getOffice().getType());
				pointsMapList.add(pointMap);
			}
			lineMap.put(ONLINE_MAP_KEY_POINTS, pointsMapList);
			// 获取线路规划数据
			List<String> longitudeList = Lists.newArrayList();
			List<String> latitudeList = Lists.newArrayList();
			String[] params = null;
			if (stoRouteInfoDao.getRoutePlan(info) != null) {
				if (StringUtils.isNotBlank(stoRouteInfoDao.getRoutePlan(info).getRouteLnglat())) {
					params = stoRouteInfoDao.getRoutePlan(info).getRouteLnglat().split(Constant.Punctuation.COMMA);
					for (int iCount = 0; iCount < params.length; iCount++) {
						if (iCount % 2 == 0) {
							longitudeList.add(params[iCount]);
						} else {
							latitudeList.add(params[iCount]);
						}
					}
				}
			}
			
			if (stoRouteInfoDao.getRoutePlan(info) != null) {
				// 规划线路颜色
				if (StringUtils.isNotEmpty(stoRouteInfoDao.getRoutePlan(info).getRoutePlanColor())) {
					lineMap.put(ROUTE_PLAN_COLOR, stoRouteInfoDao.getRoutePlan(info).getRoutePlanColor());
				} else {
					lineMap.put(ROUTE_PLAN_COLOR, "00FF00");
				}
				// 车辆经过轨迹颜色
				if (StringUtils.isNotEmpty(stoRouteInfoDao.getRoutePlan(info).getCarTrackColor())) {
					lineMap.put(CAR_TRACK_COLOR, stoRouteInfoDao.getRoutePlan(info).getCarTrackColor());
				} else {
					lineMap.put(CAR_TRACK_COLOR, "888888");
				}
			}
			lineMap.put("longitudeList", longitudeList);
			lineMap.put("latitudeList", latitudeList);
			lineMapList.add(lineMap);
			
		}
		lineDataMap.put(ONLINE_MAP_KEY_LINEDATA, lineMapList);
		return lineDataMap;
	}
	
	/**
	 * 
	 * Title: getAllRelData
	 * <p>Description: 在线地图人行清分中心和网点调拨信息</p>
	 * @author:     wanghan
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> getAllRelData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<Map<String, Object>> lineDataList = Lists.newArrayList();
		// 获取当前用户
		User user = UserUtils.getUser();
		// 获取当前机构
		Office curOffice = SysCommonUtils.findOfficeById(user.getOffice().getId());
		List<Office> officeList = Lists.newArrayList();
		if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			// 获取与清分中心有业务往来机构（父级人行，同级金库和清分中心本身）
			officeList = officeDao.getAssWithClearOffice(curOffice.getId(), curOffice.getParentId());
		} else if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
			// 获取与人行有业务往来机构
			officeList = officeDao.getAssoGenOffice(curOffice.getId(),curOffice.getParentId());
		} else if (Constant.OfficeType.OUTLETS.equals(curOffice.getType())) {
			// 获取与网点有业务往来机构（父级金库）
			officeList = officeDao.getProCityOffice(curOffice.getId());
		}
		
		Office currentOffice = null;
		Office parentOffice = null;
		List<Office> childOfficeList = Lists.newArrayList();

		for (Office office : officeList) {
			// 当前机构
			if (office.getId().equals(curOffice.getId())) {
				currentOffice = office;
			}
			// 清分中心
			if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
				if (!(office.getId().equals(curOffice.getId()))) {
					childOfficeList.add(office);
				}
				// 人行
			} else if (Constant.OfficeType.CENTRAL_BANK.equals(curOffice.getType())) {
				if (!(office.getId().equals(curOffice.getId()))) {
					childOfficeList.add(office);
				}
				// 网点
			} else if (Constant.OfficeType.OUTLETS.equals(curOffice.getType())) {
				// 父机构
				if (office.getId().equals(curOffice.getParentId())) {
					parentOffice = office;
				}
			}
		}

		// 获取地图points项的数据
		if (currentOffice != null && StringUtils.isNotBlank(currentOffice.getLongitude())
				&& StringUtils.isNotBlank(currentOffice.getLatitude())) {
			Map<String, Object> LineMap;
			if (parentOffice != null) {
				LineMap = Maps.newHashMap();
				LineMap.put(ONLINE_MAP_KEY_LINE_COLOR, ONLINE_MAP_VALUE_LINE_COLOR);
				List<Map<String, Object>> officeInfoList = Lists.newArrayList();
				if(StringUtils.isNotBlank(parentOffice.getLongitude())
						&& StringUtils.isNotBlank(parentOffice.getLatitude())) {
					// 起始机构
					Map<String, Object> startOfficeMap = makeOnlineData(parentOffice);
					officeInfoList.add(startOfficeMap);
				}
				// 终点机构
				Map<String, Object> endOfficeMap = makeOnlineData(currentOffice);
				// 标记当前机构
				endOfficeMap.put(ONLINE_MAP_KEY_IS_CURRENT_OFFICE, true);
				officeInfoList.add(endOfficeMap);
				LineMap.put(ONLINE_MAP_KEY_POINTS, officeInfoList);
				if(!(lineDataList.contains(LineMap))) {
					lineDataList.add(LineMap);
				}
			}

			for (Office childOffice : childOfficeList) {
				List<Map<String, Object>> officeInfoList = Lists.newArrayList();
				// 起始机构
				Map<String, Object> startOfficeMap = makeOnlineData(currentOffice);
				// 标记当前机构
				startOfficeMap.put(ONLINE_MAP_KEY_IS_CURRENT_OFFICE, true);
				officeInfoList.add(startOfficeMap);
				if (StringUtils.isNotBlank(childOffice.getLongitude())
						&& StringUtils.isNotBlank(childOffice.getLatitude())) {
					// 终点机构
					Map<String, Object> endOfficeMap = makeOnlineData(childOffice);
					officeInfoList.add(endOfficeMap);
				}else {
					continue;
				}
				LineMap = Maps.newHashMap();
				LineMap.put(ONLINE_MAP_KEY_LINE_COLOR, ONLINE_MAP_VALUE_LINE_COLOR);
				LineMap.put(ONLINE_MAP_KEY_POINTS, officeInfoList);
				lineDataList.add(LineMap);
			}
			
			if(lineDataList.size() == 0) {
				LineMap = Maps.newHashMap();
				LineMap.put(ONLINE_MAP_KEY_LINE_COLOR, ONLINE_MAP_VALUE_LINE_COLOR);
				List<Map<String, Object>> officeInfoList = Lists.newArrayList();
				Map<String, Object> endOfficeMap = makeOnlineData(currentOffice);
				// 标记当前机构
				endOfficeMap.put(ONLINE_MAP_KEY_IS_CURRENT_OFFICE, true);
				officeInfoList.add(endOfficeMap);
				LineMap.put(ONLINE_MAP_KEY_POINTS, officeInfoList);
				if(!(lineDataList.contains(LineMap))) {
					lineDataList.add(LineMap);
				}
			}
		}

		rtnMap.put(ONLINE_MAP_KEY_LINEDATA, lineDataList);
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: makeOnlineData
	 * <p>Description: 在线地图数据</p>
	 * @author:     wanghan
	 * @param office 机构实体类对象
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	public Map<String, Object> makeOnlineData(Office office) {
		Map<String, Object> officeInfoMap = Maps.newHashMap();
		// 机构名字
		officeInfoMap.put(ONLINE_MAP_KEY_NAME, office.getName());
		// 机构类型
		officeInfoMap.put(ONLINE_MAP_KEY_POINT_TYPE, office.getType());
		// 机构坐标（经纬度）
		List<String> coorList = Lists.newArrayList();
		coorList.add(office.getLongitude());
		coorList.add(office.getLatitude());
		officeInfoMap.put(ONLINE_MAP_KEY_LNGLAT, coorList);

		return officeInfoMap;
	}
}
