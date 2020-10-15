package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeApp;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class EquipmentInfoAppService extends CrudService<EquipmentInfoDao, EquipmentInfo> {

	@Autowired
	private EquipmentInfoDao equipmentInfoDao;
	@Autowired
	private OfficeDao officeDao;
	
	/**
	 * serviceNo: 042 根据用户 查询商户列表 展示第一个商户的门店及机具信息 查询出机具正常(01)及异常(05)数量
	 * 
	 * @author zxk
	 * @version 2019-8-27
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getMerchartInfoList(User user, Page<OfficeApp> page) {
		Map<String, Object> jsonData = Maps.newHashMap();
		// 根据用户id 得到用户归属机构
		Office office = user.getOffice();
		// 获取机构类型
		String type = office.getType();
		EquipmentInfo eq = new EquipmentInfo();
		eq.setDelFlag(Constant.deleteFlag.Valid);
		// 根据归属机构查询机具列表,按不同状态统计机具总数
		int eCount = 0; // 机具异常数目
		int nCount = 0; // 机具正常数目
		if (type.equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 如果用户属于清分中心
			eq.setVinOffice(office);
		} else if (type.equals(Constant.OfficeType.STORE)) {
			// 如果用户属于门店
			eq.setaOffice(office);
		} else if (type.equals(Constant.OfficeType.MERCHANT)) {
			// 如果用户属于商户
			eq.setMerchantId(office.getId());
		} else {
			// 如果用户不属于清分中心
			eq.getSqlMap().put("dsf", "AND a.parent_ids LIKE '%" + office.getId() + "%' AND a.type='8'");
		}
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(eq);
		for (EquipmentInfo equip : list) {
			if (DoorOrderConstant.ConnStatus.UNUSUAL.equals(equip.getConnStatus())) {
				eCount += equip.getCount();
			} else if (DoorOrderConstant.ConnStatus.NORMAL.equals(equip.getConnStatus())) {
				nCount += equip.getCount();
			}
		}
		// 查询商户信息列表
		// 设置分页信息
		OfficeApp officeApp = new OfficeApp();
		officeApp.setPage(page);
		// 数据过滤(中心看上级人行下的所有商户，其他用户正常穿透)
		// 登录机构id
		if (type.equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 登录机构是清分中心
			officeApp.getSqlMap().put("dsf", "AND a.parent_ids LIKE '%" + office.getParentId() + "%' AND a.type='9'");
		} else if (type.equals(Constant.OfficeType.STORE)) {
			// 登录机构是门店
			officeApp.getSqlMap().put("dsf", "AND a.id = '" + office.getParentId() + "' AND a.type='9'");
		} else {
			// 登录机构是非清分中心(包括商户)
			officeApp.getSqlMap().put("dsf", "AND (a.parent_ids LIKE '%" + office.getId() + "%' OR A.ID = "
					+ office.getId() + ")" + " AND a.type='9'");
		}
		// 设置异常连接类型
		List<String> elist = Lists.newArrayList();
		// 05
		elist.add(DoorOrderConstant.ConnStatus.UNUSUAL);
		officeApp.setList(elist);
		List<OfficeApp> merList = officeDao.getMerListByOffice(officeApp);
		if (merList.size() > 0 && merList.get(0) != null) {
			// 取得第一个商户下的门店列表
			if (StringUtils.isNotBlank(merList.get(0).getId())) {
				Office of = new Office();
				if (type.equals(Constant.OfficeType.STORE)) {
					of.setId(office.getId());
				} else {
					of.setId(merList.get(0).getId());
				}
				Page<OfficeApp> doorPage = getDoorInfoList(of, new Page<OfficeApp>(1, 10));
				merList.get(0).setDoorList(doorPage);
			}
		}
		// 商户列表(含分页)
		page.setList(merList);
		jsonData.put(Parameter.MERCHANT_LIST, page);
		// 正常数量
		jsonData.put(Parameter.EQUIP_NORMAL_COUNT, nCount);
		// 异常数量
		jsonData.put(Parameter.EQUIP_UNUSUAL_COUNT, eCount);

		return jsonData;
	}

	/**
	 * serviceNo: 043 根据商户id查询门店列表,并根据门店查询机具信息
	 * 
	 * @author zxk
	 * @version 2019-8-27
	 * @return
	 */
	public Page<OfficeApp> getDoorInfoList(Office office, Page<OfficeApp> page) {
		OfficeApp officeApp = new OfficeApp();
		officeApp.setPage(page);
		officeApp.getSqlMap().put("dsf", "AND (a.parent_ids LIKE '%" + office.getId() + "%' OR A.ID = " + office.getId()
				+ ")" + " AND a.type='8'");
		officeApp.setType(DoorOrderConstant.ClearStatus.CLEAR);
		List<OfficeApp> doorList = officeDao.selectDoorAndEquipList(officeApp);
		if (Collections3.isEmpty(doorList)) {
			doorList = Lists.newArrayList();
		}else{
			for (OfficeApp door : doorList) {
				//门店下机具数量
				door.setEquipmentCount(door.getEquipmentInfoList().size());
			}
		}
		page.setList(doorList);
		return page;
	}

	/**
	 * serviceNo: 根据机具连接状态查询机具总列表
	 * 
	 * @author zxk
	 * @version 2019-8-27
	 * @return Page<OfficeApp>
	 */
	public Map<String, Object> getEquipmentListByConnStatus(User user, String connStatus,String id, Page<OfficeApp> page) {
		Map<String, Object> jsonData = Maps.newHashMap();
		// 根据用户id 得到用户归属机构
		Office office = user.getOffice();
		// 查询商户信息列表
		OfficeApp officeApp = new OfficeApp();
		// 数据过滤条件
		if (office.getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 清分中心
			officeApp.getSqlMap().put("dsf", "AND a.parent_ids LIKE '%" + office.getParentId() + "%' AND a.type='8'");
		} else if (office.getType().equals(Constant.OfficeType.STORE)) {
			// 门店
			officeApp.getSqlMap().put("dsf", "AND a.id = " + office.getId() + " AND a.type='8'");
		} else {
			// 其他(非清分中心,商户)
			officeApp.getSqlMap().put("dsf", "AND a.parent_ids LIKE '%" + office.getId() + "%' AND a.type='8'");
		}
		officeApp.setPage(page);
		officeApp.setConnStatus(connStatus);
		officeApp.setId(id);
		officeApp.setType(DoorOrderConstant.ClearStatus.CLEAR);
		List<OfficeApp> doorAndEquipList = officeDao.selectDoorAndEquipList(officeApp);
		if (Collections3.isEmpty(doorAndEquipList)) {
			doorAndEquipList = Lists.newArrayList();
		}
		for (OfficeApp doorInfoList : doorAndEquipList) {
			if (Collections3.isEmpty(doorInfoList.getEquipmentInfoList())) {
				doorInfoList.setEquipmentInfoList(new ArrayList<EquipmentInfo>());
			}
			doorInfoList.setEquipmentCount(doorInfoList.getEquipmentInfoList().size());
		}
		page.setList(doorAndEquipList);
		jsonData.put(Parameter.OFFICE_EQUIP_LIST, page);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return jsonData;
	}
	
	/**
	 * <p>
	 * Description: 获取机具列表
	 * </p>
	 * 
	 * @param page,equipmentInfo
	 * @return Page<EquipmentInfo> 返回类型
	 */
    public List<EquipmentInfo> findEqpList(String userId, Page<EquipmentInfo> page, String seriesNumber) {
    	EquipmentInfo equipmentInfo = new EquipmentInfo();
    	equipmentInfo.setSeriesNumber(seriesNumber);
    	equipmentInfo.setPage(page);
    	User user = UserUtils.get(userId);
    	Office office = user.getOffice();
        if (StringUtils.isNotBlank(office.getType())) {
            switch (office.getType()) {
                case Constant.OfficeType.CLEAR_CENTER:
                    equipmentInfo.setVinOffice(office);
                    break;
                case Constant.OfficeType.MERCHANT:
                    equipmentInfo.setpOfficeId(office.getId());
                    break;
                case Constant.OfficeType.STORE:
                    equipmentInfo.setaOffice(office);
                    break;
                default:
                    equipmentInfo.getSqlMap().put("dsf", dataScopeFilter(user, "b", null));
            }
        }
        return super.findList(equipmentInfo);
    }

}
