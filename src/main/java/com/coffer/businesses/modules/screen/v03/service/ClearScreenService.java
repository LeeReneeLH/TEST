package com.coffer.businesses.modules.screen.v03.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.screen.ScreenConstant;
import com.coffer.businesses.modules.screen.v03.dao.ClearScreenDao;
import com.coffer.businesses.modules.screen.v03.entity.AtmInfo;
import com.coffer.businesses.modules.screen.v03.entity.ClearScreenMain;
import com.coffer.businesses.modules.screen.v03.entity.DoorInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * Title: MapService
 * <p>
 * Description: 地图相关的逻辑层
 * </p>
 * 
 * @author wanghan
 * @date 2017年11月28日 上午11:16:54
 */
@Service
@Transactional(readOnly = true)
public class ClearScreenService extends CrudService<ClearScreenDao, ClearScreenMain> {

	@Autowired
	private ProvinceDao proDao;
	@Autowired
	private CityDao cityDao;
	@Autowired
	private CountyDao countyDao;

	@Autowired
	private ClearScreenDao clearScreenDao;
	@Autowired
	private OfficeDao officeDao;
	
	/**
	 * 
	 * Title: makeMapGraphData
	 * <p>
	 * Description: 离线地图
	 * </p>
	 * 
	 * @author: wanghan
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> makeMapGraphData(Map<String, Object> AlldataMap) {
		return null;
	}

	/**
	 * 全国网点地址取得
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> getChinaAddress(String officeId, String mapFlag) {
		Office office = SysCommonUtils.findOfficeById(officeId);
		// 返回集合
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 所有描点坐标
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		// 起始坐标、终止坐标
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		// 坐标类型以及对应value
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		List<String> strList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		// 获取离线地图初始机构所在省份
		String officeProvinceName = proDao.findProName(office.getProvinceCode());
		// 全国地图
		rtnMap = makeMapChinaOption();
		List<ProvinceEntity> list = Lists.newArrayList();
		// 离线数据
		if (ScreenConstant.MapFlag.OFFLINE.equals(mapFlag)) {
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				// 获取与清分中心有业务往来的机构（包括清分中心本身）
				list = proDao.findClearProDataByClear(office);
			}
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				// 获取与清分中心有业务往来的机构（包括清分中心本身）
				list = proDao.findClearProDataByPlat(office);
			}
		}
		// 在线数据
		if (ScreenConstant.MapFlag.ONLINE.equals(mapFlag)) {
			List<ProvinceEntity> listOnline = Lists.newArrayList();
			// 所有机构数据（清分中心）
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				listOnline = proDao.findOnlineClearProDataByClear(office);
			}
			// 所有机构数据（平台）
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				listOnline = proDao.findOnlineClearProDataByPlat(office);
			}
			// 过滤省内机构
			for (ProvinceEntity province : listOnline) {
				// 若机构为平台
				if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())
						&& Constant.OfficeType.DIGITAL_PLATFORM.equals(province.getType())) {
					list.add(province);
					continue;
				}
				// 若机构为清分中心
				if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
						&& Constant.OfficeType.CLEAR_CENTER.equals(province.getType())) {
					list.add(province);
					continue;
				}
				// 若经纬度坐标为空
				if (!StringUtils.isEmpty(province.getLongitude()) && !StringUtils.isEmpty(province.getLatitude())) {
					String ProvinceName = province.getAddress().substring(0, 3);
					if (!officeProvinceName.equals(ProvinceName)) {
						list.add(province);
					}
				}
			}
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
			if (pro.getOfficeId().equals(office.getId())) {
				pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				currentPro = pro;
				currentPro.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				PointDataList.add(pointMap);
			}

			// 父机构
			if (pro.getOfficeId().equals(office.getParentId())) {
				if (Constant.OfficeType.CENTRAL_BANK.equals(pro.getType())) {
					pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
					parentPro = pro;
					parentPro.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
					PointDataList.add(pointMap);
				}
			}

			// 同级机构
			if (pro.getParentId().equals(office.getParentId())) {
				if (Constant.OfficeType.COFFER.equals(pro.getType())) {
					pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
					childProList.add(pro);
					PointDataList.add(pointMap);
				}
			}

			// 下属清分中心
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				if (Constant.OfficeType.CLEAR_CENTER.equals(pro.getType())) {
					pointMap = getMarkPointData(pro.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
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
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_GEOCOORMAP, geoCoordMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_LINEDATALIST, lineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_POINTDATALIST, PointDataList);
		return rtnMap;
	}

	/**
	 * 全省网点地址取得
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> getProvinceAddress(String officeId, String mapFlag) {
		Office office = SysCommonUtils.findOfficeById(officeId);
		// 返回集合
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 所有描点坐标
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		// 起始坐标、终止坐标
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		// 坐标类型以及对应value
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		List<String> strList = Lists.newArrayList();
		// 省级地图
		String provinceCode = office.getProvinceCode();
		// 省份地图数据设置项
		rtnMap = makeMapProvinceOption(provinceCode);

		// 获取离线地图初始机构所在省份
		String officeProvinceName = proDao.findProName(office.getProvinceCode());
		// 获取离线地图机构所在城市
		String officeCityName = cityDao.findCityName(office.getCityCode());

		List<CityEntity> list = Lists.newArrayList();
		// 离线数据
		if (ScreenConstant.MapFlag.OFFLINE.equals(mapFlag)) {
			// 获取机构数据（清分中心）
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				list = cityDao.findClearCityDataByclear(office);
			}
			// 获取机构数据（平台）
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				list = cityDao.findClearCityDataByPlat(office);
			}
			// 获取机构数据（人民银行）
			if (Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
				list = cityDao.findClearCityDataByPeopleBank(office);
			}
		}
		// 在线数据
		if (ScreenConstant.MapFlag.ONLINE.equals(mapFlag)) {
			List<CityEntity> listOnline = Lists.newArrayList();
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				// 获取与清分中心有业务往来的机构（清分中心）
				listOnline = cityDao.findOnlineClearCityDataByClear(office);
			}
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				// 获取与清分中心有业务往来的机构（平台）
				listOnline = cityDao.findOnlineClearCityDataByPlat(office);
			}
			// 过滤省内机构
			for (CityEntity city : listOnline) {
				// 平台
				if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())
						&& Constant.OfficeType.DIGITAL_PLATFORM.equals(city.getType())) {
					list.add(city);
					continue;
				}
				// 清分中心
				if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
						&& Constant.OfficeType.CLEAR_CENTER.equals(city.getType())) {
					list.add(city);
					continue;
				}
				// 若经纬度坐标为空
				if (!StringUtils.isEmpty(city.getLongitude()) && !StringUtils.isEmpty(city.getLatitude())) {
					String provinceName = city.getAddress().substring(0, 3);
					String cityName = city.getAddress().substring(3, 6);
					if (officeProvinceName.equals(provinceName)) {
						if (!officeCityName.equals(cityName)) {
							list.add(city);
						}
					}

				}
			}
		}

		CityEntity currentCity = null;
		CityEntity parentCity = null;
		List<CityEntity> childCityList = Lists.newArrayList();

		for (CityEntity city : list) {
			// 获取地图 geoCoord数据
			strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
			geoCoordMap.put(city.getOfficeName(), strList);
			// 获取地图markPoint项的data数据
			// 当前机构
			if (city.getOfficeId().equals(office.getId())) {
				pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				currentCity = city;
				currentCity.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				PointDataList.add(pointMap);
			}

			// 父机构
			if (city.getOfficeId().equals(office.getParentId())) {
				if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
					pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
					parentCity = city;
					parentCity.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
					PointDataList.add(pointMap);
				}
			}

			// 同级机构
			if (city.getParentId().equals(office.getParentId())) {
				if (Constant.OfficeType.COFFER.equals(city.getType())) {
					pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
					childCityList.add(city);
					PointDataList.add(pointMap);
				}
			}
			// 下属清分中心
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())
					|| Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
				if (Constant.OfficeType.CLEAR_CENTER.equals(city.getType())) {
					pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
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

		// 把地图数据返回前台
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_GEOCOORMAP, geoCoordMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_LINEDATALIST, lineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_POINTDATALIST, PointDataList);
		return rtnMap;
	}

	/**
	 * 通过省份Id获取全省地图
	 * 
	 * @author qph
	 * @version 2018-01-31
	 * @param
	 * @return
	 */
	public Map<String, Object> getProvinceAddressByProvinceCode(String provinceCode) {
		// 返回集合
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 所有描点坐标
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		// 起始坐标、终止坐标
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		// 坐标类型以及对应value
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		List<String> strList = Lists.newArrayList();
		// 省份地图数据设置项
		rtnMap = makeMapProvinceOption(provinceCode);
		Office office = new Office();
		// 查询机构
		Office curOffice = new Office();
		office.setProvinceCode(provinceCode);
		// 获取省级人民银行
		List<Office> officeList = cityDao.findPeopleBankByProvinceCode(office);
		if (!Collections3.isEmpty(officeList)) {
			curOffice = officeList.get(0);
		}
		List<CityEntity> list = Lists.newArrayList();
		// 获取机构列表
		list = cityDao.findClearCityDataByProvinceCode(curOffice);
		List<CityEntity> childCityList = Lists.newArrayList();

		for (CityEntity city : list) {
			// 获取地图 geoCoord数据
			strList = getGeoCoordData(city.getLongitude(), city.getLatitude());
			geoCoordMap.put(city.getOfficeName(), strList);
			// 获取地图markPoint项的data数据
			// 清分中心
			if (Constant.OfficeType.CLEAR_CENTER.equals(city.getType())) {
				pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
				childCityList.add(city);
				PointDataList.add(pointMap);
			}
			// 人民银行
			if (Constant.OfficeType.CENTRAL_BANK.equals(city.getType())) {
				// 地图markPoint项的data数据
				pointMap = getMarkPointData(city.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
				PointDataList.add(pointMap);
			}
		}
		// 把地图数据返回前台
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_GEOCOORMAP, geoCoordMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_LINEDATALIST, lineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.MAP_POINTDATALIST, PointDataList);
		return rtnMap;
	}

	/**
	 * 全市网点地址取得
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> getCityAddress(String officeId, String mapFlag) {
		Office office = SysCommonUtils.findOfficeById(officeId);
		// 返回集合
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 所有描点坐标
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		// 起始坐标、终止坐标
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		// 坐标类型以及对应value
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		// 市级地图
		String cityCode = office.getCityCode();
		// 城市地图数据设置项
		rtnMap = makeMapCityOption(cityCode);
		List<CountyEntity> list = Lists.newArrayList();
		// 获取离线地图初始机构所在省份
		String officeProvinceName = proDao.findProName(office.getProvinceCode());
		// 获取离线地图机构所在城市
		String officeCityName = cityDao.findCityName(office.getCityCode());
		// 离线数据
		if (ScreenConstant.MapFlag.OFFLINE.equals(mapFlag)) {
			// 所有机构数据（清分中心）
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				list = countyDao.findClearCountyDataByclear(office);
			}
			// 所有机构数据（平台）
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				list = countyDao.findClearCountyDataByPlat(office);
			}
			// 所有机构数据（人民银行）
			if (Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
				list = countyDao.findClearCountyDataByPeopleBank(office);
			}
		}
		// 在线数据
		if (ScreenConstant.MapFlag.ONLINE.equals(mapFlag)) {
			List<CountyEntity> listOnline = Lists.newArrayList();
			// 所有机构数据（清分中心）
			if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
				listOnline = countyDao.findOnlineClearCountyDataByclear(office);
			}
			// 所有机构数据（平台）
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				listOnline = countyDao.findOnlineClearCountyDataByPlat(office);
			}
			// 过滤省内机构
			for (CountyEntity county : listOnline) {
				// 平台
				if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())
						&& Constant.OfficeType.DIGITAL_PLATFORM.equals(county.getType())) {
					list.add(county);
					continue;
				}
				// 清分中心
				if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
						&& Constant.OfficeType.CLEAR_CENTER.equals(county.getType())) {
					list.add(county);
					continue;
				}
				// 若经纬度坐标不为空
				if (!StringUtils.isEmpty(county.getLongitude()) && !StringUtils.isEmpty(county.getLatitude())) {
					String provinceName = county.getAddress().substring(0, 2);
					String cityName = county.getAddress().substring(3, 6);
					if (officeProvinceName.equals(provinceName)) {
						if (officeCityName.equals(cityName)) {
							list.add(county);
						}
					}

				}
			}
		}
		CountyEntity currentCounty = null;
		CountyEntity parentCounty = null;
		List<CountyEntity> childCountyList = Lists.newArrayList();
		List<String> strList = Lists.newArrayList();

		for (CountyEntity county : list) {
			// 获取地图 geoCoord数据
			strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
			geoCoordMap.put(county.getOfficeName(), strList);
			// 获取地图markPoint项的data数据
			// 当前机构
			if (county.getOfficeId().equals(office.getId())) {
				pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				currentCounty = county;
				currentCounty.setBankNum(Constant.MapPointValue.CURRENT_OFFICE_VALUE);
				PointDataList.add(pointMap);
			}

			// 父机构
			if (county.getOfficeId().equals(office.getParentId())) {
				if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
					// 地图markPoint项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
					parentCounty = county;
					parentCounty.setBankNum(Constant.MapPointValue.GENTRAL_VALUE);
					PointDataList.add(pointMap);
				}
			}

			// 同级金库
			if (county.getParentId().equals(office.getParentId())) {
				if (Constant.OfficeType.COFFER.equals(county.getType())) {
					// 地图markPoint项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
					childCountyList.add(county);
					PointDataList.add(pointMap);
				}
			}

			// 人民银行权限
			if (Constant.OfficeType.CENTRAL_BANK.equals(office.getType())) {
				// 清分中心
				if (Constant.OfficeType.CLEAR_CENTER.equals(county.getType())) {
					// 地图markline项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
					currentCounty = county;
					currentCounty.setBankNum(Constant.MapPointValue.CLEAR_VALUE);
					PointDataList.add(pointMap);
				}
				// 金库
				if (Constant.OfficeType.COFFER.equals(county.getType())) {
					// 地图markline项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
					childCountyList.add(county);
					PointDataList.add(pointMap);
				}

			}
			// 平台权限
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				// 清分中心
				if (Constant.OfficeType.CLEAR_CENTER.equals(county.getType())) {
					// 地图markline项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
					childCountyList.add(county);
					PointDataList.add(pointMap);
				}
				// 人民银行
				if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
					// 地图markline项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
					childCountyList.add(county);
					PointDataList.add(pointMap);
				}
				// 金库
				if (Constant.OfficeType.COFFER.equals(county.getType())) {
					// 地图markline项的data数据
					pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
					childCountyList.add(county);
					PointDataList.add(pointMap);
				}
			}
		}

		// 获取地图makeline项的data数据
		if (currentCounty != null) {
			List<Map<String, String>> lineList = Lists.newArrayList();
			if (parentCounty != null) {
				// 地图markline项的data数据
				lineList = getMarkLineData(parentCounty.getOfficeName(), currentCounty.getOfficeName(),
						parentCounty.getBankNum());
				lineDataList.add(lineList);
			}
			if (childCountyList.size() != 0) {
				for (CountyEntity childCounty : childCountyList) {
					// 地图markline项的data数据
					lineList = getMarkLineData(currentCounty.getOfficeName(), childCounty.getOfficeName(),
							currentCounty.getBankNum());
					lineDataList.add(lineList);
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
	 * 全市网点地址取得
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> getCityAddressByCityCode(String cityJsonCode) {
		// 返回集合
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 所有描点坐标
		Map<String, List<String>> geoCoordMap = Maps.newHashMap();
		// 起始坐标、终止坐标
		List<List<Map<String, String>>> lineDataList = Lists.newArrayList();
		// 坐标类型以及对应value
		List<Map<String, String>> PointDataList = Lists.newArrayList();
		Map<String, String> pointMap = Maps.newHashMap();
		String cityCode = cityDao.findCityCodeByJson(cityJsonCode);
		List<CountyEntity> list = Lists.newArrayList();
		Office curOffice = new Office();
		if (!StringUtils.isEmpty(cityCode)) {
			Office office = new Office();
			office.setCityCode(cityCode);
			// 查询机构
			// 获取省级人民银行
			List<Office> officeList = countyDao.findPeopleBankByCityCode(office);
			if (!Collections3.isEmpty(officeList)) {
				curOffice = officeList.get(0);
			}
		}
		// 城市地图数据设置项
		rtnMap = makeMapCityOption(cityCode);
		curOffice.setCityCode(cityCode);
		list = countyDao.findClearCityDataByCityCode(curOffice);

		List<String> strList = Lists.newArrayList();

		for (CountyEntity county : list) {
			// 获取地图 geoCoord数据
			strList = getGeoCoordData(county.getLongitude(), county.getLatitude());
			geoCoordMap.put(county.getOfficeName(), strList);
			// 获取地图markPoint项的data数据
			// 当前机构
			// 父机构
			if (Constant.OfficeType.CENTRAL_BANK.equals(county.getType())) {
				// 地图markPoint项的data数据
				pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.GENTRAL_VALUE);
				PointDataList.add(pointMap);
			}
			// 同级金库
			if (Constant.OfficeType.COFFER.equals(county.getType())) {
				// 地图markPoint项的data数据
				pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.COFFER_VALUE);
				PointDataList.add(pointMap);
			}
			// 清分中心
			if (Constant.OfficeType.CLEAR_CENTER.equals(county.getType())) {
				pointMap = getMarkPointData(county.getOfficeName(), Constant.MapPointValue.CLEAR_VALUE);
				PointDataList.add(pointMap);
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
	 * <p>
	 * Description: 全国地图数据设置项
	 * </p>
	 * 
	 * @author: wanghan
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> makeMapChinaOption() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, String> jsonInfoMap = Maps.newHashMap();

		rtnMap.put(ReportGraphConstant.SeriesProperties.SERIES_MAPTYPE_KEY,
				ReportGraphConstant.SeriesProperties.MAP_JSON_CHINA);
		jsonInfoMap.put(ReportGraphConstant.SeriesProperties.MAP_JSON_CHINA, Global.getConfig("map.mapJson.graph"));
		rtnMap.put(ReportGraphConstant.MAP_JSON_INFO_KEY, jsonInfoMap);
		return rtnMap;
	}

	/**
	 * 
	 * Title: makeMapProvinceOption
	 * <p>
	 * Description: 省份地图数据设置项
	 * </p>
	 * 
	 * @author: wanghan
	 * @param provinceCode
	 *            省份编码
	 * @return Map<String,Object> 返回类型
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
	 * <p>
	 * Description: 城市地图数据设置项
	 * </p>
	 * 
	 * @author: wanghan
	 * @param cityCode
	 *            城市编码
	 * @return Map<String,Object> 返回类型
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
	 * <p>
	 * Description: 获取地图 geoCoord数据
	 * </p>
	 * 
	 * @author: wanghan
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @return List<String> 返回类型
	 */
	public List<String> getGeoCoordData(String longitude, String latitude) {
		List<String> strList = Lists.newArrayList();
		strList.add(longitude);
		strList.add(latitude);
		return strList;
	}

	/**
	 * 
	 * Title: getMarkPointData
	 * <p>
	 * Description: 地图markPoint项的data数据
	 * </p>
	 * 
	 * @author: wanghan
	 * @param name
	 *            地图的点的名字
	 * @param value
	 *            常量（用来区分颜色）
	 * @return Map<String,String> 返回类型
	 */
	public Map<String, String> getMarkPointData(String name, String value) {
		Map<String, String> pointMap = Maps.newHashMap();
		pointMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
		pointMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, value);
		return pointMap;
	}

	/**
	 * 
	 * Title: getMarkLineData
	 * <p>
	 * Description: 地图markline项的data数据
	 * </p>
	 * 
	 * @author: wanghan
	 * @param startName
	 *            地图起始点名字
	 * @param endName
	 *            地图结束点名字
	 * @param value
	 *            常量（用来区分颜色）
	 * @return List<Map<String,String>> 返回类型
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

	
	
	
//	public ClearScreenMain findList(ClearScreenMain clearScreenMain) {
//		return super.findList(clearScreenMain);
//	}
	
	//-----------------------------------------------------------------
	/**
	 * 
	 * @author wl
	 * @version 商行上缴额的取得
	 * 
	 * @param officeId
	 * @return
	 */
	public ClearScreenMain findUpList(String officeId , Date registerDate ) {
		// 登记用户所属机构
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setRegisterDate(registerDate);
		
		//商行金库ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行
		officeList = findOfficeIdList(officeId , typeList);
		if (Collections3.isEmpty(officeList)) {
			return clearScreenMain;
		}
		
		clearScreenMain.setOfficeList(officeList);
		clearScreenMain.setBusinessType(ClearConstant.BusinessType.BANK_PAY);
		return clearScreenDao.findUpList(clearScreenMain);
	}
	
	/**
	 * 
	 * @author wl
	 * @version 商行取款额的取得
	 * 
	 * @param officeId
	 * @return
	 */
	public ClearScreenMain findBackList(String officeId , Date registerDate ) {
		// 登记用户所属机构
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setRegisterDate(registerDate);
		//商行金库ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行
		officeList = findOfficeIdList(officeId , typeList);
		if (Collections3.isEmpty(officeList)) {
			return clearScreenMain;
		}
		
		clearScreenMain.setOfficeList(officeList);
		clearScreenMain.setBusinessType(ClearConstant.BusinessType.BANK_GET);
		return clearScreenDao.findBackList(clearScreenMain);
	}
	
	/**
	 * 
	 * @author wl
	 * @version 清分差错额的取得
	 * 
	 * @param officeId
	 * @return
	 */
	public ClearScreenMain findErrorList(String officeId , Date registerDate ) {
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setRegisterDate(registerDate);
		
		//清分中心ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.CLEAR_CENTER);	//清分中心
		officeList = findOfficeIdList(officeId , typeList);
		if (Collections3.isEmpty(officeList)) {
			return clearScreenMain;
		}
		
		clearScreenMain.setOfficeList(officeList);
		return clearScreenDao.findErrorList(clearScreenMain);
	}
	
	
	/**
	 * 
	 * @author sg
	 * @version 获取上缴下拨差错额
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public ClearScreenMain findClearList(String officeId , Date registerDate ) {
		// 登记用户所属机构
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setRegisterDate(registerDate);
		
		//清分中心ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.CLEAR_CENTER);	//清分中心
		officeList = findOfficeIdList(officeId , typeList);
		if (Collections3.isEmpty(officeList)) {
			return clearScreenMain;
		}
		
		clearScreenMain.setOfficeList(officeList);
		clearScreenMain.setExcludeTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		return clearScreenDao.findClearList(clearScreenMain);
	}
	
	/**
	 * 
	 * @author sg
	 * @version 自助设备服务统计查询
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public List<Map<String, Object>> findAtmCountList(String officeId  ) {
		// 登记用户所属机构
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		
		//商行金库ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行
		officeList = findOfficeIdList(officeId , typeList);

		clearScreenMain.setOfficeList(officeList);
		return getNewAtmList(clearScreenDao.findAtmCountList(clearScreenMain));
	}
	
	
	/**
	 * 
	 * @author wl
	 * @version ATM服务统计的前4名的取得
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public List<Map<String, Object>> getNewAtmList(List<AtmInfo> atmList) {
		List<Map<String, Object>> returnAtmList  = Lists.newArrayList();
		DecimalFormat df = new DecimalFormat("######0.00");     
		double countSum = 0;
		Long countOtherSum = (long) 0;
		for (int i = 0; i < atmList.size(); i++) {
			if (i <4){
				Map<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("officeId", atmList.get(i).getOfficeId());		//机构ID
				resultMap.put("officeName", atmList.get(i).getOfficeName());	//机构名
				resultMap.put("atmAmount", atmList.get(i).getAtmAmount());		//自助设备数量
				resultMap.put("atmPct", atmList.get(i).getAtmPct());			//自助设备所占百分比
				returnAtmList.add(resultMap);
			}else{
				countOtherSum = countOtherSum + Long.valueOf(atmList.get(i).getAtmAmount());
			}
			
			countSum = countSum + Double.valueOf(atmList.get(i).getAtmAmount());
		}
		
		//其他银行
		if (countOtherSum > 0){
			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("officeId", "");		//机构ID
			resultMap.put("officeName", "其 他 银 行");	//机构名
			resultMap.put("atmAmount", String.valueOf(countOtherSum));		//自助设备数量
			resultMap.put("atmPct", "0");			//自助设备所占百分比
			returnAtmList.add(resultMap);
		}
		//所占百分比的设定
		if (countSum > 0){
			for (int j = 0; j < returnAtmList.size(); j++) {
				double douValue1 = (Double.valueOf((String) returnAtmList.get(j).get("atmAmount")) / countSum) * 100;
				returnAtmList.get(j).put("atmPct", df.format(douValue1));
			}
		}
		
		
		return returnAtmList;
	}
		
	/**
	 * 
	 * @author wl
	 * @version 上门收款服务统计的前4名的取得
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public List<Map<String, Object>> getNewDoorList(List<DoorInfo> doorList) {
		List<Map<String, Object>> returnDoorList  = Lists.newArrayList();
		DecimalFormat df = new DecimalFormat("######0.00");     
		double countSum = 0;
		double countOtherSum =  0;
		for (int i = 0; i < doorList.size(); i++) {
			if (i <4){
				Map<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("officeId", doorList.get(i).getOfficeId());		//机构ID
				resultMap.put("officeName", doorList.get(i).getOfficeName());	//机构名
				resultMap.put("businessAmount", doorList.get(i).getBusinessAmount());		//业务金额
				resultMap.put("businessPct", doorList.get(i).getBusinessPct());			//业务所占比例
				
				//未设定保留
				List<Map<String, Object>> custList = Lists.newArrayList();
				Map<String, Object> custMap = Maps.newHashMap();
				custMap.put("custNo", "");				//商户编号
				custMap.put("custName", "");			//商户名称
				custMap.put("custAmount", "");			//金额
				custList.add(custMap);
				resultMap.put("custList", custList);		//商户list
				returnDoorList.add(resultMap);
			}else{
				countOtherSum = countOtherSum + Double.valueOf(doorList.get(i).getBusinessAmount());
			}
			
			countSum = countSum + Double.valueOf(doorList.get(i).getBusinessAmount());
		}
		
		//其他银行
		if (countOtherSum > 0){
			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("officeId", "");		//机构ID
			resultMap.put("officeName", "其 他 银 行");	//机构名
			resultMap.put("businessAmount", String.valueOf(countOtherSum));		//业务金额
			resultMap.put("businessPct", "0");			//业务所占比例
			//未设定保留
			List<Map<String, Object>> custList = Lists.newArrayList();
			Map<String, Object> custMap = Maps.newHashMap();
			custMap.put("custNo", "");				//商户编号
			custMap.put("custName", "");			//商户名称
			custMap.put("custAmount", "");			//金额
			custList.add(custMap);
			resultMap.put("custList", custList);		//商户list
			returnDoorList.add(resultMap);
		}
		//所占百分比的设定
		if (countSum > 0){
			for (int j = 0; j < returnDoorList.size(); j++) {
				double douValue1 = (Double.valueOf((String) returnDoorList.get(j).get("businessAmount")) / countSum) * 100;
				returnDoorList.get(j).put("businessPct", df.format(douValue1));
			}
		}
		
		
		
		return returnDoorList;
	}
	
	
	/**
	 * 
	 * @author sg
	 * @version 上门收款服务统计查询
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public List<Map<String, Object>> findDoorOrderList(String officeId  ) {
		// 登记用户所属机构
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		
		//门店ID的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.STORE);	//门店
		officeList = findOfficeIdList(officeId , typeList);
		
		clearScreenMain.setOfficeList(officeList);
		return getNewDoorList(clearScreenDao.findDoorOrderList(clearScreenMain));
	}

	
	
	/**
	 * @author qph
	 * @version 2018-02-01
	 * 
	 *          根据省市code获取机构列表
	 * 
	 */

	public String getOfficeList(String mapCode, String mapType) {
		Office office = new Office();
		Office curOffice = new Office();
		// 省级地图
		if (ScreenConstant.MapTypeCode.PROVINCEMAPCODE.equals(mapType)) {
			office.setProvinceCode(mapCode);
			// 获取省级人民银行
			List<Office> officeList = cityDao.findPeopleBankByProvinceCode(office);
			if (!Collections3.isEmpty(officeList)) {
				curOffice = officeList.get(0);
			}
		}
		// 市级地图
		if (ScreenConstant.MapTypeCode.CITYMAPCODE.equals(mapType)) {
			String mapCodeJson = mapCode.substring(0, 4);
			office.setCityCode(mapCodeJson);
			List<Office> officeList = countyDao.findPeopleBankByCityCode(office);
			if (!Collections3.isEmpty(officeList)) {
				curOffice = officeList.get(0);
			}
		}
		return curOffice.getId();
	}

	/**
	 * @author qph
	 * @version 2018-02-01
	 * 
	 *          同步机构信息(接口)
	 * 
	 */
	public List<Map<String, Object>> getOfficeListByService() {
		List<Map<String, Object>> officeList = officeDao.getOfficeListByOffice(new Office());
		return officeList;
	}
	
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询服务清分业务机构数量
	 * 
	 */
	public int getClearCount(String officeId) {

		//商业银行（金库）的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行（金库）
		officeList = findOfficeIdList(officeId , typeList);

		return officeList.size();
	}
	
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询服务金库业务机构数量
	 * 
	 */
	public int getGoldBankCount(String officeId) {

		//商业银行（金库）的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行（金库）
		officeList = findOfficeIdList(officeId , typeList);
		
		return officeList.size();
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询上门收款门店
	 * 
	 */
	public int getDoorCustCount(String officeId) {
		//门店的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.STORE);	//门店
		officeList = findOfficeIdList(officeId , typeList);
		
		return officeList.size();
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询上门收款商户 
	 * 
	 */
	public int getDoorBusinessCount(String officeId) {
		//商户的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.GROUP);	//商户
		officeList = findOfficeIdList(officeId , typeList);
		
		return officeList.size();
	}
	
	
	


	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询服务上门收款
	 * 
	 */
	public int getDoorGoldBankCount(String id) {
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setId(id);
		int count1 =  clearScreenDao.getDoorGoldBankCount(clearScreenMain);
		return count1;
	}
	
	
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询加钞自助设备(ATM)
	 * 
	 */
	public int getAtmCount(String officeId) {
		//商业银行（金库）的取得
		List<String> officeList = Lists.newArrayList();
		List<String> typeList = Lists.newArrayList();
		typeList.add(Constant.OfficeType.COFFER);	//商业银行（金库）
		officeList = findOfficeIdList(officeId , typeList);
		
		
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setOfficeList(officeList);
		int count1 =  clearScreenDao.getAtmCount(clearScreenMain);
		return count1;
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询服务自助设备客户(ATM)
	 * 
	 */
	public int getAtmCustCount(String id) {
		ClearScreenMain clearScreenMain = new ClearScreenMain();
		clearScreenMain.setId(id);
		int count1 =  clearScreenDao.getAtmCustCount(clearScreenMain);
		return count1;
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询省图编号
	 * 
	 */
	public String getProvinceCode(String name) {
		String strCode =  clearScreenDao.getProvinceCode(name);
		return strCode;
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          机构表查询省图编号
	 * 
	 */
	public String getOfficeProvinceCode(String name) {
		String strCode =  clearScreenDao.getOfficeProvinceCode(name);
		return strCode;
	}
	
	/**
	 * @author wl
	 * @version 2018-02-01
	 * 
	 *          查询地图编号
	 * 
	 */
	public String getMapCode(String mapName , String mapType) {
		String strMapCode = "";
		//全国点击的场合
		if (ScreenConstant.MapTypeCode.CHINAMAPCODE.equals(mapType)) {
			//省图表的查询
			strMapCode = clearScreenDao.getProvinceCode(mapName);
			if (StringUtils.isBlank(strMapCode)) {
				strMapCode = clearScreenDao.getOfficeProvinceCode(mapName);
			}
		}
		//全省点击的场合
		if (ScreenConstant.MapTypeCode.PROVINCEMAPCODE.equals(mapType)) {
			//市图表的查询
			strMapCode = clearScreenDao.getCityCode(mapName);
			if (StringUtils.isBlank(strMapCode)) {
				strMapCode = clearScreenDao.getOfficeCityCode(mapName);
			}
		}
		return strMapCode;
	}
	
	
	
	

	/**
	 * @author qph
	 * @version 2018-02-01
	 * 
	 *          获取清分服务金额
	 * 
	 */
	public ClOutMain getOutAmount(String id) {
		return clearScreenDao.getClearServiceAmount(id);
	}

	/**
	 * @author qph
	 * @version 2018-02-01
	 * 
	 *          获取现金服务金额
	 * 
	 */
	public AllAllocateInfo getCashAmount(String id) {
		return clearScreenDao.getCashAmount(id);
	}
	
	/**
	 * @author qph
	 * @version 2018-02-01
	 * 
	 *          获取上门收款金额
	 * 
	 */
	public CheckCashMain getDoorAmount(String id) {
		return clearScreenDao.getDoorAmount(id);
	}
	
	/**
	 * @author qph
	 * @version 2018-02-07
	 * 
	 *          获取上门收款金额
	 * 
	 */
	public AtmBindingInfo getAtmAmount(String id) {
		return clearScreenDao.getAtmAmount(id);
	}
	
	/**
	 * @author wl
	 * @version 2018-02-07
	 * 
	 *          机构ID的取得
	 * 
	 */
    public  List<String> findOfficeIdList(String  strOfficeId , List<String> typeList){
    	List<Office> listOffice1 = officeDao.findCustList(null, strOfficeId, typeList,Global.getConfig("jdbc.type"));
    	List<String> officeNoList1 = Lists.newArrayList();
    	for (Office resultOffice : listOffice1) {
    		officeNoList1.add(resultOffice.getId());
    	}
    	return officeNoList1;
    }
	/**
	 * @author qph
	 * @version 2018-02-08
	 * 
	 *          数字化金融服务平台业务统计列表
	 * 
	 */
	public List<Map<String, Object>> getAllAmountList(String strOfficeId) {
		// 获取机构列表
		List<String> officeNumList = this.getOfficeNumList(strOfficeId);
		// 返回结果List
		List<Map<String, Object>> list = Lists.newArrayList();
		for (String officeId : officeNumList) {
			// 返回商户List
			List<Map<String, Object>> businessList = Lists.newArrayList();
			Office office = SysCommonUtils.findOfficeById(officeId);
			// 返回结果集合
			Map<String, Object> resultMap = Maps.newHashMap();
			// 清分服务金额
			BigDecimal clearServiceAmount = new BigDecimal(0);
			// 发行库金额
			BigDecimal issuanceAmount = new BigDecimal(0);
			// 现金库
			BigDecimal cashAmount = new BigDecimal(0);
			// 上门收款
			BigDecimal doorAmount = new BigDecimal(0);
			// 自助设备金额
			BigDecimal atmAmount = new BigDecimal(0);
			// 总金额
			BigDecimal totalAmount = new BigDecimal(0);
			// 清分服务
			ClOutMain cloutMain = new ClOutMain();
			// 根据机构号获取清分服务金额
			cloutMain = this.getOutAmount(officeId);
			Map<String, Object> clearScreenMap = Maps.newHashMap();
			clearScreenMap.put("businessName", ScreenConstant.ServiceName.CLEARSERVICE);
			clearScreenMap.put("businessPct", new BigDecimal(0));
			if (cloutMain != null) {
				clearServiceAmount = cloutMain.getOutAmount();
				clearScreenMap.put("businessAmount", clearServiceAmount);
			} else {
				clearScreenMap.put("businessAmount", new BigDecimal(0));
			}
			totalAmount = totalAmount.add(clearServiceAmount);
			businessList.add(clearScreenMap);

			// 发行库
			Map<String, Object> issuanceMap = Maps.newHashMap();
			issuanceMap.put("businessName", ScreenConstant.ServiceName.ISSUANCESERVICE);
			issuanceMap.put("businessAmount", issuanceAmount);
			issuanceMap.put("businessPct", new BigDecimal(0));
			businessList.add(issuanceMap);
			// 现金库
			Map<String, Object> allocateInfoMap = Maps.newHashMap();
			AllAllocateInfo allocateInfo = this.getCashAmount(officeId);
			allocateInfoMap.put("businessName", ScreenConstant.ServiceName.CASHSERVICE);
			allocateInfoMap.put("businessPct", new BigDecimal(0));
			if (allocateInfo != null) {
				cashAmount = allocateInfo.getConfirmAmount();
				allocateInfoMap.put("businessAmount", cashAmount);

			} else {
				allocateInfoMap.put("businessAmount", new BigDecimal(0));
			}
			businessList.add(allocateInfoMap);
			totalAmount = totalAmount.add(cashAmount);

			// 上门收款
			Map<String, Object> checkCashMainMap = Maps.newHashMap();
			CheckCashMain checkCashMain = this.getDoorAmount(officeId);
			checkCashMainMap.put("businessName", ScreenConstant.ServiceName.DOORSERVICE);
			checkCashMainMap.put("businessPct", new BigDecimal(0));
			if (checkCashMain != null) {
				doorAmount = new BigDecimal(checkCashMain.getCheckAmount());
				checkCashMainMap.put("businessAmount", doorAmount);

			} else {
				checkCashMainMap.put("businessAmount", new BigDecimal(0));
			}
			businessList.add(checkCashMainMap);
			totalAmount = totalAmount.add(doorAmount);
			// 自助设备
			Map<String, Object> atmBindingInfoMap = Maps.newHashMap();
			AtmBindingInfo atmBindingInfo = this.getAtmAmount(officeId);
			atmBindingInfoMap.put("businessName", ScreenConstant.ServiceName.ATMSERVICE);
			atmBindingInfoMap.put("businessPct", new BigDecimal(0));
			if (atmBindingInfo != null) {
				atmAmount = atmBindingInfo.getAddAmount();
				atmBindingInfoMap.put("businessAmount", atmAmount);

			} else {
				atmBindingInfoMap.put("businessAmount", new BigDecimal(0));
			}
			businessList.add(atmBindingInfoMap);
			totalAmount = totalAmount.add(atmAmount);
			// 计算百分比
			this.getBusinessList(businessList, totalAmount);
			// 获取最大金额、百分比
			this.CompareAmount(resultMap, businessList);
			// 获取最大百分比
			resultMap.put("officeId", officeId);
			resultMap.put("officeName", office.getName());
			resultMap.put("businessCount", totalAmount);
			resultMap.put("businessList", businessList);
			list.add(resultMap);
		}
		return list;
	}
	/**
	 *
	 * @author qph
	 * @version 2017-02-06
	 *
	 * @Description 数字化金融服务平台业务统计
	 * @param paramMap
	 * @return
	 */

	private List<String> getOfficeNumList(String strOfficeId) {
		// 机构类型列表
		List<String> officeTypeList = Lists.newArrayList();
		officeTypeList.add(Constant.OfficeType.COFFER);
		// 查询所有下属机构
		List<Office> officeList = officeDao.findCustList(null, strOfficeId, officeTypeList,Global.getConfig("jdbc.type"));
		// 机构编号列表
		List<String> officeNumList = Lists.newArrayList();
		for (Office office : officeList) {
			officeNumList.add(office.getId());
		}
		return officeNumList;
	}

	/**
	 *
	 * @author qph
	 * @version 2017-02-06
	 *
	 * @Description 数字化金融服务平台业务统计
	 * @param paramMap
	 * @return
	 */

	private void CompareAmount(Map<String, Object> resultMap, List<Map<String, Object>> businessList) {
		BigDecimal count = new BigDecimal(0);
		int num = 0;
		for (int i = 0; i < businessList.size(); i++) {
			int result = count.compareTo((BigDecimal) businessList.get(i).get("businessAmount"));
			if (result == -1) {
				count = (BigDecimal) businessList.get(i).get("businessAmount");
				num = i;
			}
		}
		// 获取最大金额业务
		String businessName = (String) businessList.get(num).get("businessName");
		// 获取最大金额
		BigDecimal maxAmount = (BigDecimal) businessList.get(num).get("businessAmount");
		// 获取最大百分比
		String maxPct = (String) businessList.get(num).get("businessPct");
		resultMap.put("maxName", businessName);
		resultMap.put("maxAmount", maxAmount);
		resultMap.put("maxPct", maxPct);
	}

	/**
	 *
	 * @author qph
	 * @version 2017-02-06
	 *
	 * @Description 封装List
	 * @param clearServiceAmount
	 *            issuanceAmount cashAmount doorAmount totalAmount
	 * @return
	 */
	private void getBusinessList(List<Map<String, Object>> amountList, BigDecimal totalAmount) {
		DecimalFormat df = new DecimalFormat("######0.00");
		// 若总金额为0
		if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
			for (int i = 0; i < amountList.size(); i++) {
				amountList.get(i).put("businessPct", df.format(0));
			}
		}
		// 若总金额不为0
		if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
			for (int i = 0; i < amountList.size(); i++) {
				double douValue1 = (((BigDecimal) amountList.get(i).get("businessAmount")).doubleValue()
						/ totalAmount.doubleValue()) * 100;
				amountList.get(i).put("businessPct", df.format(douValue1));
			}
		}
	}
}
